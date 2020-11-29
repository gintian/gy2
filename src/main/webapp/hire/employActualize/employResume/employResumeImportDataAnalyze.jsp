<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.hire.employActualize.EmployResumeForm"%>
<%@ page import="java.util.*"%>
<%
    EmployResumeForm employResumeForm=(EmployResumeForm)session.getAttribute("employResumeForm");
    ArrayList inforNotExistList=employResumeForm.getInforNotExistList();//指标不存在信息 
    ArrayList inforNotFormatList=employResumeForm.getInforNotFormatList();//指标类型不一致信息 
    ArrayList setNotExistList=employResumeForm.getSetNotExistList();//指标集不存在信息 
    String importZipData=employResumeForm.getImportZipData();
    int count=0;
%>
<html>
<head></head>
<hrms:themes></hrms:themes>
<body>
<div id="ly" style="position:absolute;top:0px;FILTER:alpha(opacity=0);background-color:#FFF;z-index:2;left:0px;display:none;"></div> 
<div id='wait1' style='position:absolute;top:285;left:80;display:none;'>
        <table border="1" width="300" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
            <tr>
                <td class="td_style" height=24>
                                            正在导入数据，请稍候.........
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
    <html:form action="/hire/employActualize/employResume">
    <logic:equal name="employResumeForm" property="importZipData" value="1">
        <table width="700px" align="center" cellspaceing="0" cellpadding="0" class="ListTable">
            <tr>
             <td class="TableRow">
                <bean:message key="sys.import.alertMessage"/>
             </td>
            </tr>
            <tr>
              <td class="RecordRow">
                <bean:message key="sys.import.dataCount"/><bean:write  name="employResumeForm" property="zipRecordcout"/><bean:message key="sys.import.otherMessage"/><bean:write  name="employResumeForm" property="importcount"/><bean:message key="sys.import.men"/>
              </td>
            </tr>
        </table>
        <table width="700px" align="center">
            <tr>
                <td align="center">
                    <input type="button" class="mybutton" value='<bean:message key="button.ok"/>' onclick="goback()" />
                </td>
            </tr>
       </table>
    </logic:equal>
    <logic:notEqual name="employResumeForm" property="importZipData" value="1">
        <table width="700px" align="center" cellspaceing="0" cellpadding="0" class="ListTable">
            <tr>
                <td class="TableRow">
                   <bean:message key="sys.import.alertMessage"/>
                </td>
            </tr>
            <!-- 提示指标集不存在信息 begin -->
             <%
               for(int i=0;i<setNotExistList.size();i++){
                 count++;
                 String infor=(String)setNotExistList.get(i);
             %>
            <tr>
                <td class="RecordRow">
                    <%=count+"、"+infor%>
                </td>
            </tr>
            <%
              }
            %>
             <!-- 提示指标集不存在信息end-->
             
             <!-- 提示指标不存在信息 begin -->
              <%
               for(int i=0;i<inforNotExistList.size();i++){
                  count++;
                 String infor=(String)inforNotExistList.get(i);
             %>
            <tr>
                <td class="RecordRow">
                    <%=count+"、"+infor%>
                </td>
            </tr>
            <%
              }
            %>
            <!-- 提示指标不存在信息end-->
            
            <!-- 提示指标类型不一致信息 begin -->
            <%
               for(int i=0;i<inforNotFormatList.size();i++){
                 count++;
                 String infor=(String)inforNotFormatList.get(i);
             %>
            <tr>
                <td class="RecordRow">
                     <%=count+"、"+infor%>
                </td>
            </tr>
            <%
              }
            %>
             <!-- 提示指标类型不一致信息end-->
             
            <tr>
                <td class="RecordRow">
                     <bean:message key="sys.import.queryMessage"/>                           
                </td>
            </tr>
        </table>
        <table width="700px" align="center">
            <tr>
                <td align="center">
                    <input type="button" class="mybutton" value='<bean:message key="sys.export.derivedIn"/>' onclick="nowImport()" />
                    <input type="button" class="mybutton" value='<bean:message key="button.cancel"/>' onclick="goback()">
                </td>
            </tr>
        </table>
      </logic:notEqual>
    </html:form>
  </body>
  <script type="text/javascript">
    function goback(){
        document.employResumeForm.action="/hire/employActualize/employResume.do?b_query=link";
        document.employResumeForm.submit();
    }
  </script>
  <logic:notEqual name="employResumeForm" property="importZipData" value="1">
  <script type="text/javascript">
    function nowImport(){
         jindu1();
        document.employResumeForm.action="/hire/employActualize/employResume.do?b_sureImport=import&sure=sure";
        document.employResumeForm.submit();
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
	    waitInfo.style.display="";
    }
  </script>
  </logic:notEqual>
</html>
