package com.hjsj.hrms.module.template.templatetoolbar.apply.transaction;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.module.template.templatetoolbar.apply.businessobject.TemplateApplyPrepareBo;
import com.hjsj.hrms.module.template.utils.TemplateBo;
import com.hjsj.hrms.module.template.utils.TemplateFuncBo;
import com.hjsj.hrms.module.template.utils.javabean.TemplateFrontProperty;
import com.hjsj.hrms.module.template.utils.javabean.TemplateParam;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:TemplateApplyPrepareTrans.java</p>
 * <p>Description>报批之前的检查:    
 * //判断是否选中记录
    //是否符合业务规则
    //是否超编
    //判断必填项
    //校验公式</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2016-3-25 下午03:13:00</p>
 * <p>@author:wangrd</p>
 * <p>@version: 7.0</p>
 */
public class TemplateApplyPrepareTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
	    try {
            TemplateFrontProperty frontProperty = new TemplateFrontProperty(this.getFormHM());
            String sysType = frontProperty.getSysType();
            String moduleId = frontProperty.getModuleId();
            String returnFlag = frontProperty.getReturnFlag();
            String tabId = frontProperty.getTabId();
            String taskId = frontProperty.getTaskId();            
            String inforType = frontProperty.getInforType();
            String applyFlag = (String)this.getFormHM().get("applyFlag");
            String fillInfo = (String) this.userView.getHm().get("fillInfo");
            boolean bReject="2".equals(applyFlag);//驳回
            boolean bBatchApprove = frontProperty.isBatchApprove();
            String checkdata = (String)this.getFormHM().get("checkdata");
            String checkvalue = (String)this.getFormHM().get("checkvalue");
            //验证码
            if(checkdata!=null){
            	checkdata = SafeCode.decode(PubFunc.decryption(checkdata));
            	if(!checkdata.equalsIgnoreCase(checkvalue)){
            		this.getFormHM().put("checkflag", "false");
            		return;
            	}
            }
            TemplateBo templateBo = new TemplateBo(this.getFrameconn(), this.userView, Integer.parseInt(tabId));
            TemplateTableBo tablebo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tabId),this.userView);
            templateBo.setModuleId(moduleId);
            templateBo.setTaskId(taskId);
            TemplateParam paramBo = templateBo.getParamBo();
            String info ="";
            // 报批时判断调动人员库 是否设置了目标库
            if (paramBo.getOperationType() == 4 
                    && (paramBo.getDest_base() == null || "".equals(paramBo.getDest_base())))
                throw new GeneralException(ResourceFactory.getProperty("error.notdefine.desddbase"));
            // 单据正在处理，不允许重复申请
            if ("0".equals(taskId)){
                info = templateBo.validateExistData();
                if (info.length() > 0)
                    throw new GeneralException(info);
            }
            TemplateApplyPrepareBo prepareBo=new TemplateApplyPrepareBo(this.frameconn,this.userView,paramBo,frontProperty); 
            //"1"校验是否选中 ； "2" 校验业务规则  "3"校验编制 
            //校验是否选中
            String validateFlag="1"; 
            if(!"1".equals(fillInfo))
            	info =prepareBo.validateIsSelect(applyFlag);//校验是否选中
            //校验目标库是否有当前唯一指标值对应记录 暂定只在起草时校验
            if(info.length()<1 &&"0".equals(taskId)&&paramBo.getInfor_type()==1&&(paramBo.getOperationType()==0||
            		paramBo.getOperationType()==1||paramBo.getOperationType()==2||paramBo.getOperationType()==4)) {//0,1,2,4
            	info = prepareBo.validateExistOnlyData();
            }
            //校验流程是否关联审批关系
            if(info.length()<1 &&"0".equals(taskId)&&paramBo.getSp_mode()==0&&paramBo.isBsp_flag()) {
            	info = prepareBo.validateApplyRelation();
            }
            //校验业务规则
            if (info.length()<1 && (!bReject)){
                validateFlag ="2";
                /* 判断操作类型是否是0的0代表不加业务判断规则非0要加业务判断规则 */
                if (paramBo.getOperationType() != 0 && paramBo.getOperationType() != 5) {
                    info =prepareBo.judgeBusinessRule();
                }
            }
            
            ArrayList taskList =prepareBo.getTaskList(frontProperty.getTaskId());              
          //自动计算 保存时已经进行自动计算，这里不再进行。
            /*if (info.length() < 1  && (!bReject) ) {
            	autoCalc(templateBo,paramBo,taskList,taskId);
			}*/
            
           // 校验必填项及审核公式
            if (info.length()<1 && (!bReject)){
                prepareBo.validateMustFillItem(tablebo,taskList);
                prepareBo.checkLogicExpress(templateBo, taskList);
            }
            //单位校验
            if((paramBo.getInfor_type()==2||paramBo.getInfor_type()==3) && (!bReject)) {//如果是单位部门或岗位
                String srcTab =templateBo.getTableName(moduleId,Integer.parseInt(tabId), taskId); 
                StringBuffer strsql=new StringBuffer(""); 
                if("0".equals(taskId)){
                	strsql.append("select * from ");
                	strsql.append(srcTab);
                	strsql.append(" where submitflag=1");
                	ContentDAO dao = new ContentDAO(this.frameconn);
                    this.frowset=dao.search(strsql.toString());
                    HashMap tableColumnMap=new HashMap();
                    ResultSetMetaData mt=this.frowset.getMetaData();
                    for(int i=1;i<=mt.getColumnCount();i++)
                    {
                        String columnName=mt.getColumnName(i);
                        tableColumnMap.put(columnName.toLowerCase(),"1");
                    }
                    tablebo.validateSysItem(tableColumnMap);
                    if(paramBo.getOperationType()==5) 
                    {
                        tablebo.checkNewOrgFillItem(strsql.toString(),paramBo.getOperationType());
                    }
                    if(paramBo.getOperationType()==8||paramBo.getOperationType()==9)
                    {
                        tablebo.checkSelectedRule(strsql.toString(),srcTab,"");
                    }
                    if(paramBo.getOperationType()==7) {//撤销机构验证机构下是否还有人员
                    	String havePerson = tablebo.checkIsHavePerson(strsql.toString(),"");
                    	if(havePerson.length()>1&&info.length()<1) {
                    		validateFlag ="4";//校验机构撤销
                    		info=havePerson+"组织下还有人员,是否要执行此项操作?";
                    	}
                    }
                }else{
                	strsql.append("select * from ");
					strsql.append(srcTab);  
					strsql.append(" where  exists (select null from t_wf_task_objlink where "+srcTab+".seqnum=t_wf_task_objlink.seqnum and "+srcTab+".ins_id=t_wf_task_objlink.ins_id ");
					strsql.append("  and task_id="+taskId+"  and submitflag=1  and (state is null or  state=0 )  and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) ) ) ");	
					if(paramBo.getOperationType()==8||paramBo.getOperationType()==9)
					{
						tablebo.checkSelectedRule(strsql.toString(),srcTab,taskId);
					}
                }
                
            }
            //校验编制 必须放在最后 warn方式下点确定后，此交易类就不再执行了。
            String headControlType ="";
            if (info.length()<1 && "1".equals(paramBo.getHeadCount_control())&& (!bReject)){
                validateFlag ="3";
                HashMap map =prepareBo.validateHeadCount();
                headControlType =(String)map.get("flag");//warn 或error
                info =(String)map.get("msgs");
                this.getFormHM().put("headControlType", headControlType);
            }
            
            this.getFormHM().put("info", info);
            this.getFormHM().put("validateFlag", validateFlag);            
            this.getFormHM().put("sp_mode", paramBo.getSp_mode()+"");
            
            //是否是自定义审批过程 0未勾选 1 勾选未定义(起草判断) 2 勾选定义了
            String def_flow_self="0";
            if ("1".equals(paramBo.getDef_flow_self())){
                for(int i=0;i<taskList.size();i++)
                {
                    String task_id=((RecordVo)taskList.get(i)).getString("task_id");
                    if("0".equals(task_id)){
                    	if (tablebo.isDef_flow_self(Integer.parseInt(task_id)))
                            def_flow_self="2";
                        else
                        	def_flow_self="1";
                        break;
                    }else{
                    	if (tablebo.isDef_flow_self(Integer.parseInt(task_id))){
                            def_flow_self="2";
                            break;
                        }
                    }
                } 
            }
            this.getFormHM().put("def_flow_self", def_flow_self);
            //是否弹出审批意见框。
            String no_sp_yj=paramBo.getNo_sp_yj();
            if("true".equalsIgnoreCase(SystemConfig.getPropertyValue("no_sp_opinion")))
                no_sp_yj="1";
            this.getFormHM().put("no_sp_yj", no_sp_yj);
            
            //手工-是否有批准权限
            String  approveFunc="0";
            //linbz 28494 多一个提交的权限号去掉010703
            String approveFunctionids = "400040101,32101,33001001,33101001,2701501,0C34801,32001,324010102,325010102,3800701";
        	if (TemplateFuncBo.haveFunctionIds(approveFunctionids, this.userView)){
	        	if(paramBo.getEndUser()!=null && paramBo.getEndUser().length()>0&&"0".equals(def_flow_self)){/**不是自定义审批*/
	        		if ("1".equals(paramBo.getEndUserType())){//自助用户
            			if ((this.userView.getDbname()+this.userView.getA0100()).equals(paramBo.getEndUser())){
            				approveFunc="1";	
            			}
            		}
            		else {
            			if ((this.userView.getUserName()).equals(paramBo.getEndUser())){
            				approveFunc="1";	
            			}
            		}
	        	}else{
	        		approveFunc="1";
	        	}
        	}
            this.getFormHM().put("approveFunc", approveFunc);
            //是否是开始节点，开始节点无驳回按钮
            String startFlag="0";
            if (paramBo.getSp_mode()==1){//手工报批
                for(int i=0;i<taskList.size();i++)
                {
                    String task_id=((RecordVo)taskList.get(i)).getString("task_id");
                    startFlag = templateBo.isStartNode(task_id);
                    if ("1".equals(startFlag)){
                        break;
                    }
                }  
                this.getFormHM().put("startFlag", startFlag);
                //业务办理人 是否显示同意按钮 (不是自定义审批流程的情况)
                if (!"0".equals(taskId)){
                	boolean bAgree=false;
                	String agreeFunctions = "400040117,0C34817,32017,33101017,3800717,3900302,33001017,2701517,324010117,325010117,32117,010736";
                	if (TemplateFuncBo.haveFunctionIds(agreeFunctions, this.userView)){
	                	if (paramBo.getEndUser()!=null && paramBo.getEndUser().length()>0&&"0".equals(def_flow_self)){
	                		if ("1".equals(paramBo.getEndUserType())){//自助用户
	                			if (!(this.userView.getDbname()+this.userView.getA0100()).equals(paramBo.getEndUser())){
	                				bAgree=true;	
	                			}
	                		}
	                		else {
	                			if (!(this.userView.getUserName()).equals(paramBo.getEndUser())){
	                				bAgree=true;	
	                			}
	                		}
	                		if (bAgree){
	                			this.getFormHM().put("endUserType", paramBo.getEndUserType());
	                			this.getFormHM().put("endUser", PubFunc.encrypt(paramBo.getEndUser()));
	                			String enduser_fullname=getenduser_fullname(tablebo.getEnduser(),tablebo.getEndusertype());
	                			this.getFormHM().put("endUserFullName", enduser_fullname);
	                		}
	                	}
                	}
                	if (bAgree)
                		this.getFormHM().put("displayAgreeBtn", "true");
                	else 
                		this.getFormHM().put("displayAgreeBtn", "false");
                }
            }
          //不走审批直接提交选择通知模板后是否弹出页面
            if("0".equals(applyFlag)){//不走审批
            	//judgeoperationtype(tabId);
            	String isSendMessage="0";
    			if(tablebo.isBemail()&&tablebo.isBsms())
    				isSendMessage="3";
    			else if(tablebo.isBemail())
    				isSendMessage="1";
    			else if(tablebo.isBsms())
    				isSendMessage="2";
    			this.getFormHM().put("isSendMessage", isSendMessage);
            }
			//判断是否有抄送通知的权限。
            if(!this.userView.hasTheFunction("2701515")&&!this.userView.hasTheFunction("0C34815")
                    &&!this.userView.hasTheFunction("32015")&&!this.userView.hasTheFunction("325010115")&&!this.userView.hasTheFunction("324010115")&&!this.userView.hasTheFunction("010701")&&!this.userView.hasTheFunction("32115")&&!this.userView.hasTheFunction("3800715")){
            	this.getFormHM().put("isSendCopyMessage", "0");//是否有抄送权限。
            }else{
            	this.getFormHM().put("isSendCopyMessage", "1");//是否有抄送权限。
            }
            //手工审批时报批人不能选自己
            ArrayList deprecateList = new ArrayList();
            if(this.userView.getA0100()!=null&&this.userView.getA0100().trim().length()>0)
            	deprecateList.add(PubFunc.encrypt(this.userView.getDbname()+this.userView.getA0100()));
            this.getFormHM().put("deprecate", deprecateList);
            //确定是不是驳回的单子
            String taskIntoType = "08";//报审
            if(!"0".equals(taskId)){
	            String[] tasklist=StringUtils.split(taskId,",");
				for(int i=0;i<tasklist.length;i++){
					RecordVo vo=new RecordVo("t_wf_task");
					ContentDAO dao=new ContentDAO(this.frameconn);
					vo.setString("task_id", tasklist[i]);
					vo=dao.findByPrimaryKey(vo);
					if ("07".equals(vo.getString("state"))){
						taskIntoType="07";
	                    break;
	                }
				}
            }
			this.getFormHM().put("taskIntoType", taskIntoType);

            
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
		
	}
	
	private String getenduser_fullname(String enduser,String endusertype)
	{
		String fullname="";
		if(enduser.trim().length()>0&&endusertype.trim().length()>0)
		{
			try
			{
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				if("0".equals(endusertype)) //用户
				{
					this.frowset=dao.search("select fullname from operuser where username='"+enduser+"'");
				}
				else
				{
					this.frowset=dao.search("select a0101 fullname from "+enduser.trim().substring(0,3)+"a01 where a0100='"+enduser.trim().substring(3)+"'");
				}
				if(this.frowset.next())
				{
					if(this.frowset.getString(1)!=null&&this.frowset.getString(1).trim().length()>0)
						fullname=this.frowset.getString(1);
					else
						fullname=enduser;
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return fullname;
	}
	
	/**  自动计算
	 * @param templateBo
	 * @param paramBo
	 * @param taskList
	 * @param taskId
	 * @throws GeneralException
	 */
	private void autoCalc(TemplateBo templateBo,TemplateParam paramBo,ArrayList taskList,String taskId) throws GeneralException {	   
		Boolean bCalc=false;		
		if("0".equals(taskId)|| "1".equals(templateBo.isStartNode(taskId))){
			if(paramBo.getAutoCaculate().length()==0){
				if("true".equalsIgnoreCase(SystemConfig.getPropertyValue("templateAutoCompute"))){
					bCalc=true;
				}
			}
			else if("1".equals(paramBo.getAutoCaculate())){
				bCalc=true;
			}	
		}else {
			if(paramBo.getSpAutoCaculate().length()==0){
				if("true".equalsIgnoreCase(SystemConfig.getPropertyValue("templateAutoCompute"))){
					bCalc=true;
				}
			}
			else if("1".equals(paramBo.getSpAutoCaculate())){
				bCalc=true;
			}
		}
		if(bCalc){
			ArrayList formulalist = templateBo.readFormula();
			if (formulalist.size() > 0) {
				String ins_ids = "";
				for (int i = 0; i < taskList.size(); i++) {
					String ins_id = ((RecordVo) taskList.get(i))
					.getString("ins_id");
					ins_ids = ins_ids + "," + ins_id;
				}
				templateBo.batchCompute(ins_ids.substring(1));
			}
		}
	}
	    
	       
}
