package DataTypes;

import java.io.Serializable;
import java.util.List;

public class Effort implements Serializable {
    private String effortValue;
    private String imgPath;
    public static final List<String> LEGAL_EFFORT_VALUES = List.of("0", "1/2", "1", "2", "3", "5", "8", "13", "20", "40", "100", "?", "i");


    public Effort(String effortValue, String imgPath) {
        this.effortValue = effortValue;
        this.imgPath = imgPath;
    }

    public static List<String> getLegalEffortValues() {
        return LEGAL_EFFORT_VALUES;
    }



    public String getEffortValue() {
        return effortValue;
    }

    public String getImgPath() {
        return imgPath;
    }
}
