package com.xatkit.plugins.slack.platform.io;

import com.xatkit.core.session.XatkitSession;
import com.xatkit.intent.EventDefinition;
import com.xatkit.intent.IntentFactory;
import com.xatkit.plugins.slack.SlackUtils;
import com.xatkit.plugins.slack.platform.SlackPlatform;
import com.xatkit.plugins.slack.platform.SlackTest;
import com.xatkit.stubs.StubXatkitCore;
import org.apache.commons.configuration2.BaseConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.text.MessageFormat;
import java.util.Map;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;

public class SlackIntentProviderTest extends SlackTest {

    private SlackIntentProvider slackIntentProvider;

    private StubXatkitCore stubXatkitCore;

    private SlackPlatform slackPlatform;

    private String SLACK_CHANNEL;

    private static EventDefinition VALID_EVENT_DEFINITION;

    @BeforeClass
    public static void setUpBeforeClass() {
        VALID_EVENT_DEFINITION = IntentFactory.eINSTANCE.createIntentDefinition();
        VALID_EVENT_DEFINITION.setName("Default Welcome Intent");
    }

    @Before
    public void setUp() {
        stubXatkitCore = new StubXatkitCore();
        Configuration configuration = new BaseConfiguration();
        configuration.addProperty(SlackUtils.SLACK_TOKEN_KEY, getSlackToken());
        slackPlatform = new SlackPlatform(stubXatkitCore, configuration);
        SLACK_CHANNEL = slackPlatform.getChannelId("général");
    }

    @After
    public void tearDown() {
        if (nonNull(slackIntentProvider)) {
            slackIntentProvider.close();
        }
        if (nonNull(slackPlatform)) {
            slackPlatform.shutdown();
        }
        if (nonNull(stubXatkitCore)) {
            stubXatkitCore.shutdown();
        }
    }

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test(expected = NullPointerException.class)
    public void constructNullXatkitCore() {
        slackIntentProvider = new SlackIntentProvider(null, new BaseConfiguration());
    }

    @Test(expected = NullPointerException.class)
    public void constructNullConfiguration() {
        slackIntentProvider = new SlackIntentProvider(slackPlatform, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructNoTokenConfiguration() {
        slackIntentProvider = new SlackIntentProvider(slackPlatform, new BaseConfiguration());
    }

    @Test
    public void constructValidConfiguration() {
        Configuration configuration = new BaseConfiguration();
        configuration.addProperty(SlackUtils.SLACK_TOKEN_KEY, getSlackToken());
        slackIntentProvider = new SlackIntentProvider(slackPlatform, configuration);
        assertThat(slackIntentProvider.getRtmClient()).as("Not null RTM client").isNotNull();
    }

    @Test
    public void sendValidSlackMessage() {
        slackIntentProvider = getValidSlackInputProvider();
        slackIntentProvider.getRtmClient().onMessage(getValidMessage());
        assertThat(stubXatkitCore.getHandledEvents()).as("Event handled").hasSize(1);
        /*
         * Check equality on names, equals() should not be redefined for EObjects.
         */
        softly.assertThat(stubXatkitCore.getHandledEvents().get(0).getName()).as("Valid Event handled").isEqualTo
                (VALID_EVENT_DEFINITION.getName());
        XatkitSession session = stubXatkitCore.getXatkitSession(SLACK_CHANNEL);
        assertThat(session).as("Not null session").isNotNull();
        Map<String, Object> slackContext =
                session.getRuntimeContexts().getContextVariables(SlackUtils.SLACK_CONTEXT_KEY);
        assertThat(slackContext).as("Not null slack context").isNotNull();
        softly.assertThat(slackContext).as("Not empty slack context").isNotEmpty();
        Object contextChannel = slackContext.get(SlackUtils.CHAT_CHANNEL_CONTEXT_KEY);
        assertThat(contextChannel).as("Not null channel context variable").isNotNull();
        softly.assertThat(contextChannel).as("Channel context variable is a String").isInstanceOf(String.class);
        softly.assertThat(contextChannel).as("Valid channel context variable").isEqualTo(SLACK_CHANNEL);
        Object contextUsername = slackContext.get(SlackUtils.CHAT_USERNAME_CONTEXT_KEY);
        assertThat(contextUsername).as("Not null username context variable").isNotNull();
        softly.assertThat(contextUsername).as("Username context variable is a String").isInstanceOf(String.class);
        softly.assertThat(contextUsername).as("Valid context username variable").isEqualTo("gwendal");
    }

    @Test
    public void sendMentionGroupChannelListenToMentionProperty() {
        Configuration configuration = getValidSlackIntentProviderConfiguration();
        configuration.addProperty(SlackUtils.LISTEN_MENTIONS_ON_GROUP_CHANNELS_KEY, true);
        slackIntentProvider = new SlackIntentProvider(slackPlatform, configuration);
        slackIntentProvider.getRtmClient().onMessage(getValidMessageMention());
        assertThat(stubXatkitCore.getHandledEvents()).as("Event handled").hasSize(1);
        XatkitSession session = stubXatkitCore.getXatkitSession(SLACK_CHANNEL);
        String rawMessage = (String) session.getRuntimeContexts().getContextValue(SlackUtils.SLACK_CONTEXT_KEY,
                SlackUtils.CHAT_RAW_MESSAGE_CONTEXT_KEY);
        assertThat(rawMessage).as("Filtered mention").doesNotContain("<@" + slackIntentProvider.getSelfId() + ">");
    }

    @Test
    public void sendNoMentionGroupChannelListenToMentionProperty() {
        Configuration configuration = getValidSlackIntentProviderConfiguration();
        configuration.addProperty(SlackUtils.LISTEN_MENTIONS_ON_GROUP_CHANNELS_KEY, true);
        slackIntentProvider = new SlackIntentProvider(slackPlatform, configuration);
        slackIntentProvider.getRtmClient().onMessage(getValidMessage());
        assertThat(stubXatkitCore.getHandledEvents()).as("No event handled").isEmpty();
    }

    @Test
    public void sendSlackMessageInvalidType() {
        slackIntentProvider = getValidSlackInputProvider();
        slackIntentProvider.getRtmClient().onMessage(getMessageInvalidType());
        assertThat(stubXatkitCore.getHandledEvents()).as("Empty handled events").isEmpty();
        assertThat(stubXatkitCore.getXatkitSession(SLACK_CHANNEL)).as("Null session").isNull();
    }

    @Test
    public void sendSlackMessageNullText() {
        slackIntentProvider = getValidSlackInputProvider();
        slackIntentProvider.getRtmClient().onMessage(getMessageNullText());
        assertThat(stubXatkitCore.getHandledEvents()).as("Empty handled events").isEmpty();
        assertThat(stubXatkitCore.getXatkitSession(SLACK_CHANNEL)).as("Null session").isNull();
    }

    @Test
    public void sendSlackMessageNullChannel() {
        slackIntentProvider = getValidSlackInputProvider();
        slackIntentProvider.getRtmClient().onMessage(getMessageNullChannel());
        assertThat(stubXatkitCore.getHandledEvents()).as("Empty handled events").isEmpty();
        assertThat(stubXatkitCore.getXatkitSession(SLACK_CHANNEL)).as("Null session").isNull();
    }

    @Test
    public void sendSlackMessageNullUser() {
        slackIntentProvider = getValidSlackInputProvider();
        slackIntentProvider.getRtmClient().onMessage(getMessageNullUser());
        assertThat(stubXatkitCore.getHandledEvents()).as("Empty handled events").isEmpty();
        assertThat(stubXatkitCore.getXatkitSession(SLACK_CHANNEL)).as("Null session").isNull();
    }

    @Test
    public void sendSlackMessageEmptyMessage() {
        slackIntentProvider = getValidSlackInputProvider();
        slackIntentProvider.getRtmClient().onMessage(getMessageEmptyText());
        assertThat(stubXatkitCore.getHandledEvents()).as("Empty handled events").isEmpty();
        assertThat(stubXatkitCore.getXatkitSession(SLACK_CHANNEL)).as("Null session").isNull();
    }

    private SlackIntentProvider getValidSlackInputProvider() {
        Configuration configuration = getValidSlackIntentProviderConfiguration();
        return new SlackIntentProvider(slackPlatform, configuration);
    }

    private Configuration getValidSlackIntentProviderConfiguration() {
        Configuration configuration = new BaseConfiguration();
        configuration.addProperty(SlackUtils.SLACK_TOKEN_KEY, getSlackToken());
        return configuration;
    }

    private String getValidMessage() {
        return MessageFormat.format("'{'\"type\":\"message\",\"text\":\"hello\", \"channel\":\"{0}\", " +
                "\"user\":\"UBD4Z7SKH\"'}'", SLACK_CHANNEL);
    }

    private String getValidMessageMention() {
        String botMention = "<@" + slackIntentProvider.getSelfId() + ">";
        return MessageFormat.format("'{'\"type\":\"message\",\"text\":\"hello {0}\", \"channel\":\"{1}\", " +
                "\"user\":\"UBD4Z7SKH\"'}'", botMention, SLACK_CHANNEL);
    }

    private String getMessageInvalidType() {
        return MessageFormat.format("'{'\"type\":\"invalid\",\"text\":\"hello\", \"channel\":\"{0}\", " +
                "\"user\":\"123\"'}'", SLACK_CHANNEL);
    }

    private String getMessageNullText() {
        return MessageFormat.format("'{'\"type\":\"message\", \"channel\":\"{0}\", \"user\":\"123\"'}'",
                SLACK_CHANNEL);
    }

    private String getMessageNullChannel() {
        return "{\"type\":\"message\", \"user\":\"123\"}";
    }

    private String getMessageNullUser() {
        return "{\"type\":\"message\"}";
    }

    private String getMessageEmptyText() {
        return MessageFormat.format("'{'\"type\":\"message\",\"text\":\"\", \"channel\":\"{0}\", " +
                "\"user\":\"123\"'}'", SLACK_CHANNEL);
    }

}
