package net.ilbay.util;

import java.io.IOException;

public class Converter {
	
	public static void main(String args[]){
		Converter.convertToOgg("/home/ilbay/git/YoutubeMusicPlayer/YoutubeMusicPlayer/music/B2iF5yYzs-4.mp4");
	}
	
	public static String convertToOgg(String sourceFile){
		try{
			String destFilename=sourceFile.split("\\.")[0]+".ogg";

			Process p=new ProcessBuilder("vlc","-I","dummy",sourceFile,"--sout",String.format("#transcode{acodec=vorb}:standard{access=file,mux=ogg,dst=%s}",destFilename),"vlc://quit").start();
			p.waitFor();
			
			return destFilename;
		}catch(IOException ex){
			ex.printStackTrace();
		}catch(InterruptedException ex){
			ex.printStackTrace();
		}
		return null;
	}
}