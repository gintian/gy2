
/*
 * 创建日期 2005-6-22
 *
 */

package com.hjsj.hrms.transaction.performance;


import com.hjsj.hrms.actionform.performance.AppraiseselfForm;
import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.log4j.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Vector;


/**
 * PerforBean类的分支
 * 
 * @author luangaojiong
 *
 */
public class PerforBeanHelp {
	
	
	Connection conn = null;
	transient Category cat;
	PreparedStatement statement;

	ResultSet resultset;
	ArrayList perpointtemp=new ArrayList();
	ArrayList newPointlist=new ArrayList(); //过滤后ArrayList
	ArrayList gradelist=new ArrayList();	//上一次成绩hashtable对象
	/**全部标度指标列表*/
	ArrayList allgradelist=null;

	String sql2 = "";
	int nowNum = 0;

	int maxNum = 0;
	int topNum=0;
	int bottomNum=0;
	String planId="0";
	String objectId="0";
	String userId="0";
	
	/**按混合模式打分*/
	String scoreflag="0";
	/**
	 * 得到用过滤的的要素号
	 *
	 */
	public PerforBeanHelp(String template_id,String plantpId,String objectIdtp,String userIdtp,Connection conn,String scoreflag)
	{
		cat = Category.getInstance("com.hjsj.hrms.transaction.performance");
		planId=plantpId;
		objectId=objectIdtp;
		userId=userIdtp;
		this.conn=conn;
		this.scoreflag=scoreflag;
		ContentDAO dao = new ContentDAO(this.conn);
		ResultSet rs2=null;
		String sql2="select point_id from per_template_point where item_id in (select item_id from per_template_item where template_id='"+template_id+"')";
		try
		{
			rs2=dao.search(sql2);
			while(rs2.next())
			{
				newPointlist.add(PubFunc.NullToZero(rs2.getString("point_id")));
			}
		}
		catch(Exception ex)
		{
				ex.printStackTrace();
		}
		finally
		{
			PubFunc.closeResource(rs2);
		}
	}
	
	public String getGrade()
	{
		String flag="0";
		StringBuffer sb=new StringBuffer();
		String tableName = "per_table_"+planId;
		sb.append("select score,amount,point_id,degree_id from ");
		sb.append(tableName);
		sb.append(" where ( ");
		/*
		for(int i=0;i<newPointlist.size();i++)
		{
			sb.append(" point_id='");
			sb.append(newPointlist.get(i).toString());
			sb.append("' or ");
		}
		*/
			sb.append(" object_id='");
			sb.append(objectId);
			sb.append("' and mainbody_id='");
			sb.append(userId);
			sb.append("')");
		
		ContentDAO dao = new ContentDAO(this.conn);
		ResultSet rs2=null;
		try
		{
			cat.debug("-------->com.hjsj.hrms.transaction.performance.PerforBeanHelp-->getGrade-->sb-->"+sb.toString());

			rs2=dao.search(sb.toString());
			while(rs2.next())
			{
				flag="1";
				DynaBean vo=new LazyDynaBean();
				vo.set("score",PubFunc.NullToZero(rs2.getString("score")).trim());
				vo.set("amount",PubFunc.NullToZero(rs2.getString("amount")).trim());
				vo.set("point_id",PubFunc.NullToZero(rs2.getString("point_id")).trim());
				vo.set("degree_id",PubFunc.NullToZero(rs2.getString("degree_id")).trim());
				cat.debug("-------->com.hjsj.hrms.transaction.performance.PerforBeanHelp-->getGrade-score->sb-->"+vo.get("amount"));
  			    gradelist.add(vo);
			}
		}
		catch(Exception ex)
		{
				ex.printStackTrace();
		}
		finally
		{
			PubFunc.closeResource(rs2);
		}
		
		return flag;
	}
	
	/**
	 * 得到多级栏目输出字符串
	 * @param itemlist
	 * @param maxNum
	 * @param template_id
	 * @param rootId
	 * @return
	 */
	
	public String  getMuiltString(ArrayList itemlist,int maxLay, String template_id,String rootId,ArrayList rootlist,ArrayList  perpointlist,ArrayList pointpopedomlist)
	{
		StringBuffer temp=new StringBuffer();
		perpointtemp=perpointlist;			//保存perpointlist对象为页面输出名子做准备
		try
		{
			sql2="SELECT item_id,itemdesc FROM per_template_item WHERE parent_id= ? and template_id='"
				+ template_id + "' ORDER BY item_id ASC";
			statement=conn.prepareStatement(sql2);
			for(int i=0;i<rootlist.size();i++)
			{
				nowNum = 0;
				String tempStr="";
				AppraiseselfForm af=(AppraiseselfForm)rootlist.get(i);
				tempStr=MenuList(af.getItemid(), "", 0,	new StringBuffer(), false,itemlist,af.getNowMaxlay(),maxLay, perpointlist,pointpopedomlist);
				temp.append(tempStr);
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		finally
		{
			PubFunc.closeResource(resultset);
			PubFunc.closeResource(statement);
		}
		
		return temp.toString();
		
	}
	
	/**
	 * 生成绩效考评表数据采集页面内容
	 * @param parentId
	 * @param name
	 * @param position
	 * @param outHTML
	 * @param init
	 * @param itemlist
	 * @param nowMaxLay
	 * @param maxLay
	 * @param perpointlist
	 * @param pointpopedomlist
	 * @return
	 */
	public String MenuList(String parentId, String name, int position,
			StringBuffer outHTML, boolean init,ArrayList itemlist,String nowMaxLay,int maxLay,ArrayList  perpointlist,ArrayList pointpopedomlist) 
	{
		//第一次进入
		if(nowNum==0)
		{
			outHTML.append("<tr>");
		}
		DbSecurityImpl dbS = new DbSecurityImpl();
		Vector childList = new Vector();
		Vector nameList = new Vector();
		try 
		{       //连接是传进来的，不用创建
				//conn= (Connection) AdminDb.getConnection();
				statement.setString(1, parentId);
				// 打开Wallet
				dbS.open(conn, sql2);
				resultset = statement.executeQuery();
				while (resultset.next()) 
				{
					childList.addElement(resultset.getString("item_id"));
					nameList.addElement(resultset.getString("itemdesc"));

				}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}finally{
			try {
				// 关闭Wallet
				dbS.close(this.conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		int msize = childList.size();
		
		if (msize > 0)
		{	
			outHTML.append("<td class=\"RecordRow\" align=\"center\" nowrap width=\"100\" rowspan=\""+getAppForm(parentId,itemlist).getLeafPointElementNum()+"\">");
			outHTML.append(getAppForm(parentId,itemlist).getItemName());
			outHTML.append("</td>");
			++nowNum;
			if(nowNum>maxNum)
			{
				maxNum=nowNum;
			}
			for (int i = 0; i < msize; i++)
			{					
				MenuList((String) childList.elementAt(i), (String) nameList	.elementAt(i), i, outHTML, false,itemlist,nowMaxLay,maxLay, perpointlist,pointpopedomlist);
			}
		} 
		else 
		{			
				outHTML.append("<td class=\"RecordRow\" align=\"center\" nowrap width=\"100\" rowspan=\""+getAppForm(parentId,itemlist).getLeafPointElementNum()+"\" >");
				outHTML.append(getAppForm(parentId,itemlist).getItemName());
				outHTML.append("</td>");
				if(maxLay>Integer.parseInt(getAppForm(parentId,itemlist).getLayer()))
				{
					for(int j=0;j<(maxLay-Integer.parseInt(getAppForm(parentId,itemlist).getLayer()));j++)
					{
						outHTML.append("<td class=\"RecordRow\" width=\"1\">");
						outHTML.append("</td>");		
					}
				}
				//第一个要素
				outHTML.append("<td class=\"RecordRow\" nowrap >&nbsp;<table width=\"400\"><tr><td align=\"left\" width=\"200\">");
				outHTML.append(getFirstPoint(getAppForm(parentId,itemlist).getPointlst()).getPointName());
				outHTML.append("&nbsp;</td><td align=\"left\" width=\"300\">");
				
				String pointId=getFirstPoint(getAppForm(parentId,itemlist).getPointlst()).getPointId();
				String score=getFirstPoint(getAppForm(parentId,itemlist).getPointlst()).getScore();
				if("2".equals(scoreflag)|| "1".equals(getFirstPoint(getAppForm(parentId,itemlist).getPointlst()).getPointKind()))
					createInputText(pointId,score, outHTML, itemlist, perpointlist, pointpopedomlist);
				else
				{
					createInputRadio(pointId,score, outHTML, itemlist, perpointlist, pointpopedomlist);
				}
				
				outHTML.append("</td></tr></table></td></tr>");
				//第二个以下的要素
				for(int k=1;k<getAppForm(parentId,itemlist).getPointlst().size();k++)
				{
							outHTML.append("<tr>");
							if(maxLay>Integer.parseInt(getAppForm(parentId,itemlist).getLayer()))
							{	
								for(int j=0;j<(maxLay-Integer.parseInt(getAppForm(parentId,itemlist).getLayer()));j++)
								{
									outHTML.append("<td class=\"RecordRow\"  width=\"100\">");
									outHTML.append("</td>");		
								}
							}
							outHTML.append("<td class=\"RecordRow\" nowrap >&nbsp;<table width=\"400\"><tr><td align=\"left\" width=\"200\">");
							outHTML.append(((AppraiseselfForm)getAppForm(parentId,itemlist).getPointlst().get(k)).getPointName());
							outHTML.append("&nbsp;</td><td align=\"left\" width=\"300\">");
							pointId=((AppraiseselfForm)getAppForm(parentId,itemlist).getPointlst().get(k)).getPointId();
							score=((AppraiseselfForm)getAppForm(parentId,itemlist).getPointlst().get(k)).getScore();
							if("2".equals(scoreflag)|| "1".equals(((AppraiseselfForm)getAppForm(parentId,itemlist).getPointlst().get(k)).getPointKind()))
								createInputText(pointId,score,outHTML,itemlist,perpointlist,pointpopedomlist);
							else
							{
								createInputRadio(pointId,score,outHTML,itemlist,perpointlist,pointpopedomlist);
							}
//chenmengqing added createInputText method;
//							outHTML.append("<input  type=\"text\" name=\"");
//							outHTML.append(getPointIdName(perpointlist,((AppraiseselfForm)getAppForm(parentId,itemlist).getPointlst().get(k)).getPointId()));
//							outHTML.append("\"  " );
//							outHTML.append(getPopedomName(pointpopedomlist,((AppraiseselfForm)getAppForm(parentId,itemlist).getPointlst().get(k)).getPointId()));		//权限控制
//							outHTML.append(" value=\"");
//							outHTML.append(getTextValue(perpointlist,((AppraiseselfForm)getAppForm(parentId,itemlist).getPointlst().get(k)).getPointId()));
//							outHTML.append("\" ");
//							outHTML.append(" title=\"");
//							outHTML.append(getDisplayText(perpointlist,((AppraiseselfForm)getAppForm(parentId,itemlist).getPointlst().get(k)).getScore(), ((AppraiseselfForm)getAppForm(parentId,itemlist).getPointlst().get(k)).getPointId()));
//							outHTML.append("\" onBlur=\"checkNum(this)\" maxlength=\"6\" >");
//							outHTML.append(getDisplayText(perpointlist,((AppraiseselfForm)getAppForm(parentId,itemlist).getPointlst().get(k)).getScore(), ((AppraiseselfForm)getAppForm(parentId,itemlist).getPointlst().get(k)).getPointId()));
							outHTML.append("</td></tr></table></td></tr>");
				}
			}
			return outHTML.toString();
	}
	
	/**
	 * 创建输入框
	 * @param parentId
	 * @param outHTML
	 * @param itemlist
	 * @param perpointlist
	 * @param pointpopedomlist
	 */
	private void createInputText(String pointId,String score,StringBuffer outHTML, ArrayList itemlist, ArrayList perpointlist, ArrayList pointpopedomlist) {
//		outHTML.append("<input  type=\"text\" name=\"");
//		outHTML.append(getPointIdName(perpointlist,getFirstPoint(getAppForm(parentId,itemlist).getPointlst()).getPointId()));
//		outHTML.append("\" " );
//		outHTML.append(getPopedomName(pointpopedomlist,getFirstPoint(getAppForm(parentId,itemlist).getPointlst()).getPointId()));
//		outHTML.append(" value=\"");
//		outHTML.append(getTextValue(perpointlist,getFirstPoint(getAppForm(parentId,itemlist).getPointlst()).getPointId()));
//		outHTML.append("\" ");
//		outHTML.append(" title=\"");
//		outHTML.append(getDisplayText(perpointlist,getFirstPoint(getAppForm(parentId,itemlist).getPointlst()).getScore(), getFirstPoint(getAppForm(parentId,itemlist).getPointlst()).getPointId()));
//		outHTML.append("\" onBlur=\"checkNum(this)\"  maxlength=\"6\" >");
//		outHTML.append(getDisplayText(perpointlist,getFirstPoint(getAppForm(parentId,itemlist).getPointlst()).getScore(), getFirstPoint(getAppForm(parentId,itemlist).getPointlst()).getPointId()));
		outHTML.append("<input  type=\"text\" name=\"");
		outHTML.append(getPointIdName(perpointlist,pointId));
		outHTML.append("\" " );
		outHTML.append(getPopedomName(pointpopedomlist,pointId));
		outHTML.append(" value=\"");
		outHTML.append(getTextValue(perpointlist,pointId));
		outHTML.append("\" ");
		outHTML.append(" title=\"");
		outHTML.append(pointId);
		outHTML.append(":");
		outHTML.append(score);
		//outHTML.append(getDisplayText(perpointlist,score, pointId));
		outHTML.append("\" onchange=\"checkNum(this)\"  maxlength=\"6\" >");
		outHTML.append(getDisplayText(perpointlist,score, pointId));
	}
	
	private void createInputRadio(String pointId,String score,StringBuffer outHTML, ArrayList itemlist, ArrayList perpointlist, ArrayList pointpopedomlist) {
		if(isHavePrivPointId(pointpopedomlist,pointId))
		{
			ArrayList glist=getGradeListOfPointId(pointId);
			outHTML.append(getDisplayText(perpointlist,score, pointId));
			outHTML.append("<br>");			
			for(int i=0;i<glist.size();i++)
			{
				PerGradeBean vo=(PerGradeBean)glist.get(i);
				outHTML.append("<input  type=\"radio\" name=\"");
				outHTML.append(getPointIdName(perpointlist,pointId));
				outHTML.append("\" " );	
				outHTML.append(" value=\"");
				outHTML.append(vo.gradecode);
				outHTML.append("\" title=\"");
				outHTML.append(pointId);
				outHTML.append(":");				
				outHTML.append(score);
				outHTML.append("\"");
				String degree_id=getGradeCodeValue(pointId);
				if(degree_id.equals(vo.gradecode))
				{
					outHTML.append(" checked ");
				}
				
				outHTML.append(" onclick=\"checkNum(this)\">");
				outHTML.append(vo.getGradedesc());
				outHTML.append("<br>");
			}
			
//			outHTML.append("<input  type=\"text\" name=\"");
//			outHTML.append(getPointIdName(perpointlist,pointId));
//			outHTML.append("\" " );
//			outHTML.append(getPopedomName(pointpopedomlist,pointId));
//			outHTML.append(" value=\"");
//			outHTML.append(getTextValue(perpointlist,pointId));
//			outHTML.append("\" ");
//			outHTML.append(" title=\"");
//			outHTML.append(getDisplayText(perpointlist,score, pointId));
//			outHTML.append("\" onBlur=\"checkNum(this)\"  maxlength=\"6\" >");
//			outHTML.append(getDisplayText(perpointlist,score, pointId));
		}
	}	
	/**
	 * 取得对应指标的标度
	 * @param pointId
	 * @return
	 */
	private ArrayList getGradeListOfPointId(String pointId)
	{
		ArrayList list=new ArrayList();
		for(int i=0;i<this.allgradelist.size();i++)
		{
			PerGradeBean vo=(PerGradeBean)this.allgradelist.get(i);
			if(vo.getPoint_id().equals(pointId))
			{
				list.add(vo);
			}
		}
		return list;
	}
	/**
	 * 得到提交后的值
	 * @param perpointlist
	 * @param pointid
	 * @return
	 */
	public String getTextValue(ArrayList perpointlist,String pointid)
	{
		String value="";
		for(int i=0;i< perpointlist.size();i++)
		{
			PerPointBean ppb=(PerPointBean)perpointlist.get(i);
			if(ppb.getPoint_id().equals(pointid))
			{
			
				if(ppb.getPointkind()==1)//定量标识
				{					
					value=getGradeValue(pointid,"1");
				}
				else
				{
					value=getGradeValue(pointid,"0");
				}
				break;
			}
		}
		return value;
	}
	
	public String getGradeCodeValue(String pointid)
	{
		String value="";
		if(gradelist.size()<=0)
		{
			return "";
		}
		for(int i=0;i<gradelist.size();i++)
		{
			DynaBean vo=(DynaBean)gradelist.get(i);
			if(vo.get("point_id").toString().trim().equals(pointid))
			{
				value=vo.get("degree_id").toString();
				break;
			}
		}
		return value;
	}	
	/**
	 * 得到提交后的值
	 * @param pointid
	 * @param gradflag 1为定量值 0为定性值
	 * @return
	 */
	public String getGradeValue(String pointid,String gradflag)
	{
		String value="";
		if(gradelist.size()<=0)
		{
			return "";
		}
		for(int i=0;i<gradelist.size();i++)
		{
			DynaBean vo=(DynaBean)gradelist.get(i);
			if(vo.get("point_id").toString().trim().equals(pointid) && "0".equals(gradflag))
			{
				value=vo.get("score").toString();
				break;
			}
			else if(vo.get("point_id").toString().trim().equals(pointid) && "1".equals(gradflag))
			{
				value=vo.get("amount").toString();
				break;
			}
		}
		return value;
	}
	/**
	 * 得到项目中第一个要素
	 * @param pointlist
	 * @return
	 */
	public AppraiseselfForm getFirstPoint(ArrayList pointlist)
	{	
		
		AppraiseselfForm af=new AppraiseselfForm();
		if(pointlist.size()<=0)
		{
			 af=new AppraiseselfForm();
		}
		else
		{
			 af=(AppraiseselfForm)pointlist.get(0);
		}
		return af;
	}
	
	/**
	 * 取得项目对象
	 */
	public AppraiseselfForm getAppForm(String itemid,ArrayList itemlist)
	{
		AppraiseselfForm af1=new AppraiseselfForm();
		AppraiseselfForm af2=new AppraiseselfForm();
		for(int i=0;i<itemlist.size();i++)
		{
			af1=(AppraiseselfForm)itemlist.get(i);
			if(af1.getItemid().equals(itemid))
			{
				af2=af1;
				break;
			}
		}
				
		return af2;
	}
	/**
	 * 权限控制字符输出
	 * @param pointpopedomlist
	 * @param pointid
	 * @return
	 */
	public String getPopedomName(ArrayList pointpopedomlist,String pointid)
	{
		String popedomName="disabled class=\"selftext\" ";
		for(int i=0;i<pointpopedomlist.size();i++)
		{
			AppraiseselfForm ppb2=(AppraiseselfForm)pointpopedomlist.get(i);
			if(ppb2.getPointId().equals(pointid))
			{
				popedomName="";
				break;
			}
		}
		return popedomName;
	}
	
	/**
	 * 是否有此指标的权限
	 * @param pointpopedomlist
	 * @param pointid
	 * @return
	 */
	public boolean isHavePrivPointId(ArrayList pointpopedomlist,String pointid)
	{
		boolean bflag=false;
		for(int i=0;i<pointpopedomlist.size();i++)
		{
			AppraiseselfForm ppb2=(AppraiseselfForm)pointpopedomlist.get(i);
			if(ppb2.getPointId().equals(pointid))
			{
				bflag=true;
				break;
			}
		}		
		return bflag;
	}
	/**
	 * 从所有的要素中去找对应的位置过滤pointid返回定性或定量名
	 * 即：文本框的名称
	 * @author luangaojiong
	 *
	 */
	
	public String getPointIdName(ArrayList perpointlist,String pointid)
	{
		String pointIdName="";
		String pointValue="0";
		for(int i=0;i< perpointlist.size();i++)
		{
			PerPointBean ppb=(PerPointBean)perpointlist.get(i);
			if(ppb.getPoint_id().equals(pointid))
			{
				if(ppb.getPointkind()==1)
				{
					pointIdName=pointid;
					ppb.setType("q");	//定量标识
				}
				else
				{
					pointIdName=pointid;
				}
			    ppb.setValue(pointIdName);
			    perpointtemp.set(i, ppb);
				pointValue="pointreturnlist["+i+"].value";
				break;
			}
		}
		return pointValue;
	}
	/**
	 * 返回文本框名称ArrayList
	 * @author luangaojiong
	 *
	 */
	public ArrayList getTextNameArrayList()
	{
		return perpointtemp;
	}
	
	/**
	 * 过滤pointid 
	 *
	 */
	public String getDisplayText(ArrayList perpointlist,String score, String pointid)
	{
		String deplayName="";
		
		for(int i=0;i< perpointlist.size();i++)
		{
			
			PerPointBean ppb=(PerPointBean)perpointlist.get(i);
			
			
			if(ppb.getPoint_id().equals(pointid))
			{
				if(ppb.getPointkind()==1)
				{
					  //调用
					 //System.out.println("------>ppb.getPointkind()定量-->");
					  getTopAndBottomValue(ppb);
					  deplayName="(数值:"+bottomNum+"~"+topNum+")";
					  ppb.setTopNum(topNum);
					  ppb.setBottomNum(bottomNum);
					  perpointtemp.set(i, ppb);
				}
				else
				{
					//System.out.println("---------->"+score);
					 deplayName="(分值："+score+")";
					 ppb.setMaxScore(Double.parseDouble(score));
					 perpointtemp.set(i, ppb);
				}
				break;
			}
		}
		
		return deplayName;
	}
	

	/**
	 * 得到最大上限及最小下限值
	 * @author luangaojiong
	 *
	 */
	void getTopAndBottomValue(PerPointBean ppb)
	{
		 topNum=0;
		 bottomNum=0;
			 
		for(int i=0;i<ppb.getPerGradelist().size();i++)
		{
			//指标对象Bean
			PerGradeBean bgbean=(PerGradeBean)ppb.getPerGradelist().get(i);
			if(i==0)
			{
				bottomNum=(int)bgbean.getBottom_value();
			}
			else
			{
				if(bgbean.getBottom_value()<bottomNum)
				{
					bottomNum=(int)bgbean.getBottom_value();
				}
			}
			
			if(bgbean.getTop_value()>topNum)
			{
				topNum=(int)bgbean.getTop_value();
			}
		}
		
		
	}

	public ArrayList getAllgradelist() {
		return allgradelist;
	}

	public void setAllgradelist(ArrayList allgradelist) {
		this.allgradelist = allgradelist;
	}

	public void setScoreflag(String scoreflag) {
		this.scoreflag = scoreflag;
	}
	
	

}
