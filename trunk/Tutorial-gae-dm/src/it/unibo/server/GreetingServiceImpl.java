package it.unibo.server;

import it.unibo.client.GreetingService;
import it.unibo.shared.DownloadableFile;
import it.unibo.shared.OAuth2ClientCredentials;
import it.unibo.shared.OAuth2Native;
import it.unibo.shared.PMF;
import it.unibo.shared.RecordQuestbook;
import it.unibo.shared.Utility;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessProtectedResource;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonError.ErrorInfo;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.json.JsonHttpParser;
import com.google.api.client.json.Json;
import com.google.api.services.prediction.Prediction;
import com.google.api.services.prediction.model.Input;
import com.google.api.services.prediction.model.InputInput;
import com.google.api.services.prediction.model.Output;
import com.google.api.services.prediction.model.Training;
import com.google.appengine.api.rdbms.AppEngineDriver;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Server per le chiamate RPC da parte del client.
 * @author Fabio Magnani, Enrico Gramellini.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements GreetingService {
	
	// Elemento usato nelle Prediction API.
	private Prediction prediction = null;
	
	// Modello di weka.
	private ModelWeka model = null;
	
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException{
    	GoogleAccessProtectedResource accessProtectedResource = OAuth2Native.exchangeCodeForToken(Utility.getTransport(),
				Utility.getJsonFactory(),
		        OAuth2ClientCredentials.CLIENT_ID,
		        OAuth2ClientCredentials.CLIENT_SECRET);
		prediction = Prediction.builder(Utility.getTransport(), Utility.getJsonFactory())
    		.setApplicationName("Google-PredictionSample/1.0")
    		.setHttpRequestInitializer(accessProtectedResource).build();
	    if(Utility.isStartLocal())
	    	res.setHeader("Refresh","0; url=/Tutorial_gae_dm.html?gwt.codesvr=127.0.0.1:9997");
	    else
	    	res.setHeader("Refresh","0; url=/Tutorial_gae_dm.html");
    }

    // Legge i dati della guestbook per il tutorial Google Cloud SQL.
	@Override
	public Vector<RecordQuestbook> dataCloud() throws Exception {
		// Dati della guestbook.
		Vector<RecordQuestbook> listGuestbook = new Vector<RecordQuestbook>();
		Connection c = null;
		
		try {
			// Imposto la connessione.
	    	DriverManager.registerDriver(new AppEngineDriver());
	    	if(Utility.isStartLocal())
		    	// Connessione locale.
		    	c = DriverManager.getConnection("jdbc:google:rdbms://localhost:3306/guestbook","root","fabio.magnani3");
	    	else
	    		// Connessione remoto.
	    		c = DriverManager.getConnection("jdbc:google:rdbms://tutorial-id:firstinstance/guestbook");
	    	ResultSet rs = c.createStatement().executeQuery("SELECT * FROM entries");
	    	while (rs.next()){
	    		// Ogni record lo strasformo in un elemento della lista di ritorno.
	    		listGuestbook.add(new RecordQuestbook(rs.getString("entryID"),rs.getString("guestName"),rs.getString("content")));
	    	}
	    } 
	    catch (SQLException e) {
	    	throw new Exception("Internal Error - Database connection failed"); 
		} 
	    finally {
	    	if (c != null) {
	    		try {
	    			c.close();
	            } 
	    		catch (SQLException ignore) {
	            }
	    	}
	    }
	    
	    // Ritorno tutti i dati appena letti.
		return listGuestbook;
	}
	
    // Crea il classificatore per il tutorial su Weka.
	@Override
	public String serviceWeka(String nameFile) throws Exception{
		String result = null;
		// Creazione del modello.
		model = new ModelWeka();
		// Esecuzione della classificazione.
		result = model.makeClassifier(nameFile);
		
		return result;
	}
	
    // Classifica un messaggio usando Weka.
	@Override
	public String classifyMessage(String instance) throws Exception{
		if(model==null)
			throw new Exception("Internal Error - No model available. Load the model.");
		else{
			// Esecuzione della predizione.
			String result = model.classifyMessage(instance);
			return result;
		}
	}	

    // Legge tutti i dataset gia' caricati per il tutorial Weka.
	@SuppressWarnings("unchecked")
	@Override
	public Vector<String> datasetWeka() {
		// Lista dei dataset caricati sul datastore.
		Vector<String> listDataset = new Vector<String>();
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		// Lettura
		Query query = pm.newQuery(DownloadableFile.class);

		List<DownloadableFile> results = (List<DownloadableFile>) query.execute();
		if (!results.isEmpty()) {
			for (DownloadableFile file : results) {
				listDataset.add(file.getFilename());
			}
		}
		query.closeAll();
	    
	    // Ritorno la lista.
		return listDataset;
	}
	
	// Mi dice se devono essere chieste le autorizzazioni o meno per il tutorial sulle Prediction API.
    @Override
    public int authorization(){
    	if(prediction==null)
    		return 0;
    	else
    		return 1;
    }
    
    @Override
	public String doPredict(String input) {
		
		if (OAuth2ClientCredentials.CLIENT_ID == null || OAuth2ClientCredentials.CLIENT_SECRET == null) {
	          System.err.println("Please enter your client ID and secret in " + OAuth2ClientCredentials.class);
		}
		else{
			try {
	        	predict(prediction, "Is this sentence in English?");
	        	predict(prediction, "Esta frase es en Español?");
	        	predict(prediction, "Est-ce cette phrase en Français?");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return "Prediction OK";
	}

	@Override
	public String doTrain() {
		try {
			try {
				if (OAuth2ClientCredentials.CLIENT_ID == null || OAuth2ClientCredentials.CLIENT_SECRET == null) {
					System.err.println( "Please enter your client ID and secret in " + OAuth2ClientCredentials.class);
				} else {
					train(prediction);
				}
				// success!
				return "Train OK";
			} catch (HttpResponseException e) {
				if (!Json.CONTENT_TYPE.equals(e.getResponse().getContentType())) {
					System.err.println(e.getResponse().parseAsString());
		        } else {
		        	GoogleJsonError errorResponse = GoogleJsonError.parse(Utility.getJsonFactory(), e.getResponse());
		        	System.err.println(errorResponse.code + " Error: " + errorResponse.message);
		        	for (ErrorInfo error : errorResponse.errors) {
		        		System.err.println(Utility.getJsonFactory().toString(error));
		        	}
		        }
			}
	    } catch (Throwable t) {
	    	t.printStackTrace();
	    }
	    // Fallimento train
	    return "Train KO";
	}

	private static void train(Prediction prediction) throws IOException {
		Training training = new Training();
		training.setId(Utility.MODEL_ID);
		training.setStorageDataLocation(Utility.STORAGE_DATA_LOCATION);
		prediction.trainedmodels().insert(training).execute();
		System.out.println("Training started.");
		System.out.print("Waiting for training to complete");
		System.out.flush();

		JsonHttpParser jsonHttpParser = new JsonHttpParser(Utility.getJsonFactory());
		int triesCounter = 0;
		while (triesCounter < 100) {
			// NOTE: if model not found, it will throw an HttpResponseException with a 404 error
			try {
				HttpResponse response = prediction.trainedmodels().get(Utility.MODEL_ID).executeUnparsed();
				if (response.getStatusCode() == 200) {
					training = jsonHttpParser.parse(response, Training.class);
					System.out.println();
					System.out.println("Training completed.");
					System.out.println(training.getModelInfo());
					return;
				}
				response.ignore();
			} 
			catch (HttpResponseException e) {
			}

			try {
				// 5 seconds times the tries counter
				Thread.sleep(5000 * (triesCounter + 1));
			} 
			catch (InterruptedException e) {
				break;
			}
			System.out.print(".");
			System.out.flush();
			triesCounter++;
	    }
	    error("ERROR: training not completed.");
  	}

  	private static void error(String errorMessage) {
  		System.err.println();
  		System.err.println(errorMessage);
	  	System.exit(1);
  	}

  	private static void predict(Prediction prediction, String text) throws IOException {
	    Input input = new Input();
	    InputInput inputInput = new InputInput();
	    inputInput.setCsvInstance(Collections.<Object>singletonList(text));
	    input.setInput(inputInput);
	    Output output = prediction.trainedmodels().predict(Utility.MODEL_ID, input).execute();
	    System.out.println("Text: " + text);
	    System.out.println("Predicted language: " + output.getOutputLabel());
  	}
    

}
