# 🐾 Pawnder

> 유기동물을 위한 입양·제보·커뮤니티 플랫폼


## 🎯 프로젝트 목표

**Pawnder**는 유기동물과 사람을 연결해주는 웹 서비스입니다.  
사용자는 유기견/유기묘를 제보하거나 입양 신청할 수 있으며, 입양 후기와 반려동물 이야기를 나눌 수 있는 커뮤니티 공간도 함께 제공합니다.

## 배포 구성
```
/home/ubuntu/
└── my-app/
    ├── backend/
    │   ├── pawnder-0.0.1-SNAPSHOT.jar
    │   └── Dockerfile
    │   └── start.sh 
    ├── frontend/
    │   └── react-app/
    │       └── build/ (React 앱 빌드 결과물)
    ├── .env
    ├── docker-compose.yml
    └── nginx.conf
```
## FrontEnd Repo
https://github.com/thisisubin/front-pawnder.git

## 📁 주요 디렉터리 설명

| 디렉터리 | 설명 |
|----------|------|
| `config/` | 보안 설정(Spring Security), Swagger, WebMvc 설정 등 |
| `constant/` | ENUM 정의 |
| `controller/` | REST API 엔드포인트 정의 |
| `dto/` | 요청/응답용 객체 정리 |
| `entity/` | 엔티티 정의 |
| `repository/` | JPA repository |
| `service/` | 핵심 비즈니스 로직 처리 |
