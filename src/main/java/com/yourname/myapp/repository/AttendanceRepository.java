package com.yourname.myapp.repository;

import com.yourname.myapp.config.DatabaseConnection;
import com.yourname.myapp.entity.AttendanceRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AttendanceRepository {
    private static final Logger logger = LoggerFactory.getLogger(AttendanceRepository.class);

    public void save(AttendanceRecord record) {
        String sql = "INSERT INTO attendance_record (employee_id, attendance_date, check_in_time, check_out_time, overtime_hours) " +
                     "VALUES (?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE check_in_time = VALUES(check_in_time), check_out_time = VALUES(check_out_time), overtime_hours = VALUES(overtime_hours)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, record.getEmployeeId());
            pstmt.setDate(2, Date.valueOf(record.getAttendanceDate()));
            pstmt.setTime(3, record.getCheckInTime() != null ? Time.valueOf(record.getCheckInTime()) : null);
            pstmt.setTime(4, record.getCheckOutTime() != null ? Time.valueOf(record.getCheckOutTime()) : null);
            pstmt.setDouble(5, record.getOvertimeHours());
            pstmt.executeUpdate();
            logger.info("Attendance record saved: {}", record.getId());
        } catch (SQLException e) {
            logger.error("Error saving attendance record", e);
            throw new RuntimeException("Failed to save attendance record", e);
        }
    }

    public List<AttendanceRecord> findAll() {
        String sql = "SELECT * FROM attendance_record";
        List<AttendanceRecord> records = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                records.add(mapRowToAttendanceRecord(rs));
            }
            return records;
        } catch (SQLException e) {
            logger.error("Error fetching all attendance records", e);
            throw new RuntimeException("Failed to fetch attendance records", e);
        }
    }

    public List<AttendanceRecord> findByEmployeeId(String employeeId) {
        String sql = "SELECT * FROM attendance_record WHERE employee_id = ?";
        List<AttendanceRecord> records = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, employeeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    records.add(mapRowToAttendanceRecord(rs));
                }
                return records;
            }
        } catch (SQLException e) {
            logger.error("Error finding attendance records by employee: {}", employeeId, e);
            throw new RuntimeException("Failed to find attendance records", e);
        }
    }

    public List<AttendanceRecord> findByDateRange(LocalDate from, LocalDate to) {
        String sql = "SELECT * FROM attendance_record WHERE attendance_date BETWEEN ? AND ?";
        List<AttendanceRecord> records = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(from));
            pstmt.setDate(2, Date.valueOf(to));
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    records.add(mapRowToAttendanceRecord(rs));
                }
                return records;
            }
        } catch (SQLException e) {
            logger.error("Error finding attendance records by date range", e);
            throw new RuntimeException("Failed to find attendance records by date range", e);
        }
    }

    public Optional<AttendanceRecord> findById(Long id) {
        String sql = "SELECT * FROM attendance_record WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToAttendanceRecord(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            logger.error("Error finding attendance record by ID: {}", id, e);
            throw new RuntimeException("Failed to find attendance record", e);
        }
    }

    private AttendanceRecord mapRowToAttendanceRecord(ResultSet rs) throws SQLException {
        AttendanceRecord record = new AttendanceRecord();
        record.setId(rs.getLong("id"));
        record.setEmployeeId(rs.getString("employee_id"));
        Date attendanceDate = rs.getDate("attendance_date");
        if (attendanceDate != null) {
            record.setAttendanceDate(attendanceDate.toLocalDate());
        }
        Time checkInTime = rs.getTime("check_in_time");
        if (checkInTime != null) {
            record.setCheckInTime(checkInTime.toLocalTime());
        }
        Time checkOutTime = rs.getTime("check_out_time");
        if (checkOutTime != null) {
            record.setCheckOutTime(checkOutTime.toLocalTime());
        }
        record.setOvertimeHours(rs.getDouble("overtime_hours"));
        return record;
    }
}