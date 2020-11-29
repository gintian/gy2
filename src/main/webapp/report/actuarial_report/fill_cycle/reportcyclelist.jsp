<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.frame.utility.AdminCode" %>
<script language="javascript">
 
		function del()
{
		var num=0;
		for(var i=0;i<document.reportCycleForm.elements.length;i++)
  		{
  			if(document.reportCycleForm.elements[i].type=='checkbox'&&document.reportCycleForm.elements[i].name!='selbox')
  			{
  				if(document.reportCycleForm.elements[i].checked==true)
  				{
  				
  					num++;
  				}
  			}
  		}
  		if(num==0)
  		{
  			alert("请选择需要删除的周期！");
  		    return;
  		}
		
		if(confirm("你确定要删除选中的填报周期吗?\n填报周期所有相关的内容都将删除!"))
		{
			document.reportCycleForm.action="/report/actuarial_report/fill_cycle.do?b_del=del";
			document.reportCycleForm.submit();
		}else{
		return;
		}
		
}
function setup(id){
var waitInfo=eval("wait");	   
 waitInfo.style.display="block";
  var pars="reportcycleid="+id;  
     var request=new Request({method:'post',asynchronous:false,parameters:pars,
                              onSuccess:setupvalidate,functionId:'03060000110'});

}
function setupvalidate(outparamters){
 var flag=outparamters.getValue("flag");
 var reportcycleid=outparamters.getValue("reportcycleid");
 if(flag=="true"){
 	var waitInfo=eval("wait");	
	waitInfo.style.display="none";
 alert("仅允许一个填报表周期处于发布状态");
 return;
 }else{
 
 window.location.href="/report/actuarial_report/fill_cycle.do?b_setup=setup&report_id="+reportcycleid;
  
 }
}
function MusterInitData()
{
	var waitInfo=eval("wait");	
	waitInfo.style.display="none";
}
</script>
<%
	int i=0;
	String parm="";
	
	try
	{
%>
<hrms:themes/>
<html:form action="/report/actuarial_report/fill_cycle">
<table width="85%" border="0" cellspacing="0"  align="center" style="margin-top: 1px;" cellpadding="0" class="ListTable">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap>
            <input type="checkbox" name="selbox" onclick="batch_select(this,'reportCycleForm.select');" title='<bean:message key="label.query.selectall"/>'>&nbsp;	    
            </td>           
            <td align="center" class="TableRow" nowrap width="30%">
		<bean:message key="reportcyclelist.name"/>&nbsp;
	    </td>
            <!--<td align="center" class="TableRow" nowrap>
		<bean:message key="conlumn.board.content"/>&nbsp;
	    </td>-->
            <td align="center" class="TableRow" nowrap>
		<bean:message key="reportcyclelist.adddate"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="reportcyclelist.year"/>&nbsp;
	    </td>
	    <td align="center" class="TableRow" nowrap>
	    	<bean:message key="reportcyclelist.actuarial.emethod"/>&nbsp;
	    </td>
	     <td align="center" class="TableRow" nowrap>
		<bean:message key="reportcyclelist.state"/>&nbsp;
	    </td>
	    <td align="center" class="TableRow" nowrap>
			<bean:message key="reportcyclelist.option"/>&nbsp;
	    </td>
         
           </tr>
   	  </thead>
          <hrms:extenditerate id="element" name="reportCycleForm" property="reportCycleForm.list" indexes="indexes"  pagination="reportCycleForm.pagination" pageCount="10" scope="session">
          <%
          if(i%2==0)
          {
          %>
          <tr class="trShallow" id="<bean:write name="element" property="string(id)" filter="true"/>">
          <%}
          else
          {%>
          <tr class="trDeep" id="<bean:write name="element" property="string(id)" filter="true"/>">
          <%
          }
          i++; 
          
        //  String statusname =AdminCode.getCodeName("23","<%=<bean:write name='element' property='string(name)' filter='true'/>");         
          %>  
            <td align="center" class="RecordRow" nowrap>
     		   <hrms:checkmultibox name="reportCycleForm" property="reportCycleForm.select" value="true" indexes="indexes"/>&nbsp;
	    </td>            
            <td align="left" class="RecordRow" >
            <logic:equal name="element" property="string(status)" value="04"> 
            	<%parm="04"; %>
                    <a href="/report/actuarial_report/fill_cycle.do?b_edit2=edit&report_id=<bean:write name="element" property="string(id)" filter="true"/>"><bean:write name="element" property="string(name)" filter="true"/>&nbsp;
                     </a>
                   </logic:equal>
                   <logic:notEqual name="element" property="string(status)" value="04"> 
                  <bean:write name="element" property="string(name)" filter="true"/>&nbsp;
                 
                   </logic:notEqual>
	    </td>
            <td align="left" class="RecordRow" nowrap>
                   <bean:write name="element" property="string(bos_date)" filter="true"/>&nbsp;
	    </td>
            <td align="left" class="RecordRow" nowrap>
                   <bean:write name="element" property="string(theyear)" filter="true"/>&nbsp;
	    </td> 
	    <logic:equal name="element" property="string(kmethod)" value="0">
	    <td align="left" class="RecordRow" nowrap>
	    	 <bean:message key="reportcyclelist.actuarial.allmethod"/>&nbsp;
	    </td>	
	    </logic:equal>  
	     <logic:equal name="element" property="string(kmethod)" value="1">
	    <td align="left" class="RecordRow" nowrap>
	    	 <bean:message key="reportcyclelist.actuarial.rollmethod"/>&nbsp;
	    </td>	
	    </logic:equal>  
	    
	     <logic:equal name="element" property="string(status)" value="01">               
            <td align="left" class="RecordRow" nowrap>
                 <%=AdminCode.getCodeName("23","01")%>&nbsp; 
	    </td>
	   </logic:equal>
	   <logic:equal name="element" property="string(status)" value="04">               
            <td align="left" class="RecordRow" nowrap>
                 <%=AdminCode.getCodeName("23","04")%>&nbsp; 
	    </td>
	   </logic:equal>
	   <logic:equal name="element" property="string(status)" value="06">               
            <td align="left" class="RecordRow" nowrap>
                 <%=AdminCode.getCodeName("23","06")%>&nbsp; 
	    </td>
	   </logic:equal>
	   <logic:equal name="element" property="string(status)" value="09">               
            <td align="left" class="RecordRow" nowrap>
                 <%=AdminCode.getCodeName("23","09")%>&nbsp; 
	    </td>
	   </logic:equal>
	     <logic:equal name="element" property="string(status)" value="01">               
            <td align="left" class="RecordRow" nowrap>
            <a href="/report/actuarial_report/fill_cycle.do?b_edit=edit&report_id=<bean:write name="element" property="string(id)" filter="true"/>"><img src="/images/edit.gif" border=0>(编辑)</a>
            <a href="javascript:setup('<bean:write name="element" property="string(id)" filter="true"/>')"><img src="/images/compute.gif" border=0>(启动)</a>
	    </td>
	   </logic:equal>
	     <logic:equal name="element" property="string(status)" value="04">               
            <td align="left" class="RecordRow" nowrap>
                 
                 <a href="/report/actuarial_report/fill_cycle.do?b_suspend=link&report_id=<bean:write name="element" property="string(id)" filter="true"/>"><img src="/images/icon_suspend.gif" border=0>(暂停)</a>
	   			 <a href="/report/actuarial_report/fill_cycle.do?b_end=link&report_id=<bean:write name="element" property="string(id)" filter="true"/>"><img src="/images/report_end.gif" border=0>(结束)</a>
	    </td>
	   </logic:equal>
	     <logic:equal name="element" property="string(status)" value="09">               
            <td align="left" class="RecordRow" nowrap>
         <a href="javascript:setup('<bean:write name="element" property="string(id)" filter="true"/>')"><img src="/images/compute.gif" border=0>(启动)</a>
	  
	    </td>
	   </logic:equal>
	    <logic:equal name="element" property="string(status)" value="06">               
            <td align="left" class="RecordRow" nowrap>
	   			 &nbsp;
	    </td>
	   </logic:equal>
          </tr>
        </hrms:extenditerate>
        
</table>

<table  width="85%" align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		    
				<bean:message key="label.page.serial" />
					<bean:write name="reportCycleForm" property="reportCycleForm.pagination.current" filter="true" />
				<bean:message key="label.page.sum" />
					<bean:write name="reportCycleForm" property="reportCycleForm.pagination.count" filter="true" />
				<bean:message key="label.page.row" />
					<bean:write name="reportCycleForm" property="reportCycleForm.pagination.pages" filter="true" />
				<bean:message key="label.page.page" />
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="reportCycleForm" property="reportCycleForm.pagination"
				nameId="reportCycleForm" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</table>

<table  width="85%" align="center">
          <tr>
            <td align="center">
         	<hrms:submit styleClass="mybutton" property="b_add">
            		<bean:message key="button.insert"/>
	 	</hrms:submit>
	 	<button extra="mybutton" id="clo3"
							onclick="del()"
							allowPushDown="false" down="false">
							<bean:message key="button.delete" />
						</button>
						<%
						if(!parm.equals("")){
						 %>
						
						 <button extra="mybutton" id="clo3"
							onclick="javascript:window.location.href='/report/actuarial_report/fill_cycle.do?b_query2=lisk&cycleparm=<%=parm %>'"
							allowPushDown="false" down="false">
							<bean:message key="button.initdata" />
						</button>
						 <%} %>
	
            </td>
          </tr>          
</table>
<input type="hidden" name="usrnames" value=""/>
</html:form> 
<div id='wait' style='position:absolute;top:200;left:250;display:none;'>
  <table border="1" width="400" cellspacing="0" cellpadding="4"  class="table_style" height="87" align="center">
           <tr>
             <td class="td_style"  height=24>正在启动计划,请稍候....</td>
           </tr>
           <tr>
             <td style="font-size:12px;line-height:200%" align=center>
               <marquee class="marquee_style" direction="right" width="300" scrollamount="5" scrolldelay="10" >
                 <table cellspacing="1" cellpadding="0">
                   <tr height=8>
                     <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                    </tr>
                  </table>
               </marquee>
             </td>
          </tr>
        </table>
</div> 
<script language="javascript">
 MusterInitData();
</script>
<%
	}
	catch(OutOfMemoryError error)
	{
	}
	catch(Exception ex)
	{
	}
%>
