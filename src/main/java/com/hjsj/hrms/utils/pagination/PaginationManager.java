package com.hjsj.hrms.utils.pagination;

import com.hjsj.hrms.utils.FormatValue;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
 
/**
 * <p>Title:管理数据分页显示</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:May 13, 2005:9:50:27 AM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class PaginationManager {

    /**
     * 表
     */
    private String table;
    /**
     * 大文本字段是否转html（默认为true） 
     * true 转码 | false 不转码
     */
    private boolean transCode;//解决大文本转html后乱码问题  2016-05-26 16:30:03
    /**
     * 条件字符串
     */
    private String where_str;
    /**
     * 查询串，for examples select * from xxxx;但不包括where条件
     */
    private String sql_str;
    /**
     * field
     */
    private String[] columns;
    /**
     *排序
     */
    private String order_by;
    /**求记录唯一性标识字段内容,可以是表达式*/
    private String distinct;
    /**备注字段是否全部取得*/
    private boolean bAllMemo=false;
    /**
     * 当前页号
     */
    private int current_page_num;
    /**
     * 每页行数
     */
    private int pagerows=20;
    /**
     * 最大页数
     */
    private int maxpages;
    /**
     * 总行数
     */
    private int maxrows=0;

    /**来源字典=0档案字典,=1业务字典,默认状态为整个扫描*/
    private String fromdict="0";
    
    private ArrayList keylist;
    
    public ArrayList getKeylist() {
		return keylist;
	}

	public void setKeylist(ArrayList keylist) {
		this.keylist = keylist;
	}

	/**
     * 数据初始化
     */
    private void initData()
    {
        //最大记录数
        maxrows=getRecordCount();
        maxpages=maxrows/pagerows;
        int vv=maxrows%pagerows;
        if(vv>0)
            ++maxpages; 
    }
    
    /**
     * 取得记录总数
     * @return
     */
    private int getRecordCount()
    {
        int icount=0;   
        Connection conn=null;
        ContentDAO dao=null;
        RowSet rset=null;        
        try
        {        
            StringBuffer strsql=new StringBuffer();
            if(table==null|| "".equals(table))
            {
              if(!(where_str==null|| "".equals(where_str)))
              {
            	if(distinct==null|| "".equals(distinct))
            	{
            		strsql.append("select count(*) as ncount ");
            		strsql.append(where_str); 
            	}
            	else
            	{
            		strsql.append("select count(distinct(");
            		strsql.append(distinct);
            		strsql.append(")) as ncount ");
            		strsql.append(where_str);             		
            	}
              }
              else
              {
          		strsql.append("select count(*) as ncount from (");
          		strsql.append(this.sql_str);
          		strsql.append(") myset "); 
              }
            }
            else
            {
            	if(distinct==null|| "".equals(distinct))
            	{
            		strsql.append("select count(*) as ncount from ");
            		strsql.append(table);//表名称
            		strsql.append(where_str);   
            	}
            	else
            	{
            		strsql.append("select count(distinct(");
            		strsql.append(distinct);
            		strsql.append(" )) as ncount from ");
            		strsql.append(table);//表名称
            		strsql.append(where_str);              		
            	}
            }
           //System.out.println("str-ssss="+strsql.toString());            
            conn=AdminDb.getConnection();
            dao=new ContentDAO(conn);   
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
	            //if(rset!=null)
	            //    rset.close();
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
    
    /**
     * 查询具体数据
     * @param start 起始记录数
     * @param pagerows 每页的行数
     * @return
     */
    private List getItems(int start,int pagerows)
    {
        Connection conn=null; 
        RowSet rset=null;
        Statement stmt = null;
		//ResultSet rset = null;
		ResultSetMetaData rsetmd=null;
        StringBuffer strsql=new StringBuffer();
        strsql.append(sql_str);
        strsql.append(" ");
        if(!(where_str==null|| "".equals(where_str)));
        	strsql.append(where_str);
        strsql.append(" ");
        if(!(order_by==null|| "".equals(order_by)));
            strsql.append(order_by);
//        System.out.println("SQL="+strsql.toString());
        int i=0;

        ArrayList selectlist=null;
        try
        {
            selectlist=new ArrayList();            
            conn=AdminDb.getConnection(); 

            ContentDAO dao=new ContentDAO(conn);
          //这里需要这样写的原因是有些业务模块使用分页便签时都自己在页面端缓存了sql语句（例如ajax使用），导致再次提交时走过滤器转为全角，
            //在这里和各位组长沟通后各业务模块不想改很多，就求全暂时会存在sql注入风险  xuj add 2013-11-18
            rset=dao.search(/*PubFunc.keyWord_reback(*/strsql.toString()/*)*/);
            //rset=dao.search(PubFunc.keyWord_reback(strsql.toString()), 21, pagerows);
            rsetmd=rset.getMetaData();
            if((!rset.absolute(start))||maxrows==0)//通过应用数据库进行定位查询,如果取得具体对象
            {
                return selectlist;
            }
            FormatValue formatvalue=new FormatValue();                    
           
            while(i<pagerows&&(start+i)<=maxrows)
            {
                /**不传表名的话，则用LazyDynaBean*/
                if(table==null|| "".equals(table))
                {
                    DynaBean dbean=new LazyDynaBean();
	                for(int j=1;j<=rsetmd.getColumnCount()/*columns.length*/;j++)
	                {   
	                	String fieldname=rsetmd.getColumnName(j).toUpperCase();
	                	if(!isVisible(fieldname))
	                		continue;
	                	FieldItem item=null;
	                	if("1".equals(this.fromdict)) //业务字典
	                		item=DataDictionary.getFieldItem(fieldname,1);
	                	else
	                		item=DataDictionary.getFieldItem(fieldname);
	                    String temp = getValueByFieldType(rset, rsetmd, j);
	                    item = checkFieldByType(rsetmd,j,item);
	                    if(item==null)
	                    {
	                    	if(rset.getObject(j)!=null)	                    	
	                           dbean.set(fieldname.toLowerCase(),temp);
	                    	else
	                    		dbean.set(fieldname.toLowerCase(),"");	
	                    }
	                    else
	                    {
	                    	temp = getValueByFieldType(rset, rsetmd, j, item);
	                    	if(rset.getObject(j)!=null)            //wlh更改姓名等属性为空值
	                           dbean.set(fieldname.toLowerCase(),formatvalue.format(item,temp));
	                    	else
	                    	   dbean.set(fieldname.toLowerCase(),"");
	                    }
	                }
	                selectlist.add(dbean);	                           
                }
                else
                {
	                RecordVo vo=new RecordVo(table);
	                for(int j=1;j<=rsetmd.getColumnCount()/*columns.length*/;j++)
	                {
	                	String fieldname=rsetmd.getColumnName(j).toUpperCase();
	                	if(!isVisible(fieldname))
	                		continue;
	                	FieldItem item=null;
	                	if("1".equals(this.fromdict))
	                		item=DataDictionary.getFieldItem(fieldname,1);
	                	else
	                		item=DataDictionary.getFieldItem(fieldname);

	                    String temp = getValueByFieldType(rset, rsetmd, j);
	                    item = checkFieldByType(rsetmd,j,item);
	                    vo.setString(fieldname.toLowerCase(),formatvalue.format(item,temp));
	                }
	                selectlist.add(vo);	                
                }
                rset.next();
                ++i;
            }
        }
        catch(Exception sqle)
        {
            sqle.printStackTrace();
        }        
        finally
        {
        	try
        	{
	        	if(rset!=null)
	        		rset.close();
	        	if(stmt!=null)
	        		stmt.close();
	        	if(conn!=null&&(!conn.isClosed()))
	        		conn.close();
        	}
        	catch(SQLException se)
        	{
        		se.printStackTrace();
        	}
        }
        return selectlist;     
    }
    
    /**
     * 查询具体数据
     * @param pageNum 当前页数
     * @param pagerows 每页的行数
     * @return
     */
    private List getItemsBypage(int pagerows,int pageNum)
    {
        Connection conn=null; 
        RowSet rset=null;
        Statement stmt = null;
		//ResultSet rset = null;
		ResultSetMetaData rsetmd=null;
        StringBuffer strsql=new StringBuffer();
        strsql.append(sql_str);
        strsql.append(" ");
        if(!(where_str==null|| "".equals(where_str)));
        	strsql.append(where_str);
        strsql.append(" ");
        if(!(order_by==null|| "".equals(order_by)));
            strsql.append(order_by);
//        System.out.println("SQL="+strsql.toString());
        int i=0;

        ArrayList selectlist=null;
        try
        {
            selectlist=new ArrayList();   
            if(maxrows==0)
            {
                return selectlist;
            }
            conn=AdminDb.getConnection(); 

            ContentDAO dao=new ContentDAO(conn);
          //这里需要这样写的原因是有些业务模块使用分页便签时都自己在页面端缓存了sql语句（例如ajax使用），导致再次提交时走过滤器转为全角，
            //在这里和各位组长沟通后各业务模块不想改很多，就求全暂时会存在sql注入风险  xuj add 2013-11-18
            rset=dao.search(/*PubFunc.keyWord_reback(*/strsql.toString()/*)*/, pagerows, pageNum);
            rsetmd=rset.getMetaData();
           
            FormatValue formatvalue=new FormatValue();                    
           
            while(rset.next())
            {
                /**不传表名的话，则用LazyDynaBean*/
                if(table==null|| "".equals(table))
                {
                    DynaBean dbean=new LazyDynaBean();
	                for(int j=1;j<=rsetmd.getColumnCount()/*columns.length*/;j++)
	                {   
	                	String fieldname=rsetmd.getColumnName(j).toUpperCase();
	                	if(!isVisible(fieldname))
	                		continue;
	                	FieldItem item=null;
	                	if("1".equals(this.fromdict)) //业务字典
	                		item=DataDictionary.getFieldItem(fieldname,1);
	                	else
	                		item=DataDictionary.getFieldItem(fieldname);
	                	
	                    String temp = getValueByFieldType(rset, rsetmd, j);
	                    item = checkFieldByType(rsetmd,j,item);
	                    if(item==null)
	                    {
	                    	if(rset.getObject(j)!=null)	                    	
	                           dbean.set(fieldname.toLowerCase(),temp);
	                    	else
	                    		dbean.set(fieldname.toLowerCase(),"");	
	                    }
	                    else
	                    {
	                    	temp = getValueByFieldType(rset, rsetmd, j, item);
	                    	if(rset.getObject(j)!=null)            //wlh更改姓名等属性为空值
	                           dbean.set(fieldname.toLowerCase(),formatvalue.format(item,temp));
	                    	else
	                    	   dbean.set(fieldname.toLowerCase(),"");
	                    }
	                }
	                selectlist.add(dbean);	                           
                }
                else
                {
	                RecordVo vo=new RecordVo(table);
	                for(int j=1;j<=rsetmd.getColumnCount()/*columns.length*/;j++)
	                {
	                	String fieldname=rsetmd.getColumnName(j).toUpperCase();
	                	if(!isVisible(fieldname))
	                		continue;
	                	FieldItem item=null;
	                	if("1".equals(this.fromdict))
	                		item=DataDictionary.getFieldItem(fieldname,1);
	                	else
	                		item=DataDictionary.getFieldItem(fieldname);

	                    String temp = getValueByFieldType(rset, rsetmd, j);
	                    item = checkFieldByType(rsetmd,j,item);
	                    vo.setString(fieldname.toLowerCase(),formatvalue.format(item,temp));
	                }
	                selectlist.add(vo);	                
                }
            }
        }
        catch(Exception sqle)
        {
            sqle.printStackTrace();
        }        
        finally
        {
        	try
        	{
	        	if(rset!=null)
	        		rset.close();
	        	if(stmt!=null)
	        		stmt.close();
	        	if(conn!=null&&(!conn.isClosed()))
	        		conn.close();
        	}
        	catch(SQLException se)
        	{
        		se.printStackTrace();
        	}
        }
        return selectlist;     
    }
    
    /**
     * 
     * @param currpage 取得页数
     * @param pagerows 每页记录行数
     * @param list 关键字段列表
     * @return
     */
    private List getItems(int currpage,int pagerows,List list)
    {
        Connection conn=null; 
        RowSet rset=null;
        Statement stmt = null;
		//ResultSet rset = null;
		ResultSetMetaData rsetmd=null;
        StringBuffer strsql=new StringBuffer();
        strsql.append(sql_str);
        strsql.append(" ");
        if(!(where_str==null|| "".equals(where_str)));
        	strsql.append(where_str);
        strsql.append(" ");
        if(!(order_by==null|| "".equals(order_by)));
            strsql.append(order_by);
        //System.out.println("SQL="+strsql.toString());
        ArrayList selectlist=null;
        try
        {
            selectlist=new ArrayList();            
            conn=AdminDb.getConnection(); 
            ContentDAO dao=new ContentDAO(conn);
//            ArrayList keys=new ArrayList();
//            keys.add("A0100");0
            //这里需要这样写的原因是有些业务模块使用分页便签时都自己在页面端缓存了sql语句（例如ajax使用），导致再次提交时走过滤器转为全角，
            //在这里和各位组长沟通后各业务模块不想改很多，就求全暂时会存在sql注入风险  xuj add 2013-11-18
            String sql=/*PubFunc.keyWord_reback(*/strsql.toString()/*)*/;
            rset=dao.search(sql,pagerows,currpage,list);
            //rset=dao.search(strsql.toString());
            rsetmd=rset.getMetaData();
            FormatValue formatvalue=new FormatValue();                    
            while(rset.next())
            {  
                /**不传表名的话，则用LazyDynaBean*/
                if(table==null|| "".equals(table))
                {
                    DynaBean dbean=new LazyDynaBean();
	                for(int j=1;j<=rsetmd.getColumnCount()/*columns.length*/;j++)
	                {   
	                	String fieldname=rsetmd.getColumnName(j).toUpperCase();
	                	if(!isVisible(fieldname))
	                		continue;
	                	FieldItem item=null;
	                	if("1".equals(this.fromdict))
	                		item=DataDictionary.getFieldItem(fieldname,1);
	                	else
	                		item=DataDictionary.getFieldItem(fieldname);
	                    String temp = getValueByFieldType(rset, rsetmd, j);
	                    item = checkFieldByType(rsetmd,j,item);
	                    if(item==null)
	                    {
	                    	if(rset.getObject(j)!=null)
	                           dbean.set(fieldname.toLowerCase(),temp);
	                    	else
		                       dbean.set(fieldname.toLowerCase(),"");	                    		
	                    }
	                    else
	                    {
	                    	temp = getValueByFieldType(rset, rsetmd, j, item);
	                    	if(rset.getObject(j)!=null)            //wlh更改姓名等属性为空值
	                           dbean.set(fieldname.toLowerCase(),formatvalue.format(item,temp));
	                    	else
	                    	   dbean.set(fieldname.toLowerCase(),"");
	                    }
	                }
	                selectlist.add(dbean);	                           
                }
                else
                {
	                RecordVo vo=new RecordVo(table);
	                for(int j=1;j<=rsetmd.getColumnCount()/*columns.length*/;j++)
	                {
	                	String fieldname=rsetmd.getColumnName(j).toUpperCase();
	                	if(!isVisible(fieldname))
	                		continue;	      
	                	FieldItem item=null;
	                	if("1".equals(this.fromdict))
	                		item=DataDictionary.getFieldItem(fieldname,1);
	                	else
	                		item=DataDictionary.getFieldItem(fieldname);
	                   
	                    String temp = getValueByFieldType(rset, rsetmd, j);
	                    item = checkFieldByType(rsetmd,j,item);
	                    vo.setString(fieldname.toLowerCase(),formatvalue.format(item,temp));
	                }
	                selectlist.add(vo);	                
                }
            }
        }
        catch(Exception sqle)
        {
            sqle.printStackTrace();
        }        
        finally
        {
        	try
        	{
	        	if(rset!=null)
	        		rset.close();
	        	if(stmt!=null)
	        		stmt.close();
	        	if(conn!=null&&(!conn.isClosed()))
	        		conn.close();
        	}
        	catch(SQLException se)
        	{
        		se.printStackTrace();
        	}
        }
        return selectlist;     
    }
    
    /**
     * 是否需要显示
     * @param fieldname
     * @return
     */
    private boolean isVisible(String fieldname)
    {
    	boolean bflag=false;
    	for(int i=0;i<columns.length;i++)
    	{
    		if(columns[i].equalsIgnoreCase(fieldname))
    		{
    			bflag=true;
    			break;
    		}
    	}
    	return bflag;
    }
	/**
	 * @param rset
	 * @param rsetmd
	 * @param j
	 * @return
	 * @throws SQLException
	 */
	public String getValueByFieldType(ResultSet rset, ResultSetMetaData rsetmd, int j) throws SQLException {
		String temp=null;
		switch(rsetmd.getColumnType(j))
		{
		
		case Types.DATE:
		        temp=PubFunc.FormatDate(rset.getDate(j));
		        break;			
		case Types.TIMESTAMP:
			    temp=PubFunc.FormatDate(rset.getTimestamp(j),"yyyy-MM-dd HH:mm:ss");			    
			    /*if(temp.indexOf("12:00:00")!=-1)
			        temp=PubFunc.FormatDate(rset.getDate(j));*/
				break;
		case Types.CLOB:
		case -1://text mssql chenmengqing added 20071007
			    temp=Sql_switcher.readMemo(rset,rsetmd.getColumnName(j));
			   
			    if(this.getTransCode()) //判断是否需要转html added by liubq 2016-05-26 16:28:59
			    	temp=PubFunc.toHtml(temp);
			    if(isBAllMemo())
			    {
			    	//temp=PubFunc.toHtml(temp);
			    }
			    else
			    {
			    	if(temp.length()>30) //chenmengqing added 20071007
			    		temp=temp.substring(0, 30)+"...";
			    	
			    	temp = PubFunc.substr(temp);//chenxg add 过滤备注类型指标中空格（&nbsp;）、换行（<br>）截断时，显示的源码  2015-04-18
			    }
				break;
		case Types.BLOB:
				temp="二进制文件";	                    	
				break;		
		case Types.NUMERIC:
			  int preci=rsetmd.getScale(j);
			  temp=String.valueOf(rset.getDouble(j));			  
			  temp=PubFunc.DoFormatDecimal(temp, preci);
			  break;

		default:		
				temp=rset.getString(j);
				break;
		}
		return temp;
	}
	
	public String getValueByFieldType(ResultSet rset, ResultSetMetaData rsetmd, int j,FieldItem item) throws SQLException {
		String temp=null;
		int type = rsetmd.getColumnType(j);
		//是oracle库，是18位的时间类型时，date转成timestamp，否则用rset.getDate(j)取不到时分秒
		if (Types.DATE==type && "D".equalsIgnoreCase(item.getItemtype()) && item.getItemlength() == 18 && Sql_switcher.searchDbServer() == 2) {
			type = Types.TIMESTAMP;
		}
		switch(type)
		{
		
		case Types.DATE:
		        temp=PubFunc.FormatDate(rset.getDate(j));
		        break;			
		case Types.TIMESTAMP:
				String format = "yyyy-MM-dd HH:mm:ss";
				if("D".equalsIgnoreCase(item.getItemtype())&& item.getItemlength() == 4){
					format="yyyy";
				}else if("D".equalsIgnoreCase(item.getItemtype())&& item.getItemlength() == 7){
					format="yyyy-MM";
				}else if("D".equalsIgnoreCase(item.getItemtype())&& item.getItemlength() == 10){
					format="yyyy-MM-dd";
				}else if("D".equalsIgnoreCase(item.getItemtype())&& item.getItemlength() == 15){
					format="yyyy-MM-dd HH:mm";
				}else if("D".equalsIgnoreCase(item.getItemtype())&& item.getItemlength() == 18){
					format="yyyy-MM-dd HH:mm:ss";
				}
			    temp=PubFunc.FormatDate(rset.getTimestamp(j),format);			    
			    /*if(temp.indexOf("12:00:00")!=-1)
			        temp=PubFunc.FormatDate(rset.getDate(j));*/
				break;
		case Types.CLOB:
		case -1://text mssql chenmengqing added 20071007
			    temp=Sql_switcher.readMemo(rset,rsetmd.getColumnName(j));
			    
			    if(this.getTransCode()) //判断是否需要转html  added by liubq 2016-05-26 16:29:24
			    	temp=PubFunc.toHtml(temp);
			    if(isBAllMemo())	
			    	
			    {
			    	//temp=PubFunc.toHtml(temp);	/*xpy  历史时点备注型指标显示不正确*/
			    }
			    else
			    {
			    	if(temp.length()>30) //chenmengqing added 20071007
			    		temp=temp.substring(0, 30)+"...";
			    	
			    	temp = PubFunc.substr(temp);//chenxg add 过滤备注类型指标中空格（&nbsp;）、换行（<br>）截断时，显示的源码  2015-04-18
			    }
				break;
		case Types.BLOB:
				temp="二进制文件";	                    	
				break;		
		case Types.NUMERIC:
			  int preci=rsetmd.getScale(j);
			  
			  //zxj add 20130708 oracle库驱动问题，计算列取不到精度，取业务字典定义的精度
			  if (Sql_switcher.searchDbServer() == Constant.ORACEL && (-127 == preci || 0 == preci) && item != null) 
			      preci = item.getDecimalwidth();
			  
			  temp=String.valueOf(rset.getDouble(j));
			  temp=PubFunc.DoFormatDecimal(temp, preci);
			  break;

		default:		
				temp=rset.getString(j);
				break;
		}
		return temp;
	}
	
    /**
     * 数据查询接口
     */
    public PaginationManager(String sql_str,String table,String where_str,String order_by,String[] columns,String distinct) {
        this.sql_str=sql_str;
        this.table=table;
        this.where_str=where_str;
        this.columns=columns;
        this.order_by=order_by;
        this.distinct=distinct;
        initData();
    }
    
    
    /*检查从数据字典中获取的指标FieldItem的类型是否和元数据ResultSetMetaData字段类型一致，不一致说明数据字典中的指标信息不是此字段的，解决
     同名字段在数据字典中获取FieldItem后续代码进行类型转换时错误问题  xuj add 2014-10-15
     * */
    public FieldItem checkFieldByType( ResultSetMetaData rsetmd, int j,FieldItem item) throws SQLException {
    	if(item==null)
    		return item;
		switch(rsetmd.getColumnType(j))
		{
		
		case Types.DATE:
		        if(!"D".equals(item.getItemtype()))
		        	item = null;
		        break;			
		case Types.TIMESTAMP:
			if(!"D".equals(item.getItemtype()))
	        	item = null;
				break;
		case Types.CLOB:
		case -1://text mssql chenmengqing added 20071007
			    if(!"M".equals(item.getItemtype()))
			    	item = null;
				break;
		case Types.BLOB:
				                    	
				break;		
		case Types.NUMERIC:
			if(!"N".equals(item.getItemtype()))
	        	item = null;
			  break;

		default:		
			if(!"A".equals(item.getItemtype()))
	        	item = null;
				break;
		}
		return item;
	}
    
    /**
     * 取得第几页的数据
     * @param pageNum
     * @return
     */
    public List getPage(int pageNum)
    {
        if (pageNum < 1) {
            return new ArrayList(0);
        }
        // 计算该页第一条记录的序号
        if(this.getKeylist()==null||this.getKeylist().size()==0)
        {
        	//xuj update 2014-8-11  优化查询采用分页sql查询，要多少条查询出多少条，先前查询方式是全部查询出来 ，会慢很多
        	//int start = pagerows * (pageNum - 1)+1 ;
        	//return getItems(start, pagerows);
        	return getItemsBypage( pagerows,pageNum);
        }
        else
        {
        	return getItems(pageNum, pagerows,this.keylist);        	
        }
    }
    /**
     * @return Returns the columns.
     */
    public String[] getColumns() {
        return columns;
    }
    /**
     * @param columns The columns to set.
     */
    public void setColumns(String[] columns) {
        this.columns = columns;
    }
    /**
     * @return Returns the current_page_num.
     */
    public int getCurrent_page_num() {
        return current_page_num;
    }
    /**
     * @param current_page_num The current_page_num to set.
     */
    public void setCurrent_page_num(int current_page_num) {
        this.current_page_num = current_page_num;
    }
    /**
     * @return Returns the maxpages.
     */
    public int getMaxpages() {
    	//zxj 20160528 因为会修改每页条数等操作，所以应动态计算最大页
        this.maxpages = this.maxrows/pagerows;
        int vv = this.maxrows % pagerows;
        if(vv>0)
            ++maxpages; 
        
        return maxpages;
    }
    /**
     * @param maxpages The maxpages to set.
     */
    public void setMaxpages(int maxpages) {
        this.maxpages = maxpages;
    }
    /**
     * @return Returns the maxrows.
     */
    public int getMaxrows() {
        return maxrows;
    }
    /**
     * @param maxrows The maxrows to set.
     */
    public void setMaxrows(int maxrows) {
        this.maxrows = maxrows;
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
        this.order_by = order_by;
    }
    /**
     * @return Returns the pagerows.
     */
    public int getPagerows() {
        return pagerows;
    }
    /**
     * 每页最大行数仅能为5000
     * @param pagerows The pagerows to set.
     */
    public void setPagerows(int pagerows) {
        if((pagerows<1)||(pagerows>5000))
            return;
        this.pagerows = pagerows;
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
        this.table = table;
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
        this.where_str = where_str;
    }

	public String getDistinct() {
		return distinct;
	}

	public void setDistinct(String distinct) {
		this.distinct = distinct;
	}

	public void setFromdict(String fromdict) {
		this.fromdict = fromdict;
	}

	public void setBAllMemo(boolean allMemo) {
		bAllMemo = allMemo;
	}

	public boolean isBAllMemo() {
		return bAllMemo;
	}

	public boolean getTransCode() {
		return transCode;
	}

	public void setTransCode(boolean transCode) {
		this.transCode = transCode;
	}
}
