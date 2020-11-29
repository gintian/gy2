<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.gz.voucher.VoucherForm" %>
<%@ page import="com.hrms.struts.taglib.CommonData"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean" %>
<%@ page import="java.lang.*"%>
<link href="../../css/css1.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" type="text/css" href="../../ajax/skin.css"></link>
<hrms:themes />
<script language="JavaScript" src="/js/validate.js"></script>
<%
	VoucherForm voucherForm=(VoucherForm)session.getAttribute("financial_voucherForm");
	ArrayList maintainList = voucherForm.getMaintainList();
	int i=0;
	LazyDynaBean JounalBean = new LazyDynaBean();
	CommonData temp  = new CommonData();
	String itemid="";
%>
<SCRIPT LANGUAGE="JavaScript">
function showSelectBox(srcobj,num){
   		var pos=getAbsPosition(srcobj);
   		var currentId=srcobj.id;
   		var visSelect=document.getElementById(currentId+num);
   		var SelectWidth=1;
   		for(var i=0;i<visSelect.length;i++){
   		    var l=0;
   		    var option =visSelect[i].innerHTML;
   		    var lastindex=option.lastIndexOf("　");
   		    if(option!=""){
   		       var a=option.split("");
   		       for(var j=0;j<a.length;j++){
   		          if (a[j].charCodeAt(0)<299) {
                       l++; 
                  } else { 
                    l+=2; 
                  }  
   		       }
   		    }
   		    if(lastindex>0){
   		       l=l+lastindex;
   		    }
   		    l=l*6.5;
   		    if(l>SelectWidth){
   		       SelectWidth=l;
   		    }
   		}
   		var currentWidth=srcobj.offsetWidth;
   		/**确定div显示的位置**/
   		document.getElementById(currentId+"value").style.position='absolute';
   		document.getElementById(currentId+"value").style.posLeft=pos[0]-1;
 		document.getElementById(currentId+"value").style.posTop=pos[1]-1+srcobj.offsetHeight;
 		/**确定div的长度**/
		if(SelectWidth<currentWidth){//如果select的长度小于文本框的长度，那么默认要和文本框的长度相同 
		  document.getElementById(currentId+num).style.width=currentWidth;
		  document.getElementById(currentId+"value").style.width=currentWidth;
		}else{
		  document.getElementById(currentId+num).style.width=SelectWidth;
		  document.getElementById(currentId+"value").style.width=SelectWidth;
		}
		document.getElementById(currentId+"value").style.display="block";          
} 

function removeSlectBox(srcobj,num){
	var act = document.activeElement.id;
	var currentID=srcobj.id;
	var divID=currentID+num;
	if(act==currentID||act==divID){
	   document.getElementById(currentID+"value").style.display="block";
	}else{
	   document.getElementById(currentID+"value").style.display="none";
	}
}
function checkc_subjectsave(objectvalue,num){
	var c_subject=document.getElementById("c_subjecthidden").value;//之所以特殊取值，是因为按月汇总科目取值要开头必须是数字开头 
	if(!c_subject){
		alert("科目不能为空！");
    	return false;
	}

	var interface_type=document.getElementById("interface_type").value;
	var pn_id=document.getElementById("pn_id").value;
	var temp=document.getElementById("c_mark");
	if(temp){
		var c_mark=document.getElementById("c_mark").value;
	}
//	var c_mark=document.getElementById("c_mark").value;//之所以特殊拿出来,是因为c_mark长度有限制;
    var temp1=document.getElementById("fl_name");
   
    if(temp1){
    	var fl_name=document.getElementById("fl_name").value;
    	 if(null==fl_name||fl_name.length==0){
    	    	alert("分录名称不能为空！");
    	    	return false;
    	    }
    }
//	var fl_name=document.getElementById("fl_name").value;//之所以特殊拿出来,是因为fl_name长度有限制;
	var fl_id = document.getElementById("fl_id").value;
	var flag = document.getElementById("flag").value;
	if(temp&&c_mark!=""){
	    var l=0;
       var a=c_mark.split(""); 
       for (var i=0;i<a.length;i++){ 
           if (a[i].charCodeAt(0)<299) {
               l++; 
           } else { 
              l+=2; 
           } 
       } 
       if(l>200){
           alert("摘要名称过长,请限制在200个字符以内");
           return false;
       }
	}
	if(temp1&&fl_name!=""){//区别汉字和英文字符所占字符不同的判断 xcs modify@2013-9-22
	   var l=0;
	   var a=fl_name.split(""); 
	   for (var i=0;i<a.length;i++){ 
	       if (a[i].charCodeAt(0)<299) {
	           l++; 
	       } else { 
	          l+=2; 
	       } 
	   } 
	   if(l>30){
	       alert("分录名称过长，请限制在30个字符以内");
	       return false;
	   }
	}
	var itemid = objectvalue;
	var itemArray = itemid.split(",");
	var itemvalue="";
	itemid="";
	if("1"==interface_type){
	    var hashvo=new ParameterSet();
	    hashvo.setValue("pn_id",pn_id);
	    hashvo.setValue("fl_id",fl_id);
	    hashvo.setValue("interface_type",interface_type);
	    for(var k=0;k<itemArray.length;k++){
	       var filename =itemArray[k];
	       var at = filename.indexOf("&");
	       var filevalue="";
	       if(at!=-1&&at==(filename.length-1)){
	         filename=filename.substr(0,filename.length-1);
	         filevalue=document.getElementById(filename+"hidden").value;
	       }else{
	         filevalue=document.getElementById(itemArray[k]).value;
	       }
	       if(filevalue==""){
	           filevalue=" ";
	       }
	       itemvalue=itemvalue+filevalue+",";
	       itemid=itemid+filename+",";
	    }
	    itemid=itemid.substr(0,itemid.length-1);
	    hashvo.setValue("itemid",itemid);
	    hashvo.setValue("itemvalue",itemvalue);
	    hashvo.setValue("flag",flag);
	    if(num==1){
	       var request=new Request({method:'post',asynchronous:false,parameters:null,onSuccess:saveOk,functionId:'3020073012'},hashvo);
	    }else{
	       var request=new Request({method:'post',asynchronous:false,parameters:null,onSuccess:savereturnOk,functionId:'3020073012'},hashvo);
	    }
	    
		//financial_voucherForm.action="/gz/voucher/addUpdteJournal.do?b_save=link&pn_id="+pn_id+"&c_mark="+c_mark+"&c_subject="+c_subject+"&fl_name="+fl_name;
		//financial_voucherForm.submit();
	}
	if("2"==interface_type){
	   if(c_subject==""){
			var hashvo=new ParameterSet();
	        hashvo.setValue("pn_id",pn_id);
	        hashvo.setValue("fl_id",fl_id);
	        hashvo.setValue("interface_type",interface_type);
	        for(var k=0;k<itemArray.length;k++){
	           var filename =itemArray[k];
	           var at = filename.indexOf("&");
	           var filevalue="";
	           if(at!=-1&&at==(filename.length-1)){
	             filename=filename.substr(0,filename.length-1);
	             filevalue=document.getElementById(filename+"hidden").value;
	           }else{
	             filevalue=document.getElementById(itemArray[k]).value;
	           }
	           if(filevalue==""){
                filevalue=" ";
               }
	           itemvalue=itemvalue+filevalue+",";
	           itemid=itemid+filename+",";
	        }
	        itemid=itemid.substr(0,itemid.length-1);
	        hashvo.setValue("itemid",itemid);
	        hashvo.setValue("itemvalue",itemvalue);
	        hashvo.setValue("flag",flag);
	        if(num==1){
                var request=new Request({method:'post',asynchronous:false,parameters:null,onSuccess:saveOk,functionId:'3020073012'},hashvo);
            }else{
                var request=new Request({method:'post',asynchronous:false,parameters:null,onSuccess:savereturnOk,functionId:'3020073012'},hashvo);
            }
		}else{
			var fristchar=c_subject.charAt(0);//xiegh 20170621 bug: 28859
			/* if((fristchar>='a'&&fristchar<='z')||(fristchar>='A'&&fristchar<='Z')){ */
				var hashvo=new ParameterSet();
	            hashvo.setValue("pn_id",pn_id);
	            hashvo.setValue("fl_id",fl_id);
	            hashvo.setValue("interface_type",interface_type);
	            for(var k=0;k<itemArray.length;k++){
	               var filename =itemArray[k];
	               var at = filename.indexOf("&");
	               var filevalue="";
	               if(at!=-1&&at==(filename.length-1)){
	                 filename=filename.substr(0,filename.length-1);
	                 filevalue=document.getElementById(filename+"hidden").value;
	               }else{
	                 filevalue=document.getElementById(itemArray[k]).value;
	               }
	               itemvalue=itemvalue+filevalue+",";
	               itemid=itemid+filename+",";
	            }
	            itemid=itemid.substr(0,itemid.length-1);
	            hashvo.setValue("itemid",itemid);
	            hashvo.setValue("itemvalue",itemvalue);
	            hashvo.setValue("flag",flag);
	           if(num==1){
                   var request=new Request({method:'post',asynchronous:false,parameters:null,onSuccess:saveOk,functionId:'3020073012'},hashvo);
               }else{
                   var request=new Request({method:'post',asynchronous:false,parameters:null,onSuccess:savereturnOk,functionId:'3020073012'},hashvo);
               }
		/* 	}else{
				alert(CHANGE_SELECT_IS_NOTEXPECT);
				return false;
			} */
		}	
	}
}
function saveOk (outparameters){
    var pn_id=outparameters.getValue("pn_id");
    var interface_type=outparameters.getValue("interface_type");
    var btnsaveobj = document.getElementsByName("btnsave")[0];//add by xiegh bug36515
    if(btnsaveobj)
    	btnsaveobj.disabled = true;
    financial_voucherForm.action="/gz/voucher/searchvoucherdate.do?b_query=link&pn_id="+pn_id+"&interface_type="+interface_type+"&showflag=2";
    financial_voucherForm.submit();
}
function savereturnOk(outparameters){
    var btnsaveobj = document.getElementsByName("btnsavereturn")[0];//add by xiegh bug36515
    if(btnsaveobj)
    	btnsaveobj.disabled = true;
    var pn_id=outparameters.getValue("pn_id");
    var flag=outparameters.getValue("flag");
    financial_voucherForm.action="/gz/voucher/searchvoucherdate.do?b_add=link&pn_id="+pn_id+"&flag="+flag;
    financial_voucherForm.submit();
}
function getC_mark(objSelect,num){
        var mm=objSelect.selectedIndex;
        var SelectId=objSelect.id;
        var SelectIdLength=SelectId.length;
        SelectId=SelectId.substr(0,SelectIdLength-1);
        if(mm==-1){
           document.getElementById(SelectId).focus();
           return false;
        }
		var textValue=objSelect.options[objSelect.selectedIndex].text;
		textValue=trim(textValue);
		var codeValue=objSelect.options[objSelect.selectedIndex].value;
		document.getElementById(SelectId+"value").style.display="none";
		var item = document.getElementById(SelectId);
		var code =document.getElementById(SelectId+"hidden");
		code.value=codeValue;
		item.value=textValue;
		item.blur();
}
function otherFoucs(objectdiv,num){
    var divId = objectdiv.id;
    var divIdLength=divId.length;
    divId=divId.substr(0,divIdLength-5);
    var act = document.activeElement.id;//当前激活的事件的ID;
	if(document.activeElement.id!=""){
		document.getElementById(divId).focus();
	}else{
	   document.getElementById(objectdiv.id).style.display="none";
	}
}
function trim(str){//去掉字符串前的空格  
    var index=str.lastIndexOf("　");
    index = index+1;
    str=str.substring(index,str.length);
    return str;
}
function removevalue(obj){
    obj.value="";
}  
</SCRIPT>
<body>
<html:form action="/gz/voucher/addUpdteJournal">

		<html:hidden  property="pn_id" styleId='pn_id'/>
		<html:hidden  property="fl_id" styleId='fl_id'/>
		<html:hidden  property="interface_type" styleId='interface_type'/>
		<html:hidden  property="flag" styleId='flag'/>
		<table width="700px"  align="center" border="0" cellpadding="0" cellspacing="0" style="margin-top:60px;">
		<tr>  
          <td>
          <fieldset align="center" style="width:60%;"><legend ><bean:message key="gz.voucher.journal"/></legend>
          <br/>
		<table border="0"   align="center" cellpadding="0" height="80%">
          <logic:iterate id="element"    name="financial_voucherForm"  property="maintainList" indexId="index">
		  <tr>
		      <%
		          JounalBean=(LazyDynaBean)maintainList.get(i);
		           
		      %>
		      <td align="right"><bean:write name="element" property="desc" filter="true"/></td>
		      <%
		          String filedtype=(String)JounalBean.get("filedtype");
		          String codesetid=(String)JounalBean.get("codesetid");
		          String filedid=(String)JounalBean.get("filedid");
		          if("A".equalsIgnoreCase(filedtype)){//处理字符型数据
		              if("0".equalsIgnoreCase(codesetid)){//处理字符型数据且不是代码类的数据处理  
		                  if("c_mark".equalsIgnoreCase(filedid)){//摘要特殊处理 
		       %>
		           <td align="left" class="t_cell_data">
                     &nbsp;
                     <input type="text" id="<bean:write name='element' property='filedid' filter='true'/>"  value="<bean:write name='element' property='textvalue' filter='true'/>" onclick="showSelectBox(this,<%=i %>)" onblur="removeSlectBox(this,<%=i %>)" size="30">
                     <input type="hidden" id="<%=filedid%>hidden" value="<bean:write name='element' property='textvalue' filter='true'/>">
                     <div id="c_markvalue" style="display:none;" onclick="otherFoucs(this,<%=i %>)">
                           <%
                               ArrayList tempList = (ArrayList)JounalBean.get("list");
                               itemid=itemid+filedid+",";
                           %>
                                <select id="<%=filedid+i%>" ondblclick="getC_mark(this,<%=i %>)" size="5" width="100%">
                           <%
                               for(int n=0;n<tempList.size();n++){
                                    String dataName=(String)tempList.get(n);
                           %>
                                   <option value="<%=dataName%>"><%=dataName%></option>
                           <%
                               }
                           %>
                               </select>
                     </div>
                   </td>
		       <%
		                  }else if("n_loan".equalsIgnoreCase(filedid)){//借贷方向特殊处理 
		        %>
		            <td align="left" class="t_cell_data">
                     &nbsp;
                           <%
                               ArrayList tempList = (ArrayList)JounalBean.get("list");
                               itemid=itemid+filedid+",";
                           %>
                           <input type="text"  id="<bean:write name='element' property='filedid' filter='true'/>"  value="<bean:write name='element' property='textvalue' filter='true'/>" onfocus="showSelectBox(this,<%=i %>)" onblur="removeSlectBox(this,<%=i %>)" size="30" readonly="readonly">
                           <input type="hidden"  id="<%=filedid%>hidden" value="<bean:write name='element' property='textvalue' filter='true'/>">
                            <div id="n_loanvalue" style="display:none;" onclick="otherFoucs(this,<%=i %>)">
                                <select id="<%=filedid+i%>" ondblclick="getC_mark(this,<%=i %>)" size="5">
                           <%
                               for(int n=0;n<tempList.size();n++){
                                 String dataName=(String)tempList.get(n);
                                 String textvalue=(String)JounalBean.get("textvalue");
                           %>
                                   <option value="<%=dataName%>"><%=dataName%></option>
                           <%
                               }
                           %>
                               </select>
                     </div>
                   </td>      
		        <%              
		                  }else if("c_subject".equalsIgnoreCase(filedid)){//科目的处理
		        %>
                  <td align="left" class="t_cell_data">
                         &nbsp;
                        <%
                            ArrayList tempList = (ArrayList)JounalBean.get("list");
                            String textvalue=(String)JounalBean.get("textvalue");
                            itemid=itemid+filedid+"&,";
                        %>
                             <input type="text" id="<bean:write name='element' property='filedid' filter='true'/>"  value="<bean:write name='element' property='textvalue' filter='true'/>" onfocus="showSelectBox(this,<%=i %>)" onblur="removeSlectBox(this,<%=i %>)" onkeyup="removevalue(this)" size="30">
                             <input type="hidden" id="<%=filedid%>hidden" value="<bean:write name='element' property='realvalue' filter='true'/>">
                             <div id="<%=filedid%>value" style="display:none;" onclick="otherFoucs(this,<%=i %>)">
                             <select id="<%=filedid+i%>" ondblclick="getC_mark(this,<%=i %>)" size="5">
                        <%
                            for(int n=0;n<tempList.size();n++){
                              temp=(CommonData)tempList.get(n);
                              String dataName=temp.getDataName();
                              String dataValue=temp.getDataValue();
                        %>
                               <option value="<%=dataValue%>" ><%=dataName%></option>
                        <%
                              }
                        %>
                           </select>
                     </div>
                 </td>   
		        <%   
		                  }else{
		                  itemid=itemid+filedid+",";
		        %>        <td align="left" class="t_cell_data">&nbsp;
		                 <input type="text" id="<bean:write name='element' property='filedid' filter='true'/>"  value="<bean:write name='element' property='textvalue' filter='true'/>" size="30">
		                 </td> 
		        <%
		                  }
		              }else{//代码类数据的处理 
		        %>
		              <td align="left" class="t_cell_data">
                         &nbsp;
                       <%
                            ArrayList tempList = (ArrayList)JounalBean.get("list");
                            String textvalue=(String)JounalBean.get("textvalue");
                            itemid=itemid+filedid+"&,";
                        %>
                        <input type="text" name="<%=filedid%>.viewvalue"  id="<bean:write name='element' property='filedid' filter='true'/>"  value="<bean:write name='element' property='textvalue' filter='true'/>" size="30" readonly >
                        <input type="hidden" name="<%=filedid%>.value" id="<%=filedid%>hidden" value="<bean:write name='element' property='realvalue' filter='true'/>">
                        <img src="/images/code.gif" onclick='openInputCodeDialog("<%=codesetid %>","<%=filedid%>.viewvalue");'/>
                    </td>   
		        <%
		              } i=i+1;
		          }else{//非字符型数据的处理
		               itemid=itemid+filedid+",";
		        %>
		          <td align="left" class="t_cell_data">
                     &nbsp;
                     <input type="text" id="<bean:write name='element' property='filedid' filter='true'/>"  value="<bean:write name='element' property='textvalue' filter='true'/>" size="30">
                  </td>
		        <%
		           i=i+1;
		          }
		         
		      %>  
		  </tr>
		 </logic:iterate>
		 <%
		  itemid=itemid.substring(0,itemid.length()-1);
		  %>
		</table>
		<br><br>
		</fieldset>
		</td>
        </tr>
        </table>

		<table  width="700px;" align="center">
	          <tr>
	            <td align="center">
		            	<input type="button" name="btnsave"
							value='<bean:message key="button.save"/>' class="mybutton"
							onclick="checkc_subjectsave('<%=itemid %>','1')">
				<logic:equal name="financial_voucherForm" property="flag" value="1">
					<input type="button" name="btnsavereturn"
							value='<bean:message key="button.savereturn"/>' class="mybutton"
							onclick="checkc_subjectsave('<%=itemid %>','2')">
				</logic:equal>
		 	<hrms:submit styleClass="mybutton" property="b_back">
	            		<bean:message key="button.return"/>
		 	</hrms:submit>
	            </td>
	          </tr>          
		</table>
</html:form>
  <script type="text/javascript">
  <%String privflag = (String)request.getParameter("privflag");%>
  	<%if("3".equals(privflag)){%>
		document.getElementsByName("btnsave")[0].disabled=true;
		if(document.getElementsByName("btnsavereturn")[0]){
			document.getElementsByName("btnsavereturn")[0].disabled=true;
		}
	<%}%>
var aa=document.getElementsByTagName("input");
for(var i=0;i<aa.length;i++){
	if(aa[i].type=="text"){
		aa[i].className="inputtext";
	}
}
</script>  
</body>
