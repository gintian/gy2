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
<style>
 .myfixedDiv {  
    overflow:auto; 
    height:expression(document.body.clientHeight-100);
    width:expression(document.body.clientWidth-30); 
    BORDER-BOTTOM: #99BBE8 1pt solid; 
    BORDER-LEFT: #99BBE8 1pt solid; 
    BORDER-RIGHT: #99BBE8 1pt solid; 
    BORDER-TOP: #99BBE8 1pt solid;
}
.fixedtab 
{ 
	overflow:auto; 
	BORDER-BOTTOM: 0pt solid; 
    BORDER-LEFT: 0pt solid; 
    BORDER-RIGHT: 0pt solid; 
    BORDER-TOP: 0pt solid ; 	
}
</style>
<script language="javascript">
  function winhref(nbase,a0100,a0100_encrpt)
  {
   if(a0100=="")
      return false;
   var o_obj=document.getElementById('a0100');   
   if(o_obj)
     o_obj.value=a0100;
   var url="/workbench/browse/showselfinfo.do?b_search=link&userbase="+nbase+"&flag=notself&returnvalue=4&isUserEmploy=1"
   highQueryForm.action=url;
   highQueryForm.target="_self";
   highQueryForm.submit();
  }  
  function winopen(nbase,a0100,a0100_encrypt)
  {
     if(a0100=="")
      return false;
    var o_obj=document.getElementById('a0100');   
   if(o_obj)
     o_obj.value=a0100;
   //highQueryForm.action="/general/inform/synthesisbrowse/synthesiscard.do?b_search=link&userbase="+nbase+"&inforkind=${highQueryForm.type}&tabid=${highQueryForm.tabid}&multi_cards=-1";
   //highQueryForm.target="_blank";
   //highQueryForm.submit();
   var url="/general/inform/synthesisbrowse/synthesiscard.do?b_search=link&userbase="+nbase+"&a0100="+a0100_encrypt+"&inforkind=${highQueryForm.type}&tabid=${highQueryForm.tabid}&multi_cards=-1";
      url = url.replace(/&/g,"`");
      var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(url);
      window.open(iframe_url,"_blank","left=0,top=0,width="+screen.availWidth+",height="+(screen.availHeight-50)+",scrollbars=yes,toolbar=no,menubar=no,location=no,resizable=no,status=no");
  }
  document.oncontextmenu = function() {return false;}
  
   function winopo()
  {
     highQueryForm.action="/workbench/query/hquery_result.do?b_view_photo=link";
     highQueryForm.target="il_body";
     highQueryForm.submit()
  }
   function winore()
  {
     highQueryForm.action="/workbench/query/hquery_result.do?br_return=link";
     highQueryForm.target="il_body";
     highQueryForm.submit();
  }
  function OutFile(infokind,dbpre)
  {
	  var isQuery = "false";
	  var url = window.location.pathname;
	  if(url.indexOf("/workbench/query/hquerycond_interface.do") > -1)
		  isQuery = "true";

     var dw=540,dh=400,dl=(screen.width-dw)/2;
     var dt=(screen.height-dh)/2;
     var thecodeurl="/workbench/query/query_interface.do?br_field=field`infokind="+infokind+"`dbpre="+dbpre+"`isQuery="+isQuery+"`callback=openReturn"; 
     var iframe_url = "/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
     var dialog = []; dialog.dw=540;dialog.dh=460;dialog.iframe_url=iframe_url;
     openWin(dialog);
  }
//ext window弹窗方法  wangb 20180201 bug 34402
function openWin(dialog){

	//兼容非IE浏览器 弹窗替换用 open弹窗  wangb 20171205
	var iTop = (window.screen.availHeight - 30 - dialog.dh) / 2;  //获得窗口的垂直位置
	var iLeft = (window.screen.availWidth - 10 - dialog.dw) / 2; //获得窗口的水平位置 
	window.open(dialog.iframe_url,"","width="+dialog.dw+"px,height="+dialog.dh+"px,resizable=no,scrollbars=no,status=no,left="+iLeft+",top="+iTop);
}
//open弹窗调用方法  wangb 20180201 bug 34402
function openReturn(values){
	if(values){
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
	     		 
		setTimeout("getExportFileName()",1000);
		var obj=new Object();
    	obj=values;
    	var ids=obj[0];
    	var infokind=obj[3];
    	var isQuery=obj[5]
    	var strwhere=obj[2];
    	var dbpre =obj[4]
    	var hashVo=new ParameterSet();
    	hashVo.setValue("dbpre",dbpre);
    	hashVo.setValue("ids",ids);
    	hashVo.setValue("infokind",infokind);
    	hashVo.setValue("strwhere",strwhere);
    	hashVo.setValue("querytype","1");
    	hashVo.setValue("isQuery",isQuery);
    	hashVo.setValue("selectDate",obj[6]);
	    hashVo.setValue("times",obj[7]);
	    hashVo.setValue("selectField",obj[8]);
	    hashVo.setValue("where",obj[9]);
    	var request=new Request({method:'post',asynchronous:true,onSuccess:function (){},functionId:'0202001021'},hashVo);	
	}
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
			 wait.style.display = "none";
		
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
			 wait.style.display="none";
		
		document.getElementById("exportRows").innerHTML="…";
	   	var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName,"excel");
	   	var viewPhoto = document.getElementById("viewPhoto");
	   	if(viewPhoto)
	   		viewPhoto.disabled = false;
	   		 
	   	var exportExcel = document.getElementById("exportExcel");
	   	if(exportExcel)
	   		exportExcel.disabled = false;
	   		 
	   	var returnButton = document.getElementById("returnButton");
	   	if(returnButton)
	   		returnButton.disabled = false;
	   	
   	} else {
   		var exportRows = outparameters.getValue("exportRows");
   		var totalRows = outparameters.getValue("totalRows");
   		if(totalRows)
   			document.getElementById("exportRows").innerHTML="，已导出" + exportRows + "/" + totalRows + "条数据…";
   		
   		setTimeout("getExportFileName()",3000);
   	}
}

//dbname为筛选条件的人员库前缀，nbase是当前元素的人员库前缀  
function multimediahref(dbname,a0100){
	var thecodeurl =""; 
	var return_vo=null;
	var setname = "A01";
	var dw=800,dh=500,dl=(screen.width-dw)/2;
	var dt=(screen.height-dh)/2;
  	thecodeurl="/general/inform/multimedia/multimedia_tree.do?b_query=link`setid="+setname+"`a0100="+a0100+"`nbase="+dbname+"`dbflag=A`canedit=false";
  	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
   	iframe_url = iframe_url.replace(/&/g,"`");
  	if(getBrowseVersion()){
  		return_vo= window.showModalDialog(iframe_url, "", 
  		"dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:800px; dialogHeight:500px;resizable:no;center:yes;scroll:no;status:no");
  	}else{//非IE浏览器
  		var iTop = (window.screen.availHeight - 30 - dh) / 2;  //获得窗口的垂直位置
		var iLeft = (window.screen.availWidth - 10 - dw) / 2; //获得窗口的水平位置 
		window.open(iframe_url,"","width="+dw+",height="+dh+",resizable=no,scrollbars=no,status=no,left="+iLeft+",top="+iTop);
  	}
  	
}
<logic:equal name="highQueryForm" property="type" value="1">
function setDivStyle(){
    document.getElementById("fixedDiv").style.height = document.body.clientHeight-100;
    document.getElementById("fixedDiv").style.width = document.body.clientWidth-15; 
    //document.getElementById("pageDiv").style.height = document.body.clientHeight-150;
    document.getElementById("pageDiv").style.width = document.body.clientWidth-15; 
}
window.onresize = function(){
    setDivStyle();
}
</logic:equal>
</script>
<%int i=0;%>
<hrms:themes />
<!--zgd 2014-7-9 信息列表中岗位中有兼职情况的特殊处理。partdescdiv在ParttimeTag中写入-->
<style>
<%if("hcm".equals(bosflag)){%>
.partdescdiv{           
	margin-top:-14px;
}
<%}%>
</style>
<html:form action="/workbench/query/hquery_result">
<input type="hidden" name="a0100" id="a0100">
<%if("hcm".equals(bosflag)){ %>
<div class="fixedtab">
<%}else{ %>
<div class="fixedtab" style="margin-top: 10px">
<%} %>
<logic:equal name="highQueryForm" property="type" value="1">
<div class="myfixedDiv" id='fixedDiv' style="padding: 0;">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
   	  <thead>
           <tr style="position:relative;" class="fixedHeaderTr">
            <logic:iterate id="element"    name="highQueryForm"  property="resultlist" indexId="index">
              <logic:notEqual name="element" property="priv_status" value="0">               
              <td align="center" class="TableRow" style="border-left: none;border-top: none;" nowrap>
                 <bean:write  name="element" property="itemdesc" filter="true"/>
	      </td>  
	      </logic:notEqual>	                           
            </logic:iterate>
      	    <logic:notEqual name="highQueryForm" property="tabid" value="-1">
            <td align="center" class="TableRow" style="border-left: none;border-top: none;" nowrap>
		     	<bean:message key="tab.base.info"/>          	
		    </td>	                
		    </logic:notEqual>
            <td align="center" class="TableRow" style="border-left: none;border-top: none;" nowrap>
		     	<bean:message key="tab.synthesis.info"/>          	
		    </td>
		    <logic:equal name="highQueryForm" property="multimedia_file_flag" value="1">
	 		      <td align="center" class="TableRow" style="border-left: none;border-top: none;" nowrap>
					<bean:message key="conlumn.resource_list.name"/>             	
				  </td> 
			  </logic:equal>	    	    	    		        	        	        
           </tr>
   	  </thead>
          <hrms:paginationdb id="element" name="highQueryForm" sql_str="highQueryForm.strsql" table="" where_str="highQueryForm.strwhere" columns="highQueryForm.columns" order_by="highQueryForm.order" page_id="pagination" pagerows="20" distinct="${highQueryForm.distinct}" keys="${highQueryForm.keys}">
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
        
			<bean:define id="a0100" name="element" property="a0100"/><!-- 获取兼职内容在ParttimeTag类内 -->	          
			<bean:define id="nbase" name="element" property="nbase"/>    
			  <%
                  LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
           	   	  String a0100_encrypt=(String)abean.get("a0100");              	            	   
                  request.setAttribute("a0100_encrypt",PubFunc.encrypt(a0100_encrypt)); 
                  	
                  %>    
            <hrms:parttime a0100="${a0100}" nbase="${nbase}" part_map="${highQueryForm.part_map}" name="element" scope="page" code="" kind="" uplevel="${highQueryForm.uplevel}"  b0110_desc="b0110_desc" e0122_desc="e0122_desc" part_desc="part_desc" descOfPart="descOfPart"/>
            <logic:iterate id="fielditem"  name="highQueryForm"  property="resultlist" indexId="index">
              <logic:notEqual name="fielditem" property="priv_status" value="0">   
                <logic:notEqual value="N" name="fielditem" property="itemtype">
                    <td align="left" class="RecordRow" style="border-left: none;border-top: none;" nowrap>
                </logic:notEqual>
                <logic:equal value="N" name="fielditem" property="itemtype">
                    <td align="right" class="RecordRow" style="border-left: none;border-top: none;" nowrap>
                </logic:equal>          
                 
              <logic:notEqual  name="fielditem" property="codesetid" value="0">  
	                   <logic:notEqual  name="fielditem"   property="itemid" value="e0122">  
	                    <logic:notEqual  name="fielditem"   property="itemid" value="b0110">  
	                     <logic:notEqual  name="fielditem"   property="itemid" value="e01a1"> 
	                      <logic:equal name="fielditem" property="codesetid" value="UM">
	                       <hrms:codetoname codeid="UM" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page" uplevel="${highQueryForm.uplevel}"/>  	      
	          	          		<!-- 
	          	            	//tianye update start
								//关联部门的指标支持指定单位（部门中查不出信息就去单位中查找）
	          	            	-->
	          	          		<logic:notEqual  name="codeitem" property="codename" value="">
	          	           			  <bean:write name="codeitem" property="codename" /> 
	          	           		</logic:notEqual>
	          	          		<logic:equal  name="codeitem" property="codename" value="">
	          	          		 	<hrms:codetoname codeid="UN" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page" uplevel="${highQueryForm.uplevel}"/>  
	          	           			  <bean:write name="codeitem" property="codename" /> 
	          	           		</logic:equal>
	          	           		<!-- end -->
	                      </logic:equal>
	                      <logic:notEqual name="fielditem" property="codesetid" value="UM">                      
	                        <hrms:codetoname codeid="${fielditem.codesetid}" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page"/>  	      
	          	    	     <bean:write name="codeitem" property="codename" />  
	                        
	                      </logic:notEqual>                
	                     </logic:notEqual>
	                    </logic:notEqual>
	                   </logic:notEqual>
	                   <logic:equal name="fielditem"   property="itemid" value="b0110">
	                      ${b0110_desc}     
	                   </logic:equal>
	                   <logic:equal name="fielditem"   property="itemid" value="e0122">
	                         ${e0122_desc}   
	                   </logic:equal>
	                   <logic:equal name="fielditem"   property="itemid" value="e01a1">
	                        <hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page"/>  	      
	          	             <bean:write name="codeitem" property="codename" />  
	          	             <logic:empty name="codeitem" property="codename">${descOfPart}</logic:empty>  
          	                 <logic:notEmpty name="codeitem" property="codename">${part_desc}</logic:notEmpty>
	                   </logic:equal>
	          	     </logic:notEqual>
              <logic:equal name="fielditem" property="codesetid" value="0">
                <logic:equal name="fielditem" property="itemid" value="a0101">
                
                  <logic:notEqual  name="highQueryForm" property="dbpre" value="All">
                 	<a href="###" onclick="winhref('${highQueryForm.dbpre}','<bean:write name="element" property="a0100" filter="true"/>');"> <bean:write name="element" property="${fielditem.itemid}" filter="true"/></a>            	
		          </logic:notEqual>
		          <logic:equal  name="highQueryForm" property="dbpre" value="All">
		             <a href="###" onclick="winhref('<bean:write name="element" property="nbase" filter="true"/>','<bean:write name="element" property="a0100" filter="true"/>');"><bean:write name="element" property="${fielditem.itemid}" filter="true"/></a>
		          </logic:equal>  
                   
                </logic:equal>
                <logic:notEqual name="fielditem" property="itemid" value="a0101">
                      <bean:write name="element" property="${fielditem.itemid}" filter="true"/>
                   </logic:notEqual>                   
              </logic:equal>                           
	      &nbsp;</td>   
	      </logic:notEqual>	 	                          
            </logic:iterate> 
      	    <logic:notEqual name="highQueryForm" property="tabid" value="-1">
              <td align="center" class="RecordRow" style="border-left: none;border-top: none;" nowrap>
                 <logic:notEqual  name="highQueryForm" property="dbpre" value="All">
               		<a href="###" onclick="winopen('${highQueryForm.dbpre}','<bean:write name="element" property="a0100" filter="true"/>','${a0100_encrypt }')"><img src="../../images/table.gif" border="0"></a>
		         </logic:notEqual>
		          <logic:equal  name="highQueryForm" property="dbpre" value="All">
               		<a href="###" onclick="winopen('<bean:write name="element" property="nbase" filter="true"/>','<bean:write name="element" property="a0100" filter="true"/>','${a0100_encrypt }')"><img src="../../images/table.gif" border="0"></a>
		         </logic:equal>
		      </td>	                
            </logic:notEqual>	                		
            <td align="center" class="RecordRow" style="border-left: none;border-top: none;" nowrap>
             <logic:notEqual  name="highQueryForm" property="dbpre" value="All">
            	<a href="###" onclick="winhref('${highQueryForm.dbpre}','<bean:write name="element" property="a0100" filter="true"/>');"><img src="../../images/view.gif" border="0"></a>            	
		     </logic:notEqual>
		     <logic:equal  name="highQueryForm" property="dbpre" value="All">
		       <a href="###" onclick="winhref('<bean:write name="element" property="nbase" filter="true"/>','<bean:write name="element" property="a0100" filter="true"/>');"><img src="../../images/view.gif" border="0"></a>
		     </logic:equal>   
		    </td> 
		    <logic:equal name="highQueryForm" property="multimedia_file_flag" value="1">
             	<td align="center" class="RecordRow" style="border-left: none;border-top: none;" nowrap>
            		<a href="###"  onclick='multimediahref("<bean:write name="element" property="nbase" filter="true"/>","<bean:write name="element" property="a0100" filter="true"/>");'><img src="/images/muli_view.gif" border=0></a>
	      		</td>
	      	</logic:equal>           	    	    	    		        	        	        
          </tr>
        </hrms:paginationdb>
</table>
</div>
</logic:equal>

<logic:equal name="highQueryForm" property="type" value="2">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTableF">
   	  <thead>
           <tr style="position:relative;">
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="highQueryForm" fieldname="B0110" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />
            </td>           
            <logic:iterate id="element"    name="highQueryForm"  property="resultlist" indexId="index">
              <logic:notEqual name="element" property="priv_status" value="0">               
              <td align="center" class="TableRow" nowrap>
                 <bean:write  name="element" property="itemdesc" filter="true"/>
	      </td>  
	      </logic:notEqual>	                           
            </logic:iterate> 
            <logic:equal name="highQueryForm" property="multimedia_file_flag" value="1">
	 		      <td align="center" class="TableRow" nowrap>
					<bean:message key="conlumn.resource_list.name"/>             	
				  </td> 
			  </logic:equal>    
    	    	    		        	        	        
           </tr>
   	  </thead>
          <hrms:paginationdb id="element" name="highQueryForm" sql_str="highQueryForm.strsql" table="" where_str="highQueryForm.strwhere" columns="highQueryForm.columns" order_by=" order by b01.b0110" page_id="pagination" pagerows="21" distinct="${highQueryForm.distinct}">
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
            <td align="left" class="RecordRow" nowrap>
               <a href="/workbench/query/hbrowser.do?b_query=link&keyid=<bean:write name="element" property="b0110" filter="true"/>">        
          	<hrms:codetoname codeid="UN" name="element" codevalue="b0110" codeitem="codeitem" scope="page"/>  	      
          	<bean:write name="codeitem" property="codename" />
          	<hrms:codetoname codeid="UM" name="element" codevalue="b0110" codeitem="codeitem" scope="page"/>  	      
          	<bean:write name="codeitem" property="codename" />      
              </a>    	
	    </td>            
            <logic:iterate id="fielditem"  name="highQueryForm"  property="resultlist" indexId="index">
              <logic:notEqual name="fielditem" property="priv_status" value="0">             
              <td align="left" class="RecordRow" nowrap>
                 <logic:notEqual name="fielditem" property="codesetid" value="0">
          	   <hrms:codetoname codeid="${fielditem.codesetid}" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page"/>  	      
          	   <bean:write name="codeitem" property="codename" />                    
                 </logic:notEqual>
                 <logic:equal name="fielditem" property="codesetid" value="0">
                   <bean:write name="element" property="${fielditem.itemid}" filter="true"/>                 
                 </logic:equal>                                
	      </td>   
	      </logic:notEqual>	 	                          
            </logic:iterate> 
            <logic:equal name="highQueryForm" property="multimedia_file_flag" value="1">
             	<td align="center" class="RecordRow" nowrap>
            		<a href="###"  onclick='multimediahref("<bean:write name="element" property="nbase" filter="true"/>","<bean:write name="element" property="a0100" filter="true"/>");'><img src="/images/muli_view.gif" border=0></a>
	      		</td>
	      	</logic:equal>	    	    	    		        	        	        
          </tr>
        </hrms:paginationdb>
</table>
</logic:equal>

<logic:equal name="highQueryForm" property="type" value="3">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTableF">
   	  <thead>
           <tr style="position:relative;">
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="highQueryForm" fieldname="E01A1" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />
	    </td>
            <logic:iterate id="element"    name="highQueryForm"  property="resultlist" indexId="index">
              <logic:notEqual name="element" property="priv_status" value="0">               
              <td align="center" class="TableRow" nowrap>
                 <bean:write  name="element" property="itemdesc" filter="true"/>
	      </td>  
	      </logic:notEqual>	                           
            </logic:iterate>
            <logic:equal name="highQueryForm" property="multimedia_file_flag" value="1">
	 		      <td align="center" class="TableRow" nowrap>
					<bean:message key="conlumn.resource_list.name"/>             	
				  </td> 
			  </logic:equal>     
    	    	    		        	        	        
           </tr>
   	  </thead>
          <hrms:paginationdb id="element" name="highQueryForm" sql_str="highQueryForm.strsql" table="" where_str="highQueryForm.strwhere" columns="highQueryForm.columns" order_by=" order by k01.e01a1" page_id="pagination" pagerows="21" distinct="${highQueryForm.distinct}">
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
            <td align="left" class="RecordRow" nowrap>
               <a href="/workbench/query/hbrowser.do?b_query=link&keyid=<bean:write name="element" property="e01a1" filter="true"/>">          
                <hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page"/>  	      
          	<bean:write name="codeitem" property="codename" />   
               </a> 
	    </td>
            <logic:iterate id="fielditem"  name="highQueryForm"  property="resultlist" indexId="index">
              <logic:notEqual name="fielditem" property="priv_status" value="0">             
              <td align="left" class="RecordRow" nowrap>
                 <logic:notEqual name="fielditem" property="codesetid" value="0">
          	   <hrms:codetoname codeid="${fielditem.codesetid}" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page"/>  	      
          	   <bean:write name="codeitem" property="codename" />                    
                 </logic:notEqual>
                 <logic:equal name="fielditem" property="codesetid" value="0">
                   <bean:write name="element" property="${fielditem.itemid}" filter="true"/>                 
                 </logic:equal>                                
	      </td>   
	      </logic:notEqual>	 	                          
            </logic:iterate> 
            <logic:equal name="highQueryForm" property="multimedia_file_flag" value="1">
             	<td align="center" class="RecordRow" nowrap>
            		<a href="###"  onclick='multimediahref("<bean:write name="element" property="nbase" filter="true"/>","<bean:write name="element" property="a0100" filter="true"/>");'><img src="/images/edit.gif" border=0></a>
	      		</td>
	      	</logic:equal>	    	    	    		        	        	        
          </tr>
        </hrms:paginationdb>
</table>
</logic:equal>
</div>
<logic:equal name="highQueryForm" property="type" value="1">
<div id='pageDiv' style="padding: 0;">
</logic:equal>
<table  width="100%" align="center" class="RecordRowP" style="">
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
		          <p align="right"><hrms:paginationdblink name="highQueryForm" property="pagination" nameId="highQueryForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
</table>
<logic:equal name="highQueryForm" property="type" value="1">
</div>
</logic:equal>
<table  width="70%" align="center" >
		  <tr>
		  	<td height="0px"></td>
		  </tr>
          <tr>
            <td align="center">
               <logic:equal name="highQueryForm" property="type" value="1">               
         	     <html:button styleId="viewPhoto" styleClass="mybutton" property="b_view_photo" onclick="winopo();">
					<bean:message key="button.query.viewphoto"/>
				</html:button>
               </logic:equal>		
               <hrms:priv func_id="2601007,0303013">
               <html:button styleClass="mybutton" styleId="exportExcel" property="excel" onclick="return OutFile('${highQueryForm.type}','${highQueryForm.dbpre}');">
					<bean:message key="report.actuarial_report.exportExcel"/>
				</html:button>    
				</hrms:priv>     	
               <html:button styleClass="mybutton" styleId="returnButton" property="br_return" onclick="winore();">    
            		<bean:message key="button.return"/>
	 	        </html:button>
            </td>
          </tr>          
</table>
</html:form>
<div id='wait' style='position:absolute;top:45%;left:35%;display:none;z-index: 999px;'>
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
<logic:equal name="highQueryForm" property="type" value="1">
<script type="text/javascript">
setDivStyle();
</script>
</logic:equal>
