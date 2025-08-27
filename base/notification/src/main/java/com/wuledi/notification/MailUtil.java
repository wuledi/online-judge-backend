package com.wuledi.notification;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * 邮件工具类
 *
 * @author wuledi
 */
@Slf4j
@Component
public class MailUtil {

    @Value("${spring.mail.username}") // 注入发件人
    private String from;

    private final JavaMailSender javaMailSender; // 注入JavaMailSender
    public MailUtil(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    /**
     * 发送纯文本(常规)的邮件
     *
     * @param to      收件人
     * @param subject 主题
     * @param content 内容
     * @return 是否成功
     */
    public boolean sendGeneralMail(String subject, String content, String... to) {
        SimpleMailMessage message = new SimpleMailMessage(); // 创建邮件对象
        message.setFrom(from); // 设置发件人
        message.setTo(to); // 设置收件人
        message.setSubject(subject); // 设置邮件主题
        message.setText(content); // 设置邮件内容
        try {
            javaMailSender.send(message); // 发送邮件
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 发送html的邮件
     *
     * @param to      收件人
     * @param subject 主题
     * @param content 内容
     */
    public void sendHtmlMail(String subject, String content, String... to) {
        MimeMessage message = javaMailSender.createMimeMessage(); // 创建邮件对象
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true); // 创建邮件帮助对象
            helper.setFrom(from); // 设置发件人
            helper.setTo(to); // 设置收件人
            helper.setSubject(subject); // 设置邮件主题
            helper.setText(content, true); // 第二个参数为true表示内容为HTML
            javaMailSender.send(message); // 发送邮件
        } catch (MessagingException e) {
            log.error("发送邮件失败", e);
        }
    }

    /**
     * 发送带附件的邮件
     *
     * @param to        收件人
     * @param subject   主题
     * @param content   内容
     * @param filePaths 附件路径
     * @return 是否成功
     */
    public boolean sendAttachmentsMail(String subject, String content, String[] filePaths, String... to) {
        MimeMessage message = javaMailSender.createMimeMessage(); // 创建邮件对象
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true); // 创建邮件帮助对象
            helper.setFrom(from); // 设置发件人
            helper.setTo(to); // 设置收件人
            helper.setSubject(subject); // 设置邮件主题
            helper.setText(content); // 设置邮件内容
            for (String filePath : filePaths) { // 添加附件
                helper.addAttachment(filePath, new File(filePath));
            }
            javaMailSender.send(message); // 发送邮件
        } catch (MessagingException e) {
            return false;
        }
        return true;
    }

    /**
     * 发送带静态资源的邮件
     *
     * @param to      收件人
     * @param subject 主题
     * @param content 内容
     * @param rscPath 静态资源路径
     * @param rscId   静态资源id
     * @return 是否成功
     */
    public boolean sendInlineMail(String subject, String content, String rscPath, String rscId, String... to) {
        MimeMessage message = javaMailSender.createMimeMessage(); // 创建邮件对象
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true); // 创建邮件帮助对象
            helper.setFrom(from); // 设置发件人
            helper.setTo(to); // 设置收件人
            helper.setSubject(subject); // 设置邮件主题
            helper.setText(content, true); // 第二个参数为true表示内容为HTML
            helper.addInline(rscId, new File(rscPath)); // 添加静态资源
            javaMailSender.send(message); // 发送邮件
        } catch (MessagingException e) {
            return false;
        }
        return true;
    }
}
