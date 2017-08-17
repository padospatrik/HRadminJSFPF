package databeans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.AttributeOverride;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
//@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS) //PP: enélkül is működik
@AttributeOverride(name="id", column=@Column(name="DEPARTMENT_ID"))
@Table(name="DEPARTMENTS")
public class Department extends AbstractTable implements Comparable<Department>{
  
  @Basic
  @Column(name="DEPARTMENT_NAME")
  private String name;
  
  @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
  @JoinColumn(name="DEPARTMENT_ID")
  private List<Employee> employees=new ArrayList<>();

  public Department() {
    super("DEPARTMENTS");
  }
  
  public Department(int id, String name) {
    super(id, "DEPARTMENTS");
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Employee> getEmployees() {
    Collections.sort(employees);
    return employees;
  }
  
  @Override
  public String toString() {
    return getName();
  }

  @Override
  public boolean equals(Object obj) {
    if(obj!=null && obj instanceof Department)
      return getId()==((Department)obj).getId();
    else return false;
  }

  @Override
  public int compareTo(Department o) {
    return getName().compareTo(o.getName());
  }
   
}
