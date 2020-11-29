package com.hjsj.hrms.module.recruitment.recruitprocess.transaction;

import com.hjsj.hrms.module.recruitment.recruitflow.businessobject.RecruitFlowLink;
import com.hjsj.hrms.module.recruitment.recruitprocess.businessobject.FunctionRecruitBo;
import com.hjsj.hrms.module.recruitment.recruitprocess.businessobject.RecruitProcessBo;
import com.hjsj.hrms.module.recruitment.util.FeedBackBo;
import com.hjsj.hrms.module.recruitment.util.ZpPendingtaskBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;

public class UpdateRecruitTrans extends IBusiness{

    /**
     * 流程修改
     * @throws GeneralException
     */
    @Override
    public void execute() throws GeneralException {
        try {       
            FunctionRecruitBo bo = new FunctionRecruitBo(this.frameconn, this.userView);
            //职位ID
            String z0301=PubFunc.decrypt(SafeCode.decode((String) this.getFormHM().get("z0301")));
            //人员序号
            String a0100 = SafeCode.decode((String) this.getFormHM().get("a0100"));
            
            //流程流程序号
            String link_id = SafeCode.decode((String)this.getFormHM().get("link_id"));
            //流程环节序号
            String node_id = SafeCode.decode((String)this.getFormHM().get("node_id"));
            //人员姓名
//          String a0101 = SafeCode.decode((String)this.getFormHM().get("name"));
            //邮箱
            String c0102 = SafeCode.decode((String)this.getFormHM().get("c0102"));
            //方法名
            String function_str = SafeCode.decode((String)this.getFormHM().get("function_str"));
            String now_linkId = (String)this.getFormHM().get("now_linkId");
            //用来发送待办和待办邮件通知
            ArrayList person = (ArrayList)this.getFormHM().get("person");
            ArrayList bususer = (ArrayList)this.getFormHM().get("bususer");
            
            String realfunction = function_str;
            //简历被选中的人数
            String[] a0100Array= a0100.split(","); 
            int resumeNumber = a0100Array.length;
            
            //通过操作比较特殊实际上也是做转状态操作
            if("passChoice".equals(function_str)&&StringUtils.isNotEmpty(now_linkId))
                realfunction = "toStage";
            //招聘流程集合
            UserView userView = this.getUserView();
            ArrayList stageList =(ArrayList)userView.getHm().get("stageList");
            RecruitProcessBo recruitBo = new RecruitProcessBo(this.frameconn,this.userView);
            //更改指标集合
            ArrayList functionList = new ArrayList();
            String real_nodeid="";  //招聘环节
            if(node_id.length()>2)
            {
                //当更改状态时传入的node_id为当前选中状态的resume_flag值，不需要从操作bean中进行读取
                functionList.add(link_id);
                functionList.add(node_id);
                RecruitFlowLink recruitFlowLink=new RecruitFlowLink(link_id,this.getFrameconn());
                real_nodeid=recruitFlowLink.getNode_id();
            }else{              
                functionList = bo.getFunctionList(link_id,node_id, realfunction, stageList);
                real_nodeid=node_id;
                bo.sendNotice(person, link_id, z0301, resumeNumber, functionList,true);
                bo.sendNotice(bususer, link_id, z0301, resumeNumber, functionList,false);
                //bo.getPrincipal(link_id, node_id, z0301, resumeNumber,functionList);
            } 
            
            String []a0100s = a0100.split(",");
            this.getFormHM().put("link_id", link_id);
            this.getFormHM().put("node_id",node_id);

            String z0301s = "";
            ArrayList<ArrayList<String>> valueList = new ArrayList<ArrayList<String>>();
            //应聘人员库
            String dbname = "";
            if(functionList!=null&&functionList.size()>0)
            {   
                ArrayList employeeStatusList=new ArrayList();   
                RecordVo vo=ConstantParamter.getConstantVo("ZP_DBNAME");
                if(vo!=null)
                    dbname=vo.getString("str_value"); 
                for(int i=0;i<a0100s.length;i++)
                {   
                    ArrayList<String> value = new ArrayList<String>();
                    z0301s += PubFunc.encrypt(z0301)+",";
                    ArrayList tempList=new ArrayList();
                    tempList.addAll(functionList);
                    tempList.add(z0301);
                    tempList.add(PubFunc.decrypt(a0100s[i]));
                    tempList.add(dbname);
                    employeeStatusList.add(tempList);
                    
                    value.add("");
                    value.add(PubFunc.decrypt(a0100s[i]));
                    value.add(dbname);
                    value.add(z0301);
                    valueList.add(value);
                    if("toStage".equals(realfunction))
                    	recruitBo.changeConfirmState("-1", PubFunc.decrypt(a0100s[i]), dbname, z0301, "", "");
                }
                bo.changeStatus(employeeStatusList,real_nodeid,function_str);//更改状态操作
                LazyDynaBean statusBean = (LazyDynaBean)bo.getStatus((String)functionList.get(1),link_id);
                ZpPendingtaskBo pendBo = new ZpPendingtaskBo(this.frameconn, this.userView);
                //处理待办
                pendBo.cancelPendingTask(z0301, now_linkId==null?link_id:now_linkId);
                this.getFormHM().put("link_name", (String)bo.getCustom_name(link_id).get("custom_name"));
                this.getFormHM().put("custom_name",statusBean.get("custom_name"));
                this.getFormHM().put("status",statusBean.get("status"));
            }  
            if("passChoice".equalsIgnoreCase(function_str))
            {
                this.formHM.put("nModule", "40");
            }else if("sendOffer".equalsIgnoreCase(function_str))
            {
                this.formHM.put("nModule", "60");
            }else{
                this.formHM.put("nModule", "50");
            }
            
            FeedBackBo feedBo = new FeedBackBo(this.frameconn);
            feedBo.deleteFeedBack(valueList);
            
            if("toStage".equalsIgnoreCase(function_str)){
                //获取查询方案列表
                int flag = 2;//环节范围
                ArrayList projectList = recruitBo.getProjectList(link_id,z0301,flag);  
                LazyDynaBean bean = (LazyDynaBean) projectList.get(0);
                if(projectList.size() > 1)
                    bean = (LazyDynaBean) projectList.get(1);
                
                String status_id = bean==null?node_id:(String) bean.get("status");
                this.formHM.put("status", status_id);
            }
            //获得招聘流程下一阶段的link_id
            String nextLinkId=recruitBo.getNextLinkId(link_id,stageList);
            String nextNodeId=recruitBo.getNextNodeId(link_id, stageList);
            this.getFormHM().put("next_linkId",nextLinkId);
            this.getFormHM().put("next_nodeId",nextNodeId);
            this.formHM.put("stageList", stageList);
            this.formHM.put("z0301s", z0301s);
            this.formHM.put("c0102s", c0102);
            this.formHM.put("result", Boolean.TRUE);
            this.formHM.put("function_str", function_str);
        } catch (Exception e) {
            this.formHM.put("result", Boolean.TRUE);
            e.printStackTrace();
        }finally{
            
        }
    }
}
