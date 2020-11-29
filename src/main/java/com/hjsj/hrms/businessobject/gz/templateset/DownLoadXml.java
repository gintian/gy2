package com.hjsj.hrms.businessobject.gz.templateset;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.TempvarBo;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.struts.upload.FormFile;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * 
 *<p>Title:DownLoadXml.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Sep 7, 2007</p> 
 *@author dengcan
 *@version 4.0
 */
public class DownLoadXml {
	public DownLoadXml(){
		
	}
	
//****************************导入工资类别********************************************* */	
	public static HashMap getAllSalarytemplate(Connection conn)
	{
		HashMap map = new HashMap();
		try
		{
			String sql = "select salaryid,cname from salarytemplate";
			ContentDAO dao = new ContentDAO(conn);
			RowSet rs = null;
			rs = dao.search(sql);
			while(rs.next())
			{
				map.put(rs.getString("salaryid"),rs.getString("salaryid"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	
/**
 * 插入  表数据
 * @param flag   0:无关联关系 1有关联关系  3:else
 * @param con
 * @param oKeyTOnKeyMap 主键 更改映射map
 * @param fileContext   文件内容
 * @param tableName     导入表名称
 * @param keyName       表主键 名称
 * @param foreignKeyName 外键名称
 * @param foreignKeyMap  外键 更改映射map
 * @param salaryids      导入数据约束
 */
	private HashMap sMap = new HashMap();
	public static void importTableData(int flag,Connection con,HashMap oKeyTOnKeyMap,String fileContext,String tableName,String keyName,String foreignKeyName,HashMap foreignKeyMap,String salaryids,HashMap hm)
	{
			try
			{
				ContentDAO dao=new ContentDAO(con);
				int maxID=0;
				RowSet rowSet=dao.search("select max("+keyName+") from "+tableName);
				if(rowSet.next())
					maxID=rowSet.getInt(1);
				int rsdtlid = DbNameBo.getPrimaryKey("reportdetail","rsdtlid",con);
				Document doc = PubFunc.generateDom(fileContext);
				ArrayList dataList=getTableData(doc,null,"");
				HashMap templatemap =null;
				if("salaryid".equalsIgnoreCase(keyName))
					templatemap= getAllSalarytemplate(con);//追加导入，但是存在类别相同的类别
				ArrayList list=new ArrayList();
				ArrayList tempVar = new ArrayList();
				for(int i=0;i<dataList.size();i++)
				{
					LazyDynaBean abean=(LazyDynaBean)dataList.get(i);
					String salaryid=(String)abean.get(foreignKeyName);
					if(salaryids!=null)
					{
						
						if("midvariable".equals(tableName))
						{
							if(salaryid!=null&&salaryid.length()>0)
							{
								if(salaryids.indexOf("#"+salaryid+"#")==-1)
									continue;
							}
							String cname=(String)abean.get("cname");
							String chz=(String)abean.get("chz");
							String cstate=(String)abean.get("cstate");
							/*if(hm.get(salaryid)!=null)//覆盖的
							{
								//覆盖的，以导入文件的数据为准，私有的临时变量删除，共有的如果chz或cname相同，也删除
								int type=executeEqualRecord(cname,chz,(String)hm.get(salaryid),dao,cstate,1);
								if(type==1)
									continue;
								if(type==2||type==3)
								{
									abean.set("cstate","");
								}
							}
							else{
								//追加的，以数据库中的为准，如果存在私有的临时变量chz或cname相同的，不导入，共有的也不导入
*/								int type=executeEqualRecord(cname,chz,salaryid,dao,cstate,2,con,tempVar);
								if(type==2||type==1||type==3)
								{
									continue;
								}
							//}
						}
						else
						{
							if(salaryids.indexOf("#"+salaryid+"#")==-1)
								continue;
						}
					}
					if(flag!=3)
					{	
						int id=Integer.parseInt((String)abean.get(keyName.toLowerCase()));
						if("salaryid".equalsIgnoreCase(keyName))
						{
					    	if(hm.get(salaryid)==null)//追加导入的，
					    	{
					    		if(templatemap.get(salaryid)!=null)
					    		{
					    			oKeyTOnKeyMap.put(String.valueOf(id),String.valueOf(++maxID));
		    			        	abean.set(keyName.toLowerCase(),String.valueOf(maxID));
					    		}
					    		else
					    		{
		    			        	oKeyTOnKeyMap.put(String.valueOf(id),String.valueOf(id));
		    			        	abean.set(keyName.toLowerCase(),String.valueOf(id));
					    		}
					    	}
					     	else
					    	{
					    		oKeyTOnKeyMap.put(String.valueOf(id),String.valueOf((String)hm.get(salaryid)));
		    		    		abean.set(keyName.toLowerCase(),String.valueOf((String)hm.get(salaryid)));
					    	}
						}else if("stid".equalsIgnoreCase(keyName)){//薪资报表  zhaoxg add 2014-11-10
					    	if(hm.get(salaryid)==null)//追加导入的，
					    	{	foreignKeyMap.put("rsdtlid"+abean.get("rsdtlid"), String.valueOf(rsdtlid));//记住追加后报表编号  为马上导入工资报表项目表所用
					    		foreignKeyMap.put("stid"+abean.get("stid"), (String)foreignKeyMap.get((String)abean.get(foreignKeyName.toLowerCase())));//记住薪资类别号
	    			        	abean.set("rsdtlid",String.valueOf((rsdtlid++)));				    		
	    			        	abean.set(keyName.toLowerCase(),(String)foreignKeyMap.get((String)abean.get(foreignKeyName.toLowerCase())));
					    	}else{
	    			        	abean.set(keyName.toLowerCase(),String.valueOf(id));
					    	}							

						}else if("rsdtlid".equalsIgnoreCase(keyName)){//工资报表项目表   zhaoxg add 2014-11-17
					    	if(foreignKeyMap.get("rsdtlid"+abean.get("rsdtlid"))!=null)//追加导入的，
					    	{
	    			        	abean.set("rsdtlid",foreignKeyMap.get("rsdtlid"+abean.get("rsdtlid")));		
	    			        	abean.set("stid",foreignKeyMap.get("stid"+abean.get("stid")));		
					    	}else{
	    			        	abean.set(keyName.toLowerCase(),String.valueOf(id));
					    	}							

						}else
						{
							oKeyTOnKeyMap.put(String.valueOf(id),String.valueOf(++maxID));
	    			    	abean.set(keyName.toLowerCase(),String.valueOf(maxID));
						}
						if(flag==1)
						{
							if("salaryid".equalsIgnoreCase(foreignKeyName))
							{
								if(hm.get(salaryid)==null)
						    	{
									abean.set(foreignKeyName.toLowerCase(),(String)foreignKeyMap.get((String)abean.get(foreignKeyName.toLowerCase())));
						    	}
						     	else
						    	{
			    		    		abean.set(foreignKeyName.toLowerCase(),String.valueOf((String)hm.get(salaryid)));
						    	}
							}
							else{
					     		abean.set(foreignKeyName.toLowerCase(),(String)foreignKeyMap.get((String)abean.get(foreignKeyName.toLowerCase())));
							}
						}
					}
					else
					{
						if("salaryid".equalsIgnoreCase(foreignKeyName))
						{
							if(hm.get(salaryid)==null)
					    	{
								abean.set(foreignKeyName.toLowerCase(),(String)foreignKeyMap.get((String)abean.get(foreignKeyName.toLowerCase())));
					    	}
					     	else
					    	{
		    		    		abean.set(foreignKeyName.toLowerCase(),String.valueOf((String)hm.get(salaryid)));
					    	}
						}
						else
						{
				    		abean.set(foreignKeyName.toLowerCase(),(String)foreignKeyMap.get((String)abean.get(foreignKeyName.toLowerCase())));
						}
					}
					list.add(abean);
				}
				HashMap salary_column_type=getColumnTypeMap(con,tableName);
				importData(con,list,salary_column_type,tableName);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
	}
	/**
	 * 覆盖导入处理方法
	 * @param cname
	 * @param chz
	 * @param salaryid
	 * @param dao
	 * @param cstate
	 * @param opt =1覆盖=2追加
	 * @return
	 */
	static StringBuffer error = new StringBuffer("");
	static StringBuffer error1 = new StringBuffer("");
	public static StringBuffer getError1() {
		return error1;
	}

	public static void setError1(StringBuffer error1) {
		DownLoadXml.error1 = error1;
	}

	public static StringBuffer getError2() {
		return error2;
	}

	public static void setError2(StringBuffer error2) {
		DownLoadXml.error2 = error2;
	}


	static StringBuffer error2 = new StringBuffer("");
	public static StringBuffer getError() {
		return error;
	}

	public static void setError(StringBuffer error) {
		DownLoadXml.error = error;
	}

	public static int executeEqualRecord(String cname,String chz,String salaryid,ContentDAO dao,String cstate,int opt,Connection con,ArrayList tempList)
	{
		int type=0;
		RowSet rs = null;
		try
		{
			//删掉重复的，第一次进入问题
			rs=dao.search("select nid,cstate,cname, chz from MidVariable where (UPPER(cname)='"+cname.toUpperCase()+"' or UPPER(chz)='"+chz.toUpperCase()+"') " +
					" and nflag=0 and templetID=0");
			while(rs.next())
			{
				String cstateValue=rs.getString("cstate");
				String tempcname = rs.getString("cname");
				String tempchz = rs.getString("chz");
				int nid=rs.getInt("nid");
				if(chz.equals(tempchz)){//chz重名
					if(cstate==null|| "".equals(cstate))//源共享临时变量查找库中所有(不导入)
					{
						type=1;
						if(!tempList.contains(chz)){
							tempList.add(chz);
							error1.append("  "+chz);
						}
					} else {
						if(cstateValue==null|| "".equals(cstateValue.trim()))//源私有，在库中共享的有chz重名
				    	{
							type=2;
							if(!tempList.contains(chz)){
								tempList.add(chz);
								error1.append("  "+chz);
							}
				    	}
					}
				}
				else {//cname重名，生成新的cname
					TempvarBo tempvarbo = new TempvarBo();
					int maxnid = tempvarbo.getid(con);
					//插入临时表之前先判断CName的值
					StringBuffer sqlquery = new StringBuffer("");
					String cname_new = "yk"+maxnid;
					sqlquery.append(" select count(*) from midvariable where cname='"+cname_new+"' ");
					try {
						rs = dao.search(sqlquery.toString());
						int num = 0;
						if(rs.next()){
							num = rs.getInt(1);
						}
						if(num > 0){
							cname_new = "ykC"+maxnid;
						}
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
					dao.update("update MidVariable set cname='"+cname_new+"' where nid="+nid);
					error1.append("  "+chz);
					error2.append("   源临时变量编号："+cname+", 新增临时变量编号："+cname_new);
					error2.append("\r\n");
					
				}
		/*		if(cstate==null||cstate.equals(""))
				{
					type=1;
					if(cstateValue!=null&&!cstateValue.equals(""))//如果导入的是共享，库中私有，将库中设为共享
					{
						int nid=rs.getInt("nid");
					    dao.update("update MidVariable set cstate=null where nid="+nid);
					}
				}
				else
				{
			    	if(cstateValue==null||cstateValue.trim().equals(""))
			    	{
			     		type=2;
			     		if(opt==1)//源私有，在库中共享的有chz重名
	    				{
	    				  int nid=rs.getInt("nid");
						  dao.delete("delete from MidVariable where nid="+nid, new ArrayList());
	    				}
			    	}
		    		else     //库中私有，导入也是私有
			    	{
		    			if(cstateValue.equalsIgnoreCase(cstate)&&opt==1)
		    			{
		    				type=3;
		    				if(opt==1)//数据库中的是私有，删除库中的，把导入的导入
		    				{
		    				  int nid=rs.getInt("nid");
							  dao.delete("delete from MidVariable where nid="+nid, new ArrayList());
		    				}
		    				if(opt==2)
		    				{
		    					int nid=rs.getInt("nid");
						    	dao.update("update MidVariable set cstate=null where nid="+nid);
		    				}
		    			}
		    			else
		    			{
		    				type=4;//存在相同，但是库中的不是一个工资类别，也不是共享，不用处理
		    				if(opt==2)
		    				{
		    					
		    				}
		    			}
		    			
		    			if(opt==1)
		    			{
		    				type=1;
		    				int nid=rs.getInt("nid");
							 dao.delete("delete from MidVariable where nid="+nid, new ArrayList());
		    			}
		    			if(opt==2)
	    				{
	    					int nid=rs.getInt("nid");
	    					dao.delete("delete from MidVariable where nid="+nid, new ArrayList());
	    				}
			    	}
				}*/
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally 
		{
			if(rs!=null)
			{
				try
				{
					rs.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		return type;
	}
	/**
	 * 插入  薪资公式  salaryformula 表数据
	 */
	public static void importSpecialTableData(Connection con,HashMap salaryTStandKeyMap,String salaryStandKey,String fileContext,String tableName,String keyName,String taxKeyName,HashMap taxKeyMap,String foreignKeyName,HashMap foreignKeyMap,String selectSalaryids,HashMap map)
	{
			try
			{
				ContentDAO dao=new ContentDAO(con);
				int maxID=0;
				RowSet rowSet=dao.search("select max("+keyName+") from "+tableName);
				if(rowSet.next())
					maxID=rowSet.getInt(1);
				
				Document doc = PubFunc.generateDom(fileContext);
				ArrayList dataList=getTableData(doc,null,"");
				
				HashMap amap = new HashMap();
				ArrayList list=new ArrayList();
				for(int i=0;i<dataList.size();i++)
				{
					LazyDynaBean abean=(LazyDynaBean)dataList.get(i);
					int id=Integer.parseInt((String)abean.get(keyName.toLowerCase()));
					++maxID;
					String salaryid=(String)abean.get("salaryid");
					if(selectSalaryids.indexOf("#"+salaryid+"#")==-1)
						continue;
				
					abean.set(keyName.toLowerCase(),String.valueOf(++maxID));

					String runflag=(String)abean.get("runflag");  //计算公式=0,执行标准=1, 执行税表=2,
					if("1".equals(runflag))
					{
						abean.set(salaryStandKey.toLowerCase(),(String)salaryTStandKeyMap.get((String)abean.get(salaryStandKey.toLowerCase())));
					}
					else if("2".equals(runflag))
					{
						abean.set(taxKeyName.toLowerCase(),(String)taxKeyMap.get((String)abean.get(taxKeyName.toLowerCase())));
					}
					String dd=(String)abean.get(foreignKeyName.toLowerCase());
					abean.set(foreignKeyName.toLowerCase(),(String)foreignKeyMap.get((String)abean.get(foreignKeyName.toLowerCase())));
					if("salaryformula".equalsIgnoreCase(tableName)&& "salaryid".equalsIgnoreCase(foreignKeyName))
					{
						if("2".equals(runflag))
						{
				    		String salarynewid=(String)foreignKeyMap.get(dd);
				    		int newid=maxID;
				    		int j=0;
				    		if(amap.get(salarynewid)!=null)
				    			j=Integer.parseInt(((String)amap.get(salarynewid)));
					    	SalaryCtrlParamBo salarybo = new SalaryCtrlParamBo(con,Integer.parseInt(salarynewid));
					    	salarybo.updateXML_IDValue(SalaryCtrlParamBo.YS_FIELDITEM, newid+"",j);
					    	salarybo.saveParameter();
					    	j++;
					    	amap.put(salarynewid, j+"");
						}
					}
					list.add(abean);
				}
				HashMap salary_column_type=getColumnTypeMap(con,tableName);
				importData(con,list,salary_column_type,tableName);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
	}
	/**
	 * 导入审核公式  
	 * @param con
	 * @param fileContext
	 * @param tableName
	 * @param keyName
	 * @param selectSalaryids
	 */
	public static void importSpFormulaData(Connection con,String fileContext,String tableName,String keyName,String selectSalaryids,String foreignKeyName,HashMap foreignKeyMap)
	{
			try
			{
				ContentDAO dao=new ContentDAO(con);
				String maxID="";
				IDGenerator idg = new IDGenerator(2, con);
				int seq = 0;
				String sql = "select max(seq) seq from hrpchkformula ";
				RowSet rs = dao.search(sql);
				while(rs.next())
				{
					seq = rs.getInt("seq");
				}				
				Document doc = PubFunc.generateDom(fileContext);
				ArrayList dataList=getTableData(doc,null,"");
				
				ArrayList list=new ArrayList();
				for(int i=0;i<dataList.size();i++)
				{
					LazyDynaBean abean=(LazyDynaBean)dataList.get(i);
					String tabid=(String)abean.get("tabid");
					if(selectSalaryids.indexOf("#"+tabid+"#")==-1)
						continue;
					maxID = idg.getId("hrpchkformula.chkid");
					abean.set(keyName.toLowerCase(),maxID);
					seq++;
					abean.set("seq", seq+"");
					abean.set(foreignKeyName.toLowerCase(),(String)foreignKeyMap.get((String)abean.get(foreignKeyName.toLowerCase())));
					list.add(abean);
				}
				HashMap salary_column_type=getColumnTypeMap(con,tableName);
				importData(con,list,salary_column_type,tableName);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
	}
	
	
	
	
	
	
	
	
	public static void importData(Connection con,ArrayList dataList,HashMap columnMap,String tableName)
	{
		Set keySet=columnMap.keySet();
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(con);
			Calendar d=Calendar.getInstance();
			for(int i=0;i<dataList.size();i++)
			{
				LazyDynaBean abean=(LazyDynaBean)dataList.get(i);
				RecordVo record_vo=new RecordVo(tableName);
				for(Iterator t=keySet.iterator();t.hasNext();)
				{
					String columnName=((String)t.next()).toLowerCase();					
					{
						String type=(String)columnMap.get(columnName);
						String value=(String)abean.get(columnName);
						if(value!=null&&value.length()>0)
						{
							value = value.replaceAll("<br>","\r\n");//把br转成回车符入库，由于xml文件不能识别回车符，所以导出时候回车符转成<br>了  zhaoxg add 2014-7-18
							if("D".equals(type))
							{
								String[] values=value.split("-");
								Calendar dd=Calendar.getInstance();
								dd.set(Calendar.YEAR,Integer.parseInt(values[0]));
								dd.set(Calendar.MONTH,Integer.parseInt(values[1])-1);
								dd.set(Calendar.DATE,Integer.parseInt(values[2]));
								record_vo.setDate(columnName,dd.getTime());
								
							}
							else if("F".equals(type))
							{
								record_vo.setDouble(columnName,Double.parseDouble(value));
								
							}
							else if("N".equals(type))
							{
								record_vo.setInt(columnName,Integer.parseInt(value));
							}
							else
							{
								record_vo.setString(columnName,value);
							}
							
						}
						
					}
				}
				list.add(record_vo);
			}
			dao.addValueObject(list);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	//****************************************end*************************************************
	/**
	 * 导入工资标准
	 * @param flag   1: 覆盖导入   2:追加导入
	 * @param importStandardIds
	 * @param form_file
	 */
	public static void impotFile(Connection con,String flag,String[] importStandardIds,FormFile form_file,ArrayList gzStandardPackageInfo,String createOrgValue,UserView userView) throws GeneralException
	{
		InputStream stream=null;
		try
		{
			stream=form_file.getInputStream();
			HashMap fileMap=extZipFileList(stream) ;
			String  startPkgID=getStartPkgID(con);
			
			Document standard_doc = PubFunc.generateDom((String)fileMap.get("gz_stand.xml"));
			
			Document item_doc = PubFunc.generateDom((String)fileMap.get("gz_item.xml"));
			
			ArrayList gz_stand_data=new ArrayList();
			ArrayList gz_item_data=new ArrayList();
			gz_stand_data=getTableData(standard_doc,importStandardIds,"2");
			gz_item_data=getTableData(item_doc,importStandardIds,"3");
			HashMap standIdMap =new HashMap();
			/**不能覆盖的标准*/
			HashMap map = DownLoadXml.getHasNoCreateOrgStand(startPkgID, con, createOrgValue);
			HashMap amap = new HashMap();
			if("1".equals(flag))
			{
				deleteFromExistData(con,gzStandardPackageInfo,importStandardIds,startPkgID,map);
				gz_stand_data=editStandDataList(con,gz_stand_data,standIdMap,1,map,amap);
				gz_item_data=editStandDataList(con,gz_item_data,standIdMap,2,map,amap);
			}
			else if("2".equals(flag))
			{
				gz_stand_data=editStandDataList(con,gz_stand_data,standIdMap,1);
				gz_item_data=editStandDataList(con,gz_item_data,standIdMap,2);
			}
			
			importData(con,gz_stand_data,gz_item_data,startPkgID,createOrgValue,amap,flag);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally
        {
            PubFunc.closeIoResource(stream);
        }
	}
    /**
     * 不能覆盖的工资标准集合
     * @param pkgid
     * @param con
     * @param createOrgValue
     * @return
     */
	public static HashMap getHasNoCreateOrgStand(String pkgid,Connection con,String createOrgValue)
	{
		HashMap map = new HashMap();
		RowSet rs = null;
		try
		{
			ContentDAO dao = new ContentDAO(con);
			rs = dao.search("select id,pkg_id,createorg from gz_stand_history where pkg_id="+pkgid);
			while(rs.next())
			{
				String org = rs.getString("createorg");
				if(org==null|| "".equals(org))
				{
					continue;
				}else
				{
					if(org.equalsIgnoreCase(createOrgValue))
					{
						continue;
					}
					else{
						map.put(rs.getString("id"), "1");
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			if(rs!=null)
			{
				try
				{
					rs.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		return map;
	}
	
	
	public static void importData(Connection con,ArrayList gz_stand_data,ArrayList gz_item_data,String pkg_id,String createOrgValue,HashMap amap,String flag)
	{
		try
		{
			
			HashMap gz_stand_column_type=getColumnTypeMap(con,"gz_stand");
			HashMap gz_item_column_type=getColumnTypeMap(con,"gz_item");
			HashMap gz_standHistory_column_type=getColumnTypeMap(con,"gz_stand_history");
			HashMap gz_itemHistory_column_type=getColumnTypeMap(con,"gz_item_history");
			
			importData2(con,gz_stand_data,gz_stand_column_type,1,pkg_id,createOrgValue,amap,flag);
	 		importData2(con,gz_item_data,gz_item_column_type,2,pkg_id,createOrgValue,amap,flag);
	 		importData2(con,gz_stand_data,gz_standHistory_column_type,3,pkg_id,createOrgValue,amap,flag);
	 		importData2(con,gz_item_data,gz_itemHistory_column_type,4,pkg_id,createOrgValue,amap,flag);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param dataList
	 * @param columnMap
	 * @param flag 1:gz_stand  2:gz_item  3:gz_stand_history  4:gz_item_history
	 */
	public static void importData2(Connection con,ArrayList dataList,HashMap columnMap,int flag,String pkg_id,String createOrgValue,HashMap amap,String aflag)
	{
		Set keySet=columnMap.keySet();
		ArrayList list=new ArrayList();
		
		ArrayList valuelist=new ArrayList();
		String sql="";
		StringBuffer names = new StringBuffer();
		try
		{
			//afalg=1覆盖
			ContentDAO dao=new ContentDAO(con);
			Calendar d=Calendar.getInstance();
			int z=0;
			for(int i=0;i<dataList.size();i++)
			{ 
				z++;//计数器，拼sql用的 循环一次获得字段就行了
				LazyDynaBean abean=(LazyDynaBean)dataList.get(i);
				
				RecordVo record_vo=null;
				ArrayList list1=new ArrayList();
				
				if(flag==1){
					record_vo=new RecordVo("gz_stand");
					sql="insert into gz_stand ";
				}else if(flag==2){
					record_vo=new RecordVo("gz_item");
					sql="insert into gz_item ";
				}else if(flag==3){
					record_vo=new RecordVo("gz_stand_history");
					sql="insert into gz_stand_history ";
				}else if(flag==4){
					record_vo=new RecordVo("gz_item_history");
					sql="insert into gz_item_history ";
				}
				for(Iterator t=keySet.iterator();t.hasNext();)
				{
					String columnName=((String)t.next()).toLowerCase();
					if(columnName!=null&&!"".equals(columnName)&&z==1){
						names.append(columnName);
						names.append(",");
					}

					if(flag==1&& "flag".equalsIgnoreCase(columnName)){
						record_vo.setInt("flag",1);
						list1.add(new Integer(1));
					}else if(flag==3&& "createtime".equalsIgnoreCase(columnName)){
						record_vo.setDate("createtime",d.getTime());
						list1.add(DateUtils.getSqlDate(d.getTime()));
					}else if(flag==3&& "pkg_id".equalsIgnoreCase(columnName)){
						record_vo.setInt("pkg_id",Integer.parseInt(pkg_id));
						list1.add(new Integer(pkg_id));
					}else if(flag==4&& "pkg_id".equalsIgnoreCase(columnName)){
						record_vo.setInt("pkg_id",Integer.parseInt(pkg_id));
						list1.add(new Integer(pkg_id));
					}else if(flag==3&& "b0110".equalsIgnoreCase(columnName))
					{
						String id = (String)abean.get("id");
						/*if(aflag.equals("1")&&amap.get(id)==null)//覆盖
						{*/
						if(abean.get(columnName)==null|| "".equals((String)abean.get(columnName)))
						{
							record_vo.setString("b0110", null);
							list1.add(null);
						}else
						{
							record_vo.setString("b0110", (String)abean.get(columnName));
							list1.add((String)abean.get(columnName));
						}
						/*}else{
					    	if(createOrgValue!=null)
					    	{
						    	record_vo.setString("b0110", createOrgValue.equalsIgnoreCase("UN")?null:(","+createOrgValue));
					     	}
					    	if(createOrgValue!=null)
					    	{
						    	record_vo.setString("b0110", null);
					     	}
						}*/
					}
					else if(flag==3&& "createorg".equalsIgnoreCase(columnName))
					{
						String id = (String)abean.get("id");
						if("1".equals(aflag)&&amap.get(id)==null)//覆盖
						{
							if(abean.get(columnName)==null|| "".equals((String)abean.get(columnName)))
							{
								record_vo.setString("createorg", null);
								list1.add(null);
							}else
							{
								record_vo.setString("createorg", (String)abean.get(columnName));
								list1.add((String)abean.get(columnName));
							}
						}else{
							record_vo.setString("createorg", createOrgValue);
							list1.add(createOrgValue);
						}
					}
					else
					{
						String type=(String)columnMap.get(columnName);
						String value=(String)abean.get(columnName);
						if(value!=null&&value.length()>0)
						{
							if("D".equals(type))
							{
								String[] values=value.split("-");
								Calendar dd=Calendar.getInstance();
								dd.set(Calendar.YEAR,Integer.parseInt(values[0]));
								dd.set(Calendar.MONTH,Integer.parseInt(values[1])-1);
								dd.set(Calendar.DATE,Integer.parseInt(values[2]));
								record_vo.setDate(columnName,dd.getTime());
								list1.add(DateUtils.getSqlDate(dd.getTime()));
							}
							else if("F".equals(type))
							{
								record_vo.setDouble(columnName,Double.parseDouble(value));
								list1.add(new Double(value));
							}
							else if("N".equals(type))
							{
								record_vo.setInt(columnName,Integer.parseInt(value));
								list1.add(new Integer(value));
							}
							else
							{
								record_vo.setString(columnName,value);
								list1.add(value);
							}
							
						}else{
							if("D".equals(type))
							{
								String[] values=value.split("-");
								Calendar dd=Calendar.getInstance();
								dd.set(Calendar.YEAR,Integer.parseInt(values[0]));
								dd.set(Calendar.MONTH,Integer.parseInt(values[1])-1);
								dd.set(Calendar.DATE,Integer.parseInt(values[2]));
								record_vo.setDate(columnName,dd.getTime());
								list1.add(DateUtils.getSqlDate(dd.getTime()));
							}
							else if("F".equals(type))
							{
								record_vo.setDouble(columnName,Double.parseDouble(value));
								list1.add(new Double(0));
							}
							else if("N".equals(type))
							{
								record_vo.setInt(columnName,Integer.parseInt(value));
								list1.add(new Integer(0));
							}
							else
							{
								list1.add(null);
							}
						}
						
					}
				}
				list.add(record_vo);
				valuelist.add(list1);
			}
			//dao.addValueObject(list); zhaoxg add前程序   
			StringBuffer value = new StringBuffer();
			for(int i=0;i<columnMap.size();i++){
				value.append("?");
				value.append(",");
			}
			String _sql = sql+"("+names.toString().substring(0, names.toString().length()-1)+") values ("+value.toString().substring(0, value.length()-1)+")";
			int num = valuelist.size()/1000;//批量增加，提速  zhaoxg add 2013-9-22
			if(valuelist.size()%1000!=0){
				num++;
			}
			ArrayList templist = null;
			for(int n=0;n<num;n++){
				templist = new ArrayList();
				for(int x=n*1000;x<(n+1)*1000;x++){
					if(x>=valuelist.size()){
						break;
					}
					templist.add(valuelist.get(x));
				}
				dao.batchInsert(_sql, templist);
			}
			

			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * 取得表列类型
	 * @param tableName
	 * @return
	 */
	public static HashMap getColumnTypeMap(Connection con,String tableName)
	{
		HashMap columnTypeMap=new HashMap();
		try
		{
			ContentDAO dao = new ContentDAO(con);
			ResultSet resultSet=dao.search("select * from "+tableName+" where 1=2");
			ResultSetMetaData metaData=resultSet.getMetaData();
			for (int i = 1; i <= metaData.getColumnCount(); i++) {
				String name=metaData.getColumnName(i).toLowerCase();
				String type="A";
				int  columnType=metaData.getColumnType(i);
				switch (columnType) {
				case Types.LONGVARCHAR:
				{
					type="M";
					break;
				}
				case Types.TINYINT:
				{
					type="N";
					break;
				}
				case Types.SMALLINT:
				{
					type="N";
					break;
				}
				case Types.INTEGER:
				{
					type="N";
					break;
				}
				case Types.BIGINT:
				{
					type="N";
					break;
				}
				case Types.FLOAT:
				{
					type="F";
					break;
				}
				case Types.DOUBLE:
				{
					type="F";
					break;
				}
				case Types.DECIMAL:
				{
					type="F";
					break;
				}
				case Types.NUMERIC:
				{
					type="F";
					break;
				}
				case Types.REAL: 
				{
					type="F";
					break;
				}
				case Types.DATE:
				{
					type="D";
					break;
				}
				case Types.TIME:
				{
					type="D";
					break;
				}
				case Types.TIMESTAMP: {
					type="D";
					break;
				}
				default:
					type="A";
					break;
				}
				columnTypeMap.put(name,type);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return columnTypeMap;
	}
	
	
	
	/**
	 * 
	 * @param con
	 * @param dataList
	 * @param standIdMap
	 * @param flag  1 standData  2itemData
	 * @return
	 */
	public static ArrayList editStandDataList(Connection con,ArrayList dataList,HashMap standIdMap,int flag)
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(con);
			int maxID=0;
			if(flag==1)
			{
				RowSet rowSet=dao.search("select max(id) from gz_stand");
				if(rowSet.next())
					maxID=rowSet.getInt(1);
			}
			
			for(int i=0;i<dataList.size();i++)
			{
				LazyDynaBean abean=(LazyDynaBean)dataList.get(i);
				String id=(String)abean.get("id");
				if(flag==1)
				{
					if(standIdMap.get(id)==null)
					{
						standIdMap.put(id,String.valueOf(++maxID));
					}
				}
				abean.set("id",(String)standIdMap.get(id));
				list.add(abean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	public static ArrayList editStandDataList(Connection con,ArrayList dataList,HashMap standIdMap,int flag,HashMap map,HashMap amap)
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(con);
			int maxID=0;
			if(flag==1&&map.size()>0)
			{
				RowSet rowSet=dao.search("select max(id) from gz_stand");
				if(rowSet.next())
					maxID=rowSet.getInt(1);
			}
			
			for(int i=0;i<dataList.size();i++)
			{
				LazyDynaBean abean=(LazyDynaBean)dataList.get(i);
				String id=(String)abean.get("id");
				if(flag==1)
				{
					if(standIdMap.get(id)==null&&map.get(id)!=null)
					{
						standIdMap.put(id,String.valueOf(++maxID));
						amap.put(maxID+"", "1");
					}else if(standIdMap.get(id)==null){
						standIdMap.put(id,String.valueOf(id));
					}
				}
				abean.set("id",(String)standIdMap.get(id));
				list.add(abean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	
	
	/**
	 * 取得当前历史沿革
	 * @param con
	 * @return
	 */
	public static String getStartPkgID(Connection con)
	{
		String pkg_id="";
		try
		{
			ContentDAO dao=new ContentDAO(con);
			RowSet rowSet=dao.search("select pkg_id from gz_stand_pkg where status='1'");
			if(rowSet.next())
				pkg_id=rowSet.getString(1);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return pkg_id;
	}
	
	

	
	
	
	static void deleteFromExistData(Connection con,ArrayList gzStandardPackageInfo,String[] importStandardIds,String startPkgID,HashMap noCrtMap) throws GeneralException
	{
		try
		{
			StringBuffer allowIDs=new StringBuffer("");
			StringBuffer info=new StringBuffer("");
			for(int i=0;i<importStandardIds.length;i++)
			{
				if(noCrtMap.get(importStandardIds[i])!=null)//没权限覆盖的
				{
					info.append(","+importStandardIds[i]);
				}
				allowIDs.append("#"+importStandardIds[i]+"#");
			}
			
			if(info.length()>0)
				throw GeneralExceptionHandler.Handle(new Exception("下列薪资标准无权限覆盖！<br>薪资标准号："+info.substring(1)));
			
			ContentDAO dao=new ContentDAO(con);
			
			int num=gzStandardPackageInfo.size()/200;
			if(gzStandardPackageInfo.size()%200!=0)
				num++;
			for(int n=0;n<num;n++)
			{
				StringBuffer pk_whl=new StringBuffer("");
				StringBuffer stand_whl=new StringBuffer("");
				StringBuffer stand_whl2=new StringBuffer("");
				for(int i=n*200;i<(n+1)*200;i++)
				{
					if(gzStandardPackageInfo.size()>i)
					{ 
						LazyDynaBean abean=(LazyDynaBean)gzStandardPackageInfo.get(i);
						String flag=(String)abean.get("flag");
						String name=(String)abean.get("name");
						String id=(String)abean.get("id"); 
						/**如果不是自己创建的，不能删除*/
						if(allowIDs.indexOf("#"+id+"#")!=-1)
						{
							if(noCrtMap.get(id)!=null)//没权限覆盖的
								continue;
							stand_whl2.append(" or  (pkg_id="+startPkgID+" and id="+id+")");
							stand_whl.append(","+id);
						}
						
					}else{
						break;
					}		
					
				}
				if(stand_whl.length()>0)
				{
					dao.delete("delete from gz_stand where id in ("+stand_whl.substring(1)+")",new ArrayList());
					dao.delete("delete from gz_item where id in ("+stand_whl.substring(1)+")",new ArrayList());			
					dao.delete("delete from gz_stand_history where "+stand_whl2.substring(3),new ArrayList());
					dao.delete("delete from gz_item_history where "+stand_whl2.substring(3),new ArrayList());
				}
			}															
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	
	
	
	/**
	 * //取得导入数据
	 * @param doc
	 * @param importStandardIds  需导入的数据id
	 * @param type    1：gz_stand_pkg  2:gz_stand   3:gz_item   4: 
	 * @return
	 */
	public static ArrayList getTableData(Document doc,String[] importStandardIds,String type)
	{
		ArrayList list=new ArrayList();
		try
		{
			StringBuffer allowIDs=new StringBuffer("");
			if(importStandardIds!=null)
			{
				for(int i=0;i<importStandardIds.length;i++)
				{
					allowIDs.append("#"+importStandardIds[i]);
				}
			}
			allowIDs.append("#");
		
			Element root=doc.getRootElement();
			List childrenList=root.getChildren();
			LazyDynaBean a_bean=null;
			for(Iterator t=childrenList.iterator();t.hasNext();)
			{
				Element record=(Element)t.next();
				
				a_bean =new LazyDynaBean();
				List attributes=record.getAttributes();
				for(int i=0;i<attributes.size();i++)
				{
					Attribute att=(Attribute)attributes.get(i);
					a_bean.set(att.getName().toLowerCase(),att.getValue());					
				}
				List children=record.getChildren();
				for(int i=0;i<children.size();i++)
				{
					Element att=(Element)children.get(i);
					a_bean.set(att.getName().toLowerCase(),att.getValue());					
				}
				
				boolean isAdd=false;  //判断是否是需导入的数据
				if(importStandardIds==null)  //所有
				{
					isAdd=true;
				}
				else if("1".equals(type)) //gz_stand_pkg
				{
					String id=(String)a_bean.get("pkg_id");
					if(allowIDs.indexOf("#"+id+"#")!=-1)
						isAdd=true;
					
				}
				else if("2".equals(type)|| "3".equals(type)) //gz_stand
				{
					String id=(String)a_bean.get("id");
					
					if(allowIDs.indexOf("#"+id+"#")!=-1)
						isAdd=true;
				}
				
				
				if(isAdd)
					list.add(a_bean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	
	
///////////////////------分析工资标准--------////////////////////////	
	public static  ArrayList AnalyseImportStandard(FormFile form_file)
	{
		ArrayList list=new ArrayList();
		InputStream in = null;
		try {
			in = form_file.getInputStream();
			HashMap fileMap=extZipFileList(in) ;
			list=getStandardList((String)fileMap.get("gz_stand.xml"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{PubFunc.closeResource(in);}
		return list;
	}
	
	
	
	static ArrayList getStandardList(String stand_context)
	{
		ArrayList list=new ArrayList();
		
		try
		{
			
			if(stand_context==null||stand_context.length()==0)
				return list;
			
	//		getTableData(pkg_context);
			
			
			Document standard_doc = PubFunc.generateDom(stand_context);
			getStandardList(standard_doc,list);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	/*	
		for(Iterator t=list.iterator();t.hasNext();)
		{
			LazyDynaBean bean=(LazyDynaBean)t.next();
			System.out.println((String)bean.get("flag")+"   "+(String)bean.get("id")+"    "+(String)bean.get("name"));
			
		}
		*/
		return list;
	}
	
	
	static void getStandardList(Document doc,ArrayList list)
	{
		
		try
		{
			LazyDynaBean a_bean=null;
			Element root=doc.getRootElement();
			List nodeList=root.getChildren();
			for(Iterator t=nodeList.iterator();t.hasNext();)
			{
				Element record=(Element)t.next();
				String id=record.getAttributeValue("ID");
				XPath xPath0 = XPath.newInstance("./NAME");
				Element nameNode = (Element) xPath0.selectSingleNode(record);
				String name=nameNode.getValue();
				
				a_bean=new LazyDynaBean();
				a_bean.set("flag","1");   //工资标准
				a_bean.set("name",name);
				a_bean.set("id",id);
				list.add(a_bean);
			
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	
	}
	
	
	/**
	 * 读取压缩包里的文件
	 * @param inputStream
	 * @return
	 */
	public static   HashMap   extZipFileList(InputStream inputStream)  
	{   
		  HashMap fileMap=new HashMap();
		  try   
		  {   
				  ZipInputStream   in   =   new   ZipInputStream(inputStream);   
				  ZipEntry   entry   =   null;   
			      while   ((entry =in.getNextEntry())!=null)   
			      {     
					  if   (entry.isDirectory())   {   
						  continue;
						  /*
						  File   file   =   new   File(extPlace   +   entryName);   
						  file.mkdirs();   
						  System.out.println("创建文件夹:"   +   entryName);  
						  */ 
					  }   
					  else   
					  {   
						 String entryName=entry.getName(); 
						 BufferedReader ain=new BufferedReader(new InputStreamReader(in));
						 StringBuffer s=new StringBuffer("");
						 String line;
						 while((line=ain.readLine())!=null)
						 {
						 	 //已有的xml，导入时编码改为UTF-8
							 line= line.replace("encoding=\"GB2312\"", "encoding=\"UTF-8\"").replace("encoding=\"gb2312\"", "encoding=\"UTF-8\"");
							 s.append(line);
						 }
						 in.closeEntry();   
						 fileMap.put(entryName.toLowerCase(),s.toString());
						 //System.out.println(s.toString());   
					}   
			  }  
			  in.close();
		    
		  }   
		  catch   (IOException   e)   {   
			  e.printStackTrace();
		  }   
		  return fileMap;
	}   
	
	
	
////////////////////导出工资标准////////////////////////////	
	
	/**
	 * 导出工资标准
	 * @param con
	 * @return
	 */
	public static String outPutXmlInfo(Connection con,String ids)
	{
		String fileName="ZipOutOfStandard.zip";
		ZipOutputStream outputStream = null;
		FileOutputStream fileOut = null;
		FileInputStream fileIn = null;
		BufferedInputStream origin = null;
		try {
			produceFolder();   //产生newdata文件夹
			SalaryStandardPackBo bo=new SalaryStandardPackBo(con);
			LazyDynaBean abean=null;
			String outName="";		
			//导出gz_stand_pkg
		/*	 abean=bo.getOutPutTableInfo(ids,"1");
			 outName="gz_stand_pkg.xml";
			 fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+"newdata"+System.getProperty("file.separator")+outName);
			 fileOut.write(getXmlContent(abean));
			 fileOut.close();  */
			 //导出gz_stand
			 abean=bo.getOutPutTableInfo(ids,"2");
			 outName="gz_stand.xml";
			 try{
				 fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+"newdata"+System.getProperty("file.separator")+outName);
				 fileOut.write(getXmlContent(abean));
			 }finally{
				 PubFunc.closeResource(fileOut);
			 }
			
			//导出gz_item
			 abean=bo.getOutPutTableInfo(ids,"3");
			 outName="gz_item.xml";
			 fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+"newdata"+System.getProperty("file.separator")+outName);
			 fileOut.write(getXmlContent(abean));
			 fileOut.close();
			 
			 //压缩文件
			 ArrayList fileNames = new ArrayList(); // 存放文件名,并非含有路径的名字
			 ArrayList files = new ArrayList(); // 存放文件对象
				
			fileOut = new FileOutputStream(
					System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+fileName);
			outputStream = new ZipOutputStream(fileOut);
			File rootFile = new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+"newdata");
			listFile(rootFile, fileNames, files);
			byte data[] = new byte[2048];
		
			for (int loop = 0; loop < files.size(); loop++) {
				String a_fileName=(String) fileNames.get(loop);
				if(!"gz_stand.xml".equalsIgnoreCase(a_fileName)&&!"gz_item.xml".equalsIgnoreCase(a_fileName))
					continue;
				fileIn = new FileInputStream((File) files.get(loop));
				
				origin = new BufferedInputStream(fileIn, 2048);
				outputStream.putNextEntry(new ZipEntry((String) fileNames.get(loop)));
				int count;
				while ((count = origin.read(data, 0, 2048)) != -1) {
					outputStream.write(data, 0, count);
				}
				origin.close();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(outputStream);
			PubFunc.closeResource(origin);
			PubFunc.closeResource(fileIn);
			PubFunc.closeResource(fileOut);
		}
		return fileName;
	}
	
	
	
	public static byte[] getXmlContent(LazyDynaBean abean)
	{
		Element root = new Element("root");
		String columns=(String)abean.get("columns");
		String keycolumns=(String)abean.get("keycolumns");
		root.setAttribute("columns",columns);
		root.setAttribute("rowcount",(String)abean.get("rowcount"));
		root.setAttribute("keycolumns",keycolumns);
		
		ArrayList records=(ArrayList)abean.get("records");
		for(int i=0;i<records.size();i++)
		{
			LazyDynaBean bean=(LazyDynaBean)records.get(i);
			Element record  = new Element("record");
			if(keycolumns.trim().length()>0)
			{
				String[] temps=keycolumns.split(",");
				for(int j=0;j<temps.length;j++)
				{
					record.setAttribute(temps[j],(String)bean.get(temps[j]));
				}
			}
			
			if(columns.trim().length()>0)
			{
				String[] temps=columns.split(",");
				for(int j=0;j<temps.length;j++)
				{
					Element d  = new Element(temps[j]);
					d.addContent((String)bean.get(temps[j]));
					record.addContent(d);
				}
			}
			root.addContent(record);
		}
		Document myDocument = new Document(root);
		XMLOutputter outputter = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);
		return outputter.outputString(myDocument).getBytes();
	}
	
	
	
	//产生newdata文件夹
	public static void produceFolder()
	{
		if(!(new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+"newdata/").isDirectory()))
		{
			new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+"newdata/").mkdir();
				
		}
	}
	
	
	
	public static void main(String[] args)
	{
	    FileOutputStream fos = null;
		try
		{
			if(!(new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+"newdata/").isDirectory()))
			{
				new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+"newdata/").mkdir();
					
			}
			File myFile = new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+"newdata/myfile.txt ");
			fos = new FileOutputStream(myFile);
			fos.write(11);
		}
		catch(Exception e)
		{
		         System.out.println("can not make directory");
		} finally{
		    if(fos != null)
		        PubFunc.closeIoResource(fos);
		}
		
		/*
		ArrayList fileNames = new ArrayList(); // 存放文件名,并非含有路径的名字
		ArrayList files = new ArrayList(); // 存放文件对象
		
		try
		{
			FileOutputStream fileOut = new FileOutputStream("E:/ZipOutOfPath.zip");
			ZipOutputStream outputStream = new ZipOutputStream(fileOut);
		
			File rootFile = new File("E:/temp");
			listFile(rootFile, fileNames, files);      
			for (int loop=0; loop<files.size(); loop++)
			{
				FileInputStream fileIn = new FileInputStream((File)files.get(loop));
				outputStream.putNextEntry(new ZipEntry((String)fileNames.get(loop)));
				byte[] buffer = new byte[1024];
				while (fileIn.read(buffer) != -1)
				{
					outputStream.write(buffer);
				}
				outputStream.closeEntry();
				fileIn.close();
			}
				outputStream.close();
			}catch (IOException ioe){
				ioe.printStackTrace();
			}
			
			*/
		
		}
		
		public static void  listFile(File parentFile, List nameList, List fileList)
		{
			if (parentFile.isDirectory())
			{
				File[] files = parentFile.listFiles();
				for (int loop=0; loop<files.length; loop++)
				{
					listFile(files[loop], nameList, fileList);
				}
			}
			else
			{
				fileList.add(parentFile);
				nameList.add(parentFile.getName());
			}
		}
		
}
