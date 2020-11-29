<%@ page contentType="text/html; charset=UTF-8"%>
<script language="javascript" src="/js/validate.js"></script>

<script language=javascript type="text/javascript">

function F(str)
{
    //兼容非IE浏览器 wangbs 20190315
    if(getBrowseVersion()){
        top.returnValue = str;
        top.close();
    }else{
        if(parent.Ext){
            var closeTarget = parent.Ext.getCmp("deleteSelectPosWin");
            if(closeTarget){
                closeTarget.return_vo = str;
                closeTarget.close();
            }
        }
    }
}

</script>
<body style="background: menu">

<div style="margin-top: 5%; margin-left:8%">
<div id="msg" style="font-size:12px;margin-bottom:10%"></div>
<input id="Button1" type="button" value="是(Y)" name="6" style="width:60px;height:25px" onclick="F(Button1.name);"/>
 
<input id="Button2" type="button" value="否(N)" name="7" style="width:60px;height:25px" onclick="F(Button2.name);"/>

<input id="Button3" type="button" value="取消" name="8" style="width:60px;height:25px" onclick="F(Button3.name);" />

</div>

</body>
<script language=javascript type="text/javascript">
    if(getBrowseVersion()){
        document.getElementById("msg").innerHTML=parent.dialogArguments;
    }else{
        //非IE浏览器处理 wangbs 20190315
        if(parent.Ext){
            if(parent.Ext.getCmp("deleteSelectPosWin")){
                document.getElementById("msg").innerHTML = parent.Ext.getCmp("deleteSelectPosWin").content;
            }
        }
    }
</script>