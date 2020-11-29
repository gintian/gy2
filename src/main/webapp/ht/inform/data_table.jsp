<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.taglib.CommonData"%>
<%@ page import="com.hjsj.hrms.actionform.ht.inform.ContractForm"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
    ContractForm contractForm = (ContractForm)session.getAttribute("contractForm");
    int i=0;
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    String bosflag="";
    if(userView != null){     
        bosflag=userView.getBosflag();
        bosflag=bosflag!=null?bosflag:"";                
    }
    
%>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<hrms:themes/>
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
<script language="JavaScript" src="infor.js"></script>
<script language="javascript" src="/js/dict.js"></script> 
<html:form action="/ht/inform/data_table.do?b_query=link">
<html:hidden name="contractForm" property="a_code"/>
<table><tr><td>
<table><tr><td>
<hrms:menubar menu="menu1" id="menubar1">  
  <hrms:menuitem name="gz3" label="infor.menu.query" function_id="3300201">
      <hrms:menuitem name="mitem1" label="infor.menu.squery" function_id="330020101" icon="" url="searchInform(1,1,'${contractForm.a_code}','${contractForm.dbname}');" command="" enabled="true" visible="true"/>
      <hrms:menuitem name="mitem2" label="infor.menu.hquery" function_id="330020102" icon="" url="searchInform(1,2,'${contractForm.a_code}','${contractForm.dbname}');" command="" enabled="true" visible="true"/>
      <hrms:menuitem name="mitem3" label="infor.menu.gquery" function_id="330020103" icon="" url="">
        <%int n=4;%>
        <logic:iterate id="element"  name="contractForm"  property="searchlist" indexId="index">  
             <%
                CommonData searhcitem=(CommonData)pageContext.getAttribute("element");
                String searchname=searhcitem.getDataValue();
                String id=searhcitem.getDataName();
                String a_code = (String)request.getParameter("a_code");
                String searchgeneral = "searchGeneral(1,"+id+",'"+a_code+"','"+contractForm.getDbname()+"');";
            %>
            <hrms:menuitem name='<%="mitem"+n%>' label='<%=searchname%>' icon="" url="<%=searchgeneral%>" command="" enabled="true" visible="true"/>
            <%n++;%>
        </logic:iterate>
        <hrms:menuitem name='<%="mitem"+(n+1)%>' label='general.inform.search.themore' icon="" url="searchInform(1,3,'${contractForm.a_code}','${contractForm.dbname}');" command="" enabled="true" visible="true"/>
      </hrms:menuitem>
      <logic:equal name="contractForm" property="viewsearch" value="1">
         <hrms:menuitem name="mitem100" label="general.inform.search.view.result" function_id="330020104" icon="" url="searchOk(0);" checked="true" groupindex="1"/>
      </logic:equal>
      <logic:equal name="contractForm" property="viewsearch" value="0">
        <hrms:menuitem name="mitem100" label="general.inform.search.view.result" function_id="330020104" icon="" url="searchOk(1);" groupindex="1"/>
      </logic:equal>
  </hrms:menuitem>  
    <hrms:menuitem name="gz2" label="infor.menu.bat" function_id="3300202">
      <hrms:menuitem name="mitem5" label="infor.menu.compute" function_id="330020201" icon="" url="batchHand(5,'${contractForm.a_code}','${contractForm.dbname}','${contractForm.viewsearch}');" command="" enabled="true" visible="true"/>
 </hrms:menuitem>
   <hrms:menuitem name="gz5" label="infor.menu.print" function_id="3300203">
       <%--新版花名册--%>
        <hrms:menuitem name="m1" function_id="330020301" label="infor.menu.outmuster" icon="/images/print.gif" url="openwin('/module/muster/mustermanage/MusterManage.html?musterType=1&moduleID=0');" command="" enabled="true" visible="true">
          <%--<hrms:menuitem name="m11" label="infor.menu.display.data" icon="" url="printInform(1,'${contractForm.dbname}','${contractForm.a_code}',1,'${contractForm.viewsearch}');" command="" enabled="true" visible="true"/>
          <hrms:menuitem name="m12" label="infor.menu.query.data" icon="" url="printInform(1,'${contractForm.dbname}','${contractForm.a_code}',1,'2');" command="" enabled="true" visible="true"/>--%>
      </hrms:menuitem>
      <hrms:menuitem name="m2" function_id="330020302" label="infor.menu.outcard" icon="/images/print.gif" url="" command="" enabled="true" visible="true">
          <hrms:menuitem name="m20" label="infor.menu.select.data" icon="" url="printInform(2,'${contractForm.tablename}','${contractForm.a_code}',1,2);" command="" enabled="true" visible="true"/>
          <hrms:menuitem name="m21" label="infor.menu.display.data" icon="" url="printInform(2,'${contractForm.tablename}','${contractForm.a_code}',1,'${contractForm.viewsearch}');" command="" enabled="true" visible="true"/>
          <hrms:menuitem name="m22" label="infor.menu.query.data" icon="" url="printInform(2,'${contractForm.tablename}','${contractForm.a_code}',1,'1');" command="" enabled="true" visible="true"/>
      </hrms:menuitem>
      <hrms:menuitem name="m3" function_id="330020303" label="合同台帐" icon="/images/print.gif" url="" command="" enabled="true" visible="true">
          <hrms:menuitem name="m31" label="infor.menu.display.data" icon="" url="printInform(3,'${contractForm.dbname}','${contractForm.a_code}',1,'${contractForm.viewsearch}');" command="" enabled="true" visible="true"/>
          <hrms:menuitem name="m32" label="infor.menu.query.data" icon="" url="printInform(3,'${contractForm.dbname}','${contractForm.a_code}',1,'2');" command="" enabled="true" visible="true"/>
      </hrms:menuitem>   
  </hrms:menuitem>  
</hrms:menubar>
</td></tr></table>
</td>
<td>
&nbsp; 人员库
 <html:select name="contractForm" property="dbname" onchange="reloadBySetId();" style="width:120">
    <html:optionsCollection property="dblist" value="dataValue" label="dataName" />
 </html:select> 
  合同状态
 <html:select name="contractForm" property="ctflag" onchange="reloadBySetId();" >
    <html:optionsCollection property="ctflaglist" value="dataValue" label="dataName" />
 </html:select> 
<logic:equal value="dxt" name="contractForm" property="returnvalue">
     <hrms:tipwizardbutton flag="contract" target="il_body" formname="contractForm"></hrms:tipwizardbutton>
</logic:equal>
</td>
</tr>
</table>
<div>
<hrms:dataset name="contractForm" property="fieldlist" scope="session" setname="${contractForm.tablename}"  
setalias="data_table" readonly="false" rowlock="true" editable="true" select="true" 
sql="${contractForm.sql}" pagerows="${contractForm.pagerows}" buttons="bottom"> 
</hrms:dataset>
</div>
<input type="hidden" name="setname" value="A01">
</html:form>
<script language="javascript">
function ${contractForm.tablename}_afterChange(dataset,field,value){
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
    var tablename = "${contractForm.tablename}";
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
    hashvo.setValue("inforflag","1");   
    var request=new Request({method:'post',asynchronous:false,onSuccess:checkOnlyName,functionId:"1010090011"},hashvo);
}
function checkOnlyName(outparamters){
    var chkflag = outparamters.getValue("chkflag");
    var onlynameflag = outparamters.getValue("onlynameflag");
    if(chkflag!='true')
        alert(chkflag);
    if(onlynameflag!='true')
        alert(onlynameflag);
}  

function table${contractForm.tablename}_onRowClick(table){
    var getablename = "${contractForm.tablename}";
    var dataset=table.getDataset(); 
    var record=dataset.getCurrent();
    if(!record)
        return;
    var a0100=record.getValue("A0100"); 
    var a0101=record.getValue("A0101");
    var menuUrl = "/ht/inform/data_table.do?b_menu=link&a0100=";
    menuUrl+=a0100+"&dbname=${contractForm.dbname}&ctflag=${contractForm.ctflag}";
    parent.ril_body2.location=menuUrl;
    var objs = document.getElementById("dbname");
    var dbstr="";
    for(var i=0;i<objs.options.length;i++){
        if(objs.options[i].value=="${contractForm.dbname}")
            dbstr = objs.options[i].text;
    }
    window.status=dbstr+" "+a0101;
}
//新增打开窗口方法
function openwin(url)
{
    window.open(url,"_blank","left=0,top=0,width="+(screen.availWidth-20)+",height="+(screen.availHeight-60)+",scrollbars=yes,toolbar=no,menubar=no,location=no,resizable=yes,status=no");
}
document.body.onbeforeunload=function(){ 
    window.status='';
}
parent.ril_body2.location="/templates/welcome/welcome.html";
window.parent.Ext.getCmp("iframe_body2").setHeight(240);
</script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>

