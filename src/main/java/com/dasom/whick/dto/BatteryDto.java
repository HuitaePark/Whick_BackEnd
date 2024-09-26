package com.dasom.whick.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BatteryDto {
    private Integer level;  // 배터리 잔량
    private String status;  // 상태 (예: "정상", "낮음")
}