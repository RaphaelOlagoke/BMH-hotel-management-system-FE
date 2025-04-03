package com.bmh.hotelmanagementsystem.BackendService.entities.hall;

import com.bmh.hotelmanagementsystem.BackendService.enums.HallLogStatus;

public class UpdateHallLogStatusRequest {

    private String ref;
    private HallLogStatus status;

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public HallLogStatus getStatus() {
        return status;
    }

    public void setStatus(HallLogStatus status) {
        this.status = status;
    }
}
