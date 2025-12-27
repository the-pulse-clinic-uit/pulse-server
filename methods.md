Dưới đây là danh sách chi tiết các phương thức (methods) của từng lớp (class) trong hệ thống Quản lý Bệnh viện được trích xuất từ tài liệu phân tích, định dạng chuẩn Markdown (`.md`).

Bạn có thể sao chép nội dung bên dưới và lưu thành file `Hospital_System_Methods.md`.

***

# Danh sách Phương thức theo Class (Hospital Management System)

[cite_start]Tài liệu tham khảo: *Analysis document of Hệ thống Quản lý Bệnh viện - Version 1.1*[cite: 3, 4, 9, 15].

## 1. Lớp Roles (Vai trò)
**Mô tả:** Quản lý các vai trò trong hệ thống (Admin, Doctor, Staff, Patient).

| Tên phương thức | Loại (Visibility) | Kiểu trả về | Ý nghĩa/Ghi chú |
| :--- | :--- | :--- | :--- |
| `createRole()` | public | boolean | [cite_start]Tạo vai trò mới trong hệ thống [cite: 96] |
| `updateRole()` | public | boolean | [cite_start]Cập nhật thông tin vai trò [cite: 96] |
| `deleteRole()` | public | boolean | [cite_start]Xóa vai trò khỏi hệ thống [cite: 96] |
| `findAll()` | public | List | [cite_start]Liệt kê tất cả vai trò có sẵn [cite: 96] |
| `findByName(String)` | public | Optional | [cite_start]Tìm vai trò theo tên [cite: 99] |
| `assignToUser(User)` | public | boolean | [cite_start]Gán vai trò cho người dùng [cite: 99] |

---

## 2. Lớp Users (Người dùng)
**Mô tả:** Lớp cơ sở chứa thông tin chung và xác thực cho mọi người dùng.

| Tên phương thức | Loại (Visibility) | Kiểu trả về | Ý nghĩa/Ghi chú |
| :--- | :--- | :--- | :--- |
| `registerAccount(UserRequestDto)` | public | User | [cite_start]Đăng ký tài khoản người dùng mới [cite: 106] |
| `authenticate(email, password)` | public | User | [cite_start]Xác thực đăng nhập người dùng [cite: 106] |
| `updatePersonalInfo(UserRequestDto)` | public | boolean | [cite_start]Cập nhật thông tin cá nhân [cite: 109] |
| `changePassword(oldPass, newPass)` | public | boolean | [cite_start]Thay đổi mật khẩu tài khoản [cite: 109] |
| `resetPassword(email)` | public | boolean | [cite_start]Gửi email đặt lại mật khẩu [cite: 109] |
| `activateAccount()` | public | boolean | [cite_start]Kích hoạt tài khoản người dùng [cite: 109] |
| `deactivateAccount()` | public | boolean | [cite_start]Vô hiệu hóa tài khoản người dùng [cite: 109] |
| `updateAvatar(String)` | public | boolean | [cite_start]Cập nhật ảnh đại diện [cite: 109] |
| `verifyEmail()` | public | boolean | [cite_start]Xác minh địa chỉ email [cite: 109] |
| `hasRole(String)` | public | boolean | [cite_start]Kiểm tra người dùng có vai trò cụ thể không [cite: 109] |
| `getFullDetails()` | public | UserDto | [cite_start]Lấy hồ sơ người dùng đầy đủ [cite: 109] |

---

## 3. Lớp Patients (Bệnh nhân)
**Mô tả:** Quản lý thông tin y tế và lịch sử của bệnh nhân.

| Tên phương thức | Loại (Visibility) | Kiểu trả về | Ý nghĩa/Ghi chú |
| :--- | :--- | :--- | :--- |
| `registerPatient(PatientRequestDto)` | public | Patient | [cite_start]Tạo hồ sơ bệnh nhân mới [cite: 115] |
| `updateMedicalInfo(PatientRequestDto)` | public | boolean | [cite_start]Cập nhật bảo hiểm, nhóm máu, dị ứng [cite: 115] |
| `getMedicalHistory()` | public | List | [cite_start]Lấy lịch sử bệnh án của bệnh nhân [cite: 115] |
| `findByCitizenId(String)` | public static | Optional | [cite_start]Tìm bệnh nhân theo CMND/CCCD [cite: 115] |
| `getActiveAppointments()` | public | List | [cite_start]Lấy các cuộc hẹn sắp tới [cite: 115] |
| `getPastAppointments()` | public | List | [cite_start]Lấy các cuộc hẹn đã hoàn thành [cite: 118] |
| `getInvoices()` | public | List | [cite_start]Lấy các hóa đơn của bệnh nhân [cite: 118] |
| `getUnpaidInvoices()` | public | List | [cite_start]Lấy các hóa đơn chưa thanh toán [cite: 118] |
| `getPrescriptions()` | public | List | [cite_start]Lấy tất cả đơn thuốc [cite: 118] |
| `getAdmissions()` | public | List | [cite_start]Lấy hồ sơ nhập viện [cite: 118] |
| `getCurrentAdmission()` | public | Optional | [cite_start]Lấy hồ sơ nhập viện đang hoạt động (nếu có) [cite: 118] |
| `addAllergy(String)` | public | boolean | [cite_start]Thêm dị ứng mới [cite: 118] |
| `removeAllergy(String)` | public | boolean | [cite_start]Xóa dị ứng khỏi danh sách [cite: 118] |

---

## 4. Lớp Departments (Khoa/Phòng ban)
**Mô tả:** Quản lý các khoa, phòng ban và nhân sự trực thuộc.

| Tên phương thức | Loại (Visibility) | Kiểu trả về | Ý nghĩa/Ghi chú |
| :--- | :--- | :--- | :--- |
| `createDepartment(DepartmentRequestDto)` | public | Department | [cite_start]Tạo khoa mới [cite: 125] |
| `updateDepartment(DepartmentRequestDto)` | public | boolean | [cite_start]Cập nhật thông tin khoa [cite: 125] |
| `deleteDepartment()` | public | boolean | [cite_start]Xóa khoa nếu không có nhân viên được gán [cite: 125] |
| `listStaff()` | public | List | [cite_start]Liệt kê tất cả nhân viên trong khoa [cite: 125] |
| `listDoctors()` | public | List | [cite_start]Liệt kê tất cả bác sĩ trong khoa [cite: 125] |
| `listRooms()` | public | List | [cite_start]Liệt kê tất cả phòng trong khoa [cite: 125] |
| `getStaffCount()` | public | Integer | [cite_start]Đếm số lượng nhân viên [cite: 125] |
| `getDoctorCount()` | public | Integer | [cite_start]Đếm số lượng bác sĩ [cite: 125] |
| `getRoomCount()` | public | Integer | [cite_start]Đếm số lượng phòng [cite: 125] |
| `assignStaff(Staff)` | public | boolean | [cite_start]Gán nhân viên vào khoa [cite: 128] |
| `removeStaff(Staff)` | public | boolean | [cite_start]Xóa nhân viên khỏi khoa [cite: 128] |

---

## 5. Lớp Staffs (Nhân viên)
**Mô tả:** Lớp quản lý nhân sự chung của bệnh viện.

| Tên phương thức | Loại (Visibility) | Kiểu trả về | Ý nghĩa/Ghi chú |
| :--- | :--- | :--- | :--- |
| `createStaff(StaffRequestDto)` | public | Staff | [cite_start]Tạo hồ sơ nhân viên mới [cite: 135] |
| `updateInfo(StaffRequestDto)` | public | boolean | [cite_start]Cập nhật thông tin nhân viên [cite: 135] |
| `removeStaff()` | public | boolean | [cite_start]Xóa nhân viên khỏi hệ thống [cite: 135] |
| `getUser()` | public | User | [cite_start]Lấy thông tin người dùng liên kết [cite: 135] |
| `getRatings()` | public | List | [cite_start]Lấy đánh giá nhân viên [cite: 135] |
| `getAverageRating()` | public | Float | [cite_start]Tính điểm đánh giá trung bình [cite: 135] |
| `getShiftAssignments()` | public | List | [cite_start]Lấy phân công ca làm việc [cite: 135] |
| `getUpcomingShifts()` | public | List | [cite_start]Lấy các ca làm việc sắp tới [cite: 135] |
| `updatePosition(Position)` | public | boolean | [cite_start]Cập nhật chức vị nhân viên [cite: 135] |

---

## 6. Lớp Doctors (Bác sĩ)
**Mô tả:** Kế thừa từ Staff, quản lý chuyên môn và khám chữa bệnh.

| Tên phương thức | Loại (Visibility) | Kiểu trả về | Ý nghĩa/Ghi chú |
| :--- | :--- | :--- | :--- |
| `createDoctor(DoctorRequestDto)` | public | Doctor | [cite_start]Tạo hồ sơ bác sĩ mới [cite: 145] |
| `updateSpecialization(Department)` | public | boolean | [cite_start]Cập nhật chuyên khoa của bác sĩ [cite: 145] |
| `verifyLicense()` | public | boolean | [cite_start]Xác minh giấy phép hành nghề y [cite: 145] |
| `checkLicenseValidity()` | public | boolean | [cite_start]Kiểm tra giấy phép có hợp lệ hay không [cite: 145] |
| `getAppointments(LocalDate)` | public | List | [cite_start]Lấy các cuộc hẹn cho ngày cụ thể [cite: 145] |
| `getUpcomingAppointments()` | public | List | [cite_start]Lấy các cuộc hẹn sắp tới [cite: 145] |
| `getEncounters(LocalDate, LocalDate)` | public | List | [cite_start]Lấy các bệnh án trong khoảng thời gian [cite: 145] |
| `prescribeMedication(Encounter)` | public | Prescription | [cite_start]Tạo đơn thuốc cho bệnh án [cite: 145] |
| `recordDiagnosis(Encounter, String)` | public | boolean | [cite_start]Ghi lại chẩn đoán cho bệnh án [cite: 145] |
| `createFollowUpPlan(Encounter)` | public | FollowUpPlan | [cite_start]Tạo kế hoạch tái khám [cite: 145] |
| `getShiftSchedule(LocalDate)` | public | List | [cite_start]Lấy lịch ca làm việc cho ngày cụ thể [cite: 145] |
| `checkAvailability(LocalDateTime)` | public | boolean | [cite_start]Kiểm tra bác sĩ có sẵn sàng hay không [cite: 145] |
| `getDepartment()` | public | Department | [cite_start]Lấy khoa của bác sĩ [cite: 145] |
| `getPatients()` | public | List | [cite_start]Lấy tất cả bệnh nhân đã được bác sĩ khám [cite: 145] |
| `getActiveAdmissions()` | public | List | [cite_start]Lấy bệnh nhân hiện đang nhập viện dưới quyền bác sĩ [cite: 145] |

---

## 7. Lớp Shifts (Ca trực)
**Mô tả:** Định nghĩa các ca làm việc (Sáng, Chiều, Cấp cứu...).

| Tên phương thức | Loại (Visibility) | Kiểu trả về | Ý nghĩa/Ghi chú |
| :--- | :--- | :--- | :--- |
| `createShift(ShiftRequestDto)` | public | Shift | [cite_start]Tạo ca làm việc mới [cite: 155] |
| `updateShift(ShiftRequestDto)` | public | boolean | [cite_start]Cập nhật thời gian hoặc cài đặt ca [cite: 155] |
| `deleteShift()` | public | boolean | [cite_start]Xóa ca làm việc [cite: 155] |
| `listAssignedDoctors(LocalDate)` | public | List | [cite_start]Liệt kê bác sĩ được phân công vào ca trong ngày [cite: 155] |
| `getAvailableSlots(LocalDate)` | public | List | [cite_start]Lấy các slot hẹn còn trống [cite: 155] |
| `calculateSlots()` | public | Integer | [cite_start]Tính số lượng slot trong ca làm việc [cite: 155] |
| `isTimeSlotAvailable(LocalDateTime)` | public | boolean | [cite_start]Kiểm tra thời điểm cụ thể có khả dụng hay không [cite: 155] |
| `assignDoctor(Doctor, LocalDate)` | public | ShiftAssignment | [cite_start]Phân công bác sĩ vào ca làm việc trong ngày [cite: 155] |
| `getCapacity()` | public | Integer | [cite_start]Lấy tổng sức chứa của ca làm việc [cite: 155] |
| `getDuration()` | public | Duration | [cite_start]Lấy thời lượng ca làm việc [cite: 155] |

---

## 8. Lớp ShiftAssignments (Phân công ca trực)
**Mô tả:** Liên kết cụ thể giữa Bác sĩ và Ca trực.

| Tên phương thức | Loại (Visibility) | Kiểu trả về | Ý nghĩa/Ghi chú |
| :--- | :--- | :--- | :--- |
| `assignDoctor(Doctor, Shift, LocalDate)` | public | ShiftAssignment | [cite_start]Phân công bác sĩ vào ca làm việc [cite: 164] |
| `cancelAssignment()` | public | boolean | [cite_start]Hủy phân công ca làm việc [cite: 167] |
| `updateStatus(ShiftAssignmentStatus)` | public | boolean | [cite_start]Cập nhật trạng thái phân công [cite: 167] |
| `updateRoom(Room)` | public | boolean | [cite_start]Thay đổi phòng được phân công [cite: 167] |
| `updateRole(ShiftAssignmentRole)` | public | boolean | [cite_start]Thay đổi vai trò trong ca làm việc [cite: 167] |
| `findByDate(LocalDate)` | public static | List | [cite_start]Lấy các phân công theo ngày [cite: 167] |
| `findByDoctor(Doctor, LocalDate, LocalDate)`| public static | List | [cite_start]Lấy phân công của bác sĩ trong khoảng thời gian [cite: 167] |
| `findByShift(Shift, LocalDate)` | public static | List | [cite_start]Lấy tất cả phân công cho ca làm việc trong ngày [cite: 167] |
| `checkConflicts(Doctor, LocalDate, Shift)` | public static | boolean | [cite_start]Kiểm tra xung đột lịch trình [cite: 167] |
| `getAppointments()` | public | List | [cite_start]Lấy các cuộc hẹn trong thời gian phân công này [cite: 167] |

---

## 9. Lớp Appointments (Cuộc hẹn)
**Mô tả:** Quản lý việc đặt lịch khám bệnh.

| Tên phương thức | Loại (Visibility) | Kiểu trả về | Ý nghĩa/Ghi chú |
| :--- | :--- | :--- | :--- |
| `scheduleAppointment(Dto)` | public | Appointment | [cite_start]Đặt lịch hẹn mới [cite: 177] |
| `updateStatus(AppointmentStatus)` | public | boolean | [cite_start]Cập nhật trạng thái cuộc hẹn [cite: 177] |
| `cancelAppointment(String reason)` | public | boolean | [cite_start]Hủy cuộc hẹn [cite: 177] |
| `rescheduleAppointment(Time)` | public | boolean | [cite_start]Thay đổi thời gian cuộc hẹn [cite: 177] |
| `confirmAppointment()` | public | boolean | [cite_start]Xác nhận cuộc hẹn đang chờ [cite: 177] |
| `checkIn()` | public | boolean | [cite_start]Đánh dấu bệnh nhân đã check-in [cite: 177] |
| `markAsDone()` | public | boolean | [cite_start]Hoàn thành cuộc hẹn [cite: 177] |
| `markAsNoShow()` | public | boolean | [cite_start]Đánh dấu bệnh nhân không đến [cite: 177] |
| `checkConflicts()` | public | boolean | [cite_start]Kiểm tra xung đột lịch trình [cite: 177] |
| `validateTimeSlot()` | public | boolean | [cite_start]Xác thực slot thời gian có khả dụng hay không [cite: 177] |
| `createEncounter()` | public | Encounter | [cite_start]Tạo bệnh án từ cuộc hẹn [cite: 177] |
| `sendReminder()` | public | boolean | [cite_start]Gửi nhắc nhở cuộc hẹn [cite: 177] |
| `canCancel()` | public | boolean | [cite_start]Kiểm tra cuộc hẹn có thể hủy hay không [cite: 177] |
| `canReschedule()` | public | boolean | [cite_start]Kiểm tra cuộc hẹn có thể dời lịch hay không [cite: 177] |

---

## 10. Lớp WaitlistEntry (Danh sách chờ)
**Mô tả:** Quản lý hàng đợi bệnh nhân tại phòng khám.

| Tên phương thức | Loại (Visibility) | Kiểu trả về | Ý nghĩa/Ghi chú |
| :--- | :--- | :--- | :--- |
| `addToWaitlist(Dto)` | public | WaitlistEntry | [cite_start]Thêm bệnh nhân vào danh sách chờ [cite: 185] |
| `callNext(Doctor, LocalDate)` | public static | Optional | [cite_start]Gọi bệnh nhân tiếp theo trong hàng đợi [cite: 185] |
| `changePriority(Priority)` | public | boolean | [cite_start]Cập nhật mức độ ưu tiên [cite: 185] |
| `updateStatus(Status)` | public | boolean | [cite_start]Cập nhật trạng thái [cite: 185] |
| `markAsCalled()` | public | boolean | [cite_start]Đánh dấu đã gọi [cite: 185] |
| `markAsServed()` | public | boolean | [cite_start]Đánh dấu đã phục vụ [cite: 185] |
| `markAsNoShow()` | public | boolean | [cite_start]Đánh dấu không đến [cite: 185] |
| `cancelEntry()` | public | boolean | [cite_start]Hủy mục danh sách chờ [cite: 185] |
| `getWaitingCount(Doctor, LocalDate)` | public static | Integer | [cite_start]Đếm số bệnh nhân đang chờ [cite: 188] |
| `getEstimatedWaitTime()` | public | Duration | [cite_start]Ước tính thời gian chờ [cite: 188] |
| `getPosition()` | public | Integer | [cite_start]Lấy vị trí trong hàng đợi [cite: 188] |
| `generateTicketNumber(...)` | private static | String | [cite_start]Tạo số thứ tự duy nhất [cite: 188] |

---

## 11. Lớp Encounters (Buổi khám/Tiếp nhận)
**Mô tả:** Ghi nhận thông tin chi tiết của một buổi khám bệnh thực tế.

| Tên phương thức | Loại (Visibility) | Kiểu trả về | Ý nghĩa/Ghi chú |
| :--- | :--- | :--- | :--- |
| `startEncounter(Dto)` | public | Encounter | [cite_start]Bắt đầu khám bệnh mới [cite: 195] |
| `recordDiagnosis(String)` | public | boolean | [cite_start]Ghi lại chẩn đoán y khoa [cite: 195] |
| `addNotes(String)` | public | boolean | [cite_start]Thêm ghi chú lâm sàng [cite: 195] |
| `endEncounter()` | public | boolean | [cite_start]Kết thúc khám bệnh [cite: 195] |
| `createInvoice()` | public | Invoice | [cite_start]Tạo hóa đơn cho khám bệnh [cite: 195] |
| `createPrescription()` | public | Prescription | [cite_start]Tạo đơn thuốc [cite: 198] |
| `admitPatient(Room)` | public | Admission | [cite_start]Nhập viện bệnh nhân [cite: 198] |
| `createFollowUpPlan(...)` | public | FollowUpPlan | [cite_start]Tạo kế hoạch tái khám [cite: 198] |
| `getDuration()` | public | Duration | [cite_start]Lấy thời lượng khám bệnh [cite: 198] |
| `isComplete()` | public | boolean | [cite_start]Kiểm tra khám bệnh đã hoàn thành chưa [cite: 198] |
| `getPrescriptions()` | public | List | [cite_start]Lấy tất cả đơn thuốc [cite: 198] |
| `getInvoices()` | public | List | [cite_start]Lấy tất cả hóa đơn [cite: 198] |
| `getAdmission()` | public | Optional | [cite_start]Lấy hồ sơ nhập viện nếu có [cite: 198] |
| `generateSummary()` | public | String | [cite_start]Tạo tóm tắt bệnh án [cite: 198] |

---

## 12. Lớp Drugs (Thuốc)
**Mô tả:** Quản lý danh mục thuốc trong bệnh viện.

| Tên phương thức | Loại (Visibility) | Kiểu trả về | Ý nghĩa/Ghi chú |
| :--- | :--- | :--- | :--- |
| `addDrug(DrugRequestDto)` | public | Drug | [cite_start]Thêm thuốc mới vào danh mục [cite: 204] |
| `updateDrugInfo(Dto)` | public | boolean | [cite_start]Cập nhật thông tin thuốc [cite: 204] |
| `deleteDrug()` | public | boolean | [cite_start]Xóa thuốc khỏi danh mục [cite: 204] |
| `searchByName(String)` | public static | List | [cite_start]Tìm kiếm thuốc theo tên [cite: 204] |
| `updatePrice(BigDecimal)` | public | boolean | [cite_start]Cập nhật giá đơn vị [cite: 207] |
| `checkAvailability()` | public | boolean | [cite_start]Kiểm tra thuốc có sẵn hay không [cite: 207] |
| `getFormattedStrength()` | public | String | [cite_start]Lấy chuỗi hàm lượng đã định dạng [cite: 207] |
| `calculatePrice(Integer)` | public | BigDecimal | [cite_start]Tính tổng giá [cite: 207] |

---

## 13. Lớp Prescription (Đơn thuốc)
**Mô tả:** Quản lý đơn thuốc do bác sĩ kê.

| Tên phương thức | Loại (Visibility) | Kiểu trả về | Ý nghĩa/Ghi chú |
| :--- | :--- | :--- | :--- |
| `createPrescription(Encounter)` | public | Prescription | [cite_start]Tạo đơn thuốc mới [cite: 214] |
| `addDrugItem(...)` | public | PrescriptionDetail | [cite_start]Thêm thuốc vào đơn [cite: 214] |
| `removeDrugItem(Detail)` | public | boolean | [cite_start]Xóa thuốc khỏi đơn [cite: 214] |
| `finalizePrescription()` | public | boolean | [cite_start]Hoàn tất đơn thuốc (DRAFT -> FINAL) [cite: 214] |
| `dispenseMedication()` | public | boolean | [cite_start]Đánh dấu đã xuất thuốc [cite: 214] |
| `calculateTotal()` | public | BigDecimal | [cite_start]Tính tổng giá [cite: 214] |
| `updateTotal()` | private | void | [cite_start]Tính lại và cập nhật tổng [cite: 214] |
| `getDetails()` | public | List | [cite_start]Lấy tất cả mục thuốc [cite: 214] |
| `printPrescription()` | public | String | [cite_start]Tạo đơn thuốc có thể in [cite: 217] |
| `canModify()` | public | boolean | [cite_start]Kiểm tra đơn thuốc có thể sửa hay không [cite: 217] |
| `canDispense()` | public | boolean | [cite_start]Kiểm tra đơn thuốc có thể xuất hay không [cite: 217] |
| `isDraft()` | public | boolean | [cite_start]Kiểm tra có ở trạng thái nháp hay không [cite: 217] |
| `isFinal()` | public | boolean | [cite_start]Kiểm tra đã hoàn tất hay chưa [cite: 217] |
| `isDispensed()` | public | boolean | [cite_start]Kiểm tra đã xuất thuốc hay chưa [cite: 217] |

---

## 14. Lớp PrescriptionDetails (Chi tiết đơn thuốc)
**Mô tả:** Chi tiết từng loại thuốc trong một đơn thuốc.

| Tên phương thức | Loại (Visibility) | Kiểu trả về | Ý nghĩa/Ghi chú |
| :--- | :--- | :--- | :--- |
| `createDetail(Dto)` | public | PrescriptionDetail | [cite_start]Tạo mục thuốc trong đơn [cite: 227] |
| `updateDosage(...)` | public | boolean | [cite_start]Cập nhật thông tin liều lượng [cite: 227] |
| `updateQuantity(Integer)` | public | boolean | [cite_start]Cập nhật số lượng [cite: 227] |
| `calculateLineTotal()` | public | BigDecimal | [cite_start]Tính tổng giá mục [cite: 227] |
| `updateLineTotal()` | private | void | [cite_start]Tính lại và cập nhật tổng dòng [cite: 227] |
| `getFormattedDosage()` | public | String | [cite_start]Lấy chuỗi liều lượng đã định dạng [cite: 227] |
| `getFormattedInstructions()` | public | String | [cite_start]Lấy hướng dẫn đầy đủ [cite: 227] |

---

## 15. Lớp Room (Phòng bệnh)
**Mô tả:** Quản lý phòng và giường bệnh nội trú.

| Tên phương thức | Loại (Visibility) | Kiểu trả về | Ý nghĩa/Ghi chú |
| :--- | :--- | :--- | :--- |
| `createRoom(RoomRequestDto)` | public | Room | [cite_start]Tạo phòng mới [cite: 233] |
| `updateAvailability(Boolean)` | public | boolean | [cite_start]Cập nhật tình trạng sẵn sàng của phòng [cite: 233] |
| `checkAvailability()` | public | boolean | [cite_start]Kiểm tra phòng có sẵn sàng hay không [cite: 233] |
| `transferPatient(Admission, Room)`| public static | boolean | [cite_start]Chuyển bệnh nhân sang phòng khác [cite: 233] |
| `getOccupancy()` | public | Integer | [cite_start]Lấy số giường đang sử dụng [cite: 236] |
| `getAvailableBeds()` | public | Integer | [cite_start]Lấy số giường trống [cite: 236] |
| `getCurrentAdmissions()` | public | List | [cite_start]Lấy các hồ sơ nhập viện hiện tại trong phòng [cite: 236] |
| `markAsAvailable()` | public | boolean | [cite_start]Đánh dấu phòng sẵn sàng [cite: 236] |
| `markAsOccupied()` | public | boolean | [cite_start]Đánh dấu phòng đã được sử dụng [cite: 236] |
| `findAvailableRooms(Dept)` | public static | List | [cite_start]Tìm phòng trống trong khoa [cite: 236] |

---

## 16. Lớp Admission (Nhập viện)
**Mô tả:** Quản lý quy trình nhập viện, điều trị nội trú và xuất viện.

| Tên phương thức | Loại (Visibility) | Kiểu trả về | Ý nghĩa/Ghi chú |
| :--- | :--- | :--- | :--- |
| `admitPatient(Dto)` | public | Admission | [cite_start]Nhập viện bệnh nhân [cite: 242] |
| `transferRoom(Room)` | public | boolean | [cite_start]Chuyển bệnh nhân sang phòng khác [cite: 245] |
| `updateNotes(String)` | public | boolean | [cite_start]Cập nhật ghi chú nhập viện [cite: 245] |
| `updateStatus(AdmissionStatus)` | public | boolean | [cite_start]Cập nhật trạng thái nhập viện [cite: 245] |
| `dischargePatient()` | public | boolean | [cite_start]Xuất viện bệnh nhân [cite: 245] |
| `getDuration()` | public | Duration | [cite_start]Lấy thời lượng nhập viện [cite: 245] |
| `getPatient()` | public | Patient | [cite_start]Lấy bệnh nhân đang nhập viện [cite: 245] |
| `getDoctor()` | public | Doctor | [cite_start]Lấy bác sĩ giám sát [cite: 245] |
| `getRoom()` | public | Room | [cite_start]Lấy phòng được phân công [cite: 245] |
| `isOngoing()` | public | boolean | [cite_start]Kiểm tra nhập viện có đang hoạt động hay không [cite: 245] |
| `canTransfer()` | public | boolean | [cite_start]Kiểm tra bệnh nhân có thể chuyển phòng hay không [cite: 245] |
| `canDischarge()` | public | boolean | [cite_start]Kiểm tra bệnh nhân có thể xuất viện hay không [cite: 245] |

---

## 17. Lớp Invoice (Hóa đơn)
**Mô tả:** Quản lý thanh toán và hóa đơn.

| Tên phương thức | Loại (Visibility) | Kiểu trả về | Ý nghĩa/Ghi chú |
| :--- | :--- | :--- | :--- |
| `createInvoice(Encounter)` | public | Invoice | [cite_start]Tạo hóa đơn cho cuộc khám [cite: 254] |
| `recordPayment(BigDecimal)` | public | boolean | [cite_start]Ghi nhận số tiền thanh toán [cite: 254] |
| `updateStatus()` | private | void | [cite_start]Cập nhật trạng thái dựa trên thanh toán [cite: 254] |
| `voidInvoice(String reason)` | public | boolean | [cite_start]Hủy hóa đơn [cite: 254] |
| `calculateBalance()` | public | BigDecimal | [cite_start]Tính số dư còn lại [cite: 254] |
| `getBalance()` | public | BigDecimal | [cite_start]Lấy số dư hiện tại [cite: 254] |
| `isPaid()` | public | boolean | [cite_start]Kiểm tra đã thanh toán đủ chưa [cite: 254] |
| `isOverdue()` | public | boolean | [cite_start]Kiểm tra thanh toán có quá hạn không [cite: 254] |
| `generatePDF()` | public | byte[] | [cite_start]Tạo hóa đơn PDF [cite: 254] |
| `sendToPatient()` | public | boolean | [cite_start]Gửi hóa đơn cho bệnh nhân [cite: 254] |
| `addLineItem(String, BigDecimal)`| public | boolean | [cite_start]Thêm phí vào hóa đơn [cite: 254] |
| `applyDiscount(BigDecimal)` | public | boolean | [cite_start]Áp dụng giảm giá cho hóa đơn [cite: 254] |

---

## 18. Lớp FollowUpPlan (Kế hoạch tái khám)
**Mô tả:** Quản lý lịch trình tái khám định kỳ.

| Tên phương thức | Loại (Visibility) | Kiểu trả về | Ý nghĩa/Ghi chú |
| :--- | :--- | :--- | :--- |
| `createPlan(Dto)` | public | FollowUpPlan | [cite_start]Tạo kế hoạch tái khám [cite: 263] |
| `editPlan(Dto)` | public | boolean | [cite_start]Sửa chi tiết kế hoạch [cite: 263] |
| `pausePlan()` | public | boolean | [cite_start]Tạm dừng kế hoạch [cite: 263] |
| `resumePlan()` | public | boolean | [cite_start]Tiếp tục kế hoạch đã dừng [cite: 263] |
| `completePlan()` | public | boolean | [cite_start]Đánh dấu kế hoạch đã hoàn thành [cite: 263] |
| `generateAppointments()` | public | List | [cite_start]Tạo các cuộc hẹn từ kế hoạch [cite: 263] |
| `getNextDueDate()` | public | LocalDateTime| [cite_start]Lấy lịch tái khám tiếp theo [cite: 263] |
| `getUpcomingAppointments()` | public | List | [cite_start]Lấy các cuộc hẹn trong tương lai [cite: 263] |
| `isActive()` | public | boolean | [cite_start]Kiểm tra kế hoạch có đang hoạt động không [cite: 263] |
| `canModify()` | public | boolean | [cite_start]Kiểm tra kế hoạch có thể sửa đổi không [cite: 263] |

---

## 19. Lớp Notification (Thông báo)
**Mô tả:** Hệ thống thông báo tới người dùng (Email, SMS, App).

| Tên phương thức | Loại (Visibility) | Kiểu trả về | Ý nghĩa/Ghi chú |
| :--- | :--- | :--- | :--- |
| `createNotification()` | public | boolean | [cite_start]Tạo thông báo mới [cite: 272] |
| `sendNotification()` | public | boolean | [cite_start]Gửi thông báo qua kênh chỉ định [cite: 272] |
| `markAsRead()` | public | boolean | [cite_start]Đánh dấu thông báo là đã đọc [cite: 272] |
| `listByUser()` | public | List | [cite_start]Liệt kê thông báo của người dùng [cite: 272] |

---

## 20. Lớp StaffRating (Đánh giá nhân viên)
**Mô tả:** Quản lý đánh giá từ bệnh nhân/khách đối với nhân viên.

| Tên phương thức | Loại (Visibility) | Kiểu trả về | Ý nghĩa/Ghi chú |
| :--- | :--- | :--- | :--- |
| `addRating(Dto)` | public | StaffRating | [cite_start]Thêm đánh giá nhân viên mới [cite: 281] |
| `updateRating(Integer, String)` | public | boolean | [cite_start]Cập nhật điểm và nhận xét [cite: 281] |
| `deleteRating()` | public | boolean | [cite_start]Xóa đánh giá [cite: 281] |
| `calculateAverageRating(Staff)` | public static | Float | [cite_start]Tính điểm đánh giá trung bình của nhân viên [cite: 281] |
| `listRatingsByStaff(Staff)` | public static | List | [cite_start]Lấy tất cả đánh giá của nhân viên [cite: 281] |
| `listRatingsByPatient(Patient)` | public static | List | [cite_start]Lấy các đánh giá do bệnh nhân thực hiện [cite: 281] |
| `getRatingDistribution(Staff)` | public static | Map | [cite_start]Lấy phân bố điểm đánh giá (1-5 sao) [cite: 281] |
| `moderateRating()` | public | boolean | [cite_start]Gắn cờ đánh giá để kiểm duyệt [cite: 281] |
| `isAnonymous()` | public | boolean | [cite_start]Kiểm tra đánh giá có ẩn danh không [cite: 281] |