<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<script language=JavaScript>   
function saveCode() {
    var hashvo = new ParameterSet();
    var vos = document.getElementById("right");
    if (vos == null) {
        alert("已选指标项不能为空！");
        return false;
    } else {
        var code_fields = new Array();
        for (var i = 0; i < vos.length; i++) {
            var valueS = vos.options[i].value;
            code_fields[i] = valueS;
        }
    }
    hashvo.setValue("code_fields", code_fields);
    hashvo.setValue("codeitemid", "${cardConstantForm.codeitemid}");
    hashvo.setValue("codesetname", "${cardConstantForm.codesetname}");
    hashvo.setValue("fashion_flag", "${cardConstantForm.fashion_flag}");
    hashvo.setValue("codename", "${cardConstantForm.codename}");
    hashvo.setValue("mobapp", "${cardConstantForm.mobapp}");
    var request = new Request({method: 'post', onSuccess: showSelect, functionId: '1010030013'}, hashvo);
}

function showSelect(outparamters) {
    var types = outparamters.getValue("types");
    if (types == "ok") {
        alert("编辑成功");
        var mess = outparamters.getValue("mess");
        var thevo = new Object();
        thevo.mess = mess;
        //window.returnValue = thevo;
        //window.close();
        winclose(thevo);
    } else {
        alert("编辑失败");
    }
}
//【7250】系统管理，应用设置，薪酬表设置中可以增加重复项。 jingq add 2015.01.30
function additem(sourcebox_id,targetbox_id){
	var left_vo,right_vo,vos,voss,i;
	vos= document.getElementsByName(sourcebox_id);
	if(vos==null){
		return false;
	}
	left_vo=vos[0];
	var nofield=true;
	voss= document.getElementsByName(targetbox_id);  
	right_vo=voss[0];
	for(i=0;i<left_vo.options.length;i++){
		if(left_vo.options[i].selected){
			if(right_vo.options.length==0){
				var no = new Option();
				no.value=left_vo.options[i].value;
				no.text=left_vo.options[i].text;
				right_vo.options[right_vo.options.length]=no;
			}else{
				for(var j=0;j<right_vo.options.length;j++){
					if(right_vo.options[j].text==left_vo.options[i].text){
						nofield=false; 
					}         
				}
				if(nofield){
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
//关闭弹窗回方法  wangb 20190318
function winclose(return_vo){
	if(parent.Ext){
		var win = parent.Ext.getCmp('selectSalaryTable');
		win.return_vo = return_vo;
		win.close();
	}else{
		window.returnValue = return_vo;
        window.close();
	}
}
</script> 
<html:form action="/ykcard/cardconstantset">
<table width="490" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <%--<thead>--%>
           <%--<tr>--%>
            <%--<td align="left" class="TableRow" nowrap>--%>
		<%--选择薪酬表&nbsp;&nbsp;--%>
            <%--</td>            	        	        	        --%>
           <%--</tr>--%>
   	  <%--</thead>--%>
   	   <tr>
            <td width="100%" align="center" class="RecordRow" nowrap>
              <table>
                <tr>
                 <td align="center"  width="46%">
                   <table align="center" width="100%">
                    <tr>
                    <td align="left">
                        备选登记表&nbsp;&nbsp;
                    </td>
                    </tr>                    
                   <tr>
                       <td align="center">
                       <html:select name="cardConstantForm" property="left_fields" multiple="multiple" size="10" ondblclick="additem('left_fields','code_fields');" style="height:230px;width:100%;font-size:9pt">
                           <html:optionsCollection property="yklist" value="dataValue" label="dataName"/>	        
                        </html:select>                         
                       </td>
                    </tr>
                   </table>
                </td>
               
                <td width="4%" align="center" valign="middle">
                   <html:button  styleClass="smallbutton" property="b_addfield" onclick="additem('left_fields','code_fields');">
            		 <bean:message key="button.setfield.addfield"/> 
	           </html:button>
	           <html:button  styleClass="smallbutton" property="b_delfield" onclick="removeitem('code_fields');" style="margin-top:30px;">
            		     <bean:message key="button.setfield.delfield"/>    
	           </html:button>	     
                </td>         
                
                <td width="46%" align="center">
                 <table width="100%">
                  <tr>
                  <td width="100%" align="left">
                     已选登记表&nbsp;&nbsp;
                  </td>
                  </tr>
                  <tr>
                  <td width="100%" align="left">
 		     <html:select name="cardConstantForm" property="code_fields" styleId="right"  multiple="multiple" size="10" ondblclick="removeitem('code_fields');" style="height:230px;width:100%;font-size:9pt">
                           <html:optionsCollection property="cardfieldlist" value="dataValue" label="dataName"/>   		      
 		        </html:select>
                  </td>
                  </tr>
                  </table>             
                </td>
                
                <td width="4%" align="center" valign="middle" style="margin-right:5px;">
                   <html:button  styleClass="smallbutton" property="b_up" onclick="upItem($('code_fields'));">
            		     <bean:message key="button.previous"/> 
	           </html:button>
	           <html:button  styleClass="smallbutton" property="b_down" onclick="downItem($('code_fields'));" style="margin-top:30px;">
            		     <bean:message key="button.next"/>    
	           </html:button>	     
                </td>                                
                </tr>
              </table>             
            </td>
          </tr>
            
          <tr>
          <td align="center"  style="height: 35px;" class="RecordRow" nowrap>
          
             <input type="button" name="btnreturn" value='确定' class="mybutton" onclick=" saveCode();">
	     <input type="button" name="btnreturn" value='关闭' class="mybutton" onclick="winclose();">
          </td>
          </tr>   
</table>
</html:form>
