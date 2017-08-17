
package databeans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;


@Entity
@Table(name="HRADMINCONFIG")
public class ColumnMeta implements Serializable{
  
  @Id
  @Column(name="TABLENAME")//As in the database
  private String tablename;
  
  @Id
  @Column(name="COLUMNNAME")//As in the database
  private String columnname;
  
  @Basic
  @Column(name="JAVANAME")//Property's name in the POJO of the corresponding database column
  private String javaname;
  
  @Basic
  @Column(name="HEADERNAME")//As it will be presented to the user in front-end's tables
  private String headername;
  
  @Basic
  @Column(name="TYPE_") 
  private String type;
  
  @Basic
  @Column(name="SIZE_")
  private int size;  //how many characters are allowed by the database
  
  @Basic
  @Column(name="KEY_")
  private boolean key;
  
  @Basic
  @Column(name="NULLABLE_")
  private boolean nullable;
  
  @Basic
  @Column(name="READABLE")
  private boolean readable;
  
  @Basic
  @Column(name="WRITEABLE")
  private boolean writeable;
  
  @Basic
  @Column(name="REFERENCETABLE")
  private String refTable;
  
  @Basic
  @Column(name="REFERENCECOLUMN")
  private String refColumn;
  
  @Basic
  @Column(name="REFERENCEDESC")
  private String refDesc;
  
  @Transient
  private ArrayList<String[]> values;

  public ColumnMeta() {
  }
  
  public ColumnMeta(String tablename, String columnname, String javaname, String headername, String type, int size, boolean key, boolean nullable,
          boolean readable, boolean writeable,String refTable, String refColumn, String refDesc, 
          ArrayList<String[]> values) {
    validate();
    this.tablename = tablename;
    this.columnname = columnname;
    this.javaname = javaname;
    this.headername = headername;
    this.type = type;
    this.size = size;
    this.key = key;
    this.nullable=nullable;
    this.readable=readable;
    this.writeable=writeable;
    this.refTable = refTable;
    this.refColumn=refColumn;
    this.refDesc=refDesc;
    this.values = values;
  }

  public String getColumnname() {
    return columnname;
  }

  public void setColumnname(String columnname) {
    this.columnname = columnname;
  }

  public String getHeadername() {
    return headername;
  }

  public void setHeadername(String headername) {
    this.headername = headername;
  }

  public String getJavaname() {
    return javaname;
  }

  public void setJavaname(String javaname) {
    this.javaname = javaname;
  }

  public boolean isNullable() {
    return nullable;
  }

  public void setNullable(boolean nullable) {
    this.nullable = nullable;
  }

  public String getTablename() {
    return tablename;
  }

  public void setTablename(String tablename) {
    this.tablename = tablename;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public boolean isKey() {
    return key;
  }

  public void setKey(boolean key) {
    this.key = key;
  }

  public boolean isReadable() {
    return readable;
  }

  public void setReadable(boolean readable) {
    this.readable = readable;
  }

  public boolean isWriteable() {
    return writeable;
  }

  public void setWriteable(boolean writeable) {
    this.writeable = writeable;
  }

  public String getRefTable() {
    return refTable;
  }

  public void setRefTable(String refTable) {
    this.refTable = refTable;
  }

  public String getRefColumn() {
    return refColumn;
  }

  public void setRefColumn(String refColumn) {
    this.refColumn = refColumn;
  }

  public String getRefDesc() {
    return refDesc;
  }

  public void setRefDesc(String refDesc) {
    this.refDesc = refDesc;
  }

  public ArrayList<String[]> getValues() {
    return values;
  }

  public void setValues(ArrayList<String[]> values) {
    this.values = values;
  }

  private void validate(){
     if ((key && refTable!=null) || (key && !readable) || //Ha key nem látható, bonyolultabb lenne a kódolás, miközben feleslegesen rejtenénk el az ID-t, nem titkos adat
            (refTable==null && refColumn!=null) || (refTable!=null && refColumn==null)) 
      throw new IllegalArgumentException("Set-up values are not consistent: "+tablename+type
          +size+key+readable+writeable+refTable+refColumn);
  }
  
  @Override
  public String toString() {
    return String.format("Column metadata: Tablename: %s, Columnname: %s, Javaname: %s, Headername: %s,Type: %s, Size: %s, Key?: %s,"
            + "Nullable?: %s, Readable?: %s, Writeable?: %s, Reference table: %s, Reference column: %s"
            + "Reference description: %s"
            
            ,getTablename(),getColumnname(),getJavaname(), getHeadername(), getType(), getSize(),isKey(),isNullable(),isReadable(),isWriteable(),
            getRefTable(), getRefColumn(), getRefDesc());
  }

  @Override
  public boolean equals(Object obj) {
    if(obj!=null && obj instanceof ColumnMeta)
      return getTablename().equals(((ColumnMeta)obj).getTablename()) && 
              getColumnname().equals(((ColumnMeta)obj).getColumnname());
    else return false; 
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 59 * hash + Objects.hashCode(this.tablename);
    hash = 59 * hash + Objects.hashCode(this.columnname);
    return hash;
  }

}