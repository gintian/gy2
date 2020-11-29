package com.hjsj.hrms.module.gz.salaryaccounting.batch.transaction;

import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * 项目名称：hcm7.x 
 * 类名称：SetBatchImportTrans 
 * 类描述：执行批量引入交易类 
 * 创建人：sunming 
 * 创建时间：2015-7-28
 * 
 * @version
 */
public class SetBatchImportTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		String opt = (String) this.getFormHM().get("opt");
		if ("1".equals(opt)) {//opt=1 初始化时加载某年某月某次的值  opt=2 加载薪资项目的列表
			// 业务日期
			String appdate = (String) this.getFormHM().get("appdate");
			appdate = PubFunc.decrypt(SafeCode.decode(appdate));
			// 对appdate业务日期进行处理，将年月表识初始化
			appdate = appdate.replaceAll("\\.", "-");
			String[] arr = appdate.split("-");
			int year = Integer.parseInt(arr[0]);
			int month = Integer.parseInt(arr[1]);
			this.getFormHM().put("year", year);
			this.getFormHM().put("month", month);
		} else {//加载薪资项目的列表
			// 薪资id
			String salaryid = (String) this.getFormHM().get("salaryid");
			salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
			// 模块号
			String imodule = (String) this.getFormHM().get("imodule");
			imodule = PubFunc.decrypt(SafeCode.decode(imodule));
			
			String type= (String) this.getFormHM().get("type");// 1为重新导入  0为批量引入
			try {
				ArrayList list = new ArrayList();
				if (salaryid == null || "-1".equalsIgnoreCase(salaryid))
					throw new GeneralException(ResourceFactory
							.getProperty("error.notdefine.salaryid"));
				/** 薪资类别 */
				SalaryTemplateBo gzbo = new SalaryTemplateBo(this
						.getFrameconn(), Integer.parseInt(salaryid),
						this.userView);
				/** 取得全部的薪资项目 */
				ArrayList templist = gzbo.getSalaryItemList("", salaryid, 1);
				String manager = gzbo.getManager();
				String a01z0Flag = gzbo.getCtrlparam().getValue(
						SalaryCtrlParamBo.A01Z0, "flag"); // 是否显示停发标识 1：有

				HashMap map = new HashMap();
				if (SystemConfig.getPropertyValue("salaryitem") != null
						&& "true"
								.equalsIgnoreCase(SystemConfig.getPropertyValue("salaryitem"))) {
					ArrayList formulaList = gzbo.getFormulaList(-1, salaryid,
							null);
					for (int i = 0; i < formulaList.size(); i++) {
						DynaBean dbean = (LazyDynaBean) formulaList.get(i);
						String itemname = (String) dbean.get("itemname");
						map.put(itemname.toLowerCase(), "1");
					}
				}

				/** 读权限的指标是否允许重新引入=0不可以 */
				String read_field = gzbo.getCtrlparam().getValue(
						SalaryCtrlParamBo.READ_FIELD);
				if (read_field == null || "".equals(read_field))
					read_field = "0";
				for (int i = 0; i < templist.size(); i++) {
					LazyDynaBean dynabean = (LazyDynaBean) templist.get(i);
					/**初始化标识 0输入项 1累积项 2导入项 3系统项**/
					String flag = (String) dynabean.get("initflag");
					/**指标代码**/
					String itemid = (String) dynabean.get("itemid");
					if (SystemConfig.getPropertyValue("salaryitem") != null
							&& "true"
									.equalsIgnoreCase(SystemConfig.getPropertyValue("salaryitem"))) {
						if (map.get(itemid.toLowerCase()) != null)
							continue;
					}
					if ("3".equalsIgnoreCase(flag)
							&& !"A01Z0".equalsIgnoreCase(itemid))
						continue;
					
					if("1".equals(type)&& !"2".equalsIgnoreCase(flag)&& !"A01Z0".equalsIgnoreCase(itemid))//若为重新导入 则只取导入项 zhanghua 2017-3-20
						continue;
					
					LazyDynaBean tmp = new LazyDynaBean();
					tmp.set("itemid", dynabean.get("itemid") == null ? ""
							: dynabean.get("itemid"));
					tmp.set("itemdesc", dynabean.get("itemdesc") == null ? ""
							: dynabean.get("itemdesc"));

					if ("a01z0".equalsIgnoreCase(itemid)
							&& (a01z0Flag == null || "0".equals(a01z0Flag)))
						continue;
					if (manager.length() == 0
							|| manager.equalsIgnoreCase(this.userView
									.getUserName())) {
						if (!"3".equalsIgnoreCase(flag)) {
							if ("0".equals(read_field)) {
								if ("2"
										.equals(this.userView.analyseFieldPriv(itemid)))
									list.add(tmp);
							} else {
								if ("2"
										.equals(this.userView.analyseFieldPriv(itemid))
										|| "1".equals(this.userView.analyseFieldPriv(
												itemid)))
									list.add(tmp);
							}
						} else
							list.add(tmp);
					} else {
						if ("A01Z0".equalsIgnoreCase(itemid))
							list.add(tmp);
						else if (!"3".equalsIgnoreCase(flag)) {
							if ("0".equals(read_field)
									&& "2"
											.equals(this.userView.analyseFieldPriv(itemid)))
								list.add(tmp);
							else if ("1".equals(read_field)
									&& ("2"
											.equals(this.userView.analyseFieldPriv(itemid)) || "1".equals(
                                    this.userView
                                    .analyseFieldPriv(itemid))))
								list.add(tmp);
						}
					}
				}
				this.getFormHM().put("data", list);

			} catch (Exception ex) {
				ex.printStackTrace();
				throw GeneralExceptionHandler.Handle(ex);
			}
		}
	}

}
