package it.unibo.shared;

import java.io.Serializable;


/**
 * @author Fabio Magnani, Enrico Gramellini.
 * Classe per l'uso dei record della tabella per la lettura dei dati della guestbook.
 *
 */
@SuppressWarnings("serial")
public class RecordQuestbook implements Serializable {
	// Identificativo del record.
	private String entryID;
	// Nome dell'utente.
	private String guestName;
	// Messaggio dell'utente.
	private String content;
	
	// Numero dei campi.
	private static int NUM_FIELDS=3;
	
	public RecordQuestbook() {}
	
	/**
	 * @param entryID Identificativo del record. 
	 * @param guestName Nome dell'utente.
	 * @param content Messaggio dell'utente.
	 */ 
	public RecordQuestbook(String entryID, String guestName, String content) {
		super();
		this.entryID = entryID;
		this.guestName = guestName;
		this.content = content;
	}

	/**
	 * @return Identificativo del record.
	 */
	public String getEntryID() {
		return entryID;
	}

	/**
	 * @param entryID Identificativo del record.
	 */
	public void setEntryID(String entryID) {
		this.entryID = entryID;
	}

	/**
	 * @return Nome dell'utente.
	 */
	public String getGuestName() {
		return guestName;
	}

	/**
	 * @param guestName Nome dell'utente.
	 */
	public void setGuestName(String guestName) {
		this.guestName = guestName;
	}

	/**
	 * @return Messaggio dell'utente.
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content Messaggio dell'utente.
	 */
	public void setContent(String content) {
		this.content = content;
	}
	
	/**
	 * @param i Indice del campo che si vuole ottenere.
	 * @return Valore del campo richiesto.
	 */
	public String getCampo(int i){
		if(i==0)
			return entryID;
		else if(i==1)
			return guestName;
		else
			return content;
	}
	
	/**
	 * @return Numero dei campi del record.
	 */
	public int getNumFields(){
		return NUM_FIELDS;
	}
	
}
