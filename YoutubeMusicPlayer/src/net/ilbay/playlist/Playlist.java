package net.ilbay.playlist;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


import java.util.ArrayList;
import java.util.List;

public class Playlist{
	public Playlist(){
		try{
			connectToDatabase();
		}catch(Exception e){
			//TODO: throw a new exception about being not able to connect to database.
			e.printStackTrace();
		}
	}
	
	public static List<String> getCategories(){
		List<String> playList=new ArrayList<String>();
		try {
			if(conn==null)
				connectToDatabase();
			
			Statement stat = conn.createStatement();
			ResultSet resultSet=stat.executeQuery("select name from PLAYLIST");
			while(resultSet.next()){
				playList.add(resultSet.getString("name"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (ClassNotFoundException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return playList;
	}
	
	public static void addPlaylist(String playlist){
		try {
			Statement stat = conn.createStatement();
			stat.execute("INSERT INTO PLAYLIST(name) VALUES('"+playlist+"')");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void deletePlaylist(String playlist){
		try {
			Statement stat=conn.createStatement();
			stat.execute("DELETE FROM PLAYLIST WHERE name='"+playlist+"'");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void renamePlaylist(String oldPlaylist,String newPlaylist){
		try {
			Statement stat=conn.createStatement();
			stat.execute("UPDATE PLAYLIST SET name='"+newPlaylist+"' WHERE name='"+oldPlaylist+"'");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
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