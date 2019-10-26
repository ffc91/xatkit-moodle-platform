package com.xatkit.plugins.moodle.platform.io;

import com.xatkit.core.platform.io.IntentRecognitionHelper;
import com.xatkit.core.session.XatkitSession;
import com.xatkit.intent.RecognizedIntent;
import com.xatkit.plugins.chat.ChatUtils;
import com.xatkit.plugins.chat.platform.io.ChatIntentProvider;
import com.xatkit.plugins.moodle.platform.ReactPlatform;
import com.xatkit.plugins.moodle.platform.utils.platform.utils.MessageObject;
import com.xatkit.plugins.moodle.platform.utils.platform.utils.ReactUtils;
import com.xatkit.plugins.moodle.platform.utils.platform.utils.SocketEventTypes;

import fr.inria.atlanmod.commons.log.Log;
import org.apache.commons.configuration2.Configuration;

/**
 * A {@link ChatIntentProvider} that receives message through the socket server and translates them into
 * {@link RecognizedIntent}s.
 */
public class MoodleIntentProvider extends ChatIntentProvider<MoodlePlatform> {

    /**
     * Constructs a {@link ReactIntentProvider} from the provided {@code runtimePlatform} and {@code configuration}.
     * <p>
     * This constructor registers a dedicated listener to the socket server that receives user messages and
     * translates them into {@link RecognizedIntent}s using the {@link IntentRecognitionHelper}.
     *
     * @param runtimePlatform the {@link ReactPlatform} containing this provider
     * @param configuration   the platform's {@link Configuration}
     * @see IntentRecognitionHelper
     */
    public ReactIntentProvider(ReactPlatform runtimePlatform, Configuration configuration) {
        super(runtimePlatform, configuration);
        this.runtimePlatform.getSocketIOServer().addEventListener(SocketEventTypes.USER_MESSAGE.label,
                MessageObject.class, (socketIOClient, messageObject, ackRequest) -> {
                    Log.info("Received message {0}", messageObject.getMessage());
                    Log.info("Session ID: {0}", socketIOClient.getSessionId());
                    String username = messageObject.getUsername();
                    String channel = socketIOClient.getSessionId().toString();
                    String rawMessage = messageObject.getMessage();
                    XatkitSession session = this.getRuntimePlatform().createSessionFromChannel(channel);
                    RecognizedIntent recognizedIntent = IntentRecognitionHelper.getRecognizedIntent(rawMessage,
                            session, this.getRuntimePlatform().getXatkitCore());
                    session.getRuntimeContexts().setContextValue(ReactUtils.REACT_CONTEXT_KEY, 1,
                            ReactUtils.CHAT_USERNAME_CONTEXT_KEY, username);
                    session.getRuntimeContexts().setContextValue(ReactUtils.REACT_CONTEXT_KEY, 1,
                            ReactUtils.CHAT_CHANNEL_CONTEXT_KEY, channel);
                    session.getRuntimeContexts().setContextValue(ReactUtils.REACT_CONTEXT_KEY, 1,
                            ReactUtils.CHAT_RAW_MESSAGE_CONTEXT_KEY, rawMessage);
                    /*
                     * This provider extends ChatIntentProvider, and must set chat-related context values.
                     */
                    session.getRuntimeContexts().setContextValue(ChatUtils.CHAT_CONTEXT_KEY, 1,
                            ChatUtils.CHAT_USERNAME_CONTEXT_KEY, username);
                    session.getRuntimeContexts().setContextValue(ChatUtils.CHAT_CONTEXT_KEY, 1,
                            ChatUtils.CHAT_CHANNEL_CONTEXT_KEY, channel);
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
