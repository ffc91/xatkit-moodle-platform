package com.xatkit.plugins.moodle.platform.io;

import com.xatkit.plugins.moodle.platform.MoodlePlatform;
import org.apache.commons.configuration2.Configuration;

/**
 * A generic Moodle {@link com.xatkit.plugins.chat.platform.io.ChatIntentProvider}.
 * <p>
 * This class wraps the {@link MoodleIntentProvider} and allows to use it as a generic <i>ChatIntentProvider</i> from
 * the <i>ChatPlatform</i>.
 *
 * @see MoodleIntentProvider
 */
public class ChatProvider extends MoodleIntentProvider {

    /**
     * Constructs a new {@link ChatProvider} from the provided {@code runtimePlatform} and {@code configuration}.
     *
     * @param runtimePlatform the {@link MoodlePlatform} containing this {@link ChatProvider}
     * @param configuration   the {@link Configuration} used to initialize the {@link MoodlePlatform}
     */
    public ChatProvider(MoodlePlatform runtimePlatform, Configuration configuration) {
        super(runtimePlatform, configuration);
    }
}
