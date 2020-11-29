package com.hjsj.hrms.transaction.general.template;

import com.hjsj.hrms.businessobject.general.template.DefFlowSelfBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SearchDefFlowSelfTrans.java</p>
 * <p>Description>:自定义审批流程界面类</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Dec 26, 2013 5:54:31 PM</p>
 * <p>@author:wangrd</p>
 * <p>@version: 6.0</p>
 */
public class SearchDefFlowSelfTrans extends IBusiness {

    public void execute() throws GeneralException {
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        HashMap hmMap = (HashMap) this.getFormHM().get("requestPamaHM");
        String tab_id = (String) hmMap.get("tabid");
        String task_id = (String) hmMap.get("task_id");
        /**安全平台,需要验证是taskid是否存在于后台中**/
        /*注释掉 存在误报的情况，暂时没查找原因 wangrd 2015-04-10
        HashMap templateMap = (HashMap) this.userView.getHm().get("templateMap");
        if(templateMap!=null&&!templateMap.containsKey(task_id)){
        	throw new GeneralException(ResourceFactory.getProperty("lable.rsbd.modify.taskid"));
        }
        */
        String ins_id = (String) hmMap.get("ins_id");
        String node_id = (String) hmMap.get("node_id");
        String fromflag = (String) hmMap.get("fromflag");
        if (tab_id==null) tab_id="0";
        if (task_id==null) task_id="0";
        if (ins_id==null) ins_id="-1";
        if (node_id==null) node_id="0";
        if ("0".equals(ins_id)) ins_id="-1";
        if (fromflag==null) fromflag="card"; 
        
        
        try{
            DefFlowSelfBo selfBo =new DefFlowSelfBo(this.frameconn,this.userView,
                     Integer.parseInt(tab_id),Integer.parseInt(task_id),
                     Integer.parseInt(ins_id),Integer.parseInt(node_id));
            
            ArrayList list = selfBo.getBeanList();
            this.getFormHM().put("defFlowSelfList", list);
            String strXml= selfBo.getStrXml();
            this.getFormHM().put("defFlowSelfXml", SafeCode.encode(strXml));
            this.getFormHM().put("fromflag",fromflag);

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

  


    }

}
