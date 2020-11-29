package com.hjsj.hrms.module.recruitment.position.transaction;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.module.recruitment.position.businessobject.PositionBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.axis.utils.StringUtils;
/**
 * 
 * <p>Title: PublishPositionListTrans </p>
 * <p>Description: </p>
 * <p>Company: hjsj</p>
 * <p>create time  2015-1-26 上午11:55:25</p>
 * @author xiongyy
 * @version 1.0
 */
public class PublishPositionListTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
    	 //标示是那个操作
        String act = (String) this.getFormHM().get("act"); 
        //选中的职位
        String z0301s = (String) this.getFormHM().get("z0301s");
        //退回意见
        String opinion = (String) this.getFormHM().get("opinion");
        PositionBo pobo = new PositionBo(this.getFrameconn(),new ContentDAO(this.getFrameconn()), this.getUserView());
       
        try {
            if(z0301s!=null&&z0301s.length()>0){
            	if(StringUtils.isEmpty(opinion)){
                    pobo.functionOfPosition(act,z0301s);
                }else{
                    pobo.functionOfPosition(act,z0301s,opinion);
                }
                //发布、暂停、结束 后刷新外网职位列表
                EmployNetPortalBo bo = new EmployNetPortalBo(this.getFrameconn());
    			bo.refreshStaticAttribute();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        
    }

}
