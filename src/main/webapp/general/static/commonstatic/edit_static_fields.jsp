<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
	UserView userView = (UserView) session
			.getAttribute(WebConstant.userView);
	String bosflag="";
	if(userView != null)
	{
		bosflag=userView.getBosflag();
	}
%>
 <link href="/css/css1.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="/js/validate.js"></script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language="JavaScript">

function showSetList(outparamters)
	{
		var setlist=outparamters.getValue("setlist");
		AjaxBind.bind(staticFieldForm.setlist,setlist);
		if($('setlist').options.length>0)
		{
		  $('setlist').options[0].selected=true;
		  try{
    	   if (navigator.appName.indexOf("Microsoft")!= -1) { 
    	  		 $('setlist').fireEvent('onchange'); 
		        //ie  
		    }else{ 
		        $('setlist').onchange();  
		    }  
		}catch(e){
		}
		}
	}
	function showFieldList(outparamters)
	{
		var fieldlist=outparamters.getValue("fieldlist");
		AjaxBind.bind(staticFieldForm.left_fields,fieldlist);
	}
				
	function searchFieldList()
	{
	   var tablename=$F('setlist');
	   var In_paramters="tablename="+tablename;
   	   var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showFieldList,functionId:'05301010002'});
	}
	function MusterInitData(infor)
	{
	   var pars="base="+infor;
   	   var request=new Request({method:'post',asynchronous:false,parameters:pars,onSuccess:showSetList,functionId:'05301010001'});
	}
	function setitem(listbox)
	{
	   var vos,right_vo,i,list;
       vos= document.getElementsByName(listbox);
       if(vos==null || vos[0].length==0)
       {
         top.close(); 
         return;  	
 	     vos[0].options[0].selected=false;

       }        //设为要可选状态
       right_vo=vos[0]; 
       var flag='0';   
       list="";     
       for(i=0;i<right_vo.options.length;i++)
       {
          flag='1';
	      right_vo.options[i].selected=true;
	      list=list+right_vo.options[i].value+",";
       } 
       var codesetvo=new Object();
        //codesetvo.code = $F("code");
       codesetvo.list = list;
       codesetvo.tran_flag =flag;
       if(getBrowseVersion()){
           parent.window.returnValue=codesetvo;
       		parent.window.close();
       }else if(!getBrowseVersion() && parent.opener && parent.opener.openReturn){//员工历史统计 统计项 新增统计条件  wangb 20190614
       	   parent.opener.openReturn(codesetvo);
       	   parent.window.close();
       }else{//非IE浏览器 回调父页面方法返回数据  wangb 20180127
       		parent.parent.openEdit_field(codesetvo);
       }
       
    }
	
</script>
<hrms:themes />
<%if("hcm".equalsIgnoreCase(bosflag)){ %>
<style>
.EditStaticFieldsTable {
	width:expression(document.body.clientWidth-10);
	height:expression(document.body.clientHeight-20);
}
</style>
<%}else{ %>
<style>
.EditStaticFieldsTable {
	width:expression(document.body.clientWidth-10);
	height:expression(document.body.clientHeight-20);
}
</style>
<%} %>
<html:form action="/general/static/commonstatic/editstatic">
<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0" class="EditStaticFieldsTable">
  <tr>  
    <td valign="top">
      
     <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="RecordRow">
   	  <thead>
       <tr>
        <td class="TableRow" nowrap colspan="3"><bean:message key="static.select"/>
         </td>            	        	        	        
        </tr>
   	  </thead>
   	   <tr>
        <td width="100%" align="center" class="RecordRow_left" nowrap>
          <table border="0" cellspacing="0"  align="center" cellpadding="0">
            <tr>
             <td align="center"  width="48%">
               <table align="center" width="100%" border="0" cellspacing="0" cellpadding="0" style="padding-top: 3px;margin-bottom: -8px">
                <tr>
                 <td align="left">
                     <bean:message key="static.target"/>&nbsp;&nbsp;
                  </td>
                 </tr>
                <tr>
                 <td align="center">
                  <select name="setlist" size="1"  style="width:100%" onchange="searchFieldList();">    
			             <option value="1111">#</option>
			</select>			             
                  </td>
                 </tr>
                <tr>
                 <td align="center">
                  <select name="left_fields" multiple="multiple" ondblclick="additem('left_fields','right_fields');" style="height:228px;width:100%;font-size:9pt">
                   </select>
                   </td>
                  </tr>
                 </table>
                </td>
               <td width="48px" align="center">
               		<table border="0" cellspacing="0"  align="center" cellpadding="0" style="margin-left:6px;">
               			<tr>
               				<td align="center">
					            <html:button  styleClass="mybutton" property="b_addfield" onclick="additem('left_fields','right_fields');">
				            		     <bean:message key="button.setfield.addfield"/> 
					            </html:button>
				            </td>
               			</tr>
               			<tr>
               				<td height="30px"></td>
               			</tr>
               			<tr>
               				<td align="center">
					           <html:button  styleClass="mybutton" property="b_delfield" onclick="removeitem('right_fields');">
					          		     <bean:message key="button.setfield.delfield"/>    
					           </html:button>	
               				</td>
               			</tr>
               		</table>  
                </td>         
                <td width="48%" align="center">
                 <table width="100%" border="0" cellspacing="0" cellpadding="0" align="center" style="padding-top: 3px;margin-bottom: -7px">
                  <tr>
                  <td width="100%" align="left">
                     <bean:message key="static.ytarget"/>&nbsp;&nbsp;
                  </td>
                  </tr>
                  <tr>
                  <td width="100%" align="center">
     	             <html:select name="staticFieldForm" property="right_fields" multiple="multiple" size="10" ondblclick="removeitem('right_fields');" style="height:255px;width:100%;font-size:9pt">
 		                </html:select>
                   </td>
                  </tr>
                 </table>             
                </td>               
                </tr>
              </table>             
            </td>
            </tr>
          <tr>
          <td align="center" class="RecordRow" nowrap  colspan="3" style="height: 35">
           <html:button styleClass="mybutton" property="affirm" onclick="setitem('right_fields');">
             <bean:message key="button.ok"/>
	         </html:button> 
	        
         </td>
        </tr>   
     </table>
   </td>
  </tr>
</table>
</html:form>
<script language="javascript">
   MusterInitData('<bean:write name="staticFieldForm"  property="infor_Flag"/>');
if(!getBrowseVersion()){//兼容非IE浏览器   wangb 20180127
	var RecordRowLeft = document.getElementsByClassName('RecordRow_left')[0];
	RecordRowLeft.style.height ='280px';
	RecordRowLeft.setAttribute('valign','top');
	RecordRowLeft.style.borderRight='1px #c4d8ee solid';
}else{// 调整table 宽高不出现滚动条  wangb  20180718 bug 38670
	var form = document.getElementsByTagName('form')[0];
	var EditStaticFieldsTable = form.getElementsByTagName('table')[0];
	EditStaticFieldsTable.style.width = parseInt(EditStaticFieldsTable.style.width) -60;
	EditStaticFieldsTable.style.height = parseInt(EditStaticFieldsTable.style.height) -40;
}
</script>
<script>
    if(getBrowseVersion()==10){ //非IE浏览器兼容性   wangb 20180127
        if(getIE11Version()){
            //新增统计条件，新增按钮触发页面样式修改  wangbs
            var outForm = document.getElementsByTagName("form")[0];
            outForm.style.width = "99%";

            var Table3 = outForm.getElementsByTagName("table")[3];
            Table3.style.marginBottom = "0px";

            var Table4 = outForm.getElementsByTagName("table")[4];
            Table4.style.marginLeft = "5px";

            var Table5 = outForm.getElementsByTagName("table")[5];
            Table5.style.marginBottom = "0px";

            var select1 = document.getElementsByTagName("select")[1];
            select1.style.height = "235px";
        }
    }
</script>