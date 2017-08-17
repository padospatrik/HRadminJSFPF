
package ejb;

import databeans.AbstractTable;
import databeans.AbstractTableFull;
import databeans.DepartmentFull;
import databeans.Employee;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import org.hibernate.Session;
import org.primefaces.model.TreeNode;
import databeans.EmployeeFull;
import databeans.RegionFull;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import org.apache.derby.client.am.SqlException;

@Stateless
public class DataManager {  //nem enged interfacet implementálni
  private static final Logger logger = Logger.getLogger("ejb.DataManager");
  @PersistenceUnit(unitName = "HRadmin")
  private EntityManagerFactory emf;
  private EntityManager entityManager = null;
  private Session session;
  private List<EmployeeFull> employees;
  private List<AbstractTableFull> rows;
  private final String HQLEMPLOYEESINREGION="select e from EmployeeFull e, DepartmentFull d, "
          + "Region r, Location l, Country c " +
            "where d.location_ID=l.id and l.countryId=c.id and "
          + "c.regionId=r.id and e.departmentId=d.id and r.id=%s";
  private final String HQLEMPLOYEESINDEPARTMENT="from EmployeeFull " +
            "where departmentId=%s";
  private final String HQLEMPLOYEESINGLE="from EmployeeFull where id=%s";
  private final String HQLDEPARTMENTS="select d from DepartmentFull d,"
          + "Region r, Location l, Country c " +
          "where d.location_ID=l.id and l.countryId=c.id and "
          + "c.regionId=r.id and r.id=%s";

  @PostConstruct
  private void init(){

  }

  public List<EmployeeFull> queryEmployees(TreeNode node){
    AbstractTable data=null;
    String selectedClass = "String";
    if (node!=null)
      selectedClass = node.getData().getClass().getSimpleName();
    if (!selectedClass.equals("String"))
      data = (AbstractTable)(node.getData());
    String HQL="";
    switch (selectedClass){
      case "String":
        HQL="from EmployeeFull";break;
      case "Region":
        HQL=String.format(HQLEMPLOYEESINREGION,data.getId());break;
      case "Department":
        HQL=String.format(HQLEMPLOYEESINDEPARTMENT,data.getId());break;
      case "Employee":
        HQL=String.format(HQLEMPLOYEESINGLE,data.getId());break;
    }
    
    entityManager = emf.createEntityManager();
    employees = entityManager.createQuery(HQL).getResultList();
    for (EmployeeFull employee : employees) {//one hour need to be added, otherwise 1 day is deducted when converted to String in the browser. Weird!!!
      employee.setHiredate(new Date(employee.getHiredate().getTime()+1000*60*60));
    }
    try {
      entityManager.close();
    } catch (Exception e) {
    }
    return employees;
  }
  
  public List<AbstractTableFull> queryTable(TreeNode node){
    AbstractTable data=null;
    String selectedClass = "String";
    if (node!=null)
      selectedClass = node.getData().getClass().getSimpleName();
    if (!selectedClass.equals("String"))
      data = (AbstractTable)(node.getData());
    String HQL="";
    switch (selectedClass){
      case "String":
        HQL="from RegionFull";break;
      case "Region":
        HQL=String.format(HQLDEPARTMENTS,data.getId());break;
      case "Department":
        HQL=String.format(HQLEMPLOYEESINDEPARTMENT,data.getId());break;
      case "Employee":
        HQL=String.format(HQLEMPLOYEESINGLE,data.getId());break;
    }
    
    entityManager = emf.createEntityManager();
    rows = entityManager.createQuery(HQL).getResultList();
    if (selectedClass.equals("Department") || selectedClass.equals("Employee")) 
    for (AbstractTableFull row : rows) {//one hour need to be added, otherwise 1 day is deducted when converted to String in the browser. Weird!!!
      ((EmployeeFull)row).setHiredate(new Date(((EmployeeFull)row).getHiredate().getTime()+1000*60*60));
    }
    try {
      entityManager.close();
    } catch (Exception e) {
    }
    return rows;
  }
  
//  public List<EmployeeFull> getEmployees(){
//    return employees;
//  }
  
  public FacesMessage updateDatabase(List<AbstractTableFull> changeItems,String username){
    StringBuilder logText=new StringBuilder();
    try{
      entityManager = emf.createEntityManager();
      session = entityManager.unwrap( Session.class );
//      EntityTransaction trans = entityManager.getTransaction();
//      trans.begin();  //JPA doesn't allow to update detached instances, Hibernate API does allow
      session.beginTransaction();
      String tag = "";
      for (AbstractTableFull item : changeItems) {
        if (item.getEditTag()!=null)
          tag=item.getEditTag();
        else
          tag="";
        switch (tag){
          case "notvalidatednewrow":
            try {
              session.close();
              entityManager.close();
            } catch (Exception e) {
            }
            return new FacesMessage(FacesMessage.SEVERITY_WARN, "Edit not completed yet",
              "There are new row(s) that need to be successfully completed.");
          case "validatednewrow":
            session.persist(item);
            logText.append("New item:"+item.toString()+";");
            break;
          case "editedrow":
            session.update(item);
            logText.append("Updated item:"+item.toString()+";");
            break;
          case "deletedrow":
            if (item instanceof EmployeeFull){
              EmployeeFull delItem = new EmployeeFull();
              session.load(delItem,item.getId());//delete not allowed with detached instances, we first need to load into session
              session.delete(delItem);
              logText.append("Deleted employee:"+item.toString()+";");
            }
            else if (item instanceof RegionFull){
              RegionFull delItem = new RegionFull();
              session.load(delItem,item.getId());//delete not allowed with detached instances, we first need to load into session
              session.delete(delItem);
              logText.append("Deleted region:"+item.toString()+";");
            }
            else if (item instanceof DepartmentFull){
              DepartmentFull delItem = new DepartmentFull();
              session.load(delItem,item.getId());//delete not allowed with detached instances, we first need to load into session
              session.delete(delItem);
              logText.append("Deleted department:"+item.toString()+";");
            }
            break;
        }
      }
//      trans.commit();
        session.getTransaction().commit();
      try {
        session.close();
        entityManager.close();
      } catch (Exception e) {
      }
      logger.log(Level.INFO,username+": Succesfull db change: "+logText.toString());
      return new FacesMessage(FacesMessage.SEVERITY_INFO, "Succesfull update",
              "Item(s) have been succesfully updated.");
    }
    catch (Exception e){
      String error="";
      Throwable et = e.getCause();
      while (et!=null &&
              !et.getClass().getSimpleName()
              .equals("SQLIntegrityConstraintViolationException")){
        et = et.getCause();
      }
      if (et!=null)
        error = et.getMessage();
      try {
        session.close();
        entityManager.close();
      } catch (Exception ex) {
      }
      logger.log(Level.WARNING,username+": Failed db change: "+logText.toString());
      return new FacesMessage(FacesMessage.SEVERITY_WARN, "Update failed",
              "Item(s) have not been updated.\n"+error);
    }
  }

  public FacesMessage moveEmployee(Employee employee, int departmentId, String username){
    StringBuilder logText=new StringBuilder();
    try{
      entityManager = emf.createEntityManager();
      session = entityManager.unwrap( Session.class );
      session.beginTransaction();
      EmployeeFull fullEmployee = new EmployeeFull();
      session.load(fullEmployee,employee.getId());
      fullEmployee.setDepartmentId(departmentId);
      session.update(fullEmployee);
      logText.append("Moved employee:"+fullEmployee.toString()+";");
      session.getTransaction().commit();
      try {
        session.close();
        entityManager.close();
      } catch (Exception e) {
      }
      logger.log(Level.INFO,username+": Succesfull db change: "+logText.toString());
      return new FacesMessage(FacesMessage.SEVERITY_INFO, "Succesfull update",
              "Employee has been succesfully moved.");
    }
    catch (Exception e){
        e.printStackTrace();
      try {
        session.close();
        entityManager.close();
      } catch (Exception ex) {
      }
      logger.log(Level.WARNING,username+": Failed db change: "+logText.toString());
      return new FacesMessage(FacesMessage.SEVERITY_WARN, "Update failed",
              "Employee failed to be moved.");
    }
  }
  
}
