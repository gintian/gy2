package com.hjsj.hrms.module.template.templatetoolbar.businessobject;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.sql.RowSet;

import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.general.template.workflow.WF_Instance;
import com.hjsj.hrms.businessobject.general.template.workflow.WF_Node;
import com.hjsj.hrms.module.jobtitle.configfile.transaction.DomXml;
import com.hjsj.hrms.module.template.utils.TemplateBo;
import com.hjsj.hrms.module.template.utils.TemplateDataBo;
import com.hjsj.hrms.module.template.utils.TemplateFuncBo;
import com.hjsj.hrms.module.template.utils.TemplateLayoutBo;
import com.hjsj.hrms.module.template.utils.TemplateUtilBo;
import com.hjsj.hrms.module.template.utils.javabean.TemplateFrontProperty;
import com.hjsj.hrms.module.template.utils.javabean.TemplateParam;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.VersionControl;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;

/**
 * <p>Title:TemplateLayoutBo.java</p>
 * <p>Description>:展现工具栏按钮</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2015-08-23 上午10:36:32</p>
 * <p>@version: 7.0</p>
 */
@SuppressWarnings("all")
public class TemplateToolBarBo {

	private UserView userview;
	private Connection conn=null;
	public TemplateToolBarBo(Connection conn,UserView userview){
		this.conn = conn;
		this.userview=userview;
	}
	/** todo 考虑按人事、薪资、保险等模块区分权限
	 * 人事异动主界面功能按钮 需要参数 
	 * @return
	 */
	public ArrayList getAllToolButtonList(TemplateParam tableParamBo,HashMap formMap)throws GeneralException{
		//获取参数
		ArrayList toolButtonList = new ArrayList();
		VersionControl ver = new VersionControl();
		try{
		    TemplateFrontProperty frontProperty =new TemplateFrontProperty(formMap);
            String sysType = frontProperty.getSysType();
            String returnFlag = frontProperty.getReturnFlag();
            String approveFlag= frontProperty.getApproveFlag();
            String taskId = frontProperty.getTaskId();
            String moduleId=frontProperty.getModuleId();
            boolean ismobilebrowser = Boolean.valueOf(frontProperty.getOtherParam("ismobilebrowser"));//是否手机端浏览器访问
            String recallflag = frontProperty.getOtherParam("recallflag");//是否显示撤回按钮
            String browseprint = frontProperty.getOtherParam("browseprint");//是否是浏览打印点进来
            String view_type = TemplateFuncBo.getValueFromMap(formMap,"view_type");
            String[] taskids = taskId.split(","); 
            
            TemplateUtilBo utilBo = new TemplateUtilBo(this.conn,this.userview);
            TemplateTableBo tablebo=new TemplateTableBo(this.conn,tableParamBo.getTabId(),this.userview);
			if (!"card".equals(view_type)&&!"list".equals(view_type)){//如果没设置，则取默认定义方式
				view_type = tableParamBo.getView();
			}
			//是否是起草、审批? 只有起草审批才有编辑相关的权限，其他只是浏览打印	
			boolean bCanEdit=  "1".equals(approveFlag) || "0".equals(taskId);
			//起草
			boolean bDraft=bCanEdit && ("0".equals(taskId));
			//报审审批 bs_flag=1
			boolean bProcess=bCanEdit && (!"0".equals(taskId));
			//报备 加签 bs_flag=2 ||  bs_flag=3
			boolean bReport=!"0".equals(taskId) && ("2".equals(approveFlag)|| "3".equals(approveFlag)) ;//报备 加签
			//报审审批
			boolean bApprove=bProcess && ("1".equals(approveFlag)) ;//报审
			//自助申请 
			boolean bSelfApply = frontProperty.isSelfApply() && bDraft;
			int templateStatic=tableParamBo.getTemplateStatic();
			boolean bsp_flag = tableParamBo.isBsp_flag();
			TemplateBo templateBo= new TemplateBo(this.conn,this.userview,tableParamBo);
			TemplateDataBo templateDataBo= new TemplateDataBo(this.conn,this.userview,tableParamBo);
			
			String functions="";
			int operationType=tableParamBo.getOperationType();
			Boolean canRevoke=false;
			if(!bCanEdit&&"".equals(recallflag)&&!"4".equals(returnFlag)&&!"1".equals(returnFlag)&&!"2".equals(returnFlag)&&bsp_flag)//直接去除待办任务，已办任务，任务监控进来的情况
			{
				canRevoke=hasRevokeButton(moduleId,taskId);
			}
			//business_model 是否在2,61,62,71,72。
			//liuyz 刷新按钮权限 业务、自助起草审批有刷新按钮，浏览打印没有刷新按钮。
			//boolean refercEnable=(bCanEdit||bSelfApply)&&(operationType!=0&&operationType!=5);
			boolean refercEnable=false;
			//bug 32568 业务平台和自助平台的刷新按钮只要授权一个，另一个应予以同步处理
			if(bCanEdit&&haveFunctionIds("32033,010734,3800734,0C34833,324010133,325010134,2306730,23110230,32129,2701533,400040133")&&(operationType!=0&&operationType!=5))
			{
				refercEnable=true;
			}
			boolean bDown=false;
			//下载模板
			boolean bDownload=false;
			functions="400040124,32024,32122,37022,37122,37222,33001024,33101024,2701524,0C34824,32024,325010121,324010121,2306721,23110221,3800724,400040124";
			if (bDraft && (!bSelfApply) && haveFunctionIds(functions)){
				bDownload=true;
			}
			//syl 职称评审Excel模板下载与上传
			boolean bExcelTemp=false;
			functions="400050001";
			/**起草状态，且非自助申请*/
			if (bDraft && (!bSelfApply) && haveFunctionIds(functions)){
				bExcelTemp=true;
			}
			//上传模板
			boolean bUploadload=false;
			functions="400040125,33001025,33101025,2701525,0C34825,32025,32123,37023,37123,37223,37323,324010122,325010122,2306722,23110222,3800725,400040125";
			if (bDraft && (!bSelfApply) && haveFunctionIds(functions)){
				bUploadload=true;
			}
			if(bDownload||bUploadload){
				bDown = true;
			}
			//非起草状态也要有下载模板功能
			boolean bDownload_process = false;
			functions="400040124,32024,32122,37022,37122,37222,33001024,33101024,2701524,0C34824,32024,325010121,324010121,2306721,23110221,3800724,400040124";
			if(!bDraft&& (!bSelfApply) && haveFunctionIds(functions))
				bDownload_process=true;
			//合并
			boolean bcombine=false;
			if (operationType==8 && bDraft){
				bcombine=true;
			}
			//划转
			boolean bTransferred=false;
			if (operationType==9 && bDraft){
				bTransferred=true;
			}

			//新增：
			boolean bAdd=false;
			if ( bDraft && (!bSelfApply) && (operationType==0 || operationType==5) ){
				functions="32107,37007,37107,37207,37307,33001007,33101007,2701507,0C34807,32007,325010107,324010107,2306707,23110207,3800707,400040107";
				if (haveFunctionIds(functions)){
					bAdd=true;
				}
			}
			//选人：
			boolean bSelect=false;
			if ( bDraft && (!bSelfApply) && (operationType!=0 && operationType!=5)){
				functions="400040109,32109,37009,37109,37209,37309,33001009,33101009,2701509,0C34809,32009,325010109,324010109,2306709,23110209,3800709,400040109";
				if (haveFunctionIds(functions)){
					bSelect=true;
				}
				//简单查询 通用查询
			}
			//手工选人
			boolean bHandSelect=false;
			if (bDraft && (!bSelfApply)){//&& !"1".equals(tableParamBo.getNo_priv_ctrl()))liuyz 不按管理范围控制也显示手工选人{//按管理范围
				bHandSelect=true;
			}			
			
			//撤销：
			boolean bCancel=false;
			if (bCanEdit  && (!bSelfApply) && (!bReport)){
				functions="010730,32103,37003,37103,37203,37303,33001008,2701508,0C34808,32008,325010108,324010108,2306708,23110208,33101008,3800708,400040108";
				if (haveFunctionIds(functions)){
					bCancel=true;
				}
			}
			//保存：
			boolean bSave=false;
			if (bCanEdit && (!bReport)){
				bSave=true;//参考旧代码，不需要判断功能号  lis 20160513
				/*functions="0570010105";
				if (haveFunctionIds(functions)){
				}*/
			}
			//上会
			boolean bSubMeeting=false;
			if (bApprove){
				if(tableParamBo.getSp_mode()==0) {
					RowSet rset = null;
					ContentDAO dao=new ContentDAO(this.conn);
					try {
						String a0100=this.userview.getDbname()+this.userview.getA0100();
						String usrname=this.userview.getUserId();
						String sql = "select count(1) as num from t_wf_node where tabid="+tableParamBo.getTabId()+" and node_id in (select node_id from t_wf_task twt left join t_wf_instance twi on twt.ins_id=twi.ins_id where " + 
								"(twi.actorid='"+a0100+"' or twi.actorid='"+usrname+"') and twt.task_id in ("+taskId+")) and nodetype=1";
						rset = dao.search(sql);
						if(rset.next()) {
							int num = rset.getInt("num");
							if(num>0)
								bSubMeeting = false;
							else {
								functions="3800733,010733";
		                        if (haveFunctionIds(functions)){
		                            bSubMeeting=true;
		                        }
							}
						}
					}catch(Exception e) {
						e.printStackTrace();
					}finally {
						PubFunc.closeDbObj(rset);
					}
				}else {
		             for(int i=0;i<taskids.length;i++){
	                    /**填充起始节点标识，此申请是否为当前用户*/ 
	                    String isStartNode=templateBo.isStartNode(taskids[i]);
	                    if(!"1".equals(isStartNode)) //非发起人才有上会按钮  dengcan
	                    {
	                        functions="3800733,010733";
	                        if (haveFunctionIds(functions)){
	                            bSubMeeting=true;
	                        }
	                    } 
	                    else {
	                        bSubMeeting=false;
	                        break;
	                    }
		             }
				}
	            if (bSubMeeting){
	                bSubMeeting =isDisSubMeetingButton(frontProperty.getTabId());
	            }
			}
			
			
			//是否有生成序号 
			boolean bCreateSequece =templateBo.hasSequenceFieldItem();
			//批量处理 
			boolean bBatch=false;
			if (bCanEdit  && (!bSelfApply) && (!bReport)){
				functions="33001019,33101019,2701519,0C34819,32019,32119,37019,37119,37219,37319,324010119,325010119,2306719,23110219,3800719,400040119";
				if (bCreateSequece){
					functions="400040128,3001019,33101019,2701519,0C34819,32019,32119,37019,37119,37219,37319,324010119,325010119,2306719,23110219,010724,32124,37024,37124,37224,37324,33001028,33101028,2701528,0C34828,32028,324010124,325010124,2306724,23110224,3800719,3800728,400040119";
				}
				if (haveFunctionIds(functions)){
					bBatch=true;
				}
			}
			//批量修改多指标
			boolean bBatchModify=false;
			if (bCanEdit && (!bSelfApply)){
				functions="400040119,33001019,33101019,2701519,0C34819,32019,32119,37019,37119,37219,37319,324010119,325010119,2306719,23110219,3800719";
				if (haveFunctionIds(functions)){
					bBatchModify=true;
				}
			}
			//批量计算
			boolean bBatchCal=false;
			if (bCanEdit && (!bSelfApply)){
				functions="400040105,32105,37005,37105,37205,37305,33001005,33101005,2701505,0C34805,32005,324010105,325010105,2306705,23110205,3800705";
				if ( haveFunctionIds(functions)){
					bBatchCal=true;
				}
			}
			
			
			boolean bNeedSp=(tableParamBo.isBsp_flag()&&!ismobilebrowser);
			boolean bHandSp=(bNeedSp && tableParamBo.getSp_mode()!=0);
			boolean bAutoSp=(bNeedSp && tableParamBo.getSp_mode()==0);
			//发起报批
			boolean bStartApply=false;
			if (bNeedSp && bDraft){
				/*if(bSelfApply){
					functions = "010704";
					if (haveFunctionIds(functions))
						bStartApply=true;
					else
						bStartApply=false;
				}else*/
					bStartApply=true;
			}		
			
			//自定义审批
			boolean bDefFlowSelf=false;
			if (bHandSp  && /*(!bSelfApply) && */"1".equals(tableParamBo.getDef_flow_self())&& "0".equals(taskId)){
				functions="400040130,33001030,33101030,2701530,0C34830,32030,32126,37026,37126,37226,37226,324010130,325010130,010731,2306725,23110225,3800730";
				if (haveFunctionIds(functions)){
					bDefFlowSelf=true;
				}
			}
			//检验自定义审批流程是否定义（报批过程中）
			boolean def_flow_self = false;
            if (bHandSp  &&"1".equals(tableParamBo.getDef_flow_self())&& !"0".equals(taskId)){
                for(int i=0;i<taskids.length;i++)
                {
                    String task_id=taskids[i];
                    if (tablebo.isDef_flow_self(Integer.parseInt(task_id))){
                        def_flow_self = true;
                        break;
                    }
                } 
            }
            //自定义审批流程是否有驳回按钮
            boolean isBReject_self = false;
			//驳回

			boolean bReject=false;
			if (bCanEdit && (!bSelfApply) ){
				if(tableParamBo.getSp_mode()==0) {
					RowSet rset = null;
					ContentDAO dao=new ContentDAO(this.conn);
					try {
						String a0100=this.userview.getDbname()+this.userview.getA0100();
						String usrname=this.userview.getUserId();
						String sql = "select count(1) as num from t_wf_node where tabid="+tableParamBo.getTabId()+" and node_id in (select node_id from t_wf_task twt left join t_wf_instance twi on twt.ins_id=twi.ins_id where " + 
								"(twi.actorid='"+a0100+"' or twi.actorid='"+usrname+"') and twt.task_id in ("+taskId+")) and nodetype=1";
						rset = dao.search(sql);
						if(rset.next()) {
							int num = rset.getInt("num");
							if(num>0)
								bReject=false;
							else {
								for(int i=0;i<taskids.length;i++){
									if (!"0".equals(taskids[i])){
							            bReject=true;
							        }
								}
							}
						}
					}catch(Exception e) {
						e.printStackTrace();
					}finally {
						PubFunc.closeDbObj(rset);
					}
				}else {
				    for(int i=0;i<taskids.length;i++){
				        /**填充起始节点标识，此申请是否为当前用户*/ 
				        String isStartNode=templateBo.isStartNode(taskids[i]);
				        if(bHandSp&& !"1".equals(isStartNode) && !"0".equals(taskids[i])&&def_flow_self){
				        	isBReject_self = true;
				        }else{
				            bReject=false;//如果有一个任务是不显示驳回，则都不显示驳回按钮
				            break;
				        }	
				    }
				}
			}
			//判断是否是已结束的单据
			boolean isFinishTask= templateBo.isFinishedTask(taskId);
			//不报批提交
			boolean bSubmit=false;
			if (!"1".equals(tableParamBo.isBsp_flag()) && bDraft&&!ismobilebrowser/*&& (!bSelfApply)自助不走审批也显示提交按钮*/ ){//bug 39358  客户通过手机浏览器服务大厅打开表单提交按钮没有屏蔽。
				if(bSelfApply){
					functions = "010703";
					if (haveFunctionIds(functions))
						bSubmit=true;
					else
						bSubmit=false;
				}else
					bSubmit=true;
			}
			
			//审批过程
			boolean bSpProcess=false;
			if (bNeedSp  && !"0".equals(taskId)){	
			    functions="400040132,010732,0C34832,2306727,23110227,32032,33101032,33001032,2701532,324010132,325010133,32128,37028,37128,37228,37328,3800732";
                if ( haveFunctionIds(functions)){
                    bSpProcess=true;
                    
                }
			}
			//发表意见
			boolean bPubOpinion=false;
			if ((bReport) && !isFinishTask){				
				bPubOpinion=true;
			}
			
			
			//临时变量
			boolean bMidVar=false;
			functions="400040116,32110,37010,37110,37210,37310,33001016,33101016,2701516,0C34816,32016,324010116,325010116,2306716,23110216,3800716";
			if (bCanEdit && (!bSelfApply) && haveFunctionIds(functions)){
				bMidVar=true;
			}
			//计算公式
			boolean bCalFormula=false;
			functions="400040104,32111,37011,37111,37211,37309,33001004,33101004,2701504,0C34804,32004,324010104,325010104,2306704,23110204,3800704";
			if (bCanEdit && (!bSelfApply) && haveFunctionIds(functions)){
				bCalFormula=true;
			}
			//审核公式
			boolean bVerifyFormula=false;
			functions="400040106,32106,37006,37106,37206,37309,33001006,33101006,2701506,0C34806,32006,324010106,325010106,2306706,23110206,3800706";
			if (bCanEdit && (!bSelfApply) && haveFunctionIds(functions)){
				bVerifyFormula=true;
			}
			//设置业务日期
			boolean bSetAppDate=false;
			functions="400040131,0C34831,2306726,23110226,32031,33101031,33001031,2701531,324010131,325010132,32127,37027,37127,37227,37327,3800731";
			if (bCanEdit && (!bSelfApply) && haveFunctionIds(functions)){
			    bSetAppDate=true;
			}
			
			
			//打印输出
			boolean bPrint=false;
			boolean bOtherPrint=true;
			 if (templateStatic==2 && !this.userview.isSuper_admin()){//薪资变动 处理特殊
		        //if (!isFinishTask){
                    functions="324010103";
                    if (haveFunctionIds(functions)){
                        bPrint=true;
                        if(userview.hasTheFunction("324010120")&&isFinishTask)
                        	bPrint=true;
                    }else{
                        if (!userview.hasTheFunction("324010120")&&isFinishTask){
                            bOtherPrint=false;
                        }else if(userview.hasTheFunction("324010120")&&isFinishTask){
                        	bOtherPrint=true;
                        }
                    }
               // }
			}
			else {
				functions="400040103,32104,37004,37104,37204,37304,33001003,33101003,2701503,0C34803,32003,324010103,325010103,2306703,23110203,010705,3800703";
				if (haveFunctionIds(functions)){
					bPrint=true;
				}
			}
			//导出 不受功能权限控制
			boolean bOutWord=false;
			functions = "010709";
			if (haveFunctionIds(functions)){
				bOutWord=true;
			}
			//预览 不受功能权限控制
			boolean bOutPdf=false;
			functions = "010710";
			if (haveFunctionIds(functions)){
				bOutPdf=true;
			}
			//高级花名册
			boolean bMuster=false;//org.performance.print.highroster
			if(checkFlagHmuster(String.valueOf(tableParamBo.getTabId()))){
				bMuster=true;
			}
			
			//设置
            boolean bSet=true;
            if ((bCanEdit ) && (bMidVar || bVerifyFormula || bCalFormula)){              
               // bSet=true;
            }
            if (bSelfApply){
                bSet=false;
            }
//---------------------------------开始生成按钮------------------------------------------------------------		

			//流程节点控制是否显示按钮
			boolean word=true;
			boolean pdf=true;
			boolean print=true;
			boolean delete=true;
			boolean change_view=true;
			boolean compute=true;
			boolean reject=true;
			boolean process=true;
			boolean down=true;
			boolean batchupdate=true;
			boolean setbusidate=true;
			boolean import_btn=true;
			boolean card_btn=true;
			boolean wordtemplate_btn=true;
			boolean muster_btn=true;
			LazyDynaBean showbtn_node_bean = null;
			if(tableParamBo.isBsp_flag()&&tableParamBo.getSp_mode()==0) {//需要审批and自动流转
				showbtn_node_bean=this.nodeBtnExtparam(tableParamBo.getTabId(), taskId, tablebo,formMap);
			}
			if(showbtn_node_bean!=null) {
				boolean isshowoutBtn=(Boolean)showbtn_node_bean.get("isshowoutBtn");
				//bPrint=isshowoutBtn;
				bOutWord=isshowoutBtn;
				bOutPdf=isshowoutBtn;
				
				
				if(showbtn_node_bean.get("card")!=null) {
					card_btn=(Boolean)showbtn_node_bean.get("card");
				}
				
				if(showbtn_node_bean.get("wordtemplate")!=null) {
					wordtemplate_btn=(Boolean)showbtn_node_bean.get("wordtemplate");
				}
				
				if(showbtn_node_bean.get("muster")!=null) {
					muster_btn=(Boolean)showbtn_node_bean.get("muster"); 
				}
				
				if(showbtn_node_bean.get("word")!=null) {
					word=(Boolean)showbtn_node_bean.get("word");
					if(isshowoutBtn&&word)
						bOutWord=word;
				}
				
				if(showbtn_node_bean.get("pdf")!=null) {
					pdf=(Boolean)showbtn_node_bean.get("pdf");
					if(isshowoutBtn&&pdf)
						bOutPdf=pdf;
				}
				
				if(showbtn_node_bean.get("print")!=null) {
					print=(Boolean)showbtn_node_bean.get("print");//打印
					//bPrint=print;  bprint 业务办理模块包含打印预览和输出两个功能 
				}
				
				if(showbtn_node_bean.get("delete")!=null) {
					delete=(Boolean)showbtn_node_bean.get("delete");//撤销
					bCancel=delete;
				}
				if(showbtn_node_bean.get("change_view")!=null) {
					change_view=(Boolean)showbtn_node_bean.get("change_view");//切换列表|卡片
				}
				if(showbtn_node_bean.get("compute")!=null) {
					compute=(Boolean)showbtn_node_bean.get("compute");//计算
				}
				if(showbtn_node_bean.get("reject")!=null) {
					reject=(Boolean)showbtn_node_bean.get("reject");//驳回
				}
				if(showbtn_node_bean.get("process")!=null) {
					process=(Boolean)showbtn_node_bean.get("process");//审批过程
				}
				if(showbtn_node_bean.get("down")!=null) {
					down=(Boolean)showbtn_node_bean.get("down");//下载模板  前台处理
					if(down&& (!bSelfApply)&&!"0".equals(taskId))
					{
						bDownload_process=true;
					}
				}
				if(showbtn_node_bean.get("batchupdate")!=null) {
					batchupdate=(Boolean)showbtn_node_bean.get("batchupdate");//批量修改 
					if(batchupdate)
					{
						bBatch=true;
						bBatchModify=true;
					}
				}
				if(showbtn_node_bean.get("setbusidate")!=null) {
					setbusidate=(Boolean)showbtn_node_bean.get("setbusidate");//设置业务日期
					bSetAppDate=setbusidate;
				}
				if(showbtn_node_bean.get("import")!=null) {
					import_btn=(Boolean)showbtn_node_bean.get("import");//导入数据  前台处理
					if(import_btn&&"0".equals(taskId)&& (!bSelfApply) )
					{
						bDown=true;
					}
				}
				 
			}
			formMap.put("change_view", change_view);
			formMap.put("import_btn", import_btn);
			
			ButtonInfo splitButton = null;
		 //增加功能导航菜单
			ArrayList navigationList = new ArrayList();
			ArrayList menuList = new ArrayList();			
			LazyDynaBean oneBean = null;

			//审批过程
			if(process&&bSpProcess&&!"0".equals(approveFlag)){
				LazyDynaBean spprocess = getMenuBean(ResourceFactory.getProperty("rsbd.wf.sploop"),
                        "templateTool_me.openShowyj()","",new ArrayList()); 
				spprocess.set("id","viewProcessButton");
				navigationList.add(spprocess);
			}
			//导入数据
            if (import_btn&&bDown&&operationType!=8&&operationType!=9){//机构合并与划转不需要导入数据
            	LazyDynaBean download = getMenuBean(ResourceFactory.getProperty("import.tempData"),
                        "templateTool_me.downTempData()","",new ArrayList());
            	download.set("id", "m_downLoad");
                navigationList.add(download);
            }
            //非起草状态也要有下载模板功能
            if((!"0".equals(taskId)||!bDown||!import_btn)&&down&&bDownload_process&&operationType!=8&&operationType!=9){
            	LazyDynaBean uploadload = getMenuBean(ResourceFactory.getProperty("button.download.template"),
                        "templateTool_me.downLoadTempData()","",new ArrayList());
                navigationList.add(uploadload);
            }
            //导入数据
            /*if (bUploadload){
            	LazyDynaBean uploadload = getMenuBean(ResourceFactory.getProperty("import.tempData"),
                        "templateTool_me.upLoadTempData()","",new ArrayList());
            	uploadload.set("id", "m_upLoad");
                navigationList.add(uploadload);
            }*/
            //syl 下载Excel模板
            if (bExcelTemp){
            	LazyDynaBean download = getMenuBean("导入HTML表单",
                        "templateTool_me.downExcelTemp()","",new ArrayList());
            	download.set("id", "z_downExcelTemp");
                navigationList.add(download);
            }
            //批量处理
            if (bBatch){
                menuList = new ArrayList();
                if (bBatchModify&&batchupdate){//批量修改
                    //批量修改多指标
                    menuList.add(getMenuBean(ResourceFactory.getProperty("menu.gz.batchmany.update"),
                            "templateTool_me.batchUpdateFields()","",new ArrayList()));
                    //批量修改单指标
                    menuList.add(getMenuBean(ResourceFactory.getProperty("menu.gz.single.update"),
                            "templateTool_me.singleUpdateFields()","",new ArrayList()));
                }
                
                if (bCreateSequece){//生成序号
                    
                    if("1".equals(templateBo.getParamBo().getId_gen_manual())){  //关联序号的变化后指标是否手工生成序号, 0加人时自动生成(默认值),1手工生成   20160612 dengcan
                        menuList.add(getMenuBean(ResourceFactory.getProperty("menu.gz.create.sequence"),
                                "templateTool_me.filloutSequence()","",new ArrayList()));
                    }
                }   
                if(menuList.size()>0) {
                	oneBean = new LazyDynaBean();
                	oneBean.set("text", ResourceFactory.getProperty("menu.gz.batch"));
                	oneBean.set("menu", menuList);
                	navigationList.add(oneBean);    
                }
            }
            //自助业务办理-打印 
			String task_id = taskId;
			String isStartNode = "";
			boolean isSelf = false;
			if(taskId.startsWith(","))
				task_id = taskId.substring(1,taskId.length());
			String [] taskarr = task_id.split(",");
			if(taskarr.length==1){
				isStartNode=templateBo.isStartNode(task_id);
				//通过 taskid 判断是不是自助发起的
				isSelf = this.getActor_Type(task_id);
			}

            //打印输出
            if (bPrint &&(!bSelfApply)&&!("3".equals(returnFlag)&&"9".equals(moduleId))){
                //打印
            	ArrayList printList = new ArrayList();
            	ArrayList wordList = new ArrayList();
            	ArrayList pdfList = new ArrayList();
            	LazyDynaBean printldb = getMenuBean(ResourceFactory.getProperty("button.print"),
                        "print()","",new ArrayList());
            	printldb.set("id", "printButton");
            	if(print) {//打印预览
            		navigationList.add(printldb);     
            	}
            	//当前人员生成WORD
            	String curOutwordDesc=ResourceFactory.getProperty("menu.gz.currword");
                String allOutwordDesc=ResourceFactory.getProperty("menu.gz.allword");
                String partOutwordDesc=ResourceFactory.getProperty("menu.gz.selword");
                //当前人员生成PDF
                String curOutPdfDesc=ResourceFactory.getProperty("menu.gz.currpdf");
                String allOutPdfDesc=ResourceFactory.getProperty("menu.gz.allpdf");
                String partOutPdfDesc=ResourceFactory.getProperty("menu.gz.selpdf");
                if (tableParamBo.getInfor_type()==2){
                    curOutPdfDesc=curOutPdfDesc.replace("人员", "机构");
                    allOutPdfDesc=allOutPdfDesc.replace("人员", "机构");
                    partOutPdfDesc=partOutPdfDesc.replace("人员", "机构");
                    curOutwordDesc=curOutwordDesc.replace("人员", "机构");
                    allOutwordDesc=allOutwordDesc.replace("人员", "机构");
                    partOutwordDesc=partOutwordDesc.replace("人员", "机构");
                }
                else if (tableParamBo.getInfor_type()==3){
                    curOutPdfDesc=curOutPdfDesc.replace("人员", "岗位");
                    allOutPdfDesc=allOutPdfDesc.replace("人员", "岗位");
                    partOutPdfDesc=partOutPdfDesc.replace("人员", "岗位");
                    curOutwordDesc=curOutwordDesc.replace("人员", "岗位");
                    allOutwordDesc=allOutwordDesc.replace("人员", "岗位");
                    partOutwordDesc=partOutwordDesc.replace("人员", "岗位");
                }
                /*pdfList.add(getMenuBean(curOutPdfDesc,"curOutPdf",
                        "outPdf(1,1)","",new ArrayList()));*/
                pdfList.add(getMenuBean(curOutPdfDesc,"curOutPdf",
                        "outPdf(1,1)","",selectOfficeORWps(1, "1", 1)));
	            pdfList.add(getpdfOrWordList(allOutPdfDesc,1,2,tableParamBo.getInfor_type()));
	            pdfList.add(getpdfOrWordList(partOutPdfDesc,1,3,tableParamBo.getInfor_type()));
	           /* wordList.add(getMenuBean(curOutwordDesc,"curOutword",
	                    "outword(1,1)","",new ArrayList()));*/
	            wordList.add(getMenuBean(curOutwordDesc,"curOutword",
	                    "outword(1,1)","",selectOfficeORWps(1, "1", 0)));
	            wordList.add(getpdfOrWordList(allOutwordDesc,2,2,tableParamBo.getInfor_type()));
	            wordList.add(getpdfOrWordList(partOutwordDesc,2,3,tableParamBo.getInfor_type()));
	            //输出pdf
	            if (bOutPdf && (bSelfApply||((("1".equals(isStartNode)||"3".equals(returnFlag))&&isSelf)))){
	            	
	            }else{
	            	if(pdf) {//自助模块 流程节点授权  56957
	            		oneBean = new LazyDynaBean();
	            		oneBean.set("text", "导出PDF");
	            		oneBean.set("menu", pdfList);
	            		navigationList.add(oneBean); 
	            	}
	            }
	            //输出word
	            if (bOutWord && (bSelfApply||((("1".equals(isStartNode)||"3".equals(returnFlag))&&isSelf)))){
	            	
	            }else{
	            	if(word) {//自助模块 流程节点授权
	            		oneBean = new LazyDynaBean();
	            		oneBean.set("text", "导出WORD");
	            		oneBean.set("menu", wordList);
	            		navigationList.add(oneBean); 
	            	}
	            }
                //输出高级花名册
                menuList = new ArrayList(); 
                if(bMuster&&muster_btn){
                	navigationList.add(getMenuBean("导出花名册",
                            "printInform()","",new ArrayList()));
                }
                //其他打印或输出word 暂时不提供该功能按钮，以后添加   lis   20160517 
                if(bOtherPrint){
                	//liuyz打开
                	ArrayList otherOutList =templateDataBo.getMusterOrTemplate();
                	ArrayList singleList=new ArrayList();
                	ArrayList mulitList=new ArrayList();
                	ArrayList cardList=new ArrayList();
                	for (int i=0;i<otherOutList.size();i++){
                		menuList=new ArrayList();
                		menuList.clear();
                        LazyDynaBean item=(LazyDynaBean)otherOutList.get(i);
                        String id=(String)item.get("id");
                        String name=(String)item.get("name");
                        String flag=(String)item.get("flag");
                        String tabid=(String)item.get("tabid");
                        String filetype=(String)item.get("filetype");
                        String isHtml=(String)item.get("isHtml");//liuyz 导出单人、多人模版支持word直接上传
                        String jsfunc="print()";
                        String pactive="printActive('"+id+"','"+flag+"')";
                        String cardpdf="printcardpdf('"+id+"','"+flag+"')"; 
                        if(flag!=null&&"2".equals(flag)&&this.userview.isHaveResource(IResourceConstant.CARD,id)){
                            menuList.add(getMenuBean(ResourceFactory.getProperty("button.print"),
                            		pactive,"",new ArrayList())); 
                            menuList.add(getMenuBean(curOutPdfDesc,"",
                            		cardpdf,"",new ArrayList())); 
                            
                            oneBean = new LazyDynaBean();
                            oneBean.set("text", name);
                            oneBean.set("menu", menuList);
                            cardList.add(oneBean);
                        }
                        //单人多人模版
                        else if(flag!=null&&"1".equals(flag))
                        {
                        	if("2".equals(filetype))
                        	{
                        		singleList.add(getMenuBean(ResourceFactory.getProperty(name),
                        				"printPdf('"+id+"','"+flag+"','"+tabid+"','"+filetype+"','"+isHtml+"','0')","",new ArrayList())); 
   
                        		
                        	}
                        	else if("0".equals(filetype))
                        	{
                        		mulitList.add(getMenuBean(ResourceFactory.getProperty(name),
                        				"printPdf('"+id+"','"+flag+"','"+tabid+"','"+filetype+"','"+isHtml+"','0')","",new ArrayList())); 
                        	}
                        }
                    }
                	//输出登记表
                	if(cardList.size()>0&&card_btn){
	            	    oneBean = new LazyDynaBean();
	                    oneBean.set("text", "导出登记表");
	                    oneBean.set("menu", cardList);
	                    navigationList.add(oneBean);
                	}
                	if(singleList.size()>0)
                	{
                		 oneBean = new LazyDynaBean();
                         oneBean.set("text", "单人模板");
                         oneBean.set("menu", singleList);
                         oneBean.set("id", "singleListMenu");
                         printList.add(oneBean);
                	}
                	if(mulitList.size()>0)
                	{
                		 oneBean = new LazyDynaBean();
                         oneBean.set("text", "多人模板");
                         oneBean.set("menu", mulitList);
                         printList.add(oneBean);
                	}
                	if(printList.size()>0&&wordtemplate_btn){
	                	oneBean = new LazyDynaBean();
	                    oneBean.set("text", "按模板导出");
	                    oneBean.set("menu", printList);
	                    navigationList.add(oneBean);
                	}
                }
            }

			//设置
			if (bSet){
				menuList = new ArrayList();
				//临时变量
				if(bMidVar){
					menuList.add(getMenuBean(ResourceFactory.getProperty("menu.gz.variable"),
							"templateTool_me.setTempVar()","",new ArrayList()));
				}
				//计算公式
				if(bCalFormula){
					menuList.add(getMenuBean(ResourceFactory.getProperty("menu.gz.formula"),
							"templateTool_me.setFormula()","",new ArrayList()));
				}
				//校验公式
				if(bVerifyFormula){
					menuList.add(getMenuBean(ResourceFactory.getProperty("label.gz.shformula"),
							"templateTool_me.checkFormula()","",new ArrayList()));
				}
				//业务日期
				if(bSetAppDate&&setbusidate){
					ArrayList _list = new ArrayList();
					String appdate=ConstantParamter.getAppdate(this.userview.getUserName()).trim();
					String[] ym=StringUtils.split(appdate,".");		
					if(ym.length<3){//业务日期设置错误会引起进入人事异动模版报数组越界。在此做校验友好提示。
						throw new Exception(ResourceFactory.getProperty("error.template.formatdataerror"));
					}
					_list.add(getDateMenuBean("templateTool_me.setAppDate(picker,date)","/images/waiting.gif","datepicker","new Date('"+ym[0]+"','"+(Integer.parseInt(ym[1])-1)+"','"+ym[2]+"')"));
					menuList.add(getMenuBean(ResourceFactory.getProperty("menu.gz.appdate"),
							"treemenu","",_list));
				}
				if (menuList.size()>0){
				    oneBean = new LazyDynaBean();
				    oneBean.set("text", ResourceFactory.getProperty("menu.gz.options"));
				    oneBean.set("menu", menuList);
				    navigationList.add(oneBean);
				}
			}
			

			//功能导航
			if (navigationList.size()>0){
			    String menu = this.getMenuStr(ResourceFactory.getProperty("gz_new.gz_accounting.FunctionNavigation"),"navigationId",
			            navigationList);			
			    toolButtonList.add(menu);
			    splitButton = new ButtonInfo("split_navigation");//占位符
			    toolButtonList.add(splitButton);
			}

			//合并
			if (bcombine){
				toolButtonList.add(new ButtonInfo(ResourceFactory.getProperty("button.combine")
						,"templateTool_me.combine()"));
			}
			//划转
			if (bTransferred){
				toolButtonList.add(new ButtonInfo(ResourceFactory.getProperty("button.transfer")
						,"templateTool_me.combine()"));
			}
			//新增
			if (bAdd){
				toolButtonList.add(new ButtonInfo(ResourceFactory.getProperty("button.insert")
						,"templateTool_me.add_newobj()"));
			}
			//选人
			if (bSelect){
				menuList = new ArrayList();
				String desc=ResourceFactory.getProperty("label.rsbd.candi");
				String handDesc=ResourceFactory.getProperty("menu.hand.select");
				if (tableParamBo.getInfor_type()!=1){
					desc=ResourceFactory.getProperty("column.select");
					handDesc=ResourceFactory.getProperty("jx.eval.handSel");
				}
				//手工选择
				if(bHandSelect){
					menuList.add(getMenuBean(handDesc,"m_handSelect",
							"templateTool_me.getHandQuery()","",new ArrayList()));
				}
				//简单查询
				/* 去掉此功能 只保持通用查询
				menuList.add(getMenuBean(ResourceFactory.getProperty("menu.simple.query"),
							"templateTool_me.simpleQuery()","",new ArrayList()));
				*/
				//通用查询
				menuList.add(getMenuBean(ResourceFactory.getProperty("menu.general.query"),
						"templateTool_me.generalQuery()","",new ArrayList()));				
				
				//选人
				String selectMenu = this.getMenuStr(desc,"",menuList);			
				toolButtonList.add(selectMenu);
			}
		
			//撤销
			if (bCancel&&delete){
				toolButtonList.add(new ButtonInfo(ResourceFactory.getProperty("button.abolish")
						,"templateTool_me.delete_obj()"));
			}
			//保存
			if (bSave){
				toolButtonList.add(new ButtonInfo(ResourceFactory.getProperty("button.save")
						,"templateTool_me.save('false','true','0')"));
			}
			//计算
			if (bBatchCal&&compute){
				toolButtonList.add(new ButtonInfo(ResourceFactory.getProperty("button.computer")
						,"templateTool_me.batchCalc()"));
			}
			//上会
			if (bSubMeeting){
			    /*toolButtonList.add(new ButtonInfo(ResourceFactory.getProperty("t_template.jobtitle.submeeting")
			            ,"templateTool_me.subMeeting()"));*/
			}
			
			if (!isSplitButtonInLast(toolButtonList) &&toolButtonList.size()>0){
			    splitButton = new ButtonInfo(ButtonInfo.BUTTON_SPLIT);//占位符
			    toolButtonList.add(splitButton);
			}
			String entryTabid = SystemConfig.getPropertyValue("entryTabid");//入职表单(入库)
			List<String> entryTabidList = Arrays.asList(entryTabid.split(","));
			if (entryTabidList.contains(tableParamBo.getTabId() + "") && bCanEdit) {
				toolButtonList.add(buildButtonInfo("指纹、人脸录入","templateTool_me.fingerprintinfo()","template_fingerprintButton"));
			}
			if (bNeedSp){
				//首次报批
				if (bStartApply){
					String desc=getApplyObjectName(taskId, String.valueOf(tableParamBo.getTabId()));
					toolButtonList.add(buildButtonInfo(desc,"templateTool_me.apply()","template_applyButton"));
				}

				//驳回或者手工自定义流程
				if (bReject&&reject||(def_flow_self&&isBReject_self)){//添加非发起节点
					toolButtonList.add(buildButtonInfo(ResourceFactory.getProperty("button.reject"),"templateTool_me.assign(2)","template_rejectButton"));
				}
				
				//审批(自动流转)				
				if (bProcess){					
					String desc=ResourceFactory.getProperty("button.appeal");
					if(bAutoSp||def_flow_self){//自动流程或者手工自定义流程
						if(def_flow_self){
							WF_Instance ins=new WF_Instance(tablebo,this.conn);
							if (taskId.contains(",")){//批量
								toolButtonList.add(buildButtonInfo("审批","templateTool_me.assign(1)","template_applyButton"));
							}else{
								if(ins.isEndNode(Integer.parseInt(taskId),tablebo)){
						        	toolButtonList.add(buildButtonInfo("批准","templateTool_me.assign(1)","template_applyButton"));
								}else
									toolButtonList.add(buildButtonInfo(desc,"templateTool_me.assign(1)","template_applyButton"));
							}
						}else{
							if (taskId.contains(",")){//批量
								 int size = this.getNodeWithTaskid(taskId, String.valueOf(tableParamBo.getTabId()));
								 if(size==1){
									desc=getApplyObjectName(taskId.split(",")[0], String.valueOf(tableParamBo.getTabId()));
									if(ResourceFactory.getProperty("button.appeal").equals(desc)){
									    toolButtonList.add(buildButtonInfo(desc,"templateTool_me.assign(1)","template_applyButton"));  //继续报批
									}else if(ResourceFactory.getProperty("button.submit").equals(desc)){
									    toolButtonList.add(buildButtonInfo(desc,"templateTool_me.assign(3)","template_applyButton"));  //批准 提交
									}else{
									    toolButtonList.add(buildButtonInfo(desc,"templateTool_me.assign(1)","template_applyButton"));  //在节点中自己定义的名称
									}
								 }else{
									 toolButtonList.add(buildButtonInfo("报送&确认","templateTool_me.assign(1)","template_applyButton"));  
								 }
							}
							else {
								desc=getApplyObjectName(taskId, String.valueOf(tableParamBo.getTabId()));
								if(ResourceFactory.getProperty("button.appeal").equals(desc)){
								    toolButtonList.add(buildButtonInfo(desc,"templateTool_me.assign(1)","template_applyButton"));  //继续报批
								}else if(ResourceFactory.getProperty("button.submit").equals(desc)){
								    toolButtonList.add(buildButtonInfo(desc,"templateTool_me.assign(3)","template_applyButton"));  //批准 提交
								}else{
								    toolButtonList.add(buildButtonInfo(desc,"templateTool_me.assign(1)","template_applyButton"));  //在节点中自己定义的名称
								}
							}
						}
					}
					else if(bHandSp&&!def_flow_self){//审批   手工但没有定义自定义流程
					    desc=ResourceFactory.getProperty("button.apply");
					    toolButtonList.add(buildButtonInfo(desc,"templateTool_me.assign(4)","template_applyButton"));
					}	
				}
			}
			else {//非报批提交
				if (bSubmit){
					toolButtonList.add(buildButtonInfo(ResourceFactory.getProperty("button.submit")
							,"templateTool_me.submit()","template_submitButton"));
				}
			}
			if (!isSplitButtonInLast(toolButtonList) && toolButtonList.size()>0){
			    splitButton = new ButtonInfo(ButtonInfo.BUTTON_SPLIT);//占位符
			    toolButtonList.add(splitButton);
			}
			//列表、卡片 不控制全显示
			ButtonInfo cardButton =new ButtonInfo(ResourceFactory.getProperty("kjg.title.listtable")
					,"templateTool_me.changeView()");
			cardButton.setId("cardButton");
			if ("list".equalsIgnoreCase(view_type)){
				cardButton.setText(ResourceFactory.getProperty("button.card"));
			}
			 
			if (!bSelfApply&&!(operationType==8||operationType==9)){
			    toolButtonList.add(cardButton);	
			}
			
			if (bOutWord && (bSelfApply||((("1".equals(isStartNode)||"3".equals(returnFlag))&&isSelf)))){
                //toolButtonList.add(new ButtonInfo(ResourceFactory.getProperty("button.print")
                //    ,"print()"));
				/*ButtonInfo wordButton = new ButtonInfo(ResourceFactory.getProperty("button.outword"),"outword(1,1)");
				toolButtonList.add(wordButton);
				wordButton.setIcon("/images/outword.png");*/
				toolButtonList.add(new ButtonInfo(TemplateLayoutBo.getMenuStr("导出", "", "/images/outword.png",  selectOfficeORWps(1, "1", 0))));
            }
			//自助业务办理-导出pdf 目前使用打印权限 
            if (bOutPdf && (bSelfApply||((("1".equals(isStartNode)||"3".equals(returnFlag))&&isSelf)))){
                //ResourceFactory.getProperty("edit_report.importPDF")
               /* ButtonInfo pdfButton = new ButtonInfo(ResourceFactory.getProperty("template_new.preview")//"预览"
                        ,"outPdf(1,1)");
                toolButtonList.add(pdfButton);
                pdfButton.setIcon("/images/outpdf.png");*/
            	 toolButtonList.add(new ButtonInfo(TemplateLayoutBo.getMenuStr("导出", "", "/images/outpdf.png",  selectOfficeORWps(1, "1", 1))));
                
            }
			//审批过程
			if(process&&bSpProcess&&"0".equals(approveFlag)){
				ButtonInfo viewProcessButton =new ButtonInfo(ResourceFactory.getProperty("rsbd.wf.sploop")
                        ,"templateTool_me.openShowyj()");
				viewProcessButton.setId("viewProcessButton");
				toolButtonList.add(viewProcessButton);
			}
			//发表意见
			if(bPubOpinion){
				toolButtonList.add(new ButtonInfo(ResourceFactory.getProperty("general.template.publishopinion")
						,"templateTool_me.pubOpinion()"));
			}
			//自定义审批
			if (bDefFlowSelf){
				ButtonInfo selfButton = new ButtonInfo(ResourceFactory.getProperty("t_template.approve.selfdefflow"),
                        "templateTool_me.showDefFlowSelf()");
	           	toolButtonList.add(selfButton);
			}
			//Liuyz
			if(refercEnable&&"0".equals(tableParamBo.getAutosync_beforechg_item()))
				toolButtonList.add(new ButtonInfo("刷新","templateTool_me.refreshData()"));
			//撤回按钮
			if(("1".equals(recallflag)||canRevoke)&&"0".equals(browseprint)){
				toolButtonList.add(new ButtonInfo("撤回","templateTool_me.recallTask()"));
			}
			if (!"14".equals(returnFlag)){
			    if (!isSplitButtonInLast(toolButtonList)){//
	                splitButton = new ButtonInfo(ButtonInfo.BUTTON_SPLIT);//占位符
	                toolButtonList.add(splitButton);
	            }
			    String info =ResourceFactory.getProperty("button.return");
			    if ("13".equals(returnFlag)){
			        info =ResourceFactory.getProperty("button.close");
			    }
			    toolButtonList.add(new ButtonInfo(info,"templateTool_me.returnBack(false)"));
			}
			
		
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return toolButtonList;
	}
	
	/***
	 * 兼容导出wps与导出word按钮设置
	 * @param allOrPart
	 * @param downtype
	 * @param type
	 * @return
	 */
	private ArrayList<LazyDynaBean> selectOfficeORWps(int allOrPart,String downtype,int type){
	    ArrayList<LazyDynaBean> list = new ArrayList<LazyDynaBean>();
	    LazyDynaBean officeBean = new LazyDynaBean();
	    officeBean.set("text", "兼容Office");
        LazyDynaBean wpsBean = new LazyDynaBean();
        wpsBean.set("text", "兼容WPS");
	    if(type==0) {
	      //word
	        officeBean.set("handler", "outword("+allOrPart+","+downtype+",'0')");
	        wpsBean.set("handler", "outword("+allOrPart+","+downtype+",'1')");
	    }else if(type==1) {
	        //pdf
	        officeBean.set("handler", "outPdf("+allOrPart+","+downtype+",'0')");
	        wpsBean.set("handler", "outPdf("+allOrPart+","+downtype+",'1')");
	    }
        list.add(officeBean);
        list.add(wpsBean);
	    return list;
	}
	
	/**
	 * 生成导出压缩包的菜单
	 * @param desc 菜单描述
	 * @param type 导出类型
	 * @param allOrPart 全部还是部分
	 * @return
	 */
	private LazyDynaBean getpdfOrWordList(String desc, int type, int allOrPart,int infor_type) {
		LazyDynaBean oneBean = new LazyDynaBean();
		ArrayList pdfOrWordList = new ArrayList();
		String person_orgStr_one="一人";
		String person_orgStr_All="多人";
		if(infor_type!=1) {
			person_orgStr_one="一机构";
			person_orgStr_All="多机构";
		}
		if(type==1){
			pdfOrWordList.add(getMenuBean(person_orgStr_one+"一文档",
	                "outPdf("+allOrPart+",0)","",new ArrayList()));
			pdfOrWordList.add(getMenuBean(person_orgStr_All+"一文档",
	                "outPdf("+allOrPart+",1)","",new ArrayList()));
		}else if(type==2){
	        pdfOrWordList.add(getMenuBean(person_orgStr_one+"一文档",
	                "outword("+allOrPart+",0)","",new ArrayList()));
	        pdfOrWordList.add(getMenuBean(person_orgStr_All+"一文档",
	                "outword("+allOrPart+",1)","",new ArrayList()));
		}
		oneBean.set("text", desc);
	    oneBean.set("menu", pdfOrWordList);
		return oneBean;
	}
	private boolean getActor_Type(String task_id) {
    	boolean isSelf = false;
    	RowSet rset=null;
    	try {
	    	ContentDAO dao=new ContentDAO(this.conn);
	    	StringBuffer sb = new StringBuffer();
	    	sb.append("select  twi.actor_type from t_wf_task twt,t_wf_instance twi where twt.ins_id = twi.ins_id  and twt.task_id="+task_id);
			rset = dao.search(sb.toString());
			if(rset.next()){
				int actor_type = rset.getInt("actor_type");
				if(actor_type==1)
					isSelf = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rset);
		}
		return isSelf;
	}
    private int getNodeWithTaskid(String taskId, String tabid) {
    	RowSet rset=null;
    	int size = 0;
    	ContentDAO dao=new ContentDAO(this.conn);
        try {
            rset=dao.search("select DISTINCT node_id from t_wf_task where task_id in("+taskId+")");
            if(rset.next()){
                size++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{
            PubFunc.closeDbObj(rset);
        }
        return size;
	}
    /**
     * 判断当前审批节点是否设置了控制导出按钮设置
     * 如果设置为true则只有在报批后才可以导出
     * @param tabid
     * @param task_id
     * @param tablebo
     * @return
     */
    private LazyDynaBean nodeBtnExtparam(int tabid,String task_id,TemplateTableBo tablebo,HashMap formMap) {
    	RowSet rs=null;
    	try {
    		int node_id=-1;
    		int state=0;//单子是否处理标识
    		if(!tablebo.isBsp_flag()) {//不需要审批
    			return null;
    		}
			ContentDAO dao=new ContentDAO(this.conn);
			rs=dao.search("select distinct node_id,state from t_wf_task_objlink where tab_id=? and task_id in ("+task_id+")", Arrays.asList(tabid));
			while(rs.next()) {
				node_id=rs.getInt("node_id");
				state=rs.getInt("state");
			}
			//return_flag==3 通过我的申请查看时，可认为是发起节点 
			if(formMap.containsKey("return_flag")) {
				if("3".equals(formMap.get("return_flag"))||"0".equals(task_id)) {
					if(node_id!=-1) {//我的申请查看数据可以认为已报批，state应该为1
						state=1;
					}
					node_id=-1;
				}
			}
			if(node_id==-1) {
				rs=null;
				rs=dao.search("select node_id from t_wf_node where tabid=? and nodetype=1 ",Arrays.asList(tabid));
				while(rs.next()) {
					node_id=rs.getInt("node_id");
				}
			}
			WF_Node node=new WF_Node(node_id, conn,tablebo);
			
			LazyDynaBean bean=node.getOutSetting(node.getExt_param(),node.getNodetype());
			if(bean!=null) {
				String applied_out=(String)bean.get("applied_out");//applied_out:报批后允许输出
				if(StringUtils.isNotEmpty(applied_out)) {
					if("true".equalsIgnoreCase(applied_out)) {
						if(state==1) {
							bean.set("isshowoutBtn", true);
						}else {
							bean.set("isshowoutBtn", false);
							bean.set("word", false);
							bean.set("pdf", false);
						}
					}else {
						bean.set("isshowoutBtn", true);
					}
					String out_pages=(String)bean.get("pages");//pages:输出的页签号
					if(StringUtils.isNotEmpty(out_pages)) {
						formMap.put("out_pages", PubFunc.encrypt(out_pages));
					}else {
						formMap.put("out_pages", "");
						bean.set("isshowoutBtn", false);//设置所有页不导出时隐藏导出按钮
						bean.set("word", false);
						bean.set("pdf", false);
					}
				}else {
					bean.set("isshowoutBtn", true);
				}
			}
			return bean;
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeIoResource(rs);
		}
    	return null;
    }
    
	/*
     * 是否显示上会 按钮
     * */
    public boolean isDisSubMeetingButton(String tabid){
        boolean b=false;
        try{
            DomXml  domXml = new DomXml();
            String templateId= ","+domXml.getJobtitleTemplateByType(this.conn, "5")+",";
            if (templateId.contains(","+tabid+",")){
                b=true;
                return b;
            }
            templateId= ","+domXml.getJobtitleTemplateByType(this.conn, "6")+",";
            if (templateId.contains(","+tabid+",")){
                b=true;
                return b;
            }
    
        }catch(Exception e){
            e.printStackTrace();
        }
        return b;
    }
	/**
	 * 得到下一环节审批对象的名称，为了在审批或提交按钮上显示
	 * 审批对象的名称
	 * @param task_id
	 * @param ins_id
	 * @return
	 * @throws GeneralException
	 */
	private String getApplyObjectName(String task_id,String tabid)throws GeneralException
	{
		String applyName=ResourceFactory.getProperty("button.appeal");
		ArrayList actorlist=new ArrayList();
		ArrayList nextlist=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);		
		WF_Node wf_node=null;
		boolean b_end=false;
		try
		{
			int node_id=-1;
		    if(!"0".equals(task_id))
		    {
		        RowSet rset=null;
		        try {
		            rset=dao.search("select node_id from t_wf_task where task_id="+task_id);
		            if(rset.next())
		                node_id=rset.getInt(1);
		            
		        } catch (SQLException e) {
		            e.printStackTrace();
		        }
		        finally{
		            PubFunc.closeDbObj(rset);
		        }
		    }
		    if(node_id==-1||"0".equals(task_id))
		    {
		        wf_node=new  WF_Node(this.conn);
		        RecordVo vo=wf_node.getBeginNode(String.valueOf(tabid));
		        node_id=vo.getInt("node_id");
		    }
		    wf_node=new  WF_Node(node_id,this.conn); 
		    if(wf_node.getNodename()==null&&wf_node.getNodetype()==0&&wf_node.getExt_param()==null&&wf_node.getTabid()==null){
		    	throw new GeneralException("流程已变更，此流程节点已不存在，请删除待办，让发起人重新发起流程！");
		    }
		    String sp_flag=wf_node.getSpFlag(wf_node.getExt_param());
		    if(sp_flag.length()>0)
		    {
		        applyName=sp_flag;
		    }
		    else
		    {
		        ArrayList nextNodeList=wf_node.getNextNodeList(null); //获得下一节点
		        if(nextNodeList!=null&&nextNodeList.size()>0){
		            WF_Node nextnode=(WF_Node)nextNodeList.get(0);
		            if(nextnode.getNodetype()==9){
		                applyName=ResourceFactory.getProperty("button.submit");
		            }
		        }
		    }
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return applyName;
	}
	
	/** 
	* @Title: checkFlagHmuster 
	* @Description: 是否显示高级花名册
	* @param @param relatTableid
	* @param @return
	* @return boolean
	*/ 
	private boolean checkFlagHmuster(String relatTableid){
		boolean checkflag = false;
		String temp=this.userview.getResourceString(5);
		if(temp.trim().length()==0) 
			temp="-1";
		StringBuffer strsql = new StringBuffer();
		strsql.append("SELECT tabid FROM muster_name where ");
		strsql.append("nmodule='5'");
		strsql.append(" and nPrint="+relatTableid);
		ContentDAO dao=new ContentDAO(this.conn);
		try {
			RowSet rSet=dao.search(strsql.toString());
			String tabid ="-1";
			while(rSet.next()){
				tabid = rSet.getString("tabid");
				//bug 32551  如果登陆人没有高级花名册权限不显示打印高级花名册
				if(this.userview.isAdmin())
				{
					checkflag=true;
					break;
				}
				else if(this.userview.isHaveResource(IResourceConstant.HIGHMUSTER, tabid))
				{
					checkflag=true;
					break;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return checkflag;
	}
	
	
	/** 
	* @Title: haveFunctionIds 
	* @Description:是否有权限 
	* @param @param fuctionIds 权限号 以逗号分隔
	* @param @return
	* @return boolean
	*/ 
	public boolean haveFunctionIds(String fuctionIds)
	{
		return TemplateFuncBo.haveFunctionIds(fuctionIds, userview);
	}
	

	/**
	 * 递归生成功能导航菜单的json串
	 * @param name 菜单名
	 * @param list 菜单内容
	 * @return
	 */
	public String getMenuStr(String name,String id, ArrayList list){
		return TemplateLayoutBo.getMenuStr(name,id, list);
		
	}
	/**
	 * 生成菜单的bean
	 * @param text 名称
	 * @param handler 触发事件
	 * @param icon 图标
	 * @return
	 */
	public LazyDynaBean getMenuBean(String text,String handler,String icon,ArrayList list){
		return TemplateLayoutBo.getMenuBean(text, handler,icon,list);
	}
	/**
	 * 生成菜单的bean
	 * @param text 名称
	 * @param handler 触发事件
	 * @param icon 图标
	 * @return
	 */
	public LazyDynaBean getDateMenuBean(String handler,String icon,String xtype,String value){
		return TemplateLayoutBo.getDateMenuBean(handler,icon,xtype,value);
	}
	/**
	 * gaohy(人事异动-手工选择)
	 * 生成菜单的bean
	 * @param text 名称
	 * @param id 主键
	 * @param handler 触发事件
	 * @param icon 图标
	 * @return
	 */
	public LazyDynaBean getMenuBean(String text,String id,String handler,String icon,ArrayList list){
		LazyDynaBean bean = new LazyDynaBean();
		try{
			if(text!=null&&text.length()>0)
				bean.set("text", text);
			if(id!=null&&id.length()>0)
				bean.set("id", id);
			if(icon!=null&&icon.length()>0)
				bean.set("icon", icon);
			if(handler!=null&&handler.length()>0){
				if(list!=null&&list.size()>0){
					bean.set("menu", list);
				}else{
					bean.set("handler", handler);
				}				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bean;
	}
	
	
	/**   
	 * @Title: isSplitButtonInLast   
	 * @Description: 判断最后一个是否是分隔符  
	 * @param @param toolButtonList
	 * @param @return 
	 * @return boolean 
	 * @throws   
	*/
	private boolean isSplitButtonInLast(ArrayList toolButtonList) {
        boolean b = false;
        if (toolButtonList.size() > 0) {
            Object obj = (Object) toolButtonList.get(toolButtonList.size()-1);
            if (obj instanceof ButtonInfo) {
                ButtonInfo objButton = (ButtonInfo) obj;
                if (ButtonInfo.BUTTON_SPLIT.equals(objButton.getInnerHTML())) {
                    b = true;
                }
            }
        }
        return b;
    }
    //liuyz bug31743 用户点击报批、审批、驳回、提交后将按钮置灰，防止用户多次点击产生错误数据
	private ButtonInfo buildButtonInfo(String desc,String JSfunctionName,String id)
	{
		ButtonInfo buttonInfo=new ButtonInfo(desc,JSfunctionName);
		buttonInfo.setId(id);
		return buttonInfo;
	}
	
	private Boolean hasRevokeButton(String moduleId, String taskId )
	{
		Boolean hasRevoke=true;
		RowSet rowset=null;
		RowSet rowset1=null;
		try {
			ContentDAO dao=new ContentDAO(this.conn);
			if(taskId.startsWith(","))
				taskId = taskId.substring(1,taskId.length());
			String [] taskarr  = taskId.split(",");
			if(taskarr.length>1)
				hasRevoke = false;
			else{
				//判断是不是发起人
				boolean isStart = false;
				StringBuffer strsql=new StringBuffer();
				strsql.append("select 1 from t_wf_instance twi where twi.finished=2 and ");//bug 50691 撤回的单据应该排除掉已结束的。
				strsql.append("((twi.actor_type=4 and lower(twi.actorid)='"+this.userview.getUserName().toLowerCase()+"') ");
				if(this.userview.getA0100()!=null&&!"".equals(this.userview.getA0100()))
					strsql.append(" or (twi.actor_type=1 and lower(twi.actorid)='"+this.userview.getDbname().toLowerCase()+this.userview.getA0100()+"') ");
				strsql.append(" ) and  twi.ins_id = (select tt.ins_id from t_wf_task tt where tt.task_id = '"+taskId+"') ");
				rowset1=dao.search(strsql.toString());
				if(rowset1.next())
					isStart = true;
				if(isStart){
					strsql.setLength(0);
					strsql.append("select count(*) num from t_wf_task twt where twt.task_type=2 ");
					strsql.append("and twt.bread=1 and twt.ins_id = (select tt.ins_id from t_wf_task tt where tt.task_id = "+taskId+") ");
					rowset = dao.search(strsql.toString());
					if(rowset.next())
					{
						int num = rowset.getInt("num");
						if(num>0)
						{
							hasRevoke=false;
						}
					}
				}else
					hasRevoke = false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rowset);
			PubFunc.closeDbObj(rowset1);
		}
		return hasRevoke;
	}
}
