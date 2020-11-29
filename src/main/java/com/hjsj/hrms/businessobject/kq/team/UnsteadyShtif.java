package com.hjsj.hrms.businessobject.kq.team;

import com.hjsj.hrms.businessobject.kq.options.kq_class.KqClassConstant;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * 不定班次排班业务
 * <p>Title:UnsteadyShtif.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Nov 7, 2006 5:37:47 PM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class UnsteadyShtif implements KqClassArrayConstant,KqClassConstant{

	private Connection conn;
	private UserView userView;
	public UnsteadyShtif()
	{
		
	}
	public UnsteadyShtif(UserView userView,Connection conn)
	{
		this.userView=userView;
		this.conn=conn;
	}
	public ArrayList getUnsteadyShiftVoList(ArrayList classlist)
	{
		StringBuffer class_str=new StringBuffer();
		for(int i=0;i<classlist.size();i++)
		{
			class_str.append("'"+classlist.get(i).toString()+"',");
		}
		class_str.setLength(class_str.length()-1);
		StringBuffer sql=new StringBuffer();
    	sql.append("select * ");
    	sql.append(" from "+this.kq_class_table);
    	sql.append(" where "+this.kq_class_id+"<>'0'");
    	sql.append(" and "+this.kq_class_id+" in("+class_str+")");
    	sql.append(" order by "+this.kq_class_id);
    	RowSet rs=null;
    	ArrayList list=new ArrayList();
    	try
    	{
    		ContentDAO dao=new ContentDAO(this.conn);
    		rs=dao.search(sql.toString());
    		
    		while(rs.next())
    		{
    			RecordVo vo = new RecordVo(this.kq_class_table);
   			    String name=rs.getString(this.kq_class_name);
   			    String class_id=rs.getString(this.kq_class_id);
   			    vo.setString(this.kq_class_id,class_id);
   			    vo.setString(this.kq_class_name,name);	
   			    vo.setString("onduty_1",rs.getString("onduty_1"));
   			    vo.setString("offduty_1",rs.getString("offduty_1"));
   			    vo.setString("onduty_2",rs.getString("onduty_2"));
   			    vo.setString("offduty_2",rs.getString("offduty_2"));
   			    vo.setString("onduty_3",rs.getString("onduty_3"));
   			    vo.setString("offduty_3",rs.getString("offduty_3"));
   			    vo.setString("onduty_4",rs.getString("onduty_4"));
   			    vo.setString("offduty_4",rs.getString("offduty_4"));
   			    list.add(vo);
    		}
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}finally{
	    	  if(rs!=null){
	    		  try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	  }
	      }
    	return list;
	}
	public String getKq_Org_Able_shift(String code,String kind)
	{
		String codesetid="";
		if("2".equals(kind))
		{
			codesetid="UN";
		}else if("1".equals(kind))
		{
			codesetid="UM";
		}else if("0".equals(kind))
		{
			codesetid="QK";
		}
		String sql="select "+this.org_dept_class_id+" from "+this.kq_org_dept_able_shift_table;
		sql=sql+" where "+this.org_dept_id+"='"+code+"' and "+this.org_dept_codesetid+"='"+codesetid+"'";
		return sql;
	}
	/**
	 * 通过条件得到班次的列表
	 * @param code
	 * @param kind
	 * @return
	 */
	public ArrayList getClassList(String code,String kind)
	{
		String sql=getKq_Org_Able_shift(code,kind);
		ArrayList list=new ArrayList();
		RowSet rs = null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			rs=dao.search(sql);
			while(rs.next())
			{
				list.add(rs.getString(this.org_dept_class_id));
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
	    	  if(rs!=null){
	    		  try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	  }
	      }
		return list;
	}
}
