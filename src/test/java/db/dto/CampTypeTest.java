package db.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import view.message.ErrorCodeMessage;

class CampTypeTest {

    @DisplayName("문자로 Camp를 찾는다.")
    @Test
    void findByColor() {
        System.out.println(ErrorCodeMessage.FAIL_FIND);
        assertAll(
                () -> assertThat(CampType.findByColorName("WHITE")).isEqualTo(CampType.WHITE),
                () -> assertThat(CampType.findByColorName("BLACK")).isEqualTo(CampType.BLACK)
        );
    }
}
