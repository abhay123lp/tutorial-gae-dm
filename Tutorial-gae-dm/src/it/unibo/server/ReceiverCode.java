package it.unibo.server;


import it.unibo.shared.OAuth2Native;

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
      		System.exit(1);
      	}
      	code = req.getParameter("code");
      	OAuth2Native.setCode(code);
      	res.setHeader("Refresh", "0; url=/tutorial_gae_dm/greet");
	}
}
