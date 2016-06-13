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

package com.ficar.server;

import com.ficar.shared.*;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import static com.googlecode.objectify.ObjectifyService.ofy;


/**
 * Cette classe regroupe l'ensemble des m�thodes utilis�es
 * par les servlet du serveur pour manipuler la base de donn�es de google
 * @see http://code.google.com/p/objectify-appengine/wiki/BestPractices#Utilisez_un_DAO
 *  
 * @author Herve Hoareau
 */
public class DAO  {
	private static final Logger log = Logger.getLogger(DAO.class.getName());
    public static DAO dao=null;

	static 	{			
			ObjectifyService.register(User.class);
			ObjectifyService.register(Message.class);	
			ObjectifyService.register(Event.class);
			ObjectifyService.register(Lieu.class);
			}

    public static synchronized DAO getInstance() {
        if (null == dao) {
            dao = new DAO();
        }
        return dao;
    }


    public static Long lastDemande=0L;
	
	public List<Message> getPlayList(){
		Long dtStart=Tools.StringToDate("now")-10*3600*1000; //10 derniere heures
		List<Message> lm=getMessages(dtStart,Tools.StringToDate("now"));
		return lm;
	}
	
	public Key<User> save(User c) {
		return ofy().save().entity(c).now();
	}
	
	
	public Key<Message> save(Message m) {
		return ofy().save().entity(m).now();
	}
	

	public void raz() {
		log.warning("Raz");
        ofy().delete().keys(ofy().load().type(Message.class).keys().list());
        ofy().delete().keys(ofy().load().type(User.class).keys().list());
        ofy().delete().keys(ofy().load().type(Vote.class).keys().list());
        ofy().delete().keys(ofy().load().type(Lieu.class).keys().list());
        ofy().delete().keys(ofy().load().type(Event.class).keys().list());
	}
	

	public List<Message> findMessages(User user) {
		if(user==null)
			return new ArrayList<Message>();
		else
			return ofy().load().type(Message.class).filter("idUser", user.email).list();
	}

	
	public void delUser(User c){
		//ofy().delete(this.findMessages(c));
		//ofy().delete(c);
	}
	

	public User findUser(User u) {
		try{
			return ofy().load().type(User.class).id(u.email).now();
		} catch (Exception e) {
			return null;
		}
		
	}



	public List<Message> getMessagesSince(Event e,long dt) {
		return ofy().load().type(Message.class)
					.filter("dtMessage >", dt)
					.filter("idEvent", e.Id)
					.filter("type",0)
					.list();
	}

	/**
	 * Retrouve l'evenement d'un message
	 * @param m
	 * @return
	 */
	public Event findEvents(Message m){
		for(Event e:ofy().load().type(Event.class).filter("dtStart <", m.dtMessage)
								.filter("dtEnd >", m.dtMessage).list()){
			
			Collection<Event> lu=ofy().load().type(Event.class).ids(e.Presents).values();
			User u=ofy().load().type(User.class).id(m.from).now();
			
			if(lu.contains(u))return(e);
		}
		
		return null;
	}
	
	/**
	 * Donne les message depuis x heure
	 * @param delay nombre de d'heure
	 * @return liste des messages
	 */
	public List<Message> getMessagesFrom(Event e,Integer delay) {
		Long dt=Tools.StringToDate("now")-3600*delay*1000;
		return getMessagesSince(e,dt);
	}
	


	public List<Message> getMessages(long dtStart, long dtEnd) {
		return ofy().load().type(Message.class)
					.filter("dtMesure >", dtStart)
					.filter("dtMesure <", dtEnd).list();
	}
	

	public List<User> getPresents(Event e){
		return (List<User>) ofy().load().type(User.class).ids(e.Invites).values();
	}

	/**
	 * 
	 * @param e
	 * @return la liste de toutes les chansons demand� pour une soir�e
	 */
	public List<Song> getSongs(Event e){
		ArrayList<Song> playlist=new ArrayList<Song>();
		for(Message m:ofy().load().type(Message.class).filter("idEvent", e.Id)
                .filter("type", Message.TYPE_SONG)
                .list()){
			
			e.addSong(new Song(m));
		}
		return(playlist);
	}


	public User findUser(String from) {
		if(from==null)return null;
		try{
			return ofy().load().type(User.class).id(from).now();
		} catch (Exception e){
			return null;
		}
		
	}


	public void save(Event event) {
		ofy().save().entities(event);
	}

	
	public Event findEvent(String idEvent) {
		if(idEvent==null)return null;
		try{
			return ofy().load().type(Event.class).id(idEvent).now();
		} catch (Exception e){
			return null;
		}
	}

	/**
	 * Recherche d'un evenement par proximit�
	 * @return
	 */
	public Lieu findLieu(double gps_long, double gps_lat) {
		Lieu rc=null;
		Double d=(double) 1000000;
		for(Lieu e:ofy().load().type(Lieu.class).list()){
			if(distance(e.gpsLong,e.gpsLat,gps_long,gps_lat)<d){
				d=distance(e.gpsLong,e.gpsLat,gps_long,gps_lat);
				rc = e;
			}
		}
		return rc;
	}

	private Double distance(Double lg1, Double lat1, double lg2,double lat2) {
		return Math.acos(Math.sin(lg1)*Math.sin(lg2)+Math.cos(lat1)*Math.cos(lat2))*6371;
	}

	/**
	 * 
	 * @param dt
	 * @return event actif at dt date
	 */
	public List<Event> findEvents(Long dt) {
		List<Event> le=new ArrayList<Event>();
		for(Event e:ofy().load().type(Event.class)	.filter("dtStart <=", dt)	.list())
			if(e.dtEnd>dt)
				le.add(e);
		
		return le;
	}

	/**
	 * Retourne la liste des evenement possible pour un utilisateur donn�e
	 * @param u
	 * @return
	 */
	public List<Event> findEvents(User u) {
		if(u==null)return null;
		
		List<Event> rc=new ArrayList<Event>();
		
		for(Event e:this.getActifEvents()){
			Lieu l=this.findLieu(e.idLieu);
			e.distanceFromUser=l.distance(u);
			
			if(e.Invites.contains(u.email) || (e.ipAdresse!=null && e.ipAdresse.equals(u.ip)))
				rc.add(e);
			else{	
				if(e.opened && e.distanceFromUser<100)rc.add(e);
			}
		}		
		return rc;
	}

	/**
	 * Retourne les evenemnts actuellement actifs 
	 * @return All actif events
	 */
	public List<Event> getActifEvents() {
		return this.findEvents(Tools.StringToDate("now"));
	}

	public void save(Vote vote) {
		ofy().save().entities(vote);
	}

	public List<User> getUsers(List<String> presents) {
		return (List<User>) ofy().load().type(User.class).ids(presents).values();
	}

	public List<Demande> getDemandeSince(Event e, Long date) {
		List<Demande> rc=new ArrayList<Demande>();
		if(e==null)return null;
		for(User u:this.getUsers(e.Presents))
			for(Demande d:u.demandes){
				if(d.dtDemande>date){
					d.to=u.email;
					rc.add(d);
				}
			}
		return rc;
	}

	/**
	 * 
	 * @param Id
	 * @return
	 */
	public Lieu findLieu(String Id) {
		if(Id==null)return null;
		return ofy().load().type(Lieu.class).id(Id).now();
	}
	
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public List<Lieu> findLieuByName(String name) {
		if(name==null)return null;
		return ofy().load().type(Lieu.class).filter("name",name).list();
	}
	
	
	public void save(Lieu l){
		ofy().save().entities(l);
	}

	
	public Event findEvent(Lieu l,Long date) {
		for(Event e:this.findEvents(date))
			if(e.idLieu.equals(l.Id))
				return e;
		
		return null;
	}

	public Lieu findLieu(String number, String street, String cp) {
		for(Lieu l:ofy().load().type(Lieu.class).filter("CP", cp).list())
			if(l.number.equals(number) && l.street.equals(street))return l;
		return null;
	}

	public void delete(User u) {
		ofy().delete().entity(u);
	}

	public Message findMessage(String id) {
		return ofy().load().type(Message.class).id(id).now();
	}

}


