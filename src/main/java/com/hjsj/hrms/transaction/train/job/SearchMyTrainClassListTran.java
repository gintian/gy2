package com.hjsj.hrms.transaction.train.job;

import com.hjsj.hrms.businessobject.train.TrainClassBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.Calendar;

public class SearchMyTrainClassListTran extends IBusiness {

	public void execute() throws GeneralException {
		try {
			String items = SystemConfig.getPropertyValue("train_self_class_items");
			String ayrar = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
			String year = (String) this.getFormHM().get("year");
			String classname = (String) this.getFormHM().get("classname");
			classname = SafeCode.decode(classname);
			
			year = year == null || "".equalsIgnoreCase(year) ? ayrar : year;

			if (this.userView.getA0100() == null || userView.getA0100().trim().length() < 1)
				throw new GeneralException(ResourceFactory.getProperty("selfservice.module.pri"));
			
			String wherestr = this.getwhere(year, classname);
			
			StringBuffer column = new StringBuffer();
			ArrayList list = new ArrayList();
			ArrayList fieldlistr31 = DataDictionary.getFieldList("r31", 1);
			
			FieldItem fielditem = DataDictionary.getFieldItem("r3101");
			list.add(fielditem);
			fielditem = DataDictionary.getFieldItem("r3130");
			list.add(fielditem);
			column.append("r3101,r3130,");
			
			if(items!=null&&items.length()>0){
				items = items.toLowerCase();
				String[] item = items.split(",");
				for(int i=0;i<item.length;i++){
					if("r3101".equalsIgnoreCase(item[i])|| "r3130".equalsIgnoreCase(item[i])
						|| "r4013".equalsIgnoreCase(item[i])|| "r4015".equalsIgnoreCase(item[i]))
						continue;
					for(int j=0;j<fieldlistr31.size();j++){
						FieldItem fi = (FieldItem)fieldlistr31.get(j);
						if(item[i].equals(fi.getItemid()) && "1".equalsIgnoreCase(fi.getState())){
							list.add(fi);
							column.append(fi.getItemid()+",");
						}
					} 
				}
			
			}else{
				fielditem = DataDictionary.getFieldItem("r4008");
				list.add(fielditem);
				fielditem = DataDictionary.getFieldItem("r4010");
				list.add(fielditem);
				column.append("r4008,r4010,");
			}
			fielditem = DataDictionary.getFieldItem("R4009");
			list.add(fielditem);
			fielditem = DataDictionary.getFieldItem("r4015");
			list.add(fielditem);
			column.append("r4009,r4015");
			
			String columns=column.toString().replace("b0110", "r31.b0110").replace("e0122", "r31.e0122");
			
			StringBuffer buf = new StringBuffer();
			buf.append("select "+columns+" from r40,r31");
			buf.append(" where r40.r4005=r31.r3101 and r4013<>'01' and r4013<>'02' and r4013<>'08' and r4013<>'07'"+wherestr);
			buf.append(" and r4001='");
			buf.append(this.userView.getA0100());
			buf.append("' and upper(nbase)='");
			buf.append(this.userView.getDbname().toUpperCase());
			buf.append("'");
			String orderBy = "order by r40.r4006 desc";
			TrainClassBo bo = new TrainClassBo(this.getFrameconn());

			ArrayList yearList = bo.getYearList();
			this.getFormHM().put("sql", buf.toString());
			this.getFormHM().put("columns", column.toString());
			this.getFormHM().put("yearList", yearList);
			this.getFormHM().put("year", year);
			this.getFormHM().put("list", list);
			this.getFormHM().put("orderBy", orderBy);
			this.getFormHM().put("classname", classname);

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
    /**
     * 获取查询条件
     * @param year
     * @param classname
     * @return
     */
	public String getwhere(String year, String classname) {
		StringBuffer wherestr = new StringBuffer();
		if ((year != null && year.length() > 0) && (!year.equalsIgnoreCase(ResourceFactory.getProperty("train.job.all.year"))))
			wherestr.append(" AND " + Sql_switcher.year("R31.R3115") + "=" + year);
		if (classname != null && classname.length() > 0)
			wherestr.append(" AND R3130 LIKE '%" + classname + "%'");
		return wherestr.toString();
	}
}
