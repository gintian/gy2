<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.sys.cms.ChannelContentDetailForm"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    String bosflag= userView.getBosflag();//得到系统的版本号
    
%>  
<html>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<link href="/css/xtree.css" rel="stylesheet" type="text/css" ></link>
<link href="/components/tableFactory/tableGrid-theme/tableGrid-theme-all.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="../../ext/ext-all.js" ></script>
<script type="text/javascript" src="../../ext/ext-lang-zh_CN.js" ></script> 
<script type="text/javascript" src="../../ext/rpc_command.js"></script>
<script language="javascript" src="/js/validate.js"></script>
<head>
<%
	int i=0;
	ChannelContentDetailForm channelContentDetailForm = (ChannelContentDetailForm)session.getAttribute("channelContentDetailForm");
	String path=channelContentDetailForm.getPath();
%>
<style type="text/css">
#window-body{
	background-color: white;
}
</style>
<script type="text/javascript">

<!--
function backup()
	{
	  jinduo(1);
	  var hashvo=new ParameterSet();
      hashvo.setValue("path","<%=path%>");
	
    var request=new Request({method:'post',asynchronous:true,onSuccess:backup_ok,functionId:'3000000189'},hashvo);			   	
	}
	function backup_ok(outparameters)
   {
     closejinduo(1);
     var outName=outparameters.getValue("filename");
     if(outName=="1")
     {
         alert("要备份的文件不存在，备份失败");
         return;
     }
     var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName,"zip");
    }
	var objlist = new Object();
	function Reduction()
	{
		objlist = new Object();
	   var theurl="/hire/parameterSet/configureParameter/select_reduction_file.do?br_init=init&isclose=1";
	   Ext.widget("window",{
		   id:'window',
    	   modal:true,
    	   title:'还原',
           layout:'fit',
           height:210,
           width:380,
           border:0,
           html:'<iframe src="'+theurl+'" width=370 height=210 frameborder="0" scrolling="no"></iframe>',
           resizable:false
   	 }).show();
	   	//Ext.getDom("window-1009-body").style.background-color="white";
	return;
	   //var iframe_url="/gz/templateset/tax_table/iframe_tax.jsp?src="+theurl;
       //var objlist =window.showModalDialog(iframe_url,null,"dialogWidth=350px;dialogHeight=250px;resizable=yes;status=no;");  
	   
	}

	function Reduction_return(){
		if(objlist == null)
		  	 return;
		   var obj=new Object();
	       obj.dir=objlist.dir;
	       jinduo(2);
	       var hashVo=new ParameterSet();
	       hashVo.setValue("dir",obj.dir);
	       hashVo.setValue("path","<%=path%>");
	       var In_parameters="flag=1";
	       //var request=new Request({method:'post',asynchronous:true,parameters:In_parameters,onSuccess:export_in_tax_ok,functionId:'3000000188'},hashVo);
	}
	
	function export_in_tax_ok(outparameters)
	{
	   closejinduo(2);
	}
function closejinduo(type){
	   var waitInfo;
	if(type==1)
	     waitInfo=eval("wait");
	else
	     waitInfo = eval("wait2");
	waitInfo.style.display="none";
     }
function jinduo(type){
	//var x=document.body.scrollLeft+event.clientX-250;
    //var y=document.body.scrollTop+event.clientY; 
	var waitInfo;
	if(type==1)
	     waitInfo=eval("wait");
	else
	     waitInfo = eval("wait2");
	
	waitInfo.style.top=100;
	waitInfo.style.left=100;
	waitInfo.style.display="block";
}
function channelDetail_move(){
channelContentDetailForm.action="/sys/cms/channelDetailMoveList.do";
channelContentDetailForm.submit();
}

//屏蔽右键,实在没有办法的采用此办法,解决重复提交问题
document.body.oncontextmenu=function(){return false;};
//-->
</script>
</head>
<hrms:themes></hrms:themes>
<body>
  <html:form action="/sys/cms/queryChannel">
  <div id='wait' style='position:absolute;top:285;left:120;display:none;'>
		<table border="1" width="37%" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
			<tr>
				<td class="td_style" height=24>
					正在进行备份操作，请稍候...
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
		<table border="1" width="37%" cellspacing="0" cellpadding="4" style="border-collapse: collapse" bgcolor="#F7FAFF" height="87" align="center">
			<tr>
				<td bgcolor="#057AFC" style="font-size:12px;color:#ffffff" height=24>
					正在进行还原操作，请稍候...
				</td>
			</tr>
			<tr>
				<td style="font-size:12px;line-height:200%" align=center>
					<marquee style="border:1px solid #000000" direction="right" width="300" scrollamount="5" scrolldelay="10" bgcolor="#ECF2FF">
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
<%
    if(bosflag!=null&&!bosflag.equals("hcm")){
%>
<Br>
<%
}
%>
	<div id="detail" style="margin-top:0px;">
   <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" style="margin-top:2px;">
   <thead>
   <tr>
            <td align="center" class="TableRow" nowrap width="5%">
             <bean:message key="lable.channel_detail.choose"/>&nbsp;&nbsp;
             </td>
           
            <td align="center" class="TableRow" nowrap>
            <bean:message key="lable.channel_detail.title"/>
            </td>
            
          
            <td align="center" class="TableRow" nowrap>
	         <bean:message key="lable.channel_detail.out_url"/> 
	   		 </td>
            <td align="center" class="TableRow" nowrap>
	         <bean:message key="lable.channel_detail.params"/>	
	    	</td>            
		    <td align="center" class="TableRow" nowrap width="5%">
				<bean:message key="lable.channel.visible_type"/>	
		    </td>
		      <td align="center" class="TableRow" nowrap width="5%">
	         <bean:message key="column.operation"/>
	   	 	</td>
		     <td align="center" class="TableRow" nowrap width="5%">
				   &nbsp; 	
		    </td>	        	        	        
         </tr>
   	  </thead>
   	  
   	  <hrms:extenditerate id="element" name="channelContentDetailForm" property="contentListForm.list" indexes="indexes"  pagination="contentListForm.pagination" pageCount="100" scope="session">
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
            <td align="center" class="RecordRow" nowrap>
                 <logic:equal name="element" property="state" value="0">
	     		  	 <hrms:checkmultibox name="channelContentDetailForm" property="contentListForm.select" value="true" indexes="indexes"/>&nbsp;
            	 </logic:equal> 
	    	</td>                	  
            <td align="left" class="RecordRow" nowrap>
                 <logic:equal name="element" property="state" value="0">            
           			 <bean:write name="element" property="title" />
            	 </logic:equal>             
	   	 	</td>
            
            <td align="left" class="RecordRow" nowrap>
            &nbsp;<bean:write name="element" property="out_url" />&nbsp;
	    	</td>            
		    <td align="left" class="RecordRow" nowrap>
			&nbsp;<bean:write name="element" property="params" />&nbsp;
		    </td>
		    <td align="center" class="RecordRow" nowrap>
				<logic:equal name="element" property="content_type" value="0"> 
					<bean:message key="lable.content_channel.url"/>
				</logic:equal> 
				<logic:equal name="element" property="content_type" value="1"> 
					<bean:message key="lable.channel_detail.content"/>
				</logic:equal>  				   
		    </td>
		    <td align="center" class="RecordRow" nowrap>
                      <bean:define id="content_id" name="element" property="content_id"/>
			         <%
			         	String str1 = "content_id="+content_id.toString();
			         	
			         %>
            		 <a href="/sys/cms/addContentChannelDetail.do?b_edit=edit&encryptParam=<%=PubFunc.encrypt(str1)%>">
	        		 <img src="/images/edit.gif" border="0"></a>	
	        		  <logic:equal name="element" property="state" value="0">
	        		  
	        		  </logic:equal> 
            	     	          
	   		 </td>
		    
		    <td align="center" class="RecordRow" nowrap>
				<logic:equal name="element" property="visible" value="1"> 
					<bean:message key="lable.channel.visible"/>
				</logic:equal> 
			  <logic:equal name="element" property="visible" value="0"> 
					<bean:message key="lable.channel.hide"/>
				</logic:equal>  
		    </td>
       </tr>		    
	</hrms:extenditerate>
	</table>
	<table  width="100%" align="center">
          <tr>
            <td align="center">
                <logic:notEqual name="channelContentDetailForm" property="channel_id" value="-1">
            	<hrms:submit property="b_add" styleClass="mybutton"><bean:message key="button.add_content"/></hrms:submit>
            	<hrms:submit property="b_delete" styleClass="mybutton" onclick="return checks();"><bean:message key="button.delete_content"/></hrms:submit>
            	<hrms:submit property="b_move" styleClass="mybutton" onclick="channelDetail_move();"><bean:message key="button.change_content_sort"/></hrms:submit>
		       </logic:notEqual>
            	<input type="button" value="备份" class="mybutton" onclick="backup();"/>
		        <input type="button" value="还原" class="mybutton" onclick="Reduction();"/>
           </td>
         </tr>          
	</table>
	<html:hidden name="channelContentDetailForm" property="channel_id"/>
	</div>
</html:form>
</body>
</html>
<script type="text/javascript">
	function checks(){
		var inputs = document.getElementsByTagName("input");//获取所有的input标签对象
		var checkboxArray = [];//初始化空数组，用来存放checkbox对象。
		var num = 0;
		for(var i=0;i<inputs.length;i++){
		  var obj = inputs[i];
		  if(obj.type=='checkbox'){
		     if(obj.checked){
				num++;
			}
		  }
		}
		if(num<1)
		{
			alert("请选择需要删除的数据！");
			return false;
		}else{
			return ifdelinfo();
		}
	}
</script>
