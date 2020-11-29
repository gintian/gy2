<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/meizzDate.js"></script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>

<%
int i=0;
%>
<script language="JavaScript" >

	function selAll() {
		var i = 0;
		var struts = document.getElementById("struts");
		var ress = document.getElementById("ress");
		var fest = document.getElementById("fest");
		var daoxiu = document.getElementById("daoxiu");
		if (struts.checked == true) {
			i ++;
		}
		if (ress.checked == true) {
			i ++;
		}
		
		if (fest.checked == true) {
			i ++;
		}
		
		if (daoxiu.checked == true) {
			i ++;
		}
		
		if (i > 0) {
			return true;
		} else {
			return false;
		}
     
        
  	} 
  	
  	/***
  	* 保存
  	*/
	function saveRe() {
		if (selAll()) {
			 var isSu = document.getElementById("isSu");
			 var mess = "（仅保留超级用户设置）";
			 if(isSu.checked == false)
			  {
				 mess="";
			  }
			if(confirm(KQ_PARAMETER_INIT_PARAMSET+"\n"+mess)){
				var waitInfo=eval("wait");	   
	        	waitInfo.style.display="block";
	           	kqInitCodeForm.action="/kq/options/initcode/kq_clearset.do?b_ok=link";
	           	kqInitCodeForm.submit();
			}
        } else {
	         alert(KQ_PARAMETER_INIT_PARAMSET_SELECT);
	         return false;
      	}
  }
	  function selectAll(){
		  var obj = document.getElementById("selAll");
		  var len=document.kqInitCodeForm.elements.length;
	      var i;
		  if(obj.checked == true)
		  {
		      for (i=0;i<len;i++)
		      {
		         if (document.kqInitCodeForm.elements[i].type=="checkbox" && document.kqInitCodeForm.elements[i].name!="isSu")
		          {
		            document.kqInitCodeForm.elements[i].checked=true;
		          }
		      }
		  }else
		  {
			  for (i=0;i<len;i++)
		      {
		         if (document.kqInitCodeForm.elements[i].type=="checkbox")
		          {
		            document.kqInitCodeForm.elements[i].checked=false;
		          }
		      }
	      }
	  }
</script>
<html:form action="/kq/options/initcode/kq_clearset">
	<br>
<table width="480" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap>
		&nbsp;<bean:message key="kq.init.clearset"/>&nbsp;	
            </td>            	        	        	        
           </tr>
   	  </thead>
   	   <tr>
            <td width="100%" align="center" class="RecordRow" nowrap>
            <br>
            <table align="center" width="100%">
             <tr>
              <td>
               <fieldset align="center" style="width:95%;">
                  <legend ><bean:message key="kq.init.selectparam"/> </legend>
                 <table border="0" cellspacing="0"  align="center" cellpadding="2" width="95%">
                  <tr >
                   <td align="left" nowrap align="left">        
                    <html:checkbox name="kqInitCodeForm" property="struts" value="1" styleId="struts"/> <bean:message key="kq.init.strutsparam"/>        
                   </td>
                  </tr>
                  <tr>
                   <td align="left" nowrap align="left">        
                    <html:checkbox name="kqInitCodeForm" property="ress" value="1" styleId="ress"/> <bean:message key="kq.init.ress"/>        
                   </td>
                   </tr>
                   <tr>
                   <td align="left" nowrap align="left">        
                   <html:checkbox name="kqInitCodeForm" property="fest" value="1" styleId="fest"/> <bean:message key="kq.init.fest"/>   
                   </td>
                   </tr>
                   <tr>
                  <td align="left" nowrap align="left">        
                   <html:checkbox name="kqInitCodeForm" property="daoxiu" value="1" styleId="daoxiu"/> <bean:message key="kq.init.daoxiu"/>   
                   </td>
                  </tr>
                  
                  <tr>
        	    <td align="left" nowrap align="left">          
                       <!--<html:checkbox name="kqInitCodeForm" property="kqorg" value="1" />单位班组排班表   --> 
                   </td>
                   <td align="left" nowrap align="left">        
                       
                   </td>
                  </tr> 
                  <tr>
        	    <td align="left" nowrap align="left" >
                    </td>                    
                  </tr>  
                </table>       
	       </fieldset>
              </td>
             </tr>
             <tr>
        	    <td align="left" nowrap valign="left" style="padding-left:27px">
        	    	<input type="checkbox" name="selAll" id="selAll" value="1" onclick="selectAll();"/><bean:message key="label.query.selectall"/>
                </td>                    
             </tr>
             <tr>
             <td>
             <br>
                <fieldset align="center" style="width:95%;">
                <legend ><bean:message key="kq.init.scope"/> </legend>
                  <table border="0" cellspacing="0"  align="center" cellpadding="2" width="95%">
                   <tr >
                    <td align="left" nowrap>        
                       <html:checkbox name="kqInitCodeForm" property="isSu" value="1" /> <bean:message key="kq.init.onlyparam"/>      
                    </td>
                    <td align="left" nowrap align="right">        
          
                    </td>
                    </tr>  
                    
                 </table>
	     </fieldset>
             </td>
             </tr>
             <tr>
              <td align="center" width="100%" height="30" valign="middle">
                 <table border="0" cellspacing="0" cellpadding="2" width="95%">
                   <tr>
                    <td width="80%">
                      
                    </td>
                    <td align="left">                    
	           
                    </td>
                   </tr>
	         </table>
              </td>
              </tr>
            </table> 
          </td>
        </tr> 
 </table>	
  <table align="center">
  	<tr>
  		<td style="height:35px;">
  		 <input type="button" name="b_next" value="<bean:message key="button.ok"/>"  onclick="saveRe();" class="mybutton">
	
  		</td>
  	</tr>
  </table>
	
		<div id='wait' style='position:absolute;top:200;left:250;display:none;'>
  <table border="1" width="300" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
           <tr>

             <td class="td_style common_background_color" height=24><bean:message key="classdata.isnow.wiat"/></td>

           </tr>
           <tr>
             <td style="font-size:12px;line-height:200%" align=center>
               <marquee class="marquee_style" direction="right" width="300" scrollamount="5" scrolldelay="10">
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
</html:form>

