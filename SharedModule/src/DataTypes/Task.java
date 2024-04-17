package DataTypes;

public class Task
{
  private String description;
  private String finalEffort;



  public Task(String description)
  {
    setDescription(description);
    setFinalEffort("Undefined");
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
    Task copy = new Task(this.getDescription());
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
    return (this.getDescription().equals(task.getDescription())
        && this.getFinalEffort().equals(task.getFinalEffort()));
  }
}
