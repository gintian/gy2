<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<style>
.myfixedDiv 
{ 
	overflow:auto;
	BORDER-BOTTOM: #C4D8EE 1pt solid;
    BORDER-LEFT: #C4D8EE 1pt solid;
    BORDER-RIGHT: #C4D8EE 1pt solid;
    BORDER-TOP: 0;
}

</style>
<hrms:themes />
<script>
   /**
	 * 判断当前浏览器是否为ie6
	 * 返回boolean 可直接用于判断 
	 * @returns {Boolean}
	 */
	function isIE6() 
	{ 
		if(navigator.appName == "Microsoft Internet Explorer") 
		{ 
			if(navigator.userAgent.indexOf("MSIE 6.0")>0) 
			{ 
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
   function add(){   	  
       target_url="/performance/options/perKnowAdd.do?b_add=link`info=save"; 
       var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
       /* if(isIE6()){
           var return_vo=window.showModalDialog(iframe_url,'perknowAddglWin','dialogWidth:410px; dialogHeight:210px;resizable:no;center:yes;scroll:no;status:no');
       }else{
           var return_vo=window.showModalDialog(iframe_url,'perknowAddglWin','dialogWidth:400px; dialogHeight:210px;resizable:no;center:yes;scroll:no;status:no');
       } */
       
       var config = {
   		    width:410,
   		    height:200,
   		    type:'1',
   		    id:'perknow_win',
           title:'了解程度维护'
   		}

   		modalDialog.showModalDialogs(iframe_url,"perknow_win",config,perknow_ok);
	}
   
   function perknow_ok(return_vo) {
	   if(return_vo!=null)
	   		 reflesh();   	
   }
   function edit(knowId)
   {
	   var target_url="/performance/options/perKnowAdd.do?b_edit=link`knowId="+knowId+"`info=edit";
	   var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
	   //var return_vo=window.showModalDialog(iframe_url,'perknowEditWin','dialogWidth:400px; dialogHeight:210px;resizable:no;center:yes;scroll:no;status:no');
 	   var config = {
   		    width:410,
   		    height:200,
   		    type:'1',
   		    id:'perknow_win'
   		}

   		modalDialog.showModalDialogs(iframe_url,"perknow_win",config,perknow_ok);
   }

	function checkdelete(){
			var str="";
			for(var i=0;i<document.perKnowForm.elements.length;i++)
			{
				if(document.perKnowForm.elements[i].type=="checkbox")
				{
					if(document.perKnowForm.elements[i].checked==true  && document.perKnowForm.elements[i].name!="selbox")
					{
						str+=document.perKnowForm.elements[i+1].value+"/";
					}
				}
			}
			if(str.length==0)
			{
				alert('<bean:message key="jx.paramset.selDel"/>');
				return;
			}else{
				if(confirm("确认删除了解程度？"))
    			{	
					perKnowForm.action="/performance/options/perKnowList.do?b_delete=link&deletestr="+str; 
				 	perKnowForm.submit();
				}
			}
	  }
	  
	  
	function IfWindowClosed() {
		if (newwindow.closed == true) { 
			window.clearInterval(timer)
			perKnowForm.action="/performance/options/perKnowList.do?b_query=link&modelflag=${param.modelflag}"
		    perKnowForm.submit();
		}
	}
	function toSorting(){
		var thecodeurl="/performance/options/perKnowSort.do?b_sort=link";
		var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
		//var return_vo= window.showModalDialog(thecodeurl, "", 
	    //         "dialogWidth:500px; dialogHeight:430px;resizable:no;center:yes;scroll:yes;status:no");
		var config = {
		    width:570,
		    height:440,
		    type:'1',
		    id:"toSorting_win"
		}

		modalDialog.showModalDialogs(iframe_url,"toSorting_win",config,perknow_ok);
	}
	function reflesh(){		
		document.perKnowForm.action="/performance/options/perKnowList.do?b_query=link&modelflag=${param.modelflag}";
	    document.perKnowForm.submit();
   }

</script>
<%
	int i=0;
	String temp=request.getParameter("modelflag");
%>
<html:form action="/performance/options/perKnowList">

	<table width="100%" border="0" align="center">
		<tr>
			<td>
			<div id="tbl-container" class="myfixedDiv">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
       <tr class="fixedHeaderTr">
         <td align="center"  class="TableRow_right common_background_color common_border_color" nowrap width="10%">
		    <input type="checkbox" name="selbox" onclick="batch_select(this, 'setlistform.select');">
         </td>         
         <td align="center" class="TableRow" nowrap >
		   <bean:message key="report.number"/>
	     </td>          
         <td align="center"  class="TableRow" nowrap >
		      <bean:message key='column.name' />
	     </td>
	      <td align="center"  class="TableRow" nowrap >
			 <bean:message key='kh.field.flag' />
         </td> 
	      <td align="center"  class="TableRow_left  common_background_color common_border_color" nowrap >
			<bean:message key='lable.tz_template.edit' />
         </td>         
           </tr>
   	  </thead>
          <hrms:extenditerate id="element" name="perKnowForm" property="setlistform.list" indexes="indexes"  pagination="setlistform.pagination" pageCount="1000" scope="session">
          <bean:define id="nid" name="element" property="string(know_id)"/>
          <%
          if(i%2==0)
          {
          %>
          <tr class="trShallow">
          <%}
          else
          {%>
          <tr class="trDeep">
          <%
          }
          i++;          
          %>  
        <td align="center" style="border-top:0px;" class="RecordRow_right" nowrap>
     		<hrms:checkmultibox name="perKnowForm" property="setlistform.select" value="true" indexes="indexes"/>
   	    </td>          
        <td align="right" style="border-top:0px;" class="RecordRow" nowrap>
            <bean:write name="element" property="string(know_id)" filter="true"/>
             <Input type='hidden' value='<bean:write name="element" property="string(know_id)" filter="true"/>'  name='knowId' />
              <Input type='hidden' value='<bean:write name="element" property="string(seq)" filter="true"/>'  name='seq' />
	    </td>        
        <td align="left" style="border-top:0px;" class="RecordRow" nowrap>
            &nbsp; <bean:write name="element" property="string(name)" filter="true"/>
	    </td>
        <td align="left" style="border-top:0px;" class="RecordRow" nowrap>
       		<logic:equal name="element" property="string(status)" value="1">
		    	 &nbsp;<bean:message key='kh.field.yx' />
		    </logic:equal>
		    <logic:equal name="element" property="string(status)" value="0">
		    	 &nbsp;<bean:message key='kh.field.wx' />
			</logic:equal>
        </td>
	     <td align="center" style="border-top:0px;" class="RecordRow_left" nowrap>
			<a onclick="edit('<bean:write name="element" property="string(know_id)" filter="true"/>');"><img src="/images/edit.gif" border=0 style="cursor:hand;"></a>
		</td>        
       </tr>
    </hrms:extenditerate>
</table>
</div>
	</td>        
       </tr>
      </table>
<table  width="100%">
          <tr>
            <td  align="center">
 
         	<input type='button' class="mybutton" property="b_add"  onclick='add()' value='<bean:message key="button.insert"/>'  />

    
            <input type='button' class="mybutton" property="b_delete"  onclick='checkdelete()' value='<bean:message key="button.delete"/>'  />

         <input type="button"  value="<bean:message key='kq.item.change'/>" onclick="toSorting();" Class="mybutton">
         <%if("performance".equals(temp)){ %>
         <hrms:tipwizardbutton flag="performance" target="il_body" formname="perKnowForm"/>  
         <%}else if("capability".equals(temp)){ %>
         <hrms:tipwizardbutton flag="capability" target="il_body" formname="perKnowForm"/>  
         <%} %>
            </td>
          </tr>          
</table>
<script type="text/javascript">
    var div = document.getElementById("tbl-container");
    if(div){
        div.style.height= (document.body.clientHeight-110)+"px";
        div.style.width= (document.body.clientWidth-100)+"px";
    }
</script>
</html:form>