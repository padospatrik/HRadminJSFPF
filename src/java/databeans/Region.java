package databeans;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.AttributeOverride;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@AttributeOverride(name="id", column=@Column(name="REGION_ID"))
@Table(name="REGIONS")
public class Region extends AbstractTable implements Comparable<Region>{

  @Basic
  @Column(name="REGION_NAME")
  private String name;
   
  @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
  @JoinColumn(name="REGION_ID")
  private List<Country> countries=new ArrayList<>();

  public Region() {
    super("REGIONS");
  }
  
  public Region(int id, String name) {
    super(id, "REGIONS");
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public List<Country> getCountries() {
    return countries;
  }

  @Override
  public String toString() {
    return getName();
  }

  @Override
  public boolean equals(Object obj) {
    if(obj!=null && obj instanceof Region)
      return getId()==((Region)obj).getId();
    else return false;
  }

  @Override
  public int compareTo(Region o) {
    return getName().compareTo(o.getName());
  }
  
}
