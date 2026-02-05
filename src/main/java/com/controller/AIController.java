package com.controller;

import com.dto.request.AIRequest;
import com.dto.request.ApiResponse;
import com.dto.response.AIResponse;
import com.service.AIService;
import com.service.GithubService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.text.ParseException;

@Slf4j
@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class AIController {
    AIService aiService;
    GithubService githubService;
    @PostMapping("/analyze-tech")
    public Mono<AIResponse> analyze(@RequestBody AIRequest request) {
        System.out.println(request.toString());
        String linkRepo = request.getMessage();
        if (linkRepo != null) {
            linkRepo = linkRepo.replaceAll("\\.git$", "");
        }
        return githubService.analyzeRepo(linkRepo)
                .flatMap(teachList -> {
                    String prompt = "Chỉ trích xuất tên tối đa 7 tên công nghệ quan trọng nhất từ danh sách sau. Trả về kết quả DUY NHẤT dưới dạng chuỗi string nối tiếp nhau, mỗi tên cách nhau 1 dấu phẩy. Trả về tên không có @ hay gạch chéo. KHÔNG bao gồm tiêu đề, KHÔNG giải thích, KHÔNG dùng Markdown code block (```html), KHÔNG trả về các thẻ cấu trúc trang web (body, head)." + teachList;
                    try {
                        AIResponse result = aiService.analyzeTech(
                                AIRequest.builder()
                                        .message(prompt)
                                        .build()
                        );
                        return Mono.just(result);
                    } catch (ParseException e) {
                        return Mono.error(new RuntimeException("Lỗi khi gọi AI Service"));
                    }
                })
                // Nếu có lỗi trong quá trình lấy từ GitHub, trả về thông báo lỗi
                .onErrorReturn(AIResponse.<String>builder()
                        .responseMessage("Lấy danh sách công nghệ không thành công")
                        .data(null)
                        .build());
    }

    @PostMapping("/generate-content")
    public ApiResponse<AIResponse> getContent(@RequestBody AIRequest request) {
        System.out.println(request.toString());
        String promp = "từ đoạn này hãy tạo thêm sau đó khoảng 10 đến 20 từ sao cho hợp lý nhất có thể, chú ý chỉ được chèn thêm, không chỉnh sửa nội dung gốc, chỉ tra về nội dung không cần câu dẫn, trả về 1 phương án duy nhất: " + request.getMessage();
        return ApiResponse.<AIResponse>builder()
                .code(200)
                .message("get generate content successfully")
                .result(aiService.generateContent(promp))
                .build();
    }
}
