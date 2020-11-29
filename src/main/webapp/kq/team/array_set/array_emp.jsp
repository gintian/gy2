<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
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
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/kq/kq.js"></script>
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
</script>
<script language="javascript"> 
  function submitDEL()
  {
  		var str="";
		for(var i=0;i<document.arrayGroupForm.elements.length;i++)
			{
				if(document.arrayGroupForm.elements[i].type=="checkbox")
				{
					if(document.arrayGroupForm.elements[i].checked==true)
					{
						if(document.arrayGroupForm.elements[i].name=="selbox")
							continue;
							str+=document.arrayGroupForm.elements[i].value+"/";
					}
				}
			}
		if(str.length==0)
		{
				alert("请选择！");
				return;
		}
		else{
			if(confirm("确定要删除所选记录吗？"))
    			{
       				arrayGroupForm.action="/kq/team/array_group/search_array_emp_data.do?b_delete=link&group_id=${arrayGroupForm.group_id}";
       				arrayGroupForm.submit();
    			}
		}
  }
  function getEmploy(group_id)
  {
	 var return_vo=select_array_emp_dialog(1,1,1);
	 if(return_vo)
	 {
         //var hashvo=new ParameterSet();
	     //hashvo.setValue("a0100s",return_vo.content);
	     //hashvo.setValue("group_id",group_id);
         //var request=new Request({asynchronous:false,onSuccess:searchemp,functionId:'15221300007'},hashvo);
         var t_urll="/kq/team/array_group/load_zidong_class.do?b_query=link";
     	 var iframe_urll="/general/query/common/iframe_query.jsp?src="+t_urll;
     	 var returnd_vo= window.showModalDialog(iframe_urll,'rr', 
       			"dialogWidth:620px; dialogHeight:380px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
       	 if(!returnd_vo){
	     	loadpage();
	     	return false;
	     }
	     if(returnd_vo.flag=="true")
	     {
	     	var hashvo=new ParameterSet();
            hashvo.setValue("a0100s",return_vo.content);
	        hashvo.setValue("group_id",group_id);
	        hashvo.setValue("start_date",returnd_vo.start_date);
  	 		hashvo.setValue("end_date",returnd_vo.end_date);
  	 		hashvo.setValue("flag",returnd_vo.flag);
  	 		hashvo.setValue("zhji",returnd_vo.zhji);
  	 		var request=new Request({asynchronous:false,onSuccess:searchemp,functionId:'15221300007'},hashvo);
	     }else
	     {
	     	var hashvo=new ParameterSet();
            hashvo.setValue("a0100s",return_vo.content);
	        hashvo.setValue("group_id",group_id);
	        hashvo.setValue("start_date",returnd_vo.start_date);
  	 		hashvo.setValue("end_date",returnd_vo.end_date);
  	 		hashvo.setValue("flag",returnd_vo.flag);
  	 		hashvo.setValue("zhji",returnd_vo.zhji);
  	 		var request=new Request({asynchronous:false,onSuccess:searchemp,functionId:'15221300007'},hashvo);
	     }     
	 }
  }
  function getHardEmploy(group_id)
  {
        var t_url="/kq/team/array_group/load_emp_data_record.do?b_search=link";
        var iframe_url="/general/query/common/iframe_query.jsp?src="+t_url;
        var return_vo= window.showModalDialog(iframe_url,'rr', 
            "dialogWidth:556px; dialogHeight:540px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
        if(!return_vo){
	        return false;
	    } 
        if(return_vo.flag=="true")
        {
            //var hashvo=new ParameterSet();
            //hashvo.setValue("a0100s",return_vo.str);
	        //hashvo.setValue("group_id",group_id);
            //var request=new Request({asynchronous:false,onSuccess:searchemp,functionId:'15221300007'},hashvo);
            var t_urll="/kq/team/array_group/load_zidong_class.do?b_query=link";
     		var iframe_urll="/general/query/common/iframe_query.jsp?src="+t_urll;
     		var returnd_vo= window.showModalDialog(iframe_urll,'rr', 
       				"dialogWidth:620px; dialogHeight:380px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");       		
       		if(!returnd_vo){
	     		return false;
	     	}
	        if(returnd_vo.flag=="true")
	        {
	            var hashvo=new ParameterSet();
            	hashvo.setValue("a0100s",return_vo.str);
	        	hashvo.setValue("group_id",group_id);
	        	hashvo.setValue("start_date",returnd_vo.start_date);
  	 			hashvo.setValue("end_date",returnd_vo.end_date);
  	 			hashvo.setValue("flag",returnd_vo.flag);
  	 			hashvo.setValue("zhji",returnd_vo.zhji);
            	var request=new Request({asynchronous:false,onSuccess:searchemp,functionId:'15221300007'},hashvo);
	        }else
	        {
	            var hashvo=new ParameterSet();
            	hashvo.setValue("a0100s",return_vo.str);
	        	hashvo.setValue("group_id",group_id);
	        	hashvo.setValue("start_date",returnd_vo.start_date);
  	 			hashvo.setValue("end_date",returnd_vo.end_date);
  	 			hashvo.setValue("flag",returnd_vo.flag);
  	 			hashvo.setValue("zhji",returnd_vo.zhji);
            	var request=new Request({asynchronous:false,onSuccess:searchemp,functionId:'15221300007'},hashvo);
	        }
        }
  }
  function getFil(group_id)
  {
  	var nbase = document.getElementById("dbper").value;
  	var hashvo=new ParameterSet();
  	hashvo.setValue("nbase1",nbase);
  	hashvo.setValue("group_id1",group_id);
  	var request=new Request({asynchronous:false,onSuccess:judgeuser,functionId:'15221300014'},hashvo);
  }
  function searchemp(outparamters)
  {
      var save_flag=outparamters.getValue("save_flag"); 
      if(save_flag=="true")
      {
        loadpage();
        arrayGroupForm.action="/kq/team/array_group/search_array_emp_data.do?b_search=link&group_id=${arrayGroupForm.group_id}";
        arrayGroupForm.submit();
      }else
      {
         alert("添加人员失败！");
      }
  }
  function clearCheackbox()
  {
      var len=document.arrayGroupForm.elements.length;
      var i;
      for (i=0;i<len;i++)
      {
          if (document.arrayGroupForm.elements[i].type=="checkbox")
          {
            document.arrayGroupForm.elements[i].checked=false;
          }
      } 
  } 
  function judgeuser(outparamters)
  {
		var msg=outparamters.getValue("msg"); 
		var nbase=outparamters.getValue("nbase");
		var group_id=outparamters.getValue("group_id");		
		if(msg=="true")
		{
			var t_user="/kq/team/array_group/load_host_data_record.do?b_search=link`nbase="+nbase+"`group_id="+group_id;
  			var iframe_url="/general/query/common/iframe_query.jsp?src="+t_user;
  			var return_vo= window.showModalDialog(iframe_url,'rr', 
            		"dialogWidth:556px; dialogHeight:480px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
  			if (return_vo == 'true') 
              loadpage();
		}else{
			var str="";
			var t_url="/kq/team/array_group/load_zidong_class.do?b_search=link";
     		var iframe_url="/general/query/common/iframe_query.jsp?src="+t_url;
     		var return_vo= window.showModalDialog(iframe_url,'rr', 
       				"dialogWidth:420px; dialogHeight:360px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");     		
	 		if(return_vo&&return_vo.flag=="true")
	 		{
	 			var hashvo=new ParameterSet();
  	 			hashvo.setValue("group_id",group_id);
  	 			hashvo.setValue("start_date",return_vo.start_date);
  	 			hashvo.setValue("end_date",return_vo.end_date);
  	 			hashvo.setValue("nbase",nbase);
  	 			hashvo.setValue("a0100s",str);
  	 			var request=new Request({asynchronous:false,onSuccess:searchemp,functionId:'15221300009'},hashvo);
	 		}else
	 		{
	 		  loadpage();
	 		}
		} 
  }
  function goback()
  {
     var len=document.arrayGroupForm.elements.length;
     var i;
     for (i=0;i<len;i++)
     {
           if (document.arrayGroupForm.elements[i].type=="checkbox")
            {
              document.arrayGroupForm.elements[i].checked=false;
            }
      }
     arrayGroupForm.action="/kq/team/array_group/search_array_emp_data.do?br_return=link";
     arrayGroupForm.submit();
  }
  function selectEmploy(group_id)
  {
     var len=document.arrayGroupForm.elements.length;
     var i;
     for (i=0;i<len;i++)
     {
           if (document.arrayGroupForm.elements[i].type=="checkbox")
            {
              document.arrayGroupForm.elements[i].checked=false;
            }
      }
     arrayGroupForm.action="/kq/team/array_group/selectfiled.do?b_init=link&group_id="+group_id;
     arrayGroupForm.submit();
  }
  function change()
  {
    clearCheackbox();
    arrayGroupForm.action="/kq/team/array_group/search_array_emp_data.do?b_search=link&group_id=${arrayGroupForm.group_id}";
    arrayGroupForm.submit();
  }
     var checkflag = "false";

 function selAll()
  {
      var len=document.arrayGroupForm.elements.length;
       var i;

    
  
     if(checkflag=="false")
     {
        for (i=0;i<len;i++)
        {
           if (document.arrayGroupForm.elements[i].type=="checkbox")
            {
              document.arrayGroupForm.elements[i].checked=true;
            }
         }
        checkflag="true";
     }else
     {
        for (i=0;i<len;i++)
        {
          if (document.arrayGroupForm.elements[i].type=="checkbox")
          {
            document.arrayGroupForm.elements[i].checked=false;
          }
        }
        checkflag = "false";    
    } 
        
  }
  function putclass()
  {
  	    var str="";
		for(var i=0;i<document.arrayGroupForm.elements.length;i++)
			{
				if(document.arrayGroupForm.elements[i].type=="checkbox")
				{
					if(document.arrayGroupForm.elements[i].checked==true)
					{
						if(document.arrayGroupForm.elements[i].name=="selbox")
							continue;
							str+=document.arrayGroupForm.elements[i].value+"/";
					}
				}
			}
		if(str.length==0)
		{
				alert("请选择需要调换的人员！");
				return;
		}else
		{
			 var t_url="/kq/team/array_group/search_array_emp_data.do?b_putclass=link&group_id=${arrayGroupForm.group_id}";
     		 var iframe_url="/general/query/common/iframe_query.jsp?src="+t_url;
     		 var return_vo= window.showModalDialog(iframe_url,'rr', 
         			"dialogWidth:420px; dialogHeight:300px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");     		 
	     	 if(return_vo&&return_vo.flag=="true")
	     	 {
	     	    var waitInfo=eval("wait");	   
	            waitInfo.style.display="block"; 
	     	    arrayGroupForm.action="/kq/team/array_group/search_array_emp_data.do?b_saveclass=link&classId="+return_vo.joincodename+"&start_date_save="+return_vo.start_date+"&end_date_save="+return_vo.end_date+"&zhji="+return_vo.zhji;
                arrayGroupForm.submit();                
	     	 }
		}
		
  }
  function loadpage()
  {
      clearCheackbox();
      window.location.href="/kq/team/array_group/search_array_emp_data.do?b_search=link";      
  }

</script>
<%
int i = 0;
%>
<html:form action="/kq/team/array_group/search_array_emp_data">
	<table border="0" cellspacing="0" align="center" cellpadding="0"
		width="100%">
		<tr>
			<td align="left">
				<html:hidden name="arrayGroupForm" property="a_code" />
				<hrms:optioncollection name="arrayGroupForm" property="dlist" collection="list" />
				<html:select name="arrayGroupForm" property="dbper" size="1" onchange="change();">
					<html:options collection="list" property="dataValue" labelProperty="dataName" />
				</html:select>
				 &nbsp;按 &nbsp; <html:select name="arrayGroupForm" property="select_type"  size="1">
            	<html:option value="0"><bean:message key="label.title.name" /></html:option>                      
                <html:option value="1">工号</html:option>
                <html:option value="2">考勤卡号</html:option>
           </html:select>
				<html:text name="arrayGroupForm" styleClass="inputtext" property="a0101_s" size="20"
					maxlength="30"></html:text>
				<html:button styleClass="mybutton" property="b_all"
					onclick="change();">
					<bean:message key="button.query" />
				</html:button>
			</td>
		</tr>
		<tr>
			<td width="90%">
				<table width="100%" border="0" cellspacing="0" align="center"
					cellpadding="0" class="ListTable">
					<thead>
						<tr>
							<td align="center" class="TableRow" nowrap>
								<input type="checkbox" name="aa" value="true" onclick="selAll()">
								&nbsp;
							</td>
							<logic:iterate id="element" name="arrayGroupForm"
								property="fieldlist" indexId="index">
								<logic:equal name="element" property="visible" value="true">
									<td align="center" class="TableRow" nowrap>
										<bean:write name="element" property="itemdesc" />
										&nbsp;
									</td>
								</logic:equal>
							</logic:iterate>
							<td align="center" class="TableRow" nowrap>
								班组
							</td>
					</thead>
					<hrms:paginationdb id="element" name="arrayGroupForm"
						sql_str="arrayGroupForm.sqlstr" table=""
						where_str="arrayGroupForm.where" columns="arrayGroupForm.column"
						order_by="order by b0110,e0122" pagerows="19" page_id="pagination">
						<%
						if (i % 2 == 0) {
						%>
						<tr class="trShallow">
							<%
							} else {
							%>
						
						<tr class="trDeep">
							<%
							}
							%>
							<td align="center" class="RecordRow" nowrap>
								<hrms:checkmultibox name="arrayGroupForm"
									property="pagination.select" value="true" indexes="indexes" />
								&nbsp;
							</td>
							<logic:iterate id="info" name="arrayGroupForm"
								property="fieldlist" indexId="index">
								<logic:equal name="info" property="visible" value="true">
									<logic:notEqual name="info" property="codesetid" value="0">
										<td align="left" class="RecordRow" nowrap>
										<logic:equal name="info" property="codesetid" value="UM">
											<hrms:codetoname codeid="${info.codesetid}" name="element"
												codevalue="${info.itemid}" codeitem="codeitem" scope="page"  uplevel="${arrayGroupForm.uplevel}"/>
										</logic:equal>
										<logic:notEqual name="info" property="codesetid" value="UM">
											<hrms:codetoname codeid="${info.codesetid}" name="element"
												codevalue="${info.itemid}" codeitem="codeitem" scope="page"/>
										</logic:notEqual>
											&nbsp;
											<bean:write name="codeitem" property="codename" />
											&nbsp;
										</td>
									</logic:notEqual>
									<logic:equal name="info" property="codesetid" value="0">
										<td align="left" class="RecordRow" nowrap>
											&nbsp;
											<bean:write name="element" property="${info.itemid}"
												filter="true" />
											&nbsp;
										</td>
									</logic:equal>
								</logic:equal>
							</logic:iterate>
							<td align="center" class="RecordRow" nowrap>
								&nbsp;
								<bean:write name="arrayGroupForm" property="groupName"
									filter="true" />
								&nbsp;
							</td>
							<%
							i++;
							%>
						</tr>
					</hrms:paginationdb>
				</table>
			</td>
		</tr>
		<tr>
			<td>
				<table width="100%" class="RecordRowP" align="center">
					<tr>
						<td valign="bottom" class="tdFontcolor">
							第
							<bean:write name="pagination" property="current" filter="true" />
							页 共
							<bean:write name="pagination" property="count" filter="true" />
							条 共
							<bean:write name="pagination" property="pages" filter="true" />
							页
						</td>
						<td align="right" nowrap class="tdFontcolor">
							<p align="right">
								<hrms:paginationdblink name="arrayGroupForm"
									property="pagination" nameId="arrayGroupForm" scope="page">
								</hrms:paginationdblink>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td align="center" style="height: 35px;">
				<input type="button" name="tt" value="组织机构选人" class="mybutton"
					onclick="getEmploy('${arrayGroupForm.group_id}');">
				<hrms:priv func_id="27071201,0C351201" module_id="">
					<input type="button" name="tt" value="调换班组" class="mybutton"
						onclick="putclass();">
				</hrms:priv>
				<input type="button" name="tt" value="手工选人" class="mybutton"
					onclick="getHardEmploy('${arrayGroupForm.group_id}');">
				<input type="button" name="tt" value="条件选人" class="mybutton"
					onclick="selectEmploy('${arrayGroupForm.group_id}');">
				<logic:equal name="arrayGroupForm" property="fil" value="1">
					<input type="button" name="tt" value="自动分配班组" class="mybutton"
						onclick="getFil('${arrayGroupForm.group_id}');">
				</logic:equal>
				<input type="button" name="tdf"
					value="<bean:message key="button.delete"/>" class="mybutton"
					onclick="submitDEL();">
				<input type="button" name="btnreturn"
					value='<bean:message key="kq.emp.button.return"/>'
					onclick="goback();" class="mybutton">
			</td>
		</tr>
	</table>
</html:form>
<div id='wait'
	style='position: absolute; top: 200; left: 250; display: none;'>
	<table border="1" width="400" cellspacing="0" cellpadding="4"
		class="table_style" height="87" align="center">
		<tr>

			<td class="td_style common_background_color" height=24>
				<bean:message key="classdata.isnow.wiat" />
			</td>

		</tr>
		<tr>
			<td style="font-size: 12px; line-height: 200%" align=center>
				<marquee class="marquee_style" direction="right" width="300"
					scrollamount="5" scrolldelay="10">
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
   var dropDownDate=createDropDown("dropDownDate");
   var __t=dropDownDate;
   __t.type="date";
   __t.tag="";
   _array_dropdown[_array_dropdown.length]=__t;
   initDropDown(__t);
</script>
<script language="javascript">
    var dropdownCode=createDropDown("dropdownCode");
    var __t=dropdownCode;
    __t.type="custom";
    __t.path="/general/muster/select_code_tree.do";
    __t.readFields="codeitemid";
    //__t.writeFields="xxxx";
    __t.cachable=true;__t.tag="";
    _array_dropdown[_array_dropdown.length]=__t;
    initDropDown(__t);
</script>
<script language="javascript">
  initDocument(); 
  hide_nbase_select('dbper'); 
</script>
