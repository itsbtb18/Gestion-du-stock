package org.example.util;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailUtil {
    
    private static final String SMTP_HOST = "smtp.gmail.com"; 
    private static final String SMTP_PORT = "587";
    private static final String FROM_EMAIL = "noreply@reb7a.com"; 
    private static final String EMAIL_PASSWORD = "your_password"; 
    
    public static boolean sendEmail(String to, String subject, String body) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, EMAIL_PASSWORD);
            }
        });
        
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);
            
            Transport.send(message);
            System.out.println("Email sent successfully to: " + to);
            return true;
            
        } catch (MessagingException e) {
            System.err.println("Error sending email: " + e.getMessage());
            return false;
        }
    }
    
    public static boolean sendHTMLEmail(String to, String subject, String htmlBody) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, EMAIL_PASSWORD);
            }
        });
        
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setContent(htmlBody, "text/html; charset=utf-8");
            
            Transport.send(message);
            System.out.println("HTML Email sent successfully to: " + to);
            return true;
            
        } catch (MessagingException e) {
            System.err.println("Error sending HTML email: " + e.getMessage());
            return false;
        }
    }
    
    public static boolean sendReceipt(String to, String customerName, String receiptNumber, double total) {
        String subject = "Votre reçu #" + receiptNumber;
        String body = String.format(
            "Bonjour %s,\n\n" +
            "Merci pour votre achat!\n\n" +
            "Numéro de reçu: %s\n" +
            "Montant total: %.2f €\n\n" +
            "Cordialement,\n" +
            "L'équipe Reb7a Supermarket",
            customerName, receiptNumber, total
        );
        
        return sendEmail(to, subject, body);
    }
    
    public static boolean sendLowStockAlert(String to, String productName, int quantity) {
        String subject = "⚠️ Alerte Stock Bas - " + productName;
        String body = String.format(
            "ALERTE STOCK BAS\n\n" +
            "Produit: %s\n" +
            "Quantité actuelle: %d\n\n" +
            "Veuillez réapprovisionner ce produit.\n\n" +
            "Système de Gestion Reb7a",
            productName, quantity
        );
        
        return sendEmail(to, subject, body);
    }
    
    public static boolean sendExpirationAlert(String to, String productName, String expirationDate) {
        String subject = "⚠️ Alerte Expiration - " + productName;
        String body = String.format(
            "ALERTE EXPIRATION\n\n" +
            "Produit: %s\n" +
            "Date d'expiration: %s\n\n" +
            "Ce produit expire bientôt.\n\n" +
            "Système de Gestion Reb7a",
            productName, expirationDate
        );
        
        return sendEmail(to, subject, body);
    }
}
