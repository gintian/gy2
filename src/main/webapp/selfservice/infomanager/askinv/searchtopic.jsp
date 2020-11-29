<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.frame.dao.RecordVo,com.hjsj.hrms.utils.PubFunc"%>

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
<script language="javascript" src="/js/validate.js"></script>

<script language="javascript" src="/js/constant.js"></script>
<script language='javascript'>

	var ViewProperties=new ParameterSet();

	//导入excel
	function inputTemplete(){
	var theurl='/selfservice/infomanager/askinv/import.do?br_selectfile=link';
    var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
    var return_vo= window.showModalDialog(iframe_url, 'mytree_win', 
      		"dialogWidth:500px; dialogHeight:250px;resizable:no;center:yes;scroll:yes;status:no");	 				
  	 if(return_vo){
  	 	var waitInfo=eval("wait");	//显示进度条
	    waitInfo.style.display="block";
   		form1.action="/selfservice/infomanager/askinv/import.do?b_exedata=link";
      	form1.submit(); 
		}
	}
	//下载模板
	function outTemplete(){
		var hashvo=new ParameterSet();	
		hashvo.setValue("model","1");
		var request=new Request({method:'post',asynchronous:false,onSuccess:showfile,functionId:'1030070018'},hashvo);
	}
	function showfile(outparamters)
	{
		var outName=outparamters.getValue("outName");
		window.location.target="_blank";
		window.location.href = "/servlet/vfsservlet?fromjavafolder=true&fileid="+outName;
	}
</script>
<%
	int i=0;
%>


<html:form action="/selfservice/infomanager/askinv/searchtopic" styleId="form1">
<div id='wait'
				style='position: absolute; top: 200; left: 250; display: none;'>
				<table border="1" width="400" cellspacing="0" cellpadding="4"
					class="table_style" height="87" align="center">
					<tr>
						<td class="td_style" height=24>
							正在导入问卷调查....
						</td>
					</tr>
					<tr>
						<td style="font-size: 12px; line-height: 200%" align=center>
							<marquee class="marquee_style" direction="right" width="300"
								scrollamount="5" scrolldelay="10">
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
	<table width="70%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" style="margin-top:6px;">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap>
            <input type="checkbox" name="selbox" onclick="batch_select(this,'topicForm.select');" title='<bean:message key="label.query.selectall"/>'>&nbsp;	    
            </td>           
            <td align="center" class="TableRow" nowrap>
		<bean:message key="conlumn.investigate.content"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="conlumn.investigate.releasedate"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="conlumn.investigate.enddate"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="conlumn.investigate.days"/>&nbsp;
	    </td>
	     <td align="center" class="TableRow" nowrap>
		<bean:message key="conlumn.investigate.flag"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="conlumn.investigate.status"/>&nbsp;
	    </td>
           
	   	    	    	    
           
            <td align="center" class="TableRow" nowrap>
		<bean:message key="label.edit"/>            	
	    </td>
	      <td align="center" class="TableRow" nowrap>
		<bean:message key="conlumn.investigate.additem"/>&nbsp;
	    </td>
	    <td align="center" class="TableRow" nowrap><bean:message key="conlumn.investigate.endview"/>&nbsp;</td>
	    <td align="center" class="TableRow" nowrap>
	    <bean:message key="conlumn.investigate.endinv"/>&nbsp;
	    </td>
           	    	    		        	        	        
           </tr>
   	  </thead>
          <hrms:extenditerate id="element" name="topicForm" property="topicForm.list" indexes="indexes"  pagination="topicForm.pagination" pageCount="10" scope="session">
          <%
          if(i%2==0)
          {
          %>
          <tr class="trShallow">
          <%}
          else
          {%>
          <tr class="trDeep">
          <%
          }
          i++;          
          %>  
            <td align="center" class="RecordRow" nowrap>
     		   <hrms:checkmultibox name="topicForm" property="topicForm.select" value="true" indexes="indexes"/>&nbsp;
	    </td>            
            <td align="left" class="RecordRow" >
                   &nbsp;<bean:write name="element" property="string(content)" filter="true"/>&nbsp;
	    </td>
         
            <td align="left" class="RecordRow" nowrap>
                   &nbsp;<bean:write name="element" property="string(releasedate)" filter="true"/>&nbsp;
	    </td>
            <td align="left" class="RecordRow" nowrap>
                   &nbsp;<bean:write name="element" property="string(enddate)" filter="true"/>&nbsp;
	    </td>
            <td align="left" class="RecordRow" nowrap>
                   &nbsp;<bean:write name="element" property="string(days)" filter="true"/>&nbsp;
	    </td> 	                
            <td align="left" class="RecordRow" nowrap>
             <logic:equal name="element" property="string(flag)" value="1">
	    	   &nbsp;<bean:message key="datestyle.yes"/>
	    	   </logic:equal>
	    	   <logic:equal name="element" property="string(flag)" value="0">
	    	   &nbsp;<bean:message key="datesytle.no"/>
	    	   </logic:equal>
                   
	    </td>
	    <td align="left" class="RecordRow" nowrap>
	    	   <logic:equal name="element" property="string(status)" value="0">
	    	   &nbsp;<bean:message key="lable.investigate.single"/>
	    	   </logic:equal>
	    	   <logic:equal name="element" property="string(status)" value="1">
	    	   &nbsp;<bean:message key="lable.investigate.multil"/>
	    	   </logic:equal>
	    	   
                  
	    </td>
	    <%
	      RecordVo avo=(RecordVo)pageContext.getAttribute("element");
	      String id=avo.getString("id");
	      String mdid=PubFunc.encryption(id);
	      String flag = avo.getString("flag");
	      String status = avo.getString("status");
	      String content = avo.getString("content");
	     %>
              	    
           
            <td align="center" class="RecordRow" nowrap>
            	<a href="/selfservice/infomanager/askinv/addtopic.do?b_query=link&encryptParam=<%=PubFunc.encrypt("a_id="+mdid+"&flag="+flag+"&status="+status) %>"><img src="/images/edit.gif" border=0></a>
	    </td>
	      <td align="center" class="RecordRow" nowrap>
            	<a href="/selfservice/infomanager/askinv/additem.do?b_addquery=link&encryptParam=<%=PubFunc.encrypt("id="+mdid+"&content="+content) %>" ><img src="/images/edit.gif" border=0></a>
	    </td>
	    <td align="center" class="RecordRow" nowrap>
	    	<a href="/selfservice/infomanager/askinv/searchendview.do?b_query=link&encryptParam=<%=PubFunc.encrypt("id="+mdid+"&f=0") %>"><img src="/images/view.gif" border=0></a>
	    </td>
	    <td align="center" class="RecordRow" nowrap>
	    	<a href="/selfservice/infomanager/askinv/addtopic.do?b_end=link&encryptParam=<%=PubFunc.encrypt("a_id="+mdid+"&content="+content) %>" ><img src="/images/edit.gif" border=0></a>
	    </td>
           	    	    		        	        	        
          </tr>
        </hrms:extenditerate>
        
    
<tr><td colspan="11">
   <table  width="100%" align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
				<bean:message key="label.page.serial" />
					<bean:write name="topicForm" property="topicForm.pagination.current" filter="true" />
				<bean:message key="label.page.sum" />
					<bean:write name="topicForm" property="topicForm.pagination.count" filter="true" />
				<bean:message key="label.page.row" />
					<bean:write name="topicForm" property="topicForm.pagination.pages" filter="true" />
				<bean:message key="label.page.page" />
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="topicForm" property="topicForm.pagination"
				nameId="topicForm" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
  </table>
</td></tr>
</table>



<table  width="70%" align="center">
          <tr>
            <td align="center" height="35px;">
         	<hrms:submit styleClass="mybutton" property="b_add">
            		<bean:message key="button.insert"/>
	 	</hrms:submit>
         	<hrms:submit styleClass="mybutton" property="b_delete" onclick="document.topicForm.target='_self';validate( 'R','topicvo.string(id)','项目名称');return (document.returnValue && ifdel());">
            		<bean:message key="button.delete"/>
	 	</hrms:submit>
        <input class="mybutton" type="button" name="exe" value="<bean:message key="button.download.template"/>" onclick="outTemplete();"/>	
        <input class="mybutton" type="button" name="imp" value="<bean:message key="import.tempData"/>" onclick="inputTemplete();"/>
            </td>
          </tr>          
</table>

</html:form>
<script type="text/javascript">
<!--
function ifdel()
{	
	   var isSelected=false;
   	   for(var i=0;i<document.topicForm.elements.length;i++)
   	   {
   			if(document.topicForm.elements[i].type=='checkbox'&&document.topicForm.elements[i].name.length>17&&document.topicForm.elements[i].name.substring(0,17)=='topicForm.select[')
   			{
   				if(document.topicForm.elements[i].checked==true)
   				{
   					isSelected=true;
   					
   				}  				
   			}
   		}
   		
  		if(!isSelected)
  		{
  			alert(PLASE_SELECT_RECORD+"！");
  			return false ;
  		}else{
  			return ( confirm('确认删除选择的项目？') );
  		}		

}

//-->
</script>
