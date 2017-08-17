
package helperbeans;


import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.ServletSecurity.TransportGuarantee;
import org.primefaces.push.annotation.OnMessage;
import org.primefaces.push.annotation.PathParam;
import org.primefaces.push.annotation.PushEndpoint;
import org.primefaces.push.annotation.Singleton;
import org.primefaces.push.impl.JSONEncoder;
 
@ServletSecurity(
@HttpConstraint(transportGuarantee = TransportGuarantee.CONFIDENTIAL))
@PushEndpoint("/pushlogout/{sessionId}")
@Singleton
public class PushResourceLogout {
@PathParam("sessionId") private String sessionId;

    @OnMessage(encoders = {JSONEncoder.class})
    public String onMessage(String message) {
      return message;
    }
 
}
