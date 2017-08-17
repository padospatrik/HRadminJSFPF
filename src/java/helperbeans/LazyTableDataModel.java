
package helperbeans;

import databeans.AbstractTableFull;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

public class LazyTableDataModel extends LazyDataModel<AbstractTableFull>{
  private List<AbstractTableFull> rows;

  public List<AbstractTableFull> getRows() {
    return rows;
  }

  public void setRows(List<AbstractTableFull> rows) {
    this.rows = rows;
  }
     
  @Override
  public AbstractTableFull getRowData(String rowKey) {
      for(AbstractTableFull row : rows) {
          if(String.valueOf(row.getId()).equals(rowKey))
              return row;
      }
      return null;
  }

  @Override
  public Object getRowKey(AbstractTableFull row) {
      return row.getId();
  }

  @Override
  public List<AbstractTableFull> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String,Object> filters) {

      //sort
      if(sortField != null && rows!=null) {
          Collections.sort(rows, new LazySorter(sortField, sortOrder));
      }

      //rowCount
      int dataSize;
      if(rows!=null){
        dataSize = rows.size();
      }
      else {
        dataSize = 0;
      }
      this.setRowCount(dataSize);
      
      //paginate
      if(dataSize > pageSize) {
          try {
              return rows.subList(first, first + pageSize);
          }
          catch(IndexOutOfBoundsException e) {
              return rows.subList(first, first + (dataSize % pageSize));
          }
      }
      else {
          return rows;
      }
  }
}
