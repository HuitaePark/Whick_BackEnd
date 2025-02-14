package com.dasom.whick.domain.Pressure;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PressureDto {
    private Integer pressure;  // 공기압 수치
    private String status;     // 상태 (예: "정상", "위험")
}