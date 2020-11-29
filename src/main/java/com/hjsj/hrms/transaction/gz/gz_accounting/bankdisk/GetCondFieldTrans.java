package com.hjsj.hrms.transaction.gz.gz_accounting.bankdisk;

import com.hjsj.hrms.businessobject.gz.BankDiskSetBo;
import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryLProgramBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.businessobject.gz.gz_analyse.HistoryDataBo;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.StringUtils;

import java.util.*;

public class GetCondFieldTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			HashMap map =(HashMap)this.getFormHM().get("requestPamaHM");
			String model=this.getFormHM().get("model")!=null?(String)this.getFormHM().get("model"):"";
			String condid=(String)map.get("condid");
			BankDiskSetBo bdsb = new BankDiskSetBo(this.getFrameconn());
			String salaryid=(String)map.get("salaryid");
			ArrayList fieldlist=new ArrayList();
			ArrayList list=new ArrayList();
			String tablename = this.userView.getUserName() + "_salary_" + salaryid;
			ArrayList aList = new ArrayList();
			if(!"history".equalsIgnoreCase(model)) {//history 表示为薪资历史数据分析进入
				String xml = bdsb.getXml(salaryid);
				SalaryLProgramBo bo = new SalaryLProgramBo(xml);
				String field = bo.getServiceItemField(condid);
				fieldlist = bdsb.getCondFieldList(field, salaryid);
				list = bdsb.getAllItemList(salaryid);
				SalaryTemplateBo gzbo = new SalaryTemplateBo(this.getFrameconn(), Integer.parseInt(salaryid), this.userView);

				String a01z0Flag = gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.A01Z0, "flag");  // 是否显示停发标识  1：有
				for (int i = 0; i < list.size(); i++) {
					CommonData data = (CommonData) list.get(i);
					if ("a01z0".equalsIgnoreCase(data.getDataValue()) && (a01z0Flag == null || "0".equals(a01z0Flag)))
						continue;
					else {
						if (!"a00z0".equalsIgnoreCase(data.getDataValue()) && !"a00z1".equalsIgnoreCase(data.getDataValue()) && !"a00z2".equalsIgnoreCase(data.getDataValue()) && !"a00z3".equalsIgnoreCase(data.getDataValue())) {
							if (!(this.userView.isSuper_admin() || "1".equals(this.userView.getGroupId()))) {
								if ("0".equals(this.userView.analyseFieldPriv(data.getDataValue())))
									continue;
							}
						}
					}
					aList.add(data);
				}
				list = aList;
				if (gzbo.isApprove())
					list.add(new CommonData("SP_FLAG", ResourceFactory.getProperty("label.gz.sp")));
			}else{

				BankDiskSetBo bo = new BankDiskSetBo(this.getFrameconn());
				String [] salaryidList=salaryid.split(",");
				LinkedHashMap<String,CommonData> fieldItemMap=new LinkedHashMap<String, CommonData>();
				HistoryDataBo hbo=new HistoryDataBo(this.getFrameconn(),this.getUserView());
				ArrayList<String> field =hbo.getPersionFilterField(condid);

				for (String salary_id : salaryidList) {
					if(StringUtils.isBlank(salary_id)){
						continue;
					}
					list=bo.getAllItemList(salary_id);

					for (int i = 0; i < list.size(); i++) {
						CommonData fieldItem=(CommonData)list.get(i);

						if(!fieldItemMap.containsKey(fieldItem.getDataValue())){
							fieldItemMap.put(fieldItem.getDataValue(),fieldItem);
						}
					}
				}
				for (String s : field) {
					if(fieldItemMap.containsKey(s)){
						fieldlist.add(fieldItemMap.get(s));
					}
				}

				Iterator iterator=fieldItemMap.entrySet().iterator();

				list.clear();
				while (iterator.hasNext()){
					Map.Entry entry=(Map.Entry)iterator.next();
					list.add(entry.getValue());
				}

				for (int i = 0; i < list.size(); i++) {
					CommonData data = (CommonData) list.get(i);
					if ("a01z0".equalsIgnoreCase(data.getDataValue()))
						continue;
					else {
						if (!"a00z0".equalsIgnoreCase(data.getDataValue()) && !"a00z1".equalsIgnoreCase(data.getDataValue()) && !"a00z2".equalsIgnoreCase(data.getDataValue()) && !"a00z3".equalsIgnoreCase(data.getDataValue())) {
							if (!(this.userView.isSuper_admin() || "1".equals(this.userView.getGroupId()))) {
								if ("0".equals(this.userView.analyseFieldPriv(data.getDataValue())) && !"nbase".equalsIgnoreCase(data.getDataValue()))//放开人员库标识  zhaoxg update 2015-6-24
									continue;
							}
						}
					}
					aList.add(data);
				}
				list=aList;

				tablename="salaryarchive";
			}
		    this.getFormHM().put("selectedFieldList",fieldlist);
		    this.getFormHM().put("filterCondId",condid);
		    this.getFormHM().put("allList",list);
		   /* this.getFormHM().put("allList",list);
			this.getFormHM().put("selectedFieldList",selectedFieldList);*/
			this.getFormHM().put("salaryid",salaryid);
			this.getFormHM().put("tableName",tablename);
		    
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	

}
