<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.ykcard.CardTagParamForm" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="com.hrms.frame.utility.AdminDb" %>
<%@ page import="com.hjsj.hrms.utils.PubFunc" %>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    //System.out.println(PubFunc.convertTo64Base("su"));
    //System.out.println(PubFunc.convert64BaseToString("aHVhbmdxdW4="));
    String css_url="/css/css1.css";
    Connection con=null;
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    boolean isCorrect=true;
	if(userView != null)
	{
	   CardTagParamForm cardTagParamForm=(CardTagParamForm)session.getAttribute("cardTagParamForm");   
       String username=cardTagParamForm.getUsername(); 
       try
       {
          con=AdminDb.getConnection();    
          userView=new UserView(username,con);      		    
	      String a0100="";
	      if(userView != null)
	      {
	         css_url=userView.getCssurl();
	         if(userView.canLogin())
	         {
	            if(css_url==null||css_url.equals(""))
	  	          css_url="/css/css1.css";
	            a0100=userView.getA0100();
	            String nbase=userView.getDbname();
	            cardTagParamForm.setUserbase(nbase);
	            session.setAttribute("cardTagParamForm",cardTagParamForm);
	            session.setAttribute("a0100",a0100);
	            session.setAttribute("nbase",nbase);
	         }else
	         {
	           out.println("<script LANGUAGE=javascript>");
               out.println("alert('得到用户HR信息时有误，请联系管理员！');");
               out.println("window.close();");
               out.println("</script>");
               isCorrect=false;
	         }
	         
	      }else
	      {
	          out.println("<script LANGUAGE=javascript>");
              out.println("alert('没有得到用户HR信息有误，请联系管理员！');");
              out.println("window.close();");
              out.println("</script>");
              isCorrect=false;
	      }
        }catch(Exception e)
        {
       
        }finally
        {
         try{ if(con!=null)
          con.close();
         }catch(Exception e){}
       }
	}else
	{
	          out.println("<script LANGUAGE=javascript>");
              out.println("alert('单点登录有误，请联系管理员！');");
              out.println("window.close();");
              out.println("</script>");
              isCorrect=false;
	}
   
    
    
%>
<%String aurl = (String)request.getServerName();
  String port=request.getServerPort()+"";  
  String url_p=SystemConfig.getServerURL(request);
  session.setAttribute("url_p",url_p);
  
  String browser = "MSIE";
	String agent = request.getHeader("user-agent").toLowerCase(); 
	if(agent.indexOf("firefox")!=-1)
		browser="Firefox";
	else if(agent.indexOf("chrome")!=-1)
		browser="Chrome";
	else if(agent.indexOf("safari")!=-1)
		browser="Safari";
%>

<script LANGUAGE=javascript src="/js/xtree.js"></script> 
<script language="javascript" src="/js/dict.js"></script>  
<hrms:themes></hrms:themes>
<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
<html:form action="/general/inform/synthesisbrowse/synthesiscard">
   <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" >   
	 <tr align="left">
		<td valign="top">
		</td>
	 </tr>          
         <tr>        
           <td align="left">
           </td>
         </tr>            
   </table> 
  <div id="card">
    <table>
       <tr>
         <td>   
         <%if(isCorrect){ %>      
            <hrms:ykcard name="cardTagParamForm" property="cardparam"  nid="${a0100}" tabid="${cardTagParamForm.tabid}" infokind="1" cardtype="no" disting_pt="javascript:screen.width" userpriv="${cardTagParamForm.userpriv}" base_url="${url_p}" fieldpurv="1" havepriv="1" queryflag="0" istype="3"  browser="<%=browser %>" />
         <%} %>
         </td>
       </tr>
    </table>
   </div> 

</html:form>
