package com.hjsj.hrms.transaction.welcome;

import com.hjsj.hrms.actionform.welcome.WelcomeForm;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.math.BigDecimal;
import java.util.*;

/**
 * <p>
 * Title:调查分析
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2005-6-6:10:03:31
 * </p>
 * 
 * @author luangaojiong
 * @version 1.0
 *  
 */
public class WelcomeEndViewListTrans extends IBusiness {
    String flag = "false";

	public void execute() throws GeneralException {

			
		/**
		 * //取得调查表id及名称
		 */
		
		String id = "0";
		if(this.getFormHM().get("hotid")!=null)
		{
			id=this.getFormHM().get("hotid").toString();
		}
		else
		{
			return;
		}

		String name = "";

		ContentDAO dao = new ContentDAO(this.getFrameconn());
		/*
		 * //得到主题名
		 *  
		 */
		try {
			this.frowset = dao.search("select content from investigate where id='" + id
							+ "'");
			if (this.frowset.next()) {
				name = this.frowset.getString("content");
			}
		} catch (Exception ex) {

		}

		this.getFormHM().put("name", name);
		/*
		 * //判断项目是否存在
		 *  
		 */
//		try {
//			this.frowset = dao.search("select * from investigate_item where id='" + id
//							+ "'");
//			if (this.frowset.next()) {
//
//			} else {
//
//				//return;
//			}
//
//		} catch (Exception ex) {
//
//		} finally {
//			try {
//				this.frowset.close();
//			} catch (Exception ex) {
//
//			}
//		}

		LinkedHashMap htpoint=new LinkedHashMap();
        LinkedHashMap htpointnull = new LinkedHashMap();
		Hashtable htitem = new Hashtable();
		ArrayList resultlist=new ArrayList();	//结果表对象
		
		try {
			/**
			 * 得到结果表中的对象列表
			 */
			this.frowset=dao.search("select pointid,itemid,context from investigate_result");
			while(this.frowset.next())
			{
				DynaBean vo=new LazyDynaBean();
				vo.set("pointid",this.frowset.getString("pointid"));
				vo.set("itemid",this.frowset.getString("itemid"));
				vo.set("context",PubFunc.nullToStr(this.frowset.getString("context")));
				resultlist.add(vo);
			}
			
			/**
			 * //得到要点hashtable
			 *  
			 */
			this.frowset = dao.search("select pointid,name ,itemid from investigate_point");
			while (this.frowset.next()) {
				String pidtemp = this.frowset.getString("pointid");
				htpointnull.put(pidtemp, this.frowset.getString("itemid"));
				htpoint.put(pidtemp, this.frowset.getString("name"));

			}

			/**
			 * // 得到项目hashtable
			 *  
			 */
			this.frowset = dao.search("select itemid,name from investigate_item");
			while (this.frowset.next()) {
				htitem.put(this.frowset.getString("itemid"), this.frowset.getString("name"));

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		/*
		 * //第二个要点调查循环
		 *  
		 */
		
		StringBuffer strsql=new StringBuffer();
	  	strsql.append("SELECT  count(investigate_result.pointid) as sumNum ,investigate_point.pointid as pointid,");
	  	strsql.append("investigate_point.itemid as itemid  ");
	  	strsql.append("FROM investigate_point ");
		strsql.append(Sql_switcher.left_join("investigate_point","investigate_result","investigate_point.pointid","investigate_result.pointid")); //investigate_result ON  investigate_point.pointid = investigate_result.pointid ");
	  	strsql.append(" where investigate_point.itemid in (select itemid from investigate_item where id ='"+id+"' ) and state=2 group by investigate_point.pointid,investigate_point.itemid");
		ArrayList endviewlst = new ArrayList();
		String pointidStr = "";
		String pointName = "";
		String itemidStr = "";
		String sumNum = "0";
		try {
			
			
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
			while (this.frowset.next()) {
				WelcomeForm evf = new WelcomeForm();
				String itemName = "";
				sumNum = this.getFrowset().getString("sumNum");
				pointidStr = this.getFrowset().getString("pointid");
				if (htpoint.containsKey(pointidStr)) {
					pointName = htpoint.get(pointidStr).toString();
				} else {
					pointName = "";
				}
				itemidStr = this.getFrowset().getString("itemid");
				if (htitem.containsKey(itemidStr)) {
					itemName = htitem.get(itemidStr).toString();
				} else {
					itemName = "";
				}
				
				  String percent="0%";
		          String count=(String)percentMap.get(this.frowset.getString("itemid"));
		          if(!"0".equals(count))
		          {
		        	  BigDecimal a=new BigDecimal(count);
		        	  BigDecimal b=new BigDecimal(sumNum);
		        	  percent=b.divide(a,2,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")).divide(new BigDecimal("1"),0,BigDecimal.ROUND_HALF_UP).toString()+"%";  
		          }
				
				
				evf.setItemid(itemidStr);
				evf.setItemName(itemName);
				evf.setPointid(pointidStr);
				evf.setPointName(pointName);
				evf.setSumNum(sumNum);
				evf.setPrecent(percent);
				evf.setConextFlag(getResultTxtFlag(resultlist,pointidStr,itemidStr));
				endviewlst.add(evf);

			}
		} catch (Exception sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		} 
		     /*
			 * //第一个项目循环
			 *  
			 */
			ArrayList itemwhilelst = new ArrayList();
			try {
				StringBuffer strsql2=new StringBuffer();
		  	 	
		  	 	strsql2.append("SELECT  ");
			  	strsql2.append("investigate_point.itemid as itemid  ");
			  	strsql2.append("FROM investigate_point ");
			  	strsql2.append(Sql_switcher.left_join("investigate_point","investigate_result","investigate_point.pointid","investigate_result.pointid"));//LEFT OUTER JOIN  investigate_result ON  investigate_point.pointid = investigate_result.pointid ");
			  	strsql2.append(" where investigate_point.itemid in (select itemid from investigate_item where id ='"+id+"' ) group by investigate_point.itemid");
			  //  cat.debug("------>WelcomeEndViewListTrans-->第一个循环开始 sql "+strsql2.toString());	  	 	
			  //	System.out.println("------>WelcomeEndViewListTrans-->第一个循环开始 sql "+strsql2.toString());
			  	
			  	this.frowset=dao.search(strsql2.toString());
				if (this.frowset.next()) {
					WelcomeForm evf = new WelcomeForm();
					String itemid = this.frowset.getString("itemid");
					String itemName = "";
					if (htitem.containsKey(itemid)) {
						itemName = htitem.get(itemid).toString();
					} else {
						itemName = "";
					}
					evf.setItemid(itemid);
					evf.setItemName(itemName);
					ArrayList lst = new ArrayList();
					lst = (ArrayList) getSecondlst(itemid, endviewlst);
					lst = doPointNull(itemid, htitem, htpointnull, htpoint, lst); //初始化没有点击的数据项
					evf.setEndviewlst(lst);
					evf.setPicList(getPicList(lst));
					evf.setChartFlag(flag);
					itemwhilelst.add(evf);
				}
				this.getFormHM().put("itemwhilelst", itemwhilelst);
			} catch (Exception ex) {
				throw GeneralExceptionHandler.Handle(ex);
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

	/*
	 * 得到图像lst
	 *  
	 */
	public ArrayList getPicList(ArrayList lst) {
		ArrayList list = new ArrayList();
		for (int i = 0; i < lst.size(); i++) {
			WelcomeForm evf = new WelcomeForm();
			evf = (WelcomeForm) lst.get(i);
			CommonData vo = new CommonData();
			vo.setDataName(evf.getPointName());
			vo.setDataValue(evf.getSumNum());
			if(!"0".equals(evf.getSumNum()))
			    flag = "true";
			list.add(vo);

		}

		return list;
	}

	/**
	 * 得到项目对应的要点列表lst内容
	 *  
	 */
	public ArrayList getSecondlst(String itemid, ArrayList plst) {
		ArrayList ls = new ArrayList();
		for (int i = 0; i < plst.size(); i++) {
			WelcomeForm evf = new WelcomeForm();
			evf = (WelcomeForm) plst.get(i);
			if (evf.getItemid().equals(itemid)) {
				ls.add(evf);
			}

		}

		return ls;
	}

	/**
	 * 
	 *  Title:判断查找的要点内容是否为空 
	 *  create time:2005-6-7:10:32:04 
	 *  @author luangaojiong
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
				WelcomeForm evf = new WelcomeForm();
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
						WelcomeForm evf = new WelcomeForm();
						evf = (WelcomeForm) lst.get(j);
						if (pointid.equals(evf.getPointid())) {
							flag = "1";
						}

					}

					if ("1".equals(flag)) {

					} else {

						WelcomeForm evf = new WelcomeForm();
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