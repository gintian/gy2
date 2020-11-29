package com.hjsj.hrms.transaction.org.orgdata;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

public class NewMultimediaList extends IBusiness
{

    public void execute() throws GeneralException
    {

	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
	String infor = (String) hm.get("infor");
	hm.remove("infor");
	String kind = "";
	if ("3".equals(infor))
	    kind = "0";
	else if("2".equals(infor))
	    kind = "2";
	else
	    kind="6";
	this.getFormHM().put("kind", kind);

	String a0100 = (String) hm.get("itemid");
	a0100 = a0100 != null ? a0100 : "";
	hm.remove("itemid");
	this.getFormHM().put("a0100", a0100);
	
	String curri9999 = (String) hm.get("curri9999");
	curri9999 = curri9999 != null ? curri9999 : "";
	hm.remove("curri9999");
	this.getFormHM().put("curri9999", curri9999);	
	
	ContentDAO dao = new ContentDAO(this.getFrameconn());
	
	if(hm.get("b_edit")!=null)//编辑标题
	{
	    String i9999 = (String) hm.get("i9999");
	    hm.remove("i9999");
	    this.getFormHM().put("i9999", i9999);
	    
	    String dbpre=(String) hm.get("dbpre");
		hm.remove("dbpre");
	    this.getFormHM().put("dbname", dbpre);
	    String title = this.getTitle(dao, kind, a0100, i9999, dbpre);
	    this.getFormHM().put("filetitle", title);	 
	    
	    hm.remove("b_edit");
	}else if(hm.get("b_add2")!=null)
	{
	    this.getFormHM().put("filetitle", "");	
	    ArrayList fileTypeList = this.getTypeList(dao, kind);
	    this.getFormHM().put("fileTypeList", fileTypeList);
	    hm.remove("b_add2");
	}	
	
    }

    
    public String getTitle(ContentDAO dao, String kind,String a0100,String i9999,String dbpre)
    {
	String title="";
	StringBuffer sql = new StringBuffer();
	try
	{
		if("6".equals(kind))
		{
		    sql.append(" select title from "+dbpre+"a00 ");
		    sql.append(" where a0100='"+a0100+"' and i9999="+i9999);
		}else if("0".equals(kind))
		{
		    sql.append(" select title from k00 ");
		    sql.append(" where e01a1='"+a0100+"' and i9999="+i9999);
		}else 
		{
		    sql.append(" select title from b00 ");
		    sql.append(" where b0110='"+a0100+"' and i9999="+i9999);
		}
	    this.frowset = dao.search(sql.toString());
	    if(this.frowset.next())
		title = this.frowset.getString(1);
	} catch (Exception e)
	{
	    e.printStackTrace();
	}
	return title;
    }
    
    
    
    
    public ArrayList getTypeList(ContentDAO dao, String kind)
    {

	ArrayList retlist = new ArrayList();
	String sql = "";
	try
	{
	    if ("6".equals(kind))// 人员
	    {
		sql = "select id,flag,sortname from MediaSort where dbflag=1";

	    } else if ("0".equals(kind))// 职位
	    {
		sql = "select id,flag,sortname from MediaSort where dbflag=3";

	    } else
	    // 单位
	    {
		sql = "select id,flag,sortname from MediaSort where dbflag=2";
	    }
	    this.frowset = dao.search(sql);
	    while (this.frowset.next())
	    {
		String flag = this.frowset.getString("flag");
		if (this.userView.isSuper_admin())
		{
		    String datavalue = this.frowset.getString("sortname");
		    CommonData cd = new CommonData(flag, datavalue);
		    retlist.add(cd);
		} else
		{
		    // if(this.checkMediaPriv(dao,flag))
		    // String id = this.frowset.getString("id");
		    // if(this.userView.isHaveResource(IResourceConstant.MEDIA_EMP,id))
		    if (this.userView.hasTheMediaSet(flag))
		    {
			String datavalue = this.frowset.getString("sortname");
			CommonData cd = new CommonData(flag, datavalue);
			retlist.add(cd);
		    }
		}

	    }
	} catch (Exception e)
	{
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
    public boolean checkMediaPriv(ContentDAO dao, String flag)
    {

	RowSet rs;
	boolean ret = false;
	String mediapriv = "";
	int status = 0;
	StringBuffer sb = new StringBuffer();
	sb.append(" select * from t_sys_function_priv where id = '" + this.userView.getUserName().toLowerCase() + "'");
	try
	{
	    rs = dao.search(sb.toString());
	    while (rs.next())
	    {
		mediapriv = rs.getString("mediapriv");
	    }
	    if (!(mediapriv == null || "".equals(mediapriv) || ",".equals(mediapriv)))
	    {
		String arr[] = mediapriv.split(",");
		for (int i = 0; i < arr.length; i++)
		{
		    if (arr[i].equalsIgnoreCase(flag))
		    {
			ret = true;
			break;
		    }
		}
	    }

	} catch (Exception ee)
	{
	    ee.printStackTrace();
	}
	return ret;
    }
}
