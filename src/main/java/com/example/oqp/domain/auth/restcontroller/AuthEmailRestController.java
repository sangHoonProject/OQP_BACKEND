package com.example.oqp.domain.auth.restcontroller;

import com.example.oqp.common.error.ErrorResponse;
import com.example.oqp.db.entity.MailCode;
import com.example.oqp.db.entity.PasswordAuthCode;
import com.example.oqp.domain.auth.restcontroller.request.EmailSendRequest;
import com.example.oqp.domain.auth.restcontroller.request.EmailVerifyRequest;
import com.example.oqp.domain.auth.restcontroller.response.EmailVerifyResponse;
import com.example.oqp.domain.auth.service.AuthEmailRestService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
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
@Tag(name = "메일 발송 API Controller")
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

    @SneakyThrows
    @Operation(summary = "비밀번호 재설정 메일 발송 API", description = "비밀번호 분실 또는 재설정 필요 시 호출하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "발송 성공", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = PasswordAuthCode.class))
            }),
            @ApiResponse(responseCode = "404", description = "비밀번호 재설정할 사용자를 찾지 못했을 경우 반환", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            }),
            @ApiResponse(responseCode = "500", description = "이메일 발송 실패시 반환", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            })
    })
    @PostMapping("/password-code")
    public ResponseEntity<PasswordAuthCode> sendAuthCode(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "비밀번호 재설정 인증 코드 발급 요청 객체"
            )
            @RequestBody EmailSendRequest request
    ){
        return ResponseEntity.ok(authEmailRestService.sendAuthCode(request));
    }
}
