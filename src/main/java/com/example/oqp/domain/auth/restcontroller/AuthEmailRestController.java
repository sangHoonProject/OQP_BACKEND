package com.example.oqp.domain.auth.restcontroller;

import com.example.oqp.common.error.ErrorResponse;
import com.example.oqp.db.entity.MailCode;
import com.example.oqp.domain.auth.restcontroller.request.EmailSendRequest;
import com.example.oqp.domain.auth.restcontroller.request.EmailVerifyRequest;
import com.example.oqp.domain.auth.restcontroller.response.EmailVerifyResponse;
import com.example.oqp.domain.auth.service.AuthEmailRestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/mail")
@RequiredArgsConstructor
public class AuthEmailRestController {

    private final AuthEmailRestService authEmailRestService;

    @Operation(summary = "이메일 인증 번호 발송 API", description = "이메일로 인증 번호를 발송하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = MailCode.class))
            }),
            @ApiResponse(responseCode = "404", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class,
                            description = "이메일로 가입된 사용자를 찾지 못했을때 응답"))
            }),
            @ApiResponse(responseCode = "500", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class,
                    description = "이메일 발송이 실패했을때 응답"))
            })

    })
    @PostMapping("/send")
    @SneakyThrows
    public ResponseEntity<MailCode> send(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "이메일 발송 요청 객체"
            )
            @RequestBody EmailSendRequest request
    ) {
        return ResponseEntity.ok(authEmailRestService.send(request));
    }

    @Operation(summary = "이메일 발송 인증 번호 검증 API", description = "이메일로 전송된 인증번호 검증 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = EmailVerifyResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "이미 사용된 인증 번호이거나 인증 번호를 찾을 수 없을때 응답",
            content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            })
    })
    @PostMapping("/verify")
    public ResponseEntity<EmailVerifyResponse> verify(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "이메일 인증 번호 검증 요청 객체"
            )
            @RequestBody EmailVerifyRequest request
    ) {
        boolean verify = authEmailRestService.verify(request);
        EmailVerifyResponse response = new EmailVerifyResponse();

        if (verify) {
            response = EmailVerifyResponse.builder()
                    .isVerified(true)
                    .message("인증이 완료되었습니다.")
                    .build();
        }else{
            response = EmailVerifyResponse.builder()
                    .isVerified(false)
                    .message("인증이 실패되었습니다.")
                    .build();
        }

        return ResponseEntity.ok(response);
    }
}
