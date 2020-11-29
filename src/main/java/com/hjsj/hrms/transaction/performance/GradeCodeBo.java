/**
 * 
 */
package com.hjsj.hrms.transaction.performance;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:得到标准代码</p>
 * <p>Description:主要用于标度转换限制规则计算，比如对于一些标准不能
 * 　　超过一定的比例或个数
 * </p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-2-21:9:34:49</p>
 * @author chenmengqing
 * @version 1.0
 */
public class GradeCodeBo {

	private String body_id;
	private String tablename;
	private Connection conn;
	/**计划号*/
	private int plan_id;
	private RecordVo plan_vo = null;               // 考核计划信息vo
	/**打分对象个数*/
	private int ncount=0;
	/**考核要素对应的打分标准个数字典*/
	private HashMap hm=new HashMap();
	
	public GradeCodeBo(Connection conn,String body_id,int plan_id) {
		this.conn=conn;
		this.body_id=body_id;
		this.tablename="per_table_"+plan_id;
		this.plan_id=plan_id;
		this.plan_vo=getPlanVo(String.valueOf(plan_id));
		this.ncount=getObjectIdCount();
	}
	/**
	 * 获得计划信息
	 * @param planid
	 * @return
	 */
	public RecordVo getPlanVo(String planid)
	{
		RecordVo vo=new RecordVo("per_plan");
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			vo.setInt("plan_id",Integer.parseInt(planid));
			vo=dao.findByPrimaryKey(vo);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return vo;
	}
	/**
	 * 从标准标度模块取得所有的标度值
	 * @return
	 */
	private ArrayList getGradeCodeTemplate()
	{
		String per_comTable = "per_grade_template"; // 绩效标准标度
		if(String.valueOf(this.plan_vo.getInt("busitype"))!=null && String.valueOf(this.plan_vo.getInt("busitype")).trim().length()>0 && this.plan_vo.getInt("busitype")==1)
			per_comTable = "per_grade_competence"; // 能力素质标准标度
		ArrayList codelist=new ArrayList();
		StringBuffer strsql=new StringBuffer();
		strsql.append("select grade_template_id from "+per_comTable+" order by gradevalue desc");
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rset=null;
		try
		{
				rset=dao.search(strsql.toString());
				while(rset.next())
					codelist.add(rset.getString("grade_template_id"));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			try
			{
				if(rset!=null)
					rset.close();
			}
			catch(Exception exx)
			{
				exx.printStackTrace();
			}
		}
		return codelist;
	}
	
	/**
	 * 查找每个标度现在得A有多少？
	 *
	 */
	public void initdata()
	{
		StringBuffer strsql=new StringBuffer();
		//select count(*),point_id from per_table_3 where mainbody_id='00000014'and object_id<>'00000014' and degree_id='A' group by point_id,;
		ArrayList list=getGradeCodeTemplate();
		strsql.append("select count(*) as nrec,point_id from ");
		strsql.append(this.tablename);
		strsql.append(" where mainbody_id='");
		strsql.append(this.body_id);
		strsql.append("' and degree_id=? group by point_id");
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rset=null;
		ArrayList paralist=new ArrayList();
		try
		{
			for(int i=0;i<list.size();i++)
			{
				/**暂时仅对最高标度的实施限制*/
				if(i!=0)
					continue;
				String gradecode=(String)list.get(i);
				paralist.add(gradecode);
				rset=dao.search(strsql.toString(),paralist);
				while(rset.next())
				{
					String point_id=rset.getString("point_id");
					if(this.hm.get(point_id)==null)
					{
						DynaBean vo=new LazyDynaBean();
						vo.set(gradecode,String.valueOf(rset.getInt("nrec")));
						this.hm.put(point_id,vo);
					}
					else
					{
						DynaBean temp=(DynaBean)this.hm.get(point_id);
						temp.set(gradecode,String.valueOf(rset.getInt("nrec")));
						temp.set(point_id,temp);
					}
				}
				paralist.clear();
			}
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			try
			{
				if(rset!=null)
					rset.close();
			}
			catch(Exception exx)
			{
				exx.printStackTrace();
			}
		}
		
	}
	/**
	 * 是否超过限制
	 * @return
	 */
	public boolean isOverLimitation(String limitation,String point_id,String gradecode)	
    {
		if("-1".equals(limitation))
			return false;
    	boolean bflag=false;
    	DynaBean temp=(DynaBean)this.hm.get(point_id);
    	if(temp!=null)
    	{
    		Object ss=temp.get(gradecode);
    		if(ss!=null)
    		{
    			long nsum=0;
        		int ncount=Integer.parseInt((String)ss);
//        		System.out.println("count="+ncount);
        		double lim=Double.parseDouble(limitation);
        		if(lim<1)
        		{
        			nsum=Math.round((this.ncount*lim));
        			System.out.println("nsum="+nsum);
        			if((ncount+1)>nsum)
        				bflag=true;
        		}
        		else
        		{
//        			System.out.println("limitation="+limitation);        			
        			if((ncount+1)>Integer.parseInt(limitation))
        				bflag=true;
        		}
        			
    		}
    	}
    	return bflag;
    }
	/**
	 * 根据考核计划号和考核主体查询对对应的考核对象的
	 * 个数，主要为了计算最高标准所占的比例
	 * @return
	 */
	private int getObjectIdCount()
	{
		int nrec=0;
		StringBuffer strsql=new StringBuffer();
		strsql.append("select count(*) as nrec from per_mainbody where mainbody_id='");
		strsql.append(this.body_id);
		strsql.append("' and plan_id=");
		strsql.append(this.plan_id);
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rset=null;
		try
		{
			rset=dao.search(strsql.toString());
			if(rset.next())
				nrec=rset.getInt("nrec");
		}
		catch(Exception ex)
		{
		  ex.printStackTrace();
		}
		finally
		{
		  try
		  {
			if(rset!=null)
				rset.close();
		  }
		  catch(Exception ee)
		  {
			  ee.printStackTrace();
		  }
		}
		return nrec;
	}
}
