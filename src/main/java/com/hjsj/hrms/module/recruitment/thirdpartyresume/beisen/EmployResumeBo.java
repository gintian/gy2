package com.hjsj.hrms.module.recruitment.thirdpartyresume.beisen;

import com.hjsj.hrms.businessobject.hire.HireTemplateBo;
import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hjsj.hrms.businessobject.hire.PositionDemand;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;

public class EmployResumeBo {
	private Connection conn=null;
	private UserView userView;
	
	public EmployResumeBo(Connection conn) {
		this.conn=conn;
	}
	public EmployResumeBo(Connection conn,UserView view)
	{
		this.conn=conn;
		this.userView=view;
	}
	
	/**
	 * 取得招聘对象列表
	 * @return
	 */
	public ArrayList getHireObjectList()
	{
		ArrayList list=new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowset=null;
		try
		{
			list.add(new CommonData("-1","全部"));
			rowset=dao.search("select * from codeitem  where codesetid='35'");
			while(rowset.next())
			{
				list.add(new CommonData(rowset.getString("codeitemid"),rowset.getString("codeitemdesc")));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
/**
 * 取得简历导出指标 排除z03内容
 * @return
 */
	public ArrayList getFieldListExceptZ03()
	{
		ArrayList list=new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowset=null;
		RowSet rowset2=null;
		try
		{
			ParameterXMLBo xmlBo=new ParameterXMLBo(this.conn,"1");
			HashMap map=xmlBo.getAttributeValues();
			String fields="";
			if(map!=null)
				fields=(String)map.get("fields");
			if(!"".equals(fields))
			{
				String[] a_fields=fields.split("`");
				StringBuffer whl=new StringBuffer("");
				boolean un_flag_1 = false;
				boolean un_flag_2 = true;
				boolean um_flag_1=false;
				boolean um_flag_2=true;
				boolean k_flag_1=false;
				boolean k_flag_2=true;
				HashMap temp_map=new HashMap();
				for(int i=0;i<a_fields.length;i++)
				{
					if("B0110".equalsIgnoreCase(a_fields[i]))
					{
						un_flag_1=true;
					}
					if("E0122".equalsIgnoreCase(a_fields[i]))
					{
						um_flag_1=true;
					}
					if("E01A1".equalsIgnoreCase(a_fields[i]))
					{
						k_flag_1=true;
					}
					whl.append(",'"+a_fields[i]+"'");
				}
				rowset=dao.search("select * from fielditem where UPPER(itemid) in ("+whl.substring(1).toUpperCase()+")");
				rowset2=dao.search("select * from t_hr_busifield where UPPER(itemid) in ("+whl.substring(1).toUpperCase()+") and useflag='1' and state= '1' and fieldsetid ='Z03'  ");
				while(rowset.next())
				{
					if("M".equalsIgnoreCase(rowset.getString("itemtype")))
						continue;
					if("E0122".equalsIgnoreCase(rowset.getString("itemid")))
					{
						um_flag_2=false;
					}
					if("B0110".equalsIgnoreCase(rowset.getString("itemid")))
					{
						un_flag_2=false;
					}
					if("E01A1".equalsIgnoreCase(rowset.getString("itemid")))
					{
						k_flag_2=false;
					}
					FieldItem fieldItem1=new FieldItem();
					fieldItem1.setItemid(rowset.getString("itemid"));
					fieldItem1.setItemtype(rowset.getString("itemtype"));
					fieldItem1.setCodesetid(rowset.getString("codesetid"));
					fieldItem1.setItemdesc(rowset.getString("itemdesc"));
					fieldItem1.setFieldsetid(rowset.getString("fieldsetid"));
					fieldItem1.setDecimalwidth(rowset.getInt("decimalwidth"));
					fieldItem1.setUseflag(rowset.getString("useflag"));
					temp_map.put(rowset.getString("itemid").toLowerCase(),fieldItem1);
					
				}

				if(un_flag_1&&un_flag_2)
				{
					FieldItem fieldItem1=new FieldItem();
					fieldItem1.setItemid("B0110");
					fieldItem1.setItemtype("A");
					fieldItem1.setCodesetid("UN");
					fieldItem1.setItemdesc("单位");
					fieldItem1.setFieldsetid("A01");
					
					temp_map.put("b0110",fieldItem1);
				}
				if(um_flag_1&&um_flag_2)
				{
					FieldItem fieldItem1=new FieldItem();
					fieldItem1.setItemid("E0122");
					fieldItem1.setItemtype("A");
					fieldItem1.setCodesetid("UM");
					fieldItem1.setItemdesc("部门");
					fieldItem1.setFieldsetid("A01");
					
					temp_map.put("e0122",fieldItem1);
				}
				if(k_flag_1&&k_flag_2)
				{
					FieldItem fieldItem1=new FieldItem();
					fieldItem1.setItemid("E01A1");
					fieldItem1.setItemtype("A");
					fieldItem1.setCodesetid("@K");
					fieldItem1.setItemdesc("职位");
					fieldItem1.setFieldsetid("A01");
					
					temp_map.put("e01a1",fieldItem1);
				}
				for(int i=0;i<a_fields.length;i++)
				{
					FieldItem item=(FieldItem)temp_map.get(a_fields[i].toLowerCase());
					if(item!=null){
						if("1".equals(item.getUseflag()))
							list.add(item);
					}
						
					
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
	 * 取得简历导出指标 
	 * @return
	 */
	public ArrayList getFieldList()
	{
		ArrayList list=new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowset=null;
		RowSet rowset2=null;
		try
		{
			ParameterXMLBo xmlBo=new ParameterXMLBo(this.conn,"1");
			HashMap map=xmlBo.getAttributeValues();
			String fields="";
			if(map!=null)
				fields=(String)map.get("fields");
			if(!"".equals(fields))
			{
				String[] a_fields=fields.split("`");
				StringBuffer whl=new StringBuffer("");
				boolean un_flag_1 = false;
				boolean un_flag_2 = true;
				boolean um_flag_1=false;
				boolean um_flag_2=true;
				boolean k_flag_1=false;
				boolean k_flag_2=true;
				HashMap temp_map=new HashMap();
				for(int i=0;i<a_fields.length;i++)
				{
					if("B0110".equalsIgnoreCase(a_fields[i]))
					{
						un_flag_1=true;
					}
					if("E0122".equalsIgnoreCase(a_fields[i]))
					{
						um_flag_1=true;
					}
					if("E01A1".equalsIgnoreCase(a_fields[i]))
					{
						k_flag_1=true;
					}
					whl.append(",'"+a_fields[i]+"'");
				}
				rowset=dao.search("select * from fielditem where UPPER(itemid) in ("+whl.substring(1).toUpperCase()+")");
				rowset2=dao.search("select * from t_hr_busifield where UPPER(itemid) in ("+whl.substring(1).toUpperCase()+") and useflag='1' and state= '1' and fieldsetid ='Z03'  ");
				while(rowset.next())//取输出的字段
				{
					if("M".equalsIgnoreCase(rowset.getString("itemtype")))
						continue;
					if("E0122".equalsIgnoreCase(rowset.getString("itemid")))
					{
						um_flag_2=false;
					}
					if("B0110".equalsIgnoreCase(rowset.getString("itemid")))
					{
						un_flag_2=false;
					}
					if("E01A1".equalsIgnoreCase(rowset.getString("itemid")))
					{
						k_flag_2=false;
					}
					FieldItem fieldItem1=new FieldItem();
					fieldItem1.setItemid(rowset.getString("itemid"));
					fieldItem1.setItemtype(rowset.getString("itemtype"));
					fieldItem1.setCodesetid(rowset.getString("codesetid"));
					fieldItem1.setItemdesc(rowset.getString("itemdesc"));
					fieldItem1.setFieldsetid(rowset.getString("fieldsetid"));
					fieldItem1.setDecimalwidth(rowset.getInt("decimalwidth"));
					fieldItem1.setUseflag(rowset.getString("useflag"));
					temp_map.put(rowset.getString("itemid").toLowerCase(),fieldItem1);
					//list.add(fieldItem1);
					
				}
				while(rowset2.next())		
				{
					FieldItem fieldItem2=new FieldItem();
					fieldItem2.setItemid(rowset2.getString("itemid"));
					fieldItem2.setItemtype(rowset2.getString("itemtype"));
					fieldItem2.setCodesetid(rowset2.getString("codesetid"));
					fieldItem2.setItemdesc(rowset2.getString("itemdesc"));
					fieldItem2.setFieldsetid(rowset2.getString("fieldsetid"));
					fieldItem2.setDecimalwidth(rowset2.getInt("decimalwidth"));
					fieldItem2.setUseflag(rowset2.getString("useflag"));
					temp_map.put(rowset2.getString("itemid").toLowerCase(),fieldItem2);
				}
				if(un_flag_1&&un_flag_2)
				{
					FieldItem fieldItem1=new FieldItem();
					fieldItem1.setItemid("B0110");
					fieldItem1.setItemtype("A");
					fieldItem1.setCodesetid("UN");
					fieldItem1.setItemdesc("单位");
					fieldItem1.setFieldsetid("A01");
					
					temp_map.put("b0110",fieldItem1);
				}
				if(um_flag_1&&um_flag_2)
				{
					FieldItem fieldItem1=new FieldItem();
					fieldItem1.setItemid("E0122");
					fieldItem1.setItemtype("A");
					fieldItem1.setCodesetid("UM");
					fieldItem1.setItemdesc("部门");
					fieldItem1.setFieldsetid("A01");
					
					temp_map.put("e0122",fieldItem1);
				}
				if(k_flag_1&&k_flag_2)
				{
					FieldItem fieldItem1=new FieldItem();
					fieldItem1.setItemid("E01A1");
					fieldItem1.setItemtype("A");
					fieldItem1.setCodesetid("@K");
					fieldItem1.setItemdesc("职位");
					fieldItem1.setFieldsetid("A01");
					
					temp_map.put("e01a1",fieldItem1);
				}
				for(int i=0;i<a_fields.length;i++)
				{
					FieldItem item=(FieldItem)temp_map.get(a_fields[i].toLowerCase());
					if(item!=null){
						if("1".equals(item.getUseflag()))
							list.add(item);
					}
						
					
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
	 * getResumeSql2修改
	 * @param fieldList
	 * @param nbase
	 * @param codeset
	 * @param codeid
	 * @param resumeState
	 * @param z0301
	 * @param queryType
	 * @param subSetType
	 * @return
	 */
	public String getResumeSql2Modify(ArrayList fieldList,String nbase,String codeset,String codeid,String resumeState,String z0301,String queryType,int subSetType,String encryption_sql )
	{
		StringBuffer sql=new StringBuffer("");
		String tempName="";

		StringBuffer select_str=new StringBuffer(" select "+nbase+"A01.A0100");
		if(subSetType==1)
			select_str.append(",temp.resume_flag");
		StringBuffer from_str=new StringBuffer(" from ("+encryption_sql+")  zhubiao left join "+nbase+"A01  on zhubiao.a0100="+nbase+"A01.a0100");					
		HashMap fieldSetMap=new HashMap();
		fieldSetMap.put("A01","1");
		
		for(int i=0;i<fieldList.size();i++)
		{
			FieldItem item=(FieldItem)fieldList.get(i);
			String itemid=item.getItemid();
			String itemtype=item.getItemtype();
			String fieldsetid=item.getFieldsetid();
			String codesetid=item.getCodesetid();
			
			if("z03".equalsIgnoreCase(fieldsetid)){
				   tempName=fieldsetid;
			    
				}else {
					tempName=nbase+fieldsetid;
					
				}
			StringBuffer viewSql=new StringBuffer("");
			if("M".equals(itemtype)&&!"z03".equalsIgnoreCase(fieldsetid))
				continue;
			
			if(fieldSetMap.get(fieldsetid)==null&&!"z03".equalsIgnoreCase(fieldsetid))
			{
				
				
				
				viewSql.append("(SELECT * FROM ");
				viewSql.append(tempName);
				viewSql.append(" A WHERE A.I9999 =(SELECT MAX(B.I9999) FROM "); 
				viewSql.append(tempName);
				viewSql.append(" B WHERE ");
				viewSql.append(" A.A0100=B.A0100  )) ");
				viewSql.append(tempName);
				from_str.append(" left join "+viewSql.toString()+" on "+nbase+"A01.a0100="+tempName+".a0100 ");
				fieldSetMap.put(fieldsetid.toUpperCase(),"1");
			}
			if(subSetType==1){
		    	String column=tempName+"."+itemid;
		    	select_str.append(","+column);
			}
		}
		
		sql.append(select_str.toString());
		sql.append(from_str.toString());
		if("-1".equals(resumeState)){
			sql.append(" left join (select z03.*,zp.A0100 from zp_pos_tache zp left join z03 on z03.z0301 = zp.zp_pos_id) z03 on z03.a0100 =zhubiao.a0100  ");
		}else{
			sql.append(" left join (select z03.*,zp.A0100 from zp_pos_tache zp left join z03 on z03.z0301 = zp.zp_pos_id) z03 on z03.a0100 =zhubiao.a0100 and z03.z0301=zhubiao.zp_pos_id ");
		}
		
		StringBuffer condSql = new StringBuffer();
		
		condSql.append(" select t.a0100,t.resume_flag,t.zp_pos_id from zp_pos_tache t,z03 z where t.zp_pos_id = z.z0301");					
			if("-0".equals(codeid))
			{
				condSql.append(" and z.z0311 like '#' ");
			}
			else if(codeid.indexOf("`")==-1&&!"".equals(codeid))
			{
				String _str=Sql_switcher.isnull("z.z0336","''");
				condSql.append(" and ( ( z.z0311 like '"+codeid.substring(2)+"%' and  "+_str+"<>'01' ) or ( z.z0321 like '"+codeid.substring(2)+"%' and  "+_str+"='01' ) or ( z.z0325 like '"+codeid.substring(2)+"%' and  "+_str+"='01' ) ) ");
			}
			else if("".equals(codeid))
			{
				condSql.append(" and (z.z0311 like '%' or z.z0311 is null)");
			}
			else
			{
				String[] temp=codeid.split("`");
				String _str=Sql_switcher.isnull("z.z0336","''");
	    		StringBuffer tempSql=new StringBuffer("");
	    		StringBuffer tempSql2=new StringBuffer("");
				StringBuffer tempSql3=new StringBuffer("");
		    	for(int i=0;i<temp.length;i++)
	     		{
	    			if(temp[i]==null|| "".equals(temp[i]))
		    			continue;
		    		tempSql.append(" or  z.z0311 like '"+temp[i].substring(2)+"%'");
		    		tempSql2.append(" or z.z0321 like '"+temp[i].substring(2)+"%'");
					tempSql3.append(" or z.z0325 like '"+temp[i].substring(2)+"%'");
	    		}
		    	condSql.append(" and ( ( ( "+tempSql.substring(3)+" ) and  "+_str+"<>'01' ) or ( ( "+tempSql2.substring(3)+" ) and  "+_str+"='01' ) or ( ( "+tempSql3.substring(3)+" ) and  "+_str+"='01' ) ) ");

			}
		if(resumeState!=null&&!"-1".equals(resumeState)&&!"-2".equals(resumeState)&&!"-3".equals(resumeState))//
			condSql.append(" and t.resume_flag='"+resumeState+"'");
		if(resumeState!=null&& "12".equals(resumeState))
		{
			if("0".equals(queryType))
			{
				condSql.append(" and (UPPER(t.username)='"+this.userView.getUserName().toUpperCase()+"' or t.username is null)");
			}
		}
		if(!"-1".equals(z0301))
			condSql.append(" and t.zp_pos_id='"+z0301+"'");
		if(condSql!=null&&!"".equals(condSql.toString()))
		{
			if("-3".equals(resumeState)|| "-2".equals(resumeState))
			{
				sql.append(" left join ("+condSql+") temp on zhubiao.a0100=temp.a0100 and zhubiao.ZP_POS_ID=temp.zp_pos_id where 1=1");
			}
			else
			{
	         	sql.append(",("+condSql+") temp where zhubiao.a0100=temp.a0100 and zhubiao.ZP_POS_ID=temp.zp_pos_id ");
			}
		}
		/**未选职位的人员*/
		if("-1".equals(resumeState))
			sql.append(" and "+nbase+"a01.a0100 not in (select a0100 from zp_pos_tache)  and "+nbase+"A01.a0100 is not null ");
		ParameterXMLBo xmlBo=new ParameterXMLBo(this.conn);
		try
		{
	    	HashMap map=xmlBo.getAttributeValues();
	    	String active_field="";
			if(map!=null&&map.get("active_field")!=null&&!"".equals((String)map.get("active_field")))
			{
				active_field=(String)map.get("active_field");
			}
			if(active_field!=null&&!"".equals(active_field))
			{
				sql.append(" and ("+nbase+"a01."+active_field+"='1' or "+nbase+"A01."+active_field+" is null)");
				if(!(this.userView.isSuper_admin()|| "1".equals(this.userView.getGroupId())))
				{
					if("-2".equals(resumeState)|| "-3".equals(resumeState))
					{
						if(this.userView.getStatus()==0)
						{
							String unitid=this.userView.getUnit_id();
							if(unitid==null|| "".equals(unitid))
								sql.append(" and 1=2 ");
							else if("UN`".equalsIgnoreCase(unitid))
							{
								//全部，现在处理为都可以看见
							}
							else
							{
								String[] unit_arr=this.userView.getUnit_id().split("`");
								for(int i=0;i<unit_arr.length;i++)
								{
									if(unit_arr[i]==null|| "".equals(unit_arr[i]))
										continue;
									String org=unit_arr[i].substring(2);
									sql.append(" and "+nbase+"a01.b0110 not like '"+org+"%'");
								}
							}
						}
						else
						{
				        	String org=this.userView.getUserOrgId();
				        	if(org==null|| "".equals(org))
				    	    	sql.append(" and 1=2 ");
				            else
			                	sql.append(" and "+nbase+"a01.b0110 not like '"+org+"%'");
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return sql.toString();
		
	}
	
	public String getResumeSql2(ArrayList fieldList,String nbase,String codeset,String codeid,String resumeState,String z0301,String queryType,int subSetType)
	{
		StringBuffer sql=new StringBuffer("");
		String tempName="";

		StringBuffer select_str=new StringBuffer(" select "+nbase+"A01.A0100");
		if(subSetType==1)
			select_str.append(",temp.resume_flag");
		StringBuffer from_str=new StringBuffer(" from "+nbase+"A01 ");					
		HashMap fieldSetMap=new HashMap();
		fieldSetMap.put("A01","1");
		
		for(int i=0;i<fieldList.size();i++)
		{
			FieldItem item=(FieldItem)fieldList.get(i);
			String itemid=item.getItemid();
			String itemtype=item.getItemtype();
			String fieldsetid=item.getFieldsetid();
			String codesetid=item.getCodesetid();
			
			if("z03".equalsIgnoreCase(fieldsetid)){
				   tempName=fieldsetid;
			    
				}else {
					tempName=nbase+fieldsetid;
					
				}
			StringBuffer viewSql=new StringBuffer("");
			if("M".equals(itemtype)&&!"z03".equalsIgnoreCase(fieldsetid))
				continue;
			
			if(fieldSetMap.get(fieldsetid)==null&&!"z03".equalsIgnoreCase(fieldsetid))
			{
				
				
				
				viewSql.append("(SELECT * FROM ");
				viewSql.append(tempName);
				viewSql.append(" A WHERE A.I9999 =(SELECT MAX(B.I9999) FROM "); 
				viewSql.append(tempName);
				viewSql.append(" B WHERE ");
				viewSql.append(" A.A0100=B.A0100  )) ");
				viewSql.append(tempName);
				from_str.append(" left join "+viewSql.toString()+" on "+nbase+"A01.a0100="+tempName+".a0100 ");
				fieldSetMap.put(fieldsetid.toUpperCase(),"1");
			}
			if(subSetType==1){
		    	String column=tempName+"."+itemid;
		    	select_str.append(","+column);
			}
		}
		
		sql.append(select_str.toString());
		sql.append(from_str.toString());
		sql.append(" left join (select z03.*,zp.A0100 from zp_pos_tache zp left join z03 on z03.z0301 = zp.zp_pos_id) z03 on z03.a0100 = "+nbase+"A01.a0100");
		StringBuffer condSql = new StringBuffer();
		
		condSql.append(" select t.a0100,t.resume_flag from zp_pos_tache t,z03 z where t.zp_pos_id = z.z0301");					
			if("-0".equals(codeid))
			{
				condSql.append(" and z.z0311 like '#' ");
			}
			else if(codeid.indexOf("`")==-1&&!"".equals(codeid))
			{
				String _str=Sql_switcher.isnull("z.z0336","''");
				condSql.append(" and ( ( z.z0311 like '"+codeid.substring(2)+"%' and  "+_str+"<>'01' ) or ( z.z0321 like '"+codeid.substring(2)+"%' and  "+_str+"='01' ) or ( z.z0325 like '"+codeid.substring(2)+"%' and  "+_str+"='01' ) ) ");
			}
			else if("".equals(codeid))
			{
				condSql.append(" and (z.z0311 like '%' or z.z0311 is null)");
			}
			else
			{
				String[] temp=codeid.split("`");
				String _str=Sql_switcher.isnull("z.z0336","''");
	    		StringBuffer tempSql=new StringBuffer("");
	    		StringBuffer tempSql2=new StringBuffer("");
				StringBuffer tempSql3=new StringBuffer("");
		    	for(int i=0;i<temp.length;i++)
	     		{
	    			if(temp[i]==null|| "".equals(temp[i]))
		    			continue;
		    		tempSql.append(" or  z.z0311 like '"+temp[i].substring(2)+"%'");
		    		tempSql2.append(" or z.z0321 like '"+temp[i].substring(2)+"%'");
					tempSql3.append(" or z.z0325 like '"+temp[i].substring(2)+"%'");
	    		}
		    	condSql.append(" and ( ( ( "+tempSql.substring(3)+" ) and  "+_str+"<>'01' ) or ( ( "+tempSql2.substring(3)+" ) and  "+_str+"='01' ) or ( ( "+tempSql3.substring(3)+" ) and  "+_str+"='01' ) ) ");

			}
		if(resumeState!=null&&!"-1".equals(resumeState)&&!"-2".equals(resumeState)&&!"-3".equals(resumeState))//
			condSql.append(" and t.resume_flag='"+resumeState+"'");
		if(resumeState!=null&& "12".equals(resumeState))
		{
			if("0".equals(queryType))
			{
				condSql.append(" and (UPPER(t.username)='"+this.userView.getUserName().toUpperCase()+"' or t.username is null)");
			}
		}
		if(!"-1".equals(z0301))
			condSql.append(" and t.zp_pos_id='"+z0301+"'");
		if(condSql!=null&&!"".equals(condSql.toString()))
		{
			if("-3".equals(resumeState)|| "-2".equals(resumeState))
			{
				sql.append(" left join ("+condSql+") temp on "+nbase+"A01.a0100=temp.a0100 where 1=1");
			}
			else
			{
	         	sql.append(",("+condSql+") temp where "+nbase+"A01.a0100=temp.a0100 ");
			}
		}
		/**未选职位的人员*/
		if("-1".equals(resumeState))
			sql.append(" and "+nbase+"a01.a0100 not in (select a0100 from zp_pos_tache)  and "+nbase+"A01.a0100 is not null ");
		ParameterXMLBo xmlBo=new ParameterXMLBo(this.conn);
		try
		{
	    	HashMap map=xmlBo.getAttributeValues();
	    	String active_field="";
			if(map!=null&&map.get("active_field")!=null&&!"".equals((String)map.get("active_field")))
			{
				active_field=(String)map.get("active_field");
			}
			if(active_field!=null&&!"".equals(active_field))
			{
				sql.append(" and ("+nbase+"a01."+active_field+"='1' or "+nbase+"A01."+active_field+" is null)");
				if(!(this.userView.isSuper_admin()|| "1".equals(this.userView.getGroupId())))
				{
					if("-2".equals(resumeState)|| "-3".equals(resumeState))
					{
						if(this.userView.getStatus()==0)
						{
							String unitid=this.userView.getUnit_id();
							if(unitid==null|| "".equals(unitid))
								sql.append(" and 1=2 ");
							else if("UN`".equalsIgnoreCase(unitid))
							{
								//全部，现在处理为都可以看见
							}
							else
							{
								String[] unit_arr=this.userView.getUnit_id().split("`");
								for(int i=0;i<unit_arr.length;i++)
								{
									if(unit_arr[i]==null|| "".equals(unit_arr[i]))
										continue;
									String org=unit_arr[i].substring(2);
									sql.append(" and "+nbase+"a01.b0110 not like '"+org+"%'");
								}
							}
						}
						else
						{
				        	String org=this.userView.getUserOrgId();
				        	if(org==null|| "".equals(org))
				    	    	sql.append(" and 1=2 ");
				            else
			                	sql.append(" and "+nbase+"a01.b0110 not like '"+org+"%'");
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return sql.toString();
		
	}
	public String getResumeSql3(ArrayList fieldList,String nbase,String resumeState,UserView userView,int type,int subSetType)
	{
		String tempName="";
		StringBuffer sql=new StringBuffer("");
		StringBuffer select_str=new StringBuffer(" select "+nbase+"A01.A0100");
		StringBuffer from_str=new StringBuffer(" from "+nbase+"A01 ");					
		HashMap fieldSetMap=new HashMap();
		fieldSetMap.put("A01","1");
		
		for(int i=0;i<fieldList.size();i++)
		{
			FieldItem item=(FieldItem)fieldList.get(i);
			String itemid=item.getItemid();
			String itemtype=item.getItemtype();
			String fieldsetid=item.getFieldsetid();
			String codesetid=item.getCodesetid();
			if("z03".equalsIgnoreCase(fieldsetid)){
				   tempName=fieldsetid;
			    
				}else {
					tempName=nbase+fieldsetid;
					
				}
			
			//String tempName=nbase+fieldsetid;
			if("M".equals(itemtype)&&!"z03".equalsIgnoreCase(fieldsetid))
				continue;
			if("z03".equalsIgnoreCase(fieldsetid)){//人才库、应聘简历处 不导出涉及z03的信息
				continue;
			}
			if(fieldSetMap.get(fieldsetid)==null&&!"z03".equalsIgnoreCase(fieldsetid))
			{
				
				StringBuffer viewSql=new StringBuffer("");
				viewSql.append("( SELECT * FROM ");
				viewSql.append(tempName);
				viewSql.append(" A WHERE A.I9999 =(SELECT MAX(B.I9999) FROM "); 
				viewSql.append(tempName);
				viewSql.append(" B WHERE ");
				viewSql.append(" A.A0100=B.A0100  )) ");
				viewSql.append(tempName);
				from_str.append(" left join "+viewSql.toString()+" on "+nbase+"A01.a0100="+tempName+".a0100 ");
				fieldSetMap.put(fieldsetid.toUpperCase(),"1");
			}
			if(subSetType==1){
	    		String column=tempName+"."+itemid;
		    	select_str.append(","+column);
			}
		}
		
		sql.append(select_str.toString());
		sql.append(from_str.toString());
		if(type==1)
	    	sql.append(" where "+nbase+"a01.a0100 in(select a0100 from zp_resume_pack  where  logonname='"+userView.getUserId()+"' and nbase='"+nbase+"')");
		return sql.toString();
		
	}
	/**
	 * getResumeSql的修改
	 */
	public String getResumeSqlModify(ArrayList fieldList,String nbase,String a0100,int subSetType,String number,String personType,String encryption_sql,String resumeState)
	{

		StringBuffer sql=new StringBuffer("");
		StringBuffer select_str=new StringBuffer(" select "+nbase+"A01.A0100");
		StringBuffer from_str=new StringBuffer(" from ("+encryption_sql+")  zhubiao left join "+nbase+"A01  on zhubiao.a0100="+nbase+"A01.a0100");	
		HashMap fieldSetMap=new HashMap();
		fieldSetMap.put("A01","1");
		
		for(int i=0;i<fieldList.size();i++)
		{
			FieldItem item=(FieldItem)fieldList.get(i);
			String itemid=item.getItemid();
			String tempName="";

			String itemtype=item.getItemtype();
			String fieldsetid=item.getFieldsetid();
			String codesetid=item.getCodesetid();
			if("z03".equalsIgnoreCase(fieldsetid)){
				   tempName=fieldsetid;
			    
				}else {
					tempName=nbase+fieldsetid;
					
				}
			if("M".equals(itemtype)&&!"z03".equalsIgnoreCase(fieldsetid))
				continue;
			
			if(!"0".equals(personType)&& "z03".equalsIgnoreCase(fieldsetid)){//人才库、应聘简历处 不导出涉及z03的信息
				continue;
			}
			if(fieldSetMap.get(fieldsetid)==null&&!"z03".equalsIgnoreCase(fieldsetid))
			{
				StringBuffer viewSql=new StringBuffer("");
				viewSql.append("( SELECT * FROM ");
				viewSql.append(tempName);
				viewSql.append(" A WHERE A.I9999 =(SELECT MAX(B.I9999) FROM "); 
				viewSql.append(tempName);
				viewSql.append(" B WHERE ");
				viewSql.append(" A.A0100=B.A0100  )) ");
				viewSql.append(tempName);
				from_str.append(" left join "+viewSql.toString()+" on "+nbase+"A01.a0100="+tempName+".a0100 ");
				fieldSetMap.put(fieldsetid.toUpperCase(),"1");
			}
			if(subSetType==1)
			{
		    	String column=tempName+"."+itemid;
		    	select_str.append(","+column);
			}
		}
		
		sql.append(select_str.toString());
		sql.append(from_str.toString());
		if("0".equals(personType)){//应聘库
			if("-1".equals(resumeState)){//未选岗位
				sql.append(" left join (select z03.*,zp.A0100,zp.thenumber from zp_pos_tache zp left join z03 on z03.z0301 = zp.zp_pos_id) z03 on z03.a0100 =zhubiao.a0100 ");
			}else{
				sql.append(" left join (select z03.*,zp.A0100,zp.thenumber from zp_pos_tache zp left join z03 on z03.z0301 = zp.zp_pos_id) z03 on z03.a0100 =zhubiao.a0100 and z03.z0301=zhubiao.zp_pos_id ");
			}
			
		}

		sql.append(" where ");
		
		String[] a0100s=a0100.split("#");
		String[] numbers=number.split("#");
		StringBuffer whl1=new StringBuffer("");
		for(int i=0;i<a0100s.length;i++)
		{
			if("$".equals(numbers[i]))
			whl1.append(" or ("+nbase+"a01.a0100 = '"+a0100s[i]+"')");
			else
			whl1.append(" or ("+nbase+"a01.a0100 = '"+a0100s[i]+"' and z03.thenumber = "+numbers[i]+")");
		
		}
		sql.append(" "+whl1.substring(3)+"");
		return sql.toString();
		

	}
	
	public String getResumeSql(ArrayList fieldList,String nbase,String a0100,int subSetType,String number,String personType)
	{

		StringBuffer sql=new StringBuffer("");
		StringBuffer select_str=new StringBuffer(" select "+nbase+"A01.A0100");
		StringBuffer from_str=new StringBuffer(" from "+nbase+"A01 ");					
		HashMap fieldSetMap=new HashMap();
		fieldSetMap.put("A01","1");
		
		for(int i=0;i<fieldList.size();i++)
		{
			FieldItem item=(FieldItem)fieldList.get(i);
			String itemid=item.getItemid();
			String tempName="";

			String itemtype=item.getItemtype();
			String fieldsetid=item.getFieldsetid();
			String codesetid=item.getCodesetid();
			if("z03".equalsIgnoreCase(fieldsetid)){
				   tempName=fieldsetid;
			    
				}else {
					tempName=nbase+fieldsetid;
					
				}
			if("M".equals(itemtype)&&!"z03".equalsIgnoreCase(fieldsetid))
				continue;
			
			if(fieldSetMap.get(fieldsetid)==null&&!"z03".equalsIgnoreCase(fieldsetid))
			{
				StringBuffer viewSql=new StringBuffer("");
				viewSql.append("( SELECT * FROM ");
				viewSql.append(tempName);
				viewSql.append(" A WHERE A.I9999 =(SELECT MAX(B.I9999) FROM "); 
				viewSql.append(tempName);
				viewSql.append(" B WHERE ");
				viewSql.append(" A.A0100=B.A0100  )) ");
				viewSql.append(tempName);
				from_str.append(" left join "+viewSql.toString()+" on "+nbase+"A01.a0100="+tempName+".a0100 ");
				fieldSetMap.put(fieldsetid.toUpperCase(),"1");
			}
			if(subSetType==1)
			{
		    	String column=tempName+"."+itemid;
		    	select_str.append(","+column);
			}
		}
		
		sql.append(select_str.toString());
		sql.append(from_str.toString());
		//zp_pos_tache zpt  on  zpt.a0100 = OthA01.a0100 left join
		sql.append(" left join (select z03.*,zp.A0100,zp.thenumber from zp_pos_tache zp left join z03 on z03.z0301 = zp.zp_pos_id) z03 on z03.a0100 = "+nbase+"A01.a0100");
		sql.append(" where ");
		
		String[] a0100s=a0100.split("#");
		String[] numbers=number.split("#");
		StringBuffer whl1=new StringBuffer("");
		if("0".equals(personType)){
		for(int i=0;i<a0100s.length;i++)
		{
			//whl1.append(",'"+a0100s[i]+"'");
			if("$".equals(numbers[i]))
			whl1.append(" or ("+nbase+"a01.a0100 = '"+a0100s[i]+"')");
			else
			whl1.append(" or ("+nbase+"a01.a0100 = '"+a0100s[i]+"' and z03.thenumber = "+numbers[i]+")");
		
		}
		sql.append(" "+whl1.substring(3)+"");
		//sql.append(" "+nbase+"a01.a0100 in ("+whl1.substring(1)+")");
		}else{
		
		for(int i=0;i<a0100s.length;i++)
		{
			whl1.append(",'"+a0100s[i]+"'");
	    }
		sql.append(" "+nbase+"a01.a0100 in ("+whl1.substring(1)+")");
		}
		return sql.toString();
		

	}
	
	
	
	/**
	 * 取得简历状态列表
	 * @return
	 */
	public ArrayList getResumeStateList(String activeField)
	{
		ArrayList list=new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowset=null;
		try
		{
			rowset=dao.search("select * from codeitem where codesetid='36' and parentid='1' and parentid<>codeitemid order by codeitemid");
			while(rowset.next())
			{
				CommonData data=new CommonData(rowset.getString("codeitemid"),rowset.getString("codeitemdesc"));
				list.add(data);
			}
			CommonData data1=new CommonData("-1","未选岗位");
			list.add(data1);
			if(activeField!=null&&!"".equals(activeField))
			{
		    	data1=new CommonData("-2","已选岗位");
		    	list.add(data1);
			}
			data1=new CommonData("-3","全部");
			list.add(data1);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	
	
	public String[] getResumeFieldStr()
	{
		String[] resumes=null;
		try
		{
			ParameterXMLBo xmlBo=new ParameterXMLBo(this.conn);
			HashMap map=xmlBo.getAttributeValues();
			String resume_field="";
			if(map.get("resume_field")!=null)
				resume_field=(String)map.get("resume_field");
			
			
		//	resume_field="a0101,a0111,a0177,a0107,a1905,a1910,c1901";
			if(resume_field.length()!=0)
			{
				resumes=resume_field.split(",");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return resumes;
	}
	
	
	public HashMap getResumeFieldMap(String[] resumes,String personType,String resumeState)
	{
		
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowset=null;
		RowSet rowset1=null;
		HashMap field_map=new HashMap();
		try
		{
			if(resumes!=null&&resumes.length>0)
			{
				StringBuffer whl=new StringBuffer("");
				for(int i=0;i<resumes.length;i++)
				{

					if(resumes[i].trim().length()>0)
					{
						whl.append(" or itemid='"+resumes[i]+"'");
					}
				}
				if(whl.length()>0)
				{
					rowset=dao.search("select * from fielditem where ("+whl.substring(3)+") and useflag='1'");
					rowset1=dao.search("select * from t_hr_busifield where fieldsetid='Z03' and ("+whl.substring(3)+") and useflag='1' and state= '1'");
					LazyDynaBean abean=null;
					while(rowset.next())
					{
						abean=new LazyDynaBean();
						abean.set("itemtype",rowset.getString("itemtype"));
						abean.set("itemdesc",rowset.getString("itemdesc"));
						abean.set("codesetid",rowset.getString("codesetid"));
						abean.set("itemid",rowset.getString("itemid"));
						abean.set("fieldsetid",rowset.getString("fieldsetid"));
						field_map.put(rowset.getString("itemid").toLowerCase(),abean);
					}
					if("4".equals(personType)|| "1".equals(personType)){//是人才库和收藏夹的时候屏蔽掉z03里面的字段
	                }else{
	                    while(rowset1.next()){
	                        abean=new LazyDynaBean();
	                        abean.set("itemtype",rowset1.getString("itemtype"));
	                        abean.set("itemdesc",rowset1.getString("itemdesc"));
	                        abean.set("codesetid",rowset1.getString("codesetid"));
	                        abean.set("itemid",rowset1.getString("itemid"));
	                        abean.set("fieldsetid",rowset1.getString("fieldsetid"));
	                        field_map.put(rowset1.getString("itemid").toLowerCase(),abean);
	                    }   
	                }
					
					
					
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		if("0".equals(personType)&&!"-1".equals(resumeState))
		{
			LazyDynaBean abean1=new LazyDynaBean();
			abean1.set("itemtype","D");
			abean1.set("itemdesc","应聘时间");
			abean1.set("codesetid","0");
			abean1.set("itemid","apply_date");
			abean1.set("fieldsetid","zp_pos_tache");
			field_map.put("apply_date",abean1);
		}
		else if("1".equals(personType)|| "4".equals(personType)|| "-1".equals(resumeState))
		{
			LazyDynaBean abean1=new LazyDynaBean();
			abean1.set("itemtype","D");
			abean1.set("itemdesc","简历入库时间");
			abean1.set("codesetid","0");
			abean1.set("itemid","createtime");
			abean1.set("fieldsetid","A01");
			field_map.put("createtime",abean1);
			
		}
		return field_map;
	}
	
	
	/**
	 * 取得简历预览指标列表
	 * @param fieldMap
	 * @param resumes
	 * @param personType // 0:应聘库 1：人才库
	 * @return
	 */
	public ArrayList getResumeFieldList(HashMap fieldMap,String[] resumes,String personType,String resumeState)
	{
		ArrayList list=new ArrayList();
		try
		{
		int n=0;
		if(resumes!=null)
		{
			for(int i=0;i<resumes.length;i++)
			{
				if(resumes[i].trim().length()>0)
				{
					n++;
					LazyDynaBean abean=(LazyDynaBean)fieldMap.get(resumes[i].toLowerCase());
					if(abean!=null)
					{
			    		String desc=(String)abean.get("itemdesc");
			    		String value=(String)abean.get("itemid");
			    		CommonData data=new CommonData(value,desc);
				    	list.add(data);
					}
					
					if(n==4)
					{
						LazyDynaBean abean1=null;
						if("0".equals(personType)&&!"-1".equals(resumeState))
						{
							abean1=(LazyDynaBean)fieldMap.get("apply_date");
						}
						else if("1".equals(personType)|| "4".equals(personType)|| "-1".equals(resumeState))
						{
							abean1=(LazyDynaBean)fieldMap.get("createtime");
							
						}
						String desc1=(String)abean1.get("itemdesc");
						String value1=(String)abean1.get("itemid");
						CommonData data1=new CommonData(value1,desc1);
						list.add(data1);
						n++;
					}
				}
			}
		}
		
		if(n<5)
		{
			LazyDynaBean abean1=null;
			if("0".equals(personType)&&!"-1".equals(resumeState))
			{
				abean1=(LazyDynaBean)fieldMap.get("apply_date");
			}
			else if("1".equals(personType)|| "4".equals(personType)|| "-1".equals(resumeState))
			{
				abean1=(LazyDynaBean)fieldMap.get("createtime");
				
			}			
			String desc1=(String)abean1.get("itemdesc");
			String value1=(String)abean1.get("itemid");
			CommonData data1=new CommonData(value1,desc1);
			list.add(data1);
		}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	
	/**
	 * 取得排序字段列表集合
	 * @param field1
	 * @param field2
	 * @param field3
	 * @param field4
	 * @param field5
	 * @param fieldMap
	 * @return
	 */
	public ArrayList getOrderItemList(String field1,String field2,String field3,String field4,String field5,HashMap fieldMap,String personType)
	{
		HashMap map=new HashMap();
		map.put(field1.toLowerCase(),"1");
		map.put(field2.toLowerCase(),"1");
		map.put(field3.toLowerCase(),"1");
		map.put(field4.toLowerCase(),"1");
		map.put(field5.toLowerCase(),"1");
		ArrayList list=new ArrayList();
		if("0".equals(personType))
		{
	    	list.add(new CommonData("zp_pos_id",ResourceFactory.getProperty("e01a1.label")));
	    	try
	    	{
	        	ParameterXMLBo xmlBo=new ParameterXMLBo(this.conn);
		    	HashMap amap=xmlBo.getAttributeValues();
		    	String hireMajor="";
		    	if(amap.get("hireMajor")!=null)
		    	{
		    		hireMajor=(String)amap.get("hireMajor");
		    	}
		    	if(hireMajor!=null&&hireMajor.trim().length()>0)
		    	{
		    		FieldItem item = DataDictionary.getFieldItem(hireMajor.toLowerCase());
		    		if(item!=null)
		    		{
		    			list.add(new CommonData(item.getItemid(),item.getItemdesc()));
		    		}
		    	}
	    	}catch(Exception e)
	    	{
	    		e.printStackTrace();
	    	}
		}
		Set set=map.keySet();
		for(Iterator t=set.iterator();t.hasNext();)
		{
			String temp=(String)t.next();
			LazyDynaBean abean1=(LazyDynaBean)fieldMap.get(temp);
			if(abean1!=null)
			{
				String desc1=(String)abean1.get("itemdesc");
				String value1=(String)abean1.get("itemid");
				CommonData data1=new CommonData(value1,desc1);
				list.add(data1);
			}
		}
		return list;
	}
	
	
	/**
	 * 
	 * @param dbname
	 * @param fielditemList
	 * @param resumeFieldMap
	 * @param personType // 0:应聘库 1：人才库   4:我的收藏夹
	 * @return
	 */
	public String getSql_select(String dbname,ArrayList fielditemList,HashMap resumeFieldMap,String personType,String resumeState) throws GeneralException
	{
		StringBuffer select=new StringBuffer("");

		String hireMajor="";
		HashMap map = null;
		try {
			ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.conn,"1");
			map = parameterXMLBo.getAttributeValues();
		} catch (GeneralException e2) {
			e2.printStackTrace();
		}
		if(map.get("hireMajor")!=null)
			hireMajor=(String)map.get("hireMajor");  //招聘专业指标
		if("".equals(hireMajor)||hireMajor.equals(null)){
			throw GeneralExceptionHandler.Handle(new Exception("没有选择招聘专业指标"));
		}
		boolean hireMajorIsCode=false;
		FieldItem hireMajoritem=null;
		if(hireMajor.length()>0)
		{
			hireMajoritem=DataDictionary.getFieldItem(hireMajor.toLowerCase());
			if(hireMajoritem.getCodesetid().length()>0&&!"0".equals(hireMajoritem.getCodesetid()))
				hireMajorIsCode=true;
		}
		if("0".equals(personType)&&!"-1".equals(resumeState))
		{
			if("-3".equals(resumeState))
			{
				select.append("select zpt.resume_flag,"+Sql_switcher.isnull("zpt.resumeState", "''")+" resumeState,zpt.thenumber,");
				select.append(dbname+"A01.a0101,"+dbname+"A01.a0100,zpt.zp_pos_id ,zpt.zp_name");
			}
			else
			{
		 		select.append("select zpt.resume_flag,code0.codeitemdesc resumeState,zpt.thenumber,");
				select.append(dbname+"A01.a0101,"+dbname+"A01.a0100,zpt.zp_pos_id,"); //org0.codeitemdesc zp_name");
				if(hireMajor.length()==0)
					select.append("org0.codeitemdesc zp_name");
				else
				{
					if(hireMajorIsCode)
					{
						select.append("case when z03.Z0336='01'  then  code10.codeitemdesc else org0.codeitemdesc end as zp_name");
					}
					else
					{
						select.append("case when z03.Z0336='01'  then  z03."+hireMajor+" else org0.codeitemdesc end as zp_name");
					}
				}
			}
		}
		else if("4".equals(personType))
			select.append("select "+dbname+"A01.a0101,"+dbname+"A01.a0100");	//select.append("select "+dbname+"A01.a0101,"+dbname+"A01.a0100,zpt.thenumber");
		else if("1".equals(personType)) { //等于1为人才库页面使用此值，加入岗位名称
            select.append("select "+dbname+"A01.a0101,"+dbname+"A01.a0100,");   //select.append("select "+dbname+"A01.a0101,"+dbname+"A01.a0100,zpt.thenumber");    
            if(hireMajorIsCode) {
                select.append("case when ot.Z0336='01'  then  ot.codeitemname else ot.codeitemdesc end as zp_name");
            } else {
                select.append("case when ot.Z0336='01'  then  ot."+hireMajor+" else ot.codeitemdesc end as zp_name");
            }
        } else if("-1".equals(resumeState))
			select.append("select "+dbname+"A01.a0101,"+dbname+"A01.a0100");
		try
		{
			for(int i=0;i<fielditemList.size();i++)
			{
				String fielditem1=(String)fielditemList.get(i);
				
				LazyDynaBean abean1=(LazyDynaBean)resumeFieldMap.get(fielditem1.toLowerCase());
				String aset=((String)abean1.get("fieldsetid")).toLowerCase();
				String itemtype=(String)abean1.get("itemtype");
				String codesetid=(String)abean1.get("codesetid");
				String newTable=dbname+aset;
				if("z03".equals(aset))
					newTable="Z03";		
				if(("-1".equals(resumeState)|| "-3".equals(resumeState))&& "z03".equalsIgnoreCase(newTable)){
					newTable="zpt";
				}
				if(("1".equals(personType)|| "4".equals(personType))&& "z03".equalsIgnoreCase(newTable))
					newTable="zpt";
				if("A".equals(itemtype))
				{
					if("0".equals(codesetid))
					{
						if(!"Othaz7".equals(newTable)){
						    select.append(","+newTable+"."+fielditem1+" "+fielditem1+(i+1));
						}
					}
					else
					{   
						
						select.append(",org"+(i+1)+".codeitemdesc ");
						select.append(fielditem1+(i+1));
						select.append(","+newTable+"."+fielditem1+" fielditem"+(i+1));
						
					}
				}
				else if("D".equals(itemtype))
				{
					if("apply_date".equals(fielditem1))
							newTable="zpt";
					StringBuffer dd=new StringBuffer("");
					dd.append("case when "+newTable+"."+fielditem1+" is null then '' else ");
					dd.append(Sql_switcher.numberToChar(Sql_switcher.year(newTable+"."+fielditem1)));
					dd.append(Sql_switcher.concat()+"'-'"+Sql_switcher.concat());
					dd.append(Sql_switcher.numberToChar(Sql_switcher.month(newTable+"."+fielditem1)));
					dd.append(Sql_switcher.concat()+"'-'"+Sql_switcher.concat());
					dd.append(Sql_switcher.numberToChar(Sql_switcher.day(newTable+"."+fielditem1)));
					
					select.append(",");
					select.append(dd.toString()+" end as "+fielditem1+(i+1));
					select.append(","+newTable+"."+fielditem1+" fielditem"+(i+1));
					
				}
				else if("N".equals(itemtype)|| "M".equals(itemtype))
				{
				    select.append(",");
				    select.append(newTable+"."+fielditem1+" "+fielditem1+(i+1));
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return select.toString();
	}
	
	
	
	/**
	 * 
	 * @param dbname
	 * @param fielditemList
	 * @param resumeFieldMap
	 * @param resumeState		简历状态
	 * @param posConditionList  查询条件
	 * @param upValue 子集代码型 以上
	 * @param vague  是否按模糊查询
	 *  @param hireObjectId  招聘对象 -1 全部  x校园
	 *  @param personType    0:应聘库 1：人才库  4:我的收藏夹
	 * @return
	 */
	public String getSql_from(String dbname,ArrayList fielditemList,HashMap resumeFieldMap,String resumeState,ArrayList posConditionList,String personType,String z0301,String codeid,String upValue,String vague,UserView userView,String queryType,String hireObjectId,String applyStartDate,String applyEndDate)
	{
		HashMap setMap=new HashMap();
		setMap.put("a01","1");
		StringBuffer from=new StringBuffer("");
		try
		{
			// author:dengcan
			ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.conn,"1");
			HashMap map=parameterXMLBo.getAttributeValues();
			String active_field="";
			if(map!=null&&map.get("active_field")!=null&&!"".equals((String)map.get("active_field")))
			{
				active_field=(String)map.get("active_field");
			}
			String hireMajor="";
			if(map.get("hireMajor")!=null)
				hireMajor=(String)map.get("hireMajor");  //招聘专业指标
			boolean hireMajorIsCode=false;
			FieldItem hireMajoritem=null;
			if(hireMajor.length()>0)
			{
				hireMajoritem=DataDictionary.getFieldItem(hireMajor.toLowerCase());
				if(hireMajoritem.getCodesetid().length()>0&&!"0".equals(hireMajoritem.getCodesetid()))
					hireMajorIsCode=true;
			}
	 		
			if("0".equals(personType))//来自应聘库的查询
			{
				if(!"-1".equals(resumeState))//-1代表着未选岗位
				{
					if("-3".equals(resumeState))//-3代表着全部
					{
						setMap.put("zp_pos_tache","1");
						from.append(" from ");
						String sql_str = "";
						//2014.11.28 xxd 应聘简历选择全部时加入时间点查询
						if(!("".equals(applyStartDate.trim())&&"".equals(applyEndDate.trim()))){
								if(Sql_switcher.searchDbServer() == Constant.MSSQL){	
									sql_str = "(select * from Otha01 where A0100 in (select  A0100 from zp_pos_tache where  apply_date between '"+applyStartDate+" 00:00:00' and '"+applyEndDate+" 23:59:59'  ) )   ";	
							}else{
								sql_str = "(select * from Otha01 where A0100 in (select  A0100 from zp_pos_tache where  apply_date between to_date('"+applyStartDate+" 00:00:00','yyyy-mm-dd hh24:mi:ss') and to_date('"+applyEndDate+" 23:59:59','yyyy-mm-dd hh24:mi:ss') ) )   ";
							}
							from.append(sql_str+" ");
						}
						from.append(" "+dbname+"a01 left join (select zp_pos_tache.*,code0.codeitemdesc resumeState,z03.*,");
						if(hireMajor.length()<=0)
							from.append("org0.codeitemdesc zp_name ");
						else
						{ 
							if(hireMajorIsCode)
			    			{
								from.append("case when z03.Z0336='01'  then  code10.codeitemdesc else org0.codeitemdesc end as zp_name");
			    			}
			    			else
			    			{
			    				from.append("case when z03.Z0336='01'  then  z03."+hireMajor+" else org0.codeitemdesc end as zp_name");
			    			}
						}
						from.append(" from zp_pos_tache left join (select * from codeitem where codesetid='36') code0 on zp_pos_tache.resume_flag=code0.codeitemid");
						from.append(" left join ");
						if("-1".equals(hireObjectId)){
							from.append("z03 on zp_pos_tache.zp_pos_id=z03.z0301 left join (select * from organization where codesetid='@K') org0 on z03.z0311=org0.codeitemid  ");
						}else{
							from.append("(select * from z03 where z0336='"+hireObjectId+"')z03 on zp_pos_tache.zp_pos_id=z03.z0301 left join (select * from organization where codesetid='@K') org0 on z03.z0311=org0.codeitemid  ");
						}
						
						if(hireMajor.length()>0&&hireMajorIsCode)
							from.append(" left join (select * from codeitem where codesetid='"+hireMajoritem.getCodesetid()+"') code10 on z03."+hireMajor+"=code10.codeitemid  ");
						
						if("-1".equals(hireObjectId)){
							from.append(" ) zpt on" +" zpt.a0100="+dbname+"a01.a0100 ");	
						}else{
							from.append("  where z0336='"+hireObjectId+"') zpt on" +" zpt.a0100="+dbname+"a01.a0100 ");	
						}
						
					}
					else
					{
			    		setMap.put("zp_pos_tache","1");
						if(!("".equals(applyStartDate.trim())&& "".equals(applyEndDate.trim()))){
							String candidatesTimeFind0 = "";
							if(Sql_switcher.searchDbServer()==1){
							candidatesTimeFind0= "from (select  * from zp_pos_tache where  apply_date between '"+applyStartDate+" 00:00:00' and '"+applyEndDate+" 23:59:59'  ) zpt   ";	
							}else{
								candidatesTimeFind0= "from (select  * from zp_pos_tache where  apply_date between to_date('"+applyStartDate+" 00:00:00','yyyy-mm-dd hh24:mi:ss') and to_date('"+applyEndDate+" 23:59:59','yyyy-mm-dd hh24:mi:ss') ) zpt   ";
							}
							from.append(candidatesTimeFind0+" ");
						}
						else{
							from.append(" from zp_pos_tache zpt ");
						}
				    	from.append(" left join  (select * from codeitem where codesetid='36') code0 on zpt.resume_flag=code0.codeitemid");
				    	from.append(" left join "+dbname+"a01 on zpt.a0100="+dbname+"a01.a0100 ");
				    	from.append(" left join z03 on zpt.zp_pos_id=z03.z0301 ");
				    	from.append(" left join (select * from organization where codesetid='@K') org0 on z03.z0311=org0.codeitemid ");
				    	if(hireMajorIsCode)
				    		from.append(" left join  (select * from codeitem where codesetid='"+hireMajoritem.getCodesetid()+"') code10 on z03."+hireMajor+"=code10.codeitemid ");
					}
				}
				else{
					from.append(" from "+dbname+"a01 left join (select Z03.*,zppos.* from Z03 left join zp_pos_tache zppos on Z03.Z0301=zppos.ZP_POS_ID ) zpt on "+dbname+"a01.a0100=zpt.a0100");	
				}
			}
			else if("4".equals(personType))//1代表着来自人才库  4：我的收藏夹
			{		
			    from.append(" from "+dbname+"a01");	
			}
			/**
			 *创建人：臧雪健
			 *创建日期：20140514
			 *创建原因：人才库加入招聘岗位列
			 *方法作用：如果连接中personType的值为1，则查询第一意向岗位名称
			 */
			else if("1".equals(personType)){
                String codesetid = hireMajoritem.getCodesetid();
              
                from.append(" from "+dbname+"a01");
                from.append(" left join (select zp.a0100,org.codeitemdesc,z03.Z0336, z03."+hireMajor);
                if(hireMajorIsCode && "1".equals(personType) && !"0".equalsIgnoreCase(codesetid)){
                    from.append(",code10.codeitemname");
                }
                from.append(" from (select * from zp_pos_tache a where a.THENUMBER=(select MIN(b.thenumber) from zp_pos_tache b where a.A0100=b.A0100 )) zp, organization org ,z03");
                if(hireMajorIsCode && "1".equals(personType) && !"0".equalsIgnoreCase(codesetid)){
                    from.append(" ,(select codeitemid,codeitemdesc codeitemname from codeitem where codesetid='" + codesetid + "') code10");
                }
                
                from.append(" where z03.Z0301=zp.ZP_POS_ID and  z03.Z0311=org.codeitemid");
                if(hireMajorIsCode && "1".equals(personType) && !"0".equalsIgnoreCase(codesetid)){
                    from.append(" and z03."+hireMajor+"=code10.codeitemid");
                }
                
                from.append(") ot on "+dbname+"A01.A0100=ot.A0100");

			}
				 
			for(int i=0;i<fielditemList.size();i++)
			{
				String fielditem1=(String)fielditemList.get(i);
				LazyDynaBean abean1=(LazyDynaBean)resumeFieldMap.get(fielditem1.toLowerCase());
				String aset=((String)abean1.get("fieldsetid")).toLowerCase();
				String newTable=dbname+aset;
				if("z03".equals(aset))
					newTable = "z03";
				if("zp_pos_tache".equalsIgnoreCase(aset))
					newTable = "zp_pos_tache";
				if(setMap.get(aset)==null)
				{
					if(!"z03".equals(newTable)&&!"zp_pos_tache".equals(newTable)){
					from.append(" left join (select * from "+newTable+" a where  a.i9999=(select max(i9999) from "+newTable+" b where a.a0100=b.a0100)) "+newTable+" on ");
					if("0".equals(personType)&&!"-1".equals(resumeState)&&!"-3".equals(resumeState))
						from.append(" zpt.a0100="+newTable+".a0100 ");		
					else if("1".equals(personType)|| "4".equals(personType)|| "-1".equals(resumeState)|| "-3".equals(resumeState))
						from.append(" "+dbname+"a01.a0100="+newTable+".a0100 ");	
					setMap.put(aset,"1");
					}
				}	
				if("A".equals((String)abean1.get("itemtype"))&&!"0".equals((String)abean1.get("codesetid")))
				{
						String setid=(String)abean1.get("codesetid");
						if("UN".equals(setid)|| "UM".equals(setid)|| "@K".equals(setid)){
							if("z03".equals(newTable)&&("-1".equals(resumeState)|| "-3".equals(resumeState)|| "1".equals(personType)|| "4".equals(personType))){
								newTable="zpt";
							}
							from.append(" left join (select * from organization where codesetid='"+setid+"') org"+(i+1)+" on "+newTable+"."+fielditem1+"=org"+(i+1)+".codeitemid ");
						}else{
							if("z03".equals(newTable)&&("-1".equals(resumeState)|| "-3".equals(resumeState)|| "1".equals(personType)|| "4".equals(personType))){
								newTable="zpt";
							}
							from.append(" left join (select * from codeitem where codesetid='"+setid+"') org"+(i+1)+" on "+newTable+"."+fielditem1+"=org"+(i+1)+".codeitemid ");
						}
				}			
					
				
			}
			
			String personTypeField="";		
			if(map!=null&&map.get("person_type")!=null)
				personTypeField=(String)map.get("person_type");//person_type :人才库标识指标
			String candidatesTimeFind = "";
			String apck = "";
			if("0".equals(personType))
			{

				from.append(" where 1=1 ");
				if("12".equals(resumeState))//12:已选状态的简历
				{
					if("0".equals(queryType))
						from.append(" and (UPPER(zpt.username)='"+userView.getUserName().toUpperCase()+"' or zpt.username is null)");
				}
			}
			else if("1".equals(personType)){
					if(!("".equals(applyStartDate.trim())&& "".equals(applyEndDate.trim()))){
						if(Sql_switcher.searchDbServer()==1){
							candidatesTimeFind= " inner join  (select a0100 from zp_pos_tache where  apply_date between '"+applyStartDate+"' and '"+applyEndDate+"'  group by a0100  ) zpt on "+dbname+"a01.A0100=zpt.a0100 ";
						}else{
							candidatesTimeFind= " inner join  (select a0100 from zp_pos_tache where  apply_date between to_date('"+applyStartDate+" 00:00:00','yyyy-mm-dd hh24:mi:ss') and to_date('"+applyEndDate+" 23:59:59','yyyy-mm-dd hh24:mi:ss')  group by a0100  ) zpt on "+dbname+"a01.A0100=zpt.a0100 ";
						}
						
						from.append(candidatesTimeFind+" ");
					}
				from.append(" where  "+personTypeField+"='"+personType+"'");
			}
			else if("4".equals(personType)){
				if(!("".equals(applyStartDate.trim())&& "".equals(applyEndDate.trim()))){
					if(Sql_switcher.searchDbServer()==1){
						candidatesTimeFind= " inner join  (select a0100 from zp_pos_tache where  apply_date between '"+applyStartDate+"' and '"+applyEndDate+"'  group by a0100  ) zpt on zpp.a0100=zpt.a0100 ";
					}else{
						candidatesTimeFind= " inner join  (select a0100 from zp_pos_tache where  apply_date between to_date('"+applyStartDate+"  00:00:00','yyyy-mm-dd hh24:mi:ss') and  to_date('"+applyEndDate+" 23:59:59','yyyy-mm-dd hh24:mi:ss')  group by a0100  ) zpt on zpp.a0100=zpt.a0100 ";
					}
					
					apck= "left join zp_resume_pack zpp on "+dbname+"a01.A0100=zpp.a0100 ";
					from.append(" "+apck+" "+candidatesTimeFind+" ");
				}
				from.append(" where "+dbname+"a01.a0100 in (select a0100 from zp_resume_pack  where  logonname='"+userView.getUserId()+"' and nbase='"+dbname+"')");
			}
			
			
			PositionDemand bo=new PositionDemand(this.conn);
			String sql=bo.getSqlByCondition(posConditionList,dbname,upValue,vague);
			
			if(getWhlConditionCount(sql)>0) //如果有条件
			{
				if("0".equals(personType)&&!"-1".equals(resumeState)&&!"-3".equals(resumeState))
					from.append(" and zpt.a0100  in ("+sql+")");
				else if("1".equals(personType)|| "4".equals(personType)|| "-1".equals(resumeState)|| "-2".equals(resumeState)|| "-3".equals(resumeState))
					from.append(" and "+dbname+"a01.a0100  in ("+sql+")");
			}
			
			if(!"-1".equals(z0301)&& "0".equals(personType)&&!"-1".equals(resumeState))
				from.append(" and zpt.zp_pos_id='"+z0301+"'");
			
			if("0".equals(personType))
			{
				if(!"-1".equals(resumeState)&&!"-2".equals(resumeState)&&!"-3".equals(resumeState))
				{
		    		if(userView.isSuper_admin()|| "1".equals(userView.getGroupId()))
		    		{
		    		}
			    	else{
	    	    		if(codeid.trim().length()>=0)
	    	    		{
					
	    		    		if(codeid.indexOf("`")==-1)
	    		    		{
	    		    		}
	    		    		else
	    		    		{
	    		    			String _str=Sql_switcher.isnull("z03.z0336","''");
		    		    		StringBuffer tempSql=new StringBuffer("");
		    		    		StringBuffer tempSql2=new StringBuffer("");
								StringBuffer tempSql3=new StringBuffer("");
								
		    		    		String[] temp=codeid.split("`");
			    		    	for(int i=0;i<temp.length;i++)
			    	     		{
			    	    			if(temp[i]==null|| "".equals(temp[i]))
			    		    			continue;
		    			    		tempSql.append(" or  z03.z0311 like '"+temp[i].substring(2)+"%'");
		    			    		tempSql2.append(" or z03.z0321 like '"+temp[i].substring(2)+"%'");
				    				tempSql3.append(" or z03.z0325 like '"+temp[i].substring(2)+"%'");
		    		    		}
			    		    	from.append(" and ( ( ( "+tempSql.substring(3)+" ) and  "+_str+"<>'01' ) or ( ( "+tempSql2.substring(3)+" ) and  "+_str+"='01' ) or ( ( "+tempSql3.substring(3)+" ) and  "+_str+"='01' ) ) ");
		    	    		}
	    				/**天辰，已选人数和面试安排中的人数不同，因为面试安排中为发布状态的招聘需求，已选中美有限制*/
	    		    	}
			    	}
			    	from.append(" and zpt.resume_flag='"+resumeState+"'");
				}else
				{
					if("-2".equals(resumeState))//已选职位，（没选我管理的职位的简历）
					{
						if(userView.isSuper_admin()|| "1".equals(userView.getGroupId()))
			    		{
			    		}
				    	else{
		    	    		if(codeid.trim().length()>=0)
		    	    		{
						        /**用需求单位控制*/
		    		    		if(codeid.indexOf("`")==-1)
		    		    		{
		    		    			String _str=Sql_switcher.isnull("z03.z0336","''");
		    		    			from.append(" and ( ( z03.z0311 not like '"+codeid.substring(2)+"%' and  "+_str+"<>'01' ) or ( z03.z0321 not like '"+codeid.substring(2)+"%' and  "+_str+"='01' ) or ( z03.z0325 not like '"+codeid.substring(2)+"%' and  "+_str+"='01' ) ) ");
		    		    		}
		    		    		else
		    		    		{
		    		    			String _str=Sql_switcher.isnull("z03.z0336","''");
			    		    		StringBuffer tempSql=new StringBuffer("");
			    		    		StringBuffer tempSql2=new StringBuffer("");
									StringBuffer tempSql3=new StringBuffer("");
			    		    		String[] temp=codeid.split("`");
				    		    	for(int i=0;i<temp.length;i++)
				    	     		{
				    	    			if(temp[i]==null|| "".equals(temp[i]))
				    		    			continue;
			    			    		tempSql.append(" and z03.z0311 not like '"+temp[i].substring(2)+"%'");
			    			    		tempSql2.append(" and z03.z0321 not  like '"+temp[i].substring(2)+"%'");
					    				tempSql3.append(" and z03.z0325 not  like '"+temp[i].substring(2)+"%'");
			    		    		}
				    		    	from.append(" and ( ( ( "+tempSql.substring(4)+" ) and  "+_str+"<>'01' ) or ( ( "+tempSql2.substring(4)+" ) and  "+_str+"='01' ) or ( ( "+tempSql3.substring(4)+" ) and  "+_str+"='01' ) ) ");
			    	    		}
		    		    	}
				    	}
					}
					else
					{
						if("".equals(active_field)&& "-3".equals(resumeState))//这个应该是全部(raido那个按钮个人管理范围之内的)
						{
							from.append(" and (");
							from.append("("+dbname+"a01.a0100 not in (select a0100 from zp_pos_tache)) or "+dbname+"a01.a0100 in (");
							from.append("select a0100 from zp_pos_tache left join Z03 on zp_pos_tache.zp_pos_id=Z03.z0301 where  ");
							if(userView.isSuper_admin()|| "1".equals(userView.getGroupId())|| "".equals(codeid))
				    		{
								from.append(" 1=1 ");
				    		}
					    	else{
			    	    		if(codeid.trim().length()>=0)
			    	    		{
			    	    		    String _str=Sql_switcher.isnull("z03.z0336","''");
			    	    		    StringBuffer tempSql=new StringBuffer("");
			    	    		    StringBuffer tempSql2=new StringBuffer("");
			    	    		    StringBuffer tempSql3=new StringBuffer("");
			    	    		    
			    	    		    String[] temp=codeid.split("`");
			    	    		    for(int i=0;i<temp.length;i++)
			    	    		    {
			    	    		        if(temp[i]==null|| "".equals(temp[i]))
			    	    		            continue;
			    	    		        tempSql.append(" or  z03.z0311 like '"+temp[i].substring(2)+"%'");
			    	    		        tempSql2.append(" or z03.z0321 like '"+temp[i].substring(2)+"%'");
			    	    		        tempSql3.append(" or z03.z0325 like '"+temp[i].substring(2)+"%'");
			    	    		    }
			    	    		    from.append("  ( ( ( "+tempSql.substring(3)+" ) and  "+_str+"<>'01' ) or ( ( "+tempSql2.substring(3)+" ) and  "+_str+"='01' ) or ( ( "+tempSql3.substring(3)+" ) and  "+_str+"='01' ) ) ");
			    		    	}
					    	}
							from.append("))");
						}
					}
				}
			}
			if("-1".equals(resumeState))
			{
				from.append(" and "+dbname+"a01.a0100 not in (select a0100 from zp_pos_tache)");
				
			}
				
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return from.toString();
	}
	
	
	public int getWhlConditionCount(String sql)
	{
		String dd=sql.substring(sql.indexOf("where")+5);
		dd=dd.replaceAll("\\(","");
		dd=dd.replaceAll("\\)","");
		dd=dd.replaceAll("1","");
		dd=dd.replaceAll("=","");
		dd=dd.replaceAll("and","");
		return dd.trim().length();
	}
	
	public static void main(String[] arg)
	{
		
		String sql="select distinct WPRA01.a0100  from WPRa01 where  (  1=1  ) and (  1=1  )";
		sql="select distinct WPRA01.a0100  from WPRa01,WPRa04 where  WPRa01.a0100=WPRa04.a0100 and (  (  A0435='bbb' )  ) and (  1=1  )";
		String dd=sql.substring(sql.indexOf("where")+5);
		dd=dd.replaceAll("\\(","");
		dd=dd.replaceAll("\\)","");
		dd=dd.replaceAll("1","");
		dd=dd.replaceAll("=","");
		dd=dd.replaceAll("and","");
		
	}
	
	/**
	 * 取得符合条件 简历数量
	 * @return
	 */
	public String getResumeCount(String select,String from)
	{
		String count="0";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowset=null;
		try
		{
			rowset=dao.search("select count(*) "+from);
			while(rowset.next())
				count=rowset.getString(1);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return count;
	}
	
	
	public String getSql_order(HashMap resumeFieldMap,String order_item,String order_desc,String dbname,String personType,String resumeState)
	{
		StringBuffer order=new StringBuffer(" order by ");
		try
		{
		ParameterXMLBo xmlBo=new ParameterXMLBo(this.conn);
		HashMap map=xmlBo.getAttributeValues();
		String hireMajor="";
		if(map.get("hireMajor")!=null)
			hireMajor=(String)map.get("hireMajor");
		if("a0101".equalsIgnoreCase(order_item))
		{
			if("0".equals(personType)&&!"-1".equals(resumeState))
				order.append(" "+dbname+"A01.a0101 "+order_desc+",zpt.thenumber "+order_desc+","+dbname+"A01.a0100");
			else if("1".equals(personType)|| "4".equals(personType)|| "-1".equals(resumeState))
				order.append(" "+dbname+"A01.a0101 "+order_desc+","+dbname+"A01.a0100");
		}else if("zp_pos_id".equalsIgnoreCase(order_item)||order_item.equalsIgnoreCase(hireMajor))
		{
			if("zp_pos_id".equalsIgnoreCase(order_item))
		    	order.append(" zpt."+order_item+" "+order_desc);
			else
		    	order.append(" "+order_item+" "+order_desc);
		}
		else if("username".equalsIgnoreCase(order_item))
		{
			order.append(" zpt."+order_item+" "+order_desc);
		}
		else
		{
			LazyDynaBean abean1=(LazyDynaBean)resumeFieldMap.get(order_item.toLowerCase());			
			String aset=((String)abean1.get("fieldsetid")).toLowerCase();
			String newTable=dbname+aset;
			if("apply_date".equals(order_item))
				newTable="zpt";
			if(("-1".equals(resumeState)|| "-3".equals(resumeState)|| "1".equals(personType)|| "4".equals(personType))&& "z03".equalsIgnoreCase(aset))
				order.append(" zpt."+order_item+" "+order_desc+","+dbname+"A01.a0100");
			else if("z03".equalsIgnoreCase(aset))
			{
				order.append(" "+aset+"."+order_item+" "+order_desc+","+dbname+"A01.a0100");
			}else
		    	order.append(" "+newTable+"."+order_item+" "+order_desc+","+dbname+"A01.a0100");
			
		}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return order.toString();
		
	}
	public String getSelect(HashMap resumeFieldMap,String order_item,String order_desc,String dbname,String personType,String resumeState)
	{
		StringBuffer order=new StringBuffer("");
		try
		{
		ParameterXMLBo xmlBo=new ParameterXMLBo(this.conn);
		HashMap map=xmlBo.getAttributeValues();
		String hireMajor="";
		if(map.get("hireMajor")!=null)
			hireMajor=(String)map.get("hireMajor");
		if("a0101".equalsIgnoreCase(order_item))
		{
			if("0".equals(personType)&&!"-1".equals(resumeState))
				order.append(" "+dbname+"A01.a0101 "+",thenumber ");
			else if("1".equals(personType)|| "4".equals(personType)|| "-1".equals(resumeState))
				order.append(" "+dbname+"A01.a0101 ");
		}
		else if("username".equalsIgnoreCase(order_item))
		{
			order.append(" zpt."+order_item+" ");
		}
		else if("zp_pos_id".equalsIgnoreCase(order_item)||order_item.equalsIgnoreCase(hireMajor))
		{
			order.append(" "+order_item+" ");
		}
		else
		{
			LazyDynaBean abean1=(LazyDynaBean)resumeFieldMap.get(order_item.toLowerCase());			
			String aset=((String)abean1.get("fieldsetid")).toLowerCase();
			String newTable=dbname+aset;
			if("apply_date".equals(order_item))
				newTable="zpt";
			order.append(" "+newTable+"."+order_item+" ");
			
		}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return order.toString();
		
	}
	
	
	
	
	/////////////////////////////    批量发送邮件     ///////////////////////////////////
	
	/**
	 * 取得指标列表
	 */
	public ArrayList getZbList(String setid)
	{
		ArrayList zb_list=new ArrayList();
		try
		{
			zb_list.add(new CommonData(""," "));
			zb_list.add(new CommonData("人员姓名(A0100)","人员姓名(A0100)"));
			zb_list.add(new CommonData("应聘职位(z0311)","应聘职位(z0311)"));
			zb_list.add(new CommonData("(~系统时间~)","(~系统时间~)"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return zb_list;
	}
	
	/**
	 * 取得指标集列表
	 * @return
	 */
	public ArrayList getZbjList()
	{
		ArrayList list=new ArrayList();
		try
		{
			list.add(new CommonData("#","初次筛选信息表"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 根据权限取得邮件模版列表
	 * @param userView
	 * @return
	 */
	public ArrayList getMailTempList(UserView userView) 
	{
		ArrayList list=new ArrayList();
		CommonData data=new CommonData("","");
		list.add(data);
		
		try
		{
			 HireTemplateBo bo = new HireTemplateBo(this.conn);
			 String b0110=bo.getB0110(userView);
		     boolean flag=true;
		     String[] b_arr=null;
		     if("HJSJ".equalsIgnoreCase(b0110))
		     {
		        flag=false;
		      }
		     else
		     {
		        b_arr=b0110.split("`");
		     }
			RowSet frowset=null;
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sql=new StringBuffer("select * from t_sys_msgtemplate where zploop='1' and (template_type=0 or template_type=1)");
			frowset=dao.search(sql.toString());
			while(frowset.next())
			{
				if(!flag)
		    	{
	     			CommonData data1=new CommonData(frowset.getString("template_id"),frowset.getString("name"));
				    list.add(data1);
		    	}
				else
				{
					String bb=frowset.getString("b0110");
		    		boolean bool=false;
		    		if(bb==null|| "".equals(bb)|| "HJSJ".equalsIgnoreCase(bb))
		    		{
		    			continue;
		    		}
		    		else
		    		{
		    			String[] bb_arr=bb.split("`");
		    			for(int i=0;i<bb_arr.length;i++)
		    			{
		    				String temp_bb=bb_arr[i];
		    				for(int j=0;j<b_arr.length;j++)
		    				{
		    					if(temp_bb.indexOf(b_arr[j])!=-1)
		    					{
		    						bool=true;
		    						break;
		    					}
		    				}
		    				if(bool)
		    				{
		    					break;
		    				}
		    			}
		    			if(bool)
		    			{
		    				CommonData data1=new CommonData(frowset.getString("template_id"),frowset.getString("name"));
						    list.add(data1);
		    			}
		    		}
				}
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return list;
	}
	public HashMap getSubSetMap(String sql,ArrayList fieldList,String nbase)
	{
		HashMap  map = new HashMap();
		HashMap  mymap = new HashMap();
		HashMap  zjmap = new HashMap();
		
		RowSet rs = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			HashMap subMap = new HashMap(); 
			HashMap select = new HashMap();
			for(int i=0;i<fieldList.size();i++)
			{
				FieldItem item=(FieldItem)fieldList.get(i);
				if((item.getFieldsetid().startsWith("A")||item.getFieldsetid().startsWith("a"))&&!"A01".equalsIgnoreCase(item.getFieldsetid()))
				{
					if(subMap.get(item.getFieldsetid().toUpperCase())!=null)
					{
						ArrayList alist = (ArrayList)subMap.get(item.getFieldsetid().toUpperCase());
						alist.add(item);
						subMap.put(item.getFieldsetid().toUpperCase(), alist);
					}
					else{
						ArrayList alist = new ArrayList();
						alist.add(item);
						subMap.put(item.getFieldsetid().toUpperCase(), alist);
					}
					if(select.get(item.getFieldsetid().toUpperCase())!=null)
					{
						String sel_str= (String)select.get(item.getFieldsetid().toUpperCase());
						sel_str+=","+item.getItemid();
						select.put(item.getFieldsetid().toUpperCase(), sel_str);
					}
					else{
						String sel_str="A0100";
						sel_str+=","+item.getItemid();
						select.put(item.getFieldsetid().toUpperCase(), sel_str);
					}
				}
			}
			Set keySet = subMap.keySet();
			for(Iterator it=keySet.iterator();it.hasNext();)
			{
				String key=(String)it.next();
				StringBuffer buf = new StringBuffer();
				String select_str=(String)select.get(key);
				ArrayList alist = (ArrayList)subMap.get(key);
				buf.append(" select "+select_str);
				buf.append(" from "+nbase+key);
				buf.append(" where a0100 in (");
				buf.append(sql);
				buf.append(") order by i9999");
				rs = dao.search(buf.toString());
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				int n=0;
				while(rs.next())
				{
					String a0100=rs.getString("a0100");
					for(int i=0;i<alist.size();i++)
					{ 
						FieldItem item=(FieldItem)alist.get(i);
						String value="";
						/*if((item.getFieldsetid().startsWith("A")||item.getFieldsetid().startsWith("a"))&&!item.getFieldsetid().equalsIgnoreCase("A01"))
						{*/
				    		if("A".equalsIgnoreCase(item.getItemtype()))
				    		{
				    			value=rs.getString(item.getItemid())==null?"":rs.getString(item.getItemid());
					    		if(item.isCode())
					    			value=AdminCode.getCodeName(item.getCodesetid(), value);
								
					    	}else if("D".equalsIgnoreCase(item.getItemtype()))
				    		{
				    			if(rs.getDate(item.getItemid())!=null)
					     			value=format.format(rs.getDate(item.getItemid()));
					    	}else if("N".equalsIgnoreCase(item.getItemtype()))
					    	{
					    		int scale = item.getDecimalwidth();
						    	if(rs.getString(item.getItemid())!=null)
							    	value=PubFunc.round(rs.getString(item.getItemid()), scale);
								
				    		}
				    		else{
					    		value=rs.getString(item.getItemid())==null?"":rs.getString(item.getItemid());
					    	}
				    		String akey = (a0100+item.getItemid()).toUpperCase();
					    	if(map.get(akey)!=null)
					    	{
				    			String v=(String)map.get(akey);
				    			v+="\r\n"+value;
					    		map.put(akey, v);
				    		}else{
				    			//map.put(akey+"_a",k);
					    		map.put(akey, value);
					    
				    		}	    	
					}

				}	
				
			}
		
			//map.put(a0100, k);
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try
			{
				if(rs!=null)
					rs.close();
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return map;
	}
	/**
	 * 获取指标
	 */
	public ArrayList getitemIDList(){
		String sql = "select * from fielditem where useflag='1' and fieldsetid='A01' and codesetid='0' and (itemtype='A' or itemtype='N') order by displayid";
		ArrayList list = new ArrayList();
		ArrayList datalist = null;
		CommonData data=null;
		HashMap<String, String>  map = null;
		try{
			ContentDAO dao = new ContentDAO(this.conn);
			datalist = dao.searchDynaList(sql);
			for(Iterator it=datalist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				String itemId = dynabean.get("itemid").toString();
				String itemdesc = dynabean.get("itemdesc").toString();
				if(sql.indexOf(","+itemId.toLowerCase()+",")==-1){
					map = new HashMap<String,String>();
					map.put("id", itemId);
					map.put("name", itemdesc);
				}
				list.add(map);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 获取人员库
	 */
	public ArrayList getuserList(){
		String sql = "select dbid,dbname,pre from dbname order by dbid";
		ArrayList userList=null;
		ArrayList list = new ArrayList();
		HashMap<String,String> map = null;
		try{
			ContentDAO dao = new ContentDAO(this.conn);
			userList =  dao.searchDynaList(sql);
			for(Iterator it=userList.iterator();it.hasNext();){
			    DynaBean dynabean=(DynaBean)it.next();
			    String pre = dynabean.get("pre").toString();
			    String dbname = dynabean.get("dbname").toString();
			    if(sql.indexOf(","+pre.toLowerCase()+",")==-1){
			        map = new HashMap<String,String>();
			        map.put("id", pre);
			        map.put("name", dbname);
			    }
			    list.add(map);
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	public ArrayList getModeList(){
		HashMap<String,String>  map = new HashMap<String, String>();
		ArrayList list = new ArrayList();
		map .put("1","不导入");
		map .put("2","替换");
		map .put("3","追加");
		list.add(map);
		return list;
		
	}
	
	
	public String getItemHtml(String resumefld,String state,ArrayList fieldItemList)
	{
		StringBuffer html=new StringBuffer("<select name='"+resumefld+"' onchange='setEhrfld(this)' > \n");
		html.append("<option value='' ");
		if("".equals(state)){
			html.append(" selected ");
		}
		html.append(">请选择</option>");
		if(fieldItemList!=null){
		for(int i=0;i<fieldItemList.size();i++)
		{
			FieldItem item = (FieldItem) fieldItemList.get(i);

			html.append("<option value='"+item.getItemid()+"' ");
			if(state.equalsIgnoreCase(item.getItemid())){
				html.append("selected ");
			}
			html.append(">"+item.getItemid().toUpperCase()+":"+item.getItemdesc()+"</option> \n");
		}
		}
		return html.toString();
	}
	
	public String getEditHtml(String resumeset,String state,int i)
	{

		StringBuffer html=new StringBuffer("<a href=\"javascript:goToPage('");
		html.append(resumeset+"',");
		html.append("'"+state+"',"+i+")");
		html.append("\"> ");
		html.append("<img src=\"/images/edit.gif\" border=0></a>");
		return html.toString();
	}
	
	public String getInputHtml(String id,String value)
	{

		StringBuffer html=new StringBuffer("<input name=\"resumeinfo\" style=\"border:0\" type=\"text\" id=\"");
		html.append(id+"\"");
		html.append(" value=\"");
		html.append(value+"\"");
		return html.toString();
	}
	
	public String getCheckboxHtml(String id,String valid){
		//valid为0,true时不勾选为1,false为勾选
		
		StringBuffer html = new StringBuffer("<input type=\"checkbox\" id=\""+id+"\" name=\"checkbox\"");
		
		if("false".equals(valid)){
			html.append(" checked ");
		}
		html.append(" /> ");
		
		return html.toString();
	}
	/**
     * 获取应聘人员的应聘职位
     * @param a0100
     * @param personType
     * @param resumeState
     * @param dbname
     * @return
     */
    public static String getPosition(String a0100, String personType, String resumeState, String dbname, String z0301) {
        String position = "";
        Connection con = null;
        RowSet rs = null;
        String posHtml = "";
        String posTile = "";
        StringBuffer sql = new StringBuffer();
        a0100 = PubFunc.decrypt(a0100);
        try {
            con = AdminDb.getConnection();
            String hireMajor = "";
            ParameterXMLBo parameterXMLBo = new ParameterXMLBo(con, "1");
            HashMap map = parameterXMLBo.getAttributeValues();

            if (map.get("hireMajor") != null)
                hireMajor = (String) map.get("hireMajor"); // 招聘专业指标

            if ("".equals(hireMajor) || hireMajor.equals(null)) {
                throw GeneralExceptionHandler.Handle(new Exception("没有选择招聘专业指标"));
            }

            boolean hireMajorIsCode = false;
            FieldItem hireMajoritem = null;
            if (hireMajor.length() > 0) {
                hireMajoritem = DataDictionary.getFieldItem(hireMajor.toLowerCase());
                if (hireMajoritem.getCodesetid().length() > 0 && !"0".equals(hireMajoritem.getCodesetid()))
                    hireMajorIsCode = true;
            }

            if (("0".equals(personType) || "4".equals(personType) || "1".equals(personType)) && !"-1".equals(resumeState)) {
                    sql.append("select zpt.resume_flag,zpt.zp_pos_id,code0.codeitemdesc resumeState,zpt.thenumber,");
                    sql.append(dbname + "A01.a0101," + dbname + "A01.a0100,zpt.zp_pos_id,"); // org0.codeitemdesc
                    // zp_name");
                    if (hireMajor.length() == 0)
                        sql.append("org0.codeitemdesc zp_name");
                    else {
                        if (hireMajorIsCode)
                            sql.append("case when z03.Z0336='01'  then  code10.codeitemdesc else org0.codeitemdesc end as zp_name");
                        else
                            sql.append("case when z03.Z0336='01'  then  z03." + hireMajor + " else org0.codeitemdesc end as zp_name");
                }
            } else if ("-1".equals(resumeState))
                sql.append("select " + dbname + "A01.a0101," + dbname + "A01.a0100");
            
            sql.append(" from zp_pos_tache zpt left join (select * from codeitem where codesetid = '36') code0");
            sql.append(" on zpt.resume_flag = code0.codeitemid");
            sql.append(" left join " + dbname + "a01 on zpt.a0100 = " + dbname + "a01.a0100");
            sql.append(" left join z03 on zpt.zp_pos_id = z03.z0301");
            
            FieldItem fi = DataDictionary.getFieldItem("z0311", "Z03");
            String codesetid = fi.getCodesetid();
            sql.append(" left join (select * from organization where codesetid = '" + codesetid + "') org0  on z03.z0311 = org0.codeitemid");
            if (hireMajor != null && hireMajor.length() > 0) {
                fi = DataDictionary.getFieldItem(hireMajor, "Z03");
                codesetid = fi.getCodesetid();
                sql.append(" left join (select * from codeitem where codesetid = '" + codesetid + "') code10 on z03."+hireMajor+" = code10.codeitemid");
            }
            
            sql.append(" where "+dbname+"A01.a0100='"+a0100+"'");
            
            if(!"-3".equalsIgnoreCase(resumeState))
                sql.append(" and zpt.resume_flag = '"+ resumeState +"'");
            
            if(z0301 != null && z0301.length() > 0 && !"-1".equalsIgnoreCase(z0301)){
                if(!Pattern.matches("\\d+", z0301))
                    z0301=PubFunc.decrypt(z0301);
                
                if(!"-1".equals(z0301)&& "0".equals(personType)&&!"-1".equals(resumeState))
                    sql.append(" and zpt.zp_pos_id='"+z0301+"'");
            }

            sql.append(" order by zpt.thenumber");
            
            ContentDAO dao = new ContentDAO(con);
            rs = dao.search(sql.toString());
            int i = 0;
            String posid = "";
            while (rs.next()) {
                i++;
                String codeitemdesc = rs.getString("zp_name");
                codeitemdesc = codeitemdesc==null ? "" : codeitemdesc;
                if(i == 1)
                    posid = rs.getString("zp_pos_id");
                
                if (i < 3) {
                    if (i == 2)
                        posHtml += "<br>";

                    posHtml += codeitemdesc;
                } else {
                    if (i == 3)
                        posTile += posHtml.replaceAll("<br>", "\n");
                     
                    posTile += "\n" + codeitemdesc;
                }
            }

            if (i > 2) 
                posHtml += "<br>...";
            
            position = posHtml + "`" + posTile + "`" + PubFunc.encrypt(posid);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	PubFunc.closeResource(rs);
            PubFunc.closeResource(con);
        }
        return position;
    }
    /**
     * 获取同步标识可用指标（关联45号代码类的指标）
     * @return
     */
    public ArrayList<HashMap<String, String>> getSynchronousList() {
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        ArrayList fieldList = DataDictionary.getFieldList("a01", Constant.USED_FIELD_SET);
        for(int i = 0; i <fieldList.size(); i++){
            FieldItem fi = (FieldItem) fieldList.get(i);
            if("45".equalsIgnoreCase(fi.getCodesetid())) {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("id", fi.getItemid());
                map.put("name", fi.getItemdesc());
                list.add(map);
            }
        }
        return list;
    }
}
