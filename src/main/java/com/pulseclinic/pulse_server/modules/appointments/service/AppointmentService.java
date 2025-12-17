package com.pulseclinic.pulse_server.modules.appointments.service;

import com.pulseclinic.pulse_server.enums.AppointmentStatus;
import com.pulseclinic.pulse_server.modules.appointments.dto.AppointmentDto;
import com.pulseclinic.pulse_server.modules.appointments.dto.AppointmentRequestDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AppointmentService {
    /**
     * Đặt lịch hẹn mới
     */
    AppointmentDto scheduleAppointment(AppointmentRequestDto appointmentRequestDto);

    /**
     * Cập nhật trạng thái cuộc hẹn
     */
    boolean updateStatus(UUID appointmentId, AppointmentStatus status);

    /**
     * Hủy cuộc hẹn
     */
    boolean cancelAppointment(UUID appointmentId, String reason);

    /**
     * Thay đổi thời gian cuộc hẹn
     */
    boolean rescheduleAppointment(UUID appointmentId, LocalDateTime newStartTime, LocalDateTime newEndTime);

    /**
     * Xác nhận cuộc hẹn đang chờ
     */
    boolean confirmAppointment(UUID appointmentId);

    /**
     * Đánh dấu bệnh nhân đã check-in
     */
    boolean checkIn(UUID appointmentId);

    /**
     * Hoàn thành cuộc hẹn
     */
    boolean markAsDone(UUID appointmentId);

    /**
     * Đánh dấu bệnh nhân không đến
     */
    boolean markAsNoShow(UUID appointmentId);

    /**
     * Kiểm tra xung đột lịch trình
     */
    boolean checkConflicts(UUID appointmentId);

    /**
     * Xác thực slot thời gian có khả dụng hay không
     */
    boolean validateTimeSlot(UUID appointmentId);

    /**
     * Tạo bệnh án từ cuộc hẹn
     */
    Object createEncounter(UUID appointmentId);

    /**
     * Gửi nhắc nhở cuộc hẹn
     */
    boolean sendReminder(UUID appointmentId);

    /**
     * Kiểm tra cuộc hẹn có thể hủy hay không
     */
    boolean canCancel(UUID appointmentId);

    /**
     * Kiểm tra cuộc hẹn có thể dời lịch hay không
     */
    boolean canReschedule(UUID appointmentId);
    
    /**
     * Lấy thông tin appointment theo ID
     */
    Optional<AppointmentDto> getAppointmentById(UUID appointmentId);
    
    /**
     * Lấy tất cả appointments
     */
    List<AppointmentDto> findAll();
    
    /**
     * Lấy appointments của bệnh nhân
     */
    List<AppointmentDto> findByPatient(UUID patientId);
    
    /**
     * Lấy appointments sắp tới của bệnh nhân
     */
    List<AppointmentDto> findUpcomingByPatient(UUID patientId);
    
    /**
     * Xóa mềm appointment
     */
    boolean deleteAppointment(UUID appointmentId);
}
