package com.hjsj.hrms.businessobject.gz.templateset;

import com.hjsj.hrms.businessobject.gz.FormulaBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * 
 *<p>Title:</p> 
 *<p>Description:薪资标准包 控制类</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jul 3, 2007:11:46:12 AM</p> 
 *@author dengcan
 *@version 4.0
 */
public class SalaryStandardPackBo {
	private Connection conn=null;
	private UserView userView;
	public SalaryStandardPackBo(Connection con)
	{
		this.conn=con;
	}
	public SalaryStandardPackBo(Connection con,UserView userView)
	{
		this.conn=con;
		this.userView=userView;
	}
	
	
	
	
	
	/**
	 * 获得需导出的数据
	 * @param ids  
	 * @param type  1：gz_stand_pkg  2:gz_stand   3:gz_item   4:   5:   6:
	 * @return
	 */
	public LazyDynaBean getOutPutTableInfo(String ids,String type)
	{
		LazyDynaBean abean=new LazyDynaBean();
		String[] a_ids=ids.split("#");
		String columns="";
		String keycolumns="";
		if("1".equals(type))
		{
			columns="NAME,START_DATE,END_DATE,STATUS";
			keycolumns="PKG_ID";
		}
		else if("2".equals(type))
		{
			columns="NAME,DB_TYPE,UNIT_TYPE,FLAG,HFACTOR,HCONTENT,VFACTOR,VCONTENT,ITEM,LEXPR,FACTOR,S_VFACTOR,S_HFACTOR,b0110,createorg";
			keycolumns="ID";
		}
		else if("3".equals(type))
		{
			columns="ID,HVALUE,VVALUE,S_HVALUE,S_VVALUE,STANDARD";
			keycolumns="";
		}
		abean.set("columns",columns);
		abean.set("keycolumns",keycolumns);		
		ArrayList list=getTableContextInfoList(a_ids,type,columns,keycolumns);
		abean.set("records",list);
		abean.set("rowcount",String.valueOf(list.size()));	
		return abean;
	}
	
	
	
	public ArrayList getTableContextInfoList(String[] ids,String type,String columns,String keycolumns)
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String tableName=getTableName(type);
			HashMap columnTypeMap=getColumnTypeMap(tableName);
			int num=ids.length/200;
			if(ids.length%200!=0)
				num++;
			for(int n=0;n<num;n++)
			{
				String sql=getSql(ids,type,n);
				RowSet rowSet=dao.search(sql);
				LazyDynaBean abean=null;
				SimpleDateFormat  df=new SimpleDateFormat("yyyy-MM-dd");
				while(rowSet.next())
				{
					abean=new LazyDynaBean();
					
					for(int j=0;j<2;j++)
					{
						String temp="";
						if(j==0)
							temp=columns;
						else
							temp=keycolumns;
						if(temp.trim().length()>0)
						{
							String[] temps=temp.split(",");
							for(int i=0;i<temps.length;i++)
							{
								if(columnTypeMap.get(temps[i].toLowerCase())!=null)
								{
									if("D".equals((String)columnTypeMap.get(temps[i].toLowerCase())))
									{
										if(rowSet.getString(temps[i])==null)
											abean.set(temps[i],"");
										else
											abean.set(temps[i],df.format(rowSet.getDate(temps[i])));
									}
									else if("M".equals((String)columnTypeMap.get(temps[i].toLowerCase())))
									{
										abean.set(temps[i],Sql_switcher.readMemo(rowSet,temps[i]));
									}	
									else
									{
										abean.set(temps[i],rowSet.getString(temps[i])==null?"":rowSet.getString(temps[i]));
									}
								}
								else
									abean.set(temps[i],rowSet.getString(temps[i])==null?"":rowSet.getString(temps[i]));
							}
						}
					}
					list.add(abean);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	
	public String getSql(String[] ids,String type,int num)
	{
		String sql="";
		StringBuffer whl=new StringBuffer("");
		
		for(int i=num*200;i<(num+1)*200;i++)
		{
			if(ids.length>i)
			{
				if(ids[i].length()>0)
					whl.append(","+ids[i]);
			}
			else
				break;
		}
		
		if("1".equals(type)){
			sql="select * from gz_stand_pkg where pkg_id in ("+whl.substring(1)+")";
	    }else if("2".equals(type)){
	    	String pkgid=DownLoadXml.getStartPkgID(this.conn);
			sql="select gz_stand.*,b0110,createorg from gz_stand left join (select * from gz_stand_history where pkg_id="+pkgid+") gzh on gz_stand.id=gzh.id where gz_stand.id in ("+whl.substring(1)+") order by gz_stand.id";
		}else if("3".equals(type)){
			sql="select * from gz_item where id in ("+whl.substring(1)+") order by gz_item.id";
		}
		return sql;
	}
	
	
	/**
	 * 取得表列类型
	 * @param tableName
	 * @return
	 */
	public HashMap getColumnTypeMap(String tableName)
	{
		HashMap columnTypeMap=new HashMap();
		try
		{
			ContentDAO dao = new ContentDAO(conn);
			ResultSet resultSet=dao.search("select * from "+tableName+" where 1=2");
			ResultSetMetaData metaData=resultSet.getMetaData();
			for (int i = 1; i <= metaData.getColumnCount(); i++) {
				String name=metaData.getColumnName(i).toLowerCase();
				String type="A";
				int  columnType=metaData.getColumnType(i);
				switch (columnType) {
				case Types.LONGVARCHAR:
				{
					type="M";
					break;
				}
				case Types.TINYINT:
				{
					type="N";
					break;
				}
				case Types.SMALLINT:
				{
					type="N";
					break;
				}
				case Types.INTEGER:
				{
					type="N";
					break;
				}
				case Types.BIGINT:
				{
					type="N";
					break;
				}
				case Types.FLOAT:
				{
					type="N";
					break;
				}
				case Types.DOUBLE:
				{
					type="N";
					break;
				}
				case Types.DECIMAL:
				{
					type="N";
					break;
				}
				case Types.NUMERIC:
				{
					type="N";
					break;
				}
				case Types.REAL: 
				{
					type="N";
					break;
				}
				case Types.DATE:
				{
					type="D";
					break;
				}
				case Types.TIME:
				{
					type="D";
					break;
				}
				case Types.TIMESTAMP: {
					type="D";
					break;
				}
				default:
					type="A";
					break;
				}
				columnTypeMap.put(name,type);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return columnTypeMap;
	}
	
	
	
	public String getTableName(String type)
	{
		String tableName="";
		if("1".equals(type))
			tableName="gz_stand_pkg";
		else if("2".equals(type))
			tableName="gz_stand_history";
		else if("3".equals(type))
			tableName="gz_item_history";
		else if("4".equals(type))
			tableName="";
		return tableName;
	}
	
	
	
	/**
	 * 取得需导出工资包的详细信息
	 * @param packageList
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getGzStandardPackageInfo(ArrayList packageList)throws GeneralException
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer whl=new StringBuffer("");
			for(int i=0;i<packageList.size();i++)
			{
				LazyDynaBean abean=(LazyDynaBean)packageList.get(i);
				whl.append(","+(String)abean.get("pkg_id"));
			}
			StringBuffer sql=new StringBuffer("select pkg.pkg_id,pkg.name,gsh.id,gsh.name s_name ");
			sql.append(" from gz_stand_history gsh,gz_stand_pkg pkg ");
			sql.append(" where gsh.pkg_id=pkg.pkg_id and gsh.pkg_id in ("+whl.substring(1)+") ");
			String unitid = "XXXX";
			StringBuffer tt = new StringBuffer();
			if(this.userView.isSuper_admin())
			{
				unitid="";
				tt.append(" or 1=1 ");
			}
			else
			{
				if(this.userView.getUnit_id()!=null&&this.userView.getUnit_id().trim().length()>2)
				{
					if(this.userView.getUnit_id().length()==3)
					{
						unitid="";
						tt.append(" or 1=1 ");
					}
					else
					{
				    	unitid=this.userView.getUnit_id();
				    	String[] unit_arr = unitid.split("`");
				    	for(int i=0;i<unit_arr.length;i++)
				    	{
				    		if(unit_arr[i]==null|| "".equals(unit_arr[i]))
				    			continue;
				    		tt.append(" or b0110 like '%,"+unit_arr[i].substring(2)+"%' ");
				    	}
					}
				}
				else{
					if(this.userView.getManagePrivCode()!=null&&this.userView.getManagePrivCode().trim().length()>0)
					{
						if(this.userView.getManagePrivCodeValue()==null|| "".equals(this.userView.getManagePrivCodeValue().trim()))
						{
							unitid="";
							tt.append(" or 1=1 ");
						}
						else{
					    	unitid=this.userView.getManagePrivCode()+this.userView.getManagePrivCodeValue();
					    	tt.append(" or b0110 like '%,"+this.userView.getManagePrivCodeValue()+"%'");
						}
					}
					else//没有范围
					{
						
					}
				}
			}
			if(tt.toString().length()>0)
			{
				if(this.userView.isSuper_admin()|| "".equals(unitid))
				{
					
				}else
				{
					sql.append(" and (");
					sql.append("("+tt.toString().substring(3)+")");
					sql.append(" or UPPER(b0110)='UN' or "+Sql_switcher.isnull("b0110", "'E'")+"='E'");
					sql.append(")");
				}
			}
			if("XXXX".equals(unitid))
			{
				sql.append(" and "+Sql_switcher.isnull("b0110", "'E'")+"='E'");
			}
			sql.append("  order by gsh.id");
			RowSet rowSet=dao.search(sql.toString());
			
			String pkg_id="";
			LazyDynaBean a_bean=null;
			while(rowSet.next())
			{
				String a_pkg_id=rowSet.getString("pkg_id");
				String a_name=rowSet.getString("name");
				String a_id=rowSet.getString("id");
				String s_name=rowSet.getString("s_name");
				
				if(pkg_id.length()==0)
				{
					a_bean=new LazyDynaBean();
					a_bean.set("flag","0");   //历史沿革
					a_bean.set("name",a_name);
					a_bean.set("id",a_pkg_id);
					list.add(a_bean);
					pkg_id=a_pkg_id;
				}
				if(!a_pkg_id.equals(pkg_id))
				{
					a_bean=new LazyDynaBean();
					a_bean.set("flag","0");   //历史沿革
					a_bean.set("name",a_name);
					a_bean.set("id",a_pkg_id);
					list.add(a_bean);
					pkg_id=a_pkg_id;
				}
				
				a_bean=new LazyDynaBean();
				a_bean.set("flag","1");   //工资标准
				a_bean.set("name",s_name);
				a_bean.set("id",a_id);
				list.add(a_bean);
				
			}
			
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}
	
	/**
	 * 取得有效标识
	 * @param pkg_id
	 * @return
	 */
	public int getFlag(String pkg_id)throws GeneralException
	{
		int flag=0;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select status from gz_stand_pkg where pkg_id="+pkg_id);
			if(rowSet.next())
				flag=Integer.parseInt(rowSet.getString("status"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return flag;
	}
	
	
	public void updatePackageStandarList(String pkg_id,String[] ids)throws GeneralException
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			int flag=getFlag(pkg_id);
			if(ids.length==1&& "#".equals(ids[0]))
			{
				dao.delete("delete from gz_stand_history where pkg_id="+pkg_id,new ArrayList());
				dao.delete("delete from gz_item_history where pkg_id="+pkg_id,new ArrayList());		
			}
			else
			{
			
				StringBuffer a_ids=new StringBuffer("");
				for(int i=0;i<ids.length;i++)
					a_ids.append("/"+ids[i]);
				
				StringBuffer whl=new StringBuffer("");
				ArrayList existStandardIds=getStandardList(pkg_id,1);
				StringBuffer existIDs=new StringBuffer("");
				for(int i=0;i<existStandardIds.size();i++)
				{
					LazyDynaBean abean=(LazyDynaBean)existStandardIds.get(i);
					String id=(String)abean.get("id");
					boolean _flag=false;
					for(int j=0;j<ids.length;j++){
						if(id.equals(ids[j])){//a_ids.indexOf("/"+id)这种方式如果拿/12和1或者2比较的话就不对了，所以用equals zhaoxg 2014-1-6
							_flag=true;
							break;
						}
					}
					if(!_flag){
						whl.append(","+id);					
					}
//					if(a_ids.indexOf("/"+id)==-1)
//					{
//						whl.append(","+id);
//					}
					existIDs.append(","+id);
				}	
				FormulaBo bo = new FormulaBo();
				String pkg=bo.pkgId(conn, "");
				StringBuffer in_str=new StringBuffer();
				//删除不用的工资标准数据
				if(whl.length()>0)
				{
					String[] newwhl=whl.substring(1).toString().split(",");
					int zheng = newwhl.length/999;
					int yu = newwhl.length%999;
					in_str.append("( ");
					for(int j=0;j<zheng;j++){
						if(j!=0){
							in_str.append("or ");
						}
						in_str.append("id in (");
						for(int i=j*999;i<(j+1)*999;i++){
							if(i!=j*999){
								in_str.append(",");
							}
							in_str.append(newwhl[i]);
						}
						in_str.append(")");
					}
					if(zheng==0){
						if(yu>0){
							in_str.append(" id in (");
							for(int i=zheng*999;i<zheng*999+yu;i++){
								if(i!=zheng*999){
									in_str.append(",");
								}
								in_str.append(newwhl[i]);
							}
							in_str.append(")");
						}
					}else{
						if(yu>0){
							in_str.append("or id in (");
							for(int i=zheng*999;i<zheng*999+yu;i++){
								if(i!=zheng*999){
									in_str.append(",");
								}
								in_str.append(newwhl[i]);
							}
							in_str.append(")");
						}
					}

					in_str.append(")");
					dao.delete("delete from gz_stand_history where pkg_id="+pkg_id+" and  "+in_str+"",new ArrayList());
					dao.delete("delete from gz_item_history where pkg_id="+pkg_id+" and  "+in_str+"",new ArrayList());	
					if(flag==1)
						dao.update("update gz_stand set flag=0 where "+in_str+"");
				
				}
				//新添工资标准
				whl.setLength(0);
				in_str.setLength(0);
				String[] temp=existIDs.toString().split(",");
				for(int i=0;i<ids.length;i++)
				{
					boolean _flag=false;
					for(int j=0;j<temp.length;j++){
						if(ids[i].equals(temp[j])){//zhaoxg 2014-1-6 原因同上
							_flag=true;
						}
					}
					if(!_flag){
						whl.append(","+ids[i]);					
					}
//					if(existIDs.indexOf("/"+ids[i])==-1)
//						whl.append(","+ids[i]);
					
				}
				if(whl.length()>0)
				{
						String[] newwhl=whl.substring(1).toString().split(",");
						int zheng = newwhl.length/999;
						int yu = newwhl.length%999;
						in_str.append("( ");
						for(int j=0;j<zheng;j++){
							if(j!=0){
								in_str.append("or ");
							}
							in_str.append("id in (");
							for(int i=j*999;i<(j+1)*999;i++){
								if(i!=j*999){
									in_str.append(",");
								}
								in_str.append(newwhl[i]);
							}
							in_str.append(")");
						}
						if(zheng==0){
							if(yu>0){
								in_str.append(" id in (");
								for(int i=zheng*999;i<zheng*999+yu;i++){
									if(i!=zheng*999){
										in_str.append(",");
									}
									in_str.append(newwhl[i]);
								}
								in_str.append(")");
							}
						}else{
							if(yu>0){
								in_str.append("or id in (");
								for(int i=zheng*999;i<zheng*999+yu;i++){
									if(i!=zheng*999){
										in_str.append(",");
									}
									in_str.append(newwhl[i]);
								}
								in_str.append(")");
							}
						}

						in_str.append(")");
					StringBuffer sql=new StringBuffer(" insert into gz_stand_history (id,pkg_id,name,s_hfactor,hfactor,hcontent,s_vfactor,vfactor,vcontent,item,createtime )  ");
					sql.append(" select id,"+pkg_id+",name,s_hfactor,hfactor,hcontent,s_vfactor,vfactor,vcontent,item,"+Sql_switcher.sqlNow()+" from gz_stand where "+in_str+"");
					dao.update(sql.toString());
					sql.setLength(0);
					sql.append("insert into gz_item_history (id,pkg_id,hvalue,vvalue,s_hvalue,s_vvalue,standard) select id,"+pkg_id+", hvalue,vvalue,s_hvalue,s_vvalue,standard from ");
					sql.append(" gz_item_history where "+in_str+" and pkg_id="+pkg);
					dao.update(sql.toString());
					if(flag==1)
						dao.update("update gz_stand set flag=1 where "+in_str+"");
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	
	
	/**
	 * 取得工资标准列表
	 * @param pkg_id 包id
	 * @param flag  1:工资标准包里的标准  2：不在工资标准包里的标准
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getStandardList(String pkg_id,int flag)throws GeneralException
	{
		ArrayList standardList=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String sql="";
			if(flag==1)
			{
				sql="select id,name from gz_stand_history where pkg_id="+pkg_id;
			}
			else if(flag==2)
			{
				sql="select id,name from gz_stand where id not in (select id  from gz_stand_history where pkg_id="+pkg_id+")";
			}
			RowSet rowSet=dao.search(sql);
			while(rowSet.next())
			{
				LazyDynaBean abean=new LazyDynaBean();
				abean.set("id",rowSet.getString("id"));
				abean.set("name",rowSet.getString("name"));
				standardList.add(abean);
				
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return standardList;
	}
	
	
	/**
	 * 保存新建历史沿革
	 * @throws GeneralException
	 */
	public void saveStandardPackage(String startDate,String packName,String isStart,String[] newStandards)throws GeneralException
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
		
           //	取得pkg_id
			IDGenerator idg = new IDGenerator(2, this.conn);
			String pkg_id = idg.getId("gz_stand_pkg.pkg_id");
			
			RecordVo vo=new RecordVo("gz_stand_pkg");
			vo.setInt("pkg_id",Integer.parseInt(pkg_id));
			vo.setString("name",packName);	
			Calendar d=Calendar.getInstance();
			String[] temps=null;
			if(startDate.indexOf("-")!=-1)
				temps=startDate.split("-");
			else
				temps=startDate.split("\\.");
			
			int year=Integer.parseInt(temps[0]);
			int month=Integer.parseInt(temps[1])-1;
			int day=Integer.parseInt(temps[2]);
			d.set(Calendar.YEAR,year);
			d.set(Calendar.MONTH,month);
			d.set(Calendar.DAY_OF_MONTH,day);
			
			vo.setDate("start_date",d.getTime());
			vo.setString("status","0");
			dao.addValueObject(vo);
			FormulaBo bo = new FormulaBo();
			String pkg=bo.pkgId(conn, "");
			StringBuffer in_str=new StringBuffer("");
			if(newStandards!=null&&newStandards.length>0)
			{
				int zheng = newStandards.length/999;
				int yu = newStandards.length%999;
				in_str.append("( ");
				for(int j=0;j<zheng;j++){
					if(j!=0){
						in_str.append("or ");
					}
					in_str.append("id in (");
					for(int i=j*999;i<(j+1)*999;i++){
						if(i!=j*999){
							in_str.append(",");
						}
						in_str.append(newStandards[i]);
					}
					in_str.append(")");
				}
				if(zheng==0){
					if(yu>0){
						in_str.append(" id in (");
						for(int i=zheng*999;i<zheng*999+yu;i++){
							if(i!=zheng*999){
								in_str.append(",");
							}
							in_str.append(newStandards[i]);
						}
						in_str.append(")");
					}
				}else{
					if(yu>0){
						in_str.append("or id in (");
						for(int i=zheng*999;i<zheng*999+yu;i++){
							if(i!=zheng*999){
								in_str.append(",");
							}
							in_str.append(newStandards[i]);
						}
						in_str.append(")");
					}
				}

				in_str.append(")");
//				if(newStandards.length>999){
//					for(int i=0;i<newStandards.length;i++){
//						in_str.append(","+newStandards[i]);
//					}
//				}else{
//					
//				}

				DbWizard dbw=new DbWizard(this.conn);
				String str="";
				if(dbw.isExistField("gz_stand_history","createorg",false))
					str+=",createorg";
				if(dbw.isExistField("gz_stand_history","b0110",false))
					str+=",b0110";
				 
				StringBuffer sql=new StringBuffer(" insert into gz_stand_history (id,pkg_id,name,s_hfactor,hfactor,hcontent,s_vfactor,vfactor,vcontent,item,createtime"+str+" )  ");
				sql.append(" select id,"+pkg_id+",name,s_hfactor,hfactor,hcontent,s_vfactor,vfactor,vcontent,item,"+Sql_switcher.sqlNow()+str+" from gz_stand_history where "+in_str+"  and pkg_id="+pkg);
				dao.update(sql.toString());
				sql.setLength(0);
				sql.append("insert into gz_item_history (id,pkg_id,hvalue,vvalue,s_hvalue,s_vvalue,standard) select id,"+pkg_id+", hvalue,vvalue,s_hvalue,s_vvalue,standard from ");
				sql.append(" gz_item_history where "+in_str+" and pkg_id="+pkg);
				dao.update(sql.toString());
			}
			
			
			if("1".equals(isStart))
			{
				startSalaryStandardPack(pkg_id,startDate);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	
	/**
	 * 取得当前历史沿革标准列表
	 * @return
	 */
	public ArrayList GetCurrentStandardList()throws GeneralException
	{
		ArrayList standardList=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select id,name from gz_stand_history where pkg_id=(select pkg_id from gz_stand_pkg where status=1)");
			while(rowSet.next())
			{
				LazyDynaBean abean=new LazyDynaBean();
				abean.set("id",rowSet.getString("id"));
				abean.set("name",rowSet.getString("name"));
				standardList.add(abean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return standardList;
	}
	
	
	/**
	 * 历史沿革是否包含工资标准
	 * @param pkg_id
	 * @return
	 * @throws GeneralException
	 */
	public boolean isContainSalaryStandard(String pkg_id)throws GeneralException
	{
		boolean  isContain=false;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select count(id) from gz_stand_history where pkg_id="+pkg_id);
			if(rowSet.next())
			{
				if(rowSet.getInt(1)!=0)
					isContain=true;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return isContain;
		
	}
	
	
	/**
	 * 启动 工资标准包
	 * @param pkg_id
	 * @param startDate
	 * @throws GeneralException
	 */
	public void startSalaryStandardPack(String pkg_id,String startDate)throws GeneralException
	{
		
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			Calendar d=Calendar.getInstance();
			d.add(Calendar.DAY_OF_MONTH,-1);
			ArrayList paramList = new ArrayList();
			String sql = "update gz_stand_pkg set end_date=?,status=? where status=1";
			paramList.add(new Date(d.getTimeInMillis()));
			paramList.add(0);
			dao.update(sql,paramList);
			
			d=Calendar.getInstance();
			String[] temps=null;
			if(startDate.indexOf("-")!=-1)
				temps=startDate.split("-");
			else
				temps=startDate.split("\\.");
			
			int year=Integer.parseInt(temps[0]);
			int month=Integer.parseInt(temps[1])-1;
			int day=Integer.parseInt(temps[2]);
			d.set(Calendar.YEAR,year);
			d.set(Calendar.MONTH,month);
			d.set(Calendar.DAY_OF_MONTH,day);
			
			RecordVo vo=new RecordVo("gz_stand_pkg");
			vo.setInt("pkg_id",Integer.parseInt(pkg_id));
			RecordVo a_vo=dao.findByPrimaryKey(vo);
			a_vo.setDate("start_date",d.getTime());
			a_vo.setString("end_date",null);
			a_vo.setInt("status",1);
			dao.updateValueObject(a_vo);
			
			importStartData(pkg_id);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	/**
	 * 导入启用的包标准数据
	 * @param pkg_id
	 */
	public void importStartData(String pkg_id)throws GeneralException
	{
		try
		{
		//	Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
		//	String unit_type=sysbo.getValue(Sys_Oth_Parameter.UNITTYPE);
			
			ContentDAO dao=new ContentDAO(this.conn);
			dao.delete("delete from gz_stand where id in (select  id from gz_stand_history where pkg_id="+pkg_id+")",new ArrayList());
			dao.delete("delete from gz_item where id in (select distinct id from gz_item_history where pkg_id="+pkg_id+")",new ArrayList());
			
			String sql="insert into gz_stand (id,name,unit_type,flag,hfactor,hcontent,vfactor,vcontent,item,s_vfactor,s_hfactor)"
				+" select id,name,'',1,hfactor,hcontent,vfactor,vcontent,item,s_vfactor,s_hfactor from gz_stand_history where pkg_id="+pkg_id;
			dao.update(sql);
			sql="insert into gz_item (id,hvalue,vvalue,standard,s_hvalue,s_vvalue) "
				+" select id,hvalue,vvalue,standard,s_hvalue,s_vvalue from gz_item_history where pkg_id="+pkg_id;
			dao.update(sql);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	
	
	
	/**
	 * 取得工资标准包列表
	 * @return
	 */
	public ArrayList getSalaryStandardPackList()throws GeneralException
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select * from gz_stand_pkg order by start_date desc");
			SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
			while(rowSet.next())
			{
				LazyDynaBean abean=new LazyDynaBean();
				abean.set("pkg_id",rowSet.getString("pkg_id"));
				abean.set("name",rowSet.getString("name"));
				if(rowSet.getDate("start_date")!=null)
					abean.set("start_date",df.format(rowSet.getDate("start_date")));
				else
					abean.set("start_date","");
				if(rowSet.getDate("end_date")!=null)
					abean.set("end_date",df.format(rowSet.getDate("end_date")));
				else
					abean.set("end_date","");
				abean.set("status",rowSet.getString("status"));
				list.add(abean);
			}
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		
		return list;
	}
	
	

}
