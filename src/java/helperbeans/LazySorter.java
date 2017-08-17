
package helperbeans;

import databeans.AbstractTableFull;
import databeans.DepartmentFull;
import databeans.EmployeeFull;
import databeans.RegionFull;
import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.Date;
import org.primefaces.model.SortOrder;

public class LazySorter implements Comparator<AbstractTableFull> {
 
    private String sortField;
    private SortOrder sortOrder;
     
    public LazySorter(String sortField, SortOrder sortOrder) {
        this.sortField = sortField;
        this.sortOrder = sortOrder;
    }
 
    @Override
    public int compare(AbstractTableFull record1, AbstractTableFull record2) {
        try {
          int value=0;
          Object value1=null;
          Object value2=null;
          Field field=null;
          if (!sortField.equals("id")){
            try {
              field = EmployeeFull.class.getDeclaredField(sortField);
            } catch (NoSuchFieldException e1) {
              try {
                field = DepartmentFull.class.getDeclaredField(sortField);
              } catch (NoSuchFieldException e2) {
                try {
                  field = RegionFull.class.getDeclaredField(sortField);
                } catch (NoSuchFieldException e3) {
                  e3.printStackTrace();
                  throw new RuntimeException(e3.getMessage());
                }
              }
            }
            
            field.setAccessible(true);
            Object selector = field.get(record1);
            if (selector == null) 
              selector = field.get(record2);
            if (selector == null)
              return 1;
            switch (selector.getClass().getSimpleName()){
              case "String":
                if (field.get(record1)!=null)
                  value1 = ((String)field.get(record1)).toUpperCase();
                if (value1==null) value1="";
                if (field.get(record2)!=null)
                  value2 = ((String)field.get(record2)).toUpperCase();
                if (value2==null) value2="";
                value = ((Comparable)value1).compareTo(value2);
                break;
              case "Integer":
                if (field.get(record1)!=null)
                  value1 = field.get(record1);
                else value1 = 0;
                if (field.get(record2)!=null)
                  value2 = field.get(record2);
                else value2 = 0;
                value = ((Comparable)value1).compareTo(value2);
                break;
              case "Date":
                value1 = (Date)field.get(record1);
                value2 = (Date)field.get(record2);
                if (value1==null) value1=new Date(Long.MIN_VALUE/100000);
                if (value2==null) value2=new Date(Long.MIN_VALUE/100000);
                value = ((Comparable)value1).compareTo(value2);
                break;
            }
          } else{ 
            value = ((Integer)record1.getId()).compareTo(record2.getId());
          }
//            int value = ((Comparable)value1).compareTo(value2);
            return SortOrder.ASCENDING.equals(sortOrder) ? value : -1 * value;
        }
        catch (IllegalArgumentException ex) {
          throw new RuntimeException();
        } 
        catch (IllegalAccessException ex) {
          ex.printStackTrace();
          throw new RuntimeException();
      }
    }
}
