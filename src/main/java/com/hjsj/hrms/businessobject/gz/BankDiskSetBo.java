package com.hjsj.hrms.businessobject.gz;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.math.BigDecimal;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
/**
 * <p>Title:BankDiskSetBo.java</p>
 * <p>Descroption:银行报盘的一些方法</p>
 * <p>Company:HJSJ</p>
 * <p>Create time:2007.08.09 13:35:40 pm</p>
 * @author lizhenwei
 * @version 4.0
 */

public class BankDiskSetBo {
	private Connection conn;
	private UserView userview;
	private String salaryid="";
	public BankDiskSetBo(Connection conn){
		this.conn=conn;
	}
	public BankDiskSetBo(Connection conn,UserView userview){
		this.conn=conn;
		this.userview=userview;
	}
	
	public BankDiskSetBo(){
	
	}
	public int getItemid()
	{
		int n=0;
		try
		{
			String sql = "select MAX(item_id) as item_id from gz_bank_item ";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql);
			while(rs.next())
			{
				n=rs.getInt("item_id");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return (n+1);
	}
	/**
	 * 判断是否有银行模板
	 * @return
	 */
	public String getBankCount()
	{
		String count="1";
		try
		{
			String sql = "";
			if(this.userview.isSuper_admin()){
				sql = "select bank_id,bank_name from gz_bank order by bank_id";
			}else{
				sql = "select bank_id,bank_name from gz_bank ";
				sql+=" where  (scope=1 and username='"+this.userview.getUserName()+"') or (scope is null or scope=0) order by bank_id";
			}
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs=dao.search(sql);
			while(rs.next())
			{
				count="2";
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return count;
			
	}
	/**
	 * 取得代发银行模板列表
	 * @return
	 */
	public ArrayList getBankTemplatesList()
	{
		ArrayList list = new ArrayList();
		try
		{
			String sql = "";
			if(this.userview.isSuper_admin()){
				sql = "select bank_id,bank_name ,username from gz_bank ";
				sql+=" order by bank_id";
			}else{
				sql ="select bank_id,bank_name from gz_bank ";
				sql+=" where  (scope=1 and username='"+this.userview.getUserName()+"') or (scope is null or scope=0) order by bank_id";
			}
			
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs=dao.search(sql);
			while(rs.next())
			{
				if(this.userview.isSuper_admin()){
					String username=rs.getString("username");
					if(username==null|| "null".equalsIgnoreCase(username)){
						list.add(new CommonData(rs.getString("bank_id"),rs.getString("bank_name")));
					}else{
						list.add(new CommonData(rs.getString("bank_id"),rs.getString("bank_name")+"("+username+")"));
					}
					
				}else{
					list.add(new CommonData(rs.getString("bank_id"),rs.getString("bank_name")));
				}
				
			}
			list.add(new CommonData("#","<新建>"));
			if(list.size()==1)
			{
				list.add(0,new CommonData("*","     "));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
		
	}
	/**
	 * 代发银行要求的数据的列的信息(列名,类型,长度等)
	 * @param bank_id
	 * @param salaryid
	 * @param type 
	 * @return
	 */
	public ArrayList getBankItemInfo(String bank_id,String salaryid,int type,HashMap salarySetMap)
	{
		ArrayList list = new ArrayList();
		try
		{
			StringBuffer sql = new StringBuffer();
			if(type==1)
			{
			     sql.append("select g.*,s.decwidth from gz_bank_item g left join (select distinct itemid,decwidth from salaryset ) s on g.field_name=s.itemid where g.bank_id=");
			     sql.append(bank_id);
			     sql.append("  order by g.norder");
				
			}
			else
			if(type==3){
				sql.append("select g.*,s.decwidth from gz_bank_item g left join (select distinct itemid,decwidth from salaryset ) s on g.field_name=s.itemid where g.bank_id=");
			     sql.append("10000000");
			     sql.append("  order by g.norder");
			}else{
				sql.append(" select g.item_id,g.bank_id,g.item_type,g.field_name,g.field_default,g.format,s.itemdesc, g.item_name,s.decwidth from");
				sql.append(" gz_bank_item g,salaryset s where ");
				sql.append(" g.bank_id=");
				sql.append(bank_id);
				sql.append(" and g.field_name=s.itemid and s.salaryid=");
				sql.append(salaryid+" order by g.norder");
			}
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs=dao.search(sql.toString());
			HashMap map=new HashMap();
			while(rs.next())
			{
				if(salarySetMap.get(rs.getString("field_name").toUpperCase())!=null)
				{	
	    			LazyDynaBean bean = new LazyDynaBean();
		    		bean.set("item_id",rs.getString("item_id"));//pk 
		    		bean.set("bank_id",rs.getString("bank_id"));
		    		bean.set("itemdesc", "a0000".equalsIgnoreCase(rs.getString("field_name"))?"序号":rs.getString("item_name"));//lable
		    		if(rs.getInt("item_type")==0)
		    		{
		    		    bean.set("itemtype","数值型");
		    		    bean.set("item_type","N");
		    		}
		    		if(rs.getInt("item_type")==1)
		    		{
			    		bean.set("itemtype","日期型");
		    			 bean.set("item_type","D");
		    		}
		    		if(rs.getInt("item_type")==2)
		    		{
		    			bean.set("itemtype","字符型");
		    			 bean.set("item_type","A");
			    	}
				    bean.set("decwidth",(rs.getString("decwidth")==null|| "".equals(rs.getString("decwidth"))?"0":rs.getString("decwidth")));
		    		bean.set("itemid",rs.getString("field_name").toUpperCase());//column name
		    		
		    		if(map.get(rs.getString("field_name").toLowerCase())!=null)
		    		{
		    			continue; 
		    		}
		    	 
		    		map.put(rs.getString("field_name").toLowerCase(),"1");
		    		
		    		bean.set("itemlength",rs.getString("field_default"));
		    		bean.set("format",rs.getString("format")==null?"":rs.getString("format"));
		    		list.add(bean);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	public HashMap getDefault_length(String bank_id)
	{
		HashMap map = new HashMap();
		try
		{
			String sql = "select field_name,field_default from gz_bank_item where bank_id="+bank_id;
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs= null;
			rs=dao.search(sql);
			while(rs.next())
			{
				map.put(rs.getString("field_name").toUpperCase(),rs.getString("field_default"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 取得数据列表
	 * @param tableName
	 * @param a_code
	 * @param columns
	 * @return
	 */
	public ArrayList getPersonInfoList(String tableName,String a_code,ArrayList columns,ArrayList columnsInfo,HashMap map,HashMap lengthMap,String model,String spSQL)
	{
		ArrayList list = new ArrayList();
		try
		{ 
			if(columns==null||columns.size()==0)
				return list;
			StringBuffer colBuf=new StringBuffer();
			boolean flag=true;
			boolean a0000flag=true;
			boolean a00z1=true;
			StringBuffer codebuf = new StringBuffer("");
			for(int j=0;j<columns.size();j++)
			{
				colBuf.append(",");
				FieldItem fielditem = DataDictionary.getFieldItem(((String)columns.get(j)).toLowerCase());
				if(fielditem!=null&& "N".equalsIgnoreCase(fielditem.getItemtype())&&!"A00Z1".equalsIgnoreCase((String)columns.get(j)))
				{
					colBuf.append("sum("+(String)columns.get(j)+") as "+(String)columns.get(j));
				}else
				{
					colBuf.append("max("+(String)columns.get(j)+") as "+(String)columns.get(j));
				}
				if("a0100".equalsIgnoreCase((String)columns.get(j)))
					flag=false;
				if("a0000".equalsIgnoreCase((String)columns.get(j)))
					a0000flag=false;
				if("a00z1".equalsIgnoreCase((String)columns.get(j)))
					a00z1=false;
				String format =  (String)map.get(((String)columns.get(j)).toUpperCase());
				if(format.indexOf("#2")!=-1){
					if(fielditem!=null&& "A".equalsIgnoreCase(fielditem.getItemtype())&&!"0".equals(fielditem.getCodesetid())){
						String codeName="codeitem";
						if("UN".equalsIgnoreCase(fielditem.getCodesetid())|| "UM".equalsIgnoreCase(fielditem.getCodesetid())|| "@K".equalsIgnoreCase(fielditem.getCodesetid()))
						{
							codeName="organization";
						}
						colBuf.append(",max("+fielditem.getItemid()+"_code.corcode) as "+fielditem.getItemid()+"_corcode ");
						codebuf.append(" left join (select corcode,codeitemid from ");
						codebuf.append(codeName+" where UPPER(codesetid)='"+fielditem.getCodesetid().toUpperCase()+"') ");
						codebuf.append(fielditem.getItemid()+"_code on "+tableName+"."+fielditem.getItemid()+"="+fielditem.getItemid()+"_code.codeitemid ");
					}
				}
				
			}
			if(flag&&columns!=null&&columns.size()!=0)
				colBuf.append(" ,max(A0100) as A0100 ");
			colBuf.append(",max(nbase) as pre");
			if(a0000flag&&columns!=null&&columns.size()!=0)
			{
				colBuf.append(",max("+tableName+".a0000) as a0000");
			}
			if(a00z1&&columns!=null&&columns.size()!=0)
			{
				colBuf.append(",max(a00z1) as a00z1");
			}
			StringBuffer sql = new StringBuffer();
			sql.append("select ");
			sql.append(colBuf.toString().substring(1));
			sql.append(",max(dbid) as dbid from ");
			sql.append(tableName);
			if(codebuf.length()>0)
				sql.append(codebuf.toString());
			sql.append("  where 1=1");
			if(!(a_code==null|| "".equals(a_code)))
			{
				String code=a_code.substring(0,2);
				String codeValue=a_code.substring(2);
				String orgid ="";
				String deptid="";
				if(salaryid!=null&&salaryid.length()>0)
				{
					SalaryTemplateBo gbo=new SalaryTemplateBo(this.conn,Integer.parseInt(salaryid),this.userview);
					orgid = gbo.getCtrlparam().getValue(SalaryCtrlParamBo.SUM_FIELD,"orgid");
					orgid = orgid != null ? orgid : "";
					deptid = gbo.getCtrlparam().getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid");
					deptid = deptid != null ? deptid : "";
				}
				if(orgid.length()>0||deptid.length()>0)
				{
					if(orgid.length()>0&&deptid.length()>0&& "UN".equalsIgnoreCase(code))
					{
						sql.append(" and ("+orgid+" like '");
			    		sql.append(codeValue);
			    		sql.append("%'");
				    	if("".equals(codeValue))
				    		sql.append(" or "+orgid+" is null");
				    	sql.append(" or "+deptid+" like '");
			    		sql.append(codeValue);
			    		sql.append("%'");
				    	if("".equals(codeValue))
				    		sql.append(" or "+deptid+" is null");
				    	sql.append(")");
					}
					else if(orgid.length()>0&& "UN".equalsIgnoreCase(code))
					{
						sql.append(" and ("+orgid+" like '");
			    		sql.append(codeValue);
			    		sql.append("%'");
				    	if("".equals(codeValue))
				    		sql.append(" or "+orgid+" is null");
				    	sql.append(")");
					}
					else if(deptid.length()>0){
						sql.append(" and ("+deptid+" like '");
			    		sql.append(codeValue);
			    		sql.append("%'");
				    	if("".equals(codeValue))
				    		sql.append(" or "+deptid+" is null");
				    	sql.append(")");
					}
				}
				else
				{
			    	if("UM".equalsIgnoreCase(code))
		    		{
		    			sql.append(" and (e0122 like '");
			    		sql.append(codeValue);
			    		sql.append("%'");
				    	if("".equals(codeValue))
				    		sql.append(" or e0122 is null");
				    	sql.append(")");
				    }
		    		if("UN".equalsIgnoreCase(code))
			    	{
			     		sql.append(" and ( b0110 like '");
			     		sql.append(codeValue);
			    		sql.append("%'");
			    		if(codeValue==null|| "".equals(codeValue))
				    		sql.append(" or b0110 is null");
			    		sql.append(")");
	    			}
				}
			}
			if("1".equals(model))
				sql.append(" and "+spSQL);
			sql.append(" group by nbase,a0100,a00z0 order by dbid,a0000");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs= null;
			rs=dao.search(sql.toString());
			int x=0;
			while(rs.next())
			{
				x++;
				LazyDynaBean bean = new LazyDynaBean();
				for(int i=0;i<columnsInfo.size();i++)
				{
					LazyDynaBean abean=(LazyDynaBean)columnsInfo.get(i);
					int itemlength = Integer.parseInt(((String)abean.get("itemlength")));
					if("A0000".equalsIgnoreCase((String)abean.get("itemid")))
					{
						  if(map.get(((String)abean.get("itemid")).toUpperCase())!=null&&((String)map.get(((String)abean.get("itemid")))).trim().length()>0)
					         {
					              String temp=this.getNumberFormat(x+"", (String)map.get(((String)abean.get("itemid")).toUpperCase()),itemlength);
					              //temp=temp.replaceAll(",","");
					              bean.set((String)abean.get("itemid"),temp);
					         }
					         else
					         {
					        	 String temp= this.getNumberFormat(x+"", "0",itemlength);
					        	 bean.set((String)abean.get("itemid"),temp);
					        	//System.out.println(rs.getString((String)abean.get("itemid")));
					         }
					}
					else if("A".equalsIgnoreCase((String)abean.get("item_type"))&&!"0".equalsIgnoreCase((String)abean.get("codesetid")))
					{
						
						String format =  (String)map.get(((String)abean.get("itemid")).toUpperCase());
						String value="";
						if(format==null|| "#0".equals(format)|| "#".equals(format)|| "".equals(format)){
							value=AdminCode.getCodeName((String)abean.get("codesetid"),rs.getString((String)abean.get("itemid")));
							String codesetid=(String)abean.get("codesetid");
							if("UN".equalsIgnoreCase(codesetid)&&(value==null|| "".equals(value)))
							{
								value=AdminCode.getCodeName("UM",rs.getString((String)abean.get("itemid")));
							}
							if("UM".equalsIgnoreCase(codesetid)&&(value==null|| "".equals(value)))
							{
								value=AdminCode.getCodeName("UN",rs.getString((String)abean.get("itemid")));
							}
							bean.set((String)abean.get("itemid"),value);
						}else {
						     if("#1".equals(format))
						     {
						    	 value=rs.getString((String)abean.get("itemid"));
						    	 if(value==null)
						    		 value="";
						    	 bean.set((String)abean.get("itemid"),value);
						     }else if("#2".equals(format)){
						    	 value=rs.getString(((String)abean.get("itemid"))+"_corcode");
						    	 if(value==null)
						    		 value="";
						    	 bean.set((String)abean.get("itemid"),value);
						     }else{
						    	 boolean hasValue=true;
						    	 if(format.indexOf("#1")!=-1){
						    		 value=rs.getString((String)abean.get("itemid"));
						    		 if(value==null)
							    		 value="";
						    	     format=format.replaceAll("#1", "");
						    	     hasValue=false;
						    	 }
						    	 if(format.indexOf("#2")!=-1){
						             value=rs.getString(((String)abean.get("itemid"))+"_corcode");
						             if(value==null)
							    		 value="";
						    	     format=format.replaceAll("#2", "");
						    	     hasValue=false;
						    	 }
						    	 if(hasValue)
						    	 {
						    		 value=AdminCode.getCodeName((String)abean.get("codesetid"),rs.getString((String)abean.get("itemid")));
						    		 if(value==null)
							    		 value="";
						    	 }
						    	 
								String hh=(String)(lengthMap.get(((String)(abean.get("itemid")==null?"0":abean.get("itemid"))))==null?"0":lengthMap.get(((String)(abean.get("itemid")==null?"0":abean.get("itemid"))).toUpperCase()));
								value=this.getCharacterFormat(value, format, Integer.parseInt(hh));
							     bean.set((String)abean.get("itemid"),value);
						     }
						    
							
						}
						
					}
					
						//-----------------------------------------------------
					else if("N".equalsIgnoreCase((String)abean.get("item_type")))
						{
							         if(map.get(((String)abean.get("itemid")).toUpperCase())!=null&&((String)map.get(((String)abean.get("itemid")))).trim().length()>0)
							         {
							              String temp=this.getNumberFormat(rs.getString((String)abean.get("itemid")), (String)map.get(((String)abean.get("itemid")).toUpperCase()),itemlength);
							              //temp=temp.replaceAll(",","");
							              bean.set((String)abean.get("itemid"),temp);
							         }
							         else
							         {
							        	 String temp="";
							        	 if(rs.getString((String)abean.get("itemid"))!=null)
							        	 {
							        		 if(((String)abean.get("decwidth"))!=null&& "0".equals((String)abean.get("decwidth")))
							        		 {
							        			 temp= this.getNumberFormat(rs.getString((String)abean.get("itemid")), "0",itemlength);
							        		 }
							        		 else{
							        	    	 temp= this.getNumberFormat(rs.getString((String)abean.get("itemid")), "0.00",itemlength);
							        		 }
							        	 }
							        	 bean.set((String)abean.get("itemid"),temp);
							        	//System.out.println(rs.getString((String)abean.get("itemid")));
							         }
						}
						else if("D".equalsIgnoreCase((String)abean.get("item_type")))
						{
							 if(map.get(((String)abean.get("itemid")).toUpperCase())!=null&&!"".equals((String)map.get(((String)abean.get("itemid")).toUpperCase())))
							 {
								 if(Sql_switcher.searchDbServer() == Constant.ORACEL)
								 {
									 bean.set((String)abean.get("itemid"),getDateFormat((rs.getDate((String)abean.get("itemid"))==null?"":rs.getDate((String)abean.get("itemid")).toString()),(String)map.get(((String)abean.get("itemid")).toUpperCase())));
								 }
								 else
								 {
									 bean.set((String)abean.get("itemid"),getDateFormat(rs.getString((String)abean.get("itemid"))==null?"":rs.getString((String)abean.get("itemid")),(String)map.get(((String)abean.get("itemid")).toUpperCase())));
								 }
								
							 }
							 else
							 {
								 String temp="";
								 if(Sql_switcher.searchDbServer() == Constant.ORACEL)
								 {
									 if(rs.getTimestamp((String)abean.get("itemid"))!=null){
										 temp=rs.getTimestamp((String)abean.get("itemid")).toString();
									 }
								 } 
								 else
								 {
									  temp=rs.getString((String)abean.get("itemid"));
								 }
								
								 if(temp!=null&&!"".equals(temp))
								 {
									 if(temp.length()>10)
									 {
										 temp=temp.substring(0,10);
									 }
								 }
								 else
								 {
									 temp="";
								 }
								 bean.set((String)abean.get("itemid"),temp);
							 }
						}
						else if("A".equalsIgnoreCase((String)abean.get("item_type"))&& "0".equalsIgnoreCase((String)abean.get("codesetid")))
						{
							if(map.get(((String)abean.get("itemid")).toUpperCase())!=null&&((String)map.get(((String)abean.get("itemid")))).trim().length()>0)
							 {
								 String hh=(String)(lengthMap.get(((String)(abean.get("itemid")==null?"0":abean.get("itemid"))))==null?"0":lengthMap.get(((String)(abean.get("itemid")==null?"0":abean.get("itemid"))).toUpperCase()));
								 //value=this.getCharacterFormat(value, (String)map.get(((String)abean.get("itemid")).toUpperCase()), Integer.parseInt(hh));
								 bean.set((String)abean.get("itemid"),getCharacterFormat(rs.getString((String)abean.get("itemid"))==null?"":rs.getString((String)abean.get("itemid")),(String)map.get(((String)abean.get("itemid")).toUpperCase()),Integer.parseInt(hh)));
							 }
							 else
							 {
								 bean.set((String)abean.get("itemid"),rs.getString((String)abean.get("itemid"))==null?"":rs.getString((String)abean.get("itemid")));
							 }
							
						}
						else
						{
					          bean.set((String)abean.get("itemid"),rs.getString((String)abean.get("itemid"))==null?"":rs.getString((String)abean.get("itemid")));
						}
					
					if("N".equalsIgnoreCase((String)abean.get("item_type")))
					{
						bean.set("itemtype","N");
					}
					else
					{
						bean.set("itemtype","A");
					}
				}
				bean.set("pre",rs.getString("pre"));
				list.add(bean);
				
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 代发银行要显示的人员信息的列名
	 * @param bank_id
	 * @return
	 */
	public ArrayList getColumns(String bank_id,HashMap salarySetMap)
	{
		ArrayList list= new ArrayList();
		try
		{
			String sql = "select field_name from gz_bank_item where bank_id="+bank_id+" order by norder";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs=dao.search(sql);
			while(rs.next())
			{
				if(salarySetMap.get(rs.getString("field_name").toUpperCase())!=null)
			    	list.add(rs.getString("field_name"));
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	public HashMap getitemtype(String bank_id,HashMap salarySetMap)
	{
		HashMap map = new HashMap();
		try
		{
			String sql = "select field_name,item_type from gz_bank_item where bank_id="+bank_id+" order by norder";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs=dao.search(sql);
			while(rs.next())
			{
				if(salarySetMap.get(rs.getString("field_name").toUpperCase())!=null)
			    	map.put(rs.getString("field_name").toUpperCase(), rs.getString("item_type"));
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	public HashMap getItemFormatMap(String bank_id,HashMap salarySetMap)
	{
		HashMap map = new HashMap();
		try
		{
			String sql = "select field_name,format from gz_bank_item where bank_id="+bank_id+" order by norder";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs=dao.search(sql);
			while(rs.next())
			{
				if(salarySetMap.get(rs.getString("field_name").toUpperCase())!=null&&rs.getString("format")!=null&&rs.getString("format").length()>0)
			    	map.put(rs.getString("field_name").toUpperCase(), rs.getString("format"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 取得新建的银行模板的id
	 * @return
	 */
	public String getFirstBank_id()
	{
		String bank_id="*";
		try
		{
			String sql = "select bank_id from gz_bank ";
			if(this.userview.isSuper_admin()){
				sql+=" order by bank_id";
			}else{
				sql+=" where  (scope=1 and username='"+this.userview.getUserName()+"') or (scope is null or scope=0) order by bank_id";
			}
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs= null;
			rs=dao.search(sql);
			while(rs.next())
			{
				bank_id=rs.getString("bank_id");
				break;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return bank_id;
	}
	/**
	 * 取得新建的银行模板的id
	 * @return
	 */
	public ArrayList  getFirstScope(String id)
	{
		ArrayList bank_id=new ArrayList();
		try
		{
			String sql = "select scope,username from gz_bank ";
			sql+=" where bank_id="+id+"";	
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs= null;
			rs=dao.search(sql);
			if(rs.next())
			{
				String scope=rs.getString("scope");
				if(scope==null|| "null".equalsIgnoreCase(scope)||scope.trim().length()==0){
					scope="0";
				}
				bank_id.add(scope);
				
				String username=rs.getString("username");
				if(username==null|| "null".equalsIgnoreCase(username)||username.trim().length()==0){
					username=" ";
				}
				bank_id.add(username);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return bank_id;
	}
	/**
	 * 得到要显示的列名的列表,即已选的银行模板的项目
	 * @param bank_id
	 * @return
	 */
	public ArrayList getTemplateColumns(String bank_id,HashMap salarySetMap)
	{
		ArrayList list = new ArrayList();
		//item_name 汉字描述
		String sql = "select item_name,field_name ,item_type from gz_bank_item where bank_id='"+bank_id+"' order by norder";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try
		{
			rs=dao.search(sql);
			while(rs.next())
			{
				if(salarySetMap.get(rs.getString("field_name").toUpperCase())!=null)
				{
	    			LazyDynaBean bean= new LazyDynaBean();
		    		bean.set(rs.getString("field_name"),rs.getString("item_name"));
	    			if("0".equalsIgnoreCase(rs.getString("item_type")))
	    			{
	    				bean.set("item_type","N");
		    		}
		    		if("1".equalsIgnoreCase(rs.getString("item_type")))
		    		{
					bean.set("item_type","D");
		    		}
		    		if("2".equalsIgnoreCase(rs.getString("item_type")))
		    		{
			     		bean.set("item_type","A");
		    		}
			    	list.add(bean);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 取得薪资类别的所有指标
	 * @param salaryid
	 * @return
	 */
	public HashMap getSalarySetFields(String salaryid)
	{
		HashMap map = new HashMap();
		try
		{
			String sql = "select itemid from salaryset where salaryid="+salaryid;
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs= null;
			rs=dao.search(sql);
			while(rs.next())
			{
				map.put(rs.getString("itemid").toUpperCase(),rs.getString("itemid"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 取得银行模板的项目信息，当columnslist传null时得到所有，当columnsList不为null时得到已选的
	 * @param ArrayList columnsList
	 * @param String salaryid
	 * @return ArrayList
	 */
	public ArrayList getFieldInfoFromSalarySet(ArrayList columnsList,String salaryid,int type,HashMap salarySetMap)
	{
		ArrayList list = new ArrayList();
		StringBuffer buf = new StringBuffer();
		if(columnsList!=null&&columnsList.size()!=0)
		{
			boolean flag=true;
			boolean a0000flag=true;
			boolean a00z1=true;
		    for(int i=0;i<columnsList.size();i++)
		    {
			   
			    if(salarySetMap.get(((String)columnsList.get(i)).toUpperCase())==null)
			    	continue;
			    buf.append(",'");
			    buf.append(columnsList.get(i));
			    if("a0100".equalsIgnoreCase((String)columnsList.get(i)))
			    	flag=false;
			    if("a0000".equalsIgnoreCase((String)columnsList.get(i)))
			    	a0000flag=false;
			    if("a00z1".equalsIgnoreCase((String)columnsList.get(i)))
			    	a00z1=false;
			    buf.append("'");
		    }
		    if(flag)
		    	buf.append(",'A0100' ");
		    if(a0000flag)
		    	buf.append(",'A0000' ");
		    if(a00z1)
		    	buf.append(",'A00Z1' ");
		}
		StringBuffer sql = new StringBuffer();
		sql.append("select itemid,itemlength,decwidth,codesetid,itemdesc,sortid,itemtype from salaryset where salaryid='");
		sql.append(salaryid+"'");
		if(columnsList!=null&&columnsList.size()!=0)
		{
		    sql.append(" and itemid in (");
		    sql.append(buf.toString().substring(1));
		    sql.append(")");
		}
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try
		{
			rs=dao.search(sql.toString());
			while(rs.next())
			{
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("itemid",rs.getString("itemid").toUpperCase());
				bean.set("itemlength",rs.getString("itemlength"));
				bean.set("decwidth",rs.getString("decwidth"));
				bean.set("codesetid",rs.getString("codesetid"));
				bean.set("itemdesc", "a0000".equalsIgnoreCase(rs.getString("itemid"))?"序号":rs.getString("itemdesc"));
				bean.set("sortid",rs.getString("sortid"));
				if(type==1)
				{
			       	if("N".equalsIgnoreCase(rs.getString("itemtype")))
			       	{
				         bean.set("itemtype","数值型");
				         bean.set("item_type",rs.getString("itemtype"));
			       	}
			    	if("D".equalsIgnoreCase(rs.getString("itemtype")))
			    	{
			    		 bean.set("itemtype","日期型");
			    		 bean.set("item_type",rs.getString("itemtype"));
			    	}
			    	if("A".equalsIgnoreCase(rs.getString("itemtype"))|| "M".equalsIgnoreCase(rs.getString("itemtype")))
			    	{
				    	 bean.set("itemtype","字符型");
				    	 bean.set("item_type","A");
			    	}
				}
				if(type==2)
				{
					bean.set("item_type",rs.getString("itemtype"));
				}
				bean.set("format","");
				list.add(bean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	public ArrayList firstSaveItem(String itemids,String salaryid,ArrayList alist)
	{
		ArrayList list= new ArrayList();
		try
		{
			if(itemids==null|| "".equals(itemids))
			{
				return list;
			}
			String[] temp=itemids.split(",");
			StringBuffer str = new StringBuffer();
			for(int i=0;i<temp.length;i++)
			{
				boolean flag=false;
				for(int j=0;j<alist.size();j++)
				{
					
					LazyDynaBean bean = (LazyDynaBean)alist.get(j);
					if(temp[i].equalsIgnoreCase(((String)bean.get("itemid"))))
					{
						list.add(bean);
						flag=true;
						break;
					}
				}
				if(!flag)
				{
		    		str.append(",'");
		    		str.append(temp[i]);
				    str.append("'");
				}
			}
			if(str.length()>0)
			{
	    		StringBuffer sql = new StringBuffer();
	    		sql.append("select itemid,itemlength,decwidth,codesetid,itemdesc,sortid,itemtype from salaryset where salaryid='");
	    		sql.append(salaryid+"'");
	    	    sql.append(" and itemid in (");
	    		sql.append(str.toString().substring(1));
	    	    sql.append(")");
	    		ContentDAO dao = new ContentDAO(this.conn);
	    		RowSet rs = null;
	    		rs=dao.search(sql.toString());
	    		while(rs.next())
	    		{
		    		LazyDynaBean bean = new LazyDynaBean();
		    		bean.set("itemid",rs.getString("itemid"));
		    		bean.set("itemlength",rs.getString("itemlength"));
		    		bean.set("decwidth",rs.getString("decwidth"));
		    		bean.set("codesetid",rs.getString("codesetid"));
		    		bean.set("itemdesc", "a0000".equalsIgnoreCase(rs.getString("itemid"))?"序号":rs.getString("itemdesc"));
		    		bean.set("sortid",rs.getString("sortid"));
		    		String dd=rs.getString("itemtype");
		    		if("N".equalsIgnoreCase(dd))
		    		{
		    		    bean.set("itemtype","数值型");
		    		    bean.set("item_type","N");
		    		}
		    		if("D".equalsIgnoreCase(dd))
		    		{
			    		bean.set("itemtype","日期型");
		    			 bean.set("item_type","D");
		    		}
		    		if("A".equalsIgnoreCase(dd))
		    		{
		    			bean.set("itemtype","字符型");
		    			 bean.set("item_type","A");
			    	}
		    		bean.set("format","");
		    		list.add(bean);
		    	}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
		
	}
	public String getMaxBank_id()
	{
		String max_id = "";
		String sql = "select MAX(bank_id) from gz_bank ";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs= null;
		try
		{
			rs = dao.search(sql);
			while(rs.next())
			{
				max_id=rs.getString(1);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return max_id;
		
	}
	/***
	 * 已选的显示列名的列表(不使用前面的getTemplateColumns,因为要在前台展现为select)
	 * @param String bank_id
	 * @return ArrayList
	 */
	public ArrayList getSelectedItemList(String bank_id)
	{
		ArrayList list = new ArrayList();
		try
		{
			String sql = "select item_name,field_name from gz_bank_item where bank_id='"+bank_id+"' order by norder";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs= null;
			rs=dao.search(sql);
			while(rs.next())
			{
				list.add(new CommonData(rs.getString(2),rs.getString(1)));
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * dml 2011年8月29日16:24:39
	 * 如果在修改银行报盘页面将所有指标删除会在调整顺序页面出现指标，原因
	 * salaryset表中不存在这些指标，在工资套中已经删除
	 * 
	 * */
	public ArrayList getSelectedItemList(String bank_id,HashMap salarySetMap)
	{
		ArrayList list = new ArrayList();
		try
		{
			String sql = "select item_name,field_name from gz_bank_item where bank_id='"+bank_id+"' order by norder";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs= null;
			rs=dao.search(sql);
			while(rs.next())
				
			{
				if(salarySetMap.get(rs.getString("field_name").toUpperCase())!=null)
				{	
					list.add(new CommonData(rs.getString(2),rs.getString(1)));
				}
				
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 得到全部的显示列名的列表
	 * @param String salaryid
	 * @return ArrayList
	 */
	public ArrayList getAllItemList(String salaryid)
	{
		ArrayList list = new ArrayList();
		try
		{
			//a0100,a0000,a00z3,a00z2,a00z0,a00z1
		     
		     /*select salaryset.itemid,salaryset.itemdesc from fielditem left join salaryset on fielditem.itemid=salaryset.itemid where salaryset.salaryid=83
		     
		     and fielditem.itemid in (select itemid from salaryset where salaryid='83')*/
			StringBuffer sql = new StringBuffer();
			sql.append("select itemid,itemdesc from salaryset where salaryid='");
			sql.append(salaryid+"' and UPPER(itemid) not in('A0100','A0000','A00Z3','A00Z2','A00Z0','A00Z1')  order by sortid  ");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs= null;
			rs=dao.search(sql.toString());
			while(rs.next())
			{
				FieldItem item = DataDictionary.getFieldItem(rs.getString(1).toLowerCase());
				if(item==null|| "0".equals(item.getUseflag()))
					continue;
				list.add(new CommonData(rs.getString(1),rs.getString(2)));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
    		
	}
	public ArrayList getList(String items)
	{
		ArrayList list = new ArrayList();
		try{
		    String[] itemids=items.split(",");
		
	     	for(int i=0;i<itemids.length;i++)
		    {
		    	list.add(itemids[i]);
		     }
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return list;
	}
	/**
	 * 取得首末行标志和首末行输出串
	 * @param String bank_id
	 * @return HashMap
	 */
	public HashMap getCheckAndFormat(String bank_id)
	{
		HashMap map = new HashMap();
		try
		{
			String sql = "select bankcheck,bankformat,bank_name,scope from gz_bank where bank_id='"+bank_id+"'";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs=null;
			rs=dao.search(sql);
			while(rs.next())
			{
				map.put("bankcheck",rs.getString("bankcheck"));
				String format=rs.getString("bankformat")==null?"":rs.getString("bankformat");
				String bank_name=rs.getString("bank_name")==null?"":rs.getString("bank_name");
				//format=format.replaceAll("\\\\n","n");
				String scope=rs.getString("scope")==null?"0":rs.getString("scope");
				map.put("bankformat",format);
				map.put("bank_name",bank_name);
				map.put("scope",scope);
				//System.out.println(rs.getString("bankcheck")+rs.getString("bankformat"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
		
	}
	/**
	 * 删除选择的银行的模板和项目信息
	 * @param String tableName
	 * @param String bank_id
	 */
	public void deleteBankInfo(String tableName,String bank_id)
	{
		try{
			StringBuffer sql = new StringBuffer();
			sql.append("delete from ");
			sql.append(tableName);
			sql.append(" where bank_id =");
			sql.append(bank_id);
			ContentDAO dao = new ContentDAO(this.conn);
			dao.delete(sql.toString(),new ArrayList());
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public void deleteItem(ArrayList itemList,String bank_id)
	{
		try
		{
			StringBuffer buf = new StringBuffer("");
			for(int i=0;i<itemList.size();i++)
			{
				LazyDynaBean bean = (LazyDynaBean)itemList.get(i);
				String itemid=(String)bean.get("itemid");
				buf.append(",");
				buf.append("'"+itemid+"'");
			}
			if(buf.toString().length()>0)
			{
 //    			String sql="delete from gz_bank_item where UPPER(field_name) in ("+buf.toString().substring(1).toUpperCase()+") and bank_id ="+bank_id;
     			String sql="delete from gz_bank_item where  bank_id ="+bank_id;
     			ContentDAO dao = new ContentDAO(this.conn);
     			dao.delete(sql, new ArrayList());
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 保存为银行模板选择的项目
	 * @param Arraylist selectedFieldList
	 * @param String bank_id
	 */
	public void saveTemplateItem(ArrayList selectedFieldList,String bank_id)
	{
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sql = new StringBuffer();
			int item_id = this.getItemid();
			int seq=1;
			for(int i=0;i<selectedFieldList.size();i++)
			{
				LazyDynaBean bean = (LazyDynaBean)selectedFieldList.get(i);
				sql.append("insert into gz_bank_item ");
				sql.append("(bank_id,item_name,item_type,field_name,field_default,format");
				if(Sql_switcher.searchDbServer() != Constant.MSSQL)
				{
					sql.append(",item_id");
				}
				sql.append(",norder)");
				sql.append(" values (");
				sql.append(bank_id+",'");
				sql.append((String)bean.get("itemdesc"));
				sql.append("','");
				if("N".equalsIgnoreCase((String)bean.get("item_type")))
				    sql.append("0','");
				if("A".equalsIgnoreCase((String)bean.get("item_type")))
					sql.append("2','");
				if("D".equalsIgnoreCase((String)bean.get("item_type")))
					sql.append("1','");
				sql.append((String)bean.get("itemid"));
				sql.append("','");
				sql.append((String)bean.get("itemlength"));
				sql.append("','");
				sql.append(PubFunc.keyWord_reback((String)bean.get("format")));				
				sql.append("'");
				if(Sql_switcher.searchDbServer() != Constant.MSSQL)
				{
					sql.append(","+item_id);
				}
				sql.append(","+seq+")");
				dao.insert(sql.toString(),new ArrayList());
				sql.setLength(0);
				item_id++;
				seq++;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 更新银行模板的首末行输出串和首末行标志
	 * @param String bankCheck
	 * @param String bankFormat
	 * @param String bank_id
	 */
	public void updateBankTemplate(String bankCheck,String bankFormat,String bank_id)
	{
		try
		{
    		StringBuffer sql =new StringBuffer();
    		sql.append("update gz_bank set bankCheck="+bankCheck+" where bank_id=");
    		sql.append(bank_id);
    		ArrayList list = new ArrayList();
		    ContentDAO dao = new ContentDAO(this.conn);
			dao.update(sql.toString(),list);
			sql.setLength(0);
			sql.append("update gz_bank set bankFormat=? where bank_id=");
			sql.append(bank_id);
			list.add(bankFormat);
			dao.update(sql.toString(),list);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
	}
	/**
	 * dml 2011年8月29日10:05:11
	 * 
	 * */
	public void updateBankNameandPriv(String bankname,String scope,String bank_id)
	{
		try
		{
    		StringBuffer sql =new StringBuffer();
    		sql.append("update gz_bank set bank_name='"+bankname+"',scope="+scope+" where bank_id=");
    		sql.append(bank_id);
    		ArrayList list = new ArrayList();
		    ContentDAO dao = new ContentDAO(this.conn);
			dao.update(sql.toString(),list);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
	}
	/******************************************************************************************/
	public ArrayList getNewSelectFieldList(String newSelectField,String salaryid,String bank_id)
	{
		ArrayList list = new ArrayList();
		try
		{
			StringBuffer columnsBuf = new StringBuffer();
			StringBuffer sqlBuf = new StringBuffer();
			StringBuffer inGzItem = new StringBuffer();
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			String[] newSelectField_arr=newSelectField.split(",");
			for(int i=0;i<newSelectField_arr.length;i++)
			{
				columnsBuf.append(",'");
				columnsBuf.append(newSelectField_arr[i]);
				columnsBuf.append("'");
			}
			sqlBuf.append("select * from gz_bank_item where field_name in(");
			sqlBuf.append(columnsBuf.toString().substring(1));
			sqlBuf.append(") and bank_id=");
			sqlBuf.append(bank_id);
			sqlBuf.append(" order by norder");
			rs = dao.search(sqlBuf.toString());
			while(rs.next())
			{
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("itemdesc",rs.getString("item_name"));
				bean.set("itemid",rs.getString("field_name"));
				if("0".equalsIgnoreCase(rs.getString("item_type")))
				{
			    	bean.set("itemtype","数值型");
			    	bean.set("item_type","N");
				}else if("1".equalsIgnoreCase(rs.getString("item_type")))
				{
					bean.set("itemtype","日期型");
			    	bean.set("item_type","D");
				}else if("2".equalsIgnoreCase(rs.getString("item_type")))
				{
					bean.set("itemtype","字符型");
			    	bean.set("item_type","A");
				}
				bean.set("itemlength",rs.getString("field_default"));
				bean.set("format",rs.getString("format"));
				inGzItem.append(",'");
				inGzItem.append(rs.getString("field_name"));
				inGzItem.append("'");
				list.add(bean);
			}
			rs.close();
			sqlBuf.setLength(0);
			sqlBuf.append("select itemid,itemdesc,itemlength,codesetid,sortid,itemtype from salaryset where itemid in (");
			sqlBuf.append(columnsBuf.toString().substring(1));
			sqlBuf.append(")");
			if(inGzItem!=null&&inGzItem.toString().trim().length()>0)
			{
				sqlBuf.append(" and itemid not in (");
				sqlBuf.append(inGzItem.toString().substring(1));
				sqlBuf.append(")");
			}
			sqlBuf.append(" and salaryid=");
			sqlBuf.append(salaryid);
			rs=dao.search(sqlBuf.toString());
			while(rs.next())
			{
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("itemdesc", "a0000".equalsIgnoreCase(rs.getString("itemid"))?"序号":rs.getString("itemdesc"));
				bean.set("itemid",rs.getString("itemid"));
				if("N".equalsIgnoreCase(rs.getString("itemtype")))
				{
			    	bean.set("itemtype","数值型");
			    	bean.set("item_type","N");
				}else if("D".equalsIgnoreCase(rs.getString("itemtype")))
				{
					bean.set("itemtype","日期型");
			    	bean.set("item_type","D");
				}else if("A".equalsIgnoreCase(rs.getString("itemtype"))|| "M".equalsIgnoreCase(rs.getString("itemtype")))
				{
					bean.set("itemtype","字符型");
			    	bean.set("item_type","A");
				}
				bean.set("itemlength",rs.getString("itemlength"));
				bean.set("format","");
				list.add(bean);
			}
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	public ArrayList getAllBankItem(String salaryid,String bank_id)
	{
		ArrayList list = new ArrayList();
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs= null;
			String sql = "select itemid from salaryset where salaryid="+salaryid;
			rs = dao.search(sql);
			Hashtable ht=new Hashtable(); 
			while(rs.next())
			{
				if("0".equalsIgnoreCase(this.userview.analyseFieldPriv(rs.getString("itemid")))){
					continue;
				}//dml 2011年7月22日11:33:13
				ht.put(rs.getString("itemid"),rs.getString("itemid"));
			}
			StringBuffer buf = new StringBuffer();
			StringBuffer selected_item_buf = new StringBuffer("'a0100','nbase'");
			buf.append("select item_name,field_name from gz_bank_item where bank_id=");
			buf.append(bank_id);
			buf.append(" order by norder");
			rs=dao.search(buf.toString());
			while(rs.next())
			{
				LazyDynaBean bean= new LazyDynaBean();
				if(ht.get(rs.getString("field_name"))!=null)
				{
					
	     			bean.set("itemid",rs.getString("field_name"));
	    			bean.set("itemdesc",rs.getString("item_name"));
	    			bean.set("isSelect","1");
	    			list.add(bean);
	    			selected_item_buf.append(",'");
	    			selected_item_buf.append(rs.getString("field_name"));
	    			selected_item_buf.append("'");
				}
			}
			buf.setLength(0);
			//rs.close();
			buf.append("select itemid,itemdesc from salaryset where salaryid=");
			buf.append(salaryid);
			if(selected_item_buf!=null&&selected_item_buf.toString().length()>0)
			{
				buf.append(" and UPPER(itemid) not in(");
				buf.append(selected_item_buf.toString().toUpperCase()/*.substring(1)*/);
				buf.append(")");
			}
			rs=dao.search(buf.toString());
			while(rs.next())
			{
				
				LazyDynaBean bean = new LazyDynaBean();
				if("a0000".equalsIgnoreCase(rs.getString("itemid")))
				{
					bean.set("itemid",rs.getString("itemid"));
					bean.set("itemdesc","序号");
				}
				else
				{
					if("0".equalsIgnoreCase(this.userview.analyseFieldPriv(rs.getString("itemid")))){
						continue;
					}//dml 2011年7月22日11:33:29
		    		bean.set("itemid",rs.getString("itemid"));
	    			bean.set("itemdesc",rs.getString("itemdesc"));
				}
				bean.set("isSelect","0");
				list.add(bean);
			}
			//rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	
	}
	/******************************************************************************************/
	/**
	 * 取得每一个银行模板项目的格式
	 * @param ArrayList columns
	 * @param String bank_id
	 * @return HashMap
	 */
	public HashMap getFormatMap(ArrayList columns,String bank_id)
	{
		HashMap map = new HashMap();
		if(columns.size()==0)
		{
			return map;
		}
		try{
			StringBuffer sql = new StringBuffer();
			StringBuffer columnBuf= new StringBuffer();
			for(int i=0;i<columns.size();i++)
			{
				columnBuf.append(",'");
				columnBuf.append(columns.get(i));
				columnBuf.append("'");
			}
			sql.append("select field_name,format from gz_bank_item where bank_id=");
			sql.append(bank_id);
			sql.append(" and field_name in (");
			sql.append(columnBuf.toString().substring(1));
			sql.append(")");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs=dao.search(sql.toString());
			while(rs.next())
			{
				map.put(rs.getString("field_name").toUpperCase(),rs.getString("format")==null?"":rs.getString("format"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	/************************************
	 * 人员筛选的一些方法
	 ************************************/
	/**
	 * 前台指标的查询条件列表
	 * @param String fieldIds 已选指标
	 * @param String salaryid 薪资表号
	 */
	public ArrayList getPersonFilterFieldList(String fieldIds,String salaryid)
	{
		ArrayList list = new ArrayList();
		try
		{
			String[] field_arr=fieldIds.split(",");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			StringBuffer whereBuf= new StringBuffer();
			StringBuffer sql = new StringBuffer();
			
			for(int i=0;i<field_arr.length;i++)
			{
				sql.append("select itemid,itemlength,codesetid,itemdesc,sortid,itemtype from salaryset where salaryid=");
				sql.append(salaryid);
				sql.append(" and itemid ='");
				sql.append(field_arr[i]);
				sql.append("'");
				rs=dao.search(sql.toString());
				while(rs.next())
				{
					LazyDynaBean bean = new LazyDynaBean();
					bean.set("log","");
					bean.set("connection","");
					bean.set("itemid",rs.getString("itemid"));
					bean.set("itemdesc", "a0000".equalsIgnoreCase(rs.getString("itemid"))?"序号":rs.getString("itemdesc"));
					bean.set("codesetid",rs.getString("codesetid"));
					if("M".equalsIgnoreCase(rs.getString("itemtype")))
					{
						bean.set("itemtype","A");
					}else
					{
						bean.set("itemtype",rs.getString("itemtype"));
					}
					
					bean.set("itemlength",rs.getString("itemlength"));
					bean.set("value","");
					bean.set("viewvalue","");
					list.add(bean);
					sql.setLength(0);
				}
				
			}
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	
	}
	
    public ArrayList getFilterResult(String tableName,String a_code,ArrayList columns,ArrayList columnsInfo,HashMap map,String a0100s,String t_name,String model,String boscount,String bosdate,UserView view,String salaryid)
    {
    	ArrayList list = new ArrayList();
		try
		{ 
			if(columns==null||columns.size()==0)
				return list;
			StringBuffer colBuf=new StringBuffer();
			boolean flag=true;
			boolean a0000flag=true;
			boolean a00z1=true;
			for(int j=0;j<columns.size();j++)
			{
				colBuf.append(",T.");
				colBuf.append((String)columns.get(j));
				if("a0100".equalsIgnoreCase((String)columns.get(j)))
					flag=false;
				if("a0000".equalsIgnoreCase((String)columns.get(j)))
					a0000flag=false;
				if("a00z1".equalsIgnoreCase((String)columns.get(j)))
					a00z1=false;
				
			}
			SalaryTemplateBo gzbo = new SalaryTemplateBo(this.conn,Integer.parseInt(salaryid),view);
			StringBuffer spSQL=new StringBuffer("");
			if("1".equals(model))
			{
				spSQL.append(" b.salaryid="+salaryid);
				spSQL.append(" and b.A00Z3=");
				spSQL.append(boscount);
				spSQL.append(" and b.A00Z2=");
				spSQL.append(Sql_switcher.dateValue(bosdate));
				spSQL.append(" and (( b.curr_user='"+view.getUserId()+"' and ( b.sp_flag='02' or b.sp_flag='07' ) ) or ( ( (b.AppUser is null  "+gzbo.getPrivWhlStr("b")+"  ) or b.AppUser Like '%;"+view.getUserName()+";%' ) and   b.sp_flag='06' or  b.sp_flag='03' ) ) ");

			}
			
			 String order=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.DEFAULT_ORDER,view);
			StringBuffer sql = new StringBuffer();
			sql.append("select ");
			sql.append(colBuf.toString().substring(1));
			sql.append(" from ");
			sql.append(t_name);
			sql.append(" T,");
			sql.append(tableName);
			sql.append(" b,dbname c where T.a0100=b.a0100 and UPPER(T.pre)=UPPER(b.nbase) and UPPER(T.pre)=UPPER(c.pre) and T.a00z1=b.a00z1 ");
			if("1".equals(model))
			{
				sql.append(" and ("+spSQL+")");
			}
			
    	    sql.append(" and (");
	    	sql.append(a0100s);
	    	sql.append(")");
	    	if(order!=null&&!"".equals(order.trim()))
		    {
		    	order = "b."+order.replaceAll(",", ",b.");
		    	sql.append(" order by "+order);
		    }
		    else
		    {
		    	sql.append(" order by c.dbid,b.a0000,b.a00z0,b.a00z1");
		    }
			
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs= null;
			rs=dao.search(sql.toString());
			while(rs.next())
			{
				LazyDynaBean bean = new LazyDynaBean();
				for(int i=0;i<columnsInfo.size();i++)
				{
					LazyDynaBean abean=(LazyDynaBean)columnsInfo.get(i);
					if(flag&& "a0100".equalsIgnoreCase((String)abean.get("itemid")))
						continue;
					if(a0000flag&& "a0000".equalsIgnoreCase((String)abean.get("itemid")))
						continue;
					if(a00z1&& "A00Z1".equalsIgnoreCase((String)abean.get("itemid")))
						continue;
					if("a0000".equalsIgnoreCase((String)abean.get("itemid")))
					{
						String value=rs.getString((String)abean.get("itemid"));
						//bean.set((String)abean.get("itemid"),PubFunc.round(value,0)); 转换为数字型会丢失格式。zhanghua 2017-8-1
						bean.set((String)abean.get("itemid"),value,0);
					}
					else
		        	 	bean.set((String)abean.get("itemid"),rs.getString((String)abean.get("itemid"))==null?"":rs.getString((String)abean.get("itemid")));
					if("N".equalsIgnoreCase((String)abean.get("item_type")))
					{
						bean.set("itemtype","N");
					}
					else
					{
						bean.set("itemtype","A");
					}
				}
				list.add(bean);
				
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
    
    } 
    public double getCount(String tableName,String itemid,String a0100s,String model,String boscount,String bosdate,UserView view,String salaryid)
    {
    	double d=0.0;
    	RowSet rs = null;
    	try
    	{
    		StringBuffer spSQL=new StringBuffer("");
			if("1".equals(model))
			{
				SalaryTemplateBo gzbo=new SalaryTemplateBo(this.conn,Integer.parseInt(salaryid),view);
				spSQL.append(" salaryid="+salaryid);
				spSQL.append(" and A00Z3=");
				spSQL.append(boscount);
				spSQL.append(" and A00Z2=");
				spSQL.append(Sql_switcher.dateValue(bosdate));
				spSQL.append(" and (( curr_user='"+view.getUserId()+"' and ( sp_flag='02' or sp_flag='07' ) ) or ( ( (AppUser is null  "+gzbo.getPrivWhlStr("")+"  ) or AppUser Like '%;"+view.getUserName()+";%' ) and   sp_flag='06' ) ) ");

			}
			StringBuffer sql = new StringBuffer();
			sql.append("select ");
			sql.append("sum("+itemid+") ");
			sql.append(" from ");
			sql.append(tableName+" T ");
			sql.append(" where 1=1 ");
			if("1".equals(model))
			{
				sql.append(" and ("+spSQL+")");
			}
			
    	    sql.append(" and (");
	    	sql.append(a0100s.toUpperCase().replaceAll("T.PRE", "T.NBASE"));
	    	sql.append(")");
	    	ContentDAO dao  = new ContentDAO(this.conn);
	    	rs= dao.search(sql.toString());
            while(rs.next())
            {
            	d += rs.getDouble(1);
            }
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	finally
    	{
    		if(rs!=null)
    		{
    			try
    			{
    				rs.close();
    			}
    			catch(Exception e)
    			{
    				e.printStackTrace();
    			}
    		}
    	}
    	return d;
    }
    public void deleteTempTable(String tableName)
    {
    	try
    	{
    		Table table = new Table(tableName);
    		DbWizard dbWizard=new DbWizard(this.conn);
    		if(dbWizard.isExistTable(table.getName(),false))
			{
				dbWizard.dropTable(table);
			}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
    
    /**临时表*/
    public void createBankDiskTempTable(ArrayList itempropertylist,String salaryid,String bank_id,UserView view,ArrayList dataList,ArrayList columns)
    {
    	//ArrayList list = new ArrayList();
    	try
    	{
    		String tableName="TT"+view.getUserName()+"_gz_b";
    		DbWizard dbWizard=new DbWizard(this.conn);
    		if(itempropertylist==null||itempropertylist.size()==0)
    			return;
    	
    		Table table=new Table(tableName);
    		table.setCreatekey(false);
    		Field temp = null;
    		boolean flag=true; 
    		boolean a0000flag=true;
    		boolean a00z1=true;
    		for(int i=0;i<itempropertylist.size();i++)
    		{
    			LazyDynaBean bean  =(LazyDynaBean)itempropertylist.get(i);
    			temp =new Field((String)bean.get("itemid"),(String)bean.get("itemdesc"));
    			if("a0100".equalsIgnoreCase((String)bean.get("itemid")))
    				flag=false;
    			if("a0000".equalsIgnoreCase((String)bean.get("itemid")))
    				a0000flag=false;
    			if("a00z1".equalsIgnoreCase((String)bean.get("itemid")))
    				a00z1=false;
    			temp.setVisible(true);
    			temp.setKeyable(false);
    			temp.setNullable(true);
    			temp.setDatatype(DataType.STRING);
    			temp.setAlign("left");
    			temp.setSortable(true);
    			temp.setLength(100);
    			table.addField(temp);
    		}
    		if(flag)
    		{
    			temp= new Field("A0100","人员编号");
    			temp.setDatatype(DataType.STRING);
    			temp.setAlign("left");
    			temp.setVisible(false);
    			temp.setLength(10);
    			table.addField(temp);
    		}
    		if(a0000flag)
    		{
    			temp= new Field("A0000","序号");
    			temp.setDatatype(DataType.INT);
    			temp.setAlign("left");
    			temp.setVisible(false);
    			table.addField(temp);
    		}
    		if(a00z1)
    		{
    			temp= new Field("A00Z1","归属次数");
    			temp.setDatatype(DataType.STRING);
    			temp.setAlign("left");
    			temp.setVisible(false);
    			temp.setLength(10);
    			table.addField(temp);
    		}
    		temp= new Field("pre","人员库");
			temp.setDatatype(DataType.STRING);
			temp.setAlign("left");
			temp.setVisible(false);
			temp.setLength(10);
			table.addField(temp);
		
			if(dbWizard.isExistTable(table.getName(),false))
			{
				dbWizard.dropTable(table);
			}
			
			dbWizard.createTable(table);// table created
			/**import data*/
			importDataFromSalaryToTempTable(dataList,itempropertylist,tableName);

    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
    public void importDataFromSalaryToTempTable(ArrayList dataList,ArrayList itempropertylist,String tableName)
    {
    	try
    	{
    		StringBuffer columnsBuf= new StringBuffer();
    		StringBuffer valueBuf = new StringBuffer();
    		StringBuffer insertSqlBuf= new StringBuffer();
    		ContentDAO dao = new ContentDAO(this.conn);
    		boolean flag=true;
    		boolean a0000flag=true;
    		boolean a00z1=true;
    		
    		if(dataList.size()==0)
    			return;
    		
    		ArrayList recordList=new ArrayList();
    		ArrayList beanList=new ArrayList();
    		for(int j=0;j<dataList.size();j++)
    		{
    			LazyDynaBean bean = (LazyDynaBean)dataList.get(j);
    			
    			beanList=new ArrayList();
    			for(int k=0;k<itempropertylist.size();k++)
    			{
    				LazyDynaBean abean=(LazyDynaBean)itempropertylist.get(k);
    				if(j==0)
    				{
    				//------
    		    		columnsBuf.append(",");
        	    		columnsBuf.append((String)abean.get("itemid"));
        	    		if("a0100".equalsIgnoreCase((String)abean.get("itemid")))
        	    			flag=false;
        	    		if("a0000".equalsIgnoreCase((String)abean.get("itemid")))
        	    			a0000flag=false;
        	    		if("a00z1".equalsIgnoreCase((String)abean.get("itemid")))
        	    			a00z1=false;
    				//------
    				}
    				valueBuf.append(",");
    				valueBuf.append("?");
    				
    			
    				if(bean.get(((String)abean.get("itemid")).toUpperCase())==null|| "".equals((String)bean.get(((String)abean.get("itemid")).toUpperCase())))
    				{
    				//	valueBuf.append("''");
    					
    					beanList.add("");
    				}
    				else
    				{
    	    			 
    	    		//		valueBuf.append("'");
    		    	//	    valueBuf.append((String)bean.get(((String)abean.get("itemid")).toUpperCase())); 
    		    	//		valueBuf.append("'");
    		    			
    		    			beanList.add((String)bean.get(((String)abean.get("itemid")).toUpperCase()));
    				}
    			}
    			if(flag)
    			{
    				if(j==0)
    				   columnsBuf.append(",A0100");
    			//	valueBuf.append(",'");
    			//	valueBuf.append((String)bean.get("A0100"));
    			//	valueBuf.append("'");
    				valueBuf.append(",?");
    				beanList.add((String)bean.get("A0100"));
    			}
    			if(a00z1)
    			{
    				if(j==0)
    				   columnsBuf.append(",A00Z1");
    			//	valueBuf.append(",'");
    			//	valueBuf.append((String)bean.get("A00Z1"));
    			//	valueBuf.append("'");
    				valueBuf.append(",?");
    				beanList.add((String)bean.get("A00Z1"));
    			}
    			if(a0000flag)
    			{
    				if(j==0)
     				   columnsBuf.append(",A0000");
     			//	valueBuf.append(",'");
     			//	valueBuf.append((String)bean.get("A0000"));
     			//	valueBuf.append("'");
    				valueBuf.append(",?");
     				beanList.add((String)bean.get("A0000"));
    			}
    			if(j==0)
    		    	columnsBuf.append(",pre");
    		//	valueBuf.append(",'");
			//	valueBuf.append((String)bean.get("pre"));
			//	valueBuf.append("'");
    			valueBuf.append(",?");
				beanList.add((String)bean.get("pre"));
				if(j==0)
				{
	    			insertSqlBuf.append("insert into ");
	    			insertSqlBuf.append(tableName);
	    			insertSqlBuf.append(" (");
	    			insertSqlBuf.append(columnsBuf.toString().substring(1));
	    			insertSqlBuf.append(") values (");
	    			insertSqlBuf.append(valueBuf.toString().substring(1));
	    			insertSqlBuf.append(")");
				}
    			//System.out.println(insertSqlBuf.toString());
    			//dao.insert(insertSqlBuf.toString(),new ArrayList());
    			//insertSqlBuf.setLength(0);
    			//valueBuf.setLength(0);
    			
    			recordList.add(beanList);
    		}
    		dao.batchInsert(insertSqlBuf.toString(),recordList);
    		
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
    public ArrayList getItemidAndDescList(String bank_id)
    {
    	ArrayList list = new ArrayList();
		try
		{
			String sql = "select item_name,field_name from gz_bank_item where bank_id='"+bank_id+"' order by item_id";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs= null;
			rs=dao.search(sql);
			while(rs.next())
			{
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("itemid",rs.getString("item_name"));
				bean.set("itemdesc",rs.getString("field_name"));
				list.add(bean);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
    }
    public ArrayList getLabelList(ArrayList itemidAndDesc,HashMap hm)
    {
    	ArrayList list = new ArrayList();
    	try
    	{
    		for(int i=0;i<itemidAndDesc.size();i++)
    		{
    			LazyDynaBean bean = (LazyDynaBean)itemidAndDesc.get(i);
    			Field newField = new Field((String)bean.get("itemid"), (String)bean.get("itemdesc"));
    			String ss=(String)bean.get("itemid");
    			if("D".equalsIgnoreCase((String)bean.get("item_type")))
    			{
    				newField.setLength(10);
    				//newField.setDatatype(DataType.DATE);
    			}
    			//System.out.println("itemid="+ss+";itemtype="+(String)bean.get("item_type")+";decimal="+(String)bean.get("decwidth"));
    			newField.setDecimalDigits(Integer.parseInt((String)bean.get("decwidth")));
    			if("N".equalsIgnoreCase((String)bean.get("item_type"))&&((String)hm.get(((String)bean.get("itemid")).toUpperCase())).length()<=0)
    			{
    				
    				if("0".equalsIgnoreCase((String)bean.get("decwidth")))
    				{
    					newField.setDatatype(DataType.INT);
    					newField.setFormat("####");
    				}
    				
    			}
    			newField.setReadonly(true);
    			newField.setSortable(true);
    			list.add(newField);
    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return list;
    	
    }
    /**得到人员筛选条件列表*/
    public ArrayList getFilterCondList(String salaryid)
    {
    	ArrayList list= new ArrayList();
    	String cond_str="";
    	try
    	{
    		String str="select lprogram from salarytemplate where salaryid="+salaryid;
    		ContentDAO dao = new ContentDAO(this.conn);
    		RowSet rs=null;
    		rs= dao.search(str);
    		while(rs.next())
    		{
    			cond_str=rs.getString("lprogram");
    		}
    		CommonData temp=new CommonData("all",ResourceFactory.getProperty("label.gz.allman"));
    		list.add(temp);
    		if(!(cond_str==null||cond_str.trim().length()<=0))
    		{
    			
    		    SalaryLProgramBo lbo = new SalaryLProgramBo(cond_str,this.userview); //xieguiquan add this.userview 20100828
    		    list.addAll(lbo.getServiceItemList2());
    		}
    		temp=new CommonData("new",ResourceFactory.getProperty("label.gz.new"));
    		list.add(temp);	
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return list;
    }
    public ArrayList getFilterCondBeanList(String salaryid,UserView userView) //增加参数UserView userView xieguiquan 20100827
    {
    	ArrayList list = new ArrayList();
    	try
    	{
    		String cond_str="";
    		String str="select lprogram from salarytemplate where salaryid="+salaryid;
    		ContentDAO dao = new ContentDAO(this.conn);
    		RowSet rs=null;
    		rs= dao.search(str);
    		while(rs.next())
    		{
    			cond_str=rs.getString("lprogram");
    		}
    		if(!(cond_str==null||cond_str.trim().length()<=0))
    		{
    		    SalaryLProgramBo lbo = new SalaryLProgramBo(cond_str,userView);
    		    ArrayList temp=lbo.getServiceItemList2();
    		    
    		    for(int i=0;i<temp.size();i++)
    		    {
    		    	CommonData cd=(CommonData)temp.get(i);
    		    	LazyDynaBean bean = new LazyDynaBean();
    		    	bean.set("condid",cd.getDataValue());
    		    	bean.set("name",cd.getDataName());
    		    	//特殊处理：这里是读取的信息，超级用户能读旧信息
    		    	list.add(bean);
    		    }
    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return list;
    }
    public String getCondXML(String salaryid)
    {
    	String xml="";
    	try
    	{
    		String str="select lprogram from salarytemplate where salaryid="+salaryid;
    		ContentDAO dao = new ContentDAO(this.conn);
    		RowSet rs=null;
    		rs= dao.search(str);
    		while(rs.next())
    		{
    			xml=rs.getString("lprogram");
    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return xml;
    }
    /**
     * 得到做为人员筛选条件的指标的属性
     */
   public LazyDynaBean getFiledItemProperty(String salaryid,String itemid)
   {
	   LazyDynaBean bean = new LazyDynaBean();
	   try
	   {
		   if("SP_FLAG".equalsIgnoreCase(itemid))
		   {
			   bean.set("itemid","SP_FLAG");
    		   bean.set("itemdesc",ResourceFactory.getProperty("label.gz.sp"));
    		   bean.set("itemlength","50");
    		   bean.set("decwidth","0");
	    	   bean.set("codesetid","23");
    		   bean.set("itemtype","A");
		   }else if("SP_FLAG2".equalsIgnoreCase(itemid))
		   {
			   bean.set("itemid","SP_FLAG2");
    		   bean.set("itemdesc","报审状态");
    		   bean.set("itemlength","50");
    		   bean.set("decwidth","0");
	    	   bean.set("codesetid","23");
    		   bean.set("itemtype","A");
		   }
		   else
		   {
	    	   StringBuffer sql = new StringBuffer();
	    	   sql.append("select itemid,itemdesc,itemlength,decwidth,codesetid,itemtype from salaryset where ");
	    	   sql.append("salaryid=");
	    	   sql.append(salaryid);
	    	   sql.append(" and itemid='");
	    	   sql.append(itemid);
	    	   sql.append("'");
	    	   ContentDAO dao = new ContentDAO(this.conn);
	    	   RowSet rs = null;
	    	   rs=dao.search(sql.toString());
		       while(rs.next())
	    	   {
		    	   bean.set("itemid",rs.getString("itemid"));
	    		   bean.set("itemdesc", "a0000".equalsIgnoreCase(rs.getString("itemid"))?"序号":rs.getString("itemdesc"));
	    		   bean.set("itemlength",rs.getString("itemlength"));
	    		   bean.set("decwidth",rs.getString("decwidth"));
		    	   bean.set("codesetid",rs.getString("codesetid"));
	    		   bean.set("itemtype",rs.getString("itemtype"));
    		   }
    	   }
	   }
	   catch(Exception e)
	   {
		   e.printStackTrace();
	   }
	   return bean;
   }
	public HashMap getFieldItemMap(int salaryid,UserView userView)
	{
		HashMap map = new HashMap();
		try
		{
			SalaryTemplateBo sTBo = new SalaryTemplateBo(this.conn,salaryid,userView);
			ArrayList fieldlist=sTBo.getFieldlist();
			Field field=null;
			for(int i=0;i<fieldlist.size();i++)
			{
				field=(Field)fieldlist.get(i);
				FieldItem item = new FieldItem();
				String s = field.getName();
				item.setCodesetid(field.getCodesetid());
				item.setUseflag("1");
				if(field.getDatatype()==DataType.DATE)
				{
					item.setItemtype("D");
				}
				else if(field.getDatatype()==DataType.STRING)
				{
					item.setItemtype("A");
				}
				else if(field.getDatatype()==DataType.INT||field.getDatatype()==DataType.FLOAT)
				{
					item.setItemtype("N");
				}
				else if(field.getDatatype()==DataType.CLOB)
				{
					item.setItemtype("M");
				}
				else 
					item.setItemtype("A");
				item.setItemid(field.getName().toUpperCase());
				item.setAlign(field.getAlign());
				item.setItemdesc(field.getLabel());

				map.put(field.getName().toUpperCase(),item);
	
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 取得前台显示的列表列和取得数据的sql语句
	 * @param itemList 前台表头指标列表
	 * @param tableName 薪资表名
	 * @param formatMap 每一个表头指标数据的显示格式
	 * @return
	 */
	public HashMap getSQL(ArrayList itemList,String tableName,HashMap formatMap) throws GeneralException
	{
		HashMap map = new HashMap();
		StringBuffer sql = new StringBuffer();
		StringBuffer buf= new StringBuffer();
		ArrayList fieldList = new ArrayList();
		Field field=null;
		try
		{
			for(int i=0;i<itemList.size();i++)
			{
				LazyDynaBean bean = (LazyDynaBean)itemList.get(i);
				field = new Field(((String)bean.get("itemid")).toUpperCase(),(String)bean.get("itemdesc"));
				String type=(String)bean.get("itemtype");
				String codesetid=(String)bean.get("codesetid");
				if("N".equalsIgnoreCase(type))
				{
					int decwidth=Integer.parseInt((String)bean.get("decwidth"));
					if(decwidth>0)
					{
						field.setDatatype(DataType.FLOAT);
					}
					else
					{
						field.setDatatype(DataType.INT);
					}
					field.setAlign("right");
				}
				else if("M".equalsIgnoreCase(type))
				{
					field.setDatatype(DataType.CLOB);
					field.setAlign("left");
				}
				else if("D".equalsIgnoreCase(type))
				{
					field.setDatatype(DataType.DATE);
					field.setFormat("yyyy.MM.dd");
					field.setLength(20);
					field.setAlign("left");
				}
				else if("A".equalsIgnoreCase(type))
				{
					if(codesetid==null|| "0".equals(codesetid)|| "".equals(codesetid))
						field.setLength(Integer.parseInt((String)bean.get("itemlength")));						
					else
						field.setLength(50);
					field.setDatatype(DataType.STRING);
					field.setAlign("left");
				}
				else
				{
					field.setDatatype(DataType.STRING);
					field.setAlign("left");
					field.setLength(Integer.parseInt((String)bean.get("itemlength")));
				}
				field.setCodesetid((String)bean.get("codesetid"));
				fieldList.add(field);
				//--解析sql语句
				/**是数字的按照格式显示*/
				if("N".equalsIgnoreCase(type))
				{
					buf.append(",");
					/**格式串如 0000.00*/
					String format=(String)formatMap.get(((String)bean.get("itemid")).toUpperCase());
					
				}
				else
				{
					buf.append(",");
					buf.append((String)bean.get("itemid"));
				}
				
			}
			sql.append("select ");
			sql.append(buf.toString().substring(1));
			sql.append(" from ");
			sql.append(tableName);
			map.put("1",sql.toString());
			map.put("2",fieldList);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return map;
	}
	public String getA0100s(String beforeSql,String filterSql,String tableName,String priv_mode,String privSql,String model,String spSQL)
	{
		StringBuffer sql = new StringBuffer("");
		try
		{
			String sql_str="select a0100,nbase from "+tableName+" where 1=1 ";
			if(filterSql!=null&&filterSql.trim().length()>0)
			{
				sql_str+=" and "+SafeCode.decode(filterSql);
			}
			 if(beforeSql!=null&&beforeSql.trim().length()>0)
			{
				sql_str+=" and "+SafeCode.decode(SafeCode.decode(beforeSql)).trim().substring(3);
			}
			
			 if(privSql!=null&&privSql.trim().length()>0)
			 {
				 sql_str+=" and "+privSql;
			 }
			 if("1".equals(model))
			 {
				 sql_str+=" and ("+spSQL+")";
			 }
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs=dao.search(sql_str);
			while(rs.next())
			{
				sql.append(" or (T.a0100='");
				sql.append(rs.getString("a0100"));
				sql.append("' and UPPER(T.pre)='"+rs.getString("nbase").toUpperCase()+"')");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		if(sql!=null&&sql.toString().trim().length()>0)
			return sql.toString().substring(3);
		else
			return " 1=2 ";
	}
	public ArrayList getCondFieldList(String field_str,String salaryid)
	{
		ArrayList list = new ArrayList();
		try
		{
			if(field_str==null|| "".equals(field_str))
				return list;
			RowSet rs= null;
			ContentDAO dao = new ContentDAO(this.conn);
			String[] tt=field_str.split(",");
			for(int i=0;i<tt.length;i++)
			{
				if("'sp_fl'".equalsIgnoreCase(tt[i]))
				{
					list.add(new CommonData("sp_flag","审批状态"));
					continue;
				}
	    		String sql="select itemid,itemdesc from salaryset where salaryid="+salaryid+" and itemid ="+tt[i];
			    rs=dao.search(sql);
		    	while(rs.next())
	     		{
		    		list.add(new CommonData(rs.getString("itemid"), "a0000".equalsIgnoreCase(rs.getString("itemid"))?"序号":rs.getString("itemdesc")));
	    		}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	//---------------------------------------------------------------
	public HashMap getCondField(String expr,String salaryid)
	{
		HashMap map = new HashMap();
		try
		{
			if(expr==null|| "".equals(expr))
				return map;
			String str1=expr.substring(0,expr.indexOf("|"));
			String str2=expr.substring(expr.indexOf("|")+1);
			String[] bds=str2.split("`");
			for(int i=0;i<bds.length;i++)
			{
			    LazyDynaBean bean=new LazyDynaBean();
				String str=bds[i];//Z0106=1
				String fieldid=str.substring(0,5);//指标
				String fieldvalue="";//值
				String oper="";//关系符号
				if(str.indexOf("sp_flag")!=-1||str.indexOf("SP_FLAG")!=-1)
				{
					fieldid=str.substring(0,7);
					if(str.length()<=7)
	    			{
	    				oper=str.substring(7);
	    			}
    				else if(7<str.length()&&str.length()<=8)
		    		{
		    			oper=str.substring(7);
		    		}
		    		else
		    		{
		    	    	String oper_temp=str.substring(8,9);
		        		int flag=0;
			        	if("=".equals(oper_temp)|| "<".equals(oper_temp)|| ">".equals(oper_temp))
		        		{
		        			oper=str.substring(7,9);
		        			flag=1;
		        		}
		        		else
		        		{
		        			oper=str.substring(7,8);
		        			flag=2;
		         		}
				
		        		if(flag==1)
	    	    			fieldvalue=str.substring(9);
	    	    		else
	        				fieldvalue=str.substring(8);
		    		}
				}
				else
				{
	    			if(str.length()<=5)
	    			{
	    				oper=str.substring(5);
	    			}
    				else if(5<str.length()&&str.length()<=6)
		    		{
		    			oper=str.substring(5);
		    		}
		    		else
		    		{
		    	    	String oper_temp=str.substring(6,7);
		        		int flag=0;
			        	if("=".equals(oper_temp)|| "<".equals(oper_temp)|| ">".equals(oper_temp))
		        		{
		        			oper=str.substring(5,7);
		        			flag=1;
		        		}
		        		else
		        		{
		        			oper=str.substring(5,6);
		        			flag=2;
		         		}
				
		        		if(flag==1)
	    	    			fieldvalue=str.substring(7);
	    	    		else
	        				fieldvalue=str.substring(6);
		    		}
				}
				bean.set("oper",oper);
				bean.set("value",fieldvalue);
				/**分析逻辑符号*/
				//如果有括号的怎么办
               /* if(i!=0)
                {
                	int index=2*i-1;
                	String log=str1.substring(index,index+1);
                	bean.set("log",log);
                }
                else
                	bean.set("log","*");*/
				map.put(fieldid.toUpperCase()+i,bean);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
		
	}
	/**
	 *  get xml String
	 * @param salaryid
	 * @return
	 */
	public String getXml(String salaryid)
	{
		String str="";
		try
		{
			String sql="select lprogram from salarytemplate where salaryid="+salaryid;
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs=dao.search(sql);
			while(rs.next())
			{
				str=rs.getString("lprogram");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return str;
	}
	/**
	 * get character format
	 * @param value
	 * @param format
	 * @return
	 */
	public String getCharacterFormat(String value,String format_str,int length)
	{
		String return_str="";
		try
		{
			if(value==null|| "".equals(value))
			{
				return return_str;
			}
			if(format_str==null|| "".equals(format_str))
			{
				return value;
			}
			String format="";
			String str="";
			if(format_str.indexOf("^")!=-1)
			{
				int index=format_str.indexOf("^");
				int indexT=format_str.lastIndexOf("^");
				//从左取
				if(index==indexT)
				{
					String constant=format_str.substring(0,index);
					String zero=format_str.substring(index+1);
					//System.out.println(value+"="+value.getBytes().length);
					if("".equals(zero))
						return_str=constant+value;
					else if(value.getBytes().length<=zero.length())
						return_str=constant+value;
					else
						return_str=constant+value.substring(0,zero.length());
				}
				else
				{
					String constant=format_str.substring(0,index);
					String zero=format_str.substring(indexT+1);
					if("".equals(zero))
						return_str=constant+value;
					else if(value.getBytes().length<=zero.length())
						return_str=constant+value;
					else
						return_str=constant+value.substring(value.length()-zero.length());
				}
				
			}
			else if(format_str.length()>=2)
			{
				format=format_str.substring(0,1);
				str=format_str.substring(1);
				if("@".equals(format))
				{
					if(value.length()<length)
					{
						int tmp=length-value.getBytes().length;
						int format_str_length=str.getBytes().length;
						if(tmp<format_str_length)
						{
							/**是补全还是截断？*/
							return_str=str.substring(0,tmp)+value;
						}
						else if(tmp==format_str_length)
						{
							return_str=str+value;
						}
						else
						{
							int n=tmp/format_str_length;
							String temp="";
							for(int j=0;j<n;j++)
							{
								temp+=str;
							}
							/**是补全还是截断？*/
							int vv=tmp-format_str_length*n;
							temp+=str.substring(0,(tmp-format_str_length*n));
							return_str=temp+value;
							
						}
						/*String temp="";
						for(int j=0;j<tmp;j++)
						{
							temp+=str;
						}
						return_str=temp+value;*/
					}
					else
					{
						return_str=value;
					}
				}
				else if("&".equals(format))
				{
					if(value.length()<length)
					{
						int tmp=length-value.getBytes().length;
						String temp="";
						for(int j=0;j<tmp;j++)
						{
							temp+=str;
						}
						return_str=value+temp;
					}
					else
					{
						return_str=value;
					}
				}
				else
				{
					return_str=value;
				}
				
			}
			else 
			{
    			if("&".equals(format_str))
	    		{
    				int tmp=length-value.getBytes().length;
					String temp=" ";
					for(int j=0;j<tmp;j++)
					{
						str+=temp;
					}
					return_str=value+str;
	    		}
	    		else if("@".equals(format_str))
	    		{
	    			int tmp=length-value.getBytes().length;
					String temp=" ";
					for(int j=0;j<tmp;j++)
					{
						str+=temp;
					}
					return_str=str+value;
	    		}
	    		else if("<".equals(format_str))
	    		{
	    			return_str=value.toLowerCase();
	    		}
	      		else if(">".equals(format_str))
		    	{
	     			return_str=value.toUpperCase();
	    		}
	    		else
	    		{
	    			return_str=value;
	    		}
			}
    	}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return return_str;
	}
	public LazyDynaBean getItemInfo(String itemid,String salaryid)
	{
		LazyDynaBean bean = new LazyDynaBean();
		try
		{
			String sql="select itemtype,itemdesc from salaryset where itemid='"+itemid+"' and salaryid="+salaryid;
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs= null;
			rs=dao.search(sql);
			while(rs.next())
			{
				bean.set("itemtype",rs.getString("itemtype"));
				bean.set("itemdesc", "a0000".equalsIgnoreCase(itemid)?"序号":rs.getString("itemdesc"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return bean;
	} 
	public LazyDynaBean getItemInfo2(String itemdesc,String salaryid)
	{
		LazyDynaBean bean = null;
		try
		{
			String sql="select itemtype,itemid,itemlength from salaryset where itemdesc='"+itemdesc+"' and salaryid="+salaryid;
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs= null;
			rs=dao.search(sql);
			while(rs.next())
			{
				bean = new LazyDynaBean();
				bean.set("itemtype",rs.getString("itemtype"));
				bean.set("itemid",rs.getString("itemid"));
				bean.set("itemlength", rs.getInt("itemlength")+"");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return bean;
	} 
	/**
	 * get Date format
	 * @param value
	 * @param format_str
	 * @return
	 */
	public String getDateFormat(String value,String format_str)
	{
		String return_str="";
		try
		{
			if(value==null|| "".equals(value))
				return return_str;
			String temp="";
			if(value.length()>10)
				temp=value.substring(0,10);
			else
				temp=value;
			String oper="";
			if(value.indexOf("-")!=-1)
				oper="-";
			else
				oper=".";
			String[] arr=temp.split(oper);
			String year="";
			String month="";
			String day="";
			int Cyear=Calendar.getInstance().get(Calendar.YEAR);
			if(arr.length>=1)
				year=arr[0];
			if(arr.length>=2)
				month=arr[1];
			if(arr.length>=3)
				day=arr[2];
			
			 
			
			if("MM".equalsIgnoreCase(format_str)){
				String temp_month="";
				if(month.length()<2&&Integer.parseInt(month)<10)
				{
					temp_month="0"+month;
				}
				else
				{
					temp_month=month;
				}
				return_str=temp_month;
			}
			else if("MM月".equalsIgnoreCase(format_str)){
				String temp_month="";
				if(month.length()<2&&Integer.parseInt(month)<10)
				{
					temp_month="0"+month;
				}
				else
				{
					temp_month=month;
				}
				temp_month+="月";
				return_str=temp_month;
			}
			else if("YYYY".equalsIgnoreCase(format_str))
			{
				if(year.length()<=2)
				{
					String s=String.valueOf(Cyear);
					String t_1=s.substring(0,2);
					String t_2=s.substring(2);
					if(Integer.parseInt(year)>Integer.parseInt(t_2))
					{
						return_str=String.valueOf(Integer.parseInt(t_1)-1)+year;
					}
					else
					{
						return_str=t_1+year;
					}
					
				}
				else
	    			return_str=year;
			}
			else if("yyyy.mm".equalsIgnoreCase(format_str)|| "yyyy-mm".equalsIgnoreCase(format_str))
			{
				String temp_year="";
				String temp_month="";
				if(year.length()<=2)
				{
					String s=String.valueOf(Cyear);
					String t_1=s.substring(0,2);
					String t_2=s.substring(2);
					
					if(Integer.parseInt(year)>Integer.parseInt(t_2))
					{
						temp_year=String.valueOf(Integer.parseInt(t_1)-1)+year;
					}
					else
					{
						temp_year=t_1+year;
					}
				}
				else
				{
					temp_year=year;
				}
				if(month.length()<2&&Integer.parseInt(month)<10)
				{
					temp_month="0"+month;
				}
				else
				{
					temp_month=month;
				}
				if("yyyy.mm".equalsIgnoreCase(format_str))
				      return_str=temp_year+"."+temp_month;
				else
					 return_str=temp_year+"-"+temp_month;
			}
			else if("yy.mm.dd".equalsIgnoreCase(format_str)|| "yy-mm-dd".equalsIgnoreCase(format_str))
			{
				String temp_year="";
				String temp_month="";
				String temp_day="";
				if(year.length()<=2)
					temp_year=year;
				else
					temp_year=year.substring(2);
				if(month.length()<=1&&Integer.parseInt(month)<10)
					temp_month="0"+month;
				else
					temp_month=month;
				if(day.length()<=1&&Integer.parseInt(day)<10)
					temp_day="0"+day;
				else
					temp_day=day;
				if("yy.mm.dd".equalsIgnoreCase(format_str))
					
		     		return_str=temp_year+"."+temp_month+"."+temp_day;
				else
					return_str=temp_year+"-"+temp_month+"-"+temp_day;
			}
			else if("yyyy-mm-dd".equalsIgnoreCase(format_str))
			{
				String temp_year="";
				String temp_month="";
				String temp_day="";
				if(year.length()<=2)
				{
					String s=String.valueOf(Cyear);
					String t_1=s.substring(0,2);
					String t_2=s.substring(2);
					
					if(Integer.parseInt(year)>Integer.parseInt(t_2))
					{
						temp_year=String.valueOf(Integer.parseInt(t_1)-1)+year;
					}
					else
					{
						temp_year=t_1+year;
					}
				}
				else
				{
					temp_year=year;
				}
				if(month.length()<2&&Integer.parseInt(month)<10)
				{
					temp_month="0"+month;
				}
				else
				{
					temp_month=month;
				}
				if(day.length()<=1&&Integer.parseInt(day)<10)
					temp_day="0"+day;
				else
					temp_day=day;
				return_str=temp_year+"-"+temp_month+"-"+temp_day;
			}
			else if("yymm".equalsIgnoreCase(format_str))
			{
				String temp_year="";
				String temp_month="";
				if(year.length()<=2)
					temp_year=year;
				else
					temp_year=year.substring(2);
				if(month.length()<=1&&Integer.parseInt(month)<10)
					temp_month="0"+month;
				else
					temp_month=month;
				return_str=temp_year+temp_month;
			}
			else if("yyyymmdd".equalsIgnoreCase(format_str))
			{
				String temp_year="";
				String temp_month="";
				String temp_day="";
				if(year.length()<=2)
				{
					String s=String.valueOf(Cyear);
					String t_1=s.substring(0,2);
					String t_2=s.substring(2);
					
					if(Integer.parseInt(year)>Integer.parseInt(t_2))
					{
						temp_year=String.valueOf(Integer.parseInt(t_1)-1)+year;
					}
					else
					{
						temp_year=t_1+year;
					}
				}
				else
				{
					temp_year=year;
				}
				if(month.length()<2&&Integer.parseInt(month)<10)
				{
					temp_month="0"+month;
				}
				else
				{
					temp_month=month;
				}
				if(day.length()<=1&&Integer.parseInt(day)<10)
					temp_day="0"+day;
				else
					temp_day=day;
				return_str=temp_year+temp_month+temp_day;
			}
			else if("yymmdd".equalsIgnoreCase(format_str))
			{
				String temp_year="";
				String temp_month="";
				String temp_day="";
				if(year.length()<=2)
					temp_year=year;
				else
					temp_year=year.substring(2);
				if(month.length()<2&&Integer.parseInt(month)<10)
				{
					temp_month="0"+month;
				}
				else
				{
					temp_month=month;
				}
				if(day.length()<=1&&Integer.parseInt(day)<10)
					temp_day="0"+day;
				else
					temp_day=day;
				return_str=temp_year+temp_month+temp_day;
			}
			else if("yy年mm月".equalsIgnoreCase(format_str))
			{
				String temp_year="";
				String temp_month="";
				if(year.length()<=2)
					temp_year=year;
				else
					temp_year=year.substring(2);
				if(month.length()<=1&&Integer.parseInt(month)<10)
					temp_month="0"+month;
				else
					temp_month=month;
				return_str=temp_year+"年"+temp_month+"月";
			}
			else if(format_str.toLowerCase().indexOf("dd")!=-1||format_str.toLowerCase().indexOf("yy")!=-1||format_str.toLowerCase().indexOf("mm")!=-1)
			{
				//支持格式 yyyy|（aa）mm
				String temp_year="";
				String temp_year2="";
				String temp_month="";
				String temp_day="";
				if(year.length()<=2)
				{
					String s=String.valueOf(Cyear);
					String t_1=s.substring(0,2);
					String t_2=s.substring(2);
					
					if(Integer.parseInt(year)>Integer.parseInt(t_2))
					{
						temp_year=String.valueOf(Integer.parseInt(t_1)-1)+year;
					}
					else
					{
						temp_year=t_1+year;
					}
				}
				else
				{
					temp_year=year;
				}
				if(year.length()<=2)
					temp_year2=year;
				else
					temp_year2=year.substring(2);
				if(month.length()<2&&Integer.parseInt(month)<10)
				{
					temp_month="0"+month;
				}
				else
				{
					temp_month=month;
				}
				if(day.length()<=1&&Integer.parseInt(day)<10)
					temp_day="0"+day;
				else
					temp_day=day;
				
				 
				if(format_str.indexOf("yyyy")!=-1)
				{
					format_str=format_str.replaceAll("yyyy",temp_year);
				
				}
				if(format_str.indexOf("YYYY")!=-1)
				{
					format_str=format_str.replaceAll("YYYY",temp_year);
				
				}
				if(format_str.indexOf("yy")!=-1)
				{
					format_str=format_str.replaceAll("yy",temp_year2);
				
				}
				if(format_str.indexOf("YY")!=-1)
				{
					format_str=format_str.replaceAll("YY",temp_year2);
				
				}

				if(format_str.indexOf("mm")!=-1)
				{
					format_str=format_str.replaceAll("mm",temp_month);
				
				}
				if(format_str.indexOf("MM")!=-1)
				{
					format_str=format_str.replaceAll("MM",temp_month);
				
				}
				
				if(format_str.indexOf("dd")!=-1)
				{
					format_str=format_str.replaceAll("dd",temp_day);
				
				}
				if(format_str.indexOf("DD")!=-1)
				{
					format_str=format_str.replaceAll("DD",temp_day);
				
				}
				return_str=format_str;
			}
			else
			{
				String temp_year="";
				String temp_month="";
				String temp_day="";
				if(year.length()<=2)
				{
					String s=String.valueOf(Cyear);
					String t_1=s.substring(0,2);
					String t_2=s.substring(2);
					
					if(Integer.parseInt(year)>Integer.parseInt(t_2))
					{
						temp_year=String.valueOf(Integer.parseInt(t_1)-1)+year;
					}
					else
					{
						temp_year=t_1+year;
					}
				}
				else
				{
					temp_year=year;
				}
				if(month.length()<2&&Integer.parseInt(month)<10)
				{
					temp_month="0"+month;
				}
				else
				{
					temp_month=month;
				}
				if(day.length()<=1&&Integer.parseInt(day)<10)
					temp_day="0"+day;
				else
					temp_day=day;
				return_str=temp_year+oper+temp_month+oper+temp_day;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return return_str;
			
	}
	public String getNumberFormat(String value,String format,int itemlength)
	{
		String return_str="";
		String before="";
		String after="";
		try
		{
			if(value==null|| "".equals(value))
			{
				return return_str;
			}
			if(format==null|| "".equals(format))
			{
				return value;
			}
			String prefix="";
			if(format.indexOf("@")!=-1)
			{
				String sub_str =value;
				if(value.indexOf(".")!=-1)
					sub_str=value.substring(0,value.indexOf("."));
				while(prefix.length()+sub_str.length()<itemlength)
					prefix+=" ";
			}
			if(format.indexOf("\"")!=-1)
			{
				/*if(format.indexOf("\"")==0)//在前
				{
					xx=format.substring(format.indexOf("\"")+1,format.lastIndexOf("\""));
					format = format.substring(0,format.indexOf("\""));
				}
				else//在后
				{
		    		xx=format.substring(format.indexOf("\"")+1,format.lastIndexOf("\""));
		    		format = format.substring(0,format.indexOf("\""));
				}*/
				String aformat = format;
				char[] achar=aformat.toCharArray();
				int y=1;
				StringBuffer sb_format=new StringBuffer("");
				StringBuffer aa=new StringBuffer("");
				boolean isBefore=false;
				for(int i=0;i<achar.length;i++)
				{
					if(achar[i]=='\"')
					{
						if(i==0)
							isBefore=true;
						if(y==1)
						{
							y=2;
						}
						else
						{
							if(isBefore)
							{
								if(i!=achar.length-1)
							 	{
							    	before=aa.toString();
						         	aa.setLength(0);
							 	}
							}
							y=1;
						}
					}
					if(y==2)//固定串
					{
						if(achar[i]!='\"')
					    	aa.append(achar[i]);
					}
					else{//格式串
						if(achar[i]!='\"')
					    	sb_format.append(achar[i]);
					}
				}
				after=aa.toString();
				format = sb_format.toString();
			}
			if(format.indexOf("0")!=-1&&(format.indexOf("!")==-1&&format.indexOf("#")==-1&&format.indexOf("%")==-1))
			{
				if(format.indexOf(".")==-1)
				{
	    			 DecimalFormat dcom = new DecimalFormat(format);
	     			 return_str=dcom.format(Double.parseDouble(value));
				}
				else
				{
					String t_str=getXS(value,format.substring(format.indexOf(".")+1).length());
					DecimalFormat dcom = new DecimalFormat(format);
	     			return_str=dcom.format(Double.parseDouble(t_str));
				}
			}
			else if(format.indexOf("!")!=-1)//00000000!根据格式判断是否显示小数位?
			{
				double d=Double.parseDouble(value);
				String t_format=format.substring(0,format.lastIndexOf("0")+1);
				if(format.indexOf("%")!=-1&&format.indexOf(".")!=-1)
				 {
					 d=d*100;
					 t_format=format.substring(0,format.lastIndexOf("."))+format.substring(format.indexOf(".")+1,format.lastIndexOf("0")+1);
				 }
				//if(format.indexOf("@")!=-1)
				//{
					// if(format.indexOf(".")!=-1)
					// {
					//	 if(value.indexOf(".")!=-1)
					//	 {
							// String integer=value.substring(0,value.lastIndexOf("."));
						//	 String scale=value.substring(value.lastIndexOf(".")+1);
							 
						// }
					// }
				//}
				//else
				//{
		       DecimalFormat dcom = new DecimalFormat(t_format);
	           return_str=dcom.format(d);
			//	}
				 
				 
			}
			else if(format.indexOf("!")==-1&&(format.indexOf("%")!=-1&&format.indexOf("#")!=-1))//#0.00%
			{
				double d=Double.parseDouble(value);
				d=d*100;
				String t_format=format.replaceAll("#","");
				t_format=t_format.replaceAll("%", "");
				DecimalFormat dcom = new DecimalFormat(t_format);
				return_str=dcom.format(d)+"%";
			}
			else if(format.indexOf(",")!=-1)//#,##00
			{
				int xs=0;
				String t_value=value;
				String p_value="";
				if(format.indexOf(".")!=-1)
				{
					xs=format.substring(format.lastIndexOf(".")+1).length();
				}
				t_value=getXS(value,xs);
				if(t_value.indexOf(".")!=-1)
				{  
					if(t_value.length()>t_value.substring(0,t_value.indexOf(".")).length()+1)
					{
			     		p_value=t_value.substring(t_value.indexOf(".")+1);//小数部分
					}
					t_value=t_value.substring(0,t_value.indexOf("."));//整数部分	
				}
				if(t_value.length()%3==0)
				{
					String[] t=new String[t_value.length()/3];
					StringBuffer buf=new StringBuffer();
					for(int i=0;i<t.length;i++)
					{
						t[i]=t_value.substring(i*3,i*3+3);
					}
					for(int j=0;j<t.length;j++)
					{
						buf.append(t[j]);
						buf.append(",");
					}
					buf.setLength(buf.length()-1);
					return_str=buf.toString()+(p_value.length()==0?"":("."+p_value));
				}
				else
				{
					String[] t=new String[(int)(t_value.length()/3)];
					StringBuffer buf=new StringBuffer();
					String temp=t_value.substring(0,t_value.length()%3);
					String tmp=t_value.substring(t_value.length()%3);
					for(int i=0;i<t.length;i++)
					{
						t[i]=tmp.substring(i*3,i*3+3);
					}
					buf.append(temp);
					for(int j=0;j<t.length;j++)
					{
						buf.append(",");
						buf.append(t[j]);
					}
					return_str=buf.toString()+(p_value.length()==0?"":("."+p_value));
				}
				
			}
			else if(format.indexOf("#")!=-1&&format.indexOf("%")==-1&&format.indexOf(",")==-1&&format.indexOf("!")==-1)//####.##
			{
				if(value.indexOf(".")==-1)
				{
					return_str=getXS(value,0);
				}
				else
				{
     				String v_xs=value.substring(value.indexOf(".")+1);
     				if(format.indexOf(".")!=-1)
     				{
     					String f_xs=format.substring(format.indexOf(".")+1);
     					if(v_xs.length()>f_xs.length())
     					{
     						return_str=getXS(value,f_xs.length());
     					}
     					else
     					{
     						return_str=getXS(value,v_xs.length());
     					}
     				}
     				else
     				{
     					return_str=getXS(value,v_xs.length());
     				}
     				while(return_str.endsWith("0")||return_str.endsWith("."))
     				{
     					if(return_str.endsWith("."))
     					{
     					    return_str=return_str.substring(0,return_str.length()-1);
     					    break;
     					}else
     					{
     						 return_str=return_str.substring(0,return_str.length()-1);
     					}
     					
     				}
				}
			}
			else
			{
				return_str=value;
			}
			return_str=return_str.replace("@", prefix);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return before+return_str+after;
	}
	 public String getXS(String str,int scale){
	    	if(str==null|| "null".equalsIgnoreCase(str)|| "".equals(str))
	    		str="0.00";
	    	BigDecimal m=new BigDecimal(str);
	    	BigDecimal one = new BigDecimal("1");
	    	return m.divide(one, scale, BigDecimal.ROUND_HALF_UP).toString();
	    }
	 public void updateLprogram(String salaryid,String xml)
		{
			try
			{
				String sql = "update salarytemplate set lprogram=? where salaryid="+salaryid;
				ArrayList list = new ArrayList();
				list.add(xml);
				ContentDAO dao = new ContentDAO(this.conn);
				dao.update(sql,list);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
		}
	 public void sortTemplateField(String ids,String bank_id)
	 {
		 try
		 {
			 StringBuffer buf = new StringBuffer("");
			 if(ids==null|| "".equals(ids))
				 return;
			 String[] arr=ids.split(",");
			 ArrayList list = new ArrayList();
			 for(int i=0;i<arr.length;i++)
			 {
				 buf.append("update gz_bank_item set norder="+(i+1));
				 buf.append(" where UPPER(field_name)='"+arr[i].toUpperCase()+"' and bank_id="+bank_id);
				 list.add(buf.toString());
				 buf.setLength(0);
			 }
			 ContentDAO dao = new ContentDAO(this.conn);
			 dao.batchUpdate(list);
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
	 }
	public String getSalaryid() {
		return salaryid;
	}
	public void setSalaryid(String salaryid) {
		this.salaryid = salaryid;
	}
	public Connection getConn() {
		return conn;
	}
	public void setConn(Connection conn) {
		this.conn = conn;
	}
	public UserView getUserview() {
		return userview;
	}
	public void setUserview(UserView userview) {
		this.userview = userview;
	}
}
