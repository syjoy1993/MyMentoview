# MyMentoview

기존 팀 프로젝트(Mentoview)를 기반으로  
**백엔드 아키텍처 개선, 도메인 재설계, 인프라 및 운영 관점 확장**을 목적으로 진행한 개인 포트폴리오 프로젝트입니다.

본 레포지토리는 **As-Is와 To-Be 구조를 명확히 분리**하여,
설계 개선 과정과 결과를 함께 보여주는 것을 목표로 합니다.

---

## 🧠 Mentoview - AI 면접 서비스
> 이력서를 기반으로 AI가 면접 질문을 생성하고
> 사용자의 응답을 분석하여 정량적·정성적 피드백을 제공하는 서비스

---

## 📌프로젝트 목적 (Why this project)
팀 프로젝트에서 경험한 구조적 한계와 책임 집중 문제를 바탕으로, 
다음 영역을 중심으로 백엔드 구조 재설계를 진행했습니다. 

- 결제 도메인 및 결제 flow 재설계
- Interview 도메인 구조 개선(Orchestrator 패턴 도입)
- 비동기 처리 흐름 재정리
- Prometheus / Grafana 기반 애플리케이션 모니터링 구조 추가 구성
  (지표 설계 및 운영 기준 정립은 진행 중)
  
단순 기능 구현이 아닌,
운영·확장·유지보수 가능성을 고려한 구조 실험에 초점을 두었습니다

---
## 🧩 Branch 전략
> 설계 변경 이력과 구조 개선 흐름을 함께 보여주기 위해 브랜치 전략을 분리했습니다.

| Branch      | Description                             |
| ----------- | --------------------------------------- |
| `main-asis` | 팀 프로젝트 당시의 원본 구조 (As-Is) |
| `main-tobe` | 개인 리팩토링 및 구조 개선 결과 (To-Be, **Default**) |
| `dev-saya`  | 기능 실험 및 점진적 개선 작업 |
| `feature/*` | 기능 단위 리팩토링 브랜치 |

포트폴리오 열람 시 main-tobe 브랜치를 기준으로 확인해주세요.
---

## 🧩 핵심 기능
- Form Login & OAuth2 기반 인증/인가 (Spring Security 6)
- 이력서 등록 및 OCR 처리
- AI 면접 질문 생성 및 응답 분석
- 인터뷰 상태 기반 도메인 설계
- 정기 구독 및 결제 (PortOne)
- Prometheus / Grafana 기반 모니터링 

---

## 💻기술 스택 (Tech Stack)
- Backend: Java 17, Spring Boot 3, Spring Security 6, Spring Data JPA, OpenAI, PDFBOX, Tesseract OCR, SWAGGER, Spring Reactive
- Frontend : React, JS ES6 , HTML5, CSS3 ,Redux, React-Query, Styled-Components
- DB: MySQL 8
- Infra: AWS EKS, S3, Lambda, Docker, Prometheus, Grafana
- CICD : GitHub Actions, ArgoCD
  
## ☁️배포 (Deployment)
- GitHub Actions CI/CD
- ArgoCD
- AWS CloudFormation(AWS EKS)

## dependency
- joe1534/mentoview-tesseract:v3 
- S3

## 모니터링 / 성능 (Optional)
- Prometheus
- Grafana

--- 

## ⚙️ 설정 관리

- 민감 정보는 GitHub에 commit하지 않으며,  
  **환경 변수 및 외부 설정으로 분리**하여 관리합니다.
---

## 💻 개발 환경 (Development Environment)

- Java 17 (Temurin)
- Spring Boot 3.4.3
- Gradle 8.12.1
- MySQL 8.0.40
- transcribe 2.30.26
- openapi
- awssdk V2
- s3:3.0.2
- pdfbox 3.0.3
- Tesseract 5.5.0
- Leptonica 1.85.0
- portone server-sdk 0.15.0
- webflux 
- prometheus
- jjwt jjwt-api:0.12.3/ jjwt-impl:0.12.3/ jjwt-jackson:0.12.3
- spring security 6 

---

### FrontEnd 
> 
### manifast
> 

