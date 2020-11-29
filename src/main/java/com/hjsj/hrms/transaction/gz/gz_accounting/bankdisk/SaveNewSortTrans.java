package com.hjsj.hrms.transaction.gz.gz_accounting.bankdisk;

import com.hjsj.hrms.businessobject.gz.BankDiskSetBo;
import com.hjsj.hrms.businessobject.gz.SalaryLProgramBo;
import com.hjsj.hrms.businessobject.gz.gz_analyse.HistoryDataBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
/**
 * 30200710258
 * <p>Title:SaveNewSortTrans.java</p>
 * <p>Description>:SaveNewSortTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Dec 25, 2009 2:44:29 PM</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class SaveNewSortTrans extends IBusiness{
	public void execute() throws GeneralException {
		try
		{
			String sortStr=(String)this.getFormHM().get("sortStr");
			String salaryid=(String)this.getFormHM().get("salaryid");
			String model=this.getFormHM().get("model")!=null?(String)this.getFormHM().get("model"):"";//history 表示为薪资历史数据分析进入
			if(!"history".equalsIgnoreCase(model)) {

				String str = "select lprogram from salarytemplate where salaryid=" + salaryid;
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				RowSet rs = null;
				rs = dao.search(str);
				String cond_str = "";
				while (rs.next()) {
					cond_str = rs.getString("lprogram");
				}
				if (!(cond_str == null || cond_str.trim().length() <= 0)) {

					SalaryLProgramBo lbo = new SalaryLProgramBo(cond_str, this.userView);
					HashMap map = lbo.getItemMap();
					LinkedHashMap<String, LazyDynaBean> list = lbo.getItemlist();
					String[] arr = sortStr.split("/");
					lbo.deleteProperty();
					int j = 0;
					for (int i = 0; i < arr.length; i++) {
						if (arr[i] == null || "".equals(arr[i]))
							continue;
						LazyDynaBean bean = (LazyDynaBean) map.get(arr[i]);

						if (bean != null) {
							j++;
							list.remove(arr[i]);
							String user_name = this.userView.getUserName();
							if (bean.get("user_name") != null)
								user_name = (String) bean.get("user_name");
							lbo.setProperty(i + "", (String) bean.get("Name"), (String) bean.get("Expr"), (String) bean.get("Factor"), user_name);
						}

					}
					//依次插入之前排序剩下的记录 zhanghua 2017-8-22
					Iterator iter = list.entrySet().iterator();
					while (iter.hasNext()) {
						Map.Entry entry = (Map.Entry) iter.next();
						LazyDynaBean bean = (LazyDynaBean) entry.getValue();
						if (bean != null) {

							String user_name = this.userView.getUserName();
							if (bean.get("user_name") != null)
								user_name = (String) bean.get("user_name");
							lbo.setProperty(j + "", (String) bean.get("Name"), (String) bean.get("Expr"), (String) bean.get("Factor"), user_name);
							j++;
						}

					}
					String xml = lbo.outPutContent();
					BankDiskSetBo bo = new BankDiskSetBo(this.getFrameconn());
					bo.updateLprogram(salaryid, xml);
				}
			}else{
				HistoryDataBo hbo=new HistoryDataBo(this.getFrameconn(),this.getUserView());
				hbo.sortPersionFilter(sortStr);
			}
    		this.getFormHM().put("salaryid",salaryid);

		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	


}
