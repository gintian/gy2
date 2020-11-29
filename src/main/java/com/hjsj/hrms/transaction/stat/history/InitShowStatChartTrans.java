/*
 * Created on 2005-6-10
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.stat.history;

import com.hjsj.hrms.businessobject.stat.StatDataEncapsulation;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author Administrator
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class InitShowStatChartTrans extends IBusiness {

	/*
	 * ( non-Javadoc)
	 * 
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub

		float[] statvalues=null;
		String[] fieldDisplay;
		String SNameDisplay="";
		String chart_type= (String)this.getFormHM().get("chart_type");
		String userbase = (String) this.getFormHM().get("userbase");
		String statId = "0";//(String) this.getFormHM().get("statid");
		String querycond = (String) this.getFormHM().get("querycond");// 组织机构
		String infokind = (String) this.getFormHM().get("infokind");
		String backdates = (String) this.getFormHM().get("backdates");
		String allbackdates = (String)this.getFormHM().get("allbackdates");
		String[] tmp =allbackdates.split(",");
		ArrayList backdateslist = new ArrayList();
		for(int i=0;i<tmp.length;i++){
			CommonData data = new CommonData(tmp[i],tmp[i]);
			backdateslist.add(data);
		}
		this.getFormHM().put("backdateslist", backdateslist);
		StringBuffer sql1 = new StringBuffer();
		sql1.append("select id from hr_hisdata_sname order by snorder");
		List rs1 = ExecuteSQL.executeMyQuery(sql1.toString());
		if (!rs1.isEmpty()) {
			LazyDynaBean rec = (LazyDynaBean) rs1.get(0);
			statId = (String) rec.get("id");
		}

		if("29".equals(chart_type)||"31".equals(chart_type)){
			StatDataEncapsulation simplestat = new StatDataEncapsulation();
			LinkedHashMap jfreemap = new LinkedHashMap();
			ArrayList datalist = new ArrayList();
			try {
				tmp = backdates.split("&");
				int size = tmp.length;
				
				for (int n = 0; n < size; n++) {
					ArrayList list = new ArrayList();
					String backdate = tmp[n];
					if(backdate.length()<10)
						continue;
					statvalues = simplestat.getLexprData(userbase, Integer
							.parseInt(statId), querycond, userView.getUserName(),
							userView.getManagePrivCode(), userView, infokind,backdate);
					SNameDisplay = simplestat.getSNameDisplay();
					if (statvalues != null && statvalues.length > 0) {
						fieldDisplay = simplestat.getDisplay();
						int statTotal = 0;
						for (int i = 0; i < statvalues.length; i++) {
							CommonData vo = new CommonData();
							String str = fieldDisplay[i];
							vo.setDataName(str);
							vo.setDataValue(String.valueOf(statvalues[i]));
							list.add(vo);
							statTotal += statvalues[i];
						}
						
					} else {
						StringBuffer sql = new StringBuffer();
						sql.append("select * from hr_hisdata_sname where id=");
						sql.append(statId);
						List rs = ExecuteSQL.executeMyQuery(sql.toString());
						if (!rs.isEmpty()) {
							LazyDynaBean rec = (LazyDynaBean) rs.get(0);
							SNameDisplay = rec.get("name") != null ? rec
									.get("name").toString() : "";
						}
						CommonData vo = new CommonData();
						vo.setDataName("");
						vo.setDataValue("0");
						list.add(vo);
					}
					
					jfreemap.put(backdate, list);
				}
				this.getFormHM().put("snamedisplay", SNameDisplay);
				
				if(statvalues!=null){
					for(int n=0;n<statvalues.length;n++){
						ArrayList dataList = new ArrayList();
						String categoryName = "";
						for(Iterator i = jfreemap.keySet().iterator();i.hasNext();){
							String key = (String)i.next();
							ArrayList l = (ArrayList)jfreemap.get(key);
							CommonData c = (CommonData)l.get(n);
							CommonData tc = new CommonData(c.getDataValue(),key);
							categoryName = c.getDataName();
							dataList.add(tc);
						}
						LazyDynaBean abean=new LazyDynaBean();
						 abean.set("categoryName", categoryName);
						 abean.set("dataList",dataList);
						 datalist.add(abean);
					} 
				}else{
					StringBuffer sql = new StringBuffer();
					sql.append("select * from hr_hisdata_sname where id=");
					sql.append(statId);
					List rs = ExecuteSQL.executeMyQuery(sql.toString());
					if (!rs.isEmpty()) {
						LazyDynaBean rec = (LazyDynaBean) rs.get(0);
						SNameDisplay = rec.get("name") != null ? rec
								.get("name").toString() : "";
					}
					CommonData vo = new CommonData();
					vo.setDataName("");
					vo.setDataValue("0");
					datalist.add(vo);
				}
				this.getFormHM().put("list", datalist);
				this.getFormHM().put("jfreemap", jfreemap);
			
			} catch (Exception e) {
				e.printStackTrace();
				throw new GeneralException("", e.toString(), "", "");
			}
		}else{
		
			StatDataEncapsulation simplestat = new StatDataEncapsulation();
			LinkedHashMap jfreemap = new LinkedHashMap();
			try {
				tmp = backdates.split("&");
				int size = tmp.length;
				for (int n = 0; n < size; n++) {
					String backdate = tmp[n];
					if(backdate.length()<10)
						continue;
					ArrayList list = new ArrayList();
					statvalues = simplestat.getLexprData(userbase, Integer
							.parseInt(statId), querycond, userView.getUserName(),
							userView.getManagePrivCode(), userView, infokind,backdate);
					SNameDisplay = simplestat.getSNameDisplay();
					if (statvalues != null && statvalues.length > 0) {
						fieldDisplay = simplestat.getDisplay();
						int statTotal = 0;
						for (int i = 0; i < statvalues.length; i++) {
							CommonData vo = new CommonData();
							String str = fieldDisplay[i];
							vo.setDataName(str);
							vo.setDataValue(String.valueOf(statvalues[i]));
							list.add(vo);
							statTotal += statvalues[i];
						}
						this.getFormHM().put("snamedisplay", SNameDisplay);
						this.getFormHM().put("list", list);
					} else {
						StringBuffer sql = new StringBuffer();
						sql.append("select * from hr_hisdata_sname where id=");
						sql.append(statId);
						List rs = ExecuteSQL.executeMyQuery(sql.toString());
						if (!rs.isEmpty()) {
							LazyDynaBean rec = (LazyDynaBean) rs.get(0);
							SNameDisplay = rec.get("name") != null ? rec
									.get("name").toString() : "";
						}
						CommonData vo = new CommonData();
						vo.setDataName("");
						vo.setDataValue("0");
						list.add(vo);
						this.getFormHM().put("snamedisplay", SNameDisplay);
						this.getFormHM().put("list", list);
					}
					
					jfreemap.put(backdate, list);
				}
				
				
				this.getFormHM().put("jfreemap", jfreemap);
			} catch (Exception e) {
				e.printStackTrace();
				throw new GeneralException("", e.toString(), "", "");
			}
		}
		this.getFormHM().put("statid", statId);//田野添加 初始化statid为数据库中的第一条数据修改HistoryStatForm初始化statid="1"的错误
		
	}
}
