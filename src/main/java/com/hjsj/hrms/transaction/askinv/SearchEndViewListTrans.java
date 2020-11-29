/*
 * Created on 2005-6-1
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.askinv;

import com.hjsj.hrms.actionform.askinv.EndViewForm;
import com.hjsj.hrms.actionform.welcome.WelcomeForm;
import com.hjsj.hrms.businessobject.train.EvaluatingToExcelBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.pagination.PaginationManager;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchEndViewListTrans extends IBusiness {
    
	public void execute() throws GeneralException {
	  	 HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
	  	 
	  	 ArrayList itemtxtlist=new ArrayList();  //项目对象id
	  	 if(hm.get("f")!=null)
	  	 {
	  	 	this.getFormHM().put("f",hm.get("f").toString());
	  	 }
	  	 
	  	 //取得调查表id及名称
	  	 String id=(String)hm.get("id");
	  	 if(id==null || "".equals(id))
	  	 {
	  	 	if(this.getFormHM().get("id")!=null)
	  	 	{
	  	 		id=this.getFormHM().get("id").toString();
	  	 	}
	  	 	else
	  	 	{
	  	 		id="0";
	  	 	}
	  	 }
	  	 else
	  	 {
	  		 id=PubFunc.decryption(id);
	  	    this.getFormHM().put("id",id);
	  	 }
	  	 
	  	String name=(String)hm.get("name");
	  	if(!(name==null || "".equals(name)))
	  	{
	  		try
			{
	  			name=ChangeStr.ToGbCode(name);
			}
	  		catch(IOException ex)
			{
	  			ex.printStackTrace();
			}
	  		this.getFormHM().put("name",name);	  		
	  	}
	  	
	  	
	  	
		  	StringBuffer sqlStr = new StringBuffer();
			ArrayList values = new ArrayList();
			values.add(id);
			
	  	   ContentDAO dao=new ContentDAO(this.getFrameconn());
		   try
		   {
		   		/**得到主题名*/
		   		this.frowset=dao.search("select content from investigate where id="+id);
		   		if(this.frowset.next())
		   		{
		   			name=PubFunc.nullToStr(this.frowset.getString("content"));
		   		}
		   		this.getFormHM().put("name",name);
		   	
		   		/**得到问答项目列表*/
		   	   
		   		HashMap itemMap = new HashMap();
		   		
		   		StringBuffer itemIds = new StringBuffer();
		   		
		   		sqlStr.setLength(0);
		   		sqlStr.append("select itemid,name,fillflag from investigate_item where id='");
		   		sqlStr.append(id);
		   		sqlStr.append("' and status=1 order by investigate_item.itemid");
		   		
		   		//System.out.println("---->SearchEndViewListTrans-->sql1-->"+sbtp.toString());
		   		cat.debug("---->SearchEndViewListTrans-->sql1-->"+sqlStr.toString());
		   		this.frowset=dao.search(sqlStr.toString());
		   		while(this.frowset.next())
		   		{
		   			WelcomeForm wf=new WelcomeForm();
		   			String itemid=PubFunc.NullToZero(this.frowset.getString("itemid"));
		   			String itemname=PubFunc.nullToStr(this.frowset.getString("name"));
		   			wf.setItemName(itemname);
		   			wf.setItemid(itemid);
		   			String fillflag="0";
		   			if(this.frowset.getString("fillflag")!=null&&!"".equals(this.frowset.getString("fillflag")))
		   				fillflag=this.frowset.getString("fillflag");
		   			wf.setFillflag(fillflag);
		   			itemtxtlist.add(wf);
		   			itemIds.append(itemid);
		   			itemIds.append(",");
		   			
		   			
		   		}
		   		
		   		String[] itemidArray = itemIds.toString().split(",");
		   		String[] fields = new String[]{"staff_id","context"};
		   		PaginationManager paginationm =null;
		   		for(int i=0;i<itemidArray.length;i++){
		   		//得到问答题答案
		   			sqlStr.setLength(0);
		   			sqlStr.append("select staff_id,context from investigate_content where investigate_content.state=2 and investigate_content.itemid='");
		   			sqlStr.append(itemidArray[i]);
		   			sqlStr.append("'");
		   	        
		   	        //只取前20条
			        paginationm=new PaginationManager(sqlStr.toString(),"","","",fields,"");
			        paginationm.setBAllMemo(true);
			        paginationm.setPagerows(20);
			        List data = paginationm.getPage(1);
			        
		   	        ArrayList itemwhilelst = new ArrayList();
		   	        for(int j =0;j<data.size();j++) 
		            {   
		   	        	    DynaBean db = (DynaBean)data.get(j);
		   	        		WelcomeForm wf=new WelcomeForm();
		                String itemName=PubFunc.nullToStr((String)db.get("name"));
		                wf.setItemName(itemName);
		                //this.getFormHM().put("name",itemName);
		                wf.setUserName(PubFunc.nullToStr((String)db.get("staff_id")));
		                wf.setContext(PubFunc.reverseHtml(PubFunc.nullToStr((String)db.get("context"))));
		                itemwhilelst.add(wf);
		            }
		   	        itemMap.put(itemidArray[i], itemwhilelst);
		   		}
		   		
		   		this.getFormHM().put("itemMap", itemMap);
		   		this.getFormHM().put("itemtxtlist",itemtxtlist);
		   }
		   catch(Exception ex)
		   {
		   		ex.printStackTrace();
		   }
		   
		   LinkedHashMap htpoint=new LinkedHashMap();
		   LinkedHashMap htpointnull = new LinkedHashMap();
	       Hashtable htitem=new Hashtable();
	       Hashtable fillflag= new Hashtable();
	       ArrayList resultlist=new ArrayList();	//结果表对象
		   try
		   {
			   
			
			
			
			/**得到结果表中的对象列表 */
			sqlStr.setLength(0);
			sqlStr.append(" select a.pointid,a.itemid,a.context from investigate_result a left join investigate_item b on a.itemid=b.itemid");
			sqlStr.append(" left join investigate c on b.id=c.id where c.id=? ");
			
		   	this.frowset=dao.search(sqlStr.toString(),values);
			while(this.frowset.next())
			{
				DynaBean vo=new LazyDynaBean();
				vo.set("pointid",PubFunc.NullToZero(this.frowset.getString("pointid")));
				vo.set("itemid",PubFunc.NullToZero(this.frowset.getString("itemid")));
				//vo.set("context",PubFunc.nullToStr(this.frowset.getString("context")));
				vo.set("context",PubFunc.nullToStr(Sql_switcher.readMemo(this.frowset,"context")));
				resultlist.add(vo);
			}

			sqlStr.setLength(0);
			sqlStr.append(" select a.pointid,a.name ,a.itemid from investigate_point a left join investigate_item b on a.itemid=b.itemid");
			sqlStr.append(" left join investigate c on b.id=c.id where c.id=? order by pointid ");
		    //得到要点hashtable
		    this.frowset=dao.search(sqlStr.toString(),values);
		    while(this.frowset.next())
		    {
		    	String pidtemp = PubFunc.NullToZero(this.frowset.getString("pointid"));
				htpointnull.put(pidtemp, PubFunc.NullToZero(this.frowset.getString("itemid")));
				htpoint.put(pidtemp, PubFunc.nullToStr(this.frowset.getString("name")));
		    }

		    //		  得到项目hashtable
		    sqlStr.setLength(0);
			sqlStr.append(" select itemid,name,fillflag from investigate_item where id=? order by itemid");
		    this.frowset=dao.search(sqlStr.toString(),values);
		    while(this.frowset.next())
		    {
		    	htitem.put(PubFunc.NullToZero(this.frowset.getString("itemid")),PubFunc.NullToZero(this.frowset.getString("name")));
		    	fillflag.put(PubFunc.NullToZero(this.frowset.getString("itemid")),PubFunc.NullToZero(this.frowset.getString("fillflag")));
		    }
		   }
		   catch(Exception ex)
		   {
			   ex.printStackTrace();
		   }
		 
		 
	  	//第二个要点调查循环
		StringBuffer strsql=new StringBuffer();
	  	strsql.append("SELECT  count(investigate_result.pointid) as sumNum ,investigate_point.pointid as pointid,");
	  	strsql.append("investigate_point.itemid as itemid  ");
	  	strsql.append("FROM investigate_point ");
		strsql.append(Sql_switcher.left_join("investigate_point","investigate_result","investigate_point.pointid","investigate_result.pointid")); 
	  	strsql.append(" where investigate_point.itemid in (select itemid from investigate_item where id ='"+id+"' ) and state=2 group by investigate_point.pointid,investigate_point.itemid ");
	  //	System.out.println("sql-->"+strsql.toString());
		cat.debug("---->SearchEndViewListTrans-->sql-->"+strsql.toString());
	    try
	    {
	    	
	    	String sumNumStr="0";
	       	ArrayList endviewlst=new ArrayList();
	        String pointidStr="";
	        String pointName="";
	        String itemidStr="";
	        
	        String sumNum="0";
	         
	      ////////////////   取得百分比   /////
	        HashMap percentMap=new HashMap();
	        StringBuffer strsql3=new StringBuffer();
		  	strsql3.append("SELECT  count(investigate_result.pointid) as sumNum ,investigate_point.itemid as itemid  ");
		  	strsql3.append("FROM investigate_point ");
			strsql3.append(Sql_switcher.left_join("investigate_point","investigate_result","investigate_point.pointid","investigate_result.pointid")); 
		  	strsql3.append(" where investigate_point.itemid in (select itemid from investigate_item where id ='"+id+"' ) and investigate_result.state=2 group by investigate_point.itemid");		
	        this.frowset = dao.search(strsql3.toString());
	        while(this.frowset.next())
	        {
	        	String itemid=this.frowset.getString("itemid");
	        	String  count=this.frowset.getString("sumNum");
	        	percentMap.put(itemid,count);
	        }
	      ////////////////////////////////// 
	        
	      this.frowset = dao.search(strsql.toString()+"  order by itemid,pointid");
	      while(this.frowset.next())
	      {

	          EndViewForm evf=new EndViewForm();        
	          String itemName="";
	          itemidStr=PubFunc.NullToZero(this.frowset.getString("itemid"));
	          sumNum=PubFunc.NullToZero(this.frowset.getString("sumNum"));
	          pointidStr=PubFunc.NullToZero(this.frowset.getString("pointid"));
	          
	          String percent="0%";
	          String count=(String)percentMap.get(this.frowset.getString("itemid"));
	          if(!"0".equals(count))
	          {
	        	  BigDecimal a=new BigDecimal(count);
	        	  BigDecimal b=new BigDecimal(sumNum);
	        	  percent=b.divide(a,2,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")).divide(new BigDecimal("1"),0,BigDecimal.ROUND_HALF_UP).toString()+"%";  
	          }
	          
	          if(htpoint.containsKey(pointidStr))
	          {
	          	pointName=htpoint.get(pointidStr).toString();
	          }
	          else
	          {
	          	pointName="";
	          }
	          
	          if(htitem.containsKey(itemidStr))
	          {
	          	itemName=htitem.get(itemidStr).toString();
	          }
	          else
	          {
	          	itemName="";
	          }
	          String fillflagstr="0";
	          if(fillflag.containsKey(itemidStr))
	          {
	        	  fillflagstr=fillflag.get(itemidStr).toString();
	          }
	          evf.setItemid(itemidStr);
	          evf.setItemName(itemName);
	          evf.setPointid(pointidStr);
	          evf.setPointName(pointName);
	          evf.setSumNum(sumNum);
	          evf.setFillflag(fillflagstr);
	          evf.setConextFlag(getResultTxtFlag(resultlist,pointidStr,itemidStr));
	          evf.setPrecent(percent);
	          endviewlst.add(evf);
	         	              
	      }
	     // this.getFormHM().put("endviewlst",endviewlst);
	    
	      //第一个项目循环
		  	 ArrayList itemwhilelst=new ArrayList();
		  	 ArrayList allList = new ArrayList();
		  	 try
			 {
		  	 	StringBuffer strsql2=new StringBuffer();
		  	 	
		  	 	strsql2.append("SELECT  ");
			  	strsql2.append("investigate_point.itemid as itemid  ");
			  	strsql2.append("FROM investigate_point ");
			  	strsql2.append(Sql_switcher.left_join("investigate_point","investigate_result","investigate_point.pointid","investigate_result.pointid"));
			  	strsql2.append(" where investigate_point.itemid in (select itemid from investigate_item where id ='"+id+"' ) group by investigate_point.itemid order by investigate_point.itemid");
			//	System.out.println("sql2-->"+strsql2.toString());
			  	cat.debug("---->SearchEndViewListTrans-->sql2-->"+strsql2.toString());
		  	 	this.frowset=dao.search(strsql2.toString());
			  	while(this.frowset.next())
		  	 	{
		  	 	 EndViewForm evf=new EndViewForm(); 
		  	 	 String itemid=PubFunc.NullToZero(this.frowset.getString("itemid"));
		  	 	 String itemName="";
		  	   if(htitem.containsKey(itemid))
		          {
		          	itemName=htitem.get(itemid).toString();
		          }
		          else
		          {
		          	itemName="";
		          }
		  	   String fill = "0";
		  	   if(fillflag.containsKey(itemid))
		  	   {
		  		   fill = fillflag.get(itemid).toString();
		  	   }
		  	   evf.setFillflag(fill);
		  	   	  evf.setItemid(itemid);
		  	   	  evf.setItemName(itemName);
		  	   	 ArrayList lst=new ArrayList();
				 lst=(ArrayList)getSecondlst(itemid,endviewlst);
				 lst = doPointNull(itemid, htitem, htpointnull, htpoint, lst); //初始化没有点击的数据项
		  	   	  evf.setEndviewlst(lst);
		  	   	 
		  	   	  evf.setPicList(getPicList(lst));
		  	      allList.add(getPicList(lst));
		  	   	  itemwhilelst.add(evf);
		  	    
		  	   
		  	 	}
			  //	System.out.println("---SearchEndViewListTrans-->"+itemwhilelst.size());
			  	
		  	   this.getFormHM().put("itemwhilelst",itemwhilelst);
		  	  this.getFormHM().put("allList", allList);
		  	  
		  	  
			  	String opt=(String)this.getFormHM().get("opt");
			  	if(opt!=null&& "2".equals(opt))
			  	{
			  		EvaluatingToExcelBo bo=new EvaluatingToExcelBo(this.getFrameconn());
			  		String outName=bo.getInvestigateExcel(itemwhilelst,itemtxtlist,name);  
			  		outName=outName.replaceAll(".xls","#");
			  		this.getFormHM().put("outName",outName);
			  	  
			  	}
		  	  
		  	 	
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
	
	/**
	 * 判断要点描述所有的是否为空
	 * @param lst
	 * @return
	 */
	private String getResultTxtFlag(ArrayList list,String pointid,String itemid)
	{
		String flag="0";
		
		for(int i=0;i<list.size();i++)
		{
			DynaBean  vo=(DynaBean)list.get(i);
			if(vo.get("pointid").toString().equals(pointid)
				&& vo.get("itemid").toString().equals(itemid)
				&& !"".equals(vo.get("context").toString().trim()))
			{
				flag="1";
				break;
			}
		}
		
		return flag;
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
	  
	  /*
		 * 
		 *  Title:判断查找的要点内容是否为空 
		 * 
		 * 
		 *  
		 */
		public ArrayList doPointNull(String itemid, Hashtable htitem,
		        LinkedHashMap htpointnull, LinkedHashMap htpoint, ArrayList lst) {
			//获取某项目要点列表
			ArrayList list = new ArrayList();

			Set mapSet =  htpointnull.keySet();
			for (Iterator t = mapSet.iterator(); t.hasNext();){
			    String set = (String)t.next();
			    String typeid = (String)htpointnull.get(set);
				if (typeid.equals(itemid)) {
					if (!list.contains(set)) {
						list.add(set);
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
