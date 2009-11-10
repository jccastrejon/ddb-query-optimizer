package mx.itesm.ddb.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;

import mx.itesm.ddb.service.operator.OperatorTree;

/**
 * Graphic Export Service.
 * 
 * @author jccastrejon
 * 
 */
public class GraphicExportService {

    /**
     * Class logger.
     */
    Logger logger = Logger.getLogger(GraphicExportService.class);

    /**
     * Save an image of the specified intermediate Operator Tree, in a directory
     * with the Query Id as name, in the specified Image Directory.
     * 
     * @param operatorTree
     *            Intermediate Operator Tree.
     * @param queryId
     *            Query Id.
     * @param intermediateOperatorTreeCount
     *            Number of intermediate Operator Tree.
     * @param label
     *            Image Label.
     * @param imageDir
     *            Directory where to save the temporary operator trees.
     * @throws IOException
     *             If an I/O error occurs.
     */
    public void saveIntermediateOperatorTree(final OperatorTree operatorTree, final String queryId,
	    final int intermediateOperatorTreeCount, final String label, final File imageDir)
	    throws IOException {
	File currentOperatorTreeImage;

	if (imageDir != null) {
	    currentOperatorTreeImage = new File(imageDir.getAbsolutePath() + "/" + queryId + "-"
		    + intermediateOperatorTreeCount + ".png");
	    // currentOperatorTreeImage.deleteOnExit();
	    this.exportOperatorTreeToPNG(operatorTree, intermediateOperatorTreeCount, label,
		    currentOperatorTreeImage);
	}
    }

    /**
     * Export the given Operator Tree to the specified PNG file.
     * 
     * @param operatorTree
     *            Operator Tree.
     * @param intermediateOperatorTreeCount
     *            Number of intermediate Operator Tree.
     * @param label
     *            Image Label.
     * @param imageFile
     *            Image File where the image will be saved.
     * @throws IOException
     *             In an I/O error occurs.
     */
    public void exportOperatorTreeToPNG(final OperatorTree operatorTree,
	    final int intermediateOperatorTreeCount, final String label, final File imageFile)
	    throws IOException {
	File dotFile;
	int processCode;
	Process process;
	String fileName;
	String dotCommand;
	FileWriter fileWriter;
	StringBuilder dotDescription;

	if ((imageFile == null) || (!imageFile.getAbsolutePath().endsWith(".png"))) {
	    throw new IllegalArgumentException("Not an png file: " + imageFile.getAbsolutePath());
	}

	fileName = imageFile.getName().substring(0, imageFile.getName().indexOf('.'));
	dotFile = new File(imageFile.getParent() + "/" + fileName + ".dot");

	// Build dot file
	dotDescription = new StringBuilder(
		"digraph \""
			+ fileName
			+ "\" {\n\tfontsize=8;\n\tlabel=\"Step #"
			+ intermediateOperatorTreeCount
			+ "\\n("
			+ label
			+ ")\\n\\n\";\n\tlabelloc=\"t\";\n\tnode[shape=box, fontsize=8, height=.1, width=.1];\n");
	dotDescription.append(operatorTree.getRootNode().toString());
	dotDescription.append("}");

	// Save dot file
	fileWriter = new FileWriter(dotFile, false);
	fileWriter.write(dotDescription.toString());
	fileWriter.close();

	// Execute dot command
	try {
	    dotCommand = "dot -Tpng " + dotFile.getAbsolutePath() + " -o "
		    + imageFile.getAbsolutePath();
	    process = Runtime.getRuntime().exec(dotCommand);
	    processCode = process.waitFor();
	    dotFile.delete();

	    if (processCode != 0) {
		throw new RuntimeException("An error ocurred while executing: " + dotCommand);
	    }

	} catch (Exception e) {
	    logger.error("Error creating image file: " + imageFile.getAbsolutePath(), e);
	    throw new RuntimeException("Error creating image file: " + imageFile.getAbsolutePath(),
		    e);
	}
    }
}
