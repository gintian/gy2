package com.hjsj.hrms.businessobject.sys.job;

import com.hjsj.hrms.businessobject.stat.StatCondAnalyse;
import com.hjsj.hrms.businessobject.structuresql.StructureExecSqlString;
import com.hjsj.hrms.transaction.stat.ArchiveXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class StatArchiveJob implements Job {

	@Override
    public void execute(JobExecutionContext paramJobExecutionContext)
			throws JobExecutionException {
		Connection conn = null;
		RowSet rs = null;
		ContentDAO dao = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String backdate = sdf.format(new Date());
			StatCondAnalyse sca = new StatCondAnalyse();
			conn = AdminDb.getConnection();
			dao = new ContentDAO(conn);
			UserView userView = new UserView("su", "", conn);
			userView.setSuper_admin(true);
			
			ArrayList list = new ArrayList();
			String sql = "select sname.id,infokind,archive_set,nbase,archive,archive_type,archive_field,LExpr,Factor,slegend.flag from sname join slegend on sname.id = slegend.id where type=1 and archive is not null and archive_field is not null order by snorder";
			rs = dao.search(sql);
			while (rs.next()) {
				String id = rs.getString("id");
				String infokind = rs.getString("infokind");
				String archive_set = rs.getString("archive_set");
				String nbase = rs.getString("nbase");
				String archive = rs.getString("archive");
				String archive_type = rs.getString("archive_type");
				String archive_field = rs.getString("archive_field");
				//统计分析归档，常用统计中没有在信息集设置中勾选人员库则不进行统计 chenxg 2017-07-14
				if(StringUtils.isEmpty(nbase)) {
					continue;
				}
				
				// 统计条件 where (...)
				String lexpr = rs.getString("lexpr");
				String factor = rs.getString("factor");
				String flag = rs.getString("flag");
				boolean ishistory = false;
				if("1".equals(flag)) {
					ishistory = true;
				}
				
				String strquery = sca.getCondQueryString(lexpr, factor, nbase,
						ishistory, userView.getUserName(), "", userView, infokind,
						true);
				StringBuffer strsql = new StringBuffer();
				
				/**
				 * 自动计算默认统计个数
				 * 为方便获取值，统一将B0110、E01A1 命名为E0122，a0100为无效值，只是为了统计个数用
				 */
				//单位统计
				if("2".equals(infokind)){
					strsql.append(" select B01.B0110 e0122,'1' a0100 ").append(strquery);
				}
				//岗位统计
				else if("3".equals(infokind)){
					strsql.append(" select K01.E01A1 e0122,'1' a0100 ").append(strquery);
				}
				//人员，如果没有设置人员库，没有数据，随便写个sql返回值为空
				else if(nbase.length()<3){
					strsql.append(" select '1' E0122,'2' a0100 from DBName where 1<>1 ");
				}
				//人员统计
				else{
					String[] pres = nbase.split(",");
					if(ishistory){
						for (int i = 0; i < pres.length; i++) {
						    //统计分析归档，常用统计中没有在信息集设置中勾选人员库则不进行统计 chenxg 2017-07-14
			                if(StringUtils.isEmpty(pres[i])) {
								continue;
							}
			                
							strquery = sca.getCondQueryString(lexpr, factor, pres[i],
									ishistory, userView.getUserName(), "", userView, infokind,
									true);
							strsql.append("select (case when b0110 is null then e0122 else b0110 end) e0122,"+pres[i]+".a0100 from (");
							strsql.append("select distinct " + pres[i] + "A01.a0100 "+strquery);
							strsql.append(") tab"+i);
							strsql.append(" join "+pres[i]+"a01 "+pres[i]);
							strsql.append(" on tab"+i+".a0100 = "+pres[i]+".a0100");
							if(i!=pres.length-1){
								strsql.append(" union all ");
							}
						}
					}else{
						for (int i = 0; i < pres.length; i++) {
						    //统计分析归档，常用统计中没有在信息集设置中勾选人员库则不进行统计 chenxg 2017-07-14
                            if(StringUtils.isEmpty(pres[i])) {
								continue;
							}
                            
                            strquery = sca.getCondQueryString(lexpr, factor, pres[i],
                                    ishistory, userView.getUserName(), "", userView, infokind,
                                    true);
//                            strsql.append("select (case when b0110 is null then e0122 else b0110 end) e0122,a0100 "+strquery);
                            strsql.append("select (case when e0122 is null then b0110 else e0122 end) e0122,a0100 "+strquery);
                            if(i!=pres.length-1){
                                strsql.append(" union all ");
                            }
                        }
					}
					
				}
				strquery = strsql.toString();
				
				ArchiveXml xml = new ArchiveXml(conn, id, archive);
				String auto = xml.getValue("auto");
				if (!"1".equals(auto)) {
					continue;
				}
				String unit_level = xml.getValue("unit_level");
				String dept_level = xml.getValue("dept_level");
				String dept_ctrl = xml.getValue("dept_ctrl");
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("archive_set", archive_set);
				bean.set("archive_type", archive_type);
				bean.set("archive_field", archive_field);
				bean.set("strquery", strquery);
				bean.set("unit_level", unit_level);
				bean.set("dept_level", dept_level);
				bean.set("dept_ctrl", dept_ctrl);
				list.add(bean);
			}

			for (int i = 0; i < list.size(); i++) {
				LazyDynaBean bean = (LazyDynaBean) list.get(i);
				String archive_set = (String) bean.get("archive_set");
				if(archive_set == null || archive_set.trim().length()==0) { //归档子集未null 空 sql语句报错  wangb 2020-01-13
					continue;
				}
				String archive_field = (String) bean.get("archive_field");
				String archive_type = (String) bean.get("archive_type");
				String unit_level = (String) bean.get("unit_level");
				String dept_level = (String) bean.get("dept_level");
				String dept_ctrl = (String) bean.get("dept_ctrl");
				String strquery = (String) bean.get("strquery");
				if (!"1".equals(dept_ctrl)) {
					dept_level = "0";
				}

				String insertsql = "insert into "
						+ archive_set
						+ "("
						+ archive_field
						+ ",B0110,I9999,CreateTime,ModTime,CreateUserName,ModUserName,"
						+ archive_set + "Z0," + archive_set
						+ "Z1) values(?,?,?,?,?,?,?,?,?)";
				
				String updatesql = "update " + archive_set + " set "
						+ archive_field
						+ " = ?,ModTime = ?,ModUserName = ? where "
						+ archive_set + "Z0 = ? and " + archive_set
						+ "Z1 = ? and b0110 = ?";
				
				ArrayList validlist = new ArrayList();
				getTime(archive_type);
				String validsql = "select b0110 from " + archive_set
						+ " where " + archive_set + "Z0 = "
						+ Sql_switcher.dateValue(z0) + " and " + archive_set
						+ "Z1 = " + z1;
				
				RowSet rs2 = null;
				rs2 = dao.search(validsql);
				while (rs2.next()) {
					validlist.add(rs2.getString("b0110"));
				}
				String inputsql = "select codeitemid from organization where codesetid <> '@K' and layer <= (case when codesetid = 'UN' then '"
						+ unit_level
						+ "' when codesetid = 'UM' then '"
						+ dept_level
						+ "' end) and "
						+ Sql_switcher.dateValue(backdate)
						+ " between start_date and end_date";
				ArrayList inputlist = new ArrayList();
				rs2 = dao.search(inputsql);
				while(rs2.next()){
					inputlist.add(rs2.getString("codeitemid"));
				}
				try{
					if (rs2 != null){
						rs2.close();
						rs2 = null;
					}
				} catch(Exception e){
					e.printStackTrace();
				}
				sql = "select codeitemid,(case when sum(ind) is not null then sum(ind) else 0 end) mcount from organization org left join (select E0122,count(a0100) ind from ("
						+ strquery
						+ ") A group by E0122 ) tab on org.codeitemid = "
						+ Sql_switcher.substr("tab.E0122", "1",
								Sql_switcher.length("org.codeitemid"))
						+ " where codesetid <> '@K' and "
						+ Sql_switcher.dateValue(backdate)
						+ " between start_date and end_date group by codeitemid order by codeitemid";
				ArrayList insertlist = new ArrayList();
				ArrayList updatelist = new ArrayList();
				
				rs = dao.search(sql);
				while (rs.next()) {
					String codeitemid = rs.getString("codeitemid");
					ArrayList indexlist = new ArrayList();
					if (validlist.contains(codeitemid)) {
						if(inputlist.contains(codeitemid)){
							indexlist.add(rs.getString("mcount"));
							indexlist.add(new java.sql.Date(new Date().getTime()));
							indexlist.add(userView.getUserFullName());
							indexlist.add(new java.sql.Date(DateUtils.getDate(z0,
									"yyyy-MM-dd").getTime()));
							indexlist.add(z1);
							indexlist.add(rs.getString("codeitemid"));
							updatelist.add(indexlist);
						}
					} else {
						if(inputlist.contains(codeitemid)){
							StructureExecSqlString structureExecSqlString = new StructureExecSqlString();
							String i9999 = structureExecSqlString.getUserI9999(
									archive_set, rs.getString("codeitemid"),
									"B0110", conn);
							indexlist.add(rs.getString("mcount"));
							indexlist.add(rs.getString("codeitemid"));
							indexlist.add(i9999);
							indexlist.add(new java.sql.Date(new Date().getTime()));
							indexlist.add(new java.sql.Date(new Date().getTime()));
							indexlist.add(userView.getUserName());
							indexlist.add(userView.getUserFullName());
							indexlist.add(new java.sql.Date(DateUtils.getDate(z0,
									"yyyy-MM-dd").getTime()));
							indexlist.add(z1);
							insertlist.add(indexlist);
						}
					}
				}
				if (insertlist.size() > 0) {
					dao.batchInsert(insertsql.toString(), insertlist);
				}
				if (updatelist.size() > 0) {
					dao.batchUpdate(updatesql.toString(), updatelist);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs!=null){
					rs.close();
					rs = null;
				}
				if(conn!=null){
					conn.close();
					conn = null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	private String z0 = "";//年月标示
	private String z1 = "1";//次数
	
	private void getTime(String archive_type) {
		Calendar rightNow = Calendar.getInstance();
		String year = rightNow.get(Calendar.YEAR) + "";
		String month = (rightNow.get(Calendar.MONTH) + 1) + "";
		if ("1".equals(archive_type))// 月
		{
			z0 = year + "-" + month + "-01";
		} else if ("2".equals(archive_type))// 季
		{
			if (1 <= Integer.parseInt(month) && Integer.parseInt(month) <= 3) {
				month = "1";
				z0 = year + "-01-01";

			} else if (4 <= Integer.parseInt(month)
					&& Integer.parseInt(month) <= 6) {
				month = "2";
				z0 = year + "-04-01";
			} else if (7 <= Integer.parseInt(month)
					&& Integer.parseInt(month) <= 9) {
				month = "3";
				z0 = year + "-07-01";
			} else {
				month = "4";
				z0 = year + "-10-01";
			}
			z1 = month;
		} else if ("3".equals(archive_type))// 半年
		{
			if (Integer.parseInt(month) < 7) {
				month = "1";
				z0 = year + "-01-01";
			} else {
				month = "2";
				z0 = year + "-07-01";
			}
			z1 = month;
		} else if ("4".equals(archive_type))// 年
		{
			z0 = year + "-01-01";
		}
	}
	
}
