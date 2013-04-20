package net.ilbay.playlist;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MusicDB {
	public static void main(String[] args){
		Music music=new Music();
		music.setTitle("Asian Dream Song");
		music.setArtist("Joe Hisaishi");
		music.setGenre("Instrumental");
		music.setTime("05:54");
		//MusicDB.addMusic(9,music);
		//MusicDB.getMusicList();
		System.out.println(music.getId());
	}
	
	public static List<Music> getMusicList(Playlist playlist){
		List<Music> musicList=new ArrayList<Music>();
		try {
			if(conn==null)
				connectToDatabase();
			
			Statement stat = conn.createStatement();
			ResultSet resultSet=stat.executeQuery("select * from MUSIC,PLAYLIST_MUSIC WHERE playlistId="+playlist.getId());
			while(resultSet.next()){
				Music music=new Music();
				music.setTitle(resultSet.getString("title"));
				music.setArtist(resultSet.getString("artist"));
				music.setGenre(resultSet.getString("genre"));
				music.setTime(resultSet.getString("time"));
				music.setId(resultSet.getInt("id"));
				music.setVideoId(resultSet.getString("videoId"));
				musicList.add(music);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (ClassNotFoundException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return musicList;
	}
	
	public static void deleteMusic(Playlist playlist,Music music){
		try {
			if(conn==null)
				connectToDatabase();
			Statement statement=conn.createStatement();
			statement.execute("DELETE FROM PLAYLIST_MUSIC WHERE musicId='"+music.getId()+"' AND playlistId='"+playlist.getId()+"'");
			
			ResultSet resultSet=statement.executeQuery("SELECT * FROM PLAYLIST_MUSIC WHERE musicId='"+music.getId()+"'");
			if(!resultSet.next()){
				statement.execute("DELETE FROM MUSIC WHERE id='"+music.getId()+"'");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(ClassNotFoundException e){
			e.printStackTrace();
		}
	}
	
	public static void addMusic(Playlist playlist,Music music){
		try {
			if(conn==null)
				connectToDatabase();
			PreparedStatement prepStatement=conn.prepareStatement("INSERT INTO MUSIC(title,artist,genre,time,videoId) VALUES(?,?,?,?,?)");
			prepStatement.setString(1, music.getTitle());
			prepStatement.setString(2, music.getArtist()==null ? "Unknown" : music.getArtist());
			prepStatement.setString(3, music.getGenre()==null ? "Other" : music.getGenre());
			prepStatement.setString(4, music.getTime());
			prepStatement.setString(5, music.getVideoId());
			prepStatement.execute();
			
			Statement statement=conn.createStatement();
			ResultSet resultSet=statement.executeQuery("SELECT LAST_INSERT_ROWID() FROM MUSIC");
			while(resultSet.next())
				music.setId(resultSet.getInt(1));
			
			statement.execute("INSERT INTO PLAYLIST_MUSIC(playlistId,musicId) VALUES('"+playlist.getId()+"','"+music.getId()+"')");			
		}catch (SQLException e) {
			e.printStackTrace();
		}catch(ClassNotFoundException e){
			e.printStackTrace();
		}
	}
	
	public void updateMusic(Music music){
		try {
			if(conn==null)
				connectToDatabase();
			PreparedStatement prepStatement=conn.prepareStatement("UPDATE MUSIC SET title=?,artist=?,genre=? WHERE id='"+music.getId()+"'");
			prepStatement.setString(1,music.getTitle());
			prepStatement.setString(2,music.getArtist());
			prepStatement.setString(3,music.getGenre());
			prepStatement.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(ClassNotFoundException e){
			e.printStackTrace();
		}
	}
	
	public static void disconnectFromDatabase(){
		if(conn!=null){
			try {
				conn.close();
				conn=null;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private static void connectToDatabase() throws ClassNotFoundException, SQLException{
		Class.forName("org.sqlite.JDBC");
		conn=DriverManager.getConnection("jdbc:sqlite:data/database.db");
		Statement stat=conn.createStatement();
		stat.execute("PRAGMA foreign_keys=ON");
	}
	
	private static Connection conn;
}