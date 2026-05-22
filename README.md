## 프로젝트 개요
수강 신청 시스템 REST API입니다.
크리에이터(강사)는 강의를 개설하고 수강 정원, 가격, 기간을 설정할 수 있습니다.
클래스메이트(수강생)는 원하는 강의에 수강 신청/결제/취소 등을 할 수 있습니다.
정원이 초과된 강의는 신청이 불가하며 신청 후 결제를 완료되어야 수강이 확정 됩니다.
크리에이터(강사)는 강의를 개설하고 수강 정원, 가격, 기간을 설정합니다.

## 기술 스택
- JAVA 17
- Spring Boot
- Spring Boot Data JPA
- H2 (인메모리 DB)
- Lombok

## 실행 방법
1. 프로젝트 클론: git clone
2. 프로젝트 실행: ./gradlew bootRun, ClassEnrollmentApplication.java 실행(IntelliJ)
3. H2 콘솔 접속: http://localhost:8080/h2-console / JDBC URL: jdbc:h2:mem:testdb

## 요구사항 해석 및 가정
- 인증/인가는 userId 파라미터로 전달하는 방식으로 구현
- 결제 확정은 외부 시스템 연동 없이 상태 변경으로 대체
    - 수강 신청 시 PENDING 상태로 저장 
    - 결제 완료 시 CONFIRMED 상태로 변경
- 강의 상태는 DRAFT -> OPEN -> CLOSED 순서만 가능

## 설계 결정과 이유
- 동시성 제어: 여러 사용자가 동시에 마지막 자리에 신청할 경우를 고려 
              -> 여러 정원 초과 방지를 위해 비관적 락 (PESSIMISTIC_WRITE) 적용
- 강의 상세 조회 시 현재 신청 인원 포함: Map으로 반환하여 강의(course) 정보, 현재 신청 인원(currentCount) 정보 함께 제공
- 정원 체크시 CANCELLED 상태 제외: 취소된 신청은 자리를 차지 하지 않기 때문에 제외하고 체크
- 
## 미구현 / 제약사항
- 인증/인가 미구현 (userId 파라미터로 대채)
-외부 결제 시스템 미연동
- 대기열(waitlist) 기능
- 강의 별 수강생 목록 조회(크리에이터 전용)

## AI 활용 범위
- ERD 설계: 직접 설계 후 피드백 및 확인
- Repository: 직접 작성
- Service 로직: 직접 설계, 구현 중 막히는 부분 힌트 요청
- 동시성 제어(findByIdWithLock), 강의 상세 Map 반환: Claude 코드 참고 후 이해하여 적용
- 테스트 코드: 패턴 배우며 직접 작성

## API 목록 및 예시
###User
| Method | URL | 설명 |
|--------|-----|------|
| POST | /users | 유저 생성 |
| GET | /users/{id} | 유저 조회 |

###Course
| Method | URL | 설명 |
|--------|-----|------|
| POST | /courses | 강의 등록 |
| GET | /courses | 강의 목록 조회 |
| GET | /courses/{id} | 강의 상세 조회 |
| PATCH | /courses/{id}/status | 강의 상태 변경 |

###Enrollment
| Method | URL | 설명 |
|--------|-----|------|
| POST | /enrollments | 수강 신청 |
| PATCH | /enrollments/{id}/confirm | 결제 확정 |
| PATCH | /enrollments/{id}/cancel | 수강 취소 |
| GET | /enrollments/my | 내 수강 목록 조회 |

## 데이터 모델 설명
###users
| 컬럼 | 타입 | 설명 |
|-----|------|-----|
| id | BIGINT | 기본키 |
| name | VARCHAR(10) | 이름 |
| email | VARCHAR(30) | 이메일 |

###course
| 컬럼 | 타입 | 설명 |
|-----|------|-----|
| id | BIGINT | 기본키 |
| title | VARCHAR(20) | 강의 제목 |
| description | VARCHAR(50) | 강의 설명 |
| price | INT | 가격 |
| max_capacity | INT | 최대 정원 |
| start_date | DATE | 시작일 |
| end+date | DATE | 종료일 |
| status | ENUM | DRAFT/OPEN/CLOSED |

###enrollment
| 컬럼 | 타입 | 설명 |
|-----|------|-----|
| id | BIGINT | 기본키 |
| user_id | BIGINT | 유저 FK |
| course_id | BIGINT | 강의 FK |
| enrolled_at | DATETIME | 신청 시간 |
| status | ENUM | PENDING/CONFIRMED/CANCELLED |

## 테스트 실행 방법
- 터미널: ./gradlew test
- IntelliJ: 테스트 파일 연 후, CourseServiceTest.java, EnrollmentServiceTest.java, UserServiceTest.java 파일 실행