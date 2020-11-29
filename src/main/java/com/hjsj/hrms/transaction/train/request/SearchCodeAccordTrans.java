package com.hjsj.hrms.transaction.train.request;

import com.hjsj.hrms.businessobject.train.TrainArchiveBaseBo;
import com.hjsj.hrms.businessobject.train.TrainArchiveBoFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * Title:SearchCodeAccordTrans.java
 * </p>
 * <p>
 * Description:初始化代码对应
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2008-07-04 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class SearchCodeAccordTrans extends IBusiness
{
    public void execute() throws GeneralException
    {
        HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");

        String sourceField = (String) hm.get("sourceField");        
        String destCode = (String) hm.get("destCode");
        
        //archType: 1:培训班归档；2：培训教师归档；3：考试计划归档
        String archType = (String)hm.get("type");
        String busiId = (String) hm.get("id");

        TrainArchiveBaseBo bo = TrainArchiveBoFactory.getTrainArchiveBo(archType, busiId, this.getFrameconn());
        
        ArrayList list = bo.getNoAccordCodes(sourceField, destCode);
        this.getFormHM().put("SourceCodes", list);// 源代码为代码对应存储表中没有建立对应关系的代码

        list = bo.getTargetCodes(destCode,sourceField);
        this.getFormHM().put("TargetCodes", list);

        list = bo.getHaveAccordCodes(destCode, sourceField);
        this.getFormHM().put("AccordCodes", list);
    }

}
