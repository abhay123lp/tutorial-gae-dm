/*
 * Copyright (c) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package it.unibo.shared;

import java.io.IOException;

import com.google.api.client.auth.oauth2.draft10.AccessTokenResponse;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessProtectedResource;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessTokenRequest.GoogleAuthorizationCodeGrant;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;

/**
 * Implementa l'autentificazione attraverso OAuth.
 * 
 * @author Fabio Magnani, Enrico Gramellini
 */
public class OAuth2Native {

	// Codice ricevuto da Google quando l'utente da il consenso nella richiesta di autorizzazione.
	private static String CODE = "";
	
	/**
	 * @param code Codice di sicurezza ricevuto da Google.
	 */
	public static void setCode(String code){
		CODE = code;
	}
	
	/**
	 * @return Codice di sicurezza ricevuto da Google.
	 */
	public static String getCode(){
		return CODE;
	}
	
	/**
	 * @param transport Oggetto utile per l'acceso.
	 * @param jsonFactory Oggetto utile per l'acceso.
	 * @param clientId Client ID delle credenziali di OAuth.
	 * @param clientSecret Client Secret delle credenziali di OAuth.
	 * @return Oggetto che mi consente di usare le Prediction API.
	 */
	public static GoogleAccessProtectedResource exchangeCodeForToken(HttpTransport transport,
	      JsonFactory jsonFactory,
	      String clientId,
	      String clientSecret){
	    
		AccessTokenResponse response = null;
		// Redirezione dopo aver preso il token.
		String redirectUrl = "";
		if(Utility.isStartLocal())
			redirectUrl = "http://127.0.0.1:8888/receiverCode";
		else
			redirectUrl = "http://tutorial-gae-dm.appspot.com/receiverCode";
		
	    try {
	    	// Prendo il token attraverso il codice.
			response = exchangeCodeForAccessToken(redirectUrl,transport,jsonFactory,clientId,clientSecret);
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		// Ritorno l'oggetto che mi consente di usare le Prediction API.
		return new GoogleAccessProtectedResource(response.accessToken,transport,jsonFactory,clientId,clientSecret,response.refreshToken) {
		    @Override
		    protected void onAccessToken(String accessToken) {
		    }
		};
	}

  	/**
  	 * Prendo il token attraverso il codice.
  	 * @param redirectUrl Redirezione dopo aver preso il token.
  	 * @param transport Oggetto utile per l'acceso.
  	 * @param jsonFactory Oggetto utile per l'acceso.
	 * @param clientId Client ID delle credenziali di OAuth.
	 * @param clientSecret Client Secret delle credenziali di OAuth.
  	 * @return Token.
  	 * @throws IOException 
  	 */
  	private static AccessTokenResponse exchangeCodeForAccessToken(String redirectUrl,
		HttpTransport transport,
		JsonFactory jsonFactory,
		String clientId,
		String clientSecret) throws IOException {
  		
  		try {
  			// Scambio il codice per il token di accesso.
  			return new GoogleAuthorizationCodeGrant(new NetHttpTransport(),
				jsonFactory,
				clientId,
				clientSecret,
				CODE,
				redirectUrl).execute();
  		} 
  		catch (HttpResponseException e) {
  			return null;
  		}
  }

  private OAuth2Native() {
  }
}
