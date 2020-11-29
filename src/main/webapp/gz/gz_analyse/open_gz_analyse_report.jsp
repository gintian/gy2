<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="javaScript" type="text/javascript">
<!--
 function dataExport(filename)
  {
	var fieldName = getDecodeStr(filename);
  	var dd=open("/servlet/vfsservlet?fileid="+fieldName+"&fromjavafolder=true","excel");
  }
function closeWindow()
{
 window.close();
}
 function pageOptions(rsid,rsdtlid)
  {
      var thecodeurl="/general/print/page_options.do?b_edit=link`state=4`id=-1`rsid="+rsid+"`rsdtlid="+rsdtlid; 
	  var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
	  
	  var xmltype= window.showModalDialog(iframe_url, arguments, 
        "dialogWidth:800px; dialogHeight:450px;resizable:no;center:yes;scroll:yes;status:no");        
      
  }
  function getHighgrade(id){
  		var thecodeurl="/gz/gz_analyse/getHighgrade.do?b_query=link`salaryid="+id; 
	  	var iframe_url="/gz/gz_analyse/iframeHighgrade.jsp?src="+thecodeurl;
	  
	  	var xmltype= window.showModalDialog(iframe_url, arguments, 
        "dialogWidth:700px; dialogHeight:500px;resizable:no;center:yes;scroll:yes;status:no"); 

        if(typeof(xmltype)=="undefined"){
        	xmltype="zxgflag";
        }
  		window.document.earchive.setText(xmltype);
  }


//-->
</script>
<body bgColor='#F7F7F7' scroll='no' topMargin='0' leftMargin='0' >
<html:form action="/gz/gz_anaylse/open_gz_analyse_report">
   <jsp:plugin  type="applet" name="earchive" archive="hj_client.jar,struts_extends.jar,hessian-3.1.3.jar,command.jar,rowset.jar,jai_codec.jar,jai_core.jar,commons-beanutils.jar,commons-logging-api.jar,jsuite_swing_all.jar,plugin.jar" code="com.hjsj.hrms.client.gz.analyse.GzAnalyseApplet.class"  width="${gzAnalyseForm.width}"  height="${gzAnalyseForm.height}" 
   codebase="/client" >  
       <jsp:params>  
           <jsp:param  name="salaryid"  value="${gzAnalyseForm.salaryid}"/>  
           <jsp:param  name="pre"  value="${gzAnalyseForm.pre}"/>    
           <jsp:param  name="reportTabId"  value="${gzAnalyseForm.reportTabId}"/>    
           <jsp:param  name="rsdtlid"  value="${gzAnalyseForm.rsdtlid}"/>  
           <jsp:param name="username" value="${gzAnalyseForm.username}"/> 
           <jsp:param name="bgroup" value="${gzAnalyseForm.bgroup}"/>
           <jsp:param name="privDb" value="${gzAnalyseForm.privDb}"/>
           <jsp:param name="role" value="${gzAnalyseForm.role}"/>
           <jsp:param name="privCode" value="${gzAnalyseForm.privCode}"/>
            <jsp:param name="privCodeValue" value="${gzAnalyseForm.privCodeValue}"/>
            <jsp:param name="address" value="${gzAnalyseForm.address}"/>
             <jsp:param name="salaryArchive" value="${gzAnalyseForm.archive}"/>
           <jsp:param name="MAYSCRIPT" value="true"/>
      </jsp:params> 
           <jsp:fallback>
                 <p>Unable to start plugin.</p>
           </jsp:fallback>	           
   </jsp:plugin>
</html:form>
</body>
