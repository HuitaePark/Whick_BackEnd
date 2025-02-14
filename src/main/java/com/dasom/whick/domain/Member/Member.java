package com.dasom.whick.domain.Member;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "memberId",nullable = false, unique = true)
    private String memberId;
    @Column(name = "password",nullable = false)
    private String password;
    @Column(name = "name",nullable = false)
    private String memberName;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role")
    private MemberRole memberRole;


    @CreationTimestamp
    @Column(name = "createDate", updatable = false)
    private LocalDateTime createDate;

    @UpdateTimestamp
    @Column(name = "updateDate")
    private LocalDateTime updateDate;
}
