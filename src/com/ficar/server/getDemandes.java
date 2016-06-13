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
 */
@SuppressWarnings("serial")
public class getDemandes  {
	//TODO passer en m�thode post pour la s�curit�
	/*
    public void doGet(HttpServletRequest req, final HttpServletResponse resp) throws IOException {
		String date=req.getParameter("date");
		Event e=dao.findEvent(req);
		
		String lastDemande=null;
		if(req.getSession().getValue("lastDemande")!=null)lastDemande=req.getSession().getValue("lastDemande").toString();
		
		if(lastDemande==null){
			req.getSession().putValue("lastDemande","0");
			lastDemande="0";
		}
		
		
		if(date!=null){
			Long lg=Long.parseLong(lastDemande);
			if(DAO.lastDemande>=lg){
				List<Demande> rc=dao.getDemandeSince(e,Long.parseLong(date));
				Collections.sort(rc);
				dao.lastDemande=lg;
				req.getSession().putValue("lastDemande", lg.toString());
				this.returnJSON(resp, rc);	
			} else this.returnString(resp, "[]");
		}else
			this.returnError(resp, "date missing");
		
	}*/

}
