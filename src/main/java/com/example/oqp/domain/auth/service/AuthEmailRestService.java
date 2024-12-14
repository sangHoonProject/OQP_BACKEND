package com.example.oqp.domain.auth.service;

import com.example.oqp.common.enums.UseYn;
import com.example.oqp.common.error.CustomException;
import com.example.oqp.common.error.ErrorCode;
import com.example.oqp.db.entity.MailCode;
import com.example.oqp.db.repository.MailCodeRepository;
import com.example.oqp.db.repository.UserInfoRepository;
import com.example.oqp.domain.auth.restcontroller.request.EmailSendRequest;
import com.example.oqp.domain.auth.restcontroller.request.EmailVerifyRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthEmailRestService {

    private final UserInfoRepository userInfoRepository;
    private final MailCodeRepository mailCodeRepository;
    private final JavaMailSender javaMailSender;

    public MailCode send(EmailSendRequest request) throws MessagingException {

        String email = request.getEmail();

        verifyEmail(email);

        String secureCode = createSecureCode();

        MimeMessage mimeMessage = setMailMessage(email, secureCode);

        try{

            javaMailSender.send(mimeMessage);

            List<MailCode> mailCodeList = mailCodeRepository.findByEmailWhereUseYn(email, UseYn.N);
            if(!mailCodeList.isEmpty()){

                mailCodeList.forEach(mailCode -> {
                    mailCode.setUseYn(UseYn.Y);
                });

                mailCodeRepository.saveAll(mailCodeList);
            }

            MailCode mailCode = MailCode.builder()
                    .email(email)
                    .code(secureCode)
                    .useYn(UseYn.N)
                    .build();

            mailCodeRepository.save(mailCode);

            return mailCode;

        } catch (MailException e) {

            throw new CustomException(ErrorCode.EMAIL_SEND_FAILED);

        }

    }

    private void verifyEmail(String email) {

        if(!userInfoRepository.existsByEmail(email)) {

            throw new CustomException(ErrorCode.USER_NOT_FOUND);

        }
    }

    private String createSecureCode() {

        SecureRandom random = new SecureRandom();

        char[] characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();

        StringBuilder key = new StringBuilder();

        for(int i = 0; i < 6; i++){

            int index = random.nextInt(characters.length);

            key.append(characters[index]);

        }

        return key.toString();
    }

    private MimeMessage setMailMessage(String email, String secureCode) throws MessagingException {

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setTo(email);

        helper.setSubject("Online Quiz Project 이메일 인증");

        String content = getMailContent(secureCode);

        helper.setText(content, true);

        return mimeMessage;
    }

    private String getMailContent(String secureCode) {

        return "<html>" +
                "<body style='font-family: Arial, sans-serif; background-color: #f4f7fa; margin: 0; padding: 0;'>" +
                "<div style='width: 100%; padding: 40px 20px; text-align: center; background-color: #ffffff; border-radius: 15px; box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1); max-width: 600px; margin: 50px auto;'>" +

                "<div style='font-size: 26px; font-weight: bold; color: #333333; margin-bottom: 30px;'>" +
                "<span style='color: #4CAF50;'>OQP 이메일 인증</span>" +
                "</div>" +

                "<p style='font-size: 18px; color: #555555; margin-bottom: 20px;'>인증 번호를 입력하여 이메일을 인증해 주세요.</p>" +

                "<div style='font-size: 32px; font-weight: bold; color: #ffffff; background-color: #4CAF50; padding: 15px 30px; border-radius: 8px; margin: 20px 0; display: inline-block;'>" + secureCode + "</div>" +

                "<p style='font-size: 16px; color: #555555;'>만약 본인이 요청하지 않은 이메일이라면, 즉시 계정 보안을 위해 비밀번호를 변경해 주세요.</p>" +
                "<p style='font-size: 16px; color: #555555; margin-bottom: 30px;'>보안을 위해 인증 번호는 타인과 공유하지 마세요.</p>" +

                "<div style='font-size: 14px; color: #888888; margin-top: 40px;'>" +
                "<p>Online Quiz Project</p>" +
                "</div>" +

                "</div>" +
                "</body>" +
                "</html>";
    }

    public boolean verify(EmailVerifyRequest request) {

        String email = request.getEmail();

        String code = request.getAuthCode();

        MailCode mailCode = mailCodeRepository.findByEmailAndCode(email, code)
                .orElseThrow(() -> new CustomException(ErrorCode.AUTH_CODE_OR_EMAIL_NOT_FOUND));

        validateMailCodeUseYn(mailCode);

        mailCode.setUseYn(UseYn.Y);

        mailCodeRepository.save(mailCode);

        return true;
    }

    private void validateMailCodeUseYn(MailCode mailCode) {
        if(mailCode.getUseYn() == UseYn.Y){
            throw new CustomException(ErrorCode.AUTH_CODE_USED);
        }
    }
}