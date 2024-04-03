package db.dto;

import java.util.Arrays;
import model.Camp;

public enum CampType {

    WHITE(Camp.WHITE, "WHITE"),
    BLACK(Camp.BLACK, "BLACK");

    private final Camp camp;
    private final String colorName;

    CampType(final Camp camp, final String colorName) {
        this.camp = camp;
        this.colorName = colorName;
    }

    public static CampType findByColorName(final String color) { // TODO enum 찾는 static 메서드명 통일하기
        return Arrays.stream(values())
                .filter(campType -> campType.colorName.equals(color))
                .findFirst()
                .orElseThrow();
    }

    public static CampType findByCamp(final Camp camp) {
        return Arrays.stream(values())
                .filter(campType -> campType.camp == camp)
                .findFirst()
                .orElseThrow();
    }

    public String getColorName() {
        return colorName;
    }

    public Camp getCamp() {
        return camp;
    }
}
