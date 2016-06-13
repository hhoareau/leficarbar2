package com.ficar.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Song implements Serializable,Comparable<Song> {
	private static final long serialVersionUID = 1L;
	
	public String url;
	public Long dtPlay=null;		//Date � laquel la musique est pass�
	public Long dtCreate=Tools.StringToDate("now");
	public String title="";
	public String from;
	public ArrayList<String> votants=new ArrayList<String>();
	public Integer score=0;
	
	public Song(){};
	
	public Song(Message m){
		 Matcher match= Pattern.compile("https?:\\/\\/(?:[0-9A-Z-]+\\.)?(?:youtu\\.be\\/|youtube\\.com\\S*[^\\w\\-\\s])([\\w\\-]{11})(?=[^\\w\\-]|$)(?![?=&+%\\w]*(?:['\"][^<>]*>|<\\/a>))[?=&+%\\w]*",Pattern.CASE_INSENSITIVE).matcher(m.title+" "+m.getText());
		 while(match.find()){
			String u=match.group()+"&";
			if(u.contains("v=")){
				u=u.split("v=")[1].split("&")[0];
				this.url="https://www.youtube.com/v/"+u+"?version=3";	
			}
			else this.url=null;
		 }
		 this.title=m.title;
		 this.from=m.from;
	}

	public String getYouTubeID() {
		String param=url.split("\\?version")[0];
		return param.split("v/")[1];
	}

	@Override
	public int compareTo(Song o) {
		if(o.score<this.score)return -1;
		if(o.score==this.score && o.dtCreate>this.dtCreate)return -1;
		return 1;
	}

	public void setTitle(String title) {
		title=title.replace(" - YouTube by", "");
		if(title.length()>50)title=title.substring(0,50);
		this.title=title;
	}
	
}
