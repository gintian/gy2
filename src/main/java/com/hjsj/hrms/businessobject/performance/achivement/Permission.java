package com.hjsj.hrms.businessobject.performance.achivement;

import com.hjsj.hrms.businessobject.performance.batchGrade.AnalysePlanParameterBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * 
 *<p>Title:</p> 
 *<p>Description:业绩任务书权限划分</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Sep 1, 2010</p> 
 *@author JinChunhai
 *@version 4.0
 */

public class Permission {
	
	private String pointcode_menu="";//指标编号子集
	private String pointname_menu="";//指标名称子集
	private String pointset_menu="";//考核指标子集
	
	private HashMap map=new HashMap();
	
	private Connection con=null;
	private UserView userview=null;	
	
	public Permission(Connection a_con,UserView userView)
	{
		this.con=a_con;
		this.userview=userView;
		init();
		
	}
	ArrayList alist=new ArrayList();
	ArrayList list=new ArrayList();
	Sql_switcher sqlswitcher = new Sql_switcher();
    private void init()		//初始化获取某一个子集中的所有组织机构编码
	{		
    	AnalysePlanParameterBo bo=new AnalysePlanParameterBo(this.con);
    	Hashtable ht=bo.analyseParameterXml();
    	if(ht!=null)
    	{
    		if(ht.get("pointset_menu")!=null && ((String)ht.get("pointset_menu")).trim().length()>0)
    		{
    			String _pointset_menu=(String)ht.get("pointset_menu");
    			if(DataDictionary.getFieldSetVo(_pointset_menu)==null) {
                    return ;
                }
    			this.pointset_menu=_pointset_menu;
    		}
    		else {
                return;
            }
    		if(ht.get("pointcode_menu")!=null  && ((String)ht.get("pointcode_menu")).trim().length()>0)
    		{
    			String _pointcode_menu=(String)ht.get("pointcode_menu");
    			if(DataDictionary.getFieldItem(_pointcode_menu)==null) {
                    return ;
                }
    			this.pointcode_menu=_pointcode_menu;
    		}
    		else {
                return;
            }
    		if(ht.get("pointname_menu")!=null && ((String)ht.get("pointname_menu")).trim().length()>0)
    		{
    			String _pointname_menu=(String)ht.get("pointname_menu");
    			if(DataDictionary.getFieldItem(_pointname_menu)==null) {
                    return ;
                }
    			this.pointname_menu=_pointname_menu;
    		}
    		else {
                return;
            }
    	}
    	
		ContentDAO dao=new ContentDAO(this.con);
		RowSet rowSet;
		RowSet rowSet1;
		try {
			String sql="select  DISTINCT "+sqlswitcher.length("codeitemid")+" length from organization where codesetid <> '@K' ";
			rowSet1 = dao.search(sql);							
			while(rowSet1.next())
			{				
				int length=rowSet1.getInt("length");				
				alist.add(new Integer(length));
			}
			Collections.sort(alist);
			DbWizard dbw = new DbWizard(this.con);
			if(!dbw.isExistTable(pointset_menu,false))//zhaoxg add 这个表如果不存在就不往下走了，以免报错
			{
				return;
			}
			String strSql="select B0110,"+pointcode_menu+","+pointname_menu +" from "+pointset_menu+" order by B0110";
			rowSet = dao.search(strSql);							
			while(rowSet.next())
			{
				String B0110=rowSet.getString("B0110")!=null?rowSet.getString("B0110"):"";				
				String code=rowSet.getString(pointcode_menu)!=null?rowSet.getString(pointcode_menu):"";
				list.add(code);
				map.put(B0110, list);				
				if(rowSet.next()!=false)
				{
					if(!B0110.equalsIgnoreCase(rowSet.getString("B0110")!=null?rowSet.getString("B0110"):"")){
						list=new ArrayList();
					}
				}				
				rowSet.previous();				
			}
			if(rowSet1!=null) {
                rowSet1.close();
            }
			if(rowSet!=null) {
                rowSet.close();
            }
		} catch (SQLException e) {
			e.printStackTrace();
		}														
	}		
	public boolean getPrivPoint(String B0110,String E0122,String pointId)	//判断此组织机构是否被赋予此权限
	{	
		if(map==null || map.size()<=0)
		{
			return true;
		}else{
			String temps="";
			if(E0122!=null && E0122.length()>0) {
                temps=E0122;
            } else if(B0110!=null && B0110.length()>0) {
                temps=B0110;
            }
			
			ArrayList value = (ArrayList) map.get(temps);
			if(value==null || value.size()<=0)
			{
				int z=0;
				int a;
				int x=temps.length();				
				for(int i=0;i<alist.size();i++)
				{
					int y = ((Integer)alist.get(i)).intValue();
					if(x==y)
					{
						a=i;
						while(a>0)
						{
							int temp = ((Integer)alist.get(a-1)).intValue();
							z=y-temp;
							a--;	
							int l=(temps.length()-z);
							String tempss=temps.substring(0,l);
							ArrayList value1 = (ArrayList) map.get(tempss);
							if(value1!=null)
							{
								for(int j=0;j<value1.size();j++)
								{					
									String pointcode=(String)value1.get(j);						       		
						       		if(pointcode.equalsIgnoreCase(pointId)) {
                                        return true;
                                    }
								}
								return false;
							}
						}	
					}
				}
			}						       			 			       			 	
			else{				
				for(int j=0;j<value.size();j++)
				{					
					String pointcode=(String)value.get(j);						       		
					if(pointcode.equalsIgnoreCase(pointId)) {
                        return true;
                    }
				}
				return false;
			}
			return false;
		}		
	}
	
	public String getPointcode_menu() {
		return pointcode_menu;
	}
	public void setPointcode_menu(String pointcode_menu) {
		this.pointcode_menu = pointcode_menu;
	}
	public String getPointname_menu() {
		return pointname_menu;
	}
	public void setPointname_menu(String pointname_menu) {
		this.pointname_menu = pointname_menu;
	}
	public String getPointset_menu() {
		return pointset_menu;
	}
	public void setPointset_menu(String pointset_menu) {
		this.pointset_menu = pointset_menu;
	}
	public HashMap getMap() {
		return map;
	}
	public void setMap(HashMap map) {
		this.map = map;
	}
		
}

