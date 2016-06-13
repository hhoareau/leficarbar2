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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * Ajoute ou remplace un parser dans la base
 * @author Studio
 *
 */
@SuppressWarnings("serial")
public class getNextMedia  {
	//TODO passer en m�thode post pour la s�curit�
	public void doGet(HttpServletRequest req, final HttpServletResponse resp) throws IOException {				
		/*
        Event e=dao.findEvent(req);
		if(e!=null){
			List<Song> ls=e.getPlaylist();
			if(ls.size()>0){
				Song s=ls.get(0);
				s.dtPlay=Tools.StringToDate("now");
				e.addSong(s);
				dao.save(e);
				
				this.returnString(resp, s.url);	
			}	
		}
		*/
	}
}
