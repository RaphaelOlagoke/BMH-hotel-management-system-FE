package com.bmh.hotelmanagementsystem.BackendService.entities.HouseKeeping;

import com.bmh.hotelmanagementsystem.BackendService.enums.CleaningStatus;

public class UpdateCleaningLogRequest {

    private String ref;
    private CleaningStatus status;

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public CleaningStatus getStatus() {
        return status;
    }

    public void setStatus(CleaningStatus status) {
        this.status = status;
    }
}
