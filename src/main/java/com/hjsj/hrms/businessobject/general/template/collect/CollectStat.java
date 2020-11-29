package com.hjsj.hrms.businessobject.general.template.collect;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
/**
 * 汇总统计
 * <p>Title:CollectStat.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Nov 24, 2006 1:28:43 PM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class CollectStat {
    private Connection conn;
	public CollectStat()
	{
		
	}
	public CollectStat(Connection conn)
	{
		this.conn=conn;
	}
	/**
	 * 得到操作人员库
	 * @param dblist
	 * @return
	 */
	public ArrayList getBaseList(ArrayList dblist) {
		 StringBuffer cond=new StringBuffer();
	        cond.append("select pre,dbname from dbname where pre in (");
	       
	        for(int i=0,j=0;i<dblist.size();i++)
	        {
	           if(j!=0) {
                   cond.append(",");
               }
	            cond.append("'");
	            cond.append((String)dblist.get(i));
	            cond.append("'");
	            j++;
	       }	       
	       cond.append(")");
	       ArrayList list =new ArrayList();
           try
           {
        	   ContentDAO dao=new ContentDAO(this.conn);
        	   RowSet rs=null;
        	   rs=dao.search(cond.toString());
        	   CommonData vo=null;
        	   while(rs.next())
        	   {
                      vo=new  CommonData();
                      vo.setDataName(rs.getString("dbname"));
                      vo.setDataValue(rs.getString("pre"));
                      list.add(vo);
        	   }
           }catch(Exception e)
           {
        	   e.printStackTrace();
           }
           return list;
	}

	/**
	 * 得到子集的字段列表
	 * @param setname
	 * @return
	 */
	public String getFieldString(ArrayList fieldlist,String nbase)
	{
		StringBuffer fields=new StringBuffer();		
		try
		{
			for(int i=0;i<fieldlist.size();i++)
			{
				FieldItem fielditem=(FieldItem)fieldlist.get(i);
				if(i!=0) {
                    fields.append(",");
                }
				fields.append(fielditem.getItemid());
				
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return fields.toString();
	}
	public String getSQLFieldString(ArrayList fieldlist,String nbase)
	{
		StringBuffer fields=new StringBuffer();		
		try
		{
			for(int i=0;i<fieldlist.size();i++)
			{
				FieldItem fielditem=(FieldItem)fieldlist.get(i);
				if(!"a0100".equals(fielditem.getItemid()))
				{
					if(i!=0) {
                        fields.append(",");
                    }
					fields.append(fielditem.getItemid());
				}else
				{
					if(i!=0) {
                        fields.append(",");
                    }
					fields.append(nbase+"a01."+fielditem.getItemid()+" as a0100");
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return fields.toString();
	}
	/**
	 * 得到子集的字段列表List
	 * @param setname
	 * @return
	 */
	public ArrayList getFieldList(ArrayList fieldlist)
	{
		ArrayList fields=new ArrayList();		
		CommonData comm=null;
		comm=new CommonData();
		
		comm.setDataName("请选择..");
		comm.setDataValue("-1");
		fields.add(comm);
		try
		{
			for(int i=0;i<fieldlist.size();i++)
			{
				comm=new CommonData();
				FieldItem fielditem=(FieldItem)fieldlist.get(i);
				if(!"a0100".equals(fielditem.getItemid())&&!"i9999".equals(fielditem.getItemid()))
				{
					comm.setDataName(fielditem.getItemdesc());
					comm.setDataValue(fielditem.getItemid());
					fields.add(comm);
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return fields;
	}
	 public ArrayList getColumnlist(ArrayList columnlist)
	 {
		 ArrayList list=new ArrayList();
		 FieldItem fielditem=new FieldItem();
		 fielditem.setItemid("a0101");
		 fielditem.setItemtype("A");
		 fielditem.setItemdesc("姓名");
		 fielditem.setCodesetid("0");
		 fielditem.setVisible(true);
		 list.add(fielditem);
		 fielditem=new FieldItem();
		 fielditem.setItemid("b0110");
		 fielditem.setItemtype("A");
		 fielditem.setItemdesc("单位名称");
		 fielditem.setCodesetid("UN");
		 fielditem.setVisible(true);
		 list.add(fielditem);
		 fielditem=new FieldItem();
		 fielditem.setItemid("e0122");
		 fielditem.setItemtype("A");
		 fielditem.setItemdesc("部门");
		 fielditem.setCodesetid("UM");
		 fielditem.setVisible(true);
		 list.add(fielditem);
		 fielditem=new FieldItem();
		 fielditem.setItemid("a0100");
		 fielditem.setItemtype("A");
		 fielditem.setItemdesc("人员编号");
		 fielditem.setCodesetid("0");
		 fielditem.setVisible(false);
		 list.add(fielditem);
		 fielditem=new FieldItem();
		 fielditem.setItemid("i9999");
		 fielditem.setItemtype("A");
		 fielditem.setItemdesc("子集编号");
		 fielditem.setCodesetid("0");
		 fielditem.setVisible(false);
		 list.add(fielditem);
		 for(int i=0;i<columnlist.size();i++)
		 {
			 fielditem=(FieldItem)columnlist.get(i);
			 if(!"a0100".equals(fielditem.getItemid()))
			 {
				fielditem.setVisible(true);
			 }else
			 {
				 fielditem.setVisible(false);
			 }
			 list.add(fielditem);			 
		 }
		 return list;
	 } 
	 public  String getSelectHtml(ArrayList codelist,String code)
	    {
	    	StringBuffer selecthtml= new StringBuffer();
	    	selecthtml.append("<select name='childset' size='1'>");
	    	selecthtml.append("<option value=all>");    		
			selecthtml.append("全部");
			selecthtml.append("</option>"); 
	    	for(int i=0;i<codelist.size();i++)
	    	{
	    		CommonData vo=(CommonData)codelist.get(i);
	    		if(vo.getDataValue().trim().equalsIgnoreCase(code.trim()))
	    		{
	    			selecthtml.append("<option value="+vo.getDataValue()+" selected>");    		
		    		selecthtml.append(vo.getDataName());
		    		selecthtml.append("</option>"); 
	    		}else
	    		{
	    			selecthtml.append("<option value="+vo.getDataValue()+">");    		
		    		selecthtml.append(vo.getDataName());
		    		selecthtml.append("</option>"); 
	    		}
	    		   	
	    	}
	    	selecthtml.append("</select> ");
	    	return selecthtml.toString();
	    }
	 public ArrayList getList(String  filetype) throws GeneralException
		{
			  ArrayList list=new ArrayList();
		  		StringBuffer sql=new StringBuffer();
				ContentDAO dao=new ContentDAO(this.conn);
				CommonData datavo = null;			
				RowSet rs=null;
				try
				{
					sql.append("SELECT codeitemid, codeitemdesc,parentid  FROM codeitem  where codesetid ='"+filetype+"'");
					sql.append("union select codeitemid, codeitemdesc,parentid from organization where codesetid='");
					sql.append(filetype);
					sql.append("' order by codeitemdesc");
					rs=dao.search(sql.toString());
					while(rs.next())
					{  
					   
						 datavo=new CommonData(rs.getString("codeitemid"),rs.getString("codeitemdesc"));
		        	     list.add(datavo);				  
					}
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
		   	       throw GeneralExceptionHandler.Handle(ex);			
				}
				return list;
		}
	  public String  getWhere(String code,String kind,String nbase,UserView userView,String subset)
	    {
	    	if(kind==null||kind.length()<=0)
			{
				kind=RegisterInitInfoData.getKindValue(kind,userView);
				code=userView.getManagePrivCodeValue();
			}
	    	String whereIN=InfoUtils.getWhereINSql(userView,nbase);
	    	StringBuffer where=new StringBuffer();
	    	where.append(" where 1=1 and "+nbase+subset+".a0100="+nbase+"A01.a0100");
	    	where.append(" and "+nbase+subset+".a0100 in(select "+nbase+"A01.a0100 "+whereIN+")");
	        where.append(" and "+nbase+subset+".a0100 in( select "+nbase+"A01.a0100 from "+nbase+"A01 where");
	    	if("1".equals(kind))
			{
	    		where.append("  e0122 like '"+code+"%'");
			}else if("0".equals(kind))
			{
				where.append("  e01a1 like '"+code+"%'");	
			}else
			{
				where.append("  b0110 like '"+code+"%'");	
			}
	    	where.append(")");
	    	return where.toString();
	    }
}
