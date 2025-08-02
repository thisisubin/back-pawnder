### 📌 PR 개요
관리자가 입양 신청을 승인할 수 있는 기능을 구현했습니다.

---

### 🔨 작업 내용 요약
- 기능 추가: 입양 신청 승인 기능 구현 (`approveAdoption`)
- 수정: AdoptPet ID 기반으로 AbandonedPet, AbandonedPetForm 상태 변경
- 프론트 연동: 승인 버튼 클릭 시 adoptPetId 기반으로 승인 요청 전송

---

### ✅ 상세 구현 내용
- `approveAdoption(Long adoptPetId)` 메서드에서 AdoptPet ID를 기준으로 입양 신청을 조회
- 해당 입양 신청과 연관된 AbandonedPet 및 AbandonedPetForm 엔티티 상태를 `ADOPT`로 변경
- AdoptPet 상태는 `APPROVED`로 변경되며 승인 시각도 함께 기록
- 기존에 잘못된 `petId` 기반 로직 제거하고 올바른 관계 기반 처리 방식 적용
- React 프론트엔드에서 "승인" 버튼 클릭 시 `adoptPetId`가 서버로 전달되도록 연동 완료
