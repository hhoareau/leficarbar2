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

/**
 * Ajoute ou remplace un parser dans la base
 * @author Studio
 * 
 * test : http://localhost:8888/addMessage?from=hhoareau@gmail.com&title=titre&body=body
 * http://localhost:8888/addMessage?from=hhoareau@gmail.com&title=salut+les+cocos&body=https://docs.google.com/file/d/0B1XHEhVj9zBDQ3QzT3ItcXlLdW8/edit?usp=sharing
 * 
 */
@SuppressWarnings("serial")
public class respons  {
	//TODO passer en m�thode post pour la s�curit�
	/*
    public void doGet(HttpServletRequest req, final HttpServletResponse resp) throws IOException {

		String from=req.getParameter("from");
		String to=req.getParameter("to");
		String nature=req.getParameter("nature");
		String accept=req.getParameter("accept");
		
		User u=dao.findUser(from);
		Event e=dao.findEvent(req.getParameter("event"));
		if(u!=null){
			User dest=dao.findUser(to);
			String s=e.getDemande(nature);
			Demande d=dest.setDemande(e,from,nature,
						accept.equals("true"),
						Tools.extract(s, "acceptIcon=", "\r\n", false),
						Tools.extract(s, "refuseIcon=", "\r\n", false),
						Tools.extract(s, "acceptPhoto=", "\r\n", false),
						Tools.extract(s, "refusePhoto=", "\r\n", false)
						);
			if(d!=null){
				dao.save(dest);
				dao.save(new Message(d,u,dest));
				returnJSON(resp,dest);
				return;		
			}
		}
		returnString(resp,"");
	}
	*/
}
