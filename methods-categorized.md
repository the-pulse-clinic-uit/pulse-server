Đây là đề xuất refactor các API endpoint cho các lớp được yêu cầu, tuân thủ nghiêm ngặt Nguyên tắc Trách nhiệm Đơn lẻ (SRP). Mỗi tài nguyên (Class) sẽ được tách biệt thành các nhóm chức năng rõ ràng: **Quản lý Hồ sơ (CRUD)**, **Quản lý Trạng thái/Nghiệp vụ (Workflow)**, và **Tạo/Liên kết tài nguyên phụ**.

### 1. Doctors (Bác sĩ)

| Nhóm Trách nhiệm | Phương thức API (Refactored) | Mô tả dựa trên SRP | Tham khảo nguồn |
| :--- | :--- | :--- | :--- |
| **Quản lý Hồ sơ** | `POST /doctors` | Tạo hồ sơ bác sĩ mới. | `createDoctor` |
| | `GET /doctors` | Lấy danh sách tất cả bác sĩ. | - |
| | `GET /doctors/{id}` | Lấy thông tin chi tiết bác sĩ. | `getDoctor` |
| | `PUT /doctors/{id}` | Cập nhật thông tin cơ bản bác sĩ. | `updateDoctor` |
| | `PUT /doctors/{id}/specialization` | Cập nhật chuyên khoa. | `updateSpecialization` |
| | `GET /doctors/{id}/patients` | Lấy danh sách bệnh nhân đã khám. | `getPatients` |
| **Tra cứu Lịch trình** | `GET /doctors/{id}/schedule` | Lấy lịch ca làm việc cho ngày cụ thể. | `getShiftSchedule` |
| | `GET /doctors/{id}/availability` | Kiểm tra trạng thái sẵn sàng. | `checkAvailability` |

### 2. Shifts (Ca trực)

Trách nhiệm quản lý định nghĩa ca trực (thời gian, sức chứa) được tách biệt khỏi việc phân công thực tế.

| Nhóm Trách nhiệm | Phương thức API (Refactored) | Mô tả dựa trên SRP | Tham khảo nguồn |
| :--- | :--- | :--- | :--- |
| **Quản lý Định nghĩa** | `POST /shifts` | Tạo ca làm việc mới. | `createShift` |
| | `GET /shifts` | Lấy danh sách tất cả ca làm việc. | - |
| | `GET /shifts/{id}` | Lấy thông tin chi tiết ca làm việc. | `getShift` |
| | `PUT /shifts/{id}` | Cập nhật cài đặt ca làm việc. | `updateShift` |
| | `DELETE /shifts/{id}` | Xóa ca làm việc. | `deleteShift` |
| **Tra cứu Sức chứa** | `GET /shifts/{id}/slots/available` | Lấy các slot hẹn còn trống. | `getAvailableSlots` |
| | `GET /shifts/{id}/capacity` | Lấy tổng sức chứa của ca. | `getCapacity` |

### 3. Shift Assignments (Phân công Ca trực)

Trách nhiệm quản lý việc gán bác sĩ vào ca trực theo ngày.

| Nhóm Trách nhiệm | Phương thức API (Refactored) | Mô tả dựa trên SRP | Tham khảo nguồn |
| :--- | :--- | :--- | :--- |
| **Quản lý Gán việc** | `POST /shifts/assignments` | Phân công bác sĩ vào ca làm việc. | `assignDoctor` |
| | `GET /shifts/assignments/{id}` | Lấy thông tin chi tiết phân công. | `getAssignment` |
| | `PUT /shifts/assignments/{id}/status` | Cập nhật trạng thái phân công (ví dụ: hủy). | `updateStatus`, `cancelAssignment` |
| | `PUT /shifts/assignments/{id}/room` | Thay đổi phòng được phân công. | `updateRoom` |
| **Tra cứu** | `GET /shifts/{shift_id}/assignments` | Lấy tất cả phân công của một ca trực. | `getAssignmentsByShift` |
| | `GET /shifts/assignments/by_doctor/{doctor_id}` | Lấy phân công của bác sĩ theo khoảng thời gian. | `findByDoctor` |

### 4. Appointments (Cuộc hẹn)

Trách nhiệm quản lý vòng đời cuộc hẹn, tách biệt chức năng tạo buổi khám (`Encounter`) sang một endpoint riêng.

| Nhóm Trách nhiệm | Phương thức API (Refactored) | Mô tả dựa trên SRP | Tham khảo nguồn |
| :--- | :--- | :--- | :--- |
| **Quản lý Lịch trình** | `POST /appointments` | Đặt lịch hẹn mới. | `scheduleAppointment` |
| | `GET /appointments/{id}` | Lấy thông tin chi tiết cuộc hẹn. | `getAppointment` |
| | `PUT /appointments/{id}/reschedule` | Thay đổi thời gian cuộc hẹn. | `rescheduleAppointment` |
| | `PUT /appointments/{id}/cancel` | Hủy cuộc hẹn. | `cancelAppointment` |
| **Quản lý Trạng thái** | `PUT /appointments/{id}/confirm` | Xác nhận cuộc hẹn đang chờ. | `confirmAppointment` |
| | `PUT /appointments/{id}/checkin` | Đánh dấu bệnh nhân đã check-in. | `checkIn` |
| | `PUT /appointments/{id}/done` | Hoàn thành cuộc hẹn. | `markAsDone` |
| **Tạo Tài nguyên phụ** | `POST /appointments/{id}/encounter` | Tạo hồ sơ buổi khám từ cuộc hẹn (Chuyển đổi trạng thái). | `createEncounter` |

### 5. Waitlist Entries (Danh sách Chờ)

Trách nhiệm quản lý thứ tự và luồng ưu tiên trong hàng đợi.

| Nhóm Trách nhiệm | Phương thức API (Refactored) | Mô tả dựa trên SRP | Tham khảo nguồn |
| :--- | :--- | :--- | :--- |
| **Quản lý Hàng đợi** | `POST /waitlist` | Thêm bệnh nhân vào danh sách chờ. | `addToWaitlist` |
| | `GET /waitlist` | Lấy danh sách tất cả entry đang chờ. | - |
| | `GET /waitlist/{id}` | Lấy thông tin chi tiết entry. | `getEntry` |
| | `POST /waitlist/department/{dept_id}/call_next` | Gọi bệnh nhân tiếp theo trong hàng đợi của department. | `callNext` |
| | `PUT /waitlist/{id}/priority` | Cập nhật mức độ ưu tiên. | `changePriority` |
| **Quản lý Trạng thái** | `PUT /waitlist/{id}/served` | Đánh dấu đã phục vụ. | `markAsServed` |
| | `PUT /waitlist/{id}/cancel` | Hủy mục danh sách chờ. | `cancelEntry` |
| **Tra cứu** | `GET /waitlist/department/{dept_id}/waiting/count` | Đếm số bệnh nhân đang chờ của department. | `getWaitingCount` |

### 6. Encounters (Buổi khám)

Trách nhiệm quản lý quá trình khám bệnh và các thông tin lâm sàng cơ bản, tách biệt các phương thức tạo tài nguyên phụ (Hóa đơn, Đơn thuốc, Nhập viện) thành các API chuyên biệt (Inter-resource creation).

| Nhóm Trách nhiệm | Phương thức API (Refactored) | Mô tả dựa trên SRP | Tham khảo nguồn |
| :--- | :--- | :--- | :--- |
| **Quản lý Quy trình** | `POST /encounters` | Bắt đầu khám bệnh mới. | `startEncounter` |
| | `GET /encounters/{id}` | Lấy thông tin chi tiết buổi khám. | `getEncounter` |
| | `PUT /encounters/{id}/diagnosis` | Ghi lại chẩn đoán y khoa. | `recordDiagnosis` |
| | `PUT /encounters/{id}/notes` | Thêm ghi chú lâm sàng. | `addNotes` |
| | `POST /encounters/{id}/end` | Kết thúc khám bệnh. | `endEncounter` |
| **Tra cứu** | `GET /encounters/{id}/summary` | Tạo tóm tắt bệnh án. | `generateSummary` |

### 7. Prescriptions (Đơn thuốc)

Trách nhiệm quản lý vòng đời đơn thuốc (DRAFT, FINAL, DISPENSED) và tính tổng giá.

| Nhóm Trách nhiệm | Phương thức API (Refactored) | Mô tả dựa trên SRP | Tham khảo nguồn |
| :--- | :--- | :--- | :--- |
| **Tạo Đơn từ Encounter**| `POST /prescriptions/from_encounter/{encounter_id}` | Tạo đơn thuốc mới, liên kết với buổi khám. | `createPrescription` |
| | `GET /prescriptions/{id}` | Lấy thông tin chi tiết đơn thuốc. | `getPrescription` |
| | `GET /prescriptions/{id}/details` | Lấy danh sách thuốc trong đơn. | `getDetails` |
| **Quản lý Trạng thái** | `PUT /prescriptions/{id}/finalize` | Hoàn tất đơn thuốc (DRAFT → FINAL). | `finalizePrescription` |
| | `PUT /prescriptions/{id}/dispense` | Đánh dấu đã xuất thuốc (FINAL → DISPENSED). | `dispenseMedication` |
| **Tính toán/Xuất** | `GET /prescriptions/{id}/total` | Tính tổng giá của đơn thuốc. | `calculateTotal` |
| | `GET /prescriptions/{id}/print` | Tạo đơn thuốc có thể in. | `printPrescription` |

### 8. Prescription Details (Chi tiết Kê đơn)

Trách nhiệm quản lý các mục thuốc riêng lẻ trong một đơn thuốc.

| Nhóm Trách nhiệm | Phương thức API (Refactored) | Mô tả dựa trên SRP | Tham khảo nguồn |
| :--- | :--- | :--- | :--- |
| **Quản lý Mục thuốc** | `POST /prescriptions/{id}/details` | Thêm mục thuốc mới vào đơn. | `addDrugItem`, `createDetail` |
| | `GET /prescriptions/details/{item_id}` | Lấy thông tin chi tiết mục thuốc. | `getDetail` |
| | `PUT /prescriptions/details/{item_id}/dosage` | Cập nhật thông tin liều lượng/tần suất. | `updateDosage` |
| | `PUT /prescriptions/details/{item_id}/quantity` | Cập nhật số lượng. | `updateQuantity` |
| | `DELETE /prescriptions/details/{item_id}` | Xóa thuốc khỏi đơn. | `removeDrugItem` |

### 9. Invoices (Hóa đơn)

Trách nhiệm quản lý việc lập hóa đơn, tính toán số dư và các mục phí (billing), tách biệt khỏi hành động ghi nhận thanh toán (`Payment`).

| Nhóm Trách nhiệm | Phương thức API (Refactored) | Mô tả dựa trên SRP | Tham khảo nguồn |
| :--- | :--- | :--- | :--- |
| **Tạo Hóa đơn** | `POST /invoices/from_encounter/{encounter_id}` | Tạo hóa đơn mới liên kết với buổi khám. | `createInvoice` |
| | `GET /invoices/{id}` | Lấy thông tin chi tiết hóa đơn. | `getInvoice` |
| | `GET /invoices/{id}/balance` | Lấy số dư cần thanh toán. | `getBalance` |
| | `GET /invoices/{id}/line_items` | Lấy danh sách mục phí. | `getLineItems` |
| **Quản lý Mục phí** | `POST /invoices/{id}/line_item` | Thêm mục phí/dịch vụ vào hóa đơn. | `addLineItem` |
| | `POST /invoices/{id}/discount` | Áp dụng chiết khấu. | `applyDiscount` |
| **Quản lý Trạng thái** | `POST /invoices/{id}/void` | Hủy hóa đơn. | `voidInvoice` |
| **Ghi nhận Thanh toán** | `POST /invoices/{id}/payment` | Ghi nhận số tiền thanh toán (thay thế cho `recordPayment` trong lớp `Invoice` để tập trung trách nhiệm thanh toán). | `recordPayment` |

### 10. Admissions (Nhập viện)

Trách nhiệm quản lý vòng đời của một đợt nhập viện (từ nhập viện, chuyển phòng đến xuất viện).

| Nhóm Trách nhiệm | Phương thức API (Refactored) | Mô tả dựa trên SRP | Tham khảo nguồn |
| :--- | :--- | :--- | :--- |
| **Quản lý Vòng đời** | `POST /admissions` | Nhập viện bệnh nhân. *Lưu ý: Nếu được tạo từ Encounter, sử dụng endpoint chuyên biệt.* | `admitPatient` |
| | `POST /admissions/from_encounter/{encounter_id}` | Nhập viện từ buổi khám. | `admitPatient` |
| | `GET /admissions/{id}` | Lấy thông tin chi tiết đợt nhập viện. | `getAdmission` |
| | `PUT /admissions/{id}/transfer` | Chuyển bệnh nhân sang phòng khác. | `transferRoom` |
| | `POST /admissions/{id}/discharge` | Cho bệnh nhân xuất viện. | `dischargePatient` |
| **Cập nhật** | `PUT /admissions/{id}/notes` | Cập nhật ghi chú nhập viện. | `updateNotes` |

### 11. Follow Up Plans (Kế hoạch Tái khám)

Trách nhiệm quản lý định nghĩa kế hoạch tái khám và chức năng tạo ra các cuộc hẹn thực tế.

| Nhóm Trách nhiệm | Phương thức API (Refactored) | Mô tả dựa trên SRP | Tham khảo nguồn |
| :--- | :--- | :--- | :--- |
| **Quản lý Kế hoạch** | `POST /followup/plans` | Tạo kế hoạch tái khám mới. | `createPlan` |
| | `POST /followup/plans/from_encounter/{encounter_id}` | Tạo kế hoạch tái khám từ buổi khám. | `createPlanFromEncounter` |
| | `GET /followup/plans/{id}` | Lấy thông tin chi tiết kế hoạch. | `getPlan` |
| | `PUT /followup/plans/{id}` | Chỉnh sửa chi tiết kế hoạch. | `editPlan` |
| **Quản lý Trạng thái** | `PUT /followup/plans/{id}/pause` | Tạm dừng kế hoạch. | `pausePlan` |
| | `PUT /followup/plans/{id}/resume` | Tiếp tục kế hoạch đã tạm dừng. | `resumePlan` |
| | `PUT /followup/plans/{id}/complete` | Đánh dấu kế hoạch hoàn tất. | `completePlan` |
| **Sinh Tài nguyên phụ** | `POST /followup/plans/{id}/generate_appointments` | Sinh ra các cuộc hẹn từ kế hoạch. | `generateAppointments` |

Việc tách biệt này đảm bảo rằng mỗi endpoint chỉ chịu trách nhiệm cho một khía cạnh nghiệp vụ cụ thể của tài nguyên chính, ví dụ: endpoint `/encounters/{id}/diagnosis` chỉ xử lý việc ghi chẩn đoán, trong khi endpoint `/prescriptions/from_encounter/{id}` chịu trách nhiệm tạo ra tài nguyên `Prescription`,. Điều này giúp lớp **Encounter** tập trung vào trách nhiệm cốt lõi là ghi lại thông tin lâm sàng của buổi khám.


