<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<hrms:themes></hrms:themes>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript">
function goback()
{
	  document.info_paramForm.action="/system/sys_param_panel.do";
	  document.info_paramForm.submit();  
} 
Array.prototype.remove=function(dx)
{
  if(isNaN(dx) || dx>this.length)
     return false;
  for(var i=0,n=0;i<this.length;i++)
  {
     if(this[i]!=this[dx])
     {
        this[n++]=this[i]
     }
  }
  this.length-=1
}

	function showFieldList(outparamters)
	{
		var fieldlist=outparamters.getValue("fieldlist");
                var resultlist=new Array();
                for(var i=0,n=0;i<fieldlist.length;i++)
                {
                   var obj=fieldlist[i];
                  
                      resultlist[n]=fieldlist[i];
                      n++;
                   
                }
		AjaxBind.bind(info_paramForm.left_fields,resultlist);
		var rightlist=outparamters.getValue("rightlist");
		AjaxBind.bind(info_paramForm.right_fields,rightlist);
	}
				
	function searchFieldList()
	{
	   var In_paramters="tablename=V01";
   	   var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showFieldList,functionId:'1010020809'});
	}	

function additems(sourcebox_id,targetbox_id)
{
  var left_vo,right_vo,vos,voss,i;
  vos= document.getElementsByName(sourcebox_id);
  if(vos==null)
  	return false;
  left_vo=vos[0];
  var nofield=true;

  voss= document.getElementsByName(targetbox_id);  
  right_vo=voss[0];
  for(i=0;i<left_vo.options.length;i++)
  {
   if(left_vo.options[i].selected)
   {
    if(right_vo.options.length==0)
     {
        var no = new Option();
        no.value=left_vo.options[i].value;
        no.text=left_vo.options[i].text;
        right_vo.options[right_vo.options.length]=no;
     }else{
        for(var j=0;j<right_vo.options.length;j++)
        {
           if(right_vo.options[j].text==left_vo.options[i].text)
           {
               nofield=false; 
           }         
        }
          if(nofield)
           {
              var no = new Option();
              no.value=left_vo.options[i].value;
              no.text=left_vo.options[i].text;
              right_vo.options[right_vo.options.length]=no;
           }
           nofield=true;
    }
   }
 }
 return true;	  	
}
</script>
<html:form action="/system/options/corps_param">
	<table width="100%" align="center" border="0" cellpadding="0"
		cellspacing="0">
		<tr>
			<td valign="top">
				<table width="700" border="0" cellspacing="0" align="center"
					cellpadding="0" class="RecordRow">
					<thead>
						<tr>
							<td align="left" class="TableRow" nowrap colspan="3">
								<bean:message key="label.query.selectfield" />
								&nbsp;&nbsp;
							</td>
						</tr>
					</thead>
					<tr>
						<td width="100%" align="center" class="RecordRow" nowrap style="border-right:none;">
							<table align="left" border="0" cellspacing="0" style="margin-left:-2px;">
								<tr>
									<td align="center" width="46%">
										<table align="center" width="100%">
											<tr>
												<td align="left">
													<bean:message key="static.target" />
													&nbsp;&nbsp;
												</td>
											</tr>
											<tr>
												<td align="center">
													<select name="left_fields" multiple="multiple"
														ondblclick="additems('left_fields','right_fields');"
														style="height: 250px; width: 100%; font-size: 9pt">
													</select>
												</td>
											</tr>
										</table>
									</td>
									<td width="4%" align="right">
										<html:button styleClass="smallbutton" property="b_addfield"
											onclick="additems('left_fields','right_fields');">
											<bean:message key="button.setfield.addfield" />
										</html:button>
										<html:button styleClass="smallbutton" property="b_delfield"
											onclick="removeitem('right_fields');" style="margin-top:30px;">
											<bean:message key="button.setfield.delfield" />
										</html:button>
									</td>
									<td width="46%" align="center">
										<table width="100%">
											<tr>
												<td width="100%" align="left">
													<bean:message key="static.ytarget" />
													&nbsp;&nbsp;
												</td>
											</tr>
											<tr>
												<td width="100%" align="left">
													<hrms:optioncollection name="info_paramForm"
														property="browsefields" collection="list" />
													<html:select name="info_paramForm" property="right_fields"
														multiple="multiple" size="10"
														ondblclick="removeitem('right_fields');"
														style="height:250px;width:100%;font-size:9pt">
														<html:options collection="list" property="dataValue"
															labelProperty="dataName" />
													</html:select>

												</td>
											</tr>
										</table>
									</td>
									<td width="4%" align="right">
										<html:button styleClass="smallbutton" property="b_up"
											onclick="upItem($('right_fields'));">
											<bean:message key="button.previous" />
										</html:button>
										<html:button styleClass="smallbutton" property="b_down"
											onclick="downItem($('right_fields'));" style="margin-top:30px;">
											<bean:message key="button.next" />
										</html:button>
									</td>
								</tr>
							</table>
						</td>
					</tr>
					<tr height="35">
						<td align="center" nowrap colspan="3">
							<hrms:submit styleClass="mybutton" property="b_save"
								onclick="setselectitem('right_fields');">
								<bean:message key="button.save" />
							</hrms:submit>
							<logic:equal name="info_paramForm" property="edition" value="4">
								<input type="button" name="btnreturn" value='返回'
									onclick="goback();" class="mybutton">
							</logic:equal>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</html:form>

<script language="javascript">
   searchFieldList();
</script>