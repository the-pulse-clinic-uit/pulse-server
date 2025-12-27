Dựa trên sơ đồ ERD (Entity-Relationship Diagram) bạn cung cấp, đây là một hệ thống quản lý bệnh viện/phòng khám khá toàn diện. Hệ thống này bao phủ từ khâu đặt lịch, tiếp đón, khám bệnh, nội trú, đến kê đơn và thanh toán.

Dưới đây là mô tả chi tiết quy trình hoạt động (Business Flow) của hệ thống dựa trên các bảng dữ liệu:

### 1. Quản trị Nhân sự & Phân quyền (User & Staff Management)

Trước khi hệ thống vận hành, dữ liệu nền tảng được thiết lập:

* **Người dùng (Users):** Mọi đối tượng (Bác sĩ, Nhân viên, Bệnh nhân, Admin) đều bắt đầu từ bảng `users` để quản lý thông tin đăng nhập và cá nhân cơ bản.
* **Phân vai (Roles):** Hệ thống phân định rõ `roles` (doctor, staff, patient...).
* **Hồ sơ chuyên sâu:**
* Nếu là **Nhân viên/Bác sĩ**: Dữ liệu sẽ được mở rộng sang bảng `staff` (phòng ban, chức vụ) và `doctors` (chuyên khoa, số giấy phép hành nghề).
* Nếu là **Bệnh nhân**: Dữ liệu mở rộng sang `patients` (nhóm máu, tiền sử dị ứng, bảo hiểm y tế).



### 2. Quy trình Lịch trực & Đặt hẹn (Scheduling)

Đây là bước đầu tiên để bệnh nhân tiếp cận dịch vụ:

1. **Tạo Ca trực (Shifts):** Quản lý sẽ tạo các khung giờ làm việc (`shifts`) cho phòng khám hoặc cấp cứu (ER).
2. **Phân công (Assignments):** Bác sĩ được gán vào các ca trực cụ thể thông qua `shift_assignments` vào các ngày cụ thể (`duty_date`). Bác sĩ có thể là người trực chính (PRIMARY) hoặc trực thay thế/gọi khi cần (ON_CALL).
3. **Đặt hẹn (Booking):** Bệnh nhân tạo `appointments`.
* Cuộc hẹn sẽ liên kết với một bác sĩ hoặc một ca trực cụ thể.
* Hệ thống kiểm tra trạng thái (`status`): PENDING -> CONFIRMED.
* Nếu là tái khám, cuộc hẹn có thể liên kết với `follow_up_plans` hoặc `previous_encounter_id`.



### 3. Quy trình Tiếp đón & Hàng đợi (Reception & Queuing)

Khi bệnh nhân đến bệnh viện vào ngày hẹn (hoặc đến cấp cứu):

1. **Check-in:** Bệnh nhân được đưa vào hàng đợi thông qua bảng `waitlist_entries`.
* Mỗi lượt khám sẽ có số thứ tự (`ticket_no`).
* Trạng thái chuyển từ WAITING -> CALLED (Được gọi vào khám).


2. **Phân loại:** Tại đây nhân viên có thể xác định mức độ ưu tiên (`priority`) là NORMAL (Thường) hay EMERGENCY (Cấp cứu).

### 4. Quy trình Khám bệnh (Clinical Encounter) - **Core Feature**

Đây là trọng tâm của hệ thống, nơi bác sĩ tương tác với bệnh nhân:

1. **Bắt đầu khám:** Khi bác sĩ nhận bệnh nhân, một bản ghi `encounters` được tạo ra.
* Dữ liệu ghi nhận thời gian bắt đầu (`started_at`).
* Phân loại khám là theo hẹn (`APPOINTED`) hay cấp cứu (`EMERGENCY`).


2. **Chẩn đoán:** Bác sĩ nhập thông tin chẩn đoán (`diagnosis`) và ghi chú lâm sàng (`notes`).
3. **Kết thúc khám:** Cập nhật `ended_at`.

### 5. Quy trình Điều trị & Kê đơn (Treatment & Prescription)

Trong hoặc sau quá trình khám (`encounters`):

1. **Kê đơn thuốc:** Bác sĩ tạo đơn thuốc (`prescriptions`).
2. **Chi tiết thuốc:** Bác sĩ chọn thuốc từ danh mục `drugs` để đưa vào `prescriptions_details`.
* Hệ thống lưu liều lượng (`dose`), cách dùng (`instructions`), tần suất (`frequency`).
* Đơn thuốc có trạng thái: DRAFT (Nháp) -> FINAL (Chốt) -> DISPENSED (Đã phát thuốc).


3. **Kế hoạch tái khám:** Nếu cần theo dõi lâu dài, bác sĩ tạo `follow_up_plans`.
* Quy định bao nhiêu ngày khám lại (`interval_days`), số lần khám (`occurrences`).
* Hệ thống có thể tự động nhắc hoặc tạo lịch hẹn dựa trên kế hoạch này.



### 6. Quy trình Nội trú (Inpatient Admission)

Nếu bệnh nhân nặng cần nhập viện:

1. **Nhập viện:** Tạo bản ghi `admissions`.
2. **Xếp phòng:** Bệnh nhân được gán vào `rooms` (kiểm tra `bed_amount` và `is_available`).
3. **Theo dõi:** Trạng thái là ONGOING. Bác sĩ chịu trách nhiệm sẽ theo dõi quá trình này.
4. **Xuất viện:** Cập nhật `discharged_at` và chuyển trạng thái sang DISCHARGED.

### 7. Quy trình Tài chính (Billing)

Sau khi quy trình khám hoặc điều trị kết thúc:

1. **Tạo hóa đơn:** Hệ thống sinh ra `invoices` dựa trên `encounters` (bao gồm phí khám, phí thuốc từ `prescriptions`, phí nội trú...).
2. **Thanh toán:** Nhân viên thu ngân cập nhật `amount_paid` và trạng thái (UNPAID -> PAID).

### 8. Các tính năng hỗ trợ (Utilities)

* **Thông báo (`notifications`):** Hệ thống tự động gửi tin nhắn (SMS/Email/App) khi có lịch hẹn, nhắc đóng tiền, hoặc nhắc uống thuốc.
* **Đánh giá (`staff_ratings`):** Sau khi hoàn tất khám, bệnh nhân có thể đánh giá sao và để lại bình luận về bác sĩ hoặc nhân viên.

### Tóm tắt luồng đi chính (Happy Path):

> **Đặt lịch** (`appointments`) -> **Đến viện & Xếp hàng** (`waitlist`) -> **Gặp bác sĩ** (`encounters`) -> **Chẩn đoán & Kê đơn** (`prescriptions`) -> **Thanh toán** (`invoices`) -> **Lấy thuốc & Ra về**.