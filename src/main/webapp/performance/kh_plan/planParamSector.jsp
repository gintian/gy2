<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<script type="text/javascript">

function setTemplateString()
{
	var planId = '${param.plan_id}';	
	var status = '${examPlanForm.status}';
	var template_id = '${examPlanForm.template_id}';
		
	var canedit='1';
	getTemplate1(template_id,planId,canedit);			
}

function getTemplate1(templId,planID,canedit)
{	  
	var method = $F('m_'+planID);	
	method=3//程序暂时改为考核模板显示所有，不受考核方法的制约，但是考核方法随着考核模板变动   
	//method=1 显示非个性化项目的模板 method=2 显示个性化项目的模板 method=3 显示全部
	var theurl="/performance/kh_system/kh_template/kh_template_tree.do?b_query=link`subsys_id=33`isVisible=2`persionControl=no`method="+method+"`isEdit="+canedit+"`templateid="+templId;
	if(canedit=='1')
		theurl="/performance/kh_system/kh_template/kh_template_tree.do?b_query=link`subsys_id=33`isVisible=2`persionControl=no`method="+method+"`isEdit=1`templateid="+templId;
	else if(canedit=='0')
		theurl="/performance/kh_system/kh_template/init_kh_item.do?b_query=link`templateid="+templId+"`subsys_id=33`isVisible=2`method="+method+"`isEdit=0";
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);

    temp_parameter=new Object();
    temp_parameter.planID=planID;

  /*  if(window.showModalDialog){
        var return_vo= window.showModalDialog(iframe_url, 'template_win',
            "dialogWidth:800px; dialogHeight:600px;resizable:yes;center:yes;scroll:no;status:no;minimize:yes;maximize:yes;");
        getTemplate_ok(return_vo);
    }else{
        window.open(iframe_url, "template_win", "width=800; height=600;resizable=no;center=yes;scroll=no;status=no");
    }*/
    var config = {
        width:800,
        height:600,
        type:'2'
    }
    modalDialog.showModalDialogs(iframe_url,"template_win",config,getTemplate_ok);

}

function getTemplate_ok(return_vo) {
    if(return_vo==null)
        return;
    var template=return_vo.split(',');
    if(template!=null)
    {
        var templateId = template[1];
        var oldTemplate_id = '${examPlanForm.oldTemplate_id}';
        if(templateId==oldTemplate_id)
        {
            alert("您选择的部门模板和人员模板相同！");
            return;
        }
        document.examPlanForm.action="/performance/kh_plan/kh_params.do?b_save=save&opt=1&planId="+temp_parameter.planID+"&newTemplate_id="+templateId;
        document.examPlanForm.submit();
    }
}


function saveMenRefDeptTmpl()
{
	document.examPlanForm.action="/performance/kh_plan/kh_params.do?b_save=save&opt=2";
	document.examPlanForm.submit();		
}

function showOrHide()
{	
	if(document.getElementById("sectorTemplate").checked==true)
	{
		Element.hide('template_Name');
		Element.hide('b_DeptTmp');
	}else{
		Element.show('template_Name');	
		Element.show('b_DeptTmp');	
	}	
}

function goback()
{
    if(window.showModalDialog) {
        parent.window.close();
    }else{
        window.open("about:blank","_top").close();
    }
}
</script>
<html>
	<hrms:themes />
	<head>
		
	</head>
	<body>
		<html:form action="/performance/kh_plan/kh_params" style="margin-top:0px;margin-left:0px;">
			<bean:message key='jx.khplan.param2.menRefDeptTmpl'/>
			<fieldset align="center">	
				<table border="0" cellspacing="0"  align="left" cellpadding="0" >
	          		<tr align="left" > 
	            		<td>			
							<html:radio styleId="sectorTemplate" name="examPlanForm" property="sectorTemplate" value="0" onclick="showOrHide();"/>
								<bean:message key='jx.khplan.param2.menRefDeptTmpl0' />												
						</td>
	          		</tr>	  
	        		<tr align="left" > 
	            		<td>													
							<html:radio styleId="sectorTemplates" name="examPlanForm" property="sectorTemplate" value="1" onclick="showOrHide();"/>
								<bean:message key='jx.khplan.param2.menRefDeptTmpl1' />
						       
							<html:hidden  name="examPlanForm" styleId="template_id"
								property="template_id" />	
							<html:text name="examPlanForm" styleId="template_Name" property="template_Name"  readonly="true" styleClass="inputtext"/>
							&nbsp;
							<input type="button" value="..." id="b_DeptTmp" class="mybutton" onclick="setTemplateString();" />
						</td>
	          		</tr>
				</table>	                         		           
		    </fieldset>	
		    <table border="0" cellspacing="0"  align="center" cellpadding="0" style="margin-top:5px;">
          		<tr align="center" > 
            		<td>
					    <input type="button" value="确定" id="b_ok" class="mybutton" onclick="saveMenRefDeptTmpl();goback();" />
					    <input type="button" value="取消" id="b_cansal" class="mybutton" onclick="goback();" />
					</td>
          		</tr>	  
        	</table>
        	<script>
        		if(document.getElementById("sectorTemplate").checked==true)
				{
					Element.hide('template_Name');
					Element.hide('b_DeptTmp');
				}else{
					Element.show('template_Name');	
					Element.show('b_DeptTmp');	
				}
				var theStatus = '${examPlanForm.status}';				
				if(theStatus=='5' || theStatus=='0')				
					document.getElementById("b_ok").disabled=false;
				else
					document.getElementById("b_ok").disabled=true;
        	</script>
	   </html:form>								
   </body>
</html>

