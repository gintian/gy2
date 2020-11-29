<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script type="text/javascript">
//【6738】绩效管理：业绩数据录入，切换考核指标，如果按键盘Backspace键，在进行切换，报错 jingq add 2015.02.03
window.onload=function(){//必须含有body元素才可屏蔽backspace  
	document.getElementsByTagName("body")[0].onkeydown =function(e){            
        //获取事件对象  
        var event = e?e:window.event;
		var elem = event.relatedTarget || event.srcElement || event.target ||event.currentTarget;   
        if(event.keyCode==8){//判断按键为backSpace键  
			//获取按键按下时光标做指向的element  
            var elem = event.srcElement || event.currentTarget;   
            //判断是否需要阻止按下键盘的事件默认传递  
            var name = elem.nodeName;  
            if(name!='INPUT' && name!='TEXTAREA'){  
            	return _stopIt(event);  
            }  
            var type_e = elem.type.toUpperCase();  
            if(name=='INPUT' && (type_e!='TEXT' && type_e!='TEXTAREA' && type_e!='PASSWORD' && type_e!='FILE')){  
            	return _stopIt(event);
            }  
            if(name=='INPUT' && (elem.readOnly==true || elem.disabled ==true)){  
            	return _stopIt(event);  
            }  
        }  
    };
};  
function _stopIt(e){  
	if(e.returnValue){  
		e.returnValue = false ;  
    }  
    if(e.preventDefault ){  
        e.preventDefault();  
    }                 
    return false;
}
</script>
<body>
<html:form action="/performance/achivement/dataCollection/khplanMenu">
	<hrms:tabset name="cardset" height="100%" type="true">
		<logic:iterate id="element" name="dataCollectForm" property="khPlans"
			indexId="index">
			<bean:define id="planID" name="element" property="plan_id" />
			<bean:define id="planName" name="element" property="name" />
			<hrms:tab name="menu${planID}" label="${planName}" visible="true" 
				url="/performance/achivement/dataCollection/dataCollect.do?b_query=link&planId=${planID}">
			</hrms:tab>
		</logic:iterate>
	</hrms:tabset>
</html:form>
</body>
<script language="javascript">
//切换计划是执行对当前页面的保存操作
   function cardset_beforeTabChange(TabSettabSet, stringoldName, stringnewName)
   {
		var objs = window.frames('detail').document.getElementsByTagName('input');
		var saveBt = window.frames('detail').document.getElementById('save');		
		if(saveBt!=null)
			//window.frames('detail').document.getElementById('save').fireEvent("onClick");
			window.frames('detail').changePlan();
   }   	
</script>
