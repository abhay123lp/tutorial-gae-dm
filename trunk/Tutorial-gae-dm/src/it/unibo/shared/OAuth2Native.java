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

import java.awt.Desktop;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.auth.oauth2.draft10.AccessTokenErrorResponse;
import com.google.api.client.auth.oauth2.draft10.AccessTokenResponse;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessProtectedResource;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAuthorizationRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessTokenRequest.GoogleAuthorizationCodeGrant;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Implements OAuth authentication "native" flow recommended for installed clients in which the end
 * user must grant access in a web browser and then copy a code into the application.
 *
 * @author Yaniv Inbar
 */
@SuppressWarnings("serial")
public class OAuth2Native {

	private static String CODE = "";
	
	public static void setCode(String code){
		CODE = code;
	}
	
	public static String getCode(){
		return CODE;
	}
	
	public static GoogleAccessProtectedResource exchangeCodeForToken(HttpTransport transport,
	      JsonFactory jsonFactory,
	      String clientId,
	      String clientSecret){
	    
		AccessTokenResponse response = null;
		String redirectUrl = "";
		if(Utility.isStartLocal())
			redirectUrl = "http://127.0.0.1:8888/receiverCode";
		else
			redirectUrl = "http://tutorial-gae-dm.appspot.com/receiverCode";
		
	    try {
			response = exchangeCodeForAccessToken(redirectUrl,
			transport,
			jsonFactory,
			clientId,
			clientSecret);
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		return new GoogleAccessProtectedResource(response.accessToken,
			transport,
			jsonFactory,
			clientId,
			clientSecret,
			response.refreshToken) {
		
		    @Override
		    protected void onAccessToken(String accessToken) {
		    }
		};
	}

  	private static AccessTokenResponse exchangeCodeForAccessToken(String redirectUrl,
		HttpTransport transport,
		JsonFactory jsonFactory,
		String clientId,
		String clientSecret) throws IOException {
  		
  		try {
  			// exchange code for an access token
  			return new GoogleAuthorizationCodeGrant(new NetHttpTransport(),
				jsonFactory,
				clientId,
				clientSecret,
				CODE,
				redirectUrl).execute();
  		} 
  		catch (HttpResponseException e) {
  			AccessTokenErrorResponse response = e.getResponse().parseAs(AccessTokenErrorResponse.class);
  			System.out.println();
  			System.err.println("Error: " + response.error);
  			System.out.println();
  			System.exit(1);
  			return null;
  		}
  }

  private OAuth2Native() {
  }
}
