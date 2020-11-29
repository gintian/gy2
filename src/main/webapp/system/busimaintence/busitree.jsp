<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>


<%// 在标题栏显示当前用户和日期 2004-5-10 
			String userName = null;
			String css_url = "/css/css1.css";
			UserView userView = (UserView) session
					.getAttribute(WebConstant.userView);
			if (userView != null) {
				css_url = userView.getCssurl();
				if (css_url == null || css_url.equals(""))
					css_url = "/css/css1.css";
			}
%>
<script LANGUAGE=javascript src="/js/xtree.js"></script><!-- /js/codextree.js  -->
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<link href="/css/xtree.css" rel="stylesheet" type="text/css">
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/control.js"></script>
<script language="javascript" src="/ajax/dataset.js"></script>
<script language="javascript" src="/ajax/editor.js"></script>
<script language="javascript" src="/ajax/dropdown.js"></script>
<script language="javascript" src="/ajax/table.js"></script>
<script language="javascript" src="/ajax/menu.js"></script>
<script language="javascript" src="/ajax/tree.js"></script>
<script language="javascript" src="/ajax/pagepilot.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<script language="javascript" src="/js/validate.js"></script>
<script language="javascript" src="/js/codetree.js"></script>
<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script type="text/javascript" src="/js/popcalendar.js"></script>
<script type="text/javascript">
<!--
function newZj()
{
    var currnode=Global.selectedItem; 
    var id=currnode.uid;
    var arr = id.split("#");
    if(id=='root'||arr[1]=='0')
    {
       return;
    }
  var theurl = "/system/busimaintence/new_fieldset.do?b_init=init`mid="+arr[0]+"`isclose=0";
  var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
  var return_vo= window.showModalDialog(iframe_url, null, 
        "dialogWidth:500px; dialogHeight:280px;resizable:no;center:yes;scroll:yes;status:no");
  var nfieldset="#";
  
    if(return_vo)
  {
      var obj = new Object();
      obj.type=return_vo.type;
      if(obj.type=="1")
      {
          return;
       }
       obj.fieldsetid=return_vo.fieldsetid;
       obj.setdesc = return_vo.setdesc;
       obj.mid=return_vo.mid;
       obj.changeflag=return_vo.changeflag
       var hashVo=new ParameterSet();
       hashVo.setValue("setid",obj.fieldsetid);
       hashVo.setValue("setdesc", obj.setdesc);
       hashVo.setValue("mid",obj.mid);
       hashVo.setValue("changeflag",obj.changeflag);
       var In_parameters="opt=1";
      var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:save_ok,functionId:'1010060022'},hashVo);			
       
  }
  }    
function save_ok(outparameters)
{
   var currnode=Global.selectedItem;
     var setid=outparameters.getValue("setid");
     var setdesc=outparameters.getValue("setdesc");
     var imgurl = "/images/admin.gif";	 
    	 if(currnode.load)
     	 {
     	   var tmp = new xtreeItem(setid,setdesc,"/system/busimaintence/showbusifield.do?b_query=link&amp;fieldsetid="+setid+"&&amp;param=child","mil_body",setdesc,imgurl,"");
           currnode.add(tmp);
         }
         else
         	currnode.expand();
      self.parent.mil_body.location="/system/busimaintence/showbusifield.do?b_query=link&amp;fieldsetid="+setid+"&&amp;param=child";
}
function delZj()
{
    var currnode=Global.selectedItem; 
    var id=currnode.uid;
    var arr = id.split("#");
    if(id=='root'||arr[1]=='1')
    {
       return;
    }
    if(!ifdelete(currnode.text))
       return;    
    var fieldsetid = arr[0];
     var hashVo=new ParameterSet();
    hashVo.setValue("fieldsetid",fieldsetid);
    var In_parameters="opt=1";
    var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:del_ok,functionId:'1010060023'},hashVo);			
}
function del_ok(outparameters)
{
     var msg = outparameters.getValue("msg");
     if(msg=='no')
     {
        alert("该子集已经构库，不能删除");
        return;
     }
     var currnode=Global.selectedItem;
     var preitem=currnode.getPreviousSibling();
    
      var id ;
     if(preitem!=null)
     {
       preitem.select(preitem);
       id = preitem.uid.split("#");
     }
     else
     {
         id = currnode.uid.split("#");
     }
     currnode.remove();
     self.parent.mil_body.location="/system/busimaintence/showbusifield.do?b_query=link&amp;fieldsetid="+id[0]+"&&amp;param=child";
}
//-->
function add(uid,text,action,target,title,imgurl,xml){
		var currnode=Global.selectedItem;
		var tmp = new xtreeItem(uid,text,action,target,title,imgurl,xml);
		currnode.add(tmp);
		//tmp.expand();
	}
</script>
<html:form action="/system/busimaintence/busitree">
<table width="100%" border="0" cellspacing="1" align="left" cellpadding="1">

<tr>
<td>
	<table width="100%" border="0" cellspacing="1" align="left" cellpadding="1">
		<tr>
			<td align="left">
				<DIV id="treemenu">
					<SCRIPT LANGUAGE=javascript> 
              		 <bean:write name="busiMaintenceForm" property="busiTree" filter="false"/> 
              		 var currnode=Global.selectedItem;
              		 currnode.select(); 		 	
             </SCRIPT>
				</DIV>
			</td>
	</table>
	</td>
	</tr>
</table>
<script language="javascript">
  initDocument();
</script>
</html:form>
