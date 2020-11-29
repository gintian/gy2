package com.hjsj.hrms.module.gz.salaryaccounting.changecompare.transaction;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.module.gz.salaryaccounting.changecompare.businessobject.ChangeCompareBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryAccountBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.module.gz.salarytype.businessobject.ApplicationOrgBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.axis.utils.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * 薪资发放_变动比对 主界面初始化
 * 
 * @createtime July 02, 2015 9:07:55 PM
 * @author chent
 * 
 */
public class ChangeCompareTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {

		// 薪资类别号
		String salaryid = (String) this.getFormHM().get("salaryid");
		salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
		// 0:薪资 1:保险
		String gz_module = (String) this.getFormHM().get("imodule");
		gz_module = PubFunc.decrypt(SafeCode.decode(gz_module));
		// 业务日期
		String appdate = (String) this.getFormHM().get("appdate");
		appdate = PubFunc.decrypt(SafeCode.decode(appdate));
		if(appdate==null||appdate.trim().length()==0)
		{ 
				SimpleDateFormat df=new SimpleDateFormat("yyyy-MM");
				appdate=df.format(new Date())+"-01"; 
		}
		if(appdate.trim().length()==7){//业务日期如果不包含日的话加上01，防止格式不对报错  zhaoxg add 2016-12-22
			appdate = appdate+"-01";
		}
		// 发放次数
		String count = (String) this.getFormHM().get("count");
		count = PubFunc.decrypt(SafeCode.decode(count));
		if(count==null||count.trim().length()==0)
		{ 
			count="1";
		}
		try {
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			
			//共享账套非管理员只有在指定的业务日期内且应用机构置为启用时才能填报数据
			//进入的时候判断是否在应用机构设置的时间范围内
			ApplicationOrgBo aorgbo = new ApplicationOrgBo(this.getFrameconn(),salaryid,this.userView);
			String info = aorgbo.getComeInfo(gz_module,gzbo,appdate);
			if(!StringUtils.isEmpty(info)) {
				throw GeneralExceptionHandler.Handle(new Exception(info));
			}
		    
		    
			ChangeCompareBo changeCompareBo = new ChangeCompareBo(this.getFrameconn(), this.userView);// 工具类

			/** 判读用户权限 */
			CheckPrivSafeBo checkPrivSafeBo = new CheckPrivSafeBo(this
					.getFrameconn(), this.userView);
			checkPrivSafeBo.isSalarySetResource(salaryid, gz_module);

			/** 判断人员范围是否有效 */
			changeCompareBo.checkPersonScope(salaryid);
			
		    ArrayList itemList=gzbo.getSalaryItemList("",""+salaryid,1);
			gzbo.SalarySet(itemList); //判断哪些字段改变了需要同步
			
			/** 工资管理员check：不是管理员的话继续判断是否存在管理员的发放记录 */
			SalaryCtrlParamBo ctrlparam = new SalaryCtrlParamBo(this.getFrameconn(), Integer.parseInt(salaryid));
			String manager = ctrlparam.getValue(SalaryCtrlParamBo.SHARE_SET, "user");// 获取管理员
			// 不是管理员
			if (!StringUtils.isEmpty(manager)
					&& !manager.equalsIgnoreCase(this.userView.getUserName())) {
				// 判断<<薪资发放表>>中是否存在管理员的发放记录
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				String sql = "select * from gz_extend_log where salaryid=? and username=?";
				ArrayList<String> list = new ArrayList<String>();
				list.add(salaryid);
				list.add(manager);
				this.frowset = dao.search(sql, list);
				// 没有记录，提示没有表
				if (!this.frowset.next()) {
					this.getFormHM().put("error", "0");
					throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("gz_new.gz_accounting.notcreatetable")));
//					if (gz_module.equals("1")) {
//						throw GeneralExceptionHandler.Handle(new Exception(
//								ResourceFactory.getProperty("gz_new.gz_accounting.notcreatebxtable")));//该保险类别的管理员还没有建立保险表!
//					} else {
//						throw GeneralExceptionHandler.Handle(new Exception(
//								ResourceFactory.getProperty("gz_new.gz_accounting.notcreatesalarytable")));//该薪资类别的管理员还没有建立薪资表!
//					}
				}
			}
			// <TODO>同步薪资表结构暂时不要，时间性能太慢templatebo.syncGzTableStruct();

			/** 生成变动比对表：<<新增人员>>、<<减少人员>>、<<信息变动人员>>、<<停发人员>> */
			SalaryAccountBo salaryAccountBo = new SalaryAccountBo(this.frameconn, this.userView, Integer.parseInt(salaryid));
			String createResult = salaryAccountBo.executeChangeInfo(Integer.parseInt(salaryid), appdate, count); // 返回结果:"ADCT" A：增加  // D：减少 C：数据变动 T：停发

			String isExistAdd = "0", isExistDel = "0", isExistInfo = "0", isExistStop = "0";
			if (!StringUtils.isEmpty(createResult)) {
				if (createResult.indexOf("A") != -1) {
					isExistAdd = "1";
				}
				if (createResult.indexOf("D") != -1) {
					isExistDel = "1";
				}
				if (createResult.indexOf("C") != -1) {
					isExistInfo = "1";
				}
				if (createResult.indexOf("T") != -1) {
					isExistStop = "1";
				}
				// 人员库转化,前台用。
				HashMap<String, String> dbnameMap = new HashMap<String, String>();
				dbnameMap = changeCompareBo.getDbname();
				this.getFormHM().put("dbname", dbnameMap);
			}
			// 没有有效表时，前台弹出提示。需要cname(薪资类别名)
			if ("0".equals(isExistAdd) && "0".equals(isExistDel)
					&& "0".equals(isExistInfo) && "0".equals(isExistStop)) {
				RecordVo vo = new RecordVo("salarytemplate");
				vo.setInt("salaryid", Integer.parseInt(salaryid));
				ContentDAO dao = new ContentDAO(this.frameconn);
				vo = dao.findByPrimaryKey(vo);
				String name = vo.getString("cname");
				this.getFormHM().put("cname", name);
			}

			this.getFormHM().put("isExistAdd", isExistAdd);
			this.getFormHM().put("isExistDel", isExistDel);
			this.getFormHM().put("isExistInfo", isExistInfo);
			this.getFormHM().put("isExistStop", isExistStop);
			this.getFormHM().put("salaryid", salaryid);

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}
}
