
package databeans;

import java.util.ArrayList;
import java.util.Date;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.persistence.AttributeOverride;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Named
@RequestScoped
@Entity
@Table(name="EMPLOYEES")
@AttributeOverride(name="id", column=@Column(name="EMPLOYEE_ID"))
public class EmployeeFull extends AbstractTableFull implements Comparable<EmployeeFull>{
  //Szívesebben örökíteném az Employee osztályt, így duplikált kódjaim vannak, de Hibernate úgy 
  //hibára fut, DType oszlopot szeretne betenni az adatbázisba
  
  @Basic
  @Column(name="FIRST_NAME")
  private String firstname;
  
  @Basic
  @Column(name="LAST_NAME")
  private String lastname;
  
  @Basic
  @Column(name="EMAIL")
  private String email;
    
  @Basic
  @Column(name="PHONE_NUMBER")
  private String phnumber;
    
  @Basic
  @Column(name="HIRE_DATE")
  private Date hiredate;
  
  @Basic
  @Column(name="JOB_ID")
  private String jobId;
 
  @Basic
  @Column(name="SALARY")
  private Integer salary;
 
  @Basic
  @Column(name="COMMISSION_PCT")
  private Integer commission;
 
  @Basic
  @Column(name="MANAGER_ID")
  private Integer managerId;
   
  @Basic
  @Column(name="DEPARTMENT_ID")
  private Integer departmentId;
  
  public EmployeeFull() {
    super("EMPLOYEES");
  }
  
  public EmployeeFull(int id, String editTag) {
    super(id, "EMPLOYEES",editTag);
  }

  public EmployeeFull(int id,String firstname, String lastname, String email, 
          String phnumber, Date hiredate, String jobId, Integer salary, 
          Integer commission, Integer managerId, Integer departmentId, String editTag) {
    super(id, "EMPLOYEES",editTag);
    this.firstname = firstname;
    this.lastname = lastname;
    this.email = email;
    this.phnumber = phnumber;
    this.hiredate = hiredate;
    this.jobId = jobId;
    this.salary = salary;
    this.commission = commission;
    this.managerId = managerId;
    this.departmentId = departmentId;
  }
  
  public String getFirstname() {
    return firstname;
  }

  public void setFirstname(String firstname) {
    this.firstname = firstname;
  }
  
  public String getLastname() {
    return lastname;
  }

  public void setLastname(String lastname) {
    this.lastname = lastname;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPhnumber() {
    return phnumber;
  }

  public void setPhnumber(String phnumber) {
    this.phnumber = phnumber;
  }

  public Date getHiredate() {
    return hiredate;
  }

  public void setHiredate(Date hiredate) {
    this.hiredate = hiredate;
  }

  public String getJobId() {
    return jobId;
  }

  public void setJobId(String jobId) {
    this.jobId = jobId;
  }

  public Integer getSalary() {
    return salary;
  }

  public void setSalary(Integer salary) {
    this.salary = salary;
  }

  public Integer getCommission() {
    return commission;
  }

  public void setCommission(Integer commission) {
    this.commission = commission;
  }

  public Integer getManagerId() {
    return managerId;
  }

  public void setManagerId(Integer managerId) {
    this.managerId = managerId;
  }

  public Integer getDepartmentId() {
    return departmentId;
  }

  public void setDepartmentId(Integer departmentId) {
    this.departmentId = departmentId;
  }

  @Override
  public String toString() {
    return getFirstname()+" "+getLastname()+" "+getEmail()+" "+getPhnumber()
            +" "+getHiredate()+" "+getJobId()+" "+getSalary()+" "+getCommission()
            +" "+getManagerId()+" "+getDepartmentId()+" "+getEditTag();
  }

  @Override
  public boolean equals(Object obj) {
    if(obj!=null && obj instanceof EmployeeFull)
      return getId()==((Employee)obj).getId();
    else return false;
  }

  @Override
  public int compareTo(EmployeeFull o) {
    return toString().compareTo(o.toString());
  }
  
  public EmployeeFull clone(){
    EmployeeFull tmp = new EmployeeFull(this.getId(),firstname,lastname,email,
      phnumber,hiredate,jobId,salary,commission,managerId,departmentId,this.getEditTag());
    return tmp;
  }
  
  @Override
  public ArrayList getErtekek(){
    ArrayList aL=new ArrayList();
    aL.add(getId());
    aL.add(firstname);
    aL.add(lastname);
    aL.add(email);
    aL.add(phnumber);
    aL.add(hiredate);
    aL.add(jobId);
    aL.add(salary);  
    aL.add(commission); 
    aL.add(managerId); 
    aL.add(departmentId);
    return aL;
  }
    

}
