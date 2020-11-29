package com.hjsj.hrms.businessobject.general.inform.e_archive;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

public class E_ArchiveBo {
	private Connection conn;
	public E_ArchiveBo()
	{
		super();
	}
	public E_ArchiveBo(Connection conn)
	{
		this.conn=conn;
	}
	/**
	 * 判断人员基本情况子集是否构建档案号指标
	 * @return
	 */
	public boolean a01HasArchiveNoField()
	{
		boolean flag=false;
		try
		{
			StringBuffer sql = new StringBuffer();
			sql.append("select itemid from fielditem where fieldsetid='a01' and itemdesc='");
			sql.append(ResourceFactory.getProperty("workdiary.message.file.no"));
			sql.append("' and useflag=1");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs=null;
			rs=dao.search(sql.toString());
			while(rs.next())
			{
				flag=true;
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
	/**
	 * 分析选择的目录子集是否创建浏览档案所需指标
	 * @param fieldsetid 子集id
	 * @param itemdesc 固定指标的汉字名
	 * @return
	 */
	public boolean hasArchiveField(ArrayList list,String itemdesc)
	{
		boolean flag=false;
		try
		{
			for(int i=0;i<list.size();i++)
			{
				FieldItem item = (FieldItem)list.get(i);
				if(item.getItemdesc().equalsIgnoreCase(itemdesc)&& "1".equalsIgnoreCase(item.getUseflag()))
				{
					flag=true;
					break;
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
	/**
	 * 根据指标的汉字名称得到指标的itemid
	 * @param fieldsetid
	 * @param itemdesc
	 * @return
	 */
	public String getArchiveItemid(ArrayList list,String itemdesc)
	{
		String itemid="";
		try
		{
			for(int i=0;i<list.size();i++)
			{
				FieldItem item = (FieldItem)list.get(i);
				
				if(item.getItemdesc().equalsIgnoreCase(itemdesc)&& "1".equalsIgnoreCase(item.getUseflag()))
				{
					itemid=item.getItemid();
					break;
				}
			}
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return itemid;
	}
    /**
     * 取得档案浏览目录列表
     * @param litemid 类号指标id
     * @param citemid 材料名称指标id
     * @param yitemid  年指标id
     * @param mitemid 月指标id
     * @param ditemid 日指标id
     * @param sitemid 份数指标id
     * @param pitemid 页数指标id
     * @param tableName  表名
     * @param a0100 登录用户id
     * @return ArrayList
     */
	public ArrayList getArchiveList(String litemid,String citemid,String yitemid,String mitemid,String ditemid,
			String sitemid,String pitemid,String fitemid,String tableName,String a0100,UserView view)
	{
		ArrayList list = new ArrayList();
		try
		{
			StringBuffer buf = new StringBuffer();
			buf.append("select ");
			buf.append(litemid);
			buf.append(","+citemid);
			buf.append(","+yitemid);
			buf.append(","+mitemid);
			buf.append(","+ditemid);
			buf.append(","+sitemid);
			buf.append(","+pitemid);
			buf.append(","+fitemid+",a0100 from ");
			buf.append(tableName);
			buf.append(" where a0100='");
			buf.append(a0100+"' order by "+litemid);
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs=null;
			rs=dao.search(buf.toString());
			String archivetype=SystemConfig.getPropertyValue("archivetype");
			if(archivetype==null|| "".equals(archivetype.trim())) {
                archivetype="XB";
            }
			while(rs.next())
			{
				LazyDynaBean bean = new LazyDynaBean();
				if(!(rs.getString(litemid)==null|| "".equals(rs.getString(litemid))))
				{
		    		if(view.isHaveResource(IResourceConstant.ARCH_TYPE, rs.getString(litemid)))
		    		{
	    	    		bean.set("typeid",rs.getString(litemid)==null?"":AdminCode.getCodeName(archivetype,rs.getString(litemid)));
		        		bean.set("archivename",rs.getString(citemid)==null?"":rs.getString(citemid));
		         		bean.set("year",rs.getString(yitemid)==null?"":rs.getString(yitemid));
		        		bean.set("month",rs.getString(mitemid)==null?"":rs.getString(mitemid));
		        		bean.set("day",rs.getString(ditemid)==null?"":rs.getString(ditemid));
			        	bean.set("share",rs.getString(sitemid)==null?"":rs.getString(sitemid));
			        	bean.set("page", rs.getString(pitemid)==null?"":rs.getString(pitemid));
			        	String filename=rs.getString(fitemid)==null?"":rs.getString(fitemid);
			        	/**\0001~9999\1004\10040101.Tif,\传输过程会出现问题,chenmengqing*/
			        	filename=SafeCode.encode(filename);
			        	bean.set("filename", filename);
		        		bean.set("a0100",rs.getString("a0100"));
		        		bean.set("nbase",tableName.substring(0,3));
		    	    	bean.set("itemid",rs.getString(litemid)==null?"":rs.getString(litemid));
		    	    	list.add(bean);
		    		}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 取得人员的单位,部门,姓名
	 * @param pre
	 * @param a0100
	 * @return
	 */
	public HashMap getUNAndUMAndName(String pre,String a0100)
	{
		HashMap map = new HashMap();
		try
		{
			StringBuffer sql = new StringBuffer();
			sql.append("select b0110,e0122,a0101 from ");
			sql.append(pre+"a01");
			sql.append(" where a0100='");
			sql.append(a0100+"'");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs=null;
			rs=dao.search(sql.toString());
			while(rs.next())
			{
				map.put("b0110",rs.getString("b0110")==null?"":AdminCode.getCodeName("UN",rs.getString("b0110")));
				map.put("a0101",rs.getString("a0101"));
				map.put("e0122",rs.getString("e0122")==null?"":AdminCode.getCodeName("UM",rs.getString("e0122")));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	public int getPamaryKey()
	{
		int n=0;
		try
		{
			String sql = "select max(logid) as id from t_hr_archlog";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs=dao.search(sql);
			while(rs.next())
			{
				n=rs.getInt("id");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return n+1;
	}
	/**
	 * 记录浏览档案的日志
	 * @param pamaryKey
	 * @param clientip
	 * @param pcname
	 * @param browse_name
	 * @param view
	 * @param archiveid
	 * @param a0101
	 * @param b0110
	 * @param e0122
	 * @param a0100
	 * @param pre
	 */
	public void noteLog(int pamaryKey,String clientip,String logintime,String pcname,String browse_name,String username,String archiveid,String a0101,String b0110,String e0122,String a0100,String pre)
	{
		try
		{
			RecordVo vo = new RecordVo("t_hr_archlog");
			vo.setInt("logid",pamaryKey);
			vo.setString("username",username);
			vo.setString("name",browse_name);
			vo.setString("computername",pcname);
			vo.setString("ip_addr",clientip);
			vo.setDate("logintime",logintime);
			//vo.setString("logouttime","");
			vo.setString("archtype",archiveid);
			vo.setString("b0110",b0110);
			vo.setString("e0122",e0122);
			vo.setString("a0101",a0101);
			vo.setString("a0100",a0100);
			vo.setString("nbase",pre);
			ContentDAO dao = new ContentDAO(this.conn);
			dao.addValueObject(vo);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 更新日志的推出时间
	 * @param pamaryKey
	 * @param logouttime
	 */
	public void updateLog(int pamaryKey,String logouttime)
	{
		try
		{
			RecordVo vo = new RecordVo("t_hr_archlog");
			vo.setInt("logid",pamaryKey);
			ContentDAO dao = new ContentDAO(this.conn);
			RecordVo avo=dao.findByPrimaryKey(vo);
			if(avo!=null)
			{
				avo.setDate("logouttime", logouttime);
				dao.updateValueObject(avo);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public int getMaxid()
	{
		int n=0;
		try
		{
			String sql = "select max(logid) as id from t_hr_archlog";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs=dao.search(sql);
			while(rs.next())
			{
				n=rs.getInt("id");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return n;
	}

}
