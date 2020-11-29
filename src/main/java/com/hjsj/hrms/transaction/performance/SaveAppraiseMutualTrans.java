/*
 * 创建日期 2005-6-27
 *
 * TODO 要更改此生成的文件的模板，请转至
 * 窗口 － 首选项 － Java － 代码样式 － 代码模板
 */
package com.hjsj.hrms.transaction.performance;

import com.hjsj.hrms.interfaces.performance.PerMainBody;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * @author luangaojiong
 *
 * 互评保存提交
 * 
 */
public class SaveAppraiseMutualTrans extends IBusiness {

	String planId="0";
	AmountToScore amountToScore=null;
	public void execute() throws GeneralException 
	{
		try
		{
		 String wholeEven="";
		 String knowDegree="";
		 if(this.getFormHM().get("wholeEven")!=null && !"".equals(this.getFormHM().get("wholeEven").toString()))
		 {
		 	wholeEven=this.getFormHM().get("wholeEven").toString();
		 }
		 if(this.getFormHM().get("knowDegree")!=null && !"".equals(this.getFormHM().get("knowDegree").toString()))
		 {
		 	knowDegree=this.getFormHM().get("knowDegree").toString();
		 }
		
		ArrayList listpointid = new ArrayList(); //定性
		ArrayList listquantity = new ArrayList(); //定量
		String tableName = "per_table_"; //表名
		String objectId="0";
		/**得到计划号*/
		String nowPlanNum = "";
		if (this.getFormHM().get("planNum") != null) 
		{
			nowPlanNum = this.getFormHM().get("planNum").toString();
			tableName = tableName + nowPlanNum;
		} 
		else 
		{
			return;
		}
		
		planId=nowPlanNum;
		/**
		 * 得到考查对象id
		 */
		if(this.getFormHM().get("objectId")!=null)
		{
			 objectId=this.getFormHM().get("objectId").toString();
		}
		else
		{
			return;
		}
		 /**得到提交的文本内容*/
		 ArrayList list=(ArrayList)this.getFormHM().get("pointreturnlist");
		 list=getFilter(list,nowPlanNum); //过滤
			/**声明定量定性值转换类*/
		 amountToScore=new AmountToScore(nowPlanNum);
		 Hashtable htxml=new Hashtable();		 
		 LoadXml loadxml=new LoadXml(this.getFrameconn(),planId);
		 htxml=loadxml.getDegreeWhole();
		 /**最高标度加上限制*/
		 GradeCodeBo codebo=new GradeCodeBo(this.getFrameconn(),this.userView.getA0100(),Integer.parseInt(planId));
		 codebo.initdata();
		 if (list.size()>0) 
		 {
				for(int i=0;i<list.size();i++)
				{			
					PerPointBean ppb=(PerPointBean)list.get(i);
					if(ppb.getValue()==null|| "".equals(ppb.getValue()))
						   continue;
					if("q".equals(ppb.getType()))
					{
							/**有效验证*/
						    
							if(Float.parseFloat(ppb.getValue())<ppb.getBottomNum())
							{
								this.getFormHM().put("message","输入的值小于了下限值,请重新输入!");
								return;
							}
							if(Float.parseFloat(ppb.getValue())>ppb.getTopNum())
							{
								this.getFormHM().put("message","输入的值大于了上限值,请重新输入!");
								return;
							}
							if(Float.parseFloat(ppb.getValue())<0)
							{
								this.getFormHM().put("message","值不能小于0!");
								return;
							}
							amountToScore.dlAmountToScoreGrade(ppb,(String)htxml.get("limitrule"));
							listquantity.add(ppb);
					}
					else
					{
							/**录入是代码时，转换成分值*/
							amountToScore.gradeCodeToScore(ppb,(String)htxml.get("limitrule"));		
							/**有效验证*/							
//							if(Float.parseFloat(ppb.getValue())>ppb.getMaxScore())
//							{
//								this.getFormHM().put("message","输入的值大于了最高分值,请重新输入!");
//								return;
//							}
//							if(Float.parseFloat(ppb.getValue())<0)
//							{
//								this.getFormHM().put("message","值不能小于0!");
//								return;
//							}
							if(ppb.getScore() >ppb.getMaxScore())
							{
								this.getFormHM().put("message","输入的值大于了最高分值,请重新输入!");
								return;
							}
							if(ppb.getScore()<0)
							{
								this.getFormHM().put("message","值不能小于0!");
								return;
							}							
							listpointid.add(ppb);
					}
					if(codebo.isOverLimitation((String)htxml.get("limitation"),ppb.getPoint_id(),ppb.getGradecode()))
					{
						this.getFormHM().put("message","有些考核指标最高标度超过比例或个数限制!");						
						return ;
					}
				}
			}
			else
			{
				this.getFormHM().put("message","数据没填写完成不能提交");
				return;
			}
		
		  /**
         * 过滤
         */
		 listpointid=getFilter(listpointid,nowPlanNum);
		 listquantity=getFilter(listquantity,nowPlanNum);
		 /**
		 * 所有为空提示
		 */
		 if(listpointid.size()<=0 && listquantity.size()<=0)
		 {
			this.getFormHM().put("message","数据没填写完成不能提交");
			return;
		 }
		SaveSorce(listpointid,listquantity,tableName,objectId);		
		/**
		 * 执行了解程度及总体评价
		 */
		doWholeDegree(wholeEven ,knowDegree,nowPlanNum,objectId);
		/**保存打分提交状态*/		
		PerMainBody permainbody=new PerMainBody(this.getFrameconn());
		permainbody.updateEditStatus(objectId,userView.getA0100(),nowPlanNum,"1");
		this.getFormHM().put("status","1");		
	  }
      catch(Exception ex)
      {
    	  ex.printStackTrace();
    	  throw GeneralExceptionHandler.Handle(ex);
      }
	}
	
	
	/**
	 * 保存打分 chenmengqing added 20051103
	 * @param listpointid
	 * @param listquantity
	 * @param tableName
	 * @param planId
	 */
	void SaveSorce(ArrayList listpointid, ArrayList listquantity,String tableName,String objectId)
	{
		saveDxScore(listpointid, tableName,objectId);
		saveDlScore(listquantity, tableName,objectId);		
	}

	/**保存定量指标值
	 * @param listpointid
	 * @param tableName
	 */
	private void saveDlScore(ArrayList listquantity, String tableName,String objectId) {
		StringBuffer strquery=new StringBuffer();
		strquery.append("select id from ");
		strquery.append(tableName);
		strquery.append(" where point_id=? and object_id=? and mainbody_id=?");
		
		StringBuffer strInsert=new StringBuffer();
		StringBuffer strUpdate=new StringBuffer();
		strUpdate.append("update  ");
		strUpdate.append(tableName);
		strUpdate.append(" set score=?,amount=?,degree_id=? where point_id=? and object_id=? and mainbody_id=?");		

		strInsert.append("insert into ");
		strInsert.append(tableName);
		strInsert.append(" (id,object_id,mainbody_id,score,amount,degree_id,point_id) values (?,?,?,?,?,?,?)");

		
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		ArrayList params=new ArrayList();
		ArrayList insertparams=new ArrayList();
		ArrayList updateparams=new ArrayList();
		int idNum=0;
		int num[]=null;
		try
		{
			for(int i=0;i<listquantity.size();i++)
			{
				PerPointBean vo = (PerPointBean) listquantity.get(i);
				params.add(vo.getPoint_id());
				params.add(objectId);
				params.add(this.userView.getA0100());
				this.frowset=dao.search(strquery.toString(),params);
				//amountToScore.getAmountToScore(vo.getPoint_id(),vo.getValue(),"1");				
				if(this.frowset.next())
				{
					//update data
					ArrayList temp=new ArrayList();	
					temp.add(Double.toString(vo.getScore())/*amountToScore.getScore()*/);
					temp.add(vo.getValue());
					temp.add(vo.getGradecode()/*amountToScore.getGradecode()*/);					
					temp.add(vo.getPoint_id());
					temp.add(objectId);
					temp.add(this.userView.getA0100());
					updateparams.add(temp);
				}
				else
				{
					//insert data
					ArrayList temp=new ArrayList();
					idNum=getMaxNum(tableName);
					temp.add(Integer.toString(idNum));
					temp.add(objectId);
					temp.add(this.userView.getA0100());
					temp.add(Double.toString(vo.getScore())/*amountToScore.getScore()*/);
					temp.add(vo.getValue());
					temp.add(vo.getGradecode()/*amountToScore.getGradecode()*/);					
					temp.add(vo.getPoint_id());	
					insertparams.add(temp);
				}
				/**参数清空*/
				params.clear();
			}//for loop end.
			if(insertparams.size()>0)
				num=dao.batchUpdate(strInsert.toString(),insertparams);
			if(updateparams.size()>0)
				num=dao.batchUpdate(strUpdate.toString(),updateparams);
			if(num!=null)
				this.getFormHM().put("message","操作成功");				
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		}
	}
	
	/**保存定性
	 * @param listpointid
	 * @param tableName
	 */
	private void saveDxScore(ArrayList listpointid, String tableName,String objectId) {
		StringBuffer strquery=new StringBuffer();
		strquery.append("select id from ");
		strquery.append(tableName);
		strquery.append(" where point_id=? and object_id=? and mainbody_id=?");
		
		StringBuffer strInsert=new StringBuffer();
		StringBuffer strUpdate=new StringBuffer();
		strUpdate.append("update ");
		strUpdate.append( tableName);
		strUpdate.append(" set score=?,degree_id=? where point_id=? and object_id=? and mainbody_id=?");		

		strInsert.append("insert into ");
		strInsert.append( tableName);
		strInsert.append(" (id,object_id,mainbody_id,score,degree_id,point_id) values (?,?,?,?,?,?)");
		
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		ArrayList params=new ArrayList();
		ArrayList insertparams=new ArrayList();
		ArrayList updateparams=new ArrayList();
		int idNum=0;
		int num[]=null;
		try
		{
			for(int i=0;i<listpointid.size();i++)
			{
				PerPointBean vo = (PerPointBean) listpointid.get(i);
				params.add(vo.getPoint_id());
				params.add(objectId);
				params.add(this.userView.getA0100());
				this.frowset=dao.search(strquery.toString(),params);
				if(this.frowset.next())
				{
					//update data
					ArrayList temp=new ArrayList();		
					temp.add(Double.toString(vo.getScore())/* vo.getValue()*/);
					temp.add(vo.getGradecode());
					temp.add(vo.getPoint_id());
					temp.add(objectId);
					temp.add(this.userView.getA0100());
					updateparams.add(temp);
				}
				else
				{
					//insert data
					ArrayList temp=new ArrayList();
					idNum=getMaxNum(tableName);
					temp.add(Integer.toString(idNum));
					temp.add(objectId);
					temp.add(this.userView.getA0100());
					temp.add(Double.toString(vo.getScore())/*vo.getValue()*/);
					temp.add(vo.getGradecode());					
					temp.add(vo.getPoint_id());	
					insertparams.add(temp);
				}
				/**参数清空*/
				params.clear();
			}//for loop end.
			if(insertparams.size()>0)
				num=dao.batchUpdate(strInsert.toString(),insertparams);
			if(updateparams.size()>0)
				num=dao.batchUpdate(strUpdate.toString(),updateparams);
			if(num!=null)
				this.getFormHM().put("message","操作成功");				
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		}
	}
	
	/**
	 * 更新操作,
	 * @param list
	 * @param planId
	 * @return
	 */
	void doUpdateOper(ArrayList listpointid, ArrayList listquantity,
			String tableName,String planId,String objectId)
	{
		StringBuffer sqlpointid = new StringBuffer();
		sqlpointid.append("update ");
		sqlpointid.append( tableName);
		sqlpointid.append(" set score=? where point_id=? and object_id=? and mainbody_id=?");

		StringBuffer sqlquery =new StringBuffer();
		sqlquery.append("update  ");
		sqlquery.append(tableName);
		sqlquery.append(" set score=?,amount=? where point_id=? and object_id=? and mainbody_id=?");

		ContentDAO dao = new ContentDAO(this.getFrameconn());
		/**
		 * 定性值的更新
		 */
		int num=0;
		try
		{
			for (int i = 0; i < listpointid.size(); i++) 
			{
				PerPointBean vo = (PerPointBean) listpointid.get(i);
				
				ArrayList lst = new ArrayList();
				
				lst.add(vo.getValue());
				lst.add(vo.getPoint_id());
				lst.add(objectId);
				lst.add(this.userView.getA0100());
				
				num=dao.update(sqlpointid.toString(),lst);
				
			}
			if(num>0)
			{
				this.getFormHM().put("message","提交成功");
			}
		}
		catch(Exception ex)
		{
			System.out.println("----->com.hjsj.hrms.transaction.performance.SaveAppraiseMutualTrans--->doUpdateOper 定性值更新 error");
			ex.printStackTrace();
		}
		
		/**
		 * 定量值的更新
		 */
		try
		{
			for (int i = 0; i < listquantity.size(); i++) {
				PerPointBean vo = (PerPointBean) listquantity.get(i);
				
				ArrayList lst = new ArrayList();
				lst.add(amountToScore.getAmountToScore(vo.getPoint_id(),vo.getValue()));			
				lst.add(vo.getValue());
				lst.add(vo.getPoint_id());
				lst.add(objectId);
				lst.add(this.userView.getA0100());
				
				num=dao.update(sqlquery.toString(), lst);
				
			}
			if(num>0)
			{
				this.getFormHM().put("message","提交成功");
			}
		}
		catch(Exception ex)
		{
			System.out.println("----->com.hjsj.hrms.transaction.performance.SaveAppraiseselfTrans--->doUpdateOper 定性值更新 error");
			ex.printStackTrace();
		}
		
	}
	
	/**
	 * 过滤方法
	 */
	ArrayList getFilter(ArrayList list,String planId)
	{
		/**
		 * 得到模板号
		 */
		String sql = "select plan_id,template_id from per_plan where  plan_id='"
			+ planId + "'";
		String template_id="0";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try 
		{
			this.frowset = dao.search(sql);
			if (this.frowset.next()) 
			{
				template_id = this.frowset.getString("template_id");
			}
		} 
	    catch (Exception ex) 
	    {
			cat	.debug("----->com.hjsj.hrms.transaction.performance.SaveAppraiseselfTrans--->read planid second error");
			ex.printStackTrace();
		}
		
		ArrayList secondpointlist=new ArrayList();
		/**
		 * 过滤pointlist
		 */
		String sql2="select point_id from per_template_point where item_id in (select item_id from per_template_item where template_id='"+template_id+"')";
		ResultSet rs2=null;
		ArrayList newPointlist=new ArrayList();
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
		
		for(int i=0;i<list.size();i++)
		{
			PerPointBean ppb=(PerPointBean)list.get(i);
			if(newPointlist.contains(ppb.getPoint_id()))
			{
				secondpointlist.add(ppb);
			}
		}
		return secondpointlist;
	}
	/**
	 * 执行总体评价
	 */
	public void doWholeDegree(String wholeEven ,String knowDegree,String nowPlanNum,String objectId)
	{
		if("".equals(wholeEven) && "".equals(knowDegree))
		{
			this.getFormHM().put("wholeEven","");
		 	this.getFormHM().put("knowDegree","");
		 	return ;
		}
		StringBuffer sb=new StringBuffer();		
		if("".equals(wholeEven) && !"".equals(knowDegree))
		{
			sb.append("update per_mainbody set know_id=");
			sb.append(knowDegree);
			sb.append(" where object_id='");
			sb.append(objectId);
			sb.append("' and mainbody_id='");
			sb.append(this.userView.getA0100());
			sb.append("' and plan_id='");
			sb.append(nowPlanNum);
			sb.append("'");
		}
		else if(!"".equals(wholeEven) && "".equals(knowDegree))
		{
			sb.append("update per_mainbody set whole_grade_id=");
			sb.append(wholeEven);
			sb.append(" where object_id='");
			sb.append(objectId);
			sb.append("' and mainbody_id='");
			sb.append(this.userView.getA0100());
			sb.append("' and plan_id='");
			sb.append(nowPlanNum);
			sb.append("'");
		}
		else if(!"".equals(wholeEven) && !"".equals(knowDegree))
		{
			sb.append("update per_mainbody set whole_grade_id=");
			sb.append(wholeEven);
			sb.append(",know_id=");
			sb.append(knowDegree);
			sb.append(" where object_id='");
			sb.append(objectId);
			sb.append("' and mainbody_id='");
			sb.append(this.userView.getA0100());
			sb.append("' and plan_id='");
			sb.append(nowPlanNum);
			sb.append("'");
		}
		else
		{
			this.getFormHM().put("wholeEven","");
			this.getFormHM().put("knowDegree","");
			return;			
		}		
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			dao.update(sb.toString());
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		}		
		this.getFormHM().put("wholeEven","");
	 	this.getFormHM().put("knowDegree","");
	}
	/**
	 * 执行插入操作
	 * @param listpointid
	 * @param listquantity
	 * @param tableName
	 * @param objectId
	 */
	public void doInsertOper(ArrayList listpointid, ArrayList listquantity,
			String tableName,String objectId) {
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try 
		{
				StringBuffer sqlpointid = new StringBuffer();
				sqlpointid.append("insert into ");
				sqlpointid.append( tableName);
				sqlpointid.append(" (id,object_id,mainbody_id,score,point_id) values (?,?,?,?,?)");
				StringBuffer sqlquery =new StringBuffer();
				sqlquery.append("insert into ");
				sqlquery.append(tableName);
				sqlquery.append(" (id,object_id,mainbody_id,score,amount,point_id) values (?,?,?,?,?,?)");
				/**得到id号*/
				int idNum = 0;
				this.frowset = dao.search("select max(id) as nowmaxid from  "
					+ tableName);
				if (this.frowset.next()) 
				{
					idNum = this.frowset.getInt("nowmaxid");
				}
			/**定性值的添加*/
			int num=0;
			if (listpointid.size() > 0) 
			{
				for (int i = 0; i < listpointid.size(); i++) 
				{
					PerPointBean vo = (PerPointBean) listpointid.get(i);
					ArrayList lst = new ArrayList();
					idNum=getMaxNum(tableName);
					//idNum=idNum+1;
					lst.add(Integer.toString(idNum));
					lst.add(objectId);
					lst.add(this.userView.getA0100());
					lst.add(vo.getValue());
					lst.add(vo.getPoint_id());
					num=dao.insert(sqlpointid.toString(),lst);
				}
				if(num>0)
				{
					this.getFormHM().put("insertUpdateFlag","1");
					/**设为提交状态*/
					String sql="update per_mainbody set status=2 where object_id='"+objectId+"' and mainbody_id='"+this.userView.getA0100()+"' and plan_id="+planId;
					dao.update(sql);
				}
			}
			/**定量值的添加*/
			if (listquantity.size() > 0) {

				for (int i = 0; i < listquantity.size(); i++) 
				{
					PerPointBean vo = (PerPointBean)listquantity.get(i);
					ArrayList lst = new ArrayList();
					idNum=getMaxNum(tableName);
					//idNum=idNum+1;
					lst.add(Integer.toString(idNum));
					lst.add(objectId);
					lst.add(this.userView.getA0100());
					lst.add(amountToScore.getAmountToScore(vo.getPoint_id(),vo.getValue()));
					lst.add(vo.getValue());
					lst.add(vo.getPoint_id());
				    num=dao.insert(sqlquery.toString(), lst);
				}
			}
			if(num>0)
			{
				this.getFormHM().put("insertUpdateFlag","1");
				this.getFormHM().put("message","提交成功");
				/**设为提交状态*/
				String sql="update per_mainbody set status=2 where object_id='"+objectId+"' and mainbody_id='"+this.userView.getA0100()+"' and plan_id="+planId;
				dao.update(sql);
			}
		} 
		catch (Exception ex) 
		{
			ex.printStackTrace();
		}	
}
			 /**
			  * 得到最大id号
			  *  
			  */
	int getMaxNum(String tableName) {
		int idNum = 0;
		ContentDAO dao = new ContentDAO(this.getFrameconn());
        IDGenerator idg=new IDGenerator(2,this.getFrameconn());		
		try {
	        String id=idg.getId("per_table_xxx.id");
	        idNum=Integer.parseInt(id);			
//			this.frowset = dao.search("select max(id) as nowmaxid from  "
//					+ tableName);
//			if (this.frowset.next()) {
//				idNum = this.frowset.getInt("nowmaxid");
//			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return idNum;
	}
}
