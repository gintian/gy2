<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@page import="com.hjsj.hrms.actionform.gz.gz_accounting.piecerate.PieceRateTjForm" %>

<%
PieceRateTjForm prform=(PieceRateTjForm)session.getAttribute("pieceRateTjForm");
String defId =prform.getDefId();
 %>

<script language="javascript">
function init()
{ 
	var obj=document.getElementById("defId");
	for(var i=0;i<obj.options.length;i++)
	{
		if('<%= defId%>' =='null'||'<%= defId%>'==''||'<%= defId%>'=='-1'){
			obj.options[0].selected=true;
			return;
		}else{
			if(obj.options[i].value=='<%= defId%>'){
				obj.options[i].selected=true;
				return;
			}
		}
	}
}
function deleteReport(){
	if(!confirm("确认删除选中报表吗?"))
  	return;
	var hashVo=new ParameterSet();
	var obj=document.getElementById("defId");
	var values="";
	for(var i=0;i<obj.options.length;i++)
	{
		if(obj.options[i].selected==true){
			if(values==""){
			values=obj.options[i].value;
			}else{
			values+=","+obj.options[i].value;
			}
		}
	}
	if(values==""){
		alert("没有数据可以删除");
		return;
	}
	hashVo.setValue("defId",values);
	var request=new Request({method:'post',asynchronous:false,onSuccess:deleteReportOK,functionId:'3020091054'},hashVo);
}
function deleteReportOK(outparameters){
	var info=getDecodeStr(outparameters.getValue("info"));
	
	if(info=="Fail"){
		alert("删除失败！");
		return;
	}
	var obj=document.getElementById("defId");
	//for(var i=0;i<obj.options.length;i++)修改倒序循环避免
	for(var i = obj.options.length-1;i>=0;i--)
	{
		if(obj.options[i].selected==true){
		var sortid=document.getElementById("sortId").value;
		document.getElementById("sortId").value=sortid.substring(0,sortid.lastIndexOf(","));//删除序列中的最后一个
		obj.options[i]=null
		//因为删除一个option后select减少一个option所以要在当期位置开始（也就是其他的option会填补上这个索引位置）要i--
		//i--;
		}
	}
	document.getElementById("updated").value="1";//删除过
	alert("删除成功！");
}
function sortReport(){
	if(!confirm("确认提交当前报表排列顺序吗?"))
  	return;
  	var hashVo=new ParameterSet();
	var obj=document.getElementById("defId");
	
	var sortIds=document.getElementById("sortId").value;
	var defId ="";
	for(var i=0;i<obj.options.length;i++)
	{
	defId += ","+ obj.options[i].value;
	}
	if(defId==""){
		alert("没有数据可以排序");
		return;
	}
	hashVo.setValue("defId",defId.substring(1));
	hashVo.setValue("sortIds",sortIds);
	var request=new Request({method:'post',asynchronous:false,onSuccess:sortReportOK,functionId:'3020091055'},hashVo);
}

function sortReportOK(outparameters){
	var info=getDecodeStr(outparameters.getValue("info"));
	alert(info)

}
function addReport(){
	 var theURL = "/gz/gz_accounting/piecerate/piecerate_tj_def.do?b_selectfld=link`model=add";
     var iframe_url="/gz/gz_accounting/bankdisk/iframe_bank_disk.jsp?src="+theURL;   
     if(isIE6()){
     var objlist =window.showModalDialog(iframe_url,null,"dialogWidth=630px;dialogHeight=420px;resizable:no;center:yes;scroll:no;status:no"); 
     }else{
     var objlist =window.showModalDialog(iframe_url,null,"dialogWidth=600px;dialogHeight=400px;resizable:no;center:yes;scroll:no;status:no"); 
     }	 
	var obj=new Object();
	if(objlist==null)
	{
	   return;
	}else{
	var obj=document.getElementById("defId");
	if(document.getElementById("sortId").value==""){
		document.getElementById("sortId").value=objlist.sortid;
	}else{
		document.getElementById("sortId").value+=","+objlist.sortid;
	}
   	obj.options[obj.length] = new Option(objlist.defname,objlist.defid ); 
   	document.getElementById("updated").value="1";//添加过
	}
}

function editReport(){
	var obj=document.getElementById("defId");
	var defid="";
	for(var i=0;i<obj.options.length;i++)
	{
		if(obj.options[i].selected==true){
			if(defid==""){
			defid=obj.options[i].value;
			}else{
			defid+=","+obj.options[i].value;
			}
		}
	}
	var defids = defid.split(",");
	if(defids.length>1 || defids.length==0){
		alert("请选择一个报表");
		return;
	}
	var theURL = "/gz/gz_accounting/piecerate/piecerate_tj_def.do?b_selectfld=link`model=edit`defid="+defid;
    var iframe_url="/gz/gz_accounting/bankdisk/iframe_bank_disk.jsp?src="+theURL;   
	var objlist =window.showModalDialog(iframe_url,null,"dialogWidth=600px;dialogHeight=400px;resizable:no;center:yes;scroll:yes;status:no");  
	var obj=new Object();
	if(objlist==null)
	{
	   return;
	}else{
		obj =document.getElementById("defId");
		for(var i=0;i<obj.options.length;i++){
			if(obj.options[i].value==objlist.defid){
				obj.options[i].text=objlist.defname;
				document.getElementById("updated").value="1";//修改过
				continue;
			}
		}
	}

}
function okOrClose(str){
	document.getElementById("okClose").value=str;
	window.close();

}
window.onbeforeunload = onbeforeunload_handler;
function onbeforeunload_handler(){
   var updated = document.getElementById("updated").value;
   var okOrClose = document.getElementById("okClose").value;
	if(updated=="1"||okOrClose=="ok"){
		obj =document.getElementById("defId");
		for(var i=0;i<obj.options.length;i++){
			if(obj.options[i].selected==true){
				window.returnValue=obj.options[i].value;
				return;
			}
		}
		window.returnValue="-1";
	}
}
</script>
<hrms:themes />
<base target=_self />
<html:form action="/gz/gz_accounting/piecerate/search_piecerate_tj_report">

<input name="sortId" type="hidden" value='${pieceRateTjForm.sortId}' />
<input name="updated" value ="0" type="hidden"/>
<input name="okClose" value ="init" type="hidden"/>
<table width='440px;' border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
           <tr>
            <td align="left" class="TableRow" nowrap colspan="3">
             <bean:message key="gz.piecerate.tj.report"/> 
		&nbsp;&nbsp;
            </td>            	        	        	        
           </tr>
   	  </thead>
   	   <tr>
            <td width="100%" align="center" class="RecordRow" nowrap style="border-top=0px">
              <table   >
                <tr>               
                <td width="46%" align="center" >
                 <table width="100%">               
                  <tr>
                  <td width="100%" align="left">
 		     
 		        <hrms:optioncollection name="pieceRateTjForm" property="reportList" collection="list"/>
		              <html:select styleId="dps" name="pieceRateTjForm" size="10" property="defId" multiple="multiple"  style="height:230px;width:100%;font-size:9pt">
		              <html:options collection="list" property="dataValue" labelProperty="dataName" />
		        </html:select>	
 		     
                 </td>
                  </tr>
                  </table>             
                </td>
               <td width="8%" align="center">
                   <html:button  styleClass="mybutton" property="b_up" onclick="upItem($('defId'));">
            		     <bean:message key="button.previous"/> 
	           </html:button >
	           <br>
	           <br>
	           <html:button  styleClass="mybutton" property="b_down" onclick="downItem($('defId'));">
            		     <bean:message key="button.next"/>    
	           </html:button >	     
                </td>                                               
                </tr>
               
              </table>             
            </td>
            </tr>
             <tr>
                <td colspan="3" height="35" align="center">
                      
                   
	      			<hrms:priv func_id="">
                   <html:button styleClass="mybutton" property="b_delete" onclick="addReport();">
            		      <bean:message key="menu.gz.new"/>
	      			</html:button>
	      			</hrms:priv>
	      		<html:button styleClass="mybutton" property="b_edit" onclick="editReport();">
            		      修改
	      			</html:button>
	            <html:button styleClass="mybutton" property="b_delete" onclick="deleteReport();">
            		      <bean:message key="button.delete"/>
	      			</html:button> 	 
	      		<html:button styleClass="mybutton" property="b_sort" onclick="sortReport();">
            		      保存排序
	      			</html:button>
                    <html:button styleClass="mybutton" property="b_delete" onclick="okOrClose('ok');">
            		      <bean:message key="button.ok"/>
	      			</html:button>
	      			<html:button styleClass="mybutton" property="b_cancel" onclick="okOrClose('close');">
            		      <bean:message key="button.close"/>
	      			</html:button> 	
                </td>
                </tr>
</table>
</html:form>
<script language="javascript">
	init();
</script>