### 📌 PR 개요
<!-- 이 PR이 어떤 기능/수정/버그패치인지 간략하게 설명해주세요 -->
유저 프로필 기반 반려동물 등록 및 조회 기능을 구현하였습니다.

---

### 🔨 작업 내용 요약
<!-- 주요 변경사항을 bullet 형식으로 요약해 주세요 -->
- 기능 추가: 로그인된 유저의 반려동물 등록 API (`POST /api/pet/enroll`)
- 기능 추가: 유저 프로필의 반려동물 목록 조회 API (`GET /api/pet/profile/pets`)
- 기능 개선: `PetService`에서 DTO → Entity 변환 및 연관관계 설정
- 리팩토링: `UserService`에서 인증 유저 정보 기반 조회 로직 정리

---

### ✅ 상세 구현 내용
<!-- 상세하게 어떤 작업을 했는지 설명해주세요 -->
- `@AuthenticationPrincipal`을 활용하여 로그인된 유저의 userId를 기반으로 반려동물 등록
  - `PetProfileDto`를 받아 `Pet` Entity로 변환한 후 `User`와 연관지어 저장
- 반려동물 등록 시 `userRepository.findByUserId()`로 유저 식별 및 연관관계 설정
- 반려동물 목록 조회 시 `petRepository.findByUserUserId()` 메서드를 통해 해당 유저의 반려동물 조회
  - `Pet` Entity 리스트를 `PetProfileDto` 리스트로 변환 후 반환
- API 보안 강화를 위해 `/api/pet/**` 경로는 `SecurityConfig`에서 인증된 사용자만 접근 가능하도록 설정

---

### 🧪 테스트 방법
<!-- 어떻게 테스트했는지, 테스트 시나리오가 있다면 적어주세요 -->
1. Swagger 또는 Postman으로 로그인한 상태에서 아래 요청 수행
2. `POST /api/pet/enroll`
  - Body에 반려동물 정보(`name`, `type`, `age`, `weight`, `gender`, `size`, `adopt`) 포함
  - 응답: `200 OK - 반려동물 등록 성공`
3. `GET /api/pet/profile/pets`
  - 응답: 로그인된 사용자의 반려동물 리스트(JSON 배열 형태)

---

### 📷 스크린샷 (선택)
<!-- UI 작업을 했다면 캡처 첨부해주세요 -->
_(예: Swagger UI 요청 화면 등 필요 시 추가)_

---

### 🔍 중점적으로 봐줬으면 하는 부분
<!-- 리뷰어가 특히 신경써서 봐야 할 코드나 로직이 있다면 적어주세요 -->
- `PetService` 내 DTO → Entity 변환 및 연관관계 설정이 적절한지
- `PetController`에서 인증된 유저 정보 처리 흐름이 안전하고 자연스러운지
- DTO를 활용한 응답 구조가 API 응답에 적절하게 구성되었는지
- Security 설정에서 해당 API 접근이 적절히 보호되고 있는지

---

### 📎 관련 이슈
- Resolved: #반려동물-등록-조회-기능
