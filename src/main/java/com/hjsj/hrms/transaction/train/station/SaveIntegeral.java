package com.hjsj.hrms.transaction.train.station;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * <p>SaveIntegeral.java</p>
 * <p>Description:保存积分管理参数</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2012-05-2 10:03:20</p>
 * @author  Xuzhe
 * @version 5.0
 */
public class SaveIntegeral extends IBusiness{

	
	public void execute() throws GeneralException {
		/**
		 * setid=积分子集，
         * post_setid=可用积分指标，
	     * post_setxid=已用积分指标
	     * reg=登记表
		 */
		String setid = (String)this.getFormHM().get("emp_setid");	
		String post_setid = (String)this.getFormHM().get("post_setid");	
		post_setid = post_setid == null ? "" : post_setid; 
		String post_setxid = (String)this.getFormHM().get("post_setxid");
		post_setxid = post_setxid == null ? "" : post_setxid;
		String reg = (String)this.getFormHM().get("reg_setid");	

		String mess="";
		if(!"".equals(post_setid) && !"".equals(post_setxid)
		   && !"#".equals(post_setid) && !"#".equals(post_setxid)
		   &&  post_setid.equals(post_setxid)){	
			mess = "nook";
		}else{
			ConstantXml constantbo = new ConstantXml(this.getFrameconn(),"TR_PARAM");
			constantbo.setAttributeValue("/param/point_set", "subset", setid);
			constantbo.setAttributeValue("/param/point_set", "cur_point_field", post_setid);
			constantbo.setAttributeValue("/param/point_set", "used_point_field", post_setxid);
			constantbo.setAttributeValue("/param/em_point_tab", "id", reg);
			constantbo.saveStrValue();
			mess = "ok";
			
		}
		this.getFormHM().put("mess", mess);
		
		
	}

}
