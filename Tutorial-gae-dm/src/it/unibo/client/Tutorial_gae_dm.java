package it.unibo.client;


import it.unibo.shared.Attributo;
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
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabBar;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Classe entry point.
 * @author Fabio Magnani, Enrico Gramellini
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
	
	private static final Image image = new Image("loader.gif");
	
	// Metodo dell'entry point.
	public void onModuleLoad() {
		// Menu' tab per selezionare il tutorial desiderato.
		final TabBar widget = new TabBar();
		widget.addTab("Google Cloud SQL");
	    widget.addTab("Weka");
	    widget.addTab("Prediction API");
	    widget.addSelectionHandler(new SelectionHandler<Integer>() {
	        public void onSelection(SelectionEvent<Integer> event) {
	    	    // Aggiorno il tab selezionato
	        	Cookies.setCookie("cookieTab", String.valueOf(event.getSelectedItem().intValue()));
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
	    		if(widget.getSelectedTab()!=-1)
	    			if(event.getItem().intValue() != widget.getSelectedTab())
	    				// Se il tab selezionato e' diverso da quello corrente, chiedo conferma dell'operazione.
	    				if(!Window.confirm("You really want to leave '"
	    					+ widget.getTabHTML(widget.getSelectedTab())
	    					+ "' and go to '"
	    					+ widget.getTabHTML(event.getItem())
	    					+ "'?"))
	    					// Se l'utente ha fatto annulla, cancello l'evento. Cioè non vado nella SelectionHandler.
	    					event.cancel();
	        }
	    });
	    // Seleziono il primo Tab perche' cosi' inizializzo la pagina con il contenuto per il
	    // tutorial Google Cloud SQL.
	    String tab = Cookies.getCookie("cookieTab");
	    if(tab!=null){
	    	widget.selectTab(Integer.parseInt(tab));
	    }
	    else
	    	widget.selectTab(0);
	    RootPanel.get("tab").add(widget);
	}
	
	/**
	 * Gestione del tutorial riguardante Google Cloud SQL.
	 */
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

		// Pulisco il contenuto della pagina HTML.
		RootPanel.get("content").clear();
		
		RootPanel.get("content").add(image);
		
		// E' stato premuto il bottone per vedere il tutorial di "Google Cloud SQL".
		greetingService.dataCloud(new AsyncCallback<Vector<RecordQuestbook>>() {
			@Override
			public void onSuccess(Vector<RecordQuestbook> result) {
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
				vFormPanel.add(new Label("Nationality: "));
				final ListBox nationality = new ListBox();
				nationality.addItem("Italian");
				nationality.addItem("English");
				nationality.addItem("French");
				nationality.addItem("Spanish");
				vFormPanel.add(nationality);
				nationality.setName("nationality");
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
						if(content.getText().equals(""))
							Window.alert("Insert the field.");
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
				RootPanel.get("content").clear();
				RootPanel.get("content").add(hMainPanel);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// Fallimento.
				VerticalPanel dialogVPanel = new VerticalPanel();
				HTML serverResponseLabel = new HTML();
				serverResponseLabel.addStyleName("serverResponseLabelError");
				if(caught.getMessage().startsWith("Internal Error -"))
					serverResponseLabel.setHTML(caught.getMessage());
				else
					serverResponseLabel.setHTML(SERVER_ERROR);
				dialogVPanel.add(serverResponseLabel);
				showDialogBox("Remote Procedure Call - Failure",dialogVPanel,null);
			}
		});		
	}
	
	/**
	 * Gestione del tutorial riguardante l'uso di Weka con il datastore offerto da Google.
	 */
	private void Weka(){
		// Pannello principale.
		final VerticalPanel hMainPanel = new VerticalPanel();
		// Pannello principale dei controlli.
		final HorizontalPanel hMainControlPanel = new HorizontalPanel();
		// Pannello principale del dataset.
		final HorizontalPanel hMainDatasetPanel = new HorizontalPanel();
		final VerticalPanel vFirstPanel = new VerticalPanel();
		final VerticalPanel vSecondPanel = new VerticalPanel();
		final VerticalPanel vThirdPanel = new VerticalPanel();
		final VerticalPanel vFourthPanel = new VerticalPanel();
		// Pannello che contiene gli elementi della form.
		final VerticalPanel vFormPanel = new VerticalPanel();
		// Form upload file.
		final FormPanel formUpload = new FormPanel("");
		// ListBox contenente i file gia' caricati.
		final ListBox listDataset = new ListBox();
		// Serve per poter caricare un file.
		final FileUpload upload = new FileUpload();
		// Bottone per usare il dataset.
		final Button useDataset = new Button("Use Dataset");
		// Bottone per classificare una istanza.
		final Button predictInstance = new Button("Classify");
		// Bottoni per il caricamento dei dataset.
		final Button loadDataset = new Button("Load Dataset");
		//Pannello per l'inserimento della predizione
		final HorizontalPanel vPredictPanel = new HorizontalPanel();
		//Pannello per l'inserimento della predizione
		final VerticalPanel vPredictNamePanel = new VerticalPanel();
		//Pannello per l'inserimento della predizione
		final VerticalPanel vPredictAttributePanel = new VerticalPanel();
		
		// Setto le proprieta' della form upload.
		formUpload.setEncoding(FormPanel.ENCODING_MULTIPART);
		formUpload.setMethod(FormPanel.METHOD_POST);
		formUpload.setAction("/fileUpload");
		
		// Pulisco il contenuto della pagina HTML.
		RootPanel.get("content").clear();
		RootPanel.get("content").add(image);
		
		final Button closeButton = new Button("Close");
		// Quando viene cliccato il bottone di chiusura del popup, riabilito gli altri bottoni.
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
				useDataset.setEnabled(true);
				loadDataset.setEnabled(true);
				predictInstance.setEnabled(true);
			}
		});
		
		predictInstance.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String instance = "";
				boolean error = false;
				// Mi creo l'istanza settata dall'utente, inserendo tutti gli attributi 
				// separati da una ",".
				for(int i=0;i<vPredictAttributePanel.getWidgetCount();i++){
					// Scorro tutti i widget del pannello, se è un ListBox o un TextBox,
					// Significa che quel widget e' un attributo.
					if(vPredictAttributePanel.getWidget(i) instanceof ListBox){
						ListBox tempListBox = ((ListBox)vPredictAttributePanel.getWidget(i));
						instance = instance.concat(tempListBox.getItemText(tempListBox.getSelectedIndex()) + ",");
					}
					if(vPredictAttributePanel.getWidget(i) instanceof TextBox){
						TextBox tempTextBox = ((TextBox)vPredictAttributePanel.getWidget(i));
						try {
							double value = Double.valueOf(tempTextBox.getText());
							instance = instance.concat(String.valueOf(value) + ",");
						}catch(NumberFormatException e){
							error = true;
							break;
						}
					}
				}
				if(error)
					Window.alert("Insert all fields correctly");
				else{
					// Elimino l'ultima virgola
					instance = instance.substring(0, instance.length()-1);
					// Disabilito i pulsanti.
					useDataset.setEnabled(false);
					loadDataset.setEnabled(false);
					predictInstance.setEnabled(false);
					greetingService.classifyMessage(instance, new AsyncCallback<String>() {
						@Override
						public void onSuccess(String result) {
							// Successo.
							VerticalPanel dialogVPanel = new VerticalPanel();
							HTML serverResponseLabel = new HTML();
							dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
							serverResponseLabel.setHTML(result);
							dialogVPanel.add(serverResponseLabel);
							showDialogBox("Remote Procedure Call",dialogVPanel,closeButton);
	
						}
						@Override
						public void onFailure(Throwable caught) {
							// Fallimento.
							VerticalPanel dialogVPanel = new VerticalPanel();
							HTML serverResponseLabel = new HTML();
							dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
							serverResponseLabel.addStyleName("serverResponseLabelError");
							if(caught.getMessage().startsWith("Internal Error -"))
								serverResponseLabel.setHTML(caught.getMessage());
							else
								serverResponseLabel.setHTML(SERVER_ERROR);
							dialogVPanel.add(serverResponseLabel);
							showDialogBox("Remote Procedure Call - Failure",dialogVPanel,closeButton);
						}
					});
				}
			}
		});
		
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
		vFirstPanel.add(new HTML("<br><b>Load DataSet:</b>"));
		vFormPanel.add(upload);
		vFormPanel.add(loadDataset);
		formUpload.add(vFormPanel);
		vFirstPanel.add(formUpload);
		
		vSecondPanel.add(new HTML("<br><b>Use DataSet:</b>"));
		// Richiedo al sever di leggere dal datastore tutti i file gia' caricati.
		greetingService.datasetWeka(new AsyncCallback<Vector<String>>() {
			@Override
			public void onSuccess(Vector<String> result) {
				listDataset.addItem("File Upload");
				if(result.size()>0){
					// Inserisco i dati nella tabella.
					for(int i=0;i<result.size();i++)
						listDataset.addItem(result.get(i));
				}
				// Aggiungo nel secondo panello, la tabella.
				vSecondPanel.add(listDataset);
				vSecondPanel.add(useDataset);
				RootPanel.get("content").clear();
				RootPanel.get("content").add(hMainPanel);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// Fallimento.
				VerticalPanel dialogVPanel = new VerticalPanel();
				HTML serverResponseLabel = new HTML();
				dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
				serverResponseLabel.addStyleName("serverResponseLabelError");
				serverResponseLabel.setHTML(SERVER_ERROR);
				dialogVPanel.add(serverResponseLabel);
				showDialogBox("Remote Procedure Call - Failure",dialogVPanel,null);
			}
		});
		// Aggiungo un handler che parte quando viene cliccato il bottone.
		// Ricordo che addClickListener e' deprecato, quindi non l'ho usato.
		useDataset.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String nameFile = listDataset.getItemText(listDataset.getSelectedIndex());
				if(listDataset.getSelectedIndex()==0)
					// Non c'e' neanche un RadioButton selezionato.
					Window.alert("Select one file!");
				else{
					hMainDatasetPanel.clear();
					hMainDatasetPanel.add(image);
					useDataset.setEnabled(false);
					loadDataset.setEnabled(false);
					predictInstance.setEnabled(false);
					// C'e' almeno un file selezionato.
					greetingService.serviceWeka(nameFile, new AsyncCallback<String>() {
						@Override
						public void onSuccess(String result) {
							if (result.contains("\n"))
								result = result.replaceAll("\n", "<br>");
							vThirdPanel.clear();
							// Successo. Imposto il terzo pannello con l'albero del dataset.
							HTML serverResponseLabel = new HTML();
							vThirdPanel.add(new HTML("<br><b>Tree classifier:</b>"));
							serverResponseLabel.setHTML(result);
							vThirdPanel.add(serverResponseLabel);
							// Imposto il quarto pannello in modo tale da fare la predizione.
							greetingService.attributesDataset(new AsyncCallback<Vector<Attributo>>() {
								@Override
								public void onSuccess(Vector<Attributo> result) {
									vFourthPanel.clear();
									vPredictPanel.clear();
									vPredictNamePanel.clear();
									vPredictAttributePanel.clear();
									Attributo temp;
									// Scorro tutti gli attributi.
									for(int i=0;i<result.size();i++){
										temp = result.get(i);
										vPredictNamePanel.add(new Label(temp.getName()));
										if(temp.isNominal()){
											// Se l'attributo e' Nominal allora creo una ListBox.		
											ListBox widget = new ListBox();
											Vector<String> values = temp.getValues();
											for(int j=0;j<values.size();j++)
												widget.addItem(values.get(j));
											vPredictAttributePanel.add(widget);
										}
										if(temp.isNumeric()){
											// Se l'attributo e' Numeric allora creo una TextBox.
											String range = "";
											range = "min: " + temp.getMinNumericBound();
											range = range.concat(" and max: " + temp.getMaxNumericBound());
											TextBox widget = new TextBox();
											widget.setText(range);
											vPredictAttributePanel.add(widget);
										}										
									}
									vPredictAttributePanel.add(predictInstance);
									vPredictPanel.add(vPredictNamePanel);
									vPredictPanel.add(vPredictAttributePanel);
									vFourthPanel.add(new HTML("<br><b>Predict sentence:</b>"));
									vFourthPanel.add(vPredictPanel);
									hMainDatasetPanel.clear();
									hMainDatasetPanel.add(vThirdPanel);
									hMainDatasetPanel.add(vFourthPanel);
									hMainPanel.add(hMainDatasetPanel);
									// Riabilito i pulsanti.
									useDataset.setEnabled(true);
									loadDataset.setEnabled(true);
									predictInstance.setEnabled(true);
								}
								@Override
								public void onFailure(Throwable caught) {
									// Fallimento.
									VerticalPanel dialogVPanel = new VerticalPanel();
									HTML serverResponseLabel = new HTML();
									dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
									serverResponseLabel.addStyleName("serverResponseLabelError");
									if(caught.getMessage().startsWith("Internal Error -"))
										serverResponseLabel.setHTML(caught.getMessage());
									else
										serverResponseLabel.setHTML(SERVER_ERROR);
									dialogVPanel.add(serverResponseLabel);
									showDialogBox("Remote Procedure Call - Failure",dialogVPanel,closeButton);
								}
							});
						}
						@Override
						public void onFailure(Throwable caught) {
							// Fallimento.
							VerticalPanel dialogVPanel = new VerticalPanel();
							HTML serverResponseLabel = new HTML();
							dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
							serverResponseLabel.addStyleName("serverResponseLabelError");
							if(caught.getMessage().startsWith("Internal Error -"))
								serverResponseLabel.setHTML(caught.getMessage());
							else
								serverResponseLabel.setHTML(SERVER_ERROR);
							dialogVPanel.add(serverResponseLabel);
							showDialogBox("Remote Procedure Call - Failure",dialogVPanel,closeButton);
						}
					});
				}
			}
		});
		
		// Aggiungo la varie parti al pannello principale.
		hMainControlPanel.add(vFirstPanel);
		hMainControlPanel.add(vSecondPanel);
		hMainPanel.add(hMainControlPanel);
	}
	
	/**
	 * Gestione del tutorial riguardante le Prediction API offerte da Google.
	 */
	private void Prediction(){
		// Pulisco il contenuto della pagina HTML.
		RootPanel.get("content").clear();
		RootPanel.get("content").add(image);
		greetingService.authorization(new AsyncCallback<Integer>() {
			@Override
			public void onSuccess(Integer result) {
				if(result==0)
					authorization();
				else if(result==1)
					predictionAPI();
			}
			@Override
			public void onFailure(Throwable caught) {
				// Fallimento.
				VerticalPanel dialogVPanel = new VerticalPanel();
				HTML serverResponseLabel = new HTML();
				dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
				serverResponseLabel.addStyleName("serverResponseLabelError");
				serverResponseLabel.setHTML(SERVER_ERROR);
				dialogVPanel.add(serverResponseLabel);
				showDialogBox("Remote Procedure Call - Failure",dialogVPanel,null);
			}
		});
		
	}
	
	/**
	 * Richiesta di autorizzazione per l'uso delle Prediction API.
	 */
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
		
		RootPanel.get("content").clear();
		RootPanel.get("content").add(formPredict);
	}
	
   /**
	 * Uso delle Prediction API.
	 */
	private void predictionAPI() {
		// Pannello principale.
		final VerticalPanel vMainPanel = new VerticalPanel();
		// Pannello dei controlli.
		final HorizontalPanel hControlPanel = new HorizontalPanel();
		// Pannello principale relativo al train.
		final VerticalPanel vTrainMainPanel = new VerticalPanel();
		// Pannello relativo al train.
		final HorizontalPanel hTrainPanel = new HorizontalPanel();
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
		// Inserisco nel pannello dei controlli i vari elementi.
		hControlPanel.add(queryField);
		hControlPanel.add(sendQueryButton);
		hControlPanel.add(sendTrainButton);
		vMainPanel.add(hControlPanel);
		vMainPanel.add(vTrainMainPanel);
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

		RootPanel.get("content").clear();
		// Inserisco nella RootPanel il panello principale. 
		RootPanel.get("content").add(vMainPanel);

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
				if(textToServer.equals(""))
					Window.alert("Insert the query");
				else{
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
									if(caught.getMessage().startsWith("Internal Error -"))
										serverResponseLabel.setHTML(caught.getMessage());
									else
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
								if(caught.getMessage().startsWith("Internal Error -"))
									serverResponseLabel.setHTML(caught.getMessage());
								else
									serverResponseLabel.setHTML(SERVER_ERROR);								
								dialogVPanel.add(serverResponseLabel);
								showDialogBox("Remote Procedure Call - Failure",dialogVPanel,closeButton);
							}

							public void onSuccess(String result) {
								// Successo.
								VerticalPanel vSxPanel = new VerticalPanel();
								VerticalPanel vDxPanel = new VerticalPanel();
								if (result.contains("classificationAccuracy")){
									vSxPanel.add(new Label("Classification Accuracy"));
									int min = result.indexOf(":");
									int max = result.indexOf(",");
									vDxPanel.add(new Label(result.substring(min+1, max)));
									result = result.substring(max+1);
								}
								if (result.contains("confusionMatrix")){
									vSxPanel.add(new Label("Confusion Matrix"));
									int min = result.indexOf(":");
									int max = result.indexOf("}}");
									String confusionMatrix = result.substring(min+2, max+1);
									vDxPanel.add(new Label(confusionMatrix));
									result = result.substring(max+3);
								}
								if (result.contains("confusionMatrixRowTotals")){
									vSxPanel.add(new Label("Confusion Matrix Row Totals"));
									int min = result.indexOf(":");
									int max = result.indexOf("}");
									vDxPanel.add(new Label(result.substring(min+2, max)));
									result = result.substring(max+2);
								}
								if (result.contains("modelType")){
									vSxPanel.add(new Label("Model Type"));
									int min = result.indexOf(":");
									int max = result.indexOf(",");
									vDxPanel.add(new Label(result.substring(min+1, max)));
									result = result.substring(max+1);
								}
								if (result.contains("numberInstances")){
									vSxPanel.add(new Label("Number Instances"));
									int min = result.indexOf(":");
									int max = result.indexOf(",");
									vDxPanel.add(new Label(result.substring(min+1, max)));
									result = result.substring(max+1);
								}
								if (result.contains("numberLabels")){
									vSxPanel.add(new Label("Number Labels"));
									int min = result.indexOf(":");
									int max = result.indexOf("}");
									vDxPanel.add(new Label(result.substring(min+1, max)));
								}
								vTrainMainPanel.clear();
								hTrainPanel.clear();
								// Successo. Imposto il terzo pannello con l'albero del dataset.
								hTrainPanel.add(vSxPanel);
								hTrainPanel.add(vDxPanel);
								vTrainMainPanel.add(new HTML("<br><b>Training Model Info:</b>"));
								vTrainMainPanel.add(hTrainPanel);
								// Riabilito i pulsanti.
								sendQueryButton.setEnabled(true);
								sendTrainButton.setEnabled(true);
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
