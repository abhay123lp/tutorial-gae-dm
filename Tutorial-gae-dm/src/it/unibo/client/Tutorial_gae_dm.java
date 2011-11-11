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
import com.google.gwt.user.client.ui.Hidden;
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
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";
	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final GreetingServiceAsync greetingService = GWT
			.create(GreetingService.class);

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		final TabBar widget = new TabBar();
		
		widget.addTab("Google Cloud SQL");
	    widget.addTab("Weka");
	    widget.addTab("Prediction API");
	    widget.selectTab(2);
	    widget.addSelectionHandler(new SelectionHandler<Integer>() {
	        public void onSelection(SelectionEvent<Integer> event) {
	            // Let the user know what they just did.
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
	    			if(!Window.confirm("You really want to leave '"
	    			    + widget.getTabHTML(widget.getSelectedTab())
	    			    + "' and go to '"
	    			    + widget.getTabHTML(event.getItem())
	    			    + "'?"))
	    				// Se l'utente ha fatto annulla, cancello l'evento. Cioè non vado nella SelectionHandler.
	    				event.cancel();
	        }
	    });
	    // Riseleziono il primo Tab perche' cosi' inizializzo la pagina con il contenuto per il
	    // tutorial Google Cloud SQL.
	    widget.selectTab(2);
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
		// Form.
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
				TextBox guestName = new TextBox();
				guestName.setName("guestName");
				vFormPanel.add(guestName);
				vFormPanel.add(new Label("Message: "));
				TextArea content = new TextArea();
				content.setName("content");
				vFormPanel.add(content);
				Button sendGuest = new Button("Send");
				// Aggiungo un handler che parte quando viene cliccato il bottone.
				// Ricordo che addClickListener e' deprecato, quindi non l'ho usato.
				sendGuest.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
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
				// Pulisco il contenuto della pagina HTML.
				RootPanel.get("content").clear();
				Window.alert(SERVER_ERROR);
			}
		});		
	}
	
	private void Weka(){
		// Pannello principale.
		final HorizontalPanel hMainPanel = new HorizontalPanel();
		final VerticalPanel vSxPanel = new VerticalPanel();
		final VerticalPanel vDxPanel = new VerticalPanel();
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
		
		// Setto le proprieta' della form upload.
		formUpload.setEncoding(FormPanel.ENCODING_MULTIPART);
		formUpload.setMethod(FormPanel.METHOD_POST);
		formUpload.setAction("/fileUpload");
		
		// Pulisco il contenuto della pagina HTML.
		RootPanel.get("content").clear();
		
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
		// Sistemo la parte sinistra del pannello principale.
		vSxPanel.add(new Label("Load DataSet"));
		vFormPanel.add(upload);
		vFormPanel.add(loadDataset);
		formUpload.add(vFormPanel);
		vSxPanel.add(formUpload);

		
		vDxPanel.add(new Label("Use Dataset"));
		// Richiedo al sever di leggere dal datastore tutti i file gia' caricati.
		greetingService.datasetWeka(new AsyncCallback<Vector<String>>() {
			
			@Override
			public void onSuccess(Vector<String> result) {
				if(result.size()>0){
					// Inserisco i dati nella tabella.
					for(int i=0;i<result.size();i++) {
						vFilePanel.add(new RadioButton("group", result.get(i)));
					}
					// Aggiungo nella parte destra, la tabella.
					vDxPanel.add(vFilePanel);
				}
				else
					vDxPanel.add(new Label("No file upload!!"));
				vDxPanel.add(useDataset);
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
							RootPanel.get("content").clear();
							if (result.contains("\n"))
								result = result.replaceAll("\n", "<br>");
							RootPanel.get("content").add(new HTML(result));
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
		
		// Aggiungo il pannello di sinistra e quello di destra a quello principale.
		hMainPanel.add(vSxPanel);
		hMainPanel.add(vDxPanel);
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
				RootPanel.get("content").clear();
				Window.alert(SERVER_ERROR);
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
		
		// Add a handler to close the DialogBox
		sendAuthorization.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				formPredict.submit();
			}
		});
		formPredict.add(sendAuthorization);
		RootPanel.get("content").add(formPredict);
	}
	
	private void prediction(){
		final HorizontalPanel hMainPanel = new HorizontalPanel();
		final Button sendQueryButton = new Button("Send");
		sendQueryButton.setTitle("Send");
		final Button sendTrainButton = new Button("Train");
		sendTrainButton.setTitle("Train");
		final TextBox queryField = new TextBox();
		queryField.setText("query");
		// We can add style names to widgets
		sendQueryButton.addStyleName("sendButton");
		sendTrainButton.addStyleName("sendButton");

		// Add the nameField and sendButton to the RootPanel
		// Use RootPanel.get() to get the entire body element
		hMainPanel.add(queryField);
		hMainPanel.add(sendQueryButton);
		hMainPanel.add(sendTrainButton);
		RootPanel.get("content").add(hMainPanel);

		// Focus the cursor on the name field when the app loads
		queryField.setFocus(true);
		queryField.selectAll();

		// Create the popup dialog box
		final DialogBox dialogBox = new DialogBox();
		dialogBox.setText("Remote Procedure Call");
		dialogBox.setAnimationEnabled(true);
		final Button closeButton = new Button("Close");
		// We can set the id of a widget by accessing its Element
		closeButton.getElement().setId("closeButton");
		final Label textToServerLabel = new Label();
		final HTML serverResponseLabel = new HTML();
		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName("dialogVPanel");
		dialogVPanel.add(new HTML("<b>Sending query to the server:</b>"));
		dialogVPanel.add(textToServerLabel);
		dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
		dialogVPanel.add(serverResponseLabel);
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		dialogVPanel.add(closeButton);
		dialogBox.setWidget(dialogVPanel);

		// Add a handler to close the DialogBox
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
				sendQueryButton.setEnabled(true);
				sendTrainButton.setEnabled(true);
				sendQueryButton.setFocus(true);
			}
		});

		// Create a handler for the sendButton and nameField
		class MyHandler implements ClickHandler, KeyUpHandler {
			/**
			 * Fired when the user clicks on the sendButton.
			 */
			public void onClick(ClickEvent event) {
				Button b = (Button)event.getSource();
				if(b.getTitle().equals("Send"))
					sendQueryToServer();
				else if(b.getTitle().equals("Train"))
					sendTrainRequestToServer();
			}

			/**
			 * Fired when the user types in the nameField.
			 */
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					sendQueryToServer();
				}
			}

			/**
			 * Send query to the server and wait for a response.
			 */
			private void sendQueryToServer() {
				// First, we validate the input.
				String textToServer = queryField.getText();

				// Then, we send the input to the server.
				sendQueryButton.setEnabled(false);
				sendTrainButton.setEnabled(false);
				textToServerLabel.setText(textToServer);
				serverResponseLabel.setText("");
				greetingService.doPredict(textToServer,
						new AsyncCallback<String>() {
							public void onFailure(Throwable caught) {
								// Show the RPC error message to the user
								dialogBox
										.setText("Remote Procedure Call - Failure");
								serverResponseLabel
										.addStyleName("serverResponseLabelError");
								serverResponseLabel.setHTML(SERVER_ERROR);
								dialogBox.center();
								closeButton.setFocus(true);
							}

							public void onSuccess(String result) {
								dialogBox.setText("Remote Procedure Call");
								serverResponseLabel
										.removeStyleName("serverResponseLabelError");
								serverResponseLabel.setHTML(result);
								dialogBox.center();
								closeButton.setFocus(true);
							}
						});
			}
			
			/**
			 * Send train request to the server and wait for a response.
			 */
			private void sendTrainRequestToServer() {
				// First, we validate the input.
				String textToServer = queryField.getText();

				// Then, we send the input to the server.
				sendQueryButton.setEnabled(false);
				sendTrainButton.setEnabled(false);
				textToServerLabel.setText(textToServer);
				serverResponseLabel.setText("");
				greetingService.doTrain(
						new AsyncCallback<String>() {
							public void onFailure(Throwable caught) {
								// Show the RPC error message to the user
								dialogBox
										.setText("Remote Procedure Call - Failure");
								serverResponseLabel
										.addStyleName("serverResponseLabelError");
								serverResponseLabel.setHTML(SERVER_ERROR);
								dialogBox.center();
								closeButton.setFocus(true);
							}

							public void onSuccess(String result) {
								dialogBox.setText("Remote Procedure Call");
								serverResponseLabel
										.removeStyleName("serverResponseLabelError");
								serverResponseLabel.setHTML(result);
								dialogBox.center();
								closeButton.setFocus(true);
							}
						});
			}
		}

		// Add a handler to send the name to the server
		MyHandler handler = new MyHandler();
		sendQueryButton.addClickHandler(handler);
		sendTrainButton.addClickHandler(handler);
		queryField.addKeyUpHandler(handler);
	}
}
