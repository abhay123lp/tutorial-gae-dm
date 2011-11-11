package it.unibo.shared;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

/**
 * @author Fabio Magnani, Enrico Gramellini
 * PersistenceManager, utile per leggere/inserire dei dati nel datastore.
 */
public final class PMF {
   
	/**
	 * Istanza della PersistenceManager.
	 */
	private static final PersistenceManagerFactory pmfInstance =
        JDOHelper.getPersistenceManagerFactory("transactions-optional");

    private PMF() {}

    /**
     * @return PersistenceManager creata.
     */
    public static PersistenceManagerFactory get() {
        return pmfInstance;
    }
}
