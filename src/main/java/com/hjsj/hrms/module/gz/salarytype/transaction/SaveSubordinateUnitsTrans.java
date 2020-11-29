package com.hjsj.hrms.module.gz.salarytype.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
/**
 * 
 * <p>Title:SaveSubordinateUnitsTrans.java</p>
 * <p>Description>:保存归属单位</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Dec 14, 2016 5:44:52 PM</p>
 * <p>@version: 7.0</p>
 * <p>@author:zhaoxg</p>
 */
public class SaveSubordinateUnitsTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		try
		{
			String salaryid=(String)this.getFormHM().get("salaryid");
			salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
			String workunits = (String) this.getFormHM().get("workunits");
			workunits = PubFunc.decrypt(SafeCode.decode(workunits));
			String subordinateunits=(String)this.getFormHM().get("subordinateunits");
			ContentDAO dao=new ContentDAO(this.frameconn);
			String sql = "update salarytemplate set b0110=? where salaryid=?";
			ArrayList list = new ArrayList();
			list.add(workunits);
			list.add(salaryid);


			dao.update(sql, list);

			TableDataConfigCache cache = (TableDataConfigCache)userView.getHm().get("salaryType");
			if(cache.getTableData().size()>0){
				ArrayList<LazyDynaBean> dataList=cache.getTableData();
				for(LazyDynaBean bean:dataList){
					if(bean.get("salaryid").toString().equalsIgnoreCase(salaryid)){
						bean.set("subordinateunits",subordinateunits);
						bean.set("workunits",StringUtils.isBlank(workunits)?"":PubFunc.encrypt("UN"+workunits));
						break;
					}
				}
			}
			//选人控件右侧显示已选人员，这里需要加上UN进行加密
			this.getFormHM().put("workunits", StringUtils.isBlank(workunits)?"":PubFunc.encrypt("UN"+workunits));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
