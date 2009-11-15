package mx.itesm.ddb.dao;

import mx.itesm.ddb.model.dictionary.Relation;

/**
 * Database Dictionary DAO.
 * 
 * @author jccastrejon
 * 
 */
public interface DatabaseDictionaryDao {

    /**
     * Get the relation identified by the given name.
     * 
     * @param name
     *            Relation name.
     * @return A Relation instance if there's a Relation identified by the given
     *         name in the database dictionary, <em>null</em> otherwise.
     */
    public Relation getRelation(final String name);
}
