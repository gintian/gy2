package com.hjsj.hrms.module.recruitment.recruitflow.transaction;

import com.hjsj.hrms.module.recruitment.recruitflow.businessobject.RecruitflowBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * <p>
 * Title:SaveRecruitFlowTrans.java
 * </p>
 * <p>
 * Description:保存招聘流程的工作职责描述。
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2015-5-4 下午04:52:02
 * </p>
 * 
 * @author zhangx
 * @version 1.0
 *
 */
public class SaveRecruitFlowTrans extends IBusiness{

    @Override
    public void execute() throws GeneralException {
        try {
        	String skipflag = (String) this.getFormHM().get("skipflag");
        	RecruitflowBo recruitflowBo = new RecruitflowBo(this.frameconn, this.userView);
        	String flowid = (String) this.getFormHM().get("flowid");
        	if (flowid != null && flowid.length() > 0)
        		flowid = PubFunc.decrypt(flowid);
        	if(skipflag==null){
	            String description = (String) this.getFormHM().get("description");
	            description = PubFunc.reverseHtml(description);
	            String newname = (String) this.getFormHM().get("newname");
	            newname =  PubFunc.keyWord_filter(newname);
	            String b0110 = (String) this.getFormHM().get("b0110");
	            recruitflowBo.upDescription(newname,description, flowid,b0110);
	            
	            description = description.replace("\n", "<br/>").replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;").replace(" ", "&nbsp;");
	            this.getFormHM().put("flowid", flowid);
	            this.getFormHM().put("description",description);
        	}else{
        		recruitflowBo.saveSkipFlag(skipflag, flowid);
        	}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
