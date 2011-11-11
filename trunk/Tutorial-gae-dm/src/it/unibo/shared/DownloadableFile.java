package it.unibo.shared;
import java.io.Serializable;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

/**
 * @author Fabio Magnani, Enrico Gramellini.
 * Classe che rappresenta la tabella "DownloadableFile" che si vuole inserire nel datastore.
 */
@SuppressWarnings("serial")
@PersistenceCapable
public class DownloadableFile implements Serializable {
	/**
	 * Chiave della tabella, e' l'identificativo del record.
	 */
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
	
	/**
	 * Nome del file da salvare.
	 */
	@Persistent
    private String fileName;
	
	/**
	 * Contenuto del file.
	 */
	@Persistent
	private byte[] file;

	/**
	 * @param fileName Nome del file da salvare.
	 * @param file Contenuto del file.
	 */
	public DownloadableFile( String fileName, byte[] file) {
		super();
		this.file = file;
		this.fileName = fileName;
	}
	/**
	 * @return Identificativo del record.
	 */
	public Key getKey() {
		return key;
	}
	/**
	 * @return Nome del file da salvare.
	 */
	public String getFilename() {
		return fileName;
	}
	/**
	 * @param fileName Nome del file da salvare.
	 */
	public void setFilename(String fileName) {
		this.fileName = fileName;
	}
	/**
	 * @return Contenuto del file.
	 */
	public byte[] getFile() {
		return file;
	}
	/**
	 * @param file Contenuto del file.
	 */
	public void setFile(byte[] file) {
		this.file = file;
	}
}