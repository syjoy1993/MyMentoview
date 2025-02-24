## 특이사항
### controller.dto : 클라이언트 <-> controller Dto입니다
  - request : 만약에 사용을 안하게 되신다면 가장 마지막에 삭제 해주시면 될것같습니다!
  - response : 응답용도로 만들었어요!
  - 각각 dto 들은 사용하시는 용도에 맞게 필드 변경부탁드립니당!
    

### Entity Interview - InterviewQuestion
- 현재 단방향인데, 추후 쿼리 사용보고 양방향 매핑이나, QueryDsl, 또는 JPQL로 쿼리 튜닝이 필요할것같습니다!
- 혹시 이외에 쿼리가 너무 복잡하거나 한번에 너무 많이 나간다 싶으시면 회의때 꼭 !! 말씀해주세요!!

### UserDto 
- Controller 레벨에서 항상 우리 유저가맞는지 인증이 필요합니다!!
- 이때 클라이언트에서 모두 토큰을 보냅니다!
- 컨트롤러에서 Userdetails를 사용해주시면 됩니다!! 그래서 Request Dto들에게 userId 필드가 없습니다!


### S3
- S3 Dependency 완료 
- S3Config 사용시 CLIv2 버전으로 사용해주세요
  - (안그러면 와르르 멘션됩니당....)

### 전반적으로 잘못된 부분이랑, 특히 메서드 제가 미리 만들어 둔 것들이 테스트를 일일히 해보지 못해서, 하시다가 오류 발생하면 꼭 말씀해주세요!!!
- 제가 최대한 노력은 했는데ㅠㅠ 에러 안나길,,, 간절히 바랍니다...

### 프로메테우스 설정 
      global:
      scrape_interval: 15s  # 데이터 수집 간격 (기본 15초)
      scrape_configs:
        - job_name: 'spring-boot-app'
          metrics_path: '/management/prometheus'
          static_configs:
        - targets: ['spring-boot-service:9090'] # EKS 환경시 변경

- 
