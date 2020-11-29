package com.hjsj.hrms.businessobject.gz.gz_budget.budgeting;

import com.hjsj.hrms.businessobject.gz.gz_budget.budget_rule.options.BudgetSysBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.struts.upload.FormFile;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class BudgetingBo {
	private UserView userView;
	private Connection con;
	/**错误提示信息*/
	private String errorMessage="";
	/**总额子集*/
	private String zeSet = "";
	/**总额子集索引指标*/
	private String zeIndex="";
	/**总额子集总额指标*/
	private String zeField="";
	/**总额子集生僻状态指标*/
	private String zeSpFlag="";
	/**预算参数子集*/
	private String paramSet = "";
	/**预算参数子集索引指标*/
	private String paramIndex="";
	/**预算参数子集，新员工入职月份指标*/
	private String paramMonthField="";
	/**当前索引*/
	private String currentIndex="";
	/**人员类别代码类 */
	private String sc000CodeSet="0";
	/**当前最近预算单位**/
	private String b0110="";
	private RecordVo gz_budget_tabVo=null;
	private String canImports="false";
	private String strExportFlds="";
	
	/**
	 * 
	 * @param con
	 * @param userView
	 * @param loadysParam 是否加载预算参数设置
	 * @param tab_id 如果有值，则初始化gz_budget_tabVo
	 */
	public BudgetingBo(Connection con,UserView userView,boolean loadysParam,String tab_id){
		this.userView=userView;
		this.con=con;
		if(loadysParam)
			this.analyseParameter();
		if(tab_id!=null&&!"".equals(tab_id)&&tab_id.indexOf("zonge")!=0&&tab_id.indexOf("params")!=0)
			this.initVo(tab_id);
			
	}
	public BudgetingBo(Connection con){
		this.con=con;
	} 
	public void initVo(String tab_id){
		try{
			ContentDAO dao = new ContentDAO(this.con);
			gz_budget_tabVo = new RecordVo("gz_budget_tab");
			gz_budget_tabVo.setInt("tab_id", Integer.parseInt(tab_id));
			gz_budget_tabVo=dao.findByPrimaryKey(gz_budget_tabVo);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * 判断是否能够进入该模块
	 */
	public void canEnter(){
		RowSet rs = null;
		try{
			String b0110=this.getUnitcode();
			if(this.userView.getUserOrgId()==null|| "".equals(this.userView.getUserOrgId().trim())){
				this.setErrorMessage(ResourceFactory.getProperty("gz.budget.budgeting.selfuser"));
				return;
			}
			StringBuffer buf = new StringBuffer();
			buf.append(" select max(budget_id) budget_id from gz_budget_index where (budgetType=1 or budgetType=2 or budgetType=3)");
			ContentDAO dao = new ContentDAO(this.con);
			rs=dao.search(buf.toString());
			String maxIndex="";
			while(rs.next())
			{
				maxIndex=rs.getString("budget_id");
			}
			if(maxIndex==null|| "".equals(maxIndex)){
				this.setErrorMessage(ResourceFactory.getProperty("gz.budget.budgeting.noys"));
				return;
			}
			buf.setLength(0);
			buf.append("select b0110 from "+this.getZeSet()+" where b0110='"+b0110+"'");
			buf.append(" and "+this.getZeIndex()+"="+maxIndex);
			buf.append(" and "+this.getZeSpFlag()+"<>'01' ");
			rs=dao.search(buf.toString());
			boolean flag=true;
		    while(rs.next())
		    {
		    	flag=false;
		    	break;
		    }
			if(flag)
			{
				//this.setErrorMessage(ResourceFactory.getProperty("gz.budget.budgeting.nodata"));
				this.setErrorMessage(ResourceFactory.getProperty("gz.budget.budgeting.cannotedit"));
		    	return;
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null)
					rs.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	/**
	 * 分析设置的参数
	 */
	public void analyseParameter(){
		//RowSet rs = null;
		try{
			BudgetSysBo bo = new BudgetSysBo(this.con, this.userView);
			HashMap sysMap = bo.getSysValueMap();
			zeSet = (String)sysMap.get("ysze_set");
			zeIndex = (String)sysMap.get("ysze_idx_menu");
			zeField = (String)sysMap.get("ysze_ze_menu");
			zeSpFlag = (String)sysMap.get("ysze_status_menu");
			
			paramSet = (String)sysMap.get("ysparam_set");
			paramIndex = (String)sysMap.get("ysparam_idx_menu");
			paramMonthField = (String)sysMap.get("ysparam_newmonth_menu");
			sc000CodeSet = (String)sysMap.get("rylb_codeset");
	
			/**
			ContentDAO dao = new ContentDAO(this.con);
			rs = dao.search("select str_value from constant where UPPER(constant)='GZ_BUDGET_PARAMS'");
			String xml="";
			while(rs.next()){
				xml=Sql_switcher.readMemo(rs, "str_value");
			}
			if(xml.equals("")||xml==null)
				xml="<?xml version=\"1.0\" encoding=\"GB2312\"?><params></params>";
			//xml="<?xml version=\"1.0\" encoding=\"GB2312\"?><params><ysze set=\"BB2\" idx_menu=\"BB2Z2\" ze_menu=\"BB2Z3\" status_menu=\"BB2Z4\"/><ysparam set=\"BB3\" idx_menu=\"BB3Z2\" newmonth_menu=\"BB3Z3\"/></params>";
			Document doc=PubFunc.generateDom(xml);
			XPath xpath = XPath.newInstance("/params/ysze");
			Element ysze = (Element)xpath.selectSingleNode(doc);
			xpath = XPath.newInstance("/params/ysparam");
			Element ysparam=(Element)xpath.selectSingleNode(doc);
			if(ysze==null||ysparam==null){
				this.setErrorMessage(ResourceFactory.getProperty("gz.budget.budgeting.ysparam"));
				return;
			}
			String zeSet = ysze.getAttributeValue("set");
			String zeIndex=ysze.getAttributeValue("idx_menu");
			String zeField=ysze.getAttributeValue("ze_menu");
			String zeSpflag=ysze.getAttributeValue("status_menu");
			String paramSet = ysparam.getAttributeValue("set");
			String paramIndex=ysparam.getAttributeValue("idx_menu");
			String paramMonthField=ysparam.getAttributeValue("newmonth_menu");
			sc000CodeSet=ysparam.getAttributeValue("");
			
			this.setZeSet(zeSet);
			this.setZeIndex(zeIndex);
			this.setZeField(zeField);
			this.setZeSpFlag(zeSpflag);
			this.setParamSet(paramSet);
			this.setParamMonthField(paramMonthField);
			this.setParamIndex(paramIndex);
			*/
		}catch(Exception e){
			e.printStackTrace();
		}finally{
//			try{
//				if(rs!=null)
//					rs.close();
//			}catch(Exception e){
//				e.printStackTrace();
//			}
		}
	}
	
	public void initData(){
		RowSet rs = null;
		String b0110=this.getUnitcode();
		this.getIndex();
		String tabname="Sc03";
		try{
			if(this.gz_budget_tabVo!=null)
			{				
				ContentDAO dao = new ContentDAO(this.con);
                if(this.gz_budget_tabVo.getInt("tab_type")==3)
                {
                    tabname="Sc02";  
                } else   if(this.gz_budget_tabVo.getInt("tab_type")==4)
				{
				    ; 
				}
				StringBuffer buf = new StringBuffer("select count(*) from ");				
				buf.append(" "+tabname+" where ");
				buf.append(" budget_id="+this.getCurrentIndex());
				buf.append(" and tab_id="+this.gz_budget_tabVo.getInt("tab_id"));
				buf.append(" and b0110='"+b0110+"'");
				rs = dao.search(buf.toString());
				boolean isAdd=false;
				while(rs.next())
				{
					if(rs.getInt(1)==0)
						isAdd=true;
				}
			
				String codesetid=this.gz_budget_tabVo.getString("codesetid");				
				ArrayList list = this.getCodeitemListHasOrder(codesetid);
				if(isAdd){
						buf.setLength(0);
						buf.append("insert into "+tabname+" (budget_id,tab_id,b0110,itemid,itemdesc,seq) values (?,?,?,?,?,?) ");
						ArrayList valueList = null;
						for(int i=0;i<list.size();i++){
							LazyDynaBean bean = (LazyDynaBean)list.get(i);
							String codeitemid=(String)bean.get("codeitemid");
							String codeitemdesc=(String)bean.get("codeitemdesc");
							valueList = new ArrayList();
							valueList.add(this.getCurrentIndex());
							valueList.add(this.gz_budget_tabVo.getString("tab_id"));
							valueList.add(b0110);
							valueList.add(codeitemid);
							valueList.add(codeitemdesc);
							valueList.add((i+1)+"");
							dao.insert(buf.toString(), valueList);
						}
						this.setLastSummy(tabname,b0110, this.gz_budget_tabVo.getInt("tab_id"));
				}
				else {//更新项目顺序 wangrd 2013-12-01
	                ArrayList valueList = null;
	                buf.setLength(0);
	                buf.append("update  ");
	                buf.append(tabname);
	                buf.append(" set seq=?");
	                
	                buf.append(" where budget_id="+this.currentIndex);
	                buf.append(" and B0110='"+this.getUnitcode()+"'");
	                buf.append(" and tab_id="+this.gz_budget_tabVo.getInt("tab_id"));
	                buf.append(" and itemid=?");
	                for(int i=0;i<list.size();i++){
	                    LazyDynaBean bean = (LazyDynaBean)list.get(i);
	                    String codeitemid=(String)bean.get("codeitemid");		      
                        valueList = new ArrayList();
                        valueList.add(String.valueOf(i+1));
                        valueList.add(codeitemid);
                        dao.update(buf.toString(), valueList);  	
	                }
				}
	

			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null){
					rs.close();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
	}
	
	public void initAllData(){
		RowSet rs = null;
		RowSet rsTabs = null;
		String b0110=this.getUnitcode();
		ContentDAO dao = new ContentDAO(this.con);
		this.getIndex();
		boolean isAdd=true;
		try{
			StringBuffer buf = new StringBuffer();
			buf.append("select count(*) from ");
			buf.append(" Sc03 where ");
			buf.append(" budget_id="+this.getCurrentIndex());
			buf.append(" and b0110='"+b0110+"'");
			rs = dao.search(buf.toString());			
			if (rs.next()){
				if(rs.getInt(1)!=0)	isAdd=false ;
			}
			if (isAdd){
				buf.setLength(0);
				buf.append("select count(*) from ");
				buf.append(" Sc02 where ");
				buf.append(" budget_id="+this.getCurrentIndex());
				buf.append(" and b0110='"+b0110+"'");
				rs = dao.search(buf.toString());
				if (rs.next()){
					if(rs.getInt(1)!=0)	isAdd=false;
				}
				
			}
			if (!isAdd) return;
			
			buf.setLength(0);
			buf.append(" select tab_id,tab_type,codesetid  from gz_budget_tab ");
			buf.append(" where validFlag=1 and tab_type in (3,4)  order by seq");
			rsTabs = dao.search(buf.toString());

			while (rsTabs.next()){
				String tab_id= rsTabs.getString("tab_id");
				int tab_type= rsTabs.getInt("tab_type");	
				String codesetid=rsTabs.getString("codesetid");
				String tabName="Sc02";
				if(tab_type==4) {tabName="Sc03";}
				
				buf.setLength(0);
				buf.append("insert into "+tabName+" (budget_id,tab_id,b0110,itemid,itemdesc,seq) values (?,?,?,?,?,?) ");
				ArrayList list = this.getCodeitemListHasOrder(codesetid);
				ArrayList valueList = null;
				for(int i=0;i<list.size();i++){
					LazyDynaBean bean = (LazyDynaBean)list.get(i);
					String codeitemid=(String)bean.get("codeitemid");
					String codeitemdesc=(String)bean.get("codeitemdesc");
					valueList = new ArrayList();
					valueList.add(this.getCurrentIndex());
					valueList.add(tab_id);
					valueList.add(b0110);
					valueList.add(codeitemid);
					valueList.add(codeitemdesc);
					valueList.add((i+1)+"");
					dao.insert(buf.toString(), valueList);
				}			
			}
			
			this.setLastSummy("SC03",b0110, -1);
			this.setLastSummy("SC02",b0110, -1);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null){
					rs.close();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
	}
	
	// 上期数据
	private int getPriorBudgetId() {
		int idx = -1;
		ContentDAO dao = new ContentDAO(this.con);
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append("select * from gz_budget_index A ");
			buffer.append("where exists(select 1 from gz_budget_index B where B.budget_id = ");
			buffer.append(this.getCurrentIndex());
			buffer.append(" and A.yearNum<=B.yearNum and A.budgetType<=3 and B.firstMonth>=A.firstMonth and B.budget_id>A.budget_id )");
			buffer.append(" order by budget_id desc"); 
			
			RowSet rSet = dao.search(buffer.toString());
			if (rSet.next()) {
				idx = rSet.getInt("budget_id");
			}
			rSet.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return idx;			
	}
	
	// 取本次开始月份
	// 取开始月份，通过预算索引号
	public int getStartMonth() {
		int startMonth=0;
		if ("".equals(this.getCurrentIndex()))this.getIndex();
		ContentDAO dao = new ContentDAO(con);
		StringBuffer sb = new StringBuffer(
				"select firstMonth, SPFlag from gz_budget_index where budget_id=");
		sb.append(this.getCurrentIndex());
		RowSet rs = null;
		try {
			rs = dao.search(sb.toString());
			while (rs.next()) {
				startMonth = rs.getInt("firstMonth");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return startMonth;	
	}

	
	// 读取上期数据
	private void setLastSummy(String tabname, String b0110, int tabid) {
		int lastId = getPriorBudgetId();
		if ( lastId >= 0){
			
			ContentDAO dao = new ContentDAO(con);		
			try {
				// 条件
				String updSql, swnString, swwString;
				// 起始月份前的数据
				int firstMonth = getStartMonth();
				// 起始月份前数据复制字符串
				StringBuffer bufSQL = new StringBuffer();
				StringBuffer bufFlds = new StringBuffer();
				for (int i=1; i<firstMonth; i++) {
					bufSQL.append(", val_");
					bufSQL.append(i);
					bufSQL.append("=A.val_");
					bufSQL.append(i);
					
					bufFlds.append(",val_");
					bufFlds.append(i);
				}
				
				if (tabid == -1) {
					swnString = " and b0110='" + b0110 + "' ";
					swwString = swnString;
				} else {
					swnString = " and b0110='" + b0110 + "' and tab_id="+tabid;
					swwString = " and @tb.b0110='" + b0110 + "' and @tb.tab_id="+tabid;
				}
				
				switch (Sql_switcher.searchDbServer()) {
				case Constant.MSSQL:
					updSql = "update @tb SET lastYearSum=A.thisYearSum " + bufSQL.toString()
						   + " from (select itemid, tab_id, thisYearSum " + bufFlds.toString()
						   + " from @tb where budget_id=" + lastId + swnString + ") A "
						   + "where @tb.itemid=A.itemid and @tb.tab_id=A.tab_id and budget_id=" + this.getCurrentIndex() + swwString;
					break;
				case Constant.ORACEL:
					updSql = "update @tb SET (lastYearSum" + bufFlds.toString() 
					   + ")=(select thisYearSum " + bufFlds.toString() 
					   + " from (select itemid, tab_id, thisYearSum " + bufFlds.toString()
					   + " from @tb where  budget_id=" + lastId + swnString + ") A "
					   + " where @tb.itemid=A.itemid and @tb.tab_id=A.tab_id) "
					   + "where budget_id=" + this.getCurrentIndex() + swwString;
					break;
				default:
					updSql="";
					break;
				}
				
				updSql = PubFunc.replace(updSql, "@tb", tabname);					
				dao.update(updSql);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}		
	}
	
	public ArrayList getCodeitemListHasOrder(String codesetid){
		ArrayList list = new ArrayList();
		RowSet rs = null;
		try{
			ContentDAO dao = new ContentDAO(this.con);
			rs = dao.search("select codeitemid,codeitemdesc,parentid from codeitem"
			         +" where UPPER(codesetid)='"+codesetid.toUpperCase()+"' and codeitemid=parentid"
			         +" order by a0000,codeitemid");
			while(rs.next()){
				String codeitemid=rs.getString("codeitemid");
				String codeitemdesc=rs.getString("codeitemdesc");
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("codeitemid",codeitemid);
				bean.set("codeitemdesc",codeitemdesc);
				list.add(bean);
				if(this.hasChild(codesetid, codeitemid))
			    	this.doMethod(dao, list, codesetid, codeitemid,0);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	public void doMethod(ContentDAO dao,ArrayList list,String codesetid,String parent_id,int layer){
		RowSet rs = null;
		try{
			rs = dao.search("select codeitemid,codeitemdesc from codeitem "
			        +"where UPPER(codesetid)='"+codesetid.toUpperCase()
			        +"' and codeitemid<>parentid and parentid='"+parent_id+"'"
			        + " order by a0000,codeitemid");
			layer++;
			while(rs.next()){
				String codeitemid=rs.getString("codeitemid");
				String codeitemdesc=rs.getString("codeitemdesc");
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("codeitemid",codeitemid);
				bean.set("codeitemdesc",this.getBlank(layer)+codeitemdesc);
				list.add(bean);
				if(!this.hasChild(codesetid, codeitemid))
					continue;
				this.doMethod(dao, list, codesetid, codeitemid,layer);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null)
					rs.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	public String getBlank(int layer){
		StringBuffer buf = new StringBuffer();
		for(int i=0;i<layer;i++){
			buf.append("    ");
		}
		return buf.toString();
	}
	public boolean hasChild(String codesetid,String parent_id){
		boolean flag=false;
		RowSet rs = null;
		try{
		    ContentDAO dao = new ContentDAO(this.con);
		    rs = dao.search("select codeitemid,codeitemdesc from codeitem where UPPER(codesetid)='"+codesetid.toUpperCase()+"' and parentid='"+parent_id+"'");
		    while(rs.next())
		    {
		    	flag=true;
		    	break;
		    }
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null)
					rs.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return flag;
	}
	public String getIndex(){
		String index="";
		RowSet rs = null;
		try{
			ContentDAO dao = new ContentDAO(this.con);
			rs = dao.search("select max(budget_id) budget_id from gz_budget_index where (budgetType=1 or budgetType=2 or budgetType=3)");
			while(rs.next()){
				index=rs.getString("budget_id");//改了
				this.setCurrentIndex(index);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null)
					rs.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return index;
	}
	/**
	 * 得到展现数据的sql语句
	 * @return
	 */
	public String getSQL(){
		if(this.gz_budget_tabVo.getInt("tab_type")!=1){
			this.initData();
		}
		String b0110=this.getUnitcode();
		StringBuffer sql = new StringBuffer("select * from ");
		if(this.gz_budget_tabVo!=null)
		{
			switch(this.gz_budget_tabVo.getInt("tab_type")){
			case 1:
				this.getIndex();
				sql.append(this.getTableName());
				sql.append(" where ");
				sql.append(this.getZeIndex()+"="+this.getCurrentIndex());
				sql.append(" and b0110='"+b0110+"'");
				break;
			case 2:
//				this.getIndex();
//				sql.append(this.getTableName());
//				sql.append(" where ");
//				sql.append(" budget_id="+this.getCurrentIndex());
//				sql.append(" and b0110='"+b0110+"'");
				sql.setLength(0);
				sql.append(getSQL("",""));
				break;

			case 3:
				sql.append(this.getTableName());
				sql.append(" where ");
				sql.append(" budget_id="+this.getCurrentIndex());
				sql.append(" and tab_id="+this.gz_budget_tabVo.getInt("tab_id"));
				sql.append(" and b0110='"+b0110+"' order by seq");
				break;
			case 4:
				sql.append(this.getTableName());
				sql.append(" where ");
				sql.append(" budget_id="+this.getCurrentIndex());
				sql.append(" and tab_id="+this.gz_budget_tabVo.getInt("tab_id"));
				sql.append(" and b0110='"+b0110+"' order by seq");
				break;
			}
		}
		return sql.toString();
	}
	/**
	 * 得到展现薪资预算名册的sql语句
	 * @return
	 */
	public String getSQL(String unitcode,String PersonName){
		String b0110="'"+this.getUnitcode()+"'";
		boolean isRoot = unitcode == null || unitcode.length()==0; 
		String sql = "";
		String sqlOrder = " order by A0000, sc000, sc010";  // 顺序，类别，主键
		this.getIndex();
		if ("".equals(PersonName)){
			if(isRoot){
				sql="select * from "+this.getTableName()+
				" where budget_id="+ 
				this.getCurrentIndex()+" and b0110="+b0110+sqlOrder; 
			}else{
				sql="select * from "+this.getTableName()+" where budget_id= "+this.getCurrentIndex()+" and b0110="+b0110
				   +" and e0122 like '"+unitcode+"%'" + sqlOrder;
			}
		}
		else {			
			sql="select * from "+this.getTableName()+
			" where budget_id="+ 
			this.getCurrentIndex()+" and b0110="+b0110+" and A0101 like '"+ PersonName+"%'"; 
		}

		return sql;
	}
	//得到批量导出模板的sql语句
	public String getBatchSQL(int j,String tab_id){
		
		//把该预算表的数据插入到SC03表中（如果没有）
		this.executeInsert(tab_id);
		StringBuffer sql = new StringBuffer();
		sql.append("select * from ");
		sql.append(this.getTableName());
		sql.append(" where ");
		sql.append(" budget_id="+this.getCurrentIndex());
		sql.append(" and tab_id ="+tab_id);
		sql.append(" and b0110='"+this.userView.getUserOrgId()+"' order by seq");
		return sql.toString();
	
	}
	//得到预算分类 即计提，支出等
	public String getBudgetGroup(int tab_id){
		String str = "";
		
		RowSet rs = null;
		try{
			StringBuffer buf = new StringBuffer();
			buf.append("select budgetgroup from gz_budget_tab where tab_id="+tab_id+" and tab_type<>3");
			ContentDAO dao = new ContentDAO(this.con);
			rs=dao.search(buf.toString());
			if(rs.next()){
				String budgetgroup = rs.getString("budgetgroup");
				if(!(budgetgroup==null || "".equals(budgetgroup)))
					str = budgetgroup;
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null)
					rs.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return str;
	}
	//根据预算分类得到tabid号
	public ArrayList getTabidList(String budgetGroup){

		RowSet rs = null;
		ArrayList list = new ArrayList();
		try{
			StringBuffer buf = new StringBuffer();
			if("".equals(budgetGroup))
				buf.append("select tab_id,tab_name from gz_budget_tab where tab_type=3");
			else
				buf.append("select tab_id,tab_name from gz_budget_tab where budgetgroup='"+budgetGroup+"' and tab_type<>3 order by seq");
			ContentDAO dao = new ContentDAO(this.con);
			rs=dao.search(buf.toString());
			while(rs.next()){
				String tab_id = rs.getString("tab_id");
				String tab_name = rs.getString("tab_name");
				list.add(tab_id+"`"+tab_name);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null)
					rs.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return list;
	
	}
	//根据预算分类得到tab_id和tab_name的map
	public HashMap getTabNameIdMap(String budgetGroup){

		RowSet rs = null;
		HashMap map = new HashMap();
		try{
			StringBuffer buf = new StringBuffer();
			if("".equals(budgetGroup))
				buf.append("select tab_id,tab_name from gz_budget_tab where tab_type=3");
			else
				buf.append("select tab_id,tab_name from gz_budget_tab where budgetgroup='"+budgetGroup+"' order by seq");
			ContentDAO dao = new ContentDAO(this.con);
			rs=dao.search(buf.toString());
			while(rs.next()){
				String tab_id = rs.getString("tab_id");
				String tab_name = rs.getString("tab_name");
				map.put(tab_name, tab_id);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null)
					rs.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return map;
	
	
	}

	public String getUnitSpflag(){
		RowSet rs = null;
		String spflag="";
		String b0110=this.getUnitcode();
		try{
			StringBuffer buf = new StringBuffer();
			buf.append("select "+this.getZeSpFlag()+" from "+this.getZeSet()+" where b0110='"+b0110+"'");
			buf.append(" and "+this.getZeIndex()+"=");
			buf.append(" (select max(budget_id) budget_id from gz_budget_index where (budgetType=1 or budgetType=2 or budgetType=3))");
			ContentDAO dao = new ContentDAO(this.con);
			rs=dao.search(buf.toString());
			while(rs.next()){
				spflag=rs.getString(1);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null)
					rs.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return spflag;
	}
	//获取预算表的名称 郭峰增加
	public String getTab_Name(String tabid){

		String str="";
		RowSet rs = null;
		try{
			ContentDAO dao = new ContentDAO(this.con);
			StringBuffer sb = new StringBuffer();
			sb.append("select tab_name from gz_budget_tab where tab_id="+tabid);
			rs=dao.search(sb.toString());
			if(rs.next()){
				str = rs.getString("tab_name");
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null)
					rs.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return str;
	
	}
	public String getInfoStr(String tab_name){
		String str="";
		String b0110=this.getUnitcode();
		int budget_id=0;
		RowSet rs = null;
		try{
			String spflag=this.getUnitSpflag();
			String spflagDesc="";
			if("04".equals(spflag)){
				this.setCanImports("true");
				spflagDesc="起草";
			}else if("07".equals(spflag)){
				this.setCanImports("true");
				spflagDesc="驳回";
			}else if("02".equals(spflag))
				spflagDesc="已报批";
			else if("03".equals(spflag)){
				spflagDesc="已批";
			}
			/*gz.budget.budgeting.ysunit=预算单位
			gz.budget.budgeting.currentys=当前预算
			gz.budget.budgeting.yearc=年初预算
			gz.budget.budgeting.yearz=年中预算
			gz.budget.budgeting.tbtz=特别调整
			gz.budget.budgeting.sz=实支
			gz.budget.budgeting.wbsf=外部实发*/
			String ysUnit=AdminCode.getCodeName("UN",b0110);
			
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.con);
			String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
			if(display_e0122==null|| "00".equals(display_e0122)|| "".equals(display_e0122))
				display_e0122="0";
			if(Integer.parseInt(display_e0122)>0)
			{
				CodeItem item=AdminCode.getCode("UN",b0110,Integer.parseInt(display_e0122));
    	    	if(item==null)
    	    	{
    	    		ysUnit=AdminCode.getCodeName("UN",b0110);
        		}
    	    	else
    	    	{
    	    		ysUnit=item.getCodename(); 
    	    	}
    	    	
			} 
			
			ContentDAO dao = new ContentDAO(this.con);
			String currentYs="";
			rs=dao.search(" select budget_id,yearNum,budgetType from gz_budget_index where budget_id=(select max(budget_id) from gz_budget_index where (budgetType=1 or budgetType=2 or budgetType=3))");
			while(rs.next()){
				String yearNum=rs.getString("yearNum");
				int budgetType=rs.getInt("budgetType");
				budget_id=rs.getInt("budget_id");
				currentYs=yearNum+ResourceFactory.getProperty("datestyle.year");
				if(budgetType==1)
					currentYs+=ResourceFactory.getProperty("gz.budget.budgeting.yearc");
				else if(budgetType==2)
					currentYs+=ResourceFactory.getProperty("gz.budget.budgeting.yearz");
				else if(budgetType==3)
					currentYs+=ResourceFactory.getProperty("gz.budget.budgeting.tbtz");
				else if(budgetType==4)
					currentYs+=ResourceFactory.getProperty("gz.budget.budgeting.sz");
				else if(budgetType==5)
					currentYs+=ResourceFactory.getProperty("gz.budget.budgeting.wbsf");
			}
			str=ResourceFactory.getProperty("gz.budget.budget_examination.budgetTable")+":"+tab_name+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+ResourceFactory.getProperty("gz.budget.budgeting.ysunit")+":"+ysUnit+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+ResourceFactory.getProperty("gz.budget.budgeting.currentys")+":"+currentYs;
			str+="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
			str+=ResourceFactory.getProperty("column.sys.status")+":"+spflagDesc;
			
/*			rs=dao.search(" select * from "+ this.getZeSet()+" where b0110='"+b0110+"'"+" and "+this.getZeIndex()+"="+budget_id );
			while(rs.next()){
					double ze= rs.getFloat(this.getZeField());
					DecimalFormat df = new DecimalFormat();
					String style = "0.00";//定义要显示的数字的格式
					df.applyPattern(style);// 将格式应用于格式化器					
					str+="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
					str+=ResourceFactory.getProperty("gz.budget.budget_allocation.zonge")+":"+df.format(ze);
				
			}*/
		}catch(Exception e){
			e.printStackTrace(); 
		}finally{
			try{
				if(rs!=null)
					rs.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return str;
	}
	/**
	 * 获得数据及标签的fieldlist
	 * @return
	 */
	public ArrayList getFieldList(){
		//int startMonth= getStartMonth();
		int startMonth= 0;//屏蔽 wangrd 2015-8-12  任务11995
		ArrayList list=new ArrayList();
		String fieldSet = "";
		StringBuffer format=new StringBuffer();	
		format.append("###################");		
		if(this.gz_budget_tabVo!=null)
		{
			switch(this.gz_budget_tabVo.getInt("tab_type")){
				case 1://工资总额
					if(this.zeSet==null|| "".equals(this.zeSet))
						this.analyseParameter();
					fieldSet=this.getZeSet();
					break;
				case 2://名册
					fieldSet="SC01";
                    break;
				case 3://计划表
					fieldSet="SC02";
					break;
				case 4://用工明细表
					fieldSet="SC03";
					break;
			}
			String unitSpflag=this.getUnitSpflag();
			ArrayList fieldItemList = DataDictionary.getFieldList(fieldSet.toUpperCase(),Constant.USED_FIELD_SET);
			for(int i=0;i<fieldItemList.size();i++){
				FieldItem  fielditem = (FieldItem)fieldItemList.get(i);
				String desc=fielditem.getItemdesc();
				//if(desc.length()<=2)
					desc="&nbsp;&nbsp;&nbsp;"+desc+"&nbsp;&nbsp;&nbsp;";
				Field field = new Field(fielditem.getItemid(),desc);
				if("N".equalsIgnoreCase(fielditem.getItemtype()))
				{
					field.setLength(fielditem.getItemlength());
					field.setDecimalDigits(fielditem.getDecimalwidth());
					if(fielditem.getDecimalwidth()==0){
						field.setDatatype(DataType.INT);
						field.setFormat("####");
					}else{
						field.setDatatype(DataType.FLOAT);
						field.setFormat("####."+format.toString().substring(0,fielditem.getDecimalwidth()));
					}
					field.setAlign("right");
				}else if("D".equalsIgnoreCase(fielditem.getItemtype())){
					field.setLength(20);
					field.setDatatype(DataType.DATE);
					field.setFormat("yyyy.MM.dd");
					field.setAlign("right");	
				}else if("M".equalsIgnoreCase(fielditem.getItemtype())){
					field.setDatatype(DataType.CLOB);
					field.setAlign("left");		
				}else if("A".equalsIgnoreCase(fielditem.getItemtype())){
					field.setDatatype(DataType.STRING);
					if(fielditem.getCodesetid()==null|| "0".equals(fielditem.getCodesetid())|| "".equals(fielditem.getCodesetid()))
						field.setLength(fielditem.getItemlength());						
					else
						field.setLength(50);
					field.setAlign("left");
				}else{
					field.setDatatype(DataType.STRING);
					field.setLength(fielditem.getItemlength());
					field.setAlign("left");			
				}
				field.setCodesetid(fielditem.getCodesetid());
				if("sc000".equalsIgnoreCase(fielditem.getItemid())){
					field.setCodesetid(sc000CodeSet);
				}
				if("nbase".equalsIgnoreCase(fielditem.getItemid())){
					field.setCodesetid("@@");
				}
				
				if(this.gz_budget_tabVo.getInt("tab_type")==4||this.gz_budget_tabVo.getInt("tab_type")==3||this.gz_budget_tabVo.getInt("tab_type")==2){
					if("b0110".equalsIgnoreCase(fielditem.getItemid())|| "budget_id".equalsIgnoreCase(fielditem.getItemid())|| "tab_id".equalsIgnoreCase(fielditem.getItemid())||
							"itemid".equalsIgnoreCase(fielditem.getItemid())|| "a0100".equalsIgnoreCase(fielditem.getItemid())|| "sc010".equalsIgnoreCase(fielditem.getItemid())||
							"beginmonth".equalsIgnoreCase(fielditem.getItemid())|| "endmonth".equalsIgnoreCase(fielditem.getItemid())|| "a0000".equalsIgnoreCase(fielditem.getItemid()))
						field.setVisible(false);
					if("04".equals(unitSpflag)|| "07".equalsIgnoreCase(unitSpflag))
						field.setReadonly(false);
					else
						field.setReadonly(true);
					if("nbase".equalsIgnoreCase(fielditem.getItemid())|| "a0101".equalsIgnoreCase(fielditem.getItemid())
							|| "e0122".equalsIgnoreCase(fielditem.getItemid())|| "sc000".equalsIgnoreCase(fielditem.getItemid())
							|| "retmonth".equalsIgnoreCase(fielditem.getItemid())|| "a0000".equalsIgnoreCase(fielditem.getItemid())
							|| "beginmonth".equalsIgnoreCase(fielditem.getItemid())|| "endmonth".equalsIgnoreCase(fielditem.getItemid())
					)
						field.setReadonly(true);
					field.setSortable(false);
				}
				if ("val_".equalsIgnoreCase(fielditem.getItemid().substring(0, 4))){
					int month= Integer.parseInt(fielditem.getItemid().substring(4), 12);
					if (month < startMonth)
						field.setReadonly(true);
					
					
				}
				list.add(field);	
			}
		}
		return list;
	}
	/**
	 * 根据表类，取得表名
	 * @return
	 */
	public String getTableName(){
		String str="";
		if(this.gz_budget_tabVo!=null)
		{
			switch(this.gz_budget_tabVo.getInt("tab_type")){
				case 1://工资总额
					if(this.zeSet==null|| "".equals(this.zeSet))
						this.analyseParameter();
					str=this.getZeSet();
					break;
				case 2://名册
                    str="SC01";
                    break;
				case 3://用工明细表
					str="SC02";
					break;
				case 4://计划表
					str="SC03";
					break;
			}
		}
		return str;
	}
	public String downloadTemplateFactory(String flag){
		String fileName="";
		HSSFWorkbook wb = null;
		try{
			if(this.gz_budget_tabVo!=null)
			{
				if(this.gz_budget_tabVo.getInt("tab_type")==4||this.gz_budget_tabVo.getInt("tab_type")==3||this.gz_budget_tabVo.getInt("tab_type")==2)
				{
					if("0".equals(flag))//如果是普通的导出模板
						fileName=this.downloadPlanTableTemplate();
					else if("1".equals(flag)){//如果是批量导出模板
						wb = new HSSFWorkbook();
						//首先根据当前的tab_id得到预算分类 即计提，支出等
						String budgetGroup = getBudgetGroup(this.gz_budget_tabVo.getInt("tab_id"));
						//根据每个预算分类得到所有的tab_id号。
						ArrayList tabidList = getTabidList(budgetGroup);
						int tabidCount = tabidList.size();
						String randomNum = PubFunc.getStrg();
						//先创建"目录"工作表
						fileName = this.createCatelogue(wb,tabidList,budgetGroup,randomNum);
						//再创建数据工作表
						for(int j=0;j<tabidCount;j++){
							String[] tabTemp = ((String)tabidList.get(j)).split("`");
							String sql = this.getBatchSQL(j,tabTemp[0]);
							fileName=this.batchDownloadPlanTableTemplate(wb,sql,tabTemp[1],budgetGroup,randomNum);
						}
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(wb);
		}
		return fileName;
	}
	
	public String downloadPlanTableTemplate(){
		String fileName="budgeting"+this.userView.getUserName()+".xls";
		RowSet rs = null;
		HSSFWorkbook wb = null;
		try{
			ContentDAO dao = new ContentDAO(this.con);
			String sql = this.getSQL();
			ArrayList list = this.getFieldList();
			wb = new HSSFWorkbook(); // 创建新的Excel 工作簿
			HSSFSheet sheet = wb.createSheet(this.gz_budget_tabVo.getString("tab_name"));
			fileName =this.gz_budget_tabVo.getString("tab_name")+"_"+this.userView.getUserName()+"_"+PubFunc.getStrg()+".xls";
			HSSFCellStyle cellStyle=wb.createCellStyle();
			HSSFFont afont=wb.createFont();
			afont.setColor(HSSFFont.COLOR_NORMAL);
			afont.setBold(false);
			cellStyle.setFont(afont);
			cellStyle.setBorderBottom(BorderStyle.THIN);
			cellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
			cellStyle.setBorderLeft(BorderStyle.THIN);
			cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);
			cellStyle.setBorderRight(BorderStyle.THIN);
			cellStyle.setRightBorderColor(HSSFColor.BLACK.index);
			cellStyle.setBorderTop(BorderStyle.THIN);
			cellStyle.setTopBorderColor(HSSFColor.BLACK.index);
			cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			cellStyle.setWrapText(false);
		    cellStyle.setAlignment(HorizontalAlignment.LEFT);
		
			
			HSSFPatriarch patr = sheet.createDrawingPatriarch();
			HSSFComment comm = null;		
			int rowNum=0;
			HSSFRow row = sheet.createRow(rowNum);
			HSSFCell cell = null;
			int index=0;
			ArrayList headList = new ArrayList();
			row.setHeight((short)500);
			ArrayList codeFieldList = new ArrayList();
			String mcMustExpFlds="SC010,Nbase,A0100,A0101,B0110,E0122,";
			mcMustExpFlds= (mcMustExpFlds+ this.strExportFlds).toUpperCase();
			for(int i=0;i<list.size();i++){
				Field field = (Field)list.get(i);
				if("nbse".equalsIgnoreCase(field.getName())){
					field.setCodesetid("@@");
					
				}

				switch(this.gz_budget_tabVo.getInt("tab_type")){
					case 2://名册
						{
							if (mcMustExpFlds.indexOf(field.getName().toUpperCase())<0){
								continue;
							}
		                    break;
						}
					default: {						
						if("b0110".equalsIgnoreCase(field.getName())|| "budget_id".equalsIgnoreCase(field.getName())
								|| "tab_id".equalsIgnoreCase(field.getName())|| "itemid".equalsIgnoreCase(field.getName()))
							continue;
						
						}	


				}
				cell=row.createCell(index);
				if(field.getDatatype()==DataType.CLOB)
					sheet.setColumnWidth(index, (short) 8000);
				else{
					if(((field.getLength()+field.getDecimalDigits())*250)>8000)
						sheet.setColumnWidth(index, (short) 8000);
					else
			        	sheet.setColumnWidth(index, (short) ((field.getLength()+field.getDecimalDigits())*250));
				}
				cell.setCellStyle(BudgetingBo.getHSSFCellStyle(wb, -2,DataType.STRING));
				HSSFRichTextString rts=new HSSFRichTextString(field.getLabel().replaceAll("&nbsp;",""));
				cell.setCellValue(rts);
				comm = patr.createComment(new HSSFClientAnchor(0, 0, 0, 1, (short) (index + 1), 0, (short) (index + 3), 2));
				comm.setString(new HSSFRichTextString(field.getName()));
				cell.setCellComment(comm);
				headList.add(field);
				if(!"0".equals(field.getCodesetid()))
					codeFieldList.add(field.getCodesetid()+":"+index);
				index++;
			}
			rowNum++;
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			rs = dao.search(sql);
			HashMap stylesmap = new HashMap();
			while(rs.next()){
				row = sheet.createRow(rowNum);
				for(int i=0;i<headList.size();i++){
					Field field = (Field)headList.get(i);
					cell=row.createCell(i);
					String key="-1";
					if ((field.getDatatype()==DataType.INT||field.getDatatype()==DataType.FLOAT) || (field.getDecimalDigits()!=0))
						key=field.getDecimalDigits()+"";
					HSSFCellStyle style=null;
					if(stylesmap.get(key)!=null){
						style=(HSSFCellStyle)stylesmap.get(key);			
					}else{
						style=BudgetingBo.getHSSFCellStyle(wb, Integer.parseInt(key),field.getDatatype());
						stylesmap.put(key, style);		
					}
					if(i==0)
						cell.setCellStyle(cellStyle);
					else
				    	cell.setCellStyle(style);
                    if(field.getDatatype()==DataType.INT||field.getDatatype()==DataType.FLOAT){
                    	cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                    	float value=rs.getFloat(field.getName());
                    	cell.setCellValue(PubFunc.round(value+"", field.getDecimalDigits()));
                    }else if(field.getDatatype()==DataType.DATE){
                    	cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    	if(rs.getDate(field.getName())!=null)
						{
							HSSFRichTextString textstr = new HSSFRichTextString(format.format(rs.getDate(field.getName())));
							cell.setCellValue(textstr);
						}
						else
						{
							HSSFRichTextString textstr = new HSSFRichTextString("");
							cell.setCellValue(textstr);
						}
                    }else {
                    	cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    	if(!"0".equals(field.getCodesetid()))
                    	{
                    		String value=rs.getString(field.getName());
                    		HSSFRichTextString textstr = new HSSFRichTextString(AdminCode.getCodeName(field.getCodesetid(),value==null?"":value));
                    		cell.setCellValue(textstr);
                    	}else{
                    		String value=rs.getString(field.getName());
                    		HSSFRichTextString textstr = new HSSFRichTextString(value==null?"":value);
                    		cell.setCellValue(textstr);
                    	}
                    }
				}
				rowNum++;
			}
			rowNum--;
			index = 0;
			String[] lettersUpper =
			{ "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
			
			int div = 0;
			int mod = 0;			
			for (int n = 0; n < codeFieldList.size(); n++)
			{
				String codeCol = (String) codeFieldList.get(n);
				String[] temp = codeCol.split(":");
				String codesetid = temp[0];
				int codeCol1 = Integer.valueOf(temp[1]).intValue();
				StringBuffer codeBuf = new StringBuffer();
				if (!"UM".equals(codesetid) && !"UN".equals(codesetid) && !"@K".equalsIgnoreCase(codesetid))
				{
					if ("@@".equals(codesetid)){
						codeBuf.append("select * from dbname order by dbid");
					}
					else {						
						codeBuf.append("select codesetid,codeitemid,codeitemdesc from codeitem where codesetid='" + codesetid + "'");
					}
				} else
				{
					if (!"UN".equals(codesetid))
					{
						if("UM".equalsIgnoreCase(codesetid))
							codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization where ( codesetid='UM' OR codesetid='UN' ) "
									+" order by codeitemid");
						else
							codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization where codesetid='" + codesetid
								+ "' and  codeitemid not in (select parentid from organization where codesetid='" + codesetid + "')");
					}
					else if ("UN".equals(codesetid))
					{
						codeBuf.append("select count(*) from organization where codesetid='UN'");
						rs = dao.search(codeBuf.toString());
						if (rs.next())
							if (rs.getInt(1) == 1)
							{
								codeBuf.setLength(0);
								codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization where codesetid='UN'");
							} else if (rs.getInt(1) > 1)
							{
								codeBuf.setLength(0);
								codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization where codesetid='" + codesetid
										+ "' and  codeitemid not in (select parentid from organization where codesetid='" + codesetid + "')");
								codeBuf.append(" union all ");
								codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization where codesetid='UN' and  codeitemid=parentid and childid in (select codeitemid from organization where codesetid!='UN')");
							}
					}
				}
				rs = dao.search(codeBuf.toString());
				int m = 0;
				while (rs.next())
				{
					row = sheet.getRow(m + 0);
					if (row == null)
						row = sheet.createRow(m + 0);
					cell = row.createCell((short) (208 + index));
					if (!"UM".equals(codesetid) && !"UN".equals(codesetid) && !"@K".equalsIgnoreCase(codesetid)){
						if ("@@".equals(codesetid))
							cell.setCellValue(new HSSFRichTextString(rs.getString("dbname")));
						else
							cell.setCellValue(new HSSFRichTextString(rs.getString("codeitemdesc")));
					}
					else 
						cell.setCellValue(new HSSFRichTextString(rs.getString("codeitemid")+":"+rs.getString("codeitemdesc")));
					m++;
				}
				if(m==0)
					m=2;
				sheet.setColumnWidth((short) (208 + index), (short) 0);
				div = index/26;
				mod = index%26;
				String strFormula = "$" +lettersUpper[7+div]+ lettersUpper[mod] + "$1:$"+lettersUpper[7+div]+  lettersUpper[mod] + "$" + Integer.toString(m); // 表示BA列1-m行作为下拉列表来源数据
			 
				CellRangeAddressList addressList = new CellRangeAddressList(1, rowNum, codeCol1, codeCol1);
				DVConstraint dvConstraint = DVConstraint.createFormulaListConstraint(strFormula);
				HSSFDataValidation dataValidation = new HSSFDataValidation(addressList, dvConstraint);
				dataValidation.setSuppressDropDownArrow(false);
				sheet.addValidationData(dataValidation);
				index++;
			}
			FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+fileName);
			wb.write(fileOut);
			fileOut.close();	
			sheet=null;
			wb=null;
			/* 安全问题 文件下载 编制预算-下载模版 xiaoyun 2014-9-20 start */
			//fileName = fileName.replace(".xls", "#");
			/* 安全问题 文件下载 编制预算-下载模版 xiaoyun 2014-9-20 end */
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(wb);
			PubFunc.closeResource(rs);
		}
		return fileName;
	}
	//批量导出模板时还要增加一个目录的工作表
	public String createCatelogue(HSSFWorkbook wb,ArrayList tabidList,String budgetGroup,String randomNum){
		String fileName="";
		if("".equals(budgetGroup) || budgetGroup==null)
			fileName = "基本表_"+this.userView.getUserName()+"_"+randomNum+".xls";
		else
			fileName = budgetGroup+"_"+this.userView.getUserName()+"_"+randomNum+".xls";
		
		HSSFSheet sheet = wb.createSheet("目录");      
		HSSFRow row=null;   //处理行的类
		HSSFCell csCell=null; //处理单元格的类
		short n=0;
		HSSFFont font = wb.createFont();		//处理字体的类	
		font.setColor(HSSFFont.COLOR_NORMAL);
		HSSFCellStyle cellStyle= wb.createCellStyle();    //创建单元格对象
		cellStyle.setFont(font);
		cellStyle.setAlignment(HorizontalAlignment.RIGHT);//水平居中
		sheet.setColumnWidth(0, (short) 8000);
		row=sheet.createRow(n);    //创建第0行，并为它设置一些属性
		csCell=row.createCell(Short.parseShort("0"));    //创建第一个单元格
		HSSFRichTextString ss = new HSSFRichTextString("（本页内容、工作表名称及标题行自动生成，不能修改）");
		csCell.setCellValue(ss);    //向单元格中写入内容
		
		n++;
		row=sheet.createRow(n);   //创建第1行，并为它设置一些属性
		csCell =row.createCell((short)0);
		ss = new HSSFRichTextString("薪资预算批量导入模板");
		csCell.setCellValue(ss);
		
		n++;
		row=sheet.createRow(n);   //创建第2行
		csCell =row.createCell((short)0);
		ss = new HSSFRichTextString("预算表分类");
		csCell.setCellValue(ss);
		csCell =row.createCell((short)1);
		ss = new HSSFRichTextString(budgetGroup);
		csCell.setCellValue(ss);
		
		n++;
		row=sheet.createRow(n);   //创建第3行（是个空行）
		csCell =row.createCell((short)0);
		ss = new HSSFRichTextString("");
		csCell.setCellValue(ss);
		csCell =row.createCell((short)1);
		ss = new HSSFRichTextString("");
		csCell.setCellValue(ss);
		
		n++;
		row=sheet.createRow(n);   //创建第4行
		csCell =row.createCell((short)0);
		ss = new HSSFRichTextString("工作表");
		csCell.setCellValue(ss);
		csCell =row.createCell((short)1);
		ss = new HSSFRichTextString("tabid");
		csCell.setCellValue(ss);
		
		n++;
		int tabidCount = tabidList.size();
		for(int k=0;k<tabidCount;k++){
			String[] temp = ((String)tabidList.get(k)).split("`");
			row=sheet.createRow(n);   
			csCell =row.createCell((short)0);
			ss = new HSSFRichTextString(temp[1]);
			csCell.setCellValue(ss);
			csCell =row.createCell((short)1);
			csCell.setCellStyle(cellStyle);
			ss = new HSSFRichTextString(temp[0]);
			csCell.setCellValue(ss);
			n++;
		}
		
		
		//写入excel文件
		try
		{
			FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+fileName);
			wb.write(fileOut);
			fileOut.close();
			sheet=null;
			wb=null;
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return fileName;
	}
	//批量导出模板 郭峰增加
	public String batchDownloadPlanTableTemplate(HSSFWorkbook wb,String sql,String tab_name,String budgetGroup,String randomNum){

		String fileName="";
		if("".equals(budgetGroup) || budgetGroup==null)
			fileName = "基本表_"+"_"+this.userView.getUserName()+"_"+randomNum+".xls";
		else
			fileName = budgetGroup+"_"+this.userView.getUserName()+"_"+randomNum+".xls";
		RowSet rs = null;
		try{
			ContentDAO dao = new ContentDAO(this.con);
			ArrayList list = this.getFieldList();
			
			HSSFSheet sheet = wb.createSheet(tab_name);
			HSSFCellStyle cellStyle=wb.createCellStyle();
			HSSFFont afont=wb.createFont();
				afont.setColor(HSSFFont.COLOR_NORMAL);
				afont.setBold(false);
				cellStyle.setFont(afont);
				cellStyle.setBorderBottom(BorderStyle.THIN);
				cellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
				cellStyle.setBorderLeft(BorderStyle.THIN);
				cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);
				cellStyle.setBorderRight(BorderStyle.THIN);
				cellStyle.setRightBorderColor(HSSFColor.BLACK.index);
				cellStyle.setBorderTop(BorderStyle.THIN);
				cellStyle.setTopBorderColor(HSSFColor.BLACK.index);
				cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
				cellStyle.setWrapText(false);
			    cellStyle.setAlignment(HorizontalAlignment.LEFT);
			
				
				HSSFPatriarch patr = sheet.createDrawingPatriarch();
				HSSFComment comm = null;		
				int rowNum=0;
				HSSFRow row = sheet.createRow(rowNum);
				HSSFCell cell = null;
				int index=0;
				ArrayList headList = new ArrayList();
				row.setHeight((short)500);
				ArrayList codeFieldList = new ArrayList();
				for(int i=0;i<list.size();i++){
					Field field = (Field)list.get(i);
					if("b0110".equalsIgnoreCase(field.getName())|| "budget_id".equalsIgnoreCase(field.getName())|| "tab_id".equalsIgnoreCase(field.getName())|| "itemid".equalsIgnoreCase(field.getName()))
						continue;
					cell=row.createCell(index);
					if(field.getDatatype()==DataType.CLOB)
						sheet.setColumnWidth(index, (short) 8000);
					else{
						if(((field.getLength()+field.getDecimalDigits())*250)>8000)
							sheet.setColumnWidth(index, (short) 8000);
						else
				        	sheet.setColumnWidth(index, (short) ((field.getLength()+field.getDecimalDigits())*250));
					}
					cell.setCellStyle(BudgetingBo.getHSSFCellStyle(wb, -2,DataType.STRING));
					HSSFRichTextString rts=new HSSFRichTextString(field.getLabel().replaceAll("&nbsp;",""));
					cell.setCellValue(rts);
					comm = patr.createComment(new HSSFClientAnchor(0, 0, 0, 1, (short) (index + 1), 0, (short) (index + 3), 2));
					comm.setString(new HSSFRichTextString(field.getName()));
					cell.setCellComment(comm);
					headList.add(field);
					if(!"0".equals(field.getCodesetid()))
						codeFieldList.add(field.getCodesetid()+":"+index);
					index++;
				}
				rowNum++;
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				rs = dao.search(sql);
				HashMap stylesmap = new HashMap();
				while(rs.next()){
					row = sheet.createRow(rowNum);
					for(int i=0;i<headList.size();i++){
						Field field = (Field)headList.get(i);
						cell=row.createCell(i);
						String key="-1";
						if(field.getDecimalDigits()!=0)
							key=field.getDecimalDigits()+"";
						HSSFCellStyle style=null;
						if(stylesmap.get(key)!=null){
							style=(HSSFCellStyle)stylesmap.get(key);			
						}else{
							style=BudgetingBo.getHSSFCellStyle(wb, Integer.parseInt(key),field.getDatatype());
							stylesmap.put(key, style);		
						}
						if(i==0)
							cell.setCellStyle(cellStyle);
						else
					    	cell.setCellStyle(style);
	                    if(field.getDatatype()==DataType.INT||field.getDatatype()==DataType.FLOAT){
	                    	cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
	                    	float value=rs.getFloat(field.getName());
	                    	cell.setCellValue(PubFunc.round(value+"", field.getDecimalDigits()));
	                    }else if(field.getDatatype()==DataType.DATE){
	                    	cell.setCellType(HSSFCell.CELL_TYPE_STRING);
	                    	if(rs.getDate(field.getName())!=null)
							{
								HSSFRichTextString textstr = new HSSFRichTextString(format.format(rs.getDate(field.getName())));
								cell.setCellValue(textstr);
							}
							else
							{
								HSSFRichTextString textstr = new HSSFRichTextString("");
								cell.setCellValue(textstr);
							}
	                    }else {
	                    	cell.setCellType(HSSFCell.CELL_TYPE_STRING);
	                    	if(!"0".equals(field.getCodesetid()))
	                    	{
	                    		String value=rs.getString(field.getName());
	                    		HSSFRichTextString textstr = new HSSFRichTextString(AdminCode.getCodeName(field.getCodesetid(),value==null?"":value));
	                    		cell.setCellValue(textstr);
	                    	}else{
	                    		String value=rs.getString(field.getName());
	                    		HSSFRichTextString textstr = new HSSFRichTextString(value==null?"":value);
	                    		cell.setCellValue(textstr);
	                    	}
	                    }
					}
					rowNum++;
				}
				rowNum--;
				index = 0;
				String[] lettersUpper =
				{ "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
				
				int div = 0;
				int mod = 0;			
				for (int n = 0; n < codeFieldList.size(); n++)
				{
					String codeCol = (String) codeFieldList.get(n);
					String[] temp = codeCol.split(":");
					String codesetid = temp[0];
					int codeCol1 = Integer.valueOf(temp[1]).intValue();
					StringBuffer codeBuf = new StringBuffer();
					if (!"UM".equals(codesetid) && !"UN".equals(codesetid) && !"@K".equalsIgnoreCase(codesetid))
					{
						codeBuf.append("select codesetid,codeitemid,codeitemdesc from codeitem where codesetid='" + codesetid + "'");
					} else
					{
						if (!"UN".equals(codesetid))
						{
							if("UM".equalsIgnoreCase(codesetid))
								codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization where ( codesetid='UM' OR codesetid='UN' ) "
										+" order by codeitemid");
							else
								codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization where codesetid='" + codesetid
									+ "' and  codeitemid not in (select parentid from organization where codesetid='" + codesetid + "')");
						}
						else if ("UN".equals(codesetid))
						{
							codeBuf.append("select count(*) from organization where codesetid='UN'");
							rs = dao.search(codeBuf.toString());
							if (rs.next())
								if (rs.getInt(1) == 1)
								{
									codeBuf.setLength(0);
									codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization where codesetid='UN'");
								} else if (rs.getInt(1) > 1)
								{
									codeBuf.setLength(0);
									codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization where codesetid='" + codesetid
											+ "' and  codeitemid not in (select parentid from organization where codesetid='" + codesetid + "')");
									codeBuf.append(" union all ");
									codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization where codesetid='UN' and  codeitemid=parentid and childid in (select codeitemid from organization where codesetid!='UN')");
								}
						}
					}
					rs = dao.search(codeBuf.toString());
					int m = 0;
					while (rs.next())
					{
						row = sheet.getRow(m + 0);
						if (row == null)
							row = sheet.createRow(m + 0);
						cell = row.createCell((short) (208 + index));
						if (!"UM".equals(codesetid) && !"UN".equals(codesetid) && !"@K".equalsIgnoreCase(codesetid))
							cell.setCellValue(new HSSFRichTextString(rs.getString("codeitemdesc")));
						else
							cell.setCellValue(new HSSFRichTextString(rs.getString("codeitemid")+":"+rs.getString("codeitemdesc")));
						m++;
					}
					if(m==0)
						m=2;
					sheet.setColumnWidth((short) (208 + index), (short) 0);
					div = index/26;
					mod = index%26;
					String strFormula = "$" +lettersUpper[7+div]+ lettersUpper[mod] + "$1:$"+lettersUpper[7+div]+  lettersUpper[mod] + "$" + Integer.toString(m); // 表示BA列1-m行作为下拉列表来源数据
				 
					CellRangeAddressList addressList = new CellRangeAddressList(1, rowNum, codeCol1, codeCol1);
					DVConstraint dvConstraint = DVConstraint.createFormulaListConstraint(strFormula);
					HSSFDataValidation dataValidation = new HSSFDataValidation(addressList, dvConstraint);
					dataValidation.setSuppressDropDownArrow(false);
					sheet.addValidationData(dataValidation);
					index++;
				}
				FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+fileName);
				wb.write(fileOut);
				fileOut.close();	
				sheet=null;
				wb=null;
				/* 安全问题 文件下载 编制预算-下载模版 xiaoyun 2014-9-20 start */
				// fileName = fileName.replace(".xls", "#");				
				/* 安全问题 文件下载 编制预算-下载模版 xiaoyun 2014-9-20 end */
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null)
					rs.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return fileName;
	
	}
	
	public void updateChildCodeDesc(String tab_id){
		this.getIndex();
		RowSet rs = null;
		String tabName="Sc02";
		try{
			this.initVo(tab_id);
			if(this.gz_budget_tabVo!=null)	{
				ContentDAO dao = new ContentDAO(this.con);
				StringBuffer buf = new StringBuffer();
				if(this.gz_budget_tabVo.getInt("tab_type")==4){
					tabName="Sc03";
				}

	
				String codesetid=this.gz_budget_tabVo.getString("codesetid");
				buf.setLength(0);
				buf.append("update  ");
				buf.append(tabName);
				buf.append(" set itemdesc=?");
				
				buf.append(" where budget_id="+this.currentIndex);
				buf.append(" and B0110='"+this.getUnitcode()+"'");
				buf.append(" and tab_id="+tab_id);
				buf.append(" and itemid=?");
				
				ArrayList list = this.getCodeitemListHasOrder(codesetid);
				
				ArrayList valueList = null;
				for(int i=0;i<list.size();i++){
					LazyDynaBean bean = (LazyDynaBean)list.get(i);
					String codeitemid=(String)bean.get("codeitemid");
					String codeitemdesc=(String)bean.get("codeitemdesc");
					if (codeitemdesc.indexOf(" ")>-1){
						valueList = new ArrayList();
						valueList.add(codeitemdesc);
						valueList.add(codeitemid);
						dao.update(buf.toString(), valueList);	
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null){
					rs.close();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
	}
	
	//如果SC03表中没有数据，就执行插入
	public void executeInsert(String tab_id){
		RowSet rs = null;
		try{
			this.initVo(tab_id);
			if(this.gz_budget_tabVo!=null)
			{
				ContentDAO dao = new ContentDAO(this.con);
				StringBuffer buf = new StringBuffer("select count(*) from ");
				if(this.gz_budget_tabVo.getInt("tab_type")==4||this.gz_budget_tabVo.getInt("tab_type")==3)
				{
					this.getIndex();
					buf.append(" Sc03 where ");
					buf.append(" budget_id="+this.getCurrentIndex());
					buf.append(" and tab_id="+tab_id);
					buf.append(" and b0110='"+this.userView.getUserOrgId()+"'");
				}
				rs = dao.search(buf.toString());
				boolean isAdd=false;
				while(rs.next())
				{
					if(rs.getInt(1)==0)
						isAdd=true;
				}
				if(isAdd){
					if(this.gz_budget_tabVo.getInt("tab_type")==4||this.gz_budget_tabVo.getInt("tab_type")==3){
						String codesetid=this.gz_budget_tabVo.getString("codesetid");
						buf.setLength(0);
						buf.append("insert into SC03 (budget_id,tab_id,b0110,itemid,itemdesc,seq) values (?,?,?,?,?,?) ");
						ArrayList list = this.getCodeitemListHasOrder(codesetid);
						ArrayList valueList = null;
						for(int i=0;i<list.size();i++){
							LazyDynaBean bean = (LazyDynaBean)list.get(i);
							String codeitemid=(String)bean.get("codeitemid");
							String codeitemdesc=(String)bean.get("codeitemdesc");
							valueList = new ArrayList();
							valueList.add(this.getCurrentIndex());
							valueList.add(this.gz_budget_tabVo.getString("tab_id"));
							valueList.add(this.userView.getUserOrgId());
							valueList.add(codeitemid);
							valueList.add(codeitemdesc);
							valueList.add((i+1)+"");
							dao.insert(buf.toString(), valueList);
						}
					}
				}
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null){
					rs.close();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
	}
	public static short getDataFormat(int deciwidth,HSSFWorkbook workbook){
		HSSFDataFormat df = workbook.createDataFormat();
		StringBuffer buf = new StringBuffer();
		for(int i=0;i<deciwidth;i++)
		{
			buf.append("0");
		}
		String format="0."+buf.toString()+"_ ";
		return df.getFormat(format);
	}
	public static HSSFCellStyle getHSSFCellStyle(HSSFWorkbook workbook,int deciwidth,int dataType){
		HSSFCellStyle cellStyle = workbook.createCellStyle();
		HSSFFont afont = workbook.createFont();
		if(deciwidth==-2){
			afont.setFontHeightInPoints((short) 10);
			cellStyle.setFont(afont);
			cellStyle.setAlignment(HorizontalAlignment.CENTER);
			cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			cellStyle.setWrapText(true);
			cellStyle.setBorderBottom(BorderStyle.THIN);
			cellStyle.setBorderLeft(BorderStyle.THIN);
			cellStyle.setBorderRight(BorderStyle.THIN);
			cellStyle.setBorderTop(BorderStyle.THIN);
			cellStyle.setBottomBorderColor((short) 8);
			cellStyle.setLeftBorderColor((short) 8);
			cellStyle.setRightBorderColor((short) 8);
			cellStyle.setTopBorderColor((short) 8);
			cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			cellStyle.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);

		}		
		else{
			afont.setColor(HSSFFont.COLOR_NORMAL);
			afont.setBold(false);
			cellStyle.setFont(afont);
			cellStyle.setBorderBottom(BorderStyle.THIN);
			cellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
			cellStyle.setBorderLeft(BorderStyle.THIN);
			cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);
			cellStyle.setBorderRight(BorderStyle.THIN);
			cellStyle.setRightBorderColor(HSSFColor.BLACK.index);
			cellStyle.setBorderTop(BorderStyle.THIN);
			cellStyle.setTopBorderColor(HSSFColor.BLACK.index);
			cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			cellStyle.setWrapText(false);
			if(dataType==DataType.INT||dataType==DataType.FLOAT||dataType==DataType.DOUBLE){
				cellStyle.setAlignment(HorizontalAlignment.RIGHT);
				if(deciwidth>0)
			    	cellStyle.setDataFormat(BudgetingBo.getDataFormat(deciwidth, workbook));
			}else{
		    	cellStyle.setAlignment(HorizontalAlignment.CENTER);
			}
		}
		return cellStyle;
	}
	public String importDataFactory(FormFile file){
		String str="0";
		try{
			if(this.gz_budget_tabVo!=null)
			{
				if(this.gz_budget_tabVo.getInt("tab_type")==4||this.gz_budget_tabVo.getInt("tab_type")==3
						 ||this.gz_budget_tabVo.getInt("tab_type")==2)
				{
					str=importData(file);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return str;
	}
	
	public String importData(FormFile file){
		String str="0";
		InputStream is = null;
		HSSFWorkbook wk = null;
		try{
			is = file.getInputStream();
			wk = (HSSFWorkbook) WorkbookFactory.create(is);
	    	HSSFSheet sheet = wk.getSheetAt(0);
	    	String sheetName = sheet.getSheetName();
	    	if(!"目录".equals(sheetName)){//如果是单个导入
	    		HSSFRow row = sheet.getRow(0);
		    	int rowNum = sheet.getPhysicalNumberOfRows()-1;
		    	int colIndexNum=row.getPhysicalNumberOfCells();
		    	HSSFCell cell=null;
		    	ArrayList fieldItemList  = new ArrayList();
		    	String codesetWhere="'"+this.gz_budget_tabVo.getString("codesetid")+"'";
		    	String organizationWhere="";
		    	String tab_type =this.gz_budget_tabVo.getString("tab_type");
		    	String tableName="SC03";
		    	String budget_id=this.getIndex();
		    	if ("3".equals(tab_type)){//用工计划表
		    		tableName="SC02";
		    	}
		    	else if ("2".equals(tab_type)){//员工名册
		    		tableName="SC01";
		    	}	
		    	ContentDAO dao  = new ContentDAO(this.con);
		    	DbWizard dbw= new DbWizard(this.con);
		    	if ("SC01".equals(tableName)){//员工名册
		    		String mcExcptFlds="SC010,Nbase,A0100,A0101,B0110,E0122,".toUpperCase();
			    	StringBuffer updSql = new StringBuffer();	
			    	codesetWhere+=",'@@'";
			    	for(int i=0;i<colIndexNum;i++){
			    		cell=row.getCell(i);
			    		if(cell==null)	break;
			    		HSSFComment comment=cell.getCellComment();
			    		String content=comment.getString().toString();
			    		FieldItem item =DataDictionary.getFieldItem(content.toLowerCase(), tableName);
			    		if(item==null)continue;
						if("sc000".equalsIgnoreCase(item.getItemid())){
							item.setCodesetid(sc000CodeSet);
						}
			    		if(item.isCode()){
			    			if("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid()))
			    				organizationWhere+=",'"+item.getCodesetid().toUpperCase()+"'";
			    			else
			    		    	codesetWhere+=",'"+item.getCodesetid().toUpperCase()+"'";
			    		}
			    		fieldItemList.add(item);
			    	}
			    	HashMap codeItemMap = this.getCodeItemMap(codesetWhere, organizationWhere);
			    		

			    	for (int r = 1; r <= rowNum; r++) {
			    		updSql.setLength(0);
			    		updSql.append("update "+tableName+" set ");  
			    		String sc010="";
			    		String itemid="";
			    		String nBase="";
			    		String A0100="";
			    		row = sheet.getRow(r);
			    		ArrayList list = new ArrayList();	    		
			    		for(int i=0;i<fieldItemList.size();i++){
			    			String value=null;
			    			cell=row.getCell(i);
			    			FieldItem item =(FieldItem)fieldItemList.get(i);
			    			int decwidth=item.getDecimalwidth();
			    			if ("nbase".equalsIgnoreCase(item.getItemid())){
			    				item.setCodesetid("@@");
			    			}
							if("sc000".equalsIgnoreCase(item.getItemid())){
								item.setCodesetid(sc000CodeSet);
							}	    			
			    			if (cell==null){continue;}

			    			switch(cell.getCellType()){
				    			case HSSFCell.CELL_TYPE_FORMULA:
									break;
								case HSSFCell.CELL_TYPE_NUMERIC:
									value = String.valueOf( cell.getNumericCellValue());
									if (value.indexOf("E") > -1)
									{ 
										String x1 = value.substring(0, value.indexOf("E"));
										String y1 = value.substring(value.indexOf("E") + 1);
		
										value = (new BigDecimal(Math.pow(10, Integer.parseInt(y1.trim()))).multiply(new BigDecimal(x1))).toString();
									}
									value = PubFunc.round(value, decwidth);
									break;
								case HSSFCell.CELL_TYPE_STRING:
									value = cell.getRichStringCellValue().toString();
									if(value.trim().length()>0){
										if(item.isCode()){
											value=(String)codeItemMap.get(item.getCodesetid().toUpperCase()+value.trim());
										}
										if("itemdesc".equalsIgnoreCase(item.getItemid()))
										    itemid=(String)codeItemMap.get(this.gz_budget_tabVo.getString("codesetid").toUpperCase()+value.trim());
				    		    	}
									break;
								case HSSFCell.CELL_TYPE_BLANK:
									value=null;
									break;
								default:
									value = null;
			    			}
			    			if ("sc010".equalsIgnoreCase(item.getItemid())){
			    				if (value!=null)
			    					sc010=value;
			    				else	
			    					sc010="";
			    			}
			    			if ("nbase".equalsIgnoreCase(item.getItemid())){
			    				if (value!=null)
			    					nBase=value;
			    				else	
			    					nBase="";
			    			}
			    			if ("A0100".equalsIgnoreCase(item.getItemid())){
			    				if (value!=null)
			    					A0100=value;
			    				else	
			    					A0100="";
			    			}
			    			if ("".equals(value)) value =null;
			    			if (mcExcptFlds.indexOf(item.getItemid().toUpperCase()+",")<0){
				    			updSql.append(item.getItemid()+"=?,");
				    			list.add(value);
			    			}

		    			}
			    		updSql.append(" tab_id= 2");
			    		updSql.append(" where budget_id =" + budget_id);
			    		updSql.append(" and tab_id ="+this.gz_budget_tabVo.getString("tab_id") );
			    		updSql.append(" and B0110 ='" +this.userView.getUserOrgId()+ "'" );
			    		boolean bHaveThisPerson =false;
			    		if (!"".equals(sc010)){
			    			//有则更新
					    	RecordVo sc01Vo=new RecordVo("sc01");
					    	sc01Vo.setInt("sc010", Integer.parseInt(sc010));
					    	if(dao.isExistRecordVo(sc01Vo)){
					    		bHaveThisPerson=true;	
					    	}
			    		}
			    		if (bHaveThisPerson) {
			    			updSql.append(" and sc010 =" +sc010 );	
			    		}
			    		else {
			    			if (!"".equals(nBase)&& !"".equals(A0100)){
			    				updSql.append(" and nbase ='" + nBase+"'" );
			    				updSql.append(" and A0100 ='" +A0100+"'" );
			    			}
			    			else {
			    				break;
			    			}
			    		}
                      /*
		    			if(isListContainsNull(list)){
		    				str = "单个导入出错：请选择正确的模板！";
		    				return str;
		    			}
		    		*/	
			    		dao.update(updSql.toString(),list);
		    		
			    	}
		    		
		    	}
		    	else {
                    String mcExcptFlds="itemid,itemdesc,B0110,".toUpperCase();
                   // int firstMonth= getStartMonth();
                    int firstMonth= 0;//屏蔽 wangrd 2015-8-12  任务11995
                    for (int i=1; i<firstMonth; i++) {
                        mcExcptFlds =mcExcptFlds+ ("val_"+i+",").toUpperCase();               
                    }
                    StringBuffer updSql = new StringBuffer();   
                    codesetWhere+=",'@@'";
                    for(int i=0;i<colIndexNum;i++){
                        cell=row.getCell(i);
                        if(cell==null)  break;
                        HSSFComment comment=cell.getCellComment();
                        String content=comment.getString().toString();
                        FieldItem item =DataDictionary.getFieldItem(content.toLowerCase(), tableName);
                        if(item==null)continue;
                        if("sc000".equalsIgnoreCase(item.getItemid())){
                            item.setCodesetid(sc000CodeSet);
                        }
                        if(item.isCode()){
                            if("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid()))
                                organizationWhere+=",'"+item.getCodesetid().toUpperCase()+"'";
                            else
                                codesetWhere+=",'"+item.getCodesetid().toUpperCase()+"'";
                        }
                        fieldItemList.add(item);
                    }                    
                    HashMap codeItemMap = this.getCodeItemMap(codesetWhere, organizationWhere);
                    
                    String notMatchDesc="";
                    for (int r = 1; r <= rowNum; r++) {
                        String itemid="";
                        String itemdesc="";
                        row = sheet.getRow(r);
                        for(int i=0;i<fieldItemList.size();i++){
                            cell=row.getCell(i);
                            FieldItem item =(FieldItem)fieldItemList.get(i);
                            if (item==null){continue;}
                            if("itemdesc".equalsIgnoreCase(item.getItemid())){
                                int decwidth=item.getDecimalwidth();                        
                                switch(cell.getCellType()){
                                    case HSSFCell.CELL_TYPE_FORMULA:
                                        //   
                                        break;
                                    case HSSFCell.CELL_TYPE_NUMERIC:                                 
                                        break;
                                    case HSSFCell.CELL_TYPE_STRING:
                                        itemdesc = cell.getRichStringCellValue().toString();
                                        if(itemdesc.trim().length()>0){                                   
                                            itemid=(String)codeItemMap.get(this.gz_budget_tabVo.getString("codesetid").toUpperCase()+itemdesc.trim());
                                        }                             
                                }
                  
                            }
                        }
                        if ((itemid==null)|| ("".equals(itemid))){
                            if ("".equals(notMatchDesc)){
                                notMatchDesc =itemdesc;
                            } 
                            else {
                                notMatchDesc =notMatchDesc+"<br>"+itemdesc;
                            }
                        }
                    }
                    if (!"".equals(notMatchDesc)){
                        str = "请选择正确的模板！以下项目描述在当前预算表找不到："+notMatchDesc;
                        return str;                        
                    }
                    
                    for (int r = 1; r <= rowNum; r++) {
                        updSql.setLength(0);
                        updSql.append("update "+tableName+" set ");                        
                        String itemid="";                
                        row = sheet.getRow(r);
                        ArrayList list = new ArrayList();               
                        for(int i=0;i<fieldItemList.size();i++){
                            String value=null;
                            cell=row.getCell(i);
                            FieldItem item =(FieldItem)fieldItemList.get(i);
                            int decwidth=item.getDecimalwidth();
                                   
                            if (cell==null){continue;}

                            switch(cell.getCellType()){
                                case HSSFCell.CELL_TYPE_FORMULA:
                                    break;
                                case HSSFCell.CELL_TYPE_NUMERIC:
                                    value = String.valueOf( cell.getNumericCellValue());
                                    if (value.indexOf("E") > -1)
                                    { 
                                        String x1 = value.substring(0, value.indexOf("E"));
                                        String y1 = value.substring(value.indexOf("E") + 1);
        
                                        value = (new BigDecimal(Math.pow(10, Integer.parseInt(y1.trim()))).multiply(new BigDecimal(x1))).toString();
                                    }
                                    value = PubFunc.round(value, decwidth);
                                    break;
                                case HSSFCell.CELL_TYPE_STRING:
                                    value = cell.getRichStringCellValue().toString();
                                    if(value.trim().length()>0){
                                        if(item.isCode()){
                                            value=(String)codeItemMap.get(item.getCodesetid().toUpperCase()+value.trim());
                                        }
                                        if("itemdesc".equalsIgnoreCase(item.getItemid()))
                                            itemid=(String)codeItemMap.get(this.gz_budget_tabVo.getString("codesetid").toUpperCase()+value.trim());
                                    }
                                    if ("N".equalsIgnoreCase(item.getItemtype())){
                                        try{
                                            Double.parseDouble(value);
                                        }
                                        catch(Exception e){
                                            str = "Excel列["+item.getItemdesc()+"]有非数值型数据，请检查!";
                                            return str;   
                                        }                                                                        
                                    }
                                    break;
                                case HSSFCell.CELL_TYPE_BLANK:
                                    value=null;
                                    break;
                                default:
                                    value = null;
                            }
                    
                     
                            if ("".equals(value)) value =null;
                            if (mcExcptFlds.indexOf(item.getItemid().toUpperCase()+",")<0){
                                updSql.append(item.getItemid()+"=?,");
                                list.add(value);
                            }
               
                        }                       
                        updSql.append("budget_id =" + budget_id);
                        updSql.append(" where budget_id =" + budget_id);
                        updSql.append(" and tab_id ="+this.gz_budget_tabVo.getString("tab_id") );
                        updSql.append(" and B0110 ='" +this.userView.getUserOrgId()+ "'" );
                        updSql.append(" and itemid ='" +itemid+ "'" );
              
                 
                      /*
                        if(isListContainsNull(list)){
                            str = "单个导入出错：请选择正确的模板！";
                            return str;
                        }
                    */  
                        dao.update(updSql.toString(),list);
                    
                    }
 
		    	}	   	
	    	}
	    	else{//如果是批量导入
	    		//首先判断导入的模板是否匹配
	    		String tableName="SC03";
	    		String budget_id=this.getIndex();
	    		ContentDAO dao  = new ContentDAO(this.con);
	    		HSSFRow rowTemp = sheet.getRow(2);
	    		HSSFCell cellTemp=rowTemp.getCell(1);
	    		String shuttering = cellTemp.getRichStringCellValue().toString();//所导入的模板的分类

	    		//根据tab_id得到它属于哪个分类，再得到那个分类下的所有tab_id号
	    		String budgetGroup = getBudgetGroup(this.gz_budget_tabVo.getInt("tab_id"));
	    		if(!shuttering.equals(budgetGroup)){
	    			String strFrom = "";
	    			if(!"".equals(budgetGroup)){
	    				strFrom = "（"+budgetGroup+"）";
	    			}
	    			String strTo = "";
	    			if(!"".equals(shuttering)){
	    				strTo = "（"+shuttering+"）";
	    			}
	    			str="批量导入出错：左侧定位的薪资预算表分类"+strFrom+"与导入文件的分类"+strTo+"不一致，请核对！";
	    			return str;
	    		}
	    		String mcExcptFlds="itemid,itemdesc,B0110,".toUpperCase();
	    		//int firstMonth= getStartMonth();
	    		int firstMonth= 0;//屏蔽 wangrd 2015-8-12  任务11995
	            for (int i=1; i<firstMonth; i++) {
                    mcExcptFlds =mcExcptFlds+ ("val_"+i+",").toUpperCase();               
                }
				//根据每个预算分类得到所有的tab_id号和tab_name的map。
	    		HashMap tabNameIdMap = getTabNameIdMap(budgetGroup);
	    		//判断一共有几个有效的sheet
	    		int tabidCount = wk.getNumberOfSheets()-1;//得到sheet的个数(除去"目录")
				for(int j=0;j<tabidCount;j++){
					
					HSSFSheet sheet2 = wk.getSheetAt(j+1);
					String sheetName2 = sheet2.getSheetName();
					
					String tab_id = (String)tabNameIdMap.get(sheetName2);
					if(!(tab_id==null || "".equals(tab_id))){
						HSSFRow row = sheet2.getRow(0);
				    	int rowNum = sheet2.getPhysicalNumberOfRows()-1;
				    	int colIndexNum=row.getPhysicalNumberOfCells();
				    	HSSFCell cell=null;
				    	ArrayList fieldItemList  = new ArrayList();
				    	//根据tab_id重新初始化gz_budget_tabVo。
				    	this.initVo(tab_id);
				    	
				    	
				    	String codesetWhere="'"+this.gz_budget_tabVo.getString("codesetid")+"'";
				    	String organizationWhere="";
	                    StringBuffer updSql = new StringBuffer();   
	                    for(int i=0;i<colIndexNum;i++){
	                        cell=row.getCell(i);
	                        if(cell==null)  break;
	                        HSSFComment comment=cell.getCellComment();
	                        String content=comment.getString().toString();
	                        FieldItem item =DataDictionary.getFieldItem(content.toLowerCase(), tableName);
	                        if(item==null)continue;
	                        if("sc000".equalsIgnoreCase(item.getItemid())){
	                            item.setCodesetid(sc000CodeSet);
	                        }
	                        if(item.isCode()){
	                            if("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid()))
	                                organizationWhere+=",'"+item.getCodesetid().toUpperCase()+"'";
	                            else
	                                codesetWhere+=",'"+item.getCodesetid().toUpperCase()+"'";
	                        }
	                        fieldItemList.add(item);
	                    }                    
	                    HashMap codeItemMap = this.getCodeItemMap(codesetWhere, organizationWhere);
	                   	         
	                    for (int r = 1; r <= rowNum; r++) {
	                        updSql.setLength(0);
	                        updSql.append("update "+tableName+" set ");                        
	                        String itemid="";                
	                        row = sheet2.getRow(r);
	                        ArrayList list = new ArrayList();               
	                        for(int i=0;i<fieldItemList.size();i++){
	                            String value=null;
	                            cell=row.getCell(i);
	                            FieldItem item =(FieldItem)fieldItemList.get(i);
	                            int decwidth=item.getDecimalwidth();
	                                   
	                            if (cell==null){continue;}

	                            switch(cell.getCellType()){
	                                case HSSFCell.CELL_TYPE_FORMULA:
	                                    break;
	                                case HSSFCell.CELL_TYPE_NUMERIC:
	                                    value = String.valueOf( cell.getNumericCellValue());
	                                    if (value.indexOf("E") > -1)
	                                    { 
	                                        String x1 = value.substring(0, value.indexOf("E"));
	                                        String y1 = value.substring(value.indexOf("E") + 1);
	        
	                                        value = (new BigDecimal(Math.pow(10, Integer.parseInt(y1.trim()))).multiply(new BigDecimal(x1))).toString();
	                                    }
	                                    value = PubFunc.round(value, decwidth);
	                                    break;
	                                case HSSFCell.CELL_TYPE_STRING:
	                                    value = cell.getRichStringCellValue().toString();
	                                    if(value.trim().length()>0){
	                                        if(item.isCode()){
	                                            value=(String)codeItemMap.get(item.getCodesetid().toUpperCase()+value.trim());
	                                        }
	                                        if("itemdesc".equalsIgnoreCase(item.getItemid()))
	                                            itemid=(String)codeItemMap.get(this.gz_budget_tabVo.getString("codesetid").toUpperCase()+value.trim());
	                                    }
	                                    if ("N".equalsIgnoreCase(item.getItemtype())){
	                                        try{
	                                            Double.parseDouble(value);
	                                        }
	                                        catch(Exception e){
	                                            str = "Excel"+sheetName2+"列["+item.getItemdesc()+"]有非数值型数据，请检查!";
	                                            return str;   
	                                        }                                                                        
	                                    }
	                                    break;
	                                case HSSFCell.CELL_TYPE_BLANK:
	                                    value=null;
	                                    break;
	                                default:
	                                    value = null;
	                            }
	                    
	                     
	                            if ("".equals(value)) value =null;
	                            if (mcExcptFlds.indexOf(item.getItemid().toUpperCase()+",")<0){
	                                updSql.append(item.getItemid()+"=?,");
	                                list.add(value);
	                            }
	               
	                        }                       
	                        updSql.append("budget_id =" + budget_id);
	                        updSql.append(" where budget_id =" + budget_id);
	                        updSql.append(" and tab_id ="+tab_id );
	                        updSql.append(" and B0110 ='" +this.userView.getUserOrgId()+ "'" );
	                        updSql.append(" and itemid ='" +itemid+ "'" );
	              
	                 
	                      /*
	                        if(isListContainsNull(list)){
	                            str = "单个导入出错：请选择正确的模板！";
	                            return str;
	                        }
	                    */  
	                        dao.update(updSql.toString(),list);
	                    
	                    }
	 
	                }    
				}
		    		
			}
	    	
	    		
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(wk);
			PubFunc.closeIoResource(is);
		}
		return str;
	}
	//判断List中是否有NULL数据
	public boolean isListContainsNull(ArrayList list){
		boolean flag = false;//默认没有NULL数据
		int n = list.size();
		for(int i=0;i<n;i++){
			if(list.get(i)==null){
				flag = true;
				break;
			}
		}
		return flag;
	}
	public HashMap getCodeItemMap(String codeSetWhere,String organizationWhere){
		RowSet rs = null;
		HashMap map = new HashMap();
		try{
			ContentDAO dao = new ContentDAO(this.con);
			if(codeSetWhere.length()>0)
			{
		    	String sql = "select codesetid,codeitemid,codeitemdesc from codeitem where UPPER(codesetid) in ("+codeSetWhere+")";
		    	rs=dao.search(sql);
		    	while(rs.next()){
		    		map.put(rs.getString("codesetid").toUpperCase()+rs.getString("codeitemdesc").toUpperCase(), rs.getString("codeitemid"));
		    	}
			}
			if(organizationWhere.length()>0){
				String sql = "select codesetid,codeitemid,codeitemdesc from organization where UPPER(codesetid) in ("+organizationWhere.substring(1)+")";
		    	rs=dao.search(sql);
		    	while(rs.next()){
		    		map.put(rs.getString("codesetid").toUpperCase()+rs.getString("codeitemdesc").toUpperCase(), rs.getString("codeitemid"));
		    	}
			}
			if (codeSetWhere.indexOf("@@")>-1){
		    	String sql = "select * from DBName order by dbid";
		    	rs=dao.search(sql);
		    	while(rs.next()){
		    		map.put("@@"+rs.getString("dbname").toUpperCase(), rs.getString("pre"));
		    	}
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null)
					rs.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return map;
	}
	public String reportData(){
		String info="0";
		RowSet rs = null;
		try{
			StringBuffer buf = new StringBuffer();
			buf.append("select "+this.getZeSpFlag()+" from "+this.getZeSet()+" where b0110='"+this.getUnitcode()+"'");
			buf.append(" and "+this.getZeIndex()+"=");
			buf.append(" (select max(budget_id) budget_id from gz_budget_index where (budgetType=1 or budgetType=2 or budgetType=3))");
			ContentDAO dao = new ContentDAO(this.con);
			rs=dao.search(buf.toString());
			while(rs.next()){
				String spflag=rs.getString(1);
				if("04".equals(spflag)|| "07".equalsIgnoreCase(spflag)){
					buf.setLength(0);
					buf.append(" update "+this.getZeSet()+" set "+this.getZeSpFlag()+"='02' where b0110='"+this.getUnitcode()+"' ");
					buf.append(" and "+this.zeIndex+"=(");
					buf.append("select max(budget_id) budget_id from gz_budget_index where (budgetType=1 or budgetType=2 or budgetType=3)");
					buf.append(")");
					dao.update(buf.toString());
				}else{
					info="1";
				}
			}
			
			
		}catch(Exception e){
			info="2";
			e.printStackTrace();
		}
		return info;
	}
	public boolean CanCalc(){
		boolean b= false;
		RowSet rs = null;
		try{
			StringBuffer buf = new StringBuffer();
			buf.append("select "+this.getZeSpFlag()+" from "+this.getZeSet()+" where b0110='"+this.getUnitcode()+"'");
			buf.append(" and "+this.getZeIndex()+"=");
			buf.append(" (select max(budget_id) budget_id from gz_budget_index where (budgetType=1 or budgetType=2 or budgetType=3))");
			ContentDAO dao = new ContentDAO(this.con);
			rs=dao.search(buf.toString());
			while(rs.next()){
				String spflag=rs.getString(1);
				if("04".equals(spflag)|| "07".equalsIgnoreCase(spflag)){
					 b= true;	
				}
			}
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return b;
	}
	public String getZeSet() {
		return zeSet;
	}
	public void setZeSet(String zeSet) {
		this.zeSet = zeSet;
	}
	public String getZeIndex() {
		return zeIndex;
	}
	public void setZeIndex(String zeIndex) {
		this.zeIndex = zeIndex;
	}
	public String getZeField() {
		return zeField;
	}
	public void setZeField(String zeField) {
		this.zeField = zeField;
	}
	public String getParamSet() {
		return paramSet;
	}
	public void setParamSet(String paramSet) {
		this.paramSet = paramSet;
	}
	public String getParamIndex() {
		return paramIndex;
	}
	public void setParamIndex(String paramIndex) {
		this.paramIndex = paramIndex;
	}
	public String getParamMonthField() {
		return paramMonthField;
	}
	public void setParamMonthField(String paramMonthField) {
		this.paramMonthField = paramMonthField;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String getZeSpFlag() {
		return zeSpFlag;
	}
	public void setZeSpFlag(String zeSpFlag) {
		this.zeSpFlag = zeSpFlag;
	}
	public RecordVo getGz_budget_tabVo(){
		return this.gz_budget_tabVo;
	}
	public String getCurrentIndex() {
		return currentIndex;
	}
	public void setCurrentIndex(String currentIndex) {
		this.currentIndex = currentIndex;
	}

	public String getCanImports() {
		return canImports;
	}
	public void setCanImports(String canImports) {
		this.canImports = canImports;
	}
	
//	public ArrayList getUnitcodeList(){
//		String unitcode = this.userView.getUserOrgId();
//		ArrayList list = new ArrayList();
//		for(int n=unitcode.length();n>=0;n--){
//			String str=unitcode.substring(0, n);
//			list.add(str);
//		}
//		return list;
//	}
//	/**
//	 * 获得当前用户的（最近的）预算单位编码
//	 * @return
//	 */
//	
//	public String getB0110(){
//		String str = "";
//		ArrayList unitcodeList=this.getUnitcodeList();
//		try{
//			ContentDAO dao = new ContentDAO(this.con);
//			ResultSet rs = null;
//			StringBuffer sb = new StringBuffer();
//			int n = unitcodeList.size();
//			for(int i=0;i<n;i++){
//				sb.delete(0, sb.length());
//				sb.append("select * from "+this.getZeSet()+" where b0110='"+unitcodeList.get(i)+"'");
//				rs = dao.search(sb.toString());
//				if(rs.next()){
//					str = (String)unitcodeList.get(i);
//					break;
//				}
//			}
//			if(rs!=null)
//				rs.close();
//			
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		return str;
//	}
	/**
	 * 获得当前用户的（最近的）预算单位编码，非顶级单位
	 */
	public String getUnitcode(){
		String str = "";
		StringBuffer sb=new StringBuffer();
		ResultSet rs=null;
		try{
			ContentDAO dao = new ContentDAO(this.con);
			sb.append("select codeitemid from organization ,( select codeitemid AS elseitemid from organization where codeitemid in('");
			sb.append(this.userView.getUserOrgId()+"'))A where codeitemid="
			        + Sql_switcher.left("a.elseitemid",Sql_switcher.length("codeitemid"))
			        +"  and codeitemid in(select b0110 from "+this.getZeSet()+" ) " +
					"and codeitemid <>parentid order by codeitemid desc");
			rs=dao.search(sb.toString());
			if (rs.next()){
				str=rs.getString("codeitemid");
			}
			}catch(Exception e){
			e.printStackTrace();
		}
		
		return str;
	}
	/**
	 * 是否设置了总额、参数子集
	 */
	public boolean HaveZeParameter(){
		boolean b=true;
		if ( (this.getZeSet()==null) || ("".equals(this.getZeSet()))
		       || (this.getZeField()==null)   || ("".equals(this.getZeField()))
		       || (this.getZeIndex()==null) || ("".equals(this.getZeIndex())) ){
			
			b=false;
		}
		if ( (this.getParamSet()==null) ||("".equals(this.getParamSet()))
		        || (this.getParamIndex()==null)   || ("".equals(this.getParamIndex()))
		        || (this.getParamMonthField()==null) || ("".equals(this.getParamMonthField())) ){
			
			b=false;
		}
		try{

		}catch(Exception e){
			e.printStackTrace();
		}
		
		return b;
	}
	public void setB0110(String b0110) {
		this.b0110 = b0110;
	}
	public String getB0110() {
		return b0110;
	}
	public String getStrExportFlds() {
		return strExportFlds;
	}
	public void setStrExportFlds(String strExportFlds) {
		this.strExportFlds = strExportFlds;
	}
}
