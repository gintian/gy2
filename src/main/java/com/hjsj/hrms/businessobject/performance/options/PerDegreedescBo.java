package com.hjsj.hrms.businessobject.performance.options;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * <p>Title:PerDegreedescBo.java</p>
 * <p>Description>:项目等级分类</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jan 17, 2011 12:15:35 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: JinChunhai
 */

public class PerDegreedescBo
{

    private String id;
    private String degreeId;
    private String itemname;
    private String topscore;
    private String bottomscore;
    private String itemdesc;
    private String percentvalue;
    private String strict;
    private String flag;
    private Connection conn = null;

    public PerDegreedescBo(Connection conn)
    {
    	this.conn = conn;
    }   

    public ArrayList searchPerDegreedescList(String degreeId) throws GeneralException
    {

		ArrayList list = new ArrayList();
		StringBuffer buf = new StringBuffer();
		try
		{
		    buf.append(" select id,degree_id,itemname,topscore,bottomscore,itemdesc,percentvalue,strict,flag from per_degreedesc where  degree_id=" + degreeId);
		    ContentDAO dao = new ContentDAO(this.conn);
		    RowSet rset = dao.search(buf.toString());
		    while (rset.next())
		    {
				LazyDynaBean lazyvo = new LazyDynaBean();
				lazyvo.set("id", rset.getString("id"));
				lazyvo.set("degreeId", rset.getString("degree_id"));
				lazyvo.set("itemname", rset.getString("itemname"));
				
				String topscore=(String)rset.getString("topscore")==null?"":(String)rset.getString("topscore");
				if(topscore!=null && topscore.trim().length()>0) {
                    topscore=Double.toString(Double.parseDouble(topscore));//去掉小数点后面的0
                }
				lazyvo.set("topscore", topscore);
				
				String bottomscore = (String)rset.getString("bottomscore")==null?"":(String)rset.getString("bottomscore");
				if(bottomscore!=null && bottomscore.trim().length()>0) {
                    bottomscore=Double.toString(Double.parseDouble(bottomscore));//去掉小数点后面的0
                }
				lazyvo.set("bottomscore", bottomscore);
				
/*				lazyvo.set("topscore", rset.getString("topscore"));
				String bottomscore = rset.getString("bottomscore");
				if (bottomscore == null)
				{
				    bottomscore = "";
				}
				lazyvo.set("bottomscore", bottomscore);
*/				
				lazyvo.set("itemdesc", rset.getString("itemdesc"));
				lazyvo.set("percentvalue", rset.getString("percentvalue"));
				String strict = rset.getString("strict");
				if (strict == null)
				{
				    strict = "";
				}
				lazyvo.set("strict", strict);
				lazyvo.set("flag", rset.getString("flag"));
				list.add(lazyvo);
		    }
		    
		    if(rset!=null) {
                rset.close();
            }
	
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
		return list;
    }

    public RecordVo getVo(String id)
    {
		RecordVo vo = new RecordVo("per_degreedesc");
		
		try
		{
		    vo.setInt("id", Integer.parseInt(id));
		    ContentDAO dao = new ContentDAO(this.conn);
		    vo = dao.findByPrimaryKey(vo);
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return vo;
    }
    /**测试当前的记录是否为第一条*/
    public boolean isTheFirst(String id,String degreeId)
    {
		boolean flag=false;
		String strSql ="select id from per_degreedesc where degree_id=" + degreeId + " order by id";
	    ContentDAO dao = new ContentDAO(this.conn);
		try
		{
		    RowSet rset = dao.search(strSql);
		    String temp = null;
		    boolean isExist=false;
		    if(rset.next())//取第一个
		    {
				temp = rset.getString("id");
				isExist=true;
		    }
		    if(!isExist && (id==null || id!=null && id.length()==0)) {
                flag=true;
            }
		    if(isExist && id!=null)
		    {
				if(temp!=null && temp.equals(id)) {
                    flag=true;
                }
		    }
		    
		    if(rset!=null) {
                rset.close();
            }
		    	
		} catch (SQLException e)
		{
		    e.printStackTrace();
		}
		return flag;
    }
    /**测试当前的记录是否为第一条和最后一条*/
    public boolean isFirstOrLast(String id,String degreeId)
    {
		boolean flag=false;
		String strSql ="select * from per_degreedesc where degree_id=" + degreeId + " order by id";
	    ContentDAO dao = new ContentDAO(this.conn);
		try
		{
		    RowSet rset = dao.search(strSql);
		    String temp = null;
		    int num=0;
		    while(rset.next())
		    {
				temp = rset.getString("id");
				num++;
				if(num==1 && (id!=null && id.equals(temp))) {
                    return true;//是第一条
                }
		    }	    
	
		    if(id!=null)
		    {
				if(temp!=null && temp.equals(id)) {
                    flag=true;//是最后一条
                }
				if(id.length()==0) {
                    flag=true;//是第一条
                }
		    }
		    if(id==null) {
                flag=true;//是第一条
            }
		   
		    if(rset!=null) {
                rset.close();
            }
		    
		} catch (SQLException e)
		{
		    e.printStackTrace();
		}
		return flag;
    }
    
    public String getId()
    {
    	return id;
    }

    public void setId(String id)
    {
    	this.id = id;
    }

    public String getDegreeId()
    {
    	return degreeId;
    }

    public void setDegreeId(String degreeId)
    {
    	this.degreeId = degreeId;
    }

    public String getItemname()
    {
    	return itemname;
    }

    public void setItemname(String itemname)
    {
    	this.itemname = itemname;
    }

    public String getTopscore()
    {
    	return topscore;
    }

    public void setTopscore(String topscore)
    {
    	this.topscore = topscore;
    }

    public String getBottomscore()
    {
    	return bottomscore;
    }

    public void setBottomscore(String bottomscore)
    {
    	this.bottomscore = bottomscore;
    }

    public String getItemdesc()
    {
    	return itemdesc;
    }

    public void setItemdesc(String itemdesc)
    {
    	this.itemdesc = itemdesc;
    }

    public String getPercentvalue()
    {
    	return percentvalue;
    }

    public void setPercentvalue(String percentvalue)
    {
    	this.percentvalue = percentvalue;
    }

    public String getStrict()
    {
    	return strict;
    }

    public void setStrict(String strict)
    {
    	this.strict = strict;
    }

    public String getFlag()
    {
    	return flag;
    }

    public void setFlag(String flag)
    {
    	this.flag = flag;
    }
}
