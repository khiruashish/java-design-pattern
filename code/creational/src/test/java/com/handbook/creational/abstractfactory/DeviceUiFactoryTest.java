package com.handbook.creational.abstractfactory;

import com.handbook.creational.abstractfactory.streaming.DeviceType;
import com.handbook.creational.abstractfactory.streaming.DeviceUiFactory;
import com.handbook.creational.abstractfactory.streaming.MobileUiFactory;
import com.handbook.creational.abstractfactory.streaming.PlaybackScreen;
import com.handbook.creational.abstractfactory.streaming.SmartTvUiFactory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DeviceUiFactoryTest {

    @Test
    void picksFactoryByDevice() {
        assertThat(DeviceUiFactory.forDevice(DeviceType.MOBILE)).isInstanceOf(MobileUiFactory.class);
        assertThat(DeviceUiFactory.forDevice(DeviceType.SMART_TV)).isInstanceOf(SmartTvUiFactory.class);
    }

    @Test
    void playerAndControlsAlwaysMatch() {
        String tv = new PlaybackScreen(new SmartTvUiFactory()).render("X");
        assertThat(tv).contains("4K HDR", "D-pad").doesNotContain("swipe");
    }
}
