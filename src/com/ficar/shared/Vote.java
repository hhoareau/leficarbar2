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
public class Vote implements Serializable {

	private static final long serialVersionUID = 1440513031923567299L;

	protected static final Logger log = Logger.getLogger(Vote.class.getName());
	
	//@Id public String Id; 					//Id interne des Users (adresse mail)
	
	public String votant;						//Nom du User
	public String caracteristique; 
	public Integer score;
	public Long dtVote=Tools.StringToDate("now");

	public Vote(String votant,String cible,String cara,Integer note){
		//this.Id=votant+cible+cara;
		this.caracteristique=cara;
		this.votant=votant;
		this.score=note;
	}
	
	public Vote(){}
	

	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((caracteristique == null) ? 0 : caracteristique.hashCode());
		result = prime * result + ((votant == null) ? 0 : votant.hashCode());
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
		Vote other = (Vote) obj;
		if (caracteristique == null) {
			if (other.caracteristique != null)
				return false;
		} else if (!caracteristique.equals(other.caracteristique))
			return false;
		if (votant == null) {
			if (other.votant != null)
				return false;
		} else if (!votant.equals(other.votant))
			return false;
		return true;
	}

	/**
	 * Format destin� au debugeur
	 */
	public String toString(){
		String rc=this.caracteristique+":"+this.score;		
		return rc;
	}


	
}
