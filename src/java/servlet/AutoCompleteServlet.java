
package servlet;

import databeans.EmployeeFull;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.ServletSecurity.TransportGuarantee;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import managedbeans.EmployeeBean;
import managedbeans.Mb;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONObject;

@ServletSecurity(
@HttpConstraint(transportGuarantee = TransportGuarantee.CONFIDENTIAL,
    rolesAllowed = {"hradmin"}))
public class AutoCompleteServlet extends HttpServlet {
  //@Inject private Mb bean;  Class-level variable not recommended by https://stackoverflow.com/questions/3106452/how-do-servlets-work-instantiation-sessions-shared-variables-and-multithreadi as it is not thread-safe

  /**
   * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
   * methods.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  

  //Returns the values for JQuery autocomplete as a JSON file. JQuery will search this list for representing results
  //Two fields are supported with search: names and phone number. 
  private void upload(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
    Mb bean = (Mb)request.getSession(false).getAttribute("mb");
    EmployeeBean eBean = (EmployeeBean)request.getSession(false).getAttribute("employeeBean");
//    request.getSession(false).setAttribute("ACServlet",this);
    response.setContentType("application/json");
    JSONObject obj = new JSONObject();
    JSONArray jAr = new JSONArray();
    try (PrintWriter out = response.getWriter()) {
      if (eBean!=null){
        if (!bean.isUnderFilter()){//when table is already filtered autocomplete is inactive. So multi-level filter is not supported. 
          List<EmployeeFull> employees = eBean.getEmployees();
          Map<String, String> item = new HashMap<>();
          for (EmployeeFull employee : employees) {
              item.put("label", employee.getFirstname()+" "+employee.getLastname());
              item.put("value", employee.getFirstname()+" "+employee.getLastname());
              item.put("desc", "Name");
              jAr.put(item);
              if (!employee.getPhnumber().equals("")){
                item.put("label", employee.getPhnumber());
                item.put("value", employee.getFirstname()+" "+employee.getLastname());
                item.put("desc", "Phone number");
                jAr.put(item);
              }
          }
          obj.put("employees",jAr);
          out.print(obj);
  //        out.print("callback("+obj+");");  //for jsonp
          response.addHeader("state", "filled");
  //        response.setHeader("Access-Control-Allow-Origin", "https://localhost:8181");
  //        response.setHeader("Access-Control-Allow-Methods", "GET");
        }
        else{
          obj.put("employees",new JSONArray());
          out.print(obj);
          response.addHeader("state", "empty");
        }
      }
    }
  }
    
   
  //This method finally not used as it run into ConcurrentModificationException on ArrayList after LazyModel was implemented. W/o lazy model it worked fine
  //would have been called when item was selected form the autocomplete list and would have filtered datatable 
  private void refresh(HttpServletRequest request, HttpServletResponse response,String param)
          throws ServletException, IOException {
//    bean.employeeFilter(param);
  }
  
  protected void processRequest(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
//    getServletContext().setAttribute("ACservlet", this);
    String param = request.getParameter("item");
    switch(param){
      case "upload":
        upload(request, response);break;
      case "refresh":
        refresh(request, response,request.getParameter("value"));break;
    }
    

  }

  // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
  /**
   * Handles the HTTP <code>GET</code> method.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
    processRequest(request, response);
  }

  /**
   * Handles the HTTP <code>POST</code> method.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
    processRequest(request, response);
  }

  /**
   * Returns a short description of the servlet.
   *
   * @return a String containing servlet description
   */
  @Override
  public String getServletInfo() {
    return "Short description";
  }// </editor-fold>

}
