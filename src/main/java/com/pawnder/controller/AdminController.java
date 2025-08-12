package com.pawnder.controller;

import com.pawnder.config.SessionUtil;
import com.pawnder.config.DashboardSocketIOHandler;
import com.pawnder.constant.Role;
import com.pawnder.dto.AbandonPetFormDto;
import com.pawnder.dto.AdoptPetDto;
import com.pawnder.dto.dashboard.AbandonedStatusResponse;
import com.pawnder.dto.dashboard.ApplyStatsResponse;
import com.pawnder.dto.dashboard.DonationStatsResponse;
import com.pawnder.entity.AbandonedPetForm;
import com.pawnder.entity.AdoptPet;
import com.pawnder.entity.User;
import com.pawnder.repository.AbandonedPetFormRepository;
import com.pawnder.repository.AdoptPetRepository;
import com.pawnder.service.AbandonPetService;
import com.pawnder.service.AdoptPetService;
import com.pawnder.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "관리자 관련 API")
public class AdminController {

    private final AbandonPetService abandonPetService;
    private final AbandonedPetFormRepository abandonedPetFormRepository;
    private final AdoptPetService adoptPetService;
    private final AdoptPetRepository adoptPetRepository;
    private final DashboardService dashboardService;
    private final DashboardSocketIOHandler dashboardSocketIOHandler;

    @Operation(summary = "유기동물 제보 등록 (관리자만)")
    @PostMapping("/reports/{id}/register")
    public ResponseEntity<?> registerAnimal(HttpSession session, @PathVariable Long id) {
        User user = SessionUtil.getLoginUser(session);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        if (user.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("관리자만 접근 가능합니다.");
        }

        try {
            abandonPetService.registerAsAbandonedPet(id);

            // 실시간 대시보드 업데이트
            dashboardSocketIOHandler.broadcastDashboardUpdate("dashboard_update",
                    Map.of("type", "pet_registered", "message", "새로운 유기동물이 등록되었습니다."));

            return ResponseEntity.ok("제보가 유기동물로 등록되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("등록 중 오류 발생: " + e.getMessage());
        }
    }

    //유기 조회 (유저/관리자)
    @Operation(summary = "유기동물 제보리스트 (JSON) 조회")
    @GetMapping("/abandoned-pets")
    public ResponseEntity<List<AbandonPetFormDto>> getAll() {
        List<AbandonedPetForm> forms = abandonedPetFormRepository.findAll();
        List<AbandonPetFormDto> dtoList = forms.stream()
                .map(AbandonPetFormDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/abandoned-pets/{id}")
    public ResponseEntity<AbandonPetFormDto> getAbandonedPetById(@PathVariable Long id) {
        Optional<AbandonedPetForm> optionalForm = abandonedPetFormRepository.findById(id);

        if (optionalForm.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        AbandonPetFormDto dto = new AbandonPetFormDto(optionalForm.get());

        return ResponseEntity.ok(dto);
    }

    //관리자 -> 입양 신청 조회
    @Operation(summary = "입양 신청 전체 조회 (관리자용)")
    @GetMapping("/adopt-applications")
    public ResponseEntity<List<AdoptPetDto>> getAllApplications() {
        List<AdoptPet> adoptPets = adoptPetRepository.findAll();

        List<AdoptPetDto> result = adoptPets.stream()
                .map(AdoptPetDto::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    // 관리자 -> 입양 승인시
    @Operation(summary = "유기견 입양 승인")
    @PostMapping("/adopt/approve/{id}")
    public ResponseEntity<?> approveAdoption(@PathVariable Long id) {
        try {
            adoptPetService.approveAdoption(id);

            // 실시간 대시보드 업데이트
            dashboardSocketIOHandler.broadcastDashboardUpdate("dashboard_update",
                    Map.of("type", "adoption_approved", "message", "입양이 승인되었습니다."));

            return ResponseEntity.ok("입양이 승인되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("승인 중 오류 발생: " + e.getMessage());
        }
    }


    /* 관리자 대시보드 영역 */
    @Operation(summary = "유기견 수 변화 통계")
    @GetMapping("/dashboard/abandoned-status")
    public ResponseEntity<?> dashPet() {
        // LOST, PROTECTING, WAITING, ADOPT 상태별 수량 리턴
        AbandonedStatusResponse response = dashboardService.getAbandonedPetStatus();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "후원금 누적치")
    @GetMapping("/dashboard/donations")
    public ResponseEntity<?> totalAmount() {
        // 후원금 총액, 월별 추이 등
        DonationStatsResponse response = dashboardService.getDonationStats();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "유기견 제보/입양 처리 현황")
    @GetMapping("/dashboard/reports-adoptions")
    public ResponseEntity<ApplyStatsResponse> dashApply() {
        // 제보 수 vs 입양 신청 수
        ApplyStatsResponse response = dashboardService.getApplicationStats();
        return ResponseEntity.ok(response);
    }
}
