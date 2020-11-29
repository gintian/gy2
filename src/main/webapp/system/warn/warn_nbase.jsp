<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.businessobject.sys.warn.ContextTools"%>

<hrms:themes></hrms:themes>
<style>
<!--
.TableRow_lrt {
	background-position : center left;
	font-size: 12px;  
	BORDER-BOTTOM: #C4D8EE 1pt solid;
	BORDER-LEFT: #C4D8EE 0pt  solid; 
	BORDER-RIGHT: #C4D8EE 0pt solid; 
	BORDER-TOP: #C4D8EE 1pt solid;
	height:29px;
	font-weight: bold;
	/*
	color:#336699;
	*/
	valign:middle;
	padding:0 5px 0 5px;
}
-->
</style>
<script language="javascript">
//xus 18/3/7 关闭窗口方法
function closeWin(){
	if (!window.showModalDialog) {
		parent.Ext.getCmp("setnbaseWin").close();
	}else{
		window.close(); 
	}
}
 function setnbase()
 {
     var mes=$F('messi');    
     if(mes==null||mes.length<=0)
     {
        alert("请设置人员库！");     
        return false;
     }else
     {
        var nbases="";
        var names="";   
        if(isArray(mes))
        {
           for(var i=0;i<mes.length;i++)
           {
          
              var value=mes[i];        
              var values=value.split("`");           
              nbases=nbases+values[0]+",";
              names=names+values[1]+",";
           }  
        }else
        {
            var values=mes.split("`");           
            nbases=nbases+values[0]+",";
            names=names+values[1]+",";
        }  
        if(names.length>0) 
        names=names.substring(0,names.length-1);
        var thevo=new Object();
        thevo.flag="true";
        thevo.nbases=nbases;
        thevo.names=names;
        //xus 18/3/6 解决chrome浏览器无法得到window.showModalDialog返回值的问题 
        if (!window.showModalDialog) 
        	 parent.setnbaseByChildwin(thevo);
        else
        	window.returnValue=thevo;
        //window.close();
        closeWin();
     }     
     
 }
function isArray(obj) 
{ 
      return (obj.constructor.toString().indexOf('Array')!= -1);
} 	
	
</script>

<html:form action="/system/warn/config_maintenance">
<div class="fixedDiv2" style="border-top:none;height:200px;">
      <table  width="100%"  border="0" cellpmoding="0" cellspacing="0"  class=""  cellpadding="0" valign="top" align="center">   
      <THEAD>
      <tr class="fixedHeaderTr">
            <td align="left" class="TableRow_lrt" nowrap>
		人员库
            </td>            	        	        	        
      </tr>
      </THEAD>              	
		 <tr>
		  <td width="100%" height="30" >
		    <table style="margin-left: 10px">
		      <logic:iterate id="element" name="warnConfigForm"  property="dblist" indexId="index"> 
                       <tr>
                        <td align="left" nowrap class="tdFontcolor" colspan="4">
                          <logic:equal name="warnConfigForm" property='<%="perlist["+index+"]"%>' value="1">
                           <input type="checkbox" name="messi" value="${element.dataValue}" checked="true"><bean:write name="element" property="dataName"/>
                          </logic:equal>    
                          <logic:notEqual name="warnConfigForm" property='<%="perlist["+index+"]"%>' value="1">
                	       <input type="checkbox" name="messi" value="${element.dataValue}"><bean:write name="element" property="dataName"/>
                          </logic:notEqual>   
                                                                      	      	
                        </td>
                       </tr>
                      </logic:iterate>           	
	        </table>		         
	     </td>
         </tr>   
         </table>
         </div>
         <table valign="top" align="center">      
         <tr>
            <td align="center" height="35" nowrap>
		         <input type="button" name="savebutton" value="确定" class="mybutton" onclick='setnbase();'>
		         <input type="button" name="savebutton" value="关闭" class="mybutton" onclick='closeWin();'>
            </td>            	        	        	        
      </tr>
        </table>
</html:form>

<script language="javascript">

</script>
