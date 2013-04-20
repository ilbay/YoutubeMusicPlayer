package net.ilbay.ui;

import java.net.URL;

import net.ilbay.downloader.Downloader;
import net.ilbay.downloader.VideoInfo;
import net.ilbay.downloader.YoutubeVideoDownloader;

import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.Bindable;
import org.apache.pivot.collections.Map;
import org.apache.pivot.util.Resources;
import org.apache.pivot.util.concurrent.Task;
import org.apache.pivot.util.concurrent.TaskExecutionException;
import org.apache.pivot.wtk.ActivityIndicator;
import org.apache.pivot.wtk.ApplicationContext;
import org.apache.pivot.wtk.Border;
import org.apache.pivot.wtk.Button;
import org.apache.pivot.wtk.ButtonPressListener;
import org.apache.pivot.wtk.Dialog;
import org.apache.pivot.wtk.PushButton;
import org.apache.pivot.wtk.TextInput;
import org.apache.pivot.wtk.TaskAdapter;
import org.apache.pivot.util.concurrent.TaskListener;

public class ImportOnlineMediaDialog extends Dialog implements Bindable{
	@Override
	public void initialize(Map<String, Object> arg0, URL arg1, Resources arg2) {
		musicInfoBorder.setVisible(false);
		
		loadMusicInfoButton.getButtonPressListeners().add(new ButtonPressListener() {
			@Override
			public void buttonPressed(Button arg0) {
				final String videoId=videoIdTextInput.getText();
				if(!videoId.isEmpty()){
					changeInputState(false);
					activityIndicator.setActive(true);
					
					Task<String> downloadTask=new Task<String>(){
						@Override
						public String execute() throws TaskExecutionException {
							Downloader downloader=new YoutubeVideoDownloader(videoId);
							VideoInfo videoInfo=downloader.getVideoInfo();
							return videoInfo.getVideoTitle();
						}
					};
					
					
					TaskListener<String> taskListener=new TaskListener<String>(){
						@Override
						public void executeFailed(Task<String> arg0) {
							activityIndicator.setActive(false);
							changeInputState(true);
							System.out.println(arg0.getFault());
						}

						@Override
						public void taskExecuted(Task<String> arg0) {
							changeInputState(true);
							titleTextInput.setText(arg0.getResult());
							activityIndicator.setActive(false);
							musicInfoBorder.setVisible(true);
						}

					};
					downloadTask.execute(new TaskAdapter<String>(taskListener));
				}
			}
		});
		
		saveButton.getButtonPressListeners().add(new ButtonPressListener() {
			@Override
			public void buttonPressed(Button arg0){
				Downloader downloader=new YoutubeVideoDownloader(titleTextInput.getText());
				downloader.saveVideo();
			}
		});
	}

	private void changeInputState(boolean isEnabled){
		videoIdTextInput.setEnabled(isEnabled);
		loadMusicInfoButton.setEnabled(isEnabled);
		titleTextInput.setEnabled(isEnabled);
		artistTextInput.setEnabled(isEnabled);
		genreTextInput.setEnabled(isEnabled);
		saveButton.setEnabled(isEnabled);
	}
		
	private @BXML PushButton saveButton;
	private @BXML PushButton loadMusicInfoButton;
	
	private @BXML TextInput videoIdTextInput;
	private @BXML TextInput titleTextInput;
	private @BXML TextInput artistTextInput;
	private @BXML TextInput genreTextInput;
	
	private @BXML ActivityIndicator activityIndicator;
	
	private @BXML Border musicInfoBorder;
}