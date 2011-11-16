package it.unibo.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.rdbms.AppEngineDriver;
import it.unibo.shared.Utility;

/**
 * @author Fabio Magnani, Enrico Gramellini.
 * Servlet per inserire nell'istanza Google Cloud SQL nuovi record. 
 */
@SuppressWarnings("serial")
public class GuestbookServlet extends HttpServlet{
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException{
		PrintWriter out = res.getWriter();
		Connection c = null;
		String timeRedirect="0; ";
		try {
			// Imposto la connessione.
	    	DriverManager.registerDriver(new AppEngineDriver());
	    	if(Utility.isStartLocal())
		    	// Connessione locale.
		    	c = DriverManager.getConnection("jdbc:google:rdbms://localhost:3306/prediction","root","fabio.magnani3");
	    	else
	    		// Connessione remoto.
	    		c = DriverManager.getConnection("jdbc:google:rdbms://tutorial-id:firstinstance/prediction");
	    	// Recupero i parametri della form.
	    	req.getParameterNames();
	    	String guestName = req.getParameter("nationality");
	    	String content = req.getParameter("content");
	    	if (guestName == "" || content == "") {
	    		out.println("<html><head></head><body>You are missing either a message or a name! Try again! Redirecting in 3 seconds...</body></html>");
	    	}
	    	else {
	    		// Preparo l'istruzione SQL da eseguire.
	    		String statement ="INSERT INTO entries (nationality, content) VALUES( ? , ? )";
	    		PreparedStatement stmt = c.prepareStatement(statement);
	    		// Setto i parametri della form nell'istruzione SQL.
	    		stmt.setString(1, guestName);
	    		stmt.setString(2, content);
	    		int success = 2;
	    		// Eseguo l'SQL.
	    		success = stmt.executeUpdate();
	    		if(success == 0) {
	    			// L'istruzione non andata a buon fine.
	    			out.println("<html><head></head><body>Failure! Please try again! Redirecting in 3 seconds...</body></html>");
	    			timeRedirect="3; ";
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
	    // Faccio il refresh della pagina.
	    if(Utility.isStartLocal())
	    	res.setHeader("Refresh",timeRedirect + "url=/Tutorial_gae_dm.html?gwt.codesvr=127.0.0.1:9997");
	    else
	    	res.setHeader("Refresh",timeRedirect + "url=/Tutorial_gae_dm.html");
	}
}
