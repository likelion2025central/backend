package com.example.centralhackathon.config.matching;

import io.micrometer.common.lang.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QdrantClient {
    @Value("${app.qdrant.base-url}") private String baseUrl;
    private final WebClient web = WebClient.builder().build();

    public void createCollectionIfMissing(String collection, int dim) {
        Map<String, Object> createBody = Map.of(
                "vectors", Map.of("size", dim, "distance", "Cosine")
        );

        // 먼저 GET 요청으로 존재 여부 확인
        boolean exists = Boolean.TRUE.equals(
                web.get().uri(baseUrl+"/collections/" + collection)
                        .retrieve()
                        .toBodilessEntity()
                        .map(r -> true)
                        .onErrorReturn(WebClientResponseException.NotFound.class, false)
                        .block()
        );

        if (!exists) {
            web.put().uri(baseUrl+"/collections/" + collection)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(createBody)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        }
    }

    /** upsert */
    public void upsert(String collection, long id, float[] vec, Map<String,Object> payload) {
        Map<String,Object> point = new HashMap<>();
        point.put("id", id);
        point.put("vector", vec);
        Map<String,Object> pl = new HashMap<>(payload);
        pl.put("id", id);
        point.put("payload", pl);

        Map<String,Object> req = Map.of("points", List.of(point));
        web.put().uri(baseUrl + "/collections/" + collection + "/points")
           .contentType(MediaType.APPLICATION_JSON)
           .bodyValue(req)
           .retrieve().toBodilessEntity().block();
    }

    /** search */
    public List<Map<String,Object>> search(String collection, float[] queryVec, int top, @Nullable Map<String,Object> filter) {
        Map<String,Object> req = new HashMap<>();
        req.put("vector", queryVec);
        req.put("top", top);
        req.put("with_payload", true);
        if (filter != null) req.put("filter", filter);

        Map<String,Object> resp = web.post().uri(baseUrl + "/collections/" + collection + "/points/search")
           .contentType(MediaType.APPLICATION_JSON)
           .bodyValue(req)
           .retrieve()
           .bodyToMono(new ParameterizedTypeReference<Map<String,Object>>(){}).block();

        List<Map<String,Object>> results = (List<Map<String,Object>>) resp.get("result");

        // 변환: score → similarity (0~1)
        for (Map<String,Object> r : results) {
            double rawScore = ((Number) r.get("score")).doubleValue();
            double similarity = Math.max(0, Math.min(1, (rawScore + 1) / 2));
            r.put("similarity", similarity);
        }
        return results;
    }
}
