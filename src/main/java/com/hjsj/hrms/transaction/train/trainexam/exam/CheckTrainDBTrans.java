package com.hjsj.hrms.transaction.train.trainexam.exam;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class CheckTrainDBTrans extends IBusiness{

    public void execute() throws GeneralException {
      //培训参数中人员库
        ConstantXml constantbo = new ConstantXml(this.getFrameconn(),"TR_PARAM");
        String tmpnbase = constantbo.getTextValue("/param/post_traincourse/nbase");
        if(tmpnbase==null || tmpnbase.length()<1)
            throw GeneralExceptionHandler.Handle(new Exception("未设置人员库！\n\n请到   培训管理>参数设置>其它参数>岗位培训指标设置   中设置人员库。"));
    }
    
}
