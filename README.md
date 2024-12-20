# 2. 기술 스택
### ✔️ Container & Orchestration
<img src="https://img.shields.io/badge/KUBERNETES-326CE5?style=for-the-badge&logo=Kubernetes&logoColor=white"><img src="https://img.shields.io/badge/DOCKER-2496ED?style=for-the-badge&logo=Docker&logoColor=white">
### ✔️ CI/CD
<img src="https://img.shields.io/badge/GITHUB ACTIONS-2088FF?style=for-the-badge&logo=githubactions&logoColor=white">

### ✔️ Infra
<img src="https://img.shields.io/badge/AWS-232F3E?style=for-the-badge&logo=amazonwebservices&logoColor=white"><img src="https://img.shields.io/badge/ELB-8C4FFF?style=for-the-badge&logo=awselasticloadbalancing&logoColor=white"><img src="https://img.shields.io/badge/EKS-FF9900?style=for-the-badge&logo=amazoneks&logoColor=white"><img src="https://img.shields.io/badge/EC2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white"><img src="https://img.shields.io/badge/RDS-527FFF?style=for-the-badge&logo=amazonrds&logoColor=white"><img src="https://img.shields.io/badge/API GATEWAY-FF4F8B?style=for-the-badge&logo=amazonapigateway&logoColor=white"><img src="https://img.shields.io/badge/LAMBDA-FF9900?style=for-the-badge&logo=awslambda&logoColor=white">
### ✔️ Monitoring & Notification
<img src="https://img.shields.io/badge/DISCORD-5865F2?style=for-the-badge&logo=discord&logoColor=white">

### ✔️ Static Analysis
<img src="https://img.shields.io/badge/SONARCLOUD-F3702A?style=for-the-badge&logo=sonarcloud&logoColor=white">

# 4. 아키텍처

- TEST, STAGING, PRODUCTION 환경의 아키텍처를 다르게 구성

## TEST

![아키텍처 설계도 _ TEST 환경](https://github.com/user-attachments/assets/59f00907-3e7d-4f3c-b059-c03bf2667ca9)


- ALB를 이용한 SSL/TLS 인증 수행
- public subnet 내 서버용 단일 EC2 인스턴스와 RDS 존재

## STAGING

![아키텍처 설계도 _ STAGING 환경](https://github.com/user-attachments/assets/042a87cd-7d65-456b-bf6f-a1a65c4148dc)


- EKS를 이용한 쿠버네티스 환경 설정
- 워커노드 2개를 default로 설정하여 파드 내 Spring Boot 컨테이너 실행
- ECR을 통한 이미지 관리
- API Gateway와 Lambda를 통한 함수 실행

## Production

![아키텍처 설계도 _ PRODUCTION 환경](https://github.com/user-attachments/assets/0363f812-d410-40dd-83be-bf9d8e7fff4f)


- private subnet에 프로덕션용 RDS 설정
- bastion host를 통한 RDS 관리 설정
