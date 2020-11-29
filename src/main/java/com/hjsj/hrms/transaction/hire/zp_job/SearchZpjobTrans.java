/*
 * Created on 2005-8-12
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_job;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SearchZpjobTrans</p>
 * <p>Description:查询招聘活动,zp_job</p>
 * <p>Company:hjsj</p>
 * <p>create time:August 15, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */

public class SearchZpjobTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String zp_job_id = (String) hm.get("a_id");		
		String zp_job_id_value = (String)this.getFormHM().get("zp_job_id_value");
		String flag = (String) this.getFormHM().get("flag");
		String flag_mid = (String) this.getFormHM().get("flag_mid");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		RecordVo vo = new RecordVo("zp_job",1);
		ArrayList list = new ArrayList();	
		/**
		 * 按新增按钮时，则不进行查询，直接退出
		 */
		if ("1".equals(flag)) {
			this.getFormHM().put("zpjobDetailslist", list);
			this.getFormHM().put("managepriv", this.userView.getManagePrivCode());
			this.getFormHM().put("manageprivvalue", this.userView.getManagePrivCodeValue());
			return;
		}else if("0".equals(flag) && "1".equals(flag_mid)){
				try {
					String sql = "select detail_id,zp_job_id,detailname,charge,realcharge from zp_job_details where zp_job_id = '"+ zp_job_id_value + "'";
					this.frowset = dao.search(sql);
					while (this.frowset.next()) {
						RecordVo vo_detail = new RecordVo("zp_job_details");
						vo_detail.setString("detail_id", this.getFrowset().getString("detail_id"));
						vo_detail.setString("zp_job_id", this.getFrowset().getString("zp_job_id"));
						vo_detail.setString("detailname", this.getFrowset().getString("detailname"));
						vo_detail.setString("charge", this.getFrowset().getString("charge"));
						vo_detail.setString("realcharge", this.getFrowset().getString("realcharge"));

						list.add(vo_detail);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					ArrayList tempList = new ArrayList();
					vo.setString("zp_job_id", zp_job_id_value);
					String strsql = "select zp_job_id,name,start_date,end_date,principal,attendee,status,plan_id,resource_id,description,real_invite_amount from zp_job where zp_job_id = '"+zp_job_id_value+"'";
					ResultSet rs = dao.search(strsql,tempList);
					while(rs.next()){
			           vo.setString("start_date",PubFunc.DoFormatDate(PubFunc.FormatDate(rs.getDate("start_date"))));
			           vo.setString("end_date",PubFunc.DoFormatDate(PubFunc.FormatDate(rs.getDate("end_date"))));
			           vo.setString("name",rs.getString("name"));
			           vo.setString("principal",rs.getString("principal"));
			           vo.setString("attendee",rs.getString("attendee"));
			           vo.setString("status",rs.getString("status"));
			           vo.setString("plan_id",rs.getString("plan_id"));
			           vo.setString("resource_id",rs.getString("resource_id"));
					}
				} catch (SQLException sqle) {
					sqle.printStackTrace();
					throw GeneralExceptionHandler.Handle(sqle);
				} finally {
					this.getFormHM().put("zpjobvo", vo);
					this.getFormHM().put("zpjobDetailslist", list);
					this.getFormHM().put("managepriv", this.userView.getManagePrivCode());
					this.getFormHM().put("manageprivvalue", this.userView.getManagePrivCodeValue());
					this.getFormHM().put("zp_job_id_value", zp_job_id_value);
				}
		}else{
				try {
					String sql = "";
					if(zp_job_id != null && !"".equals(zp_job_id)){
						sql = "select detail_id,zp_job_id,detailname,charge,realcharge from zp_job_details where zp_job_id = '"+ zp_job_id + "'";
					}else if(zp_job_id_value != null && !"".equals(zp_job_id_value)){
						sql = "select detail_id,zp_job_id,detailname,charge,realcharge from zp_job_details where zp_job_id = '"+ zp_job_id_value + "'";
					}
					
				    this.frowset = dao.search(sql);
				    while (this.frowset.next()) {
					   RecordVo vo_detail = new RecordVo("zp_job_details");
					   vo_detail.setString("detail_id", this.getFrowset().getString("detail_id"));
					   vo_detail.setString("zp_job_id", this.getFrowset().getString("zp_job_id"));
					   vo_detail.setString("detailname", this.getFrowset().getString("detailname"));
					   vo_detail.setString("charge", this.getFrowset().getString("charge"));
					   vo_detail.setString("realcharge", this.getFrowset().getString("realcharge"));

					   list.add(vo_detail);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
		try {
			ArrayList tempList = new ArrayList();
			vo.setString("zp_job_id", zp_job_id);
			String strsql = "select zp_job_id,name,start_date,end_date,principal,attendee,status,plan_id,resource_id,description,real_invite_amount from zp_job where zp_job_id = '"+zp_job_id+"'";
			ResultSet rs = dao.search(strsql,tempList);
			while(rs.next()){
			   vo.setString("start_date",PubFunc.DoFormatDate(PubFunc.FormatDate(rs.getDate("start_date"))));
		       vo.setString("end_date",PubFunc.DoFormatDate(PubFunc.FormatDate(rs.getDate("end_date"))));
	           vo.setString("name",rs.getString("name"));
	           vo.setString("principal",rs.getString("principal"));
	           vo.setString("attendee",rs.getString("attendee"));
	           vo.setString("status",rs.getString("status"));
	           vo.setString("plan_id",rs.getString("plan_id"));
	           vo.setString("resource_id",rs.getString("resource_id"));
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		} finally {
			this.getFormHM().put("zpjobvo", vo);
			this.getFormHM().put("zpjobDetailslist", list);
			this.getFormHM().put("managepriv", this.userView.getManagePrivCode());
			this.getFormHM().put("manageprivvalue", this.userView.getManagePrivCodeValue());
			if(zp_job_id != null && !"".equals(zp_job_id)){
				this.getFormHM().put("zp_job_id_value", zp_job_id);
			}else if(zp_job_id_value != null && !"".equals(zp_job_id_value)){
				this.getFormHM().put("zp_job_id_value", zp_job_id_value);
			}
			
		}

	}

	}

}
