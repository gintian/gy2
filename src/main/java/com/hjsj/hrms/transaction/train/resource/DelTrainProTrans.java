package com.hjsj.hrms.transaction.train.resource;

import com.hjsj.hrms.businessobject.train.resource.TrainResourceBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>
 * Title:DelTrainProTrans.java
 * </p>
 * <p>
 * Description:删除培训项目交易类
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2008-07-28 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class DelTrainProTrans extends IBusiness
{
    public void execute() throws GeneralException
    {
        try{
            String delStr = (String)this.getFormHM().get("deletestr");  
            String type = (String)this.getFormHM().get("type");
            
            TrainResourceBo bo = new TrainResourceBo(this.frameconn, type);
            String[] ids = delStr.split("/");
            
            bo.delete(ids);
            this.getFormHM().put("flag","ok");
        }catch(Exception e){
            e.printStackTrace();
            this.getFormHM().put("flag","fail");
        }
    }

}
