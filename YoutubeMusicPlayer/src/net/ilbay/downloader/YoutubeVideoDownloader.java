package net.ilbay.downloader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.net.URLEncoder;
import java.net.URLDecoder;

import net.ilbay.listener.PlayingMusicListener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

public class YoutubeVideoDownloader implements Downloader{
	public static void main(String[] args){
		YoutubeVideoDownloader yvd=new YoutubeVideoDownloader("vYV-XJdzupY");
		System.out.println(yvd.getVideoInfo().getVideoTitle());
		//yvd.saveVideo();
	}
	
	public YoutubeVideoDownloader(String videoId){
		this.videoId=videoId;
	}
	
	public VideoInfo getVideoInfo(){
		VideoInfo resultVideoInfo=new VideoInfo();
		resultVideoInfo.setVideoId(videoId);
		
		try {
			URI uri=createURL(videoId);
			resultVideoInfo.setVideoUrl(uri.toString());
			
			CookieStore cookieStore = new BasicCookieStore();
			HttpContext localContext = new BasicHttpContext();
			localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
			
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(uri);
			httpget.setHeader("User-Agent", userAgent);

			HttpResponse response = httpclient.execute(httpget, localContext);
			HttpEntity entity = response.getEntity();
			if (entity != null && response.getStatusLine().getStatusCode() == 200) {
				InputStream instream = entity.getContent();
				List<NameValuePair> infoMap=new ArrayList<NameValuePair>();
				String videoInfo = getStringFromInputStream("UTF-8", instream);
				URLEncodedUtils.parse(infoMap, new Scanner(videoInfo), "UTF-8");
				String videoRealUrl="",signature="";

				for(NameValuePair pair:infoMap){
					if(pair.getName().equals("title")){
						resultVideoInfo.setVideoTitle(pair.getValue());
					}else if(pair.getName().equals("url_encoded_fmt_stream_map")){
						String fmt_stream_map=pair.getValue();
						String videoUrlList[]=fmt_stream_map.split(",");
						boolean isTargetExtFileFound=false;
						for(String videoUrl:videoUrlList){
							List<NameValuePair> fmt_stream_info=new ArrayList<NameValuePair>();
							URLEncodedUtils.parse(fmt_stream_info,new Scanner(videoUrl),"UTF-8");
							for(NameValuePair innerPair:fmt_stream_info){
								if(innerPair.getName().equals("url")){
									videoRealUrl=innerPair.getValue();
								}else if(innerPair.getName().equals("sig")){
									signature=innerPair.getValue();
								}else if(innerPair.getName().equals("itag") && innerPair.getValue().equals(format)){
									isTargetExtFileFound=true;
								}
							}
							if(isTargetExtFileFound)
								break;
						}
						resultVideoInfo.setVideoDownloadUrl(URLDecoder.decode(videoRealUrl, "UTF-8")+"&signature="+signature);
					}
				}
			}
		} catch (UnsupportedEncodingException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(ClientProtocolException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
		
		this.videoInfo=resultVideoInfo;
		
		return resultVideoInfo;
	}
	
	public String saveVideo(){
		try{
			CookieStore cookieStore = new BasicCookieStore();
			HttpContext localContext = new BasicHttpContext();
			localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
			
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(new URI(videoInfo.getVideoDownloadUrl()));
			httpget.setHeader("User-Agent", userAgent);
			
			HttpResponse response = httpclient.execute(httpget, localContext);
			HttpEntity entity = response.getEntity();
			
			if (entity != null && response.getStatusLine().getStatusCode() == 200) {
				InputStream instream = entity.getContent();
				
				File musicFolder=new File("music");
				if(!musicFolder.exists())
					musicFolder.mkdir();
				
				File file=new File("music/"+videoInfo.getVideoId()+".mp4");
				if(file.exists())
					file.delete();

				FileOutputStream fileOutputStream=new FileOutputStream(file);
				
				byte buffer[]=new byte[1024];
				int n;
				while((n=instream.read(buffer))!=-1){
					fileOutputStream.write(buffer,0,n);
				}
				
				fileOutputStream.flush();
				fileOutputStream.close();
				
				return file.toString();
			}
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(URISyntaxException e){
			e.printStackTrace();
		}catch(ClientProtocolException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
		
		return null;
	}
	
	private URI createURL(String videoId) throws URISyntaxException, UnsupportedEncodingException{
		return new URI(scheme, host, "/get_video_info","video_id="+videoId, null);
	}
	
	private String getStringFromInputStream(String encoding,InputStream instream){
		Writer writer=new StringWriter();
		char buffer[]=new char[1024];
		try{
			BufferedReader reader=new BufferedReader(new InputStreamReader(instream,encoding));
			int n;
			while((n=reader.read(buffer))!=-1){
				writer.write(buffer,0,n);
			}
		}catch(Exception e){
			return null;
		}finally{
			try{
				instream.close();
			}catch(IOException e){
				return null;
			}
		}
		return writer.toString();
	}
	
	private final static String host="www.youtube.com";
	private final static String format="18";
	private final static String scheme="http";
	private final static String userAgent="Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.2.13) Gecko/20101203 Firefox/3.6.13";
	
	private String videoId;
	private VideoInfo videoInfo;
}