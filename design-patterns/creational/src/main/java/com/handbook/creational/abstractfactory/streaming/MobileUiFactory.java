package com.handbook.creational.abstractfactory.streaming;

public class MobileUiFactory implements DeviceUiFactory {

    @Override
    public VideoPlayer videoPlayer() {
        return title -> "Playing '%s' at 720p, adaptive bitrate for 4G".formatted(title);
    }

    @Override
    public ControlScheme controls() {
        return () -> "swipe=seek, double-tap=skip 10s, pinch=zoom";
    }
}
