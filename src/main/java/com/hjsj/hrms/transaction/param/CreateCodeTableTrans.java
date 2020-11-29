package com.hjsj.hrms.transaction.param;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * <p>Title:CreateCodeTableTrans.java</p>
 * <p>Description:</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2008-2-3 下午01:08:44</p>
 * @author LiZhenWei
 * @version 4.0
 */
public class CreateCodeTableTrans extends IBusiness{

	private HashMap map =null;
	private HashMap codesetmap;
	private ArrayList itemlist;
	private ArrayList parentlist;
	private ArrayList lll;
	private HashMap mm;
	private boolean isOrg=true;
	private String seprartor="/";
	private int codeItemPoint=0; //代码项生成位置指针，刷新数据字典使用 zhanghua 2017-12-29
	public void execute() throws GeneralException {
		int n=0;
		try
		{
			String path = SafeCode.decode((String)this.getFormHM().get("path"));
			//n=createCodeTable(path);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		this.getFormHM().put("n",n+"");
		
	}
	public int  createCodeTable(String path,Connection con)
	{
		int n=0;
		map = new HashMap();
		codesetmap=new HashMap();
		FileOutputStream fileOut=null;
		ByteBuffer buffer = null;
		FileChannel fc =null;
		try
		{
			StringBuffer buf_codeitem = new StringBuffer();
			StringBuffer buf_fielditem = new StringBuffer();
			buf_codeitem.append(" var g_dm = new Array();");
			buf_codeitem.append("\r\n");
			/***组织机构表*/
			ContentDAO dao  = new ContentDAO(con);
			RowSet rs = null;
			itemlist=new ArrayList();
			parentlist = new ArrayList();
			lll=new ArrayList();
			mm=new HashMap();
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(con);
			String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
			if(display_e0122==null|| "00".equals(display_e0122)|| "".equals(display_e0122))
				display_e0122="0";
			seprartor=sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122, "sep");
			 seprartor=seprartor!=null&&seprartor.length()>0?seprartor:"/";
			 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String backdate = sdf.format(new Date());
//				String sql = " select * from organization where " + Sql_switcher.dateValue(backdate)
//     			+ " between start_date and end_date or codeitemdesc='待定'  order by codesetid,codeitemid ";
				String sql = " select * from organization   order by codesetid,codeitemid ";
			rs = dao.search(sql);
		    while(rs.next())
		    {
		    	LazyDynaBean bean = new LazyDynaBean();
		    	bean.set("codesetid",rs.getString("codesetid"));
		    	bean.set("codeitemid",rs.getString("codeitemid"));
		    	bean.set("parentid",rs.getString("parentid")==null?"":rs.getString("parentid"));
		    	bean.set("codeitemdesc",rs.getString("codeitemdesc")==null?"":rs.getString("codeitemdesc"));
		    	String yy=rs.getDate("start_date")==null?"1900-01-01":rs.getDate("start_date").toString();
		    	String xx=rs.getDate("end_date")==null?"9999-12-31":rs.getDate("end_date").toString();
		    	bean.set("startdate",Integer.parseInt(yy.split("-")[0])*10000+Integer.parseInt(yy.split("-")[1])*100+Integer.parseInt(yy.split("-")[2].substring(0,2))+"");
		    	bean.set("enddate",Integer.parseInt(xx.split("-")[0])*10000+Integer.parseInt(xx.split("-")[1])*100+Integer.parseInt(xx.split("-")[2].substring(0,2))+"");
		    	if(codesetmap.get(rs.getString("parentid").toUpperCase())==null)
		    	{
		    		ArrayList lllll=new ArrayList();
			    	lllll.add(bean);
			    	codesetmap.put(rs.getString("parentid").toUpperCase(), lllll);
		    	}
		    	else
		    	{
		    		ArrayList lllll=(ArrayList)codesetmap.get(rs.getString("parentid").toUpperCase());
			    	lllll.add(bean);
			    	codesetmap.put(rs.getString("parentid").toUpperCase(), lllll);
		    	}
		    	itemlist.add(bean);
		    	if(rs.getString("parentid").equals(rs.getString("codeitemid")))
		    	{
		    		parentlist.add(bean);
		    	}
		    	else
		    	{
		    		lll.add(bean);
		    	}
		    }
		    rs.close();
		    mm=this.getLeafItemLinkMap(itemlist,"organization");
			for(int j=0;j<parentlist.size();j++)
			{
				LazyDynaBean pbean=(LazyDynaBean)parentlist.get(j);
				String codesetid=(String)pbean.get("codesetid");
				String codeitemid=(String)pbean.get("codeitemid");
				String codeitemdesc=(String)pbean.get("codeitemdesc");
				String parentid=(String)pbean.get("parentid");
				String startdate=(String)pbean.get("startdate");
				String enddate=(String)pbean.get("enddate");
				if(this.map.get((codesetid+codeitemid).toUpperCase())==null)
				{
			    	buf_codeitem.append("g_dm[g_dm.length] = ");
            		buf_codeitem.append("{ID:\"");
	        		buf_codeitem.append(codesetid.replaceAll("\\n", "").replaceAll("\\r","")+codeitemid.replaceAll("\\n", "").replaceAll("\\r",""));
	        		buf_codeitem.append("\",V:\"");
            		buf_codeitem.append(codeitemdesc.replaceAll("\\n", "").replaceAll("\\r","").replaceAll("\"", "\\\\\""));
            		
	        		buf_codeitem.append("\",S:\"");
            		buf_codeitem.append(startdate);
	        		buf_codeitem.append("\",E:\"");
            		buf_codeitem.append(enddate);
            		
            		buf_codeitem.append("\"");
    	        	buf_codeitem.append(",L:"+1);
    	        	if("UM".equalsIgnoreCase(codesetid))
    	        	{
    	        		CodeItem item=AdminCode.getCode("UM",codeitemid,Integer.parseInt(display_e0122));
    	        		String p=item.getCodename();
    	        	/*	if(p.lastIndexOf("/")!=-1)
    	        			p=p.substring(0,p.lastIndexOf("/"));
    	        		if(p.replaceAll("\\n", "").replaceAll("\\r","").replaceAll("\"", "\\\\\"").equalsIgnoreCase(codeitemdesc.replaceAll("\\n", "").replaceAll("\\r","").replaceAll("\"", "\\\\\"")))
    	        			p="";*/
    	        		buf_codeitem.append(",P:\""+p.replaceAll("\\n", "").replaceAll("\\r","").replaceAll("\"", "\\\\\"")+"\"");
    	        	}
    	        	buf_codeitem.append("};");
    	        	buf_codeitem.append("\r\n");
    	        	map.put((codesetid+codeitemid).toUpperCase(), "1");
				}
    	    	this.recursionLoop(codesetid, codeitemid, parentid, buf_codeitem, 1,con,dao,display_e0122);
			}
			itemlist=null;
			parentlist=null;
			codesetmap=null;
			lll=null;
			mm=null;
			/**虚拟组织机构*/
			rs = null;
			itemlist=new ArrayList();
			parentlist = new ArrayList();
			codesetmap=new HashMap();
			lll = new ArrayList();
			mm=new HashMap();
			
//			sql = " select * from vorganization where " + Sql_switcher.dateValue(backdate)
// 			+ " between start_date and end_date  order by codesetid,codeitemid ";
			sql = " select * from vorganization  order by codesetid,codeitemid ";
			rs = dao.search(sql);
		    while(rs.next())
		    {
		    	LazyDynaBean bean = new LazyDynaBean();
		    	bean.set("codesetid",rs.getString("codesetid"));
		    	bean.set("codeitemid",rs.getString("codeitemid"));
		    	bean.set("parentid",rs.getString("parentid")==null?"":rs.getString("parentid"));
		    	bean.set("codeitemdesc",rs.getString("codeitemdesc")==null?"":rs.getString("codeitemdesc"));
		    	String yy=rs.getDate("start_date")==null?"1900-01-01":rs.getDate("start_date").toString();
		    	String xx=rs.getDate("end_date")==null?"9999-12-31":rs.getDate("end_date").toString();
		    	bean.set("startdate",Integer.parseInt(yy.split("-")[0])*10000+Integer.parseInt(yy.split("-")[1])*100+Integer.parseInt(yy.split("-")[2].substring(0,2))+"");
		    	bean.set("enddate",Integer.parseInt(xx.split("-")[0])*10000+Integer.parseInt(xx.split("-")[1])*100+Integer.parseInt(xx.split("-")[2].substring(0,2))+"");
		    	if(codesetmap.get(rs.getString("parentid").toUpperCase())==null)
		    	{
		    		ArrayList lllll=new ArrayList();
			    	lllll.add(bean);
			    	codesetmap.put(rs.getString("parentid").toUpperCase(), lllll);
		    	}
		    	else
		    	{
		    		ArrayList lllll=(ArrayList)codesetmap.get(rs.getString("parentid").toUpperCase());
			    	lllll.add(bean);
			    	codesetmap.put(rs.getString("parentid").toUpperCase(), lllll);
		    	}
		    	itemlist.add(bean);
		    	if(rs.getString("parentid").equals(rs.getString("codeitemid")))
		    	{
		    		parentlist.add(bean);
		    	}
		    	else
		    	{
		    		lll.add(bean);
		    	}
		    }
		    rs.close();		    
		    mm=this.getLeafItemLinkMap(itemlist,"organization");
			for(int j=0;j<parentlist.size();j++)
			{
				LazyDynaBean pbean=(LazyDynaBean)parentlist.get(j);
				String codesetid=(String)pbean.get("codesetid");
				String codeitemid=(String)pbean.get("codeitemid");
				String codeitemdesc=(String)pbean.get("codeitemdesc");
				String parentid=(String)pbean.get("parentid");
				String startdate=(String)pbean.get("startdate");
				String enddate=(String)pbean.get("enddate");
				if(this.map.get((codesetid+codeitemid).toUpperCase())==null)
				{
			    	buf_codeitem.append("g_dm[g_dm.length] = ");
            		buf_codeitem.append("{ID:\"");
	        		buf_codeitem.append(codesetid.replaceAll("\\n", "").replaceAll("\\r","")+codeitemid.replaceAll("\\n", "").replaceAll("\\r",""));
	        		buf_codeitem.append("\",V:\"");
            		buf_codeitem.append(codeitemdesc.replaceAll("\\n", "").replaceAll("\\r","").replaceAll("\"", "\\\\\""));
            		
            		
	        		buf_codeitem.append("\",S:\"");
            		buf_codeitem.append(startdate);
	        		buf_codeitem.append("\",E:\"");
            		buf_codeitem.append(enddate);
            		
            		buf_codeitem.append("\"");
    	        	buf_codeitem.append(",L:"+1);
    	        	if("UM".equalsIgnoreCase(codesetid))
    	        	{
    	        		CodeItem item=AdminCode.getCode("UM",codeitemid,Integer.parseInt(display_e0122));
    	        		String p=item.getCodename();
    	        		/*if(p.lastIndexOf("/")!=-1)
    	        			p=p.substring(0,p.lastIndexOf("/"));
    	        		if(p.replaceAll("\\n", "").replaceAll("\\r","").replaceAll("\"", "\\\\\"").equalsIgnoreCase(codeitemdesc.replaceAll("\\n", "").replaceAll("\\r","").replaceAll("\"", "\\\\\"")))
    	        			p="";*/
    	        		buf_codeitem.append(",P:\""+p.replaceAll("\\n", "").replaceAll("\\r","").replaceAll("\"", "\\\\\"")+"\"");
    	        	}
    	        	buf_codeitem.append("};");
    	        	buf_codeitem.append("\r\n");
    	        	map.put((codesetid+codeitemid).toUpperCase(), "1");
				}
    	    	this.recursionLoop(codesetid, codeitemid, parentid, buf_codeitem, 1,con,dao,display_e0122);
			}
			itemlist=null;
			parentlist=null;
			codesetmap=null;
			lll=null;
			mm=null;
			isOrg=false;
			/**代码表*/
//		    sql =" select * from codeitem  where UPPER(codesetid) in (select distinct UPPER(codesetid) from " +
//		    		"(select distinct codesetid from fielditem where useflag<>0 and codesetid<>'0' union all select distinct codesetid " +
//		    		"from t_hr_busifield where useflag<>0 and codesetid<>'0' ) T ) order by codesetid,codeitemid";
		    
		    sql =" select codeitem.codesetid,codeitem.codeitemid,codeitem.parentid,codeitem.codeitemdesc,codeitem.invalid,codeitem.start_date,codeitem.end_date,CodeSet.validateflag from codeitem,CodeSet where codeitem.codesetid = CodeSet.CodeSetId  order by codeitem.codesetid,codeitemid";
			rs = null;
			itemlist=new ArrayList();
			parentlist = new ArrayList();
			codesetmap=new HashMap();
			lll = new ArrayList();
			mm=new HashMap();
			rs = dao.search(sql);
			while(rs.next())
			{
				if(rs.getString("codeitemid")==null||rs.getString("codeitemid").trim().length()==0)
					continue;
		    	LazyDynaBean bean = new LazyDynaBean();
		    	bean.set("codesetid",rs.getString("codesetid"));
		    	bean.set("codeitemid",rs.getString("codeitemid"));
		    	bean.set("parentid",rs.getString("parentid")==null?"":rs.getString("parentid"));
		    	bean.set("codeitemdesc",rs.getString("codeitemdesc")==null?"":rs.getString("codeitemdesc"));
		    	bean.set("invalid",rs.getString("invalid")==null?"0":rs.getString("invalid"));
		    	bean.set("validateflag",rs.getString("validateflag")==null?"0":rs.getString("validateflag"));
		    	String yy=rs.getDate("start_date")==null?"1900-01-01":rs.getDate("start_date").toString();
		    	String xx=rs.getDate("end_date")==null?"9999-12-31":rs.getDate("end_date").toString();
		    	bean.set("startdate",Integer.parseInt(yy.split("-")[0])*10000+Integer.parseInt(yy.split("-")[1])*100+Integer.parseInt(yy.split("-")[2].substring(0,2))+"");
		    	bean.set("enddate",Integer.parseInt(xx.split("-")[0])*10000+Integer.parseInt(xx.split("-")[1])*100+Integer.parseInt(xx.split("-")[2].substring(0,2))+"");
		    	if(codesetmap.get(rs.getString("codesetid").toUpperCase())==null)
		    	{
		    		ArrayList lllll=new ArrayList();
			    	lllll.add(bean);
			    	codesetmap.put(rs.getString("codesetid").toUpperCase(), lllll);
		    	}
		    	else
		    	{
		    		ArrayList lllll=(ArrayList)codesetmap.get(rs.getString("codesetid").toUpperCase());
			    	lllll.add(bean);
			    	codesetmap.put(rs.getString("codesetid").toUpperCase(), lllll);
		    	}
		    	itemlist.add(bean);
		    	if(rs.getString("parentid")==null||rs.getString("parentid").equals(rs.getString("codeitemid")))
		    	{
		    		parentlist.add(bean);
		    	}
		    	else
		    	{
		    		lll.add(bean);
		    	}
			}
		    rs.close();			
			mm=this.getLeafItemLinkMap(itemlist,"codeitem");
			for(int j=0;j<parentlist.size();j++)
			{
				LazyDynaBean pbean=(LazyDynaBean)parentlist.get(j);
				String codesetid=(String)pbean.get("codesetid");
				String codeitemid=(String)pbean.get("codeitemid");
				String codeitemdesc=(String)pbean.get("codeitemdesc");
				String parentid=(String)pbean.get("parentid");
				
				String invalid=(String)pbean.get("invalid");
				String validateflag=(String)pbean.get("validateflag");
				String startdate=(String)pbean.get("startdate");
				String enddate=(String)pbean.get("enddate");
				
				if(this.map.get((codesetid+codeitemid).toUpperCase())==null)
				{
				buf_codeitem.append("g_dm[g_dm.length] = ");
        		buf_codeitem.append("{ID:\"");
	    		buf_codeitem.append(codesetid.replaceAll("\\n", "").replaceAll("\\r","")+codeitemid.replaceAll("\\n", "").replaceAll("\\r",""));
	    		buf_codeitem.append("\",V:\"");
        		buf_codeitem.append(codeitemdesc.replaceAll("\\n", "").replaceAll("\\r","").replaceAll("\"", "\\\\\""));
        		
	    		buf_codeitem.append("\",I:\"");
        		buf_codeitem.append(invalid);
	    		buf_codeitem.append("\",VF:\"");
        		buf_codeitem.append(validateflag);
	    		buf_codeitem.append("\",S:\"");
        		buf_codeitem.append(startdate);
	    		buf_codeitem.append("\",E:\"");
        		buf_codeitem.append(enddate);
        		
        		buf_codeitem.append("\"");
    	    	buf_codeitem.append(",L:"+1);
    	    	buf_codeitem.append("};");
    	    	buf_codeitem.append("\r\n");
    	    	map.put((codesetid+codeitemid).toUpperCase(), "1");
				}
    	    	this.ffff(codesetid, codeitemid, parentid, buf_codeitem, 1,con,dao);
			}
			/**人才库*/
			rs=null;
			sql="select * from dbname order by dbid";
			rs=dao.search(sql);
			while(rs.next())
			{
				buf_codeitem.append("g_dm[g_dm.length] = ");
        		buf_codeitem.append("{ID:\"");
        		buf_codeitem.append("@@"+rs.getString("pre").toLowerCase());
        		buf_codeitem.append("\",V:\"");
        		buf_codeitem.append(rs.getString("dbname").replaceAll("\\n", "").replaceAll("\\r","").replaceAll("\"", "\\\\\""));
        		buf_codeitem.append("\"");
	        	buf_codeitem.append(",L:"+1);
	        	buf_codeitem.append("};");
	        	buf_codeitem.append("\r\n");
			}
			itemlist=null;
			parentlist=null;
			codesetmap=null;
			lll=null;
			mm=null;
		    rs.close();			
			//培训代码表 t_hr_relatingcode
			rs = null;
			itemlist=new ArrayList();
			parentlist = new ArrayList();
			codesetmap=new HashMap();
			lll = new ArrayList();
			mm=new HashMap();
			ArrayList tablelist=this.getT_hr_relatingcodeInfo(dao);
			this.getInfo(tablelist,con);
			this.mm=getMapT(itemlist);
			for(int j=0;j<parentlist.size();j++)
			{
				LazyDynaBean pbean=(LazyDynaBean)parentlist.get(j);
				String codesetid=(String)pbean.get("codesetid");
				String codeitemid=(String)pbean.get("codeitemid");
				String codeitemdesc=(String)pbean.get("codeitemdesc");
				String parentid=(String)pbean.get("parentid");
				if(this.map.get((codesetid+codeitemid).toUpperCase())==null)
				{
			    	buf_codeitem.append("g_dm[g_dm.length] = ");
            		buf_codeitem.append("{ID:\"");
	        		buf_codeitem.append(codesetid.replaceAll("\\n", "").replaceAll("\\r","")+codeitemid.replaceAll("\\n", "").replaceAll("\\r",""));
	        		buf_codeitem.append("\",V:\"");
            		buf_codeitem.append(codeitemdesc.replaceAll("\\n", "").replaceAll("\\r","").replaceAll("\"", "\\\\\""));
            		buf_codeitem.append("\"");
    	        	buf_codeitem.append(",L:"+1);
    	        	buf_codeitem.append("};");
    	        	buf_codeitem.append("\r\n");
    	        	map.put((codesetid+codeitemid).toUpperCase(), "1");
				}
    	    	this.ffff(codesetid, codeitemid, parentid, buf_codeitem, 1,con,dao);
			}
			buf_codeitem.append("for( var i=0 ; i<g_dm.length ; i++){");
			buf_codeitem.append("\r\n");
			buf_codeitem.append("   ");
			buf_codeitem.append("g_dm[\"_\"+"+"g_dm[i].ID]=g_dm[i];");
			buf_codeitem.append("\r\n");
			buf_codeitem.append("}");
			/**指标表*/
			rs = null;
			buf_fielditem.append(" var g_fm = new Array();");
			buf_fielditem.append("\r\n");
			sql = "select itemid,itemdesc,itemtype,codesetid,itemlength,decimalwidth from fielditem where useflag<>0 order by codesetid,itemid";
			rs = dao.search(sql);
			while(rs.next())
			{
				buf_fielditem.append("g_fm[g_fm.length]={ID:\"");
				buf_fielditem.append(rs.getString("itemid").replaceAll("\\n", "").replaceAll("\\r",""));
				buf_fielditem.append("\",V:\"");
				buf_fielditem.append(rs.getString("itemdesc").replaceAll("\\n", "").replaceAll("\\r","").replaceAll("\"", "\\\\\""));
				buf_fielditem.append("\",T:\"");
				buf_fielditem.append(rs.getString("itemtype").replaceAll("\\n", "").replaceAll("\\r",""));
				buf_fielditem.append("\",C:\"");
				buf_fielditem.append(rs.getString("codesetid").replaceAll("\\n", "").replaceAll("\\r",""));
				buf_fielditem.append("\",L:");
				buf_fielditem.append(rs.getInt("itemlength"));
				buf_fielditem.append(",D:");
				buf_fielditem.append(rs.getInt("decimalwidth"));
				buf_fielditem.append("};");
				buf_fielditem.append("\r\n");
			}
			itemlist=null;
			parentlist=null;
			codesetmap=null;
			lll=null;
			mm=null;
		    rs.close();			
			buf_fielditem.append("for( var i=0 ; i<g_fm.length ; i++){");
			buf_fielditem.append("\r\n");
			buf_fielditem.append("   ");
			buf_fielditem.append("g_fm[\"_\"+"+"g_fm[i].ID]=g_fm[i];");
			buf_fielditem.append("\r\n");
			buf_fielditem.append("}");
			StringBuffer buf = new StringBuffer();
			buf.append(buf_codeitem);
			buf.append("\r\n");
			buf.append(buf_fielditem);
			String filename="dict.js";
		    fileOut = new FileOutputStream(PubFunc.keyWord_reback(path)+System.getProperty("file.separator")+filename);
		    rs.close();
		    fc = fileOut.getChannel();
		    buffer = ByteBuffer.allocate(buf.toString().getBytes().length);
		    buffer.put(buf.toString().getBytes());
		    buffer.flip();
		    fc.write( buffer );  
			System.gc();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{

			if(buffer!=null)
				buffer.clear();
			PubFunc.closeResource(fileOut);
			PubFunc.closeResource(fc);
		}
		return n;
	}

	/**
	 * 刷新数据字典：将组织机构、代码和指标的数据写入到js文件中
	 * @param path dict.js文件所在目录在服务器磁盘上的的绝对路径
	 * @param con 数据库连接
	 * @throws GeneralException 如果发生了SQLException，或者人为抛出异常
	 * @author 刘蒙
	 */
	public void generateCodeTable(String path, Connection con) throws GeneralException {
		path = PubFunc.keyWord_reback(path);
		ContentDAO dao = new ContentDAO(con);
		RowSet rs = null;
		
		try {
			StringBuffer buf_codeitem = new StringBuffer();
			StringBuffer buf_fielditem = new StringBuffer();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

			Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(con);
			String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
			if (display_e0122 == null || "00".equals(display_e0122) || "".equals(display_e0122)) {
				display_e0122 = "0";
			}
			seprartor = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122, "sep");
			seprartor = seprartor != null && seprartor.length() > 0 ? seprartor : "/";
			
			// 删除原有的js文件
			File dict = new File(path + File.separator + "dict.js");
			File dict_org1 = new File(path + File.separator + "dict_org1.js");
			File dict_org2 = new File(path + File.separator + "dict_org2.js");
			File dict_org3 = new File(path + File.separator + "dict_org3.js");
			dict_code1 = new File(path + File.separator + "dict_code1.js");
			dict_code2 = new File(path + File.separator + "dict_code2.js");
			dict_code3 = new File(path + File.separator + "dict_code3.js");
			File dict_field = new File(path + File.separator + "dict_field.js");
			dict.delete();
			dict_org1.delete();
			dict_org2.delete();
			dict_org3.delete();
			dict_code1.delete();
			dict_code2.delete();
			dict_code3.delete();
			dict_field.delete();
			
			// #################################################### 组织机构表 ####################################################
			itemlist = new ArrayList();
			parentlist = new ArrayList();
			mm = new HashMap();

			StringBuffer orgSql = new StringBuffer();
			orgSql.append("SELECT o.codesetid,o.codeitemid,o.parentid,o.codeitemdesc,o.start_date,o.end_date,t.codesetid prt_set_id");
			orgSql.append(" FROM organization o,organization t WHERE o.parentid = t.codeitemid ORDER BY o.codesetid,o.codeitemid");
			rs = dao.search(orgSql.toString());
			while (rs.next()) {
				String _setId = rs.getString("codesetid");
				String _itemId = rs.getString("codeitemid");
				String _prtId = rs.getString("parentid");
				String _prtSetId = rs.getString("prt_set_id");
				
				if(_itemId == null || _itemId.trim().length() == 0) {
					continue;
				}
				
		    	LazyDynaBean bean = new LazyDynaBean();
		    	bean.set("codesetid", _setId);
		    	bean.set("codeitemid", _itemId);
		    	bean.set("parentid", _prtId == null ? "" : _prtId);
		    	bean.set("codeitemdesc", rs.getString("codeitemdesc") == null ? "" : rs.getString("codeitemdesc"));
		    	Date start_date = rs.getDate("start_date") == null ? sdf.parse("19000101") : rs.getDate("start_date");
				Date end_date = rs.getDate("end_date") == null ? sdf.parse("99991231") : rs.getDate("end_date");
				bean.set("startdate", sdf.format(start_date));
				bean.set("enddate", sdf.format(end_date));
				
				String _key = (_prtSetId + _prtId).toUpperCase();
				if (!_itemId.equals(_prtId)) {
			    	if (mm.get(_key) == null) {
		    			ArrayList lllll = new ArrayList();
						lllll.add(bean);
						mm.put(_key, lllll);
			    	} else {
			    		ArrayList lllll = (ArrayList) mm.get(_key);
				    	lllll.add(bean);
				    	mm.put(_key, lllll);
			    	}
				}
		    	itemlist.add(bean);
		    	if (rs.getString("parentid") == null || rs.getString("parentid").equals(rs.getString("codeitemid"))) {
		    		parentlist.add(bean);
		    	}
			}
			rs = null;
			
			for (int j=0, len = parentlist.size(); j < len; j++) {
				LazyDynaBean pbean = (LazyDynaBean) parentlist.get(j);
				String codesetid = (String) pbean.get("codesetid");
				String codeitemid = (String) pbean.get("codeitemid");
				String parentid = (String) pbean.get("parentid");
				
				pbean.set("L", 1 + "");
				
				if("UM".equalsIgnoreCase(codesetid)) {
					CodeItem item = AdminCode.getCode("UM",codeitemid,Integer.parseInt(display_e0122));
					String p = item.getCodename();
					pbean.set("P", p.replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\"", "\\\\\""));
				}
				
    	    	this.recursionOrg(codesetid, codeitemid, parentid, buf_codeitem, 1, con, dao, display_e0122);
			}
			parentlist.clear();
			mm.clear();
			
			// #################################################### 虚拟组织机构表 ####################################################
			StringBuffer vorgSql = new StringBuffer();
			vorgSql.append("SELECT o.codesetid,o.codeitemid,o.parentid,o.codeitemdesc,o.start_date,o.end_date,t.codesetid prt_set_id");
			vorgSql.append(" FROM vorganization o,vorganization t WHERE o.parentid = t.codeitemid ORDER BY o.codesetid,o.codeitemid");
			rs = dao.search(vorgSql.toString());
			while (rs.next()) {
				String _setId = rs.getString("codesetid");
				String _itemId = rs.getString("codeitemid");
				String _prtId = rs.getString("parentid");
				String _prtSetId = rs.getString("prt_set_id");
				
				if(_itemId == null || _itemId.trim().length() == 0) {
					continue;
				}
				
		    	LazyDynaBean bean = new LazyDynaBean();
		    	bean.set("codesetid", _setId);
		    	bean.set("codeitemid", _itemId);
		    	bean.set("parentid", _prtId == null ? "" : _prtId);
		    	bean.set("codeitemdesc", rs.getString("codeitemdesc")==null ? "" : rs.getString("codeitemdesc"));
		    	Date start_date = rs.getDate("start_date") == null ? sdf.parse("19000101") : rs.getDate("start_date");
				Date end_date = rs.getDate("end_date") == null ? sdf.parse("99991231") : rs.getDate("end_date");
				bean.set("startdate", sdf.format(start_date));
				bean.set("enddate", sdf.format(end_date));
				
				String _key = (_prtSetId + _prtId).toUpperCase();
				if (!_itemId.equals(_prtId)) {
			    	if (mm.get(_key) == null) {
			    		ArrayList lllll = new ArrayList();
						lllll.add(bean);
			    		mm.put(_key, lllll);
			    	} else {
			    		ArrayList lllll = (ArrayList) mm.get(_key);
				    	lllll.add(bean);
				    	mm.put(_key, lllll);
			    	}
		    	}
		    	itemlist.add(bean);
		    	if (rs.getString("parentid") == null || rs.getString("parentid").equals(rs.getString("codeitemid"))) {
		    		parentlist.add(bean);
		    	}
			}
			rs = null;
			
			for (int j=0, len = parentlist.size(); j < len; j++) {
				LazyDynaBean pbean = (LazyDynaBean) parentlist.get(j);
				String codesetid = (String) pbean.get("codesetid");
				String codeitemid = (String) pbean.get("codeitemid");
				String parentid = (String) pbean.get("parentid");
				
				pbean.set("L", 1 + "");
				
				if("UM".equalsIgnoreCase(codesetid)) {
					CodeItem item = AdminCode.getCode("UM",codeitemid,Integer.parseInt(display_e0122));
					String p = item.getCodename();
					pbean.set("P", p.replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\"", "\\\\\""));
				}
				
    	    	this.recursionOrg(codesetid, codeitemid, parentid, buf_codeitem, 1, con, dao, display_e0122);
			}
			parentlist.clear();
			mm.clear();
			
			// 向dict_orgX.js文件中写入数据,且保证org3个js都被创建
			int separator = 10000; // dict.js按多少条记录拆分
			for (int i = 0, len = itemlist.size(); i < len; i++) {
				LazyDynaBean pbean = (LazyDynaBean) itemlist.get(i);
				String codesetid = (String) pbean.get("codesetid");
				String codeitemid = (String) pbean.get("codeitemid");
				String codeitemdesc = (String) pbean.get("codeitemdesc");
				String startdate = (String) pbean.get("startdate");
				String enddate = (String) pbean.get("enddate");
				
				String _L = (String) pbean.get("L");
				String _P = (String) pbean.get("P");
				
				String _id = codesetid.replaceAll("\\n", "").replaceAll("\\r", "")
						+ codeitemid.replaceAll("\\n", "").replaceAll("\\r", "");
				buf_codeitem.append("g_dm[g_dm.length]=");
				buf_codeitem.append("{ID:\"").append(_id);
				buf_codeitem.append("\",V:\"");
				buf_codeitem.append(codeitemdesc.replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\"", "\\\\\""));
				buf_codeitem.append("\",S:\"");
				buf_codeitem.append(startdate);
				buf_codeitem.append("\",E:\"");
				buf_codeitem.append(enddate);
				buf_codeitem.append("\"");
				buf_codeitem.append(",L:" + _L);
				if ("UM".equalsIgnoreCase(codesetid)) {
					buf_codeitem.append(",P:\"");
					buf_codeitem.append(_P);
					buf_codeitem.append("\"");
				}
				buf_codeitem.append("};\r\n");
				
				if (len <= separator) {
					if (i == len - 1) {
						write(dict_org1, buf_codeitem.toString());
						buf_codeitem.setLength(0);
					}
					dict_org2.createNewFile();
					dict_org3.createNewFile();
				} else if (len > separator && len <= separator * 2) {
					if (i == separator - 1) {
						write(dict_org1, buf_codeitem.toString());
						buf_codeitem.setLength(0);
					} else if (i == len - 1) {
						write(dict_org2, buf_codeitem.toString());
						buf_codeitem.setLength(0);
					}
					dict_org3.createNewFile();
				} else {
					if (i == separator - 1) {
						write(dict_org1, buf_codeitem.toString());
						buf_codeitem.setLength(0);
					} else if (i == separator * 2 - 1) {
						write(dict_org2, buf_codeitem.toString());
						buf_codeitem.setLength(0);
					} else if (i == len - 1) {
						write(dict_org3, buf_codeitem.toString());
						buf_codeitem.setLength(0);
					}
				}
			}
			itemlist.clear();
			
			// #################################################### 代码表 ####################################################
			StringBuffer codeSql = new StringBuffer();
			codeSql.append("SELECT i.codesetid,i.codeitemid,i.parentid,i.codeitemdesc,i.invalid,i.start_date,i.end_date,s.validateflag");
			codeSql.append(" FROM codeitem i,CodeSet s WHERE i.codesetid = s.CodeSetId ORDER BY i.codesetid,i.a0000,i.codeitemid ");
			rs = dao.search(codeSql.toString());
			int maxlen=0;
			while(rs.next()) {
				String _setId = rs.getString("codesetid");
				String _itemId = rs.getString("codeitemid");
				String _prtId = rs.getString("parentid");
				
				if(_itemId == null || _itemId.trim().length() == 0) {
					continue;
				}
				
		    	LazyDynaBean bean = new LazyDynaBean();
		    	bean.set("codesetid", _setId);
		    	bean.set("codeitemid", _itemId);
		    	bean.set("parentid", _prtId == null ? "" : _prtId);
		    	bean.set("codeitemdesc", rs.getString("codeitemdesc")==null ? "" : rs.getString("codeitemdesc"));
		    	bean.set("invalid", rs.getString("invalid") == null?"0":rs.getString("invalid"));
		    	bean.set("validateflag", rs.getString("validateflag") == null ? "0" : rs.getString("validateflag"));
		    	Date start_date = rs.getDate("start_date") == null ? sdf.parse("19000101") : rs.getDate("start_date");
				Date end_date = rs.getDate("end_date") == null ? sdf.parse("99991231") : rs.getDate("end_date");
				bean.set("startdate", sdf.format(start_date));
				bean.set("enddate", sdf.format(end_date));
				
				String _key = (_setId + _prtId).toUpperCase();
				if (!_itemId.equals(_prtId)) {
			    	if (mm.get(_key) == null) {
		    			ArrayList lllll = new ArrayList();
						lllll.add(bean);
						mm.put(_key, lllll);
			    	} else {
			    		ArrayList lllll = (ArrayList) mm.get(_key);
				    	lllll.add(bean);
				    	mm.put(_key, lllll);
			    	}
		    	}
		    	itemlist.add(bean);
		    	if (rs.getString("parentid") == null || rs.getString("parentid").equals(rs.getString("codeitemid"))) {
		    		parentlist.add(bean);
		    	}
			}
			rs = null;
			
			for (int j=0, len = parentlist.size(); j < len; j++) {
				LazyDynaBean pbean = (LazyDynaBean) parentlist.get(j);
				String codesetid = (String) pbean.get("codesetid");
				String codeitemid = (String) pbean.get("codeitemid");
				String codeitemdesc = (String) pbean.get("codeitemdesc");
				String parentid = (String) pbean.get("parentid");
				String invalid = (String) pbean.get("invalid");
				String validateflag = (String) pbean.get("validateflag");
				String startdate = (String) pbean.get("startdate");
				String enddate = (String) pbean.get("enddate");
				
				buf_codeitem.append("g_dm[g_dm.length]={ID:\"");
	    		buf_codeitem.append(codesetid.replaceAll("\\n", "").replaceAll("\\r", "")+codeitemid.replaceAll("\\n", "").replaceAll("\\r", ""));
	    		buf_codeitem.append("\",V:\"");
        		buf_codeitem.append(codeitemdesc.replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\"", "\\\\\""));
	    		buf_codeitem.append("\",I:\"");
        		buf_codeitem.append(invalid);
	    		buf_codeitem.append("\",VF:\"");
        		buf_codeitem.append(validateflag);
	    		buf_codeitem.append("\",S:\"");
        		buf_codeitem.append(startdate);
	    		buf_codeitem.append("\",E:\"");
        		buf_codeitem.append(enddate);
        		buf_codeitem.append("\",L:"+1);
    	    	buf_codeitem.append("};\r\n");
                this.writeCodeJs(buf_codeitem,false);
    	    	this.ffff(codesetid, codeitemid, parentid, buf_codeitem, 1, con, dao);
			}
			//buf_codeitem.append("\r\n");
			
			parentlist.clear();
			mm.clear();
			itemlist.clear();
			
			// #################################################### 人才库 ####################################################
			String dbnameSql = "select pre,dbname from dbname order by dbid";
			rs = dao.search(dbnameSql);
			while (rs.next()) {
				buf_codeitem.append("g_dm[g_dm.length]=");
				buf_codeitem.append("{ID:\"");
				buf_codeitem.append("@@").append(rs.getString("pre").toLowerCase());
				buf_codeitem.append("\",V:\"");
				buf_codeitem.append(rs.getString("dbname").replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\"", "\\\\\""));
				buf_codeitem.append("\"");
				buf_codeitem.append(",L:" + 1);
				buf_codeitem.append("};\r\n");
                this.writeCodeJs(buf_codeitem,false);
			}
			rs = null;
			//buf_codeitem.append("\r\n");
			
			// #################################################### 培训代码表 t_hr_relatingcode ####################################################
			map = new HashMap();
			itemlist.clear();
			parentlist.clear();
			lll = new ArrayList();
			codesetmap = new HashMap();
			mm.clear();
			ArrayList tablelist = this.getT_hr_relatingcodeInfo(dao);
			this.getInfo(tablelist,con);
			this.mm = getMapT(itemlist);
			for (int j = 0, len = parentlist.size(); j < len; j++) {
				LazyDynaBean pbean = (LazyDynaBean) parentlist.get(j);
				String codesetid = (String) pbean.get("codesetid");
				String codeitemid = (String) pbean.get("codeitemid");
				String codeitemdesc = (String) pbean.get("codeitemdesc");
				String parentid = (String) pbean.get("parentid");
				if (this.map.get((codesetid + codeitemid).toUpperCase()) == null) {
					buf_codeitem.append("g_dm[g_dm.length]=");
					buf_codeitem.append("{ID:\"");
					buf_codeitem.append(codesetid.replaceAll("\\n", "").replaceAll("\\r", ""));
					buf_codeitem.append(codeitemid.replaceAll("\\n", "").replaceAll("\\r", ""));
					buf_codeitem.append("\",V:\"");
					buf_codeitem.append(codeitemdesc.replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\"", "\\\\\""));
					buf_codeitem.append("\"");
					buf_codeitem.append(",L:" + 1);
					buf_codeitem.append("};");
					buf_codeitem.append("\r\n");
                    this.writeCodeJs(buf_codeitem,false);
					map.put((codesetid + codeitemid).toUpperCase(), "1");
				}
				this.ffff(codesetid, codeitemid, parentid, buf_codeitem, 1, con, dao);

			}




            this.writeCodeJs(buf_codeitem,true);
			buf_codeitem = null;

			itemlist = null;
			parentlist = null;
			codesetmap = null;
			lll = null;
			mm = null;
			map = null;
			
			// #################################################### 指标表 ####################################################
			String fieldSql = "select itemid,itemdesc,itemtype,codesetid,itemlength,decimalwidth from fielditem where useflag<>0 order by codesetid,itemid";
			rs = dao.search(fieldSql);
			while (rs.next()) {
				buf_fielditem.append("g_fm[g_fm.length]={ID:\"");
				buf_fielditem.append(rs.getString("itemid").replaceAll("\\n", "").replaceAll("\\r", ""));
				buf_fielditem.append("\",V:\"");
				buf_fielditem.append(rs.getString("itemdesc").replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\"", "\\\\\""));
				buf_fielditem.append("\",T:\"");
				buf_fielditem.append(rs.getString("itemtype").replaceAll("\\n", "").replaceAll("\\r", ""));
				buf_fielditem.append("\",C:\"");
				buf_fielditem.append(rs.getString("codesetid").replaceAll("\\n", "").replaceAll("\\r", ""));
				buf_fielditem.append("\",L:");
				buf_fielditem.append(rs.getInt("itemlength"));
				buf_fielditem.append(",D:");
				buf_fielditem.append(rs.getInt("decimalwidth"));
				buf_fielditem.append("};\r\n");
			}
			rs = null;
			buf_fielditem.append("\r\n");
			buf_fielditem.append("for(var i=0;i<g_fm.length;i++){");
			buf_fielditem.append("\r\n\t");
			buf_fielditem.append("g_fm[\"_\"+"+"g_fm[i].ID]=g_fm[i];");
			buf_fielditem.append("\r\n");
			buf_fielditem.append("}\r\n");
			
			write(dict_field, buf_fielditem.toString());
			
			// dict.js,放在最后是为了防止其他js文件生成失败而引用了不存在的js文件，而导致IE这坨屎报错
			StringBuffer buf_dict = new StringBuffer(); // 在dict.js引入其他js
			buf_dict.append("var g_dm=new Array();\r\n");
			buf_dict.append("var g_fm=new Array();\r\n");
			buf_dict.append("document.write(\"<script type='text/javascript' src='/js/dict_org1.js'></script>\");\r\n");
			buf_dict.append("document.write(\"<script type='text/javascript' src='/js/dict_org2.js'></script>\");\r\n");
			buf_dict.append("document.write(\"<script type='text/javascript' src='/js/dict_org3.js'></script>\");\r\n");
			buf_dict.append("document.write(\"<script type='text/javascript' src='/js/dict_code1.js'></script>\");\r\n");
            buf_dict.append("document.write(\"<script type='text/javascript' src='/js/dict_code2.js'></script>\");\r\n");
            buf_dict.append("document.write(\"<script type='text/javascript' src='/js/dict_code3.js'></script>\");\r\n");
			buf_dict.append("document.write(\"<script type='text/javascript' src='/js/dict_field.js'></script>\");\r\n");
			write(dict, buf_dict.toString());
			
		} catch (Exception e) {
			throw new GeneralException(e.getMessage());
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private File dict_code1=null;
    private File dict_code2=null;
    private File dict_code3=null;
	private void writeCodeJs(StringBuffer buf_codeitem,boolean isEnd){
	    try{
	        int separator=7000;
            if (codeItemPoint == separator - 1||(isEnd&&codeItemPoint <= separator - 1)) {
                if(isEnd){
                    buf_codeitem.append("\r\n");
                    buf_codeitem.append("for(var i=0;i<g_dm.length;i++){");
                    buf_codeitem.append("\r\n\t");
                    buf_codeitem.append("g_dm[\"_\"+" + "g_dm[i].ID]=g_dm[i];");
                    buf_codeitem.append("\r\n");
                    buf_codeitem.append("}\r\n");
                    dict_code2.createNewFile();
                    dict_code3.createNewFile();
                }
                write(dict_code1, buf_codeitem.toString());
                buf_codeitem.setLength(0);
            } else if (codeItemPoint == separator * 2 - 1||(isEnd&&codeItemPoint <= separator * 2 - 1)) {
                if(isEnd){
                    buf_codeitem.append("\r\n");
                    buf_codeitem.append("for(var i=0;i<g_dm.length;i++){");
                    buf_codeitem.append("\r\n\t");
                    buf_codeitem.append("g_dm[\"_\"+" + "g_dm[i].ID]=g_dm[i];");
                    buf_codeitem.append("\r\n");
                    buf_codeitem.append("}\r\n");
                    dict_code3.createNewFile();
                }
                write(dict_code2, buf_codeitem.toString());
                buf_codeitem.setLength(0);
            } else if (isEnd) {
                buf_codeitem.append("\r\n");
                buf_codeitem.append("for(var i=0;i<g_dm.length;i++){");
                buf_codeitem.append("\r\n\t");
                buf_codeitem.append("g_dm[\"_\"+" + "g_dm[i].ID]=g_dm[i];");
                buf_codeitem.append("\r\n");
                buf_codeitem.append("}\r\n");
                write(dict_code3, buf_codeitem.toString());
                buf_codeitem.setLength(0);
            }
            codeItemPoint++;

        }catch (Exception e){
	        e.printStackTrace();
        }
    }
	
	/**
	 * 查找参数codeitemid节点的一级子节点,参数与recursionLoop相同
	 * @author 刘蒙
	 */
	private void recursionOrg(String codesetid, String codeitemid, String parentid, StringBuffer buf_codeitem, int lay, Connection con,
			ContentDAO dao, String display_e0122) {
		try {
			ArrayList list = (ArrayList) mm.get((codesetid+codeitemid).toUpperCase());
			if(list==null || list.size() == 0) {
				return;
			}
			lay++;
			for(int i = 0; i < list.size(); i++) {
				LazyDynaBean pbean = (LazyDynaBean) list.get(i);
				String codeset_id = (String) pbean.get("codesetid");
				String codeitem_id = (String) pbean.get("codeitemid");
				String codeitemdesc = (String) pbean.get("codeitemdesc");
				
	        	pbean.set("L", lay + "");
	        	
	        	if("UM".equalsIgnoreCase(codeset_id)) {
	        		CodeItem item = AdminCode.getCode("UM",codeitem_id,Integer.parseInt(display_e0122));
	        		if (item != null && !"0".equals(display_e0122)) {
	        	    	String p = item.getCodename();
	        		    pbean.set("P", p.replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\"", "\\\\\""));
	        		} else if(isOrg && !"0".equals(display_e0122)) {
	        			ArrayList beanlist = new ArrayList();
	        			String parent = (String) pbean.get("parentid");
	        			this.getUpperParent(parent, Integer.parseInt(display_e0122), 0, beanlist);
	        			StringBuffer sb = new StringBuffer("");
	        			for(int xx = beanlist.size() - 1; xx >= 0; xx--) {
	        				LazyDynaBean ab=(LazyDynaBean)beanlist.get(xx);
	        				sb.append((String) ab.get("codeitemdesc"));
	        				sb.append(seprartor);
	        			}
	        			sb.append(codeitemdesc);
	        			pbean.set("P", sb.toString().replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\"", "\\\\\""));
	        		} else {
	        			pbean.set("P", codeitemdesc.replaceAll("\\n", "").replaceAll("\\r","").replaceAll("\"", "\\\\\""));
	        		}
	        	}
				if (mm.get((codeset_id+codeitem_id).toUpperCase())==null||((ArrayList)mm.get((codeset_id+codeitem_id).toUpperCase())).size()==0) {
					continue;
				} else {
	    			this.recursionOrg(codeset_id, codeitem_id, parentid, buf_codeitem, lay, con, dao, display_e0122);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 将字符串写入到文件中
	 * @param dest 将要写入数据的文件
	 * @param content 要写入的内容
	 * @throws Exception 如果发生了IOExcepion
	 * @author 刘蒙
	 */
	public void write(File dest, String content) throws Exception {
		FileOutputStream fileOut = null;
		ByteBuffer buffer = null;
		FileChannel fc = null;
		try {
			fileOut = new FileOutputStream(dest);
			fc = fileOut.getChannel();
			byte[] b = content.getBytes("UTF-8");
			buffer = ByteBuffer.allocate(b.length);
			buffer.put(b);
			buffer.flip();
			fc.write(buffer);
			System.gc();
		} catch (Exception e) {
			throw e;
		} finally {
			if (buffer != null)
				buffer.clear();
			PubFunc.closeResource(fileOut);
			PubFunc.closeResource(fc);
		}
	}
	
	/**
	 * 生成新增机构的js
	 * @param mappingStr
	 * @param con
	 * @return
	 */
	public String getNewOrgInfo(String mappingStr,Connection con)
	{
		StringBuffer buf_codeitem=new StringBuffer("");
		try
		{
			ContentDAO dao = new ContentDAO(con);
			String str="";
			String[] mappings=mappingStr.split(",");
			for(int i=0;i<mappings.length;i++)
			{
				if(mappings[i].trim().length()>0)
				{
					String[] temp=mappings[i].split("=");
					str+=",'"+temp[1]+"'";
				}
			}
			if(str.length()>0)
			{
				RowSet rowSet=dao.search("select * from organization where codeitemid in ("+str.substring(1)+") order by codeitemid ");
				String codesetid="";
				String codeitemid="";
				String codeitemdesc="";
				String id="";
				while(rowSet.next())
				{
					codesetid=rowSet.getString("codesetid");
					codeitemid=rowSet.getString("codeitemid");
					codeitemdesc=rowSet.getString("codeitemdesc");
					id=codesetid.replaceAll("\\n", "").replaceAll("\\r","")+codeitemid.replaceAll("\\n", "").replaceAll("\\r","");
					buf_codeitem.append("g_dm[g_dm.length] = ");
            		buf_codeitem.append("{ID:\"");
	        		buf_codeitem.append(id);
	        		buf_codeitem.append("\",V:\"");
            		buf_codeitem.append(codeitemdesc.replaceAll("\\n", "").replaceAll("\\r","").replaceAll("\"", "\\\\\""));
            		buf_codeitem.append("\"");
    	        	buf_codeitem.append(",L:"+1);
    	        	if("UM".equalsIgnoreCase(codesetid))
    	        	{
    	        		buf_codeitem.append(",P:\"\"");
    	        	}
    	        	buf_codeitem.append("};");
    	        	buf_codeitem.append("\r\n");

    	        	buf_codeitem.append("g_dm[\"_"+id+"\"] =");
    	        	buf_codeitem.append("{ID:\"");
	        		buf_codeitem.append(id);
	        		buf_codeitem.append("\",V:\"");
            		buf_codeitem.append(codeitemdesc.replaceAll("\\n", "").replaceAll("\\r","").replaceAll("\"", "\\\\\""));
            		buf_codeitem.append("\"");
    	        	buf_codeitem.append(",L:"+1);
    	        	if("UM".equalsIgnoreCase(codesetid))
    	        	{
    	        		buf_codeitem.append(",P:\"\"");
    	        	}
    	        	buf_codeitem.append("};");
    	        	buf_codeitem.append("\r\n");
				}
				if(rowSet!=null)
					rowSet.close();
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return buf_codeitem.toString();
	}
	
	
	
	public void recursionLoop(String codesetid,String codeitemid,String parentid,StringBuffer buf_codeitem,int lay,Connection con,ContentDAO dao,String display_e0122)
	{
		try
		{
			ArrayList list = (ArrayList)mm.get((codesetid+codeitemid).toUpperCase());
			if(list==null||list.size()==0)
				return;
			lay++;
			for(int i=0;i<list.size();i++)
			{
				
				LazyDynaBean pbean=(LazyDynaBean)list.get(i);
				String codeset_id=(String)pbean.get("codesetid");
				String codeitem_id=(String)pbean.get("codeitemid");
				String codeitemdesc=(String)pbean.get("codeitemdesc");
				String startdate=(String)pbean.get("startdate");
				String enddate=(String)pbean.get("enddate");
				if(this.map.get((codeset_id+codeitem_id).toUpperCase())==null)
				{
		    		buf_codeitem.append("g_dm[g_dm.length] = ");
            		buf_codeitem.append("{ID:\"");
	        		buf_codeitem.append(codeset_id.replaceAll("\\n", "").replaceAll("\\r","")+codeitem_id.replaceAll("\\n", "").replaceAll("\\r",""));
	    	    	buf_codeitem.append("\",V:\"");
        	    	buf_codeitem.append(codeitemdesc.replaceAll("\\n", "").replaceAll("\\r","").replaceAll("\"", "\\\\\""));
        	    	
	        		buf_codeitem.append("\",S:\"");
            		buf_codeitem.append(startdate);
	        		buf_codeitem.append("\",E:\"");
            		buf_codeitem.append(enddate);
            		buf_codeitem.append("\"");
        	    	
    	        	buf_codeitem.append(",L:"+lay);
    	        	if("UM".equalsIgnoreCase(codeset_id))
    	        	{
    	        		CodeItem item=AdminCode.getCode("UM",codeitem_id,Integer.parseInt(display_e0122));
    	        		if(item!=null&&!"0".equals(display_e0122))
    	        		{
    	        	    	String p=item.getCodename();
    	        		/*if(p.lastIndexOf("/")!=-1)
    	        			p=p.substring(0,p.lastIndexOf("/"));
    	        		if(p.replaceAll("\\n", "").replaceAll("\\r","").replaceAll("\"", "\\\\\"").equalsIgnoreCase(codeitemdesc.replaceAll("\\n", "").replaceAll("\\r","").replaceAll("\"", "\\\\\"")))
    	        			p="";*/
    	        		    buf_codeitem.append(",P:\""+p.replaceAll("\\n", "").replaceAll("\\r","").replaceAll("\"", "\\\\\"")+"\"");
    	        		}
    	        		else if(isOrg&&!"0".equals(display_e0122))
    	        		{
    	        			ArrayList beanlist = new ArrayList();
    	        			String parent = (String)pbean.get("parentid");
    	        			this.getUpperParent(parent, Integer.parseInt(display_e0122), 0, beanlist);
    	        			StringBuffer sb=new StringBuffer("");
    	        			for(int xx=beanlist.size()-1;xx>=0;xx--)
    	        			{
    	        				LazyDynaBean ab=(LazyDynaBean)beanlist.get(xx);
    	        				sb.append((String)ab.get("codeitemdesc"));
    	        				sb.append(seprartor);
    	        			}
    	        			sb.append(codeitemdesc);
    	        			 buf_codeitem.append(",P:\""+sb.toString().replaceAll("\\n", "").replaceAll("\\r","").replaceAll("\"", "\\\\\"")+"\"");
    	        		}
    	        		else
    	        		{
    	        			 buf_codeitem.append(",P:\""+codeitemdesc.replaceAll("\\n", "").replaceAll("\\r","").replaceAll("\"", "\\\\\"")+"\"");
    	        		}
    	        	}
    	        	buf_codeitem.append("};");
    	        	buf_codeitem.append("\r\n");
    	        	map.put((codeset_id+codeitem_id).toUpperCase(), "1");
				}
				if(mm.get((codeset_id+codeitem_id).toUpperCase())==null||((ArrayList)mm.get((codeset_id+codeitem_id).toUpperCase())).size()==0)
				{
					continue;
					
				}else{
	    			this.recursionLoop(codeset_id, codeitem_id, parentid, buf_codeitem, lay,con,dao,display_e0122);
				}
    	    	
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public void ffff(String codesetid,String codeitemid,String parentid,StringBuffer buf_codeitem,int lay,Connection con,ContentDAO dao)
	{
		try
		{
			ArrayList list = (ArrayList)mm.get((codesetid+codeitemid).toUpperCase());
			if(list==null||list.size()==0)
				return;
			lay++;
			for(int i=0;i<list.size();i++)
			{
				
				LazyDynaBean pbean=(LazyDynaBean)list.get(i);
				String codeset_id=(String)pbean.get("codesetid");
				String codeitem_id=(String)pbean.get("codeitemid");
				String codeitemdesc=(String)pbean.get("codeitemdesc");
				
				String invalid=(String)pbean.get("invalid");
				String validateflag=(String)pbean.get("validateflag");
				String startdate=(String)pbean.get("startdate");
				String enddate=(String)pbean.get("enddate");
				
	    		buf_codeitem.append("g_dm[g_dm.length]=");
        		buf_codeitem.append("{ID:\"");
        		buf_codeitem.append(codeset_id.replaceAll("\\n", "").replaceAll("\\r","")+codeitem_id.replaceAll("\\n", "").replaceAll("\\r",""));
    	    	buf_codeitem.append("\",V:\"");
    	    	buf_codeitem.append(codeitemdesc.replaceAll("\\n", "").replaceAll("\\r","").replaceAll("\"", "\\\\\""));
    	    	
	    		buf_codeitem.append("\",I:\"");
        		buf_codeitem.append(invalid);
	    		buf_codeitem.append("\",VF:\"");
        		buf_codeitem.append(validateflag);
	    		buf_codeitem.append("\",S:\"");
        		buf_codeitem.append(startdate);
	    		buf_codeitem.append("\",E:\"");
        		buf_codeitem.append(enddate);
    	    	
    	    	buf_codeitem.append("\"");
	        	buf_codeitem.append(",L:"+lay);
	        	buf_codeitem.append("};");
	        	buf_codeitem.append("\r\n");
                this.writeCodeJs(buf_codeitem,false);
	        	if(mm.get((codeset_id+codeitem_id).toUpperCase())==null||((ArrayList)mm.get((codeset_id+codeitem_id).toUpperCase())).size()==0)
				{
					continue;
					
				}else{
	    			this.ffff(codeset_id, codeitem_id, parentid, buf_codeitem, lay,con,dao);
				}
    	    	
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public  HashMap getLeafItemLinkMap(ArrayList list,String tablename)
	{
		HashMap map=new HashMap();
		try
		{
			LazyDynaBean abean=null;
			for(int i=0;i<list.size();i++)
			{
				abean=(LazyDynaBean)list.get(i);
				String item_id=(String)abean.get("codeitemid");
				String parent_id=(String)abean.get("parentid");
				String codesetid=(String)abean.get("codesetid");
				ArrayList linkList=new ArrayList();
				getParentItem(linkList,abean,tablename);
				map.put((codesetid+item_id).toUpperCase(),linkList);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	public void getParentItem(ArrayList list,LazyDynaBean abean,String tablename)
	{
		String item_id=(String)abean.get("codeitemid");
		String parent_id=(String)abean.get("parentid");
		String codeset_id=(String)abean.get("codesetid");
		LazyDynaBean a_bean=null;
		String sstr="";
		if("organization".equalsIgnoreCase(tablename))
			sstr=item_id;
		else
			sstr=codeset_id;
		ArrayList llll=(ArrayList)codesetmap.get(sstr.toUpperCase());
		if(llll==null||llll.size()==0)
			return;
		/***原先循环的事itemlist*/
		for(int i=0;i<llll.size();i++)
		{
			a_bean=(LazyDynaBean)llll.get(i);
			String itemid=(String)a_bean.get("codeitemid");
			String parentid=(String)a_bean.get("parentid");
			String codesetid=(String)a_bean.get("codesetid");
			if("organization".equalsIgnoreCase(tablename))
			{
				if(item_id.equals(parentid)&&!itemid.equals(parentid))
	    		{
	    			list.add(a_bean);
	    			getParentItem(list,a_bean,tablename);
	    		}
			}
			else
			{
	    		if(item_id.equals(parentid)&&!itemid.equals(parentid)&&codeset_id.equalsIgnoreCase(codesetid))
	    		{
	    			list.add(a_bean);
	    			getParentItem(list,a_bean,tablename);
	    		}
			}
		}				
	}
		
	public HashMap getInfo(ArrayList list,Connection conn)
	{
		HashMap map = new HashMap();
		try
		{
			ContentDAO dao = new ContentDAO(conn);
			RowSet rs = null;
			for(int i=0;i<list.size();i++)
			{
				LazyDynaBean bean =(LazyDynaBean)list.get(i);
				String codesetid=(String)bean.get("codesetid");
				String codetable=(String)bean.get("codetable");
				String codevalue=(String)bean.get("codevalue");
				String codedesc=(String)bean.get("codedesc");
				String upcodevalue=(String)bean.get("upcodevalue");
				String status=(String)bean.get("status");
				StringBuffer sql = new StringBuffer();
				sql.append("select "+codevalue+","+codedesc+","+upcodevalue+" from "+codetable);
				rs = dao.search(sql.toString());
				ArrayList maplist = new ArrayList();
				while(rs.next())
				{
					LazyDynaBean abean = new LazyDynaBean();
			    	abean.set("codesetid","1_"+codesetid);
			    	abean.set("codeitemid",rs.getString(codevalue));
			    	abean.set("parentid",(rs.getString(upcodevalue)==null||rs.getString(codevalue).equalsIgnoreCase(rs.getString(upcodevalue)))?"":rs.getString(upcodevalue));
			    	abean.set("codeitemdesc",rs.getString(codedesc)==null?"":rs.getString(codedesc));
			    	maplist.add(abean);
			    	if(!codevalue.equalsIgnoreCase(upcodevalue))
			         	itemlist.add(abean);
			    	if(rs.getString(upcodevalue)==null|| "".equals(rs.getString(upcodevalue))||codevalue.equalsIgnoreCase(upcodevalue))
			    	{
			    		parentlist.add(abean);
			    	}
			    	else
			    	{
			    		lll.add(abean);
			    	}
				}
				rs.close();
				this.codesetmap.put(("1_"+codesetid).toUpperCase(), maplist);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	public ArrayList getT_hr_relatingcodeInfo(ContentDAO dao)
	{
		ArrayList list= new ArrayList();
		try
		{
			String sql = " select * from t_hr_relatingcode";
			RowSet rs = null;
			rs = dao.search(sql);
			while(rs.next())
			{
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("codesetid",rs.getString("codesetid"));
				bean.set("codetable",rs.getString("codetable"));
				bean.set("codevalue",rs.getString("codevalue"));
				bean.set("codedesc",rs.getString("codedesc"));
				bean.set("upcodevalue",rs.getString("upcodevalue"));
				bean.set("status",rs.getString("status"));
				list.add(bean);
			}
			rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	public HashMap getMapT(ArrayList list)
	{
		HashMap map = new HashMap();
		try
		{
			LazyDynaBean abean=null;
			for(int i=0;i<list.size();i++)
			{
				abean=(LazyDynaBean)list.get(i);
				String item_id=(String)abean.get("codeitemid");
				String parent_id=(String)abean.get("parentid");
				String codesetid=(String)abean.get("codesetid");
				ArrayList linkList=new ArrayList();
				getParentItemT(linkList,abean);
				map.put((codesetid+item_id).toUpperCase(),linkList);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	public void getParentItemT(ArrayList list,LazyDynaBean abean)
	{
		try
		{
    		String item_id=(String)abean.get("codeitemid");
	    	String parent_id=(String)abean.get("parentid");
	    	String codeset_id=(String)abean.get("codesetid");
	     	LazyDynaBean a_bean=null;
	    	ArrayList llll=(ArrayList)codesetmap.get(codeset_id.toUpperCase());
	    	if(llll==null||llll.size()==0)
    			return;
	    	for(int i=0;i<llll.size();i++)
	    	{
	    		a_bean=(LazyDynaBean)llll.get(i);
	    		String itemid=(String)a_bean.get("codeitemid");
	    		String parentid=(String)a_bean.get("parentid");
		    	String codesetid=(String)a_bean.get("codesetid");
		    	if(item_id.equals(parentid))
	        	{
	         		list.add(a_bean);
	        		getParentItemT(list,a_bean);
	        	}
    		}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public void getUpperParent(String parent,int display,int lay,ArrayList list)
	{
		if(lay==display)
			return;
		for(int i=0;i<itemlist.size();i++)
		{
			LazyDynaBean bean = (LazyDynaBean)itemlist.get(i);
			String codeitemid=(String)bean.get("codeitemid");
			String codesetid=(String)bean.get("codesetid");
			if("UN".equalsIgnoreCase(codesetid))
				return;
			if(codeitemid.equalsIgnoreCase(parent))
			{
				list.add(bean);
				lay++;
				String parentid=(String)bean.get("parentid");
				this.getUpperParent(parentid, display, lay, list);
			}
			else
				continue;
		}
	}
}
