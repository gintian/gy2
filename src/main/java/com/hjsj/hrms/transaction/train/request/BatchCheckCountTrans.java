package com.hjsj.hrms.transaction.train.request;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.businessobject.train.TransDataBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 培训班批量修改检测符合条件的数据共多少条
 * @author chenxg 2014-05-12
 *
 */
public class BatchCheckCountTrans extends IBusiness {

	public void execute() throws GeneralException {

		try {
			String a_code = (String) this.getFormHM().get("a_code");
			a_code = a_code != null && a_code.trim().length() > 0 ? a_code : "";
			if ("".equals(a_code)) {
				TrainCourseBo bo = new TrainCourseBo(this.userView);
				a_code = bo.getUnitIdByBusi();
			}

			String selectid = (String) this.getFormHM().get("selectid");
			//a_code为所属单位，当获取的单位值为空时默认没有权限抛出异常
			if ("".equals(a_code) && !userView.isSuper_admin())
				throw new GeneralException(ResourceFactory.getProperty("train.job.authorization1"));

			String spflag = (String) this.getFormHM().get("spflag");
			spflag = spflag != null && spflag.trim().length() > 0 ? spflag : "";
			spflag = "00".equalsIgnoreCase(spflag) ? "" : spflag;

			String timeflag = (String) this.getFormHM().get("timeflag");
			timeflag = timeflag != null && timeflag.trim().length() > 0 ? timeflag : "";
			timeflag = "00".equalsIgnoreCase(timeflag) ? "" : timeflag;

			String startime = (String) this.getFormHM().get("startime");
			startime = startime != null && startime.trim().length() > 0 ? startime : "";

			String endtime = (String) this.getFormHM().get("endtime");
			endtime = endtime != null && endtime.trim().length() > 0 ? endtime : "";

			String searchstr = (String) this.getFormHM().get("searchstr");
			searchstr = searchstr != null && searchstr.trim().length() > 0 ? searchstr : "";

			//0=查询结果 ，1=权限范围下所有允许修改的数据 
			if("1".equalsIgnoreCase(selectid)){
				spflag="";
				timeflag="";
				startime="";
				endtime="";
				searchstr="";
			}
			
			TransDataBo transbo = new TransDataBo(this.getFrameconn(), "3");

			StringBuffer sql = new StringBuffer();
			String times = transbo.timesSql(timeflag, startime, endtime);
			sql.append("select count(1) count ");
			sql.append(transbo.sqlWhere(searchstr, a_code, times, spflag));
			sql.append(" and r3127 in ('03','09')");
			if(!this.userView.isSuper_admin()){
			    String where = TrainCourseBo.getUnitIdByBusiStrWhere(this.userView);
			    where = where.replaceFirst("where", "and");
			    sql.append(" " + where);
			}

			String count = "0";
			ContentDAO dao = new ContentDAO(this.frameconn);
			this.frowset = dao.search(sql.toString());
			if (this.frowset.next())
				count = this.frowset.getString("count");

			this.getFormHM().put("count", count);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
