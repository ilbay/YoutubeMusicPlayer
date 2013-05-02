package net.ilbay.listener;

public interface PlayingMusicListener {
	public void finished();
	public void currentTime(long microseconds);
}