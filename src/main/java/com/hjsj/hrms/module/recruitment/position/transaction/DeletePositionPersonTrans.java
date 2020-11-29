package com.hjsj.hrms.module.recruitment.position.transaction;

import com.hjsj.hrms.module.recruitment.position.businessobject.PositionBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 
 * <p>Title: DeletePositionPersonTrans </p>
 * <p>Description: </p>
 * <p>Company: hjsj</p>
 * <p>create time  2015-2-13 下午01:23:14</p>
 * @author xiongyy
 * @version 1.0
 */
public class DeletePositionPersonTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
        try{
            
            String memberid= (String) this.getFormHM().get("id");
            PositionBo pobo= new PositionBo(this.getFrameconn(), new ContentDAO(this.getFrameconn()), this.getUserView());
            pobo.deleteByMemberId(memberid);
            
        }catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

}
