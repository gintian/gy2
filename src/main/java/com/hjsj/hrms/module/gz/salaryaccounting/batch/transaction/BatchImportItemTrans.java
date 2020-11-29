package com.hjsj.hrms.module.gz.salaryaccounting.batch.transaction;

import com.hjsj.hrms.module.gz.salaryaccounting.batch.businessobject.BatchImportBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

/**
 * 
 * 项目名称：hcm7.x 
 * 类名称：BatchImportItemTrans 
 * 类描述：执行批量修改交易类 
 * 创建人：sunming 
 * 创建时间：2015-8-18
 * 
 * @version
 */
public class BatchImportItemTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		/**薪资id**/
		String salaryid = (String) this.getFormHM().get("salaryid");
		salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
		/**业务日期**/
		String appdate =  (String) this.getFormHM().get("appdate");
		appdate = PubFunc.decrypt(SafeCode.decode(appdate));
		/**次数**/
		String count = (String) this.getFormHM().get("count");
		count = PubFunc.decrypt(SafeCode.decode(count));
		String viewtype = (String) this.getFormHM().get("viewtype");//0 薪资发放 1 薪资审批
		viewtype = PubFunc.decrypt(SafeCode.decode(viewtype));
		try {

			// 如果用户没有当前薪资类别的资源权限
			BatchImportBo bo = new BatchImportBo(this.getFrameconn(), Integer
					.parseInt(salaryid), this.userView);
			bo.isSalarySetResource(null);

			/**导入类型 =1同月上次 =2上月同次 =3档案数据 =4某年某月某次*/
			String importtype = (String) this.getFormHM().get("importtype");
			/** 前台数组转换成后台ArrayList对象,前台只选中一个项目时，转换成String */
			Object obj = this.getFormHM().get("items");
			ArrayList items = null;
			if (obj instanceof String) {
				items = new ArrayList();
				items.add(obj);
			} else
				items = (ArrayList) this.getFormHM().get("items");
			/**某年月 次的组成的集合**/
			ArrayList busiDateSome = new ArrayList();
			if ("4".equals(importtype)) {
				busiDateSome = (ArrayList) this.getFormHM().get("busiDateSome");
			}
			//薪资审批中的批量引入
			if ("1".equals(viewtype)) {
				LazyDynaBean busiDate = new LazyDynaBean();
				if(appdate.trim().length()>0){
					busiDate.set("date", appdate.replaceAll("\\.", "-"));
					busiDate.set("count", count);
					//薪资审核中批量引入数据
					bo.batchImport_history(busiDate, importtype, items, busiDateSome);
					//将引入的数据同步至临时表
					bo.batchUpdateTempData(items,busiDate);
				}
			} else {
				//薪资发放中的批量引入方法
				bo.batchImport(importtype, items, busiDateSome,appdate,count);
			}
			this.getFormHM().put("viewtype", viewtype);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

	}

}
