# MyMentoview
기존 팀 프로젝트를 기반, 나만의 기능 확장과 사용해보고 싶은 기술들을 실험하는 개인 개발용 프로젝트입니당~!!


## 🧠 Mentoview - AI 면접 서비스
> "이력서 기반으로 AI가 면접 질문을 생성하고, 응답을 분석하여 피드백을 제공합니다."  

## 📌프로젝트 개요 (Overview)
> 사용자가 이력서를 제출하면 AI가 면접을 진행하고, 응답 분석 결과를 피드백으로 제공합니다.

## 💻기술 스택 (Tech Stack)
- Backend: Java 17, Spring Boot 3, Spring Security 6, Spring Data JPA, JPA, OpenAI, PDFBOX, Tesseract OCR, SWAGGER, Spring Reactive
- Frontend : React, JS ES6 , HTML5, CSS3 ,Redux, React-Query, Styled-Components
- DB: MySQL 8
- Infra: AWS EKS, S3, Lambda, Docker, Prometheus, Grafana
- CICD : GitHub Actions, ArgoCD


## ⚙️기능 소개 (Features)
- 회원가입 (폼 & OAuth2)
- 이력서 등록 및 관리
- AI 면접 질문 자동 생성
- 응답 분석 및 피드백 제공
- 정기 구독 및 결제
- 모니터링

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
- 
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

### FrontEnd 
> 
### manifast
> 

### application.properties

```
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=
spring.datasource.username=
spring.datasource.password=

# JPA
spring.jpa.generate-ddl=false
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# Actuator

management.endpoint.prometheus.access=unrestricted
management.endpoint.health.show-details=always
management.endpoints.web.exposure.include=health,info,prometheus,metrics
management.server.port=9090
management.endpoints.web.base-path=/api/management

#prometheus

# aws
cloud.aws.credentials.access-key=
cloud.aws.credentials.secret-key=
cloud.aws.s3.bucket.name=
cloud.aws.region.static=ap-northeast-2
aws.lambda.header=
aws.lambda.secret-key=

spring.servlet.multipart.max-file-size=20MB
spring.servlet.multipart.max-request-size=20MB

# OAuth
spring.jwt.secret=
temporary-token-expiration: 120000
access-token-expiration: 3000000
refresh-token-expiration: 604800000

##
spring.task.scheduling.enabled=true

### registration
spring.security.oauth2.client.registration.google.client-name=google
spring.security.oauth2.client.registration.google.client-id
spring.security.oauth2.client.registration.google.client-secret

openai
spring.ai.openai.api-key=
spring.ai.openai.chat.options.model=gpt-4o-mini

portone
IMP_API_KEY=
PORTONE_WEBHOOK_SECRET=
NOTIFICATION_URL=http://?/api/webhook/payment

# tesseract
tesseract.tessdata.path=/usr/local/share/tessdata

#swagger
#springdoc.api-docs.path=/api/v3/api-docs springdoc.swagger-ui.path=/api/swagger-ui
#springdoc.api-docs.path=/v3/api-docs springdoc.swagger-ui.path=/swagger-ui
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.api-docs.path=/v3/api-docs

```
