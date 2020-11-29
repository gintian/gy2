package com.hjsj.hrms.module.gz.mysalary.transaction;

import com.hjsj.hrms.module.gz.mysalary.businessobject.MySalaryService;
import com.hjsj.hrms.module.gz.mysalary.businessobject.impl.MySalaryServiceImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

/**
 * 新建视图表界面
 * @author Administrator
 *
 */
public class MySalaryViewTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		MySalaryService salaryService = new MySalaryServiceImpl(this.getFrameconn());
		MorphDynaBean jsonStr = (MorphDynaBean) this.getFormHM().get("jsonStr");
		HashMap paramHM = PubFunc.DynaBean2Map(jsonStr);
		String return_code = "success";
		HashMap returnData = null;
		String type = (String) paramHM.get("type");
		try {
			if("search".equalsIgnoreCase(type)){
				String view = (String) paramHM.get("view");
				returnData = salaryService.getSalaryViewParam(this.userView);
				if(StringUtils.isNotBlank(view)){
					returnData.putAll(salaryService.getViewData(view));
				}
			}else if("add".equalsIgnoreCase(type) || "update".equalsIgnoreCase(type)){// add 新增 update 修改视图
				String salary_table_name = (String) jsonStr.get("salary_table_name");
				String salary_table = (String) jsonStr.get("salary_table");
				String fieldsetid = (String) jsonStr.get("fieldsetid");
				String nbase = (String) jsonStr.get("nbase");
				MorphDynaBean items = (MorphDynaBean) jsonStr.get("items");
				HashMap map = new HashMap();
				map.put("salary_table_name", salary_table_name);
				map.put("salary_table", salary_table);
				map.put("fieldsetid", fieldsetid);
				map.put("nbase", nbase);
				map.put("items", items);
				returnData = salaryService.saveSalaryView(map,type,this.userView);
				
			}else if ("searchNumberFieldItem".equalsIgnoreCase(type)){
				// 子集id
				String fieldSetId = (String) jsonStr.get("fieldsetid");
				// 查询该子集下的数值型指标
				List list = salaryService.searchNumberFieldItem(fieldSetId,"search");
				returnData = new HashMap();
				returnData.put("numberFieldItem",list);
			}else if ("checkFormula".equalsIgnoreCase(type)){
				// 计算公式内容
				String c_expr = (String) jsonStr.get("c_expr");
				c_expr=c_expr!=null&&c_expr.trim().length()>0?c_expr:"";
				String itemType = (String) jsonStr.get("itemType");
				String fieldSetId = (String) jsonStr.get("fieldSetId");
				String info = salaryService.checkFormula(this.userView, c_expr, itemType,fieldSetId);
				returnData = new HashMap();
				returnData.put("info",info);
			}else if ("saveFormula".equalsIgnoreCase(type)){
				String c_expr = (String) jsonStr.get("c_expr");
				c_expr=c_expr!=null&&c_expr.trim().length()>0?c_expr:"";
				String itemId = (String) jsonStr.get("itemId");

			}
		} catch (GeneralException e) {
			return_code = "fail";
			e.printStackTrace();
			this.getFormHM().put("return_msg", e.getErrorDescription());
		} catch (SQLException exception){
			exception.printStackTrace();
		}
		this.getFormHM().put("return_data",returnData);
		this.getFormHM().put("return_code", return_code);

	}

}
