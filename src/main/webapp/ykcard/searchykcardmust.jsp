<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.actionform.ykcard.CardConstantForm" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.taglib.CommonData"%>
<%
UserView userView=(UserView)session.getAttribute(WebConstant.userView);
%>
<script language="javascript" src="/module/utils/js/template.js"></script>
<style type="text/css">
.RecordRowC {
	border: inset 1px #C4D8EE;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 1pt solid;
	font-size: 12px;
}
.RecordRowC1 {
	border: inset 1px #C4D8EE;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 2pt solid; 
	BORDER-TOP: #C4D8EE 1pt solid;
	font-size: 12px;
}
</style>
<hrms:themes></hrms:themes>
<%
                         CardConstantForm cardConstantForm=(CardConstantForm)session.getAttribute("cardConstantForm");
                         ArrayList mustmesslist=cardConstantForm.getMustmesslist();
                         ArrayList codesetlist = cardConstantForm.getCodesetlist();
                         int i=0;
                      %>
<script language="javascript">
 function setTable(fashion,codename,codevalue)
 {
    if(fashion=="0")
    {
    
    }else if(fashion=="1")
    {
       
    }
 }
 function setFashion(fashion,orderid,codename,codeitem)
 {
   if(fashion=="0")
    {
      setTableFashion(codeitem);
    }else if(fashion=="1")
    {
        setCodeFashion(orderid,codename,codeitem)
    }
 }
 function setTableFashion(codeitemid)
 {
    var ro_obj=document.getElementById("radio_0");
    if(ro_obj.checked==true)
    {
        var theurl="/ykcard/mustconstantset.do?b_selectchild=link&mustflag=0&showtitle=0";
       /*
        var return_vo= window.showModalDialog(theurl,1, 
        "dialogWidth:500px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
        if(!return_vo)
	  return false;	   
        if(return_vo.mess!='')
        {
		  var in_obj=document.getElementById(codeitemid);  
		  in_obj.innerHTML=return_vo.mess;
		}else{
			document.getElementById(codeitemid).innerHTML=return_vo.mess;; 
		}
		*/
		openExtWin(theurl,codeitemid);
    }else
    {
      alert("请选择按单位显示登记表！");
    }
 }
 
 function setCodeFashion(orderid,codesetname,codeitemid)
 {
    var ro_obj=document.getElementById("radio_1");    
    if(ro_obj.checked==true)
    {
        var theurl="/ykcard/mustconstantset.do?b_selectchild=link&mustflag=1&showtitle=0&codeitemid="+codeitemid+"&codesetname="+codesetname+"&orderid="+orderid+"&&codename="+$URL.encode('${cardConstantForm.codename}');
        /*
        var return_vo= window.showModalDialog(theurl,0, 
        "dialogWidth:500px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
        //var winFeatures = "dialogHeight:300px; dialogLeft:200px;"; 
        //newwindow=window.open(theurl,'_blank','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top=200,left=250,width=596,height=354'); 
       if(!return_vo)
	  return false;	   
       if(return_vo.mess!='')
        {
	  var in_obj=document.getElementById(codeitemid);  
	  in_obj.innerHTML=return_vo.mess;
	}else
	{
	  var in_obj=document.getElementById(codeitemid);  
	  in_obj.innerHTML="";
	}
	*/
	openExtWin(theurl,codeitemid);
    }else
    {
      alert("请选择按代码类型显示登记表！");
    }
 }
 //改用Ext 弹窗显示  wangb 20190318
function openExtWin(theurl,codeitemid){
	 var win = Ext.create('Ext.window.Window',{
		id:'selectSalaryTable',
		title:'选择花名册',
		width:520,
		height:370,
		resizable:'no',
		modal:true,
		autoScoll:false,
		autoShow:true,
		autoDestroy:true,
		html:'<iframe style="background-color:#fff" frameborder="0" SCROLLING=NO height="100%" width="100%" src="'+theurl+'"></iframe>',
		renderTo:Ext.getBody(),
		listeners:{
			'close':function(){
				if (!this.return_vo) 
        			return false;
        		if (this.return_vo.mess != '') {
            		var in_obj = document.getElementById(codeitemid);
            		in_obj.innerHTML = this.return_vo.mess;
        		} else {
            		var in_obj = document.getElementById(codeitemid);
            		in_obj.innerHTML = "";
        		}
			}
		}
	 });
}
 function strCodetype()
 {
    var ro_obj=document.getElementById("radio_1");    
    if(ro_obj.checked==true)
    {
       cardConstantForm.action="/ykcard/mustconstantset.do?b_setchild=link";
       cardConstantForm.submit(); 
      
    }else
    {
       alert("请选择按代码类型显示登记表！");
    }
 }
 function showFashion(flag)
 {
   if(flag=="")
     flag="1";
   if(flag=="1")
   {
     var waitInfo=eval("wait");	
     waitInfo.style.display="block";
     waitInfo=eval("b0110");	
     waitInfo.style.display="none";
   }else if(flag=="0")
   {
     var waitInfo=eval("wait");	
     waitInfo.style.display="none";
     waitInfo=eval("b0110");	
     waitInfo.style.display="block";
   }
 }
 function cleanStruts()
 {
    var tt="确定是要清除薪酬设置吗？";
    <%
       if(userView.isSuper_admin())
       {
    %>
        tt="确定清除所有单位的薪酬表设置吗？";
    <%  }
    %>
    if(confirm(tt))
    {
      var request=new Request({method:'post',onSuccess:showClean,functionId:'1010030014'});
    }
      
 }
 function showClean(outparamters)
 {
     var flagmess=outparamters.getValue("flagmess");  
     alert(flagmess);
     location.reload();
     
 }
 
 function save(){
	 if(document.getElementById("b0110").style.display=='block')
		 return;//按单位选择花名册 设置已保存参数设置，直接return
	 var codesetlist = "<%=codesetlist%>";
   	 var flag = false;  
	<%
	for(int j=0;j<codesetlist.size();j++){
		CommonData dataobj = (CommonData)codesetlist.get(j);
		String dataValue = dataobj.getDataValue();
	%>
		var valueId = document.getElementById("<%=dataValue%>");
		if(valueId){
			var value = valueId.innerHTML;
			value = value.replace("/\s/g","");
			if(value!=null && value!=""){
				flag = true;
			}
		}
	<%
	}
	%>
	 if(flag){
		 cardConstantForm.action="/ykcard/mustconstantset.do?b_save=link";
	     cardConstantForm.submit();
	 }else{
		 alert("请选择花名册！");
	} 
 }
 function goback(){
 	cardConstantForm.action="/ykcard/recordconstantset.do?b_search=set";
    cardConstantForm.submit(); 
 }
</script>
<html:form action="/ykcard/mustconstantset">
	<table width="80%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable complex_border_color">
		<thead>
			<tr>
				<td align="left" class="TableRow" colspan="2" nowrap>
					薪酬表高级花名册设置&nbsp;
				</td>
			</tr>
		</thead>		
		<tr>
		      <td height="30" class="RecordRowC common_border_color" nowrap style="border-right:none;">
		         <table width="90%" border="0" cellpadding="0" cellspacing="0">
		           <tr>
		             <td  width="15%" nowrap>
		               &nbsp;
		              </td>
		              <td width="20%" align="right">
		                &nbsp;选择显示的方式&nbsp;
		              </td>
			       <td width="65%" nowrap>
				<html:radio name="cardConstantForm" property="mustflag" styleId="radio_0" onclick="showFashion('0');" value="0"/>&nbsp; 按单位选择花名册
				<html:radio name="cardConstantForm" property="mustflag" styleId="radio_1" onclick="showFashion('1');" value="1"/> &nbsp; 按类型选择花名册
			       </td>			       
		           </tr>
		         </table>
		        </td>
		        
	        </tr>
	        <tr>
	         <td class="RecordRowC common_border_color" nowrap>
		  <table width="100%" border="0" cellpadding="0" cellspacing="0">	
		   <tr>
		    <td nowrap>		      
		      <div id="b0110" style="width:100%;display:none;">
		      <br>
		      <fieldset align="center" style="width:60%;">
                        <legend >按单位显示</legend>
		         <table width="90%" border="0" cellpadding="0" cellspacing="0">
		           <tr>
		               <td width="20%" align="right">
		                
		               </td>
			      <td width="60%" id="table1" nowrap>
				&nbsp;	<bean:write name="cardConstantForm" property="mustidmess" filter="false"/>		
			      </td>
			      <td width="15%" style="padding-bottom:5px;">
			         <input type="button" name="b_1" class="mybutton" value="设置" onclick="setFashion('0','0','0','table1');">
			      </td>
		           </tr>
		         </table>
		         </fieldset>
		          <br>
		         </div>
		       </td>		        
		     </tr>  
		     <tr>
		      <td nowrap>
		      
		       <div id="wait" style="width=100%;display:none;">
		       <br>
		       <fieldset align="center" style="width:60%;">
                         <legend >按类型显示</legend>
		           <table width="90%" border="0" cellpadding="0" cellspacing="0">
		            <tr>
		              <td width="20%" align="right">
		              </td>
			      <td  width="60%" nowrap height="35" valign="middle">
			             
				<hrms:importgeneraldata showColumn="itemdesc" valueColumn="codesetid" flag="true" paraValue="" sql="select itemid,itemdesc from  fielditem where useflag=1 and fieldsetid='A01' and  codesetid<>'0' and itemid not in('B0100','E0122','E01A1')" collection="list" scope="page" />
				<html:select name="cardConstantForm" property="codename" size="1" onchange="strCodetype();">
					<html:option value="#">请选择...&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</html:option>
					<html:options collection="list" property="dataValue" labelProperty="dataName" />
				</html:select>				
			      </td>
			       <td width="15%">
			        &nbsp;		
			       </td>
		              </tr>
		              <tr>		             
		                 <td  width="25%" align="right" nowrap>
		                &nbsp;
		                </td>
		                <td nowrap>
		                  <table width="100%" border="0" cellpadding="0" cellspacing="0">
		                   <logic:iterate id="element" name="cardConstantForm"  property="codesetlist" indexId="index"> 
		                     <tr>
		                       <td height="28">
		                       &nbsp;<hrms:codetoname codeid="${element.dataName}" name="element" codevalue="dataValue" codeitem="codeitem" scope="page"/>  	      
                                         <bean:write name="codeitem" property="codename" />:&nbsp;  
		                        </td>
		                         <td id="${element.dataValue}">
		                          <%=(String)mustmesslist.get(i)%>
		                         </td>
		                         <td>		                     
		                           <input type="button" name="b_1" class="mybutton" value="设置" onclick='setFashion("1","<%=i%>","${element.dataName}","${element.dataValue}");'>
		                         </td>
		                       </tr>
		                     <%i++;
		                     %>
		                      </logic:iterate>
		                     </table>
		                   </td>
		                 </tr>
		               </table>
		               </fieldset>
		               <br>
		             </div>
		             <br>
		          </td>
		         </tr>
		     </table>
		   </td>
		</tr>
		
		<tr>
			<td align="center" colspan="2" class="RecordRow" nowrap style="height: 35px;">
			 <input type="button" name="btnreturn" value='上一步' class="mybutton" onclick="goback();">
				&nbsp;&nbsp;
				<%--linbz 20160908 <input type="submit" name="b_save" class="mybutton" value="&nbsp;确定&nbsp;">--%>
				<input type="button" name="b_save" class="mybutton" value="&nbsp;确定&nbsp;" onclick="save();"/>
			</td>
		</tr>
	</table>

</html:form>
<script language="javascript">
 showFashion("${cardConstantForm.mustflag}");	
 if(!getBrowseVersion() || getBrowseVersion()){//非ie兼容模式下 样式修改  wangb 20190318
 	var fieldset = document.getElementsByTagName('fieldset');
 	for(var i = 0 ; i < fieldset.length ;i++){
 		fieldset[i].style.margin ='0 auto';
 	}
 }
</script>