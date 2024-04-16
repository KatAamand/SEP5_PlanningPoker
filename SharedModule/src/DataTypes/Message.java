package DataTypes;

public class Message
{
  private String message;



  public Message()
  {
    setMessage("");
    //TODO
  }



  public void setMessage(String message)
  {
    this.message = message;
  }

  public String getMessage()
  {
    return this.message;
  }



  @Override public boolean equals(Object obj)
  {
    if(!(obj instanceof Message))
    {
      return false;
    }
    Message msg = (Message) obj;
    return (this.getMessage().equals(msg.getMessage()));
  }



  public Message copy()
  {
    Message newMessage = new Message();
    newMessage.setMessage(this.getMessage());

    return newMessage;
  }
}
