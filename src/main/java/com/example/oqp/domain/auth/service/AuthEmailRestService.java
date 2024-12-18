package com.example.oqp.domain.auth.service;

import com.example.oqp.common.enums.UseYn;
import com.example.oqp.common.error.CustomException;
import com.example.oqp.common.error.ErrorCode;
import com.example.oqp.db.entity.MailCode;
import com.example.oqp.db.entity.UserInfo;
import com.example.oqp.db.repository.MailCodeRepository;
import com.example.oqp.db.repository.UserInfoRepository;
import com.example.oqp.domain.auth.restcontroller.request.EmailSendRequest;
import com.example.oqp.domain.auth.restcontroller.request.PasswordResetRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthEmailRestService {

    private final UserInfoRepository userInfoRepository;
    private final MailCodeRepository mailCodeRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;

    public MailCode send(EmailSendRequest request) throws MessagingException {

        String email = request.getEmail();

        String secureCode = createSecureCode();
        String subject = "Online Quiz Project 이메일 인증";

        MimeMessage mimeMessage = setMailMessage(email, secureCode, subject);

        try{

            javaMailSender.send(mimeMessage);

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

    private MimeMessage setMailMessage(String email, String secureCode, String subject) throws MessagingException {

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setTo(email);

        helper.setSubject(subject);

        String content = "<html>" +
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

        helper.setText(content, true);

        return mimeMessage;
    }

    public UserInfo passwordReset(PasswordResetRequest passwordResetRequest) throws MessagingException {
        String email = passwordResetRequest.getEmail();

        UserInfo userInfo = userInfoRepository.findByEmail(email);

        if(userInfo == null){
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        String newPassword = createSecureCode();

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setTo(email);

        helper.setSubject("OQP 임시 비밀번호 발급 이메일");

        String content = "<html>" +
                "<body style='font-family: Arial, sans-serif; background-color: #f4f7fa; margin: 0; padding: 0;'>" +
                "<div style='width: 100%; padding: 40px 20px; text-align: center; background-color: #ffffff; border-radius: 15px; box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1); max-width: 600px; margin: 50px auto;'>" +

                "<div style='font-size: 26px; font-weight: bold; color: #333333; margin-bottom: 30px;'>" +
                "<span style='color: #4CAF50;'>임시 비밀번호 발급</span>" +
                "</div>" +

                "<p style='font-size: 18px; color: #555555; margin-bottom: 20px;'>아래의 임시 비밀번호를 사용하여 로그인하세요.</p>" +

                "<div style='font-size: 32px; font-weight: bold; color: #ffffff; background-color: #4CAF50; padding: 15px 30px; border-radius: 8px; margin: 20px 0; display: inline-block;'>" +
                newPassword + "</div>" +

                "<p style='font-size: 16px; color: #555555;'>로그인 후 반드시 비밀번호를 변경해 주세요.</p>" +
                "<p style='font-size: 16px; color: #555555; margin-bottom: 30px;'>보안을 위해 임시 비밀번호는 타인과 공유하지 마세요.</p>" +

                "<div style='font-size: 14px; color: #888888; margin-top: 40px;'>" +
                "<p>Online Quiz Project</p>" +
                "</div>" +

                "</div>" +
                "</body>" +
                "</html>";

        helper.setText(content, true);

        try{
            javaMailSender.send(mimeMessage);

            userInfo.setPassword(passwordEncoder.encode(newPassword));

            return userInfoRepository.save(userInfo);
        }catch (Exception e){
            throw new CustomException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }
}
