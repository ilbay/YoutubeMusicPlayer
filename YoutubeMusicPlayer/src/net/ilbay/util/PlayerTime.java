package net.ilbay.util;

public class PlayerTime {
	public static String convertSeconds(long time){
		String ss="";

		while(time>0){
			if(!ss.equals(""))
				ss=":"+ss;
			String temp=String.valueOf(time%60);
			ss=(temp.length()%2!=0 ? "0"+temp : temp)+ss;
			time=(long)Math.floor(time/60);
		}
				
		return ss;
	}
}