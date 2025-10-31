물론이지. 아래는 네가 바로 README.md에 붙여넣을 수 있는 완성형 버전이야.
이건 “성능 실험용 커머스 코어”를 설명하는 문서로 구성되어 있고,
기획서 + API 명세 + 기술 요구사항 + 테스트 시나리오까지 포함돼 있어서
리포의 첫 진입점으로 쓰기에 적합해.

⸻


# 🏗️ Commerce Core (Product · Order · Stock)

> **Purpose:**  
> 최소한의 커머스 도메인을 기반으로 백엔드의 핵심 기술(트랜잭션, 동시성, 정합성, 성능)을 실험하기 위한 프로젝트입니다.  
> 상품(Product), 주문(Order), 재고(Stock) 세 가지 도메인만으로 완결된 실험 무대를 구성합니다.

---

## ⚙️ 목표

- 단순 CRUD가 아니라 **트랜잭션 경합, 동시성, 캐시, 정합성**을 실험할 수 있는 구조 설계
- JMeter / Prometheus / Grafana 등을 활용한 **성능 관찰 및 시각화**
- 실험을 통해 백엔드 아키텍처의 설계 결정이 성능에 미치는 영향을 체감

---

## 📦 도메인 개념

### 🛍️ Product (상품)

| 필드 | 타입 | 설명 | 제약 |
|------|------|------|------|
| id | Long | PK | auto |
| name | String | 상품명 | NOT NULL, 1–200 |
| price | BigDecimal | 판매가 | 0 이상 |
| status | enum(ProductStatus) | SALE / SOLD_OUT / HIDDEN | |
| createdAt | LocalDateTime | 생성일시 | now |
| updatedAt | LocalDateTime | 수정일시 | now |

> - 상품 상태와 재고는 분리 관리.
> - 상품은 판매 가능 여부를 나타내고, 재고는 실제 수량을 관리.

---

### 📦 Stock (재고)

| 필드 | 타입 | 설명 | 제약 |
|------|------|------|------|
| id | Long | PK | auto |
| productId | Long | 상품 FK | UNIQUE (1:1) |
| quantity | int | 현재 가용 재고 수량 | 0 이상 |
| updatedAt | LocalDateTime | 수정일시 | now |

> **핵심 메서드**
> - `decrease(int qty)` — 수량 차감, 0 미만 시 예외
> - `increase(int qty)` — 재고 복원
> - 트랜잭션/락 실험의 핵심 포인트

---

### 🧾 Order (주문)

| 필드 | 타입 | 설명 |
|------|------|------|
| id | Long | PK |
| orderNo | String | 주문번호 (yyyyMMddHHmmss + random 4자리) |
| buyerId | Long (nullable) | 구매자 ID (가짜 유저) |
| totalAmount | Long | 총 금액 |
| status | enum(OrderStatus) | NEW, FAILED_STOCK, CANCELED |
| createdAt | LocalDateTime | 주문 시각 |

#### OrderItem (주문상품)

| 필드 | 타입 | 설명 |
|------|------|------|
| id | Long | PK |
| orderId | Long | 주문 FK |
| productId | Long | 상품 FK |
| productName | String | 상품명 스냅샷 |
| unitPrice | Long | 단가 스냅샷 |
| quantity | int | 주문 수량 |
| lineAmount | Long | unitPrice × quantity |

> - 스냅샷을 남겨 조인 없는 조회 성능 실험 가능
> - v1에서는 단일 상품 주문만 허용

---

## 🧭 유스케이스

### UC-01. 상품 등록
- **입력**
  ```json
  { "name": "아이폰 17 프로", "price": 1990000, "initialStock": 10 }

	•	출력

{ "id": 1, "name": "아이폰 17 프로", "price": 1990000, "stockQuantity": 10 }


	•	비즈니스 로직
	1.	Product 생성
	2.	Stock 생성 (초기 수량 설정)

⸻

UC-02. 상품 조회
•	GET /api/v1/products?page=0&size=20
•	옵션 파라미터
•	name (포함 검색)
•	status (SALE/HIDDEN)
•	출력

{
"content": [
{ "id": 1, "name": "아이폰 17 프로", "price": 1990000, "stockQuantity": 10 }
]
}



⸻

UC-03. 주문 생성
•	입력

{
"buyerId": 1001,
"items": [
{ "productId": 1, "quantity": 2 }
]
}


	•	처리 흐름
	1.	상품 유효성 검증 (SALE 상태)
	2.	재고 차감 시도
	3.	주문/주문상품 생성
	4.	금액 합산 및 상태 설정
	•	출력

{
"orderId": 11,
"orderNo": "20251031-011",
"status": "NEW",
"totalAmount": 3980000
}


	•	예외
	•	재고 부족 → 409 (NOT_ENOUGH_STOCK)
	•	상품 비활성 → 409
	•	존재하지 않는 상품 → 404

⸻

UC-04. 주문 조회
•	GET /api/v1/orders/{id}
→ 주문 헤더 + 주문상품 리스트 반환

⸻

🧱 비즈니스 규칙
1.	재고가 0이면 주문 불가
2.	상품 상태가 SOLD_OUT/HIDDEN이면 주문 불가
3.	주문은 All-or-Nothing 트랜잭션
4.	주문 금액은 주문 시점 가격 기준
5.	주문 후 상품 가격이 변경되어도 영향 없음

⸻

🔐 트랜잭션 & 락 정책

단계	방식	목적
v1	비관적 락 (SELECT ... FOR UPDATE)	확실한 정합성 확보
v2	낙관적 락 / Redis 분산 락	성능 비교 실험


⸻

📊 로깅 포인트

이벤트	내용
주문 생성	요청 payload, 처리 시간(ms), 재고 차감 결과
재고 부족	productId, 남은 수량, timestamp
Lock 경쟁	threadId, 대기 시간

→ JMeter 로그와 매칭해서 latency 분석 가능

⸻

🧩 시드 데이터 (테스트용)

상품명	재고	비고
상품 A	50	Hot 상품 (경합 실험용)
상품 B	100	일반 상품
상품 C	0	품절 상태


⸻

🔬 테스트 시나리오

1️⃣ 단일 유저 시나리오
•	상품 등록 → 주문 생성 → 주문 조회
•	정상 흐름 검증

2️⃣ 동시 주문 시나리오
•	상품 A (재고 50)에 대해
•	200명 동시 주문 (1개씩)
•	기대 결과: 50 성공 / 150 실패
•	실험 포인트
•	overselling 발생 여부
•	lock wait / deadlock 발생 여부
•	평균 응답시간 비교

3️⃣ 재고 없음 시나리오
•	재고 0 → 주문 요청 → 409 반환
•	DB 접근 전 차단 여부 확인

4️⃣ 조회 부하 시나리오
•	/api/v1/products?page=0&size=20 1000RPS
•	캐시 적용 전/후 latency 비교

⸻

📈 확장 포인트 (v2 이후)

기능	실험 주제
결제 모듈	외부 I/O, timeout, retry, 보상 트랜잭션
포인트 적립	비동기 이벤트, Kafka Outbox
배송 요청	분산 트랜잭션, SAGA
CQRS / replica	읽기 부하 분산, consistency lag 실험


⸻

🧰 기술 스택 제안

분류	도구
Language	Java 21
Framework	Spring Boot 3.x
ORM	JPA / MyBatis (비교 실험)
DB	MySQL (Docker)
Cache	Redis (Docker)
Test	JMeter / Gatling
Metrics	Spring Actuator + Micrometer + Prometheus + Grafana


⸻

🧠 학습 포인트
•	트랜잭션과 락의 실제 동작을 눈으로 본다
•	동시성 실험을 통해 “정합성 vs 성능”의 trade-off를 체감한다
•	캐시, 쿼리, 비동기 구조가 시스템 자원에 미치는 영향 관찰
•	단순 도메인으로도 고도화 실험이 가능한 백엔드의 본질 훈련

⸻

“단순한 도메인으로, 복잡한 현실을 시뮬레이션한다.”
이 README는 커머스 시스템의 본질을 관찰하기 위한 성능 실험 무대의 스펙 문서입니다.

---

원하면 내가 이걸 기반으로  
👉 `docs/01-domain-design.md`, `docs/02-api-spec.md`, `docs/03-test-scenario.md`  
식으로 세분화된 버전도 만들어줄 수 있어.  
README는 깃허브 홈에 두고, 세부 문서는 `docs/`로 정리하는 방식.  
그렇게 확장할까?