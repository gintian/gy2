package com.hjsj.hrms.transaction.general.template;

import com.hjsj.hrms.businessobject.general.template.DefFlowSelfBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>Title:OperationDefFlowSelfTrans.java</p>
 * <p>Description>自定义审批流程 增加、删除审批、增加删除人员，同步层级:</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Dec 26, 2013 5:55:22 PM</p>
 * <p>@author:wangrd</p>
 * <p>@version: 6.0</p>
 */
public class OperationDefFlowSelfTrans extends IBusiness {

    public void execute() throws GeneralException {
      //  HashMap hmMap = (HashMap) this.getFormHM().get("requestPamaHM");
        String oprflag=(String)this.getFormHM().get("oprflag");
        String strXml=(String)this.getFormHM().get("strXml");
        String tab_id = (String) this.getFormHM().get("tabid");
        String task_id = (String) this.getFormHM().get("task_id");
        String ins_id = (String) this.getFormHM().get("ins_id");
        String node_id = (String) this.getFormHM().get("node_id");
        if (tab_id==null) tab_id="0";
        if (task_id==null) task_id="0";
        if (ins_id==null) ins_id="-1";
        if (node_id==null) node_id="0";
        /**安全改造,判断task_id是否在后台存在**/
        HashMap templateMap = (HashMap) this.userView.getHm().get("templateMap");
        /*注释掉 存在误报的情况，暂时没查找原因 wangrd 2015-04-10
        if(templateMap!=null&&!templateMap.containsKey(task_id)){
        	throw new GeneralException(ResourceFactory.getProperty("lable.rsbd.modify.taskid"));
        }
        */
        if(strXml==null) strXml="";
        strXml=SafeCode.decode(strXml);
        strXml=PubFunc.keyWord_reback(strXml);
        DefFlowSelfBo selfBo =new DefFlowSelfBo(this.frameconn,this.userView,
                Integer.parseInt(tab_id),Integer.parseInt(task_id),
                Integer.parseInt(ins_id),Integer.parseInt(node_id));
        if ("addLevel".equalsIgnoreCase(oprflag)){//增加层级            
            HashMap map =selfBo.AddLevel(strXml);
            this.getFormHM().put("levelnum", (String)map.get("levelnum"));
            this.getFormHM().put("leveldesc", (String)map.get("leveldesc"));           
        }
        else if ("delLevel".equalsIgnoreCase(oprflag)){//删除层级
            String levels = (String) this.getFormHM().get("levels");
            HashMap map =selfBo.delLevel(strXml,levels);   
            this.getFormHM().put("levelDescs", (String)map.get("levelDescs"));
        }
        else if ("addPerson".equalsIgnoreCase(oprflag)){//增加审批人
            String bs_flag = (String) this.getFormHM().get("bs_flag");
            String levelnum = (String) this.getFormHM().get("levelnum");
            String A0100s = (String) this.getFormHM().get("A0100s");
            HashMap map =selfBo.addPerson(strXml,bs_flag,levelnum,A0100s);   
            this.getFormHM().put("ids", (String)map.get("ids"));  
            this.getFormHM().put("A0101Descs", (String)map.get("A0101Descs")); 
            
            this.getFormHM().put("bs_flag", bs_flag); 
            this.getFormHM().put("levelnum", levelnum); 
            
        }        
        else if ("delPerson".equalsIgnoreCase(oprflag)){//删除审批人
            String id = (String) this.getFormHM().get("id");
            HashMap map =selfBo.delPerson(strXml,id);   
            this.getFormHM().put("ids", (String)map.get("ids"));  
            this.getFormHM().put("A0101Descs", (String)map.get("A0101Descs")); 
            
            this.getFormHM().put("bs_flag", (String)map.get("bs_flag")); 
            this.getFormHM().put("levelnum", (String)map.get("levelnum"));    
        }
        else if ("synSpLevel".equalsIgnoreCase(oprflag)){//同步审批层级            
            selfBo.synSpLevel();       
        }
        
        strXml= selfBo.getStrXml();
        this.getFormHM().put("strXml", SafeCode.encode(strXml));
    }

}
