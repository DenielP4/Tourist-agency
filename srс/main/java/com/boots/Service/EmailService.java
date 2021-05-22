package com.boots.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;

    @Async
    public void send(String mail,String message){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("maks.akopa.ru@gmail.com");
        mailMessage.setTo(mail);
        mailMessage.setSubject("Турагентство \"Убвувнгве убвунвунбвге осас\"");
        mailMessage.setText(message);
        javaMailSender.send(mailMessage);
    }
}
