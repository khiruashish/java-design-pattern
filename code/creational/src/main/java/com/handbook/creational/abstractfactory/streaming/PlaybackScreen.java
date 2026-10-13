package com.handbook.creational.abstractfactory.streaming;

/** Client: renders a playback screen for whatever device it was given. */
public class PlaybackScreen {

    private final VideoPlayer player;
    private final ControlScheme controls;

    public PlaybackScreen(DeviceUiFactory ui) {
        this.player = ui.videoPlayer();
        this.controls = ui.controls(); // always from the same family as the player
    }

    public String render(String title) {
        return player.play(title) + "\n    controls: " + controls.describe();
    }
}
