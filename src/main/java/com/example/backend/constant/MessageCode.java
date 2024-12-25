package com.example.backend.constant;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum MessageCode {
    REPORT_00("REPORT_00", "SUCCESS"),
    REPORT_01("REPORT_01", "FAILURE"),
    AUTH_02("AUTH_02", "ACCESS_DENIED"),
    REPORT_03("REPORT_03", "DELETE PREV REQUEST"),
    REPORT_04("REPORT_04", "DATE EXISTED"),
    REPORT_05("REPORT_05", "NOT FOUND"),
    REPORT_06("REPORT_06", "BAD REQUEST"),
    ;
    final String code;
    final String message;
    MessageCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
