package com.pawnder.service;

import com.pawnder.constant.Role;
import com.pawnder.dto.UserLoginDto;
import com.pawnder.dto.UserSignUpDto;
import com.pawnder.entity.User;
import com.pawnder.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final StringRedisTemplate redisTemplate;
    private final JavaMailSender javaMailSender;

    private static final String EMAIL_CODE_PREFIX = "EMAIL_CODE:";
    private static final String VERIFIED_EMAIL_PREFIX = "VERIFIED_EMAIL:";
    private static final int CODE_EXPIRY_MINUTES = 2;
    private static final int VERIFICATION_EXPIRY_MINUTES = 10;

    public void sendVerificationEmail(String email) {
        if (!StringUtils.hasText(email)) {
            throw new IllegalArgumentException("이메일은 필수입니다.");
        }

        // 1. 이미 가입된 회원인지 중복 확인
        if (userRepository.existsByEmail(email)) {
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }

        // 2. 인증 코드 생성 (6자리 숫자, SecureRandom 사용)
        String code = generateVerificationCode();

        // 3. Redis에 저장 (TTL: 2)
        redisTemplate.opsForValue().set(
                EMAIL_CODE_PREFIX + code,
                email,
                CODE_EXPIRY_MINUTES,
                TimeUnit.MINUTES
        );

        // 4. 이메일 전송
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("[Pawnder] 이메일 인증 코드");
            message.setText("인증 코드는 " + code + "입니다. \n" +
                    CODE_EXPIRY_MINUTES + "분 내에 입력해주세요.");
            javaMailSender.send(message);

            log.info("인증 코드 이메일 발송 완료: {}", email);
        } catch (Exception e) {
            log.error("이메일 발송 실패: {}", email, e);
            throw new RuntimeException("이메일 발송에 실패했습니다.");
        }
    }

    public void verifyCode(String code) {
        if (!StringUtils.hasText(code)) {
            throw new IllegalArgumentException("인증 코드는 필수입니다.");
        }

        // 1. Redis에서 code에 해당하는 이메일 찾기
        String email = redisTemplate.opsForValue().get(EMAIL_CODE_PREFIX + code);
        if (email == null) {
            throw new IllegalStateException("유효하지 않거나 만료된 코드입니다.");
        }

        // 2. 인증된 이메일이라는 표시 저장
        redisTemplate.opsForValue().set(
                VERIFIED_EMAIL_PREFIX + email,
                "true",
                VERIFICATION_EXPIRY_MINUTES,
                TimeUnit.MINUTES
        );

        // 3. 인증에 사용된 코드 제거
        redisTemplate.delete(EMAIL_CODE_PREFIX + code);

        log.info("이메일 인증 완료: {}", email);
    }

    public User signUp(UserSignUpDto dto) {
        validateSignUpDto(dto);

        // 이메일 인증이 완료된 이메일인지 확인
        String verifiedEmail = redisTemplate.opsForValue().get(VERIFIED_EMAIL_PREFIX + dto.getEmail());
        if (verifiedEmail == null || !verifiedEmail.equals("true")) {
            throw new IllegalStateException("이메일 인증을 먼저 완료해주세요.");
        }

        // 1. 유효성 검사 (이메일 중복 등)
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalStateException("이미 가입된 회원의 이메일입니다.");
        }

        // 2. DTO -> ENTITY 변환 회원생성
        User user = new User();
        user.setId(dto.getId());
        user.setName(dto.getName());
        user.setUserId(dto.getUserId());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setBirth(dto.getBirth());
        user.setPhoneNm(dto.getPhoneNm());
        user.setVerified(true);
        user.setRole(Role.USER);

        // 3. 저장
        User savedUser = userRepository.save(user);

        // 4. 인증 완료 표시 제거
        redisTemplate.delete(VERIFIED_EMAIL_PREFIX + dto.getEmail());

        log.info("회원가입 완료: {}", dto.getEmail());
        return savedUser;
    }

    public Optional<User> login(UserLoginDto userLoginDto) {
        if (userLoginDto == null || !StringUtils.hasText(userLoginDto.getUserId()) ||
                !StringUtils.hasText(userLoginDto.getPassword())) {
            return Optional.empty();
        }

        try {
            // 사용자 조회
            Optional<User> userOptional = userRepository.findByUserId(userLoginDto.getUserId());

            if (userOptional.isPresent()) {
                User user = userOptional.get();

                // 비밀번호 확인
                if (passwordEncoder.matches(userLoginDto.getPassword(), user.getPassword())) {
                    log.info("로그인 성공: {}", userLoginDto.getUserId());
                    return Optional.of(user);
                } else {
                    log.warn("비밀번호 불일치: {}", userLoginDto.getUserId());
                }
            } else {
                log.warn("사용자를 찾을 수 없음: {}", userLoginDto.getUserId());
            }
        } catch (Exception e) {
            log.error("로그인 처리 중 오류 발생: {}", userLoginDto.getUserId(), e);
        }

        return Optional.empty();
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("해당 사용자를 찾을 수 없습니다. : " + userId));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUserId())
                .password(user.getPassword())
                .roles(user.getRole().toString())
                .build();
    }

    private String generateVerificationCode() {
        SecureRandom random = new SecureRandom();
        return String.format("%06d", random.nextInt(1000000));
    }

    private void validateSignUpDto(UserSignUpDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("회원가입 정보가 필요합니다.");
        }
        if (!StringUtils.hasText(dto.getName())) {
            throw new IllegalArgumentException("이름은 필수입니다.");
        }
        if (!StringUtils.hasText(dto.getUserId())) {
            throw new IllegalArgumentException("사용자 ID는 필수입니다.");
        }
        if (!StringUtils.hasText(dto.getEmail())) {
            throw new IllegalArgumentException("이메일은 필수입니다.");
        }
        if (!StringUtils.hasText(dto.getPassword())) {
            throw new IllegalArgumentException("비밀번호는 필수입니다.");
        }
    }
}