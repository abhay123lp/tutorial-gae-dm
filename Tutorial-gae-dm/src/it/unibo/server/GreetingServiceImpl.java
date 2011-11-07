package it.unibo.server;

import it.unibo.client.GreetingService;
import it.unibo.shared.RecordQuestbook;
import it.unibo.shared.Utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

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
}
