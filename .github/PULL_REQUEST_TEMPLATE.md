### 📌 PR 개요
<!-- 이 PR이 어떤 기능/수정/버그패치인지 간략하게 설명해주세요 -->
ElasticSearch를 통한 유기동물 복합 조건 검색을 구현하였습니다.

---

### 🔨 작업 내용 요약
<!-- 주요 변경사항을 bullet 형식으로 요약해 주세요 -->
- 기능 추가: 로그인된 유저의 유기동물 복합 조건 검색 API (`GET /api/abandoned/abandoned-pets/search`)
- 🔄 전체 흐름
  1. Controller(`AbandonedPetController`)에서 검색 조건(`type`, `location`, `foundDate`)을 Query Parameter로 전달받음
  2. Service(`AbandonedPetSearchService`)에서 조건에 맞게 Elasticsearch DSL 쿼리를 동적으로 구성
  3. `ElasticsearchClient`를 사용해 검색 요청 수행
  4. 검색된 결과를 `AbandonedPetDocument` 리스트로 매핑하여 반환
- 🔍 검색 조건 및 쿼리 DSL 구성
  - 사용자가 입력한 조건은 모두 **AND 조건**(`bool.must`)으로 적용됨
  - 조건이 없으면 `match_all` 쿼리를 사용하여 전체 데이터를 조회함
  - 각 필드별 검색 방식은 다음과 같음:
    - `type`, `foundDate`: `Keyword` 타입이므로 `term` 쿼리 사용
    - `location`: `Text` 타입이므로 `match` 쿼리 사용
  - 예시 DSL 구조:
    ```json
    {
      "bool": {
        "must": [
          { "term": { "type": "비숑" } },
          { "match": { "location": "인천" } },
          { "term": { "foundDate": "2025-07-09" } }
        ]
      }
    }
    ```
---


### ✅ 상세 구현 내용
<!-- 상세하게 어떤 작업을 했는지 설명해주세요 -->
- Elasticsearch Java API Client를 활용하여 유기동물 복합 조건 검색 기능 구현
- 검색 조건: 품종(`type`), 발견 장소(`location`), 발견 날짜(`foundDate`)를 기반으로 검색 가능
  - `type`, `foundDate` 필드는 `Keyword` 타입 → `term` 쿼리 사용
  - `location` 필드는 `Text` 타입 → `match` 쿼리 사용
- 검색 조건은 선택적으로 입력할 수 있도록 구성 (AND 조건)
  - 조건이 하나도 없을 경우, 전체 데이터를 `match_all` 쿼리로 조회
- 검색 쿼리는 `BoolQuery`를 구성하여 `must` 절에 조건별 `Query` 추가
- 검색 결과는 `AbandonedPetDocument` 클래스로 매핑되어 리스트로 반환
- 검색 API: `GET /api/abandoned/abandoned-pets/search`
  - Query Parameter: `type`, `location`, `foundDate(yyyy-MM-dd)`
  - 로그인 사용자만 접근 가능하도록 설정 (Spring Security 적용)

---

### 🧪 테스트 방법
<!-- 어떻게 테스트했는지, 테스트 시나리오가 있다면 적어주세요 -->
1. Swagger 또는 Postman으로 로그인한 상태에서 아래 요청 수행
2. `POST /api/abandoned/register`
  - Body에 유기동물 정보(`latitude`, `longitude`, `imageUrl`, `gender`, `description`, `location`, `type`, `foundDate`, `foundTime`) 포함
  - 응답: `200 OK - 유기동물 제보 성공`
3. `POST /api/abandoned/admin/reports/{id}/`
  - 응답: `200 OK - 유기동물 등록 성공`
4. `GET /api/abandoned/abandoned-pets/search`
  - 응답: `200 [ {
    "id": 2,
    "type": "비숑",
    "gender": "string",
    "foundDate": "2025-07-09",
    "foundTime": "14:30",
    "description": null,
    "location": "string"
    } ]`

