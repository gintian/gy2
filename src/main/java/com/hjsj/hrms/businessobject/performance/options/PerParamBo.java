 package com.hjsj.hrms.businessobject.performance.options;

 import com.hrms.frame.dao.ContentDAO;
 import com.hrms.frame.dao.RecordVo;
 import com.hrms.struts.exception.GeneralException;
 import com.hrms.struts.exception.GeneralExceptionHandler;
 import org.apache.commons.beanutils.LazyDynaBean;

 import javax.sql.RowSet;
 import java.sql.Connection;
 import java.util.ArrayList;


public class PerParamBo {

	private String id;
	private String kind;
	private String content;
	private String username;
	private String paramName;
	
	private Connection conn=null;
	
	public PerParamBo(Connection conn) {
		this.conn=conn;
	}



	public String getId() {
		return id;
	}



	public void setId(String id) {
		this.id = id;
	}



	public String getKind() {
		return kind;
	}



	public void setKind(String kind) {
		this.kind = kind;
	}



	public String getContent() {
		return content;
	}



	public void setContent(String content) {
		this.content = content;
	}



	public String getUsername() {
		return username;
	}



	public void setUsername(String username) {
		this.username = username;
	}



	public String getParamName() {
		return paramName;
	}



	public void setParamName(String paramName) {
		this.paramName = paramName;
	}



	public ArrayList searchPerParamList()throws GeneralException
	{
		ArrayList list=new ArrayList();
		StringBuffer buf=new StringBuffer();
		try
		{
			
			buf.append(" select id,kind,content,username,param_name from per_param ");
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rset=dao.search(buf.toString());
			while(rset.next())
			{
				LazyDynaBean lazyvo=new LazyDynaBean();
				lazyvo.set("id", rset.getString("id"));
				lazyvo.set("kind", rset.getString("kind"));
				lazyvo.set("content", rset.getString("content"));
				lazyvo.set("username", rset.getString("username"));
				lazyvo.set("paramName", rset.getString("param_name"));
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
	
	public RecordVo getPlanVo(String id)
	{
		RecordVo vo=new RecordVo("per_param");
		try
		{
			vo.setInt("id",Integer.parseInt(id));
			ContentDAO dao = new ContentDAO(this.conn);
			vo=dao.findByPrimaryKey(vo);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return vo;
	}
	
}
