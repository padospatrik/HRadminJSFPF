
package helperbeans;


import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.ServletSecurity.TransportGuarantee;
import org.primefaces.push.annotation.OnMessage;
import org.primefaces.push.annotation.PushEndpoint;
import org.primefaces.push.impl.JSONEncoder;
 
@ServletSecurity(
@HttpConstraint(transportGuarantee = TransportGuarantee.CONFIDENTIAL))
@PushEndpoint("/push")
public class PushResource {
         
    @OnMessage(encoders = {JSONEncoder.class})
    public String onMessage(String message) {
      return message;
    }
 
}
