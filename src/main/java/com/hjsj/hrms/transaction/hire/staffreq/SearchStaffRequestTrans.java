/*
 * Created on 2005-8-2
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.staffreq;

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
 * <p>Title:SearchStaffRequestTrans</p>
 * <p>Description:查询临时用工申请，zp_gather</p>
 * <p>Company:hjsj</p>
 * <p>create time:August 15, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */

public class SearchStaffRequestTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String  gather_type=(String)hm.get("gather_type");
		//String	gather_id = (String)hm.get("a_id");
		String gather_id_value = (String)this.getFormHM().get("gather_id_value");
		String gather_id=gather_id_value;
		String flag = (String) this.getFormHM().get("flag");
		String flag_mid = (String) this.getFormHM().get("flag_mid");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		RecordVo vo = new RecordVo("zp_gather",1);
		String org_id_value = "";
		String dept_id_value = "";
		String pos_id_value = "";
		ArrayList list = new ArrayList();
		/**
		 * 按新增按钮时，则不进行查询，直接退出
		 */
		if ("1".equals(flag)) {
			vo.setInt("gather_type",Integer.parseInt(gather_type));
			this.getFormHM().put("gatherPoslist", list);
			if("UM".equals(this.userView.getManagePrivCode())){
				String sql = "select parentid from organization where codeitemid = '"+this.userView.getManagePrivCodeValue()+"'";
				try{
				   this.frowset = dao.search(sql);
				   while(this.frowset.next()){
				   	  vo.setString("org_id",this.frowset.getString("parentid"));
				   	  vo.setString("dept_id",this.userView.getManagePrivCodeValue());
				   	  this.getFormHM().put("zpgathervo", vo);
				   	  this.getFormHM().put("org_id_value", AdminCode.getCodeName("UN",this.getFrowset().getString("parentid")));  
				   	  this.getFormHM().put("dept_id_value", AdminCode.getCodeName("UM",this.userView.getManagePrivCodeValue()));  
				   }
				}catch(SQLException e){
					e.printStackTrace();
					throw GeneralExceptionHandler.Handle(e);
				}
			}else if("UN".equals(this.userView.getManagePrivCode())){
				vo.setString("org_id",this.userView.getManagePrivCodeValue());
				this.getFormHM().put("zpgathervo", vo);
				this.getFormHM().put("org_id_value", AdminCode.getCodeName("UN",this.userView.getManagePrivCodeValue()));  
				this.getFormHM().put("dept_id_value","");
				this.getFormHM().put("deptparentcode",this.userView.getManagePrivCodeValue());
			}else{
				this.getFormHM().put("dept_id_value","");
				this.getFormHM().put("org_id_value","");
				this.getFormHM().put("orgparentcode","");
				this.getFormHM().put("deptparentcode","");
			}
			this.getFormHM().put("managepriv",this.userView.getManagePrivCode());
			this.getFormHM().put("orgparentcode",this.userView.getManagePrivCodeValue());
			return;
		}else{
				try {
					String sql = "";
					if(gather_id != null && !"".equals(gather_id)){
						 sql = "select gather_id,pos_id,amount,type,reason from zp_gather_pos where gather_id = '"+ gather_id + "'";
					}else if(gather_id_value != null && !"".equals(gather_id_value)){
						 sql = "select gather_id,pos_id,amount,type,reason from zp_gather_pos where gather_id = '"+ gather_id_value + "'";
					     gather_id=gather_id_value;
					}
					this.frowset = dao.search(sql);
					while (this.frowset.next()) {
						RecordVo vo_pos = new RecordVo("zp_gather_pos");
						vo_pos.setString("gather_id", this.getFrowset().getString("gather_id"));
						vo_pos.setString("pos_id", this.getFrowset().getString("pos_id"));
						vo_pos.setInt("amount", this.getFrowset().getInt("amount"));
						vo_pos.setString("type", this.getFrowset().getString("type"));
						String reason = PubFunc.toHtml(this.getFrowset().getString("reason"));
						if(reason.length() > 20){
							vo_pos.setString("reason",reason.substring(0,20)+"...");
				         }else{
				          	vo_pos.setString("reason",reason);
				         }

						list.add(vo_pos);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}		
		    try {
		      ArrayList tempList = new ArrayList();
			  vo.setString("gather_id", gather_id);
			  String strsql = "select gather_id,org_id,dept_id,valid_date,gather_type,create_date,staff_id,usedflag,status from zp_gather where gather_id = '"+gather_id+"'";
			  ResultSet rs = dao.search(strsql,tempList);
			  while(rs.next()){
	             vo.setString("valid_date",PubFunc.DoFormatDate(PubFunc.FormatDate(rs.getDate("valid_date"))));
	             vo.setString("create_date",PubFunc.DoFormatDate(PubFunc.FormatDate(rs.getDate("create_date")))); 
	             vo.setString("org_id",rs.getString("org_id")); 
	             vo.setString("dept_id",rs.getString("dept_id")); 
	             vo.setInt("gather_type",rs.getInt("gather_type"));  
	             vo.setString("staff_id",rs.getString("staff_id"));  
	             vo.setInt("usedflag",rs.getInt("usedflag")); 
	             vo.setString("status",rs.getString("status")); 
			     org_id_value = AdminCode.getCodeName("UN",vo.getString("org_id"));
			     dept_id_value = AdminCode.getCodeName("UM",vo.getString("dept_id"));
			     this.getFormHM().put("deptparentcode",rs.getString("org_id"));
			     this.getFormHM().put("org_id",rs.getString("org_id"));
			     this.getFormHM().put("dept_id",rs.getString("dept_id"));
			  }
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);     
		} finally {
			this.getFormHM().put("zpgathervo", vo);
			this.getFormHM().put("gatherPoslist", list);
			this.getFormHM().put("org_id_value",org_id_value);
			this.getFormHM().put("dept_id_value",dept_id_value);
			this.getFormHM().put("managepriv",this.userView.getManagePrivCode());
			this.getFormHM().put("orgparentcode",this.userView.getManagePrivCodeValue());
			if(gather_id != null && !"".equals(gather_id)){
				this.getFormHM().put("gather_id_value", gather_id);
			}else if(gather_id_value != null && !"".equals(gather_id_value)){
				this.getFormHM().put("gather_id_value", gather_id_value);
			}
		}
	  }
	}
}