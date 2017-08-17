
package databeans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

@MappedSuperclass
public abstract class AbstractTableFull implements Serializable{
  @Id
  private int id;
  
  @Transient
  private String dbTableName;//az adatbázis tábla neve, amiben a többi adat megtalálható
  
  /*
  notvalidatednewrow: new row, not completed yet. Save will be blocked by this element.
  validatednewrow: edited and validated new row. Triggers INSERT statement.
  editedrow: edited and validated row. Triggers UPDATE statement.
  deletedrow: deleted row. Triggers DELETE statement.
  */
  @Transient
  private String editTag;

  public AbstractTableFull() {
  }
  
  public AbstractTableFull(String dbTableName) {
    this.dbTableName = dbTableName;
  }
  
  public AbstractTableFull(int id, String dbTableName, String editTag) {
    this.id = id;
    this.dbTableName = dbTableName;
    this.editTag = editTag;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }
  
  public String getEditTag() {
    return editTag;
  }

  public void setEditTag(String editTag) {
    this.editTag = editTag;
  }
  
  public String getDbTableName() {
    return dbTableName;
  }

  public void setDbTableName(String dbTableName) {
    this.dbTableName = dbTableName;
  }
  
  public abstract ArrayList getErtekek();
  
  protected static Date datumFormaz(String szovegdatum){
    GregorianCalendar cal=new GregorianCalendar(Integer.valueOf(szovegdatum.substring(0,4)), 
            Integer.valueOf(szovegdatum.substring(5,7))-1, 
                    Integer.valueOf(szovegdatum.substring(8,10)));
    return cal.getTime();
  }
    
}
