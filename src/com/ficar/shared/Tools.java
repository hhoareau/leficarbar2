package com.ficar.shared;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.io.UnsupportedEncodingException;

import java.net.URLEncoder;

import java.text.DateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;

import com.ficar.server.RestCall;
import com.google.gdata.util.common.util.Base64;
import com.google.gdata.util.common.util.Base64DecoderException;



public class Tools {
	public static final Logger log = Logger.getLogger(Tools.class.getName());

	private static final String ADMIN_EMAIL = "mailsellerserver@gmail.com";
		
	public static String sep_champs=",";
	public static String sep_enreg="sepenreg";
	public static String char_perso="_";
	
	

	public static long defaultValidityDelay=200*24*3600*1000; //200 jours de validity pour les parser
	
	public static String convertStreamToString(InputStream is) {
	    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}
	
	public static byte[] convertStreamToByte(java.io.InputStream is) {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		int nRead;
		byte[] data = new byte[106384];

		try {
			while ((nRead = is.read(data, 0, data.length)) != -1) {
			  buffer.write(data, 0, nRead);
			}
			buffer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return buffer.toByteArray();
	}
	
	
	
	public static String washString(String str){
		/**
		 * @TODO inclure ici une fonction de netoyage des chaines de caract�re 
		 */
		str=str.replaceAll("  ", " ").trim().replaceAll(sep_champs, "").replaceAll(sep_enreg, "").trim();
		str=str.replaceAll("\n", "").replaceAll("\r", "").replaceAll(":", "").replaceAll("<", "").replaceAll(">", "");
		str=str.replaceAll("%", "");
		return(str);
	}

	/**
	 * Extraction des chaines de caracteres
	 * @param str chaine a d�couper
	 * @param start debut de la d�coupe
	 * @param end fin de la d�coupe
	 * @param toLowerCase pr�cise si l'on tient compte de la casse pour trouver les chaines
	 * @return la chaine comprise entre "start" et "end"
	 */
	public static String extract(String str,String start,String end,Boolean toLowerCase){
		if(str==null)return null;
		int i=0,j=str.length();
		if(toLowerCase)
			i=str.toLowerCase().indexOf(start.toLowerCase())+start.length();
		else
			i=str.indexOf(start)+start.length();
		
		if(i<start.length())i=0;
		
		if(end.length()>0){
			if(toLowerCase)
				j=str.toLowerCase().indexOf(end.toLowerCase(),i);
			else
				j=str.indexOf(end,i);
			
			if(j==-1)j=str.length();
		}	
		return(str.substring(i,j));
	}
	
	public static String ampute(String s,int fromStart,int fromEnd){
		return s.substring(fromStart, s.length()-fromEnd);
	}

	protected String remove(String str,String start,String end){
		int i=str.indexOf(start)+start.length();if(i<start.length())i=0;
		int j=str.indexOf(end,i);if(j==0)j=str.length()-1;
		return(str.substring(0, i)+str.subSequence(j, str.length()-1));
	}
	
	/**
	 * Traitement des messages mime
	 * @param message
	 * @return
	 */
	public ArrayList<String> getDocuments(MimeMessage message) {
	
		String s=null;
		ArrayList<String> rc=new ArrayList<String>(); 
		try {
			//log.warning("Message de type : "+message.getContentType());
			Object o=message.getContent();
			
			if (o instanceof Multipart) {
				Multipart m = (Multipart) o;
				for(int i=0;i<m.getCount();i++){
					Part part=m.getBodyPart(i);
					s+=m.getBodyPart(i).getContent().toString();
					
					String disposition = part.getDisposition();
					if (disposition != null && (disposition.equals(Part.ATTACHMENT) || disposition.equals(Part.INLINE))){
						String file=new Scanner(part.getInputStream(),"UTF-8").next();
						log.warning("File size="+file.length()+" content:"+part.getContentType());
						rc.add(file);
					}
				}
				
				rc.add(s);
			};
			
			if(o instanceof InputStream){
				s=new Scanner((InputStream) o,"Cp1252").useDelimiter("\\A").next();
			}
			
			if(o instanceof String){
				s=(String) o;
			}
			
			
		} catch (MessagingException | IOException e) {
			e.printStackTrace();
		}
			
		return rc;
	}
	
	
	
	

	public static String compile(String ens1, String ens2) {
		String rc=ens1;
		for(String elt:ens2.split(sep_champs))
			if(!rc.contains(elt))rc+=elt+sep_champs;
		
		return rc.substring(0,rc.length()-sep_champs.length());
	}
	

	public static boolean chkMail(String email){
		if(email!=null)
			if(email.contains("@") && !email.startsWith("@") && email.endsWith(".com"))return(true);
		return false;
	}
	
	/**
	 * Recherche une adresse mail dans un corps de mail
	 * TODO � tester
	 * @see http://imss-www.upmf-grenoble.fr/prevert/Prog/Java/CoursJava/expressionsRegulieres.html  
	 * @see http://www.regxlib.com/DisplayPatterns.aspx?cattabindex=0&categoryId=1
	 * 
	 * @param s
	 * @return la liste des mail trouver dans le document
	 */
	public static List<String> findMail(String s){
		
		ArrayList<String> rc=new ArrayList<String>();
		s=" "+s.replaceAll("<", " ").replaceAll(">", " ").toLowerCase()+" ";
		s=s.replaceAll("\"", "").replaceAll("\"", "");
		/*
		String mailRegex = "([a-z0-9_]|\\-|\\.)+@(([a-z0-9_]|\\-)+\\.)+[a-z]{2,4}";
		//String mailRegex = "^[_a-z0-9-]+(\\.[_a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)+$";
		
		
		
		
		Pattern p = Pattern.compile(mailRegex);
		Matcher m = p.matcher(s);
		if( m.matches())
			   for(int i= 0; i<= m.groupCount(); ++i){
				   String adresse=m.group(i);
				   if(adresse!=null && adresse.contains("@"))rc.add(adresse);
			   }
		
		if(rc.size()==0){
			log.severe("Aucun adresse mail trouv� dans "+s);
		}
		
		return rc;
		
		*/
		try{
			int i=0,j,k;
			String cars=" <>";
			while(i>-1){
				i=s.indexOf("@",i+1);
				if(i>-1){
					for(j=i-1;j>=0;j--){
						String ch=s.substring(j,j+1);
						if(cars.indexOf(ch)>-1){
							for(k=i+1;k<s.length();k++)
								if(cars.indexOf(s.substring(k,k+1))>-1){
									rc.add(s.substring(j+1,k).toLowerCase());
									i=k+1;
									break;
								}
							break;
						}
							
					}	
				}
			}
		}catch (Exception e){
			log.severe(e.getMessage()+" : Impossible de trouver les mails dans "+s);
		}
		
		
		return rc;
	}
	
	/*vrai si A est inclu dans B
	public static boolean estDans(List<Info> A,List<Info> B){
		for(Info i:A)
			if(!i.estDans(B,true))return false;
		
		return true;
	}*/

	
	
		
	public static String getDate(String dt) {
		if(dt.length()==0)return("");
		if(dt.contains("/"))
			return Tools.StringToDate(dt).toString();
		else
			return getDate(Long.parseLong(dt));
	}

	
	public static String getDate(Long dt) {
		if(dt==null)dt=new Date().getTime();
		GregorianCalendar calendar = new GregorianCalendar();
		DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.FRANCE);
		TimeZone timeZone = TimeZone.getTimeZone("CST");
		formatter.setTimeZone(timeZone);
		
		return formatter.format(dt);
	}

	public static String generatePassword() {    
		return UUID.randomUUID().toString().substring(0,6);
	}

	public static Long  getFromNow(int i) {
		return new Date().getTime()-i*24*3600*1000-30*365*24*3600*1000;
	}

	
	/**
	 * 
	 */
	public static Long StringToDate(String date) {
		//Tools.log("Date="+date);
		if(date!=null){
			date=date.trim();
			if(date.equals("now"))
				return new Date().getTime();

			if(Pattern.compile("-?[0-9]+").matcher(date).matches()){
				Long nbJours=Long.parseLong(date);
				if(nbJours>0 && nbJours<10000)nbJours=-nbJours;
				if(nbJours<0) //On considere que la date est en nombre de jour depuis now
					return new Date().getTime()+nbJours*24*3600*1000;
				else
					return(nbJours); //On �tait d�j� en format millisecond
			}else{
				String s=Tools.dateParser(date);
				if(s!=null)
					return new DateTime(s).getMillis();
			}
		}
		//Tools.log("le champs "+date+" n'est pas valid� comme une date");
		return null;
	}

	

	/**
	 * Supprime les contenu entre guillement
	 * Cette fonction est utilis� pour d�tecter les variables dans un script de parser
	 * @param s
	 * @return
	 */
	public static String removeString(String s) {
		Pattern reg=Pattern.compile("\".*\"");
		Matcher matcher=reg.matcher(s);
		while(matcher.find()){
			s=s.replaceAll(matcher.group(), "");
		}
		return s;
	}



	/**
	 * Retourne la difference entre deux date en heure

	 * @return diff�rence entre deux date en heure
	 */
	public static Long dateDiff(Long d1, Long d2) {
		if(d1==null)d1=0L;
		if(d2==null)d2=0L;
		Long delay=Math.abs(d1-d2)/(3600*1000);
		return delay;
	}


	static DateTimeParser[] parsers = {
		DateTimeFormat.forStyle("SS").getParser(),
		DateTimeFormat.forStyle("LL").getParser(),
		DateTimeFormat.forStyle("MM").getParser(),
		DateTimeFormat.forStyle("FF").getParser(),
		DateTimeFormat.forPattern( "yyyy-MMMM-dd" ).getParser(),
		DateTimeFormat.forPattern( "dd-MM-yyyy" ).getParser(),
		DateTimeFormat.forPattern( "dd-MM-yyyy hh:mm:ss" ).getParser(),
		DateTimeFormat.forPattern( "dd-MM-yy" ).getParser(),
		DateTimeFormat.forPattern( "yyyy-MMM-dd" ).getParser(),
		DateTimeFormat.forPattern( "yyyy MMMM dd" ).getParser(),
		DateTimeFormat.forPattern( "yy MMMM dd" ).getParser(),
        DateTimeFormat.forPattern( "yyyy-MM-dd" ).getParser(),
        DateTimeFormat.forPattern( "yyyy-MM-dd HH:mm" ).getParser(),
        DateTimeFormat.forPattern( "yyyy-MM-dd HH:mm:ss" ).getParser(),
        DateTimeFormat.forPattern( "yy-MM-dd" ).getParser(),
        DateTimeFormat.forPattern( "yy-MMM-dd" ).getParser(),
        DateTimeFormat.forPattern( "yy-MMMM-dd" ).getParser(),
        DateTimeFormat.forPattern( "MMMM dd yyyy" ).getParser(),
        DateTimeFormat.forPattern( "dd MMMM yyyy" ).getParser(),
        DateTimeFormat.forPattern( "ddMMMyyyy" ).getParser()
        };
	
	static DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
	
	public static String traduit(String s,String lang){
		String dic="Aug=Ao�t,Jan=Janvier,Feb=F�vrier,Mar=Mars,March=Mars,Apr=Avril,Avr=Avril,May=Mai,Jui=Juin,Jul=Juillet,Sep=Septembre,Oct=Octobre,Nov=Novembre,Dec=D�cembre";
		for(String mot:dic.split(","))
			s=s.replaceAll("-"+mot.split("=")[0]+"-", "-"+mot.split("=")[1]+"-");
		
		return s;
	}
	
	
	//TODO : fonction a am�liorer
	public static String dateParser(String value) {
		
		//value=traduit(value,"EN");		
		value=value.replaceAll("/", "-").replaceAll("//.","-").toLowerCase();
		value=value.replaceAll(" cest", "").replaceAll("cet", "").replaceAll("pst","").trim();
		
		DateTimeFormatter frenchFmt = formatter.withLocale(Locale.FRENCH);
		DateTimeFormatter englishFmt = formatter.withLocale(Locale.ENGLISH);
		
		try{
			value=frenchFmt.parseDateTime(value).toString();
			return value;
		} catch (Exception e){}				
		
		
		try{
			value=englishFmt.parseDateTime(value).toString();
			return value;
		} catch (Exception e){}
		
		return null;

	}


	/**
	 * Retourne le nombre de jours depuis l'extraction de la date
	 * @param dtMesure
	 * @return
	 */
	public static long getDelay(Long dtMesure) {
		return (dateDiff(StringToDate("now"),dtMesure))/24;
	}



	/**
	 * Distance entre points exprim�s en degr�s
	 * @param lat1
	 * @param lon1
	 * @param lat2
	 * @param lon2
	 * @param unit
	 * @return distance entre les points de lat1,lon1 et lat2,lon2
	 */
	public static double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
		  double theta = lon1 - lon2;
		  double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
		  dist = Math.acos(dist);
		  dist = rad2deg(dist);
		  dist = dist * 60 * 1.1515;
		  if (unit == 'K') {
		    dist = dist * 1.609344;
		  } else if (unit == 'N') {
		  	dist = dist * 0.8684;
		    }
		  return (dist);
		}

		/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
		/*::  This function converts decimal degrees to radians             :*/
		/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
		private static double deg2rad(double deg) {
		  return (deg * Math.PI / 180.0);
		}

		/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
		/*::  This function converts radians to decimal degrees             :*/
		/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
		private static double rad2deg(double rad) {
		  return (rad * 180.0 / Math.PI);
		}
	

}
