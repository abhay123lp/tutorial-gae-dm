package it.unibo.server;

import it.unibo.shared.DownloadableFile;
import it.unibo.shared.PMF;
import it.unibo.shared.Utility;

import java.io.IOException;
import java.io.InputStream;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * @author Fabio Magnani, Enrico Gramellini.
 * Servlet per l'inserimento di un file su datastore.
 */
@SuppressWarnings("serial")
public class FileUpload extends HttpServlet {

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try {
			
			// Servlet per l'upload.
			ServletFileUpload upload = new ServletFileUpload();
			res.setContentType("text/plain");
			
			// Prendo i file selezionati dall'utente.
			FileItemIterator iterator = upload.getItemIterator(req);
			while (iterator.hasNext()) {
				FileItemStream item = iterator.next();
				InputStream stream = item.openStream();
				if (!item.isFormField()) {
					// I file vengono caricati come array di byte.
					byte[] buffer = new byte[stream.available()];
					while (stream.read(buffer, 0, buffer.length) != -1) {
						//System.out.println(buffer);
						// Serve per vedere se lo ha caricato bene. Quindi lo stampo nella risposta.
						res.getOutputStream().write(buffer);
					}
					// Preparo la classe che sara' inserita nel datastore.
					DownloadableFile file = new DownloadableFile(item.getName(), buffer);
					PersistenceManager pm = PMF.get().getPersistenceManager();
				    // Scrittura su datastore tramite le JDO.
			        pm.makePersistent(file);
				}
				stream.close();
			}
		} catch (Exception ex) {
			res.getOutputStream().println("Internal Error - Error upload.");
		}
		
	    // Faccio il refresh della pagina.
	    if(Utility.isStartLocal())
	    	res.setHeader("Refresh","3; url=/Tutorial_gae_dm.html?gwt.codesvr=127.0.0.1:9997");
	    else
	    	res.setHeader("Refresh","3; url=/Tutorial_gae_dm.html");
		
	}
}