package com.hjsj.hrms.transaction.train.b_plan;

import com.hjsj.hrms.businessobject.train.TrainPlanBo;
import com.hjsj.hrms.businessobject.train.TransDataBo;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:培训计划</p>
 * <p>Description:培训计划显示</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2007-12-13 下午06:07:55</p>
 * @author lilinbing
 * @version 4.0
 */
public class TrainTrans extends IBusiness {

    public void execute() throws GeneralException {
        // TODO Auto-generated method stub
        HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
        String r2501 = (String)hm.get("r2501");
        r2501=r2501!=null?r2501:"";
        hm.remove("r2501");
        
        String model = (String)hm.get("model");
        model=model!=null?model:"1";
        hm.remove("model");
        
        String spflag = (String)hm.get("spflag");
        spflag=spflag!=null?spflag:"01";
        hm.remove("spflag");
        
        String edit="";
        if("1".equals(model)){
            if("01".equals(spflag)){
                edit="true";
            }else if("07".equals(spflag)){
                edit="true";
            }
        }else{
            if("04".equals(spflag)){
                edit="true";
            }else if("03".equals(spflag)){
                edit="true";
            }
        }
        
        ArrayList list = new ArrayList();
        TransDataBo tdb = new TransDataBo(this.frameconn);
        ArrayList fieldlist = tdb.filedItemList();
        //ArrayList fieldlist = DataDictionary.getFieldList("r31",Constant.USED_FIELD_SET);
        StringBuffer buf = new StringBuffer();
        StringBuffer wherestr = new StringBuffer();
        StringBuffer columns = new StringBuffer();
        buf.append("select ");
        for(int i=0;i<fieldlist.size();i++){
            FieldItem fielditem = (FieldItem)fieldlist.get(i);
            if(!fielditem.isVisible())
                continue;
            
            if("r3125".equalsIgnoreCase(fielditem.getItemid()))
                buf.append("(select R2502 from R25 where R2501=r31.R3125) as r3125");
            else
                buf.append(fielditem.getItemid());
            
            columns.append(fielditem.getItemid());
            buf.append(",");
            columns.append(",");
            if("r3101".equalsIgnoreCase(fielditem.getItemid())){
                list.add(0,fielditem);
            }else
                list.add(fielditem);
        }
        
        wherestr.append(" from r31 where ");
        
        TrainPlanBo bo = new TrainPlanBo(this.frameconn);
            
        if(r2501!=null&&r2501.trim().length()>0){ 
            if(!bo.checkPlanPiv(r2501, this.userView))
                throw new GeneralException("", ResourceFactory.getProperty("train.b_plan.nopiv"), "", "");
            else
                wherestr.append("r3125='"+r2501+"'");
        }else
            wherestr.append("1=2");
        wherestr.append(" and r3127 not in('01','07')");
        
        this.getFormHM().put("tablename","r31");
        this.getFormHM().put("itemlist",list);
        this.getFormHM().put("model",model);
        this.getFormHM().put("r2501",r2501);
        this.getFormHM().put("sql",buf.substring(0,buf.length()-1));
        this.getFormHM().put("wherestr",wherestr.toString());
        this.getFormHM().put("columns",columns.substring(0,columns.length()-1));
        this.getFormHM().put("edit",edit);
    }

}
