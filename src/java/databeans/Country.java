
package databeans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="COUNTRIES")
public class Country implements Serializable {  //PP: nem tudom AbstractTable-t örökíteni, mert id itt nem int
  
  @Id
  @Column(name="COUNTRY_ID")
  private String id;
//  @Basic
//  @Column(name="COUNTRY_NAME")
//  private String name;
  
  @Basic
  @Column(name="REGION_ID")
  private int regionId;
   
  @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
  @JoinColumn(name="COUNTRY_ID")
  private List<Location> locations=new ArrayList<>();
  
  public Country() {
  }
  
  public Country(String id) {
    this.id=id;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public int getRegionId() {
    return regionId;
  }
  
  public List<Location> getLocations() {
    return locations;
  }

//  @Override
//  public String toString() {
//    return getName();
//  }

  @Override
  public boolean equals(Object obj) {
    if(obj!=null && obj instanceof Country)
      return getId()==((Country)obj).getId();
    else return false;
  }
  
  
}
