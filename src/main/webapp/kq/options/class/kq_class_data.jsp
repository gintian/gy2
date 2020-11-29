<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/timeS.js"></script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>

<style type="text/css">
body {
	background-color: transparent;
	margin:0px;
}
.m_frameborder {
	border-left: 1px inset #D4D0C8;
	border-top: 1px inset #D4D0C8;
	border-right: 1px inset #FFFFFF;
	border-bottom: 1px inset #FFFFFF;
	width: 41px;
	height: 19px;
	background-color: transparent;
	overflow: hidden;
	text-align: right;
	font-family: "Tahoma";
	font-size: 6px;
}

.m_input {
	width: 18px;
	height: 14px;
	border: 0px solid black;
	font-family: "Tahoma";
	font-size: 9px;
	text-align: right;
}
input{
	background-color:transparent;
}
input_text {
	border: inset 1px #000000;
	BORDER-BOTTOM: #FFFFFF 0pt dotted; 
	BORDER-LEFT: #FFFFFF 0pt dotted; 
	BORDER-RIGHT: #FFFFFF 0pt dotted; 
	BORDER-TOP: #FFFFFF 0pt dotted;	
}

.RecordRowNOH {
    border: inset 1px #94B6E6;
    BORDER-BOTTOM: #94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 1pt solid;
    font-size: 12px;
    text-align: center;
}
</style>
<hrms:themes /> <!-- 7.0css -->
<style type="text/css">
.m_arrow {
	width: 16px;
	height: 8px;
	font-family: "Webdings";
	font-size: 7px;
	line-height: 2px;
	padding-left: 2px;
	cursor: default;
}
</style>
<script type="text/javascript">
	var old_obj;		//旧单元格对象
	var old_input;		//旧表单对象
	var m;
	var old_id;
	var class_id="${kqclassForm.class_id}";
	function edit(obj,name,vlaue_id)
	{		
       var vlaue=document.getElementById(vlaue_id).value; 

       if((old_obj!=null&&old_obj!=obj)||old_obj==null)
	   {        
	     if(class_id=="")
	       return false;     
	                old_input=obj
	                hide_view(vlaue_id);	
	       		m=new minute("m",name,vlaue);
	       		obj.innerHTML =m;	
			old_obj=obj;
			old_id=vlaue_id;	
			setTimeout("focusTest(\"hh_t\")", 100);		
	   }                  
	        					
	}
	
	function hide_view(vlaue_id)
	{
	  if(old_obj!=null&&old_id!=vlaue_id&&old_id!=null)
	  {
	          var vt=m.getNewTime();	                            
	          old_obj.innerHTML="";
	          if(vt==null||vt.length<=0)
			 old_obj.innerHTML="&nbsp;";
			else
			  old_obj.innerHTML=m.getNewTime();							
			var o=document.getElementById(old_id);						
		        o.value=m.getNewTime();		        
	  }	
       }  
       function IsDigit() 
      { 
        return ((event.keyCode >= 46) && (event.keyCode <= 57)&&(event.keyCode!=47)); 
      } 
     function saveClass() 
     { if(old_obj!=null&&old_id!=null&&old_id!=""&&m!=null)
       {
           old_obj.innerHTML=m.getNewTime();							
	   var o=document.getElementById(old_id);						
	   o.value=m.getNewTime();	
       }
        var flag=true;
        var comp=true;
        var note;
        for(var i=1;i<5;i++)
        {
          if(i==1)
           note="时段一";
          else if(i==2)
           note="时段二";
          else if(i==3)
           note="时段三";
          else 
           note="加班"; 
          if(!ondutyCard(i))
          {
            flag=false;            
            break;            
          }else if(!offdutyCard(i))
          {
             flag=false;            
             break; 
          }  
          if(!ondutyCompleteCard(i,note))
          {
             comp=false;
             break;
          }else if(!offdutyCompleteCard(i,note))
          {
             comp=false;
             break;
          }       
        }
        var ob_overtime=document.getElementById("check_tran_overtime"); 
        var ob_overtime_1=document.getElementById("check_tran_overtime_1");
        if(ob_overtime.checked==true)
        {
           ob_overtime_1.value="1";
        }else
        {
           ob_overtime_1.value="0";
        }
        if(!nightComplete())
          comp=false;
        
        if(!comp)
          return;

        if(!flag)
        {
            alert("需刷卡点的时间数据填写完整，\n否则刷卡数据将不能正常分析！");
            return false;
        }
        
        var class_id="${kqclassForm.class_id}";
        if(class_id=="")
        {
          alert("无法在基本班次目录保存，请选择班次！");
          return false;
        }
         
	   	 for(var i=1;i<=4;i++)
	   	 {
	   		var onduty = document.getElementById("onduty_" + i).value;
	   		var offduty = document.getElementById("offduty_" + i).value;
	
	   		if(((onduty == null || "" == onduty) && (offduty != null && offduty != ""))
	   					|| ((onduty != null && onduty != "") && (offduty == null || "" == offduty)))
	   		{
	   			alert("班次定义错误！每个时间段的上班时间点和下班时间点要一一对应！");
	   			return false;
	   		}
	   	 }

    	 kqclassForm.action="/kq/options/class/kq_class_data.do?b_save=link";
    	 kqclassForm.submit();
     } 
     
     function ondutyCard(n)
     {
        var ob=document.getElementById("onduty_card_"+n); 
        var ob2=document.getElementById("onduty_card_1"+n);  
        var on_selected="";
        for(i=0;i<ob.options.length;i++)
        {
          if(ob.options[i].selected)
          {   
             on_selected=ob.options[i].value;   
          }
        }   
        if(on_selected!="0")
        {
          ob2.value=on_selected;
          var onduty_start="onduty_start_"+n;
          var onduty="onduty_"+n;
          var be_late_for="be_late_for_"+n;
          var absent_work="absent_work_"+n;
          var onduty_end="onduty_end_"+n;
          var o=document.getElementById(onduty_start);
          if(o.value=="")
            return false;
          o=document.getElementById(onduty);
          if(o.value=="")
            return false;
          o=document.getElementById(be_late_for);
          if(o.value=="")
            return false;
          o=document.getElementById(absent_work);
          if(o.value=="")
            return false;  
          o=document.getElementById(onduty_end);
          if(o.value=="")
            return false;  
          return true;
        }else
        {
          ob2.value="0";          
          return true;
        }        
      }
      //下班刷卡
     function offdutyCard(n)
     {
        var ob=document.getElementById("offduty_card_"+n);
        var ob2=document.getElementById("offduty_card_1"+n);
        var on_selected="";
        for(i=0;i<ob.options.length;i++)
        {
          if(ob.options[i].selected)
          {   
             on_selected=ob.options[i].value;   
          }
        }    
        if(on_selected!="0")
        {   
           ob2.value=on_selected;
           var offduty_start="offduty_start_"+n;
           var leave_early_absent="leave_early_absent_"+n;
           var leave_early="leave_early_"+n;
           var offduty="offduty_"+n;
           var offduty_end="offduty_end_"+n;
           var o=document.getElementById(offduty_start);
           if(o.value=="")
            return false;
           o=document.getElementById(leave_early_absent);
           if(o.value=="")
            return false;
           o=document.getElementById(leave_early);
           if(o.value=="")
            return false;
           o=document.getElementById(offduty);
           if(o.value=="")
            return false;
           o=document.getElementById(offduty_end);
           if(o.value=="")
            return false; 
           return true;
        }else
        {
          ob2.value="0";
          return true;
        }
      }
      function saveSturt(flag)
      {
         if(flag=="ok")
         {
           alert("基本班次保存成功");
         }else if(flag=="eor")
         {
            alert("基本班次保存失败");
         }
      }
     function ondutyCompleteCard(n,note)
     {
        var ob=document.getElementById("onduty_card_"+n); 
        var ob2=document.getElementById("onduty_card_1"+n);        
        var onduty_start="onduty_start_"+n;
        var onduty="onduty_"+n;
        var be_late_for="be_late_for_"+n;
        var absent_work="absent_work_"+n;
        var onduty_end="onduty_end_"+n;
        var onduty_flextime="onduty_flextime_"+n;
        
        var o=document.getElementById(onduty_start);        
        if(o.value!=""&&o.value.length!=5)
        {
               alert(note+"上班刷卡起时间不完整,"+o.value); 
               return false;
        }
        
        o=document.getElementById(onduty);        
        if(o.value!=""&&o.value.length!=5)
        {
               alert(note+"上班时间不完整,"+o.value);
               return false;
        }

        o=document.getElementById(onduty_flextime);        
        if(o.value!=""&&o.value.length!=5)
        {
               alert(note+"弹性上班时间不完整,"+o.value);
               return false;
        }
        
        o=document.getElementById(be_late_for);
        if(o.value!=""&&o.value.length!=5)
        {
               alert(note+"迟到时间不完整,"+o.value); 
               return false;
           }
        
        o=document.getElementById(absent_work);
        if(o.value!=""&&o.value.length!=5)
        {
               alert(note+"迟到旷工时间不完整,"+o.value);
               return false;
        }  
        
        o=document.getElementById(onduty_end);
        if(o.value!=""&&o.value.length!=5)
        {
               alert(note+"上班刷卡止时间不完整,"+o.value); 
               return false;
         } 
        return true;
      }
      function offdutyCompleteCard(n,note)
      {
           var offduty_start="offduty_start_"+n;
           var leave_early_absent="leave_early_absent_"+n;
           var leave_early="leave_early_"+n;
           var offduty="offduty_"+n;
           var offduty_end="offduty_end_"+n;
           var offduty_flextime="offduty_flextime_"+n;
           
           var o=document.getElementById(offduty_start);
           if(o.value!=""&&o.value.length!=5)
           {
               alert(note+"下班刷卡起时间不完整,"+o.value); 
               return false;
           } 
           
           o=document.getElementById(leave_early_absent);
            if(o.value!=""&&o.value.length!=5)
           {
               alert(note+"早退旷工起时间不完整,"+o.value); 
               return false;
           } 
            
           o=document.getElementById(leave_early);
           if(o.value!=""&&o.value.length!=5)
           {
               alert(note+"早退时间不完整,"+o.value); 
               return false;
           } 
           
           o=document.getElementById(offduty);
           if(o.value!=""&&o.value.length!=5)
           {
               alert(note+"下班时间不完整,"+o.value); 
               return false;
           } 

           o=document.getElementById(offduty_flextime);
           if(o.value!=""&&o.value.length!=5)
           {
               alert(note+"弹性下班时间不完整,"+o.value); 
               return false;
           } 
           
           o=document.getElementById(offduty_end);
           if(o.value!=""&&o.value.length!=5)
           {
               alert(note+"下班刷卡止时间不完整,"+o.value); 
               return false;
           } 
           return true;        
      }
      function nightComplete()
      {
         var o=document.getElementById("night_shift_start");
         if(o.value!=""&&o.value.length!=5)
         {
               alert("夜班开始时间不完整,"+o.value); 
               return false;
         } 
         var o=document.getElementById("night_shift_end");
         if(o.value!=""&&o.value.length!=5)
         {
               alert("夜班结束时间不完整,"+o.value); 
               return false;
         } 
         return true;
      }
      function getSelectedEmploy()
	{
	  var return_vo=select_org_dialog11(0,1,1); 
	  if(return_vo==null)
	    return false;
	  var changePublicRight ='${kqclassForm.changePublicRight}';
	  if(return_vo.content==""&&changePublicRight=="0"){
		  var orgName = document.getElementById("n2");
		  orgName.value = "";
		  var b_save = document.getElementById("b_save");
		  b_save.disabled=true;
		  alert("请选择所属部门！");
		  return;
	  }
	  
	  var orgId=document.getElementById("orgId");
	  if(orgId && return_vo.content.indexOf("@K")==-1){
		  orgId.value=return_vo.content;
	  }else{
		 alert("所属部门不能选择岗位！");
		 return false;
	  }
	  var orgTitle=document.getElementById("n2");
	  if(orgTitle)
		  orgTitle.value=return_vo.title;  
    }
	function select_org_dialog11(flag,selecttype,dbtype,priv,isfilter,loadtype)
	{
		 if(dbtype!=1)
		 	dbtype=0;
		 if(priv!=0)
		    priv=1;
		    
	     var orgId=document.getElementById("orgId");
	     var orgTitle=document.getElementById("n2");
	     var theurl="/system/logonuser/org_tree.do?flag="+flag+"`selecttype="+selecttype+"`dbtype="+dbtype+
	                "`priv="+priv + "`isfilter=" + isfilter+"`loadtype=1`privtype=kq`orgId="+orgId.value
	                +"`orgTitle="+orgTitle.value;
	     if($URL)
	    	 theurl = $URL.encode(theurl);
	     var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
	     var dw=310,dh=400,dl=(screen.width-dw)/2,dt=(screen.height-dh)/2;
	     if (isIE6())
	         dh=430;
	     var return_vo= window.showModalDialog(iframe_url,1, 
	        "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
		 return return_vo;
	}
	
		function reLoading(){
		var classType ='${kqclassForm.classType}';
		var changePublicRight ='${kqclassForm.changePublicRight}';
		if(classType=="1"||(changePublicRight=="0"&&classType=="0")){
			b_save = document.getElementsByName("b_save");
			b_save[0].disabled=true;
			var showCodeList = document.getElementById("showCodeList");
			showCodeList.style.display = "none";	
		}
    }
</script>
<script language="JavaScript" src="/js/validate.js"></script>
<body onload="reLoading()">
<html:form action="/kq/options/class/kq_class_data">
<table width="95%" border="0" cellspacing="0"  align="center" cellpadding="0" style="margin-tOp:10px;">
   <tr> 
            
    <td align="left" nowrap>
    <table width="1150" border="0" cellpadding="0" cellspacing="0" align="center">
        <tr height="20"> 
          <td align=left class="TableRow">&nbsp;
          <logic:notEqual name="kqclassForm" property='kq_class.string(name)' value="">
               <bean:write name="kqclassForm" property="kq_class.string(name)" filter="true"/>
           </logic:notEqual> 
           <logic:equal name="kqclassForm" property='kq_class.string(name)' value="">
                <bean:message key="kq.class.title"/> 
           </logic:equal>   
          </td>
        </tr>
        <tr> 
          <td width="100%" class="framestyle9">
           <table width="95%" border="0" cellpadding="0" cellspacing="0" align="center" class="ListTable">
              <tr> 
                <td width="5%">&nbsp;</td>
                <td width="7%">&nbsp;</td>
                <td width="7%">&nbsp;</td>
                <td width="6%">&nbsp;</td>
                <td width="6%">&nbsp;</td>
		        <td width="6%">&nbsp;</td>
                <td width="6%">&nbsp;</td>
                <td width="7%">&nbsp;</td>
                <td width="7%">&nbsp;</td>
		        <td width="7%">&nbsp;</td>
                <td width="6%">&nbsp;</td>
                <td width="6%">&nbsp;</td>
                <td width="6%">&nbsp;</td>
                <td width="6%">&nbsp;</td>
                <td width="8%">&nbsp;</td>
              </tr>
              <tr> 
                <td class="TableRow">&nbsp;</td>
                <td class="TableRow" align="center"><bean:message key="kq.class.onduty_card"/></td>
                <td class="TableRow" align="center"><bean:message key="kq.class.onduty_start"/></td>
                <td class="TableRow" align="center"><bean:message key="kq.class.onduty"/></td>
                <td class="TableRow" align="center" nowrap>弹性上班时间</td>
                <td class="TableRow" align="center"><bean:message key="kq.class.be_late_for"/></td>
		        <td class="TableRow" align="center"><bean:message key="kq.class.absent_work"/></td>
                <td class="TableRow" align="center"><bean:message key="kq.class.onduty_end"/></td>
                <td class="TableRow" align="center"><bean:message key="kq.class.offduty_card"/></td>
                <td class="TableRow" align="center"><bean:message key="kq.class.offduty_start"/></td>
		        <td class="TableRow" align="center"><bean:message key="kq.class.leave_early_absent"/></td>
                <td class="TableRow" align="center"><bean:message key="kq.class.leave_early"/></td>
                <td class="TableRow" align="center"><bean:message key="kq.class.offduty"/></td>
                <td class="TableRow" align="center" nowrap>弹性下班时间</td>
                <td class="TableRow" align="center"><bean:message key="kq.class.offduty_end"/></td>
              </tr>
              <tr> 
                <td class="RecordRowNOH" height="40" nowrap align="center"><bean:message key="kq.class.tiem1"/></td>
                <td class="RecordRowNOH" nowrap align="center"> 
	                <html:hidden name="kqclassForm" styleId='onduty_start_1' property="kq_class.string(onduty_start_1)" /> 
	                <html:hidden name="kqclassForm" styleId='onduty_1' property="kq_class.string(onduty_1)" /> 
	                <html:hidden name="kqclassForm" styleId='onduty_flextime_1' property="kq_class.string(onduty_flextime_1)" /> 
	                <html:hidden name="kqclassForm" styleId='be_late_for_1' property="kq_class.string(be_late_for_1)" /> 
	                <html:hidden name="kqclassForm" styleId='absent_work_1' property="kq_class.string(absent_work_1)" /> 
	                <html:hidden name="kqclassForm" styleId='onduty_end_1' property="kq_class.string(onduty_end_1)" />      
	                <html:hidden name="kqclassForm" styleId='offduty_start_1' property="kq_class.string(offduty_start_1)" /> 
	                <html:hidden name="kqclassForm" styleId='leave_early_absent_1' property="kq_class.string(leave_early_absent_1)" />
	                <html:hidden name="kqclassForm" styleId='leave_early_1' property="kq_class.string(leave_early_1)" /> 
	                <html:hidden name="kqclassForm" styleId='offduty_1' property="kq_class.string(offduty_1)" />
	                 <html:hidden name="kqclassForm" styleId='offduty_flextime_1' property="kq_class.string(offduty_flextime_1)" /> 
	                <html:hidden name="kqclassForm" styleId='offduty_end_1' property="kq_class.string(offduty_end_1)" /> 
	                <html:hidden name="kqclassForm" styleId='onduty_card_11' property="kq_class.string(onduty_card_1)" /> 
	                <html:hidden name="kqclassForm" styleId='offduty_card_11' property="kq_class.string(offduty_card_1)" /> 
	                <html:select name="kqclassForm" property="kq_class.string(onduty_card_1)" styleId="onduty_card_1" size="1" >
	                    <html:option value="0">否</html:option>
	                    <html:option value="1">是</html:option>
	                    <html:option value="2">不限</html:option>
	                </html:select> 
                </td>
                <td class="RecordRowNOH" nowrap onclick='edit(this,"onduty_start_1","onduty_start_1");' >
		          <bean:write name="kqclassForm" property="kq_class.string(onduty_start_1)" filter="true"/>&nbsp;
		        </td> 
                <td class="RecordRowNOH" nowrap onclick='edit(this,"onduty_1","onduty_1");'>
			<bean:write name="kqclassForm" property="kq_class.string(onduty_1)" filter="true"/>&nbsp;
		</td>
		<td class="RecordRowNOH" nowrap onclick='edit(this,"onduty_flextime_1","onduty_flextime_1");'>
			<bean:write name="kqclassForm" property="kq_class.string(onduty_flextime_1)" filter="true"/>&nbsp;
		</td>
		<td class="RecordRowNOH" nowrap onclick='edit(this,"be_late_for_1","be_late_for_1")'> 
		    <bean:write name="kqclassForm" property="kq_class.string(be_late_for_1)" filter="true"/>&nbsp;
                </td>
		<td class="RecordRowNOH" nowrap onclick='edit(this,"absent_work_1","absent_work_1")'>
		       <bean:write name="kqclassForm" property="kq_class.string(absent_work_1)" filter="true"/>&nbsp;
                </td>
		<td class="RecordRowNOH" nowrap onclick='edit(this,"onduty_end_1","onduty_end_1")'> 
		   <bean:write name="kqclassForm" property="kq_class.string(onduty_end_1)" filter="true"/>&nbsp;
		</td>
                <td class="RecordRowNOH" nowrap align="center">   
                <html:select name="kqclassForm" property="kq_class.string(offduty_card_1)" styleId="offduty_card_1" size="1" >
                    <html:option value="0">否</html:option>
                    <html:option value="1">是</html:option>
                    <html:option value="2">不限</html:option>
                  </html:select> 
                </td>               
                <td class="RecordRowNOH" nowrap onclick='edit(this,"offduty_start_1","offduty_start_1")'> 
                    <bean:write name="kqclassForm" property="kq_class.string(offduty_start_1)" filter="true"/>&nbsp;
                 </td>
                <td class="RecordRowNOH" nowrap  onclick='edit(this,"leave_early_absent_1","leave_early_absent_1")'> 
                 <bean:write name="kqclassForm" property="kq_class.string(leave_early_absent_1)" filter="true"/>&nbsp;
                 </td>              
                <td class="RecordRowNOH" nowrap  onclick='edit(this,"leave_early_1","leave_early_1")'> 
                  <bean:write name="kqclassForm" property="kq_class.string(leave_early_1)" filter="true"/>&nbsp;
		</td>
		 <td class="RecordRowNOH" nowrap onclick='edit(this,"offduty_1","offduty_1")'>
                <bean:write name="kqclassForm" property="kq_class.string(offduty_1)" filter="true"/>&nbsp;
		</td>  
		<td class="RecordRowNOH" nowrap onclick='edit(this,"offduty_flextime_1","offduty_flextime_1");'>
			<bean:write name="kqclassForm" property="kq_class.string(offduty_flextime_1)" filter="true"/>&nbsp;
		</td>
                <td class="RecordRowNOH" nowrap onclick='edit(this,"offduty_end_1","offduty_end_1")'>
		    <bean:write name="kqclassForm" property="kq_class.string(offduty_end_1)" filter="true"/>&nbsp;
		</td>
              </tr>
              <tr> 
                <td class="RecordRowNOH" height="40" nowrap align="center"><bean:message key="kq.class.tiem2"/></td>
                <td class="RecordRowNOH" nowrap align="center"> 
                <html:hidden name="kqclassForm" styleId='onduty_start_2' property="kq_class.string(onduty_start_2)" /> 
                <html:hidden name="kqclassForm" styleId='onduty_2' property="kq_class.string(onduty_2)" /> 
                <html:hidden name="kqclassForm" styleId='onduty_flextime_2' property="kq_class.string(onduty_flextime_2)" /> 
                <html:hidden name="kqclassForm" styleId='be_late_for_2' property="kq_class.string(be_late_for_2)" /> 
                <html:hidden name="kqclassForm" styleId='absent_work_2' property="kq_class.string(absent_work_2)" /> 
                <html:hidden name="kqclassForm" styleId='onduty_end_2' property="kq_class.string(onduty_end_2)" />      
                <html:hidden name="kqclassForm" styleId='offduty_start_2' property="kq_class.string(offduty_start_2)" /> 
                <html:hidden name="kqclassForm" styleId='leave_early_absent_2' property="kq_class.string(leave_early_absent_2)" />
                <html:hidden name="kqclassForm" styleId='leave_early_2' property="kq_class.string(leave_early_2)" /> 
                <html:hidden name="kqclassForm" styleId='offduty_2' property="kq_class.string(offduty_2)" />
                 <html:hidden name="kqclassForm" styleId='offduty_flextime_2' property="kq_class.string(offduty_flextime_2)" /> 
                <html:hidden name="kqclassForm" styleId='offduty_end_2' property="kq_class.string(offduty_end_2)" /> 
                <html:hidden name="kqclassForm" styleId='onduty_card_12' property="kq_class.string(onduty_card_2)" /> 
                <html:hidden name="kqclassForm" styleId='offduty_card_12' property="kq_class.string(offduty_card_2)" /> 
                
                <html:select name="kqclassForm" property="kq_class.string(onduty_card_2)" styleId="onduty_card_2" size="1" >
                    <html:option value="0">否</html:option>
                    <html:option value="1">是</html:option>
                    <html:option value="2">不限</html:option>
                  </html:select> 
                </td>
                <td class="RecordRowNOH" nowrap onclick='edit(this,"onduty_start_2","onduty_start_2");' >
		 <bean:write name="kqclassForm" property="kq_class.string(onduty_start_2)" filter="true"/>&nbsp;
		 
		</td> 
                <td class="RecordRowNOH" nowrap onclick='edit(this,"onduty_2","onduty_2");'>
			<bean:write name="kqclassForm" property="kq_class.string(onduty_2)" filter="true"/>&nbsp;
		</td>
		<td class="RecordRowNOH" nowrap onclick='edit(this,"onduty_flextime_2","onduty_flextime_2");'>
			<bean:write name="kqclassForm" property="kq_class.string(onduty_flextime_2)" filter="true"/>&nbsp;
		</td>
		<td class="RecordRowNOH" nowrap onclick='edit(this,"be_late_for_2","be_late_for_2")'> 
		    <bean:write name="kqclassForm" property="kq_class.string(be_late_for_2)" filter="true"/>&nbsp;
                </td>
		<td class="RecordRowNOH" nowrap onclick='edit(this,"absent_work_2","absent_work_2")'>
		       <bean:write name="kqclassForm" property="kq_class.string(absent_work_2)" filter="true"/>&nbsp;
                </td>
		<td class="RecordRowNOH" nowrap onclick='edit(this,"onduty_end_2","onduty_end_2")'> 
		   <bean:write name="kqclassForm" property="kq_class.string(onduty_end_2)" filter="true"/>&nbsp;
		</td>
                <td class="RecordRowNOH" nowrap align="center">                
                <html:select name="kqclassForm" property="kq_class.string(offduty_card_2)" styleId="offduty_card_2" size="1" >
                    <html:option value="0">否</html:option>
                    <html:option value="1">是</html:option>
                    <html:option value="2">不限</html:option>
                  </html:select> 
                </td>               
                <td class="RecordRowNOH" nowrap onclick='edit(this,"offduty_start_2","offduty_start_2")'> 
                    <bean:write name="kqclassForm" property="kq_class.string(offduty_start_2)" filter="true"/>&nbsp;
                 </td>
                <td class="RecordRowNOH" nowrap  onclick='edit(this,"leave_early_absent_2","leave_early_absent_2")'> 
                 <bean:write name="kqclassForm" property="kq_class.string(leave_early_absent_2)" filter="true"/>&nbsp;
                 </td>              
                <td class="RecordRowNOH" nowrap  onclick='edit(this,"leave_early_2","leave_early_2")'> 
                  <bean:write name="kqclassForm" property="kq_class.string(leave_early_2)" filter="true"/>&nbsp;
		</td>
		 <td class="RecordRowNOH" nowrap onclick='edit(this,"offduty_2","offduty_2")'>
                <bean:write name="kqclassForm" property="kq_class.string(offduty_2)" filter="true"/>&nbsp;
		</td>  
		<td class="RecordRowNOH" nowrap onclick='edit(this,"offduty_flextime_2","offduty_flextime_2");'>
			<bean:write name="kqclassForm" property="kq_class.string(offduty_flextime_2)" filter="true"/>&nbsp;
		</td>
                <td class="RecordRowNOH" nowrap onclick='edit(this,"offduty_end_2","offduty_end_2")'>
		    <bean:write name="kqclassForm" property="kq_class.string(offduty_end_2)" filter="true"/>&nbsp;
		</td>
              </tr>
              <tr> 
                <td class="RecordRowNOH" height="40" nowrap align="center"><bean:message key="kq.class.tiem3"/></td>
                <td class="RecordRowNOH" nowrap align="center"> 
                <html:hidden name="kqclassForm" styleId='onduty_start_3' property="kq_class.string(onduty_start_3)" /> 
                <html:hidden name="kqclassForm" styleId='onduty_3' property="kq_class.string(onduty_3)" /> 
                <html:hidden name="kqclassForm" styleId='onduty_flextime_3' property="kq_class.string(onduty_flextime_3)" /> 
                <html:hidden name="kqclassForm" styleId='be_late_for_3' property="kq_class.string(be_late_for_3)" /> 
                <html:hidden name="kqclassForm" styleId='absent_work_3' property="kq_class.string(absent_work_3)" /> 
                <html:hidden name="kqclassForm" styleId='onduty_end_3' property="kq_class.string(onduty_end_3)" />      
                <html:hidden name="kqclassForm" styleId='offduty_start_3' property="kq_class.string(offduty_start_3)" /> 
                <html:hidden name="kqclassForm" styleId='leave_early_absent_3' property="kq_class.string(leave_early_absent_3)" />
                <html:hidden name="kqclassForm" styleId='leave_early_3' property="kq_class.string(leave_early_3)" /> 
                <html:hidden name="kqclassForm" styleId='offduty_3' property="kq_class.string(offduty_3)" />
                 <html:hidden name="kqclassForm" styleId='offduty_flextime_3' property="kq_class.string(offduty_flextime_3)" /> 
                <html:hidden name="kqclassForm" styleId='offduty_end_3' property="kq_class.string(offduty_end_3)" /> 
                <html:hidden name="kqclassForm" styleId='onduty_card_13' property="kq_class.string(onduty_card_3)" /> 
                <html:hidden name="kqclassForm" styleId='offduty_card_13' property="kq_class.string(offduty_card_3)" /> 
                 
                <html:select name="kqclassForm" property="kq_class.string(onduty_card_3)" styleId="onduty_card_3" size="1" >
                    <html:option value="0">否</html:option>
                    <html:option value="1">是</html:option>
                    <html:option value="2">不限</html:option>
                  </html:select>                
                </td>
                <td class="RecordRowNOH" nowrap onclick='edit(this,"onduty_start_3","onduty_start_3");' >
		 <bean:write name="kqclassForm" property="kq_class.string(onduty_start_3)" filter="true"/>&nbsp;
		 
		</td> 
                <td class="RecordRowNOH" nowrap onclick='edit(this,"onduty_3","onduty_3");'>
			<bean:write name="kqclassForm" property="kq_class.string(onduty_3)" filter="true"/>&nbsp;
		</td>
		<td class="RecordRowNOH" nowrap onclick='edit(this,"onduty_flextime_3","onduty_flextime_3");'>
			<bean:write name="kqclassForm" property="kq_class.string(onduty_flextime_3)" filter="true"/>&nbsp;
		</td>
		<td class="RecordRowNOH" nowrap onclick='edit(this,"be_late_for_3","be_late_for_3")'> 
		    <bean:write name="kqclassForm" property="kq_class.string(be_late_for_3)" filter="true"/>&nbsp;
                </td>
		<td class="RecordRowNOH" nowrap onclick='edit(this,"absent_work_3","absent_work_3")'>
		       <bean:write name="kqclassForm" property="kq_class.string(absent_work_3)" filter="true"/>&nbsp;
                </td>
		<td class="RecordRowNOH" nowrap onclick='edit(this,"onduty_end_3","onduty_end_3")'> 
		   <bean:write name="kqclassForm" property="kq_class.string(onduty_end_3)" filter="true"/>&nbsp;
		</td>
                <td class="RecordRowNOH" nowrap align="center">              
                <html:select name="kqclassForm" property="kq_class.string(offduty_card_3)" styleId="offduty_card_3" size="1" >
                    <html:option value="0">否</html:option>
                    <html:option value="1">是</html:option>
                    <html:option value="2">不限</html:option>
                  </html:select> 
               </td>               
                <td class="RecordRowNOH" nowrap onclick='edit(this,"offduty_start_3","offduty_start_3")'> 
                    <bean:write name="kqclassForm" property="kq_class.string(offduty_start_3)" filter="true"/>&nbsp;
                 </td>
                <td class="RecordRowNOH" nowrap  onclick='edit(this,"leave_early_absent_3","leave_early_absent_3")'> 
                 <bean:write name="kqclassForm" property="kq_class.string(leave_early_absent_3)" filter="true"/>&nbsp;
                 </td>              
                <td class="RecordRowNOH" nowrap  onclick='edit(this,"leave_early_3","leave_early_3")'> 
                  <bean:write name="kqclassForm" property="kq_class.string(leave_early_3)" filter="true"/>&nbsp;
		</td>
		 <td class="RecordRowNOH" nowrap onclick='edit(this,"offduty_3","offduty_3")'>
                <bean:write name="kqclassForm" property="kq_class.string(offduty_3)" filter="true"/>&nbsp;
		</td>  
		<td class="RecordRowNOH" nowrap onclick='edit(this,"offduty_flextime_3","offduty_flextime_3");'>
			<bean:write name="kqclassForm" property="kq_class.string(offduty_flextime_3)" filter="true"/>&nbsp;
		</td>
                <td class="RecordRowNOH" nowrap onclick='edit(this,"offduty_end_3","offduty_end_3")'>
		    <bean:write name="kqclassForm" property="kq_class.string(offduty_end_3)" filter="true"/>&nbsp;
		</td>
              </tr>
               <tr> 
                <td class="RecordRowNOH" height="40" nowrap align="center"><bean:message key="kq.class.tiem4"/></td>
                <td class="RecordRowNOH" nowrap align="center"> 
                <html:hidden name="kqclassForm" styleId='onduty_start_4' property="kq_class.string(onduty_start_4)" /> 
                <html:hidden name="kqclassForm" styleId='onduty_4' property="kq_class.string(onduty_4)" /> 
                <html:hidden name="kqclassForm" styleId='onduty_flextime_4' property="kq_class.string(onduty_flextime_4)" /> 
                <html:hidden name="kqclassForm" styleId='be_late_for_4' property="kq_class.string(be_late_for_4)" /> 
                <html:hidden name="kqclassForm" styleId='absent_work_4' property="kq_class.string(absent_work_4)" /> 
                <html:hidden name="kqclassForm" styleId='onduty_end_4' property="kq_class.string(onduty_end_4)" />      
                <html:hidden name="kqclassForm" styleId='offduty_start_4' property="kq_class.string(offduty_start_4)" /> 
                <html:hidden name="kqclassForm" styleId='leave_early_absent_4' property="kq_class.string(leave_early_absent_4)" />
                <html:hidden name="kqclassForm" styleId='leave_early_4' property="kq_class.string(leave_early_4)" /> 
                <html:hidden name="kqclassForm" styleId='offduty_4' property="kq_class.string(offduty_4)" />
                <html:hidden name="kqclassForm" styleId='offduty_flextime_4' property="kq_class.string(offduty_flextime_4)" /> 
                <html:hidden name="kqclassForm" styleId='offduty_end_4' property="kq_class.string(offduty_end_4)" /> 
                <html:hidden name="kqclassForm" styleId='onduty_card_14' property="kq_class.string(onduty_card_4)" /> 
                <html:hidden name="kqclassForm" styleId='offduty_card_14' property="kq_class.string(offduty_card_4)" /> 
                   
                 <html:select name="kqclassForm" property="kq_class.string(onduty_card_4)" styleId="onduty_card_4" size="1" >
                    <html:option value="0">否</html:option>
                    <html:option value="1">是</html:option>
                   <html:option value="2">不限</html:option>
                  </html:select>             
                </td>
                <td class="RecordRowNOH" nowrap onclick='edit(this,"onduty_start_4","onduty_start_4");' >
		 <bean:write name="kqclassForm" property="kq_class.string(onduty_start_4)" filter="true"/>&nbsp;		 
		</td> 
                <td class="RecordRowNOH" nowrap onclick='edit(this,"onduty_4","onduty_4");'>
			<bean:write name="kqclassForm" property="kq_class.string(onduty_4)" filter="true"/>&nbsp;
		</td>
		<td class="RecordRowNOH" nowrap onclick='edit(this,"onduty_flextime_4","onduty_flextime_4");'>
			<bean:write name="kqclassForm" property="kq_class.string(onduty_flextime_4)" filter="true"/>&nbsp;
		</td>
		<td class="RecordRowNOH" nowrap onclick='edit(this,"be_late_for_4","be_late_for_4")'> 
		    <bean:write name="kqclassForm" property="kq_class.string(be_late_for_4)" filter="true"/>&nbsp;
                </td>
		<td class="RecordRowNOH" nowrap onclick='edit(this,"absent_work_4","absent_work_4")'>
		       <bean:write name="kqclassForm" property="kq_class.string(absent_work_4)" filter="true"/>&nbsp;
                </td>
		<td class="RecordRowNOH" nowrap onclick='edit(this,"onduty_end_4","onduty_end_4")'> 
		   <bean:write name="kqclassForm" property="kq_class.string(onduty_end_4)" filter="true"/>&nbsp;
		</td>
                <td class="RecordRowNOH" nowrap align="center">
                 <html:select name="kqclassForm" property="kq_class.string(offduty_card_4)" styleId="offduty_card_4" size="1" >
                    <html:option value="0">否</html:option>
                    <html:option value="1">是</html:option>
                    <html:option value="2">不限</html:option>
                  </html:select>  
               </td>               
                <td class="RecordRowNOH" nowrap onclick='edit(this,"offduty_start_4","offduty_start_4")'> 
                    <bean:write name="kqclassForm" property="kq_class.string(offduty_start_4)" filter="true"/>&nbsp;
                 </td>
                <td class="RecordRowNOH" nowrap  onclick='edit(this,"leave_early_absent_4","leave_early_absent_4")'> 
                 <bean:write name="kqclassForm" property="kq_class.string(leave_early_absent_4)" filter="true"/>&nbsp;
                 </td>              
                <td class="RecordRowNOH" nowrap  onclick='edit(this,"leave_early_4","leave_early_4")'> 
                  <bean:write name="kqclassForm" property="kq_class.string(leave_early_4)" filter="true"/>&nbsp;
		</td>
		 <td class="RecordRowNOH" nowrap onclick='edit(this,"offduty_4","offduty_4")'>
                <bean:write name="kqclassForm" property="kq_class.string(offduty_4)" filter="true"/>&nbsp;
		</td>  
		<td class="RecordRowNOH" nowrap onclick='edit(this,"offduty_flextime_4","offduty_flextime_4");'>
			<bean:write name="kqclassForm" property="kq_class.string(offduty_flextime_4)" filter="true"/>&nbsp;
		</td>
                <td class="RecordRowNOH" nowrap onclick='edit(this,"offduty_end_4","offduty_end_4")'>
		    <bean:write name="kqclassForm" property="kq_class.string(offduty_end_4)" filter="true"/>&nbsp;
		</td>
              </tr>
			  <tr align="center"> 
               <td colspan="12" height="30">
               </td>
              </tr> 
              <tr>
                <td class="RecordRow" align="center" colspan="6" nowrap>
                <br>
                <html:hidden name="kqclassForm" styleId='night_shift_start' property="kq_class.string(night_shift_start)" /> 
                <html:hidden name="kqclassForm" styleId='night_shift_end' property="kq_class.string(night_shift_end)" /> 
                  <fieldset align="center" style="width:90%;">
    		     <legend><bean:message key="kq.class.Add.setup"/></legend>
		       <table width="90%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">
		          <tr>
		           <td height="20" align="right" > 
                              <bean:message key="lable.pos.e0122"/>&nbsp;&nbsp;&nbsp;&nbsp;                               
                           </td>   
                           <td height="30" align="left" nowrap>
                           	<html:hidden name="kqclassForm" styleId="orgId" property="orgId"/> 
                           	
                             <html:text name="kqclassForm" styleClass="text" property="orgName" styleId="n2" readonly="true"  size="20"/>&nbsp;
								<img src="/images/code.gif" id="showCodeList" onclick='javascript:getSelectedEmploy();' align="center" />&nbsp;
								</td>                              					   
                        <tr>                         
                         <tr>
		         <tr>
		           <td height="20" align="right"> 
                              <bean:message key="kq.class.one_absent"/>&nbsp;&nbsp;&nbsp;&nbsp;                               
                           </td>   
                           <td height="30" align="left"> 
                             <html:text name="kqclassForm" property="kq_class.string(zero_absent)" size="3" styleClass="text" onkeypress="event.returnValue=IsDigit();"/>&nbsp; 
                              <bean:message key="kq.class.hour"/>
                           </td>                              					   
                        <tr>                         
                         <tr>
                          <td height="30" align="right"> 
                              <bean:message key="kq.class.night.start"/>&nbsp;&nbsp;&nbsp;&nbsp;                               
                           </td>
		           <td height="30" onclick='edit(this,"night_shift_start","night_shift_start")' class="RecordRowNOH"> 
                               <bean:write name="kqclassForm" property="kq_class.string(night_shift_start)" filter="true"/>&nbsp;                       
                           </td>                             					   
                        <tr>
                        <tr>
                          <td height="30" align="right"> 
                              <bean:message key="kq.class.night.end"/>&nbsp;&nbsp;&nbsp;&nbsp;                                
                           </td>
		           <td height="30" class="RecordRowNOH" onclick='edit(this,"night_shift_end","night_shift_end")'> 
                               <bean:write name="kqclassForm" property="kq_class.string(night_shift_end)" filter="true"/>&nbsp;                       
                           </td>                             					   
                        </tr>
                        <tr>
                          <td height="30" align="right"> 
                              <bean:message key="kq.class.check_tran_overtime"/>&nbsp;&nbsp;&nbsp;&nbsp;                                
                           </td>
		                   <td height="30"> 
                              <logic:equal name="kqclassForm" property='kq_class.string(check_tran_overtime)' value="1">
                	             <input type="checkbox" name="check_tran_overtime" id="check_tran_overtime" value="1" checked="true"> &nbsp;
                              </logic:equal>    
                              <logic:notEqual name="kqclassForm" property='kq_class.string(check_tran_overtime)' value="1">
                	             <input type="checkbox" name="check_tran_overtime" id="check_tran_overtime" value="1"> &nbsp;
                              </logic:notEqual>     
                              <html:hidden name="kqclassForm" styleId='check_tran_overtime_1' property="kq_class.string(check_tran_overtime)" />     
                           </td>                             					   
                        </tr>
                        <tr>
                          <td height="30" align="right"> 
                              <bean:message key="kq.class.overtime_from"/>&nbsp;&nbsp;&nbsp;&nbsp;                                
                           </td>
		                   <td height="30"> 
                              <html:text name="kqclassForm" property="kq_class.string(overtime_from)" size="3" styleClass="text" onkeypress="event.returnValue=IsDigit();"/>&nbsp;分钟             
                           </td>                             					   
                        </tr>
                        <tr>
                          <td height="30" align="right"> 
                              <bean:message key="kq.class.overtime_type"/>&nbsp;&nbsp;&nbsp;&nbsp;                                
                           </td>
		                   <td height="30"> 
                               <html:select name="kqclassForm" property="kq_class.string(overtime_type)" size="1">
                                  <html:optionsCollection property="overlist" value="dataValue" label="dataName"/>	        
                               </html:select>           
                           </td>                             					   
                        </tr>
		       </table>  
		     </fieldset>
		  <br>&nbsp;&nbsp;
                </td>
                <td class="RecordRow" colspan="7" nowrap>
                <br>
                  <fieldset align="center" style="width:90%;">
    		     <legend><bean:message key="kq.class.explain"/></legend>
		       <table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
		         <tr>
		           <td height="90" align="center"> 
		            <bean:message key="kq.class.explain.content"/>
                           </td>		   
                           						   
                        <tr>
		       </table>  
		     </fieldset>
		  <br>&nbsp;&nbsp;
	       </td>
               
              </tr>
	      <tr align="center" class="list3"> 
               <td colspan="13" height="20">
               </td>
             </tr>
            </table> </td>
        </tr>
      </table></td>
          </tr>
          <tr>
          <td height="30" align="center" style="height:35px;">
              <hrms:priv func_id="">  
	       <input type="button" name="b_save" value='<bean:message key="kq.emp.button.save"/>' onclick="saveClass();" class="mybutton">  	
	      </hrms:priv>
	      <hrms:tipwizardbutton flag="workrest" target="il_body" formname="kqclassForm"/> 	   
          </td>
          </tr>
        </table>
</html:form>
</body>
<script language="javascript">
  saveSturt('<bean:write name="kqclassForm" property="save_flag" filter="true"/>');
  
</script>