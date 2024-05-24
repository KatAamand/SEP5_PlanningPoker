package DataTypes;

import java.io.Serializable;

public class Task implements Serializable
{
  private int taskID;
  private String header;
  private String description;
  private String finalEffort;


  public Task(String taskHeader, String description) throws NullPointerException {
    if(taskHeader == null || description == null) {
      throw new NullPointerException();
    }

    setTaskHeader(taskHeader);
    setDescription(description);
    setFinalEffort(null);
  }

  public Task(int id, String taskName, String description) throws NullPointerException {
    if(taskName == null || description == null) {
      throw new NullPointerException();
    }

    setTaskID(id);
    setTaskHeader(taskName);
    setDescription(description);
    setFinalEffort(null);
  }

  public Task(int id, String header, String description, String finalEffort) {
    this.taskID = id;
    this.header = header;
    this.description = description;
    this.finalEffort = finalEffort;
  }

  public void setTaskID(int taskID) {
    this.taskID = taskID;
  }

  public int getTaskID() {
    return taskID;
  }

  public void setTaskHeader(String taskHeader) throws NullPointerException {
    if(taskHeader == null) {
      throw new NullPointerException();
    }
    this.header = taskHeader;
  }

  public String getTaskHeader() {
    return this.header;
  }


  public void setDescription(String desc) throws NullPointerException {
    if(desc == null) {
      throw new NullPointerException();
    }
    this.description = desc;
  }


  public String getDescription() {
    return this.description;
  }


  public void setFinalEffort(String effort) {

    this.finalEffort = effort;
  }


  public String getFinalEffort() {
    return this.finalEffort;
  }


  public Task copy() {
    Task copy = new Task(this.getTaskHeader(), this.getDescription());
    copy.setFinalEffort(this.getFinalEffort());
    copy.setTaskID(this.getTaskID());

    return copy;
  }


  public void copyAttributesFromTask(Task otherTask){
    this.setTaskHeader(otherTask.getTaskHeader());
    this.setDescription(otherTask.getDescription());
    this.setFinalEffort(otherTask.getFinalEffort());
    this.setTaskID(otherTask.getTaskID());
  }


  @Override public boolean equals(Object obj) {
    if(!(obj instanceof Task))
    {
      return false;
    }
    Task task = (Task) obj;
    if(this.getFinalEffort() == null && task.getFinalEffort() == null) {
      return (this.getTaskHeader().equals(task.getTaskHeader())
          && this.getDescription().equals(task.getDescription())
          && this.getTaskID() == (task.getTaskID())
      );
    } else if(this.getFinalEffort() == null || task.getFinalEffort() == null)
    {
      return false;
    } else {
      return (this.getTaskHeader().equals(task.getTaskHeader())
          && this.getDescription().equals(task.getDescription())
          && this.getFinalEffort().equals(task.getFinalEffort())
          && this.getTaskID() == (task.getTaskID())
      );
    }

  }
}
