/*
 * 创建日期 2005-7-13
 *
 */
package com.hjsj.hrms.transaction.askinv;

import com.hjsj.hrms.actionform.askinv.EndViewForm;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
/**
 * @author luangaojiong
 *
 * 图形统计交易类
 * 
 */
public class SearchQuestionnaireEndViewTrans extends IBusiness {

	public void execute() throws GeneralException {
	  	
		/**
		 * 项目id ArrayList对象列表
		 * 
		 */	
		
	  	ArrayList endViewlist=(ArrayList)this.getFormHM().get("questionnarieEndView");
	  	  	
	  	if(endViewlist.size()<=0)
	  	{
	  		return;
	  	}
	  	
	  	/**
	  	 * 得到主题id
	  	 */
	  	 String id="0";
	  	 ContentDAO dao=new ContentDAO(this.getFrameconn());
	  	 String sql2="select id from investigate_item where itemid='"+endViewlist.get(0).toString()+"'";
	  	 
	  	 try
		 {
	  	 	this.frowset=dao.search(sql2);
	  	 	if(this.frowset.next())
	  	 	{
	  	 	 id=this.frowset.getString("id");
	  	 	}
		 }
	  	 catch(Exception ex)
		 {
	  	 	ex.printStackTrace();
		 }
	  	 /**
	  	  * 得到主题名称
	  	  */
	  	  try
		   {
	  	  	    String name="";
		   		this.frowset=dao.search("select * from investigate where id='"+id+"'");
		   		if(this.frowset.next())
		   		{
		   			name=this.frowset.getString("content");
		   		}
		   		this.getFormHM().put("name",name);
		   }
		   catch(Exception ex)
		   {
		   		ex.printStackTrace();
		   }	   
		   
		   Hashtable htpoint=new Hashtable();
		   Hashtable htpointnull = new Hashtable();
		   Hashtable htitem=new Hashtable();
		   try
		   {
		    /**得到所有要点hashtable*/
		    this.frowset=dao.search("select pointid,name ,itemid from investigate_point");
		    while(this.frowset.next())
		    {
		    	String pidtemp = this.frowset.getString("pointid");
				htpointnull.put(pidtemp, this.frowset.getString("itemid"));
				htpoint.put(pidtemp, this.frowset.getString("name"));
		    }
		    this.frowset.close();
		    /**得到所有项目hashtable*/		  
		    this.frowset=dao.search("select itemid,name from investigate_item");
		    while(this.frowset.next())
		    {
		    	htitem.put(this.frowset.getString("itemid"),this.frowset.getString("name"));
		    }
		   }
		   catch(Exception ex)
		   {
			   ex.printStackTrace();
		   }
		 
		 
	  	/**第二个要点调查循环* 
	  	 */
		   
	  	StringBuffer strsql=new StringBuffer();
	  	
	  	strsql.append("SELECT  count(investigate_result.pointid) as sumNum ,investigate_point.pointid as pointid,");
	  	strsql.append("investigate_point.itemid as itemid  ");
	  	strsql.append("FROM investigate_point "+Sql_switcher.left_join("investigate_point","investigate_result","investigate_point.pointid","investigate_result.pointid"));
	  	strsql.append(" where investigate_point.itemid in (select itemid from investigate_item where id ="+id+" ) group by investigate_point.pointid,investigate_point.itemid");
	    try
	    {
	    	String sumNumStr="0";
	       	ArrayList endviewlst=new ArrayList();
	        String pointidStr="";
	        String pointName="";
	        String itemidStr="";
	      
	        String sumNum="0";
	         
	      this.frowset = dao.search(strsql.toString());
	      while(this.frowset.next())
	      {
	          EndViewForm evf=new EndViewForm();        
	          String itemName="";
	          sumNum=this.getFrowset().getString("sumNum");
	          pointidStr=this.getFrowset().getString("pointid");
	          if(htpoint.containsKey(pointidStr))
	          {
	          	pointName=htpoint.get(pointidStr).toString();
	          }
	          else
	          {
	          	pointName="";
	          }
	          itemidStr=this.getFrowset().getString("itemid");
	          if(htitem.containsKey(itemidStr))
	          {
	          	itemName=htitem.get(itemidStr).toString();
	          }
	          else
	          {
	          	itemName="";
	          }
	          evf.setItemid(itemidStr);
	          evf.setItemName(itemName);
	          evf.setPointid(pointidStr);
	          evf.setPointName(pointName);
	          evf.setSumNum(sumNum);
	          endviewlst.add(evf);
	      }
	     // this.getFormHM().put("endviewlst",endviewlst);
	      //第一个项目循环
		  	 ArrayList itemwhilelst=new ArrayList();
		  	 try
			 {
		  	 	StringBuffer strsql2=new StringBuffer();
		  	 	
		  	 	strsql2.append("SELECT  ");
			  	strsql2.append("investigate_point.itemid as itemid  ");
			  	strsql2.append("FROM investigate_point "+Sql_switcher.left_join("investigate_point","investigate_result","investigate_point.pointid","investigate_result.pointid"));
			  	strsql2.append(" where investigate_point.itemid in (select itemid from investigate_item where id ="+id+" ) group by investigate_point.itemid");
			  		  	 	
		  	 	this.frowset=dao.search(strsql2.toString());
		  	 	
			  	while(this.frowset.next())
		  	 	{
		  	 	 EndViewForm evf=new EndViewForm(); 
		  	 	 String itemid=this.frowset.getString("itemid");
		  	 	 String itemName="";
		  	     if(htitem.containsKey(itemid))
		          {
		          	itemName=htitem.get(itemid).toString();
		          }
		          else
		          {
		          	itemName="";
		          }
		  	   	  evf.setItemid(itemid);
		  	   	  evf.setItemName(itemName);
		  	   	  
		  	   	  ArrayList lst=new ArrayList();
				  lst=(ArrayList)getSecondlst(itemid,endviewlst);
				  lst = doPointNull(itemid, htitem, htpointnull, htpoint, lst); //初始化没有点击的数据项
		  	   	  evf.setEndviewlst(lst);
		  	   	  evf.setPicList(getPicList(lst));
		  	   	  itemwhilelst.add(evf);
		  	 	}
		  	  this.getFormHM().put("itemwhilelst",itemwhilelst);
			 }
		  	 catch(Exception ex)
			 {
		  	 	ex.printStackTrace();
			 }
	     
	       
	    }
	    catch(Exception sqle)
	    {
	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }
	  }
		
	  //得到图像lst
	  public ArrayList getPicList(ArrayList lst)
	  {
	  	  ArrayList list=new ArrayList();
	  	  for(int i=0;i<lst.size();i++)
	  	  {
	  	    EndViewForm evf=new EndViewForm(); 
	  	  	evf=(EndViewForm)lst.get(i);
	        CommonData vo=new CommonData();
	        vo.setDataName(evf.getPointName());
	        vo.setDataValue(evf.getSumNum());
	        list.add(vo);
	  	  }
          return list;
	  }
	  
	  //得到要查找的lst内容
	  public ArrayList getSecondlst(String itemid,ArrayList plst)
	  {
	  	ArrayList ls=new ArrayList();
	  	for(int i=0;i<plst.size();i++)
	  	{
	  		EndViewForm evf=new EndViewForm(); 
	  		evf=(EndViewForm)plst.get(i);
	  		if(evf.getItemid().equals(itemid))
	  		{
	  			ls.add(evf);
	  		}
	  	}
	  	return ls;	  	
	  }
	  
	  	/**
		 * 
		 *  Title:判断查找的要点内容是否为空 
		 *   
		 */
		public ArrayList doPointNull(String itemid, Hashtable htitem,
				Hashtable htpointnull, Hashtable htpoint, ArrayList lst) {
			//获取某项目要点列表
			ArrayList list = new ArrayList();

			Enumeration x = htpointnull.keys();

			while (x.hasMoreElements()) {
				String typeid = x.nextElement().toString();

				if (htpointnull.get(typeid).toString().equals(itemid)) {
					if (!list.contains(typeid)) {
						list.add(typeid);
					}
					
				}

			}
			//end while

			//list为空时的判断及初始化
			if (lst.size() == 0 || lst == null) {
				lst = new ArrayList();
				for (int i = 0; i < list.size(); i++) {
					EndViewForm evf = new EndViewForm();
					String pointid = list.get(i).toString();
					String itemname = htitem.get(itemid).toString();
					String pointName = htpoint.get(pointid).toString();
					evf.setItemid(itemid);
					evf.setItemName(itemname);
					evf.setPointid(pointid);
					evf.setPointName(pointName);
					evf.setSumNum("0");
					lst.add(evf);

				}
			} else {
				if (list.size() > lst.size()) {
					for (int i = 0; i < list.size(); i++) {
						String pointid = list.get(i).toString();
						String flag = "0";
						for (int j = 0; j < lst.size(); j++) {
							EndViewForm evf = new EndViewForm();
							evf = (EndViewForm) lst.get(j);
							if (pointid.equals(evf.getPointid())) {
								flag = "1";
							}

						}

						if ("1".equals(flag)) {

						} else {

							EndViewForm evf = new EndViewForm();
							String itemname = htitem.get(itemid).toString();
							String pointName = htpoint.get(pointid).toString();
							evf.setItemid(itemid);
							evf.setItemName(itemname);
							evf.setPointid(pointid);
							evf.setPointName(pointName);
							evf.setSumNum("0");
							lst.add(evf);
						}

					}
				}
			}

			return lst;
		}
}
