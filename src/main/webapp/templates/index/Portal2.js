
function openwinMuster(url){
	window.parent.frames.location.href=url;
}
function openwins(id){
       		
     	var waitInfo=eval("wait");	   
        waitInfo.style.display="block";
	   	var map = new HashMap();
        map.put("ispriv","1");
        map.put("id",id);
        var u = realurl;
       		 map.put("realurl",u);
        Rpc({functionId:'10100103413',success:showSelect},map);
	}
	
	function showSelect(response) { 
     var waitInfo=eval("wait");	   
     waitInfo.style.display="none";
     	var value=response.responseText;
		var map=Ext.util.JSON.decode(value);
     var url = map.url;
     var filename =  map.filename;
     url = url + "?filename=" +filename;
     var html = document.getElementById("htmlparam");
     if(map.htmlparam){
     html.value = getDecodeStr(map.htmlparam);
     }
     window.open(url,"_blank","left=0,top=0,width="+screen.availWidth+",height="+screen.availHeight+",scrollbars=yes,toolbar=no,menubar=no,location=no,resizable=no,status=no");
     
  	}
	function openwin(id){
        var url = id;
		var iframe_url="/general/query/common/iframe_query.jsp?src="+url;
	  window.showModalDialog(iframe_url,"","dialogWidth="+window.screen.width+"px;dialogHeight="+window.screen.height+"px;resizable=yes;scroll=no;status=no;"); 
	  
	   
	}
	function decode(strIn)
{
	var intLen = strIn.length;
	var strOut = "";
	var strTemp;

	for(var i=0; i<intLen; i++)
	{
		strTemp = strIn.charAt(i);
		switch (strTemp)
		{
			case "~":{
				strTemp = strIn.substring(i+1, i+3);
				strTemp = parseInt(strTemp, 16);
				strTemp = String.fromCharCode(strTemp);
				strOut = strOut+strTemp;
				i += 2;
				break;
			}
			case "^":{
				strTemp = strIn.substring(i+1, i+5);
				strTemp = parseInt(strTemp,16);
				strTemp = String.fromCharCode(strTemp);
				strOut = strOut+strTemp;
				i += 4;
				break;
			}
			default:{
				strOut = strOut+strTemp;
				break;
			}
		}

	}
	return (strOut);
}


function getDecodeStr(str) {
	return ((str)?decode(getValidStr(str)):"");
}
function getValidStr(str) 
{
	str += "";
	if (str=="undefined" || str=="null" || str=="NaN")
		return "";
	else
		return reNew(str);
		
}
function reNew(str)
{
    var re;
	re=/%26amp;/g;
	str=str.replace(re,"&");
	re=/%26apos;/g;  
	str=str.replace(re,"'");
	re=/%26lt;/g;  
	str=str.replace(re,"<");
	re=/%26gt;/g;  
	str=str.replace(re,">");
	re=/%26quot;/g;  
	str=str.replace(re,"\"");
	re=/%25/g;
	str=str.replace(re,"%");
	re=/````/g;
	str=str.replace(re,",");
	return(str);		
}	