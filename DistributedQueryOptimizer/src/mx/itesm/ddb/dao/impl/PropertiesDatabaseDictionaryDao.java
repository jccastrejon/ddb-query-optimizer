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
import mx.itesm.ddb.model.dictionary.FragmentationType;
import mx.itesm.ddb.model.dictionary.HorizontalFragment;
import mx.itesm.ddb.model.dictionary.HybridFragment;
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
     * Relations defined in the database dictionary.
     */
    private Map<String, Relation> relations;

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
	    returnValue = this.relations.get(name.toLowerCase());
	    if (returnValue == null) {
		for (Relation relation : this.relations.values()) {
		    if (relation.getFragments() != null) {
			for (Relation fragment : relation.getFragments()) {
			    if (fragment.getName().equalsIgnoreCase(name)) {
				returnValue = fragment;
				break;
			    }
			}

			if (returnValue != null) {
			    break;
			}
		    }
		}
	    }
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
	Attribute currentAttribute;
	List<String> keyAttributes;
	FragmentationType fragmentationType;
	AttributeDomain currentAttributeDomain;
	Collection<Predicate> currentPredicates;
	Collection<Attribute> currentAttributes;

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
		    currentAttribute = new Attribute(relation.toLowerCase() + "."
			    + attribute.toLowerCase(), currentAttributeDomain, keyAttributes
			    .contains(attribute));
		    currentAttributes.add(currentAttribute);
		}

		this.relations.put(relation.toLowerCase(),
			new Relation(relation, currentAttributes));
	    }
	}

	// Load horizontal fragmentations
	propertyValue = this.databaseDictionary.getProperty("horizontalFragments");
	if (propertyValue != null) {
	    relations = propertyValue.split(",");

	    // Load horizontal fragment data
	    for (String fragment : relations) {
		currentFragment = this.databaseDictionary.getProperty("horizontalFragment."
			+ fragment + ".source");
		predicates = this.databaseDictionary.getProperty(
			"horizontalFragment." + fragment + ".predicates").split(",");

		currentRelation = this.getRelation(currentFragment);
		currentPredicates = this.loadPredicates(predicates);

		// Decide fragmentation type
		fragmentationType = FragmentationType.Horizontal;
		for (Predicate predicate : currentPredicates) {
		    // If the predicates reference another relation, this is a
		    // derived horizontal fragment
		    if (!currentRelation.containsAttribute(predicate.getAttribute().getName())) {
			fragmentationType = FragmentationType.DerivedHorizontal;
			break;
		    }
		}

		new HorizontalFragment(fragment, currentRelation, currentPredicates,
			fragmentationType);
	    }
	}

	// Load vertical fragmentations
	propertyValue = this.databaseDictionary.getProperty("verticalFragments");
	if (propertyValue != null) {
	    relations = propertyValue.split(",");

	    // Load vertical fragment data
	    for (String fragment : relations) {
		currentFragment = this.databaseDictionary.getProperty("verticalFragment."
			+ fragment + ".source");
		attributes = this.databaseDictionary.getProperty(
			"verticalFragment." + fragment + ".attributes").split(",");

		currentRelation = this.getRelation(currentFragment);
		currentAttributes = this.loadAttributes(attributes, currentRelation,
			currentFragment);

		new VerticalFragment(fragment, currentRelation, currentAttributes);
	    }
	}

	// Load hybrid fragmentations
	propertyValue = this.databaseDictionary.getProperty("hybridFragments");
	if (propertyValue != null) {
	    relations = propertyValue.split(",");

	    // Load hybrid fragment data
	    for (String fragment : relations) {
		currentFragment = this.databaseDictionary.getProperty("hybridFragment." + fragment
			+ ".source");
		attributes = this.databaseDictionary.getProperty(
			"hybridFragment." + fragment + ".attributes").split(",");
		predicates = this.databaseDictionary.getProperty(
			"hybridFragment." + fragment + ".predicates").split(",");
		currentRelation = this.getRelation(currentFragment);

		// Load predicates (Horizontal)
		currentPredicates = this.loadPredicates(predicates);

		// Load attributes (Vertical)
		currentAttributes = this.loadAttributes(attributes, currentRelation,
			currentFragment);

		new HybridFragment(fragment, currentRelation, currentPredicates, currentAttributes,
			FragmentationType.Hybrid);
	    }
	}
    }

    /**
     * Load the specified predicates definitions.
     * 
     * @param predicates
     *            Predicates to load.
     * @return Collection with the loaded Predicates.
     */
    private Collection<Predicate> loadPredicates(final String[] predicates) {
	Predicate currentPredicate;
	String currentPredicateValue;
	Collection<Predicate> returnValue;
	String[] currentPredicateAttribute;
	PredicateOperator currentPredicateOperator;

	returnValue = new ArrayList<Predicate>(predicates.length);
	for (String predicate : predicates) {
	    currentPredicateAttribute = this.databaseDictionary.getProperty(
		    "predicate." + predicate + ".attribute").split("\\.");
	    currentPredicateOperator = PredicateOperator.valueOf(this.databaseDictionary
		    .getProperty("predicate." + predicate + ".operator"));
	    currentPredicateValue = this.databaseDictionary.getProperty("predicate." + predicate
		    + ".value");
	    currentPredicate = new Predicate(this.relations.get(
		    currentPredicateAttribute[0].toLowerCase()).getAttribute(
		    currentPredicateAttribute[0].toLowerCase() + "."
			    + currentPredicateAttribute[1].toLowerCase()),
		    currentPredicateOperator, currentPredicateValue);
	    returnValue.add(currentPredicate);
	}

	return returnValue;
    }

    /**
     * Load the specified attributes definition.
     * 
     * @param attributes
     *            Attributes to load.
     * @param currentRelation
     *            Relation being loaded.
     * @param currentFragment
     *            Fragment being loaded.
     * @return Collection with the loaded Attributes.
     */
    private Collection<Attribute> loadAttributes(final String[] attributes,
	    final Relation currentRelation, final String currentFragment) {
	Collection<Attribute> returnValue;

	returnValue = new ArrayList<Attribute>(attributes.length);
	for (String attribute : attributes) {
	    returnValue.add(currentRelation.getAttribute(currentFragment + "." + attribute));
	}

	return returnValue;
    }
}
