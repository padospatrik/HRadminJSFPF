/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbeans;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


//https://javatutorial.net/glassfish-form-based-authentication-example
@Named(value = "login")
@SessionScoped
public class Login implements Serializable {
  
  private static Logger log = Logger.getLogger(Login.class.getName());

  private String username;
  private String password;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
  
  public String login() {
    FacesContext context = FacesContext.getCurrentInstance();
    HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
    try {
      request.login(username, password);
    } catch (ServletException e) {
      context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login failed!", null));
      return "login";
    }
    log.info("Authentication done for user: " + username);
    if (request.isUserInRole("hradmin")) {
      request.getSession().setAttribute("username", username);
      return "/index?faces-redirect=true";
    } else {
      return "login";
    }
  }

  public String logout() {
    FacesContext context = FacesContext.getCurrentInstance();
    HttpServletRequest request = (HttpServletRequest) context.getExternalContext()
            .getRequest();
    HttpSession session = request.getSession();
//    ((HttpServlet)session.getAttribute("ACServlet")).destroy();
//    ((HttpServlet)FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().get("ACservlet")).destroy();
//    FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().remove("ACservlet");
    try {
      request.logout();
    } catch (ServletException ex) {
      Logger.getLogger(Mb.class.getName()).log(Level.SEVERE, null, ex);
    }
    session.setAttribute("logout", "111");
    session.invalidate();
    return "/login?faces-redirect=true";
  }
  
}
