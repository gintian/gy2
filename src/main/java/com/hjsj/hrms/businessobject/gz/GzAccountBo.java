package com.hjsj.hrms.businessobject.gz;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;

public class GzAccountBo {
	private Connection conn;
	private ArrayList rightlist = new ArrayList();
	public GzAccountBo(){

	}
	public GzAccountBo(Connection conn,String salaryid){
		this.conn=conn;
		this.rightlist = rightList(salaryid);
	}
	 /**
     * 查询薪资项目子集
     * @param dao
     * @throws GeneralException
     */
	public ArrayList rightList(String salaryid){
		ArrayList retlist=new ArrayList();
		SalaryCtrlParamBo ctrl_par = new SalaryCtrlParamBo(conn,Integer.parseInt(salaryid));
		String rightvalue = ctrl_par.getValue(SalaryCtrlParamBo.COMPARE_FIELD);
		rightvalue=rightvalue!=null?rightvalue.replaceAll(",","','"):"";
		ContentDAO dao = new ContentDAO(conn);
		StringBuffer sql=new StringBuffer();
		sql.append("select itemid,itemdesc,sortid from salaryset where initflag<>3 and salaryid=");
		sql.append(salaryid);
		sql.append(" and itemid in ('");
		sql.append(rightvalue);
		sql.append("') group by itemid,itemdesc,sortid order by sortid");
		ArrayList dylist = null;
		try {
			dylist = dao.searchDynaList(sql.toString());
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				CommonData obj=new CommonData(dynabean.get("itemid").toString(),dynabean.get("itemdesc").toString());
				retlist.add(obj);
			}
		} catch (GeneralException e) {
			e.printStackTrace();
		}
		return retlist;
	}
	 /**
     * 差额合计
     * @param userView
     * @return vartotlaList
     * @throws GeneralException
     */
	public ArrayList varianceTotal(UserView userView){
		ArrayList vartotlaList=new ArrayList();
		if(rightlist.size()>0){
			ContentDAO dao = new ContentDAO(conn);
			StringBuffer sql1=new StringBuffer();
			StringBuffer sql2=new StringBuffer();
			sql1.append("select ");
			sql2.append("select ");
			for(int i=0;i<rightlist.size();i++){
				CommonData obj = (CommonData)rightlist.get(i);
				String itemid = obj.getDataValue().toLowerCase();
				FieldItem fielditem = DataDictionary.getFieldItem(itemid);
				if(fielditem!=null&& "N".equalsIgnoreCase(fielditem.getItemtype())){
					sql2.append("sum("+itemid+"_2) as ");
					sql2.append(itemid+"_2");
					
					sql1.append("sum("+itemid+"_1) as ");
					sql1.append(itemid+"_1");
					sql1.append(",");
					sql2.append(",");
				}
			}
			
			sql1.append(" sum(a0000) as a0000 from ");
			sql1.append(userView.getUserName()+"_gz_his_chg where changeflag=0");
		
			sql2.append(" sum(a0000) as a0000 from ");
			sql2.append(userView.getUserName()+"_gz_his_chg where changeflag=0");
			try {
				RowSet rs1 = dao.search(sql1.toString());
				RowSet rs2 = dao.search(sql2.toString());
				while(rs1.next()&&rs2.next()){
					for(int i=0;i<rightlist.size();i++){
						CommonData obj = (CommonData)rightlist.get(i);
						String itemid = obj.getDataValue();
						FieldItem fielditem = DataDictionary.getFieldItem(itemid);
						if(fielditem!=null&& "N".equalsIgnoreCase(fielditem.getItemtype())){
							double values1 = rs1.getDouble(obj.getDataValue().toLowerCase()+"_1");
							double values2 = rs2.getDouble(obj.getDataValue().toLowerCase()+"_2");
					
							vartotlaList.add(PubFunc.round((values2-values1)+"",fielditem.getDecimalwidth()));
						}else{
							vartotlaList.add("");
						}
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return vartotlaList;
	}
	 /**
     * 合计
     * @param userView
     * @param changeflag
     * @return totlaList
     * @throws GeneralException
     */
	public ArrayList totalValue(UserView userView,String changeflag){
		ArrayList totlaList=new ArrayList();
		if(rightlist.size()>0){
			ContentDAO dao = new ContentDAO(conn);
			StringBuffer sql=new StringBuffer();
			sql.append("select ");
			for(int i=0;i<rightlist.size();i++){
				CommonData obj = (CommonData)rightlist.get(i);
				String itemid = obj.getDataValue();
				FieldItem fielditem = DataDictionary.getFieldItem(itemid);
				/**减少人员_1有数据_2是0，新增人员_2有数据_1是0*/
			    /**1是新增，2是减少*/
				if(fielditem!=null&& "N".equalsIgnoreCase(fielditem.getItemtype())){
					if("0".equals(changeflag))
					{
				    	sql.append("sum(case when "+itemid+"_1="+itemid+"_2 then 0 else "+itemid+"_2 end)");
				    	sql.append(" as ");
				    	sql.append(itemid);
				    	sql.append(",");
					}
					if("1".equals(changeflag))
					{
						sql.append("sum("+itemid+"_2)");
				    	sql.append(" as ");
				    	sql.append(itemid);
				    	sql.append(",");
					}
					if("2".equals(changeflag))
					{
						sql.append("sum("+itemid+"_1)");
				    	sql.append(" as ");
				    	sql.append(itemid);
				    	sql.append(",");
					}
					
				}
			
			}
			sql.append(" sum(a0000) as a0000 from "+userView.getUserName());
			sql.append("_gz_his_chg where changeflag=");
			sql.append(changeflag);
			try {
				RowSet rs = dao.search(sql.toString());
				while(rs.next()){
					for(int i=0;i<rightlist.size();i++){
						CommonData obj = (CommonData)rightlist.get(i);
						String itemid = obj.getDataValue();
						FieldItem fielditem = DataDictionary.getFieldItem(itemid);
						if(fielditem!=null&& "N".equalsIgnoreCase(fielditem.getItemtype())){
							double values = rs.getDouble(itemid.toLowerCase());
							totlaList.add(PubFunc.round(values+"",fielditem.getDecimalwidth()));
						}else{
							totlaList.add("");
						}
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return totlaList;
	}
	 /**
     * 获取变化前字段
     * @return beforelist
     * @throws GeneralException
     */
	public ArrayList beforeChange(){
		ArrayList beforelist=new ArrayList();

		for(int i=0;i<rightlist.size();i++){
			CommonData obj = (CommonData)rightlist.get(i);
			beforelist.add(obj.getDataValue().toLowerCase()+"_1");
			
		}
		return beforelist;
	}
	 /**
     * 获取变化后字段
     * @return afterlist
     * @throws GeneralException
     */
	public ArrayList afterChange(){
		ArrayList afterlist=new ArrayList();

		for(int i=0;i<rightlist.size();i++){
			CommonData obj = (CommonData)rightlist.get(i);
			afterlist.add(obj.getDataValue().toLowerCase()+"_2");
		}
		return afterlist;
	}
	public ArrayList changeList(){
		ArrayList afterlist=new ArrayList();
		for(int i=0;i<rightlist.size();i++){
			CommonData obj = (CommonData)rightlist.get(i);
			FieldItem fielditem = DataDictionary.getFieldItem(obj.getDataValue());
//			Field field = fielditem.cloneField();
			afterlist.add(fielditem);
		}
		return afterlist;
	}
	 /**
     * 获取字段
     * @return afterlist
     * @throws GeneralException
     */
	public ArrayList fieldList(){
		ArrayList fieldlist=new ArrayList();
		for(int i=0;i<rightlist.size();i++){
			CommonData obj = (CommonData)rightlist.get(i);
			fieldlist.add(obj.getDataName());
		}
		return fieldlist;
	}
	 /**
     * 获取变动比对sql语句
     * @param userView
     * @param changeflag
     * @return sqlstr
     * @throws GeneralException
     */
	public String changeSql(UserView userView,String changeflag){
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("select b0110,e0122,a0101");
		for(int i=0;i<rightlist.size();i++){
			CommonData obj = (CommonData)rightlist.get(i);
			if("0".equals(changeflag))
			{
				FieldItem item = DataDictionary.getFieldItem(obj.getDataValue());
				if(item==null||!"N".equalsIgnoreCase(item.getItemtype()))
				{
					sqlstr.append(","+obj.getDataValue().toLowerCase()+"_2");
					sqlstr.append(","+obj.getDataValue().toLowerCase()+"_1");
				}
				else
				{
		    		sqlstr.append(",case when ("+obj.getDataValue().toLowerCase()+"_2=");
	    			sqlstr.append(obj.getDataValue().toLowerCase()+"_1 or "+obj.getDataValue().toLowerCase()+"_2 is null) then 0 else "+obj.getDataValue().toLowerCase()+"_2 end as ");
		    		sqlstr.append(obj.getDataValue().toLowerCase()+"_2 ");
				
		    		sqlstr.append(",case when ("+obj.getDataValue().toLowerCase()+"_2=");
		    		sqlstr.append(obj.getDataValue().toLowerCase()+"_1 or "+obj.getDataValue().toLowerCase()+"_1 is null) then 0 else "+obj.getDataValue().toLowerCase()+"_1 end as ");
			    	sqlstr.append(obj.getDataValue().toLowerCase()+"_1 ");
				}
			}
			if("2".equals(changeflag))
			{
				sqlstr.append(","+obj.getDataValue().toLowerCase()+"_1");
			}
			if("1".equals(changeflag))
			{
				sqlstr.append(","+obj.getDataValue().toLowerCase()+"_2");
			}
		}
		sqlstr.append(" from "+userView.getUserName());
		sqlstr.append("_gz_his_chg ");
		sqlstr.append(" where changeflag="+changeflag);
		sqlstr.append(" order by a0000");
		
 		return sqlstr.toString();
	}
	 /**
     * 获取变动比对sql语句
     * @param changeflag
     * @return sqlstr
	 * @throws SQLException 
     * @throws GeneralException
     */
	public String sqlStr(String changeflag,String tablename,String where) throws SQLException{
		StringBuffer sqlstr = new StringBuffer();
		ContentDAO dao=new ContentDAO(this.conn);
		sqlstr.append("select b0110,e0122,a0101");
		for(int i=0;i<rightlist.size();i++){
			CommonData obj = (CommonData)rightlist.get(i);
			if("0".equals(changeflag))
			{
				//sqlstr.append(","+obj.getDataValue().toLowerCase()+"_2,"+obj.getDataValue().toLowerCase()+"_1");
				/**代码型相同的还显示**/
				FieldItem item = DataDictionary.getFieldItem(obj.getDataValue());
				if(item==null||!"N".equalsIgnoreCase(item.getItemtype()))
				{
					sqlstr.append(","+obj.getDataValue().toLowerCase()+"_2");
					sqlstr.append(","+obj.getDataValue().toLowerCase()+"_1");
				}
				else 
				{
					StringBuffer temp = new StringBuffer();
					StringBuffer temp2 = new StringBuffer();
					temp2.append("case when ("+obj.getDataValue().toLowerCase()+"_2=");
					temp2.append(obj.getDataValue().toLowerCase()+"_1 or "+obj.getDataValue().toLowerCase()+"_2 is null) then 0 else "+obj.getDataValue().toLowerCase()+"_2 end ");
					temp.append(obj.getDataValue().toLowerCase()+"_2 = "+temp2);
					//dao.update("update "+tablename+" set "+obj.getDataValue().toLowerCase()+"_2 = "+temp2+" where "+where+" ");
					sqlstr.append(","+obj.getDataValue().toLowerCase()+"_2");
					
					StringBuffer temp1 = new StringBuffer();
					temp1.append("case when ("+obj.getDataValue().toLowerCase()+"_2=");
					temp1.append(obj.getDataValue().toLowerCase()+"_1 or "+obj.getDataValue().toLowerCase()+"_1 is null) then 0 else "+obj.getDataValue().toLowerCase()+"_1 end ");
					temp.append(","+obj.getDataValue().toLowerCase()+"_1 = "+temp1);
					//dao.update("update "+tablename+" set "+obj.getDataValue().toLowerCase()+"_1 = "+temp1+" where "+where+" ");
					dao.update("update "+tablename+" set "+temp+" where "+where+" ");
			    	sqlstr.append(","+obj.getDataValue().toLowerCase()+"_1");
			    	
//		    		sqlstr.append(",case when ("+obj.getDataValue().toLowerCase()+"_2=");
//	    			sqlstr.append(obj.getDataValue().toLowerCase()+"_1 or "+obj.getDataValue().toLowerCase()+"_2 is null) then 0 else "+obj.getDataValue().toLowerCase()+"_2 end as ");
//		    		sqlstr.append(obj.getDataValue().toLowerCase()+"_2 ");
//				
//		    		sqlstr.append(",case when ("+obj.getDataValue().toLowerCase()+"_2=");
//		    		sqlstr.append(obj.getDataValue().toLowerCase()+"_1 or "+obj.getDataValue().toLowerCase()+"_1 is null) then 0 else "+obj.getDataValue().toLowerCase()+"_1 end as ");
//			    	sqlstr.append(obj.getDataValue().toLowerCase()+"_1 ");
				}
			}
			if("1".equals(changeflag))
			{
				sqlstr.append(","+obj.getDataValue().toLowerCase()+"_2");
			}
			/**减少人员要显示数据的话，用这个*/
			if("2".equals(changeflag))
			{
				sqlstr.append(","+obj.getDataValue().toLowerCase()+"_1");
			}
		}
		
 		return sqlstr.toString();
	}
	
	
	 /**
     * 获取变动比对sql语句
     * @param changeflag
     * @return sqlstr
	 * @throws SQLException 
     * @throws GeneralException
     */
	public String sqlStr(String changeflag) throws SQLException{
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("select b0110,e0122,a0101");
		for(int i=0;i<rightlist.size();i++){
			CommonData obj = (CommonData)rightlist.get(i);
			if("0".equals(changeflag))
			{
				//sqlstr.append(","+obj.getDataValue().toLowerCase()+"_2,"+obj.getDataValue().toLowerCase()+"_1");
				/**代码型相同的还显示**/
				FieldItem item = DataDictionary.getFieldItem(obj.getDataValue());
				if(item==null||!"N".equalsIgnoreCase(item.getItemtype()))
				{
					sqlstr.append(","+obj.getDataValue().toLowerCase()+"_2");
					sqlstr.append(","+obj.getDataValue().toLowerCase()+"_1");
				}
				else 
				{				
		    		sqlstr.append(",case when ("+obj.getDataValue().toLowerCase()+"_2=");
	    			sqlstr.append(obj.getDataValue().toLowerCase()+"_1 or "+obj.getDataValue().toLowerCase()+"_2 is null) then 0 else "+obj.getDataValue().toLowerCase()+"_2 end as ");
		    		sqlstr.append(obj.getDataValue().toLowerCase()+"_2 ");
				
		    		sqlstr.append(",case when ("+obj.getDataValue().toLowerCase()+"_2=");
		    		sqlstr.append(obj.getDataValue().toLowerCase()+"_1 or "+obj.getDataValue().toLowerCase()+"_1 is null) then 0 else "+obj.getDataValue().toLowerCase()+"_1 end as ");
			    	sqlstr.append(obj.getDataValue().toLowerCase()+"_1 ");
				}
			}
			if("1".equals(changeflag))
			{
				sqlstr.append(","+obj.getDataValue().toLowerCase()+"_2");
			}
			/**减少人员要显示数据的话，用这个*/
			if("2".equals(changeflag))
			{
				sqlstr.append(","+obj.getDataValue().toLowerCase()+"_1");
			}
		}
		
 		return sqlstr.toString();
	}
	
	
	
	
	 /**
     * 获取column
     * @param values
     * @throws GeneralException
     */
	public String column(String changeflag){
		StringBuffer column = new StringBuffer("b0110,e0122,a0101");
		
		for(int i=0;i<rightlist.size();i++){
			CommonData obj = (CommonData)rightlist.get(i);
			/*if(changeflag.equals("0")||changeflag.equals("2"))
				column.append(","+obj.getDataValue().toLowerCase()+"_2");
			if(changeflag.equals("0")||changeflag.equals("1"))
				column.append(","+obj.getDataValue().toLowerCase()+"_2");*/
			if("0".equals(changeflag))
			{
				column.append(","+obj.getDataValue().toLowerCase()+"_2,"+obj.getDataValue().toLowerCase()+"_1");
			}
			if("1".equals(changeflag))
			{
				column.append(","+obj.getDataValue().toLowerCase()+"_2");
			}
			if("2".equals(changeflag))
			{
				column.append(","+obj.getDataValue().toLowerCase()+"_1");
			}
		}
		
 		return column.toString();
	}
	public String getColumStr(RowSet rset,ResultSetMetaData rsetmd,String str) throws SQLException{
		int j=rset.findColumn(str);
		String temp=null;
		switch(rsetmd.getColumnType(j)){
		
		case Types.DATE:
		        temp=PubFunc.FormatDate(rset.getDate(j));
		        break;			
		case Types.TIMESTAMP:
			    temp=PubFunc.FormatDate(rset.getDate(j),"yyyy-MM-dd hh:mm:ss");
			    if(temp.indexOf("12:00:00")!=-1)
			        temp=PubFunc.FormatDate(rset.getDate(j));
				break;
		case Types.CLOB:
			    temp=Sql_switcher.readMemo(rset,rsetmd.getColumnName(j));	                    	
				break;
		case Types.BLOB:
				temp=ResourceFactory.getProperty("gz.acount.binary.files");	                    	
				break;		
		case Types.NUMERIC:
			  int preci=rsetmd.getScale(j);
			  temp=String.valueOf(rset.getDouble(j));			  
			  temp=PubFunc.DoFormatDecimal(temp, preci);
			  break;
		default:		
				temp=rset.getString(j);
				break;
		}
		return temp;
	}
	 /**
     * 将小数点精确到2位
     * @param values
     * @throws GeneralException
     */
	public String doubles(String values){
		String doubles ="";
		values=values!=null&&values.trim().length()>0?values:"0.00";
		/*String[] arr = values.split("\\.");
		if(arr.length==2){
			doubles.append(arr[0]+".");
			if(arr[1].length()>2){
				doubles.append(arr[1].substring(0,2));
			}else if(arr[1].length()==1){
				doubles.append(arr[1]+"0");
			}else if(arr[1].length()==2){
				doubles.append(arr[1]);
			}else if(arr[1].length()<1){
				doubles.append("00");
			}
		}*/
		try
		{
			doubles=PubFunc.round(values,2);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
 		return doubles;
	}
}
