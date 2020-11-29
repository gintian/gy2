package com.hjsj.hrms.businessobject.gz.gz_budget.budget_allocation;

import com.hjsj.hrms.businessobject.gz.gz_budget.budget_rule.options.BudgetSysBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * 
 * <p>Title: BudgetAllocBo.java
 * <p>Description: 薪资预算分配业务类
 * <p>Company: HJSJ
 * <p>Create Time: 2012.10.18
 * @author genglz
 * @version 5.0
 *
 */
public class BudgetAllocBo {
	private Connection conn = null;
	private UserView userView=null;
	private BudgetSysBo bo=null;
	private HashMap sysOptionMap=new HashMap();  //系统项参数
	private String errorMessage=null;
	
	public BudgetAllocBo(Connection _con)
	{
		this.conn=_con;
	}
	
	public BudgetAllocBo(Connection _con,UserView _userView)
	{
		conn=_con;
		userView=_userView;
		init();
	}
	
	
	private void init()
	{
		// 获得系统项参数放入 Map 中 sysOptionMap=xxxxx
		bo=new BudgetSysBo(this.conn,this.userView);
		sysOptionMap=bo.getSysValueMap();
	}
	
	private RecordVo getRecordVo(String tabname,String primary_key,int key) 
	{
		if(key==0){
			return null;
		}
		RecordVo vo=new RecordVo(tabname); 
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			vo.setInt(primary_key, key);
			vo=dao.findByPrimaryKey(vo);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return vo;
	}
	
	private String getBudgetUnits()
	{
		String s="''";
		String units=(String)sysOptionMap.get("units");
		String[] temp=units.split(",");
		if(temp.length!=0){
			s="'";
			for(int i=0;i<temp.length;i++){
				if(i==temp.length-1)
					s +=temp[i]+"'";
				else
					s +=temp[i]+"','";
			}
			
		}
		
		return s;
	}
	
	private String getBudgetDate(Integer budgetid)
	{
		String result = null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select * from gz_budget_index where budget_id = "+budgetid);
			if(rowSet.next())
			{
				String MM = (rowSet.getInt("FirstMonth")<10?"0":"")+ rowSet.getString("FirstMonth");
				result =rowSet.getString("YearNum")+"-"+ MM +"-01";
			}
			
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 返回预算分配数据的sql(用于dataset标签)
	 * @param b0110
	 * @param budget_id
	 * @return
	 * @throws GeneralException
	 */
	public String getSql(String b0110,Integer budget_id,ArrayList fieldList)throws GeneralException
	{
		String sql="";
		String tableName = (String)sysOptionMap.get("ysze_set");
		String flds=getDisplayFlds(fieldList);
		boolean isRoot = b0110 == null || b0110.length()==0; 
		if(isRoot)  // 显示顶级单位
		{
			sql="select "+flds+" from "+tableName+
				" where B0110 in (select codeitemid from organization where parentid=codeitemid) and "+ 
				        sysOptionMap.get("ysze_idx_menu") + "="+budget_id+
				" order by B0110"; 
		}
		else if (b0110.equalsIgnoreCase(getTopBudgetUnit()))  // 选中集团单位，显示一级预算单位数据
		{
			sql="select "+flds+" from "+tableName+
			    " where B0110 in ("+getBudgetUnits()+") and "+ sysOptionMap.get("ysze_idx_menu") + "="+budget_id+
			    " order by B0110"; 
		}
		else  // 显示直属下级机构
		{
			sql="select "+flds+" from "+tableName+
			    " where B0110 in (select codeitemid from organization where parentid='"+b0110+"' or codeitemid = '"+b0110+"') and "+
			    sysOptionMap.get("ysze_idx_menu") + "="+budget_id+
			    " order by B0110";
		}
		
		return sql;
	}
	
	private String getDisplayFlds(ArrayList fieldList){
		String flds="";
		for(int i=0;i<fieldList.size();i++){
			Field item=(Field)fieldList.get(i);
			if(flds.length()>0)
				flds+=",";
			if(item.getName().equalsIgnoreCase((String)sysOptionMap.get("ysze_status_menu")))
			{
				// 已发布->已分发
				String s="case "+item.getName();
				ArrayList codes = AdminCode.getCodeItemList("23");
				for(int j=0;j<codes.size();j++){
					CodeItem c=(CodeItem)codes.get(j);
					s+=" when '"+c.getCodeitem()+"' then '";
					if("04".equals(c.getCodeitem()))
						s += ResourceFactory.getProperty("gz.budget.budget_allocation.status.distribute");
					else
						s += c.getCodename();
					s+="'";
				}
				s+=" else "+item.getName()+" end as " + item.getName();
				flds+=s;
			}
			else{
				flds+=item.getName();
			}
		}
		return flds;
	}
	
	/**
	 * 检查能否使用预算分配功能
	 * @param 
	 * @return
	 * @throws 
	 */
	public void checkCanEnter()
	{
		errorMessage = null;
		if(this.userView.getUserOrgId()==null|| "".equals(this.userView.getUserOrgId().trim())){
			errorMessage = ResourceFactory.getProperty("gz.budget.budgeting.selfuser");
			return;
		}

		if (!bo.isParamValid())
		{
			errorMessage = ResourceFactory.getProperty("gz.budget.budgeting.ysparam");
			return;
		}
	}
	/**
	 * 检查是否可以设置预算参数
	 */
	public boolean checkBudgetParams(String unitid, Integer budgetid)
	{
		errorMessage = null;             // unitid为空表示选中机构树'组织机构'节点
		boolean isTopUnit = unitid == null||unitid.length()==0||unitid.equals(getTopBudgetUnit());  // 集团单位分发
		if(unitid == null||unitid.length()==0)
			unitid = getTopBudgetUnit();
		String unitname = AdminCode.getCode("UN", unitid).getCodename();
		LazyDynaBean bean = getAppealStatus(unitid, budgetid.toString());
		if ("0".equals(bean.get("exists")))
		{                  
			errorMessage = formatStr(ResourceFactory.getProperty("gz.budget.budget_allocation.setBudgetParmas"), unitname);
			return false;
		}
		return true;
	}
	
	/**
	 * 检查预算是否可以分发
	 * @return
	 */
	public boolean checkCanDistribute(String unitid, Integer budgetid)
	{
		errorMessage = null;             // unitid为空表示选中机构树'组织机构'节点
		boolean isTopUnit = unitid == null||unitid.length()==0||unitid.equals(getTopBudgetUnit());  // 集团单位分发
		if(unitid == null||unitid.length()==0)
			unitid = getTopBudgetUnit();
		if(unitid==null||unitid.length()==0){
			errorMessage=ResourceFactory.getProperty("gz.budget.budget_allocation.distributeHint0");//'预算系统项-参与预算单位'设置有误，请检查！
			return false;
		}
		String unitname = AdminCode.getCode("UN", unitid).getCodename();
		LazyDynaBean bean = getAppealStatus(unitid, budgetid.toString());
		if ("0".equals(bean.get("exists")))
		{                  
			errorMessage = formatStr(ResourceFactory.getProperty("gz.budget.budget_allocation.distributeHint1"), unitname);
			return false;
		}
		if(isTopUnit)
		{
			// 集团单位需要为起草状态
			if(!"01".equals(bean.get("status")))
			{
				errorMessage = formatStr(ResourceFactory.getProperty("gz.budget.budget_allocation.distributeHint2"), unitname);
				return false;
			}
		}
		else
		{
			// 非集团单位需要为已发布(04)状态,才能继续向下级单位分发
			if(!"04".equals(bean.get("status")))
			{
				errorMessage = formatStr(ResourceFactory.getProperty("gz.budget.budget_allocation.distributeHint3"), unitname);
				return false;
			}
			if(!hasSubBudgetUnit(unitid,budgetid))
			{
				errorMessage = formatStr(ResourceFactory.getProperty("gz.budget.budget_allocation.distributeHint4"), unitname);
				return false;
			}
			if(!isSubBudgetUnitDraft(unitid,budgetid))
			{
				errorMessage = formatStr(ResourceFactory.getProperty("gz.budget.budget_allocation.distributeHint5"), unitname);
				return false;
			}
		}
		// 检查所选单位预算总额是否等于直属单位预算总额之和
		if (calcSubUnitBudgetAmount(unitid, budgetid)!= ((Double)bean.get("amount")).doubleValue())
		{
			errorMessage = formatStr(ResourceFactory.getProperty("gz.budget.budget_allocation.distributeHint6"), unitname);
			return false;
		}
		return true;
	}

	/**
	 * 检查预算是否可以退回
	 * @return
	 */
	public boolean checkCanReject(String unitid, Integer budgetid)
	{
		errorMessage = null;             // unitid为空表示选中机构树'组织机构'节点
		boolean isTopUnit = unitid == null||unitid.length()==0||unitid.equals(getTopBudgetUnit());  // 集团单位分发
		if(unitid == null||unitid.length()==0)
			unitid = getTopBudgetUnit();
		if(unitid==null||unitid.length()==0){
			errorMessage=ResourceFactory.getProperty("gz.budget.budget_allocation.distributeHint0");//'预算系统项-参与预算单位'设置有误，请检查！
			return false;
		}
		String unitname = AdminCode.getCode("UN", unitid).getCodename();
		LazyDynaBean bean = getAppealStatus(unitid, budgetid.toString());
		if ("0".equals(bean.get("exists")))  // 非预算单位
		{                  
			errorMessage = formatStr(ResourceFactory.getProperty("gz.budget.budget_allocation.rejectHint1"), unitname);
			return false;
		}
		if(isTopUnit)  // 集团单位
		{
			// 集团单位不能为起草状态
			if("01".equals(bean.get("status")))
			{
				errorMessage = formatStr(ResourceFactory.getProperty("gz.budget.budget_allocation.rejectHint2"), unitname);
				return false;
			}
		}
		else  // 非集团单位
		{
			// 非集团单位不能为起草状态
			if("01".equals(bean.get("status")))
			{
				errorMessage = formatStr(ResourceFactory.getProperty("gz.budget.budget_allocation.rejectHint2"), unitname);
				return false;
			}
			if(!hasSubBudgetUnit(unitid,budgetid))  // 无下级预算单位
			{
				errorMessage = formatStr(ResourceFactory.getProperty("gz.budget.budget_allocation.rejectHint3"), unitname);
				return false;
			}
			if(isSubBudgetUnitDraft(unitid,budgetid))  // 下级单位为起草状态,不必退回
			{
				errorMessage = formatStr(ResourceFactory.getProperty("gz.budget.budget_allocation.rejectHint4"), unitname);
				return false;
			}
		}
		// 所有下级为起草(01)或已发布(04)或驳回(07)状态才能退回
		if(!isSubBudgetUnitStatus(unitid,budgetid, new String[]{"01","04","07"}))
		{
			errorMessage = formatStr(ResourceFactory.getProperty("gz.budget.budget_allocation.rejectHint5"), unitname);
			return false;
		}
		return true;
	}
	
	/**
	 * 检查是否可以新增预算单位
	 * @return
	 */
	public boolean checkCanAddBudgetUnit(String unitid, Integer budgetid)
	{
		errorMessage = null;             
		boolean isTopUnit = unitid == null||unitid.length()==0||unitid.equals(getTopBudgetUnit());  // 集团单位分发
		if(unitid == null||unitid.length()==0)  // 选中机构树'组织机构'节点
		{
			errorMessage = ResourceFactory.getProperty("gz.budget.budget_allocation.addUnitHint1");
			return false;
		}
		// 只能新增一级预算单位的下级单位
		if(!isSubBudgetUnit(unitid, budgetid))
		{
			errorMessage = ResourceFactory.getProperty("gz.budget.budget_allocation.addUnitHint1");
			return false;
		}
		String unitname = AdminCode.getCode("UN", unitid).getCodename();
		LazyDynaBean bean = getAppealStatus(unitid, budgetid.toString());
		if ("1".equals(bean.get("exists")))  // 已经是预算单位
		{                  
			errorMessage = formatStr(ResourceFactory.getProperty("gz.budget.budget_allocation.addUnitHint2"), unitname);
			return false;
		}		
		String parentid = AdminCode.getCode("UN", unitid).getPcodeitem();  // 上级单位
		String parentname = AdminCode.getCode("UN", parentid).getCodename();
		
		bean = getAppealStatus(parentid, budgetid.toString());
		if ("0".equals(bean.get("exists"))) // 上级单位不是预算单位
		{                  
			errorMessage = formatStr(ResourceFactory.getProperty("gz.budget.budget_allocation.addUnitHint4"), unitname);
			return false;
		}		
		
		// 兄弟单位无预算单位,或兄弟单位及其下级为起草或驳回状态,才能新增(即已发布或已报批或已批状态不能新增)
		if(!isSubBudgetUnitStatus(parentid,budgetid,new String[]{"01","07"})) // 起草/驳回
		{
			errorMessage = formatStr(ResourceFactory.getProperty("gz.budget.budget_allocation.addUnitHint3"), parentname);
			return false;
		}
		return true;
	}
	
	/**
	 * 检查是否可以删除预算单位
	 * @param units 逗号(,)分隔
	 * @return
	 */
	public boolean checkCanDeleteBudgetUnit(String units, Integer budgetid)
	{
		errorMessage = null;             
		String unitid = null;
		String[] unitlist = units.split(",");
		for (int i=0;i<unitlist.length;i++)
		{
			unitid = unitlist[i];
			if(unitid==null||unitid.length()==0)
				continue;
			LazyDynaBean bean = getAppealStatus(unitid, budgetid.toString());
			if ("0".equals(bean.get("exists")))  // 非预算单位
			{                  
				errorMessage = ResourceFactory.getProperty("gz.budget.budget_allocation.delUnitHint1");
				return false;
			}
			// 只能删除一级预算单位的下级单位
			if(!isSubBudgetUnit(unitid, budgetid))
			{
				errorMessage = ResourceFactory.getProperty("gz.budget.budget_allocation.delUnitHint1");
				return false;
			}
	
			// 只能删除起草或驳回状态, 不考虑下级单位状态
			if(!"01".equals(bean.get("status"))&&!"07".equals(bean.get("status")))
			{
				errorMessage = ResourceFactory.getProperty("gz.budget.budget_allocation.delUnitHint2");
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 预算分发
	 */
	public void distributeBudget(String unitid, Integer budgetid) throws GeneralException
	{
		boolean isTopUnit = unitid == null||unitid.length()==0||unitid.equals(getTopBudgetUnit());
		if(unitid == null||unitid.length()==0) 
			unitid=getTopBudgetUnit();
		String sql=null;
		String zeset = (String)sysOptionMap.get("ysze_set");
		String paramset = (String)sysOptionMap.get("ysparam_set");
	    String paramIndexMenu =(String)sysOptionMap.get("ysparam_idx_menu");
		String paramz0 = paramset + "Z0";
		String paramz1 = paramset + "Z1";
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			// 0. 必须先建预算参数表  WJH 2013-10-28
			if(!"".equals(getBudgetParamFlds()) && !hasBudgetParam(unitid, budgetid)){
				throw GeneralExceptionHandler.Handle(
					new GeneralException(
						ResourceFactory.getProperty("gz.budget.budget_allocation.musthavebudgetparam")));
				
			}			
			// 1.更新状态
			String setflds=sysOptionMap.get("ysze_status_menu") +"='04',"+
							"ModUserName='"+userView.getUserName()+"',ModTime="+Sql_switcher.sqlNow();
			if(isTopUnit) // 集团单位分发
			{
				// 预算状态置为执行中（分发）05
				sql = "update gz_budget_index set SPFlag='05' where budget_id = " + budgetid;
				dao.update(sql);
				
				// 一级预算单位和集团单位总额状态置为已发布04
				sql="update "+zeset+ " set " + setflds+
				    " where (B0110 in ("+getBudgetUnits()+") or B0110 = '"+unitid+"') and "+ 
			        	sysOptionMap.get("ysze_idx_menu") + "="+budgetid; 				
				dao.update(sql);
			}
			else
			{
				// 直属单位总额状态置为已发布04
				sql="update "+zeset+ " set " + setflds+
				    " where B0110 in (select codeitemid from organization where parentid='"+unitid+"' and parentid<>codeitemid) and "+ 
			        	sysOptionMap.get("ysze_idx_menu") + "="+budgetid; 				
				dao.update(sql);
			}
		   sql = "select * from gz_budget_index where budget_id = " + budgetid;
	       RowSet rowSet = dao.search(sql);
	       String yearnum ="0";
	       String firstmonth ="0";
	       if (rowSet.next())
           {
	           yearnum = rowSet.getString("yearnum");
	           firstmonth = rowSet.getString("firstmonth");
	            
           }
			// 2.同步预算执行表(多删少增)
			String tabid = null;
			String seq = null;
			String unitCond = null;
			if(isTopUnit)  // 包括集团单位和一级预算单位
			{
				unitCond="(B0110 in ("+getBudgetUnits()+") or B0110 = '"+unitid+"')"; 				
			}
			else  // 包括直属单位
			{
				unitCond="B0110 in (select codeitemid from organization where parentid='"+unitid+"' and parentid<>codeitemid)"; 				
			}

			sql = "select tab_id,seq from gz_budget_tab where validFlag=1";
			rowSet = dao.search(sql);
			while (rowSet.next())
			{
				tabid = rowSet.getString("tab_id");
				seq = rowSet.getString("seq");
				sql="insert into gz_budget_exec(budget_id,tab_id,B0110,status,seq,LastUser,LastTime) " +
				    " select "+budgetid+","+tabid+",B0110,0,"+seq+",'"+userView.getUserName()+"',"+Sql_switcher.today()+
				    " from "+zeset+ " ze " +
					" where "+sysOptionMap.get("ysze_idx_menu") + "="+budgetid+" and " + unitCond +" and " +
					    " not exists(select 1 from gz_budget_exec zx "+
					                " where zx.budget_id="+budgetid+" and zx.tab_id="+tabid+" and zx.B0110=ze.B0110)"; 				
				dao.update(sql);
				
				// 不需要同步删除
			}
			if(rowSet!=null)
				rowSet.close();
			
			// 3.同步预算参数, 把参数复制到直属单位
			if(hasBudgetParam(unitid, budgetid))
			{
				if(isTopUnit) 
					unitCond="B0110 in ("+getBudgetUnits()+")"; 				
				else
					unitCond="B0110 in (select codeitemid from organization "+
					                 " where parentid='"+unitid+"' and parentid<>codeitemid)"; 				

				sql = "select * from "+zeset+" where "+sysOptionMap.get("ysze_idx_menu") + "="+budgetid+" and " + unitCond;
				rowSet = dao.search(sql);
				String subunitid = null;
				String extflds = getBudgetParamFlds();  // 含Z0,Z1
				int I9999 = 1;
				//String z0val = getBudgetDate(budgetid);
				//int z1val = 1;
				while (rowSet.next())
				{
					subunitid = rowSet.getString("B0110");
					I9999 = getBudgetParamNewI9999(subunitid);
					//z1val = getNewBudgetZ1(paramset, subunitid, z0val);
					sql="insert into "+paramset+"(B0110,I9999,"+/*paramz0+","+paramz1+","+*/"CreateUserName,CreateTime,"+extflds+") " +
					    " select '"+subunitid+"',"+I9999+","+/*Sql_switcher.dateValue(z0val)+","+z1val+","+*/
					              "'"+userView.getUserName()+"',"+Sql_switcher.sqlNow()+","+extflds+
					    " from "+paramset+ 
						" where "+sysOptionMap.get("ysparam_idx_menu") + "="+budgetid+" and B0110 = '" + unitid +"' and " +
						    " not exists(select 1 from "+paramset+" p "+
						                " where p.B0110='"+subunitid+"' and p."+sysOptionMap.get("ysparam_idx_menu")+"="+budgetid+")"; 				
					dao.update(sql);			
					
					//继承上一次预算参数 ，只更新空值或0
					String oldLatelyId="0";//上次预算id
					sql="select budget_id from gz_budget_index where budgetType in (1,2,3) " 
					    + " and budget_id <>" +String.valueOf(budgetid)
					    +" and ((yearnum <"+yearnum+") or (yearnum = "+yearnum+" and firstmonth<="+firstmonth+" ) )"
					    +" order by yearNum desc,I9999 desc";
					RowSet rset=dao.search(sql);
					if (rset.next()){
					    oldLatelyId=String.valueOf(rset.getInt(1));
					}
					sql ="select * from "+paramset+" where "+paramIndexMenu + "="+oldLatelyId
					+" and B0110 = '" + subunitid +"'";
					rset =dao.search(sql);
					if (rset.next()){                 
					    sql ="select * from "+paramset+" where "+paramIndexMenu + "="+budgetid
					    +" and B0110 = '" + subunitid +"'";
					    rset =dao.search(sql);
					    if (rset.next()){    
					        String tmptable="t#_"+this.userView.getUserName()+"_1";
					        DbWizard dbw =new DbWizard(this.conn);
					        dbw.dropTable(tmptable);
					        dbw.createTempTable(paramset, tmptable,
					                "*", paramIndexMenu + "="+oldLatelyId
					                +" and B0110 = '" + subunitid +"'", "");
					        ArrayList list=DataDictionary.getFieldList(paramset,Constant.USED_FIELD_SET);
					        FieldItem f=null;
					        String updFlds="";
					        String srcFlds="";
					        String destFlds="";
					        for(int i=0;i<list.size();i++) {
					            f=(FieldItem)list.get(i);
					            if (paramz0.equalsIgnoreCase(f.getItemid())) continue;
					            if (paramz1.equalsIgnoreCase(f.getItemid())) continue;
					            if (paramIndexMenu.equalsIgnoreCase(f.getItemid())) continue;   
					            if ((",B0110,I9999,CreateUserName,CreateTime,").toUpperCase()
					                    .indexOf(","+f.getItemid().toUpperCase()+",")>-1) continue;
					            String value =(String)rset.getString(f.getItemid().toLowerCase());
					            if ((value==null)|| (value=="") || ("0".equals(value))){
					                if (updFlds.length()>0) updFlds+=",";
					                updFlds= updFlds+ f.getItemid()+"= A."+f.getItemid();   
					                //oracle 
					                if (srcFlds.length()>0) srcFlds+=",";
					                	srcFlds=srcFlds+f.getItemid();
			                	    if (destFlds.length()>0) destFlds+=",";
			                	    	destFlds=destFlds+f.getItemid(); 	
					            }
					        }  
					        if (updFlds.length()>0){
					        	if (Sql_switcher.searchDbServer() == 1) {
					        		sql="update "+paramset+" set "+updFlds+ " from "+tmptable+" A where "
					        		+paramset+"."+paramIndexMenu + "="+budgetid +" and "
					        		+paramset+"."+"B0110 = '" + subunitid +"'"
					        		+" and "+paramset+"."+"B0110= A.B0110 ";
					        		dao.update(sql);
					        		
					        	}
					        	else {
					        		sql="update "+paramset+" set ("+destFlds+") ="
					        		+"(select "+srcFlds +" from "+tmptable+" where "
					        		+ paramset+"."+"B0110= "+tmptable+".B0110"
					        		+" and " +paramset+".I9999"+"= "+tmptable+".i9999"
					        		+")" +
					        		" where "
					        		+paramIndexMenu + "="+budgetid +" and "
					        		+"B0110 = '" + subunitid +"'";
					        		dao.update(sql);
					        		
					        	}
					        }
					        dbw.dropTable(tmptable);
					        if(rset!=null)
					            rset.close();
					    }
					}
					
				}
				if(rowSet!=null)
					rowSet.close();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	/**
	 * 预算退回
	 */
	public void rejectBudget(String unitid, Integer budgetid)
	{
		boolean isTopUnit = unitid == null||unitid.length()==0||unitid.equals(getTopBudgetUnit());
		if(unitid == null||unitid.length()==0) 
			unitid=getTopBudgetUnit();
		String sql=null;
		String zeset = (String)sysOptionMap.get("ysze_set");
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			if(isTopUnit) // 集团单位
			{
				// 预算状态置为起草01
				sql = "update gz_budget_index set SPFlag='01' where budget_id = " + budgetid;
				dao.update(sql);
				
				// 所有预算单位总额状态置为起草
				sql="update "+zeset+ " set " +sysOptionMap.get("ysze_status_menu") +"='01'"+
				    " where B0110 like '"+unitid+"%' and "+ 
			        	sysOptionMap.get("ysze_idx_menu") + "="+budgetid; 				
				dao.update(sql);
			}
			else
			{
				// 所有下级单位(不包括该单位)，总额状态置为起草
				sql="update "+zeset+ " set " +sysOptionMap.get("ysze_status_menu") +"='01'"+
				    " where B0110 in (select codeitemid from organization "+
				                     " where parentid like '"+unitid+"%' and codeitemid<> '"+unitid+"') and "+ 
			        	sysOptionMap.get("ysze_idx_menu") + "="+budgetid; 				
				dao.update(sql);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}	
	
	/**
	 * 新增预算单位
	 */
	public void addBudgetUnit(String unitid, Integer budgetid)
	{
		String sql=null;
		String zeset = (String)sysOptionMap.get("ysze_set");
		String zeidx = (String)sysOptionMap.get("ysze_idx_menu");
		String zestatus = (String)sysOptionMap.get("ysze_status_menu");
		String zez0 = zeset + "Z0";
		String zez1 = zeset + "Z1";
		String paramset = (String)sysOptionMap.get("ysparam_set");
		String paramz0 = paramset+"Z0";
		String paramz1 = paramset+"Z1";
		String parentid=AdminCode.getCode("UN", unitid).getPcodeitem();
		LazyDynaBean parentbean = getAppealStatus(parentid, budgetid.toString());
		String z0val = getBudgetDate(budgetid);
		int z1val = 1;
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rowSet=null;
		try
		{
			// 1.新增预算总额记录
			int I9999 = getNewI9999(zeset, unitid);
			z1val = getNewBudgetZ1(zeset, unitid, z0val);
			sql="insert into "+zeset+ "(B0110,I9999,"+zez0+","+zez1+","+zeidx+","+zestatus+",CreateUserName,CreateTime)"+
			    " values('"+unitid+"',"+I9999+","+Sql_switcher.dateValue(z0val)+","+z1val+","+
			                budgetid + ",'01','"+userView.getUserName()+"',"+Sql_switcher.sqlNow()+")"; 				
			dao.update(sql);
			
			// 2.新增预算参数记录(从上级单位复制)
			String extflds = getBudgetParamFlds();  // 含Z0,Z1
			I9999 = getBudgetParamNewI9999(unitid);
			//z1val = getNewBudgetZ1(paramset, unitid, z0val);
			sql="insert into "+paramset+"(B0110,I9999,"+/*paramz0+","+paramz1+","+*/"CreateUserName,CreateTime,"+extflds+") " +
			    " select '"+unitid+"',"+I9999+","+/*Sql_switcher.dateValue(z0val)+","+z1val+","+*/
			              "'"+userView.getUserName()+"',"+Sql_switcher.sqlNow()+","+extflds+
			    " from "+paramset+ 
				" where "+sysOptionMap.get("ysparam_idx_menu") + "="+budgetid+" and B0110 = '" + parentid +"'"; 				
			dao.update(sql);
			
			// 3.新增预算执行表记录
			String tabid = null;
			String seq = null;
			sql = "select tab_id,seq from gz_budget_tab where validFlag=1";
			rowSet = dao.search(sql);
			while (rowSet.next())
			{
				tabid = rowSet.getString("tab_id");
				seq = rowSet.getString("seq");
				sql="insert into gz_budget_exec(budget_id,tab_id,B0110,status,seq,LastUser,LastTime) " +
				    " values("+budgetid+","+tabid+",'"+unitid+"',0,"+seq+",'"+userView.getUserName()+"',"+Sql_switcher.sqlNow()+")";
				dao.update(sql);
			}
			if(rowSet!=null)
				rowSet.close();			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}	
	
	/**
	 * 删除预算单位
	 * @param units 逗号分隔
	 */
	public void deleteBudgetUnit(String units, Integer budgetid)
	{
		String sql=null;
		String zeset = (String)sysOptionMap.get("ysze_set");
		String zeidx = (String)sysOptionMap.get("ysze_idx_menu");
		String paramset = (String)sysOptionMap.get("ysparam_set");
		String paramidx = (String)sysOptionMap.get("ysparam_idx_menu");
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			String cond = "";
			String unitid = "";
			String[] unitlist = units.split(",");
			for (int i=0;i<unitlist.length;i++)
			{
				unitid = unitlist[i];
				if(unitid==null||unitid.length()==0)
					continue;
				if (cond.length()!=0)
					cond+=" or ";
				cond += "B0110 like '"+unitid+"%'"; // 包括所有下级
			}
			cond = "("+cond+")";

			// 1.删除预算总额记录(包括所有下级)
			sql="delete from "+zeset+" where "+zeidx+" = "+budgetid+" and "+cond;
			dao.update(sql);
			
			// 2.删除预算参数
			sql="delete from "+paramset+" where "+paramidx+" = "+budgetid+" and "+cond;
			dao.update(sql);
			
			// 3.删除执行表
			sql="delete from gz_budget_exec where budget_id = "+budgetid+" and "+cond;
			dao.update(sql);
			
			// 4.删除名册
			sql="delete from SC01 where budget_id = "+budgetid+" and "+cond;
			dao.update(sql);
			
			// 5.删除用工计划
			sql="delete from SC02 where budget_id = "+budgetid+" and "+cond;
			dao.update(sql);
			
			// 6.删除月度明细
			sql="delete from SC03 where budget_id = "+budgetid+" and "+cond;
			dao.update(sql);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}	
	
	/**
	 * 把字符串中的%s全部替换掉
	 * @param format
	 * @param param
	 * @return
	 */
	private String formatStr(String format, String param)
	{
		// format JKD1.5才支持
//		String.format(format, new String[]{param});
		return PubFunc.Replace(format, "%s", param);
	}
	
	private String getBudgetParamFlds()
	{
		String result = "";
		String paramset = (String)sysOptionMap.get("ysparam_set");
		ArrayList list=DataDictionary.getFieldList(paramset,Constant.USED_FIELD_SET);
		FieldItem f=null;
		for(int i=0;i<list.size();i++)
		{
			f=(FieldItem)list.get(i);
			if (result.length()!=0)
				result+=",";
			result += f.getItemid();
		}		
		return result;
	}
	
	private int getBudgetParamNewI9999(String unitid)
	{
		String paramset = (String)sysOptionMap.get("ysparam_set");
		return getNewI9999(paramset, unitid);
	}
	
	private int getNewI9999(String unitset, String unitid)
	{
		int result = 1;
		String sql="select Max(I9999) as I9999 from "+unitset+
					" where B0110 = '"+unitid+"'"; 
		RowSet rowSet=null;
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			rowSet=dao.search(sql);
			if (rowSet.next())
			{
				result = rowSet.getInt("I9999")+1;
			}
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
		return result;
	}
	
	private int getNewBudgetZ1(String unitset, String unitid, String z0val)
	{
		int result=1;
		String z0 = unitset+"Z0";
		String z1 = unitset+"Z1";

		String sql="select max("+z1+") z1 from "+unitset+
					" where B0110 = '"+unitid+"' and "+Sql_switcher.year(z0) + "="+z0val.substring(0, 4); 
		RowSet rowSet=null;
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			rowSet=dao.search(sql);
			if (rowSet.next())
			{
				result = rowSet.getInt("z1")+1;
			}
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 是否有预算参数
	 * @param unitid
	 * @param budgetid
	 * @return
	 */
	public boolean hasBudgetParam(String unitid, Integer budgetid)
	{
		boolean result = false;
		if (unitid==null||unitid.length()==0) 
			unitid = getTopBudgetUnit();
		String set = (String)sysOptionMap.get("ysparam_set");
		String sql="select * from "+set+
			" where B0110 = '"+unitid+"' and "+ 
	        	sysOptionMap.get("ysparam_idx_menu") + "="+budgetid; 
		RowSet rowSet=null;
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			rowSet=dao.search(sql);
			if (rowSet.next())
			{
				result = true;
			}
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 是否有下级预算单位
	 */
	private boolean hasSubBudgetUnit(String unitid, Integer budgetid)
	{
		boolean result = false;
		if(unitid==null||unitid.length()==0)// 集团单位
		{
			result = true;
		}
		else
		{
			String set = (String)sysOptionMap.get("ysze_set");
			String sql="select * from "+set+
				" where B0110 like '"+unitid+"%' and B0110 <> '"+unitid+"' and "+ 
		        	sysOptionMap.get("ysze_idx_menu") + "="+budgetid; 
			RowSet rowSet=null;
			ContentDAO dao=new ContentDAO(this.conn);
			try
			{
				rowSet=dao.search(sql);
				if (rowSet.next())
				{
					result = true;
				}
				if(rowSet!=null)
					rowSet.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return result;
	}
	
	/**
	 * 计算直属下级单位预算总额之和
	 * @param unitid
	 * @param budgetid
	 * @return
	 */
	private double calcSubUnitBudgetAmount(String unitid, Integer budgetid)
	{
		double result = 0;
		String set = (String)sysOptionMap.get("ysze_set");
		String ze=(String)sysOptionMap.get("ysze_ze_menu");
		String sql = null;
		if(unitid==null||unitid.length()==0||unitid.equals(getTopBudgetUnit()))// 集团单位
		{
			sql="select sum("+ze+") as ze from "+set+
			    " where B0110 in ("+getBudgetUnits()+") and "+ 
		        	sysOptionMap.get("ysze_idx_menu") + "="+budgetid; 
		}
		else
		{
			sql="select sum("+ze+") as ze from "+set+
				" where B0110 in (select codeitemid from organization where parentid='"+unitid+"' and parentid<>codeitemid) and "+ 
				    sysOptionMap.get("ysze_idx_menu") + "="+budgetid; 
		}
		RowSet rowSet=null;
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			rowSet=dao.search(sql);
			if (rowSet.next())
			{
				result = rowSet.getDouble("ze");
			}
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
		return result;
	}
	
	/**
	 * 下级预算单位是否全部为起草状态
	 * @param unitid
	 * @param budgetid
	 * @return
	 */
	private boolean isSubBudgetUnitDraft(String unitid, Integer budgetid)
	{
		boolean result = true;
		String set = (String)sysOptionMap.get("ysze_set");
		String sql = null;
		if(unitid==null||unitid.length()==0)// 集团单位
		{
			sql="select * from "+set+
			    " where B0110 in ("+getBudgetUnits()+") and "+sysOptionMap.get("ysze_status_menu")+" <> '01' and "+ 
		        	sysOptionMap.get("ysze_idx_menu") + "="+budgetid; 
		}
		else
		{
			sql="select * from "+set+
				" where B0110 like '"+unitid+"%' and B0110 <> '"+unitid+"' and "+ sysOptionMap.get("ysze_status_menu")+" <> '01' and "+
				    sysOptionMap.get("ysze_idx_menu") + "="+budgetid; 
		}
		RowSet rowSet=null;
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			rowSet=dao.search(sql);
			if (rowSet.next())
			{
				result = false;
			}
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return result;
	}
	
	private String getStatusCond(String fld, String[] status)
	{
		String result="";
		for (int i=0;i<status.length;i++)
		{
			if(result.length()!=0)
				result+=" and ";
			result+= fld + "<>'"+status[i]+"'";
		}
		return result;
	}
	
	/**
	 * 下级预算单位是否指定状态
	 * @param unitid
	 * @param budgetid
	 * @return
	 */
	private boolean isSubBudgetUnitStatus(String unitid, Integer budgetid, String[] status)
	{
		boolean result = true;
		String set = (String)sysOptionMap.get("ysze_set");
		String sql = null;
		if(unitid==null||unitid.length()==0)// 集团单位
			unitid = getTopBudgetUnit();
		sql="select * from "+set+
			" where B0110 like '"+unitid+"%' and B0110 <> '"+unitid+"' and "+ 
			        getStatusCond((String)sysOptionMap.get("ysze_status_menu"),status)+" and "+
			    sysOptionMap.get("ysze_idx_menu") + "="+budgetid; 
		RowSet rowSet=null;
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			rowSet=dao.search(sql);
			if (rowSet.next())
			{
				result = false;
			}
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return result;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * 判断单位是否是顶层节点
	 * @param b0110
	 * @return
	 * @throws GeneralException
	 */
	public boolean isTopUn(String b0110)throws GeneralException
	{
		boolean isTopUn=false;
		try
		{
			RowSet rowSet=null;
			ContentDAO dao=new ContentDAO(this.conn);
			rowSet=dao.search("select parentid from organization where codeitemid='"+b0110+"' ");
			if(rowSet.next())
			{
				String parentid=rowSet.getString("parentid");
				if(parentid!=null&&parentid.equals(b0110))
					isTopUn=true; 
			}
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);	
		}
		return isTopUn;
	}
	
	// 是否是一级预算单位的下级单位
	private boolean isSubBudgetUnit(String unitid, Integer budgetid)
	{
		boolean result = false;

		String sql = "select codeitemid from organization suborg "+ 
			         " where codeitemid = '"+unitid+"' and "+
			           " exists(select 1 from organization parent "+
			                   " where parent.codeitemid in ("+getBudgetUnits()+") and parent.codeitemid <> suborg.codeitemid and "+
			          Sql_switcher.substr("suborg.codeitemid", "1", Sql_switcher.length("parent.codeitemid"))+ "=parent.codeitemid) ";
		try
		{
			RowSet rowSet=null;
			ContentDAO dao=new ContentDAO(this.conn);
			rowSet=dao.search(sql);
			if(rowSet.next())
			{
				result=true;
			}
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}			
		return result;
	}
	
	/**
	 * 根据当前用户权限，返回机构树顶级节点，空表示全权
	 * @return
	 */
	public String getTopUn()
	{
		String result = null;
        if (!userView.isSuper_admin()) // 按管理范围控制
        {
    		String codevalue=userView.getManagePrivCodeValue();
    		if(codevalue.length()>0)
    			result = codevalue;
        }
        return result;
	}
	
	/**
	 * 根据一级预算单位，取顶级(集团)预算单位
	 */
	public String getTopBudgetUnit()
	{
		String unitid = null;
		String sql = "select codeitemid from organization "+ 
                     " where exists(select 1 from organization o "+
                                   " where o.codeitemid in ("+getBudgetUnits()+") and o.codeitemid <> organization.codeitemid and "+
                       Sql_switcher.substr("o.codeitemid", "1", Sql_switcher.length("organization.codeitemid"))+ "=organization.codeitemid) "+
                     " order by codeitemid,A0000";
		try
		{
			RowSet rowSet=null;
			ContentDAO dao=new ContentDAO(this.conn);
			rowSet=dao.search(sql);
			if(rowSet.next())
			{
				unitid=rowSet.getString("codeitemid");
			}
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return unitid;
	}

	/**
	 * 生成机构树xtree.js XML数据，可以显示单位状态。
	 * @param params
	 * @param actionName...
	 */
	public String genOrgXml(String topUnitCode, String actionName, String target, String flag) 
	{
		//生成的XML文件
		StringBuffer xmls = new StringBuffer();

		//DB相关
		ResultSet rs = null;		
		//创建xml文件的根元素
		Element root = new Element("TreeNode");
		
		//设置根元素属性
		root.setAttribute("id", "");
		root.setAttribute("text", "root");
		root.setAttribute("title", "root");

		//创建xml文档自身
		Document myDocument = new Document(root);
		try {
			// 生成SQL语句
			StringBuffer strsql = new StringBuffer();
			strsql.append("select * from organization where codesetid = 'UN'");  // 只加载单位
            String bosdate=DateStyle.dateformat(new Date(),"yyyy-MM-dd");
	        strsql.append(" and " +Sql_switcher.dateValue(bosdate)+" between start_date and end_date ");
	        // 只显示一级预算单位及其上级单位和下级单位
			if (topUnitCode!=null&&topUnitCode.length()>0)  // 加载下级单位
			{
				strsql.append(" and parentid='"+topUnitCode+"' and codeitemid <> parentid and (" +
						"exists(select 1 from organization o "+        // 预算单位及上级
						" where o.codeitemid in ("+getBudgetUnits()+") and "+
				Sql_switcher.substr("o.codeitemid", "1", Sql_switcher.length("organization.codeitemid"))+ "=organization.codeitemid) or " +
						"exists(select 1 from organization o "+		   // 预算单位下级
						" where o.codeitemid in ("+getBudgetUnits()+") and o.codeitemid <> organization.codeitemid and "+
				Sql_switcher.substr("organization.codeitemid", "1", Sql_switcher.length("o.codeitemid"))+ "=o.codeitemid)"+
					")");
			}
	    	else
	    	{   // 加载顶级单位
	    		strsql.append(" and parentid = codeitemid and " +
	    					"exists(select 1 from organization o "+    // 预算单位上级
	    							" where o.codeitemid in ("+getBudgetUnits()+") and "+
	    			Sql_switcher.substr("o.codeitemid", "1", Sql_switcher.length("organization.codeitemid"))+ "=organization.codeitemid)"); 
	    	}
			strsql.append(" order by A0000");				
			
			// 执行 SQL
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(strsql.toString());
			
			String status = "";
			String budgetid = getCurrentBudgetId().toString();  // 当前预算
			//设置跳转字符串
			String theaction = null;
			while (rs.next()) {
				//创建子元素
				Element child = new Element("TreeNode");
				//设置子元素属性
				String unitcode =  rs.getString("codeitemid");
				String unitname =  rs.getString("codeitemdesc");
				
				child.setAttribute("id", unitcode);

				LazyDynaBean bean = getAppealStatus(unitcode, budgetid);
				status = "";
				if(bean.get("exists") == "1"){
					if("04".equals(bean.get("status")))
						status = "("+ResourceFactory.getProperty("gz.budget.budget_allocation.status.distribute")+")";
					else
						status = "("+bean.get("statusDesc").toString()+")";
				}
				child.setAttribute("text", unitname + status);
				child.setAttribute("title", unitname);

				theaction = actionName + "&a_code="+ rs.getString("codesetid")+ unitcode;								
				child.setAttribute("href", theaction);
				child.setAttribute("target", target);
				child.setAttribute("icon", "/images/unit.gif");
				child.setAttribute("xml" ,"budget_org_tree_xml.jsp?topunit=" + unitcode);
				
				// 将子元素作为内容添加到根元素
				root.addContent(child);
			}

			XMLOutputter outputter = new XMLOutputter();

			//格式化输出类
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);

			//将生成的XML文件作为字符串形式
			xmls.append(outputter.outputString(myDocument));
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return xmls.toString();		
	}
	
	/**
	 * 获得预算总额表的fieldList
	 * @param unitid 机构树所选单位
	 */
	public ArrayList getFieldList(String unitid, Integer budgetid)throws GeneralException
	{
		ArrayList fieldList=new ArrayList();
		try
		{
			String set = (String)sysOptionMap.get("ysze_set");
			ArrayList list=DataDictionary.getFieldList(set,Constant.USED_FIELD_SET);
			list.add(0, DataDictionary.getFieldItem("B0110").cloneItem());  // 单位名称

			FieldItem I9999=new FieldItem(set, "I9999");
			I9999.setItemdesc(ResourceFactory.getProperty("label.serialnumber"));
			I9999.setItemlength(8);
			I9999.setDecimalwidth(0);
			I9999.setItemtype("N");
			list.add(1, I9999);
			
			if (unitid==null||unitid.length()==0)
				unitid=getTopBudgetUnit();
			// 同级各单位状态应该是一样的, 可以统一控制. 只有起草状态才能编辑.
			boolean isDraft = isSubBudgetUnitDraft(unitid, budgetid);
			Field item=null;
			boolean readonly;
			for(int i=0;i<list.size();i++)
			{
				item=(Field)((FieldItem)list.get(i)).cloneField();
				if("I9999".equalsIgnoreCase(item.getName()) ||
				   item.getName().equalsIgnoreCase((String)sysOptionMap.get("ysze_idx_menu")) ||
				   item.getName().equalsIgnoreCase(set+"Z0") ||item.getName().equalsIgnoreCase(set+"Z1"))
				{
					item.setVisible(false);
				}
				item.setLabel("&nbsp;&nbsp;&nbsp;"+item.getLabel()+"&nbsp;&nbsp;&nbsp;");
				if(isDraft)
					readonly = "B0110".equalsIgnoreCase(item.getName()) || "I9999".equalsIgnoreCase(item.getName()) ||
				                   item.getName().equalsIgnoreCase((String)sysOptionMap.get("ysze_status_menu"));
				else
					readonly = true;
				item.setReadonly(readonly);  
				if(item.getName().equalsIgnoreCase((String)sysOptionMap.get("ysze_status_menu")))
					item.setCodesetid("0");
				fieldList.add(item);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);	
		}
		
		return fieldList;
	}
	/**
	 * 获得预算总额表getFieldList0
	 */
	public ArrayList getFieldList0(String b0110)throws GeneralException
	{
		ArrayList fieldList=new ArrayList();
		ArrayList list=new ArrayList();
	    ContentDAO dao=new ContentDAO(this.conn);
	    RowSet rs=null;
		try
		{
			String set = (String)sysOptionMap.get("ysze_set");
			String suoYin = ((String)sysOptionMap.get("ysze_idx_menu")).toLowerCase();
			String bugdet_id=getCurrentBudgetId().toString();
			list=DataDictionary.getFieldList(set,Constant.USED_FIELD_SET);
			StringBuffer sql=new StringBuffer();
			FieldItem item=null;
			String str="";
//			SimpleDateFormat sdf=new SimpleDateFormat ("yyyy-mm-dd");
			sql.append("select * from ");
			sql.append(set+" where ");
			sql.append("b0110='");
			sql.append(b0110+"' and "+suoYin+"=" +bugdet_id);
			rs=dao.search(sql.toString());
			while(rs.next()){
				for(int i=0;i<list.size();i++)
				{ 
					item=(FieldItem)((FieldItem)list.get(i)).cloneItem();
					String quanxian=this.userView.analyseFieldPriv(item.getItemid());
					if("I9999".equalsIgnoreCase(item.getItemid()) ||
					   item.getItemid().equalsIgnoreCase((String)sysOptionMap.get("ysze_idx_menu")) ||
					   item.getItemid().equalsIgnoreCase(set+"Z0") ||item.getItemid().equalsIgnoreCase(set+"Z1")||
					   item.getItemid().equalsIgnoreCase((String)sysOptionMap.get("ysze_status_menu") ))
					{
						continue;
					}
					LazyDynaBean bean = new LazyDynaBean();
					String ss=item.getItemid();
					str=rs.getString(ss);
					if(str==null){
						str="";
					}
					String itemtype=item.getItemtype();
					if("D".equalsIgnoreCase(itemtype)&&!"".equals(str)){
					  DateFormat df = DateFormat.getDateInstance();
		           	  Date datevalue=df.parse(str);
		           	  str=DateStyle.dateformat(datevalue,"yyyy-MM-dd");  
					}
					int decimalwidth=item.getDecimalwidth();
					int itemlength=item.getItemlength();
					bean.set("itemtype", itemtype);
					bean.set("decimalwidth", String.valueOf(decimalwidth));
					bean.set("itemlength", String.valueOf(itemlength));
					bean.set("priv", quanxian);
					bean.set("itemid", item.getItemid().toLowerCase());
					bean.set("itemdesc", item.getItemdesc());
					bean.set("value", str);
					fieldList.add(bean);
					}
			 }
				
			}catch(Exception e)
			{
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);	
			}
			return fieldList;
	}

	/**
	 * 获得预算参数表的fieldList1
	 */
	public ArrayList getFieldList1(String b0110)throws GeneralException
	{
		ArrayList fieldList=new ArrayList();
		ArrayList list=new ArrayList();
	    ContentDAO dao=new ContentDAO(this.conn);
	    RowSet rs=null;
		try
		{
			String set = (String)sysOptionMap.get("ysparam_set");
			String suoYin = ((String)sysOptionMap.get("ysparam_idx_menu")).toLowerCase();
			String bugdet_id=getCurrentBudgetId().toString();
			list=DataDictionary.getFieldList(set,Constant.USED_FIELD_SET);//数据字典获得字段属性
			StringBuffer sql=new StringBuffer();
			FieldItem item=null;
			String str="";
//			SimpleDateFormat sdf=new SimpleDateFormat ("yyyy-mm-dd");
			sql.append("select * from ");
			sql.append(set+" where ");
			sql.append("b0110='");
			sql.append(b0110+"' and "+suoYin+"=" +bugdet_id);
			rs=dao.search(sql.toString());
			if(rs.next()){

				for(int i=0;i<list.size();i++)
				{ 
					item=(FieldItem)((FieldItem)list.get(i)).cloneItem();
					String quanxian=this.userView.analyseFieldPriv(item.getItemid());//获得用户相应指标的权限
					if("I9999".equalsIgnoreCase(item.getItemid()) ||
					   item.getItemid().equalsIgnoreCase((String)sysOptionMap.get("ysparam_idx_menu")) ||
					   item.getItemid().equalsIgnoreCase(set+"Z0") ||item.getItemid().equalsIgnoreCase(set+"Z1"))
					{
						continue;
					}
					LazyDynaBean bean = new LazyDynaBean();
					String ss=item.getItemid();
					str=rs.getString(ss);
					if(str==null){
						str="";
					}
					String itemtype=item.getItemtype();
					if("D".equalsIgnoreCase(itemtype)&&!"".equals(str)){
					  DateFormat df = DateFormat.getDateInstance();
		           	  Date datevalue=df.parse(str);
		           	  str=DateStyle.dateformat(datevalue,"yyyy-MM-dd");  
					}
					int decimalwidth=item.getDecimalwidth();
					int itemlength=item.getItemlength();
					bean.set("itemtype", itemtype);
					bean.set("decimalwidth", String.valueOf(decimalwidth));
					bean.set("itemlength", String.valueOf(itemlength));
					if (decimalwidth>0) {
						bean.set("maxlength", String.valueOf(itemlength + decimalwidth + 1));
					} else {
						bean.set("maxlength", String.valueOf(itemlength));
					}
					
					bean.set("priv", quanxian);
					bean.set("itemid", item.getItemid());
					bean.set("itemdesc", item.getItemdesc());
					bean.set("value", str);
					fieldList.add(bean);
					}
				

			}else{
				for(int i=0;i<list.size();i++)
				{
					item=(FieldItem)((FieldItem)list.get(i)).cloneItem();
					if("I9999".equalsIgnoreCase(item.getItemid()) ||
					   item.getItemid().equalsIgnoreCase((String)sysOptionMap.get("ysparam_idx_menu")) ||
					   item.getItemid().equalsIgnoreCase(set+"Z0") ||item.getItemid().equalsIgnoreCase(set+"Z1"))
					{
						continue;
					}	
					
					String quanxian=this.userView.analyseFieldPriv(item.getItemid());
					LazyDynaBean bean = new LazyDynaBean();
					String itemtype=item.getItemtype();
					int decimalwidth=item.getDecimalwidth();
					int itemlength=item.getItemlength();
					bean.set("itemtype", itemtype);
					bean.set("decimalwidth", String.valueOf(decimalwidth));
					bean.set("itemlength", String.valueOf(itemlength));
					if (decimalwidth>0) {
						bean.set("maxlength", String.valueOf(itemlength + decimalwidth + 1));
					} else {
						bean.set("maxlength", String.valueOf(itemlength));
					}
					bean.set("priv", quanxian);
					bean.set("itemid", item.getItemid());
					bean.set("itemdesc", item.getItemdesc());
					bean.set("value", "");
					fieldList.add(bean);
				}
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);	
		}
		
		return fieldList;
	}
	/**
	 * 返回当前预算id
	 * @return 未定义返回0
	 */
	public Integer getCurrentBudgetId()
	{
		Integer budgetId = new Integer(0);
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select max(budget_id) as budget_id from gz_budget_index where budgettype<4");
			if(rowSet.next())
			{
				budgetId = Integer.valueOf(rowSet.getInt("budget_id"));
			}
			
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return budgetId;
	}
	
	/**
	 * 取预算描述
	 * @param budget_id
	 * @return
	 * @throws GeneralException
	 */
	public String getBudgetDesc(Integer budget_id)throws GeneralException
	{
		String desc="";
		try
		{
			RecordVo vo = null;
			if (budget_id != null)
				vo=getRecordVo("gz_budget_index","budget_id",budget_id.intValue());
			if (vo==null||budget_id==null)
			{
				desc = ResourceFactory.getProperty("gz.budget.budgeting.noys");
			}
			else 
			{
				int yearNum=vo.getInt("yearnum");
				int budgetType=vo.getInt("budgettype");  //1 年初预算	  2 年中预算	3 特别调整
				desc=yearNum+ResourceFactory.getProperty("datestyle.year");
				if(budgetType==1)
					desc+=ResourceFactory.getProperty("gz.budget.budgeting.yearc");
				else if(budgetType==2)
					desc+=ResourceFactory.getProperty("gz.budget.budgeting.yearz");
				else if(budgetType==3)
					desc+=ResourceFactory.getProperty("gz.budget.budgeting.tbtz");
				else{
					desc=ResourceFactory.getProperty("gz.budget.budgeting.noys");
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);	
		}
		return desc;
	}
	
	public LazyDynaBean getBudgetStatus(Integer budget_id)
	{
		LazyDynaBean abean=new LazyDynaBean();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select * from gz_budget_index where budget_id = "+budget_id.toString());
			if(rowSet.next())
			{
				String  _status=rowSet.getString("SPFlag");
				CodeItem item=AdminCode.getCode("23", _status);
				abean.set("status",_status);
				abean.set("statusDesc", item.getCodename());
			}
			else
			{
				abean.set("status", "");
				abean.set("statusDesc", ResourceFactory.getProperty(""));  // 起草
			}
			
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return abean;		
	}
	
	/**
	 * 获得单位的预算总额、状态等信息
	 * @param b0110
	 * @param budget_id
	 * @return
	 */
	public LazyDynaBean getAppealStatus(String b0110,String budget_id)
	{
		LazyDynaBean abean=new LazyDynaBean();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			if(sysOptionMap!=null&&sysOptionMap.get("ysze_set")!=null&&sysOptionMap.get("ysze_idx_menu")!=null&&
			   sysOptionMap.get("ysze_status_menu")!=null)
			{
				String set=(String)sysOptionMap.get("ysze_set");
				String idx=(String)sysOptionMap.get("ysze_idx_menu");
				String status=(String)sysOptionMap.get("ysze_status_menu");
				String ze=(String)sysOptionMap.get("ysze_ze_menu");
				String z0=set+"Z0";
				String z1=set+"Z1";
				RowSet rowSet=dao.search("select * from "+set+" where "+idx+"="+budget_id+" and b0110='"+b0110+"'");
				if(rowSet.next())
				{
					String  _status=rowSet.getString(status);
					CodeItem item=AdminCode.getCode("23", _status);
					abean.set("status",_status);
					abean.set("statusDesc", item==null?_status:item.getCodename());
					abean.set("amount", Double.valueOf(rowSet.getDouble(ze)));
					abean.set("Z0", rowSet.getDate(z0)==null?null:PubFunc.FormatDate(rowSet.getDate(z0)));
					abean.set("Z1", rowSet.getString(z1));
					abean.set("exists", "1");
				}
				else
				{
					abean.set("status", "01");
					abean.set("statusDesc", ResourceFactory.getProperty("performance.workdiary.worknobao"));
					abean.set("amount", Double.valueOf(0));
					abean.set("exists", "0");
				}
				
				if(rowSet!=null)
					rowSet.close();
			}
			
			 
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return abean;
	}

/**
 * 获得本单位的下级单位预算总额、状态等信息
 * @param b0110
 * @param budget_id
 * @return
 */
public LazyDynaBean getAppealStatus_1(String b0110,String budget_id)
{
	LazyDynaBean abean=new LazyDynaBean();
	try
	{
		ContentDAO dao=new ContentDAO(this.conn);
		if(sysOptionMap!=null&&sysOptionMap.get("ysze_set")!=null&&sysOptionMap.get("ysze_idx_menu")!=null&&
		   sysOptionMap.get("ysze_status_menu")!=null)
		{
			String set=(String)sysOptionMap.get("ysze_set");
			String idx=(String)sysOptionMap.get("ysze_idx_menu");
			String status=(String)sysOptionMap.get("ysze_status_menu");
			String ze=(String)sysOptionMap.get("ysze_ze_menu");
			String z0=set+"Z0";
			String z1=set+"Z1";
			RowSet rowSet=dao.search("select min(b0110) from "+set+" where "+idx+"="+budget_id+" and b0110 like '"+b0110+"%' and b0110!='"+b0110+"'"  );
			if(rowSet.next())
			{
				String  unitcode=rowSet.getString(1);
				RowSet  rowSet1=dao.search("select * from "+set+" where "+idx+"="+budget_id+" and b0110='"+unitcode+"'");
				while(rowSet1.next()){
					abean.set("status1", rowSet1.getString(status));
				}
			}	
			if(rowSet!=null)
				rowSet.close();
		}
		
		 
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
	return abean;
}
}
