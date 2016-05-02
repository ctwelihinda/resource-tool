package stans;

import java.io.ByteArrayInputStream;
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.*;

/**
 *
 * @author mike
 */
public class XMLReader {

	String strRootPath;

	public void setRootPath (String strRootPath) {
		this.strRootPath = strRootPath;
	}

	String lang;

	public void setLang (String lang) {
		this.lang = lang;
	}

	Document doc;

	public void setDoc (String strXMLFile) {

		try {
			strXMLFile = strXMLFile == null ? "default.xml" : strXMLFile;

			DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
			dbfactory.setNamespaceAware(true);
			dbfactory.setXIncludeAware(true);

			DocumentBuilder parser = dbfactory.newDocumentBuilder();

			this.doc = parser.parse(new File(strRootPath + "xml/" + strXMLFile));
			
		} catch (Exception e) {
			System.out.println("XMLReader.setDoc: Error Setting Document\n");
			e.printStackTrace();
		}
	}
        
        public void setStream (ByteArrayInputStream iStream) {
            try {
				DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
				dbfactory.setNamespaceAware(true);
				dbfactory.setXIncludeAware(true);

				DocumentBuilder parser = dbfactory.newDocumentBuilder();

				this.doc = parser.parse(iStream);

            } catch (Exception e) {
                System.out.println("XMLReader.setStream: Error Setting Stream\n");
                e.printStackTrace();
            }
        }

	public XMLReader () {}

		public String getNodeValue (String xpath) {
			try {
				if (doc == null){
					return "ERROR: Document is null" + xpath;
				}
				XPathFactory xpfactory = XPathFactory.newInstance();
				XPath xpathprocessor = xpfactory.newXPath();
				//create XPath expression
				XPathExpression xpe = xpathprocessor.compile(xpath);
				return xpe.evaluate(doc);
			} catch (Exception e){
				System.out.println("XMLReader.getNodeValue: Error\n");
				e.printStackTrace();
				return "ERROR";
			}
		}
        
        public Boolean nodeExists (String xpath) {
		try {
			if (doc == null){
				return false;
			}
			XPathFactory xpfactory = XPathFactory.newInstance();
			XPath xpathprocessor = xpfactory.newXPath();
			//create XPath expression
			XPathExpression xpe = xpathprocessor.compile(xpath);
			//execute the XPath expression
			//return xpe.evaluate(doc);
                        //int numberofbodies = Integer.parseInt((String) xpe.evaluate(xpath, doc));
                        //if( numberofbodies==0) {
                        //     body node missing
                        //}
			return (Boolean)xpe.evaluate(doc, XPathConstants.BOOLEAN);
		} catch (Exception e){
			System.out.println("XMLReader.nodeExists: Error\n");
			e.printStackTrace();
			return false;
		}
        }
        
        public NodeList getNodelist (String xpath)
        {            
			try {
				if( doc==null ) {
					return null;
				}
				XPathFactory xpfactory = XPathFactory.newInstance();
				XPath xpathprocessor = xpfactory.newXPath();
			//create XPath expression
				XPathExpression xpe = xpathprocessor.compile(xpath);
				return (NodeList)xpe.evaluate(doc, XPathConstants.NODESET);
			} catch (Exception e){
				System.out.println("XMLReader.getNodeList: Error\n");
				e.printStackTrace();
							return null;
			}

        }
        
        public String[] getChildren (String xpath) {
            try {
                if (doc == null){
                    return null;
                }
                XPathFactory xpfactory = XPathFactory.newInstance();
                XPath xpathprocessor = xpfactory.newXPath();
                //create XPath expression
                XPathExpression xpe = xpathprocessor.compile(xpath);

                String[] theChildren;
                
                return null;
            } catch (Exception e){
                System.out.println("Error in getChildren function\n");
                e.printStackTrace();
                return null;
            }
            
        } 
}
