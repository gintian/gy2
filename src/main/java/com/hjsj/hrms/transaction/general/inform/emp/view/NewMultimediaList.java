package com.hjsj.hrms.transaction.general.inform.emp.view;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.util.ArrayList;

public class NewMultimediaList extends IBusiness {

	public  void execute()throws GeneralException
	{		
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String kind = (String)this.getFormHM().get("kind");
		String isvisible=(String)this.getFormHM().get("isvisible");
		ArrayList fileTypeList = this.getTypeList(dao,kind);
		this.getFormHM().put("isvisible",isvisible);
		this.getFormHM().put("fileTypeList",fileTypeList);
	}

	public ArrayList getTypeList(ContentDAO dao,String kind)
	{
		ArrayList retlist = new ArrayList();
		String sql = "";
		
		try
		{
			if("0".equals(kind)){//xuj 2010-4-20 ，k代号已成为多媒体岗位说明书固定分类,但此分类中只能上传一条记录
				String e01a1 = (String)this.getFormHM().get("a0100");
				sql = "select e01a1 from k00 where e01a1='"+e01a1+"' and upper(flag)='K'";
				this.frowset = dao.search(sql);
				if(!this.frowset.next()){
					if(this.userView.hasTheMediaSet("K")){
						CommonData cd = new CommonData("K",ResourceFactory.getProperty("lable.pos.e01a1.manual"));
						retlist.add(cd);
					}
				}
			}
			if("9".equals(kind)){//xuj 2010-4-20 ，k代号已成为多媒体岗位说明书固定分类,但此分类中只能上传一条记录 //基准岗位分类和岗位分类是一样的
				String h0100 = (String)this.getFormHM().get("a0100");
				sql = "select h0100 from H00 where h0100='"+h0100+"' and upper(flag)='K'";
				this.frowset = dao.search(sql);
				if(!this.frowset.next()){
					if(this.userView.hasTheMediaSet("K")){
						CommonData cd = new CommonData("K",ResourceFactory.getProperty("lable.pos.e01a1.manual"));
						retlist.add(cd);
					}
				}
			}
			if("6".equals(kind))// 人员
			{
				sql = "select id,flag,sortname from MediaSort where dbflag=1  order by id";
				
			}else if("0".equals(kind))// 职位
			{
				sql = "select id,flag,sortname from MediaSort where dbflag=3  order by id";
				
			}else if("9".equals(kind))// 基准岗位
			{
				sql = "select id,flag,sortname from MediaSort where dbflag=4  order by id";
				
			}else  // 单位
			{
				sql = "select id,flag,sortname from MediaSort where dbflag=2  order by id";			
			}
			this.frowset = dao.search(sql);
			while(this.frowset.next())
			{
				String flag = this.frowset.getString("flag");
				if(this.userView.isSuper_admin())
				{
					String datavalue = this.frowset.getString("sortname");//文件分类
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
