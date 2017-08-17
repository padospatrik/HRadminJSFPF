
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
@AttributeOverride(name="id", column=@Column(name="LOCATION_ID"))
@Table(name="LOCATIONS")
public class Location extends AbstractTable {
   
  @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
  @JoinColumn(name="LOCATION_ID")
  private List<Department> departments=new ArrayList<>();
  
  @Basic
  @Column(name="COUNTRY_ID")
  private String countryId;
  
  public Location() {
    super("LOCATIONS");
  }

  public Location(int id) {
    super(id, "LOCATIONS");
  }

  public String getCountryId() {
    return countryId;
  }

  public List<Department> getDepartments() {
    return departments;
  }

//  @Override
//  public String toString() {
//    return getName();
//  }

  @Override
  public boolean equals(Object obj) {
    if(obj!=null && obj instanceof Location)
      return getId()==((Location)obj).getId();
    else return false;
  }
  
  
}
