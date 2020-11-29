<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<style>
<!--
.divscan1{
	text-align: left;margin-top: 15px;margin-bottom: 20px;
	width:expression(document.body.clientWidth-60);
	height:expression(document.body.clientHeight-35); 
	text-decoration: none;
}
.divimg1{
	height: expression(document.body.clientHeight-80);
	width: expression(document.body.clientWidth-60);
	overflow: auto;
}
-->
</style>
<center>
<div class="divscan1">
	&nbsp;<b>查看扫面图像：</b>
	<table border="0" cellpadding="0" cellspacing="0" style="font-size: 12px;width: 100%;border-collapse: collapse;">
		<tr>
			<td bgcolor="#F4F7F7" class="RecordRow" style="height: 25px;border-right: 0px;">
				&nbsp;<label id="scansp" style="color:#999999;cursor:hand;">首票</label>
				&nbsp;<label id="scanqp" style="color:#999999;cursor:hand;">上票</label>
				&nbsp;<label id="scanxp" style="color:#999999;cursor:hand;">下票</label>
				&nbsp;<label id="scanmp" style="color:#999999;cursor:hand;">末票</label>
				&nbsp;&nbsp;第<label id="curscan">0</label>票/共<label id="sumscan">0</label>票
			</td>
			<td bgcolor="#F4F7F7" class="RecordRow" align="right" style="border-left: 0px;">
				&nbsp;&nbsp;&nbsp;&nbsp;<label onclick="scan_change('1')" style="cursor:hand;">放大</label>
				&nbsp;<label onclick="scan_change('0')" style="cursor:hand;">缩小</label>
				<!-- <input type="button" value="关闭" class="mybutton" onclick="javascript:window.close();"/> -->&nbsp;
			</td>
		</tr>
		<tr>
			<td align="center" valign="middle" colspan="2" class="RecordRow" style="height: expression(document.body.clientHeight-80);">
				<div class="divimg1">
					<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center"><div id="scan"><font color="#999999">(无图像)</font></div></td>
						</tr>
					</table>
				</div>
			</td>
		</tr>
	</table>
</div>
</center>
<script type="text/javascript">
<!--
	/** liweichao */
	var w=0,h=0;//图片的宽和高
	var current=0;//当前票
	var sum=0;//总票
	var scans="${inSupportCommendForm.scans}";
	if(scans==null||scans=="")
		scans=",";
	var scanArray=scans.substring(0,scans.length-1).split(",");
	sum=scanArray.length;
	if(sum>0&&scanArray[0].length>0){
		showScan(0,1);
		document.getElementById("sumscan").innerHTML=sum;
	}
	/**
	 *控制显示票
	 *前一票：cut=1， 首票：cut=-1
	 *后一票：app=1， 末票：app=-1
	 */
	function showScan(cut,app){
		//cut=1前一票
		if(cut==1&&current>1)
			current--;
		//app=1后一票
		if(app==1&&current<sum)
			current++;
		//cut=-1首票
		if(cut==-1)
			current=1;
		//app=-1末票
		if(app==-1)
			current=sum;
		//alert(cut+"--"+app+"==="+scanArray[current-1]);
		//用img显示服务器临时目录的图像
		var strImg="<img id='scan_img' src=\"/servlet/DisplayOleContent?filename="+scanArray[current-1]+"\" onmousewheel=\"scan_event(this,event);\" onload='setImg(this);' border=0 style='cursor:hand;'>";
		document.getElementById("curscan").innerHTML=current;
		document.getElementById("scan").innerHTML=strImg;
		
		if(current==1){
			document.getElementById("scansp").onclick="";
			document.getElementById("scansp").style.color="#999999";
			document.getElementById("scanqp").onclick="";
			document.getElementById("scanqp").style.color="#999999";
		}else{
			document.getElementById("scansp").onclick=function(){showScan(-1,0);};
			document.getElementById("scansp").style.color="";
			document.getElementById("scanqp").onclick=function(){showScan(1,0);};
			document.getElementById("scanqp").style.color="";
		}
		if(current==sum){
			document.getElementById("scanxp").onclick="";
			document.getElementById("scanxp").style.color="#999999";
			document.getElementById("scanmp").onclick="";
			document.getElementById("scanmp").style.color="#999999";
		}else{
			document.getElementById("scanxp").onclick=function(){showScan(0,1);};
			document.getElementById("scanxp").style.color="";
			document.getElementById("scanmp").onclick=function(){showScan(0,-1);};
			document.getElementById("scanmp").style.color="";
		}
	}
	function setImg(obj){
		w=obj.width;
		h=obj.height;
		while(w>1000||h>1000){
			w=w/2;h=h/2;
			obj.style.width=w;
			obj.style.height=h;
		}
	}
	function scan_change(c){
		if(sum>1){
			if(c=="1"){
				w=w*1.2;h=h*1.2;
			}
			if(c=="0"){
				w=w/1.2;h=h/1.2;
			}
			var s_img=document.getElementById("scan_img");
			s_img.style.width=w;
			s_img.style.height=h;
		}
	}
	
	function scan_event(obj,event){
		event = event?event:window.event;
		if (event.wheelDelta>=120){
			w=w*1.1;h=h*1.1;
		}else{
			w=w/1.1;h=h/1.1;
		}
	 	obj.style.width=w;
	 	obj.style.height=h;
	}
	
	
	//关闭窗体时返回要删除的临时图像名称
	window.onunload=function(){ 
		if(!window.closed)
		{
		   if(sum>0&&scanArray[0].length>0){
	   			var returnScan=new Object();
	   			returnScan.scanArray=scans;
	   			returnValue=returnScan;
		   }
		}
	}
	
//-->
</script>