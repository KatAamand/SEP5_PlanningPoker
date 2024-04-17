package DataTypes;

import java.io.Serializable;

public class Task implements Serializable
{
  private String name;
  private String description;
  private String finalEffort;



  public Task(String taskName, String description)
  {
    setTaskName(taskName);
    setDescription(description);
    setFinalEffort("Undefined");
  }


  public void setTaskName(String name)
  {
    this.name = name;
  }

  public String getTaskName()
  {
    return this.name;
  }



  public void setDescription(String desc)
  {
    this.description = desc;
  }

  public String getDescription()
  {
    return this.description;
  }



  public void setFinalEffort(String effort)
  {
    this.finalEffort = effort;
  }

  public String getFinalEffort()
  {
    return this.finalEffort;
  }



  public Task copy()
  {
    Task copy = new Task(this.getTaskName(), this.getDescription());
    copy.setFinalEffort(this.getFinalEffort());

    return copy;
  }



  @Override public boolean equals(Object obj)
  {
    if(!(obj instanceof Task))
    {
      return false;
    }
    Task task = (Task) obj;
    return (this.getTaskName().equals(task.getTaskName())
        && this.getDescription().equals(task.getDescription())
        && this.getFinalEffort().equals(task.getFinalEffort()));
  }
}
