package com.hjsj.hrms.transaction.performance.options;

import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:CheckBodyObjectListTrans.java</p>
 * <p>Description:定义主体类别</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-09-08 11:11:11</p>
 * @author JinChunhai
 * @version 1.0
 */

public class CheckBodyObjectListTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{		
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");	
		String busitype = (String)hm.get("busitype");	// 业务分类 =0(绩效考核); =1(能力素质)					
		String noself = (String)hm.get("noself");
		hm.remove("busitype");
		hm.remove("noself");
		noself=noself==null?"0":noself;		
		this.getFormHM().put("noself", noself);		
			
		String sql = "select * from per_mainbodyset where body_id=-1";		
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try 
		{
			String levelFild = "level";
			if (Sql_switcher.searchDbServer() == Constant.ORACEL)
			    levelFild = "level_o";
			
			//动态增加团队负责人记录
			this.frowset = dao.search(sql);
		    if(!this.frowset.next())
		    {
		    	RecordVo vo = new RecordVo("per_mainbodyset");
		    	vo.setString("name", "团队负责人");
		    	vo.setInt("status", 1);
		    	vo.setInt("body_type", 0);		
		    	vo.setInt("body_id", -1);
		    	vo.setInt(levelFild, 5);
		    	vo.setInt("seq", this.getSeq());
		    	dao.addValueObject(vo);
		    }
		    
			String bodyType = (String) this.getFormHM().get("bodyType");	
			ArrayList setlist = this.searchCheckBodyObjectList(bodyType,noself,busitype);
			if (Sql_switcher.searchDbServer() == Constant.ORACEL)
			    this.getFormHM().put("dbType", "oracle");
			else
			    this.getFormHM().put("dbType", "sqlserver");
			this.getFormHM().put("setlist", setlist);
			this.getFormHM().put("bodyType", bodyType);
			this.getFormHM().put("busitype", busitype);
			
		} catch (Exception ex) 
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		
		String returnflag=(String)hm.get("returnflag");
		this.getFormHM().put("returnflag",returnflag);
	}

	public ArrayList searchCheckBodyObjectList(String body_type,String noself,String busitype) throws GeneralException 
	{
		ArrayList list = new ArrayList();
		StringBuffer buf = new StringBuffer();
		buf.append(" select * from per_mainbodyset where 1=1 ");
		if ("0".equals(body_type)) 
		{
			buf.append(" and (body_type=0 or body_type is null) ");
			if("1".equals(noself))
				buf.append(" and body_id not in (5,-1) ");
			else
			{
				if(busitype!=null && busitype.trim().length()>0 && "1".equals(busitype))
					buf.append(" and body_id<>-1 ");				
			}
		}else
			buf.append(" and body_type="+body_type);
		buf.append(" order by seq ");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try 
		{
			this.frowset = dao.search(buf.toString());
			while (this.frowset.next()) 
			{
				RecordVo vo = new RecordVo("per_mainbodyset");
				vo.setString("body_id", this.frowset.getString("body_id"));
				vo.setString("name", this.frowset.getString("name"));
				vo.setString("seq", this.frowset.getString("seq"));
				vo.setString("status", this.frowset.getString("status"));
				vo.setString("body_type", this.frowset.getString("body_type"));
				String level = "";
				if (Sql_switcher.searchDbServer() == Constant.ORACEL)
				{
				    level=this.frowset.getString("level_o")==null?"":this.frowset.getString("level_o");
				    vo.setString("level_o", "".equals(level)?"6":level);
				}				    
				else
				{
				    level=this.frowset.getString("level")==null?"6":this.frowset.getString("level");
				    vo.setString("level", "".equals(level)?"6":level);
				}		
				String  object_type = "";
				if(this.frowset.getInt("object_type")==2)
					object_type="人员";
				else if(this.frowset.getInt("object_type")==1)
					object_type="团队";				
				vo.setString("object_type", object_type);
				if(!"1".equals(noself))
				{
					String cond = Sql_switcher.readMemo(this.frowset, "cond");
					cond = cond==null?"":cond;
					String cexpr = Sql_switcher.readMemo(this.frowset, "cexpr");
					cexpr = cexpr==null?"":cexpr;
					vo.setString("cond", SafeCode.encode(cond));
					vo.setString("cexpr", cexpr);
					String  scope = this.frowset.getString("scope");
					if(scope!=null){
					if("1".equals(scope)){
						scope ="单位"; 
					}else if ("2".equals(scope)){
						scope ="上级部门"; 
					}else if ("3".equals(scope)){
						scope ="本部"; 
					}
					else if ("0".equals(scope)|| "-1".equals(scope)){
						scope =""; 
					}
					}else{
						scope="";
					}
					vo.setString("scope", scope);
				}
				list.add(vo);
			}

		} catch (Exception e) 
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}
	
	public synchronized int getSeq() throws GeneralException
	{
		int num = 0; // 序号默认为0
		String sql = "select max(seq) as num from per_mainbodyset";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
		    this.frowset = dao.search(sql.toString());
		    if (this.frowset.next())
		    {
		    	num = this.frowset.getInt("num");
		    }
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
		return num + 1;
	}
	
}
