<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<script LANGUAGE=javascript src="/js/function.js"></script>
<script type="text/javascript">
<!--
var beforeitemid="-1";
 //是否已有项目=0有=1没有
var isHaveItem="${standardItemForm.isHaveItem}";
var isUsed="${standardItemForm.isUsed}";
//opt=0增加同级=1增加下级=2插入=3编辑
var opt_='';
function addStandardItem(opt)
    {
    opt_=opt;
    if(opt==1||opt==2||opt==3||(opt==0&&isHaveItem=='0'))
    {
    if(beforeitemid=='-1')
    {
    alert("请选择一个项目后，再进行该操作");
    return;
    }
    }
    var theurl="/performance/achivement/standarditem/search_standarditem_list.do?b_add=addstandard`ruletype=1`opt="+opt+"`itemid="+beforeitemid;
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
    /*var return_vo= window.showModalDialog(iframe_url, arguments,
    "dialogWidth:400px; dialogHeight:210px;resizable:no;center:yes;scroll:no;status:no");*/
    var config = {
    width:480,
    height:210,
    type:'2'
    }
    modalDialog.showModalDialogs(iframe_url,"addStandardItemWin",config,addStandardItem_ok)

    }
    function addStandardItem_ok(return_vo){
        if(return_vo)
        {
            var obj=new Object();
            obj.desc=return_vo.desc;
            obj.ruletype=return_vo.ruletype;
            var point_id=standardItemForm.point_id.value;
            var hashvo=new ParameterSet();
            hashvo.setValue("opt",opt_);
            hashvo.setValue("desc",getEncodeStr(obj.desc));
            hashvo.setValue("beforeitemid",beforeitemid);
            hashvo.setValue("isHaveItem",isHaveItem);
            hashvo.setValue("point_id",point_id);
            hashvo.setValue("model","standard");
            hashvo.setValue("ruletype",obj.ruletype);
            var request=new Request({asynchronous:false,onSuccess:save_ok,functionId:'9020020203'},hashvo);
        }
    }

    function save_ok(outparameters)
    {
    standardItemForm.action="/performance/achivement/standarditem/search_standarditem_list.do?b_init=init";
    standardItemForm.submit();
    }
    function changeColor(itemid)
    {
    if(trim(beforeitemid).length>0&&document.getElementById(beforeitemid)!=null)
    {
    document.getElementById(beforeitemid).className='RecordRow';
    }
    var e = document.getElementById(itemid);
    beforeitemid=itemid;
    e.className="recordrow selectedBackGroud";
    }
    function deleteStandardItem()
    {
    if(beforeitemid=='-1')
    {
    alert("请选择要删除的项目！");
    return;
    }
    if(confirm("确认执行删除操作？"))
    {
    var point_id=standardItemForm.point_id.value;
    var hashvo=new ParameterSet();
    hashvo.setValue("point_id",point_id);
    hashvo.setValue("itemid",beforeitemid);
    var request=new Request({asynchronous:false,onSuccess:save_ok,functionId:'9020020204'},hashvo);
    }
    }
    function saveSocre()
    {
    var s_arr=document.getElementsByName("score");
    var i_arr=document.getElementsByName("itemid");
    //var checkFloat = /^\d+(\.\d+)?$/;
    var val="";
    if(s_arr==null||s_arr.length==0)
    {
    return;
    }
    for(var i=0;i<s_arr.length;i++)
    {
    val+=","+s_arr[i].value+"/"+i_arr[i].value;
    }
    var hashvo=new ParameterSet();
    hashvo.setValue("value",val.substring(1));
    var request=new Request({asynchronous:false,onSuccess:save_ok,functionId:'9020020207'},hashvo);
    }
    function save_oks(outparameter)
    {
    alert("已保存！");
    }
    function checkValue(obj)
    {
    if(obj.value.length>0)
    {
    if(!checkIsNum2(obj.value))
    {
    alert('请输入数值！');
    obj.value='';
    obj.focus();
    }
    }

    /*
    var checkFloat = myReg =/^(-?\d+)(\.\d+)?$/;
    if(obj.value!='')
    {
    if(!checkFloat.test(obj.value))
    {
    alert("分值请输入数值！");
    obj.value="";
    return;
    }
    }
    */
    }
    function checkKeyCode(e)
    {
        var event = e || window.event;
        var keyCode = event.which || event.keyCode;
        if ( !(((keyCode >= 48) && (keyCode <= 57))
        || (keyCode == 13) || (keyCode == 46)
        || (keyCode == 45)))
        {
            return false;
        }

    }
    //-->
    </script>
    <style>

    .ListTable_self {
    BACKGROUND-COLOR: #FFFFFF;
    BORDER-BOTTOM: medium none;
    BORDER-C6OLLAPSE: collapse;
    BORDER-LEFT: medium none;
    BORDER-RIGHT: medium none;
    BORDER-TOP: medium none;

    }

    .RecordRow_self {
    border: inset 1px #94B6E6;
    BORDER-BOTTOM: #94B6E6 1pt solid;
    BORDER-LEFT: #94B6E6 1pt solid;
    BORDER-RIGHT: #94B6E6 1pt solid;
    BORDER-TOP: #94B6E6 1pt solid;
    font-size: 12px;

    }

    .trDeep_self {
    border: inset 1px #94B6E6;
    BORDER-BOTTOM: #94B6E6 1pt solid;
    BORDER-LEFT: #94B6E6 1pt solid;
    BORDER-RIGHT: #94B6E6 1pt solid;
    BORDER-TOP: #94B6E6 1pt solid;
    font-size: 12px;
    background-color: #DDEAFE;
    }
    .Input_self{
    font-size:   12px;
    font-weight:   bold;
    background-color:   #FFFFFF;
    letter-spacing:   1px;
    text-align:   right;
    height:   90%;
    width:   90%;
    border:   1px   solid   #94B6E6;
    cursor:   hand;
    }

    </style>
    <html:form action="/performance/achivement/standarditem/search_standarditem_list">
        <script language="javascript">
        var _checkBrowser=true;
        var _disableSystemContextMenu=false;
        var _processEnterAsTab=true;
        var _showDialogOnLoadingData=true;
        var _enableClientDebug=true;
        var _theme_root="/ajax/images";
        var _application_root="";
        var __viewInstanceId="968";
        var ViewProperties=new ParameterSet();
        </script>

        <!--
        <hrms:menubar menu="menu2" id="menubar2">
            <hrms:menuitem name="mitem0" label="label.edit.user" >
                <hrms:menuitem name="mitem1" icon="/images/add.gif" label="label.kh.new.tjxm" url="addStandardItem('0');">
                </hrms:menuitem>
                <hrms:menuitem name="mitem2" icon="/images/add.gif" label="label.kh.new.xjxm" url="addStandardItem('1');">
                </hrms:menuitem>
                <hrms:menuitem name="mitem3" icon="/images/add.gif" label="label.kh.crxm" url="addStandardItem('2');">
                </hrms:menuitem>
                <hrms:menuitem name="mitem4" icon="/images/edit.gif" label="label.kh.edit" url="addStandardItem('3');">
                </hrms:menuitem>
                <hrms:menuitem name="mitem5" icon="/images/del.gif" label="label.kh.del" url="deleteStandardItem();">
                </hrms:menuitem>
            </hrms:menuitem>
        </hrms:menubar>
        -->
        <table border="0" align="center">
            <tr>
                <td align="left" nowrap width="540">
                    <input type="button" class="mybutton" name="addT" value="<bean:message key="label.kh.new.tjxm"/>" onclick="addStandardItem('0');"/>
                    <input type="button" class="mybutton" name="addX" value="<bean:message key="label.kh.new.xjxm"/>" onclick="addStandardItem('1');"/>
                    <input type="button" class="mybutton" name="crxm" value="<bean:message key="label.kh.crxm"/>" onclick="addStandardItem('2');"/>
                    <input type="button" class="mybutton" name="edit" value="<bean:message key="label.kh.edit"/>" onclick="addStandardItem('3');"/>
                    <input type="button" class="mybutton" name="del" value="<bean:message key="label.kh.del"/>" onclick="deleteStandardItem();"/>
                    <input type="button" class="mybutton" name="save" value="<bean:message key="button.save"/>" onclick="saveSocre();"/>
                    <input type="button" class="mybutton" name="ret" value="<bean:message key="button.close"/>" onclick="parent.window.close();"/>



                </td>
            </tr>
            <tr>
                <td align="left">
                    <div style="width:540px;height:360px;overflow:auto;" >
  ${standardItemForm.tableHtml}
  </div>
  </td>
   </tr>
   <html:hidden name="standardItemForm" property="point_id"/>
   </table>




<script language="javascript">
initDocument();
		var aa=document.getElementsByTagName("input");
		for(var i=0;i<aa.length;i++){
			if(aa[i].type=="text"){
				aa[i].className="inputtext";
			}
		}

</script>
</html:form>