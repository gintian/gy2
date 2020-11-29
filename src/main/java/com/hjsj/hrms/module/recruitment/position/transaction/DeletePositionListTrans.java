package com.hjsj.hrms.module.recruitment.position.transaction;

import com.hjsj.hrms.module.recruitment.position.businessobject.PositionBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 
 * <p>Title: DeletePositionListTrans </p>
 * <p>Description: 删除招聘职位</p>
 * <p>Company: hjsj</p>
 * <p>create time  2015-1-20 下午05:15:38</p>
 * @author xiongyy
 * @version 1.0
 */
public class DeletePositionListTrans extends IBusiness {
   
    @Override
    public void execute() throws GeneralException {
        try {
            String data = (String)this.getFormHM().get("z0301s");
            if(data == null || data.length() <= 0)
                return;
            
            PositionBo pobo = new PositionBo(this.getFrameconn(), new ContentDAO(this.getFrameconn()), this.getUserView());
            pobo.deletePosition(data);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

}
