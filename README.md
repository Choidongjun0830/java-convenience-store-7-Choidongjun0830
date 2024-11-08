# java-convenience-store-precourse

## 구매자의 할인 혜택과 재고 상황을 고려하여 최종 결제 금액을 계산하고 안내하는 결제 시스템을 구현한다. 

## 기능 요구 사항
1. 사용자가 입력한 상품의 가격과 수량을 기반으로 최종 결제 금액을 계산
2. 총 구매액은 상품별 가격과 수량을 곱하여 계산
3. 프로모션 및 멤버십 할인 정책을 반영하여 최종 결제 금액을 산출
4. 구매 내역과 산출한 금액 정보를 영수증으로 출력
5. 영수증 출력 후 추가 구매를 진행할지 또는 종료할지 선택 가능
6. 사용자가 잘못된 값 입력시 IllegalArgumentException을 발생시키고, "[ERROR]"로 시작하는 에러 메시지 출력 후 그 부분부터 입력 다시 받기

### 재고 관리
1. 각 상품의 재고 수량을 고려하여 결제 가능 여부 확인
2. 고객이 상품을 구매할 때마다, 결제된 수량만큼 해당 상품의 재고에서 차감하여 수량 관리
3. 재고를 차감함으로써 시스템은 최신 재고 상태를 유지하며 다음 고객이 구매할 때 정확한 재고 정보를 제공

### 프로모션 할인
1. 오늘 날짜가 프로모션 기간 내에 포함된 경우에만 할인 적용
2. 프로모션은 N개 구매시 1개 무료 증정의 형태
3. 1+1 또는 2+1 프로모션이 각각 지정된 상품에 적용되며, 동일 상품에 여러 프로모션이 적용되지 않음. 
4. 프로모션 혜택은 프로모션 재고 내에서만 적용 가능
5. 프로모션 기간 중이라면 프로모션 재고를 우선적으로 차감하며, 프로모션 재고가 부족할 경우에는 일반 재고를 사용
6. 프로모션 적용이 가능한 상품에 대해 고객이 해당 수량보다 적게 가져온 경우, 필요한 수량을 추가로 가져오면 혜택을 받을 수 있음을 안내해야 함.
7. 프로모션 재고가 부족하여 일부 수량을 프로모션 혜택 없이 결제해야 하는 경우, 일부 수량에 대해 정가로 결제하게 됨을 안내해야 함.

### 멤버십 할인
1. 멤버십 회원은 프로모션 미적용 금액의 30%를 할인 받는다.
2. 프로모션 적용 후 남은 금액에 대해 멤버십 할인을 진행한다.
3. 멤버십 할인의 최대 한도는 8000원이다. 

### 영수증 출력
1. 영수증은 고객의 구매 내역과 할인을 요약하여 출력한다.

#### 영수증 항목
1. 구매 상품 내역: 구매한 상품명, 수량, 가격
2. 증정 상품 내역: 프로모션에 따라 무료로 제공된 증정 상품의 목록
3. 금액 정보 - 총 구매액: 구매한 상품의 총 수량과 총 금액, 행사할인: 프로모션에 의해 할인된 금액, 멤버십할인: 멤버십에 의해 추가로 할인된 금액, 내실돈: 최종 결제 금액
4. 영수증의 구성 요소를 보기 좋게 정렬

## 입출력 요구 사항

### 입력
1. 구현에 필요한 상품 목록과 행사 목록을 파일 입출력을 통해 불러온다.
2. src/main/resources/product.md와 src/main/resources/promotions.md 파일을 이용한다. - 값 수정 가능
3. 구매할 상품과 수량을 입력받는다. 상품명, 수량은 하이픈(-)으로, 개별 상품은 대괄호([])로 묶어 쉼표(,)로 구분한다. - [콜라-10],[사이다-3]
4. 프로모션 적용이 가능한 상품에 대해 해당 수량보다 적게 가져온 경우, 그 수량만큼 추가 여부를 입력 받는다. - Y: 증정받을 수 있는 상품 추가, N: 추가X
5. 프로모션 재고가 부족하여 일부 수량을 프로모션 혜택 없이 결제해야 하는 경우, 일부 수량에 대해 정가로 결제할지 여부를 입력받는다. - Y: 일부 수량 정가 결제, N: 정가로 결제해야 하는 수량 만큼 제외 후 결제
6. 멤버십 할인 적용 여부 - Y: 할인 적용, N: 할인 적용 X
7. 추가 구매 여부 - Y: 재고가 업데이트된 상품 목록을 확인 후 추가 구매 진행, N: 구매 종료

### 출력
1. 환영 인사와 함께 상품명, 가격, 프로모션 이름, 재고를 안내, 재고가 0개이면, '재고 없음'을 출력
```
안녕하세요. W편의점입니다.
   현재 보유하고 있는 상품입니다.

- 콜라 1,000원 10개 탄산2+1
- 콜라 1,000원 10개
- 사이다 1,000원 8개 탄산2+1
- 사이다 1,000원 7개
- 오렌지주스 1,800원 9개 MD추천상품
- 오렌지주스 1,800원 재고 없음
- 탄산수 1,200원 5개 탄산2+1
- 탄산수 1,200원 재고 없음
- 물 500원 10개
- 비타민워터 1,500원 6개
- 감자칩 1,500원 5개 반짝할인
- 감자칩 1,500원 5개
- 초코바 1,200원 5개 MD추천상품
- 초코바 1,200원 5개
- 에너지바 2,000원 5개
- 정식도시락 6,400원 8개
- 컵라면 1,700원 1개 MD추천상품
- 컵라면 1,700원 10개

구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])
```

2. 프로모션 적용이 가능한 상품에 대해 고객이 해당 수량만큼 가져오지 않았을 경우, 혜택에 대한 안내 메시지 출력
```
현재 {상품명}은(는) 1개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)
```

3. 프로모션 재고가 부족하여 일부 수량을 프로모션 혜택 없이 결제해야하면, 일부 수량에 대해 정가로 결제할지 여부에 대한 안내 메시지 출력
```
현재 {상품명} {수량}개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)
```

4. 멤버십 할인 적용 여부
```
멤버십 할인을 받으시겠습니까? (Y/N)
```

5. 구매 상품 내역, 증정 상품 내역, 금액 정보를 출력
```
===========W 편의점=============
상품명		수량	금액
콜라		3 	3,000
에너지바 		5 	10,000
===========증	정=============
콜라		1
==============================
총구매액		8	13,000
행사할인			-1,000
멤버십할인			-3,000
내실돈			 9,000
```

6. 추가 구매 여부를 확인하기 위해 안내 문구 출력
```
감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)
```

## 예시 실행 결과
```
안녕하세요. W편의점입니다.
현재 보유하고 있는 상품입니다.

- 콜라 1,000원 10개 탄산2+1
- 콜라 1,000원 10개
- 사이다 1,000원 8개 탄산2+1
- 사이다 1,000원 7개
- 오렌지주스 1,800원 9개 MD추천상품
- 오렌지주스 1,800원 재고 없음
- 탄산수 1,200원 5개 탄산2+1
- 탄산수 1,200원 재고 없음
- 물 500원 10개
- 비타민워터 1,500원 6개
- 감자칩 1,500원 5개 반짝할인
- 감자칩 1,500원 5개
- 초코바 1,200원 5개 MD추천상품
- 초코바 1,200원 5개
- 에너지바 2,000원 5개
- 정식도시락 6,400원 8개
- 컵라면 1,700원 1개 MD추천상품
- 컵라면 1,700원 10개

구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])
[콜라-3],[에너지바-5]

멤버십 할인을 받으시겠습니까? (Y/N)
Y 

===========W 편의점=============
상품명		수량	금액
콜라		3 	3,000
에너지바 		5 	10,000
===========증	정=============
콜라		1
==============================
총구매액		8	13,000
행사할인			-1,000
멤버십할인			-3,000
내실돈			 9,000

감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)
Y

안녕하세요. W편의점입니다.
현재 보유하고 있는 상품입니다.

- 콜라 1,000원 7개 탄산2+1
- 콜라 1,000원 10개
- 사이다 1,000원 8개 탄산2+1
- 사이다 1,000원 7개
- 오렌지주스 1,800원 9개 MD추천상품
- 오렌지주스 1,800원 재고 없음
- 탄산수 1,200원 5개 탄산2+1
- 탄산수 1,200원 재고 없음
- 물 500원 10개
- 비타민워터 1,500원 6개
- 감자칩 1,500원 5개 반짝할인
- 감자칩 1,500원 5개
- 초코바 1,200원 5개 MD추천상품
- 초코바 1,200원 5개
- 에너지바 2,000원 재고 없음
- 정식도시락 6,400원 8개
- 컵라면 1,700원 1개 MD추천상품
- 컵라면 1,700원 10개

구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])
[콜라-10]

현재 콜라 4개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)
Y

멤버십 할인을 받으시겠습니까? (Y/N)
N

===========W 편의점=============
상품명		수량	금액
콜라		10 	10,000
===========증	정=============
콜라		2
==============================
총구매액		10	10,000
행사할인			-2,000
멤버십할인			-0
내실돈			 8,000

감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)
Y

안녕하세요. W편의점입니다.
현재 보유하고 있는 상품입니다.

- 콜라 1,000원 재고 없음 탄산2+1
- 콜라 1,000원 7개
- 사이다 1,000원 8개 탄산2+1
- 사이다 1,000원 7개
- 오렌지주스 1,800원 9개 MD추천상품
- 오렌지주스 1,800원 재고 없음
- 탄산수 1,200원 5개 탄산2+1
- 탄산수 1,200원 재고 없음
- 물 500원 10개
- 비타민워터 1,500원 6개
- 감자칩 1,500원 5개 반짝할인
- 감자칩 1,500원 5개
- 초코바 1,200원 5개 MD추천상품
- 초코바 1,200원 5개
- 에너지바 2,000원 재고 없음
- 정식도시락 6,400원 8개
- 컵라면 1,700원 1개 MD추천상품
- 컵라면 1,700원 10개

구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])
[오렌지주스-1]

현재 오렌지주스은(는) 1개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)
Y

멤버십 할인을 받으시겠습니까? (Y/N)
Y

===========W 편의점=============
상품명		수량	금액
오렌지주스		2 	3,600
===========증	정=============
오렌지주스		1
==============================
총구매액		2	3,600
행사할인			-1,800
멤버십할인			-0
내실돈			 1,800

감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)
N
``` 