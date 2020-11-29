<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<hrms:themes></hrms:themes>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript">
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
function save(){
	var return_item="";
	var return_value="";
	var rights=document.getElementsByName("photo_h")[0];
	for(var i=0,n=0;i<rights.length;i++)
    {
    	return_item+=rights[i].value+",";
    	return_value+=(i+1)+"."+rights[i].text+"、";
    }
    if(parent.Ext)
    	parent.Ext.getCmp('select_cond').return_vo =return_item+"#"+return_value; 
    else
    	returnValue=return_item+"#"+return_value;
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
		AjaxBind.bind(otherParamForm.left_fields,resultlist);		
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
function getSelectedIndex(selectbox1)
{
   var selectbox=document.getElementById(selectbox1);
   var idx=-1;
   for(i=0;i<selectbox.options.length;i++)
   {  
    if(selectbox.options[i].selected)
    {
       idx=i;
       break;
    }   	
   }
   return idx;
}
function upItem(selectbox1)
{
  var selectbox=document.getElementById(selectbox1);
  if(selectbox==null)
     return;
   var idx=getSelectedIndex(selectbox1);
   if(idx==-1)
     return;
   if(idx==0)
     return;
   var currvalue=selectbox.options[idx].value;
   var currtext=selectbox.options[idx].text;
   selectbox.options[idx].value=selectbox.options[idx-1].value;
   selectbox.options[idx].text=selectbox.options[idx-1].text;
   selectbox.options[idx-1].value=currvalue;
   selectbox.options[idx-1].text=currtext;
   selectbox.options[idx].selected=false;
   selectbox.options[idx-1].selected=true;	
}
function downItem(selectbox1)
{
 var selectbox=document.getElementById(selectbox1);
   if(selectbox==null)
     return;
   var idx=getSelectedIndex(selectbox1);
   if(idx==-1)
     return;
   if(idx==selectbox.options.length-1)
     return;
   var currvalue=selectbox.options[idx].value;
   var currtext=selectbox.options[idx].text;
   selectbox.options[idx].value=selectbox.options[idx+1].value;
   selectbox.options[idx].text=selectbox.options[idx+1].text;
   selectbox.options[idx+1].value=currvalue;
   selectbox.options[idx+1].text=currtext;
   selectbox.options[idx].selected=false;
   selectbox.options[idx+1].selected=true;
}
//关闭弹窗  wangb 20190319
function winclose(){
	if(parent.Ext)
		parent.Ext.getCmp('select_cond').close();
	else
		window.close();
}
</script>
<html:form action="/selfservice/param/otherparam">
	<table width="100%" align="center" border="0" cellpadding="0"
		cellspacing="0" >
		<tr>
			<td valign="top">
				<table width="490" border="0" cellspacing="0" align="center"
					cellpadding="0" >

					<tr>
						<td width="100%" align="center"  nowrap style="border-right:none;">
							<table>
								<tr>
									<td align="center" width="46%">
										<table align="center" width="100%">
											<tr>
												<td align="left">
													备选条件&nbsp;
												</td>
											</tr>
											<tr>
												<td align="center">
													<hrms:optioncollection name="otherParamForm"
														property="condlist" collection="llist" />
													<html:select name="otherParamForm" property="photo_w"
														multiple="multiple" size="10"
														ondblclick="additems('photo_w','photo_h');"
														style="height:230px;width:100%;font-size:9pt">
														<html:options collection="llist" property="dataValue"
															labelProperty="dataName" />
													</html:select>
												</td>
											</tr>
										</table>
									</td>
									<td width="4%" align="center">
										<html:button styleClass="smallbutton" property="b_addfield"
											onclick="additems('photo_w','photo_h');">
											<bean:message key="button.setfield.addfield" />
										</html:button>
										<html:button styleClass="smallbutton" property="b_delfield"
											onclick="removeitem('photo_h');" style="margin-top:30px;">
											<bean:message key="button.setfield.delfield" />
										</html:button>
									</td>
									<td width="46%" align="center">
										<table width="100%">
											<tr>
												<td width="100%" align="left">
													已选条件&nbsp;
												</td>
											</tr>
											<tr>
												<td width="100%" align="left">
													<hrms:optioncollection name="otherParamForm"
														property="g_conds" collection="rlist" />
													<html:select name="otherParamForm" property="photo_h" styleId="photo_h"
														multiple="multiple" size="10"
														ondblclick="removeitem('photo_h');"
														style="height:230px;width:100%;font-size:9pt">
														<html:options collection="rlist" property="dataValue"
															labelProperty="dataName" />
													</html:select>

												</td>
											</tr>
										</table>
									</td>
									<td width="4%" align="center" style="">
										<html:button styleClass="smallbutton" property="b_up"
											onclick="upItem('photo_h');">
											<bean:message key="button.previous" />
										</html:button>
										<html:button styleClass="smallbutton" property="b_down"
											onclick="downItem('photo_h');" style="margin-top:30px;">
											<bean:message key="button.next" />
										</html:button>
									</td>
								</tr>
							</table>
						</td>
					</tr>
					<tr height="35">
						<td align="center" nowrap colspan="3" style="border-top:#E5E5E5 1pt solid">
							<input type="button" name="btnsave" value='<bean:message key="button.ok" />'
									onclick="setselectitem('photo_h');save();winclose();" class="mybutton">
								<input type="button" name="btnreturn" value='<bean:message key="button.cancel"/>'
									onclick="winclose();" class="mybutton">
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
	</html:form>
	
	<script>
		if(!getBrowseVersion()){
			var form = document.getElementsByName('otherParamForm')[0];
			var td = form.getElementsByTagName('table')[0].getElementsByTagName('table')[0].getElementsByTagName('tr')[1].getElementsByTagName('td')[0];
			td.style.borderRight = '';
		}
	</script>