<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT> 
 <link href="/css/xtree.css" rel="stylesheet" type="text/css" >
<script language="JavaScript">
   	
   	function savecode()
   	{
       	   var currnode,codeitemid;
    	   currnode=Global.selectedItem;	
    	   if(currnode==null)
    	    	return;   
    	   codeitemid=currnode.uid;
    	   /*输入相关代码类和选择的相关代码一致*/      	 
       	   //alert(codeitemid.substring(0,2));
       	   if(codeitemid=='root'){
       	   	targethidden.value='';
       	   	targetobj.value='';
       	   }else{
    	   		targethidden.value=codeitemid.substring(2);
    	   		targetobj.value=currnode.text;
    	   }
    	   //alert(root.getSelected());	  	
    	   
   	}
   	
   </SCRIPT>
   <div class="fixedDiv3">
<html:form action="/selfservice/lawbase/law_into_base">
   <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">          
         <tr>
           <td align="left">         
            <div id="treemenu" style="height: 330px;overflow: auto;border-width:1px;width: 100%;" class="RecordRow"> 
             <SCRIPT LANGUAGE=javascript>     
                var codesetid,codevalue,name;
                var paraArray=dialogArguments; 
               var targetobj,targethidden;	
              //显示代码描述的对象
               targetobj=paraArray[0];
               
               //代码值对象
              targethidden=paraArray[1];   
              input_code_id=codesetid;            
               <bean:write name="lawbaseForm" property="tarTreeCode" filter="false"/>
             </SCRIPT>
             </div>             
           </td>
           </tr>  
            <tr>
          <td align="center" style="padding-top: 5px;" nowrap>
               <input type="button" name="btncance2" value="<bean:message key="button.ok"/>" class="mybutton" onclick="savecode();window.close();"> 
	       <input type="button" name="btncancel" value="<bean:message key="button.cancel"/>" class="mybutton" onclick="window.close();">  
          </td>
          </tr>         
    </table>
</html:form>
</div>