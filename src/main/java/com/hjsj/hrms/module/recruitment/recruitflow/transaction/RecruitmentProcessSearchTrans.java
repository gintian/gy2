package com.hjsj.hrms.module.recruitment.recruitflow.transaction;

import com.hjsj.hrms.module.recruitment.recruitflow.businessobject.RecruitflowBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.HashMap;
/**
 * <p>
 * Title:RecruitmentProcessSearch.java
 * </p>
 * <p>
 * Description:招聘流程信息显示
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2015-5-4 下午04:56:55
 * </p>
 * 
 * @author chenxg
 * @version 1.0
 *
 */
public class RecruitmentProcessSearchTrans extends IBusiness {

    private String flowid = "";

    @Override
    public void execute() throws GeneralException {
        try {
            HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
            this.flowid = (String) hm.get("flowid");
            RecruitflowBo rfb = new RecruitflowBo(this.frameconn, this.userView);
            String[] res = rfb.getDefinition(this.flowid);
            String flowHtml = res[1];
            this.flowid = PubFunc.decrypt(res[0]);
            this.getFormHM().put("flowHtml", flowHtml);
            LazyDynaBean flowBean = rfb.getLazyDyna(this.flowid);

            this.getFormHM().put("flowid", PubFunc.encrypt(this.flowid));
            this.getFormHM().put("valid", flowBean.get("valid")+"");
            this.getFormHM().put("isParent", String.valueOf(flowBean.get("isParent")));
            this.getFormHM().put("flowBean", flowBean);
            this.getFormHM().put("b0110", flowBean.get("b0110"));
            this.getFormHM().put("codeitemdesc", flowBean.get("codeitemdesc"));
            String description =  PubFunc.nullToStr((String) flowBean.get("description")).replace("\n", "<br/>");
            description = description.replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;").replace(" ", "&nbsp;");
            this.getFormHM().put("description",description);
            this.getFormHM().put("flowName", PubFunc.nullToStr((String) flowBean.get("name")));
            this.getFormHM().put("skipflag", flowBean.get("skipflag"));
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
    

}
