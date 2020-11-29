<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.hire.employActualize.EmployResumeForm"%>
<%@ page import="java.io.File"%>
<html>
  <head>
    <script language="JavaScript" src="/js/validate.js"></script>
    <script language="JavaScript" src="/js/function.js"></script>
  </head>
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
    <html:form action="/hire/employActualize/employResume"  enctype="multipart/form-data" >
        <div style="margin-top:50px;">
            <table width="700px;" align="center">
                <td><font size=2  ><bean:message key="sys.import.explain"/></font></td>
            </table>
            <div style="margin-top:10px;">
            <fieldset align="center" style="width:700px;">
                <legend>
                  <bean:message key="sys.import.selectFile"/>
                </legend>
                <table border="0" cellspacing="0" align="center" cellpadding="0">
                    <tr>
                        <td width="400">
                            <Br>
                            <bean:message key="sys.import.File"/>&nbsp;&nbsp;
                            <input type="file" name="file" size="40" class="complex_border_color">
                            <br>
                            <br>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            &nbsp;
                        </td>
                    </tr>
                </table>
            </fieldset>
            <table width='700px' align="center">
                <tr height="35px">
                    <td align="center">
                    <input type="button" class="mybutton" id='b_update' value='<bean:message key="sys.import.beginImport"/>' onclick="update()"/>
                    <input type="button" class="mybutton" value='<bean:message key="sys.import.back"/>' onclick='goback()'/>
                    </td>
                </tr>
            </table>
            </div>
        </div>
    </html:form>
  </body>
  <script type="text/javascript">
    function update(){
        
        var filepath=document.employResumeForm.file.value;
        if(filepath.length==0)
        {
            alert("请选择附件!");
            return;
        }else{
            if(filepath.substring(filepath.lastIndexOf(".")+1,filepath.length)!="zip"){
                alert("请用zip压缩包来导入数据！");
                return;
            }
        }
       var isRightPath = validateUploadFilePath(filepath);
       if(!isRightPath) 
            return;
        jindu1();
        document.getElementById("b_update").disabled="true";
        employResumeForm.action="/hire/employActualize/employResume.do?b_update=link&isok=ok";
        ///document.positionDemandForm.target="mil_body"
        employResumeForm.submit();
    }
    function goback(){
        document.employResumeForm.action="/hire/employActualize/employResume.do?b_query=link";
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
</html>
