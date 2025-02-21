# 🤖 I.T.S (Immortal-Ticketing  Service)

---
![main_image](/assets/main_image.jpg)

## 🌈 서비스/프로젝트 소개

#### "쓰러지지 않는 콘서트 예매 서버"

> 관리자는 공연장을 등록하고 해당 공연장에 오픈할 콘서트를 등록할 수 있습니다.
> 회원은 원하는 가수의 콘서트를 예매하고 취소할 수 있습니다.
> 예매 서비스의 특성 상 동시 접속자가 많아지는 트래픽 환경을 고려합니다.
> 동시성 제어, 인덱스, 캐싱 등의 기법을 적용하여 안정적인 서비스를 구현합니다.

## 🔑 Key Summary

<details>
<summary>예매 시스템 성능 개선</summary>

- 예매 시스템은 콘서트 티켓 예매 서버에서 핵심적인 기능이고 순간적으로 많은 트래픽을
  처리해야하는 기능입니다.

- 예매 기능 중에 좌석을 임시 선택하는 부분이 병목 지점이 될 수 있음을 판단했고 단계적으로 성능을 개선했습니다.

- 성능테스트는 실제 환경과 유사하게 1초 동안 사용자가 같은 좌석을 예매하려는 상황을 가정하고 평균 응답 시간을
  계산했습니다.

> 1. Redis 분산락으로 동시성 제어한 상태

![main_image](/assets/redis_image.png)

- 성능 개선 전 평균 응답 시간 2163ms

> 2. 예매 내역 테이블 인덱스 추가

![main_image](/assets/redis_with_index_image.png)

- 인덱스 설정 후 평균 응답 시간 1713ms
- 인덱스 설정 후 평균 응답 시간 20.80% 개선

> 3. Redis 분산락으로 동시성 제어한 상태

![main_image](/assets/redis_with_index_cache_image.png)

- Redis 캐시를 이용해서 개선 후 평균 응답 시간 20ms
- 인덱스 개선에서 평균 응답 시간 98.83% 개선
- 성능 개선 전보다 평균 응답 시간 99.08% 개선

</details>

<details>
<summary>Test Code</summary>

<br>

> 1. 목표

이번 프로젝트에서는 기존 코드의 안정성을 보장하고, 애플리케이션의 확장성을 고려하여 `Test Code` 작성을
중점적으로 진행하여 코드 변경 시 예상치 못한 오류를 사전에 방지하고 유지보수성을 높이는 것이 목표입니다.

> 2. Tools

- Junit 5
- Mockito
- Jacoco

> 3. SQL 파일 활용

```JAVA
SqlGroup( {
	@Sql(
		value = "/sql/domain/concert/repository/setup.sql",
		config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.DEFAULT),
		executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS
	),
	@Sql(
		value = "/sql/domain/concert/repository/delete.sql",
		config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.DEFAULT),
		executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS
	)
})

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ConcertRepositoryTest {

	@Autowired
	private ConcertRepository concertRepository;

	@Autowired
	private ReservationRepository reservationRepository;
```

> 4. Code Coverage 측정

Code Coverage 를 통해서 기존 코드가 얼마나 안정적으로 운영되는지 알 수 있고,
작성된 코드의 양을 파악할 수 있는 장점에서 커버리지를 측정하였습니다.

- Code Coverage - 70% 달성
  ![main_image](/assets/jacoco_coverage_image.png)

</details>

## 🏢 인프라 설계도

---
![infra_image](/assets/infra_image.png)

## 👉 주요 기능

1. 👷‍♂️ **관리자**
    - 관리자는 공연장과 콘서트를 등록 가능
    - 관리자는 공연장과 콘서트에 필요한 이미지 등록 가능
    - 관리자는 공연장 예매 결과를 조회 가능


2. 🎪 **공연장/콘서트**
    - 해당 도메인 (공연장/콘서트) 가 등록될 때, AWS S3에 이미지를 같이 등록할 수 있으며,
      등록된 이미지는 수정 및 삭제가 가능


3. 👦 **유저**
    - 유저는 회원가입/로그인을 통해 인증/인가
    - 유저는 콘서트를 조회하며, 콘서트 자리 선정
    - 최종적으로 유저는 해당 자리에 대하여 예매 가능
    - 유저는 자신의 예매 내역을 확인 가능


4. 🎫 **좌석 예매**
    - 콘서트 좌석 예매
        - 유저는 콘서트를 예매할 때, `좌석 선택` → `좌석 선택 완료` 단계를 걸쳐 예매를 완료
        - 여기서 `좌석 선택` 단계 에서는 동 시간 내에 많은 요청이 오더라도 `Redis` 의 `분산락`
          이 적용되어 있어 `동시성 제어` 가 작동하며 데이터 불일치 해결
    - 콘서트 예매 내역 조회

## 🛠 기술 스택

### 1. 라이브러리 & 프레임 워크

<img src="https://img.shields.io/badge/Spring boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"> <img src="https://img.shields.io/badge/Spring Data jpa-6DB33F?style=for-the-badge&logo=&logoColor=white"> <img src="https://img.shields.io/badge/Spring Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white"> <img src="https://img.shields.io/badge/QueryDsl-2496ED?style=for-the-badge&logo=&logoColor=white"> <img src="https://img.shields.io/badge/Redisson-FF4438?style=for-the-badge&logo=&logoColor=white">

### 2. DB & Optimization

<img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white"> <img src="https://img.shields.io/badge/Redis-FF4438?style=for-the-badge&logo=redis&logoColor=white"> 

### 3. 인프라 & 배포

<img src="https://img.shields.io/badge/amazon web Service-232F3E?style=for-the-badge&logo=amazonwebservices&logoColor=white"> <img src="https://img.shields.io/badge/Amazon%20EC2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white"> <img src="https://img.shields.io/badge/Amazon%20S3-FF9900?style=for-the-badge&logo=amazons3&logoColor=white"> <img src="https://img.shields.io/badge/Amazon%20RDS-527FFF?style=for-the-badge&logo=amazonrds&logoColor=white">

### 4. CI/CD

<img src="https://img.shields.io/badge/github actions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white"> <img src="https://img.shields.io/badge/docker-2496ED?style=for-the-badge&logo=docker&logoColor=white">

### 5. 협업툴

<img src="https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white"> <img src="https://img.shields.io/badge/Slack-4A154B?style=for-the-badge&logo=slack&logoColor=white"> <img src="https://img.shields.io/badge/miro-050038?style=for-the-badge&logo=miro&logoColor=white"> <img src="https://img.shields.io/badge/notion-000000?style=for-the-badge&logo=notion&logoColor=white">

### 6. 언어 및 IDE

<img src ="https://img.shields.io/badge/Java jdk 17-007396.svg?&style=for-the-badge&logo=Java&logoColor=white"/> <img src ="https://img.shields.io/badge/intellijidea-000000.svg?&style=for-the-badge&logo=intellijidea&logoColor=white"/>

## 💬 기술적 의사 결정

- 🚧 **인증/인가 방식은 어떻게?**

- 🔀 **동시성 제어는 어떻게?**

- 🐳 **CI/CD는 어떻게?**

- [링크](https://www.notion.so/teamsparta/e160e43339284eed8b0672ff71940674?pvs=4#19d9f456236b49318dbd51bea54cec92)

## 📌 트러블슈팅

- ✌ **Query DSL N+1 문제 해결 및 성능 비교**
    - [링크](https://www.notion.so/teamsparta/d3e67a34cf04448ea440c25020901745?pvs=4)
- 🔒 **동시성 제어 (Lock)**
    - [링크](https://www.notion.so/teamsparta/b42c984fdf3244559bbc5db347eb7dc6?pvs=4)
- 📈 **인덱스로 성능 최적화**
    - [링크](https://www.notion.so/teamsparta/33ade95b2fe546da9ceabf9eb13f7f50?pvs=4)
- 🚀 **Redis 캐시로 동시성 제어 및 성능 개선**
    - [링크](https://www.notion.so/teamsparta/8d71d85d4300437287b9fd4e1738adf8?pvs=4)

## 와이어 프레임

- [링크](https://miro.com/app/board/uXjVLyRKUrI=/?share_link_id=1001838057)

## ERD

![erd_image](/assets/ERD_image.png)

## API 명세서

- [링크](https://www.notion.so/teamsparta/1932dc3ef51481e7ba5dca853c300cf8?v=1932dc3ef514811a8e6e000ced0e6315&pvs=4)

## 👍 Contributor

| 팀원명 | 포지션 | 담당(개인별 기여점)                                                                                                                                                                                                                                                                 | 깃허브 링크                                               |
|-----|-----|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------|
| 정선우 | 리더  | ▶ 유저 인증 인가 (Spring Security)<br> - JWT 토큰 인증/인가<br>  ▶ CI/CD<br> - EC2 + DOCKER + ECR을 이용한 CI/CD<br> - Docker Compose를 이용해 환경 구성<br> ▶ Redis를 이용한 동시성 제어 및 성능 최적화<br> - 'Redisson`을 이용하여 '분산락' 구현<br> - Redis를 사용하여 성능 최적화                                                  | [GitHub Seonu-Jeong](https://github.com/Seonu-Jeong) |
| 김태현 | 부리더 | ▶ 공연장 CRUD<br> - 좌석 관련 Bulk insert 적용<br> ▶ 이미지 로직 (AWS S3)<br> - 저장/수정/삭제 적용<br> ▶ 동시성 제어<br> - DB '네임드 락' 적용<br> ▶ Redis 성능 최적화 <br> -  Redis 를 사용하여 성능 최적화<br>                                                                                                           | [GitHub kimuky](https://github.com/kimuky)           |
| 장우태 | 팀원  | ▶ 콘서트 CRUD<br> -  콘서트 조회 시 Querydsl 과 Pageable 사용하여 조회<br> - 복잡한 쿼리 문 projections 사용하여 조회 대상을 지정해 원하는 값만 조회<br>  ▶ 테스트 코드<br> -콘서트, 공연장 관련 domain에 테스트 코드를 작성<br> - @SqlGroup 으로  sql 파일을 생성하여  테스트 실행하고 @BeforeEach 대신 사용하여 코드 가독성을 높임<br> - Jacoco를 활용하여 test coverage 측정 | [GitHub Jangutm600](https://github.com/Jangutm600)   |
| 허준  | 팀원  | ▶ 예매 관련 CRUD<br> - 예매, 취소 목록 조회 시 QueryDsl 과 Pageable 사용하여 조회<br> ▶ 테스트 코드<br> -  예매, 취소 목록, 유저, S3  테스트 작성 <br> - Jacoco를 활용하여 test coverage 측정                                                                                                                            | [GitHub huhjune98](https://github.com/huhjune98)     |



