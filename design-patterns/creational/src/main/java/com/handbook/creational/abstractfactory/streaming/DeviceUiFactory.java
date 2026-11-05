package com.handbook.creational.abstractfactory.streaming;

public interface DeviceUiFactory {

    VideoPlayer videoPlayer();

    ControlScheme controls();

    /** A factory method that picks the right abstract factory. */
    static DeviceUiFactory forDevice(DeviceType type) { // (1)!
        return switch (type) {
            case MOBILE -> new MobileUiFactory();
            case SMART_TV -> new SmartTvUiFactory();
        };
    }
}
