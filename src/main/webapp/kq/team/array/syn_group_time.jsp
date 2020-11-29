<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="com.hrms.struts.constant.WebConstant,java.text.SimpleDateFormat,java.util.Date"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null)
	{
	  userName = userView.getUserFullName();
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
	}
	//String date = DateStyle.getSystemDate().getDateString();
	int flag=1;
	String webserver=SystemConfig.getPropertyValue("webserver");
	if(webserver.equalsIgnoreCase("websphere"))
		flag=2;
		

	
%>
<%
SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
String date = sdf.format(new Date());

 %>
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/control.js"></script>
<script language="javascript" src="/ajax/dataset.js"></script>
<script language="javascript" src="/ajax/editor.js"></script>
<script language="javascript" src="/ajax/dropdown.js"></script>
<script language="javascript" src="/ajax/table.js"></script>
<script language="javascript" src="/ajax/menu.js"></script>
<script language="javascript" src="/ajax/tree.js"></script>
<script language="javascript" src="/ajax/pagepilot.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script type="text/javascript" src="/js/validateDate.js"></script>
<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script type="text/javascript" src="/js/popcalendar.js"></script>
<script language="javascript">
    var _checkBrowser=true;
	var _disableSystemContextMenu=false;
	var _processEnterAsTab=true;
	var _showDialogOnLoadingData=true;
	var _enableClientDebug=true;
	var _theme_root="/ajax/images";
	var _application_root="";
	var __viewInstanceId="968";
	var ViewProperties=new ParameterSet();
	var webserver=<%=flag%>;
	
</script>
<script language="javascript">

function countdata()
{
	var msg = "";
	<logic:equal name="kqClassArrayForm" property="syn_uro" value="1">
		msg = "<bean:message key='kq.class.syntime1' />"
	</logic:equal>
	<logic:equal name="kqClassArrayForm" property="syn_uro" value="2">
		msg = "<bean:message key='kq.class.syntime2' />"
	</logic:equal>
	if(!confirm(msg))
    	return false;
	var syc_type;
	for(var i=0;i<document.kqClassArrayForm.syc_type.length;i++)
	{
		if(document.kqClassArrayForm.syc_type[i].checked)
		{
			syc_type=document.kqClassArrayForm.syc_type[i].value;
			break;
		}
	}
    if(syc_type==2)
	{
	      var count_start=document.kqClassArrayForm.start_date.value;
	      var count_end=document.kqClassArrayForm.end_date.value;
	      if(count_start=="")
	      {
	         alert("请选择计算开始时间！");
                 return;
	      }else if(count_end=="")
	      {
	          alert("请选择计算结束时间！");
                  return;
	      }else
	      {
	         var thevo=new Object();
	         thevo.syc_type=syc_type;
		     thevo.start=count_start;
             thevo.end=count_end;            
		     window.returnValue=thevo;
		     window.close();  
	      }		
    }else if(syc_type==1)
	{
	       var thevo=new Object();
	       thevo.syc_type=syc_type;	
	       thevo.start="";
           thevo.end="";              
	       window.returnValue=thevo;
	       window.close();
	} 
}
</script>
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<html:form action="/kq/team/array/syn_group_data">
<div  class="fixedDiv2" style="height: 100%;border: none">
<table align="center" width="100%" height="200" border="0" >
  <tr>
     <td valign='top'>
      <table border="0" width="95%" cellspacing="0" cellpadding="4" class="RecordRow" style="border-style: solid;border-width:1px;"  height="87" align="center">
        <tr>
           <td class='TableRow' height=24>同步时间范围
            </td>             
        </tr>
        <tr>
         <td>
           <table>
            <tr>
             <td>
             <table>
	        <tr>
		  <td>
                  &nbsp;<html:radio name="kqClassArrayForm" property="syc_type" value="1"/> 
                  </td>
                   <td>
                     &nbsp;<bean:message key="kq.register.kqduration"/>&nbsp;&nbsp;&nbsp;
                     <font face='宋体' style='color: #0000FF;' > 
                     <bean:write name="kqClassArrayForm" property="session_data" />
                    </font>
                   </td>
		 <tr>
	        </table>   
             </td>             
           </tr>        
           <tr>
	     <td width="100%" height="50" >
		<table>
		   <tr>
		      <td nowrap="nowrap">
		        &nbsp;<html:radio name="kqClassArrayForm" property="syc_type" value="2"/>
		         &nbsp;<bean:message key="kq.datewidth"/>&nbsp;
		      </td>
		      <td nowrap="nowrap">
		         <bean:message key="label.query.from"/>
		         &nbsp;<input type="text" name="start_date" value='<bean:write name="kqClassArrayForm" property="start_date"/>' extra="editor" class="textColorWrite" style="width:100px;font-size:10pt;text-align:left;" id="editor1"  dropDown="dropDownDate" onchange="if(!validate(this,'开始时间','yyyy.mm.dd')) {this.focus(); this.value='<%=date %>'; }"> 
		         &nbsp;<bean:message key="label.query.to"/>&nbsp;
		         <input type="text" name="end_date" value='<bean:write name="kqClassArrayForm" property="end_date"/>' extra="editor" class="textColorWrite" style="width:100px;font-size:10pt;text-align:left;" id="editor1"  dropDown="dropDownDate" onchange="if(!validate(this,'结束时间','yyyy.mm.dd')) {this.focus(); this.value='<%=date %>'; }">                             
		      </td>
		      <tr>
		      <tr style="padding-top: 5px;">
		      	<td colspan="4" align="left">
		      		<font size="2">
		      			<logic:equal name="kqClassArrayForm" property="syn_uro" value="1">
		      				<!--  说明：同步个人排班操作会将选中人员的所在班组排班信息同步复制给该人员，请谨慎操作！！-->
		      				<bean:message key="kq.class.syntime1" /> 
		      			</logic:equal>
		      			<logic:equal name="kqClassArrayForm" property="syn_uro" value="2">
		      				<!-- 说明：同步班组排班操作会将选中班组排班信息同步复制给该班组权限范围内人员，请谨慎操作！！  -->
		      				<bean:message key="kq.class.syntime2" /> 
		      			</logic:equal>
		      		</font>
		      	</td>
		      </tr>
		  </table>  
		</td>
	     </tr>
            </table>
          </td>  
        </tr>        
	    </table>   
          </td>
        </tr>
        <tr>
          <td align="center">
             <input type="button" name="btnreturn" value='<bean:message key="kq.register.kqduration.ok"/>' onclick="countdata();" class="mybutton">
	     &nbsp;&nbsp;<input type="button" name="btnreturn" value='<bean:message key="kq.register.kqduration.cancel"/>' onclick="window.close();" class="mybutton"> 
          </td>
        </tr>	
      </table>
 </div>
</html:form>


<script language="javascript">
   var dropDownDate=createDropDown("dropDownDate");
   var __t=dropDownDate;
   __t.type="date";
   __t.tag="";
   _array_dropdown[_array_dropdown.length]=__t;
   initDropDown(__t);
   
   var dropDownList=createDropDown("dropDownList");
   var __t=dropDownList;
   __t.type="list";
   __t.tag="";
   _array_dropdown[_array_dropdown.length]=__t;
   initDropDown(__t);   
</script>
<script language="javascript">
    var dropdownCode=createDropDown("dropdownCode");
    var __t=dropdownCode;
    __t.type="custom";
    __t.path="/general/muster/select_code_tree.do";
	//__t.path="/system/gcodeselect.jsp";    
    __t.readFields="codeitemid";
    //__t.writeFields="xxxx";
    __t.cachable=true;__t.tag="";
    _array_dropdown[_array_dropdown.length]=__t;
    initDropDown(__t);
</script>
<script language="javascript">
  initDocument();
</script>