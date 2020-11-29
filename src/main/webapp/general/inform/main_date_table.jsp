<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.taglib.CommonData"%>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<%@ page import="com.hjsj.hrms.actionform.general.inform.MInformForm"%>
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
  <link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/control.js"></script>
<script language="javascript" src="/ajax/dataset.js"></script>
<script language="javascript" src="/ajax/editor.js"></script>
<script language="javascript" src="/ajax/dropdown.js"></script>
<script language="javascript" src="/ajax/table.js"></script>
<script language="javascript" src="/ajax/menu.js"></script>
<script language="javascript" src="/ajax/tree.js"></script>
<script language="javascript" src="/ajax/pagepilot.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<script language="javascript" src="/js/validate.js"></script>
<script type="text/javascript" src="/js/dict.js"></script>
<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script type="text/javascript" src="/js/popcalendar.js"></script>
<%
	MInformForm mInformForm = (MInformForm)session.getAttribute("mInformForm");
	int i=0;
	String userdb = "";
	int flag=1;
	String webserver=SystemConfig.getPropertyValue("webserver");
	if(webserver.equalsIgnoreCase("websphere"))
		flag=2;
%>
<script language="javascript">
    var _checkBrowser=true;
	var _disableSystemContextMenu=false;
	var _processEnterAsTab=true;
	var _showDialogOnLoadingData=true;
	var _enableClientDebug=true;
	var _theme_root="/ajax/images";
	var _application_root="";
	var __viewInstanceId="968";
	var ViewProperties=new ParameterSet();
	var webserver=<%=flag%>;
	
</script>
<hrms:themes></hrms:themes>
<style type="text/css"> 
.viewPhoto{
     position:absolute;
     left:200px;
     top:100px;
     z-index:20;
     background-color:#FFFFCC;
     overflow:visible;
}
.selectPre{
	position:absolute;
    left:500px;
    top:35px;
}
.appblack {
	BORDER-BOTTOM: #94B6E6 0pt solid; 
	BORDER-LEFT: #94B6E6 0pt solid; 
	BORDER-RIGHT: #94B6E6 0pt solid; 
	BORDER-TOP: #94B6E6 0pt solid;
	border-collapse:separate;
	font-size: 12px;
 	background-image:url(/images/mainbg.jpg);
}
</style>

<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/general/inform/iteminfor.js"></script>
<script language="javascript" src="/general/inform/photo.js"></script>
<script language="javascript" src="/js/dict.js"></script> 
<html:form action="/general/inform/get_data_table">
<html:hidden name="mInformForm" property="a_code"/>
<html:hidden name="mInformForm" property="sort_str" />
<html:hidden name="mInformForm" property="sort_record_scope" />
<html:hidden name="mInformForm" property="a0100" />
<html:hidden name="mInformForm" property="viewdata" />
<table><tr><td>
<table><tr><td>
<hrms:menubar menu="menu1" id="menubar1">
  <hrms:menuitem name="gz0" label="infor.menu.base">
    	<logic:iterate id="element"  name="mInformForm"  property="dblist" indexId="index">
            <%
            	CommonData item=(CommonData)pageContext.getAttribute("element");
            	String pre=item.getDataValue();
            	String name=item.getDataName();
            	String jsfunc="reloadBybase(\""+pre+"\");";
            	++i;
            %>        	
	             <logic:equal name="mInformForm" property="dbname" value="<%=pre%>">		
						<hrms:menuitem name='<%="mitem" + i%>' label="<%=name%>" checked="true" groupindex="1" url="<%=jsfunc%>"  />
						<%userdb=name;%>
				 </logic:equal>
				 <logic:notEqual name="mInformForm" property="dbname" value="<%=pre%>">	
				 		<hrms:menuitem name='<%="mitem" + i%>' label="<%=name%>" groupindex="1" url="<%=jsfunc%>"  />
				 </logic:notEqual>            
   		</logic:iterate>   
   		<logic:equal name="mInformForm"  property="prive" value="2"> 
        <hrms:menuitem name="m3" function_id="2606401" label="infor.menu.transbase" enabled="${mInformForm.viewbutton}" icon="/images/link.gif" url="shiftlabrary('${mInformForm.dbname}','${mInformForm.tablename}','${mInformForm.a_code}')"/>              
  		</logic:equal>
  </hrms:menuitem>
  <hrms:menuitem name="gz1" label="infor.menu.edit" function_id="2606402,2606405,2606403,2606404">
    <hrms:menuitem name="m1" function_id="2606402" label="infor.menu.new" enabled="${mInformForm.viewbutton}" icon="/images/quick_query.gif" url="append('${mInformForm.tablename}','${mInformForm.a_code}');" command="" />
    <hrms:menuitem name="m2" function_id="2606403" label="infor.menu.ins" enabled="${mInformForm.viewbutton}" icon="/images/deal.gif" url="insert('${mInformForm.tablename}','${mInformForm.a_code}');" command="" />  
    <hrms:menuitem name="m3" function_id="2606404" label="infor.menu.del" enabled="${mInformForm.viewbutton}" icon="/images/del.gif" url="" command="delselected" hint="general.inform.search.confirmed.del" />  
    <hrms:menuitem name="m4" function_id="2606405" label="infor.menu.move" icon="/images/quick_query.gif" url="to_move_record('${mInformForm.tablename}');" command="" />  
  </hrms:menuitem>  
  <hrms:menuitem name="gz2" label="infor.menu.bat" function_id="2606406,2606407,2606408,2606409,2606410,2606411,2606411">
      <hrms:menuitem name="mitem1" function_id="2606406" label="infor.menu.batupdate_s" icon="/images/add_del.gif" url="batchHand(1,'${mInformForm.a_code}','${mInformForm.dbname}','${mInformForm.viewsearch}');" command="" enabled="true" visible="true"/>
      <hrms:menuitem name="mitem2" function_id="2606407" label="infor.menu.batupdate_m" icon="/images/write.gif" url="batchHand(2,'${mInformForm.a_code}','${mInformForm.dbname}','${mInformForm.viewsearch}');" command="" enabled="true" visible="true"/>
      <logic:equal name="mInformForm" property="inforflag" value="1">
      <hrms:menuitem name="mitem5" function_id="2606410" label="infor.menu.compute" icon="" url="batchHand(5,'${mInformForm.a_code}','${mInformForm.dbname}','${mInformForm.viewsearch}');" command="" enabled="true" visible="true"/>
  	  <hrms:menuitem name="mitem6" function_id="2606411" label="org.gzdatamaint.gzdatamaint.singlecheck" icon="/images/quick_query.gif" url="singleAudit(1,'${mInformForm.dbname}');" command="" enabled="true" visible="true"/>
  	 </logic:equal>
  </hrms:menuitem>  
  <hrms:menuitem name="gz3" label="infor.menu.query">
      <hrms:menuitem name="mitem1" label="infor.menu.squery" icon="" url="searchInform(1,1,'${mInformForm.a_code}','${mInformForm.dbname}');" command="" enabled="true" visible="true"/>
      <hrms:menuitem name="mitem2" label="infor.menu.hquery" icon="" url="searchInform(1,2,'${mInformForm.a_code}','${mInformForm.dbname}');" command="" enabled="true" visible="true"/>
      <hrms:menuitem name="mitem3" label="infor.menu.gquery" icon="" url="">
      	<%int n=4;%>
      	<logic:iterate id="element"  name="mInformForm"  property="searchlist" indexId="index">  
      		 <%
            	CommonData searhcitem=(CommonData)pageContext.getAttribute("element");
            	String searchname=searhcitem.getDataValue();
            	String id=searhcitem.getDataName();
            	String a_code = (String)request.getParameter("a_code");
            	String searchgeneral = "searchGeneral(1,"+id+",'"+a_code+"','"+mInformForm.getDbname()+"');";
            %>
      		<hrms:menuitem name='<%="mitem"+n%>' label='<%=searchname%>' icon="" url="<%=searchgeneral%>" command="" enabled="true" visible="true"/>
      		<%n++;%>
      	</logic:iterate>
      	<hrms:menuitem name='<%="mitem"+(n+1)%>' label='general.inform.search.themore' icon="" url="searchInform(1,3,'${mInformForm.a_code}','${mInformForm.dbname}');" command="" enabled="true" visible="true"/>
      </hrms:menuitem>
      <logic:equal name="mInformForm" property="viewsearch" value="1">
      	 <hrms:menuitem name="mitem100" label="general.inform.search.view.result" icon="" url="searchOk(0);" checked="true" groupindex="1"/>
  	  </logic:equal>
  	  <logic:equal name="mInformForm" property="viewsearch" value="0">
  	  	<hrms:menuitem name="mitem100" label="general.inform.search.view.result" icon="" url="searchOk(1);" groupindex="1"/>
  	  </logic:equal>
  </hrms:menuitem>  
   <hrms:menuitem name="gz5" label="infor.menu.print">
      <hrms:menuitem name="m1" function_id="2606412" label="infor.menu.outmuster" icon="/images/print.gif" url="" command="" enabled="true" visible="true">
	      <hrms:menuitem name="m11" label="infor.menu.display.data" icon="" url="printInform(1,'${mInformForm.dbname}','${mInformForm.a_code}',1,'${mInformForm.viewsearch}');" command="" enabled="true" visible="true"/>
    	  <hrms:menuitem name="m12" label="infor.menu.query.data" icon="" url="printInform(1,'${mInformForm.dbname}','${mInformForm.a_code}',1,'2');" command="" enabled="true" visible="true"/>
      </hrms:menuitem>
      <hrms:menuitem name="m2" function_id="2606413" label="infor.menu.outcard" icon="/images/print.gif" url="" command="" enabled="true" visible="true">
	      <hrms:menuitem name="m20" label="infor.menu.select.data" icon="" url="printInform(2,'${mInformForm.tablename}','${mInformForm.a_code}',1,2);" command="" enabled="true" visible="true"/>
	      <hrms:menuitem name="m21" label="infor.menu.display.data" icon="" url="printInform(2,'${mInformForm.tablename}','${mInformForm.a_code}',1,'${mInformForm.viewsearch}');" command="" enabled="true" visible="true"/>
    	  <hrms:menuitem name="m22" label="infor.menu.query.data" icon="" url="printInform(2,'${mInformForm.tablename}','${mInformForm.a_code}',1,'1');" command="" enabled="true" visible="true"/>
      </hrms:menuitem>
      <hrms:menuitem name="m3" function_id="2606414" label="infor.menu.outhmuster" icon="/images/print.gif" url="" command="" enabled="true" visible="true">
	      <hrms:menuitem name="m31" label="infor.menu.display.data" icon="" url="printInform(3,'${mInformForm.dbname}','${mInformForm.a_code}',1,'${mInformForm.viewsearch}');" command="" enabled="true" visible="true"/>
    	  <hrms:menuitem name="m32" label="infor.menu.query.data" icon="" url="printInform(3,'${mInformForm.dbname}','${mInformForm.a_code}',1,'2');" command="" enabled="true" visible="true"/>
      </hrms:menuitem>      
  </hrms:menuitem> 
  <hrms:menuitem name="gz4" label="infor.menu.view" function_id="2606415,2606416,2606417,2606418,2606419,2606420">
      <hrms:menuitem name="mitem1" function_id="2606415" label="infor.menu.picture" icon="/images/man.gif" url="diplaypicture('${mInformForm.tablename}');" command="" enabled="true" visible="true"/>
      <logic:equal name="mInformForm" property="inforflag" value="1"> 
      <hrms:menuitem name="mitem2" function_id="2606416" label="infor.menu.media" icon="/images/prop_ps.gif" url="to_multimedia_tree('${mInformForm.tablename}');" command="" enabled="true" visible="true"/>
      </logic:equal> 
      <hrms:menuitem name="mitem3" function_id="2606417" label="infor.menu.msort" icon="/images/sort.gif" url="to_sort_main_info();" command="" enabled="true" visible="true"/>
      <logic:equal value="A01" name="mInformForm" property="setname">
      		<hrms:menuitem name="mitem4" function_id="2606418" label="infor.menu.ssort" icon="/images/sort.gif" url="to_sort_subset_info('${mInformForm.tablename}');" command="" enabled="false" visible="true"/>
      </logic:equal>
      <logic:notEqual value="A01" name="mInformForm" property="setname">
      		<hrms:menuitem name="mitem4" function_id="2606418" label="infor.menu.ssort" icon="/images/sort.gif" url="to_sort_subset_info('${mInformForm.tablename}');" command="" enabled="true" visible="true"/>
      </logic:notEqual>     
      <hrms:menuitem name="mitem5" function_id="2606419" label="infor.menu.hide" icon="" url="to_hide_field();" command="" enabled="true" visible="true"/>
      <hrms:menuitem name="mitem6" function_id="2606420" label="infor.menu.sortitem" icon="/images/sort.gif" url="to_sort_field();" command="" enabled="true" visible="true"/>
  		<logic:notEqual name="mInformForm"  property="prive" value="2"> 
  		<logic:equal name="mInformForm" property="viewdata" value="1">
      		<hrms:menuitem name="show4" label="org.gzdatamaint.gzdatamaint.viewdata" icon="" url="viewRecord('0');" checked="true" command=""/>
      	</logic:equal>
      	<logic:notEqual name="mInformForm" property="viewdata" value="1">
      		<hrms:menuitem name="show4" label="org.gzdatamaint.gzdatamaint.viewdata" icon="" url="viewRecord('1');" command=""/>
      	</logic:notEqual>
      </logic:notEqual>
  </hrms:menuitem>   
</hrms:menubar>
</td></tr></table>
</td>
<td>&nbsp;
</td>
</tr>
</table>
<hrms:dataset name="mInformForm" property="fieldlist" scope="session" setname="${mInformForm.tablename}"  
setalias="data_table" readonly="false" rowlock="true" editable="true" select="true" 
sql="${mInformForm.sql}" pagerows="${mInformForm.pagerows}" buttons="bottom">
   <hrms:commandbutton name="table" function_id="2606402" hint="" functionId="" visible="${mInformForm.viewbutton}" refresh="true" type="selected" setname="${mInformForm.tablename}" onclick="append('${mInformForm.tablename}','${mInformForm.a_code}');" >
     <bean:message key="button.insert"/> 
   </hrms:commandbutton>
   <hrms:commandbutton name="tableinsert" function_id="2606403" hint="" functionId="" visible="${mInformForm.viewbutton}" refresh="true" type="selected" setname="${mInformForm.tablename}" onclick="insert('${mInformForm.tablename}','${mInformForm.a_code}');" >
    <bean:message key="button.new.insert"/>
   </hrms:commandbutton>
   <hrms:commandbutton name="edit" function_id="2606402" hint="" functionId="" visible="true" refresh="true" type="selected" setname="${mInformForm.tablename}" onclick="edit('${mInformForm.tablename}','${mInformForm.a_code}');" >
     <bean:message key="label.kh.edit"/>
   </hrms:commandbutton>  
    <hrms:commandbutton name="delselected" function_id="2606404" hint="general.inform.search.confirmed.del" visible="${mInformForm.viewbutton}" functionId="1010090003" refresh="true" type="selected" setname="${mInformForm.tablename}" >
     <bean:message key="button.delete"/>
   </hrms:commandbutton>
   <hrms:commandbutton name="compute" function_id="2606410" functionId="" refresh="true" visible="${mInformForm.viewbutton}" type="selected" setname="${mInformForm.tablename}" onclick="batchHand(5,'${mInformForm.a_code}','${mInformForm.dbname}','${mInformForm.viewsearch}');">
     <bean:message key="button.computer"/>
   </hrms:commandbutton>          
</hrms:dataset>
<html:hidden name="mInformForm" property="a0100"/>
<html:hidden name="mInformForm" property="type"/>
<html:hidden name="mInformForm" property="photo_w"/>
<html:hidden name="mInformForm" property="photo_h"/>
<div id="movDiv" class="viewPhoto" onmousedown="getFocus(this)" style="display:none">
	<table width="85" border="0" cellspacing="0" cellpadding="0" class="ListTable1">
		<tr>
			<td width="95%" class="TableRow" style="cursor:move;height:10;" onmousedown="MDown(movDiv)">
			<div id="realName"></div></td>
			<td align="right" class="TableRow"><img src="/images/del.gif" onclick="closePhoto();"></td>
		</tr>
		<tr>
			<td class="appblack" align="center" colspan="2">
				<iframe id="ole" name="ole" width="85" height="120" frameborder="0"  scrolling="no"  src="#"></iframe> 
			</td>
		</tr>
		<tr>
			<td class="TableRow" colspan="2">
				<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td width="10%"  nowrap="nowrap">
							<img src="/images/viewBig.gif" onclick="maxSize();" border="0">
							<input type='button' value="取照片"  name='save' onclick="setPhoto();" Class="mybutton"/>
							<img src="/images/viewSmall.gif" onclick="minSize();" border="0">
						<td>
						<td style="cursor:move;height:10;" onmousedown="MDown(movDiv)">&nbsp;</td>
					<tr>
				</table>
			</td>
		</tr>
	</table>
	<iframe src="javascript:false" style="position:absolute; visibility:inherit; top:0px; left:0px;width:150px;height:30px;z-index:-1;filter='progid:DXImageTransform.Microsoft.Alpha(style=0,opacity=0)';">
	</iframe>
</div>
<input type="hidden" name="dbstr" value="<%=userdb%>">
</html:form>

<script language="javascript">
function ${mInformForm.tablename}_afterChange(dataset,field,value){
	var field_name=field.getName();
	var record,pfield;
	record=dataset.getCurrent(); 
	if(field_name=='select')
		return;
	if(field_name=="e01a1"){
   	  	value=record.getValue("e01a1");
   	  	if(value!=null&&value.length>0){
   	  		value=getDeptParentId(value);
   	  		pfield=dataset.getField("e0122");
			if(typeof(pfield)!="undefined"){
				if(isExistField(dataset,'e0122')) 
					record.setValue("e0122",value);
			}
		}
	}
	if(field_name=="e0122"){
		value=record.getValue("e0122");
   	  	if(value!=null&&value.length>0){
   	  		value=getUnitParentId(value);
   	  		pfield=dataset.getField("e0122");
			if(typeof(pfield)!="undefined"){
				 if(isExistField(dataset,'b0110')){    	  		 	
					record.setValue("b0110",value);
				}
			}
		}
	} 
	var a0100 = record.getValue("a0100");
	var tablename = "${mInformForm.tablename}";
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
	hashvo.setValue("fieldvalue",fieldvalue);  
	hashvo.setValue("itemid",field_name);	
	hashvo.setValue("tablename",tablename);  
	hashvo.setValue("a0100",a0100);  
	if(tablename.indexOf("A01")==-1&&tablename.indexOf("a01")==-1){
		var i9999 = record.getValue("i9999");
		hashvo.setValue("i9999",i9999);  
	}
	var request=new Request({method:'post',asynchronous:false,functionId:"1010090011"},hashvo);
}  

function table${mInformForm.tablename}_onRowClick(table){
	var getablename = "${mInformForm.tablename}";
	var dataset=table.getDataset();	
   	var record=dataset.getCurrent();
   	if(!record)
	    return;
    var a0100=record.getValue("A0100");	
    var a0101=record.getValue("A0101");
    parent.frames['a'].location="/general/inform/get_data_table.do?b_menu=link&a0100="+a0100+"&dbname=${mInformForm.dbname}";
    window.status="<%=userdb%>  "+a0101;
	if (document.getElementById("movDiv")){
		target = document.getElementById("movDiv");
		var photo_state = parent.parent.mil_menu.document.all.photo_state.value;
		photo_state=photo_state!=null&&photo_state.length>0?photo_state:"0";
		if(photo_state=="1")
			target.style.display = "block";
		if(target.style.display != "block"){
			return;
		}
	}
    document.getElementById("realName").innerHTML=a0101;
    var photo_w = 127;
    if(document.all('ole').offsetWidth!=null&&document.all('ole').offsetWidth!=0)
    	photo_w = document.all('ole').offsetWidth;
    var photo_h = 180;
    if(document.all('ole').offsetHeight!=null&&document.all('ole').offsetHeight!=0)
    	photo_h = document.all('ole').offsetHeight;
    document.all('ole').style.height=parseInt(photo_h);
    document.all('ole').style.width=parseInt(photo_w);
    document.ole.location.href="/general/inform/emp/view/displaypicture.do?b_query=link&a0100="+a0100+"&a0101="+getEncodeStr(a0101);
    toggles("movDiv");
}
function closePhoto(){
	hides("movDiv");
	parent.parent.mil_menu.document.all.photo_state.value="0";
}
var fixpos_w=movDiv.style.posTop;
var fixpos_h=movDiv.style.posLeft;
document.body.onscroll=function(){ 
	movDiv.style.posTop=document.body.scrollTop+fixpos_w;
	movDiv.style.posLeft=document.body.scrollLeft+fixpos_h; 
}

document.body.onbeforeunload=function(){ 
	window.status='';
}
</script>
<script language="javascript">
   var dropDownDate=createDropDown("dropDownDate");
   var __t=dropDownDate;
   __t.type="date";
   __t.tag="";
   _array_dropdown[_array_dropdown.length]=__t;
   initDropDown(__t);
   
   var dropDownList=createDropDown("dropDownList");
   var __t=dropDownList;
   __t.type="list";
   __t.tag="";
   _array_dropdown[_array_dropdown.length]=__t;
   initDropDown(__t);   
</script>
<script language="javascript">
    var dropdownCode=createDropDown("dropdownCode");
    var __t=dropdownCode;
    __t.type="custom";
    __t.path="/general/muster/select_code_tree.do";
	//__t.path="/system/gcodeselect.jsp";    
    __t.readFields="codeitemid";
    //__t.writeFields="xxxx";
    __t.cachable=true;__t.tag="";
    _array_dropdown[_array_dropdown.length]=__t;
    initDropDown(__t);
</script>
<script language="javascript">
  initDocument();
</script>

