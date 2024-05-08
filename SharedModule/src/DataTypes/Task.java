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
    //TODO: Update this method to only allow certain effort values to be saved, otherwise throw an exception.
    if(effort == null) {
      throw new NullPointerException();
    }

    // Below is commented out until we have defined what values are legal values to evaluate against here:
    /*ArrayList<String> legalEffortValues = new ArrayList<>();
    //TODO: Add legal effort values here:
    legalEffortValues.add("Undefined");
    legalEffortValues.add("THIS_IS_A_LEGAL_EFFORT_VALUE"); // <- Remove/Refactor this line.

    if(!legalEffortValues.contains(effort)) {
      throw new IllegalArgumentException();
    }*/

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
