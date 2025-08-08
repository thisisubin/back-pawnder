package com.pawnder.controller;

import com.pawnder.config.SessionUtil;
import com.pawnder.dto.UserLoginDto;
import com.pawnder.dto.UserSignUpDto;
import com.pawnder.entity.User;
import com.pawnder.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.Session;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.OptimisticLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "User", description = "회원 관련 API")
public class UserController {

    private final UserService userService;

    //이메일 인증
    @Operation(summary = "이메일 인증")
    @PostMapping("/send-email")
    public ResponseEntity<String> sendEmail(@RequestParam String email) {
        userService.sendVerificationEmail(email);
        return ResponseEntity.ok("인증 이메일을 전송했습니다.");
    }

    //이메일 인증 확인
    @Operation(summary = "이메일 인증 확인")
    @PostMapping("/verify-email")
    public ResponseEntity<String> verifyCode(@RequestParam String code) {
        userService.verifyCode(code);
        return ResponseEntity.ok("인증되었습니다.");
    }

    //회원가입
    @Operation(
            summary = "회원가입",
            description = "이름, 아이디, 이메일, 비밀번호, 생년월일로 회원가입을 진행합니다. 전화번호는 현재 필수 입력 값이 아닙니다.",
            responses = {
                @ApiResponse(responseCode = "200", description = "회원가입 성공"),
                @ApiResponse(responseCode = "400", description = "잘못된 요청")
            }
    )
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody UserSignUpDto userSignUpDto) {
        userService.signUp(userSignUpDto);
        return ResponseEntity.ok("회원가입 성공!");
    }

    //로그인
    @Operation(
            summary = "로그인",
            description = "아이디와 비밀번호로 로그인합니다.",
            responses = {
                @ApiResponse(responseCode = "200", description = "로그인 성공!"),
                @ApiResponse(responseCode = "400", description = "인증 실패")
            }
    )
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginDto userLoginDto, HttpServletRequest request) {
        Optional<User> user = userService.login(userLoginDto);

        if (user.isPresent()) {
            // 세션 생성
            HttpSession session = request.getSession();
            SessionUtil.setLoginUser(session, user.get());

            // 인증 처리
            UserDetails userDetails = userService.loadUserByUsername(userLoginDto.getUserId());
            UsernamePasswordAuthenticationToken authToken
                    = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            // SecurityContext 설정
            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(authToken);

            // 세션에 SecurityContext 저장
            session.setAttribute("SPRING_SECURITY_CONTEXT", context);

            return ResponseEntity.ok("로그인 성공!");
        } else {
            return ResponseEntity.status(400).body("아이디 또는 비밀번호가 올바르지 않습니다.");
        }
    }

    @Operation(summary = "로그인 여부")
    @GetMapping("/check-session")
    public ResponseEntity<?> checkSession(HttpSession session, Principal principal) {
        System.out.println("✅ /check-session 호출됨");
        System.out.println("세션 ID: " + (session != null ? session.getId() : "세션 없음"));
        System.out.println("Principal: " + principal);

        // 1. Spring Security Principal 확인
        boolean loggedIn = (principal != null);

        // 2. 세션에서 사용자 정보 확인
        User sessionUser = SessionUtil.getLoginUser(session);
        String sessionUserId = SessionUtil.getLoginUserId(session);

        System.out.println("세션 사용자: " + sessionUser);
        System.out.println("세션 사용자 ID: " + sessionUserId);

        Map<String, Object> res = new HashMap<>();

        if (loggedIn || sessionUser != null) {
            // 로그인된 상태
            res.put("loggedIn", true);
            res.put("username", principal != null ? principal.getName() : sessionUserId);

            // 소셜 로그인 사용자의 경우 세션에 사용자 정보 저장
            if (loggedIn && sessionUser == null) {
                try {
                    // Principal에서 사용자 정보를 가져와서 세션에 저장
                    String userId = principal.getName();
                    User user = userService.findByUserId(userId);
                    if (user != null) {
                        SessionUtil.setLoginUser(session, user);
                        System.out.println("소셜 로그인 사용자 세션 설정 완료: " + userId);
                        
                        // 소셜 로그인 정보 추가
                        res.put("provider", user.getProvider());
                        res.put("socialId", user.getSocialId());
                    }
                } catch (Exception e) {
                    System.out.println("소셜 로그인 사용자 세션 설정 실패: " + e.getMessage());
                }
            } else if (sessionUser != null) {
                // 일반 로그인 사용자
                res.put("provider", sessionUser.getProvider());
                res.put("socialId", sessionUser.getSocialId());
            }
        } else {
            // 로그인되지 않은 상태
            res.put("loggedIn", false);
            res.put("username", null);
        }

        return ResponseEntity.ok(res);
    }

}
