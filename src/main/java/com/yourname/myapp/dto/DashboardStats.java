package com.yourname.myapp.dto;

/**
 * Dashboard statistics DTO containing aggregate employee data.
 */
public class DashboardStats {
    private long totalEmployeeCount;
    private long activeEmployeeCount;
    private long newJoinersCount;
    private long onLeaveCount;

    public DashboardStats() {}

    public DashboardStats(long totalEmployeeCount, long activeEmployeeCount, long newJoinersCount, long onLeaveCount) {
        this.totalEmployeeCount = totalEmployeeCount;
        this.activeEmployeeCount = activeEmployeeCount;
        this.newJoinersCount = newJoinersCount;
        this.onLeaveCount = onLeaveCount;
    }

    // Getters
    public long getTotalEmployeeCount() {
        return totalEmployeeCount;
    }

    public long getActiveEmployeeCount() {
        return activeEmployeeCount;
    }

    public long getNewJoinersCount() {
        return newJoinersCount;
    }

    public long getOnLeaveCount() {
        return onLeaveCount;
    }

    // Setters
    public void setTotalEmployeeCount(long totalEmployeeCount) {
        this.totalEmployeeCount = totalEmployeeCount;
    }

    public void setActiveEmployeeCount(long activeEmployeeCount) {
        this.activeEmployeeCount = activeEmployeeCount;
    }

    public void setNewJoinersCount(long newJoinersCount) {
        this.newJoinersCount = newJoinersCount;
    }

    public void setOnLeaveCount(long onLeaveCount) {
        this.onLeaveCount = onLeaveCount;
    }

    @Override
    public String toString() {
        return "DashboardStats{" +
                "totalEmployeeCount=" + totalEmployeeCount +
                ", activeEmployeeCount=" + activeEmployeeCount +
                ", newJoinersCount=" + newJoinersCount +
                ", onLeaveCount=" + onLeaveCount +
                '}';
    }
}
