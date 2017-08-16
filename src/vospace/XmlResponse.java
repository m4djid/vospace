package vospace;

import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import uws.UWSException;
import uws.job.ErrorType;

public class XmlResponse {

	Document doc;
	String cible, direction, protocol, result, view;
	


	public static String toString(Document doc) {
	    try {
	        StringWriter sw = new StringWriter();
	        TransformerFactory tf = TransformerFactory.newInstance();
	        Transformer transformer = tf.newTransformer();
	        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
	        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	
	        transformer.transform(new DOMSource(doc), new StreamResult(sw));
	        return sw.toString();
	    } catch (Exception ex) {
	        throw new RuntimeException("Error converting to String", ex);
	    }
	}


	
	public String responseXML(String cible, String direction, String protocol, String result, String view) throws UWSException{
		this.cible = cible;
		this.direction = direction;
		this.protocol = protocol;
		this.result = result;
		this.view = view;
		  try {
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	
				// root elements
				doc = docBuilder.newDocument();
				Element rootElement = doc.createElement("vos:transfer");
				doc.appendChild(rootElement);
				
				// set attribute to root element
				Attr xmlns = doc.createAttribute("xmlns:vos");
				xmlns.setValue("http://www.ivoa.net/xml/VOSpace/v2.1");
				rootElement.setAttributeNode(xmlns);
	
				// target element
				Element target = doc.createElement("vos:target");
				rootElement.appendChild(target);
				target.appendChild(doc.createTextNode(this.cible));
	
				// direction element
				Element direct = doc.createElement("vos:direction");
				rootElement.appendChild(direct);
				direct.appendChild(doc.createTextNode(this.direction));
							
				// protocol element
				Element proto = doc.createElement("vos:protocol");
				rootElement.appendChild(proto);
				
				// set attribute to protocol element
				Attr p = doc.createAttribute("uri");
				p.setValue(this.protocol);
				proto.setAttributeNode(p);
				
				// view element
				if(!view.equals("_")) {
					Element v = doc.createElement("vos:view");
					rootElement.appendChild(v);
					
					// set attribute to view element
					Attr vUri = doc.createAttribute("uri");
					vUri.setValue(this.view);
					v.setAttributeNode(vUri);
				}
	
				// endpoint element
				Element endpoint = doc.createElement("vos:endpoint");
				endpoint.appendChild(doc.createTextNode(this.result));
				proto.appendChild(endpoint);
				
				return toString(doc);
				
			  } catch (ParserConfigurationException pce) {
				pce.printStackTrace();
				throw new UWSException(UWSException.INTERNAL_SERVER_ERROR, pce, ErrorType.TRANSIENT);
			  }
	  
	}
	  
}
