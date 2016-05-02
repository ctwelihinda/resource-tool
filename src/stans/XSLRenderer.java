/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
/**
 *
 * @author peter
 */
public class XSLRenderer {
    public static String transformXMLFromStream( HttpServletRequest request, ByteArrayInputStream iStream, String strXSLFile, HashMap params ) {
        String error_text = "";
        try {

            error_text = "could not create stringWriter";
            StringWriter output = new StringWriter();


            //for grabbing straight from the building block

            //getting xsl file to load into
            error_text = "could not open XSL file";
            File xslFile = new File( strXSLFile );

            //StringWriter functions like a buffer to get the results
            error_text = "could not create SrResult";
            StreamResult srResult = new StreamResult(output);

            error_text = "could not create ssXSLSource StreamSource";
            StreamSource ssXSLSource = new StreamSource( request.getSession().getServletContext().getRealPath( "/" ) + "xsl/" + strXSLFile );

            error_text = "could not open outputstream, there may be a problem with the provided XML";
            StreamSource ssXML = new StreamSource( iStream );

            error_text = "could not create transformer";
            TransformerFactory tfFactory = TransformerFactory.newInstance();
            String strFinalResult = null;

            error_text = "could not attach ssXSLSource to transformer";
			
            Transformer tranTransformer = tfFactory.newTransformer( ssXSLSource );
			if( params!=null ) {
				Iterator iter = params.entrySet().iterator();
				while (iter.hasNext()) {
					error_text = "iterator error ";
					Map.Entry pairs = (Map.Entry) iter.next();
					error_text = "could not add parameter: " + (String) pairs.getKey() + "["+ pairs.getValue() +"]";
					//tranTransformer.setParameter((String) pairs.getKey(), pairs.getValue());
				}
			}

            //tranTransformer.setParameter("theXML", strXMLFile);
            error_text = "could not perform transform, XML or XSL are invalid ";
            tranTransformer.transform( ssXML, srResult );

            strFinalResult = output.getBuffer().toString();

            error_text = "";
            return strFinalResult;
        } catch (Exception e) {
			StringWriter stringWriter = new StringWriter();
			e.printStackTrace( new PrintWriter( stringWriter ) );
			String stackTrace = stringWriter.toString();
            return "Error(" + error_text + "): " + stackTrace;
        }
    }

}
