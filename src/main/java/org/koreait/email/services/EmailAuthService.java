package org.koreait.email.services;

import lombok.RequiredArgsConstructor;
import org.koreait.email.controllers.RequestEmail;
import org.koreait.email.exceptions.AuthCodeExpiredException;
import org.koreait.email.exceptions.AuthCodeMismatchException;
import org.koreait.global.exceptions.BadRequestException;
import org.koreait.global.libs.Utils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailAuthService {
    private final Utils utils;
    private final EmailService emailService;

    /**
     *
     * @param to : 수신쪽 이메일 주소
     * @return
     */
    public boolean sendCode(String to) {
        Random random = new Random();
        String subject = utils.getMessage("Email.authCode.subject");

        /**
         * 인증 코드는 5자리 정수
         * 만료시간을 3분으로 기록
         * 사용자의 입력을 검증하기 위해서 세션에 인증 코드와 만료시간을 기록
         */
        Integer authCode = random.nextInt(10000, 99999);

        LocalDateTime expired = LocalDateTime.now().plusMinutes(3L);

        // 인증 코드 및 만료시간 저장 필요
        utils.saveValue("authCode", authCode);
        utils.saveValue("expiredTime", expired);

        Map<String, Object> tplData = new HashMap<>();
        tplData.put("authCode", authCode);

        RequestEmail form = new RequestEmail();
        form.setTo(List.of(to));
        form.setSubject(subject);

        return emailService.sendEmail(form, "auth", tplData);
    }

    /**
     * 인증코드 검증
     *
     * @param code : 사용자가 입력한 인증 코드
     */
    public void verify(Integer code) {
        if (code == null) {
            throw new BadRequestException(utils.getMessage("NotBlank.authCode"));
        }

        LocalDateTime expired = utils.getValue("expiredTime");
        Integer authCode = utils.getValue("authCode");

        if (expired != null && expired.isBefore(LocalDateTime.now())) { // 코드가 만료된 경우
            throw new AuthCodeExpiredException();
        }

        if (authCode == null) {
            throw new BadRequestException();
        }

        if (!code.equals(authCode)) {
            throw new AuthCodeMismatchException();
        }

        // 인증 성공 상태 세션에 기록
        utils.saveValue("authCodeVerified", true);
    }
}
