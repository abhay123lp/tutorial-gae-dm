package it.unibo.server;


import it.unibo.shared.OAuth2Native;
import it.unibo.shared.Utility;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class ReceiverCode extends RemoteServiceServlet{
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException{
	    String code;  
      	String error = req.getParameter("error");
      	if (error != null) {
      		System.out.println("Authorization failed. Error=" + error);
      		System.out.println("Quitting.");
    	    if(Utility.isStartLocal())
    	    	res.setHeader("Refresh","0; url=/Tutorial_gae_dm.html?gwt.codesvr=127.0.0.1:9997");
    	    else
    	    	res.setHeader("Refresh","0; url=/Tutorial_gae_dm.html");
      	}
      	else{
	      	code = req.getParameter("code");
	      	OAuth2Native.setCode(code);
	      	res.setHeader("Refresh", "0; url=/tutorial_gae_dm/greet");
      	}
	}
}
