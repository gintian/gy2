<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.general.query.CommonQueryForm,        
                 java.util.*"%>
<script language="JavaScript" src="/js/function.js"></script>
<%
    CommonQueryForm commonQueryForm=(CommonQueryForm)session.getAttribute("commonQueryForm");
    String[] dbpre=commonQueryForm.getDbpre();
    ArrayList factorlist=commonQueryForm.getFactorlist();
    int i=0;
    int j=0;
    int num=factorlist.size();
    int status=0;
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    status=userView.getStatus();  
    String manager=userView.getManagePrivCodeValue();  

   /**
    * 由先前的按人员管理范围控制改成按如规则进行控制
    * 人员、单位和岗位按业务范围-操作单位-人员管理范围优先级进行控制 
    * cmq changed at 2012-09-29
    */  
    if(commonQueryForm.getType().equalsIgnoreCase("1")||commonQueryForm.getType().equalsIgnoreCase("2")||commonQueryForm.getType().equalsIgnoreCase("3"))
    {
        manager=userView.getUnitIdByBusi("4");
    }
    //end.  
    
%>
<script language="javascript">
   var date_desc;
   /*只有一个库时,对库进行隐藏*/
 
   
   function setSelectValue()
   {
     if(date_desc)
     {
       date_desc.value=$F('date_box');
       Element.hide('date_panel');   
     }
   }
   
   function showDateSelectBox(srcobj)
   {
       //if(event.button==2)
       //{
          date_desc=srcobj;
          Element.show('date_panel');   
          var pos=getAbsPosition(srcobj);
      with($('date_panel'))
      {
            style.position="absolute";
            if(navigator.appName.indexOf("Microsoft")!= -1){
                style.posLeft=pos[0]-1;
                style.posTop=pos[1]-1+srcobj.offsetHeight;
            }else{
                style.left=pos[0]+"px";
                style.top=pos[1]+srcobj.offsetHeight+"px";
            }
            style.width=(srcobj.offsetWidth<150)?150:srcobj.offsetWidth+1;
          }                 
       //}
   }
     
    function submitCond()
    {//邓灿修改
        <logic:equal name="commonQueryForm" property="query_type" value="1">
            var n=1;
            var _str="1";
            var expressionObjs_self=document.getElementsByName('expression')[0].value;
            expressionObjs_self=replaceAll(expressionObjs_self, "+", "\*" );        
            var temps=expressionObjs_self.split("\*")
            for(var i=0;i<document.commonQueryForm.elements.length;i++)
            {
                if(document.commonQueryForm.elements[i].id&&document.commonQueryForm.elements[i].id.indexOf("log")!=-1&&document.commonQueryForm.elements[i].type=='select-one')
                {
                    n++;
                    _str+=document.commonQueryForm.elements[i].value+n;                             
                }       
            }
           var expression=_str;
        </logic:equal>
        <logic:notEqual name="commonQueryForm" property="query_type" value="1">
            var  expressionObjs=document.getElementsByName('expression');
            var expressionObj=expressionObjs[0];
            var expression=expressionObj.value;
        </logic:notEqual>
       var hashvo=new ParameterSet();
       hashvo.setValue("expression",expression);
       var vosId= document.getElementsByName('hz');
       var vosoper= document.getElementsByName('oper');
       var vosFieldname=document.getElementsByName("itemid");  
       <logic:equal name="commonQueryForm" property="query_type" value="1">   
          var vosLog=document.getElementsByName("log");       
       </logic:equal>      
       var arr=new Array();
          // alert(vosId.length+":"+vosoper.length+":"+vosFieldname.length);
       if(vosId&&vosoper&&vosFieldname&&vosFieldname.length==vosId.length&&vosId.length==vosoper.length)
       {
           for(var r=0;r<vosId.length;r++)
           {
                  var objId=vosId[r];
                  var objfieldname=vosFieldname[r];               
                  var value=objId.value;
                  var fieldname=objfieldname.value;
                  var log="";
                  <logic:equal name="commonQueryForm" property="query_type" value="1">
                    if(r!=0)
                    {
                        var oolog=vosLog[r-1];                     
                        if(!oolog)
                          break;
                        for(var i=0;i<oolog.options.length;i++)
                        {
                          if(oolog.options[i].selected)
                          {
                            log=oolog.options[i].value;                           
                            break;
                          }
                        } 
                    }
                  </logic:equal>   
                  var objOper=vosoper[r];   
                  var oper="";
                  for(var i=0;i<objOper.options.length;i++)
                  {
                      if(objOper.options[i].selected)
                      {
                         oper=objOper.options[i].value;
                         break;
                      }
                  }     
                               
                  var oobj=new Object();
                  oobj.value=value;
                  oobj.oper=oper;
                  oobj.fieldname=fieldname;   
                  oobj.log=log;             
                  arr[r]=oobj;                  
           }  
       }    
       hashvo.setValue("type","${commonQueryForm.type}");
       hashvo.setValue("arr",arr); 
       var  request=new Request({onSuccess:showSelect,functionId:'0202011007'},hashvo);
    } 
    function showSelect(outparamters)
    {
       alert("校验成功！");
       var expr=outparamters.getValue("expr");
       var  objs=document.getElementsByName('expr');
       var obj=objs[0];
       obj.value=expr;
    }
    function getCond(){ 
        if(navigator.appName.indexOf("Microsoft")!= -1){
           window.returnValue=$('expr').value;         
           window.close();  
       }else{
            top.returnValue=$('expr').value;           
           top.close();
       }        
    }
    
    function isHaveDbPre(info)
    {
        
        
    var expression = document.getElementById("expression").value;
    if(expression==null||expression.length<1){
        return;
    }
    var size ="<%=num %>";
    var hashvo=new ParameterSet();
    hashvo.setValue("expression",getDecodeStr(expression));
    hashvo.setValue("size",size);
    var request=new Request({method:'post',asynchronous:false,onSuccess:check_ok,functionId:'0202011017'},hashvo);  
           
            
    }
    function check_ok(outparameters)
{
   var info=outparameters.getValue("info");
   if(info=='')
   {
     commonQueryForm.action="/kq/options/sign_point/common_query.do?b_query=query";
     commonQueryForm.submit();
    
   }
   else
   {
     return ;
   }
}
</script>
<base id="mybase" target="_self">
<html:form action="/kq/options/sign_point/common_query">
  <br>
  <br>
<html:hidden property="expr"/>  
  <table width="550" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr height="20">
            <!--  td width=1 valign="top" class="tableft1"></td>
            <logic:equal name="commonQueryForm" property="query_type" value="1">
                <td width=130 align=center class="tabcenter">&nbsp;<bean:message key="label.query.hquery"/>&nbsp;</td>
                </logic:equal> 
            <logic:equal name="commonQueryForm" property="query_type" value="2">
                <td width=130 align=center class="tabcenter">&nbsp;<bean:message key="label.query.cquery"/>&nbsp;</td>
                </logic:equal>
            <logic:equal name="commonQueryForm" property="query_type" value="3">
                <td width=130 align=center class="tabcenter">&nbsp;<bean:message key="label.sys.cond"/>&nbsp;</td>
                </logic:equal>                                                      
            <td width=10 valign="top" class="tabright"></td>
            <td valign="top" class="tabremain" width="700"></td-->  
             <td align="left" colspan="4" class="TableRow">&nbsp;
              <bean:message key="label.query.hquery"/>
                &nbsp;</td>                       
          </tr> 
          <tr>
            <td colspan="4" class="framestyle3">
               <table border="0"  cellspacing="0" width="70%" class="ListTable"  cellpadding="2" align="center">
                        
                                            
                      <tr><td colspan="4">&nbsp;</td></tr>
                      <tr><td colspan="4"> 
                      <table border="0"  cellspacing="0" width="70%" class="ListTable1"  cellpadding="2" align="center">
                      <tr>
                         <logic:equal name="commonQueryForm" property="query_type" value="1">                       
                          <td align="center" nowrap class="TableRow"><bean:message key="label.query.logic"/></td>
                         </logic:equal> 
                         <logic:notEqual name="commonQueryForm" property="query_type" value="1">    
                          <td align="center" nowrap class="TableRow"><bean:message key="label.query.number"/></td>
                         </logic:notEqual>                                                    
                          <td align="center" nowrap class="TableRow"><bean:message key="label.query.field"/></td>
                          <td align="center" nowrap class="TableRow"><bean:message key="label.query.relation"/></td>
                          <td align="center" nowrap class="TableRow"><bean:message key="label.query.value"/></td>
                        
                      </tr> 
                        
                        <logic:equal name="commonQueryForm" property="define" value="1">         
                      <logic:iterate id="element" name="commonQueryForm"  property="factorlist" indexId="index"> 
                      <tr>       
                         <logic:equal name="commonQueryForm" property="query_type" value="1">    
                           <td align="center" class="RecordRow" nowrap >
                             <%
                                if(i!=0)
                                {
                             %>
                               <select name="log" id="${index }log" size="1">                               
                                <option value="*">并且</option>
                                <option value="+">或</option>
                               </select>
                               <script type="text/javascript">
                                    var selected="<bean:write name="commonQueryForm" property='<%="factorlist["+index+"].log"%>' />";
                                    //document.getElementById('${index }log').value=selected;
                                    var _options=document.getElementById('${index }log').options;
                                        if("*"==selected){
                                            _options[0].selected=true;
                                        }
                                        if("+"==selected){
                                            _options[1].selected=true;
                                        }
                               </script>
                             <%
                               }
                             %>
                          </td>
                         </logic:equal> 
                         <logic:notEqual name="commonQueryForm" property="query_type" value="1">    
                           <td align="center" class="RecordRow" nowrap >
                <%=i+1%>　   
                          　</td>
                         </logic:notEqual>                          
                          <td align="center" class="RecordRow" nowrap >
                             <bean:write name="element" property="hz" />&nbsp;
                              <input type="hidden" name="itemid" id='itemid' value="<bean:write name="element" property="fieldname" />" >
                          </td>  
                          <td align="center" class="RecordRow" nowrap >
                               <select name="oper" id="${index }select" size="1">
                                <option value="=">=</option>
                                <option value="&gt;">&gt;</option>
                                <option value="&gt;=">&gt;=</option>
                                <option value="&lt;">&lt;</option>
                                <option value="&lt;=">&lt;=</option>
                                <option value="&lt;&gt;">&lt;&gt;</option>  
                               </select>
                               <script type="text/javascript">
                                    var selected="<bean:write name="commonQueryForm" property='<%="factorlist["+index+"].oper"%>' />";
                                    var _options=document.getElementById('${index }select').options;
                                        if("="==selected){
                                            _options[0].selected=true;
                                        }
                                        if("&gt;"==selected){
                                            _options[1].selected=true;
                                        }
                                        if("&gt;="==selected){
                                            _options[2].selected=true;
                                        }
                                        if("&lt;"==selected){
                                            _options[3].selected=true;
                                        }
                                        if("&lt;="==selected){
                                            _options[4].selected=true;
                                        }
                                        if("&lt;&gt;"==selected){
                                            _options[5].selected=true;
                                        }
                               </script>
                          </td>                                                  
                          <!--日期型 -->                            
                          <logic:equal name="element" property="fieldtype" value="D">
                            <td align="left" class="RecordRow" nowrap>                
                            <input type="text" value="<bean:write name="commonQueryForm" property='<%="factorlist["+index+"].value"%>' />" size="30" maxlength="30" name="hz" class="text4" ondblclick="showDateSelectBox(this);" />
                            </td>                           
                          </logic:equal>
                          <!--备注型 -->                              
                          <logic:equal name="element" property="fieldtype" value="M">
                            <td align="left" class="RecordRow" nowrap>                
                                <input type="text" value="<bean:write name="commonQueryForm" property='<%="factorlist["+index+"].value"%>' />" size="30" maxlength='<%="factorlist["+index+"].itemlen"%>' name="hz" class="text4" />
                            </td>                           
                          </logic:equal>
                          <!--字符型 -->                                                    
                          <logic:equal name="element" property="fieldtype" value="A">
                            <td align="left" class="RecordRow" nowrap>
                              <logic:notEqual name="element" property="codeid" value="0">
                                <input type="hidden" value="<bean:write name="commonQueryForm" property='<%="factorlist["+index+"].value"%>' />" name="hz" id='<%="factorlist["+index+"].value"%>' class="text4"/>
                                <html:text name="commonQueryForm" property='<%="factorlist["+index+"].hzvalue"%>' size="30" maxlength="50" styleClass="text4" onchange="fieldcode(this,1)"/>
                                         <logic:equal name="element" property="fieldname" value="b0110"> 
                                           <img src="/images/code.gif" onclick='openInputCodeDialogOrgInputPos("UN","<%="factorlist["+index+"].hzvalue"%>","<%=manager%>",1);'/>
                                         </logic:equal> 
                                         <logic:notEqual name="element" property="fieldname" value="b0110">   
                                            <logic:equal name="element" property="fieldname" value="e0122"> 
                                                <img src="/images/code.gif" onclick='openInputCodeDialogOrgInputPos("UM","<%="factorlist["+index+"].hzvalue"%>","<%=manager%>",1);'/>
                                            </logic:equal>
                                            <logic:equal name="element" property="fieldname" value="e01a1"> 
                                                <img src="/images/code.gif" onclick='openInputCodeDialogOrgInputPos("@K","<%="factorlist["+index+"].hzvalue"%>","<%=manager%>",1);'/>
                                            </logic:equal>
                                            <logic:notEqual name="element" property="fieldname" value="e0122"> 
                                            <logic:notEqual name="element" property="fieldname" value="e01a1"> 
                                                <img src="/images/code.gif" onclick='openCondCodeDialog("${element.codeid}","<%="factorlist["+index+"].hzvalue"%>");'/>
                                            </logic:notEqual>     
                                            </logic:notEqual>                                                                                                                           
                                         </logic:notEqual>                                    
                                
                              </logic:notEqual> 
                              <logic:equal name="element" property="codeid" value="0">
                                <input type="text" value="<bean:write name="commonQueryForm" property='<%="factorlist["+index+"].value"%>' />" size="30" maxlength="${element.itemlen}" name="hz" class="text4" />
                              </logic:equal>                               
                            </td>                           
                          </logic:equal> 
                          <!--数据值-->                            
                          <logic:equal name="element" property="fieldtype" value="N">
                            <td align="left" class="RecordRow" nowrap>      
                                <html:text name="commonQueryForm" property='<%="factorlist["+index+"].value"%>' size="30" styleId="hz"  maxlength="${element.itemlen}" styleClass="text4"/>                               
                            </td>                           
                          </logic:equal>                           
                       </tr>        
                       <%
                        ++i;
                       %>                    
                       </logic:iterate>
                       </logic:equal>
                       <logic:equal name="commonQueryForm" property="define" value="0"> 
                                             <logic:iterate id="element" name="commonQueryForm"  property="factorlist" indexId="index"> 
                      <tr>       
                         <logic:equal name="commonQueryForm" property="query_type" value="1">    
                           <td align="center" class="RecordRow" nowrap >
                             <%
                                if(i!=0)
                                {
                             %>
                               <html:select name="commonQueryForm" property='<%="factorlist["+index+"].log"%>' styleId="log" size="1">
                                  <html:optionsCollection property="logiclist" value="dataValue" label="dataName"/>                                  
                               </html:select>
                             <%
                               }
                             %>
                          </td>
                         </logic:equal> 
                         <logic:notEqual name="commonQueryForm" property="query_type" value="1">    
                           <td align="center" class="RecordRow" nowrap >
                <%=i+1%>　   
                          　</td>
                         </logic:notEqual>                          
                          <td align="center" class="RecordRow" nowrap >
                             <bean:write name="element" property="hz" />&nbsp;
                              <input type="hidden" name="feildname" id='itemid' value="<bean:write name="element" property="fieldname" />" >
                          </td>  
                          <td align="center" class="RecordRow" nowrap >
                               <html:select name="commonQueryForm" property='<%="factorlist["+index+"].oper"%>' styleId="oper" size="1">
                                  <html:optionsCollection property="operlist" value="dataValue" label="dataName"/>                                   
                               </html:select>
                          </td>                                                  
                          <!--日期型 -->                            
                          <logic:equal name="element" property="fieldtype" value="D">
                            <td align="left" class="RecordRow" nowrap>                
                <html:text name="commonQueryForm" property='<%="factorlist["+index+"].value"%>' size="30" maxlength="30" styleId="hz"  styleClass="text4" ondblclick="showDateSelectBox(this);" />
                            </td>                           
                          </logic:equal>
                          <!--备注型 -->                              
                          <logic:equal name="element" property="fieldtype" value="M">
                            <td align="left" class="RecordRow" nowrap>                
                               <html:text name="commonQueryForm" property='<%="factorlist["+index+"].value"%>' size="30" styleId="hz"  maxlength='<%="factorlist["+index+"].itemlen"%>' styleClass="text4"/>                               
                            </td>                           
                          </logic:equal>
                          <!--字符型 -->                                                    
                          <logic:equal name="element" property="fieldtype" value="A">
                            <td align="left" class="RecordRow" nowrap>
                              <logic:notEqual name="element" property="codeid" value="0">
                                <html:hidden name="commonQueryForm" property='<%="factorlist["+index+"].value"%>' styleId="hz"  styleClass="text4"/>                               
                                <html:text name="commonQueryForm" property='<%="factorlist["+index+"].hzvalue"%>' size="30" maxlength="50" styleClass="text4" onchange="fieldcode(this,1)"/>
                                         <logic:equal name="element" property="fieldname" value="b0110"> 
                                           <img src="/images/code.gif" align="middle" onclick='openInputCodeDialogOrgInputPos("UN","<%="factorlist["+index+"].hzvalue"%>","<%=manager%>",1);'/>
                                         </logic:equal> 
                                         <logic:notEqual name="element" property="fieldname" value="b0110">   
                                            <logic:equal name="element" property="fieldname" value="e0122"> 
                                                <img src="/images/code.gif" align="middle"  onclick='openInputCodeDialogOrgInputPos("UM","<%="factorlist["+index+"].hzvalue"%>","<%=manager%>",1);'/>
                                            </logic:equal>
                                            <logic:equal name="element" property="fieldname" value="e01a1"> 
                                                <img src="/images/code.gif" align="middle"  onclick='openInputCodeDialogOrgInputPos("@K","<%="factorlist["+index+"].hzvalue"%>","<%=manager%>",1);'/>
                                            </logic:equal>
                                            <logic:notEqual name="element" property="fieldname" value="e0122"> 
                                            <logic:notEqual name="element" property="fieldname" value="e01a1"> 
                                                <img src="/images/code.gif" align="middle"  onclick='openCondCodeDialog("${element.codeid}","<%="factorlist["+index+"].hzvalue"%>");'/>
                                            </logic:notEqual>  
                                            </logic:notEqual>                                                                                                                             
                                         </logic:notEqual>                                    
                                
                              </logic:notEqual> 
                              <logic:equal name="element" property="codeid" value="0">
                                <html:text name="commonQueryForm" property='<%="factorlist["+index+"].value"%>' size="30" styleId="hz"  maxlength="${element.itemlen}" styleClass="text4"/>                               
                              </logic:equal>                               
                            </td>                           
                          </logic:equal> 
                          <!--数据值-->                            
                          <logic:equal name="element" property="fieldtype" value="N">
                            <td align="left" class="RecordRow" nowrap>      
                                <html:text name="commonQueryForm" property='<%="factorlist["+index+"].value"%>' size="30" styleId="hz"  maxlength="${element.itemlen}" styleClass="text4"/>                               
                            </td>                           
                          </logic:equal>                           
                       </tr>        
                       <%
                        ++i;
                       %>                    
                       </logic:iterate>
                       </logic:equal>
                       
                       <logic:notEqual name="commonQueryForm" property="query_type" value="1">
                       <tr>
                       <td align="left" nowrap class="RecordRow" colspan="4">
                         <span><bean:message key="label.query.expression"/></span><br>
                         <html:textarea property="expression" rows="5" cols="60"/>
                        </td>
                       </tr>                             
                      </logic:notEqual>
                      <logic:equal name="commonQueryForm" property="query_type" value="1">
                        <html:hidden name="commonQueryForm" property='expression'/>  
                      </logic:equal>
                      <logic:equal name="commonQueryForm" property="define" value="0">                        
                        <tr>
                            <td align="center" nowrap class="RecordRow" colspan="4">
                                <html:checkbox name="commonQueryForm" property="like" value="1"><bean:message key="label.query.like"/></html:checkbox>
        <!--  <% //if(status==0){%>                               
                                <html:checkbox name="commonQueryForm" property="result" value="1"><bean:message key="hmuster.label.search_result"/></html:checkbox>
                  <%//}%>                                 
                                <html:checkbox name="commonQueryForm" property="history" value="1"><bean:message key="label.query.history"/></html:checkbox>           
                             --> 
                            </td>
                        </tr> 
                       </logic:equal>                           
                      </table>
                      </td>
                      </tr>
                      <tr><td height="15" colspan="4"></td></tr>                                      
           </table>                 
            </td>
          </tr>
          <tr class="list3">
            <td align="center" colspan="2">
        &nbsp;           
            </td>
          </tr>            
          <tr class="list3">
            <td colspan="4">
                 <html:button styleClass="mybutton"  property="b_query" onclick=" isHaveDbPre('${commonQueryForm.type}');">
                    <bean:message key="button.query"/>
                 </html:button>
                       
           <!--       <hrms:submit styleClass="mybutton"  property="b_list_cond">
                    <bean:message key="button.save"/>
             </hrms:submit> -->          
                 <hrms:submit styleClass="mybutton" property="br_return">
                    <bean:message key="button.query.pre"/>
             </hrms:submit>              

             <!-- 
                 <html:reset styleClass="mybutton">
                    <bean:message key="button.clear"/>
             </html:reset>  
              -->
            </td>
          </tr>  
  </table>
  <logic:equal name="commonQueryForm" property="define" value="1"> 
  <br>
  <br>  
         <fieldset align="center" style="width:80%;">
         <legend ><bean:message key="label.description" /></legend>
             <bean:message key="label.query.desc" />
         </fieldset>
  </logic:equal>
                     <div id="date_panel">
            <select name="date_box" multiple="multiple" size="10"  style="width:200" onchange="setSelectValue();" onclick="setSelectValue();">
                <option value="$AGE_Y[10]">年份差</option>                 
                <option value="$WORKAGE[10]">工龄</option>                    
                <option value="$YRS[10]">年限</option>
                <option value="当年">当年</option>
                <option value="当月">当月</option>
                <option value="当天">当天</option>                      
                <option value="今天">今天</option>
                <option value="截止日期">截止日期</option>
                <option value="1992.4.12">1992.4.12</option>    
                <option value="1992.4">1992.4</option>  
                <option value="1992">1992</option>              
                <option value="????.??.12">????.??.12</option>
                <option value="????.4.12">????.4.12</option>
                <option value="????.4">????.4</option>                                          
                        </select>
                    </div>
</html:form>
<script language="javascript">
   Element.hide('date_panel');
   
   <% if(dbpre!=null&&dbpre.length>0){
            for(int e=0;e<dbpre.length;e++)
            {
                String a_dbpre=dbpre[e];
                if(a_dbpre!=null&&a_dbpre.length()>0){
    %>
                selectedDbpreBox('<%=a_dbpre%>');       
    <%      
                }
            }
        }
     %>
   
   //让XXX人员库复选框自动选中
   function selectedDbpreBox(a_value)
   {
        var objs=document.getElementsByName("dbpre");
        if(objs)
        {
            for(var j=0;j<objs.length;j++)
            {
                if(objs[j].value.toUpperCase()==a_value.toUpperCase())
                {
                    objs[j].selected=true;
                }
            }
        }
   }
   
</script>