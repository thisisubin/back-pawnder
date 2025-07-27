package com.pawnder.controller;

import com.pawnder.dto.AbandonPetFormDto;
import com.pawnder.entity.AbandonedPet;
import com.pawnder.entity.AbandonedPetDocument;
import com.pawnder.repository.AbandonedPetRepository;
import com.pawnder.repository.AbandonedPetSearchRepository;
import com.pawnder.service.AbandonPetService;
import com.pawnder.service.AbandonedPetSearchService;
import com.pawnder.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/abandoned")
@Tag(name = "AbandonPet", description = "유기동물 관련 API")
public class AbandonedPetController {
    private final FileService fileService;
    private final AbandonPetService abandonPetService;
    private final AbandonedPetRepository abandonedPetRepository;
    private final AbandonedPetSearchService abandonedPetSearchService;

    @Operation(summary = "유기동물 제보 (JSON)")
    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerAdoptPet(@AuthenticationPrincipal UserDetails userDetails,
                                              @RequestBody AbandonPetFormDto dto) {
        // imageUrl 필드는 나중에 넣거나, 임시 URL을 dto에 포함해도 됨
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 후 이용해주세요.");
        }//디버깅

        String userId = userDetails.getUsername(); // 또는 userDetails.getUserId() 등

        abandonPetService.save(dto, userId);
        return ResponseEntity.ok("유기 동물 등록 성공 (JSON)");
    }


    //유기 등록 (관리자)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "유기동물 제보 등록")
    @PostMapping("/admin/reports/{id}/register")
    public String registerAnimal(@PathVariable Long id) {
        abandonPetService.registerAsAbandonedPet(id);
        return "redirect:/admin/reports";
    }


    //유기 조회 (유저/관리자)
    @Operation(summary = "유기동물 제보리스트 (JSON)")
    @GetMapping("/abandoned-pets")
    public List<AbandonedPet> getAllAbandonedPets() {
        return abandonPetService.getAllAbandonedPets();
    }


    //Elasticsearch 적용 검색 필터링
    @Operation(summary = "유기동물 복합 조건 검색")
    @GetMapping("/abandoned-pets/search")
    public List<AbandonedPetDocument> searchAbandonedPets(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate foundDate) {

        return abandonedPetSearchService.search(type, location, foundDate);
    }


    @Operation(summary = "특정 유기동물 1마리에 대한 정보")
    @GetMapping("/abandoned-pets/{id}")
    public ResponseEntity<AbandonedPet> getAbandonedPetById(@PathVariable Long id) {
        AbandonedPet pet = abandonedPetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 유기동물이 없습니다."));
        return ResponseEntity.ok(pet);
    }
}
