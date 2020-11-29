<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<script language="javascript">
function closeWindow() {
	if (!window.showModalDialog) {
		parent.Ext.getCmp("feastWin").close();
	}else{
		window.close(); 
	}
  }
  
  function valide()
	{
	
	   var tag=true;   
     var tag;
     var m=0;
     var gg=0;
     var tem,tes,www;
     
     var nam=$F('feast_name');
	  if(nam==null||nam.length<=0)
	  {
	    alert("<bean:message key="kq.search_feast.month"/>");
	    return false;
	 }
	    tag = $F('sdate');
	    tem=tag.split(",");
	    if(tem[(tem.length-1)]==""||tem[(tem.length-1)]==null)
	    {
	      gg=tem.length-1;
	    }else{
	       gg=tem.length;
	    }
	    for(m=0;m<gg;m++)
	    {
	         www=tem[m].replace(".","-");
	    	    tes=www.replace(".","-");
	    	    var ver=Array();
	    	   
	    	  if(tem[m].length==3||tem[m].length==5||tem[m].length==4)
	    	  {
	    	  
	    	      ver=tes.split("-");
	    	    if(ver.length==2)
	    	    {
	    	        if(tem[m].length==3)
	    	       {
	    	           tag= checkDat("1999-0"+ver[0]+"-0"+ver[1]);
	    	           if(tag==false)
	                {
	                   return false;
	                }
	             }
	            if(tem[m].length==4&&ver[0].length==1)
	    	      {
	    	           tag= checkDat("1999-0"+ver[0]+"-"+ver[1]);
	    	          if(tag==false)
	               {
	                   return false;
	               }
	            }
	            if(tem[m].length==4&&ver[1].length==1)
	    	      {
	    	          tag= checkDat("1999-"+ver[0]+"-0"+ver[1]);
	    	          if(tag==false)
	               {
	                    return false;
	               }
	            }
	            if(tem[m].length==5)
	    	      {
	    	          tag= checkDat("1999-"+ver[0]+"-"+ver[1]);
	    	          if(tag==false)
	                {
	                    return false;
	                }
	            }
	          }else{
	              alert("<bean:message key="kq.search_feast.labor"/>");
	    	        return false;
	          }
	    	 }
	    	 if(tem[m].length==8||tem[m].length==10||tem[m].length==9)
	    	 {
	    	      ver=tes.split("-");
	    	       
	    	     if(ver.length==3)
	    	     {
	    	        if(tem[m].length==8)
	    	       {
	    	           tag= checkDat(ver[0]+"-0"+ver[1]+"-0"+ver[2]);
	    	           if(tag==false)
	                {
	                  return false;
	                }
	             }
	             if(tem[m].length==9&&ver[1].length==1)
	    	       {
	    	           tag= checkDat(ver[0]+"-0"+ver[1]+"-"+ver[2]);
	    	           if(tag==false)
	                {
	                   return false;
	                 }
	             }
	             if(tem[m].length==9&&ver[2].length==1)
	    	       {
	    	           tag= checkDat(ver[0]+"-"+ver[1]+"-0"+ver[2]);
	    	           if(tag==false)
	                {
	                   return false;
	                }
	             }
	             if(tem[m].length==10)
	    	       {
	    	           tag= checkDat(ver[0]+"-"+ver[1]+"-"+ver[2]);
	    	           if(tag==false)
	                {
	                    return false;
	                }
	             }
	            }else{
	            
	             alert("<bean:message key="kq.search_feast.labor"/>");
	    	       return false;
	            }
	            
	    	 }
	    	  
	    	  if(tem[m].length!=8&&tem[m].length!=10&&tem[m].length!=9&&tem[m].length!=3&&tem[m].length!=5&&tem[m].length!=4&&tem[m].length!=0)
	    	  {
	    	    alert("<bean:message key="kq.search_feast.labor"/>");
	    	    return false;
	    	  }
         
	   }
    	saveFeast();
	}
  
  function saveFeast() {
         var hashvo=new ParameterSet();			
	     hashvo.setValue("feast_name", $F('feast_name'));
	     hashvo.setValue("sdate",$F('sdate'));	
	     hashvo.setValue("feast_id", "${kqFeastForm.feast_id}");
	     hashvo.setValue("need_search", "no");
	     var request=new Request({method:'post',asynchronous:false,onSuccess:showResut,functionId:'15202110003'},hashvo);
  }
  
  function showResut() {
	  if (!window.showModalDialog) 
     	 parent.refleshs1(2);
     else
     	window.returnValue=2;
	  closeWindow();
  }
	
	  function checkDat(str)
	  {
	    var ret=false;
	      var mm="";
	      var dd="";
	      var tem="";
	      var cc=0;
        var reg = /^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2})$/; 
        var r = str.match(reg); 
          
        if(r==null)
        {
           alert("<bean:message key="kq.search_feast.labor"/>");
           return false; 
        }
         var d=new Date(r[1], r[3]-1,r[4]);
            dd=""+d.getDate();
            cc=d.getMonth()+1;
            mm=""+cc;
        if(mm.length==1&&dd.length==2)
        {
          tem=d.getFullYear()+r[2]+("0"+(d.getMonth()+1))+r[2]+d.getDate();
        }
         if(dd.length==1&&mm.length==2)
        {
          tem=d.getFullYear()+r[2]+(d.getMonth()+1)+r[2]+("0"+d.getDate());
        }
        if(dd.length==1&&mm.length==1)
        {
          tem=d.getFullYear()+r[2]+("0"+(d.getMonth()+1))+r[2]+("0"+d.getDate());
        }
        if(dd.length==2&&mm.length==2)
        {
         tem=d.getFullYear()+r[2]+(d.getMonth()+1)+r[2]+d.getDate();
        }
        // alert(tem+str);
         if(tem==str)
         {
             ret=true;
         }else{
            alert("<bean:message key="kq.search_feast.labor"/>");
            return false;
         }
       return ret;
     } 
</script>
<html:form  action="/kq/options/add_feast_type">
<div class="fixedDiv3">
   <fieldset align="center"  style="width:400px;">
	   <legend ><bean:message key="kq.kq_rest.shuoming"/></legend>
         &nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="kq.search_feast.day"/>
	  </fieldset>
	 <br>
	 <fieldset align="center"  style="width:400px;">
	  <legend ><bean:message key="kq.search_feast.holiday"/></legend>
  <table width="200" border="0" cellpadding="0" class="ListTable" cellspacing="0" align="center">
      <tr >
         <td align="center" class="TableRow" nowrap ><bean:message key="kq.search_feast.name"/></td>
          <td align="center" class="TableRow" nowrap ><bean:message key="kq.search_feast.jday"/></td>
       </tr> 
       <tr>
         <td class="RecordRow" nowrap><html:text name="kqFeastForm" property="feast_name" styleClass="inputtext"  maxlength="20" size="10" /> </td>
         <td class="RecordRow" nowrap><html:text name="kqFeastForm" property="sdate"  styleClass="inputtext"  size="20" /> </td>
      </tr>   
   </table>        
   <br>
   </fieldset>
    <table width="400px;" border="0" cellpmoding="0" cellspacing="0"    cellpadding="0">                                                 
        <tr class="list3">
        <td align="center" colspan="2" style="height:35px;">
         <input type="button"  value="<bean:message key="button.save"/>" class="mybutton" onclick="valide()"> 
         	<input type="button"  value="取消" class="mybutton" onclick="closeWindow();"> 
        </td>
       </tr>          
      </table>
      </div>
</html:form>
