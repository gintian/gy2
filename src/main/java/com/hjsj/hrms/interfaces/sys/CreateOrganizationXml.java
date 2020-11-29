package com.hjsj.hrms.interfaces.sys;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.register.history.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.analyse.YearMonthCount;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminDb;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
/**
 * <p>Title:在web前台界面生成组织机构树形</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:May 12, 2005:1:54:19 PM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class CreateOrganizationXml 
{
    /**
     * 参数串
     */
    private String params;
    /**
     * 执行jsp文件
     */
    private String action;
    /**
     * 目标窗口
     */
    private String target;
    /**人员还是对组织*/
    private String flag;
    /**加载应用库标识
     * =0权限范围内的库
     * =1权限范围内的登录库
     * =2招聘
     * =-2培训
     * =3考勤
     * */
    private String dbtype;
    /**权限过滤标识
     * =0， 不进行权限过滤
     * =1  进行权限过滤
     * */
    private String priv="1";
    /**加载选项
     * =0（单位|部门|职位）
     * =1 (单位|部门)
     * =2 (单位)
     * */
    private String loadtype="0";
    /**是否加其他过滤条件*/
    private String isfilter;
    private String filterfactor=null;
    /**首次加载,控制首次不加人员*/
    private boolean bfirst=false;
    /**是否加载虚拟组织,默认为不加载*/
    private boolean bloadvorg=false;
    /**是否为虚拟机构节点*/
    private boolean borg=false;
    /** 是否显示人员库 */
    private String showDbName="0";
    
    /** 只列本人所在单位节点  0:不显示  1：显示  author:dc*/
    private String showSelfNode="0";
    
    private String chitemid=""; /**中国联通推荐表专用*/
    private String orgcode="";/**中国联通推荐表专用*/

    private String  viewunit_self="0";
    
    private boolean isPost=true;// 考勤组织机构树是否显示岗位
    private String privtype="";// 考勤组织机构树privtype=kq
    
    private String midvari_sql ="";//传设置算法分析器设置临时变量语句
    private String model_string="";//传不同的模块
    private String isAddAction="true";
    /**是否只显示自己所在部门内人员*/
	String isShowSelfDepts="0";
	
	private String dbvalue = "";   //领导班子带有人员库机构树所加

	public String getIsShowSelfDepts() {
		return isShowSelfDepts;
	}

	public void setIsShowSelfDepts(String isShowSelfDepts) {
		this.isShowSelfDepts = isShowSelfDepts;
	}

	public String getIsAddAction() {
		return isAddAction;
	}

	public void setIsAddAction(String isAddAction) {
		this.isAddAction = isAddAction;
	}

	public boolean isBfirst() {
		return bfirst;
	}

	public void setBfirst(boolean bfirst) {
		this.bfirst = bfirst;
	}

	 private static HashMap urlmap = new HashMap();
		
	/**
     * 构造函数
     * @param params
     * @param action
     * @param target
     * @param flag
     */
    public CreateOrganizationXml(String params,String action,String target,String flag,String dbtype) {
    	params = PubFunc.keyWord_reback(params);
        this.params=params;
        this.target=target;
        this.action=action;
        this.flag=flag;
        this.dbtype=dbtype;
    }
    
    public CreateOrganizationXml(String params,String action,String target,String flag,String dbtype,String priv) {
    	this(params,action,target,flag,dbtype);
    	this.priv=priv;
    }
    
    public CreateOrganizationXml(String params,String action,String target,String flag) {
    	params = PubFunc.keyWord_reback(params);
        this.params=params;
        this.target=target;
        this.action=action;
        this.flag=flag;
        this.dbtype="0";
    } 
    
    public CreateOrganizationXml(String params,String action,String target,String flag,String dbtype,String priv,String isfilter,String filterfactor) {
    	this(params,action,target,flag,dbtype);
    	this.priv=priv;
    	this.isfilter=isfilter;
    	this.filterfactor=filterfactor;
    }
    public CreateOrganizationXml(String params,String action,String target,String flag,String dbtype,String priv,String isfilter,String filterfactor,boolean isPost) {
    	this(params,action,target,flag,dbtype);
    	this.priv=priv;
    	this.isfilter=isfilter;
    	this.filterfactor=filterfactor;
    	this.isPost=isPost;
    }
    public CreateOrganizationXml(String params,String action,String target,String flag,String dbtype,String priv,String isfilter,String filterfactor,boolean isPost,String midvari_sql) {
    	this(params,action,target,flag,dbtype);
    	this.priv=priv;
    	this.isfilter=isfilter;
    	this.filterfactor=filterfactor;
    	this.isPost=isPost;
    	this.midvari_sql=midvari_sql; 
    }
    public CreateOrganizationXml(String params,String action,String target,String flag,String dbtype,String priv,String isfilter,String filterfactor,boolean isPost,String midvari_sql,String model_string) {
    	this(params,action,target,flag,dbtype);
    	this.priv=priv;
    	this.isfilter=isfilter;
    	this.filterfactor=filterfactor;
    	this.isPost=isPost;
    	this.midvari_sql=midvari_sql; 
    	this.model_string = model_string;
    }
    /**
     * 分析是否有子节点
     * @param codeitemid
     * @param conn
     * @return
     */
    private boolean HaveChild(String codeitemid,Connection conn)
    {
        StringBuffer strsql=new StringBuffer();
        boolean bhave=false;
        strsql.append("select count(*) as num from organization where parentid='");
        strsql.append(codeitemid);
        strsql.append("'");
        ResultSet rset=null;
        int ncount=0;
        ContentDAO dao=new ContentDAO(conn);
        try
        {
             rset=dao.search(strsql.toString());
             ncount=rset.getInt("num");
             //System.out.println("------->record count="+ncount);
             if(ncount<=0)
                 bhave= false;
             else
                 bhave= true;
        }
        catch(SQLException sqle)
        {
            sqle.printStackTrace();
        }
        finally
        {
            try
            {
                if(rset!=null)
                    rset.close();
            }
            catch(SQLException sql)
            {
                sql.printStackTrace();
            }
        }

        return bhave;
    }

	private String getSelectString(String dbpre)
    {
        	StringBuffer strsql=new StringBuffer();
	        strsql.append("select distinct a0000,");
	        strsql.append(dbpre);
	        strsql.append("a01.a0100 ,'");
	        strsql.append(dbpre);
	        strsql.append("' as dbase,");
	        strsql.append(dbpre);        
	        strsql.append("a01.b0110 b0110,e0122,");
	        strsql.append(dbpre);
	        strsql.append("a01.e01a1 e01a1,a0101 ");           	
	        strsql.append(" ");
	        return strsql.toString();
    }	
    
    private void getEmploys(UserView userview,String parentid,Element root,Connection conn,String dbpre,String nmodule)
    {
      String strsql=getPrivSql(userview, parentid,conn,dbpre,nmodule);
      if("".equals(strsql))
    	  return;
      //System.out.println(strsql);
      String theaction=null;
      ContentDAO dao=new ContentDAO(conn);
      RowSet rset=null;
      try
      {
    	  rset=dao.search(strsql);
    	  while(rset.next())
    	  {
    		  String nbase=rset.getString("dbase");
    		  String a0100=rset.getString("a0100");
    		  String a0101=rset.getString("a0101");
              Element child = new Element("TreeNode");
              child.setAttribute("id", nbase+a0100);
              if(a0101==null)
            	  a0101="";
              if(!(this.action==null|| "".equals(this.action)))
              {
  		        if(this.action.indexOf('?')==0)
  		        	theaction=this.action+"?a_code="+nbase+rset.getString("a0100");
  		        else
  		        	theaction=this.action+"&a_code="+nbase+rset.getString("a0100");
              }
            //将url链接参数加密为一个参数encryptParam  xuj add 2014-9-2  start
              if(theaction!=null){
	  			int index = theaction.indexOf("&");
	  			if(index>-1){
	  				String allurl = theaction.substring(0,index);
	  				String allparam = theaction.substring(index);
	  				theaction=allurl+"&encryptParam="+PubFunc.encrypt(allparam);
	  			}
              }
  			//将url链接参数加密为一个参数encryParam  xuj add 2014-9-2  end
              if(theaction==null|| "".equals(theaction)|| "false".equalsIgnoreCase(this.isAddAction))
              	child.setAttribute("href", "javascript:void(0)");            	
              else
              	child.setAttribute("href", theaction);
              child.setAttribute("text", a0101);
              child.setAttribute("title", a0101);
              child.setAttribute("target", this.target);
              //child.setAttribute("xml", "javascript:void(0)");
              child.setAttribute("icon","/images/man.gif");
              root.addContent(child);
    	  }
      }
      catch(Exception ex)
      {
    	  ex.printStackTrace();
      }
    }
    /**
     * 田野添加 2013-02-27
     * 修改 childsetId的内容形式：01010101`Usr`董事长`p
     * @param userview
     * @param parentid
     * @param root
     * @param conn
     * @param dbpre
     * @param nmodule
     */
    private void getEmploysApproval(UserView userview,String parentid,Element root,Connection conn,String dbpre,String nmodule)
    {
      String strsql=getPrivSql(userview, parentid,conn,dbpre,nmodule);
      if("".equals(strsql))
    	  return;
      //System.out.println(strsql);
      String theaction=null;
      ContentDAO dao=new ContentDAO(conn);
      RowSet rset=null;
      try
      {
    	  rset=dao.search(strsql);
    	  while(rset.next())
    	  {
    		  String nbase=rset.getString("dbase");
    		  String a0100=rset.getString("a0100");
    		  String a0101=rset.getString("a0101");
              Element child = new Element("TreeNode");
              child.setAttribute("id", a0100+"`"+nbase+"`"+a0101+"`p");
              if(a0101==null)
            	  a0101="";
              if(!(this.action==null|| "".equals(this.action)))
              {
  		        if(this.action.indexOf('?')==0)
  		        	theaction=this.action+"?a_code="+nbase+rset.getString("a0100");
  		        else
  		        	theaction=this.action+"&a_code="+nbase+rset.getString("a0100");
              }
              
            //将url链接参数加密为一个参数encryptParam  xuj add 2014-9-2  start
              if(theaction!=null){
    			int index = theaction.indexOf("&");
    			if(index>-1){
    				String allurl = theaction.substring(0,index);
    				String allparam = theaction.substring(index);
    				theaction=allurl+"&encryptParam="+PubFunc.encrypt(allparam);
    			}
              }
    			//将url链接参数加密为一个参数encryParam  xuj add 2014-9-2  end
    			
              if(theaction==null|| "".equals(theaction)|| "false".equalsIgnoreCase(this.isAddAction))
              	child.setAttribute("href", "javascript:void(0)");            	
              else
              	child.setAttribute("href", theaction);
              child.setAttribute("text", a0101);
              child.setAttribute("title", a0101);
              child.setAttribute("type", "true");
              child.setAttribute("target", this.target);
              //child.setAttribute("xml", "javascript:void(0)");
              child.setAttribute("icon","/images/man.gif");
              root.addContent(child);
    	  }
      }
      catch(Exception ex)
      {
    	  ex.printStackTrace();
      }
    } 
    
    
    /**
     * 取得人员库列表
     * @param userview
     * @param conn
     * @return
     */
    private ArrayList getFilterDbList(UserView userview,Connection conn)
    {
    	ArrayList list=new ArrayList();
    	try
    	{
    		if(dbvalue!=null && !"".equals(dbvalue)){   //wangcq 2014-12-15 领导班子获取相应人员库
				String[] dbs = dbvalue.split(",");
				for(int i=0; i<dbs.length; i++){
					list.add(dbs[i]);
				}
				return list;
			}else{
    			/**招聘中的报批人员做特殊处理，出招聘库外的所有登录用户库，*/
        		if("2".equals(this.dbtype))
        		{
        			DbNameBo dbbo=new DbNameBo(conn);				
        			ArrayList logdblist=dbbo.getAllLoginDbNameList();
        			RecordVo vo=ConstantParamter.getConstantVo("ZP_DBNAME");
        			String dbname="";
        			if(vo!=null)
        				dbname=vo.getString("str_value");
        			else 
        				return list;
        			for(int i=0;i<logdblist.size();i++)
        			{
        				RecordVo avo=(RecordVo)logdblist.get(i);
        				/*strlog.append(vo.getString("pre"));
        				strlog.append(",");*/
        				if(avo.getString("pre").equalsIgnoreCase(dbname))
        		    		continue;
        				list.add(avo.getString("pre"));
        			}
        			return list;
        			
        		}
        		/**考勤模块涉及的人员库*/
        		if("3".equals(this.dbtype))
        		{ 
        			KqUtilsClass kqUtilsClass = new KqUtilsClass(conn,userview); 
        			return kqUtilsClass.setKqPerList(null,"2");
        			
        		}
        		//培训考试计划手工选人特殊处理  取参培参数设置的交集
        		if("-2".equals(this.dbtype)){
        			 ArrayList sel_nbase = new ArrayList();
        			 ConstantXml constantbo = new ConstantXml(conn,"TR_PARAM");
        			 String tmpnbase = constantbo.getTextValue("/param/post_traincourse/nbase");
        			 if(tmpnbase!=null&&tmpnbase.length()>0){
        				 String nbs[]=tmpnbase.split(",");
        				 for(int i=0;i<nbs.length;i++){
        					 if(nbs[i]!=null&&nbs[i].length()>0){
        						 sel_nbase.add(nbs[i]);
        					 }
        				 }
        			 }
        			 ArrayList arrayList = new ArrayList();
        			 ArrayList dblist=userview.getPrivDbList();		
        			 for (int i = 0; i < dblist.size(); i++) {
    					if(sel_nbase.contains(dblist.get(i)))
    						arrayList.add(dblist.get(i));
    				}
        			return arrayList;
        		}
        		ArrayList dblist=userview.getPrivDbList();		
        		if(this.chitemid==null||this.chitemid.trim().length()<1){
        			if("0".equals(this.dbtype))
        				return dblist;	
        		}else{
        			dblist.add("Usr");
        		}
    			DbNameBo dbbo=new DbNameBo(conn);				
    			ArrayList logdblist=dbbo.getAllLoginDbNameList();
    			StringBuffer strlog=new StringBuffer();
    			for(int i=0;i<logdblist.size();i++)
    			{
    				RecordVo vo=(RecordVo)logdblist.get(i);
    				strlog.append(vo.getString("pre"));
    				strlog.append(",");
    			}
    			String str_db=strlog.toString().toUpperCase();
    			for(int j=0;j<dblist.size();j++)
    			{
    				String dbpre=(String)dblist.get(j);
    				if(str_db.indexOf(dbpre.toUpperCase())==-1)
    					continue;
    				list.add(dbpre);
    			}
    		}
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    	}
    	return list;
    }
    
    /**
     * 取得人员权限过滤条件
     * @param userview
     * @param parentid
     * @param conn
     * @param dbpre
     * @return
     */
	private String getPrivSql(UserView userview, String parentid,Connection conn,String dbpre,String nmodule) {
		StringBuffer strSql=new StringBuffer();	

		try
		  {
			/**权限因子*/
			String codeid=parentid.substring(0,2);
			String codevalue=parentid.substring(2);
			StringBuffer expr=new StringBuffer();

			if("UN".equalsIgnoreCase(codeid))
			{
				if("1".equals(this.loadtype))
				{
					expr.append("1*2|");				
					expr.append("E0122=`B0110=");
				}else{
					//xuj update 20141102 有岗位直接挂在单位下的组织架构，顾人员机构信息可能为单位、岗位,部门信息为空  如汉口银行组织架构
					expr.append("1*2*3|");				
					expr.append("E0122=`E01A1=`B0110=");
				}
			}
			else if("UM".equalsIgnoreCase(codeid))
			{
				
				if("1".equals(this.loadtype))
				{
					expr.append("1*2|");	
				    expr.append("B0110<>`E0122=");
				}else{
					expr.append("1*2*3|");	
					expr.append("E01A1=`B0110<>`E0122=");
				}
				 
			}
			else
			{
				//expr.append("1*2*3|");				
				//expr.append("B0110<>`E0122<>`E01A1=");
				//xuj update 20141102 有岗位直接挂在单位下的组织架构，顾人员机构信息可能为单位、岗位,部门信息为空  如汉口银行组织架构
				expr.append("1*2|");	
				expr.append("B0110<>`E01A1=");
			}
			if(codevalue.length()==0)
				expr.append("`");	
			else
				expr.append(codevalue+"`");
			StringBuffer mudelBuf = new StringBuffer("");
			boolean isByModule= false;
			boolean isall=false;
			if(nmodule!=null&&!"".equals(nmodule))
			{
				isByModule=true;
				String str="UN";
				if(!userview.isSuper_admin())
					str=userview.getUnitIdByBusi(nmodule);
				
				if(str==null|| "".equals(str.trim())){
					mudelBuf.append(" or 1=2 ");
				}else{
					String[] tmp = str.split("`");
					for(int i=0;i<tmp.length;i++)
					{
						String cv=tmp[i];
						String c=cv.substring(0,2);
						String v = cv.substring(2);
						if("UN".equalsIgnoreCase(c)&& "".equals(v)){
							isall=true;
						}else if("UN".equalsIgnoreCase(c)){
							mudelBuf.append(" or B0110 like '"+v+"%'");
							if("".equals(v))
								mudelBuf.append(" or B0110 is null or B0110='' ");
							
						//}else if(c.equalsIgnoreCase("UN"))田野修改 控制范围直接配置部门时，查询人员没有结果 将UN修改为UM
						}else if("UM".equalsIgnoreCase(c))
						{
							mudelBuf.append(" or E0122 like '"+v+"%'");
							if("".equals(v))
								mudelBuf.append(" or E0122 is null or E0122='' ");
						}else if("@K".equalsIgnoreCase(c))
						{
							mudelBuf.append(" or E01A1 like '"+v+"%'");
							if("".equals(v))
								mudelBuf.append(" or E01A1 is null or E01A1='' ");
						}
					}
					if(isall)
						mudelBuf.append(" or 1=1 ");
				}
				
			}
			if ("".equals(mudelBuf.toString()))
            {
			    mudelBuf.append(" or 1=1");
            } 
			   
			
			ArrayList dblist=getFilterDbList(userview,conn);// userview.getPrivDbList();
			if(dblist.size()==0)
				return "";
			ArrayList fieldlist=new ArrayList();
			String strWhere=null;
			String strSelect=null;
			
			/**加权限过滤*/
			ArrayList alUsedFields=null;
			String temptable=null;
			/**是否加其他过滤条件*/
			if("1".equals(this.isfilter) && this.filterfactor!=null && this.filterfactor.trim().length()>0)
		    { 
			  alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
			  temptable=createSearchTempTable(conn);
		    } 
			if(dbpre!=null && !"null".equalsIgnoreCase(dbpre) && dbpre!=null && dbpre.length()>0)
			{
				if(dbpre.indexOf(",")==-1)
				{
			    	 if("1".equals(priv)&&!(this.viewunit_self!=null&& "1".equals(this.viewunit_self))) //按操作单位显示人员时，不用控制权限
		    		 {
	    			  	    strSelect=getSelectString(dbpre);	
	    			  	    /**是否为虚拟机构节点*/
	    				    if(!isBorg())
	    				    {
				    		
	    				    	if(this.chitemid!=null&&this.chitemid.trim().length()>0){
		    				    	String[] temps=expr.toString().split("\\|");
		    				    	FactorList factor = new FactorList(temps[0],temps[1],
		    					    		dbpre, false, false, true, 1, "su");
		    				    	strWhere = factor.getSqlExpression();
			    		    	}else if(isByModule)
			    		    	{
			    		    		if(isall)
			    		    		{
			    		    			String[] temps=expr.toString().split("\\|");
			    				    	FactorList factor = new FactorList(temps[0],temps[1], dbpre, false, false, true, 1, "su");
			    				    	if ("6".equalsIgnoreCase(nmodule))
			    				    	  //培训模块不加人员的高级权限，否则人员范围为空时，查不到人员   chenxg  2015-03-12
			    				    	    strWhere = factor.getSqlExpression();
			    				    	else
			    				    	 // 2014-03-06 wangrd 加上人员高级权限
			    				    	    strWhere=userview.getPrivSQLExpression(expr.toString(),dbpre,false,true,fieldlist);
			    		    		}else
			    		    		{
			    		    			String[] temps=expr.toString().split("\\|");
                                        FactorList factor = new FactorList(temps[0], temps[1], dbpre, false, false, true, 1, "su");
                                        if ("6".equalsIgnoreCase(nmodule))
                                          //培训模块不加人员的高级权限，否则人员范围为空时，查不到人员   chenxg  2015-03-12
                                            strWhere = factor.getSqlExpression();
                                        else {
                                            // 2014-03-06 wangrd 加上人员高级权限
                                            strWhere = userview.getPrivSQLExpression(expr.toString(), dbpre, false, true, fieldlist);
                                            strWhere += " and (" + mudelBuf.toString().substring(4) + ")";
                                        }
			    		    		}
			    		    	}
		    			    	else
			    		    	{
		    			    		if(privtype!=null&& "kq".equals(privtype))
		    	 				    {
		    			    			if (userview.getKqManageValue() != null && !"".equals(userview.getKqManageValue())){
		    			    				strWhere=userview.getKqPrivSQLExpression(expr.toString(), dbpre, fieldlist);
		    			    			}
		    			    			else {
		    			    				strWhere=userview.getPrivSQLExpression(expr.toString(),dbpre,false,true,fieldlist);
		    			    			}
		    	 				    }
		    			    		else {
		    			    			strWhere=userview.getPrivSQLExpression(expr.toString(),dbpre,false,true,fieldlist);
		    			    		}
			    		    	}
				    		
			    	    		strSql.append(strSelect);
			    			    strSql.append(strWhere); 
			    			    if("1".equals(this.isfilter) && this.filterfactor!=null && this.filterfactor.trim().length()>0)
			    			    { 
		    					    strSql.append(" and " + getFilterSQL(temptable,userview,this.isfilter,dbpre,alUsedFields,conn));
		    				    }
			    			    if(this.chitemid!=null&&this.chitemid.trim().length()>0){
			    			    	strSql.append(" and "+dbpre+"A01."+this.chitemid+"='1' ");
		    				    }
		    				    strSql.append(" UNION ");
		    				    /**非虚拟机构增加兼职子集记录查询*/
	    				    	DbNameBo dbbo=new DbNameBo(conn);
	    				    	if("RSYD".equals(this.model_string)){
	    				    		
	    				    	}else{
		    			    	strWhere=dbbo.getQueryFromPartTime(userview, dbpre, codevalue);
		    			    	if(!(strWhere==null|| "".equalsIgnoreCase(strWhere)))
			    		    	{
			    		    		strSql.append(strSelect);
				    	    		strSql.append(strWhere);
			    		    		if(this.chitemid!=null&&this.chitemid.trim().length()>0){
			    				    	strSql.append(" and "+dbpre+"A01."+this.chitemid+"='1' ");
			    				    }
			    		    		strSql.append(" UNION ");
		    			    	}
		    			    }
	    				    }
		    			    else//加载虚拟机构的人员列表
		    			    {
	    				    	strSql.append(strSelect);
		    			    	DbNameBo dbbo=new DbNameBo(conn);
		    			    	strWhere=dbbo.getQueryFromVorg(userview, dbpre, codevalue);
		    			    	strSql.append(strWhere);
		     			    	strSql.append(" UNION ");
		    			    }
	    			}
	    			else
	    			{
	    		      	   // strWhere=userview.getPrivSQLExpression(expr.toString(),(String)dblist.get(i),false,true,fieldlist);
	    			  	   // strSelect=getSelectString((String)dblist.get(i));
	    				if(this.chitemid!=null&&this.chitemid.trim().length()>0){
	    			    	String[] temps=expr.toString().split("\\|");
	    			    	FactorList factor = new FactorList(temps[0],temps[1],
	     				    		dbpre, false, false, true, 1, "su");
		    		    	strWhere = factor.getSqlExpression();
		    	    	}
		    			else
		     			{
	    					FactorList factor_bo=new FactorList(expr.toString(),dbpre,false,false,true,1,userview.getUserId());
	    	         	    strWhere=factor_bo.getSqlExpression();
	    				}  
	    				if(isByModule)
	    		    	{
	    		    		if(isall)
	    		    		{
	    		    			
	    		    		}else
	    		    		{
	    				    	strWhere+=" and ("+mudelBuf.toString().substring(4)+")";
	    		    		}
	    		    	}
		         	    
		         	    
		             	   strSelect=getSelectString(dbpre);         	   
		             	   strSql.append(strSelect);
		               	   strSql.append(strWhere);  
		             	   if(this.chitemid!=null&&this.chitemid.trim().length()>0){
		     			    	strSql.append(" and "+dbpre+"A01."+this.chitemid+"='1' ");
		    			   }
		               	   if("1".equals(this.isfilter) && this.filterfactor!=null && this.filterfactor.trim().length()>0)
		    			   { 
		              		  strSql.append(" and " + getFilterSQL(temptable,userview,this.isfilter,dbpre,alUsedFields,conn));
		   			        //strSql.append(")");
		    			   }
	     	         	  strSql.append(" UNION ");
	    			}
				}
				else
				{
					String[] arr=dbpre.split(",");
					if("1".equals(priv)&&!(this.viewunit_self!=null&& "1".equals(this.viewunit_self))) //按操作单位显示人员时，不用控制权限
					{
						for(int i=0;i<arr.length;i++)
						{        	
							
							if(arr[i]==null|| "".equals(arr[i]))
								continue;
					  	    
							if(this.chitemid!=null&&this.chitemid.trim().length()>0){
						    	String[] temps=expr.toString().split("\\|");
						    	FactorList factor = new FactorList(temps[0],temps[1],
							    		dbpre, false, false, true, 1, "su");
						    	strWhere = factor.getSqlExpression();
					    	}
							else if(isByModule)
		    		    	{
		    		    		if(isall)
		    		    		{
		    		    			String[] temps=expr.toString().split("\\|");
		    				    	FactorList factor = new FactorList(temps[0],temps[1],
		    				    			arr[i], false, false, true, 1, "su");
		    				    	strWhere = factor.getSqlExpression();
		    		    		}else
		    		    		{
		    		    			String[] temps=expr.toString().split("\\|");
		    				    	FactorList factor = new FactorList(temps[0],temps[1],
		    				    			arr[i], false, false, true, 1, "su");
		    				    	strWhere = factor.getSqlExpression();
		    				    	strWhere+=" and ("+mudelBuf.toString().substring(4)+")";
		    		    		}
		    		    	}
					    	else
					    	{
					    		strWhere=userview.getPrivSQLExpression(expr.toString(),arr[i],false,true,fieldlist);
					    	}
							
							
							
							strSelect=getSelectString(arr[i]);
						    strSql.append(strSelect);
						    strSql.append(strWhere); 
						    if(this.chitemid!=null&&this.chitemid.trim().length()>0){
						    	strSql.append(" and "+arr[i]+"A01."+this.chitemid+"='1' ");
						    }
						    if("1".equals(this.isfilter) && this.filterfactor!=null && this.filterfactor.trim().length()>0)
						    { 
						    	
							    strSql.append(" and " + getFilterSQL(temptable,userview,this.isfilter,arr[i],alUsedFields,conn));
						    }
						   strSql.append(" UNION ");         		
						}
						 
					}
					else
					{
			      	   for(int i=0;i<arr.length;i++)
			     	   {
			      		   // strWhere=userview.getPrivSQLExpression(expr.toString(),(String)dblist.get(i),false,true,fieldlist);
					  	   // strSelect=getSelectString((String)dblist.get(i));
			      		   if(this.chitemid!=null&&this.chitemid.trim().length()>0){
						    	String[] temps=expr.toString().split("\\|");
						    	FactorList factor = new FactorList(temps[0],temps[1],
							    		dbpre, false, false, true, 1, "su");
						    	strWhere = factor.getSqlExpression();
					    	}
					    	else
					    	{
			      		   
				      		   FactorList factor_bo=new FactorList(expr.toString(),arr[i],false,false,true,1,userview.getUserId());
				         	   strWhere=factor_bo.getSqlExpression();
					    	}
			      		 if(isByModule)
		    		    	{
		    		    		if(isall)
		    		    		{
		    		    		}else
		    		    		{
		    				    	strWhere+=" and ("+mudelBuf.toString().substring(4)+")";
		    		    		}
		    		    	}
			         	   
			         	   
			         	   strSelect=getSelectString(arr[i]);         	   
			         	   strSql.append(strSelect);
			         	   strSql.append(strWhere);  
			         	   if(this.chitemid!=null&&this.chitemid.trim().length()>0){
						    	strSql.append(" and "+arr[i]+"A01."+this.chitemid+"='1' ");
						    }
			           	   if("1".equals(this.isfilter) && this.filterfactor!=null && this.filterfactor.trim().length()>0)
						   { 
			           		  strSql.append(" and " + getFilterSQL(temptable,userview,this.isfilter,arr[i],alUsedFields,conn));
						        //strSql.append(")");
						   }
			         	  strSql.append(" UNION ");
			     	   }
					}
				}
			}
			else
			{
				 if("1".equals(priv)&&!(this.viewunit_self!=null&& "1".equals(this.viewunit_self))) //按操作单位显示人员时，不用控制权限
					{
						for(int i=0;i<dblist.size();i++)
						{        	
							
							
					  	    
							if(this.chitemid!=null&&this.chitemid.trim().length()>0){
						    	String[] temps=expr.toString().split("\\|");
						    	FactorList factor = new FactorList(temps[0],temps[1],
							    		dbpre, false, false, true, 1, "su");
						    	strWhere = factor.getSqlExpression();
					    	}
							else if(isByModule)
		    		    	{
		    		    		if(isall)
		    		    		{
		    		    			String[] temps=expr.toString().split("\\|");
		    				    	FactorList factor = new FactorList(temps[0],temps[1],
		    				    			(String)dblist.get(i), false, false, true, 1, "su");
		    				    	strWhere = factor.getSqlExpression();
		    		    		}else
		    		    		{
		    		    			String[] temps=expr.toString().split("\\|");
		    				    	FactorList factor = new FactorList(temps[0],temps[1],
		    				    			(String)dblist.get(i), false, false, true, 1, "su");
		    				    	strWhere = factor.getSqlExpression();
		    				    	strWhere+=" and ("+mudelBuf.toString().substring(4)+")";
		    		    		}
		    		    	}
					    	else
					    	{
					    		strWhere=userview.getPrivSQLExpression(expr.toString(),(String)dblist.get(i),false,true,fieldlist);
					    	}
							
							
							
							strSelect=getSelectString((String)dblist.get(i));
						    strSql.append(strSelect);
						    strSql.append(strWhere); 
						    if(this.chitemid!=null&&this.chitemid.trim().length()>0){
						    	strSql.append(" and "+(String)dblist.get(i)+"A01."+this.chitemid+"='1' ");
						    }
						    if("1".equals(this.isfilter) && this.filterfactor!=null && this.filterfactor.trim().length()>0)
						    { 
						    	
							    strSql.append(" and " + getFilterSQL(temptable,userview,this.isfilter,(String)dblist.get(i),alUsedFields,conn));
						    }
						   strSql.append(" UNION ");         		
						}
						 
					}
					else
					{
			      	   for(int i=0;i<dblist.size();i++)
			     	   {
			      		   // strWhere=userview.getPrivSQLExpression(expr.toString(),(String)dblist.get(i),false,true,fieldlist);
					  	   // strSelect=getSelectString((String)dblist.get(i));
			      		   if(this.chitemid!=null&&this.chitemid.trim().length()>0){
						    	String[] temps=expr.toString().split("\\|");
						    	FactorList factor = new FactorList(temps[0],temps[1],
							    		dbpre, false, false, true, 1, "su");
						    	strWhere = factor.getSqlExpression();
					    	}
					    	else
					    	{
			      		   
				      		   FactorList factor_bo=new FactorList(expr.toString(),(String)dblist.get(i),false,false,true,1,userview.getUserId());
				         	   strWhere=factor_bo.getSqlExpression();
					    	}
			      		   if(isByModule)
		    		    	{
		    		    		if(isall)
		    		    		{
		    		    			
		    		    		}else
		    		    		{
		    				    	strWhere+=" and ("+mudelBuf.toString().substring(4)+")";
		    		    		}
		    		    	}
			         	   
			         	   
			         	   strSelect=getSelectString((String)dblist.get(i));         	   
			         	   strSql.append(strSelect);
			         	   strSql.append(strWhere);  
			         	   if(this.chitemid!=null&&this.chitemid.trim().length()>0){
						    	strSql.append(" and "+(String)dblist.get(i)+"A01."+this.chitemid+"='1' ");
						    }
			           	   if("1".equals(this.isfilter) && this.filterfactor!=null && this.filterfactor.trim().length()>0)
						   { 
			           		  strSql.append(" and " + getFilterSQL(temptable,userview,this.isfilter,(String)dblist.get(i),alUsedFields,conn));
						        //strSql.append(")");
						   }
			         	  strSql.append(" UNION ");
			     	   }
					}
			}
			
		
		    strSql.setLength(strSql.length()-7);
		    strSql.append(" order by dbase desc,a0000");   
		   //System.out.println(strSql.toString());
		  }
		  catch(Exception ex)
		  {
			  ex.printStackTrace();
		  }
		  return strSql.toString() ;		  
	}
	
	private String createSearchTempTable(Connection conn)
	{
		String temptable="temp_search_xry_01";
		try{
			StringBuffer sql=new StringBuffer();
			sql.delete(0,sql.length());
			sql.append("drop table ");
			sql.append(temptable);
			try{
			  ExecuteSQL.createTable(sql.toString(),conn);
			}catch(Exception e)
			{
				//e.printStackTrace();
			}
			sql.delete(0,sql.length());
			sql.append("CREATE TABLE ");
			sql.append(temptable);
	//		sql.append("(a0100  varchar (100) PRIMARY KEY (a0100))");
			sql.append("(a0100  varchar (100))");
			try{
				  ExecuteSQL.createTable(sql.toString(),conn);				  			  
		    }catch(Exception e)
			{
				e.printStackTrace();
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return temptable;
	}
	public String outOrgEmployTree(UserView userview,String parentid)throws GeneralException
	{
		return outOrgEmployTree(userview,parentid,null);
	}
    public String outOrgEmployTree(UserView userview,String parentid,String dbpre)throws GeneralException
    {
    	String a_code = userview.getUnit_id();
        StringBuffer xmls = new StringBuffer();
        StringBuffer strsql = new StringBuffer();
        ResultSet rset = null;
        Connection conn = AdminDb.getConnection();
        Element root = new Element("TreeNode");
        root.setAttribute("id","00");
        root.setAttribute("text","root");
        root.setAttribute("title","organization");
        Document myDocument = new Document(root);
        String theaction=null;
        try
        {
          strsql.append("select codesetid,codeitemid,codeitemdesc,childid,'0' as orgtype,A0000 from organization where ");
          strsql.append(params);
          /**加载虚拟组织节点*/
          if(bloadvorg)
          {
        	strsql.append(" union select codesetid,codeitemid,codeitemdesc,childid,'1',A0000 as orgtype from vorganization where ");
            strsql.append(params);        	
          }
          strsql.append(" ORDER BY a0000,codeitemid,orgtype");
          
          ContentDAO dao = new ContentDAO(conn);
          rset = dao.search(strsql.toString());
          String codeid=null;
          /**加载组织机构树*/
          while (rset.next())
          {
        	codeid=rset.getString("codesetid"); 
            if("2".equalsIgnoreCase(this.loadtype))
            {
            	if("@K".equalsIgnoreCase(codeid)|| "UM".equalsIgnoreCase(codeid))
            		continue;            	
            }
            /*单位下挂岗位，也加载 wangrd 20150804
            if(this.loadtype.equalsIgnoreCase("1"))
            {
            	if(codeid.equalsIgnoreCase("@K"))
            		continue;
            }
            */
        	Element child = new Element("TreeNode");
            child.setAttribute("id", rset.getString("codesetid")+rset.getString("codeitemid"));
            child.setAttribute("text", rset.getString("codeitemdesc"));
            child.setAttribute("title", rset.getString("codeitemdesc"));
            if(!(this.action==null|| "".equals(this.action)))
            {
		        if(this.action.indexOf('?')==0)
		        	theaction=this.action+"?a_code="+rset.getString("codesetid")+rset.getString("codeitemid");
		        else
		        	theaction=this.action+"&a_code="+rset.getString("codesetid")+rset.getString("codeitemid");
            }
          //将url链接参数加密为一个参数encryptParam  xuj add 2014-9-2  start
            if(theaction!=null){
	  			int index = theaction.indexOf("&");
	  			if(index>-1){
	  				String allurl = theaction.substring(0,index);
	  				String allparam = theaction.substring(index);
	  				theaction=allurl+"&encryptParam="+PubFunc.encrypt(allparam);
	  			}
            }
  			//将url链接参数加密为一个参数encryParam  xuj add 2014-9-2  end
            if(theaction==null|| "".equals(theaction))
            	child.setAttribute("href", "javascript:void(0)");            	
            else
            	child.setAttribute("href", theaction);
            child.setAttribute("target", this.target);
    		String url="/system/load_tree?dbpre=" + dbpre + "&isfilter=" + this.isfilter + "&target="+this.target+"&flag="+this.flag+"&dbtype="+this.dbtype+"&priv="+this.priv+"&loadtype="+this.loadtype;
    		url=url+"&params=parentId<>codeitemid and parentid%3D'" + rset.getString("codeitemid")+"'";
    		url=url+"&id="+rset.getString("codesetid")+rset.getString("codeitemid");
    		if(this.isBloadvorg())
    			url=url+"&lv=1";
    		url=url+"&vg="+rset.getString("orgtype");
    		
    		//将url链接参数加密为一个参数encryptParam  xuj add 2014-9-2  start
    		if(url!=null){
	  			int index = url.indexOf("?");
	  			if(index>-1){
	  				String allurl = url.substring(0,index);
	  				String allparam = url.substring(index+1);
	  				url=allurl+"?encryptParam="+PubFunc.encrypt(allparam);
	  			}
    		}
  			//将url链接参数加密为一个参数encryParam  xuj add 2014-9-2  end
    		
     		//if((!rset.getString("codeitemid").equalsIgnoreCase(rset.getString("childid"))))
    				child.setAttribute("xml", url);
            if("UN".equals(rset.getString("codesetid")))
            {
               if("0".equals(rset.getString("orgtype")))
                child.setAttribute("icon","/images/unit.gif");
               else
                child.setAttribute("icon","/images/vroot.gif");
            }
            if("UM".equals(rset.getString("codesetid")))
            {
                if("0".equals(rset.getString("orgtype")))
                	child.setAttribute("icon","/images/dept.gif");
                else
                	child.setAttribute("icon","/images/vdept.gif");
            }
            if("@K".equals(rset.getString("codesetid")))
            {
                if("0".equals(rset.getString("orgtype")))
                	child.setAttribute("icon","/images/pos_l.gif");
                else
                	child.setAttribute("icon","/images/vpos_l.gif");
            }
            root.addContent(child);
          }
          /**加载当前机构下的人员*/
          if("1".equals(flag)&&!this.isBfirst())
          {
        	  dbpre=dbpre!=null&&dbpre.trim().length()>0?dbpre:"Usr";
        	  getEmploys(userview,parentid,root,conn,dbpre,null);
          }
          XMLOutputter outputter = new XMLOutputter();
          Format format=Format.getPrettyFormat();
          format.setEncoding("UTF-8");
          outputter.setFormat(format);
          xmls.append(outputter.outputString(myDocument));
          //System.out.println("SQL=" +xmls.toString());
        }
        catch (Exception ee)
        {
          ee.printStackTrace();
          //GeneralExceptionHandler.Handle(ee);
        }
        finally
        {
          try
          {
            if (rset != null)
            {
              rset.close();
            }
            if (conn != null)
            {
              conn.close();
            }
          }
          catch (SQLException ee)
          {
            ee.printStackTrace();
          }
          
      }
      return xmls.toString();        
    }
    
    
    /**
     * 取得当前用户所在单位的最上层节点
     * @param userview
     * @return
     */
    public String getSelfUns(UserView userview)
    {
    	String un="";
    	 Connection conn =null;
         ResultSet rset = null;
    	try
    	{
    		ArrayList list=new ArrayList();
    		conn = AdminDb.getConnection();
    		String a_un=userview.getUserOrgId();
    		String a_um=userview.getUserDeptId();
    		String a_k=userview.getUserPosId();
    		ContentDAO dao = new ContentDAO(conn);
	        
    		         
    		if(a_un.trim().length()>0)
    			list.add(a_un);
    		String codeitemid=a_un;
    		if(codeitemid.trim().length()==0)
    			codeitemid=a_um;
    		if(codeitemid.trim().length()==0)
    			codeitemid=a_k;
    		
    		while(true)
    		{
    				String sql="select * from organization where codeitemid=(select parentid from organization where codeitemid='"+codeitemid+"' and codeitemid<>parentid)";
    				rset = dao.search(sql);
    				if(rset.next())
    				{
    					String a_codeitemid=rset.getString("codeitemid");
    					String codesetid=rset.getString("codesetid");
    					if("UN".equalsIgnoreCase(codesetid))
    						list.add(a_codeitemid);
    					codeitemid=a_codeitemid;
    				}
    				else
    					break;
    		}
    		
    		for(int j=0;j<list.size();j++){
    			un+=",'"+((String)list.get(j))+"'";
    		}
    		
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	finally
    	{
    		try
    		{
    			if(rset!=null)
    				rset.close();
    			if(conn!=null)
    				conn.close();
    			
    		}
    		catch(Exception ee)
    		{
    			ee.printStackTrace();
    		}
    	}
    	if(un.length()>0)
    		return un.substring(1);
    	return un;
    }
    
    
    
    
    public String outOrgEmployTree(UserView userview,String parentid,String dbpre,
    		String viewunit,String ctrlviewunit,String nextlevel,String umlayer,String nmodule,String ctrlmodule,String cascadingctrl,String parent_id)throws GeneralException
    {
    	String a_code = userview.getUnit_id();
    	if(viewunit!=null&& "1".equals(viewunit)){
	    	a_code=a_code!=null?a_code:"";
	    	if("UN".equals(a_code)){//xuj 2010-6-7 add 针对oracle非超级业务用户未授权任何操作单位时的情况 UN
	    		a_code="";
	    	}
    	}
    	if(nmodule!=null&&!"".equals(nmodule))
    	{
    		if(userview.isSuper_admin())
    			a_code="UN";
    		else
    			a_code = userview.getUnitIdByBusi(nmodule);
    		ctrlmodule=nmodule;
    	}
    	if(viewunit!=null&& "1".equals(viewunit))
    	{
    		ctrlviewunit="1";
    		this.viewunit_self="1";
    	}
    	if("0".equals(this.viewunit_self)&& "1".equals(ctrlviewunit))
    		this.viewunit_self="1";
    	
    	a_code = PubFunc.getTopOrgDept(a_code);
    	a_code=a_code!=null?a_code:"";
        StringBuffer xmls = new StringBuffer();
        StringBuffer strsql = new StringBuffer();
        ResultSet rset = null;
        Connection conn = AdminDb.getConnection();
        Element root = new Element("TreeNode");
        root.setAttribute("id","00");
        root.setAttribute("text","root");
        root.setAttribute("title","organization");
        Document myDocument = new Document(root);
       
        String selfUns="";
        if("1".equals(this.showSelfNode))
        {
        	selfUns=getSelfUns(userview);
        }
        
        String theaction=null;
        String codevalue="";
        String codeall="";
        
        
        
        
        
        String unitarr[] = a_code.split("`"); 
        int n=0;
        for(int i=0;i<unitarr.length;i++){
        	String codeid = unitarr[i];
        	if(codeid!=null&&codeid.trim().length()>2){
        		codevalue+="'"+codeid.substring(2)+"',";
        		n++;
        	}else if(codeid!=null&& "UN".equalsIgnoreCase(codeid)){
        		codeall=codeid;
        		n++;
        	}
        }
        //如果没有操作单位，需按管理范围啦  dengcan
        //编制管理取操作单位的权限，不获取其他权限 chenxg
        if(n==0&&!userview.isSuper_admin()&& "1".equals(this.priv)
                &&!"bz".equalsIgnoreCase(this.privtype))
        {
        	String _codeid=userview.getManagePrivCode();
    		String _codevalue=userview.getManagePrivCodeValue();
    		if(_codevalue.length()>0) {
				codevalue += "'" + _codevalue + "',";
			}else if("un".equalsIgnoreCase(_codeid)){
				codeall="UN";
			}
        }
        if(isShowSelfDepts!=null&& "1".equals(isShowSelfDepts)){
        	
        	codevalue="'"+userview.getUserDeptId()+"',";
        }
        codevalue+="'aaaaa'";
        if(nmodule!=null&&!"".equals(nmodule))
        {
        	codevalue="";
        	codeall="";
        	 String nunitarr[] = a_code.split("`"); 
             for(int i=0;i<nunitarr.length;i++){
             	String codeid = nunitarr[i];
             	if(codeid!=null&&codeid.trim().length()>2){
             		codevalue+="'"+codeid.substring(2)+"',";
             	}else if(codeid!=null&& "UN".equalsIgnoreCase(codeid)){
             		codeall=codeid;
             	}
             }
             codevalue+="'aaaaa'";
        }
        StringBuffer wheresql = new StringBuffer();
        if(codeall!=null&& "UN".equalsIgnoreCase(codeall)){
        	 wheresql.append(" codeitemid=parentid");
        }else{
        	wheresql.append(" codeitemid in("+codevalue+")");
        }
        String ctrlsql="";
        if((nmodule==null|| "".equals(nmodule))&&(ctrlmodule!=null)&& "1".equals(cascadingctrl)&&codeall!=null&&!"UN".equalsIgnoreCase(codeall))
        {
        	ctrlsql=this.getLinkChild(ctrlmodule, userview, parent_id, conn,1);
        }
        if((viewunit==null|| "0".equals(viewunit))&&ctrlviewunit!=null&& "1".equals(ctrlviewunit)&& "1".equals(cascadingctrl)&&codeall!=null&&!"UN".equalsIgnoreCase(codeall))
        {
        	ctrlsql=this.getLinkChild(ctrlmodule, userview, parent_id, conn,2);
        }
        try
        {
        	
         if("0".equals(this.showDbName))
         {
        	
	          strsql.append("select codesetid,codeitemid,codeitemdesc,childid,'0' as orgtype,A0000 from organization where ");
	          if(viewunit!=null&& "0".equals(viewunit))
	          {
	        	  if(nmodule!=null&&!"".equals(nmodule))
	        	  {
	        		  strsql.append(wheresql.toString());
	        	  }
	        	  else
	        	  {
	        		  if(ctrlsql!=null&&!"".equals(ctrlsql))
	        		  {
	        			  strsql.append(ctrlsql);
	        		  }
	        		  else
	        		  {
	            	     strsql.append(params);
	        		  }
	        	  }
	          }else{
	        	  if("UN".equalsIgnoreCase(parentid)){
	        		  strsql.append(" parentid=codeitemid");
	        		  if("1".equals(nextlevel))
	        			  nextlevel="2";
	        	  }else{
	        		  strsql.append(wheresql.toString());
	        		  if(codeall!=null&& "UN".equalsIgnoreCase(codeall)){
	//        			  nextlevel="2";
	        		  }
	        	  }
	          }
	          
	          if("1".equals(this.showSelfNode)&&selfUns.length()>0)
	          {
	        	  strsql.append(" and ( ( codeitemid in ("+selfUns+") and codesetid='UN' ) OR codesetid<>'UN' ) ");
	          }
	          /**组织机构历史点控制-20091130*/
	          String bosdate=DateStyle.dateformat(new Date(),"yyyy-MM-dd");
	          strsql.append(" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date ");
	          //end.	          
	          
	          /**加载虚拟组织节点*/
	          if(bloadvorg)
	          {
	        	strsql.append(" union select codesetid,codeitemid,codeitemdesc,childid,'1',A0000 as orgtype from vorganization where ");
	        	if(viewunit!=null&& "0".equals(viewunit))
	          	  {

		        	  if(nmodule!=null&&!"".equals(nmodule))
		        	  {
		        		  strsql.append(wheresql.toString());
		        	  }
		        	  else
		        	  {
		        		  if(ctrlsql!=null&&!"".equals(ctrlsql)&&!userview.isSuper_admin()&&!"1".equals(userview.getGroupId()))
		        		  {
		        			  strsql.append(ctrlsql);
		        		  }
		        		  else
		        		  {
		            	     strsql.append(params);
		        		  }
		        	  }
	          	  }else{
	            	if("UN".equalsIgnoreCase(parentid)){
	          		  strsql.append(" parentid=codeitemid");
	          	  	}else{
	          		  strsql.append(wheresql.toString());
	          	  	}
	            }    	
	          }
		       // 不显示岗位
		      	if (!isPost) {
		      		strsql.append(" and codesetid<>'@K' ");
		      	}
	          strsql.append(" ORDER BY a0000,codeitemid,orgtype");
	          
	          ContentDAO dao = new ContentDAO(conn);
	          rset = dao.search(strsql.toString());
	          String codeid=null;
	          /**加载组织机构树*/
	          while (rset.next())
	          {
	        	codeid=rset.getString("codesetid"); 
	            if("2".equalsIgnoreCase(this.loadtype))
	            {
	            	if("@K".equalsIgnoreCase(codeid)|| "UM".equalsIgnoreCase(codeid))
	            		continue;            	
	            }
	            if("1".equalsIgnoreCase(this.loadtype))
	            {
	            	if("@K".equalsIgnoreCase(codeid))
	            		continue;
	            }
	        	Element child = new Element("TreeNode");
	            child.setAttribute("id", rset.getString("codesetid")+rset.getString("codeitemid"));
	            // 49536  库中的岗位没有岗位名称 节点无法添加null 导致报错
	            String codeitemdesc = rset.getString("codeitemdesc");
	            codeitemdesc = StringUtils.isBlank(codeitemdesc) ? "" : codeitemdesc;
	            child.setAttribute("text", codeitemdesc);
	            child.setAttribute("title", codeitemdesc);
	            if(!(this.action==null|| "".equals(this.action)))
	            {
			        if(this.action.indexOf('?')==0){
			        	theaction=this.action+"?a_code="+rset.getString("codesetid")+rset.getString("codeitemid"); 
			        }
			        else{
			        	theaction=this.action+"&a_code="+rset.getString("codesetid")+rset.getString("codeitemid");
			        }
	            }
	            if(theaction==null|| "".equals(theaction)|| "false".equalsIgnoreCase(this.isAddAction))
	            	child.setAttribute("href", "javascript:void(0)");            	
	            else{
	            	//将url链接参数加密为一个参数encryptParam  xuj add 2014-9-2  start
	            	if(theaction!=null){
		      			int index = theaction.indexOf("&");
		      			if(index>-1){
		      				String allurl = theaction.substring(0,index);
		      				String allparam = theaction.substring(index);
		      				theaction=allurl+"&encryptParam="+PubFunc.encrypt(allparam);
		      			}
	            	}
	      			//将url链接参数加密为一个参数encryParam  xuj add 2014-9-2  end
	            	if("2".equals(nextlevel))
	            		child.setAttribute("href", theaction+"&nextlevel="+nextlevel);
	            	else
	            		child.setAttribute("href", theaction);	
	            }
	            child.setAttribute("target", this.target);
	    		String url="/system/load_tree?dbpre=" + dbpre + "&isfilter=" + this.isfilter 
	    				+ "&target="+this.target+"&flag="+this.flag+"&dbtype="+this.dbtype
	    				+"&priv="+this.priv+"&loadtype="+this.loadtype+"&chitemid="+this.chitemid
	    				+"&umlayer="+umlayer+"&orgcode="+this.orgcode+"&privtype="+this.privtype;
	    		//如果不级联显示，加上限制
	    		url=url+"&params=parentId<>codeitemid and parentid%3D'" + rset.getString("codeitemid")+"'";
	    		url=url+"&id="+rset.getString("codesetid")+rset.getString("codeitemid");
	    		if("1".equals(this.showSelfNode))
	    				url=url+"&showSelfNode="+this.showSelfNode;
	    		if(this.isBloadvorg())
	    			url=url+"&lv=1";
	    		url=url+"&vg="+rset.getString("orgtype");
	    		url=url+"&cascadingctrl="+cascadingctrl+"&ctrlviewunit="+ctrlviewunit;
	    		if("1".equals(cascadingctrl))
	    			url+="&parent_id="+rset.getString("codeitemid");
	    		if(ctrlmodule!=null&&!"".equals(ctrlmodule))
	    		{
	    			url+="&ctrlmodule="+ctrlmodule;
	    		}
	    		url+="&isAddAction="+this.isAddAction;
	    		
	    		//将url链接参数加密为一个参数encryptParam  xuj add 2014-9-2  start
	    		if(url!=null){
		  			int index = url.indexOf("?");
		  			if(index>-1){
		  				String allurl = url.substring(0,index);
		  				String allparam = url.substring(index+1);
		  				url=allurl+"?encryptParam="+PubFunc.encrypt(allparam);
		  			}
	    		}
	  			//将url链接参数加密为一个参数encryParam  xuj add 2014-9-2  end
	  			
	     		//if((!rset.getString("codeitemid").equalsIgnoreCase(rset.getString("childid"))))
	        	if("0".equals(nextlevel))
	    			child.setAttribute("xml", url);
	        	else if("1".equals(nextlevel))
	        		child.setAttribute("xml", url+"&nextlevel=2");
	        	else if("2".equals(nextlevel))
	        		child.setAttribute("xml", "aaaaa");
	        	umlayer = umlayer!=null&&umlayer.trim().length()>0?umlayer:"0";
	        	try{//控制部门加载层级
	        		int uml= Integer.parseInt(umlayer);
		        	if(uml>0){
		        		if("UM".equalsIgnoreCase(codeid)){
		        			String codeitemid = rset.getString("codeitemid");
		        			this.currentlayer=1;
		        			if(this.currentlayer(codeitemid)==uml){
		        				child.setAttribute("xml", "");
		        			}
		        		}
		        	}
	        	}catch(Exception e){}
	            if("UN".equals(rset.getString("codesetid")))
	            {
	               if("0".equals(rset.getString("orgtype")))
	                child.setAttribute("icon","/images/unit.gif");
	               else
	                child.setAttribute("icon","/images/vroot.gif");
	            }
	            if("UM".equals(rset.getString("codesetid")))
	            {
	                if("0".equals(rset.getString("orgtype")))
	                	child.setAttribute("icon","/images/dept.gif");
	                else
	                	child.setAttribute("icon","/images/vdept.gif");
	            }
	            if("@K".equals(rset.getString("codesetid")))
	            {
	                if("0".equals(rset.getString("orgtype")))
	                	child.setAttribute("icon","/images/pos_l.gif");
	                else
	                	child.setAttribute("icon","/images/vpos_l.gif");
	            }
	            root.addContent(child);
	          }
	          /**加载当前机构下的人员*/
	          if("1".equals(flag)&&!this.isBfirst())
	          {
	        	  dbpre=dbpre!=null&&dbpre.trim().length()>0?dbpre:"Usr";
	        	  getEmploys(userview,parentid,root,conn,dbpre,ctrlmodule);
	          }
         }
         else  //如果如果机构下加载人员信息 需先加载人员库
         {
        	 ArrayList dblist=getFilterDbList(userview,conn);// userview.getPrivDbList();
 			 if(dblist.size()>0)
 			 {
 				ContentDAO dao = new ContentDAO(conn);
			    rset = dao.search("select * from dbname "); 
			    HashMap dbMap=new HashMap();
			    while(rset.next())
			    {
			    	dbMap.put(rset.getString("pre").toLowerCase(),rset.getString("dbname"));
			    }
 				for(int i=0;i<dblist.size();i++) 
 				{
 					String pre=(String)dblist.get(i);
 					if(dbpre!=null && !"null".equalsIgnoreCase(dbpre) && dbpre!=null && dbpre.length()>0)
 					{
 						if(!dbpre.equalsIgnoreCase(pre))
 							continue;
 					}
 					if(dbMap.get(pre.toLowerCase())==null)
 						continue;
 			        
 			        String dbname=(String)dbMap.get(pre.toLowerCase());
 			        	
 			        Element child = new Element("TreeNode");
 			        child.setAttribute("id", "@@"+pre);
 			        child.setAttribute("text",dbname);
 			        child.setAttribute("title",dbname);
 			        child.setAttribute("href", "javascript:void(0)");    
 			        child.setAttribute("target", this.target);
 			            
 			        String url="/system/load_tree?dbpre=" +pre
 						+ "&isfilter=" + this.isfilter + "&target="+this.target
 						+"&flag="+this.flag+"&dbtype="+this.dbtype+"&priv="
 						+this.priv+"&loadtype="+this.loadtype+"&first=1"
 						+"&lv="+(this.bloadvorg?"1":"0")+"&viewunit="+viewunit+"&nextlevel="+nextlevel
 						+"&chitemid="+this.chitemid+"&orgcode="+this.orgcode+"&umlayer="+umlayer+"&nmodule="+nmodule+"&privtype="+this.privtype;
 			       if("1".equals(this.showSelfNode))
	    				url=url+"&showSelfNode="+this.showSelfNode;  
 			        url+="&cascadingctrl="+cascadingctrl+"&ctrlviewunit="+ctrlviewunit;
 			       if("1".equals(cascadingctrl))
 		    			url+="&parent_id="+rset.getString("codeitemid");
 			       if(ctrlmodule!=null&&!"".equals(ctrlmodule))
 		    		{
 		    			url+="&ctrlmodule="+ctrlmodule;
 		    		}
 			        url+="&isAddAction="+this.isAddAction;
 			        String acodeid=userview.getManagePrivCode();
 			        String acodevalue=userview.getManagePrivCodeValue(); 
 					if(privtype!=null&& "kq".equals(privtype))
 				    {
 						acodeid=RegisterInitInfoData.getKqPrivCode(userview);
 						acodevalue=RegisterInitInfoData.getKqPrivCodeValue(userview);
 				    }
 			        
 			        String aa_code=acodeid+acodevalue;	
	 			 	  
	 			 	String acodeall = userview.getUnit_id();
	 			 	acodeall = PubFunc.getTopOrgDept(acodeall);
	 			 	String aunitarr[] = acodeall.split("`"); 
	 			 	if(viewunit!=null&& "1".equals(viewunit)){
	 			 			for(int j=0;j<aunitarr.length;j++){
	 			 				if(aunitarr[j]!=null&& "UN".equalsIgnoreCase(aunitarr[j])){
	 			 					aa_code="UN";
	 			 					break;
	 			 				}else{
	 			 					aa_code="";
	 			 				}
	 			 			}
	 			 	 }
 			         if(nmodule!=null&&!"".equals(nmodule))
 			         {
 			        	 if(userview.isSuper_admin())
 			        		 aa_code="UN";
 			        	 else{
	 			        	String nacodeall = userview.getUnitIdByBusi(nmodule);
	 		 			 	nacodeall = PubFunc.getTopOrgDept(nacodeall);
	 		 			 	String naunitarr[] = nacodeall.split("`"); 
	 		 			 	for(int j=0;j<naunitarr.length;j++){
	 		 			 		if(naunitarr[j]!=null&& "UN".equalsIgnoreCase(naunitarr[j])){
	 		 			 			aa_code="UN";
	 		 			 			break;
	 		 			 		}else{
	 		 			 			aa_code="";
	 		 			 		}
	 		 			 	}
			        	 }
 			         }
 			           
				 	if("0".equals(this.priv))//不加权限过滤
				 	{
				 				url=url+"&params=codeitemid%3Dparentid&id=UN";
				 				aa_code="";
				 	}
				 	else
				 	{
				 				if(!("UN".equals(aa_code)))
				 				{
				 					if("1".equals(viewunit))
				 						url=url+"&params=codeitemid%3D'"+acodevalue+"'&id="+acodeall;
				 					else
				 						url=url+"&params=codeitemid%3D'"+acodevalue+"'&id="+acodeid+acodevalue;
				 					
				 				}
				 				else
				 				{
				 					url=url+"&params=codeitemid%3Dparentid&id=UN";
				 				}
				 	}
				 	
				 	//将url链接参数加密为一个参数encryptParam  xuj add 2014-9-2  start
				 	if(url!=null){
		  			int index = url.indexOf("?");
		  			if(index>-1){
		  				String allurl = url.substring(0,index);
		  				String allparam = url.substring(index+1);
		  				url=allurl+"?encryptParam="+PubFunc.encrypt(allparam);
		  			}
				 	}
		  			//将url链接参数加密为一个参数encryParam  xuj add 2014-9-2  end
		  			
			 		child.setAttribute("xml", url);
			 		child.setAttribute("icon","/images/open.png");//人员库改用文件夹图标2011-12-06 xieguiquan
			 		root.addContent(child);
 				}
 			 }
        	 
         }
        
          XMLOutputter outputter = new XMLOutputter();
          Format format=Format.getPrettyFormat();
          format.setEncoding("UTF-8");
          outputter.setFormat(format);
          xmls.append(outputter.outputString(myDocument));
          //System.out.println("SQL=" +xmls.toString());
        }
        catch (Exception ee)
        {
          ee.printStackTrace();
          //GeneralExceptionHandler.Handle(ee);
        }
        finally
        {
          try
          {
            if (rset != null)
            {
              rset.close();
            }
            if (conn != null)
            {
              conn.close();
            }
          }
          catch (SQLException ee)
          {
            ee.printStackTrace();
          }
          
      }
      return xmls.toString();        
    }
    
    public String outOrganizationTree()throws GeneralException
    {
  	
        StringBuffer xmls = new StringBuffer();
        StringBuffer strsql = new StringBuffer();
        ResultSet rset = null;
        Connection conn = AdminDb.getConnection();
        Element root = new Element("TreeNode");
        root.setAttribute("id","00");
        root.setAttribute("text","root");
        root.setAttribute("title","organization");
        Document myDocument = new Document(root);
        String theaction=null;
        try
        {
          //    	strsql.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
          strsql.append("select codesetid,codeitemid,codeitemdesc,childid from organization where ");
          strsql.append(params);
          /**组织机构历史点控制-20091130*/
          String bosdate=DateStyle.dateformat(new Date(),"yyyy-MM-dd");
          strsql.append(" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date ");
          //end.
          strsql.append(" order by a0000");
          ContentDAO dao = new ContentDAO(conn);
         // System.out.println("SQL="+strsql.toString());
          rset = dao.search(strsql.toString());
          while (rset.next())
          {
            Element child = new Element("TreeNode");
            child.setAttribute("id", rset.getString("codeitemid"));
            child.setAttribute("text", rset.getString("codeitemdesc"));
            child.setAttribute("title", rset.getString("codeitemid")+":"+rset.getString("codeitemdesc"));

            theaction=this.action+"?b_query=link&encryptParam="+PubFunc.encrypt("a_code="+rset.getString("codesetid")+rset.getString("codeitemid"));
            child.setAttribute("href", theaction);
            child.setAttribute("target", this.target);
            //if(!HaveChild(rset.getString("codeitemid"),conn))
            child.setAttribute("xml", "/system/security/get_org_tree.jsp?encryptParam="+PubFunc.encrypt("flag="+flag+"&params=parentId<>codeitemid and parentid%3D'" + rset.getString("codeitemid")+"'"));
            if("UN".equals(rset.getString("codesetid")))
                child.setAttribute("icon","/images/unit.gif");
            if("UM".equals(rset.getString("codesetid")))
                child.setAttribute("icon","/images/dept.gif");
            if("@K".equals(rset.getString("codesetid")))
                child.setAttribute("icon","/images/pos_l.gif");
            
            root.addContent(child);
          }

          XMLOutputter outputter = new XMLOutputter();
          Format format=Format.getPrettyFormat();
          format.setEncoding("UTF-8");
          outputter.setFormat(format);

          xmls.append(outputter.outputString(myDocument));

          //System.out.println("SQL=" +xmls.toString());
        }
        catch (Exception ee)
        {
          ee.printStackTrace();
          GeneralExceptionHandler.Handle(ee);
        }
        finally
        {
          try
          {
            if (rset != null)
            {
              rset.close();
            }
            if (conn != null)
            {
              conn.close();
            }
          }
          catch (SQLException ee)
          {
            ee.printStackTrace();
          }
          
      }
      return xmls.toString();        
    }
	private String getFilterSQL(String temptable,UserView uv,String isfilter,String BasePre,ArrayList alUsedFields,Connection conn1)
	{
		String sql=" (1=1)";
		try{
		if("1".equals(isfilter))
		{
			StringBuffer inserSql=new StringBuffer();
			inserSql.append("insert into ");
			inserSql.append(temptable);
			inserSql.append("(a0100) select '");
			inserSql.append(BasePre);
			inserSql.append("'"+Sql_switcher.concat()+"a0100 from ");
			ContentDAO dao=new ContentDAO(conn1);
			//this.filterfactor="性别 <> '1'";
			int infoGroup = 0; // forPerson 人员
			int varType = 8; // logic								
			String whereIN=InfoUtils.getWhereINSql(uv,BasePre);
			whereIN="select a0100 "+whereIN;							
			YksjParser yp = new YksjParser( uv ,alUsedFields,
					YksjParser.forSearch, varType, infoGroup, "Ht", BasePre);
			YearMonthCount ymc=null;
			 if("1".equals(this.isfilter) && this.filterfactor!=null && this.filterfactor.trim().length()>0&&this.midvari_sql!=null&&this.midvari_sql.trim().length()>0)
			    { 
				 yp.setSupportVar(true,this.midvari_sql);  //支持临时变量
			    }
			yp.run_Where(this.filterfactor, ymc,"","", dao, whereIN,conn1,"A", null);
			String tempTableName = yp.getTempTableName();
			sql="('" + BasePre + "'"+Sql_switcher.concat()+  BasePre +"A01.a0100 in  (select distinct a0100 from " + temptable + "))";
			inserSql.append(tempTableName);
			if( yp.getSQL().length()>0)
			inserSql.append(" where " + yp.getSQL());
			dao.insert(inserSql.toString(),new ArrayList());	
		}	
		}catch(Exception e)
		{
			e.printStackTrace();
		}	
		return sql;
	}
	public String getPriv() {
		return priv;
	}

	public void setPriv(String priv) {
		this.priv = priv;
	}

	public String getLoadtype() {
		return loadtype;
	}

	public void setLoadtype(String loadtype) {
		this.loadtype = loadtype;
	}

	public String getIsfilter() {
		return isfilter;
	}

	public void setIsfilter(String isfilter) {
		this.isfilter = isfilter;
	}

	public String getFilterfactor() {
		return filterfactor;
	}

	public void setFilterfactor(String filterfactor) {
		this.filterfactor = filterfactor;
	}

	public boolean isBloadvorg() {
		return bloadvorg;
	}

	public void setBloadvorg(boolean bloadvorg) {
		this.bloadvorg = bloadvorg;
	}

	public boolean isBorg() {
		return borg;
	}

	public void setBorg(boolean borg) {
		this.borg = borg;
	}

	public String getShowDbName() {
		return showDbName;
	}

	public void setShowDbName(String showDbName) {
		this.showDbName = showDbName;
	}

	public String getShowSelfNode() {
		return showSelfNode;
	}

	public void setShowSelfNode(String showSelfNode) {
		this.showSelfNode = showSelfNode;
	}

	public String getChitemid() {
		return chitemid;
	}

	public void setChitemid(String chitemid) {
		this.chitemid = chitemid;
	}

	public String getOrgcode() {
		return orgcode;
	}

	public void setOrgcode(String orgcode) {
		this.orgcode = orgcode;
	}
	
	private int currentlayer=1;
	/**
	 * 判断部门是第几层
	 * @param codeitemid
	 * @return
	 */
	private int currentlayer(String codeitemid){
		String sql = "select * from organization where codesetid='UM' and codeitemid=(select parentid from organization where codesetid='UM' and codeitemid='"+codeitemid+"')";
		/**加载虚拟组织节点*/
        if(bloadvorg)
        {
        	sql += " union select * from vorganization where codesetid='UM' and codeitemid=(select parentid from vorganization where codesetid='UM' and codeitemid='"+codeitemid+"')";
        }
        Connection conn =null;
        ResultSet rset = null;
        try {
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			rset = dao.search(sql);
			while(rset.next()){
				++currentlayer;
				currentlayer(rset.getString("codeitemid"));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				rset.close();
				if(conn!=null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
        
		return currentlayer;
	}
	public String getLinkChild(String module,UserView view,String parentid,Connection conn,int type)
	{
		StringBuffer sql= new StringBuffer("");
		try
		{
			String unit="";
			if(type==1)
				unit=view.getUnitIdByBusi(module);
			else
				unit=view.getUnit_id();
			if(unit==null|| "".equals(unit))
				sql.append(" 1=2 ");
			else
			{
		    	String wheresql="";
	            String unitarr[] = unit.split("`"); 
	            HashMap map = new HashMap();
	            boolean isall=false;
	            for(int i=0;i<unitarr.length;i++){
    	        	String codeid = unitarr[i];
    	        	if(codeid==null|| "".equals(codeid))
    	        		continue;
	            	if(codeid!=null&&codeid.trim().length()>2){
	            		map.put(codeid.substring(2),codeid.substring(2));
	            		wheresql+="'"+codeid.substring(2)+"',";
	            	}else if(codeid!=null&& "UN".equalsIgnoreCase(codeid)){
	            		isall=true;
	            	}
	            }
    			StringBuffer buf = new StringBuffer();
	    		buf.append(" select codeitemid,parentid,childid from organization where ");
	    		buf.append(" codeitemid like '"+parentid+"%' and codeitemid<>'"+parentid+"'");
	    		if(!"".equals(wheresql))
	    		{
	    			buf.append(" and codeitemid in ("+wheresql+"'aaaaa'"+")");
	    		}
	    		buf.append(" and (UPPER(codesetid)='UN' or UPPER(codesetid)='UM') order by codeitemid");
	    		ContentDAO dao = new ContentDAO(conn);
	    		RowSet rs = dao.search(buf.toString());
	    		ArrayList list =new ArrayList();
	    		while(rs.next())
	    		{
	    			if(isall)
	    			{
	    				sql.append(",'"+rs.getString("codeitemid")+"'");
	    			}
	    			else
	    			{
	    				LazyDynaBean bean = new LazyDynaBean();
	    				bean.set("codeitemid", rs.getString("codeitemid"));
	    				bean.set("parentid",rs.getString("parentid"));
	    				bean.set("childid", rs.getString("childid"));
	    				list.add(bean);
	    				
	    			}
	    		}
	    		if(!isall)
	    		{
	    			for(int i=0;i<list.size();i++)
	    			{
	    				LazyDynaBean bean = (LazyDynaBean)list.get(i);
	    				String parent_id=(String)bean.get("parentid");
	    				String codeitemid=(String)bean.get("codeitemid");
	    				/**如果父节点就是当前节点，加上*/
	    				if(parent_id.equalsIgnoreCase(parentid))
	    					sql.append(",'"+codeitemid+"'");
	    				else
	    				{
	    					/**判断父节点，*/
	    					if(this.isAdd(list, parentid, parent_id))
	    					{
	    						sql.append(",'"+codeitemid+"'");
	    					}
	    				}
	    				
	    			}
	    		}
	    		if(sql.toString().length()>0)
	    		{
	    			String str=sql.toString().substring(1);
	    			sql.setLength(0);
	    			sql.append("codeitemid in ("+str+")");
	    		}
	    		else
	    		{
	    			sql.append(" 1=2 ");
	    		}
	    		
	    		if(isall)//个人所得税 当用户的权限是所有时
	    		{
	    			sql.setLength(0);
	    			sql.append(" parentid='"+parentid+"' and codeitemid<>'"+parentid+"' ");
	    		}
	    		
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return sql.toString();
	}
	
	public boolean isAdd(ArrayList codeList,String pparentid,String itemid)
	{
		boolean flag=true;
		try
		{
			for(int i=0;i<codeList.size();i++)
			{
				LazyDynaBean bean=(LazyDynaBean)codeList.get(i);
				String parent_id=(String)bean.get("parentid");
				String codeitemid=(String)bean.get("codeitemid");
			    /**如果当前循环到得节点，是调递归节点的父节点，并且当前循环节点的父节点是当前点击的节点，则现在不展现*/
				if(codeitemid.equalsIgnoreCase(itemid)&&parent_id.equalsIgnoreCase(pparentid))
				{
					return false;
				}
				else if((!codeitemid.equalsIgnoreCase(itemid))&&itemid.startsWith(codeitemid))
				{
					return false;
				}
				else if(codeitemid.equalsIgnoreCase(itemid))
				{
					isAdd(codeList,pparentid,parent_id);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
	
	/**
	*田野添加含有判断标记approvalRelationDefine参数的方法
	*/
	  public String outOrgEmployTree(UserView userview,String parentid,String dbpre,
	    		String viewunit,String ctrlviewunit,String nextlevel,String umlayer,String nmodule,String ctrlmodule,String cascadingctrl,String parent_id,String approvalRelationDefine)throws GeneralException
	    {
	    	String a_code = userview.getUnit_id();
	    	if(viewunit!=null&& "1".equals(viewunit)){
		    	a_code=a_code!=null?a_code:"";
		    	if("UN".equals(a_code)){//xuj 2010-6-7 add 针对oracle非超级业务用户未授权任何操作单位时的情况 UN
		    		a_code="";
		    	}
	    	}
	    	if(nmodule!=null&&!"".equals(nmodule))
	    	{
	    		if(userview.isSuper_admin())
	    			a_code="UN";
	    		else
	    			a_code = userview.getUnitIdByBusi(nmodule);
	    		ctrlmodule=nmodule;
	    	}
	    	if(viewunit!=null&& "1".equals(viewunit))
	    	{
	    		ctrlviewunit="1";
	    		this.viewunit_self="1";
	    	}
	    	if("0".equals(this.viewunit_self)&& "1".equals(ctrlviewunit))
	    		this.viewunit_self="1";
	    	
	    	a_code = PubFunc.getTopOrgDept(a_code);
	    	a_code=a_code!=null?a_code:"";
	        StringBuffer xmls = new StringBuffer();
	        StringBuffer strsql = new StringBuffer();
	        
	        ResultSet rset = null;
	        Connection conn = AdminDb.getConnection();
	        Element root = new Element("TreeNode");
	        root.setAttribute("id","");
	        root.setAttribute("text","root");
	        root.setAttribute("title","organization");
	        Document myDocument = new Document(root);
	        ContentDAO dao = new ContentDAO(conn);
	        String selfUns="";
	        if("1".equals(this.showSelfNode))
	        {
	        	selfUns=getSelfUns(userview);
	        }
	        
	        String theaction=null;
	        String codevalue="";
	        String codeall="";
	        
	        
	        
	        
	        
	        String unitarr[] = a_code.split("`"); 
	        int n=0;
	        for(int i=0;i<unitarr.length;i++){
	        	String codeid = unitarr[i];
	        	if(codeid!=null&&codeid.trim().length()>2){
	        		codevalue+="'"+codeid.substring(2)+"',";
	        		n++;
	        	}else if(codeid!=null&& "UN".equalsIgnoreCase(codeid)){
	        		codeall=codeid;
	        		n++;
	        	}
	        }
	        if(n==0&&!userview.isSuper_admin())//如果没有操作单位，需按管理范围啦  dengcan
	        {
	        	String _codeid=userview.getManagePrivCode();
	    		String _codevalue=userview.getManagePrivCodeValue();
	    		if(_codevalue.length()>0)
	    			codevalue+="'"+_codevalue+"',";
	        }
	        
	        codevalue+="'aaaaa'";
	        if(nmodule!=null&&!"".equals(nmodule))
	        {
	        	codevalue="";
	        	codeall="";
	        	 String nunitarr[] = a_code.split("`"); 
	             for(int i=0;i<nunitarr.length;i++){
	             	String codeid = nunitarr[i];
	             	if(codeid!=null&&codeid.trim().length()>2){
	             		codevalue+="'"+codeid.substring(2)+"',";
	             	}else if(codeid!=null&& "UN".equalsIgnoreCase(codeid)){
	             		codeall=codeid;
	             	}
	             }
	             codevalue+="'aaaaa'";
	        }
	        StringBuffer wheresql = new StringBuffer();
	        if(codeall!=null&& "UN".equalsIgnoreCase(codeall)){
	        	 wheresql.append(" codeitemid=parentid");
	        }else{
	        	wheresql.append(" codeitemid in("+codevalue+")");
	        }
	        String ctrlsql="";
	        if((nmodule==null|| "".equals(nmodule))&&(ctrlmodule!=null)&& "1".equals(cascadingctrl)&&codeall!=null&&!"UN".equalsIgnoreCase(codeall))
	        {
	        	ctrlsql=this.getLinkChild(ctrlmodule, userview, parent_id, conn,1);
	        }
	        if((viewunit==null|| "0".equals(viewunit))&&ctrlviewunit!=null&& "1".equals(ctrlviewunit)&& "1".equals(cascadingctrl)&&codeall!=null&&!"UN".equalsIgnoreCase(codeall))
	        {
	        	ctrlsql=this.getLinkChild(ctrlmodule, userview, parent_id, conn,2);
	        }
	        try
	        {
	        	
	         if("0".equals(this.showDbName))
	         {
	        	
		          strsql.append("select codesetid,codeitemid,codeitemdesc,childid,'0' as orgtype,A0000 from organization where ");
		          if(viewunit!=null&& "0".equals(viewunit))
		          {
		        	  if(nmodule!=null&&!"".equals(nmodule))
		        	  {
		        		  strsql.append(wheresql.toString());
		        	  }
		        	  else
		        	  {
		        		  if(ctrlsql!=null&&!"".equals(ctrlsql))
		        		  {
		        			  strsql.append(ctrlsql);
		        		  }
		        		  else
		        		  {
		            	     strsql.append(params);
		        		  }
		        	  }
		          }else{
		        	  if("UN".equalsIgnoreCase(parentid)){
		        		  strsql.append(" parentid=codeitemid");
		        		  if("1".equals(nextlevel))
		        			  nextlevel="2";
		        	  }else{
		        		  strsql.append(wheresql.toString());
		        		  if(codeall!=null&& "UN".equalsIgnoreCase(codeall)){
		//        			  nextlevel="2";
		        		  }
		        	  }
		          }
		          
		          if("1".equals(this.showSelfNode)&&selfUns.length()>0)
		          {
		        	  strsql.append(" and ( ( codeitemid in ("+selfUns+") and codesetid='UN' ) OR codesetid<>'UN' ) ");
		          }
		          /**组织机构历史点控制-20091130*/
		          String bosdate=DateStyle.dateformat(new Date(),"yyyy-MM-dd");
		          strsql.append(" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date ");
		          //end.	          
		          
		          /**加载虚拟组织节点*/
		          if(bloadvorg)
		          {
		        	strsql.append(" union select codesetid,codeitemid,codeitemdesc,childid,'1',A0000 as orgtype from vorganization where ");
		        	if(viewunit!=null&& "0".equals(viewunit))
		          	  {

			        	  if(nmodule!=null&&!"".equals(nmodule))
			        	  {
			        		  strsql.append(wheresql.toString());
			        	  }
			        	  else
			        	  {
			        		  if(ctrlsql!=null&&!"".equals(ctrlsql)&&!userview.isSuper_admin()&&!"1".equals(userview.getGroupId()))
			        		  {
			        			  strsql.append(ctrlsql);
			        		  }
			        		  else
			        		  {
			            	     strsql.append(params);
			        		  }
			        	  }
		          	  }else{
		            	if("UN".equalsIgnoreCase(parentid)){
		          		  strsql.append(" parentid=codeitemid");
		          	  	}else{
		          		  strsql.append(wheresql.toString());
		          	  	}
		            }    	
		          }
			       // 不显示岗位
			      	if (!isPost) {
			      		strsql.append(" and codesetid<>'@K' ");
			      	}
		          strsql.append(" ORDER BY a0000,codeitemid,orgtype");
		          
		          rset = dao.search(strsql.toString());
		          String codeid=null;
		          /**加载组织机构树*/
		          while (rset.next())
		          {
		        	codeid=rset.getString("codesetid"); 
		            if("2".equalsIgnoreCase(this.loadtype))
		            {
		            	if("@K".equalsIgnoreCase(codeid)|| "UM".equalsIgnoreCase(codeid))
		            		continue;            	
		            }
		            if("1".equalsIgnoreCase(this.loadtype))
		            {//单位下挂岗位，也加载 wangrd 20150804
			            //	if(codeid.equalsIgnoreCase("@K"))
			            //		continue;
			        }
		        	Element child = new Element("TreeNode");
		            child.setAttribute("id", rset.getString("codeitemid")+"`"+dbpre+"`"+rset.getString("codeitemdesc")+"`"+rset.getString("codesetid"));
		            child.setAttribute("text", rset.getString("codeitemdesc"));
		            child.setAttribute("title", rset.getString("codeitemdesc"));
		            if(!(this.action==null|| "".equals(this.action)))
		            {
				        if(this.action.indexOf('?')==0){
				        	theaction=this.action+"?a_code="+rset.getString("codesetid")+rset.getString("codeitemid"); 
				        }
				        else{
				        	theaction=this.action+"&a_code="+rset.getString("codesetid")+rset.getString("codeitemid");
				        }
		            }
		            if(theaction==null|| "".equals(theaction)|| "false".equalsIgnoreCase(this.isAddAction))
		            	child.setAttribute("href", "javascript:void(0)");            	
		            else{
		            	//将url链接参数加密为一个参数encryptParam  xuj add 2014-9-2  start
		            	if(theaction!=null){
			      			int index = theaction.indexOf("&");
			      			if(index>-1){
			      				String allurl = theaction.substring(0,index);
			      				String allparam = theaction.substring(index);
			      				theaction=allurl+"&encryptParam="+PubFunc.encrypt(allparam);
			      			}
		            	}
		      			//将url链接参数加密为一个参数encryParam  xuj add 2014-9-2  end
		            	if("2".equals(nextlevel))
		            		child.setAttribute("href", theaction+"&nextlevel="+nextlevel);
		            	else
		            		child.setAttribute("href", theaction);	
		            }
		            child.setAttribute("type", "false");
		            child.setAttribute("target", this.target);
		    		String url="/system/load_tree?dbpre=" + dbpre + "&isfilter=" + this.isfilter 
		    				+ "&target="+this.target+"&flag="+this.flag+"&dbtype="+this.dbtype
		    				+"&priv="+this.priv+"&loadtype="+this.loadtype+"&chitemid="+this.chitemid
		    				+"&umlayer="+umlayer+"&orgcode="+this.orgcode+"&privtype="+this.privtype+"&approvalRelationDefine="+approvalRelationDefine;
		    		//如果不级联显示，加上限制
		    		url=url+"&params=parentId<>codeitemid and parentid%3D'" + rset.getString("codeitemid")+"'";
		    		url=url+"&id="+rset.getString("codesetid")+rset.getString("codeitemid");
		    		if("1".equals(this.showSelfNode))
		    				url=url+"&showSelfNode="+this.showSelfNode;
		    		if(this.isBloadvorg())
		    			url=url+"&lv=1";
		    		url=url+"&vg="+rset.getString("orgtype");
		    		url=url+"&cascadingctrl="+cascadingctrl+"&ctrlviewunit="+ctrlviewunit;
		    		if("1".equals(cascadingctrl))
		    			url+="&parent_id="+rset.getString("codeitemid");
		    		if(ctrlmodule!=null&&!"".equals(ctrlmodule))
		    		{
		    			url+="&ctrlmodule="+ctrlmodule;
		    		}
		    		url+="&isAddAction="+this.isAddAction;
		    		
		    		//将url链接参数加密为一个参数encryptParam  xuj add 2014-9-2  start
		    		if(url!=null){
		  			int index = url.indexOf("?");
		  			if(index>-1){
		  				String allurl = url.substring(0,index);
		  				String allparam = url.substring(index+1);
		  				url=allurl+"?encryptParam="+PubFunc.encrypt(allparam);
		  			}
		    		}
		  			//将url链接参数加密为一个参数encryParam  xuj add 2014-9-2  end
		  			
		     		//if((!rset.getString("codeitemid").equalsIgnoreCase(rset.getString("childid"))))
		        	if("0".equals(nextlevel))
		    			child.setAttribute("xml", url);
		        	else if("1".equals(nextlevel))
		        		child.setAttribute("xml", url+"&nextlevel=2");
		        	else if("2".equals(nextlevel))
		        		child.setAttribute("xml", "aaaaa");
		        	umlayer = umlayer!=null&&umlayer.trim().length()>0?umlayer:"0";
		        	try{//控制部门加载层级
		        		int uml= Integer.parseInt(umlayer);
			        	if(uml>0){
			        		if("UM".equalsIgnoreCase(codeid)){
			        			String codeitemid = rset.getString("codeitemid");
			        			this.currentlayer=1;
			        			if(this.currentlayer(codeitemid)==uml){
			        				child.setAttribute("xml", "");
			        			}
			        		}
			        	}
		        	}catch(Exception e){}
		            if("UN".equals(rset.getString("codesetid")))
		            {
		               if("0".equals(rset.getString("orgtype")))
		                child.setAttribute("icon","/images/unit.gif");
		               else
		                child.setAttribute("icon","/images/vroot.gif");
		            }
		            if("UM".equals(rset.getString("codesetid")))
		            {
		                if("0".equals(rset.getString("orgtype")))
		                	child.setAttribute("icon","/images/dept.gif");
		                else
		                	child.setAttribute("icon","/images/vdept.gif");
		            }
		            if("@K".equals(rset.getString("codesetid")))
		            {
		                if("0".equals(rset.getString("orgtype")))
		                	child.setAttribute("icon","/images/pos_l.gif");
		                else
		                	child.setAttribute("icon","/images/vpos_l.gif");
		            }
		            root.addContent(child);
		          }
		          /**加载当前机构下的人员*/
		          if("1".equals(flag)&&!this.isBfirst())
		          {
		        	  dbpre=dbpre!=null&&dbpre.trim().length()>0?dbpre:"Usr";
		        	  getEmploysApproval(userview,parentid,root,conn,dbpre,ctrlmodule);
		          }
	         }
	         else  //如果如果机构下加载人员信息 需先加载人员库
	         {
	        	 ArrayList dblist=getFilterDbList(userview,conn);// userview.getPrivDbList();
	 			 if(dblist.size()>0)
	 			 {
	 				RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN");
			        String A01 = login_vo.getString("str_value");
				    rset = dao.search("select * from dbname "); 
				    HashMap dbMap=new HashMap();
				    
			          
			        
				    while(rset.next())
				    {	//过滤出用户权限的人员库
				    	 if(A01.contains(rset.getString("pre")))
			                {
				    		 dbMap.put(rset.getString("pre").toLowerCase(),rset.getString("dbname"));
			                }
				    }
	 				for(int i=0;i<dblist.size();i++) 
	 				{
	 					String pre=(String)dblist.get(i);
	 					if(dbpre!=null && !"null".equalsIgnoreCase(dbpre) && dbpre!=null && dbpre.length()>0)
	 					{
	 						if(!dbpre.equalsIgnoreCase(pre))
	 							continue;
	 					}
	 					if(dbMap.get(pre.toLowerCase())==null)
	 						continue;
	 			        
	 			        String dbname=(String)dbMap.get(pre.toLowerCase());
	 			        	
	 			        Element child = new Element("TreeNode");
	 			        child.setAttribute("id", "@@"+pre);
	 			        child.setAttribute("text",dbname);
	 			        child.setAttribute("title",dbname);
	 			        child.setAttribute("type", "false");
	 			        child.setAttribute("target", this.target);
	 			        child.setAttribute("href", "javascript:void(0)");    
	 			            
	 			        String url="/system/load_tree?dbpre=" +pre
	 						+ "&isfilter=" + this.isfilter + "&target="+this.target
	 						+"&flag="+this.flag+"&dbtype="+this.dbtype+"&priv="
	 						+this.priv+"&loadtype="+this.loadtype+"&first=1"
	 						+"&lv="+(this.bloadvorg?"1":"0")+"&viewunit="+viewunit+"&nextlevel="+nextlevel
	 						+"&chitemid="+this.chitemid+"&orgcode="+this.orgcode+"&umlayer="+umlayer+"&nmodule="+nmodule+"&privtype="+this.privtype+"&approvalRelationDefine="+approvalRelationDefine;
	 			       if("1".equals(this.showSelfNode))
		    				url=url+"&showSelfNode="+this.showSelfNode;  
	 			        url+="&cascadingctrl="+cascadingctrl+"&ctrlviewunit="+ctrlviewunit;
	 			       if("1".equals(cascadingctrl))
	 		    			url+="&parent_id="+rset.getString("codeitemid");
	 			       if(ctrlmodule!=null&&!"".equals(ctrlmodule))
	 		    		{
	 		    			url+="&ctrlmodule="+ctrlmodule;
	 		    		}
	 			        url+="&isAddAction="+this.isAddAction;
	 			        String acodeid=userview.getManagePrivCode();
	 			        String acodevalue=userview.getManagePrivCodeValue(); 
	 			        String aa_code=acodeid+acodevalue;	
		 			 	  
		 			 	String acodeall = userview.getUnit_id();
		 			 	acodeall = PubFunc.getTopOrgDept(acodeall);
		 			 	String aunitarr[] = acodeall.split("`"); 
		 			 	if(viewunit!=null&& "1".equals(viewunit)){
		 			 			for(int j=0;j<aunitarr.length;j++){
		 			 				if(aunitarr[j]!=null&& "UN".equalsIgnoreCase(aunitarr[j])){
		 			 					aa_code="UN";
		 			 					break;
		 			 				}else{
		 			 					aa_code="";
		 			 				}
		 			 			}
		 			 	 }
	 			         if(nmodule!=null&&!"".equals(nmodule))
	 			         {
	 			        	 if(userview.isSuper_admin())
	 			        		 aa_code="UN";
	 			        	 else{
		 			        	String nacodeall = userview.getUnitIdByBusi(nmodule);
		 		 			 	nacodeall = PubFunc.getTopOrgDept(nacodeall);
		 		 			 	String naunitarr[] = nacodeall.split("`"); 
		 		 			 	for(int j=0;j<naunitarr.length;j++){
		 		 			 		if(naunitarr[j]!=null&& "UN".equalsIgnoreCase(naunitarr[j])){
		 		 			 			aa_code="UN";
		 		 			 			break;
		 		 			 		}else{
		 		 			 			aa_code="";
		 		 			 		}
		 		 			 	}
				        	 }
	 			         }
	 			           
					 	if("0".equals(this.priv))//不加权限过滤
					 	{
					 				url=url+"&params=codeitemid%3Dparentid&id=UN";
					 				aa_code="";
					 	}
					 	else
					 	{
					 				if(!("UN".equals(aa_code)))
					 				{
					 					if("1".equals(viewunit))
					 						url=url+"&params=codeitemid%3D'"+acodevalue+"'&id="+acodeall;
					 					else
					 						url=url+"&params=codeitemid%3D'"+acodevalue+"'&id="+acodeid+acodevalue;
					 					
					 				}
					 				else
					 				{
					 					url=url+"&params=codeitemid%3Dparentid&id=UN";
					 				}
					 	}
					 	
					 	//将url链接参数加密为一个参数encryptParam  xuj add 2014-9-2  start
					 	if(url!=null){
			  			int index = url.indexOf("?");
			  			if(index>-1){
			  				String allurl = url.substring(0,index);
			  				String allparam = url.substring(index+1);
			  				url=allurl+"?encryptParam="+PubFunc.encrypt(allparam);
			  			}
					 	}
			  			//将url链接参数加密为一个参数encryParam  xuj add 2014-9-2  end
			  			
				 		child.setAttribute("xml", url);
				 		child.setAttribute("icon","/images/open.png");//人员库改用文件夹图标2011-12-06 xieguiquan
				 		root.addContent(child);
	 				}
	 			 }
	        	 
	         }
	        
	          XMLOutputter outputter = new XMLOutputter();
	          Format format=Format.getPrettyFormat();
	          format.setEncoding("UTF-8");
	          outputter.setFormat(format);
	          xmls.append(outputter.outputString(myDocument));
	          //System.out.println("SQL=" +xmls.toString());
	        }
	        catch (Exception ee)
	        {
	          ee.printStackTrace();
	          //GeneralExceptionHandler.Handle(ee);
	        }
	        finally
	        {
	          try
	          {
	            if (rset != null)
	            {
	              rset.close();
	            }
	            if (conn != null)
	            {
	              conn.close();
	            }
	          }
	          catch (SQLException ee)
	          {
	            ee.printStackTrace();
	          }
	          
	      }
	      return xmls.toString();        
	    }
	public String getModel_string() {
		return model_string;
	}

	public void setModel_string(String model_string) {
		this.model_string = model_string;
	}

	public String getPrivtype() {
		return privtype;
	}

	public void setPrivtype(String privtype) {
		this.privtype = privtype;
	}

	public String getDbvalue() {
		return dbvalue;
	}

	public void setDbvalue(String dbvalue) {
		this.dbvalue = dbvalue;
	}
}
