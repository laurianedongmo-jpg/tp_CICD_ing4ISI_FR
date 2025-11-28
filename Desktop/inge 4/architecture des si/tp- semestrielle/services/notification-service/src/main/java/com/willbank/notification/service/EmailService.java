package com.willbank.notification.service;

import com.willbank.notification.entity.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    
    private final JavaMailSender mailSender;
    
    @Value("${notification.email.from}")
    private String fromEmail;
    
    @Value("${notification.email.enabled:true}")
    private boolean emailEnabled;
    
    @Async
    public void sendEmail(String to, String subject, String content) {
        if (!emailEnabled) {
            log.info("Email sending is disabled. Would send to: {} - Subject: {}", to, subject);
            return;
        }
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            
            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
            
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }
    
    public void sendWelcomeEmail(String email, String nom, String prenom, String numeroClient) {
        String subject = "Bienvenue chez WillBank !";
        String content = String.format(
            "Bonjour %s %s,\n\n" +
            "Bienvenue chez WillBank !\n\n" +
            "Votre compte client %s a été créé avec succès.\n\n" +
            "Pour finaliser votre inscription, veuillez compléter votre dossier KYC.\n\n" +
            "Cordialement,\n" +
            "L'équipe WillBank",
            prenom, nom, numeroClient
        );
        
        sendEmail(email, subject, content);
    }
    
    public void sendKYCValidatedEmail(String email, String numeroClient) {
        String subject = "KYC Validé - WillBank";
        String content = String.format(
            "Bonjour,\n\n" +
            "Votre dossier KYC pour le compte %s a été validé avec succès.\n\n" +
            "Vous pouvez maintenant profiter de tous nos services.\n\n" +
            "Cordialement,\n" +
            "L'équipe WillBank",
            numeroClient
        );
        
        sendEmail(email, subject, content);
    }
    
    public void sendCompteCreatedEmail(String email, String numeroCompte, String typeCompte) {
        String subject = "Nouveau compte créé - WillBank";
        String content = String.format(
            "Bonjour,\n\n" +
            "Votre compte %s de type %s a été créé avec succès.\n\n" +
            "Vous pouvez maintenant effectuer des opérations sur ce compte.\n\n" +
            "Cordialement,\n" +
            "L'équipe WillBank",
            numeroCompte, typeCompte
        );
        
        sendEmail(email, subject, content);
    }
    
    public void sendTransactionEmail(String email, String reference, String typeTransaction, 
                                    String montant, String numeroCompte, String nouveauSolde) {
        String subject = "Transaction effectuée - " + reference;
        String content = String.format(
            "Bonjour,\n\n" +
            "Une transaction a été effectuée sur votre compte %s.\n\n" +
            "Type: %s\n" +
            "Montant: %s XOF\n" +
            "Référence: %s\n" +
            "Nouveau solde: %s XOF\n\n" +
            "Si vous n'êtes pas à l'origine de cette transaction, contactez-nous immédiatement.\n\n" +
            "Cordialement,\n" +
            "L'équipe WillBank",
            numeroCompte, typeTransaction, montant, reference, nouveauSolde
        );
        
        sendEmail(email, subject, content);
    }
    
    public void sendCompteStatusChangedEmail(String email, String numeroCompte, 
                                            String oldStatus, String newStatus) {
        String subject = "Changement de statut de compte - WillBank";
        String content = String.format(
            "Bonjour,\n\n" +
            "Le statut de votre compte %s a été modifié.\n\n" +
            "Ancien statut: %s\n" +
            "Nouveau statut: %s\n\n" +
            "Si vous avez des questions, n'hésitez pas à nous contacter.\n\n" +
            "Cordialement,\n" +
            "L'équipe WillBank",
            numeroCompte, oldStatus, newStatus
        );
        
        sendEmail(email, subject, content);
    }
}
