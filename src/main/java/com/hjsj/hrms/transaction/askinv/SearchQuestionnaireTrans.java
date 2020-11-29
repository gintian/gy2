/*
 * 创建日期 2005-7-12
 *
 */
package com.hjsj.hrms.transaction.askinv;

import com.hjsj.hrms.actionform.welcome.WelcomeForm;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * @author luangaojiong
 * 问卷调查交易类
 */
public class SearchQuestionnaireTrans extends IBusiness {
	
	private String visibleUserName="";

	/**
	 * 
	 */
	public void execute() throws GeneralException {
		
		/**首页调用调查操作*/
		try
		{
			visibleUserName = SystemConfig.getPropertyValue("investigate_visible_username");
				if(visibleUserName==null|| "".equals(visibleUserName))
					visibleUserName="false";
			exeInvestigate();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	/***
	 * 
	 * @author luangaojiong
	 * 执行问卷调查操作
	 */
//	执行调查操作*****************************************************

	public void exeInvestigate() throws GeneralException {

		String temp = "";
		String id = "0";

		ContentDAO dao = new ContentDAO(this.getFrameconn());
         
		try {
			String columnCount=SystemConfig.getPropertyValue("inv_align");
			if(columnCount==null||columnCount.trim().length()<=0)
				columnCount="2";
			this.getFormHM().put("columnCount",columnCount);
			/**取得所有有效要点列表*/
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String ids=PubFunc.decryption((String)hm.get("id"));
			String home="4";
			String enteryType="0";
			String isClose="0";
			if(hm.get("enteryType")!=null)
			{
				enteryType=(String)hm.get("enteryType");
			}else
			{
				enteryType=(String)this.getFormHM().get("enteryType");
			}
			if(hm.get("home")!=null)
			{
				home=(String)hm.get("home");
			}
			else
			{
				home=(String)hm.get("home");
			}
			if(hm.get("isClose")!=null)
			{
				isClose=(String)hm.get("isClose");
			}else
			{
				isClose=(String)hm.get("isClose");
			}
			this.getFormHM().put("home", home==null?"4":home);
			this.getFormHM().put("enteryType", enteryType==null?"1":enteryType);
			this.getFormHM().put("isClose", isClose==null?"0":isClose);
			
			
			/**
			 * guodd 2015-12-02
			 * 一条sql查出所有的选项及试题
			 */
			StringBuffer  sql = new StringBuffer();
			sql.append(" select a.id,a.content,a.description,b.*,c.pointid,c.name pointname,c.describestatus from investigate a left join investigate_item b");
			sql.append(" on a.id = b.id LEFT JOIN investigate_point c on b.itemid=c.itemid ");
			sql.append(" where a.id=? and a.flag=1 and a.status=1 order by b.itemid,c.pointid");
			
			ArrayList values = new ArrayList();
			values.add(ids);
			
			this.frowset = dao.search(sql.toString(), values);
			
			//问卷对象
			WelcomeForm mainWf = null;
			
			//保存试题的有序map
			LinkedHashMap itemMap = new LinkedHashMap();
			
			//预存 试题id，用于区分 选项属于哪个试题
			String saveitemid = "";
			
			// 选项List
			ArrayList pointList = new ArrayList();
			
	        while(this.frowset.next()){
	        	
	        		//如果 问卷对象为空，说明是第一次进入，生成问卷对象。因为问卷是唯一（问卷id）的，只需走一次就可以
	        	    if(mainWf==null){
	        	    		mainWf = new WelcomeForm();
	        	    		id = this.frowset.getString("id");
	    				if (id == null || "".equals(id)) {
	    					mainWf.setId("0");
	    					id = "0";
	    				} else {
	    					mainWf.setId(id);
	    				}
	    				mainWf.setMdid(PubFunc.encryption(id));
	    				String description=Sql_switcher.readMemo(this.frowset,"description");
	    				if(description!=null&&description.length()>0)
	    					mainWf.setDescription(description);
	    				temp = this.frowset.getString("content");
	    				if (temp == null || "".equals(temp)) {
	    					mainWf.setName("");
	    				} else {
	    					mainWf.setName(temp);
	    				}
	        	    }
	        	    
	        	    /**
	        	     * 生成题目对象  
	        	     */
	        	    
	        	    //题目id
	        	    String itemid = this.frowset.getString("itemid");
	        	    
	        	    //如果 题目集合（itemMap）里有此题目了，不再添加。如果没有，添加题目对象
	        	    if(!itemMap.containsKey(itemid)){
	        	    		WelcomeForm itemWf = new WelcomeForm();
	    				temp = this.frowset.getString("itemid");
	    				if (temp == null || "".equals(temp)) {
	    					itemWf.setItemid("0");
	    				} else {
	    					itemWf.setItemid(temp);
	    				}
	    				itemWf.setItemContext("itemid¤"+temp);			//项目问答名称
	    				itemWf.setItemStatus(PubFunc.NullToZero(this.frowset.getString("status")).trim());		//单选、多选、问答标识
	    				
	    				String idtemp = this.frowset.getString("id");
	    				if (idtemp == null || "".equals(idtemp)) {
	    					idtemp = "0";
	    				}
	    				itemWf.setId(idtemp);
	    				temp = "";
	    				temp = this.frowset.getString("name");
	    				if (temp == null || "".equals(temp)) {
	    					itemWf.setItemName("");
	    				} else {
	    					itemWf.setItemName(temp);
	    				}
	    				temp = this.frowset.getString("fillflag");
	    				if(temp==null|| "".equals(temp))
	    				{
	    					temp="0";
	    				}
	    				itemWf.setFillflag(temp);
	    				itemWf.setSelects(this.frowset.getString("selects"));
	    				itemWf.setMinvalue(this.frowset.getString("minvalue"));
	    				itemWf.setMaxvalue(this.frowset.getString("maxvalue"));
	    				//将题目对象保存到题目集合里，题目id为key
	    				itemMap.put(itemid, itemWf);
	        	    }
	        	    
	        	    
	        	    /**
	        	     * 生成 选项 对象
	        	     * 逻辑说明：
	        	     * 每创建一个选项对象，会保存到pointList集合中，并会将此选项所属题目id保存到变量saveitemid中。
	        	     * 如果此次选项所属题目id和saveitemid不同，说明换题了，通过saveitemid从itemMap查找处上一道题目，
	        	     * 并将pointList放到题目对象中。到此上一道题目的所有选项已经检索完毕了，清空pointList对象，继续
	        	     * 保存当前题目的选项
	        	     */
	        	    
	        	    // 如果 上一个选项所属题目id（saveitemid） 不等于 本选项所属题目id 并且 saveitemid为空（只有第一次为空），
	        	    // 说明已经换题了,将 选项集合（pointList）保存进上一个题目选项中
	        	    if((!saveitemid.equals(itemid)) && pointList.size()>0 && saveitemid.length()>0){
	        	    		WelcomeForm itemWf = (WelcomeForm)itemMap.get(saveitemid);
	        	    		itemWf.setPointList(pointList);
	        	    		//清空 选项集合
	        	    		pointList = new ArrayList();
	        	    }
	        	    
	        	    //如果pointid为空，说明不是选择题，跳过
	        	    if(this.frowset.getString("pointid")==null || this.frowset.getString("pointid").length()<1){
	        	    	   continue;
	        	    }
	        	    
	        	    //创建选项对象
	        	    WelcomeForm pointWf = new WelcomeForm();
				String pointid=this.frowset.getString("pointid");
				pointWf.setPointid(pointid);
				pointWf.setPointContext("☆"+itemid+"☆"+pointid);		//要点描述
				pointWf.setItemid(itemid);
				pointWf.setMultitem("¤"+itemid);		//多选单选名称
				pointWf.setPointName(this.frowset.getString("pointname"));
				pointWf.setDescribestatus(PubFunc.NullToZero(this.frowset.getString("describestatus")));
				pointList.add(pointWf);
				//保存当前题目id
				saveitemid = itemid;
	        }

	        
	       /**
	        * 当上面的所有数据循环完毕，如果最后一道题是选择题，就无法判断是否换题了。pointList无法放入题目中。
	        * 如果最后不是选择题，那么pointList肯定早清空了。
	        * 所以判断如果pointList不为空，那么将pointList放入到对于的题目中
	        */
	        if(pointList.size()>0){
		        	WelcomeForm itemWf = (WelcomeForm)itemMap.get(saveitemid);
		    		itemWf.setPointList(pointList);
	        }
	        
			ArrayList itemList = new ArrayList(itemMap.values());
			mainWf.setItemwhilelst(itemList);
			ArrayList topicList = new ArrayList();
			topicList.add(mainWf);
			this.getFormHM().put("topicList", topicList);
			
			ArrayList answerList = new ArrayList();
			answerList = getAnswers(itemList);
			this.getFormHM().put("answerList", answerList);
			
			ArrayList answerDesc = new ArrayList();
			answerDesc = getAnswersDesc(itemList);
			this.getFormHM().put("answerDesc", answerDesc);
			
			ArrayList essayDesc = new ArrayList();
			essayDesc = getEssayDesc(itemList);
			this.getFormHM().put("essayDesc", essayDesc);
			String state = "0";
			String str = "";
			if(answerList.size()>0)
				str = " select state from investigate_result where itemid=? and staff_id=? and create_user=?";
			else if(essayDesc.size()>0)
				str = " select state from investigate_content where itemid=? and staff_id=? and create_user=?";
			if(!"".equals(str))
				state = getState(itemList,str);
			this.getFormHM().put("state", state);
			
			
			//得到项目标题
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
	/**
	 * 
	 * @Title: getState   
	 * @Description: 得到保存或者提交的状态   
	 * @param @param itemlist
	 * @param @param sql
	 * @param @return 
	 * @return String    
	 * @throws
	 */
	String getState(ArrayList itemlist,String sql){
		
		String state = "0";
		try {
		    ContentDAO dao = new ContentDAO(frameconn);
		    
			for(int i=0;i<itemlist.size();i++)
			{
				WelcomeForm wf=(WelcomeForm)itemlist.get(i);
				String itemid=wf.getItemid();
				
				ArrayList values = new ArrayList();
				values.add(itemid);
				values.add("false".equalsIgnoreCase(visibleUserName)?this.userView.getUserName():this.userView.getUserFullName());
				values.add(this.userView.getUserName());
				
				this.frecset = dao.search(sql, values);
				while(this.frecset.next()){
					state = frecset.getString("state");
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return state;
	}
	/**
	 * 获取问答题的答案
	 * @Title: getEssayDesc   
	 * @Description:    
	 * @param @param itemlist
	 * @param @return 
	 * @return ArrayList    
	 * @throws
	 */
	ArrayList getEssayDesc(ArrayList itemlist){
	    ArrayList list = new ArrayList();
	    
		StringBuffer sql = new StringBuffer();
		sql.append(" select context from investigate_content where staff_id=? and itemid=?   and create_user=?");
		try {
		    ContentDAO dao = new ContentDAO(frameconn);
		    
			for(int i=0;i<itemlist.size();i++)
			{
				WelcomeForm wf=(WelcomeForm)itemlist.get(i);
				String itemid=wf.getItemid();
				
		        ArrayList values = new ArrayList();
		        values.add("false".equalsIgnoreCase(visibleUserName)?this.userView.getUserName():this.userView.getUserFullName());
		        values.add(itemid);
		        values.add(this.userView.getUserName());
		        
				this.frecset = dao.search(sql.toString(), values);
				while(this.frecset.next()){
					if(frecset.getString("context") != null && !"".equals(frecset.getString("context"))){
						list.add(itemid+"`"+frecset.getString("context"));
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 
	 * @Title: getAnswersDesc   
	 * @Description: 获取选择题的描述信息  
	 * @param @param itemlist
	 * @param @return 
	 * @return ArrayList    
	 * @throws
	 */
	ArrayList getAnswersDesc(ArrayList itemlist){
		StringBuffer sql = new StringBuffer();
		ArrayList list=new ArrayList();
		sql.append(" select pointid,context from investigate_result where staff_id=? and itemid=?   and create_user=?");
		try {
		    ContentDAO dao = new ContentDAO(frameconn);
		    
			for(int i=0;i<itemlist.size();i++)
			{
				WelcomeForm wf=(WelcomeForm)itemlist.get(i);
				String itemid=wf.getItemid();
				
				ArrayList values = new ArrayList();
				values.add("false".equalsIgnoreCase(visibleUserName)?this.userView.getUserName():this.userView.getUserFullName());
				values.add(itemid);
				values.add(this.userView.getUserName());
				
				this.frecset = dao.search(sql.toString(), values);
				while(this.frecset.next()){
					if(frecset.getString("context") != null && !"".equals(frecset.getString("context")))
						list.add(frecset.getString("pointid")+"`"+frecset.getString("context"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	
	/**
	 * 
	 * @Title: getAnswers   
	 * @Description: 获取选择题答案   
	 * @param @param itemlist
	 * @param @return 
	 * @return ArrayList    
	 * @throws
	 */
	ArrayList getAnswers(ArrayList itemlist){
		StringBuffer sql = new StringBuffer();
		ArrayList list=new ArrayList();
		sql.append(" select pointid from investigate_result where staff_id=? and itemid=?  and create_user=?");
		try {
		    ContentDAO dao = new ContentDAO(frameconn);
		    String staffid = "false".equalsIgnoreCase(visibleUserName)?this.userView.getUserName():this.userView.getUserFullName();
		    
			for(int i=0;i<itemlist.size();i++)
			{
			    
				WelcomeForm wf=(WelcomeForm)itemlist.get(i);
				String itemid=wf.getItemid();
				
				ArrayList values = new ArrayList();
				values.add(staffid);
				values.add(itemid);
				values.add(this.userView.getUserName());
				
				this.frecset = dao.search(sql.toString(), values);
				while(this.frecset.next()){
					list.add(frecset.getString("pointid"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

}
