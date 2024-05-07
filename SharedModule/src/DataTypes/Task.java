package DataTypes;

import java.io.Serializable;

public class Task implements Serializable
{
  private String name;
  private String description;
  private String finalEffort;


  public Task(String taskName, String description) throws NullPointerException {
    if(taskName == null || description == null) {
      throw new NullPointerException();
    }

    setTaskName(taskName);
    setDescription(description);
    setFinalEffort("Undefined");
  }


  public void setTaskName(String name) throws NullPointerException {
    if(name == null) {
      throw new NullPointerException();
    }
    this.name = name;
  }

  public String getTaskName() {
    return this.name;
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
    Task copy = new Task(this.getTaskName(), this.getDescription());
    copy.setFinalEffort(this.getFinalEffort());

    return copy;
  }


  @Override public boolean equals(Object obj) {
    if(!(obj instanceof Task))
    {
      return false;
    }
    Task task = (Task) obj;
    return (this.getTaskName().equals(task.getTaskName())
        && this.getDescription().equals(task.getDescription())
        && this.getFinalEffort().equals(task.getFinalEffort())
    );
  }
}
