package com.handbook.creational.abstractfactory.streaming;

public class SmartTvUiFactory implements DeviceUiFactory {

    @Override
    public VideoPlayer videoPlayer() {
        return title -> "Playing '%s' in 4K HDR with Dolby Atmos".formatted(title);
    }

    @Override
    public ControlScheme controls() {
        return () -> "D-pad=navigate, OK=play/pause, long-press=menu";
    }
}
