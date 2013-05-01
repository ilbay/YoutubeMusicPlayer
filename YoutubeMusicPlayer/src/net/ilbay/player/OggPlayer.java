package net.ilbay.player;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.tritonus.share.sampled.file.TAudioFileFormat;


public class OggPlayer implements Player{
	
	public static void main(String args[]){
		OggPlayer oggPlayer=new OggPlayer();
		oggPlayer.initialize("/home/ilbay/git/YoutubeMusicPlayer/YoutubeMusicPlayer/music/tugay1.ogg");
		System.out.println(oggPlayer.getTotalDuration());
		oggPlayer.play();
		try {
			Thread.sleep(1500);
			oggPlayer.setVolume(100f);
			oggPlayer.pause();
			Thread.sleep(5000);
			oggPlayer.resume();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void initialize(String filename){
		this.filename=filename;
		stop();
		
		try{
			File file=new File(filename);
			AudioFileFormat baseFileFormat = AudioSystem.getAudioFileFormat(file);
			if (baseFileFormat instanceof TAudioFileFormat){
				Map props = ((TAudioFileFormat)baseFileFormat).properties();
				totalDuration = (long) Math.ceil((((Long)props.get("duration")).longValue())/1000000);
			}
		}catch(IOException ex){
			ex.printStackTrace();
		}catch(UnsupportedAudioFileException ex){
			ex.printStackTrace();
		}
	}
	
	@Override
	public void play(){
		if(oggPlayerThread!=null){
			oggPlayerThread.resumePlayer();
		}else{
			oggPlayerThread=new OggPlayerThread(filename);
			oggPlayerThread.start();
		}
	}

	@Override
	public void pause(){
		if(oggPlayerThread!=null)
			oggPlayerThread.pausePlayer();
	}

	@Override
	public void stop() {
		if(oggPlayerThread!=null)
			oggPlayerThread.stopPlayer();
		oggPlayerThread=null;
	}

	@Override
	public void resume(){
		if(oggPlayerThread!=null)
			oggPlayerThread.resumePlayer();
	}
	
	@Override
	public long getTotalDuration(){
		return totalDuration;
	}

	public void setVolume(float volume){
		this.volume=volume>100.0f ? 100.0f : (volume<0 ? 0 : volume);
		oggPlayerThread.setVolume(this.volume);
	}
	
	@Override
	public long getCurrentDuration() {
		return (long)Math.ceil(oggPlayerThread.getCurrentDuration()/1000000);
	}
	
	@Override
	public float getVolume(){
		return volume;
	}
	
	private OggPlayerThread oggPlayerThread=null;
	private String filename;
	private long totalDuration;
	private float volume=100f;
	
	private class OggPlayerThread extends Thread{
		
		public OggPlayerThread(String filename){
			this.filename=filename;
		}
		
		public void run(){
			try{
				File file = new File(filename);

				// Get AudioInputStream from given file.	
				AudioInputStream in= AudioSystem.getAudioInputStream(file);
				AudioInputStream din = null;
				if (in != null)
				{
					AudioFormat baseFormat = in.getFormat();
					AudioFormat  decodedFormat = new AudioFormat(
							AudioFormat.Encoding.PCM_SIGNED,
							baseFormat.getSampleRate(),
							16,
							baseFormat.getChannels(),
							baseFormat.getChannels() * 2,
							baseFormat.getSampleRate(),
							false);
					
					// Get AudioInputStream that will be decoded by underlying VorbisSPI
					din = AudioSystem.getAudioInputStream(decodedFormat, in);
					// Play now !
					rawplay(decodedFormat, din);
					in.close();
				}
			}catch(IOException ex){
				ex.printStackTrace();
			}catch(LineUnavailableException ex){
				ex.printStackTrace();
			}catch(UnsupportedAudioFileException ex){
				ex.printStackTrace();
			}
		}
		
		public long getCurrentDuration(){
			synchronized (currentDurationLock) {
				return currentDuration;
			}
		}
		
		public void pausePlayer(){
			synchronized (isPausedLock) {
				isPaused=true;
			}
		}
		
		public void resumePlayer(){
			synchronized (isPausedLock) {
				isPaused=false;
			}
		}
		
		public void stopPlayer(){
			synchronized (isStoppedLock) {
				isStopped=true;
			}
		}
		
		public void setVolume(float volume){
			if (volume == 0) {
				volumeControl.setValue(volumeControl.getMinimum());
			} else {                                        
				float minimum = volumeControl.getMinimum();
				float maximum = volumeControl.getMaximum();

				double db = Math.log10(volume) * 20; //Map linear volume to logarithmic dB scale

				volumeControl.setValue(Math.max(minimum, Math.min(maximum, (float)db)));
			}
		}
		
		private void rawplay(AudioFormat targetFormat,AudioInputStream din) throws IOException, LineUnavailableException
		{
			byte[] data = new byte[4096];
			SourceDataLine line = getLine(targetFormat);
			if (line != null)
			{
				// Start
				line.start();
				volumeControl=(FloatControl)line.getControl(FloatControl.Type.MASTER_GAIN);
				setVolume(volume);
				int nBytesRead = 0, nBytesWritten = 0;
				while (nBytesRead != -1)
				{
					synchronized (isPausedLock) {
						if(isPaused) continue;
					}
					
					synchronized (isStoppedLock) {
						if(isStopped) break;
					}
					
					nBytesRead = din.read(data, 0, data.length);
					if (nBytesRead != -1) nBytesWritten = line.write(data, 0, nBytesRead);
					
					synchronized (currentDurationLock) {
						currentDuration=line.getMicrosecondPosition();
					}
				}
				// Stop
				line.drain();
				line.stop();
				line.close();
				din.close();
			}
		}

		private SourceDataLine getLine(AudioFormat audioFormat) throws LineUnavailableException
		{
			SourceDataLine res = null;
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
			res = (SourceDataLine) AudioSystem.getLine(info);
			res.open(audioFormat);
			return res;
		}
		
		private String filename;
		private long currentDuration;
		private boolean isPaused;
		private boolean isStopped;
		private FloatControl volumeControl;
		private Object currentDurationLock=new Object();
		private Object isPausedLock=new Object();
		private Object isStoppedLock=new Object();
	}
}