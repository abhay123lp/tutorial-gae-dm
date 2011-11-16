package it.unibo.server;


import it.unibo.shared.OAuth2Native;
import it.unibo.shared.Utility;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * @author Fabio Magnani, Enrico Gramellini.
 * Servlet richiamata al momento del consenso delle autorizzazioni per usare le Prediction API.
 */
@SuppressWarnings("serial")
public class ReceiverCode extends RemoteServiceServlet{
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException{
	    String code; 
      	String error = req.getParameter("error");
      	if (error != null) {
      		// L'utente non ha cosentito.
    	    if(Utility.isStartLocal())
    	    	res.setHeader("Refresh","0; url=/Tutorial_gae_dm.html?gwt.codesvr=127.0.0.1:9997");
    	    else
    	    	res.setHeader("Refresh","0; url=/Tutorial_gae_dm.html");
      	}
      	else{
      		// L'utente ha accettato, quindi prelevo il "code" rilasciato da google.
	      	code = req.getParameter("code");
	      	OAuth2Native.setCode(code);
	      	// Refresh della pagina.
	      	res.setHeader("Refresh", "0; url=/createPredict");
      	}
	}
}
