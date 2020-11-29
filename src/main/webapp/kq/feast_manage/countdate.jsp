<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/kq/kq.js"></script>
<script language="javascript">
function countdata()
{
	var count_type;
	
	      var count_start=document.feastForm.feast_start.value;
	      var count_end=document.feastForm.feast_end.value;
	      var dbpre=document.feastForm.dbpre.value;
	      if(count_start=="")
	      {
	         alert("请选择计算开始时间！");
                 return;
	      }else if(count_end=="")
	      {
	          alert("请选择计算结束时间！");
                  return;
	      }else if(count_start>count_end)
	      {
	    	  alert("起始时间不能大于终止时间!");
	    	  return;
	      }else
	      {
	          var exp_fields="";
		      var vos= document.getElementById("exp_fields");       
             if(vos==null)
             {
              exp_fields="q1703";
             }else
             {
               for(var i=0;i<vos.length;i++)
               {
                 var valueS=vos.options[i].value;          
                 exp_fields=exp_fields+valueS+"`";
               }       
             }    
             var checkboxVo=document.getElementsByName("clear_zone");
             var clear_zone="";
             if(checkboxVo.length >= 1){
	    	     if(checkboxVo[0].checked)
	    	     {
	    	        clear_zone="1";
	    	     }else
	    	     {
	    	       clear_zone="0";
	    	     }
    	     }
    	      
    	     // 上年结余
    	     var balanceObj = document.getElementById("balance");
    	     var balanceValue = "0";
    	     if (balanceObj) {	    	     
	    	     if (balanceObj.checked) {
	    	     	balanceValue = "1";
	    	     }   
    	     } 
    	     
    	     // 结余截止日期 
    	     var balanceEndObj = document.getElementById("balanceEnd");
    	       	       
		     var thevo=new Object();
	         thevo.start=count_start;
             thevo.end=count_end;
             thevo.dbpre=dbpre;
             thevo.clear_zone=clear_zone;
             thevo.exp_fields=exp_fields;
             if (balanceObj) {
             	thevo.balance = balanceValue;
             }
             
             if (balanceEndObj) {
             	thevo.balanceEnd = balanceEndObj.value;
             } else {
             	thevo.balanceEnd = "";
             }
		     window.returnValue=thevo;		       
		       window.close(); 
	    }	 
}

</script>
<html:form action="/kq/feast_manage/managerdata">
	<div class="fixedDiv2" style="height: 100%;border: none">
	<table width="100%" border="0" cellpmoding="0" cellspacing="0"
		class="DetailTable" cellpadding="0" align="center" valign="middle">
		<tr height="20">
			<td colspan="4" align=center class="TableRow">
				<bean:message key="kq.countdate.width" />
			</td>
		</tr>
		<tr>
			<td width="100%" height="50" colspan="4"
				style="BORDER-LEFT: #94B6E6 1pt solid; BORDER-RIGHT: #94B6E6 1pt solid;" class="common_border_color" >
				<table>
					<tr>
						<td align="right">
							
						</td>
						<td height="35">

							<hrms:optioncollection name="feastForm" property="dblist"
								collection="list" />
							<html:select name="feastForm" property="dbpre" size="1">
								<html:options collection="list" property="dataValue"
									labelProperty="dataName" />
							</html:select>
						</td>
					</tr>
					<tr>
						<td height="35" align="right">
							<bean:message key="label.query.from" />
							&nbsp;
						</td>
						<td>
							<input type="text" name="feastForm"
								value="${feastForm.feast_start}" 
								 class="inputtext" style="width: 100px; font-size: 10pt; text-align: left" readonly="readonly"
								id="feast_start" onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);'>
							&nbsp;
							<bean:message key="label.query.to" />
							&nbsp;
							<input type="text" name="feastForm"
								value="${feastForm.feast_end}" 
								 class="inputtext" style="width: 100px; font-size: 10pt; text-align: left" readonly="readonly"
								id="feast_end" onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);'>
							<html:hidden name="feastForm" property="hols_status"
								styleClass="text" />
						</td>
					<tr>
				</table>

			</td>
		</tr>
		<tr>
			<td width="100%" height="50" colspan="4"
				style='BORDER-BOTTOM: #94B6E6 1pt solid; BORDER-LEFT: #94B6E6 1pt solid; BORDER-RIGHT: #94B6E6 1pt solid; ' class="common_border_color" >
				<table>
					<tr>
						<td width="20%">
							<bean:message key="kq.item.order"/>:
						</td>
						<td width="60%">
							<html:select name="feastForm" property="exp_fields"
								styleId="exp_fields" multiple="multiple" size="10"
								style="height:150px;width:100%;font-size:9pt">
								<html:optionsCollection property="exp_fieldlist"
									value="dataValue" label="dataName" />
							</html:select>
						</td>
						<td width="20%">
							<html:button styleClass="mybutton" property="b_up"
								onclick="upItem($('exp_fields'));">
								<bean:message key="button.previous" />
							</html:button>
							<br>
							<br>
							<html:button styleClass="mybutton" property="b_down"
								onclick="downItem($('exp_fields'));">
								<bean:message key="button.next" />
							</html:button>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td>
				<br/>
			</td>
		</tr>
			<tr>
				<td colspan="4">
					<table border="0" style="border: 1px solid #94B6E6; " class="common_border_color" width="100%" height="100%">
						<tr>
							<td align="left" colspan="2">
								<html:multibox name="feastForm" property="clear_zone" value="1" />
								<bean:message key="kq.feast.no.create.zero.record"/>
							</td>
						</tr>
						<logic:equal value="1" name="feastForm" property="existBalance">
						<tr>
							<td align="left">
								<input id="balance" type="checkbox" name="balance" value="1">
								<label for="balance">
									<bean:message key="kq.feast.sum.preyear"/>
								</label>
							</td>
							<logic:equal value="1" name="feastForm"
								property="existBalanceEnd">
								<td align="left">
									<bean:message key="kq.feast.sum.preyear.enddate"/>&nbsp;
									<input type="text" name="feastForm"
										value="${feastForm.balanceEnd}" 
										  class="inputtext" style="width: 100px; font-size: 10pt; text-align: left" readonly="readonly"
										id="balanceEnd" onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);'/>
								</td>
							</logic:equal>
						</tr>
						</logic:equal>
					</table>
				</td>
			</tr>
		<tr>
			<td height="40" align="center" colspan="4">
				<input type="button" name="btnreturn"
					value='<bean:message key="lable.tz_template.enter"/>'
					onclick="countdata();" class="mybutton">
				<input type="button" name="btnreturn"
					value='<bean:message key="kq.register.kqduration.cancel"/>'
					onclick="window.close();" class="mybutton">
			</td>
		</tr>
	</table>
	</div>
</html:form>
<script language="javascript">
hide_nbase_select('dbpre');
</script>