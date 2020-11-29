<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.report.edit_report.EditReportForm,java.util.ArrayList,org.apache.commons.beanutils.LazyDynaBean" %>
<html>
<head>
<title>Insert title here</title>
<hrms:themes />
<style>
 div#treemenu {
 background-color: #FFFFFF;
 BORDER-BOTTOM:#94B6E6 1pt inset; 
 BORDER-COLLAPSE: collapse;
 BORDER-LEFT: #94B6E6 1pt inset; 
 BORDER-RIGHT: #94B6E6 1pt inset; 
 BORDER-TOP: #94B6E6 1pt inset; 
 margin-TOP:0px;
 margin-left:10px;
 width: 230px;
 height: 198px;
 overflow: auto;
 }

</style>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<%
	EditReportForm erh=(EditReportForm)session.getAttribute("editReportForm");
	ArrayList list=erh.getSortlist();
	String unitcode=erh.getDmlunit();
	String tsort=erh.getDmlsort();
	String subunitcode=(String)request.getParameter("unitcode");
	String unitname=erh.getDmlunitname();
	String kvalue="";
	String text="";
	boolean flag=false;
	if(erh.getSelectunit().indexOf("/")!=-1) {
		flag=true;
		kvalue=erh.getSelectunit().split("/")[0];
		text=erh.getSelectunit().split("/")[1];
	}
 %>
 <script type="text/javascript">
 	function addunits(){
 		if(root.getSelected()==""){
 			alert("请选择单位！");
				return;
 		}
 		var temp_str=root.getSelected();
		var temps=temp_str.split(",");
		var right=document.getElementsByName("right_fields");
		var right_vo=right[0];
		for(i=0;i<temps.length;i++)
		{
		    if(temps[i].length>0)
		    {
			    if(temps[i].split("/").length==1){
			    	alert("不能驳回本人负责单位！自动过滤本单位！");
			    	continue;
			    }
			     if(temps[i].split("/").length==1){
			     	continue;
			     }
		    	var isExist=0;
		    	if(right_vo.options.length>0){
			    	for(var j=0;j<right_vo.options.length;j++){
			    		if(right_vo.options[j].value==temps[i].split("/")[0])
			    		{	
			    			isExist=1;
			    			break;
			    		}
			    	}
		    	}
		    	if(isExist==1)
		    		continue;
		    	var no = new Option();
		    	no.value=temps[i].split("/")[0];
		    	no.text=temps[i].split("/")[1];
		    	right_vo.options[right_vo.options.length]=no;
		    }
		}
 	}
 	function delunit(){
		  vos= document.getElementsByName("right_fields");
		  if(vos==null)
		  	return false;
		  right_vo=vos[0];
		  var k=0;
		  for(i=right_vo.options.length-1;i>=0;i--)
		  {
		    if(right_vo.options[i].selected)
		    {
				right_vo.options.remove(i);
				k++;
		    }
		  }
		  if(k==0){
		  	alert("请选择删除单位！");
		  	return;
		  }
	
 	}
 	function goback()
	{
	
		returnValue="0";
		var win = parent.Ext.getCmp("reportApprove");
		if(win)
			win.close();
		else
			window.close();
	}
	function sub()
	{	
		var right=document.getElementsByName("right_fields");
		var right_vo=right[0];
		if(right_vo.options.length==0)
		{
			alert("请选择需要驳回的单位！");
			return;
		}
		setselectitem('right_fields');
		var rightfields="";
		for(i=right_vo.options.length-1;i>=0;i--){
		 	rightfields+=right_vo.options[i].value+",";
		}
		var tsort=document.getElementById("aa").value;
		var hashvo=new ParameterSet();
		hashvo.setValue("tsort",tsort); 
		hashvo.setValue("unitcode","<%=unitcode%>");
		hashvo.setValue("selectUnitcodes",rightfields);
		var In_paramters="flag=1"; 	
		var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:sucess,functionId:'03030000190'},hashvo);			
		
		//window.open("/report/report_collect/reportOrgCollecttree.do?b_writeDesc=description&flag=2&selfunitcode=<%=unitcode%>&tsort=" +tsort,
		//'_self','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,top=0,left=220,resizable=yes,width=430,height=360');
	}
	
	function sucess(outparamters){
		var info=outparamters.getValue("pass");
		var tsort=outparamters.getValue("tsort");
		var filter=outparamters.getValue("filter");
		if(filter=='false'){
			if(confirm("所选单位均已无上报状态报表是否继续驳回？"))
			{
			
			
			}else{
				return;
			}
		}
		if(info=="false"){
			alert("我已经上报该类报表，不允许由我去驳回，只能由我的上级驳回！");
			return;
		}else{
			document.getElementsByName("enter")[0].disabled=true;
			document.editReportForm.action="/report/report_collect/reportOrgCollecttree.do?b_writeDesc=description&flag=2&selfUnitcode=<%=unitcode%>&tsort=" +tsort+"&unitcode=<%=subunitcode%>";
			document.editReportForm.submit();
		}
		
	}
	function refresh(){
		var tsort;
		document.getElementById('treemenu').innerHTML='';
		
		Global={
			id:0,
			getId:function(){return this.id++;},
			all:[],
			selectedItem:null,
			defaultText:"",
			defaultAction:"javascript:void(0)",
			defaultTarget:"_blank",
			closeAction:"",         //关闭事件
			checkvalue:",",      //选中的值,可以根据选中的值，置checkbox radio的状态
			defaultInput:0,      //是否为0无,1　checkbox 2=radio default 无	
			showroot:true,     //根节点是否显示 checkbox,radio
			drag:false,
			showorg:0,//   =1为组织机构树,=0为其它树
			defaultchecklevel:0,   //showorg=1时  =all出现单选框,1=部门以下都出现,2=职位以下,3=只有人员出现
			/**check 级联选中*/
			cascade:false,	
			defaultradiolevel:0, //0=all出现单选框,1=部门以下都出现,2=职位以下,3=只有人员出现
			setDrag:function(bool){this.drag=bool;},
			isIE:navigator.appName.indexOf("Microsoft")!= -1		
		}
		var obj=document.getElementById('aa');
		for(var i=0;i<obj.options.length;i++){
			 if(obj.options[i].selected){
			 	tsort=obj.options[i].value;
			 }
		}
		vos= document.getElementsByName("right_fields");
		  if(vos==null)
		  	return false;
		  right_vo=vos[0];
		  for(i=right_vo.options.length-1;i>=0;i--)
		  {
				right_vo.options.remove(i);
		  }
	
		var m_sXMLFile= "search_rep_rej_unit_tree.jsp?unitcode=<%=subunitcode%>&selfunitcode=<%=unitcode%>&tsort="+tsort+"&init=1";
		var root=new xtreeItem("<%= unitcode%>","<%= unitname%>","","",REPORT_UNIT,"/images/unit.gif",m_sXMLFile);
		Global.defaultInput=1;   
		Global.showroot=false;
		root.setup(document.getElementById("treemenu"));
	}
 </script>
</head>
<body>

<html:form action="/report/edit_report/editReport">
<br>
<table width="90%" align="center" border="0" cellpadding="0" cellspacing="0" class="ListTable">
<thead>
		<tr>
		<td colspan="3" align="left" class="TableRow" nowrap >
		批量驳回
		</td>
		</tr>
	</thead>
<tr>
<td   width="100%"  align="center" class="TableRow" style="background-color:rgb(255,255,255);padding-right:0px;" nowrap> 
	<table  width="100%" border="0" cellspacing="1" align="center" cellpadding="1" class="ListTable">
	
	<tr>
	<td valign="top">
	<table width="100%" border="0" cellspacing="1" align="center" cellpadding="1" class="ListTable">
			<tr>
			<td valign="top">
			&nbsp;&nbsp;表类：
			</td>
			</tr>
			<tr>
				<td valign="top">
					<html:select styleId="aa" name="editReportForm" property="dmlsort"  style="height:20px;width:230px;font-size:9pt;margin-left:10px;"  onchange="refresh()">
					<%
						if(list!=null)
						for(int i=0;i<list.size();i++){
						LazyDynaBean bean=(LazyDynaBean)list.get(i);
						String value=(String)bean.get("dataValue");
						String name=(String)bean.get("dataName");
					 %>
						<html:option value="<%=value %>"><%= name%></html:option>
						<%} %>
					</html:select>
				</td>
			</tr>
			<tr>
				<td>
				  <div id="treemenu"></div> 
				</td>
			</tr>
		</table>
	
</td>
<td align="center">

 &nbsp;<html:button  styleClass="mybutton" property="b_addfield"  onclick='addunits();'  >
            		     <bean:message key="button.setfield.addfield"/> 
	   </html:button >&nbsp;
	    <br>
	    <br>
 &nbsp;<html:button  styleClass="mybutton" property="b_delfield" onclick='delunit();'  >
            		     <bean:message key="button.setfield.delfield"/>    
	  </html:button >&nbsp;
</td>

<td >
	<table>
	<tr>
	<td valign="top">
	已选单位：
	</td>
	</tr>
	<tr>
		<td valign="top">
		    <select name="right_fields" multiple="multiple"   style="height:230px;width:230px;font-size:10pt;margin-right:10px;margin-left:0px">
		    <% if(flag) { %>
		    <option value="<%=kvalue %>">
		    <%=text %>
		    </option>
		    <%}else{ %>
		    <%} %>
             </select>
             </td>
             </tr>
</table>
<br>
</td>
</tr>
</table>
</td>
</tr>
<tr>
<td colspan='4' align="center" style="padding:5px">
	 <html:button  styleClass="mybutton" property="enter" onclick='sub()'  >
            		     <bean:message key="button.ok"/>
	            </html:button >	
	      &nbsp; &nbsp;<html:button  styleClass="mybutton" property="cancel" onclick='goback()'  >
            		     <bean:message key="button.cancel"/>
	            </html:button >	 &nbsp;
</td>
</tr>
</table>
</html:form>
</body>
<SCRIPT LANGUAGE=javascript>
	var m_sXMLFile= "search_rep_rej_unit_tree.jsp?unitcode=<%=subunitcode%>&selfunitcode=<%=unitcode%>&tsort=<%=tsort%>&init=1";
	var root=new xtreeItem("<%= unitcode%>","<%= unitname%>","","",REPORT_UNIT,"/images/unit.gif",m_sXMLFile);
	Global.defaultInput=1;   
	Global.showroot=false;
	root.setup(document.getElementById("treemenu"));
</SCRIPT>
</html>