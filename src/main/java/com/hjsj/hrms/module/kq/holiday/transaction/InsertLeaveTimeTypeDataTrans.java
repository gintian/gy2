package com.hjsj.hrms.module.kq.holiday.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

public class InsertLeaveTimeTypeDataTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		try {
			ArrayList<String> itemList = (ArrayList<String>) this.getFormHM().get("itemList");
			ArrayList<MorphDynaBean> mapsList = (ArrayList<MorphDynaBean>) this.getFormHM().get("mapsList");
			
			if(itemList.isEmpty())
				return;
			
			StringBuffer sql = new StringBuffer();
			StringBuffer valueStr = new StringBuffer("?,?");
			// 34666 新增记录SQL insert 缺少into
			sql.append("insert into q33 (q3301,a0100");
			for(int i = 0; i < itemList.size(); i++) {
				String itemid = itemList.get(i);
				if("jobnumber".equalsIgnoreCase(itemid) || "primarykey".equalsIgnoreCase(itemid))
					continue;
				
				sql.append("," + itemid);
				valueStr.append(",?");
			}
			
			sql.append(") values (");
			sql.append(valueStr);
			sql.append(")");
			
			ArrayList<ArrayList<Object>> valuesList = new ArrayList<ArrayList<Object>>();
			for(int i = 0; i < mapsList.size(); i++) {
				MorphDynaBean bean = mapsList.get(i);
				// 39042 MorphDynaBean对象在获取键值为null的时候报错 先转化为map对象操作数据
				HashMap formMap = PubFunc.DynaBean2Map(bean);
				if(bean == null)
					continue;
				
				ArrayList<Object> valueList = new ArrayList<Object>();
				IDGenerator idg = new IDGenerator(2, this.getFrameconn());
				String q3301 = idg.getId("Q33.Q3301");
				valueList.add(q3301);
				valueList.add((String) bean.get("a0100"));
				
				for(int m = 0; m < itemList.size(); m++) {
					String itemid = itemList.get(m);
					if("jobnumber".equalsIgnoreCase(itemid) || "primarykey".equalsIgnoreCase(itemid))
						continue;

					String value = formMap.containsKey(itemid) ? (String) formMap.get(itemid) : "";
					if(StringUtils.isEmpty(value) || "```".equalsIgnoreCase(value))
						value = "";
					
					FieldItem fi = DataDictionary.getFieldItem(itemid, "Q33");
					if(fi != null) {
						if("D".equalsIgnoreCase(fi.getItemtype())) {
							String format = "yyyy-MM-dd";
							int length = value.length();
							if(length == 4)
								format = "yyyy";
							else if(length == 7)
								format = "yyyy-MM";
							else if(length == 13)
								format = "yyyy-MM-dd hh";
							else if(length == 16)
								format = "yyyy-MM-dd hh:mm";
							else if(length == 19)
								format = "yyyy-MM-dd hh:mm:ss";
							
							valueList.add(new Timestamp(DateUtils.getDate(value, format).getTime()));
						} else if("N".equalsIgnoreCase(fi.getItemtype())) {
							if(StringUtils.isEmpty(value))
								value = "0";
							int decwidth = fi.getDecimalwidth();
							double dValue = Double.parseDouble(value);
							dValue = new BigDecimal(dValue).setScale(decwidth, BigDecimal.ROUND_HALF_UP).doubleValue();
							value = Double.toString(dValue);
							value = PubFunc.round(value, decwidth);
							valueList.add(value);
						}
						else 
							valueList.add(value);
					}
				}
				
				valuesList.add(valueList);
			}
			
			ContentDAO dao = new ContentDAO(this.frameconn);
			dao.batchInsert(sql.toString(), valuesList);
			// 34877 批量插入成功后，oracle库不知为何返回-2，现改为如果一条插入失败就都不会插入；如果成功则就是全部list集合的条数，不需读执行语句后的返回数据
			this.getFormHM().put("count", valuesList.size());
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
