# TODO Summary - Pulse Clinic API

## Priority Levels
- **P0 (Critical)**: Core functionality that blocks other features or is essential for basic operations
- **P1 (High)**: Important features frequently used, significant impact on user experience
- **P2 (Medium)**: Enhances functionality, not immediately critical
- **P3 (Low)**: Nice-to-have features, polish and optimization

## Doctor Service (DoctorServiceImpl.java)

### 1. Appointments Management
- **[P1]** **Line 133**: `getAppointments(UUID doctorId, LocalDate date)`
  - TODO: Implement logic lấy các cuộc hẹn cho ngày cụ thể
  - Cần kết hợp với module appointments
  - Status: 🔴 Not Implemented
  - **Priority Reason**: Essential for doctor's daily workflow

- **[P1]** **Line 141**: `getUpcomingAppointments(UUID doctorId)`
  - TODO: Implement logic lấy các cuộc hẹn sắp tới
  - Cần kết hợp với module appointments
  - Status: 🔴 Not Implemented
  - **Priority Reason**: Critical for appointment management

### 2. Encounter/Medical Records Management
- **[P2]** **Line 149**: `getEncounters(UUID doctorId, LocalDate startDate, LocalDate endDate)`
  - TODO: Implement logic lấy các bệnh án trong khoảng thời gian
  - Cần kết hợp với module encounters
  - Status: 🔴 Not Implemented
  - **Priority Reason**: Useful for reporting and analysis

- **[P1]** **Line 165**: `recordDiagnosis(UUID doctorId, UUID encounterId, String diagnosis)`
  - TODO: Implement logic ghi lại chẩn đoán cho bệnh án
  - Cần kết hợp với module encounters
  - Status: 🔴 Not Implemented
  - **Priority Reason**: Core medical documentation feature

### 3. Prescription Management
- **[P1]** **Line 157**: `prescribeMedication(UUID doctorId, UUID encounterId)`
  - TODO: Implement logic tạo đơn thuốc cho bệnh án
  - Cần kết hợp với module pharmacy
  - Status: 🔴 Not Implemented
  - **Priority Reason**: Essential for treatment workflow

### 4. Follow-up Plan Management
- **[P2]** **Line 173**: `createFollowUpPlan(UUID doctorId, UUID encounterId)`
  - TODO: Implement logic tạo kế hoạch tái khám
  - Cần kết hợp với module encounters
  - Status: 🔴 Not Implemented
  - **Priority Reason**: Important for patient care continuity

### 5. Scheduling & Availability
- **[P0]** **Line 181**: `getShiftSchedule(UUID doctorId, LocalDate date)`
  - TODO: Implement logic lấy lịch ca làm việc cho ngày cụ thể
  - Cần kết hợp với module scheduling
  - Status: 🔴 Not Implemented
  - **Priority Reason**: Critical for appointment booking system

- **[P0]** **Line 189**: `checkAvailability(UUID doctorId, LocalDateTime dateTime)`
  - TODO: Implement logic kiểm tra bác sĩ có sẵn sàng hay không
  - Cần kết hợp với module scheduling và appointments
  - Status: 🔴 Not Implemented
  - **Priority Reason**: Blocks appointment creation functionality

### 6. Patient Management
- **[P2]** **Line 209**: `getPatients(UUID doctorId)`
  - TODO: Implement logic lấy tất cả bệnh nhân đã được bác sĩ khám
  - Cần kết hợp với module encounters
  - Status: 🔴 Not Implemented
  - **Priority Reason**: Useful for patient history tracking

- **[P1]** **Line 217**: `getActiveAdmissions(UUID doctorId)`
  - TODO: Implement logic lấy bệnh nhân hiện đang nhập viện dưới quyền bác sĩ
  - Cần kết hợp với module admissions
  - Status: 🔴 Not Implemented
  - **Priority Reason**: Important for inpatient care management

---

## Staff Service (StaffServiceImpl.java)

### 1. Staff Management
- **[P2]** **Line 97-98**: `removeStaff(UUID staffId)`
  - TODO: Kiểm tra xem staff có đang có doctor record không
  - Nếu có thì không cho phép xóa
  - Status: 🟡 Partial Implementation
  - **Priority Reason**: Data integrity validation

### 2. Ratings Management
- **[P3]** **Line 126**: `getRatings(UUID staffId)`
  - TODO: Implement logic lấy đánh giá nhân viên
  - Cần kết hợp với module ratings
  - Status: 🔴 Not Implemented
  - **Priority Reason**: Nice-to-have for staff performance tracking

- **[P3]** **Line 134**: `getAverageRating(UUID staffId)`
  - TODO: Implement logic tính điểm đánh giá trung bình
  - Cần kết hợp với module ratings
  - Status: 🔴 Not Implemented
  - **Priority Reason**: Analytics feature, not critical

### 3. Shift Management
- **[P1]** **Line 142**: `getShiftAssignments(UUID staffId)`
  - TODO: Implement logic lấy phân công ca làm việc
  - Cần kết hợp với module scheduling
  - Status: 🔴 Not Implemented
  - **Priority Reason**: Important for staff scheduling

- **[P1]** **Line 150**: `getUpcomingShifts(UUID staffId)`
  - TODO: Implement logic lấy các ca làm việc sắp tới
  - Cần kết hợp với module scheduling
  - Status: 🔴 Not Implemented
  - **Priority Reason**: Essential for workforce management

---

## Prescription & Prescription Details

### Current Status
✅ **No TODOs Found** - All prescription and prescription detail functionalities have been implemented.

**Implemented Features:**
- ✅ Create prescription
- ✅ Add/remove drug items
- ✅ Finalize prescription
- ✅ Dispense medication
- ✅ Calculate totals
- ✅ Print prescription
- ✅ Soft delete for prescriptions and prescription details
- ✅ Status transitions (DRAFT → FINAL → DISPENSED)

---

---

## Invoice Service (InvoiceServiceImpl.java)

### 1. Void Invoice Enhancement
- **[P2]** **Line 125**: `voidInvoice(UUID invoiceId, String reason)`
  - TODO: Lưu reason vào notes hoặc field riêng
  - Currently reason parameter is not saved
  - Status: 🟡 Partial Implementation
  - **Priority Reason**: Audit trail improvement

### 2. PDF Generation
- **[P1]** **Line 178**: `generatePDF(UUID invoiceId)`
  - TODO: Implement PDF generation logic
  - Need to use PDF library (e.g., iText, Apache PDFBox)
  - Status: 🔴 Not Implemented
  - **Priority Reason**: Important for billing documentation

### 3. Email/Notification
- **[P2]** **Line 185**: `sendToPatient(UUID invoiceId)`
  - TODO: Implement email/notification sending logic
  - Need to integrate email service (e.g., Spring Mail, SendGrid)
  - Status: 🔴 Not Implemented
  - **Priority Reason**: Enhances patient communication

---

## Shift Service (ShiftServiceImpl.java)

### 1. Room Assignment
- **[P2]** **Line 61**: `createShift(ShiftRequestDto shiftRequestDto)`
  - TODO: Set default_room nếu có (cần RoomRepository)
  - Status: 🔴 Not Implemented
  - **Priority Reason**: Optional enhancement for shift creation

### 2. Shift Deletion Safety
- **[P1]** **Line 119**: `deleteShift(UUID shiftId)`
  - TODO: Kiểm tra xem có shift assignment nào không
  - Nếu có thì không cho phép xóa
  - Status: 🟡 Partial Implementation
  - **Priority Reason**: Prevents data integrity issues

### 3. Assigned Doctors List
- **[P1]** **Line 132**: `listAssignedDoctors(UUID shiftId, LocalDate date)`
  - TODO: Implement logic liệt kê bác sĩ được phân công
  - Cần query từ ShiftAssignment với shiftId và date
  - Status: 🔴 Not Implemented
  - **Priority Reason**: Essential for shift management visibility

### 4. Available Slots Filtering
- **[P0]** **Line 157**: `getAvailableSlots(UUID shiftId)`
  - TODO: Filter out booked slots from appointments
  - Currently returns all time slots without checking bookings
  - Status: 🟡 Partial Implementation
  - **Priority Reason**: Critical for appointment booking - shows incorrect availability

### 5. Slot Booking Check
- **[P0]** **Line 192**: `isSlotAvailable(UUID shiftId, LocalDateTime dateTime)`
  - TODO: Kiểm tra xem slot có bị book chưa
  - Need to query appointments for this time slot
  - Status: 🟡 Partial Implementation
  - **Priority Reason**: Critical - prevents double booking

### 6. Doctor Assignment
- **[P0]** **Line 200**: `assignDoctor(UUID shiftId, UUID doctorId, LocalDate date)`
  - TODO: Implement logic phân công bác sĩ vào ca làm việc
  - Cần tạo ShiftAssignment
  - Status: 🔴 Not Implemented
  - **Priority Reason**: Core scheduling functionality

---

## ShiftAssignment Service (ShiftAssignmentServiceImpl.java)

### 1. Room Assignment on Create
- **[P1]** **Line 75**: `createAssignment(ShiftAssignmentRequestDto shiftAssignmentRequestDto)`
  - TODO: Set room từ roomId
  - Need RoomRepository to fetch and set room
  - Status: 🔴 Not Implemented
  - **Priority Reason**: Important for complete shift assignment setup

### 2. Room Assignment Update
- **[P2]** **Line 126**: `assignRoom(UUID assignmentId, UUID roomId)`
  - TODO: Tìm room và set cho assignment
  - Cần RoomRepository
  - Status: 🔴 Not Implemented
  - **Priority Reason**: Allows flexibility in room assignment

### 3. Get Related Appointments
- **[P2]** **Line 187**: `getAppointments(UUID assignmentId)`
  - TODO: Implement logic lấy các cuộc hẹn trong thời gian phân công này
  - Cần kết hợp với module appointments
  - Status: 🔴 Not Implemented
  - **Priority Reason**: Useful for workload visibility

---

## WaitlistEntry Service (WaitlistEntryServiceImpl.java)

### 1. Accurate Wait Time Calculation
- **[P3]** **Line 235**: `estimateWaitTime(UUID entryId)`
  - TODO: Có thể tính toán chính xác hơn dựa trên lịch sử
  - Currently uses fixed 15 minutes per patient
  - Should use historical data or doctor performance metrics
  - Status: 🟡 Partial Implementation
  - **Priority Reason**: Optimization feature, current estimation is acceptable

---

## Appointment Service (AppointmentServiceImpl.java)

### 1. Encounter Creation
- **[P0]** **Line 239**: `createEncounter(UUID appointmentId)`
  - TODO: Implement logic tạo bệnh án từ cuộc hẹn
  - Cần kết hợp với module encounters
  - Status: 🔴 Not Implemented
  - **Priority Reason**: Critical bridge between appointment and medical workflow

### 2. Reminder Sending
- **[P2]** **Line 247**: `sendReminder(UUID appointmentId)`
  - TODO: Implement logic gửi nhắc nhở cuộc hẹn
  - Có thể gửi email hoặc SMS
  - Status: 🔴 Not Implemented
  - **Priority Reason**: Reduces no-shows but not blocking core functionality

---

## Encounter Service (EncounterServiceImpl.java)

### 1. Invoice Creation
- **[P1]** **Line 146**: `createInvoice(UUID encounterId)`
  - TODO: Implement với module Invoice (Billing)
  - Return type: Object → should be InvoiceDto
  - Status: 🔴 Not Implemented
  - **Priority Reason**: Essential for billing workflow

### 2. Prescription Creation
- **[P0]** **Line 153**: `createPrescription(UUID encounterId)`
  - TODO: Implement với module Prescription (Pharmacy)
  - Return type: Object → should be PrescriptionDto
  - Status: 🔴 Not Implemented
  - **Priority Reason**: Critical for treatment process

### 3. Patient Admission
- **[P1]** **Line 160**: `admitPatient(UUID encounterId, UUID roomId)`
  - TODO: Implement với module Admission
  - Return type: Object → should be AdmissionDto
  - Status: 🔴 Not Implemented
  - **Priority Reason**: Important for inpatient workflow

### 4. Get Prescriptions
- **[P2]** **Line 219**: `getPrescriptions(UUID encounterId)`
  - TODO: Implement với module Prescription (Pharmacy)
  - Return type: List<Object> → should be List<PrescriptionDto>
  - Status: 🔴 Not Implemented
  - **Priority Reason**: Useful for encounter summary

### 5. Get Invoices
- **[P2]** **Line 226**: `getInvoices(UUID encounterId)`
  - TODO: Implement với module Invoice (Billing)
  - Return type: List<Object> → should be List<InvoiceDto>
  - Status: 🔴 Not Implemented
  - **Priority Reason**: Useful for financial tracking

### 6. Get Admission
- **[P2]** **Line 233**: `getAdmission(UUID encounterId)`
  - TODO: Implement với module Admission
  - Return type: Optional<Object> → should be Optional<AdmissionDto>
  - Status: 🔴 Not Implemented
  - **Priority Reason**: Useful for patient status checking

**Note**: Interface definition also has TODOs for return types (Lines 18-20, 24-26)

---

## FollowUpPlan Service (FollowUpPlanServiceImpl.java)

### 1. Generate Appointments from RRULE
- **[P2]** **Line 166**: `generateAppointments(UUID planId)`
  - TODO: Implement logic để tạo các cuộc hẹn dựa trên RRULE
  - Cần parse RRULE (RFC 5545) và tạo appointments theo lịch
  - Requires RRULE parser library (e.g., ical4j)
  - Status: 🔴 Not Implemented
  - **Priority Reason**: Automates recurring appointments but can be manual initially

### 2. Calculate Next Due Date
- **[P3]** **Line 181**: `getNextDueDate(UUID planId)`
  - TODO: Parse RRULE để tính ngày tiếp theo
  - Currently returns first_due_at instead of calculating next occurrence
  - Status: 🟡 Partial Implementation
  - **Priority Reason**: Enhancement for recurring appointment tracking

### 3. Get Upcoming Appointments
- **[P2]** **Line 189**: `getUpcomingAppointments(UUID planId)`
  - TODO: Implement để lấy các appointments đã tạo từ plan này
  - Need to query appointments linked to this follow-up plan
  - Status: 🔴 Not Implemented
  - **Priority Reason**: Useful for follow-up plan tracking

---

## Admissions Module

### Current Status
✅ **No TODOs Found** - All admission functionalities have been implemented.

**Implemented Features:**
- ✅ Admit patient
- ✅ Discharge patient
- ✅ Transfer room
- ✅ Update admission
- ✅ Get admissions by patient/room/status
- ✅ Soft delete

---

## Summary Statistics

- **Total TODOs**: 39
- **Doctor Service**: 10 TODOs
- **Staff Service**: 5 TODOs
- **Invoice Service**: 3 TODOs
- **Shift Service**: 6 TODOs
- **ShiftAssignment Service**: 3 TODOs
- **WaitlistEntry Service**: 1 TODO
- **Appointment Service**: 2 TODOs
- **Encounter Service**: 6 TODOs
- **FollowUpPlan Service**: 3 TODOs
- **Prescription/Details**: 0 TODOs
- **Admissions**: 0 TODOs

### Priority Breakdown by Level
- **P0 (Critical)**: 7 TODOs
  - Doctor availability checking
  - Doctor shift schedule
  - Shift slot availability filtering
  - Slot booking validation
  - Doctor assignment to shifts
  - Appointment to encounter creation
  - Encounter prescription creation
  
- **P1 (High)**: 14 TODOs
  - Doctor appointments management (2)
  - Doctor diagnosis recording
  - Doctor medication prescribing
  - Doctor active admissions
  - Staff shift management (2)
  - Shift deletion safety
  - Shift assigned doctors list
  - ShiftAssignment room creation
  - Invoice PDF generation
  - Encounter invoice creation
  - Encounter patient admission

- **P2 (Medium)**: 14 TODOs
  - Doctor encounters retrieval
  - Doctor follow-up plan creation
  - Doctor patients list
  - Staff removal validation
  - Invoice void reason saving
  - Invoice patient notification
  - Shift default room
  - ShiftAssignment room update
  - ShiftAssignment appointments
  - Appointment reminders
  - Encounter data retrieval (3)
  - FollowUpPlan appointment generation
  - FollowUpPlan upcoming appointments

- **P3 (Low)**: 4 TODOs
  - Staff ratings retrieval (2)
  - WaitlistEntry wait time optimization
  - FollowUpPlan next due date calculation

### Implementation Status
- 🔴 **Not Implemented**: 28 TODOs
- 🟡 **Partial Implementation**: 11 TODOs
- ✅ **Complete**: Prescription, Admissions modules

### Module Dependencies
The TODOs require integration with:
- `appointments` module (6 TODOs)
- `encounters` module (5 TODOs)
- `scheduling` module (7 TODOs)
- `ratings` module (2 TODOs)
- `admissions` module (3 TODOs)
- `pharmacy` module (3 TODOs)
- `billing/invoices` module (3 TODOs)
- `rooms` module (3 TODOs)
- **External libraries needed**:
  - PDF generation library (1 TODO)
  - Email/SMS service (2 TODOs)
  - RRULE parser (RFC 5545) (2 TODOs)

---

## Recommended Implementation Order (By` Priority)

### 🔴 Sprint 1: Critical Path (P0) - 7 TODOs
**Goal**: Enable core appointment booking and medical workflow
1. `Doctor.checkAvailability()` - Check doctor availability [P0]
2. `Doctor.getShiftSchedule()` - Get shift schedule [P0]
3. `Shift.getAvailableSlots()` - Filter booked slo`ts [P0]
4. `Shift.isSlotAvailable()` - Check slot availability [P0]
5. `Shift.assignDoctor()` - Assign doctor to shift [P0]
6. `Appointment.createEncounter()` - Create encounter from appointment [P0]
7. `Encounter.createPrescription()` - Create prescription from encounter [P0]

**Estimated Effort**: 2-3 weeks  
**Impact**: Unblocks appointment booking and basic medical workflow

---

### 🟠 Sprint 2: High Priority Features (P1) - 14 TODOs
**Goal**: Complete essential doctor and scheduling workflows

#### Doctor Workflows (6 TODOs)
8. `Doctor.getAppointments()` - Get doctor's appointments [P1]
9. `Doctor.getUpcomingAppointments()` - Get upcoming appointments [P1]
10. `Doctor.recordDiagnosis()` - Record diagnosis [P1]
11. `Doctor.prescribeMedication()` - Create prescription [P1]
12. `Doctor.getActiveAdmissions()` - Get active admissions [P1]

#### Scheduling & Room Management (4 TODOs)
13. `Shift.listAssignedDoctors()` - List doctors assigned to shift [P1]
14. `Shift.deleteShift()` - Check assignments before deletion [P1]
15. `ShiftAssignment.createAssignment()` - Set room on create [P1]
16. `Staff.getShiftAssignments()` - Get shift assignments [P1]

#### Billing & Admission (4 TODOs)
17. `Staff.getUpcomingShifts()` - Get upcoming shifts [P1]
18. `Invoice.generatePDF()` - Generate invoice PDF [P1] ...
19. `Encounter.createInvoice()` - Create invoice from encounter [P1]
20. `Encounter.admitPatient()` - Admit patient from encounter [P1]

**Estimated Effort**: 3-4 weeks  
**Impact**: Completes core clinical and operational features

---

### 🟡 Sprint 3: Medium Priority Enhancements (P2) - 14 TODOs
**Goal**: Add reporting, communication, and workflow improvements

#### Reporting & Analytics (5 TODOs)
21. `Doctor.getEncounters()` - Get encounters by date range [P2]
22. `Doctor.getPatients()` - Get all patients [P2]
23. `Encounter.getPrescriptions()` - Get prescriptions from encounter [P2]
24. `Encounter.getInvoices()` - Get invoices from encounter [P2]
25. `Encounter.getAdmission()` - Get admission from encounter [P2]

#### Follow-Up & Communication (4 TODOs)
26. `Doctor.createFollowUpPlan()` - Create follow-up plan [P2]
27. `FollowUpPlan.generateAppointments()` - Generate from RRULE [P2]
28. `FollowUpPlan.getUpcomingAppointments()` - Get linked appointments [P2]
29. `Appointment.sendReminder()` - Send appointment reminder [P2] ...

#### System Improvements (5 TODOs)
30. `Staff.removeStaff()` - Complete validation [P2]
31. `Invoice.voidInvoice()` - Save void reason [P2]
32. `Invoice.sendToPatient()` - Send invoice notification [P2] ...
33. `Shift.createShift()` - Set default room [P2]
34. `ShiftAssignment.assignRoom()` - Assign room update [P2]
35. `ShiftAssignment.getAppointments()` - Get related appointments [P2]

**Estimated Effort**: 3-4 weeks  
**Impact**: Improves user experience and operational efficiency

---

### 🟢 Sprint 4: Low Priority Polish (P3) - 4 TODOs
**Goal**: Optimization and analytics features

36. `Staff.getRatings()` - Get staff ratings [P3]
37. `Staff.getAverageRating()` - Calculate average rating [P3]
38. `WaitlistEntry.estimateWaitTime()` - Improve calculation [P3]
39. `FollowUpPlan.getNextDueDate()` - Calculate from RRULE [P3]

**Estimated Effort**: 1-2 weeks  
**Impact**: Nice-to-have features for analytics and optimization

---

## Sprint Dependencies

### External Libraries Required
- **Sprint 1**: None
- **Sprint 2**: PDF library (iText/Apache PDFBox) for invoice generation
- **Sprint 3**: Email service (Spring Mail/SendGrid), RRULE parser (ical4j)
- **Sprint 4**: None

### Module Integration Points
- **appointments** ↔ **encounters** ↔ **pharmacy** (Sprint 1 critical path)
- **scheduling** ↔ **shifts** ↔ **doctors** (Sprint 1-2)
- **encounters** ↔ **billing** ↔ **admissions** (Sprint 2)
- **follow-up plans** ↔ **appointments** (Sprint 3)

---

**Last Updated**: December 17, 2025
