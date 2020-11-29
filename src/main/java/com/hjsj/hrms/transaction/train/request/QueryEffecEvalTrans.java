package com.hjsj.hrms.transaction.train.request;

import com.hjsj.hrms.businessobject.train.TrainClassBo;
import com.hjsj.hrms.businessobject.train.TrainEffectEvalBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>
 * Title:AddTrainResourceTrans.java
 * </p>
 * <p>
 * Description:查询培训效果评估交易类
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
public class QueryEffecEvalTrans extends IBusiness
{
    public void execute() throws GeneralException
    {
    	String classid = "";
    	String R3127 = "";
    	
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		if(null != hm)
		    classid = (String) hm.get("classid");
		else
			classid = (String)this.getFormHM().get("classid");
		
		 TrainClassBo cbo = new TrainClassBo(frameconn);
         if(!cbo.checkClassPiv(classid, this.userView))
             throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("train.info.chang.nopiv")));
			
		//普通调用
		if(null != hm)
		{
		    TrainEffectEvalBo bo = new TrainEffectEvalBo(this.frameconn,classid,"0");
	        ArrayList list = bo.getEffectEvalXML();
			this.getFormHM().put("temJob", list.get(0));
			this.getFormHM().put("temTeacher", list.get(1));
			this.getFormHM().put("quesJob", list.get(2));
			this.getFormHM().put("quesTeacher", list.get(3));
			this.getFormHM().put("className", bo.getClassName());
			this.getFormHM().put("ctrl_apply", list.get(4));
			this.getFormHM().put("ctrl_count", list.get(5));
			//查询培训班的状态
			try {
			    ContentDAO dao = new ContentDAO(this.frameconn);
			    this.frowset = dao.search("select r3127 from r31 where r3101='" + classid + "'");
			    if(this.frowset.next())
			        R3127 = this.frowset.getString("r3127");
			    this.getFormHM().put("r3127", R3127);
			} catch (SQLException e) {
			    e.printStackTrace();
			    throw GeneralExceptionHandler.Handle(e);
			}
		}
		else //ajax调用
		{
		    TrainEffectEvalBo bo = new TrainEffectEvalBo(this.frameconn,classid,"1");
	        ArrayList list = bo.getEffectEvalXML();
		    LazyDynaBean temJob = (LazyDynaBean) list.get(0);
			this.getFormHM().put("temJob", (String)temJob.get("text"));
			LazyDynaBean temTeacher = (LazyDynaBean) list.get(1);
			this.getFormHM().put("temTeacher", (String)temTeacher.get("text"));
			LazyDynaBean quesJob = (LazyDynaBean) list.get(2);
			this.getFormHM().put("quesJob", (String)quesJob.get("text"));
			this.getFormHM().put("mdquesJob", PubFunc.encryption((String)quesJob.get("text")));
			LazyDynaBean quesTeacher = (LazyDynaBean) list.get(3);
			this.getFormHM().put("quesTeacher", (String)quesTeacher.get("text"));
			LazyDynaBean ctrl_apply = (LazyDynaBean) list.get(4);
			this.getFormHM().put("ctrl_apply", (String)ctrl_apply.get("text"));
			LazyDynaBean ctrl_count = (LazyDynaBean) list.get(5);
			this.getFormHM().put("ctrl_count", (String)ctrl_count.get("text"));
		}
	}
	
}
