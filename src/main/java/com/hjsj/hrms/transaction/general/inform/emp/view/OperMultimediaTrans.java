package com.hjsj.hrms.transaction.general.inform.emp.view;

import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *<p>Title:OperMultimediaTrans</p> 
 *<p>Description:OperMultimediaTrans</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-9-4:下午02:03:54</p> 
 *@author FengXiBin
 *@version 4.0
 */

public class OperMultimediaTrans extends IBusiness {

	public  void execute()throws GeneralException
	{
		String unit="";
		String pos="";
		String a0101="";
		StringBuffer strsql=new StringBuffer();
		boolean buttonhidden = false;
		try
		{
			
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 		
			String multimediaflag = (String)hm.get("multimediaflag");
			String isvisible ="0";
			if(hm.get("isvisible")!=null)
			{
				isvisible=(String)hm.get("isvisible");
				hm.remove("isvisible");
			}else
			{
				if(this.getFormHM().get("isvisible")!=null)
					isvisible=(String)this.getFormHM().get("isvisible");
			}
			//hm.put("isvisible",isvisible);
			if(multimediaflag==null || "".equals(multimediaflag))
			{
				multimediaflag=(String)this.getFormHM().get("multimediaflag");
				if(multimediaflag==null || "".equals(multimediaflag))
					this.getFormHM().put("multimediaflag","");
				else
					this.getFormHM().put("multimediaflag",multimediaflag);					
			}
			else
				this.getFormHM().put("multimediaflag",multimediaflag);		
			hm.put("multimediaflag","");
			String kind = (String)this.getFormHM().get("kind");
			String dbname = "";
			if("6".equals(kind))
			{
				dbname = (String)this.getFormHM().get("dbname");
			}
			String A0100 = (String)hm.get("a0100");
			CheckPrivSafeBo checkPrivSafeBo=new CheckPrivSafeBo(this.frameconn,this.userView);
			if("6".equals(kind)){
				dbname=checkPrivSafeBo.checkDb(dbname);
				A0100=checkPrivSafeBo.checkA0100("", dbname, A0100, "");
			}else if(!"9".equals(kind)){
				checkPrivSafeBo.checkOrg(A0100, "4");
			}
			/*判断是否显示状态*/
			Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
			String approveflag=sysoth.getValue(Sys_Oth_Parameter.APPROVE_FLAG);
			if("1".equals(approveflag))
			{
				this.getFormHM().put("display_state","yes");		
			}else if("0".equals(approveflag))
			{
				this.getFormHM().put("display_state","no");		
			}
			/*判断是否拥有主集*/
			String check = CheckMain( dao, kind, dbname, A0100);
			if("yes".equalsIgnoreCase(check))
				this.getFormHM().put("check_main","yes");	
			else
				this.getFormHM().put("check_main","no");	
			/*判断是否可以新增文件*/
//			check = this.newFilePriv(dao,kind);
//			if(check.equalsIgnoreCase("yes"))
//				this.getFormHM().put("newFilePriv","yes");	
//			else
//				this.getFormHM().put("newFilePriv","no");				
			/*判断是业务还是自助平台*/
			
			if("hcm".equals(this.userView.getBosflag()) || "hl".equals(this.userView.getBosflag())){//6.0版本之后权限不再区分业务和自助  guodd
				this.getFormHM().put("is_yewu","all");
			}else{//6.0版本之前区分
				/*判断是业务还是自助平台*/
				if(this.userView.isBbos())
					this.getFormHM().put("is_yewu","yes");	
				else
					this.getFormHM().put("is_yewu","no");
			}
			/*
			if (this.userView.getA0100().length() > 0) {
				if (this.userView.getStatus() == 4) {
					this.getFormHM().put("is_yewu","no");
				} else {
					this.getFormHM().put("is_yewu","yes");
				}
			} else {
				this.getFormHM().put("is_yewu","yes");
			}
			*/
			/*前台多媒体文件*/
			ArrayList multimedialist=new ArrayList();
			if("K".equalsIgnoreCase(multimediaflag)){//xuj 2010-4-20 ，k代号已成为多媒体岗位说明书固定分类,但此分类中只能上传一条记录
				multimedialist=this.getMultimediaListK(dao,A0100,approveflag,kind);
			}else{
				multimedialist = this.getMultimediaList(dao,dbname,A0100,multimediaflag,kind,approveflag);
			}
			this.getFormHM().put("multimedialist", multimedialist);
			
			if("6".equals(kind))  // 人员
			{
				strsql.append("select b0110,e0122,e01a1,a0101 from ");
			    strsql.append(dbname);
			    strsql.append("A01 where a0100='");
			    strsql.append(A0100);
			    strsql.append("'");
			    this.frowset = dao.search(strsql.toString()); 
			    if(this.frowset.next())
				{
			    	unit=this.getFrowset().getString("B0110");
			    	pos=this.getFrowset().getString("E0122");
				    a0101=this.getFrowset().getString("a0101");			
				}
			    if(unit !=null && unit.trim().length()>0)
			 		unit=AdminCode.getCode("UN",unit)!=null?AdminCode.getCode("UN",unit).getCodename():"";
				if(pos !=null && pos.trim().length()>0)
					pos=AdminCode.getCode("UM",pos)!=null?AdminCode.getCode("UM",pos).getCodename():"";
			
				this.getFormHM().put("unit",unit);
				this.getFormHM().put("pos",pos);	
				this.getFormHM().put("a0101",a0101);
			}else if("0".equals(kind))   // 职位
			{
				pos=A0100;
				if(pos !=null && pos.trim().length()>0)
				{
					pos=AdminCode.getCode("@K",pos)!=null?AdminCode.getCode("@K",pos).getCodename():"";
					if(pos ==null || pos.trim().length()<=0){
						pos=A0100;
						pos=AdminCode.getCode("UM",pos)!=null?AdminCode.getCode("UM",pos).getCodename():"";
					}
				}
				this.getFormHM().put("pos",pos);
				
				
				//维护岗位多媒体时，如果 已上传岗位说明书（分类K） 并且 没有其他分类 隐藏 添加按钮
				String sql = " select  '1' from k00 u where flag='K' and e01a1='"+A0100+"'";
				this.frowset = dao.search(sql);
				if(frowset.next())
					buttonhidden = true;
				else
					buttonhidden = false;
				
				if(buttonhidden){
					sql = " select '1' from mediasort where dbflag=3 ";
					this.frowset = dao.search(sql);
					if(frowset.next())
						buttonhidden = false;
				}
				
				
			}else if("9".equals(kind))
			{
				RecordVo vo = ConstantParamter.getRealConstantVo("PS_C_CODE");
				String codesetid = vo.getString("str_value");
				strsql.delete(0, strsql.length());
				strsql.append("select codeitemdesc from codeitem where codesetid='"+codesetid+"' ");
				strsql.append(" and codeitemid='"+A0100+"' ");
				this.frowset = dao.search(strsql.toString()); 
				if(frowset.next()){
					pos = frowset.getString("codeitemdesc");
				}
				this.getFormHM().put("pos", pos);
			}
			else    // 单位
			{
				unit=A0100;
				if(unit !=null && unit.trim().length()>0)
				{
					unit=AdminCode.getCode("UN",unit)!=null?AdminCode.getCode("UN",unit).getCodename():"";
			 		if(unit ==null || unit.trim().length()<=0){
			 			unit=A0100;
			 			unit=AdminCode.getCode("UM",unit)!=null?AdminCode.getCode("UM",unit).getCodename():"";
			 		}
				}		 		
			 	this.getFormHM().put("unit",unit);
			 	this.getFormHM().put("isvisible",isvisible);
			}		
		    
			if(buttonhidden)
				this.getFormHM().put("buttonflag", "1");
			else
				this.getFormHM().put("buttonflag", "0");
			
		}catch(Exception e){
			e.printStackTrace();
		}
		Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
		//是否需要审核，1为需要，0为不需要
		String approveflag=sysoth.getValue(Sys_Oth_Parameter.APPROVE_FLAG);
		//是否直接入库，如果1：不直接入库；0为直接入库
		String inputchinfor=sysoth.getValue(Sys_Oth_Parameter.INPUTCHINFOR);
		this.getFormHM().put("approveflag", approveflag);
		this.getFormHM().put("inputchinfor", inputchinfor);
	}
	/*
	 * 获得多媒体list
	 */
	public ArrayList getMultimediaList(ContentDAO dao,String dbname,String A0100,String flag,String kind,String approveflag)
	{
		ArrayList retlist = new ArrayList();
		if((flag==null || "".equals(flag)))
		if(this.userView.hasTheMediaSet("K")){
			retlist=this.getMultimediaListK(dao,A0100,approveflag,kind);
		}
		StringBuffer sb = new StringBuffer();	
		try
		{
			String t_flag = "";
			sb.append("select mediapriv from t_sys_function_priv where id = '"+this.userView.getUserName()+"'");
			this.frowset = dao.search(sb.toString());
			if(this.frowset.next())
			{
				t_flag = this.frowset.getString("mediapriv");
				if(!(t_flag==null || "".equals(t_flag)|| ",".equals(t_flag)))
				{
					t_flag = t_flag.substring(1,t_flag.length()-1);
					t_flag = this.getString(t_flag);
				}
					
			}
			sb.delete(0,sb.length());
			if("6".equals(kind)) // 人员
			{
				if(!(dbname==null || "".equals(dbname)))
				{
					sb.append(" select  u.i9999,u.title,u.flag,u.state,m.sortname,u.fileid from "+dbname+"a00 u ");
				}else{
					sb.append(" select  u.i9999,u.title,u.flag,u.state,m.sortname,u.fileid from usra00 u ");
				}
			}else if("0".equals(kind)) // 职位
			{
				sb.append(" select  u.i9999,u.title,u.flag,u.state,m.sortname,u.fileid from k00 u ");
			}else if("9".equals(kind))
			{
				sb.append(" select  u.i9999,u.title,u.flag,u.state,m.sortname,u.fileid from H00 u ");
			}else // 单位
			{
				sb.append(" select  u.i9999,u.title,u.flag,u.state,m.sortname,u.fileid from b00 u ");
			}
			
			int dbflag=0;
			String adda0100="";
			if("6".equals(kind)) // 人员
			{
				if(!(A0100==null || "".equals(A0100)))
				{
					adda0100=" and u.a0100='"+A0100+"' ";
				}
				dbflag=1;
			}else if("0".equals(kind)) // 职位
			{
				if(!(A0100==null || "".equals(A0100)))
				{
					adda0100=" and u.e01a1='"+A0100+"' ";
				}
				dbflag=3;
			}else if("9".equals(kind))
			{
				if(!(A0100==null || "".equals(A0100)))
				{
					adda0100=" and u.H0100='"+A0100+"' ";
				}
				dbflag=4;
			}else // 单位
			{
				if(!(A0100==null || "".equals(A0100)))
				{
					adda0100=" and u.b0110='"+A0100+"' ";
				}
				dbflag=2;
			}
			
			sb.append(" left join (select * from mediasort where dbflag="+dbflag+") m  on u.flag=m.flag ");
			if(Sql_switcher.searchDbServer()== Constant.ORACEL)
				sb.append(" where nvl(u.flag,'none') <> 'p' ");
			else
			    sb.append(" where isnull(u.flag,'none') <> 'p' ");
			if(!(flag==null || "".equals(flag)))
			{
				sb.append(" and u.flag='"+flag+"' ");
			}else{
				if(!this.userView.isSuper_admin())
				{
					StringBuffer mediabuf = this.userView.getMediapriv();
					if(!(mediabuf.toString()==null || "".equals(mediabuf.toString())|| ",".equals(mediabuf.toString())))
						sb.append(" and u.flag in ("+this.getFlag()+")");
					else
						sb.append(" and 1=2 ");
				}
			}
			
			sb.append(adda0100);
			sb.append(" and u.flag!='K' and u.flag!='P' ");
			sb.append(" order by u.flag ,u.i9999 ");
			this.frowset = dao.search(sb.toString());             //获取子集的纪录数据
			  while(this.frowset.next())
			  {
				  String title =this.getFrowset().getString("title");
			     DynaBean vo=new LazyDynaBean();
			     vo.set("i9999",Integer.toString(this.getFrowset().getInt("i9999")));
			     vo.set("title",(title==null|| "".equalsIgnoreCase(title))?"未知文件名":title);
			     vo.set("flag",this.getFrowset().getString("flag"));
			       String sortname=this.getFrowset().getString("sortname");
			       sortname=sortname==null|| "".equalsIgnoreCase(sortname)?"未分类":sortname;
			     vo.set("sortname",sortname);
			     vo.set("fileid",this.getFrowset().getString("fileid"));
			     if("1".equals(approveflag))
			     {
			    	 String statevalue = this.getFrowset().getString("state");
			    	 if(statevalue==null) {statevalue="0";}
			    	 vo.set("state",statevalue);
			     }
			     
			     retlist.add(vo);
			  }
		}catch(Exception e){
			e.printStackTrace();
		}
		return retlist;
	}
	/**
	 * 判断是否拥有主集
	 * @param dao
	 * @param kind
	 * @param dbname
	 * @param A0100
	 * @return
	 */
	public String CheckMain(ContentDAO dao,String kind,String dbname,String A0100)
	{
		StringBuffer sb = new StringBuffer();
		String retstr = "";
		if("6".equals(kind)) // 人员
		{
			sb.append(" select *  from "+dbname+"a01 ");
			sb.append(" where a0100 = '"+A0100.toUpperCase()+"'");
		}else if("0".equals(kind)) // 职位
		{
			sb.append(" select * from k01 ");
			sb.append(" where  e01a1='"+A0100.toUpperCase()+"' ");
		}else if("9".equals(kind)){
			sb.append(" select * from h01 ");
			sb.append(" where  h0100='"+A0100.toUpperCase()+"' ");
		}else
		{
			sb.append(" select * from b01 ");
			sb.append(" where  b0110='"+A0100.toUpperCase()+"' ");
		}
		try
		{
			this.frowset = dao.search(sb.toString());
			if(this.frowset.next())
				retstr = "yes";
			else
				retstr = "no";
		}catch(Exception e){
			e.printStackTrace();
		}
		return retstr;
	}
	
	public String newFilePriv(ContentDAO dao,String kind)
	{
		
		StringBuffer sb = new StringBuffer();
		int i = 0;
		String retstr = "";
		String flag = "";
		if("6".equals(kind)) // 人员
		{
			sb.append(" select flag  from mediasort ");
			sb.append(" where dbflag = 1 ");
		}else if("0".equals(kind)) // 职位
		{
			sb.append(" select flag  from mediasort ");
			sb.append(" where dbflag = 3 ");
		}else	
		{
			sb.append(" select flag  from mediasort ");
			sb.append(" where dbflag = 2 ");
		}
		try
		{
			this.frowset = dao.search(sb.toString());
			while(this.frowset.next())
			{
				flag = this.frowset.getString("flag");
				if(this.checkMediaPriv(dao,flag))
					i++;
			}
			if(i>0)
				retstr = "yes";
			else
				retstr = "no";
		}catch(Exception e){
			e.printStackTrace();
		}
		return retstr;
	}
	
	public String getString(String t_flag)
	{
		StringBuffer ret = new StringBuffer();
		String[] temp = t_flag.split(","); 
		for(int i=0;i<temp.length;i++)
		{
			temp[i] = "'"+temp[i]+"'";
		}
		for(int i=0;i<temp.length;i++)
		{
			ret.append(","+temp[i]);
		}
		return ret.substring(1).toString();
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
	   
	   public String getFlag()
	   {
		   StringBuffer ret = new StringBuffer();
		   StringBuffer mediabuf = this.userView.getMediapriv();
		   if(!(mediabuf.toString()==null || "".equals(mediabuf.toString())|| ",".equals(mediabuf.toString())))
			{
				String arr[] = mediabuf.toString().split(",");
				for(int i=0;i<arr.length;i++)
				{
					//if(i>0)
						ret.append(",'"+arr[i]+"'");
				}
			}
		   return ret.substring(1).toString();
	   }

	public ArrayList getMultimediaListK(ContentDAO dao, String A0100, String approveflag, String kind) {

		ArrayList retlist = new ArrayList();
		StringBuffer sb = new StringBuffer();
		String itemid = "e01a1";
		try {
			if(!"0".equalsIgnoreCase(kind) && !"9".equalsIgnoreCase(kind))
				return retlist;
			
			sb.delete(0, sb.length());
			if ("0".equals(kind)) {
				sb.append(" select  u.i9999,u.title,u.flag,u.state from k00 u where flag='K'");
			} else {
				sb.append(" select  u.i9999,u.title,u.flag,u.state from h00 u where flag='K'");
				itemid = "h0100";
			}
			
			if (!(A0100 == null || "".equals(A0100))) {
				sb.append(" and u.");
				sb.append(itemid);
				sb.append("='" + A0100 + "' ");
			}
			
			this.frowset = dao.search(sb.toString()); // 获取子集的纪录数据
			while (this.frowset.next()) {
				String title = this.getFrowset().getString("title");
				DynaBean vo = new LazyDynaBean();
				vo.set("i9999", Integer.toString(this.getFrowset().getInt("i9999")));
				vo.set("title", (title == null || "".equalsIgnoreCase(title)) ? "未知文件名" : title);
				vo.set("flag", this.getFrowset().getString("flag"));
				vo.set("sortname", ResourceFactory.getProperty("lable.pos.e01a1.manual"));
				if ("1".equals(approveflag)) {
					String statevalue = this.getFrowset().getString("state");
					if (statevalue == null) {
						statevalue = "0";
					}
					vo.set("state", statevalue);
				}

				retlist.add(vo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return retlist;
	}
}
