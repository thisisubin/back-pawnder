package com.pawnder.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawnder.config.SessionUtil;
import com.pawnder.dto.PetProfileDto;
import com.pawnder.entity.Pet;
import com.pawnder.entity.User;
import com.pawnder.repository.PetRepository;
import com.pawnder.service.FileService;
import com.pawnder.service.PetService;
import com.pawnder.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pet")
@Tag(name = "Pet", description = "반려견 관련 API")
public class PetController {

    private final PetService petService;
    private final PetRepository petRepository;
    private final UserService userService;

    @Operation(summary = "나의 반려견 등록")
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> enrollPet(
            HttpSession session,
            @RequestPart("pet") String petJson,
            @RequestPart("profile") MultipartFile profileImage) {

        Map<String, Object> response = new HashMap<>();

        try {
            // 현재 로그인한 사용자 정보 가져오기
            String userId = SessionUtil.getLoginUserId(session);
            if (userId == null) {
                response.put("success", false);
                response.put("message", "로그인이 필요합니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // JSON 파싱
            ObjectMapper mapper = new ObjectMapper();
            PetProfileDto petProfileDto = mapper.readValue(petJson, PetProfileDto.class);

            // 반려견 등록
            petService.enrollPet(userId, petProfileDto, profileImage);

            response.put("success", true);
            response.put("message", "반려견 등록이 완료되었습니다.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("반려견 등록 중 오류 발생", e);
            response.put("success", false);
            response.put("message", "등록 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "나의 반려견 조회")
    @GetMapping("/profile/pets")
    public ResponseEntity<?> getUserPets(HttpSession session, Principal principal) {
        try {
            System.out.println("✅ /profile/pets 호출됨");
            System.out.println("세션 ID: " + (session != null ? session.getId() : "세션 없음"));
            System.out.println("Principal: " + principal);

            String userId = null;

            // 1. 세션에서 사용자 ID 확인 (일반 로그인)
            userId = SessionUtil.getLoginUserId(session);
            System.out.println("세션에서 가져온 userId: " + userId);

            // 2. Principal에서 사용자 ID 확인 (소셜 로그인)
            if (userId == null && principal != null) {
                userId = principal.getName();
                System.out.println("Principal에서 가져온 userId: " + userId);

                // 소셜 로그인 사용자의 경우 세션에 사용자 정보 저장
                try {
                    User user = userService.findByUserId(userId);
                    if (user != null) {
                        SessionUtil.setLoginUser(session, user);
                        System.out.println("소셜 로그인 사용자 세션 설정 완료: " + userId);
                    }
                } catch (Exception e) {
                    System.out.println("소셜 로그인 사용자 세션 설정 실패: " + e.getMessage());
                }
            }

            if (userId == null) {
                System.out.println("사용자 ID를 찾을 수 없음 - 401 반환");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
            }

            // 반려견 목록 조회
            List<PetProfileDto> pets = petService.getPetsByUserId(userId);
            System.out.println("조회된 반려견 수: " + pets.size());
            return ResponseEntity.ok(pets);

        } catch (Exception e) {
            System.out.println("반려견 조회 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("반려견 조회 중 오류가 발생했습니다.");
        }
    }

    /*@Operation(summary = "반려견 상세 정보 조회")
    @GetMapping("/{petId}")
    public ResponseEntity<Map<String, Object>> getPetDetail(@PathVariable Long petId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            String userId = getCurrentUserId(request);
            if (userId == null) {
                response.put("success", false);
                response.put("message", "로그인이 필요합니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            Optional<PetProfileDto> petOptional = petService.getPetsByUserId(userId);

            if (petOptional.isPresent()) {
                response.put("success", true);
                response.put("pet", petOptional.get());
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "반려견 정보를 찾을 수 없습니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

        } catch (Exception e) {
            log.error("반려견 상세 정보 조회 중 오류 발생", e);
            response.put("success", false);
            response.put("message", "반려견 정보 조회 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }*/
    @GetMapping("/{petId}")
    public ResponseEntity<PetProfileDto> getPet(@PathVariable String petId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new IllegalArgumentException("해당 반려견을 찾을 수 없습니다."));
        PetProfileDto dto = PetProfileDto.fromEntity(pet);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "반려견 정보 수정")
    @PutMapping("/edit/{petId}")
    public ResponseEntity<Map<String, Object>> updatePet(
            @PathVariable String petId,
            @RequestPart("pet") String petJson,
            @RequestPart(value = "profile", required = false) MultipartFile profileImage,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        //유저 아이디를 세션에서 가져와야 하는지,, 다시 생각해보기
        try {
            String userId = SessionUtil.getLoginUserId(session);
            if (userId == null) {
                response.put("success", false);
                response.put("message", "로그인이 필요합니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            ObjectMapper mapper = new ObjectMapper();
            PetProfileDto petProfileDto = mapper.readValue(petJson, PetProfileDto.class);

            boolean updated = petService.updatePet(petId, userId, petProfileDto, profileImage);

            if (updated) {
                response.put("success", true);
                response.put("message", "반려견 정보가 수정되었습니다.");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "반려견 정보 수정에 실패했습니다.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

        } catch (Exception e) {
            log.error("반려견 정보 수정 중 오류 발생", e);
            response.put("success", false);
            response.put("message", "반려견 정보 수정 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "반려견 삭제")
    @DeleteMapping("/delete/{petId}")
    public ResponseEntity<Map<String, Object>> deletePet(@PathVariable String petId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        try {
            String userId = SessionUtil.getLoginUserId(session);
            if (userId == null) {
                response.put("success", false);
                response.put("message", "로그인이 필요합니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            boolean deleted = petService.deletePet(petId, userId);

            if (deleted) {
                response.put("success", true);
                response.put("message", "반려견 정보가 삭제되었습니다.");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "반려견 정보 삭제에 실패했습니다.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

        } catch (Exception e) {
            log.error("반려견 정보 삭제 중 오류 발생", e);
            response.put("success", false);
            response.put("message", "반려견 정보 삭제 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}