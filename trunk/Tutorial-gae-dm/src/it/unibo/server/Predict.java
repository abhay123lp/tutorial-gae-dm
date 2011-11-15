package it.unibo.server;

import it.unibo.shared.OAuth2ClientCredentials;
import it.unibo.shared.Utility;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAuthorizationRequestUrl;
import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * @author Fabio Magnani, Enrico Gramellini.
 * Servlet usata per la richiesta delle autorizzazioni per l'uso delle Prediction API. 
 */
@SuppressWarnings("serial")
public class Predict extends RemoteServiceServlet{
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException{
		String redirectUrl = "";
		String authorizationUrl = "";
		if(Utility.isStartLocal()){
			// L'applicazione sta girando in locale.
			redirectUrl = "http://127.0.0.1:8888/receiverCode";
			// Richiesta dell'autorizzazioni. Mi ritorna l'url che contiene la richiesta da fare all'utente.
			authorizationUrl = new GoogleAuthorizationRequestUrl(OAuth2ClientCredentials.CLIENT_ID, redirectUrl, Utility.SCOPE).setAccessType("offline").build();
		}
		else {
			// L'applicazione gira sul web.
			redirectUrl = "http://tutorial-gae-dm.appspot.com/receiverCode";
			// Richiesta dell'autorizzazioni. Mi ritorna l'url che contiene la richiesta da fare all'utente.
			authorizationUrl = new GoogleAuthorizationRequestUrl(OAuth2ClientCredentials.CLIENT_ID, redirectUrl, Utility.SCOPE).setAccessType("online").build();
		}
		// Redirigo la pagina.
		res.setHeader("Refresh", "0; url="+authorizationUrl);
	}
}
