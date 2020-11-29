<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@page import="com.hrms.struts.constant.WebConstant"%>
<script type="text/javascript" src="/fckeditor/fckeditor.js"></script>

<%@ page import="java.util.*"%>
<%
String viewunit="1";
UserView userView=(UserView)session.getAttribute(WebConstant.userView);
if(userView != null)
{
    if(userView.getStatus()==4||userView.isSuper_admin())
      viewunit="0";
    else{
    	String codeall = userView.getUnit_id();
		if(codeall==null||codeall.length()<2)
			viewunit="0";
    }
    userView.getHm().put("fckeditorAccessTime", new Date().getTime());
}
%>
<script language='javascript'>

	 function showView() {
       var oEditor = FCKeditorAPI.GetInstance('FCKeditor1');
       var oldInputs = document.getElementsByName('topicvo.string(description)');
       var tmpvalue=oEditor.GetXHTML(true);
       while(tmpvalue.indexOf("&nbsp;")!=-1)
       	tmpvalue=tmpvalue.replace("&nbsp;","?");
       oldInputs[0].value = tmpvalue;
    }
    
	    
	    function selectobject(obj){
		 var objecttype=obj.value;
		 if(objecttype==null||objecttype=="00")
		 	return ;
		 if(objecttype=="01"){
	     	var return_vo=select_org_emp_dialog6(1,1,1,1,1,1,"true",4);   //select_org_emp_dialog2  //select_org_emp_dialog22(1,1,1,1,1,1);
			if(return_vo){
			 	var a_temps=return_vo.title.split(",");
		 		var temps=return_vo.content.split(",");
		 		for(var i=0;i<temps.length;i++){
		 			if(temps[i].length>0&&temps[i].substr(0,2)!='UN'
		 			&&temps[i].substr(0,2)!='UM'
		 			&&temps[i].substr(0,2)!='@K'){
		 				document.getElementById("noticeperson").value+=a_temps[i]+",";
		 				document.getElementById("selectPerson").value+="4:"+temps[i]+",";
		 			}
		 		}
		 	}	
		 }else if(objecttype=="02"){
			var return_vo=select_role_dialog(1);
			if(return_vo&&return_vo.length>0){   
		   		for(var i=0;i<return_vo.length;i++){
	       			var rolevo=return_vo[i];
		   			document.getElementById("noticeperson").value+=rolevo.role_name+",";
		   			document.getElementById("selectPerson").value+="1:"+rolevo.role_id+","; 
		   		}        	   
		   }
		 }
    	}
 
		 function select_org_emp_dialog22(flag,selecttype,dbtype,priv,isfilter,loadtype)
		{
			 if(dbtype!=1)
			 	dbtype=0;
			 if(priv!=0)
			    priv=1;
			 <logic:equal name="topicForm" property="chflag" value="1">
		      	dbtype=-2;
		      </logic:equal>
		     var theurl="/system/logonuser/org_employ_tree.do?flag="+flag+"`showDb=1`selecttype="+selecttype+"`dbtype="+dbtype+"`viewunit=<%=viewunit %>"+
		                "`priv="+priv + "`isfilter=" + isfilter+"`loadtype="+loadtype;
		      <logic:equal name="topicForm" property="chflag" value="1">
		      	theurl+="`nmodule=6";
		      </logic:equal>
		      var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;  
		      
		     var return_vo= window.showModalDialog(iframe_url,1, 
		        "dialogWidth:300px; dialogHeight:450px;resizable:no;center:yes;scroll:yes;status:no");
			 return return_vo;
		}
 
	function clearObjs(){
		document.getElementById("noticeperson").value="";
		document.getElementById("selectPerson").value="";
	}
	function block(display){
		var tempTable=document.getElementsByTagName("table")[0]; //表示第几个表格
		var tempTd=tempTable.getElementsByTagName("tr")[8];    //表格中的第几行
		
		if(display == '0')
			tempTd.style.display = "";
		else
			tempTd.style.display = "none";
	}
	 function IsDigit() 
	  { 
	    return ((event.keyCode >= 46) && (event.keyCode <= 57)); 
	  }
</script>
<body>
<html:form action="/selfservice/infomanager/askinv/addtopic">
      <table width="720" border="0" style="margin-top:6px;" cellpadding="0" cellspacing="0" align="center" class="ftable">
          <tr height="20" >
       		<td align="left" colspan="2" class="TableRow"><bean:message key="lable.investigate.repair"/></td>
          </tr> 
                     <tr class="list3">
                       <td align="right" nowrap valign="center"><bean:message key="conlumn.investigate.content"/></td>
                       <td><html:text name="topicForm" property="topicvo.string(content)" size="25" maxlength="250" styleClass="text4" style="width:400px;"/></td> 
                     </tr>   

                     <tr class="list3">
                       <td align="right" nowrap valign="center">填表说明</td>
                       <td>
                            <html:textarea name="topicForm" property="topicvo.string(description)" cols="160" rows="20" style="display:none;" />
							<script type="text/javascript">
					              var oldInputs = document.getElementsByName('topicvo.string(description)');                             
					              var oFCKeditor = new FCKeditor( 'FCKeditor1' ) ;
					              oFCKeditor.BasePath	= '/fckeditor/';
					              oFCKeditor.Height	= 300 ;			
					              oFCKeditor.Width	= 600 ;			            
					              oFCKeditor.ToolbarSet='Simple';
					              oFCKeditor.Value	= oldInputs[0].value;
					              oFCKeditor.Create() ;
           
                            </script>
                       </td> 
                     </tr>   

                      <tr class="list3">
                	      <td align="right" nowrap valign="center"> <bean:message key="conlumn.investigate.releasedate"/></td><!-- 发布日期 -->
                	      <td align="left"  nowrap>
                	      	<html:text  property="first_date.year" size="4" maxlength="4"  styleClass="text" /><bean:message key="datestyle.year"/>&nbsp;
            			<html:text  property="first_date.month" size="2" maxlength="2" styleClass="text" /><bean:message key="datestyle.month"/>&nbsp;
            			<html:text  property="first_date.date" size="2" maxlength="2" styleClass="text" /><bean:message key="datestyle.day"/>
                          </td>
                      </tr> 
                     
					  <tr>
			          <td align="right" nowrap valign="center"><bean:message key="conlumn.investigate.days"/></td><!-- 调查天数 -->
                      <td><html:text name="topicForm" property="topicvo.string(days)" size="25" maxlength="9" onkeypress="event.returnValue=IsDigit();" styleClass="text4" style="width:400px;"/></td> 
                     </tr>
					 <tr>
                       <td align="right" nowrap valign="center"><bean:message key="conlumn.investigate.flag"/></td>
                       <td>
                        <html:radio name="topicForm" property="topicvo.string(flag)" value="1"/><bean:message key="datestyle.yes"/>
                        <html:radio name="topicForm" property="topicvo.string(flag)" value="0"/><bean:message key="datesytle.no"/>                   
                     	</td> 
                     </tr> 
                     <tr class="list3">
                     	<td align="right" nowrap valign="center"><bean:message key="conlumn.investigate.status"/></td>
                   	  	<td>
                     		<html:radio  name="topicForm" property="topicvo.string(status)" value="0" onclick="block('1');"/><bean:message key="lable.investigate.single"/>
                     		<html:radio  name="topicForm" property="topicvo.string(status)" value="1" onclick="block('0');"/><bean:message key="lable.investigate.multil"/>
                     	</td>
                     </tr>
                     <logic:equal value="0" name ="topicForm" property="topicvo.string(status)">
                     <tr class="list3"  style="display:none">
                     	<td align="right" nowrap value="center"><bean:message key="conlumn.investiagte.survey"/> </td>
							<td align="left">
								<hrms:optioncollection name="topicForm" property="spersonlist"
									collection="list" />
								<html:select name="topicForm" property="sperson"
									onchange="selectobject(this);" style="width:60">
									<html:options collection="list" property="dataValue"
										labelProperty="dataName" />
								</html:select>
								<html:text name="topicForm" property="noticeperson"
									styleClass="TEXT" size="48" maxlength="200" style="width:335px;"/>
								<img src='/images/del.gif' title='清空调查对象' onclick='clearObjs()' />
								<html:hidden name="topicForm" property="selectPerson" />
							</td>
                     </tr> 
                      </logic:equal>
                      <logic:equal value="1" name ="topicForm" property="topicvo.string(status)">
                     <tr class="list3">
                     	<td align="right" nowrap valign="center"><bean:message key="conlumn.investiagte.survey"/> </td>
							<td align="left">
								<hrms:optioncollection name="topicForm" property="spersonlist"
									collection="list" />
								<html:select name="topicForm" property="sperson"
									onchange="selectobject(this);" style="width:60">
									<html:options collection="list" property="dataValue"
										labelProperty="dataName" />
								</html:select>
								<html:text name="topicForm" property="noticeperson"
									styleClass="TEXT" size="48" maxlength="200" style="width:335px;"/>
								<img src='/images/del.gif' title='清空调查对象' onclick='clearObjs()' />
								<html:hidden name="topicForm" property="selectPerson" />
							</td>
                     </tr> 
                      </logic:equal>                         
          <tr class="list3">
            <td align="center" colspan="2" style="height: 35px">
           	<hrms:submit styleClass="mybutton" property="b_save" onclick="showView();document.topicForm.target='_self';validate( 'R','topicvo.string(content)','调查主题','RD','first_date.','发布日期','RI','topicvo.string(days)','调查天数');return (document.returnValue && ifqrbc());">
            		<bean:message key="button.save"/>
	 	    </hrms:submit>
			<html:reset styleClass="mybutton" property="reset"><bean:message key="button.clear"/></html:reset>

         	<hrms:submit styleClass="mybutton" property="br_return">
            		<bean:message key="button.return"/>
	 		</hrms:submit>  
            </td>
          </tr>          
      </table>
</html:form>
</body>