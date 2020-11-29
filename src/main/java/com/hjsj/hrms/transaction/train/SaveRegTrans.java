package com.hjsj.hrms.transaction.train;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * <p>
 * Title:保存报名的信息
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2005-6-20:8:36:20
 * </p>
 * 
 * @author luangaojiong
 * @version 1.0
 *  
 */
public class SaveRegTrans extends IBusiness {

	/*
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {

		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try {
			/**
			 * 取得学员编号
			 */
			IDGenerator idg = new IDGenerator(2, this.getFrameconn());
			String stuId = this.userView.getUserId();
			String moveid = "0";	//活动id
			moveid = this.getFormHM().get("movementNum").toString();
			//userView.getUserId();
			/**
			 * 取得培训费用及学时
			 */
			String payMoney = "0";
			String eduTime = "0";
			String eduStart = null;
			String eduEnd = null;
			this.frowset=dao.search("select R3111,R3112,R3115,R3116 from R31 where R3101='"+ moveid + "'");
			if (this.frowset.next()) 
			{
				double payDouble = this.frowset.getDouble("R3111");
				if (new Double(payDouble) == null) {
					payDouble = 0;
				}
				payMoney = Double.toString(payDouble);
				double edutm = this.frowset.getDouble("R3112");
				if (new Double(edutm) == null) {
					edutm = 0;
				}
				eduTime = Double.toString(edutm);
				eduStart = PubFunc.FormatDate(this.frowset.getDate("R3115"));
				eduEnd = PubFunc.FormatDate(this.frowset.getDate("R3116"));
			}
			/**
			 * 取得评估结果
			 */
			/**
			 * 对该活动同一用户验证
			 */
			this.frowset=dao.search("select R4002 from R40 where R4001='"+ this.userView.getUserId() + "' and R4005='" + moveid+ "'");
			String flag = "0";
			int num = 0;
			if (this.frowset.next()) {
				this.getFormHM().put("moveFlag", "1");
				flag = "1";
			} else {
				this.getFormHM().put("moveFlag", "0");
			}
			
			/**
			 * 提交操作
			 */
			if ("0".equals(flag))
			{
	
				String sql = "insert into R40(r4001,r4002,r4005,b0110,e0122,r4006,r4007,r4008,r4009,r4010,NBASE,r4013)values ";
				String sql2 = "(?,?,?,?,?,?,?,?,?,?,?,?)";
				StringBuffer sb = new StringBuffer();
				sb.append(sql);
				sb.append(sql2);
				
				ArrayList paralist=new ArrayList();
				paralist.add(stuId);
				paralist.add(this.userView.getUserFullName());
				paralist.add(moveid);
				paralist.add(this.userView.getUserOrgId());
				paralist.add(this.userView.getUserDeptId());
				if("".equals(eduStart))
					paralist.add(null);					
				else
					paralist.add(DateUtils.getSqlDate(eduStart,"yyyy-MM-dd"));
				if("".equals(eduEnd))
					paralist.add(null);					
				else
					paralist.add(DateUtils.getSqlDate(eduEnd,"yyyy-MM-dd"));
				
				paralist.add(Double.valueOf(eduTime));
				paralist.add("");
				paralist.add(Double.valueOf(payMoney));
				paralist.add(this.getUserView().getDbname());
				paralist.add("02");
				dao.update(sb.toString(),paralist);
//				ps = con.prepareStatement(sb.toString());
//				ps.setString(1, stuId);
//				ps.setString(2, this.userView.getUserFullName());
//				ps.setString(3, moveid);
//				ps.setString(4, this.userView.getUserOrgId());
//				ps.setString(5,this.userView.getUserDeptId() );
//				ps.setString(6, eduStart);
//				ps.setString(7, eduEnd);
//				ps.setInt(8, ((int) Double.parseDouble(eduTime)));
//				ps.setString(9, "");
//				ps.setDouble(10, Double.parseDouble(payMoney));
//				num = ps.executeUpdate();
			}
			if (num > 0) {
				this.getFormHM().put("flag", "1");
			} else {
				this.getFormHM().put("flag", "0");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

	}

}