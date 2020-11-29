<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script type="text/javascript">
<!--
function addToTarget(source,targetSource,type)
{
    var sourceObj=document.getElementById(source);
    var targetObj = document.getElementById(targetSource);
    var num=0;
    var count=0;
    var flag=true;
    for(var i=0;i<sourceObj.options.length;i++)
    {
        flag=true;
        if(sourceObj.options[i].selected)
        {
            
              var arr=sourceObj.options[i].value.split("/");
              if(type==1&&arr[1]!='A')
              {
                  count++;
                  continue;
              }
            if(type==2&&arr[1]!='N')
              {
                  count++;
                  continue;
              }
             for(var j=0;j<targetObj.options.length;j++)
             {
                 if(targetObj.options[j].value==sourceObj.options[i].value)
                 {
                     num++;
                     flag=false;
                     break;
                 }
             }
             if(flag)
             {
                 var opt=new Option();
                 opt.value=sourceObj.options[i].value;
                 opt.text=sourceObj.options[i].text;
                 targetObj.add(opt);
             }
        }
    }
    if(count>0&&num>0)
    {
        if(type==1)
        {
           alert("系统自动过滤了重复选择的和非字符型的指标");
        }else{
           alert("系统自动过滤了重复选择的和非数值型的指标");
        }
        return;
    }else if(count>0)
    {
         if(type==1)
        {
           alert("系统自动过滤了非字符型的指标");
        }else{
           alert("系统自动过滤了非数值型的指标");
        }
        return;
    }
    else if(num>0)
    {
       
        alert("系统自动过滤了重复选择的指标");
        return;
    } 
}
function selectOK()
{
   var group=document.getElementById("rf2");
   var count=document.getElementById("rf");
   var groupField="";
   var countField="";
   for(var i=0;i<group.options.length;i++)
   {
      groupField+="`"+group.options[i].value+"/"+group.options[i].text;
   }
   for(var i=0;i<count.options.length;i++)
   {
      countField+="`"+count.options[i].value+"/"+count.options[i].text;
   }
   if(groupField=='')
   {
       alert("请选择分组指标！");
       return;
   }
   if(countField=='')
   {
      alert("请选择汇总指标！");
      return;
   }
   var hashVo=new ParameterSet();
   hashVo.setValue("groupField",getEncodeStr(groupField.substring(1)));
   hashVo.setValue("countField",getEncodeStr(countField.substring(1)));
   hashVo.setValue("where",getEncodeStr("${positionDemandForm.whl_sql}"));
   var request=new Request({method:'post',asynchronous:false,onSuccess:exportok,functionId:'3000000249'},hashVo);			
   
}
function exportok(outparameters)
{
   var fileName=outparameters.getValue("filename");
   var name=fileName.substring(0,fileName.length-1)+".xls";
   name = decode(name);
   var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+name);
}
//-->
</script>
<html:form action="/hire/demandPlan/positionDemand/select_group_field">
<table width='97%' border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
           <tr>
            <td align="left" class="TableRow" nowrap>
		<bean:message key="label.query.selectfield"/>&nbsp;&nbsp;
            </td>            	        	        	        
           </tr>
   	  </thead>
   	   <tr>
            <td width="100%" align="center" class="RecordRow" nowrap>
              <table width="100%">
                <tr>
                 <td align="center"  width="46%">
                   <table align="center" width="100%">
                    <tr>
                      <td align="left" width="90%">
                         &nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="selfservice.query.queryfield"/>
                      </td>
                    </tr>
                   <tr>
                       <td align="center">
                         <hrms:optioncollection name="positionDemandForm" property="groupFieldList" collection="list"/>
		              <html:select name="positionDemandForm" size="25" property="groupFieldId" multiple="multiple" style="height:360px;width:90%;font-size:9pt">
		              <html:options collection="list" property="dataValue" labelProperty="dataName"/>
		        </html:select>		
                       </td>
                    </tr>
                   </table>
                </td>   
                    
                <td align="center">
                <table align="center" width="100%">
                <tr height="200">
                 <td width="8%" align="center" valign="top">
                   <html:button  styleClass="mybutton" property="b_addfield" onclick="addToTarget('groupFieldId','right_fields2',1);">
            		     <bean:message key="button.setfield.addfield"/> 
	           </html:button >
	           <br>
	            <html:button  styleClass="mybutton" property="b_delfield" onclick="removeitem('right_fields2');">
            		     <bean:message key="button.setfield.delfield"/>    
	           </html:button >	 
	           </td>
                </tr>
                 <tr>
                 <td width="8%" align="center" valign="bottom">
                   <html:button  styleClass="mybutton" property="b_addfield" onclick="addToTarget('groupFieldId','right_fields',2);">
            		     <bean:message key="button.setfield.addfield"/> 
	           </html:button >
	           <br>
	           <html:button  styleClass="mybutton" property="b_delfield" onclick="removeitem('right_fields');">
            		     <bean:message key="button.setfield.delfield"/>    
	           </html:button >	   
	           </td>
                </tr>
                </table>
                </td>
                 <!--  button -->  
                <td width="46%" align="left">
                 <table width="100%">
                  <tr>
                  <td width="100%" align="left">
                     分组指标
                  </td>
                  </tr>
                  <tr>          
                  <td width="100%" align="left" valign="top">
 		     
 		       
		              <select  size="7" id='rf2' name="right_fields2" multiple="multiple" ondblclick="removeitem('right_fields2');" style="height:160px;width:90%;font-size:9pt">
		              
		              </select>	
 		    
  </td>
  
                  </tr>
                  </table>   
                   <table width="100%">
                  <tr>
                  <td width="100%" align="left">
                     汇总指标
                  </td>
                  </tr>
                  <tr>
                  <td width="100%" align="left" valign="bottom">
 		     
 		       
		              <select  size="7" id='rf' name="right_fields" multiple="multiple" ondblclick="removeitem('right_fields');" style="height:160px;width:90%;font-size:9pt">
		              
		              </select>	
 		    
  </td>
                  </tr>
                  </table>            
                </td>
                                 
                </tr>
              </table>             
            </td>
            </tr>
          <tr>
          <td align="center" class="RecordRow" nowrap>
              <html:button styleClass="mybutton" property="b_next" onclick="selectOK();">
            		      <bean:message key="reporttypelist.confirm"/>
	      </html:button> 	
	      &nbsp;
	      <html:button styleClass="mybutton" property="b_close" onclick="window.close();">
            		      <bean:message key="button.close"/>
	      </html:button> 	       
          </td>
          </tr>   
</table>
</html:form>