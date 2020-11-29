/*
 * 创建日期 2005-7-2
 *
 */
package com.hjsj.hrms.transaction.welcome;

import com.hjsj.hrms.actionform.welcome.WelcomeForm;
import com.hjsj.hrms.businessobject.performance.singleGrade.TableOperateBo;
import com.hjsj.hrms.businessobject.train.CtrlParamXmlBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author luangaojiong
 *
 * 首页调查显示类
 */
public class InvestigateSearchTrans extends IBusiness {
	private String visibleUserName="";

	public void execute() throws GeneralException {
		/**
		 * 首页
		 * 调用调查操作
		 */
		try
		{
			visibleUserName = SystemConfig.getPropertyValue("investigate_visible_username");
			if(visibleUserName==null|| "".equals(visibleUserName))
				visibleUserName="false";
			String enteryType="0";
			String home="5";
			HashMap map=(HashMap)this.getFormHM().get("requestPamaHM");
			if(map.get("enteryType")!=null)
			{
				enteryType=(String)map.get("enteryType");
				map.remove("enteryType");
			}
			else if(this.getFormHM().get("enteryType")!=null)
			{
				enteryType=(String)this.getFormHM().get("enteryType");
			}
			if(map.get("home")!=null)
			{
				home=(String)map.get("home");
			}
			else if(this.getFormHM().get("home")!=null)
			{
				home=(String)this.getFormHM().get("home");
			}
			String homePageHotId="-1";
			if(map.get("homePageHotId")!=null)
			{
				homePageHotId=PubFunc.decryption((String)map.get("homePageHotId"));
				map.remove("homePageHotId");
			}
			if("".equals(homePageHotId) && this.getFormHM().get("homePageHotId")!=null)
			{
				homePageHotId=(String)this.getFormHM().get("homePageHotId");
			}
			this.getFormHM().put("home", home);
			this.getFormHM().put("enteryType", enteryType);
			this.getFormHM().put("homePageHotId", PubFunc.encrypt(homePageHotId));

			exeInvestigate(homePageHotId);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	public String getDate(int days)
	{
		String date="";
		Calendar d=Calendar.getInstance();
		d.add(Calendar.DATE,days);
		
		int year=d.get(Calendar.YEAR);
		int month=d.get(Calendar.MONTH)+1;
		int day=d.get(Calendar.DATE);
		date=year+"-"+(month>=10?(""+month):("0"+month))+"-"+(day>=10?(""+day):("0"+day));
		return date;
	}
	
	/**
	 * 得到调查问卷列表
	 * @return
	 */
	public ArrayList getQuestionaryList()
	{
		Calendar today=Calendar.getInstance();
		
		ArrayList list=new ArrayList();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
			StringBuffer sql=new StringBuffer("select * from investigate where flag=1  and status=1 order by id desc");		
			this.frowset=dao.search(sql.toString());
			while (this.frowset.next()) {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				if(this.getUserView().isHaveResource(IResourceConstant.INVEST,this.frowset.getString("id"))||this.getUserView().isAdmin())
				{
					Date releasedate=this.frowset.getDate("releasedate");
					String date_str=dateFormat.format(releasedate);
					Calendar startDate=Calendar.getInstance();
					startDate.set(Calendar.YEAR,Integer.parseInt(date_str.substring(0,4)));
					startDate.set(Calendar.MONTH,Integer.parseInt(date_str.substring(5,7))-1);
					startDate.set(Calendar.DATE,Integer.parseInt(date_str.substring(8)));
					Calendar endDate=(Calendar)startDate.clone();
					int days=this.frowset.getInt("days");
					endDate.add(Calendar.DATE,days);
					
					//有效日期内有效
					if((today.get(Calendar.YEAR)+":"+today.get(Calendar.MONTH)+":"+today.get(Calendar.DATE)).equals(startDate.get(Calendar.YEAR)+":"+startDate.get(Calendar.MONTH)+":"+startDate.get(Calendar.DATE))||
							(today.get(Calendar.YEAR)+":"+today.get(Calendar.MONTH)+":"+today.get(Calendar.DATE)).equals((endDate.get(Calendar.YEAR)+":"+endDate.get(Calendar.MONTH)+":"+endDate.get(Calendar.DATE)))||(today.after(startDate)&&today.before(endDate)))
					{
						LazyDynaBean abean=new LazyDynaBean();
						abean.set("flag","1");   //1：问卷   2：培训评估（绩效模版）
						abean.set("id",this.frowset.getString("id"));
						abean.set("mdid",PubFunc.encryption(this.frowset.getString("id")));
						abean.set("content",this.frowset.getString("content"));
						
						list.add(abean);
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
	
	/*
	 * 得到培训评估信息列表
	 * (已启动的，有权限的，在规定日期的)
	 */
	public ArrayList getTrainEvaluateList()
	{
		ArrayList list=new ArrayList();
		if(this.getUserView().isAdmin())
			return list;
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
			RecordVo vo=new RecordVo("r31");
			if(!vo.hasAttribute("ctrl_param"))
			{
				DbWizard dbWizard=new DbWizard(this.getFrameconn());
				DBMetaModel dbmodel=new DBMetaModel(this.getFrameconn());
				
				Table table=new Table("r31");
				TableOperateBo tableOperateBo=new TableOperateBo(this.getFrameconn());
				Field aField0=tableOperateBo.getField(false,"ctrl_param","控制参数","M",3,0);
				table.addField(aField0);
				dbWizard.addColumns(table);
				dbmodel.reloadTableModel("r31");
			}
			
			this.frowset=dao.search("select r3130,ctrl_param,r31.r3101 from r40,r31 where  r40.r4005=r31.r3101 and  r40.r4001='"+this.getUserView().getA0100()+"' and r40.NBase='"+this.getUserView().getDbname()+"' and (r4013='03' or r4013='04' or r4013='05' )");
			CtrlParamXmlBo xmlBo=new CtrlParamXmlBo(this.getFrameconn());
			while(this.frowset.next())
			{
				String ctrl_param=Sql_switcher.readMemo(this.frowset,"ctrl_param");
				String a_name="";
				if(this.frowset.getString("r3130")!=null)
					a_name=this.frowset.getString("r3130");
				if(ctrl_param!=null&&ctrl_param.trim().length()>0)
				{
					xmlBo.setXml(ctrl_param);
					ArrayList lists=xmlBo.getEvaluateModelList();
					for(Iterator a=lists.iterator();a.hasNext();)
					{
						LazyDynaBean abean=(LazyDynaBean)a.next();
						String name=(String)abean.get("name");   //  1:评估模版  0：调查问卷
						String type=(String)abean.get("type");
						String run=(String)abean.get("run");
						String end_date=(String)abean.get("end_date");
						String value=(String)abean.get("value");
						
						if("true".equals(run))
						{
							Calendar today=Calendar.getInstance();
							Calendar endDate=Calendar.getInstance();
							endDate.set(Calendar.YEAR,Integer.parseInt(end_date.substring(0,4)));
							endDate.set(Calendar.MONTH,Integer.parseInt(end_date.substring(5,7))-1);
							endDate.set(Calendar.DATE,Integer.parseInt(end_date.substring(8)));
							if(today.before(endDate)||today.equals(endDate))
							{
								LazyDynaBean bean=abean;
								if("job".equals(type))
								{
									if("1".equals(name))
										bean.set("desc",a_name+"培训活动评估");
									else
										bean.set("desc",a_name+"培训活动效果调查问卷");
								}
								else
								{
									if("1".equals(name))
										bean.set("desc",a_name+"教师评估");
									else
										bean.set("desc",a_name+"教师教学效果调查问卷");
								}
								bean.set("r3101",this.frowset.getString("r3101"));
								bean.set("mdvalue",PubFunc.encryption(value));
								list.add(bean);
							}
						}
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

	public String getDataValue(String fielditemid,String operate,String value)
	{
		StringBuffer a_value=new StringBuffer("");
		try
		{
			if("=".equals(operate))
			{
				a_value.append("(");
				a_value.append(Sql_switcher.year(fielditemid)+operate+value.substring(0,4)+" and ");
				a_value.append(Sql_switcher.month(fielditemid)+operate+value.substring(5,7)+" and ");
				a_value.append(Sql_switcher.day(fielditemid)+operate+value.substring(8));
				a_value.append(" ) ");
			}
			else 
			{
				a_value.append("(");
				a_value.append(Sql_switcher.year(fielditemid)+">"+value.substring(0,4)+" or ( ");
				a_value.append(Sql_switcher.year(fielditemid)+"="+value.substring(0,4)+" and "+Sql_switcher.month(fielditemid)+">"+value.substring(5,7)+" ) or ( ");
				a_value.append(Sql_switcher.year(fielditemid)+"="+value.substring(0,4)+" and "+Sql_switcher.month(fielditemid)+"="+value.substring(5,7)+" and "+Sql_switcher.day(fielditemid)+operate+value.substring(8));
				a_value.append(") ) ");
			}

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return a_value.toString();
	}
	
    //	执行调查操作*****************************************************
	public void exeInvestigate(String hotid) throws GeneralException {

		String temp = "";
		String id = "0";

		ContentDAO dao = new ContentDAO(this.getFrameconn());

		try {
			//得到调查问卷列表信息
			this.getFormHM().put("topicList2", "-1".equals(hotid)?getQuestionaryList():new ArrayList());
			//得到培训评估信息列表
			this.getFormHM().put("trainEvaluateList", "-1".equals(hotid)?getTrainEvaluateList():new ArrayList());
			//取得要点列表

			
			StringBuffer sbpoint = new StringBuffer();
			String pointSql = "select pointid,itemid,name,describestatus from investigate_point where status=1 order by pointid";
			sbpoint.append(pointSql);
			ArrayList pointlist = new ArrayList();
			this.frowset = dao.search(sbpoint.toString());
			while (this.frowset.next()) {
				
				WelcomeForm wf = new WelcomeForm();
				String pointid=this.frowset.getString("pointid");
				wf.setPointid(pointid);
				wf.setPointContext("point¤"+pointid);
				wf.setItemid(this.frowset.getString("itemid"));
				wf.setPointName(this.frowset.getString("name"));
				wf.setDescribestatus(this.frowset.getString("describestatus"));  //要点描述标识
				pointlist.add(wf);
				
			}

			//取得项目列表主题的第一个
			Hashtable itemht = new Hashtable();
			String idtemp = "";
			String where ="";
			if(!"-1".equals(hotid))
				where = " where itemid='"+hotid+"'";
			this.frowset = dao.search("select itemid,id,name,status,fillflag,selects,minvalue,maxvalue from investigate_item "+where);
			temp = "";
			while (this.frowset.next()) {

				WelcomeForm wf = new WelcomeForm();
				temp = this.frowset.getString("itemid");
				if (temp == null || "".equals(temp)) {
					wf.setItemid("0");
				} else {
					wf.setItemid(temp);
				}

				idtemp = this.frowset.getString("id");
				if (idtemp == null || "".equals(idtemp)) {
					idtemp = "0";
				}
				temp = "";
				temp = this.frowset.getString("name");
				if (temp == null || "".equals(temp)) {
					wf.setItemName("");
				} else {
					wf.setItemName(temp);
				}
				
				temp=PubFunc.NullToZero(this.frowset.getString("status"));
				wf.setItemStatus(temp);
                temp=this.frowset.getString("fillflag");
                if(temp==null|| "".equals(temp))
                {
                	wf.setFillflag("0");
                }
                else
                {
                	wf.setFillflag(temp);
                }
                temp=this.frowset.getString("selects");
                if(temp==null|| "".equals(temp.trim()))
                	wf.setSelects("0");
                else
                	wf.setSelects(temp);
                temp=this.frowset.getString("maxvalue");
                if(temp==null|| "".equals(temp.trim()))
                	wf.setMaxvalue("0");
                else
                	wf.setMaxvalue(temp);
                temp=this.frowset.getString("minvalue");
                if(temp==null|| "".equals(temp.trim()))
                	wf.setMinvalue("0");
                else
                	wf.setMinvalue(temp);
				if (!itemht.containsKey(idtemp) && idtemp != null) {
					
					itemht.put(idtemp, wf);
				}
				

			}


			/**
			 * 取得调查主题列表第一轮循环
			 * 
			 * 取出热点调查主题
			 */
			
			String topicsql = "select releasedate,days,id,content,description from investigate where flag=1 ";
			if("-1".equals(hotid))
				topicsql+=" and status=0 ";
			StringBuffer strsql = new StringBuffer();
			strsql.append(topicsql);
			ArrayList topicList = new ArrayList();
			temp = "";
			String description="";
			this.frowset = dao.search(strsql.toString());
			Calendar today=Calendar.getInstance();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			while (this.frowset.next()) 
			{
				
				WelcomeForm wf = new WelcomeForm();
                //---------------------------------------------------
				
				Date releasedate=this.frowset.getDate("releasedate");
				String date_str=dateFormat.format(releasedate);
				Calendar startDate=Calendar.getInstance();
				startDate.set(Calendar.YEAR,Integer.parseInt(date_str.substring(0,4)));
				startDate.set(Calendar.MONTH,Integer.parseInt(date_str.substring(5,7))-1);
				startDate.set(Calendar.DATE,Integer.parseInt(date_str.substring(8)));
				Calendar endDate=(Calendar)startDate.clone();
				int days=this.frowset.getInt("days");
				endDate.add(Calendar.DATE,days);
				
				
				
				//有效日期内有效
				if((today.get(Calendar.YEAR)+":"+today.get(Calendar.MONTH)+":"+today.get(Calendar.DATE)).equals(startDate.get(Calendar.YEAR)+":"+startDate.get(Calendar.MONTH)+":"+startDate.get(Calendar.DATE))||
						(today.get(Calendar.YEAR)+":"+today.get(Calendar.MONTH)+":"+today.get(Calendar.DATE)).equals((endDate.get(Calendar.YEAR)+":"+endDate.get(Calendar.MONTH)+":"+endDate.get(Calendar.DATE)))||(today.after(startDate)&&today.before(endDate)))
				{
				//---------------------------------------------------
				id = this.frowset.getString("id");
				if (id == null || "".equals(id)) {
					wf.setId("0");
					id = "0";
				} else {
					wf.setId(id);
				}

				temp = this.frowset.getString("content");
				//description
				description=Sql_switcher.readMemo(this.frowset,"description");
				if (temp == null || "".equals(temp)) {
					wf.setName("");
				} else {
					wf.setName(temp);
				}
                if(description==null|| "".equals(description))
                {
                	wf.setDescription("");
                }else
                {
                	wf.setDescription(description);
                }
				String tempItemid = "0";
				/**
				 * 查找出对应的项目及名称
				 */
				if (itemht.containsKey(id)) {
					tempItemid = ((WelcomeForm) itemht.get(id)).getItemid();
					wf.setItemid(tempItemid);
					wf.setItemName(((WelcomeForm) itemht.get(id)).getItemName());
					wf.setItemStatus(((WelcomeForm) itemht.get(id)).getItemStatus());
					wf.setFillflag(((WelcomeForm)itemht.get(id)).getFillflag());
					wf.setSelects(((WelcomeForm)itemht.get(id)).getSelects());
					wf.setMinvalue(((WelcomeForm)itemht.get(id)).getMinvalue());
					wf.setMaxvalue(((WelcomeForm)itemht.get(id)).getMaxvalue());
					ArrayList answerList = new ArrayList();
					answerList = getAnswers(tempItemid);
					this.getFormHM().put("answerList", answerList);
					
					ArrayList answerDesc = new ArrayList();
					answerDesc = getAnswersDesc(tempItemid);
					this.getFormHM().put("answerDesc", answerDesc);
					
					ArrayList essayDesc = new ArrayList();
					essayDesc = getEssayDesc(tempItemid);
					this.getFormHM().put("essayDesc", essayDesc);
					String state = "0";
					String str = "";
					if(answerList.size()>0)
						str = " select state from investigate_result where itemid=? and staff_id=? ";
					else if(essayDesc.size()>0)
						str = " select state from investigate_content where itemid=? and staff_id=? ";
					if(!"".equals(str))
						state = getState(tempItemid,str);
					this.getFormHM().put("state", state);
					
				}
				
				/**
				 * 得到要素列表
				 */
				ArrayList templst = getSecondlst(tempItemid, pointlist);
				if(templst.size()>0 || "1".equals(wf.getItemStatus()))
				{
					wf.setPointList(templst);
					topicList.add(wf);
				}
			}

			}
			//取得调查主题列表第一轮循环结束		
			//完成

			/**
			 * 以下是框架右边显示控制
			 */
			
			this.getFormHM().put("topicList", topicList);
			
			if(topicList.size()==0)
			{
				 this.getFormHM().put("displayContral","1");	//所有调查项目为空判断
			}
			else
			{
				 this.getFormHM().put("displayContral","1");	//所有要素为空判断
			     
				 String pointflag="0";	//0为没有要素项
				 String itemflag="0";	//0为没有项目描述项
				 for(int i=0;i<topicList.size();i++)
				 {
				 	WelcomeForm evf=(WelcomeForm)topicList.get(i);
				 	ArrayList list=evf.getPointList();
				
				 	for(int j=0;j<list.size();j++)
				 	{
				 		pointflag="1";
				 		//this.getFormHM().put("displayContral","0");
				 		
				 		break;
				 	}
				}
				 
				for(int i=0;i<topicList.size();i++)
				{
					WelcomeForm evf=(WelcomeForm)topicList.get(i);
					if("1".equals(evf.getItemStatus()))
					{
						itemflag="1";
						break;
					}
				}
				
				if("1".equals(itemflag) || "1".equals(pointflag))
				{
					this.getFormHM().put("displayContral","0");
				}
			}

			//得到项目标题

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	String getState(String itemid,String sql){
		String state = "0";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		ArrayList paramList = new ArrayList();
		try {
			paramList.add(itemid);
			paramList.add("false".equalsIgnoreCase(visibleUserName)?this.userView.getUserName():this.userView.getUserFullName());
			this.frecset = dao.search(sql,paramList);
			while(this.frecset.next()){
				state = frecset.getString("state");
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return state;
	}
	
	ArrayList getEssayDesc(String itemid){
		StringBuffer sql = new StringBuffer();
		ArrayList list=new ArrayList();
		sql.append(" select context from investigate_content where staff_id=? and itemid=? ");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		ArrayList paramList = new ArrayList();
		try {
			paramList.add("false".equalsIgnoreCase(visibleUserName)?this.userView.getUserName():this.userView.getUserFullName());
			paramList.add(itemid);
			this.frecset = dao.search(sql.toString(),paramList);
			while(this.frecset.next()){
				if(frecset.getString("context") != null && !"".equals(frecset.getString("context")))
					list.add(itemid+"`"+frecset.getString("context"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	ArrayList getAnswersDesc(String itemid){
		StringBuffer sql = new StringBuffer();
		ArrayList list=new ArrayList();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		ArrayList paramList = new ArrayList();
		sql.append(" select pointid,context from investigate_result where staff_id=? and itemid=? ");
		try {
			paramList.add("false".equalsIgnoreCase(visibleUserName)?this.userView.getUserName():this.userView.getUserFullName());
			paramList.add(itemid);
			this.frecset = dao.search(sql.toString(),paramList);
			while(this.frecset.next()){
				if(frecset.getString("context") != null && !"".equals(frecset.getString("context")))
					list.add(frecset.getString("pointid")+"`"+frecset.getString("context"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	ArrayList getAnswers(String itemid){
		StringBuffer sql = new StringBuffer();
		ArrayList list=new ArrayList();
		sql.append(" select pointid from investigate_result where staff_id=? and itemid=? ");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		ArrayList paramList = new ArrayList();
		try {
			paramList.add("false".equalsIgnoreCase(visibleUserName)?this.userView.getUserName():this.userView.getUserFullName());
			paramList.add(itemid);
			this.frecset = dao.search(sql.toString(),paramList);
			while(this.frecset.next()){
				list.add(frecset.getString("pointid"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	//	得到要查找的lst内容
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
}
