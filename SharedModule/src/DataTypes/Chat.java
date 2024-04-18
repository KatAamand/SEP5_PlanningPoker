package DataTypes;

import java.io.Serializable;
import java.util.ArrayList;

public class Chat implements Serializable
{
  private ArrayList<Message> chatHistory;


  public Chat()
  {
    chatHistory = new ArrayList<>();
  }



  public boolean addMessage(Message msg)
  {
    return chatHistory.add(msg);
  }



  public boolean removeMessage(Message msg)
  {
    return chatHistory.remove(msg);
  }



  public Message getMessage(int index)
  {
    return chatHistory.get(index);
  }

  public Message getMessage(Message msg) throws NullPointerException
  {
    for (int i = 0; i < chatHistory.size(); i++)
    {
      if (chatHistory.get(i).equals(msg))
      {
        return chatHistory.get(i);
      }
    }
    throw new NullPointerException();
  }



  protected ArrayList<Message> getChatHistory()
  {
    return chatHistory;
  }



  @Override public boolean equals(Object obj)
  {
    if(!(obj instanceof Chat))
    {
      return false;
    }
    Chat newChat = (Chat) obj;
    return newChat.getChatHistory().equals(this.getChatHistory());
  }



  public Chat copy()
  {
    Chat newChat = new Chat();

    for (int i = 0; i < getChatHistory().size(); i++)
    {
      newChat.addMessage(this.getMessage(i));
    }
    return newChat;
  }
}
