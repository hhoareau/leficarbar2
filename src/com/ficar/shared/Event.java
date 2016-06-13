/*
 * Copyright (C) 2012 SFR API - Herv� Hoareau

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.ficar.shared;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;


/**
 * Cette classe permet de stocker le r�sultat du parsing HTML
 * 
 * @see 
 * @author Herv� Hoareau
 *
 */
@Entity
public class Event extends Tools implements Comparable<Event> {
	protected static final Logger log = Logger.getLogger(Event.class.getName());
	
	@Id public String Id; 													//Id interne des messages	
	@Index public Long dtStart;
	@Index public Long dtEnd;
	public boolean opened=true;									//Indique si l'evt est ouvert au non invit�
	public String title;
	public String logo;
	public String ipAdresse=null;
	public String facebook_event;
	public String idLieu;										//Description de l'endroit
	public String password=null;
	public String leader=null;
	public Integer maxOnline=50;
	public String typeDemandes;
	public String flyer="";
		
	public List<Song> playlist=new ArrayList<Song>();
	
	//TODO regarder sur la doc objectify
	public List<String> Invites=new ArrayList<String>();				
	public List<String> Presents=new ArrayList<String>();

	public Long lastSave=0L;	//Date du dernier message post�

	public Double distanceFromUser;

	
	
	//@NotSaved public List<User> classement;
	
	public Event(){}
	
	/**
	 * 
	 * @param title titre de l'event
	 * @param place endroit de l'event
	 * @param dtStart date de d�but
	 * @param duration 
	 * @param leader
	 */
	public Event(String title,String password,Lieu place,Long dtStart,int duration,String leader,String typeDemandes){
		this.dtStart=dtStart;
		this.dtEnd=this.dtStart+duration*3600*1000;
		this.title=title;
		this.idLieu=place.Id;
		this.leader=leader;
		if(password!=null && password.length()>0)this.password=password;
		
		this.typeDemandes=typeDemandes;
		this.setId();
	}


	
	public Song getSong(String url){
		if(url==null)return null;
		for(Song s:playlist)
			if(s.url.equals(url))return s;
		
		return null;
	}
	
	public boolean addSong(Song song){
		for(Integer i=0;i<playlist.size();i++){
			Song s=playlist.get(i);
			if(s.url!=null && s.url.equals(song.url)){
				if(song.dtCreate!=s.dtCreate)s.score++;
				playlist.set(i, s);
				return true;
			}
		}
		
		this.playlist.add(song);
		return true;
	}


	private void setId(){
		if(this.title!=null)
			this.Id=this.title.replace(" ", "")+Tools.getDate(dtStart).replace("/", "").split(" ")[0];
		else
			this.Id=this.idLieu+Tools.getDate(dtStart).replace("/", "").split(" ")[0];		
	}

	@Override
	public int compareTo(Event o) {
		if(o.distanceFromUser<this.distanceFromUser)
			return -1;
		else
			return 1;
	}

	
	public void addInvited(User u) {
		if(!this.Invites.contains(u.email))
			this.Invites.add(u.email);
	}

	/**
	 *
	 * @return Songs never played
	 */
	public List<Song> getPlaylist() {
		List<Song> rc=new ArrayList<Song>();
		for(Song s:this.playlist){
			if(s.dtPlay==null){
				rc.add(s);
			}
				
		}
		Collections.sort(rc);
		return rc;
	}

	public boolean addPresents(User u) {
		if(!this.Presents.contains(u.email)){
			this.Presents.add(u.email);
			u.currentEvent=this.Id;
			return true;
		}
		
		return false;
	}

	public boolean delPresents(User u) {
		if(this.Presents.contains(u.email)){
			this.Presents.remove(u.email);
			u.currentEvent=null;
			return true;
		}
		
		return false;
	}

	public String[] getDemande(int pos){
		String[] s=this.typeDemandes.split("nature=");
		if(pos>s.length)return null;
		
		String[] rc=("nature="+s[pos]).split("\n");
		return rc;
	}
	
	public String getDemande(String nature){
		for(String s:this.typeDemandes.split("nature=")){
			if(s.startsWith(nature)){
				return(s.replace(nature+"\r\n",""));
			}	
		}
		return null;
		
	}

	public String toString(){
		return "Event:"+this.title+" de "+Tools.getDate(this.dtStart)+" � "+Tools.getDate(this.dtEnd)+" situ� � "+this.idLieu;
	}
	
	
}
