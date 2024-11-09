package store;

import camp.nextstep.edu.missionutils.test.NsTest;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static camp.nextstep.edu.missionutils.test.Assertions.assertNowTest;
import static camp.nextstep.edu.missionutils.test.Assertions.assertSimpleTest;
import static org.assertj.core.api.Assertions.assertThat;

class ApplicationTest extends NsTest {
    @Test
    void 파일에_있는_상품_목록_출력() {
        assertSimpleTest(() -> {
            run("[물-1]", "N", "N");
            assertThat(output()).contains(
                "- 콜라 1,000원 10개 탄산2+1",
                "- 콜라 1,000원 10개",
                "- 사이다 1,000원 8개 탄산2+1",
                "- 사이다 1,000원 7개",
                "- 오렌지주스 1,800원 9개 MD추천상품",
                "- 오렌지주스 1,800원 재고 없음",
                "- 탄산수 1,200원 5개 탄산2+1",
                "- 탄산수 1,200원 재고 없음",
                "- 물 500원 10개",
                "- 비타민워터 1,500원 6개",
                "- 감자칩 1,500원 5개 반짝할인",
                "- 감자칩 1,500원 5개",
                "- 초코바 1,200원 5개 MD추천상품",
                "- 초코바 1,200원 5개",
                "- 에너지바 2,000원 5개",
                "- 정식도시락 6,400원 8개",
                "- 컵라면 1,700원 1개 MD추천상품",
                "- 컵라면 1,700원 10개"
            );
        });
    }

    @Test
    void 상품_구매_입력_받기() {
        assertSimpleTest(() -> {
            run("[콜라-3],[에너지바-5]");
            assertThat(output());
        });
    }

    @Test
    void 멤버십_여부_입력_받기_Y() {
        assertSimpleTest(() -> {
            run("[콜라-3],[에너지바-5]", "Y", "N");
            assertThat(output());
        });
    }

    @Test
    void 멤버십_여부_입력_받기_N() {
        assertSimpleTest(() -> {
            run("[콜라-3],[에너지바-5]", "N", "N");
            assertThat(output());
        });
    }

    @Test
    void 프로모션_재고_부족() {
        assertSimpleTest(() -> {
            run("[콜라-4]", "N", "N", "N");
            assertThat(output());
        });
    }

    @Test
    void 프로모션_구매_수량_부족_1_플러스_1() {
        assertSimpleTest(() -> {
            run("[오렌지주스-3]", "Y", "Y", "N");
            assertThat(output());
        });
    }

    @Test
    void 프로모션_구매_수량_부족_2_플러스_1() {
        assertSimpleTest(() -> {
            run("[콜라-2]", "Y", "Y", "N");
            assertThat(output());
        });
    }

    @Test
    void 여러_개의_일반_상품_구매() {
        assertSimpleTest(() -> {
            run("[비타민워터-3],[물-2],[정식도시락-2]", "N", "N");
            assertThat(output().replaceAll("\\s", "")).contains("내실돈18,300");
        });
    }

    @Test
    void 전체_예시_테스트() {
        assertSimpleTest(() -> {
            run("[콜라-3],[에너지바-5]", "Y", "Y", "[콜라-10]", "Y", "N", "Y", "[오렌지주스-1]", "Y", "Y", "N");
            assertThat(output().replaceAll("\\s", "")).contains("내실돈9,000","내실돈8,000","내실돈1,800");
        });
    }

    @Test
    void 기간에_해당하지_않는_프로모션_적용() {
        assertNowTest(() -> {
            run("[감자칩-2]", "N", "N");
            assertThat(output().replaceAll("\\s", "")).contains("내실돈3,000");
        }, LocalDate.of(2024, 2, 1).atStartOfDay());
    }

    @Override
    public void runMain() {
        Application.main(new String[]{});
    }
}
