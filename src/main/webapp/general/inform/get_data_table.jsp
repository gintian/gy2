<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.taglib.CommonData"%>
<%@ page import="com.hjsj.hrms.actionform.general.inform.MInformForm"%> 
<script language="javascript" src="/js/dict.js"></script> 
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
<script language="JavaScript" src="inform.js"></script>
<script language="javascript" src="/general/inform/photo.js"></script>
<%
    MInformForm mInformForm = (MInformForm)session.getAttribute("mInformForm");
    int i=0;
    String userdb = "";
%>
<html:form action="/general/inform/get_data_table.do?b_query=link">
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
        <hrms:menuitem name="m3" function_id="2606401" visible="${mInformForm.viewbutton}" label="infor.menu.transbase" enabled="${mInformForm.viewbutton}" icon="/images/link.gif" url="shiftlabrary('${mInformForm.dbname}','${mInformForm.tablename}','${mInformForm.a_code}')"/>              
        </logic:equal>
  </hrms:menuitem>
  <logic:equal value="A01" name="mInformForm" property="setname">
  <hrms:menuitem name="gz1" label="infor.menu.edit" visible="${mInformForm.viewbutton}" function_id="260640201,2606405,260640301,260640401">
    <hrms:menuitem name="m1" function_id="260640201" label="infor.menu.new" enabled="${mInformForm.viewbutton}" icon="/images/quick_query.gif" url="append('${mInformForm.tablename}','${mInformForm.a_code}','${mInformForm.reserveitem}');" command="" />
    <hrms:menuitem name="m2" function_id="260640301" label="infor.menu.ins" enabled="${mInformForm.viewbutton}" icon="/images/deal.gif" url="insert('${mInformForm.tablename}','${mInformForm.a_code}');" command="" />  
    <hrms:menuitem name="m3" function_id="260640401" label="infor.menu.del" enabled="${mInformForm.viewbutton}" icon="/images/del.gif" url="" command="delselected" hint="general.inform.search.confirmed.del" />  
    <hrms:menuitem name="m4" function_id="2606405" label="infor.menu.move" visible="${mInformForm.viewbutton}" icon="/images/quick_query.gif" url="to_move_record('${mInformForm.tablename}');" command="" />  
  </hrms:menuitem> 
  </logic:equal> 
   <logic:notEqual value="A01" name="mInformForm" property="setname">
  <hrms:menuitem name="gz1" label="infor.menu.edit" function_id="260640202,2606405,260640302,260640402,3233101,3233102,3233103,3233121">
    <hrms:menuitem name="m1" function_id="260640202,3233101" label="infor.menu.new" enabled="${mInformForm.viewbutton}" icon="/images/quick_query.gif" url="append('${mInformForm.tablename}','${mInformForm.a_code}','${mInformForm.reserveitem}');" command="" />
    <hrms:menuitem name="m2" function_id="260640302,3233102" label="infor.menu.ins" enabled="${mInformForm.viewbutton}" icon="/images/deal.gif" url="insert('${mInformForm.tablename}','${mInformForm.a_code}');" command="" />  
    <hrms:menuitem name="m3" function_id="260640402,3233103" label="infor.menu.del" enabled="${mInformForm.viewbutton}" icon="/images/del.gif" url="" command="delselected" hint="general.inform.search.confirmed.del" />  
    <hrms:menuitem name="m4" function_id="2606405,3233121" label="infor.menu.move" icon="/images/quick_query.gif" url="to_move_record('${mInformForm.tablename}');" command="" />  
  </hrms:menuitem> 
  </logic:notEqual> 
  <hrms:menuitem name="gz2" label="infor.menu.bat" function_id="2606406,2606407,2606408,2606409,2606410,2606411,2606411,3233104,3233105,3233122,3233123">
      <hrms:menuitem name="mitem1" function_id="2606406,3233104" label="infor.menu.batupdate_s" icon="/images/add_del.gif" url="batchHand(1,'${mInformForm.a_code}','${mInformForm.dbname}','${mInformForm.viewsearch}','${mInformForm.inforflag}');" command="" enabled="true" visible="true"/>
      <logic:notEqual name="mInformForm" property="setname" value="A00">
        <hrms:menuitem name="mitem2" function_id="2606407,3233105" label="infor.menu.batupdate_m" icon="/images/write.gif" url="batchHand(2,'${mInformForm.a_code}','${mInformForm.dbname}','${mInformForm.viewsearch}','${mInformForm.inforflag}');" command="" enabled="true" visible="true"/>
      </logic:notEqual>
      <logic:notEqual name="mInformForm" property="setname" value="A01">
        <logic:notEqual name="mInformForm" property="setname" value="A00">
            <hrms:menuitem name="mitem3" function_id="2606408,3233122" label="infor.menu.batupdate_a" visible="${mInformForm.viewbutton}" icon="" url="batchHand(3,'${mInformForm.a_code}','${mInformForm.dbname}','${mInformForm.viewsearch}','${mInformForm.inforflag}');" command="" enabled="true"/>
            <hrms:menuitem name="mitem4" function_id="2606409,3233123" label="infor.menu.batupdate_d" visible="${mInformForm.viewbutton}" icon="" url="batchHand(4,'${mInformForm.a_code}','${mInformForm.dbname}','${mInformForm.viewsearch}','${mInformForm.inforflag}');" command="" enabled="true"/>
        </logic:notEqual>
      </logic:notEqual>
      <logic:equal name="mInformForm" property="inforflag" value="1">
      <hrms:menuitem name="mitem5" function_id="2606410" label="infor.menu.compute" icon="/images/add_del.gif" url="batchHand(5,'${mInformForm.a_code}','${mInformForm.dbname}','${mInformForm.viewsearch}');" command="" enabled="true" visible="true"/>
      <hrms:menuitem name="mitem6" function_id="2606411" label="org.gzdatamaint.gzdatamaint.singlecheck" icon="/images/quick_query.gif" url="singleAudit(1,'${mInformForm.dbname}');" command="" enabled="true" visible="true"/>
     </logic:equal>
  </hrms:menuitem>  
  <hrms:menuitem name="gz3" label="infor.menu.query" function_id="2606421,2606422,2606423,3233118,3233119,3233120">
      <hrms:menuitem name="mitem1" function_id="2606421,3233118" label="infor.menu.squery" icon="" url="searchInform(1,1,'${mInformForm.a_code}','${mInformForm.dbname}');" command="" enabled="true" visible="true"/>
      <hrms:menuitem name="mitem2" function_id="2606422,3233119" label="infor.menu.hquery" icon="" url="searchInform(1,2,'${mInformForm.a_code}','${mInformForm.dbname}');" command="" enabled="true" visible="true"/>
      <hrms:menuitem name="mitem3" function_id="2606423,3233120" label="infor.menu.gquery" icon="" url="">
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
        <%if(n>10){%>
        <hrms:menuitem name='<%="mitem"+(n+1)%>' label='general.inform.search.themore' icon="" url="searchInform(1,3,'${mInformForm.a_code}','${mInformForm.dbname}');" command="" enabled="true" visible="true"/>
        <%} %>
      </hrms:menuitem>
      <logic:equal name="mInformForm" property="viewsearch" value="1">
         <hrms:menuitem name="mitem100" label="general.inform.search.view.result" icon="" url="searchOk(0);" checked="true" groupindex="1"/>
      </logic:equal>
      <logic:equal name="mInformForm" property="viewsearch" value="0">
        <hrms:menuitem name="mitem100" label="general.inform.search.view.result" icon="" url="searchOk(1);" groupindex="1"/>
      </logic:equal>
  </hrms:menuitem>  
   <hrms:menuitem name="gz5" label="infor.menu.print" function_id="2606412,2606413,2606414,3233110,3233111,3233112">
      <hrms:menuitem name="m1" function_id="2606412,3233110" label="infor.menu.outmuster" icon="/images/print.gif" url="" command="" enabled="true" visible="true">
          <hrms:menuitem name="m11" label="infor.menu.display.data" icon="" url="printInform(1,'${mInformForm.dbname}','${mInformForm.a_code}',1,'${mInformForm.viewsearch}');" command="" enabled="true" visible="true"/>
          <hrms:menuitem name="m12" label="infor.menu.query.data" icon="" url="printInform(1,'${mInformForm.dbname}','${mInformForm.a_code}',1,'2');" command="" enabled="true" visible="true"/>
      </hrms:menuitem>
      <hrms:menuitem name="m2" function_id="2606413,3233111" label="infor.menu.outcard" icon="/images/print.gif" url="" command="" enabled="true" visible="true">
          <hrms:menuitem name="m20" label="infor.menu.select.data" icon="" url="printInform(2,'${mInformForm.tablename}','${mInformForm.a_code}',1,2);" command="" enabled="true" visible="true"/>
          <hrms:menuitem name="m21" label="infor.menu.display.data" icon="" url="printInform(2,'${mInformForm.tablename}','${mInformForm.a_code}',1,'${mInformForm.viewsearch}');" command="" enabled="true" visible="true"/>
          <hrms:menuitem name="m22" label="infor.menu.query.data" icon="" url="printInform(2,'${mInformForm.tablename}','${mInformForm.a_code}',1,'1');" command="" enabled="true" visible="true"/>
      </hrms:menuitem>
      <hrms:menuitem name="m3" function_id="2606414,3233112" label="infor.menu.outhmuster" icon="/images/print.gif" url="" command="" enabled="true" visible="true">
          <hrms:menuitem name="m31" label="infor.menu.display.data" icon="" url="printInform(3,'${mInformForm.dbname}','${mInformForm.a_code}',1,'${mInformForm.viewsearch}');" command="" enabled="true" visible="true"/>
          <hrms:menuitem name="m32" label="infor.menu.query.data" icon="" url="printInform(3,'${mInformForm.dbname}','${mInformForm.a_code}',1,'2');" command="" enabled="true" visible="true"/>
      </hrms:menuitem>      
  </hrms:menuitem> 
  <hrms:menuitem name="gz4" label="infor.menu.view" function_id="2606415,2606416,2606417,2606418,2606419,2606420,3233113,3233114,3233116,3233117,3233124">
      <hrms:menuitem name="mitem1" function_id="2606415,3233113" label="infor.menu.picture" icon="/images/man.gif" url="diplaypicture('${mInformForm.tablename}');" command="" enabled="true" visible="true"/>
      <logic:equal name="mInformForm" property="inforflag" value="1"> 
      <hrms:menuitem name="mitem2" function_id="2606416" label="infor.menu.media" icon="/images/prop_ps.gif" url="to_multimedia_tree('${mInformForm.tablename}','${mInformForm.dbname}');" command="" enabled="true" visible="true"/>
      </logic:equal> 
      <hrms:menuitem name="mitem3" function_id="2606417,3233114" label="infor.menu.msort" icon="/images/sort.gif" url="to_sort_main_info();" command="" enabled="true" visible="true"/>
      <logic:equal value="A01" name="mInformForm" property="setname">
            <hrms:menuitem name="mitem4" function_id="2606418,3233124" label="infor.menu.ssort" icon="/images/sort.gif" url="to_sort_subset_info('${mInformForm.tablename}');" command="" enabled="false" visible="true"/>
      </logic:equal>
      <logic:notEqual value="A01" name="mInformForm" property="setname">
          <logic:equal name="mInformForm" property="inforflag" value="1">
            <hrms:menuitem name="mitem4" function_id="2606418,3233124" label="infor.menu.ssort" icon="/images/sort.gif" url="to_sort_subset_info('${mInformForm.tablename}');" command="" enabled="true" visible="true"/>
          </logic:equal>
          <logic:equal name="mInformForm" property="inforflag" value="2">
            <hrms:menuitem name="mitem4" function_id="2606418,3233124" label="infor.menu.ssort" icon="/images/sort.gif" url="to_sort_subset_info('${mInformForm.tablename}','2');" command="" enabled="true" visible="true"/>
          </logic:equal>  
      </logic:notEqual>     
      <hrms:menuitem name="mitem5" function_id="2606419,3233116" label="infor.menu.hide" icon="" url="to_hide_field();" command="" enabled="true" visible="true"/>
      <hrms:menuitem name="mitem6" function_id="2606420,3233117" label="infor.menu.sortitem" icon="/images/sort.gif" url="to_sort_field();" command="" enabled="true" visible="true"/>
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
<logic:equal value="A01" name="mInformForm" property="setname">
<hrms:dataset name="mInformForm" property="fieldlist" scope="session" setname="${mInformForm.tablename}"  
setalias="data_table" readonly="false" rowlock="true" editable="true" select="true" 
sql="${mInformForm.sql}" keys="${mInformForm.keys}" pagerows="${mInformForm.pagerows}" buttons="movefirst,prevpage,nextpage,movelast">
   <hrms:commandbutton name="table" function_id="260640201" hint="" functionId="" visible="${mInformForm.viewbutton}" refresh="true" type="selected" setname="${mInformForm.tablename}" onclick="append('${mInformForm.tablename}','${mInformForm.a_code}','${mInformForm.reserveitem}');" >
     <bean:message key="button.insert"/> 
   </hrms:commandbutton>
   <hrms:commandbutton name="tableinsert" function_id="260640301" hint="" functionId="" visible="${mInformForm.viewbutton}" refresh="true" type="selected" setname="${mInformForm.tablename}" onclick="insert('${mInformForm.tablename}','${mInformForm.a_code}','${mInformForm.reserveitem}');" >
    <bean:message key="button.new.insert"/>
   </hrms:commandbutton> 
    <hrms:commandbutton name="delselected" function_id="260640401" hint="general.inform.search.confirmed.del" visible="${mInformForm.viewbutton}" functionId="1010090003" refresh="true" type="selected" setname="${mInformForm.tablename}" >
     <bean:message key="button.delete"/>
   </hrms:commandbutton>
   <hrms:commandbutton name="compute" function_id="2606410" functionId="" refresh="true"  type="selected" setname="${mInformForm.tablename}" onclick="batchHand(5,'${mInformForm.a_code}','${mInformForm.dbname}','${mInformForm.viewsearch}');">
     <bean:message key="button.computer"/>
   </hrms:commandbutton>  
    <hrms:commandbutton name="lie" function_id="2600,32331" hint="" functionId="" visible="true" refresh="true" type="selected" setname="${mInformForm.tablename}" onclick="lietable();" >
     <bean:message key="reportcheck.return"/>
   </hrms:commandbutton>        
</hrms:dataset>
</logic:equal>
<logic:notEqual value="A01" name="mInformForm" property="setname">
<hrms:dataset name="mInformForm" property="fieldlist" scope="session" setname="${mInformForm.tablename}"  
setalias="data_table" readonly="false" rowlock="true" editable="true" select="true" 
sql="${mInformForm.sql}" keys="${mInformForm.keys}" pagerows="${mInformForm.pagerows}" buttons="movefirst,prevpage,nextpage,movelast">
   <hrms:commandbutton name="table" function_id="260640202,3233101" hint="" functionId="" visible="${mInformForm.viewbutton}" refresh="true" type="selected" setname="${mInformForm.tablename}" onclick="append('${mInformForm.tablename}','${mInformForm.a_code}','${mInformForm.reserveitem}');" >
     <bean:message key="button.insert"/> 
   </hrms:commandbutton>
   <hrms:commandbutton name="tableinsert" function_id="260640302,3233102" hint="" functionId="" visible="${mInformForm.viewbutton}" refresh="true" type="selected" setname="${mInformForm.tablename}" onclick="insert('${mInformForm.tablename}','${mInformForm.a_code}','${mInformForm.reserveitem}');" >
    <bean:message key="button.new.insert"/>
   </hrms:commandbutton> 
    <hrms:commandbutton name="delselected" function_id="260640402,3233103" hint="general.inform.search.confirmed.del" visible="${mInformForm.viewbutton}" functionId="1010090003" refresh="true" type="selected" setname="${mInformForm.tablename}" >
     <bean:message key="button.delete"/>
   </hrms:commandbutton>
   <hrms:commandbutton name="compute" function_id="2606410,3233108" functionId="" refresh="true" type="selected" setname="${mInformForm.tablename}" onclick="batchHand(5,'${mInformForm.a_code}','${mInformForm.dbname}','${mInformForm.viewsearch}');">
     <bean:message key="button.computer"/>
   </hrms:commandbutton>  
    <hrms:commandbutton name="lie" function_id="2600,32331" hint="" functionId="" visible="true" refresh="true" type="selected" setname="${mInformForm.tablename}" onclick="lietable();" >
     <bean:message key="reportcheck.return"/>
   </hrms:commandbutton>        
</hrms:dataset>
</logic:notEqual>
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
                            <hrms:priv func_id="260641501">  
                            <input type='button' value="<bean:message key='workdiary.message.get.pic'/>"  name='save' onclick="setPhoto();" Class="mybutton"/>
                            </hrms:priv>
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
<%int privbutton=0; %>
<hrms:priv func_id="2606421,2606410,2606402,2606403,2606404">
<% privbutton=1;%>
<table border="0" width="300" style="position:absolute;left:500px;top:35px;">
 <bean:message key="infor.label.setlist"/>
 <html:select name="mInformForm" property="setname" style="width:150" onchange="reloadBySetId(this);" >
    <html:optionsCollection property="setlist" value="dataValue" label="dataName" />
 </html:select> 
</table>
</hrms:priv>
<%if(privbutton==0){ %>
<table border="0" width="300" style="position:absolute;left:500px;top:35px;">
 <bean:message key="infor.label.setlist"/>
 <html:select name="mInformForm" property="setname" style="width:150" onchange="reloadBySetId(this);" >
    <html:optionsCollection property="setlist" value="dataValue" label="dataName" />
 </html:select> 
</table>
<%} %>
<input type="hidden" name="dbstr" value="<%=userdb%>">
<input type="button" name="testbutton" value="a" style="width:1px;height:1px;display:none"/>
</html:form>

<script language="javascript">
var recordVos;
function ${mInformForm.tablename}_afterChange(dataset,field,value){
    var field_name=field.getName();
    var record,pfield;
    record=dataset.getCurrent(); 
    recordVos = dataset.getCurrent(); 
    if(field_name=='select')
        return;
        if(field_name=="b0110"){
        value=record.getValue("e0122");
        var b0110=record.getValue("b0110");
        if(value!=null&&value.length>0){
            value=getUnitParentId(value);
            pfield=dataset.getField("e0122");
            if(typeof(pfield)!="undefined"){
                 if(isExistField(dataset,'b0110')){                 
                    if(value!=b0110)
                        record.setValue("e0122","");
                }
            }
        }
    }   
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
        var e01a1=record.getValue("e01a1");
        var e0122=record.getValue("e0122");
        if(value!=null&&value.length>0){
            value=getUnitParentId(value);
            pfield=dataset.getField("e0122");
            if(typeof(pfield)!="undefined"){
                 if(isExistField(dataset,'b0110')){                 
                    record.setValue("b0110",value);
                }
            }
        }
        
        if(e01a1!=null&&e01a1.length>0){
            e01a1=getDeptParentId(e01a1);
            pfield=dataset.getField("e0122");
            if(typeof(pfield)!="undefined"){
                if(isExistField(dataset,'e0122')) 
                    if(e0122!=e01a1)
                        record.setValue("e01a1","");
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
    hashvo.setValue("fieldvalue",getEncodeStr(fieldvalue));  
    hashvo.setValue("itemid",field_name);   
    hashvo.setValue("tablename",tablename);  
    hashvo.setValue("a0100",a0100);  
    hashvo.setValue("inforflag","1");  
    if(tablename.indexOf("A01")==-1&&tablename.indexOf("a01")==-1){
        var i9999 = record.getValue("i9999");
        hashvo.setValue("i9999",i9999);  
    }
    var request=new Request({method:'post',asynchronous:false,onSuccess:checkOnlyName,functionId:"1010090011"},hashvo);
    
}  
function checkOnlyName(outparamters){
    var chkflag = outparamters.getValue("chkflag");
    var onlynameflag = outparamters.getValue("onlynameflag");
    var itemcheck = outparamters.getValue("itemcheck");
    var chupdate = outparamters.getValue("chupdate");
    var itemid = outparamters.getValue("itemid");
    var tablename = "${mInformForm.tablename}";
    if(chkflag!='true'){
        alert(chkflag);
        recordVos.setValue(itemid,"");
        return false;
    }
    if(onlynameflag!='true'){
        alert(onlynameflag);
        recordVos.setValue(itemid,"");
        return false;
    }
    if(chupdate!='true'){
        alert(chupdate);
        return false;
    }
    if(tablename.indexOf("A01")==-1&&tablename.indexOf("a01")==-1){
        var i9999 = recordVos.getValue("i9999");
        if(i9999==null||i9999.length<1)
            recordVos.setValue("i9999","1");
    }
    if(itemcheck=='ok'){
        var itemarr = outparamters.getValue("itemarr");
        var itemvaluearr = outparamters.getValue("itemvaluearr");
        var itemidArr = itemarr.split("::");
        var valueArr = itemvaluearr.split("::");
        for(var i=0;i<itemidArr.length;i++){
            if(itemidArr[i]!=null&&itemidArr[i].length>0){
                recordVos.setValue(itemidArr[i],valueArr[i]);
            }
        }
    }
}
var oldrecord;
function table${mInformForm.tablename}_onRowClick(table){
    var getablename = "${mInformForm.tablename}";
    var reserveitem = "${mInformForm.reserveitem}";
    var dataset=table.getDataset(); 
    var record=dataset.getCurrent();
    if(!record)
        return;
    /**必添项处理*/
    if(oldrecord&&record!=oldrecord){
        if(chNullRecord(oldrecord)=="true"){
            var chkrecord = "0";
            if(getablename.indexOf("A01")==-1){
                if(oldrecord.getValue("I9999")==null||oldrecord.getValue("I9999")==""){
                    chkrecord = "1";
                }
            }
            if(chkrecord=="0"){
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
        }
    }
    if(oldrecord&&record==oldrecord){
        return false;
    }
    oldrecord = record;
    var a0100=record.getValue("A0100"); 
    var a0101=record.getValue("A0101");
    window.status='<%=userdb%>'+"  "+a0101;
    if (document.getElementById("movDiv")){
        target = document.getElementById("movDiv");
        var photo_state = parent.mil_menu.document.all.photo_state.value;
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
    document.ole.location.href="/general/inform/emp/view/displaypicture.do?b_query=link&a0100="+a0100+"&a0101="+$URL.encode(getEncodeStr(a0101));
    toggles("movDiv");
   
    
}
function chNullRecord(orecord){
    var tablename="table"+"${mInformForm.tablename}";
    var table=$(tablename);
    var cha0100="false";
    var dataset=table.getDataset(); 
    var chrecord = dataset.getFirstRecord();
    if(chrecord.getValue("A0100")==orecord.getValue("A0100"))
        cha0100 = "true";
    while(true){
        chrecord=chrecord.getNextRecord();
        if(chrecord==undefined){
            break;
        }
        if(chrecord.getValue("A0100")==orecord.getValue("A0100")){
            cha0100 = "true"; 
            break;
        }   
    }   
    return cha0100;
        
}
function closePhoto(){
    hides("movDiv");
    parent.mil_menu.document.all.photo_state.value="0";
}
function lietable(){
    self.parent.location = "/general/inform/org_tree.do?b_query=link&inforflag=${mInformForm.inforflag}";
}
var fixpos_w=movDiv.style.posTop;
var fixpos_h=movDiv.style.posLeft;
document.body.onscroll=function(){ 
    movDiv.style.posTop=document.body.scrollTop+fixpos_w;
    movDiv.style.posLeft=document.body.scrollLeft+fixpos_h; 
}

document.body.onbeforeunload=function(){ 
    window.status='';
    var target = document.getElementById("testbutton");
    target.style.display = "block";
    target.focus();
    target.style.display="none"; 
}
function table${mInformForm.tablename}_oper_onRefresh(cell,value,record){   
    if(record!=null)    
        cell.innerHTML="<img src=\"/images/edit.gif\" border=\"0\" onclick=\"edit('${mInformForm.tablename}','${mInformForm.a_code}','${mInformForm.dbname}')\" style=\"cursor:hand;\">";
    else
        cell.innerHTML="";
}
function table${mInformForm.tablename}_downole_onRefresh(cell,value,record){    
    if(record!=null){
        var i9999 = record.getValue("i9999");   
        var a0100 = record.getValue("a0100");   
        if(i9999!='')
            cell.innerHTML="<img src=\"/images/view.gif\" border=\"0\" onclick=\"downLoadOle('${mInformForm.tablename}','"+a0100+"','"+i9999+"','1')\" style=\"cursor:hand;\">";
    }
}
function table${mInformForm.tablename}_upole_onRefresh(cell,value,record){  
    if(record!=null){
        var i9999 = record.getValue("i9999");   
        var a0100 = record.getValue("a0100");   
        if(i9999!='')
            cell.innerHTML="<img src=\"/images/import.gif\" border=\"0\" onclick=\"uploadMedia('${mInformForm.dbname}','"+a0100+"','"+i9999+"','1')\" style=\"cursor:hand;\">";
    }
}
function table${mInformForm.tablename}_flag_onRefresh(cell,value,record){   
    if(record!=null){
        var i9999 = record.getValue("i9999");   
        var flag = record.getValue("flag");
        var a0100 = record.getValue("a0100");   
        if(flag==null||flag.length<1)
            flag = "选择";
        var cellstr = "<div style=\"cursor:hand;color:#0033FF\" ";
        cellstr+=" onclick=\"selectMedia('${mInformForm.dbname}','"+a0100+"','";
        cellstr+=i9999+"','1','1',cell)\" ";
        cellstr+=">"+flag+"</div>";
        if(i9999!='')//有子集的时候才能编辑   
            cell.innerHTML=cellstr;
        
    }
}
</script>

