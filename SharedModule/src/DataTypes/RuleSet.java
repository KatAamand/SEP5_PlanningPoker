package DataTypes;

public class RuleSet
{
  private final String header = "Gamerules";
  private final String body =
      "Planning Poker Rules:\n" +
          "• Each estimator receives a deck of cards to vote on estimates.\n" +
          "• The Product Owner presents an item for estimation.\n" +
          "• Estimators select a card representing their estimate.\n" +
          "• All cards are revealed simultaneously.\n" +
          "• Discussion follows to understand estimates.\n" +
          "• Repeat until consensus or time limit.\n" +
          "• Record the estimate for planning.";

  public String getHeader()
  {
    return header;
  }

  public String getBody()
  {
    return body;
  }
}
