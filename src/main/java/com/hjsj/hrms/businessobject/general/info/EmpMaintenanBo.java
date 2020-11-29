package com.hjsj.hrms.businessobject.general.info;

import com.hjsj.hrms.businessobject.general.inform.CommonSql;
import com.hjsj.hrms.businessobject.gz.sort.SortBo;
import com.hjsj.hrms.businessobject.org.gzdatamaint.GzDataMaintBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
/**
 *<p>Title:SearchDataTableTrans</p> 
 *<p>Description:人员管理</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-9-4:下午02:03:54</p> 
 *@author FengXiBin
 *@version 4.0
 */
public class EmpMaintenanBo {
	private Connection conn;
	public EmpMaintenanBo(Connection conn)
	{
		this.conn = conn;
	}
	/**
	 * 显示＆隐藏指标
	 * @param fieldstr
	 * @param fieldlist
	 * @param uv
	 * @param setname
	 * @return
	 */
	public void dishidefield(String fieldstr,String setname)
	{
		String[] fields = null;
		try
		{
			GzDataMaintBo gzbo = new GzDataMaintBo(this.conn);
			FieldSet fieldset=DataDictionary.getFieldSetVo(setname);
			ContentDAO dao = new ContentDAO(this.conn);
			fields = fieldstr.split("/");
			ArrayList list = new ArrayList();
			for(int i=0;i<fields.length;i++)
			{
				String[] field = null;
				String fieldtemp= fields[i].toString();
				field = fieldtemp.split(",");
				String fieldname  = field[0].toString();
				String flag = field[1].toString();
				flag= "0".equals(flag)?"14":"0";
				if(fieldset.isMainset()&& "A01".equalsIgnoreCase(setname)){
					if("B0110".equalsIgnoreCase(fieldname)){
						gzbo.setValues("UNIT_LEN",flag);
						continue;
					}if("E01A1".equalsIgnoreCase(fieldname)){
						gzbo.setValues("POS_LEN",flag);
						continue;
					}
				}
				ArrayList itemlist = new ArrayList();
				itemlist.add(flag);
				itemlist.add(fieldname.toUpperCase());
				list.add(itemlist);
			}
			StringBuffer strsql = new StringBuffer();
			strsql.append("update fielditem set displaywidth = ? where itemid = ?");
			dao.batchUpdate(strsql.toString(),list);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 显示＆隐藏指标
	 * @param fieldstr
	 * @param fieldlist
	 * @param uv
	 * @param setname
	 * @return
	 */
	public void updateDishidefield(String fieldstr,String setname)
	{
		String[] fields = null;
		try
		{
			GzDataMaintBo gzbo = new GzDataMaintBo(this.conn);
			FieldSet fieldset=DataDictionary.getFieldSetVo(setname);
			ContentDAO dao = new ContentDAO(this.conn);
			fields = fieldstr.split("/");
			ArrayList list = new ArrayList();
			for(int i=0;i<fields.length;i++)
			{
				String[] field = null;
				String fieldtemp= fields[i].toString();
				field = fieldtemp.split(",");
				String fieldname  = field[0].toString();
				String flag = field[1].toString();
				flag=flag!=null&&flag.trim().length()>0?flag:"14";
				if(fieldset.isMainset()&& "A01".equalsIgnoreCase(setname)){
					if("B0110".equalsIgnoreCase(fieldname)){
						gzbo.setValues("UNIT_LEN",flag);
						continue;
					}if("E01A1".equalsIgnoreCase(fieldname)){
						gzbo.setValues("POS_LEN",flag);
						continue;
					}
				}
				ArrayList itemlist = new ArrayList();
				itemlist.add(flag);
				itemlist.add(fieldname.toUpperCase());
				list.add(itemlist);
			}
			StringBuffer strsql = new StringBuffer();
			strsql.append("update fielditem set displaywidth = ? where itemid = ?");
			dao.batchUpdate(strsql.toString(),list);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 隐藏字段,更新fielditem表
	 * @param itemid
	 * @param dao
	 */
	public void hidefield(String itemid,ContentDAO dao)
	{
		try
		{
			String sql = "update fielditem set displaywidth = 0 where itemid = '"+itemid.toUpperCase()+"'";
			dao.update(sql);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 显示字段,更新fielditem表
	 * @param itemid
	 * @param dao
	 */
	public void displayfield(String itemid,ContentDAO dao)
	{
		try
		{
			String sql = "update fielditem set displaywidth = 12 where itemid = '"+itemid.toUpperCase()+"'";
			dao.update(sql);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 拆分字符串
	 * @param str
	 * @return
	 */
	public String[] getStringArr (String str)
	{
		String[] Stringarr = null;
		int tempnum = str.split(",").length;
		if(tempnum>0)
		{
			Stringarr = str.split(",");
		}
		return Stringarr;
	}
	/**
	 * 指标排序
	 * @param sortfields
	 */
	public void sortfield(String[] sortfields,String setname)
	{
		try
		{
			GzDataMaintBo gzbo = new GzDataMaintBo(this.conn);
			FieldSet fieldset=DataDictionary.getFieldSetVo(setname);
			ContentDAO dao = new ContentDAO(this.conn);
			String sql = "update fielditem set displayid =? where itemid =?";
			ArrayList itemlist = new ArrayList();
			for(int i=0;i<sortfields.length;i++){
				String itemid = sortfields[i].toUpperCase().toString();
				if(itemid!=null&&itemid.trim().length()>0){
					int t = i+1;	
					if(fieldset.isMainset()&& "A01".equalsIgnoreCase(setname)){
						if("B0110".equalsIgnoreCase(itemid)){
							gzbo.setValues("UNIT_DISPLAYID",t+"");
							continue;
						}if("E01A1".equalsIgnoreCase(itemid)){
							gzbo.setValues("POS_DISPLAYID",t+"");
							continue;
						}
					}
					ArrayList list = new ArrayList();
					list.add(t+"");
					list.add(itemid);
					itemlist.add(list);
				}	
			}
			dao.batchUpdate(sql,itemlist);
			DataDictionary.refresh();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	
	/**
	 * 获得排序后面的sql
	 * @param sort_fields
	 * @return
	 */
	public String getorderbystr(String sort_fields) 
	{
		StringBuffer fieldsb = new StringBuffer();
		if(sort_fields!=null)
		{
			String[] temps = sort_fields.split("`");
			for(int i=0;i<temps.length;i++)
			{
				String[] arr = temps[i].split(":");
				String sortmode = "";
				if("1".equalsIgnoreCase(arr[2]))
				{
					sortmode = "asc";
				}else{
					sortmode = "desc";
				}
				fieldsb.append(","+arr[0]+" "+sortmode+" ");
			}
		}
		return fieldsb.substring(1).toString();
	}
	public String getTable(String field)
	{
		String ret = "";
		
		return ret;
	}
	/**
	 * 更新A0000
	 * @param sql
	 * @param dbname
	 */
	public void sortemp(String sql,String dbname)
	{
		
		RowSet rs;
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
//				System.out.println(sql);
				rs = dao.search(sql);
				int i=1;
				while(rs.next())
				{
					String a0100 = rs.getString("a0100");
					StringBuffer updatesb = new StringBuffer();
					updatesb.append(" update "+dbname+"a01 set a0000 = "+i*10+" where ");
					updatesb.append(" a0100="+a0100);				
//					System.out.println(updatesb.toString());
					dao.update(updatesb.toString());
					i++;	
				
				}

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 获得排序后面的字符串
	 * @param sort_fields
	 * @return
	 */
	public String getoMianOrderbyStr(String sort_fields) 
	{
		StringBuffer fieldsb = new StringBuffer();
		if(sort_fields!=null && sort_fields.length()>0) 
		{
			String[] temps = sort_fields.split("`");
			for(int i=0;i<temps.length;i++)
			{
				if(!(temps[i]==null || "".equals(temps[i])))
				{
					String[] arr = temps[i].split(":");
					if(!(arr[0]==null || "".equals(arr[0])))
					{
						String sortmode = "";
						if(!(arr[0]==null || "".equals(arr[0])))
						{
							FieldItem fi = DataDictionary.getFieldItem(arr[0]);
							if(!"M".equalsIgnoreCase(fi.getItemtype()))
							{
								if("1".equalsIgnoreCase(arr[2]))
								{
									sortmode = "asc";
								}else{
									sortmode = "desc";
								}
								fieldsb.append(";"+arr[0]+","+sortmode+" ");
							}
							
						}		
					}					
				}				
			}	
			if(fieldsb.length()>0) {
                return fieldsb.substring(1).toString();
            } else {
                return null;
            }
		}else {
            return null;
        }
	}
	/**
	 * 主集排序
	 * @param orderstr
	 * @param dbname
	 */
	public void sortMainTable(String orderstr,String dbname)
	{
		/**
		 * 因为前台的SQL语句是select from a01 left join a04(子表)
		 * 并且是 order by A0000,
		 * 所以只要修改 a01 的 A0000, 就也能给 a04(子表) 排序
		 */
		StringBuffer sb = new StringBuffer();
		sb.append(" select * From "+dbname+"A01 A01 ");
		String[] temp = orderstr.split(";");
		StringBuffer ordersb = new StringBuffer();
		for(int i=0;i<temp.length;i++){
			String[] order = temp[i].split(",");
			String field = order[0];
			String sortMode = order[1];
			FieldItem fi = DataDictionary.getFieldItem(field);
			String tablename = fi.getFieldsetid();
			if(!(tablename==null || "a01".equalsIgnoreCase(tablename))){
				sb.append("  Left Join ");	
				sb.append(dbname+tablename+" "+tablename);
				sb.append(" ON A01.A0100="+tablename+".A0100");
				sb.append(" AND ("+tablename+".I9999 =");
				sb.append(" (select Max(I9999) From "+dbname+tablename);
				sb.append(" where A0100=A01.A0100)  ");
				sb.append(" OR "+tablename+".I9999 is null) ");
				ordersb.append(","+tablename+"."+field+" "+sortMode);
			}else if("a01".equalsIgnoreCase(tablename))
			{
				ordersb.append(","+tablename+"."+field+" "+sortMode);
			}
		}

		ordersb.append(",A01.A0000 asc");
		sb.append(" order by "+ordersb.substring(1).toString());
		try
		{
			RowSet rs;
			ContentDAO dao = new ContentDAO(this.conn);
//			System.out.println(sb.toString());
			rs = dao.search(sb.toString());
			int i=1;
			while(rs.next())
			{
				String a0100 = rs.getString("a0100");
				StringBuffer updatesb = new StringBuffer();
				updatesb.append(" update "+dbname+"a01 set a0000 = "+i+" where ");
				updatesb.append(" a0100="+a0100);				
//				System.out.println(updatesb.toString());
				dao.update(updatesb.toString());
				i++;	
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 主集排序
	 * @param uv
	 * @param orderstr
	 * @param dbname
	 * @param a_code
	 * @param result
	 */
	public void sortMainTable(UserView uv,String orderstr,String dbname){
		/**
		 * 因为前台的SQL语句是select from a01 left join a04(子表)
		 * 并且是 order by A0000,
		 * 所以只要修改 a01 的 A0000, 就也能给 a04(子表) 排序
		 */
		String a_code = uv.getManagePrivCode()+uv.getManagePrivCodeValue();
		StringBuffer sb = new StringBuffer();
		sb.append(" select A01.A0000,A01.A0100 From "+dbname+"A01 A01 ");
		String[] temp = orderstr.split(";");
		StringBuffer ordersb = new StringBuffer();
//		ordersb.append(" order by ");
		for(int i=0;i<temp.length;i++)
		{
			String[] order = temp[i].split(",");
			String field = order[0];
			String sortMode = order[1];
			FieldItem fi = DataDictionary.getFieldItem(field);
			String tablename = fi.getFieldsetid();
			if(!(tablename==null || "a01".equalsIgnoreCase(tablename)))
			{
				sb.append("  Left Join ");	
				sb.append(dbname+tablename+" "+tablename);
				sb.append(" ON A01.A0100="+tablename+".A0100");
				sb.append(" AND ("+tablename+".I9999 =");
				sb.append(" (select Max(I9999) From "+dbname+tablename);
				sb.append(" where A0100=A01.A0100)  ");
				sb.append(" OR "+tablename+".I9999 is null) ");
				ordersb.append(","+tablename+"."+field+" "+sortMode);
			}else if("a01".equalsIgnoreCase(tablename))
			{
				ordersb.append(","+tablename+"."+field+" "+sortMode);
			}
		}
		StringBuffer wehrestr = new StringBuffer();
		if(a_code!=null&&a_code.trim().length()>2){
			wehrestr.append(" where A01.A0100 in(");
			wehrestr.append(CommonSql.whereCodeStr(uv,a_code,dbname,"A0100"));
			wehrestr.append(")");
		}
		ordersb.append(",A01.A0000 asc");
		sb.append(" order by "+ordersb.substring(1).toString());
		try{
			RowSet rs;
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sb.toString());
			ArrayList a0100list = new ArrayList();
			ArrayList a0000list = new ArrayList();
			while(rs.next()){
				String a0100 = rs.getString("A0100");
				String a0000 = rs.getString("A0000");
				a0100list.add(a0100);
				a0000list.add(a0000);
			}
			a0000list = SortBo.ascSort(a0000list);
			ArrayList listvalue = new ArrayList();
			for(int i=0;i<a0100list.size();i++){
				ArrayList list = new ArrayList();
				list.add(a0000list.get(i));
				list.add(a0100list.get(i));
				listvalue.add(list);
			}
			StringBuffer updatesb = new StringBuffer();
			updatesb.append(" update ");
			updatesb.append(dbname);
			updatesb.append("a01 set a0000 =? where ");
			updatesb.append(" a0100=?");	
			dao.batchUpdate(updatesb.toString(), listvalue);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * 更新子集当前记录
	 * @param orderstr
	 * @param a0100
	 * @param dbname
	 */
	public void sortSubsetTable(String orderstr,String a0100,String dbname)
	{
		/**
		 * 只更新 a04(子表) 的当前被选中a0100的人的 i9999;
		 */
		StringBuffer sb = new StringBuffer();
		String[] temp = orderstr.split(";");
		StringBuffer ordersb = new StringBuffer();
//		ordersb.append(" order by ");
		String[] order = temp[0].split(",");
		String field = order[0];
		String sortMode = order[1];
		FieldItem fi = DataDictionary.getFieldItem(field);
		String tablename = fi.getFieldsetid();
		if(!(tablename==null || "a01".equalsIgnoreCase(tablename)))
		{
			sb.append(" select * From "+dbname+tablename);
			sb.append(" where a0100='"+a0100+"'");	
			sb.append(" ORDER BY A0100 asc, "+field+" "+sortMode);		
		}
		for(int i=1;i<temp.length;i++)
		{
			order = temp[i].split(",");
			field = order[0];
			sortMode = order[1];
			ordersb.append(","+field+" "+sortMode);
		}
		sb.append(ordersb.toString());
		try
		{
			RowSet rs;
			ContentDAO dao = new ContentDAO(this.conn);
			int maxI9999 = this.getMaxI9999(dbname+tablename,a0100,dao);
			if(maxI9999>0)
			{
				this.operI9999(dbname+tablename,a0100,maxI9999,dao);
//				System.out.println(sb.toString());
				rs = dao.search(sb.toString());
				int i=1;
				while(rs.next())
				{
					int i9999 = rs.getInt("I9999");
					StringBuffer updatesb = new StringBuffer();
					updatesb.append(" update "+dbname+tablename+" set I9999 = "+i+" where ");
					updatesb.append(" a0100='"+a0100+"' and I9999 = "+i9999);				
//					System.out.println(updatesb.toString());
					dao.update(updatesb.toString());
					i++;	
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 取最大I9999
	 * @param table
	 * @param a0100
	 * @param dao
	 * @return
	 */
	public int getMaxI9999(String table,String a0100,ContentDAO dao)
	{
		RowSet rs;
		StringBuffer sb = new StringBuffer();
		int retint = 0;
		sb.append(" select max(i9999) as i9999 from "+table);
		sb.append(" where a0100='"+a0100+"'");
//		System.out.println(sb.toString());
		try
		{
			rs = dao.search(sb.toString());
			if(rs.next())
			{
				retint = rs.getInt("i9999");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return retint;
	}
	/**
	 * 取最大I9999
	 * @param table
	 * @param dao
	 * @return
	 */
	public int getMaxI9999(String table,ContentDAO dao)
	{
		RowSet rs;
		StringBuffer sb = new StringBuffer();
		int retint = 0;
		sb.append(" select max(i9999) as i9999 from "+table);
//		System.out.println(sb.toString());
		try
		{
			rs = dao.search(sb.toString());
			if(rs.next())
			{
				retint = rs.getInt("i9999");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return retint;
	}
	/**
	 * 更新I9999
	 * @param table
	 * @param a0100
	 * @param maxI9999
	 * @param dao
	 */
	public void operI9999(String table,String a0100,int maxI9999,ContentDAO dao)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(" update  "+table+" set i9999=i9999+"+maxI9999+" ");
		sb.append(" where a0100='"+a0100+"'");
//		System.out.println(sb.toString());
		try
		{
			dao.update(sb.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * 更新I9999
	 * @param table
	 * @param maxI9999
	 * @param dao
	 */
	public boolean checkResult(String usrname,String dbname)
	{
		boolean check=false;
		ContentDAO dao = new ContentDAO(conn);
		StringBuffer sb = new StringBuffer();
		sb.append(" select  * from "+usrname+dbname+"result");
		try
		{
			dao.search(sb.toString());
			check=true;
		}catch(Exception e){
			check=false;
		}
		return check;
	}
	/**
	 * 更新I9999
	 * @param table
	 * @param maxI9999
	 * @param dao
	 */
	public boolean checkResult(String temptable)
	{
		boolean check=false;
		ContentDAO dao = new ContentDAO(conn);
		StringBuffer sb = new StringBuffer();
		sb.append(" select  * from "+temptable);
		try
		{
			dao.search(sb.toString());
			check=true;
		}catch(Exception e){
			check=false;
		}
		return check;
	}
	/**
	 * 更新I9999
	 * @param table
	 * @param maxI9999
	 * @param dao
	 */
	public void operI9999(String table,int maxI9999,ContentDAO dao)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(" update  "+table+" set i9999=i9999+"+maxI9999+" ");
//		System.out.println(sb.toString());
		try
		{
			dao.update(sb.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * 更新子集所有记录
	 * @param orderstr
	 * @param a0100
	 * @param dbname
	 * @param username
	 */
	   public void sortSubsetTable(String orderstr,String a0100,String dbname,String username)
	    {
	        /**
	         * 更新 a04(子表) 的所有人的 i9999;
	         */
	        StringBuffer sb = new StringBuffer();
	        String[] temp = orderstr.split(";");
	        StringBuffer ordersb = new StringBuffer();
	        String[] order = temp[0].split(",");
	        String field = order[0];
	        String sortMode = order[1];
	        FieldItem fi = DataDictionary.getFieldItem(field);
	        String tablename = fi.getFieldsetid(); 
	        String subset = dbname+tablename;
	        try
	        {
	            DbWizard db = new DbWizard(this.conn);
	            ContentDAO dao = new ContentDAO(this.conn);
	            RowSet rs;
	            
	            /* 1. 更新子集 I9999 = I9999 + Max(I9999), 避免更新 I9999 时出现主键冲突 */            
	            int maxI9999 = this.getMaxI9999(subset,dao);
	            if(maxI9999 <= 0) {
                    return;
                }

	            this.operI9999(subset,maxI9999,dao);
	            
	            // 删除临时表
	            String temp_table1 = username+"_"+subset+"_t";
	            if(db.isExistTable(temp_table1, false)){
	                db.dropTable(temp_table1);
	            }
	            
	            /*
	             *2. 创建子集临时表，包含 AKey, I9999, NewI9999
	             *    newI9999: 新顺序号
	            */
	            String temp_table1_sql = "";
	            switch(Sql_switcher.searchDbServer())
	            {
	              case Constant.MSSQL:
	              {
	                  temp_table1_sql = "CREATE TABLE "+temp_table1+" (AKey varchar(50), I9999 int, newI9999 Int IDENTITY(1,1) PRIMARY KEY)";
	                  break;
	              }
	              case Constant.DB2:
	              {
	                  temp_table1_sql = "CREATE TABLE "+temp_table1+" (AKey varchar(50), I9999 int, newI9999 INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1))";
	                  break;
	              }
	              case Constant.ORACEL:
	              {
	                  temp_table1_sql = "CREATE TABLE "+temp_table1+" (AKey varchar(50), I9999 int, newI9999 Int)";
	                  break;
	              }
	            }           
	            dao.update(temp_table1_sql);
	            
	            if(!(tablename==null || "a01".equalsIgnoreCase(tablename)))
	            {
	                sb.append(" insert into "+temp_table1+" (AKey, I9999)"); 
	                sb.append(" select A0100, I9999 From "+subset);
	                sb.append(" ORDER BY A0100 asc, "+field+" "+sortMode);
	            }
	            
	            for(int i=1;i<temp.length;i++)
	            {
	                order = temp[i].split(",");
	                field = order[0];
	                sortMode = order[1];
	                ordersb.append(","+field+" "+sortMode);
	            }
	            sb.append(ordersb.toString());
	            dao.update(sb.toString());
	            
	            //oracle库newI9999无法自增，需要用行号更新一下
	            if(Constant.ORACEL == Sql_switcher.searchDbServer())
	            {
	                String sql = "UPDATE " + temp_table1 + " SET newI9999=ROWNUM";
	                dao.update(sql);
	            }
	            
	            /*
	             3. 创建子集更新临时表，包含要更新的 AKey, I9999

	                更新子集时，
	                a. 首先确定最大子集行数, 即按 A0100/B0110/E01A1 分组的最大分组行数
	                b. 从 subSetTemp 中，将每个人员/机构/职位 NewI9999 最小的记录，放到更新临时表中；
	                   更新在 strUpdTemp 中出现的子集 I9999 为 1;
	                   从 subSetTemp 中，将每个人员 NewI9999 最小的记录删除

	                c. 在循环中执行 b 操作，每次循环 I9999 依次累加，循环次数为最大子集行数
	            */
	            String temp_table2 = username+"_"+subset+"_update";
	            if(db.isExistTable(temp_table2, false)){
	                db.dropTable(temp_table2);
	            }
	            // 获得要循环的次数 
	            String getRecordNum = "select max(a) from (select count(*) as a from "+subset+" group by A0100) b ";
	            rs = dao.search(getRecordNum);
	            int num = 0;
	            if(rs.next())
	            {
	                num = rs.getInt(1);
	            }else {
                    return;
                }
	            
	            for(int i=1;i<=num;i++)
	            {
	                // 将子集临时表中每个人员的第 i 条记录，导入更新临时表
	                String s = "newI9999 = (SELECT MIN(newI9999) FROM " + temp_table1 + " A" +
	                    " WHERE " + temp_table1 + ".AKey = A.AKey)";
	                db.createTempTable(temp_table1, temp_table2, "AKey, I9999", s, "");
	                
	                // 更新子集
	                String sql2 = "";
//	                if(Constant.ORACEL == Sql_switcher.searchDbServer())
//	                {
//	                    sql2 = "Update "+subset+" Set "+subset+".I9999 = "+i+" From "+subset+" INNER JOIN "+temp_table2+" ON "+subset+".A0100 = "+temp_table2+".AKey and "+subset+".I9999 = "+temp_table2+".I9999";
//	                }else
//	                {
	                    sql2 = "Update "+subset+" Set "+subset+".I9999 = "+i+" where Exists (select * from "+temp_table2+" where "+temp_table2+".AKey = "+subset+".A0100 and "+temp_table2+".I9999 = "+subset+".I9999)";
//	                }
	                dao.update(sql2);
	                
	                // 删除子集临时表中第 i 条记录
	                s = "delete from " + temp_table1 +
	                     " where newI9999 = (SELECT MIN(newI9999) FROM " + temp_table1 + " A " +
	                                         " WHERE " + temp_table1 + ".AKey = A.AKey)";
	                dao.update(s);
	                
	                // 删除更新临时表
	                db.dropTable(temp_table2);
	            }
	            
	            /* 4.删除临时表 */
	            db.dropTable(temp_table1);
	        }
	        catch(Exception e)
	        {
	            e.printStackTrace();
	        }
	    }
	/**
	 * 求当前数据集的部分指标列表
	 * @param setname
	 * @return
	 */
	private ArrayList getFieldList(String setname)
	{
		FieldSet fieldset=DataDictionary.getFieldSetVo(setname);
		ArrayList fieldlist=new ArrayList();
		if(!fieldset.isMainset())
		{
			FieldItem tempitem=DataDictionary.getFieldItem("B0110");
			Field tempfield=tempitem.cloneField();
			tempfield.setReadonly(true);
			fieldlist.add(tempfield);
			
			tempitem=DataDictionary.getFieldItem("E0122");
			tempfield=tempitem.cloneField();
			tempfield.setReadonly(true);
			fieldlist.add(tempfield);
			
			tempitem=DataDictionary.getFieldItem("A0101");
			tempfield=tempitem.cloneField();
			tempfield.setReadonly(true);
			tempfield.setVisible(true);
			fieldlist.add(tempfield);
			
			tempfield=new Field("A0100","A0100");
			tempfield.setDatatype(DataType.STRING);
			tempfield.setLength(8);
			tempfield.setReadonly(true);			
			tempfield.setVisible(false);
			fieldlist.add(tempfield);
			
			tempfield=new Field("I9999","序号");
			tempfield.setDatatype(DataType.INT);
			tempfield.setReadonly(true);
			tempfield.setVisible(true);
			fieldlist.add(tempfield);
		}
		else
		{
			Field tempfield=new Field("A0100","A0100");
			tempfield.setDatatype(DataType.STRING);
			tempfield.setLength(8);
			tempfield.setVisible(false);
			fieldlist.add(tempfield);	
			/**有排序功能时，让其对A0000字段的值可维护,也即手动排序*/
			tempfield=new Field("A0000","序号");
			tempfield.setDatatype(DataType.INT);
			tempfield.setVisible(true);
			fieldlist.add(tempfield);
		}
		return fieldlist;
	}
	
}	