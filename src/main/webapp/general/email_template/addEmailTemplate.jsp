<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.general.email_template.GzEmailForm,java.util.ArrayList"%>
<script language="Javascript" src="/gz/salary.js"></script>
<script language="JavaScript" src="/module/utils/js/template.js"></script>
<script language="JavaScript" src="/js/hjsjUrlEncode.js"></script>
<hrms:themes></hrms:themes>
<style type="text/css">
.mybutton{/*add by xiegh ondate 20180307 bug34370*/
	margin-left:2px;
}
</style>
<%
	GzEmailForm gzEmailForm = (GzEmailForm)session.getAttribute("gzEmailForm");
	String nmodule = gzEmailForm.getNmodule();
	String type = request.getParameter("type");
 %>
<script type="text/javascript" charset="UTF-8">
<!--
var email_array=new Array();
//"id/fieldid/fieldtitle/fieldtype/fieldcontent/dateformat/fieldlen/ndec/codeset/nflag"
//fieldtitle   公式标题或指标名称
//fieldcontent  公式内容或指标id
//ndec  小数点位数
//nflag =0是指标，=1是公式
function gzemail_save()
{
var subject=gzEmailForm.subject.value;
var content = gzEmailForm.content.value;
var id=gzEmailForm.id.value;
var itemid=gzEmailForm.address.value;

if(itemid==null||trim(itemid).length==0)
{
   alert("请选择邮件地址指标");
   return;
}
if(subject==null||subject=='')
{
   alert("请输入主题名称");
   return;
}
var oldname="${gzEmailForm.name}";
var name=prompt("请输入模板名称",(oldname==null||oldname=='')?"":getDecodeStr(oldname));
if(name==null)
{
  return;
}
if(name=='')
{
    alert("模板名称不能为空！");
   return;
}
gzEmailForm.name.value=name;
       var hashvo=new ParameterSet();
	      hashvo.setValue("email_array",email_array);
	    hashvo.setValue("itemid",itemid);
	    hashvo.setValue("name",getEncodeStr(name));
	    hashvo.setValue("subject",subject);
	    hashvo.setValue("content",getEncodeStr(content));
	    hashvo.setValue("id",id);
	    hashvo.setValue("nmodule","${gzEmailForm.nmodule}");
	   	//var In_paramters="flag=1"; 	
		var request=new Request({method:'post',asynchronous:false,onSuccess:save_ok,functionId:'0202030004'},hashvo);	

}
function save_ok(outparameters)
{
var id=outparameters.getValue("id");
gzEmailForm.action="/general/email_template/addEmailTemplate.do?b_init=init&opt=edit&nmodule=${gzEmailForm.nmodule}&templateId="+id+"&type="+<%=request.getParameter("type")%>;
gzEmailForm.submit();
}
//选择指标window 接收数据回传及关闭
function itemSelectReturn(obj){
	var itemSelectWin = Ext.getCmp("itemSelectWin");
	if(itemSelectWin){
        itemSelectWin.objlist = obj?obj:"";
        itemSelectWin.close();
	}
}
function gzemail_insertField(templateId)
{				//【6499】系统管理/应用设置/邮件模板：选中“薪资发放模板”后，点击【指标】按钮后，在打开的窗口中薪资类别中的指标显示不出来  jingq upd 2015.01.06
	var theURL="/general/email_template/insert_field.do?b_init=init`encryptParam=<%=PubFunc.encrypt("type=0&nmodule="+nmodule)%>";
	theURL = $URL.encode(theURL);
	var iframe_url="/general/email_template/iframe_gz_email.jsp?src="+theURL;
	var dw=340,dh=410,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
    Ext.create("Ext.window.Window",{
        id:"itemSelectWin",
        title:'选择指标',
        width:dw,
        height:dh,
        resizable:false,
        modal:true,
        autoScroll:false,
        autoShow:true,
        autoDestroy:true,
        renderTo:Ext.getBody(),
        html:"<iframe style='background-color:#ffffff' frameborder='0' SCROLLING=NO height='100%' width='100%' src='"+iframe_url+"'></iframe>",
        listeners:{
            close:function () {
                if(this.objlist){
                    var obj=new Object();
                    obj.fieldtitle = this.objlist.fieldtitle;
                    obj.fieldtype=this.objlist.fieldtype;
                    obj.fieldcontent=this.objlist.fieldcontent;
                    obj.dateformat=this.objlist.dateformat;
                    obj.fieldlen=this.objlist.fieldlen;
                    obj.ndec=this.objlist.ndec;
                    obj.codeset=this.objlist.codeset;
                    obj.nflag=this.objlist.nflag;
                    var id=gzEmailForm.fieldid.value;
                    var arrValue=templateId+"`"+id+"`"+obj.fieldtitle+"`"+obj.fieldtype+"`"+obj.fieldcontent+"`"+obj.dateformat+"`"+obj.fieldlen+"`"+obj.ndec+"`"+obj.codeset+"`"+obj.nflag;
                    email_array.push(getEncodeStr(arrValue));
                    insertTxt("1",obj.fieldtitle,"content",id);
                    gzEmailForm.fieldid.value=parseInt(id)+1;
                }
            }
        }
    });
}
//公式window数据回传及关闭
function formulaReturn(objlist){
    var formulaWin = Ext.getCmp("formulaWin")?Ext.getCmp("formulaWin"):Ext.getCmp("modifyFormulaWin");
    if(formulaWin){
        formulaWin.objlist = objlist?objlist:"";
        formulaWin.close();
    }
}
function gzemail_insertFormula(templateId)
{
	//52443 IE浏览器使用Ext的window中有输入项时，获取当前页面的selection.createRange()插入内容位置错误。此处在操作之前先保存一下range对象，插入时使用此range解决 guodd 2019-09-24
	$('content').focus();
    var range = document.selection && getBrowseVersion()?document.selection.createRange():null;
	var id=gzEmailForm.fieldid.value;
	var theURL="/general/email_template/insert_formula.do?b_init=init`opt=0`maxid="+id+"`nmodule=${gzEmailForm.nmodule}";
	theURL = $URL.encode(theURL);
	var iframe_url="/general/email_template/iframe_gz_email.jsp?src="+theURL;
	var dw=650,dh=620,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
	Ext.create("Ext.window.Window",{
        id:"formulaWin",
        title:'设置公式',
        width:dw,
        height:dh,
        resizable:false,
        modal:true,
        autoScroll:false,
        autoShow:true,
        autoDestroy:true,
        renderTo:Ext.getBody(),
        html:"<iframe style='background-color:#ffffff' frameborder='0' SCROLLING=NO height='100%' width='100%' src='"+iframe_url+"'></iframe>",
        listeners:{
            close:function () {
				if(this.objlist){
                    var obj=new Object();
                    obj.fieldtitle = this.objlist.fieldtitle;
                    obj.fieldtype=this.objlist.fieldtype;
                    obj.fieldcontent=this.objlist.fieldcontent;
                    obj.dateformat=this.objlist.dateformat;
                    obj.fieldlen=this.objlist.fieldlen;
                    obj.ndec=this.objlist.ndec;
                    obj.codeset=this.objlist.codeset;
                    obj.nflag=this.objlist.nflag;
                    var arrValue=templateId+"`"+id+"`"+obj.fieldtitle+"`"+obj.fieldtype+"`"+obj.fieldcontent+"`"+obj.dateformat+"`"+obj.fieldlen+"`"+obj.ndec+"`"+obj.codeset+"`"+obj.nflag;
                    email_array.push(getEncodeStr(arrValue));
                    //52443 操作完成后，传入事先保存的range对象 guodd 2019-09-24
                    insertTxt("3",obj.fieldtitle,'content',id,range);
                    gzEmailForm.fieldid.value=parseInt(id)+1;
				}
            }
        }
	});
}
//选择附件 window回调关闭
function selectAttachReturn(){
    var operateTarget = Ext.getCmp("attachWin");
	if(operateTarget){
        operateTarget.close();
	}
}
function gzemail_takeboatAttach()
{
 var theURL = "/general/email_template/takeboat_attach.do?b_init=init";
 theURL = $URL.encode(theURL);
 var iframe_url="/general/email_template/iframe_gz_email.jsp?src="+theURL;
 var dw=500,dh=470,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
    Ext.create("Ext.window.Window",{
        id:"attachWin",
        title:'附件',
        width:dw,
        height:dh,
        resizable:false,
        modal:true,
        autoScroll:false,
        autoShow:true,
        autoDestroy:true,
        renderTo:Ext.getBody(),
        html:"<iframe style='background-color:#ffffff' frameborder='0' SCROLLING=NO height='100%' width='100%' src='"+iframe_url+"'></iframe>"
    });

 // var objlist =window.showModalDialog(iframe_url,null,"dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable=yes;status=no;scroll:no;");
 // if(objlist==null)
 // return;
// window.open(theURL);
}
  function initFieldArray(content)
  { 
   gzEmailForm.content.value=getDecodeStr(content);
 <%int i=0;%>
  <logic:iterate name="gzEmailForm" property="fieldList" id="element" offset="0">
   var arrVal<%=i%>='<bean:write name="element" property="con"/>';

   email_array.push(arrVal<%=i%>);
   <% i++;%>
    </logic:iterate>
  }
  //发送到window数据接收及关闭
  function emailSelectReturn(objlist){
      var emailSelectWin = Ext.getCmp("emailSelectWin");
      if(emailSelectWin){
          emailSelectWin.objlist = objlist?objlist:"";
          emailSelectWin.close();
      }
  }
  function email_select_email_field(fieldsetid,itemid)
  {
     var theURL="/general/email_template/select_email_field.do?b_query=link`fieldsetid="+fieldsetid+"`itemid="+itemid;
     theURL = $URL.encode(theURL);
    var url="/general/email_template/iframe_gz_email.jsp?src="+theURL;
    var dw=350,dh=460,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
      Ext.create("Ext.window.Window",{
		id:"emailSelectWin",
		title:'发送到',
		width:dw,
		height:dh,
		resizable:false,
		modal:true,
		autoScroll:false,
		autoShow:true,
		autoDestroy:true,
		renderTo:Ext.getBody(),
		html:"<iframe style='background-color:#ffffff' frameborder='0' SCROLLING=NO height='100%' width='100%' src='"+url+"'></iframe>",
		  listeners:{
		    close:function () {
                if(this.objlist){
                    var obj=new Object();
                    obj.value=this.objlist.value;
                    obj.text=this.objlist.text;
                    gzEmailForm.fieldname.value=obj.text;
                    gzEmailForm.address.value=obj.value+":"+obj.text;
                }
            }
		  }
	  });
  }
  
  function editF(templateId)
  {
      var selecttext;
	  if(getBrowseVersion()){
          selecttext = document.selection.createRange().text;//htmlText
	  }else{//兼容非ie浏览器 wangbs 20190320
          selecttext = window.getSelection().toString();
	  }
    if(selecttext.indexOf("#")==-1||selecttext.length<=1||selecttext.indexOf(":")==-1)
    {
       alert("请选择公式进行修改！");
       return;
    }
    if(selecttext.indexOf("##")!=-1||selecttext.indexOf(":")<selecttext.indexOf("#"))
    {
      alert("请按正确方式选择公式，正确方式为选中 [#+公式序号+冒号+名称+#] 内容！");
      return;
    }
    var formulaid=selecttext.substring(selecttext.indexOf("#")+1,selecttext.indexOf(":"));
    //templateId为空，修改新建模板的公式  jingq add 2014.10.11
    //选中的公式验证是否是新增加的，新增的从缓存中取数；不是从数据库取公式数据 15556  wangb 20170526
    var newcontent;
 	//公式转化
    for(var i=0;i<email_array.length;i++){
    	var str = getDecodeStr(email_array[i]).split("`");
    	if(str[1]==formulaid){
    		flag=true;
    		/*直接加密状态传输，不解密了，交给后台解密。否则回车符会丢失 guodd 2018-03-30*/
    		newcontent = email_array[i];
    		//newcontent = getDecodeStr(email_array[i]);//取得缓存的公式信息
    		//newcontent = newcontent.replace(/`/g,",");//将`转为,否则数据传递不到后台
    		break;
    	}
    }
    if(newcontent){
    	var theURL="/general/email_template/insert_formula.do?b_init=init`opt=newcontent`newcontent="+newcontent;
    }else{
    	var theURL="/general/email_template/insert_formula.do?b_init=init`opt=1`maxid="+formulaid+"`tid="+templateId;
    }
    var iframe_url="/general/email_template/iframe_gz_email.jsp?src="+$URL.encode(theURL);
    if(getBrowseVersion()){
    	var objlist =window.showModalDialog(iframe_url,null,"dialogWidth=580px;dialogHeight=620px;resizable=yes;status=no;scroll:no;"); 
		if(objlist==null)
			return;
		var obj=new Object();
		obj.fieldtitle = objlist.fieldtitle;
		obj.fieldtype=objlist.fieldtype;
		obj.fieldcontent=objlist.fieldcontent;
		obj.dateformat=objlist.dateformat;
		obj.fieldlen=objlist.fieldlen;
		obj.ndec=objlist.ndec;
		obj.codeset=objlist.codeset;
		obj.nflag=objlist.nflag;
		var arrValue=templateId+"`"+formulaid+"`"+obj.fieldtitle+"`"+obj.fieldtype+"`"+obj.fieldcontent+"`"+obj.dateformat+"`"+obj.fieldlen+"`"+obj.ndec+"`"+obj.codeset+"`"+obj.nflag;
		for(var i=0;i<email_array.length;i++)
		{
	   		if(email_array[i]==null)
	     		continue;
	   		var temp = getDecodeStr(email_array[i]).split("`");
	   		if(parseInt(temp[1])==parseInt(formulaid))
	   		{
	      		email_array[i]= "";
	      		break;
	   		}
		}
		email_array.push(getEncodeStr(arrValue));
		insertTxt("3",obj.fieldtitle,'content',formulaid); 
    }else{
	  Ext.create("Ext.window.Window",{
		id:"modifyFormulaWin",
		title:'公式修改',
		width:650,
		height:670,
		resizable:false,
		modal:true,
		autoScroll:false,
		autoShow:true,
		autoDestroy:true,
		renderTo:Ext.getBody(),
		html:"<iframe style='background-color:#ffffff' frameborder='0' SCROLLING=NO height='100%' width='100%' src='"+iframe_url+"'></iframe>",
		  listeners:{
		    close:function () {
		        if(this.objlist){
                    var obj=new Object();
                    obj.fieldtitle = this.objlist.fieldtitle;
                    obj.fieldtype=this.objlist.fieldtype;
                    obj.fieldcontent=this.objlist.fieldcontent;
                    obj.dateformat=this.objlist.dateformat;
                    obj.fieldlen=this.objlist.fieldlen;
                    obj.ndec=this.objlist.ndec;
                    obj.codeset=this.objlist.codeset;
                    obj.nflag=this.objlist.nflag;
                    var arrValue=templateId+"`"+formulaid+"`"+obj.fieldtitle+"`"+obj.fieldtype+"`"+obj.fieldcontent+"`"+obj.dateformat+"`"+obj.fieldlen+"`"+obj.ndec+"`"+obj.codeset+"`"+obj.nflag;
                    for(var i=0;i<email_array.length;i++)
                    {
                        if(email_array[i]==null)
                            continue;
                        var temp = getDecodeStr(email_array[i]).split("`");
                        if(parseInt(temp[1])==parseInt(formulaid))
                        {
                            email_array[i]= "";
                            break;
                        }
                    }
                    email_array.push(getEncodeStr(arrValue));
                    insertTxt("3",obj.fieldtitle,'content',formulaid);
				}
            }
		  }
	  	});
	  }
  }
  function delete_content(obj)
  {
       obj.focus();
 
       var code=window.event.keyCode;
    // alert(code);
    if ((window.event.ctrlKey)&&(code==67))
    {
    }
    //xus Ctrl+V粘贴 清除选中
    else if((window.event.ctrlKey)&&(code==86)){
    }
    else if(code!=17)
    {
       if(code==8||code==46)//两个删除键键值 $12:6666$field/#23:888#formula
       {	
       	  var selecttext;
       	  if(document.selection)
          	selecttext=document.selection.createRange().text;//htmlText
          else
          	selecttext = window.getSelection().toString();
          if(selecttext!=null&&trim(selecttext).length>0&&(selecttext.indexOf("$")!=-1||selecttext.indexOf("#")!=-1))
          {
             obj.focus();
             var alltext=gzEmailForm.content.value;
             if(alltext==selecttext)
             {
                  for(var i=0;i<email_array.length;i++)
                  {
                      email_array[i]="";
                  }
             }
            else if(selecttext.indexOf("$")!=-1)//删除指标
            { 
                obj.focus();
                var first=selecttext.indexOf("$");
                var last=selecttext.lastIndexOf("$");
                if(first==last)//必须包含两个$
                {
                    window.event.returnValue=false;
                }
                else
                {
                    var text=selecttext.substring(first+1,last);
                    if(text.indexOf("$")!=-1)
                    {
                        window.event.returnValue=false;
                    }
                    else
                    {
                         if(text.indexOf(":")==-1)//必须包含:
                         {
                            window.event.returnValue=false;
                         }
                         else
                         {
                            var f=text.indexOf(":");
                            var l=text.lastIndexOf(":");
                            if(f!=l)//只能包含一个:
                            {
                               window.event.returnValue=false;
                            }
                            else
                            { 
                                 
                                  var num=text.substring(0,text.indexOf(":"));//得到指标序号
                                  
                                  if(!isNaN(num))
                                  {
                                  	
                                      var flag=false;
                                      var index=0;
                                      for(var i=0;i<email_array.length;i++)
                                      {
               
                                         if(email_array[i]==null)
                                            continue;
                                         var t = getDecodeStr(email_array[i]);
                                         var temp=t.split("`");
                                             if(parseInt(temp[1])==parseInt(num))
                                             {
                                               flag=true;
                                               index=i;
                                               break;
                                             }
                                        
                                      }
                                      
                                      if(flag)
                                      {
                                          //document.selection.createRange().text=""; 选中后按退格键 在IE浏览器中当做返回键使用 15558 wangb 20170531
                                          email_array[index]="";
                                      }
                                  }
                                  else
                                  {
                                      window.event.returnValue=false;
                                  }
                               
                            }
                         }
                    }
                }
            }else 
            if(selecttext.indexOf("#")!=-1)//删除公式
            {
               obj.focus();
               var first=selecttext.indexOf("#");
                var last=selecttext.lastIndexOf("#");
                if(first==last)//必须包含两个$
                {
                    window.event.returnValue=false;
                }
                else
                {
                    var text=selecttext.substring(first+1,last);
                    if(text.indexOf("#")!=-1)
                    {
                        window.event.returnValue=false;
                    }
                    else
                    {
                         if(text.indexOf(":")==-1)//必须包含:
                         {
                            window.event.returnValue=false;
                         }
                         else
                         {
                            var f=text.indexOf(":");
                            var l=text.lastIndexOf(":");
                            if(f!=l)//只能包含一个:
                            {
                               window.event.returnValue=false;
                            }
                            else
                            { 
                                  var num=text.substring(0,text.indexOf(":"));//得到指标序号
                                  if(!isNaN(num))
                                  {
                                      var flag=false;
                                      var index=0;
                                      for(var i=0;i<email_array.length;i++)
                                      {
                                         if(email_array[i]==null)
                                            continue;
                                         var temp = email_array[i].split("`");
                                         for(var j=0;j<temp.length;j++)
                                         {
                                             if(parseInt(temp[1])==parseInt(num))
                                             {
                                               flag=true;
                                               index=i;
                                               break;
                                             }
                                         }
                                      }
                                      if(flag)
                                      {
                                          //document.selection.createRange().text=""; 选中后按退格键 在IE浏览器中当做返回键使用 15558 wangb 20170531
                                          email_array[index]="";
                                      }
                                  }
                                  else
                                  {
                                      window.event.returnValue=false;
                                  }
                               
                            }
                         }
                    }
                }
            }
           // else//鼠标选择内容没有$/#
            //{
           // }
          }
          else if(selecttext!=null&&trim(selecttext).length>0)
          {
              
          }
          else//没有用鼠标选择内容
          {
          	var str;
          	if(document.selection){
          		var r=document.selection.createRange();
                var t = obj.createTextRange()
                t.collapse(true);//将光标移到头
                t.select();
                var j = document.selection.createRange();
                r.setEndPoint("StartToEnd",j);//
                str = r.text;
                if(!str)
              	  str = obj.value.substring(0,1);
          	}else{
          		str = window.getSelection().toString();
          		if(!str)
          			str = obj.value.substring(0,obj.selectionStart);
          		if(obj.selectionStart === 0)
          			str = obj.value.substring(0,obj.selectionStart+1);
          	}
             
              var re = new RegExp("[\\n]","g");
               str = str.replace(re,"");//过滤
                var val=gzEmailForm.content.value;
                var re = new RegExp("[\\n]","g");
                val=val.replace(re,"");
                var temp=val.substring(str.length);
               if(str!=null&&trim(str).length>0)
               {
               if(str.charAt(str.length-1)=="$"||str.charAt(str.length-1)=="#")
               {
                   if(str=="#"||str=="$")
                   {
                      if(temp.indexOf(":")!=-1)
                      {
                        var isnan=temp.substring(0,temp.indexOf(":"));
                        if(!isNaN(isnan))
                        {
                          window.event.returnValue=false;
                        }
                      }
                   }
                   else if(str.charAt(str.length-1)=="$")
                   {
                     var before=str.substring(0,str.length-2);
                     var lastindex=before.lastIndexOf("$");
                     var zjstr=before.substring(lastindex);
                     if(zjstr.indexOf(":")!=-1)
                     {
                       var isnan=zjstr.substring(1,zjstr.indexOf(":"));
                       if(!isNaN(isnan))
                          window.event.returnValue=false;
                     }
                     if(temp.indexOf(":")!=-1)
                      {
                        var isnan=temp.substring(0,temp.indexOf(":"));
                        if(!isNaN(isnan))
                        {
                          window.event.returnValue=false;
                        }
                      }
                   }
                   else if(str.charAt(str.length-1)=="#")
                   {
                  
                     var before=str.substring(0,str.length-2);
                     var lastindex=before.lastIndexOf("#");
                     var zjstr=before.substring(lastindex);
                     if(zjstr.indexOf(":")!=-1)
                     {
                       var isnan=zjstr.substring(1,zjstr.indexOf(":"));
                       if(!isNaN(isnan))
                          window.event.returnValue=false;
                     }
                     if(temp.indexOf(":")!=-1)
                      {
                        var isnan=temp.substring(0,temp.indexOf(":"));
                        if(!isNaN(isnan))
                        {
                          window.event.returnValue=false;
                        }
                      }
                   }
                   //window.event.returnValue=false;
               }
               if(str.charAt(str.length-1)==":")
               {
                   if(str.charAt(str.length-2)!=' ')
                   {
                   if(!isNaN(str.charAt(str.length-2)))
                      window.event.returnValue=false;
                    }
                }
               if(!isNaN(str.charAt(str.length-1)))
                {   if(!isNaN(str.charAt(str.length-2)))
                   {
                      if((str.charAt(str.length-3)=="$"||str.charAt(str.length-3)=="#")&&temp.charAt(0)==":")
                      {
                     // alert("1");
                         window.event.returnValue=false;
                      }
                      if((str.charAt(str.length-2)=="$"||str.charAt(str.length-2)=="#")&&temp.charAt(0)==":")
                      {
                     //    alert("2");
                         window.event.returnValue=false;
                      }
                   }
                   if(str.charAt(str.length-2)=="$"||str.charAt(str.length-2)=="#")
                    {
                  //  alert("3");
                        window.event.returnValue=false;
                        }
                }

               if(code==46)
               {
                  var val=gzEmailForm.content.value;
                  var re = new RegExp("[\\n]","g");
                  val=val.replace(re,"");
                  var temp=val.substring(str.length);
                  var index=0;
                  if(document.selection){
                  	var r=document.selection.createRange();
                	var t = obj.createTextRange()
                	t.collapse(true);//将光标移到头
                	t.select();
                	var j = document.selection.createRange();
                	r.setEndPoint("StartToEnd",j);//
                	if(rs.next)
                  		index =rs.next.lenfth; 
                  }else{
                  	index = obj.selectionStart;
                  }
                  if(index === 0){
					  if(str=="#"||str=="$")
            	        window.event.returnValue=false;
                  }else{
	                  if(temp.charAt(0)=="#"||temp.charAt(0)=="$")
            	        window.event.returnValue=false;
                  }
               }
              if(str.indexOf("$")!=-1&&str.indexOf("#")!=-1)
              {
                 var dindex=str.lastIndexOf("$");
                 var jindex=str.lastIndexOf("#");
                 if(dindex>jindex)
                 {
                      var l=str.lastIndexOf("$");
                      if(str.indexOf(":")!=-1)
                       {
                          var ml=str.lastIndexOf(":");
                          if(ml>l)
                          {
                            var temp=str.substring(l+1,ml)
                           if(!(isNaN(temp)))
                           {
                          //   alert("5");
                             window.event.returnValue=false;
                           }
                         }
                       }
                 }else
                 {
                      var l=str.lastIndexOf("#");
                      if(str.indexOf(":")!=-1)
                      {
                        var ml=str.lastIndexOf(":");
                        if(ml>l)
                         {
                           var temp=str.substring(l+1,ml)
                            if(temp.length>0)
                            {
                           if(!(isNaN(temp)))
                           {
                           //   alert("6");
                              window.event.returnValue=false;
                            }
                        
                            }
                         }else
                         {
                         	if(document.selection){
                              r.collapse(false);//将光标移到头
                              r.select();
                              document.selection.createRange().text="";
                         	}
                         }
                     }else
                     {
						if(document.selection)                     
                        	document.selection.createRange().text="";
                     }
                 }
              }
             else if(str.indexOf("$")!=-1&&str.indexOf("#")==-1)
              {
                  var l=str.lastIndexOf("$");
                  if(str.indexOf(":")!=-1)
                  {
                     var ml=str.lastIndexOf(":");
                     if(ml>l)
                     {
                        var temp=str.substring(l+1,ml)
                        if(!(isNaN(temp)))
                        {
                         // alert("7");
                          window.event.returnValue=false;
                        }
                     }
                  }
              }
              else if(str.indexOf("#")!=-1&&str.indexOf("$")==-1)
              {
                  var l=str.lastIndexOf("#");
                  if(str.indexOf(":")!=-1)
                  {
                     var ml=str.lastIndexOf(":");
                     if(ml>l)
                     {
                        var temp=str.substring(l+1,ml)
                        if(!(isNaN(temp)))
                        {
                         // alert("8");
                          window.event.returnValue=false;
                        }
                     }
                  }
              }
              else
              {
              	   if(document.selection)
                   	 document.selection.createRange().text="";
              }
              if(document.selection){
	              r.collapse(false);//将光标移到头
	              r.select();
              }           
            }
            }
             
         
     }else//其他键
     {
     		  var str;
              if(document.selection){
              	var r=document.selection.createRange();
              	var t = obj.createTextRange()
              	t.collapse(true);//将光标移到头
              	t.select();
              	var j = document.selection.createRange();
              	r.setEndPoint("StartToEnd",j);//
              	str = r.text;
              }else{
                str = window.getSelection().toString();
              	str = obj.value.substring(0,obj.selectionStart);
              }
              var re = new RegExp("[\\n]","g");
               str = str.replace(re,"");//过滤
              if(str.charAt(str.length-1)==":")
              {
             // alert("1");
                if(str.indexOf("$")!=-1||str.indexOf("#")!=-1)
                {
                   var dindex=0;
                   var jindex=0;
                   var fh="";
                   if(str.lastIndexOf("$")!=-1)
                      dindex=str.lastIndexOf("$");
                   if(str.lastIndexOf("#")!=-1)
                      jindex=str.lastIndexOf("#");
                   if(dindex>jindex)
                   {
                       fh="$";
                   }
                   else
                   {
                       fh="#";
                   }
                   if(!isNaN(str.substring(str.lastIndexOf(fh)+1,str.indexOf(":"))))
                   {

                          window.event.returnValue=false;
                   }
                   else
                   {
                   }
                 }
              }

                else if(str.indexOf("$")!=-1&&str.indexOf("#")!=-1)
              {
                 var dindex=str.lastIndexOf("$");
                 var jindex=str.lastIndexOf("#");
                 if(str.charAt(str.length-1)=="$"||str.charAt(str.length-1)=="#")
                 {
                     var val=gzEmailForm.content.value;
                     var re = new RegExp("[\\n]","g");
                     val=val.replace(re,"");
                     var temp=val.substring(str.length);
                     if(temp.indexOf(":")!=-1)
                     {
                          var num=temp.indexOf(":");
                          var isnum=temp.substring(0,num);
                          if(trim(isnum).length!=0)
                          {
                          if(!isNaN(isnum))
                          {
                              //alert("1");
                               window.event.returnValue=false;
                          }
                          }
                     }
                 }
                 else if(dindex>jindex)
                 {
                      var l=str.lastIndexOf("$");
                      if(trim(str.substring(l+1)).length!=0)
                      {
                      if(!isNaN(str.substring(l+1)))
                         window.event.returnValue=false;
                       }
                      if(str.indexOf(":")!=-1)
                       {
                          var ml=str.lastIndexOf(":");
                          if(ml>l)
                          {
                            var temp=str.substring(l+1,ml)
                            if(trim(temp).length!=0)
                            {
                           if(!(isNaN(temp)))
                           {
                             window.event.returnValue=false;
                           }
                           }
                         }
                       }
                 }else
                 {
                      var l=str.lastIndexOf("#");
                      if(!isNaN(str.substring(l+1)))
                         window.event.returnValue=false;
                      if(str.indexOf(":")!=-1)
                      {
                        var ml=str.lastIndexOf(":");
                        if(ml>l)
                         {
                           var temp=str.substring(l+1,ml)
                           if(trim(temp).length!=0)
                           {
                           if(!(isNaN(temp)))
                           {
                            //alert("3");
                              window.event.returnValue=false;
                            }
                            else
                            {
                              //document.selection.createRange().text="";
                            }
                            }
                         }else
                         {
                         	 if(document.selection){
	                             r.collapse(false);//将光标移到头
    	                         r.select();
                         	 }
                            //document.selection.createRange().text="";
                         }
                     }else
                     {
                        //document.selection.createRange().text="";
                     }
                 }
              }
             else if(str.indexOf("$")!=-1&&str.indexOf("#")==-1)
              {
             // alert("3");
                  var l=str.lastIndexOf("$");
                  if(str.indexOf(":")!=-1)
                  {
                     var ml=str.lastIndexOf(":");
                     if(ml>l)
                     {
                        var temp=str.substring(l+1,ml)
                        if(!(trim(temp).length==0))
                        {
                        if(!(isNaN(temp)))
                        {
                          window.event.returnValue=false;
                        }
                        }
                     }
                  }
              }
              else if(str.indexOf("#")!=-1&&str.indexOf("$")==-1)
              {
              //alert("4");
                  var l=str.lastIndexOf("#");
                  if(str.indexOf(":")!=-1)
                  {
                     var ml=str.lastIndexOf(":");
                     if(ml>l)
                     {
                        var temp=str.substring(l+1,ml)
                        if(trim(temp).length!=0)
                        {
                        if(!(isNaN(temp)))
                        {
                          window.event.returnValue=false;
                        }
                        }
                     }
                  }
              }
              else
              {
              }
			  	if(document.selection){
    	          r.collapse(false);//将光标回到原位
	              r.select();
			  	}else{
			  	 // obj.setSelectionRange(obj.selectionStart,obj.selectionEnd);
			  	 //	obj.value = obj.value.substring(0,obj.selectionStart)+ String.fromCharCode(code) + obj.value.substring(obj.selectionStart,obj.selectionEnd+1);
			  	}
              }
     }
  }
function sendemail_deleteTemplate()
{
var id="";
var text="";
var num=0;
var setobj=$('templateId');
if(setobj.options.length==0)
return;
for(var i=0;i<setobj.options.length;i++)
{
  if(setobj.options[i].selected)
  {
     num++;
     id=setobj.options[i].value;
     text+="["+setobj.options[i].text+"] ";
  }
}
if(num==0)
{
  alert("请选择要删除的模板");
  return;
}
if(confirm("确认删除"+":"+text)){
 var hashVo=new ParameterSet();
 hashVo.setValue("templateId",id)
 var In_parameters="opt=1";
 var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:delete_ok,functionId:'0202030021'},hashVo);			
}else
{
return;
}
}
function delete_ok(outparameters)
{
 gzEmailForm.action="/general/email_template/addEmailTemplate.do?b_init=init&encryptParam=<%=PubFunc.encrypt("nmodule="+nmodule+"&opt=edit&templateId=first&type="+type)%>";
 gzEmailForm.submit();
}
function gzemail_addOredit()
{
 var obj=$("templateId");
 var id;
 for(var i=0;i<obj.options.length;i++)
  {
    if(obj.options[i].selected)
    {
        id=obj.options[i].value;
    }
  }
  if(id==null||trim(id).length==0)
    return;
  if(parseInt(id)==0) 
  {
     gzEmailForm.action="/general/email_template/addEmailTemplate.do?b_init=init&encryptParam=<%=PubFunc.encrypt("nmodule="+nmodule+"&opt=add&templateId=new&type="+type)%>";
      gzEmailForm.submit(); 
  }
   else
   {
     gzEmailForm.action="/general/email_template/addEmailTemplate.do?b_init=init&opt=edit&nmodule=${gzEmailForm.nmodule}&templateId="+id+"&type="+<%=request.getParameter("type")%>;
     gzEmailForm.submit();
   }
}
function newtemplate()
{
   var obj=$("templateId");
   var id;
   for(var i=0;i<obj.options.length;i++)
   {
    if(parseInt(obj.options[i].value)==0)
    {
        obj.options[i].selected=true;
    }
  }
  gzEmailForm.action="/general/email_template/addEmailTemplate.do?b_init=init&encryptParam=<%=PubFunc.encrypt("nmodule="+nmodule+"&opt=add&templateId=new&type="+type)%>";
  gzEmailForm.submit(); 
  
}
function gzemail_clear(id)
{
   if(confirm("确认清空邮件主体内容，该操作将删除已定义好的指标和公式！"))
   {
        gzEmailForm.content.value="";
        var hashvo=new ParameterSet();
    	hashvo.setValue("id",id);
        var request=new Request({method:'post',asynchronous:false,onSuccess:clear_ok,functionId:'0202030023'},hashvo);	
    } 
}
function clear_ok(outparameters)
{
   
}
function getConstant()
{
 var theURL = "/general/email_template/constant_list.do?b_query=init";
 var iframe_url="/general/email_template/iframe_gz_email.jsp?src="+theURL;
 var dw=302,dh=300,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;

	var data={
		id:'constant_window',
		width:320,
		height:310,
		type:'3',
		title:'常量列表'
	};
    modalDialog.showModalDialogs(iframe_url,"",data);

}

function getConstant_ok(objlist) {
    Ext.getCmp('constant_window').close();
    if(objlist==null)
        return;
    else
    {

        var obj=new Object();
        obj.txt=objlist.txt;
        insertTxt('4','#'+objlist.txt+'#','content')
//        var expr_editor=$("content");
//        expr_editor.focus();
//        var element = document.selection;
//        if (element!=null)
//        {
//            var rge = element.createRange();
//            if (rge!=null)
//                rge.text=;
//        }
    }
}
//-->
</script>
<html:form action="/general/email_template/addEmailTemplate">
<table width='740px;' border="0" cellspacing="0"  align="center" cellpadding="0">
<tr>
<td>
<fieldset align="center">
<legend>邮件模板</legend>
<table width='100%' border="0" cellspacing="0"  align="center" cellpadding="0">
<tr>
<td width="40%" align="left" colspan="1" nowrap style="padding-left:5px;padding-bottom:5px;">
选择模板
 <html:select name="gzEmailForm" size="1" property="templateId" onchange="gzemail_addOredit();">
		              <html:optionsCollection property="templateList" value="dataValue" label="dataName"/>
		        </html:select>	
<input type="button" class="mybutton" value="新建" name="new" onclick="newtemplate();"/>		        
<input type="button" class="mybutton" value="删除" name="del" onclick="sendemail_deleteTemplate();"/>
</td>
</tr>
</table>
</fieldset>
</td>
</tr>
<tr>
<td>
<fieldset align="center" style="margin-top:10px;">
<LEGEND>设置邮件模板</LEGEND>
<table width='100%' border="0" cellspacing="0"  align="center" cellpadding="0">
<tr>
<td width="30%" align="center" nowrap style="padding-left:5px;">
发&nbsp;送&nbsp;到&nbsp;&nbsp;
</td>
<td width="70%" align="left" colspan="2" nowrap height="30px;">
<html:text name="gzEmailForm" property="fieldname" styleClass="text4 common_border_color" style="width:300px;float:left"></html:text>
 <input type="button" style="float:left" class="mybutton" name="sel" value="..." onclick="email_select_email_field('${gzEmailForm.fieldsetid}','${gzEmailForm.itemid}');"/>
	<%-- 提示信息没有居中显示 添加样式line-height:20px;   bug 36316 wangb 20180423 --%>
	<logic:equal value="1" name="gzEmailForm" property="message"><font style="float:left;line-height:20px;" color="red">模板邮件地址指标无效(未构库或已删除)</font></logic:equal>	    
</td>
</tr>
<tr>
<td width="30%" align="center" nowrap>
主&nbsp;&nbsp;&nbsp;&nbsp;题&nbsp;&nbsp;
</td>
<td align="left" width="70%" colspan="2" nowrap>
<html:text name="gzEmailForm" property="subject" size="100" style="width:620px;" styleClass="text4 common_border_color"></html:text>
</td>
</tr>
<tr>
<td width="30%" align="center" height="200">
主&nbsp;&nbsp;&nbsp;&nbsp;体&nbsp;&nbsp;
</td>

<td width="70%" align="left" style="padding-top:5px;padding-bottom:5px;">
<%if(request.getParameter("type")==null||request.getParameter("type").equals("0")){ %>
	<%--兼容ie 样式属性加px wangbs 20190320--%>
<html:textarea name="gzEmailForm" property="content" onkeydown="delete_content(this);"  style="width:620px;height:450px;"/>
<%} else{ %>
<html:textarea name="gzEmailForm" property="content" onkeydown="delete_content(this);"  style="width:620px;height:450px;" />
<%} %>
</td>
<TD>
<table width='100%' border="0" cellspacing="0"  align="left" cellpadding="0">
<logic:notEqual name="gzEmailForm" property="nmodule" value="5">
<tr>
<td align="center" nowrap>
<input type="button" name="field" class="mybutton" value="指标" onclick="gzemail_insertField('${gzEmailForm.id}');"/>
</td>
</tr>
<tr>
<td align="center" nowrap style="padding-top:10px;">
<input type="button" name="formula" value="公式" class="mybutton" onclick="gzemail_insertFormula('${gzEmailForm.id}');"/>
</td>
</tr>
<tr>
<td align="center" nowrap style="padding-top:10px;">
<input type="button" name="edit" class="mybutton" value="修改" onclick="editF('${gzEmailForm.id}');"/>
</td>
</tr>
<tr>
<td align="center" nowrap style="padding-top:10px;">
<logic:equal name="gzEmailForm" property="nflag" value="1">
<input type="button" name="attach" value="附件" class="mybutton" onclick="gzemail_takeboatAttach();"/>
</logic:equal>
</td>
</tr>
</logic:notEqual>
<logic:equal name="gzEmailForm" property="nmodule" value="5">
<tr>
<td align="center" nowrap style="padding-top:10px;">
<input type="button" name="clear" value="常量" class="mybutton" onclick="getConstant();"/>
</td>
</tr>
</logic:equal>
<tr>
<td align="center" nowrap style="padding-top:10px;">
<input type="button" name="clear" value="清空" class="mybutton" onclick="gzemail_clear('${gzEmailForm.id}');"/>
</td>
</tr>
</table>
</TD>
</tr>
</table>
</fieldset>
</td>
</tr>
<tr>
<td>
<table width='100%' border="0" cellspacing="0"  align="left" cellpadding="0">
<tr>
<td align="center" colspan="3" nowrap height="35px;">
<input type="button" name="save" value="保存" class="mybutton" onclick="gzemail_save();"/>
<%if(request.getParameter("type")==null||request.getParameter("type").equals("0")){ %>
<input type="button" name="clo" value="关闭" class="mybutton" onclick="window.close();"/>
<%} %>
<input type="hidden" name="name" value=""/>
<input type="hidden" name="fieldid" value="${gzEmailForm.fieldid}"/>
<html:hidden name="gzEmailForm" property="id"/>
<html:hidden name="gzEmailForm" property="address"/>
<html:hidden name="gzEmailForm" property="nmodule"/>
</td>
</tr>
</table>
</td>
</tr>
</table>
</html:form>
<script type="text/javascript">
<!--
initFieldArray("${gzEmailForm.content}");
//-->
//当邮件模板都删除掉时，会出现一个空白的模板删除报错，应该去掉   wangb 20180129  32315
var templateId = document.getElementsByName('templateId')[0];
for(var i=0;i<templateId.options.length;i++){
	if(!templateId.options[i].value || templateId.options[i].value.length==0){
		templateId.options.remove(i);
	}
}
</script>