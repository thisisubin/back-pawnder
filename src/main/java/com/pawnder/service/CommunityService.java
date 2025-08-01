package com.pawnder.service;

import com.pawnder.dto.CommunityPostDto;
import com.pawnder.entity.CommunityPost;
import com.pawnder.entity.User;
import com.pawnder.repository.CommunityPostRepository;
import com.pawnder.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommunityService {
    private final UserRepository userRepository;
    private final FileService fileService;
    private final CommunityPostRepository communityPostRepository;
    //1. 타입별로 글 포스트(저장) 하는 메서드 form -> DB
    public void savePost(CommunityPostDto communityPostDto, String userId, MultipartFile ImgUrlContent) throws IOException {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        CommunityPost communityPost = new CommunityPost();
        communityPost.setPostType(communityPostDto.getPostType());
        communityPost.setId(communityPostDto.getId());
        communityPost.setTitle(communityPostDto.getTitle());
        communityPost.setUser(user);
        communityPost.setStrContent(communityPostDto.getStrContent());
        communityPost.setImgUrlContent(communityPostDto.getImgUrlContent());


        // S3에 이미지 업로드
        // String imagUrl - S3Service. ~~~~

        //유저가 반려동물 프로필을 등록한 상태인지 확인 -> 카테고리에 반려동물 자랑하기만 보여야 함
        switch (communityPostDto.getPostType()) {
            case SHOW_OFF :
                if (user.getPets().isEmpty()) {
                throw new IllegalArgumentException("반려동물 등록 후 작성할 수 있습니다.");
            }
            case TEMP_PROTECT:
                if(user.getRole().equals("ADMIN")
                        //|| 임시보호 승인 상태
                ){
                    throw new IllegalArgumentException("임시보호를 승인받은 후 작성할 수 있습니다.");
                }
            case REVIEW:
                //입양 승인을 받은 상태
        }
        //유저가 임시보호 등록한 상태 || 유저가 관리자인지 확인 -> 카테고리에 입양 홍보만 보여야 함
        //유저가 입양승인을 받은 상태 -> 카테고리에 입양 후기만 보여야함


        //게시글 이미지 처리
        if (ImgUrlContent != null && !ImgUrlContent.isEmpty()) {
            String imgUrlContent = fileService.uploadFile(ImgUrlContent);
            communityPost.setImgUrlContent(imgUrlContent);
        }

        communityPostRepository.save(communityPost);
    }



    //4. 글 조회 메서드
    public CommunityPostDto getPostDetail(Long postId) {
        CommunityPost post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글 없음"));
        return CommunityPostDto.fromEntity(post); // 엔티티 → DTO 변환
    }

    //5. 전체 글 조회
    public List<CommunityPostDto> getAllPosts() {
        List<CommunityPost> postEntities = communityPostRepository.findAllByOrderByCreatedAtDesc();

        return postEntities.stream()
                .map(CommunityPostDto::fromEntity)
                .collect(Collectors.toList());
    }


    //6. 상세 글 수정 communityRepository에 있는 id를 가져와서 UPDATE 시켜준다
    @Transactional
    public boolean editPost(Long postId, CommunityPostDto communityPostDto, MultipartFile imgurlContent) throws IOException {
        //6-2. 기존 커뮤 상세글 조회
        CommunityPost communityPost = communityPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("커뮤니티 게시글이 없습니다."));

        //6-4. 필드 수정
        communityPost.setPostType(communityPostDto.getPostType());
        communityPost.setCreatedAt(communityPostDto.getCreatedAt());
        communityPost.setStrContent(communityPostDto.getStrContent());
        communityPost.setImgUrlContent(communityPostDto.getImgUrlContent());
        communityPost.setTitle(communityPostDto.getTitle());

        //6-5. 대표 이미지 변경 처리
        if (imgurlContent != null && !imgurlContent.isEmpty()) {
            //기존 이미지가 있다면 삭제
            if (communityPost.getImgUrlContent() != null && communityPost.getImgUrlContent().startsWith("/uploads/")) {
                fileService.deleteFile(communityPostDto.getImgUrlContent());
            }

            String saveFileName = fileService.uploadFile(imgurlContent);
            communityPost.setImgUrlContent(saveFileName);
        }

        //6-6. 저장
        communityPostRepository.save(communityPost);

        return true;
    }

    //7. 상세글 삭제
    @Transactional
    public boolean deletePost(Long postId) {

        //2. 커뮤 글 찾기
        CommunityPost communityPost = communityPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("커뮤니티 게시글을 찾을 수 없습니다."));

        //4. 대표 이미지 삭제
        if (communityPost.getImgUrlContent() != null && communityPost.getImgUrlContent().startsWith("/uploads/")) {
            fileService.deleteFile(communityPost.getImgUrlContent());
        }

        //5. 상세글 삭제
        communityPostRepository.delete(communityPost);
        return true;
    }

}
