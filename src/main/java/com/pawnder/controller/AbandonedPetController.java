package com.pawnder.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawnder.config.SessionUtil;
import com.pawnder.constant.PetStatus;
import com.pawnder.dto.AbandonPetFormDto;
import com.pawnder.dto.DonationVerifyRequest;
import com.pawnder.dto.MyDonationDto;
import com.pawnder.dto.PetProfileDto;
import com.pawnder.entity.*;
import com.pawnder.repository.AbandonedPetFormRepository;
import com.pawnder.repository.DonationRepository;
import com.pawnder.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/abandoned")
@Tag(name = "AbandonPet", description = "유기동물 관련 API")
public class AbandonedPetController {

    private final AbandonPetService abandonPetService;
    private final AbandonedPetSearchService abandonedPetSearchService;
    private final AbandonedPetFormRepository abandonedPetFormRepository;
    private final CustomVisionService customVisionService;



    @Operation(summary = "유기동물 제보")
    @PostMapping(value = "/register")
    public ResponseEntity<Map<String, Object>> registerAbandonedPet(
            @RequestPart("abandoned-pet") String abandonedPetJson,
            @RequestPart("imageurl") MultipartFile imageurl,
            HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        try {
            // 현재 로그인한 사용자 정보 가져오기
            String userId = getCurrentUserId(request);
            if (userId == null) {
                response.put("success", false);
                response.put("message", "로그인이 필요합니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // JSON 파싱
            ObjectMapper mapper = new ObjectMapper();
            AbandonPetFormDto abandonPetFormDto = mapper.readValue(abandonedPetJson, AbandonPetFormDto.class);

            // 유기견 제보 등록
            abandonPetService.save(abandonPetFormDto, userId, imageurl);


            response.put("success", true);
            response.put("message", "유기견 등록이 완료되었습니다.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("유기견 등록 중 오류 발생", e);
            response.put("success", false);
            response.put("message", "등록 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    //Elasticsearch 적용 검색 필터링
    @Operation(summary = "유기동물 복합 조건 검색")
    @GetMapping("/abandoned-pets/search")
    public List<AbandonedPetDocument> searchAbandonedPets(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate foundDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime foundTime,
            @RequestParam(required = false) String location) {

        return abandonedPetSearchService.search(foundDate, foundTime, location);
    }

    //유기견 조회 (유저/관리자)
    @Operation(summary = "유기동물 승인 제보리스트 (JSON) 조회")
    @GetMapping("/abandoned-pets")
    public ResponseEntity<List<AbandonPetFormDto>> getAll() {
        List<PetStatus> statuses = List.of(PetStatus.PROTECTING, PetStatus.WAITING);
        List<AbandonedPetForm> pets = abandonedPetFormRepository.findByStatusIn(statuses);
        List<AbandonPetFormDto> dtoList = pets.stream()
                .map(AbandonPetFormDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    @Operation(summary = "AI 품종 예측 요청")
    @PostMapping("/predict")
    public ResponseEntity<Map<String, String>> predict(@RequestPart("imageurl") MultipartFile imageurl) throws IOException {
        byte[] imageBytes = imageurl.getBytes();
        String predictedBreed = customVisionService.getTopPrediction(imageBytes).getTagName();
        Map<String, String> result = Map.of("predictedBreed", predictedBreed);
        return ResponseEntity.ok(result);
    }

    private final IamportService iamportService;
    private final DonationRepository donationRepository;
    private final DonationService donationService;

    @Operation(summary = "유기견 후원결제")
    @PostMapping("/donation/{imp_uid}")
    public ResponseEntity<?> donation(@ModelAttribute DonationVerifyRequest request) {
        try {
            System.out.println("DonationVerifyRequest: " + request);
            Map<String, Object> paymentData = iamportService.getPaymentByImpUid(request.getImpUid());
            System.out.println("Payment Data: " + paymentData);

            String status = (String) paymentData.get("status");
            Integer amount = (Integer) paymentData.get("amount");

            if (!"paid".equals(status)) {
                return ResponseEntity.badRequest().body("후원 결제가 완료되지 않았습니다.");
            }
            if (!amount.equals(request.getAmount())) {
                return ResponseEntity.badRequest().body("결제 금액이 일치하지 않습니다.");
            }

            donationService.saveDonation(request);

            return ResponseEntity.ok("후원 결제가 성공적으로 저장되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();  // 예외 스택트레이스 출력
            return ResponseEntity.internalServerError().body("결제 검증 중 오류가 생겼습니다.");
        }

    }

    @Operation(summary = "나의 후원내역")
    @GetMapping("/donation/my-donations")
    public ResponseEntity<?> myDonations(HttpSession session) {
        try {
            String userName = SessionUtil.getLoginUserId(session);
            List<MyDonationDto> donations = donationRepository.findMyDonationsWithPet(userName);
            return ResponseEntity.ok(donations);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("후원내역 조회 중 오류가 생겼습니다.");
        }
    }

    /**
     * 현재 로그인한 사용자의 userId를 가져오는 헬퍼 메서드
     */
    private String getCurrentUserId(HttpServletRequest request) {
        try {
            // 1. 세션에서 먼저 확인
            HttpSession session = request.getSession(false);
            if (session != null) {
                User sessionUser = SessionUtil.getLoginUser(session);
                if (sessionUser != null) {
                    return sessionUser.getUserId();
                }

                // 또는 String으로 저장된 userId 확인
                String userId = SessionUtil.getLoginUserId(session);
                if (userId != null) {
                    return userId;
                }
            }

            // 2. Spring Security에서 확인
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
                return auth.getName();
            }

            return null;
        } catch (Exception e) {
            log.error("사용자 ID 조회 중 오류 발생", e);
            return null;
        }
    }

}
