package net.ilbay.ui.playlistdialog;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.ilbay.listener.CategoryAdditionListener;

import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.Bindable;
import org.apache.pivot.collections.Map;
import org.apache.pivot.util.Resources;
import org.apache.pivot.wtk.Alert;
import org.apache.pivot.wtk.Button;
import org.apache.pivot.wtk.ButtonPressListener;
import org.apache.pivot.wtk.Dialog;
import org.apache.pivot.wtk.PushButton;
import org.apache.pivot.wtk.TextInput;

public class NewPlaylistDialog extends Dialog implements Bindable{
	public NewPlaylistDialog(){
		super();
		categoryAdditionListeners=new ArrayList<CategoryAdditionListener>();
	}
	
	@Override
	public void initialize(Map<String, Object> arg0, URL arg1, Resources arg2) {
		okButton.getButtonPressListeners().add(new ButtonPressListener() {			
			@Override
			public void buttonPressed(Button arg0){
				String category=playlistTextInput.getText();
				for(CategoryAdditionListener listener:categoryAdditionListeners)
					listener.categoryAdded(category);
				playlistTextInput.setText("");
				NewPlaylistDialog.this.close();
			}
		});
	}

	public void addCategoryAdditionListener(CategoryAdditionListener listener){
		categoryAdditionListeners.add(listener);
	}
	
	private @BXML PushButton okButton;
	private @BXML TextInput playlistTextInput;
	
	private List<CategoryAdditionListener> categoryAdditionListeners;
}