package com.yourname.myapp.facade;

import com.yourname.myapp.entity.LeaveBalance;
import com.yourname.myapp.entity.LeaveRequest;
import com.yourname.myapp.exception.LeaveBalanceExceededException;
import com.yourname.myapp.repository.LeaveRequestRepository;
import com.yourname.myapp.repository.LeaveBalanceRepository;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class LeaveManagementFacade {
    
    private final LeaveRequestRepository leaveRequestRepo = new LeaveRequestRepository();
    private final LeaveBalanceRepository leaveBalanceRepo = new LeaveBalanceRepository();

    public void approveLeave(Long leaveRequestId) {
        // Fetch leave request
        Optional<LeaveRequest> requestOpt = leaveRequestRepo.findById(leaveRequestId);
        if (!requestOpt.isPresent()) throw new RuntimeException("Leave request not found.");
        LeaveRequest request = requestOpt.get();
        
        if (request.getLeaveStatus() != LeaveRequest.LeaveStatus.PENDING)
            throw new RuntimeException("Only PENDING requests can be approved.");

        long daysRequested = ChronoUnit.DAYS.between(
            request.getLeaveFromDate(), request.getLeaveToDate());

        // Get or create balance
        Optional<LeaveBalance> balanceOpt = leaveBalanceRepo.findByEmployeeId(request.getEmployeeId());
        LeaveBalance balance = balanceOpt.orElseGet(() -> LeaveBalance.createDefault(request.getEmployeeId()));

        if (!balanceOpt.isPresent()) {
            // New balance was created, save it
            leaveBalanceRepo.save(balance);
        }

        // Check total days - must not exceed 20
        if (balance.getBalance() <= 0) {
            throw new LeaveBalanceExceededException(
                "Cannot approve! Employee has used all 20 leave days.");
        }

        if (daysRequested > balance.getBalance()) {
            throw new LeaveBalanceExceededException(
                "Cannot approve! Requested " + daysRequested + 
                " days but only " + balance.getBalance() + 
                " days remaining out of 20.");
        }

        // Deduct balance
        balance.setBalance(balance.getBalance() - (int) daysRequested);
        request.setLeaveStatus(LeaveRequest.LeaveStatus.APPROVED);
        leaveBalanceRepo.save(balance);
        leaveRequestRepo.save(request);
    }

    public void rejectLeave(Long leaveRequestId) {
        Optional<LeaveRequest> requestOpt = leaveRequestRepo.findById(leaveRequestId);
        if (!requestOpt.isPresent()) throw new RuntimeException("Leave request not found.");
        LeaveRequest request = requestOpt.get();
        
        if (request.getLeaveStatus() != LeaveRequest.LeaveStatus.PENDING)
            throw new RuntimeException("Only PENDING requests can be rejected.");
        request.setLeaveStatus(LeaveRequest.LeaveStatus.REJECTED);
        leaveRequestRepo.save(request);
    }
}