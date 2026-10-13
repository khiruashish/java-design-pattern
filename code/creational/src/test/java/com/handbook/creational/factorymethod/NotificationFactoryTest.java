package com.handbook.creational.factorymethod;

import com.handbook.creational.factorymethod.notification.Channel;
import com.handbook.creational.factorymethod.notification.EmailNotification;
import com.handbook.creational.factorymethod.notification.NotificationFactory;
import com.handbook.creational.factorymethod.notification.PushNotification;
import com.handbook.creational.factorymethod.notification.SmsNotification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationFactoryTest {

    @Test
    void createsTheRightProductForEachChannel() {
        assertThat(NotificationFactory.create(Channel.EMAIL)).isInstanceOf(EmailNotification.class);
        assertThat(NotificationFactory.create(Channel.SMS)).isInstanceOf(SmsNotification.class);
        assertThat(NotificationFactory.create(Channel.PUSH)).isInstanceOf(PushNotification.class);
    }

    @ParameterizedTest
    @EnumSource(Channel.class)
    void everyChannelIsSupported(Channel channel) {
        assertThat(NotificationFactory.create(channel).send("x", "hello")).contains("hello");
    }

    @Test
    void smsTruncatesLongMessages() {
        String sent = NotificationFactory.create(Channel.SMS).send("+91", "a".repeat(100));
        assertThat(sent).endsWith("...\"");
    }
}
