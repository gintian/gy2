package com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject;

import com.hjsj.hrms.businessobject.gz.GzContant;
import com.hjsj.hrms.businessobject.gz.SalaryLProgramBo;
import com.hjsj.hrms.businessobject.gz.templateset.GzItemVo;
import com.hjsj.hrms.businessobject.gz.templateset.SalaryStandardBo;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo;
import com.hjsj.hrms.module.gz.utils.SalarySetBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *<p>Title:薪资业务基础类</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2015-7-22</p> 
 *@author dengc
 *@version 7.x
 */
 
public class SalaryTemplateBo implements GzContant{
	private Connection conn=null;
	/**薪资表名称*/
	private String gz_tablename;
	/**登录用户*/
	private UserView userview;
	/**薪资控制参数*/
	private SalaryCtrlParamBo ctrlparam=null;
	/**薪资类别数据对象*/
	private RecordVo templatevo=null; 	
	private String   manager="";  //工资管理员，对共享类别有效;  
	private int salaryid = -1;
	public int getSalaryid() {
		return salaryid;
	}

	public void setSalaryid(int salaryid) {
		this.salaryid = salaryid;
	}
	private String _withNoLock="";//解决sqlserver并发\死锁问题
	
	public SalaryTemplateBo(Connection conn,UserView userview) {
		this.conn = conn; 
		this.userview=userview;
		if(Sql_switcher.searchDbServer()!=2) //针对SQLSERVER 无需考虑锁表
			_withNoLock=" WITH(NOLOCK) ";
	}
	
	public SalaryTemplateBo(Connection conn, int salaryid,UserView userview) {
		this.conn = conn; 
		this.userview=userview;
		this.salaryid = salaryid;
		ctrlparam=new SalaryCtrlParamBo(this.conn,salaryid);
		this.manager=ctrlparam.getValue(SalaryCtrlParamBo.SHARE_SET, "user");
		if(Sql_switcher.searchDbServer()!=2) //针对SQLSERVER 无需考虑锁表
			_withNoLock=" WITH(NOLOCK) ";
		initData(salaryid); 
	}
	
	/**
	 * 薪资类别数据初始化
	 */
	private void initData(int salaryid)
	{
	  try
	  {
		if(this.manager.length()==0)
			this.gz_tablename=this.userview.getUserName()+"_salary_"+salaryid;
		else
			this.gz_tablename=this.manager+"_salary_"+salaryid;
		templatevo=new RecordVo("salarytemplate");
		templatevo.setInt("salaryid",salaryid);
		ContentDAO dao=new ContentDAO(this.conn);
		templatevo=dao.findByPrimaryKey(templatevo); 
	  }
	  catch(Exception ex)
	  {
		  ex.printStackTrace();
	  }
	}
	
 
	
	/** 
	 * @deprecated:获得薪资帐套下人员数据范围权限
	 * @param tabname  表名
	 * @param flag  true:考虑薪资帐套的归属单位、部门   false:不考虑
	 * @author dengc
	 * @date 2015-7-8
	 * @return 返回SQL条件
	 */
	public String getWhlByUnits(String tabname,boolean flag)
	{
		
		String whl_str="";
		try
		{
			StringBuffer whl=new StringBuffer(""); 
			String unitIdByBusiOutofPriv = SystemConfig.getPropertyValue("unitIdByBusiOutofPriv");
			if(manager!=null && manager.length()>0 && !manager.equalsIgnoreCase(this.userview.getUserName()) && !this.userview.isSuper_admin() && unitIdByBusiOutofPriv!=null&& "1".equals(unitIdByBusiOutofPriv)){
				whl = getPrivSQL(tabname);
			}else{
				String orgid="";
				String deptid="";
				
				if(flag)
				{
					orgid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD,"orgid"); //归属单位
					orgid = orgid != null ? orgid : "";
					deptid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid"); //归属部门
					deptid = deptid != null ? deptid : ""; 
				}
				if(StringUtils.isBlank(orgid)&&StringUtils.isBlank(deptid))
				{
					orgid="b0110";
					deptid="e0122";
				}
				String unitcodes = this.userview.getUnitIdByBusiOutofPriv("1");
				//String unitcodes=this.userview.getUnitIdByBusi("1");  //UM010101`UM010105` 
				if(StringUtils.isNotBlank(unitcodes) && "UN`".equalsIgnoreCase(unitcodes))
				{
					return "";
				}
				else
				{
					if(StringUtils.isBlank(unitcodes)|| "UN".equalsIgnoreCase(unitcodes))
					{
						whl = getPrivSQL(tabname);
						if(whl.length()>3) {
							whl_str = " and ( " + whl.substring(3) + " )  ";
						}
						return whl_str;
					} 
					String[] temps=unitcodes.split("`");
					if(orgid.trim().length()>0&&deptid.trim().length()>0)
					{
						for(int i=0;i<temps.length;i++)
						{
							if(temps[i].trim().length()>0)
							{
								 whl.append(" or "+tabname+"."+deptid+" like '"+temps[i].substring(2)+"%' ");
								 
								 if("UN".equals(temps[i].substring(0,2)))
								 {
									 whl.append(" or "+tabname+"."+orgid+" like '"+temps[i].substring(2)+"%' ");
								 }
							}
						}
					}
					else if(orgid.trim().length()>0)
					{
						for(int i=0;i<temps.length;i++)
						{
							if(temps[i].trim().length()>0)
							{
								if("UN".equals(temps[i].substring(0,2)))
								{
									 whl.append(" or "+tabname+"."+orgid+" like '"+temps[i].substring(2)+"%' ");
								}
							}
						} 
					}
					else if(deptid.trim().length()>0)
					{
						for(int i=0;i<temps.length;i++)
						{
							if(temps[i].trim().length()>0)
							{
								 whl.append(" or "+tabname+"."+deptid+" like '"+temps[i].substring(2)+"%' ");
							}
						}
					}
					
					if(whl.length()==0)
						return " and 1=2 ";
				} 
			}
			if(whl.length()>0)
				whl_str=" and ( "+whl.substring(3)+" ) ";
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return whl_str;
	}
	/**
	 *薪资分析-薪资历史数据获取业务范围权限sql
	 * @param salaryId
	 * @return
	 */
	public String getUnitsPrivSql() {
		StringBuffer privSql = new StringBuffer();
		try {
			ArrayList list = this.userview.getPrivDbList();
			String b_units = this.userview.getUnitIdByBusi("1");
			String clientName = SystemConfig.getPropertyValue("clientName");
			if (clientName != null && "weichai".equalsIgnoreCase(clientName)) {
				b_units = this.userview.getUnit_id();
			}
			if (this.userview.isSuper_admin() || "1".equals(this.userview.getGroupId()) || StringUtils.equalsIgnoreCase("UN`", b_units)) {
				return " and 1=1 ";
			}
			if (list == null || list.size() < 1 || StringUtils.isEmpty(b_units) || StringUtils.equalsIgnoreCase("UN",b_units)) {
				return " and 1=2";
			}
			String b0110_item = "b0110";
			String e0122_item = "e0122";
			b0110_item = "b0110";
			e0122_item = "e0122";
			String orgid = ctrlparam.getValue(com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo.SUM_FIELD, "orgid"); //归属单位
			String deptid = ctrlparam.getValue(com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo.SUM_FIELD, "deptid");//归属部门

			if (orgid != null && orgid.trim().length() > 0) {
				b0110_item = orgid;
				if (deptid != null && deptid.trim().length() > 0) {
					e0122_item = deptid;
				} else {
					e0122_item = "";
				}
			} else if (deptid != null && deptid.trim().length() > 0) {
				e0122_item = deptid;
				b0110_item = "";
			}
			String[] unitarr = b_units.split("`");
			privSql.append(" and(");
			for (int i = 0; i < unitarr.length; i++) {
				String codeid = unitarr[i];
				if (StringUtils.isEmpty(codeid)){
					continue;
				}
				if (codeid.trim().length() > 2) {
					if ("UN".equalsIgnoreCase(codeid.substring(0, 2))) {
						if (b0110_item.length() > 0){
							privSql.append(" or " + b0110_item + " like '" + codeid.substring(2) + "%' ");
						}
						else{
							privSql.append(" or " + e0122_item + " like '" + codeid.substring(2) + "%' ");
						}
					} else if ("UM".equalsIgnoreCase(codeid.substring(0, 2)) && e0122_item.length() > 0) {
						privSql.append(" or " + e0122_item + " like '" + codeid.substring(2) + "%' ");
					}
				}
			}
			privSql.append(")");
			privSql = privSql.delete(5,8);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return privSql.toString();
	}
	/**
	 * 走人员范围加高级
	 * @param tabname
	 * @return
	 */
	private StringBuffer getPrivSQL(String tabname) {
		StringBuffer whl=new StringBuffer(""); 
		try {
			tabname = tabname.toLowerCase();//防止误传大写字符过来，这样，indexof就无效了，导致权限控制错误
			/**导入数据*/
			String dbpres=getTemplatevo().getString("cbase");
			/**应用库前缀*/
			String[] dbarr=StringUtils.split(dbpres, ",");
			for(int i=0;i<dbarr.length;i++)
			{
				String pre=dbarr[i];
				if(!this.userview.isSuper_admin()&&this.userview.getDbpriv().toString().toLowerCase().indexOf(","+pre.toLowerCase()+",")==-1)
				{
					if(tabname.indexOf("salary")!=-1) 
						whl.append(" or (upper("+tabname+".nbase)='"+pre.toUpperCase()+"' and 1=2)");
					else 
						whl.append(" or 1=2");
				}
				else
				{
					if(tabname.indexOf("salary")!=-1||tabname.indexOf("t#")!=-1)
						whl.append(" or (upper("+tabname+".nbase)='"+pre.toUpperCase()+"' and upper(" + tabname + ".a0100) in (select a0100 "+this.userview.getPrivSQLExpression(pre, false)+" ) )");
					else if(tabname.indexOf(pre.toLowerCase())!=-1)//只取自己人员库前缀的a0100
						whl.append(" or (upper(" + tabname + ".a0100) in (select a0100 "+this.userview.getPrivSQLExpression(pre, false)+" ) )");
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return whl;
	}
	
	/**
	 * 获得薪资帐套临时表的表结构；
	 * @return
	 */
	public String  getSalaryFieldStr()
	{
			StringBuffer itemStr=new StringBuffer("");
			ArrayList itemList=getSalaryItemList("",this.templatevo.getInt("salaryid")+"", 1);
			for(Iterator<LazyDynaBean> t=itemList.iterator();t.hasNext();)
			{
				LazyDynaBean abean=(LazyDynaBean)t.next();
				String itemid=((String)abean.get("itemid")).toUpperCase();
				itemStr.append("/"+itemid);
			}
			itemStr.append("/SP_FLAG/SP_FLAG2/USERFLAG/APPPROCESS/");			
			return itemStr.toString();
	}
	
	
	
	
	/**
	 * 根据工资类别id得到类别下面的所有项目列表
	 * @param str_where 查询条件
	 * @param salaryid  薪资帐套号
	 * @param returnFlag 返回对象类型  1: abean  2:Fielditem 3:GzItemVo
	 * @author dengcan
	 * @return
	 */
	public ArrayList getSalaryItemList(String str_where,String salaryid,int returnFlag)
	{
		ArrayList list=new ArrayList();
		RowSet rowSet=null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer buf = new StringBuffer("select * from salaryset where salaryid=?");
			if(str_where!=null&&!"".equals(str_where))
				buf.append(" and ("+str_where+")"); 
			buf.append(" order by sortid");
			ArrayList valueList=new ArrayList();
			valueList.add(new Integer(salaryid));
			rowSet=dao.search(buf.toString(),valueList);
			LazyDynaBean abean=null;
			while(rowSet.next())
			{
				if(returnFlag==1)
				{
					abean=new LazyDynaBean();
					abean.set("salaryid",rowSet.getString("salaryid"));
					abean.set("fieldid",rowSet.getString("fieldid"));
					abean.set("itemdesc",rowSet.getString("itemdesc")!=null?rowSet.getString("itemdesc"):"");
					abean.set("initflag",rowSet.getString("initflag")!=null?rowSet.getString("initflag"):"0"); 
					abean.set("fieldsetid",rowSet.getString("fieldsetid")!=null?rowSet.getString("fieldsetid"):"");
					abean.set("heapflag",rowSet.getString("heapflag")!=null?rowSet.getString("heapflag"):"0");
					abean.set("changeflag",rowSet.getString("changeflag")!=null?rowSet.getString("changeflag"):"");
					abean.set("itemtype",rowSet.getString("itemtype"));
					abean.set("decwidth",rowSet.getString("decwidth"));
					abean.set("codesetid",rowSet.getString("codesetid"));
					abean.set("itemid",rowSet.getString("itemid"));
					abean.set("nlock",rowSet.getString("nlock"));
					abean.set("nwidth",rowSet.getString("nwidth"));
					abean.set("itemlength",rowSet.getString("itemlength"));
					abean.set("formula",Sql_switcher.readMemo(rowSet, "formula")); 
					list.add(abean);
				}
				else if(returnFlag==2)
				{
					FieldItem item=new FieldItem();
					item.setFieldsetid(rowSet.getString("fieldsetid"));
					item.setItemid(rowSet.getString("itemid"));
					item.setItemdesc(rowSet.getString("itemdesc"));
					item.setItemtype(rowSet.getString("itemtype"));
					item.setItemlength(rowSet.getInt("itemlength"));
					item.setDisplaywidth(rowSet.getInt("nwidth"));
					item.setDecimalwidth(rowSet.getInt("decwidth"));
					item.setCodesetid(rowSet.getString("codesetid"));
					item.setFormula(Sql_switcher.readMemo(rowSet,"formula"));
					item.setVarible(0);
					list.add(item);
				}else if(returnFlag==3){
					GzItemVo itemvo = new GzItemVo();
					itemvo.setSetname(rowSet.getString("fieldsetid"));
					itemvo.setFldname(rowSet.getString("itemid"));
					itemvo.setFldtype(rowSet.getString("Itemtype"));
					itemvo.setCodeid(rowSet.getString("codesetid"));
					itemvo.setHz(rowSet.getString("itemdesc"));
					itemvo.setFlddec(rowSet.getInt("Decwidth"));
					itemvo.setLen(rowSet.getInt("Itemlength"));
					itemvo.setInitflag(rowSet.getInt("Initflag"));
					itemvo.setHeapflag(rowSet.getInt("Heapflag"));
					itemvo.setLock(rowSet.getInt("Nlock"));
					itemvo.setChangeflag(rowSet.getInt("changeflag"));
					itemvo.setFormula(Sql_switcher.readMemo(rowSet, "formula"));
					list.add(itemvo);
				}
			} 
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			PubFunc.closeDbObj(rowSet);
		}
		return list;
	}

	
	
	/**
	 * 求对应的每个子集中指标项目列表
	 * @param setlist        提交工资项目涉及的子集
	 * @param gzitemlist  工资项目
	 * @return
	 */
	public ArrayList getUpdateFields(ArrayList setlist,ArrayList gzitemlist)
	{
		ArrayList fieldlist=new ArrayList();
		try
		{
			String subNoPriv=getLprogramAttri("subNoPriv",SalaryLProgramBo.CONFIRM_TYPE); // //数据提交入库不判断子集及指标权限
			if(subNoPriv==null||subNoPriv.trim().length()==0)
				subNoPriv="0";
			String temp_str="'B0110','A00Z1','A00Z0','A00Z2','A00Z3','A0000','A0100','NBASE','A01Z0','A0101','E0122','E01A1'";			
			LazyDynaBean abean=null;
			for(int i=0;i<setlist.size();i++)
			{
				StringBuffer buf=new StringBuffer();
				for(int j=0;j<gzitemlist.size();j++)
				{
					abean=(LazyDynaBean)gzitemlist.get(j); 
					String itemid=(String)abean.get("itemid");
					if(temp_str.indexOf("'"+itemid.toUpperCase()+"'")==-1)
					{
						FieldItem _tempItem=DataDictionary.getFieldItem(itemid.toLowerCase());
						if(_tempItem==null)
							continue; 
					} 
					if("3".equals((String)abean.get("initflag"))&&!"A01Z0".equalsIgnoreCase(itemid))
						continue; 
					FieldItem fielditem=DataDictionary.getFieldItem(itemid.toLowerCase());
					if(fielditem==null|| "0".equalsIgnoreCase(fielditem.getUseflag()))
						continue;
					if("0".equals(subNoPriv))
					{
						if(!"2".equalsIgnoreCase(this.userview.analyseFieldPriv(itemid))) //判断是否有写权限
							continue;
					}
					if(fielditem.getFieldsetid().equalsIgnoreCase((String)setlist.get(i)))
					{
						buf.append(itemid);
						buf.append(",");
					}
				}	//for j end.
				fieldlist.add(buf.toString());
			}//for i end.			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return fieldlist;
	}


	/**
	 * 取得当前薪资类别计算公式列表
	 * @param flag =1 (有效计算公式) =-1全部的计算公式
	 * @param itemids:选择的公式id
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getFormulaList(int flag,String salaryid,ArrayList itemids)throws GeneralException
	{
		ArrayList list=new ArrayList();
		StringBuffer buf=new StringBuffer();
		RowSet rset=null;
		try
		{
			ArrayList dataList=new ArrayList();
			buf.append("select hzName,itemname,useflag,itemid,rexpr,cond,standid,itemtype,runflag from salaryformula  where salaryid=? "); 
			/**过滤有效的计算公式*/
			if(flag==1)
				buf.append(" and useflag=1");
			dataList.add(Integer.valueOf(salaryid));
			
			if(itemids!=null&&itemids.size()>0)
			{
				StringBuffer str=new StringBuffer("");
				for(int i=0;i<itemids.size();i++)
				{
					String itemid=(String)itemids.get(i);
					str.append(",?");
					dataList.add(Integer.valueOf(itemid));
				}
				buf.append(" and itemid in ("+str.substring(1)+")");
			}
			buf.append(" order by salaryid,sortid");
			ContentDAO dao=new ContentDAO(this.conn);
			rset=dao.search(buf.toString(),dataList);
			list=dao.getDynaBeanList(rset);
			 
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		finally
		{
			PubFunc.closeDbObj(rset);
		}
		return list;
	}
	
	
	/**
	 * 临时变量公式调用其它临时变量时，也需考虑放入 varMap集合中 (递归) 
 	 * @param midList  临时变量集合
	 * @param formular_str 临时变量公式
	 * @param varMap  存放公式中用到的其它临时变量id 
	 */
	private void searchVar(ArrayList midList, String formualr_str, HashMap varMap) {
		FieldItem item;
		for (int j = 0; j < midList.size(); j++) {
			item = (FieldItem) midList.get(j);
			String item_id = item.getItemid();//.toLowerCase();
			String item_desc = item.getItemdesc().trim().toLowerCase();
			String formula = item.getFormula();
			if (formualr_str.toLowerCase().indexOf(item_desc) != -1
					&& varMap.get(item_id) == null) {
				varMap.put(item_id, "1");
				searchVar(midList, formula, varMap);

			} 
		} 
	}
	
	
	/**
	 * 查询标准表指标列表
	 * @param salaryid  薪资类别号
	 * @return
	 * @throws GeneralException
	 */
	public  ArrayList searchStdTableFieldList(int salaryid)throws GeneralException
	{
		ArrayList fieldlist=new ArrayList();
		try
		{
			ArrayList stdlist=new ArrayList();
			StringBuffer buf=new StringBuffer();
			/**查询执行标准的计算公式*/
			buf.append("select standid from salaryformula where salaryid=?  and standid>0 and runflag=1");
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rset=dao.search(buf.toString(),Arrays.asList(new Object[] {Integer.valueOf(salaryid)}));
			while(rset.next())
				stdlist.add(rset.getString("standid"));
			if(stdlist.size()==0)
				return fieldlist;
			if(rset!=null)
				rset.close();
			StringBuffer stdbuf=new StringBuffer();
			for(int i=0;i<stdlist.size();i++)
			{
				stdbuf.append(stdlist.get(i));
				stdbuf.append(",");
			}//for i loop end.
			stdbuf.setLength(stdbuf.length()-1);
			buf.setLength(0);
			/**薪资标准表*/
			buf.append("select id from gz_stand where id in(");
			buf.append(stdbuf.toString());
			buf.append(")");
			rset=dao.search(buf.toString());
			while(rset.next())
			{
				//...薪资标准表涉及到指标列表				
				SalaryStandardBo stdbo=new SalaryStandardBo(this.conn,rset.getString("id"),"");
				fieldlist.addAll(stdbo.getGzStandFactorList(1));
				fieldlist.addAll(stdbo.getGzStandFactorList(2));

			}// while loop end.
			rset.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return fieldlist;
	}
	
	
	/**
	 * 获得薪资帐套的临时变量和共享临时变量
	 * @param salaryid 帐套ID
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getMidVariableList(String salaryid)throws GeneralException
	{
		ArrayList fieldlist=new ArrayList(); 
		RowSet rowSet=null;
		try
		{
				LazyDynaBean abean=null;
				ContentDAO dao=new ContentDAO(this.conn);
				StringBuffer buf=new StringBuffer();
				buf.append("select count(cname) as num,cname,max(chz) as chz from ");
				buf.append(" midvariable where nflag=0 and templetid=0 ");
				buf.append(" and (cstate is null or cstate=?) group by cname having count(cname)>1");
				rowSet=dao.search(buf.toString(),Arrays.asList(new Object[] {Integer.valueOf(salaryid)}));
				buf.setLength(0);
				while(rowSet.next()){
					if(buf.length()==0)
						buf.append("临时变量 ");
					buf.append("\""+rowSet.getString("chz")+"\",");
				}
				if(buf.length()>0){
					buf.deleteCharAt(buf.length()-1);
					buf.append("重复定义，请删除重复项！");
					throw GeneralExceptionHandler.Handle(new Exception(buf.toString()));
				}

				buf.append("select cname,chz,ntype,cvalue,fldlen,flddec,codesetid from ");
				buf.append(" midvariable where nflag=0 and templetid=0 ");
				buf.append(" and (cstate is null or cstate=?) order by sorting");
				rowSet=dao.search(buf.toString(),Arrays.asList(new Object[] {Integer.valueOf(salaryid)}));
				while(rowSet.next())
				{
					abean=new LazyDynaBean();
					abean.set("cname",rowSet.getString("cname")!=null?rowSet.getString("cname"):"");
					abean.set("chz",rowSet.getString("chz")!=null?rowSet.getString("chz"):"");
					abean.set("ntype",rowSet.getString("ntype")!=null?rowSet.getString("ntype"):"");
					abean.set("cvalue",Sql_switcher.readMemo(rowSet, "cvalue")); 
					abean.set("fldlen",rowSet.getString("fldlen")!=null?rowSet.getString("fldlen"):"");
					abean.set("flddec",rowSet.getString("flddec")!=null?rowSet.getString("flddec"):"");
					abean.set("codesetid",rowSet.getString("codesetid")!=null?rowSet.getString("codesetid"):""); 
					fieldlist.add(abean);
				}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		finally
		{
			 PubFunc.closeDbObj(rowSet);
		}			
		return fieldlist; 
	}
	
	
	
	/**
	 * 从临时变量中取得对应指标列表
	 * @return FieldItem对象列表
	 * @throws GeneralException
	 */
	public ArrayList getMidVarItemList(String salaryid)throws GeneralException
	{
		ArrayList fieldlist=new ArrayList();
		ArrayList new_fieldList=new ArrayList();
		RowSet rset=null;
		try
		{
			StringBuffer buf=new StringBuffer();
			buf.append("select cname,chz,ntype,cvalue,fldlen,flddec,codesetid from ");
			buf.append(" midvariable where nflag=0 and templetid=0 ");
			buf.append(" and (cstate is null or cstate=? ) order by sorting");
			ContentDAO dao=new ContentDAO(this.conn);
			rset=dao.search(buf.toString(),Arrays.asList(new Object[] {Integer.valueOf(salaryid)}));
			while(rset.next())
			{
				FieldItem item=new FieldItem();
				item.setItemid(rset.getString("cname"));
				item.setFieldsetid(/*"A01"*/"");//没有实际含义
				item.setItemdesc(rset.getString("chz"));
				item.setItemlength(rset.getInt("fldlen"));
				item.setDecimalwidth(rset.getInt("flddec"));
				item.setFormula(Sql_switcher.readMemo(rset, "cvalue"));
				item.setCodesetid(rset.getString("codesetid"));
				switch(rset.getInt("ntype"))
				{
				case 1://
					item.setItemtype("N");
					break;
				case 2:
				case 4://代码型					
					item.setItemtype("A");
					break;
				case 3:
					item.setItemtype("D");
					break;
				}
				item.setVarible(1);
				fieldlist.add(item);
			}// while loop end.
			
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		finally
		{
			PubFunc.closeDbObj(rset);
		}			
		return fieldlist;

	}
	
	
	
			
	
	/**
	 * 新建临时表时，获得临时变量指标列表（过滤薪资帐套不用的临时变量）
	 *  @param salaryid 帐套ID
	 * @return FieldItem对象列表
	 * @throws GeneralException
	 */
	public ArrayList getMidVariableListByTable(String salaryid)throws GeneralException
	{
		ArrayList fieldlist=new ArrayList();
		ArrayList new_fieldList=new ArrayList(); 
		RowSet rset=null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			LazyDynaBean abean=null;
			ArrayList midvaliableList=getMidVariableList(salaryid);
			StringBuffer buf=new StringBuffer(); 
			for(int i=0;i<midvaliableList.size();i++)
			{
				abean=(LazyDynaBean)midvaliableList.get(i);
				FieldItem item=new FieldItem();
				item.setItemid((String)abean.get("cname"));
				item.setFieldsetid(/*"A01"*/"");//没有实际含义
				item.setItemdesc((String)abean.get("chz"));
				item.setItemlength(Integer.parseInt((String)abean.get("fldlen")));
				item.setDecimalwidth(Integer.parseInt((String)abean.get("flddec")));
				item.setFormula((String)abean.get("cvalue"));
				item.setCodesetid((String)abean.get("codesetid"));
				int ntype=Integer.parseInt((String)abean.get("ntype"));
				switch(ntype)
				{
				case 1://
					item.setItemtype("N");
					break;
				case 2:
				case 4://代码型					
					item.setItemtype("A");
					break;
				case 3:
					item.setItemtype("D");
					break;
				}
				item.setVarible(1);
				fieldlist.add(item);
			}// while loop end.
			
			HashMap varMap = new HashMap();
			//过滤薪资类别  计算公式用不到的临时变量
			ArrayList formulaList=getFormulaList(1,salaryid,null);
			FieldItem item=null;
			HashMap map=new HashMap();
			for(int i=0;i<formulaList.size();i++)
			{
				  DynaBean dbean=(LazyDynaBean)formulaList.get(i);
	              String formula=((String)dbean.get("rexpr")).toLowerCase();
	              String cond=((String)dbean.get("cond")).toLowerCase();
	              for(int j=0;j<fieldlist.size();j++)
	              {
	            	  item=(FieldItem)fieldlist.get(j);
	            	  String item_id=item.getItemid(); //.toLowerCase();
	            	  if(item.getItemdesc()==null)
	            		  continue;
	            	  String item_desc=item.getItemdesc().trim().toLowerCase();
	            	  if(formula.indexOf(item_desc)!=-1&&map.get(item_id)==null)  //公式
	            	  { 
	            		  varMap.put(item_id, "1");
	            		  searchVar(fieldlist,item.getFormula(),varMap); // 需考虑临时变量调用临时变量
	            		  map.put(item_id, "1");
	            	  }
	            	  
	            	  if(cond!=null&&cond.trim().length()>0)  //条件
	            	  {
		            	  if(cond.indexOf(item_desc)!=-1&&map.get(item_id)==null)
		            	  { 
		            		  varMap.put(item_id, "1");
		            		  searchVar(fieldlist,item.getFormula(),varMap); // 需考虑临时变量调用临时变量
		            		  map.put(item_id, "1");
		            	  }
	            	  }
	              }
			}
			
			
			
			if (varMap.size() > 0) {
				Set keySet = varMap.keySet();
				StringBuffer _str = new StringBuffer("");
				for (Iterator t = keySet.iterator(); t.hasNext();) {
					_str.append(",'" + (String) t.next() + "'");
				}
				
				buf.setLength(0);
				buf.append("select cname,chz,ntype,cvalue,fldlen,flddec,codesetid from ");
				buf.append(" midvariable where nflag=0 and templetid=0 ");
				buf.append(" and (cstate is null or cstate=? )   and cname in ("+ _str.substring(1) + ")  order by sorting");
				
				rset = dao.search(buf.toString(),Arrays.asList(new Object[] {Integer.valueOf(salaryid)}));
				while (rset.next()) {
					item = new FieldItem();
					item.setItemid(rset.getString("cname"));
					item.setFieldsetid("");// 没有实际含义
					item.setItemdesc(rset.getString("chz"));
					item.setItemlength(rset.getInt("fldlen"));
					item.setDecimalwidth(rset.getInt("flddec"));
					item.setFormula(Sql_switcher.readMemo(rset, "cvalue"));
					item.setCodesetid(rset.getString("codesetid"));
					switch (rset.getInt("ntype")) {
					case 1://
						item.setItemtype("N");
						break;
					case 2:
					case 4:// 代码型
						item.setItemtype("A");
						break;
					case 3:
						item.setItemtype("D");
						break;
					}
					item.setVarible(1);
					new_fieldList.add(item);
				}
			} 
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		finally
		{
			PubFunc.closeDbObj(rset);
		}			 
		return new_fieldList;
	}
	
	
	
	/**
	 * 查询薪资类别项目来源的子集列表
	 * @param salaryid 薪资帐套ID
	 * @return
	 */
	public ArrayList searchSetList(int salaryid)
	{
		  ArrayList list=new ArrayList();
		  StringBuffer buf=new StringBuffer();
		  buf.append("select fieldsetid from salaryset where salaryid=?");
		  buf.append(" group by fieldsetid order by fieldsetid");
		  ArrayList paralist=new ArrayList();
		  paralist.add(new Integer(salaryid));
		  RowSet rset=null;
		  try
		  {
			  ContentDAO dao=new ContentDAO(this.conn);
			  rset=dao.search(buf.toString(),paralist);
			  while(rset.next())
			  {
				  list.add(rset.getString("fieldsetid").toUpperCase());
			  }//
		  }
		  catch(Exception ex)
		  {
			  ex.printStackTrace();
		  }		  
		  finally
		  {
				PubFunc.closeDbObj(rset);
		  }			  
		  return list;
	}
	
	/**
	 * 判断表中是否有记录
	 * @param tablename 表名
	 * @param key  统计字段名
	 * @param where  筛选条件
	 * @return   true 有记录
	 */
	public boolean  hasRecordByTable(String tablename,String key,String where)
	{
		RowSet rset=null;
		boolean flag=false;
		try
		{
			 
			  ContentDAO dao=new ContentDAO(this.conn);
			  rset=dao.search("select count("+key+")  from "+tablename+where);
			  if(rset.next())
			  {
				 int  count=rset.getInt(1);
				 if(count>0)
					 flag=true;
			  }//
		 }
		 catch(Exception ex)
		 {
			  ex.printStackTrace();
		 }		  
		 finally
		 {
				PubFunc.closeDbObj(rset);
		 }			
		 return flag;
	}
	 
	/**
	 * @Title: getFilterAndPrivSql 
	 * @Description: 获得薪资发放前台过滤条件和当前用户的可操作范围SQL 
	 * @return String
	 * @author lis  
	 * @date 2015-8-10 上午09:52:04
	 */
	public String getFilterAndPrivSql_ff()
	{
		StringBuffer strwhere=new StringBuffer();
		strwhere.append(getfilter(this.gz_tablename)); //获取表格工具过滤以及页面模糊查询返回的sql片段 
		if(manager!=null&&manager.length()>0&&!this.userview.getUserName().equalsIgnoreCase(manager))
			strwhere.append(getWhlByUnits(this.gz_tablename,true));
		return strwhere.toString();
	}
	/**
	 * 获取表格工具过滤以及页面模糊查询返回的sql片段
	 * @param tableName 表名薪资发放为薪资临时表，审批为历史表。。。
	 * @return
	 * @author ZhangHua
	 * @date 17:33 2018/5/24
	 */
	public String getfilter(String tableName){
		StringBuffer filtersql = new StringBuffer();
		try{
			String subModuleId="";
			if (tableName.toLowerCase().indexOf("history") > -1) {
				subModuleId = "salarysp_" + this.salaryid;
			} else {
				subModuleId = "salary_" + this.salaryid;
			}

			TableDataConfigCache tableCache = (TableDataConfigCache)this.userview.getHm().get(subModuleId);
		    if(tableCache!=null)
		    {
			    if(tableCache.getFilterSql()!=null)//表格控件过滤条件
			    	filtersql.append(tableCache.getFilterSql());
			    if(tableCache.getQuerySql()!=null)//页面查询控件
			    	filtersql.append(tableCache.getQuerySql());

			    if(filtersql.length()>0){
			    	HashMap<String,FieldItem> map=tableCache.getQueryFields();
			    	String strSql=filtersql.toString();
					if(!"".equals(tableName)) {
						Iterator iter = map.entrySet().iterator();
						while (iter.hasNext()) {
							Map.Entry entry = (Map.Entry) iter.next();
							String key = String.valueOf(entry.getKey());
							//匹配不已字母和数字开头或结尾的目标字段
							strSql = strSql.replaceAll("(?<=[^A-Za-z0-9])"+key+"(?=[^A-Za-z0-9])", tableName + "." + key);

						}
					}
					filtersql.setLength(0);
					filtersql.append(strSql);
				}
		    }
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}	
		return filtersql.toString();
	}
	/**
	 * 获取薪资分析薪资历史数据表格工具过滤以及页面模糊查询返回的sql片段
	 * @return
	 */
	public String getHistoryFilter(String tableName){
		StringBuffer filtersql = new StringBuffer();
		try{
			String subModuleId="salaryHistory_" + this.salaryid;
			TableDataConfigCache tableCache = (TableDataConfigCache)this.userview.getHm().get(subModuleId);
			if(tableCache!=null)
			{
				if(tableCache.getFilterSql()!=null)//表格控件过滤条件
					filtersql.append(tableCache.getFilterSql());
				if(tableCache.getQuerySql()!=null)//页面查询控件
					filtersql.append(tableCache.getQuerySql());

				if(filtersql.length()>0){
					HashMap<String,FieldItem> map=tableCache.getQueryFields();
					String strSql=filtersql.toString();
					if(!"".equals(tableName)) {
						Iterator iter = map.entrySet().iterator();
						while (iter.hasNext()) {
							Map.Entry entry = (Map.Entry) iter.next();
							String key = String.valueOf(entry.getKey());
							//匹配不已字母和数字开头或结尾的目标字段
							strSql = strSql.replaceAll("(?<=[^A-Za-z0-9])"+key+"(?=[^A-Za-z0-9])", tableName + "." + key);

						}
					}
					filtersql.setLength(0);
					filtersql.append(strSql);
				}
			}
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return filtersql.toString();
	}
	/**
	 * @Title: getSalaryName 
	 * @Description:得到薪资类别名称 
	 * @param salaryid
	 * @return
	 * @author lis  
	 * @throws GeneralException 
	 * @date 2015-8-29 下午04:28:52
	 */
	public String getSalaryName(String salaryid) throws GeneralException{
		String str = "";
		try{
			ContentDAO dao=new ContentDAO(conn);
			String sql="select cname from SALARYTEMPLATE where SALARYID=?";
			RowSet rs = dao.search(sql,Arrays.asList(new Object[] {Integer.valueOf(salaryid)}));
			if(rs.next()){
				str = rs.getString("cname");
			}
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return str;
	}
	/**
	 * @Title: getTemplateName 
	 * @Description:得到人事异动模版名称 
	 * @param tabid
	 * @return str
	 * @author gaohy  
	 * @throws GeneralException 
	 * @date 2016-1-5 下午16:28:52
	 */
	public String getTemplateName(String tabid) throws GeneralException{
		String str = "";
		try{
			ContentDAO dao=new ContentDAO(conn);
			String sql="select Name from Template_table where TabId=?";
			RowSet rs = dao.search(sql,Arrays.asList(new Object[] {Integer.valueOf(tabid)}));
			if(rs.next()){
				str = rs.getString("Name");
			}
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return str;
	}
	/**
	 * 根据用户名获得关联用户的姓名，如没有关联用户则获得用户的全称，否则得到用户名
	 * @param username
	 * @author zhaoxg
	 * @serialData 2015-9-1
	 * @return
	 */
	public String getNameByUsername(String username)
	{
		String name=username;
		RowSet rowSet=null; 
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			rowSet=dao.search("select a0100,nbase,fullname   from operuser,usergroup where operuser.groupid=usergroup.groupid and username=? ",Arrays.asList(new Object[] {username}));
			if(rowSet.next())
			{
				String a0100=rowSet.getString("a0100")!=null?rowSet.getString("a0100").trim():"";
				String nbase=rowSet.getString("nbase")!=null?rowSet.getString("nbase").trim():"";
				String fullname=rowSet.getString("fullname")!=null?rowSet.getString("fullname").trim():""; 
				if(a0100.length()>0&&nbase.length()>0)
				{
					rowSet=dao.search("select a0101 from "+nbase+"A01 where a0100='"+a0100+"'");
					if(rowSet.next())
						name=rowSet.getString("a0101");
				}
				else if(fullname.length()>0)
				{
					name=fullname;
				}
				
			}
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return name;
	}
	/**
	 * 获得当前用户的组号，如果关联了自助用户，则为所在部门的名称->单位名称
	 * @param _userview
	 * @author zhaoxg
	 * @serialData 2015-9-1
	 * @return
	 */
	public String getGroupName(UserView _userview)
	{
		RowSet rowSet=null;
		String groupName="";
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			if(_userview.getA0100()!=null&&_userview.getA0100().trim().length()>0)
			{
				if(_userview.getUserDeptId().length()>0)
					groupName=AdminCode.getCodeName("UM",_userview.getUserDeptId());
				else if(_userview.getUserOrgId().length()>0)
					groupName=AdminCode.getCodeName("UN",_userview.getUserOrgId());
				if(groupName.length()==0){
					rowSet=dao.search("select b0110,e0122 from "+_userview.getDbname()+"A01 where a0100='"+_userview.getA0100()+"'");
					if(rowSet.next())
					{
						String b0110=rowSet.getString("b0110")!=null?rowSet.getString("b0110").trim():"";
						String e0122=rowSet.getString("e0122")!=null?rowSet.getString("e0122").trim():"";
						if(e0122.length()>0)
							groupName=AdminCode.getCodeName("UM",e0122);
						else if(b0110.length()>0)
							groupName=AdminCode.getCodeName("UN",b0110);
					}
				}
			}else{
				rowSet=dao.search("select groupName from operuser,usergroup where operuser.groupid=usergroup.groupid and username='"+_userview.getUserName()+"'");
				if(rowSet.next())
				{
					groupName=rowSet.getString(1);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return groupName;
	}
	
	/**
	 * 求移动电话号码
	 * @return
	 */
	public String getMobileNumber()
	{
        RecordVo vo=ConstantParamter.getConstantVo("SS_MOBILE_PHONE");
        if(vo==null)
        	return "";
        String field_name=vo.getString("str_value");
        if(field_name==null|| "".equals(field_name))
        	return "";
        FieldItem item=DataDictionary.getFieldItem(field_name);
        if(item==null)
        	return "";
        /**分析是否构库*/
        if("0".equals(item.getUseflag()))
        	return "";
        return field_name; 
	}
	
	
	/**
	 * 获得部门所在单位
	 * @param e0122 部门值
	 * @return
	 */
	public String getUnByUm(String e0122)
	{
		String name="";
		RowSet rowSet=null;
		try
		{
			
			ContentDAO dao=new ContentDAO(this.conn);
			int n=0;
			while(true)
			{
				n++;
				rowSet=dao.search("select codesetid,codeitemid,codeitemdesc from organization where codeitemid=(select parentid from organization where codeitemid=? )",Arrays.asList(new Object[] {e0122}));
				if(rowSet.next())
				{
					String codesetid=rowSet.getString("codesetid");
					String codeitemid=rowSet.getString("codeitemid");
					String codeitemdesc=rowSet.getString("codeitemdesc"); 
					
					if("UN".equalsIgnoreCase(codesetid))
					{
						name=codeitemdesc;
						break;
					}
					
					e0122=codeitemid;
				}
				
				if(n>10)
					break;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			PubFunc.closeDbObj(rowSet);
		}
		return name;
	}
	
	
	
	/**
	 * 获得审批关系 zhaoxg add 2015-9-15
	 * @return
	 */
	public String getSpRelationId()
	{
		String relation_id="";
		try
		{
			String  sp_relation_id=ctrlparam.getValue(SalaryCtrlParamBo.FLOW_CTRL,"sp_relation_id");
			String  flow_ctrl=ctrlparam.getValue(SalaryCtrlParamBo.FLOW_CTRL,"flag");
			if(flow_ctrl==null||flow_ctrl.trim().length()==0|| "0".equals(flow_ctrl))
				sp_relation_id="";
			DbWizard dbw = new DbWizard(this.conn);
			if(dbw.isExistTable("t_wf_relation",false))
			{
				if(sp_relation_id!=null&&sp_relation_id.trim().length()>0)
				{
					ContentDAO dao=new ContentDAO(this.conn);
					String sql="select * from t_wf_relation where validflag=1 and actor_type='4' and relation_id=?";
					ArrayList list = new ArrayList();
					list.add(sp_relation_id);
					RowSet rowSet=dao.search(sql,list);
					if(rowSet.next())
					{
						relation_id=sp_relation_id;
					}
				} 
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return relation_id;
	}
	/**
	 * 获得当前用户在审批关系中定义的直接领导信息 zhaoxg add 2015-9-15
	 * @param viewtype 页面区分 0发放 1审批
	 * @return
	 */
	public String getSpActorStr(String sp_relation_id,int viewtype)
	{
		StringBuffer str=new StringBuffer("");
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer sql = new StringBuffer();
			sql.append("select usergroup.groupName,twm.mainbody_id,operuser.fullname from t_wf_mainbody twm,operuser,usergroup where twm.mainbody_id=operuser.username and");
			sql.append(" operuser.groupid=usergroup.groupid and twm.relation_id=? ");
			sql.append(" and lower(twm.object_id)=? and twm.sp_grade=9 ");
			ArrayList list = new ArrayList();
			list.add(sp_relation_id);
			list.add(this.userview.getUserName().toLowerCase());
			
			if(viewtype!=0){//获取直属上级时排除自己 zhanghua 2017-4-20
				sql.append(" and lower(twm.mainbody_id)<>? ");
				list.add(this.userview.getUserName().toLowerCase());
			}
			sql.append(" order by twm.mainbody_id ");
			RowSet rowSet=dao.search(sql.toString(),list);
			while(rowSet.next())
			{
				String name=rowSet.getString(3);
				if(name==null||name.trim().length()==0)
					name=rowSet.getString(2);
				str.append("`"+rowSet.getString(1)+"##"+name+"##"+rowSet.getString(2)+"##"+SafeCode.encode(PubFunc.encrypt(rowSet.getString(2)))); 
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		if(str.length()>0)
			return str.substring(1);
		return str.toString();
	}
	/**
	 * 查询薪资发放临时表中是否还含有没报审的记录
	 * zhaoxg add 用于提交操作时候提示有没报审的记录
	 * @return
	 */
	public String  getIsNotSpFlag2Records()
	{
		String flag="0";
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			if(this.manager.length()>0)
			{
				RowSet rowSet=dao.search("select count(a0100) from "+this.gz_tablename+" where sp_flag2<>'02' or sp_flag2 is null");
				if(rowSet.next())
				{
					if(rowSet.getInt(1)>0)
						flag="1";
				}
				rowSet.close();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
	/**
	 * 求薪资数据各子集提交方式
	 * zhaoxg add 2015-9-15
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getSubmitTypeList(String salaryid)throws GeneralException
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer sql=new StringBuffer();
			ArrayList itemList=getSalaryItemList("",salaryid,1);
			 
		 
			StringBuffer buf=new StringBuffer();
			LazyDynaBean abean=null;
			for(Iterator t=itemList.iterator();t.hasNext();)
			{
				abean=(LazyDynaBean)t.next();
				String setid=(String)abean.get("fieldsetid");
				if(setid.charAt(0)!='A'|| "".equals(setid))
					continue;
				if("A00".equalsIgnoreCase(setid))
					continue;
				if(buf.indexOf(setid)==-1)
				{
					buf.append(setid);
					buf.append(",");
				}
			}
			SalaryLProgramBo lpbo=new SalaryLProgramBo(this.templatevo.getString("lprogram"));
			HashMap map=lpbo.getSubmitMap();
			String[] seta=StringUtils.split(buf.toString(),",");
			
			for(int i=0;i<seta.length;i++)
			{
				String setid=seta[i];
				FieldSet fieldset=DataDictionary.getFieldSetVo(setid);
				if(fieldset==null)
					continue;
				if("0".equalsIgnoreCase(fieldset.getUseflag()))
					continue;
				HashMap datamap = new HashMap();
				datamap.put("setid", setid);
				datamap.put("name", fieldset.getCustomdesc());
				String type=(String)map.get(setid);
				if(type==null||type.length()==0)
				{
					if("0".equals(fieldset.getChangeflag()))
					{
						datamap.put("type", "2");
					}
					else
						datamap.put("type", "1");
				}
				else
					datamap.put("type", type);
				list.add(datamap);
			}//for i loop end.
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);			
		}
		return list;		
	}
	/**
	 * 提交页面高级按钮中取得更新指标列表
	 * zhaoxg add 2015-10-12
	 * @param sets
	 * @param salaryid
	 * @return
	 */
	public ArrayList getUpdateItemList(String[] sets,String salaryid)
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer whl=new StringBuffer("");
			for(int i=0;i<sets.length;i++)
			{
				whl.append(",'"+sets[i]+"'");
			}
			String sql="select * from salaryset where fieldsetid in ("+whl.substring(1)+") and itemtype='N' and itemid!='A0000' and itemid!='A00Z1'  and salaryid=?  order by sortid";
			RowSet rowset=dao.search(sql,Arrays.asList(new Object[] {Integer.valueOf(salaryid)}));
			LazyDynaBean abean=null;
			
			SalaryLProgramBo lpbo=new SalaryLProgramBo(this.templatevo.getString("lprogram"));
			String  str=lpbo.getValue(SalaryLProgramBo.CONFIRM_TYPE).toLowerCase();

			while(rowset.next())
			{
				abean=new LazyDynaBean();
				abean.set("itemid",rowset.getString("itemid"));
				abean.set("itemdesc",rowset.getString("itemdesc"));
				if(str.indexOf(";"+rowset.getString("itemid").toLowerCase()+";")==-1)
					abean.set("flag","1");
				else
					abean.set("flag","0");
				list.add(abean);
			}
			rowset.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 判断是否是薪资重发数据
	 * @param ff_bosdate 发放日期
	 * @param ff_count    发放次数
	 * @param username 用户名
	 * @param salaryid  薪资帐套号
	 * @return
	 */
	public boolean getIsRedo(String ff_bosdate,String ff_count,String salaryid,String username)
	{
		boolean isRedo=false;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String[] temps=ff_bosdate.split("-");
			if(temps.length==1)
				temps=ff_bosdate.split("\\.");
			String sql="select * from gz_extend_log where salaryid=?  and lower(username)=?  and "+Sql_switcher.year("a00z2")+"=?  and "+Sql_switcher.month("a00z2")+"=?  and a00z3=?";
			ArrayList dataList=new ArrayList();
			dataList.add(new Integer(salaryid));
			dataList.add(username.toLowerCase());
			dataList.add(new Integer(temps[0]));
			dataList.add(new Integer(temps[1]));
			dataList.add(new Integer(ff_count));
			RowSet frowset=dao.search(sql,dataList);
			if(frowset.next())
			{
				if(frowset.getInt("isredo")==1)
					isRedo=true;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return isRedo;
	}
	
	
	/**
	 * 获得某账套下若干人员的发放记录
	 * @param ff_bosdate 发放日期
	 * @param ff_count    发放次数
	 * @param username 用户名
	 * @param salaryid  薪资帐套号
	 * @return
	 */
	public ArrayList getlogList(String ff_bosdate,String ff_count,String salaryid,String usernames)
	{ 
		ArrayList  logList=new ArrayList();
		RowSet frowset=null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String[] temps=ff_bosdate.split("-");
			String sql="select * from gz_extend_log where salaryid=?  and upper(username) in ( "+usernames+" )  and "+Sql_switcher.year("a00z2")+"=?  and "+Sql_switcher.month("a00z2")+"=?  and a00z3=?";
			ArrayList dataList=new ArrayList();
			dataList.add(new Integer(salaryid)); 
			dataList.add(new Integer(temps[0]));
			dataList.add(new Integer(temps[1]));
			dataList.add(new Integer(ff_count));
			 frowset=dao.search(sql,dataList);
			LazyDynaBean abean=null;
			while(frowset.next())
			{
				abean=new LazyDynaBean();
				int isRedo=frowset.getInt("isRedo");
				abean.set("sp_flag",frowset.getString("sp_flag"));
				abean.set("username", frowset.getString("username"));
				if(isRedo==1)
					abean.set("isRedo","1");
				else
					abean.set("isRedo","0");
				logList.add(abean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			PubFunc.closeDbObj(frowset);
		}
		return logList;
	}
	
	
	/**
	 * 取得指标的更新方式
	 * @param items
	 * @param uptypes
	 * @return
	 */
	public HashMap getItemUpdateType(String items,String uptypes)
	{
		HashMap map=new HashMap();
		if(items.length()>0)
		{
			items = items.replaceAll("／", "/");
			uptypes = uptypes.replaceAll("／", "/");
			String[] item_arr=items.split("/");
			String[] uptype_arr=uptypes.split("/");
			for(int i=0;i<item_arr.length;i++)
			{
				if(item_arr[i].trim().length()>0)
				{
					map.put(item_arr[i].toLowerCase(),uptype_arr[i]);
				}
			}
		}
		return map;
	}
	
	
	/**
	 * 保存薪资数据提交方式
	 * @param setlist		需要归档提交的数据集列表
	 * @param typelist		数据集提交类型列表
	 * @param items				更新指标集
	 * @param uptypes			更新方式
	 * @return
	 * @throws GeneralException
	 */
	public boolean saveSubmitType(ArrayList setlist,ArrayList typelist,String items,String uptypes,int salaryid)throws GeneralException
	{
		boolean bflag=true;
		try
		{	
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer buf=new StringBuffer();
			HashMap itemUptype=getItemUpdateType(items,uptypes);
			
			for(int i=0;i<setlist.size();i++)
			{
				buf.append(setlist.get(i));
				buf.append("`");
				buf.append(typelist.get(i));
				buf.append(";");
			}
			
			for(Iterator t=itemUptype.keySet().iterator();t.hasNext();)
			{
				String key=(String)t.next();
				if("0".equals((String)itemUptype.get(key)))
				{	buf.append(key.toUpperCase());
					buf.append(";");
				}
			} 
			
			SalaryLProgramBo lpbo=new SalaryLProgramBo(this.templatevo.getString("lprogram"));
			lpbo.setValue(SalaryLProgramBo.CONFIRM_TYPE,buf.toString());
			String str=lpbo.outPutContent();
			this.templatevo.setString("lprogram", str);
			RecordVo vo=new RecordVo("salarytemplate");
			vo.setString("lprogram", str);
			vo.setInt("salaryid", salaryid);
			dao.updateValueObject(vo);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			bflag=false;
			throw GeneralExceptionHandler.Handle(ex);			
		}
		return bflag;		
	}
	
	/**   
     * @Title: isAllowEditSubdata_Sp   
     * @Description: 薪资审批 是否允许提交后更改数据；具有 “允许提交后更改数据”且 具有提交权限才可更改数据,功能授权暂时不区分薪资保险，同薪资发放前台权限    
     * @param @param gz_module
     * @param @return
     * @author wangrd
     * @param @throws GeneralException 
     * @return boolean    
     * @throws   
    */
    public boolean isAllowEditSubdata_Sp(String gz_module) throws GeneralException
    {
        boolean bAllowEditSubdata=false;   
        try {
            String allowEditSubdata=getLprogramAttri("allow_edit_subdata",SalaryLProgramBo.CONFIRM_TYPE);            
            if(allowEditSubdata==null||allowEditSubdata.trim().length()==0)
                allowEditSubdata="0";  
           
            if ("1".equals(allowEditSubdata)){//允许提交后更改数据 且 具有提交权限,功能授权暂时不区分薪资保险，同薪资发放前台权限              
                if ("1".equals(gz_module)){ //保险                  
                    if (this.userview.hasTheFunction("3250305")|| this.userview.hasTheFunction("3271305")){
                        bAllowEditSubdata=true;                        
                    }
                } else {//薪资
                    if (this.userview.hasTheFunction("3240305")|| this.userview.hasTheFunction("3270305")){
                        bAllowEditSubdata=true;                         
                    }
                }
             }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
                           
        }
        return bAllowEditSubdata;   
    } 
    
    
    
    /**
	 * 批量插入子集纪录
	 */
	public void batchInsertSetRecord(String destname,String where_str,String where_str2)
	{
		StringBuffer buf=new StringBuffer();
		String strNow=Sql_switcher.sqlNow();
		String strIns=",createtime,createusername";
		String strvalue=","+strNow+",'"+this.userview.getUserName()+"'";
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			buf.append("insert into ");
			buf.append(destname);
			buf.append("(A0100,I9999");
			buf.append(strIns);
			buf.append(") ");
			buf.append(" select a.a0100,a.i9999+1 "+strvalue+" from ");
			buf.append(destname+" a,"+where_str+"  where a.i9999=(select max(b.i9999) from ");
			buf.append(destname+" b where a.a0100=b.a0100) and aa.a0100=a.a0100 ");
			dao.update(buf.toString());
			buf.setLength(0);
			
			buf.append("insert into ");
			buf.append(destname);
			buf.append("(A0100,I9999");
			buf.append(strIns);
			buf.append(") ");
			buf.append(where_str2);
			dao.update(buf.toString());
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 批量插入子集纪录
	 */
	public void batchInsertSetRecord_royalty(String destname,String where_str,String where_str2,ArrayList relationfieldList,String cbase)
	{
		StringBuffer buf=new StringBuffer();
		String strNow=Sql_switcher.sqlNow();
		String strIns=",createtime,createusername";
		String strvalue=","+strNow+",'"+this.userview.getUserName()+"'";
		String tableName_ori="t#"+this.userview.getUserName()+"_gz_3";   //待提交入库的数据
		String relation_str="";
		String relation_str2="";
		String relation_str3="";
		for(int i=0;i<relationfieldList.size();i++)
		{
			relation_str+=","+(String)relationfieldList.get(i);
			relation_str2+=",c."+(String)relationfieldList.get(i);
			String itemid=(String)relationfieldList.get(i);
			if(Sql_switcher.searchDbServer()==2)
			{
				if("D".equalsIgnoreCase(DataDictionary.getFieldItem(itemid.trim()).getItemtype()))
						relation_str3+=" and "+Sql_switcher.isnull(Sql_switcher.dateToChar("c."+itemid,"YYYY-MM-DD"),"'-'")+"="+Sql_switcher.isnull(Sql_switcher.dateToChar(destname+"."+itemid,"YYYY-MM-DD"),"'-'") ; 
				else
						relation_str3+=" and "+Sql_switcher.isnull("c."+itemid,"'-'")+"="+Sql_switcher.isnull(destname+"."+itemid,"'-'") ; 
			}
			else
				relation_str3+=" and "+Sql_switcher.isnull("c."+(String)relationfieldList.get(i),"''")+"="+Sql_switcher.isnull(destname+"."+(String)relationfieldList.get(i),"''"); 
		 
		}
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			
			String sql_sub="( select c.* from "+tableName_ori+" c,"+where_str+" where c.a0100=aa.a0100 and lower(c.NBASE)='"+cbase.toLowerCase()+"' and not exists ( ";
			sql_sub+=" select null from "+destname+" where c.a0100="+destname+".a0100 "+relation_str3+" ) ) ";
			 
			
			buf.append("insert into ");
			buf.append(destname);
			buf.append("(A0100,I9999");
			buf.append(strIns+relation_str);
			buf.append(") ");
			buf.append(" select a.a0100,a.i9999+1 "+strvalue+relation_str2+" from ");
			buf.append(destname+" a,"+sql_sub+" c  where   a.a0100=c.a0100 and a.i9999=(select max(b.i9999) from ");
			buf.append(destname+" b where a.a0100=b.a0100)  ");
			dao.update(buf.toString());
			buf.setLength(0);
			
			buf.append("insert into ");
			buf.append(destname);
			buf.append("(A0100,I9999");
			buf.append(strIns+relation_str);
			buf.append(") ");
			buf.append(" select a.a0100,1 "+strvalue+relation_str2+" from ( ");
			buf.append(where_str2+" ) a,"+tableName_ori+" c  where   a.a0100=c.a0100  ");
			buf.append( " and lower(c.NBASE)='"+cbase.toLowerCase()+"' ");
			dao.update(buf.toString());
		 
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 求更新串
	 * @param destname		目标表
	 * @param srcname		源表
	 * @param updates		更新串
	 * @param chgflag		子集变化标识
	 * @return
	 */
	public String getUpdateSQL(String destname,String srcname,String updates,int chgflag,HashMap itemUptype)
	{
		StringBuffer buf=new StringBuffer();
		String[] fieldarr=StringUtils.split(updates,",");
		for(int i=0;i<fieldarr.length;i++)
		{
			buf.append(destname);
			buf.append(".");
			buf.append(fieldarr[i]);
			buf.append("=");
			
			/** 累加更新 */
			if(itemUptype.get(fieldarr[i].toLowerCase())!=null&& "0".equals((String)itemUptype.get(fieldarr[i].toLowerCase())))
			{
				buf.append("("+destname);
				buf.append(".");
				buf.append(fieldarr[i]);
				
				buf.append("+"+srcname);
				buf.append(".");
				buf.append(fieldarr[i]);
				buf.append(")`");
				
			}
			else
			{
				buf.append(srcname);
				buf.append(".");
				buf.append(fieldarr[i]);
				buf.append("`");
			}
		}//for i loop end.
		buf.append("modusername='");
		buf.append(this.userview.getUserName());
		buf.append("'`modtime=");
		buf.append(Sql_switcher.sqlNow());
		buf.append("`");		
		/**按年或月变化子集*/
		if(chgflag==1||chgflag==2)
		{
			String setid=destname.substring(3);			
			String axxz0=setid+"Z0";
			String axxz1=setid+"Z1";
			buf.append(destname);
			buf.append(".");
			buf.append(axxz0);
			buf.append("=");
			buf.append(srcname);
			buf.append(".a00z0");
			buf.append("`");
			buf.append(destname);
			buf.append(".");
			buf.append(axxz1);
			buf.append("=");
			buf.append(srcname);
			buf.append(".a00z1");
			buf.append("`");
		}
		if(buf.length()>0)
			buf.setLength(buf.length()-1);
		return buf.toString();
	}
	/**
	 * 求更新串
	 * @param destname		目标表
	 * @param srcname		源表
	 * @param updates		更新串
	 * @param chgflag		子集变化标识
	 * @return
	 */
	public String getUpdateSQL_royalty(String destname,String srcname,String updates,int chgflag,HashMap itemUptype)
	{
		StringBuffer buf=new StringBuffer();
		String[] fieldarr=StringUtils.split(updates,",");
		for(int i=0;i<fieldarr.length;i++)
		{
			buf.append(destname);
			buf.append(".");
			buf.append(fieldarr[i]);
			buf.append("=");
			buf.append(srcname);
			buf.append(".");
			buf.append(fieldarr[i]);
			buf.append("`");
			
		}//for i loop end.
		buf.append("modusername='");
		buf.append(this.userview.getUserName());
		buf.append("'`modtime=");
		buf.append(Sql_switcher.sqlNow());
		buf.append("`");
		if(buf.length()>0)
			buf.setLength(buf.length()-1);
		return buf.toString();
	}
	
	/**
	 * 处理 如果子集中有相应次数，自动加1 （针对orcale）
	 * @param strym
	 * @param nbase
	 * @param dbw
	 * @param dao
	 * @return
	 */
	private String executeMaxZ1Table_history(ArrayList userFlagList,String strym,String nbase,DbWizard dbw,ContentDAO dao,ArrayList setlist,ArrayList typelist)
	{
		String tableName="t#"+this.userview.getUserName()+"_gz_1"; //this.userview.getUserName()+"_maxZ1";
		String tableName2="t#"+this.userview.getUserName()+"_gz_2"; //this.userview.getUserName()+"_maxZ1_2";
		try
		{
			 if(dbw.isExistTable(tableName,false))
				 dbw.dropTable(tableName);
			 {
				 Field field=null;
				 Table table=new Table(tableName);
				 
				 field=new Field("A0100","A0100");
				 field.setDatatype(DataType.STRING);
				 field.setLength(30);
				 table.addField(field);						
				 field=new Field("A00Z1","A00Z1");
				 field.setDatatype(DataType.INT);
				 field.setLength(10);
				 table.addField(field);	
				 field=new Field("A00Z0","A00Z0");
				 field.setDatatype(DataType.DATE);
				 table.addField(field);
				 
				 dbw.createTable(table);
			}
			if(!dbw.isExistField(tableName,"A00Z1_O",false))
			{
				Table table=new Table(tableName);
				Field field=new Field("A00Z1_O","A00Z1_O");
				field.setDatatype(DataType.INT);
				field.setLength(10);
				table.addField(field);	
				dbw.addColumns(table);
			}
				
			dbw.execute("delete from "+tableName);
			 
		 	 String salaryhistory_tmp="t#"+this.userview.getUserName()+"_gz_3";
			 String tempName="t#"+this.userview.getUserName()+"_gz";  //this.userview.getUserName()+"_GzTempTable";
			 StringBuffer sql=new StringBuffer("insert into "+tableName+" (a0100,a00z1,A00Z1_O)");
			 sql.append(" select "+tempName+".a0100,0,"+tempName+".a00z1 from "+tempName);
			 sql.append(" where add_flag=0 or add_flag is null  ");
			 dao.update(sql.toString());
			
			 if(dbw.isExistTable(tableName2,false))
				 dbw.dropTable(tableName2);
		//	 if(!dbw.isExistTable(tableName2,false))
			 {
				 if(Sql_switcher.searchDbServer()==2)
					 dbw.execute("create table "+tableName2+" as  select a0100,a00z1 from "+salaryhistory_tmp+" where 1=2");	
				 else
					 dbw.execute("select a0100,a00z1  into "+tableName2+"  from "+salaryhistory_tmp+this._withNoLock+" where 1=2");
			 }	
			 
			 String[] temp=strym.split("-");
			 for(int j=0;j<setlist.size();j++)
			 {
					String setid=(String)setlist.get(j);
					/**(0,1,2,3)=(更新,新增,不变,更新(薪资重发时))*/
					String type=(String)typelist.get(j);
					if("2".equalsIgnoreCase(type)|| "0".equalsIgnoreCase(type))//当前记录不变
						continue;
					if(setid.charAt(0)=='A')
					{
						if("A00".equalsIgnoreCase(setid))
							continue;
						FieldSet fieldset=DataDictionary.getFieldSetVo(setid);
						if(fieldset==null|| "0".equalsIgnoreCase(fieldset.getUseflag()))
							continue;
						int nflag=Integer.parseInt(fieldset.getChangeflag());
						if(nflag==1||nflag==2)
						{ 
								dbw.execute("delete from "+tableName2);
								sql.setLength(0);
								sql.append("insert into "+tableName2+" (a00z1,a0100) select  MAX("+setid+"Z1) ,a0100 from "+nbase+setid+this._withNoLock+" where  a0100 in (select a0100 from "+tableName+")  and  "+Sql_switcher.year(setid+"z0")+"="+temp[0]+" and "+Sql_switcher.month(setid+"z0")+"="+temp[1]+"	GROUP BY A0100 ");
								dbw.execute(sql.toString());
								sql.setLength(0);
								sql.append("update "+tableName+" set a00z1=(select a00z1 from  ");
								sql.append(tableName2+"  where "+tableName2+".a0100="+tableName+".a0100 and "+tableName2+".a00z1>"+tableName+".a00z1 ");
								sql.append(" ) where exists( select  null from "+tableName2+" where "+tableName2+".a0100="+tableName+".a0100 and "+tableName2+".a00z1>"+tableName+".a00z1 ");
								sql.append(" )  ");
								dbw.execute(sql.toString());
						}
					}
			 }
		
			 dbw.execute("delete from "+tableName2);
			 sql.setLength(0);
			 sql.append("insert into "+tableName2+" (a00z1,a0100) select  MAX(a00Z1) ,a0100 from salaryhistory"+this._withNoLock+" where  a0100 in (select a0100 from "+tableName+") ");
			 sql.append(" and  "+Sql_switcher.year("a00z0")+"="+temp[0]+" and "+Sql_switcher.month("a00z0")+"="+temp[1]+" and salaryid="+this.salaryid);
			 sql.append(" and not exists (select null from "+salaryhistory_tmp+" where salaryhistory.a0100="+salaryhistory_tmp+".a0100 and lower(salaryhistory.nbase)=lower("+salaryhistory_tmp+".nbase) ");
			 sql.append(" and salaryhistory.a00z0="+salaryhistory_tmp+".a00z0 and  salaryhistory.a00z1="+salaryhistory_tmp+".a00z1 )");
			 sql.append(" and  lower(nbase)='"+nbase.toLowerCase()+"'	GROUP BY A0100 ");
			 dbw.execute(sql.toString());
			 sql.setLength(0);
			 sql.append("update "+tableName+" set a00z1=(select a00z1 from  ");
			 sql.append(tableName2+"  where "+tableName2+".a0100="+tableName+".a0100 and "+tableName2+".a00z1>"+tableName+".a00z1 ");
			 sql.append(" ) where   exists( select  null from "+tableName2+" where "+tableName2+".a0100="+tableName+".a0100 and "+tableName2+".a00z1>"+tableName+".a00z1 ");
			 sql.append(" )  ");
			 dbw.execute(sql.toString());
		 
			 HashMap tabMap=new HashMap(); 
			 ArrayList tempList=null;
			 sql.setLength(0);
			 sql.append("select a.a00z1+1,"+salaryhistory_tmp+".a0100,"+salaryhistory_tmp+".a00z1,"+salaryhistory_tmp+".a00z0,"+salaryhistory_tmp+".userflag  from "+tableName+" a,"+salaryhistory_tmp+this._withNoLock);
			 sql.append(" where a.a0100="+salaryhistory_tmp+".a0100 and lower("+salaryhistory_tmp+".nbase)='"+nbase.toLowerCase()+"'    and a.a00z1>="+salaryhistory_tmp+".a00z1 ");
			 RowSet rowSet=dao.search(sql.toString());
			
			 while(rowSet.next())
			 {
				    tempList=new ArrayList();
					tempList.add(new Integer(rowSet.getInt(1)));
					tempList.add(rowSet.getString(2));
					tempList.add(new Integer(temp[0]));
					tempList.add(new Integer(temp[1]));
					tempList.add(new Integer(rowSet.getInt(3)));
					String userflag=rowSet.getString("userflag").toLowerCase();
				 	if(tabMap.get(userflag)==null)
				 	{
				 		ArrayList list=new ArrayList();
				 		list.add(tempList);
				 		tabMap.put(userflag,list);
				 	}
				 	else
				 	{
				 		ArrayList list=(ArrayList)tabMap.get(userflag);
				 		list.add(tempList);
				 	}
			 }
			 
			sql.setLength(0);
			sql.append("update salaryhistory set a00z1=(select "+tableName+".a00z1+1 from  "+tableName);
			sql.append(" where salaryhistory.a0100="+tableName+".a0100 and "+tableName+".a00z1>=salaryhistory.a00z1  )");
			sql.append(" where salaryhistory.salaryid="+this.salaryid+"  and exists (select null from "+tableName+" where salaryhistory.a0100="+tableName+".a0100 and "+tableName+".a00z1>=salaryhistory.a00z1  )    and lower(salaryhistory.nbase)='"+nbase.toLowerCase()+"'");
			sql.append(" and exists (select null from "+salaryhistory_tmp+"  where salaryhistory.a0100="+salaryhistory_tmp+".a0100 and salaryhistory.nbase="+salaryhistory_tmp+".nbase  and salaryhistory.a00z0="+salaryhistory_tmp+".a00z0 and  salaryhistory.a00z1="+salaryhistory_tmp+".a00z1    ) ");
			dbw.execute(sql.toString());
			dbw.execute("update salaryhistory set  add_flag=1 where  salaryhistory.salaryid="+this.salaryid+"  and exists (select null from "+tableName+" where "+tableName+".a0100=salaryhistory.a0100)     and lower(nbase)='"+nbase.toLowerCase()+"'  and "+Sql_switcher.year("a00z0")+"="+temp[0]+" and "+Sql_switcher.month("a00z0")+"="+temp[1]+" and exists (select null from "+salaryhistory_tmp+"  where salaryhistory.a0100="+salaryhistory_tmp+".a0100 and salaryhistory.nbase="+salaryhistory_tmp+".nbase  and salaryhistory.a00z0="+salaryhistory_tmp+".a00z0 and  salaryhistory.a00z1="+salaryhistory_tmp+".a00z1    ) ");
			    			
			 
			Set set=tabMap.keySet();
			for(Iterator t=set.iterator();t.hasNext();)
		    {
		    	String key=((String)t.next()).toLowerCase(); 
		    	ArrayList updateList=(ArrayList)tabMap.get(key);
			    String tabname=key+"_salary_"+this.salaryid;
			    dbw.execute("update "+tabname+" set  add_flag=1 where  exists (select null from "+tableName+" where "+tableName+".a0100="+tabname+".a0100)      and lower(nbase)='"+nbase.toLowerCase()+"'  and "+Sql_switcher.year("a00z0")+"="+temp[0]+" and "+Sql_switcher.month("a00z0")+"="+temp[1]); 
				
			    String sql2="update gz_tax_mx  set a00z1=? where lower(nbase)='"+nbase.toLowerCase()+"' and lower(userflag)='"+key+"' and a0100=? "
							+" and "+Sql_switcher.year("a00z0")+"=? and "+Sql_switcher.month("a00z0")+"=? and a00z1=? and salaryid="+this.salaryid;
				dao.batchUpdate(sql2, updateList);
							
				String sql1="update "+tabname+"  set a00z1=? where lower(nbase)='"+nbase.toLowerCase()+"' and a0100=? "
							+" and "+Sql_switcher.year("a00z0")+"=? and "+Sql_switcher.month("a00z0")+"=? and a00z1=? and lower(userflag)='"+key+"' ";
				dao.batchUpdate(sql1, updateList);	 
				
				sql1="update "+salaryhistory_tmp+"  set a00z1=? where lower(nbase)='"+nbase.toLowerCase()+"' and a0100=? "
						+" and "+Sql_switcher.year("a00z0")+"=? and "+Sql_switcher.month("a00z0")+"=? and a00z1=? and lower(userflag)='"+key+"' ";
				dao.batchUpdate(sql1, updateList);	 
			 } 
			 if(rowSet!=null)
				rowSet.close();
			 
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return tableName;
	}
	
	/** 处理只有一条薪资记录的用户年月子集数据 */
	public void dealwithSingleRecord_ym_history(ArrayList userFlagList,ArrayList setlist,boolean flag,boolean isYMaddSet,ArrayList updatelist,ArrayList typelist,String cbase,DbWizard dbw,HashMap itemUptype,String strNow,String strym,String subNoPriv)
	{
		try
		{
		 

			
			String[] temp=strym.split("-");
			ContentDAO dao=new ContentDAO(this.conn); 
			if(flag&&isYMaddSet)
			{ 
				try
				{
					executeMaxZ1Table_history(userFlagList,strym,cbase,dbw,dao,setlist,typelist);	
				}
				catch(Exception ee)
				{
					ee.printStackTrace();
				}
			}
		 
			
			//判断子集中是否有当年当月当次记录，没有则追加
			String strIns=",createtime,createusername";
			String strvalue=","+strNow+",'"+this.userview.getUserName()+"'";
			for(int j=0;j<setlist.size();j++)
			{
				String setid=(String)setlist.get(j);
				/**(0,1,2,3)=(更新,新增,不变,更新(薪资重发时))*/
				String type=(String)typelist.get(j);
				if("3".equals(type))
					type="0";
				if("2".equalsIgnoreCase(type))//当前记录不变
					continue;
				if("0".equals(subNoPriv))
				{
					if(!"2".equals(this.userview.analyseTablePriv(setid.toUpperCase())))
						continue;
				}
				String destname=cbase+setid;
				String axxz0=setid+"z0";
				String axxz1=setid+"z1";
				if(setid.charAt(0)=='A')
				{
					if("A00".equalsIgnoreCase(setid))
						continue;
					FieldSet fieldset=DataDictionary.getFieldSetVo(setid);
					if(fieldset==null|| "0".equalsIgnoreCase(fieldset.getUseflag()))
						continue;
					int nflag=Integer.parseInt(fieldset.getChangeflag());
					if(nflag==1||nflag==2)
					{ 
						{
							insertSetInfo(cbase,strym,dbw,dao,setid,strIns,strvalue);
						}
				 
					}
				}
			}
			 
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private 	 void insertSetInfo(String nbase,String strym,DbWizard dbw,ContentDAO dao,String setid,String strIns,String strvalue)
	{
		try
		{
			
			String  royalty_valid=this.ctrlparam.getValue(SalaryCtrlParamBo.ROYALTIES,"valid");
			String royalty_relation_fields=this.ctrlparam.getValue(SalaryCtrlParamBo.ROYALTIES,"relation_fields");
			ArrayList relationfieldList=new ArrayList();
			String royalty_setid=this.ctrlparam.getValue(SalaryCtrlParamBo.ROYALTIES,"setid");
			StringBuffer relationFieldStr=new StringBuffer("");
			String relation_str="";
			if("1".equalsIgnoreCase(royalty_valid)&&royalty_relation_fields.length()>0&&setid.equalsIgnoreCase(royalty_setid))
			{
				String[] temps=royalty_relation_fields.toLowerCase().split(",");
				for(int n=0;n<temps.length;n++)
				{
					if(temps[n].trim().length()>0&&DataDictionary.getFieldItem(temps[n].trim())!=null)
					{ 
						relationfieldList.add(temps[n]);
						relationFieldStr.append(","+temps[n]);
					}
				}
			} 
			String destname=nbase+setid;
			 String salaryhistory_tmp="t#"+this.userview.getUserName()+"_gz_3";
			String tableName="t#"+this.userview.getUserName()+"_gz_1"; //this.userview.getUserName()+"_maxZ1";
			if(dbw.isExistTable(tableName,false))
				 dbw.dropTable(tableName);
			 
				 Field field=null;
				 Table table=new Table(tableName);
				 
				 field=new Field("A0100","A0100");
				 field.setDatatype(DataType.STRING);
				 field.setLength(30);
				 table.addField(field);						
				 field=new Field("A00Z1","A00Z1");
				 field.setDatatype(DataType.INT);
				 field.setLength(10);
				 table.addField(field);	
				 field=new Field("A00Z0","A00Z0");
				 field.setDatatype(DataType.DATE);
				 table.addField(field);
				 
				 field=new Field("A00Z1_O","A00Z1_O");
				 field.setDatatype(DataType.INT);
				 field.setLength(10);
				 table.addField(field);	
				 
				 for(int i=0;i<relationfieldList.size();i++)
				 {
					 String itemid=(String)relationfieldList.get(i);
					 FieldItem item=DataDictionary.getFieldItem(itemid.toLowerCase());
					 field=new Field(itemid,itemid);
					 if("D".equalsIgnoreCase(item.getItemtype()))
						 field.setDatatype(DataType.DATE);
					 if("A".equalsIgnoreCase(item.getItemtype()))
						 field.setDatatype(DataType.STRING);
					 field.setLength(item.getItemlength());
					 table.addField(field);	  
					 if(Sql_switcher.searchDbServer()==2)
					 {
						 if("D".equalsIgnoreCase(item.getItemtype()))
							 relation_str+=" and "+Sql_switcher.isnull(Sql_switcher.dateToChar("a1."+itemid,"YYYY-MM-DD"),"'-'")+"="+Sql_switcher.isnull(Sql_switcher.dateToChar(destname+"."+itemid,"YYYY-MM-DD"),"'-'") ; 
						 else
							 relation_str+=" and "+Sql_switcher.isnull("a1."+itemid,"'-'")+"="+Sql_switcher.isnull(destname+"."+itemid,"'-'") ; 
					 }
					 else	
						   relation_str+=" and "+Sql_switcher.isnull("a1."+itemid,"''")+"="+Sql_switcher.isnull(destname+"."+itemid,"''"); 
				 } 
				 dbw.createTable(table);
			
			
			
			
			
			String tempName="t#"+this.userview.getUserName()+"_gz"; //this.userview.getUserName()+"_GzTempTable";
			
			String axxz0=setid+"z0";
			String axxz1=setid+"z1";
			String[] temp=strym.split("-");
			dao.update("delete from "+tableName);
			StringBuffer sql=new StringBuffer("insert into "+tableName+" (A0100,a00z0,a00z1"+relationFieldStr+")");
			sql.append(" select "+salaryhistory_tmp+".A0100,"+salaryhistory_tmp+".a00z0,"+salaryhistory_tmp+".a00z1"+relationFieldStr+" from "+salaryhistory_tmp+this._withNoLock+" , "+tempName);
			sql.append(" where "+salaryhistory_tmp+".a0100="+tempName+".a0100 and lower("+salaryhistory_tmp+".nbase)='"+nbase.toLowerCase()+"'");
			sql.append("   and exists (select null from "+nbase+"A01 where "+nbase+"A01.a0100="+salaryhistory_tmp+".a0100 )    and  "+Sql_switcher.year(salaryhistory_tmp+".a00z0")+"="+temp[0]+" and "+Sql_switcher.month(salaryhistory_tmp+".a00z0")+"="+temp[1]);
			dbw.execute(sql.toString());
			sql.setLength(0);
			
			if("1".equalsIgnoreCase(royalty_valid)&&setid.equalsIgnoreCase(royalty_setid))
			{
				 
				sql.setLength(0);
				sql.append("insert into "+destname+" (A0100,I9999"+strIns+","+axxz0+","+axxz1+relationFieldStr+") ");
				sql.append(" select a1.a0100,a2.i9999+1"+strvalue+",a1.a00z0,a1.a00z1"+relationFieldStr +" from ");
				sql.append(tableName+" a1, ");
				sql.append("( select a0100,i9999 from "+destname+" a"+this._withNoLock+" where a.i9999=(select max(b.i9999) from "+destname+" b where a.a0100=b.a0100 ) ) a2 ");
				sql.append("where a1.a0100=a2.a0100 and not exists ( ");
				sql.append(" select null from "+destname+" where a1.a0100="+destname+".a0100 "+relation_str+" ) ");
				dbw.execute(sql.toString());
				
				sql.setLength(0);
				sql.append("delete from "+tableName+" where exists (select null from "+destname+" where ");
				sql.append(tableName+".a0100= "+destname+".a0100 ) ");
				dbw.execute(sql.toString());
				
				sql.setLength(0);
				sql.append("insert into "+destname+" (A0100,I9999"+strIns+","+axxz0+","+axxz1+relationFieldStr+") ");
				sql.append(" select a1.a0100,1"+strvalue+",a1.a00z0,a1.a00z1"+relationFieldStr+" from ");
				sql.append(tableName+" a1 "); 
				dbw.execute(sql.toString());
			} 
			else
			{
				sql.append("delete from "+tableName+" where exists (select null from "+destname+" where "+tableName+".a0100= "+destname+".a0100 ");
				sql.append(" and "+tableName+".a00z0= "+destname+"."+axxz0+" and "+tableName+".a00z1= "+destname+"."+axxz1+"  ");
				sql.append("   ) ");
				dbw.execute(sql.toString());
				
				sql.setLength(0);
				sql.append("insert into "+destname+" (A0100,I9999"+strIns+","+axxz0+","+axxz1+") ");
				sql.append(" select a1.a0100,a2.i9999+1"+strvalue+",a1.a00z0,a1.a00z1 from ");
				sql.append(tableName+" a1, ");
				sql.append("( select a0100,i9999 from "+destname+" a where a.i9999=(select max(b.i9999) from "+destname+" b where a.a0100=b.a0100 ) ) a2 ");
				sql.append("where a1.a0100=a2.a0100 ");
				dbw.execute(sql.toString());
		
				sql.setLength(0);
				sql.append("delete from "+tableName+" where exists (select null from "+destname+" where ");
				sql.append(tableName+".a0100= "+destname+".a0100 ) ");
				dbw.execute(sql.toString());
				
				
				sql.setLength(0);
				sql.append("insert into "+destname+" (A0100,I9999"+strIns+","+axxz0+","+axxz1+") ");
				sql.append(" select a1.a0100,1"+strvalue+",a1.a00z0,a1.a00z1 from ");
				sql.append(tableName+" a1 "); 
				dbw.execute(sql.toString());
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	/**
	 *  查找薪资类别项目涉及各年月变化子集本月最大次数加1
	 * @param setList
	 * @param strym
	 * @param nbase
	 * @param a0100
	 * @return
	 */
	private int getNewA00z1(ArrayList setList,String strym,String nbase,String a0100,ArrayList typelist)
	{
		int a00z1=0;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer sub_sql=new StringBuffer("");
			String year=strym.substring(0, 4);
			String month=strym.substring(5, 7);
			for(int j=0;j<setList.size();j++)
			{
				String setid=((String)setList.get(j)).toUpperCase();
				if(setid.charAt(0)=='A')
				{
					if("A00".equalsIgnoreCase(setid))
						continue;
					
					/**(0,1,2,3)=(更新,新增,不变,更新(薪资重发时))*/
					String type=(String)typelist.get(j);
					if("2".equalsIgnoreCase(type)|| "0".equalsIgnoreCase(type))//当前记录不变
						continue;
					
					FieldSet fieldset=DataDictionary.getFieldSetVo(setid);
					if(fieldset==null|| "0".equalsIgnoreCase(fieldset.getUseflag()))
						continue;
					int nflag=Integer.parseInt(fieldset.getChangeflag());
					if(nflag==1||nflag==2)
					{
						sub_sql.append(" union all ");
						sub_sql.append("select max("+setid+"z1) z1 from "+nbase+setid+" where a0100='"+a0100+"' ");
						sub_sql.append(" and "+Sql_switcher.year(setid+"z0")+"="+year+" and "+Sql_switcher.month(setid+"z0")+"="+month);
					}
				}
			}
			if(sub_sql.length()>0)
			{
				RowSet rowSet=dao.search("select max(z1) from ( "+sub_sql.substring(11)+" ) b");
				if(rowSet.next())
				{
					if(rowSet.getString(1)!=null)
						a00z1=rowSet.getInt(1);
				}
				rowSet.close();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		a00z1++;
		return a00z1;
	}
	
	/**
	 *  查找薪资类别项目涉及各年月变化子集本月最大次数加1
	 * @param setList
	 * @param strym
	 * @param nbase
	 * @param a0100
	 * @return
	 */
	private int getNewA00z1_history(String strym,String nbase,String a0100,String _a00z1)
	{
		int a00z1=0;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer sql=new StringBuffer("");
			String year=strym.substring(0, 4);
			String month=strym.substring(5, 7);
			
			sql.append("select  MAX(a00Z1)  from salaryhistory"+this._withNoLock+" where  a0100='"+a0100+"'  and  lower(nbase)='"+nbase.toLowerCase()+"' ");
			sql.append(" and salaryid="+this.salaryid+"  and  "+Sql_switcher.year("a00z0")+"="+year+" and "+Sql_switcher.month("a00z0")+"="+month+" ");
			sql.append(" and a00z1<>"+_a00z1);
			
			RowSet rowSet=dao.search(sql.toString());
			if(rowSet.next())
			{
					if(rowSet.getString(1)!=null)
						a00z1=rowSet.getInt(1);
			}
			rowSet.close();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		a00z1++;
		return a00z1;
	}
	
	private void synchronousZ1_history(String strym,String strcount,int z1,String a0100,String nbase,ArrayList updateList,String userflag)
	{
		String year=strym.substring(0, 4);
		String month=strym.substring(5, 7); 
		ArrayList tempList=new ArrayList();
		tempList.add(new Integer(z1));
		tempList.add(userflag);
		tempList.add(a0100);
		tempList.add(new Integer(year));
		tempList.add(new Integer(month));
		tempList.add(new Integer(strcount));
		updateList.add(tempList);
	}
	
	/**
	 * 分析档案是否存在当前业务日期的记录
	 * @param destsetid
	 * @param setid
	 * @param a0100
	 * @param chgflag
	 * @param strym
	 * @param strcount
	 * @return I9999,返回值如果为-1,则表示没有当前业务日期的记录
	 */
	private int isHaveCurrentDateRecord_royalty(String destsetid,String setid,String a0100,ArrayList relationFieldList,HashMap values)
	{
		int i9999=-1;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn); 
			StringBuffer buf=new StringBuffer();
			buf.append("select I9999 from ");
			buf.append(destsetid);
			buf.append(" where a0100=?   ");
			FieldItem aitem=null;
			for(int i=0;i<relationFieldList.size();i++)
			{
				aitem=(FieldItem)relationFieldList.get(i); 
				if(!"null".equalsIgnoreCase((String)values.get(aitem.getItemid())))
				{
						if("D".equalsIgnoreCase(aitem.getItemtype()))
							buf.append(" and "+Sql_switcher.isnull(Sql_switcher.dateToChar(aitem.getItemid(),"YYYY-MM-DD"),"''")+"='"+(String)values.get(aitem.getItemid())+"' ");
						else
							buf.append(" and "+Sql_switcher.isnull(aitem.getItemid(),"''")+"='"+(String)values.get(aitem.getItemid())+"' "); 
				}
				else
				{
						if(Sql_switcher.searchDbServer()==Constant.ORACEL)
						{
							buf.append(" and "+ aitem.getItemid() +" is null "); 
						}
						else
							buf.append(" and "+Sql_switcher.isnull(aitem.getItemid(),"''")+"='' "); 
				}
					 
			} 
			ArrayList paralist=new ArrayList();
			paralist.add(a0100);
			RowSet rset=dao.search(buf.toString(),paralist);
			if(rset.next())
				i9999=rset.getInt("I9999");
			rset.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return i9999;
	}
	
	
	/**
	 * 分析档案是否存在当前业务日期的记录
	 * @param destsetid
	 * @param setid
	 * @param a0100
	 * @param chgflag
	 * @param strym
	 * @param strcount
	 * @return I9999,返回值如果为-1,则表示没有当前业务日期的记录
	 */
	private int isHaveCurrentDateRecord(String destsetid,String setid,String a0100,int chgflag,String strym,String strcount)
	{
		int i9999=-1;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String year=strym.substring(0, 4);
			String month=strym.substring(5, 7);
			String axxz0=setid+"Z0";
			String axxz1=setid+"Z1";
			StringBuffer buf=new StringBuffer();
			buf.append("select I9999 from ");
			buf.append(destsetid);
			buf.append(" where a0100=? and ");
			buf.append(Sql_switcher.year(axxz0));
			buf.append("=");
			buf.append(year);
			buf.append(" and ");
			buf.append(axxz1);
			buf.append("=");
			buf.append(strcount);			
			if(chgflag==1)
			{
				buf.append(" and ");
				buf.append(Sql_switcher.month(axxz0));
				buf.append("=");
				buf.append(month);
			}
			ArrayList paralist=new ArrayList();
			paralist.add(a0100);
			RowSet rset=dao.search(buf.toString(),paralist);
			if(rset.next())
				i9999=rset.getInt("I9999");
			rset.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return i9999;
	}
	
	/**
	 * 自动创建一条和上条记录一样的记录
	 * @param destname			目标数据表
	 * @param setid				数据集，前面不带应用库前缀
	 * @param a0100				人员库编号
	 * @param fields			指标列表串，以逗号分开
	 * @param nclearzero		清零标志 =1处理清零 =2不处理清零
	 * @param chgflag			按月变化标志
	 * @param strym				年月
	 * @param strcount			次数
	 * @throws GeneralException
	 */
	private int autoAppendRecord_royalty(String destname,String setid,String a0100  ,int chgflag,String strym,String strcount,ArrayList relationList,HashMap values)throws GeneralException
	{
		String strNow=null;
		String axxz0=setid+"Z0";
		String axxz1=setid+"Z1";
		StringBuffer buf=new StringBuffer();
		strNow=Sql_switcher.sqlNow();
		String strIns=",createtime,createusername";
		String strvalue=","+strNow+",'"+this.userview.getUserName()+"'";
		
		FieldItem aitem=null;
		for(int i=0;i<relationList.size();i++)
		{
			aitem=(FieldItem)relationList.get(i);
			if(!"null".equals((String)values.get(aitem.getItemid().toLowerCase())))
			{
				strIns+=","+aitem.getItemid();
				String _str=(String)values.get(aitem.getItemid().toLowerCase());
				if("D".equalsIgnoreCase(aitem.getItemtype()))
				{
					strvalue+=","+Sql_switcher.dateValue(_str);
				}
				else
				{
					strvalue+=",'"+_str+"'";
				}
			}
		} 
		int i9999=-1;
		 
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			i9999=DbNameBo.getPrimaryKey(destname, "I9999", " where a0100='"+a0100+"'", conn);
			 
			if(chgflag==0)
			{
					buf.append("insert into ");
					buf.append(destname);
					buf.append("(A0100,I9999");
					buf.append(strIns);
					buf.append(") values ('");
					buf.append(a0100);
					buf.append("',");
					buf.append(i9999);
					buf.append(strvalue);
					buf.append(")");
			}
			else
			{
					buf.append("insert into ");
					buf.append(destname);
					buf.append("(A0100,I9999");
					buf.append(strIns);
					buf.append(",");
					buf.append(axxz0);
					buf.append(",");
					buf.append(axxz1);
					buf.append(") values ('");
					buf.append(a0100);
					buf.append("',");
					buf.append(i9999);
					buf.append(strvalue);
					buf.append(",");
					buf.append(Sql_switcher.dateValue(strym));
					buf.append(",");
					buf.append(strcount);
					buf.append(")");
			}//子集类型 	
			dao.update(buf.toString());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);				
		}
		return i9999;		
	}
	
	
	
	/**
	 * 自动创建一条和上条记录一样的记录
	 * @param destname			目标数据表
	 * @param setid				数据集，前面不带应用库前缀
	 * @param a0100				人员库编号
	 * @param fields			指标列表串，以逗号分开
	 * @param nclearzero		清零标志 =1处理清零 =2不处理清零
	 * @param chgflag			按月变化标志
	 * @param strym				年月
	 * @param strcount			次数
	 * @throws GeneralException
	 */
	private int autoAppendRecord(String destname,String setid,String a0100,String fields,int nclearzero,int chgflag,String strym,String strcount)throws GeneralException
	{
		String strNow=null;
		String axxz0=setid+"Z0";
		String axxz1=setid+"Z1";
		StringBuffer buf=new StringBuffer();
		strNow=Sql_switcher.sqlNow();
		String strIns=",createtime,createusername";
		String strvalue=","+strNow+",'"+this.userview.getUserName()+"'";
		int i9999=-1;
		/** sql语句有错误 ,edit by dengcan  */
		if(fields.length()>0&& ",".equals(fields.substring(fields.length()-1)))
			fields=fields.substring(0,fields.length()-1);
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			i9999=DbNameBo.getPrimaryKey(destname, "I9999", " where a0100='"+a0100+"'", conn);
			if(nclearzero==1)//清零
			{
				if(chgflag==0)
				{
					buf.append("insert into ");
					buf.append(destname);
					buf.append("(A0100,I9999");
					buf.append(strIns);
					buf.append(") values ('");
					buf.append(a0100);
					buf.append("',");
					buf.append(i9999);
					buf.append(strvalue);
					buf.append(")");
				}
				else
				{
					buf.append("insert into ");
					buf.append(destname);
					buf.append("(A0100,I9999");
					buf.append(strIns);
					buf.append(",");
					buf.append(axxz0);
					buf.append(",");
					buf.append(axxz1);
					buf.append(") values ('");
					buf.append(a0100);
					buf.append("',");
					buf.append(i9999);
					buf.append(strvalue);
					buf.append(",");
					buf.append(Sql_switcher.dateValue(strym));
					buf.append(",");
					buf.append(strcount);
					buf.append(")");
				}//子集类型
			}
			else
			{
					if(i9999==1)//此人子集无记录
					{
						buf.append("insert into ");
						buf.append(destname);
						buf.append("(a0100,I9999");
						buf.append(strIns);
						if(chgflag!=0) //按月或按年变化子集
						{
							buf.append(",");								
							buf.append(axxz0);
							buf.append(",");
							buf.append(axxz1);
						}
						buf.append(") values ('");
						buf.append(a0100);
						buf.append("',");
						buf.append(i9999);
						buf.append(strvalue);
						if(chgflag!=0)
						{
							buf.append(",");								
							buf.append(Sql_switcher.dateValue(strym));
							buf.append(",");
							buf.append(strcount);
						}
						buf.append(")");							
					}
					else
					{
						buf.append("insert into ");
						buf.append(destname);
						buf.append("(a0100,I9999");
						buf.append(strIns);
						if(fields.length()>0)
						{
							buf.append(",");
							buf.append(fields);
						}
						if(chgflag!=0) //按月或按年变化子集
						{
							buf.append(",");								
							buf.append(axxz0);
							buf.append(",");
							buf.append(axxz1);
						}							
						buf.append(") select a0100,");
						buf.append(i9999);
						buf.append(strvalue);
						if(fields.length()>0)
						{
							buf.append(",");
							buf.append(fields);
						}
						if(chgflag!=0)
						{
							buf.append(",");								
							buf.append(Sql_switcher.dateValue(strym));
							buf.append(",");
							buf.append(strcount);
						}							
						buf.append(" from ");
						buf.append(destname);
						buf.append(" ");
						/** sql语句有错误 ，注释掉 by dengcan  */
					//	buf.append(destname);   
					//	buf.append(" as");
						buf.append(" where I9999=");
						buf.append(i9999-1);
						buf.append(" and a0100='");
						buf.append(a0100);
						buf.append("'");
					}
			}			
			dao.update(buf.toString());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);				
		}
		return i9999;		
	}
	
	
	
	/**对一个用户同时有多条薪资记录的一般子集数据进行处理
	 * 
	 * @param setlist
	 * @param isYMaddSet
	 * @param updatelist
	 * @param typelist
	 * @param dbarr
	 * @param i
	 * @param dbw
	 * @param itemUptype
	 * @param subNoPriv 数据提交入库不判断子集及指标权限
	 */
	public void dealwithMulRecord_history2(ArrayList setlist,boolean isYMaddSet,ArrayList updatelist,ArrayList typelist,String[] dbarr,int i,DbWizard dbw,HashMap itemUptype,String subNoPriv)
	{ 
		
		String a0100=null,cbase=null,strym=null,strcount=null,dessetid=null,supdate=null;
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			RowSet rset=null;
			String  royalty_valid=this.ctrlparam.getValue(SalaryCtrlParamBo.ROYALTIES,"valid");
			String royalty_relation_fields=this.ctrlparam.getValue(SalaryCtrlParamBo.ROYALTIES,"relation_fields");
			ArrayList relationfieldList=new ArrayList();
			String royalty_setid=this.ctrlparam.getValue(SalaryCtrlParamBo.ROYALTIES,"setid");
			StringBuffer relationFieldStr=new StringBuffer("");
			String relation_str="";
			 
			if("1".equalsIgnoreCase(royalty_valid)&&royalty_relation_fields.length()>0)
			{
				String[] temps=royalty_relation_fields.toLowerCase().split(",");
				for(int n=0;n<temps.length;n++)
				{
					if(temps[n].trim().length()>0&&DataDictionary.getFieldItem(temps[n].trim())!=null)
					{ 
						relationfieldList.add(DataDictionary.getFieldItem(temps[n].toLowerCase()));
						relationFieldStr.append(","+temps[n]);
					}
				}
			}  
			cbase=dbarr[i];
		
			 String salaryhistory_tmp="t#"+this.userview.getUserName()+"_gz_3";
			String tempName="t#"+this.userview.getUserName()+"_gz";  
			if(dbw.isExistTable(tempName,false))
			{
				dbw.dropTable(tempName); 
			}
			StringBuffer sql0=new StringBuffer("");
			if(Sql_switcher.searchDbServer()==2)
				sql0.append("create table "+tempName+" as ");
			sql0.append("select aa.a0100 ");
			if(Sql_switcher.searchDbServer()!=2)
				sql0.append(" into "+tempName);
			sql0.append(" from  (select count(a0100) c,a0100 from  "+salaryhistory_tmp+this._withNoLock+" where  1=1     and lower("+salaryhistory_tmp+".NBASE)='"+cbase.toLowerCase()+"'  group by a0100  having count(a0100)>1 ) aa");
			dao.update(sql0.toString());
			String singleRecord_where="select a0100 from "+tempName;
 
			
			
			
			StringBuffer buf=new StringBuffer("");
			if("1".equals(royalty_valid)&&royalty_relation_fields.length()>0)
				buf.append("select * from ");
			else
				buf.append("select a0100,nbase,a00z0,a00z1,add_flag,userflag from ");
			buf.append(salaryhistory_tmp);
			buf.append(" where a0100 in ("+singleRecord_where+")    and lower("+salaryhistory_tmp+".NBASE)='"+cbase.toLowerCase()+"'  and exists (select null from "+cbase+"A01 where "+cbase+"A01.a0100="+salaryhistory_tmp+".a0100 )  ");
			 
			rset=dao.search(buf.toString()+" order by a00z0,a00z1"); 
			ArrayList updateList=new ArrayList();
			int ni9999=-1;
			int add_flag=0;
			SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
			
			while(rset.next())
			{
				int num=0;
				a0100=rset.getString("a0100");
				if(rset.getString("add_flag")!=null)
					add_flag=rset.getInt("add_flag");
				else
					add_flag=0;
				String userflag=rset.getString("userflag");
				
				//cbase=rset.getString("nbase");
				strym=PubFunc.FormatDate(rset.getDate("a00z0"), "yyyy-MM-dd");
				strcount=rset.getString("a00z1");
				String ori_count=strcount;
				
				/*查找薪资类别项目涉及各年月变化子集本月最大次数加1的值更新为临时表中对应记录的
				* 归属次数并将工资数据临时表 中记录的add_flag值置为 1，当记录 add_flag值为1时，按旧的程序处理逻辑。*/
				if(isYMaddSet&&add_flag==0&&num==0)
				{
								int z1=getNewA00z1(setlist,strym,dbarr[i],a0100,typelist);
								num++;
								if(Integer.parseInt(strcount)<z1)
								{
									int history_z1=getNewA00z1_history(strym,dbarr[i],a0100,strcount);
									if(history_z1>Integer.parseInt(strcount)&&history_z1>z1)
										z1=history_z1;
									synchronousZ1_history(strym,strcount,z1,a0100,dbarr[i],updateList,userflag.toLowerCase());
									strcount=String.valueOf(z1);
								}
				}
				
				for(int j=0;j<setlist.size();j++)
				{
					String setid=(String)setlist.get(j);
					if("A00".equalsIgnoreCase(setid))
						continue;
					String fields=(String)updatelist.get(j);
					/**(0,1,2,3)=(更新,新增,不变,更新(薪资重发时))*/
					String type=(String)typelist.get(j);
					if("3".equals(type))
						type="0";
					if("1".equals(royalty_valid)&&royalty_setid.equalsIgnoreCase(setid))
						type="0";
					if("2".equalsIgnoreCase(type))//当前记录不变
						continue;
					/**子集未构库不提交*/
					FieldSet fieldset=DataDictionary.getFieldSetVo(setid);
					if(fieldset==null|| "0".equalsIgnoreCase(fieldset.getUseflag()))
						continue;
					if("0".equals(subNoPriv))
					{
						if(!"2".equals(this.userview.analyseTablePriv(setid.toUpperCase())))
							continue;
					}
					/**(0,1,2)=(一般,按月变化,按年变化)*/
					int nflag=Integer.parseInt(fieldset.getChangeflag());
					int ntype=Integer.parseInt(type);
					
					
					
					switch(setid.charAt(0))
					{
					case 'A'://人员库
						if(!"A01".equalsIgnoreCase(setid))//处理非主集
						{
							dessetid=dbarr[i]+setid;
							supdate=(String)updatelist.get(j);
							
							HashMap values=new HashMap();
							if("1".equals(royalty_valid)&&royalty_setid.equalsIgnoreCase(setid))
							{
								FieldItem aitem=null;
								for(int n=0;n<relationfieldList.size();n++)
								{
									aitem=(FieldItem)relationfieldList.get(n);
									if("D".equalsIgnoreCase(aitem.getItemtype()))
									{
										if(rset.getDate(aitem.getItemid())==null)//||rset.getString(aitem.getItemid()).trim().length()==0)
											values.put(aitem.getItemid().toLowerCase(),"null");
										else
											values.put(aitem.getItemid().toLowerCase(),df.format(rset.getDate(aitem.getItemid())));
											
									}
									else if("A".equalsIgnoreCase(aitem.getItemtype()))
									{
										if(rset.getString(aitem.getItemid())==null||rset.getString(aitem.getItemid()).trim().length()==0)
											values.put(aitem.getItemid().toLowerCase(),"null");
										else
											values.put(aitem.getItemid().toLowerCase(),rset.getString(aitem.getItemid()));
									} 
								}
							}
							
							
							
							
							switch(ntype)
							{
							case 0://更新
								switch(nflag)
								{
									case 1://按月变化
									case 2://按年变化
										if("1".equals(royalty_valid)&&royalty_setid.equalsIgnoreCase(setid))
											ni9999=isHaveCurrentDateRecord_royalty(dessetid,setid,a0100,relationfieldList,values);
										else
											ni9999=isHaveCurrentDateRecord(dessetid, setid, a0100, nflag, strym, strcount);
										/**如果没有记录，则自动创建一条记录和上条记录完全一条*/
										if(ni9999==-1)
										{
											if("1".equals(royalty_valid)&&royalty_setid.equalsIgnoreCase(setid))
												ni9999= autoAppendRecord_royalty(dessetid,setid,a0100,nflag,strym,strcount,relationfieldList,values);
											else
												ni9999=autoAppendRecord(dessetid,setid,a0100,supdate,2,nflag,strym,strcount);
										}
										break;
									case 0://一般子集
										if("1".equals(royalty_valid)&&royalty_setid.equalsIgnoreCase(setid))
										{
											ni9999=isHaveCurrentDateRecord_royalty(dessetid,setid,a0100,relationfieldList,values);
											if(ni9999==-1)
												ni9999= autoAppendRecord_royalty(dessetid,setid,a0100,nflag,strym,strcount,relationfieldList,values);
										}
										else
											ni9999=DbNameBo.getPrimaryKey(dessetid, "I9999", " where a0100='"+a0100+"'", conn)-1;
										break;
								}
								break;
							case 1://追加记录
								switch(nflag)
								{
								case 1://按月变化
								case 2://按年变化
									ni9999=isHaveCurrentDateRecord(dessetid, setid, a0100, nflag, strym, strcount);
									/**如果没有记录，则自动创建一条记录和上条记录完全一条*/
									if(ni9999==-1)
										ni9999=autoAppendRecord(dessetid,setid,a0100,supdate,1,nflag,strym,strcount);
									break;
								case 0://一般子集
									ni9999=autoAppendRecord(dessetid,setid,a0100,supdate,1,nflag,strym,strcount);
									break;
								}
								break;
							}//操作方式
							if(nflag==0)//只有一般变化子集，才一个人一个人提交数据
							{
								
								String year=strym.substring(0, 4);
								String month=strym.substring(5, 7); 
								
								String value=getUpdateSQL(dessetid,salaryhistory_tmp,fields,nflag,itemUptype);
								StringBuffer strSWhere=new StringBuffer();
								StringBuffer strDWhere=new StringBuffer();
								if(Sql_switcher.searchDbServer()==Constant.ORACEL)
								{
									strSWhere.append(dessetid);
									strSWhere.append(".A0100='");
									strSWhere.append(a0100);
									strSWhere.append("' and ");
									strSWhere.append(dessetid);
									strSWhere.append(".I9999=");
									strSWhere.append(ni9999);
									 
									
									strDWhere.append(dessetid);
									strDWhere.append(".A0100='");
									strDWhere.append(a0100);
									strDWhere.append("' and ");
									strDWhere.append(dessetid);
									strDWhere.append(".I9999=");
									strDWhere.append(ni9999);	
									strDWhere.append(" and lower(");
									strDWhere.append(salaryhistory_tmp);
									strDWhere.append(".NBASE)='");
									strDWhere.append(cbase.toLowerCase()+"'  ");
									
									strDWhere.append(" and "+Sql_switcher.year(salaryhistory_tmp+".a00z0"));
									strDWhere.append("=");
									strDWhere.append(year);
									strDWhere.append(" and "+Sql_switcher.month(salaryhistory_tmp+".a00z0"));
									strDWhere.append("=");
									strDWhere.append(month);
									strDWhere.append(" and "+salaryhistory_tmp+".a00z1");
									strDWhere.append("=");
								//	strDWhere.append(strcount);		
									strDWhere.append(ori_count);	
									
									 
									
								}
								else
								{
									strSWhere.append(dessetid);
									strSWhere.append(".A0100='");
									strSWhere.append(a0100);
									strSWhere.append("' and ");
									strSWhere.append(dessetid);
									strSWhere.append(".I9999=");
									strSWhere.append(ni9999);
									strSWhere.append(" and lower(");
									strSWhere.append(salaryhistory_tmp);
									strSWhere.append(".NBASE)='");
									strSWhere.append(cbase.toLowerCase()+"'  ");
									
									strSWhere.append(" and "+Sql_switcher.year(salaryhistory_tmp+".a00z0"));
									strSWhere.append("=");
									strSWhere.append(year);
									strSWhere.append(" and "+Sql_switcher.month(salaryhistory_tmp+".a00z0"));
									strSWhere.append("=");
									strSWhere.append(month);
									strSWhere.append(" and "+this.gz_tablename+".a00z1");
									strSWhere.append("="); 	
//									strDWhere.append(strcount);		
									strSWhere.append(ori_count);
 
									
									strDWhere.append(dessetid);
									strDWhere.append(".A0100='");
									strDWhere.append(a0100);
									strDWhere.append("' and ");
									strDWhere.append(dessetid);
									strDWhere.append(".I9999=");
									strDWhere.append(ni9999);	
								} 
								
								if("1".equals(royalty_valid)&&royalty_setid.equalsIgnoreCase(setid))
								{
									FieldItem aitem=null;
									for(int n=0;n<relationfieldList.size();n++)
									{
										aitem=(FieldItem)relationfieldList.get(n); 
										if(!"null".equalsIgnoreCase((String)values.get(aitem.getItemid())))
										{
												if("D".equalsIgnoreCase(aitem.getItemtype()))
													strDWhere.append(" and "+Sql_switcher.isnull(Sql_switcher.dateToChar(salaryhistory_tmp+"."+aitem.getItemid(),"YYYY-MM-DD"),"''")+"='"+(String)values.get(aitem.getItemid())+"' ");
												else
													strDWhere.append(" and "+Sql_switcher.isnull(salaryhistory_tmp+"."+aitem.getItemid(),"''")+"='"+(String)values.get(aitem.getItemid())+"' "); 
										}
										else
										{
												if(Sql_switcher.searchDbServer()==Constant.ORACEL)
													strDWhere.append(" and "+salaryhistory_tmp+"."+aitem.getItemid() +" is null "); 
												else
													strDWhere.append(" and "+Sql_switcher.isnull(salaryhistory_tmp+"."+aitem.getItemid(),"''")+"='' "); 
										}
											 
									} 
								}
								
								
								
								dbw.updateRecord(dessetid, salaryhistory_tmp,dessetid+".A0100="+salaryhistory_tmp+".A0100", value, strSWhere.toString(), strDWhere.toString());
							}
						}//if 非主集
						
						break;
					}//子集
				}//for setlist 数据集loop end.
				
				if(!ori_count.equals(strcount))
				{
					String year=strym.substring(0, 4);
					String month=strym.substring(5, 7);
					String sql="update "+userflag+"_salary_"+this.salaryid+" set a00z1="+strcount+",add_flag=1 where lower(nbase)='"+dbarr[i].toLowerCase()+"' and a0100='"+a0100+"'"
					+" and "+Sql_switcher.year("a00z0")+"="+year+" and "+Sql_switcher.month("a00z0")+"="+month+" and a00z1="+ori_count;
					dao.update(sql);
				}
				
				
			}//for while end.
			if(rset!=null)
				rset.close();
//			同步 薪资临时表 及 历史表里的a00z1
			if(updateList.size()>0)
			{
			//	 String[] _temps=this.gz_tablename.split("_salary_");
			//	String sql="update "+this.gz_tablename+" set a00z1=?,add_flag=1 where lower(nbase)='"+dbarr[i].toLowerCase()+"' and a0100=? "
			//	+" and "+Sql_switcher.year("a00z0")+"=? and "+Sql_switcher.month("a00z0")+"=? and a00z1=? ";
				String sql2="update salaryhistory  set a00z1=?,add_flag=1 where lower(nbase)='"+dbarr[i].toLowerCase()+"' and lower(userflag)=? and a0100=? "
				+" and "+Sql_switcher.year("a00z0")+"=? and "+Sql_switcher.month("a00z0")+"=? and a00z1=? and salaryid="+this.salaryid;
		//		dao.batchUpdate(sql, updateList);
				dao.batchUpdate(sql2, updateList);
				
				sql2="update gz_tax_mx  set a00z1=? where lower(nbase)='"+dbarr[i].toLowerCase()+"' and lower(userflag)=?  and a0100=? "
				+" and "+Sql_switcher.year("a00z0")+"=? and "+Sql_switcher.month("a00z0")+"=? and a00z1=? and salaryid="+this.salaryid;
				dao.batchUpdate(sql2, updateList);
				
			}
			
		//	dao.update("update "+this.gz_tablename+" set  add_flag=1 where lower(nbase)='"+dbarr[i].toLowerCase()+"'  "+filterWhl);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
    
	/**
	 * 根据节点属性获取lprogram中的值
	 * zhaoxg add 2015-10-12
	 * @param attriName
	 * @param nodeValue
	 * @return
	 */
	public String getLprogramAttri(String attriName,int nodeValue)
	{
		String value="";
		SalaryLProgramBo lpbo=new SalaryLProgramBo(this.templatevo.getString("lprogram"));
		value=lpbo.getValue(nodeValue,attriName);
		return value;
	}
	
	/**
	 * @Title: isHashInTable
	 * @Description: TODO(根据表名得到scheme_id)
	 * @param tableName
	 *            薪资帐套临时表名称，经过编码和加密
	 * @author lis
	 * @throws GeneralException
	 * @date 2015-7-22 下午01:24:45
	 */
	public int getSchemeId(String tableName) throws GeneralException {
		RowSet rowSet = null;
		int scheme_id = -1;
		if(StringUtils.isBlank(tableName))
			return scheme_id;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			ArrayList list = new ArrayList();
			
			
			
			String sql = "select scheme_id from t_sys_table_scheme  where submoduleid=? and username=? and is_share = '0'";
			list.add(tableName);
			list.add(this.userview.getUserName());
			rowSet = dao.search(sql, list);
			if (rowSet.next())
				scheme_id = rowSet.getInt("scheme_id");
			else{
				sql = "select scheme_id from t_sys_table_scheme  where submoduleid=? and is_share = '1' ";
				list.remove(1);//xiegh 20170401 26774
				rowSet = dao.search(sql, list);
				if (rowSet.next())
					scheme_id = rowSet.getInt("scheme_id");
			}
			return scheme_id;
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(rowSet);
		}
	}
	
	/**
	 * @Title: getTableItems
	 * @Description: TODO(从栏目设置中查找薪资项目)
	 * @param scheme_id
	 *            栏目设置id
	 * @return ArrayList<String>
	 * @author lis
	 * @throws GeneralException
	 * @date 2015-7-22 下午01:26:20
	 */
	public Map<String,Integer> getTableItems(int schemeId,String display)
			throws GeneralException {
		RowSet rowSet = null;
		Map<String,Integer> filedList = new HashMap<String,Integer>();
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sql = new StringBuffer("select itemid,displayorder from t_sys_table_scheme_item  where ");
			ArrayList list = new ArrayList();
			if(StringUtils.isNotBlank(display)){
				sql.append(" is_display=? and ");
				list.add(display);
			}
			sql.append(" scheme_id =? order by displayorder");
			list.add(schemeId);
			// 从表t_sys_table_scheme_item中查询itemid
			rowSet = dao.search(sql.toString(), list);
			int count = 0;
			while (rowSet.next()) {
				String itemid = (String) rowSet.getString("itemid");
				filedList.put(itemid.toLowerCase(),count++);
			}
			return filedList;
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(rowSet);
		}
	}
	/**
	 * 根据栏目设置取查找薪资项目
	 * @param schemeId
	 * @param display
	 * @return
	 * @throws GeneralException
	 * @author zhanghua
	 * @date 2017年7月11日 上午11:49:41
	 */
	public ArrayList<HashMap> getTableItemsToMap(int schemeId,String display)
			throws GeneralException {
		RowSet rowSet = null;
		try {
			ArrayList<HashMap> filedList = new ArrayList<HashMap>();// 封装excel表头数据
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sql = new StringBuffer("select itemid,displaywidth,align,is_sum,mergedesc from t_sys_table_scheme_item  where ");
			ArrayList list = new ArrayList();
			if(StringUtils.isNotBlank(display)){
				sql.append(" is_display=? and ");
				list.add(display);
			}
			sql.append(" scheme_id =? order by displayorder");
			list.add(schemeId);
			// 从表t_sys_table_scheme_item中查询itemid
			rowSet = dao.search(sql.toString(), list);
			while (rowSet.next()) {
				HashMap map=new HashMap();
				String mergedesc = rowSet.getString("mergedesc");
				map.put("itemid",(String) rowSet.getString("itemid") );
				map.put("displaywidth", rowSet.getInt("displaywidth") );
				map.put("align", rowSet.getShort("align") );
				map.put("is_sum", (String) rowSet.getString("is_sum") );
				map.put("mergedesc", (StringUtils.isBlank(mergedesc) || "null".equalsIgnoreCase(mergedesc)) ? "" : rowSet.getString("mergedesc") );
				filedList.add(map);
			}
			return filedList;
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(rowSet);
		}
	}
	
	/**
	 * 根据栏目设置取查找薪资项目{id:width}
	 * @param schemeId
	 * @param display
	 * @return
	 * @throws GeneralException
	 */
	public HashMap<String, Integer> getTableItemsWithToMap(int schemeId,String display)
			throws GeneralException {
		RowSet rowSet = null;
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sql = new StringBuffer("select itemid,displaywidth from t_sys_table_scheme_item  where ");
			ArrayList list = new ArrayList();
			if(StringUtils.isNotBlank(display)){
				sql.append(" is_display=? and ");
				list.add(display);
			}
			sql.append(" scheme_id =? order by displayorder");
			list.add(schemeId);
			// 从表t_sys_table_scheme_item中查询itemid
			rowSet = dao.search(sql.toString(), list);
			while (rowSet.next()) {
				map.put(rowSet.getString("itemid").toLowerCase(), rowSet.getInt("displaywidth"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(rowSet);
		}
		return map;
	}
	
	/**
	 * @Title: getHeadItemList
	 * @Description: TODO(得到显示的薪资项目)
	 * @param itemSetList
	 *            所有薪资项目
	 * @return ArrayList<LazyDynaBean>
	 * @param map 栏目设置的map集合<itemid,isDisplay>
	 * @param isDisplay 可以设置是不是需要全部，或者只需要显示的等
	 * @param flag 是否通过查找全部指标后，将薪资新增的，但不在栏目设置中的加入（前提是map是查找全部的栏目设置列）
	 * @throws GeneralException
	 * @author sunjian
	 * @date 2017-8-30 
	 */
	public ArrayList<LazyDynaBean> getSchemedHeadItemList(ArrayList<LazyDynaBean> itemSetList,Map<String,Integer> map) throws GeneralException {
		ArrayList<LazyDynaBean> itemList = new ArrayList<LazyDynaBean>();
		HashMap<Integer, LazyDynaBean> map_lazy=new HashMap<Integer, LazyDynaBean>();
		try {
			
			//排去所有非数值型的字段，并将对应的bean按照（顺序号，bean）塞入map
			ArrayList<LazyDynaBean> itemSetListClone = (ArrayList<LazyDynaBean>) itemSetList.clone();
			for(int i = 0; i<itemSetList.size(); i++){
				LazyDynaBean bean=itemSetList.get(i);
				String itemid = (String)bean.get("itemid");
				if(map.get(itemid.toLowerCase()) != null) {
					map_lazy.put((int)map.get(itemid.toLowerCase()), bean);
				}
			}
			//根据顺序，将bean塞入list中
			for(int i = 0; i <= map.size(); i++) {
				if(map_lazy.get(i) != null) {
					itemList.add((LazyDynaBean)map_lazy.get(i));
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return itemList;
	}
	
	/**
	 * 通过栏目设置生成的map 进行排序
	 */
	public ArrayList<LazyDynaBean> getSchemedHeadHashMap(ArrayList<LazyDynaBean> itemSetList,ArrayList<HashMap> itemIdList) throws GeneralException {
		ArrayList<LazyDynaBean> itemList = new ArrayList<LazyDynaBean>();
		try {
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
			String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
			if(StringUtils.isBlank(display_e0122)|| "00".equals(display_e0122))
				display_e0122="0";		
			for(HashMap map :itemIdList){
				int i=0;
				String itemid=(String)map.get("itemid");
				Integer displaywidth=(Integer)map.get("displaywidth");
				Object align = (Short) map.get("align");
				HorizontalAlignment horizontAlign= HorizontalAlignment.CENTER;
				if(align instanceof Short) {
					short alignMent = (Short) align;
					if(alignMent == 1)
						horizontAlign = HorizontalAlignment.LEFT;
					else if(alignMent == 3)
						horizontAlign = HorizontalAlignment.RIGHT;
					
				} else 
					horizontAlign = (HorizontalAlignment)map.get("align");
				
				String is_sum=(String)map.get("is_sum");
				String mergedesc=(String)map.get("mergedesc");
				while(i<itemSetList.size()){
					LazyDynaBean bean=itemSetList.get(i);
					String codeSetId = (String)bean.get("codesetid");
					if(itemid.equalsIgnoreCase((String)bean.get("itemid"))){
						//单位部门岗位长度不设置，excel控件自己去拿
						if(!"0".equals(display_e0122) && ("UM".equalsIgnoreCase(codeSetId) || "UN".equalsIgnoreCase(codeSetId) || "@K".equalsIgnoreCase(codeSetId)))
							bean.set("displaywidth", 0);
						else
							bean.set("displaywidth", displaywidth*30);
						bean.set("align", align);
						bean.set("is_sum", is_sum);
						bean.set("mergedesc", mergedesc);
						itemList.add(bean);
						break;
					}
					i++;
				}
				if(i!=itemSetList.size())
					itemSetList.remove(i);
				
			}
			for(LazyDynaBean bean:itemSetList){//数字型默认合计
				if("N".equalsIgnoreCase(bean.get("itemtype").toString())&&!"0".equals(bean.get("nwidth").toString()))
					bean.set("is_sum","1");
			}
			itemList.addAll(itemSetList);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return itemList;
	}
	
	/**
	 * @Title: getHeadItemList
	 * @Description: TODO(得到显示的薪资项目)
	 * @param itemSetList
	 *            所有薪资项目
	 * @param scheme_id
	 *            栏目设置id
	 * @return ArrayList<LazyDynaBean>
	 * @throws GeneralException
	 * @author lis
	 * @date 2015-7-22 上午11:43:56
	 */
	public ArrayList<FieldItem> getSchemedItemList(ArrayList<FieldItem> itemSetList,ArrayList<String> itemIdList) throws GeneralException {
		ArrayList<FieldItem> itemList = new ArrayList<FieldItem>();
		try {
			// 进行过滤得到薪资项目
			for (int i = 0; i < itemSetList.size(); i++) {
				FieldItem item = itemSetList.get(i);
				if (itemIdList.contains(item.getItemid().toLowerCase()))
					itemList.add(item);
				else
					continue;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return itemList;
	}
	/**
	 * 
	 * @Title: SalarySet   
	 * @Description:    判断哪些字段改变了需要同步
	 * @param  
	 * @return void 
	 * @author:zhaoxg   
	 * @throws GeneralException 
	 * @throws
	 */
	public void SalarySet(ArrayList itemList) throws GeneralException
	{
		RowSet rowField=null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String whl=" and itemid not in ('B0110','A00Z1','A00Z0','A00Z2','A00Z3','A0000','A0100','NBASE'," +
					"'A01Z0')";//,'E01A1'放开岗位，新增时可能需要添加 zhanghua 2017-8-16
			itemList=(ArrayList)itemList.clone();
			if(this.getManager()!=null&&this.getManager().trim().length()>0){
				LazyDynaBean bean=new LazyDynaBean();
				bean.set("fieldsetid","A00");
				bean.set("itemid","sp_flag2");
				bean.set("itemdesc","报审状态");
				bean.set("codesetid","23");
				bean.set("itemlength","2");
				bean.set("nwidth","0");
				bean.set("decwidth","50");
				bean.set("itemtype","A");
				itemList.add(bean);
			}

			boolean flag=false;
			StringBuffer str= new StringBuffer();
			StringBuffer itemname = new StringBuffer();
			ArrayList salarysetList=new ArrayList();
			HashMap salarysetMap=new HashMap<String,FieldItem >();
			String differentField="";
			HashMap fieldB=new HashMap<String, String>();
			LazyDynaBean abean=null;
			for(Iterator t=itemList.iterator();t.hasNext();)
			{
				abean=(LazyDynaBean)t.next();
				String itemid=(String)abean.get("itemid");
				if(whl.indexOf("'"+itemid.toUpperCase()+"'")!=-1)
					continue;
				FieldItem item = DataDictionary.getFieldItem(itemid);
				if(!"sp_flag2".equalsIgnoreCase(itemid)&&(item == null || "0".equals(item.getUseflag())))
					itemname.append((String)abean.get("itemdesc"));
				
				
				FieldItem field=new FieldItem();
				field.setFieldsetid((String)abean.get("fieldsetid"));
				field.setItemid((String)abean.get("itemid"));
				field.setItemdesc((String)abean.get("itemdesc"));
				field.setItemtype((String)abean.get("itemtype"));
				field.setItemlength(Integer.parseInt((String)abean.get("itemlength")));
				field.setDisplaywidth(Integer.parseInt((String)abean.get("nwidth")));
				field.setDecimalwidth(Integer.parseInt((String)abean.get("decwidth")));
				field.setCodesetid((String)abean.get("codesetid"));
				field.setVarible(0);
				salarysetMap.put(itemid.toLowerCase(), field);
					
				salarysetList.add(itemid.toUpperCase());
				
				str.append("'"+itemid.toUpperCase()+"'");
				str.append(",");
				flag=true;
			}
			if(flag==false)
				return;
			ArrayList strSql=new ArrayList();
			DbWizard dbWizard = new DbWizard(this.conn);
			if (dbWizard.isExistTable(this.gz_tablename, false)) {
				strSql.add("select * from  "+this.gz_tablename+" where 1=2");
			}else
				strSql.add("");
			if (dbWizard.isExistTable("salaryarchive", false)) {
				strSql.add("select * from  salaryarchive  where 1=2");
			}else
				strSql.add("");
			if (dbWizard.isExistTable("salaryhistory", false)) {
				strSql.add("select * from  salaryhistory where 1=2");
			}else
				strSql.add("");
			
			for(int i=0;i<strSql.size();i++){
				if(StringUtils.isBlank((String)strSql.get(i)))
					continue;
				fieldB=new HashMap(); 
				rowField=dao.search((String)strSql.get(i));
				ResultSetMetaData metaData=rowField.getMetaData();
				for(int j=1;j<=metaData.getColumnCount();j++){
					String ColumnName=metaData.getColumnName(j).toUpperCase();
					fieldB.put(ColumnName, ColumnName);
				}
				differentField=getDifferentField(salarysetList,fieldB);
				//若存在新增列 则自动升级薪资表。无需通过结构同步 zhanghua 2017-7-12
				if(StringUtils.isNotBlank(differentField)){
					String [] strDifferent=differentField.split(",");
					String tableName="";
					switch(i){
						case 0:tableName=this.gz_tablename;break;
						case 1:tableName="salaryarchive";break;
						case 2:tableName="salaryhistory";break;
					}
					Table table=new Table(tableName);
					for(int k=0;k<strDifferent.length;k++){
						String strk=strDifferent[k].replaceAll("'", "").toLowerCase();
						table.addField((FieldItem)salarysetMap.get(strk));
					}
					dbWizard.addColumns(table);
				}
			}
				if(flag){//没指标则不执行任何操作
					for(Iterator t=itemList.iterator();t.hasNext();)
					{
						abean=(LazyDynaBean)t.next();
						String itemid=(String)abean.get("itemid");
						String fieldid=(String)abean.get("fieldid");
						if(whl.indexOf("'"+itemid.toUpperCase()+"'")!=-1)
							continue;
						int itemlength=Integer.parseInt((String)abean.get("itemlength"));
						String codesetid=(String)abean.get("codesetid");
						String itemtype=(String)abean.get("itemtype");
						String itemdesc=(String)abean.get("itemdesc");
						int decwidth=Integer.parseInt((String)abean.get("decwidth"));
						if(DataDictionary.getFieldItem(itemid.toLowerCase())!=null&& "1".equals(DataDictionary.getFieldItem(itemid.toLowerCase()).getUseflag()))
						{
							FieldItem item=DataDictionary.getFieldItem(itemid.toLowerCase());
							if(!(itemlength==item.getItemlength())
									||!(decwidth==item.getDecimalwidth())
									||!codesetid.equalsIgnoreCase(item.getCodesetid())
									||!itemtype.equalsIgnoreCase(item.getItemtype())
									||!itemdesc.equalsIgnoreCase(item.getItemdesc())
									){//这块不考虑汉字名字
								itemname.append((String) item.getItemdesc()+",");
							}
						}
							
					}
				}
			if(itemname.length()>0){
				throw GeneralExceptionHandler.Handle(new Exception(itemname+"指标属性发生了变化，请到类别维护进行结构同步后再使用！"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		finally
		{
			PubFunc.closeDbObj(rowField);
		}
	}
	/**
	 * 
	 * @Title: isHaveUnits   
	 * @Description: //是否满足所属单位控制  0：满足 1：不满足
	 * @param @return
	 * @param @throws GeneralException 
	 * @return String 
	 * @author:zhaoxg   
	 * @throws
	 */
	public String isHaveUnits() throws GeneralException{
		String ishave = "1";//是否满足所属单位控制  0：满足 1：不满足  zhaoxg add 2016-12-14
		try{
			String unitcodes=this.userview.getUnitIdByBusi("1");  //UM010101`UM010105` 
			String[] units = unitcodes.split("`");
			String b0110 = this.getCtrlparam().getSubordinateunits();
			if(StringUtils.isBlank(b0110)||this.userview.isSuper_admin()|| "UN`".equalsIgnoreCase(unitcodes)){//超级用户或者全部则可以控制
				ishave = "0";
			}else{
				for(int i=0;i<units.length;i++)
				{
					String codeid=units[i];
					if(codeid==null|| "".equals(codeid))
						continue;
					if(codeid!=null&&codeid.trim().length()>2)
					{
						String privCode = codeid.substring(0,2);
						String privCodeValue = codeid.substring(2);
						if(b0110.length()>=privCodeValue.length()){//人的范围要大于等于所属单位才行，所以操作单位长
							if(b0110.substring(0, privCodeValue.length()).equalsIgnoreCase(privCodeValue)){
								ishave = "0";
							}
						}
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return ishave;
	}
	/**
	 * 
	 * @Title: updateSalaryTable   
	 * @Description:为薪资临时表同步一些个性字段  
	 * @param @param tableName
	 * @param @param setbo
	 * @param @throws GeneralException 
	 * @return void 
	 * @author:zhaoxg   
	 * @throws
	 */
	public void updateSalaryTable(String tableName,SalarySetBo setbo) throws GeneralException{
		try{
			DbWizard dbw=new DbWizard(this.conn);
			if(this.getManager()!=null&&this.getManager().trim().length()>0&&!dbw.isExistField(tableName, "sp_flag2",false))//如果从不共享设置成共享，没有则加上sp_flag2 防止报错 zhaoxg add 2016-12-21
			{
				Table table=new Table(tableName);
				Field field=new Field("sp_flag2","sp_flag2");
				field.setDatatype(DataType.STRING);
				field.setLength(2);
				table.addField(field);
				dbw.addColumns(table);	
				ContentDAO dao=new ContentDAO(this.conn);
				dao.update("update " + tableName + " set sp_flag2 = '01'");//从不共享设置成共享，添加所有报审状态为01，页面不现实起草斌且报审出错，报审的时候判断是sp_flag=01或07
			}
			if((setbo.isApprove()||(this.getManager()!=null&&this.getManager().trim().length()>0))&&!dbw.isExistField(tableName, "appprocess",false)){//需要审批且无appprocess字段，则新增进去，防止报错  zhaoxg add 2016-9-13
				Table table=new Table(tableName);
				Field field=new Field("appprocess","appprocess");
				field.setDatatype(DataType.CLOB);
				table.addField(field);
				dbw.addColumns(table);	
			}
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	/**
	 * 取A数组中存在 B数组中没有的字段
	 * @param fieldA salaryset字段
	 * @param fieldB 其他表字段
	 * @return
	 */
	private String getDifferentField(ArrayList fieldA,HashMap fieldB){
		StringBuffer strDifferent=new StringBuffer();
		Object j=null;
		for(int i=0;i<fieldA.size();i++){
			j=fieldB.remove(fieldA.get(i));//A表里有 b表里没有
			if(j==null)
				strDifferent.append("'"+fieldA.get(i)+"',");
		}
		return strDifferent.toString();
	}
	
	/**
	 * system.properties  salaryitem=false前台计算项不能编辑
	 * @Title: setFieldlist_readOnly   
	 * @Description:    
	 * @param @param fieldlist 
	 * @return void    
	 * @throws
	 */
	public void setFieldlist_readOnly(ArrayList fieldlist) {

		try
		{
			ArrayList formulaList=getFormulaList(-1,"" + this.salaryid,null);
			HashMap map=new HashMap();
			for(int i=0;i<formulaList.size();i++)
			{
				  DynaBean dbean=(LazyDynaBean)formulaList.get(i);
				  String itemname=(String)dbean.get("itemname");
				  map.put(itemname.toLowerCase(),"1");
			}
			
			
			FieldItem field=null;
			for(int i=0;i<fieldlist.size();i++)
			{
				field=(FieldItem)fieldlist.get(i);
				if(map.get(field.getItemid().toLowerCase())!=null)
					field.setReadonly(true);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	
	}
	
	/**
	 * @Title: getHeadList 
	 * @Description: TODO(过滤以后返回可以导出的薪资项目) 
	 * @param headItemList 可以显示的薪资项目
	 * @param calcuItemMap 公式计算项
	 * @return ArrayList
	 * @throws GeneralException
	 * @author lis  
	 * @date 2015-7-25 下午04:36:01
	 */
	public ArrayList<LazyDynaBean> getHeadList(ArrayList<LazyDynaBean> headItemList,HashMap calcuItemMap) throws GeneralException{
		try {
			//这里是对excel_template_limit=true进行控制，因为要让选择指标进行导出，对于设置的export_limits的因为是要导出的进行选择性添加
			ArrayList<LazyDynaBean> headList = new ArrayList<LazyDynaBean>();
			// export_limits:设置可以导出的只读指标项
			HashMap readOnlyFldsCanExport = new HashMap();
			String export_limits = SystemConfig.getPropertyValue("export_limits");
			if (StringUtils.isNotBlank(export_limits))
			{
				String[] readOnlyFlds = export_limits.split(",");
				for (int m = 0; m < readOnlyFlds.length; m++)
				{
					String temp = readOnlyFlds[m].trim();
					if (temp.length() > 0)
						readOnlyFldsCanExport.put(temp.toUpperCase(), "");
				}
			}
			
			String initFlag = null;
			for(int i=0;i<headItemList.size();i++){
				LazyDynaBean bean = headItemList.get(i);
				FieldItem item=DataDictionary.getFieldItem((String)bean.get("itemid"));
				initFlag = (String)bean.get("initflag");
				//排除系统项，但包含姓名、单位、部门
				if("3".equals(initFlag) && !"B0110".equals((String)headItemList.get(i).get("itemid")) && !"E0122".equals((String)headItemList.get(i).get("itemid")) && !"A0101".equals((String)headItemList.get(i).get("itemid")))
					continue;
				if (SystemConfig.getPropertyValue("excel_template_limit") != null && "true".equalsIgnoreCase(SystemConfig.getPropertyValue("excel_template_limit")))
				{
					// 去除公式计算项了
					if (calcuItemMap.get(((String)bean.get("itemid")).toLowerCase()) != null)
						continue;
					
					// 去除只读项
					if(item!=null)
					{
						String pri = userview.analyseFieldPriv((String)bean.get("itemid"));
						if ("1".equals(pri))// 只读
						{
							if (readOnlyFldsCanExport.size() > 0)
							{
								// 不属于允许导出的只读项
								if (readOnlyFldsCanExport.get(((String)bean.get("itemid")).toUpperCase()) == null)
									continue;
							} else
								// 没有设置允许导出的只读项
								continue;
						}
					}
				}
				headList.add(headItemList.get(i));
			}
			return headList;
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	/**
	 * 查询薪资类别项目来源的指标列表
	 * @param salaryid 薪资帐套ID
	 * @return
	 */
	public ArrayList searchItemList(int salaryid)
	{
		  ArrayList list=new ArrayList();
		  StringBuffer buf=new StringBuffer();
		  buf.append("select itemid from salaryset where salaryid=?");
		  ArrayList paralist=new ArrayList();
		  paralist.add(new Integer(salaryid));
		  RowSet rset=null;
		  try
		  {
			  ContentDAO dao=new ContentDAO(this.conn);
			  rset=dao.search(buf.toString(),paralist);
			  while(rset.next())
			  {
				  list.add(rset.getString("itemid").toUpperCase());
			  }//
		  }
		  catch(Exception ex)
		  {
			  ex.printStackTrace();
		  }		  
		  finally
		  {
				PubFunc.closeDbObj(rset);
		  }			  
		  return list;
	}
	
	public String getGz_tablename() {
		return gz_tablename;
	}
	public void setGz_tablename(String gz_tablename) {
		this.gz_tablename = gz_tablename;
	}
 
	public String getManager() {
		return manager;
	}
	public void setManager(String manager) {
		this.manager = manager;
	}
	 
	public RecordVo getTemplatevo() {
		return templatevo;
	}
	public void setTemplatevo(RecordVo templatevo) {
		this.templatevo = templatevo;
	}
	 
	public SalaryCtrlParamBo getCtrlparam() {
		return ctrlparam;
	}
	public void setCtrlparam(SalaryCtrlParamBo ctrlparam) {
		this.ctrlparam = ctrlparam;
	}
}
