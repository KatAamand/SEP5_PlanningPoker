package DataTypes;

import java.io.Serializable;
import java.util.List;

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
    setFinalEffort("Undefined");
  }

  public Task(int id, String taskName, String description) throws NullPointerException {
    if(taskName == null || description == null) {
      throw new NullPointerException();
    }

    setTaskID(id);
    setTaskHeader(taskName);
    setDescription(description);
    setFinalEffort("Undefined");
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


  public void setFinalEffort(String effort) throws NullPointerException, IllegalArgumentException {
    if (effort == null) {
      throw new NullPointerException();
    }

    if (!Effort.LEGAL_EFFORT_VALUES.contains(effort)) {
      throw new IllegalArgumentException("Invalid effort value");
    }

    this.finalEffort = effort;
  }


  public String getFinalEffort() {
    return this.finalEffort;
  }


  public Task copy() {
    Task copy = new Task(this.getTaskHeader(), this.getDescription());
    copy.setFinalEffort(this.getFinalEffort());

    return copy;
  }


  public void copyAttributesFromTask(Task otherTask){
    System.out.println("old header: " + this.getTaskHeader());
    System.out.println("new header: " + otherTask.getTaskHeader());
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
    return (this.getTaskHeader().equals(task.getTaskHeader())
        && this.getDescription().equals(task.getDescription())
        && this.getFinalEffort().equals(task.getFinalEffort())
    );
  }
}
