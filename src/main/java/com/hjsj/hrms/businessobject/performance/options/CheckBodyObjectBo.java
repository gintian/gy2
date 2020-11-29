 package com.hjsj.hrms.businessobject.performance.options;

 import com.hrms.frame.dao.ContentDAO;
 import com.hrms.struts.exception.GeneralException;
 import com.hrms.struts.exception.GeneralExceptionHandler;
 import com.hrms.struts.taglib.CommonData;
 import org.apache.commons.beanutils.DynaBean;
 import org.apache.commons.beanutils.LazyDynaBean;

 import javax.sql.RowSet;
 import java.sql.Connection;
 import java.util.ArrayList;
 import java.util.Iterator;

/**
 * <p>Title:CheckBodyObjectBo.java</p>
 * <p>Description:主体类别</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-09-08 11:11:11</p>
 * @author JinChunhai
 * @version 1.0
 */

public class CheckBodyObjectBo 
{

	private String bodyId;
	private String name;
	private String status;
	private String Seq;
	private String level;
	private String bodyType;
	
	private Connection conn=null;
	
	public CheckBodyObjectBo(Connection conn) 
	{
		
		this.conn=conn;
		
	}

	
	public String getBodyId() {
		return bodyId;
	}

	public void setBodyId(String bodyId) {
		this.bodyId = bodyId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSeq() {
		return Seq;
	}

	public void setSeq(String seq) {
		Seq = seq;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getBodyType() {
		return bodyType;
	}

	public void setBodyType(String bodyType) {
		this.bodyType = bodyType;
	}

	public ArrayList searchCheckBodyObjectList(String body_type)throws GeneralException
	{
		ArrayList list=new ArrayList();
		StringBuffer buf=new StringBuffer();
		try
		{
			
			buf.append(" select body_id,name,status,seq,level,body_type from per_mainbodyset ");
			buf.append(" where body_type=");
			buf.append(body_type);
			if("0".equals(body_type)){
				buf.append("  or body_type is null ");	
			}
			buf.append(" order by  seq ");
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rset=dao.search(buf.toString());
			while(rset.next())
			{
				LazyDynaBean lazyvo=new LazyDynaBean();
				lazyvo.set("bodyId", rset.getString("body_id"));
				lazyvo.set("name", rset.getString("name"));
				lazyvo.set("status", rset.getString("status"));
				lazyvo.set("seq", rset.getString("seq"));
				lazyvo.set("level", rset.getString("level"));
				lazyvo.set("bodyType", rset.getString("body_type"));
				list.add(lazyvo);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}
	public ArrayList sortList(Connection conn,String bodyType,String noself,String busitype)
	{
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(conn);
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("select body_id,name from per_mainbodyset where 1=1 ");        
		if ("0".equals(bodyType)) 
		{
			sqlstr.append(" and (body_type=0 or body_type is null) ");
			if("1".equals(noself)) {
                sqlstr.append(" and body_id not in (5,-1) ");
            } else
			{
				if(busitype!=null && busitype.trim().length()>0 && "1".equals(busitype)) {
                    sqlstr.append(" and body_id<>-1 ");
                }
			}
		}else {
            sqlstr.append(" and body_type="+bodyType);
        }
	    sqlstr.append(" order by seq");   
	
		ArrayList dylist = null;
		try 
		{
			dylist = dao.searchDynaList(sqlstr.toString());
			for(Iterator it=dylist.iterator();it.hasNext();)
			{
				DynaBean dynabean=(DynaBean)it.next();
				CommonData dataobj = new CommonData(dynabean.get("body_id").toString(),
						dynabean.get("name").toString());
				list.add(dataobj);
			}
			
		} catch(GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	
}
