package com.hjsj.hrms.taglib.general;

import com.hjsj.hrms.utils.pagination.PaginationManager;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.taglib.TagUtility;
import com.hrms.struts.valueobject.Pagination;
import org.apache.struts.taglib.TagUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
/**
 * <p>Title:PaginationDb</p>
 * <p>Description:用于对数据库进行实时分页处理</p>
 * <p>Company:hjsj</p>
 * <p>create time:May 14, 2005:10:55:23 AM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class PaginationDb extends BodyTagSupport {
    /**
     * 分页处理类
     */
    private Pagination pagination;
    /**
     * 索引号
     */
    int indexesCount=0;
    
    protected Iterator iterator;
    /**
     * 查询字符串select * from xxxx
     */
    private String sql_str="";
    /**
     * 查询条件
     */
    private String where_str="";
    /**
     * 排序方式
     */
    private String order_by="";
    /**
     * 表名称
     */
    private String table="";
    /**
     * 需要显示的字段
     */
    private String columns="";
    /**求记录唯一性标识字段内容,可以是表达式*/
    private String distinct="";
    
    
    /**
     * 每页行数
     */
    private int pagerows=20;
    /**来源字典=0档案字典,=1业务字典,默认状态为整个扫描*/
    private String fromdict="0";
    /**备注类型指标的内容是否全部显示*/
    private String allmemo="0";    
    /**
     *总页数
     */
    private int maxpages=0;
    /**
     * 当前页号
     */
    private int currentpage=1;
    private String curpage="1";
    private String id;
    private String name;
    private String page_id;
    
    /**键值列表,主要用于分页优化查询速度*/
    private String keys;
    /**
     * @return Returns the page_id.
     */
    public String getPage_id() {
        return page_id;
    }
    /**
     * @param page_id The page_id to set.
     */
    public void setPage_id(String page_id) {
        this.page_id = page_id;
    }
    /**
     * 选中的索引序号
     */
    protected String indexes;   
    
    private String pageCount;
    
    /**
     * @return Returns the columns.
     */
    public String getColumns() {
        return columns;
    }
    /**
     * @param columns The columns to set.
     */
    public void setColumns(String columns) {
    	columns=columns.replace(",q.", ",");
    	columns=columns.replace(",A01.", ",");
        String value=TagUtility.getClassValue(this.pageContext,columns);            
        if(value!=null)
            value=value.toLowerCase();
        this.columns = value;
    }
    /**
     * @return Returns the pageCount.
     */
    public String getPageCount() {
        return pageCount;
    }
    /**
     * @param pageCount The pageCount to set.
     */
    public void setPageCount(String pageCount) {
        this.pageCount = pageCount;
        //System.out.println("---->pageCount="+pageCount);
        this.pagerows=Integer.parseInt(pageCount);
    }

    /**
     * @return Returns the indexes.
     */
    public String getIndexes() {
        return indexes;
    }
    /**
     * @param indexes The indexes to set.
     */
    public void setIndexes(String indexes) {
        this.indexes = indexes;
    }
    /**
     * @return Returns the id.
     */
    public String getId() {
        return id;
    }
    /**
     * @param id The id to set.
     */
    public void setId(String id) {
        this.id = id;
    }
    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }
    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }
    /* 
     * @see javax.servlet.jsp.tagext.IterationTag#doAfterBody()
     */
    public int doAfterBody() throws JspException {
        if (bodyContent != null)
        {
          TagUtils.getInstance().writePrevious(pageContext, bodyContent.getString());
          bodyContent.clearBody();
        }        
        if (iterator.hasNext())
        {
          Object element = iterator.next();
          if (element == null)
          {
            pageContext.removeAttribute(id);
          }
          else
          {
            pageContext.setAttribute(id, element);
            pageContext.setAttribute(indexes, new Integer(indexesCount));            
          }
          indexesCount++;          
          return 2;
        }
        else
        {
          return 0;
        } 
        
    }
    /* 
     * @see javax.servlet.jsp.tagext.Tag#doEndTag()
     */
    public int doEndTag() throws JspException {
        pageContext.setAttribute(page_id, pagination);        
        return 6;
    }
    
    /**
     * 从Form中找到对应的分页处理器
     * @return
     */
    private Pagination findPagination()
    {
        Pagination vo=null;
        Object formObject = pageContext.getSession().getAttribute(name);
        if(formObject==null)
            return null;
        vo=((FrameForm)formObject).getPagination();
        return vo;
    }
    /**
     * 设置表单对象分页
     * @param vo
     */
    private void setFormPagination(Pagination vo)
    {
        Object formObject = pageContext.getSession().getAttribute(name);
        if(formObject==null)
            return;  
        ((FrameForm)formObject).setPagination(vo); 
    }
    /**
     * 分解字段属性,for examples xxx,yyy,sss,eeee,
     * @return
     */
    private String[] splitColumns()
    {
        String[] fields=null;
        ArrayList list=new ArrayList();
        String temp=this.columns.toLowerCase();
        StringTokenizer st = new StringTokenizer(temp, ",");
        while (st.hasMoreTokens())
        {
            list.add(st.nextToken());
        }   

        fields=new String[list.size()];
        for(int i=0;i<list.size();i++)
            fields[i]=(String)list.get(i);
        return fields;
    }
    /**
     * 拆分主键列表
     * @return
     */
    private ArrayList splitKeys()
    {
    	if(keys==null|| "".equals(keys))
    		return null;
        ArrayList list=new ArrayList();
        String temp=this.keys.toLowerCase();
        StringTokenizer st = new StringTokenizer(temp, ",");
        while (st.hasMoreTokens())
        {
            list.add(st.nextToken());
        }   
        return list;
    }    
    /* 
     * @see javax.servlet.jsp.tagext.Tag#doStartTag()
     */
    public int doStartTag() throws JspException {
    	//long startTime = System.currentTimeMillis();
		//System.out.println("页面开始加载，当前时间：" + startTime);
        //pagination=(Pagination)pageContext.getAttribute("pagination");
        try
        {
            //long start = System.currentTimeMillis();
	        pagination=findPagination();
	        if(pagination==null)
	        {
	            pagination=new Pagination(0);
	            setFormPagination(pagination);				
	        }
	        PaginationManager paginationm =null;
	        paginationm=new PaginationManager(sql_str,table,where_str,order_by,splitColumns(),distinct);
	        paginationm.setFromdict(this.fromdict);
	        if("1".equalsIgnoreCase(this.allmemo))
	        	paginationm.setBAllMemo(true);
	        //System.out.println("pagerows="+this.pagerows);
	        paginationm.setPagerows(this.pagerows);
	        maxpages=paginationm.getMaxpages();
	        pagination.setPageCount(this.pagerows);
	        pagination.setCount(paginationm.getMaxrows());

	        //zxj 20160528 防止当前页超出最大页
			if(pagination.getCurrent() > maxpages)
				pagination.firstPage();			
	        
	        indexesCount=0;
	//        PaginationManager paginationm;
	//        paginationm=new PaginationManager(sql_str,table,where_str,order_by,null);
	//        paginationm.setPagerows(this.pagerows);
	//        maxpages=paginationm.getMaxpages(); 
	        paginationm.setKeylist(splitKeys());
	        List list=paginationm.getPage(pagination.getCurrent());
	        /**
	         * 把查询到的数据压进分页处理类中
	         */
	        pagination.setCurr_page_list((ArrayList)list);
	        //long endTime = System.currentTimeMillis();
			//System.out.println("页面加载完成，当前时间：" + startTime);
			
			//System.out.println((endTime-startTime)+"ms");
	        if(list==null||list.size()==0)
	        {
	            return 0;
	        }
	        iterator=list.iterator();
	        if(pagination!=null)
	            pageContext.setAttribute(curpage,pagination.getCurrent()+"");
	        
            
	        if(iterator.hasNext())
	        {
	            Object element = iterator.next();
	            if (element == null)
	            {
	              pageContext.removeAttribute(id);
	            }
	            else
	            {
	              pageContext.setAttribute(id, element);
	              pageContext.setAttribute(indexes, new Integer(indexesCount));
	            }            
	            ++indexesCount;
	            long end = System.currentTimeMillis();
	            //System.out.println(end-start);
	            //System.out.println("======>="+Integer.toString(indexesCount));
	            return EVAL_BODY_BUFFERED;
	        }
	        else
	            return 0;
	        
        }
        catch(Exception ee)
        {
            ee.printStackTrace();
            return 0;
        }
    }
    /**
     * @return Returns the order_by.
     */
    public String getOrder_by() {
        return order_by;
    }
    /**
     * @param order_by The order_by to set.
     */
    public void setOrder_by(String order_by) {
        String value=TagUtility.getClassValue(this.pageContext,order_by);  
        this.order_by = (value!=null)?value:order_by;
    }
    /**
     * @return Returns the sql_str.
     */
    public String getSql_str() {
        
        return sql_str;
    }
    /**
     * @param sql_str The sql_str to set.
     */
    public void setSql_str(String sql_str) {
        String value=TagUtility.getClassValue(this.pageContext,sql_str);  
        this.sql_str = (value!=null)?value:sql_str;
    }
    /**
     * @return Returns the table.
     */
    public String getTable() {
        return table;
    }
    /**
     * @param table The table to set.
     */
    public void setTable(String table) {
        String value=TagUtility.getClassValue(this.pageContext,table);  
        this.table = (value!=null)?value:table;
    }
    /**
     * @return Returns the where_str.
     */
    public String getWhere_str() {
        return where_str;
    }
    /**
     * @param where_str The where_str to set.
     */
    public void setWhere_str(String where_str) {
        String value=TagUtility.getClassValue(this.pageContext,where_str);
        this.where_str = (value!=null)?value:where_str;
    }
    /**
     * 
     */
    public PaginationDb() {
        indexes = "indexes";
    }
    /**
     * @return Returns the pagerows.
     */
    public int getPagerows() {
        return pagerows;
    }
    /**
     * @param pagerows The pagerows to set.
     */
    public void setPagerows(int pagerows) {
        this.pagerows = pagerows;
    }
    /**
     * @return Returns the maxpages.
     */
    public int getMaxpages() {
        return maxpages;
    }
    /**
     * @param maxpages The maxpages to set.
     */
    public void setMaxpages(int maxpages) {
        this.maxpages = maxpages;
    }
    /**
     * @return Returns the pagination.
     */
    public Pagination getPagination() {
        return pagination;
    }
    /**
     * @param pagination The pagination to set.
     */
    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }
	public String getDistinct() {
		return distinct;
	}
	public void setDistinct(String distinct) {
        String value=TagUtility.getClassValue(this.pageContext,distinct);
        //value=value.replace("*",".");
		this.distinct = (value!=null)?value:distinct;
	}
	public String getKeys() {
		return keys;
	}
	public void setKeys(String keys) {
		this.keys = keys;
	}
	public String getFromdict() {
		return fromdict;
	}
	public void setFromdict(String fromdict) {
		this.fromdict = fromdict;
	}
	public String getAllmemo() {
		return allmemo;
	}
	public void setAllmemo(String allmemo) {
		this.allmemo = allmemo;
	}
	public String getCurpage() {
		return curpage;
	}
	public void setCurpage(String curpage) {
		this.curpage = curpage;
	}
}
