package net.ilbay.player;

import net.ilbay.listener.PlayingMusicListener;

public interface Player {
	public void initialize(String filename);
	public void play();
	public void pause();
	public void stop();
	public void resume();
	public void setVolume(float volume);
	public float getVolume();
	public long getTotalDuration();
	public long getCurrentDuration();
	public void addPlayingMusicListener(PlayingMusicListener playingMusicListener);
}
