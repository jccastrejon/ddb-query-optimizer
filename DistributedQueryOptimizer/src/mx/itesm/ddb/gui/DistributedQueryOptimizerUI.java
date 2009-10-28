package mx.itesm.ddb.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

import mx.itesm.ddb.service.OptimizerManager;
import mx.itesm.ddb.service.Query;

/**
 * Graphic User Interface for the Distributed Query Optimizer.
 * 
 * @author jccastrejon
 * 
 */
public class DistributedQueryOptimizerUI extends JFrame implements ActionListener, Runnable {

    /**
     * Class logger.
     */
    Logger logger = Logger.getLogger(DistributedQueryOptimizerUI.class.getName());

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Test Queries file.
     */
    private static final String TEST_QUERIES = "./sql/testQueries.sql";

    /**
     * 
     */
    private static final String RESULTS_FONT_SIZE = "6";

    /**
     * This is where the sql query is written.
     */
    private JTextArea queryText;

    /**
     * This is where the sql query result is shown.
     */
    private JTextPane resultText;

    /**
     * Contains the query text.
     */
    private JScrollPane queryScrollPane;

    /**
     * Contains the result text.
     */
    private JScrollPane resultScrollPane;

    /**
     * Button that starts the query execution.
     */
    private JButton executeQueryButton;

    /**
     * Button that starts the tests execution.
     */
    private JButton testQueriesButton;

    /**
     * Area where the query and buttons are stored.
     */
    private JPanel commandPanel;

    /**
     * Query Optimizer Manager.
     */
    OptimizerManager optimizerManager;

    /**
     * Current action event (execute - test)
     */
    private ActionEvent currentEvent;

    /**
     * Default constructor.
     */
    public DistributedQueryOptimizerUI() {
	queryText = new JTextArea(10, 40);
	resultText = new JTextPane();
	queryScrollPane = new JScrollPane(queryText);
	resultScrollPane = new JScrollPane(resultText);
	executeQueryButton = new JButton("Execute");
	testQueriesButton = new JButton("Tests");
	optimizerManager = new OptimizerManager();

	resultText.setContentType("text/html");
	resultText.setPreferredSize(new Dimension(900, 500));
	resultText.setEditable(false);
	executeQueryButton.addActionListener(this);
	testQueriesButton.addActionListener(this);

	commandPanel = new JPanel();
	commandPanel.setLayout(new BoxLayout(commandPanel, BoxLayout.X_AXIS));
	commandPanel.add(queryScrollPane);
	commandPanel.add(executeQueryButton);
	commandPanel.add(testQueriesButton);

	this.add(commandPanel, BorderLayout.NORTH);
	this.add(resultScrollPane, BorderLayout.CENTER);

	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setTitle("Distributed Query Optimizer");
    }

    /**
     * Entry point.
     * 
     * @param args
     *            Startup arguments.
     */
    public static void main(String args[]) {
	SwingUtilities.invokeLater(new Runnable() {
	    public void run() {
		DistributedQueryOptimizerUI app = new DistributedQueryOptimizerUI();
		app.pack();
		app.setVisible(true);
	    }
	});
    }

    @Override
    public void actionPerformed(ActionEvent event) {
	this.executeQueryButton.setEnabled(false);
	this.testQueriesButton.setEnabled(false);
	currentEvent = event;

	new Thread(this).start();
    }

    @Override
    public void run() {
	Query query;
	String testQuery;
	StringBuilder testQueries;
	StringBuilder testQueriesResult;
	BufferedReader testReader;

	if (currentEvent.getSource() == executeQueryButton) {
	    this.resultText.setText("<html><font size='"
		    + DistributedQueryOptimizerUI.RESULTS_FONT_SIZE + "'>"
		    + optimizerManager.createQuery(queryText.getText()).getRelationalAlgebra()
		    + "</font></html>");
	}

	else if (currentEvent.getSource() == testQueriesButton) {
	    try {
		testReader = new BufferedReader(new FileReader(
			DistributedQueryOptimizerUI.TEST_QUERIES));
		testQueries = new StringBuilder();
		testQueriesResult = new StringBuilder();
		query = new Query();

		this.queryText.setText("");
		this.resultText.setText("");
		testQueriesResult.append("<html><font size='"
			+ DistributedQueryOptimizerUI.RESULTS_FONT_SIZE + "'>");
		while ((testQuery = testReader.readLine()) != null) {
		    testQueries.append(testQuery).append("\n");
		    query.setSql(testQuery);
		    optimizerManager.updateRelationalAlgebra(query);
		    testQueriesResult.append(query.getRelationalAlgebra()).append("<br/>");
		}
		testQueriesResult.append("</font></html>");

		this.queryText.setText(testQueries.toString());
		this.resultText.setText(testQueriesResult.toString());
	    } catch (FileNotFoundException e) {
		resultText.setText(DistributedQueryOptimizerUI.TEST_QUERIES + " not found");
	    } catch (IOException e) {
		resultText.setText("Problem while reading "
			+ DistributedQueryOptimizerUI.TEST_QUERIES);
	    }
	}

	this.executeQueryButton.setEnabled(true);
	this.testQueriesButton.setEnabled(true);
	currentEvent = null;
    }
}
