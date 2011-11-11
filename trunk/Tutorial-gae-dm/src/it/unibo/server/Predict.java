package it.unibo.server;

import it.unibo.shared.OAuth2ClientCredentials;
import it.unibo.shared.Utility;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAuthorizationRequestUrl;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class Predict extends RemoteServiceServlet{
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException{
		String authorizationUrl = new GoogleAuthorizationRequestUrl(OAuth2ClientCredentials.CLIENT_ID, "http://127.0.0.1:8888/receiverCode", Utility.SCOPE).build();
		res.setHeader("Refresh", "0; url="+authorizationUrl);
	}
}
