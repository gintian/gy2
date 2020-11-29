<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<style type="text/css">
    .m_frameborder {
        border-left: 1px inset #D4D0C8;
        border-top: 1px inset #D4D0C8;
        border-right: 1px inset #FFFFFF;
        border-bottom: 1px inset #FFFFFF;
        width: 40px;
        height: 19px;
        background-color: transparent;
        overflow: hidden;
        text-align: right;
        font-family: "Tahoma";
        font-size: 6px;
    }

    .m_arrow {
        width: 16px;
        height: 8px;
        font-family: "Webdings";
        font-size: 7px;
        line-height: 2px;
        padding-left: 2px;
        cursor: default;
    }

    .m_input {
        width: 18px;
        height: 14px;
        border: 0px solid black;
        font-family: "Tahoma";
        font-size: 9px;
        text-align: right;
    }

</style>
<hrms:themes></hrms:themes>
<script language="Javascript" src="/gz/salary.js"></script>
<script type="text/javascript">
    <!--
    function changeDisplay(id1, id2, id3) {
        var tab1 = document.getElementById(id1);
        var tab2 = document.getElementById(id2);
        var tab3 = document.getElementById(id3);
        tab1.style.display = "block";
        tab2.style.display = "none";
        tab3.style.display = "none";

    }

    function gzemail_gettxt(obj) {
        insertTxt("2", obj.value, "formulacontent", "");
    }

    function gzemail_changeitemid(obj) {
        var itemid = "";
        var text = "";
        for (var i = 0; i < obj.options.length; i++) {
            if (obj.options[i].selected) {
                itemid = obj.options[i].value;
                text = obj.options[i].text;
                break;
            }
        }
        insertTxt("4", text, "formulacontent", "");
        var hashvo = new ParameterSet();
        hashvo.setValue("itemid", itemid);
        var In_paramters = "flag=1";
        var request = new Request({
            method: 'post', asynchronous: false,
            parameters: In_paramters, onSuccess: resultChangeCodeList, functionId: '0202030009'
        }, hashvo);
    }

    function resultChangeCodeList(outparamters) {
        var codelist = outparamters.getValue("codelist");
        var type = outparamters.getValue("itemtype");
        var codesetid = outparamters.getValue("codesetid");
        var obj = document.getElementById("v");
        if (type == "a" && codesetid != "0") {
            obj.style.display = "block";
        }
        else {
            obj.style.display = "none";
        }
        AjaxBind.bind(gzEmailForm.codevalue, codelist);

    }

    function gzemail_getcodevalue(obj) {
        var val = "";
        for (var i = 0; i < obj.options.length; i++) {
            if (obj.options[i].selected) {
                val = obj.options[i].value;
                break;
            }
        }
        if (val == '#')
            return;
        insertTxt("5", val, "formulacontent", "");
    }

    function gzemail_chooseformula() {
        var formula = gzEmailForm.formulacontent.value;
        //保存时，公式为空输入提示 15556  wangb 20170526
        if (formula.length == 0) {
            alert("请输入公式");//公式为空时，提示  15556  wangb 20170526
            return;
        }
        var itemid = gzEmailForm.itemid.value;
        var obj = document.getElementsByName("formulatype");
        var fieldtype = ""
        var num = 0;
        for (var i = 0; i < obj.length; i++) {
            if (obj[i].checked) {
                fieldtype = obj[i].value;
                num++;
            }
        }
        if (num == 0) {
            fieldtype = "L";
        }
        var hashvo = new ParameterSet();
        hashvo.setValue("c_expr", getEncodeStr(formula));
        hashvo.setValue("itemid", itemid);
        hashvo.setValue("type", fieldtype);
        var In_paramters = "flag=1";
        var request = new Request({
            method: 'post', asynchronous: false,
            parameters: In_paramters, onSuccess: gzemail_choose_ok, functionId: '0202030025'
        }, hashvo);
    }

    //"/fieldtitle/fieldtype/fieldcontent/dateformat/fieldlen/ndec/codeset/nflag"
    //fieldtitle   公式标题或指标名称
    //fieldcontent  公式内容或指标id
    //ndec  小数点位数
    //nflag =0是指标，=1是公式
    function gzemail_choose_ok(outparameters) {
        var info = outparameters.getValue("info");
        if (info == "ok") {
            var fieldtitle = gzEmailForm.formulatitle.value;
            var fieldlen = gzEmailForm.integerdigit.value;
            var ndec = gzEmailForm.decimalfractiondigit.value;
            var dateformat = "0";
            //var fieldtype=gzEmailForm.formulatype.value;
            if (fieldtitle == null || trim(fieldtitle).length == 0) {
                alert("请输入公式标题");
                return;
            }
            var fieldcontent = gzEmailForm.formulacontent.value;
            var setobj = new Object();
            setobj.fieldtitle = fieldtitle;
            setobj.fieldcontent = fieldcontent;
            setobj.ndec = ndec;
            setobj.codeset = "";
            setobj.nflag = "1";
            var obj = document.getElementsByName("formulatype");
            var fieldtype = ""
            for (var i = 0; i < obj.length; i++) {
                if (obj[i].checked) {
                    fieldtype = obj[i].value;
                    break;
                }
            }
            setobj.fieldtype = fieldtype;
            if (fieldtype == "A") {
                fieldlen = gzEmailForm.formulalength.value;
            }
            if (fieldtype == "D") {
                dateformat = gzEmailForm.dateFormat.value;
            }

            setobj.fieldlen = fieldlen;
            setobj.dateformat = dateformat;
            winReturn(setobj);
        } else {
            alert(getDecodeStr(info));
            return;
        }
    }

    //兼容谷歌浏览器 wangbs 20190320
    function winReturn(setobj) {
        if (parent.parent.formulaReturn) {
            parent.parent.formulaReturn(setobj);
        } else {
            returnValue = setobj ? setobj : "";
            window.close();
        }
    }

    function checkformula() {
        var formula = gzEmailForm.formulacontent.value;
        if (formula.length == 0) {
            alert("请输入公式");//公式为空时，提示  15556  wangb 20170526
            return;
        }
        var itemid = gzEmailForm.itemid.value;
        var obj = document.getElementsByName("formulatype");
        /* 放开注释的非空检验 xiaoyun 2014-9-2 start
        if(itemid=="#"||itemid==""){
          alert("请选择指标");
          return;
        }
        */
        /* 放开注释的非空检验 xiaoyun 2014-9-2 end */
        var fieldtype = ""
        var num = 0;
        for (var i = 0; i < obj.length; i++) {
            if (obj[i].checked) {
                fieldtype = obj[i].value;
                num++;
            }
        }
        if (num == 0) {
            fieldtype = "L";
        }
        var hashvo = new ParameterSet();
        hashvo.setValue("c_expr", getEncodeStr(formula));
        hashvo.setValue("itemid", itemid);
        hashvo.setValue("type", fieldtype);
        var In_paramters = "flag=1";
        var request = new Request({
            method: 'post', asynchronous: false,
            parameters: In_paramters, onSuccess: resultCheckInfo, functionId: '0202030025'
        }, hashvo);

    }

    function resultCheckInfo(outparamters) {
        var info = outparamters.getValue("info");
        if (info == "ok") {
            alert("公式正确");
            return;
        } else {
            alert(getDecodeStr(info));
            return;
        }
    }

    function replaceAllStr(str, from, to) {
        var idx = str.indexOf(from);
        while (idx > -1) {
            str = str.replace(from, to);
            idx = str.indexOf(from);
        }
        return str;
    }

    function initVisable(type) {
        if (type == 'A') {
            document.getElementById("B").style.display = "none";
            document.getElementById("C").style.display = "none";
            document.getElementById("A").style.display = "block";
        }
        if (type == 'D') {
            document.getElementById("B").style.display = "block";
            document.getElementById("C").style.display = "none";
            document.getElementById("A").style.display = "none";
        }
        if (type == 'N') {
            document.getElementById("B").style.display = "none";
            document.getElementById("C").style.display = "block";
            document.getElementById("A").style.display = "none";
        }
    }

    function openReturn(return_vo) {
        if (return_vo) {
            symbol("formulacontent", return_vo);
        }
        window.close();
    }

    //-->
</script>

<html:form action="/general/email_template/insert_formula">
    <table width='570' border="0" cellspacing="0" align="center" cellpadding="0">
        <tr>
            <td align="left" nowrap>
                <table width='100%' border="0" cellspacing="1" align="center" cellpadding="1">
                    <tr>
                        <td width="400" align="center" nowrap>
                            <table width='100%' border="0" cellspacing="1" align="center" cellpadding="1">
                                <tr>
                                    <td style="line-height: 24px;width: 60px">公式标题</td>
                                    <td>
                                        <html:text property="formulatitle" title="" name="gzEmailForm" size="50"
                                                   styleClass="text4" style="width:100%;"></html:text>
                                    </td>
                                </TR>
                            </table>
                        </td>
                        <td width="20%">
                            <html:hidden name="gzEmailForm" property="nmodule" styleId="nm"/>
                            &nbsp;
                        </td>
                    </tr>
                    <tr>
                        <td width="400" align="left" nowrap>
                            <FIELDSET>
                                <LEGEND>公式类型</LEGEND>
                                <table width='100%' border="0" cellspacing="1" align="center" cellpadding="1">
                                    <tr>
                                        <td align="left" nowrap>
                                            <html:radio name="gzEmailForm" property="formulatype" value="A"
                                                        onclick="changeDisplay('A','B','C');"/>字符
                                            <html:radio name="gzEmailForm" property="formulatype" value="D"
                                                        onclick="changeDisplay('B','A','C');"/>日期
                                            <html:radio name="gzEmailForm" property="formulatype" value="N"
                                                        onclick="changeDisplay('C','B','A');"/>数值
                                        </td>
                                    </tr>
                                    <tr>
                                        <td align="center" nowrap>
                                            <table width='100%' border="0" cellspacing="1" align="center"
                                                   cellpadding="1">
                                                <tr>
                                                    <td>
                                                        <table width="50%" id="A" border="0" cellspacing="0"
                                                               cellpadding="0">
                                                            <tr>
                                                                <td align="left" nowrap>&nbsp;长度&nbsp;</td>
                                                                <td valign="middle" align="right">
                                                                    <html:text name="gzEmailForm" styleId='year_s'
                                                                               property="formulalength" size="4"
                                                                               onkeypress="event.returnValue=IsDigit();"
                                                                               styleClass="inputtext"/>&nbsp;
                                                                </td>
                                                                <td valign="middle" align="left" width="90%">
                                                                    <table border="0" cellspacing="2" align="left"
                                                                           cellpadding="0">
                                                                            <%--防止刷新界面  wangbs 20190320--%>
                                                                        <tr>
                                                                            <td>
                                                                                <button type="button" id="1_up" name="1_up"
                                                                                        class="m_arrow"
                                                                                        onmouseup="IsInputValue('year_s','3');">
                                                                                    5
                                                                                </button>
                                                                            </td>
                                                                        </tr>
                                                                        <tr>
                                                                            <td>
                                                                                <button type="button" id="1_down" name="1_down"
                                                                                        class="m_arrow"
                                                                                        onmouseup="IsInputValue('year_s','3');">
                                                                                    6
                                                                                </button>
                                                                            </td>
                                                                        </tr>
                                                                    </table>
                                                                </td>
                                                            </tr>


                                                        </table>
                                                    </td>
                                                    <td>
                                                        <table width="50%" id="B" border="0" cellspacing="0"
                                                               cellpadding="0" style="display:none">
                                                            <tr>
                                                                <td width="100%" nowrap>
                                                                    日期格式 <html:select name="gzEmailForm" size="1"
                                                                                      property="dateFormat">
                                                                    <html:optionsCollection
                                                                            property="dateFormatList"
                                                                            value="dataValue" label="dataName"/>
                                                                </html:select>
                                                                </td>
                                                            </tr>
                                                        </table>
                                                    </td>
                                                    <td>
                                                        <table width="100%" border="0" id="C" cellspacing="0"
                                                               style="display:none" cellpadding="0">
                                                            <tr>
                                                                <td>
                                                                    <table width="50%" border="0" cellspacing="0"
                                                                           cellpadding="0">
                                                                        <tr>
                                                                            <td align="left" width="30%" nowrap>
                                                                                &nbsp;整数位&nbsp;
                                                                            </td>
                                                                            <td valign="middle">
                                                                                <html:text name="gzEmailForm"
                                                                                           styleId='year_e'
                                                                                           property="integerdigit"
                                                                                           size="4"
                                                                                           onkeypress="event.returnValue=IsDigit();"
                                                                                           styleClass="inputtext"/>&nbsp;
                                                                            </td>
                                                                            <td valign="middle" align="left">
                                                                                <table border="0" cellspacing="2"
                                                                                       cellpadding="0">
                                                                                    <tr>
                                                                                        <td>
                                                                                            <button type="button"
                                                                                                    id="1_up" name="1_up"
                                                                                                    class="m_arrow"
                                                                                                    onmouseup="IsInputValue('year_e','1');">
                                                                                                5
                                                                                            </button>
                                                                                        </td>
                                                                                    </tr>
                                                                                    <tr>
                                                                                        <td>
                                                                                            <button type="button"
                                                                                                    id="1_down" name="1_down"
                                                                                                    class="m_arrow"
                                                                                                    onmouseup="IsInputValue('year_e','1');">
                                                                                                6
                                                                                            </button>
                                                                                        </td>
                                                                                    </tr>
                                                                                </table>
                                                                            </td>
                                                                        </tr>


                                                                    </table>
                                                                </td>
                                                                <td align="left">

                                                                    <table width="50%" border="0" cellspacing="0"
                                                                           cellpadding="0">
                                                                        <tr>
                                                                            <td align="left" width="30%" nowrap>
                                                                                &nbsp;小数位&nbsp;
                                                                            </td>
                                                                            <td valign="middle">
                                                                                <html:text name="gzEmailForm"
                                                                                           styleId='card_s'
                                                                                           property="decimalfractiondigit"
                                                                                           size="4"
                                                                                           onkeypress="event.returnValue=IsDigit();"
                                                                                           styleClass="inputtext"/>&nbsp;
                                                                            </td>
                                                                            <td valign="middle" align="left">
                                                                                <table border="0" cellspacing="2"
                                                                                       cellpadding="0">
                                                                                    <tr>
                                                                                        <td>
                                                                                            <button type="button"
                                                                                                    id="1_up" name="1_up"
                                                                                                    class="m_arrow"
                                                                                                    onmouseup="IsInputValue('card_s','2');">
                                                                                                5
                                                                                            </button>
                                                                                        </td>
                                                                                    </tr>
                                                                                    <tr>
                                                                                        <td>
                                                                                            <button type="button"
                                                                                                    id="1_down" name="1_down"
                                                                                                    class="m_arrow"
                                                                                                    onmouseup="IsInputValue('card_s','2');">
                                                                                                6
                                                                                            </button>
                                                                                        </td>
                                                                                    </tr>
                                                                                </table>
                                                                            </td>
                                                                        </tr>


                                                                    </table>
                                                                </td>
                                                            </tr>
                                                        </table>
                                                    </td>
                                                </tr>
                                            </table>
                                        </td>
                                    </tr>
                                </table>
                            </FIELDSET>
                        </td>

                        <td align="center">
                            <input type="button" name="gride" onclick="function_Wizard('','formulacontent');"
                                   class="mybutton" value="公式向导"/>
                            <br>
                            <br>
                            <input type="button" name="check" class="mybutton" onclick="checkformula();"
                                   value="公式检查"/>
                        </td>

                    </tr>

                    <tr>
                        <td align="left" width="405" colspan="2" nowrap>
                            <html:textarea name="gzEmailForm" styleId="formulacontent" property="formulacontent"
                                           style="width: 405px;height: 250px;margin: 0px 2px 0 2px"/>
                                <%--谷歌下onclick执行的代码报错 注掉 wangbs 20190320--%>
                                <%--<html:textarea name="gzEmailForm" property="formulacontent" onclick="this.pos=document.selection.createRange();" cols="70" rows="15"/>--%>
                        </td>
                    </tr>
                    <tr>
                        <td width="20%" align="left" nowrap>
                            <FIELDSET style="height:98px;">
                                <LEGEND>参考项目</LEGEND>
                                <table width='80%' border="0" cellspacing="1" align="left" cellpadding="1">
                                    <tr>
                                        <td align="center" nowrap>
                                            <logic:equal value="2" name="gzEmailForm" property="nmodule">
                                                类别
                                            </logic:equal>
                                            <logic:notEqual value="2" name="gzEmailForm" property="nmodule">
                                                子集
                                            </logic:notEqual>
                                            <html:select name="gzEmailForm" property="formulafieldsetid" size="1"
                                                         onchange="changeFormulaFieldSet();" style="width:60%;">
                                                <html:optionsCollection property="fieldsetlist" value="dataValue"
                                                                        label="dataName"/>
                                            </html:select>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td align="center" nowrap>
                                            <table width='100%' border="0" id="t" style="display:none"
                                                   cellspacing="1" align="left" cellpadding="1">
                                                <tr>
                                                    <td align="center" nowrap>
                                                        指标
                                                        <html:select name="gzEmailForm" size="1" property="itemid"
                                                                     onchange="gzemail_changeitemid(this);"
                                                                     style="width:60%;">
                                                            <html:optionsCollection property="itemlist"
                                                                                    value="dataValue"
                                                                                    label="dataName"/>
                                                        </html:select>
                                                    </td>
                                                </tr>
                                            </table>

                                        </td>
                                    </tr>
                                    <tr>
                                        <td align="center"  nowrap>
                                            <table width='100%' border="0" id="v" style="display:none"
                                                   cellspacing="1" align="left" cellpadding="1">
                                                <tr>
                                                    <td align="center" width="310" nowrap>
                                                        代码
                                                        <html:select name="gzEmailForm" size="1"
                                                                     property="codevalue"
                                                                     onchange="gzemail_getcodevalue(this);"
                                                                     style="width:60%;">
                                                            <html:optionsCollection property="codefieldlist"
                                                                                    value="dataValue"
                                                                                    label="dataName"/>
                                                        </html:select>
                                                    </td>
                                                </tr>
                                            </table>

                                        </td>
                                    </tr>
                                </table>
                            </FIELDSET>
                        </td>
                        <td width="20%" align="center" nowrap valign="bottom" style="padding-left: 3px">
                            <FIELDSET align="center" style="height:81px;">
                                <table width='50%' border="0" cellspacing="1" align="center" cellpadding="1"
                                       id="smallbutton_table">
                                    <tr height="28">
                                        <td align="left" nowrap>
                                            <input type="button" name="and" onclick="gzemail_gettxt(this);"
                                                   value="且" class="smallbutton" style="width:25pt;"/>
                                        </td>
                                        <td align="left" nowrap>
                                            <input type="button" name="or" onclick="gzemail_gettxt(this);" value="或"
                                                   class="smallbutton" style="width:25pt;"/>
                                        </td>
                                        <td align="left" nowrap>
                                            <input type="button" name="not" onclick="gzemail_gettxt(this);"
                                                   value="非" class="smallbutton" style="width:25pt;"/>
                                        </td>
                                    </tr>
                                    <tr height="28">
                                        <td align="left" nowrap>
                                            <input type="button" name="if" onclick="gzemail_gettxt(this);"
                                                   value="如果" style="padding:0px;" class="smallbutton"/>
                                        </td>
                                        <td align="left" nowrap>
                                            <input type="button" name="so" onclick="gzemail_gettxt(this);"
                                                   value="那么" style="padding:0px;" class="smallbutton"/>
                                        </td>
                                        <td align="left" nowrap>
                                            <input type="button" name="else" onclick="gzemail_gettxt(this);"
                                                   value="否则" style="padding:0px;" class="smallbutton"/>
                                        </td>
                                    </tr>
                                    <tr height="28">
                                        <td align="left" nowrap>
                                            <input type="button" name="finish" onclick="gzemail_gettxt(this);"
                                                   value="结束" style="padding:0px;" class="smallbutton"/>
                                        </td>
                                        <td colspan="2" align="left" nowrap>
                                            <input type="button" name="case" onclick="gzemail_gettxt(this);"
                                                   value="分情况" class="smallbutton" style="width:50pt;padding:0px;"/>
                                        </td>
                                    </tr>
                                </table>
                            </FIELDSET>
                        </td>
                    </tr>
                </table>
                <table width='100%' border="0" cellspacing="0" align="center" cellpadding="0">
                    <TR>
                        <td align="center" colspan="2" nowrap height="35px;">
                            <input type="button" name="ok" value="确定" onclick="gzemail_chooseformula();"
                                   class="mybutton"/>
                            <input type="button" name="col" value="关闭" class="mybutton" onclick="winReturn();"/>
                        </td>
                    </TR>
                </table>
            </td>
        </tr>
    </table>
    <script language="javascript">
        initVisable("${gzEmailForm.formulatype}");
    </script>
</html:form>
<script>
    <%--兼容谷歌 ie11 邮箱模板--公式样式问题 wangbs 20190320 --%>
    if (!getBrowseVersion() || getBrowseVersion() == 10) {
        var year_s = document.getElementById("year_s");
        year_s.style.marginTop = "15px";
        var year_e = document.getElementById("year_e");
        year_e.style.marginTop = "15px";
        var card_s = document.getElementById("card_s");
        card_s.style.marginTop = "15px";
        var areaText = document.getElementsByName("formulacontent")[0];
        areaText.cols = "67";
        var smallbutton_table = document.getElementById('smallbutton_table');
        var buttons = smallbutton_table.getElementsByTagName('INPUT');
        for (var i = 0; i < buttons.length; i++) {
            if (buttons[i].getAttribute('class') != 'smallbutton')
                continue;
            buttons[i].style.padding = '';
        }
    }else{
    	var ups = document.getElementsByName('1_down');
    	for(var i = 0 ; i < ups.length ; i++){
    		ups[i].style.position = "relative";
    		ups[i].style.bottom = "8px";
    		
    	}
    }
</script>