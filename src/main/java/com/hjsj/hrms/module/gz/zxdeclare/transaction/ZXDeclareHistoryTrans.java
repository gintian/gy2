package com.hjsj.hrms.module.gz.zxdeclare.transaction;


import com.hjsj.hrms.module.gz.zxdeclare.businessobject.IDeclareService;
import com.hjsj.hrms.module.gz.zxdeclare.businessobject.impl.DeclareServiceImpl;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class ZXDeclareHistoryTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		String type = (String) this.getFormHM().get("type");
		IDeclareService zxDeclareService = new DeclareServiceImpl(this.frameconn, this.userView);
		HashMap<String, Object> declareDataMap = new HashMap<String, Object>();
		String orderSql = "";
		String return_msg = "";
		StringBuffer whereSql = new StringBuffer();
		ArrayList<String> valueList = new ArrayList<String>();
		String return_code = "fail";
		if("main".equals(type)) { //加载全部

			whereSql.append("approve_state=? ");
			valueList.add(IDeclareService.C_APPROVE_STATE_FILED);
			orderSql = " order by create_date desc";
		}else if("search".equals(type)) { //条件检索

			String year = (String) this.getFormHM().get("year");
			String declare_type = (String) this.getFormHM().get("declare_type");
			boolean isLoadAll = (StringUtils.isBlank(year) && StringUtils.isBlank(declare_type)) || (StringUtils.isBlank(year) && "-1".equals(declare_type));
			whereSql.append("approve_state=? and ");
			if(!isLoadAll) {
				if(Sql_switcher.searchDbServer() == 1)
					whereSql.append("year(create_date)=? ");
				else if(Sql_switcher.searchDbServer() == 2)
					whereSql.append("to_char(create_date,'yyyy')=? ");
			}else {
				whereSql.setLength(whereSql.length()-4);
			}
			valueList.add(IDeclareService.C_APPROVE_STATE_FILED);
			if(!isLoadAll)
				valueList.add(year);
			if(!"-1".equals(declare_type)) { //全部则不限制类型
				whereSql.append("and declare_type=?");
				valueList.add(declare_type);
			}
			if(!isLoadAll)
				orderSql = " order by declare_type";
			else
				orderSql = " order by create_date desc";
		}
		ArrayList<HashMap> declareList = zxDeclareService.listZXDeclare(whereSql.toString(), valueList, orderSql, this.userView);

//		for(HashMap itemMap : declareList) {
//			float money = 0;
//			float deductMoney = Float.parseFloat(String.valueOf(itemMap.get("deduct_money")));
//			/** 01 06没有结束日期, 单子一直在跑不会出现在历史纪录  **/
//			String[] startInfo = ((String) itemMap.get("start_date")).split("-");
//			String[] endInfo = ((String) itemMap.get("end_date")).split("-");
//			int startYear = Integer.parseInt(startInfo[0]);
//			int endYear = Integer.parseInt(endInfo[0]);
//			//获取申报年度的年金额
//			if(startYear == endYear)
//				if(Integer.parseInt(startInfo[1]) <= Integer.parseInt(endInfo[1]))
//					money = deductMoney * (Integer.parseInt(endInfo[1]) - Integer.parseInt(startInfo[1]) + 1);
//			else if(startYear < endYear) //总月数  * 月金额
//				money = deductMoney * ((12 - Integer.parseInt(startInfo[1]) + 1) + (12 * (endYear - startYear - 1)) + (Integer.parseInt(endInfo[1])));
//			itemMap.put("money", money);
//		}
		HashMap<String, Object> returnData = new HashMap<String, Object>();
		if(declareList != null) {
			return_code = "success";
			returnData.put("declare_items", declareList);
		}else {
			return_msg = ResourceFactory.getProperty("gz.zxdeclare.error.listDeclareMsg");
		}
		if(declareList.size() != 0) {
			String endDate = (String) declareList.get(0).get("create_date");
			int endyear = Integer.parseInt(endDate.substring(0, 4));
			String startDate = (String) declareList.get(declareList.size()-1).get("create_date");
			int startyear = Integer.parseInt(startDate.substring(0, 4));
			ArrayList<Integer> yearList = new ArrayList<Integer>();
			for(int n = endyear; n >= startyear; n--) {
				yearList.add(n);
			}
			returnData.put("year_list", yearList);
		}
		declareDataMap.put("return_code", return_code);
		declareDataMap.put("return_msg", return_msg);
		declareDataMap.put("return_data", returnData);

		this.getFormHM().put("returnStr", declareDataMap);
	}

}
