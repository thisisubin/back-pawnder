package com.pawnder.controller;

import com.pawnder.config.SessionUtil;
import com.pawnder.repository.AbandonedPetRepository;
import com.pawnder.service.AdoptPetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

 //입양 관련 Controller

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/adopt")
@Tag(name = "Adopt", description = "입양 관련 API")
public class AdoptPetController {
    private final AdoptPetService adoptPetService;

    //유

    //기능
    //1. 유저 -> 입양 신청시
    @Operation(summary = "입양 신청")
    @PostMapping("/apply/{petId}")
    public ResponseEntity<?> applyAdoption(@PathVariable Long petId, HttpSession session) {
        String userId = SessionUtil.getLoginUserId(session);
        //받은 petId를 가지고 서비스에서 AbandonedPetRepository에서 id찾아서
        //Status를 WAITING_ADOPT로 set
        adoptPetService.applyAdoption(petId, userId); //status = WAITING
        return ResponseEntity.ok("신청 완료");
    }


}
