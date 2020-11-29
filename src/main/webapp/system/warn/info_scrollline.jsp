
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<div id=warnResultShow>
<table width="100%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
<thead>
	<tr>
		<td align="center" class="TableRow" nowrap><label id="warnInfoTitile"></label></td>		
	</tr>
</thead>
<tr>
	<td>
<script type="text/javascript" language="javascript">
var marqueeContent=new Array();
var marqueeInterval=new Array();
var marqueeId=0;
var isStopScoll=false;
var marqueeDelay='4000';
var marqueeHeight='16';
var marqueeWidth='300';

function initMarquee(outparamters){
	var tempArray=outparamters.getValue("userViewResult");
	if(typeof(tempArray)=="undefined"||tempArray.length<1){
		Element.hide('warnResultShow');
		return;
	}
	document.getElementById("warnInfoTitile").innerHTML+='<a href=\"/system/warn/info_all.do?br_query=link\">预警信息(共'+tempArray.length+'项)</a>';

	for(var i=0;i<tempArray.length;i++) {
		marqueeContent[i]='<font siz=1><a href="/system/warn/result_manager.do?b_query=link&warn_wid='+tempArray[i].dataValue+'">'+(i+1)+'、'+tempArray[i].dataName+'</a></font>';
	}
	var str=''+marqueeContent[0];
	document.write('<div id=marqueeBox style="overflow:hidden;height:'+marqueeHeight+'px;width:'+marqueeWidth+'px" onmouseover="stopScroll()" onmouseout="startScroll()"><div>'+str+'</div></div>');
	if(marqueeContent.length>0)marqueeInterval[0]=setInterval("startMarquee()",marqueeDelay);
}

function stopScroll() {
	isStopScoll=true;
}

function startScroll() {
	isStopScoll=false;
}

function reinitMarquee() {
	if(isStopScoll==true)return;
	var str=marqueeContent[0];
	marqueeBox.childNodes[(marqueeBox.childNodes.length==1?0:1)].innerHTML=str;
	marqueeId=0;
}

function startMarquee() {
	if(isStopScoll==true)return;
	marqueeId+=1;
	if(marqueeId>=marqueeContent.length)marqueeId=0;
	var str=marqueeContent[marqueeId];

	if(marqueeBox.childNodes.length==1) {
		var nextLine=document.createElement('DIV');
		nextLine.innerHTML=str;
		marqueeBox.appendChild(nextLine);
	}else{
		marqueeBox.childNodes[0].innerHTML=str;
		marqueeBox.appendChild(marqueeBox.childNodes[0]);
		marqueeBox.scrollTop=0;
	}
	clearInterval(marqueeInterval[1]);
	marqueeInterval[1]=setInterval("scrollMarquee()",20);
}

function scrollMarquee() {
	if(isStopScoll==true)return;
	marqueeBox.scrollTop+=2;
	if(marqueeBox.scrollTop%marqueeHeight==(marqueeHeight-1)){
		clearInterval(marqueeInterval[1]);
	}
}
function initScan() {
	var tatolPars="isRole=true";
	var request=new Request({method:'post',asynchronous:false,parameters:tatolPars,onSuccess:initMarquee,functionId:'1010020307'});
}
</script>
<script type="text/javascript" language="javascript">
	initScan();
</script>
</td>
</tr>
</table>
</div>
