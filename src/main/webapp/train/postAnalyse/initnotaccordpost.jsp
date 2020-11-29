<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script> 
<%
String query = (String)request.getParameter("query");
String info="";
if(query!=null&&query.equals("1"))
{
   out.println("var url=\"/train/postAnalyse/notaccordpost.do?b_init=link&query=1\";");   
   info="正在分析不符合本岗位培训要求的人员数据";
}else if(query!=null&&query.equals("2"))
{
   out.println("var url=\"/train/postAnalyse/accordpost.do?b_init=link&query=2\";"); 
   info="正在分析按本岗位培训要求匹配的人员数据";
}else if(query!=null&&query.equals("3"))
{
   String code = (String)request.getParameter("code");
   out.println("var url=\"/train/postAnalyse/accordpost.do?b_alayse=link&flag=2&code="+code+"&query=3\";"); 
   info="正在分析按非本岗位培训要求匹配的人员数据";
}else if(query!=null&&query.equals("4"))
{
   String code = (String)request.getParameter("code");
   out.println("var url=\"/train/postAnalyse/accordpost.do?b_alayse=link&flag=1&code="+code+"&query=4\";"); 
   info="正在分析按本岗位培训要求匹配的人员数据";
}
out.println("function window.onload()");
out.println("{");
out.println("location=url;");
out.println("}");
%>

</script>
<html:form action="/train/postAnalyse/notaccordpost">
<div id='wait' style='position:absolute;top:200;left:250;'>
<table border="1" width="430" cellspacing="0" cellpadding="4" class="table_style" height="150" align="center">
           <tr>

             <td class="td_style" height="40"><%=info%>...</td>

           </tr>
           <tr>
             <td style="font-size:12px;line-height:200%" align=center>
               <marquee class="marquee_style" direction="right" width="430" scrollamount="7" scrolldelay="10">
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
</html:form>