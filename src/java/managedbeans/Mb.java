
package managedbeans;

import databeans.AbstractTable;
import databeans.AbstractTableFull;
import databeans.ColumnMeta;
import databeans.EmployeeFull;
import ejb.DataManager;
import ejb.TreeManager;
import databeans.Department;
import databeans.Employee;
import ejb.DbMeta;
import helperbeans.ProjectException;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.event.TreeDragDropEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.primefaces.push.EventBus;
import org.primefaces.push.EventBusFactory;


@Named(value = "mb")
@SessionScoped
public class Mb implements Serializable {

  HttpSession session;
  private String username;
  private TreeNode root;
  private TreeNode selectedNode;
  private TreeNode savedNodeEmployees;
  private TreeNode savedNodeTable;
  private String sessionId;
  private boolean underEdit = false;//When any change (add new row, delete, modify) initiated. Tree will be inactive until edit not completed.
  private boolean underFilter = false;//When table is filtered, this will be maintained while and after editing until Tree is clicked . 
  private boolean refreshTree = false;//When change is made in the database via this application, all existing clients will be notified with primefaces push. With certain actions tree will be refreshed in this case to prevent inconsistency.
  private int pushId;//When push is triggered, it will have an id to help identify who sent the notification. This helps decide if the notification received was sent by other client or by the receiver itself. In the latter case no further action is needed.
                      //Could be also done with sessionID without this further variable
  private String pushBack="";//The previous id when received. Can be compared to the pushId.
  private String activeTab = "";
  private @EJB TreeManager treeManager;
  private @EJB DataManager dataManager;
  private @EJB DbMeta dbMeta;
  private @Inject EmployeeBean employeeBean;
  private @Inject TableBean tableBean;
  private String employeeSearchText;//The text used for filtering the records.


  @PostConstruct
  public void init() {
    session = (HttpSession)FacesContext.getCurrentInstance()
            .getExternalContext().getSession(false);
    session.setAttribute("mb", this);
    sessionId=session.getId();
    session.setAttribute("logout", sessionId);
    username = (String)session.getAttribute("username");
    root = treeManager.getTree();
  }

  public TreeNode getRoot() {
    return root;
  }
  
  public TreeNode getSelectedNode() {
    return selectedNode;
  }

  public void setSelectedNode(TreeNode selectedNode) {
    this.selectedNode = selectedNode;
  }

  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  public boolean isUnderEdit() {
    return underEdit;
  }

  public boolean isUnderFilter() {
    return underFilter;
  }

  public String getActiveTab() {
    return activeTab;
  }

  public String getEmployeeSearchText() {
    return employeeSearchText;
  }

  public void setEmployeeSearchText(String employeeSearchText) {
    this.employeeSearchText = employeeSearchText;
  }
  
  public String getUsername(){
    return username;
  }
  
  //When database changed after editing, all clients will be notified, and this method will be called.
  public void setRefreshTree(){
    if(!String.valueOf(pushId).equals(pushBack)){//The two equal only in that session from which the push was initiated
      refreshTree = true;
    }
  }

  public String getPushBack() {
    return pushBack;
  }

  public void setPushBack(String pushBack) {
    this.pushBack = pushBack;
  }

  //When tabs are switched related datatables are displayed/hidden
  public String getEmployeesDisplay() {
    if (activeTab.equals(":form:employees"))
      return "";
    else return "none";
  }
  
  //When tabs are switched related datatables are displayed/hidden
  public String getTableDisplay() {
    if (activeTab.equals(":form:table"))
      return "";
    else return "none";
  }
  
  public String getSearchBarVisibility() {
    if (activeTab.equals(":form:employees"))
      return "visible";
    else return "hidden";
  }
  
  //when tab is activated, its style is changed. somehow with styleclass it didn't work, background-color directly has to be modified
  //Note that tabview under layoutunit couldn't be rendered, instead of tabs, buttons are mocking tabs
  public String getBtEViewClass() {
    if (activeTab.equals(":form:employees"))
      return "bisque";
    else return "";
  }
  
  //when tab is activated, its style is changed. somehow with styleclass it didn't work, background-color directly has to be modified
  public String getBtTViewClass() {
    if (activeTab.equals(":form:table"))
      return "bisque";
    else return "";
  }

  public void clickEmployeesView(){
//    ((CommandButton)event.getSource()).setStyleClass("activeTab"); Nem működik így
    underFilter=false;
    employeeSearchText="";
    if (!activeTab.equals(":form:employees") && !activeTab.equals("")){
      activeTab = ":form:employees";
      employeeBean.refreshEmployees(savedNodeEmployees,false,"");
      if (refreshTree){
        root = treeManager.getTree();
        refreshTree=false;
      }
      if (savedNodeEmployees!=null){
        expandSelectedNode(savedNodeEmployees,root);
//        System.out.println(((AbstractTable)savedNodeEmployees.getData()).getId());
      }
    }
  }
  
  public void clickTableView(){
    underFilter=false;
    employeeSearchText="";
    if (!activeTab.equals(":form:table") && !activeTab.equals("")){
      activeTab = ":form:table";
      tableBean.refreshTable(savedNodeTable==null?root.getChildren().get(0):savedNodeTable);
      if (refreshTree){
        root = treeManager.getTree();
        refreshTree=false;
      }
      if (savedNodeTable!=null){
        expandSelectedNode(savedNodeTable,root);
      }
      else{
        expandSelectedNode(root.getChildren().get(0),root);
      }
    }
  }
  
  //Collects possible values for fields when it can be selected from a list
  public List<String[]> getForeignKeys(String column){
    String table = null;
    switch (activeTab){ 
      case ":form:employees":
        table="EMPLOYEES";break;
      case ":form:table":
        table=tableBean.getActiveTableAnnotation();break;
    }
    ColumnMeta[] columnMetaRows = dbMeta.getMetaColumn(table);
    for (ColumnMeta columnMetaRow : columnMetaRows) {
      if (columnMetaRow.getColumnname().equals(column))
        return columnMetaRow.getValues();
    }
    return null;
  }
  
  //Returns whether the field may be blank or not for client-side validation
  public String columnRequired(String column){
    String table = null;
    switch (activeTab){ 
      case ":form:employees":
        table="EMPLOYEES";break;
      case ":form:table":
        table=tableBean.getActiveTableAnnotation();break;
    }
    ColumnMeta[] columnMetaRows = dbMeta.getMetaColumn(table);
    for (ColumnMeta columnMetaRow : columnMetaRows) {
      if (columnMetaRow.getColumnname().equals(column)){
        return String.valueOf(!columnMetaRow.isNullable());
      }
    }
    return "true";
  }
  
  //Returns the maximum length of the field for client-side validation
  public String validateLength(String column){
    StringBuilder sb = new StringBuilder();
    ColumnMeta[] columnMetaRows = dbMeta.getMetaColumn("EMPLOYEES");
    for (ColumnMeta columnMetaRow : columnMetaRows) {
      if (columnMetaRow.getColumnname().equals(column)){
        if (columnMetaRow.getType().equals("NUMBER")){
          for (int i = 0; i < columnMetaRow.getSize(); i++) {
            sb.append("9");
          }
          return sb.toString();
        }
        else
          return String.valueOf(columnMetaRow.getSize());
      }
    }
    return "";
  }
  
  //Removes from the records those not containing the filtered text
  public void employeeFilter()  {
    if(employeeSearchText==null || employeeSearchText.length()<3)
      return;
    underFilter=true;
    employeeBean.employeeFilter(employeeSearchText);
  }
  
  public void addRow(){
    underEdit = true;
    if (!underFilter)
      employeeSearchText="";
    toggleTreeSelection(root,false);
    int rowId=0;
    if (selectedNode!=null){//Fill department ID for the new row
      String selectedClass = selectedNode.getData().getClass().getSimpleName();
        if (selectedClass.equals("Department")){
          rowId = ((Department)(selectedNode.getData())).getId();
        }
        else if (selectedClass.equals("Employee")){
          rowId = ((Department)(selectedNode.getParent().getData())).getId();
        }
    }
    switch (activeTab){ 
      case ":form:employees":
        employeeBean.addRow(rowId);break;
      case ":form:table":
        tableBean.addRow(rowId);break;
    }
  }
  
  //Called when table's row editing is finished with approving the changes
  public void onRowEdit(RowEditEvent event){
    underEdit = true;
    if (!underFilter)
      employeeSearchText="";
    toggleTreeSelection(root,false);
    AbstractTableFull editedRow = null;
    switch (activeTab){ 
      case ":form:employees":
        editedRow = (EmployeeFull)event.getObject();
        break;
      case ":form:table":
        editedRow = (AbstractTableFull)event.getObject();
        break;
    }
    if (editedRow.getEditTag()!=null){
      switch(editedRow.getEditTag()){
        case "notvalidatednewrow":
          editedRow.setEditTag("validatednewrow");break;
        case "deletedrow":
          break;
        case "validatednewrow":
          break;
      }
    }
    else
      editedRow.setEditTag("editedrow");
   }
  
  public void deleteRow(){
    underEdit = true;
    if (!underFilter)
      employeeSearchText="";
    toggleTreeSelection(root,false);
    switch (activeTab){ 
      case ":form:employees":
        employeeBean.deleteRow();break;
      case ":form:table":
        tableBean.deleteRow();break;
    }
  }
  
  public void withdraw(){
    underEdit = false;
    if (refreshTree){
      root = treeManager.getTree();
      expandSelectedNode(selectedNode,root);
      refreshTree=false;
    }
    toggleTreeSelection(root,true);
    switch (activeTab){ 
      case ":form:employees":
        employeeBean.refreshEmployees(selectedNode,underFilter,employeeSearchText);break;
      case ":form:table":
        tableBean.refreshTable(selectedNode);
        break;
    }
  }
  
  public void save(){
    FacesMessage message = null;
    switch (activeTab){ 
      case ":form:employees":
        message = employeeBean.save(username);
        break;
      case ":form:table":
        message = tableBean.save(username);
        break;
    }
      if (message.getSeverity().equals(FacesMessage.SEVERITY_WARN))
        FacesContext.getCurrentInstance().addMessage(null, message);
      else {
        treeManager.updateTree();
        root = treeManager.getTree();//this tree is a new tree, so selection and expand lost here
        refreshTree=false;
        toggleTreeSelection(root,true);
        expandSelectedNode(selectedNode,root);//selection and expand restored as far as possible with brief code. Complex solution is not intended
        switch (activeTab){ 
          case ":form:employees":
            employeeBean.refreshEmployees(selectedNode,true,employeeSearchText);
            try {
              dbMeta.updateMetaColumnTableValues("EMPLOYEES");
            } catch (ProjectException ex) {
              FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Metadata issue",
                ex.getMessage()));
            }
            break;
          case ":form:table":
            tableBean.refreshTable(selectedNode);
            try {
              dbMeta.updateMetaColumnTableValues(tableBean.getActiveTableAnnotation());
            } catch (ProjectException ex) {
              FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Metadata issue",
                ex.getMessage()));
            }
            break;
        }
        notifyClients();
        underEdit = false;
        FacesContext.getCurrentInstance().addMessage(null, message);
      }
  }
  
  //after update is made in database, treenode needs refresh, but refresh collapse all nodes
  //we don't bother with previous state, but leaves all collapsed, except for the selected node and its parents
  private void expandSelectedNode(TreeNode search, TreeNode where){
    for (TreeNode child : where.getChildren()) {
      if (child.getData().equals(search.getData())){
        child.setSelected(true);
        expandParentNodes(child);
        expandSelectedNode(search, child);
      }
      else{
        child.setSelected(false);
        expandSelectedNode(search, child);
      }
    }
  }
  
  private void expandParentNodes(TreeNode node){
    if (node.getParent()!=null)
      expandParentNodes(node.getParent());
    node.setExpanded(true);
  }
  
  //Called when any node is clicked on the Tree
  public void onNodeSelect(NodeSelectEvent event) {
    underFilter=false;
    employeeSearchText="";
    if (activeTab.equals(""))
      activeTab = ":form:employees";
    if (savedNodeEmployees!=null && savedNodeEmployees!=selectedNode)
      savedNodeEmployees.setSelected(false);
    if (savedNodeTable!=null && savedNodeTable!=selectedNode)
      savedNodeTable.setSelected(false);
    switch (activeTab){ 
      case ":form:employees":
        employeeBean.refreshEmployees(event.getTreeNode(),false,"");
        savedNodeEmployees=selectedNode;
        break;
      case ":form:table":
        tableBean.refreshTable(event.getTreeNode());
        savedNodeTable=selectedNode;
        break;
    }
    if (refreshTree){
      root = treeManager.getTree();
      expandSelectedNode(selectedNode,root);
      refreshTree=false;
    }

  }
  
  //To avoid collapsing or expanding you have to mark your node on the java side as collapsed or expanded.
  //http://stackoverflow.com/questions/25358328/avoid-nodes-of-ptreetable-to-collapse-after-update
  public void nodeExpand(NodeExpandEvent event) {
    event.getTreeNode().setExpanded(true);      
  }

  public void nodeCollapse(NodeCollapseEvent event) {
    event.getTreeNode().setExpanded(false);     
  }
  
  //Tree is changed to not clickable (selectable) when editing is initiated until finished.
  private void toggleTreeSelection(TreeNode node, boolean onOff){
    node.setSelectable(onOff);
    for (TreeNode treeNode : node.getChildren()) {
      toggleTreeSelection(treeNode,onOff);
    }
  }
  
  //Called when node is moved within the Tree
  public void onDragDrop(TreeDragDropEvent event) {
    TreeNode dragNode = event.getDragNode();
    TreeNode dropNode = event.getDropNode();
    FacesMessage message=null;
    Employee employee;
    if (!dragNode.getData().getClass().getSimpleName().equals("Employee")){//we allow only employees movement. More complex restructure is a rare case, application shouldn't support it.
      message = new FacesMessage(FacesMessage.SEVERITY_WARN, 
                "Only employees may be dragged","");
    }
    else{
      switch(dropNode.getData().getClass().getSimpleName()){
        case "String":
          message = new FacesMessage(FacesMessage.SEVERITY_WARN, 
                  "Dragging on Total not allowed","");
          break;
        case "Region":
          message = new FacesMessage(FacesMessage.SEVERITY_WARN, 
                  "Dragging on Region not allowed","");
          break;
        case "Department":
          employee = (Employee)dragNode.getData();
          message = dataManager.moveEmployee(employee,((Department)dropNode.getData()).getId(),username);
          break;
        case "Employee":
          employee = (Employee)dragNode.getData();
          message = dataManager.moveEmployee(employee,((Department)dropNode.getParent().getData()).getId(),username);
//          treeManager.updateTree();
          //when Employee is dragged on employee (typically not accurate mouse usage) movement will be allowed, but tree has to be corrected after
          root = treeManager.getTree();
          break;
      }
    }
    if(message.getSeverity().equals(FacesMessage.SEVERITY_WARN)){
      //when movement not allowed(employee not moved under department), tree has to be restored
      root = treeManager.getTree();//this tree is a new tree, so selection and expand lost here
      expandSelectedNode(dropNode,root);
      if (selectedNode!=null)
        expandSelectedNode(selectedNode,root);//selection and expand restored as far as possible with brief code. Complex solution is not intended
      if (refreshTree){//when other client changed the database in the meanwhile we refresh the records too
        switch (activeTab){ 
          case ":form:employees":
            employeeBean.refreshEmployees(selectedNode,true,employeeSearchText);break;
          case ":form:table":
            tableBean.refreshTable(selectedNode);break;
        }
        refreshTree=false;
      }
    }
    else{//when everything fine
      treeManager.updateTree();
      if (selectedNode!=null){
        switch (activeTab){ 
          case ":form:employees":
            employeeBean.refreshEmployees(selectedNode,true,employeeSearchText);break;
          case ":form:table":
            tableBean.refreshTable(selectedNode);break;
        }
      }
      expandSelectedNode(dropNode,root);
      notifyClients();
    }
    FacesContext.getCurrentInstance().addMessage(null, message);
  }
  
  //When database has been changed with save or drag/drop all clients will be notified
  private void notifyClients(){
    pushId=(int)(Math.random()*2147483647);
    EventBus eventBus = EventBusFactory.getDefault().eventBus();
    eventBus.publish("/push", pushId);
  }
  
  public void layoutToggle(ToggleEvent event){
//    System.out.println(((LayoutUnit)(event.getSource())).);
  }
  
}
