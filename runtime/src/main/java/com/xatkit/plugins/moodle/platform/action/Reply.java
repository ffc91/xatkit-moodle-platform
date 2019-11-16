package com.xatkit.plugins.moodle.platform.action;

import com.xatkit.core.platform.action.RuntimeMessageAction;
import com.xatkit.core.session.RuntimeContexts;
import com.xatkit.core.session.XatkitSession;
import com.xatkit.plugins.chat.ChatUtils;
import com.xatkit.plugins.moodle.platform.MoodlePlatform;
import com.xatkit.plugins.moodle.platform.utils.MoodleUtils;

import static fr.inria.atlanmod.commons.Preconditions.checkArgument;
import static fr.inria.atlanmod.commons.Preconditions.checkNotNull;

/**
 * A {@link RuntimeMessageAction} that replies to a message using the input xatkit-moodle channel.
 * <p>
 * This action relies on the provided {@link XatkitSession} to retrieve the xatkit-moodle {@code channel} associated
 * to the user input.
 *
 * @see PostMessage
 */
public class Reply extends PostMessage {

    private static String getChannel(RuntimeContexts context) {
        return "";
    }

    public Reply(MoodlePlatform moodlePlatform, XatkitSession session, String message) {
        super(moodlePlatform, session, "", 0, "");
    }

}
