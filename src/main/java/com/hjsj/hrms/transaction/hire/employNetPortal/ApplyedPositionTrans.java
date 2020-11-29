package com.hjsj.hrms.transaction.hire.employNetPortal;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class ApplyedPositionTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String a0100=(String)this.getFormHM().get("a0100");
			a0100 = a0100 == null ? "" : a0100;
			
			String dbName=(String)this.getFormHM().get("dbName");
			dbName = dbName == null ? "" : dbName;
			
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			if(this.userView != null && "1".equals(this.userView.getHm().get("isHeadhunter"))){
				a0100 = (String) hm.get("a0100");
				a0100 = PubFunc.decrypt(a0100);
				this.getFormHM().put("a0100", a0100);
			}
			String hireChannel=(String)this.getFormHM().get("hireChannel");
			String zpUnitCode = (String)this.getFormHM().get("zpUnitCode");
			a0100=PubFunc.getReplaceStr(a0100);
			hireChannel=PubFunc.getReplaceStr(hireChannel);
			zpUnitCode=PubFunc.getReplaceStr(zpUnitCode);
			EmployNetPortalBo employNetPortalBo=new EmployNetPortalBo(this.getFrameconn());
			ArrayList applyedPosList=employNetPortalBo.getApplyedPosList(a0100.trim());
			String hasXiaoYuan=employNetPortalBo.getHasXaoYuan();
			
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String writeable=employNetPortalBo.getWriteable(dao, a0100, dbName);
			
			this.getFormHM().put("writeable", writeable);
			this.getFormHM().put("applyedPosList",applyedPosList);
			String max_count = "";
			ParameterXMLBo xmlBo=new ParameterXMLBo(this.getFrameconn(),"1");
			HashMap map=xmlBo.getAttributeValues();
			if(map.get("max_count")!=null)
				max_count = (String)map.get("max_count");
			this.getFormHM().put("max_count", max_count);
			this.getFormHM().put("hireChannel", hireChannel);
			String hireMajor="-1";
			if(map.get("hireMajor")!=null&&!"".equals((String)map.get("hireMajor")))
				hireMajor=(String)map.get("hireMajor");
			String candidate_status_itemId="#";//应聘身份指标
			if(StringUtils.isNotEmpty((String)map.get("candidate_status")))
				candidate_status_itemId=(String)map.get("candidate_status");
			//如果应聘身份指标参数有值，则注册时必须填写应聘身份
			if(!"#".equals(candidate_status_itemId)) {
				hireChannel = employNetPortalBo.getCandidateStatus(candidate_status_itemId, a0100);
				String channelName = employNetPortalBo.getChannelName(candidate_status_itemId,a0100,"");
				this.getFormHM().put("channelName", channelName==null?"":channelName);
			}
			this.getFormHM().put("hireMajor", hireMajor);
			this.getFormHM().put("hasXiaoYuan", hasXiaoYuan);
			this.getFormHM().put("zpUnitCode", zpUnitCode);
			String appliedPosItems = "";//外网已申请职位列表显示指标集
			if(map.get("appliedPosItems")!=null)
				appliedPosItems = (String)map.get("appliedPosItems");
			this.getFormHM().put("appliedPosItems", appliedPosItems);
		}
		catch(Exception ex)
		{
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
