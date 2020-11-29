package com.hjsj.hrms.businessobject.gz.premium;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.DynaBean;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *<p>Title:</p> 
 *<p>Description:计算公式</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author lilinbing
 *@version 4.0
**/
public class FormulaPremiumBo {
	/**
	 * 根据cstate字段和salaryid获取子标List
	 * @return ArrayList 
	 * @throws GeneralException
	 */
	public ArrayList subStandardList(Connection conn,String setid){
		ArrayList list = new ArrayList();
		CommonData dataobj1 = new CommonData("","");
		list.add(dataobj1);
		
		ArrayList fieldList2 = DataDictionary.getFieldList(setid, Constant.USED_FIELD_SET);
		for (int j = 0; j < fieldList2.size(); j++)
			{
			    FieldItem fieldItem = (FieldItem) fieldList2.get(j);
			    String itemid = fieldItem.getItemid();
			    String codesetId = fieldItem.getCodesetid();
			    
			  //  CommonData temp = new CommonData(fieldItem.getItemid(), fieldItem.getItemdesc());
			    CommonData dataobj = new CommonData(fieldItem.getItemid()+":"+ fieldItem.getItemdesc(),fieldItem.getItemid()+":"+ fieldItem.getItemdesc());
			   
			    list.add(dataobj);
			}
		
		return list;
	}
	/**
	 * 根据cstate字段和salaryid获取子标List
	 * @return ArrayList 
	 * @throws GeneralException
	 */
	public ArrayList conditionsList(Connection conn,String setid){
		ArrayList dylist = new ArrayList();;
		ArrayList fieldList2 = DataDictionary.getFieldList(setid, Constant.USED_FIELD_SET);
	    	for (int j = 0; j < fieldList2.size(); j++)
			{
			    FieldItem fieldItem = (FieldItem) fieldList2.get(j);
			    String itemid = fieldItem.getItemid();
			   String itemdesc = fieldItem.getItemdesc();
			  String type = fieldItem.getItemtype();
			    if(!"B00".equalsIgnoreCase(itemid)&&!"B01".equalsIgnoreCase(itemid)){
			 
					CommonData dataobj = new CommonData(itemid+":"+itemdesc,itemid+":"+itemdesc);
					dylist.add(dataobj);
				}
			}
	
	
		
		return dylist;
	}
	/**
	 * 根据itemid字段和salaryid获取子标计算公式值
	 * @return String 
	 * @throws GeneralException
	 */
	public String formulavalue(Connection conn,String setid,String itemid){
		String formula = "";
		ContentDAO dao = new ContentDAO(conn);
		String sqlstr = "select rexpr from bonusformula  where setid='"+setid+"' and itemid="+itemid;
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
	/**
	 * 根据itemid字段和salaryid获取子标计算公式条件
	 * @return String 
	 * @throws GeneralException
	 */
	public String formulacond(Connection conn,String setid,String itemid){
		String cond = "";
		ContentDAO dao = new ContentDAO(conn);
		String sqlstr = "select cond from bonusformula  where setid='"+setid+"' and itemid="+itemid;
		ArrayList dylist = null;
		try {
			dylist = dao.searchDynaList(sqlstr);
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				cond = dynabean.get("cond").toString();
			}
		} catch(GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cond;
	}
	public String pkgId(Connection conn,String id){
		String pkgid = "";
		ContentDAO dao = new ContentDAO(conn);
		String sqlstr = "select max(pkg_id) as pkgid from gz_stand_history  where id='"+id+"'";
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
	public void alertUseflag(Connection conn,String useflag,String setid,String itemid){
		ContentDAO dao=new ContentDAO(conn);
		String updatesql = "update bonusformula set useflag="+useflag+" where setid='"+setid+"' and itemid="+itemid;

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
	public String runFlag(Connection conn,String setid,String itemid){
		String runflag = "";
		ContentDAO dao = new ContentDAO(conn);
		String sqlstr = "select runflag from bonusformula  where setid='"+setid+"' and itemid="+itemid;
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
	public void alertRunflag(Connection conn,String runflag,String setid,String itemid){
		ContentDAO dao=new ContentDAO(conn);
		String updatesql = "update bonusformula set runflag="+runflag+" where setid='"+setid+"' and itemid="+itemid;

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
	public String standId(Connection conn,String setid,String itemid){
		String standid = "";
		ContentDAO dao = new ContentDAO(conn);
		String sqlstr = "select standid from bonusformula  where setid='"+setid+"' and itemid="+itemid;
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
	public String[] itemSortid(ContentDAO dao,String setid){
		String[] standid = {"0","0"};
		String sqlstr = "select max(itemid) as itemid,max(sortid) as sortid from bonusformula ";
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
	public ArrayList sortList(Connection conn,String setid,String fmode){
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(conn);
		String sqlstr = "select itemid,itemname,hzname from bonusformula where setid='"+setid+"'and fmode="+fmode+" order by sortid,itemid desc";
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
	public ArrayList statList() {
		ArrayList statlist = new ArrayList();
		CommonData obj = new CommonData("0", ResourceFactory
				.getProperty("org.maip.number.seek"));
		statlist.add(obj);
		obj = new CommonData("1", ResourceFactory.getProperty("kq.formula.sum"));
		statlist.add(obj);
		obj = new CommonData("2", ResourceFactory.getProperty("kq.formula.min"));
		statlist.add(obj);
		obj = new CommonData("3", ResourceFactory.getProperty("kq.formula.max"));
		statlist.add(obj);
		obj = new CommonData("4", ResourceFactory
				.getProperty("kq.formula.average"));
		statlist.add(obj);

		return statlist;
	}
	/**
	 * 根据子标获取代码
	 * @return ArrayList 
	 * @throws Exception
	 */
	public ArrayList codeList(Connection conn,String itemid){
		ArrayList list = new ArrayList();
		
		if(itemid==null||itemid.length()<1){
			CommonData dataobj = new CommonData("","");
			list.add(dataobj);
			return list;
		}
		
		FieldItem fielditem = (FieldItem)DataDictionary.getFieldItem(itemid);
		
		String codesetid ="";
		
		try {
			if(fielditem!=null){
				codesetid = fielditem.getCodesetid();
				if(fielditem.isCode()||codesetid.trim().length()>0){
					if(codesetid!=null||codesetid.trim().length()>0){
						StringBuffer sqlstr = new StringBuffer();
						if("@K".equalsIgnoreCase(codesetid)|| "UM".equalsIgnoreCase(codesetid)|| "UN".equalsIgnoreCase(codesetid)){
							sqlstr.append("select codeitemid,codeitemdesc from organization where codesetid='"); 
							sqlstr.append(codesetid);
							sqlstr.append("' order by codeitemid");
						}else if("@@".equalsIgnoreCase(codesetid)){
							sqlstr.append("select Pre as codeitemid,DBName as codeitemdesc from dbname");
						}else{
							sqlstr.append("select codeitemid,codeitemdesc from codeitem where codesetid='"); 
							sqlstr.append(codesetid);
							sqlstr.append("' order by codeitemid");
						}
						ArrayList dylist = null;
						ContentDAO dao = new ContentDAO(conn);

						dylist = dao.searchDynaList(sqlstr.toString());

						for(Iterator it=dylist.iterator();it.hasNext();){
							DynaBean dynabean=(DynaBean)it.next();
							String codeitemid = dynabean.get("codeitemid").toString();
							String codeitemdesc = dynabean.get("codeitemdesc").toString();
							CommonData dataobj = new CommonData(codeitemid,codeitemid+":"+codeitemdesc);
							list.add(dataobj);
						}
						CommonData dataobj = new CommonData("","");
						list.add(0,dataobj);

					}else{
						CommonData dataobj = new CommonData("","");
						list.add(dataobj);
					}
				}else{
					CommonData dataobj = new CommonData("","");
					list.add(dataobj);
				}
			}else{
				CommonData dataobj = new CommonData("","");
				list.add(dataobj);
			}
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
}
