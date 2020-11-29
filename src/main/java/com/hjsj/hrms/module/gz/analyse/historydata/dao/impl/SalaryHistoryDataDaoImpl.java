package com.hjsj.hrms.module.gz.analyse.historydata.dao.impl;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryPropertyBo;
import com.hjsj.hrms.businessobject.gz.TaxMxBo;
import com.hjsj.hrms.businessobject.gz.gz_analyse.GzAnalyseBo;
import com.hjsj.hrms.businessobject.hire.zp_options.stat.positionstat.PositionStatBo;
import com.hjsj.hrms.module.gz.analyse.historydata.dao.SalaryHistoryDataDao;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;
import org.apache.log4j.Category;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.util.*;

/**
 * @Title SalaryHistoryDataDaoImpl
 * @Description 薪资历史数据数据库操作实现类
 * @Company hjsj
 * @Author wangbs
 * @Date 2020/1/13
 * @Version 1.0.0
 */
public class SalaryHistoryDataDaoImpl implements SalaryHistoryDataDao {
    /**数据库底层操作类**/
    private ContentDAO dao;
    /** 日志对象 */
    private static Category log = Category.getInstance(SalaryHistoryDataDaoImpl.class.getName());
    private Connection conn;
    /**
     * 薪资历史数据数据库操作实现类构造方法
     * @author wangbs
     * @param conn 数据库连接
     * @date 2020/1/13 15:35
     */
    public SalaryHistoryDataDaoImpl(Connection conn){
        this.dao = new ContentDAO(conn);
        this.conn=conn;
    }

    @Override
    public List<RecordVo> listSalaryTemplate(List salaryIdList, int pageIndex, int pageSize) throws GeneralException {
        return null;
    }

    @Override
    public void deleteSalaryHistoryData(String type,String startDate,String endDate,String salaryId,UserView userView) throws GeneralException {
    	try
		{
    		String where = getWhereSQL(type,startDate, endDate,salaryId, userView, 1); 
			String dSQL="delete from salaryarchive where "+where;
			dao.delete(dSQL, new ArrayList());
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new GeneralException("删除失败！");
		}
    }

    @Override
    public List<DynaBean> getSwitchSalaryTemplateList(StringBuffer sql ,List<String> sqlList) throws GeneralException {
        List<DynaBean> beanList = new ArrayList<DynaBean>();
        RowSet rowSet = null;

        try {
            rowSet = this.dao.search(sql.toString(),sqlList);
            beanList = this.dao.getDynaBeanList(rowSet);
            for (DynaBean bean : beanList){
                String salaryid = (String) bean.get("salaryid");
                salaryid = PubFunc.encrypt(salaryid);
                bean.set("salaryidjiami",salaryid);
            }
        } catch (Exception e){
            e.printStackTrace();
            log.error("/module/gz/gz_resource_zh_CN.js ---> gz.historyTemplate.msg.getSalaryTemplateError",e);
            throw new GeneralException("gz.historyTemplate.msg.getSalaryTemplateError");
        }finally {
			PubFunc.closeDbObj(rowSet);
		}
        return beanList;
    }
 	

	@Override
	public void revertSalaryHistoryData(String type, String startDate, String endDate, String salaryId, UserView userView) throws GeneralException{
		RowSet rowSet = null;
		try {
			String gditem = "NBASE,A0100,A00Z0,A00Z1,SALARYID,A00Z2,A00Z3,A01Z0,A0000,B0110,E0122,A0101,USERFLAG,SP_FLAG,CURR_USER,APPUSER";
			StringBuffer columns = new StringBuffer();
			columns.append(gditem);
			DbWizard dbw=new DbWizard(this.conn);
			if(dbw.isExistField("salaryhistory","appprocess",false))
			{
				columns.append(",appprocess");
			}
			String salaryItem = this.getSalaryItem(salaryId,gditem).toUpperCase();
			String asql = "select * from salaryarchive where 1=2";
			rowSet = dao.search(asql);
			ResultSetMetaData data=rowSet.getMetaData();
			for(int i=1;i<=data.getColumnCount();i++)
			{
			    String columnName=data.getColumnName(i).toUpperCase();
			    if(salaryItem.indexOf(columnName)!=-1)
			    {
			    	columns.append(","+columnName);
			    }
			}
			if(this.isHaveAdd_flag()){
				columns.append(",ADD_FLAG");
			}
			StringBuffer sql = new StringBuffer("");
			sql.append(" insert into salaryhistory ("+columns.toString()+") ");
			sql.append(" select "+columns+" from salaryarchive ");
			sql.append(" where ");
			String where = this.getWhereSQL(type,startDate, endDate,salaryId, userView,1);
			sql.append(where);
			dao.update(sql.toString());
			String dSQL="delete from salaryarchive where "+where
			+" and exists (select null from salaryhistory sh where "
			+"  sh.a00z0=salaryarchive.a00z0 and sh.a00z1=salaryarchive.a00z1 and sh.a0100=salaryarchive.a0100 and sh.nbase=salaryarchive.nbase and sh.salaryid=salaryarchive.salaryid  )";
			dao.delete(dSQL, new ArrayList());
		}catch(Exception e) {
			e.printStackTrace();
			throw new GeneralException("gz.historyData.msg.revertDataError");
		}finally {
			PubFunc.closeDbObj(rowSet);
		}
		
	}


	@Override
	public void archiveSalaryHistoryData(String type, String startDate, String endDate,String salaryId,UserView userView) throws GeneralException{
		try {
			String gditem = "NBASE,A0100,A00Z0,A00Z1,SALARYID,A00Z2,A00Z3,A01Z0,A0000,B0110,E0122,A0101,USERFLAG,SP_FLAG,CURR_USER,APPUSER";
			StringBuffer columns = new StringBuffer();
			String salaryItem = this.getSalaryItem(salaryId,gditem);
			columns.append(gditem);
			DbWizard dbw=new DbWizard(this.conn);
			if(dbw.isExistField("salaryarchive","appprocess",false))
			{
				columns.append(",appprocess");
			}
			if(this.isHaveAdd_flag())
			{
				columns.append(",ADD_FLAG");
			}
			if(salaryItem.length()>0)
			{
				columns.append(","+salaryItem);
			}
			StringBuffer sql = new StringBuffer("");
			String where = this.getWhereSQL(type,startDate, endDate,salaryId, userView,1);
			sql.append("delete from salaryarchive where exists (select null from ");
			sql.append(" (select * from salaryhistory ");
			sql.append(" where ");
			sql.append(where);
			sql.append(" and sp_flag='06') ");
		    sql.append(" salaryhistory where salaryhistory.a0100=salaryarchive.a0100 ");
		    sql.append(" and UPPER(salaryhistory.nbase)=UPPER(salaryarchive.nbase) and salaryhistory.a00z0=salaryarchive.a00z0 ");
		    sql.append(" and salaryhistory.a00z1=salaryarchive.a00z1 and salaryhistory.salaryid=salaryarchive.salaryid) and "+where+"");
            dao.delete(sql.toString(), new ArrayList());
            sql.setLength(0);
			sql.append(" insert into salaryarchive ("+columns.toString()+") ");
			sql.append(" select "+columns+" from salaryhistory ");
			sql.append(" where ");
			sql.append(where);
			sql.append(" and sp_flag='06' ");
			dao.update(sql.toString());
			String dSQL="delete from salaryhistory where "+where+" and sp_flag='06'"
			+" and exists (select null from salaryarchive sa where "
			+"  sa.a00z0=salaryhistory.a00z0 and sa.a00z1=salaryhistory.a00z1 and sa.a0100=salaryhistory.a0100 and sa.nbase=salaryhistory.nbase and sa.salaryid=salaryhistory.salaryid  )";
			dao.delete(dSQL, new ArrayList());
			
		}catch(Exception e) {
			e.printStackTrace();
			throw new GeneralException("归档失败！");
		}
		
		
	}

	@Override
	public String getSalaryItem(String salaryId,String gditem) throws GeneralException{
		StringBuffer buf = new StringBuffer("");
		RowSet rs = null;
		try{
			String id=salaryId.replaceAll("`","','");
			String sql = "select distinct itemid from salaryset where salaryid in ('"+id+"')";
			sql+=" and UPPER(itemid) not in ('"+gditem.replaceAll(",", "','")+"')";
			rs = dao.search(sql);
			int i=0;
			while(rs.next())
			{
				if(i!=0){
					buf.append(",");
				}
				buf.append(rs.getString("itemid"));
				i++;
			}
		}catch(Exception e){
			e.printStackTrace();
			throw new GeneralException("获取薪资类别定义项失败！");
		}finally {
			PubFunc.closeDbObj(rs);
		}
		return buf.toString();
	}

	@Override
	public boolean isHaveAdd_flag() throws GeneralException{
		boolean flag = false;
		RowSet rs = null;
		try{
			rs = dao.search("select * from salaryhistory where 1=2");
			ResultSetMetaData meta=rs.getMetaData();
			for(int j=1;j<=meta.getColumnCount();j++)
			{
				String name=meta.getColumnName(j);
				if("add_flag".equalsIgnoreCase(name))
				{
					flag=true;
					break;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			throw new GeneralException("判断add_flag指标失败！");
		}finally {
			PubFunc.closeDbObj(rs);
		}
		return flag;
	}


	@Override
	public void deleteTaxData(String type,String startDate,String endDate,String salaryId,UserView userView)  throws GeneralException{
		try {
			String where = this.getWhereSQL(type,startDate, endDate,salaryId, userView,2);
			String dSQL="delete from taxarchive where "+where;
			dao.delete(dSQL, new ArrayList());
		}catch(Exception e) {
			e.printStackTrace();
			throw new GeneralException("删除个税明细表失败！");
		}
	}

	

	@Override
	public void archiveTaxData(String tableName,String type,String startDate,String endDate,String salaryId,UserView userView,int flag)  throws GeneralException{
		RowSet rowSet = null;
		try {
			StringBuffer sql = new StringBuffer("");
			sql.append(" insert into "+tableName+" (nbase,a0100,a00z0,a00z1,salaryid,tax_max_id) ");
			sql.append(" select nbase,a0100,a00z0,a00z1,salaryid,tax_max_id from gz_tax_mx ");
			sql.append(" where ");
			String where = this.getWhereSQL(type,startDate, endDate,salaryId, userView,2);
			sql.append(where);
			dao.update(sql.toString()); 
			setTmp2PrimaryKey(tableName);
			
			StringBuffer in_columns = new StringBuffer("");
			StringBuffer select_columns=new StringBuffer("");
			
			int num=0;
			rowSet = dao.search("select max(tax_max_id) from taxarchive");
			if(rowSet.next())
			{
					num=rowSet.getInt(1);
			}
			
			
			String asql = "select * from taxarchive where 1=2";
			rowSet = dao.search(asql);
			ResultSetMetaData data=rowSet.getMetaData();
			for(int i=1;i<=data.getColumnCount();i++)
			{
			    String columnName=data.getColumnName(i).toLowerCase();
			     
			    in_columns.append(","+columnName);
				if ("tax_max_id".equalsIgnoreCase(columnName)) {
					select_columns.append("," + tableName + ".id+" + num);
				} else {
					select_columns.append(",gz_tax_mx." + columnName);
				}
			    
			}
			 
			sql.setLength(0);
			sql.append(" insert into taxarchive ("+in_columns.substring(1)+") ");
			sql.append(" select "+select_columns.substring(1)+" from (select * from gz_tax_mx where "+where+" and flag=1) gz_tax_mx,"+tableName+" where  ");
			sql.append(" gz_tax_mx.tax_max_id="+tableName+".tax_max_id and gz_tax_mx.salaryid="+tableName+".salaryid   and gz_tax_mx.A0100="+tableName+".A0100  and gz_tax_mx.A00Z1="+tableName+".A00Z1   and gz_tax_mx.A00Z0="+tableName+".A00Z0   and gz_tax_mx.NBASE="+tableName+".NBASE "); 
			dao.update(sql.toString());
			String dSQL="delete from gz_tax_mx where "+where+" and flag=1"
			+" and exists (select null from taxarchive ta where "
			+"  ta.a00z0=gz_tax_mx.a00z0 and ta.a00z1=gz_tax_mx.a00z1 and ta.a0100=gz_tax_mx.a0100 and ta.nbase=gz_tax_mx.nbase and ta.salaryid=gz_tax_mx.salaryid  )";
			dao.delete(dSQL, new ArrayList());
			dao.update("delete from "+tableName);
		}catch(Exception e) {
			e.printStackTrace();
			throw new GeneralException("归档个税明细表失败！");
		}finally {
			PubFunc.closeDbObj(rowSet);
		}
		
	}

	
	@Override
	public void revertTaxData(String tableName,String type,String startDate,String endDate,String salaryId,UserView userView,int flag)  throws GeneralException{
		RowSet rowSet = null;
		try {
			StringBuffer sql = new StringBuffer("");
			sql.append(" insert into "+tableName+" (nbase,a0100,a00z0,a00z1,salaryid,tax_max_id) ");
			sql.append(" select nbase,a0100,a00z0,a00z1,salaryid,tax_max_id from taxarchive ");
			sql.append(" where ");
			String where = this.getWhereSQL(type,startDate, endDate,salaryId, userView,2);
			sql.append(where);
			dao.update(sql.toString()); 
			setTmp2PrimaryKey(tableName);
			
			
			
			HashMap archive_map=new HashMap();
			String asql = "select * from taxarchive where 1=2";
			rowSet = dao.search(asql);
			ResultSetMetaData data=rowSet.getMetaData();
			for(int i=1;i<=data.getColumnCount();i++)
			{
			    String columnName=data.getColumnName(i).toLowerCase();
			    archive_map.put(columnName,"1");
			}
			
			
			StringBuffer in_columns = new StringBuffer("");
			StringBuffer select_columns=new StringBuffer("");
			int num=0;
			rowSet=dao.search("select max(tax_max_id) from gz_tax_mx");
			if(rowSet.next())
			{
					num=rowSet.getInt(1);
			}
			
			 
			asql = "select * from gz_tax_mx where 1=2";
			rowSet = dao.search(asql);
			data=rowSet.getMetaData();
			for(int i=1;i<=data.getColumnCount();i++)
			{
			    String columnName=data.getColumnName(i).toLowerCase();
				if (archive_map.get(columnName) == null) {
					continue;
				}
			    
			    in_columns.append(","+columnName);
				if ("tax_max_id".equalsIgnoreCase(columnName)) {
					select_columns.append("," + tableName + ".id+" + num);
				} else {
					select_columns.append(",taxarchive." + columnName);
				}
			    
			}
			 
			sql.setLength(0);
			
			String dSQL="delete from gz_tax_mx where  exists (select null from taxarchive where "+where+"  and gz_tax_mx.salaryid=taxarchive.salaryid ";
			dSQL+=" and gz_tax_mx.a0100=taxarchive.a0100  and gz_tax_mx.nbase=taxarchive.nbase  and gz_tax_mx.a00z0=taxarchive.a00z0   and gz_tax_mx.a00z1=taxarchive.a00z1 )";
			dao.delete(dSQL, new ArrayList());
			
			
			sql.append(" insert into gz_tax_mx ("+in_columns.substring(1)+") ");
			sql.append(" select "+select_columns.substring(1)+" from (select * from taxarchive where "+where+") taxarchive,"+tableName+" where  ");
			sql.append(" taxarchive.tax_max_id="+tableName+".tax_max_id and  taxarchive.salaryid="+tableName+".salaryid   and taxarchive.A0100="+tableName+".A0100  and taxarchive.A00Z1="+tableName+".A00Z1   and taxarchive.A00Z0="+tableName+".A00Z0   and taxarchive.NBASE="+tableName+".NBASE "); 
			dao.update(sql.toString());
			dSQL="delete from taxarchive where "+where
			+" and exists (select null from gz_tax_mx gtm where "
			+"  gtm.a00z0=taxarchive.a00z0 and gtm.a00z1=taxarchive.a00z1 and gtm.a0100=taxarchive.a0100 and gtm.nbase=taxarchive.nbase and gtm.salaryid=taxarchive.salaryid  )";
			dao.delete(dSQL, new ArrayList());
			dao.update("delete from "+tableName);
		}catch(Exception e) {
			e.printStackTrace();
			throw new GeneralException("还原个税明细表失败！");
		}finally {
			PubFunc.closeDbObj(rowSet);
		}
		
	}

	@Override
	public boolean repeatData(String type,String startDate,String endDate,String salaryId,UserView userView,StringBuffer sql) throws GeneralException{
		RowSet rs = null;
		try {
			sql.append("  select a.A00Z0,a.A00Z1,a.A00Z2,a.A00Z3,a.A0101 from (");
			sql.append(" select A00Z0,A00Z1,A00Z2,A00Z3,A0101,A0100,NBASE,SALARYID from salaryhistory ");
			sql.append(" where ");
			String where = getWhereSQL(type,startDate, endDate, salaryId,userView,1);
			sql.append(where);
			sql.append(")a,salaryarchive sa where a.A00Z0=sa.A00Z0 and a.A00Z1=sa.A00Z1 and a.A00Z2=sa.A00Z2  and a.A00Z3=sa.A00Z3 and  a.A0100=sa.A0100 and  a.NBASE=sa.NBASE and a.SALARYID = sa.SALARYID");
			rs = dao.search(sql.toString());
			if(rs.next()){
				return true;
			}
		}catch(Exception e) {
			e.printStackTrace();
			throw new GeneralException("查询重复数据失败！");
		}finally {
			PubFunc.closeDbObj(rs);
		}
		return false;
	}

	
	/**
	 * 设置临时表的主键字段，自动增长类型
	 */
	private void setTmp2PrimaryKey(String tablename)throws GeneralException
	{
		StringBuffer buf=new StringBuffer();
		DbWizard dbw=new DbWizard(this.conn);			
		try
		{
			 
				switch(Sql_switcher.searchDbServer())
				{
				case 1://MSSQL
					buf.append("alter table ");
					buf.append(tablename);
					buf.append(" add id int identity(1,1)");
					dbw.execute(buf.toString());
					break;
				case 2://ORACLE
				case 3://DB2
					

					if(isSequence(Sql_switcher.searchDbServer(),tablename+"_seqid"))
					{
						 dbw.execute("drop sequence "+tablename+"_seqid");	
					}
					buf.append("create sequence "+tablename+"_seqid increment by 1 start with 1");
					dbw.execute(buf.toString());
					
					buf.setLength(0);
					buf.append("update ");
					buf.append(tablename);
					buf.append(" set id=");
					buf.append(Sql_switcher.sql_NextVal(tablename+"_seqid"));
					dbw.execute(buf.toString());
					buf.setLength(0);				
					buf.append("drop sequence "+tablename+"_seqid");
					dbw.execute(buf.toString());
					break;
				}//switch end.
		 
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw new GeneralException("设置临时表字段失败！");				
		}		
	}
	
	private boolean isSequence(int dbflag,String name) throws GeneralException
	{
		boolean flag=false;
		RowSet rowSet = null;
		try
		{
			if(dbflag==Constant.ORACEL){
				rowSet = dao.search("select sequence_name from user_sequences where lower(sequence_name)='" + name + "'");
				if (rowSet.next()) {
					flag = true;
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new GeneralException("判断Sequence失败！");
		}finally {
			PubFunc.closeDbObj(rowSet);
		}
		return flag;
	}
	
	
	@Override
	public String getWhereSQL(String type, String startDate, String endDate,String salaryId, UserView userView, int flag) throws GeneralException{
		StringBuffer where = new StringBuffer();
		try
		{
	    	PositionStatBo psb = new PositionStatBo(this.conn);
	    	if("1".equals(type))
	    	{
	    		String item="a00z2";//发放日期
	    		if(flag==2)
	    		{
	    			item="(case when a00z2 is not null then a00z2 else a00z0 end )";
	    			
	    		}
	    	    where.append(" ("+psb.getDateSql(">=", item, startDate)+")");
	    	    where.append(" and ");
	     	    where.append(" ("+psb.getDateSql("<=", item, endDate)+")");
	     	    where.append(" and ");
	    	}

	    	if(flag==1){//1:历史数据归档  2：个税明细归档

		    		GzAnalyseBo bo = new GzAnalyseBo(this.conn,userView);
		    		String b_units=userView.getUnitIdByBusiOutofPriv("1");// 1:工资发放  2:工资总额  3:所得税
		    		String sql = bo.getPrivSQL("", "", salaryId.replaceAll("`", ","),b_units);
		    		where.append(sql);
	    	}else{
				
				String[] temp = salaryId.split("`");
		     	HashMap map = new HashMap();
				for (int j= 0; j < temp.length; j++){
					SalaryPropertyBo bo=new SalaryPropertyBo(this.conn,temp[j],1,userView);
			        TaxMxBo tmb = new TaxMxBo(this.conn);
			        String ls_dept=tmb.getDeptID();
			        String lsDept="e0122";
			        if("true".equalsIgnoreCase(ls_dept))
			        {
			        	lsDept=bo.getCtrlparam().getValue(SalaryCtrlParamBo.LS_DEPT);
			        	lsDept=lsDept==null||lsDept.length()==0?"e0122":"deptid";
			        }					
					String item = (String) map.get(lsDept);
			    	if(item!=null&&item.length()>0){
			    		map.put(lsDept, item+",'"+temp[j]+"'");
			    	}else{
			    		map.put(lsDept, "'"+temp[j]+"'");
			    	}	
				}			
				String b_units=userView.getUnitIdByBusiOutofPriv("1");// 1:工资发放  2:工资总额  3:所得税
				if(b_units!=null&&b_units.length()>0&&!"UN".equalsIgnoreCase(b_units)&&!"UN`".equalsIgnoreCase(b_units)) //模块操作单位
				{
					String[] unitarr =b_units.split("`");
					Iterator iter = map.entrySet().iterator();
					while (iter.hasNext()) {
						Map.Entry entry = (Map.Entry) iter.next();
						Object key = entry.getKey();
						Object val = entry.getValue();
						where.append("((");
						for(int i=0;i<unitarr.length;i++)
						{
		    				String codeid=unitarr[i];
							if (codeid == null || "".equals(codeid)) {
								continue;
							}
			    			if(codeid!=null&&codeid.trim().length()>2)
		    				{
			    				String privCode = codeid.substring(0,2);
			    				String privCodeValue = codeid.substring(2);							  
								if(privCode!=null&&!"".equals(privCode))
								{		
									where.append(" ( case");
									where.append("  when nullif("+key+",'') is not null then "+key+" ");
									where.append("  when (nullif("+key+",'') is null) and nullif(e0122,'') is not null then e0122 ");
									where.append(" else b0110 end ");
									where.append(" like '"+privCodeValue+"%' ");
									where.append(") or");
								}
		    				}
						}
						if(userView.isSuper_admin()|| "1".equals(userView.getGroupId())){
							where.append(" 1=1 ");
							where.append(" ) and salaryid in ("+val.toString()+")) or");
						}else{
							String _str = where.toString();
							where.setLength(0);
							where.append(_str.substring(0, _str.length()-3));
							where.append(") and salaryid in ("+val.toString()+")) or");
						}	
					}
					String str = where.toString();
					where.setLength(0);
					where.append("("+str.substring(0, str.length()-3)+")");
				}else if("UN`".equalsIgnoreCase(b_units)){
					where.append( "  1=1 ");
					where.append(" and (");
					for (int i = 0; i < temp.length; i++){
						if(i==0){
							where.append("  (salaryid = ");
						}else{
							where.append(" or (salaryid = ");
						}
						
						where.append(temp[i]);
						where.append(")");
					}
					where.append(")");
				}
				else
				{
					if (userView.isSuper_admin() || "1".equals(userView.getGroupId())) {  //201602219 dengcan
						where.append(" 1=1 ");
					} else {
						where.append("  1=2 ");
					}
				}			
	    	}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new GeneralException("获取where语句失败！");
		}
		
		
		return where.toString();
	}
	

}
