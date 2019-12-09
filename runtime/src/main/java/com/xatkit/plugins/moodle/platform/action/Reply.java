package com.xatkit.plugins.moodle.platform.action;

import com.xatkit.core.platform.action.RuntimeMessageAction;
import com.xatkit.core.session.RuntimeContexts;
import com.xatkit.core.session.XatkitSession;
import com.xatkit.plugins.moodle.platform.MoodlePlatform;
import com.xatkit.plugins.moodle.platform.utils.MoodleUtils;

/**
 * A {@link RuntimeMessageAction} that replies to a message using the input xatkit-moodle channel.
 * <p>
 * This action relies on the provided {@link XatkitSession} to retrieve the xatkit-moodle {@code channel} associated to
 * the user input.
 *
 * @see PostMessage
 * 
 */
public class Reply extends PostMessage {

    /**
     * Returns the xatkit-moodle channel associated to the user input.
     * <p>
     * This method searches in the provided {@link RuntimeContexts} for the value stored with the key
     * {@link MoodleUtils#MOODLE_CONTEXT_KEY}.{@link MoodleUtils#CHAT_CHANNEL_CONTEXT_KEY}. Note that if the provided
     * {@link RuntimeContexts} does not contain the requested value a {@link NullPointerException} is thrown.
     *
     * @param context the {@link RuntimeContexts} to retrieve the xatkit-moodle channel from
     * @return the xatkit-moodle channel associated to the user input
     * @throws NullPointerException     if the provided {@code context} is {@code null}, or if it does not contain the
     *                                  channel information
     * @throws IllegalArgumentException if the retrieved channel is not a {@link String}
     */
    private static String getChannel(RuntimeContexts context) {
        return "";
    }

    /**
     * Constructs a new {@link Reply} with the provided {@code reactPlatform}, {@code session}, and {@code message}.
     *
     * @param reactPlatform the {@link ReactPlatform} containing this action
     * @param session       the {@link XatkitSession} associated to this action
     * @param message       the message to post
     * @throws NullPointerException     if the provided {@code reactPlatform} or {@code session} is {@code null}
     * @throws IllegalArgumentException if the provided {@code message} is {@code null} or empty
     * @see #getChannel(RuntimeContexts)
     * @see PostMessage
     */
    
    /*
     * TODO This action was not used once nor tested.
     */    
    public Reply(MoodlePlatform moodlePlatform, XatkitSession session, String moodleEndpoint, String message) {
        super(moodlePlatform, session, moodleEndpoint, (Integer) session.getRuntimeContexts()
                .getContextValue(MoodleUtils.MOODLE_CONTEXT_KEY, MoodleUtils.CHAT_USERNAME_CONTEXT_KEY), message);
    }

}
