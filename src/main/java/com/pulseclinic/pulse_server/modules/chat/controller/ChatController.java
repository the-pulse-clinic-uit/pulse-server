package com.pulseclinic.pulse_server.modules.chat.controller;

import com.pulseclinic.pulse_server.modules.chat.dto.ChatMessage;
import com.pulseclinic.pulse_server.modules.chat.service.StaffAvailabilityService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.UUID;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final StaffAvailabilityService staffAvailabilityService;

    public ChatController(SimpMessagingTemplate messagingTemplate, StaffAvailabilityService staffAvailabilityService) {
        this.messagingTemplate = messagingTemplate;
        this.staffAvailabilityService = staffAvailabilityService;
    }

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage) {
        chatMessage.setId(UUID.randomUUID().toString());
        chatMessage.setTimestamp(LocalDateTime.now());

        // Send to specific user (staff member)
        messagingTemplate.convertAndSendToUser(
            chatMessage.getRecipientId(),
            "/queue/messages",
            chatMessage
        );
    }

    @MessageMapping("/chat.requestStaff")
    public void requestStaff(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        String patientId = chatMessage.getSenderId();
        String patientUsername = headerAccessor.getUser().getName(); // get email
        String availableStaffId = staffAvailabilityService.getNextAvailableStaff();

        if (availableStaffId != null) {
            // assign staff to patient
            staffAvailabilityService.assignStaffToPatient(patientId, availableStaffId);

            // notify patient (use mail for routing)
            ChatMessage response = new ChatMessage();
            response.setId(UUID.randomUUID().toString());
            response.setTimestamp(LocalDateTime.now());
            response.setRecipientId(patientId);
            response.setSenderId(availableStaffId);
            response.setContent("Staff member connected. How can I help you?");
            response.setType(ChatMessage.MessageType.STAFF_AVAILABLE);

            messagingTemplate.convertAndSendToUser(patientUsername, "/queue/messages", response);

            // notify staff (need to get staff username from chatMessage.senderName)
            ChatMessage staffNotification = new ChatMessage();
            staffNotification.setId(UUID.randomUUID().toString());
            staffNotification.setTimestamp(LocalDateTime.now());
            staffNotification.setRecipientId(availableStaffId);
            staffNotification.setSenderId(patientId);
            staffNotification.setSenderName(chatMessage.getSenderName());
            staffNotification.setContent("New patient chat request from " + chatMessage.getSenderName());
            staffNotification.setType(ChatMessage.MessageType.PATIENT_CONNECTED);

            // send to staff (staffId = mail)
            messagingTemplate.convertAndSendToUser(availableStaffId, "/queue/messages", staffNotification);
        } else {
            // no staff available
            ChatMessage response = new ChatMessage();
            response.setId(UUID.randomUUID().toString());
            response.setTimestamp(LocalDateTime.now());
            response.setRecipientId(patientId);
            response.setContent("No staff members are currently available. Please try again later.");
            response.setType(ChatMessage.MessageType.STAFF_UNAVAILABLE);

            messagingTemplate.convertAndSendToUser(patientUsername, "/queue/messages", response);
        }
    }

    @MessageMapping("/staff.setAvailable")
    public void setStaffAvailable(@Payload ChatMessage chatMessage) {
        String staffId = chatMessage.getSenderId();
        staffAvailabilityService.markStaffAsAvailable(staffId);

        // broadcast to all staff
        ChatMessage notification = new ChatMessage();
        notification.setId(UUID.randomUUID().toString());
        notification.setTimestamp(LocalDateTime.now());
        notification.setSenderId(staffId);
        notification.setSenderName(chatMessage.getSenderName());
        notification.setContent(chatMessage.getSenderName() + " is now available");
        notification.setType(ChatMessage.MessageType.STAFF_AVAILABLE);

        messagingTemplate.convertAndSend("/topic/staff", notification);
    }

    @MessageMapping("/staff.setUnavailable")
    public void setStaffUnavailable(@Payload ChatMessage chatMessage) {
        String staffId = chatMessage.getSenderId();
        staffAvailabilityService.markStaffAsUnavailable(staffId);

        ChatMessage notification = new ChatMessage();
        notification.setId(UUID.randomUUID().toString());
        notification.setTimestamp(LocalDateTime.now());
        notification.setSenderId(staffId);
        notification.setSenderName(chatMessage.getSenderName());
        notification.setContent(chatMessage.getSenderName() + " is now unavailable");
        notification.setType(ChatMessage.MessageType.STAFF_UNAVAILABLE);

        messagingTemplate.convertAndSend("/topic/staff", notification);
    }

    @MessageMapping("/chat.endSession")
    public void endSession(@Payload ChatMessage chatMessage) {
        String patientId = chatMessage.getSenderId();
        String staffId = staffAvailabilityService.getAssignedStaff(patientId);

        if (staffId != null) {
            // notify staff
            ChatMessage notification = new ChatMessage();
            notification.setId(UUID.randomUUID().toString());
            notification.setTimestamp(LocalDateTime.now());
            notification.setRecipientId(staffId); // staff mail
            notification.setSenderId(patientId);
            notification.setContent("Patient has ended the chat session");
            notification.setType(ChatMessage.MessageType.PATIENT_DISCONNECTED);

            messagingTemplate.convertAndSendToUser(staffId, "/queue/messages", notification);

            // remove assignment
            staffAvailabilityService.endChat(patientId);
        }
    }

    // typing indicator
    @MessageMapping("/chat.typing")
    public void typing(@Payload ChatMessage chatMessage) {
        chatMessage.setType(ChatMessage.MessageType.TYPING);
        chatMessage.setTimestamp(LocalDateTime.now());

        messagingTemplate.convertAndSendToUser(
            chatMessage.getRecipientId(),
            "/queue/typing",
            chatMessage
        );
    }
}
