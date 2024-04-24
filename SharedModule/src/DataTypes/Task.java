package DataTypes;

import java.io.Serializable;

public class Task implements Serializable
{
  private String name;
  private String description;
  private String finalEffort;
  private int taskNumber;



  public Task(String taskName, String description)
  {
    setTaskName(taskName);
    setDescription(description);
    setFinalEffort("Undefined");
    setTaskNumber(0);
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


  /** Task number refers to the index/order this task has been assigned, and is used when loading the tasks, so they appear in the created/assigned order - rather than at random. */
  public void setTaskNumber(int num) {
    this.taskNumber = num;
  }

  /** Task number refers to the index/order this task has been assigned, and is used when loading the tasks, so they appear in the created/assigned order - rather than at random. */
  public int getTaskNumber() {
    return this.taskNumber;
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
        && this.getFinalEffort().equals(task.getFinalEffort())
        && this.getTaskNumber() == task.getTaskNumber());
  }
}
