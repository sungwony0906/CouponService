쿠폰 관리 서비스
======================

## 1. 개발 프레임워크
```
Spring Boot 2.1.7 RELEASE
Spring Data JPA
```
  
## 2. Rest API
### 2.1 사용자 관리 서비스
* 회원가입
```
curl -v -X POST http://localhost:8080/api/signUp \ 
     -H "Content-Type: application/json" \ 
     -d '{ "userId" : "${USER_ID}", \ 
           "password" : "${USER_PW}", \ 
           "email" : "${USER_EMAIL}", \ 
           "name" : "${USER_NAME}" }' #
```
* 로그인
```
curl -v -X POST http://localhost:8080/api/signIn \ 
     -H "Content-Type: application/json" \ 
     -d '{ "userId" : "${USER_ID}", \ 
           "password" : "${USER_PW}"}' #
```

### 2.2 쿠폰 관리 서비스
* 쿠폰 생성
```
curl -v -X POST http://localhost:8080/api/coupon \ 
     -H "Content-Type: application/json" \ 
     -H "Authorization: Bearer ${TOKEN}" \
     -d '{ "count" : "${numOfCoupon}" }' #
```
* 쿠폰 지급
```
curl -v -X GET http://localhost:8080/api/coupon \ 
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer ${TOKEN}" 
```

* 지급된 쿠폰 조회
```
curl -v -X GET http://localhost:8080/api/coupons/issued \ 
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer ${TOKEN}" 
```

* 쿠폰 사용
```
curl -v -X PUT http://localhost:8080/api/coupon/{code} \ 
     -H "Content-Type: application/json" \ 
     -H "Authorization: Bearer ${TOKEN}" \
     -d '{ "status" : "USED" }' #
```

* 쿠폰 취소
```
curl -v -X PUT http://localhost:8080/api/coupon/{code} \ 
     -H "Content-Type: application/json" \ 
     -H "Authorization: Bearer ${TOKEN}" \
     -d '{ "status" : "CANCELED" }' #
```

* 당일 만료된 쿠폰 조회
```
curl -v -X GET http://localhost:8080/api/coupons/expired \ 
     -H "Content-Type: application/json" \ 
     -H "Authorization: Bearer ${TOKEN}"
```

## 3. 문제해결 전략
* 쿠폰 만료 메세지 발송
```
Batch Scheduler (CouponScheduler) 
매일 자정에 ExpireDate를 넘긴 쿠폰을 만료
ExpireDate를 기준으로 3일 전의 쿠폰을 조회하여 만료 메세지 발송
(실제 전송이 아닌 log.info)
```

* Token 기반 API 인증
```
java-jwt 라이브러리를 활용해 JWT 기반 API 인증 토큰 생성
JWT에 User정보를 저장하여 활용
JwtAuthInterceptor라는 HandlerInterceptor를 통해 요청 Intercept
토큰 검증 및 User정보를 HttpSession에 저장
```

* Password 암호화
```
StringCryptoConverter class에서 password 저장시 AES 암호화를 바탕으로 암호화 저장
```
## 4. 빌드 및 실행 방법
### Gradle 빌드
```
gradle build
```
### Jar 실행
```
java -jar build/libs/CouponService-1.0-SNAPSHOT.jar 
```
