package com.yourname.myapp.service;

import com.yourname.myapp.entity.LeaveBalance;
import com.yourname.myapp.entity.LeaveRequest;
import com.yourname.myapp.exception.InvalidDateRangeException;
import com.yourname.myapp.exception.LeaveBalanceExceededException;
import com.yourname.myapp.repository.LeaveBalanceRepository;
import com.yourname.myapp.repository.LeaveRequestRepository;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

public class LeaveService {

    public static class LeaveResult {
        public final boolean success;
        public final boolean lowBalanceWarning;
        public final int remainingBalance;
        public final String message;

        public LeaveResult(boolean success, boolean lowBalanceWarning, int remainingBalance, String message) {
            this.success = success;
            this.lowBalanceWarning = lowBalanceWarning;
            this.remainingBalance = remainingBalance;
            this.message = message;
        }
    }

    public LeaveResult createLeaveRequest(String employeeId, LocalDate from, LocalDate to) {
        if (!from.isBefore(to))
            throw new InvalidDateRangeException("Leave start date must be before end date.");

        long daysRequested = ChronoUnit.DAYS.between(from, to);

        LeaveBalanceRepository balRepo = new LeaveBalanceRepository();
        LeaveBalance balance = balRepo.findByEmployeeId(employeeId).orElseGet(() -> {
            LeaveBalance newBalance = LeaveBalance.createDefault(employeeId);
            balRepo.save(newBalance);
            return newBalance;
        });

        // Block if no balance left
        if (balance.getBalance() <= 0) {
            throw new LeaveBalanceExceededException(
                "Leave request denied! You have used all 20 leave days for this year.");
        }

        // Block if requested days exceed remaining balance
        if (daysRequested > balance.getBalance()) {
            throw new LeaveBalanceExceededException(
                "Leave request denied! Requested: " + daysRequested +
                " days but only " + balance.getBalance() + " days remaining out of 20.");
        }

        LeaveRequest request = new LeaveRequest();
        request.setEmployeeId(employeeId);
        request.setLeaveFromDate(from);
        request.setLeaveToDate(to);
        request.setLeaveStatus(LeaveRequest.LeaveStatus.PENDING);
        new LeaveRequestRepository().save(request);

        boolean lowBalance = balance.getBalance() - daysRequested <= 2;
        int remaining = (int)(balance.getBalance() - daysRequested);
        return new LeaveResult(true, lowBalance, remaining,
                lowBalance ? "Leave request submitted. Warning: only " + remaining + " days remaining out of 20!"
                           : "Leave request submitted successfully. " + remaining + " days remaining out of 20.");
    }

    public List<LeaveRequest> getLeavesByStatus(String status) {
        return new LeaveRequestRepository().findByStatus(status);
    }

    public List<LeaveRequest> getAllLeaves() {
        return new LeaveRequestRepository().findAll();
    }

    public long getPendingCount() {
        return new LeaveRequestRepository().countPending();
    }

    public Optional<LeaveRequest> findById(Long id) {
        return new LeaveRequestRepository().findById(id);
    }

    public int getBalance(String employeeId) {
        return new LeaveBalanceRepository().findByEmployeeId(employeeId)
                .map(LeaveBalance::getBalance).orElse(20);
    }
}