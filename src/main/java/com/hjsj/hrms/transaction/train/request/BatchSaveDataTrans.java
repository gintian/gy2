package com.hjsj.hrms.transaction.train.request;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.businessobject.train.TrainEffectEvalBo;
import com.hjsj.hrms.businessobject.train.TransDataBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
/**
 * 培训班批量修改保存
 * @author Administrator
 *
 */
public class BatchSaveDataTrans extends IBusiness{

	public void execute() throws GeneralException {
		
		String flag = "false";
		try {
			String a_code = (String) this.getFormHM().get("a_code");
			a_code = a_code != null && a_code.trim().length() > 0 ? a_code : "";
			if ("".equals(a_code)) {
				TrainCourseBo bo = new TrainCourseBo(this.userView);
				a_code = bo.getUnitIdByBusi();
			}
			//0=查询结果，1=权限范围下允许修改的所有数据
			String selectid = (String) this.getFormHM().get("selectid");
			
			ArrayList itemid_arr = (ArrayList)this.getFormHM().get("itemid_arr");
			ArrayList itemvalue_arr = (ArrayList)this.getFormHM().get("itemvalue_arr");
			String ctrlapply = (String) this.getFormHM().get("ctrlapply");
			String ctrlcount = (String) this.getFormHM().get("ctrlcount");
			
			if((itemid_arr==null||itemid_arr.size()<1) && (ctrlapply==null||ctrlapply.length()<1)&&(ctrlcount==null||ctrlcount.length()<1)){
				this.getFormHM().put("flag", flag);
				return;
			}
			if(itemid_arr!=null&&itemvalue_arr!=null&&itemid_arr.size()!=itemvalue_arr.size()){
				this.getFormHM().put("flag", flag);
				return;
			}
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
			//0=查询结果，1=权限范围下允许修改的所有数据
			if("1".equalsIgnoreCase(selectid)){
				spflag="";
				timeflag="";
				startime="";
				endtime="";
				searchstr="";
			}
			
			TransDataBo transbo = new TransDataBo(this.getFrameconn(), "3");
			String times = transbo.timesSql(timeflag, startime, endtime);
			
			StringBuffer where = new StringBuffer();
			where.append(transbo.sqlWhere(searchstr, a_code, times, spflag));
			where.append(" and r3127 in ('03','09')");
			
			if (itemid_arr != null && itemvalue_arr != null) {
				StringBuffer sql = new StringBuffer();
				sql.append("update R31  set ");
				sql.append(transbo.getSqlItems(itemid_arr));

				ContentDAO dao = new ContentDAO(this.frameconn);
				dao.update(sql.toString() + where.toString().substring(9), transbo.getSqlItemValues(itemid_arr, itemvalue_arr));
			}
			
			if ((ctrlapply != null && ctrlapply.length() > 0) || (ctrlcount != null && ctrlcount.length() > 0)) {
				String sqlstr = "select r3101 " + where.toString();
				String[] classids = getclassid(sqlstr).split(",");

				LazyDynaBean apply = new LazyDynaBean();
				LazyDynaBean count = new LazyDynaBean();

				if (ctrlapply != null && ctrlapply.length() > 0)
					apply.set("text", ctrlapply);

				if (ctrlcount != null && ctrlcount.length() > 0)
					count.set("text", ctrlcount);

				//修改培训班的属性设置：由于每个培训班的属性设置会有不同故一个一个修改
				for (int i = 0; i < classids.length; i++) {
					TrainEffectEvalBo bo = new TrainEffectEvalBo(this.frameconn, classids[i]);
					LazyDynaBean temJob = bo.getBean("template", "job");
					LazyDynaBean temTeacher = bo.getBean("template", "teacher");
					LazyDynaBean quesJob = bo.getBean("questionnaire", "job");
					LazyDynaBean quesTeacher = bo.getBean("questionnaire", "teacher");
					LazyDynaBean ctrl_apply = bo.getBean("ctrl_apply", "");
					LazyDynaBean ctrl_count = bo.getBean("ctrl_count", "");
					
					if (ctrlapply != null && ctrlapply.length() > 0)
						ctrl_apply = apply;

					if (ctrlcount != null && ctrlcount.length() > 0)
						ctrl_count = count;

					bo.save(temJob, temTeacher, quesJob, quesTeacher, ctrl_apply, ctrl_count);
				}
			}
			flag = "true";
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.getFormHM().put("flag", flag);
	}
	/**
	 * 获取需要修改的培训班的id
	 * @param sql 查询语句
	 * @return
	 */
	private String getclassid(String sql) {
		String classid = "";
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			this.frowset = dao.search(sql);
			while (this.frowset.next()) {
				classid += this.frowset.getString("r3101");
				classid += ",";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if(classid.length()>0||classid!=null)
			classid=classid.substring(0, classid.length()-1);
		
		return classid;
	}
}
