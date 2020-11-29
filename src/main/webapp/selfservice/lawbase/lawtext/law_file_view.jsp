<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.utils.ResourceFactory"%>
<%@ page import="com.hjsj.hrms.actionform.lawbase.LawBaseForm,
				com.hrms.hjsj.sys.FieldItem,
				org.apache.commons.beanutils.LazyDynaBean,
				java.util.ArrayList,
				com.hjsj.hrms.utils.PubFunc" %>
<%
LawBaseForm lawForm=(LawBaseForm)session.getAttribute("lawbaseForm");
ArrayList lawBaseFileList = lawForm.getLawBaseFileList();
%>
<SCRIPT LANGUAGE="javascript">
    function getArguments(up_base)
    {
    	var up_node,base_id,val;
    	var paraArray=dialogArguments;
    	up_node = paraArray[0]; 
    	if(up_node==null)
    	   return;
    	base_id=up_node.uid;
    	val=MM_findObj_(up_base);
    	if(val==null)
    	  return;
    	val.value=base_id;
    	//alert(base_id);
    }
     function ajaxcheck(file_id)
   {
      var hashvo=new ParameterSet();
      hashvo.setValue("file_id",file_id);         
      hashvo.setValue("query","download");         
      var request=new Request({method:'post',asynchronous:false,onSuccess:showCheckFlag,functionId:'10400201035'},hashvo);
   }
   function showCheckFlag(outparamters)
   {
      var sturt=outparamters.getValue("sturt");
      var file_id=outparamters.getValue("file_id");  
      //xus 20/5/9 【60211】VFS+UTF- 8+达梦：自助服务/规章制度，制度政策标题，点击上传文件超链接，显示一片空白，文件没有被下载
      if(!file_id || file_id == ''){
    	alert("文件不存在！"); 
      }
      if(sturt=="false")
      {
        alert("您没有这个权限，查看条记录！");
        return false;
      }else
      { 
      	var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
      	var isSafari = userAgent.indexOf("Safari") > -1 && userAgent.indexOf("Chrome") == -1; //判断是否Safari浏览器 
      	if(isSafari){//Safari浏览器 下载单独处理   bug 35072 wangb 20180303
        	window.open("/servlet/vfsservlet?fileid="+file_id,"_blank");
      	}else{
      		var downloadFile = document.getElementById('downloadFile');
      		downloadFile.href="/servlet/vfsservlet?fileid="+file_id;
      		downloadFile.click();
      	} 
      }
   }	
</SCRIPT>  

<base id="mybase" target="_self">
<html:form action="/selfservice/lawbase/lawtext/law_view_base" enctype="multipart/form-data">
	  <a href="#" id="downloadFile" style="display:none;"></a>
      <table width="80%" border="0" cellpadding="0" cellspacing="0" align="center" class="ListTable" style="margin-top: 20px;">
          <tr height="20">
       		<td align="center" colspan="2" class="TableRow">
	       		<logic:notEqual name="lawbaseForm" property="basetype" value="5">
	       			制度政策
	       		</logic:notEqual>
	       		<logic:equal name="lawbaseForm" property="basetype" value="5">
	       			文件档案
	       		</logic:equal>
       		</td>           	      
          </tr> 
          <%if(lawBaseFileList.size()==0){ %>          
          <tr>
            <td class="framestyle RecordRow" width="80px" align="right" nowrap>
            	<bean:message key="lable.lawfile.title"/></td>
            <td align="left" class="framestyle3 RecordRow" nowrap>
                <bean:write name="lawbaseForm" property="lawFileVo.string(title)" filter="true"/>
            </td>
          </tr>
       <logic:notEqual name="lawbaseForm" property="basetype" value="5">  
       <logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",type,">           
            <tr>
            <td class="framestyle RecordRow" width="80px" align="right" nowrap>
            <bean:message key="lable.lawfile.typenum"/></td>
            <td class="framestyle3 RecordRow" nowrap>
	 			<bean:write name="lawbaseForm" property="lawFileVo.string(type)" filter="true"/>
	 		</td>
	 		</tr>
          </logic:notMatch>
      </logic:notEqual>
      <logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",content_type,">
           <tr>
            <td class="framestyle RecordRow" width="80px" align="right" nowrap>
               <bean:message key="lable.lawfile.contenttype"/>
            </td>
            <td class="framestyle3 RecordRow" nowrap>
	 			<bean:write name="lawbaseForm" property="lawFileVo.string(content_type)" filter="true"/>
	 		</td>
          </tr>
          </logic:notMatch>
      <logic:notEqual name="lawbaseForm" property="basetype" value="5">
      <logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",valid,">
          <tr>
          <td class="framestyle RecordRow" width="80px" align="right" nowrap>
               <bean:message key="lable.lawfile.valid"/>
          </td><td class="framestyle3 RecordRow" nowrap>
	 	<logic:equal name="lawbaseForm" property="lawFileVo.string(valid)" value="1">
	 	    <bean:message key="lable.lawfile.availability"/>
	 	 </logic:equal>
	 	 <logic:equal name="lawbaseForm" property="lawFileVo.string(valid)" value="0">
	 	    <bean:message key="lable.lawfile.invalidation"/>
	 	 </logic:equal>
	 	 <logic:equal name="lawbaseForm" property="lawFileVo.string(valid)" value="2">
	 	    <bean:message key="lable.lawfile.nowmodify"/>
	 	 </logic:equal>
	 	 <logic:equal name="lawbaseForm" property="lawFileVo.string(valid)" value="3">
	 	    <bean:message key="lable.lawfile.other"/>
	 	 </logic:equal>	      
	 	</td></tr>
          </logic:notMatch>
     </logic:notEqual>
     <logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",note_num,">
          <tr>
	 	<td class="framestyle RecordRow" width="80px" align="right" nowrap>
               <bean:message key="lable.lawfile.notenum"/></td>
               <td class="framestyle3 RecordRow" nowrap>
	 	<bean:write name="lawbaseForm" property="lawFileVo.string(note_num)" filter="true"/>
	 	</td></tr>
          </logic:notMatch>
    <logic:notEqual name="lawbaseForm" property="basetype" value="5">
    <logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",issue_org,">
          <tr>
	 	<td  class="framestyle RecordRow" width="80px" align="right" nowrap>
	 	<bean:message key="lable.lawfile.issue_org"/></td>
	 	<td class="framestyle3 RecordRow" nowrap>
	 	<bean:write name="lawbaseForm" property="lawFileVo.string(issue_org)" filter="true"/>
	 	</td></tr>
          </logic:notMatch>
          <logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",notes,">
          <tr>
	 	<td class="framestyle RecordRow" width="80px" align="right" nowrap>
              <bean:message key="lable.lawfile.note"/></td><td class="framestyle3 RecordRow" nowrap>
	 	
	 	<bean:write name="lawbaseForm" property="lawFileVo.string(notes)" filter="true"/>
	 	</td></tr>
          </logic:notMatch>
    </logic:notEqual>
    <logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",issue_date,">
          <tr>
	 	<td class="framestyle RecordRow" width="80px" align="right" nowrap>
	 	<bean:message key="lable.lawfile.printmandate"/></td><td class="framestyle3 RecordRow" nowrap>	
	 	              <bean:write name="lawbaseForm" property="first_date.year" filter="true"/><bean:message key="datestyle.year"/>
            		      <bean:write name="lawbaseForm" property="first_date.month" filter="true"/>
            		      <bean:message key="datestyle.month"/>
            		      <bean:write name="lawbaseForm" property="first_date.date" filter="true"/>
            		      <bean:message key="datestyle.day"/>    
              </td>
          </tr>
          </logic:notMatch>
    <logic:notEqual name="lawbaseForm" property="basetype" value="5">
    <logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",implement_date,">
          <tr>
	 	<td class="framestyle RecordRow" width="80px" align="right" nowrap>
               <bean:message key="lable.lawfile.actualizedate"/></td><td class="framestyle3 RecordRow" nowrap>	
	 	                <bean:write name="lawbaseForm" property="second_date.year" filter="true"/>
	 	                <bean:message key="datestyle.year"/>
            			<bean:write name="lawbaseForm" property="second_date.month" filter="true"/><bean:message key="datestyle.month"/>
            			<bean:write name="lawbaseForm" property="second_date.date" filter="true"/><bean:message key="datestyle.day"/></td></tr>
	 	
          </logic:notMatch>
          <logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",valid_date,">
          <tr>
	 	<td class="framestyle RecordRow" width="80px" align="right" nowrap><bean:message key="lable.lawfile.invalidationdate"/></td><td class="framestyle3 RecordRow" nowrap>	
	 	        <bean:write name="lawbaseForm" property="third_date.year" filter="true"/>
	 	        <bean:message key="datestyle.year"/>
	 	        <bean:write name="lawbaseForm" property="third_date.month" filter="true"/>
            		<bean:message key="datestyle.month"/>
            		<bean:write name="lawbaseForm" property="third_date.date" filter="true"/>
            		<bean:message key="datestyle.day"/></td></tr>
	 	
          </logic:notMatch>
    </logic:notEqual>
    <logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",content,">
            <tr>
	 	<td class="framestyle RecordRow" width="80px" align="right" nowrap>
               <bean:message key="lable.lawfile.upfile"/></td>    
	 	<td  nowrap class="framestyle3 RecordRow" nowrap>
	 	<bean:define id="fileid" name="lawbaseForm" property="lawFileVo.string(file_id)"/>
	 	<a href="###" onclick="ajaxcheck('<%=PubFunc.encrypt((String)fileid) %>');">
	 	   <bean:write name="lawbaseForm" property="lawFileVo.string(name)" filter="true"/>
	 	   </a>
	 	</td></tr>
      </logic:notMatch>    
      <%}else{     
      for(int i=0;i<lawBaseFileList.size();i++){
	LazyDynaBean bean = (LazyDynaBean)lawBaseFileList.get(i);
	String itemid = (String)bean.get("itemid");
	if(itemid.equalsIgnoreCase("digest")||itemid.equalsIgnoreCase("originalext")||itemid.equalsIgnoreCase("ext"))
		continue;
	String itemdesc = (String)bean.get("itemdesc");
	String itemtype = (String)bean.get("itemtype");
	int decWidth=Integer.parseInt((String)bean.get("decWidth"));
	int len = Integer.parseInt((String)bean.get("len"));
	String codesetid = (String)bean.get("codesetid");
	String value = (String)bean.get("value");
	String viewvalue = (String)bean.get("viewvalue");
	
	
%>
<tr>

		<td width='80' align="right" class="framestyle3 RecordRow" nowrap>
		<%=itemdesc %>
		</td>
		<td class="framestyle3 RecordRow"  nowrap>
		
<%
		if(itemid.equalsIgnoreCase("title")){
%>			
	<%=value %>
<% 
		}else if(itemid.equalsIgnoreCase("type")){
		
%>			
<%=value %>
<% 
		}else if(itemid.equalsIgnoreCase("content_type")){
%>
<%=value %>
			</td>
			<td>
<% 
		}else if(itemid.equalsIgnoreCase("valid")){
%>
<%if("1".equals(value)) {%><bean:message key="lable.lawfile.availability" /><%}else if("2".equals(value)){ %>
<bean:message key="lable.lawfile.partabolish" /><%}else if("3".equals(value)){ %>
<bean:message key="lable.lawfile.editing" /><%}else if("4".equals(value)){ %>
<bean:message key="lable.lawfile.other" /><%}else if("0".equals(value)){ %>
<bean:message key="lable.lawfile.allabolish" /><%} %>
<%
		}else if(itemid.equalsIgnoreCase("note_num")){
%>
<%=value %>
<% 
		}else if(itemid.equalsIgnoreCase("b0110")){
%>

			<% out.print(viewvalue); %>
<% 
		}else if(itemid.equalsIgnoreCase("issue_org")){
%>
<%=value %>
<% 
		}else if(itemid.equalsIgnoreCase("notes")){
%>
<%=value %>
<% 
		}else if(itemid.equalsIgnoreCase("issue_date")){
%>
							<bean:write name="lawbaseForm" property="first_date.year" filter="true"/><bean:message key="datestyle.year"/>
            		      <bean:write name="lawbaseForm" property="first_date.month" filter="true"/>
            		      <bean:message key="datestyle.month"/>
            		      <bean:write name="lawbaseForm" property="first_date.date" filter="true"/>
            		      <bean:message key="datestyle.day"/> 
<% 
		}else if(itemid.equalsIgnoreCase("implement_date")){
%>
							<bean:write name="lawbaseForm" property="second_date.year" filter="true"/><bean:message key="datestyle.year"/>
            		      <bean:write name="lawbaseForm" property="second_date.month" filter="true"/>
            		      <bean:message key="datestyle.month"/>
            		      <bean:write name="lawbaseForm" property="second_date.date" filter="true"/>
            		      <bean:message key="datestyle.day"/> 
<% 
		}else if(itemid.equalsIgnoreCase("valid_date")){
%>
							<bean:write name="lawbaseForm" property="third_date.year" filter="true"/><bean:message key="datestyle.year"/>
            		      <bean:write name="lawbaseForm" property="third_date.month" filter="true"/>
            		      <bean:message key="datestyle.month"/>
            		      <bean:write name="lawbaseForm" property="third_date.date" filter="true"/>
            		      <bean:message key="datestyle.day"/> 
<% 
		}else if(itemid.equalsIgnoreCase("name")){
%>
			<%=value %>
<% 		
		}else if(itemid.equalsIgnoreCase("ext")){
%>	
		<a href="###" onclick="ajaxcheck('<bean:write name="lawbaseForm" property="lawFileVo.string(file_id)" filter="true"/>');">
	 	   <bean:write name="lawbaseForm" property="lawFileVo.string(title)" filter="true"/>
	 	   </a>
<%
		}else if(itemid.equalsIgnoreCase("originalext")){
%>

<% 
		}else if(itemid.equalsIgnoreCase("viewcount")){
	
%>
		<%=value %>
<%
}else{
	if(itemtype.equalsIgnoreCase("A")){
		if(codesetid.equals("0")){
			//字符型
			out.print(value);
		}else{
			//代码型
			out.print(viewvalue);
		    }
	}else if(itemtype.equalsIgnoreCase("D")){
		out.print(value);
	}else if(itemtype.equalsIgnoreCase("M")){
		out.println("<textarea name=\"lawBaseFileList["+i+"].value\" rows='10' readonly=readonly  wrap='ON' cols='60' class='textboxMul'>"+value+"</textarea>");
	}else if(itemtype.equalsIgnoreCase("N")){
		out.print(value);
	}
}
%>			
		
		</td>
		</tr>
<% 
	}
} 
%> 
                                                
          <tr class="list3" style="padding-top: 5px;">
            <td align="center" colspan="2">
         	
		<!-- <html:submit styleClass="mybutton" property="b_return"><bean:message key="button.return"/></html:submit>	 	-->
			 <button class="mybutton" onclick="returnBack();" value=""><bean:message key="button.return"/></button>	
            </td>
          </tr>  
          
      </table>
</html:form>
<script language="JavaScript">
 function returnBack(){
	 var url = window.location.href;
	 var actionUrl = "/selfservice/lawbase/lawtext/law_maintenance.do?b_query=link&isback=y&encryptParam=${lawbaseForm.encryptParam}";
	 if(url.indexOf("&viewFlag=search") > -1) {
		 actionUrl = "/selfservice/lawbase/lawtext/law_term_query.do?b_query=link";
	 } else if(url.indexOf("&viewFlag=globalsearch") > -1) {
		 actionUrl = "/selfservice/lawbase/lawtext/globalsearch.do?b_query=link";
	 }else if(url.indexOf("&type=view") > -1) {
		 actionUrl = "/selfservice/lawbase/law_maintenance.do?b_query=link";
	 }
	 
	 lawbaseForm.action = actionUrl;
	 lawbaseForm.submit();
 }
 if(!getBrowseVersion()){//兼容非IE浏览器样式问题   wangb 20180208 bug 34699
 	var buttons = document.getElementsByTagName('button');
 	buttons[0].style.marginTop = '10px';
 }
</script>
