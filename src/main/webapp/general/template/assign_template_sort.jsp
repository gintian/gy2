<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>



<style type="text/css">
.TopRow {
   height:22;
	BACKGROUND-COLOR: #f4f7f7; 
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	font-size: 12px;  
   font-weight: bold;	
	
}
</style>
<hrms:themes></hrms:themes>
<script language="javascript"> 
//用于鼠标触发的某一行
var curObjTr= null;
var oldObjTr_c= "";
function tr_onclick(objTr,bgcolor)
{
	if(curObjTr!=null)
		curObjTr.style.background=oldObjTr_c;
	curObjTr=objTr;
	oldObjTr_c=bgcolor;
	//curObjTr.style.background='219AF3';
	curObjTr.style.background='FFF8D2';
	//curObj.style.color='#ffdead'; 
}
function display1(Q_subtree,Q_img){
  
  if (Q_subtree.style.display=="none"){ 
       Q_subtree.style.display="";     
     Q_img.src="/ext/resources/images/default/tree/elbow-end-minus-nl.gif";
    }
  else
  {
       Q_subtree.style.display="none";      
       Q_img.src="/ext/resources/images/default/tree/elbow-end-plus-nl.gif";
  } 
}
function selAll(checkflag)
  {
      var len=document.resourceForm.elements.length;
      var i;
      for (i=0;i<len;i++)
      {
           if (document.resourceForm.elements[i].type=="checkbox")
            {
              document.resourceForm.elements[i].checked=checkflag;
            }
      }
} 
function allset(flag)
{
    var tablevos,tmp;
    tablevos=document.getElementsByTagName("INPUT");
    for(var i=0;i<tablevos.length;i++)
    {
      		if(tablevos[i].type=="radio")
      		{
      		  tmp=tablevos[i].value;
      		  if(flag=='make')
      		  {
                 if(tmp!=""&&(tmp.indexOf("R")==-1||tablevos[i].name==tmp))//权限指标名称后渲染到前台 制作列 为库内存储的值 使用列为 存储值+“R”  选中制作列name与value相等时是选中制作列 changxy
      		     {
      		       tablevos[i].checked=true;
      		     }
      		  }else if(flag=='use')
      		  {
      		     if(tmp.indexOf("R")!=-1)
      		     {
      		       tablevos[i].checked=true;
      		     }
      		  }else
      		  {
      		     if(tmp=="")
      		     {
      		        tablevos[i].checked=true;
      		     }
      		  }
      		  
      		}
   }      	
}
function save()
{
	var hashvo=new ParameterSet();
    hashvo.setValue("flag","<bean:write name="resourceForm" property="flag" />");
    hashvo.setValue("roleid","<bean:write name="resourceForm" property="roleid" />");	        
    hashvo.setValue("res_flag","<bean:write name="resourceForm" property="res_flag" />");
    var  tablevos=document.getElementsByTagName("INPUT");
    var str="";
    for(var i=0;i<tablevos.length;i++)
    {
      	if(tablevos[i].type=="radio"&&tablevos[i].checked==true)
      	{
      		    if(tablevos[i].value!="")
      		       str=str+tablevos[i].value+",";
      		    
      	}      		
   }   
   hashvo.setValue("law_dir",str);	 
   var request=new Request({asynchronous:false,onSuccess:save_ok,functionId:'10400201021'},hashvo);        
}
function save_ok(outparamters)
{
    var flag=outparamters.getValue("isCorrect");
    if(flag=="true")
       alert("保存成功！");
    else
       alert("保存失败！");
}
</script>
<html:form action="/general/template/assign_template_tree"> 
   <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" style="margin-top:-2px;">
			<tr align="left" height="30px;">
				<td valign="middle">
					
					<html:button styleClass="mybutton" property="b_save" onclick="save();"><bean:message key="button.save"/></html:button>
					<html:button styleClass="mybutton" property="b_all" onclick="allset('make');">全制作</html:button>
					<html:button styleClass="mybutton" property="b_clear" onclick="allset('use');">全使用</html:button>
				    <html:button styleClass="mybutton" property="b_clear" onclick="allset('no');">全无</html:button>  
				</td>
			</tr>			 	            
         <tr>
           <td align="left"> 
                <hrms:assigntemplatesort type="${resourceForm.type}" res_flag="${resourceForm.res_flag}" roleid="${resourceForm.roleid}" flag="${resourceForm.flag}"></hrms:assigntemplatesort>
           </td>
           </tr>           
    </table> 
</html:form>
