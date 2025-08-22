package com.example.centralhackathon.config.matching;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmbeddingClient {
    private final WebClient webClient = WebClient.builder().build();
    @Value("${app.ai.embed-url}") private String embedUrl;
    @Value("${app.ai.rerank-url}") private String rerankUrl;

    public float[] embedOne(String text) {
        var resp = webClient.post().uri(embedUrl)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(Map.of("texts", List.of(text)))
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<Map<String, List<List<Double>>>>(){})
            .block();
        var v = resp.get("vectors").get(0);
        float[] f = new float[v.size()];
        for (int i=0;i<v.size();i++) f[i]=v.get(i).floatValue();
        return f;
    }

    public List<Double> rerank(String query, List<String> candidates) {
        var resp = webClient.post().uri(rerankUrl)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(Map.of("query", query, "candidates", candidates, "normalize", true))
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<Map<String, List<Double>>>(){})
            .block();
        return resp.get("scores");
    }
}
