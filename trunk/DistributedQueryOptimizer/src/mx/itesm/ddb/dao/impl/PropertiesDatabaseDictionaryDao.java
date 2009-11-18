package mx.itesm.ddb.dao.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import mx.itesm.ddb.dao.DatabaseDictionaryDao;
import mx.itesm.ddb.model.dictionary.Attribute;
import mx.itesm.ddb.model.dictionary.AttributeDomain;
import mx.itesm.ddb.model.dictionary.HorizontalFragment;
import mx.itesm.ddb.model.dictionary.Predicate;
import mx.itesm.ddb.model.dictionary.PredicateOperator;
import mx.itesm.ddb.model.dictionary.Relation;
import mx.itesm.ddb.model.dictionary.VerticalFragment;

import org.apache.log4j.Logger;

/**
 * Properties file based Database Dictionary DAO.
 * 
 * @author jccastrejon
 * 
 */
public class PropertiesDatabaseDictionaryDao implements DatabaseDictionaryDao {

    /**
     * Class logger.
     */
    private final static Logger logger = Logger.getLogger(PropertiesDatabaseDictionaryDao.class);

    /**
     * Path to the properties file containing the model's variables.
     */
    private final static String PROPERTIES_FILE_PATH = "/databaseDictionary.properties";

    /**
     * Properties file that contains the database dictionary data.
     */
    private Properties databaseDictionary;

    /**
     * Relation defined in the database dictionary.
     */
    private Map<String, Relation> relations;

    /**
     * Horizontal fragments defined in the database dictionary.
     */
    private Map<String, HorizontalFragment> horizontalFragments;

    /**
     * Vertical fragments defined in the database dictionary.
     */
    private Map<String, VerticalFragment> verticalFragments;

    /**
     * Default constructor.
     */
    public PropertiesDatabaseDictionaryDao() {
	try {
	    this.databaseDictionary = new Properties();
	    this.databaseDictionary.load(PropertiesDatabaseDictionaryDao.class
		    .getResourceAsStream(PropertiesDatabaseDictionaryDao.PROPERTIES_FILE_PATH));
	    this.initializeDatabaseDictionary();
	} catch (IOException e) {
	    logger.error("Unable to load database dictionary properties file", e);
	}
    }

    @Override
    public Relation getRelation(final String name) {
	Relation returnValue;

	returnValue = null;
	if (this.relations != null) {
	    returnValue = this.relations.get(name);
	}

	if ((returnValue == null) && (this.horizontalFragments != null)) {
	    returnValue = this.horizontalFragments.get(name);
	}

	if ((returnValue == null) && (this.verticalFragments != null)) {
	    returnValue = this.verticalFragments.get(name);
	}

	return returnValue;
    }

    /**
     * Initialize the database dictionary properties file.
     */
    private void initializeDatabaseDictionary() {
	String[] relations;
	String[] attributes;
	String[] predicates;
	String propertyValue;
	String currentFragment;
	Relation currentRelation;
	Predicate currentPredicate;
	Attribute currentAttribute;
	List<String> keyAttributes;
	String currentPredicateValue;
	String[] currentPredicateAttribute;
	AttributeDomain currentAttributeDomain;
	Collection<Predicate> currentPredicates;
	Collection<Attribute> currentAttributes;
	PredicateOperator currentPredicateOperator;

	// Load relations
	propertyValue = this.databaseDictionary.getProperty("relations");
	if (propertyValue != null) {
	    relations = propertyValue.split(",");
	    this.relations = new HashMap<String, Relation>(relations.length);

	    // Load relation attributes
	    for (String relation : relations) {
		attributes = this.databaseDictionary.getProperty(
			"relation." + relation + ".attributes").split(",");
		keyAttributes = Arrays.asList(this.databaseDictionary.getProperty(
			"relation." + relation + ".keyAttributes").split(","));

		currentAttributes = new ArrayList<Attribute>(attributes.length);
		for (String attribute : attributes) {
		    currentAttributeDomain = AttributeDomain.valueOf(this.databaseDictionary
			    .getProperty("attribute." + relation + "." + attribute + ".domain"));
		    currentAttribute = new Attribute(attribute, currentAttributeDomain,
			    keyAttributes.contains(attribute));
		    currentAttributes.add(currentAttribute);
		}

		this.relations.put(relation, new Relation(relation, currentAttributes));
	    }
	}

	// Load horizontal fragmentations
	propertyValue = this.databaseDictionary.getProperty("horizontalFragments");
	if (propertyValue != null) {
	    relations = propertyValue.split(",");
	    this.horizontalFragments = new HashMap<String, HorizontalFragment>(relations.length);

	    // Load horizontal fragment data
	    for (String fragment : relations) {
		currentFragment = this.databaseDictionary.getProperty("horizontalFragment."
			+ fragment + ".source");
		predicates = this.databaseDictionary.getProperty(
			"horizontalFragment." + fragment + ".predicates").split(",");

		currentRelation = this.getRelation(currentFragment);
		currentPredicates = new ArrayList<Predicate>(predicates.length);
		for (String predicate : predicates) {
		    currentPredicateAttribute = this.databaseDictionary.getProperty(
			    "predicate." + predicate + ".attribute").split("\\.");
		    currentPredicateOperator = PredicateOperator.valueOf(this.databaseDictionary
			    .getProperty("predicate." + predicate + ".operator"));
		    currentPredicateValue = this.databaseDictionary.getProperty("predicate."
			    + predicate + ".value");
		    currentPredicate = new Predicate(this.relations.get(
			    currentPredicateAttribute[0])
			    .getAttribute(currentPredicateAttribute[1]), currentPredicateOperator,
			    currentPredicateValue);
		    currentPredicates.add(currentPredicate);
		}

		this.horizontalFragments.put(fragment, new HorizontalFragment(fragment,
			currentRelation, currentPredicates));
	    }
	}

	// Load vertical fragmentations
	propertyValue = this.databaseDictionary.getProperty("verticalFragments");
	if (propertyValue != null) {
	    relations = propertyValue.split(",");
	    this.verticalFragments = new HashMap<String, VerticalFragment>(relations.length);

	    // Load vertical fragment data
	    for (String fragment : relations) {
		currentFragment = this.databaseDictionary.getProperty("verticalFragment."
			+ fragment + ".source");
		attributes = this.databaseDictionary.getProperty(
			"verticalFragment." + fragment + ".attributes").split(",");

		currentRelation = this.getRelation(currentFragment);
		currentAttributes = new ArrayList<Attribute>(attributes.length);
		for (String attribute : attributes) {
		    currentAttributes.add(currentRelation.getAttribute(attribute));
		}

		this.verticalFragments.put(fragment, new VerticalFragment(fragment,
			currentRelation, currentAttributes));
	    }
	}
    }
}
