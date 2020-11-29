package com.hjsj.hrms.module.gz.analysistables.analysisdata.businessobject.impl;

import com.hjsj.hrms.businessobject.infor.multimedia.PhotoImgBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.gz.analysistables.analysisdata.businessobject.EmployeePayMusterService;
import com.hjsj.hrms.module.gz.analysistables.util.GzAnalysisUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

public class EmployeePayMusterServiceImpl implements EmployeePayMusterService {

	Connection conn;

    public EmployeePayMusterServiceImpl(Connection conn) {
        this.conn = conn;
    }
    
	/**
	 * 人员工资台帐:人员列表
	 * @param flag 
	 * @param condSql 
	 */
	@Override
    public String getPersonSql(String pre, String year, String salaryid, String tableName, UserView userView,
                               String verifying, String flag, String condSql){
		StringBuffer persql = new StringBuffer();
		try {
			StringBuffer buf = new StringBuffer();
			GzAnalysisUtil gzAnalysisUtil = new GzAnalysisUtil(conn, userView);
			Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.conn);
			String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "valid");
			String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
			if(StringUtils.isBlank(year)) {
				year = "-1";
			}
			String unique = ",uniqueid";
			if ("0".equals(flag)) {
				persql.append("select DISTINCT nbase" + Sql_switcher.concat()
						+ "a0100 objectid,max(a0101) a0101,max(e0122) e0122,max(b0110) b0110,max(a0000) a0000" + unique
						+ ",nbase,a0100 from (");
			} else if ("1".equals(flag)) {
				persql.append("select count(DISTINCT nbase" + Sql_switcher.concat() + "a0100) count from (");
			}

			String[] pretemp = pre.split(",");
			for (int i = 0; i < pretemp.length; i++) {
				pre = pretemp[i];
				String only = "," + pre + "a01." + onlyname + " uniqueid";
				if (onlyname == null || "".equals(onlyname) || "a0100".equals(onlyname) || "a0101".equals(onlyname)
						|| "e0122".equals(onlyname) || "a0000".equals(onlyname)) {
					only = ",'' uniqueid";
				}
				String dbSql = gzAnalysisUtil.getDbSQL(pre, salaryid, tableName, verifying);
				String salarySql = gzAnalysisUtil.getSalarysetSQL(salaryid);
				if (i != 0) {
					buf.append(" union all ");
				}
				buf.append("select nbase," + tableName + ".a0100," + tableName + ".a0101 a0101," + tableName
							+ ".e0122 e0122," + tableName + ".b0110 b0110," + tableName + ".a0000 a0000" + only
							+ " from  " + tableName);
				
				buf.append(" LEFT join " + pre + "a01 on " + tableName + ".A0100=" + pre
						+ "a01.A0100 where upper(nbase)='" + pre.toUpperCase() + "' and ");
				buf.append(Sql_switcher.year("A00Z0"));
				buf.append("=");
				buf.append(year);
				buf.append(" ");
				buf.append(dbSql);
				buf.append(" and ");
				buf.append(salarySql);
				String b_units = userView.getUnitIdByBusiOutofPriv("1");// 1:工资发放 2:工资总额 3:所得税
				String sql = gzAnalysisUtil.getPrivSQL(tableName, pre, salaryid, b_units, "");
				buf.append(" " + sql);
			}
			String temp = buf.toString().replaceAll("salaryhistory", "salaryarchive");
			buf.append(" union all " + temp);
			persql.append(buf);
			persql.append(") s  ");
			if(condSql.length()>0) {
				persql.append(" where 1=1 "+condSql);
			}
			if ("0".equals(flag)) {
				persql.append(" group by nbase,a0100,uniqueid");
				persql.append(" order by ");
				persql.append(" b0110,e0122,a0000");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return persql.toString();
	}

	@Override
    public ArrayList<ColumnsInfo> getEmployeePayMusterColumnsInfo(UserView userView, String rsid, String rsdtlid) {
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
			info.setColumnWidth(50);
			info.setLocked(true);
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
	 * 得到人员列表的工具栏
	 */
	@Override
    public ArrayList getEmployeePayMusterButtons() {
		ArrayList buttonList = new  ArrayList();
		ButtonInfo wordButton = new ButtonInfo("","employpaymuster_me.getorgtree()");
		wordButton.setIcon("/module/gz/analysistables/images/orgtree.png");
		wordButton.setId("orgtreebutton");
		buttonList.add(wordButton);
		return buttonList;
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
			String objectid= (String)paramMap.get("objectid");
			UserView userView = (UserView)paramMap.get("userView"); 
			String tableName = (String)paramMap.get("tableName");
			String year = (String)paramMap.get("year");
			String intoflag = (String)paramMap.get("intoflag");
			String verifying = (String)paramMap.get("verifying");//是否包含审批过程数据
			GzAnalysisUtil gzAnalysisUtil  = new GzAnalysisUtil(conn, userView);
			int maxnum = 15;
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
			ArrayList recordlist = new ArrayList();
			if (objectid.trim().length()>0) {
				if ("1".equals(intoflag)) {
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
					sql.append("select ");
					sql.append(buf_nvl.toString());
					if(buf_nvl.toString().length()>0)
			    		sql.append(",");
					sql.append(Sql_switcher.month("A00Z0"));
					sql.append(" as aMonth ");
					sql.append(" from "+tableName+" where 1=1 ");
					String a0100 = objectid.substring(3);
					String nbase_ = objectid.substring(0, 3).toUpperCase();
					sql.append(" and A0100='" + a0100 + "'  "+privSql+" and ");
					sql.append(" UPPER(nbase) ='" + nbase_.toUpperCase() + "' ");
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
		            sql_all.append("select "+buf_sum+",aMonth from ("+sql);
		            sql_all.append(") a ");
		            sql_all.append(" group by aMonth order by aMonth");
				}
				if("1".equals(intoflag)) {
					rs = dao.search(sql_all.toString());
					while (rs.next()) {
						LazyDynaBean bean = new LazyDynaBean();
						bean.set("amonth", gzAnalysisUtil.getUpperMonth(rs.getInt("amonth")));
						bean.set("amonth_n", rs.getInt("amonth")+"");
						for (int j = 0; j < headList.size(); j++) {
							LazyDynaBean abean = (LazyDynaBean) headList.get(j);
							String itemid = (String) abean.get("itemid");
							bean.set(itemid.toLowerCase(),rs.getString((String) abean.get("itemid")) == null ? "": rs.getString((String) abean.get("itemid")));
						}
						recordlist.add(bean);
						list.add(bean);
					}
				}
			}
	    	
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
					if(m==maxnum-2||m==maxnum-1) {
						String amonthdesc = "";
						if(m==maxnum-2)
							amonthdesc = ResourceFactory.getProperty("gz.analysistable.monthavg");
						if(m==maxnum-1)
							amonthdesc = ResourceFactory.getProperty("gz.gz_acounting.total");
						bean.set("amonth", amonthdesc);
						bean.set("amonth_n", m+"");
						for (int j = 0; j < headList.size(); j++) {
							LazyDynaBean abean = (LazyDynaBean) headList.get(j);
							String itemtype = (String) abean.get("itemtype");
							String itemid = (String) abean.get("itemid");
							String average_ = "";
							String total_ = "";
							if("N".equalsIgnoreCase(itemtype)) {
								double sum = 0;
								double average = 0;
								double total = 0;
								if(recordlist.size()>0) {
									for(int n=0;n<recordlist.size();n++) {
										LazyDynaBean bean1 = (LazyDynaBean) recordlist.get(n);
										double value = Double.parseDouble((String)bean1.get(itemid.toLowerCase()));
										sum = sum + value;
									}
									average = sum/recordlist.size();
									total = sum;
									average_ = String.valueOf(average);
									total_ = String.valueOf(total);
									
								}else {
									average_ = "";
									total_ = "";
								}
							}
							if(m==maxnum-2) {
								if("1".equals(intoflag)) 
									bean.set(itemid.toLowerCase(), average_);
								else
									bean.set(itemid.toLowerCase(), "");
							}else if(m==maxnum-1){
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

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return list;
	}
	/**
	 * 得到人员列表的列
	 */
	@Override
    public ArrayList<ColumnsInfo> getEmployeePersonColumnsInfo() {
		ArrayList<ColumnsInfo> list = new ArrayList<ColumnsInfo>();
		try
		{
			FieldItem item = new FieldItem();
			item.setItemid("b0110");
			item.setItemdesc(ResourceFactory.getProperty("label.title.org"));
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(50);
			item.setCodesetid("UN");
			ColumnsInfo info = new ColumnsInfo(item);
			info.setEditableValidFunc("false");
			info.setColumnWidth(110);
			info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			list.add(info);
			
			item = new FieldItem();
			item.setItemid("e0122");
			item.setItemdesc(ResourceFactory.getProperty("hrms.e0122"));
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(50);
			item.setCodesetid("UM");
			info = new ColumnsInfo(item);
			info.setEditableValidFunc("false");
			info.setColumnWidth(110);
			info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			list.add(info);
			
			item = new FieldItem();
			item.setItemid("a0101");
			item.setItemdesc(ResourceFactory.getProperty("label.title.name"));
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(80);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setEditableValidFunc("false");
			info.setColumnWidth(220);
			info.setRendererFunc("employpaymuster_me.getNameDesc");
			list.add(info);
			
			item = new FieldItem();
			item.setItemid("objectid");
			item.setItemdesc("objectid");
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(80);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setEditableValidFunc("false");
			info.setColumnWidth(100);
			info.setEncrypted(true);
			info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			list.add(info);
			
			item = new FieldItem();
			item.setItemid("uniqueid");
			item.setItemdesc("uniqueid");
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(80);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setEditableValidFunc("false");
			info.setColumnWidth(100);
			info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			list.add(info);
			
			item = new FieldItem();
			item.setItemid("photo");
			item.setItemdesc("photo");
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(80);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setEditableValidFunc("false");
			info.setColumnWidth(100);
			info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			list.add(info);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return list;
	}
	/**
	 * 得到表头列表
	 * 
	 * @param rsdtld
	 * @return
	 */
	public ArrayList getTableHeadlist(String rsdtld, String rsid, String itemid,String visibleMonth,UserView userView) {
		ArrayList list = new ArrayList();
		RowSet rs = null;
		try {
			String sql = " select a.nwidth,a.align,a.itemdesc,a.itemid,a.itemfmt from reportitem a where  a.rsdtlid="
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

				/*if (rs.getString("itemid").toLowerCase().equals("a00z0")||rs.getString("itemid").toLowerCase().equals("a00z2"))
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
	/**
	 * 得到人员列表的列头信息
	 */
	@Override
    public ArrayList<LazyDynaBean> getPersonHeadlist() {
		//b0110  e0122 a0101 objectid uniqueid
		ArrayList list = new ArrayList();
		list.add(getBean("b0110",ResourceFactory.getProperty("label.title.org"),"2","20","A","UN",""));
		list.add(getBean("e0122",ResourceFactory.getProperty("hrms.e0122"),"2","20","A","UM",""));
		list.add(getBean("a0101",ResourceFactory.getProperty("label.title.name"),"2","20","A","0",""));
		list.add(getBean("objectid","objectid","2","20","A","0",""));
		list.add(getBean("objectid_e","objectid_e","2","20","A","0",""));
		list.add(getBean("uniqueid","uniqueid","2","20","A","0",""));
		list.add(getBean("photo","photo","2","20","A","0",""));
		return list;
	}
	
	private LazyDynaBean getBean(String itemid, String itemdesc, String align, String nwidth, String itemtype,
			String codesetid, String itemfmt) {
		LazyDynaBean bean = new LazyDynaBean();
		bean.set("itemid", itemid);
		bean.set("itemdesc", itemdesc);
		bean.set("align", align);
		bean.set("nwidth", nwidth);
		bean.set("itemtype", itemtype);
		bean.set("codesetid", codesetid);
		bean.set("itemfmt", itemfmt);
		return bean;
	}
	/**
	 * 得到人员列表数据
	 */
	@Override
    public ArrayList<LazyDynaBean> getPersonDataList(HashMap paramMap) {
		ArrayList datalist = new ArrayList();
		RowSet rset = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			String pre = (String) paramMap.get("nbase");
			int limit = (Integer)paramMap.get("limit");
	        int page = (Integer)paramMap.get("page");
	        String condSql = (String)paramMap.get("condSql");
	        String verifying = (String)paramMap.get("verifying");
	        String salaryid = (String)paramMap.get("salaryid");
	        String year = (String)paramMap.get("year");
	        UserView userView = (UserView) paramMap.get("userview");
	        //得到人员sql
			String sql = this.getPersonSql(pre, year, salaryid, "salaryhistory", userView, verifying,"0",condSql);
			if(limit>0 && page>0){
				rset = dao.search(sql,limit,page);
            }else{
            	rset = dao.search(sql);
            }
			// 59906 vfs更改获取人员照片方式
			PhotoImgBo imgBo = new PhotoImgBo(conn);
			imgBo.setIdPhoto(true);
			while(rset.next()) {
				LazyDynaBean bean = new LazyDynaBean();
				String b0110 = rset.getString("b0110");
                String b0110_desc = AdminCode.getCodeName("UN",b0110);
				String e0122 = rset.getString("e0122");
				String e0122_desc = AdminCode.getCodeName("UM",e0122);
				String a0101 = rset.getString("a0101");
				String nbase = rset.getString("nbase");
				String a0100 = rset.getString("a0100");
				String objectid = rset.getString("objectid");
				String uniqueid = rset.getString("uniqueid");
				bean.set("b0110", b0110_desc==null?"":b0110_desc);
				bean.set("e0122", e0122_desc==null?"":e0122_desc);
				bean.set("a0101", a0101==null?"":a0101);
				bean.set("objectid", objectid==null?"":objectid);
				bean.set("objectid_e", objectid==null?"":PubFunc.encrypt(objectid));
				bean.set("uniqueid", uniqueid==null?"":uniqueid);
				String filename = imgBo.getPhotoPath(nbase, a0100);
				bean.set("photo", filename);
				datalist.add(bean);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rset);
		}
		return datalist;
	}

	@Override
    public int getDataCount(HashMap paramMap) {
		int count = 0;
		RowSet rset = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			String pre = (String) paramMap.get("nbase");
	        String condSql = (String)paramMap.get("condSql");
	        String verifying = (String)paramMap.get("verifying");
	        String salaryid = (String)paramMap.get("salaryid");
	        String year = (String)paramMap.get("year");
	        UserView userView = (UserView) paramMap.get("userview");
	        //得到人员sql
			String sql = this.getPersonSql(pre, year, salaryid, "salaryhistory", userView, verifying,"1",condSql);
            rset = dao.search(sql);
            
			if(rset.next()) {
				count = rset.getInt("count");
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rset);
		}
		return count;
	}

}
