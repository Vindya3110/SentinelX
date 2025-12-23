package io.vindhya.gmailmcp.tools;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class GmailTools {

    @Value("${gmail.host}")
    private String host;

    @Value("${gmail.port}")
    private String portStr;

    @Value("${gmail.username}")
    private String username;

    @Value("${gmail.password}")
    private String password;

    @Value("${gmail.recipientList}")
    private String recipientList;

    private int port;

    @PostConstruct
    public void init() {
        try {
            this.port = Integer.parseInt(portStr);
            log.info("Gmail configuration initialized - Host: {}, Port: {}, Username: {}", host, port, username);
        } catch (NumberFormatException e) {
            log.error("Invalid port number: {}", portStr, e);
            this.port = 587; // Default SMTP port
        }
    }

    @McpTool(description = "Send an email through Gmail SMTP server")
    public Map<String, String> sendEmail(
            @McpToolParam(description = "Subject of the email")
            String subject,
            @McpToolParam(description = "Content of the email in HTML format")
            String content
    ) {
        log.info("Attempting to send email with subject: {}", subject);
        
        List<String> recipients = Arrays.asList(recipientList.split("\\s*,\\s*"));
        Map<String, String> results = new LinkedHashMap<>();

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        for (String toEmail : recipients) {
            try {
                log.debug("Sending email to: {}", toEmail);
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(username));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail.trim()));
                message.setSubject(subject);
                
                // Create multipart message for better HTML handling
                MimeBodyPart htmlPart = new MimeBodyPart();
                htmlPart.setContent(content, "text/html; charset=utf-8");
                
                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(htmlPart);
                
                message.setContent(multipart);

                Transport.send(message);
                results.put(toEmail, "SUCCESS");
                log.info("Email successfully sent to: {}", toEmail);

            } catch (MessagingException e) {
                String errorMsg = "FAILED: " + e.getMessage();
                results.put(toEmail, errorMsg);
                log.error("Failed to send email to {}: {}", toEmail, e.getMessage(), e);
            }
        }

        log.info("Email sending completed. Results: {}", results);
        return results;
    }

    @McpTool(description = "Send an email to a specific recipient through Gmail SMTP server")
    public Map<String, String> sendEmailToRecipient(
            @McpToolParam(description = "Recipient email address")
            String recipientEmail,
            @McpToolParam(description = "Subject of the email")
            String subject,
            @McpToolParam(description = "Content of the email in HTML format")
            String content
    ) {
        log.info("Attempting to send email to: {} with subject: {}", recipientEmail, subject);
        
        Map<String, String> result = new LinkedHashMap<>();

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail.trim()));
            message.setSubject(subject);
            
            // Create multipart message for better HTML handling
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(content, "text/html; charset=utf-8");
            
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(htmlPart);
            
            message.setContent(multipart);

            Transport.send(message);
            result.put(recipientEmail, "SUCCESS");
            log.info("Email successfully sent to: {}", recipientEmail);

        } catch (MessagingException e) {
            String errorMsg = "FAILED: " + e.getMessage();
            result.put(recipientEmail, errorMsg);
            log.error("Failed to send email to {}: {}", recipientEmail, e.getMessage(), e);
        }

        return result;
    }
}
