package com.hjsj.hrms.module.recruitment.position.transaction;

import com.hjsj.hrms.module.recruitment.position.businessobject.ResumeFilterBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONArray;

import java.util.ArrayList;
/**
 * 
 * <p>Title: AddFilterItemsTrans </p>
 * <p>Description: 设置简历筛选指标</p>
 * <p>Company: hjsj</p>
 * <p>create time  2016-01-05 下午02:15:38</p>
 * @author zx
 * @version 1.0
 */
public class AddFilterItemsTrans extends IBusiness {
   
    @Override
    public void execute() throws GeneralException {
        try {
			String positionId = (String)this.getFormHM().get("z0301");
        	positionId = PubFunc.decrypt(positionId);
        	
        	//筛选器指标
        	String itemid = (String)this.getFormHM().get("itemid");
        	//筛选器指标对应值
			String jsonStr = (String) this.getFormHM().get("jsonStr");
			JSONArray jsonObj = JSONArray.fromObject(jsonStr);
			
        	ResumeFilterBo bo = new ResumeFilterBo(this.frameconn,this.userView);
        	
        	bo.addFilterItems(positionId, itemid, jsonObj);
        	//保存筛选器时调用简历筛选
        	ArrayList z03list = new ArrayList();
        	z03list.add(positionId);
        	bo.updateSuitable(z03list,null);
        	this.getFormHM().put("jsonStr", jsonStr);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

}
