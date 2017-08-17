
package managedbeans;

import databeans.AbstractTableFull;
import databeans.EmployeeFull;
import ejb.DataManager;
import ejb.DbMeta;
import databeans.ColumnMeta;
import databeans.DepartmentFull;
import databeans.RegionFull;
import helperbeans.LazyTableDataModel;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.TreeNode;
import javax.persistence.Table;

@Named(value = "tableBean")
@SessionScoped
public class TableBean implements Serializable {

  private @EJB DbMeta dbMeta;
  private @EJB DataManager dataManager;
  private List<AbstractTableFull> rows;
  private List<AbstractTableFull> selectedRows;
  private LazyDataModel<AbstractTableFull> lazyModel;
  private List<ColumnMeta> columns;
  private String activeTable;//which table we are working on. Actually it refers to the node selected, not the table.
  private String activeTableAnnotation;

  @PostConstruct
  public void init() {
    ((HttpSession)FacesContext.getCurrentInstance()
        .getExternalContext().getSession(false)).setAttribute("tableBean", this);
    lazyModel = new LazyTableDataModel();
    fillColumns(null);
    activeTableAnnotation="REGIONS";
  }

  public List<AbstractTableFull> getRows() {
    return rows;
  }

  public LazyDataModel<AbstractTableFull> getLazyModel() {
    return lazyModel;
  }

  public void setLazyModel(LazyDataModel<AbstractTableFull> lazyModel) {
    this.lazyModel = lazyModel;
    rows = ((LazyTableDataModel)lazyModel).getRows();
  }

  public List<ColumnMeta> getColumns() {
    return columns;
  }

  public List<AbstractTableFull> getSelectedRows() {
    return selectedRows;
  }

  public void setSelectedRows(List<AbstractTableFull> selectedRows) {
    this.selectedRows = selectedRows;
  }

  protected String getActiveTableAnnotation() {
    return activeTableAnnotation;
  }
  
  protected void addRow(int rowId){
    if (selectedRows!=null)
      selectedRows.clear();
    AbstractTableFull newRow = null;
    switch(activeTable){
      case "String": //we are on TOTAL. Table is RegionFull
        newRow = new RegionFull();
        break;
      case "Region": //we are on Region node. Table is DepartmentFull
        newRow = new DepartmentFull();
        break;
      case "Department": //we are on Department node. Table is EmployeeFull
        newRow = new EmployeeFull();
        if (rowId!=0)
          ((EmployeeFull)newRow).setDepartmentId(rowId);
        break;
      case "Employee": //we are on EmployeeFull node. Table is EmployeeFull
        newRow = new EmployeeFull();
        if (rowId!=0)
          ((EmployeeFull)newRow).setDepartmentId(rowId);
        break;
        
    }
    newRow.setEditTag("notvalidatednewrow");//See details on editTag in AbstractTableFull.java
    rows.add(newRow);
//    ((LazyEmployeeDataModel)lazyModel).setEmployees(employees);
  }
  
  protected void deleteRow(){
    for (AbstractTableFull selectedRecord : selectedRows) {
      selectedRecord.setEditTag("deletedrow");
    }
    selectedRows.clear();
  }
  
  protected FacesMessage save(String username){
    return dataManager.updateDatabase(rows,username);
  }

  //Updates records from the database based on the selected node and filters id needed
  protected void refreshTable(TreeNode node){
    if (!node.getData().getClass().getSimpleName().equals(activeTable)){
      activeTable = node.getData().getClass().getSimpleName();
      try {
        Table table = node.getChildren().get(0).getData().getClass().getAnnotation(Table.class);
        activeTableAnnotation = table.name();
      } catch (IndexOutOfBoundsException e) {
        activeTableAnnotation = node.getData().getClass().getAnnotation(Table.class).name();
      }
      fillColumns(node);
    }
    rows = dataManager.queryTable(node);
    ((LazyTableDataModel)lazyModel).setRows(rows);
  }
  
  private void fillColumns(TreeNode node){
    if (node!=null){
      switch (node.getData().getClass().getSimpleName()){
        case "String":
          columns = Arrays.asList(dbMeta.getMetaColumn("REGIONS"));break;
        case "Region":
          columns = Arrays.asList(dbMeta.getMetaColumn("DEPARTMENTS"));break;
        case "Department": case "Employee":
          columns = Arrays.asList(dbMeta.getMetaColumn("EMPLOYEES"));break;
      }
    }
    else {
      columns = Arrays.asList(dbMeta.getMetaColumn("REGIONS"));
    }

  }
    
  
}
