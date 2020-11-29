package com.hjsj.hrms.transaction.train.request;

import com.hjsj.hrms.businessobject.train.TrainArchiveBaseBo;
import com.hjsj.hrms.businessobject.train.TrainArchiveBoFactory;
import com.hjsj.hrms.businessobject.train.TrainClassBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * <p>
 * Title:SaveResultFiledTrans.java
 * </p>
 * <p>
 * Description:保存结果归档
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2008-06-28 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class SaveResultFiledTrans extends IBusiness
{

    public void execute() throws GeneralException
    {
        ArrayList sourceCodes = (ArrayList) this.getFormHM().get("sourceCodes");
        ArrayList sourceNames = (ArrayList) this.getFormHM().get("sourceNames");
        ArrayList destCodes = (ArrayList) this.getFormHM().get("destCodes");
        ArrayList destTypes = (ArrayList) this.getFormHM().get("destTypes");
        String setName = (String) this.getFormHM().get("setName");
        String schemasave = (String)this.getFormHM().get("schemasave");
        
        //archType: 1:培训班归档；2：培训教师归档；3：考试计划归档
        String archType = (String)this.getFormHM().get("type");
        String busiId = (String)this.getFormHM().get("id");
        
        if("1".equalsIgnoreCase(archType) || "2".equals(archType)){
            TrainClassBo bo = new TrainClassBo(this.frameconn);
            if(!bo.checkClassPiv(busiId, this.userView))
                throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("train.info.chang.nopiv")));
        } else {
            busiId = PubFunc.decrypt(SafeCode.decode(busiId));
        }
        
        TrainArchiveBaseBo bo = TrainArchiveBoFactory.getTrainArchiveBo(archType, busiId, this.getFrameconn());
        
        
        String isFlag = "";
        
        // 保存归档方案
        if (null != schemasave && "1".equals(schemasave))
        {
            isFlag = bo.genetateXML(sourceCodes, sourceNames, destCodes, destTypes, setName);
            if ("yes".equals(isFlag))
            {
                this.getFormHM().put("flag", "success");
            }
            else {
                this.getFormHM().put("flag", "failure");
            }
        }
        else // 归档
        {
            String userName = this.getUserView().getUserName();
            boolean flag = bo.save(sourceCodes, sourceNames, destCodes, destTypes, setName, userName);
            if (flag)
            {
                // 同时把此培训班设置为结束状态，
                // 另外如果此培训班关联了计划，如果此计划下的所有培训班状态都为结束状态时，则把培训计划也设置为结束状态
                bo.updateState();
                this.getFormHM().put("flag", "success");
            }
            else
                this.getFormHM().put("flag", "failure");
        }       
    }
}
