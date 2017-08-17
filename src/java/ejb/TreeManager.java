
package ejb;

import databeans.Country;
import databeans.Department;
import databeans.Employee;
import databeans.Location;
import databeans.Region;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

@Singleton
public class TreeManager {

  @PersistenceUnit(unitName = "HRadmin")
  private EntityManagerFactory emf;
  private EntityManager entityManager = null;
  private Session session;
  private List<Region> regions;
  private DefaultTreeNode root;

  @PostConstruct
  private void init(){
    fillRegion();
    fillTree();
  }

  private void fillRegion(){
    regions = new ArrayList<>();
    entityManager = emf.createEntityManager();
    session = entityManager.unwrap( Session.class );
    Query query = session.createQuery("from Region");
    regions=query.list();
    try {
      session.close();
      entityManager.close();
    } catch (Exception e) {
    }
  }
  
  private void fillTree(){
    root = new DefaultTreeNode("Root", null);
    TreeNode mainNode = new DefaultTreeNode("TOTAL", root);
    for (int i=0;i<regions.size();i++){
      Region region=regions.get(i);
      ArrayList<Department> aLReszleg=new ArrayList<>();
      for (Country country: region.getCountries()){
        for(Location location: country.getLocations()){
          for (Department department : location.getDepartments()) {
            aLReszleg.add(department);
          }
        }
      }
      if (aLReszleg.size()>0){
        TreeNode nodeRegion = new DefaultTreeNode(region, mainNode);
        Collections.sort(aLReszleg);
        for (Department department : aLReszleg) {
          TreeNode nodeDepartment = new DefaultTreeNode(department, nodeRegion);
          for (Employee employee : department.getEmployees()) {
            nodeDepartment.getChildren().add(new DefaultTreeNode(employee));
          }
        }
      }
      else{
        regions.remove(region);//Ha nem tartozik részleg a régió alá, akkor nem tároljuk feleslegesen
        i--;
      }
    }
  }
  
  public synchronized void updateTree(){
    fillRegion();
    fillTree();
  }
    
  public synchronized DefaultTreeNode getTree(){
    //I don't give back the same Nodestructure to all clients, as they all need 
    //their own copies to select, expand and collapse. So I need to clone the nodes.
    DefaultTreeNode rootT = new DefaultTreeNode("Root", null);
    TreeNode mainNode = new DefaultTreeNode("TOTAL", rootT);
    for (TreeNode treeNode : root.getChildren().get(0).getChildren()) {
      TreeNode nodeRegion = new DefaultTreeNode(treeNode.getData(), mainNode);
      for (TreeNode treeNode1 : treeNode.getChildren()) {
        TreeNode nodeDepartment = new DefaultTreeNode(treeNode1.getData(), nodeRegion);
        for (TreeNode treeNode2 : treeNode1.getChildren()) {
          nodeDepartment.getChildren().add(new DefaultTreeNode(treeNode2.getData()));
        }
      }
    }
    return rootT;
  }

}
