<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="javascript" src="/js/dict.js"></script> 
<script language="javascript">
	function getSelectedData(setname)
	{
        var tablename,table,dataset;
        var objlist=new Array();
        tablename="table"+setname;
        table=$(tablename);
        if(table.length==0)
        	return;
        dataset=table.getDataset();
  	   var params=window.dialogArguments;
  	   var flag=params[0];
	   var record=dataset.getFirstRecord();	
	   while (record) 
	   {
		if (record.getValue("select"))
		{		
		 var thevo=new Object();
		 thevo.role_id=record.getValue("role_id")
		 thevo.role_name=record.getValue("role_name")
	     objlist.push(thevo);
	     if(flag=="1")
		     break;	     
		}		
		record=record.getNextRecord();
	   }       
	   returnValue=objlist;
	   window.close();		
	}
	function getSelecteData1(){
	   var objlist=new Array();
	   var len=document.getElementsByTagName("input");	   
	   var flag="0";
	   var params=window.dialogArguments;
	   if(params!=null)
	   	flag=params[0];
	   var ss=0;
       for (var i=0;i<len.length;i++)
       {
           if (len[i].type=="checkbox"&&len[i].name!="selall")
           {
              if(len[i].checked==true)
              {
              	var tmp = len[i].value.split(",");
              	var thevo=new Object();
              	thevo.role_id=tmp[0];
				thevo.role_name=tmp[1];
			    objlist.push(thevo);
			    ss=1;
                if(flag=="1")
		    	  break;	    
              }
           }
       }
       if(ss==0)
       {
          alert("请选择角色！");
          return;
       }
       //returnValue=objlist;
	   templateOthForm.action="/general/template/select_role_dialog.do?b_return=link";
	   templateOthForm.submit();			
	}
	function selectAll(obj){
		var len=document.getElementsByTagName("input");
		for (var i=0;i<len.length;i++)
        {
           if (len[i].type=="checkbox"&&len[i].name!="selall")
           {
           	  len[i].checked=obj.checked;
           }
        }
	}
	function selectOne(obj){
		/*
		var len=document.getElementsByTagName("input");
		for (var i=0;i<len.length;i++)
        {
           if (len[i].type=="checkbox"&&len[i].name!=obj.name)
           {
           	  len[i].checked=false;
           }
        }*/
	}
</script>

<html:form action="/general/template/select_role_dialog">
<table width="100%" cellpadding="0" cellspacing="0" border="0" align="center" style="border-collapse: collapse;">
  <tr>
    <td>
       <table class="ListTable" width="510">
          <thead>
			<tr>
				<td align="center" class="TableRow" style="width:25px;">
					<!--<input type="checkbox" name="selall" id="selall" onclick="batch_select(this,'roleListForm.select');"/>&nbsp; bug 39426 重新指派不应该能全选角色-->
				</td>
				<td align="center" class="TableRow" style="width:170px;">
					&nbsp;名称&nbsp;
				</td>
				<td align="center" class="TableRow" style="">
					&nbsp;描述&nbsp;
				</td>
			</tr>
		</thead>
       </table>
    </td>
  </tr>
<tr><td valign="top" align="left" style="height: 300px;">
<div style="height: 300px;width:510px;overflow-y:auto;position: absolute;border-right:1px solid #C4D8EE;border-bottom:1px solid #C4D8EE;border-left:1px solid #C4D8EE;" class="common_border_color">
<table width="100%" border="0" cellspacing="0" align="center"
		cellpadding="0" class="ListTableF" style="margin-top:-1px;border-left:none;border-right:none;">
		
		<hrms:extenditerate id="element" name="templateOthForm" property="roleListForm.list" indexes="indexes"  pagination="roleListForm.pagination" pageCount="${templateOthForm.pagerows}" scope="session">
          <tr class="trShallow">
		   <td align="center" class="RecordRow"  style="width:25px;border-left:none;">		
     		  <hrms:checkmultibox name="templateOthForm" property="roleListForm.select" value="true" indexes="indexes" onclick="selectOne(this);" />&nbsp;             
		   </td>
		   <td align="left" class="RecordRow"  style="width:170px;">
			 &nbsp;<bean:write name="element" property="string(role_name)" filter="true"/>
		   </td>
		   <td align="left" style="border-right:none;" class="RecordRow"  >
			&nbsp;<bean:write  name="element" property="string(role_desc)" filter="false"/>
		   </td>
	      </tr>
       </hrms:extenditerate>
       
</table>
</div>
</td></tr>
<tr><td align="left">
<table width="510"  align="left" class="RecordRowP">
		<tr>
		    <td valign="middle" class="tdFontcolor">
		            <hrms:paginationtag name="templateOthForm"
								pagerows="${templateOthForm.pagerows}" property="roleListForm.pagination"
								scope="session" refresh="true"></hrms:paginationtag>
			</td>
	        <td valign="middle" align="right" nowrap class="tdFontcolor" >
		          <p align="right"><hrms:paginationlink name="templateOthForm" property="roleListForm.pagination"
				nameId="roleListForm" propertyId="roleListProperty">
				</hrms:paginationlink></p>
			</td>
		</tr>
</table>
</td></tr>
<tr><td align="left">
<table width="510" align="center">
          <tr>
            <td align="center" height="35px;">
            	<html:button styleClass="mybutton" property="b_save" onclick="getSelecteData1();">
            		<bean:message key="button.ok"/>
	 	    	</html:button>
         		<html:button styleClass="mybutton" property="br_return" onclick="parent.window.close();">
            		<bean:message key="button.close"/>
	 	    	</html:button>	 	
            </td>
          </tr>          
</table>

</td></tr>
</table>
</html:form>
		   