package com.dasom.whick.domain.Collision;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "collision") // 테이블 이름 지정 가능
public class Collision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본키 설정 및 자동 증가
    private Long id;

    private Long distance;
}