package com.pawnder.controller;

import com.pawnder.config.SessionUtil;
import com.pawnder.constant.Role;
import com.pawnder.dto.AbandonPetFormDto;
import com.pawnder.dto.AdoptPetDto;
import com.pawnder.entity.AbandonedPet;
import com.pawnder.entity.AbandonedPetForm;
import com.pawnder.entity.AdoptPet;
import com.pawnder.entity.User;
import com.pawnder.repository.AbandonedPetFormRepository;
import com.pawnder.repository.AdoptPetRepository;
import com.pawnder.service.AbandonPetService;
import com.pawnder.service.AdoptPetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

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
        adoptPetService.approveAdoption(id);
        return ResponseEntity.ok("승인됨");
    }

}

