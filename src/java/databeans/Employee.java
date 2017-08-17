package databeans;

import javax.persistence.AttributeOverride;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="EMPLOYEES")
@AttributeOverride(name="id", column=@Column(name="EMPLOYEE_ID"))
public class Employee extends AbstractTable implements Comparable<Employee>{
  
  @Basic
  @Column(name="FIRST_NAME")
  private String firstname;
  
  @Basic
  @Column(name="LAST_NAME")
  private String lastname;

  public Employee() {
    super("EMPLOYEES");
  }
  
  public Employee(int id) {
    super(id, "EMPLOYEES");
  }
  
  public Employee(int id, String firstname, String lastname) {
    super(id, "EMPLOYEES");
    this.firstname = firstname;
    this.lastname = lastname;
  }
  
  public String getFirstname() {
    return firstname;
  }

  public String getLastname() {
    return lastname;
  }

  public void setFirstname(String firstname) {
    this.firstname = firstname;
  }

  public void setLastname(String lastname) {
    this.lastname = lastname;
  }
  
  @Override
  public String toString() {
    return getFirstname()+" "+getLastname();
  }

  @Override
  public boolean equals(Object obj) {
    if(obj!=null && obj instanceof Employee)
      return getId()==((Employee)obj).getId();
    else return false;
  }

  @Override
  public int compareTo(Employee o) {
    return toString().compareTo(o.toString());
  }
   
}
