package net.ilbay.ui.playlistdialog;

import java.net.URL;

import net.ilbay.listener.CategoryAdditionListener;
import net.ilbay.listener.RenamePlaylistDialogListener;

import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.Bindable;
import org.apache.pivot.collections.Map;
import org.apache.pivot.util.Resources;
import org.apache.pivot.wtk.Button;
import org.apache.pivot.wtk.ButtonPressListener;
import org.apache.pivot.wtk.Dialog;
import org.apache.pivot.wtk.PushButton;
import org.apache.pivot.wtk.TextInput;
import org.apache.pivot.wtk.Window;

public class RenamePlaylistDialog extends Dialog implements Bindable{
	@Override
	public void initialize(Map<String, Object> arg0, URL arg1, Resources arg2) {
		okButton.getButtonPressListeners().add(new ButtonPressListener() {			
			@Override
			public void buttonPressed(Button arg0){
				String playlist=playlistTextInput.getText();
				playlistTextInput.setText("");
				if(listener!=null)
					listener.playlistChanged(playlist);
				RenamePlaylistDialog.this.close();
			}
		});

	}
	
	public void open(Window window,String oldPlaylist){
		playlistTextInput.setText(oldPlaylist);
		super.open(window);
	}
	
	public void addRenamePlaylistDialogListener(RenamePlaylistDialogListener listener){
		this.listener=listener;
	}
	
	private @BXML TextInput playlistTextInput;
	private @BXML PushButton okButton;
	
	private RenamePlaylistDialogListener listener;
}