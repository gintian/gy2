package com.hjsj.hrms.module.template.templatetoolbar.selfdefineflow.transaction;

import com.hjsj.hrms.module.template.templatetoolbar.selfdefineflow.businessobject.SelfDefineFlowBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SelfDefineFlowTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		
		String oprflag=(String)this.getFormHM().get("oprflag");
        String tab_id = (String) this.getFormHM().get("tabid");
        String task_id = (String) this.getFormHM().get("task_id");
        String ins_id = (String) this.getFormHM().get("ins_id");
        String node_id = (String) this.getFormHM().get("node_id");
        if (tab_id==null) tab_id="0";
        if (task_id==null) task_id="0";
        if (ins_id==null) ins_id="-1";
        if (node_id==null) node_id="0";
        SelfDefineFlowBo selfBo =new SelfDefineFlowBo(this.frameconn,this.userView,
        		Integer.parseInt(tab_id),Integer.parseInt(task_id),
        		Integer.parseInt(ins_id),Integer.parseInt(node_id));
        if("saveFlow".equalsIgnoreCase(oprflag)){//保存层级
        	selfBo.delAllByTabId();//在保存之前进行删除操作
        	String bs_flag = "";//区分是报批还是报备
        	ArrayList flowId=(ArrayList) this.getFormHM().get("flowId");
        	ArrayList reportId=(ArrayList) this.getFormHM().get("reportId");
        	
        	bs_flag = "1";//指定是审批
        	selfBo.addFlow(bs_flag, flowId);//保存审批人
        	bs_flag = "3";//指定是报备
        	selfBo.addReport(bs_flag, reportId);//保存报备人
        	
        }else if("initFlow".equalsIgnoreCase(oprflag)){//初始化层级及
        	//设定一级审批人时不能选自己
            String deprecate = "";
            if(this.userView.getA0100()!=null&&this.userView.getA0100().trim().length()>0)
            	deprecate = PubFunc.encrypt(this.userView.getDbname()+this.userView.getA0100());
            this.getFormHM().put("deprecate", deprecate);
        	ArrayList listFlow = selfBo.initFlow();
        	ArrayList listReport = selfBo.initReport();
        	this.getFormHM().put("listFlow", listFlow);//将审批list传到前台
        	this.getFormHM().put("listReport", listReport);//将报备list传到前台
        }
	}

}
