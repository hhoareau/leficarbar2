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

import java.io.Serializable;
import java.util.logging.Logger;

/**
 * Cette classe permet de stocker le r�sultat du parsing HTML
 * @see 
 * @author Herv� Hoareau
 *
 */
public class Demande implements Serializable,Comparable<Demande> {

	private static final long serialVersionUID = 1440513031923567299L;

	protected static final Logger log = Logger.getLogger(Demande.class.getName());

	public static final String DEMANDE_BISOUS = "Bisou";
	public static final String BISOU_DEMANDE_ICON = "Bisou";
	public static final String BISOU_ACCEPT_ICON = "Bisou";
	public static final String BISOU_REFUSE_ICON = "Bisou";
	
	public String event_id;
	public Long dtDemande=Tools.StringToDate("now");
	public String from;						//Nom du User
	public String to;
	public String nature=this.DEMANDE_BISOUS; 
	public Boolean reponse=null;
	public String icon=this.BISOU_DEMANDE_ICON;
	public String photo="";
	

	public Demande(String idEvent,String user,String nature,String icon,String photo){
		this.from=user;
		this.nature=nature;
		this.event_id=idEvent;
		this.photo=photo;
		this.icon=icon;
	}
	
	public void setRespons(boolean b,String acceptIcon,String refuseIcon,String acceptPhoto,String refusePhoto){
		this.reponse=b;
		if(b){
			this.icon=acceptIcon; 
			this.photo=acceptPhoto;
		}
		else {
			this.icon=refuseIcon;
			this.photo=refusePhoto;
		}
	}
	
	
	public Demande(Message m) {
		this.event_id=m.idEvent;
		this.from=Tools.extract(m.getText(), "from=", ";",false);
		this.to=Tools.extract(m.getText(), "to=", ";",false);
		this.nature=Tools.extract(m.getText(), "demande=", ";",false);
		this.icon=Tools.extract(m.getText(), "icon=", ";",false);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((event_id == null) ? 0 : event_id.hashCode());
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		result = prime * result + ((nature == null) ? 0 : nature.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Demande other = (Demande) obj;
		if (event_id == null) {
			if (other.event_id != null)
				return false;
		} else if (!event_id.equals(other.event_id))
			return false;
		if (from == null) {
			if (other.from != null)
				return false;
		} else if (!from.equals(other.from))
			return false;
		if (nature == null) {
			if (other.nature != null)
				return false;
		} else if (!nature.equals(other.nature))
			return false;
		return true;
	}


	@Override
	public int compareTo(Demande o) {
		if(this.dtDemande>o.dtDemande)return -1;
		return 0;
	}

	
	
}
