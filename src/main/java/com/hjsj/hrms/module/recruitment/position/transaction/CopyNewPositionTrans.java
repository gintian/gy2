package com.hjsj.hrms.module.recruitment.position.transaction;

import com.hjsj.hrms.module.recruitment.position.businessobject.PositionBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @Title:        CopyNewPositionTrans.java
 * @Description:  创建职位新轮招聘调用的交易类
 * @Company:      hjsj     
 * @Create time:  2019-5-7 
 * @author        gaozy
 * @version       1.0
 */
public class CopyNewPositionTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		String z0301 = (String) this.getFormHM().get("z0301");
        //flag为前台操作步骤参数，值为1时创建新的招聘职位，否者获取点击职位的详细信息		
		String flag = (String) this.getFormHM().get("flag");
		
		try {
			String posid = PubFunc.decrypt(z0301);
			PositionBo pobo = new PositionBo(this.getFrameconn(),
	                new ContentDAO(this.getFrameconn()), this.getUserView());
			
			if("1".equalsIgnoreCase(flag)){
				HashMap map = new HashMap();
				String z0315 = (String) this.getFormHM().get("z0315");
				String z0329 = (String) this.getFormHM().get("z0329");
				String z0331 = (String) this.getFormHM().get("z0331");
				map.put("z0315", z0315);
				map.put("z0329", z0329);
				map.put("z0331", z0331);
				map.put("posid", posid);
				pobo.copyPosition(map);
				return;
			}
			
			
			ArrayList<LazyDynaBean> positionInfo = pobo.getPositionInfo(posid);
			this.getFormHM().put("z0301", z0301);
			this.getFormHM().put("positionInfo", positionInfo);
			
		} catch (Exception e) {
            e.printStackTrace();
        } 
	}

}
