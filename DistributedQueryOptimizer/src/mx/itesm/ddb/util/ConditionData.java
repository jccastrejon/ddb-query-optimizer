package mx.itesm.ddb.util;

/**
 * @author jccastrejon
 * 
 */
public interface ConditionData {

    /**
     * 
     */
    public final static ConditionData OR_CONDITION = new ConditionData() {
    };

    /**
     * 
     */
    public final static ConditionData AND_CONDITION = new ConditionData() {
    };

    /**
     * 
     */
    public final static ConditionData NOT_CONDITION = new ConditionData() {
    };
    
    /**
     * 
     */
    public final static ConditionData EXISTS_CONDITION = new ConditionData() {
    };
}
