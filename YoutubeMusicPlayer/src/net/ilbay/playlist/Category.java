package net.ilbay.playlist;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


import java.util.ArrayList;
import java.util.List;

public class Category{
	public Category(){
		try{
			connectToDatabase();
		}catch(Exception e){
			//TODO: throw a new exception about being not able to connect to database.
			e.printStackTrace();
		}
	}
	
	public static List<String> getCategories(){
		List<String> categoryList=new ArrayList<String>();
		try {
			if(conn==null)
				connectToDatabase();
			
			Statement stat = conn.createStatement();
			ResultSet resultSet=stat.executeQuery("select name from CATEGORY");
			while(resultSet.next()){
				categoryList.add(resultSet.getString("name"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (ClassNotFoundException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return categoryList;
	}
	
	public static void addCategory(String category){
		try {
			Statement stat = conn.createStatement();
			stat.execute("INSERT INTO CATEGORY(name) VALUES('"+category+"')");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void deleteCategory(String category){
		try {
			Statement stat=conn.createStatement();
			stat.execute("DELETE FROM CATEGORY WHERE name='"+category+"'");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void renameCategory(String oldCategory,String newCategory){
		try {
			Statement stat=conn.createStatement();
			stat.execute("UPDATE CATEGORY SET name='"+newCategory+"' WHERE name='"+oldCategory+"'");
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