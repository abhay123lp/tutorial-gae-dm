package it.unibo.client;


import it.unibo.shared.RecordQuestbook;

import java.util.Vector;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Tutorial_gae_dm implements EntryPoint {

	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final GreetingServiceAsync greetingService = GWT
			.create(GreetingService.class);

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		final Button cloudButton = new Button("Google Cloud SQL");
		final Button wekaButton = new Button("Weka");
		final Button predictionButton = new Button("Google Prediction API");
		final HorizontalPanel hPanel = new HorizontalPanel();
		final Grid table = new Grid();
		
		// Imposto lo stile ai bottoni
		cloudButton.addStyleName("button");
		wekaButton.addStyleName("button");
		predictionButton.addStyleName("button");
		
		// Setto il titolo dei bottoni in modo tale da poterli identificare.
		cloudButton.setTitle("Cloud");
		wekaButton.setTitle("Weka");
		predictionButton.setTitle("Prediction");

		// Aggiungo i bottoni alla RootPanel. 
		// RootPanel.get() ritorna l'elemento body.
		RootPanel.get("googleCloudSQL").add(cloudButton);
		RootPanel.get("weka").add(wekaButton);
		RootPanel.get("googlePredictionAPI").add(predictionButton);
		

		// Imposto il focus sul bottone cloud.
		cloudButton.setFocus(true);

		// Creazione di un handler per la pressione dei bottoni.
		class MyHandler implements ClickHandler {
			public void onClick(ClickEvent event) {
				Button b = (Button)event.getSource();
				if(b.getTitle().equals("Cloud"))
					Cloud();
				else if(b.getTitle().equals("Weka"))
					Weka();
				else
					Prediction();
			}
			
			// E' stato premuto il bottone per vedere il tutorial di "Google Cloud SQL"
			private void Cloud(){
				greetingService.dataCloud(new AsyncCallback<Vector<RecordQuestbook>>() {
					
					@Override
					public void onSuccess(Vector<RecordQuestbook> result) {
						int numFields = result.get(0).getNumFields();
						table.resize(result.size(), numFields);
						// Inserisco i dati nella tabella.
						for(int i=0;i<result.size();i++)
							for(int j=0;j<numFields;j++)
								table.setWidget(i, j, new Label(result.get(i).getCampo(j)));
						
						// Aggiungo nella parte sinistra, la tabella.
						hPanel.add(table);
						hPanel.add(new Label("Questa e' la parte destra."));
						RootPanel.get("content").add(hPanel);
					}
					
					@Override
					public void onFailure(Throwable caught) {
						// Fallimento.
						RootPanel.get("content").add(new Label("Fallimento"));
					}
				});
			}

			// E' stato premuto il bottone per vedere il tutorial di "Weka"
			private void Weka(){
				RootPanel.get("content").add(new Label("Weka"));
			}
			
			// E' stato premuto il bottone per vedere il tutorial di "Google Prediction API"
			private void Prediction(){
				RootPanel.get("content").add(new Label("Prediction"));
			}
		}
		
		// Add a handler to send the name to the server
		MyHandler handler = new MyHandler();
		cloudButton.addClickHandler(handler);
		wekaButton.addClickHandler(handler);
		predictionButton.addClickHandler(handler);
	}
	
}
