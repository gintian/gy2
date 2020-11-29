<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
  com.hjsj.hrms.actionform.sys.PrivForm form=(com.hjsj.hrms.actionform.sys.PrivForm)session.getAttribute("privForm");
  UserView userView=(UserView)session.getAttribute(WebConstant.userView);  
  String codeid="@K";//userView.getManagePrivCode();
  String codevalue=userView.getManagePrivCodeValue();
  if(userView.isSuper_admin()) 
  {
     codevalue="ALL";  
     codeid="@K";
  }
%>
<%
    String bosflag= userView.getBosflag();//得到系统的版本号
%>  
<link rel="stylesheet" href="/css/tabpane.css" type="text/css">
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<SCRIPT LANGUAGE=javascript src="/js/constant.js"></SCRIPT>
<script language="JavaScript">
	//this.status ="招聘管理 / 应聘人才库";
       
	function setCurrent(tab)
	{
		var nodes,currnode;
		currnode=document.getElementById("current");
		if(currnode==null)
		   return;
		currnode.id="";
		nodes=tab.parent;
		if(nodes==null)
		   return;
		nodes.id="current";
		
	}

      
      /*
      *树形菜单控制,功能菜单
      */
      function show(div_id)
      {
      	var oDiv;
      	oDiv=document.getElementById(div_id);
      	if(oDiv==null)
      	  return;
      	for(var i=0;i<oDiv.childNodes.length;i++)
      	{
      		if(oDiv.childNodes[i].tagName=='DIV')
      		{
      		   if(oDiv.childNodes[i].style.display=="none")
      		     oDiv.childNodes[i].style.display="block";
      		   else
      		     oDiv.childNodes[i].style.display="none";	  
      		}
      	}		
      }
            
      /**
      *组装表权限或字段权限串，最后通过document.baseOptionsForm.field_set_str
      *隐藏域传到后台．
      */
      function combinePrivString()
      {
      	var tablevos,thecontent,tmp,tablename,tabname;
      	thecontent="";
      	tabname=document.baseOptionsForm.current_tab.value;

      	if(tabname=="tablepriv")
      	{
	      	tablevos=document.getElementsByName("func");
	      	for(var i=0;i<tablevos.length;i++)
	      	{
	      		if(tablevos[i].type=="checkbox")
	      		{
	      		  tmp=tablevos[i].value;
	      		  if(tmp=="0")
	      		    continue;
	      		  if(!tablevos[i].checked)  
	      		    continue;
	      		  var hirechannel = document.getElementsByName("func"+i);
	      		  var hireStr="";
	      		  for(var j=0;j<hirechannel.length;j++)
	      		  {
	      		     var obj=hirechannel[j];
	      		     if(obj.checked)
	      		     {
	      		        hireStr+="`"+obj.value+"#1";
	      		     }
	      		     else
	      		     {
	      		        hireStr+="`-1#1";
	      		     }
	      		  }
	      		  tmp=tmp+"["+hireStr.substring(1)+"]";
	      		  tmp=tmp+",";
	      		  thecontent=thecontent+tmp;
	      		}
	      	}
      		thecontent=","+thecontent;
      		var fieldSetContent=thecontent.replace(/\,/g,"");///子集为空 会造成数组下标越界
      		if(fieldSetContent.length==0){
      			alert("请选择子集！");
      			return;
      		}
      		document.baseOptionsForm.field_set_str.value=thecontent; 
      	}
      	else if(tabname=="fieldpriv")
      	{
      		var constent="";
      		var constent_show="";
      		var constent_must="";
      		var constent_only=""
      		var field_vo=eval("document.baseOptionsForm.func");
      		var show_field_vo=eval("document.baseOptionsForm.func_show");
      		if(field_vo!=null)
      		{
      		
	      		var must_field_vo=eval("document.baseOptionsForm.func_must");
	      		var only_field_vo=eval("document.baseOptionsForm.func_onlys");
	      		for(var i=0;i<field_vo.length;i++)
	      		{
	      			if(show_field_vo[i].checked||must_field_vo[i].checked)
	      			{
	      				if(!field_vo[i].checked)
	      				{
	      					alert(MUST_AND_VISIBLE_FIELD_BE_IN_AVAILABLEIN+"!");
	      					return;
	      				}
	      			}
	      			if(only_field_vo[i])
	      			{
	      			if(only_field_vo[i].checked)
	      			{
	      			     if(!field_vo[i].checked||!show_field_vo[i].checked||!must_field_vo[i].checked)
	      			     {
	      			          alert(ONLY_MUST_AVAILABLE_VISIBLE_FIELD+"！");
	      			          return;
	      			     }
	      			}
	      			}
	      			if(only_field_vo[i])
	      			{
	      			if(only_field_vo[i].checked)
	      			{
	      			    constent_only=constent_only+only_field_vo[i].value+",";
	      			}
	      			}
	      			if(show_field_vo[i].checked)
	      			{	     				
	      				constent_show=constent_show+show_field_vo[i].value+',';
	      			}
	      			if(field_vo[i].checked)
	      			{    				
	      				constent=constent+field_vo[i].value+',';
	      			}
	      			if(must_field_vo[i].checked)
	      			{
	      				
	      				constent_must=constent_must+must_field_vo[i].value+',';
	      			}
	      		}
	      		var fieldContent=constent.replace(/\,/g,"");///指标不能为空
	      		if(fieldContent.length==0){
	      			alert("请选择有效指标！");
	      			return;
	      		}
	      		document.baseOptionsForm.field_set_str.value=constent; 
	      		document.baseOptionsForm.show_field_str.value=constent_show;
	      		document.baseOptionsForm.mustFill_field_str.value=constent_must;
	      		if(constent_only.length>0)
	      		constent_only=constent_only.substring(0,constent_only.length-1);
	      		document.baseOptionsForm.func_only.value=constent_only;
	      	}
      	}
      	
      	document.baseOptionsForm.action="/hire/zp_options/basesetfield.do?b_save=save";
	    document.baseOptionsForm.submit();
      	
      }   
    function initOnly()
    {
         var str="${baseOptionsForm.func_only}";
         if(str=='')
            return;
         var arr=str.split(",");
         var only_field_vo=eval("document.baseOptionsForm.func_onlys");
         for(var i=0;i<only_field_vo.length;i++)
         {
             for(var j=0;j<arr.length;j++)
             {
                 if(arr[j]==only_field_vo[i].value)
                 {
                      only_field_vo[i].checked=true;
                 }
             }
         }
    }
    function resetValue()
    {
      
          for(var i=0;i<document.forms[0].elements.length;i++)
          {
             if(document.forms[0].elements[i].type=='radio'||document.forms[0].elements[i].type=='checkbox')
             {
                  document.forms[0].elements[i].checked=false;
             }
          }
    } 
    function allSelect(obj)
    {
        var arr=document.getElementsByName("func");
        if(arr)
        {
           for(var i=0;i<arr.length;i++)
           {
              if(obj.checked)
                 arr[i].checked=true;
              else
                 arr[i].checked=false;
           }
        }
    }		
</script>
<html:form action="/hire/zp_options/basesetfield">
  <!--保存计算过的需要递交的子集或指标内容 -->
  <html:hidden name="baseOptionsForm" property="show_field_str"/>
  <html:hidden name="baseOptionsForm" property="mustFill_field_str"/>
  <html:hidden name="baseOptionsForm" property="func_only"/>
  <html:hidden name="baseOptionsForm" property="field_set_str"/>
  <html:hidden name="baseOptionsForm" property="current_tab"/>
  <html:hidden name="baseOptionsForm" property="org"/> 
  <%
    if(bosflag!=null&&!bosflag.equals("hcm")){
  %>
  <br>
  <%
  }
  %>
<div style="margin-top:10px;">
  <table width="80%"  border="0" cellpadding="1" cellspacing="1" class="framestyle" align="center">
      <tr>
      	<td>	
                	<bean:write  name="baseOptionsForm" property="script_str" filter="false"/>	 		 	      	  	 	      	    		
      	</td>
      </tr>
 </table> 
<table  width="80%" align="center">
          <tr>
            <td align="center" height="35px;">    	 		 	                            	 		 	        
        	
	 		<Input type='button' value='<bean:message key="button.save"/>'  onclick="combinePrivString()"  class="mybutton" /> 
	 	   <Input type='button' value='<bean:message key="button.clear"/>'  onclick="resetValue();"  class="mybutton" />  	       
            </td>
          </tr>          
</table>
</div>
<script language="JavaScript">
initOnly();
</script>
</html:form>
