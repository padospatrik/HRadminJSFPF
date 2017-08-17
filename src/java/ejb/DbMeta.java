
package ejb;

import databeans.ColumnMeta;
import helperbeans.ProjectException;
import javax.ejb.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.primefaces.context.RequestContext;

@Singleton
public class DbMeta {
//Collects information on database tables to allow to create datatables dynamically
//It would habe been stored in csv file, but file management in EE is unknown to me, I didn't succeed to find the solution
//Therefore I switched to database table
//Information on tables' columns, types, size, whether it is key, 
//and additional set-ups, like readable, writeable to users
//See ColumnMeta.java
  @PersistenceUnit(unitName = "HRadmin")
  private EntityManagerFactory emf;
  private EntityManager entityManager = null;
  private Session session;
  HashMap<String, ColumnMeta[]> hmColumnMeta = new HashMap<>(7);
    
  @PostConstruct
  public void init(){
    List<ColumnMeta> cm;
    entityManager = emf.createEntityManager();
    session = entityManager.unwrap( Session.class );
    Query query = session.createQuery("from ColumnMeta");
    cm=query.list();
    
    try{
      String tableName="";
      ArrayList<ColumnMeta> aL=new ArrayList<>();
      for (ColumnMeta row : cm) {
        if(!tableName.equals(row.getTablename())){
            if (!aL.isEmpty()){
              hmColumnMeta.put(tableName,aL.toArray(new ColumnMeta[aL.size()]));
              updateMetaColumnTableValues(tableName);
              aL.clear();
            }
        }
        tableName=row.getTablename();
        aL.add(new ColumnMeta(
              row.getTablename(),row.getColumnname(),row.getJavaname(), row.getHeadername(),row.getType(),row.getSize(),
              row.isKey(),row.isNullable(),row.isReadable(),row.isWriteable(),row.getRefTable(),
              row.getRefColumn(),row.getRefDesc(), null));

      }
      hmColumnMeta.put(tableName,aL.toArray(new ColumnMeta[aL.size()]));
      updateMetaColumnTableValues(tableName);
    } catch (IllegalArgumentException | ProjectException ex) {
      ex.printStackTrace();
      //call client-side javascript Nem működik(???)
//        StringBuilder sb = new StringBuilder();
//        sb.append("alert(Initiation failed.");
//        sb.append("\n"+ex.getMessage());
//        sb.append("\nContact the system administrator.)");
//        RequestContext.getCurrentInstance().execute(sb.toString());
    }
    finally{
      try {
        session.close();
        entityManager.close();
      } catch (Exception e) {
      }
    }
    
  }
  
  public void updateMetaColumnTableValues(String tableName) throws ProjectException{
  ColumnMeta[] columnMetaRows = hmColumnMeta.get(tableName);
    
    entityManager = emf.createEntityManager();
    
    try {
      for (int i=0; i<columnMetaRows.length; i++) {
        ArrayList<String[]> values=new ArrayList<>();
        if(columnMetaRows[i].getRefTable()!=null){
          List<Object[]> lista=entityManager.createNativeQuery("SELECT "+columnMetaRows[i].getRefColumn()+
                  ", "+columnMetaRows[i].getRefDesc() +" FROM "+columnMetaRows[i].getRefTable()).getResultList();
          for (Object[] object : lista) {
            String[] str={object[0].toString(),object[1].toString()};
            values.add(str);
          }
          columnMetaRows[i].setValues(values);
        } 
      }
      hmColumnMeta.put(tableName,columnMetaRows);

    } catch (Exception ex) {
        throw new ProjectException("Update of metadata failed.\n"
                + "Please contact system administrator."+ex.getMessage());
    }
    finally{
      try {
        entityManager.close();
      } catch (Exception e) {
      }
    }
}
    
  public ColumnMeta[] getMetaColumn(String tableName){
    return hmColumnMeta.get(tableName);
  }
}
