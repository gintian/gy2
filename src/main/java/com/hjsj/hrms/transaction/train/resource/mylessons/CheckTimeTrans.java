package com.hjsj.hrms.transaction.train.resource.mylessons;

import com.hjsj.hrms.businessobject.train.trainexam.exam.mytest.MyTestBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CheckTimeTrans extends IBusiness {

	public void execute() throws GeneralException {
		String a0100 = (String) this.userView.getA0100();
		String r5100 = (String) this.getFormHM().get("R5100");
		r5100 = PubFunc.decrypt(SafeCode.decode(r5100));
		String nbase = (String) this.userView.getDbname();

		StringBuffer buff = new StringBuffer();
		ContentDAO dao = new ContentDAO(this.frameconn);
		String check = "no";//是否有考试计划
		String che = "no";//自测标识
		int r5000 = 0;
		int r5300 = 0;
		int r5400 = 0;
		int type = 0;
		int modle = 0;
		boolean flag = false;
		RowSet rs = null;
		RowSet row = null;
		RowSet rss = null;
		try {
			//查询课程的学习进度
			this.frowset = dao.search("select r5000 from r51 where r5100=" + r5100);
			if (this.frowset.next()) {
				r5000 = this.frowset.getInt("r5000");
				buff .append("select lprogress from tr_selected_lesson where a0100='");
				buff.append(a0100);
				buff.append("' and nbase='");
				buff.append(nbase);
				buff.append("' and r5000=(");
				buff.append("select r5000 from r50 where r5000=");
				buff.append(r5000);
				buff.append(" and R5018='2'");
				buff.append(")");

				rs = dao.search(buff.toString());
				if (rs.next()) {
					int lprogress = rs.getInt("lprogress");
					if (lprogress == 100)
						flag = true;
				}
			}
			if (flag) {//查询课程关联的试卷
				this.frowset = dao.search("select r5300 from tr_lesson_paper where r5000=" + r5000);
				if (this.frowset.next()) {
					r5300 = this.frowset.getInt("r5300");
					this.frowset = dao.search("select r5315 from r53 where r5300=" + r5300 + " and r5311='04'");
					if (this.frowset.next())
						modle = this.frowset.getInt("r5315");
					else
						r5300 = 0;

				}
				if (r5300 != 0) {
					//查询课程关联的考试计划
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String newDate = sdf.format(new Date());

					buff.delete(0, buff.length());
					buff.append("select r5400,r5409 from r54 where r5300=");
					buff.append(r5300);
					buff.append(" and r5411='05'");
					buff.append(" and " + Sql_switcher.dateValue(newDate) + " between r5405 and r5406");
					row = dao.search(buff.toString());
					if (row.next()) {
						r5400 = row.getInt("r5400");
						type = row.getInt("r5409");
					}

					if (r5400 != 0 && type != 0) {
						//查询考试计划中的参考人员是否包含当前用户
						buff.delete(0, buff.length());
						buff.append("select nbase,a0100,a0101 from r55 where r5400=");
						buff.append(r5400);
						buff.append(" and nbase='");
						buff.append(nbase);
						buff.append("' and a0100='");
						buff.append(a0100);
						buff.append("' and r5513=-1");

						rss = dao.search(buff.toString());
						if (rss.next())
							check = "yes";
						else
							che = "yes";

					} else
						che = "yes";
					
					//Begin 检查自考次数 13-08-05 gdd
					buff.delete(0, buff.length());
					int examNum = 0;
					MyTestBo bo = new MyTestBo(this.frameconn);
					String testCount = bo.getStringByR5000(r5000+"", "r5026");
					buff.append("select COUNT('1') as examNum from tr_selfexam_paper where nbase='"+nbase+"' and a0100='"+a0100+"' and r5300="+r5300);
					rss = dao.search(buff.toString());
					rss.next();
					examNum = rss.getInt("examNum");
					if(examNum>= Integer.parseInt(testCount))
						che = "no";
					//End	
					
				}
				
				
			}

			this.getFormHM().put("check", check);
			this.getFormHM().put("che", che);
			this.getFormHM().put("r5000", SafeCode.encode(PubFunc.encrypt(String.valueOf(r5000))));
			this.getFormHM().put("r5300", SafeCode.encode(PubFunc.encrypt(String.valueOf(r5300))));
			this.getFormHM().put("r5400", SafeCode.encode(PubFunc.encrypt(String.valueOf(r5400))));
			this.getFormHM().put("type", Integer.valueOf(type));
			this.getFormHM().put("modle", Integer.valueOf(modle));
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (row != null)
					row.close();
				if (rss != null)
					rss.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
