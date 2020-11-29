<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@page import="org.apache.commons.beanutils.LazyDynaBean"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    String bosflag="";
    if(userView!=null){
     bosflag = userView.getBosflag();
    }
%>
<hrms:themes></hrms:themes>
<style>
.myfixedDiv{  
    overflow:auto; 
    height:expression(document.body.clientHeight-150);
    width:expression(document.body.clientWidth-30); 
    BORDER-BOTTOM: #99BBE8 1pt solid; 
    BORDER-LEFT: #99BBE8 1pt solid; 
    BORDER-RIGHT: #99BBE8 1pt solid; 
    BORDER-TOP: #99BBE8 1pt solid;
}
.fixedtab 
{ 
	overflow:auto; 
	height:expression(document.body.clientHeight-110);
	width:expression(document.body.clientWidth-52); 
}
</style>
<script language="javaScript">
var userAgent = window.navigator.userAgent; //取得浏览器的userAgent字符串  
var isOpera = userAgent.indexOf("Opera") > -1;
var isIE = (userAgent.indexOf("compatible") > -1 && userAgent.indexOf("MSIE") > -1 && !isOpera); 
   var fromFlag="${complexInterfaceForm.fromFlag}";
   if(fromFlag=='1')
   {
	   if(isIE){
	       window.returnValue="1";
	       window.close();
	   }else{
	   	  if(parent.opener){
	   	   parent.opener.returnValue(1);
		   top.close();
	   	  }
	   }
   }
	function changedb(){		
		complexInterfaceForm.action="/workbench/query/complex_interface.do?b_search=link";
		complexInterfaceForm.submit();
	}
	function showpoh()
	{
	    complexInterfaceForm.action="/workbench/query/complex_interface_pho.do?b_query=link";
		complexInterfaceForm.submit();
	
	}
	function winopen(nbase,a0100)
    {
      complexInterfaceForm.action="/workbench/browse/showselfinfo.do?b_search=link&userbase="+nbase+"&a0100="+a0100+"&flag=notself&returnvalue=complex&isUserEmploy=1";
      complexInterfaceForm.target="il_body";
      complexInterfaceForm.submit()
   }

	var timeFun;
	function OutFile(infokind,dbpre) {
		var isQuery = "false";
		var url = window.location.pathname;
		if(url.indexOf("/workbench/query/query_interface.do") > -1
				|| url.indexOf("/workbench/query/gquery_interface.do") > -1)
			isQuery = "true";
		  
     var thecodeurl="/workbench/query/query_interface.do?br_field=field`callback=closeAction`dbpre="+dbpre+"`isQuery="+isQuery+"`infokind="+infokind; 
     var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
	 var dw=540,dh=460,dl=(screen.width-dw)/2;
	 var dt=(screen.height-dh)/2;
	 window.open(iframe_url,'_blank',"width=540,height=460,top="+screen.height/3+"px,left="+screen.width/3+"px,toolbar=no,location=no,resizable=no");
  }
	function closeAction(values){
		if(values) {   	  
   			var viewPhoto = document.getElementById("viewPhoto");
	    	if(viewPhoto)
	    		viewPhoto.disabled = true;
	    		 
	    	var exportExcel = document.getElementById("exportExcel");
	    	if(exportExcel)
	    		exportExcel.disabled = true;
	    		 
	    	var returnButton = document.getElementById("returnButton");
	    	if(returnButton)
	    		returnButton.disabled = true;
	    		 
	    	var wait = document.getElementById("wait");
	    	if(wait)
	    		wait.style.display="block";
	    	 
	    	var pre="";
	        var obj = document.getElementById("dbpre");
	        for(var i=0;i<obj.options.length;i++) {
	           if(obj.options[i].selected)
	              pre=obj.options[i].value;
	        }
	        
	        timeFun = setTimeout("getExportFileName()",1000);
	         var hashVo=new ParameterSet();
	         hashVo.setValue("dbpre",values[4]);
	         hashVo.setValue("ids",values[0]);
	         hashVo.setValue("infokind",values[3]);
	         hashVo.setValue("strwhere",values[2]);
	         hashVo.setValue("querytype","5");
	         hashVo.setValue("pre",pre);
	         hashVo.setValue("selectDate",values[6]);
	         hashVo.setValue("times",values[7]);
	         hashVo.setValue("selectField",values[8]);
	         hashVo.setValue("where",values[9]);
	         hashVo.setValue("isQuery", "true");
	         var request=new Request({method:'post',asynchronous:true,onSuccess:function () {},functionId:'0202001021'},hashVo);	
     }		
  }
   
function opentable(url)
{
  window.open(url,"_blank","left=0,top=0,width="+screen.availWidth+",height="+(screen.availHeight-50)+",scrollbars=yes,toolbar=no,menubar=no,location=no,resizable=no,status=no");
}

function getExportFileName(){
	var hashVo=new ParameterSet();
    hashVo.setValue("exportFlag","1");
    var request=new Request({method:'post',asynchronous:true,onSuccess:exportFile,functionId:'0202001021'},hashVo);
}

function exportFile(outparameters){
	var msg = outparameters.getValue("msg");
	if("ok" != msg) {
		var wait = document.getElementById("wait");
		if(wait)
			 wait.style.display="none";
		
		alert(msg);
		var viewPhoto = document.getElementById("viewPhoto");
	   	if(viewPhoto)
	   		viewPhoto.disabled = false;
	   		 
	   	var exportExcel = document.getElementById("exportExcel");
	   	if(exportExcel)
	   		exportExcel.disabled = false;
	   		 
	   	var returnButton = document.getElementById("returnButton");
	   	if(returnButton)
	   		returnButton.disabled = false;
	   		
		return false;
	}
		
   	var outName=outparameters.getValue("exportEmployeFileName");
   	
   	if(outName) {
   		var wait = document.getElementById("wait");
		if(wait)
			 wait.style.display = "none";
		
		var viewPhoto = document.getElementById("viewPhoto");
	   	if(viewPhoto)
	   		viewPhoto.disabled = false;
	   		 
	   	var exportExcel = document.getElementById("exportExcel");
	   	if(exportExcel)
	   		exportExcel.disabled = false;
	   		 
	   	var returnButton = document.getElementById("returnButton");
	   	if(returnButton)
	   		returnButton.disabled = false;
	   		
		var wait = document.getElementById("wait");
		if(wait)
			 wait.style.display="none";
		 
	   	clearTimeout(timeFun);
		document.getElementById("exportRows").innerHTML="…";
	   	var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName,"excel");
   	} else {
   		var exportRows = outparameters.getValue("exportRows");
   		var totalRows = outparameters.getValue("totalRows");
   		if(totalRows)
   			document.getElementById("exportRows").innerHTML="，已导出" + exportRows + "/" + totalRows + "条数据…";
   		
   		timeFun = setTimeout("getExportFileName()",3000);
   	}
}

function multimediahref(dbname,a0100){
	var thecodeurl =""; 
	var return_vo=null;
	var setname = "A01";
	var dw=800,dh=500,dl=(screen.width-dw)/2,dt=(screen.height-dh)/2;
  	thecodeurl="/general/inform/multimedia/multimedia_tree.do?b_query=link&setid="+setname+"&a0100="+a0100+"&nbase="+dbname+"&dbflag=A&canedit=false";
  	return_vo= window.showModalDialog(thecodeurl, "", 
  	"dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:800px; dialogHeight:500px;resizable:no;center:yes;scroll:no;status:no");
}

function setDivStyle(){
    document.getElementById("fixedDiv").style.height = document.body.clientHeight-150;
    document.getElementById("fixedDiv").style.width = document.body.clientWidth-30; 
    document.getElementById("pageDiv").style.width = document.body.clientWidth-30; 
}
window.onresize = function(){
    setDivStyle();
}

</script>
<%int i=0;%>
<!--zgd 2014-7-9 信息列表中岗位中有兼职情况的特殊处理。partdescdiv在ParttimeTag中写入-->
<style>
<%if("hcm".equals(bosflag)){%>
.partdescdiv{           
	margin-top:-5px;
}
<%}%>
</style>
<html:form action="/workbench/query/complex_interface">
<%if("hcm".equals(bosflag)){ %>
<table width="100%">
<%}else{ %>
<table width="100%" style="margin-top: 10px">
<%} %>
<bean:size id="length" name="complexInterfaceForm" property="dblist"/>
   <tr <logic:lessThan value="2" name="length">style="display: none"</logic:lessThan>>
	<td><bean:message key="menu.base"/>
	              <html:select name="complexInterfaceForm" styleId="dbpre" property="dbpre" size="1" onchange="changedb()">
                  <html:optionsCollection property="dblist" value="dataValue" label="dataName"/>
                  <!-- tiany update 选择全部人员库查询时，查询后下拉菜单锁定不对，显示人员库因为全部 -->
		               <logic:notEqual name="complexInterfaceForm" property="dbpre" value="">
		               <logic:notEqual name="complexInterfaceForm" property="dbpre" value="ALL">
		                   <option value="ALL"><bean:message key="label.all"/>人员库</option>
		               </logic:notEqual>
		               </logic:notEqual>
		               <logic:equal name="complexInterfaceForm" property="dbpre" value="">
		                   <option value="ALL" selected="selected"><bean:message key="label.all"/>人员库</option>
		               </logic:equal>
		                <logic:equal name="complexInterfaceForm" property="dbpre" value="ALL">
		                   <option value="ALL" selected="selected"><bean:message key="label.all"/>人员库</option>
		               </logic:equal>
                 <!-- tiany update end -->
                </html:select> 	
	</td>
   </tr>
</table>
<div class="myfixedDiv" id='fixedDiv' style="padding: 0;">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   <thead>
     <tr style="position:relative;" class="fixedHeaderTr">
         <logic:iterate id="info"    name="complexInterfaceForm"  property="browsefields">   
              <td align="center" class="TableRow" class="TableRow" style="border-left: none;border-top: none;" nowrap>
                   <bean:write  name="info" property="itemdesc" filter="true"/>                  
              </td>
             </logic:iterate> 
	   <logic:notEqual name="complexInterfaceForm" property="tabid" value="-1">
           <td align="center" class="TableRow" class="TableRow" style="border-left: none;border-top: none;" nowrap>
		     	<bean:message key="tab.base.info"/>          	
		    </td>
	  </logic:notEqual>
            <td align="center" class="TableRow" class="TableRow" style="border-left: none;border-top: none;" nowrap>
		     	<bean:message key="tab.synthesis.info"/>          	
	     </td>
	     <logic:equal name="complexInterfaceForm" property="multimedia_file_flag" value="1">
	 		      <td align="center" class="TableRow" class="TableRow" style="border-left: none;border-top: none;" nowrap>
					<bean:message key="conlumn.resource_list.name"/>             	
				  </td> 
			  </logic:equal>	    	    	    		        	        	        
    </tr>
  </thead>
  <hrms:paginationdb id="element" name="complexInterfaceForm" sql_str="complexInterfaceForm.strsql" table="" where_str="" columns="complexInterfaceForm.columns" order_by="complexInterfaceForm.order" page_id="pagination" pagerows="20" distinct="" keys="">
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
      <%
      LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
	   String a0100_encrypt=(String)abean.get("a0100");              	            	   
     request.setAttribute("a0100_encrypt",PubFunc.encrypt(a0100_encrypt));   
      
      %>
	    <bean:define id="a0100" name="element" property="a0100"/><!-- 获取兼职内容在ParttimeTag类内 -->	          
	    <bean:define id="nbase" name="element" property="nbase"/><!-- 获取兼职内容在ParttimeTag类内 -->	          
        <hrms:parttime a0100="${a0100}" nbase="${nbase}" part_map="${complexInterfaceForm.part_map}" name="element" scope="page" code="" kind="" uplevel="${complexInterfaceForm.uplevel}"  b0110_desc="b0110_desc" e0122_desc="e0122_desc" part_desc="part_desc" descOfPart="descOfPart"/>
	     <logic:iterate id="info"    name="complexInterfaceForm"  property="browsefields">   
	     			<bean:define id="a0100" name="element" property="a0100"/>
                  <logic:notEqual  name="info" property="itemtype" value="N">               
                    <td align="left" class="RecordRow" class="TableRow" style="border-left: none;border-top: none;" nowrap>        
                  </logic:notEqual>
                  <logic:equal  name="info" property="itemtype" value="N">               
                    <td align="right" class="RecordRow" class="TableRow" style="border-left: none;border-top: none;" nowrap>        
                  </logic:equal>    
                  <logic:equal  name="info" property="codesetid" value="0">   
                   <logic:notEqual name="info"   property="itemid" value="a0101">        
                     <bean:write  name="element" property="${info.itemid}" filter="true"/>
                   </logic:notEqual>
                      <logic:equal name="info"   property="itemid" value="a0101">  
                        <a href="###" onclick="winopen('<bean:write name="element" property="nbase" filter="true"/>','<bean:write name="element" property="a0100" filter="true"/>');">
          	   			 <bean:write name="element" property="a0101" filter="true"/>
          	   			</a>
          	   	 	</logic:equal>
                  </logic:equal>
                  <logic:notEqual  name="info" property="codesetid" value="0">
						<logic:notEqual  name="info"   property="itemid" value="e0122">  
	                    <logic:notEqual  name="info"   property="itemid" value="b0110">  
	                     <logic:notEqual  name="info"   property="itemid" value="e01a1">
	                     <logic:notEqual  name="info"   property="itemid" value="a0101"> 
	                      <logic:equal name="info" property="codesetid" value="UM">
	                       <hrms:codetoname codeid="UM" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page" uplevel="${complexInterfaceForm.uplevel}"/>  	      
	          	          		<!-- 
	          	            	//tianye update start
								//关联部门的指标支持指定单位（部门中查不出信息就去单位中查找）
	          	            	-->
	          	          		<logic:notEqual  name="codeitem" property="codename" value="">
	          	           			  <bean:write name="codeitem" property="codename" /> 
	          	           		</logic:notEqual>
	          	          		<logic:equal  name="codeitem" property="codename" value="">
	          	          		 	<hrms:codetoname codeid="UN" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page" uplevel="${complexInterfaceForm.uplevel}"/>  
	          	           			  <bean:write name="codeitem" property="codename" /> 
	          	           		</logic:equal>
	          	           		<!-- end -->
	                      </logic:equal>
	                      <logic:notEqual name="info" property="codesetid" value="UM">                      
	                        <hrms:codetoname codeid="${info.codesetid}" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page"/>  	      
	          	    	     <bean:write name="codeitem" property="codename" />  
	                        
	                      </logic:notEqual>                
	                      </logic:notEqual>                
	                     </logic:notEqual>
	                    </logic:notEqual>
	                   </logic:notEqual>
	                   <logic:equal name="info"   property="itemid" value="b0110">
	                      ${b0110_desc}     
	                   </logic:equal>
	                   <logic:equal name="info"   property="itemid" value="e0122">
	                         ${e0122_desc}   
	                   </logic:equal>
	                   <logic:equal name="info"   property="itemid" value="e01a1">
	                        <hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page"/>  	      
	          	             <bean:write name="codeitem" property="codename" />  
	          	             <logic:empty name="codeitem" property="codename">${descOfPart}</logic:empty>  
          	                 <logic:notEmpty name="codeitem" property="codename">${part_desc}</logic:notEmpty>
	                   </logic:equal>
	          	     </logic:notEqual>  
              &nbsp;</td>
             </logic:iterate> 
	<logic:notEqual name="complexInterfaceForm" property="tabid" value="-1">
	<td align="center" class="RecordRow" class="TableRow" style="border-left: none;border-top: none;" nowrap>
               		<a href="###" onclick="opentable('/general/inform/synthesisbrowse/synthesiscard.do?b_search=link&userbase=<bean:write name="element" property="nbase" filter="true"/>&a0100=${a0100_encrypt}&inforkind=1&tabid=${complexInterfaceForm.tabid}&multi_cards=-1');"><img src="/images/table.gif" border="0"></a>
	</td>	
	</logic:notEqual>                
        <td align="center" class="RecordRow" class="TableRow" style="border-left: none;border-top: none;" nowrap>
            	<a href="###" onclick="winopen('<bean:write name="element" property="nbase" filter="true"/>','<bean:write name="element" property="a0100" filter="true"/>');"><img src="/images/view.gif" border="0"></a>            	
	</td>     
     <logic:equal name="complexInterfaceForm" property="multimedia_file_flag" value="1">
    	<td align="center" class="RecordRow" class="TableRow" style="border-left: none;border-top: none;" nowrap>
   			<a href="###"  onclick='multimediahref("<bean:write name="element" property="nbase" filter="true"/>","<bean:write name="element" property="a0100" filter="true"/>");'><img src="/images/muli_view.gif" border=0></a>
		</td>
	</logic:equal>  
     </tr>
  </hrms:paginationdb>
 </table>
</div>
<div id='pageDiv' style="padding: 0;">
<table  width="100%" align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            <bean:message key="label.page.serial"/>
					<bean:write name="pagination" property="current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="pagination" property="count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="pagination" property="pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="complexInterfaceForm" property="pagination" nameId="complexInterfaceForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
</table>
</div>
<table width="100%" align="center">
		  <tr>
		  	<td height="0px"></td>
		  </tr>
          <tr>
            <td align="center">
                  <input type="button" name="returnbutton" styleId="viewPhoto" value="显示照片" class="mybutton" onclick="showpoh();">       
                  <hrms:priv func_id="2601007,0303013">
                   <html:button styleClass="mybutton" styleId="exportExcel" property="excel" onclick="OutFile('1','${complexInterfaceForm.comple_db}');">
					<bean:message key="report.actuarial_report.exportExcel"/>
				</html:button>      
				</hrms:priv>
              	 <hrms:submit styleClass="mybutton" styleId="returnButton" property="br_return">
            		<bean:message key="button.return"/>
	 	          </hrms:submit>
	 	                    
            </td>
          </tr>          
</table>
</html:form>
<div id='wait' style='position:absolute;top:45%;left:35%;display:none;'>
  <table border="1" width="400" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
           <tr>
             <td class="td_style" height="24">正在导出数据<span id='exportRows'>…</span></td>
           </tr>
           <tr>
             <td style="font-size:12px;line-height:200%" align=center>
               <marquee class="marquee_style" direction="right" width="400" scrollamount="5" scrolldelay="10">
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
<script type="text/javascript">
setDivStyle();
</script>
