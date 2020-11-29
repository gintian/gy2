/*
 * 创建日期 2005-6-23
 *
 */
package com.hjsj.hrms.transaction.performance;

import com.hjsj.hrms.actionform.performance.AppraiseselfForm;
import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.exception.GeneralException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Vector;
/**
 * 多级栏目处理帮助类
 * @author luangaojiong
 *
 */
public class PerforBeanMuilt  {

	Connection con = null;

	PreparedStatement statement;

	ResultSet resultset;

	String sql2 = "";
	int nowNum = 0;

	int maxNum = 0;
	public ArrayList itemlist2=new ArrayList();
			

	public void execute() throws GeneralException {
		
	}
	/**
	 * 得到项目叶子统计出要素个数后的ArrayList
	 * @param itemlist
	 * @param pointlist
	 * @return
	 */
	public ArrayList getPointNum(ArrayList itemlist,ArrayList pointlist)
	{
		ArrayList list =new ArrayList();
		for(int i=0;i<itemlist.size();i++)
		{
			AppraiseselfForm aftemp=doPontNum(((AppraiseselfForm)itemlist.get(i)),pointlist);
			list.add(aftemp);
		}
		
		return list;
	}
	/**
	 * 项目叶子元素统计处理函数
	 * @param af
	 * @param pointlist
	 * @return
	 */
	
	public AppraiseselfForm doPontNum(AppraiseselfForm af,ArrayList pointlist)
	{
		String itemid=af.getItemid();
		int num=0;
		for(int i=0;i<pointlist.size();i++)
		{
			AppraiseselfForm aft=(AppraiseselfForm)pointlist.get(i);
			if(aft.getItemid().equals(itemid))
			{
				++num;
			}
		}
		
		af.setLeafPointElementNum(num);
		
		return af;
		
		
	}
	/**
	 * 处理项目元素个数
	 * @param list
	 * @param af
	 * @param itemlist
	 * @return
	 */
	public AppraiseselfForm doItemPointNum(ArrayList list,AppraiseselfForm af,ArrayList itemlist)
	{
		if(list.size()<=1 && "1".equals(af.getNowMaxlay()))
		{
			return af;
		}
		else
		{
						
			String tempid="";
			int num=0;
			for(int i=0;i<list.size();i++)
			{
				for(int j=0;j<itemlist.size();j++)
				{
					if(((AppraiseselfForm)itemlist.get(j)).getItemid().equals(list.get(i).toString()))
					{
						num=num+((AppraiseselfForm)itemlist.get(j)).getLeafPointElementNum();
					}
				}
			}
			
			af.setLeafPointElementNum(num);
			//System.out.println("-com.hjsj.hrms.transaction.performance---PerforBeanMuilt-跨要素数目录-->"+af.getLeafPointElementNum());
		}
		
		
		return af;
	}
	
	/**
	 * 得到项目枝所包含的所有元素数
	 * @param itemlist
	 * @param maxNum
	 * @param template_id
	 * @param rootId
	 * @param pointlist
	 * @return
	 */
	public ArrayList  getItemPonintNum(ArrayList itemlist,int maxNum, String template_id,String rootId,ArrayList pointlist)
	{
		StringBuffer temp=new StringBuffer();
		ArrayList firstlist=new ArrayList();
		DbSecurityImpl dbS = new DbSecurityImpl();
		try
		{
			sql2="SELECT item_id,itemdesc FROM per_template_item WHERE parent_id= ? and template_id='"
			+ template_id + "' ORDER BY item_id ASC";
			StringBuffer menu = new StringBuffer();
			con= (Connection) AdminDb.getConnection();
			// 打开Wallet
			dbS.open(con, sql2);
			statement=con.prepareStatement(sql2);
			
			
			for(int i=0;i<itemlist.size();i++)
			{
				itemlist2=new ArrayList();
				AppraiseselfForm aft=doItemPointNum(MenuList(((AppraiseselfForm)itemlist.get(i)).getItemid(), "", 0, menu, true,pointlist),(AppraiseselfForm)itemlist.get(i),itemlist);
				
				firstlist.add(aft);
			}
		}
		catch(Exception ex){}
		finally
		{
			PubFunc.closeResource(resultset);
			PubFunc.closeResource(statement);
			// 关闭Wallet
			dbS.close(this.con);
			PubFunc.closeResource(con);
		}
		
		
		
		
		return firstlist;
		
	}
	
	/**
	 * 
	 * 递归遍历项目列表得到每一个项目的叶子数
	 */
	public ArrayList MenuList(String parentId, String name, int position,
			StringBuffer outHTML, boolean init,ArrayList pointlist) 
	{
		Vector childList = new Vector();
		Vector nameList = new Vector();
		
		try 
		{

				statement.setString(1, parentId);
				con= (Connection) AdminDb.getConnection();
				
				resultset = statement.executeQuery();
				while (resultset.next()) 
				{
					childList.addElement(resultset.getString("item_id"));
					nameList.addElement(resultset.getString("itemdesc"));

				}
		} 
		catch (Exception e) 
		{
			
		}

		int msize = childList.size();
		
		if (msize > 0)
		{	
			//System.out.println("-com.hjsj.hrms.transaction.performance---PerforBeanMuilt---->中间项目itemid号"+parentId);		
					
			for (int i = 0; i < msize; i++)
			{					
				MenuList((String) childList.elementAt(i), (String) nameList	.elementAt(i), i, outHTML, false,pointlist);
						    
			}
					
		} 
		else 
		{			
			//System.out.println("-com.hjsj.hrms.transaction.performance---PerforBeanMuilt---->未级项目itemid号"+parentId);
			
			itemlist2.add(parentId);
		}
		
		return itemlist2;
	}
	
	/**
	 *过滤字符为项目ArrayList中名称为空的值 
	 *
	 */
	
	public ArrayList clearItemlstNameIsNull(ArrayList itemlist)
	{
		 ArrayList list=new ArrayList();
		
		for(int i=0;i<itemlist.size();i++)
		{
			AppraiseselfForm af=(AppraiseselfForm)itemlist.get(i);
			if("".equals(af.getItemName()) || af.getItemName()==null)
			{
				
			}
			else
			{
				list.add(af);
			}
		
		}
		return list;
	}
	


}
