package com.bmh.hotelmanagementsystem.BackendService.entities;


import com.bmh.hotelmanagementsystem.BackendService.enums.LoginDepartment;

public class UserFilterRequest {

    private String role;
    private boolean enabled;
    private LoginDepartment department;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public LoginDepartment getDepartment() {
        return department;
    }

    public void setDepartment(LoginDepartment department) {
        this.department = department;
    }
}
