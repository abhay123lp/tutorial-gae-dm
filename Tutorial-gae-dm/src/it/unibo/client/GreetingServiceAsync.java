package it.unibo.client;


import it.unibo.shared.RecordQuestbook;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GreetingServiceAsync {
	void dataCloud(AsyncCallback<Vector<RecordQuestbook>> callback);
	void datasetWeka(AsyncCallback<Vector<String>> callback);
	void serviceWeka(String nameFile,AsyncCallback<String> callback);
}
