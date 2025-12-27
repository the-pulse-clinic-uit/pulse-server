package com.pulseclinic.pulse_server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // General Errors (9xxx)
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_REQUEST(9001, "Invalid request data", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED_ACCESS(9002, "Unauthorized access", HttpStatus.UNAUTHORIZED),
    FORBIDDEN_ACTION(9003, "Forbidden action", HttpStatus.FORBIDDEN),
    
    // User Module (1xxx)
    USER_NOT_FOUND(1001, "User not found", HttpStatus.NOT_FOUND),
    USER_ALREADY_EXISTS(1002, "User already exists", HttpStatus.CONFLICT),
    INVALID_CREDENTIALS(1003, "Invalid email or password", HttpStatus.UNAUTHORIZED),
    EMAIL_ALREADY_EXISTS(1004, "Email already exists", HttpStatus.CONFLICT),
    WEAK_PASSWORD(1005, "Password does not meet security requirements", HttpStatus.BAD_REQUEST),
    EMAIL_NOT_VERIFIED(1006, "Email not verified", HttpStatus.FORBIDDEN),
    
    // Role Module (2xxx)
    ROLE_NOT_FOUND(2001, "Role not found", HttpStatus.NOT_FOUND),
    ROLE_ALREADY_EXISTS(2002, "Role already exists", HttpStatus.CONFLICT),
    CANNOT_DELETE_ROLE(2003, "Cannot delete role with assigned users", HttpStatus.CONFLICT),
    
    // Patient Module (3xxx)
    PATIENT_NOT_FOUND(3001, "Patient not found", HttpStatus.NOT_FOUND),
    PATIENT_ALREADY_EXISTS(3002, "Patient already exists", HttpStatus.CONFLICT),
    INVALID_CMND(3003, "Invalid CMND number", HttpStatus.BAD_REQUEST),
    
    // Department Module (4xxx)
    DEPARTMENT_NOT_FOUND(4001, "Department not found", HttpStatus.NOT_FOUND),
    DEPARTMENT_ALREADY_EXISTS(4002, "Department already exists", HttpStatus.CONFLICT),
    DEPARTMENT_HAS_STAFF(4003, "Cannot delete department with staff members", HttpStatus.CONFLICT),
    
    // Staff Module (5xxx)
    STAFF_NOT_FOUND(5001, "Staff not found", HttpStatus.NOT_FOUND),
    STAFF_ALREADY_EXISTS(5002, "Staff already exists for this user", HttpStatus.CONFLICT),
    INVALID_HIRE_DATE(5003, "Invalid hire date", HttpStatus.BAD_REQUEST),
    
    // Doctor Module (6xxx)
    DOCTOR_NOT_FOUND(6001, "Doctor not found", HttpStatus.NOT_FOUND),
    DOCTOR_ALREADY_EXISTS(6002, "Doctor already exists for this staff", HttpStatus.CONFLICT),
    INVALID_LICENSE(6003, "Invalid license number", HttpStatus.BAD_REQUEST),
    DOCTOR_NOT_AVAILABLE(6004, "Doctor not available at requested time", HttpStatus.CONFLICT),
    
    // Shift Module (7xxx)
    SHIFT_NOT_FOUND(7001, "Shift not found", HttpStatus.NOT_FOUND),
    SHIFT_FULL(7002, "Shift is at maximum capacity", HttpStatus.CONFLICT),
    INVALID_SHIFT_TIME(7003, "Invalid shift time range", HttpStatus.BAD_REQUEST),
    SHIFT_ALREADY_EXISTS(7004, "Shift already exists for this date and time", HttpStatus.CONFLICT),
    
    // Shift Assignment Module (8xxx)
    ASSIGNMENT_NOT_FOUND(8001, "Shift assignment not found", HttpStatus.NOT_FOUND),
    ASSIGNMENT_CONFLICT(8002, "Staff already assigned to another shift at this time", HttpStatus.CONFLICT),
    CANNOT_CANCEL_ASSIGNMENT(8003, "Cannot cancel confirmed assignment", HttpStatus.CONFLICT),
    ROOM_ALREADY_ASSIGNED(8004, "Room already assigned for this shift", HttpStatus.CONFLICT),
    
    // Waitlist Module (10xxx)
    WAITLIST_ENTRY_NOT_FOUND(10001, "Waitlist entry not found", HttpStatus.NOT_FOUND),
    ALREADY_IN_WAITLIST(10002, "Patient already in waitlist for this department", HttpStatus.CONFLICT),
    WAITLIST_ALREADY_CALLED(10003, "Waitlist entry already called", HttpStatus.CONFLICT),
    
    // Appointment Module (11xxx)
    APPOINTMENT_NOT_FOUND(11001, "Appointment not found", HttpStatus.NOT_FOUND),
    APPOINTMENT_CONFLICT(11002, "Appointment conflicts with existing schedule", HttpStatus.CONFLICT),
    CANNOT_CANCEL_APPOINTMENT(11003, "Cannot cancel appointment within 24 hours", HttpStatus.CONFLICT),
    APPOINTMENT_ALREADY_COMPLETED(11004, "Appointment already completed", HttpStatus.CONFLICT),
    INVALID_APPOINTMENT_TIME(11005, "Invalid appointment time", HttpStatus.BAD_REQUEST),
    CANNOT_RESCHEDULE_APPOINTMENT(11006, "Cannot reschedule this appointment", HttpStatus.CONFLICT),
    
    // Drug Module (12xxx)
    DRUG_NOT_FOUND(12001, "Drug not found", HttpStatus.NOT_FOUND),
    DRUG_ALREADY_EXISTS(12002, "Drug already exists", HttpStatus.CONFLICT),
    INSUFFICIENT_STOCK(12003, "Insufficient drug stock", HttpStatus.CONFLICT),
    DRUG_OUT_OF_STOCK(12004, "Drug is out of stock", HttpStatus.CONFLICT),
    
    // Prescription Module (13xxx)
    PRESCRIPTION_NOT_FOUND(13001, "Prescription not found", HttpStatus.NOT_FOUND),
    INVALID_PRESCRIPTION_STATUS(13002, "Invalid prescription status transition", HttpStatus.CONFLICT),
    PRESCRIPTION_ALREADY_DISPENSED(13003, "Prescription already dispensed", HttpStatus.CONFLICT),
    CANNOT_MODIFY_PRESCRIPTION(13004, "Cannot modify finalized prescription", HttpStatus.CONFLICT),
    PRESCRIPTION_HAS_NO_ITEMS(13005, "Prescription must have at least one item", HttpStatus.BAD_REQUEST),
    
    // Prescription Detail Module (14xxx)
    PRESCRIPTION_DETAIL_NOT_FOUND(14001, "Prescription detail not found", HttpStatus.NOT_FOUND),
    INVALID_QUANTITY(14002, "Invalid quantity specified", HttpStatus.BAD_REQUEST),
    
    // Encounter Module (15xxx)
    ENCOUNTER_NOT_FOUND(15001, "Encounter not found", HttpStatus.NOT_FOUND),
    ENCOUNTER_ALREADY_COMPLETED(15002, "Encounter already completed", HttpStatus.CONFLICT),
    ENCOUNTER_NOT_STARTED(15003, "Encounter not started yet", HttpStatus.CONFLICT),
    MISSING_DIAGNOSIS(15004, "Diagnosis is required to complete encounter", HttpStatus.BAD_REQUEST),
    
    // Room Module (16xxx)
    ROOM_NOT_FOUND(16001, "Room not found", HttpStatus.NOT_FOUND),
    ROOM_ALREADY_EXISTS(16002, "Room number already exists", HttpStatus.CONFLICT),
    ROOM_NOT_AVAILABLE(16003, "Room is not available", HttpStatus.CONFLICT),
    ROOM_AT_CAPACITY(16004, "Room is at maximum capacity", HttpStatus.CONFLICT),
    CANNOT_DELETE_OCCUPIED_ROOM(16005, "Cannot delete occupied room", HttpStatus.CONFLICT),
    
    // Admission Module (17xxx)
    ADMISSION_NOT_FOUND(17001, "Admission not found", HttpStatus.NOT_FOUND),
    PATIENT_ALREADY_ADMITTED(17002, "Patient already has an ongoing admission", HttpStatus.CONFLICT),
    ADMISSION_ALREADY_DISCHARGED(17003, "Admission already discharged", HttpStatus.CONFLICT),
    CANNOT_TRANSFER_ADMISSION(17004, "Cannot transfer admission to unavailable room", HttpStatus.CONFLICT),
    ADMISSION_NOT_ONGOING(17005, "Admission is not ongoing", HttpStatus.CONFLICT),
    
    // Invoice Module (18xxx)
    INVOICE_NOT_FOUND(18001, "Invoice not found", HttpStatus.NOT_FOUND),
    INVOICE_ALREADY_PAID(18002, "Invoice already paid", HttpStatus.CONFLICT),
    INVOICE_ALREADY_VOIDED(18003, "Invoice already voided", HttpStatus.CONFLICT),
    INVALID_PAYMENT_AMOUNT(18004, "Invalid payment amount", HttpStatus.BAD_REQUEST),
    OVERPAYMENT_NOT_ALLOWED(18005, "Payment exceeds invoice balance", HttpStatus.BAD_REQUEST),
    CANNOT_MODIFY_PAID_INVOICE(18006, "Cannot modify paid invoice", HttpStatus.CONFLICT),
    
    // Follow Up Plan Module (19xxx)
    FOLLOWUP_PLAN_NOT_FOUND(19001, "Follow-up plan not found", HttpStatus.NOT_FOUND),
    PLAN_NOT_ACTIVE(19002, "Follow-up plan is not active", HttpStatus.CONFLICT),
    PLAN_ALREADY_COMPLETED(19003, "Follow-up plan already completed", HttpStatus.CONFLICT),
    CANNOT_MODIFY_COMPLETED_PLAN(19004, "Cannot modify completed plan", HttpStatus.CONFLICT),
    INVALID_RRULE(19005, "Invalid recurrence rule format", HttpStatus.BAD_REQUEST),
    
    // Staff Rating Module (20xxx)
    RATING_NOT_FOUND(20001, "Staff rating not found", HttpStatus.NOT_FOUND),
    INVALID_RATING_VALUE(20002, "Rating must be between 1 and 5", HttpStatus.BAD_REQUEST),
    RATING_ALREADY_EXISTS(20003, "Rating already submitted for this encounter", HttpStatus.CONFLICT),
    CANNOT_RATE_OWN_SERVICE(20004, "Staff cannot rate their own service", HttpStatus.CONFLICT);

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private int code;
    private String message;
    private HttpStatusCode statusCode;
}

