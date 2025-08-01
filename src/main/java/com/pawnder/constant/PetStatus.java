package com.pawnder.constant;

/* 유기견 상태 */
public enum PetStatus {
    PROTECTING, //유기견 승인
    ADOPT, //입양승인
    WAITING_ADOPTION, //입양대기 (관리자 제보리스트에 입양대기로 표시)
    LOST //유기견 대기(목록에서 제외)
}
