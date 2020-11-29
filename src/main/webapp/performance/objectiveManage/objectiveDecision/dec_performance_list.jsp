<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,
				 com.hjsj.hrms.actionform.performance.objectiveManage.objectiveDecision.DecPerformanceForm,
				 org.apache.commons.beanutils.LazyDynaBean,
				 com.hrms.hjsj.sys.DataDictionary,
				 com.hrms.hjsj.sys.FieldItem,
				 com.hrms.struts.taglib.CommonData,
				 com.hjsj.hrms.utils.ResourceFactory,
				 com.hjsj.hrms.utils.PubFunc,
				 com.hrms.struts.valueobject.UserView,
				 com.hrms.struts.constant.WebConstant" %>
<html>
  <head>
   <%
		DecPerformanceForm decPerformanceForm=(DecPerformanceForm)session.getAttribute("decPerformanceForm");
		String object_type = decPerformanceForm.getObject_type();
		String md_planid=PubFunc.encryption(decPerformanceForm.getPlan_id());
		int n=0;
   %>		
  </head>
<style>

 .TEXT_NB {
	BACKGROUND-COLOR:transparent;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: medium none; 
	BORDER-RIGHT: medium none; 
	BORDER-TOP: medium none;
	
}
 .TableRow_self {
	
	margin-left:auto;
	margin-right:auto;
	background-position : center;
	background-color:#f4f7f7;
	font-size: 12px;  
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 1pt solid;
	height:25px;
	font-weight: bold;	
	valign:middle;
}
</style>

<script language='javascript' >

var _object_id;
var _plan_id;

function query()
{
   decPerformanceForm.action="/performance/objectiveManage/objectiveDecision/dec_performance_list.do?b_query=link&opt=2";
   decPerformanceForm.submit();
} 

//用于鼠标触发的某一行
var curObjTr= null;
var oldObjTr_c= "";
function trcheck(objTr)
{
	if(curObjTr!=null)
		curObjTr.style.backgroundColor="";
	curObjTr=objTr;
	oldObjTr_c="FFF8D2";
	curObjTr.style.backgroundColor='FFF8D2';		 
}
function copyBody(object_id,plan_id)
{	 
	_object_id=object_id;
	_plan_id=plan_id;
}
//全选
function selectAll()
{
	var records=document.getElementsByName("targetCalcItemt");
 	var allselect=document.getElementById("checkAll");
 	if(records)
 	{
     	for(var i=0;i<records.length;i++)
     	{
        	if(allselect.checked)       	
           		records[i].checked=true;        	
        	else       	
            	records[i].checked=false;       	
     	}
 	}
}

function copyBodys()
{	 
	var records=document.getElementsByName("targetCalcItemt");
    var num=0;
    var selectTargetCalcItemts="";
    if(records)
    {
      	for(var i=0;i<records.length;i++)
      	{
         	if(records[i].checked)
         	{
            	num++;
            	selectTargetCalcItemts+="/"+records[i].value;
         	}
      	}
   	}
   	if(num==0 || num>1)
   	{
      	alert("请选择一条考核对象记录！再进行复制！");
      	return;
   	}    
	var object_type = '<%=object_type%>';
	var hashvo=new ParameterSet();     
	hashvo.setValue("object_id",getEncodeStr(selectTargetCalcItemts.substring(1)));
	hashvo.setValue("plan_id",'${decPerformanceForm.plan_id}');
	hashvo.setValue("object_type",object_type);
	hashvo.setValue("opt",'1');
	var request=new Request({method:'post',onSuccess:isHaveBodys,functionId:'9028000287'},hashvo);
       
}
var plan_id_temp='';
var object_type_temp='';
var object_id_temp='';
function isHaveBodys(outparamters)
{
    var info=outparamters.getValue("info");   
    var plan_id=outparamters.getValue("plan_id");
    var object_type=outparamters.getValue("object_type");
    var object_id=outparamters.getValue("object_id");
    plan_id_temp=plan_id;
    object_type_temp=object_type;
    object_id_temp=object_id;
	if(info.length==0) {	 	
		var opt = 8;
		var infos=new Array();
		infos[0]=plan_id;
		infos[1]=opt;
		infos[2]=object_id;
	
   		var strurl="/performance/handSel.do?b_query=link`planid="+plan_id+"`opt="+opt+"`object_id="+object_id;
		var iframe_url="/gz/gz_accounting/bankdisk/iframe_bank_disk.jsp?src="+$URL.encode(strurl);
	
		var config = {
			    width:670,
			    height:490,
			    type:'1',
			    id:'ishavebodys_win',
			    dialogArguments:infos
			}
	   modalDialog.showModalDialogs(iframe_url,"ishavebodys_win",config,select_ok);
		
		/* // 多浏览器兼容showModalDialog改为Ext.window形式 chent 20171226 add
	    if(/msie/i.test(navigator.userAgent)){
			var objList=window.showModalDialog(iframe_url,infos,"dialogWidth=600px;dialogHeight=480px;resizable=yes;scroll=no;status=no;"); 
			select_ok(objList);
			return ;
		} else {
		    function openWin(){
			    Ext.create("Ext.window.Window",{
			    	id:'ishavebodys_win',
			    	width:670,
			    	height:490,
			    	title:'请选择',
			    	resizable:false,
			    	modal:true,
			    	autoScroll:true,
			    	renderTo:Ext.getBody(),
			    	html:"<iframe style='background-color:#ffffff' frameborder='0' SCROLLING=NO height='100%' width='100%' src='"+iframe_url+"'></iframe>"
			    }).show();	
			}
			
			if(typeof window.Ext == 'undefined'){
				insertFile("/ext/ext6/resources/ext-theme.css","css", function(){
					insertFile("/ext/ext6/ext-all.js","js" ,openWin);
				});
				
			} else {
				openWin();
			}
		} */
		
	}else	
	    alert(info);		
}  
function select_ok(objList){
	if(objList==null)
		return false;	

	if(objList.length>0) {
		var right_fields='';
		for(var i=0;i<objList.length;i++)		   	
	   		right_fields+=","+objList[i]+"";	
	   				  			   			   	
		if(confirm("确定把所选对象的"+JX_KHPLAN_TARGETCARD+"复制给指定的考核对象吗？")) {    
   			var hashvo=new ParameterSet();   		
   			var r=confirm("是否清除原有任务?");
		    if (r==true)
		    {				    
	     		hashvo.setValue("yorn",'y');   	            	
		    }
		    else
		    {				    
	     		hashvo.setValue("yorn",'n');   	            	
		    }			    
	     	hashvo.setValue("opt",'2');   
	     	hashvo.setValue("object_id", object_id_temp);
            hashvo.setValue("object_type", object_type_temp);
            hashvo.setValue("object_past", right_fields);
            hashvo.setValue("plan_id",plan_id_temp);
            var request=new Request({method:'post',onSuccess:copytOk,functionId:'9028000287'},hashvo);      			     		
   		}
	}
}
function selectWinClose(){
	Ext.getCmp('ishavebodys_win').close();
}
function copytOk(outparamters)
{
	var flag=outparamters.getValue("flag");
   	if(flag=="1")
		alert('复制操作完成！');
	else
		alert('复制操作失败！');
		
	decPerformanceForm.action="/performance/objectiveManage/objectiveDecision/dec_performance_list.do?b_query=link&opt=2";
	decPerformanceForm.submit();
} 

//opt: 0:查看 1：操作 2.打分  // model 1:团对  2:我的目标   3:目标制订  4.目标评估  5.目标结果  6:目标执行情况   7:目标卡代制订  8:评分调整   // body_id 5:本人 -2：第四级领导 ,-1：第三级领导,0：主管领导,1：直接上级
function unberlingObjective(body_id,opt,planid,a0100)
{
	objectCardForm.action="/performance/objectiveManage/objectiveCard.do?b_query=query&zglt=0&returnflag=menu&entranceType=0&body_id="+body_id+"&model=7&opt="+opt+"&planid="+planid+"&object_id="+a0100;
    objectCardForm.submit();	
}

//下载目标卡模板
function downLoadTarget(param)
{
	var onlyPram = '${decPerformanceForm.onlyFild}';	
	if(onlyPram==null || onlyPram.length<=0)
	{
		alert('系统没有指定唯一性指标,不能下载模板!');
		return;
	}
	var records=document.getElementsByName("targetCalcItemt");		
    var num=0;
    var selectTargetCalcItemts="";
    if(records)
    {
      	for(var i=0;i<records.length;i++)
      	{
      		if(param=='batch')
      		{
      			num++;
	            selectTargetCalcItemts+="/"+records[i].value;
      		}else
      		{
	         	if(records[i].checked)
	         	{
	            	num++;
	            	selectTargetCalcItemts+="/"+records[i].value;
	         	}
	        }
      	}
   	}  	
   	if(num==0)
   	{
      	alert("请选择要下载模板的考核对象！再进行下载！");
      	return;
   	} 
 	var hashvo=new ParameterSet();   
	hashvo.setValue("object_id",getEncodeStr(selectTargetCalcItemts.substring(1)));
	hashvo.setValue("plan_id",'<%=md_planid%>');
	hashvo.setValue("model",'7');
	hashvo.setValue("body_id",'1');
	hashvo.setValue("opt",'1');
	hashvo.setValue("searchOrBatch",param);
	var request=new Request({method:'post',asynchronous:false,onSuccess:showfile,functionId:'9028000288'},hashvo);    	  	
}
function showfile(outparamters)
{
  	var outName=outparamters.getValue("outName");
//	window.location.href = "/servlet/DisplayOleContent?filename="+outName;
	//20/3/6 xus vfs改造
	window.location.href = "/servlet/vfsservlet?fileid="+outName+"&fromjavafolder=true";
}
function importTarget()
{
	var onlyPram = '${decPerformanceForm.onlyFild}';	
	if(onlyPram==null || onlyPram.length<=0)
	{
		alert('系统没有指定唯一性指标,不能导入!');
		return;
	}
	var target_url="/performance/objectiveManage/objectiveDecision/dec_performance_list.do?br_import=link";
 	var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
	
 	// 多浏览器兼容showModalDialog改为Ext.window形式 chent 20171226 add
    if(/msie/i.test(navigator.userAgent)){
		var return_vo= window.showModalDialog(iframe_url, "importExcel", "dialogWidth:550px; dialogHeight:260px;resizable:no;center:yes;scroll:no;status:no");	
		if(!return_vo)
			return;	   
		if(return_vo.flag=="true")
			alert('导入成功!');  
		decPerformanceForm.action="/performance/objectiveManage/objectiveDecision/dec_performance_list.do?b_query=link&opt=2";
	    decPerformanceForm.submit();
		return ;
	} else {
	    function openWin(){
		    Ext.create("Ext.window.Window",{
		    	id:'importtarget_win',
		    	width:580,
		    	height:290,
		    	title:'导入目标',
		    	resizable:false,
		    	modal:true,
		    	autoScroll:true,
		    	renderTo:Ext.getBody(),
		    	html:"<iframe style='background-color:#ffffff' frameborder='0' SCROLLING=NO height='100%' width='100%' src='"+iframe_url+"'></iframe>"
		    }).show();	
		}
		
		if(typeof window.Ext == 'undefined'){
			insertFile("/ext/ext6/resources/ext-theme.css","css", function(){
				insertFile("/ext/ext6/ext-all.js","js" ,openWin);
			});
			
		} else {
			openWin();
		}
	}
}
function importTarget_ok(flag){
	if(flag=="true")
		alert('导入成功!');  
	decPerformanceForm.action="/performance/objectiveManage/objectiveDecision/dec_performance_list.do?b_query=link&opt=2";
    decPerformanceForm.submit();
}
function sub1(o)
{
     decPerformanceForm.action="/performance/objectiveManage/objectiveDecision/dec_performance_list.do?b_query=query&operate=init0";
	 decPerformanceForm.submit(); 	
}
</script>
 <hrms:themes />
<body>
<html:form action="/performance/objectiveManage/objectiveDecision/dec_performance_list">
<html:hidden name="decPerformanceForm" property="object_type" />
<table width="90%" border="0" cellspacing="0"  align="center" cellpadding="0">	
	<tr><td style="height:35px">   
		<bean:message key="lable.performance.perPlan"/>: 
		<html:select name="decPerformanceForm"  onchange="query()"  styleId="plan_id" property="plan_id" size="1">
	  		<html:optionsCollection property="itemSumList" value="dataValue" label="dataName"/>
		</html:select> 
		&nbsp;&nbsp;&nbsp;&nbsp;	
		<bean:message key="lable.zp_plan.status"/>: 
		<html:select name="decPerformanceForm"  onchange="query()"  styleId="status" property="status" size="1">
	  		<html:optionsCollection property="statusList" value="dataValue" label="dataName"/>
		</html:select>	
		&nbsp;&nbsp;&nbsp;	
		<hrms:priv> 
	    	<input type="button" name="init" class="mybutton" value="复制<bean:message key="org.performance.card"/>至" onclick="copyBodys();"/>
	  	</hrms:priv>
	</td></tr>
	
	<tr><td width='100%'  class="common_border_color">		
	<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
	<thead>
        <tr >
        	 
        	  <td align="center" class="TableRow common_border_color" nowrap>
  			  		<input type="checkbox" name="check" id='checkAll' value="1" onclick='selectAll();' />
  			  </td>
			 <%
			 	FieldItem fielditem = DataDictionary.getFieldItem("E0122");
			  	if(object_type!=null && object_type.equals("2"))
			  	{
				 	out.print(" <td align='center' style='color:black'   class='TableRow_self common_border_color' >"+ResourceFactory.getProperty("b0110.label")+"</td>");
			 	 	out.print(" <td align='center' style='color:black'   class='TableRow_self common_border_color' >"+fielditem.getItemdesc()+"</td>");
			 	 	out.print(" <td align='center' style='color:black'   class='TableRow_self common_border_color' >"+ResourceFactory.getProperty("e01a1.label")+"</td>");
			 	 	out.print(" <td align='center' style='color:black'   class='TableRow_self common_border_color' >"+ResourceFactory.getProperty("hire.employActualize.name")+"</td>");
			 	}
			  	else
			  	{
			 	 	out.print(" <td align='center' style='color:black'    class='TableRow_self common_border_color' >"+ResourceFactory.getProperty("tree.unroot.undesc")+"/"+ResourceFactory.getProperty("tree.umroot.umdesc")+"</td>");
			  	}
			  %>
			
			 <td align="center" style='color:black'    class="TableRow_self common_border_color" nowrap><bean:message key="lable.zp_plan.status"/></td>
			 <td align="center" style='color:black'    class="TableRow_self common_border_color" nowrap><bean:message key="lable.zp_plan.decstatus"/></td>
			 <td align="center" style='color:black'    class="TableRow_self common_border_color" nowrap><bean:message key="reportcyclelist.option"/></td>
			
		</tr>
	 </thead>
	 
	  <hrms:extenditerate id="element" name="decPerformanceForm" property="planListForm.list" indexes="indexes"  pagination="planListForm.pagination" pageCount="${decPerformanceForm.pagerows}" scope="session">
	    <%
		          if(n%2==0)
		          {
		          %>
		          <tr class="trShallow"  onClick="copyBody('<bean:write name="element" property="mda0100" filter="true"/>','<bean:write name="element" property="mdplanid"/>')">
		          <%}
		          else
		          {%>
		          <tr class="trDeep"  onClick="copyBody('<bean:write name="element" property="mda0100" filter="true"/>','<bean:write name="element" property="mdplanid"/>')">
		          <%
		          }
		          n++;          
		          %>  
		          
		          <td align="center" class="RecordRow" nowrap width="5%">
						<input name="targetCalcItemt" type="checkbox" value="<bean:write name="element" property="mda0100" filter="true"/>"/>																			
				  </td>
		          
		          <%
		           if(object_type!=null && object_type.equals("2")){
		           %>
		           <td align='left' class='RecordRow' nowrap>&nbsp;&nbsp;<bean:write name="element" property="b0110" filter="true"/></td>
		           <td align='left' class='RecordRow' nowrap>&nbsp;&nbsp;<bean:write name="element" property="e0122" filter="true"/></td>
		           <td align='left' class='RecordRow' nowrap>&nbsp;&nbsp;<bean:write name="element" property="e01a1" filter="true"/></td>
		           <td align='left' class='RecordRow' nowrap>&nbsp;&nbsp;<bean:write name="element" property="a0101" filter="true"/></td>
		          <% 
		           }else{
		           %>
		           
		           <td align='left' class='RecordRow' nowrap>&nbsp;&nbsp;<bean:write name="element" property="a0101" filter="true"/></td>
		           <% 
		           }
		           %>
		           
		           <td align='center' class='RecordRow' nowrap width="10%">&nbsp;&nbsp;<bean:write name="element" property="sp_flag" filter="true"/></td>
		           
		           <%  
		          		LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
	 					String object_id=(String)abean.get("object_id");
	 					if(object_id.equals("0")){
	 			  %>
		           		<td align='center' class='RecordRow' nowrap>未制订</td>
		           <% 
		           }else{
		          %>
		      			<td align='center' class='RecordRow' nowrap>制订中</td>
		      	  <%}%>
				           
		          <%  
	 					String sp_flag=(String)abean.get("sp");
	 					String editCard=(String)abean.get("editCard");
	 				//	if((sp_flag.equals("01")) || (sp_flag.equals("07")))
	 				    // 修改为：计划没启动且员工目标任务没有被评分，目标卡制订模块都可以修改员工的目标卡任务
	 					if(editCard!=null && editCard.equalsIgnoreCase("true"))
	 					{
	 			  %>	

		     		    <td align="center" class="RecordRow" nowrap>
	       					<a href="javascript:unberlingObjective('1','1','<bean:write name="element" property="mdplanid"/>','<bean:write name="element" property="mda0100"/>');">制订</a>
	    		   		</td>
		     	  <% 
		           }else{
		          %>
		      			<td align="center" class="RecordRow" nowrap>
	       					<a href="javascript:unberlingObjective('1','0','<bean:write name="element" property="mdplanid"/>','<bean:write name="element" property="mda0100"/>');">查看</a>
	    		   		</td>
		      	  <%}%>        		           
	 			 </tr>
	  </hrms:extenditerate>	  
	  </table>
</td></tr>	
</table>	

<table  width="90%" align="center" class="RecordRowP">
	<tr>
		<td valign="bottom" class="tdFontcolor">
			<bean:message key="label.page.serial"/>
			<bean:write name="decPerformanceForm" property="planListForm.pagination.current" filter="true" />
			<bean:message key="label.page.sum"/>
			<bean:write name="decPerformanceForm" property="planListForm.pagination.count" filter="true" />
			<bean:message key="label.page.row"/>
			<bean:write name="decPerformanceForm" property="planListForm.pagination.pages" filter="true" />
			<bean:message key="label.page.page"/>&nbsp;&nbsp;	
			 每页显示<html:text property="pagerows" name="decPerformanceForm" size="3"></html:text>条&nbsp;&nbsp;<a href="javascript:sub1(0);">刷新</a>	
		</td>
	    <td align="right" nowrap class="tdFontcolor">
			<p align="right">
			<hrms:paginationlink name="decPerformanceForm" property="planListForm.pagination" nameId="planListForm"></hrms:paginationlink>
		</td>
	</tr>
</table>

<table width="90%" border="0" cellspacing="0"  align="center" cellpadding="0">	
	<tr>
		<td align="left" style="height:35px">
		<hrms:priv func_id="06070801">
			<input type="button" name="downLoad" class="mybutton" value="<bean:message key="button.download.template"/>" onclick="downLoadTarget('serch');"/>			
		</hrms:priv>
		<hrms:priv func_id="06070803">
			<input type="button" name="batchDownLoad" class="mybutton" value="<bean:message key="button.download.batchtemplate"/>" onclick="downLoadTarget('batch');"/>			
		</hrms:priv>
		<hrms:priv func_id="06070802">
			<input type="button" name="importData" class="mybutton" value="<bean:message key="button.import"/><bean:message key="lable.menu.main.target"/>" onclick='importTarget();'/>
		</hrms:priv>						
		</td>
	</tr>
</table>

</html:form>
  
  
<html:form action="/performance/objectiveManage/objectiveCard">
<input type="hidden" name="returnURL" value="/performance/objectiveManage/objectiveDecision/dec_performance_list.do?b_query=link&opt=2&plan_id=${decPerformanceForm.plan_id}&status=${decPerformanceForm.status}"/>
<input type="hidden" name="target" value="il_body"/>
</html:form>

  </body>
</html>
