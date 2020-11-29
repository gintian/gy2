package com.hjsj.hrms.transaction.performance.data_collect;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.performance.data_collect.DataCollectBo;
import com.hjsj.hrms.utils.analyse.YearMonthCount;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
* 
* 类名称：AddData_collectTrans   
* 类描述：   
* 创建人：zhaoxg   
* 创建时间：Aug 21, 2013 1:13:19 PM   
* 修改人：zhaoxg   
* 修改时间：Aug 21, 2013 1:13:19 PM   
* 修改备注：   增加人员
* @version    
*
 */
public class AddData_collectTrans extends IBusiness {

	public void execute() throws GeneralException {
		ContentDAO dao=new ContentDAO(this.frameconn);
		try
		{
			String fieldsetid=(String)this.getFormHM().get("fieldsetid");
		DataCollectBo bo = new DataCollectBo(this.frameconn,"Params");
		DataCollectBo databo = new DataCollectBo(this.frameconn,this.userView);
		ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
		String cbase  = bo.getXmlValue1("cbase",fieldsetid);
		String[] _cbase = cbase.split(",");
		String flag  = bo.getXmlValue1("flag",fieldsetid);//flag 1:简单条件  2:复杂条件
		String value = bo.getValue(fieldsetid);
		String set_id  = bo.getXmlValue1("set_id",fieldsetid);
		String state_id  = bo.getXmlValue1("state_id",fieldsetid);
		String year = (String) this.getFormHM().get("year");
		String month = (String) this.getFormHM().get("month");
		String ym = Sql_switcher.dateValue(year+"."+month+"."+01);
		HashMap tempym = this.userView.getHm();
		String pre = (String) tempym.get("pre");


		if("1".equals(flag)&&value.length()>0){
			
				StringBuffer _sql = new StringBuffer("");
				String priStrSql = InfoUtils.getWhereINSql(this.userView, pre);
				_sql.append("select "+pre+"a01.A0100 ");
				if (priStrSql.length() > 0)
					_sql.append(priStrSql);
				else
					_sql.append(" from "+pre+"a01");

				
				FactorList factor = new FactorList("1", value,pre, false, false, true, 1, this.userView.getUserId());				
				String strSql = factor.getSqlExpression();
				StringBuffer sql = new StringBuffer();
				sql.append("insert into "+pre+set_id+" (a0100,"+state_id+","+set_id+"z0,"+set_id+"z1,i9999) ");
				sql.append(" select a0100,'01',"+Sql_switcher.dateValue(year+"."+month+"."+01)+",'0','0' from "+pre+"a01 where 1=1 ");
	    		if(this.userView.isSuper_admin()|| "1".equals(this.userView.getGroupId()))
	    		{
	    			
	    		}
	    		else
	    		{
	    			sql.append(" and "+pre+"A01.a0100 in ( "+_sql+")"); 
	    		}
				sql.append(" and "+pre+"A01.a0100 in ( select "+pre+"A01.a0100 "+strSql+")"); 
				dao.update(sql.toString());
				databo.UpdateIZ(pre,set_id,ym);
			
		}else if("2".equals(flag)&&value.length()>0){
			
				StringBuffer _sql = new StringBuffer("");
				String priStrSql = InfoUtils.getWhereINSql(this.userView, pre);
				_sql.append("select "+pre+"a01.A0100 ");
				if (priStrSql.length() > 0)
					_sql.append(priStrSql);
				else
					_sql.append(" from "+pre+"a01");
				
				String tempTableName ="";
				String w ="";
				int infoGroup = 0; // forPerson 人员
				int varType = 8; // logic	
				String whereIN="select "+pre+"A01.a0100 from "+pre+"A01";
				alUsedFields.addAll(this.getMidVariableList(set_id));
				YksjParser yp = new YksjParser(this.userView ,alUsedFields,
						YksjParser.forSearch, varType, infoGroup, "Ht",pre);
				YearMonthCount ymc=null;							
				yp.run_Where(value, ymc,"","hrpwarn_result", dao, whereIN,this.frameconn,"A", null);
				tempTableName = yp.getTempTableName();
				w = yp.getSQL();
				StringBuffer sql = new StringBuffer();
				sql.append("insert into "+pre+set_id+" (a0100,"+state_id+","+set_id+"z0,"+set_id+"z1,i9999) ");
				sql.append(" select a0100,'01',"+Sql_switcher.dateValue(year+"."+month+"."+01)+",'0','0' from "+pre+"a01 where 1=1 ");
	    		if(this.userView.isSuper_admin()|| "1".equals(this.userView.getGroupId()))
	    		{
	    			
	    		}
	    		else
	    		{
	    			sql.append(" and "+pre+"A01.a0100 in ( "+_sql+")"); 
	    		}
				sql.append("and exists (select null from "+tempTableName+" where "+tempTableName+".a0100="+pre+"A01.a0100 and ( "+w+" ))");
				dao.update(sql.toString());
				databo.UpdateIZ(pre,set_id,ym);
			
			}else{
				
					StringBuffer _sql = new StringBuffer("");
					String priStrSql = InfoUtils.getWhereINSql(this.userView, pre);
					_sql.append("select "+pre+"a01.A0100 ");
					if (priStrSql.length() > 0)
						_sql.append(priStrSql);
					else
						_sql.append(" from "+pre+"a01");

					
					StringBuffer sql = new StringBuffer();
					sql.append("insert into "+pre+set_id+" (a0100,"+state_id+","+set_id+"z0,"+set_id+"z1,i9999) ");
					sql.append(" select a0100,'01',"+Sql_switcher.dateValue(year+"."+month+"."+01)+",'0','0' from "+pre+"a01 where 1=1 ");
		    		if(this.userView.isSuper_admin()|| "1".equals(this.userView.getGroupId()))
		    		{
		    			
		    		}
		    		else
		    		{
		    			sql.append(" and "+pre+"A01.a0100 in ( "+_sql+")"); 
		    		}
					dao.update(sql.toString());
					databo.UpdateIZ(pre,set_id,ym);
				}
			
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	/**
	 * 从临时变量中取得对应指标列表
	 * @return FieldItem对象列表
	 * @throws GeneralException
	 */
	public ArrayList getMidVariableList(String fieldsetid)throws GeneralException
	{
		ArrayList fieldlist=new ArrayList();
		ArrayList new_fieldList=new ArrayList();
		RowSet rset=null;
		try
		{
			StringBuffer buf=new StringBuffer();
			buf.append("select cname,chz,ntype,cvalue,fldlen,flddec,codesetid from ");
			buf.append(" midvariable where nflag=5 and templetid=0 ");
			buf.append(" and (cstate is null or cstate='");
			buf.append(fieldsetid);
			buf.append("') order by sorting");
			ContentDAO dao=new ContentDAO(this.frameconn);
			rset=dao.search(buf.toString());
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
			}		
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		finally
		{
			try
			{
				if(rset!=null)
					rset.close();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}			
		return new_fieldList;
	}
}
