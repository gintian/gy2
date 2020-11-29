<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="javascript" src="/js/page_options.js"></script>
<script language="javascript" src="/js/dict.js"></script> 
<script language="javascript">

	function subm()
	{
		jingpinForm.action="/hire/jp_contest/search_jp_pos.do?b_query=link";
 		jingpinForm.submit();
	}
	function ifinit()
	{
		return ( confirm(AFFIRM_DELETE_ALL_DATA+"?") );	
	}
	function init_table(tabame)
	{
		if(ifinit())
		{
			var tablename,table,dataset,preno,bmainset;
		    tablename="table"+tabame;
		    table=$(tablename);
		    dataset=table.getDataset();
		    dataset.clearData();
			var hashvo=new ParameterSet();
			var request=new Request({method:'post',asynchronous:false,onSuccess:init_table_ok,functionId:'3970001005'},hashvo);
		}
		
	}
	function init_table_ok(outparamters)
	{
		jingpinForm.action="/hire/jp_contest/search_jp_pos.do?b_query=link";
 		jingpinForm.submit();
	}
	var gsetname;
	function new_record(tablename)
	{
		gsetname=tablename
		var hashvo=new ParameterSet();
		hashvo.setValue("tablename",tablename);	
		var request=new Request({method:'post',asynchronous:false,onSuccess:setNewRecord,functionId:'3970001002'},hashvo);
	}	
	function setNewRecord(outparamters)
	{
		var tablename,table,dataset,preno,bmainset;
	    tablename="table"+gsetname;
	    table=$(tablename);
	    dataset=table.getDataset();
		record=dataset.getCurrent();
				
		dataset.insertRecord("after");	
	    record=dataset.getCurrent();		  
	    record.setValue("Z0705",outparamters.getValue("Z0705"));  
	    record.setState("modify");
	    record.setValue("z0700",outparamters.getValue("z0700")); 
	    //alert(record.getValue("z0700"));
		if(dm["_2301"]==null)
		{
		    var dmitem= new Object(); 
		    dmitem.id="23";
		    dmitem.value="01";
		    dmitem.name=DRAFT_OUT;
		    dm[dm.length]=dmitem;
		    dm["_2301"]=dm[dm.length-1];
		}
	    record.setValue("z0713","01"); 
	    record.setValue("z0711",outparamters.getValue("Z0711"));
	}
	function to_save()
	{alert(new Date());
		var tablename,table,dataset,preno,bmainset;
	    tablename="table"+gsetname;
	    table=$(tablename);
	    dataset=table.getDataset();
		record=dataset.getCurrent();
	    alert(record.getValue("z0700"));
	}
	function outExcel()
	{
		var  state = "${jingpinForm.state}";	
		var  tablename = "${jingpinForm.tablename}";	
		var hashvo=new ParameterSet();
		hashvo.setValue("state",state);
		hashvo.setValue("tablename",tablename);
		var In_paramters="flag=1"; 	
		var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showfile,functionId:'3970001004'},hashvo);		
	}
	function showfile(outparamters)
	{
		var outName=outparamters.getValue("outputName");
        outName = decode(outName);
        var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName);
	}
	function showDateSelectBox(srcobj)
    {   
	    Element.show('date_panel');   
	    date_desc=srcobj;  
	    var expr_editor=$('date_box');
	    expr_editor.focus();
	    
      	for(var i=0;i<document.jingpinForm.date_box.options.length;i++)
  	  	{
  	  		document.jingpinForm.date_box.options[i].selected=false;
  	  	}
      	var pos=getAbsPosition(srcobj);
	  	with($('date_panel'))
	  	{
	        style.position="absolute";
    		style.posLeft=pos[0]-1;
			style.posTop=pos[1]-1+srcobj.offsetHeight;
			style.width=75;
      	}                 
      
    }
   function changeState()
   {
       Element.hide('date_panel');   
       var hashVo=new ParameterSet();
       var tablename="table${jingpinForm.tablename}";
        table=$(tablename);
        dataset=table.getDataset();
	    var record=dataset.getFirstRecord();
	    var state;
	    // 取得最终要修改的状态
	    for(var i=0;i<jingpinForm.date_box.options.length;i++)
	    {
		    if(jingpinForm.date_box.options[i].selected)
		    {
		    	state=jingpinForm.date_box.options[i].value+"";
	    	}
	    }
	    var selectID="";	
	    var isUsed=0;	
	    var noNum=0;
	    if(state=='05')
	    {
	    	// 取得被选中的记录
		    while (record) 
			{				
				if (record.getValue("select"))
				{							
					 selectID+=","+record.getValue("z0700");						        	    
				}
				record=record.getNextRecord();
			}  	
		    if(selectID.length<0 ||selectID.length==0)
		    {
			    alert(CHOICE_EXECUTE_REGISTER);
			    return;
		    }
	       if(selectID.length>0)
	       {
		        hashVo.setValue("selectIds",selectID);
		        hashVo.setValue("state","05");
		        var In_parameters="flag=1";
		        var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:execute_ok,functionId:'3970001006'},hashVo);					       		 
	       }
	    }
	    if(state=='06')
	    {
		     if(confirm(AFFIRM_OPERATE+"！！"))
		     {
			    while (record) 
				{					
					if (record.getValue("select"))
					{							
						if(record.getValue("z0713")=='05'||record.getValue("z0713")=='09')
							selectID+=","+record.getValue("z0700");							        	    
					}
					record=record.getNextRecord();
				}  	
			    if(selectID.length<0 ||selectID.length==0)
			    {
				    alert(CHOICE_PAST_REGISTER);
				    return;
			    }
		       if(selectID.length>0)
		       {
			        hashVo.setValue("selectIds",selectID);
			        hashVo.setValue("state","06");
			        var In_parameters="flag=1";
			        var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:refresh,functionId:'3970001006'},hashVo);						       		 
		       }
		    }
	    }
	    if(state=='09')
	    {
		     if(confirm(JP_CONSTANT_INFO))
		     {
			    while (record) 
				{
					if (record.getValue("select"))
					{							
						if(record.getValue("z0713")=='05')
							selectID+=","+record.getValue("z0700");							        	    
					}
					record=record.getNextRecord();
				}  	
			    if(selectID.length<0 ||selectID.length==0)
			    {
				    alert(CHOICE_PAUSE_REGISTER);
				    return;
			    }
		       if(selectID.length>0)
		       {		       
			        hashVo.setValue("selectIds",selectID);
			        hashVo.setValue("state","09");
			        var In_parameters="flag=1";
			        var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:refresh,functionId:'3970001006'},hashVo);					       		 
		       }
		    }
	    }
	}
	
	function execute_ok(outparamters)
	{
		 var ishave=outparamters.getValue("have");
		 if(ishave=='no')
		 {
		 	alert(NOT_EXECUTE_OPERATE);
		 }else
		 {
			jingpinForm.action="/hire/jp_contest/search_jp_pos.do?b_query=link";
 			jingpinForm.submit();
		 }
 	}
 	function refresh(outparamters)
 	{
 		jingpinForm.action="/hire/jp_contest/search_jp_pos.do?b_query=link";
 		jingpinForm.submit();
 	}
 	function tableZ07_b_onRefresh(cell, value, record)
	{
	    //table+数据集＋“要加连接的字段”
		if(record!=null)
			cell.innerHTML = "<a href='/workbench/browse/showposinfo.do?b_browse=link&a0100="+record.getValue("z0701")+"&userbase=Usr&npage=1' target='_blank'> <img src='/images/view.gif' border=0> </a >";    
	}
</script>

<html:form action="/hire/jp_contest/search_jp_pos">
<table width="100%" border="0" ><tr><td>
<bean:message key="hire.jp.pos.state"/>
<html:radio name='jingpinForm' property='state' value='00' onclick='subm();'><bean:message key="hire.jp.pos.all"/></html:radio>
<html:radio name='jingpinForm' property='state' value='01' onclick='subm();'> <bean:message key="hire.jp.pos.draftout"/></html:radio>
<html:radio name='jingpinForm' property='state' value='09' onclick='subm();'> <bean:message key="hire.jp.pos.pausee"/></html:radio>
<html:radio name='jingpinForm' property='state' value='05' onclick='subm();'> <bean:message key="hire.jp.pos.executing"/></html:radio>
<html:radio name='jingpinForm' property='state' value='06' onclick='subm();'> <bean:message key="hire.jp.pos.end"/></html:radio>
</td>
</table>


<hrms:dataset name="jingpinForm" property="fieldlist" scope="session" setname="${jingpinForm.tablename}"  setalias="z07_set" readonly="false" editable="true" select="true" sql="${jingpinForm.sql}" buttons="movefirst,prevpage,nextpage,movelast">
<hrms:commandbutton name="newrecord"  functionId="" refresh="true" type="selected" setname="${jingpinForm.tablename}" onclick='new_record("${jingpinForm.tablename}");'>
     <bean:message key="button.insert"/>
    </hrms:commandbutton>
    <hrms:commandbutton name="delselected" hint="<bean:message key='general.inform.search.confirmed.del'/>"  functionId="3970001003"  refresh="true" type="selected" setname="${jingpinForm.tablename}" >
     <bean:message key="button.delete"/>
   </hrms:commandbutton>
    <hrms:commandbutton name="setstate" onclick="showDateSelectBox(this);" functionId="" refresh="true" type="" setname="${jingpinForm.tablename}">
	   <bean:message key="hire.jp.pos.state"/>
	 </hrms:commandbutton>
   <hrms:commandbutton name="export_m"  onclick="outExcel();"  >
     <bean:message key="hire.jp.pos.export"/>
    </hrms:commandbutton> 
	<hrms:commandbutton name="init" hint="<bean:message key='hire.jp.param.affirminitialization'/>"  onclick="init_table('${jingpinForm.tablename}');"  >
     <bean:message key="hire.jp.pos.init"/>
    </hrms:commandbutton>   
    <hrms:commandbutton name="savedata" functionId="3970001007" refresh="true" type="all-change" setname="${jingpinForm.tablename}"  >
     <bean:message key="button.save"/>
   </hrms:commandbutton>
</hrms:dataset>

<div id="date_panel" >
   			<select onblur="Element.hide('date_panel');" id="date_box" name="date_box" multiple="multiple" size="3"  style="width:75"  onchange="changeState();" onblur="Element.hide('date_panel');" >    		
			    <option value="05"><bean:message key="hire.jp.pos.promulgat"/></option>
			    <option value="09"><bean:message key="hire.jp.pos.pausee"/></option>				    
			    <option value="06"><bean:message key="hire.jp.pos.end"/></option>	    			    		    
              </select>
         </div>
         
<script language="javascript">
   Element.hide('date_panel');
  
</script>

</html:form>
