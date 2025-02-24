package com.dasom.whick.domain.Auth.dto;

import com.dasom.whick.domain.Member.Member;
import com.dasom.whick.domain.Member.MemberRole;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JoinRequestDto {

    @NotBlank(message = "로그인 아이디가 비어있습니다.")
    private String memberId;

    @NotBlank(message = "비밀번호가 비어있습니다.")
    private String password;
    private String passwordCheck;

    @NotBlank(message = "이름이 비어있습니다.")
    private String memberName;

    public Member toEntity(){
        return Member.builder()
                .memberId(this.memberId)
                .password(this.password)
                .memberName(this.memberName)
                .memberRole(MemberRole.USER)
                .build();
    }
}
