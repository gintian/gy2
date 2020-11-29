package com.hjsj.hrms.transaction.general.query.common;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;

public class SaveGeneralOrderTrans extends IBusiness {
	public void execute() throws GeneralException {
		ArrayList selects=(ArrayList)this.getFormHM().get("selects");
		String type=(String)this.getFormHM().get("type");
		String curr_value=(String)this.getFormHM().get("curr_value");
		String categories = (String)this.getFormHM().get("categories");
		if(type==null|| "".equals(type))
        	type="1";
		if(selects==null||selects.size()<=0)
			return;
		StringBuffer sql=new StringBuffer();
		sql.append("update lexpr set norder=? where id=? and type=?");
		ArrayList list=new ArrayList();
		String id="";
		for(int i=0;i<selects.size();i++)
		{
			id=(String)selects.get(i);
			ArrayList olist=new ArrayList();
			olist.add(new Integer(i+1));
			olist.add(id);
			olist.add(type);
			list.add(olist);
		}
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try {
			dao.batchUpdate(sql.toString(), list);
			sql.setLength(0);
			sql.append("select id,name,type from lexpr where type='");//
			sql.append(type);
			if(categories==null||categories.length()==0){
				sql.append("' and (categories='' or categories is null) order by norder");
	        }else
	        	sql.append("' and categories='"+categories+"' order by norder");
			 /**常用查询条件列表*/
            this.frowset=dao.search(sql.toString());
            int i=1;
            ArrayList condlist=new ArrayList();
            while(this.frowset.next())
            {
                if(!(this.userView.isHaveResource(IResourceConstant.LEXPR,this.frowset.getString("id"))))
                	continue;
                CommonData tempvo=new CommonData(this.frowset.getString("id"),i+"."+this.getFrowset().getString("name"));
                condlist.add(tempvo);
               /* DynaBean vo=new LazyDynaBean();
                vo.set("id",this.frowset.getString("id"));
                vo.set("name",i+"."+this.getFrowset().getString("name"));
                vo.set("type",this.frowset.getString("type"));
                condlist.add(vo);*/
                ++i;
            }
            this.getFormHM().put("condlist",condlist);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
