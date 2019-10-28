package com.xatkit.plugins.moodle.platform.action;

import com.xatkit.core.platform.action.RuntimeMessageAction;
import com.xatkit.core.session.XatkitSession;
import com.xatkit.plugins.moodle.platform.MoodlePlatform;
import com.xatkit.plugins.moodle.platform.utils.MessageObject;
import com.xatkit.plugins.moodle.platform.utils.SocketEventTypes;

import java.util.UUID;

import static fr.inria.atlanmod.commons.Preconditions.checkArgument;
import static java.util.Objects.nonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * A {@link RuntimeMessageAction} that posts a {@code message} to a given xatkit-react {@code channel}.
 */
public class PostMessage extends RuntimeMessageAction<MoodlePlatform> {

    /**
     * The channel to post the message to.
     */
    private String channel;

    private String touserid;
    private String text;
    private String textformat;
    private String clientmsgid;

    /**
     * Constructs a new {@link PostMessage} with the provided {@code runtimePlatform}, {@code session}, {@code
     * message}, and {@code channel}.
     *
     * @param runtimePlatform the {@link ReactPlatform} containing this action
     * @param session         the {@link XatkitSession} associated to this action
     * @param message         the message to post
     * @param channel         the xatkit-react channel to post the message to
     * @throws NullPointerException     if the provided {@code runtimePlatform} or {@code session} is {@code null}
     * @throws IllegalArgumentException if the provided {@code message} or {@code channel} is {@code null}
     */
    public PostMessage(MoodlePlatform runtimePlatform, XatkitSession session, String message, String channel) {
        super(runtimePlatform, session, message);
        checkArgument(nonNull(channel) && !(channel.isEmpty()), "Cannot construct a %s action with the provided " +
                "channel %s, expected a non-null and not empty String", this.getClass().getSimpleName(), channel);
               
        
      
                
        /// NEED TO BE CHANGED
        String token = "a27063218561c0a2ebdc83eb563e1614";
        String domainName = "http://192.168.3.5";

        /// REST RETURNED VALUES FORMAT
        String restformat = "json"; //Also possible in Moodle 2.2 and later: 'json'
                                   //Setting it to 'json' will fail all calls on earlier Moodle version
        if (restformat.equals("json")) {
            restformat = "&moodlewsrestformat=" + restformat;
        } else {
            restformat = "";
        }

        /// PARAMETERS - NEED TO BE CHANGED IF YOU CALL A DIFFERENT FUNCTION
        String functionName = "core_message_send_instant_messages";
        String urlParameters;
        HttpURLConnection connection = null;
		BufferedReader reader = null;
		String retVal = null;
		String serverurl;
        try {
	    	urlParameters = String.format("messages[0][touserid]=%s&messages[0][text]=%s&messages[0][textformat]=%s&messages[0][clientmsgid]=%s",
	    			URLEncoder.encode(touserid, "UTF-8"),
	    			URLEncoder.encode(text, "UTF-8"),
	    			URLEncoder.encode(textformat, "UTF-8"),
	    			URLEncoder.encode(clientmsgid, "UTF-8"));
		

			serverurl = domainName + "/webservice/rest/server.php" + "?wstoken=" + token + "&wsfunction=" + functionName;    
			URL resetEndpoint = new URL(serverurl+"?"+urlParameters);
			connection = (HttpURLConnection) resetEndpoint.openConnection();
			//Set request method to GET as required from the API
			connection.setRequestMethod("GET");
			
			//con.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			//con.setRequestProperty("Content-Language", "en-US");
			//con.setDoOutput(true);
			//con.setUseCaches (false);
			//con.setDoInput(true);
			
			// Read the response
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuilder jsonSb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				jsonSb.append(line);
			}
			retVal = jsonSb.toString();
			// print out the json response
			System.out.println(retVal);
		} catch (Exception e) {
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
			if (connection != null) {
				connection.disconnect();
				}
		}
    }

    /**
     * Posts the provided {@code message} to the given {@code channel}.
     * <p>
     * Posted messages are pushed to the client application using the underlying socket server.
     *
     * @return {@code null}
     */
    @Override
    protected Object compute() {
        this.runtimePlatform.getSocketIOServer().getClient(UUID.fromString(channel)).
                sendEvent(SocketEventTypes.BOT_MESSAGE.label, new MessageObject(message, "xatkit"));
        return null;
    }

    @Override
    protected XatkitSession getClientSession() {
        return this.runtimePlatform.createSessionFromChannel(channel);
    }
    
    
    
    
}
