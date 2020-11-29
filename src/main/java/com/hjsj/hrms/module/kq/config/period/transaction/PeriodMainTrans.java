package com.hjsj.hrms.module.kq.config.period.transaction;

import com.hjsj.hrms.module.kq.config.period.businessobject.PeriodService;
import com.hjsj.hrms.module.kq.config.period.businessobject.impl.PeriodServiceImpl;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.List;
/**
 * 考勤期间
 * 
 * @author haosl
 * 
 * 2018-9-25
 *
 */
public class PeriodMainTrans extends IBusiness {
	private static final long serialVersionUID = 1L;

	/**
	 * 参数说明：
	 * type:
	 * 	=list  获得期间列表信息
	 *  =create 新建考勤期间
	 *  =delete 删除考勤期间
	 *  		修改？
	 * 
	 */
	@Override
	public void execute() throws GeneralException {
		
		PeriodService service = new PeriodServiceImpl(this.userView,this.frameconn);
		
		String jsonStr = (String)this.formHM.get("jsonStr");
		//获得班次信息列表
		JSONObject jsonObj = JSONObject.fromObject(jsonStr);
		String type = jsonObj.getString("type");
		String returnStr = "";
		if("pclist".equals(type)) {
			String kqYear = (String) jsonObj.get("kq_year");
			if(StringUtils.isEmpty(kqYear)) {
				String config = service.getShiftsTableConfig();
				this.getFormHM().put("tableConfig", config);
				
				//获得考勤期间的年度
				List<LazyDynaBean> yearList = service.getYearList();
				this.getFormHM().put("yearList",yearList);
			}else {
				String datasql = service.getTableSql();
				TableDataConfigCache catche = (TableDataConfigCache)this.userView.getHm().get("period_list_subModuleId");
				catche.setTableSql(datasql+" where kq_year='"+kqYear+"'");
			}
		}else if("delete".equals(type)) {
			String kq_year = (String)jsonObj.get("kq_year");
			List<String> kq_durations = (List<String>)jsonObj.get("kq_durations");
			returnStr = service.deleteDurations(kq_year, kq_durations);
		}else if("checkHasPeriod".equals(type)) {  
			String kq_year = (String)jsonObj.get("kq_year");
			JSONObject obj = new JSONObject();
			String return_code = "success";
			String return_msg = "";
			boolean flag = false;
			try {
				flag = service.checkHasPrivPeriod(kq_year);
			}  catch (Exception e) {
				e.printStackTrace();
				return_code = "fail";
				return_msg = e.getMessage();
			}finally {
				obj.put("return_code", return_code);
				obj.put("return_msg", return_msg);
				obj.put("flag", flag);
			}
			returnStr = obj.toString();
		}else if("create".equals(type)) {
			returnStr = service.crteatePeriod(jsonObj);
		}
		this.getFormHM().put("returnStr", returnStr);
	}

}
