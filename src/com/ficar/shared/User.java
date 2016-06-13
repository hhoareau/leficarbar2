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

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Cette classe permet de stocker le r�sultat du parsing HTML
 * @see 
 * @author Herv� Hoareau
 *
 */
@Entity
public class User implements Comparable<User> {
	protected static final Logger log = Logger.getLogger(User.class.getName());
	
	@Id public String email; 					//Id interne des Users (adresse mail)
	
	public String name="";						//Nom du User	
	public String facebookid=null; 
	public String firstname;
    public String ip=null;
	public String photo=null;
	public String state="";
	
	public String currentEvent=null;
	
	public Double lg=null;
	public Double lat=null;
	
	public ArrayList<Vote> votes=new ArrayList<Vote>();
	public ArrayList<Demande> demandes=new ArrayList<Demande>();

	public Integer score=0;

	private Long dtLastPosition;
    private String home;

    public User(String email,String name,String facebookid,String photo){
		this.email=email.toLowerCase();
		if(name==null || name.length()==0)
			this.name=email.split("@")[0];
		else
			this.name=name;
		
		this.setFacebookId(facebookid);
		if(photo!=null && photo.length()>0)this.photo=photo;
	}

    protected void initUser(infoFacebook infos) {
        if(infos.email!=null)
            this.email = infos.email;
        else
            this.email = infos.id;

        this.firstname = infos.first_name;
        this.photo = infos.link;
        this.home = "https://www.facebook.com/" + infos.id;

    }

    public User(infoFacebook i) {
        initUser(i);
    }


    public User(String email) {
        initUser(new infoFacebook(email));
    }



    /**
	 * Fixe la position de l'utilisateur
	 * @param lg
	 * @param lat
	 */
	public void setPosition(Double lg,Double lat){
		this.lg=lg;
		this.lat=lat;
		this.dtLastPosition=Tools.StringToDate("now");
	}
	
	

	
	public User(){}




	/**
	 * Format destin� au mail ou page web
	 * @return
	 */
	public String toMail(){
		return("bonjour "+name+"\n Votre email enregistre est le "+email+" \n");
	}

	@Override
	public int compareTo(User o) {
		if(o.score>this.score)
			return -1;
		else
			return 1;
	}


	public boolean addScore(Vote v) {
		if(!this.votes.contains(v)){
			this.score+=v.score;		
			this.votes.add(v);
			return true;
		}
		return false;
	}

	

	public void setFacebookId(String facebookid) {
		this.facebookid=facebookid;
		if(this.facebookid!=null && facebookid.length()>0 && !facebookid.equals("null"))
			this.photo="https://graph.facebook.com/"+facebookid+"/picture";
		else
			this.photo="personne.png";
	}


	public boolean addDemande(Demande d) {
		if(!this.demandes.contains(d)){
			this.demandes.add(d);
			return true;
		}
		return false;
	}


	public Demande setDemande(Event e,String from, String nature, boolean accept,String acceptIcon,String refuseIcon,String acceptPhoto,String refusePhoto) {
		Demande d=new Demande(e.Id,from,nature,null,null);
		d.to=this.email;
		d.dtDemande=Tools.StringToDate("now");
		d.setRespons(accept, acceptIcon, refuseIcon,acceptPhoto, refusePhoto);
		int pos=this.demandes.indexOf(d);
		if(pos>-1){
			this.demandes.set(pos, d);
			return d;
		}
		return null;
			
	}

	
	
	
}
