package net.ilbay.ui;

import java.net.URL;

import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.Bindable;
import org.apache.pivot.collections.Map;
import org.apache.pivot.util.Resources;
import org.apache.pivot.wtk.ActivityIndicator;
import org.apache.pivot.wtk.Border;
import org.apache.pivot.wtk.Button;
import org.apache.pivot.wtk.ButtonPressListener;
import org.apache.pivot.wtk.Dialog;
import org.apache.pivot.wtk.PushButton;
import org.apache.pivot.wtk.TextInput;

public class ImportOnlineMediaDialog extends Dialog implements Bindable{
	@Override
	public void initialize(Map<String, Object> arg0, URL arg1, Resources arg2) {
		musicInfoBorder.setVisible(false);
		
		loadMusicInfoButton.getButtonPressListeners().add(new ButtonPressListener() {
			@Override
			public void buttonPressed(Button arg0) {
				changeInputState(false);
				activityIndicator.setActive(true);
			}
		});
		
		okButton.getButtonPressListeners().add(new ButtonPressListener() {
			@Override
			public void buttonPressed(Button arg0) {
				
			}
		});
	}

	private void changeInputState(boolean isEnabled){
		videoIdTextInput.setEnabled(isEnabled);
		loadMusicInfoButton.setEnabled(isEnabled);
		titleTextInput.setEnabled(isEnabled);
		artistTextInput.setEnabled(isEnabled);
		genreTextInput.setEnabled(isEnabled);
		okButton.setEnabled(isEnabled);
	}
	
	@BXML PushButton okButton;
	@BXML PushButton loadMusicInfoButton;
	
	@BXML TextInput videoIdTextInput;
	@BXML TextInput titleTextInput;
	@BXML TextInput artistTextInput;
	@BXML TextInput genreTextInput;
	
	@BXML ActivityIndicator activityIndicator;
	
	@BXML Border musicInfoBorder;
}