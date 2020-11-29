/*
 * Created on 2005-8-9
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_plan;

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

/**
 * <p>Title:SearchZpplanTrans</p>
 * <p>Description:查询招聘计划,zp_plan</p>
 * <p>Company:hjsj</p>
 * <p>create time:August 15, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */

public class SearchZpplanTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		String plan_id_value = (String)this.getFormHM().get("plan_id_value");
		String plan_id=plan_id_value;
		String flag = (String) this.getFormHM().get("flag");
		String flag_mid = (String) this.getFormHM().get("flag_mid");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		RecordVo vo = new RecordVo("zp_plan",1);
		ArrayList list = new ArrayList();
		String org_id_value = "";
		String dept_id_value = "";
		//System.out.println(flag);
		//System.out.println(flag_mid);
		//System.out.println(plan_id);
		//System.out.println(plan_id_value);
		/**
		 * 按新增按钮时，则不进行查询，直接退出
		 */
		if ("1".equals(flag)) {
			this.getFormHM().put("zpplanDetailslist", list);
			this.getFormHM().put("zpplanDetailslist", list);
			if("UM".equals(this.userView.getManagePrivCode())){
				String sql = "select parentid from organization where codeitemid = '"+this.userView.getManagePrivCodeValue()+"'";
				try{
				   this.frowset = dao.search(sql);
				   while(this.frowset.next()){
				   	  vo.setString("org_id",this.frowset.getString("parentid"));
				   	  this.getFormHM().put("zpplanvo", vo);
				   	  this.getFormHM().put("org_id_value", AdminCode.getCodeName("UN",this.getFrowset().getString("parentid")));  
				   	  this.getFormHM().put("deptparentcode",this.getFrowset().getString("parentid"));
				   }
				}catch(SQLException e){
					e.printStackTrace();
					throw GeneralExceptionHandler.Handle(e);
				}
			}else if("UN".equals(this.userView.getManagePrivCode())){
				vo.setString("org_id",this.userView.getManagePrivCodeValue());
				this.getFormHM().put("zpplanvo", vo);
				this.getFormHM().put("org_id_value", AdminCode.getCodeName("UN",this.userView.getManagePrivCodeValue())); 
				this.getFormHM().put("deptparentcode",this.userView.getManagePrivCodeValue());
			}else{
				this.getFormHM().put("org_id_value","");
				this.getFormHM().put("orgparentcode","");
				this.getFormHM().put("deptparentcode", "");
			}
			this.getFormHM().put("managepriv",this.userView.getManagePrivCode());
			this.getFormHM().put("orgparentcode",this.userView.getManagePrivCodeValue());
			return;
		}else if("0".equals(flag) && "1".equals(flag_mid)){
				try {
					String sql = "";
					if("UM".equals(this.userView.getManagePrivCode())){
					   sql = "select details_id,dept_id,pos_id,amount,domain,plan_id,gather_id,invite_amount,invite_flag,status from zp_plan_details where plan_id = '"+ plan_id_value + "' and dept_id = '"+this.userView.getManagePrivCodeValue()+"'";
					}else{
						sql = "select details_id,dept_id,pos_id,amount,domain,plan_id,gather_id,invite_amount,invite_flag,status from zp_plan_details where plan_id = '"+ plan_id_value + "'";
					}
				    this.frowset = dao.search(sql);
					while (this.frowset.next()) {
						RecordVo vo_detail = new RecordVo("zp_plan_details");
						vo_detail.setString("details_id", this.getFrowset().getString("details_id"));
						vo_detail.setString("dept_id", this.getFrowset().getString("dept_id"));
						vo_detail.setString("pos_id", this.getFrowset().getString("pos_id"));
						vo_detail.setString("amount", this.getFrowset().getString("amount"));
						vo_detail.setString("domain", PubFunc.toHtml(this.getFrowset().getString("domain")));
						vo_detail.setString("plan_id", this.getFrowset().getString("plan_id"));
						vo_detail.setString("gather_id", this.getFrowset().getString("gather_id"));
						vo_detail.setString("invite_amount", this.getFrowset().getString("invite_amount"));
						vo_detail.setString("invite_flag", this.getFrowset().getString("invite_flag"));
						vo_detail.setString("status", this.getFrowset().getString("status"));

						list.add(vo_detail);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					  ArrayList tempList= new ArrayList();
					  vo.setString("plan_id", plan_id_value);
					  String strsql = "select plan_id,name,org_id,start_date,end_date,budget_fee,real_fee,plan_invite_amount,real_invite_amount,dept_id,staff_id,approve_date,domain,zp_object,status from zp_plan where plan_id = '"+plan_id_value+"'";
					  ResultSet rs = dao.search(strsql,tempList);
					  while(rs.next()){
					  	 vo.setString("start_date",PubFunc.DoFormatDate(PubFunc.FormatDate(rs.getDate("start_date"))));
				         vo.setString("end_date",PubFunc.DoFormatDate(PubFunc.FormatDate(rs.getDate("end_date"))));
			             vo.setString("name",rs.getString("name"));
			             vo.setString("org_id",rs.getString("org_id"));
			             vo.setString("budget_fee",rs.getString("budget_fee"));
			             vo.setString("plan_invite_amount",rs.getString("plan_invite_amount"));
			             vo.setString("dept_id",rs.getString("dept_id"));
			             vo.setString("staff_id",rs.getString("staff_id"));
			             vo.setString("domain",rs.getString("domain"));
			             vo.setString("zp_object",rs.getString("zp_object"));
			             vo.setString("status",rs.getString("status"));
			             org_id_value = AdminCode.getCodeName("UN",rs.getString("org_id"));
			             dept_id_value = AdminCode.getCodeName("UM",rs.getString("dept_id"));
			             this.getFormHM().put("deptparentcode",rs.getString("org_id"));
			             this.getFormHM().put("org_id",rs.getString("org_id"));
			             this.getFormHM().put("dept_id",rs.getString("dept_id"));
					  }
				} catch (SQLException sqle) {
					sqle.printStackTrace();
					throw GeneralExceptionHandler.Handle(sqle);
				} finally {
					this.getFormHM().put("zpplanvo", vo);
					this.getFormHM().put("zpplanDetailslist", list);
					this.getFormHM().put("plan_id_value", plan_id_value);
					this.getFormHM().put("org_id_value", org_id_value);
					this.getFormHM().put("dept_id_value", dept_id_value);
					this.getFormHM().put("managepriv",this.userView.getManagePrivCode());
					this.getFormHM().put("orgparentcode",this.userView.getManagePrivCodeValue());
				}
		}else{
				try {
					String sql = "";
					plan_id=plan_id_value;
					if("UM".equals(this.userView.getManagePrivCode())){
					   if(plan_id != null && !"".equals(plan_id)){
						   sql = "select details_id,dept_id,pos_id,amount,domain,plan_id,gather_id,invite_amount,invite_flag,status from zp_plan_details where plan_id = '"+ plan_id + "' and dept_id = '"+this.userView.getManagePrivCodeValue()+"'";
					   }else if(plan_id_value != null && !"".equals(plan_id_value)){
						   sql = "select details_id,dept_id,pos_id,amount,domain,plan_id,gather_id,invite_amount,invite_flag,status from zp_plan_details where plan_id = '"+ plan_id_value + "' and dept_id = '"+this.userView.getManagePrivCodeValue()+"'";
					   }
					}else{
						if(plan_id != null && !"".equals(plan_id)){
							sql = "select details_id,dept_id,pos_id,amount,domain,plan_id,gather_id,invite_amount,invite_flag,status from zp_plan_details where plan_id = '"+ plan_id + "'";
						}else if(plan_id_value != null && !"".equals(plan_id_value)){
							plan_id=plan_id_value;
							sql = "select details_id,dept_id,pos_id,amount,domain,plan_id,gather_id,invite_amount,invite_flag,status from zp_plan_details where plan_id = '"+ plan_id_value + "'";
						}
					}
					this.frowset = dao.search(sql);
					while (this.frowset.next()) {
						RecordVo vo_detail = new RecordVo("zp_plan_details");
						vo_detail.setString("details_id", this.getFrowset().getString("details_id"));
				        vo_detail.setString("dept_id", this.getFrowset().getString("dept_id"));
				        vo_detail.setString("pos_id", this.getFrowset().getString("pos_id"));
				        vo_detail.setString("amount", this.getFrowset().getString("amount"));
				        vo_detail.setString("domain", PubFunc.toHtml(this.getFrowset().getString("domain")));
				        vo_detail.setString("plan_id", this.getFrowset().getString("plan_id"));
				        vo_detail.setString("gather_id", this.getFrowset().getString("gather_id"));
				        vo_detail.setString("invite_amount", this.getFrowset().getString("invite_amount"));
		                vo_detail.setString("invite_flag", this.getFrowset().getString("invite_flag"));
				        vo_detail.setString("status", this.getFrowset().getString("status"));

						list.add(vo_detail);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
		try {
			  ArrayList tempList= new ArrayList();
			  vo.setString("plan_id", plan_id);
			  String strsql = "select plan_id,name,org_id,start_date,end_date,budget_fee,real_fee,plan_invite_amount,real_invite_amount,dept_id,staff_id,approve_date,domain,zp_object,status from zp_plan where plan_id = '"+plan_id+"'";
			  ResultSet rs = dao.search(strsql,tempList);
			  while(rs.next()){
			  	 vo.setString("start_date",PubFunc.DoFormatDate(PubFunc.FormatDate(rs.getDate("start_date"))));
		         vo.setString("end_date",PubFunc.DoFormatDate(PubFunc.FormatDate(rs.getDate("end_date"))));
	             vo.setString("name",rs.getString("name"));
	             vo.setString("org_id",rs.getString("org_id"));
	             vo.setString("budget_fee",rs.getString("budget_fee"));
	             vo.setString("plan_invite_amount",rs.getString("plan_invite_amount"));
	             vo.setString("dept_id",rs.getString("dept_id"));
	             vo.setString("staff_id",rs.getString("staff_id"));
	             vo.setString("domain",rs.getString("domain"));
	             vo.setString("zp_object",rs.getString("zp_object"));
	             vo.setString("status",rs.getString("status"));
	             org_id_value = AdminCode.getCodeName("UN",rs.getString("org_id"));
	             dept_id_value = AdminCode.getCodeName("UM",rs.getString("dept_id"));
	             this.getFormHM().put("deptparentcode",rs.getString("org_id"));
			  }
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		} finally {
			this.getFormHM().put("zpplanvo", vo);
			this.getFormHM().put("zpplanDetailslist", list);
			if(plan_id != null && !"".equals(plan_id)){
				this.getFormHM().put("plan_id_value", plan_id);
			}else if(plan_id_value != null && !"".equals(plan_id_value)){
				this.getFormHM().put("plan_id_value", plan_id_value);
			}
			this.getFormHM().put("managepriv",this.userView.getManagePrivCode());
			this.getFormHM().put("orgparentcode",this.userView.getManagePrivCodeValue());
			this.getFormHM().put("org_id_value", org_id_value);
			this.getFormHM().put("dept_id_value", dept_id_value);
		}

	}

	}

}
