package com.hjsj.hrms.transaction.info.leader;

import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class AddLeaderInfoTrans extends IBusiness {

	public void execute() throws GeneralException {

		String flag = (String) this.getFormHM().get("flag");
		
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		
		if ("add".equals(flag)) {
			String msg = "ok";
			try {
				String a0100 = (String) this.getFormHM().get("a0100");
				String emp_e = (String) this.getFormHM().get("emp_e");
				String link_field = (String) this.getFormHM().get("link_field");
				String b0110field = (String)this.getFormHM().get("b0110field");
				String orderbyfield = (String)this.getFormHM().get("orderbyfield");
				String b0110 = (String) this.getFormHM().get("b0110");
				String i9999 = (String) this.getFormHM().get("i9999");
				String[] persons=a0100.split(",");
				
				HashMap dbpreMap = new HashMap();
				for(int k=0;k<persons.length;k++){
					String person = persons[k];
					if(person.length()!=11)
						continue;
					String dbpre = person.substring(0, 3);
					a0100 = person.substring(3);
					if(dbpreMap.containsKey(dbpre)){
						String a0100In = (String)dbpreMap.get(dbpre); 
						a0100In+="'"+a0100+"',";
						dbpreMap.put(dbpre, a0100In);
					}else{
						dbpreMap.put(dbpre, "'"+a0100+"',");
					}
				}
				
				StringBuffer personStr = new StringBuffer();
				Iterator ite = dbpreMap.keySet().iterator();
				while(ite.hasNext()){
					String dbpre = (String)ite.next();
					personStr.append(" select '"+dbpre+"' dbpre,a0100,a0000 from "+dbpre+"A01 where a0100 in ("+dbpreMap.get(dbpre)+" 'a') ");
					personStr.append(" union all ");
				}
				
				personStr.delete(personStr.length()-10, personStr.length());
				personStr.append(" order by a0000 ");
				
				
				List personList = ExecuteSQL.executeMyQuery(personStr.toString(), this.frameconn);
				
				for(int j =0;j<personList.size();j++){
					LazyDynaBean ldb = (LazyDynaBean)personList.get(j);
					String dbpre = (String)ldb.get("dbpre");
					String pa0100 = (String)ldb.get("a0100");
					
					String sql ="select a0100 from "+dbpre+emp_e+" where "+ link_field + "=" + i9999 + " and "+b0110field+"='"+b0110+"' and a0100='"+pa0100+"'";
					
					this.frecset = dao.search(sql);
					if(this.frecset.next())
						continue;
					
					sql = "insert into "+dbpre
						+ emp_e
						+ " (State,CreateTime,ModTime,CreateUserName,a0100,i9999,"
						+ link_field + ","+b0110field+","+orderbyfield+") values(?,?,?,?,?,?,?,?,?)";
					ArrayList values = new ArrayList();
					values.add("3");
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					Date date = new Date(sdf.parse(sdf.format(new java.util.Date())).getTime());
							
					values.add(date);
					values.add(date);
					values.add(this.userView.getUserName());
					values.add(pa0100);
					values.add(new Integer(this.getMaxInt(dbpre,emp_e,"I9999", "where a0100='"+pa0100+"'")));
					values.add(new Integer(i9999));
					values.add(b0110);
					values.add(new Integer(this.getMaxInt(dbpre,emp_e,orderbyfield, "where "+b0110field+"='"+b0110+"'")));
			
					dao.insert(sql, values);
				}
				
			} catch (Exception e) {
				msg = "error";
				e.printStackTrace();
			} finally {
				this.getFormHM().put("msg", msg);
			}
		} else if ("del".equals(flag)) {
			try {
				ArrayList selectedlist = (ArrayList) this.getFormHM().get(
						"selectedinfolist");
				String emp_e = (String) this.getFormHM().get("emp_e");
				String link_field = (String) this.getFormHM().get("link_field");
				String b0110field = (String) this.getFormHM().get("b0110field");
				String sql = "delete from ###"+emp_e+" where "+link_field+"=? and "+b0110field+"=? and a0100=?";
				
				for (int i = 0; i < selectedlist.size(); i++) {
					LazyDynaBean rec = (LazyDynaBean) selectedlist.get(i);
					String a0100 = (String)rec.get("a0100");
					String dbpre = (String)rec.get("dbpre");
					String b0110fieldvalue=(String)rec.get(b0110field);
					String link_fieldvalue=(String)rec.get(link_field);
					if(a0100==null||dbpre==null||dbpre.length()!=3)
						continue;
					ArrayList values = new ArrayList();
					values.add(Integer.valueOf(link_fieldvalue));
					values.add(b0110fieldvalue);
					values.add(a0100);
					dao.delete(sql.replaceAll("###", dbpre), values);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {

			}
		}
	}

	

	/**
	 * 获取 指定 的列的最大值加一
	 * @param dbpre 人员库
	 * @param emp_e 表名
	 * @param itemid 字段名（必须是数整型指标）
	 * @param a0100 人员id
	 * @return
	 * @throws GeneralException
	 */
	private int getMaxInt(String dbpre,String emp_e,String itemid, String where)
	throws GeneralException {
		int i = 1;
		StringBuffer sql = new StringBuffer(
				"select max("+itemid+")+1 as nmax from "+dbpre + emp_e +" "+where);//" where a0100='"+a0100+"'");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			this.frowset = dao.search(sql.toString());
			if (this.frowset.next()) {
				i = this.frowset.getInt("nmax");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return i;
	}
}
