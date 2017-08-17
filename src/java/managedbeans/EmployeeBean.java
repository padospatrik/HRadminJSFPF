
package managedbeans;

import databeans.AbstractTableFull;
import databeans.EmployeeFull;
import ejb.DataManager;
import ejb.DbMeta;
import helperbeans.LazyEmployeeDataModel;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.TreeNode;


@Named(value = "employeeBean")
@SessionScoped
public class EmployeeBean implements Serializable {

  private @EJB DataManager dataManager;
  private List<EmployeeFull> employees;
  private LazyDataModel<EmployeeFull> lazyModel;
  private List<EmployeeFull> selectedEmployees;

  @PostConstruct
  public void init() {
    ((HttpSession)FacesContext.getCurrentInstance()
        .getExternalContext().getSession(false)).setAttribute("employeeBean", this);
    lazyModel = new LazyEmployeeDataModel();
  }

  public List<EmployeeFull> getEmployees() {
    return employees;
  }

  public LazyDataModel<EmployeeFull> getLazyModel() {
    return lazyModel;
  }

  public void setLazyModel(LazyDataModel<EmployeeFull> lazyModel) {
    this.lazyModel = lazyModel;
    employees = ((LazyEmployeeDataModel)lazyModel).getEmployees();
  }

  public List<EmployeeFull> getSelectedEmployees() {
    return selectedEmployees;
  }

  public void setSelectedEmployees(List<EmployeeFull> selectedEmployees) {
    this.selectedEmployees = selectedEmployees;
  }
 
  //Removes from the records those not containing the filtered text
  protected void employeeFilter(String employeeSearchText)  {
    for (int i=0; i<employees.size();i++) {
      if (employees.get(i).getFirstname().contains(employeeSearchText))
        continue;
      if (employees.get(i).getLastname().contains(employeeSearchText))
        continue;
      if ((employees.get(i).getFirstname()+" "+employees.get(i).getLastname()).contains(employeeSearchText))
        continue;
      if (!employees.get(i).getPhnumber().contains(employeeSearchText)){
        employees.remove(i);
        i--;
      }
    }
//    ((LazyEmployeeDataModel)lazyModel).setEmployees(employees);
  }

  protected void addRow(int rowId){
    if (selectedEmployees!=null)
      selectedEmployees.clear();
    EmployeeFull newRow=new EmployeeFull();
    newRow.setEditTag("notvalidatednewrow");//See details on editTag in AbstractTableFull.java
    if (rowId!=0)
      newRow.setDepartmentId(rowId);
    employees.add(newRow);
//    ((LazyEmployeeDataModel)lazyModel).setEmployees(employees);
  }
  
  protected void deleteRow(){
    for (EmployeeFull selectedRecord : selectedEmployees) {
      selectedRecord.setEditTag("deletedrow");
    }
    selectedEmployees.clear();
  }
  
  protected FacesMessage save(String username){
    List<AbstractTableFull> atF = new ArrayList<>();
    atF.addAll(employees);
    return dataManager.updateDatabase(atF,username);
  }

  //Updates employees records from the database based on the selected node and filters id needed
  protected void refreshEmployees(TreeNode node, boolean filter, String employeeSearchText){
    employees = dataManager.queryEmployees(node);
//    employees = dataManager.getEmployees();
    if (filter){
      employeeFilter(employeeSearchText);
    }
    ((LazyEmployeeDataModel)lazyModel).setEmployees(employees);
  }
  
}
