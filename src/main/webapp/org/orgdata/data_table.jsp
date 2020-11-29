<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.taglib.CommonData"%>
<%@ page import="com.hjsj.hrms.actionform.org.orgdata.OrgDataForm"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<script language="JavaScript" src="/js/common.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/numberS.js"></script>
<script language="JavaScript" src="./orgtable.js"></script>
<script language="JavaScript" src="/js/dict.js"></script>
<hrms:themes></hrms:themes> 
<style type="text/css"> 
.selectPre{
	position:absolute;
    left:380px;
    top:36px;
}
</style>
<%
OrgDataForm orgForm = (OrgDataForm)session.getAttribute("orgDataForm");
UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String bosflag="";
	if(userView != null){	  
	  	bosflag=userView.getBosflag();
	  	bosflag=bosflag!=null?bosflag:"";                
	}
%>
<html:form action="/org/orgdata/orgdata.do?b_rmain=link&infor=${orgDataForm.infor}&a_code=${orgDataForm.a_code}">
<html:hidden name="orgDataForm" property="sort_str"/>
<html:hidden name="orgDataForm" property="viewdata"/>
<html:hidden name="orgDataForm" property="viewsearch"/>
<html:hidden name="orgDataForm" property="infor"/>
<html:hidden name="orgDataForm" property="setname"/>
<logic:notEqual name="orgDataForm" property="infor" value="3">
<table><tr><td>
<table><tr><td>
<hrms:menubar menu="menu1" id="menubar1">
  <hrms:menuitem name="unit1" label="infor.menu.edit" function_id="230650101,230650102">
  <%if(orgForm.getCheckadd().equals("1")){%>
    <hrms:menuitem name="madd" label="infor.menu.new" function_id="230650101"  icon="/images/quick_query.gif" url="insert('${orgDataForm.tablename}','${orgDataForm.a_code}','add');" command="" />
   <%} %> 
    <hrms:menuitem name="m3" label="infor.menu.del" function_id="230650102"  icon="/images/del.gif" url="" command="delselected" hint="general.inform.search.confirmed.del" /> 
  </hrms:menuitem>  
  <hrms:menuitem name="unit2" label="infor.menu.bat" function_id="2306503,2306504,2306505,2306508">
      	<hrms:menuitem name="mitem1" label="infor.menu.batupdate_s" function_id="2306504" icon="/images/add_del.gif" url="batchHand(1,'${orgDataForm.a_code}','${orgDataForm.infor}');" command="" enabled="true" visible="true"/>
      	<hrms:menuitem name="mitem2" label="infor.menu.batupdate_m" function_id="2306505" icon="/images/write.gif" url="batchHand(2,'${orgDataForm.a_code}','${orgDataForm.infor}');" command="" enabled="true" visible="true"/>
    	<hrms:menuitem name="mitem5" label="infor.menu.compute" function_id="2306503" icon="" url="batchCond('${orgDataForm.a_code}');" command="" enabled="true" visible="true"/>
  	 	<hrms:menuitem name="mitem6" label="org.gzdatamaint.gzdatamaint.singlecheck" function_id="2306508" icon="/images/quick_query.gif" url="singleAudit();" command="" enabled="true" visible="true"/>
  </hrms:menuitem>  
   <hrms:menuitem name="gz5" label="infor.menu.print" function_id="2306509,2306510,2306511">
      <hrms:menuitem name="m1" label="infor.menu.outmuster" function_id="2306509" icon="/images/print.gif" url="" command="" enabled="true" visible="true">
	      <hrms:menuitem name="m11" label="infor.menu.display.data" icon="" url="printInform(1,'','${orgDataForm.a_code}','${orgDataForm.infor}','${orgDataForm.viewsearch}');" command="" enabled="true" visible="true"/>
    	  <hrms:menuitem name="m12" label="infor.menu.query.data" icon="" url="printInform(1,'','${orgDataForm.a_code}','${orgDataForm.infor}','2');" command="" enabled="true" visible="true"/>
      </hrms:menuitem>
      <hrms:menuitem name="m2" label="infor.menu.outcard" function_id="2306510" icon="/images/print.gif" url="" command="" enabled="true" visible="true">
	      <hrms:menuitem name="m20" label="infor.menu.select.data" icon="" url="printInform(2,'${orgDataForm.tablename}','${orgDataForm.a_code}','${orgDataForm.infor}',2);" command="" enabled="true" visible="true"/>
	      <hrms:menuitem name="m21" label="infor.menu.display.data" icon="" url="printInform(2,'${orgDataForm.tablename}','${orgDataForm.a_code}','${orgDataForm.infor}','${orgDataForm.viewsearch}');" command="" enabled="true" visible="true"/>
    	  <hrms:menuitem name="m22" label="infor.menu.query.data" icon="" url="printInform(2,'${orgDataForm.tablename}','${orgDataForm.a_code}','${orgDataForm.infor}','1');" command="" enabled="true" visible="true"/>
      </hrms:menuitem>
      <hrms:menuitem name="m3" label="infor.menu.outhmuster" function_id="2306511" icon="/images/print.gif" url="" command="" enabled="true" visible="true">
	      <hrms:menuitem name="m31" label="infor.menu.display.data" icon="" url="printInform(3,'','${orgDataForm.a_code}','${orgDataForm.infor}','${orgDataForm.viewsearch}');" command="" enabled="true" visible="true"/>
    	  <hrms:menuitem name="m32" label="infor.menu.query.data" icon="" url="printInform(3,'','${orgDataForm.a_code}','${orgDataForm.infor}','2');" command="" enabled="true" visible="true"/>
      </hrms:menuitem>      
  </hrms:menuitem> 
  <hrms:menuitem name="unit3" label="infor.menu.query" function_id="2306512,2306513,2306514">
      <hrms:menuitem name="mitem1" label="infor.menu.squery" function_id="2306512" icon="" url="searchInform('${orgDataForm.infor}',1,'${orgDataForm.a_code}','${orgDataForm.tablename}');" command="" enabled="true" visible="true"/>
      <hrms:menuitem name="mitem2" label="infor.menu.hquery" function_id="2306513" icon="" url="searchInform('${orgDataForm.infor}',2,'${orgDataForm.a_code}','${orgDataForm.tablename}');" command="" enabled="true" visible="true"/>
       <hrms:menuitem name="mitem3" label="infor.menu.gquery" function_id="2306514" icon="" url="">
      	<%int n=4;%>
      	<logic:iterate id="element"  name="orgDataForm"  property="searchlist" indexId="index">  
      		 <%
            	CommonData searhcitem=(CommonData)pageContext.getAttribute("element");
            	String searchname=searhcitem.getDataValue();
            	String id=searhcitem.getDataName();
            	String a_code = (String)request.getParameter("a_code");
            	
            	String searchgeneral = "searchGeneral("+orgForm.getInfor()+","+id+",'"+a_code+"','"+orgForm.getSetname()+"');";
            %>
      		<hrms:menuitem name='<%="mitem"+n+""%>' label='<%=searchname%>' icon="" url="<%=searchgeneral%>" command="" enabled="true" visible="true"/>
      		<%n++;%>
      	</logic:iterate>
      	<%if(n>10){%>
      	<hrms:menuitem name='<%="mitem"+(n+1)+""%>' label='general.inform.search.themore' icon="" url="searchInform('${orgDataForm.infor}',3,'${orgDataForm.a_code}','${orgDataForm.tablename}');" command="" enabled="true" visible="true"/>
      	<%} %>
      	</hrms:menuitem>
      	<logic:equal name="orgDataForm" property="viewsearch" value="1">
      	 <hrms:menuitem name="mitem100" label="general.inform.search.view.result" icon="" url="searchOk(0);" checked="true" groupindex="1"/>
  	  </logic:equal>
  	  <logic:equal name="orgDataForm" property="viewsearch" value="0">
  	  	<hrms:menuitem name="mitem100" label="general.inform.search.view.result" icon="" url="searchOk(1);" groupindex="1"/>
  	  </logic:equal>
  </hrms:menuitem>  
  <hrms:menuitem name="unit4" label="infor.menu.view" function_id="2306516,2306517">
      <hrms:menuitem name="show2" label="infor.menu.hide" function_id="2306516" icon="" url="to_hide_field('${orgDataForm.setname}');" command=""/>
      <hrms:menuitem name="show3" label="infor.menu.sortitem" function_id="2306517" icon="" url="to_sort_field('${orgDataForm.setname}');" command=""/>
  </hrms:menuitem>   
</hrms:menubar>
</td></tr></table>
</td>
<td>&nbsp;
</td>
</tr>
</table>
<hrms:dataset name="orgDataForm" property="itemlist" scope="session" setname="${orgDataForm.tablename}"  
setalias="data_table" readonly="false" editable="true" select="true" 
sql="${orgDataForm.selectsql}" pagerows="${orgDataForm.pagerows}"  buttons="movefirst,prevpage,nextpage,movelast">
   <%if(orgForm.getCheckadd().equals("1")&&orgForm.getPriItem().equals("2")){%>
   <hrms:commandbutton name="tableadd" hint="" function_id="230650101" functionId="" refresh="true" type="selected" setname="${orgDataForm.tablename}" onclick="insert('${orgDataForm.tablename}','${orgDataForm.a_code}','add');">
     <bean:message key="button.insert"/>
   </hrms:commandbutton>
   <%}if(orgForm.getPriItem().equals("2")){%>
    <hrms:commandbutton name="delselected" function_id="230650201" hint="general.inform.search.confirmed.del" functionId="0401000042" refresh="true" type="selected" setname="${orgDataForm.tablename}">
     <bean:message key="button.delete"/>
   </hrms:commandbutton>
   <%}%>
   <hrms:commandbutton name="compute" function_id="2306503" functionId="" refresh="true" type="selected" setname="${orgDataForm.tablename}" onclick="batchCond('${orgDataForm.a_code}');" >
     <bean:message key="button.computer"/>
   </hrms:commandbutton>
   <hrms:commandbutton name="lietable" functionId="" refresh="true" type="selected" setname="${orgDataForm.tablename}" onclick="lieTable();" >
     <bean:message key="kjg.title.listtable"/>
   </hrms:commandbutton> 
   <%if(bosflag.equals("hl")){%>
   <hrms:commandbutton name="firstpage" function_id="" hint="" functionId="" visible="true" refresh="true" type="selected" setname="${orgDataForm.tablename}" onclick="returnFirst();" >
     <bean:message key="reportcheck.return"/>
   </hrms:commandbutton> 
   <%}%>         
</hrms:dataset>
</logic:notEqual>
<logic:equal name="orgDataForm" property="infor" value="3">
<table><tr><td>
<table><tr><td>
<hrms:menubar menu="menu1" id="menubar1">
  <hrms:menuitem name="unit1" label="infor.menu.edit" >
  <%if(orgForm.getCheckadd().equals("1")){%>
    <hrms:menuitem name="madd" label="infor.menu.new" function_id="250650101"  icon="/images/quick_query.gif" url="insert('${orgDataForm.tablename}','${orgDataForm.a_code}','add');" command="" />
   <%} %> 
    <hrms:menuitem name="m3" label="infor.menu.del" function_id="250650102"  icon="/images/del.gif" url="" command="delselected" hint="general.inform.search.confirmed.del" /> 
  </hrms:menuitem>  
  <hrms:menuitem name="unit2" label="infor.menu.bat" function_id="2506503,2506504,2506505,2506508">
      	<hrms:menuitem name="mitem1" label="infor.menu.batupdate_s" function_id="2506504" icon="/images/add_del.gif" url="batchHand(1,'${orgDataForm.a_code}','${orgDataForm.infor}');" command="" enabled="true" visible="true"/>
      	<hrms:menuitem name="mitem2" label="infor.menu.batupdate_m" function_id="2506505" icon="/images/write.gif" url="batchHand(2,'${orgDataForm.a_code}','${orgDataForm.infor}');" command="" enabled="true" visible="true"/>
    	<hrms:menuitem name="mitem5" label="infor.menu.compute" function_id="2506503" icon="" url="batchCond('${orgDataForm.a_code}');" command="" enabled="true" visible="true"/>
  	 	<hrms:menuitem name="mitem6" label="org.gzdatamaint.gzdatamaint.singlecheck" function_id="2506508" icon="/images/quick_query.gif" url="singleAudit();" command="" enabled="true" visible="true"/>
  </hrms:menuitem>  
   <hrms:menuitem name="gz5" label="infor.menu.print" function_id="2506509,2506510,2506511">
      <hrms:menuitem name="m1" label="infor.menu.outmuster" function_id="2506509" icon="/images/print.gif" url="" command="" enabled="true" visible="true">
	      <hrms:menuitem name="m11" label="infor.menu.display.data" icon="" url="printInform(1,'','${orgDataForm.a_code}','${orgDataForm.infor}','${orgDataForm.viewsearch}');" command="" enabled="true" visible="true"/>
    	  <hrms:menuitem name="m12" label="infor.menu.query.data" icon="" url="printInform(1,'','${orgDataForm.a_code}','${orgDataForm.infor}','2');" command="" enabled="true" visible="true"/>
      </hrms:menuitem>
      <hrms:menuitem name="m2" label="infor.menu.outcard" function_id="2506510" icon="/images/print.gif" url="" command="" enabled="true" visible="true">
	      <hrms:menuitem name="m20" label="infor.menu.select.data" icon="" url="printInform(2,'${orgDataForm.tablename}','${orgDataForm.a_code}','${orgDataForm.infor}',2);" command="" enabled="true" visible="true"/>
	      <hrms:menuitem name="m21" label="infor.menu.display.data" icon="" url="printInform(2,'${orgDataForm.tablename}','${orgDataForm.a_code}','${orgDataForm.infor}','${orgDataForm.viewsearch}');" command="" enabled="true" visible="true"/>
    	  <hrms:menuitem name="m22" label="infor.menu.query.data" icon="" url="printInform(2,'${orgDataForm.tablename}','${orgDataForm.a_code}','${orgDataForm.infor}','1');" command="" enabled="true" visible="true"/>
      </hrms:menuitem>
      <hrms:menuitem name="m3" label="infor.menu.outhmuster" function_id="2506511" icon="/images/print.gif" url="" command="" enabled="true" visible="true">
	      <hrms:menuitem name="m31" label="infor.menu.display.data" icon="" url="printInform(3,'','${orgDataForm.a_code}','${orgDataForm.infor}','${orgDataForm.viewsearch}');" command="" enabled="true" visible="true"/>
    	  <hrms:menuitem name="m32" label="infor.menu.query.data" icon="" url="printInform(3,'','${orgDataForm.a_code}','${orgDataForm.infor}','2');" command="" enabled="true" visible="true"/>
      </hrms:menuitem>      
  </hrms:menuitem> 
  <hrms:menuitem name="unit3" label="infor.menu.query" function_id="2506512,2506513,2506514">
      <hrms:menuitem name="mitem1" label="infor.menu.squery" function_id="2506512" icon="" url="searchInform('${orgDataForm.infor}',1,'${orgDataForm.a_code}','${orgDataForm.tablename}');" command="" enabled="true" visible="true"/>
      <hrms:menuitem name="mitem2" label="infor.menu.hquery" function_id="2506513" icon="" url="searchInform('${orgDataForm.infor}',2,'${orgDataForm.a_code}','${orgDataForm.tablename}');" command="" enabled="true" visible="true"/>
       <hrms:menuitem name="mitem3" label="infor.menu.gquery" function_id="2506514" icon="" url="">
      	<%int m=4;%>
      	<logic:iterate id="element"  name="orgDataForm"  property="searchlist" indexId="index">  
      		 <%
            	CommonData searhcitem=(CommonData)pageContext.getAttribute("element");
            	String searchname=searhcitem.getDataValue();
            	String id=searhcitem.getDataName();
            	String a_code = (String)request.getParameter("a_code");
            	
            	String searchgeneral = "searchGeneral("+orgForm.getInfor()+","+id+",'"+a_code+"','"+orgForm.getSetname()+"');";
            %>
      		<hrms:menuitem name='<%="mitem"+m+""%>' label='<%=searchname%>' icon="" url="<%=searchgeneral%>" command="" enabled="true" visible="true"/>
      		<%m++;%>
      	</logic:iterate>
      	<%if(m>10){%>
      	<hrms:menuitem name='<%="mitem"+(m+1)+""%>' label='general.inform.search.themore' icon="" url="searchInform('${orgDataForm.infor}',3,'${orgDataForm.a_code}','${orgDataForm.tablename}');" command="" enabled="true" visible="true"/>
      	<%} %>
      	</hrms:menuitem>
      	<logic:equal name="orgDataForm" property="viewsearch" value="1">
      	 <hrms:menuitem name="mitem100" label="general.inform.search.view.result" icon="" url="searchOk(0);" checked="true" groupindex="1"/>
  	  </logic:equal>
  	  <logic:equal name="orgDataForm" property="viewsearch" value="0">
  	  	<hrms:menuitem name="mitem100" label="general.inform.search.view.result" icon="" url="searchOk(1);" groupindex="1"/>
  	  </logic:equal>
  </hrms:menuitem>  
  <hrms:menuitem name="unit4" label="infor.menu.view" function_id="2506516,2506517">
      <hrms:menuitem name="show2" label="infor.menu.hide" function_id="2506516" icon="" url="to_hide_field('${orgDataForm.setname}');" command=""/>
      <hrms:menuitem name="show3" label="infor.menu.sortitem" function_id="2506517" icon="" url="to_sort_field('${orgDataForm.setname}');" command=""/>
  </hrms:menuitem>   
</hrms:menubar>
</td></tr></table>
</td>
<td>&nbsp;
</td>
</tr>
</table>
<hrms:dataset name="orgDataForm" property="itemlist" scope="session" setname="${orgDataForm.tablename}"  
setalias="data_table" readonly="false" editable="true" select="true" 
sql="${orgDataForm.selectsql}" pagerows="${orgDataForm.pagerows}"  buttons="movefirst,prevpage,nextpage,movelast">
   <%if(orgForm.getCheckadd().equals("1")&&orgForm.getPriItem().equals("2")){%>
   <hrms:commandbutton name="tableadd" hint="" function_id="250650101" functionId="" refresh="true" type="selected" setname="${orgDataForm.tablename}" onclick="insert('${orgDataForm.tablename}','${orgDataForm.a_code}','add');">
     <bean:message key="button.insert"/>
   </hrms:commandbutton>
   <%}if(orgForm.getPriItem().equals("2")){%>
    <hrms:commandbutton name="delselected" function_id="250650201" hint="general.inform.search.confirmed.del" functionId="0401000042" refresh="true" type="selected" setname="${orgDataForm.tablename}">
     <bean:message key="button.delete"/>
   </hrms:commandbutton>
   <%}%>
   <hrms:commandbutton name="compute" function_id="2506503" functionId="" refresh="true" type="selected" setname="${orgDataForm.tablename}" onclick="batchCond('${orgDataForm.a_code}');" >
     <bean:message key="button.computer"/>
   </hrms:commandbutton>
   <hrms:commandbutton name="lietable" functionId="" refresh="true" type="selected" setname="${orgDataForm.tablename}" onclick="lieTable();" >
     <bean:message key="kjg.title.listtable"/>
   </hrms:commandbutton> 
   <%if(bosflag.equals("hl")){%>
   <hrms:commandbutton name="firstpage" function_id="" hint="" functionId="" visible="true" refresh="true" type="selected" setname="${orgDataForm.tablename}" onclick="returnFirst();" >
     <bean:message key="reportcheck.return"/>
   </hrms:commandbutton> 
   <%}%>         
</hrms:dataset>
</logic:equal>
<html:hidden name="orgDataForm" property="a_code"/>
<input type="button" name="testbutton" value="a" style="width:1px;height:1px;display:none"/>
</html:form>

<script language="javascript">
function ${orgDataForm.tablename}_afterChange(dataset,field,value){
	var field_name=field.getName();
	var record,pfield;
	record=dataset.getCurrent(); 
	if(field_name=='select')
		return;
	var infor = "${orgDataForm.infor}";
	var mainset = "B01";
	var a0100 = "";
	if(infor=='2'){
		a0100 = record.getValue("b0110");
	}else if(infor=='3'){
		a0100 = record.getValue("e01a1");
		mainset = "K01";
	}
	var tablename = "${orgDataForm.tablename}";
	var fieldvalue = record.getValue(field_name);
	if(field.getDataType()=='date'&&fieldvalue!=null&&fieldvalue!=""){
		var date=new Date(); 
		date.setTime(fieldvalue); 
		var month =  date.getMonth();
		var year =  date.getFullYear();
		if(month>11){
			month=1;
			year+=1;
		}else{
			month+=1;
		}
		fieldvalue = year+"-"+month+"-"+date.getDate();
	}

	var hashvo=new ParameterSet();
	hashvo.setValue("fieldvalue",getEncodeStr(fieldvalue));  
	hashvo.setValue("itemid",field_name);	
	hashvo.setValue("tablename",tablename);  
	hashvo.setValue("a0100",a0100);  
	hashvo.setValue("inforflag","${orgDataForm.infor}");  
	if(tablename.indexOf(mainset)==-1&&tablename.indexOf(mainset.toLowerCase())==-1){
		var i9999 = record.getValue("i9999");
		hashvo.setValue("i9999",i9999);  
	}
	var request=new Request({method:'post',asynchronous:false,functionId:"1010090011"},hashvo);
	if(tablename.indexOf(mainset)==-1&&tablename.indexOf(mainset.toLowerCase())==-1){
		var i9999 = record.getValue("i9999");
		if(i9999==null||i9999.length<1)
			record.setValue("i9999","1");
	}
} 
var oldrecord;
function table${orgDataForm.tablename}_onRowClick(table){
	var getablename = "${orgDataForm.tablename}";
	var reserveitem = "${orgDataForm.reserveitem}";
	var dataset=table.getDataset();	
   	var record=dataset.getCurrent();
   	if(!record)
	    return;
	/**必添项处理*/
	if(oldrecord&&record!=oldrecord){
		var arr = reserveitem.split("`");
		var itemvalues="";
		var checkflag="";
		for(var i=0;i<arr.length;i++){
			if(arr[i]!=null&&arr[i].length>0){
				var item_arr = arr[i].split(",.");
				if(item_arr!=null&&item_arr.length==2){
					itemvalues=oldrecord.getValue(item_arr[0]); 
					if(itemvalues==null||itemvalues.length<1){
						checkflag = item_arr[1]+"为必填项!";
						break;
					}
				}
			}
		}
		if(checkflag!=null&&checkflag.length>3){
			dataset.setCurrent(oldrecord); 
			alert(checkflag);
			return false;
		}  
	}
	if(oldrecord&&record==oldrecord){
		return false;
	}
	oldrecord = record;
   var infor = "${orgDataForm.infor}";
	var a0100 = "";
	if(infor=='2'){
		a0100 = record.getValue("B0110");
	}else if(infor=='3'){
		a0100 = record.getValue("E01A1");
		mainset = "K01";
	}
	if(a0100==null||a0100.length<1){
		var acode = "${orgDataForm.a_code}";
		a0100=acode.substring(2);
	}
	parent.ril_body2.location="/org/orgdata/orgdata.do?b_menu=link&itemid="+a0100+"&infor=${orgDataForm.infor}";
}
document.body.onbeforeunload=function(){ 
	var target = document.getElementById("testbutton");
	target.style.display = "block";
	target.focus();
	target.style.display="none"; 
}
function lieTable(){
	self.parent.location = "/org/orgdata/org_tree.do?b_init=link&infor=${orgDataForm.infor}";
}
function table${orgDataForm.tablename}_oper_onRefresh(cell,value,record){		
	if(record!=null)	
		cell.innerHTML="<img src=\"/images/edit.gif\" border=\"0\" onclick=\"edit('${orgDataForm.tablename}','${orgDataForm.a_code}','${orgDataForm.infor}')\" style=\"cursor:hand;\">";
}
parent.ril_body2.location="/templates/welcome/welcome.html";
</script>
