package com.hjsj.hrms.transaction.stat;

import com.hjsj.hrms.businessobject.sys.logonuser.UserObjectBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SaveStaticSnameTrans  extends IBusiness{
	
	  public void execute() throws GeneralException 
	  {
		  String statid=(String)this.getFormHM().get("statid");		 
		  String opflag=(String)this.getFormHM().get("opflag");
		  String infor_Flag=(String)this.getFormHM().get("infor_Flag");
		  if(opflag==null||opflag.length()<=0)
			  opflag="new";
		  String stat_name=(String)this.getFormHM().get("stat_name");
		  String find=(String)this.getFormHM().get("find");
		  if(find==null||find.length()<=0)
			  find="0";
		  String categories = (String)this.getFormHM().get("categories");
		  categories = com.hrms.frame.codec.SafeCode.decode(categories);
		  categories = categories.trim();
		  String viewtypeValue = (String)this.getFormHM().get("viewtypeValue");
		  viewtypeValue = com.hrms.frame.codec.SafeCode.decode(viewtypeValue);
		  viewtypeValue = viewtypeValue.trim();
		  //常用统计新增存储图标功能 wangbs 20190327
		  String photo = (String)this.getFormHM().get("photo");
		  String org_filter = (String)this.getFormHM().get("org_filter");//组织机构过滤参数
		  if("edit".equals(opflag))
		  {
			  StringBuffer sql=new StringBuffer();
			  ArrayList paralist=new ArrayList();
			  RowSet rs = null;
			  ContentDAO dao=new ContentDAO(this.getFrameconn());
			  String grouptype = (String)this.getFormHM().get("type");//修改 统计条件分类 参数     
			  String old_categories = (String)this.getFormHM().get("old_categories");//原 统计分类名称
			  old_categories = com.hrms.frame.codec.SafeCode.decode(old_categories);
			  old_categories = old_categories.trim();
			  if(grouptype != null && "categories".equalsIgnoreCase(grouptype)){//修改统计分类名称   wangb 20190704
				 try {
					sql.append("update sname set categories=? where categories=?");
					paralist.add(categories);
					paralist.add(old_categories);
					int i = dao.update(sql.toString(), paralist);
					if(i > 0 )
						this.getFormHM().put("opflag", "true");
					else
						this.getFormHM().put("opflag", "false");
				  } catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					this.getFormHM().put("opflag", "false");
				  }finally{
					PubFunc.closeDbObj(rs);
				  }
				 this.getFormHM().put("text", categories);
				  return;
			  }
			  
			  if(statid==null||statid.length()<=0)
				  throw GeneralExceptionHandler.Handle(new GeneralException("该统计项不存在！"));
			  paralist.add(stat_name);
			  paralist.add(find);
			  paralist.add(categories);
			  paralist.add(viewtypeValue);
			  paralist.add(photo);
			  paralist.add(Integer.parseInt(org_filter));
			  paralist.add(new Integer(statid)); 
			  sql.append("update sname set name=?,flag=?,categories=?,viewtype=?,photo=?,org_filter=? where id=?");
 
			  try {
				dao.update(sql.toString(),paralist);
				String uid=statid;
				this.getFormHM().put("uid", uid);
				this.getFormHM().put("text", stat_name);
				this.getFormHM().put("action", "");
				this.getFormHM().put("xml", "");
				this.getFormHM().put("opflag", "true");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				this.getFormHM().put("opflag", "false");
			}
		  }else
		  {
			    ArrayList paralist=new ArrayList();	
			    StringBuffer sql=new StringBuffer();
			    String id=getMaxId();
				sql.append("insert into sname(Id,Name,Flag,Type,InfoKind,categories,viewtype,photo,org_filter)values(?,?,?,?,?,?,?,?,?)");
				paralist.add(new Integer(id));
				paralist.add(stat_name);
				paralist.add(find);
				paralist.add("1");
				paralist.add(new Integer(infor_Flag));
				paralist.add(categories);
				paralist.add(viewtypeValue);
				paralist.add(photo);
				paralist.add(Integer.parseInt(org_filter));
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				try
				{
					dao.insert(sql.toString(),paralist);
					UserObjectBo user_bo = new UserObjectBo(this.getFrameconn());
					user_bo.saveResource(id, this.userView,IResourceConstant.STATICS);
					StringBuffer action=new StringBuffer();					
					action.append("statshow.do?b_chart=chart&statid=" + id);
					StringBuffer xml=new StringBuffer();
					xml.append("/com/workbench/stat/statitemtree?tablename=slegend&");	
					xml.append( "parentid="+id+"&target=mil_body");
					String uid=id;
					this.getFormHM().put("uid", uid);
					this.getFormHM().put("text", stat_name);
					this.getFormHM().put("action", action.toString());
					this.getFormHM().put("xml", xml.toString());	
					this.getFormHM().put("opflag", "true");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					this.getFormHM().put("opflag", "false");
				}
		  }
	  }
	  private String getMaxId()throws GeneralException
		{
			int nid=-1;
			StringBuffer sql=new StringBuffer("select max(id)+1 as nmax from sname");
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			try
			{
				this.frowset=dao.search(sql.toString());
				if(this.frowset.next())
				{
					nid=this.frowset.getInt("nmax");
				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
	   	       throw GeneralExceptionHandler.Handle(ex);			
			}
			return String.valueOf(nid);
		}

}
