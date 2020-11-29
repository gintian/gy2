package com.hjsj.hrms.module.jobtitle.configfile.businessobject;

import com.hjsj.hrms.businessobject.competencymodal.personPostModal.PersonPostModalBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:KhTemplateBo.java</p>
 * <p>Description>:KhTemplateBo.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2018-4-10 下午01:49:31</p>
 * <p>@version: 1.0</p>
 * <p>@author: linbz
 */

public class KhTemplateBo 
{

	private Connection conn;
	private int td_width=130;
	private int td_height=50;
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
	 * 根据模板id获得模板信息
	 * @param templateid
	 * @return
	 */
	public LazyDynaBean getTemplateInfo(String templateid)
	{
		LazyDynaBean bean = new LazyDynaBean();
		RowSet rs = null;
		try
		{
			String sql = "select * from per_template where template_id='"+templateid+"'";
			ContentDAO dao = new ContentDAO(this.conn);
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
		}finally{
			PubFunc.closeResource(rs);
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
		RowSet rs = null;
		try
		{
			String sql = "select template_setid,name from per_template_set where template_setid=(select template_setid from per_template where template_id='"+templateid+"')";
			ContentDAO dao = new ContentDAO(this.conn);
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
		}finally{
			PubFunc.closeResource(rs);
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
		RowSet rs = null;
		try
		{
			String sql = "select * from per_template_set where parent_id='"+parent_id+"'";
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
		}finally{
			PubFunc.closeResource(rs);
		}
		
	}
	/**
	 * 递归删除模板分类，以及模板分类下的模板
	 * @param id
	 */
	public void deleteTemplateSet(String id,ContentDAO dao)
	{
		RowSet rs = null;
		try{
			//StringBuffer buf = new StringBuffer();
			StringBuffer set_buf = new StringBuffer("");
			StringBuffer item_buf = new StringBuffer("");
			set_buf.append("'"+id.toUpperCase()+"'");
			this.getTemplateid(id, item_buf, dao);
			this.getTemplateSetChild(id, set_buf, item_buf, dao);
			StringBuffer templateitem = new StringBuffer("");
			if(item_buf!=null&&!"".equals(item_buf.toString()))
			{
	    	    String sql = "select * from per_template_item where UPPER(template_id) in("+item_buf.toString().toUpperCase().substring(1)+")";
	    	    rs = dao.search(sql);
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
		}finally{
			PubFunc.closeResource(rs);
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
		RowSet rs = null;
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
			rs = dao.search(sql.toString());
			if(rs.next())
			{
				flag=false;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
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
		RowSet rs = null;
		try
		{
			String sql = "select template_id from per_plan where template_id is not null";
			rs = dao.search(sql);
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
		}finally{
			PubFunc.closeResource(rs);
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
		RowSet rs = null;
		try{
			StringBuffer buf = new StringBuffer();
			buf.append(" select * from per_plan where UPPER(template_id)='");
			buf.append(id.toUpperCase());
			buf.append("'");
            rs = dao.search(buf.toString());
            if(rs.next())
            {
            	flag=true;
            }
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
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
		RowSet rs=null;
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
		}finally{
			PubFunc.closeResource(rs);
		}
		return flag;
	}
	public boolean isUse(String setid,ContentDAO dao)
	{
		boolean flag = false;
		RowSet rs = null;
		try
		{
			StringBuffer sql = new StringBuffer();
			sql.append(" select * from per_plan where template_id in (");
			sql.append(" select template_id from per_template where UPPER(template_setid)='"+setid+"')");
			rs = dao.search(sql.toString());
			if(rs.next())
			{
				flag=true;
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
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
		RowSet rs = null;
		try
		{
			String sql="select * from per_template_set where UPPER(parent_id)='"+itemid.toUpperCase()+"'";
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
		}finally{
			PubFunc.closeResource(rs);
		}
	}
	public void getTemplateid(String setid,StringBuffer buf,ContentDAO dao)
	{
		RowSet rs = null;
		try
		{
			String sql ="select template_id from per_template where UPPER(template_setid)='"+setid.toUpperCase()+"'";
			rs = dao.search(sql);
			while(rs.next())
			{
				buf.append(",'"+rs.getString("template_id").toUpperCase()+"'");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
		}
	}
	/**
	 * 设置考核模板项目的孩子节点
	 * @param pid
	 * @param id
	 */
	public void setTemplateItemChild(String pid,String id)
	{
		RowSet rs = null;
		try
		{
			if(pid==null|| "".equals(pid))
				return;
			StringBuffer buf = new StringBuffer();
			ContentDAO dao = new ContentDAO(this.conn);
			buf.append("select * from per_template_item where UPPER(parent_id)="+pid.toUpperCase()+" and item_id<>"+id);
			rs = dao.search(buf.toString());
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
		}finally{
			PubFunc.closeResource(rs);
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
		RowSet rs = null;
		try
		{
			StringBuffer buf = new StringBuffer();
			buf.append("select * from per_template_point where ");
			buf.append(" item_id="+itemid);
			buf.append(" and UPPER(point_id)='"+pointid.toUpperCase()+"'");
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(buf.toString());
			while(rs.next())
			{
				flag=true;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
		}
		return flag;
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
		RowSet rs = null;
		try
		{
			String sql = "select * from per_template_item where UPPER(template_id)='"+templateid.toUpperCase()+"' order by seq";
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql);
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
		}finally{
			PubFunc.closeResource(rs);
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
		RowSet rs = null;
		try
		{
			String sql = "select name from per_template where UPPER(template_id)='"+templateID.toUpperCase()+"'";
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql);
			while(rs.next())
			{
				title=rs.getString("name");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
		}
		return title;
	}
	 /**模板对应的所有指标列表*/
	 private ArrayList pointList;
		/**
		 * 取得模板指标列表
		 * @return
		 */
		public ArrayList getPointList(String templateID)
		{
			ArrayList list = new ArrayList();
			RowSet rowSet = null;
			try
			{
				ContentDAO dao = new ContentDAO(this.conn);
				rowSet=dao.search("select po.point_id,po.pointname,po.pointkind,pi.item_id,pp.score,pp.rank  from per_template_item pi,per_template_point pp,per_point po "
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
			}finally{
				PubFunc.closeResource(rowSet);
			}
			return list;
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
				if(itemToPointMap.get(item_id)!=null)
					n+=((ArrayList)itemToPointMap.get(item_id)).size();
				
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
		extendtHead.append("<td class='TableRow_2rows' style='border-top:0px;border-left:0px;' valign='middle' align='center' width='100' rowspan='"+row+"' colspan='"+this.lay+"'>考核项目</td>\r\n");
		extendtHead.append("<td class='TableRow_2rows'  style='border-top:0px;' valign='middle' align='center' width='100' rowspan='"+row+"' >考核指标</td>\r\n");
		extendtHead.append("<td class='TableRow_2rows'  style='border-top:0px;' valign='middle' align='center' width='100' rowspan='"+row+"' >指标解释</td>\r\n");
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
				if(existWriteItem.get(itemid)!=null)
					continue;
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
							    if(h!=0)
			    		    		htmlContext.append("<tr>\r\n");
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
					    					CommonData cd=(CommonData)bodyList.get(i);
					    					String name=cd.getDataName();
					    					String bodyid=cd.getDataValue();
					    					if(boydid!=null&& "all1".equalsIgnoreCase(boydid)){
					    						if("all".equalsIgnoreCase(bodyid)){
					    							continue;
					    						}else{
					    							if(bodyid.equalsIgnoreCase(boydid)){
					    								
					    							}else{
					    								continue;
					    							}
					    						}
					    					}
					    					 LazyDynaBean ticket=(LazyDynaBean)bodyGrade.get(bodyid);// 基本标度值
											 LazyDynaBean scale =(LazyDynaBean)bodyscale.get(bodyid);
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
											 LazyDynaBean scale =(LazyDynaBean)scaleforall1.get(point_id);
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
					    					String name=cd.getDataName();
					    					String bodyid=cd.getDataValue();
						    				
						    				 LazyDynaBean ticket=(LazyDynaBean)bodyGrade.get(bodyid);// 基本标度值
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
					    					CommonData cd=(CommonData)bodyList.get(i);
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
						    				LazyDynaBean ticket=(LazyDynaBean)ponitforall1.get(pointid);
											 LazyDynaBean scale =(LazyDynaBean)scaleforall1.get(pointid);	 
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
		    				if(x!=0)
		    		    		htmlContext.append("<tr>\r\n");
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
								 htmlContext.append(" rowspan='2' ");
								 htmlContext.append("  style='border-right:0px;' >&nbsp;&nbsp;</td>");
							 }
							 if(showaband!=null&&showaband.trim().length()!=0&& "1".equalsIgnoreCase(showaband)){//显示弃权
									
								 htmlContext.append("<td align=\"right\" class='RecordRow' rowspan='2'  style='border-right:0px;' >");
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
							 htmlContext.append("<td align=\"right\" style='border-right:0px;' class='RecordRow'");
							 if(method==null|| "0".equalsIgnoreCase(method)||method.trim().length()==0){
								  htmlContext.append(" rowspan='2' ");
							 }
							 htmlContext.append(" >&nbsp;&nbsp;</td>");
						 }
						htmlContext.append("</tr>");
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
		html="<table class='ListTable' width='"+nums*250+"' >"+extendtHead+htmlContext.toString();
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
		RowSet rowSet = null;
		try
		{
			PersonPostModalBo ppo = new PersonPostModalBo(this.conn);
			String per_comTable = "per_grade_template"; // 绩效标准标度
			if(ppo.getComOrPer(templateID,"temp"))
				per_comTable = "per_grade_competence"; // 能力素质标准标度
		    String sql = "select * from "+per_comTable+" order by gradevalue desc";
		    ContentDAO dao = new ContentDAO(this.conn);
		    rowSet = dao.search(sql);
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
		    
		    if(rowSet!=null)
		    	rowSet.close();
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		}finally{
			PubFunc.closeResource(rowSet);
		}
		return list;
	}
	
	public String writeHtml(){
		
		StringBuffer htmlContext=new StringBuffer("");
		StringBuffer score = new StringBuffer("");
		StringBuffer r_item=new StringBuffer("");
		HashMap existWriteItem=new HashMap();
		LazyDynaBean abean=null;
		LazyDynaBean a_bean=null;
		StringBuffer extendtHead = new StringBuffer();
		
		// 输出表头
		extendtHead.append("<tr class='trDeep_self' style='height:50px;font-size:16px;font-family:微软雅黑;' >\r\n");
		extendtHead.append("<td class='TableRow_2rows'  valign='middle' align='center'  colspan='"+this.lay+"'>考核项目</td>\r\n");
		extendtHead.append("<td class='TableRow_2rows'  valign='middle' align='center' style='min-width:110px;' >考核指标</td>\r\n");		
		extendtHead.append("<td class='TableRow_2rows'  valign='middle' align='center' >指标解释</td>\r\n");
		extendtHead.append("<td class='TableRow_2rows'  valign='middle' align='center' style='min-width:70px;' >指标类型</td>\r\n");
		extendtHead.append("<td class='TableRow_2rows'  valign='middle' align='center' style='min-width:60px;' >分值</td>\r\n");//width=\"40\"
       /**如果为权重的话，加一列权重*/
		if("1".equals(this.status))
        {
			extendtHead.append("<td class='TableRow_2rows'  valign='middle' align='center' >权重</td>\r\n");
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
				
				htmlContext.append("<tr style='min-height:50px;'>\r\n");
				rowNum++;
				/**所有父亲列表*/
				ArrayList linkParentList=(ArrayList)this.leafItemLinkMap.get(item_id);
				int current=linkParentList.size();
				/**叶子项目的继承关系列表*/
				for(int e=linkParentList.size()-1;e>=0;e--)
				{
					a_bean=(LazyDynaBean)linkParentList.get(e);
					String itemid=(String)a_bean.get("item_id");
					if(existWriteItem.get(itemid)!=null)
						continue;
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
							    if(h!=0)
			    		    		htmlContext.append("<tr style='min-height:50px;'>\r\n");
							    for(int f=0;f<this.lay-layer;f++)
								{
								   htmlContext.append("<td align=\"left\" class='RecordRow'>&nbsp;&nbsp;</td>");
								}
							    htmlContext.append("<td align=\"left\" class='RecordRow' ");
//				    			if(this.isVisible.equals("1"))
//				        			htmlContext.append(" onclick='changeColor(\""+(String)xbean.get("point_id")+"\",\""+2+"\")' id='"+(String)xbean.get("point_id")+"'");
				    			htmlContext.append(">"+(String)xbean.get("name")+"</td>");
				    			// 画指标描述
				    			htmlContext.append("<td align=\"left\" class='RecordRow' ");
				    			htmlContext.append(">"+(String)xbean.get("description")+"</td>");
				    			// 画指标类型
				    			String pointkindflag = (String)xbean.get("pointkind");
				    			String pointkind = "0".equals(pointkindflag) ? "定量" : "定性";
				    			htmlContext.append("<td align=\"left\" class='RecordRow' ");
				    			htmlContext.append(">"+pointkind+"</td>");
				    			
				    			htmlContext.append("<td align=\"right\" width=\"100\" class='RecordRow'>");
				    			if("1".equals(this.isVisible))
				    			{
//				    				htmlContext.append("<input onkeydown=\"checkKeyCode();\" type=\"text\" class=\"Input_self common_border_color\" name=\"score\" id=\"s_"+(String)xbean.get("point_id")+"\" value=\""+(String)xbean.get("score")+"\" maxlength='10'/>");
				    				htmlContext.append(""+(String)xbean.get("score")+"");
				    				score.append(","+(String)xbean.get("point_id"));
				    			}
				    			else
				    				htmlContext.append((String)xbean.get("score"));
				    			htmlContext.append("</td>");
				    			if("1".equals(this.status))
				    			{
				    				htmlContext.append("<td align=\"right\" class='RecordRow'>");
					    			if("1".equals(this.isVisible))
					    			{
//					    				htmlContext.append("<input onBlur=\"checkValue(this);\" onFocus=\"saveBeforeValue(this);\" onkeydown=\"checkKeyCode();\" type=\"text\" class=\"Input_self common_border_color\" name=\"score\" id=\"r_"+(String)xbean.get("point_id")+"\" value=\""+(String)xbean.get("rank")+"\" maxlength='10'/>");
					    				htmlContext.append(""+(String)xbean.get("rank")+"");
					    			}
					    			else
					    				htmlContext.append((String)xbean.get("rank"));
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
			    		    // 画指标描述
			    			htmlContext.append("<td align=\"left\" class='RecordRow' ></td>");
			    			// 画指标类型
			    			htmlContext.append("<td align=\"left\" class='RecordRow' ></td>");
			    			
					    	/**兼容目标管理，个性项目（没有指标的项目），可以直接给项目录分*/
					    	htmlContext.append("<td align=\"right\" class='RecordRow' width=\"100\">");
					    	if("1".equals(this.isVisible))
		    		    	{
//		    		    		htmlContext.append("<input onkeydown=\"checkKeyCode();\" type=\"text\" class=\"Input_self common_border_color\" name=\"i_score\" id=\"si_"+(String)a_bean.get("item_id")+"\" value=\""+(String)a_bean.get("score")+"\" maxlength='10'/>");
		    		    		htmlContext.append(""+(String)a_bean.get("score")+"");
		    		    		r_item.append(","+(String)a_bean.get("item_id"));
		    		    	}
		    		    	else
		    		    		htmlContext.append((String)a_bean.get("score"));
		    		    	htmlContext.append("</td>");
		    	    		if("1".equals(this.status))
		    		    	{
		    		    		htmlContext.append("<td align=\"right\" class='RecordRow'>");
			    	    		if("1".equals(this.isVisible))
			    	     		{
//			    		    		htmlContext.append("<input onBlur=\"checkValue(this);\" onFocus=\"saveBeforeValue(this);\" onkeydown=\"checkKeyCode();\" type=\"text\" class=\"Input_self common_border_color\" name=\"r_score\" id=\"ri_"+(String)a_bean.get("item_id")+"\" value=\""+(String)a_bean.get("rank")+"\" maxlength='10'/>");
			    		    		htmlContext.append(""+(String)a_bean.get("rank")+"");
			    	     		}
			        			else
			    	    			htmlContext.append((String)a_bean.get("rank"));
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
			    				if(x!=0)
			    		    		htmlContext.append("<tr style='min-height:50px;'>\r\n");
			    				LazyDynaBean xbean = (LazyDynaBean)fieldlist.get(x);
			    				 for(int f=0;f<this.lay-layer;f++)
			    	    		{
			    		    		htmlContext.append("<td align=\"left\" class='RecordRow'>&nbsp;&nbsp;</td>");
				     	    	}
				    			htmlContext.append("<td align=\"left\" class='RecordRow' ");
//				    			if(this.isVisible.equals("1"))
//				        			htmlContext.append(" onclick='changeColor(\""+(String)xbean.get("point_id")+"\",\""+2+"\")' id='"+(String)xbean.get("point_id")+"'");
				    			htmlContext.append(">"+(String)xbean.get("name")+"</td>");
				    			// 画指标描述
				    			htmlContext.append("<td align=\"left\" class='RecordRow' ");
				    			htmlContext.append(">"+(String)xbean.get("description")+"</td>");
				    			// 画指标类型
				    			String pointkindflag = (String)xbean.get("pointkind");
				    			String pointkind = "0".equals(pointkindflag) ? "定量" : "定性";
				    			htmlContext.append("<td align=\"left\" class='RecordRow' ");
				    			htmlContext.append(">"+pointkind+"</td>");
				    			
				    			htmlContext.append("<td align=\"right\" width=\"100\" class='RecordRow'>");
				    			if("1".equals(this.isVisible))
				    			{
//				    				htmlContext.append("<input onkeydown=\"checkKeyCode();\" onFocus=\"clearValue('"+(String)xbean.get("score")+"','s_"+(String)xbean.get("point_id")+"')\" type=\"text\" class=\"Input_self common_border_color\" name=\"score\" id=\"s_"+(String)xbean.get("point_id")+"\" value=\""+(String)xbean.get("score")+"\" maxlength='10'/>");
				    				htmlContext.append(""+(String)xbean.get("score")+"");
				    				score.append(","+(String)xbean.get("point_id"));
				    			}
				    			else
				    				htmlContext.append((String)xbean.get("score"));
				    			htmlContext.append("</td>");
				    			if("1".equals(this.status))
				    			{
				    				htmlContext.append("<td align=\"right\" class='RecordRow'>");
					    			if("1".equals(this.isVisible))
					    			{
//					    				htmlContext.append("<input onBlur=\"checkValue(this);\" onFocus=\"saveBeforeValue(this);\" onkeydown=\"checkKeyCode();\" type=\"text\" class=\"Input_self common_border_color\" name=\"score\" id=\"r_"+(String)xbean.get("point_id")+"\" value=\""+(String)xbean.get("rank")+"\" maxlength='10'/>");
					    				htmlContext.append(""+(String)xbean.get("rank")+"");
					    			}
					    			else
					    				htmlContext.append((String)xbean.get("rank"));
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
							// 画指标描述
			    			htmlContext.append("<td align=\"left\" class='RecordRow' ></td>");
			    			// 画指标类型
			    			htmlContext.append("<td align=\"left\" class='RecordRow' ></td>");
			    			
							/**兼容目标管理，个性项目（没有指标的项目），可以直接给项目录分*/
							htmlContext.append("<td align=\"right\" class='RecordRow' width=\"100\">");
							if("1".equals(this.isVisible))
			    			{
//			    				htmlContext.append("<input onkeydown=\"checkKeyCode();\" type=\"text\" class=\"Input_self common_border_color\" name=\"i_score\" onFocus=\"clearValue('"+(String)a_bean.get("score")+"','si_"+(String)a_bean.get("item_id")+"')\" id=\"si_"+(String)a_bean.get("item_id")+"\" value=\""+(String)a_bean.get("score")+"\" maxlength='10'/>");
			    				htmlContext.append(""+(String)a_bean.get("score")+"");
			    				r_item.append(","+(String)a_bean.get("item_id"));
			    			}
			    			else
			    				htmlContext.append((String)a_bean.get("score"));
			    			htmlContext.append("</td>");
			    			if("1".equals(this.status))
			    			{
			    				htmlContext.append("<td align=\"right\" class='RecordRow'>");
				    			if("1".equals(this.isVisible))
				    			{
//				    				htmlContext.append("<input onBlur=\"checkValue(this);\" onFocus=\"saveBeforeValue(this);\" onkeydown=\"checkKeyCode();\" type=\"text\" class=\"Input_self common_border_color\" name=\"r_score\" id=\"ri_"+(String)a_bean.get("item_id")+"\" value=\""+(String)a_bean.get("rank")+"\" maxlength='10'/>");
				    				htmlContext.append(""+(String)a_bean.get("rank")+"");
				    			}
				    			else
				    				htmlContext.append((String)a_bean.get("rank"));
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
		
		int width=(this.lay)*this.td_width;
		int height=rowNum*this.td_height;//border=1px    font-family:微软雅黑;font-size:14px;border-color:#CDCDCD;
		StringBuffer titleHtml=new StringBuffer("<table class='ListTable_self' style='background-color:#FFF;BORDER-RIGHT: #E0DEDE 1pt solid;'  width='100%' >");
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
		RowSet rs = null;
		try
		{
			StringBuffer sql = new StringBuffer();
			sql.append("select ptp.*,pp.pointname from per_template_point ptp,per_point pp where  ptp.item_id in (");
			sql.append("select item_id from per_template_item where UPPER(template_id)='");
			sql.append(templateID.toUpperCase()+"') and ptp.point_id=pp.point_id order by ptp.seq");
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql.toString());
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
		}finally{
			PubFunc.closeResource(rs);
		}
		return map;
	}

	
	public HashMap getItemHasFieldCount(String templateID)
	{
		HashMap itemsCountMap=new HashMap();
		RowSet rowSet = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
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
		}finally{
			PubFunc.closeResource(rowSet);
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
		}finally{
			PubFunc.closeResource(rs);
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
		RowSet rowSet = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			rowSet = dao.search("select * from  per_template_item where template_id='"+templateID+"'  order by seq");
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
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rowSet);
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
				if(parent_id.equals(item_id))
					setLeafItemFunc(a_bean);
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
    	}finally{
			PubFunc.closeResource(rs);
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
				if(itemToPointMap.get(item_id)!=null)
					n+=((ArrayList)itemToPointMap.get(item_id)).size();
				else
					n+=1;
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
				if(parent_id.equals(item_id))
					getLeafItemList(a_bean,list);
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
				if(linkList.size()>lay)
					lay=linkList.size();
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
		td.append("\r\n<td class='RecordRow' style='border-left:0px;' valign='middle' align='"+align+"'");
		if("1".equals(this.isVisible))
		{
	    	td.append(" onclick='changeColor(\""+itemid+"\",\""+type+"\");' id='"+itemid+"'" );
	    	td.append(" ondblclick='gaibian(\""+itemid+"\",\""+type+"\");'");
		}
		if(rowspan!=0)
			td.append(" rowspan='"+(rowspan)+"' ");
		else
			td.append(" height='"+td_height+"' ");
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
		
		if(rowspan!=0)
			td.append(" rowspan='"+(rowspan)+"' ");
		else
			td.append(" height='"+td_height+"' ");
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
		RowSet rs= null;
		try{
			StringBuffer buf = new StringBuffer();
			buf.append("select status from per_template where UPPER(template_id)='"+templateID.toUpperCase()+"'");
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(buf.toString());
			while(rs.next())
			{
				status=rs.getString("status")==null?"0":rs.getString("status");
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
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
		RowSet rs = null;
		try
		{
			String sql = " select * from per_template_item where UPPER(template_id)='"+templateid.toUpperCase()+"'";
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql);
			while(rs.next())
			{
				flag=true;
				break;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
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
		RowSet rs = null;
		try
		{
			String sql ="select parent_id from per_template_item where item_id="+itemid;
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql);
			while(rs.next())
			{
				parentid=rs.getString("parent_id");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
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
		RowSet rs = null;
		try
		{
			String sql = " select seq from per_template_item where item_id="+itemid;
			ContentDAO dao = new ContentDAO(this.conn);
			rs =dao.search(sql);
			while(rs.next())
			{
				seq=rs.getInt("seq");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
		}
		return seq;
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
		RowSet rs = null;
		try
		{
			StringBuffer sql = new StringBuffer();
			sql.append(" select a.item_id from per_template_point a,per_template_item b ");
			sql.append(" where a.item_id=b.item_id and UPPER(a.point_id)='");
			sql.append(pointid.toUpperCase()+"' and UPPER(b.template_id)='");
			sql.append(templateid.toUpperCase()+"'");
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql.toString());
			while(rs.next())
			{
				itemid=rs.getString("item_id");
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
		}
		return itemid;
	}
	
	/**************************************************************************************
	 *************************************************************************************/
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
	
	
	/****************************************************************************************************
	 * **************************************************************************************************/
	
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
	
	
}
