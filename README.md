# 1. 개요

Spring Boot 기반의 AI 서비스를 위한 확장 가능하고 안정적인 인프라스트럭처 구축 프로젝트입니다.   
Kubernetes(EKS)를 활용한 컨테이너 오케스트레이션과 GitHub Actions를 통한 자동화된 CI/CD 파이프라인을 구현했습니다.

<br>

# 2. 기술 스택
### ✔️ Container & Orchestration
<img src="https://img.shields.io/badge/DOCKER-2496ED?style=for-the-badge&logo=Docker&logoColor=white"><img src="https://img.shields.io/badge/KUBERNETES-326CE5?style=for-the-badge&logo=Kubernetes&logoColor=white">
### ✔️ CI/CD
<img src="https://img.shields.io/badge/GITHUB ACTIONS-2088FF?style=for-the-badge&logo=githubactions&logoColor=white">

### ✔️ Infra
<img src="https://img.shields.io/badge/AWS-232F3E?style=for-the-badge&logo=amazonwebservices&logoColor=white"><img src="https://img.shields.io/badge/ELB-8C4FFF?style=for-the-badge&logo=awselasticloadbalancing&logoColor=white"><img src="https://img.shields.io/badge/EKS-FF9900?style=for-the-badge&logo=amazoneks&logoColor=white"><img src="https://img.shields.io/badge/EC2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white"><img src="https://img.shields.io/badge/RDS-527FFF?style=for-the-badge&logo=amazonrds&logoColor=white"><img src="https://img.shields.io/badge/API GATEWAY-FF4F8B?style=for-the-badge&logo=amazonapigateway&logoColor=white"><img src="https://img.shields.io/badge/LAMBDA-FF9900?style=for-the-badge&logo=awslambda&logoColor=white">
### ✔️ Monitoring & Notification
<img src="https://img.shields.io/badge/DISCORD-5865F2?style=for-the-badge&logo=discord&logoColor=white">

### ✔️ Static Analysis
<img src="https://img.shields.io/badge/SONARCLOUD-F3702A?style=for-the-badge&logo=sonarcloud&logoColor=white">

<br>

# 3. 주요 특징
### 안정적인 서비스 운영을 위한 인프라 구성

- TEST/STAGING/PRODUCTION 환경 분리
- 환경별 독립적인 인프라 구성으로 안정성 확보
- GitHub Branch 전략과 연계한 자동화된 배포 프로세스 구현

### Blue/Green 무중단 배포 및 롤백

- Pod 레벨의 Blue/Green 배포 전략 구현
- 무중단 배포를 통한 서비스 가용성 확보
- 배포 실패 시 이전 버전으로 자동 롤백

### 자동화된 품질 관리

- GitHub Actions와 SonarCloud 연동
- 코드 품질 메트릭스 자동 분석
- 품질 기준 미달 시 배포 차단

### 모니터링 및 알림

- Discord를 통한 자동화된 배포 알림
- 배포 성공/실패 상세 정보 제공

### 보안 강화

- IAM을 통한 세분화된 권한 관리
- 환경별 분리된 보안 그룹 구성

<br>

# 4. 아키텍처

- TEST, STAGING, PRODUCTION 환경을 다르게 구성

## TEST

![아키텍처 설계도 _ TEST 환경](https://github.com/user-attachments/assets/59f00907-3e7d-4f3c-b059-c03bf2667ca9)


- ALB를 이용한 SSL/TLS 인증 수행
- public subnet 내 서버용 단일 EC2 인스턴스와 RDS 구축

## STAGING

![아키텍처 설계도 _ STAGING 환경](https://github.com/user-attachments/assets/042a87cd-7d65-456b-bf6f-a1a65c4148dc)


- EKS를 이용한 쿠버네티스 환경 구축
- 워커노드 2개를 default로 설정하여 파드 내 Spring Boot 컨테이너 실행
- ECR을 통한 이미지 관리
- API Gateway와 Lambda를 통한 함수 실행

## Production

![아키텍처 설계도 _ PRODUCTION 환경](https://github.com/user-attachments/assets/0363f812-d410-40dd-83be-bf9d8e7fff4f)


- private subnet 내 프로덕션용 RDS 구축
- bastion host를 통해 RDS 관리


<br>

# 5. CI/CD 프로세스 전략

![CD 프로세스](https://github.com/user-attachments/assets/240d2466-5999-400e-bea1-2972c0ef5c36)

- 개발자는 각자 브랜치에서 백로그 작업을 진행한 후, test 브랜치로 pr을 전송한다.
- 이때 sonar cloud를 통한 단위 테스트, 정적 분석이 수행된다.
- QA는 pr을 승인하고 백로그에 대한 통합 테스트를 진행한다.
- 통합 테스트 성공 시 QA는 staging 브랜치로 pr을 전송해 시스템 테스트를 진행한다.
- 통합 테스트 실패 시 이전 staging 환경으로 롤백한다.
- 시스템 테스트 성공 시 QA는 production 브랜치로 pr을 전송해 실제 운영 환경에 업데이트 사항을 적용한다.
- 시스템 테스트 실패 시 케이스에 따라 문제를 해결한다.
