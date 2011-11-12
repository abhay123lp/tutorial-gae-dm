package it.unibo.client;


import it.unibo.shared.RecordQuestbook;

import java.util.Vector;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabBar;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Tutorial_gae_dm implements EntryPoint {
	// Messaggio di errore visualizzato quando il server non e' raggiungibile.
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	// Creazione di un remote service proxy per chiamare il server.
	private final GreetingServiceAsync greetingService = GWT
			.create(GreetingService.class);

	// Per i messaggio di errore o per quelli ricevuti dal server faccio vedere la DialogBox.
	private final DialogBox dialogBox = new DialogBox();
	
	// Metodo dell'entry point.
	public void onModuleLoad() {
		// Menu' tab per selezionare il tutorial desiderato.
		final TabBar widget = new TabBar();
		
		widget.addTab("Google Cloud SQL");
	    widget.addTab("Weka");
	    widget.addTab("Prediction API");
	    widget.selectTab(0);
	    widget.addSelectionHandler(new SelectionHandler<Integer>() {
	        public void onSelection(SelectionEvent<Integer> event) {
	            // A seconda di cosa e' stato selezionato, visualizzo il tutorial.
	        	if(event.getSelectedItem().intValue() == 0)
	        		Cloud();
	        	else if(event.getSelectedItem().intValue() == 1)
					Weka();
	        	else
	        		Prediction();
	        }
        });
	    
	    widget.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
	    	public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
	    		if(event.getItem().intValue() != widget.getSelectedTab())
	    			// Se il tab selezionato e' diverso da quello corrente, chiedo conferma dell'operazione.
	    			if(!Window.confirm("You really want to leave '"
	    			    + widget.getTabHTML(widget.getSelectedTab())
	    			    + "' and go to '"
	    			    + widget.getTabHTML(event.getItem())
	    			    + "'?"))
	    				// Se l'utente ha fatto annulla, cancello l'evento. Cio� non vado nella SelectionHandler.
	    				event.cancel();
	        }
	    });
	    // Riseleziono il primo Tab perche' cosi' inizializzo la pagina con il contenuto per il
	    // tutorial Google Cloud SQL.
	    widget.selectTab(0);
	    RootPanel.get("tab").add(widget);
	}
	
	private void Cloud(){
		// Pannello principale.
		final HorizontalPanel hMainPanel = new HorizontalPanel();
		// Parte sinistra del pannello principale.
		final VerticalPanel vSxPanel = new VerticalPanel();
		// Tabella contenente i dati della guestbook.
		final Grid table = new Grid();
		// Pannello che contiene gli elementi della form.
		final VerticalPanel vFormPanel = new VerticalPanel();
		// Form per la guestbook.
		final FormPanel formGuest = new FormPanel("");
		
		// Setto le proprieta' della form.
		formGuest.setEncoding(FormPanel.ENCODING_URLENCODED);
		formGuest.setMethod(FormPanel.METHOD_POST);
		formGuest.setAction("/sendGuestbook");

		// E' stato premuto il bottone per vedere il tutorial di "Google Cloud SQL".
		greetingService.dataCloud(new AsyncCallback<Vector<RecordQuestbook>>() {
			@Override
			public void onSuccess(Vector<RecordQuestbook> result) {
				// Pulisco il contenuto della pagina HTML.
				RootPanel.get("content").clear();
				if(result.size()>0)
				{
					int numFields = result.get(0).getNumFields();
					table.resize(result.size(), numFields);
					// Inserisco i dati nella tabella.
					for(int i=0;i<result.size();i++)
						for(int j=0;j<numFields;j++)
							table.setWidget(i, j, new Label(result.get(i).getCampo(j)));
					// Aggiungo nella parte sinistra, la tabella.
					vSxPanel.add(table);
				}
				else
					vSxPanel.add(new Label("No message!!"));
				// Costruzione della form per l'invio dei messaggio sul guestbook.
				vFormPanel.add(new Label("First Name: "));
				final TextBox guestName = new TextBox();
				guestName.setName("guestName");
				vFormPanel.add(guestName);
				vFormPanel.add(new Label("Message: "));
				final TextArea content = new TextArea();
				content.setName("content");
				vFormPanel.add(content);
				Button sendGuest = new Button("Send");
				// Aggiungo un handler che parte quando viene cliccato il bottone.
				// Ricordo che addClickListener e' deprecato, quindi non l'ho usato.
				sendGuest.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						if(content.getText().equals("") || guestName.getText().equals(""))
							Window.alert("Insert the fields.");
						else
							formGuest.submit();
					}
				});
				// Aggiungo gli elementi.
				vFormPanel.add(sendGuest);
				formGuest.add(vFormPanel);
				hMainPanel.add(vSxPanel);
				// Aggiungo nella parte destra una form.
				hMainPanel.add(formGuest);
				RootPanel.get("content").add(hMainPanel);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// Fallimento.
				VerticalPanel dialogVPanel = new VerticalPanel();
				HTML serverResponseLabel = new HTML();
				serverResponseLabel.addStyleName("serverResponseLabelError");
				serverResponseLabel.setHTML(SERVER_ERROR);
				dialogVPanel.add(serverResponseLabel);
				showDialogBox("Remote Procedure Call - Failure",dialogVPanel,null);
			}
		});		
	}
	
	private void Weka(){
		// Pannello principale.
		final HorizontalPanel hMainPanel = new HorizontalPanel();
		final VerticalPanel vFirstPanel = new VerticalPanel();
		final VerticalPanel vSecondPanel = new VerticalPanel();
		final HorizontalPanel vThirdPanel = new HorizontalPanel();
		// Pannello che contiene gli elementi della form.
		final VerticalPanel vFormPanel = new VerticalPanel();
		// Form upload file.
		final FormPanel formUpload = new FormPanel("");
		// Pannello contenente gli elementi della form di upload dei file.
		final VerticalPanel vFilePanel = new VerticalPanel();
		// Serve per poter caricare un file.
		final FileUpload upload = new FileUpload();
		// Bottone per usare il dataset.
		final Button useDataset = new Button("Use Dataset");
		// TextBox dove e' possibile inserire l'istanza da classificare.
		final TextBox instanceField = new TextBox();
		instanceField.setText("instance");
		// Bottone per classificare una istanza.
		final Button predictInstance = new Button("Classify");		
		
		// Setto le proprieta' della form upload.
		formUpload.setEncoding(FormPanel.ENCODING_MULTIPART);
		formUpload.setMethod(FormPanel.METHOD_POST);
		formUpload.setAction("/fileUpload");
		
		// Pulisco il contenuto della pagina HTML.
		RootPanel.get("content").clear();
		
		// Setto nel terzo pannello gli elementi per predire un'istanza.
		vThirdPanel.add(instanceField);
		vThirdPanel.add(predictInstance);
		
		predictInstance.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String instance = instanceField.getText();
				greetingService.classifyMessage(instance, new AsyncCallback<String>() {
					@Override
					public void onSuccess(String result) {
						Window.alert(result);
					}
					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}
				});
			}
		});
		
		Button loadDataset = new Button("Load Dataset");
		// Aggiungo un handler che parte quando viene cliccato il bottone.
		// Ricordo che addClickListener e' deprecato, quindi non l'ho usato.
		loadDataset.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String fileName = null;
				fileName = upload.getFilename();
				if(fileName != null && fileName.endsWith(".arff"))
					formUpload.submit();
				else
					Window.alert("File is not correct");
			}
		});
		// Sistemo la prima parte del pannello principale.
		vFirstPanel.add(new Label("Load DataSet"));
		vFormPanel.add(upload);
		vFormPanel.add(loadDataset);
		formUpload.add(vFormPanel);
		vFirstPanel.add(formUpload);

		
		vSecondPanel.add(new Label("Use Dataset"));
		// Richiedo al sever di leggere dal datastore tutti i file gia' caricati.
		greetingService.datasetWeka(new AsyncCallback<Vector<String>>() {
			
			@Override
			public void onSuccess(Vector<String> result) {
				if(result.size()>0){
					// Inserisco i dati nella tabella.
					for(int i=0;i<result.size();i++) {
						vFilePanel.add(new RadioButton("group", result.get(i)));
					}
					// Aggiungo nel secondo panello, la tabella.
					vSecondPanel.add(vFilePanel);
				}
				else
					vSecondPanel.add(new Label("No file upload!!"));
				vSecondPanel.add(useDataset);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// Fallimento.
				// Pulisco il contenuto della pagina HTML.
				RootPanel.get("content").clear();
				Window.alert(SERVER_ERROR);
			}
		});
		// Aggiungo un handler che parte quando viene cliccato il bottone.
		// Ricordo che addClickListener e' deprecato, quindi non l'ho usato.
		useDataset.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				RadioButton temp = null;
				boolean select = false;
				String nameFile = "";
				// Cerco se c'e' almeno un RadioButton selezionato.
				for(int i=0;i<vFilePanel.getWidgetCount();i++) {
					temp = (RadioButton)(vFilePanel.getWidget(i));
					if(temp.getValue()) {
						// RadioButton selezionato.
						nameFile = temp.getText();
						select=true;
						break;
					}
				}
				
				if(!select)
					// Non c'e' neanche un RadioButton selezionato.
					Window.alert("Select at least one file!");
				else
					// C'e' almeno un RadioButton selezionato.
					greetingService.serviceWeka(nameFile, new AsyncCallback<String>() {
						@Override
						public void onSuccess(String result) {
							if (result.contains("\n"))
								result = result.replaceAll("\n", "<br>");
							Window.alert(result);
//							RootPanel.get("content").clear();
//							RootPanel.get("content").add(new HTML(result));
						}
						@Override
						public void onFailure(Throwable caught) {
							// Fallimento.
							// Pulisco il contenuto della pagina HTML.
							RootPanel.get("content").clear();
							Window.alert(SERVER_ERROR);
						}
					});
			}
		});
		
		// Aggiungo la varie parti al pannello principale.
		hMainPanel.add(vFirstPanel);
		hMainPanel.add(vSecondPanel);
		hMainPanel.add(vThirdPanel);
		RootPanel.get("content").add(hMainPanel);
	}
	
	private void Prediction(){
		// Pulisco il contenuto della pagina HTML.
		RootPanel.get("content").clear();
		greetingService.authorization(new AsyncCallback<Integer>() {
			@Override
			public void onSuccess(Integer result) {
				if(result==0)
					authorization();
				else
					prediction();
			}
			@Override
			public void onFailure(Throwable caught) {
			}
		});
		
	}
	
	private void authorization(){
		final Button sendAuthorization = new Button("Authorization");
		// Form per le autorizzazioni per usare la api.		
		final FormPanel formPredict = new FormPanel("");
		// Setto le proprieta' della form.
		formPredict.setEncoding(FormPanel.ENCODING_URLENCODED);
		formPredict.setMethod(FormPanel.METHOD_GET);
		formPredict.setAction("/predict");
		// Quando viene premuto il buttone faccio il submit della form.
		sendAuthorization.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				formPredict.submit();
			}
		});
		formPredict.add(sendAuthorization);
		RootPanel.get("content").add(formPredict);
	}
	
	private void prediction() {
		// Pannello principale.
		final HorizontalPanel hMainPanel = new HorizontalPanel();
		// Bottone per l'invio delle query.
		final Button sendQueryButton = new Button("Send");
		sendQueryButton.setTitle("Send");
		// Bottone per impostare il train.
		final Button sendTrainButton = new Button("Train");
		sendTrainButton.setTitle("Train");
		// TextBox dove e' possibile inserire la query da inviare al server.
		final TextBox queryField = new TextBox();
		queryField.setText("query");
		// Imposto gli stili.
		sendQueryButton.addStyleName("sendButton");
		sendTrainButton.addStyleName("sendButton");
		// Inserisco nel pannello principale i vari elementi.
		hMainPanel.add(queryField);
		hMainPanel.add(sendQueryButton);
		hMainPanel.add(sendTrainButton);
		// Inserisco nella RootPanel il panello principale. 
		RootPanel.get("content").add(hMainPanel);
		// Faccio il focus
		queryField.setFocus(true);
		queryField.selectAll();
		// Creazione del popup per le risposte del server.
		final Button closeButton = new Button("Close");
		
		// Quando viene cliccato il bottone di chiusura del popup, riabilito gli altri bottoni.
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
				sendQueryButton.setEnabled(true);
				sendTrainButton.setEnabled(true);
				sendQueryButton.setFocus(true);
			}
		});

		// Handler che gestisce i bottoni di invio della query e del train.
		class MyHandler implements ClickHandler, KeyUpHandler {
			// Quando c'e' un evento onClick, guardo che pulsante e' stato premuto. 
			public void onClick(ClickEvent event) {
				Button b = (Button)event.getSource();
				if(b.getTitle().equals("Send"))
					sendQueryToServer();
				else if(b.getTitle().equals("Train"))
					sendTrainRequestToServer();
			}
			// Viene gestito anche la pressione del tasto enter sul TextBox della query.
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					// Quando premo invio nella casella di testo riservata alla query, la invio al server.
					sendQueryToServer();
				}
			}

			// Invio della query al server e aspetto la risposta.
			private void sendQueryToServer() {
				// First, we validate the input.
				String textToServer = queryField.getText();
				final Label textToServerLabel = new Label(textToServer);
				// Disabilito i bottoni.
				sendQueryButton.setEnabled(false);
				sendTrainButton.setEnabled(false);
				// Richiamo la funzione del server.
				greetingService.doPredict(textToServer,
						new AsyncCallback<String>() {
							public void onFailure(Throwable caught) {
								// Fallimento.
								VerticalPanel dialogVPanel = new VerticalPanel();
								HTML serverResponseLabel = new HTML();
								dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
								serverResponseLabel.addStyleName("serverResponseLabelError");
								serverResponseLabel.setHTML(SERVER_ERROR);
								dialogVPanel.add(serverResponseLabel);
								showDialogBox("Remote Procedure Call - Failure",dialogVPanel,closeButton);
							}

							public void onSuccess(String result) {
								// Successo.
								VerticalPanel dialogVPanel = new VerticalPanel();
								HTML serverResponseLabel = new HTML();
								dialogVPanel.add(new HTML("<b>Sending query to the server:</b>"));
								dialogVPanel.add(textToServerLabel);
								dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
								serverResponseLabel.setHTML(result);
								dialogVPanel.add(serverResponseLabel);
								showDialogBox("Remote Procedure Call",dialogVPanel,closeButton);
							}
						});
			}
			
			// Invio la richiesta di train al server e aspetto la risposta.
			private void sendTrainRequestToServer() {

				// Disabilito i bottoni.
				sendQueryButton.setEnabled(false);
				sendTrainButton.setEnabled(false);
				// Viene richiamata la funzione del server.
				greetingService.doTrain(
						new AsyncCallback<String>() {
							public void onFailure(Throwable caught) {
								// Fallimento.
								VerticalPanel dialogVPanel = new VerticalPanel();
								HTML serverResponseLabel = new HTML();
								dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
								serverResponseLabel.addStyleName("serverResponseLabelError");
								serverResponseLabel.setHTML(SERVER_ERROR);
								dialogVPanel.add(serverResponseLabel);
								showDialogBox("Remote Procedure Call - Failure",dialogVPanel,closeButton);
							}

							public void onSuccess(String result) {
								// Successo.
								VerticalPanel dialogVPanel = new VerticalPanel();
								HTML serverResponseLabel = new HTML();
								dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
								serverResponseLabel.setHTML(result);
								dialogVPanel.add(serverResponseLabel);
								showDialogBox("Remote Procedure Call",dialogVPanel,closeButton);
							}
						});
			}
		}

		// Aggiungo l'handler ai bottoni.
		MyHandler handler = new MyHandler();
		sendQueryButton.addClickHandler(handler);
		sendTrainButton.addClickHandler(handler);
		queryField.addKeyUpHandler(handler);
	}
	
	/**
	 * Visualizzazione di un DialogBox
	 * @param titleBox Titolo del DialogBox.
	 * @param dialogVPanel Contenuto del DialogBox.
	 * @param closeButton Bottoni di chiusura del DialogBox.
	 */
	private void showDialogBox(String titleBox, VerticalPanel dialogVPanel, Button closeButton){
		// Imposto i vari componenti del DialogBox.
		dialogBox.setText("Remote Procedure Call");
		dialogBox.setAnimationEnabled(true);
		dialogVPanel.addStyleName("dialogVPanel");
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		dialogBox.setWidget(dialogVPanel);
		
		final Button hideButton = new Button("Close");
		if(closeButton == null){
			// Quando viene cliccato il bottone di chiusura del popup, nascondo il DialogBox.
			hideButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					dialogBox.hide();
				}
			});
			dialogVPanel.add(hideButton);
			hideButton.setFocus(true);
		}
		else{
			dialogVPanel.add(closeButton);
			closeButton.setFocus(true);
		}
		// Cancello quello che c'era prima.
		dialogBox.clear();
		// Inserisco il nuovo contenuto.
		dialogBox.setWidget(dialogVPanel);
		// Visualizzo il DialogBox.
		dialogBox.center();
		
	}
}
