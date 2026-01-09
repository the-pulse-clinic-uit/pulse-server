package com.pulseclinic.pulse_server.config;

import com.pulseclinic.pulse_server.enums.BloodType;
import com.pulseclinic.pulse_server.enums.DrugDosageForm;
import com.pulseclinic.pulse_server.enums.DrugUnit;
import com.pulseclinic.pulse_server.enums.Position;
import com.pulseclinic.pulse_server.modules.patients.entity.Patient;
import com.pulseclinic.pulse_server.modules.patients.repository.PatientRepository;
import com.pulseclinic.pulse_server.modules.pharmacy.entity.Drug;
import com.pulseclinic.pulse_server.modules.pharmacy.repository.DrugRepository;
import com.pulseclinic.pulse_server.modules.rooms.entity.Room;
import com.pulseclinic.pulse_server.modules.rooms.repository.RoomRepository;
import com.pulseclinic.pulse_server.modules.staff.entity.Department;
import com.pulseclinic.pulse_server.modules.staff.entity.Doctor;
import com.pulseclinic.pulse_server.modules.staff.entity.Staff;
import com.pulseclinic.pulse_server.modules.staff.repository.DepartmentRepository;
import com.pulseclinic.pulse_server.modules.staff.repository.DoctorRepository;
import com.pulseclinic.pulse_server.modules.staff.repository.StaffRepository;
import com.pulseclinic.pulse_server.modules.users.entity.Role;
import com.pulseclinic.pulse_server.modules.users.entity.User;
import com.pulseclinic.pulse_server.modules.users.repository.RoleRepository;
import com.pulseclinic.pulse_server.modules.users.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(
            RoleRepository roleRepository,
            UserRepository userRepository,
            DepartmentRepository departmentRepository,
            StaffRepository staffRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository,
            DrugRepository drugRepository,
            RoomRepository roomRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            // Check if data already exists
            if (doctorRepository.count() > 0) {
                System.out.println("Database already seeded. Skipping...");
                return;
            }

            System.out.println("Starting database seeding...");

            // 1. Seed Roles
            System.out.println("Seeding roles...");
            Role patientRole = roleRepository.save(Role.builder()
                    .name("patient")
                    .build());

            Role staffRole = roleRepository.save(Role.builder()
                    .name("staff")
                    .build());

            Role doctorRole = roleRepository.save(Role.builder()
                    .name("doctor")
                    .build());

            // 2. Seed Departments
            System.out.println("Seeding departments...");
            Department cardiology = departmentRepository.save(Department.builder()
                    .name("Cardiology")
                    .description("Heart and cardiovascular care")
                    .build());

            Department pediatrics = departmentRepository.save(Department.builder()
                    .name("Pediatrics")
                    .description("Child healthcare")
                    .build());

            Department emergency = departmentRepository.save(Department.builder()
                    .name("Emergency")
                    .description("Emergency and urgent care")
                    .build());

            Department orthopedics = departmentRepository.save(Department.builder()
                    .name("Orthopedics")
                    .description("Bone and joint care")
                    .build());

            Department pharmacy = departmentRepository.save(Department.builder()
                    .name("Pharmacy")
                    .description("Medication and pharmaceutical services")
                    .build());

            // 3. Seed Users (Doctors)
            System.out.println("Seeding users...");
            User adminDoctor = userRepository.save(User.builder()
                    .email("admin.doctor@hospital.com")
                    .fullName("Dr. Admin")
                    .hashedPassword(passwordEncoder.encode("password123"))
                    .citizenId("000000000001")
                    .phone("0900000001")
                    .gender(true)
                    .birthDate(LocalDate.of(1980, 1, 1))
                    .address("1 Hospital Street")
                    .role(doctorRole)
                    .isActive(true)
                    .build());

            User drSmith = userRepository.save(User.builder()
                    .email("dr.smith@hospital.com")
                    .fullName("Dr. John Smith")
                    .hashedPassword(passwordEncoder.encode("password123"))
                    .citizenId("123456789001")
                    .phone("0901234567")
                    .gender(true)
                    .birthDate(LocalDate.of(1975, 5, 15))
                    .address("123 Medical Lane")
                    .role(doctorRole)
                    .isActive(true)
                    .build());

            User drJohnson = userRepository.save(User.builder()
                    .email("dr.johnson@hospital.com")
                    .fullName("Dr. Sarah Johnson")
                    .hashedPassword(passwordEncoder.encode("password123"))
                    .citizenId("123456789002")
                    .phone("0902345678")
                    .gender(false)
                    .birthDate(LocalDate.of(1982, 8, 20))
                    .address("456 Health Avenue")
                    .role(doctorRole)
                    .isActive(true)
                    .build());

            // 4. Seed Staff
            System.out.println("Seeding staff...");
            Staff adminStaff = staffRepository.save(Staff.builder()
                    .user(adminDoctor)
                    .position(Position.DOCTOR)
                    .department(cardiology)
                    .build());

            Staff smithStaff = staffRepository.save(Staff.builder()
                    .user(drSmith)
                    .position(Position.DOCTOR)
                    .department(cardiology)
                    .build());

            Staff johnsonStaff = staffRepository.save(Staff.builder()
                    .user(drJohnson)
                    .position(Position.DOCTOR)
                    .department(pediatrics)
                    .build());

            // 5. Seed Doctors
            System.out.println("Seeding doctors...");
            doctorRepository.save(Doctor.builder()
                    .staff(adminStaff)
                    .licenseId("DOC000001")
                    .isVerified(true)
                    .build());

            doctorRepository.save(Doctor.builder()
                    .staff(smithStaff)
                    .licenseId("DOC123456")
                    .isVerified(true)
                    .build());

            doctorRepository.save(Doctor.builder()
                    .staff(johnsonStaff)
                    .licenseId("DOC654321")
                    .isVerified(true)
                    .build());

            // 6. Seed Pharmacy Staff
            User pharmacist1 = userRepository.save(User.builder()
                    .email("pharmacist1@hospital.com")
                    .fullName("Jane Pharmacist")
                    .hashedPassword(passwordEncoder.encode("password123"))
                    .citizenId("987654321001")
                    .phone("0909876543")
                    .gender(false)
                    .birthDate(LocalDate.of(1990, 3, 10))
                    .address("789 Pharmacy Road")
                    .role(staffRole)
                    .isActive(true)
                    .build());

            staffRepository.save(Staff.builder()
                    .user(pharmacist1)
                    .position(Position.STAFF)
                    .department(pharmacy)
                    .build());

            // 7. Seed Patients (Users)
            System.out.println("Seeding patient users...");
            User patient1User = userRepository.save(User.builder()
                    .email("patient1@example.com")
                    .fullName("John Patient")
                    .hashedPassword(passwordEncoder.encode("password123"))
                    .citizenId("555666777888")
                    .phone("0905556667")
                    .gender(true)
                    .birthDate(LocalDate.of(1995, 7, 25))
                    .address("10 Patient Street")
                    .role(patientRole)
                    .isActive(true)
                    .build());

            User patient2User = userRepository.save(User.builder()
                    .email("patient2@example.com")
                    .fullName("Mary Patient")
                    .hashedPassword(passwordEncoder.encode("password123"))
                    .citizenId("111222333444")
                    .phone("0901112223")
                    .gender(false)
                    .birthDate(LocalDate.of(1988, 12, 5))
                    .address("20 Patient Avenue")
                    .role(patientRole)
                    .isActive(true)
                    .build());

            // 8. Seed Patient Entities
            System.out.println("Seeding patient records...");
            patientRepository.save(Patient.builder()
                    .user(patient1User)
                    .healthInsuranceId("HI123456789")
                    .bloodType(BloodType.A)
                    .allergies("Penicillin, Peanuts")
                    .build());

            patientRepository.save(Patient.builder()
                    .user(patient2User)
                    .healthInsuranceId("HI987654321")
                    .bloodType(BloodType.O)
                    .allergies("None")
                    .build());

            // 9. Seed Drugs
            System.out.println("Seeding drugs...");
            List<Drug> drugs = List.of(
                    Drug.builder()
                            .name("Paracetamol")
                            .dosageForm(DrugDosageForm.TABLET)
                            .unit(DrugUnit.TABLET)
                            .strength("500mg")
                            .unitPrice(new BigDecimal("10000"))
                            .quantity(100)
                            .minStockLevel(10)
                            .build(),
                    Drug.builder()
                            .name("Ibuprofen")
                            .dosageForm(DrugDosageForm.TABLET)
                            .unit(DrugUnit.TABLET)
                            .strength("400mg")
                            .unitPrice(new BigDecimal("15000"))
                            .quantity(100)
                            .minStockLevel(10)
                            .build(),
                    Drug.builder()
                            .name("Amoxicillin")
                            .dosageForm(DrugDosageForm.CAPSULE)
                            .unit(DrugUnit.CAPSULE)
                            .strength("500mg")
                            .unitPrice(new BigDecimal("20000"))
                            .quantity(100)
                            .minStockLevel(10)
                            .build(),
                    Drug.builder()
                            .name("Cough Syrup")
                            .dosageForm(DrugDosageForm.SYRUP)
                            .unit(DrugUnit.BOTTLE)
                            .strength("120ml")
                            .unitPrice(new BigDecimal("200000"))
                            .quantity(100)
                            .minStockLevel(10)
                            .build(),
                    Drug.builder()
                            .name("Insulin")
                            .dosageForm(DrugDosageForm.INJECTION)
                            .unit(DrugUnit.VIAL)
                            .strength("100IU/ml")
                            .unitPrice(new BigDecimal("1000000"))
                            .quantity(100)
                            .minStockLevel(10)
                            .build(),
                    Drug.builder()
                            .name("Hydrocortisone Cream")
                            .dosageForm(DrugDosageForm.CREAM)
                            .unit(DrugUnit.TUBE)
                            .strength("1%")
                            .unitPrice(new BigDecimal("80000"))
                            .quantity(100)
                            .minStockLevel(10)
                            .build(),
                    Drug.builder()
                            .name("Aspirin")
                            .dosageForm(DrugDosageForm.TABLET)
                            .unit(DrugUnit.TABLET)
                            .strength("300mg")
                            .unitPrice(new BigDecimal("5000"))
                            .quantity(100)
                            .minStockLevel(10)
                            .build(),
                    Drug.builder()
                            .name("Omeprazole")
                            .dosageForm(DrugDosageForm.CAPSULE)
                            .unit(DrugUnit.CAPSULE)
                            .strength("20mg")
                            .unitPrice(new BigDecimal("30000"))
                            .quantity(100)
                            .minStockLevel(10)
                            .build()
            );
            drugRepository.saveAll(drugs);

            // 10. Seed Rooms
            System.out.println("Seeding rooms...");
            List<Room> rooms = List.of(
                    Room.builder()
                            .roomNumber("101")
                            .bedAmount(1)
                            .isAvailable(true)
                            .department(cardiology)
                            .build(),
                    Room.builder()
                            .roomNumber("102")
                            .bedAmount(1)
                            .isAvailable(true)
                            .department(pediatrics)
                            .build(),
                    Room.builder()
                            .roomNumber("201")
                            .bedAmount(4)
                            .isAvailable(true)
                            .department(cardiology)
                            .build(),
                    Room.builder()
                            .roomNumber("202")
                            .bedAmount(1)
                            .isAvailable(true)
                            .department(cardiology)
                            .build(),
                    Room.builder()
                            .roomNumber("301")
                            .bedAmount(1)
                            .isAvailable(true)
                            .department(emergency)
                            .build(),
                    Room.builder()
                            .roomNumber("401")
                            .bedAmount(1)
                            .isAvailable(true)
                            .department(orthopedics)
                            .build(),
                    Room.builder()
                            .roomNumber("E01")
                            .bedAmount(2)
                            .isAvailable(true)
                            .department(emergency)
                            .build()
            );
            roomRepository.saveAll(rooms);

            System.out.println("Database seeding completed successfully!");
            System.out.println("==============================================");
            System.out.println("TEST ACCOUNTS:");
            System.out.println("Doctor: admin.doctor@hospital.com / password123");
            System.out.println("Doctor: dr.smith@hospital.com / password123");
            System.out.println("Doctor: dr.johnson@hospital.com / password123");
            System.out.println("Staff: pharmacist1@hospital.com / password123");
            System.out.println("Patient: patient1@example.com / password123");
            System.out.println("Patient: patient2@example.com / password123");
            System.out.println("==============================================");
        };
    }
}
