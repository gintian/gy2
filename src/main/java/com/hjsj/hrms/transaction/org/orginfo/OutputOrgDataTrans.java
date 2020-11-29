package com.hjsj.hrms.transaction.org.orginfo;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * 组织编码，输出数据
 *<p>Title:OutputOrgDataTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Oct 11, 2007</p> 
 *@author sunxin
 *@version 4.0
 */
public class OutputOrgDataTrans extends IBusiness{
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    public void execute()throws GeneralException{
    	String orgid=(String)this.getFormHM().get("orgid");
    	String time=(String)this.getFormHM().get("time");//没有设置历史时日就是今日日期  设置了就是历史时日导出 wangb 20170814 30188
    	if(orgid==null|| "".equals(orgid))
    		orgid="root";
    	if(orgid.length()<=2)
    	{
    		this.getFormHM().put("file","");
    	}
    	String file="";
    	if("root".equalsIgnoreCase(orgid))
    	{
//    		file=getAllFileUrl();
    		file=getAllFileUrl(time);
    	}else 
    	{
    		String codeitemid=orgid.substring(2);
            String codesetid=orgid.substring(0,2);
//            file=getFileUrl(codesetid,codeitemid);
            file=getFileUrl(codesetid,codeitemid,time);
    	}
    	file = PubFunc.encrypt(file);   //add by wangchaoqun on 2014-9-29
    	this.getFormHM().put("file",file);
    }
//    private String getFileUrl(String codesetid,String codeitemid)
	private String getFileUrl(String codesetid,String codeitemid,String time)//添加String time 时间参数 当前时间或历史节点 wangb 20170814  30188
	{
        StringBuffer sql=new StringBuffer();
//        sql.append("select * from organization where codeitemid like '" + codeitemid + "%' and codeitemid<>'"+codeitemid+"' and "+Sql_switcher.dateValue(sdf.format(new Date()))+" between start_date and end_date ORDER BY a0000");//ParentId, codeitemid
        sql.append("select * from organization where codeitemid like '" + codeitemid + "%' and codeitemid<>'"+codeitemid+"' and "+Sql_switcher.dateValue(time)+" between start_date and end_date ORDER BY a0000");//ParentId, codeitemid 查询某一时间点的机构  time 当前日期或历史时日  wangb 20170814 30188
		ContentDAO dao=new ContentDAO(this.getFrameconn());	
		String file=this.userView.getUserName()+"_outout_organization"+".txt";
		PrintWriter out=null;
		OutputStreamWriter ow=null;
		try
		{
			FileOutputStream fo=new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+file);
			ow=new OutputStreamWriter(fo,"UTF-8");
			out=new PrintWriter(ow);
			this.frowset=dao.search(sql.toString());
			while(this.frowset.next())
			{
				String codeID=this.frowset.getString("codeitemid");
				String codeset=this.frowset.getString("codesetid");
				String codeitemdesc=this.frowset.getString("codeitemdesc");
				codeID=codeID.substring(codeitemid.length());
				String corcode = this.frowset.getString("corcode");
				corcode=corcode!=null?corcode:"";
				corcode=!"null".equals(corcode)?corcode:"";
				Date s_date = this.frowset.getDate("start_date");
				String start_date = s_date==null?"":s_date.toString();
				Date e_date = this.frowset.getDate("end_date");
				String end_date = e_date==null?"":e_date.toString();
				out.print(codeset+"\t"+codeID+"\t"+codeitemdesc+"\t"+corcode+"\t"+start_date+"\t"+end_date+"\r\n");
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			PubFunc.closeIoResource(ow);
			if(out!=null)
				out.close();
		}
		return file;
	}
//	private String getAllFileUrl()
	private String getAllFileUrl(String time)//添加 String time 时间参数 当前时间或历史节点 wangb 20170814 30188
	{
        StringBuffer sql=new StringBuffer();
        if(this.userView.getManagePrivCode().length()>0)
        	sql.append("select * FROM ORGANIZATION where "+Sql_switcher.dateValue(time)+" between start_date and end_date and codeitemid like '"+this.userView.getManagePrivCodeValue()+"%' ORDER BY a0000");//ParentId, codeitemid 查询某一时间点的机构   time 当前日期或历史时日  wangb 20170814 30188
//        sql.append("select * FROM ORGANIZATION where "+Sql_switcher.dateValue(sdf.format(new Date()))+" between start_date and end_date and codeitemid like '"+this.userView.getManagePrivCodeValue()+"%' ORDER BY a0000");//ParentId, codeitemid
        else 
        	sql.append("select * FROM ORGANIZATION where codesetid=''");
        ContentDAO dao=new ContentDAO(this.getFrameconn());	
		String file="outout_organization_"+this.userView.getUserName()+".txt";
		PrintWriter out=null;
		FileOutputStream fo = null;
		OutputStreamWriter ow= null;
		try
		{
			fo=new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+file);
			ow = new OutputStreamWriter(fo,"gbk");
			out=new PrintWriter(ow);
			this.frowset=dao.search(sql.toString());
			while(this.frowset.next())
			{
				String codeID=this.frowset.getString("codeitemid");
				String codeset=this.frowset.getString("codesetid");
				String codeitemdesc=this.frowset.getString("codeitemdesc");
				String corcode = this.frowset.getString("corcode");
				corcode=corcode!=null?corcode:"";
				corcode=!"null".equals(corcode)?corcode:"";
				Date s_date = this.frowset.getDate("start_date");
				String start_date = s_date==null?"":s_date.toString();
				Date e_date = this.frowset.getDate("end_date");
				String end_date = e_date==null?"":e_date.toString();
				out.println(codeset+"\t"+codeID+"\t"+codeitemdesc+"\t"+corcode+"\t"+start_date+"\t"+end_date+"\r");
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			PubFunc.closeIoResource(ow);
			PubFunc.closeIoResource(fo);
			if(out!=null)
				out.close();
		}
		return file;
	}
	
}
