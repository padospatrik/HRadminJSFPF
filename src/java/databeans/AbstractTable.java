
package databeans;

import java.io.Serializable;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

@MappedSuperclass
public abstract class AbstractTable implements Serializable{
  @Id
  private int id;
  
  @Transient
  private String dbTableName;//az adatbázis tábla neve, amiben a többi adat megtalálható

  public AbstractTable() {
  }
  
  public AbstractTable(String dbTableName) {
    this.dbTableName = dbTableName;
  }
  
  public AbstractTable(int id, String dbTableName) {
    this.id = id;
    this.dbTableName = dbTableName;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getDbTableName() {
    return dbTableName;
  }

  public void setDbTableName(String dbTableName) {
    this.dbTableName = dbTableName;
  }

  public boolean equals(AbstractTable aT) {
    return this.getId()==aT.getId(); 
  }

  
}
