package it.unibo.shared;

import com.google.api.client.extensions.appengine.http.urlfetch.UrlFetchTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.prediction.Prediction;

/**
 * @author Fabio Magnani, Enrico Gramellini.
 * Classe contenente informazioni e funzioni di base.
 *
 */
public class Utility {
	// Mi dice se l'applicazione gira in locale o meno.
	private static final boolean START_LOCALE = false;

	// Elemento usato nelle Prediction API.
	private static Prediction prediction = null;
	
	// Utili per le richieste delle API.
	private static final HttpTransport TRANSPORT = new UrlFetchTransport();
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();
  
	// OAuth 2 scope.
	public static final String SCOPE = "https://www.googleapis.com/auth/prediction";
	
	// Nome del modello creato dopo aver fatto il train.
	public static final String MODEL_ID = "language_prediction_model";
	
	// Legacy Storage Access Keys.
	public static final String LEGACY_STORAGE_ACCESS_KEYS = "pY3Jk8WoXaVCmxStQluBGsHHFZH5jGlYmOn2Ib80";
	// Google Access Key
	public static final String GOOGLE_ACCESS_KEYS = "GOOGNQVY66JXAQEQOOSK";
	// Bucket del Google Cloud Storage
	public static final String BUCKET = "fabiohelloprediction";
	// Nome File per le predizioni.
	public static final String FILE_PREDICT = "testLanguage.txt";
	// Locazioni del bucket nello storage. 
	public static final String STORAGE_DATA_LOCATION = BUCKET + "/" + FILE_PREDICT;

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
	
	/**
	 * @return Element prediction.
	 */
	public static Prediction getPrediction() {
		return prediction;
	}

	/**
	 * @param prediction Element prediction.
	 */
	public static void setPrediction(Prediction myPrediction) {
		prediction = myPrediction;
	}
}
