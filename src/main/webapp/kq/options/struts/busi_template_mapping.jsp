<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script>

function Save(){
	  var mappingtab = document.getElementById("mappings");
    var rowCnt = mappingtab.rows.length;
    var colCnt = mappingtab.rows[0].cells.length;
    var mappings = "";

    for (var i=1; i<rowCnt; i++)
    {   
       var kqItemId;
       var ydItemId;
       
       if(getIEVersion()!="")
       {
         kqItemId=trimStr(mappingtab.rows[i].cells[0].innerText);
         ydItemId=document.getElementById("ydItemId" + i).value;
       }
       else
       {
         state=trimStr(mappingtab.rows[i].cells[0].textContent);
         ydItemId=document.getElementById("ydItemId" + i).value;
       }              
      
       if(ydItemId!=null && ydItemId!="" && ydItemId!="#")
       {
         if(mappings!="")
            mappings += ',';

         mappings +=  kqItemId + ':' + ydItemId;
       }
    }

    if(rowCnt>1 && mappings=="")
    {
      alert("请设置指标的对应关系！");
      return;
    }
    
    window.returnValue=mappings;
    window.close();
}

</script>

<html:form action="/kq/options/struts/busi_template_mapping">
<center>
     <br/>
     <div class="fixedDiv" style="border: #C4D8EE 1pt solid;margin-left: 8px; border-left:0px;">
        <table id="mappings" border="0" cellspacing="0" cellpadding="0" class="ListTableF" width="100%" style="padding:2px;">
            <thead>
              <tr class="fixedHeaderTr">
	              <td class="TableRow"  align="center"  nowrap="nowrap">
	                <bean:message key="kq.pigeonhole.srcfldname"/>
	              </td>
	              <td class="TableRow"  align="center"  nowrap="nowrap">
	                <bean:message key="kq.pigeonhole.destfldname"/>
	              </td>
              </tr>
            </thead>
            <% int i = 0; %>
            <logic:iterate id="element" name="kqBusiTemplateMappingForm" property="mappings" >
            <%
               if (i % 2 == 0)
               {
            %>
            <tr class="trShallow">
            <%
               } else
               {
            %>
            
            <tr class="trDeep">
            <%
               }
               i++;
            %>
                <td align="left" class="RecordRow" style="display:none;" nowrap>
                  <bean:write name="element" property="kqItemId" filter="true"/>
                </td>
                <td align="left" class="RecordRow" style="width:50%;" nowrap>
                    &nbsp;<bean:write name="element" property="kqItemDesc" filter="true"/>
                </td>
                <td align="left" class="RecordRow" style="width:50%;" nowrap>
                     <% pageContext.setAttribute("index",Integer.valueOf(i)); %>
                     <html:select styleId="ydItemId${index}" name="element" property="ydItemId" size="1"  style="width:100%" onchange="">
                        <html:optionsCollection  name="element" property="ydItems" value="dataValue" label="dataName"/>
                     </html:select>
                </td>
            </tr>
          </logic:iterate>
        </table>
    </div>
    <br>
    <table width='100%' align='center'>
        <tr>
            <td align='center'>
               <input type='button' value='&nbsp;<bean:message key="button.save"/>&nbsp;' class='mybutton' onclick='Save();'>
                      &nbsp;
               <input type='button' id="button_goback" value='&nbsp;<bean:message key='button.cancel' />&nbsp;' class="mybutton" onclick='window.close();'>
            </td>
        </tr>
    </table>
</center>
</html:form>