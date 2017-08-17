package databeans;

import java.util.ArrayList;
import javax.persistence.AttributeOverride;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@AttributeOverride(name="id", column=@Column(name="DEPARTMENT_ID"))
@Table(name="DEPARTMENTS")
public class DepartmentFull extends AbstractTableFull {
  
  @Basic
  @Column(name="DEPARTMENT_NAME")
  private String name;

  @Basic
  @Column(name="MANAGER_ID")
  public Integer manager_ID;
    
  @Basic
  @Column(name="LOCATION_ID")
  public Integer location_ID;
  
  public DepartmentFull() {
    super("DEPARTMENTS");
  }
  
  public DepartmentFull(int id, String name, String editTag) {
    super(id, "DEPARTMENTS",editTag);
//    this.id=id;
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getManager_ID() {
    return manager_ID;
  }

  public void setManager_ID(Integer manager_ID) {
    this.manager_ID = manager_ID;
  }

  public Integer getLocation_ID() {
    return location_ID;
  }

  public void setLocation_ID(Integer location_ID) {
    this.location_ID = location_ID;
  }
  
  @Override
  public String toString() {
    return getName();
  }

  @Override
  public boolean equals(Object obj) {
    if(obj!=null && obj instanceof DepartmentFull)
      return getId()==((DepartmentFull)obj).getId();
    else return false;
  }

  @Override
  public ArrayList getErtekek() {
    ArrayList aL=new ArrayList();
    aL.add(getId());
    aL.add(getName());
    aL.add(manager_ID);
    aL.add(location_ID);
    return aL;
  }

}
