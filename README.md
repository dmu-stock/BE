resources/application-local.yml 생성

spring:
  datasource:
    url: jdbc:postgresql://aws-1-ap-northeast-2.pooler.supabase.com:5432/postgres
    //환경변수 등록
    username: ${SUPABASE_NAME}
    password: ${SUPABASE_PW}
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        #        show_sql: true
        format_sql: true
        dialect: org.hibernate.dialect.PostgreSQLDialect
        default_batch_fetch_size: 100

//이거는 맘대로
server:
  port: 8082
hantu:
  appkey: ****
  appsecret: ****
  
