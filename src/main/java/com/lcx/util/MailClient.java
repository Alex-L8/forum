package com.lcx.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

/**
 * Create by LCX on 7/15/2022 11:56 AM
 */
@Component
public class MailClient {

        private static final Logger logger = LoggerFactory.getLogger(MailClient.class);

        @Autowired
        private JavaMailSender mailSender;

        // 从配置文件中获取值
        @Value("${spring.mail.username}")
        private String from;

        public void sendMail(String to, String subject, String content) {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message);
                helper.setFrom(from,"隆昌翔");
                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(content, true); // true允许发送html格式
                mailSender.send(helper.getMimeMessage());
            } catch (MessagingException | UnsupportedEncodingException e) {
                logger.error("发送邮件失败:" + e.getMessage());
            }
        }
}
