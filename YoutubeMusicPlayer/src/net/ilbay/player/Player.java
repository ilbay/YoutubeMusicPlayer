package net.ilbay.player;

public interface Player {
	public void play();
	public void pause();
	public void stop();
	public void resume();
	public void setVolume(float volume);
	public float getVolume();
	public long getTotalDuration();
	public long getCurrentDuration();
}
