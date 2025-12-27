# Tài Liệu Controllers - Pulse Clinic API

## 1. DoctorController

| STT | Tên phương thức | Loại | Kiểu trả về | Ý nghĩa/Ghi chú |
|-----|----------------|------|-------------|-----------------|
| 1 | createDoctor | public | DoctorDto | Tạo mới bác sĩ (chỉ admin) |
| 2 | getAllDoctors | public | List | Lấy danh sách tất cả bác sĩ (chỉ admin) |
| 3 | getDoctorById | public | DoctorDto | Lấy thông tin bác sĩ theo ID |
| 4 | updateDoctor | public | DoctorDto | Cập nhật thông tin cơ bản của bác sĩ |
| 5 | updateSpecialization | public | Void | Cập nhật chuyên khoa của bác sĩ |
| 6 | getPatients | public | List | Lấy danh sách bệnh nhân của bác sĩ |
| 7 | getShiftSchedule | public | List | Lấy lịch làm việc của bác sĩ theo ngày |
| 8 | checkAvailability | public | Boolean | Kiểm tra tình trạng có mặt của bác sĩ tại thời điểm |

## 2. InvoiceController

| STT | Tên phương thức | Loại | Kiểu trả về | Ý nghĩa/Ghi chú |
|-----|----------------|------|-------------|-----------------|
| 1 | createInvoice | public | InvoiceDto | Tạo hóa đơn mới (admin/staff) |
| 2 | getInvoiceById | public | InvoiceDto | Lấy thông tin hóa đơn theo ID (admin/staff) |
| 3 | getBalance | public | BigDecimal | Lấy số dư còn lại của hóa đơn |
| 4 | getLineItems | public | List\> | Lấy danh sách các mục trong hóa đơn |
| 5 | addLineItem | public | Void | Thêm mục mới vào hóa đơn (admin/staff) |
| 6 | applyDiscount | public | Void | Áp dụng giảm giá cho hóa đơn (admin/staff) |
| 7 | voidInvoice | public | Void | Hủy hóa đơn với lý do (admin/staff) |
| 8 | createPayment | public | String | Tạo thanh toán cho hóa đơn (admin/staff) |
| 9 | recordPayment | public | Void | Ghi nhận thanh toán cho hóa đơn |

## 3. PatientController

| STT | Tên phương thức | Loại | Kiểu trả về | Ý nghĩa/Ghi chú |
|-----|----------------|------|-------------|-----------------|
| 1 | searchPatient | public | List | Tìm kiếm bệnh nhân (admin/staff) |
| 2 | registerPatient | public | PatientDto | Đăng ký bệnh nhân mới (staff/admin) |
| 3 | getPatientMe | public | PatientDto | Lấy thông tin bệnh nhân hiện tại (bản thân) |
| 4 | getPatients | public | List | Lấy danh sách tất cả bệnh nhân (admin/staff) |
| 5 | getPatientById | public | PatientDto | Lấy thông tin bệnh nhân theo ID (admin/staff) |
| 6 | updatePatientMe | public | PatientDto | Cập nhật thông tin bệnh nhân (bản thân) |
| 7 | updatePatient | public | PatientDto | Cập nhật thông tin bệnh nhân theo ID (admin/staff) |

## 4. EncounterController

| STT | Tên phương thức | Loại | Kiểu trả về | Ý nghĩa/Ghi chú |
|-----|----------------|------|-------------|-----------------|
| 1 | startEncounter | public | EncounterDto | Bắt đầu cuộc khám bệnh mới (admin/staff) |
| 2 | getEncounterById | public | EncounterDto | Lấy thông tin cuộc khám theo ID (admin/staff) |
| 3 | recordDiagnosis | public | Void | Ghi nhận chẩn đoán (admin/staff) |
| 4 | addNotes | public | Void | Thêm ghi chú cho cuộc khám (admin/staff) |
| 5 | endEncounter | public | Void | Kết thúc cuộc khám (admin/staff) |
| 6 | generateSummary | public | String | Tạo tóm tắt cuộc khám (admin/staff) |

## 5. AppointmentController

| STT | Tên phương thức | Loại | Kiểu trả về | Ý nghĩa/Ghi chú |
|-----|----------------|------|-------------|-----------------|
| 1 | scheduleAppointment | public | AppointmentDto | Đặt lịch hẹn mới (admin/staff) |
| 2 | getAppointmentById | public | AppointmentDto | Lấy thông tin lịch hẹn theo ID |
| 3 | rescheduleAppointment | public | Void | Dời lịch hẹn (admin/staff) |
| 4 | cancelAppointment | public | Void | Hủy lịch hẹn với lý do |
| 5 | confirmAppointment | public | Void | Xác nhận lịch hẹn (admin/staff) |
| 6 | checkIn | public | Void | Check-in lịch hẹn |
| 7 | markAsDone | public | Void | Đánh dấu lịch hẹn hoàn thành (admin/staff) |
| 8 | createEncounter | public | Object | Tạo cuộc khám từ lịch hẹn (admin/staff) |

## 6. PrescriptionController

| STT | Tên phương thức | Loại | Kiểu trả về | Ý nghĩa/Ghi chú |
|-----|----------------|------|-------------|-----------------|
| 1 | createPrescription | public | PrescriptionDto | Tạo đơn thuốc mới từ cuộc khám (admin/staff) |
| 2 | getPrescriptionById | public | PrescriptionDto | Lấy thông tin đơn thuốc theo ID (admin/staff) |
| 3 | getDetails | public | List | Lấy chi tiết thuốc trong đơn (admin/staff) |
| 4 | finalizePrescription | public | Void | Hoàn tất đơn thuốc DRAFT -> FINAL (admin/staff) |
| 5 | dispenseMedication | public | Void | Cấp phát thuốc FINAL -> DISPENSED (admin/staff) |
| 6 | calculateTotal | public | BigDecimal | Tính tổng tiền đơn thuốc (admin/staff) |
| 7 | printPrescription | public | String | In đơn thuốc (admin/staff) |

## 7. FollowUpPlanController

| STT | Tên phương thức | Loại | Kiểu trả về | Ý nghĩa/Ghi chú |
|-----|----------------|------|-------------|-----------------|
| 1 | createPlan | public | FollowUpPlanDto | Tạo kế hoạch tái khám |
| 2 | createFromEncounter | public | FollowUpPlanDto | Tạo kế hoạch tái khám từ cuộc khám |
| 3 | getFollowUpPlanById | public | FollowUpPlanDto | Lấy thông tin kế hoạch tái khám theo ID |
| 4 | editPlan | public | Void | Chỉnh sửa kế hoạch tái khám |
| 5 | pausePlan | public | Void | Tạm dừng kế hoạch tái khám |
| 6 | resumePlan | public | Void | Tiếp tục kế hoạch tái khám |
| 7 | completePlan | public | Void | Hoàn thành kế hoạch tái khám |
| 8 | generateAppointments | public | List | Tạo các lịch hẹn từ kế hoạch |

## 8. AdmissionController

| STT | Tên phương thức | Loại | Kiểu trả về | Ý nghĩa/Ghi chú |
|-----|----------------|------|-------------|-----------------|
| 1 | admitPatient | public | AdmissionDto | Nhập viện cho bệnh nhân (admin/staff) |
| 2 | getAdmissionById | public | AdmissionDto | Lấy thông tin nhập viện theo ID (admin/staff) |
| 3 | dischargePatient | public | Void | Xuất viện cho bệnh nhân (admin/staff) |
| 4 | updateNotes | public | Void | Cập nhật ghi chú nhập viện (admin/staff) |

## 9. WaitlistEntryController

| STT | Tên phương thức | Loại | Kiểu trả về | Ý nghĩa/Ghi chú |
|-----|----------------|------|-------------|-----------------|
| 1 | addToWaitlist | public | WaitlistEntryDto | Thêm bệnh nhân vào danh sách chờ (admin/staff) |
| 2 | getAllEntries | public | List | Lấy tất cả mục trong danh sách chờ (admin/staff) |
| 3 | getEntryById | public | WaitlistEntryDto | Lấy mục trong danh sách chờ theo ID (admin/staff) |
| 4 | callNext | public | WaitlistEntryDto | Gọi bệnh nhân tiếp theo theo khoa (admin/staff) |
| 5 | changePriority | public | Void | Thay đổi mức độ ưu tiên (admin/staff) |
| 6 | markAsServed | public | Void | Đánh dấu đã phục vụ (admin/staff) |
| 7 | cancelEntry | public | Void | Hủy mục trong danh sách chờ (admin/staff) |
| 8 | getWaitingCount | public | Integer | Đếm số bệnh nhân đang chờ theo khoa (admin/staff) |

## 10. ShiftAssignmentController

| STT | Tên phương thức | Loại | Kiểu trả về | Ý nghĩa/Ghi chú |
|-----|----------------|------|-------------|-----------------|
| 1 | assignDoctor | public | ShiftAssignmentDto | Phân công bác sĩ vào ca làm việc (chỉ admin) |
| 2 | getAssignmentById | public | ShiftAssignmentDto | Lấy thông tin phân công theo ID (admin/staff) |
| 3 | updateStatus | public | Void | Cập nhật trạng thái phân công (chỉ admin) |
| 4 | updateRoom | public | Void | Cập nhật phòng cho phân công (chỉ admin) |
| 5 | findByShift | public | List | Lấy phân công theo ca và ngày (chỉ admin) |
| 6 | findByDoctor | public | List | Lấy phân công theo bác sĩ trong khoảng thời gian (chỉ admin) |

## 11. PrescriptionDetailController

| STT | Tên phương thức | Loại | Kiểu trả về | Ý nghĩa/Ghi chú |
|-----|----------------|------|-------------|-----------------|
| 1 | createDetail | public | PrescriptionDetailDto | Thêm thuốc vào đơn thuốc |
| 2 | getDetailById | public | PrescriptionDetailDto | Lấy chi tiết thuốc theo ID |
| 3 | updateDosage | public | Void | Cập nhật thông tin liều lượng |
| 4 | updateQuantity | public | Void | Cập nhật số lượng thuốc |
| 5 | removeDrugItem | public | Void | Xóa thuốc khỏi đơn |

## 12. RoleController

| STT | Tên phương thức | Loại | Kiểu trả về | Ý nghĩa/Ghi chú |
|-----|----------------|------|-------------|-----------------|
| 1 | createRole | public | RoleDto | Tạo vai trò mới |
| 2 | findById | public | RoleDto | Tìm vai trò theo ID |
| 3 | findByName | public | RoleDto | Tìm vai trò theo tên |
| 4 | findAll | public | List | Lấy danh sách tất cả vai trò |
| 5 | deleteById | public | HttpStatus | Xóa vai trò theo ID |

## 13. RoomController

| STT | Tên phương thức | Loại | Kiểu trả về | Ý nghĩa/Ghi chú |
|-----|----------------|------|-------------|-----------------|
| 1 | createRoom | public | RoomDto | Tạo phòng mới (chỉ admin) |
| 2 | getAll | public | List | Lấy danh sách phòng theo khoa (admin/staff) |
| 3 | getRoom | public | RoomDto | Lấy thông tin phòng theo ID (admin/staff) |
| 4 | getAllRooms | public | List | Lấy tất cả phòng (admin/staff) |
| 5 | updateStatus | public | RoomDto | Cập nhật trạng thái phòng (admin/staff) |
| 6 | updateRoom | public | RoomDto | Cập nhật thông tin phòng (chỉ admin) |
| 7 | deleteRoom | public | Void | Xóa phòng (admin/staff) |

## 14. UserController

| STT | Tên phương thức | Loại | Kiểu trả về | Ý nghĩa/Ghi chú |
|-----|----------------|------|-------------|-----------------|
| 1 | updateAvatar | public | UserDto | Cập nhật ảnh đại diện (bản thân) |
| 2 | updatePersonalInfo | public | UserDto | Cập nhật thông tin cá nhân (bản thân) |
| 3 | updateUserRole | public | UserDto | Cập nhật vai trò người dùng |
| 4 | getUser | public | UserDto | Lấy thông tin người dùng hiện tại |
| 5 | deactivateUser | public | UserDto | Vô hiệu hóa người dùng (chỉ admin) |
| 6 | activateUser | public | UserDto | Kích hoạt người dùng (chỉ admin) |
| 7 | getAllUsers | public | List | Lấy danh sách tất cả người dùng (chỉ admin) |

## 15. DrugController

| STT | Tên phương thức | Loại | Kiểu trả về | Ý nghĩa/Ghi chú |
|-----|----------------|------|-------------|-----------------|
| 1 | createDrug | public | DrugDto | Tạo thuốc mới (chỉ admin) |
| 2 | getDrug | public | DrugDto | Lấy thông tin thuốc theo ID |
| 3 | getAllDrugs | public | List | Lấy danh sách tất cả thuốc |
| 4 | updateDrug | public | DrugDto | Cập nhật thông tin thuốc (chỉ admin) |
| 5 | deleteDrug | public | HttpStatus | Xóa thuốc (chỉ admin) |

## 16. StaffController

| STT | Tên phương thức | Loại | Kiểu trả về | Ý nghĩa/Ghi chú |
|-----|----------------|------|-------------|-----------------|
| 1 | createStaff | public | StaffDto | Tạo nhân viên mới (chỉ admin) |
| 2 | searchStaff | public | List | Tìm kiếm nhân viên theo vị trí |
| 3 | getStaffMe | public | StaffDto | Lấy thông tin nhân viên hiện tại (bản thân) |
| 4 | getStaffById | public | StaffDto | Lấy thông tin nhân viên theo ID |
| 5 | updateStaffMe | public | StaffDto | Cập nhật thông tin nhân viên (bản thân) |
| 6 | updateStaffById | public | StaffDto | Cập nhật thông tin nhân viên theo ID |

## 17. ShiftController

| STT | Tên phương thức | Loại | Kiểu trả về | Ý nghĩa/Ghi chú |
|-----|----------------|------|-------------|-----------------|
| 1 | createShift | public | ShiftDto | Tạo ca làm việc mới (chỉ admin) |
| 2 | getAllShifts | public | List | Lấy danh sách tất cả ca làm việc (staff/admin) |
| 3 | getShiftById | public | ShiftDto | Lấy thông tin ca làm việc theo ID (staff/admin) |
| 4 | updateShift | public | Void | Cập nhật ca làm việc (chỉ admin) |
| 5 | deleteShift | public | Void | Xóa ca làm việc (chỉ admin) |
| 6 | getAvailableSlots | public | List | Lấy các khung giờ trống |
| 7 | getCapacity | public | Integer | Lấy sức chứa của ca |

## 18. NotificationController

| STT | Tên phương thức | Loại | Kiểu trả về | Ý nghĩa/Ghi chú |
|-----|----------------|------|-------------|-----------------|
| 1 | createNotification | public | NotificationDto | Tạo thông báo mới (chỉ admin) |
| 2 | getMyNotifications | public | List | Lấy tất cả thông báo của tôi |
| 3 | getMyUnreadNotifications | public | List | Lấy thông báo chưa đọc của tôi |
| 4 | getMyUnreadCount | public | Integer | Đếm số thông báo chưa đọc của tôi |
| 5 | getById | public | NotificationDto | Lấy thông báo theo ID |
| 6 | markAsRead | public | NotificationDto | Đánh dấu thông báo đã đọc |
| 7 | markAllAsRead | public | Void | Đánh dấu tất cả thông báo đã đọc |
| 8 | getNotificationsByUserId | public | List | Lấy thông báo theo user ID (chỉ admin) |

## 19. DepartmentController

| STT | Tên phương thức | Loại | Kiểu trả về | Ý nghĩa/Ghi chú |
|-----|----------------|------|-------------|-----------------|
| 1 | assignStaff | public | HttpStatus | Phân công nhân viên vào khoa (chỉ admin) |
| 2 | deleteStaff | public | HttpStatus | Xóa nhân viên khỏi khoa (chỉ admin) |
| 3 | create | public | DepartmentDto | Tạo khoa mới (chỉ admin) |
| 4 | getAllStaff | public | List | Lấy danh sách nhân viên trong khoa |
| 5 | getDepartmentStatistics | public | DepartmentStatisticsDto | Lấy thống kê khoa (chỉ admin) |
| 6 | getById | public | DepartmentDto | Lấy thông tin khoa theo ID |
| 7 | getAll | public | List | Lấy danh sách tất cả khoa (admin/staff) |
| 8 | update | public | DepartmentDto | Cập nhật thông tin khoa |
| 9 | delete | public | Void | Xóa khoa |

---

## Ghi chú chung

### Quyền truy cập:
- **admin**: Chỉ quản trị viên
- **staff**: Nhân viên y tế
- **admin/staff**: Cả admin và staff
- Không có annotation: Tất cả người dùng đã xác thực

### HTTP Methods:
- **POST**: Tạo mới tài nguyên
- **GET**: Lấy thông tin
- **PUT**: Cập nhật toàn bộ
- **PATCH**: Cập nhật một phần
- **DELETE**: Xóa tài nguyên

### Kiểu trả về phổ biến:
- `ResponseEntity<Dto>`: Trả về đối tượng đơn
- `ResponseEntity<List<Dto>>`: Trả về danh sách đối tượng
- `ResponseEntity<Void>`: Không có dữ liệu trả về (chỉ status code)
- `ResponseEntity<String>`: Trả về chuỗi văn bản
- `ResponseEntity<Integer>`: Trả về số nguyên
- `ResponseEntity<BigDecimal>`: Trả về số thập phân lớn
