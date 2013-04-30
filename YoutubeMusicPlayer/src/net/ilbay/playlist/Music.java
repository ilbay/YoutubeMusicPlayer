package net.ilbay.playlist;

import org.apache.pivot.collections.HashMap;

public class Music{
	public Music(){
		
	}
	
	public Music(HashMap<String,String> hashMap){
		this.id=Integer.parseInt(hashMap.get("id"));
		this.videoId=hashMap.get("videoId");
		this.genre=hashMap.get("genre");
		this.artist=hashMap.get("artist");
		this.time=hashMap.get("time");
		this.title=hashMap.get("title");
	}
	
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
	
	public String getVideoId() {
		return videoId;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}
	
	public HashMap<String,String> toHashMap(){
		HashMap<String,String> hashMap=new HashMap<String,String>();
		hashMap.put("title",title);
		hashMap.put("artist",artist);
		hashMap.put("genre",genre);
		hashMap.put("time",time.isEmpty() ? "00:00" : time);
		hashMap.put("id", Integer.toString(id));
		hashMap.put("videoId", videoId);
		return hashMap;
	}

	private String title;
	private String artist;
	private String time;
	private String genre;
	private int id;
	private String videoId;
}