package com.hjsj.hrms.module.jobtitle.reviewmeeting.transaction;

import com.hjsj.hrms.module.jobtitle.configfile.businessobject.JobtitleConfigBo;
import com.hjsj.hrms.module.jobtitle.reviewfile.businessobject.StartReviewBo;
import com.hjsj.hrms.module.jobtitle.reviewmeeting.businessobject.ReviewMeetingPortalBo;
import com.hjsj.hrms.module.jobtitle.utils.JobtitleUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
/**
 * 编辑|创建会议初始化
 * @author haosl
 *
 */
public class InitMeetingTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		/**
		 * opt 
		 * =1 创建会议； 
		 * =2编辑会议  
		 * =3 初始化会议数据
		 *  =4加载评委会列表
		 *  =5保存评审人员设置数据
		 *  =6查询评审人员设置数据
		 *  =7添加或删除打分模板时同步打分信息
		 *  =8查询功能权限信息
		 */
		try {
			ReviewMeetingPortalBo rmpbo = new ReviewMeetingPortalBo(this.userView,this.frameconn);
			String opt = (String)formHM.get("opt");
			if("1".equals(opt)
					||"2".equals(opt)) {
				MorphDynaBean valueBean = (MorphDynaBean)formHM.get("values");
				String w0301 = (String)formHM.get("w0301");
				LazyDynaBean bean = rmpbo.saveCommitee(opt,valueBean,w0301);
				//向前台返回会议数据
				formHM.put("meetingData",bean);
			}else if("3".equals(opt)) {
				
				//根据w0301查询会议数据
				String w0301 = (String)formHM.get("w0301");
				LazyDynaBean meetingData = null;
				String orgid = "";//所属机构id
				if(StringUtils.isNotBlank(w0301)) {
					w0301 = PubFunc.decrypt(w0301);
					meetingData = rmpbo.getMeetingData(w0301);
					orgid = (String)meetingData.get("b0110");
				}else {//给所属机构赋默认值
					meetingData = new LazyDynaBean();
					String b0110 = userView.getUnitIdByBusi("9");
					//超级用户或者默认最大业务范围  多个则取第一个
					if(userView.isSuper_admin() || "UN`".equals(b0110)) {
						JobtitleUtil util = new JobtitleUtil(this.getFrameconn(), userView);
						String orgs = util.getTopOrgs();
						if(StringUtils.isNotBlank(orgs)) {
							orgid = orgs.split(",")[0];
						}
						
					}else {
						String[] units = ArrayUtils.EMPTY_STRING_ARRAY; 
						units = StringUtils.split(b0110, "`");
						if(units.length>0) {
							orgid = units[0].substring(2);
						}
					}
				}
				String orgName = "";
				if(StringUtils.isNotBlank(orgid)) {
					String UNName = AdminCode.getCodeName("UN", orgid);
					String UMName = AdminCode.getCodeName("UM", orgid);
					orgName = StringUtils.isNotEmpty(UNName)?UNName:UMName;
					meetingData.set("b0110", orgid+"`"+orgName);
				}
				
				this.formHM.put("meetingData",meetingData);
				
				JobtitleConfigBo jobtitleConfigBo = new JobtitleConfigBo(this.getFrameconn(), this.getUserView());
				//配置参数中已勾选的测评表
				formHM.put("per_templates", jobtitleConfigBo.getJobtitleParamConfig("per_templates"));
			}else if("4".equals(opt)) { //加载评委会列表
				ArrayList<LazyDynaBean> datas = rmpbo.getCommitteList();
				formHM.put("datas", datas);
			}else if("5".equals(opt)) {
				MorphDynaBean valueBean = (MorphDynaBean)formHM.get("values");
				String w0301 = (String)formHM.get("w0301");
				LazyDynaBean bean = rmpbo.saveExpertSetting(valueBean,w0301);
				formHM.put("meetingData", bean);
			}else if("6".equals(opt)) {
				String w0301 = (String)formHM.get("w0301");
				if(StringUtils.isNotBlank(w0301)) {
					w0301 = PubFunc.decrypt(w0301);
				}
				LazyDynaBean expertSetting = rmpbo.getExpertSetting(w0301);
				formHM.put("expertSetting", expertSetting);
			}else if("7".equals(opt)) {
				String w0301 = (String)formHM.get("w0301");
				if(StringUtils.isNotBlank(w0301)) {
					w0301 = PubFunc.decrypt(w0301);
				}
				String review_links = (String)formHM.get("review_links");
				ArrayList<String> newTemplate_Id = (ArrayList<String>)formHM.get("newTemplate_Id");
				ArrayList<String> oldTemplate_Id = (ArrayList<String>)formHM.get("oldTemplate_Id");
				StartReviewBo bo = new StartReviewBo(this.getFrameconn());
				bo.SynchronizeKH_Table(w0301, review_links, newTemplate_Id, oldTemplate_Id);
			}else if("8".equals(opt)) {//查询功能权限信息
				/** 操作权限 */
				boolean createMeetingFunc = this.userView.hasTheFunction("380050501");//创建会议
				formHM.put("createMeetingFunc", createMeetingFunc);
			}
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
