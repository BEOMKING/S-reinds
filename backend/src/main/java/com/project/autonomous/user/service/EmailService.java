package com.project.autonomous.user.service;

import com.project.autonomous.user.dto.request.AuthCode;
import com.project.autonomous.user.entity.Email;
import com.project.autonomous.user.repository.EmailRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@AllArgsConstructor
public class EmailService {

    @Autowired
    EmailRepository emailRepository;


    private JavaMailSender javaMailSender;
    private static final String FROM_ADDRESS = "ssafyD106@gmail.com";

    public void sendMail(String mailAddress) {
        SimpleMailMessage message = new SimpleMailMessage();
        int code = makeCode();
        String text = "인증코드 : [" + String.valueOf(code)+"]\n5분 이내에 인증을 완료해주세요." ;

        message.setTo(mailAddress);
        message.setFrom(EmailService.FROM_ADDRESS);
        message.setSubject("[Sriends] 인증코드 발송");
        message.setText(text);
        javaMailSender.send(message);

        if(emailRepository.findByEmail(mailAddress).isPresent()){
            System.out.println("있어");
            Email email = emailRepository.findByEmail(mailAddress).get();
            email.setCreateDate(LocalDateTime.now());
            email.setLastModifiedDate(LocalDateTime.now());
            email.setExpirationDate(LocalDateTime.now().plusMinutes(5));
            email.setExpired(false);
            email.setCode(code);
            emailRepository.save(email);
        }else{
            System.out.println("없어");
            Email email = new Email();
            email.setEmail(mailAddress);
            email.setCreateDate(LocalDateTime.now());
            email.setLastModifiedDate(LocalDateTime.now());
            email.setExpirationDate(LocalDateTime.now().plusMinutes(5));
            email.setExpired(false);
            email.setCode(code);
            emailRepository.save(email);
        }
    }

    public int makeCode(){
        int certNumLength = 6;

        Random random = new Random(System.currentTimeMillis());
        int range = (int)Math.pow(10,certNumLength);
        int trim = (int)Math.pow(10, certNumLength-1);
        int result = random.nextInt(range)+trim;

        if(result>range){
            result = result - trim;
        }

        return result;

    }

    public boolean checkCode(AuthCode authCode, String emailAddress) {

        if(emailRepository.findByEmail(emailAddress).isPresent()){//아이디가 있는지 확인
            Email email = emailRepository.findByEmail(emailAddress).get();
            System.out.println(email.isExpired());

            if(authCode.getCode() == email.getCode()){//입력한거랑 아닌거랑 다르면 패스
                if(!email.isExpired()){//인증코드가 만료됐는지 확인
                    if(email.getExpirationDate().isAfter(LocalDateTime.now())){//인증시간이 지났는지 확인
                        email.setExpired(true);
                        email.setLastModifiedDate(LocalDateTime.now());
                        emailRepository.save(email);
                        return true;
                    } else{
                        System.out.println("코드는 맞지만 만료");
                        email.setExpired(true);
                        emailRepository.save(email);
                        return false;
                    }
                }
                else{
                    System.out.println("만료된 코드");
                }
            }
        }
        return false;
    }



}