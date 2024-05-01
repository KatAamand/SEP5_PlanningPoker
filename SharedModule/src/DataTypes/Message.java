package DataTypes;

import java.io.Serializable;

public class Message implements Serializable
{
  private String message;



  public Message(String message)
  {
    setMessage(message);

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
    Message newMessage = new Message(this.getMessage());
    return newMessage;
  }
}
