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
    void 예외_테스트() {
        assertSimpleTest(() -> {
            runException("[컵라면-12]");
            assertThat(output()).contains(ExceptionMessage.STOCK_OVER_EXCEPTION);
        });
    }

    @Override
    public void runMain() {
        Application.main(new String[]{});
    }
}
