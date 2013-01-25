package net.ilbay.ui;

import java.net.URL;

import net.ilbay.listener.ConfirmationDialogListener;

import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.Bindable;
import org.apache.pivot.collections.Map;
import org.apache.pivot.util.Resources;
import org.apache.pivot.wtk.Button;
import org.apache.pivot.wtk.ButtonPressListener;
import org.apache.pivot.wtk.Dialog;
import org.apache.pivot.wtk.Label;
import org.apache.pivot.wtk.PushButton;
import org.apache.pivot.wtk.Window;

public class ConfirmationDialog extends Dialog implements Bindable{

	@Override
	public void initialize(Map<String, Object> arg0, URL arg1, Resources arg2){
		okButton.getButtonPressListeners().add(new ButtonPressListener() {
			@Override
			public void buttonPressed(Button arg0){
				if(listener!=null)
					listener.confirmed();
				listener=null;
				ConfirmationDialog.this.close();
			}
		});
		
		cancelButton.getButtonPressListeners().add(new ButtonPressListener() {
			@Override
			public void buttonPressed(Button arg0) {
				ConfirmationDialog.this.close();
			}
		});
	}
	
	public void open(Window ownerArgument,String text,ConfirmationDialogListener listener){
		this.listener=listener;
		content.setText(text);
		super.open(ownerArgument);
	}
	
	private @BXML Label content;
	private @BXML PushButton okButton;
	private @BXML PushButton cancelButton;
	private ConfirmationDialogListener listener;
}