package com.example.booking.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailMessage;
import org.springframework.mail.MailParseException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MailService implements MailMessage {
    @Autowired
    private JavaMailSender mailSender;

    public void sendMail(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true); // Đặt tham số thứ hai là true để cho phép gửi email dưới dạng HTML
        mailSender.send(message);

    }
    @Override
    public void setFrom(String from) throws MailParseException {

    }

    @Override
    public void setReplyTo(String replyTo) throws MailParseException {

    }

    @Override
    public void setTo(String to) throws MailParseException {

    }

    @Override
    public void setTo(String... to) throws MailParseException {

    }

    @Override
    public void setCc(String cc) throws MailParseException {

    }

    @Override
    public void setCc(String... cc) throws MailParseException {

    }

    @Override
    public void setBcc(String bcc) throws MailParseException {

    }

    @Override
    public void setBcc(String... bcc) throws MailParseException {

    }

    @Override
    public void setSentDate(Date sentDate) throws MailParseException {

    }

    @Override
    public void setSubject(String subject) throws MailParseException {

    }

    @Override
    public void setText(String text) throws MailParseException {

    }
}
