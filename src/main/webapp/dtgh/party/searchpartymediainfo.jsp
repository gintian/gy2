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

<script>
  
   function savefile(flag){
			   var fileEx = partyBusinessForm.picturefile.value;
				 if(fileEx == "")
				 {
			    	alert(SELECT_FIELD+"!");
			    	
			    	return ;
			     }
			     if(!validateUploadFilePath(fileEx)){
			     	return;
			     }
			     var  obj=document.getElementById('FileView'); 
		      if (obj != null)
		      {
		             obj.SetFileName(fileEx);
		             var facSize=obj.GetFileSize();
		             if(facSize==-1)
		             {
						     alert(FIELD_NOT_EXIST);
						     return;                
		             }   
				}
				else
				    return;
	   partyBusinessForm.action="/dtgh/party/searchpartybusinesslist.do?b_Sfile=link&oper="+flag;
	   partyBusinessForm.submit();
	   
   }
   
   function delfile(flag){
	   partyBusinessForm.action="/dtgh/party/searchpartybusinesslist.do?b_Sfile=link&oper="+flag;
	   partyBusinessForm.submit();
   }
   
   function upload()
	{
		var fileEx = mInformForm.picturefile.value;
		 if(fileEx == "")
		 {
	    	alert(SELECT_FIELD+"!");
	    	
	    	return ;
	     }
	     if(!validateUploadFilePath(fileEx)){
	     	return;
	     }
	     var  obj=document.getElementById('FileView'); 
        if (obj != null)
        {
               obj.SetFileName(fileEx);
               var facSize=obj.GetFileSize();
               if(facSize==-1)
               {
				     alert(FIELD_NOT_EXIST);
				     return;                
               }   
 		}
 		else
 		    return;
	    document.getElementById("filepath").value=fileEx;
		document.getElementById("i9999").value="";
		var flag = document.getElementById("filetype").value;
		if(flag=="")
		{	
			alert(SELECT_TYPE+"!");
			return ;
		}
		
		document.mInformForm.action="/general/inform/emp/view/savemultimedia.do?b_query=link";
		document.mInformForm.submit();
		document.getElementById('wcommit').disabled=true;
	}
   
   
   
   
   
   
   function exeReturn(returnStr,target)
   {
     //target_url=returnStr;
    // window.open(target_url,target); 
      partyBusinessForm.action=returnStr;
      partyBusinessForm.target=target;
      partyBusinessForm.submit();
   }
</script>

  <link href="<%=css_url%>" rel='stylesheet' type='text/css' >
 <html:form action="/dtgh/party/searchpartybusinesslist" enctype="multipart/form-data">

 <div style="position: absolute; top: 10%; left:30%; display: block;"  align="center" id="div1">
    <fieldset style="width: 300px;height: 150px;" >
       <legend>岗位说明书</legend>
        <table class="ListTable" width="80%"  >
          <logic:notEmpty name="partyBusinessForm" property="medialist">
                <tr><td>&nbsp;</td></tr>
		         <tr>
		            <td  class="tableRow" nowrap="nowrap" width="80%" align="center">标题</td>
		            <td  class="tableRow" align="center" nowrap="nowrap">操作</td>
		         </tr>
		         <logic:iterate id="ele" name="partyBusinessForm" property="medialist">
		              <tr>
		                 <td class="RecordRow" nowrap="nowrap" style="padding-left: 20px;"><a href="/pos/roleinfo/pos_dept_post?usertable=h00&usernumber=${ele.map.h0100}&i9999=1">${ele.map.title}</a></td>
		                 <td class="RecordRow" align="center"><a href="javascript:delfile('del')">删除</a></td>
		              </tr>
		              <bean:define id="titledesc" value="${ele.map.title} "></bean:define>
		         </logic:iterate>
		         <tr>
		            <td style="padding-top: 10px" colspan="2">
		               <button class="mybutton" id="add_button" onclick="javascript:changediv('');"> 更换说明书  </button>&nbsp;<button class="mybutton" onclick="exeReturn('/dtgh/party/searchpartybusinesslist.do?b_query=link','mil_body')">返回</button>
		            </td>
		         </tr>
		         
		         
		         
         </logic:notEmpty>
         <logic:empty name="partyBusinessForm" property="medialist">
                <tr> <td colspan="2" style="padding-bottom: 10px;padding-top: 10px;">该岗位暂无岗位说明书。</td></tr>  
                <tr>
                   <td class="RecordRow" align="center"><bean:message key="general.mediainfo.title"/></td>
                   <td class="RecordRow" width="180"><input type="text" name="filename" class="textColorWrite" style="width: 180px;" ></td>
                </tr>
                <tr>
                   <td class="RecordRow" align="center"><bean:message key="conlumn.mediainfo.filename" /></td>
                   <td class="RecordRow" ><html:file property="picturefile" name="partyBusinessForm" styleClass="textColorWrite" style="width:180px;"/>   </td>
                </tr>
                <tr>
		            <td style="padding-top: 10px" colspan="2">
		               <button class="mybutton" onclick="savefile('save')"> 确定  </button>&nbsp;<button class="mybutton" onclick="exeReturn('/dtgh/party/searchpartybusinesslist.do?b_query=link','mil_body')">返回</button>
		            </td>
		         </tr>
         </logic:empty>
     </table>
     
    </fieldset>
 </div>  
  
  <logic:notEmpty name="partyBusinessForm" property="medialist">
	<div style="position: absolute; top: 10%; left:30%; display: none;z-index: 1"  align="center" id="div2">
	     <fieldset style="width: 300px;height: 150px;" >
	       <legend>岗位说明书</legend>
	        <table class="ListTable" width="80%"  >
	                <tr> <td colspan="2" style="padding-bottom: 10px;">替换岗位说明书</td></tr>  
	                <tr>
	                   <td class="RecordRow" align="center"><bean:message key="general.mediainfo.title"/></td>
	                   <td class="RecordRow" width="180"><input type="text" name="filename" class="textColorWrite" style="width: 180px;" value="${titledesc }"></td>
	                </tr>
	                <tr>
	                   <td class="RecordRow" align="center"><bean:message key="conlumn.mediainfo.filename"/></td>
	                   <td class="RecordRow" ><html:file property="picturefile" name="partyBusinessForm" styleClass="textColorWrite" style="width:180px;"/></td>
	                </tr>
	                <tr>
			            <td style="padding-top: 10px" colspan="2">
			               <button class="mybutton" onclick="savefile('update')"> 确定 </button>&nbsp;<button class="mybutton" onclick="changediv()">返回</button>
			            </td>
			         </tr>
	     </table>
	     
	    </fieldset>
	
	
	</div>
  </logic:notEmpty>
  
<script src="/general/sys/hjaxmanage.js"></script>
<script>
addAx("FileView", "FileViewerX");
</script>
 </html:form>

 <script>
 
    function changediv(){
    	var div1 = document.getElementById("div1");
    	var div2 = document.getElementById("div2");
    	if(div1.style.display == 'block'){
    		div1.style.display='none';
    		div2.style.display='block';
    	}else{
    		div1.style.display='block';
    		div2.style.display='none';
    	}
    }
 </script>