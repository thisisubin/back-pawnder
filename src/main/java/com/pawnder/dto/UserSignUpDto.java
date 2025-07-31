package com.pawnder.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@Schema(description = "회원가입 요청 DTO")
public class UserSignUpDto {

    private Long id;

    @Schema(description = "사용자 이름", example = "홍길동")
    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;

    @Schema(description = "사용자 아이디", example = "usertest")
    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    private String userId;

    @Schema(description = "이메일 주소", example = "hong@example.com")
    @Email(message = "이메일 형식으로 입력해주세요.")
    private String email;

    @Schema(description = "비밀번호", example = "securePassword123!")
    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Length(min = 6, max = 16, message = "비밀번호는 6자 이상, 16자 이하로 입력해주세요.")
    private String password;

    @Schema(description = "생년월일", example = "yyyy-MM-dd")
    @NotNull(message = "생년월일은 필수 입력 값입니다.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birth;

    @Schema(description = "전화번호", example = "010-1234-1234")
    private String phoneNm;
}
