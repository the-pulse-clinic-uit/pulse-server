package com.pulseclinic.pulse_server.modules.chat.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Service
public class StaffAvailabilityService {

    // Track available staff members
    private final Set<String> availableStaff = new CopyOnWriteArraySet<>();

    // Track active patient-staff conversations
    private final Map<String, String> activeChats = new ConcurrentHashMap<>(); // patientId -> staffId

    public void markStaffAsAvailable(String staffId) {
        availableStaff.add(staffId);
    }

    public void markStaffAsUnavailable(String staffId) {
        availableStaff.remove(staffId);
    }

    public boolean isStaffAvailable(String staffId) {
        return availableStaff.contains(staffId);
    }

    public Set<String> getAvailableStaff() {
        return Set.copyOf(availableStaff);
    }

    public String getNextAvailableStaff() {
        return availableStaff.stream().findFirst().orElse(null);
    }

    public void assignStaffToPatient(String patientId, String staffId) {
        activeChats.put(patientId, staffId);
    }

    public String getAssignedStaff(String patientId) {
        return activeChats.get(patientId);
    }

    public void endChat(String patientId) {
        activeChats.remove(patientId);
    }

    public int getAvailableStaffCount() {
        return availableStaff.size();
    }

    public boolean hasAvailableStaff() {
        return !availableStaff.isEmpty();
    }
}
