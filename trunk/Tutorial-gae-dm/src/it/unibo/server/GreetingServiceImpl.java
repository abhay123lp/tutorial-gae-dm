package it.unibo.server;

import it.unibo.client.GreetingService;
import it.unibo.shared.Attributo;
import it.unibo.shared.DownloadableFile;
import it.unibo.shared.PMF;
import it.unibo.shared.RecordQuestbook;
import it.unibo.shared.UploadFileBucket;
import it.unibo.shared.Utility;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.Vector;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.api.client.auth.oauth.OAuthParameters;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.json.JsonHttpParser;
import com.google.api.services.prediction.model.Input;
import com.google.api.services.prediction.model.InputInput;
import com.google.api.services.prediction.model.Output;
import com.google.api.services.prediction.model.Training;
import com.google.appengine.api.rdbms.AppEngineDriver;
import com.google.appengine.repackaged.com.google.common.util.Base64;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Server per le chiamate RPC da parte del client.
 * @author Fabio Magnani, Enrico Gramellini.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements GreetingService {
	
	// Modello di weka.
	private ModelWeka model = null;
	
	// Mi dice se e' stato fatto almeno un train.
	private boolean train = false;

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
		    	c = DriverManager.getConnection("jdbc:google:rdbms://localhost:3306/prediction","root","fabio.magnani3");
	    	else
	    		// Connessione remoto.
	    		c = DriverManager.getConnection("jdbc:google:rdbms://tutorial-id:firstinstance/prediction");
	    	ResultSet rs = c.createStatement().executeQuery("SELECT * FROM entries");
	    	while (rs.next()){
	    		// Ogni record lo strasformo in un elemento della lista di ritorno.
	    		listGuestbook.add(new RecordQuestbook(rs.getString("entryID"),rs.getString("nationality"),rs.getString("content")));
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
    	if(Utility.getPrediction()==null)
    		return 0;
    	else
    		return 1;
    }
    
    // Esegue la predizione sulla query inserita dall'utente.
    @Override
	public String doPredict(String query) throws Exception {
    	if(Utility.getPrediction() == null)
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
			    Output output = Utility.getPrediction().trainedmodels().predict(Utility.MODEL_ID, input).execute();
			    return "Predicted language: " + output.getOutputLabel();
    		}
    	}
	}

    // Faccio il train del modello.
	@Override
	public String doTrain() throws Exception{
    	if(Utility.getPrediction() == null)
    		throw new Exception("Internal Error - Access denied. Get authorization!!");
    	else
    	{ 
			// Settaggio del train con le informazioni impostate nell'utility.
			Training training = new Training();
			training.setId(Utility.MODEL_ID);
			training.setStorageDataLocation(Utility.STORAGE_DATA_LOCATION);
			Utility.getPrediction().trainedmodels().insert(training).execute();
			JsonHttpParser jsonHttpParser = new JsonHttpParser(Utility.getJsonFactory());

			int triesCounter = 0;
			while (triesCounter < 100) {
				// Se il modello non sara' trovato, verra' generato un errore 404 HttpResponseException.
				try {
					HttpResponse response = Utility.getPrediction().trainedmodels().get(Utility.MODEL_ID).executeUnparsed();
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
	
	// Prepara i settaggi per fare l'upload di un file nel Google Cloud Storage.
	@Override
	public UploadFileBucket uploadDataPredict() throws Exception{
		UploadFileBucket setting = new UploadFileBucket();
		String acl = "private";
		String contentType = "application/txt";
		// Calcolo la data
        GregorianCalendar gc = new GregorianCalendar();
        gc.add(Calendar.MINUTE, 20);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        String expiration = df.format(gc.getTime());

        // Preparo il documento policy.
        String policyDocument = "";
        StringBuilder buf = new StringBuilder();
        buf.append("{\"expiration\": \"");
        buf.append(expiration);
        buf.append("\"");
        buf.append(",\"conditions\": [");
        buf.append(",{\"acl\": \"");
        buf.append(acl);
        buf.append("\"}");        
        buf.append("[\"starts-with\", \"$key\", \"\"],");
        buf.append("[\"eq\", \"$Content-Type\", \"" + contentType + "\"]");
        buf.append("]}");
        policyDocument = Base64.encode(buf.toString().replaceAll("\n", "").getBytes());
        
		// Prendo i dati da Google Cloud SQL.
		Vector<RecordQuestbook> dataPredict = dataCloud();
		// Metto tutti i dati in una stringa.
		String strDataPredict = "";
		RecordQuestbook temp;
		for(int i=0;i<dataPredict.size();i++){
			temp = dataPredict.get(i);
			strDataPredict = strDataPredict.concat("\"" + temp.getNationality() + "\",\""+ temp.getContent() +"\"\n");
		}
		
		// Preparo la signature.
		String signature = "";
        try {
        	String secret = OAuthParameters.escape(Utility.LEGACY_STORAGE_ACCESS_KEYS);
            Mac mac = Mac.getInstance("HmacSHA1");
            byte[] secretBytes = secret.getBytes("UTF8");
            SecretKeySpec signingKey = new SecretKeySpec(secretBytes, "HmacSHA1");
            mac.init(signingKey);
            byte[] signedSecretBytes = mac.doFinal(policyDocument.getBytes("UTF8"));
            signature = Base64.encode(signedSecretBytes);
        } catch (InvalidKeyException e) {
        	throw new Exception("Internal Error - Invalid Key for signature.");
        } catch (NoSuchAlgorithmException e) {
        	throw new Exception("Internal Error - No such Algorithm.");
        } catch (UnsupportedEncodingException e) {
        	throw new Exception("Internal Error - Unsupported Encoding.");
        }
        
        // Setto gli elementi appena creati.
        setting.setPolicy(policyDocument);
        setting.setContentFile(strDataPredict);
        setting.setSignature(signature);
        setting.setGoogleAccessId(Utility.GOOGLE_ACCESS_KEYS);
        setting.setBucket(Utility.BUCKET);
        setting.setAcl(acl);
        setting.setNameFile(Utility.FILE_PREDICT);
        setting.setContentType(contentType);
        return setting;
	}
}
