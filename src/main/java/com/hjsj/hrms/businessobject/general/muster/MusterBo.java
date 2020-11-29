/**
 * 
 */
package com.hjsj.hrms.businessobject.general.muster;

import com.hjsj.hrms.businessobject.general.inform.search.SearchInformBo;
import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.muster.mustermanage.businessobject.MusterManageService;
import com.hjsj.hrms.module.muster.mustermanage.businessobject.impl.MusterManageServiceImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.UsrResultTable;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * <p>Title:MusterBo</p>
 * <p>Description:对花名册的一些操作</p>
 * <p>Company:hjsj</p>
 * <p>create time:2005-12-15:12:00:55</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class MusterBo {
	
    private transient Category cat = Category.getInstance(this.getClass());
    /**数据库服务器标志*/
    private static int dbflag=Constant.MSSQL;
    
    private UserView userview=null;
    private String orgtype="";
    static
    {
       dbflag=Sql_switcher.searchDbServer();

    }	
	private Connection conn=null;
	/**花名册指标*/
	private ArrayList fieldlist=new ArrayList();
	/**排序指标*/
	private ArrayList sortlist=new ArrayList();
	private String chcksort = "";
	private int code_length=0;
	private int codedesc_length=0;
	private int org_length=0;
	private int orgdesc_length=0;	
	
	private boolean isExistField = false;//判断所有筛选条件指标在结果表里是否都存在
	private String wherestr = "";//筛选人
	private String wheresql = "";//筛选子集记录
	private String repeat_mainset="";//包含历史记录是否包含主集 changxy 20161010
	
	public ArrayList getFieldlist() {
		return fieldlist;
	}
	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}
	public MusterBo(Connection conn) {
		this.conn=conn;
		initCodeLength();
	}
	public MusterBo(Connection conn,UserView userview) {
		this.userview=userview;
		this.conn=conn;
		initCodeLength();
	}	
	/**
	 * 所有的构造函数，必须调用该方法，初始化一些数据
	 */
	public void initCodeLength()
	{
		RowSet rset=null;
		try
		{
			ContentDAO dao  = new ContentDAO(this.conn);
			Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.conn);
			String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);//显示部门层数
			uplevel=uplevel!=null&&uplevel.trim().length()>0?uplevel:"0";
			int nlevel=Integer.parseInt(uplevel);//如果设置了部门显示几级参数，e0122字段长度有不同
			if(Sql_switcher.searchDbServer()!=Constant.ORACEL)
			{
		    	rset = dao.search("select * from codeitem where 1=2");
		    	ResultSetMetaData rsmd = rset.getMetaData();
	  	    	for(int i=1;i<=rsmd.getColumnCount();i++)
	    		{
	    			String cname = rsmd.getColumnName(i);
	    			if("codeitemid".equalsIgnoreCase(cname)) {
                        code_length=rsmd.getColumnDisplaySize(i);
                    } else if("codeitemdesc".equalsIgnoreCase(cname)) {
                        codedesc_length=rsmd.getColumnDisplaySize(i);
                    }
					
	    		}
	    		rset=dao.search("select * from organization where 1=2 ");
	    		rsmd = rset.getMetaData();
	       		for(int i=1;i<=rsmd.getColumnCount();i++)
		    	{
	     			String cname = rsmd.getColumnName(i);
	    			if("codeitemid".equalsIgnoreCase(cname)) {
                        org_length=rsmd.getColumnDisplaySize(i);
                    } else if("codeitemdesc".equalsIgnoreCase(cname)) {
                        orgdesc_length=rsmd.getColumnDisplaySize(i);
                    }
	    		}
			}else
			{
                rset = dao.search(" SELECT column_name,data_length FROM User_Tab_Columns WHERE table_name='CODEITEM' and (column_name='CODEITEMID' or column_name='CODEITEMDESC')");
	    		
		    	while(rset.next())
		    	{
		     		String cname = rset.getString("column_name");
		    		if("codeitemid".equalsIgnoreCase(cname)) {
                        code_length=rset.getInt("data_length");
                    }
		    		if("codeitemdesc".equalsIgnoreCase(cname)) {
                        codedesc_length=rset.getInt("data_length");
                    }
					
		    	}
		    	rset=dao.search(" SELECT column_name,data_length FROM User_Tab_Columns WHERE table_name='ORGANIZATION' and (column_name='CODEITEMID' or column_name='CODEITEMDESC')");

		    	while(rset.next())
		    	{
		     		String cname = rset.getString("column_name");
		    		if("codeitemid".equalsIgnoreCase(cname)) {
                        org_length=rset.getInt("data_length");
                    }
		    		if("codeitemdesc".equalsIgnoreCase(cname)) {
                        orgdesc_length=rset.getInt("data_length");
                    }
					
		    	}
			}
			if(nlevel>0) {
                orgdesc_length=300;
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			if(rset!=null)
			{
				try
				{
					rset.close();
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 取得花名册包含历史记录是否包含主集信息 20161010
	 * */
	public String getHistoryBytable(String tableid){
		ContentDAO dao=new ContentDAO(this.conn);
			MusterXMLStyleBo xmltylebo = new MusterXMLStyleBo(this.conn,tableid);
			String flag=xmltylebo.getParamValue(1,"");
		return flag;
	}
	
	/**
	 * 取得花名册是否包含历史记录
	 * @param setName
	 * @return
	 */
	public String getHistoryById(String setName)
	{
		String history="0";
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet recset=dao.search("select expr from lbase where tabid="+setName.substring(1,setName.indexOf("_")));		
			if(recset.next())
			{
				if(recset.getString(1)!=null&& "1".equals(recset.getString(1))) {
                    history="1";
                }
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return history;
	}
	
	
	public int getMaxRecidx(String tableName)throws GeneralException
	{
		int recidx=0;
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;		
		try
		{
			recset=dao.search("select max(recidx) from "+tableName);
			if(recset.next()) {
                recidx=recset.getInt(1);
            }
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		    throw GeneralExceptionHandler.Handle(ex);
		}
		return recidx;
	}
	
	
	/**
	 * 动态创建表的主键
	 * @param tableName
	 * @throws GeneralException
	 */
	public void  autoCreatePrimaryKey(String tableName,String muster_id,String infor_kind)throws GeneralException
	{
		try
		{
			
		//	RecordVo vo=new RecordVo(tableName.toLowerCase());
		//	System.out.println(vo.getKeylist().size());
		//	if(vo.getKeylist().size()==0)
			{
			//	DbWizard dbWizard=new DbWizard(this.conn); 
				
			//	Table table0=new Table(tableName);
			//	Field temp=new Field("recidx",ResourceFactory.getProperty("recidx.label"));
			//	temp.setNullable(false);
			//	temp.setKeyable(true);
			//	temp.setDatatype(DataType.INT);
			//    temp.setSortable(true);	
			//    table0.addField(temp);
			//    dbWizard.dropColumns(table0);
			 
		//	    dbWizard.addColumns(table0);
		//	    updateMusterRecidx(tableName);
			//    DBMetaModel dbmodel=new DBMetaModel(this.conn);
			//	dbmodel.reloadTableModel(tableName);	
				
			//	ArrayList fieldlist=getMusterFields(muster_id,infor_kind);
			//	Table table=new Table(tableName);
			//	for(int i=0;i<fieldlist.size();i++)
			//		table.addField((Field)fieldlist.get(i));
				
				
				
			//	dbWizard.addPrimaryKey(table0);
			}
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		    throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	
	/**
	 * 取得花名同列表
	 * 列表对象保存RecordVo类对象
	 * @return
	 */
	public ArrayList getMusterTypeList()throws GeneralException
	{
		ArrayList list=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;		
		try
		{
			String temp=this.userview.getResourceString(4);
			HashMap map = this.getHaveChildLstyle(dao);
			StringBuffer sql=new StringBuffer();
			sql.append("select styleid,styledesc from lstyle");
			recset=dao.search(sql.toString());			
			while(recset.next())
			{
				RecordVo vo=new RecordVo("lstyle");
				if(!this.userview.isSuper_admin()&&!"1".equals(this.userview.getGroupId()))
				{
					if(map.get(recset.getString("styleid"))==null|| "2".equals((String)map.get(recset.getString("styleid")))) {
                        continue;
                    }
				}
				vo.setInt("styleid",Integer.parseInt(recset.getString("styleid")));
				vo.setString("styledesc",recset.getString("styledesc"));
				list.add(vo);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		    throw GeneralExceptionHandler.Handle(ex);
		}
		finally
		{
			
		}
		return list;
	}
	public HashMap getHaveChildLstyle(ContentDAO dao)
	{
		HashMap map = new HashMap();
		try
		{
			StringBuffer sql = new StringBuffer("");
			sql.append("select tabid,moduleflag from lname");
			RowSet rs = dao.search(sql.toString());
			while(rs.next())
			{
				if(rs.getString("moduleflag")==null|| "".equals(rs.getString("moduleflag"))) {
                    continue;
                }
				String styleid=rs.getString("moduleflag").substring(1, 3);
		    	if(this.userview.isHaveResource(IResourceConstant.MUSTER, rs.getString("tabid")))
		    	{
		    		map.put(styleid, "1");
		    	}
		    	else
		    	{
		    		if(map.get(styleid)==null||!"1".equals((String)map.get(styleid)))
		    		{
		    			map.put(styleid,"2");
		    		}
		    	}
				
			}
			rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 取得花名同列表
	 * 
	 * @return
	 */
	public ArrayList getMusterTypeList(String flaga,String checkflag)throws GeneralException
	{
		ArrayList list=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;		
		try
		{
			String temp=this.userview.getResourceString(4);
			StringBuffer sql=new StringBuffer();
			sql.append("select styleid,styledesc from lstyle");
			if(!(userview.isAdmin()&& "1".equals(userview.getGroupId()))){
				String wherestr = whereStr((temp==null|| "".equals(temp))?"-1":temp,flaga);
				wherestr=wherestr!=null&&wherestr.trim().length()>0?wherestr:"";
				sql.append(" where UPPER(styleid) in("+wherestr.toUpperCase()+")");
				if("1".equals(checkflag)){
					if(Sql_switcher.searchDbServer()==Constant.ORACEL){
						sql.append(" or styleid not in (select substr(moduleflag,2,2) from lname where flag='"+flaga+"')");
					}else{
						sql.append(" or styleid not in (select substring(moduleflag,2,2) from lname where flag='"+flaga+"')");
					}
				}
			}else{
				if("0".equals(checkflag)){
					if(Sql_switcher.searchDbServer()==Constant.ORACEL){
						sql.append(" where styleid in (select substr(moduleflag,2,2) from lname where flag='"+flaga+"') ");
					}else{
						sql.append(" where styleid in (select substring(moduleflag,2,2) from lname where flag='"+flaga+"')");
					}
				}
			}
			sql.append(" order by styleid");
			recset=dao.search(sql.toString());			
			while(recset.next())
			{
				RecordVo vo=new RecordVo("lstyle");
				vo.setString("styleid",recset.getString("styleid"));
				vo.setString("styledesc",recset.getString("styledesc"));
				list.add(vo);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		    throw GeneralExceptionHandler.Handle(ex);
		}
		finally
		{
			
		}
		return list;
	}
	private String whereStr(String temp,String flaga){
		StringBuffer wherestr = new StringBuffer();
		StringBuffer sqlstr = new StringBuffer("SELECT moduleflag from lname where flag='");
		sqlstr.append(flaga);
		sqlstr.append("'"); 
		if(!(userview.isAdmin()&& "1".equals(userview.getGroupId()))){
			sqlstr.append(" and tabid in (");   
			sqlstr.append(temp); 
			sqlstr.append(")");
		}
		sqlstr.append(" group by moduleflag");
		List rs=ExecuteSQL.executeMyQuery(sqlstr.toString());
		HashMap map=new HashMap();
		if(!rs.isEmpty()){
			for(int i=0;i<rs.size();i++){
				DynaBean rec=(DynaBean)rs.get(i);
				if(rec.get("moduleflag")!=null) {
                    map.put(rec.get("moduleflag").toString().substring(1,3), "1");
                }
			}
		}
		Iterator   it   =(Iterator) map.entrySet().iterator();  
		while(it.hasNext())
		{   
		  Map.Entry   entry=(Map.Entry)it.next();   
		  Object   key =entry.getKey()   ;   
		  wherestr.append(",'"+key.toString()+"'");
		}
		String str="";
		if(wherestr.length()>0) {
            str=wherestr.substring(1);
        } else {
            str="'-1'";
        }
		return str;
	}
	public String styleId(){
		String styleid = "";
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;		
		try{
			StringBuffer sql=new StringBuffer();
			sql.append("select max(styleid) as styleid from lstyle");
			recset=dao.search(sql.toString());			
			if(recset.next()){
				styleid=recset.getString("styleid");
				
			}
			styleid=this.getID(styleid);
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		return styleid;
	}
	public String getID(String aid)
	{
		if(aid==null|| "".equals(aid)) {
            aid="1";
        }
		if(aid.length()<2) {
            aid="0"+aid;
        }
		aid=aid.toUpperCase();
		String[] chartset = new String[]{"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
		String a=aid.substring(0,1);
		String b=aid.substring(1);
		int a_index=0;
		int b_index=0;
		for(int i=0;i<chartset.length;i++)
		{
			if(a.equals(chartset[i])) {
                a_index=i;
            }
			if(b.equals(chartset[i])) {
                b_index=i;
            }
			if(a_index!=0&&b_index!=0) {
                break;
            }
		}
		if("0123456789".indexOf(a)!=-1)
		{
			if("9".equals(b))
			{
				a=chartset[a_index+1];
			    b="0";
			}
			else 
			{
				b=chartset[b_index+1];
			}
		}
		else
		{	
	    	if("Z".equals(b))
	    	{
	    		b=chartset[0];
	    		a=chartset[a_index+1];
	    	}
    		else 
	    	{
	    		b=chartset[b_index==0?1:b_index+1];
	       	}
		}
		return a+b;
	}
	
	/**
	 * 重新设置列标题
	 * @param tabid 
	 * @param field_name for examples AXXXX_1 ,内容大写
	 * @param title
	 * @throws GeneralException
	 */
	public void reSetColumnTitle(String tabid,String field_name,String title)throws GeneralException
	{
		ContentDAO dao=new ContentDAO(this.conn);
		
		try
		{
			String temp=field_name.substring(0,5);
			FieldItem fielditem=DataDictionary.getFieldItem(temp);
			if(fielditem!=null) {
                temp=fielditem.getFieldsetid()+"."+fielditem.getItemid().toUpperCase();
            } else {
                temp = field_name;
            }
			
			int size=0;
			RowSet rowSet=dao.search("select width from lbase where tabid="+tabid+" and field_name='"+temp+"'");
			if(rowSet.next()) {
                size=rowSet.getInt("width");
            }
			StringBuffer strsql=new StringBuffer();
			strsql.append("update lbase set colhz='");
			if(title.length()<=size) {
                strsql.append(title);
            } else {
                strsql.append(title.substring(0,size));
            }
			strsql.append("' where tabid=");
			strsql.append(tabid);
			strsql.append(" and field_name='");		
			strsql.append(temp);		
			strsql.append("'");
		
			dao.update(strsql.toString());
		}
		catch(Exception ex)
		{
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	/**
	 * 得到排序指标
	 * @param sortarr
	 * @return  for examples A01.A0405,A01.A0107,
	 */
	private String getSortFields(String[] sortarr,String infor)
	{
		if(sortarr==null||sortarr.length==0) {
            return "";
        }
		StringBuffer sorts=new StringBuffer();
		
		for(int i=0;i<sortarr.length;i++)
		{
		  FieldItem fielditem=DataDictionary.getFieldItem(sortarr[i].toUpperCase());
		  if("B0110".equalsIgnoreCase(fielditem.getItemid())&& "2".equals(infor)) {
              sorts.append("B01");
          } else if("E01A1".equalsIgnoreCase(fielditem.getItemid())&& "3".equals(infor)) {
              sorts.append("K01");
          } else {
              sorts.append(fielditem.getFieldsetid());
          }
		  sorts.append(".");
		  sorts.append(fielditem.getItemid().toUpperCase());
		  /**default=0升序,暂时如此吧*/
		  sorts.append("0");
		  sorts.append(",");
		}
//		int nlen=0;
//		if((nlen=sorts.length())>0)
//			sorts.setLength(nlen-1);
		return sorts.toString();
	}
	private String getSortFields(String sortitem,String infor)
	{
		if(sortitem==null||sortitem.trim().length()==0) {
            return "";
        }
		String sortarr[] = sortitem.split("`");

		StringBuffer sorts=new StringBuffer();

		for(int i=0;i<sortarr.length;i++)
		{
			if(sortarr[i]==null) {
                continue;
            }
			String arr[] = sortarr[i].split(":");
			if(arr==null) {
                continue;
            }
			if(arr.length!=3) {
                continue;
            }
			
			FieldItem fielditem=DataDictionary.getFieldItem(arr[0].toUpperCase());
			if("B0110".equalsIgnoreCase(fielditem.getItemid())&& "2".equals(infor)) {
                sorts.append("B01");
            } else if("E01A1".equalsIgnoreCase(fielditem.getItemid())&& "3".equals(infor)) {
                sorts.append("K01");
            } else {
                sorts.append(fielditem.getFieldsetid());
            }
			sorts.append(".");
			sorts.append(fielditem.getItemid().toUpperCase());
			/**default=0升序,暂时如此吧*/
			sorts.append(arr[2]);
			sorts.append(",");
		}
		return sorts.toString();
	}
	
	/**
	 * 重建花名册结构,增减花名册指标
	 * @param name   for examples m11_su_usr(B,K)
	 * @param fieldlist 新结构字段列表 
	 */
	public void addDelStrutField(String setname,ArrayList fieldlist,String infor)throws GeneralException
	{
		
//		StringTokenizer st=new StringTokenizer(setname,"_");
//	    String tabid=st.nextToken().substring(1);
//	    String user_id=st.nextToken();
		/*防止出现 用户名中有 "_" */
	    String[] st = setname.trim().split("_");
	    String tabid= "";
	    String dbpre= "";
	    String user_id= "";
	    if(st!=null && st.length>=3){
	    	tabid=st[0].substring(1);
	    	dbpre=st[st.length-1];	
	    	user_id=setname.trim().substring(setname.indexOf("_")+1,
	    			setname.lastIndexOf("_"));
	    }
	    if("B".equalsIgnoreCase(dbpre))
	    {
	    	dbpre="";
	    	//infor="2";
	    }
	    else if("K".equalsIgnoreCase(dbpre))
	    {
	    	dbpre="";	    	
	    	//infor="3";
	    }
	   // else
	    	//infor="1";
	    /**花名册指标列表*/
	    StringBuffer strSrc=new StringBuffer();
	    for(int i=0;i<fieldlist.size();i++)
	    {
	    	String temp=(String)fieldlist.get(i);
	    	
	    	strSrc.append(temp.toUpperCase());
	    	strSrc.append(",");
	    }
	    cat.debug("new fields="+strSrc.toString());
	    StringBuffer strDest=new StringBuffer();
	    ArrayList destlist=getMusterFields(tabid,infor);
	    for(int i=0;i<destlist.size();i++)
	    {
	    	Field field=(Field)destlist.get(i);
	    	strDest.append(field.getName().toUpperCase());
	    	strDest.append(",");
	    }//for loop end.
	    cat.debug("old fields="+strDest.toString());
	    String temp=null;
	    /**字段增加*/
	    ArrayList addlist=new ArrayList();

	    try
	    {
		    for(int i=0;i<fieldlist.size();i++)
		    {
		    	temp=(String)fieldlist.get(i);
		    	temp=temp.toUpperCase();
		    	
		    	if(strDest.indexOf(temp+",")!=-1)
		    	{
		    		
		    		continue;
		    	}
		    	addlist.add(temp);
		    }
	    }
	    catch(Exception e)
	    {
	    	e.printStackTrace();
	    }

	    if(addlist.size()>0)
	    {
	    	addField(Integer.parseInt(tabid),setname,addlist.toArray(),dbpre,infor);
	    }
	    /**字段删除*/
	    addlist.clear();
	    for(int i=0;i<destlist.size();i++)
	    {
	    	Field field=(Field)destlist.get(i);
	    	temp=field.getName();
	    	if(strSrc.toString().toUpperCase().indexOf(temp.toUpperCase()+",")!=-1) {
                continue;
            }
	    	if("recidx".equalsIgnoreCase(temp)) {
                continue;
            }
	    	if("1".equals(infor)&& "A0100".equalsIgnoreCase(temp)) {
                continue;
            }
	    	if("2".equals(infor)&& "B0110_CODE".equalsIgnoreCase(temp)) {
                continue;
            }
	    	if("3".equals(infor)&& "E01A1_CODE".equalsIgnoreCase(temp)) {
                continue;
            }
	    	addlist.add(field);
	    }
	    if(addlist.size()>0) {
            dropField(Integer.parseInt(tabid),setname,addlist);
        }
		/**重新加载数据模型*/

		DBMetaModel dbmodel=new DBMetaModel(this.conn);
		dbmodel.reloadTableModel(setname);	  
	    /**调整顺序*/
	    //HashMap baseidMap=getBaseIdMap(tabid);
	    ContentDAO dao=new ContentDAO(this.conn);	
	    try
	    {
	    	int maxbaseid=getMaxBaseIdByTab(Integer.parseInt(tabid));
	    	StringBuffer strsql=new StringBuffer();
	    	strsql.append("update lbase set baseid=baseid+");
	    	strsql.append(maxbaseid+1);
	    	strsql.append(" where tabid=");
	    	strsql.append(tabid);
	    	dao.update(strsql.toString());
		    for(int i=0;i<fieldlist.size();i++)
		    {
		    	temp = (String) fieldlist.get(i);
				temp = temp.toUpperCase();
				if (temp.indexOf("_CODE") != -1) {
                    temp = temp.substring(0, 5);
                }
				//String baseid = (String) baseidMap.get(temp);	
				strsql.setLength(0);
				strsql.append("update lbase set baseid=");
				strsql.append(i);
				strsql.append(" where tabid=");
				strsql.append(tabid);
				strsql.append(" and (field_name like '%.");
				strsql.append(temp);
				strsql.append("' or field_name like '%."+temp.toLowerCase()+"')");
				dao.update(strsql.toString());
		    }
	    }
	    catch(Exception e)
	    {
	    	e.printStackTrace();
	    }
	}
	/**
	 * 重建花名册结构,增减花名册指标
	 * @param name   for examples m11_su_usr(B,K)
	 * @param fieldlist 新结构字段列表 
	 */
	public void addDelTableField(String setname,ArrayList fieldlist)throws GeneralException
	{
		String infor=null;
		StringTokenizer st=new StringTokenizer(setname,"_");
	    String tabid=st.nextToken().substring(1);
	    String user_id=st.nextToken();
	    String dbpre=st.nextToken();	
	    if("B".equalsIgnoreCase(dbpre))
	    {
	    	dbpre="";
	    	infor="2";
	    }
	    else if("K".equalsIgnoreCase(dbpre))
	    {
	    	dbpre="";	    	
	    	infor="3";
	    }
	    else {
            infor="1";
        }
	    /**花名册指标列表*/
	    StringBuffer strSrc=new StringBuffer();
	    DbWizard dbwizard=new DbWizard(this.conn);
	    for(int i=0;i<fieldlist.size();i++)
	    {
	    	String temp=(String)fieldlist.get(i);

	    	strSrc.append(temp.toUpperCase());
	    	strSrc.append(",");
	    }
	    cat.debug("new fields="+strSrc.toString());
	    StringBuffer strDest=new StringBuffer();
	    ArrayList destlist=getMusterFields(tabid,infor);
	    for(int i=0;i<destlist.size();i++)
	    {
	    	Field field=(Field)destlist.get(i);
	    	if("recidx".equalsIgnoreCase(field.getName())){
	    		strDest.append(field.getName().toUpperCase());
		    	strDest.append(",");
	    	}else if("1".equals(infor)&& "A0100".equalsIgnoreCase(field.getName())){
	    		strDest.append(field.getName().toUpperCase());
		    	strDest.append(",");
	    	}else if("2".equals(infor)&& "B0110_CODE".equalsIgnoreCase(field.getName())){
	    		strDest.append(field.getName().toUpperCase());
		    	strDest.append(",");
	    	}else if("3".equals(infor)&& "E01A1_CODE".equalsIgnoreCase(field.getName())){
	    		strDest.append(field.getName().toUpperCase());
		    	strDest.append(",");
	    	}else{
	    		if(isExistField(setname, field.getName())){
	    			strDest.append(field.getName().toUpperCase());
	    	    	strDest.append(",");
	    		}
	    	}
	    }//for loop end.
	    cat.debug("old fields="+strDest.toString());
	    String temp=null;
	    /**字段增加*/
	    ArrayList addlist=new ArrayList();

	    try
	    {
		    for(int i=0;i<fieldlist.size();i++)
		    {
		    	temp=(String)fieldlist.get(i);
		    	temp=temp.toUpperCase();
		    	
		    	if(strDest.indexOf(temp+",")!=-1)
		    	{
		    		
		    		continue;
		    	}
		    	addlist.add(temp);
		    }
	    }
	    catch(Exception e)
	    {
	    	e.printStackTrace();
	    }

	    if(addlist.size()>0)
	    {
	    	addTableField(setname,addlist.toArray(),dbpre,infor);
	    }
	    /**字段删除*/
	    addlist.clear();
	    for(int i=0;i<destlist.size();i++)
	    {
	    	Field field=(Field)destlist.get(i);
	    	temp=field.getName();
	    	if(strSrc.toString().toUpperCase().indexOf(temp.toUpperCase()+",")!=-1) {
                continue;
            }
	    	if("recidx".equalsIgnoreCase(temp)) {
                continue;
            }
	    	if("1".equals(infor)&& "A0100".equalsIgnoreCase(temp)) {
                continue;
            }
	    	if("2".equals(infor)&& "B0110_CODE".equalsIgnoreCase(temp)) {
                continue;
            }
	    	if("3".equals(infor)&& "E01A1_CODE".equalsIgnoreCase(temp)) {
                continue;
            }
	    	addlist.add(field);
	    }
	    if(addlist.size()>0) {
            dropTableField(setname,addlist);
        }
		/**重新加载数据模型*/

		DBMetaModel dbmodel=new DBMetaModel(this.conn);
		dbmodel.reloadTableModel(setname);	  
	    /**调整顺序*/
	}
	
	public HashMap getBaseIdMap(String tabid)
	{
		HashMap map=new HashMap();
		ContentDAO dao=new ContentDAO(this.conn);	
		RowSet recset=null;
		try
		{
			recset=dao.search("select * from lbase where tabid="+tabid);
			while(recset.next())
			{
				map.put(recset.getString("field_name").substring(recset.getString("field_name").indexOf(".")+1),recset.getString("baseid"));
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	
	
	
	
	
	
	/**
	 * 取得当前花名册lbase最大的序号
	 * @param tabid
	 * @return
	 */
	private int getMaxBaseIdByTab(int tabid)
	{
		StringBuffer strsql=new StringBuffer();
		strsql.append("select max(baseid) as nmax from lbase where tabid=");
		strsql.append(tabid);
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rset=null;
		int nmax=0;
		try
		{
			rset=dao.search(strsql.toString());
			if(rset.next()) {
                nmax=rset.getInt("nmax")+1;
            }
		
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
//			try
//			{
//				if(rset!=null)
//					rset.close();
//			}
//			catch(Exception ee)
//			{
//				ee.printStackTrace();
//			}
		}
		return nmax;			
	}
	
	/**
	 * 返回花名册数据表查询sql，用于dataset标签。
	 * 
	 * @Title: getDataSetSelectSql   
	 * @Description:    
	 * @param dbpre
	 * @return
	 */
	public String getDataSetSelectSql(String infor, String dbpre, 
	        String thetabid) {
	    String sql="";
	    String flds=getSelectFlds();
	    String tabname=getTableName(infor,dbpre,thetabid,userview.getUserName().trim().replaceAll(" ", ""));
        sql="select "+flds+" from "+tabname+" order by recidx";	    
	    return sql;
	}

    public String getDataSetAllDBSelectSql(String infor, ArrayList dblist, 
            String thetabid) {
        String sql="";
        String union="";
        if (fieldlist.size()==0) {
            fieldlist=getMusterFields(thetabid,infor);
        }
        String flds=getSelectFlds();
        DbWizard db = new DbWizard(conn);
        for(int i=0;i<dblist.size();i++){
            CommonData vo=(CommonData)dblist.get(i);
            String dbpre=vo.getDataValue();
            String tabname=getTableName(infor,dbpre,thetabid,userview.getUserName().trim().replaceAll(" ", ""));
            if (db.isExistTable(tabname, false)) {
                if(union.length()>0) {
                    union+=" union all ";
                }
                union+="select "+i +" as dbid,"+ flds+" from "+tabname;
            }
        }

        if(Sql_switcher.searchDbServer()==Constant.ORACEL){
            String flds2=flds.replaceAll("recidx", "RowNum as recidx");
            sql="select "+flds2+" from (select dbid,"+flds+" from ("+union+") a order by dbid, recidx) b order by dbid, recidx";
        }
        else{
            sql="select "+flds+" from ("+union+") a order by dbid, recidx";
        }
        return sql;
    }
    
    private String getSelectFlds() {
        ArrayList fieldlist = getFieldlist();
        StringBuffer column=new StringBuffer();
        for(int i=0;i<fieldlist.size();i++){
            Field field = (Field)fieldlist.get(i);
            FieldItem fielditem = DataDictionary.getFieldItem(field.getName());
            if(fielditem!=null&& "D".equalsIgnoreCase(fielditem.getItemtype()))
            {
                if(fielditem.getItemlength()==10) {
                    column.append(","+field.getName());
                } else if(fielditem.getItemlength()==7)
                {
                    if(Sql_switcher.searchDbServer()==Constant.ORACEL)
                    {
                        column.append(","+Sql_switcher.substr(field.getName(), "0", "7")+" as "+field.getName());
                    }else {
                        column.append(","+Sql_switcher.substr(field.getName(), "0", "8")+" as "+field.getName());
                    }
                }else if(fielditem.getItemlength()==4){
                    if(Sql_switcher.searchDbServer()==Constant.ORACEL)
                    {
                        column.append(","+Sql_switcher.substr(field.getName(), "0", "4")+" as "+field.getName());
                    }else {
                        column.append(","+Sql_switcher.substr(field.getName(), "0", "5")+" as "+field.getName());
                    }
                }else {
                    column.append(","+field.getName());
                }
            }
            else
            {
                column.append(","+field.getName());
            }
        }
        return column.toString().substring(1);
    }
    
	/**
	 * 
	 * @Title: calcPersonCount   
	 * @Description:    
	 * @return
	 */
	public int calcPersonCount(String infor, String dbpre, String thetabid) {
	    ContentDAO dao=new ContentDAO(conn);
	    String tabname=getTableName(infor,dbpre,thetabid,userview.getUserName().trim().replaceAll(" ", ""));
        int i=0;
	    try{
	        DbWizard dbwizard = new DbWizard(this.conn);
	        if(dbwizard.isExistTable(tabname)) {
                RowSet rs = dao.search("select Count(Distinct A0100) as cnt from "+tabname);
                if(rs.next()){
                    i=rs.getInt("cnt");
                }
	        }
	    }
        catch(Exception e) {
            e.printStackTrace();
        }
        return i;
	}
	
    public int calcAllDBPersonCount(String infor, ArrayList dblist, String thetabid) {
        int cnt=0;
        
        for(int i=0;i<dblist.size();i++){
            CommonData vo=(CommonData)dblist.get(i);
            String dbpre=vo.getDataValue();
            if("ALL".equals(dbpre)) {
                continue;
            }
            cnt+=calcPersonCount(infor, dbpre, thetabid);
        }

        return cnt;
    }
    
    /**
     * 返回当前用户授权的全部人员库
     * @return
     */
    public ArrayList getUserAllDBList() {
        ArrayList list=new ArrayList();
        try{
            ArrayList dblist=this.userview.getPrivDbList();
            DbNameBo dbvo=new DbNameBo(this.conn);
            dblist=dbvo.getDbNameVoList(dblist);
            for(int i=0;i<dblist.size();i++){
                CommonData vo=new CommonData();
                RecordVo dbname=(RecordVo)dblist.get(i);
                vo.setDataName(dbname.getString("dbname"));
                vo.setDataValue(dbname.getString("pre"));
                list.add(vo);
            }
        }
        catch(Exception e) {
            //
        }
        return list;
    }
	
	/**
	 * 是否汉口银行专版
	 * @Title: isHkyh   
	 * @Description:    
	 * @return
	 */
	public static boolean isHkyh() {
	    return SystemConfig.getPropertyValue("clientName")!=null&&"hkyh".equals(SystemConfig.getPropertyValue("clientName").trim());
	}
	
	/**
	 * 设置花名册字段长度和类型
	 * @param item
	 * @param field
	 */
    private Field getFieldVo(FieldItem item)
    {
    	Field field=item.cloneField();
		if("A".equals(item.getItemtype()))
		{
			/**字段为代码型,长度定为50*/
			field.setDatatype(DataType.STRING);			
			if(item.getCodesetid()==null|| "".equals(item.getCodesetid())|| "0".equals(item.getCodesetid())) {
                field.setLength(item.getItemlength());
            } else
			{
				if("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid())) {
                    field.setLength(this.orgdesc_length);
                } else {
                    field.setLength(this.codedesc_length);
                }
			}
		}
		else if("M".equals(item.getItemtype()))
		{
			field.setDatatype(DataType.CLOB);
			field.setAlign("left");					
		}
		else if("N".equals(item.getItemtype()))
		{
			field.setDatatype(DataType.STRING);			
			field.setLength(12);
			
		}	
		else if("D".equals(item.getItemtype()))
		{
			field.setDatatype(DataType.STRING);			
			field.setLength(20);
			
		}	
		else
		{
			field.setDatatype(DataType.STRING);
			field.setLength(item.getItemlength());
		}
		return field;
    }
	/**
	 * 增加字段
	 * @param tabid
	 * @param muster_arr 
	 * @param tabname
	 * @param dbpre 应用库前缀
	 * @param infor 信息群种类
	 * @throws GeneralException
	 */
	private void addField(int tabid,String tabname,Object[] muster_arr,String dbpre,String infor)throws GeneralException
	{
		  ContentDAO dao=new ContentDAO(this.conn);
		  int imax=getMaxBaseIdByTab(tabid);
		  int i=0;
		  ArrayList fieldlist=new ArrayList();
		  try
		  {
			  ArrayList list=new ArrayList();
			  
			  StringBuffer fields=new StringBuffer();
			  int imatch=0;
			  String itemid=null;
			  String field_name=null;
			  for(i=0;i<muster_arr.length;i++)
			  {
				  String name=(String)(muster_arr[i]);
				  FieldItem fielditem=DataDictionary.getFieldItem(name.toUpperCase());
				  if(fielditem==null) {
                      continue;
                  }
				  itemid=fielditem.getItemid().toUpperCase();
				  /**计算此字段以前是否出现过*/
				  imatch=StringUtils.countMatches(fields.toString(),itemid);
				  RecordVo vo=new RecordVo("lbase");
				  vo.setInt("tabid",tabid);
				  vo.setInt("baseid",i+imax);
				  if(imatch!=0) {
                      field_name=fielditem.getFieldsetid()+"."+itemid+"_"+imatch;
                  } else{
						if("B0110".equalsIgnoreCase(fielditem.getItemid())|| "E0122".equalsIgnoreCase(fielditem.getItemid())
								|| "E01A1".equalsIgnoreCase(fielditem.getItemid())){
							if("A".equalsIgnoreCase(tabname.substring(tabname.length()-1,tabname.length()))){
								field_name="A01."+fielditem.getItemid();
							}else if("B".equalsIgnoreCase(tabname.substring(tabname.length()-1,tabname.length()))){
								field_name="B01."+fielditem.getItemid();
							}else if("K".equalsIgnoreCase(tabname.substring(tabname.length()-1,tabname.length()))){
								field_name="K01."+fielditem.getItemid();
							}else{
								field_name=fielditem.getFieldsetid()+"."+fielditem.getItemid();
							}
						}else{
							field_name=fielditem.getFieldsetid()+"."+fielditem.getItemid();
						}
				  }
				  vo.setString("field_name",field_name.toUpperCase());
				  vo.setString("colhz",fielditem.getItemdesc());
				  vo.setString("field_type",fielditem.getItemtype());
				  vo.setInt("wrap",1);
				  int width = fielditem.getDisplaywidth();
				  width=width!=0?width:1800;
				  vo.setInt("width",width);
				  vo.setInt("format",0);				  
				  if("N".equals(fielditem.getItemtype())) {
                      vo.setInt("align",3);
                  } else {
                      vo.setInt("align",1);
                  }
				  list.add(vo);
				  fieldlist.add(getFieldVo(fielditem));
				  /**把此字段加上计算字符串中去*/
				  fields.append(itemid);
				  fields.append(" ");
			  }
			  dao.addValueObject(list);
			  /**增加字段*/
			  DbWizard dbwizard = new DbWizard(this.conn);
			Table table = new Table(tabname);
			int n=0;
			
			for (i = 0; i < fieldlist.size(); i++){
				Field field = (Field) fieldlist.get(i);
				if(field!=null){
					if(!isExistField(tabname, field.getName())){
						n++;
						/*if(field.getName().equalsIgnoreCase("E0122"))
							field.setLength(200);*/
						table.addField(field);
					}
				}
			}
			if(n>0){
				dbwizard.addColumns(table);
				/** 导入数据 */

				HashMap hmset = splitFieldBySet(fieldlist, infor);
				importData(hmset, tabname, infor, dbpre);
				// this.fieldlist=fieldlist;
				transData(tabname, fieldlist);
			}
		  }
		  catch(Exception ex)
		  {
			  ex.printStackTrace();
			  throw GeneralExceptionHandler.Handle(ex);				  
		  }		
	}
	/**
	 * 增加字段
	 * @param tabid
	 * @param muster_arr 
	 * @param tabname
	 * @param dbpre 应用库前缀
	 * @param infor 信息群种类
	 * @throws GeneralException
	 */
	private void addTableField(String tabname,Object[] muster_arr,String dbpre,String infor)throws GeneralException
	{
		int i=0;
		ArrayList fieldlist=new ArrayList();
		try
		{
			for(i=0;i<muster_arr.length;i++)
			{
				String name=(String)(muster_arr[i]);
				FieldItem fielditem=DataDictionary.getFieldItem(name.toUpperCase());
				if(fielditem==null) {
                    continue;
                }
				fieldlist.add(getFieldVo(fielditem));
			}
			/**增加字段*/
			DbWizard dbwizard = new DbWizard(this.conn);
			Table table = new Table(tabname);
			int n=0;
			for (i = 0; i < fieldlist.size(); i++){
				Field field = (Field) fieldlist.get(i);
				if(field!=null){
					if(!isExistField(tabname, field.getName())){
						n++;
						/*if(field.getName().equalsIgnoreCase("E0122"))
							field.setLength(200);*/
						table.addField(field);
					}
				}
			}
			if(n>0){
				dbwizard.addColumns(table);
				/** 导入数据 */

				HashMap hmset = splitFieldBySet(fieldlist, infor);
				importData(hmset, tabname, infor, dbpre);
				// this.fieldlist=fieldlist;
				transData(tabname, fieldlist);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);				  
		}		
	}
	/**
	 * 删除花名册结构指标
	 * @param tabid
	 * @param tabname
	 * @param fieldlist
	 * @throws GeneralException
	 */
	private void dropField(int tabid,String tabname,ArrayList fieldlist)throws GeneralException
	{
		StringBuffer strsql=new StringBuffer();
		strsql.append("delete from lbase where tabid=? and field_name=?");
		ArrayList paralist=new ArrayList();
		for(int i=0;i<fieldlist.size();i++)
		{
			Field field=(Field)fieldlist.get(i);
			FieldItem fielditem=DataDictionary.getFieldItem(field.getName().toUpperCase().substring(0,5));
			if(fielditem==null) {
                continue;
            }
			String temp="";
			if("B0110".equalsIgnoreCase(fielditem.getItemid())|| "E0122".equalsIgnoreCase(fielditem.getItemid())
					|| "E01A1".equalsIgnoreCase(fielditem.getItemid())){
				if("A".equalsIgnoreCase(tabname.substring(tabname.length()-1,tabname.length()))){
					temp="A01."+field.getName();
				}else if("B".equalsIgnoreCase(tabname.substring(tabname.length()-1,tabname.length()))){
					temp="B01."+field.getName();
				}else if("K".equalsIgnoreCase(tabname.substring(tabname.length()-1,tabname.length()))){
					temp="K01."+field.getName();
				}else{
					temp=fielditem.getFieldsetid()+"."+field.getName();
				}
			}else{
				temp=fielditem.getFieldsetid()+"."+field.getName();
			}

			ArrayList list=new ArrayList();
			list.add(String.valueOf(tabid));
			list.add(temp);
			paralist.add(list);
		}
		if(paralist.size()==0) {
            return;
        }
		ContentDAO dao=new ContentDAO(this.conn);		
		try
		{
			dao.batchUpdate(strsql.toString(),paralist);
			/**删除数据*/
			DbWizard dbwizard=new DbWizard(this.conn);
			Table table=new Table(tabname);
			for(int i=0;i<fieldlist.size();i++)
			{
				Field field = (Field)fieldlist.get(i);
				if(field!=null){
					if(isExistField(tabname,field.getName())) {
                        table.addField(field);
                    }
				}
			}
			if(table!=null&&table.size()>0) {
                dbwizard.dropColumns(table);
            }
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	/**
	 * 删除花名册字段 
	 * @param tabname
	 * @param fieldlist
	 * @throws GeneralException
	 */
	private void dropTableField(String tabname,ArrayList fieldlist)throws GeneralException
	{
		try
		{
			/**删除数据*/
			DbWizard dbwizard=new DbWizard(this.conn);
			Table table=new Table(tabname);
			for(int i=0;i<fieldlist.size();i++)
			{
				Field field = (Field)fieldlist.get(i);
				if(field!=null){
					if(isExistField(tabname,field.getName())) {
                        table.addField(field);
                    }
				}
			}
			if(table!=null&&table.size()>0) {
                dbwizard.dropColumns(table);
            }
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	
	/**
	 * 求花名册标志字段内容
	 * @param userid
	 * @param usedflag
	 * @param mustertype
	 * @return for examples 总长20位
	 * 格式:×(0:公用，1:私用)+××(花名册类别ID)+ABC(用户名)+`+00000000000000
	 */
	private String getModuleFlag(String userid,String usedflag,String mustertype)
	{
		StringBuffer module_str=new StringBuffer();
		module_str.append(usedflag);
		if(mustertype.length()==1) {
            module_str.append("0");
        }
		module_str.append(mustertype);
		module_str.append(userid);
		module_str.append("`");
		module_str.append("00000000000000000000");
		return module_str.substring(0,20);
	}
	
	/**
	 * 取得花名册号
	 * @return 具体算法,最大加1
	 */
	private int getMusterTabId()throws GeneralException
	{
		int tabid=-1;
		StringBuffer sql=new StringBuffer();
		sql.append("select max(tabid)+1 as nmax from lname");
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;		
		try
		{
			recset=dao.search(sql.toString());
			if(recset.next())
			{
				tabid=recset.getInt("nmax");
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		    throw GeneralExceptionHandler.Handle(ex);
		}	
		finally
		{
			;
//			   try
//			   {
//				if(recset!=null)
//					recset.close();
//			   }
//			   catch(Exception ee)
//			   {
//				   ee.printStackTrace();
//			   }			
		}
		return tabid;
	}
	
	/**
	 * 保存花名册lbase表中的内容
	 * @param tabid
	 * @param muster_arr
	 * @param history
	 */
	private void saveMusterLbase(int tabid,String[] muster_arr,String history)throws GeneralException
	{
		  ContentDAO dao=new ContentDAO(this.conn);
		  int i=0;
		  try
		  {
			  ArrayList list=new ArrayList();
			  StringBuffer fields=new StringBuffer();
			  int imatch=0;
			  String itemid=null;
			  for(i=0;i<muster_arr.length;i++)
			  {
				  FieldItem fielditem=DataDictionary.getFieldItem(muster_arr[i]);
				  if(fielditem==null) {
                      continue;
                  }
				  itemid=fielditem.getItemid().toUpperCase();
				  /**计算此字段以前是否出现过*/
				  imatch=StringUtils.countMatches(fields.toString(),itemid);
				  RecordVo vo=new RecordVo("lbase");
				  vo.setInt("tabid",tabid);
				  vo.setInt("baseid",i);
				  if(imatch!=0){
					  vo.setString("field_name",fielditem.getFieldsetid()+"."+itemid+"_"+imatch);
				  }else{
					  vo.setString("field_name",fielditem.getFieldsetid()+"."+itemid);	
				  }
				 
				  vo.setString("colhz",fielditem.getItemdesc());
				  vo.setString("field_type",fielditem.getItemtype());
				  vo.setInt("wrap",1);
				  int width = fielditem.getDisplaywidth();
				  width=width!=0?width:1800;
				  vo.setInt("width",width);
				  vo.setInt("format",0);	
				  vo.setString("expr",history);
				  if("N".equals(fielditem.getItemtype())) {
                      vo.setInt("align",3);
                  } else {
                      vo.setInt("align",1);
                  }
				  list.add(vo);
				  /**把此字段加上计算字符串中去*/
				  fields.append(itemid);
				  fields.append(" ");
			  }
			  dao.addValueObject(list);
		  }
		  catch(Exception ex)
		  {
			  ex.printStackTrace();
			  throw GeneralExceptionHandler.Handle(ex);				  
		  }
	}
	/**
	 * 保存花名册lbase表中的内容
	 * @param tabid
	 * @param muster_arr
	 * @param history
	 */
	private void saveMusterLbase(int tabid,String[] muster_arr,String history,String infor_kind)throws GeneralException
	{
		  ContentDAO dao=new ContentDAO(this.conn);
		  int i=0;
		  try
		  {
			  ArrayList list=new ArrayList();
			  StringBuffer fields=new StringBuffer();
			  int imatch=0;
			  String itemid=null;
			  for(i=0;i<muster_arr.length;i++)
			  {
				  FieldItem fielditem=DataDictionary.getFieldItem(muster_arr[i]);
				  if(fielditem==null) {
                      continue;
                  }
				  itemid=fielditem.getItemid().toUpperCase();
				  /**计算此字段以前是否出现过*/
				  imatch=StringUtils.countMatches(fields.toString(),itemid);
				  RecordVo vo=new RecordVo("lbase");
				  vo.setInt("tabid",tabid);
				  vo.setInt("baseid",i);
				  if(imatch!=0){
					  if("B0110".equalsIgnoreCase(itemid)|| "E0122".equalsIgnoreCase(itemid)
							  || "E01A1".equalsIgnoreCase(itemid)){
						  if("1".equals(infor_kind)) {
                              vo.setString("field_name",fielditem.getFieldsetid()+"."+itemid+"_"+imatch);
                          } else if("2".equals(infor_kind)) {
                              vo.setString("field_name","B01"+"."+itemid+"_"+imatch);
                          } else if("3".equals(infor_kind)) {
                              vo.setString("field_name","K01"+"."+itemid+"_"+imatch);
                          } else {
                              vo.setString("field_name",fielditem.getFieldsetid()+"."+itemid+"_"+imatch);
                          }
					  }else{
						  vo.setString("field_name",fielditem.getFieldsetid()+"."+itemid+"_"+imatch);
					  }
				  }else{
					  if("1".equals(infor_kind)) {
                          vo.setString("field_name",fielditem.getFieldsetid()+"."+itemid);
                      } else if("2".equals(infor_kind)){
						  if("B0110".equalsIgnoreCase(itemid)) {
                              vo.setString("field_name","B01"+"."+itemid);
                          } else if("E0122".equalsIgnoreCase(itemid)) {
                              vo.setString("field_name","B01"+"."+itemid);
                          } else if("E01A1".equalsIgnoreCase(itemid)) {
                              vo.setString("field_name","B01"+"."+itemid);
                          } else {
                              vo.setString("field_name",fielditem.getFieldsetid()+"."+itemid);
                          }
					  }else if("3".equals(infor_kind)){
						  if("B0110".equalsIgnoreCase(itemid)) {
                              vo.setString("field_name","K01"+"."+itemid);
                          } else if("E0122".equalsIgnoreCase(itemid)) {
                              vo.setString("field_name","K01"+"."+itemid);
                          } else if("E01A1".equalsIgnoreCase(itemid)) {
                              vo.setString("field_name","K01"+"."+itemid);
                          } else {
                              vo.setString("field_name",fielditem.getFieldsetid()+"."+itemid);
                          }
					  }else{
						  vo.setString("field_name",fielditem.getFieldsetid()+"."+itemid); 
					  }
				  }
				 
				  vo.setString("colhz",fielditem.getItemdesc());
				  vo.setString("field_type",fielditem.getItemtype());
				  vo.setInt("wrap",1);
				  int width = fielditem.getDisplaywidth();
				  width=width!=0?width:1800;
				  vo.setInt("width",width);
				  vo.setInt("format",0);	
				  vo.setString("expr",history);
				  if("N".equals(fielditem.getItemtype())) {
                      vo.setInt("align",3);
                  } else {
                      vo.setInt("align",1);
                  }
				  list.add(vo);
				  /**把此字段加上计算字符串中去*/
				  fields.append(itemid);
				  fields.append(" ");
			  }
			  dao.addValueObject(list);
		  }
		  catch(Exception ex)
		  {
			  ex.printStackTrace();
			  throw GeneralExceptionHandler.Handle(ex);				  
		  }
	}
	
	/**
	 * 取得所有花名册列表
	 * @param inforkind
	 * @return
	 */
	public ArrayList getMusterList(String inforkind)throws GeneralException
	{
		ArrayList list=new ArrayList();
		StringBuffer strsql=new StringBuffer();
		strsql.append("select tabid,hzname from lname where flag='");
		strsql.append(inforkind);
		strsql.append("'");
		ContentDAO dao=new ContentDAO(this.conn);				
		RowSet recset=null;		
		try
		{
			recset=dao.search(strsql.toString());
			while(recset.next())
			{
				RecordVo vo=new RecordVo("lname");
				vo.setInt("tabid",recset.getInt("tabid"));
				vo.setString("hzname",recset.getInt("tabid")+"."+recset.getString("hzname"));
				list.add(vo);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);					
		}
		finally
		{
			;
//			try
//			{
//				if(recset!=null)
//					recset.close();
//			}
//			catch(Exception ee)
//			{
//				ee.printStackTrace();
//			}
		}
		return list;
	}
	/**
	 * 保存花名册结构s
	 * @param para_vo
	 * @throws GeneralException
	 */
	public RecordVo saveMuster(MusterParamterVo para_vo)throws GeneralException
	{
	  ContentDAO dao=new ContentDAO(this.conn);
	  RecordVo vo=new RecordVo("lname");
	  try
	  {
		String[] muster_fields=para_vo.getMusterfields();
//		String[] sort_fields=para_vo.getSortfields();
		
		/**保存花名册lname*/
		int tabid=this.getMusterTabId();
		vo.setInt("tabid",tabid);
		vo.setString("hzname",para_vo.getMustername());
		vo.setString("title",para_vo.getMustername());
		vo.setString("moduleflag",this.getModuleFlag(para_vo.getUsername(),para_vo.getUsed_flag(),para_vo.getMustertype()));//创建的添加到lname lbase
//		vo.setString("sortfield",this.getSortFields(sort_fields,para_vo.getInfor_kind()));
		vo.setString("sortfield",this.getSortFields(para_vo.getSortitem(),para_vo.getInfor_kind()));
		vo.setString("flag",para_vo.getInfor_kind());
		/**default*/
		vo.setString("titlefont","/fn\""+ResourceFactory.getProperty("gz.gz_acounting.m.font")+"\"/fz\"15\"/fb1/fi0/fu0/fk0");
		vo.setString("headtailfont","/fn\""+ResourceFactory.getProperty("gz.gz_acounting.m.font")+"\"/fz\"15\"/fb1/fi0/fu0/fk0");
		vo.setString("bodyfont",ResourceFactory.getProperty("gz.gz_acounting.m.font")+",9,0,0,0,0,");
		vo.setString("showmode","1");
		vo.setInt("marginleft",20);
		vo.setInt("marginright",20);
		vo.setInt("margintop",20);
		vo.setInt("marginbottom",20);
		
		dao.addValueObject(vo);
		/**保存花名册lbase*/
		saveMusterLbase(tabid,muster_fields,para_vo.getHistory(),para_vo.getInfor_kind());
		
	  }
	  catch(Exception ex)
	  {
		  ex.printStackTrace();
		  throw GeneralExceptionHandler.Handle(ex);		  
	  }
	  finally
	  {
		  dao=null;
	  }		
	  return vo;
	}
	
	/**
	 * 取得排序串
	 * @param username
	 * @return
	 */
	private String getMusterOrder(String username,String history,String infor)
	{
		String idx_table="T#"+username+"_mus";
		StringBuffer strorder=new StringBuffer();
		//strorder.append(" order by ");
		for(int i=0;i<this.sortlist.size();i++)
		{
			SortFieldVo vo=(SortFieldVo)this.sortlist.get(i);
			
			// 按单位部门排序 顺序取自organization 表单位部门的a0000     JinChunhai 2013.01.29
			String field_name = vo.getField_name().toLowerCase();
			if(("2".equals(infor)&&!"b0110".equals(field_name)) || ("1".equals(infor)&&!"a0100".equals(field_name)) || ("3".equals(infor)&&!"e01a1".equals(field_name))){
				
				if(!"1".equals(history))
				{
					strorder.append(idx_table);
					strorder.append(".");
				}
				
				if("b0110".equals(field_name)) {
                    strorder.append("bsort");
                } else if("e0122".equals(field_name)) {
                    strorder.append("esort");
                } else if("e01a1".equals(field_name)) {
                    strorder.append("asort");
                } else {
                    strorder.append(field_name);
                }
				
				if(vo.getOrder_flag()==0) {
                    strorder.append(" ASC ");
                } else {
                    strorder.append(" DESC ");
                }
				strorder.append(",");
			}else{
				if("b0110".equals(field_name)){
					strorder.append(field_name);
					if(vo.getOrder_flag()==0) {
                        strorder.append(" ASC ");
                    } else {
                        strorder.append(" DESC ");
                    }
					strorder.append(",");
				}
			}
			
		}
		
		if("2".equals(infor) && sortlist.size()<1) {
            strorder.append(" a0000 ASC,");
        }
		
		
		
		/**人员排序，直接加上一个人员顺序*/
		if("1".equals(infor)&&strorder.toString().toUpperCase().indexOf("A0000")==-1)
		{
			strorder.append(" a0000 ASC,");
		}
		if(strorder.toString().endsWith(",")) {
            strorder.setLength(strorder.length()-1);
        }
		if(this.sortlist.size()==0 && !"2".equals(infor))
		{
			if(!"1".equals(infor)) {
                return "";
            }
		}
		
		if(strorder.length()>0){
			strorder.insert(0, " order by ");
		}
		return strorder.toString();
	}

	/**
	 * 重填花名册
	 * @param infor
	 * @param dbpre
	 * @param musterid
	 * @param username
	 * @return
	 * @throws GeneralException
	 */
	public boolean refilloutMusterTable(String infor,String dbpre,String musterid,String username)throws GeneralException
	{
		boolean bflag=false;
		try
		{		
			/**固定字段*/
			this.fieldlist=getMusterFields(musterid,infor);
			bflag=true;
		}
		catch(Exception ex)
		{
			bflag=false;
		}
		return bflag;		
	}	
	/**
	 * 打开花名册
	 * @param infor
	 * @param dbpre
	 * @param musterid
	 * @param username
	 * @return
	 * @throws GeneralException
	 */
	public boolean openMusterTable(String infor,String dbpre,String musterid,String username)throws GeneralException
	{
		boolean bflag=false;
		try
		{		
			/**固定字段*/
			this.fieldlist=getMusterFields(musterid,infor);
			bflag=true;
		}
		catch(Exception ex)
		{
			bflag=false;
		}
		return bflag;		
	}
	/**
	 * 根据登录用户,信息群种类,花名册号创建花名册的临时表
	 * 临时表命名规则
	 * 人员花名册:m+花名册编号+”_”+用户+”_”+库前缀(Usr,Oth…)
	 * 单位花名册:m+花名册编号+”_” +用户+”_“+B
	 * 职位花名册:m+花名册编号+”_” +用户+”_“+K
	 * 排序表命名规则"order"+临时花名册的表名
	 * @param infor 信息群种类
	 * @param dbpre 应用库前缀
	 * @param musterid 花名册号
	 * @param username 用户名
	 * @param history  是否包含历史纪录
	 * @return
	 * @throws GeneralException
	 */
	public boolean createMusterTempTable(String infor,String dbpre,String musterid,
			String username,String history)throws GeneralException
	{
		boolean bflag=false;
		try
		{		
			String tablename = getTableName(infor, dbpre, musterid, username);			
			/**固定字段*/
			this.fieldlist=getMusterFieldsTemp(musterid,infor);
//			for(int i=0;i<this.fieldlist.size();i++)
//			{
//				cat.debug("--->field="+(this.fieldlist.get(i)).toString());
//			}
			Table table=new Table(tablename.toString());
			table.setCreatekey(false);
			for(int i=0;i<this.fieldlist.size();i++) {
                table.addField((Field)this.fieldlist.get(i));
            }
			
			Field temp2=new Field("i9999","i9999");
			temp2.setNullable(true);
			temp2.setKeyable(false);
			temp2.setDatatype(DataType.INT);
		    temp2.setSortable(true);		
			table.addField(temp2);
			
			DbWizard dbWizard=new DbWizard(this.conn);
			if(dbWizard.isExistTable(table.getName(),false))
			{
				dbWizard.dropTable(table);
			}
			
			dbWizard.createTable(table);
			/**从档案库中导入数据*/
			fillMusterData(infor,dbpre,musterid,username,history);
			/**导完数据后，再建主键*/
			dbWizard.addPrimaryKey(table);
			/**重新加载数据模型*/
			DBMetaModel dbmodel=new DBMetaModel(this.conn);
			dbmodel.reloadTableModel(tablename);
			bflag=true;
		}
		catch(Exception ex)
		{
			bflag=false;
		}
		return bflag;
	}
	/**
	 * 根据登录用户,信息群种类,花名册号创建花名册的临时表
	 * 临时表命名规则
	 * 人员花名册:m+花名册编号+”_”+用户+”_”+库前缀(Usr,Oth…)
	 * 单位花名册:m+花名册编号+”_” +用户+”_“+B
	 * 职位花名册:m+花名册编号+”_” +用户+”_“+K
	 * 排序表命名规则"order"+临时花名册的表名
	 * @param infor 信息群种类
	 * @param dbpre 应用库前缀
	 * @param musterid 花名册号
	 * @param username 用户名
	 * @param history  是否包含历史纪录
	 * @return
	 * @throws GeneralException
	 */
	public boolean createTempTable(String infor,String dbpre,String musterid,
			String username,String history)throws GeneralException
	{
		boolean bflag=false;
		try
		{		
			String tablename = getTableName(infor, dbpre, musterid, username.trim());			
			/**固定字段*/
			this.fieldlist=getMusterFieldsTemp(musterid,infor);
//			for(int i=0;i<this.fieldlist.size();i++)
//			{
//				cat.debug("--->field="+(this.fieldlist.get(i)).toString());
//			}
			Table table=new Table(tablename.toString());
			table.setCreatekey(false);
			for(int i=0;i<this.fieldlist.size();i++) {
                table.addField((Field)this.fieldlist.get(i));
            }
			
			Field temp2=new Field("i9999","i9999");
			temp2.setNullable(true);
			temp2.setKeyable(false);
			temp2.setDatatype(DataType.INT);
		    temp2.setSortable(true);
		    temp2.setLength(10);
			table.addField(temp2);
			
			DbWizard dbWizard=new DbWizard(this.conn);
			if(dbWizard.isExistTable(table.getName(),false))
			{
				dbWizard.dropTable(table);
			}
			
			dbWizard.createTable(table);
			/**建主键*/
			dbWizard.addPrimaryKey(table);
			/**重新加载数据模型*/
			DBMetaModel dbmodel=new DBMetaModel(this.conn);
			dbmodel.reloadTableModel(tablename);
			DbSecurityImpl dsi = new DbSecurityImpl();
			dsi.encryptTableName(conn, tablename);
			bflag=true;
		}
		catch(Exception ex)
		{
			bflag=false;
		}
		return bflag;
	}
	/**
	 * 根据登录用户,信息群种类,花名册号创建花名册的临时表
	 * 临时表命名规则
	 * 人员花名册:m+花名册编号+”_”+用户+”_”+库前缀(Usr,Oth…)
	 * 单位花名册:m+花名册编号+”_” +用户+”_“+B
	 * 职位花名册:m+花名册编号+”_” +用户+”_“+K
	 * 排序表命名规则"order"+临时花名册的表名
	 * @param infor 信息群种类
	 * @param dbpre 应用库前缀
	 * @param musterid 花名册号
	 * @param username 用户名
	 * @param history  是否包含历史纪录
	 * @return
	 * @throws GeneralException
	 */
	public boolean createMusterTempTable(String infor,String dbpre,String musterid,
			String username,String history,String a_code)throws GeneralException
	{
		boolean bflag=false;
		try
		{	
			String tablename = getTableName(infor, dbpre, musterid, username);			
			/**固定字段*/
			this.fieldlist=getMusterFieldsTemp(musterid,infor);
//			for(int i=0;i<this.fieldlist.size();i++)
//			{
//				cat.debug("--->field="+(this.fieldlist.get(i)).toString());
//			}
			Table table=new Table(tablename.toString());
			table.setCreatekey(false);
			for(int i=0;i<this.fieldlist.size();i++) {
                table.addField((Field)this.fieldlist.get(i));
            }
			
			Field temp2=new Field("i9999","i9999");
			temp2.setNullable(true);
			temp2.setKeyable(false);
			temp2.setDatatype(DataType.INT);
		    temp2.setSortable(true);		
			table.addField(temp2);
			
			DbWizard dbWizard=new DbWizard(this.conn);
			if(dbWizard.isExistTable(table.getName(),false))
			{
				dbWizard.dropTable(table);
			}
			
			dbWizard.createTable(table);
			/**从档案库中导入数据*/
			fillMusterData(infor,dbpre,musterid,username,history,a_code);
			/**导完数据后，再建主键*/
			dbWizard.addPrimaryKey(table);
			/**重新加载数据模型*/
			DBMetaModel dbmodel=new DBMetaModel(this.conn);
			dbmodel.reloadTableModel(tablename);
			bflag=true;
		}
		catch(Exception ex)
		{
			bflag=false;
		}
		return bflag;
	}
	
    public void createAllDBTempTable(String infor,ArrayList dblist,String musterid,
            String username,String history,String a_code)throws GeneralException {
        for(int i=0;i<dblist.size();i++){
            CommonData vo=(CommonData)dblist.get(i);
            String dbpre=vo.getDataValue();
            if("ALL".equals(dbpre)) {
                continue;
            }
            createMusterTempTable(infor,dbpre,musterid,username,history,a_code);
        }
    }

    public void createAllDBTempTable(String infor,ArrayList dblist,String musterid,
            String username,String history)throws GeneralException {
        for(int i=0;i<dblist.size();i++){
            CommonData vo=(CommonData)dblist.get(i);
            String dbpre=vo.getDataValue();
            if("ALL".equals(dbpre)) {
                continue;
            }
            createMusterTempTable(infor,dbpre,musterid,username,history);
        }
    }
    
	/**
	 * 根据登录用户,信息群种类,花名册号创建花名册的临时表
	 * 临时表命名规则
	 * 人员花名册:m+花名册编号+”_”+用户+”_”+库前缀(Usr,Oth…)
	 * 单位花名册:m+花名册编号+”_” +用户+”_“+B
	 * 职位花名册:m+花名册编号+”_” +用户+”_“+K
	 * 排序表命名规则"order"+临时花名册的表名
	 * @param infor 信息群种类
	 * @param dbpre 应用库前缀
	 * @param musterid 花名册号
	 * @param username 用户名
	 * @param history  是否包含历史纪录
	 * @return
	 * @throws GeneralException
	 */
	public boolean createMusterTempTable(String infor,String dbpre,String musterid,
			String username,String history,String a_code,String wherestr)throws GeneralException
	{
		boolean bflag=false;
		try
		{		
			String tablename = getTableName(infor, dbpre, musterid, username);			
			/**固定字段*/
			this.fieldlist=getMusterFields(musterid,infor);
//			for(int i=0;i<this.fieldlist.size();i++)
//			{
//				cat.debug("--->field="+(this.fieldlist.get(i)).toString());
//			}
			Table table=new Table(tablename.toString());
			table.setCreatekey(false);
			for(int i=0;i<this.fieldlist.size();i++) {
                table.addField((Field)this.fieldlist.get(i));
            }
			
			Field temp2=new Field("i9999","i9999");
			temp2.setNullable(true);
			temp2.setKeyable(false);
			temp2.setDatatype(DataType.INT);
		    temp2.setSortable(true);		
			table.addField(temp2);
			
			DbWizard dbWizard=new DbWizard(this.conn);
			if(dbWizard.isExistTable(table.getName(),false))
			{
				dbWizard.dropTable(table);
			}
			
			dbWizard.createTable(table);
			/**从档案库中导入数据*/
			fillMusterData(infor,dbpre,musterid,username,history,a_code,wherestr);
			/**导完数据后，再建主键*/
			dbWizard.addPrimaryKey(table);
			/**重新加载数据模型*/
			DBMetaModel dbmodel=new DBMetaModel(this.conn);
			dbmodel.reloadTableModel(tablename);
			bflag=true;
		}
		catch(Exception ex)
		{
			bflag=false;
		}
		return bflag;
	}
	
	/**
	 * 
	 * @param tabid
	 * @return
	 */
	private RecordVo getFontDescription(int tabid)
	{
		ContentDAO dao=new ContentDAO(this.conn);
		RecordVo vo=new RecordVo("lname");
		try
		{
			vo.setInt("tabid",tabid);
			vo=dao.findByPrimaryKey(vo);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return vo;		
	}
	/**
	 * 解析字体定义
	 *  表头表尾：/fn"新宋体"/fz"14"/fb0/fi0/fu0/fk0
	 * @param vo
	 * @param fontdesc
	 */
	private void parseHeadFontDefintion(FontBeanVo vo ,String fontdesc)
	{
		StringTokenizer st=new StringTokenizer(fontdesc,"/");
		int i=0;
		while(st.hasMoreTokens())
		{
			String temp=st.nextToken();
			switch(i)
			{
				case 0:
					vo.setFontname(temp.substring(3,temp.length()-1));
					break;
				case 1:
					vo.setFontsize(Integer.parseInt(temp.substring(3,temp.length()-1)));
					break;
				case 2:
					if("1".equals(temp.substring(2))) {
                        vo.setFsbold(true);
                    }
					break;
				case 3:
					if("1".equals(temp.substring(2))) {
                        vo.setFsItalic(true);
                    }
					break;
				case 4:
					if("1".equals(temp.substring(2))) {
                        vo.setFsUnderline(true);
                    }
					break;
				default:
					if("1".equals(temp.substring(2))) {
                        vo.setFsStrikeOut(true);
                    }
					break;
			}
			++i;
		}//while loop end.
	}
	
	/**
	 * 标题：/fn"新宋体"/fz"14"/c/fb0/fi0/fu0/fk0
	 * @param vo
	 * @param fontdesc
	 */
	private void parseTitleFontDefintion(FontBeanVo vo ,String fontdesc)
	{
		StringTokenizer st=new StringTokenizer(fontdesc,"/");
		int i=0;
		while(st.hasMoreTokens())
		{
			String temp=st.nextToken();
			switch(i)
			{
				case 0:
					vo.setFontname(temp.substring(3,temp.length()-1));
					break;
				case 1:
					vo.setFontsize(Integer.parseInt(temp.substring(3,temp.length()-1)));
					break;
				case 3:
					if("1".equals(temp.substring(2))) {
                        vo.setFsbold(true);
                    }
					break;
				case 4:
					if("1".equals(temp.substring(2))) {
                        vo.setFsItalic(true);
                    }
					break;
				case 5:
					if("1".equals(temp.substring(2))) {
                        vo.setFsUnderline(true);
                    }
					break;
				case 6:
					if("1".equals(temp.substring(2))) {
                        vo.setFsStrikeOut(true);
                    }
					break;
				default:
					break;
			}
			++i;
		}//while loop end.
	}	
	/**
	 * 解析字体定义
	 * 表体：宋体,11,0,0,0,0,
	 * @param vo
	 * @param fontdesc
	 */
	private void parseBodyFontDefintion(FontBeanVo vo ,String fontdesc)
	{
		StringTokenizer st=new StringTokenizer(fontdesc,",");
		int i=0;
		while(st.hasMoreTokens())
		{
			String temp=st.nextToken();
			switch(i)
			{
				case 0:
					vo.setFontname(temp);
					break;
				case 1:
					vo.setFontsize(Integer.parseInt(temp));
					break;
				case 2:
					if("1".equals(temp)) {
                        vo.setFsbold(true);
                    }
					break;
				case 3:
					if("1".equals(temp)) {
                        vo.setFsItalic(true);
                    }
					break;
				case 4:
					if("1".equals(temp)) {
                        vo.setFsUnderline(true);
                    }
					break;
				default:
					if("1".equals(temp)) {
                        vo.setFsStrikeOut(true);
                    }
					break;
			}
			++i;
		}//while loop end.
	}
	
	/**
	 * 取得对应的花名册的标题\表体\表头表尾
	 * flag=0 标题
	 *     =1 表体
	 *     =2 表头表尾
	 * @param flag
	 * @param tabid
	 */
	public FontBeanVo getFontProperty(int flag,int tabid)
	{
		FontBeanVo fontvo=new FontBeanVo();
		String fontdesc=null;
		RecordVo vo=getFontDescription(tabid);
		switch(flag)
		{
			case 0:
				fontdesc=vo.getString("titlefont");
				parseTitleFontDefintion(fontvo,fontdesc);				
				break;
			case 1:
				fontdesc=vo.getString("bodyfont");	
				parseBodyFontDefintion(fontvo,fontdesc);			
				break;
			default:
				fontdesc=vo.getString("headtailfont");	
				parseHeadFontDefintion(fontvo,fontdesc);			
				break;
		}
		return fontvo;
	}
	
	/**
	 * 增加记录(人员,单位或职位)
	 * @param infor
	 * @return
	 */
	public boolean addMusterRecordData(String infor,String tablename,ArrayList objlist,String history)
	{
		boolean bflag=false;
		StringTokenizer st=new StringTokenizer(tablename,"_");
	    String tabid=st.nextToken().substring(1);
	    String username=st.nextToken();
	    
	    String dbpre=tablename.substring(tablename.lastIndexOf("_")+1);	
	    if("B".equalsIgnoreCase(dbpre)) {
            dbpre="";
        } else if("K".equalsIgnoreCase(dbpre)) {
            dbpre="";
        }
		InfoGroup infogroup=new InfoGroup(Integer.parseInt(infor));		
		ContentDAO dao=new ContentDAO(this.conn);	
		
					
		/*取消主键*/
		this.fieldlist=getMusterFields(tablename.substring(1,tablename.indexOf("_")),infor);
		ArrayList list=getMusterFieldsValue(tabid,infor);
		this.sortlist=getOrderFieldList(tabid);
		
		Table table=new Table(tablename.toString());
		for(int i=0;i<this.fieldlist.size();i++) {
            table.addField((Field)this.fieldlist.get(i));
        }
		DbWizard dbWizard=new DbWizard(this.conn);
		
		try
		{		
			dbWizard.dropPrimaryKey(table.getName());
			dao.update("delete "+table.getName());
			ArrayList fieldlist=getMusterFieldsValue(tabid,infor);	    
			/**创建排序表*/
			createOrderTable(infor,tabid,username);
			ArrayList sqlList=new ArrayList();
			String id="";
			for(int i=0;i<objlist.size();i++)
			{
				id+=","+objlist.get(i);
				if((i+1)%1000==0||(i==objlist.size()-1)) {
					sqlList.add(id.substring(1));
					id="";
				}
			}
			/**根据查询结果中把数据填充至排序表中*/
			
			fillOutOrderDataByWhere(infor,dbpre,username,sqlList);
			/**取得排序字符串*/
			String strorder=getMusterOrder(username,history,infor);
			
			/**从临时排序表导入主键值*/
			String srcTab="T#"+username+"_mus";
			/**导入键值指标*/
			StringBuffer strsql=new StringBuffer();
			/**删除临时排序表中在花名册中已存在的记录*/
			strsql.append("delete from ");
			strsql.append(srcTab);
			strsql.append(" where ");
			strsql.append(infogroup.getKeyfield());		
	
			strsql.append(" in (select ");
			if("1".equals(infor)) {
                strsql.append(infogroup.getKeyfield());
            } else {
                strsql.append(infogroup.getKeyfield()+"_CODE");
            }
			strsql.append(" from ");
			strsql.append(tablename);
			strsql.append(")");
			dao.update(strsql.toString());
			
			if(!"1".equals(history))
			{
				
				/**开始导入数据*/
				strsql.setLength(0);
				strsql.append("insert into ");
				strsql.append(tablename);
				strsql.append("(");
				if("1".equals(infor)) {
                    strsql.append(infogroup.getKeyfield());
                } else {
                    strsql.append(infogroup.getKeyfield()+"_CODE");
                }
				strsql.append(",recidx) select ");
				strsql.append(infogroup.getKeyfield());
				strsql.append(",100000000 from ");
				strsql.append(srcTab);
		    	strsql.append(strorder);
		    	
				dao.update(strsql.toString());
				dao.update("update "+tablename+" set recidx='100000000'");
				/**更新花名册排序指标序号(recidx)*/
				updateMusterRecidx(tablename);
				HashMap hmset=splitFieldBySet(fieldlist,infor);
				importData(hmset,tablename,infor,dbpre);
				transData(tablename,fieldlist);
			}
			else
			{
				
				importData2(tablename,infor,dbpre,list,username,strorder);
				/**更新花名册排序指标序号(recidx)*/
				updateMusterRecidx(tablename);	
				String whereStr = "";
				if(sqlList.size()>0) {
					for(int i=0;i<sqlList.size();i++) {
						if(i!=0) {
							whereStr+=" or ";
						}
						whereStr="A0100 in ("+sqlList.get(i)+")";
					}
				}
					
				transData(tablename,fieldlist,whereStr);
			}
			//this.fieldlist=fieldlist;
			dbWizard.addPrimaryKey(table);
			appendResultTable(infor,objlist,tablename );
//			reSort(infor,dbpre,tabid,username);//重新排序
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return bflag;

	}
	
	
	
	
	public void appendResultTable(String infor,ArrayList objlist,String tablename )
	{
		ContentDAO dao=new ContentDAO(this.conn);
		String tableName="";
		try
		{
			String[] tableNameArray=tablename.split("_");
			
			UsrResultTable resulttable = new UsrResultTable();
			if(!resulttable.isNumber(tableNameArray[1])){
				String existDataID=getExistDataID(tablename,infor,objlist);
				if("1".equals(infor))
				{
					tableName=tableNameArray[1]+tableNameArray[2]+"Result";
					for(int i = 0 ; i<objlist.size(); i++){
						if(existDataID.indexOf((","+(String)objlist.get(i)))==-1) {
                            dao.insert("insert into "+tableName+" (A0100)values('"+(String)objlist.get(i)+"')",new ArrayList());
                        }
					}
				}
				else if("2".equals(infor))
				{
					tableName=tableNameArray[1]+"BResult";
					for(int i = 0 ; i<objlist.size(); i++){
						if(existDataID.indexOf((","+(String)objlist.get(i)))==-1) {
                            dao.insert("insert into "+tableName+" (B0110)values('"+(String)objlist.get(i)+"')",new ArrayList());
                        }
					}
					
				}
				else {
					tableName=tableNameArray[1]+"KResult";				
					for(int i = 0 ; i<objlist.size(); i++){
						if(existDataID.indexOf((","+(String)objlist.get(i)))==-1) {
                            dao.insert("insert into "+tableName+" (E01A1)values('"+(String)objlist.get(i)+"')",new ArrayList());
                        }
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
	}
	
	/**
	 * 得到结果表里已经存在的数据id
	 * @return
	 */
	public String getExistDataID(String tablename,String infor,ArrayList objlist)
	{
		StringBuffer existID=new StringBuffer("");
		ContentDAO dao=new ContentDAO(this.conn);	
		RowSet rowSet=null;
		try
		{
			
			String tableName="";
			StringBuffer sql=new StringBuffer("select ");
			StringBuffer whl_sql=new StringBuffer("");
			String[] tableNameArray=tablename.split("_");
			if("1".equals(infor))
			{
				tableName=tableNameArray[1]+tableNameArray[2]+"Result";
				sql.append("a0100 id");
				whl_sql.append(" a0100  in (");
			}
			else if("2".equals(infor))
			{
				tableName=tableNameArray[1]+"BResult";
				sql.append("b0110 id");
				whl_sql.append(" b0110  in (");
			}
			else
			{
				tableName=tableNameArray[1]+"KResult";
				sql.append("e01a1 id");
				whl_sql.append(" e01a1  in (");
			}
			createTable(this.conn ,tableName,infor);
			
			if(objlist.size()<1000) {
				sql.append(" from "+tableName+" where "+whl_sql.toString());
				StringBuffer whl=new StringBuffer("");
				for(int i=0;i<objlist.size();i++)
				{
					whl.append(",'"+(String)objlist.get(i)+"'");
				}
				sql.append(whl.substring(1)+" )");
			}else {
				sql.append(" from "+tableName+" where ");
				ArrayList sqlList=new ArrayList();
				String id="";
				for(int i=0;i<objlist.size();i++) {
					id+=","+(String)objlist.get(i);
					if((i+1)%1000==0||(i==(objlist.size()-1))) {
						sqlList.add(id.substring(1));
						id="";
					}
				}
				if(sqlList.size()>0) {
					for(int i=0;i<sqlList.size();i++) {
						sql.append(whl_sql.toString()+" "+sqlList.get(i).toString()+" )");
						if(i<sqlList.size()-1) {
							sql.append(" or ");
						}
					}
				}
				
			}
			rowSet=dao.search(sql.toString());
			while(rowSet.next())
			{
				existID.append(","+rowSet.getString("id"));
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		return existID.toString();
		
	}
	
	
	
	
	/**
	 * 判断特定表是否存在，不存在则创建表
	 * @param conn        DB连接
	 * @param tabldName   表名
	 * @param info_flag   标识
	 * @throws GeneralException
	 */
	public void createTable(Connection conn ,String tabldName ,String info_flag) throws GeneralException{
		Table table=new Table(tabldName);
		DbWizard dbWizard=new DbWizard(conn);
		if(!dbWizard.isExistTable(tabldName,false)){
			try {	
				ArrayList fieldList=this.getTableFields(info_flag);
				for(Iterator t=fieldList.iterator();t.hasNext();)
				{
					Field temp=(Field)t.next();
					table.addField(temp);
				}
				dbWizard.createTable(table);
			} catch (GeneralException e) {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}	
		}
	}
	/**
	 * 花名册重新排序
	 * @param infor
	 * @param dbpre
	 * @param musterid
	 * @param username
	 */
	public void reSort(String infor,String dbpre,String musterid,String username){
		String tablename = getTableName(infor, dbpre, musterid, username);
		Table table=new Table(tablename);
		
		ArrayList fielditemlist=getMusterFields(musterid,infor);

		for(int i=0;i<fielditemlist.size();i++) {
            table.addField((Field)fielditemlist.get(i));
        }
		
		DbWizard dbWizard=new DbWizard(this.conn);
		try {
			dbWizard.dropPrimaryKey(table.getName());
			StringBuffer strsql=new StringBuffer();
			StringBuffer updatesql = new StringBuffer();
			updatesql.append("update ");
			updatesql.append(tablename);
			updatesql.append(" set recidx=? where ");
			
			strsql.append("select ");
			if("1".equals(infor)){
				strsql.append("A0100 as itemid ");
				updatesql.append("A0100=?");
			}else if("2".equals(infor)){
				strsql.append("B0110 as itemid ");
				updatesql.append("B0110=?");
			}else if("3".equals(infor)){
				strsql.append("E01A1 as itemid ");
				updatesql.append("E01A1=?");
			}else{
				strsql.append("A0100 as itemid ");
				updatesql.append("A0100=?");
			}
			strsql.append(" from ");
			strsql.append(tablename);
			strsql.append(getOrderby(musterid));
			
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rs=dao.search(strsql.toString());
			ArrayList listvalue = new ArrayList();
			int i=1;
			while(rs.next()){
				String itemid = rs.getString("itemid");
				ArrayList list = new ArrayList();
				list.add(i+"");
				list.add(itemid);
				listvalue.add(list);
				i++;
			}
			
			dao.batchUpdate(updatesql.toString(), listvalue);
			dbWizard.addPrimaryKey(table);
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 取得排序指标
	 * @param musterid
	 * @return
	 */
	private String getOrderby(String musterid)
	{
		StringBuffer orderby = new StringBuffer(" order by ");
		StringBuffer strsql=new StringBuffer();
		strsql.append("select sortfield from lname where tabid=");
		strsql.append(musterid);
		RowSet rset=null;		
		ContentDAO dao=new ContentDAO(this.conn);
		String sortfields="";
		try{
			rset=dao.search(strsql.toString());
			if(rset.next()){
				sortfields=Sql_switcher.readMemo(rset,"sortfield");
			}else{
				return "";
			}
			String[] sortarr=StringUtils.split(sortfields,",");
			for(int i=0;i<sortarr.length;i++){
				sortfields=sortarr[i];
				if(sortfields!=null&&sortfields.trim().length()>9){
					sortfields=sortfields.substring(4);
					orderby.append(sortfields.substring(0,5));
					if("0".equals(sortfields.substring(5))) {
                        orderby.append(" ASC,");
                    } else {
                        orderby.append(" DESC,");
                    }
				}
			}
			orderby.append("recidx");
		}catch(Exception ex){}
		return orderby.toString();
	}
	 public boolean isExistField(String table_name, String field_name){
	        boolean bflag = false;
	        Statement stmt = null;
	        try{
	        	StringBuffer strsql = new StringBuffer();
	        	strsql.append("select ");
	        	strsql.append(field_name);
	        	strsql.append(" from ");
	        	strsql.append(table_name);
	        	strsql.append(" where 1=2");
	            stmt = conn.createStatement();
	            stmt.executeQuery(strsql.toString());
	            bflag = true;
	        }catch(Exception ex){

	        } finally {
				PubFunc.closeResource(stmt);
			}
	        return bflag;
	    }
	/**
	 * 获得特定表的字段
	 * @param info_flag  标识
	 * @return
	 */
	public ArrayList getTableFields(String info_flag){
		ArrayList list = new ArrayList();
		if("1".equals(info_flag)){
			//人员		
			Field field = new Field("A0100","A0100");
			field.setLength(8);
			field.setDatatype("DataType.STRING");
			
			Field field1 = new Field("B0110","B0110");
			field1.setDatatype("DataType.STRING");
			field1.setLength(30);
			list.add(field);
			list.add(field1);
			
		}else if(info_flag.endsWith("2")){
	
			Field field1 = new Field("B0110","B0110");
			field1.setDatatype("DataType.STRING");
			field1.setLength(30);
			list.add(field1);
	
		}else if("3".equals(info_flag)){
			//职位
			Field field = new Field("E01A1","E01A1");
			field.setDatatype("DataType.STRING");
			field.setLength(30);
			list.add(field);
		}
		return list;
	}
	
	
	/**
	 * 重填花名册,根据当前的查询结果进行花名册填充
	 * @param infor 信息群种类
	 * @param dbpre 应用库前缀
	 * @param musterid 花名册号
	 * @param username 用户名
	 * @param history  是否包含历史纪录
	 * @return
	 */
	public boolean fillMusterData(String infor,String dbpre,String musterid,String username,String history)
	{	
		boolean bflag=false;
		InfoGroup infogroup=new InfoGroup(Integer.parseInt(infor));		
		/**花名册数据表*/
		String tablename=getTableName(infor, dbpre, musterid, username);
		ArrayList list=getMusterFieldsValue(musterid,infor);
		/**创建排序表*/
		createOrderTable(infor,musterid,username);
		/**根据查询结果中把数据填充至排序表中*/
		fillOutOrderData(infor,dbpre,username);
		/**取得排序字符串*/
		String strorder=getMusterOrder(username,history,infor);
		
		try
		{			
			if(!"1".equals(history))
			{				
				/**从临时排序表导入主键值*/
				String srcTab="T#"+username+"_mus";
				/** 导入排序后的键值指标 
				 * 1.某些sql2008/2012库，执行insert into ... select ... from ... order by ...后，目的表的记录顺序不正确。
				 *   解决方法：先在目的表增加自增长字段，再导入数据，顺序是对的。
				 */
				StringBuffer strsql=new StringBuffer();
		    	if(Sql_switcher.searchDbServer()==Constant.MSSQL)
		    	{
		    		// 先增加自增长字段
		    		strsql.append("alter table " + tablename + " add xxx int identity(1,1)");
					DbWizard db=new DbWizard(this.conn);
					db.execute(strsql.toString());
		    	}
				
				strsql.setLength(0);
				strsql.append("insert into ");
				strsql.append(tablename);
				strsql.append("(");
				if("1".equals(infor)) {
                    strsql.append(infogroup.getKeyfield());
                } else {
                    strsql.append(infogroup.getKeyfield()+"_CODE");
                }
				strsql.append(",recidx) select ");
				strsql.append(infogroup.getKeyfield());
				strsql.append(",1 from ");
				strsql.append(srcTab);
		    	strsql.append(strorder);
		    	if(Sql_switcher.searchDbServer()==Constant.ORACEL)
		    	{
		    		strsql.setLength(0);
		    		/*StringBuffer sql = new StringBuffer();
					sql.append(" insert into "+tabname+" (recidx"+buf.toString()+") ");
					sql.append("select RowNum,a.* from (");
					sql.append(" select "+buf.toString().substring(1));
					sql.append(" from "+tabname+"_0 order by recidx,i9999) a");
					str=sql.toString();*/
		    		strsql.append("insert into "+tablename+"(");
		    		if("1".equals(infor)) {
                        strsql.append(infogroup.getKeyfield());
                    } else {
                        strsql.append(infogroup.getKeyfield()+"_CODE");
                    }
					strsql.append(",recidx) select a."+infogroup.getKeyfield());
					//strsql.append(infogroup.getKeyfield());
					strsql.append(",RowNum from ( select ");
					strsql.append(infogroup.getKeyfield());
					strsql.append(" from ");
					strsql.append(srcTab);
			    	strsql.append(strorder+") a");
		    	}
				ContentDAO dao=new ContentDAO(this.conn);
				dao.update(strsql.toString());
				/**更新花名册排序指标序号(recidx)*/
				if(Sql_switcher.searchDbServer()!=Constant.ORACEL)
				{
			    	updateMusterRecidx(tablename);
				}
				HashMap hmset=splitFieldBySet(list,infor);
				importData(hmset,tablename,infor,dbpre);
				transData(tablename,this.fieldlist);
			}
			else  //包含历史纪录 
			{
				this.repeat_mainset=getHistoryBytable(musterid);
				importData2(tablename,infor,dbpre,list,username,strorder);
				/**更新花名册排序指标序号(recidx)*/
				if(Sql_switcher.searchDbServer()!=Constant.ORACEL)
				{
			     	updateMusterRecidx(tablename);
				}
				transData(tablename,this.fieldlist);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return bflag;
		
	}
	/**
	 * 重填花名册,根据当前的查询结果进行花名册填充
	 * @param infor 信息群种类
	 * @param dbpre 应用库前缀
	 * @param musterid 花名册号
	 * @param username 用户名
	 * @param history  是否包含历史纪录
	 * @return
	 */
	public boolean fillMusterData(String infor,String dbpre,String musterid,String username,String history,String a_code)
	{	
		boolean bflag=false;
		InfoGroup infogroup=new InfoGroup(Integer.parseInt(infor));		
		/**花名册数据表*/
		String tablename=getTableName(infor, dbpre, musterid, username);
		ArrayList list=getMusterFields(musterid,infor);
		/**创建排序表*/
		createOrderTable(infor,musterid,username);
		/**根据查询结果中把数据填充至排序表中*/
		fillOutOrderData(infor,dbpre,username,a_code);
		/**取得排序字符串*/
		String strorder=getMusterOrder(username,history,infor);
		
		try
		{			
			if(!"1".equals(history))
			{				
				/**从临时排序表导入主键值*/
				String srcTab="T#"+username+"_mus";
				/**导入键值指标*/
				StringBuffer strsql=new StringBuffer();
				strsql.append("insert into ");
				strsql.append(tablename);
				strsql.append("(");
				if("1".equals(infor)) {
                    strsql.append(infogroup.getKeyfield());
                } else {
                    strsql.append(infogroup.getKeyfield()+"_CODE");
                }
				strsql.append(",recidx) select ");
				strsql.append(infogroup.getKeyfield());
				strsql.append(",1 from ");
				strsql.append(srcTab);
		    	strsql.append(strorder);
		    	if(Sql_switcher.searchDbServer()==Constant.ORACEL)
		    	{
		    		strsql.setLength(0);
		    		strsql.append("insert into ");
					strsql.append(tablename);
					strsql.append("(");
					if("1".equals(infor)) {
                        strsql.append(infogroup.getKeyfield());
                    } else {
                        strsql.append(infogroup.getKeyfield()+"_CODE");
                    }
					strsql.append(",recidx) select ");
					strsql.append(infogroup.getKeyfield());
					strsql.append(",RowNum from ");
					strsql.append("(select "+infogroup.getKeyfield()+" from ");
					strsql.append(srcTab);
			    	strsql.append(strorder);
			    	strsql.append(") a");
		    	}
				ContentDAO dao=new ContentDAO(this.conn);
				dao.update(strsql.toString());
				/**更新花名册排序指标序号(recidx)*/
				updateMusterRecidx(tablename);
				HashMap hmset=splitFieldBySet(list,infor);
				importData(hmset,tablename,infor,dbpre);
				transData(tablename,this.fieldlist);
			}
			else  //包含历史纪录 
			{	
				importData2(tablename,infor,dbpre,list,username,strorder);
				/**更新花名册排序指标序号(recidx)*/
				updateMusterRecidx(tablename);
				transData(tablename,this.fieldlist);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return bflag;
		
	}
	
	/**
	 * 重填花名册,根据当前的查询结果进行花名册填充
	 * @param infor 信息群种类
	 * @param dbpre 应用库前缀
	 * @param musterid 花名册号
	 * @param username 用户名
	 * @param history  是否包含历史纪录
	 * @return
	 */
	public boolean fillMusterData(String infor,String dbpre,String musterid,String username,
			String history,String a_code,String wherestr)
	{	
		boolean bflag=false;
		InfoGroup infogroup=new InfoGroup(Integer.parseInt(infor));		
		/**花名册数据表*/
		String tablename=getTableName(infor, dbpre, musterid, username);
		ArrayList list=getMusterFields(musterid,infor);
		/**创建排序表*/
		createOrderTable(infor,musterid,username);
		/**根据查询结果中把数据填充至排序表中*/
		fillOutOrderData(infor,dbpre,username,a_code,wherestr);
		/**取得排序字符串*/
		String strorder=getMusterOrder(username,history,infor);
		
		try
		{			
			if(!"1".equals(history))
			{				
				/**从临时排序表导入主键值*/
				String srcTab="T#"+username+"_mus";
				/**导入键值指标*/
				StringBuffer strsql=new StringBuffer();
				strsql.append("insert into ");
				strsql.append(tablename);
				strsql.append("(");
				if("1".equals(infor)) {
                    strsql.append(infogroup.getKeyfield());
                } else {
                    strsql.append(infogroup.getKeyfield()+"_CODE");
                }
				strsql.append(",recidx) select ");
				strsql.append(infogroup.getKeyfield());
				strsql.append(",1 from ");
				strsql.append(srcTab);
		    	strsql.append(strorder);
		    	if(Sql_switcher.searchDbServer()==Constant.ORACEL)
		    	{
		    		strsql.setLength(0);
		    		strsql.append("insert into ");
					strsql.append(tablename);
					strsql.append("(");
					if("1".equals(infor)) {
                        strsql.append(infogroup.getKeyfield());
                    } else {
                        strsql.append(infogroup.getKeyfield()+"_CODE");
                    }
					strsql.append(",recidx) select ");
					strsql.append(infogroup.getKeyfield());
					strsql.append(",RowNum from ");
					strsql.append("(select "+infogroup.getKeyfield()+" from ");
					strsql.append(srcTab);
			    	strsql.append(strorder);
			    	strsql.append(") a");
		    	}
				ContentDAO dao=new ContentDAO(this.conn);
				dao.update(strsql.toString());
				/**更新花名册排序指标序号(recidx)*/
				if(Sql_switcher.searchDbServer()!=Constant.ORACEL)
				{
		     		updateMusterRecidx(tablename);
				}
				HashMap hmset=splitFieldBySet(list,infor);
				importData(hmset,tablename,infor,dbpre);
				transData(tablename,this.fieldlist);
			}
			else  //包含历史纪录 
			{
				importData2(tablename,infor,dbpre,list,username,strorder);
				/**更新花名册排序指标序号(recidx)*/
				updateMusterRecidx(tablename);
				transData(tablename,this.fieldlist);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return bflag;
		
	}
	

	/**
	 * 分解拆分指标,根据子集合并同类项
	 * @param list
	 * @param infor
	 * @return
	 */
	private HashMap splitFieldBySet(ArrayList list,String infor)
	{
		HashMap hm=new HashMap();
		Field field=null;
		for(int i=0;i<list.size();i++)
		{
			field=(Field)list.get(i);
			FieldItem fielditem = DataDictionary.getFieldItem(field.getName());
			if(fielditem==null) {
                continue;
            }
			if(field==null) {
                continue;
            }
			if("A0100".equalsIgnoreCase(field.getName())|| "B0110_CODE".equalsIgnoreCase(field.getName())||
					"E01A1_CODE".equalsIgnoreCase(field.getName())) {
                continue;
            }
			/**A0502_1*/
			String name=field.getName().substring(0,5);
			FieldItem temp=DataDictionary.getFieldItem(name);
			if(temp==null) {
                continue;
            }
			FieldItem item=(FieldItem)temp.clone();			
			item.setItemid(field.getName());			
			String setname=item.getFieldsetid();
			if("B0110".equalsIgnoreCase(name)&& "2".equals(infor)) {
                setname="B01";
            }
			if(("E01A1".equalsIgnoreCase(name)|| "e0122".equalsIgnoreCase(name)|| "b0110".equalsIgnoreCase(name))&& "3".equals(infor)) {
                setname="K01";
            }
			ArrayList fieldlist=(ArrayList)hm.get(setname);
			if(fieldlist==null)
			{
				fieldlist=new ArrayList();
				fieldlist.add(item);
				hm.put(setname,fieldlist);
			}
			else {
                fieldlist.add(item);
            }
		}
		return hm;
	}
	
	
	/**
	 * 花名册是否有数据
	 * @param tablename
	 * @return
	 */
	public boolean haveDataInMuster(String tablename)
	{
		DbWizard db=new DbWizard(this.conn);
		return db.isExistTable(tablename,false);
	}
	/**
	 * 花名册是否有数据
	 * @param tablename
	 * @return
	 */
	public boolean haveDataInMuster(String tablename,String itemid)
	{
		DbWizard db=new DbWizard(this.conn);
		return db.isExistField(tablename,itemid);
	}
	
	
	public boolean isSequence(int dbflag)
	{
		boolean flag=false;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			if(dbflag==Constant.ORACEL){
				RowSet rowSet=dao.search("select sequence_name from user_sequences where lower(sequence_name)='xxx'");
				if(rowSet.next()) {
                    flag=true;
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
	 * 更新花名册的排序指标序号
	 * @param name
	 */
	public void updateMusterRecidx(String name)// throws GeneralException
	{
		StringBuffer strsql=new StringBuffer();
		try
		{
			DbWizard db=new DbWizard(this.conn);
			switch(dbflag)
			{
			//[15368] 【员工管理】信息浏览--导出excel,页面后台报错：列名'xxx'无效 sunm 2016-1-15
			//isExistField(name, "xxx")改成db.isExistField(name, "xxx", false)
			case Constant.MSSQL:
				if(!db.isExistField(name, "xxx", false)){
					strsql.append("alter table ");  // 字段已增加
					strsql.append(name);
					strsql.append(" add xxx int identity(1,1)");
				}
				break;
			default:	
				    if(isSequence(dbflag))
				    {
				    	db.execute("drop sequence xxx");	
				    }
				    strsql.append("create sequence xxx increment by 1 start with 1");
				break;
			}
			if(strsql.length()>0) {
                db.execute(strsql.toString());
            }
			strsql.setLength(0);
			switch(dbflag)
			{
			case Constant.MSSQL:
				strsql.append("update ");
				strsql.append(name);
				strsql.append(" set recidx=xxx");
				break;
			case Constant.DB2:
				strsql.append("update ");
				strsql.append(name);
				strsql.append(" set recidx=nextval for xxx");			
				break;
			case Constant.ORACEL:
				strsql.append("update ");
				strsql.append(name);
				strsql.append(" set recidx=xxx.nextval");					
				break;
			default:
				strsql.append("update ");
				strsql.append(name);
				strsql.append(" set recidx=xxx");
				break;
			}	
			db.execute(strsql.toString());	
			strsql.setLength(0);			
			switch(dbflag)
			{
			case Constant.MSSQL:
				strsql.append("alter table ");
				strsql.append(name);
				strsql.append(" drop column xxx");
				break;
			default:
				strsql.append(" drop sequence xxx");
				break;
			}		
			db.execute(strsql.toString());	
		}
		catch(Exception ex)
		{
		//	ex.printStackTrace();
		//    throw GeneralExceptionHandler.Handle(ex);			
		}
	
	}
	
	
	/**
	 * 取得update等式字符串,原始字段需进行字段类型转换,
	 * 因为目标字段全都为字符型,
	 * for examples
	 *     desttab.xxx=srcTab.xxx,desttab.aaa=srcTab.aaa
	 * @param destTab 
	 * @param srcTab
	 * @param list
	 * @return
	 */
	private String getUpdateField(String destTab,String srcTab,ArrayList list)
	{
		if(list.size()==0) {
            return "";
        }
		StringBuffer strfield=new StringBuffer();
		for(int i=0;i<list.size();i++)
		{
			FieldItem item=(FieldItem)list.get(i);
			if("0".equals(item.getUseflag())) {
                continue;
            }
			
			strfield.append(destTab);
			strfield.append(".");
			strfield.append(item.getItemid());
			strfield.append("=");
			/**数值类型转换*/
			if("D".equals(item.getItemtype()))
			{
				strfield.append(Sql_switcher.dateToChar(srcTab+"."+item.getItemid().substring(0,5),"yyyy-mm-dd"));
			}
			else if("N".equals(item.getItemtype()))
			{
				if(dbflag==Constant.ORACEL)  
				{
					if(item.getDecimalwidth()>0) {
                        strfield.append("TO_CHAR("+srcTab+"."+item.getItemid().substring(0,5)+",'FM9999999990.0999')");
                    } else {
                        strfield.append("TO_CHAR("+srcTab+"."+item.getItemid().substring(0,5)+",'FM9999999999')");
                    }
				}
				else {
                    strfield.append(Sql_switcher.numberToChar(srcTab+"."+item.getItemid().substring(0,5)));
                }
			}
			else
			{
				strfield.append(srcTab);
				strfield.append(".");		
				/**A0405_1*/
				strfield.append(item.getItemid().substring(0,5));
			}
			strfield.append("`");//
		}
		if(list.size()>0&&strfield.length()>1) {
            strfield.setLength(strfield.length()-1);
        }
		return strfield.toString();
	}

	/**
	 * 翻译代码
	 * @param destTab
	 * @param infor
	 */
	private void transData(String destTab,ArrayList fieldlist)throws GeneralException
	{
		Field item=null;
		DbWizard dbWizard =new DbWizard(this.conn);	
		ContentDAO dao = new ContentDAO(this.conn);
		StringBuffer strJoin=new StringBuffer();
		StringBuffer strSet =new StringBuffer();
		StringBuffer strSWhere=new StringBuffer();
		String srcTab=null;
		for(int i=0;i</*this.*/fieldlist.size();i++)
		{
			item=(Field)/*this.*/fieldlist.get(i);
			/**for examples A0405_1*/
			String fieldname=item.getName();
			if("E0122".equalsIgnoreCase(item.getName())){
				layerCodeName(destTab);
				continue;
			}
			
			if("A0100".equalsIgnoreCase(fieldname)|| "recidx".equalsIgnoreCase(fieldname)||
			   "B0110_CODE".equalsIgnoreCase(fieldname)|| "E01A1_CODE".equalsIgnoreCase(fieldname))
			{
				continue;
			}
			FieldItem temp=DataDictionary.getFieldItem(fieldname.substring(0,5));
			if(temp==null) {
                continue;
            }
			if("".equals(temp.getCodesetid())|| "0".equalsIgnoreCase(temp.getCodesetid())) {
                continue;
            }
			if("UN".equalsIgnoreCase(temp.getCodesetid())|| "UM".equalsIgnoreCase(temp.getCodesetid())||
					"@K".equalsIgnoreCase(temp.getCodesetid()))
			{
				srcTab="organization";
			}
			else
			{
				srcTab="codeitem";
			}
			strJoin.append(destTab);
			strJoin.append(".");
			strJoin.append(fieldname);
			strJoin.append("=");
			strJoin.append(srcTab);
			strJoin.append(".codeitemid");
			
			strSet.append(destTab);
			strSet.append(".");
			strSet.append(fieldname);
			strSet.append("=");
			strSet.append(srcTab);
			strSet.append(".codeitemdesc");
			
			strSWhere.append(" codesetid='");
			strSWhere.append(temp.getCodesetid());
			strSWhere.append("'");
			
			if("organization".equals(srcTab)&& "UN".equals(temp.getCodesetid()))
			{
				strSWhere.setLength(0);
				strSWhere.append(" ( codesetid='");
				strSWhere.append(temp.getCodesetid());
				strSWhere.append("' or codesetid='UM' ) ");
			}
			if("organization".equals(srcTab)&& "UM".equals(temp.getCodesetid()))
			{
				strSWhere.setLength(0);
				strSWhere.append(" ( codesetid='");
				strSWhere.append(temp.getCodesetid());
				strSWhere.append("' or codesetid='UN' ) ");
			}
			StringBuffer sqlstr = new StringBuffer();
			sqlstr.append("update ");
			sqlstr.append(destTab);
			sqlstr.append(" set ");
			sqlstr.append(fieldname);
			sqlstr.append("='' where ");
			sqlstr.append(fieldname);
			sqlstr.append(" not in(select codeitemid from ");
			sqlstr.append(srcTab);
			if("organization".equals(srcTab)&& "UN".equals(temp.getCodesetid())){
				sqlstr.append(" where codesetid='UM' or codesetid='");
				sqlstr.append(temp.getCodesetid());
			}else if("organization".equals(srcTab)&& "UM".equals(temp.getCodesetid())){
				sqlstr.append(" where  codesetid='UN' or codesetid='");
				sqlstr.append(temp.getCodesetid());
			}
			else
			{
				sqlstr.append(" where codesetid='");
				sqlstr.append(temp.getCodesetid());
			}
			sqlstr.append("')");
			try {
				dao.update(sqlstr.toString());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			dbWizard.updateRecord(destTab,srcTab,strJoin.toString(),strSet.toString(),"",strSWhere.toString());			

			/**清空字符串*/
			strJoin.setLength(0);
			strSWhere.setLength(0);	
			strSet.setLength(0);
		}
	}
	public void layerCodeName(String tablename){
		Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.conn);
		String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);//显示部门层数
		uplevel=uplevel!=null&&uplevel.trim().length()>0?uplevel:"0";
		int nlevel=Integer.parseInt(uplevel);
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("select E0122 from ");
		sqlstr.append(tablename);
		sqlstr.append(" group by E0122");
		ContentDAO dao = new ContentDAO(this.conn);
		ArrayList listvalue = new ArrayList();
		StringBuffer updatestr =new StringBuffer();
		updatestr.append("update ");
		updatestr.append(tablename);
		updatestr.append(" set E0122=? where E0122=?");
		try {
			RowSet rs = dao.search(sqlstr.toString());
			while (rs.next()){
				String E0122 = rs.getString("E0122");
				if(E0122!=null&&E0122.trim().length()>0){
					CodeItem item = null;
					if(nlevel>0) {
                        item = AdminCode.getCode("UM",E0122,nlevel);
                    } else {
                        item = AdminCode.getCode("UM",E0122);
                    }
					if(item==null) {
                        item = AdminCode.getCode("UN",E0122);
                    }
					ArrayList list = new ArrayList();
					if(item!=null) {
                        list.add(item.getCodename());
                    } else {
                        list.add(E0122);
                    }
					list.add(E0122);
					listvalue.add(list);
				}
			}
			dao.batchUpdate(updatestr.toString(), listvalue);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 翻译代码
	 * @param destTab
	 * @param infor
	 */
	private void transData(String destTab,ArrayList fieldlist,String toWhere)throws GeneralException
	{
		Field item=null;
		DbWizard dbWizard =new DbWizard(this.conn);	
		StringBuffer strJoin=new StringBuffer();
		StringBuffer strSet =new StringBuffer();
		StringBuffer strSWhere=new StringBuffer();
		String srcTab=null;
		for(int i=0;i</*this.*/fieldlist.size();i++)
		{
			item=(Field)/*this.*/fieldlist.get(i);
			/**for examples A0405_1*/
			String fieldname=item.getName();
			if("A0100".equalsIgnoreCase(fieldname)|| "recidx".equalsIgnoreCase(fieldname)||
			   "B0110_CODE".equalsIgnoreCase(fieldname)|| "E01A1_CODE".equalsIgnoreCase(fieldname))
			{
				continue;
			}
			FieldItem temp=DataDictionary.getFieldItem(fieldname.substring(0,5));
			if(temp==null) {
                continue;
            }
			if("".equals(temp.getCodesetid())|| "0".equalsIgnoreCase(temp.getCodesetid())) {
                continue;
            }
			if("UN".equalsIgnoreCase(temp.getCodesetid())|| "UM".equalsIgnoreCase(temp.getCodesetid())||
					"@K".equalsIgnoreCase(temp.getCodesetid()))
			{
				srcTab="organization";
			}
			else
			{
				srcTab="codeitem";
			}
			strJoin.append(destTab);
			strJoin.append(".");
			strJoin.append(fieldname);
			strJoin.append("=");
			strJoin.append(srcTab);
			strJoin.append(".codeitemid");
			
			strSet.append(destTab);
			strSet.append(".");
			strSet.append(fieldname);
			strSet.append("=");
			strSet.append(srcTab);
			strSet.append(".codeitemdesc");
			
			strSWhere.append(" codesetid='");
			strSWhere.append(temp.getCodesetid());
			strSWhere.append("'");
			
			if("organization".equals(srcTab)&& "UN".equals(temp.getCodesetid()))
			{
				strSWhere.setLength(0);
				strSWhere.append(" ( codesetid='");
				strSWhere.append(temp.getCodesetid());
				strSWhere.append("' or codesetid='UM' ) ");
			}		
		
			dbWizard.updateRecord(destTab,srcTab,strJoin.toString(),strSet.toString(),toWhere,strSWhere.toString());			

			/**清空字符串*/
			strJoin.setLength(0);
			strSWhere.setLength(0);	
			strSet.setLength(0);
		}
	}
	
	/**
	 * 从档案库导入数据至创建的花名册表中
	 * @param hm
	 * @param tabname
	 * @param infor
	 * @param dbpre
	 * @throws GeneralException
	 */
	private void importData(HashMap  hm,String tabname,String infor,String dbpre)throws GeneralException
	{
		InfoGroup infogroup=new InfoGroup(Integer.parseInt(infor));	
		DbWizard dbWizard =new DbWizard(this.conn);	
		StringBuffer strJoin=new StringBuffer();
		StringBuffer strSWhere=new StringBuffer();
		String srcTab=null;
		try
		{
			Iterator iterator=hm.entrySet().iterator();		
		    while(iterator.hasNext())
		    {
		        java.util.Map.Entry entry = (java.util.Map.Entry) iterator.next();
		        String setname=entry.getKey().toString();
		        ArrayList list=(ArrayList)entry.getValue();
		        if("1".equals(infor)) {
                    srcTab=dbpre+setname;
                } else {
                    srcTab=setname;
                }
				
				strJoin.append(tabname);
				strJoin.append(".");
				if("1".equals(infor)) {
                    strJoin.append(infogroup.getKeyfield());
                } else {
                    strJoin.append(infogroup.getKeyfield()+"_CODE");
                }
				strJoin.append("=");
				strJoin.append(srcTab);
				strJoin.append(".");
				strJoin.append(infogroup.getKeyfield());
				
				String strset=getUpdateField(tabname,srcTab,list);
				if(strset!=null&&strset.trim().length()>1){
					if(infogroup.MainSetYesNo(setname))
					{
						dbWizard.updateRecord(tabname,srcTab,strJoin.toString(),strset,"","");
					}
					else
					{
						strSWhere.append(srcTab);
						strSWhere.append(".I9999=(select MAX(I9999) from ");
						strSWhere.append(srcTab);
						strSWhere.append(" where ");
						strSWhere.append(srcTab);
						strSWhere.append(".");
						strSWhere.append(infogroup.getKeyfield());
						strSWhere.append("=");
						strSWhere.append(tabname);
						strSWhere.append(".");
						if("1".equals(infor)) {
                            strSWhere.append(infogroup.getKeyfield());
                        } else {
                            strSWhere.append(infogroup.getKeyfield()+"_CODE");
                        }
						strSWhere.append(")");

						dbWizard.updateRecord(tabname,srcTab,strJoin.toString(),strset,"",strSWhere.toString());
					}				
				}
				/**清空字符串*/
				strJoin.setLength(0);
				strSWhere.setLength(0);	   
		    }//loop end while
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		  //  throw GeneralExceptionHandler.Handle(ex);				
		}
	}
	
	private String getTableid(String tabname){
		String tabid="";
		String[] arr = tabname.split("_");
		if(arr.length>0){
			String tab = arr[0];
			tabid=tab.replaceAll("m","").replaceAll("M","");
		}	
		return tabid;
	}
	
	private void importData3(String tabname,String infor,String dbpre,ArrayList fieldList,
			String userName,String strorder)throws GeneralException
	{
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			String repeat_mainset = "True";
			String musterid = getTableid(tabname);
			if(musterid!=null&&musterid.trim().length()>0){
				MusterXMLStyleBo xmltylebo = new MusterXMLStyleBo(conn,musterid);
				repeat_mainset = xmltylebo.getParamValue(MusterXMLStyleBo.Param,"repeat_mainset");
			}
			
			HashSet tabSet=new HashSet();
			ArrayList a_list=getFieldSet(fieldList);
			HashMap fieldMap=(HashMap)a_list.get(0);
			for (Iterator t = fieldList.iterator(); t.hasNext();) {
				Field field = (Field) t.next();
				String fieldName = field.getName().toLowerCase();
				String fieldSetName="";
				
				if(!"b0110".equalsIgnoreCase(fieldName)&&!"e01a1".equalsIgnoreCase(fieldName)) {
                    fieldSetName=(String)fieldMap.get(fieldName);
                } else if("1".equals(infor))
				{
						fieldSetName="A01";
					
				}else
				{
					
					if("b0110".equalsIgnoreCase(fieldName)) {
                        fieldSetName="B01";
                    } else if("e01a1".equalsIgnoreCase(fieldName)) {
                        fieldSetName="K01";
                    }
				}			
				tabSet.add(fieldSetName);
			}
			DbWizard dbWizard=new DbWizard(this.conn);
			DBMetaModel meta=new DBMetaModel(this.conn);
			
			String baseColumnName="a0100";
			//创建花名册结果表的备份表
			createTableBak(tabname,meta,userName);
			{
				String asetName="A01";
				if("2".equals(infor))
				{
					baseColumnName="b0110";
					asetName="B01";
				}
				else if("3".equals(infor))
				{
					baseColumnName="e01a1";
					asetName="K01";
				}
				creatTempTable(infor,dbWizard,asetName,dbpre,meta,userName);  //创建临时表 temp_userName_0（符合查询结果条件，并对i9999重新排序）
				insertData(userName,fieldList,infor,dbpre,asetName,"T#"+userName+"_mus0");//tabname+"_0"
			}
			for(Iterator t=tabSet.iterator();t.hasNext();)
			{
				String setName=(String)t.next();
				if(setName==null||setName.trim().length()<1) {
                    continue;
                }
				if("a01".equalsIgnoreCase(setName)|| "b01".equalsIgnoreCase(setName)|| "k01".equalsIgnoreCase(setName)) {
                    continue;
                }
				creatTempTable(infor,dbWizard,setName,dbpre,meta,userName);  //创建临时表 temp_userName_0（符合查询结果条件，并对i9999重新排序）
				insertData(userName,fieldList,infor,dbpre,setName,"T#"+userName+"_mus0");//tabname+"_0"
			}
			/*if(repeat_mainset!=null&&repeat_mainset.equalsIgnoreCase("True")){
				updateMainSet(dao,fieldList,"T#"+userName+"_mus0",dbpre);//tabname+"_0"
			}*/
			sortTemp(dao,"T#"+userName+"_mus","T#"+userName+"_mus0",infor,strorder);//tabname+"_0"
			String str="";
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
			{
				String sqlStr=" select * from "+tabname+" where 1=2";
				StringBuffer buf = new StringBuffer();
				RowSet rs = dao.search(sqlStr);
				ResultSetMetaData rsmd=rs.getMetaData();
				for(int i=1;i<rsmd.getColumnCount();i++)
				{
					String cloName=rsmd.getColumnName(i);
					if("recidx".equalsIgnoreCase(cloName)) {
                        continue;
                    }
					buf.append(","+cloName);
				}
				StringBuffer sbf=new StringBuffer();
				sbf.append(" order by ");
				if("false".equalsIgnoreCase(this.repeat_mainset)) {
                    sbf.append(" T#"+userName+"_mus.a0000 asc,");
                }
				if(this.sortlist.size()>0){
					for (int i = 0; i < this.sortlist.size(); i++) {
						SortFieldVo  sortfieldvo=(SortFieldVo)this.sortlist.get(i);
						sbf.append(" T#"+userName+"_mus0."+sortfieldvo.getField_name());
//						sbf.append(sortfieldvo.getField_name());
						if(sortfieldvo.getOrder_flag()==0) {
                            sbf.append(" asc");
                        } else {
                            sbf.append(" desc");
                        }
						if(i<this.sortlist.size()-1) {
                            sbf.append(", ");
                        }
					}
				}else{
					sbf.append(" recidx,i9999 ");
				}
				StringBuffer sql = new StringBuffer();
				//取临时表的列，添加别名T#sumus0.A0100,T#sumus0.B0110... 为与T#su_mus连接查询  
				String[] colums=buf.toString().substring(1).split(",");
				String   colum="";
				if(colums.length>0){
					for (int i = 0; i < colums.length; i++) {
						if(i<colums.length-1) {
                            colum+="T#"+userName+"_mus0."+colums[i]+",";
                        } else {
                            colum+="T#"+userName+"_mus0."+colums[i];
                        }
					}
				}
				sql.append(" insert into "+tabname+" (recidx"+buf.toString()+") ");
				sql.append("select RowNum,T#"+userName+"_mus0.* from (");
				//sql.append(" select "+buf.toString().substring(1));
				if (StringUtils.isEmpty(wheresql)&&StringUtils.isEmpty(wherestr)){					
					sql.append(" select "+colum);//T#su_mus 做连接查询 排序不使用recidx 改为T#su_mus的A0000排序
					//sql.append(" select "+buf.toString().substring(1));
					//sql.append(" from T#"+userName+"_mus0 order by recidx,i9999) T#"+userName+"_mus0");//tabname+"_0"
					sql.append(" from T#"+userName+"_mus0 left join T#"+userName+"_Mus on T#"+userName+"_mus0.a0100=T#"+userName+"_mus.a0100 "+sbf.toString()+" ) T#"+userName+"_mus0");
					//sql.append(" from T#"+userName+"_mus0 left join T#"+userName+"_Mus on T#"+userName+"_mus0.a0100=T#"+userName+"_mus.a0100  order by T#"+userName+"_mus.A0000,T#"+userName+"_mus0.i9999) T#"+userName+"_mus0");//tabname+"_0"

				}else {				
					sql.append(" select "+buf.toString().substring(1));
					sql.append(" from T#"+userName+"_mus0 ) T#"+userName+"_mus0");//tabname+"_0"
					if(isExistField) {
                        str = str + " where " + wheresql;
                    } else {
                        str = str + " left join (select "+ dbpre +"A01.A0100 "+wherestr+") A01 on "+"T#"+userName+"_mus0.A0100=A01.A0100 ";
                    }
					//sql.append(" order by recidx,i9999");
				}
				str=sql.toString();
			}
			else
			{
				/** 某些sql2008/2012库，执行insert into ... select ... from ... order by ...后，目的表的记录顺序不正确。
				 *   解决方法：先在目的表增加自增长字段，再导入数据，顺序是对的。
				 */
		    	if(Sql_switcher.searchDbServer()==Constant.MSSQL)
		    	{
		    		// 先增加自增长字段
		    		String sql="alter table " + tabname + " add xxx int identity(1,1)";
					DbWizard db=new DbWizard(this.conn);
					db.execute(sql);
		    	}
				String flds=getFlds("T#"+userName+"_mus0");
				str="insert into "+tabname+"("+flds+") select "+flds+" from T#"+userName+"_mus0 ";
				if (StringUtils.isNotEmpty(wheresql)&&StringUtils.isNotEmpty(wherestr)){
					if(isExistField) {
                        str = str + " where " + wheresql;
                    } else {
                        str = str + " left join (select "+ dbpre +"A01.A0100 "+wherestr+") A01 on "+"T#"+userName+"_mus0.A0100=A01.A0100 ";
                    }
				}
				//str = str + " order by recidx,i9999";
				//str = str + " order by recidx,i9999";
				StringBuffer sbf=new StringBuffer();
				sbf.append(" order by ");
				if("false".equalsIgnoreCase(this.repeat_mainset)) {
                    sbf.append("recidx, ");
                }
				for (int i = 0; i < this.sortlist.size(); i++) {
					SortFieldVo  sortfieldvo=(SortFieldVo)this.sortlist.get(i);
					sbf.append(sortfieldvo.getField_name());
					if(sortfieldvo.getOrder_flag()==0) {
                        sbf.append(" asc");
                    } else {
                        sbf.append(" desc");
                    }
					if(i<this.sortlist.size()-1) {
                        sbf.append(", ");
                    }
				}
				if(this.sortlist.size()>0) {
                    str=str+sbf.toString();
                } else {
                    str = str + " order by recidx,i9999";
                }
			}
			//System.out.println(str);
			dao.update(str);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 更新序号指标recidx
	 */
	private void updateSortField(String table)
	{
		try
		{
			StringBuffer buf=new StringBuffer();
			ContentDAO dao=new ContentDAO(this.conn);
			if(Sql_switcher.searchDbServer()!=Constant.ORACEL){
				buf.setLength(0);
				buf.append("alter table ");
				buf.append(table);
				buf.append(" add recidx int identity(1,1)");
				dao.update(buf.toString());
			}
		}
		catch(Exception ex)
		{
			
		}
	}
	
	/**
	 * 取表中字段列表
	 * @param tabname
	 * @return
	 */
	private String getFlds(String tabname){
		String sqlStr="select * from "+tabname+" where 1=2";
		StringBuffer buf = new StringBuffer();
		ContentDAO dao=new ContentDAO(conn);
		try{
			RowSet rs = dao.search(sqlStr);
			ResultSetMetaData rsmd=rs.getMetaData();
			for(int i=1;i<rsmd.getColumnCount();i++)
			{
				String cloName=rsmd.getColumnName(i);
				//if(cloName.equalsIgnoreCase("recidx"))
					//continue;
				if(buf.length()>0) {
                    buf.append(",");
                }
				buf.append(tabname+"."+cloName);
			}
		}
		catch(Exception e){}
		return buf.toString();
	}
	
	private void sortTemp(ContentDAO dao,String fromtable,String toTable,String infor,String sortStr){
		try {
			String itemid = "A0100";
			if("2".equals(infor)) {
                itemid = "B0110";
            } else if("3".equals(infor)) {
                itemid = "E01A1";
            }
			/*
			StringBuffer sqlstr = new StringBuffer();
			sqlstr.append("select ");
			sqlstr.append(itemid);
			sqlstr.append(" from ");
			sqlstr.append(fromtable);
			sqlstr.append(" "+sortStr);
			int i=1;
			ArrayList listvalue = new ArrayList();
			RowSet rs = dao.search(sqlstr.toString());
			while(rs.next()){
				ArrayList list = new ArrayList();
				list.add(i+"");
				list.add(rs.getString(itemid));
				listvalue.add(list);
				i++;
			}
			sqlstr.setLength(0);
			sqlstr.append("update ");
			sqlstr.append(toTable);
			sqlstr.append(" set recidx=? where ");
			sqlstr.append(itemid);
			sqlstr.append("=?");
			dao.batchUpdate(sqlstr.toString(), listvalue);
			*/
			StringBuffer sqlstr = new StringBuffer();
			if(Sql_switcher.searchDbServer()!=Constant.ORACEL){
				sqlstr.setLength(0);//清空
				sqlstr.append("alter table ");
				sqlstr.append(fromtable);
				sqlstr.append(" add recidx int identity(1,1)");
				dao.update(sqlstr.toString());//添加序号列
			}
			sqlstr.setLength(0);//清空
			sqlstr.append("update ");
			sqlstr.append(toTable);
			if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                sqlstr.append(" set recidx=(select RowNum from ");
            } else {
                sqlstr.append(" set recidx=(select recidx from ");
            }
			sqlstr.append(fromtable);
			sqlstr.append(" where ");
			sqlstr.append(fromtable +"."+ itemid);
			sqlstr.append("="+ toTable +"."+itemid);
			sqlstr.append(")");
			dao.update(sqlstr.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	private void updateMainSet(ContentDAO dao,ArrayList fieldList,String tabname,String dbpre){
		for (Iterator t = fieldList.iterator(); t.hasNext();) {
			Field field = (Field) t.next();			
			String fieldName = field.getName().toLowerCase();
			FieldItem fielditem = DataDictionary.getFieldItem(fieldName);
			
			
			if(fielditem!=null&&fielditem.isMainSet()){//当前指标是主集
				String fieldsetid = fielditem.getFieldsetid();
//				String setname = dbpre+fieldsetid;
				StringBuffer buf = new StringBuffer();
				buf.append("update "+tabname);
				buf.append(" set ");
				buf.append(fieldName+"=(select ");
				buf.append(fieldName);
				buf.append(" from ");
				buf.append(tabname);
				buf.append(" a where ");
				if("A".equalsIgnoreCase(fieldsetid.substring(0,1))){
					buf.append("a.A0100="+tabname+".A0100");
				}else if("B".equalsIgnoreCase(fieldsetid.substring(0,1))){
					buf.append("a.B0110="+tabname+".B0110");
				}else if("K".equalsIgnoreCase(fieldsetid.substring(0,1))){
					buf.append("a.E01A1="+tabname+".E01A1");
				}else{
					buf.append("a.A0100="+tabname+".A0100");
				}
				buf.append(" and a."+fieldName+" is not null)");
				try {
					dao.update(buf.toString());
//					System.out.println(buf.toString());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void insertData(String userName,ArrayList fieldList,String infor,String dbpre,String setName,String toTableName)
	{
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet recset = null;
		try
		{
			
			ArrayList a_list=getFieldSet(fieldList);
			HashMap fieldMap=(HashMap)a_list.get(0);
			HashMap typeMap=(HashMap)a_list.get(1);
			
			StringBuffer insert_str=new StringBuffer("");
			StringBuffer select_str=new StringBuffer("");
			StringBuffer update_str=new StringBuffer("");
			StringBuffer strbuf=new StringBuffer("");
			String newTempTableName="T#"+userName+"_mus3";
			
			String baseColumnName="a0100";
			String ToBaseColumnName="a0100";
			if("2".equals(infor))
			{
				baseColumnName="b0110";
				ToBaseColumnName="b0110_code";
			}
			else if("3".equals(infor))
			{
				ToBaseColumnName="e01a1_code";
				baseColumnName="e01a1";
			}
			String insertSql="";
			String orclSql="";//oracle 插入指标 不应带入c（别名）
			String selectSql="";//
			for (Iterator t = fieldList.iterator(); t.hasNext();) {
				Field field = (Field) t.next();			
				String fieldName = field.getName().toLowerCase();
				if(!"a0100".equalsIgnoreCase(fieldName)){
					String type=(String)typeMap.get(fieldName);	
					if("a01".equalsIgnoreCase((String)fieldMap.get(fieldName))||
					   "b01".equalsIgnoreCase((String)fieldMap.get(fieldName))||
					   "k01".equalsIgnoreCase((String)fieldMap.get(fieldName))||
					   "b0110".equalsIgnoreCase(fieldName)|| "e01a1".equalsIgnoreCase(fieldName)	){
						if(type!=null&& "D".equals(type)){
							selectSql+= Sql_switcher.dateToChar("c."+fieldName, "yyyy-mm-dd") +",";	
						}else{
							selectSql+="c."+fieldName+",";
						}
						insertSql+= "c."+fieldName+",";	
					}
					/*if("b01".equalsIgnoreCase((String)fieldMap.get(fieldName))){
						insertSql+="c."+fieldName+",";
					}				
					if("k01".equalsIgnoreCase((String)fieldMap.get(fieldName))){
						insertSql+="c."+fieldName+",";
					}
					if(fieldName.equalsIgnoreCase("b0110")||fieldName.equalsIgnoreCase("e01a1")){
						insertSql+="c."+fieldName+",";
					}*/
					
					if(Sql_switcher.searchDbServer()==Constant.ORACEL){
						if("a01".equalsIgnoreCase((String)fieldMap.get(fieldName))||
						   "b01".equalsIgnoreCase((String)fieldMap.get(fieldName))||
						   "k01".equalsIgnoreCase((String)fieldMap.get(fieldName))||
						   "b0110".equalsIgnoreCase(fieldName)|| "e01a1".equalsIgnoreCase(fieldName)){
							/*if(type!=null&&type.equals("D")){ 31583	员工管理：花名册，有；两个信息集中的指标，取所有的历史记录，历史记录取不出来，后台 报值太多。
								selectSql+=Sql_switcher.dateToChar(fieldName, "yyyy-mm-dd")+",";
							}else{
								selectSql+=fieldName+",";
							}*/
							orclSql+=fieldName+",";
						}
						/*if("b01".equalsIgnoreCase((String)fieldMap.get(fieldName))){
							orclSql+=fieldName+",";
						}				
						if("k01".equalsIgnoreCase((String)fieldMap.get(fieldName))){
							orclSql+=fieldName+",";
						}
						if(fieldName.equalsIgnoreCase("b0110")||fieldName.equalsIgnoreCase("e01a1")){
							orclSql+=fieldName+",";
						}*/
					}
				}
				
				
				if(fieldName.equalsIgnoreCase(ToBaseColumnName)) {
                    continue;
                }
				if ("recidx".equals(fieldName))
				{
					insert_str.append(","+fieldName);
					select_str.append(",1");
					update_str.append(",recidx=1");
				}
				else
				{
					String type=(String)typeMap.get(fieldName);					
					String fieldSetName="";
					if(!"b0110".equalsIgnoreCase(fieldName)&&!"e01a1".equalsIgnoreCase(fieldName)) {
                        fieldSetName=(String)fieldMap.get(fieldName);
                    } else if("1".equals(infor))
					{
							fieldSetName="A01";
						
					}else
					{
						
						if("b0110".equalsIgnoreCase(fieldName)) {
                            fieldSetName="B01";
                        } else if("e01a1".equalsIgnoreCase(fieldName)) {
                            fieldSetName="K01";
                        }
					}
					
					if(!fieldSetName.equalsIgnoreCase(setName)) {
                        continue;
                    }
					
					insert_str.append(","+fieldName);
					String columnName="";
					if("1".equals(infor))
					{			
							
							if(type!=null&& "D".equals(type))
							{
								select_str.append(","+Sql_switcher.dateToChar(newTempTableName+"."+fieldName,"yyyy-mm-dd"));
								columnName=Sql_switcher.dateToChar(fieldName,"yyyy-mm-dd");
							}
							else if(type!=null&& "N".equals(type))
							{
								select_str.append(","+Sql_switcher.numberToChar(newTempTableName+"."+fieldName));
								columnName=Sql_switcher.numberToChar(fieldName);
							}
							else 
							{
								select_str.append(","+newTempTableName+"."+fieldName);
								columnName=fieldName;
							}							
					}
					else 
					{
						
						if("b0110_code".equals(fieldName.toLowerCase()))
						{
							select_str.append(","+newTempTableName+"."+"B0110");
							columnName="b0110";
						}
						else if("e01a1_code".equals(fieldName.toLowerCase()))
						{
							select_str.append(","+newTempTableName+".E01A1");
							columnName="e01a1";
						}
						else
						{
							if(type!=null&& "D".equals(type))
							{
								select_str.append(","+Sql_switcher.dateToChar(newTempTableName+"."+fieldName,"yyyy-mm-dd"));
								columnName=Sql_switcher.dateToChar(fieldName,"yyyy-mm-dd");
							}
							else if(type!=null&& "N".equals(type))
							{
								select_str.append(","+Sql_switcher.numberToChar(newTempTableName+"."+fieldName));
								columnName=Sql_switcher.numberToChar(fieldName);
							}
							else 
							{
								select_str.append(","+newTempTableName+"."+fieldName);
								columnName=fieldName;
							}
						}
						
					}	
					if(Sql_switcher.searchDbServer()==Constant.MSSQL){						
						update_str.append(","+fieldName+"="+newTempTableName+"."+fieldName);
						strbuf.append(","+columnName+" "+fieldName);
					}else if (Sql_switcher.searchDbServer()==Constant.ORACEL) {						
						if("a01".equalsIgnoreCase(setName)|| "b01".equalsIgnoreCase(setName)|| "k01".equalsIgnoreCase(setName)) {
                            update_str.append(","+fieldName+"=(select "+columnName+" from "+newTempTableName+" where "+toTableName+"."+ToBaseColumnName+"="+newTempTableName+"."+baseColumnName+" )");
                        } else {
                            update_str.append(","+fieldName+"=(select "+columnName+" from "+newTempTableName+" where "+toTableName+"."+ToBaseColumnName+"="+newTempTableName+"."+baseColumnName+" and "+toTableName+".i9999="+newTempTableName+".i9999)");
                        }
					}
				}
			}
			String update="";
			if(Sql_switcher.searchDbServer()==Constant.MSSQL){				
				update="update "+toTableName+" set "+update_str.substring(1)+" from (select "+ strbuf.substring(1) +" from "+ newTempTableName +") "+ newTempTableName;
				if("a01".equalsIgnoreCase(setName)|| "b01".equalsIgnoreCase(setName)|| "k01".equalsIgnoreCase(setName)){
					update="update "+toTableName+" set "+update_str.substring(1)+" from (select "+baseColumnName+ strbuf.toString() +" from "+ newTempTableName +") "+ newTempTableName;
					update += (" where "+toTableName+"."+ToBaseColumnName+"="+newTempTableName+"."+baseColumnName);
				}else{				
					update="update "+toTableName+" set "+update_str.substring(1)+" from (select "+baseColumnName+","+"i9999"+ strbuf.toString() +" from "+ newTempTableName +") "+ newTempTableName;
					update += (" where "+toTableName+"."+ToBaseColumnName+"="+newTempTableName+"."+baseColumnName+" and "+toTableName+".i9999="+newTempTableName+".i9999");
				}
			}else if (Sql_switcher.searchDbServer()==Constant.ORACEL) {				
				update="update "+toTableName+" set "+update_str.substring(1);
			}
			String insert="";
			if("a01".equalsIgnoreCase(setName)|| "b01".equalsIgnoreCase(setName)|| "k01".equalsIgnoreCase(setName))
			{
				insert="insert into "+toTableName+"(i9999,"+ToBaseColumnName+insert_str.toString()+") select 1,"+newTempTableName+"."+baseColumnName+select_str.toString()
    			+" from "+newTempTableName;
	
			}
			else
			{
				if("true".equalsIgnoreCase(this.repeat_mainset)){//历史包含主集
					
					String setNames="";
					if("1".equalsIgnoreCase(infor)){
						setNames=dbpre+"A01";
					}else if("2".equalsIgnoreCase(infor)){
						setNames="B01";
					}else if("3".equalsIgnoreCase(infor)) {
                        setNames="K01";
                    }
					if(Sql_switcher.searchDbServer()==Constant.MSSQL) {
                        insert="insert into "+toTableName+"("+insertSql+"i9999,"+ToBaseColumnName+insert_str.toString()+") select "+selectSql+""+newTempTableName+".i9999,"+newTempTableName+"."+baseColumnName+select_str.toString()
                        +" from "+newTempTableName+" ,"+setNames+" c where c.a0100="+newTempTableName+".a0100 and not exists (select * from "+toTableName+" where "+newTempTableName+".i9999="+toTableName+".i9999 and "+toTableName+"."+ToBaseColumnName+"="+newTempTableName+"."+baseColumnName+")";
                    } else {
                        insert="insert into "+toTableName+"("+orclSql+"i9999,"+ToBaseColumnName+insert_str.toString()+") select "+selectSql+""+newTempTableName+".i9999,"+newTempTableName+"."+baseColumnName+select_str.toString()
                        +" from "+newTempTableName+" ,"+setNames+" c where c.a0100="+newTempTableName+".a0100 and not exists (select * from "+toTableName+" where "+newTempTableName+".i9999="+toTableName+".i9999 and "+toTableName+"."+ToBaseColumnName+"="+newTempTableName+"."+baseColumnName+")";
                    }
				}else{
					insert="insert into "+toTableName+"(i9999,"+ToBaseColumnName+insert_str.toString()+") select "+newTempTableName+".i9999,"+newTempTableName+"."+baseColumnName+select_str.toString()
	    			+" from "+newTempTableName+" where not exists (select * from "+toTableName+" where "+newTempTableName+".i9999="+toTableName+".i9999 and "+toTableName+"."+ToBaseColumnName+"="+newTempTableName+"."+baseColumnName+")";

				}
					
				/**
				 * 				insert="insert into "+toTableName+"(b0110,e0122,e01a1,i9999,"+ToBaseColumnName+insert_str.toString()+") select  c.b0110,c.e0122,c.e01a1,"+newTempTableName+".i9999,"+newTempTableName+"."+baseColumnName+select_str.toString()
		    			+" from "+newTempTableName+",Usra01 c where c.a0100="+newTempTableName+".a0100 and not exists (select * from "+toTableName+" where "+newTempTableName+".i9999="+toTableName+".i9999 and "+toTableName+"."+ToBaseColumnName+"="+newTempTableName+"."+baseColumnName+")";

				 * */
			}		
			dao.update(update);
			dao.insert(insert,new ArrayList());
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	
	public void dropTable(String tabname,DbWizard dbWizard)
	{
		try
		{
			
			if(dbWizard.isExistTable(tabname,false))
			{
				Table table=new  Table(tabname);
				dbWizard.dropTable(table);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	public void createTableBak(String tabname,DBMetaModel meta,String userName)
	{
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			String sql0="";
			dropTable("T#"+userName+"_mus0",new DbWizard(this.conn));;
			if(Sql_switcher.searchDbServer()==2)
			{
				sql0="create table T#"+userName+"_mus0 as select * from "+tabname+" where 1=2";
			}
			else
			{			
				sql0="select *  into T#"+userName+"_mus0  from "+tabname+" where 1=2";
			}
			dao.update(sql0);
			meta.reloadTableModel("T#"+userName+"_mus0");
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * 
	 * @param infor
	 * @param dbWizard
	 * @param setName
	 */
	public void creatTempTable(String infor,DbWizard dbWizard,String setName,String dbpre,DBMetaModel dbmodel,String userName)
	{
		
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		String sql="";
		String sql0="";
		String tableName="";
		String newTempTableName="T#"+userName;
		try
		{
			if("1".equals(infor)) {
                tableName=dbpre+setName;
            } else {
                tableName=setName;
            }
			String cond="";
			String baseColumnName="";
			if("1".equals(infor))
			{
				cond=" a0100 in (select a0100 from T#"+userName+"_mus )";
				baseColumnName="a0100";
			}
		    if("2".equals(infor))
		    {
		    	baseColumnName="b0110";
		    	cond=" b0110 in (select b0110 from T#"+userName+"_mus )";
		    }
		    if("3".equals(infor))
		    {
		    	baseColumnName="e01a1";
		    	cond=" e01a1 in (select e01a1 from T#"+userName+"_mus )";
		    }
		    String str="";
			if(Sql_switcher.searchDbServer()==2)
			{
				sql="create table "+newTempTableName+"_mus2 as select * from "+tableName+" where "+cond;
				sql0="create table "+newTempTableName+"_mus3 as select * from "+tableName+" where 1=2";
			}
			else
			{
				sql="select *  into "+newTempTableName+"_mus2  from "+tableName+" where "+cond;
				sql0="select *  into "+newTempTableName+"_mus3  from "+tableName+" where 1=2";
			}
			if(dbWizard.isExistTable(newTempTableName+"_mus2",false)) {
                dbWizard.dropTable(new Table(newTempTableName+"_mus2"));
            }
			if(dbWizard.isExistTable(newTempTableName+"_mus3",false)) {
                dbWizard.dropTable(new Table(newTempTableName+"_mus3"));
            }
			dao.update(sql);
			dao.update("create index "+ newTempTableName +"_mus2_a0100 on "+ newTempTableName +"_mus2 (a0100) ");
			dao.update(sql0);
			dao.update("create index "+ newTempTableName +"_mus3_a0100 on "+ newTempTableName +"_mus3 (a0100) ");
			//dbmodel=new DBMetaModel(this.conn);
			dbmodel.reloadTableModel(newTempTableName+"_mus2");
			dbmodel.reloadTableModel(newTempTableName+"_mus3");
			int maxI9999=0;
			if("A01".equalsIgnoreCase(setName)|| "B01".equalsIgnoreCase(setName)|| "K01".equalsIgnoreCase(setName))
			{
				StringBuffer sqll=new StringBuffer("insert into "+newTempTableName+"_mus3 select * from "+newTempTableName+"_mus2");
				dao.update(sqll.toString());
			}
			else
			{
				StringBuffer sql_str0=new StringBuffer("");
				StringBuffer sql_str1=new StringBuffer("");					
				recset=dao.search("select * from "+tableName+" where 1=2");
				ResultSetMetaData date=recset.getMetaData();
				int columnCount=date.getColumnCount();
				for(int a=1;a<=columnCount;a++)
				{
					String columnName=date.getColumnName(a);
					sql_str0.append(","+columnName);
					if("i9999".equalsIgnoreCase(columnName)) {
						//原因：取子集i9999 分组重新排序 防止子集记录删除情况存在，（取消采用查询_mus2临时表中最大记录数 依次遍历插入数据，频繁查询插入操作会影响性能）
						sql_str1.append(",ROW_NUMBER() OVER(PARTITION BY "+newTempTableName+"_mus2.a0100 ORDER BY "+newTempTableName+"_mus2.i9999) i9999 ");
					}
					else if(columnName.equalsIgnoreCase(baseColumnName))
					{
						sql_str1.append(","+newTempTableName+"_mus2."+columnName);
					}
					else {
                        sql_str1.append(","+columnName);
                    }
					
				}
				StringBuffer sqll=new StringBuffer("insert into "+newTempTableName+"_mus3  ("+sql_str0.substring(1)+")  select "+sql_str1.substring(1));
				sqll.append(" from "+newTempTableName+"_mus2 ");
				//sqll.append("  where "+newTempTableName+"_mus2."+baseColumnName+"=a."+baseColumnName+" and "+newTempTableName+"_mus2.i9999=a.i9999");
				dao.update(sqll.toString());
				sqll.setLength(0);
				sqll.append("delete from "+newTempTableName+"_mus2 ");
				dao.delete(sqll.toString(),new ArrayList());
				
				//maxI9999=getMaxI9999(newTempTableName+"_mus2");
				/*for(int i=1;i<=maxI9999;i++)
				{
					StringBuffer sql_str0=new StringBuffer("");
					StringBuffer sql_str1=new StringBuffer("");					
					recset=dao.search("select * from "+tableName+" where 1=2");
					ResultSetMetaData date=recset.getMetaData();
					int columnCount=date.getColumnCount();
					for(int a=1;a<=columnCount;a++)
					{
						String columnName=date.getColumnName(a);
						sql_str0.append(","+columnName);
						if(columnName.equalsIgnoreCase("i9999"))
							sql_str1.append(","+i);
						else if(columnName.equalsIgnoreCase(baseColumnName))
						{
							sql_str1.append(","+newTempTableName+"_mus2."+columnName);
						}
						else
							sql_str1.append(","+columnName);
						
					}
					StringBuffer sqll=new StringBuffer("insert into "+newTempTableName+"_mus3  ("+sql_str0.substring(1)+")  select "+sql_str1.substring(1));
					sqll.append(" from "+newTempTableName+"_mus2,(select "+baseColumnName+",min(i9999)i9999 from "+newTempTableName+"_mus2 group by "+baseColumnName+" ) a");
					sqll.append("  where "+newTempTableName+"_mus2."+baseColumnName+"=a."+baseColumnName+" and "+newTempTableName+"_mus2.i9999=a.i9999");
					dao.update(sqll.toString());
					sqll.setLength(0);
					sqll.append("delete from "+newTempTableName+"_mus2  where i9999=(select min(b.i9999) from "+newTempTableName+"_mus2 b where "+newTempTableName+"_mus2."+baseColumnName+"=b."+baseColumnName+" )");
					dao.delete(sqll.toString(),new ArrayList());
				}*/
			}		
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	public int getMaxI9999(String tablename)
	{
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		int max=0;
		try
		{
			recset=dao.search("select max(i9999) from "+tablename);
			if(recset.next()) {
                max=recset.getInt(1);
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return max;
	}
	
	
	
	/**
	 * 从档案库导入数据至创建的花名册表中(包含历史纪录)
	 * @param hm
	 * @param tabname
	 * @param infor
	 * @param dbpre
	 * @throws GeneralException
	 */
	private void importData2(String tabname,String infor,String dbpre,ArrayList fieldList,String userName,String strorder)throws GeneralException
	{
//		ContentDAO dao = new ContentDAO(this.conn);
//		RowSet recset = null;
		try
		{
			importData3(tabname,infor,dbpre,fieldList,userName,strorder);
			/*
			ArrayList a_list=getFieldSet(fieldList);
			HashMap fieldMap=(HashMap)a_list.get(0);
			HashMap typeMap=(HashMap)a_list.get(1);
			StringBuffer insert_str=new StringBuffer("");
			StringBuffer select_str=new StringBuffer("");
			HashSet tabSet=new HashSet();
			
			for (Iterator t = fieldList.iterator(); t.hasNext();) {
				Field field = (Field) t.next();
				
				String fieldName = field.getName().toLowerCase();
				
				if (fieldName.equals("recidx"))  
				{
					insert_str.append(","+fieldName);
					select_str.append(",1");
				}
				else
				{
					String type=(String)typeMap.get(fieldName);
					insert_str.append(","+fieldName);
					String fieldSetName="";
					if(!fieldName.equals("b0110")&&!fieldName.toLowerCase().equals("e01a1"))
						fieldSetName=(String)fieldMap.get(fieldName);
					else if(fieldName.equals("b0110"))
						fieldSetName="B01";
					else if(fieldName.toLowerCase().equals("e01a1"))
						fieldSetName="K01";
					if(infor.equals("1"))
					{		
						if(fieldName.equals("e01a1")||fieldName.equals("e0122")||fieldName.equals("b0110"))
						{
							if(type!=null&&type.equals("D"))
							{
								select_str.append(","+Sql_switcher.dateToChar(dbpre+"A01."+fieldName,"yyyy-mm-dd"));
							}
							else if(type!=null&&type.equals("N"))
							{
								select_str.append(","+Sql_switcher.numberToChar(dbpre+"A01."+fieldName));
							}
							else 
								select_str.append(","+dbpre+"A01."+fieldName);
							fieldSetName=dbpre+"A01";
						}
						else
						{
							if(type!=null&&type.equals("D"))
							{
								select_str.append(","+Sql_switcher.dateToChar(dbpre+fieldSetName+"."+fieldName,"yyyy-mm-dd"));
							}
							else if(type!=null&&type.equals("N"))
							{
								select_str.append(","+Sql_switcher.numberToChar(dbpre+fieldSetName+"."+fieldName));
							}
							else 
								select_str.append(","+dbpre+fieldSetName+"."+fieldName);					
							fieldSetName=dbpre+fieldSetName;
						}
					}
					else 
					{
						if(fieldName.toLowerCase().equals("b0110_code"))
							select_str.append(","+fieldSetName+".B0110");
						else if(fieldName.toLowerCase().equals("e01a1_code"))
							select_str.append(","+fieldSetName+".E01A1");
						else
						{
							if(type!=null&&type.equals("D"))
							{
								select_str.append(","+Sql_switcher.dateToChar(fieldSetName+"."+fieldName,"yyyy-mm-dd"));
							}
							else if(type!=null&&type.equals("N"))
							{
								select_str.append(","+Sql_switcher.numberToChar(fieldSetName+"."+fieldName));
							}
							else 
								select_str.append(","+fieldSetName+"."+fieldName);
						}
					}
					tabSet.add(fieldSetName);
				}
			}
			StringBuffer sql=new StringBuffer("insert into "+tabname+" ( "+insert_str.substring(1)+" ) select "+select_str.substring(1)+" from m_idx_"+userName);
			
			
			
			
			if(infor.equals("1"))
				sql.append(" left join "+dbpre+"A01 on m_idx_"+userName+".a0100="+dbpre+"A01.a0100");
			else if(infor.equals("2"))
				sql.append(" left join B01 on m_idx_"+userName+".B0110=B01.B0110");
			else
				sql.append(" left join K01 on m_idx_"+userName+".E01A1=K01.E01A1");
			
			
			
			for(Iterator t=tabSet.iterator();t.hasNext();)
			{
				String setName=(String)t.next();
				if(setName!=null&&infor.equals("1")&&!setName.equals(dbpre+"A01"))
				{
					if(setName.length()>dbpre.length()+1)
					{
						sql.append(" left join "+setName+" on m_idx_"+userName+".a0100="+setName+".a0100");
					}
					else if(setName.charAt(0)=='B')
					{
						sql.append(" left join "+setName+" on "+dbpre+"A01.B0110="+setName+".B0110");
						
					}
					else if(setName.charAt(0)=='K')
					{
						sql.append(" left join "+setName+" on "+dbpre+"A01.E01A1="+setName+".E01A1");
						
					}
				}
				else if(setName!=null&&infor.equals("2")&&!setName.equals("B01"))
				{
					sql.append(" left join "+setName+" on  m_idx_"+userName+".B0110="+setName+".B0110");		
					
				}
				else if(setName!=null&&infor.equals("3")&&!setName.equals("K01"))
				{
					sql.append(" left join "+setName+" on  m_idx_"+userName+".E01A1="+setName+".E01A1");	
				}

			}
			sql.append(" "+strorder);
			System.out.println(sql.toString());
			dao.insert(sql.toString(),new ArrayList());*/
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		    throw GeneralExceptionHandler.Handle(ex);				
		}
	}
	
	
	
	
	
	//得到指标对应的指标集
	private ArrayList getFieldSet(ArrayList fieldList)throws GeneralException
	{
		ArrayList list=new ArrayList();
		HashMap map=new HashMap();
		HashMap typeMap=new HashMap();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet recset = null;
		try
		{
		   StringBuffer str=new StringBuffer("");
		   for(Iterator t=fieldList.iterator();t.hasNext();)
		   {
			   Field field=(Field)t.next();
			   String fieldName=field.getName();
			   if(!"A0100".equals(fieldName)&&!"recidx".equals(fieldName)&&!"B0110_CODE".equals(fieldName)&&!"E01A1_CODE".equals(fieldName))
			   {
				   str.append(",'"+fieldName+"'");
			   }
		   }
		   recset=dao.search("select itemid,fieldsetid,itemtype from fielditem where itemid in ("+str.substring(1)+")");
		   while(recset.next())
		   {
			   typeMap.put(recset.getString("itemid").toLowerCase(),recset.getString("itemtype"));
			   map.put(recset.getString("itemid").toLowerCase(),recset.getString("fieldsetid"));
		   }
		   map.put("a0100","A01");
		   map.put("b0110_code","B01");
		   map.put("e01a1_code","K01");
		   
		   list.add(map);
		   list.add(typeMap);
		   
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		    throw GeneralExceptionHandler.Handle(ex);				
		}
		return list;
	}
	
	
	
	
	/**
	 * 按选择对象列表创建排序表的数据
	 * @param infor
	 * @param dbpre
	 * @param username
	 * @param strWhere    for example '010101','0303'
	 */
    private void fillOutOrderDataByWhere(String infor,String dbpre,String username,ArrayList sqlList)
    {
		InfoGroup infogroup=new InfoGroup(Integer.parseInt(infor));
		String idx_table="T#"+username+"_mus";
		String mainset=dbpre+infogroup.getMainset();
	
		/**导入键值指标*/
		StringBuffer strsql=new StringBuffer();
		strsql.append("insert into ");
		strsql.append(idx_table);
		strsql.append("(");
		strsql.append(infogroup.getKeyfield());
		strsql.append(") select distinct ");
		strsql.append(mainset);
		strsql.append(".");
		strsql.append(infogroup.getKeyfield());
		strsql.append(" from ");
		strsql.append(mainset);
		strsql.append(" where  ");
		for(int i=0;i<sqlList.size();i++) {
			if(i!=0) {
                strsql.append(" or ");
            }
			strsql.append(infogroup.getKeyfield());
			strsql.append(" in (");
			strsql.append(sqlList.get(i).toString());
			strsql.append(")");	   
		}
		
		cat.debug("idx_table sql="+strsql.toString());
		
		ContentDAO dao=new ContentDAO(this.conn);
		DbWizard dbWizard =new DbWizard(this.conn);
		try
		{
			
			/**导入主键值*/
			dao.update(strsql.toString());
			/**导入排序指标值*/
			String destTab=idx_table;			
			StringBuffer strJoin=new StringBuffer();
			StringBuffer strSet=new StringBuffer();
			StringBuffer strSWhere=new StringBuffer();

			String srcTab=null;
			for(int i=0;i<this.sortlist.size();i++)
			{
				SortFieldVo vo=(SortFieldVo)this.sortlist.get(i);
				srcTab=dbpre+vo.getSet_name();	
				/**B0110分析有误,default为A01;E01A1,default也为A01*/
				if("B0110".equalsIgnoreCase(vo.getField_name())&& "2".equals(infor)) {
                    srcTab="B01";
                }
				if(("E01A1".equalsIgnoreCase(vo.getField_name())|| "E0122".equalsIgnoreCase(vo.getField_name())|| "B0110".equalsIgnoreCase(vo.getField_name()))&& "3".equals(infor)) {
                    srcTab="K01";
                }
				
				strJoin.append(destTab);
				strJoin.append(".");
				strJoin.append(infogroup.getKeyfield());
				strJoin.append("=");
				strJoin.append(srcTab);
				strJoin.append(".");
				strJoin.append(infogroup.getKeyfield());
				
				strSet.append(destTab);
				strSet.append(".");
				strSet.append(vo.getField_name());
				strSet.append("=");
				strSet.append(srcTab);
				strSet.append(".");
				strSet.append(vo.getField_name());
				/**主集*/
				if(infogroup.MainSetYesNo(vo.getSet_name()))
				{   
								
					dbWizard.updateRecord(destTab,srcTab,strJoin.toString(),strSet.toString(),"","");
				}
				else
				{
					strSWhere.append(srcTab);
					strSWhere.append(".I9999=(select MAX(I9999) from ");
					strSWhere.append(srcTab);
					strSWhere.append(" where ");
					strSWhere.append(srcTab);
					strSWhere.append(".");
					strSWhere.append(infogroup.getKeyfield());
					strSWhere.append("=");
					strSWhere.append(destTab);
					strSWhere.append(".");
					strSWhere.append(infogroup.getKeyfield());
					strSWhere.append(")");
					dbWizard.updateRecord(destTab,srcTab,strJoin.toString(),strSet.toString(),"",strSWhere.toString());
				}
				/**清空字符串*/
				strJoin.setLength(0);
				strSet.setLength(0);
				strSWhere.setLength(0);
			}//for loop end.
			/**导入a0000*/
			strJoin.setLength(0);
			strSet.setLength(0);
			strSWhere.setLength(0);			
			if("1".equals(infor))
			{
				srcTab=dbpre+"A01";
				strJoin.append(destTab);
				strJoin.append(".");
				strJoin.append(infogroup.getKeyfield());
				strJoin.append("=");
				strJoin.append(srcTab);
				strJoin.append(".");
				strJoin.append(infogroup.getKeyfield());
				strSet.append(destTab);
				strSet.append(".a0000");
				strSet.append("=");
				strSet.append(srcTab);
				strSet.append(".a0000");
				dbWizard.updateRecord(destTab,srcTab,strJoin.toString(),strSet.toString(),"","");
			}
			else
			{
				srcTab="organization";
				strJoin.append(destTab);
				strJoin.append(".");
				strJoin.append(infogroup.getKeyfield());
				strJoin.append("=");
				strJoin.append(srcTab);
				strJoin.append(".codeitemid");
				strSet.append(destTab);
				strSet.append(".a0000");
				strSet.append("=");
				strSet.append(srcTab);
				strSet.append(".a0000");
				dbWizard.updateRecord(destTab,srcTab,strJoin.toString(),strSet.toString(),"","");
			}
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}    	
    }
	/**
	 * 从档案库中导入排序指标数据
	 * insert into idx_table(A0100) select usra01.a0100 from usra01 inner join suusrresult 
	 * on usra01.a0100=suusrresult.a0100;
	 * @param infor
	 * @param idx_table
	 * @param dbpre
	 * @param username
	 */
	private void fillOutOrderData(String infor,String dbpre,String username)
	{
		InfoGroup infogroup=new InfoGroup(Integer.parseInt(infor));
		String idx_table="T#"+username+"_mus";
		String mainset=dbpre+infogroup.getMainset();

		String resultset=null;
		int resultFlag=0;
		
		if(this.userview.getStatus()==0)
		{
		    if("2".equals(infor))
		    {
		       resultset=username+"bresult";
		       mainset=infogroup.getMainset();
	    	}
	    	else if("3".equals(infor))
	    	{
	    		resultset=username+"kresult";	
	    		 mainset=infogroup.getMainset();
	    	}
	    	else {
                resultset=username+dbpre+"result";
            }
		}else
		{
			resultset="t_sys_result";
			if("2".equals(infor))
			{
				resultFlag=1;
				 mainset=infogroup.getMainset();
			}
			if("3".equals(infor))
			{
				resultFlag=2;
				 mainset=infogroup.getMainset();
			}
		}
		String bosdate=DateStyle.dateformat(new Date(),"yyyy-MM-dd");
		/**导入键值指标*/
		StringBuffer strsql=new StringBuffer();
		if(this.userview.getStatus()==0)
		{
	    	strsql.append("insert into ");
		    strsql.append(idx_table);
	    	strsql.append("(");
	    	strsql.append(infogroup.getKeyfield());
	    	if("1".equals(infor)) {
                strsql.append(",A0000");
            }
	    	strsql.append(") select distinct ");
	    	strsql.append(mainset);
	    	strsql.append(".");
	    	strsql.append(infogroup.getKeyfield());
	    	if("1".equals(infor)) {
	    	    strsql.append(",");
	    	    strsql.append(mainset);
	    	    strsql.append(".");
	    	    strsql.append("A0000");	    	    
	    	}
	    	
	    	strsql.append(" from ");
	    	strsql.append(mainset);
	    	strsql.append(" inner join ");
	    	strsql.append(resultset);
	    	strsql.append(" on ");
	     	strsql.append(resultset);
	    	strsql.append(".");
	    	strsql.append(infogroup.getKeyfield());
	    	strsql.append("=");
	    	strsql.append(mainset);
	     	strsql.append(".");
	    	strsql.append(infogroup.getKeyfield());
	    	if("1".equals(infor)) {
                strsql.append(" order by "+ mainset +".A0000");
            }
	    	if("2".equals(infor))
	    	{
	        	strsql.append(" and ");
	        	strsql.append(mainset+"."+infogroup.getKeyfield());
	        	strsql.append(" in (select codeitemid from organization where (codesetid='UN' or codesetid='UM') ");
	        	strsql.append(" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date )");
	    	}
	    	if("3".equals(infor))
	    	{
	    		strsql.append(" and ");
	        	strsql.append(mainset+"."+infogroup.getKeyfield());
	        	strsql.append(" in (select codeitemid from organization where (codesetid='@K') ");
	        	strsql.append(" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date )");
	    	}
		}
		else
		{
			strsql.append("insert into ");
		    strsql.append(idx_table);
	    	strsql.append("(");
	    	strsql.append(infogroup.getKeyfield());
	    	if("1".equals(infor)) {
                strsql.append(",A0000");
            }
	    	strsql.append(") select distinct ");
	    	strsql.append(mainset);
	    	strsql.append(".");
	    	strsql.append(infogroup.getKeyfield());
	    	if("1".equals(infor)) {
                strsql.append(","+ mainset +".A0000");
            }
	    		//strsql.append(",A0000");
	    	strsql.append(" from ");
	    	strsql.append(mainset);
	    	strsql.append(" inner join ");
	    	strsql.append(resultset);
	    	strsql.append(" on ");
	     	strsql.append(resultset);
	    	strsql.append(".");
	    	strsql.append("obj_id");
	    	strsql.append("=");
	    	strsql.append(mainset);
	     	strsql.append(".");
	    	strsql.append(infogroup.getKeyfield());
	    	strsql.append(" where "+resultset+".flag="+resultFlag+" and UPPER("+resultset+".username)='"+userview.getUserName().toUpperCase()+"'");
		    if("1".equals(infor)){
		    	strsql.append(" and UPPER("+resultset+".nbase)='"+dbpre.toUpperCase()+"'");
		    	strsql.append(" order by "+ mainset +".A0000");
	    	}
		    if("2".equals(infor))
	    	{
	        	strsql.append(" and ");
	        	strsql.append(mainset+"."+infogroup.getKeyfield());
	        	strsql.append(" in (select codeitemid from organization where (codesetid='UN' or codesetid='UM') ");
	        	strsql.append(" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date )");
	    	}
	    	if("3".equals(infor))
	    	{
	    		strsql.append(" and ");
	        	strsql.append(mainset+"."+infogroup.getKeyfield());
	        	strsql.append(" in (select codeitemid from organization where (codesetid='@K') ");
	        	strsql.append(" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date )");
	    	}
		}
		ContentDAO dao=new ContentDAO(this.conn);
		
		DbWizard dbWizard =new DbWizard(this.conn);
		/**查询结果不存在时退出*/
		if(!dbWizard.isExistTable(resultset,false)) {
            return;
        }
		try
		{
		
			/**导入主键值*/
			//System.out.println(strsql.toString());
			dao.update(strsql.toString());
			/**导入排序指标值*/
			String destTab=idx_table;			
			StringBuffer strJoin=new StringBuffer();
			StringBuffer strSet=new StringBuffer();
			StringBuffer strSWhere=new StringBuffer();

			String srcTab=null;
			for(int i=0;i<this.sortlist.size();i++)
			{
				SortFieldVo vo=(SortFieldVo)this.sortlist.get(i);
				srcTab=dbpre+vo.getSet_name();
				if(infogroup.MainSetYesNo(vo.getSet_name()))
				{
					if("2".equals(infor)){
						srcTab="B01";
					}else if("3".equals(infor))
					{
						srcTab="K01";		
					}
				}else{
					if("2".equals(infor)|| "3".equals(infor))
					{
						srcTab=vo.getSet_name();
					}
				}
				strJoin.append(destTab);
				strJoin.append(".");
				strJoin.append(infogroup.getKeyfield());
				strJoin.append("=");
				strJoin.append(srcTab);
				strJoin.append(".");
				strJoin.append(infogroup.getKeyfield());
				
				strSet.append(destTab);
				strSet.append(".");
				strSet.append(vo.getField_name());
				strSet.append("=");
				strSet.append(srcTab);
				strSet.append(".");
				strSet.append(vo.getField_name());
				/**主集*/
				if(infogroup.MainSetYesNo(vo.getSet_name()))
				{   /**B0110分析有误,default为A01;E01A1,default也为A01*/
					dbWizard.updateRecord(destTab,srcTab,strJoin.toString(),strSet.toString(),"","");
				}
				else
				{
					strSWhere.append(srcTab);
					strSWhere.append(".I9999=(select MAX(I9999) from ");
					strSWhere.append(srcTab);
					strSWhere.append(" where ");
					strSWhere.append(srcTab);
					strSWhere.append(".");
					strSWhere.append(infogroup.getKeyfield());
					strSWhere.append("=");
					strSWhere.append(destTab);
					strSWhere.append(".");
					strSWhere.append(infogroup.getKeyfield());
					strSWhere.append(")");
					dbWizard.updateRecord(destTab,srcTab,strJoin.toString(),strSet.toString(),"",strSWhere.toString());
				}
				
				// 按单位部门排序 顺序取自organization 表单位部门的a0000     JinChunhai 2013.01.29
				String field_name = vo.getField_name().toLowerCase();
				if(("2".equals(infor)&&!"b0110".equals(field_name)) || ("1".equals(infor)&&!"a0100".equals(field_name)) || ("3".equals(infor)&&!"e01a1".equals(field_name))){
					if("b0110".equals(vo.getField_name().toLowerCase()) || "e0122".equals(vo.getField_name().toLowerCase()) || "e01a1".equals(vo.getField_name().toLowerCase()))
					{
						StringBuffer BA0000 = new StringBuffer();
						StringBuffer EA0000 = new StringBuffer();
						StringBuffer WA0000 = new StringBuffer();
						if("b0110".equals(vo.getField_name().toLowerCase()))
						{																		
							BA0000.append(destTab+".b0110=organization.codeitemid");						
							EA0000.append(destTab+".bsort=organization.a0000");
							WA0000.append(" organization.codesetid = 'UN' ");
						}
						else if("e0122".equals(vo.getField_name().toLowerCase()))
						{
							BA0000.append(destTab+".e0122=organization.codeitemid");
							EA0000.append(destTab+".esort=organization.a0000");
							WA0000.append(" organization.codesetid = 'UM' ");
						}
						else if("e01a1".equals(vo.getField_name().toLowerCase()))
						{
							BA0000.append(destTab+".e01a1=organization.codeitemid");
							EA0000.append(destTab+".asort=organization.a0000");
							WA0000.append(" organization.codesetid = '@K' ");
						}
					 dbWizard.updateRecord(destTab,"organization",BA0000.toString(),EA0000.toString(),"",WA0000.toString());
				    }
					
				}
				
				/**清空字符串*/
				strJoin.setLength(0);
				strSet.setLength(0);
				strSWhere.setLength(0);
			}//for loop end.
			/**导入a0000*/
			strJoin.setLength(0);
			strSet.setLength(0);
			strSWhere.setLength(0);			
			if("1".equals(infor))
			{/*
				srcTab=dbpre+"A01";
				strJoin.append(destTab);
				strJoin.append(".");
				strJoin.append(infogroup.getKeyfield());
				strJoin.append("=");
				strJoin.append(srcTab);
				strJoin.append(".");
				strJoin.append(infogroup.getKeyfield());
				strSet.append(destTab);
				strSet.append(".a0000");
				strSet.append("=");
				strSet.append(srcTab);
				strSet.append(".a0000");
				dbWizard.updateRecord(destTab,srcTab,strJoin.toString(),strSet.toString(),"","");*/
			}
			else
			{
				srcTab="organization";
				strJoin.append(destTab);
				strJoin.append(".");
				strJoin.append(infogroup.getKeyfield());
				strJoin.append("=");
				strJoin.append(srcTab);
				strJoin.append(".codeitemid");
				strSet.append(destTab);
				strSet.append(".a0000");
				strSet.append("=");
				strSet.append(srcTab);
				strSet.append(".a0000");
				dbWizard.updateRecord(destTab,srcTab,strJoin.toString(),strSet.toString(),"","");
			}
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	/**
	 * 从档案库中导入排序指标数据
	 * insert into idx_table(A0100) select usra01.a0100 from usra01 inner join suusrresult 
	 * on usra01.a0100=suusrresult.a0100;
	 * @param infor
	 * @param idx_table
	 * @param dbpre
	 * @param username
	 */
	private void fillOutOrderData(String infor,String dbpre,String username,String a_code)
	{
		InfoGroup infogroup=new InfoGroup(Integer.parseInt(infor));
		String idx_table="T#"+username+"_mus";
		String mainset=dbpre+infogroup.getMainset();
		
		String resultset=null;
		int resultFlag=0;
		
		if(this.userview.getStatus()==0)
		{
		    if("2".equals(infor))
		    {
		       resultset=username+"bresult";
		       mainset=infogroup.getMainset();
	    	}
	    	else if("3".equals(infor))
	    	{
	    		resultset=username+"kresult";	
	    		 mainset=infogroup.getMainset();
	    	}
	    	else {
                resultset=username+dbpre+"result";
            }
		}else
		{
			resultset="t_sys_result";
			if("2".equals(infor)) {
                resultFlag=1;
            }
			if("3".equals(infor)) {
                resultFlag=2;
            }
		}
		String bosdate=DateStyle.dateformat(new Date(),"yyyy-MM-dd");
		/**导入键值指标*/
		StringBuffer strsql=new StringBuffer();
		if(this.userview.getStatus()==0)
		{
	    	strsql.append("insert into ");
		    strsql.append(idx_table);
	    	strsql.append("(");
	    	strsql.append(infogroup.getKeyfield());
	    	strsql.append(") select distinct ");
	    	strsql.append(mainset);
	    	strsql.append(".");
	    	strsql.append(infogroup.getKeyfield());
	    	strsql.append(" from ");
	    	strsql.append(mainset);
	    	strsql.append(" inner join ");
	    	strsql.append(resultset);
	    	strsql.append(" on ");
	     	strsql.append(resultset);
	    	strsql.append(".");
	    	strsql.append(infogroup.getKeyfield());
	    	strsql.append("=");
	    	strsql.append(mainset);
	     	strsql.append(".");
	    	strsql.append(infogroup.getKeyfield());
		    if("1".equals(infor)){
		    	strsql.append(" where "+mainset+".E0122 like '");
		    	strsql.append(a_code+"%'");
	    	}
		    if("2".equals(infor))
	    	{
	        	strsql.append(" and ");
	        	strsql.append(mainset+"."+infogroup.getKeyfield());
	        	strsql.append(" in (select codeitemid from organization where (codesetid='UN' or codesetid='UM') ");
	        	strsql.append(" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date )");
	    	}
	    	if("3".equals(infor))
	    	{
	    		strsql.append(" and ");
	        	strsql.append(mainset+"."+infogroup.getKeyfield());
	        	strsql.append(" in (select codeitemid from organization where (codesetid='@K') ");
	        	strsql.append(" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date )");
	    	}
		}
		else
		{
			strsql.append("insert into ");
		    strsql.append(idx_table);
	    	strsql.append("(");
	    	strsql.append(infogroup.getKeyfield());
	    	strsql.append(") select distinct ");
	    	strsql.append(mainset);
	    	strsql.append(".");
	    	strsql.append(infogroup.getKeyfield());
	    	strsql.append(" from ");
	    	strsql.append(mainset);
	    	strsql.append(" inner join ");
	    	strsql.append(resultset);
	    	strsql.append(" on ");
	     	strsql.append(resultset);
	    	strsql.append(".");
	    	strsql.append("obj_id");
	    	strsql.append("=");
	    	strsql.append(mainset);
	     	strsql.append(".");
	    	strsql.append(infogroup.getKeyfield());
	    	strsql.append(" where "+resultset+".flag="+resultFlag+" and UPPER("+resultset+".username)='"+userview.getUserName().toUpperCase()+"'");
		    if("1".equals(infor)){
		    	strsql.append(" and UPPER("+resultset+".nbase)='"+dbpre.toUpperCase()+"'");
	    	}
		    if("2".equals(infor))
	    	{
	        	strsql.append(" and ");
	        	strsql.append(mainset+"."+infogroup.getKeyfield());
	        	strsql.append(" in (select codeitemid from organization where (codesetid='UN' or codesetid='UM') ");
	        	strsql.append(" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date )");
	    	}
	    	if("3".equals(infor))
	    	{
	    		strsql.append(" and ");
	        	strsql.append(mainset+"."+infogroup.getKeyfield());
	        	strsql.append(" in (select codeitemid from organization where (codesetid='@K') ");
	        	strsql.append(" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date )");
	    	}

		}
		//cat.debug("idx_table sql="+strsql.toString());		
		ContentDAO dao=new ContentDAO(this.conn);
		
		DbWizard dbWizard =new DbWizard(this.conn);
		Table table=new Table(resultset);
		/**查询结果不存在时退出*/
		if(!dbWizard.isExistTable(table)) {
            return;
        }
		try
		{
		
			/**导入主键值*/
			//System.out.println(strsql.toString());
			dao.update(strsql.toString());
			/**导入排序指标值*/
			String destTab=idx_table;			
			StringBuffer strJoin=new StringBuffer();
			StringBuffer strSet=new StringBuffer();
			StringBuffer strSWhere=new StringBuffer();

			String srcTab=null;
			for(int i=0;i<this.sortlist.size();i++)
			{
				SortFieldVo vo=(SortFieldVo)this.sortlist.get(i);
				srcTab=dbpre+vo.getSet_name();	
				if("2".equals(infor)|| "3".equals(infor))
				{
					srcTab=vo.getSet_name();
				}
				strJoin.append(destTab);
				strJoin.append(".");
				strJoin.append(infogroup.getKeyfield());
				strJoin.append("=");
				strJoin.append(srcTab);
				strJoin.append(".");
				strJoin.append(infogroup.getKeyfield());
				
				strSet.append(destTab);
				strSet.append(".");
				strSet.append(vo.getField_name());
				strSet.append("=");
				strSet.append(srcTab);
				strSet.append(".");
				strSet.append(vo.getField_name());
				/**主集*/
				if(infogroup.MainSetYesNo(vo.getSet_name()))
				{   /**B0110分析有误,default为A01;E01A1,default也为A01*/
					if("B0110".equalsIgnoreCase(vo.getField_name())&& "2".equals(infor)) {
                        srcTab="B01";
                    }
					if("E01A1".equalsIgnoreCase(vo.getField_name())&& "3".equals(infor)) {
                        srcTab="K01";
                    }
					
					dbWizard.updateRecord(destTab,srcTab,strJoin.toString(),strSet.toString(),"","");
				}
				else
				{
					strSWhere.append(srcTab);
					strSWhere.append(".I9999=(select MAX(I9999) from ");
					strSWhere.append(srcTab);
					strSWhere.append(" where ");
					strSWhere.append(srcTab);
					strSWhere.append(".");
					strSWhere.append(infogroup.getKeyfield());
					strSWhere.append("=");
					strSWhere.append(destTab);
					strSWhere.append(".");
					strSWhere.append(infogroup.getKeyfield());
					strSWhere.append(")");
					dbWizard.updateRecord(destTab,srcTab,strJoin.toString(),strSet.toString(),"",strSWhere.toString());
				}
				/**清空字符串*/
				strJoin.setLength(0);
				strSet.setLength(0);
				strSWhere.setLength(0);
			}//for loop end.
			/**导入a0000*/
			strJoin.setLength(0);
			strSet.setLength(0);
			strSWhere.setLength(0);			
			if("1".equals(infor))
			{
				srcTab=dbpre+"A01";
				strJoin.append(destTab);
				strJoin.append(".");
				strJoin.append(infogroup.getKeyfield());
				strJoin.append("=");
				strJoin.append(srcTab);
				strJoin.append(".");
				strJoin.append(infogroup.getKeyfield());
				strSet.append(destTab);
				strSet.append(".a0000");
				strSet.append("=");
				strSet.append(srcTab);
				strSet.append(".a0000");
				dbWizard.updateRecord(destTab,srcTab,strJoin.toString(),strSet.toString(),"","");
			}
			else
			{
				srcTab="organization";
				strJoin.append(destTab);
				strJoin.append(".");
				strJoin.append(infogroup.getKeyfield());
				strJoin.append("=");
				strJoin.append(srcTab);
				strJoin.append(".codeitemid");
				strSet.append(destTab);
				strSet.append(".a0000");
				strSet.append("=");
				strSet.append(srcTab);
				strSet.append(".a0000");
				dbWizard.updateRecord(destTab,srcTab,strJoin.toString(),strSet.toString(),"","");
			}
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	/**
	 * 从档案库中导入排序指标数据
	 * insert into idx_table(A0100) select usra01.a0100 from usra01 inner join suusrresult 
	 * on usra01.a0100=suusrresult.a0100;
	 * @param infor
	 * @param idx_table
	 * @param dbpre
	 * @param username
	 */
	private void fillOutOrderData(String infor,String dbpre,String username,String a_code,String wherestr)
	{
		InfoGroup infogroup=new InfoGroup(Integer.parseInt(infor));
		String idx_table="T#"+username+"_mus";
		String mainset=dbpre+infogroup.getMainset();
		
		String resultset=null;
		if("2".equals(infor))
		{
		  resultset=username+"bresult";
		  mainset=infogroup.getMainset();
		}
		else if("3".equals(infor))
		{
			resultset=username+"kresult";	
			 mainset=infogroup.getMainset();
		}
		else {
            resultset=username+dbpre+"result";
        }
		
		/**导入键值指标*/
		StringBuffer strsql=new StringBuffer();
		strsql.append("insert into ");
		strsql.append(idx_table);
		strsql.append("(");
		strsql.append(infogroup.getKeyfield());
		strsql.append(") select distinct ");
		strsql.append(mainset);
		strsql.append(".");
		strsql.append(infogroup.getKeyfield());
		strsql.append(" from ");
		strsql.append(mainset);
		if(orgtype!=null&& "vorg".equalsIgnoreCase(orgtype)){
			if("1".equals(infor)){
				strsql.append(" where "+mainset+".A0100 in(");
				strsql.append("select A0100 from t_vorg_staff where B0110 like '");
				strsql.append(a_code+"%'");
				strsql.append(")");
				String priStrSql = InfoUtils.getWhereINSql(this.userview, dbpre);
				StringBuffer buf = new StringBuffer();
				buf.append("select "+dbpre+"a01.A0100 ");
				if (priStrSql.length() > 0) {
                    buf.append(priStrSql);
                } else {
                    buf.append(" from "+dbpre+"a01");
                }
				strsql.append(" and ");
				strsql.append(mainset);
				strsql.append(".A0100 in(");
				strsql.append(buf.toString());
				strsql.append(")");
				if(wherestr!=null&&wherestr.trim().length()>0){
					strsql.append(wherestr);
				}
			}
		}else{
			if("1".equals(infor)){
				if(wherestr!=null&&wherestr.trim().length()>0){
					String priStrSql = InfoUtils.getWhereINSql(this.userview, dbpre);
					//员工管理导出excle 已将过滤条件传入，无需再次过滤
					if(StringUtils.isNotEmpty(wherestr)) {
						strsql.append(" where 1=1 ");
						strsql.append(wherestr);
					}else {
						StringBuffer buf = new StringBuffer();
						buf.append("select "+dbpre+"A01.A0100 ");
						if (priStrSql.length() > 0) {
							buf.append(priStrSql);
						}else {
							buf.append(" from "+dbpre+"A01");
						}
						strsql.append(" where ");
						strsql.append(mainset);
						strsql.append(".A0100 in(");
						strsql.append(buf.toString());
						strsql.append(")");
					}
					
					
				}else{
					strsql.append(" where ("+mainset+".B0110 like '");
					strsql.append(a_code+"%' or ");
					strsql.append(mainset+".E0122 like '");
					strsql.append(a_code+"%' or ");
					strsql.append(mainset+".E01A1 like '");
					strsql.append(a_code+"%')");
					String priStrSql = InfoUtils.getWhereINSql(this.userview, dbpre);
					StringBuffer buf = new StringBuffer();
					buf.append("select "+dbpre+"a01.A0100 ");
					if (priStrSql.length() > 0) {
                        buf.append(priStrSql);
                    } else {
                        buf.append(" from "+dbpre+"a01");
                    }
					strsql.append(" and ");
					strsql.append(mainset);
					strsql.append(".A0100 in(");
					strsql.append(buf.toString());
					strsql.append(")");
				}
			}
		}
		//cat.debug("idx_table sql="+strsql.toString());		
		ContentDAO dao=new ContentDAO(this.conn);
		
		DbWizard dbWizard =new DbWizard(this.conn);
//		Table table=new Table(resultset);
		/**查询结果不存在时退出*/
//		if(!dbWizard.isExistTable(resultset,false))
//			return;
		try
		{
		
			/**导入主键值*/
			//System.out.println(strsql.toString());
			dao.update(strsql.toString());
			/**导入排序指标值*/
			String destTab=idx_table;			
			StringBuffer strJoin=new StringBuffer();
			StringBuffer strSet=new StringBuffer();
			StringBuffer strSWhere=new StringBuffer();

			String srcTab=null;
			for(int i=0;i<this.sortlist.size();i++)
			{
				SortFieldVo vo=(SortFieldVo)this.sortlist.get(i);
				srcTab=dbpre+vo.getSet_name();	
				if("2".equals(infor)|| "3".equals(infor))
				{
					srcTab=vo.getSet_name();
				}
				strJoin.append(destTab);
				strJoin.append(".");
				strJoin.append(infogroup.getKeyfield());
				strJoin.append("=");
				strJoin.append(srcTab);
				strJoin.append(".");
				strJoin.append(infogroup.getKeyfield());
				
				strSet.append(destTab);
				strSet.append(".");
				strSet.append(vo.getField_name());
				strSet.append("=");
				strSet.append(srcTab);
				strSet.append(".");
				strSet.append(vo.getField_name());
				/**主集*/
				if(infogroup.MainSetYesNo(vo.getSet_name()))
				{   /**B0110分析有误,default为A01;E01A1,default也为A01*/
					if("B0110".equalsIgnoreCase(vo.getField_name())&& "2".equals(infor)) {
                        srcTab="B01";
                    }
					if("E01A1".equalsIgnoreCase(vo.getField_name())&& "3".equals(infor)) {
                        srcTab="K01";
                    }
					
					dbWizard.updateRecord(destTab,srcTab,strJoin.toString(),strSet.toString(),"","");
				}
				else
				{
					strSWhere.append(srcTab);
					strSWhere.append(".I9999=(select MAX(I9999) from ");
					strSWhere.append(srcTab);
					strSWhere.append(" where ");
					strSWhere.append(srcTab);
					strSWhere.append(".");
					strSWhere.append(infogroup.getKeyfield());
					strSWhere.append("=");
					strSWhere.append(destTab);
					strSWhere.append(".");
					strSWhere.append(infogroup.getKeyfield());
					strSWhere.append(")");
					dbWizard.updateRecord(destTab,srcTab,strJoin.toString(),strSet.toString(),"",strSWhere.toString());
				}
				/**清空字符串*/
				strJoin.setLength(0);
				strSet.setLength(0);
				strSWhere.setLength(0);
			}//for loop end.
			/**导入a0000*/
			strJoin.setLength(0);
			strSet.setLength(0);
			strSWhere.setLength(0);			
			if("1".equals(infor))
			{
				srcTab=dbpre+"A01";
				strJoin.append(destTab);
				strJoin.append(".");
				strJoin.append(infogroup.getKeyfield());
				strJoin.append("=");
				strJoin.append(srcTab);
				strJoin.append(".");
				strJoin.append(infogroup.getKeyfield());
				strSet.append(destTab);
				strSet.append(".a0000");
				strSet.append("=");
				strSet.append(srcTab);
				strSet.append(".a0000");
				dbWizard.updateRecord(destTab,srcTab,strJoin.toString(),strSet.toString(),"","");
			}
			else
			{
				srcTab="organization";
				strJoin.append(destTab);
				strJoin.append(".");
				strJoin.append(infogroup.getKeyfield());
				strJoin.append("=");
				strJoin.append(srcTab);
				strJoin.append(".codeitemid");
				strSet.append(destTab);
				strSet.append(".a0000");
				strSet.append("=");
				strSet.append(srcTab);
				strSet.append(".a0000");
				dbWizard.updateRecord(destTab,srcTab,strJoin.toString(),strSet.toString(),"","");
			}
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	/**
	 * 取得排序指标
	 * @param musterid
	 * @return
	 */
	private ArrayList getOrderFieldList(String musterid)
	{
		ArrayList list=new ArrayList();
		StringBuffer strsql=new StringBuffer();
		strsql.append("select sortfield from lname where tabid='");
		strsql.append(musterid+"'");
		RowSet rset=null;		
		ContentDAO dao=new ContentDAO(this.conn);
		String sortfields="";
		try
		{
			rset=dao.search(strsql.toString());
			if(rset.next()) {
                sortfields=Sql_switcher.readMemo(rset,"sortfield");
            }
			String[] sortarr=StringUtils.split(sortfields,",");
			cat.debug("sortfields="+sortfields);
			for(int i=0;i<sortarr.length;i++)
			{
				sortfields=sortarr[i];
				sortfields=sortfields.substring(4);
				SortFieldVo fieldvo=new SortFieldVo(sortarr[i].substring(0,3),sortfields.substring(0,5),Integer.parseInt(sortfields.substring(5)));
				cat.debug("sortfieldvo="+fieldvo.toString());
				list.add(fieldvo);
			}
		}
		catch(Exception ex)
		{
			;
		}
		return list;
	}
	/**
	 * 取得排序指标
	 * @param musterid
	 * @return
	 */
	public String getOrderFieldStr(String musterid)
	{
		StringBuffer sortitem = new StringBuffer();
		StringBuffer strsql=new StringBuffer();
		strsql.append("select sortfield from lname where tabid=");
		strsql.append(musterid);
		RowSet rset=null;		
		ContentDAO dao=new ContentDAO(this.conn);
		String sortfields="";
		try
		{
			rset=dao.search(strsql.toString());
			if(rset.next()) {
                sortfields=Sql_switcher.readMemo(rset,"sortfield");
            }
			String[] sortarr=StringUtils.split(sortfields,",");
			cat.debug("sortfields="+sortfields);
			for(int i=0;i<sortarr.length;i++)
			{
				sortfields=sortarr[i];
				sortfields=sortfields.substring(4);
				FieldItem fielditem = DataDictionary.getFieldItem(sortfields.substring(0,5));
				sortitem.append(fielditem.getItemid());
				sortitem.append(":");
				sortitem.append(fielditem.getItemdesc());
				sortitem.append(":");
				if(sortfields.substring(5)!=null&& "1".equals(sortfields.substring(5))) {
                    sortitem.append("0");
                } else {
                    sortitem.append("1");
                }
				sortitem.append("`");
			}
		}
		catch(Exception ex)
		{
			;
		}
		return sortitem.toString();
	}
	/**
	 * 取得排序指标
	 * @param musterid
	 * @return
	 */
	public String getOrderField(String sortfields)
	{
		if(sortfields==null||sortfields.trim().length()<1) {
            return "";
        }
		StringBuffer sortitem = new StringBuffer();
		String[] sortarr=StringUtils.split(sortfields,",");
		cat.debug("sortfields="+sortfields);
		for(int i=0;i<sortarr.length;i++)
		{
			sortfields=sortarr[i];
			sortfields=sortfields.substring(4);
			FieldItem fielditem = DataDictionary.getFieldItem(sortfields.substring(0,5));
			sortitem.append(fielditem.getItemid());
			sortitem.append(":");
			sortitem.append(fielditem.getItemdesc());
			sortitem.append(":");
			if(sortfields.substring(5)!=null&& "1".equals(sortfields.substring(5))) {
                sortitem.append("0");
            } else {
                sortitem.append("1");
            }
			sortitem.append("`");
		}
		return sortitem.toString();
	}
	public String sortItemStr(String sortitem){
		StringBuffer sortstr = new StringBuffer();
		if(sortitem==null||sortitem.trim().length()<1) {
            return "";
        }
		String arr[] = sortitem.split("`");
		for(int i=0;i<arr.length;i++){
			if(arr[i]!=null&&arr[i].trim().length()>0){
				String itemArr[] = arr[i].split(":");
				if(itemArr.length==3){
					FieldItem fielditem = DataDictionary.getFieldItem(itemArr[0]);
					if(fielditem!=null){
						sortstr.append(fielditem.getFieldsetid());
						sortstr.append(".");
						sortstr.append(fielditem.getItemid());
						if(itemArr[2]!=null&& "1".equals(itemArr[2])) {
                            sortstr.append("0");
                        } else {
                            sortstr.append("1");
                        }
						sortstr.append(",");
					}
				}
			}
		}
		return sortstr.toString();
	}
	/**
	 * 创建临时排序表
	 * 命名规则m_idx_+"用户名"
	 * @param infor
	 * @param dbpre
	 * @param musterid
	 * @param username
	 * @return
	 */
	private boolean createOrderTable(String infor,String musterid,String username)
	{
		boolean bflag=false;
		String key_field=null;
		String field_name=null;
		String tablename = "T#"+username+"_mus";
		DbWizard dbWizard =new DbWizard(this.conn);
		Table table=new Table(tablename,tablename);
		try
		{
			this.sortlist=getOrderFieldList(musterid);
			if("1".equals(infor)) {
                key_field="A0100";
            } else if("2".equals(infor)) {
                key_field="B0110";
            } else {
                key_field="E01A1";
            }
			FieldItem item=new FieldItem(key_field,key_field);
			item.setItemlength(50);
			item.setItemtype("A");
			table.addField(item);
			/**排序字段*/
			item=new FieldItem("a0000","a0000");
			item.setItemtype("N");
			item.setItemlength(12);
			table.addField(item);
			
			for(int i=0;i<this.sortlist.size();i++)
			{
				field_name=((SortFieldVo)this.sortlist.get(i)).getField_name();
				if(("2".equals(infor)&&!"b0110".equals(field_name.toLowerCase())) || ("1".equals(infor)&&!"a0100".equals(field_name.toLowerCase())) || ("3".equals(infor)&&!"e01a1".equals(field_name.toLowerCase())))
				{
					FieldItem temp=DataDictionary.getFieldItem(field_name);
					if(temp==null) {
                        continue;
                    }
					//temp.setDecimalwidth(4);
					Field field = new Field(temp.getItemid());
					int t=DataType.STRING;
					if("D".equalsIgnoreCase(temp.getItemtype())) {
                        t=DataType.DATE;
                    } else if("N".equalsIgnoreCase(temp.getItemtype()))
					{
						if(temp.getDecimalwidth()>0) {
                            t=DataType.FLOAT;
                        } else {
                            t=DataType.INT;
                        }
					}else if("M".equalsIgnoreCase(temp.getItemtype())) {
                        t=DataType.CLOB;
                    }
					field.setDatatype(t);
					field.setLength(temp.getItemlength());
					field.setDecimalDigits(temp.getDecimalwidth());
					field.setCodesetid(temp.getCodesetid());
					//table.addField(temp);
					table.addField(field);
					
					// 按单位部门排序 顺序取自organization 表单位部门的a0000     JinChunhai 2013.01.29
					if("b0110".equals(field_name.toLowerCase()) || "e0122".equals(field_name.toLowerCase()) || "e01a1".equals(field_name.toLowerCase()))
					{
						String a0000 = "bsort";
						if("e0122".equals(field_name.toLowerCase())) {
                            a0000 = "esort";
                        } else if("e01a1".equals(field_name.toLowerCase())) {
                            a0000 = "asort";
                        }
						Field field0 = new Field(a0000);						
						field0.setDatatype(DataType.INT);
						field0.setKeyable(false);
						table.addField(field0);														
					}															
				}
			}
			
			if(dbWizard.isExistTable(table.getName(),false)) {
                dbWizard.dropTable(table);
            }
			
			bflag=true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			try
			{
				dbWizard.createTable(table);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return bflag;
	}
	/**
	 * 根据命名规则求得临时表的名称
	 * @param infor
	 * @param dbpre
	 * @param musterid
	 * @param username
	 * @return
	 */
	public String getTableName(String infor, String dbpre, String musterid, String username) {
		StringBuffer tablename=new StringBuffer();
		tablename.append("m");
		tablename.append(musterid);
		tablename.append("_");
		tablename.append(username);
		tablename.append("_");
		if("2".equals(infor)) {
            tablename.append("B");
        } else if("3".equals(infor)) {
            tablename.append("K");
        } else {
            tablename.append(dbpre);
        }
		return tablename.toString();
	}
	

	/**
	 * 取得常用查询信息列表
	 * @param type  1：人员库 2：单位 3：职位
	 * @return
	 */
	public ArrayList getUsuallyCondList(String type,UserView userview)
	{
		ArrayList list=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet frowset=null;
		try
		{
			StringBuffer strsql=new StringBuffer("");
			strsql.append("select id,name,type from lexpr where type='");//
	        strsql.append(type);
	        strsql.append("' order by id");
	        frowset=dao.search(strsql.toString());
         
            while(frowset.next())
            {
                if(!(userview.isHaveResource(IResourceConstant.LEXPR,frowset.getString("id")))) {
                    continue;
                }
                DynaBean vo=new LazyDynaBean();
                vo.set("id",frowset.getString("id"));
                vo.set("name",frowset.getString("name")!=null?frowset.getString("name"):"");
                vo.set("type",frowset.getString("type"));
                list.add(vo);            
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 取得常用查询信息列表
	 * @param type  1：人员库 2：单位 3：职位
	 * @return
	 */
	public ArrayList getCondList(String type,UserView userview)
	{
		ArrayList list=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet frowset=null;
		try
		{
			StringBuffer strsql=new StringBuffer("");
			strsql.append("select id,name,type from lexpr where type='");//
	        strsql.append(type);
	        strsql.append("' order by id");
	        frowset=dao.search(strsql.toString());
         
            while(frowset.next())
            {
                if(!(userview.isHaveResource(IResourceConstant.LEXPR,frowset.getString("id")))) {
                    continue;
                }
                CommonData vo = new CommonData();
                String name = frowset.getString("name");
                name = name!=null&&name.trim().length()>0?name:"";
                String id = frowset.getString("id");
                id = id!=null&&id.trim().length()>0?id:"";
                vo.setDataName(frowset.getString("name"));
                vo.setDataValue(frowset.getString("id"));
                list.add(vo);            
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 任务：3692 人员、机构、岗位高级花名册取数条件支持常用查询参数 xiaoyun 2014-8-14
	 * @param type  1：人员库 2：单位 3：职位
	 * @param ids   花名册id，逗号分隔
	 * @return 符合条件的常用查询列表
	 */
	public ArrayList getCondArrayList(String type, String ids) {		
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet frowset = null;
		try {
				String[] orderNo = ids.split(",");
				StringBuffer strsql = new StringBuffer("");
				strsql.append("select id,name from lexpr where type='");
		        strsql.append(type).append("' and id in(");
		        strsql.append(ids);
		        strsql.append(")");
		        strsql.append(" order by (case id ");
		        for (int i = 0; i < orderNo.length; i++) {		        	
		        	strsql.append("when ").append(Integer.valueOf(orderNo[i])).append(" then ").append(i).append(" ");
				}
		        strsql.append(" end)");
		        frowset=dao.search(strsql.toString());
	            while(frowset.next()) {
	                CommonData vo = new CommonData();
	                String name = frowset.getString("name");
	                name = name!=null&&name.trim().length()>0?name:"";
	                String id = frowset.getString("id");
	                id = id!=null&&id.trim().length()>0?id:"";
	                vo.setDataName(frowset.getString("name"));
	                vo.setDataValue(frowset.getString("id"));
	                list.add(vo);            
	            }
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
    public List getCondList(String type, String ids) {      
        return getCondArrayList(type, ids);
    }
        
    /**
     * 常用查询条件是否存在
     * @return
     */
    public boolean condExists(String condid, ArrayList condlist) {
        List list = condlist;
        return condExists(condid, list);
    }	

    public boolean condExists(String condid, List condlist) {
        for(int i=0;i<condlist.size();i++){
            CommonData vo=(CommonData)condlist.get(i);
            if(condid.equals(vo.getDataValue())) {
                return true;
            }
        }
        return false;
    }   
    
    /**
     * 对全部人员库，运行常用查询条件，将查询的结果放如到查询结果表
     * @return
     */
    public void runAllDBCondTable(String condid,String a_code,ArrayList dblist,String infor_flag) {
        for(int i=0;i<dblist.size();i++){
            CommonData vo=(CommonData)dblist.get(i);
            String dbpre=vo.getDataValue();
            if("ALL".equals(dbpre)) {
                continue;
            }
            runCondTable(condid, a_code, dbpre, infor_flag);
        }
    }

    /**
	 * 运行常用查询条件，将查询的结果放如到查询结果表
	 * @return
	 */
	public void runCondTable(String condid,String a_code,String dbpre,String infor_flag)
	{
		
		
		try {
            if("2".equals(infor_flag)) {// 单位、部门全查
                a_code = "all";
            }
			/**没有条件，查看全部，*/
			if(condid==null|| "".equals(condid.trim()))
			{
				String lexpr="1";
				String factor="A0101<>`";
				if("2".equals(infor_flag)) {
				    factor = "B0110<>`";
				}
				else if("3".equals(infor_flag)) {
                    factor = "E01A1<>`";
                }
	    		ContentDAO dao = new ContentDAO(this.conn);
	    		SearchInformBo searchInformBo = new SearchInformBo(this.conn,this.userview,a_code,dbpre);
	    		if(this.userview.getStatus()==4)
	    		{
	    			searchInformBo.saveSelfServiceQueryResult(lexpr, factor, "2", "0","0", "0", infor_flag, "", 2);
	    		}
		    	else
	    		{
	    			String wherestr = searchInformBo.strWhere(lexpr,factor,"2","0","0","0",infor_flag);
		    	    searchInformBo.saveQueryResult(infor_flag,wherestr);
	    		}
			}
			else
			{
	    		RecordVo vo = new RecordVo("LExpr");
	    		vo.setInt("id",Integer.parseInt(condid));
	    		ContentDAO dao = new ContentDAO(this.conn);
	    		vo = dao.findByPrimaryKey(vo);
	    		SearchInformBo searchInformBo = new SearchInformBo(this.conn,this.userview,a_code,dbpre);
	    		String factor=vo.getString("factor");
	    		factor=factor.replaceAll("\\$THISMONTH\\[\\]","当月");//修改cs中定义的当月bs中翻译不过来问题
	    		if(this.userview.getStatus()==4)
	    		{
	    			searchInformBo.saveSelfServiceQueryResult(vo.getString("lexpr"), factor, "2", vo.getString("history"),vo.getString("fuzzyflag"), "0", infor_flag, "", 2);
	    		}
		    	else
	    		{
	    			String wherestr = searchInformBo.strWhere(vo.getString("lexpr"),
		    	    		factor,"2",vo.getString("history"),vo.getString("fuzzyflag"),"0",infor_flag);
		    	    searchInformBo.saveQueryResult(infor_flag,wherestr);
	    		}
			}
		    
		} catch (GeneralException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 取得花名册结构指标项列表
	 * @param musterid
	 * @return
	 */
	public ArrayList getMusterFields(String musterid,String infor)
	{
		ArrayList list=new ArrayList();
		Field temp=new Field("recidx",ResourceFactory.getProperty("recidx.label"));
		temp.setNullable(false);
		temp.setKeyable(true);
		temp.setDatatype(DataType.INT);
	    temp.setSortable(false);		
		list.add(temp);
		
		if("1".equals(infor))
		{
				temp=new Field("A0100",ResourceFactory.getProperty("a0100.label"));
				temp.setDatatype(DataType.STRING);
				//temp.setKeyable(true);
			    temp.setVisible(false);
			    temp.setNullable(false);
			    temp.setSortable(false);	
				temp.setLength(50);
		}
		else if("2".equals(infor))
		{
			temp=new Field("B0110_CODE",ResourceFactory.getProperty("b0110.label"));
			temp.setDatatype(DataType.STRING);
		    temp.setNullable(false);			
			//temp.setKeyable(true);
		    temp.setVisible(false);			
			temp.setLength(50);
			temp.setSortable(false);				
		}
		else
		{
			temp=new Field("E01A1_CODE",ResourceFactory.getProperty("e01a1.label"));
			temp.setDatatype(DataType.STRING);
			//temp.setKeyable(true);
		    temp.setNullable(false);			
		    temp.setVisible(false);	
		    temp.setSortable(false);			    
			temp.setLength(50);			
		}
		list.add(temp);
		
		StringBuffer strsql=new StringBuffer();
		strsql.append("select field_name,colhz from lbase where tabid='");
		strsql.append(musterid);
		strsql.append("' and Width<>0 order by baseid");
		RowSet rset=null;
		StringBuffer format=new StringBuffer();	
		format.append("############");	
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			rset=dao.search(strsql.toString());
			while(rset.next())
			{
				String fieldname=rset.getString("field_name");
				/**A01.A0405或A01.A0405_1*/
				String setname = fieldname.substring(0, 3);
				fieldname=fieldname.substring(4);
				//FieldItem item=DataDictionary.getFieldItem(fieldname.substring(0,5));
				FieldSet set=DataDictionary.getFieldSetVo(setname);//zgd 2014-7-8确定表是否被已构库掉
				if(set==null) {
                    continue;
                }
				FieldItem item=DataDictionary.getFieldItem(fieldname.substring(0,5), setname);//缺陷2988 zgd 2014-7-8 花名册只在指标体系中取指标，不在业务字典中选取。指定表名。
				if(item==null) {
                    continue;
                }
				Field obj=new Field(fieldname/*item.getItemid()*/,item.getItemdesc());
				if("A".equals(item.getItemtype()))
				{
					obj.setDatatype(DataType.STRING);
					/**字段为代码型,长度定为50*/
					if(item.getCodesetid()==null|| "0".equals(item.getCodesetid())|| "".equals(item.getCodesetid())) {
                        obj.setLength(item.getItemlength());
                    } else{
						if("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid())) {
                            obj.setLength(this.orgdesc_length);
                        } else {
                            obj.setLength(this.codedesc_length);
                        }
					}
					obj.setAlign("left");
				}
				else if("M".equals(item.getItemtype()))
				{
					obj.setDatatype(DataType.CLOB);
					obj.setAlign("left");					
				}
				else if("N".equals(item.getItemtype()))
				{
					obj.setDatatype(DataType.STRING);
					obj.setLength(12);
					int ndec=temp.getDecimalDigits();
					if(ndec>0)
					{
						format.setLength(ndec);
						obj.setFormat("####."+format.toString());
					}
					else
					{
						obj.setFormat("####");						
					}
					obj.setAlign("right");					
				}	
				else if("D".equals(item.getItemtype()))
				{
					obj.setLength(20);
					obj.setDatatype(DataType.STRING);
					//obj.setDatatype(DataType.DATE);//
					obj.setFormat("yyyy.MM.dd");
					obj.setAlign("right");						
				}	
				else
				{
					obj.setDatatype(DataType.STRING);
					obj.setLength(item.getItemlength());
					if("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid())) {
                        obj.setLength(this.orgdesc_length);
                    }
					obj.setAlign("left");						
				}
				obj.setSortable(false);
				obj.setLabel(rset.getString("colhz"));
				list.add(obj);
			}
		
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
//		  try
//		  {
//			if(rset!=null)
//				rset.close();
//		  }
//		  catch(Exception sqle)
//		  {
//			  sqle.printStackTrace();
//		  }
		}
		return list;			
	}
	/**
	 * 取得花名册结构指标项列表
	 * @param musterid
	 * @return
	 */
	public ArrayList getMusterFieldsValue(String musterid,String infor)
	{
		ArrayList list=new ArrayList();
		Field temp=new Field("recidx",ResourceFactory.getProperty("recidx.label"));
		temp.setNullable(false);
		temp.setKeyable(true);
		temp.setDatatype(DataType.INT);
	    temp.setSortable(true);		
		list.add(temp);
		
		if("1".equals(infor))
		{
				temp=new Field("A0100",ResourceFactory.getProperty("a0100.label"));
				temp.setDatatype(DataType.STRING);
				//temp.setKeyable(true);
			    temp.setVisible(false);
			    temp.setNullable(false);
			    temp.setSortable(true);	
				temp.setLength(50);
		}
		else if("2".equals(infor))
		{
			temp=new Field("B0110_CODE",ResourceFactory.getProperty("b0110.label"));
			temp.setDatatype(DataType.STRING);
		    temp.setNullable(false);			
			//temp.setKeyable(true);
		    temp.setVisible(false);			
			temp.setLength(50);
			temp.setSortable(true);				
		}
		else
		{
			temp=new Field("E01A1_CODE",ResourceFactory.getProperty("e01a1.label"));
			temp.setDatatype(DataType.STRING);
			//temp.setKeyable(true);
		    temp.setNullable(false);			
		    temp.setVisible(false);	
		    temp.setSortable(true);			    
			temp.setLength(50);			
		}
		list.add(temp);
		
		StringBuffer strsql=new StringBuffer();
		strsql.append("select field_name,colhz from lbase where tabid='");
		strsql.append(musterid);
		strsql.append("' order by baseid");
		RowSet rset=null;
		StringBuffer format=new StringBuffer();	
		format.append("############");	
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			rset=dao.search(strsql.toString());
			while(rset.next())
			{
				String fieldname=rset.getString("field_name");
				/**A01.A0405或A01.A0405_1*/
				String setname = fieldname.substring(0, 3);
				fieldname=fieldname.substring(4);
				//FieldItem item=DataDictionary.getFieldItem(fieldname.substring(0,5));
				FieldSet set=DataDictionary.getFieldSetVo(setname);//zgd 2014-7-8确定表是否被已构库掉
				if(set==null) {
                    continue;
                }
				FieldItem item=DataDictionary.getFieldItem(fieldname.substring(0,5), setname);//缺陷2988 zgd 2014-7-8 花名册只在指标体系中取指标，不在业务字典中选取。指定表名。
				if(item==null) {
                    continue;
                }
				Field obj=new Field(fieldname/*item.getItemid()*/,item.getItemdesc());
				if("A".equals(item.getItemtype()))
				{
					obj.setDatatype(DataType.STRING);
					/**字段为代码型,长度定为50*/
					if(item.getCodesetid()==null|| "0".equals(item.getCodesetid())|| "".equals(item.getCodesetid())) {
                        obj.setLength(item.getItemlength());
                    } else
					{
						if("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid())) {
                            obj.setLength(this.orgdesc_length);
                        } else {
                            obj.setLength(this.codedesc_length);
                        }
					}
					
					obj.setAlign("left");
				}
				else if("M".equals(item.getItemtype()))
				{
					obj.setDatatype(DataType.CLOB);
					obj.setAlign("left");					
				}
				else if("N".equals(item.getItemtype()))
				{
					obj.setDatatype(DataType.STRING);
					obj.setLength(12);
					int ndec=temp.getDecimalDigits();
					if(ndec>0)
					{
						format.setLength(ndec);
						obj.setFormat("####."+format.toString());
					}
					else
					{
						obj.setFormat("####");						
					}
					obj.setAlign("right");					
				}	
				else if("D".equals(item.getItemtype()))
				{
					obj.setLength(20);
					obj.setDatatype(DataType.STRING);
					//obj.setDatatype(DataType.DATE);//
					obj.setFormat("yyyy.MM.dd");
					obj.setAlign("right");						
				}	
				else
				{
					obj.setDatatype(DataType.STRING);
					obj.setLength(item.getItemlength());
					if("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid())) {
                        obj.setLength(this.orgdesc_length);
                    }
					obj.setAlign("left");						
				}
				obj.setSortable(true);
				obj.setLabel(rset.getString("colhz"));
				list.add(obj);
			}
		
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
//		  try
//		  {
//			if(rset!=null)
//				rset.close();
//		  }
//		  catch(Exception sqle)
//		  {
//			  sqle.printStackTrace();
//		  }
		}
		return list;			
	}
	/**
	 * 取得花名册结构指标项列表
	 * @param musterid
	 * @return
	 */
	public ArrayList getMusterFieldsTemp(String musterid,String infor)
	{
		ArrayList list=new ArrayList();
		Field temp=new Field("recidx",ResourceFactory.getProperty("recidx.label"));
		temp.setNullable(false);
		temp.setKeyable(true);
		temp.setDatatype(DataType.INT);
	    temp.setSortable(true);	
	    temp.setLength(10);
		list.add(temp);
		RowSet rset=null;
		ContentDAO dao=new ContentDAO(this.conn);
		
		try
		{
			if("1".equals(infor))
			{
					temp=new Field("A0100",ResourceFactory.getProperty("a0100.label"));
					temp.setDatatype(DataType.STRING);
					//temp.setKeyable(true);
				    temp.setVisible(false);
				    temp.setNullable(false);
				    temp.setSortable(true);	
					temp.setLength(50);
			}
			else if("2".equals(infor))
			{
				temp=new Field("B0110_CODE",ResourceFactory.getProperty("b0110.label"));
				temp.setDatatype(DataType.STRING);
			    temp.setNullable(false);			
				//temp.setKeyable(true);
			    temp.setVisible(false);			
				temp.setLength(org_length);
				temp.setSortable(true);				
			}
			else
			{
				temp=new Field("E01A1_CODE",ResourceFactory.getProperty("e01a1.label"));
				temp.setDatatype(DataType.STRING);
				//temp.setKeyable(true);
			    temp.setNullable(false);			
			    temp.setVisible(false);	
			    temp.setSortable(true);			    
				temp.setLength(org_length);			
			}
			list.add(temp);
			
			StringBuffer strsql=new StringBuffer();
			strsql.append("select field_name,colhz from lbase where tabid=");
			strsql.append(musterid);
			strsql.append(" order by baseid");
			
			StringBuffer format=new StringBuffer();	
			format.append("############");	
			String fieldstr = "";
			rset=dao.search(strsql.toString());
			while(rset.next())
			{
				String fieldname=rset.getString("field_name");
				/**A01.A0405或A01.A0405_1*/
				String setname = fieldname.substring(0, 3);
				fieldname=fieldname.substring(4);
				if(fieldstr.indexOf(fieldname+",")!=-1) {
                    continue;
                }
				fieldstr+=fieldname+",";
				//FieldItem item=DataDictionary.getFieldItem(fieldname.substring(0,5));
				FieldSet set=DataDictionary.getFieldSetVo(setname);//zgd 2014-7-8确定表是否被已构库掉
				if(set==null) {
                    continue;
                }
				FieldItem item=DataDictionary.getFieldItem(fieldname.substring(0,5), setname);//缺陷2988 zgd 2014-7-8 花名册只在指标体系中取指标，不在业务字典中选取。指定表名。
				if(item==null) {
                    continue;
                }
				Field obj=new Field(fieldname/*item.getItemid()*/,item.getItemdesc());
				if("A".equals(item.getItemtype()))
				{
					obj.setDatatype(DataType.STRING);
					/**字段为代码型,长度定为50*/
					if(item.isCode()) {
                        obj.setLength(codedesc_length);
                    } else {
                        obj.setLength(item.getItemlength());
                    }
					if("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid())) {
                        obj.setLength(orgdesc_length);
                    }
					/*if(item.getItemid().equalsIgnoreCase("E0122"))
						obj.setLength(200);
					if(item.getItemid().equalsIgnoreCase("B0110"))
						obj.setLength(200);
					if(item.getItemid().equalsIgnoreCase("E01A1"))
						obj.setLength(200);*/
					obj.setAlign("left");
				}
				else if("M".equals(item.getItemtype()))
				{
					obj.setDatatype(DataType.CLOB);
					obj.setAlign("left");					
				}
				else if("N".equals(item.getItemtype()))
				{
					obj.setDatatype(DataType.STRING);
					obj.setLength(12);
					int ndec=temp.getDecimalDigits();
					if(ndec>0)
					{
						format.setLength(ndec);
						obj.setFormat("####."+format.toString());
					}
					else
					{
						obj.setFormat("####");						
					}
					obj.setAlign("right");					
				}	
				else if("D".equals(item.getItemtype()))
				{
					obj.setLength(20);
					obj.setDatatype(DataType.STRING);
					//obj.setDatatype(DataType.DATE);//
					obj.setFormat("yyyy.MM.dd");
					obj.setAlign("right");						
				}	
				else
				{
					obj.setDatatype(DataType.STRING);
					
					obj.setLength(item.getItemlength());
					if("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid())) {
                        obj.setLength(orgdesc_length);
                    }
					obj.setAlign("left");						
				}
				obj.setSortable(true);
				obj.setLabel(rset.getString("colhz"));
				list.add(obj);
			}
		
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
//		  try
//		  {
//			if(rset!=null)
//				rset.close();
//		  }
//		  catch(Exception sqle)
//		  {
//			  sqle.printStackTrace();
//		  }
		}
		return list;			
	}
	public String getOrgtype() {
		return orgtype;
	}
	public void setOrgtype(String orgtype) {
		this.orgtype = orgtype;
	}
	/**
	 * 取得所有花名册列表
	 * @param inforkind
	 * @return
	 */
	public ArrayList getPrivMusterList(String inforkind,UserView userView)throws GeneralException
	{
		ArrayList list=new ArrayList();
		StringBuffer strsql=new StringBuffer();
		strsql.append("select tabid,hzname,flag from lname where flag='");
		strsql.append(inforkind);
		strsql.append("'");
		strsql.append(" order by "+Sql_switcher.isnull("norder", "99999"));
		ContentDAO dao=new ContentDAO(this.conn);				
		RowSet recset=null;		
		try
		{
			recset=dao.search(strsql.toString());
			while(recset.next())
			{
				RecordVo vo=new RecordVo("lname");
				if(!(userView.isHaveResource(IResourceConstant.MUSTER,recset.getString("tabid")))) {
                    continue;
                }
				vo.setInt("tabid",recset.getInt("tabid"));
				vo.setString("hzname",recset.getInt("tabid")+"."+recset.getString("hzname"));
				vo.setString("flag",recset.getString("flag"));
				list.add(vo);
			}
			/*recset=dao.search("SELECT tabid,cname FROM muster_name where (sortid is null or sortid='0') and nmodule="+inforkind+" order by tabid");
			while(recset.next())
			{
				RecordVo vo=new RecordVo("muster_name");
				if(!userView.isSuper_admin()&&!(userView.isHaveResource(IResourceConstant.HIGHMUSTER,recset.getString("tabid"))))
        			continue;           		
				vo.setInt("tabid",recset.getInt("tabid"));
				vo.setString("hzname",recset.getInt("tabid")+"."+recset.getString("cname"));
				list.add(vo);
			}*/
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);					
		}
		finally
		{
			try
			{
				if(recset!=null) {
                    recset.close();
                }
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
		}
		return list;
	}
	/**
	 * 取得普通花名册列表
	 * @param musterType 1人员 2单位 3岗位 4基准岗位
	 * @author Zhiyh 2019-05-16 简单花名册优化
	 * @return
	 */
	public ArrayList getPrivCommonMusterList(String musterType,UserView userView)throws GeneralException{
		ArrayList list=new ArrayList();
		RowSet recset=null;		
		try{
			MusterManageService musterManageService = new MusterManageServiceImpl(this.conn,userView);
			String strsql=musterManageService.getMusterMainSql(musterType,"0");//0 员工管理 1 组织机构
			ContentDAO dao=new ContentDAO(this.conn);	
			recset=dao.search(strsql);
			while(recset.next()){
				RecordVo vo=new RecordVo("lname");        		
				vo.setInt("tabid",recset.getInt("tabid"));
				vo.setString("hzname",recset.getString("hzname"));
				//vo.setString("flag",recset.getString("flag"));
				list.add(vo);
			}
		}catch(Exception ex){
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);					
		}finally{
			PubFunc.closeDbObj(recset);
		}
		return list;
	}
	public ArrayList getPrivHighMusterList(String inforkind,UserView userView)throws GeneralException
	{
		ArrayList list=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);				
		RowSet recset=null;		
		try
		{
			if("1".equals(inforkind)) {
                inforkind="3";
            }
			//String dd="SELECT tabid,cname FROM muster_name where (sortid is null or sortid='0') and nmodule="+inforkind+" order by tabid";
			recset=dao.search("SELECT tabid,cname,nmodule FROM muster_name"+
			        " where  nmodule="+inforkind+" and tabid<>1000 and tabid<>1010 and tabid<>1020"+
			        " order by "+Sql_switcher.isnull("norder", "99999"));
			while(recset.next())
			{
				RecordVo vo=new RecordVo("muster_name");
				if(!userView.isSuper_admin()&&!(userView.isHaveResource(IResourceConstant.HIGHMUSTER,recset.getString("tabid")))) {
                    continue;
                }
				vo.setInt("tabid",recset.getInt("tabid"));
				vo.setString("cname",recset.getInt("tabid")+"."+recset.getString("cname"));
				vo.setInt("nmodule", recset.getInt("nmodule"));
				list.add(vo);
			}
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);					
		}
		finally
		{
			try
			{
				if(recset!=null) {
                    recset.close();
                }
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
		}
		return list;
	}
	public String getChcksort() {
		return chcksort;
	}
	public void setChcksort(String chcksort) {
		this.chcksort = chcksort;
	}
	public int getCode_length() {
		return code_length;
	}
	public void setCode_length(int code_length) {
		this.code_length = code_length;
	}
	public int getCodedesc_length() {
		return codedesc_length;
	}
	public void setCodedesc_length(int codedesc_length) {
		this.codedesc_length = codedesc_length;
	}
	public int getOrg_length() {
		return org_length;
	}
	public void setOrg_length(int org_length) {
		this.org_length = org_length;
	}
	public int getOrgdesc_length() {
		return orgdesc_length;
	}
	public void setOrgdesc_length(int orgdesc_length) {
		this.orgdesc_length = orgdesc_length;
	}
	public boolean isExistField() {
		return isExistField;
	}
	public void setExistField(boolean isExistField) {
		this.isExistField = isExistField;
	}
	public String getWherestr() {
		return wherestr;
	}
	public void setWherestr(String wherestr) {
		this.wherestr = wherestr;
	}
	public String getWheresql() {
		return wheresql;
	}
	public void setWheresql(String wheresql) {
		this.wheresql = wheresql;
	}
	
}
