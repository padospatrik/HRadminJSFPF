package databeans;

import java.util.ArrayList;
import javax.persistence.AttributeOverride;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@AttributeOverride(name="id", column=@Column(name="REGION_ID"))
@Table(name="REGIONS")
public class RegionFull extends AbstractTableFull{

  @Basic
  @Column(name="REGION_NAME")
  private String name;

  public RegionFull() {
    super("REGIONS");
  }
  
  public RegionFull(int id, String name, String editTag) {
    super(id, "REGIONS",editTag);
//    this.id = id;
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
  
  @Override
  public String toString() {
    return getName();
  }

  @Override
  public boolean equals(Object obj) {
    if(obj!=null && obj instanceof RegionFull)
      return getId()==((RegionFull)obj).getId();
    else return false;
  }

  @Override
  public ArrayList getErtekek() {
    ArrayList aL=new ArrayList();
    aL.add(getId());
    aL.add(getName());
    return aL;
  }
  
}
