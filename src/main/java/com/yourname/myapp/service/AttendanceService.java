package com.yourname.myapp.service;

import com.yourname.myapp.entity.AttendanceRecord;
import com.yourname.myapp.repository.AttendanceRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class AttendanceService {

    public void logAttendance(String employeeId, LocalDate date, LocalTime checkIn, LocalTime checkOut) {
        double overtime = 0.0;
        if (checkIn != null && checkOut != null) {
            long totalMinutes = ChronoUnit.MINUTES.between(checkIn, checkOut);
            double totalHours = totalMinutes / 60.0;
            if (totalHours > 8) overtime = totalHours - 8;
        }
        AttendanceRecord record = new AttendanceRecord();
        record.setEmployeeId(employeeId);
        record.setAttendanceDate(date);
        record.setCheckInTime(checkIn);
        record.setCheckOutTime(checkOut);
        record.setOvertimeHours(Math.round(overtime * 100.0) / 100.0);
        new AttendanceRepository().save(record);
    }

    public List<AttendanceRecord> getAttendanceByEmployeeId(String employeeId) {
        return new AttendanceRepository().findByEmployeeId(employeeId);
    }

    public List<AttendanceRecord> getAttendanceByDateRange(LocalDate from, LocalDate to) {
        return new AttendanceRepository().findByDateRange(from, to);
    }

    public List<AttendanceRecord> getAllAttendance() {
        return new AttendanceRepository().findAll();
    }
}