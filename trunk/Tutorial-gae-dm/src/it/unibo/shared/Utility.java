package it.unibo.shared;

import com.google.api.client.extensions.appengine.http.urlfetch.UrlFetchTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;

/**
 * @author Fabio Magnani, Enrico Gramellini.
 * Classe contenente informazioni e funzioni di base.
 *
 */
public class Utility {
	// Mi dice se l'applicazione gira in locale o meno.
	static boolean START_LOCALE = true;
	
	// Utili per le richieste delle API.
	private static final HttpTransport TRANSPORT = new UrlFetchTransport();
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();
  
	// OAuth 2 scope.
	public static final String SCOPE = "https://www.googleapis.com/auth/prediction";
	// Locazioni del bucket nello storage. 
	public static final String STORAGE_DATA_LOCATION = "fabiohelloprediction/language_id.txt";
	// Nome del modello creato dopo aver fatto il train.
	public static final String MODEL_ID = "language_prediction_model";
	
	/**
	 * Serve per capire se l'applicazione gira in locale.
	 * @return True, se l'applicazione gira in locale, False altrimenti.
	 */
	public static boolean isStartLocal(){
		return START_LOCALE;
	}
	
	/**
	 * @return Oggetto HttpTransport creato.
	 */
	public static HttpTransport getTransport() {
		return TRANSPORT;
	}

	/**
	 * @return Oggetto JsonFactory creato.
	 */
	public static JsonFactory getJsonFactory() {
		return JSON_FACTORY;
	}
}
