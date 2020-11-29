package com.hjsj.hrms.businessobject.gz;

import com.hjsj.hrms.businessobject.gz.gz_analyse.GzAnalyseBo;
import com.hjsj.hrms.interfaces.analyse.IParserConstant;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.TimeScope;
import com.hjsj.hrms.utils.analyse.YearMonthCount;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.tablemodel.ContentModel;
import com.hrms.frame.dao.tablemodel.ModelField;
import com.hrms.frame.dao.tablemodel.TableModel;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>Title:GrossManagBo.java</p>
 * <p>Description>:工资总额</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Mar 24, 2012  10:38:12 AM </p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class GrossManagBo 
{
	private Connection conn;
	private int current_i9999=0;
	private String priv_sql;
	private UserView userView;
	private String cascadingctrl;
	private String viewUnit;
	private String fc_flag="";
	public String getFc_flag() {
		return fc_flag;
	}
	public void setFc_flag(String fc_flag) {
		this.fc_flag = fc_flag;
	}
	public GrossManagBo()
	{
	}
   public GrossManagBo(Connection conn)
	{
		 this.conn=conn;
	}
   public GrossManagBo(Connection conn,UserView userView)
	{
		 this.conn=conn;
		 this.userView=userView;
	}
	/**
	 * 获取薪资管理的sql语句
	 * @param codesetid  子集id 
	 * @param time 查询时间
	 * @param codeitemid 部门id
	 * @return String sql语句
	 * @param comput 薪资总额计算用参数 0 非  1 计算
	 * @throws GeneralException,JDOMException
	 **/
	
	public String sqlStr(String codesetid,String time,String codeitemid,String sp_flag,String ctrl_type,String ctrl_peroid,int type,String filtervalue,String filtersql,String comput)
	{
		StringBuffer buf = new StringBuffer();
		buf.append("select ");
		String temp=viewFirms(codesetid,sp_flag,comput);
		buf.append(temp);
		if("1".equals(ctrl_peroid))
		{
			buf.append(" ,b0110 as b0110a ");
		}
		buf.append(" from ");
		buf.append(codesetid);
		buf.append(",organization o where ");
		if(Sql_switcher.searchDbServer()==Constant.ORACEL)
		{
			buf.append(" to_char("+codesetid+"z0,'YYYY')");
		}else
		{
	    	buf.append(Sql_switcher.year(codesetid+"."+codesetid+"z0"));
		}
		buf.append("= '");
		buf.append(time);
		buf.append("' ");
		if("1".equals(ctrl_type))
		{
			buf.append(" and b0110 like '");
			buf.append(codeitemid);
			buf.append("%' and o.codeitemid="+codesetid+".b0110 and (o.codesetid='UN' or o.codesetid ='UM')");
		}
		if("2".equals(ctrl_type))
		{
			buf.append(" and b0110 like '");
			buf.append(codeitemid);
			buf.append("%' and o.codeitemid="+codesetid+".b0110 and o.codesetid='UN'");
		}
		if(type==1)
		{
    		if("1".equals(ctrl_peroid))
    		{
	    		buf.append(" and ");
    			buf.append(Sql_switcher.month(codesetid+"."+codesetid+"z0"));
    			buf.append("=1");
    		}
		}
		if(filtersql!=null && filtersql.trim().length()>0)
			buf.append(filtersql);		
//		String bosdate=DateStyle.dateformat(new Date(),"yyyy-MM-dd");
//		buf.append(" and "+Sql_switcher.dateValue(bosdate)+" between o.start_date and o.end_date ");
		buf.append(" "+this.getPrivSQL(codesetid+".b0110", this.userView));
		if("0".equalsIgnoreCase(comput))
		{
			buf.append(" order by a0000,b0110,");
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
			{
				buf.append("to_char("+codesetid+"z0,'mm')");
			}
			else
			{
				buf.append(Sql_switcher.month(codesetid+"z0"));
			}
			if(this.fc_flag!=null&&fc_flag.length()!=0){
				buf.append(",");
				buf.append(codesetid);
				buf.append("z1");
			}
		}
		return buf.toString();
	}
	/**
	 * 获取RecordVo里面list值用于插入或修改数据库的值
	 * @param contentModel  RecordVo值 
	 * @param tableModel 
	 * @return String sql语句
	 **/
	public ArrayList getUpdateValues(ContentModel contentModel, TableModel tableModel,String setname){
        ModelField fields[] = tableModel.getFields();
        Map values = contentModel.getValues();
        ArrayList oRet = new ArrayList();
        String attrName = "";
        for(int i = 0; i < fields.length; i++){
            if(fields[i].getFieldType() == 0 && !fields[i].isBKey()){
                attrName = fields[i].getAttribute();
              
                if("b0110".equalsIgnoreCase(attrName)|| "i9999".equalsIgnoreCase(attrName)/*attrName.equalsIgnoreCase(setname+"z0")*/)
                	continue;
             
                if(values.containsKey(attrName))
                {
                    if(values.get(attrName) == null)
                        oRet.add(null);
                    else
                    {
                    	 if(attrName.equalsIgnoreCase(setname+"z0"))
                         {
                         	String z0 = ((String)values.get(attrName)).trim()+".1";
                         	oRet.add(z0);
                         }
                    	 else
                    	 {
                            if(values.get(attrName) instanceof String)
                               oRet.add(((String)values.get(attrName)).trim());
                            else
                               oRet.add(values.get(attrName));
                    	 }
                    }
                }
              
            }
        }
       /* String primaryKeys[] = tableModel.getPrimaryKeys();
        for(int i = 0; i < primaryKeys.length; i++)
            oRet.add(values.get(primaryKeys[i].substring(primaryKeys[i].indexOf(".") + 1)));*/

        return oRet;
    }
	/**
	 * 更新值的sql和值，，现在没有用
	 * @param contentModel
	 * @param tableModel
	 * @param setname
	 * @param othMap
	 * @return
	 */
	public HashMap updateValues(ContentModel contentModel, TableModel tableModel,String setname,HashMap othMap){
        HashMap infomap=new HashMap();
		ModelField fields[] = tableModel.getFields();
        Map values = contentModel.getValues();
        ArrayList oRet = new ArrayList();
        StringBuffer sql = new StringBuffer();
        sql.append("update "+setname+" set ");
        String attrName = "";
        for(int i = 0; i < fields.length; i++){
            if(fields[i].getFieldType() == 0 && !fields[i].isBKey()){
                attrName = fields[i].getAttribute();
              
                if("b0110".equalsIgnoreCase(attrName)|| "i9999".equalsIgnoreCase(attrName)/*attrName.equalsIgnoreCase(setname+"z0")*/)
                	continue;
                if(othMap.get(attrName.toLowerCase())!=null)
                	continue;
                if(values.containsKey(attrName))
                {
                	sql.append(attrName+" =?");
                	if(i!=fields.length-1)
                		sql.append(",");
                    if(values.get(attrName) == null)
                        oRet.add(null);
                    else
                    {
                    	 if(attrName.equalsIgnoreCase(setname+"z0"))
                         {
                         	String z0 = ((String)values.get(attrName)).trim()+".1";
                         	oRet.add(z0);
                         }
                    	 else
                    	 {
                            if(values.get(attrName) instanceof String)
                               oRet.add(((String)values.get(attrName)).trim());
                            else
                               oRet.add(values.get(attrName));
                    	 }
                    }
                }
              
            }
        }
        sql.append(" wnere ");
        infomap.put("sql",sql.toString());
        infomap.put("value",oRet);
        
       /* String primaryKeys[] = tableModel.getPrimaryKeys();
        for(int i = 0; i < primaryKeys.length; i++)
            oRet.add(values.get(primaryKeys[i].substring(primaryKeys[i].indexOf(".") + 1)));*/

        return infomap;
    }
	/**
	 * 查看单位总额明细
	 * @param codesetid
	 * @param time
	 * @param codeitemid
	 * @param sp_flag
	 * @param ctrl_type
	 * @param ctrl_peroid
	 * @return
	 */
	public HashMap getDetailInfo(String codesetid,String time,String sp_flag,String b0110,String ctrl_peroid,String season){
		HashMap mp = new HashMap();
		try
		{
    		StringBuffer buf = new StringBuffer();
    		StringBuffer column = new StringBuffer();
    		buf.append("select ");
    		//buf.append(viewFirms(codesetid,sp_flag));
    		HashMap map = new HashMap();
    		ArrayList list = new ArrayList();
    		ArrayList resultlist = new ArrayList();
    		ArrayList fieldset = codesetid!=null&&codesetid.length()>0?DataDictionary.getFieldList(codesetid,Constant.USED_FIELD_SET):new ArrayList();
    		for(int i=0;i<fieldset.size();i++)
     		{
    			FieldItem a_item = (FieldItem)fieldset.get(i);
			    FieldItem item = (FieldItem)a_item.cloneItem();
			    
			    if("0".equals(this.userView.analyseFieldPriv(item.getItemid())))
					continue;
			    
    			    if(item.getItemid().equalsIgnoreCase(codesetid+"z1"))
    			    {
    			    	continue;
    			    }
    				column.append(",");
    				if(item.getItemid().equalsIgnoreCase(codesetid+"z0"))
    				{
    					column.append(Sql_switcher.month(item.getItemid())+" as "+item.getItemid());
    					item.setItemdesc(ResourceFactory.getProperty("gz.acount.month").substring(0,ResourceFactory.getProperty("gz.acount.month").length()-1));
    					item.setCodesetid(item.getCodesetid());
    					list.add(0,item);
    				}
    				else 
    				{
    					if("D".equalsIgnoreCase(item.getItemtype()))
    					{
    						
    					    column.append(Sql_switcher.dateToChar(item.getItemid(), "YYYY-MM-DD") +" as "+item.getItemid());			
    						list.add(item);
    					}
    					else
    					{
    	    				column.append(item.getItemid());
    	    				if(item.getItemid().equalsIgnoreCase(sp_flag))
    		    			{
    			    			item.setItemdesc(ResourceFactory.getProperty("lable.zp_plan.status"));
    				    		item.setCodesetid(item.getCodesetid());
    			    			list.add(list.size()>1?1:0,item);
    			    		}
    			    		else
    			    		{
    			    			list.add(item);
    		    			}
    			    	}
    				}
    		}
    		if(column!=null && column.toString().trim().length()>0)
    		{
    		buf.append(column.toString().substring(1));
    		buf.append(" from ");
	    	buf.append(codesetid);
    		buf.append(" where "+Sql_switcher.year(codesetid+"."+codesetid+"z0")+"= '");
      		buf.append(time+"'");
      		if("2".equals(ctrl_peroid))
      		{
      			String seasoncondation=this.getSeasonCondation(Integer.parseInt(season));
      			buf.append(" and ");
      			buf.append(Sql_switcher.month(codesetid+"."+codesetid+"z0")+" in (");
      			buf.append(seasoncondation);
      			buf.append(")");
      		}
    		buf.append(" and "+codesetid+"."+"b0110='"+b0110);
    		buf.append("' order by "+codesetid+"z0");
    		ContentDAO dao = new ContentDAO(this.conn);
    		RowSet rs = null;
    		rs = dao.search(buf.toString());   		
    		LazyDynaBean bean=null;
    		GzAnalyseBo bo = new GzAnalyseBo();
    		while(rs.next())
    		{
    			bean = new LazyDynaBean();
    			for(int i=0;i<list.size();i++)
    			{
    				FieldItem item = (FieldItem)list.get(i);
    				String itemid = item.getItemid();    				
    				if("0".equals(this.userView.analyseFieldPriv(item.getItemid())))
    					continue;
    				/*if(itemid.equalsIgnoreCase(sp_flag))
    					bean.set(itemid,rs.getString(itemid)==null?"  ":(AdminCode.getCodeName("23",rs.getString(itemid))));*/
    				if(itemid.equalsIgnoreCase(codesetid+"z0"))
    				{
    					bean.set(itemid.toLowerCase(),rs.getString(itemid)==null?"  ":bo.getUpperMonth(Integer.parseInt(rs.getString(itemid))));
    				}
    				else if("A".equalsIgnoreCase(item.getItemtype())&&!"0".equalsIgnoreCase(item.getCodesetid()))
    				{
    					bean.set(itemid.toLowerCase(),(rs.getString(itemid)==null)?"  ":AdminCode.getCodeName(item.getCodesetid(), rs.getString(itemid)));
    				}
    				else
    				{
    					
    		    	   	bean.set(itemid.toLowerCase(),(rs.getString(itemid)==null)?"  ":rs.getString(itemid));
    				}
    			}
    			resultlist.add(bean);
    		}
    		}
    		mp.put("1",list);
    		mp.put("2",resultlist);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
 		return mp;
	}
	public HashMap getDetailInfo(String codesetid,String time,String sp_flag,String b0110,String ctrl_peroid,String season,String z1){
		HashMap mp = new HashMap();
		try
		{
    		StringBuffer buf = new StringBuffer();
    		StringBuffer column = new StringBuffer();
    		buf.append("select ");
    		//buf.append(viewFirms(codesetid,sp_flag));
    		HashMap map = new HashMap();
    		ArrayList list = new ArrayList();
    		ArrayList resultlist = new ArrayList();
    		ArrayList fieldset = codesetid!=null&&codesetid.length()>0?DataDictionary.getFieldList(codesetid,Constant.USED_FIELD_SET):new ArrayList();
    		for(int i=0;i<fieldset.size();i++)
     		{
    			    FieldItem a_item = (FieldItem)fieldset.get(i);
    			    FieldItem item = (FieldItem)a_item.cloneItem();
    			    
    			    if("0".equals(this.userView.analyseFieldPriv(item.getItemid())))
    					continue;
    			    
    			    if(item.getItemid().equalsIgnoreCase(codesetid+"z1"))
    			    {
    			    	if(this.fc_flag!=null&&fc_flag.length()!=0){
    			    		
    			    	}else
    			    		continue;
    			    }
    				column.append(",");
    				if(item.getItemid().equalsIgnoreCase(codesetid+"z0"))
    				{
    					column.append(Sql_switcher.month(item.getItemid())+" as "+item.getItemid());
    					item.setItemdesc(ResourceFactory.getProperty("gz.acount.month").substring(0,ResourceFactory.getProperty("gz.acount.month").length()-1));
    					item.setCodesetid(item.getCodesetid());
    					list.add(0,item);
    				}
    				else 
    				{
    					if("D".equalsIgnoreCase(item.getItemtype()))
    					{
    						
    					    column.append(Sql_switcher.dateToChar(item.getItemid(), "YYYY-MM-DD") +" as "+item.getItemid());			
    						list.add(item);
    					}
    					else
    					{
    	    				column.append(item.getItemid());
    	    				if(item.getItemid().equalsIgnoreCase(sp_flag))
    		    			{
    			    			item.setItemdesc(ResourceFactory.getProperty("lable.zp_plan.status"));
    				    		item.setCodesetid(item.getCodesetid());
    			    			list.add(list.size()>1?1:0,item);
    			    		}
    			    		else
    			    		{
    			    			list.add(item);
    		    			}
    			    	}
    				}
    		}
    		if(column!=null && column.toString().trim().length()>0)
    		{
    		buf.append(column.toString().substring(1));
    		buf.append(" from ");
	    	buf.append(codesetid);
    		buf.append(" where "+Sql_switcher.year(codesetid+"."+codesetid+"z0")+"= '");
      		buf.append(time+"'");
      		if("2".equals(ctrl_peroid))
      		{
      			String seasoncondation=this.getSeasonCondation(Integer.parseInt(season));
      			buf.append(" and ");
      			buf.append(Sql_switcher.month(codesetid+"."+codesetid+"z0")+" in (");
      			buf.append(seasoncondation);
      			buf.append(")");
      		}
    		buf.append(" and "+codesetid+"."+"b0110='"+b0110);
    		buf.append("'");
    		if(this.fc_flag!=null&&fc_flag.length()!=0&&z1!=null&&z1.length()!=0){
    			buf.append(" and "+codesetid+"z1=");
    			buf.append(z1);
    		}
    		buf.append(" order by "+codesetid+"z0");
    		ContentDAO dao = new ContentDAO(this.conn);
    		RowSet rs = null;
    		rs = dao.search(buf.toString());   		
    		LazyDynaBean bean=null;
    		GzAnalyseBo bo = new GzAnalyseBo();
    		while(rs.next())
    		{
    			bean = new LazyDynaBean();
    			for(int i=0;i<list.size();i++)
    			{
    				FieldItem item = (FieldItem)list.get(i);
    				String itemid = item.getItemid();
    				if("0".equals(this.userView.analyseFieldPriv(item.getItemid())))
    					continue;
    				/*if(itemid.equalsIgnoreCase(sp_flag))
    					bean.set(itemid,rs.getString(itemid)==null?"  ":(AdminCode.getCodeName("23",rs.getString(itemid))));*/
    				if(itemid.equalsIgnoreCase(codesetid+"z0"))
    				{
    					bean.set(itemid.toLowerCase(),rs.getString(itemid)==null?"  ":bo.getUpperMonth(Integer.parseInt(rs.getString(itemid))));
    				}
    				else if("A".equalsIgnoreCase(item.getItemtype())&&!"0".equalsIgnoreCase(item.getCodesetid()))
    				{
    					bean.set(itemid.toLowerCase(),(rs.getString(itemid)==null)?"  ":AdminCode.getCodeName(item.getCodesetid(), rs.getString(itemid)));
    				}
    				else
    				{
    					
    		    	   	bean.set(itemid.toLowerCase(),(rs.getString(itemid)==null)?"  ":rs.getString(itemid));
    				}
    			}
    			resultlist.add(bean);
    		}
    		}
    		mp.put("1",list);
    		mp.put("2",resultlist);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
 		return mp;
	}
	public String getSeasonCondation(int season)
	{
		StringBuffer buf = new StringBuffer();
		try
		{
			buf.append(season+","+(season+1)+","+(season+2));     
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return buf.toString();
	}
	
	public ArrayList getUpdateValues(ContentModel contentModel, TableModel tableModel){
        ModelField fields[] = tableModel.getFields();
        Map values = contentModel.getValues();
        ArrayList oRet = new ArrayList();
        String attrName = "";
        for(int i = 0; i < fields.length; i++){
            if(fields[i].getFieldType() == 0 && !fields[i].isBKey()){
                attrName = fields[i].getAttribute();
              
                if("b0110".equalsIgnoreCase(attrName)|| "i9999".equalsIgnoreCase(attrName))
                	continue;
                if(values.containsKey(attrName))
                    if(values.get(attrName) == null)
                        oRet.add(null);
                    else
                    {
                    	
                            if(values.get(attrName) instanceof String)
                               oRet.add(((String)values.get(attrName)).trim());
                            else
                               oRet.add(values.get(attrName));
                    	
                    }
              
            }
        }
       /* String primaryKeys[] = tableModel.getPrimaryKeys();
        for(int i = 0; i < primaryKeys.length; i++)
            oRet.add(values.get(primaryKeys[i].substring(primaryKeys[i].indexOf(".") + 1)));*/

        return oRet;
    }
	public String getUnitName(String itemvalue,String setid)
	{
		String name="";
		try
		{
			StringBuffer buf = new StringBuffer();
			buf.append("select codeitemdesc ,codesetid from organization o,");
			buf.append(setid+" a where a.b0110=o.codeitemid and UPPER(codesetid) not in('@K')");
			buf.append(" and a.b0110='"+itemvalue+"'");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs=dao.search(buf.toString());
			while(rs.next())
			{
				name=rs.getString("codeitemdesc");
				String codesetid=rs.getString("codesetid");
				if("UN".equalsIgnoreCase(codesetid))
					name=ResourceFactory.getProperty("column.sys.org")+":&nbsp;"+name;
				if("UM".equalsIgnoreCase(codesetid))
					name=ResourceFactory.getProperty("lable.hiremanage.dept_id")+":&nbsp;"+name;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return name;
	}
	/**
	 * 获取数据库查询的列数
	 * @param setname  codesetid值
	 * @return String 返回列以逗号隔开
	 **/
	public String viewFirms(String setname,String sp_flag,String comput)
	{
		StringBuffer buf = new StringBuffer();
		if(sp_flag!=null&&sp_flag.trim().length()>0)
		{
			buf.append("(select codeitemid from codeitem where codesetid='23'");
			buf.append(" and codeitemid= ");
			buf.append(setname+"."+sp_flag);
			buf.append(") as "+sp_flag+",");
		}
		
		//在已经作废的组织机构上添加标记 zhanghua 2017-4-8
		String dateSql="";
		String add="";
		if(Sql_switcher.searchDbServer() == Constant.ORACEL||Sql_switcher.searchDbServer() == Constant.DB2)
			add="||";
		else
			add="+";
		if("0".equals(comput))
			dateSql+=" (case when "+Sql_switcher.dateValue(DateStyle.dateformat(new Date(),"yyyy-MM-dd"))
				+" between o.start_date and o.end_date then o.codeitemdesc  "
				+ " else o.codeitemdesc "+add+"'(已作废)' end ) as B0110text,";
		
		
		if("0".equalsIgnoreCase(comput))
			buf.append(dateSql+"B0110,id,");
		else
			buf.append(dateSql+"B0110,");
		
		ArrayList fieldset = DataDictionary.getFieldList(setname,Constant.USED_FIELD_SET);
				
		for(int i=0;i<fieldset.size();i++)
		{
			FieldItem fielditem = (FieldItem)fieldset.get(i);
			/*if(!fielditem.getItemdesc().equals(ResourceFactory.getProperty("hmuster.label.nybs"))&&!fielditem.getItemdesc().equals(ResourceFactory.getProperty("hmuster.label.counts"))){*/
			if(fielditem.getItemid().equalsIgnoreCase(setname+"z1")){
				if(this.fc_flag!=null&&fc_flag.length()!=0){
					
				}else{
					continue;
				}
			}
			if(fielditem.getItemid().equalsIgnoreCase(setname+"z0"))
			{
				String yearcloumn=setname+"z0";
				if(i == fieldset.size()-1)
				{					
					if(Sql_switcher.searchDbServer() == Constant.ORACEL||Sql_switcher.searchDbServer() == Constant.DB2)
					{
						if("0".equalsIgnoreCase(comput))
							buf.append("to_char("+setname+"."+yearcloumn+",'YYYY.MM') as aaaa,");	
						else
							buf.append(setname+"."+yearcloumn+" as "+yearcloumn+",");
					}else
					{
						if("0".equalsIgnoreCase(comput))
							buf.append("ltrim(str("+Sql_switcher.year(setname+"."+yearcloumn)+"))+'.'+ltrim(str("+Sql_switcher.month(setname+"."+yearcloumn)+")) as aaaa,");
						else
							buf.append(setname+"."+yearcloumn+" as "+yearcloumn+",");
					}
				}else
				{	
					if(Sql_switcher.searchDbServer() == Constant.ORACEL||Sql_switcher.searchDbServer() == Constant.DB2)
					{
						if("0".equalsIgnoreCase(comput))
							buf.append("to_char("+setname+"."+yearcloumn+",'YYYY.MM') as aaaa,");
						else
							buf.append(setname+"."+yearcloumn+" as "+yearcloumn+",");
					}else
					{
						if("0".equalsIgnoreCase(comput))
							buf.append("ltrim(str("+Sql_switcher.year(setname+"."+yearcloumn)+"))+'.'+Right('100'+ltrim(str("+Sql_switcher.month(setname+"."+yearcloumn)+")),2) as aaaa ,");
						else
							buf.append(setname+"."+yearcloumn+" as "+yearcloumn+",");
					}
				}
			}
			else if(!fielditem.getItemid().equalsIgnoreCase(sp_flag))
			{
				if(i == fieldset.size()-1)
				{
					buf.append(fielditem.getItemid()+",");
					
				}else
				{
					buf.append(fielditem.getItemid());
					buf.append(",");
				}
			}
			//}
		}
		String viewfirms = buf.toString();
		
		return viewfirms.substring(0,viewfirms.length()-1);
	}
	/**
	 * 过滤sql语句
	 * @param cloumn
	 * @param filtervalue
	 * @param ctrl_peroid
	 * @return
	 */
	public String getFilterSql(String cloumn,String filtervalue,String ctrl_peroid,String sp_flag,String spType)
	{
		StringBuffer sql = new StringBuffer("");
		try
		{
			if("0".equals(ctrl_peroid))//month
			{
				if(!"0".equals(filtervalue))
					sql.append(" and "+ Sql_switcher.month(cloumn)+"="+filtervalue);
			}
			else
			{
				int value = Integer.parseInt(filtervalue);
				if(!"0".equals(filtervalue) && value<=4)
				{
	    			sql.append(" and "+ Sql_switcher.month(cloumn)+" in (");	    			
		     		if(value==1)
	        			sql.append("1,2,3");
		     		else if(value==2)
		    			sql.append("4,5,6");
		     		else if(value==3)
		    			sql.append("7,8,9");
		     		else if(value==4)
			    		sql.append("10,11,12");
		    		sql.append(")");
				}				 
			}
			// 按审批状态过滤
			if(sp_flag!=null && sp_flag.trim().length()>0 && !"0".equalsIgnoreCase(spType))
				sql.append(" and "+ sp_flag +" = '"+spType+"' ");			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return sql.toString();
	}
	/**
	 * 按年来控制
	 * @param setname
	 * @param time
	 * @param sp_flag
	 * @param datalist
	 * @param codeitemid
	 * @param ctrl_type
	 * @param comput 薪资总额计算用参数 0 非  1 计算
	 * @return
	 */
	public String getColumnSql(String setname,String time,String sp_flag,ArrayList datalist,String codeitemid,String ctrl_type,String spType,String comput){
		StringBuffer buf = new StringBuffer();
		HashMap  map = new HashMap();
		HashMap mp = new HashMap();
		HashMap pm= new HashMap();
		for(int j=0;j<datalist.size();j++)
		{
    		LazyDynaBean bean = (LazyDynaBean)datalist.get(j);
			String realitem = (String)bean.get("realitem");
			String balanceitem = (String)bean.get("balanceitem");
			String planitem=(String)bean.get("planitem");
			map.put(realitem.toLowerCase(), realitem);
			mp.put(balanceitem.toLowerCase(),balanceitem);
			pm.put(planitem.toLowerCase(),planitem);
		}
		
		//在已经作废的组织机构上添加标记 zhanghua 2017-4-8
		String dateSql="";
		String add="";
		if(Sql_switcher.searchDbServer() == Constant.ORACEL||Sql_switcher.searchDbServer() == Constant.DB2)
			add="||";
		else
			add="+";
		if("0".equals(comput))
			dateSql+=", (case when "+Sql_switcher.dateValue(DateStyle.dateformat(new Date(),"yyyy-MM-dd"))
				+" between o.start_date and o.end_date then o.codeitemdesc  "
				+ " else o.codeitemdesc "+add+"'(已作废)' end ) as B0110text";
		
		buf.append("select d.* "+dateSql+" from (");
		buf.append(" select ");
		
		if(sp_flag!=null&&sp_flag.trim().length()>0){
			buf.append(" (select codeitemid from codeitem where codesetid='23'");
			buf.append(" and codeitemid= ");
			buf.append(setname+"."+sp_flag);
			buf.append(") as "+sp_flag+",");
		}
		buf.append(setname+".B0110,");
		
		if("0".equalsIgnoreCase(comput))
			buf.append(setname+".b0110 as b0110a ,");
		ArrayList fieldset = DataDictionary.getFieldList(setname,Constant.USED_FIELD_SET);
		for(int i=0;i<fieldset.size();i++){
			FieldItem fielditem = (FieldItem)fieldset.get(i);
			if(fielditem.getItemid().equalsIgnoreCase(setname+"z1"))
			{
				if(this.fc_flag!=null&&this.fc_flag.length()!=0){
					
				}else{
					continue;
				}
			}
				
			if(fielditem.getItemid().equalsIgnoreCase(setname+"z0"))
			{
				String yearcloumn=setname+"z0";				
				if(Sql_switcher.searchDbServer() == Constant.ORACEL||Sql_switcher.searchDbServer() == Constant.DB2)
				{
					if("0".equalsIgnoreCase(comput))
					{
						buf.append("to_char("+setname+"."+yearcloumn+",'YYYY.MM') as aaaa , ");
						buf.append(Sql_switcher.year(setname+"."+yearcloumn)+" as "+yearcloumn+"b,");
						buf.append(setname+"."+yearcloumn+" as "+yearcloumn+"a,");
					}else
						buf.append(setname+"."+yearcloumn+" as "+yearcloumn+",");
				}
				else
				{
					if("0".equalsIgnoreCase(comput))
					{
						buf.append(Sql_switcher.year(setname+"."+yearcloumn)+" as "+yearcloumn+"b,");
						buf.append("ltrim(str("+Sql_switcher.year(setname+"."+yearcloumn)+"))+'.'+ltrim(str("+Sql_switcher.month(setname+"."+yearcloumn)+")) as aaaa,");
						buf.append(setname+"."+yearcloumn+" as "+yearcloumn+"a,");
					}else
						buf.append(setname+"."+yearcloumn+" as "+yearcloumn+",");
				}				
			}
			else 
			{
				if(pm.get(fielditem.getItemid().toLowerCase())!=null)//计划
				{
					buf.append("a"+String.valueOf(i)+"."+fielditem.getItemid()+",");
				}
				else if(map.get(fielditem.getItemid().toLowerCase())!=null)//实发
				{
					buf.append("a"+String.valueOf(i)+"."+fielditem.getItemid()+",");
				}
				else if(mp.get(fielditem.getItemid().toLowerCase())!=null)//剩余
				{
					buf.append("a"+String.valueOf(i)+"."+fielditem.getItemid()+",");
				}
				else if(!fielditem.getItemid().equalsIgnoreCase(sp_flag))
				{
					if(this.fc_flag!=null&&this.fc_flag.length()!=0)
					{
     					if(fielditem.getItemid().equalsIgnoreCase(this.fc_flag))
     					{
     						buf.append("a"+String.valueOf(i)+"."+fielditem.getItemid()+",");
     					}else
     					{
     						if("N".equalsIgnoreCase(fielditem.getItemtype()) && !fielditem.getItemid().equalsIgnoreCase(setname+"z1"))
     							buf.append("a"+String.valueOf(i)+"."+fielditem.getItemid()+",");
     						else
     							buf.append(setname+"."+fielditem.getItemid()+",");
     					}
     				}else
     				{
     					if("N".equalsIgnoreCase(fielditem.getItemtype()) && !fielditem.getItemid().equalsIgnoreCase(setname+"z1"))
 							buf.append("a"+String.valueOf(i)+"."+fielditem.getItemid()+",");
 						else
 							buf.append(setname+"."+fielditem.getItemid()+",");
     				}
				}
			}
		}
		buf.setLength(buf.length()-1);
		buf.append(" from ");
		buf.append(setname);
		for(int i=0;i<fieldset.size();i++){
			FieldItem fielditem = (FieldItem)fieldset.get(i);
			if(map.get(fielditem.getItemid().toLowerCase())!=null||pm.get(fielditem.getItemid().toLowerCase())!=null)//实发
			{
				if(this.fc_flag!=null&&this.fc_flag.length()!=0){
					buf.append(" left join (select b0110,"+setname+"z1,sum("+fielditem.getItemid()+") as "+fielditem.getItemid()+" from "+setname);
					buf.append(" s where "+Sql_switcher.year("s."+setname+"z0")+"="+time);
					buf.append(" group by s.b0110,"+"s."+setname+"z1"+" ) a"+i+" on "+setname+".b0110=a"+i+".b0110 and a"+i+"."+setname+"z1="+setname+"."+setname+"z1");
				}else{
					buf.append(" left join (select b0110,sum("+fielditem.getItemid()+") as "+fielditem.getItemid()+" from "+setname);
					buf.append(" s where "+Sql_switcher.year("s."+setname+"z0")+"="+time);
					buf.append(" group by s.b0110) a"+i+" on "+setname+".b0110=a"+i+".b0110");
				}
				
			}
			else if(mp.get(fielditem.getItemid().toLowerCase())!=null)//剩余
			{
				String planitem = "";
				String realitem="";
				for(int j=0;j<datalist.size();j++)
				{
		    		LazyDynaBean bean = (LazyDynaBean)datalist.get(j);
					String balanceitem = (String)bean.get("balanceitem");
					if(balanceitem.equalsIgnoreCase(fielditem.getItemid()))
					{
						realitem = (String)bean.get("realitem");
						planitem=(String)bean.get("planitem");
					}
					else
					{
						continue;
					}
					
				}
				//buf.append(" left join (select b0110,case when (T."+realitem+"=0 or T."+realitem+" is null) then 0 ");
				//buf.append(" else (T."+planitem+"-T."+realitem+") end as "+fielditem.getItemid()+" from ");
				//when T."+fielditem.getItemid()+"=(T."+planitem+"-T."+realitem+") 
				if(this.fc_flag!=null&&this.fc_flag.length()!=0){
					buf.append(" left join (select b0110,"+setname+"z1,"+fielditem.getItemid()+" from ");
					buf.append("(select b0110,"+setname+"z1,sum("+realitem+") as "+realitem+",sum("+fielditem.getItemid()+") as "+fielditem.getItemid()+",sum("+planitem+") as "+planitem+" from "+setname);
					buf.append(" s  where "+Sql_switcher.year("s."+setname+"z0")+"="+time);
					buf.append(" group by s.b0110,"+"s."+setname+"z1"+" ) T) a"+i+" on "+setname+".b0110=a"+i+".b0110 and a"+i+"."+setname+"z1="+setname+"."+setname+"z1");
				}else{
					buf.append(" left join (select b0110,"+fielditem.getItemid()+" from ");
					buf.append("(select b0110, sum("+realitem+") as "+realitem+",sum("+fielditem.getItemid()+") as "+fielditem.getItemid()+",sum("+planitem+") as "+planitem+" from "+setname);
					buf.append(" s  where "+Sql_switcher.year("s."+setname+"z0")+"="+time);
					buf.append(" group by s.b0110) T) a"+i+" on "+setname+".b0110=a"+i+".b0110");
				}
			}else
			{
 				if(this.fc_flag!=null&&fc_flag.length()!=0)
 				{
 					if(fielditem.getItemid().equalsIgnoreCase(this.fc_flag))
 					{
     					buf.append(" left join (select b0110,"+setname+"z1,"+"max("+this.fc_flag+") as "+this.fc_flag+" from "+setname);
		    			buf.append(" s where "+Sql_switcher.year("s."+setname+"z0")+"="+time);
		    			buf.append(" group by s.b0110 ,s."+setname+"z1"+") a"+i+" on "+setname+".b0110=a"+i+".b0110 and "+setname+"."+setname+"z1=a"+i+"."+setname+"z1");
 					}else
 					{
 						if("N".equalsIgnoreCase(fielditem.getItemtype()) && !fielditem.getItemid().equalsIgnoreCase(setname+"z1"))
 						{
 							buf.append(" left join (select b0110,sum("+fielditem.getItemid()+") as "+fielditem.getItemid()+" from "+setname);
 							buf.append(" s where "+Sql_switcher.year("s."+setname+"z0")+"="+time);
 							buf.append(" group by s.b0110) a"+i+" on "+setname+".b0110=a"+i+".b0110");
 						}
 					}
 				}else
 				{
 					if("N".equalsIgnoreCase(fielditem.getItemtype()) && !fielditem.getItemid().equalsIgnoreCase(setname+"z1"))
					{
						buf.append(" left join (select b0110,sum("+fielditem.getItemid()+") as "+fielditem.getItemid()+" from "+setname);
						buf.append(" s where "+Sql_switcher.year("s."+setname+"z0")+"="+time);
						buf.append(" group by s.b0110) a"+i+" on "+setname+".b0110=a"+i+".b0110");
					}
 				}
 			}
		}
		if("0".equalsIgnoreCase(comput))
			buf.append(") d,organization o where "+Sql_switcher.year("d."+setname+"z0a")+"= '");
		else
			buf.append(") d,organization o where "+Sql_switcher.year("d."+setname+"z0")+"= '");
		buf.append(time);
		buf.append("' ");
		if("1".equals(ctrl_type))
		{
			buf.append(" and d.b0110 like '");
			buf.append(codeitemid);
			buf.append("%' and o.codeitemid=d.b0110 and (o.codesetid='UN' or o.codesetid ='UM')");
		}
		if("2".equals(ctrl_type))
		{
			buf.append(" and d.b0110 like '");
			buf.append(codeitemid);
			buf.append("%' and o.codeitemid=d.b0110 and o.codesetid='UN'");
		}
		buf.append(" "+this.getPrivSQL("d.b0110", this.userView));
		buf.append(" and ");
		if("0".equalsIgnoreCase(comput))
			buf.append(Sql_switcher.month("d."+setname+"z0a"));
		else
			buf.append(Sql_switcher.month("d."+setname+"z0"));
		buf.append("=1");
		
		// 按审批状态过滤
		if(sp_flag!=null && sp_flag.trim().length()>0 && !"0".equalsIgnoreCase(spType))
			buf.append(" and "+ sp_flag +" = '"+spType+"' ");
		/**无效的也显示，为了查看历史记录，刘红梅要求，2011-06-10*/
//		String bosdate=DateStyle.dateformat(new Date(),"yyyy-MM-dd");
//		buf.append(" and "+Sql_switcher.dateValue(bosdate)+" between o.start_date and o.end_date ");
		if("0".equalsIgnoreCase(comput))
		{
			buf.append(" order by o.a0000,b0110,"+setname+"z0a");
			if(this.fc_flag!=null&&fc_flag.length()!=0) //20141212 dengcan  没考虑封存参数下需追加z1排序
			{
				buf.append(",");
				buf.append(setname);
				buf.append("z1");
			}
		}
		String viewfirms = buf.toString();
		
		return viewfirms;
	}
	/**
	 * 按季度控制
	 * @param setname
	 * @param time
	 * @param sp_flag
	 * @param datalist
	 * @param codeitemid
	 * @param ctrl_type
	 * @param comput 薪资总额计算用参数 0 非  1 计算
	 * @return
	 */
	public String getColumnSqlBySeason(String setname,String time,String sp_flag,ArrayList datalist,String codeitemid,String ctrl_type,String filtervalue,String filtersql,String comput){
		StringBuffer buf = new StringBuffer();
		int[] season = {1,4,7,10};
		HashMap  map = new HashMap();
		HashMap mp = new HashMap();
		HashMap pm= new HashMap();
		for(int j=0;j<datalist.size();j++)
		{
    		LazyDynaBean bean = (LazyDynaBean)datalist.get(j);
			String realitem = (String)bean.get("realitem");
			String balanceitem = (String)bean.get("balanceitem");
			String planitem=(String)bean.get("planitem");
			map.put(realitem.toLowerCase(), realitem);
			mp.put(balanceitem.toLowerCase(),balanceitem);
			pm.put(planitem.toLowerCase(),planitem);
		}
		
		//在已经作废的组织机构上添加标记 zhanghua 2017-4-8
		String dateSql="";
		String add="";
		if(Sql_switcher.searchDbServer() == Constant.ORACEL||Sql_switcher.searchDbServer() == Constant.DB2)
			add="||";
		else
			add="+";
		if("0".equals(comput))
			dateSql+=", (case when "+Sql_switcher.dateValue(DateStyle.dateformat(new Date(),"yyyy-MM-dd"))
				+" between o.start_date and o.end_date then o.codeitemdesc  "
				+ " else o.codeitemdesc "+add+"'(已作废)' end ) as B0110text";
		
		
		buf.append("select x.* from(");
		for(int x=0;x<season.length;x++)
		{
			if("0".equalsIgnoreCase(comput))
				buf.append("select d.*,a0000 "+dateSql+" from (");
			else
				buf.append("select d.* "+dateSql+" from (");
	    	buf.append(" select ");
		
    		if(sp_flag!=null&&sp_flag.trim().length()>0){
    			buf.append(" (select codeitemid from codeitem where codesetid='23'");
	    		buf.append(" and codeitemid= ");
	    		buf.append(setname+"."+sp_flag);
	    		buf.append(") as "+sp_flag+",");
	    	}
    		//if(this.fc_flag!=null&&fc_flag.length()!=0){
//    			buf.append(" (select codeitemid from codeitem where codesetid='45'");
//	    		buf.append(" and codeitemid= ");
//	    		buf.append(setname+"."+fc_flag);
//	    		buf.append(") as "+fc_flag+",");
    		//}
	     	buf.append(setname+".B0110,");
	     	if("0".equalsIgnoreCase(comput))
	     		buf.append(setname+".b0110 as b0110a ,");
	    	ArrayList fieldset = DataDictionary.getFieldList(setname,Constant.USED_FIELD_SET);
    		for(int i=0;i<fieldset.size();i++){
    			FieldItem fielditem = (FieldItem)fieldset.get(i);    			
    			/*if(!fielditem.getItemdesc().equals(ResourceFactory.getProperty("hmuster.label.nybs"))&&!fielditem.getItemdesc().equals(ResourceFactory.getProperty("hmuster.label.counts"))){*/
    			if(fielditem.getItemid().equalsIgnoreCase(setname+"z1"))
    			{
    				if(this.fc_flag!=null&&this.fc_flag.length()!=0){
    					
    				}else{
    					continue;
    				}
    			}
    			if(fielditem.getItemid().equalsIgnoreCase(setname+"z0"))
		    	{
		    		String yearcloumn=setname+"z0";		    		
			    	if(Sql_switcher.searchDbServer() == Constant.ORACEL||Sql_switcher.searchDbServer() == Constant.DB2)
			    	{
			    		if("0".equalsIgnoreCase(comput))
			    		{
			    			buf.append("to_char("+setname+"."+yearcloumn+",'YYYY.MM') as aaaa , ");
			    			buf.append("'"+this.getSeason(x)+"' as " +yearcloumn+"b,");
		    				buf.append(setname+"."+yearcloumn+" as "+yearcloumn+"a,");
		    				buf.append(Sql_switcher.month(setname+"."+yearcloumn)+" as season,");
			    		}
			    		else
			    			buf.append(setname+"."+yearcloumn+" as "+yearcloumn+",");
			    	}
		    		else
		    		{
		    			if("0".equalsIgnoreCase(comput))
			    		{
		    				buf.append("ltrim(str("+Sql_switcher.year(setname+"."+yearcloumn)+"))+'.'+ltrim(str("+Sql_switcher.month(setname+"."+yearcloumn)+")) as aaaa ,");
		    				buf.append("'"+this.getSeason(x)+"' as " +yearcloumn+"b,");
		    				buf.append(setname+"."+yearcloumn+" as "+yearcloumn+"a,");
		    				buf.append(Sql_switcher.month(setname+"."+yearcloumn)+" as season,");
			    		}
			    		else
			    			buf.append(setname+"."+yearcloumn+" as "+yearcloumn+",");
		    		}		    		
	    		}
	    		else 
    			{
    				if(pm.get(fielditem.getItemid().toLowerCase())!=null)//计划
    				{
	    				buf.append("a"+String.valueOf(i)+"."+fielditem.getItemid()+",");
	    			}
	    			else if(map.get(fielditem.getItemid().toLowerCase())!=null)//实发
	    			{
	    				buf.append("a"+String.valueOf(i)+"."+fielditem.getItemid()+",");
    				}
	    			else if(mp.get(fielditem.getItemid().toLowerCase())!=null)//剩余
	    			{
	    				buf.append("a"+String.valueOf(i)+"."+fielditem.getItemid()+",");
	    			}
	     			else if(!fielditem.getItemid().equalsIgnoreCase(sp_flag))
	     			{
	     				if(this.fc_flag!=null&&this.fc_flag.length()!=0)
	     				{
	     					if(fielditem.getItemid().equalsIgnoreCase(this.fc_flag))
	     					{
	     						buf.append("a"+String.valueOf(i)+"."+fielditem.getItemid()+",");
	     					}else
	     					{
	     						if("N".equalsIgnoreCase(fielditem.getItemtype()) && !fielditem.getItemid().equalsIgnoreCase(setname+"z1"))
	     							buf.append("a"+String.valueOf(i)+"."+fielditem.getItemid()+",");
	     						else
	     							buf.append(setname+"."+fielditem.getItemid()+",");		    					
	     					}
	     				}else
	     				{
	     					if("N".equalsIgnoreCase(fielditem.getItemtype()) && !fielditem.getItemid().equalsIgnoreCase(setname+"z1"))
     							buf.append("a"+String.valueOf(i)+"."+fielditem.getItemid()+",");
     						else
     							buf.append(setname+"."+fielditem.getItemid()+",");
	     				}
	     			}
	    		}
    		}
    		buf.setLength(buf.length()-1);
    		buf.append(" from ");
    		buf.append(setname);
	    	for(int i=0;i<fieldset.size();i++){
	    		FieldItem fielditem = (FieldItem)fieldset.get(i);
	    		if(map.get(fielditem.getItemid().toLowerCase())!=null||pm.get(fielditem.getItemid().toLowerCase())!=null)//实发
	    		{
	    			if(this.fc_flag!=null&&fc_flag.length()!=0){
	    				buf.append(" left join (select b0110,"+setname+"z1,"+"sum("+fielditem.getItemid()+") as "+fielditem.getItemid()+" from "+setname);
		    			buf.append(" s where "+Sql_switcher.year("s."+setname+"z0")+"="+time);
		    			int temp=season[x];
		    			buf.append(" and "+Sql_switcher.month("s."+setname+"z0")+" in("+temp+","+(temp+1)+","+(temp+2)+")");
		    			buf.append(" group by s.b0110 ,s."+setname+"z1"+") a"+i+" on "+setname+".b0110=a"+i+".b0110 and "+setname+"."+setname+"z1=a"+i+"."+setname+"z1");
	    			}else{
		    			buf.append(" left join (select b0110,sum("+fielditem.getItemid()+") as "+fielditem.getItemid()+" from "+setname);
		    			buf.append(" s where "+Sql_switcher.year("s."+setname+"z0")+"="+time);
		    			int temp=season[x];
		    			buf.append(" and "+Sql_switcher.month("s."+setname+"z0")+" in("+temp+","+(temp+1)+","+(temp+2)+")");
		    			buf.append(" group by s.b0110 ) a"+i+" on "+setname+".b0110=a"+i+".b0110 ");
	    			}
    			}
    			else if(mp.get(fielditem.getItemid().toLowerCase())!=null)//剩余
	    		{
    				String planitem = "";
    				String realitem="";
    				for(int j=0;j<datalist.size();j++)
    				{
    		    		LazyDynaBean bean = (LazyDynaBean)datalist.get(j);
    					String balanceitem = (String)bean.get("balanceitem");
    					if(balanceitem.equalsIgnoreCase(fielditem.getItemid()))
    					{
    						realitem = (String)bean.get("realitem");
    						planitem=(String)bean.get("planitem");
    					}
    					else
    					{
    						continue;
    					}
    					
    				}
    				int temp=season[x];
    				//buf.append(" left join (select b0110,case when T."+fielditem.getItemid()+"=(T."+planitem+"-T."+realitem+") ");
    				//buf.append("then T."+fielditem.getItemid()+" else T."+planitem+" end as "+fielditem.getItemid()+" from ");
    				//buf.append(" left join (select b0110,case when (T."+realitem+"=0 or T."+realitem+" is null) then 0 ");
    				//buf.append(" else (T."+planitem+"-T."+realitem+") end as "+fielditem.getItemid()+" from ");
    				if(this.fc_flag!=null&&fc_flag.length()!=0){
	    				buf.append(" left join (select b0110,"+setname+"z1,"+fielditem.getItemid()+" from ");
	    				buf.append("(select b0110, "+setname+"z1,"+"sum("+realitem+") as "+realitem+",sum("+fielditem.getItemid()+") as "+fielditem.getItemid()+",sum("+planitem+") as "+planitem+" from "+setname);
	    				buf.append(" s  where "+Sql_switcher.year("s."+setname+"z0")+"="+time);
	    				buf.append(" and "+Sql_switcher.month("s."+setname+"z0")+" in("+temp+","+(temp+1)+","+(temp+2)+")");
	    				buf.append(" group by s.b0110,s."+setname+"z1"+") T) a"+i+" on "+setname+".b0110=a"+i+".b0110 and "+setname+"."+setname+"z1=a"+i+"."+setname+"Z1");
    				}else{
    					buf.append(" left join (select b0110,"+fielditem.getItemid()+" from ");
	    				buf.append("(select b0110, sum("+realitem+") as "+realitem+",sum("+fielditem.getItemid()+") as "+fielditem.getItemid()+",sum("+planitem+") as "+planitem+" from "+setname);
	    				buf.append(" s  where "+Sql_switcher.year("s."+setname+"z0")+"="+time);
	    				buf.append(" and "+Sql_switcher.month("s."+setname+"z0")+" in("+temp+","+(temp+1)+","+(temp+2)+")");
	    				buf.append(" group by s.b0110) T) a"+i+" on "+setname+".b0110=a"+i+".b0110 ");
    				}
    				/*
    				buf.append(" left join (select b0110, sum("+fielditem.getItemid()+") as "+fielditem.getItemid()+" from "+setname);
    				int temp=season[x];
	    			buf.append(" s  where "+Sql_switcher.year("s."+setname+"z0")+"="+time);
	    			buf.append(" and "+Sql_switcher.month("s."+setname+"z0")+" in("+temp+","+(temp+1)+","+(temp+2)+")");
	    			buf.append(" group by s.b0110) a"+i+" on "+setname+".b0110=a"+i+".b0110");*/
     			}else
     			{
     				if(this.fc_flag!=null&&fc_flag.length()!=0)
     				{
     					if(fielditem.getItemid().equalsIgnoreCase(this.fc_flag))
     					{
	     					buf.append(" left join (select b0110,"+setname+"z1,"+"max("+this.fc_flag+") as "+this.fc_flag+" from "+setname);
			    			buf.append(" s where "+Sql_switcher.year("s."+setname+"z0")+"="+time);
			    			int temp=season[x];
			    			buf.append(" and "+Sql_switcher.month("s."+setname+"z0")+" in("+temp+","+(temp+1)+","+(temp+2)+")");
			    			buf.append(" group by s.b0110 ,s."+setname+"z1"+") a"+i+" on "+setname+".b0110=a"+i+".b0110 and "+setname+"."+setname+"z1=a"+i+"."+setname+"z1");
     					}else
     					{
     						if("N".equalsIgnoreCase(fielditem.getItemtype()) && !fielditem.getItemid().equalsIgnoreCase(setname+"z1"))
     						{
	     						buf.append(" left join (select b0110,sum("+fielditem.getItemid()+") as "+fielditem.getItemid()+" from "+setname);
	    		    			buf.append(" s where "+Sql_switcher.year("s."+setname+"z0")+"="+time);
	    		    			int temp=season[x];
	    		    			buf.append(" and "+Sql_switcher.month("s."+setname+"z0")+" in("+temp+","+(temp+1)+","+(temp+2)+")");
	    		    			buf.append(" group by s.b0110 ) a"+i+" on "+setname+".b0110=a"+i+".b0110 ");
     						}
     					}
     				}else
     				{
     					if("N".equalsIgnoreCase(fielditem.getItemtype()) && !fielditem.getItemid().equalsIgnoreCase(setname+"z1"))
 						{
	     					buf.append(" left join (select b0110,sum("+fielditem.getItemid()+") as "+fielditem.getItemid()+" from "+setname);
			    			buf.append(" s where "+Sql_switcher.year("s."+setname+"z0")+"="+time);
			    			int temp=season[x];
			    			buf.append(" and "+Sql_switcher.month("s."+setname+"z0")+" in("+temp+","+(temp+1)+","+(temp+2)+")");
			    			buf.append(" group by s.b0110 ) a"+i+" on "+setname+".b0110=a"+i+".b0110 ");
 						}
     				}
     			}
    		}
	    	if("0".equalsIgnoreCase(comput))
	    		buf.append(") d,organization o where "+Sql_switcher.year("d."+setname+"z0a")+"= '");
	    	else
	    		buf.append(") d,organization o where "+Sql_switcher.year("d."+setname+"z0")+"= '");
    		buf.append(time);
    		buf.append("' ");
    		if("1".equals(ctrl_type))
	    	{
    			buf.append(" and d.b0110 like '");
    			buf.append(codeitemid);
	    		buf.append("%' and o.codeitemid=d.b0110 and (o.codesetid='UN' or o.codesetid ='UM')");
	    	}
	    	if("2".equals(ctrl_type))
	    	{
    			buf.append(" and d.b0110 like '");
	    		buf.append(codeitemid);
    			buf.append("%' and o.codeitemid=d.b0110 and o.codesetid='UN'");
    		}
	    	buf.append(" "+this.getPrivSQL("d.b0110",this. userView));
    		buf.append(" and ");
    		if("0".equalsIgnoreCase(comput))
    			buf.append(Sql_switcher.month("d."+setname+"z0a"));
    		else
    			buf.append(Sql_switcher.month("d."+setname+"z0"));
    		buf.append("="+season[x]);
    		//String bosdate=DateStyle.dateformat(new Date(),"yyyy-MM-dd");
    		//buf.append(" and "+Sql_switcher.dateValue(bosdate)+" between o.start_date and o.end_date ");
    		if(season.length-x!=1)
    		{
    			buf.append(" union all ");//备注型 union 报错 wangrd 2015-02-02
    		}
		}
		buf.append(") x");
		if(filtersql!=null && filtersql.trim().length()>0)
		{
			buf.append(" where 1=1 ");
			buf.append(filtersql);
		}
		if("0".equalsIgnoreCase(comput))
		{
			buf.append(" order by a0000,b0110,"+setname+"z0a");
			if(this.fc_flag!=null&&fc_flag.length()!=0)
			{
				buf.append(",");
				buf.append(setname);
				buf.append("z1");
			}
		}
		String viewfirms = buf.toString();
		return viewfirms;
	}
	public String getSeason(int type)
	{
		String season="";
		switch(type)
		{
		case 0:
			season = ResourceFactory.getProperty("report.pigionhole.oneQuarter");
			break;
		case 1:
			season=ResourceFactory.getProperty("report.pigionhole.twoQuarter");
			break;
		case 2:
			season=ResourceFactory.getProperty("report.pigionhole.threeQuarter");
			break;
		case 3:
			season=ResourceFactory.getProperty("report.pigionhole.fourQuarter");
			break;
		}
		return season;
	}
	/**
	 * 获取hrms:dataset标签所需的list
	 * @param setname  codesetid值
	 * @return ArrayList 存放着Field
	 **/
	public ArrayList fieldList(String ctrl_peroid,String setname,String spflagid,ArrayList datalist){
		ArrayList retlist=new ArrayList();
		HashMap  map = new HashMap();
		HashMap mp = new HashMap();
		for(int j=0;j<datalist.size();j++)
		{
    		LazyDynaBean bean = (LazyDynaBean)datalist.get(j);
			String realitem = (String)bean.get("realitem");
			String balanceitem = (String)bean.get("balanceitem");
			map.put(realitem.toLowerCase(), realitem);
			mp.put(balanceitem.toLowerCase(),balanceitem);
		}
		//需要区别是否为已作废单位，加入单位名称显示列 zhanghua 2017-4-10
		FieldItem fi=DataDictionary.getFieldItem("B0110");
		Field fieldi=new Field("B0110text",ResourceFactory.getProperty("column.sys.org"));
		fieldi.setLength(100);
		fieldi.setCodesetid("0");
		fieldi.setDatatype(DataType.STRING);
		fieldi.setReadonly(true);
		retlist.add(fieldi);
		
		fi=DataDictionary.getFieldItem("B0110");
		fieldi=new Field("B0110",ResourceFactory.getProperty("column.sys.org"));
		fieldi.setLength(100);
		fieldi.setCodesetid(fi.getCodesetid());
		fieldi.setDatatype(DataType.STRING);
		fieldi.setReadonly(true);
		fieldi.setVisible(false);//隐藏单位代码。显示单位名称显示列。
		retlist.add(fieldi);
		
		ArrayList fieldset = setname!=null&&setname.length()>0?DataDictionary.getFieldList(setname,Constant.USED_FIELD_SET):new ArrayList();
		for(int i=0;i<fieldset.size();i++){
			FieldItem fielditem = (FieldItem)fieldset.get(i);
			
			if("0".equals(this.userView.analyseFieldPriv(fielditem.getItemid())))
				continue;
			
			if(fielditem.getItemid().equalsIgnoreCase(setname+"z1"))
			{
				if(this.fc_flag!=null&&this.fc_flag.length()!=0){
				}else{
					continue;
				}
			}
			if(fielditem.getItemid().equalsIgnoreCase(spflagid)){
					Field stateid=new Field(fielditem.getItemid(),ResourceFactory.getProperty("lable.zp_plan.status"));
					stateid.setLength(10);
					stateid.setCodesetid("23");
					stateid.setDatatype(DataType.STRING);
					stateid.setReadonly(true);
					retlist.add(0,stateid);
				}else{
					StringBuffer format=new StringBuffer();
					String desc = fielditem.getItemdesc();
					Field field=null;
					if("0".equals(ctrl_peroid)&&fielditem.getItemid().equalsIgnoreCase(setname+"z0"))
						field = new Field(fielditem.getItemid(),ResourceFactory.getProperty("hmuster.label.nybs"));
					else{
						if(this.fc_flag!=null&&this.fc_flag.length()!=0){
							if(fielditem.getItemid().equalsIgnoreCase(this.fc_flag)){
								field = new Field(fielditem.getItemid(),"封存状态");
							}else{
								field = new Field(fielditem.getItemid(),desc);
							}
						}else{
							field = new Field(fielditem.getItemid(),desc);
						}
					}
					field.setLength(fielditem.getItemlength());
					field.setCodesetid(fielditem.getCodesetid());
					if("N".equals(fielditem.getItemtype())){
						field.setDecimalDigits(fielditem.getDecimalwidth());
						if(fielditem.getDecimalwidth()>0){
							for(int j=0;j<fielditem.getDecimalwidth();j++){
								format.append("#");	
							}
							field.setFormat("####."+format.toString());
						}else
							field.setFormat("####");
					}
					//String type=fielditem.getItemtype();
					field.setDatatype(getFieldDataType(fielditem.getItemtype(),fielditem.getDecimalwidth()));
					if(fielditem.getItemid().equalsIgnoreCase(setname+"z0"))
					{
						Field stateid=new Field("aaaa","年月标识");
						stateid.setDatatype(getFieldDataType("A",0));
						stateid.setReadonly(true);
						if(!"0".equals(ctrl_peroid))
						{
							stateid.setVisible(false);	
						}
						retlist.add(1,stateid);
					}
					else
					{
						if(map.get(fielditem.getItemid().toLowerCase())!=null||mp.get(fielditem.getItemid().toLowerCase())!=null)
						{
							field.setReadonly(true);
						}else
						{
							if("2".equals(this.userView.analyseFieldPriv(fielditem.getItemid()))) // 写权限
								field.setReadonly(false);
							else
								field.setReadonly(true);
						}
						if(this.fc_flag!=null&&this.fc_flag.length()!=0){
							if(fielditem.getItemid().equalsIgnoreCase(this.fc_flag)){
								field.setAlign("center");
								field.setReadonly(true);
								retlist.add(1,field);
							}else{
								if(fielditem.getItemid().equalsIgnoreCase(setname+"z1")){
									field.setAlign("center");
									field.setReadonly(true);
									
								}
								retlist.add(field);
							}
							
						}else{
							retlist.add(field);
						}
					}
					
				}
			//}
		}
		return retlist;
	}
	
	public int getFieldDataType(String type,int scale)
	{
		int ret=0;
		if("N".equalsIgnoreCase(type))
		{
			if(scale<=0)
				ret=DataType.INT;
			else
		    	ret= DataType.FLOAT;
		}
		else if("D".equalsIgnoreCase(type))
			ret=DataType.DATE;
		else if("A".equalsIgnoreCase(type))
			ret=DataType.STRING;
		else 
			ret=DataType.STRING;
		return ret;
	}
	/**
	 * 将中文公式转换成sql字符串
	 * @param c_expr  中文公式
	 * @param decimalwidth 小数点后面值的宽度
	 * @return int 
	 **/
	public String zhTostr(String c_expr,String varType,UserView uv,String codesetid,String time){
		String  temp = "";
		try{
			String sql = getSql(c_expr,varType,uv,codesetid);
			if(c_expr.trim().length()>0){
				temp = ykql(sql,codesetid,time);//公式的结果
			}
		}catch(Exception ex){
			 ex.printStackTrace();
		}
		return temp ;
	}
	public String getSql(String c_expr,String varType,UserView uv,String codesetid){
		String  temp = "";
		try{
			if(c_expr.trim().length()>0){
				ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
				YksjParser yp = new YksjParser(uv,alUsedFields,YksjParser.forNormal,getColumType(varType),3,"","");
				yp.run(c_expr);
				temp = yp.getSQL();//公式的结果
			}
		}catch(Exception ex){
			 ex.printStackTrace();
		}
		return temp ;
	}
	/**
	 * 获取数据库添加语句
	 * @param  selectsql 插入值
	 * @param fieldsetid 表名
	 * @return String 
	 **/
	public String ykql(String temp,String codesetid,String time){
		StringBuffer  buf = new StringBuffer();
		StringBuffer where = new StringBuffer();
		StringBuffer codeset = new StringBuffer();
		String[] conformula = temp.split(","); 
		HashSet set = new HashSet();
		if(conformula.length>1){
			buf.append("select ");
			buf.append(temp +" from ");

			for(int i=0;i<conformula.length-1;i++){
				int templast = conformula[i].lastIndexOf("(")+1;
				String itemid = conformula[i].substring(templast,conformula[i].length());
				FieldItem fi = DataDictionary.getFieldItem(itemid);
				String fieldsetid = fi.getFieldsetid();
				
				if("a".equals(fieldsetid.substring(0,1).toLowerCase())){
					set.add(("usr"+fieldsetid));
					codeset.append("usr"+fieldsetid+".a0100='"+codesetid+"'");
					if(!"a01".equalsIgnoreCase(fieldsetid)){
						if(isConf(fieldsetid)){
							where.append(getz0time(fieldsetid,time));
						}else{
							where.append(fieldsetid+".i9999=(select max(19999) from ");
							where.append(fieldsetid);
							where.append(" where ");
							where.append(" usr"+fieldsetid+".a0100='"+codesetid+"')");
						}
					}
				}else if("b".equals(fieldsetid.substring(0,1).toLowerCase())){
					set.add((fieldsetid));
					codeset.append(fieldsetid+".b0110='"+codesetid+"'");
					if(!"b01".equalsIgnoreCase(fieldsetid)){
						if(isConf(fieldsetid)){
							where.append(getz0time(fieldsetid,time));
						}else{
							where.append(fieldsetid+".i9999=(select max(19999) from ");
							where.append(fieldsetid);
							where.append(" where ");
							where.append(fieldsetid+".b0110='"+codesetid+"')");
						}
					}
				}else{
					set.add((fieldsetid)); 
					codeset.append(fieldsetid+".e01a1='"+codesetid+"'");
					if(!"k01".equalsIgnoreCase(fieldsetid)){
						if(isConf(fieldsetid)){
							where.append(getz0time(fieldsetid,time));
						}else{
							where.append(fieldsetid+".i9999=(select max(19999) from ");
							where.append(fieldsetid);
							where.append(" where ");
							where.append(fieldsetid+".e01a1='"+codesetid+"')");
						}
					}
				}
				
				if(i+1<conformula.length-1){
					codeset.append(" and ");
				}
			}
			buf.append(set.toString().substring(1,set.toString().length()-1));
			
			if(conformula.length>2){
				if(where.length()>0){
					buf.append(" where "+where.toString()+" and "+codeset);
				}else{
					buf.append(" where "+where.toString()+codeset);
				}
			}else{
				if(where.length()>0){
					buf.append(" where "+where.toString()+" and "+codeset);
				}else{
					buf.append(" where "+codeset);
				}
			}
		}else{
			buf.append(temp);
		}
		return buf.toString() ;
	}
	/**
	 * 判断子集里面是否有年月标识这个指标
	 * @param  fieldsetid 子集id
	 * @return boolean 
	 **/
	public boolean isConf(String fieldsetid){
		boolean  buf = false;
		ArrayList list = DataDictionary.getFieldList(fieldsetid,Constant.USED_FIELD_SET);
		for(int i=0;i<list.size();i++){
			FieldItem fi = (FieldItem)list.get(i);
			if(fi.getItemid().equalsIgnoreCase(fieldsetid+"z0")){
				buf=true;
				break;
			}
		}
		return buf ;
	}
	/**
	 * 得到时间段条件
	 * @param subsetstr  子集名称
	 * @param time    时间
	 * @return
	 */
	public String getz0time(String subsetstr,String time)
	{
		TimeScope ts = new TimeScope();
		String getz0time = ts.gettimeScope(subsetstr+"z0",time+"-01",time+"-31");
		return getz0time;
	}
	/**
	 * 查询公司以下部门汇总数据
	 * @param subsetstr  子集名称
	 * @param time 时间  
	 * @param list  下一层部门数
	 * @return
	 */
	public String getcontent(String subsetstr,String time,ArrayList list){
		StringBuffer retsql = new StringBuffer();
		for(int i=0;i<list.size();i++){
			retsql.append(",(select sum("+list.get(i)+") from "+subsetstr+" a where a.b0110 in ");
			retsql.append(" ( select codeitemid from organization where codeitemid ");
			retsql.append(" like (b.b0110+'%') and (grade=(select grade from organization ");
			retsql.append(" where codeitemid=b.b0110)+1 and (codesetid='UN') or ");
			retsql.append(" grade=(select grade from organization where codeitemid=b.b0110)");
			retsql.append(" and (codesetid='UM' or codesetid='UN')))  and "+getz0time(subsetstr,time));
			retsql.append(") as t"+i  );
		}
		return retsql.toString();
	}
	/**
	 * 查询公司以下部门汇总数据
	 * @param subsetstr  子集名称
	 * @param sum    要汇总的子标项目
	 * @param y  第几个上层部门
	 * @param equalz0time  时间
	 * @return
	 */
	public String getcontentsql(String subsetstr,ArrayList list,String time){
		StringBuffer retsql = new StringBuffer();
		StringBuffer contentsetsb = new StringBuffer();
		for(int i=0;i<list.size();i++){
			contentsetsb.append(","+list.get(i)+"=t.t"+i);
		}
		
		retsql.append(" update "+subsetstr+" set "+contentsetsb.substring(1).toString()+" from ");
		retsql.append("( select b0110 "+getcontent(subsetstr,time,list).toString()+" from ");
		retsql.append(subsetstr+" b where "+getz0time(subsetstr,time));
		retsql.append(")t where "+subsetstr+".b0110=t.b0110 and" );
		retsql.append(getz0time(subsetstr,time) );
		return retsql.toString();
	}
	public ArrayList getField(String temp){
		ArrayList list = new ArrayList();
		String[] conformula = temp.split(","); 
		for(int i=0;i<conformula.length-1;i++){
			int templast = conformula[i].lastIndexOf("(")+1;
			String itemid = conformula[i].substring(templast,conformula[i].length());
			list.add(itemid);
		}
		return list ;
	}
	/**
	 * 获取数据库添加语句
	 * @param  selectsql 插入值
	 * @param fieldsetid 表名
	 * @return String 
	 **/
	public String insertStr(String selectsql,String fieldsetid,String sp_flag){
		StringBuffer  buf = new StringBuffer();
		buf.append("insert into ");
		buf.append(fieldsetid);
		buf.append("(");
		buf.append("b0110,i9999,");
	//	buf.append(viewfim);
		buf.append(sp_flag+","+fieldsetid+"z0,"+fieldsetid+"z1,");
		if(this.fc_flag!=null&&fc_flag.length()!=0){
			buf.append(this.fc_flag);
			buf.append(",");
		}
		buf.append("createtime,modtime,createusername,modusername");
		buf.append(")");
		if(Sql_switcher.searchDbServer() == Constant.ORACEL)
			buf.append(" values ");
		buf.append("(");
		buf.append(selectsql);
		buf.append(")");
		return buf.toString() ;
	}
	
	/**
	 * 获取检查数据库中记录的查询语句
	 * @param uv  用户权限控制
	 * @param fieldsetid 表名
	 * @param viewfim 插入列数,以逗号隔开
	 * @param codeitemid 公司或部门id
	 * @param c_expr 中文公式
	 * @param time 添加时间XXXX-XX
	 * @return int 
	 **/
	public String selectSql(UserView uv,String fieldsetid,String codeitemid,String c_expr,String time,String sp_flag){
		StringBuffer  buf = new StringBuffer();
		time=time+"-01";
		try
		{
    		codeitemid=codeitemid!=null&&codeitemid.trim().length()>0?codeitemid:"";
    		if(codeitemid.length()<1){
    			return "";
    		}
		
    		Date date = new Date();
    	    	buf.append("select b0110,(select max(i9999) from ");
        		buf.append( fieldsetid);
        		buf.append(" where b0110 = '");
         		buf.append( codeitemid);
        		buf.append("'");
         		buf.append(")+1 as i9999,");
        		//buf.append(viewfim);
         		buf.append(" '01' as "+sp_flag+",'");
         		buf.append(time+"' as "+fieldsetid+"z0,'1' as "+fieldsetid+"z1 ,");
         		if(this.fc_flag!=null&&fc_flag.length()!=0){
         			buf.append(" '2' as ");
         			buf.append(this.fc_flag);
         			buf.append(",");
         		}
	        	buf.append("'"+DateUtils.getTimestamp(date)+"' as createtime,'");
	        	buf.append(DateUtils.getTimestamp(date)+"' as modtime,'");
	          	buf.append(uv.getUserName()+"' as createusername,'");
        		buf.append(uv.getUserName()+"' as modusername");
	        	buf.append(" from ");
        		buf.append(fieldsetid);
        		buf.append(" where b0110 = '");
        		buf.append( codeitemid);
	        	buf.append("' and i9999 = (select max(i9999) from ");
	         	buf.append( fieldsetid);
        		buf.append(" where b0110 = '");
    	    	buf.append( codeitemid);
        		buf.append("'");
        		buf.append(")");
          
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return buf.toString() ;
	}
	public String selectSql1(UserView uv,String fieldsetid,String codeitemid,String c_expr,String time,String sp_flag,String cishu){
		StringBuffer  buf = new StringBuffer();
		time=time+"-01";
		try
		{
    		codeitemid=codeitemid!=null&&codeitemid.trim().length()>0?codeitemid:"";
    		if(codeitemid.length()<1){
    			return "";
    		}
		
    		Date date = new Date();
    	    	buf.append("select b0110,(select max(i9999) from ");
        		buf.append( fieldsetid);
        		buf.append(" where b0110 = '");
         		buf.append( codeitemid);
        		buf.append("'");
         		buf.append(")+1 as i9999,");
        		//buf.append(viewfim);
         		buf.append(" '01' as "+sp_flag+",'");
         		buf.append(time+"' as "+fieldsetid+"z0,'");
         		buf.append(cishu);
         		buf.append("' as "+fieldsetid+"z1 ,'");
         		buf.append("2'");
         		buf.append(" as ");
         		buf.append(this.fc_flag);
         		buf.append(",'");
	        	buf.append(DateUtils.getTimestamp(date)+"' as createtime,'");
	        	buf.append(DateUtils.getTimestamp(date)+"' as modtime,'");
	          	buf.append(uv.getUserName()+"' as createusername,'");
        		buf.append(uv.getUserName()+"' as modusername");
	        	buf.append(" from ");
        		buf.append(fieldsetid);
        		buf.append(" where b0110 = '");
        		buf.append( codeitemid);
	        	buf.append("' and i9999 = (select max(i9999) from ");
	         	buf.append( fieldsetid);
        		buf.append(" where b0110 = '");
    	    	buf.append( codeitemid);
        		buf.append("'");
        		buf.append(")");
          
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return buf.toString() ;
	}
	public String getOracleSql(ContentDAO dao,UserView uv,String fieldsetid,String time,String codeitemid,int type,String sp_flag)
	{
		StringBuffer buf = new StringBuffer();
		RecordVo vo = new RecordVo(fieldsetid);
		try
		{
			if(type==0)
	  		{
	      	   String sql  = "select MAX(i9999) as i9999 from "+fieldsetid+" where b0110='"+codeitemid+"'";
              // ContentDAO dao = new ContentDAO(this.conn);      
               RowSet rs = dao.search(sql);
               while(rs.next())
               {
            	   this.current_i9999=rs.getInt("i9999");
               }
               this.current_i9999+=1;
	  		}
			
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        	String dd = format.format(new Date());
        	buf.append("'"+codeitemid+"',"+(this.current_i9999+type)+",");
        	buf.append("'01',to_date('"+time+"-01','YYYY-MM-DD'),1,");
        	if(this.fc_flag!=null&&fc_flag.length()!=0){
        		buf.append("'2',");
        	}
        	buf.append("to_date('"+dd+"','YYYY-MM-DD HH24:MI:SS'),");
        	buf.append("to_date('"+dd+"','YYYY-MM-DD HH24:MI:SS'),");
        	buf.append("'"+uv.getUserName()+"','"+uv.getUserName()+"'");
        	
        /*	vo.setString("b0110",codeitemid);
        	vo.setInt("i9999",i9999);
        	vo.setString(sp_flag,"01");
        	vo.setDate(fieldsetid+"Z0",time+"-01");
        	vo.setInt(fieldsetid+"Z1",1);
        	vo.setDate("createtime", format.format(new Date()));
        	vo.setDate("modtime", format.format(new Date()));
        	vo.setString("createusername",uv.getUserName());
        	vo.setString("modusername",uv.getUserName());*/
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return buf.toString();
	}
	public String getOracleSql1(ContentDAO dao,UserView uv,String fieldsetid,String time,String codeitemid,int type,String sp_flag,String cishu)
	{
		StringBuffer buf = new StringBuffer();
		RecordVo vo = new RecordVo(fieldsetid);
		try
		{
			if(type==0)
	  		{
	      	   String sql  = "select MAX(i9999) as i9999 from "+fieldsetid+" where b0110='"+codeitemid+"'";
              // ContentDAO dao = new ContentDAO(this.conn);      
               RowSet rs = dao.search(sql);
               while(rs.next())
               {
            	   this.current_i9999=rs.getInt("i9999");
               }
               this.current_i9999+=1;
	  		}
			
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        	String dd = format.format(new Date());
        	buf.append("'"+codeitemid+"',"+(this.current_i9999+type)+",");
        	buf.append("'01',to_date('"+time+"-01','YYYY-MM-DD'),");
        	buf.append(cishu);
        	buf.append(",");
        	buf.append("2");
        	buf.append(",");
        	buf.append("to_date('"+dd+"','YYYY-MM-DD HH24:MI:SS'),");
        	buf.append("to_date('"+dd+"','YYYY-MM-DD HH24:MI:SS'),");
        	buf.append("'"+uv.getUserName()+"','"+uv.getUserName()+"'");
		}
        catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return buf.toString();
    }
	/**
	 * 如果数据库没有这个部门的记录测用这个查询语句
	 * @param uv 用户名
	 * @param fieldsetid 表名
	 * @param viewfim 插入列数,以逗号隔开
	 * @param codeitemid 公司或部门id
	 * @param c_expr 中文公式
	 * @param time 添加时间XXXX-XX
	 * @return String 
	 **/
	public String selectSql2(UserView uv,String fieldsetid,String codeitemid,String time,String sp_flag,String codesetid){
		StringBuffer  buf = new StringBuffer();
		Date date = new Date();
        time=time+"-01";
		buf.append("select (select codeitemid from organization b where codeitemid='");
		buf.append(codeitemid);
		buf.append("' and UPPER(codesetid)='"+codesetid.toUpperCase()+"' and a.codeitemid=b.codeitemid) as b0110,");
		buf.append("1 as i9999,");
		//buf.append(viewfim);
		buf.append(" '01' as "+sp_flag+",'");
		buf.append(time+"' as "+fieldsetid+"z0,'1' as "+fieldsetid+"z1 ,");
		if(this.fc_flag!=null&&this.fc_flag.length()!=0){
			buf.append("'2' as");
			buf.append(this.fc_flag);
			buf.append(",");
		}
		buf.append("'"+DateUtils.getTimestamp(date)+"' as createtime,'");
		buf.append(DateUtils.getTimestamp(date)+"' as modtime,'");
		buf.append(uv.getUserName()+"' as createusername,'");
		buf.append(uv.getUserName()+"' as modusername ");
		buf.append(" from organization a ");
		buf.append(" where codeitemid = '"+codeitemid+"' and UPPER(codesetid)='"+codesetid.toUpperCase()+"'");
		return buf.toString() ;
	}
	public String selectSql3(UserView uv,String fieldsetid,String codeitemid,String time,String sp_flag,String codesetid,String cishu){
		StringBuffer  buf = new StringBuffer();
		Date date = new Date();
        time=time+"-01";
		buf.append("select (select codeitemid from organization b where codeitemid='");
		buf.append(codeitemid);
		buf.append("' and UPPER(codesetid)='"+codesetid.toUpperCase()+"' and a.codeitemid=b.codeitemid) as b0110,");
		buf.append("1 as i9999,");
		//buf.append(viewfim);
		buf.append(" '01' as "+sp_flag+",'");
		buf.append(time+"' as "+fieldsetid+"z0,'");
		buf.append(cishu);
		buf.append("' as "+fieldsetid+"z1 ,");
		if(this.fc_flag!=null&&this.fc_flag.length()!=0){
			buf.append("'2' as");
			buf.append(this.fc_flag);
			
		}
		buf.append(",'");
		buf.append(DateUtils.getTimestamp(date)+"' as createtime,'");
		buf.append(DateUtils.getTimestamp(date)+"' as modtime,'");
		buf.append(uv.getUserName()+"' as createusername,'");
		buf.append(uv.getUserName()+"' as modusername ");
		buf.append(" from organization a ");
		buf.append(" where codeitemid = '"+codeitemid+"' and UPPER(codesetid)='"+codesetid.toUpperCase()+"'");
		return buf.toString() ;
	}
	/**
	 * 检测数据库这个部门在这个月的记录是否已被添加
	 * @param fieldsetid 表名
	 * @param codeitemid 单位或部门id
	 * @param time 时间
	 * @return String 
	 **/
	public String selectSql(String fieldsetid,String codeitemid,String time){
		StringBuffer  buf = new StringBuffer();
        String year=time.substring(0,time.indexOf("-"));
        String month = time.substring(time.indexOf("-")+1);
       	buf.append("select b0110 from ");
       	buf.append(fieldsetid);
		buf.append(" where b0110 = '"+codeitemid+"' and "+Sql_switcher.year(fieldsetid+"z0")+"='"+year+"'");
		buf.append(" and "+Sql_switcher.month(fieldsetid+"z0")+"='"+month+"'");	
		return buf.toString() ;
	}
	/**
	 * 设置Field的数据类型
	 * @param type  数据类型
	 * @param decimalwidth 小数点后面值的宽度
	 * @return int 
	 **/
	public int getColumType(String type){
		int temp=IParserConstant.STRVALUE;
		if("A".equals(type)){
			temp=IParserConstant.STRVALUE;
		}else if("D".equals(type)){
			temp=IParserConstant.DATEVALUE;
		}else if("N".equals(type)){
			temp=IParserConstant.FLOAT;
		}else if("L".equals(type)){
			temp=IParserConstant.LOGIC;
		}else{
			temp=IParserConstant.STRVALUE;
		}
		return temp;
	}
	
	/**
	 * 将字符串转换成日期
	 * @param String date 字符串
	 * @return Date
	 **/
	public Date strTodate(String date){
		String[] tempvalue=date.split("-");
		if(tempvalue.length==1){
			date=date+"-01-01";
		}
		if(tempvalue.length==2){
			if(tempvalue[1].length()==1){
				date=tempvalue[0]+"-0"+tempvalue[1]+"-01";
			}else{
				date=date+"-01";
			}
		}
		if(tempvalue.length==3){
			if(tempvalue[1].length()==1){
				tempvalue[1]="0"+tempvalue[1];
			}
			if(tempvalue[2].length()==1){
				tempvalue[2]="0"+tempvalue[2];
			}
			date=tempvalue[0]+"-"+tempvalue[1]+"-"+tempvalue[2];
		}
		
		int day = Integer.parseInt(date.substring(8,10));
		int month = Integer.parseInt(date.substring(5,7));
		int year = Integer.parseInt(date.substring(0,4)); 
		
		return DateUtils.getDate(year,month,day);
	}
	public boolean isHaveChildren(String b0110,String ctrl_type,RecordVo vo,String ctrl_peroid)
	{
		boolean flag=false;
		try
		{
			//是否控制到部门，０控制，１不控制
			String z0=vo.getString("aaaa");
			String year =z0.substring(0,4);
			String month=z0.substring(5);
			StringBuffer sql = new StringBuffer();
			sql.append(" select * from "+vo.getModelName());
			sql.append(" where "+vo.getModelName()+".b0110 in (");
			sql.append("select codeitemid from organization o where o.parentid='");
			sql.append(b0110+"' and o.codeitemid<>'"+b0110+"' ");
			if("1".equals(ctrl_type))
				sql.append(" and upper(o.codesetid)='UN'");
			else
				sql.append(" and (UPPER(o.codesetid)='UN' or UPPER(o.codesetid)='UM')");
			String bosdate=DateStyle.dateformat(new Date(),"yyyy-MM-dd");
		    sql.append(" and "+Sql_switcher.dateValue(bosdate)+" between o.start_date and o.end_date ");
			sql.append(") and ");
			sql.append(Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"'");
			if("0".equalsIgnoreCase(ctrl_peroid))
			    sql.append(" and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+month+"'");
		    if("2".equals(ctrl_peroid))
			{
			    String season = vo.getString("season");
			    String sea=this.getSeasonCondation(Integer.parseInt(season));
			    sql.append(" and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+" in ("+sea+")");
			}
		    if(this.fc_flag!=null&&fc_flag.length()!=0){
		    	sql.append(" and ");
		    	sql.append(vo.getModelName()+"z1=");
		    	sql.append(vo.getString(vo.getModelName()+"z1"));
		    }
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql.toString());
			while(rs.next())
			{
				flag=true;
				break;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
	/**
	 * 当改变控制种类时，从新初始化审批状态指标为起草
	 * @param set
	 * @param spfield
	 * @param year
	 */
	public void initSpField(String set,String spfield,String year)
	{
		try
		{
			StringBuffer sb = new StringBuffer();
			sb.append(" update "+set+" set ");
			sb.append(spfield);
			sb.append(" ='01' where ");
			sb.append(Sql_switcher.year(set+"."+set+"z0"));
			sb.append(" = '"+year+"'");
			if(this.fc_flag!=null&&fc_flag.length()!=0){
				sb.append(" and ");
				sb.append(this.fc_flag);
				sb.append("=");
				sb.append("'2'");
			}
			ContentDAO dao = new ContentDAO(this.conn);
			dao.update(sb.toString());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
  public String getSeasonZH(int month)
  {
	  String ret="";
	  try
	  {
		  switch(month)
		  {
		  case 1:
		      ret=ResourceFactory.getProperty("report.pigionhole.oneQuarter");
		      break;
		  case 4:
			  ret=ResourceFactory.getProperty("report.pigionhole.twoQuarter");
			  break;
		  case 7:
			  ret=ResourceFactory.getProperty("report.pigionhole.threeQuarter");
			  break;
		  case 10:
			  ret=ResourceFactory.getProperty("report.pigionhole.fourQuarter");
			  break;
		  }
	  }
	  catch(Exception e)
	  {
		  e.printStackTrace();
	  }
	  return ret;
  }
  /**
   * 取得月份或者季度的过滤列表
   * @param ctrl_peroid
   * @return
   */
  public ArrayList getFilterList(String ctrl_peroid)
  {
	  ArrayList list = new ArrayList();
	  list.add(new CommonData("0",ResourceFactory.getProperty("label.all")));
	  /**按月的计划，得到月份列表*/
	  if("0".equals(ctrl_peroid))
	  {
		  list.add(new CommonData("1",ResourceFactory.getProperty("date.month.january")));
		  list.add(new CommonData("2",ResourceFactory.getProperty("date.month.february")));
		  list.add(new CommonData("3",ResourceFactory.getProperty("date.month.march")));
		  list.add(new CommonData("4",ResourceFactory.getProperty("date.month.april")));
		  list.add(new CommonData("5",ResourceFactory.getProperty("date.month.may")));
		  list.add(new CommonData("6",ResourceFactory.getProperty("date.month.june")));
		  list.add(new CommonData("7",ResourceFactory.getProperty("date.month.july")));
		  list.add(new CommonData("8",ResourceFactory.getProperty("date.month.auguest")));
		  list.add(new CommonData("9",ResourceFactory.getProperty("date.month.september")));
		  list.add(new CommonData("10",ResourceFactory.getProperty("date.month.october")));
		  list.add(new CommonData("11",ResourceFactory.getProperty("date.month.november")));
		  list.add(new CommonData("12",ResourceFactory.getProperty("date.month.december")));
		 
	  }
	  /**按季度的计划，得到季度列表*/
	  else
	  {
		  list.add(new CommonData("1",ResourceFactory.getProperty("report.pigionhole.oneQuarter")));
		  list.add(new CommonData("2",ResourceFactory.getProperty("report.pigionhole.twoQuarter")));
		  list.add(new CommonData("3",ResourceFactory.getProperty("report.pigionhole.threeQuarter")));
		  list.add(new CommonData("4",ResourceFactory.getProperty("report.pigionhole.fourQuarter")));
	  }
	  return list;
  }
  
  /**
   * 取得审批状态的过滤列表
   * @return
   */
  public ArrayList getSpTypeList()
  {
	  ArrayList list = new ArrayList();
	  list.add(new CommonData("0",ResourceFactory.getProperty("label.all")));
	  
	  list.add(new CommonData("01",ResourceFactory.getProperty("label.hiremanage.status1")));
	  list.add(new CommonData("02",ResourceFactory.getProperty("workdiary.message.apped")));
	  list.add(new CommonData("03",ResourceFactory.getProperty("label.hiremanage.status3")));
	  list.add(new CommonData("07",ResourceFactory.getProperty("button.reject")));
	  
	  return list;
  }
  
  public ArrayList getSalarySetList(String gz_module,UserView userView)
  {
	  ArrayList list = new ArrayList();
		try {
			String sql = "";
			if("0".equalsIgnoreCase(gz_module))
			{
				sql="select salaryid,cname from salarytemplate where (cstate is null or cstate='')  order by seq ";
			}
			if("1".equalsIgnoreCase(gz_module))
			{
				sql = "select salaryid,cname from salarytemplate where cstate='1' order by seq";
			}
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(sql);
			while (rs.next()) {
				if("0".equals(gz_module))
				{
					if(!userView.isHaveResource(IResourceConstant.GZ_SET, rs.getString("salaryid")))
						continue;
				}
				else
				{
					if(!userView.isHaveResource(IResourceConstant.INS_SET, rs.getString("salaryid")))
						continue;
				}
				CommonData cd = new CommonData(rs.getString("salaryid"),rs.getString("cname"));
				list.add(cd);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
  }
  public String getPrivSQL(String column,UserView userView)
  {
	  String sql="";
	  try
	  {
		  StringBuffer buf = new StringBuffer("");
		  if("3".equals(this.getViewUnit()))
		  {
			  String a_code = userView.getUnitIdByBusi("2");
	    	  if(a_code!=null&&a_code.trim().length()>0)
    		  {
    	        // a_code = PubFunc.getTopOrgDept(a_code);
    	         a_code=a_code!=null?a_code:"";
    	         String codevalue="";
    	         String codeall="";
    	         String unitarr[] = a_code.split("`"); 
    	         for(int i=0;i<unitarr.length;i++){
	                 String codeid = unitarr[i];
	                 if(codeid!=null&&codeid.trim().length()>2){
	                	 if("1".equals(this.getCascadingctrl()))
	                	 {
	            	         buf.append(" or "+column+" ='"+codeid.substring(2)+"'");
	                	 }
	                	 else
	                	 {
	                		 buf.append(" or "+column+" like '"+codeid.substring(2)+"%'");
	                	 }
	                 }else if(codeid!=null&& "UN".equalsIgnoreCase(codeid)){
	                    	buf.append(" or 1=1 ");
	                 }
	             }
    		  }
		  }
		  else if("1".equals(this.getViewUnit()))
		  {
	    	  String a_code = userView.getUnit_id();
	    	  if(a_code!=null&&a_code.trim().length()>0)
    		  {
    	        // a_code = PubFunc.getTopOrgDept(a_code);
    	         a_code=a_code!=null?a_code:"";
    	         String codevalue="";
    	         String codeall="";
    	         String unitarr[] = a_code.split("`"); 
    	         for(int i=0;i<unitarr.length;i++){
	                 String codeid = unitarr[i];
	                 if(codeid!=null&&codeid.trim().length()>2){
	                	 if("1".equals(this.getCascadingctrl()))
	                	 {
	            	         buf.append(" or "+column+" ='"+codeid.substring(2)+"'");
	                	 }
	                	 else
	                	 {
	                		 buf.append(" or "+column+" like '"+codeid.substring(2)+"%'");
	                	 }
	                 }else if(codeid!=null&& "UN".equalsIgnoreCase(codeid)){
	                    	buf.append(" or 1=1 ");
	                 }
	             }
    		  }
		  }
		  else if(userView.getManagePrivCode()!=null&&userView.getManagePrivCode().trim().length()>0)
		  {
			  String code=userView.getManagePrivCodeValue();
			  if(code==null|| "".equals(code))
				  buf.append(" or 1=1 ");
			  else
				  buf.append(" or "+column+" like '"+code+"%'");
		  }
		  else
		  {
			  buf.append(" or 1=2 ");
		  }
		  sql=" and ("+buf.toString().substring(3)+")";
			  
	  }
	  catch(Exception e)
	  {
		  e.printStackTrace();
	  }
	  return sql;
  }


	/**
	 * 获取具有管理权限的单位 (size 为0 具有最大权限)
	 * @param userView
	 * @return
	 * @author ZhangHua
	 * @date 17:24 2018/10/23
	 */
	public ArrayList<String> getPrivUnit(UserView userView) {
		ArrayList<String> unitList = new ArrayList<String>();
		try {
			/**=0按管理范围控制=1按操作单位控制=3按模块操作单位控制*/
			String viewUnit = "0";
			if (userView.getUnit_id() != null && userView.getUnit_id().trim().length() > 0 && !"UN".equalsIgnoreCase(userView.getUnit_id())) {
				viewUnit = "1";
			}
			if (userView.getUnitIdByBusi("2") != null && userView.getUnitIdByBusi("2").length() > 0 && !"UN".equalsIgnoreCase(userView.getUnitIdByBusi("2"))) {
				viewUnit = "3";
			}

			String a_code = "";
			if ("3".equals(viewUnit)) {
				a_code = userView.getUnitIdByBusi("2");
			} else if ("1".equals(viewUnit)) {
				a_code = userView.getUnit_id();
			} else if (userView.getManagePrivCode() != null && userView.getManagePrivCode().trim().length() > 0) {
				a_code = userView.getManagePrivCodeValue();
				a_code = "uu" + a_code;
			}
			if (a_code != null && a_code.trim().length() > 0) {
				a_code = a_code != null ? a_code : "";
				String unitarr[] = a_code.split("`");
				for (int i = 0; i < unitarr.length; i++) {
					String codeid = unitarr[i];
					if (codeid != null && codeid.trim().length() > 2) {
						codeid=codeid.substring(2);
						unitList.add(codeid);
					} else if (codeid != null && "UN".equalsIgnoreCase(codeid)) {
						//空 具有最大权限
						return new ArrayList<String>();
					}
				}
				ArrayList<String> tempList = (ArrayList<String>) unitList.clone();

				for (String unit : tempList) {
					Iterator<String> it = unitList.iterator();
					while (it.hasNext()) {
						String id = it.next();
						Boolean topLevel = false;
						CodeItem codeItem = AdminCode.getCode("UM",id );
						if(codeItem==null){
							codeItem = AdminCode.getCode("UN",id);
						}
						if (codeItem.getPcodeitem().equalsIgnoreCase(id)) {
							topLevel = true;
						}
						if (id.startsWith(unit) && (!id.equalsIgnoreCase(unit) || topLevel)) {
							it.remove();
						}

					}
				}


			} else {
				throw GeneralExceptionHandler.Handle(new Exception("没有操作数据权限！"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return unitList;
	}
  /**
   * 获取需要引入的单位 
   * @param flag: 1 包含 0 不包含
   * @return
   */
  public ArrayList getUN(String codeitemid,String flag){
	  ArrayList list=new ArrayList();
	  RowSet rs=null;
	  try
	  {	 	
		  if(flag!=null&& "1".equals(flag)){
			  ContentDAO dao=new ContentDAO(this.conn);
			  String sql="select codeitemid from organization where codeitemid like '"+codeitemid+"%' and codesetid <> '@K'";
			  rs=dao.search(sql);
			  while(rs.next()){
				  list.add(rs.getString("codeitemid"));
			  }
		  }else{
			  list.add(codeitemid);
		  }
  
	  }
	  catch(Exception e)
	  {
		  e.printStackTrace();
	  }finally{		  
			try {
				if(rs!=null){
					rs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		  
	  }
	  return list;
  }
  public HashMap getAllFormulaInfor(String setid)
  {
	  HashMap map = new HashMap();
	  try
	  {
		  StringBuffer tablestr = new StringBuffer("");
		  StringBuffer sortstr = new StringBuffer();
		  tablestr.append("<table width=\"100%\" border=\"0\">");
		  ContentDAO dao = new ContentDAO(this.conn);
		  String sql = "select fid,flag,forname,setid,itemid from HRPFormula where unit_type=3 and UPPER(setid)='"+setid.toUpperCase()+"' order by db_type";
		  RowSet rs = dao.search(sql);
		  while(rs.next())
		  {
			    String itemid = rs.getString("itemid");
				if(!"2".equals(this.userView.analyseFieldPriv(itemid)))
					continue;
				String id = rs.getString("fid")+"_"+itemid;
				String forname = rs.getString("forname");
				int flag = rs.getInt("flag");
				tablestr.append("<tr><td onclick=\"tr_bgcolor('");
				tablestr.append(id);
				tablestr.append("')\">");
				tablestr.append("<input type=\"checkbox\" name=\"");
				tablestr.append(id);
				tablestr.append("\" value=\""+rs.getString("fid")+"\" onclick=\"setCheck(this,'sortstr');\"");
				if(flag==1){
					tablestr.append("checked");
				}
				tablestr.append(">");
				tablestr.append(forname);
				tablestr.append("</td></tr>");
				sortstr.append(id+"::");
				sortstr.append(forname+"::");
				sortstr.append(flag+"`");
		  }
		  tablestr.append("</table>");
		  map.put("1", tablestr.toString());
		  map.put("2", sortstr.toString());
	  }
	  catch(Exception e)
	  {
		  e.printStackTrace();
	  }
	  return map;
  }
  public boolean calculateFormula(String setid,String unit_type,String year,String ctrl_type,String ids)
  {
	  boolean flag=true;
	  try
	  {
		  ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
		  String sql="select * from HRPFormula where unit_type="+unit_type+" and UPPER(setid)='"+setid.toUpperCase()+"' and fid in("+ids+") order by db_type";
		  ContentDAO dao = new ContentDAO(this.conn);
		  HashMap map = this.getCtrl_item();
		  String sp_flag=(String)map.get("sp_flag");
		  String z0=setid+"Z0";
		  RowSet rs = dao.search(sql);
		  while(rs.next())
		  {
			  
			  String itemid = rs.getString("itemid");
			  /**年月标识不让计算*/
			  if(itemid.equalsIgnoreCase(z0))
				  continue;
			  /**已定义成总额控制参数(实发额和剩余额)的指标和定义的审批标识也不让计算*/
			  if(map.get(itemid.toLowerCase())!=null)
				  continue;
			  if(sp_flag.equalsIgnoreCase(itemid))
				  continue;
			  if(!"2".equals(this.userView.analyseFieldPriv(itemid)))
				continue;
			  FieldItem fielditem = DataDictionary.getFieldItem(itemid);
				if(fielditem==null)
					continue;
				String type = fielditem.getItemtype();
				String conTable=rs.getString("setid");
				StringBuffer buf = new StringBuffer();
				try
				{
			    	YksjParser yp = new YksjParser(this.userView,alUsedFields,YksjParser.forSearch,getDataType(type),YksjParser.forUnit ,"Ht","");
			    	String formula=Sql_switcher.readMemo(rs, "formula");
		     		yp.setCon(conn);
			    	yp.run(formula,conTable);
			    	String tablename = yp.getTempTableName();
			    	StringBuffer strsql = new StringBuffer();
			    	if(yp.getSQL()==null||yp.getSQL().trim().length()<1)
				    	continue;
			    	buf.append("update ");
		     		buf.append(conTable);
		     		buf.append(" set ");
	    			buf.append(itemid);
	    			buf.append("=(select ");
	     			buf.append(yp.getSQL());
		    		buf.append(" from ");
		    		buf.append(tablename);
		    		buf.append(" where ");
		    		buf.append(tablename);
		    		buf.append(".B0110=");
		     		buf.append(conTable+".B0110 ");
		     		/**只计算起草和驳回的记录*/
		    		buf.append(" and "+Sql_switcher.year(conTable+"."+setid+"z0")+"="+year+" and ("+conTable+"."+sp_flag+"='01' or "+conTable+"."+sp_flag+"='07' or "+conTable+"."+sp_flag+"='09')");
		    		buf.append(this.getPrivSQL(conTable+".B0110", userView));
		    		if(this.fc_flag!=null&&fc_flag.length()!=0){
		    			buf.append(" and "+conTable+".");
		    			buf.append(this.fc_flag+"='2'");
		    		}
		    		buf.append(") where exists (select null from ");
		     		buf.append(tablename+" where "+tablename+".b0110="+conTable+".b0110");
			    	buf.append(" and "+Sql_switcher.year(conTable+"."+setid+"z0")+"="+year+"");
			    	buf.append(this.getPrivSQL(conTable+".B0110", userView));
			    	buf.append(" and ("+conTable+"."+sp_flag+"='01' or "+conTable+"."+sp_flag+"='07' or "+conTable+"."+sp_flag+"='09')");
			    	if(this.fc_flag!=null&&fc_flag.length()!=0){
		    			buf.append(" and "+conTable+".");
		    			buf.append(this.fc_flag+"='2'");
		    		}
			    	buf.append(")");
		    		dao.update(buf.toString());
		    		/**如果计算指标是总额指标，要更新剩余额*/
		    		if(planitemmap.get(itemid.toUpperCase())!=null)
		    		{
		    			//b/r
		    			String arr=(String)planitemmap.get(itemid.toUpperCase());
		    			String balanceitem=arr.split("/")[0];
		    			String realitem=arr.split("/")[1];
		    			StringBuffer str1=new StringBuffer("");
		    			str1.append(" update "+conTable+" set ");
      		        	str1.append(balanceitem+"=");
      	     	     	str1.append(" ("+Sql_switcher.isnull(itemid,"0")+"-"+Sql_switcher.isnull(realitem,"0")+")");
      	     	        str1.append(" where  "+Sql_switcher.year(z0)+"='"+year+"'");      
			        	str1.append(this.getPrivSQL("B0110", userView));
			        	str1.append(" and ("+sp_flag+"='01' or "+sp_flag+"='07' or "+sp_flag+"='09')");
			        	if(this.fc_flag!=null&&fc_flag.length()!=0){
			    			str1.append(" and "+conTable+".");
			    			str1.append(this.fc_flag+"='2'");
			    		}
          	    		dao.update(str1.toString());
		    		}
				}
				catch(Exception e)
				{
					flag=false;
				}
		  }
		  
	  }
	  catch(Exception e)
	  {
		  flag=false;
		 // e.printStackTrace();
	  }
	  return flag;
  }
  private HashMap planitemmap = new HashMap();
  public HashMap getCtrl_item()
  {
	  HashMap map = new HashMap();
	  try
	  {
		  GzAmountXMLBo bo = new GzAmountXMLBo(this.conn,1);
		  HashMap hm = bo.getValuesMap();
		  String  sp_flag = (String) hm.get("sp_flag");
		  if(!(sp_flag==null|| "".equals(sp_flag)))
		  {
			  map.put("sp_flag", sp_flag);
		  }
		  ArrayList dataList = (ArrayList) hm.get("ctrl_item");
		  for(int i=0;i<dataList.size();i++)
			{
				LazyDynaBean bean = (LazyDynaBean)dataList.get(i);
				String planitem = (String)bean.get("planitem");
				String realitem = (String)bean.get("realitem");
				String balanceitem = (String)bean.get("balanceitem");
				String flag=(String)bean.get("flag");
				if("1".equals(flag))
				{
					map.put(realitem.toLowerCase(),realitem);
					map.put(balanceitem.toLowerCase(), balanceitem);
					planitemmap.put(planitem.toUpperCase(), balanceitem.toUpperCase()+"/"+realitem);
				}
			
			}
	  }
	  catch(Exception e)
	  {
		  e.printStackTrace();
	  }
	  return map;
  }
  private int getDataType(String type)
	{
		int datatype=0;
		switch(type.charAt(0))
		{
		case 'A':  
			datatype=YksjParser.STRVALUE;
			break;
		case 'D':
			datatype=YksjParser.DATEVALUE;
			break;
		case 'N':
			datatype=YksjParser.FLOAT;
			break;
		}
		return datatype;
	}
  public boolean saveSort(Connection conn,String sortstr){
		boolean check=false;
		ContentDAO dao = new ContentDAO(conn);
		String arr[] = sortstr.split("`");
		int n=1;
		ArrayList listvalue = new ArrayList();
		StringBuffer sql = new StringBuffer();
		sql.append("update HRPFormula set db_type=? where fid=?");
		for(int i=0;i<arr.length;i++){
			String[] item_arr = arr[i].split("::");
			if(item_arr.length==3){
				String itemarr[] = item_arr[0].split("_");
				if(itemarr.length==2){
					ArrayList list = new ArrayList();
					list.add(dbtype(n));
					list.add(itemarr[0]);
					listvalue.add(list);
					n++;
				}
			}
		}
		try {
			if(listvalue.size()>0)
				dao.batchUpdate(sql.toString(),listvalue);
			check=true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			check=false;
			e.printStackTrace();
		}
		return check;
	}
  private String dbtype(int n){
		String dbtype="";
		String dblen = n+"";
		for(int i=0;i<5-dblen.length();i++){
			dbtype+="0";
		}
		dbtype+=n;
		return dbtype;
	}
  private ArrayList tableHeaderList = new ArrayList();
  public ArrayList getAdjustAmountList(String code,String setid,String year)
  {
	  ArrayList list = new ArrayList();
	  RowSet rs = null;
	  try
	  {
		  StringBuffer buf = new StringBuffer();
		  StringBuffer select = new StringBuffer();
		  ArrayList fielditemlist = DataDictionary.getFieldList(setid,Constant.USED_FIELD_SET);
		  for(int i=0;i<fielditemlist.size();i++)
		  {
			  FieldItem item=(FieldItem)fielditemlist.get(i);
			  if("1".equals(item.getState())&&!item.getItemid().equalsIgnoreCase(setid+"z0")&&!item.getItemid().equalsIgnoreCase(setid+"z1"))
			  {
		    	  select.append(","+item.getItemid());
		    	  tableHeaderList.add(item);
			  }
		  }
		  buf.append("select B0110,I9999"+select.toString());
		  buf.append(" from "+setid);
		  buf.append(" where ");
		  buf.append(Sql_switcher.year(setid+"z0")+"="+year);
		  buf.append(" and b0110='"+code.substring(2)+"' order by i9999");
		  ContentDAO dao = new ContentDAO(this.conn);
		  SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		  rs=dao.search(buf.toString());
		  while(rs.next())
		  {
			  LazyDynaBean bean = new LazyDynaBean();
			  bean.set("b0110", rs.getString("b0110")+"`"+rs.getString("i9999"));
			  bean.set("i9999", rs.getString("i9999"));
			  for(int i=0;i<fielditemlist.size();i++)
			  {
				  FieldItem item=(FieldItem)fielditemlist.get(i);
				  if("1".equals(item.getState())&&!item.getItemid().equalsIgnoreCase(setid+"z0")&&!item.getItemid().equalsIgnoreCase(setid+"z1"))
				  {
					  if("N".equalsIgnoreCase(item.getItemtype()))
					  {
						  bean.set(item.getItemid().toLowerCase(), PubFunc.round(rs.getString(item.getItemid()), item.getDecimalwidth()));
					  }
					  else if("D".equalsIgnoreCase(item.getItemtype()))
					  {
						  bean.set(item.getItemid().toLowerCase(), rs.getDate(item.getItemid())==null?"":format.format(rs.getDate(item.getItemid())));
					  }
					  else if("A".equalsIgnoreCase(item.getItemtype()))
					  {
						  if(!"0".equals(item.getCodesetid()))
						  {
							  bean.set(item.getItemid().toLowerCase(), rs.getString(item.getItemid())==null?"":AdminCode.getCodeName(item.getCodesetid(), rs.getString(item.getItemid())));
						  }
						  else
						  {
							  bean.set(item.getItemid().toLowerCase(), rs.getString(item.getItemid())==null?"":rs.getString(item.getItemid()));
						  }
					  }
					  else if("M".equalsIgnoreCase(item.getItemtype()))
					  {
						  bean.set(item.getItemid().toLowerCase(), Sql_switcher.readMemo(rs, item.getItemid()));
					  }
					  else
					  {
						  bean.set(item.getItemid().toLowerCase(), rs.getString(item.getItemid())==null?"":rs.getString(item.getItemid()));
					  }
				  }
			  }
			  list.add(bean);
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
	  return list;
  }
public ArrayList getAdjustRecord(String opt,String year,String code,String i9999,String setid)
{
	ArrayList list = new ArrayList();
	RowSet rs = null;
	try
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		LazyDynaBean bean = new LazyDynaBean();
		bean.set("itemid", "b0110");
		bean.set("itemtype", "A");
		bean.set("codesetid", "0");
		bean.set("hidden", "1");
		bean.set("value", code.substring(2));
		bean.set("viewvalue", "");
		bean.set("mustfill", "1");
		bean.set("itemlength","50");
		bean.set("decimal", "0");
		bean.set("itemdesc", "");
		list.add(bean);
		bean = new LazyDynaBean();
		bean.set("itemid", setid+"z0");
		bean.set("itemtype", "D");
		bean.set("codesetid", "0");
		bean.set("hidden", "1");
		bean.set("value",year+"-01-01");
		bean.set("viewvalue", "");
		bean.set("mustfill", "1");
		bean.set("itemlength","50");
		bean.set("decimal", "0");
		bean.set("itemdesc", "");
		list.add(bean);
		int i99=0;
		if("edit".equalsIgnoreCase(opt))
		{
			i99=Integer.parseInt(i9999);
			ContentDAO dao  = new ContentDAO(this.conn);
			rs=dao.search("select * from "+setid+" where b0110='"+code.substring(2)+"' and I9999="+i9999);
			rs.next();
		}
		else
		{
			i99=this.getMaxI9999(setid, code.substring(2));
		}
		ArrayList fielditemlist = DataDictionary.getFieldList(setid,Constant.USED_FIELD_SET);
		for(int i=0;i<fielditemlist.size();i++)
		{
			FieldItem item=(FieldItem)fielditemlist.get(i);
			if("1".equals(item.getState())&&!item.getItemid().equalsIgnoreCase(setid+"z0")&&!item.getItemid().equalsIgnoreCase(setid+"z1"))
			{
				bean = new LazyDynaBean();
				bean.set("itemid", item.getItemid());
				bean.set("itemtype",item.getItemtype());
				bean.set("codesetid", item.getCodesetid());
				bean.set("hidden", "0");
				if("A".equalsIgnoreCase(item.getItemtype()))
				{
                    if("0".equals(item.getCodesetid()))
                    {
                    	bean.set("value",rs==null?"":(rs.getString(item.getItemid())==null?"":rs.getString(item.getItemid())));
        				bean.set("viewvalue", "");
                    }
                    else
                    {
                    	bean.set("value",rs==null?"":(rs.getString(item.getItemid())==null?"":rs.getString(item.getItemid())));
        				bean.set("viewvalue", rs==null?"":AdminCode.getCodeName(item.getCodesetid(), rs.getString(item.getItemid())));
                    }
				}else if("N".equalsIgnoreCase(item.getItemtype()))
				{
					bean.set("viewvalue", "");
					bean.set("value", rs==null?"":(rs.getString(item.getItemid())==null?"":PubFunc.round(rs.getString(item.getItemid()), item.getDecimalwidth())));
				}
				else if("D".equalsIgnoreCase(item.getItemtype()))
				{
					bean.set("viewvalue", "");
					bean.set("value",rs==null?"":(rs.getDate(item.getItemid())==null?"":format.format(rs.getDate(item.getItemid()))));
				}
				else if("M".equalsIgnoreCase(item.getItemtype()))
				{
					bean.set("value",rs==null?"":Sql_switcher.readMemo(rs, item.getItemid()));
    				bean.set("viewvalue", "");
				}else
				{
					bean.set("value",rs==null?"":(rs.getString(item.getItemid())==null?"":rs.getString(item.getItemid())));
    				bean.set("viewvalue", "");
				}
				
				bean.set("mustfill",item.isFillable()?"1":"0");
				bean.set("itemlength",item.getItemlength()+"");
				bean.set("decimal", item.getDecimalwidth()+"");
				bean.set("itemdesc", item.getItemdesc());
				list.add(bean);
			}
	   }
		bean = new LazyDynaBean();
		bean.set("mustfill","1");
		bean.set("itemid", "I9999");
		bean.set("itemtype","N");
		bean.set("codesetid", "0");
		bean.set("hidden", "1");
		bean.set("value",i99+"");
		bean.set("viewvalue", "");
		bean.set("itemdesc", "");
		bean.set("itemlength","50");
		bean.set("decimal", "0");
		list.add(bean);
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
	finally{
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
	return list;
}
public int getMaxI9999(String setid,String codeid)
{
	int retv=0;
	RowSet rs = null;
	try
	{
		ContentDAO dao = new ContentDAO(this.conn);
		rs=dao.search(" select max(i9999) from "+setid+" where b0110='"+codeid+"'");
		while(rs.next())
		{
			retv=rs.getInt(1)+1;
		}
		if(retv==0)
			retv=1;
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
	finally{
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
	return retv;
}
public void saveRecord(String setid,String optType,ArrayList list)
{
	try
	{
		RecordVo vo = new RecordVo(setid);
		for(int i=0;i<list.size();i++)
		{
			LazyDynaBean bean = (LazyDynaBean)list.get(i);
			String itemid=(String)bean.get("itemid");
			String value=(String)bean.get("value");
			String itemtype=(String)bean.get("itemtype");
			String itemlength=(String)bean.get("itemlength");
			String decimal=(String)bean.get("decimal");
			String codesetid=(String)bean.get("codesetid");
			if(value!=null&&value.trim().length()>0)
			{
				if("A".equalsIgnoreCase(itemtype)|| "M".equalsIgnoreCase(itemtype))
				{
					if(codesetid!=null&&!"0".equals(codesetid))
					{
						String viewvalue=(String)bean.get("viewvalue");
						if(viewvalue!=null&&viewvalue.trim().length()==0)
							value="";
					}
					vo.setString(itemid.toLowerCase(), value);
				}
				else if("N".equalsIgnoreCase(itemtype))
				{
					if("0".equals(decimal))
						vo.setInt(itemid.toLowerCase(), Integer.parseInt(value));
					else
					
						vo.setDouble(itemid.toLowerCase(), Double.parseDouble(value));
				}else if("D".equalsIgnoreCase(itemtype))
				{
					Calendar d=Calendar.getInstance();
					 String[] temp=value.split("-");
					 d.set(Calendar.YEAR, Integer.parseInt(temp[0]));
					 d.set(Calendar.MONTH, Integer.parseInt(temp[1])-1);
					 d.set(Calendar.DATE, Integer.parseInt(temp[2]));
					 vo.setDate(itemid.toLowerCase(), d.getTime());
				}
			}
		}
		ContentDAO dao = new ContentDAO(this.conn);
		if("new".equalsIgnoreCase(optType))
			dao.addValueObject(vo);
		else
			dao.updateValueObject(vo);
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
}
public void deleteRecord(String ids,String setid)
{
	try
	{
		ContentDAO dao = new ContentDAO(this.conn);
		String[] arr=ids.split("~");
		String sql = "delete from "+setid+" where b0110=? and i9999=?";
		for(int i=0;i<arr.length;i++)
		{
			ArrayList alist = new ArrayList();
			alist.add(arr[i].split("`")[0]);
			alist.add(arr[i].split("`")[1]);
			dao.delete(sql, alist);
		}
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
}
public ArrayList getChildLink(String codeitemid,String createType,String ctrlAmountField,UserView userView,String createNextType,String createAllNextType)
{
	ArrayList list = new ArrayList();
	RowSet rs = null;
	try
	{
		ContentDAO dao = new ContentDAO(this.conn);
		String privSQL=this.getPrivSQL("codeitemid", userView);
		StringBuffer buf = new StringBuffer("");
		buf.append(" select organization.codeitemid,codeitemdesc,b01.b0110,organization.codesetid ");
		if(ctrlAmountField!=null&&!"".equals(ctrlAmountField))
			buf.append(" ,b01."+ctrlAmountField+" ");
		buf.append(" from organization left join b01 on organization.codeitemid=b01.b0110");
		buf.append(" where ");
		if("1".equals(createType)&& "1".equals(createNextType)&& "1".equals(createAllNextType))//当前、下级、所有下级（不包含部门）
		{
	    	buf.append(" (codeitemid='"+codeitemid+"'");
	    	buf.append(" or parentid='"+codeitemid+"' or (codeitemid like '"+codeitemid+"%' and codesetid<>'UM') )");
		}else if("1".equals(createType)&& "1".equals(createNextType)){//当前、下级
	    	buf.append(" (codeitemid='"+codeitemid+"'");
	    	buf.append(" or parentid='"+codeitemid+"' )");
		}else if("1".equals(createNextType)&& "1".equals(createAllNextType)){//下级、所有下级（不包含部门）
	    	buf.append(" (parentid='"+codeitemid+"' or (codeitemid like '"+codeitemid+"%' and codesetid<>'UM') )");
	    	buf.append(" and codeitemid <> '"+codeitemid+"' ");//不包含当前 wangrd 2015-02-10
		}else if("1".equals(createType)&& "1".equals(createAllNextType)){//当前、所有下级（不包含部门）
	    	buf.append(" (codeitemid='"+codeitemid+"'");
	    	buf.append("  or (codeitemid like '"+codeitemid+"%' and codesetid<>'UM') )");
		}
		else if("1".equals(createType))
		{
			buf.append(" codeitemid='"+codeitemid+"'");
		}else if("1".equals(createNextType))
		{
			buf.append(" codeitemid<>'"+codeitemid+"'");
	    	buf.append(" and parentid='"+codeitemid+"'");
		}else if("1".equals(createAllNextType)){//包含所有下级机构（不包含部门） zhaoxg 2014-4-23
			buf.append(" codeitemid<>'"+codeitemid+"'");
	    	buf.append(" and codeitemid like '"+codeitemid+"%' and codesetid<>'UM'");
		}
		buf.append(" and codesetid<>'@K' ");
		if(privSQL!=null&&!"".equals(privSQL))
			buf.append(privSQL);
		String bosdate=DateStyle.dateformat(new Date(),"yyyy-MM-dd");
		buf.append(" and "+Sql_switcher.dateValue(bosdate)+" between organization.start_date and organization.end_date ");
		rs=dao.search(buf.toString());
		while(rs.next())
		{
			String id=rs.getString("codeitemid");
			String b0110=rs.getString("b0110")==null?"":rs.getString("b0110");
			String ctrl_value="-1";
			if(ctrlAmountField!=null&&!"".equals(ctrlAmountField))
				ctrl_value=rs.getString(ctrlAmountField)==null?"":rs.getString(ctrlAmountField);
			LazyDynaBean bean = new LazyDynaBean();
			bean.set("codeitemid",id);
			bean.set("b0110",b0110);
			bean.set("ctrl_value",ctrl_value);
			bean.set("codesetid",rs.getString("codesetid"));
			bean.set("codeitemdesc",rs.getString("codeitemdesc"));
			list.add(bean);
		}
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
	return list;
}
public String isCanCreateRecord(String itemid,String ctrlAmountField)
{
	String str="0";
	RowSet rs = null;
	try
	{
		if(ctrlAmountField==null|| "".equals(ctrlAmountField.trim()))
		{
			str="1";
			return str;
		}
		StringBuffer sql = new StringBuffer("select "+ctrlAmountField);
		sql.append(" from b01 ");
		sql.append(" where b0110='"+itemid+"'");
		ContentDAO dao= new ContentDAO(this.conn);
		rs=dao.search(sql.toString());
		while(rs.next())
		{
			if(rs.getString(1)!=null&& "1".equals(rs.getString(1)))
				str="1";
		}
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}finally{
		if(rs!=null)
		{
			try
			{
				rs.close();
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	return str;
}

	public String getMonth(String ctrl_peroid,String quterOrmonth)
	{
		String season = "";
		try
		{					
			String strYm = "";
			String appdate = ConstantParamter.getAppdate(this.userView.getUserName());
			if(appdate==null || appdate.trim().length()<=0)			
				strYm = DateUtils.format(new Date(), "yyyy-MM-dd");			
			else
				strYm = appdate.replaceAll("\\.","-");			
			String[] tmp = StringUtils.split(strYm, "-");
			season = tmp[1];
			
			if("2".equals(ctrl_peroid))// by quarter
			{
				int value = Integer.parseInt(quterOrmonth);
				if(!"0".equals(quterOrmonth) && value<=4)
				{					
					if(value==1)
						season = "01";
					else if(value==2)
						season = "04";
					else if(value==3)
				    	season = "07";
					else if(value==4)
				    	season = "10";	
				}
			}else if("0".equals(ctrl_peroid)) // by month
			{
				if(!"0".equals(quterOrmonth))
					season = quterOrmonth;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return season;
	}
	/**	
	 * 新建根据计算公式计算出薪资总额的临时表并进行操作
	 */
	public boolean creatGzColumTable(String setid,String year,String codeitemid,String quterOrmonth,String spType,String ctrl_type,HashMap map)
	{
		boolean flag = true;
		try
		{
			String sp_flag = (String)map.get("sp_flag");	// 审批状态指标	
			String ctrl_peroid = (String)map.get("ctrl_peroid"); // 年月控制标识=1按年，=0按月=2按季度
			if(ctrl_peroid==null || "".equals(ctrl_peroid))
				ctrl_peroid = "0";
			ArrayList dataList = (ArrayList) map.get("ctrl_item");
			HashMap noPlanMap = this.getCtrl_item();
			ContentDAO dao = new ContentDAO(this.conn);
				
			//  新建临时表 用完就drop掉
			String tablename = "t#"+this.userView.getUserName()+"_gz_amount";			
			DbWizard dbWizard = new DbWizard(this.conn);	
			// 此临时表若存在就先drop掉
			if(dbWizard.isExistTable(tablename,false))			
				dbWizard.dropTable(tablename);							
			
			ArrayList existColumnList = new ArrayList();
			Field bobj = new Field(sp_flag);	
			bobj.setDatatype(DataType.STRING);
			bobj.setLength(10);
			bobj.setKeyable(false);
			existColumnList.add(bobj);
			bobj = new Field("B0110");	
			bobj.setDatatype(DataType.STRING);
			bobj.setLength(30);
			bobj.setKeyable(false);
			existColumnList.add(bobj);
			ArrayList fieldList = DataDictionary.getFieldList(setid, Constant.USED_FIELD_SET);
			for(int i=0;i<fieldList.size();i++)
			{
				FieldItem item = (FieldItem)fieldList.get(i);
				String itemid = item.getItemid();
				if(itemid==null || itemid.trim().length()<=0 || itemid.equalsIgnoreCase(sp_flag))
					continue;
				if(itemid.equalsIgnoreCase(setid+"z1"))
    			{
    				if(this.fc_flag!=null && this.fc_flag.length()>0){
    					
    				}else
    					continue;   				
    			}
				existColumnList.add(item.cloneField());
			}
			
			// 获得薪资总额子集表结构
		//	ArrayList existColumnList = getGzTableColumList(setid);	
			/**从临时变量中取得对应指标列表*/			 
			ArrayList midVariableList = getMidVariableList();			
			String columName = "";
			Table table = new Table(tablename);										
			for (int i = 0; i < existColumnList.size(); i++)
		    {	
				Field obj = (Field)existColumnList.get(i);
				table.addField(obj);  				
				columName += (","+obj.getName());
		    }
			for(int i = 0; i < midVariableList.size(); i++)
			{
				FieldItem item = (FieldItem)midVariableList.get(i);
				table.addField(item);				
			}
			dbWizard.createTable(table);	
			
						
			// 向临时表中写入记录
			if(columName!=null && columName.trim().length()>0)
			{
				StringBuffer buf = new StringBuffer();
				buf.append("insert into " + tablename + " (" + columName.substring(1) + ") ");
				
				String sql = "";
				if("1".equals(ctrl_peroid))// by year
				{
					sql = setid!=null && setid.length()>0?this.getColumnSql(setid, year, sp_flag, dataList, codeitemid, ctrl_type, spType, "1"):"";
				}
				else if("2".equals(ctrl_peroid))// by quarter
				{
					String filtersql = this.getFilterSql(setid+"z0", quterOrmonth, ctrl_peroid, sp_flag, spType);
					sql = setid!=null&&setid.length()>0?this.getColumnSqlBySeason(setid, year, sp_flag, dataList, codeitemid, ctrl_type, quterOrmonth, filtersql, "1"):"";
				}
				else // by month
				{
					String filtersql = this.getFilterSql(setid+"z0", quterOrmonth, ctrl_peroid, sp_flag, spType);
				    sql = setid!=null && setid.length()>0?this.sqlStr(setid, year, codeitemid, sp_flag, ctrl_type, ctrl_peroid,1, quterOrmonth, filtersql, "1"):"";
				}				
				buf.append(sql);
													    		
				dao.insert(buf.toString(), new ArrayList());
			}
			
			// 计算临时变量  只计算单位子集
						
			String gzMonth = this.getMonth(ctrl_peroid,quterOrmonth);																	
			YearMonthCount ymc = new YearMonthCount(Integer.parseInt(year),Integer.parseInt(gzMonth),1);
			String strFilter = " select B0110 from B01 ";
			ArrayList allUsedFields = DataDictionary.getAllFieldItemList(
					Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
			for(int i = 0; i < midVariableList.size(); i++)
			{
				FieldItem item = (FieldItem)midVariableList.get(i);
				String fldtype = item.getItemtype();
				String formula = item.getFormula();
				if(formula.indexOf("取自于")!=-1)								
					continue;
								
				YksjParser yp = new YksjParser(this.userView, allUsedFields,
						   YksjParser.forSearch, getDataType(fldtype), YksjParser.forUnit, "Ht", "Usr");				
				yp.setStdTmpTable(tablename);
				yp.setTargetFieldDecimal(item.getDecimalwidth());
				
				yp.run(formula,ymc,item.getItemid(),tablename,dao,strFilter,this.conn,fldtype,item.getItemlength(),1,item.getCodesetid());				
			}
			
			// 根据计算公式计算出薪资总额
			ArrayList updateFormulaList = new ArrayList();
			ArrayList fieldlist = this.getValidateFieldList(setid); // 获得计算公式用指标和临时变量
			ArrayList formulalist = this.getFormulaList(); // 取得薪资总额计算公式列表
			for(int i=0;i<formulalist.size();i++)
			{
	    		LazyDynaBean abean = (LazyDynaBean)formulalist.get(i);
	    		String itemname = (String)abean.get("itemname");  // 计算指标编号
	    		String itemtype = (String)abean.get("itemtype");  // 计算指标类型
				String formula = (String)abean.get("rexpr");  // 计算公式
				String cond = (String)abean.get("cond");  // 计算条件
				
				/**年月标识不让计算*/
				if(itemname.equalsIgnoreCase(setid+"Z0"))
					continue;
				/**已定义成总额控制参数(实发额和剩余额)的指标和定义的审批标识也不让计算*/
				if(noPlanMap.get(itemname.toLowerCase())!=null)
					continue;
				if(sp_flag.equalsIgnoreCase(itemname))
					continue;
				if("0".equals(this.userView.analyseFieldPriv(itemname))) // 0 无权限 ，1 读权限 ，2 写权限
					continue;
								
				this.calcFormula(formula,cond,itemname,itemtype,fieldlist,ymc);
				updateFormulaList.add(abean);
			}
			
			// 把临时表中已计算好的数据同步到薪资总额子集表中
			ArrayList gzAmountList = this.getGzAmountList(tablename,setid,sp_flag); // 获得薪资总额临时表中数据
			this.updateGzAmounTable(setid,sp_flag,ctrl_peroid,gzAmountList,dataList,updateFormulaList); // 更改薪资总额子集记录
			
			// 此临时表若存在就drop掉
			if(dbWizard.isExistTable(tablename,false))			
				dbWizard.dropTable(tablename);
			
		}
		catch(Exception e)
		{
			flag = false;
		//	e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 更改薪资总额子集记录
	 */
	private void updateGzAmounTable(String setid,String sp_flag,String ctrl_peroid,ArrayList gzAmountList,ArrayList dataList,ArrayList updateFormulaList)
	{
		
		try
		{      
			ContentDAO dao = new ContentDAO(this.conn);
			HashMap planMap = new HashMap();
			for(int j=0;j<dataList.size();j++)
			{
	    		LazyDynaBean bean = (LazyDynaBean)dataList.get(j);
	    		String planitem = (String)bean.get("planitem"); // 计划额
				String realitem = (String)bean.get("realitem"); // 实发额
				String balanceitem = (String)bean.get("balanceitem"); // 剩余额
				planMap.put(planitem.toLowerCase(), realitem+"`"+balanceitem);				
			}
			
			for(int i=0;i<gzAmountList.size();i++)
			{
				LazyDynaBean abean = (LazyDynaBean)gzAmountList.get(i);
				String B0110 = (String)abean.get("B0110");
				String z0 = (String)abean.get((setid+"z0").toLowerCase());
				String year = z0.substring(0,4);
				String month = z0.substring(5,7);

				if("1".equals(ctrl_peroid)) // by year
				{
					for(int j=0;j<updateFormulaList.size();j++)
		            {
						LazyDynaBean bean = (LazyDynaBean)updateFormulaList.get(j);
						String item_id = (String)bean.get("itemname");  // 计算指标编号
						String itemtype=(String)bean.get("itemtype");
		          	//	String planitem = (String)bean.get("planitem"); // 计划额
		          	//	String realitem = (String)bean.get("realitem"); // 实发额
		          	//	String balanceitem = (String)bean.get("balanceitem"); // 剩余额
		          					          			
		          		String planValue = (String)abean.get(item_id.toLowerCase());
		          		
		          		
		          		StringBuffer str1 = new StringBuffer();
		          		str1.append(" update "+setid+" set ");
		          		
		          		if("A".equalsIgnoreCase(itemtype)|| "D".equalsIgnoreCase(itemtype))
		          		{
		          				if("D".equalsIgnoreCase(itemtype)&&(planValue==null || planValue.trim().length()<=0))
			          				str1.append(item_id+"=null ");
			          			 else
			          			 {
			          				if(planValue==null)
			          					planValue="";
			          				str1.append(item_id+"='"+planValue+"'");
			          			 }
		          		}
		          		else
		          		{
		          			if(planValue==null || planValue.trim().length()<=0)
			          			continue;
		          			double value = Double.parseDouble(planValue);
		          			double module = 0;
			          		module = (double)(value%12);
				          	value = value-module;				  
				          	str1.append(item_id+"="+((value/12)+module));	
		          		}
			          	str1.append(" where b0110='"+B0110+"' and "+Sql_switcher.year(setid+"."+setid+"z0")+"='"+year+"' ");
			          	str1.append(" and "+Sql_switcher.month(setid+"."+setid+"z0")+"='1' ");
			          	// 只计算起草、驳回和暂停的记录
			          	str1.append(" and ("+sp_flag+"='01' or "+sp_flag+"='07' or "+sp_flag+"='09')");
			          	if(this.fc_flag!=null && this.fc_flag.length()>0)
			          		str1.append(" and "+this.fc_flag+" = '2' ");
			          	dao.update(str1.toString());
			          	str1.setLength(0);
			          	str1.append(" update "+setid+" set ");
			          	
			          	if("A".equalsIgnoreCase(itemtype)|| "D".equalsIgnoreCase(itemtype))
		          		{
			          		if("D".equalsIgnoreCase(itemtype)&&(planValue==null || planValue.trim().length()<=0))
		          				str1.append(item_id+"=null ");
			          		else
			          			str1.append(item_id+"='"+planValue+"'");
		          		}
		          		else
		          		{
		          			double value = Double.parseDouble(planValue);
		          			str1.append(item_id+"="+(value/12));
		          		}
			          	str1.append(" where b0110='"+B0110+"' and "+Sql_switcher.year(setid+"."+setid+"z0")+"='"+year+"' ");
			          	str1.append(" and "+Sql_switcher.month(setid+"."+setid+"z0")+" in(2,3,4,5,6,7,8,9,10,11,12) ");
			          	// 只计算起草、驳回和暂停的记录
			          	str1.append(" and ("+sp_flag+"='01' or "+sp_flag+"='07' or "+sp_flag+"='09')");
			          	if(this.fc_flag!=null && this.fc_flag.length()>0)
			          		str1.append(" and "+this.fc_flag+" = '2' ");
			          	dao.update(str1.toString());
		          			
			          	if(planMap!=null && planMap.get(item_id.toLowerCase())!=null)
			          	{
			          		String reBa = (String)planMap.get(item_id.toLowerCase());
			          		String realitem = reBa.substring(0, reBa.indexOf("`"));
			          		String balanceitem = reBa.substring(reBa.indexOf("`")+1,reBa.length());
			          		str1.setLength(0);
			          		// 修改总额，还要伴随着修改余额,按年控制，12个月份的余额都要修改		          			
				          	for(int n=1;n<=12;n++)
				          	{
				          	    str1.append(" update "+setid+" set ");
				          	    str1.append(balanceitem+"=(");
				          	    str1.append(" select ("+Sql_switcher.isnull(item_id, "0")+"-"+Sql_switcher.isnull(realitem,"0")+") as "+balanceitem+" from "+setid);
				          	    str1.append(" where b0110='"+B0110+"' and "+Sql_switcher.year(setid+"."+setid+"z0")+"='"+year+"'");
				          	    str1.append(" and "+Sql_switcher.month(setid+"."+setid+"z0")+"='"+n+"'");
				          	    // 只计算起草、驳回和暂停的记录
					          	str1.append(" and ("+sp_flag+"='01' or "+sp_flag+"='07' or "+sp_flag+"='09')");
				          	    if(this.fc_flag!=null && this.fc_flag.length()>0)
				          	    	str1.append(" and "+this.fc_flag+" = '2' ");
				          	    str1.append(")");				          	     		
				          	    str1.append(" where b0110='"+B0110+"' and "+Sql_switcher.year(setid+"."+setid+"z0")+"='"+year+"'");
				         	    str1.append(" and "+Sql_switcher.month(setid+"."+setid+"z0")+"='"+n+"'");
				         	    // 只计算起草、驳回和暂停的记录
					          	str1.append(" and ("+sp_flag+"='01' or "+sp_flag+"='07' or "+sp_flag+"='09')");
				         	    if(this.fc_flag!=null && this.fc_flag.length()>0)
				          	    	str1.append(" and "+this.fc_flag+" = '2' ");
				          	    dao.update(str1.toString());
				          	    str1.setLength(0);
				          	}
			          	}
		            }
				}
				else if("2".equals(ctrl_peroid))// by quarter
				{
					String seasoncondation = this.getSeasonCondation(Integer.parseInt(month));
		        	for(int j=0;j<updateFormulaList.size();j++)
		            {
		                LazyDynaBean bean = (LazyDynaBean)updateFormulaList.get(j);
		                String item_id = (String)bean.get("itemname");  // 计算指标编号
		                String itemtype=(String)bean.get("itemtype");
		            //  String planitem = (String)bean.get("planitem"); // 计划额
		          	//	String realitem = (String)bean.get("realitem"); // 实发额
		          	//	String balanceitem = (String)bean.get("balanceitem"); // 剩余额
		          		
		          		String planValue = (String)abean.get(item_id.toLowerCase());
		          		
		          		
		          		StringBuffer str1 = new StringBuffer();
		          		str1.append(" update "+setid+" set ");
		          		double value = 0;
		          		if("A".equalsIgnoreCase(itemtype)|| "D".equalsIgnoreCase(itemtype))
		          		{
		          			if("D".equalsIgnoreCase(itemtype)&&(planValue==null || planValue.trim().length()<=0))
		          				str1.append(item_id+"=null ");
		          			 else
		          			 {
		          				if(planValue==null)
		          					planValue="";
		          				str1.append(item_id+"='"+planValue+"'");
		          			 }
		          		}
		          		else
		          		{
		          			if(planValue==null || planValue.trim().length()<=0)
			          			continue;
		          			
		          			value = Double.parseDouble(planValue);
			          		double module = 0;
			          		module =(double)(value%3);
			          		// 按季度算的时候，不能被三整除的，先（总数-余数）/3+余数，得到第一季度的值
			          		// 剩下两个季度的，用（总数-上面算出的第一季度的数）/2
			          		value = ((value-module)/3)+module;			          				
			          		str1.append(item_id+"="+value);
		          		}	
		          		
		          		str1.append(" where b0110='"+B0110+"' and "+Sql_switcher.year(setid+"."+setid+"z0")+"='"+year+"' ");
		          		str1.append(" and "+Sql_switcher.month(setid+"."+setid+"z0")+"='"+month+"' ");
		          		// 只计算起草、驳回和暂停的记录
			          	str1.append(" and ("+sp_flag+"='01' or "+sp_flag+"='07' or "+sp_flag+"='09')");
		          		if(this.fc_flag!=null && this.fc_flag.length()>0)
			          		str1.append(" and "+this.fc_flag+" = '2' ");
		          		dao.update(str1.toString());
		          		str1.setLength(0);
			          	str1.append(" update "+setid+" set ");
			          	
			          	if("A".equalsIgnoreCase(itemtype)|| "D".equalsIgnoreCase(itemtype))
		          		{
			          		 	if("D".equalsIgnoreCase(itemtype)&&(planValue==null || planValue.trim().length()<=0))
			          				str1.append(item_id+"=null ");
			          			 else
			          			 {
			          				if(planValue==null)
			          					planValue="";
			          				str1.append(item_id+"='"+planValue+"'");
			          			 }
		          		}
		          		else
		          		{
		          			double value_ = Double.parseDouble(planValue);
		          			// 按季度算的时候，不能被三整除的，先（总数-余数）/3+余数，得到第一季度的值
			          		// 剩下两个季度的，用（总数-上面算出的第一季度的数）/2
				          	str1.append(item_id+"="+((value_-value)/2));
		          		}
			          	str1.append(" where b0110='"+B0110+"' and "+Sql_switcher.year(setid+"."+setid+"z0")+"='"+year+"' ");
			          	str1.append(" and "+Sql_switcher.month(setid+"."+setid+"z0")+" in("+seasoncondation+") and "+Sql_switcher.month(setid+"."+setid+"z0")+"<>'"+month+"' ");
			          	// 只计算起草、驳回和暂停的记录
			          	str1.append(" and ("+sp_flag+"='01' or "+sp_flag+"='07' or "+sp_flag+"='09')");
			          	if(this.fc_flag!=null && this.fc_flag.length()>0)
			          		str1.append(" and "+this.fc_flag+" = '2' ");
			          	dao.update(str1.toString());
		          		
			          	if(planMap!=null && planMap.get(item_id.toLowerCase())!=null)
			          	{
			          		String reBa = (String)planMap.get(item_id.toLowerCase());
			          		String realitem = reBa.substring(0, reBa.indexOf("`"));
			          		String balanceitem = reBa.substring(reBa.indexOf("`")+1,reBa.length());
			          		str1.setLength(0);
			          		// 修改总额，还要伴随着修改余额,按季度控制，每个季度中的3个月份的余额都要修改		          			
				          	int x = Integer.parseInt(month);
				          	for(int n=x;n<=x+2;n++)
				          	{
				          	    str1.append(" update "+setid+" set ");
				          		str1.append(balanceitem+"=(");
				          	    str1.append(" select ("+Sql_switcher.isnull(item_id,"0")+"-"+Sql_switcher.isnull(realitem,"0")+") as "+balanceitem+" from "+setid);
				          	    str1.append(" where b0110='"+B0110+"' and "+Sql_switcher.year(setid+"."+setid+"z0")+"='"+year+"'");					          	     		
				          	    str1.append(" and "+Sql_switcher.month(setid+"."+setid+"z0")+"='"+n+"'");
				          	    // 只计算起草、驳回和暂停的记录
					          	str1.append(" and ("+sp_flag+"='01' or "+sp_flag+"='07' or "+sp_flag+"='09')");
				          	    if(this.fc_flag!=null && this.fc_flag.length()>0)
				          	    	str1.append(" and "+this.fc_flag+" = '2' ");
				          	    str1.append(")");				          	     		
				          	    str1.append(" where b0110='"+B0110+"' and "+Sql_switcher.year(setid+"."+setid+"z0")+"='"+year+"'");
				         	    str1.append(" and "+Sql_switcher.month(setid+"."+setid+"z0")+"='"+n+"'");
				         	    // 只计算起草、驳回和暂停的记录
					          	str1.append(" and ("+sp_flag+"='01' or "+sp_flag+"='07' or "+sp_flag+"='09')");
				         	    if(this.fc_flag!=null && this.fc_flag.length()>0)
				          	    	str1.append(" and "+this.fc_flag+" = '2' ");
				          	    dao.update(str1.toString());
				          	    str1.setLength(0);
				          	}
			          	}
		            }
				}
				else // by month
				{
					for(int j=0;j<updateFormulaList.size();j++)
					{
		                LazyDynaBean bean = (LazyDynaBean)updateFormulaList.get(j);
		                String item_id = (String)bean.get("itemname");  // 计算指标编号
		                String itemtype=(String)bean.get("itemtype");
		            //  String planitem = (String)bean.get("planitem"); // 计划额
		          	//	String realitem = (String)bean.get("realitem"); // 实发额
		          	//	String balanceitem = (String)bean.get("balanceitem"); // 剩余额		
		          		String planValue = (String)abean.get(item_id.toLowerCase());
		          		
		          	//	double value = Double.parseDouble(planValue); 20160427  DENGCAN
		          		
		          		StringBuffer str1 = new StringBuffer();		          		
		          		str1.append(" update "+setid+" set ");
		          		if("A".equalsIgnoreCase(itemtype)|| "D".equalsIgnoreCase(itemtype))
		          		{
		          			 if("D".equalsIgnoreCase(itemtype)&&(planValue==null || planValue.trim().length()<=0))
		          				str1.append(item_id+"=null ");
		          			 else
		          			 {
		          				if(planValue==null)
		          					planValue="";
		          				 str1.append(item_id+"='"+planValue+"'");
		          			 }
		          		}
		          		else 
		          		{
		          			if(planValue==null || planValue.trim().length()<=0)
			          			continue;
		          			str1.append(item_id+"="+planValue);
		          		}
		          		str1.append(" where b0110='"+B0110+"' and "+Sql_switcher.year(setid+"."+setid+"z0")+"='"+year+"' ");
		          		str1.append(" and "+Sql_switcher.month(setid+"."+setid+"z0")+"='"+month+"' ");
		          		// 只计算起草、驳回和暂停的记录
			          	str1.append(" and ("+sp_flag+"='01' or "+sp_flag+"='07' or "+sp_flag+"='09')");
		          		if(this.fc_flag!=null && this.fc_flag.length()!=0)
		          			str1.append(" and "+this.fc_flag+" = '2' ");
		          		dao.update(str1.toString());
		          		
		          		if(planMap!=null && planMap.get(item_id.toLowerCase())!=null)
			          	{
			          		String reBa = (String)planMap.get(item_id.toLowerCase());
			          		String realitem = reBa.substring(0, reBa.indexOf("`"));
			          		String balanceitem = reBa.substring(reBa.indexOf("`")+1,reBa.length());
			          		str1.setLength(0);
			          		// 修改总额，还要伴随着修改余额	
			                str1.append(" update "+setid+" set ");
	    		            str1.append(balanceitem+"=(");
	    	            	str1.append(" select ("+Sql_switcher.isnull(item_id,"0")+"-"+Sql_switcher.isnull(realitem,"0")+") as "+balanceitem+" from "+setid);
	    	             	str1.append(" where b0110='"+B0110+"' and "+Sql_switcher.year(setid+"."+setid+"z0")+"='"+year+"'");
	    	            	str1.append(" and "+Sql_switcher.month(setid+"."+setid+"z0")+"='"+month+"'");
	    	            	// 只计算起草、驳回和暂停的记录
				          	str1.append(" and ("+sp_flag+"='01' or "+sp_flag+"='07' or "+sp_flag+"='09')");
	    	            	if(this.fc_flag!=null && this.fc_flag.length()!=0)
	    	            		str1.append(" and "+this.fc_flag+" = '2' ");
		          	     	str1.append(")");	          	     	
	    	             	str1.append(" where b0110='"+B0110+"' and "+Sql_switcher.year(setid+"."+setid+"z0")+"='"+year+"'");
	   	            		str1.append(" and "+Sql_switcher.month(setid+"."+setid+"z0")+"='"+month+"'");	
	   	            		// 只计算起草、驳回和暂停的记录
				          	str1.append(" and ("+sp_flag+"='01' or "+sp_flag+"='07' or "+sp_flag+"='09')");
	   	            		if(this.fc_flag!=null && this.fc_flag.length()!=0)
	   	            			str1.append(" and "+this.fc_flag+" = '2' ");
	    	            	dao.update(str1.toString());
			          	}
					}										
				}
			}			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 获得薪资总额临时表中数据
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getGzAmountList(String tablename,String setid,String sp_flag) throws GeneralException
	{
		ArrayList list = new ArrayList();
		StringBuffer buf = new StringBuffer();
		RowSet rs = null;
		try
		{			
			ContentDAO dao = new ContentDAO(this.conn);
			buf.append("select * from " + tablename + " ");
			// 只计算起草、驳回和暂停的记录
    		buf.append(" where ("+sp_flag+"='01' or "+sp_flag+"='07' or "+sp_flag+"='09') ");
			rs = dao.search(buf.toString());
			while(rs.next())
			{				
				LazyDynaBean abean = new LazyDynaBean();
				abean.set("B0110",isNull(rs.getString("B0110")));
				abean.set(sp_flag.toLowerCase(),isNull(rs.getString(sp_flag)));
				
				ArrayList fieldList = DataDictionary.getFieldList(setid, Constant.USED_FIELD_SET);
				for(int i=0;i<fieldList.size();i++)
				{
					FieldItem item = (FieldItem)fieldList.get(i);
					String itemid = item.getItemid();
					if(itemid==null || itemid.trim().length()<=0 || itemid.equalsIgnoreCase(sp_flag))
						continue;
					if(itemid.equalsIgnoreCase(setid+"z1"))
	    			{
	    				if(this.fc_flag!=null && this.fc_flag.length()>0){
	    					
	    				}else
	    					continue;   				
	    			}
					if ("D".equalsIgnoreCase(item.getItemtype()))
						abean.set(itemid.toLowerCase(),isNull(String.valueOf(rs.getDate(itemid))));
					else
						abean.set(itemid.toLowerCase(),isNull(rs.getString(itemid)));
				}																
				list.add(abean);
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
				if(rs!=null)
					rs.close();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}			
		return list;
	}
	
	/**
	 * @param formula    计算公式
	 * @param cond       计算条件
	 * @param fieldname  计算项目
	 * @param itemtype   计算项目类型
	 */
	private void calcFormula(String formula,String cond,String fieldname,String itemtype,ArrayList fieldlist,YearMonthCount ymc)
	{
		YksjParser yp = null;
		String tablename = "t#"+this.userView.getUserName()+"_gz_amount";
		try
		{
			String strfilter = "";	        
			ContentDAO dao = new ContentDAO(this.conn);
			/**先对计算公式的条件进行分析*/
			if(cond!=null && cond.trim().length()>0)
			{
				yp = new YksjParser( this.userView ,fieldlist,
						 YksjParser.forNormal, YksjParser.LOGIC,YksjParser.forUnit , "Ht", "");
				yp.run_where(cond);
				strfilter = yp.getSQL();
			}			
			
			if(formula!=null && formula.trim().length()>0)
			{
				/**进行公式计算*/
				yp = new YksjParser( this.userView ,fieldlist,
						 YksjParser.forNormal, getDataType(itemtype),YksjParser.forUnit , "Ht", "");
				yp.setYmc(ymc);
				yp.run(formula,this.conn,strfilter,tablename);
						
				StringBuffer strsql = new StringBuffer();
				strsql.append("update " + tablename + " set " + fieldname + " = " + yp.getSQL());
				strsql.append(" where 1=1 ");
				if(strfilter.trim().length()>0)			
					strsql.append(" and " + strfilter);
							
				dao.update(strsql.toString());
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{ 
			yp = null;
		}		
	}
	
	/**
	 * @return FieldItem对象列表
	 * @throws GeneralException
	 */
	private ArrayList getValidateFieldList(String setid)
	{
		ArrayList fieldlist = new ArrayList();		
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			
			StringBuffer buf = new StringBuffer();
			buf.append("select cname,chz,ntype,cvalue,fldlen,flddec,codesetid from ");
			buf.append(" midvariable where nflag=4 and templetid=0 and cstate=-1 ");						
			RowSet rset = dao.search(buf.toString());
			while(rset.next())
			{
				FieldItem item = new FieldItem();
				item.setItemid(rset.getString("cname"));
				item.setFieldsetid("A01");//没有实际含义
				item.setItemdesc(rset.getString("chz"));
				item.setItemlength(rset.getInt("fldlen"));
				item.setDecimalwidth(rset.getInt("flddec"));
				item.setFormula(Sql_switcher.readMemo(rset, "cvalue"));
				switch(rset.getInt("ntype"))
				{
					case 1://
						item.setItemtype("N");
						item.setCodesetid("0");
						break;
					case 2:
						item.setItemtype("A");
						item.setCodesetid("0");
						break;
					case 4:
						item.setItemtype("A");
						item.setCodesetid(rset.getString("codesetid"));
						break;
					case 3:
						item.setItemtype("D");
						item.setCodesetid("0");
						break;
				}
				item.setVarible(1);
				fieldlist.add(item);
			}

			ArrayList tempList = DataDictionary.getFieldList(setid, Constant.USED_FIELD_SET);
			for(int i=0;i<tempList.size();i++)
			{
				FieldItem item = (FieldItem)tempList.get(i);
				item.setVarible(0);
				fieldlist.add(item);				
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return fieldlist;
	}
	
	/**
	 * 获得薪资总额子集表结构
	 */
	public ArrayList getGzTableColumList(String setid)
	{
		ArrayList existColumn = new ArrayList();
		RowSet rowSet = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			rowSet = dao.search("select * from " + setid + " where 1=2");
			ResultSetMetaData mt = rowSet.getMetaData();			
			
			boolean isKey = false; // 是否为主键
			for (int i = 1; i <= mt.getColumnCount(); i++)
			{
			    String columnName = mt.getColumnName(i).toUpperCase();
			    if(columnName==null || columnName.trim().length()<=0)
            		continue;			    
			    if("B0110".equalsIgnoreCase(columnName) || "I9999".equalsIgnoreCase(columnName))
			    	isKey = true;
			    else
			    	isKey = false;
			    
				int columnType = mt.getColumnType(i);	
				int size = mt.getColumnDisplaySize(i);
				int scale = mt.getScale(i);
				
				Field obj = new Field(columnName);
				switch(columnType)
    		 	{
    		 		case Types.DATE:			
    		 		case Types.TIMESTAMP:    		 			    		 				
						obj.setDatatype(DataType.DATE);						
    		 			break;
    		 		case Types.CLOB:
    		 		case Types.LONGVARCHAR:
    		 			obj.setDatatype(DataType.CLOB);
    		 			break;
    		 		case Types.BLOB:
    		 		case Types.LONGVARBINARY:
    		 			obj.setDatatype(DataType.CLOB);
    		 			break;		
    		 		case Types.NUMERIC:
    		 		case Types.INTEGER:
    		 		case Types.SMALLINT:
    		 		case Types.BIGINT:
    		 		case Types.DOUBLE:
    		 		case Types.FLOAT:
    		 			if(scale == 0)
						{
    		 				obj.setDatatype(DataType.INT);
    		 				obj.setLength(size);
						}else{
    		 				obj.setDatatype(DataType.DOUBLE);
							obj.setLength(size);
							obj.setDecimalDigits(scale);
    		 			}
    		 			break;
    		 		default:		
    		 			obj.setDatatype(DataType.STRING);
    		 			obj.setLength(size);
    		 			break;
    		 	}
				if(isKey)
					obj.setNullable(false);
				obj.setKeyable(isKey);
				existColumn.add(obj);
							    
			}
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}					
		return existColumn;
	}
	
	/**
	 * 从临时变量中取得对应指标列表
	 * @return FieldItem对象列表
	 * @throws GeneralException
	 */
	public ArrayList getMidVariableList()throws GeneralException
	{
		ArrayList fieldlist = new ArrayList();
		RowSet rset = null;
		try
		{
			StringBuffer buf = new StringBuffer();
			buf.append("select cname,chz,ntype,cvalue,fldlen,flddec,codesetid from ");
			buf.append(" midvariable where cstate='-1' and nflag=4 and templetid=0 ");
		//	buf.append(" and (cstate is null or cstate='"+this.salaryid+"') ");
			buf.append(" order by sorting");
			ContentDAO dao = new ContentDAO(this.conn);
			rset = dao.search(buf.toString());
			while(rset.next())
			{
				FieldItem item = new FieldItem();
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
		return fieldlist;
	}
	
	/**
	 * 取得薪资总额计算公式列表
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getFormulaList() throws GeneralException
	{
		ArrayList list = new ArrayList();
		StringBuffer buf = new StringBuffer();
		RowSet rs = null;
		try
		{			
			ContentDAO dao = new ContentDAO(this.conn);
			buf.append("select itemid,hzName,itemname,rexpr,cond,standid,itemtype,runflag,useflag ");
			buf.append(" from salaryformula where salaryid = '-1' and useflag = '1' ");								
			buf.append(" order by salaryid,sortid ");				
			rs = dao.search(buf.toString());
			while(rs.next())
			{				
				LazyDynaBean abean = new LazyDynaBean();
				abean.set("itemid",isNull(rs.getString("itemid")));
				abean.set("hzName",isNull(rs.getString("hzName")));
				abean.set("itemname",isNull(rs.getString("itemname")));
				abean.set("rexpr",isNull(rs.getString("rexpr")));
				abean.set("cond",isNull(rs.getString("cond")));
				abean.set("standid",isNull(rs.getString("standid")));				
				abean.set("itemtype",isNull(rs.getString("itemtype")));
				abean.set("runflag",isNull(rs.getString("runflag")));
				abean.set("useflag",isNull(rs.getString("useflag")));
				list.add(abean);
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
				if(rs!=null)
					rs.close();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}			
		return list;
	}
	
	public String isNull(String str)
    {
    	if (str == null || str.trim().length()<=0 || " ".equalsIgnoreCase(str) || "".equalsIgnoreCase(str))
		    return "";
		else
		    return str;
    }
	
	public String getPriv_sql() {
		return priv_sql;
	}
	public void setPriv_sql(String priv_sql) {
		this.priv_sql = priv_sql;
	}
	public String getCascadingctrl() {
		return cascadingctrl;
	}
	public void setCascadingctrl(String cascadingctrl) {
		this.cascadingctrl = cascadingctrl;
	}
	public String getViewUnit() {
		return viewUnit;
	}
	public void setViewUnit(String viewUnit) {
		this.viewUnit = viewUnit;
	}
	public ArrayList getTableHeaderList() {
		return tableHeaderList;
	}
	public void setTableHeaderList(ArrayList tableHeaderList) {
		this.tableHeaderList = tableHeaderList;
	}

}
