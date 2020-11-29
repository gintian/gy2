package com.hjsj.hrms.transaction.kq.options.sign_point;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 考勤点机构操作
 * <p>Title: SelSignOrgTrans </p>
 * <p>Description: </p>
 * <p>Company: hjsj</p>
 * <p>create time  2014-6-16 下午1:48:17</p>
 * @author jingq
 * @version 1.0
 */
public class SelSignOrgTrans extends IBusiness{

	private static final long serialVersionUID = 1L;

	public void execute() throws GeneralException {
		String pid = (String) this.getFormHM().get("pid");
		String cflag = (String) this.getFormHM().get("cflag");// 操作标识
		if ("0".equals(cflag)) {// 查询
			String sql = "select codesetid,codeitemid from kq_sign_point_org where pid = '" + pid + "'";
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			StringBuffer org = new StringBuffer(",");
			try {
				this.frowset = dao.search(sql);
				String codesetid, codeitemid;
				while (this.frowset.next()) {
					codesetid = this.frowset.getString("codesetid");
					codeitemid = this.frowset.getString("codeitemid");
					org.append(codesetid + codeitemid + ",");
				}
			} catch (Exception e) {
				throw GeneralExceptionHandler.Handle(e);
			}
			this.getFormHM().put("orglist", org.toString());
			this.getFormHM().put("pid", pid);
		} else if ("1".equals(cflag)) {// 修改
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String sql1 = "delete from kq_sign_point_org where pid = '" + pid + "'";
			ArrayList volist = new ArrayList();
			try {
				// 清空原始的数据
				dao.delete(sql1, new ArrayList());
				String orglist = (String) this.getFormHM().get("orglist");
				String[] list = orglist.split(",");
				int num = this.getNum();
				for (int i = 0; i < list.length; i++) {
					// 判断传入数据是否合理
					if (list[i].length() > 2) {
						RecordVo vo = new RecordVo("kq_sign_point_org");
						vo.setString("a0000", (++num) + "");
						vo.setString("pid", pid);
						vo.setString("codesetid", list[i].substring(0, 2));
						vo.setString("codeitemid", list[i].substring(2));
						volist.add(vo);
					}
				}
				// 注入新数据
				dao.addValueObject(volist);
			} catch (Exception e) {
				throw GeneralExceptionHandler.Handle(e);
			}
			this.getFormHM().put("cflag", "88");
		}
	}
	
	/**
	 * 
	 * @Title: getNum   
	 * @Description:获取数据库中最大A0000   
	 * @return int
	 */
	private int getNum() {
		int num = 0;
		String sql = "select max(A0000) num from kq_sign_point_org";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			this.frowset = dao.search(sql);
			while (this.frowset.next()) {
				String n = this.frowset.getString("num");
				num = n == null ? 0 : Integer.parseInt(n);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return num;
	}

}
