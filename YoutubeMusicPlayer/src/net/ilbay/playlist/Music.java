package net.ilbay.playlist;

public class Music{
	public String getTitle(){
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getArtist() {
		return artist;
	}
	
	public void setArtist(String artist) {
		this.artist = artist;
	}
	
	public String getTime() {
		return time;
	}
	
	public void setTime(String time) {
		this.time = time;
	}
	
	public String getGenre() {
		return genre;
	}
	
	public void setGenre(String genre) {
		this.genre = genre;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	private String title;
	private String artist;
	private String time;
	private String genre;
	private int id;
}