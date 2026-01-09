package com.pulseclinic.pulse_server.modules.ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pulseclinic.pulse_server.modules.ai.dto.ChatDto;
import com.pulseclinic.pulse_server.modules.ai.dto.ChatResponseDto;
import com.pulseclinic.pulse_server.modules.appointments.repository.AppointmentRepository;
import com.pulseclinic.pulse_server.modules.pharmacy.entity.Drug;
import com.pulseclinic.pulse_server.modules.pharmacy.repository.DrugRepository;
import com.pulseclinic.pulse_server.modules.staff.entity.Department;
import com.pulseclinic.pulse_server.modules.staff.entity.Doctor;
import com.pulseclinic.pulse_server.modules.staff.repository.DepartmentRepository;
import com.pulseclinic.pulse_server.modules.staff.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatAiService {

    private final DrugRepository drugRepository;
    private final DoctorRepository doctorRepository;
    private final DepartmentRepository departmentRepository;
    private final AppointmentRepository appointmentRepository;
    private final ObjectMapper objectMapper;
    private final WebClient.Builder webClientBuilder;

    @Value("${gemini.api-key}")
    private String apiKey;

    @Value("${gemini.model-name:gemini-2.5-flash}")
    private String modelName;

    private String systemInstruction;

    private String getSystemInstruction() {
        if (systemInstruction == null) {
            try {
                Path promptPath = Paths.get("src/main/resources/ai/project_prompt.json");
                if (Files.exists(promptPath)) {
                    String promptData = Files.readString(promptPath);
                    systemInstruction = "Project Overview: " + promptData +
                        "\n\nYou are a helpful, friendly assistant for Pulse Clinic! 🏥 " +
                        "Always respond with a warm, professional, and supportive tone. " +
                        "Use emojis occasionally 😊 to make responses more engaging. " +
                        "Keep answers concise, helpful, and user-focused. " +
                        "Focus on helping patients and staff with questions about appointments, medications, departments, and doctors. " +
                        "NEVER mention technical details like databases, code, or internal systems. " +
                        "If data isn't found, suggest contacting the clinic directly or checking the app.";
                } else {
                    log.warn("Project prompt file not found, using default instruction");
                    systemInstruction = getDefaultSystemInstruction();
                }
            } catch (IOException e) {
                log.error("Error loading project prompt", e);
                systemInstruction = getDefaultSystemInstruction();
            }
        }
        return systemInstruction;
    }

    private String getDefaultSystemInstruction() {
        return "You are a helpful, friendly assistant for Pulse Clinic! 🏥 " +
            "Always respond with a warm, professional, and supportive tone. " +
            "Use emojis occasionally 😊 to make responses more engaging. " +
            "Keep answers concise, helpful, and user-focused. " +
            "Focus on helping patients and staff with questions about appointments, medications, departments, and doctors. " +
            "Guide users to relevant features and provide support.";
    }

    public ChatResponseDto chat(ChatDto chatDto) {
        String message = chatDto.getMessage();
        List<ChatDto.ChatHistoryItem> history = chatDto.getHistory() != null ? chatDto.getHistory() : new ArrayList<>();

        // build db context from user query
        StringBuilder dbContext = new StringBuilder();
        String lowerMessage = message.toLowerCase();

        // query drugs if mentioned
        if (lowerMessage.contains("drug") || lowerMessage.contains("medicine") ||
            lowerMessage.contains("medication") || lowerMessage.contains("prescription")) {
            String searchTerm = extractSearchTerm(message, new String[]{"drug", "medicine", "medication", "prescription"});
            List<Drug> drugs = searchTerm != null && !searchTerm.isEmpty()
                ? drugRepository.findAll().stream()
                    .filter(d -> d.getName().toLowerCase().contains(searchTerm.toLowerCase()))
                    .limit(5)
                    .collect(Collectors.toList())
                : drugRepository.findAll().stream().limit(3).collect(Collectors.toList());

            if (!drugs.isEmpty()) {
                dbContext.append("\n\nAvailable Medications:\n");
                for (Drug drug : drugs) {
                    dbContext.append(String.format("- %s (%s, %s %s)\n",
                        drug.getName(),
                        drug.getDosageForm(),
                        drug.getStrength(),
                        drug.getUnit()));
                }
            }
        }

        // query doctors if mentioned
        if (lowerMessage.contains("doctor") || lowerMessage.contains("physician") ||
            lowerMessage.contains("specialist")) {
            List<Doctor> doctors = doctorRepository.findAll().stream().limit(5).collect(Collectors.toList());
            if (!doctors.isEmpty()) {
                dbContext.append("\n\nAvailable Doctors:\n");
                for (Doctor doctor : doctors) {
                    if (doctor.getStaff() != null && doctor.getStaff().getUser() != null) {
                        dbContext.append(String.format("- Dr. %s (Email: %s, License: %s)\n",
                            doctor.getStaff().getUser().getFullName(),
                            doctor.getStaff().getUser().getEmail(),
                            doctor.getLicenseId()));
                    }
                }
            }
        }

        // query departments if mentioned
        if (lowerMessage.contains("department") || lowerMessage.contains("ward") ||
            lowerMessage.contains("unit")) {
            List<Department> departments = departmentRepository.findAll().stream().limit(5).collect(Collectors.toList());
            if (!departments.isEmpty()) {
                dbContext.append("\n\nClinic Departments:\n");
                for (Department dept : departments) {
                    dbContext.append(String.format("- %s: %s\n", dept.getName(), dept.getDescription()));
                }
            }
        }

        // query appointments (generic only, privacy-aware)
        if (lowerMessage.contains("appointment") || lowerMessage.contains("schedule") ||
            lowerMessage.contains("booking")) {
            long totalAppointments = appointmentRepository.count();
            dbContext.append(String.format("\n\nAppointment System: The clinic manages appointments for patients. " +
                "Currently tracking %d appointments in the system.\n", totalAppointments));
        }

        // prepare system instruction with db context
        String fullSystemInstruction = getSystemInstruction();
        if (dbContext.length() > 0) {
            fullSystemInstruction += "\n\nIMPORTANT: Use this real-time data from the clinic database to answer questions. " +
                "If the data shows matches, mention them! If no matches, suggest contacting the clinic.\n" +
                "Database Context:" + dbContext.toString();
        }

        // build full prompt with history
        StringBuilder contents = new StringBuilder();
        contents.append(fullSystemInstruction).append("\n\n");

        if (history != null && !history.isEmpty()) {
            contents.append("Conversation history:\n");
            for (ChatDto.ChatHistoryItem item : history) {
                contents.append(item.getRole()).append(": ").append(item.getContent()).append("\n");
            }
        }

        contents.append("user: ").append(message);

        // call gemini api
        String reply;
        try {
            reply = callGeminiApi(contents.toString());
        } catch (RuntimeException e) {
            log.error("Error calling Gemini API", e);
            // check for rate limit message
            if (e.getMessage().contains("Rate limit")) {
                reply = "I'm currently experiencing high demand! 😅 Please wait a moment and try again. " +
                    "Or feel free to contact the clinic directly at the front desk for immediate assistance. 🏥";
            } else {
                reply = "I apologize, but I'm having trouble processing your request right now. " +
                    "Please try again later or contact the clinic directly for assistance. 😊";
            }
        }

        // update history with new messages
        List<ChatDto.ChatHistoryItem> updatedHistory = new ArrayList<>(history);
        ChatDto.ChatHistoryItem userItem = new ChatDto.ChatHistoryItem();
        userItem.setRole("user");
        userItem.setContent(message);
        updatedHistory.add(userItem);

        ChatDto.ChatHistoryItem assistantItem = new ChatDto.ChatHistoryItem();
        assistantItem.setRole("assistant");
        assistantItem.setContent(reply);
        updatedHistory.add(assistantItem);

        return new ChatResponseDto(reply, updatedHistory);
    }

    private String callGeminiApi(String prompt) {
        try {
            // build json request
            ObjectNode requestBody = objectMapper.createObjectNode();
            ArrayNode contentsArray = objectMapper.createArrayNode();
            ObjectNode contentObject = objectMapper.createObjectNode();

            ArrayNode partsArray = objectMapper.createArrayNode();
            ObjectNode partObject = objectMapper.createObjectNode();
            partObject.put("text", prompt);
            partsArray.add(partObject);

            contentObject.set("parts", partsArray);
            contentsArray.add(contentObject);

            requestBody.set("contents", contentsArray);

            // post to gemini api
            WebClient webClient = webClientBuilder.build();
            String url = String.format(
                "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s",
                modelName,
                apiKey
            );

            String responseBody = webClient.post()
                .uri(url)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            // parse response
            JsonNode responseJson = objectMapper.readTree(responseBody);

            if (responseJson.has("candidates") && responseJson.get("candidates").size() > 0) {
                JsonNode candidate = responseJson.get("candidates").get(0);
                if (candidate.has("content") && candidate.get("content").has("parts") &&
                    candidate.get("content").get("parts").size() > 0) {
                    JsonNode part = candidate.get("content").get("parts").get(0);
                    if (part.has("text")) {
                        return part.get("text").asText();
                    }
                }
            }

            return "Sorry, I could not generate a response. 😊";

        } catch (WebClientResponseException.TooManyRequests e) {
            log.warn("Gemini API rate limit exceeded", e);
            throw new RuntimeException("Rate limit exceeded. Please try again in a few moments. 🕐", e);
        } catch (WebClientResponseException e) {
            log.error("Gemini API error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new RuntimeException("Failed to call Gemini API: " + e.getStatusText(), e);
        } catch (Exception e) {
            log.error("Error in Gemini API call", e);
            throw new RuntimeException("Failed to call Gemini API", e);
        }
    }

    private String extractSearchTerm(String message, String[] keywords) {
        String[] words = message.toLowerCase().split("\\s+");
        List<String> potentialTerms = new ArrayList<>();

        for (String word : words) {
            boolean isKeyword = false;
            for (String keyword : keywords) {
                if (word.contains(keyword)) {
                    isKeyword = true;
                    break;
                }
            }
            if (!isKeyword && word.length() > 2) {
                potentialTerms.add(word);
            }
        }

        return potentialTerms.isEmpty() ? null : potentialTerms.get(potentialTerms.size() - 1);
    }
}
