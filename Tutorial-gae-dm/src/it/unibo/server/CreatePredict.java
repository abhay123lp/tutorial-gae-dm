package it.unibo.server;

import it.unibo.shared.OAuth2ClientCredentials;
import it.unibo.shared.OAuth2Native;
import it.unibo.shared.Utility;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessProtectedResource;
import com.google.api.services.prediction.Prediction;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * @author Fabio Magnani, Enrico Gramellini.
 * Servlet richiamata al momento del consenso delle autorizzazioni per usare le Prediction API.
 */
@SuppressWarnings("serial")
public class CreatePredict extends RemoteServiceServlet{
	
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException{
		if (OAuth2ClientCredentials.CLIENT_ID != null || OAuth2ClientCredentials.CLIENT_SECRET != null) {
			GoogleAccessProtectedResource accessProtectedResource = OAuth2Native.exchangeCodeForToken(Utility.getTransport(),
					Utility.getJsonFactory(),
			        OAuth2ClientCredentials.CLIENT_ID,
			        OAuth2ClientCredentials.CLIENT_SECRET);
			Utility.setPrediction(Prediction.builder(Utility.getTransport(), Utility.getJsonFactory())
	    		.setApplicationName("Google-PredictionSample/1.0")
	    		.setHttpRequestInitializer(accessProtectedResource).build());
		}
	    if(Utility.isStartLocal())
	    	res.setHeader("Refresh","0; url=/Tutorial_gae_dm.html?gwt.codesvr=127.0.0.1:9997");
	    else
	    	res.setHeader("Refresh","0; url=/Tutorial_gae_dm.html");
	    
    }
}