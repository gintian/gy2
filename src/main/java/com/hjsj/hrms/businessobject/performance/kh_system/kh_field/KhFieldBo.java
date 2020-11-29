package com.hjsj.hrms.businessobject.performance.kh_system.kh_field;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.sql.RowSet;

import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;

import com.hjsj.hrms.businessobject.gz.templateset.DownLoadXml;
import com.hjsj.hrms.businessobject.performance.kh_system.kh_template.KhTemplateBo;
import com.hjsj.hrms.businessobject.sys.RowSetToXmlBuilder;
import com.hjsj.hrms.servlet.performance.KhFieldTree;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;

/**
 * <p>Title:KhFieldBo.java</p>
 * <p>Description:</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2008-1-11 下午04:09:47</p>
 * @author LiZhenWei
 * @version 4.0
 */
public class KhFieldBo {

	private Connection conn;
	private UserView userView;
	DecimalFormat myformat1 = new DecimalFormat("########.########");//
	public KhFieldBo(Connection conn)
	{
		this.conn=conn;
	}
	public KhFieldBo()
	{
	}
	public KhFieldBo(Connection conn,UserView userView)
	{
		this.conn=conn;
		this.userView=userView;
	}
	public ArrayList getKhFieldInfo(String pointsetid,UserView userView)
	{
		ArrayList list = new ArrayList();
		try
		{
			String sql = "select point_id,seq,pointsetid,pointname,pointkind,validflag from per_point where pointsetid='"+pointsetid+"' order by seq,point_id";
			ContentDAO dao = new ContentDAO(this.conn);
			ContentDAO dao2 = new ContentDAO(this.conn);
			String sql2="select scope,pointsetid,pointsetname,parent_id,b0110,child_id,seq,validflag,subsys_id from per_pointset where pointsetid='"+pointsetid+"'";
			RowSet rs = null;
			RowSet rs2 = null;
			rs2 = dao2.search(sql2);
			rs = dao.search(sql);
			
			String b0110 ="";

			String yxb0110 = KhFieldTree.getyxb0110(userView,this.conn);
			while(rs2.next())
			{
				b0110 =rs2.getString("b0110");
			}
			while(rs.next())
			{   
				String duxie = "1";
				String isgs ="1";
				if(!b0110.equals(yxb0110)&&!userView.isSuper_admin()&&!"1".equals(userView.getGroupId())&&!"hjsj".equalsIgnoreCase(b0110)){
					isgs="2";//没有另存的权限
					}
				if(!userView.isSuper_admin()&&!"1".equals(userView.getGroupId()))
				{
					if(userView.isRWHaveResource(IResourceConstant.KH_FIELD,rs.getString("point_id")+"R")) {
                        duxie = "2";
                    }
					if(userView.isRWHaveResource(IResourceConstant.KH_FIELD,rs.getString("point_id"))) {
                        duxie = "1";
                    }
						
		if(!userView.isRWHaveResource(IResourceConstant.KH_FIELD,rs.getString("point_id"))&&!userView.isRWHaveResource(IResourceConstant.KH_FIELD,rs.getString("point_id")+"R"))
					{
						continue;
						
					}
//					if(!userView.isHaveResource(IResourceConstant.KH_FIELD, rs.getString("point_id")))
//					{
//						continue;
//					}
				}
				LazyDynaBean bean = new LazyDynaBean();
				
				bean.set("point_id",rs.getString("point_id"));
				bean.set("seq",rs.getString("seq"));
				bean.set("pointsetid",rs.getString("pointsetid"));
				bean.set("pointname",rs.getString("pointname"));
				bean.set("duxie",duxie);
				bean.set("isgs",isgs);
				if(rs.getString("pointkind")!=null&&!"".equals(rs.getString("pointkind")))
				{
					if("0".equals(rs.getString("pointkind")))
					{
						bean.set("pointkind", ResourceFactory.getProperty("kh.field.dxyd"));
					}
					if("1".equals(rs.getString("pointkind")))
					{
						bean.set("pointkind",ResourceFactory.getProperty("kh.field.dlyd"));
					}
						
				}
				bean.set("validflag",AdminCode.getCodeName("51",rs.getString("validflag")));
				list.add(bean);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	public int getNextAddOne(String column,String tablename)
	{
		int next = 0;
		try
		{
			String sql = "select max("+column+") as "+column+" from "+tablename;
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs= dao.search(sql);
			while(rs.next())
			{
				next = rs.getInt(column);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return next+1;
	}
	/**
	 * 通过序号生成器产生主键
	 * @param idname
	 * @return
	 */
	public String getNextId(String idname)
	{
		String id = "";
		try
		{
			 IDGenerator idg = new IDGenerator(2, this.conn);
			 id =idg.getId(idname);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return id;
	}
	
	/**
	 * 产生主键
	 * @param idname
	 * @return
	 */
	public String getMaxNextId(String idname,String fieldname)
	{
		String id = "";
		RowSet rowSet = null;
		try
		{
			int num = 0;	        
	        ContentDAO dao = new ContentDAO(this.conn);
	        rowSet = dao.search("select max(" + fieldname + ") from " + idname);
	        if (rowSet.next())
	        {
	        	num = rowSet.getInt(1);
	        }
	        ++num;	        
	        id = String.valueOf(num);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return id;
	}	
	
	public int saveFieldClass(String parent_id,String pointname,String flag,String b0110,String subsys_id,int Scope)
	{
		int n=0;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			RecordVo vo = new RecordVo("per_pointset");
			int pointsetid =new Integer(this.getNextId("per_pointSet.pointSetId")).intValue()/* getNextAddOne("pointsetid","per_pointset")*/;
			n=pointsetid;
			int seq = getNextAddOne("seq","per_pointset");
			if(!"root".equalsIgnoreCase(parent_id))//=root是个一级节点。其父节点为null
			{
				vo.setString("parent_id",parent_id);
				if(!isHaveChild(parent_id))
				{
					this.setChild_id(parent_id, String.valueOf(pointsetid), dao);
				}
			}
			vo.setString("pointsetname",pointname);
			vo.setInt("pointsetid",pointsetid);
			vo.setInt("seq",seq);
			vo.setInt("scope",Scope);
			vo.setString("validflag",flag);
			vo.setString("b0110",b0110);
			
			vo.setDate("create_date",new Date());
			vo.setString("subsys_id",subsys_id);
			dao.addValueObject(vo);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return n;
	}
	/**
	 * 判断是否已经有下孩子节点
	 * @param parent_id
	 * @return
	 */
	public boolean isHaveChild(String parent_id)
	{
		boolean flag= false;
		try
		{
			String sql = "select pointsetid from per_pointset where parent_id='"+parent_id+"'";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs= dao.search(sql);
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
	 * 将新增的节点的父节点的子节点赋值为新节点的id(当新节点是其父节点的第一个孩子时执行)
	 * @param pointsetid
	 * @param child_id
	 * @param dao
	 */
	public void setChild_id(String pointsetid,String child_id,ContentDAO dao)
	{
		try
		{
			String sql = "update per_pointset set child_id = '"+child_id+"' where pointsetid='"+pointsetid+"'";
			dao.update(sql);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public HashMap getFieldClassById(String pointsetid)
	{
		HashMap map = new HashMap();
		try
		{
			String sql = "select scope,validflag,pointsetname from per_pointset where pointsetid='"+pointsetid+"'";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(sql);
			while(rs.next())
			{
				map.put("name",rs.getString("pointsetname")==null?"":rs.getString("pointsetname"));
				map.put("flag",rs.getString("validflag")==null?"1":rs.getString("validflag"));
				map.put("scope",rs.getString("scope")==null?"":rs.getString("scope"));
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 修改分类指标
	 * @param pointname
	 * @param validflag
	 * @param pointsetid
	 */
	
	public void editFieldClass(String pointname,String validflag,String pointsetid,int scopeip)
	{
		try
		{
			String sql = " update per_pointset set pointsetname=?,validflag='"+validflag+"',scope='"+scopeip+"' where pointsetid = '"+pointsetid+"'";
			ArrayList list  = new ArrayList();
			list.add(pointname);
			ContentDAO dao = new ContentDAO(this.conn);
			dao.update(sql,list);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 得到调整顺序的指标的原来的顺序
	 * @param pointsetids
	 * @return
	 */
	public ArrayList getFieldClassSeq(String pointsetids)
	{
		ArrayList list = new ArrayList();
		try
		{
			String sql = "select seq  from per_pointset where pointsetid in("+pointsetids+") order by seq";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(sql);
			while(rs.next())
			{
				list.add(rs.getString("seq"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	public void reFieldClassSort(ArrayList seqList,String[] ids)
	{
		try
		{
			// 当前用户对该分类没有权限的时候，seqList为空 lium
			if (seqList == null || seqList.size() == 0) {
				return;
			}
			ContentDAO dao = new ContentDAO(this.conn);
			for(int i=0;i<ids.length;i++)
			{
				String seq= (String)seqList.get(i);
				String sql = "update per_pointset set seq='"+seq+"' where pointsetid='"+ids[i]+"'";
				dao.update(sql);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 得到要排序的指标分类
	 * @param parent_id
	 * @param subsys_id
	 * @return
	 */
	public ArrayList getFieldClassToSort(String parent_id,String subsys_id)
	{
		ArrayList list = new ArrayList();
		try
		{
			StringBuffer buf = new StringBuffer();
			buf.append("select pointsetid,pointsetname from per_pointset where ");
			if("root".equalsIgnoreCase(parent_id))
			{
				buf.append(" parent_id is null ");
			}else
			{
				buf.append(" UPPER(parent_id) = '");
				buf.append(parent_id.toUpperCase()+"' ");
			}
			buf.append(" and subsys_id = '"+subsys_id+"' ");
			/*
                bug 51720  haosl add
               非超级用户应该可以调整共享和私有（权限范围内的）指标分类
             */
            String unitIdByBusi = this.userView.getUnitIdByBusi("5");
            if(this.userView!=null && !this.userView.isSuper_admin()){
                if(!"UN`".equalsIgnoreCase(unitIdByBusi)){
                    buf.append(" and (b0110='HJSJ'");//共享
                    if(StringUtils.isNotBlank(unitIdByBusi) && unitIdByBusi.length() > 3) {
                        String[] tmp = unitIdByBusi.split("`");

                        for (int i = 0; i < tmp.length; i++) {
                            String b = tmp[i].substring(2);
                            buf.append(" or b0110 like '" + b + "%'");//本级、下级
                        }
                    }
                    buf.append(")");
                }
            }
			buf.append(" order by seq");
			ContentDAO dao  = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(buf.toString());
			while(rs.next())
			{
			    /*
			        bug 51720  haosl delete
			        此处判断当前用户是否有指标权限，而传入的是指标分类的代码，很不合理。
			        授权页面又没有指标分类的授权，所以非超级用户调整顺序时会看不见指标分类
                    讨论后决定非超级用户应该可以调整共享和私有（自己创建的）指标分类
			     */
               // if(this.userView!=null&&!this.userView.isSuper_admin()&&!this.userView.isHaveResource(IResourceConstant.KH_FIELD, rs.getString("pointsetid")))
					//continue;
				list.add(new CommonData(rs.getString("pointsetid"),rs.getString("pointsetname")));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 取得标准标度表的内容列表
	 * @return
	 */
	public ArrayList getGradeTemplateList(String subsys_id)
	{
		ArrayList list = new ArrayList();
		try
		{
			String per_comTable = "per_grade_template"; // 绩效标准标度
			if(subsys_id!=null && subsys_id.trim().length()>0 && "35".equalsIgnoreCase(subsys_id)) {
                per_comTable = "per_grade_competence"; // 能力素质标准标度
            }
			String sql ="select * from "+per_comTable+" order by UPPER(grade_template_id)";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs= dao.search(sql);
			int i=1;
			while(rs.next())
			{
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("seq",i+"");
				bean.set("gradedesc",rs.getString("gradedesc")==null?"":rs.getString("gradedesc"));
				bean.set("gradeid",rs.getString("grade_template_id")==null?"":rs.getString("grade_template_id"));
				bean.set("gradevalue",rs.getString("gradevalue")==null?"0":this.myformat1.format(rs.getDouble("gradevalue")));
				bean.set("top_value",rs.getString("top_value")==null?"0":this.myformat1.format(rs.getDouble("top_value")));
				bean.set("bottom_value",rs.getString("bottom_value")==null?"0":this.myformat1.format(rs.getDouble("bottom_value")));
				i++;
				list.add(bean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 得到单个标准标度的内容
	 * @param gradeid
	 * @return
	 */
	public LazyDynaBean getGradeInfoById(String gradeid,String subsys_id)
	{
		LazyDynaBean bean = new LazyDynaBean();
		try
		{
			String per_comTable = "per_grade_template"; // 绩效标准标度
			if(subsys_id!=null && subsys_id.trim().length()>0 && "35".equalsIgnoreCase(subsys_id)) {
                per_comTable = "per_grade_competence"; // 能力素质标准标度
            }
			String sql ="select * from "+per_comTable+" where grade_template_id='"+gradeid+"'";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs= dao.search(sql);
			while(rs.next())
			{
				bean.set("top_value",rs.getString("top_value")==null?"0":this.myformat1.format(rs.getDouble("top_value")));
				bean.set("bottom_value",rs.getString("bottom_value")==null?"0":this.myformat1.format(rs.getDouble("bottom_value")));
				bean.set("gradeid",rs.getString("grade_template_id")==null?"":rs.getString("grade_template_id"));
				bean.set("gradevalue",rs.getString("gradevalue")==null?"0":this.myformat1.format(rs.getDouble("gradevalue")));
				bean.set("gradedesc",rs.getString("gradedesc")==null?"":rs.getString("gradedesc"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return bean;
	}
	/**
	 * 判断标度代码是否已经存在
	 * @param gradeid
	 * @return
	 */
	public boolean isHaveThisRecord(String gradeid,String subsys_id)
	{
		boolean flag=false;
		try
		{
			String per_comTable = "per_grade_template"; // 绩效标准标度
			if(subsys_id!=null && subsys_id.trim().length()>0 && "35".equalsIgnoreCase(subsys_id)) {
                per_comTable = "per_grade_competence"; // 能力素质标准标度
            }
			String sql = "select grade_template_id from "+per_comTable+" where grade_template_id = '"+gradeid+"'";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(sql);
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
	public ArrayList getFieldGrade(String point_id,String subsys_id)
	{
		ArrayList list = new ArrayList();
		try
		{
			String per_comTable = "per_grade_template"; // 绩效标准标度
			if(subsys_id!=null && subsys_id.trim().length()>0 && "35".equalsIgnoreCase(subsys_id)) {
                per_comTable = "per_grade_competence"; // 能力素质标准标度
            }
			String sql = "select t.gradedesc as bz,g.* from per_grade g left join "+per_comTable+" t on g.gradecode=t.grade_template_id where g.point_id ='"+point_id+"' order by grade_template_id";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(sql);
			int i=1;
			while(rs.next())
			{
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("seq",i+"");
				bean.set("bz",rs.getString("bz")==null?"":rs.getString("bz"));
				//PubFunc.toHtml
				bean.set("gradedesc",rs.getString("gradedesc")==null?"":rs.getString("gradedesc").replaceAll("\r\n", "<br>"));
				bean.set("gradevalue",rs.getString("gradevalue")==null?"0":this.myformat1.format(rs.getDouble("gradevalue")));
				bean.set("gradeid",rs.getString("gradecode")==null?"":rs.getString("gradecode"));
				bean.set("top_value",rs.getString("top_value")==null?"0":this.myformat1.format(rs.getDouble("top_value")));
				bean.set("bottom_value",rs.getString("bottom_value")==null?"0":this.myformat1.format(rs.getDouble("bottom_value")));
				bean.set("grade_id", rs.getString("grade_id"));
				i++;
				list.add(bean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
		
	}
	
	public ArrayList getFieldGradeBr(String point_id,String subsys_id)
	{
		ArrayList list = new ArrayList();
		try
		{
			String per_comTable = "per_grade_template"; // 绩效标准标度
			if(subsys_id!=null && subsys_id.trim().length()>0 && "35".equalsIgnoreCase(subsys_id)) {
                per_comTable = "per_grade_competence"; // 能力素质标准标度
            }
			String sql = "select t.gradedesc as bz,g.* from per_grade g left join "+per_comTable+" t on g.gradecode=t.grade_template_id where g.point_id ='"+point_id+"' order by grade_template_id";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(sql);
			int i=1;
			while(rs.next())
			{
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("seq",i+"");
				bean.set("bz",rs.getString("bz")==null?"":rs.getString("bz"));
				//PubFunc.toHtml
				bean.set("gradedesc",rs.getString("gradedesc")==null?"":rs.getString("gradedesc"));
				bean.set("gradevalue",rs.getString("gradevalue")==null?"0":this.myformat1.format(rs.getDouble("gradevalue")));
				bean.set("gradeid",rs.getString("gradecode")==null?"":rs.getString("gradecode"));
				bean.set("top_value",rs.getString("top_value")==null?"0":this.myformat1.format(rs.getDouble("top_value")));
				bean.set("bottom_value",rs.getString("bottom_value")==null?"0":this.myformat1.format(rs.getDouble("bottom_value")));
				bean.set("grade_id", rs.getString("grade_id"));
				i++;
				list.add(bean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
		
	}
	
	public LazyDynaBean getFieldInfoById(String point_id)
	{
		LazyDynaBean bean = new LazyDynaBean();
		try
		{
			String sql = " select * from per_point where point_id ='"+point_id+"'";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(sql);
			while(rs.next())
			{
				bean.set("point_id", rs.getString("point_id")==null?"":rs.getString("point_id"));
				bean.set("pointname",rs.getString("pointname")==null?"":rs.getString("pointname"));
				bean.set("pointkind", rs.getString("pointkind")==null?"":rs.getString("pointkind"));
				bean.set("validflag",rs.getString("validflag")==null?"":rs.getString("validflag"));
				bean.set("description",rs.getString("description")==null?"":rs.getString("description"));
				bean.set("visible",rs.getString("visible")==null?"":rs.getString("visible"));
				bean.set("status",rs.getString("status")==null?"":rs.getString("status"));
				bean.set("pointtype",rs.getString("pointtype")==null?"0":rs.getString("pointtype"));
				bean.set("kh_content", Sql_switcher.readMemo(rs,"kh_content"));
				bean.set("gd_principle", Sql_switcher.readMemo(rs,"gd_principle"));
				bean.set("formula",rs.getString("formula")==null?"":rs.getString("formula"));
				bean.set("proposal",rs.getString("proposal")==null?"":rs.getString("proposal"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return bean;
	}
	public boolean isHaveThisField(String point_id)
	{
		boolean flag = false;
		try
		{
			String sql = "select point_id from per_point where UPPER(point_id) = '"+point_id.toUpperCase()+"'";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs= dao.search(sql);
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
	public void deleteField(String ids,String pointsetid)
	{
		try
		{
			String sql = "delete from per_point where point_id in("+ids+") and pointsetid ='"+pointsetid+"'";
			ContentDAO dao = new ContentDAO(this.conn);
			dao.delete(sql,new ArrayList());
			sql = "delete from per_grade where point_id in("+ids+")";
			dao.delete(sql,new ArrayList());
			
			// 删除指标时同时删除掉定义的指标规则  JinChunhai 2011.08.30 
			sql = "delete from per_standard_item where point_id in("+ids+")";
			dao.delete(sql,new ArrayList());			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public String isUsed(String ids,String subsys_id)
	{
		StringBuffer buf = new StringBuffer("1");
		try
		{
			StringBuffer str=new StringBuffer();
			if(ids!=null&&!"".equals(ids))
			{
				ContentDAO dao = new ContentDAO(this.conn);
				RowSet rs = null;
				String [] temp = ids.split("/");
				for(int i=0;i<temp.length;i++)
				{
					String sql = "select point_id from per_template_point where point_id ='"+temp[i]+"'";					
					rs = dao.search(sql);
					boolean used = false;
					if (rs.next())
					{
						str.append(",'");
						str.append(temp[i]);
						str.append("'");
						used = true;
					}
					if(subsys_id!=null && subsys_id.trim().length()>0 && "35".equalsIgnoreCase(subsys_id) && !used)
					{
						sql = "select point_id from per_competency_modal where point_id ='"+temp[i]+"'";
						rs = dao.search(sql);
						if (rs.next())
						{
							str.append(",'");
							str.append(temp[i]);
							str.append("'");
							used = true;
						}
					}
					if(!used)
					{
						sql = "select p4.p0401 from p04 p4,per_plan pa where p4.p0401 ='"+temp[i]+"' and p4.plan_id=pa.plan_id and pa.status!=7";
						rs = dao.search(sql);
						if (rs.next())
						{
							str.append(",'");
							str.append(temp[i]);
							str.append("'");
						}
					}
					
				}
				if(str!=null && str.length()>0)
				{
					buf.setLength(0);
					buf.append(ResourceFactory.getProperty("kh.field.field"));
					String sql = "select pointname from per_point where point_id in("+str.toString().substring(1)+")";
					rs = dao.search(sql);
					while(rs.next())
					{
						buf.append(" [");
						buf.append(rs.getString("pointname"));
						buf.append("]");
					}
					buf.append(" "+ResourceFactory.getProperty("kh.field.no_delete"));
				}
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return buf.toString();
	}
	public String isduxie(String ids,UserView userView)
	{
		StringBuffer buf = new StringBuffer("1");
		try
		{
			StringBuffer str=new StringBuffer();
			if(ids!=null&&!"".equals(ids))
			{
				ContentDAO dao = new ContentDAO(this.conn);
				RowSet rs = null;
				String [] temp = ids.split("/");
				for(int i=0;i<temp.length;i++)
				{   
					
					
					if(!userView.isSuper_admin()&&!"1".equals(userView.getGroupId()))
					{
					if(!userView.isRWHaveResource(IResourceConstant.KH_FIELD,temp[i]))
					{
						str.append(",'");
						str.append(temp[i]);
						str.append("'");
					}
					}
				}
				if(str!=null && str.length()>0)
				{
					buf.setLength(0);
					buf.append("您没有维护"+ResourceFactory.getProperty("kh.field.field"));
					String sql = "select pointname from per_point where point_id in("+str.toString().substring(1)+")";
					rs = dao.search(sql);
					while(rs.next())
					{
						buf.append(" [");
						buf.append(rs.getString("pointname"));
						buf.append("]");
					}
					buf.append(" "+"的权限!");
					//buf.append(" "+ResourceFactory.getProperty("kh.field.no_delete"));
				}
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return buf.toString();
	}
	public String hasThisGrade(String ids,String subsys_id)
	{
		StringBuffer buf = new StringBuffer("1");
		try
		{
			String per_comTable = "per_grade_template"; // 绩效标准标度
			if(subsys_id!=null && subsys_id.trim().length()>0 && "35".equalsIgnoreCase(subsys_id)) {
                per_comTable = "per_grade_competence"; // 能力素质标准标度
            }
			//StringBuffer str= new StringBuffer();
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			String[] temp = ids.split("/");
			for(int i=0;i<temp.length;i++)
			{
				String sql = " select grade_template_id from "+per_comTable+" where grade_template_id='"+temp[i]+"'";
				rs = dao.search(sql);
				if(rs.next())
				{
					continue;
				}
				else
				{
					buf.setLength(0);
					buf.append(ResourceFactory.getProperty("kh.field.yz_code")+"!");
					break;
				}
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return buf.toString();
	}
	public void deleteFieldGrade(String point_id)
	{
		try
		{
			String sql = "delete from per_grade where point_id='"+point_id+"'";
			ContentDAO dao = new ContentDAO(this.conn);
			dao.delete(sql,new ArrayList());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public static void produceFolder()
	{
		if(!(new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+"khfielddata/").isDirectory()))
		{
			new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+"khfielddata/").mkdir();
				
		}
	}
	/**
	 * 递归查找子指标分类
	 * @param pointsetid
	 * @param ids
	 * @throws GeneralException
	 */
	public void getChildID(String pointsetid,StringBuffer ids) throws GeneralException
	{
		try
		{
			 StringBuffer buf=new StringBuffer();
			  ContentDAO dao=new ContentDAO(this.conn);
			  /**查找子节点*/
			  buf.append("select pointsetid from per_pointset where ");
			  if("root".equalsIgnoreCase(pointsetid))
			  {
				  buf.append("parent_id is null or parent_id = ''");
			  }
			  else
			  {
				  buf.append("parent_id ='"+pointsetid+"'");
			  }
			 
			  RowSet rset=dao.search(buf.toString());
			  while(rset.next())
			  {
				  String temp=rset.getString("pointsetid");
				  ids.append(temp+",");
				  getChildID(temp,ids);
			  }
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	/**
	 * 查找当前指标分类所有关联的数据
	 * @param pointsetid
	 * @return
	 */
	public HashMap getCurrentPointsetInfo(String pointsetid)
	{
		HashMap map = new HashMap();
		try
		{
			StringBuffer ids = new StringBuffer("");
			StringBuffer per_point = new StringBuffer("");
			StringBuffer per_grade = new StringBuffer("");
			StringBuffer per_grade_template = new StringBuffer("");
			this.getChildID(pointsetid, ids);
			ContentDAO dao = new ContentDAO(this.conn);
			KhTemplateBo bo = new KhTemplateBo(this.conn);
			//String parentid=bo.autoRelevancePrentID(pointsetid+",", "per_pointset", " pointsetid="+pointsetid, dao, "pointsetid");
			ids.append(pointsetid);
			String temp=/*parentid+*/ids.toString();
			if(temp.toString().length()>0)
			{
				
				RowSet rs = null;
				StringBuffer sql = new StringBuffer("");
				//StringBuffer gradecode = new StringBuffer();
				sql.append(" select point_id from per_point where pointsetid in ("+temp+")");
				rs = dao.search(sql.toString());
				while(rs.next())
				{
					per_point.append("'"+rs.getString("point_id")+"',");
				}
				sql.setLength(0);
				rs=null;
				if(per_point.toString().length()>0)
				{
					per_point.setLength(per_point.length()-1);
					sql.append(" select grade_id,gradecode from per_grade where UPPER(point_id) in ("+per_point.toString().toUpperCase()+")");
					rs=dao.search(sql.toString());
					while(rs.next())
					{
						per_grade.append(rs.getString("grade_id")+",");
						per_grade_template.append("'"+rs.getString("gradecode")+"',");
					}
				}
				if(per_grade_template.toString().length()>0)
				{
					per_grade_template.setLength(per_grade_template.length()-1);
				}
				if(per_grade.toString().length()>1)
				{
					per_grade.setLength(per_grade.length()-1);
				}
				sql.setLength(0);
				rs.close();	
				/*if(gradecode.toString().length()>0)
				{
					gradecode.setLength(gradecode.length()-1);
					sql.append("select grade_template_id from per_grade_template where grade");
				}*/
			}
			map.put("per_pointset",temp.toString());
			map.put("per_point",per_point.toString());
			map.put("per_grade", per_grade.toString());
			map.put("per_grade_template",per_grade_template.toString());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	public String exportData(String subsys_id,String export_type,String pointsetid)
	{
		String filename=this.userView.getUserName()+"KhField_"+PubFunc.getStrg()+".zip";
		RowSet rs = null;
		try
		{
			String per_pointset="";
			String per_point="";
			String per_grade="";
			String per_grade_template="";
			ContentDAO dao  = new ContentDAO(this.conn);
			StringBuffer buf = new StringBuffer("select pointsetid from per_pointset where subsys_id="+subsys_id);
			if("current".equalsIgnoreCase(export_type)) {
                buf.append(" and parent_id=" + pointsetid);
            }

			boolean hasPointSet=false;
			boolean hasPoint = false;
            if(this.userView.isSuper_admin())
            {
                hasPointSet = true;
            }else{
                /*
                bug 51715 haosl add
                非超级用户应该可以导出共享和私有（权限范围内的）指标分类
                 */
                String unitIdByBusi = this.userView.getUnitIdByBusi("5");
                if(this.userView!=null && !this.userView.isSuper_admin()){
                    if(!"UN`".equalsIgnoreCase(unitIdByBusi)){
                        buf.append(" and (b0110='HJSJ'");//共享
                        if(StringUtils.isNotBlank(unitIdByBusi) && unitIdByBusi.length() > 3) {
                            String[] tmp = unitIdByBusi.split("`");

                            for (int i = 0; i < tmp.length; i++) {
                                String b = tmp[i].substring(2);
                                buf.append(" or b0110 like '" + b + "%'");//本级、下级
                            }
                        }
                        buf.append(")");
                    }
                }
                rs=dao.search(buf.toString());
                if(rs.next()){
                    hasPointSet = true;
                }
            }
			/*while(rs.next())
			{
				if(this.userView.isSuper_admin())
				{
			    	hasPointSet = true;
			    	break;
				}else{
					if(this.userView.isHaveResource(IResourceConstant.KH_FIELD, rs.getString("pointsetid")))
					{
						hasPointSet = true;
				    	break;
					}
				}
			}*/
			buf.setLength(0);
			if(!hasPointSet)//所选节点下，无指标分类
			{
				buf.append("select point_id from per_point where pointsetid in (select pointsetid from per_pointset where subsys_id="+subsys_id+")");
				if("current".equalsIgnoreCase(export_type))
				{
					buf.setLength(0);
					buf.append("select point_id from per_point where pointsetid="+pointsetid);
				}
				rs = dao.search(buf.toString());
				while(rs.next())
				{
					if(this.userView.isSuper_admin())
					{
		    			hasPoint=true;
		    			break;
					}else{
						if(this.userView.isHaveResource(IResourceConstant.KH_FIELD, rs.getString("point_id")))
						{
							hasPoint=true;
			    			break;
						}
					}
				}
			}
			if(!hasPoint&&!hasPointSet)
			{
				return "1";
			}
				
			if("current".equalsIgnoreCase(export_type))
			{
				HashMap map = this.getCurrentPointsetInfo(pointsetid);
				per_pointset=(String)map.get("per_pointset");
				per_point=(String)map.get("per_point");
				per_grade=(String)map.get("per_grade");
				per_grade_template=(String)map.get("per_grade_template");
			}
			produceFolder();
			RowSetToXmlBuilder builder=new RowSetToXmlBuilder(this.conn);
			String outName="";
			FileOutputStream fileOut=null;
			 try{
				 outName="per_pointset.xml";
				 fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+"khfielddata"+System.getProperty("file.separator")+outName);
				 fileOut.write(getFileContent(subsys_id,"per_pointset",builder,export_type,per_pointset,pointsetid));
			 }finally{
				 PubFunc.closeIoResource(fileOut);
			 }
			 try{
				 outName="per_point.xml";
				 fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+"khfielddata"+System.getProperty("file.separator")+outName);
				 fileOut.write(getFileContent(subsys_id,"per_point",builder,export_type,per_point,pointsetid));
			 }finally{
				 PubFunc.closeIoResource(fileOut);
			 }
			 try{
				 outName="per_grade.xml";
				 fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+"khfielddata"+System.getProperty("file.separator")+outName);
				 fileOut.write(getFileContent(subsys_id,"per_grade",builder,export_type,per_grade,pointsetid));
			 }finally{
				 PubFunc.closeIoResource(fileOut);
			 }
			 try{
				 outName="per_grade_template.xml";
				 if(subsys_id!=null && subsys_id.trim().length()>0 && "35".equalsIgnoreCase(subsys_id)) {
                     outName = "per_grade_competence.xml"; // 能力素质标准标度
                 }
				 fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+"khfielddata"+System.getProperty("file.separator")+outName);
				 fileOut.write(getFileContent(subsys_id,"per_grade_template",builder,export_type,per_grade_template,pointsetid));
			 }finally{
				 PubFunc.closeIoResource(fileOut);
			 }
			 
			 ArrayList fileNames = new ArrayList(); // 存放文件名,并非含有路径的名字
			 ArrayList files = new ArrayList(); // 存放文件对象
			 FileInputStream fileIn = null;
			 ZipOutputStream outputStream = null;
			 BufferedInputStream origin = null;
			 try
			 {
				 fileOut = new FileOutputStream(
							System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+filename);
					outputStream = new ZipOutputStream(fileOut);
					File rootFile = new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+"khfielddata");
					listFile(rootFile, fileNames, files);
					byte data[] = new byte[2048];
					for (int loop = 0; loop < files.size(); loop++) {
						try {
							String a_fileName=(String) fileNames.get(loop);
							if(!"per_pointset.xml".equalsIgnoreCase(a_fileName)&&!"per_point.xml".equalsIgnoreCase(a_fileName)
									&&!"per_grade.xml".equalsIgnoreCase(a_fileName)&&!"per_grade_template.xml".equalsIgnoreCase(a_fileName)&&!"per_grade_competence.xml".equalsIgnoreCase(a_fileName)) {
                                continue;
                            }
							fileIn = new FileInputStream((File) files
									.get(loop));
							
							origin = new BufferedInputStream(fileIn, 2048);
							outputStream.putNextEntry(new ZipEntry((String) fileNames
									.get(loop)));
							int count;
							while ((count = origin.read(data, 0, 2048)) != -1) {
								outputStream.write(data, 0, count);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}finally {
							PubFunc.closeResource(origin);
							PubFunc.closeIoResource(fileIn);
							PubFunc.closeResource((File) files.get(loop));
						}
					}
			 }
			 catch(IOException ioe)
			 {
				 ioe.printStackTrace();
			 }finally{
				 PubFunc.closeResource(origin);
				 PubFunc.closeIoResource(fileIn);
				 PubFunc.closeResource(outputStream);
				 PubFunc.closeIoResource(fileOut);
	         }
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			PubFunc.closeIoResource(rs);
		}
		return filename;
	}
	
	/**
	 * 对于长度超出1000的in是不能承受的，拆分开。
	 * @param pointId
	 * @param id
	 * @return
	 */
	private String reCombineId(String pointId, String id) {
		String result = pointId + " in (" + id + ")";
		try {
			List<String> id_list = Arrays.asList(id.split(","));
			if(id_list.size() > 1000) {
				result = "";
				// 向上取整，找到要拆分的次数
				int len = (int) Math.ceil((double) id_list.size() / 1000);
				for(int i = 0; i < len; i++) {
					int end_len = (i+1)*1000;
					// 截取
					List<String> id_list_new = id_list.subList(i*1000, end_len > id_list.size() ? id_list.size() : end_len);
					result += " or " + pointId + " in (" + StringUtils.join(id_list_new.toArray(), ",") + ")";
				}
				result = result.substring(4);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public byte[] getFileContent(String subsys_id,String tablename,RowSetToXmlBuilder builder,String export_type,String id,String currentPointsetid)
	{
		String content="";
		try
		{
			RowSet rs = null;
			ContentDAO dao = new ContentDAO(this.conn);
			String sql ="";
			String privColumn="";
			int model=0;
			if("per_pointset".equalsIgnoreCase(tablename))
			{
				sql = " select * from per_pointset where subsys_id='"+subsys_id+"'";
				if("current".equalsIgnoreCase(export_type))
				{
					if(id!=null&&id.length()>0) {
						sql += " and (" + reCombineId("pointsetid", id) + ")";
                        /*
                            bug 51720  haosl add
                           非超级用户应该可以调整共享和私有（权限范围内的）指标分类
                         */
                        String unitIdByBusi = this.userView.getUnitIdByBusi("5");
                        if(this.userView!=null && !this.userView.isSuper_admin()){
                            if(!"UN`".equalsIgnoreCase(unitIdByBusi)){
                                sql+=" and (b0110='HJSJ'";//共享
                                if(StringUtils.isNotBlank(unitIdByBusi) && unitIdByBusi.length() > 3) {
                                    String[] tmp = unitIdByBusi.split("`");

                                    for (int i = 0; i < tmp.length; i++) {
                                        String b = tmp[i].substring(2);
                                        sql+=" or b0110 like '" + b + "%'";//本级、下级
                                    }
                                }
                                sql+=")";
                            }
                        }
                    }
					else {
                        sql += " and 1=2";
                    }
				}
			//	privColumn="pointsetid";
			//	model=IResourceConstant.KH_FIELD;
			}
			else if("per_point".equalsIgnoreCase(tablename))
			{
				sql = "select * from per_point where pointsetid in(select pointsetid from per_pointset where subsys_id="+subsys_id+")";
				if("current".equalsIgnoreCase(export_type))
				{
					if(id!=null&&id.length()>0) {
						sql+= " and (" + reCombineId("point_id", id) + ")";
                    } else {
                        sql+=" and 1=2";
                    }
				}
				privColumn="point_id";
				model=IResourceConstant.KH_FIELD;
			}
			else if("per_grade".equalsIgnoreCase(tablename))
			{
				sql = "select * from per_grade";
				if("current".equalsIgnoreCase(export_type))
				{
					if(id!=null&&id.length()>0) {
						sql+= " where (" + reCombineId("UPPER(grade_id)", id.toUpperCase()) + ")";
                    } else {
                        sql+=" where 1=2";
                    }
				}
			}
			else if("per_grade_template".equalsIgnoreCase(tablename))
			{
				tablename = "per_grade_template"; // 绩效标准标度
				if(subsys_id!=null && subsys_id.trim().length()>0 && "35".equalsIgnoreCase(subsys_id)) {
                    tablename = "per_grade_competence"; // 能力素质标准标度
                }
				sql = "select * from "+tablename+" ";
				/*if(export_type.equalsIgnoreCase("current"))
				{
					if(id!=null&&id.length()>0)
						sql+=" where UPPER(grade_template_id) in ("+id.toUpperCase()+")";
					else
						sql+=" where 1=2";
				}*/
			}
			
			rs = dao.search(sql);
			if("per_pointset".equalsIgnoreCase(tablename)){
                content=builder.outPutXml(rs, tablename);
            }else{
                content=builder.outPutXml(rs, tablename, privColumn, userView, model,currentPointsetid);
            }

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return content.getBytes();
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
	public String analyseData(InputStream inputstream)
	{
		String flag = "1";
		try
		{
			HashMap fileMap = DownLoadXml.extZipFileList(inputstream);
			if(fileMap.get("per_pointset.xml")==null)
			{
				flag ="2";
				return flag;
			}
			Document pointset = PubFunc.generateDom((String)fileMap.get("per_pointset.xml"));
			Document point = PubFunc.generateDom((String)fileMap.get("per_point.xml"));
			Document grade = PubFunc.generateDom((String)fileMap.get("per_grade.xml"));
			Document grade_template = PubFunc.generateDom((String)fileMap.get("per_grade_template.xml"));
			
			ArrayList pointsetlist = getTableData(pointset);
			ArrayList pointlist = getTableData(point);
			flag=String.valueOf(pointlist.size());
			ArrayList gradelist = getTableData(grade);
			ArrayList gradetemplatelist=getTableData(grade_template);
			deleteExistData(conn, 1, pointsetlist);
			deleteExistData(conn, 2, pointlist);
			deleteExistData(conn, 3, gradelist);
			deleteExistData(conn, 4, gradetemplatelist);
			HashMap pointsetColumn = DownLoadXml.getColumnTypeMap(this.conn, "per_pointset");
			HashMap pointColumn = DownLoadXml.getColumnTypeMap(this.conn, "per_point");
			HashMap gradeColumn = DownLoadXml.getColumnTypeMap(this.conn,"per_grade");
			HashMap gradetemplateColumn = DownLoadXml.getColumnTypeMap(this.conn, "per_grade_template");
			/*int pointsetid = getNextAddOne("pointsetid","per_pointset");
			int pointsetseq =  getNextAddOne("seq","per_pointset");
			
			int pointseq =  getNextAddOne("seq","per_point");
			
			int grade_id =  getNextAddOne("grade_id","per_grade");*/
			importData(this.conn,1,pointsetlist,pointsetColumn);
			importData(this.conn,2,pointlist,pointColumn);
			importData(this.conn,3,gradelist,gradeColumn);
			importData(this.conn,4,gradetemplatelist,gradetemplateColumn);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return flag;
	}
	/**
	 * 导入数据
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
                    vo = new RecordVo("per_pointset");
                }
				if(flag==2) {
                    vo = new RecordVo("per_point");
                }
				if(flag==3) {
                    vo= new RecordVo("per_grade");
                }
				if(flag==4) {
                    vo=new RecordVo("per_grade_template");
                }
				for(Iterator t=keySet.iterator();t.hasNext();)
				{
					String columnName=((String)t.next()).toLowerCase();
					if(flag==1&& "create_date".equalsIgnoreCase(columnName))
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
								String[] values=value.split("-");
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
	 * 根据xml串得到所有数据
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
	 * 删除原先存在的数据
	 * @param conn
	 * @param flag
	 * @param dataList
	 */
   public static void deleteExistData(Connection conn,int flag,ArrayList dataList)
   {
	   try
	   {
		   String tablename="";
		   String keyColumn="";
		   if(flag==1)
		   {
			   tablename="per_pointset";
			   keyColumn = "pointsetid";
		   }
		   if(flag==2)
		   {
			   tablename="per_point";
			   keyColumn ="point_id";
		   }
		   if(flag==3)
		   {
			   tablename="per_grade";
			   keyColumn="grade_id";
		   }
		   if(flag==4)
		   {
			   tablename="per_grade_template";
			   keyColumn="grade_template_id";
		   }
		   StringBuffer buf = new StringBuffer();
		   for(int i=0;i<dataList.size();i++)
		   {
			   LazyDynaBean bean = (LazyDynaBean)dataList.get(i);
			   buf.append(",'");
			   buf.append((String)bean.get(keyColumn));
			   buf.append("'");
		   }
		   if(buf.length()>0)
		   {
			   ContentDAO dao = new ContentDAO(conn);
			   String sql = "delete from "+tablename+" where "+keyColumn+" in  ("+buf.toString().substring(1)+")";
			   dao.delete(sql, new ArrayList());
		   }
		   
	   }
	   catch(Exception e)
	   {
		   e.printStackTrace();
	   }
	 
   }
   public ArrayList getFieldForSort(String pointsetid)
   {
	   ArrayList list = new ArrayList();
	   try
	   {
		   String sql = "select point_id,pointname from per_point where pointsetid='"+pointsetid+"' order by seq";
		   ContentDAO dao = new ContentDAO(this.conn);
		   RowSet rs = null;
		   rs =dao.search(sql);
		   while(rs.next())
		   {
			   if(this.userView!=null&&!this.userView.isSuper_admin()&&!this.userView.isHaveResource(IResourceConstant.KH_FIELD, rs.getString("point_id"))) {
                   continue;
               }
		       list.add(new CommonData(rs.getString("point_id"),rs.getString("pointname")));
		   }
	   }
	   catch(Exception e)
	   {
		   e.printStackTrace();
	   }
	   return list;
   }
   public ArrayList getFieldSeq(String pointsetid)
   {
	   ArrayList list = new ArrayList();
	   try
	   {
		   String sql = "select seq from per_point where pointsetid='"+pointsetid+"' order by seq";
		   ContentDAO dao = new ContentDAO(this.conn);
		   RowSet rs =null;
		   rs = dao.search(sql);
		   while(rs.next())
		   {
			   list.add(rs.getString("seq"));
		   }
	   }
       catch(Exception e)
       {
    	   e.printStackTrace();
       }
	   return list;
   }
   public void reFieldSort(ArrayList seqList,String[] ids,String pointsetid)
   {
	   try
	   {
		   ContentDAO dao = new ContentDAO(this.conn);
			for(int i=0;i<ids.length;i++)
			{
				String seq= (String)seqList.get(i);
				String sql = "update per_point set seq='"+seq+"' where point_id='"+ids[i]+"' and pointsetid='"+pointsetid+"'";
				dao.update(sql);
			}
		   
	   }
	   catch(Exception e)
	   {
		   e.printStackTrace();
	   }
   }
   public String getB0110(String pre,String a0100)
	{
		String unit="";
		try
		{
			String sql = "select b0110 from "+pre+"a01 where a0100='"+a0100+"'";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs =dao.search(sql);
			while(rs.next())
			{
				unit = rs.getString("b0110");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return unit;
	}
   public String getNextSeq(String keyCloumn,String tableName)
	{
		String key="A0001";
		try
		{
			
			StringBuffer buf = new StringBuffer();
			buf.append("select MAX(");
			buf.append(keyCloumn+") as "+keyCloumn);
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
				/*if(a)
			    	key="Z"+key;*/
				
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
	 * 当修改指标分类的有效性的时候，同时修改孩子分类和指标的有效性
	 * @param parentid
	 * @param flag
	 */
	public void configChildFlag(String parentid,String flag,ContentDAO dao,ArrayList list)
	{
		try
		{
			StringBuffer sql=new StringBuffer("select pointsetid from per_pointset where ");
			StringBuffer updateSql=new StringBuffer("");
			if("root".equalsIgnoreCase(parentid))
			{
				sql.append(" parent_id is null or parent_id=''");
			}
			else
			{
				sql.append(" parent_id='"+parentid+"'");
			}
			RowSet rs=dao.search(sql.toString());
			while(rs.next())
			{
				String setid=rs.getString("pointsetid");
				updateSql.append("update per_pointset set validflag='"+flag+"' where pointsetid='"+setid+"'");
				list.add(updateSql.toString());
				updateSql.setLength(0);
				updateSql.append("update per_point set validflag='"+flag+"' where pointsetid='"+setid+"'");
				list.add(updateSql.toString());
				updateSql.setLength(0);
				configChildFlag(setid,flag,dao,list);
			}
			
			if("root".equalsIgnoreCase(parentid))
			{
				
			}
			else
			{
				updateSql.append(" update per_pointset set validflag='"+flag+"' where");
				updateSql.append(" pointsetid='"+parentid+"'");
				list.add(updateSql.toString());
				updateSql.setLength(0);
				updateSql.append("update per_point set validflag='"+flag+"' where pointsetid='"+parentid+"'");
				list.add(updateSql.toString());
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public HashMap getAllPoint()
	{
		HashMap map = new HashMap();
		RowSet rs = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			rs=dao.search("select point_id from per_point");
			while(rs.next())
			{
				map.put(rs.getString("point_id").toUpperCase(), "1");
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
		return map;
	}
	
	/**
	 * 指标类别从数据库中取得
	 */
    public ArrayList getKpiTargetTypeList()
    {
    	String creatDate = PubFunc.getStringDate("yyyy-MM-dd"); // 获得当前时间
		ArrayList list = new ArrayList();
		StringBuffer sqlStr = new StringBuffer();
		sqlStr.append("select distinct ");
		sqlStr.append(" item_type_desc from per_kpi_item where item_type_desc is not null ");
				
		StringBuffer buff = new StringBuffer();
		buff.append(Sql_switcher.year("start_date")+ "<"+ getDatePart(creatDate,"y") +" or ");
		buff.append("("+Sql_switcher.year("start_date")+ "="+ getDatePart(creatDate,"y")+" and ");
		buff.append(Sql_switcher.month("start_date")+ "<"+ getDatePart(creatDate,"m") +") or ");
		buff.append("("+Sql_switcher.year("start_date")+ "="+ getDatePart(creatDate,"y")+" and ");
		buff.append(Sql_switcher.month("start_date")+ "="+ getDatePart(creatDate,"m") +" and ");
		buff.append(Sql_switcher.day("start_date")+ "<="+ getDatePart(creatDate,"d") +")");
		sqlStr.append(" and ("+buff.toString()+") ");
		
		StringBuffer buf = new StringBuffer();
		buf.append(Sql_switcher.year("end_date")+ ">"+ getDatePart(creatDate,"y") +" or ");
		buf.append("("+Sql_switcher.year("end_date")+ "="+ getDatePart(creatDate,"y")+" and ");
		buf.append(Sql_switcher.month("end_date")+ ">"+ getDatePart(creatDate,"m") +") or ");
		buf.append("("+Sql_switcher.year("end_date")+ "="+ getDatePart(creatDate,"y")+" and ");
		buf.append(Sql_switcher.month("end_date")+ "="+ getDatePart(creatDate,"m") +" and ");
		buf.append(Sql_switcher.day("end_date")+ ">="+ getDatePart(creatDate,"d") +")");
		sqlStr.append(" and ("+buf.toString()+") ");
		
		sqlStr.append(" order by item_type_desc ");	
//		sqlStr.append(" order by cycle,item_type_desc,item_id ");
		
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			list.add(new CommonData("",""));
		    RowSet rs = dao.search(sqlStr.toString());
		    while(rs.next())
		    {
				String item_type_desc = rs.getString("item_type_desc");
				CommonData data=new CommonData(item_type_desc,item_type_desc);	   
				list.add(data);
		    }
		    
		    if(rs!=null) {
                rs.close();
            }
		    
		} catch (SQLException e)
		{
		    e.printStackTrace();
		}
		return list;
    }
    /**
	 * 根据指标类别从数据库中取得类别下的指标
	 */
    public ArrayList getKpiTarget_idList(String kpiTargetType)
    {
		ArrayList list = new ArrayList();
		RowSet rs = null;
		String creatDate = PubFunc.getStringDate("yyyy-MM-dd"); // 获得当前时间
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			
			// 查询符合条件的KPI指标								
			StringBuffer strSql = new StringBuffer();	
			strSql.append("select item_id,itemdesc from per_kpi_item where 1=1 ");
			if(kpiTargetType!=null && kpiTargetType.trim().length()>0) {
                strSql.append(" and item_type_desc='"+ kpiTargetType +"' ");
            }
			strSql.append(getUserViewPrivWhere(this.userView)); // 归属范围内的KPI指标
			
			StringBuffer buff = new StringBuffer();
			buff.append(Sql_switcher.year("start_date")+ "<"+ getDatePart(creatDate,"y") +" or ");
			buff.append("("+Sql_switcher.year("start_date")+ "="+ getDatePart(creatDate,"y")+" and ");
			buff.append(Sql_switcher.month("start_date")+ "<"+ getDatePart(creatDate,"m") +") or ");
			buff.append("("+Sql_switcher.year("start_date")+ "="+ getDatePart(creatDate,"y")+" and ");
			buff.append(Sql_switcher.month("start_date")+ "="+ getDatePart(creatDate,"m") +" and ");
			buff.append(Sql_switcher.day("start_date")+ "<="+ getDatePart(creatDate,"d") +")");
			strSql.append(" and ("+buff.toString()+") ");
			
			StringBuffer buf = new StringBuffer();
			buf.append(Sql_switcher.year("end_date")+ ">"+ getDatePart(creatDate,"y") +" or ");
			buf.append("("+Sql_switcher.year("end_date")+ "="+ getDatePart(creatDate,"y")+" and ");
			buf.append(Sql_switcher.month("end_date")+ ">"+ getDatePart(creatDate,"m") +") or ");
			buf.append("("+Sql_switcher.year("end_date")+ "="+ getDatePart(creatDate,"y")+" and ");
			buf.append(Sql_switcher.month("end_date")+ "="+ getDatePart(creatDate,"m") +" and ");
			buf.append(Sql_switcher.day("end_date")+ ">="+ getDatePart(creatDate,"d") +")");
			strSql.append(" and ("+buf.toString()+") ");
			
			strSql.append(" order by seq ");
			
			list.add(new CommonData("",""));
		    rs = dao.search(strSql.toString());
		    while(rs.next())
		    {
				String item_id = isNull(rs.getString("item_id"));
				String itemdesc = isNull(rs.getString("itemdesc"));
				CommonData data=new CommonData("[" + itemdesc + "]",itemdesc);	   
				list.add(data);
		    }
		    
		    if(rs!=null) {
                rs.close();
            }
		    
		} catch (SQLException e)
		{
		    e.printStackTrace();
		}
		return list;
    }
	/**
	 * 根据用户权限获得计划列表 先看操作单位 再看管理范围
	 * 绩效所有模块都改为：超级用户也按操作单位优先的规则限制可操作范围    JinChunhai 2011.05.11
	 */
	public String getUserViewPrivWhere(UserView userView)
	{
		String str = "";
//		if (!userView.isSuper_admin())
		{
			StringBuffer buf = new StringBuffer();
			String operOrg = userView.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理 6：培训管理 7：招聘管理
			if (operOrg!=null && operOrg.length() > 3)
			{
				StringBuffer tempSql = new StringBuffer("");
				String[] temp = operOrg.split("`");
				for (int i = 0; i < temp.length; i++)
				{
					if ("UN".equalsIgnoreCase(temp[i].substring(0, 2))) {
                        tempSql.append(" or b0110 like '%," + temp[i].substring(2) + "%'");
                    } else if ("UM".equalsIgnoreCase(temp[i].substring(0, 2))) {
                        tempSql.append(" or b0110 like '%," + temp[i].substring(2) + "%'");
                    }
				}
//				buf.append(" and ( " + tempSql.substring(3) + " ) ");
				buf.append(" and ( b0110 is null " + tempSql + " ) ");
			} 
			else if((!userView.isAdmin()) && (!"UN`".equalsIgnoreCase(operOrg))) // 按照管理范围走
			{
				String codeid = userView.getManagePrivCode();
				String codevalue = userView.getManagePrivCodeValue();
				String a_code = codeid + codevalue;

				if (a_code.trim().length() > 0)// 说明授权了
				{
					if ("UN".equalsIgnoreCase(a_code))// 说明授权了组织结构节点 此时userView.getManagePrivCodeValue()得到空串
                    {
                        buf.append(" and 1=1 ");
                    } else {
                        buf.append(" and ( b0110 is null or b0110 like '%," + codevalue + "%')");
                    }
						
				} else {
                    buf.append(" and 1=2 ");
                }
			}
			str = buf.toString();
		}

		return str;		
	}
	/**
	 * 分解当前系统时间
	 */
	public String getDatePart(String mydate, String datepart)
	{
		String str = "";
		if ("y".equalsIgnoreCase(datepart)) {
            str = mydate.substring(0, 4);
        } else if ("m".equalsIgnoreCase(datepart))
		{
			if ("0".equals(mydate.substring(5, 6))) {
                str = mydate.substring(6, 7);
            } else {
                str = mydate.substring(5, 7);
            }
		} else if ("d".equalsIgnoreCase(datepart))
		{
			if ("0".equals(mydate.substring(8, 9))) {
                str = mydate.substring(9, 10);
            } else {
                str = mydate.substring(8, 10);
            }
		}
		return str;
	}
	
	public String isNull(String str)
    {
		if (str == null || str.trim().length()<=0 || " ".equalsIgnoreCase(str) || "".equalsIgnoreCase(str)) {
            str = "";
        }
		return str;
    }
	
	// 定义公式可选择的指标
	public ArrayList getSelectList()
	{
		ArrayList filelist = new ArrayList();
		RowSet rowSet = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			//加指标
			String sql = "select item_id,itemdesc from per_kpi_item order by item_id desc";
				       
			rowSet = dao.search(sql);
			while(rowSet.next())
			{
				FieldItem item = new FieldItem();
				item.setItemid(rowSet.getString("item_id"));
				item.setItemdesc(PubFunc.keyWord_reback(rowSet.getString("itemdesc")));
				item.setItemtype("N");
				item.setDecimalwidth(4);
				item.setItemlength(12);
				filelist.add(item);
			}
					
			if (rowSet != null) {
                rowSet.close();
            }
			
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return filelist;
	}
	
	/** 检查公式定义是否正确 */
	public String testformula(String formula) throws GeneralException
	{
		String errorInfo = "ok";
//		ContentDAO dao = new ContentDAO(this.conn);
		if (formula != null && formula.trim().length() > 0)
		{												
			YksjParser yp=new YksjParser(this.userView, this.getSelectList(), YksjParser.forNormal, YksjParser.FLOAT, YksjParser.forPerson, "Ht", "");			
		 
			boolean b = false;
			b = yp.Verify_where(formula.trim());

			if (b) // 校验通过
            {
                errorInfo = "ok";
            } else {
                errorInfo = yp.getStrError();
            }
			
		}else {
            errorInfo = "noHave";
        }
		
		return errorInfo;
	}
	
	
}
