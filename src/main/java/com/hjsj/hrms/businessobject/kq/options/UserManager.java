package com.hjsj.hrms.businessobject.kq.options;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

public class UserManager {
	/**
	 * 得到字典表中考勤方式的代码项
	 * @param conn
	 * @return
	 */
	private UserView userView;
	private Connection conn;
	public UserManager()
	{
		
	}
	public UserManager(UserView userView,Connection conn)
	{
		this.userView=userView;
		this.conn=conn;
	}
	public ArrayList getKqTypeList(Connection conn)
	{
		ArrayList list=new ArrayList();
    	String sql="SELECT codeitemid, codeitemdesc  FROM codeitem where codesetid ='29'";
        ContentDAO dao=new ContentDAO(conn);
        RowSet rs=null;
        try
        {
        	rs=dao.search(sql);
        	CommonData datavo=new CommonData("","");
        	list.add(datavo);
        	while(rs.next())
        	{
        		datavo=new CommonData(rs.getString("codeitemid"),rs.getString("codeitemdesc"));
        		list.add(datavo);
        	}
        }catch(Exception e)
        {
        	e.printStackTrace();
        }finally
		{
			KqUtilsClass.closeDBResource(rs);
		}
        return list;
	}
	/**
	 * 求显示的字段列表
	 * @param type
	 * @param fieldlist
	 * @return
	 */
	public String getDisplayColumns(ArrayList fieldlist,String kq_type,String kq_cardno,String kq_gno)
	{
		StringBuffer columns=new StringBuffer();		
	    //columns.append("nbase,b0110,e0122,e01a1,a0100,a0101,");	
	    if(kq_cardno!=null&&kq_cardno.length()>0)
		{
	    	columns.append(kq_cardno+",");
		}
		if(kq_gno!=null&&kq_gno.length()>0)
		{
			columns.append(kq_gno+",");
		}

		for(int i=0;i<fieldlist.size();i++)
		{
			FieldItem item=(FieldItem)fieldlist.get(i);			
			//if(lockclient.getModuleCount(Integer.parseInt(name))==0)
			//	continue;			
			columns.append(item.getItemid());
			columns.append(",");
		}
		columns.setLength(columns.length()-1);	
		return columns.toString();
	}
	/**
	 * 
	 * 求operuser表对应字段列表	 
	 * @param kq_type
	 * @param kq_cardno
	 * @param kq_gno
	 * @return
	 */
	public ArrayList getFieldList(String kq_type,String kq_cardno,String kq_gno)
	{
		ArrayList list=new ArrayList();
		KqParameter para = new KqParameter();
		boolean isPost="1".equalsIgnoreCase(para.getKq_orgView_post())?false:true;
		FieldItem item=null;//new FieldItem();
		HashMap hm=new HashMap();
		hm.put("1",ResourceFactory.getProperty("kq.strut.cno"));
		hm.put("2",ResourceFactory.getProperty("kq.strut.gno"));
		hm.put("3",ResourceFactory.getProperty("kq.strut.expr"));
		/*hm.put("3",ResourceFactory.getProperty("kq.param.kqtype.hand"));//手工考勤
		hm.put("4",ResourceFactory.getProperty("kq.param.kqtype.machine"));//机器考勤
		hm.put("5",ResourceFactory.getProperty("kq.param.kqtype.no"));//不参加考勤
*/		/**业务平台用户*/
//		if(type==0)
//		{
//			item.setItemid("username");
//			item.setItemdesc("username");
//			item.setItemtype("A");
//			item.setCodesetid("0");	
//			list.add(item);
//		}		
		item=new FieldItem();
		item.setItemid("nbase");			
		item.setItemdesc("人员库");
		item.setItemtype("A");				
		item.setCodesetid("@@");
		item.setVisible(true);
		list.add(item);
		item=new FieldItem();
		item.setItemid("b0110");			
		item.setItemdesc(ResourceFactory.getProperty("b0110.label"));
		item.setItemtype("A");				
		item.setCodesetid("UN");
		item.setVisible(true);
		list.add(item);
		item=new FieldItem();
		item.setItemid("e0122");			
		item.setItemdesc(ResourceFactory.getProperty("e0122.label"));
		item.setItemtype("A");				
		item.setCodesetid("UM");
		item.setVisible(true);
		list.add(item);
		item=new FieldItem();
		item.setItemid("e01a1");			
		item.setItemdesc(ResourceFactory.getProperty("e01a1.label"));
		item.setItemtype("A");				
		item.setCodesetid("@K");
		item.setVisible(isPost);
		list.add(item);
		item=new FieldItem();
		item.setItemid("a0101");			
		item.setItemdesc("姓名");
		item.setItemtype("A");				
		item.setCodesetid("0");
		item.setVisible(true);
		list.add(item);
		item=new FieldItem();
		item.setItemid("a0100");			
		item.setItemdesc("人员编号");
		item.setItemtype("A");				
		item.setCodesetid("0");
		item.setVisible(false);
		list.add(item);		
		 
		ArrayList fieldlist = this.userView.getPrivFieldList("A01",	Constant.USED_FIELD_SET);
		RowSet rs=null;
		try
		{
		    /******参考指标*******/
		    String content= KqParam.getInstance().getMainSetFields(conn, userView);
			
			for(int i=0;i<fieldlist.size();i++)
			{
				FieldItem fielditem=(FieldItem)fieldlist.get(i);
				if(fielditem.getItemid().toLowerCase().equalsIgnoreCase(kq_type.toLowerCase())) {
                    continue;
                }
				if(fielditem.getItemid().toLowerCase().equalsIgnoreCase(kq_gno.toLowerCase())) {
                    continue;
                }
				if(fielditem.getItemid().toLowerCase().equalsIgnoreCase(kq_cardno.toLowerCase())) {
                    continue;
                }
				if(content.toLowerCase().indexOf(fielditem.getItemid().toLowerCase())!=-1)
				{
					if(fielditem.getPriv_status()>=1)
					{
						fielditem.setVisible(true);
						list.add(fielditem);
					}
				}
			}
			for(int i=1;i<4;i++)
			{
				if(i==1&&kq_cardno!=null&&kq_cardno.length()>0)
				{
					item=new FieldItem();
					item.setItemid("t"+(i));			
					item.setItemdesc((String)hm.get(String.valueOf(i)));
					item.setItemtype("A");				
					item.setCodesetid("0");
					item.setVisible(true);
					list.add(item);
					continue;
				}else if(i==2&&kq_gno!=null&&kq_gno.length()>0)
				{
					item=new FieldItem();
					item.setItemid("t"+(i));			
					item.setItemdesc((String)hm.get(String.valueOf(i)));
					item.setItemtype("A");				
					item.setCodesetid("0");
					item.setVisible(true);
					list.add(item);
					continue;
				}else if(i>2&&kq_type!=null&&kq_type.length()>0)
				{
					item=new FieldItem();
					item.setItemid("t"+(i));			
					item.setItemdesc((String)hm.get(String.valueOf(i)));
					item.setItemtype("A");				
					item.setCodesetid("29");//考勤方式 代码类29
					item.setVisible(true);
					list.add(item);
					continue;
				}			
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			KqUtilsClass.closeDBResource(rs);
		}
		return list;
	}
}
