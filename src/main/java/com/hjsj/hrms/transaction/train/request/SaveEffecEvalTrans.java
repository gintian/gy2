package com.hjsj.hrms.transaction.train.request;

import com.hjsj.hrms.businessobject.train.TrainEffectEvalBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.HashMap;

/**
 * <p>
 * Title:AddTrainResourceTrans.java
 * </p>
 * <p>
 * Description:保存培训效果评估交易类
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2008-08-28 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class SaveEffecEvalTrans extends IBusiness {
	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String classid = (String) hm.get("classid");

		TrainEffectEvalBo bo = new TrainEffectEvalBo(this.frameconn, classid);
		LazyDynaBean temJob = (LazyDynaBean) this.getFormHM().get("temJob");
		LazyDynaBean temTeacher = (LazyDynaBean) this.getFormHM().get("temTeacher");
		LazyDynaBean quesJob = (LazyDynaBean) this.getFormHM().get("quesJob");
		LazyDynaBean quesTeacher = (LazyDynaBean) this.getFormHM().get("quesTeacher");
		LazyDynaBean ctrl_apply = (LazyDynaBean) this.getFormHM().get("ctrl_apply");
		LazyDynaBean ctrl_count = (LazyDynaBean) this.getFormHM().get("ctrl_count");
//		LazyDynaBean checkClass = (LazyDynaBean) this.getFormHM().get("checkClass");
		
		bo.save(temJob, temTeacher, quesJob, quesTeacher, ctrl_apply, ctrl_count);
	}

}
