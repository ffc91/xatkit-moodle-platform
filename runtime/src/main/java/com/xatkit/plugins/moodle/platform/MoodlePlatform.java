package com.xatkit.plugins.moodle.platform;

import com.corundumstudio.socketio.SocketIOServer;
import com.xatkit.core.XatkitCore;
import com.xatkit.core.server.XatkitServerUtils;
import com.xatkit.core.session.XatkitSession;
import com.xatkit.plugins.chat.platform.ChatPlatform;
import com.xatkit.plugins.moodle.platform.action.PostMessage;
import com.xatkit.plugins.moodle.platform.utils.MoodleUtils;

import fr.inria.atlanmod.commons.log.Log;
import org.apache.commons.configuration2.Configuration;

/**
 * This platform creates a server that accepts socket connexions from the moodle application. Messages are received
 * in real-time, and replies are sent to the client using moodle's API.
 * <p>
 * This platform provides the following actions:
 * <ul>
 * <li>{@link PostMessage}: post a message to a given channel (i.e. moodle user)</li>
 * <li>{@link GetCourses}: retrieves the courses in which a user is registered.</li>
 * </ul>
 */
public class MoodlePlatform extends ChatPlatform {

    /**
     * The socket server used to receive and send messages.
     */
    private SocketIOServer socketIOServer;

    /**
     * Constructs a new {@link MoodlePlatform} from the provided {@link XatkitCore} and {@link Configuration}.
     * <p>
     * This constructor initializes the underlying socket server using the port specified in the provided
     * {@link Configuration}.
     *
     * @param xatkitCore    the {@link XatkitCore} instance associated to this runtimePlatform
     * @param configuration the platform's {@link Configuration} containing the port of the socket server
     * @throws NullPointerException if the provided {@code xatkitCore} or {@code configuration} is {@code null}
     */
    public MoodlePlatform(XatkitCore xatkitCore, Configuration configuration) {
        super(xatkitCore, configuration);
        int socketServerPort = configuration.getInt(MoodleUtils.MOODLE_SERVER_PORT_KEY,
                MoodleUtils.DEFAULT_MOODLE_SERVER_PORT);
        String originLocation = configuration.getString(XatkitServerUtils.SERVER_PUBLIC_URL_KEY,
                XatkitServerUtils.DEFAULT_SERVER_LOCATION);
        int originPort = configuration.getInt(XatkitServerUtils.SERVER_PORT_KEY,
                XatkitServerUtils.DEFAULT_SERVER_PORT);
        String origin = originLocation + ":" + Integer.toString(originPort);
        com.corundumstudio.socketio.Configuration socketioConfiguration =
                new com.corundumstudio.socketio.Configuration();
        /*
         * TODO Doesn't seem to be needed for the moment, needs to be tested when deployed on a server.
         */
        socketioConfiguration.setPort(socketServerPort);
        /*
         * The URL where the chatbox is displayed. Setting this is required to avoid CORS issues.
         * Note: wildcards don't work here.
         */
        socketioConfiguration.setOrigin(origin);
        /*
         * Use random sessions to avoid sharing the same session ID between multiple tabs (see https://github
         * .com/mrniko/netty-socketio/issues/617).
         */
        socketioConfiguration.setRandomSession(true);
        socketIOServer = new SocketIOServer(socketioConfiguration);
        socketIOServer.addConnectListener(socketIOClient -> Log.info("Moodle Chat User Connected"));
        socketIOServer.addDisconnectListener(socketIOClient -> Log.info("Moodle Chat User Disconnected"));
        this.socketIOServer.startAsync();
    }

    /**
     * Returns the socket server used to receive and send messages.
     *
     * @return the socket server used to receive and send messages
     */
    public SocketIOServer getSocketIOServer() {
        return this.socketIOServer;
    }

    /**
     * Stops the underlying socket server.
     */
    @Override
    public void shutdown() {
        this.socketIOServer.stop();
    }

    /**
     * Creates a {@link XatkitSession} from the provided {@code channel}.
     * <p>
     * This method ensures that the same {@link XatkitSession} is returned for the same {@code channel}.
     *
     * @param userId the channel to create a {@link XatkitSession} from
     * @return the created {@link XatkitSession}
    */
    public XatkitSession createSessionFromUserId(String userId) {
        return this.xatkitCore.getOrCreateXatkitSession(userId);
    }
   
}
