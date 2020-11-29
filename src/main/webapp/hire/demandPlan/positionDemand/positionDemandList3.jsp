<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,com.hjsj.hrms.actionform.hire.demandPlan.PositionDemandForm"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
	response.setHeader("Pragma", "No-cache");
 	response.setHeader("Cache-Control", "no-cache");
 	response.setDateHeader("Expires", 0);
	
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	PositionDemandForm positionDemandForm=(PositionDemandForm)session.getAttribute("positionDemandForm");
	String model=positionDemandForm.getModel().trim();
    String returnflag=positionDemandForm.getReturnflag();
	/*
	Enumeration e = session.getAttributeNames();
    while(e.hasMoreElements()) {
        System.out.println(e.nextElement());
    }
    */
    String bosflag= userView.getBosflag();//得到系统的版本号
%>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script language="javascript" src="/js/dict.js"></script> 
<script language="javascript">
   var codeid='<%=(request.getParameter("code"))%>'
  // this.status ="招聘管理 / 审核查询";
   //查询
   function query()
   {
   		var hashvo=new ParameterSet();
		hashvo.setValue("opt","query");
		var In_paramters="tableName=Z03"; 	
		var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:ReturnQuery,functionId:'3000000104'},hashvo);			
   }
   
   function ReturnQuery(outparamters)
   {
   		
   		var fields_temp=outparamters.getValue("fields");		
   		var fields=new Array();
   		for(var i=0;i<fields_temp.length;i++)
   		{
   			////数组[ 0:列名 1:列描述  2:列的类型 3:如果为代码型,则为代码值,否则为空 ]
   			var a_field=fields_temp[i].split("<@>");
   			fields[i]=a_field
   		}
    	 var extendSql=generalQuery("Z03",fields);
    	 if(extendSql)
    	 {
	    	 document.positionDemandForm.extendSql.value=extendSql;
    	 	 positionDemandForm.action='/hire/demandPlan/positionDemand/positionDemandTree.do?b_query3='+"${positionDemandForm.linkDesc}"+'&model=3&codeset=<%=(request.getParameter("codeset"))%>&code=<%=(request.getParameter("code"))%>';
    	 	 positionDemandForm.submit();
    	 }
  
   }
   
   
   //排序
   function taxis()
   {
   		var hashvo=new ParameterSet();
		hashvo.setValue("opt","taxis");
   		var In_paramters="tableName=Z03"; 	
		var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:ReturnTaxis,functionId:'3000000104'},hashvo);			
   }
   
   
   function ReturnTaxis(outparamters)
   {
   		var fields_temp=outparamters.getValue("fields");		
   		var fields=new Array();
   		for(var i=0;i<fields_temp.length;i++)
   		{
   			////数组[ 0:列名 1:列描述  2:列的类型 3:如果为代码型,则为代码值,否则为空 ]
   			var a_field=fields_temp[i].split("<@>");
   			fields[i]=a_field
   		}
    	 var orderSql=taxisDialog("Z03",fields);
    	 if(orderSql)
    	 {
	    	 document.positionDemandForm.orderSql.value=orderSql;
    	 	 positionDemandForm.action='/hire/demandPlan/positionDemand/positionDemandTree.do?b_query3='+"${positionDemandForm.linkDesc}"+'&model=3&codeset=<%=(request.getParameter("codeset"))%>&code=<%=(request.getParameter("code"))%>';
    	 	 positionDemandForm.submit();
    	 }
    	
   }
   
   //空缺职位查询
   function SparePositionQuery()
   {
   		 positionDemandForm.action='/hire/demandPlan/positionDemand/positionDemandTree.do?b_sparePosition=link&codeid='+codeid;
    	 positionDemandForm.submit();
   }
   
   
   
   
   //招聘计划
   function hirePlan()
   {
    	 positionDemandForm.action='/hire/demandPlan/engagePlan.do?b_init=link&codeset=<%=(request.getParameter("codeset"))%>&code=<%=(request.getParameter("code"))%>&model=2&origin=a';
    	 positionDemandForm.submit();
   }
   
   
   
   
   //输出 EXCEL OR PDF
    function showfile(outparamters)
	{
		var outName=outparamters.getValue("outName");
		var flag=outparamters.getValue("flag");
		outName = decode(outName);
		var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName);
	}

	/*
	*flag 1:pdf  2:excel
	*/
	function executeOutFile(flag)
	{
	
		
		var tablename,table,size,fieldWidths,whl_sql;       
        tablename="table${positionDemandForm.tablename}";
        table=$(tablename);
        size=${positionDemandForm.fieldSize};
        //这里定义了参数没有传递,所以删除掉了whl_sql
   		for(var i=3;i<size+3;i++)
        {
        	fieldWidths+="/"+table.getColWidth(table,i);
        }
          
		var hashvo=new ParameterSet();
		hashvo.setValue("fieldWidths",fieldWidths.substring(10));
		hashvo.setValue("tablename","${positionDemandForm.tablename}");
		//前台不允许使用sql相关参数,这里去掉了 whl_sql
	    var In_paramters="flag="+flag;
	   	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showfile,functionId:'0521010011'},hashvo);
		
	}
   
   
   function setPageFormat()
   {
   		
   		var param_vo=oageoptions_selete("3","${positionDemandForm.username}");
   
   }
   
   
   function goback(filk)
   {
   		var hashvo=new ParameterSet();
   		hashvo.setValue("flag",filk);
   		var In_paramters="flag="+filk; 	
		var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:goback2,functionId:'3000000148'});			

   }
   function goback2(outparamters)
   {	
   		this.status ="";
   		window.open("/templates/menu/hire_m_menu2.do?b_query=link&module=7","i_body")
   }
   function tableZ03_z0301a_onRefresh(cell,value,record)
{
   if(record!=null)
   {
      cell.innerHTML="<a href=\"javascript:checkPosition('"+record.getString("z0301a")+"','"+record.getString("z0319a")+"','"+record.getString("z0311a")+"')\"><img src=\"/images/view.gif\" border=\"0\"/></a>";
   }
}
function openPosition(z0301,posState)
{
	 var src="/hire/demandPlan/positionDemand/positionDemandTree.do?b_browse=edit`entertype=3`codeset=<%=request.getParameter("codeset")%>`code=<%=(request.getParameter("code"))%>`operate=browse`from=employPosition`posState="+posState+"`z0301="+z0301+"`isClose=0";
   var iframe_url="/general/query/common/iframe_query.jsp?src="+src;
   var values= window.showModalDialog(iframe_url,null, 
		        "dialogWidth:"+(window.screen.width)+"px; dialogHeight:"+(window.screen.height-200)+"px;resizable:yes;center:yes;scroll:yes;status:no;minimize:yes;maximize:yes;");			
	//window.open("/hire/demandPlan/positionDemand/positionDemandTree.do?b_browse=edit&entertype=1&codeset=<%=request.getParameter("codeset")%>&code=<%=(request.getParameter("code"))%>&operate=browse&from=employPosition&posState="+posState+"&z0301="+z0301,"_blank","width="+(window.screen.width-40)+",left=15,height="+(window.screen.height-180)+",scrollbars=yes, resizable=yes");
   if(values)
   {
      var obj= new Object();
      obj.refresh=values.refresh;
      if(obj.refresh=='1')
      {
          var codeset=positionDemandForm.codeSetId.value;
          var code=positionDemandForm.codeItemId.value;
          positionDemandForm.action='/hire/demandPlan/positionDemand/positionDemandTree.do?b_query3='+"${positionDemandForm.linkDesc}"+'&model=3&codeset='+codeset+'&code='+code;
          positionDemandForm.submit();
      }
   }
} 
function checkPosition(z0301,posState,z0311)
{
   var hashVo=new ParameterSet();
   hashVo.setValue("z0301",z0301);
   hashVo.setValue("z0311",z0311);
   hashVo.setValue("type","6");
   hashVo.setValue("posState",posState);
   var In_parameters="opt=1";
   var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:check_ok,functionId:'1010021116'},hashVo);			
		
}
function check_ok(outparameters)
{
   var msg=outparameters.getValue("msg");
   if(msg=='0')
   {
       alert("该岗位在组织机构中已被删除！");
       return; 
   }
   else
   {
     var z0301=outparameters.getValue("z0301");
     var posState=outparameters.getValue("posState");
     openPosition(z0301,posState);
   }
}  
 function returnFlowPhoto()
   {
   positionDemandForm.action="/general/tipwizard/tipwizard.do?br_retain=link";
   positionDemandForm.target="il_body";
   positionDemandForm.submit();
  }
   
</script>
<%
if(bosflag!=null&&bosflag.equalsIgnoreCase("hcm")){
%>
    <link href="/hire/css/layout.css" rel="stylesheet" type="text/css">
<%
}
%>
<html:form action="/hire/demandPlan/positionDemand/positionDemandTree.do?b_query3=3"  >
<table class="setmp"><tr><td>

<table class="left1table"><tr><td>
<hrms:menubar menu="menu1" id="menubar1">
	<hrms:menuitem name="file" label="menu.file" >						
						<hrms:menuitem name="mitem6" label="button.createpdf" icon="/images/print.gif" url="executeOutFile(1)"  />
						<hrms:menuitem name="mitem6" label="button.createescel" icon="/images/print.gif" url="executeOutFile(2)"  />
							<hrms:menuitem name="mitem6" label="kq.report.pagesetup" icon="/images/prop_ps.gif" url="setPageFormat()"  />
	</hrms:menuitem>
	<hrms:menuitem name="file" label="edit_report.status.bj" >						
						<hrms:menuitem name="mitem6" label="infor.menu.query" icon="/images/quick_query.gif" url="query()"  />
						<hrms:menuitem name="mitem6" label="label.zp_exam.sort" icon="/images/sort.gif" url="taxis()"  />				
	</hrms:menuitem>
	

</hrms:menubar>
   </td></tr></table>
</td>
<td>
&nbsp;&nbsp;
	     </td>
</tr>
</table>
	<hrms:dataset name="positionDemandForm" property="fieldlist" scope="session" setname="${positionDemandForm.tablename}"  setalias="position_set" readonly="false" editable="false" select="true" sql="${positionDemandForm.sql}"  pagerows="${positionDemandForm.pagerows}" buttons="bottom">

	<%if(userView.getBosflag()!=null&&(userView.getBosflag().equalsIgnoreCase("hl")||userView.getBosflag().equalsIgnoreCase("hcm"))&&returnflag.equalsIgnoreCase("dxt")){ %>	 	
		 <hrms:commandbutton name="intoback" hint="" functionId="" refresh="true" type="selected" setname="${musterForm.mustername}" onclick="returnFlowPhoto();">
    <bean:message key="button.return"/>
   </hrms:commandbutton>
 <%} %> 
	</hrms:dataset>
	
<input type='hidden' name='extendSql' value=" " />
<input type='hidden' name='orderSql'  value=" " />
<html:hidden name="positionDemandForm" property="codeSetId"/>
<html:hidden name="positionDemandForm" property="codeItemId"/>
</html:form>
