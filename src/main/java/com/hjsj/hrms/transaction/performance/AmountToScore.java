/*
 * 创建日期 2005-8-25
 *
 * TODO 要更改此生成的文件的模板，请转至
 * 窗口 － 首选项 － Java － 代码样式 － 代码模板
 */
package com.hjsj.hrms.transaction.performance;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * @author luangaojiong
 *
 * 定量值到定性的转换类
 */
public class AmountToScore {

	ArrayList perPointlst = new ArrayList();

	ArrayList perPointGradelist = new ArrayList();
	
	private Connection conn;
	/**分值*/
	private String score="0";
	/**标度代码*/
	private String gradecode="";
	/**
	 * 初始化数据
	 *
	 */
	public AmountToScore(String planId) {
		
		/**
		 * 得到模板号
		 */
		String sql = "select plan_id,template_id from per_plan where  plan_id='"
			+ planId + "'";
		String template_id="0";
		Connection con = null;
		ResultSet rs = null;
	try {
			con = (Connection) AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(con);
			rs=dao.search(sql);
			
			if (rs.next()) {
			
				template_id = PubFunc.NullToZero(rs.getString("template_id"));
			}
		
		
		} catch (Exception ex) {
			//cat	.debug("----->com.hjsj.hrms.transaction.performance.SaveAppraiseselfTrans--->read planid second error");
			System.out.println("----->com.hjsj.hrms.transaction.performance.AmountToScore-AmountToScore--> error");
			ex.printStackTrace();
		}
		finally
		{
			PubFunc.closeResource(rs);
			PubFunc.closeResource(con);
		}
		
		getPerGradeAllObject();		//得到标度表对象
		getPerPointAllObject(template_id);
		
		

	}
	/**
	 * 构造函数
	 * @param conn
	 */
	public AmountToScore(Connection conn)
	{
		this.conn=conn;
	}
	/**
	 * 得到考核指标要素表集合及其标度集合
	 *
	 */
	void getPerPointAllObject(String template_id)
	{
		Connection con = null;
		ResultSet rs = null;
		StringBuffer sb=new StringBuffer();
		String sql = "select per_template_point.point_id,per_template_point.score from per_template_point where  ";
		sb.append(sql);
		sb.append(" per_template_point.item_id in ( select item_id from per_template_item where template_id='");
		sb.append(template_id);
		sb.append("')");
		
		perPointlst = new ArrayList();
	
		try {
			con = (Connection) AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(con);
			rs = dao.search(sb.toString());
			while (rs.next()) {
				PerPointBean ppb=new PerPointBean();
				String pointNum=PubFunc.NullToZero(rs.getString("point_id"));
				ppb.setPoint_id(pointNum);
				ppb.setMaxScore(Double.parseDouble(PubFunc.NullToZero(rs.getString("score"))));
				ppb.setPerGradelist(getPerGradelstOfPoint(pointNum));
				perPointlst.add(ppb);
				
			}
		} catch (Exception ex) {
			System.out.println("------->com.hjsj.hrms.transaction.performance-->AmountToScore-->getPerPointArrObject error");
			ex.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
			PubFunc.closeResource(con);
		}
	}
	
	/**
	 * 得到对应要素的标度集合
	 *
	 */
	ArrayList getPerGradelstOfPoint(String pointid)
	{
		ArrayList lst=new ArrayList();
		for(int i=0;i<perPointGradelist.size();i++)
		{
			PerGradeBean pgb=(PerGradeBean)perPointGradelist.get(i);
			if(pgb.getPoint_id().equals(pointid))
			{
				lst.add(pgb);
				
			}
		}
		
		return lst;
	}

	/**
	 * 得到标度表中所有集合
	 */
	void getPerGradeAllObject() {
		Connection con = null;
		ResultSet rs = null;
		String sqlTemp = "select * from per_grade order by top_value desc";
		perPointGradelist = new ArrayList();
		try {
			con = (Connection) AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(con);
			rs = dao.search(sqlTemp);
			while (rs.next()) 
			{
				PerGradeBean pgb=new PerGradeBean();
				pgb.setGrade_id(rs.getInt("grade_id"));
				pgb.setPoint_id(rs.getString("point_id"));
				pgb.setGradevalue(rs.getDouble("gradevalue"));
				pgb.setGradedesc(PubFunc.nullToStr(rs.getString("gradedesc")));
				pgb.setGradecode(PubFunc.nullToStr(rs.getString("gradecode")));
				String topStr=PubFunc.NullToZero(rs.getString("top_value"));
				String bottomStr=PubFunc.NullToZero(rs.getString("bottom_value"));
				pgb.setTop_value(Double.parseDouble(topStr));
				pgb.setBottom_value(Double.parseDouble(bottomStr));
				if("0".equals(topStr) && "0".equals(bottomStr))
				{
					
				}
				else
				{
					perPointGradelist.add(pgb);
				}
			}
		} catch (Exception ex) {
			System.out.println("------->com.hjsj.hrms.transaction.performance-->AmountToScore-->getPerGradeAllObject error");
			ex.printStackTrace();
		} 
		finally 
		{
			PubFunc.closeResource(rs);
			PubFunc.closeResource(con);
		}
	}
	
	/**
	 * 返回定量值转换的定性值
	 * @return
	 */
	public String getAmountToScore(String pointid,String amount) {
		
		String scoreValue="0";
		PerPointBean ppb=null;
		String flag="0";
		for(int i=0;i<perPointlst.size();i++)
		{
			 ppb=(PerPointBean)perPointlst.get(i);
			 if(ppb.getPoint_id().equals(pointid))
			 {
			 	flag="1";
			 	break;
			 }
		}
		
		if("0".equals(flag))
		{
			return "0";
		}
		else
		{
			double maxScore=ppb.getMaxScore();			//要素定性最大值
			double amountvalue=Double.parseDouble(amount);
			
			ArrayList gradelist=(ArrayList)ppb.getPerGradelist();
			/**
			 * 计算出定性值
			 */
			for(int i=0;i<gradelist.size();i++)
			{
				PerGradeBean pgb=(PerGradeBean)gradelist.get(i);
			
				if((amountvalue>=pgb.getBottom_value()) && (amountvalue<=pgb.getTop_value()))
				{
					scoreValue=Double.toString(pgb.getGradevalue()*maxScore);
					break;
				}
			}
			
		}
		return scoreValue;

	}
	/**
	 * 得到考核指标对应的标度列表
	 * @param point_id
	 * @return
	 */
	private ArrayList getGradeList(String point_id)
	{
		ArrayList gradelist=new ArrayList();
		StringBuffer sql=new StringBuffer();
		sql.append("select * from per_grade where point_id='");
		sql.append(point_id);
		sql.append("' order by gradevalue desc");
		RowSet rset=null;
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			rset=dao.search(sql.toString());
			while(rset.next())
			{
				PerGradeBean vo=new PerGradeBean();
				vo.setGradevalue(rset.getDouble("gradevalue"));
				vo.setTop_value(rset.getDouble("top_value"));
				vo.setBottom_value(rset.getDouble("bottom_value"));
				gradelist.add(vo);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			PubFunc.closeResource(rset);
		}
		return gradelist;
	}
	/**
	 * 根据规则，把分值转换成对应的标度代码
	 * @param point_id
	 * @param rule
	 */
	public String comuteGradeCode(String point_id,String rule,String value,String maxscore)
	{
		double dmaxscore=Double.parseDouble(maxscore);
		String gradecode="";
		try
		{
			ArrayList gradelist=getGradeList(point_id);
			double score=Double.parseDouble(value);
			double bvalue=0;
			double tvalue=0;
			ArrayList codelist=new ArrayList();
			for(int i=0;i<gradelist.size();i++)
			{
				PerGradeBean pgb=(PerGradeBean)gradelist.get(i);
				bvalue=dmaxscore*pgb.getBottom_value();
				tvalue=dmaxscore*pgb.getTop_value();
				if((score>=bvalue) && (score<=tvalue))
				{
					codelist.add(pgb.getGradecode());
				}
			}
		
			if(codelist.size()==0)
				return "";
			if("1".equals(rule))//就高
			{
				gradecode= (String)codelist.get(0);
			}
			else
			{
				gradecode= (String)codelist.get(codelist.size()-1);			
			}		
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return gradecode;
	}
	
	
	public void comuteGradeCode(PerPointBean ppb,String rule)
	{
		try
		{
			ArrayList gradelist=(ArrayList)ppb.getPerGradelist();
			double score=Double.parseDouble(ppb.getValue());
			double bvalue=0;
			double tvalue=0;
			ArrayList codelist=new ArrayList();
			for(int i=0;i<gradelist.size();i++)
			{
				PerGradeBean pgb=(PerGradeBean)gradelist.get(i);
				bvalue=ppb.getMaxScore()*pgb.getBottom_value();
				tvalue=ppb.getMaxScore()*pgb.getTop_value();
//				System.out.println("maxscore="+ppb.getMaxScore());
//				System.out.println("b="+pgb.getBottom_value());
//				System.out.println("t="+pgb.getTop_value());
				
				if((score>=bvalue) && (score<=tvalue))
				{
					codelist.add(pgb.getGradecode());
//					System.out.println("===="+i+pgb.getGradecode());
					//break;
				}
			}
			ppb.setScore(score);			
			if(codelist.size()==0)
				return;

			if("1".equals(rule))//就高
			{
				ppb.setGradecode((String)codelist.get(0));
			}
			else
			{
				ppb.setGradecode((String)codelist.get(codelist.size()-1));			
			}		
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	/**
	 * 分析字符串是否为数值类型
	 * @param value
	 * @return
	 */
	public boolean isNumberic(String value)
	{
		try
		{
			Double.parseDouble(value);
			return true;
		}
		catch(Exception ex)
		{
			return false;
		}
	}
	/**
	 * 定性指标
	 * 标度和数值转换函数
	 * @param vo
	 * @param rule
	 * @return
	 */
	public boolean gradeCodeToScore(PerPointBean vo,String rule)
	{
		double score=0;
		boolean bflag=false;
		String gradecode=vo.getValue();
		ArrayList gradelist=(ArrayList)vo.getPerGradelist();
		if(isNumberic(gradecode.trim()))//代码值
		{
			comuteGradeCode(vo,rule);
		}
		else//代码标准
		{
			for(int i=0;i<gradelist.size();i++)
			{
				PerGradeBean gvo=(PerGradeBean)gradelist.get(i);
				if(gradecode.equals(gvo.getGradecode()))
				{
					score=gvo.gradevalue*vo.getMaxScore();
					vo.setScore(score);
					vo.setGradecode(gradecode);
					break;
				}
			}
		}
		return bflag;
	}
	
	/**
	 * 定量指标计算分值及代码
	 * @param vo
	 * @param rule
	 */
	public void dlAmountToScoreGrade(PerPointBean vo,String rule)
	{
		ArrayList gradelist=(ArrayList)vo.getPerGradelist();
		ArrayList codelist=new ArrayList();
		ArrayList scorelist=new ArrayList();		
		/**
		 * 计算出定性值
		 */
		double maxScore=vo.getMaxScore();			//要素定性最大值
		double amountvalue=Double.parseDouble(vo.getValue());		
		for(int i=0;i<gradelist.size();i++)
		{
			PerGradeBean pgb=(PerGradeBean)gradelist.get(i);
			if((amountvalue>=pgb.getBottom_value()) && (amountvalue<=pgb.getTop_value()))
			{
				scorelist.add(Double.toString(pgb.getGradevalue()*maxScore));
				codelist.add(pgb.getGradecode());
			}
		}
		if("1".equals(rule))
		{
			vo.setGradecode((String)codelist.get(0));
			vo.setScore(Double.parseDouble((String)scorelist.get(0)));
		}
		else
		{
			vo.setGradecode((String)codelist.get(codelist.size()-1));
			vo.setScore(Double.parseDouble((String)scorelist.get(scorelist.size()-1)));			
		}
	}
	/**
	 * 得到对应代码值和分值
	 * @param pointid
	 * @param amount
	 * @param rule
	 * @return
	 */
	public void getAmountToScore(String pointid,String amount,String rule) {
		
		PerPointBean ppb=null;
		String flag="0";
		ArrayList codelist=new ArrayList();
		ArrayList scorelist=new ArrayList();
		for(int i=0;i<perPointlst.size();i++)
		{
			 ppb=(PerPointBean)perPointlst.get(i);
			 if(ppb.getPoint_id().equals(pointid))
			 {
			 	flag="1";
			 	break;
			 }
		}
		
		if("0".equals(flag))
		{
			return;
		}
		else
		{
			double maxScore=ppb.getMaxScore();			//要素定性最大值
			double amountvalue=Double.parseDouble(amount);
			
			ArrayList gradelist=(ArrayList)ppb.getPerGradelist();
			/**
			 * 计算出定性值
			 */
			for(int i=0;i<gradelist.size();i++)
			{
				PerGradeBean pgb=(PerGradeBean)gradelist.get(i);
				if((amountvalue>=pgb.getBottom_value()) && (amountvalue<=pgb.getTop_value()))
				{
					scorelist.add(Double.toString(pgb.getGradevalue()*maxScore));
					codelist.add(pgb.getGradecode());
					//break;
				}
			}
			if("1".equals(rule))
			{
				this.score=(String)scorelist.get(0);
				this.gradecode=(String)codelist.get(0);
			}
			else
			{
				this.score=(String)scorelist.get(scorelist.size()-1);
				this.gradecode=(String)codelist.get(codelist.size()-1);				
			}
		}
	}
	
	public String getGradecode() {
		return gradecode;
	}
	public void setGradecode(String gradecode) {
		this.gradecode = gradecode;
	}
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}

}
