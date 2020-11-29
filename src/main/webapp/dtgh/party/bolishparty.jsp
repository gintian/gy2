<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.*"%>
<%
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		            Calendar calendar = Calendar.getInstance();
 %>
<script language="JavaScript" src="/js/function.js"></script>
<script type="text/javascript" src="/js/validateDate.js"></script>
<script language="JavaScript" src="/org/orgdata/orgedit.js"></script>
<script type="text/javascript">
<!--
	function checkDate(checkStartDate) {   
    var arys1= new Array();   
    if(checkStartDate != null) {   
        arys1=checkStartDate.split('-');   
        var sdate=new Date(arys1[0],parseInt(arys1[1]-1),arys1[2]);  
        sdate=sdate.getTime();  
        var edate=new Date(); 
        edate=new Date(edate.getFullYear(),edate.getMonth(),edate.getDate());  
        edate=edate.getTime();
        if(sdate >= edate) {   
            alert("撤销日期必须在当前日期前！");      
            return false;      
        }   
        return true;   
    }   
}  
	function check(){
		var end_date=document.getElementsByName("end_date")[0].value;
		if(checkDate(end_date)){
            winClose(end_date);
		}
	}
	function winClose(end_date) {
        var closeTarget = parent.Ext.getCmp("undoBasePosWin");
        if(closeTarget){
            closeTarget.return_vo = end_date ? end_date : "";
            closeTarget.close();
        }
    }
//-->
</script>

<body style="margin:0px;padding:0px;">
<div class="fixedDiv3">
     <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable"> 
       <tr>  
         <td  class="framestyle1">
            <table  width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
              <tr align="center">
                <td valign="middle" class="TableRow" style="border-left: 0px; border-right: 0px;">
                                                撤销基准岗位
                </td>
              </tr> 
	          <%
					calendar.add(Calendar.DATE, -1);
					String date = sdf.format(calendar.getTime());
              %>
              <tr><td height="50px"></td></tr>
        	  <tr>
                 <td align="center" valign="top" height="140px">
            	    <bean:message key="conlumn.codeitemid.end_date"/>:
            	           	    <input type="text" class="textColorWrite" name="end_date" value="<%=date %>" maxlength="50" 
							   style="BACKGROUND-COLOR:#F8F8F8;width:150px" extra="editor" 
							   dropDown="dropDownDate" 
							   onchange="if(!validate(this,'有效日期止')) {this.focus(); this.value='<%=date %>'; }"/>                                            
                 </td>
              </tr> 

            </table>
          </td>
        </tr>
     
     </table>
     <table  width="100%" align="center">
          <tr>
            <td align="center">
         	  	<input type="button" name="b_return" value="<bean:message key="button.ok"/>" class="mybutton" onclick="check();">
	     		<input type="button" name="b_return" value="<bean:message key="button.cancel"/>" class="mybutton" onclick="winClose();">
            </td>
          </tr>          
    </table>
</div>
</body>
