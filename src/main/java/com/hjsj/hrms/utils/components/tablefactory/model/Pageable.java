/*
 * 
 * 
 * 
 */
package com.hjsj.hrms.utils.components.tablefactory.model;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.exception.GeneralException;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


/**
 * 分页信息
 * 
 * @author xujian
 * @version 3.0
 */
public class Pageable implements Serializable {


	private static final long serialVersionUID = -3930180379790344299L;

	/** 默认页码 */
	private static final int DEFAULT_PAGE_NUMBER = 1;

	/** 默认每页记录数 */
	private static final int DEFAULT_PAGE_SIZE = 20;

	/** 最大每页记录数 */
	private static final int MAX_PAGE_SIZE = 1000;

	/** 页码 */
	private int pageNumber = DEFAULT_PAGE_NUMBER;

	/** 每页记录数 */
	private int pageSize = DEFAULT_PAGE_SIZE;
	
	/** 总记录数 */
	private  long total=0;
	
	private String sql_str;
	
	private List dataList;

	/**
	 * 初始化一个新创建的Pageable对象
	 */
	public Pageable() {
	}

	/**
	 * 初始化一个新创建的Pageable对象
	 * 
	 * @param pageNumber
	 *            页码
	 * @param pageSize
	 *            每页记录数
	 */
	public Pageable(int pageNumber, int pageSize,long total) {
		if (pageNumber >= 1) {
			this.pageNumber = pageNumber;
		}
		if (pageSize >= 1 && pageSize <= MAX_PAGE_SIZE) {
			this.pageSize = pageSize;
		}
		this.total = total;
	}

	/**
	 * 获取页码
	 * 
	 * @return 页码
	 */
	public int getPageNumber() {
		return pageNumber;
	}

	/**
	 * 设置页码
	 * 
	 * @param pageNumber
	 *            页码
	 */
	public void setPageNumber(int pageNumber) {
		if (pageNumber < 1) {
			pageNumber = DEFAULT_PAGE_NUMBER;
		}
		this.pageNumber = pageNumber;
	}

	/**
	 * 获取每页记录数
	 * 
	 * @return 每页记录数
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * 设置每页记录数
	 * 
	 * @param pageSize
	 *            每页记录数
	 */
	public void setPageSize(int pageSize) {
		if (pageSize < 1 || pageSize > MAX_PAGE_SIZE) {
			pageSize = DEFAULT_PAGE_SIZE;
		}
		this.pageSize = pageSize;
	}
	
	/**
	 * 获取总页数
	 * 
	 * @return 总页数
	 */
	public int getTotalPages() {
		//System.out.println((int) Math.ceil((double) getTotal() / (double) getPageSize()));
		return (int) Math.ceil((double) getTotal() / (double) getPageSize());
	}

	/**
	 * 获取总记录数
	 * 
	 * @return 总记录数
	 */
	public long getTotal() {
		if(dataList!=null)
			total = dataList.size();
		else
			total = getMaxrows();
		return total;
	}
	
	public void setTotal(long total) {
		this.total=total;
	}
	
	/**定位到首页*/
	public void goFirstPage(){
		this.pageNumber=DEFAULT_PAGE_NUMBER;
	}
	
	/**定位到末页*/
	public void goEndPage(){
		this.pageNumber=this.getTotalPages();
	}
	
	/**定位到某页*/
	public void goPage(int pageNumber){
		if(pageNumber<DEFAULT_PAGE_NUMBER)
			pageNumber=DEFAULT_PAGE_NUMBER;
		if(pageNumber>this.getTotalPages())
			pageNumber = this.getTotalPages();
		this.pageNumber = pageNumber;
	}
	
	public void setDataList(List dataList){
		this.dataList = dataList;
	}
	
	public void setSql_str(String sql_str){
		this.sql_str = sql_str;
	}
	
	private long getMaxrows(){
		Connection conn = null;
		ResultSet rset =null;
		StringBuffer strsql = new StringBuffer();
		long icount=0;
		try{
		strsql.append("select count(*) as ncount from (");
  		strsql.append(this.sql_str);
  		strsql.append(") myset "); 
  	//System.out.println("str-ssss="+strsql.toString());            
        conn=AdminDb.getConnection();
        ContentDAO dao=new ContentDAO(conn);   
      //这里需要这样写的原因是有些业务模块使用分页便签时都自己在页面端缓存了sql语句（例如ajax使用），导致再次提交时走过滤器转为全角，
        //在这里和各位组长沟通后各业务模块不想改很多，就求全暂时会存在sql注入风险  xuj add 2013-11-18
        rset=dao.search(/*PubFunc.keyWord_reback(*/strsql.toString()/*)*/);
        if(rset.next())
        {
            icount=rset.getInt("ncount");
        }
    }
    catch(SQLException sqle)
    {
        sqle.printStackTrace();
    }        
    catch(GeneralException ge)
    {
        ge.printStackTrace();
    }
    finally
    {
        try
        {
            if(rset!=null)
                rset.close();
            if(conn!=null&&(!conn.isClosed()))
                conn.close();
        }
        catch(SQLException sqe)
        {
            sqe.printStackTrace();
        }
    }
    return icount;
	}
}