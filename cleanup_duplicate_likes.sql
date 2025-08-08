-- 중복된 좋아요 레코드 정리 스크립트
-- 이 스크립트는 같은 사용자가 같은 게시글에 대해 여러 번 좋아요를 누른 중복 레코드를 정리합니다.

-- 1. 중복 레코드 확인
SELECT 
    community_post_id, 
    user_id, 
    COUNT(*) as duplicate_count
FROM likes 
GROUP BY community_post_id, user_id 
HAVING COUNT(*) > 1;

-- 2. 중복 레코드 중 가장 오래된 것만 남기고 나머지 삭제
DELETE l1 FROM likes l1
INNER JOIN likes l2 
WHERE l1.id > l2.id 
  AND l1.community_post_id = l2.community_post_id 
  AND l1.user_id = l2.user_id;

-- 3. 정리 후 중복 확인
SELECT 
    community_post_id, 
    user_id, 
    COUNT(*) as count_after_cleanup
FROM likes 
GROUP BY community_post_id, user_id 
HAVING COUNT(*) > 1;

-- 4. 전체 좋아요 개수 확인
SELECT COUNT(*) as total_likes FROM likes; 