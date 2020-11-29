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
import java.util.HashMap;

/**
 * <p>
 * Title:SearchResultFiledTrans.java
 * </p>
 * <p>
 * Description:培训班归档
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2008-08-04 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class SearchResultFiledTrans extends IBusiness
{

    public void execute() throws GeneralException
    {
        HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
        
        //archType: 1:培训班归档；2：培训教师归档；3：考试计划归档
        String archType = (String)hm.get("type");
        String busiId = (String) hm.get("id");
        String subSetName = (String) hm.get("subSetName");
        
        hm.remove("subSetName");
        
        if ((null == archType)||"".equals(archType))
        {
            archType = "1";
        }
        
        if("1".equalsIgnoreCase(archType) || "2".equals(archType)){
            TrainClassBo bo = new TrainClassBo(this.frameconn);
            if(!bo.checkClassPiv(busiId, this.userView))
                throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("train.info.chang.nopiv")));
        } else {
            busiId = PubFunc.decrypt(SafeCode.decode(busiId));
        }
        
        TrainArchiveBaseBo bo = TrainArchiveBoFactory.getTrainArchiveBo(archType, busiId, this.getFrameconn());
        
        ArrayList list = bo.getSubSet(); // 考核结果归档子集
        this.getFormHM().put("subSet", list);
        
        String setName = bo.getSetName();
        if (subSetName != null)
            setName = subSetName;
        this.getFormHM().put("setName", setName);
        
        list = bo.getPoints(setName);
        bo.generateTempTable();// 生成归档的临时表
        this.getFormHM().put("sourcePoints", list);
    }

}
