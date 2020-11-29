package com.hjsj.hrms.module.gz.salaryaccounting.importmen.businessobject;

import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryAccountBo;
import com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import net.sf.hibernate.collection.Map;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;


/**
 *<p>Title:人员引入业务类</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2015-8-14</p> 
 *@author dengc
 *@version 7.x
 */
public class ImportMenInfoBo {
	SalaryAccountBo salaryAccountBo=null;
	/**登录用户*/
	private UserView userview;
	private int salaryid;//薪资类别id
	private Connection conn=null; 
	
	public ImportMenInfoBo(Connection conn,UserView userview,int salaryid)
	{
		this.conn=conn;
		this.salaryid=salaryid;
		this.userview=userview;
		salaryAccountBo=new SalaryAccountBo(conn,userview,salaryid);
		
	}

	/**
	 * 引入手工选择人员
	 * @param right_fields : right_fields[i]=a0100/Usr  (加密)
	 * @param ff_date 发放日期
	 * @param count 发放次数
	 * @author dengcan
	 */
	public void importHandSelectedMen(String ids,String ff_date,String count)throws GeneralException
	{
		/**临时表名*/
		String tablename="t#"+this.userview.getUserName()+"_gz_Ins";
		String gz_tablename=this.salaryAccountBo.getSalaryTemplateBo().getGz_tablename();
		try
		{
			if(StringUtils.isBlank(ids))
				return;
			String[] idsArray = ids.split("'");
			ContentDAO dao=new ContentDAO(this.conn);
			//生成临时表
			this.salaryAccountBo.getSalaryTableStructBo().createInsDecTableStruct(tablename,new ArrayList());
			//根据工资类别id得到类别下面的所有项目列表
			ArrayList itemList=this.salaryAccountBo.getSalaryTemplateBo().getSalaryItemList("",""+salaryid,1);
			DbWizard dbw=new DbWizard(this.conn);
			/** 用于判断是否在临时表中存在 */
			Table table=new Table(tablename);
			Field field=new Field("isFlag","isFlag");
			field.setDatatype(DataType.STRING);
			field.setLength(10);			
			table.addField(field);
			dbw.addColumns(table);
			
			/**导入数据*/
			HashSet set=new HashSet();
			for(String userid:idsArray)
			{
				userid = PubFunc.decrypt(SafeCode.decode(userid));
				String nbase =userid.substring(0, 3);//人员库前缀
				set.add(nbase);
			}
			
			SalaryCtrlParamBo ctrlparam=this.salaryAccountBo.getSalaryTemplateBo().getCtrlparam();
			String a01z0Flag=ctrlparam.getValue(SalaryCtrlParamBo.A01Z0,"flag");  // 是否显示停发标识  1：有  
			
			ArrayList dataList=new ArrayList();
			for(Iterator t=set.iterator();t.hasNext();)
			{
				String dbName=(String)t.next();
				if(dbName==null||dbName.length()==0)
					continue;
				StringBuffer where=new StringBuffer("");
				for(String userid:idsArray)
				{
					ArrayList valueList=new ArrayList();
					ArrayList list=new ArrayList();
					userid = PubFunc.decrypt(SafeCode.decode(userid));
					String nbase =userid.substring(0, 3);//人员库前缀
					String a0100 =userid.substring(3);//人员编号
					if(nbase.equalsIgnoreCase(dbName)){
						valueList.add(a0100);
						dataList.add(valueList);
					}
				}
				
				StringBuffer buf=new StringBuffer("");
				buf.append("insert into ");
				buf.append(tablename);
				buf.append("(DBNAME,A0100,A0000,B0110,E0122,A0101,STATE,isFlag)");
				buf.append(" select '");
				buf.append(dbName);
				buf.append("' as DBNAME,");
				buf.append("A0100,A0000,B0110,E0122,A0101,'1' as STATE ,'0' as isFlag ");
				buf.append(" from "+dbName+"A01 where a0100=? ");
				dao.batchUpdate(buf.toString(),dataList);
				
				//如果显示停发标示
				if(a01z0Flag!=null&& "1".equals(a01z0Flag))
				{
					StringBuffer _sql=new StringBuffer("delete from "+tablename+" where lower(dbname)='"+dbName.toLowerCase()+"' and  exists (select null from ");
					_sql.append(dbName+"A01 where "+dbName+"A01.a0100="+tablename+".a0100  and A01Z0<>'1' and A01Z0<>'' and A01Z0 is not null  ) ");
					//删除停发薪资的人员
					dao.update(_sql.toString());
				}
				dataList.clear();
			}
			
			/**新增人员*/ 
			this.salaryAccountBo.importAddManData(true,ff_date,count,itemList,true);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			String message=e.toString();
			if(message.indexOf(ResourceFactory.getProperty("tablegrid.summary.max"))!=-1&&message.indexOf("8060")!=-1&&Sql_switcher.searchDbServer()==1) //tablegrid.summary.max=最大：
			{ 
				PubFunc.resolve8060(this.conn,gz_tablename);
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("gz_new.gz_accounting.reImportMen")+"!"));  //请重新执行人员引入操作
			} else if(message.indexOf("不存在")!=-1){
				throw GeneralExceptionHandler.Handle(e);
			}
		}
	}


	/**
	 * 判断是否在薪资账套中已经存在要引入的人
	 * @return
	 * @throws GeneralException
	 * @author ZhangHua
	 * @date 13:42 2019/3/14
	 */
	public String isHaveRepeatedData(String ids) throws GeneralException {

		ContentDAO dao=new ContentDAO(this.conn);
		StringBuffer errMsg=new StringBuffer();
		RowSet rs=null;
		try{
			StringBuffer strSql=new StringBuffer();
			StringBuffer sqlWhere=new StringBuffer();
			String privSql="";

			String gz_tablename=this.salaryAccountBo.getSalaryTemplateBo().getGz_tablename();
			//共享非管理员
			if(StringUtils.isNotBlank(this.salaryAccountBo.getSalaryTemplateBo().getManager())
					&&!this.userview.getUserName().equalsIgnoreCase(this.salaryAccountBo.getSalaryTemplateBo().getManager())) {
				privSql=this.salaryAccountBo.getSalaryTemplateBo().getWhlByUnits(gz_tablename, true);
			}
			strSql.append("select count(*) as num,Max(a0101) as a0101 from  ").append(gz_tablename);

			String[] idsArray = ids.split("'");
			/**导入数据*/
			HashMap<String,ArrayList<String>> dataMap=new HashMap<String, ArrayList<String>>();
			for(String userid:idsArray)
			{
				userid = (PubFunc.decrypt(SafeCode.decode(userid))).toUpperCase();
				String nbase=userid.substring(0,3);
				if(dataMap.containsKey(nbase)){
					dataMap.get(nbase).add(userid.substring(3));
				}else{
					ArrayList<String> list=new ArrayList<String>();
					list.add(userid.substring(3));
					dataMap.put(nbase,list);
				}
			}

			Iterator iterator=dataMap.entrySet().iterator();
			while(iterator.hasNext()){
				Map.Entry<String,ArrayList<String>> entry= (Map.Entry<String, ArrayList<String>>) iterator.next();
				String nbase=entry.getKey();
				ArrayList<String> list=entry.getValue();
				sqlWhere.setLength(0);
				sqlWhere.append(" where a0100 in (");
				for (int i = 0; i < list.size(); i++) {
					sqlWhere.append("?,");
				}
				sqlWhere.deleteCharAt(sqlWhere.length()-1);
				sqlWhere.append(")");
				sqlWhere.append(privSql);
				sqlWhere.append(" and upper(nbase) =? ");
				list.add(nbase);
				sqlWhere.append(" group by a0100 having count(*)>=1 ");

				rs=dao.search(strSql.toString()+sqlWhere.toString(),list);
				while (rs.next()){
					errMsg.append(rs.getString("a0101")+"，");
				}
			}
		}catch (Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeDbObj(rs);
		}

		if(errMsg.length()>1){
			return errMsg.toString()+ResourceFactory.getProperty("gz_new.gz_accounting.handImportPreDuplicate");//"人员数据已存在，是否重复引入？"
		}else{
			return "OK";
		}

	}


	/**
	 * 获取手工引入薪资人员范围条件
	 * @param dbName
	 * @param a00z2
	 * @return
	 * @throws GeneralException
	 * @author ZhangHua
	 * @date 14:44 2019/5/15
	 */
	public String getImportManSql(String dbName,String a00z2) throws GeneralException {
		StringBuffer strSql=new StringBuffer();
		try{

			String _flag=salaryAccountBo.getSalaryTemplateBo().getCtrlparam().getValue(SalaryCtrlParamBo.COND_MODE,"flag");  // "":没条件 0：简单条件 1：复杂条件
			RecordVo templateVo=salaryAccountBo.getSalaryTemplateBo().getTemplatevo();
			String cond=templateVo.getString("cond");
			String cexpr=templateVo.getString("cexpr");
			if("0".equals(_flag)&&cond.length()>0)  //0：简单条件
			{
				FactorList factor = new FactorList(cexpr, cond,dbName, false, false, true, 1, "su");
				strSql.append(" and "+dbName+"A01.a0100 in ( select "+dbName+"A01.a0100 "+ factor.getSqlExpression()+")");

			}
			else if("1".equals(_flag)&&cond.length()>0)  // 1：复杂条件
			{
				HashMap paramMap=new HashMap();
				paramMap.put("pre",dbName);               //人员库
				paramMap.put("ff_date",a00z2);  //发放日期
				paramMap.put("cond",cond);    //高级条件
				strSql.append(salaryAccountBo.getComplexCondSql(new HashMap(),salaryAccountBo.getSalaryTemplateBo().getCtrlparam(),paramMap));
			}

		}catch (Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return strSql.toString();
	}
	
//	/**
//	 * 引入手工选择人员(高级查询的全部引入)
//	 * @param expr 查询表达式
//	 * @param factor  表达式因子
//	 * @param history  取历史记录
//	 * @param isPriv  是否按权限筛选
//	 * @param ff_date 发放日期
//	 * @param count 发放次数
//	 */
//
//	public void importHandSelectedMenHQuery(String expr,String factor,boolean ishistory,boolean isPriv,String ff_date,String count) throws GeneralException
//	{
//		/**临时表名*/
//		String tablename="t#"+this.userview.getUserName()+"_gz_Ins";
//		String gz_tablename=this.salaryAccountBo.getSalaryTemplateBo().getGz_tablename(); //工资发放临时表
//		try
//		{
//			ContentDAO dao=new ContentDAO(this.conn);
//			//生成临时表
//			this.salaryAccountBo.getSalaryTableStructBo().createInsDecTableStruct(tablename,new ArrayList());
//			//根据工资类别id得到类别下面的所有项目列表
//			ArrayList itemList=this.salaryAccountBo.getSalaryTemplateBo().getSalaryItemList("",""+salaryid,1);
//			DbWizard dbw=new DbWizard(this.conn);
//			/** 用于判断是否在临时表中存在 */
//			Table table=new Table(tablename);
//			Field field=new Field("isFlag","isFlag");
//			field.setDatatype(DataType.STRING);
//			field.setLength(10);
//			table.addField(field);
//			dbw.addColumns(table);
//			String cbase=this.salaryAccountBo.getSalaryTemplateBo().getTemplatevo().getString("cbase");
//			cbase=cbase.substring(0,cbase.length()-1);
//			cbase=cbase.replaceAll(",","','");
//			RowSet rowSet=dao.search("select * from dbname where UPPER(pre) in ('"+cbase.toUpperCase()+"')");
//			while(rowSet.next())
//			{
//				String dbname=rowSet.getString("dbname");
//				String pre=rowSet.getString("pre");
//				String a01=pre+"A01";
//				StringBuffer sql=new StringBuffer("");
//				String condSql="";
//				ArrayList fieldlist=new ArrayList();
//				if(this.userview.isSuper_admin())
//					condSql=userview.getPrivSQLExpression(PubFunc.keyWord_reback(SafeCode.decode(expr))+"|"+PubFunc.reBackWord(SafeCode.decode(factor)),pre,ishistory,false,true,fieldlist);
//				else
//				{
//					FactorList factorslist=new FactorList(PubFunc.keyWord_reback(SafeCode.decode(expr)),PubFunc.reBackWord(SafeCode.decode(factor)),pre,ishistory ,false,true,1,userview.getUserId());
//		        	fieldlist=factorslist.getFieldList();
//		        	condSql=factorslist.getSqlExpression();
//				}
//				if(condSql.length()>0)
//				{
//					sql.append(condSql);
//				}
//				if(isPriv)
//			    {
//			    	String privSQL=InfoUtils.getWhereINSql(this.userview, pre);
//			    	sql.append(" and "+a01+".a0100 in (select "+pre+"a01.a0100 "+(privSQL.length()>0?privSQL:(" from "+a01))+")");
//			    }
//				sql.append(" and "+a01+".a0100 not in (select a0100 from "+gz_tablename+" where  upper(nbase)='"+pre.toUpperCase()+"') ");
//
//				StringBuffer buf=new StringBuffer("");
//				buf.append("insert into ");
//				buf.append(tablename);
//				buf.append("(DBNAME,A0100,A0000,B0110,E0122,A0101,STATE,isFlag)");
//				buf.append(" select '");
//				buf.append(pre);
//				buf.append("' as DBNAME,");
//				buf.append("A0100,A0000,B0110,E0122,A0101,'1' as STATE ,'0' as isFlag ");
//				buf.append(" from "+pre+"A01 where A0100 in ( select "+pre+"A01.A0100 "+sql.toString()+")");
//				dao.update(buf.toString());
//			}
//			/**新增人员*/
//			this.salaryAccountBo.importAddManData(true,ff_date,count,itemList,true);
//
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//			String message=e.toString();
//			if(message.indexOf(ResourceFactory.getProperty("tablegrid.summary.max"))!=-1&&message.indexOf("8060")!=-1&&Sql_switcher.searchDbServer()==1) //tablegrid.summary.max=最大：
//			{
//				PubFunc.resolve8060(this.conn,gz_tablename);
//				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("gz_new.gz_accounting.reImportMen")+"!"));  //请重新执行人员引入操作
//			}
//		}
//	}
	
}
