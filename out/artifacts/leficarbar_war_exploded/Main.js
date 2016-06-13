const DOMAIN="http://localhost:8080";FACEBOOK_ID="487838031409071";
//DOMAIN="http://162.168.0.11:8080";FACEBOOK_ID="605906262906911";
//const DOMAIN="https://opt-adopt.appspot.com";FACEBOOK_ID="597602987070572";
ROOT_API=DOMAIN+'/_ah/api';


var email="";

function loadScript(url, callback){   // Adding the script tag to the head as suggested before
    var head = document.getElementsByTagName('head')[0];
    var script = document.createElement('script');
    script.type = 'text/javascript';
    script.src = url;
    script.onreadystatechange = callback;
    script.onload = callback;
    head.appendChild(script);
}

function encode_utf8(s) {
    return unescape(encodeURIComponent(s));
}

function decode_utf8(s) {
    return decodeURIComponent(escape(s));
}

function message(s){
    if(s.substr(0,1)=="!")
        s="<img src='wait.gif'></img>"+ s.substr(1, s.length-1);
    var zone=document.getElementById("message");
    if(zone!=null)zone.innerHTML=s;
}


function init(){
    if(gapi.client==null){
        console.log("GAPI loading failed");
    }else{
        gapi.client.load('ficarbar', 'v1', function(){
            console.log("gapi loaded. Start call");
            start();
        }, ROOT_API);
    }
}



function refreshGame(idgame,func){
    gapi.client.irl.refreshgame({idgame:idgame}).then(func);
}

function sendPhoto(email,photo,func){
    var req= gapi.client.request({
        path: ROOT_API+'/irl/v1/sendphoto',
        method: 'POST',
        params: {email:email},
        body:{"photo":photo}
    });
    req.execute(func);
}



function utf8_to_b64(str) {
    return btoa(encodeURIComponent(str).replace(/%([0-9A-F]{2})/g, function(match, p1) {
        return String.fromCharCode('0x' + p1);
    }));
}


function httpGetAsync(theUrl,cred,callback,callbackerror) {
    var xmlHttp = createCORSRequest("GET",theUrl);
    xmlHttp.withCredentials=cred;
    xmlHttp.onreadystatechange = function() {
        if (xmlHttp.readyState == 4 && xmlHttp.status == 200)
            if(callback!=undefined)
                callback(xmlHttp.responseText);
    };
    xmlHttp.send(null);
}



function getParam(param) {
    var vars = {};
    window.location.href.replace( location.hash, '' ).replace(
        /[?&]+([^=&]+)=?([^&]*)?/gi, // regexp
        function( m, key, value ) { // callback
            vars[key] = value !== undefined ? value : '';
        }
    );
    return vars;
}



function to2D(p){
    var x= p.lng;
    var y=Math.atan(Math.sin(p.lat));
    return {x:x,y:y};
}



function mkTest(idtest){
    httpGetAsync(ROOT_API+'/irl/v1/test?id='+idtest,null,function(resp){
        //r=JSON.parse(resp);
        document.write(resp);
        //document.location.href="start.html?email="+r.items[0];
    },function(err){
        document.write("Erreur d'execution : "+err);
    });
}


loadScript("https://apis.google.com/js/client.js?onload=init",function(){
//loadScript(DOMAIN+"/client.js?onload=init",function(){
    console.log("Chargement de tous les scripts ok");
});

