<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
<script LANGUAGE=javascript src="/js/xtree.js"></script> 
  <%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
	  int i=0;
	    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
	}
    
%>
<STYLE type=text/css>
.div2
{
 overflow:auto; 
 width: 100%;height: 300px;
 line-height:15px; 
 border-width:1px; 
 border-style: groove;
 border-width :thin ;
}
</STYLE>
<script language="javascript">
xtreeItem.prototype.getSelectedNode=function()
{ 
  var currnode,values;
  values=new Object();
  values.title='根节点';
  values.value='root';
  if(Global.defaultInput==2)
  {
        var radioitems=document.getElementsByName("treeItem-radio");
        for(var i=0;i<radioitems.length;i++)
        {
          currnode=radioitems[i];
          if(currnode.checked)
          {
            values=currnode;
            break;
          }
        }   
  }
  return values;
};
 function input()
{
   var orgNode=root.getSelectedNode();
   var orgID=orgNode.value;  
   var obj=document.getElementById("input_orgid");
   obj.value=orgID;
   var fileObJA=document.getElementsByName("file");
   var obj=fileObJA[0];
   var filename = obj.value;
   var ext = filename.substr(filename.length-3,3).toLowerCase();
   if(ext=="")
   {
    alert(FILE_INPUT_NO_EMPTY+"!");
    obj.focus();    
    return false;
   }else if(ext== !"txt"){   
    alert(FILE_INPUT_MUST_TXT+"!");
    obj.focus();    
    return false;     
   }else{ 
   		var msg="您未选择组织机构，此操作将覆盖管理范围内组织机构！您确认继续吗?";
   		if(orgID!='root'){
   			msg="您确认要将组织机构导入"+orgNode.title+"下吗?";
   		}
	   if(confirm(msg)){   
		    var waitInfo=eval("wait");	   
		    waitInfo.style.display="block";
		    orgInformationForm.action="/org/orginfo/searchorglist.do?b_inputorgtree=link&rflag=1";    
		    orgInformationForm.target="_self";
		    orgInformationForm.submit();
	    }
   }
   
}
	<%String rflag = (String)request.getParameter("rflag");
		if(rflag!=null&&rflag.equals("1")){
	%>
		var ab = new Object();
	    //window.returnValue = ab;
	    //window.close();
	    winclose();
	<%}%>
function MusterInitData()
{
	   var waitInfo=eval("wait");	
	   waitInfo.style.display="none";
}
//关闭ext弹窗方法   wangb 20190306
function winclose(){
	var win = parent.Ext.getCmp('importorgtree');
   	if(win)
   		win.close();
}
</script>
<hrms:themes></hrms:themes>
<html:form action="/org/orginfo/searchorglist" enctype="multipart/form-data">     
<div class="fixedDiv3">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0"  class="framestyle0">   
<!-- 	 <tr align="left">
		<td valign="middle" class="TableRow" style="border-bottom:1px solid;border-top:0px;border-left:0px;border-right:0px;"  nowrap >
		 &nbsp;<bean:message key="button.input.data"/>
		</td>
	 </tr>        -->
		  <html:hidden name="orgInformationForm" property="input_orgid" styleId="input_orgid" styleClass="text"/>		  
         <tr height="25">        
           <td style="padding:5px;">
           		<bean:message key="org.orginfo.info02"/>
           </td>
         </tr> 	    
         <tr>        
           <td align="left" class="RecordRow" style="border-left:0px;border-right:0px;"> 
           <div id="tbl_container"  class="div2" style="border:0px;">
                 <hrms:orgtree flag="0" loadtype="0" nmodule="4" showroot="false" selecttype="2" dbtype="0" priv="1" isfilter="0"/>
           </div>
           </td>
         </tr> 
          <tr>
            <td align="center" colspan="2" height="40">
         	   <bean:message key="org.orginfo.inputfile"/>&nbsp;<html:file name="orgInformationForm" styleClass="textColorWrite" property="file" accept="txt" onkeydown="javascript:return   false;" /> 
            </td>         
         </tr>
   </table>
   <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
   		<tr style="height: 35">
            <td align="center" >
         	 <html:button styleClass="mybutton" property="b_save" onclick="input();">
            		<bean:message key="button.ok"/>
	 	    </html:button>
         	<html:button styleClass="mybutton" property="br_return" onclick="winclose();">
            		<bean:message key="button.close"/>
	 	    </html:button>  
            </td>         
         </tr>
   </table> 
</div>
</html:form>
<div id='wait' style='position:absolute;top:200;left:30;display:none;'>
  <table border="1" width="250" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
           <tr>
             <td class="td_style common_background_color" height=24>正在处理数据，请稍候....</td>
           </tr>
           <tr>
             <td style="font-size:12px;line-height:200%" align=center>
               <marquee class="marquee_style" direction="right" width="250" scrollamount="5" scrolldelay="10">
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
<script language="javascript">
 MusterInitData();
 if(!getBrowseVersion() || getBrowseVersion() == 10){
 	var file = document.getElementsByName('file')[0];
 	file.style.height='26px';
 	file.style.lineHeight='14px';
 }
</script>
