package com.hjsj.hrms.module.gz.analysistables.analysisdata.businessobject.impl;

import com.hjsj.hrms.module.gz.analysistables.analysisdata.businessobject.ItemGroupMusterService;
import com.hjsj.hrms.module.gz.analysistables.util.GzAnalysisUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class ItemGroupMusterServiceImpl implements ItemGroupMusterService {
	
	Connection conn;

    public ItemGroupMusterServiceImpl(Connection conn) {
        this.conn = conn;
    }

	@Override
    public ArrayList getRecordList(HashMap paramMap) {
		ArrayList list = new ArrayList();
		RowSet rs = null;
		try {
			String rsid = (String)paramMap.get("rsid");
			String rsdtlid = (String)paramMap.get("rsdtlid");
			String nbase = (String)paramMap.get("nbase");
			String salaryid = (String)paramMap.get("salaryid");
			String codeitem= (String)paramMap.get("codeitem");
			String codevalue= (String)paramMap.get("codevalue");
			UserView userView = (UserView)paramMap.get("userView"); 
			String tableName = (String)paramMap.get("tableName");
			String year = (String)paramMap.get("year");
			GzAnalysisUtil gzAnalysisUtil  = new GzAnalysisUtil(conn, userView);
			String incloudLowLevel = (String)paramMap.get("incloudLowLevel");
			String group = (String)paramMap.get("group");//是否分类
			String showMx = (String)paramMap.get("showMx");//是否显示每月数据
			String accumulate = (String)paramMap.get("accumulate");//显示每月汇总
			String verifying = (String)paramMap.get("verifying");//是否包含审批过程数据
			int maxnum = 14;
			ContentDAO dao = new ContentDAO(this.conn);
			ArrayList headList = this.getTableHeadlist(rsdtlid, rsid, "","",userView);
			if(headList.size()==0){
				return list;
			}
			StringBuffer buf_sum = new StringBuffer("");
			StringBuffer buf_nvl = new StringBuffer("");
			StringBuffer sql = new StringBuffer();
			StringBuffer sql_all = new StringBuffer();
			String dbSql = "";
			String salarySql = "";
			String b_units=userView.getUnitIdByBusiOutofPriv("1");// 1:工资发放  2:工资总额  3:所得税
			String privSql=gzAnalysisUtil.getPrivSQL(tableName,nbase,salaryid,b_units,"");
			for (int i = 0; i < headList.size(); i++) {
				LazyDynaBean bean = (LazyDynaBean) headList.get(i);
				String itemtype = (String) bean.get("itemtype");
				if("N".equalsIgnoreCase(itemtype)) {
					buf_sum.append(" sum("+(String) bean.get("itemid")+") as ");
					buf_sum.append((String) bean.get("itemid"));
					buf_sum.append(",");
					buf_nvl.append(" "+Sql_switcher.isnull((String) bean.get("itemid"), "0")+" as ");
					buf_nvl.append((String) bean.get("itemid"));
					buf_nvl.append(",");
				}else {
					buf_sum.append(" max("+(String) bean.get("itemid")+") as ");
					buf_sum.append((String) bean.get("itemid"));
					buf_sum.append(",");
					buf_nvl.append(" "+Sql_switcher.isnull((String) bean.get("itemid"), "0")+" as ");
					buf_nvl.append((String) bean.get("itemid"));
					buf_nvl.append(",");
				}
			}
			if(buf_sum.toString().length()>0)
				buf_sum.setLength(buf_sum.length() - 1);
			if(buf_nvl.toString().length()>0)
				buf_nvl.setLength(buf_nvl.length() - 1);
			dbSql = gzAnalysisUtil.getDbSQL(nbase,salaryid,tableName,verifying);
			salarySql = gzAnalysisUtil.getSalarysetSQL(salaryid);
			sql.append("select nbase,a0100,");
			sql.append(buf_nvl.toString());
			if(buf_nvl.toString().length()>0)
	    		sql.append(",");
			sql.append(Sql_switcher.month("A00Z0"));
			sql.append(" as aMonth ");
			sql.append(" from "+tableName+" where 1=1 ");
			if ("1".equals(group)) {
				if("-1".equals(codevalue))
				{
					if(StringUtils.isNotBlank(codeitem)) {
						sql.append(" and ");
						sql.append("("+codeitem+" is null or "+codeitem+"='') ");
					}
				}else
				{
					sql.append(" and ");
					if("1".equals(incloudLowLevel)){//是否包含下级单位
						sql.append(codeitem + " like '");
						sql.append(codevalue + "%' ");
					}else if("0".equals(incloudLowLevel)){
						sql.append(codeitem + " = '");
						sql.append(codevalue + "' ");
					}
				}
				if(privSql!=null&&!"".equals(privSql))
				{
			    	sql.append(privSql);
			    	sql.append(" ");
				}
			}else
			{
				if(privSql!=null&&!"".equals(privSql))
				{
    	    		sql.append(privSql);
    	    		sql.append(" ");
				}
			}
			if(dbSql!=null&&!"".equals(dbSql)){
				sql.append(dbSql);					
			}else{
				sql.append(" ");
			}
			sql.append(" and ");
			sql.append(Sql_switcher.year("A00Z0"));
			sql.append("=" + year+" and ");
			sql.append(salarySql);
			String temp=sql.toString().replaceAll("salaryhistory","salaryarchive");
            sql.append(" union all "+temp);
            sql_all.append("select ");
            sql_all.append(" count(distinct nbase"+Sql_switcher.concat()+"A0100) as a0100,");
            sql_all.append(buf_sum+",aMonth from ("+sql);
            sql_all.append(") a ");
            sql_all.append(" group by aMonth order by aMonth");
			ArrayList recordlist = new ArrayList();
			rs = dao.search(sql_all.toString());
			while (rs.next()) {
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("amonth", gzAnalysisUtil.getUpperMonth(rs.getInt("amonth")));
				bean.set("personnum", rs.getString("a0100"));
				bean.set("amonth_n", rs.getInt("amonth")+"");
				for (int j = 0; j < headList.size(); j++) {
					LazyDynaBean abean = (LazyDynaBean) headList.get(j);
					String itemid = (String) abean.get("itemid");
					bean.set(itemid.toLowerCase(), rs.getString((String) abean.get("itemid")) == null ? "": rs.getString((String) abean.get("itemid")));
				}
				recordlist.add(bean);
				list.add(bean);
			}
			
			//添加人数列
			LazyDynaBean bean_person = new LazyDynaBean();
			bean_person.set("itemid", "personnum");
			bean_person.set("itemdesc", ResourceFactory.getProperty("menu.gz.personnum"));
			bean_person.set("align", "left");
			bean_person.set("nwidth", "50");
			bean_person.set("itemtype", "N");
			bean_person.set("codesetid", "0");
			bean_person.set("itemfmt", "0");
			headList.add(bean_person);
			for(int m=1;m<maxnum;m++) {
				LazyDynaBean bean = new LazyDynaBean();
				boolean isHave = false;
				for(int i=0;i<recordlist.size();i++) {
					LazyDynaBean bean_ = (LazyDynaBean) recordlist.get(i);
					double amonth_n = Double.parseDouble((String)bean_.get("amonth_n"));
					if(m==amonth_n) {
						isHave = true;
						break;
					}
				}
				
				if(!isHave) {
					if(m==maxnum-1) {
						String amonthdesc = "";
						if(m==maxnum-1)
							amonthdesc = ResourceFactory.getProperty("gz.gz_acounting.total");
						bean.set("amonth", amonthdesc);
						bean.set("amonth_n", m+"");
						for (int j = 0; j < headList.size(); j++) {
							LazyDynaBean abean = (LazyDynaBean) headList.get(j);
							String itemtype = (String) abean.get("itemtype");
							String itemid = (String) abean.get("itemid");
							String total_ = "";
							if("N".equalsIgnoreCase(itemtype)) {
								double sum = 0;
								double total = 0;
								if(recordlist.size()>0) {
									for(int n=0;n<recordlist.size();n++) {
										LazyDynaBean bean1 = (LazyDynaBean) recordlist.get(n);
										double value = Double.parseDouble((String)bean1.get(itemid.toLowerCase()));
										sum = sum + value;
									}
									total = sum;
									if("personnum".equals(itemid)) {
										int b = (int)total;
										if(m==maxnum-1)
											b = b/recordlist.size();
										total_ = String.valueOf(b);
									}else {
										total_ = String.valueOf(total);
									}
								}else {
									total_ = "";
								}
							}
							if(m==maxnum-1){
								bean.set(itemid.toLowerCase(), total_);
							}
						}
					}else {
						bean.set("amonth", gzAnalysisUtil.getUpperMonth(m));
						bean.set("amonth_n", m+"");
						for (int j = 0; j < headList.size(); j++) {
							LazyDynaBean abean = (LazyDynaBean) headList.get(j);
							String itemid = (String) abean.get("itemid");
							bean.set(itemid.toLowerCase(),"");
						}
					}
					list.add(m-1,bean);
				}
			}
			//添加统计行
			//添加每月累计
			if("1".equals(accumulate)) {
				this.getMonthlyBean(list, headList);
			}
			this.getQuarterBean(list, headList,accumulate);
			if("0".equals(showMx)) {//不显示每月数据
				//将每月数据去除
				ArrayList list_noshow = new ArrayList();
				for (int i = 0; i < list.size(); i++) {
					LazyDynaBean bean = (LazyDynaBean) list.get(i);
					String amonth = (String) bean.get("amonth");
					if (amonth.indexOf(ResourceFactory.getProperty("gz.analysistable.quarter")) != -1
							|| amonth.indexOf(ResourceFactory.getProperty("gz.gz_acounting.total")) != -1
							|| amonth.indexOf(ResourceFactory.getProperty("gz.analysistable.toatlCount"))!=-1) {
						list_noshow.add(bean);
					}
		        }
				list = list_noshow;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return list;
	}
	/**
	 * 得到每月累计
	 * @param list
	 * @param headList
	 */
	private void getMonthlyBean(ArrayList list, ArrayList headList) {
		for(int i=0;i<12;i++){
			String monthdesc = getMonthLyDesc(i+1);
			LazyDynaBean bean = new LazyDynaBean();
			bean.set("amonth", monthdesc);
			boolean isHave = false;
			int m1 = i==0?0:2*i-1;
			int m2 = 2*i+1;
			for (int j = 0; j < headList.size(); j++) {
				LazyDynaBean abean = (LazyDynaBean) headList.get(j);
				String itemid = (String) abean.get("itemid");
				String itemtype = (String) abean.get("itemtype");
				String total_ = "";
				if("N".equalsIgnoreCase(itemtype)) {
					double sum = 0;
					double total = 0;
					if(list.size()>0) {
						int recordnum = 0;
						for(int n=m1;n<m2;n++) {
							LazyDynaBean bean1 = (LazyDynaBean) list.get(n);
							String itemvalue = (String)bean1.get(itemid.toLowerCase());
							double value = 0.0;
							if(!"".equals(itemvalue)) {
								value = Double.parseDouble((String)bean1.get(itemid.toLowerCase()));
								isHave = true;
								recordnum++;
							}
							sum = sum + value;
						}
						total = sum;
						if(total>=0&&isHave) {
							if("personnum".equals(itemid)) {
								int b = (int)total/recordnum;
								total_ = String.valueOf(b);
							}else {
								total_ = String.valueOf(total);
							}
						}
					}else {
						total_ = "";
					}
				}
				bean.set(itemid.toLowerCase(), total_);
			}
			list.add(m2,bean);
		}
	}
	private String getMonthLyDesc(int month) {
		String monthdesc = "";
		switch (month) {
			case 1: {
				monthdesc = ResourceFactory.getProperty("date.month.january")+ResourceFactory.getProperty("gz.analysistable.toatlCount");
				break;
			}
			case 2: {
				monthdesc = ResourceFactory.getProperty("date.month.february")+ResourceFactory.getProperty("gz.analysistable.toatlCount");
				break;
			}
			case 3: {
				monthdesc = ResourceFactory.getProperty("date.month.march")+ResourceFactory.getProperty("gz.analysistable.toatlCount");
				break;
			}
			case 4: {
				monthdesc = ResourceFactory.getProperty("date.month.april")+ResourceFactory.getProperty("gz.analysistable.toatlCount");
				break;
			}
			case 5: {
				monthdesc = ResourceFactory.getProperty("date.month.may")+ResourceFactory.getProperty("gz.analysistable.toatlCount");
				break;
			}
			case 6: {
				monthdesc = ResourceFactory.getProperty("date.month.june")+ResourceFactory.getProperty("gz.analysistable.toatlCount");
				break;
			}
			case 7: {
				monthdesc = ResourceFactory.getProperty("date.month.july")+ResourceFactory.getProperty("gz.analysistable.toatlCount");
				break;
			}
			case 8: {
				monthdesc = ResourceFactory.getProperty("date.month.auguest")+ResourceFactory.getProperty("gz.analysistable.toatlCount");
				break;
			}
			case 9: {
				monthdesc = ResourceFactory.getProperty("date.month.september")+ResourceFactory.getProperty("gz.analysistable.toatlCount");
				break;
			}
			case 10: {
				monthdesc = ResourceFactory.getProperty("date.month.october")+ResourceFactory.getProperty("gz.analysistable.toatlCount");
				break;
			}
			case 11: {
				monthdesc = ResourceFactory.getProperty("date.month.november")+ResourceFactory.getProperty("gz.analysistable.toatlCount");
				break;
			}
			case 12: {
				monthdesc = ResourceFactory.getProperty("date.month.december")+ResourceFactory.getProperty("gz.analysistable.toatlCount");
				break;
			}
		}
		return monthdesc;
	}
	@Override
    public ArrayList getEchartDataList(ArrayList recordList, ArrayList headList) {
		ArrayList list = new ArrayList();
		//合计行数据
		if(recordList.size()>0) {
			LazyDynaBean recordBean = (LazyDynaBean) recordList.get(recordList.size()-1);
			for (int i = 0; i < headList.size(); i++) {
				HashMap map = new HashMap();
				LazyDynaBean bean = (LazyDynaBean) headList.get(i);
				String itemtype = (String)bean.get("itemtype");
				String itemid = (String)bean.get("itemid");
				String itemdesc = (String)bean.get("itemdesc");
				String itemfmt = (String)bean.get("itemfmt");//0.00
				if(!"N".equalsIgnoreCase(itemtype)) {
					continue;
				}
				int length = 0;
				if(StringUtils.isNotBlank(itemfmt)) {
					if(itemfmt.indexOf(".")!=-1)
						length = itemfmt.split("\\.")[1].length();
				}
				String datavalue = (String)recordBean.get(itemid.toLowerCase());
				if("".equals(datavalue)) {
					datavalue = "0.00";
				}
				Double value = Double.parseDouble(datavalue);
				if(value!=0) {
		            String pattern = "###";
		            if (length > 0)
		                pattern += ".";
		            for (int ia = 0; ia < length; ia++)
		                pattern += "0";
		            datavalue = new DecimalFormat(pattern).format(value);
				}
	            map.put("datavalue", datavalue);
	            map.put("dataname", itemdesc);
				list.add(map);
			}
		}
		return list;
	}

	@Override
    public ArrayList<ColumnsInfo> getItemGroupMusterColumnsInfo(UserView userView, String rsid, String rsdtlid) {
		ArrayList<ColumnsInfo> list = new ArrayList<ColumnsInfo>();
		try
		{
			FieldItem item = new FieldItem();
			item.setItemid("amonth");
			item.setItemdesc("");
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(50);
			item.setCodesetid("0");
			ColumnsInfo info = new ColumnsInfo(item);
			info.setEditableValidFunc("false");
			info.setColumnWidth(75);
			info.setLocked(true);
			list.add(info);
			
			item.setItemid("personnum");
			item.setItemdesc(ResourceFactory.getProperty("menu.gz.personnum"));
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(50);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setEditableValidFunc("false");
			info.setColumnWidth(50);
			info.setTextAlign("right");
			list.add(info);
			
			ArrayList headList = this.getTableHeadlist(rsdtlid, rsid, "","",userView);
			for (int i = 0; i < headList.size(); i++) {
				LazyDynaBean bean = (LazyDynaBean) headList.get(i);
				String itemid = (String)bean.get("itemid");
				String itemdesc = (String)bean.get("itemdesc");
				String itemtype = (String)bean.get("itemtype");
				String codesetid = (String)bean.get("codesetid");
				String itemfmt = (String)bean.get("itemfmt");//0.00
				FieldItem item_ = new FieldItem();
				item_.setItemid(itemid);
				item_.setItemdesc(itemdesc);
				item_.setItemtype(itemtype);
				item_.setReadonly(true);
				item_.setItemlength(50);
				item_.setCodesetid(codesetid);
				if(StringUtils.isNotBlank(itemfmt)) {
					if(itemfmt.indexOf(".")!=-1)
						item_.setDecimalwidth(itemfmt.split("\\.")[1].length());
					else
						item_.setDecimalwidth(0);
				}else
					item_.setDecimalwidth(0);
				ColumnsInfo info_ = new ColumnsInfo(item_);
				info_.setEditableValidFunc("false");
				if ("a00z0".equalsIgnoreCase(itemid)|| "a00z2".equalsIgnoreCase(itemid))
					info_.setColumnWidth(150);
				else
					info_.setColumnWidth(100);
				list.add(info_);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return list;
	}
	/**
	 * 添加季度统计
	 * @param list
	 * @param headList
	 * @param accumulate 
	 */
	private void getQuarterBean(ArrayList list, ArrayList headList, String accumulate) {
		//添加季度统计
		for(int i=0;i<4;i++){
			String amonthdesc = "";
			if(i==0) 
				amonthdesc = ResourceFactory.getProperty("gz.analysistable.oneQuarter");
			if(i==1) 
				amonthdesc = ResourceFactory.getProperty("gz.analysistable.twoQuarter");
			if(i==2) 
				amonthdesc = ResourceFactory.getProperty("gz.analysistable.threeQuarter");
			if(i==3) 
				amonthdesc = ResourceFactory.getProperty("gz.analysistable.fourQuarter");
			LazyDynaBean bean = new LazyDynaBean();
			bean.set("amonth", amonthdesc);
			boolean isHave = false;
			int m1 = 0;
			int m2 = 0;
			if("0".equals(accumulate)) {
				m1 = 4*i;
				m2 = 4*i+3;
			}else {
				m1 = 7*i;
				m2 = 7*i+6;
			}
			for (int j = 0; j < headList.size(); j++) {
				LazyDynaBean abean = (LazyDynaBean) headList.get(j);
				String itemid = (String) abean.get("itemid");
				String itemtype = (String) abean.get("itemtype");
				String total_ = "";
				if("N".equalsIgnoreCase(itemtype)) {
					double sum = 0;
					double total = 0;
					if(list.size()>0) {
						int recordnum = 0;
						for(int n=m1;n<m2;n++) {
							LazyDynaBean bean1 = (LazyDynaBean) list.get(n);
							String itemvalue = (String)bean1.get(itemid.toLowerCase());
							String amonth_ = (String)bean1.get("amonth");
							if(amonth_.indexOf(ResourceFactory.getProperty("gz.analysistable.toatlCount"))!=-1) {
								continue;
							}
							double value = 0.0;
							if(!"".equals(itemvalue)) {
								value = Double.parseDouble((String)bean1.get(itemid.toLowerCase()));
								isHave = true;
								recordnum++;
							}
							sum = sum + value;
						}
						total = sum;
						if(total>=0&&isHave) {
							if("personnum".equals(itemid)) {
								int b = (int)total/recordnum;
								total_ = String.valueOf(b);
							}else {
								total_ = String.valueOf(total);
							}
						}
					}else {
						total_ = "";
					}
				}
				bean.set(itemid.toLowerCase(), total_);
			}
			list.add(m2,bean);
		}
	}
	/**
	 * 得到表头列表
	 * 
	 * @param rsdtld
	 * @return
	 */
	@Override
    public ArrayList getTableHeadlist(String rsdtld, String rsid, String itemid, String visibleMonth, UserView userView) {
		ArrayList list = new ArrayList();
		RowSet rs = null;
		try {
			String sql = " select a.nwidth,a.align,a.itemdesc,a.itemid,a.itemfmt from reportitem a where a.rsdtlid="
					+ rsdtld + " order by a.sortid";
			LazyDynaBean bean = null;
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql);
			while (rs.next()) {
				bean = new LazyDynaBean();
				if(userView!=null&& "0".equals(userView.analyseFieldPriv(rs.getString("itemid"))))
					continue;
				bean.set("itemid", rs.getString("itemid"));
				bean.set("itemdesc", rs.getString("itemdesc"));
				bean.set("align", rs.getString("align"));
				bean.set("nwidth", rs.getString("nwidth"));
				String itemtype = "A";
				String codesetid = "0";
				String itemfmt = "";
				if (rs.getString("itemfmt") != null)
					itemfmt = rs.getString("itemfmt");
				FieldItem item=DataDictionary.getFieldItem(rs.getString("itemid"));
				if(item!=null) {
					itemtype = item.getItemtype();
					codesetid = item.getCodesetid();
				}

				/*if (rs.getString("itemid").toLowerCase().equals("a00z0"))
					bean.set("itemtype", "D");
				else*/
					bean.set("itemtype", itemtype);
				bean.set("codesetid", codesetid);
				bean.set("itemfmt", itemfmt);
				list.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return list;
	}

	@Override
    public int[] getChartTextaera(ArrayList recordList, ArrayList<ColumnsInfo> column) {
		int[] chartTexraera = new int [4];
		int recordnum = recordList.size();
		int colnum = column.size();
		chartTexraera[0]=3;
		chartTexraera[1]=recordnum+1;
		chartTexraera[2]=colnum;
		chartTexraera[3]=recordnum+1;
		return chartTexraera;
	}
}
