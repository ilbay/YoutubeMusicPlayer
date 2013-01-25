package net.ilbay.ui;

import java.net.MalformedURLException;
import java.net.URL;

import net.ilbay.listener.CategoryAdditionListener;
import net.ilbay.listener.ConfirmationDialogListener;
import net.ilbay.listener.RenamePlaylistDialogListener;
import net.ilbay.playlist.Playlist;

import org.apache.pivot.wtk.Action;
import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.BXMLSerializer;
import org.apache.pivot.collections.ArrayList;
import org.apache.pivot.collections.List;
import org.apache.pivot.collections.Map;
import org.apache.pivot.util.concurrent.TaskExecutionException;
import org.apache.pivot.wtk.Alert;
import org.apache.pivot.wtk.Application;
import org.apache.pivot.wtk.ApplicationContext;
import org.apache.pivot.wtk.Border;
import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.ComponentMouseButtonListener;
import org.apache.pivot.wtk.Dialog;
import org.apache.pivot.wtk.Display;
import org.apache.pivot.wtk.ImageView;
import org.apache.pivot.wtk.LinkButton;
import org.apache.pivot.wtk.ListView;
import org.apache.pivot.wtk.Menu;
import org.apache.pivot.wtk.MenuBar;
import org.apache.pivot.wtk.MenuHandler;
import org.apache.pivot.wtk.Mouse.Button;
import org.apache.pivot.wtk.Prompt;
import org.apache.pivot.wtk.content.ButtonData;
import org.apache.pivot.wtk.media.Image;
import org.apache.pivot.wtk.MovieView;
import org.apache.pivot.wtk.Window;
import org.apache.pivot.wtk.Label;
import org.apache.pivot.xml.Element;

import player.*;

public class YoutubeMusicPlayer implements Application{
	public static final String APPLICATION_KEY = "application";
	
	@Override
	public void resume() throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean shutdown(boolean arg0) throws Exception {
		// TODO Auto-generated method stub
		if(window!=null){
			window.close();
		}
		Playlist.disconnectFromDatabase();
		return false;
	}

	@Override
	public void startup(Display display, Map<String, String> arg1)
			throws Exception {
		
		createMenuActions();
		
		BXMLSerializer bxmlSerializer=new BXMLSerializer();
		bxmlSerializer.getNamespace().put(APPLICATION_KEY, this);
		
		window=(Window)bxmlSerializer.readObject(YoutubeMusicPlayer.class, "YoutubeMusicPlayer.bxml");
		bxmlSerializer.bind(this, YoutubeMusicPlayer.class);
				
		categoryList=new ArrayList<String>();
		
		buttonActions();
		loadCategories();
		createContextMenuForCategory();
		
		newPlaylistDialog.addCategoryAdditionListener(new CategoryAdditionListener() {			
			@Override
			public void categoryAdded(String category) {
				if(categoryList.indexOf(category)!=-1){
					Alert.alert(category+" has already been added.",window);
				}else{
					categoryList.add(category);
					Playlist.addPlaylist(category);
				}
			}
		});
		
		window.open(display);
		
		//playerControl=PlayerFactory.createLightweightMPEG4Player();
		//playerControl.open("/home/ilbay/workspace/YoutubeVideoPlayer/aaa.mp4");
		//playerControl.start();
	}

	@Override
	public void suspend() throws Exception {
		// TODO Auto-generated method stub
	}

	private void buttonActions(){
		playButton.getComponentMouseButtonListeners().add(new ComponentMouseButtonListener(){
			private boolean isToggled=true;
			@Override
			public boolean mouseClick(Component arg0, Button arg1, int arg2,
					int arg3, int arg4) {
				String iconURL="icon/button_grey_play.png";
				if(isToggled)
					iconURL="icon/button_grey_pause.png";
				
				isToggled=!isToggled;
				ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
				URL imageURL=classLoader.getResource(iconURL);
				Image image = (Image)ApplicationContext.getResourceCache().get(imageURL);
				if(image==null){
					try {
						image=Image.load(imageURL);
					} catch (TaskExecutionException e) {
						e.printStackTrace();
					}
				}				
				playButton.setButtonData(new ButtonData(image));
				return false;
			}

			@Override
			public boolean mouseDown(Component arg0, Button arg1, int arg2,
					int arg3) {
				return false;
			}

			@Override
			public boolean mouseUp(Component arg0, Button arg1, int arg2,
					int arg3) {
				return false;
			}
		});		
	}
	
	private void loadCategories(){
		categoryListview.setListData(categoryList);
		java.util.List<String> list=Playlist.getCategories();
		for(String category:list)
			categoryList.add(category);
	}
	
	private void createMenuActions(){
		Action.getNamedActions().put("newPlaylist", new Action(){
			@Override
			public void perform(Component arg0){
				//Alert.alert("some warning", window);
				newPlaylistDialog.open(window);
			}
		});
	}
	
	private void createContextMenuForCategory(){
		categoryListview.setMenuHandler(new MenuHandler.Adapter(){
			public boolean configureContextMenu(Component component, Menu menu, int x, int y ){
				final int selectedItemIndex=categoryListview.getItemAt(y);
				if(selectedItemIndex==-1)
					return false;
				
				Menu.Section menuSection=new Menu.Section();
				menu.getSections().add(menuSection);
				
				Menu.Item renameMenuItem=new Menu.Item("Rename");
				menuSection.add(renameMenuItem);
				renameMenuItem.setAction(new Action(){
					@Override
					public void perform(Component arg0){
						final String selectedItem=categoryList.get(selectedItemIndex);
						renamePlaylistDialog.addRenamePlaylistDialogListener(new RenamePlaylistDialogListener() {
							@Override
							public void playlistChanged(String newPlaylist) {
								Playlist.renamePlaylist(selectedItem, newPlaylist);
								categoryList.update(selectedItemIndex, newPlaylist);
							}
						});
						renamePlaylistDialog.open(window, selectedItem);
					}
				});
				
				Menu.Item deleteMenuItem=new Menu.Item("Delete");
				menuSection.add(deleteMenuItem);
				deleteMenuItem.setAction(new Action(){
					@Override
					public void perform(Component arg0) {
						final String selectedItem=categoryList.get(selectedItemIndex);
						String errorMessage="Are you sure you want to remove "+selectedItem+" playlist?";
						confirmationDialog.open(window,errorMessage,new ConfirmationDialogListener(){
							@Override
							public void confirmed(){
								categoryList.remove(selectedItem);
								Playlist.deletePlaylist(selectedItem);
							}
						});
					}
				});
				
				Menu.Item exportMenuItem=new Menu.Item("Export");
				menuSection.add(exportMenuItem);
				return false;
			}
		});
	}
	
	private Window window;
	private PlayerControl playerControl;
	private List<String> categoryList;
	
	private @BXML LinkButton playButton;
	private @BXML LinkButton stopButton;
	private @BXML LinkButton soundButton;
	
	private @BXML NewPlaylistDialog newPlaylistDialog;	
	private @BXML ConfirmationDialog confirmationDialog;
	private @BXML RenamePlaylistDialog renamePlaylistDialog;
	
	private @BXML ListView categoryListview;
}