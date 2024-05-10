package DataTypes;

import java.io.Serializable;

public class UserCardData implements Serializable {
    private String username;
    private String placedCard;

    public UserCardData(String username, String placedCard) {
        this.username = username;
        this.placedCard = placedCard;
    }

    public String getUsername() {
        return username;
    }

    public String getPlacedCard() {
        return placedCard;
    }
}
