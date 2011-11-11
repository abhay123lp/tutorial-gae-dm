package it.unibo.shared;

import com.google.api.client.extensions.appengine.http.urlfetch.UrlFetchTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;

/**
 * <p>
 * FieldVerifier validates that the name the user enters is valid.
 * </p>
 * <p>
 * This class is in the <code>shared</code> package because we use it in both
 * the client code and on the server. On the client, we verify that the name is
 * valid before sending an RPC request so the user doesn't have to wait for a
 * network round trip to get feedback. On the server, we verify that the name is
 * correct to ensure that the input is correct regardless of where the RPC
 * originates.
 * </p>
 * <p>
 * When creating a class that is used on both the client and the server, be sure
 * that all code is translatable and does not use native JavaScript. Code that
 * is not translatable (such as code that interacts with a database or the file
 * system) cannot be compiled into client side JavaScript. Code that uses native
 * JavaScript (such as Widgets) cannot be run on the server.
 * </p>
 */
public class Utility {
	static boolean START_LOCALE = true;
	
	private static final HttpTransport TRANSPORT = new UrlFetchTransport();
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();
  
	/** OAuth 2 scope. */
	public static final String SCOPE = "https://www.googleapis.com/auth/prediction";
	
	public static final String STORAGE_DATA_LOCATION = "fabiohelloprediction/language_id.txt";
	
	public static final String MODEL_ID = "language_prediction_model";
	
	/**
	 * Verifies that the specified name is valid for our service.
	 * 
	 * In this example, we only require that the name is at least four
	 * characters. In your application, you can use more complex checks to ensure
	 * that usernames, passwords, email addresses, URLs, and other fields have the
	 * proper syntax.
	 * 
	 * @param name the name to validate
	 * @return true if valid, false if invalid
	 */
	public static boolean isValidName(String name) {
		if (name == null) {
			return false;
		}
		return name.length() > 3;
	}
	
	public static boolean isStartLocal(){
		return START_LOCALE;
	}
	
	public static HttpTransport getTransport() {
		return TRANSPORT;
	}

	public static JsonFactory getJsonFactory() {
		return JSON_FACTORY;
	}
}
