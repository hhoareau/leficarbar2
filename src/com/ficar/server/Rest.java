package com.ficar.server;

import com.ficar.shared.*;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.config.Nullable;
import com.google.appengine.repackaged.com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

//http://localhost:8888/_ah/api/irl/v1/init
@Api(name = "irl",description= "irl rest service",version = "v1")
public class Rest {
    public static Random randomGenerator=new Random();
    public static DAO dao = DAO.getInstance();
    public static Logger log = Logger.getLogger(String.valueOf(Rest.class));

    @ApiMethod(name = "raz", httpMethod = ApiMethod.HttpMethod.GET, path = "raz")
    public void raz() {
        dao.raz();
    }

    @ApiMethod(name = "getevent", httpMethod = ApiMethod.HttpMethod.GET, path = "getevent")
    public Event getevent(@Named("event") String id) {
        return dao.findEvent(id);
    }

    @ApiMethod(name = "raz", httpMethod = ApiMethod.HttpMethod.GET, path = "raz")
    public List<Event> getEventsByZone(@Named("lat") Double lat,@Named("lng") Double lng) {
        List<Event> le = dao.findEvents(Tools.StringToDate("now"));
        Lieu l = dao.findLieu(lng, lat);
        le.add(dao.findEvent(l, Tools.StringToDate("now")));
        return le;
    }

    @ApiMethod(name = "getuser", httpMethod = ApiMethod.HttpMethod.GET, path = "getuser")
    public User getuser(@Named("user") String email) {
        return dao.findUser(email);
    }



    @ApiMethod(name = "deluser", httpMethod = ApiMethod.HttpMethod.GET, path = "deluser")
    public void delUser(@Named("user") String email) {
        User u=dao.findUser(email);
        if(u!=null)
            dao.delete(u);
    }

    @ApiMethod(name = "getmessage", httpMethod = ApiMethod.HttpMethod.GET, path = "getmessage")
    public List<Message> getmessage(@Named("event") String eventid, @Named("date") String date, @Named("type") String type) {

        Event e=dao.findEvent(eventid);

        Long lg=Long.parseLong(date);
        if(e.lastSave>lg) {    //Si un message a ete ajout� depuis le dernier message r�cup�r� par la session
            List<Message> rc = new ArrayList<Message>();

            for (Message m : dao.getMessagesSince(e, lg)) {
                if (m.photo != null || m.getText() != null) {
                    m.author = dao.findUser(m.from);
                    rc.add(m);
                }
            }

            Collections.sort(rc);

            return (rc);
        }
        return null;
    }

    @ApiMethod(name = "join", httpMethod = ApiMethod.HttpMethod.GET, path = "join")
    public void join(@Named("event") String id,@Named("user") String email,@Nullable @Named("password") String password) {
        Event e=dao.findEvent(id);
        User u=dao.findUser(email);

        if(u!=null && e!=null) {
            if (e.password != null && !e.password.equalsIgnoreCase(password)) {
                return;
            }
            if (e.addPresents(u)) {
                dao.save(e);
                dao.save(u);
            }
        }
    }

    @ApiMethod(name = "quit", httpMethod = ApiMethod.HttpMethod.GET, path = "quit")
    public void quit(@Named("event") String id,@Named("user") String email) {
        Event e=dao.findEvent(id);
        User u=dao.findUser(email);

        if(u!=null && e!=null) {
            if(e.delPresents(u)){
                dao.save(e);
                dao.save(u);
            }
        }
    }


    @ApiMethod(name = "quit", httpMethod = ApiMethod.HttpMethod.GET, path = "quit")
    public Event setDtPlay(@Named("event") String event, @Named("user") String email, @Named("song") String id){
        User u=dao.findUser(email);
        Event e=dao.findEvent(event);

        Song s=e.getSong(id);
        if(s!=null){
            s.dtPlay=Tools.StringToDate("now");
            e.addSong(s);
            dao.save(e);
            return e;
        }
        return null;
    }

    @ApiMethod(name = "setscore", httpMethod = ApiMethod.HttpMethod.GET, path = "setscore")
    public Event setScore(@Named("song") String idsong, @Named("event") String event, @Named("user") String email){
        User u=dao.findUser(email);
        Event e=dao.findEvent(event);

        Song s=e.getSong(idsong);
        if(s!=null){
            if(s.votants.contains(u.email)){
                return null;
            } else{
                s.score+=1;
                s.votants.add(u.email);
                e.addSong(s);

                dao.save(e);
                return(e);
            }
        }
        return null;
    }


    @ApiMethod(name = "addevent", httpMethod = ApiMethod.HttpMethod.GET, path = "addevent")
    public void addEvent(@Named("user") String email,@Named("position") String position,
                        @Named("dtStart") Long dtStart,@Named("password") String password,
                        @Named("title") String title,@Named("duration") Integer duration){
        String ici="true";
        User u=dao.findUser(email);
        Lieu nl=null;
        nl=new Lieu(position);
        List<Lieu> l=dao.findLieuByName(nl.name);

        if(u!=null){
            if(l.size()>0)
                nl=l.get(0);
            else {
                if(nl.CP!=null && nl.CP.length()>0 && nl.street.length()>0){
                    if(ici.equals("true")){
                        nl.setPosition(position);
                        dao.save(nl);
                    }else{
                        dao.save(nl);
                        new RestCall("https://maps.google.com/maps/api/geocode/json?address="+nl.getAddress()+"&sensor=false",null,nl.Id){
                            @Override public void onSuccess(String rep){
                                if(rep.contains("lat") && rep.contains("lng")){
                                    String lat=Tools.extract(rep, "\"lat\" : ",",",false);
                                    String lg=Tools.extract(rep, "\"lng\" : ","}",false).trim();
                                    Lieu l=dao.findLieu(this.id);
                                    l.setPosition(lat+","+lg);
                                    dao.save(l);
                                }
                            }
                        };
                    }
                }
            }

            if(nl!=null){

                String str_localdate="fr";
                Long localdate=Long.parseLong(str_localdate);
                Long offset=Tools.StringToDate("now")-localdate;
                dtStart-=offset;

                Event e=dao.findEvent(nl,dtStart);
                    if(password!=null && password.length()==0)password=null;
                    e=new Event(	title,password,nl,
                            dtStart,
                            duration,
                            u.email,
                            //this.loadFile("/WEB-INF/demandes.txt"));
                            "");

                    //e.flyer=Tools.convertStreamToString(req.getInputStream());
                    //e.ipAdresse=req.getRemoteHost();
                    dao.save(e);

                    //if(e.dtStart<Tools.StringToDate("now") && e.dtEnd>Tools.StringToDate("now"))
                        //this.returnString(resp, "addPresent?event="+e.Id+"&passorwd="+password);
                    //else
                        //sinon on repart dans le choix des evenements
                        //this.returnString(resp, "selEvent.html?nologin&user="+u.Id);
                }
            }
    }


    @ApiMethod(name = "getslideshow", httpMethod = ApiMethod.HttpMethod.GET, path = "getslideshow")
    public List<Message> getSlideShow(@Nullable @Named("delay") Integer delay,
                                      @Named("event") String idEvent,
                                      @Named("dtStart") Long dtStart,
                                      @Named("dtEnd") Long dtEnd){
        Event e=dao.findEvent(idEvent);
        List<Message> lMessage=null;

        if(delay!=null){
            lMessage=dao.getMessagesFrom(e,delay);
        } else {
            Long lEnd=Tools.StringToDate("now");
            Long lStart=0L;
            lMessage=dao.getMessages(lStart,lEnd);
        }

        List<Message> rc=new ArrayList<Message>();
        for(Message m:lMessage)
            if(m.photo!=null)
                rc.add(m);

        Collections.sort(rc); //Les derniers messages en premier dans la liste

        return rc;
    }

    @ApiMethod(name = "addevent", httpMethod = ApiMethod.HttpMethod.GET, path = "addevent")
    public void addevent(@Named("event") String id,@Named("user") String email) {
    }

    @ApiMethod(name = "closeevent", httpMethod = ApiMethod.HttpMethod.GET, path = "closeevent")
    public void closeevent(@Named("event") String id,@Named("user") String email) {
    }

    @ApiMethod(name = "adduser", httpMethod = ApiMethod.HttpMethod.GET, path = "adduser")
    public User addUser(@Named("info") String s) {
        infoFacebook infos = null;
        if (s.startsWith("{"))
            infos = new Gson().fromJson(s, infoFacebook.class);
        else
            infos = new infoFacebook(s.replace("\"", ""));

        User u = dao.findUser(infos.email);
        if (u == null) {
            u = new User(infos);
            dao.save(u);
        }
        return u;
    }



    @ApiMethod(name = "getplaylist", httpMethod = ApiMethod.HttpMethod.GET, path = "getplaylist")
    public List<Song> getplaylist(@Named("event") String id) {
        Event e=dao.findEvent(id);
        return e.getPlaylist();
    }

    @ApiMethod(name = "getclassement", httpMethod = ApiMethod.HttpMethod.GET, path = "getclassement")
    public List<User> getclassement(@Named("event") String id) {
        Event e = dao.findEvent(id);
        if (e == null) return null;

        List<User> lu = dao.getUsers(e.Presents);
        Collections.sort(lu);
        return (lu);
    }


    @ApiMethod(name = "findplace", httpMethod = ApiMethod.HttpMethod.GET, path = "findplace")
    public List<Lieu> findplace(@Named("name") String name) {
        return dao.findLieuByName(name);
    }


    @ApiMethod(name = "addmessage", httpMethod = ApiMethod.HttpMethod.POST, path = "addmessage")
    public Message addmessage(@Named("user") String email, @Named("event") String eventid,
                              @Named("text") String text, @Named("anonymous") String anonymous,
                              byte[] data) {

        User u=dao.findUser(email);
        Event e=dao.findEvent(eventid);


        final Message m=new Message(e,u,text,data);
        if(anonymous!=null)m.anonymous=anonymous.equals("true");

        dao.save(m);
        e.lastSave=m.dtMessage;

        /*
        if(m.type==Message.TYPE_VIDEO){

            new RestCall("http://gdata.youtube.com/feeds/api/videos/"+new Song(m).getYouTubeID()+"?v=2",null,m.Id){
                @Override public void onSuccess(String rep){
                    Message m=dao.findMessage(this.id);
                    Song sg=new Song(m);
                    sg.setTitle(Tools.extract(rep, "<title>", "</title>", false));
                    e.addSong(sg);
                }

                @Override public void onFailure(int rc){
                    Message m=dao.findMessage(this.id);
                    Song sg=new Song(m);
                    sg.setTitle(m.title);
                    e.addSong(sg);
                }
            };
        }*/

        /*
        if(m.type==Message.TYPE_DEMANDE){
            Demande d=new Demande(m);
            d.icon=Tools.extract(e.getDemande(d.nature), "demandIcon=", "\r", false);
            d.photo=Tools.extract(e.getDemande(d.nature), "demandPhoto=", "\r", false);
            User dest=dao.findUser(d.to);
            if(dest!=null)
                if(dest.addDemande(d)){
                    dao.save(dest);
                    Message dd=new Message(d,u,dest);
                    if(dd.photo==null || dd.photo.length()<10){
                        String str=this.loadFile(d.photo);
                        if(str!=null)dd.photo= Base64.encode(str.getBytes());
                    }
                    dao.save(dd);
                }
        }
        dao.save(e);
        */

        return(m);
    }



}