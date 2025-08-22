package com.example.centralhackathon.config.matching;

import io.micrometer.common.lang.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QdrantClient {
    @Value("${app.qdrant.base-url}") private String baseUrl;
    private final WebClient web = WebClient.builder().build();

    /** 컬렉션 생성(idempotent) */
    public void createCollectionIfMissing(String collection, int dim) {
        Map<String,Object> body = Map.of("vectors", Map.of("size", dim, "distance", "Cosine"));
        web.put().uri(baseUrl + "/collections/" + collection)
           .contentType(MediaType.APPLICATION_JSON)
           .bodyValue(body)
           .retrieve().toBodilessEntity().block();
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
        if (filter != null) req.put("filter", filter);

        Map<String,Object> resp = web.post().uri(baseUrl + "/collections/" + collection + "/points/search")
           .contentType(MediaType.APPLICATION_JSON)
           .bodyValue(req)
           .retrieve()
           .bodyToMono(new ParameterizedTypeReference<Map<String,Object>>(){}).block();

        return (List<Map<String,Object>>) resp.get("result");
    }
}
