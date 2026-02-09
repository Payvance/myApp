/**
 * Copyright: Â© 2024 Payvance Innovation Pvt. Ltd.
 *
 * Organization: Payvance Innovation Pvt. Ltd.
 *
 * This is unpublished, proprietary, confidential source code of Payvance Innovation Pvt. Ltd.
 * Payvance Innovation Pvt. Ltd. retains all title to and intellectual property rights in these materials.
 *
 **/

/**
 *
 * @author           version     date        change description
 * Anjor         	 1.0.0       30-Dec-2025    class created
 *
 **/
package com.payvance.erp_saas.core.notification.email;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailParseException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.payvance.erp_saas.exceptions.UserNotAllowedException;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailJob {

    private final JavaMailSender mailSender;
    private static final Logger log = LoggerFactory.getLogger(EmailJob.class);

    @Async("emailTaskExecutor")
    public void sendHtmlEmail(String to,String subject, String htmlBody, List<String> ccEmails) {
        // Async fire-and-forget: log any exceptions via AsyncUncaughtExceptionHandler
        log.info("[EmailJob] Sending async email to={}", to);
        try {
            MimeMessage message = buildMimeMessage(to, subject, htmlBody, ccEmails);
            mailSender.send(message);
            log.info("[EmailJob] Async email sent to={}", to);

        } catch (Exception ex) {
            // Let the async exception handler or logs capture this; still log locally
            log.error("[EmailJob] Async email failed to send to={}: {}", to, ex.getMessage());
            throw ex instanceof RuntimeException ? (RuntimeException) ex : new RuntimeException(ex);
        }
    }

    /**
     * Synchronous email send. Use this for critical notifications where failures
     * must be visible to the caller (e.g. startTrial email - so transaction can roll back).
     */
    public void sendHtmlEmailSync(String to, String subject, String htmlBody,List<String> ccEmails) {
        log.info("[EmailJob] Sending sync email to={}", to);
        try {
            MimeMessage message = buildMimeMessage(to, subject, htmlBody, ccEmails);
            mailSender.send(message);
            log.info("[EmailJob] Sync email sent to={}", to);
        } catch (MailAuthenticationException ex) {
            log.error("[EmailJob] MailAuthenticationException sending to={}: {}", to, ex.getMessage());
            throw new UserNotAllowedException("Email authentication failed");
        } catch (MailSendException | MailParseException ex) {
            log.error("[EmailJob] MailSend/ParseException sending to={}: {}", to, ex.getMessage());
            throw new UserNotAllowedException("Failed to send email");
        } catch (MailException ex) {
            log.error("[EmailJob] MailException sending to={}: {}", to, ex.getMessage());
            throw new UserNotAllowedException("Email service unavailable");
        } catch (MessagingException ex) {
            log.error("[EmailJob] MessagingException building message for to={}: {}", to, ex.getMessage());
            throw new UserNotAllowedException("Failed to construct email");
        }
    }

    private MimeMessage buildMimeMessage(String to, String subject, String htmlBody, List<String> ccEmails) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom("no-reply@payvance.co.in");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        
        // ✅ ADD CC ONLY IF PRESENT
        if (ccEmails != null && !ccEmails.isEmpty()) {
            helper.setCc(ccEmails.toArray(new String[0]));
        }

        return message;
    }
}
