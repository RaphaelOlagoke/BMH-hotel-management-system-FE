package com.bmh.hotelmanagementsystem.BackendService.entities;


import com.bmh.hotelmanagementsystem.BackendService.enums.LoginDepartment;

public class UpdateUserRequest {
     private String oldEmail;
     private String newEmail;

     private String username;
     private String newPassword;
     private String newRole;
     private boolean isEnabled;
     private LoginDepartment department;

     public String getOldEmail() {
          return oldEmail;
     }

     public void setOldEmail(String oldEmail) {
          this.oldEmail = oldEmail;
     }

     public String getNewEmail() {
          return newEmail;
     }

     public void setNewEmail(String newEmail) {
          this.newEmail = newEmail;
     }

     public String getUsername() {
          return username;
     }

     public void setUsername(String username) {
          this.username = username;
     }

     public String getNewPassword() {
          return newPassword;
     }

     public void setNewPassword(String newPassword) {
          this.newPassword = newPassword;
     }

     public String getNewRole() {
          return newRole;
     }

     public void setNewRole(String newRole) {
          this.newRole = newRole;
     }

     public boolean isEnabled() {
          return isEnabled;
     }

     public void setEnabled(boolean enabled) {
          isEnabled = enabled;
     }

     public LoginDepartment getDepartment() {
          return department;
     }

     public void setDepartment(LoginDepartment department) {
          this.department = department;
     }
}
