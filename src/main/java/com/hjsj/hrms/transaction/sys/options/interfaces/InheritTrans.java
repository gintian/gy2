package com.hjsj.hrms.transaction.sys.options.interfaces;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;
/**
 * 数据集成,常用统计分析
 * @author Owner
 *wangyao
 */
public class InheritTrans extends IBusiness{
	private String username="";
//	private String password="";
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ArrayList list = new ArrayList();
		list = selectdata();
		this.getFormHM().put("listis", list);
	}
	public ArrayList selectdata()
	{
		//System.out.println(this.userView.getUserName()+"登录进入");
		ArrayList list = new ArrayList();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		StringBuffer sql = new StringBuffer();
		sql.append("select * from sname where infokind=1");
		RowSet rs;
		LazyDynaBean bean=null;
		try
		{
			String url="";
			String title="";
			int j=0;
			rs = dao.search(sql.toString());
			while(rs.next())
			{
				if(!(this.userView.isHaveResource(IResourceConstant.STATICS,rs.getString("id"))))
					continue;
				String hzname=rs.getString("name");//(String)statvo.get("name");
				j=hzname.indexOf(".");
				hzname=hzname.substring(j+1);
				
				if("1".equals(rs.getString("type")))
				{
					    url="javascript:openlink(\"/general/static/commonstatic/statshow.do?b_chart=link&querycond=&infokind=1&home=2&isshowstatcond=1&statid="+ rs.getString("id")+"\")";
			    }
			    else
			    {	
					   url="javascript:openlink(\"/general/static/commonstatic/statshow.do?b_doubledata=data&querycond=&home=2&infokind=1&isshowstatcond=1&statid="+ rs.getString("id")+"\")";				
				}  
				//url="/general/static/commonstatic/statshow.do?b_doubledata=data&querycond=&infokind=1&isshowstatcond=1&statid="+ rs.getString("id");
//				url="/general/static/commonstatic/statshow.do?b_doubledata=data&querycond=&infokind=1&isshowstatcond=1&home=1&statid="+ rs.getString("id")+"&etoken="+PubFunc.convertTo64Base(this.username);
				title=hzname;
				RecordVo vo=new RecordVo("sname");
//				vo.setString("moduleflag", url);
//				vo.setString("name", title);
//				vo.setString("description", "Hrp预警通知");
//				list.add(vo);
				bean=new LazyDynaBean();
				bean.set("url", url);
				bean.set("title", title);
				list.add(bean);
			}
		}catch (Exception e)
		{
			e.printStackTrace();
		}
		//System.out.println("list=="+list.size());
		//System.out.println(this.userView.getUserName()+"离开");
		return list;
	}
//	public UserView getSetView(String username,String password,String validatepwd,Connection conn)
//	   {  
//		   
//		   if(validatepwd!=null&&validatepwd.equals("false"))
//			   this.userView=new UserView(username,conn);
//		   else
//		      this.userView=new UserView(username,password,conn);	
//		   try {
//			this.username=username;   		
//			if(!this.userView.canLogin())
//			  return null;
//		   } catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		   }
//		   
//		   return userView;
//	   }

}
