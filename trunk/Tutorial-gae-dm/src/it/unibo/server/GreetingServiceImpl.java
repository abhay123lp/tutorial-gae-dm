package it.unibo.server;

import it.unibo.client.GreetingService;
import it.unibo.shared.RecordQuestbook;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.rdbms.AppEngineDriver;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements GreetingService {
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		PrintWriter out = resp.getWriter();
		Connection c = null;
		try {
			// Imposto la connessione
	    	DriverManager.registerDriver(new AppEngineDriver());
	    	// Connessione remoto
	    	//c = DriverManager.getConnection("jdbc:google:rdbms://tutorial-id:firstinstance/guestbook");
	    	// Connessione locale
	    	c = DriverManager.getConnection("jdbc:google:rdbms://localhost:3306/guestbook","root","fabio.magnani3");
	    	// Recupero i parametri della form
	    	String guestName = req.getParameter("guestName");
	    	String content = req.getParameter("content");
	    	if (guestName == "" || content == "") {
	    		out.println("<html><head></head><body>You are missing either a message or a name! Try again! Redirecting in 3 seconds...</body></html>");
	    	}
	    	else {
	    		// Preparo l'istruzione SQL da eseguire.
	    		String statement ="INSERT INTO entries (guestName, content) VALUES( ? , ? )";
	    		PreparedStatement stmt = c.prepareStatement(statement);
	    		stmt.setString(1, guestName);
	    		stmt.setString(2, content);
	    		int success = 2;
	    		success = stmt.executeUpdate();
	    		if(success == 1) {
	    			out.println("<html><head></head><body>Success! Redirecting in 3 seconds...</body></html>");
	    		} 
	    		else if (success == 0) {
	    			out.println("<html><head></head><body>Failure! Please try again! Redirecting in 3 seconds...</body></html>");
	    		}
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
	    resp.setHeader("Refresh","3; url=/Tutorial_gae_dm.html?gwt.codesvr=127.0.0.1:9997");
	}
	
	public Vector<RecordQuestbook> dataCloud() {
		Vector<RecordQuestbook> listGuestbook = new Vector<RecordQuestbook>();
		Connection c = null;
		
		try {
			// Imposto la connessione
	    	DriverManager.registerDriver(new AppEngineDriver());
	    	// Connessione remoto
	    	//c = DriverManager.getConnection("jdbc:google:rdbms://tutorial-id:firstinstance/guestbook");
	    	// Connessione locale
	    	c = DriverManager.getConnection("jdbc:google:rdbms://localhost:3306/guestbook","root","fabio.magnani3");
	    	ResultSet rs = c.createStatement().executeQuery("SELECT * FROM entries");
	    	while (rs.next()){
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

		return listGuestbook;
	}
}
