package net.ilbay.converter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Converter {
	
	public static void main(String args[]){
		Converter.convertToOgg("/home/ilbay/git/YoutubeMusicPlayer/YoutubeMusicPlayer/music/B2iF5yYzs-4.mp4");
	}
	
	public static void convertToOgg(String sourceFile){
		try{
			String destFilename=sourceFile.split("\\.")[0]+".ogg";
			String command=String.format("-I dummy %s --sout=\"#transcode{acodec=vorb}:standard{access=file,mux=ogg,dst=%s}\" vlc://quit", sourceFile,destFilename);

			Process p=new ProcessBuilder("vlc","-I","dummy",sourceFile,"--sout","#transcode{acodec=vorb}:standard{access=file,mux=ogg,dst=/home/ilbay/Desktop/aa.ogg}","vlc://quit").start();
			p.waitFor();
		}catch(IOException ex){
			ex.printStackTrace();
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}