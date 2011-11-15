package it.unibo.server;

import it.unibo.client.GreetingService;
import it.unibo.shared.Attributo;
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
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.json.JsonHttpParser;
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
	
	// Mi dice se e' stato fatto almeno un train.
	private boolean train = false;
	
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException{
		if (OAuth2ClientCredentials.CLIENT_ID != null || OAuth2ClientCredentials.CLIENT_SECRET != null) {
	    	GoogleAccessProtectedResource accessProtectedResource = OAuth2Native.exchangeCodeForToken(Utility.getTransport(),
					Utility.getJsonFactory(),
			        OAuth2ClientCredentials.CLIENT_ID,
			        OAuth2ClientCredentials.CLIENT_SECRET);
			prediction = Prediction.builder(Utility.getTransport(), Utility.getJsonFactory())
	    		.setApplicationName("Google-PredictionSample/1.0")	    	
	    		.setHttpRequestInitializer(accessProtectedResource).build();
		}
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
	
	// Ritorna tutti gli attributi del dataset caricato precedentemente.
	@Override
	public Vector<Attributo> attributesDataset() throws Exception {
		if(model==null)
			throw new Exception("Internal Error - No model available. Load the model.");
		else{
			// Lettura degli attributi del dataset.
			return model.attributesDataset();
		}
		
	}
	
    // Classifica un messaggio usando Weka.
	@Override
	public String classifyMessage(String instance) throws Exception{
		if(model==null)
			throw new Exception("Internal Error - No model available. Load the model.");
		else
			// Esecuzione della predizione.
			return model.classifyMessage(instance);
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
    
    // Esegue la predizione sulla query inserita dall'utente.
    @Override
	public String doPredict(String query) throws Exception {
    	if(prediction == null)
    		throw new Exception("Internal Error - Access denied. Get authorization!!");
    	else
    	{
    		if(!train)
    			throw new Exception("Internal Error - Execute train!!");
    		else{
		  		// Preparo l'input.
			    Input input = new Input();
			    InputInput inputInput = new InputInput();
			    inputInput.setCsvInstance(Collections.<Object>singletonList(query));
			    input.setInput(inputInput);
			    // Eseguo la predizione
			    Output output = prediction.trainedmodels().predict(Utility.MODEL_ID, input).execute();
			    return "Predicted language: " + output.getOutputLabel();
    		}
    	}
	}

	@Override
	public String doTrain() throws Exception{
    	if(prediction == null)
    		throw new Exception("Internal Error - Access denied. Get authorization!!");
    	else
    	{ 
			// Settaggio del train con le informazioni impostate nell'utility.
			Training training = new Training();
			training.setId(Utility.MODEL_ID);
			training.setStorageDataLocation(Utility.STORAGE_DATA_LOCATION);
			prediction.trainedmodels().insert(training).execute();
			JsonHttpParser jsonHttpParser = new JsonHttpParser(Utility.getJsonFactory());
			
			int triesCounter = 0;
			while (triesCounter < 100) {
				// Se il modello non sara' trovato, verra' generato un errore 404 HttpResponseException.
				try {
					HttpResponse response = prediction.trainedmodels().get(Utility.MODEL_ID).executeUnparsed();
					if (response.getStatusCode() == 200) {
						training = jsonHttpParser.parse(response, Training.class);
						train = true;
						return training.getModelInfo().toString();
					}
					response.ignore();
				} 
				catch (HttpResponseException e) {
				}
	
				try {
					// Aspetto 5 secondi, poi riprovo.
					Thread.sleep(5000);
				} 
				catch (InterruptedException e) {
					break;
				}
				triesCounter++;
		    }
		    throw new Exception("Internal Error - Training not completed.");
    	}
	}
}
