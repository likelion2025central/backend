package com.example.centralhackathon.config.matching;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BootInit implements CommandLineRunner {
    private final QdrantClient qd;
    @Value("${app.qdrant.bosses}") private String bosses;
    @Value("${app.qdrant.councils}") private String councils;

    @Override public void run(String... args) {
        qd.createCollectionIfMissing(bosses, 1024);
        qd.createCollectionIfMissing(councils, 1024);
    }
}
