/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helperbeans;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.primefaces.push.EventBus;
import org.primefaces.push.EventBusFactory;

/**
 * Web application lifecycle listener.
 */
public class SessionListener implements HttpSessionListener {

  @Override
  public void sessionCreated(HttpSessionEvent se) {
  }

  @Override
  public void sessionDestroyed(HttpSessionEvent se) {
    HttpSession session = se.getSession();
    if (session.getAttribute("logout")!=null){
      if (!session.getAttribute("logout").equals("111")){
        EventBus eventBus = EventBusFactory.getDefault().eventBus();
//        System.out.println("session Destroyed");
//        System.out.println(se.getSession().getId());  //Id keeps changing even when session remains the same. I don't know the reason, but ID can't be used for identifying the session.
        eventBus.publish("/pushlogout/"+session.getAttribute("logout"), "0");
      } 
    }
  }
}
