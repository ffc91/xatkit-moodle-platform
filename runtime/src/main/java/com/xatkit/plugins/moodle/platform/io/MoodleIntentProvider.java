package com.xatkit.plugins.moodle.platform.io;

import com.xatkit.core.platform.io.IntentRecognitionHelper;
import com.xatkit.core.session.XatkitSession;
import com.xatkit.intent.RecognizedIntent;
import com.xatkit.plugins.chat.ChatUtils;
import com.xatkit.plugins.chat.platform.io.ChatIntentProvider;
import com.xatkit.plugins.moodle.platform.MoodlePlatform;
import com.xatkit.plugins.moodle.platform.utils.MessageObject;
import com.xatkit.plugins.moodle.platform.utils.MoodleUtils;
import com.xatkit.plugins.moodle.platform.utils.SocketEventTypes;

import fr.inria.atlanmod.commons.log.Log;


import org.apache.commons.configuration2.Configuration;

/**
 * A {@link ChatIntentProvider} that receives message through the socket server and translates them into
 * {@link RecognizedIntent}s.
 */
public class MoodleIntentProvider extends ChatIntentProvider<MoodlePlatform> {

    /**
     * Constructs a {@link MoodleIntentProvider} from the provided {@code runtimePlatform} and {@code configuration}.
     * <p>
     * This constructor registers a dedicated listener to the socket server that receives user messages and
     * translates them into {@link RecognizedIntent}s using the {@link IntentRecognitionHelper}.
     *
     * @param runtimePlatform the {@link MoodlePlatform} containing this provider
     * @param configuration   the platform's {@link Configuration}
     * @see IntentRecognitionHelper
     */
    public MoodleIntentProvider(MoodlePlatform runtimePlatform, Configuration configuration) {
        super(runtimePlatform, configuration);
        this.runtimePlatform.getSocketIOServer().addEventListener(SocketEventTypes.USER_MESSAGE.label, MessageObject.class, 
                (socketIOClient, messageObject, ackRequest)-> {
                        Log.info("Received message: {0}\n", messageObject.getMessage());
                        Log.info("Received userId: {0}\n", messageObject.getUserId());
                        Log.info("Received currentCourseID: {0}\n", messageObject.getCurrentCourseID());
                        String userId = messageObject.getUserId();
                        String currentCourseID = messageObject.getCurrentCourseID();
                        String rawMessage = messageObject.getMessage();
                        XatkitSession session = this.getRuntimePlatform().createSessionFromUserId(userId);
                        RecognizedIntent recognizedIntent = IntentRecognitionHelper.getRecognizedIntent(rawMessage,
                                session, this.getRuntimePlatform().getXatkitCore());
                    	session.getRuntimeContexts().setContextValue(MoodleUtils.MOODLE_CONTEXT_KEY, 1,
                                MoodleUtils.CHAT_USERNAME_CONTEXT_KEY, userId);
                    	session.getRuntimeContexts().setContextValue(MoodleUtils.MOODLE_CONTEXT_KEY, 1,
                    			MoodleUtils.CHAT_CHANNEL_CONTEXT_KEY, userId);
                    	session.getRuntimeContexts().setContextValue(MoodleUtils.MOODLE_CONTEXT_KEY, 1,
                                MoodleUtils.CHAT_RAW_MESSAGE_CONTEXT_KEY, rawMessage);
                        session.getRuntimeContexts().setContextValue(MoodleUtils.MOODLE_CONTEXT_KEY,1,
                        		MoodleUtils.MOODLE_CHAT_COURSEID_CONTEXT_KEY,currentCourseID);
                        
                        session.getRuntimeContexts().setContextValue(ChatUtils.CHAT_CONTEXT_KEY, 1,
                                ChatUtils.CHAT_USERNAME_CONTEXT_KEY, userId);
                        session.getRuntimeContexts().setContextValue(ChatUtils.CHAT_CONTEXT_KEY, 1,
                                ChatUtils.CHAT_CHANNEL_CONTEXT_KEY, userId);
                        session.getRuntimeContexts().setContextValue(ChatUtils.CHAT_CONTEXT_KEY, 1,
                                ChatUtils.CHAT_RAW_MESSAGE_CONTEXT_KEY, rawMessage);
                        
                        this.sendEventInstance(recognizedIntent, session);
                });
    }

    @Override
    public void run() {
        /*
         * Do nothing the socket server is started asynchronously.
         */
    }
   
}
