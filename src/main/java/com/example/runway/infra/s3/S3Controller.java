package com.example.runway.infra.s3;

import com.example.runway.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class S3Controller {
    private final S3Service s3Service;

    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> upload(
            @Parameter(
                    name = "file",
                    description = "업로드할 이미지 파일",
                    required = true,
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(type = "string", format = "binary"))
            )
            @RequestPart MultipartFile file
    ) {
        String url = s3Service.uploadImage(file, "s3");
        return ResponseEntity.ok(ApiResponse.onSuccess(200, url));
    }

    @DeleteMapping("")
    public ResponseEntity<ApiResponse<String>> delete(@RequestParam String url) {
        s3Service.removeImage(url);
        return ResponseEntity.ok(ApiResponse.onSuccess(200, "해당 이미지가 삭제되었습니다."));
    }
}
