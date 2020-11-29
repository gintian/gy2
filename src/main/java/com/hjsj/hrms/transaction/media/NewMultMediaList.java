package com.hjsj.hrms.transaction.media;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.util.ArrayList;

public class NewMultMediaList extends IBusiness {

	public  void execute()throws GeneralException
	{		
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		ArrayList fileTypeList = this.getTypeList(dao);
		this.getFormHM().put("fileTypeList",fileTypeList);
	}
	
	public ArrayList getTypeList(ContentDAO dao)
	{
		ArrayList retlist = new ArrayList();
		String sql = "";
		try
		{
			sql = "select id,flag,sortname from MediaSort where dbflag=1 order by id";
			this.frowset = dao.search(sql);
			while(this.frowset.next())
			{
				String flag = this.frowset.getString("flag");
				if(this.userView.isSuper_admin())
				{
					String datavalue = this.frowset.getString("sortname");
					CommonData cd = new CommonData(flag,datavalue);
					retlist.add(cd);
				}else{
//					if(this.checkMediaPriv(dao,flag))			
//					String id = this.frowset.getString("id");
//					if(this.userView.isHaveResource(IResourceConstant.MEDIA_EMP,id))	
					if(this.userView.hasTheMediaSet(flag))
					{
						String datavalue = this.frowset.getString("sortname");
						CommonData cd = new CommonData(flag,datavalue);
						retlist.add(cd);
					}
				}					
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return retlist;
	}
	
	/**
	 * 
	 * @param dao
	 * @param flag
	 * @return
	 */
	   public boolean checkMediaPriv(ContentDAO dao,String flag)
	   {
		   RowSet rs;
		   boolean ret = false;
		   String mediapriv = "";
			int status =  0 ;
			StringBuffer sb = new StringBuffer();
			sb.append(" select * from t_sys_function_priv where id = '"+this.userView.getUserName().toLowerCase()+"'");		
			try
			{
				rs = dao.search(sb.toString());
				while(rs.next())
				{
					mediapriv = rs.getString("mediapriv");
				}
				if(!(mediapriv==null || "".equals(mediapriv)|| ",".equals(mediapriv)))
				{
					String arr[] = mediapriv.split(",");
					for(int i=0;i<arr.length;i++)
					{
						if(arr[i].equalsIgnoreCase(flag))
						{
							ret = true;
							break;
						}
					}
				}
		
			}catch(Exception ee){
	          ee.printStackTrace();
	        }
		   return ret;
	   }
}
