package com.hjsj.hrms.businessobject.performance.kh_system.kh_template;

import com.hjsj.hrms.businessobject.competencymodal.personPostModal.PersonPostModalBo;
import com.hjsj.hrms.businessobject.gz.templateset.DownLoadXml;
import com.hjsj.hrms.businessobject.performance.kh_system.kh_field.KhFieldBo;
import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.businessobject.sys.RowSetToXmlBuilder;
import com.hjsj.hrms.businessobject.sys.SysPrivBo;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.sys.ResourceParser;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.struts.upload.FormFile;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.io.*;
import java.math.BigDecimal;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * <p>Title:KhTemplateBo.java</p>
 * <p>Description>:KhTemplateBo.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2008-4-24 下午01:49:31</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */

public class KhTemplateBo 
{

	private Connection conn;
	private int td_width=130;
	private int td_height=30;
	private String isVisible;
	private String subsys_id="";
	private UserView userView;
	public String getPointsetid() {
		return pointsetid;
	}
	public void setPointsetid(String pointsetid) {
		this.pointsetid = pointsetid;
	}

	DecimalFormat myformat1 = new DecimalFormat("########.########");//
	
	public KhTemplateBo()
	{
	}
	public KhTemplateBo(Connection conn)
	{
		this.conn=conn;
	}
	public KhTemplateBo(Connection conn,String isVisible)
	{
		this.conn=conn;
		this.isVisible=isVisible;
	}
	public KhTemplateBo(Connection conn,UserView userView,String isVisible,String subsys_id)
	{
		this.conn=conn;
		this.userView=userView;
		this.isVisible=isVisible;
		this.subsys_id=subsys_id;
	}
	/**
	 *根据当前编号，返回下一个编号，编号中可以有字母和数字
     *编号增加顺序：0 -> 1 -> ... -> 9 -> 0
     *            A -> ... -> Z -> 0
     *Z 增 1 会进位, 例如：'AZ3Z' -> 'AZ40'
	 * @param keyCloumn 主键列
	 * @param tableName 表名
	 * @return
	 */
	public String getNextSeq(String keyCloumn,String tableName)
	{
		String key="A0001";
		try
		{
			
			StringBuffer buf = new StringBuffer();
			buf.append("select MAX(UPPER(");
			buf.append(keyCloumn+")) as "+keyCloumn);
			buf.append(" from ");
			buf.append(tableName);
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs =null;
			rs=dao.search(buf.toString());
			while(rs.next())
			{
				String str=rs.getString(keyCloumn);
				if(str==null|| "".equals(str)) {
                    return key;
                }
				String[] temp=new String[str.length()] ;
				for(int i=0;i<str.length();i++)
				{
					temp[i]=str.substring(i,i+1);
				}
				if("Z".equals(temp[temp.length-1])&& "Z".equals(temp[temp.length-2])){//末位是ZZ，则是两位的最大值，此时改为四位ZZ00  zhaoxg add 2014-9-19
					temp[temp.length-2]="ZZ";
					temp[temp.length-1]="00";
					for(int i=0;i<temp.length;i++)
					{
						if(i==0)
						{
							key="";
						}
						key+=temp[i];
					}
					return key;
				}
				for(int j=temp.length-1;j>=0;j--)
				{
					if(j==temp.length-1)
					{
						if("z".equalsIgnoreCase(temp[j]))
						{
							temp[j]="0";
							int i=1;
							while(j-i>=0)
							{
								String astr=temp[j-i];
								if(!"z".equals(astr))
								{
									if("9".equals(astr)) {
                                        temp[j-i]="A";
                                    } else {
                                        temp[j-i]=this.getNextLetter(temp[j-i]);
                                    }
									break;
								}
								else
								{
									temp[j-i]="0";
									i++;
								}
							}
							if(i==temp.length)
							{
								String tem="";
								for(int ff=0;ff<temp.length;ff++)
								{
									tem+="0";
								}
								return "1"+tem;
							}
						}else if("9".equalsIgnoreCase(temp[j]))
						{
							temp[j]="A";
						}
						else
						{
							temp[j]=this.getNextLetter(temp[j]);
							break;
						}
					}
				}
				for(int i=0;i<temp.length;i++)
				{
					if(i==0)
					{
						key="";
					}
					key+=temp[i];
				}
				
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return key;
	}
	public String getNextLetter(String args)
	{
	   String str="";
	   try
	   {
		   String[] lettersUpper={"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z","0","1","2","3","4","5","6","7","8","9","0"};
		  
		   if("9".equals(args)) {
               return "A";
           }
    	   for(int i=0;i<(lettersUpper.length-1);i++)
    	   {
			    String x=lettersUpper[i];
			    if(args.equalsIgnoreCase(x))
			    {
			        str=lettersUpper[i+1];
			    	break;
			    }
    	   }

	   }
	   catch(Exception e)
	   {
		   e.printStackTrace();
	   }
	   return str;
	}
	/**
	 * 主键为最大加一的，得到其下一个主键值
	 * @param tableName
	 * @param cloumn
	 * @return
	 */
	public int getMaxId(String tableName,String cloumn)
	{
		int i=0;
		try
		{
			String sql = "select MAX("+cloumn+") from "+tableName;
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(sql);
			while(rs.next())
			{
				i=rs.getInt(1);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return i+1;
	}
	/**
	 * Description: 根据id获取模版分类信息
	 * @Version1.0 
	 * Sep 18, 2012 10:26:07 AM Jianghe created
	 * @param pointsetid
	 * @return
	 */
	public HashMap getTemplateSetById(String templatesetid)
	{
		HashMap map = new HashMap();
		try
		{
			String sql = "select validflag,name,scope,b0110 from per_template_set where template_setid='"+templatesetid+"'";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(sql);
			while(rs.next())
			{
				map.put("name",rs.getString("name")==null?"":rs.getString("name"));
				map.put("flag",rs.getString("validflag")==null?"1":rs.getString("validflag"));
				map.put("scope",rs.getString("scope")==null?"0":rs.getString("scope"));
				map.put("b0110",rs.getString("b0110")==null?"0":rs.getString("b0110"));
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 根据模板id获得模板信息
	 * @param templateid
	 * @return
	 */
	public LazyDynaBean getTemplateInfo(String templateid)
	{
		LazyDynaBean bean = new LazyDynaBean();
		try
		{
			String sql = "select * from per_template where template_id='"+templateid+"'";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(sql);
			while(rs.next())
			{
				bean.set("templatename", rs.getString("name"));
				bean.set("topscore", rs.getString("topscore")==null?"0":this.myformat1.format(rs.getDouble("topscore")));
				bean.set("status", rs.getString("status")==null?"0": rs.getString("status"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return bean;
	}
	/**
	 * Description: 根据模板id获得模板类信息
	 * @Version1.0 
	 * Sep 19, 2012 10:28:24 AM Jianghe created
	 * @param templateid
	 * @return
	 */
	public LazyDynaBean getTemplateSetInfo(String templateid){
		LazyDynaBean bean = new LazyDynaBean();
		try
		{
			String sql = "select template_setid,name from per_template_set where template_setid=(select template_setid from per_template where template_id='"+templateid+"')";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(sql);
			while(rs.next())
			{
				bean.set("setname", rs.getString("name"));
				bean.set("parentsetid", rs.getString("template_setid"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return bean;
	}
	/**
	 * 如果父节点还没有孩子节点则将新建的作为孩子节点
	 * @param templatesetid
	 * @param parent_id
	 */
	public void setChild_id(String templatesetid,String parent_id)
	{
		try
		{
			String sql = "select * from per_template_set where parent_id='"+parent_id+"'";
			RowSet rs = null;
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql);
	    	if(rs.next())
			{
			}
	    	else
	    	{
	    		sql = " update per_template_set set child_id='"+templatesetid+"' where template_setid='"+parent_id+"'";
	    		dao.update(sql);
	    	}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	/**
	 * 递归删除模板分类，以及模板分类下的模板
	 * @param id
	 */
	public void deleteTemplateSet(String id,ContentDAO dao)
	{
		try{
			//StringBuffer buf = new StringBuffer();
			StringBuffer set_buf=new StringBuffer("");
			StringBuffer item_buf=new StringBuffer("");
			set_buf.append("'"+id.toUpperCase()+"'");
			this.getTemplateid(id, item_buf, dao);
			this.getTemplateSetChild(id, set_buf, item_buf, dao);
			StringBuffer templateitem = new StringBuffer("");
			if(item_buf!=null&&!"".equals(item_buf.toString()))
			{
	    	    String sql="select * from per_template_item where UPPER(template_id) in("+item_buf.toString().toUpperCase().substring(1)+")";
	    	    RowSet rs=dao.search(sql);
	    	    while(rs.next())
	    	    {
	    	    	templateitem.append(",'"+rs.getString("item_id")+"'");
	    	    }
	    	    if(templateitem!=null&&!"".equals(templateitem.toString()))
	    	    {
	    	    	/**删除指标*/
	    	    	dao.delete("delete from per_template_point where UPPER(item_id) in ("+templateitem.toString().substring(1)+")",new ArrayList());
	    	    }
	    	    /**删除项目*/
	    	    dao.delete("delete from per_template_item where UPPER(template_id) in ("+item_buf.toString().substring(1).toUpperCase()+")", new ArrayList());
	    	    /**删除模板*/
	    	    dao.delete("delete from per_template where UPPER(template_id) in("+item_buf.toString().substring(1).toUpperCase()+")", new ArrayList());
			}
			/**删除模板分类*/
			dao.delete("delete per_template_set where UPPER(template_setid) in ("+set_buf.toString().toUpperCase()+")", new ArrayList());
			/**更新父节点的child_id数据，以免在建分类时，数据错乱*/
			dao.update(" update per_template_set set child_id = null where child_id='"+id+"'");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 删除模板分类下的模板
	 * @param template_setid
	 * @param dao
	 */
	public void deleteTemplateBySetid(String template_setid,ContentDAO dao)
	{
		try
		{
			String sql = "delete from per_template where template_setid="+template_setid;
			dao.delete(sql,new ArrayList());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * @param template_setid
	 * @param dao
	 */
	public void deleteTemplate(String template_setid,ContentDAO dao)
	{
		try
		{
			StringBuffer item_buf=new StringBuffer("");
			String sql2="select item_id from per_template_item where UPPER(template_id)='"+template_setid.toUpperCase()+"'";
			RowSet rs=dao.search(sql2);
			while(rs.next())
			{
				item_buf.append(",'"+rs.getString("item_id")+"'");
			}
			if(item_buf!=null&&!"".equals(item_buf.toString().trim()))
			{
				/**删除指标*/
		    	dao.delete("delete from per_template_point where UPPER(item_id) in ("+item_buf.toString().substring(1).toUpperCase()+")", new ArrayList());
		    	/**删除项目*/
		    	dao.delete("delete from per_template_item where UPPER(template_id)='"+template_setid.toUpperCase()+"'", new ArrayList());
			}
			String sql = "delete from per_template where UPPER(template_id)='"+template_setid.toUpperCase()+"'";
			/**删除模板*/
			dao.delete(sql,new ArrayList());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 判断新增模板id和名称是否已经存在
	 * @param templateid
	 * @param templatename
	 * @return
	 */
	public boolean isHave(String templateid,String templatename,String type,String oldid,String parentsetid)
	{
		boolean flag = true;
		try
		{
			StringBuffer sql = new StringBuffer();
			sql.append(" select * from per_template where ( UPPER(template_id)='"+templateid.toUpperCase()+"'");
			sql.append(" or ");
			sql.append(" name = '"+templatename+"')");
			if(!"0".equals(type))
			{
				sql.append(" and UPPER(template_id)<>'");
				sql.append(oldid.toUpperCase()+"'");
			}
			if("3".equals(type) && parentsetid!=null && !"".equals(parentsetid.trim())){
				sql.append(" and template_setid="+parentsetid);
			}
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(sql.toString());
			if(rs.next())
			{
				flag=false;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
	/**
	 * 取得所有已使用的模板
	 * @param dao
	 * @return
	 */
	public HashMap getAllUsedTemplate(ContentDAO dao)
	{
		HashMap map = new HashMap();
		try
		{
			String sql = "select template_id from per_plan where template_id is not null";
			RowSet rs = dao.search(sql);
			while(rs.next())
			{
				if(rs.getString("template_id")!=null&&!"".equals(rs.getString("template_id")))
				{
					map.put(rs.getString("template_id").toUpperCase(), "1");
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 判断模板是否被使用
	 * @param dao
	 * @param id
	 * @return 
	 */
	public boolean templateIsUsed(String id,ContentDAO dao)
	{
		boolean flag = false;
		try{
			StringBuffer buf = new StringBuffer();
			buf.append(" select * from per_plan where UPPER(template_id)='");
			buf.append(id.toUpperCase());
			buf.append("'");
            RowSet rs = null;
            rs = dao.search(buf.toString());
            if(rs.next())
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
	 * 递归查找模板分类中已使用的模板
	 * @param id
	 * @param dao
	 * @return
	 */
	public boolean isHaveUsedTemplate(String id,ContentDAO dao)
	{
		boolean flag = false;
		try
		{
			StringBuffer set_buf=new StringBuffer("");
			StringBuffer item_buf=new StringBuffer("");
			set_buf.append("'"+id.toUpperCase()+"'");
			this.getTemplateid(id, item_buf, dao);
			this.getTemplateSetChild(id, set_buf, item_buf, dao);
			if(item_buf!=null&&item_buf.toString().trim().length()>0)
			{
				String sql="select * from per_plan where template_id in ("+item_buf.toString().substring(1)+")";
				RowSet rs=null;
				rs=dao.search(sql);
				while(rs.next())
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
	public boolean isUse(String setid,ContentDAO dao)
	{
		boolean flag = false;
		try
		{
			StringBuffer sql = new StringBuffer();
			sql.append(" select * from per_plan where template_id in (");
			sql.append(" select template_id from per_template where UPPER(template_setid)='"+setid+"')");
			RowSet rs = null;
			rs = dao.search(sql.toString());
			if(rs.next())
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
	 * 设置模板的有效性
	 * @param id
	 * @param validflag
	 * @param dao
	 */
	public void configTemplateEffectiveness(String id,String validflag,ContentDAO dao,String type)
	{
		try
		{
			if("0".equals(type))
			{
				StringBuffer set_buf=new StringBuffer("");
				StringBuffer item_buf=new StringBuffer("");
				set_buf.append("'"+id.toUpperCase()+"'");
				this.getTemplateid(id, item_buf, dao);
				this.getTemplateSetChild(id, set_buf, item_buf, dao);
				String sql=" update per_template_set set validflag='"+validflag+"' where UPPER(template_setid) in("+set_buf.toString()+")";
				ArrayList list = new ArrayList();
				list.add(sql);
				if(item_buf!=null&&!"".equals(item_buf.toString().trim()))
				{
					String sql2=" update per_template set validflag='"+validflag+"' where UPPER(template_id) in ("+item_buf.toString().substring(1)+")";
				    list.add(sql2);
				}
				dao.batchUpdate(list);
			}
			else
			{
	     		RecordVo vo = new RecordVo("per_template");
	    		vo.setString("template_id",id);
	    		vo = dao.findByPrimaryKey(vo);
	    		vo.setString("validflag",validflag);
	    		dao.updateValueObject(vo);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public void getTemplateSetChild(String itemid,StringBuffer set_buf,StringBuffer item_buf,ContentDAO dao)
	{
		try
		{
			String sql="select * from per_template_set where UPPER(parent_id)='"+itemid.toUpperCase()+"'";
			RowSet rs = null;
			rs= dao.search(sql);
			while(rs.next())
			{
				String id=rs.getString("template_setid");
				set_buf.append(",'"+id+"'");
				this.getTemplateid(id, item_buf, dao);
				this.getTemplateSetChild(id, set_buf, item_buf, dao);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public void getTemplateid(String setid,StringBuffer buf,ContentDAO dao)
	{
		try
		{
			String sql ="select template_id from per_template where UPPER(template_setid)='"+setid.toUpperCase()+"'";
			RowSet rs = dao.search(sql);
			while(rs.next())
			{
				buf.append(",'"+rs.getString("template_id").toUpperCase()+"'");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 设置考核模板项目的孩子节点
	 * @param pid
	 * @param id
	 */
	public void setTemplateItemChild(String pid,String id)
	{
		try
		{
			if(pid==null|| "".equals(pid)) {
                return;
            }
			StringBuffer buf = new StringBuffer();
			ContentDAO dao = new ContentDAO(this.conn);
			buf.append("select * from per_template_item where UPPER(parent_id)="+pid.toUpperCase()+" and item_id<>"+id);
			RowSet rs = dao.search(buf.toString());
			if(rs.next())
			{
				
			}
			else
			{
				RecordVo vo = new RecordVo("per_template_item");
				vo.setInt("item_id",Integer.parseInt(pid));
				vo = dao.findByPrimaryKey(vo);
				vo.setInt("child_id",Integer.parseInt(id));
				dao.updateValueObject(vo);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 判断要素表中该项目中是否已经有该考核指标
	 * @param itemid
	 * @param pointid
	 * @return
	 */
	public boolean isHavePoint(String itemid,String pointid)
	{
		boolean flag = false;
		try
		{
			StringBuffer buf = new StringBuffer();
			buf.append("select * from per_template_point where ");
			buf.append(" item_id="+itemid);
			buf.append(" and UPPER(point_id)='"+pointid.toUpperCase()+"'");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(buf.toString());
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
 * 导入模板，项目，指标
 * @param oldid
 * @param newid
 * @param seq
 * @param name
 * @param topscore
 * @param status
 */
	public void templateSaveAs(String oldid,String newid,int seq,String name,String topscore,String status,String parentsetid)
	{
		try
		{
			StringBuffer sql = new StringBuffer();
			ContentDAO dao = new ContentDAO(this.conn);
			/**导入模板*/
			sql.append(" insert into per_template (template_id,template_setid,seq,name,topscore,currentscore,create_date,modify_date,validflag,valid_date,invalid_date,status)");
			sql.append(" select '");
			sql.append(newid +"' as template_id,'"+parentsetid+"'as template_setid,"+seq+" as seq,'");
			sql.append(name+"' as name,"+topscore+" as topscore,currentscore,");
			sql.append(Sql_switcher.today()+" as create_date,");
			sql.append(Sql_switcher.today()+"as modify_date,validflag,valid_date,invalid_date,"+status+" as status ");
			sql.append(" from per_template where UPPER(template_id)='"+oldid.toUpperCase()+"'");
			dao.insert(sql.toString(),new ArrayList());
			sql.setLength(0);
			/**导入模板项目,导入指标*/
			//int item_id = this.getMaxId("per_template_item", "item_id");
			int item_seq = this.getMaxId("per_template_item", "seq");
			ArrayList itemlist = getItemList(item_seq, oldid,newid,topscore);
			templateItemSaveAs(dao,itemlist);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	/**
	 * 导入模板项目
	 * @param dao
	 * @param oldlist
	 */
	public void templateItemSaveAs(ContentDAO dao,ArrayList oldlist)
	{
		try
		{
			HashMap mm=new HashMap();
			HashMap mh=new HashMap();
			for(int i=0;i<oldlist.size();i++)
			{
				String[] temp = (String[])oldlist.get(i);
				String item_id=temp[0];//旧的id
				String newitem_id=temp[6];//新id
				int flag=0;
				for(int j=i;j<oldlist.size();j++)
				{
					String[] tt=(String[])oldlist.get(j);
					if(tt[1]!=null&&tt[1].equalsIgnoreCase(item_id))
					{
						if(flag==0)
						{
							temp[2] = tt[6];
						}
						tt[1]=newitem_id;
					}
					
					mm.put(String.valueOf(j), tt);
				}
				mh.put(String.valueOf(i),temp);
			}
			for(int i=0;i<mm.size();i++)
			{
				String[] ff =(String[])mm.get(String.valueOf(i));
				RecordVo vo = new RecordVo("per_template_item");
				vo.setString("item_id",ff[6]);
				if(ff[1]!=null) {
                    vo.setString("parent_id",ff[1]);
                }
				if(ff[2]!=null) {
                    vo.setString("child_id",ff[2]);
                }
				vo.setString("itemdesc",ff[4]);
				vo.setString("template_id", ff[3]);
				vo.setString("seq",ff[5]);
				if(ff[7]!=null&&!"".equals(ff[7])) {
                    vo.setString("score",ff[7]);
                }
				if(ff[8]!=null&&!"".equals(ff[8])) {
                    vo.setString("rank",ff[8]);
                }
				if(ff[9]!=null&&!"".equals(ff[9])) {
                    vo.setString("kind",ff[9]);
                }
				dao.addValueObject(vo);
				this.pointSaveAs(ff[0],ff[6],Double.parseDouble(ff[7]));//导入指标
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 导入指标
	 * @param oldid
	 * @param newid
	 */
	public void pointSaveAs(String oldid,String newid,double topScore)
	{
		try
		{
			String sql = " select * from per_template_point where item_id="+oldid;
			int seq = this.getMaxId("per_template_point", "seq");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql);
			RecordVo vo = null;
			while(rs.next())
			{
				vo = new RecordVo("per_template_point");
				vo.setString("item_id",newid);
				vo.setString("point_id",rs.getString("point_id"));
				vo.setInt("seq",seq);
				vo.setDouble("rank",rs.getDouble("rank"));
				//xuj update 同邓灿协商模板另存指标分值直接取原有指标分值  2014-12-05
				//vo.setDouble("score",topScore);
				vo.setDouble("score",rs.getDouble("score"));
				dao.addValueObject(vo);			
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 取得模板的项目的所有信息
	 * @param item_id
	 * @param seq
	 * @param templateid
	 * @param newtid
	 * @return
	 */
	public ArrayList getItemList(int seq,String templateid,String newtid,String topscore)
	{
		ArrayList list = new ArrayList();
		try
		{
			String sql = "select * from per_template_item where UPPER(template_id)='"+templateid.toUpperCase()+"' order by seq";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql);
			while(rs.next())
			{
				IDGenerator idg=new IDGenerator(2,this.conn);
				String id=idg.getId("per_template_item.item_Id");		
				String[] temp=new String[10];
				temp[0]=rs.getString("item_id");
				temp[1]=rs.getString("parent_id");
				temp[2]=rs.getString("child_id");
				temp[3]=newtid;
				temp[4]=rs.getString("itemdesc");
				temp[5]=seq+"";
				temp[6]=id;
				//xuj update 和邓灿协商模板另存模板项目分值直接取原有模板项目分值  20141205
				//temp[7]=rs.getString("score")==null?"0":this.myformat1.format(Double.parseDouble(topscore));
				temp[7]=rs.getString("score")==null?"0":this.myformat1.format(rs.getDouble("score"));
				temp[8]=rs.getString("rank")==null?"0":this.myformat1.format(rs.getDouble("rank"));
				temp[9]=rs.getString("kind")==null?"1":this.myformat1.format(rs.getDouble("kind"));
				list.add(temp);
				seq++;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//----------------------------------------------------############3------------###########3----------------------	
	
	private ArrayList templateItemList = new ArrayList();
	private ArrayList leafItemList = new ArrayList();
	private HashMap itemToPointMap = new HashMap();
	private int lay=0;
	private HashMap itemPointNum=new HashMap();
	private HashMap leafItemLinkMap = new HashMap();
	private int extendNum=1;
	//private HashMap itemHasFieldNum = new HashMap();
	private HashMap itemHaveFieldList= new HashMap();
	private String title;
	/**当前模板权重分值标识*/
	private String status ;
	private String score_str="";
	private String item_rank="";
	public String getScore_str()
	{
		return this.score_str;
	}
	private boolean isUsed;
	private HashMap childItemLinkMap;
	private HashMap layMap=new HashMap();
	private ArrayList parentList=new ArrayList();
	private HashMap ifHasChildMap=new HashMap();
	public String getStatus()
	{
		return this.status;
	}
	/**############################导出当前模板##################################**/
	/**
	 * 取得模板的名称
	 */
	public String getTemplateName(String templateID)
	{
		String title="Template Title";
		try
		{
			String sql = "select name from per_template where UPPER(template_id)='"+templateID.toUpperCase()+"'";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql);
			while(rs.next())
			{
				title=rs.getString("name");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return title;
	}
	 private HSSFWorkbook workbook = new HSSFWorkbook();

	 private HSSFSheet sheet = null;

	 private HSSFCellStyle centerstyle = null;

	 private int rowNum = 0; // 行坐标

	 private short colIndex = 0; // 纵坐标

	 private HSSFRow row = null;
	 /**模板对应的所有指标列表*/
	 private ArrayList pointList;
		/**
		 * 取得模板指标列表
		 * @return
		 */
		public ArrayList getPointList(String templateID)
		{
			ArrayList list=new ArrayList();
			try
			{
				ContentDAO dao = new ContentDAO(this.conn);
				RowSet rowSet=dao.search("select po.point_id,po.pointname,po.pointkind,pi.item_id,pp.score,pp.rank  from per_template_item pi,per_template_point pp,per_point po "
						+" where pi.item_id=pp.item_id and pp.point_id=po.point_id  and template_id='"+templateID+"'  order by pp.seq");	  //pi.seq,
				LazyDynaBean abean=null;
				while(rowSet.next())
				{
					abean=new LazyDynaBean();
					abean.set("point_id",rowSet.getString("point_id"));
					abean.set("pointname",rowSet.getString("pointname"));
					abean.set("pointkind",rowSet.getString("pointkind"));
					abean.set("item_id",rowSet.getString("item_id"));
					abean.set("score",rowSet.getString("score")==null?"0":this.myformat1.format(rowSet.getDouble("score")));
					abean.set("rank",rowSet.getString("rank"));
					list.add(abean);
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return list;
		}
	public String importCurrentTemplate(String templateID)
	{
		String outName="";
		/**项目对应指标*/
		this.itemToPointMap=this.getItemToPointMap(templateID);
		/**模板对应的所有项目*/
		this.templateItemList=getTemplateItemList(templateID);
		/**得到项目中所有的叶子项目*/
		get_LeafItemList();
		/**项目的itemid对应的是该项目的所有父亲，爷爷，太爷的列表*/
		this.leafItemLinkMap=getLeafItemLinkMap();
		/**每个项目对应的叶子节点个数*/
		this.itemPointNum=getItemPointNum();
		/**项目id对应的指标的详细信息列表*/
		this.itemHaveFieldList=this.getItemHasFieldList();//指标信息
		/**当前模板的权重分值标识*/
		this.status=this.getTemplateStatus(templateID);
		/**当前模板的名称*/
		this.title=this.getTemplateName(templateID);
		/**当前模板的所有指标*/
		this.pointList=this.getPointList(templateID);
		/**当前模板的权重分值标识*/
		//this.status=this.getTemplateStatus(templateID);
//		HashMap    subItemMap=(HashMap)list.get(3);	//各项目的子项目(hashmap)
		/**除叶子节点外的节点的指标数量*/
		this.childItemLinkMap=this.getChildItemLinkMap();
		this.doMethod2();
		/**EXCEL工作薄*/
		workbook = new HSSFWorkbook();
		/**EXCEL页眉*/
		sheet = workbook.createSheet("Sheet0");
		/**EXCEL样式*/
		centerstyle = style(workbook, 1);
		this.setTitle();   
		outName=this.outExcel();
		return outName;
	}
	private void setTitle() {
		try
		{
			short n=0;
			if("1".equals(this.status))
			{
				n=((short)(this.lay+2));
			}
			else
			{
				n=((short)(this.lay+1));
			}
			HSSFCellStyle titleStyle=style(workbook, 0);
			executeCell(this.rowNum,this.colIndex,this.rowNum,n,this.title,titleStyle);
			this.rowNum++;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 画excel的格子（合并单元格子）
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 * @param content
	 * @param aStyle
	 */
	public void executeCell(int a, short b, int c, short d, String content,
			HSSFCellStyle aStyle) {
		try {
			HSSFRow row = sheet.getRow(a);
			if(row==null) {
                row = sheet.createRow(a);
            }
			
			HSSFCell cell = row.getCell(b);
			if(cell==null) {
                cell = row.createCell(b);
            }
			cell.setCellValue(new HSSFRichTextString(content));
			cell.setCellStyle(aStyle);
			short b1 = b;
			while (++b1 <= d) {
				cell = row.getCell(b1);
				if(cell==null) {
                    cell = row.createCell(b1);
                }
				cell.setCellStyle(aStyle);
			}
			for (int a1 = a + 1; a1 <= c; a1++) {
				row = sheet.getRow(a1);
				if(row==null) {
                    row = sheet.createRow(a1);
                }
				b1 = b;
				while (b1 <= d) {
					cell = row.getCell(b1);
					if(cell==null) {
                        cell = row.createCell(b1);
                    }
					cell.setCellStyle(aStyle);
					b1++;
				}
			}
			ExportExcelUtil.mergeCell(sheet, a, b, c, d);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 导出excel
	 * @return
	 */
	public String outExcel()
	{
		String outName="CurrentTemplate_"+PubFunc.getStrg()+".xls";
		FileOutputStream fileOut = null;
		try
		{

		    HashMap existWriteItem=new HashMap();
			LazyDynaBean abean=null;
			LazyDynaBean a_bean=null;
			int columnSize=0;
			
			 //输出表头
			executeCell(this.rowNum,this.colIndex,this.rowNum,Short.parseShort(String.valueOf(this.lay-1)),ResourceFactory.getProperty("train.job.itemName"),centerstyle);
			this.colIndex+=Short.parseShort(String.valueOf(this.lay));
			executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,"指标名称",centerstyle);
			this.colIndex++;
			executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,"分值",centerstyle);
			this.colIndex++;
			if("1".equals(this.status))
	        {
     			executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,"权重",centerstyle);
	        }
     		columnSize=this.colIndex;
			
			int rowNum=0;
			for(int i=0;i<this.leafItemList.size();i++)
			{
				abean=(LazyDynaBean)this.leafItemList.get(i);
				String item_id=(String)abean.get("item_id");
				/**叶子项目对应的指标数*/
				//int num=((Integer)this.itemPointNum.get(item_id)).intValue();
				//ArrayList pointList=(ArrayList)this.itemToPointMap.get(item_id);
			//	ArrayList pointList = (ArrayList)this.itemHaveFieldList.get(item_id);
				//String item_kind=(String)abean.get("kind");
				/**叶子项目对应的指标数*/
			/*	for(int j=0;j<num;j++)
				{*/
			    	if(i==0) {
                        this.rowNum++;
                    }
					this.colIndex=0;
					rowNum++;
					ArrayList linkParentList=(ArrayList)this.leafItemLinkMap.get(item_id);
					int current=linkParentList.size();
					if(current==1)
					{
						if(existWriteItem.get(item_id)!=null)
			    		{
			    			this.colIndex++;
		    				continue;
		    			}
		    			existWriteItem.put(item_id,"1");
		    			String itemdesc=(String)abean.get("itemdesc");
		    			//haosl 20170317 将html代码转换为字符
		    			if(StringUtils.isNotBlank(itemdesc)) {
                            itemdesc = PubFunc.reverseHtml(itemdesc);
                        }
		    			/**画出一个父项目*/
		    			int colspan=((itemPointNum.get(item_id)==null?0:((Integer)itemPointNum.get(item_id)).intValue())+(childItemLinkMap.get(item_id)==null?0:((Integer)childItemLinkMap.get(item_id)).intValue()));
		    			executeCell(this.rowNum,this.colIndex,this.rowNum+colspan-1,this.colIndex,itemdesc,centerstyle);
		    			this.colIndex++;
		    			/**父项目包含指标，画出指标*/
		    			/**该项目的层数*/
		    			int layer=Integer.parseInt((String)layMap.get(item_id));
		    			/**对应指标列表*/
			    		ArrayList fieldlistp = (ArrayList)this.itemHaveFieldList.get(item_id);
			    		/**该项目有指标*/
			    		if(fieldlistp!=null&&fieldlistp.size()>0)
			    		{
			    			for(int h=0;h<fieldlistp.size();h++)
			    			{
				    			LazyDynaBean pointbean=(LazyDynaBean)fieldlistp.get(h);
				    			int k=0;
			    				for(int f=0;f<this.lay-layer;f++)
			    				{
			    					executeCell(this.rowNum,(short)(colIndex+f),this.rowNum,(short)((colIndex+f)),"",centerstyle);
			    					k++;
			    				}
			    				/**指标名称*/
			    				executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),(String)pointbean.get("name"),centerstyle);
			    				k++;
		    					/**分值*/
		    					executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),(String)pointbean.get("score"),centerstyle);
		    					/**权重*/
			    				if("1".equals(this.status))
		    					{
		    						k++;
		    						executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),(String)pointbean.get("rank"),centerstyle);
		    					}
		    	
		        			    	this.rowNum++;
	    					}
		    			}
			    		/**没有指标**/
		    			else
		    			{
		    				/**画出空格*/
		    				int k=0;
		    				for(int f=0;f<this.lay-layer;f++)
		    				{
		    					executeCell(this.rowNum,(short)(colIndex+f),this.rowNum,(short)((colIndex+f)),"",centerstyle);
		    				    k++;
		    				}
		    				executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),"",centerstyle);
		    				/**项目分值*/
		    				k++;
	    					executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),(String)abean.get("score"),centerstyle);
	    					/**项目权重*/
	    					if("1".equals(this.status))
	    					{
	    						k++;
    							executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),(String)abean.get("rank"),centerstyle);
    						}
	    					this.rowNum++;
    					}	
					}
					else
					{
			    		/**叶子项目的所有父项目列表（爷爷，太爷）*/
			    		for(int e=linkParentList.size()-1;e>=0;e--)
			    		{
				    		a_bean=(LazyDynaBean)linkParentList.get(e);
				    		String itemid=(String)a_bean.get("item_id");
			    			if(existWriteItem.get(itemid)!=null)
				    		{
				    			this.colIndex++;
			    				continue;
			    			}
			    			existWriteItem.put(itemid,"1");
			    			String itemdesc=(String)a_bean.get("itemdesc");
			    			//haosl 20170317 将html代码转换为字符
			    			if(StringUtils.isNotBlank(itemdesc)) {
                                itemdesc = PubFunc.reverseHtml(itemdesc);
                            }
			    			/**画出一个父项目*/
			    			int colspan=((itemPointNum.get(itemid)==null?0:((Integer)itemPointNum.get(itemid)).intValue())+(childItemLinkMap.get(itemid)==null?0:((Integer)childItemLinkMap.get(itemid)).intValue()));	
			        		//this.rowNum++;
			    			executeCell(this.rowNum,this.colIndex,this.rowNum+colspan-1,this.colIndex,itemdesc,centerstyle);
			    			this.colIndex++;
			    			/**父项目包含指标，画出指标*/
			    			/**该项目的层数*/
			    			int layer=Integer.parseInt((String)layMap.get(itemid));
			    			/**对应指标列表*/
				    		ArrayList fieldlistp = (ArrayList)this.itemHaveFieldList.get(itemid);
				    		/**该项目有指标*/
				    		if(fieldlistp!=null&&fieldlistp.size()>0)
				    		{  
				    			for(int h=0;h<fieldlistp.size();h++)
				    			{
					    			LazyDynaBean pointbean=(LazyDynaBean)fieldlistp.get(h);
					    			int k=0;
					    			
				    				for(int f=0;f<this.lay-layer;f++)
				    				{
				    					executeCell(this.rowNum,(short)(colIndex+f),this.rowNum,(short)((colIndex+f)),"",centerstyle);
				    					k++;
				    				}
				    				/**指标名称*/
				    				executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),(String)pointbean.get("name"),centerstyle);
				    				k++;
			    					/**分值*/
			    					executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),(String)pointbean.get("score"),centerstyle);
			    					/**权重*/
				    				if("1".equals(this.status))
			    					{
			    						k++;
			    						executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),(String)pointbean.get("rank"),centerstyle);
			    					}
			    					
			        			    	this.rowNum++;
		    					}
			    			}
				    		/**没有指标**/
			    			else
			    			{
			    				if(e==0)
			    				{
		    	    				int k=0;
			        				for(int f=0;f<this.lay-layer;f++)
			        				{
			    	     				executeCell(this.rowNum,(short)(colIndex+f),this.rowNum,(short)((colIndex+f)),"",centerstyle);
			    	    			    k++;
			    		    		}
			        				executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),"",centerstyle);
			    			    	/**项目分值*/
			    		    		k++;
		    			    		executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),(String)a_bean.get("score"),centerstyle);
		    				    	/**项目权重*/
		    			    		if("1".equals(this.status))
		    				    	{
		    				    		k++;
	    					    		executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),(String)a_bean.get("rank"),centerstyle);
	    				    		}
		    			    		this.rowNum++;
			    				}
	    					}
	    				}
					}
				/*}*/
			}
		
			for (int i = 0; i <=columnSize; i++)
			{
				this.sheet.setColumnWidth(Short.parseShort(String.valueOf(i)),(short)6000);
			}
			for (int i = 0; i <=this.rowNum; i++)
			{
			    row = sheet.getRow(i);
			    if(row==null) {
                    row = sheet.createRow(i);
                }
			    row.setHeight((short) 400);
			}
			fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+ System.getProperty("file.separator") + outName);
			workbook.write(fileOut);
//			outName=outName.replaceAll(".xls","#");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(fileOut);
			PubFunc.closeResource(this.workbook);
		}
		return outName;
	}
	public void writePointGridExcel(int current,ArrayList pointList,int j)
	{
		
		LazyDynaBean point_bean=null;
		for(int e=current;e<this.lay;e++)
		{
			executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,"",centerstyle);
			this.colIndex++;
		}
		
		if(pointList==null)
		{
			executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,"",centerstyle);
			this.colIndex++;
		}
		else
		{  
			point_bean=(LazyDynaBean)pointList.get(j);
			executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,(String)point_bean.get("name"),centerstyle);
			this.colIndex++;
		}
		  
		point_bean=(LazyDynaBean)pointList.get(j);
		executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,(String)point_bean.get("score"),centerstyle);
		this.colIndex++;
		if("1".equals(this.status))
		{
			executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,(String)point_bean.get("rank"),centerstyle);
			this.colIndex++;
		}
		
	}
	/**
	 * 除叶子节点外的节点的指标数量
	 * @return
	 */
	public HashMap getChildItemLinkMap()
	{
		HashMap map = new HashMap();
		for(int i=0;i<this.templateItemList.size();i++)
		{
			LazyDynaBean bean=(LazyDynaBean)this.templateItemList.get(i);
			ArrayList list=new ArrayList();
			doMethod(bean,list);
			LazyDynaBean aa_bean=null;
			int n=0;
			for(int j=0;j<list.size();j++)
			{
				aa_bean=(LazyDynaBean)list.get(j);
				String item_id=(String)aa_bean.get("item_id");
				if(itemToPointMap.get(item_id)!=null) {
                    n+=((ArrayList)itemToPointMap.get(item_id)).size();
                }
				
			}
			map.put((String)bean.get("item_id"),new Integer(n));
		}
		return map;
	}
	public void doMethod(LazyDynaBean bean,ArrayList list)
	{
		String itemid=(String)bean.get("item_id");
		String childid=(String)bean.get("child_id");
		if(childid.length()==0)
		{
			//list.add(bean);
			return;
		}else
		{
			list.add(bean);
		}
		for(int j=0;j<this.templateItemList.size();j++)
		{
			LazyDynaBean a_bean=(LazyDynaBean)this.templateItemList.get(j);
			String parentid=(String)a_bean.get("parent_id");
			if(parentid.equals(itemid))
			{
				doMethod(a_bean,list);
			}
		}
	}
	public void doMethod2()
	{
		for(int i=0;i<parentList.size();i++)
		{
			LazyDynaBean bean = (LazyDynaBean)parentList.get(i);
			String itemid=(String)bean.get("item_id");
			layMap.put(itemid, "1");
			doM(bean,1);		
		}
	}
	public void doM(LazyDynaBean bean,int lay)
	{
		lay++;
		for(int i=0;i<this.templateItemList.size();i++)
		{
			LazyDynaBean a_bean=(LazyDynaBean)this.templateItemList.get(i);
			String itemid=(String)bean.get("item_id");
			String a_itemid=(String)a_bean.get("item_id");
			String parentid=(String)a_bean.get("parent_id");
			if(parentid.equals(itemid))
			{
				ifHasChildMap.put(itemid, "1");
				layMap.put(a_itemid,lay+"");
				doM(a_bean,lay);
			}
		}
	}
	private String returnflag="";
	public String getObjectCardHtml(String templateID,String returnflag)
	{
		String html=""; 
		this.returnflag=returnflag;
		/**项目对应指标*/
		this.itemToPointMap=this.getItemToPointMap(templateID);
		/**模板对应的所有项目*/
		this.templateItemList=getTemplateItemList(templateID);
		/**得到项目中所有的叶子项目*/
		get_LeafItemList();
		/**项目的itemid对应的是该项目的所有父亲，爷爷，太爷的列表*/
		this.leafItemLinkMap=getLeafItemLinkMap();
		/**每个项目对应的叶子节点个数*/
		this.itemPointNum=getItemPointNum();
		/**项目对应的指标个数*/
		//this.itemHasFieldNum=this.getItemHasFieldCount(templateID);//各项目包含的指标个数
		/**项目id对应的指标的详细信息列表*/
		this.itemHaveFieldList=this.getItemHasFieldList();//指标信息
		/**当前模板的权重分值标识*/
		this.status=this.getTemplateStatus(templateID);
		this.isUsed=this.templateIsUsed(templateID, new ContentDAO(this.conn));
//		HashMap    subItemMap=(HashMap)list.get(3);	//各项目的子项目(hashmap)
		/**除叶子节点外的节点的指标数量*/
		this.childItemLinkMap=this.getChildItemLinkMap();
		this.doMethod2();
		html=writeHtml();
		return html;
	}
	public String writeHtml2(String method,String showbenbu,String showaband,HashMap poinNumMap,HashMap pointScaleMap,HashMap benbu,String boydid,ArrayList bodyList,HashMap scaleforall1,HashMap ponitforall1,String showComment,ArrayList comment,HashMap ponitMain,String templateID)
	{
		String html="";
		/*
		 * 【753】票数占比反馈表中，所有主体（分类统计）下，
		 * 指标解释显示太窄了，建议加宽些。
		 * jingq add 2015.01.19
		 * nums为了记录td的总个数，确定table的宽度。
		 */
		int nums = 0;
		StringBuffer extendtHead = new StringBuffer();
		String row="1";
		if(method==null|| "0".equalsIgnoreCase(method)||method.trim().length()==0){
			row="2";//纵向显示
		}else{
			row="3";//横向显示
		}
		String size="1";
		ArrayList perGradeTemplateList=this.getPerGradeTemplateList(templateID);
		if(perGradeTemplateList!=null){
			size=perGradeTemplateList.size()+"";
		}
		if(showaband!=null&&showaband.trim().length()!=0&& "1".equalsIgnoreCase(showaband)){
			if("all1".equalsIgnoreCase(boydid)){
				size=String.valueOf(Integer.parseInt(size)+2);
			}else{
				size=String.valueOf(Integer.parseInt(size)+1);
			}
		}
		// 根据不同条件生成表头
		extendtHead.append("<tr class='trDeep_self'  height='20' >\r\n");
		extendtHead.append("<td class='TableRow_2rows' style='border-top:0px;border-left:0px;' valign='middle' align='center' width='100' rowspan='"+row+"' colspan='"+this.lay+"'>项&nbsp;目&nbsp;名&nbsp;称</td>\r\n");
		extendtHead.append("<td class='TableRow_2rows'  style='border-top:0px;' valign='middle' align='center' width='100' rowspan='"+row+"' >指&nbsp;标&nbsp;名&nbsp;称</td>\r\n");
		extendtHead.append("<td class='TableRow_2rows'  style='border-top:0px;' valign='middle' align='center' width='100' rowspan='"+row+"' >指&nbsp;标&nbsp;解&nbsp;释</td>\r\n");
		int totalvv=0;
		if(method==null|| "0".equalsIgnoreCase(method)||method.trim().length()==0){
			if(boydid!=null&& "all1".equalsIgnoreCase(boydid)){
				nums = bodyList.size()+5;
				for(int i=0;i<bodyList.size();i++){
					
					CommonData cd=(CommonData)bodyList.get(i);
					String bodyid=cd.getDataValue();
					String name=cd.getDataName();
					if("all".equalsIgnoreCase(bodyid)|| "all1".equalsIgnoreCase(bodyid)){
						continue;
					}
					String numBody=(String)ponitMain.get(bodyid);
					totalvv+=Integer.parseInt(numBody);
					extendtHead.append("<td class='TableRow_2rows'  style='border-top:0px;' valign='middle' align='center' ' colspan='"+size+"' >"+name+"票&nbsp;数("+numBody+")&nbsp;及&nbsp;比&nbsp;例</td>\r\n");
				}
				extendtHead.append("<td class='TableRow_2rows'  style='border-right:0px;'  style='border-top:0px;' valign='middle' align='center' ' colspan='"+size+"' >合计票&nbsp;数("+totalvv+")&nbsp;及&nbsp;比&nbsp;例</td>\r\n");
			}else{
				nums = 5;
				extendtHead.append("<td class='TableRow_2rows'  style='border-right:0px;'  style='border-top:0px;' valign='middle' align='center' ' colspan='"+size+"' >票&nbsp;数&nbsp;及&nbsp;比&nbsp;例</td>\r\n");
			}
		}else{
			if(boydid!=null&& "all1".equalsIgnoreCase(boydid)){
				nums = bodyList.size()+5;
				for(int i=0;i<bodyList.size();i++){
					CommonData cd=(CommonData)bodyList.get(i);
					String bodyid=cd.getDataValue();
					String name=cd.getDataName();
					if("all".equalsIgnoreCase(bodyid)|| "all1".equalsIgnoreCase(bodyid)){
						continue;
					}
					String numBody=(String)ponitMain.get(bodyid);
					totalvv+=Integer.parseInt(numBody);
					extendtHead.append("<td class='TableRow_2rows'  style='border-top:0px;' valign='middle' align='center'  colspan='"+Integer.parseInt(size)*2+"'>"+name+"票&nbsp;数("+numBody+")&nbsp;及&nbsp;比&nbsp;例</td>\r\n");
				}
				extendtHead.append("<td class='TableRow_2rows'  style='border-right:0px;'  style='border-top:0px;' valign='middle' align='center'  colspan='"+Integer.parseInt(size)*2+"'>合计票&nbsp;数&nbsp;("+totalvv+")及&nbsp;比&nbsp;例</td>\r\n");
			}else{
				nums = 5;
				extendtHead.append("<td class='TableRow_2rows'  style='border-right:0px;'  style='border-top:0px;' valign='middle' align='center'  colspan='"+Integer.parseInt(size)*2+"'>票&nbsp;数&nbsp;及&nbsp;比&nbsp;例</td>\r\n");
			}
		}
		//extendtHead.append("<td class='TableRow_2rows'  valign='middle' align='center'  >票&nbsp;数&nbsp;及&nbsp;比&nbsp;例</td>\r\n");
		if(showbenbu!=null&&showbenbu.trim().length()!=0&& "1".equalsIgnoreCase(showbenbu)){
			nums = nums+1;
			extendtHead.append("<td class='TableRow_2rows'  style='border-right:0px;'  style='border-top:0px;' valign='middle' align='center' colspan='"+size+"' >本&nbsp;部&nbsp;平&nbsp;均</td>\r\n");
		}

		extendtHead.append("</tr>");
		extendtHead.append("<tr>");
		if(perGradeTemplateList!=null){
			if(boydid!=null&& "all1".equalsIgnoreCase(boydid)){
				for(int da=0;da<bodyList.size();da++){
					for(int k=0;k<perGradeTemplateList.size();k++){
						LazyDynaBean bean=(LazyDynaBean)perGradeTemplateList.get(k);
						String desc=(String)bean.get("gradedesc");
						if(method==null|| "0".equalsIgnoreCase(method)||method.trim().length()==0){
							extendtHead.append("<td class='TableRow_2rows'  style='border-top:0px;' valign='middle' align='center'  >"+desc+"</td>\r\n");
						}else{
							extendtHead.append("<td class='TableRow_2rows'  style='border-right:0px;'  style='border-top:0px;' valign='middle' align='center' colspan='2' >"+desc+"</td>\r\n");
						}
						
					}
					if(showaband!=null&&showaband.trim().length()!=0&& "1".equalsIgnoreCase(showaband)){
						if(method==null|| "0".equalsIgnoreCase(method)||method.trim().length()==0){
							if(boydid!=null&& "all1".equalsIgnoreCase(boydid)){
								extendtHead.append("<td class='TableRow_2rows'  style='border-top:0px;' valign='middle' align='center'  >总投票</td>\r\n");
							}
							extendtHead.append("<td class='TableRow_2rows'  style='border-right:0px;'  style='border-top:0px;' valign='middle' align='center'  >弃权</td>\r\n");
						}else{
							if(boydid!=null&& "all1".equalsIgnoreCase(boydid)){
								extendtHead.append("<td class='TableRow_2rows'  style='border-top:0px;' valign='middle' align='center' colspan='2' >总投票</td>\r\n");
							}
							extendtHead.append("<td class='TableRow_2rows'  style='border-right:0px;'  style='border-top:0px;' valign='middle' align='center' colspan='2' >弃权</td>\r\n");
						}
					}
				}
			}else{
				for(int k=0;k<perGradeTemplateList.size();k++){
					LazyDynaBean bean=(LazyDynaBean)perGradeTemplateList.get(k);
					String desc=(String)bean.get("gradedesc");
					if(method==null|| "0".equalsIgnoreCase(method)||method.trim().length()==0){
						extendtHead.append("<td class='TableRow_2rows'  style='border-right:0px;'  style='border-top:0px;' valign='middle' align='center'  >"+desc+"</td>\r\n");
					}else{
						extendtHead.append("<td class='TableRow_2rows'  style='border-right:0px;'  style='border-top:0px;' valign='middle' align='center' colspan='2' >"+desc+"</td>\r\n");
					}					
				}
				if(showaband!=null&&showaband.trim().length()!=0&& "1".equalsIgnoreCase(showaband)){
					if(method==null|| "0".equalsIgnoreCase(method)||method.trim().length()==0){
						extendtHead.append("<td class='TableRow_2rows'  style='border-right:0px;'  style='border-top:0px;' valign='middle' align='center'  >弃权</td>\r\n");
					}else{

						extendtHead.append("<td class='TableRow_2rows'  style='border-right:0px;'  style='border-top:0px;' valign='middle' align='center' colspan='2' >弃权</td>\r\n");
					}
				}
			}			
			if(showbenbu!=null&&showbenbu.trim().length()!=0&& "1".equalsIgnoreCase(showbenbu)){
				for(int k=0;k<perGradeTemplateList.size();k++){
					LazyDynaBean bean=(LazyDynaBean)perGradeTemplateList.get(k);
					String desc=(String)bean.get("gradedesc");
					if(method==null|| "0".equalsIgnoreCase(method)||method.trim().length()==0){
						extendtHead.append("<td class='TableRow_2rows'  style='border-right:0px;'  style='border-top:0px;' valign='middle' align='center'  >"+desc+"</td>\r\n");
					}else{
						extendtHead.append("<td class='TableRow_2rows'  style='border-right:0px;'  style='border-top:0px;' valign='middle' align='center' rowspan='2' >"+desc+"</td>\r\n");
					}
				}
			}
			extendtHead.append("</tr>");
			if(method==null|| "0".equalsIgnoreCase(method)||method.trim().length()==0){
				
			}else{
				
				extendtHead.append("<tr>");
				if(boydid!=null&& "all1".equalsIgnoreCase(boydid)){
					for(int da=0;da<bodyList.size();da++){
						if(perGradeTemplateList!=null){
							for(int k=0;k<perGradeTemplateList.size();k++){
								extendtHead.append("<td class='TableRow_2rows'  valign='middle' align='center'  >票&nbsp;数</td>\r\n");
								extendtHead.append("<td class='TableRow_2rows'  valign='middle' align='center'  style='border-right:0px;'  >比&nbsp;例</td>\r\n");
							}
							if(showaband!=null&&showaband.trim().length()!=0&& "1".equalsIgnoreCase(showaband)){
								if(boydid!=null&& "all1".equalsIgnoreCase(boydid)){
									extendtHead.append("<td class='TableRow_2rows'  valign='middle' align='center'  >票&nbsp;数</td>\r\n");
									extendtHead.append("<td class='TableRow_2rows'  valign='middle' align='center'  >比&nbsp;例</td>\r\n");
								}
								extendtHead.append("<td class='TableRow_2rows'  valign='middle' align='center'  >票&nbsp;数</td>\r\n");
								extendtHead.append("<td class='TableRow_2rows'  valign='middle' align='center'   style='border-right:0px;' >比&nbsp;例</td>\r\n");
							}
						}
					}
				}else{
					if(perGradeTemplateList!=null){
						for(int k=0;k<perGradeTemplateList.size();k++){
							extendtHead.append("<td class='TableRow_2rows'  valign='middle' align='center'  >票&nbsp;数</td>\r\n");
							extendtHead.append("<td class='TableRow_2rows'  valign='middle' align='center'   style='border-right:0px;' >比&nbsp;例</td>\r\n");
						}
						if(showaband!=null&&showaband.trim().length()!=0&& "1".equalsIgnoreCase(showaband)){
							extendtHead.append("<td class='TableRow_2rows'  valign='middle' align='center'  >票&nbsp;数</td>\r\n");
							extendtHead.append("<td class='TableRow_2rows'  valign='middle' align='center'   style='border-right:0px;' >比&nbsp;例</td>\r\n");
						}
					}
				}
				
				extendtHead.append("</tr>");
			}
		}
		//生成表体
		StringBuffer htmlContext=new StringBuffer();
		LazyDynaBean abean=null;
		LazyDynaBean a_bean=null;
		int rowNum=0;
		int flag=0;
		HashMap existWriteItem=new HashMap();
		for(int i=0;i<this.leafItemList.size();i++)
		{
			abean=(LazyDynaBean)this.leafItemList.get(i);
			String item_id=(String)abean.get("item_id");
			/**该项目的叶子节点(项目)个数*/
			int num=((Integer)this.itemPointNum.get(item_id)).intValue();
			ArrayList pointList=(ArrayList)this.itemToPointMap.get(item_id);
			htmlContext.append("<tr>\r\n");
			rowNum++;
			/**所有父亲列表*/
			ArrayList linkParentList=(ArrayList)this.leafItemLinkMap.get(item_id);
			for(int e=linkParentList.size()-1;e>=0;e--){
				a_bean=(LazyDynaBean)linkParentList.get(e);
				String itemid=(String)a_bean.get("item_id");
				if(existWriteItem.get(itemid)!=null) {
                    continue;
                }
				existWriteItem.put(itemid,"1");
				String itemdesc=(String)a_bean.get("itemdesc");
				  /**该项目所占的行数*/
				int rowspan=((itemPointNum.get(itemid)==null?0:((Integer)itemPointNum.get(itemid)).intValue())+(childItemLinkMap.get(itemid)==null?0:((Integer)childItemLinkMap.get(itemid)).intValue()));
				if(method==null|| "0".equalsIgnoreCase(method)||method.trim().length()==0){
					rowspan=rowspan*2;
				}else{
					
				}
				htmlContext.append(writeTd2(itemdesc,rowspan,"left",this.td_width,itemid,1));
				
				if(e!=0){
					  int layer=Integer.parseInt((String)layMap.get(itemid));//该项目在第几列
					  /**对应指标列表*/
					   ArrayList fieldlistp = (ArrayList)this.itemHaveFieldList.get(itemid);
					   if(fieldlistp!=null&&fieldlistp.size()>0){//项目下直接是指标 无任何项目
						   for(int h=0;h<fieldlistp.size();h++)
						   {
							   LazyDynaBean xbean = (LazyDynaBean)fieldlistp.get(h);
							   String point_id=(String)xbean.get("point_id");
							    if(h!=0) {
                                    htmlContext.append("<tr>\r\n");
                                }
							    for(int f=0;f<this.lay-layer;f++)
								{
								   htmlContext.append("<td align=\"left\" class='RecordRow'");
								   if(method==null|| "0".equalsIgnoreCase(method)||method.trim().length()==0){
									   htmlContext.append(" rowspan='2' ");
								   }
								   htmlContext.append(" >&nbsp;&nbsp;</td>");
								}
							    //画指标
							    htmlContext.append("<td align=\"left\" class='RecordRow' ");
								if(method==null|| "0".equalsIgnoreCase(method)||method.trim().length()==0){
									 htmlContext.append(" rowspan='2' ");
								}else{
									
								}
				    			htmlContext.append(">"+(String)xbean.get("name")+"</td>");
				    			 //画指标描述
				    			htmlContext.append("<td align=\"left\" class='RecordRow'  style='border-right:0px;' ");
								if(method==null|| "0".equalsIgnoreCase(method)||method.trim().length()==0){
									 htmlContext.append(" rowspan='2' ");
								}else{
										 
								}
					    		htmlContext.append(">"+(String)xbean.get("description")+"</td>");
					    		//画标准标度票数及占比阿什顿飞
					    		if(method==null|| "0".equalsIgnoreCase(method)||method.trim().length()==0){//纵向
					    			if(boydid!=null&&!"all".equalsIgnoreCase(boydid)){ //
					    				LazyDynaBean bbscale =(LazyDynaBean)benbu.get(point_id);
					    				HashMap bodyGrade=(HashMap)poinNumMap.get(point_id);//主体类别对应指标度票数
					    				HashMap bodyscale=(HashMap)pointScaleMap.get(point_id);//主体类别对应指标度票数
					    				for(int tem=0;tem<bodyList.size();tem++){
					    					CommonData cd=(CommonData)bodyList.get(tem);
					    					String bodyid=cd.getDataValue();
											if(boydid!=null&&!"all".equalsIgnoreCase(boydid)){
												if("all1".equalsIgnoreCase(boydid)){
													if("all".equalsIgnoreCase(bodyid)){
														continue;
													}
												}else{
													if(!bodyid.equalsIgnoreCase(boydid)){
														continue;
													}
												}
											}
					    					 LazyDynaBean ticket=(LazyDynaBean)bodyGrade.get(bodyid);// 基本标度值
											 if(ticket!=null){
												 for(int k=0;k<perGradeTemplateList.size();k++){
													 LazyDynaBean grade=(LazyDynaBean)perGradeTemplateList.get(k);
													 String gradeid=(String)grade.get("grade_template_id");
													 String ticvalue=(String)ticket.get(gradeid);
													 htmlContext.append("<td align=\"right\" style='border-right:0px;'  class='RecordRow' >");
													 htmlContext.append(ticvalue);
													 htmlContext.append("</td>");
												 }
												 if(showaband!=null&&showaband.trim().length()!=0&& "1".equalsIgnoreCase(showaband)){//显示弃权
													 String aband=(String)ticket.get("aband");
													 if("all1".equalsIgnoreCase(boydid)){
														 String total=(String)ticket.get("total");
														 htmlContext.append("<td align=\"right\" class='RecordRow' >");
														 htmlContext.append(total);
														 htmlContext.append("</td>");
													 }
													 htmlContext.append("<td align=\"right\" style='border-right:0px;'  class='RecordRow' >");
													 htmlContext.append(aband);
													 htmlContext.append("</td>");
												 }
											 }
					    				}
					    				// 画合计票数
					    				if(boydid!=null&& "all1".equalsIgnoreCase(boydid)){
					    					 LazyDynaBean ticket=(LazyDynaBean)ponitforall1.get(point_id);
											 LazyDynaBean bbscale1 =(LazyDynaBean)benbu.get(point_id);
											 if(ticket!=null){
												 for(int k=0;k<perGradeTemplateList.size();k++){
													 LazyDynaBean grade=(LazyDynaBean)perGradeTemplateList.get(k);
													 String gradeid=(String)grade.get("grade_template_id");
													 String ticvalue=(String)ticket.get(gradeid);
													 htmlContext.append("<td align=\"right\" style='border-right:0px;'  class='RecordRow' >");
													 htmlContext.append(ticvalue);
													 htmlContext.append("</td>");
												 }
												 if(showaband!=null&&showaband.trim().length()!=0&& "1".equalsIgnoreCase(showaband)){//显示弃权
													 String aband=(String)ticket.get("aband");
													 String total=(String)ticket.get("total");
													 //总投票
													 htmlContext.append("<td align=\"right\" class='RecordRow' >");
													 htmlContext.append(total);
													 htmlContext.append("</td>");
													 htmlContext.append("<td align=\"right\" style='border-right:0px;'  class='RecordRow' >");
													 htmlContext.append(aband);
													 htmlContext.append("</td>");
												 }
												 if(showbenbu!=null&&showbenbu.trim().length()!=0&& "1".equalsIgnoreCase(showbenbu)){//显示本部平均，本部平均不用显示占比所以占用两行
													 for(int k=0;k<perGradeTemplateList.size();k++){
														 LazyDynaBean grade=(LazyDynaBean)perGradeTemplateList.get(k);
														 String gradeid=(String)grade.get("grade_template_id");
														 String bbsvalue=(String)bbscale1.get(gradeid);
														 htmlContext.append("<td align=\"right\" class='RecordRow' style='border-right:0px;'  rowspan='2'>");
														 htmlContext.append(bbsvalue+"%&nbsp;");
														 htmlContext.append("</td>");
													 }
												 }
												 htmlContext.append("</tr>");

											 }
					    				}
					    				// 不是所有主题类分类归档话 本部平均
					    				if(boydid!=null&&!"all1".equalsIgnoreCase(boydid)){
						    				if(showbenbu!=null&&showbenbu.trim().length()!=0&& "1".equalsIgnoreCase(showbenbu)){//显示本部平均，本部平均不用显示占比所以占用两行
												 for(int k=0;k<perGradeTemplateList.size();k++){
													 LazyDynaBean grade=(LazyDynaBean)perGradeTemplateList.get(k);
													 String gradeid=(String)grade.get("grade_template_id");
													 String bbsvalue=(String)bbscale.get(gradeid);
													 htmlContext.append("<td align=\"right\" class='RecordRow' rowspan='2'>");
													 htmlContext.append(bbsvalue+"%&nbsp;");
													 htmlContext.append("</td>");
												 }
											 }
						    				 htmlContext.append("</tr>");//显示占比
					    				}

					    				 htmlContext.append("<tr>");//显示占比
					    				for(int tem=0;tem<bodyList.size();tem++){
					    					CommonData cd=(CommonData)bodyList.get(tem);
					    					String bodyid=cd.getDataValue();
											if(boydid!=null&&!"all".equalsIgnoreCase(boydid)){
												if("all1".equalsIgnoreCase(boydid)){
													if("all".equalsIgnoreCase(bodyid)){
														continue;
													}
												}else{
													if(!bodyid.equalsIgnoreCase(boydid)){
														continue;
													}
												}
											}
  											 LazyDynaBean scale =(LazyDynaBean)bodyscale.get(bodyid);
											 for(int k=0;k<perGradeTemplateList.size();k++){
												 LazyDynaBean grade=(LazyDynaBean)perGradeTemplateList.get(k);
												 String gradeid=(String)grade.get("grade_template_id");
												 String scalevalue=(String)scale.get(gradeid);
												 htmlContext.append("<td align=\"right\" class='RecordRow'  style='border-right:0px;' >");
												 htmlContext.append(scalevalue+"%");
												 htmlContext.append("</td>");
											 }
											 if(showaband!=null&&showaband.trim().length()!=0&& "1".equalsIgnoreCase(showaband)){//显示弃权
												 String aband=(String)scale.get("aband");
												 if("all1".equalsIgnoreCase(boydid)){
													 String total=(String)scale.get("total");
													 htmlContext.append("<td align=\"right\" class='RecordRow'  style='border-right:0px;' >");
													 htmlContext.append(total +"%");
													 htmlContext.append("</td>");
												 }
												 htmlContext.append("<td align=\"right\" class='RecordRow'  style='border-right:0px;' >");
												 htmlContext.append(aband +"%");
												 htmlContext.append("</td>");
											 }
					    				}
					    				if(boydid!=null&&!"all1".equalsIgnoreCase(boydid)){
											 htmlContext.append("</tr>");
										 }
					    				// 画合计票数占比
					    				if(boydid!=null&& "all1".equalsIgnoreCase(boydid)){// 所有考核主体分类统计画合计票数
					    					 LazyDynaBean ticket=(LazyDynaBean)ponitforall1.get(point_id);
											 LazyDynaBean scale =(LazyDynaBean)scaleforall1.get(point_id);
					    					 for(int k=0;k<perGradeTemplateList.size();k++){
												 LazyDynaBean grade=(LazyDynaBean)perGradeTemplateList.get(k);
												 String gradeid=(String)grade.get("grade_template_id");
												 String scalevalue=(String)scale.get(gradeid);
												 htmlContext.append("<td align=\"right\" class='RecordRow'  style='border-right:0px;' >");
												 htmlContext.append(scalevalue+"%");
												 htmlContext.append("</td>");
											 }
											 if(showaband!=null&&showaband.trim().length()!=0&& "1".equalsIgnoreCase(showaband)){//显示弃权
												 String aband=(String)scale.get("aband");
												 String total1=(String)scale.get("total");
												 //总投票;
												 htmlContext.append("<td align=\"right\" class='RecordRow' >");
												 htmlContext.append(total1);
												 htmlContext.append("</td>");
												 htmlContext.append("<td align=\"right\" class='RecordRow'  style='border-right:0px;' >");
												 htmlContext.append(aband +"%");
												 htmlContext.append("</td>");
											 }
											 htmlContext.append("</tr>");
					    				}

					    			}else{// 所有考核主体类别
						    				 LazyDynaBean ticket=(LazyDynaBean)poinNumMap.get(point_id);
											 LazyDynaBean scale =(LazyDynaBean)pointScaleMap.get(point_id);
											 LazyDynaBean bbscale =(LazyDynaBean)benbu.get(point_id);
											 if(ticket!=null){
												 for(int k=0;k<perGradeTemplateList.size();k++){
													 LazyDynaBean grade=(LazyDynaBean)perGradeTemplateList.get(k);
													 String gradeid=(String)grade.get("grade_template_id");
													 String ticvalue=(String)ticket.get(gradeid);
													 htmlContext.append("<td align=\"right\" class='RecordRow'  style='border-right:0px;' >");
													 htmlContext.append(ticvalue);
													 htmlContext.append("</td>");
												 }
												 if(showaband!=null&&showaband.trim().length()!=0&& "1".equalsIgnoreCase(showaband)){//显示弃权
													 String aband=(String)ticket.get("aband");
													 htmlContext.append("<td align=\"right\" class='RecordRow'  style='border-right:0px;' >");
													 htmlContext.append(aband);
													 htmlContext.append("</td>");
												 }
												 if(showbenbu!=null&&showbenbu.trim().length()!=0&& "1".equalsIgnoreCase(showbenbu)){//显示本部平均，本部平均不用显示占比所以占用两行
													 for(int k=0;k<perGradeTemplateList.size();k++){
														 LazyDynaBean grade=(LazyDynaBean)perGradeTemplateList.get(k);
														 String gradeid=(String)grade.get("grade_template_id");
														 String bbsvalue=(String)bbscale.get(gradeid);
														 htmlContext.append("<td align=\"right\" class='RecordRow' rowspan='2' style='border-right:0px;' >");
														 htmlContext.append(bbsvalue+"%&nbsp;");
														 htmlContext.append("</td>");
													 }
												 }
												 htmlContext.append("</tr>");
												 htmlContext.append("<tr>");//显示占比
												 for(int k=0;k<perGradeTemplateList.size();k++){
													 LazyDynaBean grade=(LazyDynaBean)perGradeTemplateList.get(k);
													 String gradeid=(String)grade.get("grade_template_id");
													 String scalevalue=(String)scale.get(gradeid);
													 htmlContext.append("<td align=\"right\" class='RecordRow'  style='border-right:0px;' >");
													 htmlContext.append(scalevalue+"%");
													 htmlContext.append("</td>");
												 }
												 if(showaband!=null&&showaband.trim().length()!=0&& "1".equalsIgnoreCase(showaband)){//显示弃权
													 String aband=(String)scale.get("aband");
													 htmlContext.append("<td align=\"right\" class='RecordRow'  style='border-right:0px;' >");
													 htmlContext.append(aband +"%");
													 htmlContext.append("</td>");
												 }
												 htmlContext.append("</tr>");
											 }
						    			}									
								}else{// 横向  
									
									if(boydid!=null&&!"all".equalsIgnoreCase(boydid)){
										LazyDynaBean bbscale =(LazyDynaBean)benbu.get(point_id);
					    				HashMap bodyGrade=(HashMap)poinNumMap.get(point_id);//主体类别对应指标度票数
					    				HashMap bodyscale=(HashMap)pointScaleMap.get(point_id);//主体类别对应指标度票数
					    				for(int tem=0;tem<bodyList.size();tem++){
					    					CommonData cd=(CommonData)bodyList.get(tem);
					    					String bodyid=cd.getDataValue();
											if("all1".equalsIgnoreCase(boydid)){
												if("all".equalsIgnoreCase(bodyid)){
													continue;
												}
											}else{
												if(!bodyid.equalsIgnoreCase(boydid)){
													continue;
												}
											}
					    					LazyDynaBean ticket=(LazyDynaBean)bodyGrade.get(bodyid);// 基本标度值
											LazyDynaBean scale =(LazyDynaBean)bodyscale.get(bodyid);
											if(ticket!=null){
												 for(int k=0;k<perGradeTemplateList.size();k++){
													 LazyDynaBean grade=(LazyDynaBean)perGradeTemplateList.get(k);
													 String gradeid=(String)grade.get("grade_template_id");
													 String ticvalue=(String)ticket.get(gradeid);
													 htmlContext.append("<td align=\"right\" class='RecordRow' >");
													 htmlContext.append(ticvalue);
													 htmlContext.append("</td>");
													 String scalevalue=(String)scale.get(gradeid);
													 htmlContext.append("<td align=\"right\" class='RecordRow'  style='border-right:0px;' >");
													 htmlContext.append(scalevalue+"%");
													 htmlContext.append("</td>");
												 }
												 if(showaband!=null&&showaband.trim().length()!=0&& "1".equalsIgnoreCase(showaband)){//显示弃权
													 String aband=(String)ticket.get("aband");
													 if("all1".equalsIgnoreCase(boydid)){// 总投票
														 String total=(String)ticket.get("total");
														 htmlContext.append("<td align=\"right\" class='RecordRow' >");
														 htmlContext.append(total);
														 htmlContext.append("</td>");
														 String total1=(String)scale.get("total");
														 htmlContext.append("<td align=\"right\" class='RecordRow' >");
														 htmlContext.append(total1 +"%");
														 htmlContext.append("</td>");
													 }
													 htmlContext.append("<td align=\"right\" class='RecordRow' >");
													 htmlContext.append(aband);
													 htmlContext.append("</td>");
													 String aband1=(String)scale.get("aband");
													 htmlContext.append("<td align=\"right\" class='RecordRow'  style='border-right:0px;' >");
													 htmlContext.append(aband1 +"%");
													 htmlContext.append("</td>");
												 }
												 if(boydid!=null&&!"all1".equalsIgnoreCase(boydid)){
													 if(showbenbu!=null&&showbenbu.trim().length()!=0&& "1".equalsIgnoreCase(showbenbu)){//显示本部平均，本部平均不用显示占比所以占用两行
														 for(int k=0;k<perGradeTemplateList.size();k++){
															 LazyDynaBean grade=(LazyDynaBean)perGradeTemplateList.get(k);
															 String gradeid=(String)grade.get("grade_template_id");
															 String bbsvalue=(String)bbscale.get(gradeid);
															 htmlContext.append("<td align=\"right\" class='RecordRow'  style='border-right:0px;' >");
															 htmlContext.append(bbsvalue+"%&nbsp;");
															 htmlContext.append("</td>");
														 }
													 }
												 }
											 }
					    				}
					    				if(boydid!=null&& "all1".equalsIgnoreCase(boydid)){
						    				LazyDynaBean ticket=(LazyDynaBean)ponitforall1.get(point_id);
											 LazyDynaBean scale =(LazyDynaBean)scaleforall1.get(point_id);
											 if(ticket!=null){
											 	//此处代码重复(上面已经添加过) 删掉 haosl delete 20200210
												 /* for(int k=0;k<perGradeTemplateList.size();k++){
													 LazyDynaBean grade=(LazyDynaBean)perGradeTemplateList.get(k);
													 String gradeid=(String)grade.get("grade_template_id");
													 String ticvalue=(String)ticket.get(gradeid);
													 htmlContext.append("<td align=\"right\" class='RecordRow' >");
													 htmlContext.append(ticvalue);
													 htmlContext.append("</td>");
													 String scalevalue=(String)scale.get(gradeid);
													 htmlContext.append("<td align=\"right\" class='RecordRow'  style='border-right:0px;' >");
													 htmlContext.append(scalevalue+"%");
													 htmlContext.append("</td>");
												 }
												if(showaband!=null&&showaband.trim().length()!=0&&showaband.equalsIgnoreCase("1")){//显示弃权
													 String aband=(String)ticket.get("aband");
													 String total=(String)ticket.get("total");
													 String total1=(String)scale.get("total");
													 //总投票
													 htmlContext.append("<td align=\"right\" class='RecordRow' >");
													 htmlContext.append(total);
													 htmlContext.append("</td>");
													 htmlContext.append("<td align=\"right\" class='RecordRow' >");
													 htmlContext.append(total1);
													 htmlContext.append("</td>");
													 htmlContext.append("<td align=\"right\" class='RecordRow' >");
													 htmlContext.append(aband);
													 htmlContext.append("</td>");
													 String aband1=(String)scale.get("aband");
													 htmlContext.append("<td align=\"left\"  style='border-right:0px;' class='RecordRow' >");
													 htmlContext.append(aband1 +"%");
													 htmlContext.append("</td>");
												 }*/
												 if(showbenbu!=null&&showbenbu.trim().length()!=0&& "1".equalsIgnoreCase(showbenbu)){//显示本部平均，本部平均不用显示占比所以占用两行
													 for(int k=0;k<perGradeTemplateList.size();k++){

														 LazyDynaBean grade=(LazyDynaBean)perGradeTemplateList.get(k);
														 String gradeid=(String)grade.get("grade_template_id");
														 String bbsvalue=(String)bbscale.get(gradeid);
														 htmlContext.append("<td align=\"right\" class='RecordRow'  style='border-right:0px;' >");
														 htmlContext.append(bbsvalue+"%&nbsp;");
														 htmlContext.append("</td>");
													 }
												 }
											 }
					    				}
					    				htmlContext.append("</tr>");
									}else{// 所有考核主体
										 LazyDynaBean ticket=(LazyDynaBean)poinNumMap.get(point_id);
										 LazyDynaBean scale =(LazyDynaBean)pointScaleMap.get(point_id);	 
										 LazyDynaBean bbscale =(LazyDynaBean)benbu.get(point_id);
										 if(ticket!=null){
											 for(int k=0;k<perGradeTemplateList.size();k++){
												 LazyDynaBean grade=(LazyDynaBean)perGradeTemplateList.get(k);
												 String gradeid=(String)grade.get("grade_template_id");
												 String ticvalue=(String)ticket.get(gradeid);
												 htmlContext.append("<td align=\"right\" class='RecordRow' >");
												 htmlContext.append(ticvalue);
												 htmlContext.append("</td>");
												 String scalevalue=(String)scale.get(gradeid);
												 htmlContext.append("<td align=\"right\" class='RecordRow'  style='border-right:0px;' >");
												 htmlContext.append(scalevalue+"%");
												 htmlContext.append("</td>");
											 }
											 if(showaband!=null&&showaband.trim().length()!=0&& "1".equalsIgnoreCase(showaband)){//显示弃权
												 String aband=(String)ticket.get("aband");
												 htmlContext.append("<td align=\"right\" class='RecordRow' >");
												 htmlContext.append(aband);
												 htmlContext.append("</td>");
												 String aband1=(String)scale.get("aband");
												 htmlContext.append("<td align=\"right\" class='RecordRow'  style='border-right:0px;' >");
												 htmlContext.append(aband1 +"%");
												 htmlContext.append("</td>");
											 }
											 if(showbenbu!=null&&showbenbu.trim().length()!=0&& "1".equalsIgnoreCase(showbenbu)){//显示本部平均，本部平均不用显示占比所以占用两行
												 for(int k=0;k<perGradeTemplateList.size();k++){
													 LazyDynaBean grade=(LazyDynaBean)perGradeTemplateList.get(k);
													 String gradeid=(String)grade.get("grade_template_id");
													 String bbsvalue=(String)bbscale.get(gradeid);
													 htmlContext.append("<td align=\"right\" class='RecordRow'  style='border-right:0px;' >");
													 htmlContext.append(bbsvalue+"%&nbsp;");
													 htmlContext.append("</td>");
												 }
											 }
											 htmlContext.append("</tr>");
										 }
									}
									
								}	    
						   }
						   
					   } else{
							  
							  if(ifHasChildMap.get(itemid)==null)
							  {
				    		     for(int f=0;f<this.lay-layer+1;f++)
		    	    			{
				    		    	 htmlContext.append("<td align=\"left\" class='RecordRow'");
									 if(method==null|| "0".equalsIgnoreCase(method)||method.trim().length()==0){
										  htmlContext.append(" rowspan='2' ");
									 }
									 htmlContext.append(" >&nbsp;&nbsp;</td>");
			     	    		}
				    		    htmlContext.append("<td align=\"left\" class='RecordRow'  style='border-right:0px;' ");				    		    
								if(method==null|| "0".equalsIgnoreCase(method)||method.trim().length()==0){
										htmlContext.append(" rowspan='2' ");
								}else{
											 
								}
								htmlContext.append(">&nbsp;&nbsp;</td>");
								
								if(method==null|| "0".equalsIgnoreCase(method)||method.trim().length()==0){
									 for(int k=0;k<perGradeTemplateList.size();k++){
										 htmlContext.append("<td align=\"right\" style='border-right:0px;'  class='RecordRow'");
										 htmlContext.append(" rowspan='2' ");
										 htmlContext.append(" >&nbsp;&nbsp;</td>");
									 }
									 if(showaband!=null&&showaband.trim().length()!=0&& "1".equalsIgnoreCase(showaband)){//显示弃权
											
										 htmlContext.append("<td align=\"right\" style='border-right:0px;'  class='RecordRow' rowspan='2' >");
										 htmlContext.append("&nbsp;&nbsp;");
										 htmlContext.append("</td>");
									 }
								 }else{
									 for(int k=0;k<perGradeTemplateList.size();k++){
										 htmlContext.append("<td align=\"right\" class='RecordRow'");
										 htmlContext.append("  ");
										 htmlContext.append(" >&nbsp;&nbsp;</td>");
										 htmlContext.append("<td align=\"right\" style='border-right:0px;'  class='RecordRow'");
										 htmlContext.append("  ");
										 htmlContext.append(" >&nbsp;&nbsp;</td>");
									 }
									 if(showaband!=null&&showaband.trim().length()!=0&& "1".equalsIgnoreCase(showaband)){//显示弃权
										 htmlContext.append("<td align=\"right\" class='RecordRow' >");
										 htmlContext.append("&nbsp;&nbsp;");
										 htmlContext.append("</td>");
										 htmlContext.append("<td align=\"right\" style='border-right:0px;'  class='RecordRow' >");
										 htmlContext.append("&nbsp;&nbsp;");
										 htmlContext.append("</td>");
									 }
								 }
								if(showbenbu!=null&&showbenbu.trim().length()!=0&& "1".equalsIgnoreCase(showbenbu)){//显示本部平均，本部平均不用显示占比所以占用两行
									 for(int k=0;k<perGradeTemplateList.size();k++){
										 htmlContext.append("<td align=\"\" class='RecordRow'");
										 if(method==null|| "0".equalsIgnoreCase(method)||method.trim().length()==0){
											  htmlContext.append(" rowspan='2' ");
										 }
										 htmlContext.append("  style='border-right:0px;' >&nbsp;&nbsp;</td>");
									 }
								 }
								htmlContext.append("</tr>");
				    		}
						}
				}
				if(e==0){
					int layer=Integer.parseInt((String)layMap.get(itemid));
					ArrayList fieldlist = (ArrayList)this.itemHaveFieldList.get(item_id);
					if(fieldlist!=null&&fieldlist.size()!=0){
		    			for(int x=0;x<fieldlist.size();x++)
		    			{
		    				if(x!=0) {
                                htmlContext.append("<tr>\r\n");
                            }
		    				LazyDynaBean xbean = (LazyDynaBean)fieldlist.get(x);
		    				String pointid=(String)xbean.get("point_id");
		    				 for(int f=0;f<this.lay-layer;f++)
		    	    		{
		    					 htmlContext.append("<td align=\"left\" class='RecordRow'");
								 if(method==null|| "0".equalsIgnoreCase(method)||method.trim().length()==0){
									  htmlContext.append(" rowspan='2' ");
								 }
								 htmlContext.append(" >&nbsp;&nbsp;</td>");
			     	    	}
		    				htmlContext.append("<td align=\"left\" class='RecordRow' ");
							if(method==null|| "0".equalsIgnoreCase(method)||method.trim().length()==0){
								htmlContext.append(" rowspan='2' ");
							}else{
									
							}
				    		htmlContext.append(">"+(String)xbean.get("name")+"</td>");
				    		
				    		 //画指标描述
			    			htmlContext.append("<td align=\"left\" class='RecordRow' ");
							if(method==null|| "0".equalsIgnoreCase(method)||method.trim().length()==0){
								 htmlContext.append(" rowspan='2' ");
							}else{
									 
							}
				    		htmlContext.append(">"+(String)xbean.get("description")+"</td>");
				    		
			    			
				    		if(method==null|| "0".equalsIgnoreCase(method)||method.trim().length()==0){//纵向
				    			if(boydid!=null&&!"all".equalsIgnoreCase(boydid)){ //
				    				LazyDynaBean bbscale =(LazyDynaBean)benbu.get(pointid);
				    				HashMap bodyGrade=(HashMap)poinNumMap.get(pointid);//主体类别对应指标度票数
				    				HashMap bodyscale=(HashMap)pointScaleMap.get(pointid);//主体类别对应指标度票数
				    				for(int tem=0;tem<bodyList.size();tem++){
				    					CommonData cd=(CommonData)bodyList.get(tem);
				    					String name=cd.getDataName();
				    					String bodyid=cd.getDataValue();
				    					if(boydid!=null&& "all1".equalsIgnoreCase(boydid)){
				    						if("all".equalsIgnoreCase(bodyid)){
				    							continue;
				    						}else{
				    							
				    						}
				    					}else{
				    						if(bodyid.equalsIgnoreCase(boydid)){
			    								
			    							}else{
			    								continue;
			    							}
				    					}
				    					 LazyDynaBean ticket=(LazyDynaBean)bodyGrade.get(bodyid);// 基本标度值
										 LazyDynaBean scale =(LazyDynaBean)bodyscale.get(bodyid);
										 if(ticket!=null){
											 for(int k=0;k<perGradeTemplateList.size();k++){
												 LazyDynaBean grade=(LazyDynaBean)perGradeTemplateList.get(k);
												 String gradeid=(String)grade.get("grade_template_id");
												 String ticvalue=(String)ticket.get(gradeid);
												 htmlContext.append("<td align=\"right\" class='RecordRow' >");
												 htmlContext.append(ticvalue+"&nbsp;");
												 htmlContext.append("</td>");
											 }
											 if(showaband!=null&&showaband.trim().length()!=0&& "1".equalsIgnoreCase(showaband)){//显示弃权
												 String aband=(String)ticket.get("aband");
												 if("all1".equalsIgnoreCase(boydid)){
													 String total=(String)ticket.get("total");
													 htmlContext.append("<td align=\"right\" class='RecordRow' >");
													 htmlContext.append(total+"&nbsp;");
													 htmlContext.append("</td>");
												 }
												 htmlContext.append("<td align=\"right\" class='RecordRow' >");
												 htmlContext.append(aband+"&nbsp;");
												 htmlContext.append("</td>");
											 }											 																						
										 }
				    				}
				    				// 画合计票数
				    				if(boydid!=null&& "all1".equalsIgnoreCase(boydid)){
				    					 LazyDynaBean ticket=(LazyDynaBean)ponitforall1.get(pointid);
										 LazyDynaBean scale =(LazyDynaBean)scaleforall1.get(pointid);
										 LazyDynaBean bbscale1 =(LazyDynaBean)benbu.get(pointid);
										 if(ticket!=null){
											 for(int k=0;k<perGradeTemplateList.size();k++){
												 LazyDynaBean grade=(LazyDynaBean)perGradeTemplateList.get(k);
												 String gradeid=(String)grade.get("grade_template_id");
												 String ticvalue=(String)ticket.get(gradeid);
												 htmlContext.append("<td align=\"right\" style='border-right:0px;'  class='RecordRow' >");
												 htmlContext.append(ticvalue+"&nbsp;");
												 htmlContext.append("</td>");
											 }
											 if(showaband!=null&&showaband.trim().length()!=0&& "1".equalsIgnoreCase(showaband)){//显示弃权
												 String aband=(String)ticket.get("aband");
												 String total=(String)ticket.get("total");

												 //总投票
												 htmlContext.append("<td align=\"right\" class='RecordRow' >");
												 htmlContext.append(total+"&nbsp;");
												 htmlContext.append("</td>");
												 
												 htmlContext.append("<td align=\"right\" style='border-right:0px;'  class='RecordRow' >");
												 htmlContext.append(aband+"&nbsp;");
												 htmlContext.append("</td>");
											 }
											 if(showbenbu!=null&&showbenbu.trim().length()!=0&& "1".equalsIgnoreCase(showbenbu)){//显示本部平均，本部平均不用显示占比所以占用两行
												 for(int k=0;k<perGradeTemplateList.size();k++){
													 LazyDynaBean grade=(LazyDynaBean)perGradeTemplateList.get(k);
													 String gradeid=(String)grade.get("grade_template_id");
													 String bbsvalue=(String)bbscale1.get(gradeid);
													 htmlContext.append("<td align=\"right\" class='RecordRow' style='border-right:0px;'  rowspan='2'>");
													 htmlContext.append(bbsvalue+"%&nbsp;");
													 htmlContext.append("</td>");
												 }
											 }
										}
				    				}
				    				if(boydid!=null&&!"all1".equalsIgnoreCase(boydid)){
					    				if(showbenbu!=null&&showbenbu.trim().length()!=0&& "1".equalsIgnoreCase(showbenbu)){//显示本部平均，本部平均不用显示占比所以占用两行
											 for(int k=0;k<perGradeTemplateList.size();k++){
												 LazyDynaBean grade=(LazyDynaBean)perGradeTemplateList.get(k);
												 String gradeid=(String)grade.get("grade_template_id");
												 String bbsvalue=(String)bbscale.get(gradeid);
												 htmlContext.append("<td align=\"right\" class='RecordRow' style='border-right:0px;'  rowspan='2'>");
												 htmlContext.append(bbsvalue+"%&nbsp;");
												 htmlContext.append("</td>");
											 }
										 }
					    				//显示占比
				    				}
				    				htmlContext.append("</tr>");
				    				
				    				htmlContext.append("<tr>");//显示占比
				    				for(int tem=0;tem<bodyList.size();tem++){
				    					CommonData cd=(CommonData)bodyList.get(tem);
				    					String name=cd.getDataName();
				    					String bodyid=cd.getDataValue();
				    					if(boydid!=null&& "all1".equalsIgnoreCase(boydid)){
				    						if("all".equalsIgnoreCase(bodyid)){
				    							continue;
				    						}else{
				    							
				    						}
				    					}else{
				    						if(bodyid.equalsIgnoreCase(boydid)){
			    								
			    							}else{
			    								continue;
			    							}
				    					}
					    				 LazyDynaBean ticket=(LazyDynaBean)bodyGrade.get(bodyid);// 基本标度值
										 LazyDynaBean scale =(LazyDynaBean)bodyscale.get(bodyid);
										 for(int k=0;k<perGradeTemplateList.size();k++){
											 LazyDynaBean grade=(LazyDynaBean)perGradeTemplateList.get(k);
											 String gradeid=(String)grade.get("grade_template_id");
											 String scalevalue=(String)scale.get(gradeid);
											 htmlContext.append("<td align=\"right\" class='RecordRow'  style='border-right:0px;' >");
											 htmlContext.append(scalevalue+"%&nbsp;");
											 htmlContext.append("</td>");
										 }
										 if(showaband!=null&&showaband.trim().length()!=0&& "1".equalsIgnoreCase(showaband)){//显示弃权
											 String aband=(String)scale.get("aband");
											 if("all1".equalsIgnoreCase(boydid)){
												 String total=(String)scale.get("total");
												 htmlContext.append("<td align=\"right\" class='RecordRow'  style='border-right:0px;' >");
												 htmlContext.append(total +"%&nbsp;");
												 htmlContext.append("</td>");
											 }
											 htmlContext.append("<td align=\"right\" class='RecordRow'  style='border-right:0px;' >");
											 htmlContext.append(aband +"%&nbsp;");
											 htmlContext.append("</td>");
										 }										 
				    				}
				    				// 画合计票数占比
				    				if(boydid!=null&& "all1".equalsIgnoreCase(boydid)){// 所有考核主体分类统计画合计票数
				    					 LazyDynaBean ticket=(LazyDynaBean)ponitforall1.get(pointid);
										 LazyDynaBean scale =(LazyDynaBean)scaleforall1.get(pointid);	
				    					 for(int k=0;k<perGradeTemplateList.size();k++){
											 LazyDynaBean grade=(LazyDynaBean)perGradeTemplateList.get(k);
											 String gradeid=(String)grade.get("grade_template_id");
											 String scalevalue=(String)scale.get(gradeid);
											 htmlContext.append("<td align=\"right\" class='RecordRow'  style='border-right:0px;' >");
											 htmlContext.append(scalevalue+"%&nbsp;");
											 htmlContext.append("</td>");
										 }
										 if(showaband!=null&&showaband.trim().length()!=0&& "1".equalsIgnoreCase(showaband)){//显示弃权
											 String aband=(String)scale.get("aband");
											 String total1=(String)scale.get("total");
											 //总投票
											 htmlContext.append("<td align=\"right\" class='RecordRow' >");
											 htmlContext.append(total1+"&nbsp;");
											 htmlContext.append("</td>");
											 htmlContext.append("<td align=\"right\" class='RecordRow'  style='border-right:0px;' >");
											 htmlContext.append(aband +"%&nbsp;");
											 htmlContext.append("</td>");
										 }
										
				    				}
				    				htmlContext.append("</tr>");
				    			}else{// 所有考核主体类别
				    				 LazyDynaBean ticket=(LazyDynaBean)poinNumMap.get(pointid);
									 LazyDynaBean scale =(LazyDynaBean)pointScaleMap.get(pointid);
									 LazyDynaBean bbscale =(LazyDynaBean)benbu.get(pointid);
									 if(ticket!=null){
										 for(int k=0;k<perGradeTemplateList.size();k++){
											 LazyDynaBean grade=(LazyDynaBean)perGradeTemplateList.get(k);
											 String gradeid=(String)grade.get("grade_template_id");
											 String ticvalue=(String)ticket.get(gradeid);
											 htmlContext.append("<td align=\"right\" style='border-right:0px;'  class='RecordRow' >");
											 htmlContext.append(ticvalue+"&nbsp;");
											 htmlContext.append("</td>");
										 }
										 if(showaband!=null&&showaband.trim().length()!=0&& "1".equalsIgnoreCase(showaband)){//显示弃权
											 String aband=(String)ticket.get("aband");										
											 htmlContext.append("</td>");
											 htmlContext.append("<td align=\"right\" style='border-right:0px;'  class='RecordRow' >");
											 htmlContext.append(aband+"&nbsp;");
											 htmlContext.append("</td>");
										 }
										 if(showbenbu!=null&&showbenbu.trim().length()!=0&& "1".equalsIgnoreCase(showbenbu)){//显示本部平均，本部平均不用显示占比所以占用两行
											 for(int k=0;k<perGradeTemplateList.size();k++){
												 LazyDynaBean grade=(LazyDynaBean)perGradeTemplateList.get(k);
												 String gradeid=(String)grade.get("grade_template_id");
												 String bbsvalue=(String)bbscale.get(gradeid);
												 htmlContext.append("<td align=\"right\" class='RecordRow' style='border-right:0px;'  rowspan='2'>");
												 htmlContext.append(bbsvalue+"%&nbsp;");
												 htmlContext.append("</td>");
											 }
										 }
										 htmlContext.append("</tr>");
										 htmlContext.append("<tr>");//显示占比
										 for(int k=0;k<perGradeTemplateList.size();k++){
											 LazyDynaBean grade=(LazyDynaBean)perGradeTemplateList.get(k);
											 String gradeid=(String)grade.get("grade_template_id");
											 String scalevalue=(String)scale.get(gradeid);
											 htmlContext.append("<td align=\"right\" class='RecordRow'  style='border-right:0px;' >");
											 htmlContext.append(scalevalue+"%&nbsp;");
											 htmlContext.append("</td>");
										 }
										 if(showaband!=null&&showaband.trim().length()!=0&& "1".equalsIgnoreCase(showaband)){//显示弃权
											 String aband=(String)scale.get("aband");										
											 htmlContext.append("<td align=\"right\" class='RecordRow'  style='border-right:0px;' >");
											 htmlContext.append(aband +"%&nbsp;");
											 htmlContext.append("</td>");
										 }
										 htmlContext.append("</tr>");
									 }
				    			}
								
							}else{// 横向
								if(boydid!=null&&!"all".equalsIgnoreCase(boydid)){
									LazyDynaBean bbscale =(LazyDynaBean)benbu.get(pointid);
				    				HashMap bodyGrade=(HashMap)poinNumMap.get(pointid);//主体类别对应指标度票数
				    				HashMap bodyscale=(HashMap)pointScaleMap.get(pointid);//主体类别对应指标度票数
				    				for(int tem=0;tem<bodyList.size();tem++){
				    					CommonData cd=(CommonData)bodyList.get(tem);
				    					String name=cd.getDataName();
				    					String bodyid=cd.getDataValue();
				    					if(boydid!=null&& "all1".equalsIgnoreCase(boydid)){
				    						if("all".equalsIgnoreCase(bodyid)|| "all1".equalsIgnoreCase(bodyid)){
				    							continue;
				    						}else{
				    							
				    						}
				    					}else{
				    						if(bodyid.equalsIgnoreCase(boydid)){
			    								
			    							}else{
			    								continue;
			    							}
				    					}
				    					LazyDynaBean ticket=(LazyDynaBean)bodyGrade.get(bodyid);// 基本标度值
										LazyDynaBean scale =(LazyDynaBean)bodyscale.get(bodyid);
										if(ticket!=null){
											 for(int k=0;k<perGradeTemplateList.size();k++){
												 LazyDynaBean grade=(LazyDynaBean)perGradeTemplateList.get(k);
												 String gradeid=(String)grade.get("grade_template_id");
												 String ticvalue=(String)ticket.get(gradeid);
												 htmlContext.append("<td align=\"right\" class='RecordRow' >");
												 htmlContext.append(ticvalue+"&nbsp;");
												 htmlContext.append("</td>");
												 String scalevalue=(String)scale.get(gradeid);
												 htmlContext.append("<td align=\"right\" class='RecordRow'  style='border-right:0px;' >");
												 htmlContext.append(scalevalue+"%&nbsp;");
												 htmlContext.append("</td>");
											 }
											 if(showaband!=null&&showaband.trim().length()!=0&& "1".equalsIgnoreCase(showaband)){//显示弃权
												 String aband=(String)ticket.get("aband");
												 if("all1".equalsIgnoreCase(boydid)){// 总投票
													 String total=(String)ticket.get("total");
													 htmlContext.append("<td align=\"right\" class='RecordRow' >");
													 htmlContext.append(total+"&nbsp;");
													 htmlContext.append("</td>");
													 String total1=(String)scale.get("total");
													 htmlContext.append("<td align=\"right\" class='RecordRow' >");
													 htmlContext.append(total1 +"%&nbsp;");
													 htmlContext.append("</td>");
												 }
												 htmlContext.append("<td align=\"right\" class='RecordRow' >");
												 htmlContext.append(aband+"&nbsp;");
												 htmlContext.append("</td>");
												 String aband1=(String)scale.get("aband");
												 htmlContext.append("<td align=\"right\" class='RecordRow' style='border-right:0px;'  >");
												 htmlContext.append(aband1 +"%&nbsp");
												 htmlContext.append("</td>");
											 }
											 
											 
										 }
				    				}
				    				if(boydid!=null&& "all1".equalsIgnoreCase(boydid)){
					    				 LazyDynaBean ticket=(LazyDynaBean)ponitforall1.get(pointid);
										 LazyDynaBean scale =(LazyDynaBean)scaleforall1.get(pointid);	 
										 if(ticket!=null){
											 for(int k=0;k<perGradeTemplateList.size();k++){
												 LazyDynaBean grade=(LazyDynaBean)perGradeTemplateList.get(k);
												 String gradeid=(String)grade.get("grade_template_id");
												 String ticvalue=(String)ticket.get(gradeid);
												 htmlContext.append("<td align=\"right\" class='RecordRow' >");
												 htmlContext.append(ticvalue+"&nbsp;");
												 htmlContext.append("</td>");
												 String scalevalue=(String)scale.get(gradeid);
												 htmlContext.append("<td align=\"right\" class='RecordRow'  style='border-right:0px;' >");
												 htmlContext.append(scalevalue+"%&nbsp;");
												 htmlContext.append("</td>");
											 }
											 if(showaband!=null&&showaband.trim().length()!=0&& "1".equalsIgnoreCase(showaband)){//显示弃权
												 String aband=(String)ticket.get("aband");
												 String total=(String)ticket.get("total");
												 String total1=(String)scale.get("total");
												 //总投票
												 htmlContext.append("<td align=\"right\" class='RecordRow' >");
												 htmlContext.append(total+"&nbsp;");
												 htmlContext.append("</td>");
												 htmlContext.append("<td align=\"right\" class='RecordRow' >");
												 htmlContext.append(total1+"%&nbsp;");
												 htmlContext.append("</td>");											 
												 htmlContext.append("<td align=\"right\" class='RecordRow' >");
												 htmlContext.append(aband);
												 htmlContext.append("</td>");
												 String aband1=(String)scale.get("aband");												
												 htmlContext.append("<td align=\"right\" class='RecordRow'  style='border-right:0px;' >");
												 htmlContext.append(aband1 +"%&nbsp");
												 htmlContext.append("</td>");
											 }
											 if(showbenbu!=null&&showbenbu.trim().length()!=0&& "1".equalsIgnoreCase(showbenbu)){//显示本部平均，本部平均不用显示占比所以占用两行
												 for(int k=0;k<perGradeTemplateList.size();k++){
													 LazyDynaBean grade=(LazyDynaBean)perGradeTemplateList.get(k);
													 String gradeid=(String)grade.get("grade_template_id");
													 String bbsvalue=(String)bbscale.get(gradeid);
													 htmlContext.append("<td align=\"right\" class='RecordRow'  style='border-right:0px;' >");
													 htmlContext.append(bbsvalue+"%&nbsp;");
													 htmlContext.append("</td>");
												 }
											 }
										 }
				    				}
				    				if(boydid!=null&&!"all1".equalsIgnoreCase(boydid)){
				    				if(showbenbu!=null&&showbenbu.trim().length()!=0&& "1".equalsIgnoreCase(showbenbu)){//显示本部平均，本部平均不用显示占比所以占用两行
										 for(int k=0;k<perGradeTemplateList.size();k++){
											 LazyDynaBean grade=(LazyDynaBean)perGradeTemplateList.get(k);
											 String gradeid=(String)grade.get("grade_template_id");
											 String bbsvalue=(String)bbscale.get(gradeid);
											 htmlContext.append("<td align=\"right\" class='RecordRow'  style='border-right:0px;' >");
											 htmlContext.append(bbsvalue+"%&nbsp;");
											 htmlContext.append("</td>");
										 }
									 }
				    				}
				    				htmlContext.append("</tr>");
								}else{// 所有考核主体
									 LazyDynaBean ticket=(LazyDynaBean)poinNumMap.get(pointid);
									 LazyDynaBean scale =(LazyDynaBean)pointScaleMap.get(pointid);	 
									 LazyDynaBean bbscale =(LazyDynaBean)benbu.get(pointid);
									 if(ticket!=null){
										 for(int k=0;k<perGradeTemplateList.size();k++){
											 LazyDynaBean grade=(LazyDynaBean)perGradeTemplateList.get(k);
											 String gradeid=(String)grade.get("grade_template_id");
											 String ticvalue=(String)ticket.get(gradeid);
											 htmlContext.append("<td align=\"right\" class='RecordRow' >");
											 htmlContext.append(ticvalue+"&nbsp;");
											 htmlContext.append("</td>");
											 String scalevalue=(String)scale.get(gradeid);
											 htmlContext.append("<td align=\"right\" class='RecordRow'  style='border-right:0px;' >");
											 htmlContext.append(scalevalue+"%&nbsp;");
											 htmlContext.append("</td>");
										 }
										 if(showaband!=null&&showaband.trim().length()!=0&& "1".equalsIgnoreCase(showaband)){//显示弃权
											 String aband=(String)ticket.get("aband");
											 htmlContext.append("<td align=\"right\" class='RecordRow' >");
											 htmlContext.append(aband+"&nbsp;");
											 htmlContext.append("</td>");
											 String aband1=(String)scale.get("aband");
											 htmlContext.append("<td align=\"right\" class='RecordRow'  style='border-right:0px;' >");
											 htmlContext.append(aband1 +"%&nbsp;");
											 htmlContext.append("</td>");
										 }
										 if(showbenbu!=null&&showbenbu.trim().length()!=0&& "1".equalsIgnoreCase(showbenbu)){//显示本部平均，本部平均不用显示占比所以占用两行
											 for(int k=0;k<perGradeTemplateList.size();k++){
												 LazyDynaBean grade=(LazyDynaBean)perGradeTemplateList.get(k);
												 String gradeid=(String)grade.get("grade_template_id");
												 String bbsvalue=(String)bbscale.get(gradeid);
												 htmlContext.append("<td align=\"right\" class='RecordRow'  style='border-right:0px;' >");
												 htmlContext.append(bbsvalue+"%&nbsp;");
												 htmlContext.append("</td>");
											 }
										 }
										 htmlContext.append("</tr>");
									 }
								}
								
							}	    
		    			}
					}else{
						for(int f=0;f<this.lay-layer+1;f++)
	    	    		{
							htmlContext.append("<td align=\"left\" class='RecordRow'");
							 if(method==null|| "0".equalsIgnoreCase(method)||method.trim().length()==0){
								  htmlContext.append(" rowspan='2' ");
							 }
							 htmlContext.append(" >&nbsp;&nbsp;</td>");
		     	    	}
						htmlContext.append("<td align=\"left\" class='RecordRow' ");				    		    
						if(method==null|| "0".equalsIgnoreCase(method)||method.trim().length()==0){
								htmlContext.append(" rowspan='2' ");
						}else{
									 
						}
						htmlContext.append("  style='border-right:0px;' >&nbsp;&nbsp;</td>");
						
						if(method==null|| "0".equalsIgnoreCase(method)||method.trim().length()==0){
							 for(int k=0;k<perGradeTemplateList.size();k++){
								 htmlContext.append("<td align=\"right\" class='RecordRow'");
								 htmlContext.append("  style='border-right:0px;' >&nbsp;&nbsp;</td>");
							 }
							 if(showaband!=null&&showaband.trim().length()!=0&& "1".equalsIgnoreCase(showaband)){//显示弃权
									
								 htmlContext.append("<td align=\"right\" class='RecordRow' style='border-right:0px;' >");
								 htmlContext.append("&nbsp;&nbsp;");
								 htmlContext.append("</td>");
							 }
						 }else{
							 for(int k=0;k<perGradeTemplateList.size();k++){
								 htmlContext.append("<td align=\"right\" class='RecordRow'");
								 htmlContext.append("  ");
								 htmlContext.append(" >&nbsp;&nbsp;</td>");
								 htmlContext.append("<td align=\"right\" class='RecordRow'");
								 htmlContext.append("  ");
								 htmlContext.append("  style='border-right:0px;' >&nbsp;&nbsp;</td>");
							 }
							 if(showaband!=null&&showaband.trim().length()!=0&& "1".equalsIgnoreCase(showaband)){//显示弃权
								 htmlContext.append("<td align=\"right\" class='RecordRow'  style='border-right:0px;' >");
								 htmlContext.append("&nbsp;&nbsp;");
								 htmlContext.append("</td>");
								 htmlContext.append("<td align=\"right\" class='RecordRow' >");
								 htmlContext.append("&nbsp;&nbsp;");
								 htmlContext.append("</td>");
							 }
						 }
						if(showbenbu!=null&&showbenbu.trim().length()!=0&& "1".equalsIgnoreCase(showbenbu)){//显示本部平均，本部平均不用显示占比所以占用两行
							for(int k=0;k<perGradeTemplateList.size();k++){
								LazyDynaBean grade=(LazyDynaBean)perGradeTemplateList.get(k);
								String gradeid=(String)grade.get("grade_template_id");
								htmlContext.append("<td align=\"right\" class='RecordRow'  style='border-right:0px;'");
								if(method==null|| "0".equalsIgnoreCase(method)||method.trim().length()==0){
									htmlContext.append(" rowspan='2' ");
								}
								htmlContext.append(">&nbsp;&nbsp;");
								htmlContext.append("</td>");
							}
						 }
						htmlContext.append("</tr>");
						//纵向显示需要增加占比行
						if(method==null|| "0".equalsIgnoreCase(method)||method.trim().length()==0){
							htmlContext.append("<tr>");
							for(int k=0;k<perGradeTemplateList.size();k++){
								htmlContext.append("<td align=\"right\" class='RecordRow'");
								htmlContext.append("  ");
								htmlContext.append(" >&nbsp;&nbsp;</td>");
							}
							if(showaband!=null&&showaband.trim().length()!=0&& "1".equalsIgnoreCase(showaband)){//显示弃权

								htmlContext.append("<td align=\"right\" class='RecordRow' style='border-right:0px;' >");
								htmlContext.append("&nbsp;&nbsp;");
								htmlContext.append("</td>");
							}
							htmlContext.append("</tr>");
						}
					}
				}
			}
		}
		
		
		if(showComment!=null&& "true".equalsIgnoreCase(showComment)){// 画总体评价
			htmlContext.append("<tr>");
			int lays = lay+2;
			htmlContext.append("<td class='RecordRow' align='center' style='border-left:0px;' colspan='"+lays+"' ");
			HashMap objectpoint=(HashMap)comment.get(0);
			HashMap objectscale=(HashMap)comment.get(1);
			HashMap benbuscale=(HashMap)comment.get(2);

			if(method!=null&&!"2".equalsIgnoreCase(method)){
				htmlContext.append("rowspan='2'");
			}
			htmlContext.append(">");
			 htmlContext.append("总体评价");
			 htmlContext.append("</td > ");
			for(int k=0;k<=bodyList.size();k++){
				CommonData cd=null;
				String bodyid="";
				String name="";
				if(k==bodyList.size()){
					if("all1".equalsIgnoreCase(boydid)){
						bodyid="total";
					}else{
						continue;
					}
				}else{
					cd=(CommonData)bodyList.get(k);
					bodyid=cd.getDataValue();
					name=cd.getDataName();
				}				
				if("all1".equalsIgnoreCase(boydid)){
					if("all1".equalsIgnoreCase(bodyid)|| "all".equalsIgnoreCase(bodyid)){
						continue;
					}else{
						
					}
				}else{
					if(bodyid.equalsIgnoreCase(boydid)){
						
					}else{
						if(!"total".equalsIgnoreCase(bodyid)){
							continue;
						}
						
					}
				}
				
				HashMap descpoint=(HashMap)objectpoint.get(bodyid);
				HashMap descscale=(HashMap)objectscale.get(bodyid);
				for(int m=0;m<perGradeTemplateList.size();m++){
					 LazyDynaBean grade=(LazyDynaBean)perGradeTemplateList.get(m);
					 String desc=(String)grade.get("grade_template_id");
					 String descpointvalue=(String)descpoint.get(desc);
					 htmlContext.append("<td class='RecordRow' style='border-right:0px;'  align='right' > ");
					 htmlContext.append(descpointvalue+"&nbsp;");
					 htmlContext.append("</td > ");
					 if(method!=null&& "2".equalsIgnoreCase(method)){//横向
						 String descscalevalue=(String)descscale.get(desc);
						 htmlContext.append("<td class='RecordRow' style='border-right:0px;'  align='right' > ");
						 htmlContext.append(descscalevalue+"%&nbsp;");
						 htmlContext.append("</td > ");
					 }else{
						 
					 } 
				}
				if(showaband!=null&& "1".equalsIgnoreCase(showaband.trim())){//画弃权和总投票
					 if("all1".equalsIgnoreCase(boydid)){//显示总投票
						 String descpointTotalvalue=(String)descpoint.get("total");
						 htmlContext.append("<td class='RecordRow' align='right'  > ");
						 htmlContext.append(descpointTotalvalue+"&nbsp;");
						 htmlContext.append("</td > ");
						 if(method!=null&& "2".equalsIgnoreCase(method)){//横向
							 String descscaleTotalvalue=(String)descscale.get("total");
							 htmlContext.append("<td class='RecordRow' align='right' > ");
							 htmlContext.append(descscaleTotalvalue+"%&nbsp;");
							 htmlContext.append("</td > ");
						 }
					 }
					 String descpointabandvalue=(String)descpoint.get("aband");//弃权
					 htmlContext.append("<td class='RecordRow' style='border-right:0px;'  align='right' > ");
					 htmlContext.append(descpointabandvalue+"&nbsp;");
					 htmlContext.append("</td > ");
					 if(method!=null&& "2".equalsIgnoreCase(method)){//横向
						 String descscaleabandvalue=(String)descscale.get("aband");
						 htmlContext.append("<td class='RecordRow' style='border-right:0px;'  align='right' > ");
						 htmlContext.append(descscaleabandvalue+"%&nbsp;");
						 htmlContext.append("</td > ");
					 } 
				 }				
			}
			if(showbenbu!=null&&showbenbu.trim().length()!=0&& "1".equalsIgnoreCase(showbenbu)){
				for(int m=0;m<perGradeTemplateList.size();m++){
					 LazyDynaBean grade=(LazyDynaBean)perGradeTemplateList.get(m);
					 String desc=(String)grade.get("grade_template_id");
					 String benbuvalue=(String)benbuscale.get(desc);
					 htmlContext.append("<td class='RecordRow' align='right' style='border-right:0px;'  rowspan='2'> ");
					 htmlContext.append(benbuvalue+"%&nbsp;");
					 htmlContext.append("</td > ");
				}
			}			
			htmlContext.append("</tr>");			
			if(method!=null&& "2".equalsIgnoreCase(method)){
				
			}else{
				htmlContext.append("<tr>");
				for(int k=0;k<=bodyList.size();k++){
					CommonData cd=null;
					String bodyid="";
					String name="";
					if(k==bodyList.size()){
						if("all1".equalsIgnoreCase(boydid)){
							bodyid="total";
						}else{
							continue;
						}
					}else{
						bodyid="total";
						cd=(CommonData)bodyList.get(k);
						bodyid=cd.getDataValue();
						name=cd.getDataName();
					}
					
					if("all1".equalsIgnoreCase(boydid)){
						if("all1".equalsIgnoreCase(bodyid)|| "all".equalsIgnoreCase(bodyid)){
							continue;
						}else{
							
						}
					}else{
						if(bodyid.equalsIgnoreCase(boydid)){
							
						}else{
							if(!"total".equalsIgnoreCase(bodyid)){
								continue;
							}
							
						}
					}
					HashMap descpoint=(HashMap)objectpoint.get(bodyid);
					HashMap descscale=(HashMap)objectscale.get(bodyid);
					for(int m=0;m<perGradeTemplateList.size();m++){
						 LazyDynaBean grade=(LazyDynaBean)perGradeTemplateList.get(m);
						 String desc=(String)grade.get("grade_template_id");						
						 String descscalevalue=(String)descscale.get(desc);
						 htmlContext.append("<td class='RecordRow'  style='border-right:0px;' align='right' > ");
						 htmlContext.append(descscalevalue+"%&nbsp;");
						 htmlContext.append("</td > ");
						 
					}
					if(showaband!=null&& "1".equalsIgnoreCase(showaband.trim())){//画弃权和总投票
						 if("all1".equalsIgnoreCase(boydid)){//显示总投票
							String descscaleTotalvalue=(String)descscale.get("total");
							htmlContext.append("<td class='RecordRow' align='right' > ");
							htmlContext.append(descscaleTotalvalue+"%&nbsp;");
							htmlContext.append("</td > ");
						 }
						String descscaleabandvalue=(String)descscale.get("aband");
						htmlContext.append("<td class='RecordRow' style='border-right:0px;'  align='right' > ");
						htmlContext.append(descscaleabandvalue+"%&nbsp;");
						htmlContext.append("</td > ");
						  
					 }
				}
				htmlContext.append("</tr>");
			}
			
		}
		htmlContext.append("</table>");
		html="<table id='table-vote' class='ListTable' width='"+nums*250+"' >"+extendtHead+htmlContext.toString();
		return html;
	}
	/**
     * 取得标准标度列表
     * 
     * @return
     */
	public ArrayList getPerGradeTemplateList(String templateID)
	{	
		ArrayList list = new ArrayList();
		try
		{
			PersonPostModalBo ppo = new PersonPostModalBo(this.conn);
			String per_comTable = "per_grade_template"; // 绩效标准标度
			if(ppo.getComOrPer(templateID,"temp")) {
                per_comTable = "per_grade_competence"; // 能力素质标准标度
            }
		    String sql = "select * from "+per_comTable+" order by gradevalue desc";
		    ContentDAO dao = new ContentDAO(this.conn);
		    RowSet rowSet = dao.search(sql);
		    LazyDynaBean abean = null;
		    while (rowSet.next())
		    {
				abean = new LazyDynaBean();
				abean.set("grade_template_id", rowSet.getString("grade_template_id"));
				abean.set("gradevalue", rowSet.getString("gradevalue"));
				abean.set("gradedesc", rowSet.getString("gradedesc"));
				abean.set("top_value", rowSet.getString("top_value"));
				abean.set("bottom_value", rowSet.getString("bottom_value"));
				list.add(abean);
		    }
		    
		    if(rowSet!=null) {
                rowSet.close();
            }
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return list;
	}
	public String writeHtml()
	{
		
		StringBuffer htmlContext=new StringBuffer("");
		StringBuffer score = new StringBuffer("");
		StringBuffer r_item=new StringBuffer("");
		HashMap existWriteItem=new HashMap();
		LazyDynaBean abean=null;
		LazyDynaBean a_bean=null;
		StringBuffer extendtHead = new StringBuffer();
		//ContentDAO dao = new ContentDAO(this.conn);
		
		
		 //输出表头
		extendtHead.append("<tr class='trDeep_self'  height='20' >\r\n");
		extendtHead.append("<td width='25%' class='TableRow_2rows'  valign='middle' align='center'  colspan='"+this.lay+"'>项&nbsp;目&nbsp;名&nbsp;称</td>\r\n");
        if("1".equals(this.status))
        {
            extendtHead.append("<td width='55%' class='TableRow_2rows'  valign='middle' align='center' >指&nbsp;标&nbsp;名&nbsp;称</td>\r\n");
        }else{
            extendtHead.append("<td width='65%' class='TableRow_2rows'  valign='middle' align='center' >指&nbsp;标&nbsp;名&nbsp;称</td>\r\n");
        }
		extendtHead.append("<td width='10%' class='TableRow_2rows'  valign='middle' align='center'>分&nbsp;值</td>\r\n");
       /**如果为权重的话，加一列权重*/
		if("1".equals(this.status))
        {
			extendtHead.append("<td width='10%' class='TableRow_2rows'  valign='middle' align='center' >权&nbsp;重</td>\r\n");
        }		
		int rowNum=0;
		int flag=0;
		/**所有的叶子项目*/
		for(int i=0;i<this.leafItemList.size();i++)
		{
			abean=(LazyDynaBean)this.leafItemList.get(i);
			String item_id=(String)abean.get("item_id");
			/**该项目的叶子节点(项目)个数*/
			int num=((Integer)this.itemPointNum.get(item_id)).intValue();
			ArrayList pointList=(ArrayList)this.itemToPointMap.get(item_id);
			/*for(int j=0;j<num;j++)
			{*/
				
				htmlContext.append("<tr>\r\n");
				rowNum++;
				/**所有父亲列表*/
				ArrayList linkParentList=(ArrayList)this.leafItemLinkMap.get(item_id);
				int current=linkParentList.size();
				/**叶子项目的继承关系列表*/
				for(int e=linkParentList.size()-1;e>=0;e--)
				{
					a_bean=(LazyDynaBean)linkParentList.get(e);
					String itemid=(String)a_bean.get("item_id");
					if(existWriteItem.get(itemid)!=null) {
                        continue;
                    }
					existWriteItem.put(itemid,"1");
					String itemdesc=(String)a_bean.get("itemdesc");
				    /**该项目所占的行数*/
					int colspan=((itemPointNum.get(itemid)==null?0:((Integer)itemPointNum.get(itemid)).intValue())+(childItemLinkMap.get(itemid)==null?0:((Integer)childItemLinkMap.get(itemid)).intValue()));
					/**画出该项目*/
					htmlContext.append(writeTd(itemdesc,colspan,"left",this.td_width,itemid,1));
					if(e!=0)
					{
						/**该项目的层数*/
					   int layer=Integer.parseInt((String)layMap.get(itemid));
					   /**对应指标列表*/
					   ArrayList fieldlistp = (ArrayList)this.itemHaveFieldList.get(itemid);
					   /**该项目有指标*/
					   if(fieldlistp!=null&&fieldlistp.size()>0)
					   {
						   for(int h=0;h<fieldlistp.size();h++)
						   {
							   LazyDynaBean xbean = (LazyDynaBean)fieldlistp.get(h);
							    if(h!=0) {
                                    htmlContext.append("<tr>\r\n");
                                }
							    for(int f=0;f<this.lay-layer;f++)
								{
								   htmlContext.append("<td align=\"left\" class='RecordRow'>&nbsp;&nbsp;</td>");
								}
							    htmlContext.append("<td align=\"left\" class='RecordRow' ");
				    			if("1".equals(this.isVisible)) {
                                    htmlContext.append(" onclick='changeColor(\""+(String)xbean.get("point_id")+"\",\""+2+"\")' id='"+(String)xbean.get("point_id")+"'");
                                }
				    			htmlContext.append(">"+(String)xbean.get("name")+"</td>");
				    			htmlContext.append("<td align=\"right\" width=\"100\" class='RecordRow'>");
				    			if("1".equals(this.isVisible))
				    			{
				    				htmlContext.append("<input onkeydown=\"checkKeyCode();\" type=\"text\" class=\"Input_self common_border_color\" name=\"score\" id=\"s_"+(String)xbean.get("point_id")+"\" value=\""+(String)xbean.get("score")+"\" maxlength='10'/>");
				    				score.append(","+(String)xbean.get("point_id"));
				    			}
				    			else {
                                    htmlContext.append((String)xbean.get("score"));
                                }
				    			htmlContext.append("</td>");
				    			if("1".equals(this.status))
				    			{
				    				htmlContext.append("<td align=\"right\" class='RecordRow'>");
					    			if("1".equals(this.isVisible))
					    			{
					    				htmlContext.append("<input onBlur=\"checkValue(this);\" onFocus=\"saveBeforeValue(this);\" onkeydown=\"checkKeyCode();\" type=\"text\" class=\"Input_self common_border_color\" name=\"score\" id=\"r_"+(String)xbean.get("point_id")+"\" value=\""+(String)xbean.get("rank")+"\" maxlength='10'/>");
					    			}
					    			else {
                                        htmlContext.append((String)xbean.get("rank"));
                                    }
					    			htmlContext.append("</td>");
				    			}
				    			htmlContext.append("</tr>\r\n");	    
						   }
					   }
					   /**没有指标*/
					   
					   else
					   {
						  
						  if(ifHasChildMap.get(itemid)==null)
						  {
			    		     for(int f=0;f<this.lay-layer+1;f++)
	    	    			{
	    		    			htmlContext.append("<td align=\"left\" class='RecordRow'>&nbsp;&nbsp;</td>");
		     	    		}
					    	/**兼容目标管理，个性项目（没有指标的项目），可以直接给项目录分*/
					    	htmlContext.append("<td align=\"right\" class='RecordRow' width=\"100\">");
					    	if("1".equals(this.isVisible))
		    		    	{
		    		    		htmlContext.append("<input onkeydown=\"checkKeyCode();\" type=\"text\" class=\"Input_self common_border_color\" name=\"i_score\" id=\"si_"+(String)a_bean.get("item_id")+"\" value=\""+(String)a_bean.get("score")+"\" maxlength='10'/>");
		    		    		r_item.append(","+(String)a_bean.get("item_id"));
		    		    	}
		    		    	else {
                                htmlContext.append((String)a_bean.get("score"));
                            }
		    		    	htmlContext.append("</td>");
		    	    		if("1".equals(this.status))
		    		    	{
		    		    		htmlContext.append("<td align=\"right\" class='RecordRow'>");
			    	    		if("1".equals(this.isVisible))
			    	     		{
			    		    		htmlContext.append("<input onBlur=\"checkValue(this);\" onFocus=\"saveBeforeValue(this);\" onkeydown=\"checkKeyCode();\" type=\"text\" class=\"Input_self common_border_color\" name=\"r_score\" id=\"ri_"+(String)a_bean.get("item_id")+"\" value=\""+(String)a_bean.get("rank")+"\" maxlength='10'/>");
			    	     		}
			        			else {
                                    htmlContext.append((String)a_bean.get("rank"));
                                }
			    	    		htmlContext.append("</td>");
		    	    		}
				    		htmlContext.append("</tr>\r\n");
						  }
					   }
			 		}
					
					if(e==0)
					{
						int layer=Integer.parseInt((String)layMap.get(itemid));
						ArrayList fieldlist = (ArrayList)this.itemHaveFieldList.get(item_id);
						if(fieldlist!=null&&fieldlist.size()!=0)
						{
			    			for(int x=0;x<fieldlist.size();x++)
			    			{
			    				if(x!=0) {
                                    htmlContext.append("<tr>\r\n");
                                }
			    				LazyDynaBean xbean = (LazyDynaBean)fieldlist.get(x);
			    				 for(int f=0;f<this.lay-layer;f++)
			    	    		{
			    		    		htmlContext.append("<td align=\"left\" class='RecordRow'>&nbsp;&nbsp;</td>");
				     	    	}
				    			htmlContext.append("<td align=\"left\" class='RecordRow' ");
				    			if("1".equals(this.isVisible)) {
                                    htmlContext.append(" onclick='changeColor(\""+(String)xbean.get("point_id")+"\",\""+2+"\")' id='"+(String)xbean.get("point_id")+"'");
                                }
				    			htmlContext.append(">"+(String)xbean.get("name")+"</td>");
				    			htmlContext.append("<td align=\"right\" width=\"100\" class='RecordRow'>");
				    			if("1".equals(this.isVisible))
				    			{
				    				htmlContext.append("<input onkeydown=\"checkKeyCode();\" onFocus=\"clearValue(this.value,'s_"+(String)xbean.get("point_id")+"')\" type=\"text\" class=\"Input_self common_border_color\" name=\"score\" id=\"s_"+(String)xbean.get("point_id")+"\" value=\""+(String)xbean.get("score")+"\" maxlength='10'/>");
				    				score.append(","+(String)xbean.get("point_id"));
				    			}
				    			else {
                                    htmlContext.append((String)xbean.get("score"));
                                }
				    			htmlContext.append("</td>");
				    			if("1".equals(this.status))
				    			{
				    				htmlContext.append("<td align=\"right\" class='RecordRow'>");
					    			if("1".equals(this.isVisible))
					    			{
					    				htmlContext.append("<input onBlur=\"checkValue(this);\" onFocus=\"saveBeforeValue(this);\" onkeydown=\"checkKeyCode();\" type=\"text\" class=\"Input_self common_border_color\" name=\"score\" id=\"r_"+(String)xbean.get("point_id")+"\" value=\""+(String)xbean.get("rank")+"\" maxlength='10'/>");
					    			}
					    			else {
                                        htmlContext.append((String)xbean.get("rank"));
                                    }
					    			htmlContext.append("</td>");
				    			}
				    			htmlContext.append("</tr>\r\n");
			    			}
						}
						else
						{
							for(int f=0;f<this.lay-layer+1;f++)
		    	    		{
		    		    		htmlContext.append("<td align=\"left\" class='RecordRow'>&nbsp;&nbsp;</td>");
			     	    	}
							/**兼容目标管理，个性项目（没有指标的项目），可以直接给项目录分*/
							htmlContext.append("<td align=\"right\" class='RecordRow' width=\"100\">");
							if("1".equals(this.isVisible))
			    			{
			    				htmlContext.append("<input onkeydown=\"checkKeyCode();\" type=\"text\" class=\"Input_self common_border_color\" name=\"i_score\" onFocus=\"clearValue(this.value,'si_"+(String)a_bean.get("item_id")+"')\" id=\"si_"+(String)a_bean.get("item_id")+"\" value=\""+(String)a_bean.get("score")+"\" maxlength='10'/>");
			    				r_item.append(","+(String)a_bean.get("item_id"));
			    			}
			    			else {
                                htmlContext.append((String)a_bean.get("score"));
                            }
			    			htmlContext.append("</td>");
			    			if("1".equals(this.status))
			    			{
			    				htmlContext.append("<td align=\"right\" class='RecordRow'>");
				    			if("1".equals(this.isVisible))
				    			{
				    				htmlContext.append("<input onBlur=\"checkValue(this);\" onFocus=\"saveBeforeValue(this);\" onkeydown=\"checkKeyCode();\" type=\"text\" class=\"Input_self common_border_color\" name=\"r_score\" id=\"ri_"+(String)a_bean.get("item_id")+"\" value=\""+(String)a_bean.get("rank")+"\" maxlength='10'/>");
				    			}
				    			else {
                                    htmlContext.append((String)a_bean.get("rank"));
                                }
				    			htmlContext.append("</td>");
			    			}
							htmlContext.append("</tr>\r\n");
						}
					}			
				}
		}
		htmlContext.append("</table>");
		this.score_str=score.toString();
		this.item_rank=r_item.toString();
    		if("1".equals(this.isVisible))
    		{
	    		if("1".equals(this.status))
	    		{
	    			htmlContext.append("<tr><td align=\"center\" colspan=\""+(this.lay+3)+"\">");
	    		}
	    		else
	    		{
		    		htmlContext.append("<tr><td align=\"center\" colspan=\""+(this.lay+2)+"\">");
	    		}
	    		
	    		htmlContext.append("<input class=\"mybutton\" type=\"button\" name=\"aa0\" value=\""+ResourceFactory.getProperty("label.kh.new.tjxm")+"\" onclick=\"addItem('0');\"/>");
	    		if(leafItemList!=null&&leafItemList.size()>0)
	    		{
	        		htmlContext.append("&nbsp;<input class=\"mybutton\" type=\"button\" name=\"aa1\" value=\""+ResourceFactory.getProperty("label.kh.new.xjxm")+"\" onclick=\"addItem('1');\"/>");
	    	    	htmlContext.append("&nbsp;<input class=\"mybutton\" type=\"button\" name=\"aa2\" value=\""+ResourceFactory.getProperty("label.kh.crxm")+"\" onclick=\"addItem('2');\"/>");
	    	    	//if(this.status.equals("0"))
	    	    	if(userView!=null)
	    	    	{
	    	    	    //能力素质不需要项目权重设置按钮
	    	    		//if(this.subsys_id!=null && this.subsys_id.trim().length()>0 && this.subsys_id.equalsIgnoreCase("35"))
	    	    		//{}
	    	    		
	    	    		//zxj 20160612  绩效中 项目权重设置不区分标准版专业版
	    	    		if("33".equalsIgnoreCase(this.subsys_id)) {
                            htmlContext.append("&nbsp;<input class=\"mybutton\" type=\"button\" name=\"aa3\" value=\""+ResourceFactory.getProperty("lable.kh.itemrank")+"\" onclick=\"addItem('3');\"/>");
                        }
	    	    	}
	    	    	htmlContext.append("&nbsp;<input class=\"mybutton\" type=\"button\" name=\"ap0\" value=\""+ResourceFactory.getProperty("label.kh.add.field")+"\" onclick=\"addPoint('1');\"/>");
	    	    	htmlContext.append("&nbsp;<input class=\"mybutton\" type=\"button\" name=\"ap1\" value=\""+ResourceFactory.getProperty("label.kh.crzb")+"\" onclick=\"addPoint('2');\"/>");
	    	    	htmlContext.append("&nbsp;<input class=\"mybutton\" type=\"button\" name=\"ad2\" value=\""+ResourceFactory.getProperty("label.kh.del")+"\" onclick=\"del();\"/>");	    		
	    	    	htmlContext.append("&nbsp;<input class=\"mybutton\" type=\"button\" name=\"bb\" value=\""+ResourceFactory.getProperty("button.save")+"\" onclick=\"save('"+this.score_str+"','"+this.status+"','"+this.item_rank+"');\"/>");
	    		}
	    		if(userView!=null&&userView.getBosflag()!=null&&("hl".equalsIgnoreCase(userView.getBosflag())|| "hcm".equalsIgnoreCase(userView.getBosflag()))&&!"2".equals(this.isVisible)&& "dxt".equalsIgnoreCase(this.returnflag)) {
                    htmlContext.append("&nbsp;<input type='button' value='"+ResourceFactory.getProperty("button.return")+"' class='mybutton' onclick='returnFlowPhoto();'/> ");
                }
	    	    htmlContext.append("</td></tr>");
	    		
    		}
    		else
    		{
    			if("1".equals(this.status))
	    		{
	    			htmlContext.append("<tr><td align=\"left\" colspan=\""+(this.lay+3)+"\">");
	    		}
	    		else
	    		{
		    		htmlContext.append("<tr><td align=\"left\" colspan=\""+(this.lay+2)+"\">");
	    		}
    			if(userView!=null&&userView.getBosflag()!=null&& "hl".equalsIgnoreCase(userView.getBosflag())&&!"2".equals(this.isVisible)&& "dxt".equalsIgnoreCase(this.returnflag)) {
                    htmlContext.append("&nbsp;<input type='button' value='"+ResourceFactory.getProperty("button.return")+"' class='mybutton' onclick='returnFlowPhoto();'/> ");
                } else {
                    htmlContext.append("");
                }
    			htmlContext.append("</td></tr>");
    		}
		
		int width=(this.lay)*this.td_width;
		int height=rowNum*this.td_height;
		StringBuffer titleHtml=new StringBuffer("<table  style='background-color:#FFF;' class='ListTable_self' width='100%' >");
		StringBuffer html=new StringBuffer(titleHtml.toString());
		extendtHead.append("</tr>\r\n");
		html.append(extendtHead.toString());
		html.append(htmlContext.toString());
		html.append("</table>");
		return html.toString();
	}
	public HashMap getItemToPointMap(String templateID)
	{
		HashMap map = new HashMap();
		try
		{
			StringBuffer sql = new StringBuffer();
			sql.append("select ptp.*,pp.pointname from per_template_point ptp,per_point pp where  ptp.item_id in (");
			sql.append("select item_id from per_template_item where UPPER(template_id)='");
			sql.append(templateID.toUpperCase()+"') and ptp.point_id=pp.point_id order by ptp.seq");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql.toString());
			LazyDynaBean bean = null;
			while(rs.next())
			{
				bean = new LazyDynaBean();
			
				bean.set("point_id",rs.getString("point_id"));
				bean.set("score",rs.getString("score")==null?"0":this.myformat1.format(rs.getDouble("score")));
				bean.set("rank",rs.getString("rank")==null?"0":this.myformat1.format(rs.getDouble("rank")));
				bean.set("item_id",rs.getString("item_id"));
				bean.set("pointname", rs.getString("pointname"));
				String item_id = rs.getString("item_id");
				if(map.get(item_id)!=null)
				{
					ArrayList list = (ArrayList)map.get(item_id);
					list.add(bean);
					map.put(item_id, list);	
				}
				else
				{
					ArrayList list = new ArrayList();
					list.add(bean);
					map.put(item_id,list);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}

	
	public HashMap getItemHasFieldCount(String templateID)
	{
		HashMap itemsCountMap=new HashMap();
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rowSet = null;
			String sql="select pp.item_id,count(pp.item_id) as count  from  per_template_item pi,per_template_point pp where pi.item_id=pp.item_id and UPPER(pi.template_id)='"+templateID.toUpperCase()+"' group by pp.item_id ";
			rowSet=dao.search(sql);
			
			while(rowSet.next())
			{
				itemsCountMap.put(rowSet.getString("item_id"),rowSet.getString("count")/*+"/"+rowSet.getString("score")*/);	
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return itemsCountMap;
	}
	public HashMap getItemHasFieldList()
	{
	
		HashMap map = new HashMap();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try
		{
			for(int i=0;i<this.templateItemList.size();i++)
			{
				LazyDynaBean bean = (LazyDynaBean)this.templateItemList.get(i);
				String itemid=(String)bean.get("item_id");
				/*rowSet=dao.search("select po.point_id,po.pointname,po.pointkind,pi.item_id,po.visible,
				 * po.fielditem,po.status,pp.score from per_template_item pi,per_template_point pp,per_point po "
						+" where pi.item_id=pp.item_id and pp.point_id=po.point_id  and 
						template_id='"+templateID+"'  order by pp.seq");	  //pi.seq,
*/			
				StringBuffer sql = new StringBuffer();
				sql.append("select ptp.point_id,ptp.score,ptp.seq,ptp.rank,po.pointname,po.description from per_template_point ptp,per_point po where ptp.point_id=po.point_id and item_id="+itemid+" order by ptp.seq");
				rs = dao.search(sql.toString());
				ArrayList list  =  new ArrayList();
				while(rs.next())
				{
					LazyDynaBean a_bean = new LazyDynaBean();
					a_bean.set("itemid",itemid);
					a_bean.set("point_id",rs.getString("point_id"));
					a_bean.set("score",rs.getString("score")==null?"0":this.myformat1.format(rs.getDouble("score")));
					a_bean.set("seq",rs.getString("seq"));
					a_bean.set("name",rs.getString("pointname"));
					a_bean.set("rank",rs.getString("rank")==null?"0":this.myformat1.format(rs.getDouble("rank")));
					a_bean.set("description",Sql_switcher.readMemo(rs,"description"));
					list.add(a_bean);
				}
				map.put(itemid,list);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 取得 模板项目记录
	 * @return
	 */
	public ArrayList getTemplateItemList(String templateID)
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select * from  per_template_item where template_id='"+templateID+"'  order by seq");
		    LazyDynaBean abean=null;
			while(rowSet.next())
		    {
				abean=new LazyDynaBean();
		    	abean.set("item_id",rowSet.getString("item_id"));
		    	abean.set("parent_id",rowSet.getString("parent_id")!=null?rowSet.getString("parent_id"):"");
		    	abean.set("child_id",rowSet.getString("child_id")!=null?rowSet.getString("child_id"):"");
		    	abean.set("template_id",rowSet.getString("template_id"));
		    	abean.set("itemdesc",PubFunc.toHtml(rowSet.getString("itemdesc")));
		    	abean.set("seq",rowSet.getString("seq"));
		    	abean.set("kind",rowSet.getString("kind")!=null?rowSet.getString("kind"):"");
		    	abean.set("score",rowSet.getString("score")==null?"0":this.myformat1.format(rowSet.getDouble("score")));
		    	abean.set("rank",rowSet.getString("rank")==null?"0":this.myformat1.format(rowSet.getDouble("rank")));
		    	abean.set("rank_type",rowSet.getString("rank_type")!=null?rowSet.getString("rank_type"):"");
		    	list.add(abean);
		    	if(rowSet.getString("parent_id")==null|| "".equals(rowSet.getString("parent_id")))
		    	{
		    		this.parentList.add(abean);
		    	}
		    }
			if(rowSet!=null) {
                rowSet.close();
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 叶子项目列表
	 *
	 */
	public void get_LeafItemList()
	{
		LazyDynaBean abean=null;
		for(int i=0;i<this.templateItemList.size();i++)
		{
			abean=(LazyDynaBean)this.templateItemList.get(i);
			String parent_id=(String)abean.get("parent_id");
			if(parent_id.length()==0)
			{
				setLeafItemFunc(abean);
			}
		}
	}
	
   //	递归查找叶子项目
	public void setLeafItemFunc(LazyDynaBean abean)
	{
		String item_id=(String)abean.get("item_id");
		String child_id=(String)abean.get("child_id");
		if(child_id.length()==0||isLeaf(item_id,child_id))
		{
			this.leafItemList.add(abean);
				return;
		}
		LazyDynaBean a_bean=null;
		for(int j=0;j<this.templateItemList.size();j++)
		{
				a_bean=(LazyDynaBean)this.templateItemList.get(j);
				String parent_id=(String)a_bean.get("parent_id");
				if(parent_id.equals(item_id)) {
                    setLeafItemFunc(a_bean);
                }
		}
	}
    public boolean isLeaf(String item_id,String child_id)
    {
    	boolean flag=true;
    	RowSet rs = null;
    	try
    	{
    		ContentDAO dao = new ContentDAO(this.conn);
    		rs=dao.search("select * from per_template_item where parent_id="+item_id+" or item_id="+child_id);
    		while(rs.next())
    		{
    			flag=false;
    			break;
    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	finally{
    		if(rs!=null)
    		{
    			try
    			{
    				rs.close();
    			}catch(Exception e)
    			{
    				e.printStackTrace();
    			}
    		}
    	}
    	return flag;
    }
	/**
	 * 取得项目拥有的叶子节点数
	 * @return
	 */
	public HashMap getItemPointNum()
	{
		HashMap map=new HashMap();
		LazyDynaBean a_bean=null;
		LazyDynaBean aa_bean=null;
		for(int i=0;i<templateItemList.size();i++)
		{
			a_bean=(LazyDynaBean)this.templateItemList.get(i);
			ArrayList list=new ArrayList();
			getLeafItemList(a_bean,list);
			int n=0;
			for(int j=0;j<list.size();j++)
			{
				aa_bean=(LazyDynaBean)list.get(j);
				String item_id=(String)aa_bean.get("item_id");
				if(itemToPointMap.get(item_id)!=null) {
                    n+=((ArrayList)itemToPointMap.get(item_id)).size();
                } else {
                    n+=1;
                }
			}
			map.put((String)a_bean.get("item_id"),new Integer(n));
		}
		return map;
	}
	public void getLeafItemList(LazyDynaBean abean,ArrayList list)
	{
		String item_id=(String)abean.get("item_id");
		String child_id=(String)abean.get("child_id");
		
		if(child_id.length()==0)
		{
			list.add(abean);
				return;
		}
		LazyDynaBean a_bean=null;
		for(int j=0;j<this.templateItemList.size();j++)
		{
				a_bean=(LazyDynaBean)this.templateItemList.get(j);
				String parent_id=(String)a_bean.get("parent_id");
				if(parent_id.equals(item_id)) {
                    getLeafItemList(a_bean,list);
                }
		}
		
	}
	/**
	 * 叶子项目对应的继承关系
	 * @return
	 */
	public  HashMap getLeafItemLinkMap()
	{
		HashMap map=new HashMap();
		try
		{
			LazyDynaBean abean=null;
			for(int i=0;i<this.leafItemList.size();i++)
			{
				abean=(LazyDynaBean)this.leafItemList.get(i);
				String item_id=(String)abean.get("item_id");
				String parent_id=(String)abean.get("parent_id");
				ArrayList linkList=new ArrayList();
				getParentItem(linkList,abean);
				if(linkList.size()>lay) {
                    lay=linkList.size();
                }
				map.put(item_id,linkList);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
//	寻找继承关系
	public void getParentItem(ArrayList list,LazyDynaBean abean)
	{
		String item_id=(String)abean.get("item_id");
		String parent_id=(String)abean.get("parent_id");
		/**顶级节点*/
		if(parent_id.length()==0)
		{
			list.add(abean);
			return;
		}
		LazyDynaBean a_bean=null;
		for(int i=0;i<templateItemList.size();i++)
		{
			a_bean=(LazyDynaBean)this.templateItemList.get(i);
			String itemid=(String)a_bean.get("item_id");
			String parentid=(String)a_bean.get("parent_id");
			if(itemid.equals(parent_id))
			{
				list.add(abean);
				getParentItem(list,a_bean);
			}			
		}				
	}
	
	/**##############################试验解决不能画的形式#############################################*/
	
	/**
	 * 画单元格
	 * @param context 
	 * @param rowspan
	 * @param align
	 * @param width
	 * @param itemid
	 * @param type
	 * @return
	 */
	private String writeTd(String context,int rowspan,String align,int width,String itemid,int type)
	{
		StringBuffer td=new StringBuffer("");
		td.append("\r\n<td class='RecordRow' valign='middle' align='"+align+"'");
		if("1".equals(this.isVisible))
		{
	    	td.append(" onclick='changeColor(\""+itemid+"\",\""+type+"\");' id='"+itemid+"'" );
	    	td.append(" ondblclick='gaibian(\""+itemid+"\",\""+type+"\");'");
		}
		if(rowspan!=0) {
            td.append(" rowspan='"+(rowspan)+"' ");
        } else {
            td.append(" height='"+td_height+"' ");
        }
		td.append("  width='"+width+"'");
		td.append(" >");
		td.append(context);
		td.append("</td>");
		return td.toString();
	}
	private String writeTd2(String context,int rowspan,String align,int width,String itemid,int type)
	{
		StringBuffer td=new StringBuffer("");
		td.append("\r\n<td class='RecordRow' valign='middle'  style='border-left:0px;'  align='"+align+"'");
		
		if(rowspan!=0) {
            td.append(" rowspan='"+(rowspan)+"' ");
        } else {
            td.append(" height='"+td_height+"' ");
        }
		td.append("  width='"+width+"'");
		td.append(" >");
		td.append(context);
		td.append("</td>");
		return td.toString();
	}
   /**
    * 取得模板权重分值标识
    * @param templateID
    * @return
    */
	public String getTemplateStatus(String templateID)
	{
		String status="0";
		try{
			StringBuffer buf = new StringBuffer();
			buf.append("select status from per_template where UPPER(template_id)='"+templateID.toUpperCase()+"'");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs= dao.search(buf.toString());
			while(rs.next())
			{
				status=rs.getString("status")==null?"0":rs.getString("status");
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return status;
	}
	/**
	 * 判断是否已经有项目
	 * @param templateid
	 * @return
	 */
	public boolean isHaveItem(String templateid)
	{
		boolean flag=false;
		try
		{
			String sql = " select * from per_template_item where UPPER(template_id)='"+templateid.toUpperCase()+"'";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql);
			while(rs.next())
			{
				flag=true;
				break;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
	/**
	 * 取父亲项目的id
	 * @param itemid
	 * @return
	 */
	public String getParentId(String itemid)
	{
		String  parentid="";
		try
		{
			String sql ="select parent_id from per_template_item where item_id="+itemid;
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql);
			while(rs.next())
			{
				parentid=rs.getString("parent_id");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return parentid;
	}
	/**
	 * 插入项目时，找插入项目的顺序号
	 * @param itemid
	 * @return
	 */
	public int getInsertSeq(String itemid)
	{
		int seq = 0;
		try
		{
			String sql = " select seq from per_template_item where item_id="+itemid;
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs =dao.search(sql);
			while(rs.next())
			{
				seq=rs.getInt("seq");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return seq;
	}
	/**
	 * 进行插入项目后，将插入点后面的项目序号依次加一
	 * @param seq
	 */
	public void configSeq(String seq,String itemid,String tableName)
	{
		try
		{
			StringBuffer buf = new StringBuffer();
			buf.append("update "+tableName+" set seq=seq+1 where seq>=");
			buf.append(seq+" and item_id<>"+itemid);
			ContentDAO dao = new ContentDAO(this.conn);
			dao.update(buf.toString());
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 根据指标id得到项目id
	 * @param pointid
	 * @param templateid
	 * @return
	 */
	public String getItemidByPointid(String pointid,String templateid)
	{
		String itemid="";
		try
		{
			StringBuffer sql = new StringBuffer();
			sql.append(" select a.item_id from per_template_point a,per_template_item b ");
			sql.append(" where a.item_id=b.item_id and UPPER(a.point_id)='");
			sql.append(pointid.toUpperCase()+"' and UPPER(b.template_id)='");
			sql.append(templateid.toUpperCase()+"'");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql.toString());
			while(rs.next())
			{
				itemid=rs.getString("item_id");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return itemid;
	}
	/**
	 * 删除项目,要递归删除
	 * @param itemid
	 * @param templateid
	 */
	public void deleteItem(String itemid,String templateid)
	{
		RowSet rs =null;
		try{
			StringBuffer buf = new StringBuffer();
			ContentDAO dao = new ContentDAO(this.conn);
			buf.append("'"+itemid.toUpperCase()+"'");
			/**取得所有孩子id*/
			this.getAllChildItemid(itemid, buf, dao);
			String parentid="";
			rs=dao.search("select item_id,parent_id from per_template_item where UPPER(item_id)='"+itemid.toUpperCase()+"'");
			while(rs.next())
			{
				 parentid=rs.getString("parent_id");
				
			}
			StringBuffer sql_buf = new StringBuffer();
			sql_buf.append(" delete from per_template_point where UPPER(item_id) in ("+buf.toString()+")");
			dao.delete(sql_buf.toString(), new ArrayList());
			sql_buf.setLength(0);
			sql_buf.append("delete from per_template_item where ");
			sql_buf.append(" UPPER(item_id) in("+buf.toString()+")");
			dao.delete(sql_buf.toString(), new ArrayList());
			/**如果删除项目的父项目没有孩子节点的话，将其孩子节点值更新为null*/
			updateChild_id(parentid);
			if(parentid!=null&&parentid.length()>0)
			{
	    		/**检查父项目是否还有子项目或指标，如果没有，将其置为个性项目*/
	    		boolean flag=true;
		    	sql_buf.setLength(0);
		    	sql_buf.append("select * from per_template_item where parent_id='"+parentid+"'");
		    	rs=null;
		    	rs=dao.search(sql_buf.toString());
		    	while(rs.next())
		    	{
		    		flag=false;
		    		break;
		    	}
		    	if(flag)
		    	{
		    		sql_buf.setLength(0);
		    		rs=null;
		    		sql_buf.append("select * from per_template_point where UPPER(item_id)="+parentid);
		    		rs=dao.search(sql_buf.toString());
		    		while(rs.next())
		    		{
		    			flag=false;
		    			break;
		    		}
		    	}
		    	if(flag)
		    	{
		    		RecordVo vo = new RecordVo("per_template_item");
					vo.setString("item_id",parentid);
					vo=dao.findByPrimaryKey(vo);
					vo.setInt("kind", 2);
					dao.updateValueObject(vo);
		    	}
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public void updateChild_id(String itemid)
	{
		try
		{
			if(itemid==null|| "".equals(itemid)) {
                return;
            }
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search("select * from per_template_item where UPPER(parent_id)='"+itemid.toUpperCase()+"'");
		    boolean flag=false;
			while(rs.next())
			{
				flag=true;
				break;
			}
			if(!flag)
			{
				RecordVo vo = new RecordVo("per_template_item");
				vo.setString("item_id",itemid);
				vo=dao.findByPrimaryKey(vo);
				vo.setString("child_id", null);
				dao.updateValueObject(vo);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public void getAllChildItemid(String itemid,StringBuffer buf,ContentDAO dao)
	{
		try
		{
			StringBuffer sql = new StringBuffer("");
			sql.append("select * from per_template_item where UPPER(parent_id)='"+itemid.toUpperCase()+"'");
			RowSet rs=null;
			rs=dao.search(sql.toString());
			while(rs.next())
			{
				String item_id=rs.getString("item_id");
				buf.append(",'"+item_id.toUpperCase()+"'");
				getAllChildItemid(item_id,buf,dao);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 删除指标
	 * @param pointid
	 * @param templateid
	 */
	public void deletePoint(String pointid,String templateid)
	{
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer buf = new StringBuffer();
			buf.append(" delete from per_template_point where UPPER(point_id)='");
			buf.append(pointid.toUpperCase()+"' and item_id in (");
			buf.append(" select item_id from per_template_item where UPPER(template_id)='");
			buf.append(templateid.toUpperCase()+"')");
			dao.delete(buf.toString(), new ArrayList());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 删除指标后，判断该项目是否还有指标，以便设置项目的为共性项目或者个性项目
	 * @param pointid
	 * @param templateid
	 * @return
	 */
	public boolean isHavePoint(String itemid)
	{
		boolean flag=false;
		try
		{
			StringBuffer buf = new StringBuffer();
			buf.append("select * from per_template_point where item_id=");
		    buf.append(itemid);
		    ContentDAO dao = new ContentDAO(this.conn);
		    RowSet rs = dao.search(buf.toString());
		    while(rs.next()){
		    	flag=true;
		    	break;
		    }
		    if(!flag)
		    {
		    	/**如果存在下级项目，不算个性项目*/
		    	rs=dao.search("select * from per_template_item where parent_id="+itemid);
		    	while(rs.next())
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
	public String getRank(String templateID)
	{
	   String rank="0";
	   try
	   {
		   String sql = "select status from per_template where UPPER(template_id)='"+templateID.toUpperCase()+"'";
		   ContentDAO dao = new ContentDAO(this.conn);
		   RowSet rs = dao.search(sql);
		   while(rs.next())
		   {
			   if("0".equals(rs.getString("status")))
			   {
				   rank="1";
			   }
		   }
		   
	   }
	   catch(Exception e)
	   {
		   e.printStackTrace();
	   }
	   return rank;
	}
	/**
	 * 取得插入指标的seq值
	 * @param itemid
	 * @param pointid
	 * @return
	 */
	public int getInsertPointSeq(String itemid,String pointid)
	{
		int seq = 0;
		try
		{
			String sql = "select seq from per_template_point where UPPER(point_id)='"+pointid.toUpperCase()+"' and item_id="+itemid;
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql);
			while(rs.next())
			{
				seq=rs.getInt("seq");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return seq;
	}
	public String getCurrPointItem(String templateid,String pointid)
	{
		String itemid="";
		RowSet rs = null;
		try
		{
			StringBuffer buf = new StringBuffer();
			buf.append("select item_id from per_template_point where UPPER(point_id)='"+pointid.toUpperCase()+"'");
			buf.append(" and item_id in (select item_id from per_template_item where UPPER(template_id)='"+templateid.toUpperCase()+"')");
			ContentDAO dao  = new ContentDAO(this.conn);
			rs=dao.search(buf.toString());
			while(rs.next())
			{
				itemid=rs.getString(1);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			if(rs!=null)
			{
				try
				{
					rs.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		return itemid;
	}
	/***
	 * 增加或者插入指标
	 * @param itemid
	 * @param templateID
	 * @param pointids
	 * @param type =1是增加指标=2是插入指标
	 * @param currentPoint
	 */
	public void addPoint(String itemid,String templateID,String pointids,int type,String currentPoint)
	{
		try
		{
			StringBuffer sql = new StringBuffer();
			int seq=0;
			if(type==1) {
                seq=this.getMaxId("per_template_point", "seq");
            }
			if(type==2)
			{
				itemid=this.getCurrPointItem(templateID, currentPoint);
				seq=this.getInsertPointSeq(itemid, currentPoint);
			}
			int currentSeq=seq;
			String rank=this.getRank(templateID);
			String[] arr=pointids.split(",");
			ContentDAO dao = new ContentDAO(this.conn);
			ArrayList list = new ArrayList();
			int incream = 0;
			RecordVo vo=new RecordVo("per_template");
			vo.setString("template_id",templateID);
			vo=dao.findByPrimaryKey(vo);
			String topscore="0.0";
			String status="0";
			if(vo!=null)
			{
				topscore=vo.getString("topscore");
				status=vo.getString("status");
			}
			//status=1权重模板
			if("0".equals(status)) {
                topscore="0.0";
            }
			StringBuffer buf = new StringBuffer();
			for(int i=0;i<arr.length;i++)
			{
				if(arr[i]==null|| "".equals(arr[i])) {
                    continue;
                }
				/**新建指标分值默认为空*/
				/*sql.append(" insert into per_template_point(point_id,item_id,score,seq,rank) values (");
				sql.append("'"+arr[i]+"',"+itemid+","+topscore+","+seq+","+rank+")");*/
				sql.append(" insert into per_template_point(point_id,item_id,score,seq,rank) values (");
				sql.append("'"+arr[i]+"',"+itemid+","+topscore+","+seq+","+rank+")");
				buf.append(" and UPPER(point_id)<>'"+arr[i].toUpperCase()+"'");
				list.add(sql.toString());
				seq++;
				incream++;
				sql.setLength(0);
			}
			dao.batchUpdate(list);
			/**插入指标要重新设置seq的值*/
			if(type==2)
			{
				String seqsql = " update per_template_point set seq=seq+"+incream+" where seq>="+currentSeq+" and item_id<>"+itemid+" and ("+buf.toString().substring(4)+")";
				dao.update(seqsql);
				String seqsql2= " update per_template_point set seq=seq+"+incream+" where seq>="+currentSeq+" and item_id="+itemid+" and ("+buf.toString().substring(4)+")";
				dao.update(seqsql2);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 模板校验
	 * @param templateID
	 * @return
	 */
	public String ValidationTemplate(String templateID)
	{
		StringBuffer buf = new StringBuffer();
		try
		{
			StringBuffer sql = new StringBuffer();
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer buf_a = new StringBuffer("");
			StringBuffer buf_b = new StringBuffer("");
			StringBuffer buf_c = new StringBuffer("");
			RowSet rs = null;
			/**status,=0分值，=1权重*/
			sql.append("select topscore,status from per_template where UPPER(template_id)='"+templateID.toUpperCase()+"'");
			rs=dao.search(sql.toString());
			String topscore="0";
			String status="0";
			while(rs.next())
			{
				 topscore=rs.getString("topscore");
				 status=rs.getString("status");
			}
			BigDecimal topB=new BigDecimal(topscore);
			/**指标的分数*/
			StringBuffer t_buf= new StringBuffer("");
			t_buf.append(" select score,rank from per_template_point where item_id in (");
			t_buf.append("select item_id from per_template_item where UPPER(template_id)='"+templateID.toUpperCase()+"')");
	        rs=dao.search(t_buf.toString());
	        BigDecimal total=new BigDecimal("0");
	        while(rs.next())
	        {
	        	String score=rs.getString("score");
	        	String rank=rs.getString("rank")==null?"0":rs.getString("rank");
	        	if("1".equals(status))
	        	{
	        		BigDecimal b2=new BigDecimal(score);
	        		BigDecimal b3=new BigDecimal(rank);
	        		BigDecimal b4=b2.multiply(b3);
	        		total=total.add(b4);
	        	}
	        	else
	        	{
	        		BigDecimal b1=new BigDecimal(score);
	        		total=total.add(b1);
	        	}
	        	
	        }
	        t_buf.setLength(0);
	        /**个性项目的分数*/
	        t_buf.append(" select score,rank from per_template_item where UPPER(template_id)='"+templateID.toUpperCase()+"' ");
			t_buf.append(" and item_id not in ");
			t_buf.append(" (select item_id from per_template_point where item_id in (");
			t_buf.append(" select item_id from per_template_item where UPPER(template_id)='"+templateID.toUpperCase()+"')) and kind=2");
		    rs=dao.search(t_buf.toString());
		    while(rs.next())
		    {
		       String score=rs.getString("score")==null?"0":rs.getString("score");
		       String rank=rs.getString("rank")==null?"0":rs.getString("rank");
		       if("1".equals(status))
		        {
		        	BigDecimal b2=new BigDecimal(score);
		        	BigDecimal b3=new BigDecimal(rank);
		        	BigDecimal b4=b2.multiply(b3);
		        	total=total.add(b4);
		        }
		        else
		        {
		        	BigDecimal b1=new BigDecimal(score);
		        	total=total.add(b1);
		        }   	
		     }
			if(topB.compareTo(total)!=0)
		    {
			    buf_a.append(ResourceFactory.getProperty("label.kh.template.score")+":\r\n");
			    buf_a.append(ResourceFactory.getProperty("label.kh.template.topscore")+":"+topB.floatValue()+"\r\n");
			    buf_a.append(ResourceFactory.getProperty("label.kh.template.currentscore")+":"+total.floatValue()+"\r\n");
				buf_a.append("\r\n\r\n");
	    	}
	    	
			HashMap map = new HashMap();
			LazyDynaBean bean = null;
			ArrayList list = new ArrayList();
			sql.setLength(0);
			/**kind=2的是个性项目，没有指标*/
			sql.append("select item_id,itemdesc from per_template_item where UPPER(template_id)='");
			sql.append(templateID.toUpperCase()+"' and child_id is null and kind=2");
		
		    rs = dao.search(sql.toString());
			while(rs.next())
			{
				bean = new LazyDynaBean();
				bean.set("item_id",rs.getString("item_id"));
				bean.set("itemdesc",rs.getString("itemdesc"));
				list.add(bean);
			}
			sql.setLength(0);
			sql.append("select item_id from per_template_point where item_id in (");
			sql.append("select item_id from per_template_item where UPPER(template_id)='"+templateID+"')");
			rs = dao.search(sql.toString());
			while(rs.next())
			{
				map.put(rs.getString("item_id"),"1");
			}
			for(int i=0;i<list.size();i++)
			{
				LazyDynaBean a_bean = (LazyDynaBean)list.get(i);
				String item=(String)a_bean.get("item_id");
				if(map.get(item)==null) {
                    buf_b.append((String)a_bean.get("itemdesc")+"\r\n");
                }
			}
			if(buf_b!=null&&buf_b.toString().trim().length()>0)
			{
				buf_c.append(ResourceFactory.getProperty("label.kh.nopointitem")+":\r\n");
				buf_c.append(buf_b.toString());
			}
			buf.append(buf_a.toString());
			buf.append(buf_c.toString());
			if(buf==null|| "".equals(buf.toString()))
			{
				buf.append(ResourceFactory.getProperty("label.kh.template.validation"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return buf.toString();
	}
	
	/**
	 * 保存指标分数和权重
	 * @param str
	 * @param templateID
	 * @param type=1保存指标=2保存权重
	 * @return
	 */
	public String saveScoreRand(String s_str,String r_str,String i_s_str,String i_r_str,String status,String templateID)
	{
		StringBuffer buf = new StringBuffer("");
		try
		{
			StringBuffer point_buf=new StringBuffer("");
			StringBuffer rank_buf=new StringBuffer("");
			StringBuffer item_buf=new StringBuffer("");
			StringBuffer item_s_buf=new StringBuffer("");
			BigDecimal ranktotal=new BigDecimal("0");
			BigDecimal scoretotal=new BigDecimal("0");
			ArrayList psList=new ArrayList();
			ArrayList prList = new ArrayList();
			ArrayList itemList = new ArrayList();
			ArrayList itemsList = new ArrayList();
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			/**status,=0分值，=1权重*/
			String[] r_str_arr=null;
			if("1".equals(status)&&r_str!=null&&r_str.trim().length()>0) {
                r_str_arr=r_str.split(",");
            }
			if(s_str!=null&&s_str.trim().length()>0)
			{
				String[] s_str_arr=s_str.split(",");
				
				for(int i=0;i<s_str_arr.length;i++)
				{
					if(s_str_arr[i]==null||s_str_arr[i].trim().length()<=0) {
                        continue;
                    }
					String[] s_t=s_str_arr[i].split("/");
					/**指标分值*/
					String pointid=s_t[0];
					String p_score="0.00";
					if(s_t.length==2) {
                        p_score=s_t[1]==null|| "".equals(s_t[1])?"0.00":s_t[1];
                    }
					if("1".equals(status))
					{
					     if(r_str_arr!=null)
					     {
					    	 String[] r_t=r_str_arr[i].split("/");
					    	 String rank=r_t[1]==null|| "".equals(r_t[1])?"0.00":r_t[1];
					    	 BigDecimal r_b=new BigDecimal(rank);
					    	 ranktotal=ranktotal.add(r_b);
					    	 rank_buf.append("update per_template_point set rank="+rank);
					    	 rank_buf.append(" where UPPER(point_id)='"+pointid.toUpperCase()+"' and item_id in (select item_id from per_template_item where ");
					    	 rank_buf.append("UPPER(template_id)='"+templateID.toUpperCase()+"')");
					    	 prList.add(rank_buf.toString());
							 rank_buf.setLength(0);
							 /**用权重计算分值*/
							 BigDecimal t=new BigDecimal(p_score);
							 scoretotal=scoretotal.add(t.multiply(r_b));
					     }
					}
					else
					{
						/**直接计算分值*/
						scoretotal=scoretotal.add(new BigDecimal(p_score));
					}
					/**权重模板，不可修改分值*/
					if(!"1".equals(status))
					{
				    	point_buf.append("update per_template_point set score="+p_score);
				    	point_buf.append(" where UPPER(point_id)='"+pointid.toUpperCase()+"' and item_id in (select item_id from per_template_item where ");
					    point_buf.append("UPPER(template_id)='"+templateID.toUpperCase()+"')");
					    psList.add(point_buf.toString());
				     	point_buf.setLength(0);
					}
				}
			}
			/**个性项目的计算。加上权重和分值*/		
			String[] r_str_arr_i=null;
			if("1".equals(status)&&i_r_str!=null&&i_r_str.trim().length()>0) {
                r_str_arr_i=i_r_str.split(",");
            }
			if(i_s_str!=null&&i_s_str.trim().length()>0)
			{
				String[] s_str_arr=i_s_str.split(",");
				
				for(int i=0;i<s_str_arr.length;i++)
				{
					if(s_str_arr[i]==null||s_str_arr[i].trim().length()<=0) {
                        continue;
                    }
					String[] s_t=s_str_arr[i].split("/");
					/**项目分值*/
					String pointid=s_t[0];
					String p_score="0";
					if(s_t.length>1) {
                        p_score=s_t[1]==null|| "".equals(s_t[1])?"0.00":s_t[1];
                    }
					if("1".equals(status))
					{
					     if(r_str_arr_i!=null)
					     {
					    	 String[] r_t=r_str_arr_i[i].split("/");
					    	 String rank="0";
					    	 if(r_t.length>1) {
                                 rank=r_t[1]==null|| "".equals(r_t[1])?"0.00":r_t[1];
                             }
					    	 BigDecimal r_b=new BigDecimal(rank);
					    	 ranktotal=ranktotal.add(r_b);
					    	 item_buf.append("update per_template_item set rank="+rank);
					    	 item_buf.append(" where UPPER(item_id)='"+pointid.toUpperCase()+"' ");
					    	 itemList.add(item_buf.toString());
					    	 item_buf.setLength(0);
							 /**用权重计算分值*/
							 BigDecimal t=new BigDecimal(p_score);
							 scoretotal=scoretotal.add(t.multiply(r_b));
					     }
					}
					else
					{
						/**直接计算分值*/
						scoretotal=scoretotal.add(new BigDecimal(p_score));
					}
					/**权重模板，分值不可改变*/
					if(!"1".equals(status))
					{
			     		item_s_buf.append("update per_template_item set score="+p_score);
			    		item_s_buf.append(" where UPPER(item_id)='"+pointid.toUpperCase()+"'");
			    		itemsList.add(item_s_buf.toString());
			     		item_s_buf.setLength(0);
					}
				}
			}
			/**权重是否为1*//*
			if(status.equals("1"))
			{
		    	BigDecimal one=new BigDecimal("1");
		    	if(one.compareTo(ranktotal)!=0)
		    	{
		    		buf.append(ResourceFactory.getProperty("label.kh.template.ranknotequal")+":\r\n");
    	    		buf.append(ResourceFactory.getProperty("label.kh.template.currentrank")+":"+PubFunc.round(ranktotal==null?"0.00":ranktotal.doubleValue()+"", 2)+"\r\n");
    	    		return buf.toString();
	    		}
			}
			*//**模板总分和现在指标和项目的总分和是否相等**//*
			StringBuffer sql = new StringBuffer();
			String topscore="0.00";
    		sql.append("select topscore from per_template where UPPER(template_id)='"+templateID.toUpperCase()+"'");
		    rs = dao.search(sql.toString());
		    while(rs.next())
		    {
    	    	topscore=rs.getString("topscore");
 		    }
		    if(scoretotal.compareTo(new BigDecimal(topscore))!=0)
		    {
		    	buf.append(ResourceFactory.getProperty("label.kh.template.score")+":\r\n");
    			buf.append(ResourceFactory.getProperty("label.kh.template.topscore")+":"+PubFunc.round(topscore==null?"0.00":topscore, 2)+"\r\n");
    			buf.append(ResourceFactory.getProperty("label.kh.template.currentscore")+":"+PubFunc.round(scoretotal==null?"0.00":scoretotal.doubleValue()+"", 2)+"\r\n");
    			buf.append("\r\n\r\n");
    			return buf.toString();
		    }*/
			/**更新模板当前分值*/
			String sql = "update per_template set currentscore=? where UPPER(template_id)='"+templateID.toUpperCase()+"'";
			ArrayList currentList = new ArrayList();
			currentList.add(scoretotal.toString());
			dao.update(sql,currentList);
		    dao.batchUpdate(psList);
		    dao.batchUpdate(prList);
		    dao.batchUpdate(itemList);
		    dao.batchUpdate(itemsList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return buf.toString();
	}
/**
 * 设置excel的样式
 * @param workbook
 * @param styles
 * @return
 */
	public HSSFCellStyle style(HSSFWorkbook workbook, int styles)
	{
		
		HSSFCellStyle style = workbook.createCellStyle();
		
		switch (styles)
		{
		
		case 0:
		    HSSFFont fonttitle = fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.black.font"), 15);
		    fonttitle.setBold(false);// 加粗
		    style.setFont(fonttitle);
		    style.setVerticalAlignment(VerticalAlignment.CENTER);
		    style.setAlignment(HorizontalAlignment.CENTER);
		    break;
		case 1:
		    style.setFont(fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.m.font"), 10));
		    style.setBorderBottom(BorderStyle.THIN);
		    style.setBorderLeft(BorderStyle.THIN);
		    style.setBorderRight(BorderStyle.THIN);
		    style.setBorderTop(BorderStyle.THIN);
		    style.setVerticalAlignment(VerticalAlignment.CENTER);
		    style.setAlignment(HorizontalAlignment.CENTER);
		    break;
		case 2:
		    style.setFont(fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.m.font"), 10));
		    style.setBorderBottom(BorderStyle.THIN);
		    style.setBorderLeft(BorderStyle.THIN);
		    style.setBorderRight(BorderStyle.THIN);
		    style.setBorderTop(BorderStyle.THIN);
		    style.setVerticalAlignment(VerticalAlignment.TOP);
		    break;
		case 3:
		    style.setFont(fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.m.font"), 12));
		    style.setBorderBottom(BorderStyle.THIN);
		    style.setBorderLeft(BorderStyle.THIN);
		    style.setBorderRight(BorderStyle.THIN);
		    style.setBorderTop(BorderStyle.THIN);
		    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		    style.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
		    break;
		case 4:
		    style.setFont(fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.m.font"), 12));
		    style.setBorderBottom(BorderStyle.THIN);
		    style.setBorderLeft(BorderStyle.THIN);
		    style.setBorderRight(BorderStyle.THIN);
		    style.setBorderTop(BorderStyle.THIN);
		    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		    style.setFillForegroundColor(HSSFColor.LIGHT_CORNFLOWER_BLUE.index);
		    break;
		case 5:
			HSSFFont font15=fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.m.font"), 12);
			font15.setBold(true);;
			style.setFont(font15);
		    //style.setBorderBottom(BorderStyle.THIN);
		    //style.setBorderLeft(BorderStyle.THIN);
		   // style.setBorderRight(BorderStyle.THIN);
		   // style.setBorderTop(BorderStyle.THIN);
		    style.setVerticalAlignment(VerticalAlignment.CENTER);
		    style.setAlignment(HorizontalAlignment.CENTER);
		    //style.setFillPattern(HorizontalAlignment.CENTER);
		   // style.setFillForegroundColor(HSSFColor.LIGHT_CORNFLOWER_BLUE.index);
		    break;
		default:
		    style.setFont(fonts(workbook, ResourceFactory.getProperty("gz.gz_acounting.m.font"), 12));
		    style.setAlignment(HorizontalAlignment.LEFT);
		    style.setBorderBottom(BorderStyle.THIN);
		    style.setBorderLeft(BorderStyle.THIN);
		    style.setBorderRight(BorderStyle.THIN);
		    style.setBorderTop(BorderStyle.THIN);
		    break;
		}
		style.setWrapText(true);
		return style;
	}
	/**
	 * 设置excel的字体
	 * @param workbook
	 * @param fonts
	 * @param size
	 * @return
	 */
	public HSSFFont fonts(HSSFWorkbook workbook, String fonts, int size)
	{
	
		HSSFFont font = workbook.createFont();
		font.setFontHeightInPoints((short) size);
		font.setFontName(fonts);
		return font;
	}
	/**
	 * 设置共性项目和个性项目
	 * @param itemid
	 * @param kind =1是共性=2是个性
	 */
	public void setKind(String itemid,String kind)
	{
		try
		{
			StringBuffer sql = new StringBuffer();
			sql.append("update per_template_item set kind=");
			sql.append(kind);
			sql.append(" where item_id=");
			sql.append(itemid);
			ContentDAO dao = new ContentDAO(this.conn);
			dao.update(sql.toString());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**************************************************************************************
	 * 导出模板的方法
	 *************************************************************************************/
	/**先创建目录文件夹*/
	public static void produceFolder()
	{
		if(!(new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+"khtemplatedata/").isDirectory()))
		{
			new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+"khtemplatedata/").mkdir();
				
		}
	}	
	/**模板分类表id*/
	private String templatesetid="";
	/**模板表id*/
	private String templateid="";
	/**模板项目表id*/
	private String itemid="";
	/**模板要素表id*/
	private String pointid="";
	/**模板要素分类表id*/
	private String pointsetid="";
	/**考核指标要素表id*/
	private String point_id="";
	/**标度表id*/
	private String gradeid="";
	/**标度模板表id*/
	private String gradetemplateid="";	
	/**
	 * @param isPriv 是否加权限
	 * @param isRestrictions 是否加子系统号的限制
	 * @param subsys_id
	 * @param tablename
	 * @param builder
	 * @return
	 */
	public byte[] getFileContent(boolean isPriv,boolean isRestrictions,String subsys_id,String tablename,RowSetToXmlBuilder builder,UserView view)
	{
		String content="";
		try
		{
			RowSet rs = null;
			ContentDAO dao = new ContentDAO(this.conn);
			String sql ="";
			/**模板分类表*/
			if("per_template_set".equalsIgnoreCase(tablename))
			{
				sql = "select * from per_template_set";
				if(isRestrictions)
				{
					sql+=" where subsys_id="+subsys_id;
				}
				if(isPriv)
				{
					String b0110="";
					if(view.getStatus()==0)
					{
						if(view.getManagePrivCode()!=null&&!"".equals(view.getManagePrivCode()))
						{
							b0110=view.getManagePrivCodeValue();
							if(b0110==null) {
                                b0110="";
                            }
						}
						else
						{
							b0110="#";
						}						
					}else
					{
						String a0100=view.getA0100();
						String dbname=view.getDbname();
						KhFieldBo bo = new KhFieldBo(this.conn);
						b0110=bo.getB0110(dbname, a0100);
					}
					if(isRestrictions) {
                        sql+=" and ";
                    } else {
                        sql+=" where ";
                    }
					sql+="(UPPER(b0110)='HJSJ' or b0110 like '"+b0110+"%')";
				}
				rs = dao.search(sql);
				content=builder.outPutXml(rs, tablename, "template_setid", view, IResourceConstant.KH_MODULE,"");
				if(isRestrictions||isPriv)
				{
		    		StringBuffer temp = new StringBuffer("");
		    		rs.beforeFirst();
		    		while(rs.next())
		    		{
		    			//模板分类没有授权，不应该判断权限
		    			/*if(!view.isHaveResource(IResourceConstant.KH_MODULE, rs.getString("template_setid")))
		    				continue;*/
		    			temp.append(",");
			    		temp.append("'"+rs.getString("template_setid")+"'");
		    		}
	    			templatesetid=temp.toString().length()>0?temp.toString().substring(1):"";
				}
			}
			/**模板表*/
			else if("per_template".equalsIgnoreCase(tablename))
			{
				sql="select * from per_template ";
				if(isRestrictions||isPriv) {
                    sql+=" where template_setid in ("+("".equals(templatesetid)?"'-1'":templatesetid)+")";
                }
				rs = dao.search(sql);
				content=builder.outPutXml(rs, tablename, "template_id", view, IResourceConstant.KH_MODULE,"");
				if(isRestrictions||isPriv)
				{
	    			StringBuffer temp = new StringBuffer("");
		    		rs.beforeFirst();
		    		while(rs.next())
	     			{
		    		    if(!view.isHaveResource(IResourceConstant.KH_MODULE, rs.getString("template_id"))) {
                            continue;
                        }
		     			temp.append(",");
		    			temp.append("'"+rs.getString("template_id")+"'");
		     		}
	    			templateid=temp.toString().length()>0?temp.toString().substring(1):"";
				}
				
			}
			/**模板项目表*/
			else if("per_template_item".equalsIgnoreCase(tablename))
			{
				sql=" select * from per_template_item ";
				if(isRestrictions||isPriv) {
                    sql+=" where UPPER(template_id) in("+("".equals(templateid)?"'-1'":templateid.toUpperCase())+")";
                }
				rs = dao.search(sql);
				content=builder.outPutXml(rs, tablename);
				if(isRestrictions||isPriv)
				{
    				int i=0;
	    			StringBuffer temp = new StringBuffer("");
	    			rs.beforeFirst();
	    			while(rs.next())
	    			{
	    				if(i!=0) {
                            temp.append(",");
                        }
	    				temp.append("'"+rs.getString("item_id")+"'");
	    				i++;
	      			}
	    			itemid=temp.toString();
				}
				
			}
			/**模板要素表*/
			else if("per_template_point".equalsIgnoreCase(tablename))
			{
				sql = "select * from per_template_point ";
				if(isRestrictions||isPriv) {
                    sql+=" where item_id in("+("".equals(itemid)?"'-1'":itemid)+")";
                }
				rs = dao.search(sql);
				content=builder.outPutXml(rs, tablename);
				if(isRestrictions||isPriv)
				{
	    			int i=0;
	    			StringBuffer temp = new StringBuffer("");
	    			rs.beforeFirst();
	    			while(rs.next())
	     			{
	    				if(i!=0) {
                            temp.append(",");
                        }
		    			temp.append("'"+rs.getString("point_id")+"'");
	    				i++;
    				}
	    			pointid=temp.toString();
				}
				
			}
			/**考核指标分类表*/
			else if("per_point".equalsIgnoreCase(tablename))
			{
				sql = " select * from per_point" ;
				if(isRestrictions||isPriv) {
                    sql+=" where point_id in("+("".equals(pointid)?"'-1'":pointid)+")";
                }
				rs = dao.search(sql);
				content=builder.outPutXml(rs, tablename);
				if(isRestrictions||isPriv)
				{
    				int i=0;
		    		StringBuffer temp = new StringBuffer("");
	    			StringBuffer temp2=new StringBuffer("");
		     		rs.beforeFirst();
		    		while(rs.next())
		    		{
		    			if(i!=0)
			    		{
		    				temp.append(",");
		    				temp2.append(",");
			    		}
			    		temp.append(rs.getString("pointsetid"));
				    	temp2.append("'"+rs.getString("point_id")+"'");
			    		i++;
		    		}
		    		pointsetid=temp.toString();
		    		point_id=temp2.toString();
				}
			}
			/**考核指标要素表*/
			else if("per_pointset".equalsIgnoreCase(tablename))
			{
				sql = " select * from per_pointset";
				if(isRestrictions||isPriv)
				{
					String ss="";
					if(pointsetid!=null&&!"".equals(pointsetid)) {
                        ss=this.autoRelevancePrentID(pointsetid+",", "per_pointset", "(pointsetid in ("+(pointsetid)+"))", dao, "pointsetid");
                    }
					sql+=" where pointsetid in ("+("".equals(ss)?"-1":ss.substring(0,ss.length()-1))+")";
				}
				rs = dao.search(sql);
				content=builder.outPutXml(rs, tablename);
			}
			/**标度表*/
			else if("per_grade".equalsIgnoreCase(tablename))
			{
				sql=" select * from per_grade ";
						sql+=" where UPPER(point_id) in ("+("".equals(point_id)?"'-1'":point_id.toUpperCase())+")";
				rs = dao.search(sql);
				content=builder.outPutXml(rs, tablename);
				if(isRestrictions||isPriv)
				{
	    			int i=0;
		    		StringBuffer temp = new StringBuffer("");
		    		rs.beforeFirst();
		    		while(rs.next())
		    		{
		    			
		    			if(rs.getString("gradecode")!=null&&temp.indexOf(rs.getString("gradecode"))==-1){
		    				if(i!=0) {
                                temp.append(",");
                            }
		    				temp.append("'"+rs.getString("gradecode")+"'");
		    			}
		    			i++;
		     		}
		     		gradeid=temp.toString();
				}
				
			}
			/**标度模板表*/
			else if("per_grade_template".equalsIgnoreCase(tablename))
			{				
				if(subsys_id!=null && subsys_id.trim().length()>0 && "35".equalsIgnoreCase(subsys_id)) {
                    tablename = "per_grade_competence"; // 能力素质标准标度
                }
				sql=" select * from "+tablename+" ";
				/*if(isRestrictions||isPriv)
					sql+="where UPPER(grade_template_id) in("+(gradeid.equals("")?"'-1'":gradeid.toUpperCase())+")";*/
				rs = dao.search(sql);
				content=builder.outPutXml(rs, tablename);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return content.getBytes();
	}
	public String exportDate(String subsys_id,UserView view)
	{
		String filename=view.getUserName()+"KhTemplate_"+PubFunc.getStrg()+".zip";
		RowSet rs = null;
		FileOutputStream fileOut=null;
		 ZipOutputStream outputStream =null;
		 BufferedInputStream origin = null;
		 FileInputStream fileIn = null;
		try
		{
			StringBuffer buf = new StringBuffer("");
			buf.append("select template_setid from per_template_set where subsys_id="+subsys_id);
			ContentDAO dao  = new ContentDAO(this.conn);
			rs=dao.search(buf.toString());
			boolean hasSet=false;
			boolean hasTemplate=false;
			while(rs.next())
			{
				if(view.isSuper_admin()){
				   hasSet=true;
				   break;
				}else{
					if(view.isHaveResource(IResourceConstant.KH_MODULE, rs.getString("template_setid")))
					{
						hasSet=true;
						break;
					}
				}
			}
			if(!hasSet)
			{
				buf.setLength(0);
				buf.append("select template_id from per_template where template_setid in (select template_setid from per_template_set where subsys_id="+subsys_id+")");
				rs = dao.search(buf.toString());
				while(rs.next())
				{
					if(view.isSuper_admin())
					{
		    			hasTemplate=true;
		    			break;
					}else{
						if(view.isHaveResource(IResourceConstant.KH_MODULE, rs.getString("template_id")))
						{
							hasTemplate=true;
			    			break;
						}
					}
				}
			}
			if(!hasSet&&!hasTemplate)
			{
				return "1";
			}
			produceFolder();
			String outName="";
			
			 outName="per_template_set.xml";
			 writeFileOut(subsys_id, "per_template_set", view, outName);
			 
			 outName="per_template.xml";
			 writeFileOut(subsys_id, "per_template", view, outName);
			 
			 outName="per_template_item.xml";
			 writeFileOut(subsys_id, "per_template_item", view, outName);
			 
			 outName="per_template_point.xml";
			 writeFileOut(subsys_id, "per_template_point", view, outName);
			 
			 outName="per_point.xml";
			 writeFileOut(subsys_id, "per_point", view, outName);
			 
			 outName="per_pointset.xml";
			 writeFileOut(subsys_id, "per_pointset", view, outName);
			 
			 outName="per_grade.xml";
			 writeFileOut(subsys_id, "per_grade", view, outName);
			 
			 outName="per_grade_template.xml";
			 if(subsys_id!=null && subsys_id.trim().length()>0 && "35".equalsIgnoreCase(subsys_id)) {
                 outName = "per_grade_competence.xml"; // 能力素质标准标度
             }
			 writeFileOut(subsys_id, "per_grade_template", view, outName);
			 
			 
			 
			 ArrayList fileNames = new ArrayList(); // 存放文件名,并非含有路径的名字
			 ArrayList files = new ArrayList(); // 存放文件对象
		     fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+filename);
			 outputStream = new ZipOutputStream(fileOut);
			 File rootFile = new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+"khtemplatedata");
			 listFile(rootFile, fileNames, files);
			 
			 byte data[] = new byte[2048];
			 for(int loop = 0; loop < files.size(); loop++) {
			 String a_fileName=(String) fileNames.get(loop);
			 if(!"per_template_set.xml".equalsIgnoreCase(a_fileName)&&!"per_template.xml".equalsIgnoreCase(a_fileName)
								&&!"per_template_item.xml".equalsIgnoreCase(a_fileName)&&!"per_template_point.xml".equalsIgnoreCase(a_fileName)&&!"per_point.xml".equalsIgnoreCase(a_fileName)
								&&!"per_pointset.xml".equalsIgnoreCase(a_fileName)&&!"per_grade.xml".equalsIgnoreCase(a_fileName)&&!"per_grade_template.xml".equalsIgnoreCase(a_fileName)&&!"per_grade_competence.xml".equalsIgnoreCase(a_fileName)) {
                 continue;
             }
					
					try{						
						fileIn = new FileInputStream((File) files.get(loop));
						origin = new BufferedInputStream(fileIn, 2048);
						outputStream.putNextEntry(new ZipEntry((String) fileNames.get(loop)));
						int count;
						while ((count = origin.read(data, 0, 2048)) != -1) {
							outputStream.write(data, 0, count);
						}
					}finally{
						PubFunc.closeResource(origin);
						PubFunc.closeIoResource(fileIn);
					}
				}
			 
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			PubFunc.closeResource(origin);
			PubFunc.closeResource(fileIn);
			PubFunc.closeIoResource(outputStream);
			PubFunc.closeIoResource(fileOut);
			PubFunc.closeResource(rs);
		}
		return filename;
	}
	
	private void writeFileOut(String subsys_id,String tableName,UserView view,String outName){
		RowSetToXmlBuilder builder=new RowSetToXmlBuilder(this.conn);
		FileOutputStream fileOut=null;
		try {
			 fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+"khtemplatedata"+System.getProperty("file.separator")+outName);
			 fileOut.write(getFileContent(true,true,subsys_id,tableName,builder,view));
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(fileOut);
		}
	}
	
	public static void  listFile(File parentFile, List nameList, List fileList)
	{
		if (parentFile.isDirectory())
		{
			File[] files = parentFile.listFiles();
			for (int loop=0; loop<files.length; loop++)
			{
				listFile(files[loop], nameList, fileList);
			}
		}
		else
		{
			fileList.add(parentFile);
			nameList.add(parentFile.getName());
		}
	}
	/****************************************************************************************************
	 * 导入文件的方法
	 * **************************************************************************************************/
	/**
	 * 读取压缩包里的文件
	 * @param inputStream
	 * @return
	 */
	static   HashMap   extZipFileList(InputStream inputStream)  
	{   
		  HashMap fileMap=new HashMap();
		  try   
		  {   
				  ZipInputStream   in   =   new   ZipInputStream(inputStream);   
				  ZipEntry   entry   =   null;   
			      while   ((entry =in.getNextEntry())!=null)   
			      {     
					  if   (entry.isDirectory())   {   
						  continue;
						  /*
						  File   file   =   new   File(extPlace   +   entryName);   
						  file.mkdirs();   
						  System.out.println("创建文件夹:"   +   entryName);  
						  */ 
					  }   
					  else   
					  {   
						 String entryName=entry.getName(); 
						 BufferedReader ain=new BufferedReader(new InputStreamReader(in));
						 StringBuffer s=new StringBuffer("");
						 String line;
						 while((line=ain.readLine())!=null)
						 {
							 s.append(line);
						 }
						 in.closeEntry();   
						 fileMap.put(entryName.toLowerCase(),s.toString());
						 //System.out.println(s.toString());   
					}   
			  }  
			  in.close();
		    
		  }   
		  catch   (IOException   e)   {   
			  e.printStackTrace();
		  }   
		  return fileMap;
	}   
	/**
	 * 分析选择的导入的文件（要做树）
	 * @param file
	 * @param fileName
	 * @param primaryKey
	 * @param desc
	 * @return
	 */
	public ArrayList analyseFileData(FormFile file,String fileName,String primaryKey,String desc)
	{
		ArrayList list = new ArrayList();
		try
		{
			HashMap fileMap =extZipFileList(file.getInputStream());
			String  fileContext=(String)fileMap.get(fileName);
			if(fileContext==null||fileContext.length()==0) {
                return list;
            }

			Document standard_doc = PubFunc.generateDom(fileContext);
			Element root=standard_doc.getRootElement();
			List nodeList=root.getChildren();
			LazyDynaBean a_bean=null;
			for(Iterator t=nodeList.iterator();t.hasNext();)
			{
				Element record=(Element)t.next();
				String id=record.getAttributeValue(primaryKey);
				XPath xPath0 = XPath.newInstance("./"+desc);
				Element nameNode = (Element) xPath0.selectSingleNode(record);
				String name=nameNode.getValue();
				a_bean=new LazyDynaBean();
				String setid="";
				if("per_template.xml".equalsIgnoreCase(fileName))
				{
					XPath xPathset = XPath.newInstance("./"+"template_setid");
					Element nameNodeset = (Element) xPathset.selectSingleNode(record);
					setid=nameNodeset.getValue();
					id+="`"+setid;
				}
				
				a_bean.set("name",name);
				a_bean.set("id",id);
				list.add(a_bean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	public String importData(InputStream inputstream,String subsys_id)
	{
		String flag = "1";
		try
		{
			HashMap fileMap = DownLoadXml.extZipFileList(inputstream);
			if(fileMap.get("per_template_set.xml")==null)
			{
				flag ="2";
				return flag;
			}
			Document templateset = PubFunc.generateDom((String)fileMap.get("per_template_set.xml"));

			Document template = PubFunc.generateDom((String)fileMap.get("per_template.xml"));

			Document templateitem = PubFunc.generateDom((String)fileMap.get("per_template_item.xml"));

			Document templatepoint = PubFunc.generateDom((String)fileMap.get("per_template_point.xml"));

			Document per_point = PubFunc.generateDom((String)fileMap.get("per_point.xml"));

			Document per_pointset = PubFunc.generateDom((String)fileMap.get("per_pointset.xml"));

			Document per_grade = PubFunc.generateDom((String)fileMap.get("per_grade.xml"));
			
			String per_comTable = "per_grade_template"; // 绩效标准标度
			if(subsys_id!=null && subsys_id.trim().length()>0 && "35".equalsIgnoreCase(subsys_id)) {
                per_comTable = "per_grade_competence"; // 能力素质标准标度
            }
			Document per_grade_template = PubFunc.generateDom((String)fileMap.get(per_comTable+".xml"));
			
			ArrayList templatesetlist = getTableData(templateset);
			ArrayList templatelist = getTableData(template);
			ArrayList templateitemlist = getTableData(templateitem);
			ArrayList templatepointlist=getTableData(templatepoint);
			ArrayList per_pointlist  = getTableData(per_point);
			ArrayList per_pointsetlist=getTableData(per_pointset);
			ArrayList per_gradelist=getTableData(per_grade);
			ArrayList per_grade_templatelist = getTableData(per_grade_template); 
		
			deleteExistData(conn,1,templatesetlist,subsys_id);
			deleteExistData(conn,2,templatelist,subsys_id);
			deleteExistData(conn,3, templateitemlist,subsys_id);
			deleteExistData(conn,4, templatepointlist,subsys_id);
			deleteExistData(conn,5, per_pointlist,subsys_id);
			deleteExistData(conn,6, per_pointsetlist,subsys_id);
			deleteExistData(conn,7, per_gradelist,subsys_id);
			deleteExistData(conn,8, per_grade_templatelist,subsys_id);
			
			HashMap templatesetColumn = DownLoadXml.getColumnTypeMap(this.conn, "per_template_set");
			HashMap templateColumn = DownLoadXml.getColumnTypeMap(this.conn, "per_template");
			HashMap templateitemColumn = DownLoadXml.getColumnTypeMap(this.conn,"per_template_item");
			HashMap templatepointColumn=DownLoadXml.getColumnTypeMap(this.conn, "per_template_point");
			HashMap per_pointColumn=DownLoadXml.getColumnTypeMap(this.conn,"per_point");
			HashMap per_pointsetColumn=DownLoadXml.getColumnTypeMap(this.conn, "per_pointset");
			HashMap per_gradeColumn = DownLoadXml.getColumnTypeMap(this.conn, "per_grade");
			HashMap per_grade_templateCloumn = DownLoadXml.getColumnTypeMap(this.conn, per_comTable);					
		
			importData(this.conn,1,templatesetlist,templatesetColumn);
			importData(this.conn,2,templatelist,templateColumn);
			importData(this.conn,3,templateitemlist,templateitemColumn);
			importData(this.conn,4,templatepointlist,templatepointColumn);
			importData(this.conn,5,per_pointlist,per_pointColumn);
			importData(this.conn,6,per_pointsetlist,per_pointsetColumn);
			importData(this.conn,7,per_gradelist,per_gradeColumn);
			importData(this.conn,8,per_grade_templatelist,per_grade_templateCloumn);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return flag;
	}
	/**
	 *导入数据
	 * @param con
	 * @param flag
	 * @param datalist
	 * @param columnMap
	 */
	public static void importData(Connection con,int flag,ArrayList datalist,HashMap columnMap)
	{
		try
		{
			Set keySet=columnMap.keySet();
			ArrayList list=new ArrayList();
			ContentDAO dao = new ContentDAO(con);
			Calendar d=Calendar.getInstance();
			for(int i=0;i<datalist.size();i++)
			{
				LazyDynaBean abean=(LazyDynaBean)datalist.get(i);
				RecordVo vo = null;
				if(flag==1) {
                    vo = new RecordVo("per_template_set");
                }
				if(flag==2) {
                    vo = new RecordVo("per_template");
                }
				if(flag==3) {
                    vo= new RecordVo("per_template_item");
                }
				if(flag==4) {
                    vo = new RecordVo("per_template_point");
                }
				if(flag==5) {
                    vo = new RecordVo("per_point");
                }
				if(flag==6) {
                    vo = new RecordVo("per_pointset");
                }
				if(flag==7) {
                    vo = new RecordVo("per_grade");
                }
				if(flag==8) {
                    vo = new RecordVo("per_grade_template");
                }
				for(Iterator t=keySet.iterator();t.hasNext();)
				{
					String columnName=((String)t.next()).toLowerCase();
					if(flag==2&&("create_date".equalsIgnoreCase(columnName)|| "modify_date".equalsIgnoreCase(columnName)))
					{
						vo.setDate("create_date", d.getTime());
					}else if(flag==6&& "create_date".equalsIgnoreCase(columnName))
					{
						vo.setDate("create_date", d.getTime());
					}
					else
					{
						String type=(String)columnMap.get(columnName);
						String value=(String)abean.get(columnName);
						if(value!=null&&value.length()>0)
						{
							if("D".equals(type))
							{
								
								String[] values=value.substring(10).split("-");
								Calendar dd=Calendar.getInstance();
								dd.set(Calendar.YEAR,Integer.parseInt(values[0]));
								dd.set(Calendar.MONTH,Integer.parseInt(values[1])-1);
								dd.set(Calendar.DATE,Integer.parseInt(values[2]));
								vo.setDate(columnName,dd.getTime());
								
							}
							else if("F".equals(type))
							{
								vo.setDouble(columnName,Double.parseDouble(value));
								
							}
							else if("N".equals(type))
							{
								vo.setInt(columnName,Integer.parseInt(value));
							}
							else
							{
								vo.setString(columnName,value);
							}
							
			    		}
	    			}
				}
				list.add(vo);
			}
			dao.addValueObject(list);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 根据Document得到所有数据
	 * @param doc
	 * @return
	 */
	public static ArrayList getTableData(Document doc)
	{
		ArrayList list = new ArrayList();
		try
		{
			Element root=doc.getRootElement();
			List childrenList=root.getChildren();
			LazyDynaBean a_bean=null;
			for(Iterator t=childrenList.iterator();t.hasNext();)
			{
				Element record=(Element)t.next();
				
				a_bean =new LazyDynaBean();
				List attributes=record.getAttributes();
				for(int i=0;i<attributes.size();i++)
				{
					Attribute att=(Attribute)attributes.get(i);
					a_bean.set(att.getName().toLowerCase(),att.getValue());					
				}
				List children=record.getChildren();
				for(int i=0;i<children.size();i++)
				{
					Element att=(Element)children.get(i);
					a_bean.set(att.getName().toLowerCase(),att.getValue());					
				}
				list.add(a_bean);
	    	}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 删除已存在的数据（采用覆盖的方式导入文件）
	 * @param flag
	 * @param dataList
	 * @param subsys_id
	 */
	public static void deleteExistData(Connection conn,int flag,ArrayList dataList,String subsys_id)
	{
		try
		{
			String keyCloumn="";
			String tableName="";
			if(flag==1)
			{
				keyCloumn="template_setid";
				tableName="per_template_set";
			}
			if(flag==2)
			{
				keyCloumn="template_id";
				tableName="per_template";
			}
			if(flag==3)
			{
				keyCloumn="item_id";
				tableName="per_template_item";
			}
			if(flag==4)
			{
				keyCloumn="point_id";
				tableName="per_template_point";
			}
			if(flag==5)
			{
				keyCloumn="point_id";
				tableName="per_point";
			}
			if(flag==6)
			{
				keyCloumn="pointsetid";
				tableName="per_pointset";
			}
			if(flag==7)
			{
				keyCloumn="grade_id";
				tableName="per_grade";
			}
			if(flag==8)
			{
				keyCloumn="grade_template_id";
				tableName="per_grade_template";
				if(subsys_id!=null && subsys_id.trim().length()>0 && "35".equalsIgnoreCase(subsys_id)) {
                    tableName = "per_grade_competence"; // 能力素质标准标度
                }
			}
			 StringBuffer buf = new StringBuffer("");
			 for(int i=0;i<dataList.size();i++)
			 {
				   LazyDynaBean bean = (LazyDynaBean)dataList.get(i);
				   buf.append(",'");
				   buf.append((String)bean.get(keyCloumn));
				   buf.append("'");
			 }
			 if(buf.toString().length()>0)
			 {
				 ContentDAO dao = new ContentDAO(conn);
				 String sql = " delete from "+tableName+"  where UPPER("+keyCloumn+") in("+buf.toString().substring(1).toUpperCase()+")";
				 dao.delete(sql, new ArrayList());
			 }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	//////////////////////////////////////////////////////////////////////////////
	/**
	 * 创建临时表，将zip文件的内容全部放入库中
	 * @param fieldList 表名集合
	 */
	public void createTreeTable(ArrayList tableNameList)
	{
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			DbWizard dbw=new DbWizard(this.conn);
  	        DBMetaModel dbmodel=new DBMetaModel(this.conn);
			for(int i=0;i<tableNameList.size();i++)
			{
				String tableName=(String)tableNameList.get(i);
				StringBuffer sql = new StringBuffer();
				String temp=tableName+"_TEMP";
				if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				{
					sql.append("CREATE GLOBAL TEMPORARY TABLE  "+tableName+"_TEMP On Commit Preserve Rows  as select * from "+tableName+" where 1=2");
				}
				else
				{
					sql.append("select * into ##"+tableName+"_TEMP from "+tableName+" where 1=2");
					temp="##"+tableName+"_TEMP";
				}
				if(dbw.isExistTable(temp, false))
				{
					//dbw.dropTable(temp);
					sql.setLength(0);
					sql.append(" delete from "+temp);
				}
				dao.update(sql.toString());
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public ArrayList getTableNameList(String model,String subsys_id)
	{
		ArrayList list = new ArrayList();	
		if("khtemplate".equalsIgnoreCase(model))
		{
    		list.add("per_template_set");
    		list.add("per_template");
    		list.add("per_template_item");
    		list.add("per_template_point");
    		list.add("per_point");
    		list.add("per_pointset");
    		list.add("per_grade");
    		if(subsys_id!=null && subsys_id.trim().length()>0 && "35".equalsIgnoreCase(subsys_id)) {
                list.add("per_grade_competence");
            } else {
                list.add("per_grade_template");
            }
		}
		else if("khpoint".equalsIgnoreCase(model))
		{
			list.add("per_point");
    		list.add("per_pointset");
    		list.add("per_grade");
    		if(subsys_id!=null && subsys_id.trim().length()>0 && "35".equalsIgnoreCase(subsys_id)) {
                list.add("per_grade_competence");
            } else {
                list.add("per_grade_template");
            }
		}
		return list;
	}
	/**
	 * 准备插入临时表的数据
	 * @param inputstream 选择的导入文件
	 * @param judgeSTR 判断文件是否正确的表示（就是一个xml文件的名字，如果不存在视为不正确）
	 * @param tableNameList 所有的xml文件的名字（也是表名）
	 * @param currentTimeField 当是时间型字段是，要取系统时间的指标集合
	 * @return
	 */
	public String prepareDataToInsert(InputStream inputstream,String judgeSTR,ArrayList tableNameList,HashMap currentTimeField) throws Exception
	{
		String flag = "1";
		try
		{
			HashMap fileMap = DownLoadXml.extZipFileList(inputstream);
			if(fileMap.get(judgeSTR)==null)
			{
				flag ="2";
				return flag;
			}
			for(int i=0;i<tableNameList.size();i++)
			{
                String tableName =(String)tableNameList.get(i)+".xml";
                if(!fileMap.containsKey(tableName)) {
                	throw new Exception("没有"+tableName+"文件，请确认后重新上传");
                }
	    		
			}
			for(int i=0;i<tableNameList.size();i++)
			{
                String tableName =(String)tableNameList.get(i);
	    		Document document = PubFunc.generateDom((String)fileMap.get(tableName+".xml"));
	    		ArrayList dataList = getTableData(document);	
	    		HashMap columnMap = DownLoadXml.getColumnTypeMap(this.conn, tableName);
	    		insertDataToTempTable(this.conn,tableName+"_TEMP",dataList,columnMap,currentTimeField);
			}
			
		}
		catch(Exception e)
		{
			throw GeneralExceptionHandler.Handle(e);
		}
		
		return flag;
	}
	/**
	 * 将数据存入临时表
	 * @param con
	 * @param tableName
	 * @param datalist
	 * @param columnMap
	 * @param currentTimeField
	 */
	public static void insertDataToTempTable(Connection con,String tableName,ArrayList datalist,HashMap columnMap,HashMap currentTimeField)
	{
		PreparedStatement pstmt = null;
		DbSecurityImpl dbS = new DbSecurityImpl();
		try
		{
			Set keySet=columnMap.keySet();
			ContentDAO dao = new ContentDAO(con);
			Calendar d=Calendar.getInstance();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			StringBuffer cloumn = new StringBuffer();
			StringBuffer pbuf=new StringBuffer("");
			
			String temp=tableName;
			if(Sql_switcher.searchDbServer()==Constant.MSSQL) {
                temp="##"+tableName;
            }
		   
			for(int i=0;i<datalist.size();i++)
			{
				LazyDynaBean abean=(LazyDynaBean)datalist.get(i);
				pbuf.setLength(0);
				cloumn.setLength(0);
				for(Iterator t=keySet.iterator();t.hasNext();)
				{
					String columnName=((String)t.next()).toLowerCase();
					String type=(String)columnMap.get(columnName);
					String value=(String)abean.get(columnName);
					if(value==null|| "".equals(value)) {
                        continue;
                    }
					pbuf.append("?,");
					cloumn.append(columnName+",");
					
				}
				
				 pbuf.setLength(pbuf.length()-1);
				 cloumn.setLength(cloumn.length()-1);
				 String ss="insert into "+temp+"("+cloumn.toString()+") values("+pbuf.toString()+")";
				 String sql = "insert into "+temp+"("+cloumn.toString()+") values("+pbuf.toString()+")";
				 pstmt=con.prepareStatement(sql);
				int index=1;
				for(Iterator t=keySet.iterator();t.hasNext();)
				{
					String columnName=((String)t.next()).toLowerCase();
					String type=(String)columnMap.get(columnName);
					String value=(String)abean.get(columnName);
					if(value==null|| "".equals(value)) {
                        continue;
                    }
					if("D".equalsIgnoreCase(type))
					{
						if(currentTimeField.get(columnName)!=null)
						{
							pstmt.setDate(index, new java.sql.Date(new java.util.Date().getTime()));
						}
						else
						{
							pstmt.setDate(index, new java.sql.Date(java.sql.Date.parse(value.substring(0, 10).replaceAll("-", "/"))));
						}
					}
					else if("F".equalsIgnoreCase(type))
					{
						pstmt.setDouble(index, Double.parseDouble((value)));
					}
					else if("N".equalsIgnoreCase(type))
					{
						pstmt.setInt(index, Integer.parseInt(value));
					}
					else
					{
						pstmt.setString(index,value);
					}
					index++;
				}
				// 打开Wallet
				dbS.open(con, sql);
				pstmt.execute();
				if(pstmt!=null) {
                    pstmt.close();
                }
			}
			 
			
			 
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			if(pstmt!=null)
			{
				try
				{
					pstmt.close();
					// 关闭Wallet
					dbS.close(con);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	/**当选中模板，但是没选中模板分类的时候 自动关联模板分类*/
	public String autoRelevanceSetID(String setid,String templateid,String setTableName,String setColumn,String tableName,String column)
	{
		String ss="";
		RowSet rs=null;
		try
		{
			ss=setid.toString();
			String sql = "select "+setColumn+" from "+tableName+" where ("+templateid+") group by "+setColumn;
			ContentDAO dao = new ContentDAO(this.conn);
			 rs= dao.search(sql);
		    while(rs.next())
		    {
		    	String templateSetID=rs.getString(setColumn);
		    	if(templateSetID==null|| "".equals(templateSetID)) {
                    continue;
                }
		    	if((","+setid.toUpperCase()).indexOf((","+templateSetID+","))!=-1) {
                    continue;
                } else
		    	{
		    		StringBuffer temp=new StringBuffer("");
		    		this.getParentLink(templateSetID, temp, dao, setTableName, setColumn);
		    		ss+=temp.toString();
		    	}
		    }
		    
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			if(rs!=null)
			{
				try
				{
					rs.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		return ss;
	}
	private String returnValue="";
	/**当选中子模板分类，没选中父分类的时候，自动关联父分类*/
	public String autoRelevancePrentID(String setId,String tableName,String whereColumn,ContentDAO dao,String column)
	{
		String ss="";
		RowSet rs = null;
		try
		{
			returnValue=setId;
			String sql = " select parent_id from "+tableName+" where "+whereColumn;
			rs = dao.search(sql);
			while(rs.next())
			{
				String parent_id=rs.getString("parent_id");
				if(parent_id==null|| "".equals(parent_id)) {
                    continue;
                } else
				{
					if((","+returnValue.toUpperCase()).indexOf((","+parent_id+","))!=-1)
					{
						continue;
					}
					else
					{
						this.getSetParentLink(dao, column, parent_id, tableName);
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return returnValue;
	}
	public void getSetParentLink(ContentDAO dao,String column,String id,String tableName)
	{
		try{
			String sql = " select parent_id from "+tableName+" where "+column+"="+id;
			RowSet rs = dao.search(sql);
			while(rs.next())
			{
				String parent_id=rs.getString("parent_id");
				if(parent_id==null|| "".equals(parent_id))
				{
					if((","+this.returnValue.toUpperCase()).indexOf((","+id+","))==-1)
					{
				    	this.returnValue+=id+",";
				    	
					}
					return;
				}
				else
				{
					if((","+this.returnValue.toUpperCase()).indexOf((","+id+","))==-1)
					{
				    	this.returnValue+=id+",";
					}
					this.getSetParentLink(dao, column, parent_id, tableName);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public void  getParentLink(String setid,StringBuffer buf,ContentDAO dao,String setTableName,String setColumn)
	{
		try
		{
			RowSet rs = dao.search("select parent_id from "+setTableName+" where "+setColumn+"="+setid);
			while(rs.next())
			{
				String parent_id=rs.getString("parent_id");
				if(parent_id==null|| "".equals(parent_id))
				{
					buf.append(setid+",");
					return;
				}
				else
				{
					buf.append(setid+",");
					this.getParentLink(parent_id, buf, dao, setTableName, setColumn);
				}
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 导入模板/指标 保存模板/指标资源权限
	 * @param dao
	 * @param role_id
	 * @param flag
	 * @param res_str
	 */
	public void saveResourceString(ContentDAO dao,String role_id,String flag,String res_str){
	    if(res_str==null) {
            res_str="";
        }
        /*
        RecordVo vo=new RecordVo("t_sys_function_priv",1);
        vo.setString("id",role_id);
        vo.setString("status",flag);
        vo.setString("warnpriv",res_str);
        cat.debug("role_vo="+vo.toString());	
        SysPrivBo sysbo=new SysPrivBo(vo,this.getFrameconn());
        sysbo.save(); 
        */
	      StringBuffer strsql=new StringBuffer();
	      strsql.append("select id from t_sys_function_priv where id='");
	      strsql.append(role_id);
	      strsql.append("' and status=");
	      strsql.append(flag);
	      try
	      {
	    	ArrayList paralist=new ArrayList();
	    	ResultSet res=dao.search(strsql.toString());
	    	if(res.next())
	    	{
		    	paralist.add(res_str);	    		
	    		strsql.setLength(0);
	    		strsql.append("update t_sys_function_priv set warnpriv=?");
	    		//strsql.append(field_str);
	    		strsql.append(" where id='");
	    		strsql.append(role_id);
	    		strsql.append("' and status=");
	    		strsql.append(flag);
	    	}
	    	else
	    	{
		    	paralist.add(role_id);	    		
		    	paralist.add(res_str);	    		
	    		strsql.setLength(0);
	    		strsql.append("insert into t_sys_function_priv (id,warnpriv,status) values(?,?,");
	    		strsql.append(flag);
	    		strsql.append(")");
	    	}
	    	dao.update(strsql.toString(),paralist);
	      }
	      catch(SQLException sqle)
	      {
	    	  sqle.printStackTrace();
	      }
	}
	public HashMap importData2(String selectid,UserView view,String subsys_id)
	{
		HashMap returnMap = new HashMap();
		RowSet rs = null;
		try{
			String[] arr = selectid.split(",");
			/**模版分类id*/
			StringBuffer setid=new StringBuffer("");
			/**模版id*/
			StringBuffer templateid=new StringBuffer("");
			StringBuffer buf_where = new StringBuffer("");
			StringBuffer array_buf = new StringBuffer("");
			for(int i=0;i<arr.length;i++)
			{
				if(arr[i]==null|| "".equals(arr[i])) {
                    continue;
                }
				String id=arr[i].split("#")[0];
				String type=arr[i].split("#")[1];//=0模板分类 =1是模板
				if("1".equals(type))
				{
					templateid.append("'"+id+"',");
					buf_where.append(" or UPPER(template_id)='"+id.toUpperCase()+"'");
					array_buf.append(id+",");
				}
				else
				{
					setid.append(id+",");
				}
			}
			String per_comTable = "per_grade_template"; // 绩效标准标度
			if(subsys_id!=null && subsys_id.trim().length()>0 && "35".equalsIgnoreCase(subsys_id)) {
                per_comTable = "per_grade_competence"; // 能力素质标准标度
            }
			String tempSetTableName="##per_template_set_temp";
			String tempTableName="##per_template_temp";//seq
			String itemTable="##per_template_item_temp";//seq
			String pointtableName="##per_template_point_temp";//seq
			String	perPointtableName="##per_point_temp";//seq
			String per_pointSettableName="##per_pointset_temp";//seq
			String	per_gradetableName="##per_grade_temp";
			String	pgttableName="##per_grade_template_temp";
			if(subsys_id!=null && subsys_id.trim().length()>0 && "35".equalsIgnoreCase(subsys_id)) {
                pgttableName="##per_grade_competence_temp";
            }
			if(Sql_switcher.searchDbServer() == Constant.ORACEL)
			{
				tempSetTableName="per_template_set_temp";
				tempTableName="per_template_temp";
				itemTable="per_template_item_temp";
				pointtableName="per_template_point_temp";
				perPointtableName="per_point_temp";
				per_pointSettableName="per_pointset_temp";
				per_gradetableName="per_grade_temp";
				pgttableName="per_grade_template_temp";
				if(subsys_id!=null && subsys_id.trim().length()>0 && "35".equalsIgnoreCase(subsys_id)) {
                    pgttableName="per_grade_competence_temp";
                }
			}
			/**导入规则：只有分类考虑名称相同的，其他一律覆盖导入
			 * 1如果存在名字相同的模板分类，则保留原来的，导入文件中的不用导入
			 * 2模板覆盖（判断目标库中，是否有使用的模板号，有的话，不导入）
			 * 3其他覆盖
			 * */
			ContentDAO dao = new ContentDAO(this.conn);
			String tt=this.autoRelevanceSetID(setid.toString(), buf_where.length()>0?buf_where.toString().substring(3):" 1=2", tempSetTableName, "template_setid", tempTableName, "template_id");
			setid.setLength(0);
			setid.append(tt);
			if(setid.toString().length()>0)
			{
				/***/
				String ss=this.autoRelevancePrentID(tt, tempSetTableName, "(template_setid in ("+(tt.substring(0,tt.length()-1))+"))", dao, "template_setid");
				setid.setLength(0);
				setid.append(ss.substring(0, ss.length()-1));
				dao.delete("delete from "+tempSetTableName+" where template_setid not in("+setid.toString()+")", new ArrayList());
				if(templateid.length()>0) {
                    templateid.setLength(templateid.length()-1);
                }
				String where=templateid.length()>0?templateid.toString():"'-1'";
				dao.delete("delete from "+tempTableName+" where UPPER(template_id) not in ("+where.toUpperCase()+")", new ArrayList());
				dao.delete("delete from "+pointtableName+" where item_id in (select item_id from "+itemTable+" where UPPER(template_id) not in ("+where.toUpperCase()+"))", new ArrayList());
				dao.delete("delete from "+itemTable+" where UPPER(template_id) not in ("+where.toUpperCase()+")", new ArrayList());
				dao.delete("delete from "+perPointtableName+" where UPPER(point_id) not in (select distinct UPPER(point_id) from "+pointtableName+")", new ArrayList());
				dao.delete("delete from "+per_gradetableName+" where UPPER(point_id) not in (select distinct UPPER(point_id) from "+perPointtableName+")", new ArrayList());
				HashMap map = this.getNameMap(dao);
				/**原来库中存在的模板分类名称name<--->id*/
				HashMap setNameMap = (HashMap)map.get("nameMap");
				/**原来库中存在的模板分类id，id<--->id*/
				HashMap setIdMap = (HashMap)map.get("idMap");
				/**已使用的模板id<----->id*/
				HashMap allUsedTemplate=this.getAllUsedTemplate(dao);
				/**要导入的模板分类，id<---->name*/
				HashMap tempNameMap = this.getTempNameMap(setid.toString(), tempSetTableName, dao);
				String [] array=setid.toString().split(",");
				//int id =getMaxId("per_template_set", "template_setid")+2;
				boolean flag=true;
				if(view.isSuper_admin()|| "1".equals(view.getGroupId()))
				{
					flag=false;
				}
				for(int i=0;i<array.length;i++)
				{
					String sid=array[i];
					String sName=(String)tempNameMap.get(sid);
					if(setNameMap.get(sName.toUpperCase())!=null)
					{
						String yid=(String)setNameMap.get(sName.toUpperCase());
						StringBuffer sql = new StringBuffer();
						/**将临时分类表中父节点等于导入分类节点的分类的父节点，更新为名称相同的分类的*/
						sql.append("update "+tempSetTableName+" set parent_id='"+yid+"' where parent_id='"+sid+"'");
						dao.update(sql.toString());
						sql.setLength(0);
						sql.append("update "+tempSetTableName+" set child_id='"+yid+"' where child_id='"+sid+"'");
						dao.update(sql.toString());
						sql.setLength(0);
						sql.append("delete from  "+tempSetTableName+" where UPPER(template_setid)='"+sid.toUpperCase()+"'");
						dao.update(sql.toString());
						sql.setLength(0);
						sql.append("update "+tempTableName+" set template_setid="+yid+"  where template_setid="+sid);
						dao.update(sql.toString());
						if(flag&&!view.isHaveResource(IResourceConstant.KH_MODULE,yid))
	        			{
							view.addResourceMx(yid, IResourceConstant.KH_MODULE);
	        			}
						
					}
					else
					{
						IDGenerator idg = new IDGenerator(2, this.conn);
						int id = new Integer(idg.getId("per_template_set.template_setId")).intValue();
						StringBuffer sql = new StringBuffer();
						/**将临时分类表中父节点等于导入分类节点的分类的父节点，更新为名称相同的分类的*/
						sql.append("update "+tempSetTableName+" set parent_id='"+id+"' where parent_id='"+sid+"'");
						dao.update(sql.toString());
						sql.setLength(0);
						sql.append("update "+tempSetTableName+" set child_id='"+id+"' where child_id='"+sid+"'");
						dao.update(sql.toString());
						sql.setLength(0);
						sql.append("update "+tempTableName+" set template_setid='"+id+"' where UPPER(template_setid)='"+sid.toUpperCase()+"'");
						dao.update(sql.toString());
						sql.setLength(0);
						sql.append("update "+tempSetTableName+" set template_setid="+id+" where template_setid="+sid);
						dao.update(sql.toString());
						if(flag&&!view.isHaveResource(IResourceConstant.KH_MODULE,id+""))
	        			{
							view.addResourceMx(id+"", IResourceConstant.KH_MODULE);
	        			}
					}
				}
				if(array_buf.length()>0)
				{
					String temp=array_buf.toString().substring(0, array_buf.length()-1);
					String[] array2=temp.split(",");
					for(int i=0;i<array2.length;i++)
					{
						String tid=array2[i];
						if(allUsedTemplate.get(tid.toUpperCase())!=null)
						{
							//dao.delete("delete from "+perPointtableName+" where UPPER(point_id) in (select UPPER(point_id) from "+pointtableName+" where item_id in (select item_id from "+itemTable+" where UPPER(template_id)='"+tid.toUpperCase()+"'))", new ArrayList());
							dao.delete("delete from "+tempTableName+" where UPPER(template_id)='"+tid.toUpperCase()+"'", new ArrayList());
							dao.delete("delete from "+pointtableName+" where item_id in (select item_id from "+itemTable+" where UPPER(template_id)='"+tid.toUpperCase()+"')", new ArrayList());
							dao.delete("delete from "+itemTable+" where UPPER(template_id)='"+tid.toUpperCase()+"'", new ArrayList());
							dao.delete("delete from "+perPointtableName+" where UPPER(point_id) not in (select distinct UPPER(point_id) from "+pointtableName+")", new ArrayList());
							dao.delete("delete from "+per_gradetableName+" where UPPER(point_id) not in (select distinct UPPER(point_id) from "+perPointtableName+")", new ArrayList());
						}
						if(flag&&!view.isHaveResource(IResourceConstant.KH_MODULE,tid))
	        			{
							view.addResourceMx(tid, IResourceConstant.KH_MODULE);
							/****************zzk 2014/2/26 导入模板 增加用户模板资源权限********************/
							String role_id="";
							int status=view.getStatus();// 0 业务用户 1 角色  4 自助用户
							if(status==4){
								role_id=view.getDbname()+view.getA0100();
							}else{
								role_id=view.getUserName();
							}
							SysPrivBo privbo=new SysPrivBo(role_id,status+"",conn,"warnpriv");
							String res_str=privbo.getWarn_str();
							ResourceParser parser=new ResourceParser(res_str,IResourceConstant.KH_MODULE);
							parser.addContent(tid+"");
							res_str=parser.outResourceContent();
							saveResourceString(dao,role_id,status+"",res_str);
					    
	        			}
					}
				}
				this.importPoint(view,false);
				dao.delete("delete from per_template where template_id in (select template_id from "+tempTableName+")", new ArrayList());
				dao.delete("delete from per_template_item where item_id in (select item_id from "+itemTable+")", new ArrayList());
				dao.delete("delete from per_template_point " +
						" where exists (select null from "+pointtableName+" where per_template_point.item_id="+pointtableName+".item_id and " +
								" per_template_point.point_id="+pointtableName+".point_id)", new ArrayList());
				//dao.delete("delete from per_pointset where pointsetid in (select pointsetid from "+per_pointSettableName+")", new ArrayList());
				dao.delete("delete from per_point where UPPER(point_id) in (select UPPER(point_id) from "+perPointtableName+")", new ArrayList());
				
				dao.delete("delete from per_grade where UPPER(point_id) in (select UPPER(point_id) from "+per_gradetableName+")", new ArrayList());
				dao.delete("delete from per_grade where UPPER(grade_id) in (select UPPER(grade_id) from "+per_gradetableName+")", new ArrayList());
								
				
				/**判断是否删除标准标度，用新导入进来的*/
				boolean signLogo = isImportGradeBd(pgttableName,per_comTable);				
				if(signLogo) {
                    dao.delete("delete from "+per_comTable+" ", new ArrayList());//where UPPER(grade_template_id) in (select UPPER(grade_template_id) from "+pgttableName+")
                }
			   												
				dao.insert("insert into per_template_set(template_setid,name,parent_id,child_id,b0110,validFlag,subsys_id) " +
			    		"select template_setid,name,parent_id,child_id,b0110,validFlag,subsys_id  from  "+tempSetTableName+"", new ArrayList());
			    dao.insert("insert into per_template(template_id,template_setid,seq,name,topscore,currentscore,create_date,modify_date,validflag,valid_date," +
			    		"invalid_date,status,tabids)select template_id,template_setid,seq,name,topscore,currentscore,create_date,modify_date,validflag," +
			    		"valid_date,invalid_date,status,tabids from "+tempTableName, new ArrayList());
			    dao.insert("insert into per_template_item(item_id,parent_id,child_id,template_id,itemdesc,seq,kind,score,rank,rank_type)" +
			    		"select item_id,parent_id,child_id,template_id,itemdesc,seq,kind,score,rank,rank_type from "+itemTable, new ArrayList());
			    dao.insert("insert into per_template_point(point_id,item_id,score,seq,rank,rank_type,formula)" +
			    		"select point_id,item_id,score,seq,rank,rank_type,formula from "+pointtableName, new ArrayList());
			    
			    dao.insert("insert into per_grade(grade_id,point_id,gradevalue,gradedesc,gradecode,top_value,bottom_value)" +
			    		"select grade_id,point_id,gradevalue,gradedesc,gradecode,top_value,bottom_value from "+per_gradetableName, new ArrayList());
			    if(signLogo)
			    {
				    dao.insert("insert into "+per_comTable+"(grade_template_id,gradevalue,gradedesc,top_value,bottom_value)select" +
				    		" grade_template_id,gradevalue,gradedesc,top_value,bottom_value from "+pgttableName, new ArrayList());
			    }
			    dao.insert("insert into per_pointset(pointsetid,pointsetname,parent_id,child_id,seq,create_date,validflag,invalid_date,b0110,subsys_id) select " +
			    		"pointsetid,pointsetname,parent_id,child_id,seq,create_date,validflag,invalid_date,b0110,subsys_id from  "+per_pointSettableName, new ArrayList());
			    dao.insert("insert into per_point (point_id,seq,pointsetid,pointname,pointkind,formula,validflag,description,visible,fielditem,l_FieldItem,status,pointtype,pointctrl,kh_content,gd_principle)" +
			    		"select point_id,seq,pointsetid,pointname,pointkind,formula,validflag,description,visible,fielditem,l_FieldItem,status,pointtype,pointctrl,kh_content,gd_principle from "+perPointtableName, new ArrayList());
			    rs=dao.search("select count(*) from "+tempTableName);
			    int countTemplate=0;
			    int countPoint=0;
			    while(rs.next())
			    {
			    	countTemplate=rs.getInt(1);
			    }
			    rs = dao.search("select count(*) from "+perPointtableName);
			    while(rs.next())
			    {
			    	countPoint=rs.getInt(1);
			    }
			    returnMap.put("countTemplate", countTemplate+"");
			    returnMap.put("countPoint",countPoint+"");
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			if(rs!=null)
			{
				try
				{
					rs.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		return returnMap;
	}
	
	public HashMap importPointData(String selectid,UserView view,String subsys_id)
	{
		HashMap returnMap = new HashMap();
		RowSet rs = null;
		try{
			String[] arr = selectid.split(",");
			StringBuffer setid=new StringBuffer("");
			StringBuffer templateid=new StringBuffer("");
			StringBuffer buf_where = new StringBuffer("");
			StringBuffer array_buf = new StringBuffer("");
			for(int i=0;i<arr.length;i++)
			{
				if(arr[i]==null|| "".equals(arr[i])) {
                    continue;
                }
				String id=arr[i].split("#")[0];
				String type=arr[i].split("#")[1];//=0模板分类 =1是模板
				if("1".equals(type))
				{
					templateid.append("'"+id+"',");
					buf_where.append(" or UPPER(point_id)='"+id.toUpperCase()+"'");
					array_buf.append(id+",");
				}
				else
				{
					setid.append(id+",");
				}
			}
			String per_comTable = "per_grade_template"; // 绩效标准标度
			if(subsys_id!=null && subsys_id.trim().length()>0 && "35".equalsIgnoreCase(subsys_id)) {
                per_comTable = "per_grade_competence"; // 能力素质标准标度
            }
			String	perPointtableName="##per_point_temp";
			String per_pointSettableName="##per_pointset_temp";
			String	per_gradetableName="##per_grade_temp";
			String	pgttableName="##per_grade_template_temp";
			if(subsys_id!=null && subsys_id.trim().length()>0 && "35".equalsIgnoreCase(subsys_id)) {
                pgttableName="##per_grade_competence_temp";
            }
			if(Sql_switcher.searchDbServer() == Constant.ORACEL)
			{
				perPointtableName="per_point_temp";
				per_pointSettableName="per_pointset_temp";
				per_gradetableName="per_grade_temp";
				pgttableName="per_grade_template_temp";
				if(subsys_id!=null && subsys_id.trim().length()>0 && "35".equalsIgnoreCase(subsys_id)) {
                    pgttableName="per_grade_competence_temp";
                }
			}
			/**导入规则：只有分类考虑名称相同的，其他一律覆盖导入
			 * 1如果存在名字相同的模板分类，则保留原来的，导入文件中的不用导入
			 * 2模板覆盖（判断目标库中，是否有使用的模板号，有的话，不导入）
			 * 3其他覆盖
			 * */
			ContentDAO dao = new ContentDAO(this.conn);
			String tt=this.autoRelevanceSetID(setid.toString(), buf_where.length()>0?buf_where.toString().substring(3):" 1=2", per_pointSettableName, "pointsetid", perPointtableName, "point_id");
			setid.setLength(0);
			setid.append(tt);
			if(setid.toString().length()>0)
			{
				/***/
				String ss=this.autoRelevancePrentID(tt, per_pointSettableName, "(pointsetid in ("+(tt.substring(0,tt.length()-1))+"))", dao, "pointsetid");
				setid.setLength(0);
				setid.append(ss.subSequence(0, ss.length()-1));
				dao.delete("delete from "+per_pointSettableName+" where pointsetid not in("+setid.toString()+")", new ArrayList());
				if(templateid.length()>0) {
                    templateid.setLength(templateid.length()-1);
                }
				String where=templateid.length()>0?templateid.toString():"'-1'";
				// 批量删除perPointtableName临时表中不需要保存的数据,yangj 2014-12-23
				this.importPointDataBase(perPointtableName, where, dao, rs);
				this.importPointDataBase(per_gradetableName, where, dao, rs);
				// dao.delete("delete from "+perPointtableName+" where UPPER(point_id) not in ("+where.toUpperCase()+")", new ArrayList());
				// dao.delete("delete from "+per_gradetableName+" where UPPER(point_id) not in ("+where.toUpperCase()+")", new ArrayList());
			    // dao.delete("delete from per_grade where UPPER(point_id) in ("+where.toUpperCase()+")", new ArrayList());
				this.importPoint(view,true);
 				dao.delete("delete from per_point where UPPER(point_id) in (select UPPER(point_id) from "+perPointtableName+")", new ArrayList());
				dao.delete("delete from per_grade where UPPER(grade_id) in (select UPPER(grade_id) from "+per_gradetableName+")", new ArrayList());
				
				// 插入数据
				/**判断是否删除标准标度，用新导入进来的*/
				boolean signLogo = isImportGradeBd(pgttableName,per_comTable);				
				if(signLogo) {
                    dao.delete("delete from "+per_comTable+" ", new ArrayList());//where UPPER(grade_template_id) in (select UPPER(grade_template_id) from "+pgttableName+")
                }
				
			    dao.insert("insert into per_grade(grade_id,point_id,gradevalue,gradedesc,gradecode,top_value,bottom_value)" +
			    		"select grade_id,point_id,gradevalue,gradedesc,gradecode,top_value,bottom_value from "+per_gradetableName, new ArrayList());
			    
			    if(signLogo)
			    {
				    dao.insert("insert into "+per_comTable+"(grade_template_id,gradevalue,gradedesc,top_value,bottom_value)select" +
				    		" grade_template_id,gradevalue,gradedesc,top_value,bottom_value from "+pgttableName, new ArrayList());
			    }
			    //xus 20/5/12  【59233】绩效管理-考核指标批量导出，然后重新导入系统，会清空指标库
			    dao.insert("insert into per_pointset(pointsetid,pointsetname,parent_id,child_id,seq,create_date,validflag,invalid_date,b0110,subsys_id) select pointsetid,pointsetname,parent_id,child_id,seq,create_date,validflag,invalid_date,b0110,subsys_id from  "+
			    		"(SELECT pointsetid,pointsetname,parent_id,child_id,seq,create_date,validflag,invalid_date,b0110,subsys_id FROM "+per_pointSettableName+" t WHERE NOT EXISTS (SELECT pointsetid FROM per_pointset t1 WHERE t.pointsetid = t1.pointsetid)) a", 
			    		new ArrayList());
			    dao.insert("insert into per_point (point_id,seq,pointsetid,pointname,pointkind,formula,validflag,description,visible,fielditem,l_FieldItem,status,pointtype,pointctrl,kh_content,gd_principle)" +
			    		"select point_id,seq,pointsetid,pointname,pointkind,formula,validflag,description,visible,fielditem,l_FieldItem,status,pointtype,pointctrl,kh_content,gd_principle from "+perPointtableName, new ArrayList());
			    // 修改父节点并删除已存在的指标分类
			    this.changeAndDelete(pointsetid, per_pointSettableName);
			    // 返回修改的指标数目
			    rs = dao.search("select count(*) from "+perPointtableName);
			    if(rs.next()) {
			    	returnMap.put("countPoint", rs.getInt(1) + "");
			    }
			    returnMap.put("countTemplate", "0");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			try {
				if (rs != null) {
					try {
						rs.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return returnMap;
	}
	
	/**
	 * 
	 * @Title: importPointDataBase   
	 * @Description: 批量删除perPointtableName临时表中不需要保存的数据    
	 * @param table 表名
	 * @param save  需要保存的数据
	 * @param dao
	 * @param rowSet 
	 * @return void
	 */
	private void importPointDataBase(String table, String save, ContentDAO dao, RowSet rowSet) {
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select point_id from " + table);
			rowSet = dao.search(sql.toString());
			String pointId;
			sql.setLength(0);
			int i = 0;
			while (rowSet.next()) {
				pointId = rowSet.getString("point_id");
				// 只处理需要删除的数据
				if (save.indexOf("'" + pointId + "'") == -1) {
					sql.append(",'" + pointId + "'");
					// 批量处理，每500个处理一次
					if (++i == 500) {
						dao.delete("delete from " + table + " where UPPER(point_id) in (" + sql.substring(1) + ")", new ArrayList());
						sql.setLength(0);
						i = 0;
					}
				}
			}
			// 剩余500个以内的数据统一处理
			if (sql.length() > 0) {
				dao.delete("delete from " + table + " where UPPER(point_id) in (" + sql.substring(1) + ")", new ArrayList());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// 判断是否导入标准标度  JinChunhai 2011.11.29
	public boolean isImportGradeBd(String pgttableName,String per_comTable)
	{
		boolean flag = true;
		RowSet rs = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			int gradeBd = 0;
			rs=dao.search("select count(*) from "+per_comTable+"");
			if(rs.next())
			{
				gradeBd = rs.getInt(1);
			}
			
			int gradeImportBd = 0;
			rs = dao.search("select count(*) from "+pgttableName);
			if(rs.next())
			{
				gradeImportBd = rs.getInt(1);
			}
			
			if(gradeBd>gradeImportBd) {
                flag = false;
            }
			
			if(rs!=null) {
                rs.close();
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
		return flag;
	}
		
	
	public void importPoint(UserView view,boolean isResource)
	{
		RowSet rs = null;
		try
		{
			String	perPointtableName="##per_point_temp";
			String per_pointSettableName="##per_pointset_temp";
			if(Sql_switcher.searchDbServer() == Constant.ORACEL)
			{
				perPointtableName="per_point_temp";
				per_pointSettableName="per_pointset_temp";
			}
			HashMap pointNameMap=new HashMap();
			HashMap pointIdMap=new HashMap();
			HashMap importNameMap = new HashMap();
			HashMap importIdMap = new HashMap();
			ContentDAO dao = new ContentDAO(this.conn);
			boolean flag=true;
			if(view.isSuper_admin()|| "1".equals(view.getGroupId()))
			{
				flag=false;
			}
			String sql = "select pointsetid,pointsetname from per_pointset";
			rs = dao.search(sql);
			while(rs.next())
			{
				String pointsetid=rs.getString("pointsetid");
				StringBuffer  buf = new StringBuffer();
				this.getParentNameLink(buf, pointsetid, dao, "per_pointset", "pointsetid", "parent_id", "pointsetname");
				String pointsetname=buf.toString();
				pointNameMap.put(pointsetname.toUpperCase(), pointsetid);
				
			}
			sql="select pointsetid,pointsetname from "+per_pointSettableName;
			rs=dao.search(sql);
			while(rs.next())
			{
				String pointsetid=rs.getString("pointsetid");
				StringBuffer  buf = new StringBuffer();
				this.getParentNameLink(buf, pointsetid, dao, per_pointSettableName, "pointsetid", "parent_id", "pointsetname");
				String pointsetname=buf.toString();
				importNameMap.put(pointsetname.toUpperCase(), pointsetid);
				importIdMap.put(pointsetid.toUpperCase(), pointsetname);
			}
			Set keySet=importNameMap.keySet();
			int id =getMaxId("per_pointset", "pointsetid");
			for(Iterator t=keySet.iterator();t.hasNext();)
			{
				String importName=(String)t.next();
				if(pointNameMap.get(importName.toUpperCase())!=null)
				{
					String yid=(String)pointNameMap.get(importName.toUpperCase());
					String sid=(String)importNameMap.get(importName.toUpperCase());
					if(flag&&!view.isHaveResource(IResourceConstant.KH_FIELD,yid))
        			{
						view.addResourceMx(yid, IResourceConstant.KH_FIELD);
        			}
					StringBuffer buf = new StringBuffer("");
					buf.append("update "+per_pointSettableName+" set parent_id="+yid+" where parent_id="+sid);
					dao.update(buf.toString());
					buf.setLength(0);
					buf.append("update "+per_pointSettableName+" set child_id="+yid+" where child_id="+sid);
					dao.update(buf.toString());
					buf.setLength(0);
					buf.append("delete from "+per_pointSettableName+" where pointsetid="+sid);
					dao.delete(buf.toString(), new ArrayList());
					buf.setLength(0);
					buf.append("update "+perPointtableName+" set pointsetid="+yid+" where pointsetid="+sid);
					dao.update(buf.toString());
				}else
				{
					
					boolean isHas=true;
					while(isHas)
					{
						id++;
						isHas=this.getId(id, per_pointSettableName, dao);
					}
					String sid=(String)importNameMap.get(importName.toUpperCase());
					StringBuffer buf = new StringBuffer("");
					buf.append("update "+per_pointSettableName+" set parent_id="+id+" where parent_id="+sid);
					dao.update(buf.toString());
					buf.setLength(0);
					buf.append("update "+per_pointSettableName+" set child_id="+id+" where child_id="+sid);
					dao.update(buf.toString());
					buf.setLength(0);
					buf.append("update "+perPointtableName+" set pointsetid="+id+" where pointsetid="+sid);
					dao.update(buf.toString());
					buf.setLength(0);
					buf.append("update "+per_pointSettableName+" set pointsetid="+id+" where pointsetid="+sid);
					dao.update(buf.toString());
					if(isResource&&flag&&!view.isHaveResource(IResourceConstant.KH_FIELD,id+""))
					{
						view.addResourceMx(id+"", IResourceConstant.KH_FIELD);
        			}
					
				}
			}
			if(flag)
			{
				rs=dao.search("select point_id from "+perPointtableName);
				while(rs.next())
				{
					String pointid=rs.getString("point_id");
					if(isResource&&!view.isHaveResource(IResourceConstant.KH_FIELD, pointid))
					{
						view.addResourceMx(pointid, IResourceConstant.KH_FIELD);
						/****************zzk 2014/2/26 导入指标 增加用户指标资源权限********************/
						String role_id="";
						int status=view.getStatus();// 0 业务用户 1 角色  4 自助用户
						if(status==4){
							role_id=view.getDbname()+view.getA0100();
						}else{
							role_id=view.getUserName();
						}
						SysPrivBo privbo=new SysPrivBo(role_id,status+"",conn,"warnpriv");
						String res_str=privbo.getWarn_str();
						ResourceParser parser=new ResourceParser(res_str,IResourceConstant.KH_FIELD);
						parser.addContent(pointid);
						res_str=parser.outResourceContent();
						saveResourceString(dao,role_id,status+"",res_str);
					}
				}
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public boolean getId(int id,String tableName,ContentDAO dao)
	{
		boolean flag=false;
		RowSet rs= null;
		try
		{
			rs=dao.search("select pointsetid from "+tableName+" where pointsetid="+id);
			while(rs.next())
			{
				flag=true;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			if(rs!=null)
			{
				try
				{
					rs.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		
		return flag;
	}
	public void analyseSelectImportData(String selectid)
	{
		try
		{
			String[] arr = selectid.split(",");
			StringBuffer setid=new StringBuffer("");
			StringBuffer templateid=new StringBuffer("");
			StringBuffer buf_where = new StringBuffer("");
			StringBuffer array_buf = new StringBuffer("");
			for(int i=0;i<arr.length;i++)
			{
				if(arr[i]==null|| "".equals(arr[i])) {
                    continue;
                }
				String id=arr[i].split("#")[0];
				String type=arr[i].split("#")[1];//=0模板分类 =1是模板
				if("1".equals(type))
				{
					templateid.append("'"+id+"',");
					buf_where.append(" or UPPER(template_id)='"+id.toUpperCase()+"'");
					array_buf.append(id+",");
				}
				else
				{
					setid.append(id+",");
				}
			}
			
			String tempSetTableName="##per_template_set_temp";
			String tempTableName="##per_template_temp";
			if(Sql_switcher.searchDbServer() == Constant.ORACEL)
			{
				tempSetTableName="per_template_set_temp";
				tempTableName="per_template_temp";
			}
			
			HashMap tempMap=new HashMap();
			HashMap tempSetMap = new HashMap();
			/**导入规则：
			 * 1如果存在名字相同的模板分类，则保留原来的，导入文件中的不用导入
			 * 2模板按目标库顺序增加（判断目标库中，是否有使用的模板号，有的话，不导入）
			 * 3其他覆盖
			 * */
			String tt=this.autoRelevanceSetID(setid.toString(), buf_where.length()>0?buf_where.toString().substring(3):" 1=2", tempSetTableName, "template_setid", tempTableName, "template_id");
			setid.setLength(0);
			setid.append(tt);
			if(setid.toString().length()>0)
			{
				
				setid.setLength(setid.length()-1);
				ContentDAO dao = new ContentDAO(this.conn);
				HashMap map = this.getNameMap(dao);
				/**原来库中存在的模板分类名称name<--->id*/
				HashMap setNameMap = (HashMap)map.get("nameMap");
				/**原来库中存在的模板分类id，id<--->id*/
				HashMap setIdMap = (HashMap)map.get("idMap");
				/**要导入的模板分类，id<---->name*/
				HashMap tempNameMap = this.getTempNameMap(setid.toString(), tempSetTableName, dao);
				//------------------------------------------------------------------------------------------
				String[] arr_setid=setid.toString().split(",");
				int id =getMaxId("per_template_set", "template_setid");
				for(int j=0;j<arr_setid.length;j++)
				{
					/**要导入的分类*/
					String sid=arr_setid[j];
					String importName=(String)tempNameMap.get(sid.toUpperCase());
					/**模板分类存在名称相同的,则保留原来的，导入文件中的不用导入*/
					if(setNameMap.get(importName.toUpperCase())!=null)
					{
						/**库中原来的分类*/
						String yid=(String)setNameMap.get(importName.toUpperCase());
						StringBuffer sql = new StringBuffer();
						/**将临时分类表中父节点等于导入分类节点的分类的父节点，更新为名称相同的分类的*/
						sql.append("update "+tempSetTableName+" set parent_id='"+yid+"' where parent_id='"+sid+"'");
						dao.update(sql.toString());
						sql.setLength(0);
						sql.append("update "+tempSetTableName+" set child_id='"+yid+"' where child_id='"+sid+"'");
						dao.update(sql.toString());
						sql.setLength(0);
						sql.append("update "+tempTableName+" set template_setid='"+yid+"' where UPPER(template_setid)='"+sid.toUpperCase()+"'");
						dao.update(sql.toString());
					}
					/**模板分类存在id相同*/
					if(setIdMap.get(sid)!=null)
					{
						StringBuffer sql = new StringBuffer();
						sql.append("update "+tempSetTableName+" set parent_id='"+id+"' where parent_id='"+sid+"'");
						dao.update(sql.toString());
						sql.setLength(0);
						sql.append("update "+tempSetTableName+" set child_id='"+id+"' where child_id='"+sid+"'");
						dao.update(sql.toString());
						sql.setLength(0);
						sql.append("update "+tempTableName+" set template_setid='"+id+"' where UPPER(template_setid)='"+sid.toUpperCase()+"'");
						dao.update(sql.toString());
						sql.setLength(0);
						sql.append(" update "+tempSetTableName+" set template_setid="+id+" where template_setid="+sid);
						id++;
					}
				}
				if(array_buf.length()>0)
				{
					array_buf.setLength(array_buf.length()-1);
					String[] array = array_buf.toString().split(",");
					HashMap tmap = this.getTnameMap(dao);
					/**原来库中存在的模板名称name<--->id*/
					HashMap nameMap = (HashMap)tmap.get("nameMap");
					/**原来库中存在的模板id，id<--->id*/
					HashMap idMap = (HashMap)tmap.get("idMap");
					/**要导入的模板，id<---->name*/
					HashMap ttempNameMap = this.getTTempNameMap(templateid, tempSetTableName, dao);
					/**已使用的模板id<----->id*/
					HashMap allUsedTemplate=this.getAllUsedTemplate(dao);
					for(int i=0;i<array.length;i++)
					{
						String importId=array[i];
						String importName=(String)ttempNameMap.get(importId);
						/**存在名称相同的，保留原来的*/
						if(nameMap.get(importName.toUpperCase())!=null)
						{
							String yid=(String)nameMap.get(importName.toUpperCase());
							/**模板已使用*/
							if(allUsedTemplate.get(yid.toUpperCase())!=null)
							{
								
							}
							StringBuffer sql = new StringBuffer("");
							sql.append("update "+tempTableName+" set ");
						}
					}
				}
				
			}	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public ArrayList getTempTableDataMethod(String tableName,ContentDAO dao,String keyCloumn)
	{
		ArrayList list = new ArrayList();
		try
		{
			RowSet rs = dao.search("select * from "+tableName+" where 1=2");
			ResultSetMetaData rsmd=rs.getMetaData();
			for(int i=1;i<=rsmd.getColumnCount();i++)
			{
				String cloumnName=rsmd.getColumnName(i);
				list.add(cloumnName);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 取得数据库中存在的模板分类
	 * @param dao
	 * @return
	 */
	public HashMap getNameMap(ContentDAO dao)
	{
		HashMap map = new HashMap();
		try
		{
			HashMap nameMap = new HashMap();
			HashMap idMap = new HashMap();
			String sql = "select template_setid,name from per_template_set ";
			RowSet rs=dao.search(sql);
			while(rs.next())
			{
				String template_setid=rs.getString("template_setid");
				StringBuffer  buf = new StringBuffer();
				this.getParentNameLink(buf, template_setid, dao, "per_template_set", "template_setid", "parent_id", "name");
				String name=buf.toString();
				nameMap.put(name.toUpperCase(), template_setid.toUpperCase());
				idMap.put(template_setid.toUpperCase(), template_setid.toUpperCase());
			}
			map.put("nameMap",nameMap);
			map.put("idMap", idMap);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 取得数据库中存在的模板
	 * @param dao
	 * @return
	 */
	public HashMap getTnameMap(ContentDAO dao)
	{
		HashMap map = new HashMap();
		try
		{
			HashMap nameMap = new HashMap();
			HashMap idMap = new HashMap();
			String sql = "select template_id,name from per_template ";
			RowSet rs=dao.search(sql);
			while(rs.next())
			{
				String template_setid=rs.getString("template_id");
				String name=rs.getString("name");
				nameMap.put(name.toUpperCase(), template_setid.toUpperCase());
				idMap.put(template_setid.toUpperCase(), template_setid.toUpperCase());
			}
			map.put("nameMap",nameMap);
			map.put("idMap", idMap);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 要导入的模板分类
	 * @param setid
	 * @param tempTableName
	 * @param dao
	 * @return
	 */
	public HashMap getTempNameMap(String setid,String tempTableName,ContentDAO dao)
	{
		HashMap map = new HashMap();
		try
		{
			String sql = "select template_setid,name from "+tempTableName+" where template_setid in ("+setid+")";
			RowSet rs = dao.search(sql);
			while(rs.next())
			{
				String template_setid=rs.getString("template_setid");
				StringBuffer  buf = new StringBuffer();
				this.getParentNameLink(buf, template_setid, dao, tempTableName, "template_setid", "parent_id", "name");
				String name=buf.toString();
				map.put(template_setid.toUpperCase(), name.toUpperCase());
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 要导入的模板
	 * @param setid
	 * @param tempTableName
	 * @param dao
	 * @return
	 */
	public HashMap getTTempNameMap(StringBuffer setid,String tempTableName,ContentDAO dao)
	{
		HashMap map = new HashMap();
		try
		{
			if(setid.toString().length()>0) {
                setid.setLength(setid.length()-1);
            }
			String sql = "select template_id,name from "+tempTableName+" where template_id in ("+setid+")";
			RowSet rs = dao.search(sql);
			while(rs.next())
			{
				String template_setid=rs.getString("template_id");
				String name=rs.getString("name");
				map.put(template_setid.toUpperCase(), name.toUpperCase());
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 取得项目列表，如果项目为顶级项目，列出所有顶级项目，否则只列自己
	 * @param itemid
	 * @return
	 */
	public ArrayList getItemListToConfigRank(String itemid,String template_id)
	{
		ArrayList list = new ArrayList();
		try
		{
			RecordVo vo = new RecordVo("per_template_item");
			ContentDAO dao = new ContentDAO(this.conn);
			String where="";
			if("-1".equals(itemid)) {
                where=" where parent_id is null and UPPER(template_id)='"+template_id.toUpperCase()+"'";
            } else
			{
	    		vo.setInt("item_id",Integer.parseInt(itemid));
	    		vo=dao.findByPrimaryKey(vo);
	    		if(vo.getString("parent_id")==null|| "".equals(vo.getString("parent_id"))) {
                    where=" where parent_id is null and UPPER(template_id)='"+template_id.toUpperCase()+"'";
                } else {
                    where=" where parent_id=(select parent_id from per_template_item where  item_id="+itemid+" and UPPER(template_id)='"+template_id.toUpperCase()+"') and UPPER(template_id)='"+template_id.toUpperCase()+"'";
                }
			}
			RowSet rs = null;
			rs = dao.search("select * from per_template_item "+where+" order by seq");
			while(rs.next())
			{
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("itemid",rs.getString("item_id"));
				bean.set("score", rs.getString("score")==null?"0":this.myformat1.format(rs.getDouble("score")));
				double ff=rs.getDouble("rank");
				String ss=this.myformat1.format(rs.getDouble("rank")*100);
				bean.set("rank", Double.parseDouble((rs.getString("rank")==null?"0":this.myformat1.format(rs.getDouble("rank")*100)))+"");
				bean.set("itemdesc", rs.getString("itemdesc"));
				list.add(bean);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	public void  saveItemRank(ArrayList itemList)
	{
		try
		{
			ArrayList list = new ArrayList();
			BigDecimal han=new BigDecimal("100");
			for(int i=0;i<itemList.size();i++)
			{
				StringBuffer buf = new StringBuffer(" update per_template_item set");
				LazyDynaBean  bean = (LazyDynaBean)itemList.get(i);
				buf.append(" score=");
				String score=(String)bean.get("score");
				if("".equals(score)) {
                    score="0.00";
                }
				 buf.append(score);
				 buf.append(",rank=");
				 String rank=(String)bean.get("rank");
				 if("".equals(rank)) {
                     rank="0.00";
                 }
				 BigDecimal bd= new BigDecimal(rank);
				 rank=bd.divide(han).toString();
				 buf.append(rank);
				 String itemid=(String)bean.get("itemid");
				buf.append(" where item_id=");
				buf.append(itemid);
				list.add(buf.toString());	
			}
			ContentDAO dao = new ContentDAO(this.conn);
			dao.batchUpdate(list);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public void getParentNameLink(StringBuffer buf,String pointsetid,ContentDAO dao,String tableName,String setidColumn,String parentidColumn,String nameColumn)
	{
		RowSet rs = null;
		try
		{
			String sql = "select "+setidColumn+","+nameColumn+","+parentidColumn+" from "+tableName+" where "+setidColumn+"="+pointsetid;
			rs = dao.search(sql);
			while(rs.next())
			{
				String pointname = rs.getString(nameColumn);
				String parent_id=rs.getString(parentidColumn);
				if(parent_id==null|| "".equals(parent_id.trim()))
				{
					buf.append(pointname);
					return;
				}
				else
				{
					buf.append(pointname);
					this.getParentNameLink(buf, parent_id, dao, tableName,setidColumn,parentidColumn,nameColumn);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(rs!=null)
			{
				try
				{
					
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	public  String getEvlForPointHtml(String templateID,HashMap pointNumMap,HashMap scaleMap,String method,String aband,String showbenbu,HashMap benbuScale,String bodyid,ArrayList bodylist,HashMap scaleforall1,HashMap ponitforall1,String showComment,ArrayList comment,HashMap ponitMain){
		String html="";
		this.returnflag=returnflag;
		/**项目对应指标*/
		this.itemToPointMap=this.getItemToPointMap(templateID);
		/**模板对应的所有项目*/
		this.templateItemList=getTemplateItemList(templateID);
		/**得到项目中所有的叶子项目*/
		get_LeafItemList();
		/**项目的itemid对应的是该项目的所有父亲，爷爷，太爷的列表*/
		this.leafItemLinkMap=getLeafItemLinkMap();
		/**每个项目对应的叶子节点个数*/
		this.itemPointNum=getItemPointNum();
		/**项目对应的指标个数*/
		//this.itemHasFieldNum=this.getItemHasFieldCount(templateID);//各项目包含的指标个数
		/**项目id对应的指标的详细信息列表*/
		this.itemHaveFieldList=this.getItemHasFieldList();//指标信息
		/**当前模板的权重分值标识*/
		this.status=this.getTemplateStatus(templateID);
		this.isUsed=this.templateIsUsed(templateID, new ContentDAO(this.conn));
//		HashMap    subItemMap=(HashMap)list.get(3);	//各项目的子项目(hashmap)
		/**除叶子节点外的节点的指标数量*/
		this.childItemLinkMap=this.getChildItemLinkMap();
		this.doMethod2();
		html=this.writeHtml2(method, showbenbu, aband, pointNumMap, scaleMap,benbuScale,bodyid,bodylist,scaleforall1,ponitforall1,showComment,comment,ponitMain,templateID);
		return html;
	}
	public UserView getUserView() {
		return userView;
	}
	public void setUserView(UserView userView) {
		this.userView = userView;
	}
	public String writeTableLikeMoban(String templateID,HashMap pointNumMap,HashMap scaleMap,String method,String aband,String showbenbu,HashMap benbuScale,HSSFWorkbook workbook1,ArrayList bodylist,HashMap pointforall1,HashMap scaleforall1,String bodyid,String showComment,ArrayList comment,HashMap ponitMain )
	{
		String outName="";
		/**项目对应指标*/
		this.itemToPointMap=this.getItemToPointMap(templateID);
		/**模板对应的所有项目*/
		this.templateItemList=getTemplateItemList(templateID);
		/**得到项目中所有的叶子项目*/
		get_LeafItemList();
		/**项目的itemid对应的是该项目的所有父亲，爷爷，太爷的列表*/
		this.leafItemLinkMap=getLeafItemLinkMap();
		/**每个项目对应的叶子节点个数*/
		this.itemPointNum=getItemPointNum();
		/**项目id对应的指标的详细信息列表*/
		this.itemHaveFieldList=this.getItemHasFieldList();//指标信息
		/**当前模板的权重分值标识*/
		this.status=this.getTemplateStatus(templateID);
		/**当前模板的名称*/
		this.title=this.getTemplateName(templateID);
		/**当前模板的所有指标*/
		this.pointList=this.getPointList(templateID);
		/**当前模板的权重分值标识*/
		//this.status=this.getTemplateStatus(templateID);
//		HashMap    subItemMap=(HashMap)list.get(3);	//各项目的子项目(hashmap)
		/**除叶子节点外的节点的指标数量*/
		this.childItemLinkMap=this.getChildItemLinkMap();
		this.doMethod2();
		/**EXCEL工作薄*/
		this.workbook =workbook1;
		/**EXCEL页眉*/
		sheet = workbook.getSheet("Sheet0");
		/**EXCEL样式*/
		centerstyle = style(workbook, 1);
		//this.setTitle();
		this.rowNum++;
		outName=this.outExcel3( templateID, pointNumMap ,scaleMap, method, aband, showbenbu, benbuScale,pointforall1,scaleforall1,bodyid,bodylist,showComment,comment,ponitMain);
		return outName;
	}
	public String outExcel3(String templateID, //
							HashMap pointNumMap,
							HashMap scaleMap,
							String method,
							String aband,
							String showbenbu,
							HashMap benbuScale,
							HashMap pointforall1,
							HashMap scaleforall1,
							String bodyid,
							ArrayList bodylist,
							String showComment,
							ArrayList comment,
							HashMap ponitMain){
		
		
		String totalnum="";
		String outName="CurrentTemplate_"+PubFunc.getStrg()+".xls";
		try
		{
		    HashMap existWriteItem=new HashMap();
			LazyDynaBean abean=null;
			LazyDynaBean a_bean=null;
			int columnSize=0;
			ArrayList perGradeTemplateList=this.getPerGradeTemplateList(templateID);
			int num=0;
			int hang=0;
			int lie=0;//项目层数，zhaoxg add 2014-11-27
			int title=1;
			if(method!=null&& "2".equalsIgnoreCase(method)){
				hang=0;
				if(aband!=null&& "1".equalsIgnoreCase(aband)){
					num=(perGradeTemplateList.size()+1)*2;
				}
				title=2;
			}else{
				hang=1;
				num=this.lay+perGradeTemplateList.size()*2;
				title=1;
			}
			
			 //输出表头
			executeCell(this.rowNum,this.colIndex,this.rowNum+title,Short.parseShort(String.valueOf(this.lay-1)),"考核内容",centerstyle);
			this.colIndex+=Short.parseShort(String.valueOf(this.lay));
			executeCell(this.rowNum,this.colIndex,this.rowNum+title,this.colIndex,"指标名称",centerstyle);
			this.colIndex++;
			executeCell(this.rowNum,this.colIndex,this.rowNum+title,this.colIndex,"指标解释",centerstyle);
			this.colIndex++;
			int totalvv=0;
			if(method!=null&& "2".equalsIgnoreCase(method)){
			
				if(bodyid!=null&&!"all".equalsIgnoreCase(bodyid.trim())){//按主体分类统计 和所有主体分类统计
					for(int i=0;i<bodylist.size();i++){
						
						CommonData cd=(CommonData)bodylist.get(i);
						String bdy=cd.getDataValue();
						String name=cd.getDataName();
						String privMainNum="";
						
						if("all1".equalsIgnoreCase(bodyid)){// 所有主体分类统计
							if("all".equalsIgnoreCase(bdy)|| "all1".equalsIgnoreCase(bdy)){
								continue;
							}
							privMainNum=(String)ponitMain.get(bdy);
							totalvv+=Integer.parseInt(privMainNum);
						}else{
							if(!bdy.equalsIgnoreCase(bodyid)){//按某个考核主体统计
								continue;
							}
						}
						short mm=Short.parseShort(String.valueOf((int)this.colIndex+(perGradeTemplateList.size())*2+1));
						if(aband!=null&& "1".equalsIgnoreCase(aband.trim())){
							if("all1".equalsIgnoreCase(bodyid.trim())){
								mm=Short.parseShort(String.valueOf((int)this.colIndex+(perGradeTemplateList.size()+1)*2+1));
							}
						}
						if("".equals(privMainNum)) {
                            executeCell(this.rowNum,this.colIndex,this.rowNum,mm,name+"票数及比例",centerstyle);
                        } else {
                            executeCell(this.rowNum,this.colIndex,this.rowNum,mm,name+"票数("+privMainNum+")及比例",centerstyle);
                        }
						for(int tt=0;tt<perGradeTemplateList.size();tt++){
							short kk=Short.parseShort(String.valueOf((int)this.colIndex+1));
							short kk1=Short.parseShort(String.valueOf((int)this.colIndex+1));
							LazyDynaBean bean=(LazyDynaBean)perGradeTemplateList.get(tt);
							String desc=(String)bean.get("gradedesc");
							executeCell(this.rowNum+1,this.colIndex,this.rowNum+1,kk,desc,centerstyle);
							executeCell(this.rowNum+2,this.colIndex,this.rowNum+2,this.colIndex,"票数",centerstyle);
							this.colIndex++;
							kk1=Short.parseShort(String.valueOf((int)this.colIndex+1));
							executeCell(this.rowNum+2,this.colIndex,this.rowNum+2,this.colIndex,"比例",centerstyle);
							this.colIndex++;
						}
						if(aband!=null&& "1".equalsIgnoreCase(aband.trim())){// 含弃权
							short kk=Short.parseShort(String.valueOf((int)this.colIndex+1));
							short kk1=Short.parseShort(String.valueOf((int)this.colIndex+2));
							if("all1".equalsIgnoreCase(bodyid)){
								executeCell(this.rowNum+1,this.colIndex,this.rowNum+1,(short)(this.colIndex+1),"总投票数",centerstyle);
								executeCell(this.rowNum+2,this.colIndex,this.rowNum+2,this.colIndex,"票数",centerstyle);
								this.colIndex++;
								executeCell(this.rowNum+2,this.colIndex,this.rowNum+2,this.colIndex,"比例",centerstyle);
								this.colIndex++;
							}
							executeCell(this.rowNum+1,this.colIndex,this.rowNum+1,(short)(this.colIndex+1),"弃权",centerstyle);
							executeCell(this.rowNum+2,this.colIndex,this.rowNum+2,this.colIndex,"票数",centerstyle);
							this.colIndex++;
							executeCell(this.rowNum+2,this.colIndex,this.rowNum+2,this.colIndex,"比例",centerstyle);
							this.colIndex++;
						}
					}
					
					if("all1".equalsIgnoreCase(bodyid)){// 所有主体分类统计
						short mm=Short.parseShort(String.valueOf((int)this.colIndex+(perGradeTemplateList.size()-1)*2+1));
						if(aband!=null&& "1".equalsIgnoreCase(aband.trim())){
							if("all1".equalsIgnoreCase(bodyid.trim())){
								mm=Short.parseShort(String.valueOf((int)this.colIndex+(perGradeTemplateList.size()+1)*2+1));
							}
						}
						executeCell(this.rowNum,this.colIndex,this.rowNum,mm,"合计票数("+totalvv+")及比例",centerstyle);
						for(int tt=0;tt<perGradeTemplateList.size();tt++){
							short kk=Short.parseShort(String.valueOf((int)this.colIndex+1));
							short kk1=Short.parseShort(String.valueOf((int)this.colIndex+1));
							LazyDynaBean bean=(LazyDynaBean)perGradeTemplateList.get(tt);
							String desc=(String)bean.get("gradedesc");
							executeCell(this.rowNum+1,this.colIndex,this.rowNum+1,kk,desc,centerstyle);
							executeCell(this.rowNum+2,this.colIndex,this.rowNum+2,this.colIndex,"票数",centerstyle);
							this.colIndex++;
							kk1=Short.parseShort(String.valueOf((int)this.colIndex+1));
							executeCell(this.rowNum+2,this.colIndex,this.rowNum+2,this.colIndex,"比例",centerstyle);
							this.colIndex++;
						}
						if(aband!=null&& "1".equalsIgnoreCase(aband.trim())){
							short kk=Short.parseShort(String.valueOf((int)this.colIndex+1));
							short kk1=Short.parseShort(String.valueOf((int)this.colIndex+2));
							executeCell(this.rowNum+1,this.colIndex,this.rowNum+1,(short)(this.colIndex+1),"总投票数",centerstyle);
							executeCell(this.rowNum+2,this.colIndex,this.rowNum+2,this.colIndex,"票数",centerstyle);
							this.colIndex++;
							executeCell(this.rowNum+2,this.colIndex,this.rowNum+2,this.colIndex,"比例",centerstyle);
							this.colIndex++;
							executeCell(this.rowNum+1,this.colIndex,this.rowNum+1,(short)(this.colIndex+1),"弃权",centerstyle);
							executeCell(this.rowNum+2,this.colIndex,this.rowNum+2,this.colIndex,"票数",centerstyle);
							this.colIndex++;
							executeCell(this.rowNum+2,this.colIndex,this.rowNum+2,this.colIndex,"比例",centerstyle);
							this.colIndex++;
						}
					}
				}else{// 所有主体 不分类统计
					short mm=Short.parseShort(String.valueOf((int)this.colIndex+(perGradeTemplateList.size()-1)*2+1));
					if(aband!=null&& "1".equalsIgnoreCase(aband.trim())){
						mm=Short.parseShort(String.valueOf((int)this.colIndex+(perGradeTemplateList.size())*2+1));
					}
					executeCell(this.rowNum,this.colIndex,this.rowNum,mm,"票数及比例",centerstyle);
					for(int tt=0;tt<perGradeTemplateList.size();tt++){
						short kk=Short.parseShort(String.valueOf((int)this.colIndex+1));
						short kk1=Short.parseShort(String.valueOf((int)this.colIndex+1));
						LazyDynaBean bean=(LazyDynaBean)perGradeTemplateList.get(tt);
						String desc=(String)bean.get("gradedesc");
						executeCell(this.rowNum+1,this.colIndex,this.rowNum+1,kk,desc,centerstyle);
						executeCell(this.rowNum+2,this.colIndex,this.rowNum+2,this.colIndex,"票数",centerstyle);
						this.colIndex++;
						kk1=Short.parseShort(String.valueOf((int)this.colIndex+1));
						executeCell(this.rowNum+2,this.colIndex,this.rowNum+2,this.colIndex,"比例",centerstyle);
						this.colIndex++;
					}
					if(aband!=null&& "1".equalsIgnoreCase(aband.trim())){
						short kk=Short.parseShort(String.valueOf((int)this.colIndex+1));
						short kk1=Short.parseShort(String.valueOf((int)this.colIndex+2));
						executeCell(this.rowNum+1,this.colIndex,this.rowNum+1,(short)(this.colIndex+1),"弃权",centerstyle);
						executeCell(this.rowNum+2,this.colIndex,this.rowNum+2,this.colIndex,"票数",centerstyle);
						this.colIndex++;
						executeCell(this.rowNum+2,this.colIndex,this.rowNum+2,this.colIndex,"比例",centerstyle);
						this.colIndex++;
					}
				}
				
			}else{
//				short mm=Short.parseShort(String.valueOf((int)this.colIndex+perGradeTemplateList.size()-1));
//				if(aband!=null&&aband.trim().equalsIgnoreCase("1")){
//					mm=Short.parseShort(String.valueOf((int)this.colIndex+perGradeTemplateList.size()));
//				}
				if(bodyid!=null&&!"all".equalsIgnoreCase(bodyid.trim())){
					for(int i=0;i<bodylist.size();i++){
						CommonData cd=(CommonData)bodylist.get(i);
						String bdy=cd.getDataValue();
						String name=cd.getDataName();
						String privMainNum="";
						if("all1".equalsIgnoreCase(bodyid)){// 所有主体分类统计
							if("all".equalsIgnoreCase(bdy)|| "all1".equalsIgnoreCase(bdy)){
								continue;
							}
							privMainNum=(String)ponitMain.get(bdy);
							totalvv+=Integer.parseInt(privMainNum);
						}else{
							if(!bdy.equalsIgnoreCase(bodyid)){//按某个考核主体统计
								continue;
							}
						}
						short mm=Short.parseShort(String.valueOf((int)this.colIndex+(perGradeTemplateList.size()-1)));
						if(aband!=null&& "1".equalsIgnoreCase(aband.trim())){
							mm=Short.parseShort(String.valueOf((int)this.colIndex+(perGradeTemplateList.size())));
						}
						if("".equals(privMainNum)) {
                            executeCell(this.rowNum,this.colIndex,this.rowNum,mm,name+"票数及比例",centerstyle);
                        } else {
                            executeCell(this.rowNum,this.colIndex,this.rowNum,mm,name+"票数("+privMainNum+")及比例",centerstyle);
                        }
						for(int tt=0;tt<perGradeTemplateList.size();tt++){
							LazyDynaBean bean=(LazyDynaBean)perGradeTemplateList.get(tt);
							String desc=(String)bean.get("gradedesc");
							executeCell(this.rowNum+1,this.colIndex,this.rowNum+1,this.colIndex,desc,centerstyle);
							this.colIndex++;
						}
						//this.colIndex++;
						if(aband!=null&& "1".equalsIgnoreCase(aband.trim())){
							executeCell(this.rowNum+1,this.colIndex,this.rowNum+1,this.colIndex,"弃权",centerstyle);
							this.colIndex++;
						}
					}
					if("all1".equalsIgnoreCase(bodyid)){// 所有主体分类统计
						short mm=Short.parseShort(String.valueOf((int)this.colIndex+(perGradeTemplateList.size()-1)));
						if(aband!=null&& "1".equalsIgnoreCase(aband.trim())){
							mm=Short.parseShort(String.valueOf((int)this.colIndex+(perGradeTemplateList.size())*2+1));
						}
						executeCell(this.rowNum,this.colIndex,this.rowNum,mm,"总计票数("+totalvv+")及比例",centerstyle);
						for(int tt=0;tt<perGradeTemplateList.size();tt++){
							LazyDynaBean bean=(LazyDynaBean)perGradeTemplateList.get(tt);
							String desc=(String)bean.get("gradedesc");
							executeCell(this.rowNum+1,this.colIndex,this.rowNum+1,this.colIndex,desc,centerstyle);
							this.colIndex++;
						}
						if(aband!=null&& "1".equalsIgnoreCase(aband.trim())){
							executeCell(this.rowNum+1,this.colIndex,this.rowNum+1,this.colIndex,"弃权",centerstyle);
							this.colIndex++;
						}
					}
				}else{
					short mm=Short.parseShort(String.valueOf((int)this.colIndex+(perGradeTemplateList.size()-1)));
					if(aband!=null&& "1".equalsIgnoreCase(aband.trim())){
						mm=Short.parseShort(String.valueOf((int)this.colIndex+(perGradeTemplateList.size())));
					}
					executeCell(this.rowNum,this.colIndex,this.rowNum,mm,"票数及比例",centerstyle);
					for(int tt=0;tt<perGradeTemplateList.size();tt++){
						LazyDynaBean bean=(LazyDynaBean)perGradeTemplateList.get(tt);
						String desc=(String)bean.get("gradedesc");
						executeCell(this.rowNum+1,this.colIndex,this.rowNum+1,this.colIndex,desc,centerstyle);
						this.colIndex++;
					}
					if(aband!=null&& "1".equalsIgnoreCase(aband.trim())){
						executeCell(this.rowNum+1,this.colIndex,this.rowNum+1,this.colIndex,"弃权",centerstyle);
						this.colIndex++;
					}
				}
			}
			if(showbenbu!=null&&showbenbu.trim().length()!=0&& "1".equalsIgnoreCase(showbenbu)){
				short mm=Short.parseShort(String.valueOf((int)this.colIndex+perGradeTemplateList.size()-1));
				executeCell(this.rowNum,this.colIndex,this.rowNum,mm,"本部平均",centerstyle);
				for(int tt=0;tt<perGradeTemplateList.size();tt++){
					LazyDynaBean bean=(LazyDynaBean)perGradeTemplateList.get(tt);
					String desc=(String)bean.get("gradedesc");
					if(method==null|| "0".equalsIgnoreCase(method)||method.trim().length()==0){
						executeCell(this.rowNum+1,this.colIndex,this.rowNum+1,this.colIndex,desc,centerstyle);		
					}else{
						executeCell(this.rowNum+1,this.colIndex,this.rowNum+2,this.colIndex,desc,centerstyle);	
					}
					this.colIndex++;

				}
			}
//			if(this.status.equals("1"))
//	        {
//     			executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,"权重",centerstyle);
//	        }
			
			this.rowNum=this.rowNum+title;
     		columnSize=this.colIndex;
			
			int rowNum=0;
			for(int i=0;i<this.leafItemList.size();i++)
			{
				abean=(LazyDynaBean)this.leafItemList.get(i);
				String item_id=(String)abean.get("item_id");
				/**叶子项目对应的指标数*/
				//int num=((Integer)this.itemPointNum.get(item_id)).intValue();
				//ArrayList pointList=(ArrayList)this.itemToPointMap.get(item_id);
			//	ArrayList pointList = (ArrayList)this.itemHaveFieldList.get(item_id);
				//String item_kind=(String)abean.get("kind");
				/**叶子项目对应的指标数*/
			/*	for(int j=0;j<num;j++)
				{*/
			    	if(i==0) {
                        this.rowNum++;
                    }
					this.colIndex=0;
					rowNum++;
					ArrayList linkParentList=(ArrayList)this.leafItemLinkMap.get(item_id);
					int current=linkParentList.size();
					if(current==1)
					{
						if(existWriteItem.get(item_id)!=null)
			    		{
			    			this.colIndex++;
		    				continue;
		    			}
		    			existWriteItem.put(item_id,"1");
		    			String itemdesc=(String)abean.get("itemdesc");
		    			/**画出一个父项目*/
		    			int rowspan=((itemPointNum.get(item_id)==null?0:((Integer)itemPointNum.get(item_id)).intValue())+(childItemLinkMap.get(item_id)==null?0:((Integer)childItemLinkMap.get(item_id)).intValue()));//所占行数
		    			
		    			if(method!=null&& "2".equalsIgnoreCase(method)){
		    				executeCell(this.rowNum,this.colIndex,this.rowNum+(rowspan-1)*(hang+1),this.colIndex,itemdesc,centerstyle);// 如果纵向显示则行数乘2
		    			}else{
		    				executeCell(this.rowNum,this.colIndex,this.rowNum+(rowspan-1)*(hang+1)+1,this.colIndex,itemdesc,centerstyle);// 如果纵向显示则行数乘2
		    			}
		    			this.colIndex++;
		    			/**父项目包含指标，画出指标*/
		    			/**该项目的层数*/
		    			int layer=Integer.parseInt((String)layMap.get(item_id));
		    			if(layer>1&&layer>lie){
		    				lie=layer-1;
		    			}
		    			/**对应指标列表*/
			    		ArrayList fieldlistp = (ArrayList)this.itemHaveFieldList.get(item_id);
			    		/**该项目有指标*/
			    		if(fieldlistp!=null&&fieldlistp.size()>0)
			    		{			    			
			    			for(int h=0;h<fieldlistp.size();h++)
			    			{
				    			LazyDynaBean pointbean=(LazyDynaBean)fieldlistp.get(h);
				    			String pointid=(String)pointbean.get("point_id");
				    			int k=0;
			    				for(int f=0;f<this.lay-layer;f++)
			    				{
			    					executeCell(this.rowNum,(short)(colIndex+f),this.rowNum+hang,(short)((colIndex+f)),"",centerstyle);
			    					k++;
			    				}
			    				/**指标名称*/
			    				executeCell(this.rowNum,(short)(colIndex+k),this.rowNum+hang,(short)(colIndex+k),(String)pointbean.get("name"),centerstyle);
			    				k++;
		    					/**分值*/
		    					/**指标解释*/
		    					executeCell(this.rowNum,(short)(colIndex+k),this.rowNum+hang,(short)(colIndex+k),(String)pointbean.get("description"),centerstyle);
		    					k++;
		    					//画指标票数及占比	
		    					if(bodyid!=null&&!"all".equalsIgnoreCase(bodyid)){
		    						LazyDynaBean bbscale =(LazyDynaBean)benbuScale.get(pointid);
				    				HashMap bodyGrade=(HashMap)pointNumMap.get(pointid);//主体类别对应指标度票数
				    				HashMap bodyscale=(HashMap)scaleMap.get(pointid);//主体类别对应指标度票数
				    				for(int tem=0;tem<bodylist.size();tem++){
				    					CommonData cd=(CommonData)bodylist.get(tem);
				    					String name=cd.getDataName();
				    					String bdyid=cd.getDataValue();
				    					if(bodyid!=null&& "all1".equalsIgnoreCase(bodyid)){
				    						if("all".equalsIgnoreCase(bdyid)|| "all1".equalsIgnoreCase(bdyid)){
				    							continue;
				    						}else{
				    							
				    						}
				    					}else{
				    						if(bdyid.equalsIgnoreCase(bodyid)){
			    								
			    							}else{
			    								continue;
			    							}
				    					}
				    					 LazyDynaBean ticket=(LazyDynaBean)bodyGrade.get(bdyid);// 基本标度值
										 LazyDynaBean scale =(LazyDynaBean)bodyscale.get(bdyid);
										 for(int t=0;t<perGradeTemplateList.size();t++){
												LazyDynaBean grade=(LazyDynaBean)perGradeTemplateList.get(t);
												String gradeid=(String)grade.get("grade_template_id");
												String ticvalue=(String)ticket.get(gradeid);
												String scalevalue=(String)scale.get(gradeid);
												executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),ticvalue,centerstyle);
												if(method==null|| "0".equalsIgnoreCase(method)||method.trim().length()==0){//纵向
													executeCell(this.rowNum+1,(short)(colIndex+k),this.rowNum+1,(short)(colIndex+k),scalevalue+"%",centerstyle);
												}else{
													k++;
													executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),scalevalue+"%",centerstyle);												
												}
												k++;
										}
											if(aband!=null&& "1".equalsIgnoreCase(aband.trim())){
												 String aband2=(String)ticket.get("aband");
												 String aband1=(String)scale.get("aband");
												 String total=(String)ticket.get("total");
												 String total1=(String)scale.get("total");
												 if("all1".equals(bodyid)){
													 executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),total,centerstyle);
													 if(method==null|| "0".equalsIgnoreCase(method)||method.trim().length()==0){//纵向
														executeCell(this.rowNum+1,(short)(colIndex+k),this.rowNum+1,(short)(colIndex+k),total1+"%",centerstyle);
													 }else{
														k++;
														executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),total1+"%",centerstyle);												
													 }
													 k++;
												 }
												 executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),aband2,centerstyle);
												 if(method==null|| "0".equalsIgnoreCase(method)||method.trim().length()==0){//纵向
													executeCell(this.rowNum+1,(short)(colIndex+k),this.rowNum+1,(short)(colIndex+k),aband1+"%",centerstyle);
												 }else{
													k++;
													executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),aband1+"%",centerstyle);												
												 }
												 k++;
											}
				    				}
				    				if("all1".equalsIgnoreCase(bodyid)){
					    				LazyDynaBean ticket=(LazyDynaBean)pointforall1.get(pointid);
										LazyDynaBean scale =(LazyDynaBean)scaleforall1.get(pointid);											
										if(ticket!=null){
											for(int t=0;t<perGradeTemplateList.size();t++){
												LazyDynaBean grade=(LazyDynaBean)perGradeTemplateList.get(t);
												String gradeid=(String)grade.get("grade_template_id");
												String ticvalue=(String)ticket.get(gradeid);
												String scalevalue=(String)scale.get(gradeid);
												executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),ticvalue,centerstyle);
												if(method==null|| "0".equalsIgnoreCase(method)||method.trim().length()==0){//纵向
													executeCell(this.rowNum+1,(short)(colIndex+k),this.rowNum+1,(short)(colIndex+k),scalevalue+"%",centerstyle);
												}else{
													k++;
													executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),scalevalue+"%",centerstyle);												
												}
												k++;
											}
											if(aband!=null&& "1".equalsIgnoreCase(aband.trim())){
												 String aband2=(String)ticket.get("aband");
												 String aband1=(String)scale.get("aband");
												 String total=(String)ticket.get("total");
												 String total1=(String)scale.get("total");
												 if("all1".equals(bodyid)){
													 executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),total,centerstyle);
													 if(method==null|| "0".equalsIgnoreCase(method)||method.trim().length()==0){//纵向
														executeCell(this.rowNum+1,(short)(colIndex+k),this.rowNum+1,(short)(colIndex+k),total1+"%",centerstyle);
													 }else{
														k++;
														executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),total1+"%",centerstyle);												
													 }
													 k++;
												 }
												 executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),aband2,centerstyle);
												 if(method==null|| "0".equalsIgnoreCase(method)||method.trim().length()==0){//纵向
													executeCell(this.rowNum+1,(short)(colIndex+k),this.rowNum+1,(short)(colIndex+k),aband1+"%",centerstyle);
												 }else{
													k++;
													executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),aband1+"%",centerstyle);												
												 }
												 k++;
											}											
										}
				    				}
		    					}else{
		    						HashMap bodyGrade=(HashMap)pointNumMap.get("all");//主体类别对应指标度票数
				    				HashMap bodyscale=(HashMap)scaleMap.get("all");//主体类别对应指标度票数
		    						LazyDynaBean ticket=(LazyDynaBean)pointNumMap.get(pointid);
									LazyDynaBean scale =(LazyDynaBean)scaleMap.get(pointid);
									
									if(ticket!=null){
										for(int t=0;t<perGradeTemplateList.size();t++){
											LazyDynaBean grade=(LazyDynaBean)perGradeTemplateList.get(t);
											String gradeid=(String)grade.get("grade_template_id");
											String ticvalue=(String)ticket.get(gradeid);
											String scalevalue=(String)scale.get(gradeid);
											executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),ticvalue,centerstyle);
											if(method==null|| "0".equalsIgnoreCase(method)||method.trim().length()==0){//纵向
												executeCell(this.rowNum+1,(short)(colIndex+k),this.rowNum+1,(short)(colIndex+k),scalevalue+"%",centerstyle);
											}else{
												k++;
												executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),scalevalue+"%",centerstyle);												
											}
											k++;
										}
										if(aband!=null&& "1".equalsIgnoreCase(aband.trim())){
											 String aband2=(String)ticket.get("aband");
											 String aband1=(String)scale.get("aband");
											 executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),aband2,centerstyle);
											 if(method==null|| "0".equalsIgnoreCase(method)||method.trim().length()==0){//纵向
												executeCell(this.rowNum+1,(short)(colIndex+k),this.rowNum+1,(short)(colIndex+k),aband1+"%",centerstyle);
											 }else{
												k++;
												executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),aband1+"%",centerstyle);												
											 }
											 k++;
										}
										
									}
		    					}
		    					if(showbenbu!=null&&showbenbu.trim().length()!=0&& "1".equalsIgnoreCase(showbenbu)){//显示本部平均，本部平均不用显示占比所以占用两行
		    						LazyDynaBean bbscale =(LazyDynaBean)benbuScale.get(pointid);
									 for(int t=0;t<perGradeTemplateList.size();t++){
										 LazyDynaBean grade=(LazyDynaBean)perGradeTemplateList.get(t);
										 String gradeid=(String)grade.get("grade_template_id");
										 String bbsvalue=(String)bbscale.get(gradeid);
										 if(method==null|| "0".equalsIgnoreCase(method)||method.trim().length()==0){//纵向
											 executeCell(this.rowNum,(short)(colIndex+k),this.rowNum+1,(short)(colIndex+k),bbsvalue+"%",centerstyle);
										 }else{
											 executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),bbsvalue+"%",centerstyle);
										 }
										 k++;
									 }
								 }
								totalnum=String.valueOf((int)this.colIndex+k-1);
								this.rowNum=this.rowNum+hang+1;
	    					}
		    			}
			    		/**没有指标**/
		    			else
		    			{
		    				/**画出空格*/
		    				int k=0;
		    				for(int f=0;f<this.lay-layer;f++)
		    				{
		    					executeCell(this.rowNum,(short)(colIndex+f),this.rowNum+hang,(short)((colIndex+f)),"",centerstyle);
		    				    k++;
		    				}
		    				executeCell(this.rowNum,(short)(colIndex+k),this.rowNum+hang,(short)(colIndex+k),"",centerstyle);
		    				for(int t=0;t<perGradeTemplateList.size();t++){
		    					executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),"",centerstyle);
		    					 if(method==null|| "0".equalsIgnoreCase(method)||method.trim().length()==0){//纵向
		    						 executeCell(this.rowNum,(short)(colIndex+k),this.rowNum+1,(short)(colIndex+k),"",centerstyle);
		    					 }else{
		    						 k++;
		    						 executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),"",centerstyle);
		    					 }
		    					 k++;
		    				}
		    				if(showbenbu!=null&&showbenbu.trim().length()!=0&& "1".equalsIgnoreCase(showbenbu)){
		    					 if(method==null|| "0".equalsIgnoreCase(method)||method.trim().length()==0){//纵向
		    						 executeCell(this.rowNum,(short)(colIndex+k),this.rowNum+1,(short)(colIndex+k),"",centerstyle);
		    					 }else{
		    						 executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),"",centerstyle);
		    					 }
		    					 
		    					 k++;
		    				}
	    					this.rowNum=this.rowNum+hang+1;
    					}	
					}
					else
					{
			    		/**叶子项目的所有父项目列表（爷爷，太爷）*/
			    		for(int e=linkParentList.size()-1;e>=0;e--)
			    		{
				    		a_bean=(LazyDynaBean)linkParentList.get(e);
				    		String itemid=(String)a_bean.get("item_id");
			    			if(existWriteItem.get(itemid)!=null)
				    		{
				    			this.colIndex++;				    			
			    				continue;
			    			}
			    			existWriteItem.put(itemid,"1");
			    			String itemdesc=(String)a_bean.get("itemdesc");
			    			/**画出一个父项目*/
			    			int colspan=((itemPointNum.get(itemid)==null?0:((Integer)itemPointNum.get(itemid)).intValue())+(childItemLinkMap.get(itemid)==null?0:((Integer)childItemLinkMap.get(itemid)).intValue()));	
			        		//this.rowNum++;
			    			executeCell(this.rowNum,this.colIndex,this.rowNum+colspan*(hang+1)-1,this.colIndex,itemdesc,centerstyle);
			    			this.colIndex++;
			    			
			    			totalnum=String.valueOf((int)this.colIndex);
			    			/**父项目包含指标，画出指标*/
			    			/**该项目的层数*/
			    			int layer=Integer.parseInt((String)layMap.get(itemid));
			    			if(layer>1&&layer>lie){
			    				lie=layer-1;
			    			}
			    			/**对应指标列表*/
				    		ArrayList fieldlistp = (ArrayList)this.itemHaveFieldList.get(itemid);
				    		/**该项目有指标*/
				    		if(fieldlistp!=null&&fieldlistp.size()>0)
				    		{  
				    			for(int h=0;h<fieldlistp.size();h++)
				    			{
				    				LazyDynaBean pointbean=(LazyDynaBean)fieldlistp.get(h);
				    				String pointid=(String)pointbean.get("point_id");
					    			int k=0;
				    				for(int f=0;f<this.lay-layer;f++)
				    				{
				    					executeCell(this.rowNum,(short)(colIndex+f),this.rowNum+hang,(short)((colIndex+f)),"",centerstyle);
				    					k++;				    					
				    				}
				    				/**指标名称*/
				    				executeCell(this.rowNum,(short)(colIndex+k),this.rowNum+hang,(short)(colIndex+k),(String)pointbean.get("name"),centerstyle);
				    				k++;
			    					/**指标解释*/
			    					executeCell(this.rowNum,(short)(colIndex+k),this.rowNum+hang,(short)(colIndex+k),(String)pointbean.get("description"),centerstyle);
			    					k++;
			    					//画指标票数及占比		    					
			    					if(bodyid!=null&&!"all".equalsIgnoreCase(bodyid)){
			    						LazyDynaBean bbscale =(LazyDynaBean)benbuScale.get(pointid);
					    				HashMap bodyGrade=(HashMap)pointNumMap.get(pointid);//主体类别对应指标度票数
					    				HashMap bodyscale=(HashMap)scaleMap.get(pointid);//主体类别对应指标度票数
					    				for(int tem=0;tem<bodylist.size();tem++){
					    					CommonData cd=(CommonData)bodylist.get(tem);
					    					String name=cd.getDataName();
					    					String bdyid=cd.getDataValue();
					    						if("all".equals(bdyid)){
					    							continue;
					    						}else{
					    							if(bodyid.equalsIgnoreCase(bdyid)|| "all1".equalsIgnoreCase(bodyid)){//所有主体（分类统计） 或者 跟所选主体相同的  不卡住  zhaoxg add 2014-10-30
					    								
					    							}else{
					    								continue;
					    							}
					    						}

					    					
					    					 LazyDynaBean ticket=(LazyDynaBean)bodyGrade.get(bdyid);// 基本标度值
											 LazyDynaBean scale =(LazyDynaBean)bodyscale.get(bdyid);
											 for(int t=0;t<perGradeTemplateList.size();t++){
													LazyDynaBean grade=(LazyDynaBean)perGradeTemplateList.get(t);
													String gradeid=(String)grade.get("grade_template_id");
													String ticvalue=(String)ticket.get(gradeid);
													String scalevalue=(String)scale.get(gradeid);
													executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),ticvalue,centerstyle);
													if(method==null|| "0".equalsIgnoreCase(method)||method.trim().length()==0){//纵向
														executeCell(this.rowNum+1,(short)(colIndex+k),this.rowNum+1,(short)(colIndex+k),scalevalue+"%",centerstyle);
													}else{
														k++;
														executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),scalevalue+"%",centerstyle);												
													}
													k++;
											}
												if(aband!=null&& "1".equalsIgnoreCase(aband.trim())){
													 String aband2=(String)ticket.get("aband");
													 String aband1=(String)scale.get("aband");
													 String total=(String)ticket.get("total");
													 String total1=(String)scale.get("total");
													 if("all1".equals(bodyid)){
														 executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),total,centerstyle);
														 if(method==null|| "0".equalsIgnoreCase(method)||method.trim().length()==0){//纵向
															executeCell(this.rowNum+1,(short)(colIndex+k),this.rowNum+1,(short)(colIndex+k),total1+"%",centerstyle);
														 }else{
															k++;
															executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),total1+"%",centerstyle);												
														 }
														 k++;
													 }
													 executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),aband2,centerstyle);
													 if(method==null|| "0".equalsIgnoreCase(method)||method.trim().length()==0){//纵向
														executeCell(this.rowNum+1,(short)(colIndex+k),this.rowNum+1,(short)(colIndex+k),aband1+"%",centerstyle);
													 }else{
														k++;
														executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),aband1+"%",centerstyle);												
													 }
													 k++;
												}
					    				}
					    				if("all1".equalsIgnoreCase(bodyid)){
						    				LazyDynaBean ticket=(LazyDynaBean)pointforall1.get(pointid);
											LazyDynaBean scale =(LazyDynaBean)scaleforall1.get(pointid);											
											if(ticket!=null){
												for(int t=0;t<perGradeTemplateList.size();t++){
													LazyDynaBean grade=(LazyDynaBean)perGradeTemplateList.get(t);
													String gradeid=(String)grade.get("grade_template_id");
													String ticvalue=(String)ticket.get(gradeid);
													String scalevalue=(String)scale.get(gradeid);
													executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),ticvalue,centerstyle);
													if(method==null|| "0".equalsIgnoreCase(method)||method.trim().length()==0){//纵向
														executeCell(this.rowNum+1,(short)(colIndex+k),this.rowNum+1,(short)(colIndex+k),scalevalue+"%",centerstyle);
													}else{
														k++;
														executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),scalevalue+"%",centerstyle);												
													}
													k++;
												}
												if(aband!=null&& "1".equalsIgnoreCase(aband.trim())){
													 String aband2=(String)ticket.get("aband");
													 String aband1=(String)scale.get("aband");
													 String total=(String)ticket.get("total");
													 String total1=(String)scale.get("total");
													 if("all1".equals(bodyid)){
														 executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),total,centerstyle);
														 if(method==null|| "0".equalsIgnoreCase(method)||method.trim().length()==0){//纵向
															executeCell(this.rowNum+1,(short)(colIndex+k),this.rowNum+1,(short)(colIndex+k),total1+"%",centerstyle);
														 }else{
															k++;
															executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),total1+"%",centerstyle);												
														 }
														 k++;
													 }
													 executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),aband2,centerstyle);
													 if(method==null|| "0".equalsIgnoreCase(method)||method.trim().length()==0){//纵向
														executeCell(this.rowNum+1,(short)(colIndex+k),this.rowNum+1,(short)(colIndex+k),aband1+"%",centerstyle);
													 }else{
														k++;
														executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),aband1+"%",centerstyle);												
													 }
													 k++;
												}											
											}
					    				}
			    					}else{
			    						LazyDynaBean ticket=(LazyDynaBean)pointNumMap.get(pointid);
										LazyDynaBean scale =(LazyDynaBean)scaleMap.get(pointid);
										
										if(ticket!=null){
											for(int t=0;t<perGradeTemplateList.size();t++){
												LazyDynaBean grade=(LazyDynaBean)perGradeTemplateList.get(t);
												String gradeid=(String)grade.get("grade_template_id");
												String ticvalue=(String)ticket.get(gradeid);
												String scalevalue=(String)scale.get(gradeid);
												executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),ticvalue,centerstyle);
												if(method==null|| "0".equalsIgnoreCase(method)||method.trim().length()==0){//纵向
													executeCell(this.rowNum+1,(short)(colIndex+k),this.rowNum+1,(short)(colIndex+k),scalevalue+"%",centerstyle);
												}else{
													k++;
													executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),scalevalue+"%",centerstyle);												
												}
												k++;
											}
											if(aband!=null&& "1".equalsIgnoreCase(aband.trim())){
												 String aband2=(String)ticket.get("aband");
												 String aband1=(String)scale.get("aband");
												 executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),aband2,centerstyle);
												 if(method==null|| "0".equalsIgnoreCase(method)||method.trim().length()==0){//纵向
													executeCell(this.rowNum+1,(short)(colIndex+k),this.rowNum+1,(short)(colIndex+k),aband1+"%",centerstyle);
												 }else{
													k++;
													executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),aband1+"%",centerstyle);												
												 }
												 k++;
											}											
										}
			    					}
			    					if(showbenbu!=null&&showbenbu.trim().length()!=0&& "1".equalsIgnoreCase(showbenbu)){//显示本部平均，本部平均不用显示占比所以占用两行
			    						LazyDynaBean bbscale =(LazyDynaBean)benbuScale.get(pointid);
										 for(int t=0;t<perGradeTemplateList.size();t++){
											 LazyDynaBean grade=(LazyDynaBean)perGradeTemplateList.get(t);
											 String gradeid=(String)grade.get("grade_template_id");
											 String bbsvalue=(String)bbscale.get(gradeid);
											 if(method==null|| "0".equalsIgnoreCase(method)||method.trim().length()==0){//纵向
												 executeCell(this.rowNum,(short)(colIndex+k),this.rowNum+1,(short)(colIndex+k),bbsvalue+"%",centerstyle);
											 }else{
												 executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),bbsvalue+"%",centerstyle);
											 }
											 k++;
										 }
									 }
									this.rowNum=this.rowNum+hang+1;
		    					}
			    			}
				    		/**没有指标**/
			    			else
			    			{
			    				if(e==0)
			    				{
			    					/**画出空格*/
				    				int k=0;
				    				for(int f=0;f<this.lay-layer;f++)
				    				{
				    					executeCell(this.rowNum,(short)(colIndex+f),this.rowNum+hang,(short)((colIndex+f)),"",centerstyle);
				    				    k++;
				    				}
				    				executeCell(this.rowNum,(short)(colIndex+k),this.rowNum+hang,(short)(colIndex+k),"",centerstyle);
				    				for(int t=0;t<perGradeTemplateList.size();t++){
				    					executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),"",centerstyle);
				    					 if(method==null|| "0".equalsIgnoreCase(method)||method.trim().length()==0){//纵向
				    						 executeCell(this.rowNum,(short)(colIndex+k),this.rowNum+1,(short)(colIndex+k),"",centerstyle);
				    					 }else{
				    						 k++;
				    						 executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),"",centerstyle);
				    					 }
				    					 k++;
				    				}
				    				if(showbenbu!=null&&showbenbu.trim().length()!=0&& "1".equalsIgnoreCase(showbenbu)){
				    					 if(method==null|| "0".equalsIgnoreCase(method)||method.trim().length()==0){//纵向
				    						 executeCell(this.rowNum,(short)(colIndex+k),this.rowNum+1,(short)(colIndex+k),"",centerstyle);
				    					 }else{
				    						 executeCell(this.rowNum,(short)(colIndex+k),this.rowNum,(short)(colIndex+k),"",centerstyle);
				    					 }
				    					 
				    					 k++;
				    				}
			    					this.rowNum=this.rowNum+hang+1;
			    				}
	    					}
	    				}
					}
				/*}*/
			}
			// hua总体评价
			if(showComment!=null&& "true".equalsIgnoreCase(showComment)){
				this.colIndex=0;
				int colspan=0;
				if(method!=null&& "2".equalsIgnoreCase(method)){
					colspan=1;
				}else{
					colspan=2;
				}	
				HashMap objectpoint=(HashMap)comment.get(0);
				HashMap objectscale=(HashMap)comment.get(1);
				HashMap benbuscale=(HashMap)comment.get(2);
				executeCell(this.rowNum,this.colIndex,this.rowNum+colspan-1,(short)(this.colIndex+2+lie),"总体评价",centerstyle);
				this.colIndex=(short)(this.colIndex+3+lie);
				for(int k=0;k<=bodylist.size();k++){
					CommonData cd=null;
					String bdyid="";
					String name="";
					if(k==bodylist.size()){
						if("all1".equalsIgnoreCase(bodyid)){
							bdyid="total";
						}else{
							continue;
						}
					}else{
						cd=(CommonData)bodylist.get(k);
						bdyid=cd.getDataValue();
						name=cd.getDataName();
					}				
					if("all1".equalsIgnoreCase(bodyid)){
						if("all1".equalsIgnoreCase(bdyid)|| "all".equalsIgnoreCase(bdyid)){
							continue;
						}else{
							
						}
					}else{
						if(bdyid.equalsIgnoreCase(bodyid)){
							
						}else{
							if(!"total".equalsIgnoreCase(bdyid)){
								continue;
							}
							
						}
					}
					
					HashMap descpoint=(HashMap)objectpoint.get(bdyid);
					HashMap descscale=(HashMap)objectscale.get(bdyid);
					for(int m=0;m<perGradeTemplateList.size();m++){
						 LazyDynaBean grade=(LazyDynaBean)perGradeTemplateList.get(m);
						 String desc=(String)grade.get("grade_template_id");
						 String descpointvalue=(String)descpoint.get(desc);
						 executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,descpointvalue,centerstyle);
						 String descscalevalue=(String)descscale.get(desc);
						 if(method!=null&& "2".equalsIgnoreCase(method)){//横向
							 this.colIndex++;
							 executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,descscalevalue+"%",centerstyle);
							
						 }else{
							 executeCell(this.rowNum+1,this.colIndex,this.rowNum+colspan-1,this.colIndex,descscalevalue+"%",centerstyle);
						 }
						 this.colIndex++;
					}
					if(aband!=null&& "1".equalsIgnoreCase(aband.trim())){
						if("all1".equalsIgnoreCase(bodyid)){
							String total1=(String)descpoint.get("total");
							String total2=(String)descscale.get("total");
							executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,total1,centerstyle);
							 if(method!=null&& "2".equalsIgnoreCase(method)){//横向
								 this.colIndex++;
								 executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,total2+"%",centerstyle);
								
							 }else{
								 executeCell(this.rowNum+1,this.colIndex,this.rowNum+colspan-1,this.colIndex,total2+"%",centerstyle);
							 }
							 this.colIndex++;
						}
						String aband1=(String)descpoint.get("aband");
						String aband2=(String)descscale.get("aband");
						executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,aband1,centerstyle);
						 if(method!=null&& "2".equalsIgnoreCase(method)){//横向
							 this.colIndex++;
							 executeCell(this.rowNum,this.colIndex,this.rowNum,this.colIndex,aband2+"%",centerstyle);
							
						 }else{
							 executeCell(this.rowNum+1,this.colIndex,this.rowNum+colspan-1,this.colIndex,aband2+"%",centerstyle);
						 }
						 this.colIndex++;
					}
					
				}
				if(showbenbu!=null&& "1".equalsIgnoreCase(showbenbu)){
					for(int m=0;m<perGradeTemplateList.size();m++){
						 LazyDynaBean grade=(LazyDynaBean)perGradeTemplateList.get(m);
						 String desc=(String)grade.get("grade_template_id");
						 String descpointvalue=(String)benbuscale.get(desc);
						 executeCell(this.rowNum,this.colIndex,this.rowNum+colspan-1,this.colIndex,descpointvalue+"%",centerstyle);
						 this.colIndex++;
					}
				}
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return totalnum;
		
	}
	//通过um获取un
	public String searchUnitByDpt(String um){
		String value = null;
		RowSet rs  =null;
		ContentDAO dao = new ContentDAO(this.conn);
		String sql = "select parentid from organization where codeitemid="+"'"+um+"'";
		try{
			rs = dao.search(sql);
			if(rs.next()){
				value = rs.getString("parentid");
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null) {
                    rs.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return value;
	}
	public static String getyxb0110(UserView userView,Connection con) {
		String b0110 = "";
		String codePrefix ="";
		String operOrg = userView.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理 6：培训管理 7：招聘管理
		if("UN`".equalsIgnoreCase(operOrg)) {
            return "";
        }
		if (operOrg!=null && operOrg.length() > 3)
		{
			    String[] temp = operOrg.split("`");
			    b0110 = temp[0].substring(2,temp[0].length());
			    codePrefix = temp[0].substring(0,2);
			    if("UN".equalsIgnoreCase(codePrefix))//如果是单位
                {
                    b0110 = b0110;
                } else//如果是部门
                {
                    b0110 = getUnit(b0110,con);
                }
		} 
//		else if((!userView.isAdmin()) && (!operOrg.equalsIgnoreCase("UN`"))) // 按照管理范围走 
//		{		
//			    codePrefix = userView.getManagePrivCode();
//			    String codeid = userView.getManagePrivCodeValue();
//		 if(codePrefix.equalsIgnoreCase("UN"))//如果是单位
//				b0110 = codeid;
//			else//如果是部门
//				b0110 = getUnit(codeid,con);
//		}
		else{
			    b0110 =  userView.getUserOrgId();
		}
		
		if(b0110.trim().length()==0) {
            b0110="x";
        }

		return b0110;
	}
	public static String getUnit(String codeid,Connection con){
		String unit = "";
		RowSet rs = null;
		try{
			String style = "";//返回UM或者UN
			StringBuffer sb = new StringBuffer();
			sb.append("select codesetid,codeitemid from organization where codeitemid= (select parentid from organization where codeitemid='"+codeid+"')");
			ContentDAO dao = new ContentDAO(con);
			rs = dao.search(sb.toString());
			if(rs.next()){
				style = rs.getString("codesetid");
				unit = rs.getString("codeitemid");
			}
			if("UM".equalsIgnoreCase(style)) {
                getUnit(unit,con);
            }
		}catch(SQLException e){
			e.printStackTrace();
		}
		return unit;
	}
	
	
	/**
	 * 批量另存
	 * @param oldid
	 * @param newid
	 * @param seq
	 * @param name
	 * @param topscore
	 * @param status
	 */
		public void FieldSaveAs(String oldid,String point_id,int seq,String pointname,String pointsetid)
		{
			try
			{
				StringBuffer sql = new StringBuffer();
				ContentDAO dao = new ContentDAO(this.conn);
				/**导入模板*/
				sql.append(" insert into per_point (point_id,pointsetid,seq,pointname,pointkind,formula,validflag,description,visible,fielditem,l_FieldItem,status,pointtype,pointctrl,kh_content,gd_principle,proposal)");
				sql.append(" select '");
				sql.append(point_id +"' as point_id,'"+pointsetid+"' as pointsetid,"+seq+" as seq,'");
				sql.append(pointname+"' as pointname,");
				sql.append("pointkind,formula,validflag,description,visible,fielditem,l_FieldItem,status,pointtype,pointctrl,kh_content,gd_principle,proposal ");
				sql.append(" from per_point where UPPER(point_id)='"+oldid.toUpperCase()+"'");
				dao.insert(sql.toString(),new ArrayList());
//				sql.setLength(0);
//				/**导入模板项目,导入指标*/
//				//int item_id = this.getMaxId("per_template_item", "item_id");
//				int item_seq = this.getMaxId("per_template_item", "seq");
//				ArrayList itemlist = getItemList(item_seq, oldid,newid);
//				templateItemSaveAs(dao,itemlist);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
		}
		/**
		 * 批量另存的标度
		 * @param oldid
		 * @param newid
		 * @param seq
		 * @param name
		 * @param topscore
		 * @param status
		 */
			public void ScaleFieldSave(String oldpoint_id,String point_id,String grade_id,String oldgrade_id)
			{
				try
				{
					StringBuffer sql = new StringBuffer();
					ContentDAO dao = new ContentDAO(this.conn);
					sql.append(" insert into per_grade (grade_id,point_id,gradevalue,gradedesc,gradecode,top_value,bottom_value)");
					sql.append(" select '");
					sql.append(grade_id+"' as grade_id,'"+point_id +"' as point_id");
					sql.append(",gradevalue,gradedesc,gradecode,top_value,bottom_value");
					sql.append(" from per_grade where UPPER(point_id)='"+oldpoint_id+"'and grade_id='"+oldgrade_id+"'");
					dao.insert(sql.toString(),new ArrayList());

				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				
			}
			/**
			 * 修改父节点并删除已存在的指标分类
			 * @param pointsetid
			 */
			public void changeAndDelete(String pointsetid,String tableName) {
				StringBuffer sqlstr = new StringBuffer("");
				ContentDAO dao = new ContentDAO(this.conn);
				RowSet rs = null;
				sqlstr.append("select pointsetid from "+tableName+" where parent_id not in ");
				if(Sql_switcher.searchDbServer() == Constant.ORACEL) {
					sqlstr.append(" ( select pointsetid from per_pointset_temp) or parent_id is null ");
				}
				else {
                    sqlstr.append(" ( select pointsetid from ##per_pointset_temp) or parent_id is null ");
                }
				try {
					rs = dao.search(sqlstr.toString());
					String pointset_id = "";
					while(rs.next()){
						pointset_id = rs.getString("pointsetid");
					}
					sqlstr.setLength(0);
					if(pointset_id != null && ! ("".equals(pointset_id)) && !("root".equals(pointsetid))) {
						sqlstr.append("delete from per_pointset where pointsetid<>"+pointset_id+" and pointsetname= ");
						sqlstr.append("(select pointsetname from per_pointset where parent_id="+pointsetid+" and pointsetid="+pointset_id+")");
						dao.delete(sqlstr.toString(),new ArrayList());
						sqlstr.setLength(0);
						sqlstr.append("update per_pointset set parent_id="+pointsetid+" where pointsetid= "+pointset_id+" ");
						dao.update(sqlstr.toString());
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
            public boolean templateIsUsedByJobtile(String templateid,ContentDAO dao) throws GeneralException {
                RowSet rs = null;
                boolean flag = false;
                try{
                    //已有提交打分的模板不允许修改。
                    List values = new ArrayList();
                    values.add(templateid);
                    DbWizard dbWizard = new DbWizard(this.conn);
                    if(dbWizard.isExistTable("kh_mainbody",false    )){
                        rs = dao.search("select template_id from kh_mainbody where template_id=? and status=2",values);
                        if(rs.next()){
                            flag = true;
                        }
                    }
                }catch (Exception e){
                    throw GeneralExceptionHandler.Handle(e);
                }finally {
                    PubFunc.closeDbObj(rs);
                }
                return flag;
            }
}
