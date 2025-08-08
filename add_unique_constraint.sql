-- Likes 테이블에 복합 유니크 제약조건 추가
-- 이 스크립트는 기존 중복 데이터를 정리한 후 유니크 제약조건을 추가합니다.

-- 1. 기존 중복 데이터 정리 (가장 오래된 레코드만 유지)
DELETE l1 FROM likes l1
INNER JOIN likes l2 
WHERE l1.id > l2.id 
  AND l1.community_post_id = l2.community_post_id 
  AND l1.user_id = l2.user_id;

-- 2. 복합 유니크 제약조건 추가
ALTER TABLE likes 
ADD CONSTRAINT uk_likes_post_user UNIQUE (community_post_id, user_id);

-- 3. 제약조건 추가 확인
SELECT 
    CONSTRAINT_NAME,
    TABLE_NAME,
    COLUMN_NAME
FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE 
WHERE TABLE_NAME = 'likes' 
  AND CONSTRAINT_NAME = 'uk_likes_post_user'; 