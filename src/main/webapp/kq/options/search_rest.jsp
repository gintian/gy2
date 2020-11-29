<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%> 
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script type="text/javascript" src="../../general/tipwizard/returnT.js"></script>
<script type="text/javascript" src="../../js/hjsjUrlEncode.js"></script>
<%
	int i = 0;
%>

<script language="javascript">
//屏蔽右键,实在没有办法的采用此办法,解决重复提交问题
document.body.oncontextmenu=function(){return false;};
  function adds()
  {
      target_url="/kq/options/add_turnrest.do?b_add=link";
      var config={
	    		width:550,
	            height:365,
	            id:"turnRestWin",
	            title:"新增"
	    }
	    return_vo=modalDialog.showModalDialogs(target_url,"新增",config,saveTurnRest)
	    // 不知道为什么要走两遍保存的方法saveTurnRest，下面编辑同样也是
	    //if(return_vo)
	    	//saveTurnRest(return_vo);
  }
  
  function saveTurnRest(return_vo) {
	  if(return_vo.start!=""&&return_vo.end!="")
      {
         var hashvo=new ParameterSet();			
	     hashvo.setValue("rdate",return_vo.start);
	     hashvo.setValue("tdate",return_vo.end);	
	     hashvo.setValue("tid",return_vo.tid);   
	     var request=new Request({method:'post',asynchronous:false,onSuccess:showResut,functionId:'15202210002'},hashvo);
      }
  }
  
  function showResut(outparamters)
  {
	// kqTurnRestForm.action="/kq/options/search_rest.do?b_query=link";
	// kqTurnRestForm.target="il_body";
	     
     var ms=outparamters.getValue("mess");	
     if(ms=="2")
     {
       mess();  
       window.location.href="/kq/options/search_rest.do?b_query=link&mege=4";
       return false;
     }else if(ms=="3")
     {
       messs();
       window.location.href="/kq/options/search_rest.do?b_query=link&mege=4";
       return false;
     }else if(ms=="4") 
     {
        messss();
        window.location.href="/kq/options/search_rest.do?b_query=link&mege=4";
        return false;
     }else if(ms=="5") 
     {
        alert("倒休日期不能为公休日,请重新设置!");
        window.location.href="/kq/options/search_rest.do?b_query=link&mege=4";
        return false;
     } 
     window.location.href="/kq/options/search_rest.do?b_query=link&mege=4";
   //  kqTurnRestForm.submit();     
  }
  function edit(str)
  {		
	  var dat = new Date(); 
      var target_url="/kq/options/add_turnrest.do?b_edit=link`t_id="+str+"`tim="+dat.getTime()+"`target=rr";
      if($URL)
      	target_url = $URL.encode(target_url);
	  var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
	  var return_vo= window.showModalDialog(iframe_url,"rr", 
           "dialogWidth:510px; dialogHeight:260px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");

	  if(!return_vo) {
		return false;	
	  }
	      
      if(return_vo.start!=""&&return_vo.end!="")
      {
         var hashvo=new ParameterSet();			
	     hashvo.setValue("rdate",return_vo.start);
	     hashvo.setValue("tdate",return_vo.end);	
	     hashvo.setValue("tid",return_vo.tid);  	     
	     var request=new Request({method:'post',asynchronous:false,onSuccess:showResut,functionId:'15202210002'},hashvo);
      } 
  }
  function mess()
  {
     alert("您选的不是公休日，请重新选择！");
  }
   function messs()
  {
     alert("您选的公休日已经设置过倒休，请重新选择！");
  }
  
    function messss()
  {
     alert("您选的倒休日已经设置过倒休，请重新选择！");
  }
  function turnRest()
  {
     alert("不允许删除其他单位指定的倒休！");
  }
function delcheck()
{
	var sss = document.kqTurnRestForm.elements.length;
	var bbb = false;
	for(var i=0;i<sss;i++){
		if(document.kqTurnRestForm.elements[i].type=="checkbox"){
			if(document.kqTurnRestForm.elements[i].checked == true && 
					document.kqTurnRestForm.elements[i].name != "selbox")
				bbb = true;
		}
	}
	if(bbb){
		if(confirm("是否删除选择的记录？"))
		{
			kqTurnRestForm.action = "/kq/options/search_rest.do?b_delete=link";
			kqTurnRestForm.submit();
		} 
	}else{
		alert("请选择需要删除的记录！");
	}
}
window.onload=function(){
	document.getElementById("contentid").style.height=document.body.clientHeight-250;
}
</script>
<html:form action="/kq/options/search_rest">
	<br>
	<div align="center">
	<fieldset align="center" style="width: 50%;padding:0px">
		<legend>
			说明&nbsp;
		</legend>
		<table width="90%" border="0" cellspacing="1" align="center"
			cellpadding="1">
			<tr>
				<td style="padding-bottom: 10px">
					1.若子单位单独设置了公休日，那么公休日倒休只能按该子单位设置的公休日处理！
					<br>
					2.倒休机构为空的倒休，适用于没有定义公休日的单位！
				</td>
			</tr>
		</table>
	</fieldset>
	</div>
	<br>
	<br>
		<table width="50%" border="0" cellspacing="0" align="center"
			cellpadding="0" class="ListTable">
				<tr>
					<td>
		<div id="contentid" style="width: 100%;overflow: auto;border: 1px solid #C4D8EE;" class="common_border_color">
						<table style="border-collapse: collapse;" width="100%">
							<tr>
								<td align="center" class="TableRow" style="border-left: none;border-top:none;width: 30px;" nowrap>
									<input type="checkbox" name="selbox"
										onclick="batch_select(this, 'kqTurnRestForm.select');"
										title='<bean:message key="label.query.selectall"/>'>
								</td>
								<td align="center" class="TableRow" style="border-top: none;width: 32%;" nowrap>
									倒休机构
								</td>
								<td align="center" class="TableRow" style="border-top: none;width: 28%;" nowrap>
									<bean:message key="kq.rest.rdate" />
								</td>
								<td align="center" class="TableRow" style="border-top: none;width: 28%;" nowrap>
									<bean:message key="kq.rest.tdate" />
								</td>
								<td align="center" class="TableRow" style="border-top: none;border-right: none;" nowrap>
									<bean:message key="kq.feast_type_list.modify" />
								</td>
							</tr>
						
						<hrms:extenditerate id="element" name="kqTurnRestForm"
							property="kqTurnRestForm.list" indexes="indexes"
							pagination="kqTurnRestForm.pagination" pageCount="20"
							scope="session">
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
												i++;
								%>
								<td align="center" class="RecordRow"  style="border-left: none;" nowrap>
									<hrms:checkmultibox name="kqTurnRestForm"
										property="kqTurnRestForm.select" value="true" indexes="indexes" />
								</td>
								<td align="left" class="RecordRow" nowrap>
									&nbsp;
									<bean:write name="element" property="string(description)"
										filter="true" />
									&nbsp;
								</td>
								<td align="left" class="RecordRow" nowrap>
									&nbsp;
									<bean:write name="element" property="string(week_date)"
										filter="true" />
									&nbsp;
								</td>
								<td align="left" class="RecordRow" nowrap>
									&nbsp;
									<bean:write name="element" property="string(turn_date)"
										filter="true" />
									&nbsp;
								</td>
								<td align="center" class="RecordRow" style="border-right: none;" nowrap>
									<a
										onclick="edit('<bean:write name="element" property="string(turn_id)" filter="true"/>');"><img
											src="/images/edit.gif" border=0>
									</a>
								</td>
							</tr>
						</hrms:extenditerate>
						</table>
                      </div>
					</td>
				</tr>
			<tr>
				<td align="center" nowrap class="RecordRow" style="border-top: none;" colspan="5">
					<table width="100%">
						<tr>
							<td align="right" nowrap class="tdFontcolor">
								<hrms:paginationlink name="kqTurnRestForm"
									property="kqTurnRestForm.pagination" nameId="kqTurnRestForm">
								</hrms:paginationlink>
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
		<!-- 
		<table width="90%" align="center">
			<td align="right" nowrap class="tdFontcolor">
					<hrms:paginationlink name="kqTurnRestForm"
						property="kqTurnRestForm.pagination" nameId="kqTurnRestForm">
					</hrms:paginationlink>
				</td>
		</table>
		 -->
	<table width="70%" align="center">
		<tr>
			<td align="center" height="30px">
				<input type="button" name="b_saveb"
					value="<bean:message key="button.insert"/>" class="mybutton"
					onclick="adds()">
				<input type="button" class="mybutton" name="b_delete"
					value="<bean:message key="button.delete" />"
					onclick="delcheck();">
				<logic:notEmpty name="kqTurnRestForm" property="gw_flag">
			   	     <input type="button" value="<bean:message key="button.close"/>" class="mybutton" onclick="window.close();"/>
				</logic:notEmpty>
				<hrms:tipwizardbutton flag="workrest" target="il_body"
					formname="kqTurnRestForm" />
			</td>
		</tr>
	</table>
	<logic:equal name="kqTurnRestForm" property="mess" value="2">
		<script language="javascript">
	mess();
</script>
	</logic:equal>
	<logic:equal name="kqTurnRestForm" property="mess" value="3">
		<script language="javascript">
	messs();
</script>
	</logic:equal>

	<logic:equal name="kqTurnRestForm" property="mess" value="4">
		<script language="javascript">
	messss();
</script>
	</logic:equal>

	<logic:notEqual name="kqTurnRestForm" property="mess" value="2">

	</logic:notEqual>
	<logic:equal name="kqTurnRestForm" property="turnRest_flag" value="1">
		<script language="javascript">
	turnRest();
</script>
	</logic:equal>
</html:form>

