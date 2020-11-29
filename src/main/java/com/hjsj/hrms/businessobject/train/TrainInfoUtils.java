package com.hjsj.hrms.businessobject.train;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class TrainInfoUtils {

	/**
	 * 
	 * @param conn
	 * @param code
	 * @param codesetid
	 * @return
	 */
	public ArrayList getPostClassList(Connection conn,String code,String postSetId,String postCloumn)
	{
		if(code==null||code.length()<=0) {
            return null;
        }
		if(postSetId==null||postSetId.length()<=0) {
            return null;
        }
		if(postCloumn==null||postCloumn.length()<=0) {
            return null;
        }
		FieldItem fieldItem=DataDictionary.getFieldItem(postCloumn);
		if(fieldItem==null) {
            return null;
        }
		String codesetid=fieldItem.getCodesetid();
		if(codesetid==null||codesetid.length()<=0) {
            return null;
        }
		ArrayList list=new ArrayList();
		list.add(new CommonData("###","全部课程"));
		
		String sql="select "+postCloumn+" as codeitemid from "+postSetId+" where e01a1='"+code+"'";
		ContentDAO dao=new ContentDAO(conn);
		RowSet rs=null;
		try {
			rs=dao.search(sql);
			while(rs.next())
			{
				if(rs.getString("codeitemid")!=null&&rs.getString("codeitemid").length()>0)
				{
					CodeItem codeitem=AdminCode.getCode(codesetid,rs.getString("codeitemid"));
                    if(codeitem!=null)
                    {
                    	CommonData da=new CommonData();
                    	da.setDataName(codeitem.getCodename());
                    	da.setDataValue(codeitem.getCodeitem());
                    	list.add(da);
                    }   
				}
			}
			if(list.size()<=1) {
                return new ArrayList();
            }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally
		{
			if(rs!=null) {
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

