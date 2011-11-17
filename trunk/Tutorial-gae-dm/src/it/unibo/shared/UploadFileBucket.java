package it.unibo.shared;

import java.io.Serializable;

@SuppressWarnings("serial")
public class UploadFileBucket implements Serializable{
	// Contenuto del file.
	private String contentFile;
	// Politica di scrittura del bucket.
	private String policy;
	// Signature al Google Cloud Storage.
	private String signature;
	// Access Id per il Google Cloud Storage.
	private String googleAccessId;
	// Bucket sul Google Cloud Storage.
	private String bucket;
	// Nome del file.
	private String nameFile;
	// Caratteristiche acl.
	private String acl;
	// Tipo del file.
	private String contentType;
	
	public UploadFileBucket(){}
	
	/**
	 * @param contentFile Contenuto del file.
	 * @param policy Politica di scrittura del bucket.
	 * @param signature Signature al Google Cloud Storage.
	 * @param googleAccessId Access Id per il Google Cloud Storage.
	 * @param bucket Bucket sul Google Cloud Storage.
	 * @param nameFile Nome del file.
	 * @param acl Caratteristiche acl.
	 * @param contentType Tipo del file.
	 */
	public UploadFileBucket(String contentFile, String policy, String signature, String googleAccessId, String bucket, String nameFile, String acl, String contentType) {
		super();
		this.contentFile = contentFile;
		this.policy = policy;
		this.signature = signature;
		this.googleAccessId = googleAccessId;
		this.bucket = bucket;
		this.nameFile = nameFile;
		this.acl = acl;
		this.contentType = contentType;
	}
	/**
	 * @return Contenuto del file.
	 */
	public String getContentFile() {
		return contentFile;
	}
	/**
	 * @param contentFile Contenuto del file.
	 */
	public void setContentFile(String contentFile) {
		this.contentFile = contentFile;
	}
	/**
	 * @return Politica di scrittura del bucket.
	 */
	public String getPolicy() {
		return policy;
	}
	/**
	 * @param policy Politica di scrittura del bucket.
	 */
	public void setPolicy(String policy) {
		this.policy = policy;
	}
	/**
	 * @return Signature al Google Cloud Storage.
	 */
	public String getSignature() {
		return signature;
	}
	/**
	 * @param signature Signature al Google Cloud Storage.
	 */
	public void setSignature(String signature) {
		this.signature = signature;
	}

	/**
	 * @return Access Id per il Google Cloud Storage.
	 */
	public String getGoogleAccessId() {
		return googleAccessId;
	}

	/**
	 * @param googleAccessId Access Id per il Google Cloud Storage.
	 */
	public void setGoogleAccessId(String googleAccessId) {
		this.googleAccessId = googleAccessId;
	}

	/**
	 * @return Bucket sul Google Cloud Storage.
	 */
	public String getBucket() {
		return bucket;
	}

	/**
	 * @param bucket Bucket sul Google Cloud Storage.
	 */
	public void setBucket(String bucket) {
		this.bucket = bucket;
	}

	/**
	 * @return Nome del file.
	 */
	public String getNameFile() {
		return nameFile;
	}

	/**
	 * @param nameFile Nome del file.
	 */
	public void setNameFile(String nameFile) {
		this.nameFile = nameFile;
	}

	/**
	 * @return Caratteristiche acl.
	 */
	public String getAcl() {
		return acl;
	}

	/**
	 * @param acl Caratteristiche acl.
	 */
	public void setAcl(String acl) {
		this.acl = acl;
	}

	/**
	 * @return Tipo del file.
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * @param contentType Tipo del file.
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	
}
