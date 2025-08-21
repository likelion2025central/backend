package com.example.centralhackathon.service;

import com.example.centralhackathon.config.EmailCertificationUtil;
import com.example.centralhackathon.entity.*;
import com.example.centralhackathon.repository.AssociationRepository;
import com.example.centralhackathon.repository.BossAssociationRepository;
import com.example.centralhackathon.repository.CouncilAssociationRepository;
import com.example.centralhackathon.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AssociationService {
    private final AssociationRepository associationRepository;
    private final CouncilAssociationRepository councilAssociationRepository;
    private final BossAssociationRepository bossAssociationRepository;
    private final EmailCertificationUtil emailUtil;
    private final UserRepository userRepository;
    private final MailSendService mailSendService;

    @Transactional
    public int makeAssociation(List<Long> ids, Role requesterType, Long requesterId) throws MessagingException {
        if (requesterType == Role.STUDENT) {
            throw new IllegalArgumentException("요청자 타입으로 STUDENT는 허용되지 않습니다.");
        }
        int created = 0;
        if (requesterType == Role.COUNCIL) {
            CouncilAssociation council = councilAssociationRepository.findById(requesterId)
                    .orElseThrow(() -> new IllegalArgumentException("학생회 제휴정보가 존재하지 않습니다. id=" + requesterId));

            Users base = council.getUser();

            // 1) 역할로 1차 검증 (프록시여도 OK)
            if (base.getRole() != Role.COUNCIL) {
                throw new IllegalStateException("연결된 유저가 사장이 아닙니다. id=" + base.getId());
            }
            // 2) 전용 필드 필요하면 언프로시 후 캐스팅
            StudentCouncil sc = unproxy(base, StudentCouncil.class);
            String councilInfo = "[%s %s %s]".formatted(
                    sc.getSchoolName(), sc.getCollege(), sc.getDepartment()
            );
            String councilEmail = sc.getEmail();
            for (Long bossId : ids) {
                BossAssociation boss = bossAssociationRepository.findById(bossId)
                        .orElseThrow(() -> new IllegalArgumentException("사장님 제휴정보가 존재하지 않습니다. id=" + bossId));

                Association assoc = new Association();
                assoc.setCouncil(council);
                assoc.setBoss(boss);
                assoc.setRequester(Role.COUNCIL);
                assoc.setResponder(Role.BOSS);
                assoc.setStatus(AssociationCondition.WAITING);

                associationRepository.save(assoc);
                created++;

                Users bossBase = boss.getUser();
                // 1) 역할로 1차 검증 (프록시여도 OK)
                if (bossBase.getRole() != Role.BOSS) {
                    throw new IllegalStateException("연결된 유저가 사장이 아닙니다. id=" + bossBase.getId());
                }
                // 2) 보스 전용 필드 필요하면 언프로시 후 캐스팅
                Boss bo = unproxy(bossBase, Boss.class); // Boss로 안전 캐스팅
                String bossEmail = bo.getEmail();
                String title ="[제휴고리 알림] 제휴 요청이 도착했습니다.";
                String content=
                        "[제휴고리 알림] 제휴 요청이 도착했습니다.\n" +
                                councilInfo +
                                "요청자 이메일 | " + councilEmail + "\n" +
                                "간단 협약서 바로가기 | https://jehugori.com.";

                emailUtil.sendAssocEmail(bossEmail, title, content);
            }

        } else if (requesterType == Role.BOSS) {
            BossAssociation boss = bossAssociationRepository.findById(requesterId)
                    .orElseThrow(() -> new IllegalArgumentException("사장님 제휴정보가 존재하지 않습니다. id=" + requesterId));


            Users base = boss.getUser();
            // 1) 역할로 1차 검증 (프록시여도 OK)
            if (base.getRole() != Role.BOSS) {
                throw new IllegalStateException("연결된 유저가 사장이 아닙니다. id=" + base.getId());
            }

            // 2) 보스 전용 필드 필요하면 언프로시 후 캐스팅
            Boss bo = unproxy(base, Boss.class); // Boss로 안전 캐스팅
            String BossInfo = "[%s]".formatted(bo.getStoreName());
            String bossEmail = bo.getEmail();

            for (Long councilId : ids) {
                CouncilAssociation council = councilAssociationRepository.findById(councilId)
                        .orElseThrow(() -> new IllegalArgumentException("학생회 제휴정보가 존재하지 않습니다. id=" + councilId));

                Association assoc = new Association();
                assoc.setCouncil(council);
                assoc.setBoss(boss);
                assoc.setRequester(Role.BOSS);
                assoc.setResponder(Role.COUNCIL);
                assoc.setStatus(AssociationCondition.WAITING);
                associationRepository.save(assoc);
                created++;

                Users councilBase = council.getUser();

                if (councilBase.getRole() != Role.COUNCIL) {
                    throw new IllegalStateException("연결된 유저가 학생회가 아닙니다. id=" + councilBase.getId());
                }
                StudentCouncil sc = unproxy(councilBase, StudentCouncil.class);
                String councilEmail = sc.getEmail();
                String title ="[제휴고리 알림] 제휴 요청이 도착했습니다.";
                String content=
                        "[제휴고리 알림] 제휴 요청이 도착했습니다.\n" +
                                BossInfo +
                                "요청자 이메일 | " + bossEmail + "\n" +
                                "간단 협약서 바로가기 | https://jehugori.com.";

                emailUtil.sendAssocEmail(councilEmail, title, content);
            }

        } else {
            throw new IllegalArgumentException("요청자 타입은 COUNCIL 또는 BOSS 여야 합니다.");
        }

        return created;
    }

    @Transactional
    public void updateStatus(Long associationId, AssociationCondition newStatus) {
        Association assoc = associationRepository.findById(associationId)
                .orElseThrow(() -> new IllegalArgumentException("Association not found. id=" + associationId));
        assoc.setStatus(newStatus);
    }


    @SuppressWarnings("unchecked")
    private <T> T unproxy(Object entity, Class<T> targetType) {
        Object impl = (entity instanceof HibernateProxy)
                ? ((HibernateProxy) entity).getHibernateLazyInitializer().getImplementation()
                : entity;
        return (T) impl; // 실제 구현체로 반환
    }

}
