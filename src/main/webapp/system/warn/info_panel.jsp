
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<div id=warnResultShow>
<table id='warnInfoTable' width="100%"height="0" border="0" cellspacing="0" align="center" cellpadding="1" class="ListTable">
<script type="text/javascript" language="javascript">
var marqueeContent=new Array();
var marqueeInterval=new Array();

function initMarquee(outparamters){
	var tempArray=outparamters.getValue("userViewResult");
	if(typeof(tempArray)=="undefined"||tempArray.length<1){
		Element.hide('warnResultShow');
		return;
	}
	document.write('<thead>');
	document.write('<tr>');
	document.write('	<td align="center" class="TableRow" nowrap><a href="/system/warn/info_all.do?br_query=link">预警信息(共'+tempArray.length+'项)</a></td>');
	document.write('</tr>');
	document.write('</thead>');
	var lines=tempArray.length>10?10:tempArray.length;
	for(var i=0;i<lines;i++) {
		marqueeContent[i]='<font siz=1><a href="/system/warn/result_manager.do?b_query=link&warn_wid='+tempArray[i].dataValue+'">'+(i+1)+'、'+tempArray[i].dataName+'</a></font>';
		document.write('<tr class="'+(i%2==0?'trDeep':'trShallow')+'"><td class="RecordRow">'+marqueeContent[i]+'</td></tr>');
	}
	if(tempArray.length>10){
		document.write('<tr class="'+(i%2==0?'trDeep':'trShallow')+'"><td class="RecordRow" align="right"><a href="/system/warn/info_all.do?br_query=link">>>更多</a></td></tr>');
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
</table>
</div>
