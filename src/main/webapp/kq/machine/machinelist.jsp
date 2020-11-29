<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>				
<%
	String url_p=SystemConfig.getCsClientServerURL(request); 
%>
<script language="JavaScript" src="/js/meizzDate.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script type="text/javascript" src="/general/sys/hjaxmanage.js"></script>
<script language="javascript">
var obj=null;
var loard_value=""; 
var num="";

  function download()
  {
    num=$F('machine_num');  
    if(num==""||num==null)
    {
       alert("请选择考勤机！");
       return false;
    }
    var hashvo=new ParameterSet(); 
    hashvo.setValue("machine_num",num);
    var request=new Request({method:'post',onSuccess:setSelect,functionId:'15211001110'},hashvo);    
  }
  function setSelect(outparamters)
  {
      var machine_no=outparamters.getValue("machine_no");   
      var baud_rate=outparamters.getValue("baud_rate");  
      var port=outparamters.getValue("port");
      var type_id=outparamters.getValue("type_id");   
      var ip_address=outparamters.getValue("ip_address");   
      var cardno_len=outparamters.getValue("cardno_len"); 
      getOBj(type_id,machine_no,port,baud_rate,ip_address,cardno_len);  
  }
  
  function getOBj(type_id,machine_no,port,baud_rate,ip_address,cardno_len)
  {
    obj=document.getElementById('KqMachine');  
    //汉王考勤机可以按时段下载,SetDownloadDate必须先于SetParam调用
    if (type_id=="10") {
       var startdate = document.getElementById("start_date").value;
       var enddate = document.getElementById("end_date").value;
       obj.SetDownloadDate(startdate, enddate);
    } 
    obj.SetParam(type_id,machine_no,port,baud_rate,ip_address,cardno_len);    
    loard_value=obj.DownLoad();
  }
  
  function take_over()
  {
    if(obj==null) 
    {
       alert("请先下载数据!");
       return false;
    }else if(loard_value==null||loard_value.length<=0)
    {
       alert("下载数据失败!");
       return false;
    }else
    {
       var machine_data=obj.ReadLines();
       //var machine_data = "00003718:282013032202";
       var hashvo=new ParameterSet(); 
       hashvo.setValue("machine_data",machine_data);
       if(num==""||num==null)
       {
         alert("请选择考勤机,重新下载数据！");
         return false;
       }
       var waitInfo=eval("wait");	   
       waitInfo.style.display="block";
       var cardno_len_obj=document.getElementById('cardno_len'); 
       hashvo.setValue("cardno_len",cardno_len_obj.value);
       hashvo.setValue("machine_num",num);
       var request=new Request({method:'post',onSuccess:setSussess,functionId:'15211001105'},hashvo);    
    }  
  }
  function setSussess(outparamters)
  {
    MusterInitData();
    var flag=outparamters.getValue("flag");  
    if(flag=="ok")
    {
       alert("文件接收成功");
       change();
    }else
    {
       alert("文件接收失败!");
    }
  }
   function MusterInitData()
   {
	   var waitInfo=eval("wait");	
	   waitInfo.style.display="none";
   }
   function change()
   {
      kqCardDataForm.action="/kq/machine/search_card_data.do?b_query=link";
      kqCardDataForm.submit();
   }
   function selectmachinelist()
   {
        var table=document.getElementById('tdl');        
        if(table==null)
  	      return false;
  	    var teIn="名称&nbsp;<input type='text' name='ma_name' id='ma_name' value='' style='width:100px;font-size:10pt;text-align:left'>" ;
  	    teIn=teIn+"&nbsp;&nbsp;";
  	    teIn=teIn+"<input type='button' name='br_return' value='确定' class='mybutton' onclick='selectFind();'>"
  	    table.innerHTML=teIn;
   }
   function selectFind()
   {
       var objname=document.getElementById('ma_name'); 
       if(objname==null)
         return false;
       if(objname.value=="")
       {
          alert("名称不能为空！");
          return false;
       }
       var hashvo=new ParameterSet(); 
       hashvo.setValue("name",objname.value);
       var request=new Request({method:'post',asynchronous:false,onSuccess:showCheckList,functionId:'15211001120'},hashvo);
   }
   function showCheckList(outparamters)
   {
      var machinelist=outparamters.getValue("machinelist");
      if(machinelist==null||machinelist.length<=0)
      {
         alert("没有找到你要筛选的考勤机!");
         return false;
      }      
      var vos= document.getElementById("names");  
      if(vos==null)
  	   return false;
  	  vos.disabled=false; 
  	  for(var i=vos.options.length-1;i>=0;i--)
      {
         vos.options.remove(i);
      } 
      for(var i=0;i<machinelist.length;i++)
      {
         var no = new Option();         
    	 no.value=machinelist[i].dataValue;
    	 no.text=machinelist[i].dataName;  
    	 vos.options[vos.options.length]=no;  	 
      }   
      var table=document.getElementById('tdl');        
      if(table==null)
  	      return false;
  	  var teIn="<a href='###' onclick='viewMachine()'>基本信息</a>" ;
  	  teIn=teIn+"&nbsp;&nbsp;";
  	  teIn=teIn+"<input type='button' name='br_return' value='筛选考勤机' class='mybutton' onclick='selectmachinelist();'>"
  	  table.innerHTML=teIn;  
   }
   function viewMachine()
   {
      var id="";
      var vos= document.getElementById("names");  
      if(vos==null)
  	   return false;  	 
  	  for(var i=vos.options.length-1;i>=0;i--)
      {
         if(vos.options[i].selected)
         {
            id=vos.options[i].value
         }
      } 
      if(id=="")
      {
         alert("请选择考勤机!");
         return;
      }      
      var target_url="/kq/machine/search_card_data.do?b_machine=link&id="+id;    
       var return_vo= window.showModalDialog(target_url,1, 
        "dialogWidth:440px; dialogHeight:350px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
   }
   function incept_data()
   {
      if(obj==null) 
      {
         alert("请先下载数据!");
         return false;
      }

      if(loard_value==null||loard_value.length<=0)
      {
         alert("下载数据失败!");
         return false;
      }
      
         var machine_data=obj.ReadLines();
         //var machine_data = "00000108:302012102301`00000108:312012102301`00000108:322012102301";
         //var machine_data = "00003718:282013032202";
         if(num==""||num==null)
         {
           alert("请选择考勤机,重新下载数据！");
           return false;
         }  
         if(machine_data==""||machine_data==null)
         {
            alert("没有刷卡数据！");
            return false;
         }    
          
         var waitInfo=eval("wait");	     
         waitInfo.style.display="block"; 
         var machine_data_obj=document.getElementById('machine_data'); 
         machine_data_obj.value=machine_data;
         kqCardDataForm.action="/kq/machine/search_card_data.do?b_incept=link";
         kqCardDataForm.submit();
   }

   function checkMachineType() {
	    num=$F('machine_num');  
	    if(num==""||num==null)
	    {
	       return;
	    }
	    
	    var hashvo=new ParameterSet(); 
	    hashvo.setValue("machine_num",num);
	    var request=new Request({method:'post',onSuccess:setDateSelectVisable,functionId:'15211001110'},hashvo); 
   }

   function setDateSelectVisable(outparamters) {
	   var type_id=outparamters.getValue("type_id");
	   if (type_id=="10") {
		   document.getElementById('downloaddates').style.display="";
	   }
	   else {
		   document.getElementById('downloaddates').style.display="none";
	   }
		   
   }

   var outObject;
   var weeks="";
   var feasts ="";
   var turn_dates="";
   var week_dates="";
   function getKqCalendarVar()
   {
     var request=new Request({method:'post',asynchronous:false,onSuccess:setkqcalendar,functionId:'15388800008'});
   }
   function setkqcalendar(outparamters)
   {
      weeks=outparamters.getValue("weeks");  
      feasts=outparamters.getValue("feasts"); 
      turn_dates=outparamters.getValue("turn_dates"); 
      week_dates=outparamters.getValue("week_dates");  
   }
</script>
<html:form action="/kq/machine/search_card_data">  
<br>
 <table  width="500"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0" align="center">   
                            <tr height="20">
       		               <!--   <td width=10 valign="top" class="tableft1"></td>
       		               <td width=130 align=center class="tabcenter"><bean:message key="select.kq.machine"/></td>   
       		               <td width=10 valign="top" class="tabright"></td>
       		               <td valign="top" class="tabremain" width="500"></td> -->
       		               <td  align="center" class="TableRow"><bean:message key="select.kq.machine"/></td>   
       		                      		           	      
                            </tr>                                         
                            <tr>
		               <td width="100%"  class="framestyle9">
		                  <table border="0" cellspacing="0"  align="center" cellpadding="0">
		                  <tr>
                              <td>
                              <div id="downloaddates" style="display:none">
                              <br>
                              <fieldset align="center" style="width:470;">
                                 <legend >下载日期范围</legend>
                                    <br/>
	                                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	                                <html:text name="kqCardDataForm"
	                                  property='start_date' size="10"
	                                  maxlength="20"
	                                  onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false,false);'
	                                  styleClass="TEXT4" />
	                                  ~
	                                <html:text name="kqCardDataForm"
	                                  property='end_date' size="10"
	                                  maxlength="20"
	                                  onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false,false);'
	                                  styleClass="TEXT4" />   
	                                                                                        （目前仅汉王考勤机支持下载可选时段数据）
                                    <br/>
                                    <br/>
                                    <br/>
                              </fieldset>
                              </div>
                              </td>
                            </tr>
		                      <tr>
		                        <td>
		                        <br/>
		                        <br/>
		                          <fieldset align="center" style="width:470;">
    	                            <legend><bean:message key="kq.machine.select"/></legend>
    	                            <br/>
		                            <table width="100%">
		                	         <tr>
			                	         <td  width="20%" align="right" valign="middle">
			                	           <bean:message key="kq.machine.name"/>	                	     
			                	         </td>
			                	         <td  align="left" width="50%" valign="middle">
			                	         &nbsp;
			                	         <hrms:optioncollection name="kqCardDataForm" property="machinelist" collection="list" />
		                                         <html:select name="kqCardDataForm" property="machine_num" size="1" styleId="names" onchange="checkMachineType();">
	                                                 <html:options collection="list" property="dataValue" labelProperty="dataName"/>
	                                                </html:select>    
	                                     <html:hidden name="kqCardDataForm" styleId='cardno_len' property="cardno_len" />                                               
			                	         <html:hidden name="kqCardDataForm" styleId='machine_data' property="machine_data" />                                           
			                	        </td>
			                	        <td id="tdl">
			                	          <!--
			                	          <font color="red">请先筛选</font>    <input type="button" name="btnreturn" value='筛选考勤机' onclick="selectmachinelist();" class="mybutton">               
			                	          -->
			                	        </td>
		                	         </tr>
		                	       </table> 
		                	       <br/>
		                	       <br/>
		                	     </fieldset>
		                        </td>
		                	</tr>
		                	
		                	<tr>
		                	  <td height="40"></td>
		                	</tr>		                	
		                	<tr>
		                         <td height="300" align="center">
		                         <fieldset align="center" style="width:470">
    	                                   <legend ><bean:message key="kq.machine.take"/></legend>
		                            <table width="100%" align="center" border="0">
		                	     <tr>
		                	      <td align="center" style="padding:0px;" valign="top">
									<script type="text/javascript">
									    AxManager.write("KqMachine", 440, 210, AxManager.kqmachPkgName, "<%=url_p%>");
									</script>
                                   </td>
		                	     </tr>
		                	    </table> 
		                	    </fieldset>
		                          </td>
		                        </tr>
		                    </table> 
		                    <br/> 
		                </td>
		            </tr>		            
		            <tr>
		            <td  height="40" align="center" >		                
		                <input type="button" name="btnreturn" value='<bean:message key="kq.machine.download.data"/>' onclick="download();" class="mybutton">
	                        <input type="button" name="btnreturn" value='<bean:message key="kq.machine.take"/>' onclick="incept_data();" class="mybutton">
		                <input type="button" name="btnreturn" value='<bean:message key="button.return"/>' onclick="change();" class="mybutton">
		             </td>
		         </tr>
		       </table>                
</html:form>
<div id='wait' style='position:absolute;top:50;left:200;'>
  <table border="1" width="37%" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
           <tr>
             <td class="td_style common_background_color" height=24>正在接收数据，请稍候....</td>
           </tr>
           <tr>
             <td style="font-size:12px;line-height:200%" align=center>
               <marquee class="marquee_style" direction="right" width="400" scrollamount="5" scrolldelay="10">
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
<script language="javascript">
 MusterInitData();
 checkMachineType();	
</script>