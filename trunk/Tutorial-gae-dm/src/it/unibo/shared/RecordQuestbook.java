package it.unibo.shared;

import java.io.Serializable;


public class RecordQuestbook implements Serializable {
	private String campo1;
	private String campo2;
	private String campo3;
	// Numero dei campi
	private static int NUM_FIELDS=3;
	
	public RecordQuestbook() {}
	
	public RecordQuestbook(String campo1, String campo2, String campo3) {
		this.campo1 = campo1;
		this.campo2 = campo2;
		this.campo3 = campo3;
	}
	public String getCampo1() {
		return campo1;
	}
	public void setCampo1(String campo1) {
		this.campo1 = campo1;
	}
	public String getCampo2() {
		return campo2;
	}
	public void setCampo2(String campo2) {
		this.campo2 = campo2;
	}
	public String getCampo3() {
		return campo3;
	}
	public void setCampo3(String campo3) {
		this.campo3 = campo3;
	}
	public String getCampo(int i){
		if(i==0)
			return campo1;
		else if(i==1)
			return campo2;
		else
			return campo3;
	}
	public int getNumFields(){
		return NUM_FIELDS;
	}
	
}
