<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@page import="com.hrms.frame.dao.RecordVo"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<%
	int i=0;
%>
<script language="JavaScript" src="../../module/utils/js/template.js"></script>
<script type="text/javascript" src="../../js/hjsjUrlEncode.js"></script>
<script language="javascript">
<!--
function changebase()
{
	var target_url="/system/dbinit/inforlist.do?b_chgbase=link";
	var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
	//var w =(window.screen.width-550)/2;
	//var h=(window.screen.height-400)/2;
	// var return_vo= window.showModalDialog(iframe_url, 'newwindow',
     //    "dialogWidth:450px; dialogHeight:250px;resizable:no;center:yes;scroll:no;status:no");
     //弹窗 宽和高固定 ，适应屏幕，在低分辨率屏幕下会出滚动条  wangb 20190509 bug 47261
    var theUrl = iframe_url;
    Ext.create('Ext.window.Window', {
        id:'changebase',
        height: 300,
		title:'调整人员库',
        width: 480,
        resizable:false,
        modal:true,
        autoScroll:false,
        autoShow:true,
        html:'<iframe style="background-color:#ffffff " frameborder="0" scrolling="no" height="100%" width="100%" src="'+theUrl+'"></iframe>',
        renderTo:Ext.getBody()
    });
}
function formationStoreroom(type,infor)
{
	var target_url="/system/dbinit/inforlist.do?b_formation=link`type="+type+"`infor="+infor;
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
    // var dw=540,dh=400,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
    // var return_vo= window.showModalDialog(iframe_url,infor,
    //     "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
   	// if(return_vo!=null){
   	// 	parent.frames["mil_menu"].location.reload();
   	// }
    return_vo ='';
    var theUrl = iframe_url;
    Ext.create('Ext.window.Window', {
        id:'formationStoreroom',
        height: 430,
        width: 580,
        resizable:false,
        modal:true,
        autoScroll:false,
        autoShow:true,
        html:'<iframe style="background-color:#ffffff " frameborder="0" scrolling="no" height="100%" width="100%" src="'+theUrl+'"></iframe>',
        renderTo:Ext.getBody(),
        listeners: {
            'close': function () {
                if (return_vo) {
                    parent.frames["mil_menu"].location.reload();
                }
            }
        }

    });
}
function deletefieldset(infor)
{
	var target_url="/system/dbinit/inforlist.do?b_deletefieldset=link`infor="+infor;
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
    // var return_vo= window.showModalDialog(iframe_url,infor,
    //     "dialogWidth:540px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
    // if(return_vo!=null){
   	// 	parent.frames["mil_menu"].location.reload();
   	// }
    return_vo ='';
    var theUrl = iframe_url;
    Ext.create('Ext.window.Window', {
        id:'deletefieldset',
        height: 400,
        width: 570,
        resizable:false,
        modal:true,
        autoScroll:false,
        autoShow:true,
        html:'<iframe style="background-color:#ffffff " frameborder="0" scrolling="no" height="100%" width="100%" src="'+theUrl+'"></iframe>',
        renderTo:Ext.getBody(),
        listeners: {
            'close': function () {
                if (return_vo) {
                    parent.frames["mil_menu"].location.reload();
                }
            }
        }

    });
}
function affirmInit()
{
	if(confirm("你正准备初始化数据库数据，如果选择确定，将清空现有的全部数据。")){
		jindu2();
		return true;
	}else{
		return false;
	}
}
//初始化结构
function affirmInits()
{
	if(confirm("你正准备初始化数据库结构，如果选择确定，将清空现有的全部数据和应用库结构。")){
		jindu();
		dbinitForm.action="/system/dbinit/inforlist.do?b_initstrut=link";
		dbinitForm.submit();
	}else{
		return false;
	}
}

function jindu(){
	//新加的，屏蔽整个页面不可操作
	document.all.ly.style.display="";   
	document.all.ly.style.width=document.body.clientWidth;   
	document.all.ly.style.height=document.body.clientHeight; 
	
	var x=(window.screen.width-700)/2;
    var y=(window.screen.height-500)/2; 
	var waitInfo=eval("wait");
	waitInfo.style.top=y;
	waitInfo.style.left=x;
	waitInfo.style.display="";
}
function jindu1(){
	//新加的，屏蔽整个页面不可操作
	document.all.ly.style.display="";
	document.all.ly.style.width=document.body.clientWidth;
	document.all.ly.style.height=document.body.clientHeight;
	var x=(window.screen.width-700)/2;
    var y=(window.screen.height-500)/2; 
	var waitInfo=eval("wait1");
	waitInfo.style.top=y;
	waitInfo.style.left=x;
	if(getBrowseVersion() =='10'){
		waitInfo.style.posTop=y;
		waitInfo.style.posLeft=x;
	}
	waitInfo.style.display="";
}
function jindu2(){
	//新加的，屏蔽整个页面不可操作
	document.all.ly.style.display="";   
	document.all.ly.style.width=document.body.clientWidth;   
	document.all.ly.style.height=document.body.clientHeight; 
	
	var x=(window.screen.width-700)/2;
    var y=(window.screen.height-500)/2; 
	var waitInfo=eval("wait2");
	waitInfo.style.top=y;
	waitInfo.style.left=x;
	waitInfo.style.display="";
}
//-->
</script>
<div id="ly" style="position:absolute;top:0px;FILTER:alpha(opacity=10); opacity:0.1;background-color:#FFF;z-index:2;left:0px;display:none;"></div>
<div id='wait' style='position:absolute;top:285;left:80;display:none;'>
		<table border="1" width="300" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
			<tr>
				<td class="td_style" height=24>
					系统正在初始化结构，请稍候....
				</td>
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
<div id='wait2' style='position:absolute;top:285;left:80;display:none;'>
		<table border="1" width="300" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
			<tr>
				<td class="td_style" height=24>
					系统正在初始化数据，请稍候....
				</td>
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
<div id='wait1' style='position:absolute;top:285;left:80;display:none;'>
		<table border="1" width="300" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
			<tr>
				<td class="td_style" height=24>
					正在刷新数据字典,请稍候......
				</td>
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
<html:form action="/system/dbinit/inforlist">
<table width="90%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
   	  <thead>
           <tr>
        
        <td align="center" class="TableRow" nowrap>
		<bean:message key="system.infor.set"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="system.infor.pre"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="system.infor.key"/>&nbsp;
	    </td>
           
        <td align="center" class="TableRow" nowrap>
		<bean:message key="system.infor.oper"/>            	
	    </td>
            	    	    		        	        	        
           </tr>
   	  </thead>
      <hrms:extenditerate id="element" name="dbinitForm" property="listForm.list" indexes="indexes"  pagination="listForm.pagination" pageCount="20" scope="session">
          <%
          if(i%2==0)
          {
          %>
          <tr class="trShallow" onClick="javascript:tr_onclick(this,'')">
          <%}
          else
          {%>
          <tr class="trDeep" onClick="javascript:tr_onclick(this,'')">
          <%
          }
          i++;          
          %>  
        <%
        	RecordVo vo = (RecordVo)pageContext.getAttribute("element");
        	String classpre = vo.getString("classpre");
         %>
        <td align="left" class="RecordRow" nowrap>
               &nbsp;<a href="/system/dbinit/fieldsetlist.do?b_query=link&encryptParam=<%=PubFunc.encrypt("infor="+classpre)%>"><bean:write name="element" property="string(classname)" filter="true"/></a>&nbsp;
	    </td>
         
            <td align="left" class="RecordRow" nowrap>
                   &nbsp;<bean:write  name="element" property="string(classpre)" filter="true"/>&nbsp;
	    </td>
            <td align="left" class="RecordRow" nowrap>
                    &nbsp;<bean:write  name="element" property="string(keyfield)" filter="true"/>&nbsp;
	    </td>
         <td align="center" class="RecordRow" nowrap>
         		<hrms:priv func_id="3007107">
            	<a href="javascript:formationStoreroom('0','<bean:write  name="element" property="string(classpre)" filter="true"/>')"><bean:message key="kjg.title.design"/></a>&nbsp;&nbsp;
            	</hrms:priv>
            	<hrms:priv func_id="3007108">
            	<a href="javascript:formationStoreroom('1','<bean:write  name="element" property="string(classpre)" filter="true"/>')"><bean:message key="label.kh.edit"/></a>&nbsp;&nbsp;
            	</hrms:priv>
            	<hrms:priv func_id="3007109">
            	<a href="javascript:deletefieldset('<bean:write  name="element" property="string(classpre)" filter="true"/>')"><bean:message key="label.kh.del"/></a>
	    		</hrms:priv>
	    </td>
        </tr>
      </hrms:extenditerate>
        
</table>
<table  width="90%" align="center">
          <tr>
            <td align="center" height="35px;">
            <hrms:priv func_id="3007106">
           	 <hrms:submit styleClass="mybutton" property="b_refresh" onclick="jindu1()">
            		<bean:message key="button.refresh"/>
	 	     </hrms:submit>	
	 	     </hrms:priv> 
	 	     <hrms:priv func_id="3007103">
          	 <hrms:submit styleClass="mybutton" property="br_colcard">
            		<bean:message key="button.colcard"/>
	 	     </hrms:submit>
	 	     </hrms:priv>
	 	     <hrms:priv func_id="3007104">
           	 <hrms:submit styleClass="mybutton" property="b_outitem">
            		<bean:message key="button.output.item"/>
	 	     </hrms:submit>
	 	     </hrms:priv>
	 	     <hrms:priv func_id="3007101">
             <hrms:submit styleClass="mybutton" property="b_initdata" onclick="return affirmInit();">
            		<bean:message key="button.initdata"/>
	 	     </hrms:submit>
	 	     </hrms:priv>
	 	     <hrms:priv func_id="3007102">
          	 <html:button styleClass="mybutton" property="b_initstrut" onclick="return affirmInits();">
            		<bean:message key="button.initstrut"/>
	 	     </html:button>
	 	     </hrms:priv>
	 	     <hrms:priv func_id="3007105">
	 	     <input type="button" name="b_chgbase" class="mybutton" value="<bean:message key="button.chg.base"/>" onclick="changebase()" />	     
           	 </hrms:priv>
           	 	     
            </td>
          </tr>          
</table>
</html:form>
<%String param = request.getParameter("b_initstrut");
	if(param!=null&&param.equals("link")){
 %>
<script type="text/javascript">
<!--
parent.frames["mil_menu"].location.reload();

//-->
</script>
<%}%>
