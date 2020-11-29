package com.hjsj.hrms.transaction.mobileapp.template;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 类名称:TemplateTrans
 * 类描述:
 * 创建人: xucs
 * 创建时间:2013-11-28 上午09:40:55 
 * 修改人:xucs
 * 修改时间:2013-11-28 上午09:40:55
 * 修改备注:
 * @version
 *
 */
public class TemplateTrans extends IBusiness {
    
	private static final long serialVersionUID = 1L;
	
	private final String TASK_IN_WAITING="1";//待办任务
    private final String TASK_IN_RESOLVE="2";//已办任务
    private final String TASK_IN_MYAPPLY="3";//我的申请
    private final String BUSINESS_APPLY="4";//(个人/业务)申请
    private final String ADD_BUSINESS_DATA="5";//业务单据增加数据
    private final String BUSINESS_TYPE="6";//业务单据初始化
    private final String PAGENUM="7";//获得业务单据标签
    private final String DELETE_BUSINESS="8";//删除业务单据中的某个记录
    private final String DEALWITH="9";  //办理初始化
    private final String REJECT="10";   //退回
    private final String APPROVE="11";  //办理
    private final String APPEAL="12";  //上报
    private final String VALIDATE="13";  //办理校验
    private final String SAVE_BUSINESS="15";//保存业务单据
    private final String BUSINESS_FLOW="16";//审批流程
    private final String BUSINESS_SUBMIT="17";//更新submit_flag
    private final String BUSINESS_FACTOR="18";//检索条件（增加、减少人员）
    private final String COMBINE_BUSINESS="19";//单位管理、岗位管理（合并、划转）
    private final String SAVE_COMBINE_BUSINESS="20";//合并保存
    private final String SAVE_TRANSFER_BUSINESS="21";//划转保存
    private final String CLOCKING_IN_BUSINESS="22";//考勤业务应有控制
    private final String CALL_IN_BUSINESS="23";//调入型模版新增（人员、岗位）数据
    private final String CALCULATE_BUSINESS="24";//计算功能的实现
    private final String VALIDATEMIDVAR_BUSINESS="25";//验证变量是否为空
    public void execute() throws GeneralException {
        String message = "";
        String succeed = "false";
        HashMap hm = this.getFormHM();
        try{
            String transType=(String)hm.get("transType"); //表示的是业务类型  1：待办业务  2：已办任务  3：我的申请 4：(个人/业务)申请 5：单据增加数据 6：业务单据初始化
            hm.remove("transType");
            hm.remove("message");
            hm.remove("succeed");
            //UserView userView = this.getUserView();
            Connection conn = this.getFrameconn();
            SearchDataBo templatebo = null;
            if(transType!=null){
                if (TASK_IN_WAITING.equals(transType)) {// 待办业务数目以及简略信息的展示
                    String index=(String)hm.get("pageIndex");//表示是第几页
                    String size=(String)hm.get("pageSize");//表示每页有几条数据
                    templatebo=new SearchDataBo(conn, userView);
                    this.userView.getHm().put("business_model",transType);
                    String query_type=(String)hm.get("query_type");//查询数据方式（1:最近,2:按照时间段范围）
                    String days=(String)hm.get("days");;//最近多少天数  如果不传days天默认为30天
                    if(days==null|| "".equals(days)){
                        days="30";
                    }
                    String bs_flag=(hm.get("bs_flag")==null?"1":(String)hm.get("bs_flag"));//报审标志位 ：表示任务的类型   =1：审批任务  =2：加签任务 =3：报备任务 =4：空任务  默认 1
                    this.userView.getHm().put("bs_flag", bs_flag);
                    String startDate=hm.get("startDate")==null?"":(String)hm.get("startDate");
                    String endDate=hm.get("endDate")==null?"":(String)hm.get("endDate");
                    String taskFormName=hm.get("taskFormName")==null?"":(String)hm.get("taskFormName");
                    if("1".equals(index)){//只有第一进来的时候进行数据查询，其余时候进来就从userView中获取所需要的数据
                        this.userView.getHm().put("allTaskList", templatebo.searchInWaitingTask(query_type,days,startDate,endDate,bs_flag,taskFormName));
                    }
                    ArrayList allDataList=(ArrayList)this.userView.getHm().get("allTaskList");
                    ArrayList inWaitingTaskList = templatebo.getReturnList(allDataList, index, size);
                    hm.put("transType", transType);
                    hm.put("inWaitingTaskList", inWaitingTaskList);
                    succeed = "true";
                    
                }else if(BUSINESS_TYPE.equals(transType)){//得到业务单据中的数据
                    
                   String index=(String)hm.get("pageIndex");//表示是第几页
                   String size=(String)hm.get("pageSize");//表示每页有几条数据
                   String task_id=hm.get("task_id")==null?"":(String)hm.get("task_id");
                   String  isInitData=hm.get("isInitData")==null?"1":(String)hm.get("isInitData");
                   String  url=hm.get("url")==null?"":(String)hm.get("url");
                   String tabid=(String)hm.get("tabid");
                   String ins_id=(String)hm.get("ins_id");
                   String searchValue=hm.get("searchValue")==null?"":(String)hm.get("searchValue");
                   templatebo=new SearchDataBo(conn, this.userView,tabid);
                   if("1".equals(index)){//是第一页的情况下 初始化数据
                       
                       String selfapply=hm.get("selfapply")!=null?(String)hm.get("selfapply"):"0"; //个人业务申请
                       if("1".equals(selfapply))
                           templatebo.getTemplateTableBo().setBEmploy(true);
                       ArrayList pageList = templatebo.searchTaskPageList(task_id,isInitData,tabid,ins_id,selfapply);//页签List
                       ArrayList businessDataList=templatebo.searchTaskBusinessDataList(task_id,ins_id,tabid,(String)this.userView.getHm().get("business_model"),(String)this.userView.getHm().get("bs_flag"),url,searchValue,selfapply);
                       this.userView.getHm().put("pagelist",pageList);
                       this.userView.getHm().put("businessDataList", businessDataList);
                       ArrayList firstbusinessDataList=templatebo.getReturnList((ArrayList)this.userView.getHm().get("businessDataList"), index, size);
                       hm.put("businessDataList", firstbusinessDataList);
                       hm.put("pagelist", pageList);
                       hm.put("transType", transType);
                       succeed = "true";
                   }else{//如果不是第第一页，从缓存中取得数据
                       
                       ArrayList businessDataList=templatebo.getReturnList((ArrayList)this.userView.getHm().get("businessDataList"), index, size);
                       hm.put("businessDataList", businessDataList);
                       hm.put("transType", transType);
                       succeed = "true";
                   }
                   
                }else if(PAGENUM.equals(transType)){//得到业务单据的页签
                    hm.put("pagelist", this.userView.getHm().get("pagelist"));
                    hm.put("transType", transType);
                    succeed = "true";
                    
                }else if(DELETE_BUSINESS.equals(transType)){//删除业务单据中的某条记录
                    String task_id=hm.get("task_id")==null?"":(String)hm.get("task_id");
                    String tabid=(String)hm.get("tabid");
                    String infor_type=(String)hm.get("infor_type");
                    String objid=(String)hm.get("objectId");
                    templatebo=new SearchDataBo(conn, this.userView,tabid);
                    templatebo.deleteBusinessRecData(task_id,tabid,infor_type,objid);
                    hm.put("transType", transType);
                    succeed = "true";
                    
                } else if(DEALWITH.equals(transType)){ //办理初始化
                    
	               	 String tabid=hm.get("tabid")==null?"":(String)hm.get("tabid");
	               	 templatebo=new SearchDataBo(conn, this.userView,tabid);
	               	 String task_id=hm.get("task_id")==null?"":(String)hm.get("task_id");
	               	 String ins_id=hm.get("ins_id")==null?"":(String)hm.get("ins_id");
	               	 String selfapply=hm.get("selfapply")!=null?(String)hm.get("selfapply"):"0"; /** 标示自助用户还是业务用户，0业务用户、1自助用户*/
	               	 LazyDynaBean initData=templatebo.getInitData(task_id,selfapply,ins_id); 
	               	
	                 int sp_mode=templatebo.getTemplateTableBo().getSp_mode(); //审批模式=0自动流转，=1手工指派
	                 boolean bsp_flag=templatebo.getTemplateTableBo().isBsp_flag(); //*是否需要审批  boolean ，true需要
	               	 if((ins_id==null||ins_id.trim().length()==0||"0".equals(ins_id))&&sp_mode==0&&bsp_flag)
	               	 {
	               		hm.put("noAlert","1");//流程发起时，如果是审批流程且是自动的，报批时就不弹窗
	               	 }
	               	 else
	               		hm.put("noAlert","");
	               	 
	               	 
	               	 hm.put("actor_type",(String)initData.get("actor_type"));  //1:自助用户  2：业务用户
	               	 hm.put("spButtonFlagList",templatebo.getSpButtonFlagList((String)initData.get("flag"),task_id,ins_id,tabid));
	               	 hm.put("priorityList", (ArrayList)initData.get("priorityList"));
	               	 hm.put("priorityValueList", (ArrayList)initData.get("priorityValueList"));
	               	 hm.put("flag", (String)initData.get("flag"));
	               	 hm.put("spObjectList", (ArrayList)initData.get("spObjectList"));
	               	 hm.put("spObjectValueList", (ArrayList)initData.get("spObjectValueList"));
	               	 hm.put("transType", transType);
	                  succeed = "true";
	                  
               }else if(REJECT.equals(transType)){ //退回
                   
            	     String tabid=hm.get("tabid")==null?"":(String)hm.get("tabid");
	               	 templatebo=new SearchDataBo(conn, this.userView,tabid);
	               	 String task_id=hm.get("task_id")==null?"":(String)hm.get("task_id");
	               	 String ins_id=hm.get("ins_id")==null?"":(String)hm.get("ins_id");
	               	 String priority=hm.get("priority")!=null?(String)hm.get("priority"):"";
	               	 String cause=hm.get("cause")!=null?(String)hm.get("cause"):""; 
	               	 templatebo=new SearchDataBo(conn, this.userView,tabid);
	                 templatebo.rejectTask(task_id,ins_id,priority,cause); 
	                 hm.put("transType", transType);
	                 succeed = "true";
	                 
               }else if(VALIDATE.equals(transType)){ //办理校验  驳回操作不能调用
                   
            	     String tabid=hm.get("tabid")==null?"":(String)hm.get("tabid");
            		 templatebo=new SearchDataBo(conn, this.userView,tabid);
            		 String task_id=hm.get("task_id")==null?"":(String)hm.get("task_id");
	               	 String ins_id=hm.get("ins_id")==null?"":(String)hm.get("ins_id");
	               	 String selfapply=hm.get("selfapply")!=null?(String)hm.get("selfapply"):"0"; /** 标示自助用户还是业务用户，0业务用户、1自助用户*/
            		 LazyDynaBean infoBean=templatebo.validateInfo(ins_id,task_id,selfapply);
            		 hm.put("msg", (String)infoBean.get("msg"));
            		 hm.put("flag", (String)infoBean.get("flag"));
            		 hm.put("transType", transType);
	                 succeed = "true";
	                 
               }else if(APPROVE.equals(transType)){ //办理 
                   
            	     String tabid=hm.get("tabid")==null?"":(String)hm.get("tabid");
          		     templatebo=new SearchDataBo(conn, this.userView,tabid);
          		     String task_id=hm.get("task_id")==null?"":(String)hm.get("task_id");
	               	 String ins_id=hm.get("ins_id")==null?"":(String)hm.get("ins_id"); 
	               	 String priority=hm.get("priority")!=null?(String)hm.get("priority"):"";
	               	 String content=hm.get("content")!=null?(String)hm.get("content"):"";  
	               	 String selfapply=hm.get("selfapply")!=null?(String)hm.get("selfapply"):"0"; /** 标示自助用户还是业务用户，0业务用户、1自助用户*/
	               	 templatebo.getTemplateTableBo().setValidateM_L(true);
	               	if("0".equals(task_id)&&templatebo.getTemplateTableBo().isBsp_flag()&&(templatebo.getTemplateTableBo().getSp_mode()==0|| "1".equals(templatebo.getTemplateTableBo().getDef_flow_self())))
	               	{
	               		templatebo.createNextTask(task_id,ins_id,priority,content,"","","",selfapply);
	               	}
	               	else
	               	{
	               		
	               		if(templatebo.getTemplateTableBo().isBsp_flag()&&templatebo.getTemplateTableBo().getSp_mode()==0) //自动流程办理即报批
	               			templatebo.createNextTask(task_id,ins_id,priority,content,"","","",selfapply);
	               		else if(!"0".equals(task_id)&&templatebo.getTemplateTableBo().getSp_mode()==1&& "1".equals(templatebo.getTemplateTableBo().getDef_flow_self()))//手工审批，自定义审批流程
	               		{
	               			 
	               			if(templatebo.getNextSpLevel(Integer.parseInt(task_id),templatebo.getTemplateTableBo())!=0)//当前审批层级最后一个处理的节点
	               			{
	               				templatebo.createNextTask(task_id,ins_id,priority,content,"","","",selfapply);
	               			}
	               			else
	               				templatebo.approveTask(tabid,ins_id,task_id,selfapply,priority,content);
	               		}
	               		else		
	               			templatebo.approveTask(tabid,ins_id,task_id,selfapply,priority,content);
	               	}
	                 hm.put("transType", transType);
	                 succeed = "true";
	                 
               }else if(APPEAL.equals(transType)){ //上报
                   
            	     String tabid=hm.get("tabid")==null?"":(String)hm.get("tabid");
        		     templatebo=new SearchDataBo(conn, this.userView,tabid);
        		     String task_id=hm.get("task_id")==null?"":(String)hm.get("task_id");
	               	 String ins_id=hm.get("ins_id")==null?"":(String)hm.get("ins_id"); 
	               	 String priority=hm.get("priority")!=null?(String)hm.get("priority"):"";
	               	 String content=hm.get("content")!=null?(String)hm.get("content"):""; 
	               	 String actorid=hm.get("actorid")!=null?(String)hm.get("actorid"):"";
					 String actorname=hm.get("actorname")!=null?(String)hm.get("actorname"):"";
					 String actor_type=hm.get("actor_type")!=null?(String)hm.get("actor_type"):"";
					 String selfapply=hm.get("selfapply")!=null?(String)hm.get("selfapply"):"0"; //个人业务申请
	               	 if(!"0".equals(task_id))
	               		templatebo.getTemplateTableBo().setValidateM_L(true);
					 templatebo.createNextTask(task_id,ins_id,priority,content,actorid,actorname,actor_type,selfapply);
					 hm.put("transType", transType);
	                 succeed = "true";
	                 
               }else if(SAVE_BUSINESS.equals(transType)){//保存业务单据
                   
                   String task_id=hm.get("task_id")==null?"":(String)hm.get("task_id");
                   String ins_id=hm.get("ins_id")==null?"":(String)hm.get("ins_id");
                   String saveflag=hm.get("saveflag")==null?"":(String)hm.get("saveflag");
                   String tabid=(String)hm.get("tabid");
                   String infor_type=(String)hm.get("infor_type");
                   String objid=(String)hm.get("objectId");
                   String recordData=(String)hm.get("recordData");//存放当前页面数据的recordData
                   String pagenum =(String)hm.get("pagenum");
                   String selfapply=hm.get("selfapply")!=null?(String)hm.get("selfapply"):"0"; //个人业务申请
                   templatebo=new SearchDataBo(conn, this.userView,tabid);
                   if("1".equals(selfapply))
                       templatebo.getTemplateTableBo().setBEmploy(true);
                   String savemessage=templatebo.saveBusinessData(tabid,task_id,infor_type,objid,recordData,pagenum,selfapply);
                   
                 //自动计算
                   boolean reflush = templatebo.autoCalculate(this.userView,ins_id,selfapply);
                   if(reflush){
                       hm.put("reflush", "true"); 
                   }else{
                       hm.put("reflush", "false");
                   }
                   hm.remove("recordData");//这些数据在回传的时候会导致js报错
                   hm.put("savemessage", savemessage);
                   hm.put("saveflag", saveflag);
                   hm.put("transType", transType);
                   succeed = "true";
               }else if(BUSINESS_FLOW.equals(transType)){//审批流程
                   
                   String taskid=(String)hm.get("taskid");
                   String tabid=(String)hm.get("tabid");
                   String infor_type=(String)hm.get("infor_type");
                   templatebo=new SearchDataBo(conn, this.userView,tabid);
                   ArrayList allFlowData =templatebo.searchBusinessFlowData(tabid,taskid,infor_type);
                   HashMap informap = (HashMap) allFlowData.get(0);
                   hm.put("title",(String)informap.get("tableName"));
                   hm.put("name", (String)informap.get("a0101s"));
                   HashMap fristmap = (HashMap) allFlowData.get(1);
                   hm.put("firstmap", fristmap);
                   ArrayList otherList =(ArrayList) allFlowData.get(2); 
                   hm.put("otherList", otherList);
                   hm.put("transType", transType);
                   succeed = "true";
                   
               }else if(BUSINESS_SUBMIT.equals(transType)){//更新submitflag选择
                   
                   String tabid= (String) hm.get("tabid");
                   String objectIds = (String) hm.get("selectObjectIds");
                   templatebo=new SearchDataBo(conn, this.userView,tabid);
                   templatebo.updateSubmitFlag(tabid,objectIds);
                   hm.put("transType", transType);
                   succeed = "true";
                   
               }else if(TASK_IN_RESOLVE.equals(transType)){
                   
                   // 已办业务数目以及简略信息的展示
                   String index=(String)hm.get("pageIndex");//表示是第几页
                   String size=(String)hm.get("pageSize");//表示每页有几条数据
                   templatebo=new SearchDataBo(conn, this.userView);
                   this.userView.getHm().put("business_model",transType);
                   String query_type=(String)hm.get("query_type");//查询数据方式（1:最近,2:按照时间段范围）
                   String days=(String)hm.get("days");;//最近多少天数  如果不传days天默认为30天
                   if(days==null|| "".equals(days)){
                       days="30";
                   }
                   String bs_flag=(hm.get("bs_flag")==null?"1":(String)hm.get("bs_flag"));//报审标志位 ：表示任务的类型  =1：审批任务  =2：加签任务 =3：报备任务 =4：空任务  默认 1
                   this.userView.getHm().put("bs_flag", bs_flag);
                   String startDate=hm.get("startDate")==null?"":(String)hm.get("startDate");
                   String endDate=hm.get("endDate")==null?"":(String)hm.get("endDate");
                   String taskFormName=hm.get("taskFormName")==null?"":(String)hm.get("taskFormName");
                   if("1".equals(index)){//只有第一进来的时候进行数据查询，其余时候进来就从userView中获取所需要的数据
                       this.userView.getHm().put("allResolveTaskList", templatebo.searchResolveTask(query_type,days,startDate,endDate,bs_flag,taskFormName));
                   }
                   ArrayList allDataList=(ArrayList)this.userView.getHm().get("allResolveTaskList");
                   ArrayList resloveTaskList = templatebo.getReturnList(allDataList, index, size);
                   hm.put("transType", transType);
                   hm.put("resloveTaskList", resloveTaskList);
                   succeed = "true";
                   
               }else if(TASK_IN_MYAPPLY.equals(transType)){
                   
                   // 我的申请数目以及简略信息的展示
                   String index=(String)hm.get("pageIndex");//表示是第几页
                   String size=(String)hm.get("pageSize");//表示每页有几条数据
                   templatebo=new SearchDataBo(conn, this.userView);
                   this.userView.getHm().put("business_model",transType);
                   String query_type=(String)hm.get("query_type");//查询数据方式（1:最近,2:按照时间段范围）这个东西在我的申请中用不到
                   String query_method=(String)hm.get("query_method");//查询数据的类别(0：运行中 1：结束);
                   String days=(String)hm.get("days");//最近多少天数  如果不传days天默认为30天 这个东西在我的申请中用不到
                   String bs_flag=(hm.get("bs_flag")==null?"1":(String)hm.get("bs_flag"));//报审标志位 ：表示任务的类型  =1：审批任务  =2：加签任务 =3：报备任务 =4：空任务  默认 1
                   this.userView.getHm().put("bs_flag", bs_flag);
                   String startDate=hm.get("startDate")==null?"":(String)hm.get("startDate");
                   String endDate=hm.get("endDate")==null?"":(String)hm.get("endDate");
                   String taskFormName=hm.get("taskFormName")==null?"":(String)hm.get("taskFormName");
                   
                   if("1".equals(index)){//只有第一进来的时候进行数据查询，其余时候进来就从userView中获取所需要的数据
                       userView.getHm().put("allResolveTaskList", templatebo.searchMyapplyTask(query_type,query_method,days,startDate,endDate,bs_flag,taskFormName));
                   }
                   ArrayList allDataList=(ArrayList)this.userView.getHm().get("allResolveTaskList");
                   ArrayList resloveTaskList = templatebo.getReturnList(allDataList, index, size);
                   
                   hm.put("transType", transType);
                   hm.put("resloveTaskList", resloveTaskList);
                   succeed = "true";
                   
               }else if(BUSINESS_APPLY.equals(transType)){//业务申请
                   
                   /**业务用户 =0 还是自助用户=4**/
                   this.userView.getHm().put("business_model",transType);
                   String operationcode=hm.get("id")==null?"":(String)hm.get("id");
                   String operationname=hm.get("categories")==null?"":(String)hm.get("categories");
                   String selfapply=hm.get("selfapply")!=null?(String)hm.get("selfapply"):"0"; //个人业务申请
                   templatebo=new SearchDataBo(conn, this.userView);
                   
                   if(!"0".equals(selfapply)){//自助用户全都列出来，按照tabid排序
                       ArrayList autoUserList=templatebo.getAutoUserTemplateList();
                       hm.put("transType", transType);
                       hm.put("nexttemplate", autoUserList);
                       hm.put("flag", "2");
                       succeed = "true";
                       
                   }else{//业务用户的判断  
                       boolean checkOn=false;
                       checkOn=templatebo.getBusinessOrderFlag();
                       if(checkOn){
                           if("".equals(operationcode)){//按照业务分类加载一级列表
                               ArrayList TemplateList =templatebo.searchCheckOnTemplateList();
                               hm.put("transType", transType);
                               hm.put("firsttemplate", TemplateList);
                               hm.put("flag", "1");
                               succeed = "true";
                           }else{
                               ArrayList nextTemplateList =templatebo.searchNextCheckOnTemplateList(operationcode,operationname);
                               hm.put("transType", transType);
                               hm.put("nexttemplate", nextTemplateList);
                               hm.put("flag", "2");
                               succeed = "true";
                           }
                       }else{//全部都没有启用业务分类标识
                           if("".equals(operationcode)){//第一次加载一级列表
                               ArrayList TemplateList =templatebo.searchTemplateList("0");
                               hm.put("transType", transType);
                               hm.put("firsttemplate", TemplateList);
                               hm.put("flag", "1");
                               succeed = "true";
                           }else{//第二次加载二级列表
                               ArrayList nextTemplateList =templatebo.searchNextTemplateList(operationcode,"0");
                               hm.put("transType", transType);
                               hm.put("nexttemplate", nextTemplateList);
                               hm.put("flag", "2");
                               succeed = "true";
                           } 
                       }
                   }
               }else if(ADD_BUSINESS_DATA.equals(transType)){//新增人员、组织机构
                   
                   String allObjectId=(String)hm.get("objectIds");
                   String tabid=(String)hm.get("tabid");
                   String selectObjectIds=(String)hm.get("selectObjectIds");
                   templatebo=new SearchDataBo(conn, this.userView,tabid);
                   templatebo.updateSubmitFlag(tabid,selectObjectIds);//首先要更新后台submitFlag标志
                   
                   templatebo.ImpObjToTemplet(allObjectId,tabid);//向临时表中导入数据
                   hm.put("transType", transType);
                   succeed = "true";
                   
               }else if(BUSINESS_FACTOR.equals(transType)){//根据检索条件增减人员
                   
                   String tabid=(String)hm.get("tabid");
                   String flag =(String)hm.get("flag");//   1:清空当前人员,重新引入  2:不清空,引入符合条件的数据
                   templatebo=new SearchDataBo(conn, this.userView,tabid);
                   templatebo.ImpObjToTempletByFactor(tabid,flag);
                   hm.put("transType", transType);
                   succeed = "true";
                   
               }else if(COMBINE_BUSINESS.equals(transType)){//单位管理、岗位管理（合并、划转）
                   
                   String tabid= (String) hm.get("tabid");
                   String objectIds = (String) hm.get("selectObjectIds");
                   String infor_type= (String) hm.get("infor_type");
                   String operationtype= (String) hm.get("operationtype");
                   templatebo=new SearchDataBo(conn, this.userView,tabid);
                   templatebo.updateSubmitFlag(tabid,objectIds);//首先要更新后台submitFlag标志
                   
                   HashMap backMap=templatebo.canDoCombine(tabid,infor_type,operationtype);//判断能否合并、划转
                   
                   templatebo.getCombinePageList(backMap,operationtype,hm,infor_type,tabid);//得到页面上的数据
                   
                   hm.put("transType", transType);
                   succeed = "true";
                   
               }else if(SAVE_COMBINE_BUSINESS.equals(transType)){//合并保存
                   
                   String infor_type= (String) hm.get("infor_type");
                   String table_name= (String) hm.get("table_name");
                   String tarcodeitemdesc= (String) hm.get("tarcodeitemdesc");
                   String combinecodeitemid= (String) hm.get("combinecodeitemid");
                   String end_date= (String) hm.get("end_date");
                   templatebo=new SearchDataBo(conn, this.userView);
                   templatebo.combineOrgBussiness(infor_type,table_name,combinecodeitemid,tarcodeitemdesc,end_date);
                   hm.put("transType", transType);
                   succeed = "true";
                   
               }else if(SAVE_TRANSFER_BUSINESS.equals(transType)){//划转保存
                   String transfercodeitemid= (String) hm.get("transfercodeitemid");
                   String infor_type= (String) hm.get("infor_type");
                   String end_date= (String) hm.get("end_date");
                   String table_name= (String) hm.get("table_name");
                   templatebo=new SearchDataBo(conn, this.userView);
                   String transferMessage=templatebo.transferOrgBussiness(infor_type,table_name,transfercodeitemid,end_date);
                   hm.put("transferMessage", transferMessage);
                   hm.put("transType", transType);
                   succeed = "true";
               }else if(CLOCKING_IN_BUSINESS.equals(transType)){//考勤业务应有控制
                   
                   this.userView.getHm().put("business_model",transType);
                   templatebo=new SearchDataBo(conn, this.userView);
                   HashMap kqMap = templatebo.searchClocckBusiness();
                   hm.put("kqMap", kqMap);
                   hm.put("transType", transType);
                   succeed = "true";
                   
               }else if(CALL_IN_BUSINESS.equals(transType)){//调入型模版新增数据
                   String selectObjectIds=(String)hm.get("selectObjectIds");
                   String tabid=(String)hm.get("tabid");
                   templatebo=new SearchDataBo(conn, this.userView,tabid);
                   
                   templatebo.updateSubmitFlag(tabid,selectObjectIds);//首先要更新后台submitFlag标志
                  //然后新增--
                   templatebo.addBusinessCallIn(tabid);
                   hm.put("transType", transType);
                   succeed = "true";
                   
               }else if(CALCULATE_BUSINESS.equals(transType)){//执行计算功能
                   String tabid=(String)hm.get("tabid");
                   String ins_id=(String)hm.get("ins_id");
                   String selfapply=(String)hm.get("selfapply");
                   String midValue=(String)hm.get("midValue");
                   templatebo=new SearchDataBo(conn, this.userView,tabid);
                   String calculatemessage=templatebo.calculateBusiness(ins_id,selfapply,midValue);
                   hm.put("calculatemessage", calculatemessage);
                   succeed = "true";
               }else if(VALIDATEMIDVAR_BUSINESS.equals(transType)){//计算时判断临时变量是否为空
                   String tabid=(String)hm.get("tabid");
                   templatebo=new SearchDataBo(conn, this.userView,tabid);
                   ArrayList mesList=templatebo.validateMidvarBusiness(); 
                   hm.put("mesList", mesList);
                   hm.put("transType", transType);
                   succeed = "true";
               }
            }else{
                succeed = "false";
                message = ResourceFactory.getProperty("mobileapp.contacts.error.transTypeError");
                hm.put("message", message);
            }
        }
        catch(Exception e){
            succeed = "false";
            String errorMsg=e.toString();
            int index_i=errorMsg.indexOf("description:");
            message=errorMsg.substring(index_i+12);
            hm.put("message", message);
            e.printStackTrace();
            this.cat.error(e.getMessage());
           // throw GeneralExceptionHandler.Handle(e);
        }
        finally{
            hm.put("succeed", succeed);
        }
   }  
}            