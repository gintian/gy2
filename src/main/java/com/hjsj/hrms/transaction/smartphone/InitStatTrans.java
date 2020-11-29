package com.hjsj.hrms.transaction.smartphone;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;

public class InitStatTrans   extends IBusiness {

	public void execute() throws GeneralException {
		String showstyle="";
		String statlabel="false;";
		ContentDAO dao=new ContentDAO(this.getFrameconn());//判断是否分类
		String resourcepriv=this.userView.getResourceString(IResourceConstant.STATICS);
		if(!this.userView.isSuper_admin()&&resourcepriv.length()>0){
			String[] tmp=resourcepriv.split(",");
			StringBuffer sb= new StringBuffer("-1");
			for(int i=0;i<tmp.length;i++){
				if(tmp[i].trim().length()>0)
					sb.append(","+tmp[i].trim());
			}
			resourcepriv = sb.toString();
		}else
			resourcepriv="-1";
		try {
			String sql = "select count(*) a from sname where (categories is not null or categories<>'') and infokind=1 and type=1";
			if(!this.userView.isSuper_admin())
				sql+=" and id in("+resourcepriv+")";
			this.frowset=dao.search(sql);
			if(this.frowset.next())
			{
				int count=this.frowset.getInt("a");
				if(count>0)
				{
					statlabel="true";
					showstyle="0";
				}	
				else
					showstyle="1";
			}else
				showstyle="1";
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.getFormHM().put("resourcepriv", resourcepriv);
		this.getFormHM().put("showstyle", showstyle);
		this.getFormHM().put("statlabel", statlabel);
	}

}
