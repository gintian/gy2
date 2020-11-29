<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>

<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="javascript">	
function back()
{
	orgInformationForm.action = "/org/orginfo/searchorglist.do?b_return=link";
	orgInformationForm.submit();
}

function wsubmit(){
	orgInformationForm.action = "/org/orginfo/searchorglist.do?b_savemove=link";
	orgInformationForm.submit();
	document.getElementById("butonid").disabled=true;
	reloadTree();
}

function reloadTree(){
	//var nodeList = new Array();
	    var node=null;
	    var uid;
		var codesetid;
		var action;
		var text;
		var title;
		var Xml;
		var imgurl;
	var opts =  document.getElementById("right_fields").options;
	var currnode=parent.frames['mil_menu'].Global.selectedItem;
	if(currnode==null)
			return;
	if(currnode.load)
		for(var i=0;i<opts.length;i++){
			uid = opts[i].value;
			var flag = false;
			for(var k=0;k<currnode.childNodes.length;k++){
				var olduid = currnode.childNodes[k].uid;
				codesetid = olduid.substring(0,2);
					if(olduid.substr(2)==uid){
						uid = currnode.childNodes[k].uid;
						action = currnode.childNodes[k].action;
						title = currnode.childNodes[k].title;
						text = currnode.childNodes[k].text;
						Xml = currnode.childNodes[k].Xml;
						imgurl = currnode.childNodes[k].icon;
						currnode.childNodes[k].remove();
						flag = true;
						break;
				    }
			}
			if(flag){
		 			parent.frames['mil_menu'].add(uid,text,action,"mil_body",title,imgurl,Xml);
			}
		}
	
}
</script>
<html:form action="/org/orginfo/searchorglist">
<br>
<br>

<!--查询模板指标-->
<div id="first" style="filter:alpha(Opacity=100);display:block;">
<table width="70%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap colspan="2">
		<bean:message key="label.org.orgmove"/>&nbsp;&nbsp;
            </td>            	        	        	        
           </tr>
   	  </thead>
   	  <tr>
   	  <TD align="center" class="RecordRow" nowrap colspan="2" width="95%">
   	  <TABLE width="100%">
   	  <tr>
   	   <td width="90%">
   	            <html:select name="orgInformationForm" property="right_fields" multiple="multiple" size="10" style="height:230px;width:100%;font-size:9pt" styleId="right_fields">
                      <html:optionsCollection property="list" value="dataValue" label="dataName"/>
                    </html:select>   	     
   	   </td>
   	    <td width="10%" align="center">
                   <html:button  styleClass="mybutton" property="b_up" onclick="upItem($('right_fields'));">
            		     <bean:message key="button.previous"/> 
	           </html:button>
	           <html:button styleClass="mybutton" property="b_down" onclick="downItem($('right_fields'));" style="margin-top:20px;" >
            		     <bean:message key="button.next"/>    
	           </html:button>	     
             </td>      
             </tr>
         </TABLE>
       </TD>
   	  </tr>
          <tr style="height: 35">
          <td align="center"  nowrap>
	      <button type="button" onclick="setselectitem('right_fields');wsubmit();" class="mybutton" id="butonid">
	      	<bean:message key="button.save"/>
	      </button>	       
	      <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick='back();'>
          </td>
          </tr>   
</table>
</div>
</html:form>
