package com.xatkit.plugins.moodle.platform.action;

import com.mashape.unirest.http.Headers;
import com.xatkit.core.platform.action.RestGetAction;
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
 * A {@link RestGetAction} that retrieves the courses in which the user with id {@code fromUserId} is registered.
 */
public class GetCourses extends RestGetAction<MoodlePlatform> {

    /**
     * Constructs a new {@link GetCourses} with the provided {@code runtimePlatform}, {@code session}, {@code
     * moodleEndpoint}, {@code userId}
     *
     * @param runtimePlatform the {@link MoodlePlatform} containing this action
     * @param session         the {@link XatkitSession} associated to this action
     * @param moodleEndpoint  the endpoint of the moodle instance
     * @param fromUserId      the moodle user id to get the registered courses
     * @throws NullPointerException     if the provided {@code runtimePlatform} or {@code session} is {@code null}
     * @throws IllegalArgumentException if the provided {@code message} or {@code channel} is {@code null}
     */
    public GetCourses(MoodlePlatform runtimePlatform, XatkitSession session, String moodleEndpoint,
            Integer fromUserId) {
        super(runtimePlatform, session, Collections.emptyMap(),
                moodleEndpoint + "&wsfunction=core_enrol_get_users_courses", new HashMap<String, Object>() {
                    {
                        put("userid", Integer.valueOf(fromUserId));
                    }
                });
    }

    /**
     * Handles the REST API response and computes the action's results
     * <p>
     *
     * @param headers the {@link Headers} returned by the REST API
     * @param status  the status code returned by the REST API
     * @param body    the {@link InputStream} containing the response body
     * @return the action's result
     */
    protected Object handleResponse(Headers headers, int status, InputStream body) {
        String result = "";
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
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(new InputSource(new StringReader(xml.toString())));

            NodeList errNodes = doc.getElementsByTagName("error");
            if (errNodes.getLength() > 0) {
                Element err = (Element) errNodes.item(0);
                System.out.println(err.getElementsByTagName("errorMessage").item(0).getTextContent());
            }

            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();
            XPathExpression expr = xpath.compile("//KEY[@name=\"fullname\"]");
            NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

            for (int i = 0; nl.getLength() > i; i++) {
                result += (i + 1) + " - " + nl.item(i).getTextContent() + "<br>";
            }

        } catch (SAXException | IOException | ParserConfigurationException | XPathExpressionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }
}
