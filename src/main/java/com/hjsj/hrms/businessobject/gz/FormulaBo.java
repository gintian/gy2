package com.hjsj.hrms.businessobject.gz;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 *<p>Title:</p> 
 *<p>Description:计算公式</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author lilinbing
 *@version 4.0
**/
public class FormulaBo {
	private UserView userview=null;
	
	
	public FormulaBo(UserView userview){
	    this.userview=userview;
	}
	
	public FormulaBo(){
    }
	/**
	 * 根据cstate字段和salaryid获取子标List
	 * @return ArrayList 
	 * @throws GeneralException
	 */
	public ArrayList subStandardList(Connection conn,String salaryid){
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(conn);
		String sqlstr = "";
		String sqlstr1 = "";
		ArrayList fieldList = new ArrayList();
		if(!"-1".equals(salaryid)){
			sqlstr = "select itemid,itemdesc from salaryset where salaryid="+salaryid+" order by sortid";//按sortid排序 zhaoxg add 2013-11-9
			sqlstr1 = "select cname,chz from midvariable where nflag=0 and templetid=0 and (cstate='"+salaryid+"' or cstate is null)";
		}else{
			//项目：取自薪资总额子集中的指标和薪资总额临时变量。
			String salaryAmountSet = "";
			GzAmountXMLBo bo = new GzAmountXMLBo(conn,1);
			HashMap hm = bo.getValuesMap();
			if (hm != null)
				salaryAmountSet = (String) hm.get("setid")==null?"":(String) hm.get("setid");
			fieldList = DataDictionary.getFieldList(salaryAmountSet, Constant.USED_FIELD_SET);
			sqlstr1 = "select cname,chz from midvariable where nflag=4 and templetid=0 and cstate=-1";
		}
		
		ArrayList dylist = null;
		CommonData dataobj1 = new CommonData("","");
		list.add(dataobj1);
		try {
			if(!"-1".equals(salaryid)){
				dylist = dao.searchDynaList(sqlstr);
				for(Iterator it=dylist.iterator();it.hasNext();){
					DynaBean dynabean=(DynaBean)it.next();
					String itemid = dynabean.get("itemid").toString();
					if("A0100".equalsIgnoreCase(itemid))
						continue;
					if("A0000".equalsIgnoreCase(itemid))
						continue;	
					String itemdesc = dynabean.get("itemdesc").toString();
					CommonData dataobj = new CommonData(itemid+":"+itemdesc,itemid+":"+itemdesc);
					list.add(dataobj);
				}
				dylist.clear();
			}else{
				int n = fieldList.size();
				for(int i=0;i<n;i++){
					FieldItem item = (FieldItem)fieldList.get(i);
					String itemid = item.getItemid().toUpperCase();
					if(itemid==null || "".equals(itemid))
						continue;
					String tempPriv = this.userview.analyseFieldPriv(itemid);
					if("0".equals(tempPriv))
						continue;
					String itemdesc = item.getItemdesc();
					CommonData dataobj = new CommonData(itemid+":"+itemdesc,itemid+":"+itemdesc);
					list.add(dataobj);
				}
			}
			
			dylist = dao.searchDynaList(sqlstr1);
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				String itemid = dynabean.get("cname").toString();
				if("A0100".equalsIgnoreCase(itemid))
					continue;
				if("A0000".equalsIgnoreCase(itemid))
					continue;	
				String itemdesc = dynabean.get("chz").toString();
				CommonData dataobj = new CommonData(itemid.toLowerCase()+":"+itemdesc,itemid+":"+itemdesc);
				list.add(dataobj);
			}
			CommonData dataobj = new CommonData("newcreate","新建临时变量");
			list.add(dataobj);
			
		} catch(GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 *根据cstate字段和salaryid获取子标List
	 * @return ArrayList  xcs modify @ 2013-8-5
	 * @throws GeneralException 
	 * */
	public ArrayList subStandardList(Connection conn,String salaryid,String fieldsetid){
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(conn);
		String sqlstr = "";
		String sqlstr1 = "";
		if("-2".equals(salaryid)){
			sqlstr = "select itemid,itemdesc from fielditem where fieldsetid='"+fieldsetid+"' and useflag = '1'";
			sqlstr1 = "select cname,chz from midvariable where nflag=5 and templetid=0 and (cstate='"+fieldsetid+"' or cstate is null)";
		}
		ArrayList dylist = null;
		CommonData dataobj1 = new CommonData("","");
		list.add(dataobj1);
		try {
			if("-2".equals(salaryid)){
				dylist = dao.searchDynaList(sqlstr);
				for(Iterator it=dylist.iterator();it.hasNext();){
					DynaBean dynabean=(DynaBean)it.next();
					String itemid = dynabean.get("itemid").toString();
					if("A0100".equalsIgnoreCase(itemid))
						continue;
					if("A0000".equalsIgnoreCase(itemid))
						continue;	
					String itemdesc = dynabean.get("itemdesc").toString();
					CommonData dataobj = new CommonData(itemid+":"+itemdesc,itemid+":"+itemdesc);
					list.add(dataobj);
				}
				dylist.clear();
			}
			dylist = dao.searchDynaList(sqlstr1);
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				String itemid = dynabean.get("cname").toString();
				if("A0100".equalsIgnoreCase(itemid))
					continue;
				if("A0000".equalsIgnoreCase(itemid))
					continue;	
				String itemdesc = dynabean.get("chz").toString();
				CommonData dataobj = new CommonData(itemid.toLowerCase()+":"+itemdesc,itemid+":"+itemdesc);
				list.add(dataobj);
			}
			CommonData dataobj = new CommonData("newcreate","新建临时变量");
			list.add(dataobj);
			
		} catch(GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 根据cstate字段和salaryid获取子标List
	 * @return ArrayList 
	 * @throws GeneralException
	 */
	public ArrayList conditionsList(Connection conn,String salaryid){
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(conn);
		String sqlstr = "";
		String sqlstr1 = "";
		ArrayList fieldList = new ArrayList();
		if(!"-1".equals(salaryid)){
			sqlstr = "select itemid,itemdesc from salaryset where salaryid="+salaryid;
			
				sqlstr1 = "select nid,chz,cname from midvariable where nflag=0 and templetid=0 and (cstate is null or cstate='"+salaryid+"')";
		}else{
			//项目：取自薪资总额子集中的指标和薪资总额临时变量。
			String salaryAmountSet = "";
			GzAmountXMLBo bo = new GzAmountXMLBo(conn,1);
			HashMap hm = bo.getValuesMap();
			if (hm != null)
				salaryAmountSet = (String) hm.get("setid")==null?"":(String) hm.get("setid");
			fieldList = DataDictionary.getFieldList(salaryAmountSet, Constant.USED_FIELD_SET);
			sqlstr1 = "select nid,chz,cname from midvariable where nflag=4 and templetid=0 and cstate=-1";
		}
		ArrayList dylist = null;
		try {
			CommonData dataobj = new CommonData(":","");
			list.add(dataobj);
			if(!"-1".equals(salaryid)){
				dylist = dao.searchDynaList(sqlstr);
				for(Iterator it=dylist.iterator();it.hasNext();){
					DynaBean dynabean=(DynaBean)it.next();
					String itemid = dynabean.get("itemid").toString();
					if("A0000".equalsIgnoreCase(itemid))
						continue;
					if("A0100".equalsIgnoreCase(itemid))
						continue;
					String itemdesc = dynabean.get("itemdesc").toString();
					dataobj = new CommonData(itemid+":"+itemdesc,itemid+":"+itemdesc);
					list.add(dataobj);
				}
				dylist.clear();
			}else{
				int n = fieldList.size();
				for(int i=0;i<n;i++){
					FieldItem item = (FieldItem)fieldList.get(i);
					String itemid = item.getItemid();
					if(itemid==null || "".equals(itemid))
						continue;
					String itemdesc = item.getItemdesc();
					dataobj = new CommonData(itemid+":"+itemdesc,itemid+":"+itemdesc);
					list.add(dataobj);
				}
			}
			
			dylist = dao.searchDynaList(sqlstr1);
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				String itemid = dynabean.get("nid").toString();
				String itemdesc = dynabean.get("chz").toString();
				String cname=dynabean.get("cname").toString();
				dataobj = new CommonData(itemid+":"+itemdesc,cname+":"+itemdesc);
				list.add(dataobj);
			}
			
		} catch(GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 根据cstate字段和salaryid,fieldsetid获取子标List  为了数据采集
	 * @return ArrayList 
	 * @throws GeneralException
	 */
	public ArrayList conditionsList(Connection conn,String salaryid,String fieldsetid){
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(conn);
		String sqlstr = "";
		String sqlstr1 = "";
		sqlstr = "select itemid,itemdesc from fielditem where fieldsetid='"+fieldsetid+"' and useflag = '1'";
		sqlstr1="select nid,chz,cname from midvariable where nflag=5 and templetid=0 and (cstate='"+fieldsetid+"' or cstate is null or cstate = 'null')";
		ArrayList dylist = null;
		try {
			CommonData dataobj = new CommonData(":","");
			list.add(dataobj);
			dylist = dao.searchDynaList(sqlstr);
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				String itemid = dynabean.get("itemid").toString();
				if("A0000".equalsIgnoreCase(itemid))
					continue;
				if("A0100".equalsIgnoreCase(itemid))
					continue;
				String itemdesc = dynabean.get("itemdesc").toString();
				dataobj = new CommonData(itemid+":"+itemdesc,itemid+":"+itemdesc);
				list.add(dataobj);
			}
			dylist.clear();
			dylist = dao.searchDynaList(sqlstr1);
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				String itemid = dynabean.get("nid").toString();
				String itemdesc = dynabean.get("chz").toString();
				String cname=dynabean.get("cname").toString();
				dataobj = new CommonData(itemid+":"+itemdesc,cname+":"+itemdesc);
				list.add(dataobj);
			}
			
		} catch(GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 根据itemid字段和salaryid获取子标计算公式值
	 * @return String 
	 * @throws GeneralException
	 */
	public String formulavalue(Connection conn,String salaryid,String itemid){
		String formula = "";
		ContentDAO dao = new ContentDAO(conn);
		String sqlstr = "select rexpr from salaryformula  where salaryid="+salaryid+" and itemid='"+itemid+"'";
		ArrayList dylist = null;
		try {
			dylist = dao.searchDynaList(sqlstr);
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				formula = dynabean.get("rexpr").toString();
			}
		} catch(GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return formula;
	}

	public String pkgId(Connection conn,String id){
		String pkgid = "";
		ContentDAO dao = new ContentDAO(conn);
		String sqlstr = "select pkg_id as pkgid from gz_stand_pkg where status=1";//select max(pkg_id) as pkgid from gz_stand_history  where id='"+id+"'
		ArrayList dylist = null;
		try {
			dylist = dao.searchDynaList(sqlstr);
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				pkgid = dynabean.get("pkgid").toString();
			}
		} catch(GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pkgid;
	}
	/**
	 * 修计算公式改执行状态
	 * @throws GeneralException
	 */
	public void alertUseflag(Connection conn,String useflag,String salaryid,String itemid){
		ContentDAO dao=new ContentDAO(conn);
		String updatesql = "update salaryformula set useflag="+useflag+" where salaryid="+salaryid+" and itemid="+itemid;

		try {
			dao.update(updatesql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 根据itemid字段和salaryid获取子标计算公式执行顺序
	 * @return String 
	 * @throws GeneralException
	 */
	public String runFlag(Connection conn,String salaryid,String itemid){
		String runflag = "";
		ContentDAO dao = new ContentDAO(conn);
		String sqlstr = "select runflag from salaryformula  where salaryid="+salaryid+" and itemid='"+itemid+"'";
		ArrayList dylist = null;
		try {
			dylist = dao.searchDynaList(sqlstr);
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				runflag = dynabean.get("runflag").toString();
			}
		} catch(GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return runflag;
	}
	
	/**
	 * 修改计算公式改执行顺序
	 * @throws GeneralException
	 */
	public void alertRunflag(Connection conn,String runflag,String salaryid,String itemid){
		ContentDAO dao=new ContentDAO(conn);
		String updatesql = "update salaryformula set runflag="+runflag+" where salaryid="+salaryid+" and itemid="+itemid;

		try {
			dao.update(updatesql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 根据itemid字段和salaryid获取standarid
	 * @return String 
	 * @throws GeneralException
	 */
	public String standId(Connection conn,String salaryid,String itemid){
		String standid = "";
		ContentDAO dao = new ContentDAO(conn);
		String sqlstr = "select standid from salaryformula  where salaryid="+salaryid+" and itemid='"+itemid+"'";
		ArrayList dylist = null;
		try {
			dylist = dao.searchDynaList(sqlstr);
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				standid = dynabean.get("standid").toString();
				standid=standid!=null&&standid.length()>0?standid:"";
			}
		} catch(GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return standid;
	}
	/**
	 * 获取sortid和itemid
	 * @return String[]
	 * @throws GeneralException
	 */
	public String[] itemSortid(ContentDAO dao,String salaryid){
		String[] standid = {"0","0"};
		String sqlstr = "select max(itemid) as itemid,max(sortid) as sortid from salaryformula  where salaryid="+salaryid;
		ArrayList dylist = null;
		try {
			dylist = dao.searchDynaList(sqlstr);
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				if(dynabean.get("itemid")!=null&&dynabean.get("itemid").toString().trim().length()>0){
					standid[0] = Integer.parseInt(dynabean.get("itemid").toString())+1+"";
				}
				if(dynabean.get("sortid")!=null&&dynabean.get("sortid").toString().trim().length()>0){
					standid[1] = Integer.parseInt(dynabean.get("sortid").toString())+1+"";
				}
			}
		} catch(GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return standid;
	}
	/**
	 * 根据salaryid字段获取List
	 * @return ArrayList 
	 * @throws GeneralException
	 */
	public ArrayList sortList(Connection conn,String salaryid){
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(conn);
		String sqlstr = "select itemid,itemname,hzname from salaryformula where salaryid="+salaryid+" order by sortid,itemid desc";
		ArrayList dylist = null;
		try {
			dylist = dao.searchDynaList(sqlstr);
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				CommonData dataobj = new CommonData(dynabean.get("itemname").toString()+":"+dynabean.get("itemid").toString(),
						dynabean.get("hzname").toString());
				list.add(dataobj);
			}
			if(dylist.size()<1){
				CommonData dataobj = new CommonData("","");
				list.add(dataobj);
			}
			
		} catch(GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	public UserView getUserview() {
		return userview;
	}

	public void setUserview(UserView userview) {
		this.userview = userview;
	}
	
}
