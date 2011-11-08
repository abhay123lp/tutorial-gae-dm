package it.unibo.server;

import it.unibo.client.GreetingService;
import it.unibo.shared.DownloadableFile;
import it.unibo.shared.PMF;
import it.unibo.shared.RecordQuestbook;
import it.unibo.shared.Utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.appengine.api.rdbms.AppEngineDriver;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements GreetingService {
	
	public Vector<RecordQuestbook> dataCloud() {
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
	        e.printStackTrace();
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
	
	public String serviceWeka(String nameFile){
		ModelWeka model;
		String result = null;
		// Creazione del modello.
		model = new ModelWeka();
		// Esecuzione della classificazione.
		result = model.doJob(nameFile);
		if(result!=null){
			if(result.equals(""))
				result = "ERRORE RISULTATO VUOTO";
		}
		else
			result = "ERRORE RISULTATO NULLO";
		
		return result;
	}

	public Vector<String> datasetWeka() {
		// Dati della guestbook.
		Vector<String> listDataset = new Vector<String>();
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		// Lettura
		Query query = pm.newQuery(DownloadableFile.class);

		try {
			List<DownloadableFile> results = (List<DownloadableFile>) query.execute();
			if (!results.isEmpty()) {
				for (DownloadableFile file : results) {
					listDataset.add(file.getFilename());
				}
			}
		} finally {
			query.closeAll();
		} 
	    
	    // Ritorno tutti i dati appena letti.
		return listDataset;
	}

}