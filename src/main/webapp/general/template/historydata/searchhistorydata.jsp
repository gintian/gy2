<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.HashMap"%>
<%@ page import="com.hjsj.hrms.businessobject.sys.options.param.SubsysOperation"%>
<%@ page import="java.util.*,
 				 org.apache.commons.beanutils.LazyDynaBean,
				 com.hjsj.hrms.actionform.general.template.TemplateHistorydataForm,
				 com.hjsj.hrms.actionform.general.template.TemplateForm,
				 com.hjsj.hrms.businessobject.general.template.HistoryDataBo,
				 com.hrms.hjsj.sys.FieldItem,
				 com.hrms.hjsj.sys.DataDictionary"%>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>					 
<%@ page import="com.hrms.frame.utility.AdminCode"%>					 
<%@ page import="com.hrms.frame.utility.CodeItem"%>					 
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>					 
<%@ page import="java.text.SimpleDateFormat"%>					 
				 <link href="/gz/templateset/standard/tableLocked.css" rel="stylesheet" type="text/css">  
<script language="JavaScript" src="/js/wz_tooltip.js"></script>
<script language="JavaScript" src="/js/validateDate.js"></script>
<script language="javascript" src="/general/template/templatelist/templatelist.js"></script>
<style>
.editor
{
	font-family: verdana;
	font-size: 9pt;
	border: #94B6E6 1px solid;

}
div#tbl-container {
	width:100%;
	overflow:auto;
	BORDER-BOTTOM:#94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid; 
}

div#date_panel {
	width:500;
	height: 220;
	overflow:auto;
	z-index:99999;
	background-color:#ffffff;
	BORDER-BOTTOM:#94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid; 
}
div#date_panel2 {
	width:500;
	height: 220;
	overflow:auto;
	z-index:99999;
	background-color:#ffffff;
	BORDER-BOTTOM:#94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid; 
}

	.t_cell_locked_b2 {
		border: inset 1px #94B6E6;
		BACKGROUND-COLOR: #ffffff;
		BORDER-BOTTOM: #94B6E6 1pt solid; 
		BORDER-LEFT: #94B6E6 1pt solid; 
		BORDER-RIGHT: #94B6E6 0pt solid; 
		BORDER-TOP: #94B6E6 0pt solid;
		font-size: 12px;
		border-collapse:collapse; 
		height:22;
	
		background-position : center left;
		left: expression(document.getElementById("tbl-container").scrollLeft); /*IE5+ only*/
		position: relative;
		z-index: 10;
	
	}
	.t_cell_locked_b {
		border: inset 1px #94B6E6;
		BACKGROUND-COLOR: #ffffff;
		BORDER-BOTTOM: #94B6E6 1pt solid; 
		BORDER-LEFT: #94B6E6 0pt solid; 
		BORDER-RIGHT: #94B6E6 1pt solid; 
		BORDER-TOP: #94B6E6 0pt solid;
		font-size: 12px;
		border-collapse:collapse; 
		height:22;
	
		background-position : center left;
		left: expression(document.getElementById("tbl-container").scrollLeft); /*IE5+ only*/
		position: relative;
		z-index: 10;
	
	}
	
	.t_header_locked{
	/*background-image:url(/images/listtableheader_deep-8.jpg);*/
	background-repeat:repeat;
	background-position : center left;
	BACKGROUND-COLOR: #f4f7f7;  
	
	COLOR : black;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 0pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 0pt solid;
	height:30;
	valign:middle;
	font-weight: bold;	
	text-align:center;
	top: expression(document.getElementById("tbl-container").scrollTop); /*IE5+ only*/
	position: relative;
	z-index: 15;
	}
	
	 	
	.t_cell_locked2 {
	  /*  background-image:url(/images/listtableheader_deep-8.jpg);*/
	    
		background-repeat:repeat;
		background-position : center left;
		BACKGROUND-COLOR: #f4f7f7;  
		BORDER-BOTTOM: #94B6E6 1pt solid; 
		BORDER-LEFT: #94B6E6 0pt solid; 
		BORDER-RIGHT: #94B6E6 1pt solid; 
		BORDER-TOP: #94B6E6 0pt solid;
		height:22;
		font-weight: bold;	
		valign:middle;
		COLOR : black;
		left: expression(document.getElementById("tbl-container").scrollLeft); /*IE5+ only*/
		top: expression(document.getElementById("tbl-container").scrollTop); /*IE5+ only*/
		position: relative;
		z-index: 20;
	
	}
	.t_cell_locked {
		
		BACKGROUND-COLOR: #ffffff;
		BORDER-BOTTOM: #94B6E6 1pt solid; 
		BORDER-LEFT: #94B6E6 0pt solid; 
		BORDER-RIGHT: #94B6E6 1pt solid; 
		BORDER-TOP: #94B6E6 0pt solid;
		font-size: 12px;
		border-collapse:collapse; 
		height:22;
	
		background-position : center left;
		left: expression(document.getElementById("tbl-container").scrollLeft); /*IE5+ only*/
		position: relative;
		z-index: 10;
	
	}
	.RecordRow2 {
	
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 0pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 0pt solid;
	font-size: 12px;
	border-collapse:collapse; 
	height:22;
}
.RecordRow3 {
    border: inset 1px #C4D8EE;
    BORDER-BOTTOM: #94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 0pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 0pt solid;
    font-size: 12px;
    
    height:22;
}
/*附件样式*/
.head_left{
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 1pt solid;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	font-size: 12px;
	font-weight: bold;
	height:22;
	background-position : center left;
	background-color:#f4f7f7;	
	valign:middle;
}
.head_middle_right{
	BORDER-LEFT: #C4D8EE 0pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 1pt solid;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	font-size: 12px;
	font-weight: bold;
	height:22;
	background-position : center left;
	background-color:#f4f7f7;	
	valign:middle;
}
.record_left{
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 0pt solid;
	BORDER-BOTTOM: #C4D8EE 1pt solid;
	font-size: 12px;
	height:22;
}
.record_middle_right{
	BORDER-LEFT: #C4D8EE 0pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 0pt solid;
	BORDER-BOTTOM: #C4D8EE 1pt solid;
	font-size: 12px;
	height:22;
}
  </style>
  <hrms:themes></hrms:themes>
<%
	/**
	String aurl = (String)request.getServerName();
	String port=request.getServerPort()+"";
	String prl=request.getProtocol();
	int idx=prl.indexOf("/");
    prl=prl.substring(0,idx);    
	String url_s=prl+"://"+aurl+":"+port;
	String url_p=prl+"://"+aurl+":"+port;
	**/
	String url_p=SystemConfig.getServerURL(request); 
TemplateHistorydataForm templateHistorydataForm=(TemplateHistorydataForm)session.getAttribute("templateHistorydataForm"); 
TemplateForm templateForm=(TemplateForm)session.getAttribute("templateForm"); 
String type1 ="1";
String res_flag="7";
  String isopen_1="true";
if(templateForm!=null){
type1 = templateForm.getType();
res_flag =templateForm.getRes_flag();
 SubsysOperation subsysOperation=new SubsysOperation();
 HashMap map = subsysOperation.getMap();
 if(type1==null)
 type1="1";
if(type1.equals("1"))
   isopen_1=(String)map.get("37");//37是人事异动
   else if(type1.equals("2"))
	    	 isopen_1=(String)map.get("34");//34是薪资管理
	    	  else if(type1.equals("8"))
      		 isopen_1=(String)map.get("39");//34是保险变动
	    	  else if(type1.equals("10"))
	    	 isopen_1=(String)map.get("56");//56组织机构
	    	 else if(type1.equals("11"))
	    	 isopen_1=(String)map.get("57");//57岗位变动
	    	  else if(type1.equals("3"))
				 isopen_1=(String)map.get("51");//警衔管理
			    else if(type1.equals("4"))
			    	 isopen_1=(String)map.get("53");//法官等级
			    else if(type1.equals("5"))
			    	 isopen_1=(String)map.get("54");//关衔管理
			    else if(type1.equals("6"))
			    	 isopen_1=(String)map.get("52");//检察官管理
//	    	 else if(type1.equals("12"))
//	    	 isopen_1=(String)map.get("40");//出国管理处理的业务模板，采用“人事异动”的模板相同的分类及授权
//	    	  else if(type1.equals("21"))
//	    	 isopen_1=(String)map.get("38");//合同管理处理的业务模板，采用“人事异动”的模板相同的分类及授权
	    	  
}
	ArrayList headSetList=templateHistorydataForm.getHeadSetList();
	String    table_name=templateHistorydataForm.getTable_name();
	String select_type = templateHistorydataForm.getSelect_type();
	String display_e0122=templateHistorydataForm.getDisplay_e0122();//记录部门几级显示
/*
	boolean select_0=false;
	boolean select_1=false;
	boolean select_2=false;
	boolean select_3=false;
	boolean select_4=false;
	if("0".equals(select_type))
	select_0=true;
	if("1".equals(select_type))
	select_1=true;
	if("2".equals(select_type))
	select_2=true;
	if("3".equals(select_type))
	select_3=true;
	if("4".equals(select_type))
	select_4=true;
	*/
	String select_0="";
	String select_1="";
	String select_2="";
	String select_3="";
	String select_4="";
	String displayvalue="none";
	if("0".equals(select_type))
		select_0="selected";
	if("1".equals(select_type))
		select_1="selected";
	if("2".equals(select_type))
		select_2="selected";
	if("3".equals(select_type))
		select_3="selected";
	if("4".equals(select_type))
	{
		select_4="selected";
		displayvalue="block";
	}
		int dataSize=templateHistorydataForm.getTemplateHistorydataForm().getList().size();
	
	  
 %>
<%
	String divTop = "32";
	String divHeight = "66";
	UserView userView = (UserView) session
			.getAttribute(WebConstant.userView);

	if(userView != null)
	{
	    String bosflag = userView.getBosflag();
	    if ("hcm".equals(bosflag)){
	        divTop="40";
	        divHeight="72";
	    }
	}
%>
<script>
var tabid='${templateHistorydataForm.tabid}';
var sp_batch="0";
var hasRecordFromMessage='';
var operationtype=''
var table_name='${templateHistorydataForm.table_name}';
var no_sp_opinion=''; 
var isSendMessage="";
var operationname="";
var staticid="";
var codeid='${templateHistorydataForm.codeid}';
var taskid='';
var ins_id='';
var sp_mode='';
var isApplySpecialRole='';
var tasklist_str='';
var url_s="<%=url_p%>";
var warn_id='';
var isEmployee='';
var pageHeight=document.body.clientHeight;
var _basepre="";
var _a0100="";	
var returnflag='${templateHistorydataForm.returnflag}';
var condition="${templateHistorydataForm.condition}";
function outContent(context)
{
	Tip(getDecodeStr(context));
}
var srcobj;
function hiddenPanel()
{
	Element.hide('date_panel');
}	


var flag=false;
function hiddenPanel2()
{
/*
	var obj=document.getElementById("date_panel2");
	var ptop=obj.offsetTop;
    var pleft=obj.offsetLeft;
	
//	alert(ptop+" "+(ptop+220)+"----"+pleft+"  "+(pleft+500)+"   "+window.event.x+"  "+window.event.y);
	if(window.event.x<pleft||window.event.x>(pleft+500))
	{
		Element.hide('date_panel2');
		flag=false;
	}
	else if(window.event.y<ptop||window.event.y>(ptop+220))
	{
		Element.hide('date_panel2');
		flag=false;
	}
	else
		flag=true;
*/
}

function hiddenPanel3()
{
	//52121
	if(!!window.ActiveXObject || "ActiveXObject" in window){
		return;
	}
	//非ie浏览器 隐藏子集内容
	var obj=document.getElementById("date_panel");
	var ptop=obj.offsetTop;
    var pleft=obj.offsetLeft; 
    if(obj.flag==="1"){
	    if($('date_panel').style.display!=='none'){
		    if((window.event.x>pleft+500||window.event.x<pleft)||(window.event.y<ptop||window.event.y>ptop+200)){
		    	Element.hide('date_panel');
		    	obj.flag="0";
		    }
	    }
    }else{
    	obj.flag="1";
    }

/*
	if(flag)
	{
		var obj=document.getElementById("date_panel2");
		var ptop=obj.offsetTop;
	    var pleft=obj.offsetLeft; 
		if(window.event.x<pleft||window.event.x>(pleft+500))
		{
			Element.hide('date_panel2');
			flag=false;
		}
		else if(window.event.y<ptop||window.event.y>(ptop+220))
		{	
			Element.hide('date_panel2');
			flag=false;
		}
	}
*/
}

</script>
<body onclick='hiddenPanel3()' >
<html:form action="/general/template/historydata">
<table width="100%" style='position:absolute;left:5;'border="0" cellspacing="0" cellpadding="0" ><tr><td  align='left' >
	  
	    <table border="0" cellspacing="0"    align="left" cellpadding="0"  ><tr><td>
	    
	    <input type="button" name="p01" value="<bean:message key="button.export"/>" class="mybutton" onclick="downLoadHistory('${templateHistorydataForm.tabid}','pagination.select')"/>
	    <input type="button" name="p21" value="<bean:message key="lable.tz_template.delete"/>" class="mybutton" onclick="deletehistorydata2('pagination.select')"/>
	   &nbsp;<bean:message key="label.by.time.domain"/>
	    <select name='p3' onchange='queryhistory()'>
	    	<option value='0' <%=select_0%> ><bean:message key="edit_report.All"/></option>
	    	<option value='1' <%=select_1%> ><bean:message key="jx.khplan.currentyear"/></option>
	    	<option value='2' <%=select_2%> ><bean:message key="jx.khplan.currentquarter"/></option>
	    	<option value='3' <%=select_3%> ><bean:message key="kq.wizard.bmonth"/></option>
	    	<option value='4' <%=select_4%> ><bean:message key="jx.khplan.timeframe"/></option>
	    </select>
	    		
	    		</td><td id='timescope'   style='display:<%=displayvalue%>'    >
	 &nbsp;&nbsp;&nbsp;<bean:message key="hmuster.label.from"/> <input  type="text" name="startdate" extra="editor" size='15' id="editor4"  
							dropDown="dropDownDate"  value="<bean:write name='templateHistorydataForm' property='startdate'/>">	
	       <bean:message key="kq.init.tand"/>
	       <input  type="text" name="appDate" extra="editor"  id="editor4"   size='15' 
							dropDown="dropDownDate"  value="<bean:write name='templateHistorydataForm' property='appDate'/>">	
	    		&nbsp;<input type="button" name="b_update" value="<bean:message key='button.query'/>" class="mybutton" onClick="queryHistoryData()">     
				</td></tr></table>		      	
						      	
						      	
	</td>
	</tr>
	</table>
 <script language='javascript' >
		document.write("<div id=\"tbl-container\"  onDblClick='hiddenPanel2()'  style='position:absolute;top:<%=divTop%>;left:5;height:"+(document.body.clientHeight-<%=divHeight%>)+";width:99%'  >");
    </script>	
<table width="100%" border="0" cellspacing="0"    align="center" cellpadding="0"  >
			   	  <thead>
			        <tr>
			       <td align="center"   class='t_cell_locked2 common_border_color'     height="25"   nowrap >
					    <input   type="checkbox" name="selbox"  onclick="batch_select(this,'pagination.select');"  />
			       </td>  
			      <td align="center"   class='t_cell_locked2 common_border_color'     height="25"   nowrap >
			  		     &nbsp;序号&nbsp;
			       </td>   
			        <% 
			 			
			         int  showDesc_index=-1; // 显示 变换前和变化后列下标
			        
			         LazyDynaBean abean=null;
			         	for(int i=0;i<headSetList.size();i++){ 
				        	abean=(LazyDynaBean)headSetList.get(i);
				        	if(abean==null||abean.get("hz")==null)
				        	continue;
				        	String hz=((String)abean.get("hz")).replaceAll("`","");
			        %>
			        
			         <td align="center"  class='t_header_locked common_border_color'      height="25"   nowrap >
					   &nbsp;&nbsp;&nbsp; <%=hz%>&nbsp;&nbsp;&nbsp;
			         </td>         
			 		
			 		<% 
			 			}
			 		 %>
			           </tr>
			   	  </thead>
  <%
			   	   int seq=0;
			   	   int j=1;
			   	   LazyDynaBean _abean=null;
			   	   String context="";
			   	   HistoryDataBo historyDataBo=new HistoryDataBo();
			   	   %>	 
			   	  <hrms:paginationdb id="element" name="templateHistorydataForm" sql_str="templateHistorydataForm.strsql" table="" where_str="" columns="templateHistorydataForm.columns" order_by="templateHistorydataForm.orderBy" page_id="pagination" pagerows="${templateHistorydataForm.pagerows}" distinct="" indexes="indexes" keys="">
			   	  	 <% 
			   	  	 String columns=","+templateHistorydataForm.getColumns()+",";
				     LazyDynaBean  _abean2=(LazyDynaBean)pageContext.getAttribute("element");
			         String  basepre=_abean2.get("basepre")==null?"":(String)_abean2.get("basepre");
			   	     String  basepre2=_abean2.get("basepre2")==null?"":(String)_abean2.get("basepre2");
			   	     String  a0100=_abean2.get("a0100")==null?"":(String)_abean2.get("a0100");
			   	     String  rowNum=_abean2.get("num")==null?"":(String)_abean2.get("num");
			   	     String id=_abean2.get("id")==null?"":(String)_abean2.get("id");
		        	 String from_id =_abean2.get("from_id")==null?"":(String)_abean2.get("from_id");
		        	 String  task_id=_abean2.get("task_id")==null?"":(String)_abean2.get("task_id");
			   	  	 if(seq%2==0)
			          {
			          %>
			          <tr class="trShallow"  onclick='tr_onclick(this,"#F3F5FC");setCurrent("<%=a0100%>","<%=basepre%>")' >
			          <%}
			          else
			          {%>
			          <tr class="trDeep" onclick='tr_onclick(this,"#E4F2FC");setCurrent("<%=a0100%>","<%=basepre%>")'>
			          <%
			          }
					   	%>
			        <td align="center"   class='t_cell_locked common_border_color'     height="25"   nowrap >
			   	  	 <hrms:checkmultibox name="templateHistorydataForm" property="pagination.select" value='<%=j+""%>' indexes="indexes"/>&nbsp;
					</td>
					 <input type="hidden" name="ids" value="<%=id%>">
			        <td align="center"   class='t_cell_locked common_border_color'     height="25"   nowrap >
							<%=rowNum %>
					</td>
					     <% 			
						SimpleDateFormat df2=new SimpleDateFormat("yyyy-MM-dd");//提交时间的显示样式
			        	for(int i=0;i<headSetList.size();i++){ 
			        		 _abean=(LazyDynaBean)headSetList.get(i);
				        	String hz=((String)_abean.get("hz")).replaceAll("`","");
				        	String codeid=(String)_abean.get("codeid");
				        	String isLock=(String)_abean.get("isLock");
				        	String chgstate=(String)_abean.get("chgstate");
				        	String field_name=(String)_abean.get("field_name");
				        	String field_type=(String)_abean.get("field_type");
				        	String subflag=(String)_abean.get("subflag");
				        	String setname=(String)_abean.get("setname");
				        	String sub_domain = _abean.get("sub_domain")==null?"":(String)_abean.get("sub_domain");			        	
				        	String classname="RecordRow3 common_border_color";
				        	String isvar=(String)_abean.get("isvar");
							String formula = _abean.get("formula")==null?"":""+_abean.get("formula");
							int disformat = Integer.parseInt(_abean.get("disformat")==null?"0":(String)_abean.get("disformat"));//日期型字段有多种显示方式。如2012-3-4,2013.9.9

				        	String align="left";
				        	if(field_type.equalsIgnoreCase("N"))
				        		align="right";
				        	else if(subflag.equalsIgnoreCase("1")||subflag.equals("1"))
								align="center";				        	
				        	String field_name2=field_name;
				        	String field_name_copy = field_name;
				        	if(field_name_copy.indexOf("_")!=-1){//field_name_copy把field_name的下划线去掉
				        		field_name_copy = field_name_copy.substring(0, field_name_copy.indexOf("_"));
				        	}
				        	String sub_domain_id = "";
							if(_abean.get("sub_domain_id")!=null&&"1".equals(_abean.get("chgstate"))){
								sub_domain_id = (String)_abean.get("sub_domain_id");
							if(sub_domain_id!=null&&sub_domain_id.length()>0){
								sub_domain_id ="_"+sub_domain_id;
							}else{
								sub_domain_id="";
							}
							}
				        	if(_abean2.get(field_name2)!=null){
				        		if("basepre".equals(field_name2)){
				        			context = basepre2;
				        		}else
					        		context=_abean2.get(field_name2)==null?"":(String)_abean2.get(field_name2);
				        	}
				        	context = context.replace("\r\n","<br>").replace("`","<br>");
				        	context = context.replace("%26amp;", "&");
					        	
					        String m_mouse_str="";
					        String sub_click_event_str="";
					         if(field_name2.equals("from_id")&&from_id!=null&&!from_id.trim().equals("")){
					        	align="center";
					        	//安全平台改造,将id进行加密 
					        	sub_click_event_str=" style=\"cursor:hand\"  onclick=\"showhistoryPdf('"+PubFunc.encrypt(from_id)+"')\" ";
					        	}
					        if(subflag.equals("1"))
							{
								if(chgstate.equals("1"))
								{
									context="现子集";
								}
								else
								{
									context="拟子集";
												        
					        	}
					        	String seqnum="";
					        	
					        	sub_click_event_str=" style=\"cursor:hand\"  onclick=\"showSubTable('"+table_name+"','"+a0100+"','"+basepre+"','22','"+id+"','T_"+setname+sub_domain_id+"_"+chgstate+"','"+sub_domain+"',this)\" ";
					        }else if(field_name.equals("affixfile"))//显示附件界面
					        {
					        	align="center";
					        	context="...";
					        	sub_click_event_str=" style=\"cursor:hand\" id=\"show_affix\"  onclick=\"showAffixfileTable('"+task_id+"','22',this)\" ";
					        }else if(field_name2.equals("content_pdf")){
					        	align="center";
					        	//安全平台改造，将id进行加密 
					        	context="<img src='/images/print.gif'  style=\"cursor:hand\"    onclick=\"showhistoryPdf('"+PubFunc.encrypt(id)+"')\"   border=0>";
					        }else if(isvar.equals("1")&&columns.toUpperCase().indexOf(","+field_name.toUpperCase()+",")==-1){//如果是临时变量
								context="";
							}else if(field_type.equalsIgnoreCase("M")&&context!=null&&context.length()>0&&!context.equals("NULL")){
					        	FieldItem item=DataDictionary.getFieldItem(field_name_copy);
								if(item!=null&&item.getItemtype()!=null){
									if(item.getItemtype().equalsIgnoreCase("M")){//如果字段是备注型
										
									}else if(item.getItemtype().equalsIgnoreCase("D"))//如果字段是日期型
									{
										/**yyyy-MM-dd*/
										String values ="";
										if(context.indexOf("`")!=-1){
											String strs[] =context.split("`");
											for(int num=0;num<strs.length;num++){
												if(strs[num].trim().length()>0){
													values += historyDataBo.formatDateValue(strs[num],formula,disformat);
													if(num<strs.length-1){
														values+="`";
													}
												}
											}
										}else{
											values = historyDataBo.formatDateValue(context,formula,disformat);
										}
										context=values.replace("\r\n","<br>").replace("`","<br>");
									}else if(item.getItemtype().equalsIgnoreCase("N")){//如果字段是数值型
										int ndec=disformat;//小数点位数
										String prefix=((formula==null)?"":formula);
										String values ="";
										if(context.indexOf("`")!=-1){
											String strs[] =context.split("`");
											for(int num=0;num<strs.length;num++){
												if(strs[num].trim().length()>0){
													values += prefix+PubFunc.DoFormatDecimal(strs[num],ndec);
													if(num<strs.length-1){
														values+="`";
													}
												}
											}
										}else{
											values = prefix+PubFunc.DoFormatDecimal(context,ndec);
										}
										context=values.replace("\r\n","<br>").replace("`","<br>");										
									}else{//如果字段是字符型或代码型
										String values ="";
										if(context.indexOf("`")!=-1){
											String strs[] =context.split("`");
											for(int num=0;num<strs.length;num++){
												if(strs[num].trim().length()>0){
													if(codeid!=null&&!codeid.equals("0")){
														values += AdminCode.getCodeName(codeid,strs[num]);
													}else{
														values += strs[num];
													}
													if(num<strs.length-1){
														values+="`";
													}
												}
											}
										}else{
											if(codeid!=null&&!codeid.equals("0"))
												values = AdminCode.getCodeName(codeid,context);
											else
												values = context;
										}
										context=values.replace("\r\n","<br>").replace("`","<br>");
								}
								String context_bak=SafeCode.encode(context);
					        	m_mouse_str="onmouseover=\"outContent('"+context_bak+"')\"   onmouseout=\"UnTip()\"   ";
					        	context="<font color='blue' >有数据</font>";
					        	}
					        }else if(field_type.equalsIgnoreCase("D")){//如果是日期
								if(field_name.equals("lasttime")){
									context=df2.format(df2.parse(context));	
								}else{
									context=historyDataBo.formatDateValue(PubFunc.FormatDate(context),formula,disformat);
								}
							}else{//如果是字符型或数字型
									if(field_type.equalsIgnoreCase("A")&&!codeid.equals("0"))
									{
										if(codeid.toUpperCase().equals("UM")){
											if(AdminCode.getCodeName(codeid,context).equals(""))
												codeid="UN";	
										}
										if(isvar.equals("1")&&field_type.equalsIgnoreCase("A")&&codeid.equals("")){
											
										}else{
											if(codeid.toUpperCase().equals("UM")){
											 String value="";
												if(Integer.parseInt(display_e0122)==0){
													value=AdminCode.getCodeName("UM",context);
											 	}
												else
												{
													CodeItem item=AdminCode.getCode("UM",context,Integer.parseInt(display_e0122));
									    	    	if(item!=null)
									    	    	{
									    	    		value=item.getCodename();
									        		}
									    	    	else
									    	    	{
									    	    		value = AdminCode.getCodeName("UM",context);
									    	    	}
												}	
												context=value;
											}else{
										    	context=AdminCode.getCodeName(codeid,context);
											}
										}
									}
									else{
										if(field_name.lastIndexOf("_")!=-1&&field_name.substring(0, field_name.lastIndexOf("_")).equalsIgnoreCase("codesetid")){
											String name ="";
											if(context.equalsIgnoreCase("UN")){
												name="单位";
											}else if(context.equalsIgnoreCase("UM")){
												name="部门";
											}else if(context.equalsIgnoreCase("@K")){
												name="职位";
											}
										}									
									}
							}
					        if(context!=null&&(context.length()==0||context.equals("NULL")))
					        {
					        	context="&nbsp;";
					        	if(field_type.equalsIgnoreCase("M"))
						        	context="无数据";
					        }	
			                out.println("<td align='"+align+"'  "+m_mouse_str+"  "+sub_click_event_str+"  class='"+classname+"'   height='25'   nowrap >&nbsp;"+context+"&nbsp;</td>");
			         }
					     
			 			j++;
			 			seq++;
			 		 %>
			   	  	</tr>
			   	  </hrms:paginationdb>
			   	  </table>
  </div>
   <script language='javascript' >
		document.write("<div  id='page'  style='position:absolute;left:5;top:"+(document.body.clientHeight-35)+";width:99%'  >");
	</script>
			   	  	 <table  width="100%" align="center"  class='RecordRowP' >
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            <bean:message key="label.page.serial"/>
					<bean:write name="pagination" property="current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="pagination" property="count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="pagination" property="pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	        <td  valign="middle"  align="right" nowrap class="tdFontcolor">
				<p align="right">
					<hrms:paginationdblink name="templateHistorydataForm" property="pagination" nameId="templateHistorydataForm" scope="page">
					</hrms:paginationdblink>
			</td>
		</tr>
   </table> 
</div>
<html:hidden  name="templateHistorydataForm" property="hmuster_sql"  />
 <div id="date_panel" class="common_border_color" style='display:none'  onBlur='hiddenPanel()'  ></div>
  <div id="date_panel2"  class="common_border_color" style='display:none' onBlur='hiddenPanel2()'  ></div>
  <a href="<%=url_p%>" style="display:none" id="hostname">for vpn</a>
</html:form>
<script language="javascript">
document.onclick = function (event)  
    {     
        var e = event || window.event;  
        var elem = e.srcElement||e.target;  
               
        while(elem)  
        {   
            if(elem.id == "date_panel2" || elem.id ==  'show_affix')  
            {  
                return;  
            }  
            elem = elem.parentNode;       
        }  
        Element.hide('date_panel2');
    }
 function setCurrent(a_a0100,a_base)
  {
     _basepre=a_base;
     _a0100=a_a0100;
  }
  </script>
 </body> 
  