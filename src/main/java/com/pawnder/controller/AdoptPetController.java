package com.pawnder.controller;

import com.pawnder.config.DashboardSocketIOHandler;
import com.pawnder.config.SessionUtil;
import com.pawnder.dto.AdoptPetDto;
import com.pawnder.entity.AdoptPet;
import com.pawnder.repository.AdoptPetRepository;
import com.pawnder.service.AdoptPetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//입양 관련 Controller
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/adopt")
@Tag(name = "Adopt", description = "입양 관련 API")
public class AdoptPetController {

    private final AdoptPetService adoptPetService;
    private final AdoptPetRepository adoptPetRepository;
    private final DashboardSocketIOHandler dashboardSocketIOHandler;

    @Operation(summary = "입양 신청")
    @PostMapping("/apply/{petId}")
    public ResponseEntity<?> applyAdoption(@PathVariable Long petId, HttpSession session) {
        String userId = SessionUtil.getLoginUserId(session);
        //받은 petId를 가지고 서비스에서 AbandonedPetRepository에서 id찾아서
        //Status를 WAITING_ADOPT로 set
        adoptPetService.applyAdoption(petId, userId); //status = WAITING

        // 실시간 대시보드 업데이트 - 새 입양 신청
        dashboardSocketIOHandler.broadcastDashboardUpdate("pet_adopted",
                Map.of("message", "새로운 입양 신청이 접수되었습니다.", "petId", petId, "userId", userId));

        return ResponseEntity.ok("신청 완료");
    }

    @Operation(summary = "나의 입양 내역")
    @GetMapping("/adoption/my-applications")
    public ResponseEntity<?> myAdoption(HttpSession session) {
        List<AdoptPet> adoptPets = adoptPetRepository.findAll();

        List<AdoptPetDto> result = adoptPets.stream()
                .map(AdoptPetDto::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }
}
