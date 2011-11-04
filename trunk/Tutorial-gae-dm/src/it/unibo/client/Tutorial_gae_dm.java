package it.unibo.client;

import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JTable;

import org.apache.jasper.tagplugins.jstl.core.Redirect;

import it.unibo.shared.FieldVerifier;

import com.gargoylesoftware.htmlunit.javascript.host.Element;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sun.java.swing.plaf.windows.resources.windows;

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
		final Button cloudButton = new Button("Google Cloud SQL");
		final Button wekaButton = new Button("Weka");
		final Button predictionButton = new Button("Google Prediction API");
		final Label output = new Label("");
		final Label output1 = new Label("");
		final VerticalPanel vPanel = new VerticalPanel();
		final Grid table = new Grid();
		
		// We can add style names to widgets
		cloudButton.addStyleName("button");
		wekaButton.addStyleName("button");
		predictionButton.addStyleName("button");
		cloudButton.setTitle("Cloud");
		wekaButton.setTitle("Weka");
		predictionButton.setTitle("Prediction");

		// Add the nameField and sendButton to the RootPanel
		// Use RootPanel.get() to get the entire body element
		RootPanel.get("googleCloudSQL").add(cloudButton);
		RootPanel.get("weka").add(wekaButton);
		RootPanel.get("googlePredictionAPI").add(predictionButton);
		

		// Focus the cursor on the cloudButton when the app loads
		cloudButton.setFocus(true);

		// Create a handler for the sendButton and nameField
		class MyHandler implements ClickHandler {
			public void onClick(ClickEvent event) {
				Button b = (Button)event.getSource();
				output.setText("Nome: " + b.getTitle());				
				RootPanel.get("content").add(output);
			}
		}
		
		// Add a handler to send the name to the server
		MyHandler handler = new MyHandler();
		cloudButton.addClickHandler(handler);
		wekaButton.addClickHandler(handler);
		predictionButton.addClickHandler(handler);
	}
	
}
