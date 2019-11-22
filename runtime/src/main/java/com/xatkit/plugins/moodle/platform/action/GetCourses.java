package com.xatkit.plugins.moodle.platform.action;

import com.mashape.unirest.http.Headers;
import com.xatkit.core.platform.action.RestGetAction;
import com.xatkit.core.platform.action.RuntimeMessageAction;
import com.xatkit.core.session.XatkitSession;
import com.xatkit.plugins.moodle.platform.MoodlePlatform;

import java.util.Collections;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

/**
 * A {@link RuntimeMessageAction} that posts a {@code message} to a given xatkit-moodle {@code channel}.
 */
public class GetCourses extends RestGetAction<MoodlePlatform> {

    /**
     * Constructs a new {@link PostMessage2} with the provided {@code runtimePlatform}, {@code session}...
     *
     * @param runtimePlatform the {@link MoodlePlatform} containing this action
     * @param session         the {@link XatkitSession} associated to this action
     * @throws NullPointerException     if the provided {@code runtimePlatform} or {@code session} is {@code null}
     * @throws IllegalArgumentException if the provided {@code message} or {@code channel} is {@code null}
     */
    @SuppressWarnings("serial")
	public GetCourses(MoodlePlatform runtimePlatform, XatkitSession session, String moodleEndpoint, Integer fromUserId) {
        super(runtimePlatform, session, Collections.emptyMap(), moodleEndpoint + "&wsfunction=core_enrol_get_users_courses", 
                new HashMap<String,Object>() {{
                    put("userid", Integer.valueOf(fromUserId));
                }});
    }


    protected Object handleResponse(Headers headers, int status, InputStream body) {
    	String resultado = "";
    	BufferedReader reader = new BufferedReader(new InputStreamReader(body));
    	StringBuilder xml = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				xml.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// Clean up
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(xml.toString())));
			
			NodeList errNodes = doc.getElementsByTagName("error");
			if (errNodes.getLength() > 0) {
				Element err = (Element)errNodes.item(0);
				System.out.println(err.getElementsByTagName("errorMessage").item(0).getTextContent());
			} else { 
				
			}
			
			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
			XPathExpression expr = xpath.compile("//KEY[@name=\"fullname\"]");
			NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			
			System.out.println("******************************"+nl.item(0).getTextContent()+"****************************************");
			
			for(int i=0;nl.getLength()>i; i++) {
				resultado += (i+1) + " - " + nl.item(i).getTextContent() + "<br>";
			}
			
	    } catch (SAXException | IOException | ParserConfigurationException | XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		// print out the xml response
		System.out.println(xml.toString());
    	return resultado;
    }    
}
