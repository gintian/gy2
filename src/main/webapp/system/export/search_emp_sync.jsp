<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hrms.struts.valueobject.Pagination"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="java.sql.Date"%>
<%@ page import="java.util.*,com.hjsj.hrms.actionform.sys.export.HrSyncForm,com.hrms.hjsj.sys.FieldItem"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<%@ page import="com.hrms.struts.taglib.CommonData"%>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean,com.hrms.frame.utility.DateStyle,com.hjsj.hrms.utils.PubFunc" %>

<hrms:themes></hrms:themes>
<script language="javascript" src="/module/utils/js/template.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<% 
int i=0;
String sync_version=SystemConfig.getPropertyValue("sync_version");
String str = "";
HrSyncForm hrSyncForm = (HrSyncForm)session.getAttribute("hrSyncForm");
ArrayList list = hrSyncForm.getFieldslist();
ArrayList sysid = hrSyncForm.getSysid();
for (int j=0;j<sysid.size();j++) {
    CommonData cd = (CommonData)sysid.get(j);
    str += "," + cd.getDataName();
}
str += ",flag";
String hrunique_ids = hrSyncForm.getHrunique_ids();
String orgunique_ids = hrSyncForm.getOrgunique_ids();
String postunique_ids = hrSyncForm.getPostunique_ids();
String sync_mode = hrSyncForm.getSync_mode();
String emporg = hrSyncForm.getEmporg();
Pagination pagination=hrSyncForm.getPagination();
ArrayList currlist=null;
if(pagination != null){
	currlist=pagination.getCurr_page_list();
}
//xus 18/3/16
String tacitly=hrSyncForm.getFormHM().get("tacitly")==null? "":hrSyncForm.getFormHM().get("tacitly").toString();
String WXQYUrl=hrSyncForm.getFormHM().get("url")==null? "":hrSyncForm.getFormHM().get("url").toString();
%>
<script type="text/javascript">
function to_set()
{
	hrSyncForm.action="/sys/export/SearchHrSyncSet.do?b_query=link";
	hrSyncForm.submit(); 	
}

function to_export()
{
	var request=new Request({method:'post',asynchronous:false,onSuccess:showfile,functionId:'1010100110'});			
}
//手工发送
function handwork_send()
{
      //改用ext 弹窗显示  wangb 20190320
      /*var url = "/system/outsync/selectsso.jsp";
      var win = Ext.create('Ext.window.Window',{
			id:'handwork_send',
			title:'手工发送',
			width:470,
			height:420,
			resizable:'no',
			modal:true,
			autoScoll:false,
			autoShow:true,
			autoDestroy:true,
			html:'<iframe style="background-color:#fff" frameborder="0" SCROLLING=NO height="100%" width="100%" src="'+url+'"></iframe>',
			renderTo:Ext.getBody(),
			listeners:{
				'close':function(){
					if(!this.return_vo)
	   					return false;
      				if(this.return_vo.flag=="true")
      				{
				 		var val=document.getElementById('emporg').value;
				        var hashvo=new ParameterSet();
         				hashvo.setValue("id",this.return_vo.id+"");
         				hashvo.setValue("emporg",val);
         				hashvo.setValue("hruniqueids",hrunique_ids + "");
         				hashvo.setValue("orguniqueids",orgunique_ids + "");
         				hashvo.setValue("postuniqueids",postunique_ids + "");
			            var waitInfo=eval("wait");
         				waitInfo.style.display="block";
         				var request=new Request({method:'post',asynchronous:true,onSuccess:returnResult,functionId:'1010100131'},hashvo);
      				}
				}
			}
	});*/
        //去除弹框20201016 wangcy
	if(confirm("你确定要推送数据至KAFAK吗"))
	{
		var val=document.getElementById('emporg').value;
		var hashvo=new ParameterSet();
		hashvo.setValue("emporg",val);
		hashvo.setValue("hruniqueids",hrunique_ids + "");
		hashvo.setValue("orguniqueids",orgunique_ids + "");
		hashvo.setValue("postuniqueids",postunique_ids + "");
		var waitInfo=eval("wait");
		waitInfo.style.display="block";
		var request=new Request({method:'post',asynchronous:true,onSuccess:returnResult,functionId:'1010100131'},hashvo);
	}

}
 function returnResult(outparamters) 
 {
        musterInitData();
		var resultStr = outparamters.getValue("outinfo");
		resultStr = getDecodeStr(resultStr);
		resultStr=resultStr==null?"":resultStr;
		var arguments=new Array(resultStr)
		/*
		 var return_vo= window.showModalDialog("/system/outsync/viewssoinfo.jsp",arguments, 
        "dialogWidth:450px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
        window.location.href=window.location.href;
        */
        //改用ext 弹窗显示  wangb 20190320
        var url = "/system/outsync/viewssoinfo.jsp";
        var win = Ext.create('Ext.window.Window',{
			id:'send_result',
			title:'手工发送',
			width:470,
			height:420,
			resizable:'no',
			modal:true,
			autoScoll:false,
			autoShow:true,
			autoDestroy:true,
			html:'<iframe style="background-color:#fff" frameborder="0" SCROLLING=NO height="100%" width="100%" src="'+url+'"></iframe>',
			renderTo:Ext.getBody(),
			listeners:{
				'close':function(){
					//19/7/26 xus 【47494】76 程序  钉钉oracle 同步的时候  人员 同步过去了 状态不会改变
					//之前的方法会重复执行初始化数据
					window.location.href="/sys/export/SearchEmpSync.do?b_query=link";
				}
			}
		 });  
		 win.arguments = arguments;
 }
function showfile(outparamters){
	var outName=outparamters.getValue("outName");
	var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+name,"excel");
	}
function to_hr_sync()
{
  if(confirm("您确定要初始化数据吗?"))
  { 
    var waitInfo=eval("wait");	
    waitInfo.style.display="block";
	hrSyncForm.action="/sys/export/SearchEmpSync.do?b_sync=link&encryptParam=<%=PubFunc.encrypt("emporg="+emporg)%>";
	hrSyncForm.submit(); 
  }	
}
function handwork_hr_sync()
{
   if(confirm("您确定要再次同步数据吗?"))
   { 
      var waitInfo=eval("wait");	
	  waitInfo.style.display="block";
      hrSyncForm.action="/sys/export/SearchEmpSync.do?b_handwork=link&encryptParam=<%=PubFunc.encrypt("emporg="+emporg)%>";
	  hrSyncForm.submit(); 
	}
}
function change()
{
    //全局的人员编号数组
    hrunique_ids = [];
    document.getElementById("hrunique_ids").value = "";
    //全局的机构编号数组
    orgunique_ids = [];
    document.getElementById("orgunique_ids").value = "";
    //全局的岗位编号数组
    postunique_ids = [];
    document.getElementById("postunique_ids").value = "";
//	hrSyncForm.action="/sys/export/SearchEmpSync.do?b_query=link";
	hrSyncForm.action="/sys/export/SearchEmpSync.do?b_query=link&select=1";//查询按钮和机构 区分  拼接参数select代表查询按钮 wangb 20170830 31073
	hrSyncForm.submit(); 
	
}
function emporg11()
{
	//全局的人员编号数组
    hrunique_ids = [];
    document.getElementById("hrunique_ids").value = "";
    //全局的机构编号数组
    orgunique_ids = [];
    document.getElementById("orgunique_ids").value = "";
    //全局的岗位编号数组
    postunique_ids = [];
    document.getElementById("postunique_ids").value = "";
    var val=document.getElementById('emporg').value;
    hrSyncForm.action="/sys/export/SearchEmpSync.do?b_query=link&emporg="+val;
	hrSyncForm.submit(); 
}
function musterInitData()
{
	   var waitInfo=eval("wait");	
	   waitInfo.style.display="none";
	   updateChecked();
}

//修改同步状态
function syncstata1()
{
        var val=document.getElementById('emporg').value;
	    var syncurl="";
	    //saveUnique_id();
	    if(val==1){
	            syncurl = "/system/export/edit_sync_stata.jsp?emporg="+val+"&unique_ids="+hrunique_ids;
	    }else if(val==2){
	            syncurl = "/system/export/edit_sync_stata.jsp?emporg="+val+"&unique_ids="+orgunique_ids;
	    }else if(val==3){
	            syncurl = "/system/export/edit_sync_stata.jsp?emporg="+val+"&unique_ids="+postunique_ids;
	    }
	    /*
	    var return_vo= window.showModalDialog(syncurl,"", 
	        "dialogWidth:400px; dialogHeight:380px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
	    if(return_vo)
	    	   window.location.href=window.location.href;
	    */
	    //改用ext 弹窗显示  wangb 20190320
	    var win = Ext.create('Ext.window.Window',{
			id:'syncstata1',
			title:'修改同步状态',
			width:420,
			height:400,
			resizable:'no',
			modal:true,
			autoScoll:false,
			autoShow:true,
			autoDestroy:true,
			html:'<iframe style="background-color:#fff" frameborder="0" SCROLLING=NO height="100%" width="100%" src="'+syncurl+'"></iframe>',
			renderTo:Ext.getBody(),
			listeners:{
				'close':function(){
					if(this.return_vo)
	    	   			window.location.href="/sys/export/SearchEmpSync.do?b_query=link&amp;encryptParam=<%=PubFunc.encrypt("emporg="+emporg)%>";
				}
			}
		 });  
}
//全选全不选
function selAll(obj){
	//JQuery("input[name='detail']").attr("checked",JQuery(obj).attr("checked"));
	var objs = document.getElementsByTagName("input");
	for(var j=0;j<objs.length;j++){
			if (objs[j].type=="checkbox" && objs[j].name !="all") {
					objs[j].checked = obj.checked;
					saveUni(objs[j]);
			}
		}
	
	//saveUnique_id();
}
//定义全局的人员编号数组
var hrunique_ids = [];
//定义全局的机构编号数组
var orgunique_ids = [];
//定义全局的岗位编号数组
var postunique_ids = [];

function saveUni(obj) {
var val = document.getElementById('emporg').value;
	if(val==1){
                    if(obj.checked){
                        if(hrunique_ids.length==0){
                        	hrunique_ids.push(obj.value);
                        }else{
                            var bool = "false";
                            for(var j=0;j<hrunique_ids.length;j++){
                                if(hrunique_ids[j] == obj.value){
                                	bool = "true";
                                }
                            }
                            if(bool == "false"){
                               	hrunique_ids.push(obj.value);
                                  
                            }
                        }
                    }else{
                        for(var j=0;j<hrunique_ids.length;j++){
                            if(hrunique_ids[j] == obj.value){
                                   delete hrunique_ids[j];
                            }
                        }
                    }
                }else if(val==2){
                    if(obj.checked){
                        if(orgunique_ids.length==0){
                            orgunique_ids.push(obj.value);
                        }else{
                        	var bool = "false";
                            for(var j=0;j<orgunique_ids.length;j++){
                                if(orgunique_ids[j] == obj.value){
                                    bool = "true";
                                }
                            }
                            if(bool == "false"){
                            	orgunique_ids.push(obj.value);
                                  
                            }
                        }
                    }else{
                        for(var j=0;j<orgunique_ids.length;j++){
                            if(orgunique_ids[j] == obj.value){
                                   delete orgunique_ids[j];
                            }
                        }
                    }
                }else{
                    if(obj.checked){
                        if(postunique_ids.length==0){
                            postunique_ids.push(obj.value);
                        }else{
                        	var bool = "false";
                            for(var j=0;j<postunique_ids.length;j++){
                                if(postunique_ids[j] == obj.value){
                                    bool = "true";
                                }
                            }
                            if(bool == "false"){
                            	postunique_ids.push(obj.value);
                                  
                            }
                        }
                    }else{
                        for(var j=0;j<postunique_ids.length;j++){
                            if(postunique_ids[j] == obj.value){
                                   delete postunique_ids[j];
                            }
                        }
                    }
                } 
}


//获取已勾选的人员编号
function saveUnique_id(){
	var val = document.getElementById('emporg').value;
	
	var objs = document.getElementsByTagName("input");
	for(var j=0;j<objs.length;j++){
			if (objs[j].type=="checkbox" && objs[j].name !="all") {
	
                if(val==1){
                    if(objs[j].checked){
                        if(hrunique_ids.length==0){
                        	hrunique_ids.push(objs[j].value);
                        }else{
                            var bool = "false";
                            for(var j=0;j<hrunique_ids.length;j++){
                                if(hrunique_ids[j] == objs[j].value){
                                	bool = "true";
                                }
                            }
                            if(bool == "false"){
                               	hrunique_ids.push(objs[j].value);
                                  
                            }
                        }
                    }else{
                        for(var j=0;j<hrunique_ids.length;j++){
                            if(hrunique_ids[j] == objs[j].value){
                                   delete hrunique_ids[j];
                            }
                        }
                    }
                }else if(val==2){
                    if(objs[j].checked){
                        if(orgunique_ids.length==0){
                            orgunique_ids.push(objs[j].value);
                        }else{
                        	var bool = "false";
                            for(var j=0;j<orgunique_ids.length;j++){
                                if(orgunique_ids[j] == objs[j].value){
                                    bool = "true";
                                }
                            }
                            if(bool == "false"){
                            	orgunique_ids.push(objs[j].value);
                                  
                            }
                        }
                    }else{
                        for(var j=0;j<orgunique_ids.length;j++){
                            if(orgunique_ids[j] == objs[j].value){
                                   delete orgunique_ids[j];
                            }
                        }
                    }
                }else{
                    if(objs[j].checked){
                        if(postunique_ids.length==0){
                            postunique_ids.push(objs[j].value);
                        }else{
                        	var bool = "false";
                            for(var j=0;j<postunique_ids.length;j++){
                                if(postunique_ids[j] == objs[j].value){
                                    bool = "true";
                                }
                            }
                            if(bool == "false"){
                            	postunique_ids.push(objs[j].value);
                                  
                            }
                        }
                    }else{
                        for(var j=0;j<postunique_ids.length;j++){
                            if(postunique_ids[j] == objs[j].value){
                                   delete postunique_ids[j];
                            }
                        }
                    }
                }  
                
                
              }
            } 

    document.getElementById("hrunique_ids").value = hrunique_ids;
    document.getElementById("orgunique_ids").value =orgunique_ids;
    document.getElementById("postunique_ids").value =postunique_ids;
}
function updateChecked(){
    var hrunique_id = "<%=hrunique_ids%>";
    var orgunique_id = "<%=orgunique_ids%>";
    var postunique_id = "<%=postunique_ids%>";
    var hrunique_idss = hrunique_id.split(",");
    for(var i=0;i<hrunique_idss.length;i++){
    	if(hrunique_idss[i]!="null" && hrunique_idss[i]!=""){
    		hrunique_ids.push(hrunique_idss[i]);
    	}
    }
    var orgunique_idss = orgunique_id.split(",");
    for(var i=0;i<orgunique_idss.length;i++){
    	if(orgunique_idss[i]!="null" && orgunique_idss[i]!=""){
    		orgunique_ids.push(orgunique_idss[i]);
    	}
    }
    var postunique_idss = postunique_id.split(",");
    for(var i=0;i<postunique_idss.length;i++){
    	if(postunique_idss[i]!="null" && postunique_idss[i]!=""){
    		postunique_ids.push(postunique_idss[i]);
    	}
    }
    var val = document.getElementById('emporg').value;
    if(val=="1")
    	checks(hrunique_ids);
    else if(val=="2")
    	checks(orgunique_ids);
    else if(val=="3")
    	checks(postunique_ids);
    	
    document.getElementById("hrunique_ids").value = hrunique_ids;
    document.getElementById("orgunique_ids").value =orgunique_ids;
    document.getElementById("postunique_ids").value =postunique_ids;
}
function checks(unique_id){
	/**JQuery("#tbd tr").each(
		function (i,obj){
			for(var j=0;j<unique_id.length;j++){
				if(JQuery(obj).find("td:eq(0)>input").val()==unique_id[j])
					JQuery(obj).find("td:eq(0)>input").attr("checked","checked");
			}
		}
	);**/
	
	var objs = document.getElementsByTagName("input");
	for(var i=0;i<unique_id.length;i++){
		for(var j=0;j<objs.length;j++){
			if (objs[j].type=="checkbox" && objs[j].name !="all") {
				if (objs[j].value == unique_id[i]) {
					objs[j].checked = true;
					break;
				}
			}
		}
	}
	
}
//xus 18/3/15 初始化判断是否加载‘重新关联’按钮
function init(){
	url="<%=WXQYUrl%>";
	if(url.indexOf('services/WeiXinQYData?wsdl')==-1){
		document.getElementsByName("reassociate")[0].style.display="none";
	}
}
//xus 18/3/16 ‘重新关联’触发事件
function to_reassociate(){
	 if(confirm("注意：\r    该功能会重置企业微信后台机构层级，并且同步所有符合条件的人员。请谨慎操作！！！"))
	 {
		var sys_id="<%=tacitly%>";
	 	var url="<%=WXQYUrl%>";
	 	var hashvo=new ParameterSet();
     	var newurl=url.substr(0,url.indexOf("/services/WeiXinQYData?wsdl"))+"/services/WXQYOrgCheckSyncData?wsdl";
      	hashvo.setValue("url",newurl);
      	hashvo.setValue("sys_id",sys_id); 
     	var request=new Request({method:'post',asynchronous:true,onSuccess:function(){alert("同步完成");},functionId:'1010100140'},hashvo);	
	 }
}
</script>
<html:form action="/sys/export/SearchEmpSync">
<input type="hidden" name="hrunique_ids" id="hrunique_ids" value=""/>
<input type="hidden" name="orgunique_ids" id="orgunique_ids" value=""/>
<input type="hidden" name="postunique_ids" id="postunique_ids" value=""/>
		<logic:equal name="hrSyncForm" property="emporg" value="1">
		<table  border="0" cellspacing="0" width="100%" align="center" cellpadding="0"   class="ListTable">
		   <tr>
			<td align="left" style="padding-bottom:5px;" nowrap colspan='<%=list.size()+1%>'>
				&nbsp;<hrms:optioncollection name="hrSyncForm" property="sync_typelst" collection="list" />
				<!-- 非IE浏览器，name属性不能作为id属性获取节点，得添加styleid属性  wangb 32349  20171027 -->
		        <html:select name="hrSyncForm" property="emporg" styleId="emporg" onchange="emporg11();">
		        <html:options collection="list" property="dataValue" labelProperty="dataName"/>
		        </html:select>
		        
		        <% //if (sync_mode != null && sync_mode.equalsIgnoreCase("trigger")) {%>
					&nbsp;<bean:message key="id_factory.sys"/>&nbsp;
	                <hrms:optioncollection name="hrSyncForm" property="sysid" collection="list" />
	                <html:select name="hrSyncForm" property="tacitly" onchange="change()">
	                <html:options collection="list" property="dataValue" labelProperty="dataName"/>
	                </html:select>
			        &nbsp;<bean:message key="kq.card.status"/>&nbsp;
	                <hrms:optioncollection name="hrSyncForm" property="statelist" collection="list" />
	                <html:select name="hrSyncForm" property="state" onchange="change()">
	                <html:options collection="list" property="dataValue" labelProperty="dataName"/>
	                </html:select>
					&nbsp;<bean:message key="label.dbase"/>&nbsp;
					<hrms:optioncollection name="hrSyncForm" property="dbnamelist" collection="list" />
			        <html:select name="hrSyncForm" property="dbname" onchange="change()">
			        <html:options collection="list" property="dataValue" labelProperty="dataName"/>
			        </html:select>
				<%//} %>
		        &nbsp;&nbsp;&nbsp;&nbsp;姓名&nbsp;<html:text name="hrSyncForm" property="select_name" styleClass="complex_border_color" style="height:22px;"></html:text>&nbsp;&nbsp;
		        <input type="button" name="querry" value="查询" class="mybutton" onclick="change()"/>
			</td>

		    </tr>
			<tr >
			<td width="30" align="center" class="TableRow"><input type="checkbox" name="all" onclick="selAll(this);"/></td>
				<logic:iterate id="element1" name="hrSyncForm" property="fieldslist">
					<td class="TableRow" align="center" nowrap>
						<bean:write name="element1" property="itemdesc"/>
					</td>
				</logic:iterate>
			</tr>
		    <tbody id="tbd">		
		 	<hrms:paginationdb id="element" name="hrSyncForm" sql_str="hrSyncForm.selectsql" order_by="order by a0000" table="" where_str="hrSyncForm.wheresql" columns="hrSyncForm.column"  pagerows="18" page_id="pagination" indexes="indexes" keys="unique_id">	
			    	<%
          	if(i%2==0)
          	{
          	%>
          	<tr class="trShallow">
          	<%}
          	else
          	{%>
          	<tr class="trDeep">
          	<%
         	 }
          	i++;          
          	%>  
<td width="30" align="center" class="RecordRow">
<bean:define id="ids" name="element" property="unique_id"></bean:define>
<input type="checkbox" name="detail" onchange="saveUni(this)" value="${ids}"/></td>

	<%		  
			for(int t=0;t<list.size();t++)
			{
				FieldItem fi  = (FieldItem)list.get(t);
				
				if(fi.getItemtype().equalsIgnoreCase("A")&&!(fi.getCodesetid().equalsIgnoreCase("0")))
				{
			   %>
			   <td class="RecordRow" align="left" nowrap>&nbsp;	
				
	   			<bean:write name="element" property="<%=fi.getItemid().toLowerCase()%>"/>	
			   </td>	
			<%
			}else if (str.indexOf(fi.getFieldsetid())>-1){
			%>
			<td class="RecordRow" align="center" nowrap>
			<logic:equal name="element" property="<%=fi.getItemid().toLowerCase()%>" value="0">
				<!-- 当sys_flag 值为3 在系统中人员视图 本次操作是删除同步的  wangb 20170706 -->
				<logic:equal name="element" property="sys_flag" value="3">(删除)已同步</logic:equal>
				<logic:notEqual name="element" property="sys_flag" value="3">已同步</logic:notEqual>
			</logic:equal>
			<logic:equal name="element" property="<%=fi.getItemid().toLowerCase()%>" value="1">
			 新增
			</logic:equal>
			<logic:equal name="element" property="<%=fi.getItemid().toLowerCase()%>" value="2">
			 修改
			</logic:equal>
			<logic:equal name="element" property="<%=fi.getItemid().toLowerCase()%>" value="3">
			 删除
			</logic:equal>
			<%}else if (fi.getItemtype().equalsIgnoreCase("N")){
			%>
			<td class="RecordRow" align="right" nowrap>						
				&nbsp;	<bean:write name="element" property="<%=fi.getItemid()%>"/>
			</td>
			<%}else if (fi.getItemtype().equalsIgnoreCase("D")){			 	
		     		LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
		     		String date=(String)abean.get(fi.getItemid().toLowerCase());
		     		if(date!=null&&date.length()>0)
		     		{
		     		  date = PubFunc.replace(date, ".", "-");
		     		  if(date.length()>9)	
		     			date = date.substring(0,10);  
		     		}else
		     		  	date="";
			%>
			<td class="RecordRow" align="left" nowrap>						
			<!--<bean:write name="element" property="<%=fi.getItemid().toLowerCase()%>"/>  -->
				&nbsp;	<%=date%>
			</td>
			<%	
			}else{
			%>
			<td class="RecordRow" align="left" nowrap>						
				&nbsp;	<bean:write name="element" property="<%=fi.getItemid().toLowerCase()%>"/>
			</td>	
			<%	
					}
				}
			%>
  
			    </tr>
	    	</hrms:paginationdb>
			    </tbody>

		</table>

		<table  width="100%" align="center" class="RecordRowP">
		  <tr>
		     <td valign="bottom" class="tdFontcolor">第
		     <bean:write name="pagination" property="current" filter="true" />
		     页
		     共
		     <bean:write name="pagination" property="count" filter="true" />
		     条
		     共
		     <bean:write name="pagination" property="pages" filter="true" />
		     页
		    </td>
		    <td  align="right" nowrap class="tdFontcolor">
		        <p align="right"><hrms:paginationdblink name="hrSyncForm" property="pagination" nameId="browseRegisterForm" scope="page">
		     	</hrms:paginationdblink>
		 	</td>
		  </tr>
		</table>
		</logic:equal>
		<logic:equal name="hrSyncForm" property="emporg" value="2">
		<table  border="0" cellspacing="0" width="100%" align="center" cellpadding="0" class="ListTable">
		   <tr>
			<td align="left" style="padding-bottom:5px;" nowrap colspan='<%=list.size()+1%>'>
				&nbsp;<hrms:optioncollection name="hrSyncForm" property="sync_typelst" collection="list" />
		        <!-- 非IE浏览器，name属性不能作为id属性获取节点，得添加styleid属性  wangb 32349  20171027 -->
		        <html:select name="hrSyncForm" property="emporg" styleId="emporg" onchange="emporg11();">
		        <html:options collection="list" property="dataValue" labelProperty="dataName"/>
		        </html:select>
			        <%// if (sync_mode != null && sync_mode.equalsIgnoreCase("trigger")) {%>
			        &nbsp;<bean:message key="id_factory.sys"/>&nbsp;
                    <hrms:optioncollection name="hrSyncForm" property="sysid" collection="list" />
                    <html:select name="hrSyncForm" property="tacitly" onchange="change()">
                    <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                    </html:select>
                    &nbsp;<bean:message key="kq.card.status"/>&nbsp;
                    <hrms:optioncollection name="hrSyncForm" property="statelist" collection="list" />
                    <html:select name="hrSyncForm" property="state" onchange="change()">
                    <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                    </html:select>
                    <%//} %>
				</td>
			</tr>
			<tr >
			<td width="30" align="center" class="TableRow"><input type="checkbox" name="all" onclick="selAll(this);"/></td>
				<logic:iterate id="element1" name="hrSyncForm" property="fieldslist">
					<td class="TableRow" align="center" nowrap>
						<bean:write name="element1" property="itemdesc"/>
					</td>
				</logic:iterate>
			</tr>
			<tbody id="tbd">
		 	<hrms:paginationdb id="element" name="hrSyncForm" sql_str="hrSyncForm.selectsql" order_by="order by B0110_0" table="" where_str="hrSyncForm.wheresql" columns="hrSyncForm.column"  pagerows="18" page_id="pagination" indexes="indexes" keys="unique_id">	
			<%
          	if(i%2==0)
          	{
          	%>
          	<tr class="trShallow">
          	<%}
          	else
          	{%>
          	<tr class="trDeep">
          	<%
         	 }
          	i++;          
%>
<td width="30" align="center" class="RecordRow">
<bean:define id="ids" name="element" property="unique_id"></bean:define>
<input type="checkbox" name="detail" onchange="saveUni(this)" value="${ids}" /></td>
<%			 
			for(int t=0;t<list.size();t++)
			{
				FieldItem fi  = (FieldItem)list.get(t);	
				if(fi.getItemtype().equalsIgnoreCase("A")&&!(fi.getCodesetid().equalsIgnoreCase("0")))
				{
			%>
				<td class="RecordRow" align="left" nowrap>&nbsp;
			 <% if(fi.getItemid().equalsIgnoreCase("b0110_0")){%>
				<!--<logic:equal name="element" property ="codesetid" value="UN" >
				    <hrms:codetoname codeid="UN" name="element" codevalue="<%=fi.getItemid().toLowerCase()%>" codeitem="codeitem" scope="page"/>         
	   				<bean:write name="codeitem" property="codename" />
			    </logic:equal>
			    <logic:notEqual name="element" property ="codesetid" value="UN" >
				 <logic:equal name="element" property ="codesetid" value="UM" >
					<hrms:codetoname codeid="UM" name="element" codevalue="<%=fi.getItemid().toLowerCase()%>" codeitem="codeitem" scope="page"/>         
		   				<bean:write name="codeitem" property="codename" />
				 </logic:equal>
				 <logic:notEqual name="element" property ="codesetid" value="UM" >
					<hrms:codetoname codeid="<%=fi.getCodesetid()%>" name="element" codevalue="<%=fi.getItemid().toLowerCase()%>" codeitem="codeitem" scope="page"/>         
		   				<bean:write name="element" property="<%=fi.getItemid()%>"/>
		   		 </logic:notEqual>
	   		    </logic:notEqual>-->
	   		    <bean:write name="element" property="<%=fi.getItemid().toLowerCase()%>"/>
			   <%} else{%>			   
						<bean:write name="element" property="<%=fi.getItemid().toLowerCase()%>"/>
				<%} %>
			
			</td>	
			<%}else if (str.indexOf(fi.getFieldsetid())>-1){
			%>
			<td class="RecordRow" align="center" nowrap>
            <logic:equal name="element" property="<%=fi.getItemid().toLowerCase()%>" value="0">
             	<!-- 当sys_flag 值为3 在系统中单位视图 本次操作是删除同步的  wangb 20170706 -->
				<logic:equal name="element" property="sys_flag" value="3">(删除)已同步</logic:equal>
				<logic:notEqual name="element" property="sys_flag" value="3">已同步</logic:notEqual>
            </logic:equal>
            <logic:equal name="element" property="<%=fi.getItemid().toLowerCase()%>" value="1">
             新增
            </logic:equal>
            <logic:equal name="element" property="<%=fi.getItemid().toLowerCase()%>" value="2">
             修改
            </logic:equal>
            <logic:equal name="element" property="<%=fi.getItemid().toLowerCase()%>" value="3">
             删除
            </logic:equal>
			
			
			<%}else if (fi.getItemtype().equalsIgnoreCase("N")){
			%>
			<td class="RecordRow" align="right" nowrap>&nbsp;							
				<bean:write name="element" property="<%=fi.getItemid().toLowerCase()%>"/>
			</td>
			<%}else if (fi.getItemtype().equalsIgnoreCase("D")){
					LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
		     		String date=(String)abean.get(fi.getItemid());	
		     		date = PubFunc.replace(date, ".", "-");
		     		if(date.length()>9)	
		     			date = date.substring(0,10);   
			%>
			<td class="RecordRow" align="left" nowrap>&nbsp;							
					<!--<bean:write name="element" property="<%=fi.getItemid().toLowerCase()%>"/>  -->
				<%=date%>				
			</td>
			<%	
			}else{
			%>
			<td class="RecordRow" align="left" nowrap>&nbsp;		
				<bean:write name="element" property="<%=fi.getItemid().toLowerCase()%>" />
			</td>	
			<%	
					}
				}
			%>
  
			    </tr>
	    	</hrms:paginationdb>
			    </tbody>

		</table>
		<table  width="100%" align="center" class="RecordRowP">
		  <tr>
		     <td valign="bottom" class="tdFontcolor">第
		     <bean:write name="pagination" property="current" filter="true" />
		     页
		     共
		     <bean:write name="pagination" property="count" filter="true" />
		     条
		     共
		     <bean:write name="pagination" property="pages" filter="true" />
		     页
		    </td>
		    <td  align="right" nowrap class="tdFontcolor">
		        <p align="right"><hrms:paginationdblink name="hrSyncForm" property="pagination" nameId="browseRegisterForm" scope="page">
		     	</hrms:paginationdblink>
		 	</td>
		  </tr>
		</table>
		</logic:equal>
      <logic:equal name="hrSyncForm" property="emporg" value="3">
			<table  border="0" cellspacing="0" width="100%" align="center" cellpadding="0" class="ListTable">
		   <tr>
			<td align="left" style="padding-bottom:5px;" nowrap colspan='<%=list.size()+1%>'>
				&nbsp;<hrms:optioncollection name="hrSyncForm" property="sync_typelst" collection="list" />
		        <!-- 非IE浏览器，name属性不能作为id属性获取节点，得添加styleid属性  wangb 32349  20171027 -->
		        <html:select name="hrSyncForm" property="emporg" styleId="emporg" onchange="emporg11();">
		        <html:options collection="list" property="dataValue" labelProperty="dataName"/>
		        </html:select>
			        <% //if (sync_mode != null && sync_mode.equalsIgnoreCase("trigger")) {%>
			        &nbsp;<bean:message key="id_factory.sys"/>&nbsp;
                    <hrms:optioncollection name="hrSyncForm" property="sysid" collection="list" />
                    <html:select name="hrSyncForm" property="tacitly" onchange="change()">
                    <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                    </html:select>
			        &nbsp;<bean:message key="kq.card.status"/>&nbsp;
                    <hrms:optioncollection name="hrSyncForm" property="statelist" collection="list" />
                    <html:select name="hrSyncForm" property="state" onchange="change()">
                    <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                    </html:select>
                    <%//} %>
				</td>
			</tr>
			<tr >
			<td width="30" align="center" class="TableRow"><input type="checkbox" name="all" onclick="selAll(this);"/></td>
				<logic:iterate id="element1" name="hrSyncForm" property="fieldslist">
					<td class="TableRow" align="center" nowrap>
						<bean:write name="element1" property="itemdesc"/>						
					</td>
				</logic:iterate>
			</tr>
			<tbody id="tbd">
		 	<hrms:paginationdb id="element" name="hrSyncForm" sql_str="hrSyncForm.selectsql" order_by="order by e01a1_0" table="" where_str="hrSyncForm.wheresql" columns="hrSyncForm.column"  pagerows="18" page_id="pagination" indexes="indexes" keys="unique_id">	
			<%
          	if(i%2==0)
          	{
          	%>
          	<tr class="trShallow">
          	<%}
          	else
          	{%>
          	<tr class="trDeep">
          	<%
         	 }
          	i++;          
          	%>  
<td width="30" align="center" class="RecordRow">
<bean:define id="ids" name="element" property="unique_id"></bean:define>
<input type="checkbox" name="detail" onchange="saveUni(this)" value="${ids}" /></td>
			<% 
			for(int t=0;t<list.size();t++)
			{
				FieldItem fi  = (FieldItem)list.get(t);				 
				if(fi.getItemtype().equalsIgnoreCase("A")&&!(fi.getCodesetid().equalsIgnoreCase("0")))
				{
			%>
				<td class="RecordRow" align="left" nowrap>&nbsp;
			 <% if(fi.getItemid().equalsIgnoreCase("e0122")){%>
					&nbsp;<bean:write name="element" property="<%=fi.getItemid().toLowerCase()%>"/>
				
			<%} else if(fi.getItemid().equalsIgnoreCase("e01a1")){%>
			     &nbsp;<bean:write name="element" property="<%=fi.getItemid().toLowerCase()%>"/>
			   <%} else{%>
			   			&nbsp;<bean:write name="element" property="<%=fi.getItemid().toLowerCase()%>"/>
				<%} %>
			</td>	
			<%}else if (str.indexOf(fi.getFieldsetid())>-1){
			%>
			<td class="RecordRow" align="center" nowrap>
            <logic:equal name="element" property="<%=fi.getItemid().toLowerCase()%>" value="0">
            <!-- 当sys_flag 值为3 在系统中岗位视图 本次操作是删除同步的  wangb 20170706 -->
				<logic:equal name="element" property="sys_flag" value="3">
					(删除)已同步
				</logic:equal>
				<logic:notEqual name="element" property="sys_flag" value="3">
					已同步
				</logic:notEqual>
            </logic:equal>
            <logic:equal name="element" property="<%=fi.getItemid().toLowerCase()%>" value="1">
             新增
            </logic:equal>
            <logic:equal name="element" property="<%=fi.getItemid().toLowerCase()%>" value="2">
             修改
            </logic:equal>
            <logic:equal name="element" property="<%=fi.getItemid().toLowerCase()%>" value="3">
             删除
            </logic:equal>


			<%}else if (fi.getItemtype().equalsIgnoreCase("N")){
			%>
			<td class="RecordRow" align="right" nowrap>						
				&nbsp;<bean:write name="element" property="<%=fi.getItemid()%>"/>&nbsp;
			</td>
			<%}else if (fi.getItemtype().equalsIgnoreCase("D")){
					LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
		     		String date=(String)abean.get(fi.getItemid());	
		     		date = PubFunc.replace(date, ".", "-");
		     		if(date.length()>9)	
		     			date = date.substring(0,10);   
			%>
			<td class="RecordRow" align="left" nowrap>						
					
				&nbsp;<%=date%>				
			</td>
			<%	
			}else{
			%>
			<td class="RecordRow" align="left" nowrap>	
				&nbsp;<bean:write name="element" property="<%=fi.getItemid().toLowerCase()%>" />
			</td>	
			<%	
					}
				}
			%>
  
			    </tr>
	    	</hrms:paginationdb>
			    </tbody>

		</table>
		<table  width="100%" align="center" class="RecordRowP">
		  <tr>
		     <td valign="bottom" class="tdFontcolor">第
		     <bean:write name="pagination" property="current" filter="true" />
		     页
		     共
		     <bean:write name="pagination" property="count" filter="true" />
		     条
		     共
		     <bean:write name="pagination" property="pages" filter="true" />
		     页
		    </td>
		    <td  align="right" nowrap class="tdFontcolor">
		        <p align="right"><hrms:paginationdblink name="hrSyncForm" property="pagination" nameId="browseRegisterForm" scope="page">
		     	</hrms:paginationdblink>
		 	</td>
		  </tr>
		</table>
		</logic:equal>
		<table  width="100%" align="center">
			<tr align="left">
				<td align="left" height="35px;">
					<%if (!"true".equalsIgnoreCase(SystemConfig.getPropertyValue("hiddenReSync"))) {%>
					<html:button styleClass="mybutton" property="sync" onclick="to_hr_sync();">
				  		初始化数据
					</html:button>	   

					<%} %> 
					  
					 <html:button styleClass="mybutton" property="sync" onclick="handwork_send();">
				  		手工发送
					  </html:button>
					    
					<!--<html:button styleClass="mybutton" property="excel" onclick="to_export();">
				  		<bean:message key="goabroad.collect.educe.excel"/>
					</html:button>-->	
					<hrms:priv func_id="3001D0101">
					 <html:button styleClass="mybutton" property="syncstata" onclick="syncstata1();">
				  		修改同步状态
					 </html:button>
					</hrms:priv>
					<html:button styleClass="mybutton" property="orgmapset" onclick="to_set();">
				  		<bean:message key="button.orgmapset"/>
					</html:button>	
					<html:button styleClass="mybutton" property="reassociate" onclick="to_reassociate();">
				  		重新关联
					</html:button>	 
				</td>
			</tr>
		</table>	
</html:form>
<div id='wait' style='position:absolute;top:200;left:250;display:none;'>
  <table border="1" width="400" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
           <tr>
             <td class="td_style" height=24>正在处理....</td>
           </tr>
           <tr>
             <td style="font-size:12px;line-height:200%" align=center>
               <marquee class="marquee_style" direction="right" width="300" scrollamount="5" scrolldelay="10">
                 <table cellspacing="1" cellpadding="0">
                   <tr height=8>
                     <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                    </tr>
                  </table>
               </marquee>
             </td>
          </tr>
        </table>
</div> 
<script>
init();
 musterInitData();
 
if(!getBrowseVersion() || getBrowseVersion() == 10){
	var form = document.getElementsByName('hrSyncForm')[0];
	form.style.margin = '0 4px';
	//form.style.width='99%';
	form.parentNode.style.overflow = '';
	var ListTable = document.getElementsByClassName('ListTable')[0];
	var RecordRowP = document.getElementsByClassName('RecordRowP')[0];
	var select_name = document.getElementsByName('select_name')[0];
	select_name.style.height='20px';
}else{
	var select_name = document.getElementsByName('select_name')[0];
	select_name.style.height='16px';
}
</script>
