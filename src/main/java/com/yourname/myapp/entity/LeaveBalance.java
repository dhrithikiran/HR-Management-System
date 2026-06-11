package com.yourname.myapp.entity;

/**
 * LeaveBalance POJO - Tracks leave balance per employee
 * 
 * Maps to: leave_balance table
 * Columns: id, employee_id, balance
 */
public class LeaveBalance {

    private Long id;
    private String employeeId;
    private int balance;

    public Long getId() { return id; }
    public String getEmployeeId() { return employeeId; }
    public int getBalance() { return balance; }

    public void setId(Long id) { this.id = id; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public void setBalance(int balance) { this.balance = balance; }

    public static LeaveBalance createDefault(String employeeId) {
        LeaveBalance b = new LeaveBalance();
        b.employeeId = employeeId;
        b.balance = 20;
        return b;
    }

    public static LeaveBalanceBuilder builder() { return new LeaveBalanceBuilder(); }

    public static class LeaveBalanceBuilder {
        private String employeeId;
        private int balance;

        public LeaveBalanceBuilder employeeId(String e) { this.employeeId = e; return this; }
        public LeaveBalanceBuilder balance(int b) { this.balance = b; return this; }

        public LeaveBalance build() {
            LeaveBalance lb = new LeaveBalance();
            lb.employeeId = this.employeeId;
            lb.balance = this.balance;
            return lb;
        }
    }
}