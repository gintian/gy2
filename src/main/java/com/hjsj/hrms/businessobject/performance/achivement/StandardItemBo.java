package com.hjsj.hrms.businessobject.performance.achivement;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:StandardItemBo.java</p>
 * <p>Description>:StandardItemBo.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2008-9-12 上午09:43:20</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class StandardItemBo {
	private Connection conn;
	/**所有项目节点*/
	private ArrayList standarditemlist = new ArrayList();
	/**每个项目对应的叶子节点个数*/
	private HashMap itemToLeafMap = new HashMap();
	/**最大层*/
	private int lay=0;
	/**叶子节点对应的继承机构*/
	private HashMap leafmap=new HashMap();
	private int td_width=130;
	private int td_height=30;
	/**所有叶子节点列表*/
	private ArrayList leafItemList = new ArrayList();
	  /**是否已有项目=0有=1没有*/
	private String isHaveItem="1";
	public void initData(String point_id)
	{
		String html="";
		try
		{
			/**所有项目列表和叶子项目列表*/
			this.standarditemlist=this.getStandardItemByPointid(point_id);
			/**得到叶子节点继承关系*/
			this.leafmap=this.getLeafParentLinkMap();
			/**项目对应的叶子项目数，如果没有则为0：itemid<----->叶子数目*/
			this.itemToLeafMap=this.getItemToLeafItemCounterMap();
			/**递归查找叶子节点，这样查找可以保证顺序，如果在查项目列表中求叶子项目列表，则顺序会错乱*/
			get_LeafItemList();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public StandardItemBo()
	{
		
	}
	public StandardItemBo(Connection conn)
	{
		this.conn=conn;
	}
	/**
	 * 叶子项目列表
	 *
	 */
	public void get_LeafItemList()
	{
		LazyDynaBean abean=null;
		for(int i=0;i<this.standarditemlist.size();i++)
		{
			abean=(LazyDynaBean)this.standarditemlist.get(i);
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
		ContentDAO dao = new ContentDAO(this.conn);
		boolean flag=this.isLeaf(item_id, dao);
		if(flag)
		{
			this.leafItemList.add(abean);
				return;
		}
		LazyDynaBean a_bean=null;
		for(int j=0;j<this.standarditemlist.size();j++)
		{
				a_bean=(LazyDynaBean)this.standarditemlist.get(j);
				String parent_id=(String)a_bean.get("parent_id");
				if(parent_id.equals(item_id)) {
                    setLeafItemFunc(a_bean);
                }
		}
	}
	public boolean isLeaf(String item_id,ContentDAO dao)
	{
		boolean flag=true;
		try
		{
			String sql = "select * from per_standard_item where parent_id='"+item_id+"'";
			RowSet rs = dao.search(sql);
			while(rs.next())
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
	public HashMap getItemToLeafItemCounterMap()
	{
		HashMap map = new HashMap();
		try
		{
			LazyDynaBean a_bean=null;
			LazyDynaBean aa_bean=null;
			for(int i=0;i<standarditemlist.size();i++)
			{
				a_bean=(LazyDynaBean)this.standarditemlist.get(i);
				ArrayList list=new ArrayList();
				getLeafItemList(a_bean,list);
				int n=0;
				for(int j=0;j<list.size();j++)
				{
					aa_bean=(LazyDynaBean)list.get(j);
					String item_id=(String)aa_bean.get("item_id");
					n+=1;
				}
				map.put((String)a_bean.get("item_id"),new Integer(n));
			}
			return map;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	public void getLeafItemList(LazyDynaBean abean,ArrayList list)
	{
		String item_id=(String)abean.get("item_id");
		String child_id=(String)abean.get("child_id");
		
		if(child_id.length()==0)
		{
			//list.add(abean);
				return;
		}
		LazyDynaBean a_bean=null;
		for(int j=0;j<this.standarditemlist.size();j++)
		{
				a_bean=(LazyDynaBean)this.standarditemlist.get(j);
				String parent_id=(String)a_bean.get("parent_id");
				String child=(String)a_bean.get("child_id");
				if(parent_id.equals(item_id))
				{
					if(child.length()==0) {
                        list.add(abean);
                    }
	    			getLeafItemList(a_bean,list);
				}
		}
		
	}
	public String getStandardItemHTML(String point_id)
	{
		this.initData(point_id);
		StringBuffer htmlContext=new StringBuffer("");
		StringBuffer score = new StringBuffer("");
		HashMap existWriteItem=new HashMap();
		LazyDynaBean abean=null;
		LazyDynaBean a_bean=null;
		StringBuffer extendtHead = new StringBuffer();
		ContentDAO dao = new ContentDAO(this.conn);
		int rowNum=0;
		extendtHead.append("<tr class='trDeep_self'  height='30' >\r\n");
		extendtHead.append("<td class='TableRow'  valign='middle' align='center'  colspan='"+this.lay+"'>"+ResourceFactory.getProperty("kh.field.htmltile")+"</td>\r\n");
		extendtHead.append("<td class='TableRow'  valign='middle' align='center' >"+ResourceFactory.getProperty("kh.field.htmlscore")+"</td>\r\n");
		for(int i=0;i<this.leafItemList.size();i++)
		{
			abean=(LazyDynaBean)this.leafItemList.get(i);
			String item_id=(String)abean.get("item_id");
			htmlContext.append("<tr height=\""+this.td_height+"\">\r\n");
			rowNum++;
			ArrayList linkParentList=(ArrayList)this.leafmap.get(item_id);
			int current=linkParentList.size();
			for(int e=linkParentList.size()-1;e>=0;e--)
			{
				a_bean=(LazyDynaBean)linkParentList.get(e);
				String itemid=(String)a_bean.get("item_id");
				if(existWriteItem.get(itemid)!=null) {
                    continue;
                }
				existWriteItem.put(itemid,"1");
				ArrayList list = (ArrayList)this.leafmap.get(itemid);
				String itemdesc=(String)a_bean.get("itemdesc");
				//要判断是否有孩子节点,如果有孩子，colspan=1，如果没有，用这个算法
				int colspan=((Integer)(itemToLeafMap.get(itemid))).intValue();
				if(colspan==0) {
                    htmlContext.append(writeTd(itemdesc,(((Integer)itemToLeafMap.get(itemid)).intValue()),"left",this.td_width,itemid,(this.lay-list.size()+1)));
                } else {
                    htmlContext.append(writeTd(itemdesc,(((Integer)itemToLeafMap.get(itemid)).intValue()),"left",this.td_width,itemid,1));
                }
				if(e==0)
				{
	    			htmlContext.append("\r\n<td class='RecordRow' valign='middle' width='130' align='right' height=\""+this.td_height+"\"><input onkeypress=\"return checkKeyCode();\" onBlur=\"checkValue(this);\" type=\"text\" name=\"score\" value=\""+(String)abean.get("score")+"\"/>");
	    			htmlContext.append("<input type=\"hidden\" name=\"itemid\" value=\""+itemid+"\"/>");
	    			htmlContext.append("</td>");
				}
			}
			htmlContext.append("</tr>\r\n");
		}
		htmlContext.append("</table>");
		int width=(this.lay)*this.td_width;
		int height=rowNum*this.td_height;
		StringBuffer titleHtml=new StringBuffer("<table   class='ListTable_self' width='100%' height='"+height+"' >");
		StringBuffer html=new StringBuffer(titleHtml.toString());
		extendtHead.append("</tr>\r\n");
		html.append(extendtHead.toString());
		html.append(htmlContext.toString());
		//html.append("</table>");
		//System.out.println(html.toString());
		return html.toString();
	}
	public void saveSocre(String str)
	{
		try
		{
			if(str!=null)
			{
				ContentDAO dao = new ContentDAO(this.conn);
				ArrayList list = new ArrayList();
				String[] i_s=str.split(",");
				for(int i=0;i<i_s.length;i++)
				{
					String i_s_v=i_s[i];
					if(i_s_v==null|| "".equals(i_s_v)) {
                        continue;
                    }
					String[] i_v=i_s_v.split("/");
					StringBuffer buf = new StringBuffer();
					buf.append(" update per_standard_item set score=");
					buf.append((i_v[0]==null|| "".equals(i_v[0])?"0":i_v[0]));
					buf.append(" where UPPER(item_id)='"+i_v[1].toUpperCase()+"'");	
					list.add(buf.toString());
				}
				dao.batchUpdate(list);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

		private String writeTd(String context,int rowspan,String align,int width,String itemid,int colspan)
		{
			StringBuffer td=new StringBuffer("");
			td.append("\r\n<td class='RecordRow' valign='middle' align='"+align+"'");
		    td.append(" onclick='changeColor(\""+itemid+"\")' id='"+itemid+"'" );
			if(rowspan!=0) {
                td.append(" rowspan='"+(rowspan)+"' ");
            }
			td.append(" height='"+td_height+"' ");
			td.append(" colspan=\""+colspan+"\"");
			td.append("  width='"+width+"'");
			td.append(" >");
			td.append(context);
			td.append("</td>");
			return td.toString();
		}
	public HashMap getLeafParentLinkMap()
	{
		HashMap map = new HashMap();
		try
		{
			LazyDynaBean abean=null;
			//leafItemList
			for(int i=0;i<this.standarditemlist.size();i++)
			{
				abean=(LazyDynaBean)this.standarditemlist.get(i);
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
		for(int i=0;i<standarditemlist.size();i++)
		{
			a_bean=(LazyDynaBean)this.standarditemlist.get(i);
			String itemid=(String)a_bean.get("item_id");
			String parentid=(String)a_bean.get("parent_id");
			if(itemid.equals(parent_id))
			{
				list.add(abean);
				getParentItem(list,a_bean);
			}			
		}				
	}
	public ArrayList getStandardItemByPointid(String point_id)
	{
		ArrayList list = new ArrayList();
		try
		{
			String sql = "select * from per_standard_item where UPPER(point_id)='"+point_id.toUpperCase()+"' order by seq";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql);
			while(rs.next())
			{
				this.setIsHaveItem("0");
				String parentid=rs.getString("parent_id")==null?"":rs.getString("parent_id");
				String itemid=rs.getString("item_id");
				String child_id=rs.getString("child_id");
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("item_id",rs.getString("item_id"));
				bean.set("parent_id",rs.getString("parent_id")==null?"":rs.getString("parent_id"));
				bean.set("child_id",rs.getString("child_id")==null?"":rs.getString("child_id"));
				bean.set("itemdesc",rs.getString("itemdesc"));
				bean.set("seq",rs.getString("seq"));
				bean.set("score",rs.getString("score")==null?"":PubFunc.round(rs.getString("score"),2));
				bean.set("top_value",rs.getString("top_value")==null?"":PubFunc.round(rs.getString("top_value"),2));
				bean.set("bottom_value",rs.getString("bottom_value")==null?"":PubFunc.round(rs.getString("bottom_value"),2));
				bean.set("point_id",point_id);
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
	 * 取顺序好与主键号
	 * @param column
	 * @param tableName
	 * @param point_id
	 * @param type=1取顺序号=0取主键
	 * @return
	 */
	public int getMaxValueByCloumn(String column,String tableName)
	{
		int ret=1;
		try
		{
			StringBuffer buf = new StringBuffer();
			buf.append("select max("+column+") as "+column+" from ");
			buf.append(tableName);
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(buf.toString());
			while(rs.next())
			{
				ret=rs.getInt(column)+1;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return ret;
	}
	/**
	 * 设置孩子节点
	 * @param itemid
	 * @param child
	 */
	public void configChildId(int itemid,int child)
	{
		try
		{
			String sql = " select child_id from per_standard_item where item_id="+itemid;
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql);
			boolean flag=true;
			while(rs.next())
			{
				if(rs.getString("child_id")==null|| "".equals(rs.getString("child_id"))) {
                    flag=false;
                }
			}
			if(!flag)
			{
				sql=" update per_standard_item set child_id="+child+" where item_id="+itemid;
				dao.update(sql);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
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
	public void deleteStandardItem(String itemid)
	{
		try
		{
			String sql = "select item_id from per_standard_item where parent_id="+itemid;
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql);
			while(rs.next())
			{
				String del_sql="delete from per_standard_item where item_id="+itemid;
				dao.delete(del_sql, new ArrayList());
				String item_id=rs.getString("item_id");
				deleteStandardItem(item_id);
			}
			String del_sql="delete from per_standard_item where item_id="+itemid;
			dao.delete(del_sql, new ArrayList());
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public void configChild(String itemid)
	{
		RowSet rs = null;
		try
		{
			ContentDAO dao  = new ContentDAO(this.conn);
			StringBuffer buf  = new StringBuffer();
			String del_sql="";
			//该节点的父亲是否还有孩子节点
			buf.append("select item_id from per_standard_item where parent_id=(");
			buf.append("select item_id from per_standard_item where child_id='"+itemid+"')");
			rs = dao.search(buf.toString());
			if(rs.next())
			{
				del_sql="update per_standard_item set child_id=(select min(item_id) from per_standard_item where parent_id=(select item_id from per_standard_item where child_id='"+itemid+"'))";
				del_sql+=" where item_id=(select item_id from per_standard_item where child_id='"+itemid+"')";
			}else{
		    	del_sql="update per_standard_item set child_id=null where child_id='"+itemid+"' ";
			}
			dao.delete(del_sql, new ArrayList());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public void delete(String itemids)
	{
		try
		{
			if(itemids==null||itemids.trim().length()<=0)
			{
				return;
			}
			String item[] =itemids.split("`");
			StringBuffer buf=new StringBuffer();
			for(int i=0;i<item.length;i++)
			{
				if(i!=0) {
                    buf.append(",");
                }
				buf.append(item[i]);
			}
			String sql=" delete from per_standard_item where item_id in("+buf.toString()+")";
			ContentDAO dao = new ContentDAO(this.conn);
			dao.delete(sql, new ArrayList());

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public String getIsHaveItem() {
		return isHaveItem;
	}
	public void setIsHaveItem(String isHaveItem) {
		this.isHaveItem = isHaveItem;
	}
	public HashMap getRuleValue(String point_id)
	{
		HashMap map=null;
		try
		{
			String ctrlXML="";
			String sql = " select pointctrl from per_point where UPPER(point_id)='"+point_id.toUpperCase()+"'";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql);
			while(rs.next())
			{
				ctrlXML=Sql_switcher.readMemo(rs,"pointctrl");
			}
			PointCtrlXmlBo bo = new PointCtrlXmlBo();
			map=bo.getAttributeValues(ctrlXML);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	public void deleteBaseRuleRecord(String point_id)
	{
		try
		{
			String sql = "delete from per_standard_item where UPPER(point_id)='"+point_id.toUpperCase()+"'";
			ContentDAO dao = new ContentDAO(this.conn);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public void updatePointid(String point_id,ContentDAO dao)
	{
		try
		{
			String sql = " update per_standard_item set point_id='"+point_id+"' where UPPER(point_id)='XXXXPPPP'";
			dao.update(sql);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
