package net.ilbay.downloader;

public class VideoInfo {
	public VideoInfo(){
		
	}
	
	public VideoInfo(String videoId,String videoUrl,String videoDownloadUrl){
		this.videoId=videoId;
		this.videoUrl=videoUrl;
		this.videoDownloadUrl=videoDownloadUrl;
	}
	
	public String getVideoId() {
		return videoId;
	}
	
	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}
	
	public String getVideoUrl() {
		return videoUrl;
	}
	
	public String getVideoTitle(){
		return videoTitle;
	}
	
	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}
	
	public String getVideoDownloadUrl() {
		return videoDownloadUrl;
	}
	
	public void setVideoDownloadUrl(String videoDownloadUrl) {
		this.videoDownloadUrl = videoDownloadUrl;
	}

	public void setVideoTitle(String videoTitle){
		this.videoTitle=videoTitle;
	}
	
	private String videoId;
	private String videoUrl;
	private String videoDownloadUrl;
	private String videoTitle;
}