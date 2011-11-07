package it.unibo.client;


import it.unibo.shared.RecordQuestbook;

import java.util.Vector;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
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
	    widget.selectTab(0);
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
	    widget.selectTab(0);
	    RootPanel.get("tab").add(widget);
	}
	
	private void Cloud(){
		final HorizontalPanel hMainPanel = new HorizontalPanel();
		final VerticalPanel vSxPanel = new VerticalPanel();
		final Grid table = new Grid();
		final VerticalPanel vFormPanel = new VerticalPanel();
		final FormPanel formGuest = new FormPanel("sign");
		formGuest.setEncoding(FormPanel.ENCODING_URLENCODED);
		formGuest.setMethod(FormPanel.METHOD_POST);
		formGuest.setAction("/sendGuestbook"); 

		// E' stato premuto il bottone per vedere il tutorial di "Google Cloud SQL"
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
				}
				
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
				// addClickListener è deprecato.
				sendGuest.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						formGuest.submit();
					}
				});
				vFormPanel.add(sendGuest);
				formGuest.add(vFormPanel);
										
				// Aggiungo nella parte sinistra, la tabella.
				vSxPanel.add(table);
				vSxPanel.add(new Label("No more message!!"));
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
				
				RootPanel.get("content").add(new Label("Fallimento"));
			}
		});		
	}
	
	private void Weka(){
		// Pulisco il contenuto della pagina HTML.
		RootPanel.get("content").clear();
		RootPanel.get("content").add(new Label("Weka"));
	}
	
	private void Prediction(){
		// Pulisco il contenuto della pagina HTML.
		RootPanel.get("content").clear();
		RootPanel.get("content").add(new Label("Prediction"));
	}

}
