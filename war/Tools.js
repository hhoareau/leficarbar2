
//test : http://localhost:8888/ParserDesign.html?idMail=12888&from=noreply@voyages-sncf.com&idClient=161132

var email_pattern = /^\w+@[a-zA-Z_]+?\.[a-zA-Z]{2,3}$/;
var char_perso="_";
var sep_enreg="sepenreg";

if (typeof String.prototype.startsWith != 'function') {
	  // see below for better implementation!
	  String.prototype.startsWith = function (str){
	    return this.indexOf(str) == 0;
	  };
	}


/**
 * Fonction de copy vers le presse-papier
 * @param obj
 */
function copyToClipboard(obj) {
	window.prompt('Press CTRL+C, then ENTER',obj);
}

function startFullScreen(){
	//Fullscreen mode
	var docElm = document.documentElement;
	if (docElm.requestFullscreen) {
	    docElm.requestFullscreen();
	}
	else if (docElm.mozRequestFullScreen) {
	    docElm.mozRequestFullScreen();
	}
	else if (docElm.webkitRequestFullScreen) {
	    docElm.webkitRequestFullScreen();
	}
}


/**
 * Fonction d'affichage d'une info-bulle
 * @see http://www.c-p-f.org/javascript-Des_infobulles_etendues-a27.html
 * @param id
 * @param eventObj
 */
function more(id, eventObj) {
	   // Cette fonction va afficher l'infobulle
	   var scrOfX = 0, scrOfY = 0;
	   div = eval(document.getElementById(id));
	   // On récupère les données sur le bloc à afficher
	   // La suite permet de choisir l'endroit où l'on affiche (les méthodes sont différentes en fonction des navigateurs)
	   if (typeof(window.pageYOffset) == 'number' ) {
	      //Netscape compliant
	      scrOfY = window.pageYOffset + 5;
	      scrOfX = window.pageXOffset + 10;
	   } else if (document.body && (document.body.scrollLeft || document.body.scrollTop)) {
	      //DOM compliant
	      scrOfY = document.body.scrollTop + 5;
	      scrOfX = document.body.scrollLeft + 10;
	   } else if (document.documentElement && (document.documentElement.scrollLeft || document.documentElement.scrollTop)) {
	      //IE6 standards compliant mode
	      scrOfY = document.documentElement.scrollTop + 5;
	      scrOfX = document.documentElement.scrollLeft + 10;
	   }
	   // Nous avons désormais les positions d'affichages
	   if (div) {
	      // L'id envoyée correspond bien à un bloc, on modifie son style
	      div.style.left = (eventObj.clientX + scrOfX) + "px"; // Sa position horizontale
	      div.style.top = (eventObj.clientY + scrOfY) + "px"; // Sa position verticale
	      div.style.display = 'block'; // Passé en "block" le fait afficher
	   }
	}

/**
 * Masquage de l'info bulle
 */
	function less(id) {
	   // Cette fonction cache l'infobulle
	   div = eval(document.getElementById(id));
	   if (div) {div.style.display='none';}
	}


var dtDoc=new Date("01/01/2013").getTime();
function getArrayFromString(s){
	if(s==null)return(null);
	else{
		var t=s.split('&');
		var f = [];
		for (var i=0; i<t.length; i++) {
			var x = t[ i ].split('=');
			f[x[0]]=decodeURIComponent(escape(x[1]));
		}
		return f;	
	}
}

function toDate(s){
	var rc=new Date(s);
	if(rc==null)
		return s
	else
		return rc.toUTCString();
}

function matchreg(doc,reg){
	var regexp=new RegExp(reg,"gi");
	return doc.match(regexp);
}





function URLDecode(url) {
	if(typeof(url)!="undefined")
		return decodeURIComponent(url.replace(/\+/g, ' '));
	else
		return "";
}

function URLEncode(url) {
	  return escape(url);
	}



function getParameters() {
	var t = location.search.substring(1);
	var rc=getArrayFromString(t);
	return rc;
}


function addElement(lstId,eltLib,eltValue){
	lst=document.getElementById(lstId);
	for(i=0;i<lst.options.length;i++){
		opt=lst.options[i];
		if(opt.value===eltValue && opt.text===eltLib)return;
	}
	lst.options[lst.options.length]=new Option(eltLib,eltValue,true,true);
}


var idTimeout;

function informe(s,waiting,zone){
	
	zone=zone || "lblMessage";
	
	if(s==null || s==undefined){
		s=getParameters()["message"];
		if(s!=undefined)
			while(s.indexOf("%20")!=-1)s=s.replace("%20"," ");
	}
	
	var lblMessage=document.getElementById(zone);
	if(lblMessage==null){
		document.body.innerHTML="<div id='"+zone+"'></div>"+document.body.innerHTML;
		lblMessage=document.getElementById(zone);
	}
	
	lblMessage.innerHTML="<img id='pctWaiting' src='./wait.gif'></img>";
	var pctWaiting=document.getElementById("pctWaiting");
	
	if(waiting){
		pctWaiting.style.visibility="visible";
		window.clearTimeout(idTimeout);
	}else{
		pctWaiting.style.visibility="hidden";
		idTimeout=window.setTimeout(function(){lblMessage.innerHTML="<br>";},4000);
	}		
	
	lblMessage.innerHTML+=""+s+"";	
}

function sendForm(url,formid,onEnd){
	
	
	var frm=document.getElementById(formid);
	
	var code=url+"?";
	var data=null;
	for(var i=0;i<frm.elements.length;i++){
		var elt=frm.elements[i];
		if(elt.name.length>0){
			var value=elt.value;
			if(elt.type=="checkbox")value=elt.checked;
			code+=elt.name+"="+value+"&";
		}
		
		if(elt.type=="file")data=elt;
	}
	var reader = new FileReader();
	reader.onload = (function(theFile) {
		httpPost(code,this.result,onEnd);
	});
	
	if(data.files.length==1)
		reader.readAsDataURL(data.files[0]);
	else
		httpPost(code,null,onEnd);
}



function findFirstMail(str){
	var i=str.indexOf("@");
	var start=i, end=i;
	while(str[start]!=" " && str[start]!="<" && start>0)
		start--;
	
	while(str[end]!=" " && str[end]!=">" && end<str.length)
		end++;
	
	return str.substr(start+1,end-start-1);
}





function getHTMLCode(jsonText,libelles,action){
	var code="";
	if(action!=null)
		code+="<form accept-charset='utf-8' method=GET action='"+action+"'>";
	
	code+="<table><TR>";
		
	var libs=getArrayFromString(libelles);
	
	if(typeof jsonText == 'string')
		var source=JSON.parse(jsonText);
	else
		var source=jsonText;
	
	var i=0;
	var lib="";
	for(var pName in source){
		
		if(libs==null)
			lib=pName;
		else
			lib=libs[pName];
		
		
		if(lib!=null){
			code+="<tr><td><strong>"+lib+" : </strong></td><td>";
			
			if(action!=null){
				code+="<input type='text' name='"+pName+"' value='"+eval("source."+pName)+"'>";	
			} else{
				code+=eval("source."+pName);
			}
				
			code+="</td></tr>";
			
		}
			
		i++;
	}
	
	code+="</tr></table>";

	if(action!=null)code+="<INPUT type=submit value='Envoyer'></form>";

	return code;
}


function getListHTMLCode(json,func,cols,precode){
	
	json=json||null;
	
	if(precode!=null){
		code="";
		if(precode.indexOf("<table><tr>")==-1)precode=null
	}
	
	if(precode==null)code="<table><tr>";
	
	if(json!=null){
		json.forEach(function(obj){
			var i=0;
			if(func==null){
				for(var pName in obj){
					if(cols==null || cols.indexOf(pName+",")!=-1)
						code+=" "+eval("obj."+pName)+"<br>";
				}
			}
			else
				code+=func(obj);
		});	
	}
	
	if(precode==null)
		return code+"</tr></table>";
	else
		return precode.replace("</tr></table>",code); //Ajoute a la fin
}


function include(arr,obj) {
    return (arr.indexOf(obj) != -1);
}

function findElt(arr,obj) {
    return (arr[arr.indexOf(obj)]);
}


Array.prototype.clear = function() {
    this.splice(0, this.length);
}


function httpPost(theUrl,data,onSuccess){

	if(window.location.host.indexOf("localhost")!=-1)
		theUrl="http://"+window.location.host+"/"+theUrl;
	else
		if(theUrl.indexOf("http")!=0)theUrl="https://"+window.location.host+"/"+theUrl;

	var xmlHttp = new XMLHttpRequest();
		
	xmlHttp.onreadystatechange=function(){
		if(xmlHttp.readyState==4)
			if(xmlHttp.status==200)
				onSuccess(xmlHttp.responseText);
	};
	
	xmlHttp.open("POST",theUrl);
	/*
	xmlHttp.setRequestHeader("Content-Type", "multipart/form-data");
	xmlHttp.setRequestHeader("Cache-Control", "no-cache");
	xmlHttp.setRequestHeader("X-Requested-With", "XMLHttpRequest");
	xmlHttp.setRequestHeader("X-File-Name", file.name);
	*/
	xmlHttp.setRequestHeader("Content-Type", "application/octet-stream");
	
	if(data==null)
		xmlHttp.send();
	else
		xmlHttp.send(data);
}



function httpGet(theUrl,onSuccess,onError,synchrone){

	onError=onError||null;
	synchrone=synchrone||false;
	
	if(window.location.host.indexOf("localhost")!=-1){
		if(theUrl.indexOf("http")==-1)
			theUrl="http://"+window.location.host+"/"+theUrl;
	}
	else
		if(theUrl.indexOf("http")!=0)theUrl="https://"+window.location.host+"/"+theUrl;
	
var xmlHttp= new XMLHttpRequest();

xmlHttp.onreadystatechange=function(){
		if(xmlHttp.readyState==4){
			if(xmlHttp.status==200)
				onSuccess(xmlHttp.responseText);
			else {
				if(onError!=null)
					onError(xmlHttp.responseText);
				else
					informe(xmlHttp.responseText,false);
			}				
		}
	}		

xmlHttp.open( "GET", theUrl, !synchrone );
xmlHttp.send( null );
}



function extract(str,start,end){
	var i=str.indexOf(start)+start.length;
	var j=str.length;
	
	if(i<start.length)i=0;
	
	if(end.length>0){
		j=str.indexOf(end,i);
		if(j==-1)return("");
	}	
	return(str.substring(i,j));
}



function caracteresSpeciaux(s){
var speciaux="[](){}-.*?$";
for(var i=0;i<speciaux.length;i++){
	var ch=speciaux.charAt(i);
	s=s.replace(ch,"\\"+ch);
}
return s;
}


function StringToDate(date) {
	if(date!=null){
		if(date==="now")
			return new Date().getTime();
		
		if(date.indexOf("/")!=-1) //Si date au format text
			//TODO fonction à analyser en fonction des formats
			return new Date(date).getTime();
		else {
			if(date<0)
				return new Date().getTime()+date*24*3600*1000;
			else
				return(date);
		}
	} else
		return null;
}




function mkDate(lg,format){
	if(lg==null)lg=Date.now();
	var date=new Date(lg)
	if (format==null)
		return date.getDate()+"/"+(date.getMonth()+1)+"/"+date.getFullYear();
	else{
		var month=date.getMonth()+1>9 ? date.getMonth()+1 : "0"+(date.getMonth()+1);
		var day=date.getDate()>9 ? date.getDate() : "0"+date.getDate();
		return date.getFullYear()+"-"+month+"-"+day;
	}
		
}


function setCookie(c_name,value,exdays)
{
var exdate=new Date();
exdate.setDate(exdate.getDate() + exdays);
var c_value=escape(value) + ((exdays==null) ? "" : "; expires="+exdate.toUTCString());
document.cookie=c_name + "=" + c_value;
}


function getCookie(c_name){
var c_value = document.cookie;
var c_start = c_value.indexOf(" " + c_name + "=");
if (c_start == -1)
  {
  c_start = c_value.indexOf(c_name + "=");
  }
if (c_start == -1)
  {
  c_value = null;
  }
else
  {
  c_start = c_value.indexOf("=", c_start) + 1;
  var c_end = c_value.indexOf(";", c_start);
  if (c_end == -1)
  {
c_end = c_value.length;
}
c_value = unescape(c_value.substring(c_start,c_end));
}
return c_value;
}




function addText(ctx,x,y,color,textsize,text,onEnd,maxlx){
	ctx.lineWidth=1;
	ctx.fillStyle=color;
	ctx.lineStyle=color;
	ctx.font="bold "+textsize+"px 'Indie Flower' cursive";
	
	var metrics=ctx.measureText(text);
	if(maxlx && metrics.width>maxlx){
		var pos=text.length/2;
		while(text.substring(pos,pos+1)!=" " && pos>0)pos--;
		var s=text.substring(0,pos);
		ctx.fillText(s, x-5, y);
		ctx.fillText(text.substring(pos+1), x-5, y+textsize+4);
	} else
		ctx.fillText(text, x-5, y+textsize/2);

	if(onEnd!=null)onEnd(metrics.width);
}









function getUserFromMail(email,zone,size){
	httpGet("getUsers?users="+email,function(rep){
		var u=JSON.parse(rep);
		getUser(u[0],function(rep){
			document.getElementById(zone).innerHTML=rep;
		},size);
	});
}


function getUser(u,onEnd,size){
	var rc="<div style='text-align:center;font-size:11px;margin:0px;' id='user_"+u.Id+"'>";
	rc+="<a href='https://www.facebook.com/"+u.facebookid+"'>";
	if(u.photo!=null)
		rc+="<img style='max-width:"+size+"px;' src='"+u.photo+"'></a><br>"+u.name+"</div>";
	else
		rc+=u.name;
	
	onEnd(rc+"</a></div>");	
}


function showPlaylist(evt){
	if(evt!=null){
		var div=document.getElementById("playlist");
		var code="<table>";
		evt.playlist.forEach(function(song){
			if(song.dtPlay==null){
				getUserFromMail(song.from,"zone_"+song.title,30);
				code+="<tr><td>"+song.title+" by </td><td><div id='zone_"+song.title
					+"'></div></td><td><a href='javascript:onSetScore(1,\""+song.url
					+"\")'><img style='width:20px;' src='arrow_up.png'></a><br><a href='javascript:onSetScore(-1,\""
					+song.url+"\")'><img style='width:20px;' src='arrow_down.png'></a></td><td>"+song.score+"</td></tr>";		
			}		
		});
		code+="</table>";
		div.innerHTML=code;
	}
}


function resize_image(i,maxlen,angle){
	var ratio=Math.max(i.width,i.height);
	ratio=maxlen/ratio;
	
	var canvas = document.createElement("canvas");
	var ctx=canvas.getContext('2d');
	
	var radian=angle*Math.PI/180;
	
	var lx=i.width;
	var ly=i.height;
	
	canvas.width=lx*ratio;
	canvas.height=ly*ratio;
	
	ctx.rotate(radian);
	//ctx.scale(ratio,ratio);
	//ctx.rect(0,0,lx*2,ly*2);
	ctx.drawImage(i,0,0,lx,ly,0,0,lx*ratio,ly*ratio);
	
	//return ctx.getImageData(0,0,maxlen,maxlen);
	
	return canvas.toDataURL("image/png");
}






























//http://stackoverflow.com/questions/2303690/resizing-an-image-in-an-html5-canvas
//returns a function that calculates lanczos weight
function lanczosCreate(lobes){
  return function(x){
    if (x > lobes) 
      return 0;
    x *= Math.PI;
    if (Math.abs(x) < 1e-16) 
      return 1
    var xx = x / lobes;
    return Math.sin(x) * Math.sin(xx) / x / xx;
  }
}

//elem: canvas element, img: image element, sx: scaled width, lobes: kernel radius
function thumbnailer(elem, img, sx, lobes){ 
    this.canvas = elem;
    elem.width = img.width;
    elem.height = img.height;
    elem.style.display = "none";
    this.ctx = elem.getContext("2d");
    this.ctx.drawImage(img, 0, 0);
    this.img = img;
    this.src = this.ctx.getImageData(0, 0, img.width, img.height);
    this.dest = {
        width: sx,
        height: Math.round(img.height * sx / img.width),
    };
    this.dest.data = new Array(this.dest.width * this.dest.height * 3);
    this.lanczos = lanczosCreate(lobes);
    this.ratio = img.width / sx;
    this.rcp_ratio = 2 / this.ratio;
    this.range2 = Math.ceil(this.ratio * lobes / 2);
    this.cacheLanc = {};
    this.center = {};
    this.icenter = {};
    setTimeout(this.process1, 0, this, 0);
}

thumbnailer.prototype.process1 = function(self, u){
    self.center.x = (u + 0.5) * self.ratio;
    self.icenter.x = Math.floor(self.center.x);
    for (var v = 0; v < self.dest.height; v++) {
        self.center.y = (v + 0.5) * self.ratio;
        self.icenter.y = Math.floor(self.center.y);
        var a, r, g, b;
        a = r = g = b = 0;
        for (var i = self.icenter.x - self.range2; i <= self.icenter.x + self.range2; i++) {
            if (i < 0 || i >= self.src.width) 
                continue;
            var f_x = Math.floor(1000 * Math.abs(i - self.center.x));
            if (!self.cacheLanc[f_x]) 
                self.cacheLanc[f_x] = {};
            for (var j = self.icenter.y - self.range2; j <= self.icenter.y + self.range2; j++) {
                if (j < 0 || j >= self.src.height) 
                    continue;
                var f_y = Math.floor(1000 * Math.abs(j - self.center.y));
                if (self.cacheLanc[f_x][f_y] == undefined) 
                    self.cacheLanc[f_x][f_y] = self.lanczos(Math.sqrt(Math.pow(f_x * self.rcp_ratio, 2) + Math.pow(f_y * self.rcp_ratio, 2)) / 1000);
                weight = self.cacheLanc[f_x][f_y];
                if (weight > 0) {
                    var idx = (j * self.src.width + i) * 4;
                    a += weight;
                    r += weight * self.src.data[idx];
                    g += weight * self.src.data[idx + 1];
                    b += weight * self.src.data[idx + 2];
                }
            }
        }
        var idx = (v * self.dest.width + u) * 3;
        self.dest.data[idx] = r / a;
        self.dest.data[idx + 1] = g / a;
        self.dest.data[idx + 2] = b / a;
    }

    if (++u < self.dest.width) 
        setTimeout(self.process1, 0, self, u);
    else 
        setTimeout(self.process2, 0, self);
};
thumbnailer.prototype.process2 = function(self){
    self.canvas.width = self.dest.width;
    self.canvas.height = self.dest.height;
    self.ctx.drawImage(self.img, 0, 0);
    self.src = self.ctx.getImageData(0, 0, self.dest.width, self.dest.height);
    var idx, idx2;
    for (var i = 0; i < self.dest.width; i++) {
        for (var j = 0; j < self.dest.height; j++) {
            idx = (j * self.dest.width + i) * 3;
            idx2 = (j * self.dest.width + i) * 4;
            self.src.data[idx2] = self.dest.data[idx];
            self.src.data[idx2 + 1] = self.dest.data[idx + 1];
            self.src.data[idx2 + 2] = self.dest.data[idx + 2];
        }
    }
    self.ctx.putImageData(self.src, 0, 0);
    self.canvas.style.display = "block";
}