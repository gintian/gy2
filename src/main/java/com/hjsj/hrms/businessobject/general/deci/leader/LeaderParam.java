package com.hjsj.hrms.businessobject.general.deci.leader;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class LeaderParam {
    private Connection conn;
    private UserView userView;
	public LeaderParam(){}
	public LeaderParam(Connection conn,UserView userView)
	{
		this.conn=conn;
		this.userView=userView;
	}
	public ArrayList getFieldBySetNameTrans(String tablename,UserView userView)
    {
    	ArrayList list=new ArrayList();
    	CommonData dataobj = new CommonData();
		dataobj.setDataName("请选择");
		dataobj.setDataValue("");
		list.add(dataobj);
		String setname=tablename;		
		if(setname==null||setname.length()<=0) {
            return list;
        }
		ArrayList fielditemlist=DataDictionary.getFieldList(setname,Constant.USED_FIELD_SET);
		
		if(fielditemlist!=null)
		{
			for(int i=0;i<fielditemlist.size();i++)
		    {
		      FieldItem fielditem=(FieldItem)fielditemlist.get(i);
		      if("M".equals(fielditem.getItemtype())) {
                  continue;
              }
		      if("0".equals(userView.analyseFieldPriv(fielditem.getItemid()))) {
                  continue;
              }
		      if(fielditem.getCodesetid()!=null&&!"0".equals(fielditem.getCodesetid())&&!"UM".equals(fielditem.getCodesetid())&&!"UN".equals(fielditem.getCodesetid())&&!"@K".equals(fielditem.getCodesetid()))
		      {
		    	  dataobj = new CommonData();
			      dataobj = new CommonData(fielditem.getItemid(),   fielditem.getItemid().toUpperCase()+ ":"+ fielditem.getItemdesc());
			      list.add(dataobj);		     
			    }
		      }
		     
		}
	    return list;
    }
	public ArrayList codeItemList(String setname,String itemid)
	{
		StringBuffer sqlstr=new StringBuffer();
		ArrayList list=new ArrayList();
		CommonData dataobj = new CommonData();
		dataobj.setDataName("请选择");
		dataobj.setDataValue("");
		list.add(dataobj);
		if(setname==null||setname.length()<=0) {
            return list;
        }
		sqlstr.append("select codesetid,itemdesc from fielditem"); 
		sqlstr.append(" where fieldsetid='"+setname+"'");
		sqlstr.append(" and UPPER(itemid)='"+itemid.toUpperCase()+"'");		
		
		String codesetid="";
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rs=dao.search(sqlstr.toString());
			if(rs.next())
			{
				codesetid=rs.getString("codesetid");
				sqlstr=new StringBuffer();
				sqlstr.append("select codeitemid,codeitemdesc from codeitem");
				sqlstr.append(" where UPPER(codesetid)='"+codesetid.toUpperCase()+"'");
				sqlstr.append(" order by parentid,childid");
				rs=dao.search(sqlstr.toString());				
				String codeitemid="";
				String codeitemdesc="";
				while(rs.next())
				{
					codeitemid=rs.getString("codeitemid");
					codeitemdesc=rs.getString("codeitemdesc");
				    dataobj = new CommonData(codeitemid, codeitemid.toUpperCase()+ ":"+ codeitemdesc);
				    list.add(dataobj);
				}
			}
			
		}catch(Exception e)
		{
		  e.printStackTrace();	
		}
		return list;
	}
	/**
	 * 得到已选择的常用统计
	 * @return
	 */
	public ArrayList getSelectSname(String gcond)
    {
		if(gcond==null||gcond.length()<=0) {
            gcond="";
        }
    	String gconds[]=gcond.split(",");
    	ArrayList list =new ArrayList();
    	if(gconds==null || gconds.length<=0 || StringUtils.isEmpty(gcond)) {
            return list;
        }
    	StringBuffer sql=new StringBuffer();
    	sql.append("select * from sname where ");
    	sql.append(" id in(");
    	for(int i=0;i<gconds.length;i++)
    	{
    		sql.append("'"+gconds[i]+"',");
    	}
    	sql.setLength(sql.length()-1);
    	sql.append(")");
    	sql.append(" order by id");
    	ContentDAO dao=new ContentDAO(this.conn);
    	HashMap codeMaps = new HashMap();
    	
    	try {
    		CommonData dataobj =null;
    		RowSet rs=dao.search(sql.toString());
    		while(rs.next()) {
    			 String id=rs.getString("id");
    			 codeMaps.put(id, rs.getString("name"));
    		
    		}
    		
    		if(codeMaps == null || codeMaps.size() < 1) {
    		    return list;
    		}
    		
    		for(int i=0;i<gconds.length;i++) {
    			dataobj=new CommonData();
				dataobj.setDataName((String) codeMaps.get(gconds[i]));
				dataobj.setDataValue(gconds[i]);
				list.add(dataobj);
    			
        	}
    		
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	return list;
    }
	/**
	 * 得到花名册
	 * @return
	 */
	 public ArrayList getSelectRname(String gcond)
	 {
		    if(gcond==null||gcond.length()<=0) {
                gcond="";
            }
	    	String gconds[]=gcond.split(",");
	    	ArrayList list =new ArrayList();
	    	if(gconds==null||gconds.length<=0) {
                return list;
            }
	    	StringBuffer sql=new StringBuffer();
	    	sql.append("select * from rname where ");
	    	sql.append(" tabid in(");
	    	for(int i=0;i<gconds.length;i++)
	    	{
	    		sql.append("'"+gconds[i]+"',");
	    	}
	    	sql.setLength(sql.length()-1);
	    	sql.append(")");
	    	sql.append(" order by tabid");
	    	ContentDAO dao=new ContentDAO(this.conn);	    	
	    	try
	    	{
	    		CommonData dataobj =null;
	    		RowSet rs=dao.search(sql.toString());
	    		while(rs.next())
	    		{
	    			dataobj=new CommonData();
					dataobj.setDataName(rs.getString("name"));
					dataobj.setDataValue(rs.getString("tabid"));
					list.add(dataobj);
	    		}
	    	}catch(Exception e)
	    	{
	    		e.printStackTrace();
	    	}
	    	return list;
	  }
	 /**
	  * 得到指标
	  * @param field
	  * @return
	  */
	 public ArrayList getFields(String field)
		{
			String[] fields=field.split(",");
			ArrayList list=new ArrayList();
			if(fields==null||fields.length<=0) {
                return list;
            }
			try
			{
				ContentDAO dao=new ContentDAO(this.conn);
				String sql="";
				for(int i=0;i<fields.length;i++)
				{
					
					String itemid=fields[i];
					if(itemid!=null&& "b0110".equals(itemid))
					{
						CommonData data=new CommonData();
						data.setDataName("单位名称");
						data.setDataValue(itemid);
						list.add(data);
						continue;
					}
		    		if(itemid!=null&& "e01a1".equals(itemid))
		    		{
						CommonData data=new CommonData();
						data.setDataName("岗位名称");
						data.setDataValue(itemid);
						list.add(data);
						continue;
					}		    				 
					sql="select itemdesc from fielditem where Upper(itemid)='"+itemid.toUpperCase()+"'";
					RowSet rs=dao.search(sql);
					if(rs.next())
					{
						CommonData data=new CommonData();
						data.setDataName(rs.getString("itemdesc"));
						data.setDataValue(itemid);
						list.add(data);
					}
				}
			}catch(Exception e)
			{
				
			}			
			return list;
		}
		public String getUnitMess(String unitfile_field){
			String[] unitfile = unitfile_field.trim().split(",");
			StringBuffer unitfiles = new StringBuffer();
			ArrayList unitlist = new ArrayList();
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sql = new StringBuffer();
			sql.append("select fieldsetid,fieldsetdesc from fieldSet where fieldsetid in( ");
			for(int i=0;i<unitfile.length;i++){
				sql.append("'"+unitfile[i]+"',");
			}
			sql.setLength(sql.length()-1);
			sql.append(")");
			
			try {
				RowSet rs = dao.search(sql.toString());
				while(rs.next()){
					RecordVo vo = new RecordVo("fieldset");
					vo.setString("fieldsetid",rs.getString("fieldsetid"));
					vo.setString("fieldsetdesc",rs.getString("fieldsetdesc"));
					unitlist.add(vo);
				}
			} catch (SQLException e) {e.printStackTrace();}
			for(int i=0;i<unitfile.length;i++){
				for(int j=0;j<unitlist.size();j++){
					RecordVo db = (RecordVo)unitlist.get(j);
					if(unitfile[i].equalsIgnoreCase(db.getString("fieldsetid").toString())) {
                        unitfiles.append(db.getString("fieldsetdesc").toString());
                    }
				}
				if((i+1)%2==0) {
                    unitfiles.append("<br>");
                } else {
                    unitfiles.append(",");
                }
			}
			unitfiles.setLength(unitfiles.length()-1);
			return unitfiles.toString();
		}
		 public ArrayList getUnitList(String field)
			{
				String[] fields=field.split(",");
				ArrayList list=new ArrayList();
				if(fields==null||fields.length<=0) {
                    return list;
                }
				try
				{
					ContentDAO dao=new ContentDAO(this.conn);
					String sql="";
					for(int i=0;i<fields.length;i++)
					{
						
						String itemid=fields[i];
						sql="select fieldsetdesc from fieldset where fieldsetid='"+itemid+"'";
						RowSet rs=dao.search(sql);
						if(rs.next())
						{
							CommonData data=new CommonData();
							data.setDataName(rs.getString("fieldsetdesc"));
							data.setDataValue(itemid);
							list.add(data);
						}
					}
				}catch(Exception e){e.printStackTrace();}			
				return list;
			}
		 public String getLoadMess(String loadtype_field){
			 String loadtype_mess = "";
			 if("2".equalsIgnoreCase(loadtype_field)) {
                 loadtype_mess="显示到集团";
             } else if("1".equalsIgnoreCase(loadtype_field)) {
                 loadtype_mess="显示到部门";
             } else if("0".equalsIgnoreCase(loadtype_field)) {
                 loadtype_mess="常规显示";
             }
			 return loadtype_mess;
		 }
	 
}
