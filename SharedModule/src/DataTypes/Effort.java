package DataTypes;

public class Effort
{
  private String effortValue;
  private String imgPath;

  public Effort(String effortValue, String imgPath)
  {
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
