<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.hjsj.sys.EncryptLockClient"%>
<%@ page import="com.hrms.hjsj.utils.Sql_switcher"%>
<%@ page import="java.util.*,
                 com.hjsj.hrms.actionform.general.template.TemplateListForm,
                 com.hjsj.hrms.actionform.general.template.TemplateForm,
                 org.apache.commons.beanutils.LazyDynaBean,
                 com.hrms.struts.taglib.CommonData,
                 com.hrms.struts.constant.SystemConfig,
                 com.hrms.frame.codec.SafeCode,
                 com.hrms.struts.valueobject.UserView,
                 com.hrms.struts.constant.WebConstant,
                 com.hrms.hjsj.sys.FieldItem,
                com.hrms.frame.dbstruct.Field" %>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>              
<%
    /**
    String aurl = (String)request.getServerName();
    String port=request.getServerPort()+"";
    String prl=request.getProtocol();
    int idx=prl.indexOf("/");
    prl=prl.substring(0,idx);    
    String url_s=prl+"://"+aurl+":"+port;
    String url_p=prl+"://"+aurl+":"+port;
    **/
    String url_p=SystemConfig.getServerURL(request); 
    String no_sp_opinion="false";
    if(SystemConfig.getPropertyValue("no_sp_opinion")!=null&&SystemConfig.getPropertyValue("no_sp_opinion").trim().equalsIgnoreCase("true"))
            no_sp_opinion=SystemConfig.getPropertyValue("no_sp_opinion");
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    
     String path = request.getSession().getServletContext().getRealPath("/js");
       if(SystemConfig.getPropertyValue("webserver").equals("weblogic"))
       {
          path=session.getServletContext().getResource("/js").getPath();//.substring(0);
          if(path.indexOf(':')!=-1)
          {
             path=path.substring(1);   
          }
          else
          {
             path=path.substring(0);      
          }
          int nlen=path.length();
          StringBuffer buf=new StringBuffer();
          buf.append(path);
          buf.setLength(nlen-1);
          path=buf.toString();
       }
       userView.getHm().put("js_path",path);
    
    String bosflag= userView.getBosflag();//标志着版本，在js中返回需要用到
    String username=userView.getUserName();
    String userFullName=null;
    userFullName=userView.getUserFullName();
    String superUser=userView.isSuper_admin()?"1":"0";
    String fields=userView.getFieldpriv().toString();

    String tables=userView.getTablepriv().toString();
    String dbtype=String.valueOf(Sql_switcher.searchDbServer());
    TemplateForm templateForm=(TemplateForm)session.getAttribute("templateForm");
        int versionFlag = 1;
        //zxj 20160613 人事异动不再区分标准版专业版
        //if (userView != null)
          //  versionFlag = userView.getVersion_flag(); // 1:专业版 0:标准版    
    
    
    String promptIndex_template="true"; //#拥有检索条件模板自动提示
    if(SystemConfig.getPropertyValue("promptIndex_template")!=null&&SystemConfig.getPropertyValue("promptIndex_template").trim().equalsIgnoreCase("false"))
        promptIndex_template="false"; 

    TemplateListForm templateListForm=(TemplateListForm)session.getAttribute("templateListForm"); 
    String nodeprive = templateListForm.getNodeprive(); 
    if(nodeprive!=null&&!nodeprive.equals("-1")&&nodeprive.trim().length()>3&&!nodeprive.equalsIgnoreCase("null"))
        fields =nodeprive;
    String index_template = templateListForm.getIndex_template();
    if(index_template!=null&&index_template.equals("1"))
    promptIndex_template="false";
    String filter_by_factor=templateListForm.getFilter_by_factor(); //手工选人、条件选人按检索条件过滤, 0不过滤(默认值),1过滤
    String isfilter_select="0";
    if(filter_by_factor.equals("1")&&templateListForm.getSys_filter_factor()!=null&&templateListForm.getSys_filter_factor().trim().length()>0)
    {
            isfilter_select="1";
    }
    
    
    
    String _static=templateListForm.get_static();  //1:人事异动；2：是薪资管理 ；8：保险变动；21：劳动合同；12：出国管理 ;10:单位管理机构调整;11:岗位管理机构调整
    String isFinishedRecord=templateListForm.getIsFinishedRecord();
    
     String otherObjectPrint="1";  //高级花名册|等级表是否出现打印按钮
     if(_static.equals("2")&&!userView.isSuper_admin()&&!userView.hasTheFunction("324010103")){
                if(isFinishedRecord.equals("1")&&userView.hasTheFunction("324010120")){
                
                }
                else
                    otherObjectPrint="0";
     }
    
    ArrayList tableHeadSetList=templateListForm.getTableHeadSetList();
    int dataSize=templateListForm.getTemplatelistform().getList().size();
    String    isName=templateListForm.getIsName();
    String    sp_batch=templateListForm.getSp_batch();  //是否是批量审批
    String    tasklist_str=templateListForm.getTasklist_str();
    String    operationtype=templateListForm.getOperationtype(); //0人员调入
    LazyDynaBean lockBean = templateListForm.getLockBean();

    String    isCompare=templateListForm.getIsCompare();  //是否对照
    String    selectMan_flag="true";  //是否出现选人菜单
    String    isShowCompare="true";
    if(operationtype.equals("0")||operationtype.equals("5"))    //0调入，5新增单位7撤销机构8合并机构9划转机构
    {
        selectMan_flag="false";
        isShowCompare="false";
    }
    if(operationtype.equals("6")||operationtype.equals("7")||operationtype.equals("8")||operationtype.equals("9")){
        isShowCompare="false";
    }
    String    _task_id=templateListForm.getTask_id();
    if(_task_id!=null&&_task_id.length()>0&&!_task_id.equals("0"))
        selectMan_flag="false";
    if(sp_batch!=null&&sp_batch.equalsIgnoreCase("1")&&tasklist_str!=null&&tasklist_str.trim().length()>0)
        selectMan_flag="false";     
    request.setAttribute("selectMan_flag",selectMan_flag);
    request.setAttribute("isShowCompare",isShowCompare);
    String   isCompare_str="false";
    if(isCompare.equals("1")){
        isCompare_str="true";
        dataSize = dataSize*2;  
        }
    request.setAttribute("isCompare_str",isCompare_str);
    
    String    isAppealTable=templateListForm.getIsAppealTable();
    String    table_name=templateListForm.getTable_name();
    String    isSelectAll=templateListForm.getIsSelectAll();
    HashMap prechangemap = templateListForm.getPrechangemap();
    int flag2=0;
    String manager=userView.getManagePrivCodeValue();
    
    EncryptLockClient lockclient=(EncryptLockClient)session.getServletContext().getAttribute("lock");
    String license=lockclient.getLicenseCount();
    int _version=userView.getVersion();
    if(license.equals("0"))
        _version=100+_version;
    int usedday=lockclient.getUseddays();
    String infor_type=templateListForm.getInfor_type();
    String labelname = "候选人";
    if(infor_type!=null&&infor_type.equals("1")){
    labelname = "候选人";
    }else if(infor_type!=null&&infor_type.equals("2")){
    labelname = "候选机构";
    }else if(infor_type!=null&&infor_type.equals("3")){
    labelname = "候选岗位";
    }
 %>              
<html>
  <head>
    
  </head>
<link href="/gz/templateset/standard/tableLocked.css" rel="stylesheet" type="text/css">  
<link rel="stylesheet" href="/components/tableFactory/tableGrid-theme/tableGrid-theme-all.css" type="text/css" />
<script language="JavaScript" src="/module/utils/js/template.js"></script> 
<script language="javascript" src="/general/sys/hjaxmanage.js"></script>
<script language="javascript" src="/general/template/templatelist/templatelist.js"></script>
<script type="text/javascript" src="/js/function.js"></script>  

<style>

div#tbl-container {
    width:100%;
    overflow:auto;
    BORDER-BOTTOM:#94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 1pt solid; 
}

div#date_panel {
    width:500;
    height: 220;
    overflow:auto;
    background-color:#ffffff;
    BORDER-BOTTOM:#94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 1pt solid; 
}
.t_cell_locked {
        border: inset 1px #94B6E6;
        BACKGROUND-COLOR: #ffffff;
        BORDER-BOTTOM: #94B6E6 1pt solid; 
        BORDER-LEFT: #94B6E6 0pt solid; 
        BORDER-RIGHT: #94B6E6 1pt solid; 
        BORDER-TOP: #94B6E6 0pt solid;
        font-size: 12px;
         
        height:22;
    
        background-position : center left;
        left: expression(document.getElementById("tbl-container").scrollLeft); /*IE5+ only*/
        position: relative;
        z-index: 10;
    
    }
    .t_cell_locked_b2 {
        border: inset 1px #94B6E6;
        BACKGROUND-COLOR: #ffffff;
        BORDER-BOTTOM: #94B6E6 1pt solid; 
        BORDER-LEFT: #94B6E6 0pt solid; 
        BORDER-RIGHT: #94B6E6 1pt solid; 
        BORDER-TOP: #94B6E6 0pt solid;
        font-size: 12px;
         
        height:22;
    
        background-position : center left;
        left: expression(document.getElementById("tbl-container").scrollLeft); /*IE5+ only*/
        position: relative;
        z-index: 10;
    
    }
    .t_cell_locked_b {
        border: inset 1px #94B6E6;
        BACKGROUND-COLOR: #ffffff;
        BORDER-BOTTOM: #94B6E6 1pt solid; 
        BORDER-LEFT: #94B6E6 0pt solid; 
        BORDER-RIGHT: #94B6E6 1pt solid; 
        BORDER-TOP: #94B6E6 0pt solid;
        font-size: 12px;
        
        height:22;
    
        background-position : center left;
        left: expression(document.getElementById("tbl-container").scrollLeft); /*IE5+ only*/
        position: relative;
        z-index: 10;
    
    }
    
    .t_header_locked{
    /*background-image:url(/images/listtableheader_deep-8.jpg);*/
    background-repeat:repeat;
    background-position : center left;
    BACKGROUND-COLOR: #f4f7f7;  
    border: inset 1px #94B6E6;
    COLOR : black;
    BORDER-BOTTOM: #94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 0pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 0pt solid;
    height:30;
    valign:middle;
    font-weight: bold;  
    text-align:center;
    top: expression(document.getElementById("tbl-container").scrollTop); /*IE5+ only*/
    position: relative;
    z-index: 15;
    }
    
        
    .t_cell_locked2 {
      /*  background-image:url(/images/listtableheader_deep-8.jpg);*/
        background-repeat:repeat;
        background-position : center left;
        BACKGROUND-COLOR: #f4f7f7;  
        BORDER-BOTTOM: #94B6E6 1pt solid; 
        BORDER-LEFT: #94B6E6 0pt solid; 
        BORDER-RIGHT: #94B6E6 1pt solid; 
        BORDER-TOP: #94B6E6 0pt solid;
        height:22;
        font-weight: bold;  
        valign:middle;
        COLOR : black;
        left: expression(document.getElementById("tbl-container").scrollLeft); /*IE5+ only*/
        top: expression(document.getElementById("tbl-container").scrollTop); /*IE5+ only*/
        position: relative;
        z-index: 20;
    
    }
    .RecordRow3 {
    border: inset 1px #C4D8EE;
    BORDER-BOTTOM: #94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 0pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 0pt solid;
    font-size: 12px;
    
    height:22;
}

  </style>
<script language="JavaScript" src="/js/wz_tooltip.js"></script>
<script language='javascript' > 
Ext.Loader.setConfig({
	enabled: true,
	paths: {
		'JobtitleUL': '/module/template/templatetoolbar/jobtitle'
	}
});
var controlHeadCount="1";  // 1:控制人员编制  
var isfilter_select='<%=isfilter_select%>';
var otherObjectPrint='<%=otherObjectPrint%>';
var promptIndex_template='<%=promptIndex_template%>';
var no_priv_ctrl="${templateListForm.no_priv_ctrl}";
var hasRecordFromMessage='${templateListForm.hasRecordFromMessage}';
var operationtype='${templateListForm.operationtype}'
var table_name='${templateListForm.table_name}'
var no_sp_opinion='<%=no_sp_opinion%>'; 
var isSendMessage="${templateListForm.isSendMessage}";
var operationname="${templateListForm.operationname}";
var staticid="${templateListForm.staticid}";
var codeid='${templateListForm.codeid}';
var tabid='${templateListForm.tabid}';
var taskid='${templateListForm.task_id}';
var batch_task='${templateListForm.batch_task}';
var ins_id='${templateListForm.ins_id}';
var sp_mode='${templateListForm.sp_mode}';
var allow_def_flow_self="${templateListForm.allow_def_flow_self}";
var isApplySpecialRole='${templateListForm.isApplySpecialRole}';
var tasklist_str='${templateListForm.tasklist_str}';
var url_s="<%=url_p%>";
var returnflag='${templateListForm.returnflag}';
var warn_id='${templateListForm.warn_id}';
var isEmployee='${templateListForm.isEmployee}'
var sys_filter_factor='${templateListForm.sys_filter_factor}';
var pageHeight=document.body.clientHeight;
var recordNum=<%=dataSize%>;
var _basepre="";
var _a0100="";
var num='${templateListForm.num}'
var _version='<%=_version%>';
var infor_type='${templateListForm.infor_type}';
//前台不允许使用sql这里去掉了 hmuster_sql
var sp_batch = "${templateListForm.sp_batch}";
var modeType="${templateForm.type}";
var overflag ="";
var nextNodeStr="${templateForm.nextNodeStr}";
var selectAll=0;
 var generalmessage =  '${templateListForm.generalmessage}';
 var bosflag="<%=bosflag%>";
function outContent(context)
{
    var context = getDecodeStr(context);
	if(context.indexOf('\r\n')!=-1){
		context = context.replace('\r\n','<br>');
	}
	if(context.indexOf('\n')!=-1){
		context = context.replace('\n','<br>');
	}
	if(context.indexOf('%26amp;')!=-1){
		context = context.replace('%26amp;','&');
	}
    Tip(context);
}


var srcobj;

function hiddenPanel()
{
    Element.hide('date_panel');
}

</script>
<hrms:themes></hrms:themes>
<%	   
if ("hcm".equals(bosflag)){	   
%>
<link href="/general/template/template.css" rel="stylesheet" type="text/css"/>
<%} %>
  <body   Onload='validateIndex()' >
  <% int f=0; %>
   
    <html:form action="/general/template/templatelist">
 
    
 
<table width='100%' class="listmargin" ><tr><td >
    <table ><tr><td >
        <hrms:menubar menu="menu1" id="menubar1">
          <hrms:menuitem name="p0" label="文件"  function_id="" >
          
          <%  
            if(_static.equals("2")&&!userView.isSuper_admin()&&!userView.hasTheFunction("324010103")){
                if(isFinishedRecord.equals("1")&&userView.hasTheFunction("324010120")){
         %>
            <hrms:menuitem name="p01" label="打印" icon="" url="printout()"   />  
            <logic:equal name="templateListForm" property="infor_type" value="1"> 
            <hrms:menuitem name="p03" label="全部人员生成PDF" icon="" url="outpdf('${templateListForm.table_name}','${templateListForm.ins_id}','1','0','-1');"  function_id="" />
            <hrms:menuitem name="p04" label="部分人员生成PDF" icon="" url="outpdf('${templateListForm.table_name}','${templateListForm.ins_id}','2','0','-1');"  function_id="" />
            </logic:equal>
             <logic:equal name="templateListForm" property="infor_type" value="2"> 
            <hrms:menuitem name="p03" label="全部机构生成PDF" icon="" url="outpdf('${templateListForm.table_name}','${templateListForm.ins_id}','1','0','-1');"  function_id="" />
            <hrms:menuitem name="p04" label="部分机构生成PDF" icon="" url="outpdf('${templateListForm.table_name}','${templateListForm.ins_id}','2','0','-1');"  function_id="" />
            </logic:equal> 
            <logic:equal name="templateListForm" property="infor_type" value="3"> 
            <hrms:menuitem name="p03" label="全部岗位生成PDF" icon="" url="outpdf('${templateListForm.table_name}','${templateListForm.ins_id}','1','0','-1');"  function_id="" />
            <hrms:menuitem name="p04" label="部分岗位生成PDF" icon="" url="outpdf('${templateListForm.table_name}','${templateListForm.ins_id}','2','0','-1');"  function_id="" />
            </logic:equal> 
         <% 
                }
            }else{ %>
          
            <hrms:menuitem name="p01" label="打印" icon="" url="printout()"  function_id="32104,37004,37104,37204,37304,33101003,33001003,2701503,0C34803,32003,324010103,325010103,2306703,23110203,3800703,3800703" />  
            <logic:equal name="templateListForm" property="infor_type" value="1"> 
            <hrms:menuitem name="p03" label="全部人员生成PDF" icon="" url="outpdf('${templateListForm.table_name}','${templateListForm.ins_id}','1','0','-1');"  function_id="" />
            <hrms:menuitem name="p04" label="部分人员生成PDF" icon="" url="outpdf('${templateListForm.table_name}','${templateListForm.ins_id}','2','0','-1');"  function_id="" />
            </logic:equal>
             <logic:equal name="templateListForm" property="infor_type" value="2"> 
            <hrms:menuitem name="p03" label="全部机构生成PDF" icon="" url="outpdf('${templateListForm.table_name}','${templateListForm.ins_id}','1','0','-1');"  function_id="" />
            <hrms:menuitem name="p04" label="部分机构生成PDF" icon="" url="outpdf('${templateListForm.table_name}','${templateListForm.ins_id}','2','0','-1');"  function_id="" />
            </logic:equal> 
            <logic:equal name="templateListForm" property="infor_type" value="3"> 
            <hrms:menuitem name="p03" label="全部岗位生成PDF" icon="" url="outpdf('${templateListForm.table_name}','${templateListForm.ins_id}','1','0','-1');"  function_id="" />
            <hrms:menuitem name="p04" label="部分岗位生成PDF" icon="" url="outpdf('${templateListForm.table_name}','${templateListForm.ins_id}','2','0','-1');"  function_id="" />
            </logic:equal>
          <% } %>   
            <logic:equal name="templateListForm" property="checkhmuster" value="1">
                <hrms:menuitem name="mitem10" label="org.performance.print.highroster" icon="/images/print.gif" url="printInform('${templateListForm.ins_id}','${templateListForm.tabid}');"  enabled="true" visible="true" function_id=""/>
            </logic:equal>
            <logic:notEqual name="templateListForm" property="checkhmuster" value="1">
                <hrms:menuitem name="mitem10" label="org.performance.print.highroster" icon="/images/print.gif" url="printInform('${templateListForm.ins_id}','${templateListForm.tabid}');"  enabled="false" visible="false" function_id=""/>
            </logic:notEqual>
            
            <% if(otherObjectPrint.equals("1")){ %>
            
            <logic:iterate id="element"  name="templateListForm"  property="outformlist" indexId="index">
                <%
                    LazyDynaBean item=(LazyDynaBean)pageContext.getAttribute("element");
                    String id=(String)item.get("id");
                    String name=(String)item.get("name");
                    String flag=(String)item.get("flag");
                    String tabid=(String)item.get("tabid");
                    String jsfunc="print(\""+id+"\",\""+flag+"\",\""+tabid+"\");";
                    String pactive="printActive(\""+id+"\",\"CardPreview1\",\""+table_name+"\");";
                    String cardpdf="printcardpdf(\""+id+"\");";
                    if(flag!=null&&flag.equals("2")){
                    jsfunc=cardpdf;
                }
                    ++f;
                    
                %>          
                      <hrms:menuitem name='<%="mitem"+f%>' label='<%=name%>' icon="" url='<%=jsfunc%>'  function_id="324010120,32104,37004,37104,37204,37304,33001003,33101003,2701503,0C34803,32003,324010103,325010103,2306703,23110203,3800703,3800703"  command="" enabled="true" visible="true">
                        <%if(flag!=null&&flag.equals("2")){ %>
                         <hrms:menuitem name="mitem4" label="button.print" icon="/images/print.gif" url='<%=pactive%>'    enabled="true" visible="true"/>
                        <% if(infor_type!=null&&infor_type.equals("1")){ %>
                         <hrms:menuitem name="mitem3" label="menu.gz.currpdf" icon="/images/export.gif" url='<%=cardpdf%>'  enabled="true" visible="true"/>
                       <%} %>
                        <% if(infor_type!=null&&infor_type.equals("2")){ %>
                         <hrms:menuitem name="mitem3" label="menu.gz.currorgpdf" icon="/images/export.gif" url='<%=cardpdf%>'  enabled="true" visible="true"/>
                       <%} %>
                        <% if(infor_type!=null&&infor_type.equals("3")){ %>
                         <hrms:menuitem name="mitem3" label="menu.gz.currkingpdf" icon="/images/export.gif" url='<%=cardpdf%>'  enabled="true" visible="true"/>
                       <%} %>
                       <%} %>
                      </hrms:menuitem>
                
                
            </logic:iterate>        
         
         <% } %> 
         
          </hrms:menuitem>
          
          <hrms:priv func_id="32109,37009,37109,37209,37309,33001009,33101009,2701509,0C34809,32009,325010109,324010109,2306709,23110209,32103,37003,37103,37203,37303,33001008,33101008,2701508,0C34808,32008,325010108,324010108,2306708,23110208,3800708,3800709"> 
           <% if(selectMan_flag.equalsIgnoreCase("true")){ %>
             <hrms:menuitem name="p1" label="<%=labelname %>"  function_id="" >
          <%  
          if(infor_type!=null&&infor_type.equals("1")){
           %>         
<logic:notEqual name="templateForm" property="no_priv_ctrl" value="1"> 
            <hrms:menuitem name="p11" label="手工选择" icon="" enabled="${selectMan_flag}" url="get_hand_query('${templateListForm.table_name}');"  function_id="32109,37009,37109,37209,37309,33001009,33001009,2701509,0C34809,32009,325010109,324010109,2306709,23110209,3800709" />  
</logic:notEqual>       
            <hrms:menuitem name="p12" label="简单查询" icon="" enabled="${selectMan_flag}"  url="get_common_query('${templateListForm.table_name}','${templateListForm.strpres}','1','');"  function_id="32109,37009,37109,37209,37309,33001009,33001009,2701509,0C34809,32009,325010109,324010109,2306709,23110209,3800709" />
            <hrms:menuitem name="p13" label="通用查询" icon="" enabled="${selectMan_flag}" url="get_common_query('${templateListForm.table_name}','${templateListForm.strpres}','2','');"  function_id="32109,37009,37109,37209,37309,33001009,33001009,2701509,0C34809,32009,325010109,324010109,2306709,23110209,3800709" />
            <hrms:menuitem name="p14" label="撤销选中人员" icon="" url="delete_obj_list('${templateListForm.table_name}','${templateListForm.tabid}','${templateListForm.ins_id}','1');"  function_id="32103,33001008,2701508,0C34808,32008,325010108,324010108,2306708,23110208,3800708" />
            <hrms:menuitem name="p15" label="撤销全部人员" icon="" url="delete_obj_list('${templateListForm.table_name}','${templateListForm.tabid}','${templateListForm.ins_id}','0');"  function_id="32103,33001008,2701508,0C34808,32008,325010108,324010108,2306708,23110208,3800708" />

         <% 
         }else if(infor_type!=null&&infor_type.equals("2")){
            %>        
<logic:notEqual name="templateForm" property="no_priv_ctrl" value="1"> 
            <hrms:menuitem name="p11" label="手工选择" icon="" enabled="${selectMan_flag}" url="get_hand_query('${templateListForm.table_name}');"  function_id="32109,33001009,2701509,0C34809,32009,325010109,324010109,2306709,23110209,3800709" />  
</logic:notEqual>
            <hrms:menuitem name="p12" label="简单查询" icon="" enabled="${selectMan_flag}"  url="get_common_query('${templateListForm.table_name}','${templateListForm.strpres}','1','');"  function_id="32109,37009,37109,37209,37309,33001009,2701509,0C34809,32009,325010109,324010109,2306709,23110209,3800709" />
            <hrms:menuitem name="p13" label="通用查询" icon="" enabled="${selectMan_flag}" url="get_common_query('${templateListForm.table_name}','${templateListForm.strpres}','2','');"  function_id="32109,37009,37109,37209,37309,33001009,2701509,0C34809,32009,325010109,324010109,2306709,23110209,3800709" />
            <hrms:menuitem name="p14" label="撤销选中机构" icon="" url="delete_obj_list('${templateListForm.table_name}','${templateListForm.tabid}','${templateListForm.ins_id}','1');"  function_id="32103,37003,37103,37203,37303,33001008,2701508,0C34808,32008,325010108,324010108,2306708,23110208,3800708" />
            <hrms:menuitem name="p15" label="撤销全部机构" icon="" url="delete_obj_list('${templateListForm.table_name}','${templateListForm.tabid}','${templateListForm.ins_id}','0');"  function_id="32103,37003,37103,37203,37303,33001008,2701508,0C34808,32008,325010108,324010108,2306708,23110208,3800708" />

         <% 
         }else if(infor_type!=null&&infor_type.equals("3")){
            %>   
<logic:notEqual name="templateForm" property="no_priv_ctrl" value="1">    
            <hrms:menuitem name="p11" label="手工选择" icon="" enabled="${selectMan_flag}" url="get_hand_query('${templateListForm.table_name}');"  function_id="32109,37009,37109,37209,37309,33001009,2701509,0C34809,32009,325010109,324010109,2306709,23110209,3800709" />  
</logic:notEqual>
            <hrms:menuitem name="p12" label="简单查询" icon="" enabled="${selectMan_flag}"  url="get_common_query('${templateListForm.table_name}','${templateListForm.strpres}','1','');"  function_id="32109,37009,37109,37209,37309,33001009,2701509,0C34809,32009,325010109,324010109,2306709,23110209,3800709" />
            <hrms:menuitem name="p13" label="通用查询" icon="" enabled="${selectMan_flag}" url="get_common_query('${templateListForm.table_name}','${templateListForm.strpres}','2','');"  function_id="32109,37009,37109,37209,37309,33001009,2701509,0C34809,32009,325010109,324010109,2306709,23110209,3800709" />
            <hrms:menuitem name="p14" label="撤销选中岗位" icon="" url="delete_obj_list('${templateListForm.table_name}','${templateListForm.tabid}','${templateListForm.ins_id}','1');"  function_id="32103,37003,37103,37203,37303,33001008,2701508,0C34808,32008,325010108,324010108,2306708,23110208,3800708" />
            <hrms:menuitem name="p15" label="撤销全部岗位" icon="" url="delete_obj_list('${templateListForm.table_name}','${templateListForm.tabid}','${templateListForm.ins_id}','0');"  function_id="32103,37003,37103,37203,37303,33001008,2701508,0C34808,32008,325010108,324010108,2306708,23110208,3800708" />

         <% 
         }
         %>
           
          </hrms:menuitem>
          <% } %>
          </hrms:priv>
         <hrms:priv func_id="33001018,33101018,2701518,0C34818,32018,32118,37018,37118,37218,37318,324010118,325010118,2306718,23110218,33001019,33101019,2701519,0C34819,32019,32119,37019,37119,37219,37319,324010119,325010119,2306719,23110219,32105,37005,37105,37205,37305,37005,37105,37205,37305,33001005,33101005,2701505,0C34805,32005,324010105,325010105,2306705,23110205,010724,32124,37024,37124,37224,37324,37024,37124,37224,37324,33001028,33101028,2701528,0C34828,32028,324010124,325010124,2306724,23110224,3800718,3800719,3800705,3800728"> 
          <hrms:menuitem name="p2" label="编辑"  function_id="" >
           <%
          if(infor_type!=null&&infor_type.equals("1")){
           %> 
            <hrms:menuitem name="p21" label="label.gz.condfilter" icon="" url="to_person_filter('${templateListForm.tabid}')"  function_id="33001018,33101018,2701518,0C34818,32018,32118,37018,37118,37218,37318,324010118,325010118,2306718,23110218,3800718" />  
             <%
          }
           %> 
            <%
          if(infor_type!=null&&infor_type.equals("2")){
           %> 
            <hrms:menuitem name="p21" label="label.gz.zzfilter" icon="" url="to_person_filter('${templateListForm.tabid}')"  function_id="33001018,33101018,2701518,0C34818,32018,32118,37018,37118,37218,37318,324010118,325010118,2306718,23110218,3800718" />  
             <%
          }
           %> 
            <%
          if(infor_type!=null&&infor_type.equals("3")){
           %> 
            <hrms:menuitem name="p21" label="label.gz.gwfilter" icon="" url="to_person_filter('${templateListForm.tabid}')"  function_id="33001018,33101018,2701518,0C34818,32018,32118,37018,37118,37218,37318,324010118,325010118,2306718,23110218,3800718" />  
             <%
          }
           %> 
            <hrms:menuitem name="p22" label="批量修改单个指标" icon="" url="batch_update('${templateListForm.tabid}')"  function_id="33001019,33101019,2701519,0C34819,32019,32119,37019,37119,37219,37319,324010119,325010119,2306719,23110219,3800719" />
            <hrms:menuitem name="p22" label="批量修改多个指标" icon="" url="batch_update_fields('${templateListForm.tabid}')"  function_id="33001019,33101019,2701519,0C34819,32019,32119,37019,37119,37219,37319,324010119,325010119,2306719,23110219,3800719" />
            <hrms:menuitem name="p23" label="批量计算" icon="" url="bz_computer_list('${templateListForm.tabid}','${templateListForm.ins_id}');"  function_id="32105,37005,37105,37205,37305,33001005,33101005,2701505,0C34805,32005,324010105,325010105,2306705,23110205,3800705" />
            <logic:equal name="templateListForm" property="sequence" value="1"> 
            <hrms:menuitem name="p23" label="生成序号" icon="" url="filloutSequence();"  function_id="010724,32124,37024,37124,37224,37324,33001028,33101028,2701528,0C34828,32028,324010124,325010124,2306724,23110224,3800728" />
            </logic:equal>
          </hrms:menuitem>
         </hrms:priv>
          
          
          <hrms:menuitem name="p3" label="显示"  function_id="" >
            <hrms:menuitem name="p31" label="显示/隐藏指标" icon="" url="payrollViewHide('${templateListForm.tabid}','');" command=""  function_id=""  />  
        <hrms:menuitem name="p32" label="调整指标顺序" icon="" url="payrollSort('${templateListForm.tabid}')"  function_id="" />
        <hrms:menuitem name="p33" label="锁定指标" icon="" url="payrollLock('${templateListForm.tabid}')"  function_id="" />
        <logic:equal name="templateListForm" property="infor_type" value="1"> 
         <hrms:menuitem name="p34" label="人员排序" icon="" url="to_sort_emp1('${templateListForm.tabid}');"  function_id="" />
        </logic:equal>
        <logic:equal name="templateListForm" property="infor_type" value="2"> 
         <hrms:menuitem name="p34" label="机构排序" icon="" url="to_sort_emp1('${templateListForm.tabid}');"  function_id="" />
        </logic:equal>
        <logic:equal name="templateListForm" property="infor_type" value="3"> 
         <hrms:menuitem name="p34" label="岗位排序" icon="" url="to_sort_emp1('${templateListForm.tabid}');"  function_id="" />
        </logic:equal>
       
        <hrms:menuitem name="p35" label="对照" icon=""  enabled="${isShowCompare}"  checked="${isCompare_str}" url="setShowModel()"  function_id="" />
        <%if(_static!=null&&_static.equals("1")&&versionFlag==1){ %>
        <hrms:menuitem name="p35" label="历史数据" icon=""     url="showHistory2('${templateListForm.tabid}','${templateForm.type}','${templateForm.res_flag}')"  function_id="32125,37025,37125,37225,37325,33001026,32026,324010123,325010123,2306723,23110223,010723" />
          <%} %>
          </hrms:menuitem>
          <hrms:menuitem name="p4" label="设置"  function_id="" >
            <hrms:menuitem name="p41" label="设置业务日期" icon="" url="setapp_date();"  function_id="" />  
        <% if(infor_type.equals("1")){ %>
            <hrms:menuitem name="p42" label="临时变量" icon="" url="setTempVar('${templateListForm.tabid}');"  function_id="32110,37010,37110,37210,37310,33001016,33101016,2701516,0C34816,32016,324010116,325010116,2306716,23110216,3800716" />
        <% }else if(infor_type.equals("2")){ %>
            <hrms:menuitem name="p42" label="临时变量" icon="" url="setTempVar('${templateListForm.tabid}');"  function_id="32110,37010,37110,37210,37310,33001016,33101016,2701516,0C34816,32016,324010116,325010116,2306716,23110216,3800716" />
        <%}else if(infor_type.equals("3")){ %>
            <hrms:menuitem name="p42" label="临时变量" icon="" url="setTempVar('${templateListForm.tabid}');"  function_id="32110,37010,37110,37210,37310,33001016,33101016,2701516,0C34816,32016,324010116,325010116,2306716,23110216,3800716" />
        <% }%>
            <hrms:menuitem name="p43" label="计算公式" icon="" url="setFormula('${templateListForm.tabid}');"  function_id="32111,37011,37111,37211,37311,33001004,33101004,2701504,0C34804,32004,324010104,325010104,2306704,23110204,3800704" />
            <hrms:menuitem name="p44" label="校验公式" icon="" url="chkFormula('${templateListForm.tabid}');"  function_id="32106,37006,37106,37206,37306,33001006,33101006,2701506,0C34806,32006,324010106,325010106,2306706,23110206,3800706" />
          </hrms:menuitem>
        </hrms:menubar>
    </td></tr>
    </table>
</td></tr>
<tr><td>
 <table style="margin-left:-2px;">
   <tr>
   <td>
   <logic:equal name="templateListForm" property="ins_id" value="0">
   <logic:equal name="templateListForm" property="operationtype" value="8">  
    <logic:notEqual name="templateListForm" property="infor_type" value="1">  
            <hrms:priv func_id=""> 
        
            <button extra="button" id="mybutton" onclick='combine();'  allowPushDown="false" down="false">合并</button>
            </hrms:priv>
        </logic:notEqual>
        </logic:equal>
         <logic:equal name="templateListForm" property="operationtype" value="9">  
            <hrms:priv func_id=""> 
        
            <button extra="button" id="mybutton" onclick='combine();'  allowPushDown="false" down="false">划转</button>
            </hrms:priv>
        </logic:equal>
        </logic:equal>
    <logic:equal name="templateListForm" property="ins_id" value="0">       
        <logic:equal name="templateListForm" property="operationtype" value="0">  
            <hrms:priv func_id="32107,37007,37107,37207,37307,33001007,33101007,2701507,0C34807,32007,325010107,324010107,2306707,23110207,3800707"> 
        
            <button extra="button" id="mybutton" onclick='add_newobj_list("${templateListForm.table_name}","${templateListForm.tabid}");'  allowPushDown="false" down="false"><bean:message key="button.insert"/></button>
            </hrms:priv>
        </logic:equal>
        <logic:equal name="templateListForm" property="operationtype" value="5">  
            <hrms:priv func_id="32107,37007,37107,37207,37307,33001007,33101007,2701507,0C34807,32007,325010107,324010107,2306707,23110207,3800707"> 
        
            <button extra="button" id="mybutton" onclick='add_newobj_list("${templateListForm.table_name}","${templateListForm.tabid}");'  allowPushDown="false" down="false"><bean:message key="button.insert"/></button>
            </hrms:priv>
        </logic:equal>
    </logic:equal>
    <logic:notEqual name="templateListForm" property="task_state" value="5"> 
        <hrms:priv func_id="010730,32103,37003,37103,37203,37303,33001008,2701508,0C34808,32008,325010108,324010108,2306708,23110208,3800708"> 
             <button extra="button" id="abolishButton" onclick='delete_obj_list("${templateListForm.table_name}","${templateListForm.tabid}","${templateListForm.ins_id}","1");'  allowPushDown="false" down="false"><bean:message key="button.abolish"/></button>

        </hrms:priv>
    </logic:notEqual>
    
    <logic:equal name="templateListForm" property="sp_ctrl" value="1"> <!-- 需要审批 -->
          <logic:notEqual name="templateListForm" property="ins_id" value="0"><!-- 审批流程 --> 
		        <logic:equal name="templateListForm" property="isDisSubMeetingButton" value="1"> 
			    	<hrms:priv func_id="3800733" >
			        	<button extra="button"  class="mybutton" onclick='subMeeting()' ><bean:message key="t_template.jobtitle.submeeting" /> </button>
					</hrms:priv>        	
	        	</logic:equal>                                               
         </logic:notEqual> 
         <logic:equal name="templateListForm" property="sp_mode" value="1"><!-- 手动流转 -->           
                    <logic:equal name="templateListForm" property="ins_id" value="0"><!-- 发起流程 -->                          
                        <button extra="button" id="mybutton" onclick='apply_list("${templateListForm.table_name}","${templateListForm.sp_mode}")'  allowPushDown="false" down="false"><bean:message key="button.appeal"/></button>
                    <hrms:priv func_id="33001030,33101030,2701530,0C34830,32030,32126,37026,37126,37226,37226,324010130,325010130,010731,2306725,23110225,3800730">   
                       <logic:equal name="templateListForm" property="def_flow_self" value="1">       
                            <button extra="button" 
                             onclick="showDefFlowSelf('${templateListForm.tabid}');"><bean:message key="t_template.approve.selfdefflow"/></button>
                       </logic:equal>
                    </hrms:priv>
                    </logic:equal>
                    <logic:notEqual name="templateListForm" property="ins_id" value="0"><!-- 审批流程 -->  
                        <logic:equal name="templateListForm" property="isFinishTask" value="0">                                     
                            <button extra="button" id="applyButton" onclick='assign_list();'  allowPushDown="false" down="false"><bean:message key="button.apply"/></button>
                        </logic:equal>
                        <logic:equal name="templateListForm" property="isFinishTask" value="1"> 
                             <button extra="button" id="submitButton" onclick='finishedTask("${templateListForm.ins_id}","${templateListForm.task_id}")'  allowPushDown="false" down="false"><bean:message key="button.submit"/></button>   
                        </logic:equal>                                                  
                    </logic:notEqual> 
         </logic:equal>
         <logic:equal name="templateListForm" property="sp_mode" value="0"> <!-- 自动流转 -->
                    <logic:equal name="templateListForm" property="ins_id" value="0"> <!-- 发起流程 -->         
                        <button extra="button" id="applyButton" onclick='apply_list("${templateListForm.table_name}","${templateListForm.sp_mode}");'  allowPushDown="false" down="false">${templateListForm.applyobj}</button>
                    </logic:equal>  
                    <logic:notEqual name="templateListForm" property="ins_id" value="0"> <!-- 审批流程 -->                      
                         <logic:equal name="templateListForm" property="isFinishTask" value="0">    
                           <logic:equal name="templateListForm" property="startflag" value="0"> 
                                
                                <button extra="button" id="rejectButton" onclick='assign_list("2",this)'  allowPushDown="false" down="false"><bean:message key="button.reject"/></button>   
                                
                           </logic:equal>                             
                                <button extra="button" id="applyButton" onclick='assign_list("1",this)'  allowPushDown="false" down="false">${templateListForm.applyobj}</button>  
                         </logic:equal>
                         <logic:equal name="templateListForm" property="isFinishTask" value="1"> 
                                <button extra="button" id="submitButton" onclick='finishedTask("${templateListForm.ins_id}","${templateListForm.task_id}")'  allowPushDown="false" down="false"><bean:message key="button.submit"/></button>    
                         </logic:equal>       
                    </logic:notEqual>  
         </logic:equal>
         <% if(!(sp_batch!=null&&sp_batch.equals("1"))){ %>
         <logic:notEqual  name="templateListForm" property="ins_id" value="0">      
            <button extra="button" id="mybutton" onclick='open_showyj()'  allowPushDown="false" down="false">审批过程</button>  
         </logic:notEqual>
         <% } %>
    </logic:equal>           
   
    <logic:notEqual name="templateListForm" property="sp_ctrl" value="1">
     <logic:equal name="templateListForm" property="ins_id" value="0">
         <button extra="button" onclick='submitData("${templateListForm.table_name}","${templateListForm.tabid}");'><bean:message key="button.submit"/></button>
     </logic:equal>
    </logic:notEqual> 
    <hrms:priv func_id='33001024,33101024,2701524,0C34824,32024,32122,37022,37122,37222,37322,324010121,325010121,2306721,23110221,3800724'>
                                <button extra="button" id="mybutton" onclick='downLoadTemp("${templateListForm.tabid}")'  allowPushDown="false" down="false">
                                <bean:message key="button.download.template"/></button>
    </hrms:priv>
    <hrms:priv func_id='33001025,33101025,2701525,0C34825,32025,32123,37023,37123,37223,37323,324010122,325010122,2306722,23110222,3800725'>
                                <button extra="button" id="mybutton" onclick='exportTempData("${templateListForm.tabid}")'  allowPushDown="false" down="false">
                                <bean:message key="import.tempData"/></button>
    </hrms:priv>
    
    
    
    <button extra="button" id="cardbutton" onclick='showCard2("${templateListForm.ins_id}","${templateListForm.task_id}")'  allowPushDown="false" down="false"><bean:message key="button.card"/></button>   
    
    <logic:equal name="templateListForm" property="returnflag" value="7">    
        <button extra="button" id="mybutton" onclick='returnHome()'  allowPushDown="false" down="false"><bean:message key="button.return"/></button>                
    </logic:equal>
    <logic:equal name="templateListForm" property="returnflag" value="8">    
        <button extra="button" id="mybutton" onclick='returnHome()'  allowPushDown="false" down="false"><bean:message key="button.return"/></button>                
    </logic:equal>
    <logic:equal name="templateListForm" property="returnflag" value="listhome">    
        <button extra="button" id="mybutton" onclick='returnHome()'  allowPushDown="false" down="false"><bean:message key="button.return"/></button>                
    </logic:equal>
    <logic:equal name="templateListForm" property="returnflag" value="3">    
        <button extra="button" id="mybutton" onclick='returnHome()'  allowPushDown="false" down="false"><bean:message key="button.return"/></button>                
    </logic:equal>
        <logic:equal name="templateListForm" property="returnflag" value="6">    
        <button extra="button" id="mybutton" onclick='returnHome()'  allowPushDown="false" down="false"><bean:message key="button.return"/></button>                
    </logic:equal>
    <logic:equal name="templateListForm" property="returnflag" value="warnhome">
        <button extra="button" id="mybutton" onclick='returnWarnHome()'  allowPushDown="false" down="false"><bean:message key="button.return"/></button>                
    </logic:equal>

</td>
<%if(!operationtype.equals("0")&&!operationtype.equals("5")){ %>
 <td nowrap>&nbsp;[&nbsp;
  </td>
  
  <td nowrap id="vieworhidd"> 
                          <a href="javascript:showOrClose();"> 
                              <logic:equal name="templateListForm" property="isShowCondition" value="none" >查询显示</logic:equal>   
                              <logic:equal name="templateListForm" property="isShowCondition" value="block" >查询隐藏</logic:equal>   
                          </a>
  </td>      
              
  <td nowrap>&nbsp;]&nbsp;&nbsp;&nbsp;&nbsp;
  </td>
   <%} %>    
         </tr>
        </table>
        </td> 
</tr>
</table>
<html:hidden name="templateListForm" property="no_sp_yj"/>
<table width="99%" border="0" cellspacing="0"  align="center" cellpadding="0" id='aa' style='display:${templateListForm.isShowCondition}' style="margin-left:-1px;margin-right:5px;">
  <tr>
   <td>
     <!-- 查询开始 -->
     <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="RecordRow">
       <tr class="trShallow1">
          <td align="center" colspan="4" height='20' class="RecordRow" nowrap>
            <bean:message key="label.query.inforquery"/><!-- 请选择查询条件! -->
          </td>
       </tr>
       <logic:iterate id="element" name="templateListForm"  property="queryfieldlist" indexId="index">  
         <%
                FieldItem item=(FieldItem)pageContext.getAttribute("element");
                        Field field=item.cloneField();
                        
                %>          
           <!-- 时间类型 -->
          <logic:equal name="element" property="itemtype" value="D">
               <% 
                  if(flag2==0)
                  {
                   //out.println("<tr class=\"trShallow1\">");
                   out.println("<tr>");
                         flag2=1;          
                  }else{
                       flag2=0;           
                  }
               %>  
              <td align="right" height='28' nowrap>
                   <bean:write  name="element" property="itemdesc" filter="true"/>&nbsp;
              </td>
              <td align="left"  nowrap>
                  <html:text name="templateListForm" property='<%="queryfieldlist["+index+"].value"%>' size="13" maxlength="10" styleClass="text4" title="输入格式：2008.08.08" onclick=""/>
                  <bean:message key="label.query.to"/>
                  <html:text name="templateListForm" property='<%="queryfieldlist["+index+"].viewvalue"%>' size="13" maxlength="10" styleClass="text4" title="输入格式：2008.08.08"  onclick=""/>
                      <!-- 没有什么用，仅给用户与视觉效果-->
                  <INPUT type="radio" name="${element.itemid}"  checked="true"><bean:message key="label.query.age"/>    
                  <INPUT type="radio" name="${element.itemid}" id="day"><bean:message key="label.query.day"/>
              </td>
              <%
                 if(flag2==0)
                    out.println("</tr>");
              %>   
          </logic:equal>
          <logic:equal name="element" property="itemtype" value="M">
               <% 
                  if(flag2==0)
                  {
                   //out.println("<tr class=\"trShallow1\">");
                   out.println("<tr>");
                         flag2=1;          
                  }else{
                       flag2=0;           
                  }
              %> 
              <td align="right" height='28' nowrap>
                   <bean:write  name="element" property="itemdesc" filter="true"/>&nbsp;
              </td>
              <td align="left"  nowrap>
                  <html:text name="templateListForm" property='<%="queryfieldlist["+index+"].value"%>' size="31" maxlength='<%="queryfieldlist["+index+"].itemlength"%>' styleClass="text4"/>
              </td>
              <%
                 if(flag2==0)
                    out.println("</tr>");
              %> 
          </logic:equal> 
           <logic:equal name="element" property="itemtype" value="N">   
              <% 
                  if(flag2==0)
                  {
                   //out.println("<tr class=\"trShallow1\">");
                   out.println("<tr>");
                         flag2=1;          
                  }else{
                       flag2=0;           
                  }
              %> 
              <td align="right" height='28' nowrap>
                   <bean:write  name="element" property="itemdesc" filter="true"/>&nbsp;
              </td>
             <td align="left"  nowrap> 
              <html:text name="templateListForm" property='<%="queryfieldlist["+index+"].value"%>' size="31" maxlength="${element.itemlength}" styleClass="text4"/> 
             </td>
              <%
                 if(flag2==0)
                    out.println("</tr>");
              %> 
              
           </logic:equal>
           <logic:equal name="element" property="itemtype" value="A">
              <logic:notEqual name="element" property="codesetid" value="0">              
                  <logic:equal name="element" property="codesetid" value="UN">
                     <%
                       if(flag2==0)
                       {
                          // out.println("<tr class=\"trShallow1\">");
                           out.println("<tr>");
                           flag2=1;          
                       }else{
                            flag2=0;           
                       }
                      %> 
                      <td align="right" height='28' nowrap>
                        <bean:write  name="element" property="itemdesc" filter="true"/>&nbsp;
                     </td>
                     <td align="left" nowrap>
                       <html:hidden name="templateListForm" property='<%="queryfieldlist["+index+"].value"%>' styleClass="text"/>                               
                       <html:text name="templateListForm" property='<%="queryfieldlist["+index+"].viewvalue"%>' size="31" maxlength="50" styleClass="text4" onchange="fieldcode(this,2);"/>
                       <img src="/images/code.gif" onclick="openInputCodeDialog('${element.codesetid}','<%="queryfieldlist["+index+"].viewvalue"%>','0');" align="absmiddle"/> 
                    </td>
                     <%
                       if(flag2==0)
                     out.println("</tr>");
                     %>                                  
                   </logic:equal>                          
                   <logic:equal name="element" property="codesetid" value="UM">
                       <%
                       if(flag2==0)
                       {
                           //out.println("<tr class=\"trShallow1\">");
                           out.println("<tr>");
                           flag2=1;          
                       }else{
                            flag2=0;           
                       }
                      %>  
                      <td align="right" height='28' nowrap>
                        <bean:write  name="element" property="itemdesc" filter="true"/>&nbsp;
                      </td>
                      <td align="left" nowrap>
                       <html:hidden name="templateListForm" property='<%="queryfieldlist["+index+"].value"%>' styleClass="text"/>                               
                       <html:text name="templateListForm" property='<%="queryfieldlist["+index+"].viewvalue"%>' size="31" maxlength="50" styleClass="text4" onchange="fieldcode(this,2);"/>
                       <img src="/images/code.gif" onclick="openInputCodeDialog('${element.codesetid}','<%="queryfieldlist["+index+"].viewvalue"%>','0');" align="absmiddle"/>  
                    </td>
                     <%
                       if(flag2==0)
                     out.println("</tr>");
                     %>           
                   </logic:equal>
                   <logic:equal name="element" property="codesetid" value="@K">
                       <%
                       if(flag2==0)
                       {
                           //out.println("<tr class=\"trShallow1\">");
                           out.println("<tr>");
                           flag2=1;          
                       }else{
                            flag2=0;           
                       }
                      %>  
                      <td align="right" height='28' nowrap>
                        <bean:write  name="element" property="itemdesc" filter="true"/>&nbsp;
                      </td>
                      <td align="left" nowrap>
                       <html:hidden name="templateListForm" property='<%="queryfieldlist["+index+"].value"%>' styleClass="text"/>                               
                       <html:text name="templateListForm" property='<%="queryfieldlist["+index+"].viewvalue"%>' size="31" maxlength="50" styleClass="text4" onchange="fieldcode(this,2);"/>
                       <img src="/images/code.gif" onclick="openInputCodeDialog('${element.codesetid}','<%="queryfieldlist["+index+"].viewvalue"%>','0');" align="absmiddle"/>
                    </td>
                     <%
                       if(flag2==0)
                     out.println("</tr>");
                     %>           
                   </logic:equal>
                   <logic:notEqual name="element" property="codesetid" value="UN">
                      <logic:notEqual name="element" property="codesetid" value="UM">
                         <logic:notEqual name="element" property="codesetid" value="@K">
                             <logic:greaterThan name="element" property="itemlength" value="20">
                               <!-- 大于 -->
                                <%
                                 if(flag2==0)
                                 {
                                   //out.println("<tr class=\"trShallow1\">");
                                   out.println("<tr>");
                                   flag2=1;          
                                 }else{
                                   flag2=0;           
                                 }
                                %>  
                                <td align="right" height='28' nowrap>
                                  <bean:write  name="element" property="itemdesc" filter="true"/>&nbsp;
                                </td>
                                <td align="left" nowrap>
                                  <html:hidden name="templateListForm" property='<%="queryfieldlist["+index+"].value"%>' styleClass="text"/>                               
                                  <html:text name="templateListForm" property='<%="queryfieldlist["+index+"].viewvalue"%>' size="31" maxlength="50" styleClass="text4" onchange="fieldcode(this,2);"/>
                                  <img src="/images/code.gif" onclick="openInputCodeDialog('${element.codesetid}','<%="queryfieldlist["+index+"].viewvalue"%>','0');" align="absmiddle"/>
                                </td>
                               <%
                                if(flag2==0)
                                out.println("</tr>");
                                %>         
                             </logic:greaterThan>
                             <logic:lessEqual  name="element" property="itemlength" value="20">
                               <!-- 小于等于 -->
                                 <%
                                   if(flag2==1)
                        {
                          out.println("<td colspan=\"2\">");
                                      out.println("</td>");
                                      out.println("</tr>");
                        }
                    %>      
                    <tr >
                      <td align="right" height='28' nowrap>       
                                <bean:write  name="element" property="itemdesc" filter="true"/>&nbsp;
                                <html:hidden name="templateListForm" property='<%="queryfieldlist["+index+"].value"%>' styleClass="text"/>                               
                             </td> 
                                <td align="left" colspan="3" nowrap>
                                   <table width="100%" border="0" cellspacing="0"  align="left" cellpadding="0" style="margin-left: -12px;">
                                     <tr>
                                      <td>
                                         <!--checkbox-->                                         
                                         <hrms:codesetmultiterm codesetid="${element.codesetid}" itemid="${element.itemid}" itemvalue="${element.value}" rownum="6" hiddenname='<%="queryfieldlist["+index+"].value"%>'/>
                           </td>
                                    </tr> 
                                </table> 
                            </td>
                            </tr>
                             <%flag2=0;%>
                             </logic:lessEqual>
                         </logic:notEqual>
                      </logic:notEqual>
                   </logic:notEqual>
              </logic:notEqual>
              <logic:equal name="element" property="codesetid" value="0">
              
                                                              
               <% 
                  if(flag2==0)
                  {
                  // out.println("<tr class=\"trShallow1\">");
                   out.println("<tr>");
                         flag2=1;          
                  }else{
                       flag2=0;           
                  }
              %> 
                 <!-- 特殊处理 -->    
              <%if(item.getItemid().equals("codesetid_1")) {%>
             
              <td align="right" height='10' nowrap>
                   <bean:write  name="element" property="itemdesc" filter="true"/>:
              </td>
         <td align="left" valign="center" nowrap>
            <html:select name="templateListForm" property='<%="queryfieldlist["+index+"].value"%>' styleClass="text6">
                <html:optionsCollection property="codesetidlist" value="dataValue" label="dataName" />
            </html:select>
             </td>
              <%
                 if(flag2==0)
                    out.println("</tr>");
              %> 
              <%}else{ %>                        
              
             <td align="right" height='28' nowrap>
                   <bean:write  name="element" property="itemdesc" filter="true"/>&nbsp;
              </td>
              <td align="left"  nowrap>
               <html:text name="templateListForm" property='<%="queryfieldlist["+index+"].value"%>' size="31" maxlength="${element.itemlength}" styleClass="text4"/>
              </td>
              <%
                 if(flag2==0)
                    out.println("</tr>");
              %> 
             <%} %>
            
            </logic:equal>             
         </logic:equal>
       </logic:iterate>
        <%
         if(flag2==1)
        {
             out.println("<td colspan=\"2\">");
             out.println("</td>");
             out.println("</tr>");
        }
        %> 
        <tr >
          <td align="right" height='20'  nowrap>
            
             <bean:message key="label.query.like"/>&nbsp; 
            
          </td>
          <td align="left" colspan="3" height='20' nowrap>
            <table width="100%" border="0" cellspacing="0" cellpadding="0" style="margin-left: -2px;">
              <tr>
                <td>
                   <logic:equal name="templateListForm" property="querylike" value="1">
                    <input type="checkbox" name="querlike2" value="true" style="margin-left: -2px;" onclick="selectCheckBox(this,'querylike');" checked>
                  </logic:equal>  
                  <logic:notEqual name="templateListForm" property="querylike" value="1">
                    <input type="checkbox" name="querlike2" value="true" style="margin-left: -2px;" onclick="selectCheckBox(this,'querylike');">
                  </logic:notEqual>
                   <html:hidden name="templateListForm" property='querylike' styleClass="text"/>&nbsp;
                </td>
                <td>                
                  
                      <div  id="info_cue1" style='display:none;' class="query_cue1">
                       <bean:message key="infor.menu.query.cue1"/>
                      </div>
                  
                  
                </td>
              </tr>
            </table>
          </td>       
        </tr>
        
     </table>
   </td>
  </tr>
    <tr>
      <td height="5">
      </td>
    </tr>
    <tr>
          <td align="center" colspan="4" height='20'  nowrap>          
            <Input type='button' value="<bean:message key="infor.menu.query"/>" onclick="query('${templateListForm.tabid}','1');" class='mybutton' style="margin-bottom:5px;" />  &nbsp;&nbsp; 
            <Input type='button' value="<bean:message key="button.sys.cond"/>" onclick="query('${templateListForm.tabid}','2');" class='mybutton' style="margin-bottom:5px;" />  &nbsp;&nbsp;
          </td>
    </tr>
 </table>
     <script language='javascript' >
        document.write("<div id=\"tbl-container\"  style='position:absolute;left:5;height:"+(document.body.clientHeight-102)+";width:100%;' >");
    </script>   
     
            <table width="100%" border="0" cellspacing="0"    align="center" cellpadding="0" >
                  <thead>
                    <tr>
                   <td align="center"   class='t_cell_locked2 common_background_color common_border_color'     height="25"   nowrap >
                         &nbsp;<input style="margin-left: 4px;"  type="checkbox" name="selbox"  onclick='selectAllRecord()'   <%=(isSelectAll.equals("1")?"checked":"")%>   /> &nbsp;
                   </td>  
                   <% 
                        if((infor_type.equals("3")&&operationtype.equals("8"))||((infor_type.equals("2"))&&(operationtype.equals("8")||operationtype.equals("9")))){

                    %>
                  <td align="center"   class='t_cell_locked2 common_background_color common_border_color '     height="25"   nowrap >
                         &nbsp;组号&nbsp;
                   </td>  
                   <%}else{ %> 
                    <td align="center"   class='t_cell_locked2 common_background_color common_border_color '     height="25"   nowrap >
                         &nbsp;序号&nbsp;
                   </td>  
                    <% }
                        if(lockBean==null||lockBean.get("field_name")==null||lockBean.get("chgstate")==null){

                    %>
                         <td align="center"   class='t_cell_locked2 common_background_color common_border_color '     height="25"   nowrap >
                           &nbsp;&nbsp;编辑&nbsp;&nbsp;
                         </td>  
                                        
        
                    <%
                    
                        }
                     int  showDesc_index=-1; // 显示 变换前和变化后列下标
                    
                     LazyDynaBean abean=null;
                     for(int i=0;i<tableHeadSetList.size();i++){ 
                            abean=(LazyDynaBean)tableHeadSetList.get(i);
                            String hz=((String)abean.get("hz")).replaceAll("`","");
                            String isLock=(String)abean.get("isLock");
                            String chgstate=(String)abean.get("chgstate");
                            String field_name=(String)abean.get("field_name");
                            String classname="t_header_locked common_border_color";
                            if(isLock.equals("1"))
                                classname="t_cell_locked2 common_border_color";
                            
                            
                            if(isCompare.equals("1")&&showDesc_index==-1&&chgstate.equals("2"))
                            {
                                showDesc_index=i;
                            %>
                                <td align="center"   class='<%=classname%>  common_border_color '     height="25"   nowrap >
                                   &nbsp;&nbsp;
                                </td>   
                            
                            <%
                            }       
                    %>
                    
                     <td align="center"   class='<%=classname%>  common_border_color '     height="25"   nowrap >
                       &nbsp;&nbsp;&nbsp; <%=hz%>&nbsp;&nbsp;&nbsp;
                     </td>         
                    
                    <% 
                        if(lockBean!=null&&lockBean.get("field_name")!=null&&lockBean.get("field_name").equals(field_name)&&lockBean.get("chgstate").equals(chgstate)&&lockBean.get("gridno").equals(abean.get("gridno"))&&lockBean.get("pageid").equals(abean.get("pageid"))){

                    %>
                         <td align="center"   class='t_cell_locked2  common_border_color '     height="25"   nowrap >
                           &nbsp;&nbsp;编辑&nbsp;&nbsp;
                         </td>  
                    <%                  
        
                        }
                        }
                     %>
                                                                            
                       </tr>
                  </thead>
                  <%
                   int seq=0;
                   LazyDynaBean _abean=null;
                   
                   
                   %>    
                  <hrms:extenditerate id="element" name="templateListForm" property="templatelistform.list" indexes="indexes"  pagination="templatelistform.pagination" pageCount="26" scope="session">
                 
                     <%
                      _abean=(LazyDynaBean)pageContext.getAttribute("element");
                          String  a0100=(String)_abean.get("a0100");
                      String  basepre=(String)_abean.get("basepre");
                      String seqnum=(String)_abean.get("seqnum");
                      if(basepre==null||basepre.toLowerCase().equals("null"))
                      basepre="";
                      String  ins_id=(String)_abean.get("ins_id");
                      String  task_id=(String)_abean.get("task_id");
                      String  submitflag=(String)_abean.get("submitflag");
                      String color = "";
                      if(_abean.get("color")!=null){
                      color="style=\"background-color:#FFF8D2;\"";
                      }
                     for(int j=0;j<2;j++){
                      if(isCompare.equals("1")){
                     if(j==0){
                      _abean=(LazyDynaBean)prechangemap.get(basepre.toLowerCase()+a0100);
                     }else{
                       _abean=(LazyDynaBean)pageContext.getAttribute("element");
                     }
                     }else{
                      _abean=(LazyDynaBean)pageContext.getAttribute("element");
                     if(j==1)
                     break;
                     }
                     
                 
                        a0100=(String)_abean.get("a0100");
                        basepre=(String)_abean.get("basepre");
                        ins_id=(String)_abean.get("ins_id");
                        task_id=(String)_abean.get("task_id");
    //                  submitflag=(String)_abean.get("submitflag");
                    
                    
                  
                      if(seq%2==0)
                      {
                      %>
                      <tr class="trShallow  common_border_color "  <%=color %>  onclick='tr_onclick(this,"#F3F5FC");setCurrent("<%=a0100%>","<%=basepre%>")' >
                      <%}
                      else
                      {%>
                      <tr class="trDeep  common_border_color "  <%=color %>  onclick='tr_onclick(this,"#E4F2FC");setCurrent("<%=a0100%>","<%=basepre%>")'>
                      <%
                      }
                     
                      
                      String _classname="";
                      if((dataSize==(seq+1)&&isCompare.equals("0"))||(dataSize==(seq+2)&&isCompare.equals("1")))
                                    _classname="t_cell_locked_b  common_border_color ";
                      else
                                    _classname="t_cell_locked   common_border_color ";
                                    
                       
                       if(isCompare.equals("0"))
                       {
                         out.println("<td align='center'   class='"+_classname+"' "+color+"   height='25'   nowrap >");
                         out.print("<input type='checkbox' ");
                         
                         out.print(" onclick=\"setObjectStatelist(this,'"+basepre+"','"+a0100+"','"+ins_id+"','"+table_name+"','"+seqnum+"','"+task_id+"')\" ");
                         
                         out.print((submitflag.equals("1")?"checked":"")+"    name='box' />");
                         out.println("</td>");
                         out.println("<td align='center'   class='"+_classname+"'   "+color+"   height='25'   nowrap >");
                         out.print((String)_abean.get("num"));
                         out.print("&nbsp;");
                         out.println("</td>");
                        
                        if(lockBean==null||lockBean.get("field_name")==null||lockBean.get("chgstate")==null){
                        out.println("<td align='center'   class='"+_classname+"' "+color+" height='25'   nowrap > &nbsp;&nbsp;<img src='/images/edit.gif'  style=\"cursor:hand\"    onclick=\"showCard('"+ins_id+"','"+task_id+"','"+a0100+"','"+basepre+"')\"   border=0>&nbsp;&nbsp;</td>");  
                        }
                       }
                       else
                       {
                            if(seq%2==0)
                            {
                                 out.println("<td align='center'   class='"+_classname+"' "+color+"  rowspan='2'   height='25'   nowrap >");
                                 out.print("<input type='checkbox'  "+(submitflag.equals("1")?"checked":"")+" ");
                                 out.print(" onclick=\"setObjectStatelist(this,'"+basepre+"','"+a0100+"','"+ins_id+"','"+table_name+"','"+seqnum+"','"+task_id+"')\" ");
                                 out.print(" name='box' />");
                                 out.println("</td>");
                                 out.println("<td align='center'   class='"+_classname+"' "+color+"   rowspan='2'    height='25'   nowrap >");
                                 out.print((String)_abean.get("num"));
                                 out.println("</td>");
                               if(lockBean==null||lockBean.get("field_name")==null||lockBean.get("chgstate")==null){
                                out.println("<td align='center'   class='"+_classname+"' "+color+"  height='25'  rowspan='2'  nowrap > &nbsp;&nbsp;<img src='/images/edit.gif'  style=\"cursor:hand\"    onclick=\"showCard('"+ins_id+"','"+task_id+"','"+a0100+"','"+basepre+"')\"   border=0>&nbsp;&nbsp;</td>");  
                                    }
                            }
                            else
                            {
            //                out.println("<td></td>");
                            
                            }
                              
                       
                       }            
                      
                      
                      
                                 
                  
                     for(int i=0;i<tableHeadSetList.size();i++){ 
                            abean=(LazyDynaBean)tableHeadSetList.get(i);
                            String hz=((String)abean.get("hz")).replaceAll("`","");
                            String isLock=(String)abean.get("isLock");
                            String chgstate=(String)abean.get("chgstate");
                            String field_name=(String)abean.get("field_name");
                            String field_type=(String)abean.get("field_type");
                            String subflag=(String)abean.get("subflag");
                            String setname=(String)abean.get("setname");
                            
                            String hismode="";
                            if(abean.get("hismode")!=null)
                                hismode=(String)abean.get("hismode");
                            String mode="";
                            if(abean.get("mode")!=null)
                                mode=(String)abean.get("mode");
                            
                            String classname="RecordRow3 common_border_color";
                            String isvar=(String)abean.get("isvar");
                            String align="left";
                            String sub_domain_id = "";
                            if(abean.get("sub_domain_id")!=null&&"1".equals(abean.get("chgstate"))){
                                sub_domain_id = (String)abean.get("sub_domain_id");
                            if(sub_domain_id!=null&&sub_domain_id.length()>0){
                                sub_domain_id ="_"+sub_domain_id;
                            }else{
                                sub_domain_id="";
                            }
                            }
                            if(field_type.equalsIgnoreCase("N"))
                                align="right";
                            else if(subflag.equalsIgnoreCase("1")||subflag.equals("1"))
                                align="center";     
                            String sub_domain = abean.get("sub_domain")==null?"":(String)abean.get("sub_domain");                       
                            String field_name2=field_name;
                            if(isvar.equals("0"))
                            {
                                field_name2=field_name+sub_domain_id+"_"+chgstate;
                            }
                            
                            if(isLock.equals("1"))
                            {
                                
                                if((dataSize==(seq+1)&&isCompare.equals("0"))||(dataSize==(seq+2)&&isCompare.equals("1")))
                                    classname="t_cell_locked_b  common_border_color";
                                else{
                                    classname="t_cell_locked  common_border_color";
                                    if(dataSize==(seq+1)&&isCompare.equals("1"))
                                    classname="t_cell_locked_b2 common_border_color";
                                    }
                            }
                            String context="";
                            if(_abean.get(field_name2)!=null)
                                context=_abean.get(field_name2)==null?"":(String)_abean.get(field_name2);
                                context = context.replace("\r\n","<br>");
                                context = context.replace("%26amp;", "&");
                            String m_mouse_str="";
                            if(field_type.equalsIgnoreCase("M")&&context.length()>0&&!context.equals("NULL"))
                            {
                                if(hismode!=null&&hismode.equals("4")&&mode!=null&&(mode.equals("0")||mode.equals("2")))
                                {
                                
                                }
                                else
                                {
                                    m_mouse_str="onmouseover=\"outContent('"+context+"')\"   onmouseout=\"UnTip()\"   ";
                                    context="<font color='blue' >有数据</font>";
                                }
                            }
                            
                            String sub_click_event_str="";
                            if(subflag.equals("1"))
                            {
                                if(chgstate.equals("1"))
                                {
                                    context="现子集";
                                }
                                else
                                {
                                    context="拟子集";
                                                        
                                }
                                 
                                if(isAppealTable.equals("1"))
                                    seqnum=(String)_abean.get("seqnum");
                                sub_click_event_str=" style=\"cursor:hand\"  onclick=\"showSubTable('"+table_name+"','"+a0100+"','"+basepre+"','"+isAppealTable+"','"+seqnum+"','T_"+setname+sub_domain_id+"_"+chgstate+"','"+sub_domain+"',this)\" ";
                            }
                                
                            if(context.length()==0||context.equals("NULL"))
                            {
                                context="&nbsp;";
                                if(field_type.equalsIgnoreCase("M"))
                                {
                                    if(hismode!=null&&hismode.equals("4")&&mode!=null&&(mode.equals("0")||mode.equals("2")))
                                    {
                                    
                                    }
                                    else
                                        context="无数据";
                                }
                            }   
                            
                        if(showDesc_index!=-1&&showDesc_index==i)
                        {
                            if(seq%2==0)
                                 out.println("<td align='center'  nowrap class='"+classname+"' >&nbsp;变化前&nbsp;</td> ");
                            else
                                 out.println("<td align='center'  nowrap class='"+classname+"' >&nbsp;变化后&nbsp;</td> ");
                        }    
                            
                                
                                
                        if(isCompare.equals("0"))
                            out.println("<td align='"+align+"'  "+m_mouse_str+"  "+sub_click_event_str+"  class='"+classname+"'"+color+"    height='25'   nowrap >&nbsp;"+context+"&nbsp;</td>");
                        else
                        {
                            if(seq%2==0&&(chgstate.equals("1")||subflag.equals("1")))
                                out.println("<td align='"+align+"' "+m_mouse_str+" class='"+classname+"' "+color+"   "+sub_click_event_str+"  rowspan='2'  height='25'   nowrap >&nbsp;"+context+"&nbsp;</td>"); 
                            else if(seq%2==1&&(chgstate.equals("1")||subflag.equals("1")))
                            {
                            
                            }
                            else
                                out.println("<td align='"+align+"' "+m_mouse_str+" class='"+classname+"' "+color+"  "+sub_click_event_str+"   height='25'   nowrap >&nbsp;"+context+"&nbsp;</td>");
                        }
                        if(lockBean!=null&&lockBean.get("field_name")!=null&&lockBean.get("field_name").equals(field_name)&&lockBean.get("chgstate").equals(chgstate)&&lockBean.get("subflag").equals(subflag)&&lockBean.get("gridno").equals(abean.get("gridno"))&&lockBean.get("pageid").equals(abean.get("pageid"))){
                            if(isCompare.equals("0"))
                                    out.println("<td align='center'   class='"+_classname+"' "+color+" height='25'   nowrap > &nbsp;&nbsp;<img src='/images/edit.gif'  style=\"cursor:hand\"    onclick=\"showCard('"+ins_id+"','"+task_id+"','"+a0100+"','"+basepre+"')\"   border=0>&nbsp;&nbsp;</td>");  
                                else
                                {   
                                    if(seq%2==0)
                                        out.println("<td align='center'   class='"+_classname+"' "+color+" rowspan='2'  height='25'   nowrap > &nbsp;&nbsp;<img src='/images/edit.gif'  style=\"cursor:hand\"    onclick=\"showCard('"+ins_id+"','"+task_id+"','"+a0100+"','"+basepre+"')\"   border=0>&nbsp;&nbsp;</td>");  
                                    
                                }       
                        }
                    
                        
//                      if(isName.equals("1"))
//                      {
//                          if((dataSize==(seq+1)&&isCompare.equals("0"))||(dataSize==(seq+2)&&isCompare.equals("1")))
//                                  classname="t_cell_locked_b";
//                          else
//                                  classname="t_cell_locked";
//                          if(operationtype.equals("0")&&field_name.equalsIgnoreCase("a0101")&&chgstate.equals("2"))
//                          {
//                              if(isCompare.equals("0"))
//                                  out.println("<td align='center'   class='"+classname+"' height='25'   nowrap > &nbsp;&nbsp;<img src='/images/edit.gif'  style=\"cursor:hand\"    onclick=\"showCard('"+ins_id+"','"+task_id+"','"+a0100+"','"+basepre+"')\"   border=0>&nbsp;&nbsp;</td>");  
//                              else
//                              {   
//                                  if(seq%2==0)
//                                      out.println("<td align='center'   class='"+classname+"' rowspan='2'  height='25'   nowrap > &nbsp;&nbsp;<img src='/images/edit.gif'  style=\"cursor:hand\"    onclick=\"showCard('"+ins_id+"','"+task_id+"','"+a0100+"','"+basepre+"')\"   border=0>&nbsp;&nbsp;</td>");  
//                                  
//                              }       
//                          }
//                          if(!operationtype.equals("0")&&field_name.equalsIgnoreCase("a0101")&&chgstate.equals("1"))
//                          {
//                              if(isCompare.equals("0"))
//                                  out.println("<td align='center'   class='"+classname+"'   height='25'   nowrap > &nbsp;&nbsp;<img src='/images/edit.gif'   style=\"cursor:hand\"    onclick=\"showCard('"+ins_id+"','"+task_id+"','"+a0100+"','"+basepre+"')\"  border=0>&nbsp;&nbsp;</td>");  
//                              else
//                              {   
//                                  if(seq%2==0)
//                                      out.println("<td align='center'   class='"+classname+"' rowspan='2'  height='25'   nowrap > &nbsp;&nbsp;<img src='/images/edit.gif'  style=\"cursor:hand\"    onclick=\"showCard('"+ins_id+"','"+task_id+"','"+a0100+"','"+basepre+"')\"   border=0>&nbsp;&nbsp;</td>");  
//                                  
//                              }       
//                          }
//                      }
                    
                    }
                     seq++; 
                     }
                     %>
                  </tr>
                         
                  </hrms:extenditerate>
                  
             
            </table>   
             
    </div>    
    
    <script language='javascript' >
        document.write("<div  id='page'  style='position:absolute;left:5;top:"+(pageHeight-30)+";width:100%'  >");
    </script>
         <table  width="100%" align="center"  class='RecordRowP' >
        <tr>
            <td valign="bottom" class="tdFontcolor">
                    <bean:message key="label.page.serial"/>
                    <bean:write name="templateListForm" property="templatelistform.pagination.current" filter="true" />
                    <bean:message key="label.page.sum"/>
                    <bean:write name="templateListForm" property="templatelistform.pagination.count" filter="true" />
                    <bean:message key="label.page.row"/>
                    <bean:write name="templateListForm" property="templatelistform.pagination.pages" filter="true" />
                    <bean:message key="label.page.page"/>
            </td>
            <td align="right" nowrap class="tdFontcolor">
                <p align="right">
                    <hrms:paginationlink name="templateListForm" property="templatelistform.pagination" nameId="templatelistform">
                    </hrms:paginationlink>
            </td>
        </tr>
   </table> 
     </div>
  
    <input type='hidden' name='isCompare'  value="${templateListForm.isCompare}" />
    <input type='hidden' name='hiddenItem'  value="${templateListForm.hiddenItem}" />
    <input type='hidden' name='fieldSetSortStr'  value="${templateListForm.fieldSetSortStr}" />
    <input type='hidden' name='lockedItemStr'  value="${templateListForm.lockedItemStr}" />
    <input type='hidden' name='orderStr'  value="${templateListForm.orderStr}" />
    <input type='hidden' name='filterStr'  value="${templateListForm.filterStr}" />
    <input type='hidden' name='sortitem'  value="${templateListForm.sortitem}" />
    <input type='hidden' name='table_name'  value="${templateListForm.table_name}" />
    <input type='hidden' name='tasklist_str'  value="${templateListForm.tasklist_str}" />
    <input type='hidden' name='codeid'  value="${templateListForm.codeid}" />
    <input type='hidden' name='operationtype'  value="${templateListForm.operationtype}" />
    <!--前台不能使用sql,这里去掉了needcondition-->
    
     <input type='hidden' name='isSelectAll'  value="${templateListForm.isSelectAll}" />
    <div  class="common_border_color" id="date_panel"  style='display:none'  onBlur='hiddenPanel()'  ></div>
     <a href="<%=url_p%>" style="display:none" id="hostname">for vpn</a>
    </html:form>
  <body><br><br></body>
</html>
<div id="TmplPreview1div">
<!-- 
<OBJECT
      id="TmplPreview1"
	  classid="clsid:3FF4C4CB-232A-4397-9B0C-0215C179FFDD"
	  codebase="/cs_deploy/AxTmplPreview.cab#version=1,0,68,0"
	  width="0" height="0" hspace="0"  vspace="0">
</OBJECT>
 -->
</div>


<div id='wait' style='position:absolute;top:285;left:80;display:none;z-index:999;'>
  <table border="1" width="300" cellspacing="0" cellpadding="4" class="table_style"  height="87" align="center">
           <tr>
             <td class="td_style" height=24>正在处理，请稍候...</td>
          </tr>
           <tr>
             <td style="font-size:12px;line-height:200%" align=center>
               <marquee class="marquee_style" direction="right" width="300" scrollamount="5" scrolldelay="10" >
                 <table cellspacing="1" cellpadding="0">
                   <tr height=8>
                     <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                    </tr>
                  </table>
               </marquee>
             </td>
          </tr>
        </table>
</div>


<script language="javascript">
            
         
  var hosturl=$('hostname').href;
  var dbtype ="<%=dbtype%>";
  var username="<%=username%>";
  var userFullName="<%=userFullName%>";
  var superUser = "<%=superUser%>";
  var nodeprive = "<%=nodeprive%>";
  var tables = "<%=tables%>"; 
  var usedday = "<%=usedday%>";
  var fields ="<%=fields%>";
  
 //initCard(hosturl,'<%=dbtype%>','<%=username%>','<%=userFullName%>','<%=superUser%>','<%=nodeprive%>','<%=tables%>','TmplPreview1','<%=_version%>','<%=usedday%>');
 //initAciveCard(hosturl,'<%=dbtype%>','<%=username%>','<%=userFullName%>','<%=superUser%>','<%=fields%>','<%=tables%>','CardPreview1'); 

  function printcardpdf(tabid)
  {
       var hashvo=new ParameterSet();
       hashvo.setValue("cardid",tabid);
       hashvo.setValue("cyear","${templateListForm.cardparam.csyear}");
       hashvo.setValue("userpriv","noinfo");
       hashvo.setValue("istype","1");        
       hashvo.setValue("cmonth","${templateListForm.cardparam.cmonth}");
       hashvo.setValue("season","${templateListForm.cardparam.season}");
       hashvo.setValue("ctimes","${templateListForm.cardparam.ctimes}");
       hashvo.setValue("cdatestart","${templateListForm.cardparam.cdatestart}");
       hashvo.setValue("cdateend","${templateListForm.cardparam.cdateend}");
       if(infor_type=="1"){
       hashvo.setValue("infokind","1");
       }else if(infor_type=="2"){
       hashvo.setValue("infokind","2");
       }else if(infor_type=="3"){
       hashvo.setValue("infokind","3");
       }
       hashvo.setValue("querytype","${templateListForm.cardparam.queryflagtype}");
       var table=$("${templateListForm.table_name}");      
       
       if(_a0100.length>0)
       {
           var basepre=_basepre;
           var a0100=_a0100;  
           hashvo.setValue("userbase",basepre);
           var nids=new Array();
           nids.push(a0100);
           hashvo.setValue("nid",nids);
           var In_paramters="exce=PDF";  
           var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showPDF,functionId:'07020100015'},hashvo);
       }
  }   
  
  function setCurrent(a_a0100,a_base)
  {
     _basepre=a_base;
     _a0100=a_a0100;
  }
  
  function AxGetCodeDesc(CodeSetId, CodeItemId)
    {
        var tmp="_"+CodeSetId+CodeItemId;
        if(!(g_dm[tmp]=="undefined"||g_dm[tmp]==null))
        {
            if(CodeSetId=="UM"&&!(g_dm[tmp].P=="undefined"||g_dm[tmp].P==null||g_dm[tmp].P.length==0))
                value=g_dm[tmp].P;
            else
                value=g_dm[tmp].V;
        }
        else
        {
            if(CodeSetId=="UN")
            {
                tmp="_UM"+CodeItemId;
                if(!(g_dm[tmp]=="undefined"||g_dm[tmp]==null))
                    value=g_dm[tmp].V;
                else
                    value="";
            }
            else
                value="";
        }
         return  value;
    }
  
</script>  

<%
  int num=Integer.parseInt(templateListForm.getNum());
  num++;
  templateListForm.setNum(String.valueOf(num));
%>
