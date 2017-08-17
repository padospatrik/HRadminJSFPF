
package helperbeans;

import databeans.EmployeeFull;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

public class LazyEmployeeDataModel extends LazyDataModel<EmployeeFull>{
  private List<EmployeeFull> employees;

  public List<EmployeeFull> getEmployees() {
    return employees;
  }

  public void setEmployees(List<EmployeeFull> employees) {
    this.employees = employees;
  }
     
  @Override
  public EmployeeFull getRowData(String rowKey) {
      for(EmployeeFull employee : employees) {
          if(String.valueOf(employee.getId()).equals(rowKey))
              return employee;
      }
      return null;
  }

  @Override
  public Object getRowKey(EmployeeFull employee) {
      return employee.getId();
  }

  @Override
  public List<EmployeeFull> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String,Object> filters) {

      //sort
      if(sortField != null && employees!=null) {
          Collections.sort(employees, new LazySorter(sortField, sortOrder));
      }

      //rowCount
      int dataSize;
      if(employees!=null){
        dataSize = employees.size();
      }
      else {
        dataSize = 0;
      }
      this.setRowCount(dataSize);
      
      //paginate
      if(dataSize > pageSize) {
          try {
              return employees.subList(first, first + pageSize);
          }
          catch(IndexOutOfBoundsException e) {
              return employees.subList(first, first + (dataSize % pageSize));
          }
      }
      else {
          return employees;
      }
  }
}
