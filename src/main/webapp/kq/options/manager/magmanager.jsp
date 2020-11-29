<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.taglib.CommonData"%>
<%@ page import="com.hrms.hjsj.sys.EncryptLockClient"%>
<%@ page import="com.hjsj.hrms.actionform.kq.options.manager.MagCardManagerForm"%>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<script type="text/javascript" src="/general/sys/hjaxmanage.js"></script>
<script type="text/javascript" src="/kq/kq.js"></script>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String userFullName=null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);	
	String fields="";
	String tables="";
	String superUser="0";
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  
      fields=userView.getFieldpriv().toString();
	  tables=userView.getTablepriv().toString();
	  userName=userView.getUserName(); 
	  if(userView.isSuper_admin())
	    superUser="1"; 
	  else
      {
         if(fields==null||fields.length()<=0)
	       fields=",";
	     if(tables==null||tables.length()<=0)
	       tables=","; 
	   }
	  userFullName=userView.getUserFullName();
	}
	EncryptLockClient lockclient=(EncryptLockClient)session.getServletContext().getAttribute("lock");
   String license=lockclient.getLicenseCount();
   int version=userView.getVersion();
   if(license.equals("0"))
        version=100+version;
   int usedday=lockclient.getUseddays();
		
%>
<%String aurl = (String)request.getServerName();
  String port=request.getServerPort()+"";  
  String url_p=SystemConfig.getCsClientServerURL(request); 
%>
<SCRIPT language=JavaScript>
function searchInform(type,query_type,a_code,tablename){
	var thecodeurl =""; 
	var return_vo;	
	switch(query_type){ 
         case 1	: 
              thecodeurl="/general/inform/search/generalsearch.do?b_query=link&type="+type+"&a_code="+a_code+"&tablename="+tablename;
              return_vo= window.showModalDialog(thecodeurl, "", 
              	"dialogWidth:700px; dialogHeight:410px;resizable:no;center:yes;scroll:no;status:no");
              break ; 
         case 2 : 
         	   thecodeurl="/general/inform/search/gmsearch.do?b_query=link&type="+type+"&a_code="+a_code+"&tablename="+tablename;
               return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:700px; dialogHeight:430px;resizable:no;center:yes;scroll:no;status:no");
              break ; 
         case 3 : 
              thecodeurl="/general/inform/search/searchcommon.do?b_query=link&type="+type+"&flag=search&a_code="+a_code+"&tablename="+tablename;
               return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:400px; dialogHeight:280px;resizable:no;center:yes;scroll:no;status:no");
              break ; 
         default:
         	thecodeurl="";
    } 
    if(thecodeurl.length<1){
    	return;
    }
    if(return_vo!=null){
   		magCardManagerForm.action="/kq/options/manager/magcarddata.do?b_search=link&viewsearch=1";
     	magCardManagerForm.submit();
  	}else{
  		return ;
  	}
}
function searchGeneral(type,id,a_code,tablename){
	var hashvo=new ParameterSet();
	hashvo.setValue("id",id);	
	hashvo.setValue("a_code",a_code);
	hashvo.setValue("tablename",tablename);
	hashvo.setValue("type",type);
	hashvo.setValue("flag","search");				
	var request=new Request({method:'post',asynchronous:false,onSuccess:checkSearchGeneral,functionId:'3020110076'},hashvo);
}
function checkSearchGeneral(outparamters){
	var check = outparamters.getValue("check");
	if(check=='ok'){
		magCardManagerForm.action="/kq/options/manager/magcarddata.do?b_search=link&viewsearch=1";
     	magCardManagerForm.submit();
  	}
}
function searchOk(viewsearch){ 	
   magCardManagerForm.action="/kq/options/manager/magcarddata.do?b_search=link&viewsearch="+viewsearch;
  	magCardManagerForm.submit();
}
  function change()
  {
     magCardManagerForm.action="/kq/options/manager/magcarddata.do?b_search=link";
     magCardManagerForm.submit();
  }  

function send_card()
{
     var len=document.magCardManagerForm.elements.length;
     var uu;
     var location=1;
     var a=0; 
     var selected=new Array();
     for (i=0;i<len;i++)
     {
           if (document.magCardManagerForm.elements[i].type=="checkbox")
            {
              if(document.magCardManagerForm.elements[i].checked==true)
              {
                 selected[a++]=location;
              }
              location++;
            }
     }
     if(selected.length<=0)
     {
        alert("请选择一条记录！");
        return false;
     }else if(selected.length>1)
     { 
        alert("请选择一条记录！");
        return false;
     }else
     {
        location=selected[0];
        var i9999=document.getElementById("nb"+location).value; 
        var a0100=document.getElementById("ab"+location).value; 
        //增加传入人员库
        var nbase=document.getElementById("db"+location).value;
        //var url="/kq/options/manager/sendmagcard.do?b_search=link&i9999="+i9999+"&a0100="+a0100;
          var url="/kq/options/manager/sendmagcard.do?b_search=link&i9999="+i9999+"&a0100="+a0100+"&nbase="+nbase;
         return_vo= window.showModalDialog(url, "", 
              	"dialogWidth:500px; dialogHeight:450px;resizable:no;center:yes;scroll:no;status:no");
       if(!return_vo)
	    return false;
       if(return_vo.flag=="true")
       {
          magCardManagerForm.action = "/kq/options/manager/magcarddata.do?b_search=link";   
          magCardManagerForm.target="mil_body"; 
          magCardManagerForm.submit(); 
       }
              	
     }
}
function change_card()
{
     var len=document.magCardManagerForm.elements.length;
     var uu;
     var location=1;
     var a=0; 
     var selected=new Array();
     for (i=0;i<len;i++)
     {
           if (document.magCardManagerForm.elements[i].type=="checkbox")
            {
              if(document.magCardManagerForm.elements[i].checked==true)
              {
                 selected[a++]=location;
              }
              location++;
            }
     }
     if(selected.length<=0)
     {
        alert("请选择一条记录！");
        return false;
     }else if(selected.length>1)
     { 
        alert("请选择一条记录！");
        return false;
     }else
     {
        location=selected[0];
        var i9999=document.getElementById("nb"+location).value; 
        var a0100=document.getElementById("ab"+location).value;
        //增加传入人员库
        var nbase=document.getElementById("db"+location).value; 
        //var url="/kq/options/manager/changemagcard.do?b_search=link&i9999="+i9999+"&a0100="+a0100;
        var url="/kq/options/manager/changemagcard.do?b_search=link&i9999="+i9999+"&a0100="+a0100+"&nbase="+nbase;
         return_vo= window.showModalDialog(url, "", 
              	"dialogWidth:600px; dialogHeight:450px;resizable:no;center:yes;scroll:no;status:no");
     }
}
var str="";
function downCard()
{
   var len=document.magCardManagerForm.elements.length;
     var uu;
     var location=1;
     var a=0; 
     var selected=new Array();
     for (i=0;i<len;i++)
     {
           if (document.magCardManagerForm.elements[i].type=="checkbox")
            {
              if(document.magCardManagerForm.elements[i].checked==true)
              {
                 selected[a++]=location;
              }
              location++;
            }
     }
     if(selected==null||selected.length<=0)
     {
        alert("请选择人员！");
        return false;
     }     
     for(i=0;i<selected.length;i++)
     {
           location=selected[i];
           var kqcard=document.getElementById("kb"+location).value; 
           if(kqcard=="")
             continue;
           var a0101=document.getElementById("mb"+location).value; 
           str=str+kqcard+"|"+a0101+",";
     }
     if(str!="")
     {
        str=str.substring(0,str.length-1);
     }
     var num=$F('machineid');  
     if(num==""||num==null)
     {
       alert("请选择考勤机！");
       return false;
     }
     var hashvo=new ParameterSet(); 
     hashvo.setValue("machine_num",num);
     var request=new Request({method:'post',onSuccess:showSelect,functionId:'15211001110'},hashvo);    
}
function showSelect(outparamters)
{
      var machine_no=outparamters.getValue("machine_no");   
      var baud_rate=outparamters.getValue("baud_rate");  
      var port=outparamters.getValue("port");
      var type_id=outparamters.getValue("type_id");   
      var ip_address=outparamters.getValue("ip_address");   
      var cardno_len=outparamters.getValue("cardno_len");  
      if(!AxManager.setup(null, "KqMachine", 0, 0, null, AxManager.kqmachPkgName, null, false, "<%=url_p%>"))
  		 return;  	
      var obj=document.getElementById('KqMachine');       
      if(obj!=null)
      {
         obj.SetParam(type_id,machine_no,port,baud_rate,ip_address,cardno_len); 
         obj.SendCardNO(str);
         str="";
      }
}
function returnSendCard()
{
	newwindow=window.open("/kq/options/manager/usermanager.do?b_search=link&action=usermanagerdata.do&target=mil_body&flag=noself&menu=1",'il_body');
}
  function initCard()
{
      var rl = document.getElementById("hostname").href;     
      var aurl=rl;//tomcat路径
      var DBType="${magCardManagerForm.dbType}";//1：mssql，2：oracle，3：DB2
      var UserName="<%=userName%>";  //登陆用户名暂时用su     
      var obj = document.getElementById('CardPreview1');  
      if(obj==null)
      {
         return false;
      } 
      var superUser="<%=superUser%>";
      var menuPriv="<%=fields%>";
      var tablePriv="<%=tables%>";

      obj.SetSuperUser(superUser);  // 1为超级用户,0非超级用户
      obj.SetUserMenuPriv(menuPriv);  // 指标权限, 逗号分隔, 空表示全权
      obj.SetUserTablePriv(tablePriv);  // 子集权限, 逗号分隔, 空表示全权         
      obj.SetURL(aurl);
      obj.SetDBType(DBType);
      obj.SetUserName(UserName);
      obj.SetUserFullName("<%=userFullName%>");
      obj.SetHrpVersion("<%=version%>");
      obj.SetTrialDays("<%=usedday%>","30");
}
function printCard()
{
   var hashvo=new ParameterSet();
   var inforkind="1";   
   var pers=new Array();
   hashvo.setValue("inforkind","1"); 
   var a0100s="";
   var len=document.magCardManagerForm.elements.length;
   var location=1;
   
   for (i=0;i<len;i++)
   {
     if (document.magCardManagerForm.elements[i].type=="checkbox")
     {
        if(document.magCardManagerForm.elements[i].checked==true)
        {
          var a0100=document.getElementById("ab"+location).value; 
          var base =document.getElementById("db"+location).value;
          a0100s=base+"`"+a0100;         
          pers[i]=a0100s;
        }
        location++;
     }
   }     
   if(pers==null|pers.length<=0)
   {
      alert("请选择人员！");
      return false;
   }
   hashvo.setValue("pers",pers); 
   var request=new Request({method:'post',onSuccess:showPrint,functionId:'07020100079'},hashvo);
}

function showPrint(outparamters)
{
   var personlist=outparamters.getValue("personlist");    
   if(personlist==null)
    return false;
   var cardid="${magCardManagerForm.magcard_cardid}"; 
  if(cardid=="")
  {
     alert("没有定义工作证登记表!");
     return false;
  }
  var obj = document.getElementById('CardPreview1');
  if(obj==null)
  {
      alert("没有下载打印控件，请设置IE重新下载！");
      return false;
  }
  obj.SetCardID("${magCardManagerForm.magcard_cardid}");
  obj.SetDataFlag("1");
  obj.SetNBASE("usr");
  obj.ClearObjs();  
  var isCorrect=false;
  for(var i=0;i<personlist.length;i++)
  {
       obj.AddObjId(personlist[i].dataValue);
  } 
  try { obj.SetJSessionId(AxManager.getJSessionId()); } catch(err) {}
  obj.ShowCardModal();
   
}
function openwin(id,nbase,i9999)
{
    var url="/kq/options/manager/editmagcard.do?b_search=link&i9999="+i9999+"&a0100="+id+"&nbase="+nbase;
    return_vo= window.showModalDialog(url, "", 
              	"dialogWidth:600px; dialogHeight:450px;resizable:no;center:yes;scroll:no;status:no");
}
</SCRIPT><hrms:themes /> <!-- 7.0css -->
 <% int s=0;%>
<html:form action="/kq/options/manager/magcarddata">
<a href="<%=url_p%>" style="display:none" id="hostname">for vpn</a>
<table width="95%" border="0" cellspacing="0"  align="center" cellpadding="0">
<tr>
   <td>
      <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
      <tr>
	   <td align="left" width="300">
		 <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
         <tr>
	      <td align="left">
            <hrms:menubar menu="menu2" id="menubar1" target="mil_body">  
              <hrms:menuitem name="gz3" label="infor.menu.query">
      			<hrms:menuitem name="mitem1" label="infor.menu.squery" icon="" url="searchInform(1,1,'${magCardManagerForm.a_code}','${magCardManagerForm.select_pre}');" command="" enabled="true" visible="true"/>
      			<hrms:menuitem name="mitem2" label="infor.menu.hquery" icon="" url="searchInform(1,2,'${magCardManagerForm.a_code}','${magCardManagerForm.select_pre}');" command="" enabled="true" visible="true"/>
      			<hrms:menuitem name="mitem3" label="infor.menu.gquery" icon="" url="">
      				<%int n=4;%>
      				<logic:iterate id="element"  name="magCardManagerForm"  property="searchlist" indexId="index">  
      		 		<%
            			CommonData searhcitem=(CommonData)pageContext.getAttribute("element");
            			String searchname=searhcitem.getDataValue();
            			String id=searhcitem.getDataName();
            			String a_code = (String)request.getParameter("code");
            			MagCardManagerForm magCardManagerForm = (MagCardManagerForm)session.getAttribute("magCardManagerForm");
            			String searchgeneral = "searchGeneral(1,"+id+",'"+a_code+"','"+magCardManagerForm.getSelect_pre()+"');";
           	 		%>
      				<hrms:menuitem name='<%="mitem"+n+""%>' label='<%=searchname%>' icon="" url="<%=searchgeneral%>" command="" enabled="true" visible="true"/>
      				<%n++;%>
      				</logic:iterate>
      				<hrms:menuitem name='<%="mitem"+(n+1)+""%>' label='general.inform.search.themore' icon="" url="searchInform(1,3,'${magCardManagerForm.a_code}','${magCardManagerForm.select_pre}');" command="" enabled="true" visible="true"/>
      			</hrms:menuitem>
      			<logic:equal name="magCardManagerForm" property="viewsearch" value="1">
      	 			<hrms:menuitem name="mitem100" label="general.inform.search.view.result" icon="" url="searchOk(0);" checked="true" groupindex="1"/>
  	  			</logic:equal>
  	  			<logic:equal name="magCardManagerForm" property="viewsearch" value="0">
  	  				<hrms:menuitem name="mitem100" label="general.inform.search.view.result" icon="" url="searchOk(1);" groupindex="1"/>
  	  			</logic:equal>
  			  </hrms:menuitem> 
              <hrms:menuitem name="rece2" label="发卡登记" >
	              <hrms:menuitem name="mitem3" label="打印工作证" icon="/images/write.gif" url="javascript:printCard();" command=""  function_id="270822"/>
	              <hrms:menuitem name="mitem2" label="发卡" icon="/images/view.gif" url="javascript:send_card();" command="" function_id="" />
	              <hrms:menuitem name="mitem2" label="换卡" icon="/images/sort.gif" url="javascript:change_card();" command="" function_id="" />
	              <hrms:menuitem name="mitem3" label="下传数据" icon="/images/write.gif" url="javascript:downCard();" command=""  function_id=""/>
	              <hrms:menuitem name="mitem3" label="返回" icon="/images/export.gif" url="javascript:returnSendCard();" command=""  function_id=""/>
              </hrms:menuitem>   
           </hrms:menubar>
          </td>
         </tr>
        </table>
      </td>
      <td nowrap align="left">
      &nbsp;
         <html:select name="magCardManagerForm" property="select_pre" size="1" onchange="change();">
                <html:optionsCollection property="kq_list" value="dataValue" label="dataName"/>	        
          </html:select> 
             &nbsp;姓名
         <input type="text" name="select_name" value="${magCardManagerForm.select_name}" class="inputtext" style="width:100px;font-size:10pt;text-align:left">			
         <input type="button" name="br_return" value='查询' class="mybutton" onclick="change();">          
         <html:hidden name="magCardManagerForm" property="magcard_setid" styleId="setname"/>
         考勤机
         <html:select name="magCardManagerForm" property="machineid" size="1">
                <html:optionsCollection property="machinelist" value="dataValue" label="dataName"/>	        
          </html:select> 
      </td>
     </tr>
    </table>
   </td>   
  </tr>
  <tr>
  <td> 
   <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap>
		&nbsp;<bean:message key="column.select"/>&nbsp;           
	        </td>  
	         <td align="center" class="TableRow" nowrap>
		&nbsp;<bean:message key="column.operation"/>&nbsp;           
	        </td> 
            <logic:iterate id="element"   name="magCardManagerForm"  property="fieldlist" indexId="index">
             <logic:equal name="element" property="visible" value="true">
               <td align="center" class="TableRow" nowrap>
                 <bean:write  name="element" property="itemdesc" filter="true"/>
	           </td> 
	          </logic:equal> 
            </logic:iterate> 
          </tr>
   	  </thead>    	 
   	  <hrms:paginationdb id="element" name="magCardManagerForm" sql_str="magCardManagerForm.strsql" table="" where_str="" columns="magCardManagerForm.columns" order_by="magCardManagerForm.orderby" page_id="pagination" pagerows="20" distinct="" keys="" indexes="indexes">
   	   <%
          if(s%2==0){ 
          %>
          <tr class="trShallow" onClick="javascript:tr_onclick(this,'')">
          <%
          }else{
          %>
          <tr class="trDeep" onClick="javascript:tr_onclick(this,'DDEAFE')">
          <%}s++;  
          request.setAttribute("num",s+"");  
          request.setAttribute("nb","nb"+s);           
          request.setAttribute("ab","ab"+s); 
          request.setAttribute("mb","mb"+s);           
          request.setAttribute("kb","kb"+s);  
          request.setAttribute("db","db"+s);               
          %>
          <td align="center" class="RecordRow" nowrap>   
                <hrms:checkmultibox name="magCardManagerForm" property="pagination.select" value="true" indexes="indexes"/>&nbsp;
                <html:hidden name="element" property="i9999" styleId='${nb}'/>
            	<html:hidden name="element" property="a0100" styleId='${ab}'/>
            	<html:hidden name="element" property="name" styleId='${mb}'/>
            	<html:hidden name="element" property="kqcard" styleId='${kb}'/>
            	<html:hidden name="element" property="nbase" styleId='${db}'/>
          </td>
          <td align="center" class="RecordRow" nowrap> 
          <a href="###" onclick="openwin('<bean:write name="element" property="a0100" filter="true"/>','<bean:write name="element" property="nbase" filter="true"/>','<bean:write name="element" property="i9999" filter="true"/>');">
            <img src="/images/edit.gif" border="0"> 
          </a>
          </td>
          <logic:iterate id="info" name="magCardManagerForm"  property="fieldlist" indexId="index">  
           <logic:equal name="info" property="visible" value="true">
             <td align="left" class="RecordRow" nowrap>&nbsp;
                <logic:notEqual name="info" property="codesetid" value="0">
                      <hrms:codetoname codeid="${info.codesetid}" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page"/>  	      
                       &nbsp;<bean:write name="codeitem" property="codename" />&nbsp;                              
                </logic:notEqual>
                 <logic:equal name="info" property="codesetid" value="0">
                  <logic:notEqual name="info" property="itemtype" value="D">   
                    <bean:write name="element" property="${info.itemid}"  filter="false"/>&nbsp;                    
                  </logic:notEqual>
                  <logic:equal name="info" property="itemtype" value="D">
                    <bean:write name="element" property="${info.itemid}"  filter="false"/>&nbsp;      
                  </logic:equal>
                 </logic:equal>
             </td>
           </logic:equal>
          </logic:iterate>
          </tr>
   	  </hrms:paginationdb>
   </table>
   </td>
   </tr>
   <tr>
     <td width="100%" align="center">
       <table  width="100%" class="RecordRowP" align="center">
		<tr>
		 <td valign="bottom" class="tdFontcolor"><bean:message key="label.page.serial"/>
					<bean:write name="pagination" property="current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="pagination" property="count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="pagination" property="pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	        <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="magCardManagerForm" property="pagination" nameId="magCardManagerForm" scope="page">
				</hrms:paginationdblink></p>
			</td>
		</tr>
      </table>
     </td>
   </tr>
</table> 
</html:form>
<script language="javascript" src="/general/sys/hjaxmanage.js"></script>
<script language="javascript">AxManager.writeCard();</script>
<script language="javascript">
hide_nbase_select('select_pre');
 initCard();
   
</script>