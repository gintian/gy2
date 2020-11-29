/*
 * Created on 2005-8-5
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.resource_plan;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SearchResourcePlanTrans</p>
 * <p>Description:查询人力规划，zp_hr_plan</p>
 * <p>Company:hjsj</p>
 * <p>create time:August 15, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */

public class SearchResourcePlanTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {

		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String plan_id = (String) hm.get("a_id");		
		String plan_id_value = (String)this.getFormHM().get("plan_id_value");
		String flag = (String) this.getFormHM().get("flag");
		String flag_mid = (String) this.getFormHM().get("flag_mid");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		RecordVo vo = new RecordVo("zp_hr_plan",1);
		String org_id_value = "";
		ArrayList list = new ArrayList();
		
		/**
		 * 按新增按钮时，则不进行查询，直接退出
		 */
		if ("1".equals(flag)){
			this.getFormHM().put("zpplanDetailslist", list);
			if("UM".equals(this.userView.getManagePrivCode())){
				String sql = "select parentid from organization where codeitemid = '"+this.userView.getManagePrivCodeValue()+"'";
				try{
				   this.frowset = dao.search(sql);
				   while(this.frowset.next()){
				   	  vo.setString("org_id",this.frowset.getString("parentid"));
				   	  this.getFormHM().put("zpplanvo", vo);
				   	  this.getFormHM().put("org_id_value", AdminCode.getCodeName("UN",this.getFrowset().getString("parentid")));  
				   }
				}catch(SQLException e){
					e.printStackTrace();
					throw GeneralExceptionHandler.Handle(e);
				}
			}else if("UN".equals(this.userView.getManagePrivCode())){
				vo.setString("org_id",this.userView.getManagePrivCodeValue());
				this.getFormHM().put("zpplanvo", vo);
				this.getFormHM().put("org_id_value", AdminCode.getCodeName("UN",this.userView.getManagePrivCodeValue()));  
			}else{
				this.getFormHM().put("org_id_value","");
				this.getFormHM().put("orgparentcode","");
			}
			this.getFormHM().put("managepriv",this.userView.getManagePrivCode());	
			this.getFormHM().put("orgparentcode",this.userView.getManagePrivCodeValue());
			return;
		}else if("0".equals(flag) && "1".equals(flag_mid)){
				try {
					String sql = "";
					if("UM".equals(this.userView.getManagePrivCode())){
					    sql = "select key_id,plan_id,type,dept_id,pos_id,amount,valid_date,reason,gather_id from zp_hr_plan_details where plan_id = '"+ plan_id_value + "' and dept_id = '"+this.userView.getManagePrivCodeValue()+"'";
					}else{
						sql = "select key_id,plan_id,type,dept_id,pos_id,amount,valid_date,reason,gather_id from zp_hr_plan_details where plan_id = '"+ plan_id_value + "'";
					}
					this.frowset = dao.search(sql);
					while (this.frowset.next()) {
						RecordVo vo_detail = new RecordVo("zp_hr_plan_details",1);
						vo_detail.setString("plan_id", this.getFrowset().getString("plan_id"));
						vo_detail.setString("key_id", this.getFrowset().getString("key_id"));
						vo_detail.setString("dept_id", this.getFrowset().getString("dept_id"));
						vo_detail.setString("pos_id", this.getFrowset().getString("pos_id"));
						vo_detail.setString("amount", this.getFrowset().getString("amount"));
						vo_detail.setString("valid_date", PubFunc.DoFormatDate(PubFunc.FormatDate(this.getFrowset().getDate("valid_date"))));
						vo_detail.setString("type", this.getFrowset().getString("type"));
						vo_detail.setString("gather_id", this.getFrowset().getString("gather_id"));
						String reason = PubFunc.toHtml(this.getFrowset().getString("reason"));
						if(reason.length() > 20){
							vo_detail.setString("reason",reason.substring(0,20)+"...");
				        }else{
				         	vo_detail.setString("reason",reason);
				        }

						list.add(vo_detail);
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					vo.setString("plan_id", plan_id_value);
					ArrayList tempList = new ArrayList();
					String strsql = "select plan_id,org_id,name,run_date,description,create_date,staff_id,status from zp_hr_plan where plan_id = '"+plan_id_value+"'";
					ResultSet rs = dao.search(strsql,tempList);
					while(rs.next()){
			           vo.setString("run_date",PubFunc.DoFormatDate(PubFunc.FormatDate(rs.getDate("run_date"))));
			           vo.setString("create_date",PubFunc.DoFormatDate(PubFunc.FormatDate(rs.getDate("create_date"))));
			           vo.setString("org_id",rs.getString("org_id"));
			           vo.setString("name",rs.getString("name"));
			           vo.setString("description",rs.getString("description"));
			           vo.setString("staff_id",rs.getString("staff_id"));
			           vo.setString("status",rs.getString("status"));
			           org_id_value = AdminCode.getCodeName("UN",rs.getString("org_id"));
			           this.getFormHM().put("org_id", rs.getString("org_id"));
					}
				} catch (SQLException sqle) {
					sqle.printStackTrace();
					throw GeneralExceptionHandler.Handle(sqle);
				} finally {
					this.getFormHM().put("zpplanvo", vo);
					this.getFormHM().put("zpplanDetailslist", list);
					this.getFormHM().put("plan_id_value", plan_id_value);
					this.getFormHM().put("org_id_value",org_id_value);
					this.getFormHM().put("managepriv",this.userView.getManagePrivCode());
					this.getFormHM().put("orgparentcode",this.userView.getManagePrivCodeValue());
				}
		}else{
				try {
					String sql = "";
					if("UM".equals(this.userView.getManagePrivCode())){
					   if(plan_id != null && !"".equals(plan_id)){
						   sql = "select key_id,plan_id,type,dept_id,pos_id,amount,valid_date,reason,gather_id from zp_hr_plan_details where plan_id = '"+ plan_id + "' and dept_id = '"+this.userView.getManagePrivCodeValue()+"'";
					   }else if(plan_id_value != null && !"".equals(plan_id_value)){
						   sql = "select key_id,plan_id,type,dept_id,pos_id,amount,valid_date,reason,gather_id from zp_hr_plan_details where plan_id = '"+ plan_id_value + "' and dept_id = '"+this.userView.getManagePrivCodeValue()+"'";
					   }
					}else{
						if(plan_id != null && !"".equals(plan_id)){
							sql = "select key_id,plan_id,type,dept_id,pos_id,amount,valid_date,reason,gather_id from zp_hr_plan_details where plan_id = '"+ plan_id + "'";
						}else if(plan_id_value != null && !"".equals(plan_id_value)){
							sql = "select key_id,plan_id,type,dept_id,pos_id,amount,valid_date,reason,gather_id from zp_hr_plan_details where plan_id = '"+ plan_id_value + "'";
						}
					}
					this.frowset = dao.search(sql);
					while (this.frowset.next()) {
						RecordVo vo_detail = new RecordVo("zp_hr_plan_details",1);
						vo_detail.setString("plan_id", this.getFrowset().getString("plan_id"));
						vo_detail.setString("key_id", this.getFrowset().getString("key_id"));
						vo_detail.setString("dept_id", this.getFrowset().getString("dept_id"));
						vo_detail.setString("pos_id", this.getFrowset().getString("pos_id"));
						vo_detail.setString("amount", this.getFrowset().getString("amount"));
						vo_detail.setString("valid_date", PubFunc.DoFormatDate(PubFunc.FormatDate(this.getFrowset().getDate("valid_date"))));
						vo_detail.setString("type", this.getFrowset().getString("type"));
						vo_detail.setString("gather_id", this.getFrowset().getString("gather_id"));
						String reason = PubFunc.toHtml(this.getFrowset().getString("reason"));
						if(reason.length() > 20){
							vo_detail.setString("reason",reason.substring(0,20)+"...");
				        }else{
				         	vo_detail.setString("reason",reason);
				        }

						list.add(vo_detail);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
		try {
			vo.setString("plan_id", plan_id);
			ArrayList tempList = new ArrayList();
			String sqle = "select plan_id,org_id,name,run_date,description,create_date,staff_id,status from zp_hr_plan where plan_id = '"+plan_id+"'";
			ResultSet rst = dao.search(sqle,tempList);
			while(rst.next()){
				vo.setString("run_date",PubFunc.DoFormatDate(PubFunc.FormatDate(rst.getDate("run_date"))));
		           vo.setString("create_date",PubFunc.DoFormatDate(PubFunc.FormatDate(rst.getDate("create_date"))));
	           vo.setString("org_id",rst.getString("org_id"));
	           vo.setString("name",rst.getString("name"));
	           vo.setString("description",rst.getString("description"));
	           vo.setString("staff_id",rst.getString("staff_id"));
	           vo.setString("status",rst.getString("status"));
	           org_id_value = AdminCode.getCodeName("UN",rst.getString("org_id"));
	           this.getFormHM().put("org_id", rst.getString("org_id"));
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		} finally {
			this.getFormHM().put("zpplanvo", vo);
			this.getFormHM().put("zpplanDetailslist", list);
			this.getFormHM().put("managepriv",this.userView.getManagePrivCode());
			this.getFormHM().put("orgparentcode",this.userView.getManagePrivCodeValue());
			if(plan_id != null && !"".equals(plan_id)){
				this.getFormHM().put("plan_id_value", plan_id);
			}else if(plan_id_value != null && !"".equals(plan_id_value)){
				this.getFormHM().put("plan_id_value", plan_id_value);
			}
			this.getFormHM().put("org_id_value",org_id_value);
		}

	}
  }
}
