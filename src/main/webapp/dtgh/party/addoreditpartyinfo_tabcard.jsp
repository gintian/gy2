<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.dtgh.party.*,java.util.*,com.hrms.hjsj.sys.FieldSet" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    boolean version = false;
    String css_url="/css/css1.css";
    String bosflag="";	
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  bosflag=userView.getBosflag(); 
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  
       if(userView.getVersion()>=50){//版本号大于等于50才显示这些功能
		version = true;
	  }	 	 
	}
%>
<%
       PartyBusinessForm partyBusinessForm=(PartyBusinessForm)session.getAttribute("partyBusinessForm");
       ArrayList fieldsetList = (ArrayList)partyBusinessForm.getFieldsetlist();
       String classpre = partyBusinessForm.getParam();
	   classpre = classpre!=null&&classpre.length()>0?classpre:"Y";
	   String codeitemid = partyBusinessForm.getCodeitemid();
	   String type = partyBusinessForm.getType();
	   String codesetid = partyBusinessForm.getCodesetid();
	   String return_code = partyBusinessForm.getReturn_code();
 %>
 
<script language="javascript">
function exeReturn(returnStr,target)
{
  target_url=returnStr;
  window.open(target_url,target); 
}
</script>
<div style="margin-left:-1px">
<% if(fieldsetList!=null&&fieldsetList.size()>0)
   {%>
<hrms:tabset name="pageset" width="100%" height="100%" type="true" align="center" > 
<%
      for(int i=0;i<fieldsetList.size();i++)
      {
          FieldSet set = (FieldSet)fieldsetList.get(i);
          String setid=set.getFieldsetid();
          String setfesc=set.getCustomdesc();
          if(!"H".equals(classpre) & setid.equals(classpre+"00") && type.equals("add"))
        	  continue;
          String url="";
          if(set.getFieldsetid().equalsIgnoreCase(classpre+"01"))
          {
             url="/dtgh/party/searchpartybusinesslist.do?b_query_info=link&sign=&fieldsetid="+setid;
          }else if(set.getFieldsetid().equalsIgnoreCase(classpre+"00")){
        	  url="/general/inform/emp/view/multimedia_tree.do?b_query=link&isvisible=1&kind=9&a0100="+codeitemid+"&multimediaflag=&isself=0"
        			  +"&editType="+type;
        	  if(classpre.equals("H")){
        		  session.setAttribute("code","HH"+return_code);
        	  }else if(classpre.equals("Y")){
        	  		session.setAttribute("code","YY"+return_code);
        	  }else if(classpre.equals("V")){
        	  		session.setAttribute("code","VV"+return_code);
        	  }else if(classpre.equals("W")){
        	  	session.setAttribute("code","WW"+return_code);
        	  }
        	  
          }else
           {
               url="/dtgh/party/searchpartybusinesslist.do?b_query_sub=link&sign=&fieldsetid="+setid;
           }
   //         if(set.getFieldsetid().equalsIgnoreCase(classpre+"01"))
   //       {
  %>
	  
      <%//}else{ %>
	  <hrms:tab name='<%="tab"+i%>' label="<%=setfesc%>"  visible="true" url="<%=url%>">
      </hrms:tab>
      
      <%
      //      }
      }
    
       %>	
</hrms:tabset>

<%} %>  
</div>
