<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.train.trainexam.exam.TrainExamPlanForm"%>
<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script type="text/javascript" src="/ajax/basic.js"></script>
<%
String viewunit="1";
UserView userView=(UserView)session.getAttribute(WebConstant.userView);
if(userView != null)
{
    if(userView.getStatus()==4||userView.isSuper_admin())
      viewunit="0";
}
/**liwc 业务用户走操作单位，没有操作单位时走管理范围=lmm*/
if(userView.getStatus()==0&&!userView.isSuper_admin()){
	String codeall = userView.getUnit_id();
	if(codeall==null||codeall.length()<3)
		viewunit="0";
}
%>
<style>
.fixedDiv11
{ 
  overflow:auto; 
  height:expression(document.body.clientHeight-150);
  width:expression(document.body.clientWidth-22); 
}
</style>
<script language="javascript">
//组织机构树如果显示人员，则先显示人员库
function select_org_emp_dialog22(flag,selecttype,dbtype,priv,isfilter,loadtype)
{
	 //if(dbtype!=1)
	 //	dbtype=0;
	 if(priv!=0)
	    priv=1;
     var theurl="/system/logonuser/org_employ_tree.do?flag="+flag+"`showDb=1`selecttype="+selecttype+"`dbtype="+dbtype+"`nmodule=6`viewunit=<%=viewunit %>"+
                "`priv="+priv + "`isfilter=" + isfilter+"`loadtype="+loadtype;
      var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);  
      
     var return_vo= window.showModalDialog(iframe_url,1, 
        "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
	 return return_vo;
}

function get_hand_query(planId)
 {
    var return_vo;
    return_vo = select_org_emp_dialog22("1","1","-2","1","1","0"); //select_org_emp_dialog4("1","1","0","1","0","1","","","${trainExamStudentForm.examDBPres}",""); 
        
     if(return_vo)
     {
        var sid=return_vo.content;
        var objlist=sid.split(",");
        addObject(objlist,planId); 
    }
 }  
 
  function addObject(objlist,plandId)
  { 
		var hashvo = new ParameterSet();
		hashvo.setValue("objlist",objlist);
    hashvo.setValue("planid",plandId);

    var request=new Request({asynchronous:false,onSuccess:ajaxrefresh,functionId:'2020081024'},hashvo); 
  }



  function del(planId)
  {
    var len=document.trainExamStudentForm.elements.length;
    var uu;
    for (var i=0;i<len;i++)
    {
        if (document.trainExamStudentForm.elements[i].type=="checkbox")
        {
           if(document.trainExamStudentForm.elements[i].checked==true)
           {
             uu="dd";
             break;
            }
        }
    }
    
    if(uu!="dd")
    {
       alert(CHOISE_DELETE_NOT);
       return false;
    }
    
    if(confirm(CONFIRMATION_DEL))
    {    
      trainExamStudentForm.action = "/train/trainexam/exam/student.do?b_delete=link&planid=" + planId; 
      trainExamStudentForm.submit();  
    }
  }
  
  function selByHand(planId)
  {
	  var request=new Request({asynchronous:false,onSuccess:checkDb,functionId:'2020081026'}); 
	  
	  function checkDb(out){
		  get_hand_query(planId);
	  }
      
  }
  
  function selByCond()
  {
      //trainExamStudentForm.action="/train/trainexam/exam/plan.do?b_sel=link&e_flag=cond";
      //trainExamStudentForm.submit();
     // var thecodeurl="/general/inform/search/generalsearch.do?b_query=link&type=1&a_code=${trainExamStudentForm.a_code}&tablename=Usr";
      var dat = new Date();
	    var url="/system/sms/send_sms_query.do?b_query=link`flag=exam`time="+dat.getTime();
	    var iframe_url = "/general/query/common/iframe_query.jsp?src="+$URL.encode(url)+"&tim="+dat.getTime();
      var return_vo= window.showModalDialog(iframe_url, "", 
       	"dialogWidth:700px; dialogHeight:410px;resizable:no;center:yes;scroll:no;status:no");
      if(return_vo!=null){
  	 	var hashvo = new ParameterSet();
		  hashvo.setValue("r5400","${trainExamStudentForm.r5400}");
	    //hashvo.setValue("wherestr",return_vo);
	    hashvo.setValue("expr",return_vo.expr);
	    hashvo.setValue("factor",return_vo.factor);
	    hashvo.setValue("like",return_vo.like);
	    hashvo.setValue("pre",return_vo.pre);
	    var request=new Request({asynchronous:false,onSuccess:ajaxrefresh,functionId:'2020081070'},hashvo); 
  	  }else{
  		  return ;
  	  }
        
  }
  
  function ajaxrefresh(outparamters){
  	if(outparamters!=null){
  		var flag=outparamters.getValue("flag"); 
		if("ok"==flag){
			trainExamStudentForm.action = "/train/trainexam/exam/student.do?b_query=link"; 
      		trainExamStudentForm.submit();
		}else{
			alert("操作失败！");
		}
  	}
  }
  
  function editcourse(r5400,nbase,a0100){
  	//alert(r5400+"\r\n"+nbase+"\r\n"+a0100);
  	trainExamStudentForm.action = "/train/trainexam/exam/student.do?b_edit=link&r5400="+$URL.encode(r5400)+"&nbase="+$URL.encode(nbase)+"&a0100="+$URL.encode(a0100); 
    trainExamStudentForm.submit();
  }
  
  function returnplan(){
    parent.location.href="/train/trainexam/exam/plan.do?b_query=return";
  }
  

  // 查询提交
  function exchange() {
      trainExamStudentForm.action="/train/trainexam/exam/student.do?b_query=link";
      trainExamStudentForm.submit();
  }
  
	// 阅卷
	function marking(a0100,nbase) {
		trainExamStudentForm.action="/train/trainexam/paper/preview/paperspreview.do?b_query=link&exam_type=2&flag=7&returnId=4&paper_id=${trainExamStudentForm.r5400}&a0100="+a0100+"&nbase="+nbase;
      	trainExamStudentForm.submit();
  	}
  	
  //重考
  function reexam(planId)
  { 
    var len=document.trainExamStudentForm.elements.length;
    var uu;
    for (var i=0;i<len;i++)
    {
        if (document.trainExamStudentForm.elements[i].type=="checkbox")
        {
           if(document.trainExamStudentForm.elements[i].checked==true)
           {
             uu="dd";
             break;
            }
        }
    }
    
    if(uu!="dd")
    {  
       alert(TRAIN_EXAM_SEL_STU);
       return false;
    }
    
    if(confirm(TRAIN_REEXAM_CONFIRM))
    {    
      trainExamStudentForm.action = "/train/trainexam/exam/student.do?b_reexam=link&planid=" + planId; 
      trainExamStudentForm.submit();  
    }
  }
</script>
<hrms:themes/>
<html:form action="/train/trainexam/exam/student">
<table border="0" cellspacing="0"  align="center" cellpadding="0" width="100%;" style="margin-left: 1px;">
   <tr>
     <td width="100%">
       &nbsp;<bean:message key="train.examstudent.paperstatus"/>&nbsp;<span style="vertical-align: middle;">
       <html:select name="trainExamStudentForm" property="paperStatus" size="1" onchange="exchange();">
         <html:optionsCollection name="trainExamStudentForm" property="paperStatusList" label="dataName" value="dataValue"/> 
       </html:select></span>
       
       &nbsp;<bean:message key="train.examstudent.checkstatus"/>&nbsp;<span style="vertical-align: middle;">
       <html:select name="trainExamStudentForm" property="checkStatus" size="1" onchange="exchange();">
         <html:optionsCollection name="trainExamStudentForm" property="checkStatusList" label="dataName" value="dataValue"/> 
       </html:select>  </span>

       &nbsp;<bean:message key="label.title.name"/>&nbsp;
       <html:text name="trainExamStudentForm" styleClass="text4" property="studentName"></html:text>
<span style="vertical-align: middle;">
       <input type="button" name="studentName" value="<bean:message key='button.query'/>" class="mybutton" onclick="exchange();"/> 
       </span>
     </td>
   </tr>
   <tr>
   <td>
   <div id="divid" class="fixedDiv11 common_border_color" style="border: 1pt solid;margin-top: 5px;">
      <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
         <thead>
              <tr>
               <td align="center" class="TableRow" width="20" nowrap style="border-left: 0px;border-top: none;">
                 <input type="checkbox" name="selbox" onclick="batch_select(this,'pagination.select');" title='<bean:message key="label.query.selectall"/>'/>
               </td>  
               <td style="display:none">
                 <bean:message key="label.dbase"/>
               </td>
               <td align="center" class="TableRow" style="border-left: 0px;border-top: none;" width="25%" nowrap>
                  <bean:message key="label.title.org"/>
               </td> 
               <td align="center" class="TableRow" style="border-left: 0px;border-top: none;" width="20%" nowrap>
                  <bean:message key="label.title.dept"/>
               </td>  
               <td align="center" width="15%" class="TableRow" style="border-left: 0px;border-top: none;" nowrap>
                  <bean:message key="label.codeitemid.kk"/>         
               </td> 
               <td align="center" width="8%" class="TableRow" style="border-left: 0px;border-top: none;" nowrap>
                  <bean:message key="label.title.name"/>
               </td> 
               <td align="center" width="8%" class="TableRow" style="border-left: 0px;border-top: none;" nowrap>
                  <bean:message key="train.examstudent.paperstatus"/>
               </td>
               <td align="center" width="8%" class="TableRow" style="border-left: 0px;border-top: none;" nowrap>
                  <bean:message key="train.examstudent.checkstatus"/>
               </td>                  
                 
               <td align="center" class="TableRow" width="8%" style="border-left: 0px;border-top: none;" nowrap>
                 阅卷老师
               </td>  
               <td align="center" class="TableRow" width="8%" style="border-left: 0px;border-top: none;border-right: none;" nowrap>
                 <bean:message key="train.examplan.operate"/>
               </td>
             </tr>                      
         </thead>
          <% int i=0; %> 
          <hrms:paginationdb id="element" name="trainExamStudentForm" 
            sql_str="trainExamStudentForm.sqlstr" table="" 
            where_str="trainExamStudentForm.where"
            columns="trainExamStudentForm.column" 
            order_by="order by B0110,E0122,E01A1,NBASE,A0100" 
            pagerows="${trainExamStudentForm.pagerows}" 
            page_id="pagination" indexes="indexes" >
            <bean:define id="planid" name="element" property="r5400"/>
            <bean:define id="dbname" name="element" property="nbase"/>
			<bean:define id="personid" name="element" property="a0100"/>
           <% 
           String r5400 = SafeCode.encode(PubFunc.encrypt(planid.toString()));
           String nbase = SafeCode.encode(PubFunc.encrypt(dbname.toString()));
           String a0100 = SafeCode.encode(PubFunc.encrypt(personid.toString()));
           if(i%2==0){ %>
             <tr class="trShallow" onMouseOver="javascript:tr_onclick(this,'E4F2FC')">
             <%  }else{ %>
             <tr class="trDeep" onMouseOver="javascript:tr_onclick(this,'E4F2FC')">
             <%}%>
             
              <td align="center" class="RecordRow"  nowrap  style="border-left: 0px;border-top: none;">
                 <hrms:checkmultibox name="trainExamStudentForm" property="pagination.select" value="true" indexes="indexes"/>
              </td>     
              <td style="display:none">
                <%=nbase %>
              </td>            
              <td align="left" class="RecordRow"  style="border-left: 0px;border-top: none;"  nowrap>                 
                <hrms:codetoname codeid="UN" name="element" codevalue="b0110" codeitem="codeitem" scope="page"/>         
                &nbsp;<bean:write name="codeitem" property="codename" />
              </td>   
              <td align="left" class="RecordRow" style="border-left: 0px;border-top: none;"  nowrap>               
                <hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="codeitem" uplevel="${trainExamStudentForm.uplevel}" scope="page"/>         
                &nbsp;<bean:write name="codeitem" property="codename" />
              </td>  
              <td align="left" class="RecordRow" style="border-left: 0px;border-top: none;"  nowrap>               
                <hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page"/>         
                &nbsp;<bean:write name="codeitem" property="codename" />
              </td> 
              <td align="left" class="RecordRow" style="border-left: 0px;border-top: none;"  nowrap>               
                &nbsp;<bean:write name="element" property="a0101" filter="true"/>
              </td>  
              <td align="center" class="RecordRow" style="border-left: 0px;border-top: none;"  nowrap>     
                &nbsp;
                <logic:equal name="element" property="r5513" value="-1">
                  <bean:message key="train.paperstatus.unexam"/>&nbsp;
                </logic:equal>
                <logic:equal name="element" property="r5513" value="0">
                  <bean:message key="train.paperstatus.examing"/>&nbsp;
                </logic:equal>
                <logic:equal name="element" property="r5513" value="1">
                  <bean:message key="train.paperstatus.examed"/>&nbsp;
                </logic:equal>  
              </td> 
              <td align="center" class="RecordRow" style="border-left: 0px;border-top: none;"  nowrap>     
                &nbsp;
                <logic:equal name="element" property="r5515" value="-1">
                  <bean:message key="train.checkstatus.uncheck"/>&nbsp;
                </logic:equal>
                <logic:equal name="element" property="r5515" value="0">
                  <bean:message key="train.checkstatus.checking"/>&nbsp;
                </logic:equal>
                <logic:equal name="element" property="r5515" value="1">
                  <bean:message key="train.checkstatus.checked"/>&nbsp;
                </logic:equal>
                <logic:equal name="element" property="r5515" value="2">
                  <bean:message key="train.checkstatus.published"/>&nbsp;
                </logic:equal>
              </td> 
              <td align="center" class="RecordRow" style="border-left: 0px;border-top: none;"  nowrap>     
                &nbsp;<bean:write name="element" property="r5517" filter="true"/>&nbsp;
              </td> 

               <td class="RecordRow" nowrap align="center"  style="border-left: 0px;border-top: none;border-right: none;">
                 <hrms:priv func_id="323830905">
               
               	<logic:notEqual name="trainExamStudentForm" property="planStatus" value="04">               		
                 <a href="###" onclick="editcourse('<%=r5400 %>','<%=nbase %>','<%=a0100 %>');">
                 <bean:message key="button.edit" />
                 </a> 
               	</logic:notEqual>
               	<logic:equal name="trainExamStudentForm" property="planStatus" value="04">
                 	<a href="###" onclick="editcourse('<%=r5400 %>','<%=nbase %>','<%=a0100 %>');">
                 		<bean:message key="button.edit" />
                 	</a> 
                 </logic:equal>
                 </hrms:priv>
                 
                 <hrms:priv func_id="323830906">
                 <logic:equal value="1" name="element" property="r5513">                   
	                 <a href="###" onclick="marking('<%=a0100 %>','<%=nbase %>')">
	                   <bean:message key="train.examstudent.checkpaper"/>
	                 </a> 
                 </logic:equal>
                 </hrms:priv>
                 <logic:notEqual value="1" name="element" property="r5513">
                 	&nbsp;&nbsp;&nbsp;&nbsp;
                 </logic:notEqual>
               </td> 
             <%i++;%>  
       </tr>       
      </hrms:paginationdb> 
      </table> 
      </div>
      </td></tr>
      <tr><td>
       <div style="width:expression(document.body.clientWidth-22);">
      <table width="100%" class="RecordRowP"  align="center">
        <tr>
          <td valign="bottom" class="tdFontcolor">
           <!--  第<bean:write name="pagination" property="current" filter="true" />页
             共<bean:write name="pagination" property="count" filter="true" />条
             共<bean:write name="pagination" property="pages" filter="true" />页 -->
             <hrms:paginationtag name="trainExamStudentForm"
								pagerows="${trainExamStudentForm.pagerows}" property="pagination"
								scope="page" refresh="true">
						</hrms:paginationtag>
          </td>
          <td  align="right" nowrap class="tdFontcolor">
           <p align="right">
           <hrms:paginationdblink name="trainExamStudentForm" property="pagination" nameId="trainExamStudentForm" scope="page">
           </hrms:paginationdblink>
          </td>
        </tr>
      </table>
      </div>
    </tr>
    <tr>
      <td align="left" style="height:35px;">
        <logic:equal name="trainExamStudentForm" property="planStatus" value="01">
          <hrms:priv func_id="323830901">
	        <input type="button" name="tt" class="mybutton" 
	               value="<bean:message key="train.examstudent.selbyhand" />"  
	               onclick="selByHand('<bean:write name="trainExamStudentForm" property="r5400"/>');"/>
	        </hrms:priv>
	        <hrms:priv func_id="323830902">
	        <input type="button" name="start" class="mybutton"
	               value="<bean:message key="train.examstudent.selbycond" />"   
	               onclick="selByCond();">&nbsp;
	        </hrms:priv>
	        <hrms:priv func_id="323830903">
	        <input type="button" name="tdf" class="mybutton"
	               value="<bean:message key="button.delete" />"   
	               onclick="del('<bean:write name="trainExamStudentForm" property="r5400"/>');">
	        </hrms:priv>
        </logic:equal>
        <logic:equal name="trainExamStudentForm" property="planStatus" value="09">
          <hrms:priv func_id="323830901">
	        <input type="button" name="tt" class="mybutton" 
	               value="<bean:message key="train.examstudent.selbyhand" />"  
	               onclick="selByHand('<bean:write name="trainExamStudentForm" property="r5400"/>');"/>
	        </hrms:priv>
	        <hrms:priv func_id="323830902">
	        <input type="button" name="start" class="mybutton"
	               value="<bean:message key="train.examstudent.selbycond" />"   
	               onclick="selByCond();">&nbsp;
	        </hrms:priv>
	        <hrms:priv func_id="323830903">
	        <input type="button" name="tdf" class="mybutton"
	               value="<bean:message key="button.delete" />"   
	               onclick="del('<bean:write name="trainExamStudentForm" property="r5400"/>');">
	        </hrms:priv>
	        <hrms:priv func_id="323830904">
	        <input type="button" name="re" class="mybutton"
                 value="<bean:message key="train.examstudent.redoexam"/>"
                 onclick="reexam('<bean:write name="trainExamStudentForm" property="r5400"/>');">
          </hrms:priv>
        </logic:equal>   
        <logic:equal name="trainExamStudentForm" property="planStatus" value="05">
          <hrms:priv func_id="323830904">
          <input type="button" name="re" class="mybutton"
                 value="<bean:message key="train.examstudent.redoexam"/>"
                 onclick="reexam('<bean:write name="trainExamStudentForm" property="r5400"/>');">
          </hrms:priv>
        </logic:equal>      
        <input type="button" name="rt" class="mybutton"
               value="<bean:message key="button.return" />"   
               onclick="returnplan();">
      </td>
    </tr>
  </table>  
</html:form>