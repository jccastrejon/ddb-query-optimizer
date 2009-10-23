package mx.itesm.ddb.util;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;

/**
 * @author jccastrejon
 * 
 */
@SuppressWarnings("unchecked")
public class SqlNode {

    /** Symbol table */
    protected static java.util.Hashtable symtab = new java.util.Hashtable();

    /** Stack for calculations. */
    protected static Object[] stack = new Object[1024];
    protected static int top = -1;

    /**
     * @throws UnsupportedOperationException
     *             if called
     */
    public void interpret() {
	throw new UnsupportedOperationException(); // It better not come here.
    }

    protected static Writer out = new PrintWriter(System.out);
    protected static Reader in = new InputStreamReader(System.in);

    /**
     * @param in
     *            the input to set
     */
    public static void setIn(Reader in) {
	SqlNode.in = in;
    }

    /**
     * @param out
     *            the output to set
     */
    public static void setOut(Writer out) {
	SqlNode.out = out;
    }
}
