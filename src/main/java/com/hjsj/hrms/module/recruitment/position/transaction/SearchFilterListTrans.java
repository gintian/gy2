package com.hjsj.hrms.module.recruitment.position.transaction;

import com.hjsj.hrms.module.recruitment.position.businessobject.ResumeFilterBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 
 * <p>Title: SearchFilterListTrans </p>
 * <p>Description: 查询简历筛选</p>
 * <p>Company: hjsj</p>
 * <p>create time  2015-12-29 下午05:15:38</p>
 * @author zx
 * @version 1.0
 */
public class SearchFilterListTrans extends IBusiness {
   
    @Override
    public void execute() throws GeneralException {
        try {
        	String positionId = (String)this.getFormHM().get("z0301");
        	positionId = PubFunc.decrypt(positionId);
        	ResumeFilterBo bo = new ResumeFilterBo(this.frameconn,this.userView);
        	
        	String jsonStr = bo.getFilterJson(positionId); 
        	this.getFormHM().put("jsonStr", jsonStr);
        	//zxj 20160302 判断是否有删除简历过滤规则的权限，供前台使用
        	this.getFormHM().put("delPriv", this.userView.hasTheFunction("311010904") ? "1" : "0");
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

}
