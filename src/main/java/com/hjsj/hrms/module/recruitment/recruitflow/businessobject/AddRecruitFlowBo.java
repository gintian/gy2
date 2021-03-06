package com.hjsj.hrms.module.recruitment.recruitflow.businessobject;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * <p>Title:RecruitflowBo.java</p>
 * <p>Description>:招聘流程业务类</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2015-1-22</p>
 * <p>@author:dengcan</p>
 * <p>@version: 7.x</p>
 */
public class AddRecruitFlowBo {
	private Connection conn=null;
    private UserView userview;
    
    public AddRecruitFlowBo(Connection conn, UserView userview)
    {
    	 this.conn=conn;
    	 this.userview=userview;
    }
    /**
     * 保存环节功能操作
     * @param nodeid 系统内置环节id
     * nodeid详解:  id		环节名称
     * 			-----------------------
     * 				01		人力筛选
     *				02		部门筛选
     *				03               笔  	试
     *				04		人才测评
     *				05		面试
     *				06		背景调查
     *				07		录用审批
     *				08		Offer
     *				09		体检
     *				10		入职
     */
    public void saveFuncs(ContentDAO dao,String linkid,String nodeid,IDGenerator idg){
		try {
			ArrayList list = new ArrayList();
			String id = "";
			String sql="insert into zp_flow_functions (id,link_id,group_number,function_str,valid,custom_name,seq,ownflag,sys_name) values (?,?,?,?,?,?,?,?,?)";
			if ("01".equals(nodeid)||"02".equals(nodeid)) {
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(1);
				list.add("passChoice");
				list.add(1);
				list.add("通过");
				list.add(1);
				list.add(1);
				list.add("通过");
				dao.insert(sql, list);
				list.clear();
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(1);
				list.add("obsolete");
				list.add(1);
				list.add("淘汰");
				list.add(2);
				list.add(1);
				list.add("淘汰");
				dao.insert(sql, list);
				list.clear();
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(1);
				list.add("reserve");
				list.add(1);
				list.add("备选");
				list.add(3);
				list.add(1);
				list.add("备选");
				dao.insert(sql, list);
				list.clear();
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(1);
				list.add("toStage");
				list.add(1);
				list.add("转新阶段");
				list.add(4);
				list.add(1);
				list.add("转新阶段");
				dao.insert(sql, list);
				list.clear();
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(1);
				list.add("changeStatus");
				list.add(1);
				list.add("变更状态");
				list.add(5);
				list.add(1);
				list.add("变更状态");
				dao.insert(sql, list);
				list.clear();
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(2);
				list.add("toTalents");
				list.add(1);
				list.add("转人才库");
				list.add(6);
				list.add(1);
				list.add("转人才库");
				dao.insert(sql, list);
				list.clear();
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(3);
				list.add("Global.recommendOtherPosition");
				list.add(1);
				list.add("推荐职位");
				list.add(7);
				list.add(1);
				list.add("推荐职位");
				dao.insert(sql, list);
				list.clear();
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(3);
				list.add("invitationEvaluation");
				list.add(1);
				list.add("邀请评价");
				list.add(8);
				list.add(1);
				list.add("邀请评价");
				dao.insert(sql, list);
				list.clear();
                id=idg.getId("zp_flow_functions.id");
                list.add(id);
                list.add(linkid);
                list.add(4);
                list.add("sendnotice");
                list.add(1);
                list.add("发送通知");
                list.add(9);
                list.add(1);
                list.add("发送通知");
                dao.insert(sql, list);
			}
			if ("04".equals(nodeid)) {
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(1);
				list.add("passChoice");
				list.add(1);
				list.add("通过");
				list.add(1);
				list.add(1);
				list.add("通过");
				dao.insert(sql, list);
				list.clear();
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(1);
				list.add("obsolete");
				list.add(1);
				list.add("淘汰");
				list.add(2);
				list.add(1);
				list.add("淘汰");
				dao.insert(sql, list);
				list.clear();
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(1);
				list.add("reserve");
				list.add(1);
				list.add("备选");
				list.add(3);
				list.add(1);
				list.add("备选");
				dao.insert(sql, list);
				list.clear();
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(1);
				list.add("toStage");
				list.add(1);
				list.add("转新阶段");
				list.add(4);
				list.add(1);
				list.add("转新阶段");
				dao.insert(sql, list);
				list.clear();
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(1);
				list.add("changeStatus");
				list.add(1);
				list.add("变更状态");
				list.add(5);
				list.add(1);
				list.add("变更状态");
				dao.insert(sql, list);
				list.clear();
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(2);
				list.add("toTalents");
				list.add(1);
				list.add("转人才库");
				list.add(6);
				list.add(1);
				list.add("转人才库");
				dao.insert(sql, list);
				
				list.clear();
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(3);
				list.add("Global.recommendOtherPosition");
				list.add(1);
				list.add("推荐职位");
				list.add(7);
				list.add(1);
				list.add("推荐职位");
				dao.insert(sql, list);
				list.clear();
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(3);
				list.add("uploadingResult");
				list.add(1);
				list.add("上传测评结果");
				list.add(8);
				list.add(1);
				list.add("上传测评结果");
				dao.insert(sql, list);
				list.clear();
                id=idg.getId("zp_flow_functions.id");
                list.add(id);
                list.add(linkid);
                list.add(4);
                list.add("forwardResume");
                list.add(1);
                list.add("转发简历");
                list.add(9);
                list.add(1);
                list.add("转发简历");
                dao.insert(sql, list);
                list.clear();
                id=idg.getId("zp_flow_functions.id");
                list.add(id);
                list.add(linkid);
                list.add(4);
                list.add("sendnotice");
                list.add(1);
                list.add("发送通知");
                list.add(10);
                list.add(1);
                list.add("发送通知");
                dao.insert(sql, list);
			}
			if ("05".equals(nodeid) || "03".equals(nodeid)) {
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(1);
				list.add("passChoice");
				list.add(1);
				list.add("通过");
				list.add(1);
				list.add(1);
				list.add("通过");
				dao.insert(sql, list);
				list.clear();
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(1);
				list.add("obsolete");
				list.add(1);
				list.add("淘汰");
				list.add(2);
				list.add(1);
				list.add("淘汰");
				dao.insert(sql, list);
				list.clear();
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(1);
				list.add("reserve");
				list.add(1);
				list.add("备选");
				list.add(3);
				list.add(1);
				list.add("备选");
				dao.insert(sql, list);
				list.clear();
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(1);
				list.add("toStage");
				list.add(1);
				list.add("转新阶段");
				list.add(4);
				list.add(1);
				list.add("转新阶段");
				dao.insert(sql, list);
				list.clear();
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(1);
				list.add("changeStatus");
				list.add(1);
				list.add("变更状态");
				list.add(5);
				list.add(1);
				list.add("变更状态");
				dao.insert(sql, list);
				list.clear();
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(2);
				list.add("toTalents");
				list.add(1);
				list.add("转人才库");
				list.add(6);
				list.add(1);
				list.add("转人才库");
				dao.insert(sql, list);
				list.clear();
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(4);
				list.add("Global.recommendOtherPosition");
				list.add(1);
				list.add("推荐职位");
				list.add(7);
				list.add(1);
				list.add("推荐职位");
				dao.insert(sql, list);
				
				if ("03".equalsIgnoreCase(nodeid)) {
				    list.clear();
                    id=idg.getId("zp_flow_functions.id");
                    list.add(id);
                    list.add(linkid);
                    list.add(4);
                    list.add("forwardResume");
                    list.add(1);
                    list.add("转发简历");
                    list.add(8);
                    list.add(1);
                    list.add("转发简历");
                    dao.insert(sql, list);
                    list.clear();
                    id=idg.getId("zp_flow_functions.id");
                    list.add(id);
                    list.add(linkid);
                    list.add(4);
                    list.add("sendnotice");
                    list.add(1);
                    list.add("发送通知");
                    list.add(9);
                    list.add(1);
                    list.add("发送通知");
                    dao.insert(sql, list);
				}
				
				if("05".equals(nodeid)){
					list.clear();
					id=idg.getId("zp_flow_functions.id");
					list.add(id);
					list.add(linkid);
					list.add(3);
					list.add("arrangement");
					list.add(1);
					list.add("面试安排");
					list.add(8);
					list.add(1);
					list.add("面试安排");
					dao.insert(sql, list);
					list.clear();
					id=idg.getId("zp_flow_functions.id");
					list.add(id);
					list.add(linkid);
					list.add(3);
					list.add("arrangementNotice");
					list.add(1);
					list.add("面试通知");
					list.add(9);
					list.add(1);
					list.add("面试通知");
					dao.insert(sql, list);
					
					list.clear();
					id=idg.getId("zp_flow_functions.id");
					list.add(id);
					list.add(linkid);
					list.add(3);
					list.add("resumeEvaluate");
					list.add(1);
					list.add("面试评价");
					list.add(10);
					list.add(1);
					list.add("面试评价");
					dao.insert(sql, list);
					list.clear();
					id=idg.getId("zp_flow_functions.id");
					list.add(id);
					list.add(linkid);
					list.add(3);
					list.add("uploadingResult");
					list.add(1);
					list.add("上传面试评价记录");
					list.add(11);
					list.add(1);
					list.add("上传面试评价记录");
					dao.insert(sql, list);
					list.clear();
	                id=idg.getId("zp_flow_functions.id");
	                list.add(id);
	                list.add(linkid);
	                list.add(4);
	                list.add("forwardResume");
	                list.add(1);
	                list.add("转发简历");
	                list.add(12);
	                list.add(1);
	                list.add("转发简历");
	                dao.insert(sql, list);
	                list.clear();
	                id=idg.getId("zp_flow_functions.id");
	                list.add(id);
	                list.add(linkid);
	                list.add(4);
	                list.add("sendnotice");
	                list.add(1);
	                list.add("发送通知");
	                list.add(13);
	                list.add(1);
	                list.add("发送通知");
	                dao.insert(sql, list);
				}
			}
			if ("06".equals(nodeid)) {
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(1);
				list.add("passChoice");
				list.add(1);
				list.add("通过");
				list.add(1);
				list.add(1);
				list.add("通过");
				dao.insert(sql, list);
				list.clear();
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(1);
				list.add("obsolete");
				list.add(1);
				list.add("淘汰");
				list.add(2);
				list.add(1);
				list.add("淘汰");
				dao.insert(sql, list);
				/*list.clear();
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(1);
				list.add("reserve");
				list.add(1);
				list.add("备选");
				list.add(3);
				list.add(1);
				list.add("备选");
				dao.insert(sql, list);*/
				list.clear();
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(1);
				list.add("toStage");
				list.add(1);
				list.add("转新阶段");
				list.add(3);
				list.add(1);
				list.add("转新阶段");
				dao.insert(sql, list);
				list.clear();
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(1);
				list.add("changeStatus");
				list.add(1);
				list.add("变更状态");
				list.add(4);
				list.add(1);
				list.add("变更状态");
				dao.insert(sql, list);
				list.clear();
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(2);
				list.add("toTalents");
				list.add(1);
				list.add("转人才库");
				list.add(5);
				list.add(1);
				list.add("转人才库");
				dao.insert(sql, list);
				
				list.clear();
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(3);
				list.add("Global.recommendOtherPosition");
				list.add(1);
				list.add("推荐职位");
				list.add(6);
				list.add(1);
				list.add("推荐职位");
				dao.insert(sql, list);
				list.clear();
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(3);
				list.add("uploadingResult");
				list.add(1);
				list.add("上传背景调查资料");
				list.add(7);
				list.add(1);
				list.add("上传背景调查资料");
				dao.insert(sql, list);
				list.clear();
                id=idg.getId("zp_flow_functions.id");
                list.add(id);
                list.add(linkid);
                list.add(4);
                list.add("forwardResume");
                list.add(1);
                list.add("转发简历");
                list.add(8);
                list.add(1);
                list.add("转发简历");
                dao.insert(sql, list);
                list.clear();
                id=idg.getId("zp_flow_functions.id");
                list.add(id);
                list.add(linkid);
                list.add(4);
                list.add("sendnotice");
                list.add(1);
                list.add("发送通知");
                list.add(9);
                list.add(1);
                list.add("发送通知");
                dao.insert(sql, list);
			}
			if ("07".equals(nodeid)) {
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(1);
				list.add("passChoice");
				list.add(1);
				list.add("通过");
				list.add(1);
				list.add(1);
				list.add("通过");
				dao.insert(sql, list);
				list.clear();
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(1);
				list.add("obsolete");
				list.add(1);
				list.add("淘汰");
				list.add(2);
				list.add(1);
				list.add("淘汰");
				dao.insert(sql, list);
				list.clear();
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(1);
				list.add("toStage");
				list.add(1);
				list.add("转新阶段");
				list.add(3);
				list.add(1);
				list.add("转新阶段");
				dao.insert(sql, list);
				list.clear();
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(1);
				list.add("changeStatus");
				list.add(1);
				list.add("变更状态");
				list.add(4);
				list.add(1);
				list.add("变更状态");
				dao.insert(sql, list);
				list.clear();
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(2);
				list.add("toTalents");
				list.add(1);
				list.add("转人才库");
				list.add(5);
				list.add(1);
				list.add("转人才库");
				dao.insert(sql, list);
				list.clear();
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(3);
				list.add("Global.recommendOtherPosition");
				list.add(1);
				list.add("推荐职位");
				list.add(6);
				list.add(1);
				list.add("推荐职位");
				dao.insert(sql, list);
				list.clear();
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(2);
				list.add("uploadingResult");
				list.add(1);
				list.add("上传录用审批附件");
				list.add(7);
				list.add(1);
				list.add("上传录用审批附件");
				dao.insert(sql, list);
				list.clear();
                id=idg.getId("zp_flow_functions.id");
                list.add(id);
                list.add(linkid);
                list.add(3);
                list.add("forwardResume");
                list.add(1);
                list.add("转发简历");
                list.add(8);
                list.add(1);
                list.add("转发简历");
                dao.insert(sql, list);
                list.clear();
                id=idg.getId("zp_flow_functions.id");
                list.add(id);
                list.add(linkid);
                list.add(4);
                list.add("sendnotice");
                list.add(1);
                list.add("发送通知");
                list.add(9);
                list.add(1);
                list.add("发送通知");
                dao.insert(sql, list);
			}
			if ("08".equals(nodeid)) {
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(1);
				list.add("sendOffer");
				list.add(1);
				list.add("Offer通知");
				list.add(1);
				list.add(1);
				list.add("Offer通知");
				dao.insert(sql, list);
				list.clear();
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(1);
				list.add("acceptOffer");
				list.add(1);
				list.add("接受Offer");
				list.add(2);
				list.add(1);
				list.add("接受Offer");
				dao.insert(sql, list);
				list.clear();
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(1);
				list.add("refuseOffer");
				list.add(1);
				list.add("拒绝Offer");
				list.add(3);
				list.add(1);
				list.add("拒绝Offer");
				dao.insert(sql, list);
				list.clear();
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(1);
				list.add("toStage");
				list.add(1);
				list.add("转新阶段");
				list.add(4);
				list.add(1);
				list.add("转新阶段");
				dao.insert(sql, list);
				list.clear();
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(1);
				list.add("changeStatus");
				list.add(1);
				list.add("变更状态");
				list.add(5);
				list.add(1);
				list.add("变更状态");
				dao.insert(sql, list);
				list.clear();
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(2);
				list.add("toTalents");
				list.add(1);
				list.add("转人才库");
				list.add(6);
				list.add(1);
				list.add("转人才库");
				dao.insert(sql, list);
				list.clear();
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(3);
				list.add("Global.recommendOtherPosition");
				list.add(1);
				list.add("推荐职位");
				list.add(7);
				list.add(1);
				list.add("推荐职位");
				dao.insert(sql, list);
				list.clear();
                id=idg.getId("zp_flow_functions.id");
                list.add(id);
                list.add(linkid);
                list.add(4);
                list.add("forwardResume");
                list.add(1);
                list.add("转发简历");
                list.add(8);
                list.add(1);
                list.add("转发简历");
                dao.insert(sql, list);
                list.clear();
                id=idg.getId("zp_flow_functions.id");
                list.add(id);
                list.add(linkid);
                list.add(4);
                list.add("sendnotice");
                list.add(1);
                list.add("发送通知");
                list.add(9);
                list.add(1);
                list.add("发送通知");
                dao.insert(sql, list);
                
			}
			if ("09".equals(nodeid)) {
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(1);
				list.add("passChoice");
				list.add(1);
				list.add("通过");
				list.add(1);
				list.add(1);
				list.add("通过");
				dao.insert(sql, list);
				list.clear();
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(1);
				list.add("obsolete");
				list.add(1);
				list.add("淘汰");
				list.add(2);
				list.add(1);
				list.add("淘汰");
				dao.insert(sql, list);
				list.clear();
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(1);
				list.add("reserve");
				list.add(1);
				list.add("备选");
				list.add(3);
				list.add(1);
				list.add("备选");
				dao.insert(sql, list);
				list.clear();
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(1);
				list.add("toStage");
				list.add(1);
				list.add("转新阶段");
				list.add(4);
				list.add(1);
				list.add("转新阶段");
				dao.insert(sql, list);
				list.clear();
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(1);
				list.add("changeStatus");
				list.add(1);
				list.add("变更状态");
				list.add(5);
				list.add(1);
				list.add("变更状态");
				dao.insert(sql, list);
				list.clear();
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(2);
				list.add("toTalents");
				list.add(1);
				list.add("转人才库");
				list.add(6);
				list.add(1);
				list.add("转人才库");
				dao.insert(sql, list);
				
				list.clear();
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(3);
				list.add("Global.recommendOtherPosition");
				list.add(1);
				list.add("推荐职位");
				list.add(7);
				list.add(1);
				list.add("推荐职位");
				dao.insert(sql, list);
				list.clear();
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(3);
				list.add("uploadingResult");
				list.add(1);
				list.add("上传测评结果");
				list.add(8);
				list.add(1);
				list.add("上传测评结果");
				dao.insert(sql, list);
				list.clear();
                id=idg.getId("zp_flow_functions.id");
                list.add(id);
                list.add(linkid);
                list.add(4);
                list.add("forwardResume");
                list.add(1);
                list.add("转发简历");
                list.add(9);
                list.add(1);
                list.add("转发简历");
                dao.insert(sql, list);
                list.clear();
                id=idg.getId("zp_flow_functions.id");
                list.add(id);
                list.add(linkid);
                list.add(4);
                list.add("sendnotice");
                list.add(1);
                list.add("发送通知");
                list.add(10);
                list.add(1);
                list.add("发送通知");
                dao.insert(sql, list);
			}
			if ("10".equals(nodeid)) {
				/*id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(1);
				list.add("sendRzNotice");
				list.add(1);
				list.add("发送入职通知");
				list.add(1);
				list.add(1);
				list.add("发送入职通知");
				dao.insert(sql, list);
				list.clear();*/
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(1);
				list.add("rzRegister");
				list.add(1);
				list.add("入职登记");
				list.add(1);
				list.add(1);
				list.add("入职登记");
				dao.insert(sql, list);
				list.clear();
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(1);
				list.add("refuseRz");
				list.add(1);
				list.add("拒绝入职");
				list.add(2);
				list.add(1);
				list.add("拒绝入职");
				dao.insert(sql, list);
				list.clear();
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(1);
				list.add("toStage");
				list.add(1);
				list.add("转新阶段");
				list.add(3);
				list.add(1);
				list.add("转新阶段");
				dao.insert(sql, list);
				list.clear();
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(1);
				list.add("changeStatus");
				list.add(1);
				list.add("变更状态");
				list.add(4);
				list.add(1);
				list.add("变更状态");
				dao.insert(sql, list);
				list.clear();
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(1);
				list.add("toTalents");
				list.add(1);
				list.add("转人才库");
				list.add(5);
				list.add(1);
				list.add("转人才库");
				dao.insert(sql, list);
				list.clear();
				id=idg.getId("zp_flow_functions.id");
				list.add(id);
				list.add(linkid);
				list.add(2);
				list.add("Global.recommendOtherPosition");
				list.add(1);
				list.add("推荐职位");
				list.add(6);
				list.add(1);
				list.add("推荐职位");
				dao.insert(sql, list);
				list.clear();
                id=idg.getId("zp_flow_functions.id");
                list.add(id);
                list.add(linkid);
                list.add(3);
                list.add("forwardResume");
                list.add(1);
                list.add("转发简历");
                list.add(7);
                list.add(1);
                list.add("转发简历");
                dao.insert(sql, list);
                list.clear();
                id=idg.getId("zp_flow_functions.id");
                list.add(id);
                list.add(linkid);
                list.add(4);
                list.add("sendnotice");
                list.add(1);
                list.add("发送通知");
                list.add(8);
                list.add(1);
                list.add("发送通知");
                dao.insert(sql, list);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    /**
     * 保存环节状态
     * @param nodeid 系统内置环节id
     * nodeid详解:  id		环节名称
     * 			-----------------------
     * 				01		人力筛选
     *				02		部门筛选
     *				03		人才测评
     *				04		面试
     *				05		背景调查
     *				06		录用审批
     *				07		Offer
     *				08		体检
     *				09		入职
     */
    public void saveStatus(ContentDAO dao,String linkid,String nodeid,IDGenerator idg){
		try {
			ArrayList list = new ArrayList();
			String sql="insert into zp_flow_status (id,link_id,status,valid,custom_name,seq) values (?,?,?,?,?,?)";
			String id = "";
			if ("01".equals(nodeid)) {
				id=idg.getId("zp_flow_status.id");
				list.add(id);
				list.add(linkid);
				list.add("0101");
				list.add(1);
				list.add("新候选人");
				list.add(1);
				dao.insert(sql, list);
				id=idg.getId("zp_flow_status.id");
				list.clear();
				list.add(id);
				list.add(linkid);
				list.add("0102");
				list.add(1);
				list.add("进行中");
				list.add(2);
				dao.insert(sql, list);
				id=idg.getId("zp_flow_status.id");
				list.clear();
				list.add(id);
				list.add(linkid);
				list.add("0103");
				list.add(1);
				list.add("筛选通过");
				list.add(3);
				dao.insert(sql, list);
				id=idg.getId("zp_flow_status.id");
				list.clear();
				list.add(id);
				list.add(linkid);
				list.add("0104");
				list.add(1);
				list.add("备选");
				list.add(4);
				dao.insert(sql, list);
				id=idg.getId("zp_flow_status.id");
				list.clear();
				list.add(id);
				list.add(linkid);
				list.add("0105");
				list.add(1);
				list.add("已淘汰");
				list.add(5);
				dao.insert(sql, list);
				id=idg.getId("zp_flow_status.id");
				list.clear();
				list.add(id);
				list.add(linkid);
				list.add("0106");
				list.add(1);
				list.add("终止");
				list.add(6);
				dao.insert(sql, list);
			}
			if ("02".equals(nodeid)) {
				id=idg.getId("zp_flow_status.id");
				list.add(id);
				list.add(linkid);
				list.add("0201");
				list.add(1);
				list.add("新候选人");
				list.add(1);
				dao.insert(sql, list);
				id=idg.getId("zp_flow_status.id");
				list.clear();
				list.add(id);
				list.add(linkid);
				list.add("0202");
				list.add(1);
				list.add("进行中");
				list.add(2);
				dao.insert(sql, list);
				id=idg.getId("zp_flow_status.id");
				list.clear();
				list.add(id);
				list.add(linkid);
				list.add("0203");
				list.add(1);
				list.add("筛选通过");
				list.add(3);
				dao.insert(sql, list);
				id=idg.getId("zp_flow_status.id");
				list.clear();
				list.add(id);
				list.add(linkid);
				list.add("0204");
				list.add(1);
				list.add("备选");
				list.add(4);
				dao.insert(sql, list);
				id=idg.getId("zp_flow_status.id");
				list.clear();
				list.add(id);
				list.add(linkid);
				list.add("0205");
				list.add(1);
				list.add("已淘汰");
				list.add(5);
				dao.insert(sql, list);
				id=idg.getId("zp_flow_status.id");
				list.clear();
				list.add(id);
				list.add(linkid);
				list.add("0206");
				list.add(1);
				list.add("终止");
				list.add(6);
				dao.insert(sql, list);
			}
			if ("04".equals(nodeid)) {
				id=idg.getId("zp_flow_status.id");
				list.add(id);
				list.add(linkid);
				list.add("0401");
				list.add(1);
				list.add("待邀请测评");
				list.add(1);
				dao.insert(sql, list);
				id=idg.getId("zp_flow_status.id");
				list.clear();
				list.add(id);
				list.add(linkid);
				list.add("0402");
				list.add(1);
				list.add("已邀请测评");
				list.add(2);
				dao.insert(sql, list);
				id=idg.getId("zp_flow_status.id");
				list.clear();
				list.add(id);
				list.add(linkid);
				list.add("0403");
				list.add(1);
				list.add("进行中");
				list.add(3);
				dao.insert(sql, list);
				id=idg.getId("zp_flow_status.id");
				list.clear();
				list.add(id);
				list.add(linkid);
				list.add("0404");
				list.add(1);
				list.add("测评通过");
				list.add(4);
				dao.insert(sql, list);
				id=idg.getId("zp_flow_status.id");
				list.clear();
				list.add(id);
				list.add(linkid);
				list.add("0405");
				list.add(1);
				list.add("备选");
				list.add(5);
				dao.insert(sql, list);
				id=idg.getId("zp_flow_status.id");
				list.clear();
				list.add(id);
				list.add(linkid);
				list.add("0406");
				list.add(1);
				list.add("已淘汰");
				list.add(6);
				dao.insert(sql, list);
				id=idg.getId("zp_flow_status.id");
				list.clear();
				list.add(id);
				list.add(linkid);
				list.add("0407");
				list.add(1);
				list.add("候选人放弃");
				list.add(7);
				dao.insert(sql, list);
				id=idg.getId("zp_flow_status.id");
				list.clear();
				list.add(id);
				list.add(linkid);
				list.add("0408");
				list.add(1);
				list.add("已终止");
				list.add(8);
				dao.insert(sql, list);
			}
			if ("05".equals(nodeid) || "03".equals(nodeid)) {
				String customName = "";
				if("03".equals(nodeid))
					customName = "考试";
				else
					customName = "面试";
				
				id=idg.getId("zp_flow_status.id");
				list.add(id);
				list.add(linkid);
				list.add(nodeid+"01");
				list.add(1);
				list.add("待安排"+customName);
				list.add(1);
				dao.insert(sql, list);
				id=idg.getId("zp_flow_status.id");
				list.clear();
				list.add(id);
				list.add(linkid);
				list.add(nodeid+"02");
				list.add(1);
				list.add("已安排"+customName);
				list.add(2);
				dao.insert(sql, list);
				id=idg.getId("zp_flow_status.id");
				list.clear();
				list.add(id);
				list.add(linkid);
				list.add(nodeid+"03");
				list.add(1);
				list.add("进行中");
				list.add(3);
				dao.insert(sql, list);
				id=idg.getId("zp_flow_status.id");
				list.clear();
				list.add(id);
				list.add(linkid);
				list.add(nodeid+"04");
				list.add(1);
				list.add(customName+"通过");
				list.add(4);
				dao.insert(sql, list);
				id=idg.getId("zp_flow_status.id");
				list.clear();
				list.add(id);
				list.add(linkid);
				list.add(nodeid+"05");
				list.add(1);
				list.add("备选");
				list.add(5);
				dao.insert(sql, list);
				id=idg.getId("zp_flow_status.id");
				list.clear();
				list.add(id);
				list.add(linkid);
				list.add(nodeid+"06");
				list.add(1);
				list.add("已淘汰");
				list.add(6);
				dao.insert(sql, list);
				id=idg.getId("zp_flow_status.id");
				list.clear();
				list.add(id);
				list.add(linkid);
				list.add(nodeid+"07");
				list.add(1);
				list.add("候选人放弃");
				list.add(7);
				dao.insert(sql, list);
				id=idg.getId("zp_flow_status.id");
				list.clear();
				list.add(id);
				list.add(linkid);
				list.add(nodeid+"08");
				list.add(1);
				list.add("已终止");
				list.add(8);
				dao.insert(sql, list);
			}
			if ("06".equals(nodeid)) {
				id=idg.getId("zp_flow_status.id");
				list.clear();
				list.add(id);
				list.add(linkid);
				list.add(nodeid+"01");
				list.add(1);
				list.add("进行中");
				list.add(1);
				dao.insert(sql, list);
				id=idg.getId("zp_flow_status.id");
				list.clear();
				list.add(id);
				list.add(linkid);
				list.add(nodeid+"02");
				list.add(1);
				list.add("背景调查通过");
				list.add(2);
				dao.insert(sql, list);
				id=idg.getId("zp_flow_status.id");
				list.clear();
				list.add(id);
				list.add(linkid);
				list.add(nodeid+"03");
				list.add(1);
				list.add("已淘汰");
				list.add(3);
				dao.insert(sql, list);
				id=idg.getId("zp_flow_status.id");
				list.clear();
				list.add(id);
				list.add(linkid);
				list.add(nodeid+"04");
				list.add(1);
				list.add("已终止");
				list.add(4);
				dao.insert(sql, list);
			}
			if ("07".equals(nodeid)) {
				id=idg.getId("zp_flow_status.id");
				list.clear();
				list.add(id);
				list.add(linkid);
				list.add(nodeid+"01");
				list.add(1);
				list.add("进行中");
				list.add(1);
				dao.insert(sql, list);
				id=idg.getId("zp_flow_status.id");
				list.clear();
				list.add(id);
				list.add(linkid);
				list.add(nodeid+"02");
				list.add(1);
				list.add("已录用");
				list.add(2);
				dao.insert(sql, list);
				id=idg.getId("zp_flow_status.id");
				list.clear();
				list.add(id);
				list.add(linkid);
				list.add(nodeid+"03");
				list.add(1);
				list.add("已淘汰");
				list.add(3);
				dao.insert(sql, list);
				id=idg.getId("zp_flow_status.id");
				list.clear();
				list.add(id);
				list.add(linkid);
				list.add(nodeid+"04");
				list.add(1);
				list.add("已终止");
				list.add(4);
				dao.insert(sql, list);
			}
			if ("08".equals(nodeid)) {
				id=idg.getId("zp_flow_status.id");
				list.clear();
				list.add(id);
				list.add(linkid);
				list.add(nodeid+"01");
				list.add(1);
				list.add("待发放Offer");
				list.add(1);
				dao.insert(sql, list);
				id=idg.getId("zp_flow_status.id");
				list.clear();
				list.add(id);
				list.add(linkid);
				list.add(nodeid+"02");
				list.add(1);
				list.add("进行中");
				list.add(2);
				dao.insert(sql, list);
				id=idg.getId("zp_flow_status.id");
				list.clear();
				list.add(id);
				list.add(linkid);
				list.add(nodeid+"03");
				list.add(1);
				list.add("已发Offer");
				list.add(3);
				dao.insert(sql, list);
				id=idg.getId("zp_flow_status.id");
				list.clear();
				list.add(id);
				list.add(linkid);
				list.add(nodeid+"04");
				list.add(1);
				list.add("接受Offer");
				list.add(4);
				dao.insert(sql, list);
				id=idg.getId("zp_flow_status.id");
				list.clear();
				list.add(id);
				list.add(linkid);
				list.add(nodeid+"05");
				list.add(1);
				list.add("候选人放弃");
				list.add(5);
				dao.insert(sql, list);
				id=idg.getId("zp_flow_status.id");
				list.clear();
				list.add(id);
				list.add(linkid);
				list.add(nodeid+"06");
				list.add(1);
				list.add("已终止");
				list.add(6);
				dao.insert(sql, list);
			}
			if ("10".equals(nodeid)) {
				id=idg.getId("zp_flow_status.id");
				list.clear();
				list.add(id);
				list.add(linkid);
				list.add(nodeid+"01");
				list.add(1);
				list.add("待入职");
				list.add(1);
				dao.insert(sql, list);
				id=idg.getId("zp_flow_status.id");
				list.clear();
				list.add(id);
				list.add(linkid);
				list.add(nodeid+"02");
				list.add(1);
				list.add("进行中");
				list.add(2);
				dao.insert(sql, list);
				id=idg.getId("zp_flow_status.id");
				list.clear();
				list.add(id);
				list.add(linkid);
				list.add(nodeid+"03");
				list.add(1);
				list.add("已入职");
				list.add(3);
				dao.insert(sql, list);
				id=idg.getId("zp_flow_status.id");
				list.clear();
				list.add(id);
				list.add(linkid);
				list.add(nodeid+"04");
				list.add(1);
				list.add("候选人放弃");
				list.add(4);
				dao.insert(sql, list);
				id=idg.getId("zp_flow_status.id");
				list.clear();
				list.add(id);
				list.add(linkid);
				list.add(nodeid+"05");
				list.add(1);
				list.add("已终止");
				list.add(5);
				dao.insert(sql, list);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    /**
     * 维护zp_flow_definition
     * @param flowid
     * @param name
     * @param description
     * @param b0110
     */
    public void maintainDefinetion(String flowid,String name,String description,String b0110){
		try {
			String sql = "insert into zp_flow_definition(flow_id,name,description,B0110,create_time,create_user,create_fullname,valid) values(?,?,?,?,?,?,?,?)";
			ArrayList sqlVal = new ArrayList();

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
			String now = sdf.format(new Date());// 获取当前系统时间
			String create_user = this.userview.getUserName();
			String create_fullname = this.userview.getUserFullName();
			sqlVal.add(flowid);
			sqlVal.add(name);
			sqlVal.add(description);
			sqlVal.add(b0110);
			sqlVal.add(java.sql.Timestamp.valueOf(now));//兼容oracle对日期的不同处理
			sqlVal.add(create_user);
			sqlVal.add(create_fullname);
			sqlVal.add(1);
			ContentDAO dao = new ContentDAO(this.conn);
			dao.insert(sql, sqlVal);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    /**
     * 维护zp_flow_links
     * @param node_id
     * @param custom_name
     * @param flowid
     */
    public void maintainLinks(String node_id,String custom_name,String flowid){
    	try{
    		if(StringUtils.isNotEmpty(node_id)&&StringUtils.isNotEmpty(custom_name)){
    			ContentDAO dao = new ContentDAO(this.conn);
            	String[] nodeids = node_id.split(",");
            	String[] nodeNames = custom_name.split(",");
            	int xh=1;
            	for (int i = 0; i < nodeids.length; i++) {
            		if(StringUtils.isNotEmpty(nodeids[i])){
            			IDGenerator idg = new IDGenerator(2, this.conn);
            			String linkid=idg.getId("zp_flow_links.id");//参数从系统管理-应用管理-参数设置-序号维护中获取
            			RecordVo recordVo = new RecordVo("zp_flow_links");
            			recordVo.setString("id", linkid);
            			recordVo.setString("flow_id", flowid);
            			recordVo.setString("node_id", nodeids[i]);
            			recordVo.setInt("valid", 1);
            			recordVo.setString("custom_name", nodeNames[i]);
            			if("部门筛选".equals(nodeNames[i])){
            				recordVo.setInt("org_flag", 0);
            			}
            			recordVo.setInt("seq", xh++);
            			dao.addValueObject(recordVo);
            			this.saveStatus(dao, linkid, nodeids[i], idg);
            			this.saveFuncs(dao, linkid, nodeids[i], idg);
            		}
            	}
            }
    	}catch (Exception e) {
			e.printStackTrace();
		}
    }
}
