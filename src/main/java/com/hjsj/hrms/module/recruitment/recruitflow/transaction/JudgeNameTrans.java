package com.hjsj.hrms.module.recruitment.recruitflow.transaction;

import com.hjsj.hrms.module.recruitment.recruitflow.businessobject.RecruitflowBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * <p>
 * Title:SaveRecruitFlowTrans.java
 * </p>
 * <p>
 * Description:判断招聘流程是否有招聘过程数据
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2015-5-8 10:18:02
 * </p>
 * 
 * @author zhangx
 * @version 1.0
 *
 */
public class JudgeNameTrans extends IBusiness{

    @Override
    public void execute() throws GeneralException {
        try {
            String name = (String) this.getFormHM().get("name");
            RecruitflowBo recruitflowBo = new RecruitflowBo(this.frameconn, this.userView);
            boolean isUsed = recruitflowBo.isNameUsed(name);
        	String msg = "";
            if(isUsed)
        		msg="流程名称已存在";
        	this.getFormHM().put("msg", msg);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally
    	{
    		PubFunc.closeDbObj(this.frowset);
    	}
    }

}
