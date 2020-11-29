package com.hjsj.hrms.module.recruitment.position.transaction;

import com.hjsj.hrms.module.recruitment.position.businessobject.ResumeFilterBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONObject;

import java.util.ArrayList;
/**
 * 
 * <p>Title: SearchFilterListTrans </p>
 * <p>Description: 维护简历筛选</p>
 * <p>Company: hjsj</p>
 * <p>create time  2016-01-05 下午02:15:38</p>
 * @author zx
 * @version 1.0
 */
public class UpdateFilterTrans extends IBusiness {
   
    @Override
    public void execute() throws GeneralException {
        try {
        	//当前页面和数据库都存在的筛选器id
			String filterid = (String)this.getFormHM().get("filterid");
			
			int flag = (Integer)this.getFormHM().get("flag");
			String positionId = (String)this.getFormHM().get("z0301");
        	positionId = PubFunc.decrypt(positionId);
        	
        	//筛选器指标
        	String itemid = (String)this.getFormHM().get("itemid");
        	//筛选器指标对应值
			String filterObj = (String) this.getFormHM().get("obj");
			JSONObject jsonObj = JSONObject.fromObject(filterObj);
			
        	ResumeFilterBo bo = new ResumeFilterBo(this.frameconn,this.userView);
        	boolean res = bo.saveFilter(positionId, filterid, itemid, jsonObj);
        	//保存筛选器时调用简历筛选
        	ArrayList z03list = new ArrayList();
        	z03list.add(positionId);
        	bo.updateSuitable(z03list,null);
        	this.getFormHM().put("infos", res);
        	this.getFormHM().put("flag", flag);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

}
