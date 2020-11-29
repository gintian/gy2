package com.hjsj.hrms.transaction.general.inform.emp.view;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.DynaBean;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *<p>Title:SearchDataTableTrans</p> 
 *<p>Description:显示＆隐藏指标</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-9-4:下午02:03:54</p> 
 *@author FengXiBin
 *@version 4.0
 */

public class DeleteMultimediaFolderList extends IBusiness {

	public  void execute()throws GeneralException
	{
		try
		{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
//			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
			String multimediaflag = (String)this.getFormHM().get("multimediaflag");
//			hm.put("multimediaflag","");
			this.getFormHM().put("multimediaflag",multimediaflag);
			String kind = (String)this.getFormHM().get("kind");
			String dbflag="";
			if("6".equals(kind))// 人员
    		{
				dbflag="1";
    			
    		}else if("0".equals(kind))// 职位
    		{
    			dbflag="3";
    			
    		}else  // 单位
    		{
    			dbflag="2";
    			
    		}
			ArrayList multimedialist = this.getMultimediaList(multimediaflag,dbflag,dao);
			this.getFormHM().put("multimedialist", multimedialist);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/*
	 * 获得多媒体list
	 */
	public ArrayList getMultimediaList(String flag,String dbflag,ContentDAO dao)
	{
		ArrayList templist = new ArrayList();
		ArrayList retlist = new ArrayList();
		StringBuffer sb = new StringBuffer();	
		sb.append(" select id,sortname from mediasort ");
		sb.append(" where flag <> '"+flag+"' ");
		sb.append(" and dbflag ="+dbflag);
//		System.out.println(sb.toString());
		try
		{
			templist= dao.searchDynaList(sb.toString());
			for (Iterator it = templist.iterator(); it.hasNext();)
			{
				DynaBean dyna = (DynaBean)it.next();
				String id = (String)dyna.get("id");
				String sortname = (String)dyna.get("sortname");
				CommonData cd = new CommonData(id,sortname);
				retlist.add(cd);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return retlist;
	}
}
