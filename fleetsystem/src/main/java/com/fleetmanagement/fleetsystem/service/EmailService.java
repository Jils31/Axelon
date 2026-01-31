package com.fleetmanagement.fleetsystem.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendMaintenanceAlert(String to, String vehicleRegistration, String maintenanceType, String dueDate) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Maintenance Alert - " + vehicleRegistration);
            message.setText(
                    "Dear Fleet Manager,\n\n" +
                            "This is a reminder that maintenance is due for vehicle: " + vehicleRegistration + "\n" +
                            "Maintenance Type: " + maintenanceType + "\n" +
                            "Due Date: " + dueDate + "\n\n" +
                            "Please schedule the maintenance at your earliest convenience.\n\n" +
                            "Best regards,\n" +
                            "Fleet Management System"
            );

            mailSender.send(message);
            log.info("Maintenance alert email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to: {}", to, e);
        }
    }

    public void sendMaintenanceCompletionNotification(String to, String vehicleRegistration, String maintenanceType, Double cost) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Maintenance Completed - " + vehicleRegistration);
            message.setText(
                    "Dear Fleet Manager,\n\n" +
                            "Maintenance has been completed for vehicle: " + vehicleRegistration + "\n" +
                            "Maintenance Type: " + maintenanceType + "\n" +
                            "Cost: $" + cost + "\n\n" +
                            "Best regards,\n" +
                            "Fleet Management System"
            );

            mailSender.send(message);
            log.info("Maintenance completion email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to: {}", to, e);
        }
    }
}