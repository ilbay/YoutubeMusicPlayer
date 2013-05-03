package net.ilbay.ui;

import java.net.URL;

import net.ilbay.listener.CategoryAdditionListener;
import net.ilbay.listener.ConfirmationDialogListener;
import net.ilbay.listener.ImportOnlineMediaListener;
import net.ilbay.listener.PlayingMusicListener;
import net.ilbay.listener.RenamePlaylistDialogListener;
import net.ilbay.player.OggPlayer;
import net.ilbay.player.Player;
import net.ilbay.playlist.Music;
import net.ilbay.playlist.MusicDB;
import net.ilbay.playlist.Playlist;
import net.ilbay.playlist.PlaylistDB;
import net.ilbay.ui.playlistdialog.NewPlaylistDialog;
import net.ilbay.ui.playlistdialog.RenamePlaylistDialog;
import net.ilbay.util.Converter;
import net.ilbay.util.PlayerTime;

import org.apache.pivot.wtk.Action;
import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.BXMLSerializer;
import org.apache.pivot.collections.ArrayList;
import org.apache.pivot.collections.HashMap;
import org.apache.pivot.collections.List;
import org.apache.pivot.collections.Map;
import org.apache.pivot.collections.Sequence;
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
import org.apache.pivot.wtk.ListViewSelectionListener;
import org.apache.pivot.wtk.Menu;
import org.apache.pivot.wtk.MenuBar;
import org.apache.pivot.wtk.MenuHandler;
import org.apache.pivot.wtk.Mouse.Button;
import org.apache.pivot.wtk.Prompt;
import org.apache.pivot.wtk.Slider;
import org.apache.pivot.wtk.Span;
import org.apache.pivot.wtk.TableView;
import org.apache.pivot.wtk.TableViewSelectionListener;
import org.apache.pivot.wtk.content.ButtonData;
import org.apache.pivot.wtk.media.Image;
import org.apache.pivot.wtk.MovieView;
import org.apache.pivot.wtk.Window;
import org.apache.pivot.wtk.Label;
import org.apache.pivot.xml.Element;

public class YoutubeMusicPlayer implements Application,PlayingMusicListener{
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
		PlaylistDB.disconnectFromDatabase();
		MusicDB.disconnectFromDatabase();
		player.stop();
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
				
		playlistList=new ArrayList<String>();
		playlist=new HashMap<String,Playlist>();
		musicList=new HashMap<String,java.util.List<Music>>();
		player=new OggPlayer();
		player.addPlayingMusicListener(this);
		
		buttonActions();
		loadPlaylists();
		createContextMenuForCategory();
		
		newPlaylistDialog.addCategoryAdditionListener(new CategoryAdditionListener() {			
			@Override
			public void categoryAdded(String category) {
				if(playlistList.indexOf(category)!=-1){
					Alert.alert(category+" has already been added.",window);
				}else{
					playlistList.add(category);
					PlaylistDB.addPlaylist(category);
				}
			}
		});
		
		importOnlineMediaDialog.addImportOnlineMediaListener(new ImportOnlineMediaListener() {	
			@Override
			public void newMediaSaved(Music music) {
				String playlistName=playlistListView.getSelectedItem().toString();
				musicList.get(playlistName).add(music);
				List<HashMap<String,String>> list=(List<HashMap<String,String>>)tableView.getTableData();
				HashMap<String,String> map=music.toHashMap();
				map.put("no", String.valueOf(list.getLength()+1));
				list.add(map);
			}
		});
		
		playlistListView.getListViewSelectionListeners().add(new ListViewSelectionListener(){
			@Override
			public void selectedItemChanged(ListView arg0, Object arg1) {
				showCurrentMusicList();
			}

			@Override
			public void selectedRangeAdded(ListView arg0, int arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void selectedRangeRemoved(ListView arg0, int arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void selectedRangesChanged(ListView arg0, Sequence<Span> arg1) {
			}
		});
		
		tableView.getTableViewSelectionListeners().add(new TableViewSelectionListener(){
			@Override
			public void selectedRangeAdded(TableView arg0, int arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void selectedRangeRemoved(TableView arg0, int arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void selectedRangesChanged(TableView arg0,
					Sequence<Span> arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void selectedRowChanged(TableView tableView, Object arg1) {
				currentPlayingMusicIndex=tableView.getSelectedIndex();
				String playlistName=playlistListView.getSelectedItem().toString();
				currentPlayingMusic=musicList.get(playlistName).get(currentPlayingMusicIndex);
				player.initialize("music/"+currentPlayingMusic.getVideoId()+".ogg");
				play();
			}
		});
		
		window.open(display);
		
		playlistListView.setSelectedIndex(0);
		showCurrentMusicList();
	}

	@Override
	public void suspend() throws Exception {
		// TODO Auto-generated method stub
	}

	public void finished(){
		int selectedPlaylistIndex=playlistListView.getSelectedIndex();
		String selectedPlaylistName=playlistList.get(selectedPlaylistIndex);
		java.util.List<Music> musics=musicList.get(selectedPlaylistName);
		if(musics.size()>currentPlayingMusicIndex+1){
			currentPlayingMusicIndex++;
			currentPlayingMusic=musics.get(currentPlayingMusicIndex);
			//tableView.setSelectedIndex(currentPlayingMusicIndex);
			player.initialize("music/"+currentPlayingMusic.getVideoId()+".ogg");
			ApplicationContext.queueCallback(new Runnable(){
				@Override
				public void run() {
					tableView.setSelectedIndex(currentPlayingMusicIndex);
					//play();
				}
			});
		}else{
			ApplicationContext.queueCallback(new Runnable() {
				@Override
				public void run() {
					pause();
				}
			});
		}
	}
	
	public void currentTime(final long microseconds){
		ApplicationContext.queueCallback(new Runnable() {			
			@Override
			public void run(){
				timeSlider.setValue(Math.round(microseconds/1000000));
			}
		});
	}
	
	private void buttonActions(){
		
		playButton.getComponentMouseButtonListeners().add(new ComponentMouseButtonListener(){
			@Override
			public boolean mouseClick(Component arg0, Button arg1, int arg2,
					int arg3, int arg4) {
				if(currentPlayingMusic==null)
					return false;
				
				if(isPlaying)
					pause();
				else
					play();
				
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
	
	private void loadPlaylists(){
		playlistListView.setListData(playlistList);
		java.util.List<Playlist> list=PlaylistDB.getPlaylist();
		for(Playlist category:list){
			playlistList.add(category.getName());
			playlist.put(category.getName(), category);
			java.util.List<Music> musics=MusicDB.getMusicList(category);
			musicList.put(category.getName(), musics);
		}
	}
	
	private void play(){
		String iconURL="icon/button_grey_pause.png";
		player.play();
		
		currentTimeLabel.setText(currentPlayingMusic.getTime());
		timeSlider.setRange(0, Math.round(player.getTotalDuration()/1000000));
		
		isPlaying=true;

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
	}
	
	private void pause(){
		String iconURL="icon/button_grey_play.png";
		player.pause();

		isPlaying=false;
		
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
	}
	
	private void showCurrentMusicList(){
		List<HashMap<String,String>> tableData=(List<HashMap<String,String>>)tableView.getTableData();
		tableData.clear();
		
		String playlistName=playlistListView.getSelectedItem().toString();
		
		java.util.List<Music> musics=musicList.get(playlistName);
		for(int i=0;i<musics.size();i++){
			HashMap<String,String> map=musics.get(i).toHashMap();
			map.put("no", String.valueOf(i+1));
			tableData.add(map);
		}
	}
	
	private void createMenuActions(){
		Action.getNamedActions().put("newPlaylist", new Action(){
			@Override
			public void perform(Component arg0){
				//Alert.alert("some warning", window);
				newPlaylistDialog.open(window);
			}
		});
		
		Action.getNamedActions().put("importOnlineMedia", new Action(){
			@Override
			public void perform(Component arg0) {
				importOnlineMediaDialog.open(window,playlist.get(playlistListView.getSelectedItem().toString()));
			}	
		});
	}
	
	private void createContextMenuForCategory(){
		playlistListView.setMenuHandler(new MenuHandler.Adapter(){
			public boolean configureContextMenu(Component component, Menu menu, int x, int y ){
				final int selectedItemIndex=playlistListView.getItemAt(y);
				if(selectedItemIndex==-1)
					return false;
				
				Menu.Section menuSection=new Menu.Section();
				menu.getSections().add(menuSection);
				
				Menu.Item renameMenuItem=new Menu.Item("Rename");
				menuSection.add(renameMenuItem);
				renameMenuItem.setAction(new Action(){
					@Override
					public void perform(Component arg0){
						final String selectedItem=playlistList.get(selectedItemIndex);
						renamePlaylistDialog.addRenamePlaylistDialogListener(new RenamePlaylistDialogListener() {
							@Override
							public void playlistChanged(String newPlaylist) {
								PlaylistDB.renamePlaylist(selectedItem, newPlaylist);
								playlistList.update(selectedItemIndex, newPlaylist);
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
						final String selectedItem=playlistList.get(selectedItemIndex);
						String errorMessage="Are you sure you want to remove "+selectedItem+" playlist?";
						confirmationDialog.open(window,errorMessage,new ConfirmationDialogListener(){
							@Override
							public void confirmed(){
								playlistList.remove(selectedItem);
								PlaylistDB.deletePlaylist(selectedItem);
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
	private List<String> playlistList;
	private Map<String,Playlist> playlist;
	private Map<String,java.util.List<Music>> musicList;
	
	private Player player;
	private Music currentPlayingMusic=null;
	private int currentPlayingMusicIndex=-1;
	private boolean isPlaying=false;
	
	private @BXML LinkButton playButton;
	private @BXML LinkButton stopButton;
	private @BXML LinkButton soundButton;
	
	private @BXML Label currentTimeLabel;
	private @BXML Slider timeSlider;
	
	private @BXML NewPlaylistDialog newPlaylistDialog;	
	private @BXML ConfirmationDialog confirmationDialog;
	private @BXML RenamePlaylistDialog renamePlaylistDialog;
	private @BXML ImportOnlineMediaDialog importOnlineMediaDialog;
	
	private @BXML ListView playlistListView;
	private @BXML TableView tableView;
}