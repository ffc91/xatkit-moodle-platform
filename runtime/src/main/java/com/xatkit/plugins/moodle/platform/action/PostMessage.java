package com.xatkit.plugins.moodle.platform.action;

import com.mashape.unirest.http.Headers;
import com.xatkit.core.platform.action.RestGetAction;
import com.xatkit.core.platform.action.RuntimeMessageAction;
import com.xatkit.core.session.XatkitSession;
import com.xatkit.plugins.moodle.platform.MoodlePlatform;

import java.util.Collections;
import java.util.HashMap;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * A {@link RuntimeMessageAction} that posts a {@code message} to a given xatkit-react {@code channel}.
 */
public class PostMessage extends RestGetAction<MoodlePlatform> {

    /**
     * Constructs a new {@link PostMessage} with the provided {@code runtimePlatform}, {@code session}...
     *
     * @param runtimePlatform the {@link MoodlePlatform} containing this action
     * @param session         the {@link XatkitSession} associated to this action
     * @throws NullPointerException     if the provided {@code runtimePlatform} or {@code session} is {@code null}
     * @throws IllegalArgumentException if the provided {@code message} or {@code channel} is {@code null}
     */
    public PostMessage(MoodlePlatform runtimePlatform, XatkitSession session, String moodleEndpoint, String toUserId, String message) {
        super(runtimePlatform, session, Collections.emptyMap(), moodleEndpoint + "&wsfunction=core_message_send_instant_messages", 
                new HashMap<String,Object>() {{
                    put("messages[0][touserid]", Integer.valueOf(toUserId));
                    put("messages[0][text]", message);  
                }});
    }


    protected Object handleResponse(Headers headers, int status, InputStream body) {
    	BufferedReader reader = new BufferedReader(new InputStreamReader(body));
    	StringBuilder jsonSb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				jsonSb.append(line);
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
		// print out the json response
		System.out.println(jsonSb.toString());
    	return null;
    }    
}
