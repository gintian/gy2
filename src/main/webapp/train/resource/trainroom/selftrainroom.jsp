<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.businessobject.train.resource.TrainRoomBo"%>
<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="javascript" src="/js/dict.js"></script>
<script language="JavaScript" src="/js/validateDate.js"></script>
<script type="text/javascript" src="../../../js/hjsjUrlEncode.js"></script>
<style>
body{text-align: center;}
.tbl-container
{  
    overflow:auto; 
    height:expression(document.body.clientHeight-140);
    width:expression(document.body.clientWidth-10); 
    BORDER-BOTTOM: #C4D8EE 1pt solid; 
    BORDER-LEFT: #C4D8EE 1pt solid; 
    BORDER-RIGHT: #C4D8EE 1pt solid; 
    BORDER-TOP: #C4D8EE 1pt solid;
}
.t_cell_locked 
{
    border: inset 1px #C4D8EE;
    BACKGROUND-COLOR: #ffffff;
    BORDER-BOTTOM: #C4D8EE 1pt solid; 
    BORDER-LEFT: 0pt; 
    BORDER-RIGHT: #C4D8EE 1pt solid; 
    /*BORDER-RIGHT:none;*/
    BORDER-TOP: 0pt;
    font-size: 12px;
    border-collapse:collapse; 
    
    background-position : center left;
    left: expression(document.getElementById("tbl-container").scrollLeft); /*IE5+ only*/
    position: relative;
    z-index: 10;
    
}
.t_cell_locked_b {
    border: inset 1px #C4D8EE;
    BORDER-BOTTOM: #C4D8EE 1pt solid; 
    BORDER-LEFT: 0pt; 
    BORDER-RIGHT: #C4D8EE 1pt solid; 
    BORDER-TOP: 0pt;
    font-size: 12px;
    border-collapse:collapse; 
}
.t_header_locked
{
    /*background-image:url(/images/listtableheader_deep-8.jpg);*/
    background-repeat:repeat;
    background-position : center left;
    background-color:#f4f7f7;
    border: inset 1px #C4D8EE;
    BORDER-BOTTOM: #C4D8EE 1pt solid; 
    /*BORDER-BOTTOM:none;*/
    BORDER-LEFT: 0pt; 
    BORDER-RIGHT: #C4D8EE 1pt solid; 
    BORDER-TOP: 0pt;
    valign:middle;
    font-weight: bold;  
    text-align:center;
    top: expression(document.getElementById("tbl-container").scrollTop); /*IE5+ only*/
    position: relative;
    z-index: 15;
}
            
.t_cell_locked2 
{
    /*  background-image:url(/images/listtableheader_deep-8.jpg);*/
    background-repeat:repeat;
    background-position : center left;
    background-color:#f4f7f7;
    BORDER-BOTTOM: #C4D8EE 1pt solid; 
    BORDER-LEFT: 0pt; 
    BORDER-RIGHT: #C4D8EE 1pt solid; 
    BORDER-TOP: 0pt;
    font-weight: bold;  
    valign:middle;
    left: expression(document.getElementById("tbl-container").scrollLeft); /*IE5+ only*/
    top: expression(document.getElementById("tbl-container").scrollTop); /*IE5+ only*/
    position: relative;
    z-index: 20;
    
}
</style>
<hrms:themes></hrms:themes>
<html:form action="/train/resource/trainroom/selftrainroom">
<bean:define id="day" name="facilityInfoForm" property="dateOfMonth" />
    <%
        int i = 0;
    %>
    <table border="0" cellpadding="0" cellspacing="0">
    <tr>
    <td>
        　　&nbsp;<html:select name="facilityInfoForm" property="year" styleId="year" size="1" onchange="search();">
                  <html:optionsCollection property="itemList" value="dataValue" label="dataName"/>          
               </html:select>&nbsp;年
               &nbsp;&nbsp;&nbsp;<html:select name="facilityInfoForm" property="month" styleId="month" size="1" onchange="search();">
                  <html:option value="01">01</html:option>
                  <html:option value="02">02</html:option>
                  <html:option value="03">03</html:option>
                  <html:option value="04">04</html:option>
                  <html:option value="05">05</html:option>
                  <html:option value="06">06</html:option>
                  <html:option value="07">07</html:option>
                  <html:option value="08">08</html:option>
                  <html:option value="09">09</html:option>
                  <html:option value="10">10</html:option>
                  <html:option value="11">11</html:option>
                  <html:option value="12">12</html:option>
               </html:select>&nbsp;月
               &nbsp;&nbsp;&nbsp;
               <logic:equal value="self" name="facilityInfoForm" property="type">
            说明：1、请双击单元格进行申请。
            2、时间段区分：申请(<font color="blue">蓝色</font>)，批准(<font color="green">绿色</font>)，驳回(<font color="red">红色</font>)。
            </logic:equal>
            <logic:notEqual value="self" name="facilityInfoForm" property="type">
            说明：1、双击单元格查看当天申请明细。
            2、时间段区分：申请(<font color="blue">蓝色</font>)，批准(<font color="green">绿色</font>)。
          </logic:notEqual>
    </td>
    </tr>
    <tr>
    <td>
    <div class="tbl-container common_border_color" id="tbl-container" style="margin-top: 5px;">
    <table width="100%" border="0" cellspacing="0" align="center"
        cellpadding="0">
        <thead>
            <tr>
                <td align="center" class="t_cell_locked2 common_border_color" style="height: 25px;">
                    &nbsp;培训场所&nbsp;
                </td>
                <%for(int d=1;d<=Integer.parseInt(day.toString());d++){ 
                	if(d==Integer.parseInt(day.toString())){
                		%>
                			<td align="center" class="t_header_locked common_border_color" style="border-right: none;">
                    	    &nbsp;<%=d %>&nbsp;
                  		  </td>
                		<%
                	}else{
                	String D = String.valueOf(d);
                	if(d<10)
                		D = "0" + D;
                %>
                    <td align="center" class="t_header_locked common_border_color">
                        &nbsp;<%=D %>&nbsp;
                    </td>
                <% }
                }
                %>
            </tr>
        </thead>
        <hrms:paginationdb id="element" name="facilityInfoForm"
            sql_str="facilityInfoForm.strsql" table="" where_str="facilityInfoForm.strwhere"
            columns="facilityInfoForm.columns" page_id="pagination"
            pagerows="${facilityInfoForm.pagerows}" order_by="facilityInfoForm.order_by">
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
                <bean:define id="r1001" name="element" property="r1001"/>
                <%String r1001s = SafeCode.encode(PubFunc.encrypt(r1001.toString())); %>
                <logic:equal value="self" name="facilityInfoForm" property="type"><!-- 自助平台显示列表 -->
                <td class="t_cell_locked common_border_color" nowrap>
                    &nbsp;<a href="###" onclick="trainroominfo('<%=r1001s %>');">[<bean:write name="element" property="r1011" filter="false"/>]</a>&nbsp;
                </td>
                <%for(int j=1;j<=Integer.parseInt(day.toString());j++){
                	if(j==Integer.parseInt(day.toString()))
                	{
                		%>
	                		<td class="t_cell_locked_b common_border_color" onclick="tr_onclick(this,'');" ondblclick="add('<%=j %>','<%=r1001s %>','<bean:write name="element" property="r1011"/>');" id='<%=j %>_<%=r1001s %>' style="line-height: 20px;text-align: left;border-right: none;" nowrap>
	                        &nbsp;<%=TrainRoomBo.getHtml(j+"_"+r1001.toString()) %>
	                  		  </td>
                		<%
                	}else
                	{
                		%>
		                    <td class="t_cell_locked_b common_border_color" onclick="tr_onclick(this,'');" ondblclick="add('<%=j %>','<%=r1001s %>','<bean:write name="element" property="r1011"/>');" id='<%=j %>_<%=r1001s %>' style="line-height: 20px;text-align: left;" nowrap>
		                        &nbsp;<%=TrainRoomBo.getHtml(j+"_"+r1001.toString()) %>
		                    </td>
                		<%
                	}
                } %>
                </logic:equal>
                <logic:notEqual value="self" name="facilityInfoForm" property="type">
                <bean:define id="r1001" name="element" property="r1001"/>
                <td class="t_cell_locked common_border_color" nowrap>
                    &nbsp;<a href="###" onclick="trainroomlist('<%=r1001s %>','<bean:write name="element" property="r1011"/>','');">[<bean:write name="element" property="r1011" filter="false"/>]</a>&nbsp;
                </td>
                <%for(int j=1;j<=Integer.parseInt(day.toString());j++){ 
                	if(j==Integer.parseInt(day.toString())){
                		%>
                		<td class="t_cell_locked_b common_border_color" onclick="tr_onclick(this,'');" ondblclick="trainroomlist('<%=r1001s %>','<bean:write name="element" property="r1011"/>','<%=j %>');" style="line-height: 20px;text-align: left;border-right:none;" nowrap>
                        &nbsp;<%=TrainRoomBo.getHtml(j+"_"+r1001.toString()) %>
                  		  </td>
                		<%
                	}else{
                %>
                    <td class="t_cell_locked_b common_border_color" onclick="tr_onclick(this,'');" ondblclick="trainroomlist('<%=r1001s %>','<bean:write name="element" property="r1011"/>','<%=j %>');" style="line-height: 20px;text-align: left;" nowrap>
                        &nbsp;<%=TrainRoomBo.getHtml(j+"_"+r1001.toString()) %>
                    </td>
                <%} 
                }
                %>
                </logic:notEqual>
            </tr>
        </hrms:paginationdb>
    </table>
    </div>
    </td>
    </tr>
    <tr>
        <td>
            <table width="100%" class="RecordRowP" align="center">
                <tr>
                    <td valign="bottom" class="tdFontcolor">
                        <hrms:paginationtag name="facilityInfoForm"
                            pagerows="${facilityInfoForm.pagerows}" property="pagination"
                            scope="page" refresh="true"></hrms:paginationtag>
                    </td>
                    <td align="right" class="tdFontcolor" nowrap>
                        <hrms:paginationdblink name="facilityInfoForm"
                            property="pagination" nameId="facilityInfoForm" scope="page">
                        </hrms:paginationdblink>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
    <tr>
        <td align="left" style="padding-top:5px;font-size: 12px;">
          <logic:notEqual value="self" name="facilityInfoForm" property="type">
          <input type="button" class="mybutton" value="<bean:message key='button.return'/>" onclick="returnstr();">&nbsp;
          </logic:notEqual>
        </td>
    </tr>
</table>
</html:form>
<script>
    function search(){
        facilityInfoForm.action = "/train/resource/trainroom/selftrainroom.do?b_query=link";
        facilityInfoForm.submit();
    }
    function trainroomlist(r1001,r1011,day){
        facilityInfoForm.action = "/train/resource/trainroom/trainroom.do?b_query=link&day="+day+"&fieldId="+r1001+"&fieldName="+$URL.encode(getEncodeStr(r1011));
        facilityInfoForm.submit();
    }
    
    function checkDate(day)
    {
        valid = true;
        var year = document.getElementById("year").value;
        var month = document.getElementById("month").value;
        var appdate = new Date(year,month-1,day);
        var curdate = new Date();
        curdate = new Date(curdate.getFullYear(),curdate.getMonth(),curdate.getDate());
        if(appdate<curdate)
        {
            alert("请申请当天或之后的日期！");
            valid = false;
        }
        return valid;
    }
    
    function add(day,r1001,r1011){
        if (!checkDate(day)) 
            return;
        var thecodeurl = "/train/resource/trainroom/selftrainroom.do?b_add=link&day="+day+"&fieldId="+r1001+"&fieldName="+ $URL.encode(getEncodeStr(r1011));
        var return_vo= window.showModalDialog(thecodeurl, "", 
                "dialogWidth:400px; dialogHeight:310px;resizable:no;center:yes;scroll:no;status:no");
        if(return_vo){
            document.getElementById(day+"_"+r1001).innerHTML += "&nbsp;"+getDecodeStr(return_vo);
        }
    }
    function del(r1001,_t1,_t2){
        if(!window.confirm("确认要删除吗？"))
            return;
        var hashvo=new ParameterSet();
        hashvo.setValue("state","del");
        hashvo.setValue("r1001",r1001);
        hashvo.setValue("r6101",_t1);
        hashvo.setValue("r6103",_t2);
        var request=new Request({method:'post',asynchronous:false,onSuccess:delinfo,functionId:'2020030113'},hashvo);
    }
    function delinfo(outparamters)
    {
        var flag=outparamters.getValue("flag");     
        if("ok"==flag){
            search();
        }else{
            alert("操作失败！");
        }
    }
    function returnstr(){
        facilityInfoForm.action="/train/resource/trainRescList.do?b_query=return&type=${facilityInfoForm.type}";
        facilityInfoForm.submit();
    }
    function trainroominfo(r1001){
        //facilityInfoForm.action="/train/job/browseTrainClassList.do?b_desc=query&operator=r10&id="+r1001;
        //facilityInfoForm.submit();
        var type = 3;
        facilityInfoForm.action="/train/resource/trainRescAdd.do?b_query=link&type="+type+"&priFldValue="+r1001+"&aa="+3;
        facilityInfoForm.submit();
    }
</script>