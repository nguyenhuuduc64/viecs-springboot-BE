package com.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
@RequiredArgsConstructor
public class GithubService {
    private final WebClient githubWebClient;

    public Mono<List<String>> analyzeRepo(String githubUrl) {
        if (githubUrl == null || githubUrl.isEmpty()) {
            return Mono.error(new IllegalArgumentException("URL GitHub không được để trống"));
        }
        // Tách owner và repo từ URL: https://github.com/owner/repo
        String[] parts = githubUrl.replace("https://github.com/", "").split("/");
        String owner = parts[0];
        String repo = parts[1];

        return getFileRawContent(owner, repo, "package.json")
                .map(this::parsePackageJson)
                .onErrorResume(e -> Mono.just(Collections.singletonList("Không tìm thấy package.json")));
    }

    private List<String> parsePackageJson(String json) {
        try {

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);
            List<String> techs = new ArrayList<>();

            // Kiểm tra cả 2 mục phổ biến
            if (root.has("dependencies")) {
                root.get("dependencies").fieldNames().forEachRemaining(techs::add);
            }
            if (root.has("devDependencies")) {
                root.get("devDependencies").fieldNames().forEachRemaining(techs::add);
            }

            System.out.println("Số lượng công nghệ tìm thấy: " + techs.size());
            return techs;
        } catch (Exception e) {
            System.err.println("Lỗi Parse JSON: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    private Mono<String> getFileRawContent(String owner, String repo, String path) {
        return githubWebClient.get()
                .uri("/repos/{owner}/{repo}/contents/{path}", owner, repo, path)
                .retrieve()
                .bodyToMono(Map.class)
                .map(res -> new String(Base64.getMimeDecoder().decode(((String) res.get("content")).trim())));
    }
}
