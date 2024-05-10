package DataTypes;

import java.io.Serializable;
import java.util.List;

public class Effort implements Serializable {
    private String effortValue;
    private String imgPath;


    public Effort(String effortValue, String imgPath) {
        this.effortValue = effortValue;
        this.imgPath = imgPath;
    }



    public String getEffortValue() {
        return effortValue;
    }

    public String getImgPath() {
        return imgPath;
    }
}
