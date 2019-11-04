package com.xatkit.plugins.moodle.platform.utils;

import com.xatkit.core.session.RuntimeContexts;
import com.xatkit.plugins.chat.ChatUtils;

/**
 * An utility interface that holds xatkit-moodle related helpers and context keys.
 */
public interface MoodleUtils extends ChatUtils {

    /**
     * The {@link Configuration} key to store the Moodle REST API endpoint
     * 
     * @see MoodlePlatform#MoodlePlatform(XatkitCore, Configuration)
     */
    String MOODLE_SERVER_ENDPOINT_KEY = "xatkit.moodle.restApiEndpoint";

    /**
     * The {@link Configuration} key to store the Moodle app API token.
     *
     * @see MoodlePlatform#MoodlePlatform(XatkitCore, Configuration)
     */
    String MOODLE_ACCESS_TOKEN_KEY = "xatkit.moodle.accessToken";

    /**
     * The {@link Configuration} key to store the port of the
     * {@link MoodlePlatform}'s socket server.
     * 
     * @see MoodlePlatform#MoodlePlatform(XatkitCore, Configuration)
     */
    String MOODLE_SERVER_PORT_KEY = "xatkit.moodle.port";

    /**
     * The default value of the {@link #MOODLE_SERVER_PORT_KEY} {@link Configuration} key.
     */
    int DEFAULT_MOODLE_SERVER_PORT = 5002;

    /**
     * The {@link RuntimeContexts} key used to store Moodle-related information.
     */
    String MOODLE_CONTEXT_KEY = "moodle";
}
