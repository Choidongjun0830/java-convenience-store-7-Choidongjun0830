package store;

import static camp.nextstep.edu.missionutils.test.Assertions.assertNowTest;
import static camp.nextstep.edu.missionutils.test.Assertions.assertSimpleTest;
import static org.assertj.core.api.Assertions.assertThat;

import camp.nextstep.edu.missionutils.test.NsTest;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import store.constant.ExceptionMessage;

class ExceptionTest extends NsTest {

    @Test
    void 예외_테스트_재고_초과() {
        assertSimpleTest(() -> {
            runException("[컵라면-12]");
            assertThat(output()).contains(ExceptionMessage.STOCK_OVER_EXCEPTION);
        });
    }

    @Test
    void 예외_테스트_잘못된_대괄호_형식() {
        assertSimpleTest(() -> {
            runException("{콜라-1],[에너지바-5]");
            assertThat(output()).contains(ExceptionMessage.INVALID_FORMAT_EXCEPTION);
        });
    }

    @Test
    void 예외_테스트_잘못된_하이픈_형식() {
        assertSimpleTest(() -> {
            runException("[콜라:1],[에너지바-5]");
            assertThat(output()).contains(ExceptionMessage.INVALID_FORMAT_EXCEPTION);
        });
    }

    @Test
    void 예외_테스트_잘못된_컴마_형식() {
        assertSimpleTest(() -> {
            runException("[콜라-1].[에너지바-5]");
            assertThat(output()).contains(ExceptionMessage.INVALID_FORMAT_EXCEPTION);
        });
    }

    @Test
    void 예외_테스트_상품명_공백() {
        assertSimpleTest(() -> {
            runException("[ -1],[에너지바-5]");
            assertThat(output()).contains(ExceptionMessage.INVALID_FORMAT_EXCEPTION);
        });
    }

    @Test
    void 예외_테스트_상품수_0() {
        assertSimpleTest(() -> {
            runException("[콜라-0],[에너지바-5]");
            assertThat(output()).contains(ExceptionMessage.PURCHASE_AMOUNT_INVALID_EXCEPTION);
        });
    }

    @Test
    void 예외_테스트_상품수_음수() {
        assertSimpleTest(() -> {
            runException("[콜라--1],[에너지바-5]");
            assertThat(output()).contains(ExceptionMessage.INVALID_FORMAT_EXCEPTION);
        });
    }

    @Test
    void 예외_테스트_존재하지_않는_상품() {
        assertSimpleTest(() -> {
            runException("[과자-1],[에너지바-5]");
            assertThat(output()).contains(ExceptionMessage.NOT_EXIST_PRODUCT_EXCEPTION);
        });
    }

    @Test
    void 예외_테스트_Y_N이_아닌_입력_1() {
        assertSimpleTest(() -> {
            runException("[콜라-3],[에너지바-5]", "i");
            assertThat(output()).contains(ExceptionMessage.INVALID_INPUT_EXCEPTION);
        });
    }

    @Test
    void 예외_테스트_Y_N이_아닌_입력_2() {
        assertSimpleTest(() -> {
            runException("[콜라-3],[에너지바-5]", "Y", "i");
            assertThat(output()).contains(ExceptionMessage.INVALID_INPUT_EXCEPTION);
        });
    }

    @Test
    void 예외_테스트_중복_상품_주문_재고_초과() {
        assertSimpleTest(() -> {
            runException("[사이다-3],[사이다-15]");
            assertThat(output()).contains(ExceptionMessage.STOCK_OVER_EXCEPTION);
        });
    }

    @Override
    public void runMain() {
        Application.main(new String[]{});
    }
}
