package it.unibo.shared;
import java.io.Serializable;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@SuppressWarnings("serial")
@PersistenceCapable
public class DownloadableFile implements Serializable {
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
	
	@Persistent
    private String fileName;
	
	@Persistent
	private byte[] file;

	public DownloadableFile( String fileName, byte[] file) {
		super();
		this.file = file;
		this.fileName = fileName;
	}
	public Key getKey() {
		return key;
	}
	public String getFilename() {
		return fileName;
	}
	public void setFilename(String fileName) {
		this.fileName = fileName;
	}
	public byte[] getFile() {
		return file;
	}
	public void setFile(byte[] file) {
		this.file = file;
	}
}