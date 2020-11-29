/**
 * 
 */
package com.hjsj.hrms.businessobject.gz;

import com.hjsj.hrms.businessobject.gz.templateset.DownLoadXml;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.RowSetToXmlBuilder;
import com.hjsj.hrms.businessobject.sys.logonuser.UserObjectBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.collections.FastHashMap;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.upload.FormFile;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.io.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
/**
 * <p>
 * Title:薪资类别包
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:HJHJ
 * </p>
 * <p>
 * Create time:2007-6-20:上午09:07:24
 * </p>
 * 
 * @author cmq
 * @version 4.0
 */
public class SalaryPkgBo {
	/**
	 * 薪资类别 =0 薪资 =1 保险
	 */
	private int gz_type=0;
	
	private Connection conn=null;
	/** 登录用户 */
	private UserView userview=null;
	/** 所有薪资类别中项目字典 */
	private static FastHashMap allitem_hm=null;
	
	private int show_reject=0;   // 1:显示驳回状态  0：不显示驳回状态  
	
	/**
	 * gz_type=0 薪资 =1 保险
	 */
	public SalaryPkgBo(Connection conn,UserView userview,int gz_type) {
		this.conn=conn;
		this.userview=userview;
		this.gz_type=gz_type;
	}
	
	
	
	public SalaryPkgBo(Connection conn,UserView userview) {
		this.conn=conn;
		this.userview=userview;
	}
	
	
	
	/**
	 * 取得 需审批的薪资，保险类别列表
	 * 
	 * @return list
	 */
	public  ArrayList getEndorseRecords()
	{
		ArrayList list=new ArrayList();
		try
		{
			if(!this.userview.isHavesalaryid()&&!this.userview.isSuper_admin())
				return list;
				
			ContentDAO dao=new ContentDAO(this.conn);
			String sql="select  salarytemplate.salaryid,salarytemplate.cname,salarytemplate.cstate from salarytemplate "
					+" where  exists ( select salaryid from salaryhistory where  salaryhistory.salaryid=salarytemplate.salaryid  and ( salaryhistory.sp_flag='02' or salaryhistory.sp_flag='07'  ) and   NULLIF(salaryhistory.appuser,'') is not null  and lower(salaryhistory.curr_user)='"+this.userview.getUserName().toLowerCase()+"' ) "
					+"  order by salarytemplate.cstate,salarytemplate.salaryid";
			String _sql="";
			RowSet rset=dao.search(sql);
			RowSet rset1=null;
			HashMap map=new HashMap();
			SimpleDateFormat df0=new SimpleDateFormat("yyyy.MM.dd");
			while(rset.next())
			{
				String salaryid=rset.getString("salaryid");
				String cname=rset.getString("cname");
				String cstate=rset.getString("cstate")!=null?rset.getString("cstate").trim():"";
				LazyDynaBean abean=new LazyDynaBean();
				String str="保险";
				if(cstate.trim().length()==0)
					str="薪资";
				
				if(!this.userview.isSuper_admin())
				{
					if(cstate.trim().length()==0)
					{
						if(!this.userview.isHaveResource(IResourceConstant.GZ_SET, rset.getString("salaryid")))
							continue;
					}
					else
					{
						if(!this.userview.isHaveResource(IResourceConstant.INS_SET, rset.getString("salaryid")))
							continue;
					}
				}
				
				//从待办进来，默认选中业务日期  2013-11-27  dengc
				_sql="select distinct a00z2,a00z3,sp_flag from salaryhistory where  salaryhistory.salaryid="+salaryid+"  and ( salaryhistory.sp_flag='02' or salaryhistory.sp_flag='07'  ) "
					+" and   NULLIF(salaryhistory.appuser,'') is not null  and lower(salaryhistory.curr_user)='"+this.userview.getUserName().toLowerCase()+"' order by a00z2 desc,a00z3 desc ";
				rset1=dao.search(_sql);
				if(rset1.next())
				{
					String a00z2=df0.format(rset1.getDate("a00z2"));
					String a00z3=rset1.getString("a00z3");
				//	String sp_flag=rset1.getString("sp_flag");
					
					map.put(salaryid,"1");
					abean.set("name",cname+"("+str+")");
					abean.set("url","/gz/gz_accounting/gz_sp_orgtree.do?b_query=link&fromModel=wdxx&a00z2="+a00z2+"&a00z3="+a00z3+"&ori=1&salaryid="+salaryid);
					abean.set("target","i_body");
					list.add(abean);
				}
			}
			
			
			sql="select gz_extend_log.*,salarytemplate.cname,salarytemplate.cstate from gz_extend_log,salarytemplate where gz_extend_log.salaryid=salarytemplate.salaryid and lower(gz_extend_log.userName)='"+this.userview.getUserName().toLowerCase()+"' and gz_extend_log.sp_flag='05'";
			rset=dao.search(sql);
			RowSet rowset=null;
			SimpleDateFormat df=new SimpleDateFormat("yyyy-MM");
			while(rset.next())
			{
				String salaryid=rset.getString("salaryid");
				String cname=rset.getString("cname");
				String cstate=rset.getString("cstate")!=null?rset.getString("cstate").trim():"";
				String a00z2=df.format(rset.getDate("a00z2"));
				String a00z3=rset.getString("a00z3");
				if(!this.userview.isSuper_admin())
				{
					if(cstate.trim().length()==0)
					{
						if(!this.userview.isHaveResource(IResourceConstant.GZ_SET, rset.getString("salaryid")))
							continue;
					}
					else
					{
						if(!this.userview.isHaveResource(IResourceConstant.INS_SET, rset.getString("salaryid")))
							continue;
					}
				}
				if(map.get(salaryid)!=null)
					continue;
				
				LazyDynaBean abean=new LazyDynaBean();
				String str="保险";
				String str2="&gz_module=1";
				if(cstate.trim().length()==0)
				{
					str="薪资";
					str2="&gz_module=0";
				}
				rowset=dao.search("select count(*) from "+this.userview.getUserName()+"_salary_"+salaryid+" where sp_flag='07'");
				if(rowset.next())
				{
					if(rowset.getInt(1)>0)
					{
						if(map.get(salaryid)==null)
						{
							abean.set("name",cname+"("+str+")");
							abean.set("url","/gz/gz_accounting/gz_org_tree.do?b_query=link&ff_bosdate="+a00z2+str2+"&ff_count="+a00z3+"&salaryid="+salaryid);
							abean.set("target","i_body");
							list.add(abean);
							
							map.put(salaryid,"1");
						} 
					}
				}
				
				
			}
			if(rset1!=null)
				rset1.close();
			if(rset!=null)
				rset.close();
			if(rowset!=null)
				rowset.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 我的任务显示待办任务 废弃原来去历史表取的规则  zhaoxg add 2014-7-25
	 * @return
	 */
	public ArrayList getGzPending(){
		return this.getGzPending("");
	}

	/**
	 *
	 * 获取薪资待办
	 * @param from 来源标识 首页待办列表为list。其余默认为空
	 * @author ZhangHua
	 * @date 2019/11/28
	*/
	public ArrayList getGzPending(String from) {
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			String receiver = this.userview.getUserName();
			
			StringBuffer sql = new StringBuffer();
			sql.append("select Pending_title,Pending_url,Pending_status,bread,"+Sql_switcher.dateToChar("create_time",Sql_switcher.searchDbServer()==Constant.ORACEL?"yyyy-MM-dd HH24:mi":"yyyy-MM-dd HH:mm")+" create_time");
			sql.append(" from t_hr_pendingtask");
			sql.append(" where (Pending_type='34' or Pending_type='39')");
			sql.append(" and (pending_status='0')");			
			sql.append(" and Receiver='" + receiver + "'");
			LazyDynaBean abean=null;
			rs = dao.search(sql.toString());
			while(rs.next()){
				abean=new LazyDynaBean();
				String Pending_status = "";
				String Pending_url=rs.getString("Pending_url").toLowerCase();
				if("0".equalsIgnoreCase(rs.getString("Pending_status"))&&"0".equals(rs.getString("bread")))
					if(Pending_url.toLowerCase().indexOf("salaryaccounting")>=0)//驳回到发起人的叫退回 zhanghua 2018-1-12
						Pending_status="退回";
					else
						Pending_status = "待办";
				else if("1".equals(rs.getString("bread")))
					Pending_status = "已阅";
				abean.set("name",rs.getString("Pending_title")+"("+Pending_status+")");

				String url=rs.getString("Pending_url");

				if(StringUtils.isNotBlank(from)){
					url= SafeCode.decode(url);
					String[] urllist=url.substring(url.indexOf("param=")+6).split("&");
					url=url.substring(0,url.indexOf("param=")+6);
					StringBuilder stringBuilder=new StringBuilder();
					for (String str : urllist) {
						if(str.startsWith("returnflag")){
							str+="_"+from;
						}
						stringBuilder.append(str).append("&");
					}
					stringBuilder.deleteCharAt(stringBuilder.length()-1);
					url+=SafeCode.encode(stringBuilder.toString());
				}
				abean.set("url",url);
				abean.set("target","i_body");
				String parse_date=rs.getString("create_time");//日期格式处理
                abean.set("date", parse_date);
                list.add(abean);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			
		}
		return list;

	}
	/**
	 * 根据项目号查找对应Field类
	 *
	 * @param item_id
	 * @return
	 */
	public Field searchItemById(String item_id) {
		Field field = null;
		try {
			if (item_id == null || item_id.length() < 1) {
				return field;
			}
			StringBuffer format = new StringBuffer();
			format.append("############");
			ArrayList paramList = new ArrayList();
			paramList.add(item_id);
			FieldItem fieldItem= DataDictionary.getFieldItem(item_id);

			if (fieldItem!=null) {
				String itemid = fieldItem.getItemid();
				field = new Field(itemid, fieldItem.getItemdesc());
				String type = fieldItem.getItemtype();
				String codesetid = fieldItem.getCodesetid();
				field.setCodesetid(codesetid);
				/** 字段为代码型,长度定为50 */
				if ("A".equals(type)) {
					field.setDatatype(DataType.STRING);

					if (codesetid == null || "0".equals(codesetid) || "".equals(codesetid))
						field.setLength(fieldItem.getItemlength());
					else
						field.setLength(50);
					field.setAlign("left");
				} else if ("M".equals(type)) {
					field.setDatatype(DataType.CLOB);
					field.setAlign("left");
				} else if ("N".equals(type)) {

					field.setLength(fieldItem.getItemlength());
					field.setDecimalDigits(fieldItem.getDecimalwidth());

					if (fieldItem.getDecimalwidth() > 0) {
						field.setDatatype(DataType.FLOAT);
						format.setLength(fieldItem.getDecimalwidth());
						field.setFormat("####." + format.toString());
					} else {
						field.setDatatype(DataType.INT);
						field.setFormat("####");
					}
					field.setAlign("right");
				} else if ("D".equals(type)) {
					field.setLength(20);
					field.setDatatype(DataType.DATE);
					field.setFormat("yyyy.MM.dd");
					field.setAlign("right");
				} else {
					field.setDatatype(DataType.STRING);
					field.setLength(fieldItem.getItemlength());
					field.setAlign("left");
				}
				/** 对人员库标识，采用“@@”作为相关代码类 */
				if ("nbase".equalsIgnoreCase(itemid))
					field.setCodesetid("@@");
				field.setVisible(fieldItem.isVisible());
				field.setSortable(true);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return field;
	}
	
	/**
	 * @param list
	 *            需要导出的薪资类别列表
	 * @return 导出的薪资类别包的文件目录及名称
	 */
	public String exportPkg(ArrayList list)
	{
		
		String filename="ZipOutOfSalaryTemplate_"+PubFunc.getStrg()+".zip";
		FileOutputStream fileOut=null;
		ZipOutputStream outputStream=null;
		FileInputStream fileIn = null;
		BufferedInputStream origin = null;
		try {
			produceFolder();   // 产生newdata文件夹
			
			StringBuffer whl=new StringBuffer("");
			for(int i=0;i<list.size();i++)
				whl.append(","+(String)list.get(i));
			
			// 导出薪资类别 salarytemplate
			 writeFileOut(whl, "salarytemplate");
			 
			 // 导出薪资项目 salaryset
			 writeFileOut(whl, "salaryset");
			 
			 // 导出薪资公式 salaryformula
			 writeFileOut(whl, "salaryformula");
			 
			 // 导出临时变量表 midvariable
			 writeFileOut(whl, "midvariable");
			 
			 // 导出税率表 gz_tax_rate
			 writeFileOut(whl, "gz_tax_rate");
			 
			 // 导出税率明细表 gz_taxrate_item
			 writeFileOut(whl, "gz_taxrate_item");
			 
			 // 导出薪资报表 reportdetail  zhaoxg add 2014-11-10
			 writeFileOut(whl, "reportdetail");
			 
			 // 导出工资报表项目表 reportitem  zhaoxg add 2014-11-17
			 writeFileOut(whl, "reportitem");
			 
			 // 导出审核公式 hrpchkformula  zhaoxg add 2015-3-11
			 writeFileOut(whl, "hrpchkformula");
			 // 导出货币类别 moneystyle

			 // 导出薪资标准表 gz_stand
			 writeFileOut(whl, "gz_stand");
			 
			 // 导出薪资标准明细表 gz_item
			 writeFileOut(whl, "gz_item");
			 
			 // 压缩文件
			 ArrayList fileNames = new ArrayList(); // 存放文件名,并非含有路径的名字
			 ArrayList files = new ArrayList(); // 存放文件对象

			fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+filename);
			outputStream = new ZipOutputStream(fileOut);
			File rootFile = new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+"newdata");
			listFile(rootFile, fileNames, files);

			byte data[] = new byte[2048];

			for (int loop = 0; loop < files.size(); loop++) {
				String a_fileName=(String) fileNames.get(loop);
				if(!"salarytemplate.xml".equalsIgnoreCase(a_fileName)&&!"salaryset.xml".equalsIgnoreCase(a_fileName)
						&&!"salaryformula.xml".equalsIgnoreCase(a_fileName)&&!"midvariable.xml".equalsIgnoreCase(a_fileName)
						&&!"gz_tax_rate.xml".equalsIgnoreCase(a_fileName)&&!"gz_taxrate_item.xml".equalsIgnoreCase(a_fileName)
						&&!"moneystyle.xml".equalsIgnoreCase(a_fileName)&&!"moneyitem.xml".equalsIgnoreCase(a_fileName)
						&&!"gz_stand.xml".equalsIgnoreCase(a_fileName)&&!"gz_item.xml".equalsIgnoreCase(a_fileName)&&!"reportdetail.xml".equalsIgnoreCase(a_fileName)
						&&!"reportitem.xml".equalsIgnoreCase(a_fileName)&&!"hrpchkformula.xml".equalsIgnoreCase(a_fileName))
					continue;

				fileIn = new FileInputStream((File) files
						.get(loop));
				origin = new BufferedInputStream(fileIn, 2048);
				outputStream.putNextEntry(new ZipEntry((String) fileNames
						.get(loop)));
				int count;
				while ((count = origin.read(data, 0, 2048)) != -1) {
					outputStream.write(data, 0, count);
				}
				outputStream.close();
				origin.close();
				fileIn.close();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(outputStream);
			PubFunc.closeResource(origin);
			PubFunc.closeResource(fileIn);
			PubFunc.closeIoResource(fileOut);
		}
		return filename;
	}
	
	/**
	 * 写入数据
	 * @param whl
	 * @param tableName
	 */
	private void writeFileOut(StringBuffer whl,String tableName){
		FileOutputStream fileOut = null;
		RowSetToXmlBuilder builder=new RowSetToXmlBuilder(this.conn);
		try {
			fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+"newdata"+System.getProperty("file.separator")+tableName+".xml");
			fileOut.write(getFileContext(whl.substring(1),tableName,builder));
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(fileOut);
		}

	}
	
	/**
	 * 导入薪资包
	 * 
	 * @param form_file
	 *            导入文件
	 * @param salaryids
	 *            指定导入的薪资类别id
	 * @return
	 */
	public boolean importPkg(FormFile form_file,String[] salaryidS,HashMap map) throws GeneralException
	{
		boolean bflag=true;
		InputStream inputStream = null;
		try
		{
			StringBuffer salaryids=new StringBuffer("#");
			for(int i=0;i<salaryidS.length;i++)//要导入的工资类别
				salaryids.append(salaryidS[i]+"#");
			inputStream = form_file.getInputStream();
			/**map 为要覆盖导入的工资类别，现id与原id*/
			HashMap fileMap=extZipFileList(inputStream) ;
			String  fileContext="";
			
			HashMap moneyStyle_keyMap=new HashMap();
			HashMap gz_stand_keyMap=new HashMap();
			HashMap salarytemplate_keyMap=new HashMap();
			HashMap gz_tax_rate_keyMap=new HashMap();
			HashMap midvariable_keyMap=new HashMap();
             // 导入货币类别 moneystyle
			fileContext=(String)fileMap.get("moneystyle.xml");
			if(fileContext!=null&&fileContext.length()>=0)
			{				
				DownLoadXml.importTableData(0,this.conn,moneyStyle_keyMap,fileContext,"moneystyle","nstyleid","",null,null,map);
			}
			 // 导入货币明细表 moneyitem
			fileContext=(String)fileMap.get("moneyitem.xml");
			if(fileContext!=null&&fileContext.length()>=0)
			{
				DownLoadXml.importTableData(3,this.conn,new HashMap(),fileContext,"moneyitem","nstyleid","nstyleid",moneyStyle_keyMap,null,map);
			}
			 // 导入薪资标准表 gz_stand
			fileContext=(String)fileMap.get("gz_stand.xml");
			if(fileContext!=null&&fileContext.length()>=0)
			{				
				DownLoadXml.importTableData(0,this.conn,gz_stand_keyMap,fileContext,"gz_stand","id","",null,null,map);
			}
			// 导入薪资标准明细表 gz_item
			fileContext=(String)fileMap.get("gz_item.xml");
			if(fileContext!=null&&fileContext.length()>=0)
			{
				DownLoadXml.importTableData(3,this.conn,new HashMap(),fileContext,"gz_item","id","id",gz_stand_keyMap,null,map);
			}
			
			// 导入薪资类别 salarytemplate
			fileContext=(String)fileMap.get("salarytemplate.xml");
			if(fileContext!=null&&fileContext.length()>=0)
			{				
				DownLoadXml.importTableData(0,this.conn,salarytemplate_keyMap,fileContext,"salarytemplate","salaryid","salaryid",null,salaryids.toString(),map);
			}
			
			// 导入薪资报表  reportdetail  zhaoxg add 2014-11-10
			fileContext=(String)fileMap.get("reportdetail.xml");
			if(fileContext!=null&&fileContext.length()>=0)
			{
				DownLoadXml.importTableData(0,this.conn,new HashMap(),fileContext,"reportdetail","stid","stid",salarytemplate_keyMap,null,map);
			}
			
			// 导入工资报表项目表 reportitem  zhaoxg add 2014-11-17
			fileContext=(String)fileMap.get("reportitem.xml");
			if(fileContext!=null&&fileContext.length()>=0)
			{
				DownLoadXml.importTableData(0,this.conn,new HashMap(),fileContext,"reportitem","rsdtlid","rsdtlid",salarytemplate_keyMap,null,map);
			}
			
			// 导入薪资项目 salaryset
			fileContext=(String)fileMap.get("salaryset.xml");
			if(fileContext!=null&&fileContext.length()>=0)
			{
				DownLoadXml.importTableData(3,this.conn,new HashMap(),fileContext,"salaryset","salaryid","salaryid",salarytemplate_keyMap,salaryids.toString(),map);
			}
// 导入税率表 gz_tax_rate
			fileContext=(String)fileMap.get("gz_tax_rate.xml");
			if(fileContext!=null&&fileContext.length()>=0)
			{				
				DownLoadXml.importTableData(0,this.conn,gz_tax_rate_keyMap,fileContext,"gz_tax_rate","taxid","",null,null,map);
			}
		  // 导入税率明细表 gz_taxrate_item
			fileContext=(String)fileMap.get("gz_taxrate_item.xml");
			if(fileContext!=null&&fileContext.length()>=0)
			{
				DownLoadXml.importTableData(1,this.conn,new HashMap(),fileContext,"gz_taxrate_item","taxitem","taxid",gz_tax_rate_keyMap,null,map);
			}
// 导入临时变量表 midvariable
			fileContext=(String)fileMap.get("midvariable.xml");
			if(fileContext!=null&&fileContext.length()>=0)
			{
				DownLoadXml.importTableData(1,this.conn,midvariable_keyMap,fileContext,"midvariable","nid","cstate",salarytemplate_keyMap,salaryids.toString(),map);
			}
			// 导入薪资公式 salaryformula
			fileContext=(String)fileMap.get("salaryformula.xml");
			if(fileContext!=null&&fileContext.length()>=0)
			{
				DownLoadXml.importSpecialTableData(this.conn,gz_stand_keyMap,"standid",fileContext,"salaryformula","itemid","standid",gz_tax_rate_keyMap,"salaryid",salarytemplate_keyMap,salaryids.toString(),map);
			}
			// 导入薪资公式 hrpchkformula
			fileContext=(String)fileMap.get("hrpchkformula.xml");
			if(fileContext!=null&&fileContext.length()>=0)
			{
				DownLoadXml.importSpFormulaData(this.conn, fileContext, "hrpchkformula", "chkid", salaryids.toString(),"tabid",salarytemplate_keyMap);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			bflag=false;
			throw GeneralExceptionHandler.Handle(e);
		}
		finally{
			PubFunc.closeIoResource(inputStream);
		}
		return bflag;
	}
	
	
	
	/**
	 * 获得导入压缩文件中某文件的纪录（指定字段）信息
	 * 
	 * @param fileName
	 *            文件名称
	 * @param form_file
	 *            导入文件
	 * @param primaryKey
	 * @param cname
	 * @return
	 */
	public ArrayList getSalaryTemplateList(FormFile form_file,String fileName,String primaryKey,String cname) throws GeneralException
	{
		ArrayList list=new ArrayList();
		try
		{
			HashMap fileMap=extZipFileList(form_file.getInputStream()) ;
			String  fileContext=(String)fileMap.get(fileName);
			if(fileContext==null||fileContext.length()==0)
				return list;
			
			Document standard_doc =PubFunc.generateDom(fileContext);
			Element root=standard_doc.getRootElement();
			List nodeList=root.getChildren();
			LazyDynaBean a_bean=null;
			HashMap templatemap = this.getAllSalarytemplate();
			for(Iterator t=nodeList.iterator();t.hasNext();)
			{
				Element record=(Element)t.next();
				String id=record.getAttributeValue(primaryKey);
				XPath xPath0 = XPath.newInstance("./"+cname);
				Element nameNode = (Element) xPath0.selectSingleNode(record);
				String name=nameNode.getValue();
				
				a_bean=new LazyDynaBean();
				a_bean.set("name",name);
				a_bean.set("id",id);
				if(templatemap.get(id)!=null)
				{
					a_bean.set("isrepeat","1");
					a_bean.set("oldid",id);
				}
				else
				{
					a_bean.set("isrepeat","0");	
					a_bean.set("oldid","");
				}
				list.add(a_bean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}
	
	
	
	/**
	 * 读取压缩包里的文件
	 * 
	 * @param inputStream
	 * @return
	 */
	static   HashMap   extZipFileList(InputStream inputStream)  
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
							 * File file = new File(extPlace + entryName);
							 * file.mkdirs(); System.out.println("创建文件夹:" +
							 * entryName);
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
							 s.append(line);
						 }
						 in.closeEntry();   
						 fileMap.put(entryName.toLowerCase(),s.toString());
						 // System.out.println(s.toString());
					}   
			  }  
			  in.close();
		    
		  }   
		  catch   (IOException   e)   {   
			  e.printStackTrace();
		  }   
		  return fileMap;
	}   
	
	
	
	
// 产生newdata文件夹
	public static void produceFolder()
	{
		if(!(new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+"newdata/").isDirectory()))
		{
			new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+"newdata/").mkdir();
				
		}
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
	
	/**
	 * 
	 * @param salaryids_whl
	 * @param tableName
	 *            导出薪资类别 salarytemplate;薪资项目 salaryset;薪资公式 salaryformula;临时变量表
	 *            midvariable; 税率表 gz_tax_rate;税率明细表 gz_taxrate_item;货币类别
	 *            moneystyle;货币明细表 moneyitem; 薪资标准表 gz_stand;薪资标准明细表 gz_item
	 * @return
	 */
	public byte[] getFileContext(String salaryids_whl,String tableName,RowSetToXmlBuilder builder)
	{
		String  context="";
		try
		{
			StringBuffer salaryIdsStr=new StringBuffer("");
			String[] temps=salaryids_whl.split(",");
			for(int i=0;i<temps.length;i++)
			{
				salaryIdsStr.append(",'"+temps[i]+"'");
			}
			
			RowSet rowSet=null;
			ContentDAO dao=new ContentDAO(this.conn);
			String sql="";
			if("salarytemplate".equals(tableName))
			{
				sql="select * from salarytemplate where salaryid in ("+salaryids_whl+")";
			}
			else if("salaryset".equals(tableName))
			{
				sql="select * from salaryset where salaryid in ("+salaryids_whl+") ";
			}
			else if("salaryformula".equals(tableName))
			{
				sql="select * from salaryformula where salaryid in ("+salaryids_whl+") ";
			}
			else if("midvariable".equals(tableName))
			{
				sql="select * from midvariable where   nflag=0 and TempletID=0 and (cstate is null or cstate in ("+salaryIdsStr.substring(1)+")) ";
			}
			else if("gz_tax_rate".equals(tableName))
			{
				sql="select * from gz_tax_rate where taxid in (select standid from salaryformula where salaryid in ("+salaryids_whl+")  and Runflag=2) ";
			}
			else if("gz_taxrate_item".equals(tableName))  // 税率明细表
			{
				sql="select * from gz_taxrate_item where taxid in (select standid from salaryformula where salaryid in ("+salaryids_whl+")  and Runflag=2) ";
			}
			else if("moneystyle".equals(tableName))  // 货币类别
			{
				sql="select * from moneystyle where Nstyleid in ( select nmoneyid from salarytemplate where salaryid in ("+salaryids_whl+"))";
			}
			else if("moneyitem".equals(tableName))  // 货币明细表
			{
				sql="select * from moneyitem where Nstyleid in ( select nmoneyid from salarytemplate where salaryid in ("+salaryids_whl+"))";
			}
			else if("gz_stand".equals(tableName))  // 薪资标准表
			{
				sql="select * from gz_stand where id  in (select standid from salaryformula where salaryid in ("+salaryids_whl+")  and Runflag=1) ";
			}
			else if("gz_item".equals(tableName))  // 薪资标准明细表
			{
				sql="select * from gz_item where id  in (select standid from salaryformula where salaryid in ("+salaryids_whl+")  and Runflag=1) ";
			}
			else if("reportdetail".equals(tableName))  // 薪资报表  zhaoxg add 2014-11-10
			{
				sql="select * from reportdetail where stid in ("+salaryids_whl+") ";
			}
			else if("reportitem".equals(tableName))  // 工资报表项目表  zhaoxg add 2014-11-17
			{
				sql="select * from reportitem where rsdtlid in (select rsdtlid from reportdetail where stid in ("+salaryids_whl+") ) ";
			}else if("hrpchkformula".equals(tableName)){
				sql="select * from hrpchkformula where flag=1 and tabid in ("+salaryids_whl+") ";
			}
			rowSet=dao.search(sql);
			context=builder.outPutXml(rowSet,tableName);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return context.getBytes();
	}
	
	
	public HashMap getAllSalarytemplate()
	{
		HashMap map = new HashMap();
		try
		{
			String sql = "select salaryid,cname from salarytemplate";
			ContentDAO dao = new ContentDAO(this.conn);
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
	 * 插入发放记录 包括起草01，执行中05(审批中)，结束06三种状态
	 * 
	 * @param ymd
	 * @count count
	 */
	private void appendExtendLog(String ymd,String count,String salaryid)
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			int maxid=DbNameBo.getPrimaryKey("gz_extend_log", "id", conn);
			RecordVo vo=new RecordVo("gz_extend_log");
			vo.setInt("id", maxid);
			vo.setString("username", this.userview.getUserName());
			vo.setString("sp_flag", "01");
			vo.setInt("salaryid", Integer.parseInt(salaryid));
			vo.setInt("a00z3",Integer.parseInt(count));
			vo.setDate("a00z2",ymd);
			dao.addValueObject(vo);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	/**
	 * 取得表结构信息
	 * 
	 * @param tableName
	 * @return
	 */
	public LazyDynaBean getTableInfo(String tableName)
	{
		LazyDynaBean abean=new LazyDynaBean();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select * from "+tableName+" where 1=2");
			ResultSetMetaData mt=rowSet.getMetaData();
			for(int i=0;i<mt.getColumnCount();i++)
			{
				abean.set(mt.getColumnName(i+1).toLowerCase(),"1");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return abean;
	}
	
	
	
	
	/**
	 * 根据当前用户，查找处理的业务日期和次数 
	 * 1。发放纪录表中该类别有未结束状态的纪录时，根据该条记录的业务日期确定
	 * 2。发放纪录表中该类别全为结束状态的纪录时，根据最大日期记录的业务日期确定
	 * 1.当临时表中有数据时根据数据的发放日期确定
	 * @param lazyvo
	 * @author dengcan
	 */
	public LazyDynaBean searchCurrentDate3(String salaryid,String username) 
	{
		LazyDynaBean abean=new LazyDynaBean();
		String strYm="";
		String strC="";
		boolean isNull=false;
		RowSet rowSet=null;		
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
	/*
			rowSet=dao.search("select A00z2,A00z3 from gz_extend_log where sp_flag<>'06' and  salaryid="+salaryid+" and  upper(username)='"+username.toUpperCase()+"'");
			if(rowSet.next())
			{
				strYm=PubFunc.FormatDate(rowSet.getDate("A00z2"), "yyyy-MM-dd");
				strC=rowSet.getString("A00z3");
				isNull=true;
			}
			else
			{
				rowSet=dao.search("select max(A00z2) A00z2 from gz_extend_log where  salaryid="+salaryid+" and  upper(username)='"+username.toUpperCase()+"'");
				if(rowSet.next())
				{
					if(rowSet.getDate("A00z2")!=null)
					{
						strYm=PubFunc.FormatDate(rowSet.getDate("A00z2"), "yyyy-MM-dd");
						isNull=true;
					}
					else
						strYm="";
					
				}
				if(strYm.equalsIgnoreCase(""))
				{
					strYm=DateUtils.format(new Date(), "yyyy-MM-dd");
					String[] tmp=StringUtils.split(strYm, "-");
					strYm="";// tmp[0]+"-"+tmp[1]+"-01";
					strC="";// "1";
				}
				else
				{
					StringBuffer buf=new StringBuffer("select max(A00Z3) A00Z3 from gz_extend_log");
					buf.append(" where salaryid=");
					buf.append(salaryid);
					buf.append(" and ");
					buf.append(" upper(username)='");
					buf.append(username.toUpperCase());
					buf.append("'");
					buf.append(" and A00Z2=");
					buf.append(Sql_switcher.dateValue(strYm));
				    rowSet=dao.search(buf.toString());
					if(rowSet.next())
						strC=rowSet.getString("A00Z3");
				}
			}
			*/
			if(!isNull)
			{
				DbWizard dbWizard=new DbWizard(this.conn); 
				if(dbWizard.isExistTable(username.toLowerCase()+"_salary_"+salaryid, false))
				{
					
					LazyDynaBean tableBean=getTableInfo(username.toLowerCase()+"_salary_"+salaryid);
					if(tableBean.get("a00z2")!=null&&tableBean.get("a00z3")!=null)
						rowSet=dao.search("select distinct A00z2,A00z3 from "+username+"_salary_"+salaryid);
					if(tableBean.get("a00z2")!=null&&tableBean.get("a00z3")!=null&&rowSet.next())
					{
						strYm=rowSet.getDate("A00Z2")!=null?PubFunc.FormatDate(rowSet.getDate("A00Z2"), "yyyy-MM-dd"):"";
						strC=rowSet.getString("A00Z3")!=null?rowSet.getString("A00Z3"):"";
					}
					else
					{
						if(tableBean.get("a00z2")==null||tableBean.get("a00z3")==null)
						{
							
							DbWizard dbw=new DbWizard(this.conn);
							Table table=new Table(username.toLowerCase()+"_salary_"+salaryid);
							if(tableBean.get("a00z2")==null)
							{
								Field field=new Field("A00Z2",ResourceFactory.getProperty("gz.columns.a00z2"));
								field.setDatatype(DataType.DATE);
								table.addField(field);
							}
							if(tableBean.get("a00z3")==null)
							{
								Field field=new Field("A00Z3",ResourceFactory.getProperty("gz.columns.a00z3"));
								field.setDatatype(DataType.INT);
								table.addField(field);
							}
							dbw.addColumns(table);
						}
						else
						{
							rowSet=dao.search("select A00z2,A00z3 from gz_extend_log where sp_flag<>'06' and  salaryid="+salaryid+" and  upper(username)='"+username.toUpperCase()+"'");
							if(rowSet.next())
							{
								strYm=PubFunc.FormatDate(rowSet.getDate("A00z2"), "yyyy-MM-dd");
								strC=rowSet.getString("A00z3");
								isNull=true;
							}
							else
							{
								rowSet=dao.search("select max(A00z2) A00z2 from gz_extend_log where  salaryid="+salaryid+" and  upper(username)='"+username.toUpperCase()+"'");
								if(rowSet.next())
								{
									if(rowSet.getDate("A00z2")!=null)
									{
										strYm=PubFunc.FormatDate(rowSet.getDate("A00z2"), "yyyy-MM-dd");
										isNull=true;
									}
									else
										strYm="";
									
								}
								if("".equalsIgnoreCase(strYm))
								{
									strYm=DateUtils.format(new Date(), "yyyy-MM-dd");
									String[] tmp=StringUtils.split(strYm, "-");
									strYm="";// tmp[0]+"-"+tmp[1]+"-01";
									strC="";// "1";
								}
								else
								{
									StringBuffer buf=new StringBuffer("select max(A00Z3) A00Z3 from gz_extend_log");
									buf.append(" where salaryid=");
									buf.append(salaryid);
									buf.append(" and ");
									buf.append(" upper(username)='");
									buf.append(username.toUpperCase());
									buf.append("'");
									buf.append(" and A00Z2=");
									buf.append(Sql_switcher.dateValue(strYm));
								    rowSet=dao.search(buf.toString());
									if(rowSet.next())
										strC=rowSet.getString("A00Z3");
								}
							}
						}
					}
				}
			}
			abean.set("strYm", strYm);
			abean.set("strC", strC);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(rowSet!=null)
					rowSet.close();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		return abean;
	}
	
	
	
	
	
	
	
	
	/**
	 * 根据当前用户，查找处理的业务日期和次数 1.当临时表中有数据时根据数据的发放日期确定
	 * 2。当临时表中无数据，发放纪录表中该类别有未结束状态的纪录时，根据该条记录的业务日期确定
	 * 3。当临时表中无数据，发放纪录表中该类别全为结束状态的纪录时，根据最大日期记录的业务日期确定
	 * 
	 * @param lazyvo
	 * @author dengcan
	 */
	public LazyDynaBean searchCurrentDate2(String salaryid,String username) 
	{
		// String salaryid=(String)(String)lazyvo.get("salaryid");
	// String username=this.userview.getUserName();
		LazyDynaBean abean=new LazyDynaBean();
		String strYm="";
		String strC="";
		boolean isNull=false;
		RowSet rowSet=null;		
		try
		{
			DbWizard dbWizard=new DbWizard(this.conn); 
			if(dbWizard.isExistTable(username.toLowerCase()+"_salary_"+salaryid, false))
			{
				ContentDAO dao=new ContentDAO(this.conn);
				LazyDynaBean tableBean=getTableInfo(username.toLowerCase()+"_salary_"+salaryid);
			// RecordVo vo=new
			// RecordVo(username.toLowerCase()+"_salary_"+salaryid);
				

				if(tableBean.get("a00z2")!=null&&tableBean.get("a00z3")!=null)
					rowSet=dao.search("select distinct A00z2,A00z3 from "+username+"_salary_"+salaryid);
				if(tableBean.get("a00z2")!=null&&tableBean.get("a00z3")!=null&&rowSet.next())
				{
					strYm=rowSet.getDate("A00Z2")!=null?PubFunc.FormatDate(rowSet.getDate("A00Z2"), "yyyy-MM-dd"):"";
					strC=rowSet.getString("A00Z3")!=null?rowSet.getString("A00Z3"):"";
				}
				else
				{
					if(tableBean.get("a00z2")==null||tableBean.get("a00z3")==null)
					{
						
						DbWizard dbw=new DbWizard(this.conn);
						Table table=new Table(username.toLowerCase()+"_salary_"+salaryid);
						if(tableBean.get("a00z2")==null)
						{
							Field field=new Field("A00Z2",ResourceFactory.getProperty("gz.columns.a00z2"));
							field.setDatatype(DataType.DATE);
							table.addField(field);
						}
						if(tableBean.get("a00z3")==null)
						{
							Field field=new Field("A00Z3",ResourceFactory.getProperty("gz.columns.a00z3"));
							field.setDatatype(DataType.INT);
							table.addField(field);
						}
						dbw.addColumns(table);
					}
					
					
					rowSet=dao.search("select A00z2,A00z3 from gz_extend_log where sp_flag<>'06' and  salaryid="+salaryid+" and  upper(username)='"+username.toUpperCase()+"'");
					if(rowSet.next())
					{
						strYm=PubFunc.FormatDate(rowSet.getDate("A00z2"), "yyyy-MM-dd");
						strC=rowSet.getString("A00z3");
					}
					else
					{
						rowSet=dao.search("select max(A00z2) A00z2 from gz_extend_log where  salaryid="+salaryid+" and  upper(username)='"+username.toUpperCase()+"'");
						if(rowSet.next())
						{
							if(rowSet.getDate("A00z2")!=null)
								strYm=PubFunc.FormatDate(rowSet.getDate("A00z2"), "yyyy-MM-dd");
							else
								strYm="";
						}
						if("".equalsIgnoreCase(strYm))
						{
							strYm=DateUtils.format(new Date(), "yyyy-MM-dd");
							String[] tmp=StringUtils.split(strYm, "-");
							strYm="";// tmp[0]+"-"+tmp[1]+"-01";
							strC="";// "1";
							// appendExtendLog(strYm,strC,salaryid);
							// lazyvo.set("appdate", "");
							// lazyvo.set("count", "");
							isNull=true;
						}
						else
						{
							StringBuffer buf=new StringBuffer("select max(A00Z3) A00Z3 from gz_extend_log");
							buf.append(" where salaryid=");
							buf.append(salaryid);
							buf.append(" and ");
							buf.append(" upper(username)='");
							buf.append(username.toUpperCase());
							buf.append("'");
							buf.append(" and A00Z2=");
							buf.append(Sql_switcher.dateValue(strYm));
						    rowSet=dao.search(buf.toString());
							if(rowSet.next())
								strC=rowSet.getString("A00Z3");
						}
						
						
					}
				}
				/*
				 * if(!isNull){ lazyvo.set("appdate", strYm.substring(0, 7));
				 * lazyvo.set("count", strC); }
				 */
			}
			abean.set("strYm", strYm);
			abean.set("strC", strC);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(rowSet!=null)
					rowSet.close();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		return abean;
	}
	
	
	
	/**
	 * 根据当前用户，查找处理的业务日期和次数
	 * 
	 * @param lazyvo
	 */
	private void searchCurrentDate(LazyDynaBean lazyvo) 
	{

			String salaryid=(String)(String)lazyvo.get("salaryid");

			String strYm=null;
			String strC=null;
			StringBuffer buf=new StringBuffer();
			buf.append("select max(A00Z2) A00Z2 from gz_extend_log");
			buf.append(" where salaryid=");
			buf.append(salaryid);
			buf.append(" and ");
			buf.append(" upper(username)='");
			buf.append(this.userview.getUserName().toUpperCase());
			buf.append("'");
			/** 结束的 */
			// buf.append(" and sp_flag='01'");
			try
			{
				ContentDAO dao=new ContentDAO(this.conn);
				RowSet rset=dao.search(buf.toString());
				if(rset.next())
					strYm=PubFunc.FormatDate(rset.getDate("A00Z2"), "yyyy-MM-dd");
				if("".equalsIgnoreCase(strYm))
				{
					strYm=DateUtils.format(new Date(), "yyyy-MM-dd");
					String[] tmp=StringUtils.split(strYm, "-");
					strYm="";// tmp[0]+"-"+tmp[1]+"-01";
					strC="";// "1";
					// appendExtendLog(strYm,strC,salaryid);
					lazyvo.set("appdate", "");
					lazyvo.set("count", "");				
				}
				else
				{
					buf.setLength(0);
					buf.append("select max(A00Z3) A00Z3 from gz_extend_log");
					buf.append(" where salaryid=");
					buf.append(salaryid);
					buf.append(" and ");
					buf.append(" upper(username)='");
					buf.append(this.userview.getUserName().toUpperCase());
					buf.append("'");
					buf.append(" and A00Z2=");
					buf.append(Sql_switcher.dateValue(strYm));
				
					// buf.append(" and sp_flag='01'");
					rset=dao.search(buf.toString());
					if(rset.next())
						strC=rset.getString("A00Z3");
					lazyvo.set("appdate", strYm.substring(0, 7));
					lazyvo.set("count", strC);				
				}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		/*
		 * 老算法 DbWizard db=new DbWizard(this.conn); String
		 * gz_table=this.userview.getUserName()+"_salary_"+(String)lazyvo.get("salaryid");
		 * if(db.isExistTable(gz_table, false)) { try { StringBuffer buf=new
		 * StringBuffer(); buf.append("select max(A00Z2) A00Z2,max(A00Z3) A00Z3
		 * from "); buf.append(gz_table); ContentDAO dao=new
		 * ContentDAO(this.conn); RowSet rset=dao.search(buf.toString());
		 * if(rset.next()) { String
		 * busidate=PubFunc.FormatDate(rset.getDate("A00Z2"), "yyyy.MM"); String
		 * count=rset.getString("A00Z3"); lazyvo.set("appdate",
		 * busidate==""?PubFunc.FormatDate(new Date(), "yyyy.MM"):busidate);
		 * lazyvo.set("count", count==null?"1":count); }
		 *  } catch(Exception ex) { ex.printStackTrace(); } } else { String
		 * busidate=PubFunc.FormatDate(new Date(), "yyyy.MM");
		 * lazyvo.set("appdate", busidate); lazyvo.set("count", "1"); }
		 */
	}
	/**
	 * 薪资类别是否为最近结构
	 * 
	 * @param salaryid
	 * @return
	 */
	private boolean isNewStruct(int salaryid)
	{
		boolean bflag=false;
		StringBuffer buf=new StringBuffer();
		buf.append("select salaryid from salaryset where itemid=? and salaryid=?");
		ArrayList paralist=new ArrayList();
		paralist.add("A00Z2");
		paralist.add(Integer.valueOf(salaryid));
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			RowSet rset=dao.search(buf.toString(),paralist);
			if(rset.next())
				bflag=true;
			if(rset!=null)
				rset.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			bflag=false;
		}
		return bflag;
	}
	/**
	 * 同步薪资类别，自助升级
	 * 
	 * @param salaryid
	 */
	public void syncSalaryStruct(int salaryid)
	{
		if(!isNewStruct(salaryid))
		{
			try
			{
				/** 未升过级,增加字段 */
				ContentDAO dao=new ContentDAO(this.conn);
				int fieldid=0; int sortid=0;
				RowSet rowSet=dao.search("select max(fieldid),max(sortid) from salaryset where salaryid="+salaryid);
				if(rowSet.next())
				{
					fieldid=rowSet.getInt(1);
					sortid=rowSet.getInt(2);
				}	
				
				ArrayList list=new ArrayList();
				list.add(getSalarySetRecordvo(salaryid,fieldid+1,"A00","A00Z2","发放日期",20,0,"0",sortid+1,10,"",3,1,"D"));
				list.add(getSalarySetRecordvo(salaryid,fieldid+2,"A00","A00Z3","发放次数",15,0,"0",sortid+2,10,"",3,1,"N"));
				dao.addValueObject(list);
				/** 更新名称A00Z0归属日期,A00Z1归属次数 */
				list.clear();
			
				
				RecordVo vo=new RecordVo("salaryset");
				vo.setInt("salaryid", salaryid);
				vo.setString("itemdesc", "归属日期");
				vo.setString("itemid","A00Z0");
				list.add(vo);
				vo=new RecordVo("salaryset");
				vo.setInt("salaryid", salaryid);
				vo.setString("itemdesc", "归属次数");
				vo.setString("itemid","A00Z1");
				list.add(vo);
				dao.updateValueObject(list);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}
	
	
	
	
	
	/**
	 * 取得权限范围的薪资审批列表汇总数据，列表中存放是的LazyBean
	 * @return
	 */
	public ArrayList searchGzSetCollectList()throws GeneralException
	{
		String flow_ctrl="1";
		
		ArrayList list=new ArrayList();
		StringBuffer buf=new StringBuffer();
		SalaryCtrlParamBo ctrlparam=null;
		try
		{
			buf.append("select salaryid,cname,cbase,cond,seq,ctrl_param from salarytemplate ");
			if(this.gz_type==0)
				buf.append(" where (cstate is null or cstate='')");// 薪资类别
			else
				buf.append(" where cstate='1'");// 险种类别
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rset=dao.search(buf.toString()+" order by seq,salaryid");
			RowSet rowSet=null;
			SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
			while(rset.next())
			{
				/** 加上权限过滤 */
				if(this.gz_type==0)
				{
					if(!this.userview.isHaveResource(IResourceConstant.GZ_SET, rset.getString("salaryid")))
						continue;
				}
				else
				{
					if(!this.userview.isHaveResource(IResourceConstant.INS_SET, rset.getString("salaryid")))
						continue;
				}
				LazyDynaBean lazyvo=new LazyDynaBean();
				String salaryid=rset.getString("salaryid");
				lazyvo.set("salaryid", rset.getString("salaryid"));
				lazyvo.set("seq",rset.getString("seq")!=null?rset.getString("seq"):"0");
				lazyvo.set("cname", rset.getString("cname"));
				String ctrl_param=Sql_switcher.readMemo(rset, "ctrl_param");
				ctrlparam=new SalaryCtrlParamBo(this.conn,rset.getInt("salaryid"),ctrl_param);  //new SalaryCtrlParamBo(this.conn,rset.getInt("salaryid"));
				String manager=ctrlparam.getValue(SalaryCtrlParamBo.SHARE_SET, "user"); // 工资管理员，对共享类别有效
				 
				String  verify_ctrl=ctrlparam.getValue(SalaryCtrlParamBo.VERIFY_CTRL);
				if(verify_ctrl==null||verify_ctrl.trim().length()==0) ////是否按审核条件控制
					verify_ctrl="0";
				
				String isSendMessage="0";
				if(ctrlparam.getValue(SalaryCtrlParamBo.NOTE,"mail")!=null&& "1".equals(ctrlparam.getValue(SalaryCtrlParamBo.NOTE,"mail")))
					isSendMessage="1";
				if(ctrlparam.getValue(SalaryCtrlParamBo.NOTE,"sms")!=null&& "1".equals(ctrlparam.getValue(SalaryCtrlParamBo.NOTE,"sms")))
					isSendMessage="1";
				
				String isControl=ctrlparam.getValue(SalaryCtrlParamBo.AMOUNT_CTRL,"flag");   //该工资类别是否进行总额控制
				if("1".equals(isControl))
				{
					String amount_ctrl_ff=ctrlparam.getValue(SalaryCtrlParamBo.AMOUNT_CTRL,"amount_ctrl_ff");
					if(amount_ctrl_ff!=null&&amount_ctrl_ff.trim().length()>0)
						isControl=amount_ctrl_ff;
					
				}
				 
				
				/**
				 * 审批模式 =1，需要审批 =其它值，不需要审批
				 */
				if("1".equalsIgnoreCase(flow_ctrl))
				{
					String flow_flag=ctrlparam.getValue(SalaryCtrlParamBo.FLOW_CTRL, "flag");
					if(!"1".equalsIgnoreCase(flow_flag))
						continue;
				}
				/** 自动升级薪资类别 */
				syncSalaryStruct(rset.getInt("salaryid"));
				
				String collect_je_field=ctrlparam.getValue(SalaryCtrlParamBo.COLLECT_JE_FIELD);
		        if(collect_je_field==null|| "".equals(collect_je_field))
		        	collect_je_field="";
		        else
		        	collect_je_field=",sum("+Sql_switcher.isnull(collect_je_field,"0")+") asum";
		        rowSet=dao.search("select count(a0100) acount,a00z2,a00z3"+collect_je_field+" from salaryhistory where salaryid="+salaryid+" and curr_user='"+this.userview.getUserName()+"' and ( sp_flag='02' or sp_flag='07' )  group by  a00z2,a00z3");
				int n=0;
				while(rowSet.next())
				{
					lazyvo=new LazyDynaBean();
					lazyvo.set("salaryid", salaryid);
					lazyvo.set("seq",rset.getString("seq")!=null?rset.getString("seq"):"0");
					lazyvo.set("cname", rset.getString("cname"));
					lazyvo.set("_count",String.valueOf(rowSet.getInt("acount")));
					lazyvo.set("a00z2",df.format(rowSet.getDate("a00z2")));
					lazyvo.set("a00z3", rowSet.getString("a00z3"));
					lazyvo.set("verify_ctrl",verify_ctrl);
					lazyvo.set("isSendMessage",isSendMessage);
					lazyvo.set("isControl",isControl);
					if(collect_je_field.length()>0)
						lazyvo.set("_sum",PubFunc.round(new BigDecimal(rowSet.getDouble("asum")).toString(),2));
					else
						lazyvo.set("_sum","");
					list.add(lazyvo);
					n++;
				}
				if(n==0)
				{
					lazyvo.set("a00z2","");
					lazyvo.set("a00z3","");
					lazyvo.set("_count","");
					lazyvo.set("_sum","");
					lazyvo.set("verify_ctrl", verify_ctrl);
					lazyvo.set("isSendMessage",isSendMessage);
					lazyvo.set("isControl",isControl);
					list.add(lazyvo);
				}
			}
			
			if(rowSet!=null)
				rowSet.close();
			if(rset!=null)
				rset.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		
		return list;
	}
	
	
	
	/**
	 * 取得权限范围的薪资列表 列表中存放是的LazyBean
	 * 
	 * @param flow_ctrl=1,求需要审批的薪资类别  
	 * @return
	 */
	public ArrayList searchGzSetList(String flow_ctrl)throws GeneralException
	{ 
		if(flow_ctrl==null)
			flow_ctrl="0";
		ArrayList list=new ArrayList();
		StringBuffer buf=new StringBuffer();
		SalaryCtrlParamBo ctrlparam=null;
		try
		{
		// 	buf.append("select salaryid,cname,cbase,cond,seq,ctrl_param from salarytemplate ");
		 	buf.append("select salaryid,cname,cbase,seq,cond from salarytemplate "); //20150304 dengcan  当薪资类别超过400个，查询大字段内容性能很慢
			if(this.gz_type==0)
				buf.append(" where (cstate is null or cstate='')");// 薪资类别
			else
				buf.append(" where cstate='1'");// 险种类别
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rset=dao.search(buf.toString()+" order by seq");
			DbWizard dbWizard=new DbWizard(this.conn); 
			  
			while(rset.next())
			{
				 
				
				/** 加上权限过滤 */
				if(this.gz_type==0)
				{
					if(!this.userview.isHaveResource(IResourceConstant.GZ_SET, rset.getString("salaryid")))
						continue;
				}
				else
				{
					if(!this.userview.isHaveResource(IResourceConstant.INS_SET, rset.getString("salaryid")))
						continue;
				}
				LazyDynaBean lazyvo=new LazyDynaBean();
				SalaryTemplateBo gzbo=new SalaryTemplateBo(this.conn,Integer.parseInt(rset.getString("salaryid")));
				
				
				
				String  royalty_valid=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.ROYALTIES,"valid");
				if(royalty_valid==null)
					royalty_valid="0";
				lazyvo.set("royalty_valid", royalty_valid);
				String salaryid = rset.getString("salaryid");
				lazyvo.set("salaryid", salaryid);
				lazyvo.set("seq",rset.getString("seq")!=null?rset.getString("seq"):"0");
				String cname=rset.getString("cname"); 
		//		String ctrl_param=Sql_switcher.readMemo(rset, "ctrl_param");
		
				String cond=Sql_switcher.readMemo(rset, "cond");
				
				String cbase=rset.getString("cbase");
				/** 对条件进行转换,转成用户可阅读的格式 */
				lazyvo.set("domain", "["+cbase+"]:["+cond+"]");
				
				ctrlparam=gzbo.getCtrlparam(); //new SalaryCtrlParamBo(this.conn,rset.getInt("salaryid"),ctrl_param);  //new SalaryCtrlParamBo(this.conn,rset.getInt("salaryid"));
				String manager=ctrlparam.getValue(SalaryCtrlParamBo.SHARE_SET, "user"); // 工资管理员，对共享类别有效
				String flow_flag=ctrlparam.getValue(SalaryCtrlParamBo.FLOW_CTRL, "flag"); 
				/**
				 * 审批模式 =1，需要审批 =其它值，不需要审批
				 */
				if("1".equalsIgnoreCase(flow_ctrl))
				{
					
					if(!"1".equalsIgnoreCase(flow_flag))
						continue;
					
//					SalaryCtrlParamBo ctrl_par = new SalaryCtrlParamBo(this.conn,Integer.parseInt(salaryid));
					String collectPoint = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD, "collect_field");
//					if(collectPoint!=null&&collectPoint.length()>0){//薪资汇总审批 搜房网需求  zhaoxg add
//						collectPoint = "/gz/gz_sp_collect/gz_sp_collect.do";
//					}else{
						collectPoint = "/gz/gz_accounting/gz_sp_orgtree.do";
//					}
					String sql = "select curr_user from salaryhistory where salaryid='"+salaryid+"' and curr_user='"+this.userview.getUserName()+"'";
					RowSet rs = dao.search(sql);
					if(rs.next()){
						lazyvo.set("isCurr_user", "审批");
						lazyvo.set("collectPoint", collectPoint);
					}else{
						lazyvo.set("isCurr_user", "查看");
						lazyvo.set("collectPoint", collectPoint);
					}
				}

				
				if(this.show_reject==1) //薪资发放，得到薪资类别的状态
				{
					if("1".equalsIgnoreCase(flow_flag))
					{
						String state_str=getFfState(manager,rset.getString("salaryid"),dbWizard);
						if(state_str!=null&&state_str.trim().length()>0)
							cname+=" ("+state_str+")";
					}
					
				}
				lazyvo.set("cname",cname);
				
				
				
				/** 自动升级薪资类别 */
				syncSalaryStruct(rset.getInt("salaryid"));
				/** 升级薪资表 */
			// SalaryTemplateBo templatebo=new
			// SalaryTemplateBo(this.conn,rset.getInt("salaryid"),this.userview);
			// templatebo.syncGzTableStruct();
				// searchCurrentDate2(lazyvo)
				
				if(!"1".equalsIgnoreCase(flow_ctrl))
				{
					LazyDynaBean abean=null;
					if(manager.length()==0)
						abean=searchCurrentDate3(rset.getString("salaryid"),this.userview.getUserName());
					else
						abean=searchCurrentDate3(rset.getString("salaryid"),manager);
					String strYm=abean.get("strYm")!=null?(String)abean.get("strYm"):"";
					String strC=abean.get("strC")!=null?(String)abean.get("strC"):"";
					if(strYm.length()>0)
					{
						lazyvo.set("appdate", strYm.substring(0, 7));
						lazyvo.set("count", strC);	
					}
					else  
					{
						lazyvo.set("appdate", "");
						lazyvo.set("count", "");	
					}
				}
				
				
				
				list.add(lazyvo);
			} 
			if(rset!=null)
				rset.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		
		return list;
	}
	
	
	public String getFfState(String manager,String salaryid,DbWizard dbWizard)
	{
		String str="";
		try
		{
			String tableName=this.userview.getUserName()+"_salary_"+salaryid;
			if(manager!=null&&manager.trim().length()>0)
				tableName=manager+"_salary_"+salaryid;
			if(!dbWizard.isExistTable(tableName,false))
				return str;
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select count(sp_flag) from "+tableName+" where sp_flag='07'");
			if(rowSet.next())
			{
				if(rowSet.getInt(1)>0)
					str="退回";
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return str;
	}
	
	
	
	
	public ArrayList searchGzSetList()throws GeneralException
	{
		ArrayList list=new ArrayList();
		StringBuffer buf=new StringBuffer();
		buf.append("select SALARYID from salarytemplate ");
		if(this.gz_type==0)
			buf.append(" where (CState is null or CState='')");// 薪资类别
		else
			buf.append(" where CState='1'");// 险种类别
		ContentDAO dao=new ContentDAO(this.conn);
		try {
			RowSet rset=dao.search(buf.toString());
			while(rset.next())
			{
				/** 加上权限过滤 */
				if(this.gz_type==0)
				{
					if(!this.userview.isHaveResource(IResourceConstant.GZ_SET, rset.getString("salaryid")))
						continue;
				}
				else
				{
					if(!this.userview.isHaveResource(IResourceConstant.INS_SET, rset.getString("salaryid")))
						continue;
				}
				list.add( rset.getString("salaryid"));
			}
			if(rset!=null)
				rset.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	
	
	/**
	 * 另存薪资类别
	 * 
	 * @param salaryid
	 */
	public void reSaveSalaryTemplate(String salaryid,String name)throws GeneralException
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			int a_salaryid=DbNameBo.getPrimaryKey("salarytemplate","salaryid",this.conn);  // 取得主键值
			name=name.replaceAll("'","’");
			name=name.replace("\"","”");
			StringBuffer sql=new StringBuffer("insert into salarytemplate (salaryid,cname,cbase,cond,cexpr,nmoneyid,kzero,cstate,nflag,lprogram,username,ctrl_param)");
			sql.append(" select "+a_salaryid+",'"+name+"',cbase,cond,cexpr,nmoneyid,kzero,cstate,nflag,lprogram,username,ctrl_param from salarytemplate ");
			sql.append(" where salaryid="+salaryid);
			dao.update(sql.toString());
			
			RowSet rowSet=dao.search("select max(seq) from salarytemplate");
			if(rowSet.next())
			{
				dao.update("update salarytemplate set seq="+(rowSet.getInt(1)+1)+" where salaryid="+a_salaryid);
			}
			
			sql.setLength(0);
			sql.append("insert into salaryset (salaryid,fieldid,fieldsetid,itemid,itemdesc,itemlength,decwidth,codesetid,sortid,nwidth,formula,initflag,heapflag,nlock,changeflag,itemtype)");
			sql.append(" select "+a_salaryid+",fieldid,fieldsetid,itemid,itemdesc,itemlength,decwidth,codesetid,sortid,nwidth,formula,initflag,heapflag,nlock,changeflag,itemtype ");
			sql.append(" from salaryset where salaryid="+salaryid);
			dao.update(sql.toString());
			/** 临时变量 */
			int mid_nid=DbNameBo.getPrimaryKey("midvariable","nId",this.conn);
			rowSet=dao.search("select * from midvariable where cstate='"+salaryid+"'");
			ArrayList list=new ArrayList();
			while(rowSet.next())
			{
				RecordVo vo=new RecordVo("midvariable");
				vo.setInt("nid",mid_nid);
				vo.setString("cname","yk"+mid_nid);
				vo.setString("chz",rowSet.getString("chz"));
				vo.setInt("ntype",rowSet.getInt("ntype"));
				if(rowSet.getString("cvalue")!=null)
					vo.setString("cvalue", rowSet.getString("cvalue"));
				vo.setInt("nflag",rowSet.getInt("nflag"));
				vo.setString("cstate",String.valueOf(a_salaryid));
				vo.setInt("fldlen",rowSet.getInt("FldLen"));
				vo.setInt("flddec", rowSet.getInt("FldDec"));
				vo.setInt("templetid",rowSet.getInt("TempletID"));
				vo.setString("codesetid",rowSet.getString("codesetid"));
				vo.setInt("sorting",rowSet.getInt("sorting"));
				list.add(vo);
				mid_nid++;
			}
			dao.addValueObject(list);
			/** 计算公式 */
			rowSet=dao.search("select * from salaryformula where salaryid="+salaryid+"");
			ArrayList list1=new ArrayList();
			while(rowSet.next())
			{
				RecordVo vo=new RecordVo("salaryformula");
				vo.setInt("salaryid",a_salaryid);
				vo.setInt("itemid",rowSet.getInt("itemid"));
				vo.setInt("sortid",rowSet.getInt("sortid"));
				vo.setString("hzname",rowSet.getString("hzname"));
				vo.setString("itemname",rowSet.getString("itemname"));
				vo.setString("rexpr",rowSet.getString("rexpr"));
				if(rowSet.getString("cond")!=null)
					vo.setString("cond",rowSet.getString("cond"));
				if(rowSet.getString("standid")!=null)
					vo.setInt("standid",rowSet.getInt("standid"));
				vo.setString("itemtype",rowSet.getString("itemtype"));
				vo.setInt("runflag",rowSet.getInt("runflag"));
				vo.setInt("useflag",rowSet.getInt("useflag"));
				if(rowSet.getString("cstate")!=null)
					vo.setString("cstate",rowSet.getString("cstate"));
				list1.add(vo);
			}
			dao.addValueObject(list1);
			
			if(!(this.userview.isAdmin()&& "1".equals(this.userview.getGroupId())))
			{
				UserObjectBo user_bo=new UserObjectBo(this.conn);
				if(this.gz_type==0)
					user_bo.saveResource(String.valueOf(a_salaryid),this.userview,IResourceConstant.GZ_SET);
				else
					user_bo.saveResource(String.valueOf(a_salaryid),this.userview,IResourceConstant.INS_SET);
					
			}
			//审核公式
			sql.setLength(0);
			sql.append("select *  from hrpchkformula where flag=1 and tabid='"+salaryid+"' order by seq");
			rowSet = dao.search(sql.toString());
			ArrayList flist = new ArrayList();
			IDGenerator idg = new IDGenerator(2, this.conn);
			int seq=this.getSeq()+1;
			while(rowSet.next())
			{
				RecordVo vo = new RecordVo("hrpchkformula");
				String spFormulaId = idg.getId("hrpchkformula.chkid");
				vo.setInt("chkid", Integer.parseInt(spFormulaId));
				vo.setInt("seq", seq);
				vo.setString("name",rowSet.getString("name")==null?"":rowSet.getString("name"));
				vo.setString("information",Sql_switcher.readMemo(rowSet, "information"));
				vo.setString("formula",Sql_switcher.readMemo(rowSet, "formula"));
				vo.setInt("tabid", a_salaryid);
				vo.setString("b0110", rowSet.getString("b0110")==null?"":rowSet.getString("b0110"));
				vo.setInt("flag", rowSet.getInt("flag"));
				if(rowSet.getString("validflag")!=null)
				{
					vo.setInt("validflag", rowSet.getInt("validflag"));
				}
				seq++;
				flist.add(vo);
			}
			dao.addValueObject(flist);
			
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	public int getSeq()
	{
		int seq = 0;
		try
		{
			String sql = "select max(seq) seq from hrpchkformula ";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs=dao.search(sql);
			while(rs.next())
			{
				seq=rs.getInt("seq");
			}
			rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return seq;
	}
	
	/**
	 * 重命名薪资类别
	 * 
	 * @param salaryid
	 * @param rename
	 * @throws GeneralException
	 * @author dengcan
	 */
	public void renameSalaryTemplate(String salaryid,String rename)throws GeneralException
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			dao.update("update salarytemplate set cname='"+rename+"' where salaryid="+salaryid);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	
	
	public String getDataValue(String fielditemid,String operate,String value)
	{
		StringBuffer a_value=new StringBuffer("");		
		try
		{

				if("=".equals(operate))
				{
					a_value.append("(");
					a_value.append(Sql_switcher.year(fielditemid)+operate+value.substring(0,4)+" and ");
					a_value.append(Sql_switcher.month(fielditemid)+operate+value.substring(5,7)+" and ");
					a_value.append(Sql_switcher.day(fielditemid)+operate+value.substring(8));
					a_value.append(" ) ");
				}
				else 
				{
					a_value.append("(");
					if(">=".equals(operate))
					{
						
						a_value.append(Sql_switcher.year(fielditemid)+">"+value.substring(0,4)+" or ( ");
						a_value.append(Sql_switcher.year(fielditemid)+"="+value.substring(0,4)+" and "+Sql_switcher.month(fielditemid)+">"+value.substring(5,7)+" ) or ( ");						
					}
					else if("<=".equals(operate))
					{
						
						a_value.append(Sql_switcher.year(fielditemid)+"<"+value.substring(0,4)+" or ( ");
						a_value.append(Sql_switcher.year(fielditemid)+"="+value.substring(0,4)+" and "+Sql_switcher.month(fielditemid)+"<"+value.substring(5,7)+" ) or ( ");						
					}
					else
					{
						
						a_value.append(Sql_switcher.year(fielditemid)+operate+value.substring(0,4)+" or ( ");
						a_value.append(Sql_switcher.year(fielditemid)+"="+value.substring(0,4)+" and "+Sql_switcher.month(fielditemid)+operate+value.substring(5,7)+" ) or ( ");
						
					}
					a_value.append(Sql_switcher.year(fielditemid)+"="+value.substring(0,4)+" and "+Sql_switcher.month(fielditemid)+"="+value.substring(5,7)+" and "+Sql_switcher.day(fielditemid)+operate+value.substring(8));
					a_value.append(") ) ");
				}

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return a_value.toString();
	}
	
	
	
	/**
	 * 工资历史数据初始化
	 * 
	 * @param intType //
	 *            1:全部 2：时间范围
	 * @param startDate
	 * @param endDate
	 * @param salaryid
	 */
	public void initSalaryHistoryData(String intType,String startDate,String endDate,String[] salaryid)throws GeneralException
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RecordVo recordvo=new RecordVo("salaryhistory");
			
			DbWizard dbWizard=new DbWizard(this.conn); 
			
			
			if(recordvo.hasAttribute("a00z2"))
			{
				
				if("1".equals(intType))
				{
					for(int i=0;i<salaryid.length;i++)
					{
						dao.delete("delete from salaryhistory where salaryid="+salaryid[i],new ArrayList());
						
						if(dbWizard.isExistTable("salaryarchive", false))
							dao.delete("delete from salaryarchive where salaryid="+salaryid[i],new ArrayList());
					/*
					 * RowSet rowSet=dao.search("select * from gz_extend_log
					 * where salaryid="+salaryid[i]); while(rowSet.next()) {
					 * String userName=rowSet.getString("userName");
					 * dao.delete("delete from
					 * "+userName+"_salary_"+salaryid[i],new ArrayList()); }
					 */
						int dbflag=Sql_switcher.searchDbServer();
						DbWizard dbw=new DbWizard(this.conn);
						String sql="";
						switch(dbflag)
						{
								case Constant.MSSQL:
									sql="select name from sysobjects where type='u' and lower(name) like '%_salary_"+salaryid[i]+"'";    //syscolumns 
									break;
								case Constant.DB2:
									sql="SELECT NAME FROM SYSIBM.SYSTABLES WHERE TYPE = 'T' AND CREATOR != 'SYSIBM' and name like '%_salary_"+salaryid[i]+"'";
									break;
								case Constant.ORACEL:
									sql="SELECT TABLE_NAME FROM USER_TABLES WHERE LOWER(TABLE_NAME) LIKE '%_salary_"+salaryid[i]+"'";  //all_tab_columns
									break;
						}
						if(sql.length()>0)
						{
								RowSet rowSet=dao.search(sql);
								while(rowSet.next())
								{
									String tableName=rowSet.getString(1);
									dao.delete("delete from "+tableName,new ArrayList());
								}
						}
						
						
						dao.delete("delete from gz_extend_log where salaryid="+salaryid[i], new ArrayList());					
						if(dbWizard.isExistTable("gz_tax_mx",false))
							dao.delete("delete from gz_tax_mx where salaryid="+salaryid[i], new ArrayList());  // 删除个人所得税中相关的数据
					}
				}
				else if("2".equals(intType))
				{
					StringBuffer whl=new StringBuffer("");
					for(int i=0;i<salaryid.length;i++)
					{
						whl.append(","+salaryid[i]);
					}
					
					int dbflag=Sql_switcher.searchDbServer();
					for(int i=0;i<salaryid.length;i++)
					{
						String sql="";
						switch(dbflag)
						{
								case Constant.MSSQL:
									sql="select name from sysobjects where type='u' and lower(name) like '%_salary_"+salaryid[i]+"'";
									break;
								case Constant.DB2:
									sql="SELECT NAME FROM SYSIBM.SYSTABLES WHERE TYPE = 'T' AND CREATOR != 'SYSIBM' and name like '%_salary_"+salaryid[i]+"'";
									break;
								case Constant.ORACEL:
									sql="SELECT TABLE_NAME FROM USER_TABLES WHERE LOWER(TABLE_NAME) LIKE '%_salary_"+salaryid[i]+"'";
									break;
						}
						if(sql.length()>0)
						{
								RowSet rowSet=dao.search(sql);
								while(rowSet.next())
								{
									String tableName=rowSet.getString(1);
									
									RecordVo recordvo2=new RecordVo(tableName.toLowerCase());
									if(recordvo2.hasAttribute("a00z2"))
									{
										StringBuffer ss=new StringBuffer("SELECT distinct(a00z2) FROM "+tableName+" where 1=1 ");
										if(startDate!=null&&startDate.length()>0)
										{
											ss.append(" and "+getDataValue("a00z2",">=",startDate));
										}
										if(endDate!=null&&endDate.length()>0)
										{
											ss.append(" and "+getDataValue("a00z2","<=",endDate));
										}
										RowSet rowSet2=dao.search(ss.toString());
										if(rowSet2.next())
										{
											dao.delete("delete from "+tableName,new ArrayList());
										}
									}
								}
						}
					}
					
					
					
					StringBuffer sql2=new StringBuffer("delete from gz_extend_log where salaryid in ("+whl.substring(1)+") ");
					StringBuffer sql=new StringBuffer("delete from salaryhistory where salaryid in ("+whl.substring(1)+") ");
					StringBuffer sql4=new StringBuffer("delete from salaryarchive where salaryid in ("+whl.substring(1)+") ");
					StringBuffer sql3=new StringBuffer("delete from gz_tax_mx where salaryid in ("+whl.substring(1)+") ");
					// if(intType.equals("2"))
					{
						if(startDate!=null&&startDate.length()>0)
						{
							sql.append(" and "+getDataValue("a00z2",">=",startDate));
							sql4.append(" and "+getDataValue("a00z2",">=",startDate));
							sql2.append(" and "+getDataValue("a00z2",">=",startDate));	
							sql3.append(" and "+getDataValue("a00z0",">=",startDate));
						}
						if(endDate!=null&&endDate.length()>0)
						{
							sql2.append(" and "+getDataValue("a00z2","<=",endDate));
							sql.append(" and "+getDataValue("a00z2","<=",endDate));
							sql4.append(" and "+getDataValue("a00z2","<=",endDate));
							sql3.append(" and "+getDataValue("a00z0","<=",endDate));
						}
					}
					dao.delete(sql.toString(),new ArrayList());
					dao.delete(sql2.toString(),new ArrayList());
					dao.delete(sql3.toString(),new ArrayList());
					if(dbWizard.isExistTable("salaryarchive", false))
						dao.delete(sql4.toString(),new ArrayList());
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	/**
	 * 覆盖导入薪资类别时，删除一些数据
	 * @param salaryid
	 */
	public void deleteCoverSalaryTemplate(String[] salaryid)
	{
		try{
			StringBuffer whl=new StringBuffer("");
			StringBuffer whl1=new StringBuffer("");
			for(int i=0;i<salaryid.length;i++)
			{
				whl.append(","+salaryid[i]);
				whl1.append(",'"+salaryid[i]+"'");
				
			}
			ContentDAO dao=new ContentDAO(this.conn);
			dao.delete("delete from salarytemplate where salaryid in ("+whl.substring(1)+")",new ArrayList());
			dao.delete("delete from salaryset where salaryid  in ("+whl.substring(1)+")",new ArrayList());
			// /删除薪资公式
			dao.delete("delete from salaryformula where salaryid  in ("+whl.substring(1)+")",new ArrayList());
			// 删除薪资临时变量
			dao.delete("delete from midvariable where cstate  in ("+whl1.substring(1)+")",new ArrayList());
			//删除工资报表项目表 在删除reportdetail数据前执行  zhaoxg add 2014-11-17
			dao.delete("delete from reportitem where rsdtlid  in (select rsdtlid from reportdetail where stid  in ("+whl1.substring(1)+"))",new ArrayList());
			//删除薪资报表   zhaoxg add 2014-11-10
			dao.delete("delete from reportdetail where stid  in ("+whl1.substring(1)+")",new ArrayList());
			// /删除审核公式
			dao.delete("delete from hrpchkformula where flag=1 and tabid  in ("+whl.substring(1)+")",new ArrayList());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 删除薪资类别
	 * 
	 * @param salaryid
	 * @throws GeneralException
	 * @author dengcan
	 */
	public void deleteSalaryTemplate(String[] salaryid)throws GeneralException
	{
		try
		{
			StringBuffer whl=new StringBuffer("");
			StringBuffer whl1=new StringBuffer("");
			for(int i=0;i<salaryid.length;i++)
			{
				whl.append(","+salaryid[i]);
				whl1.append(",'"+salaryid[i]+"'");
				
			}
			ContentDAO dao=new ContentDAO(this.conn);
			dao.delete("delete from salarytemplate where salaryid in ("+whl.substring(1)+")",new ArrayList());
			dao.delete("delete from salaryset where salaryid  in ("+whl.substring(1)+")",new ArrayList());
			// 删除工资数据表
			
			int dbflag=Sql_switcher.searchDbServer();
			DbWizard dbw=new DbWizard(this.conn);
			for(int i=0;i<salaryid.length;i++)
			{
				String sql="";
				switch(dbflag)
				{
					case Constant.MSSQL:
						sql="select name from sysobjects where type='u' and lower(name) like '%_salary_"+salaryid[i]+"'";
						break;
					case Constant.DB2:
						sql="SELECT NAME FROM SYSIBM.SYSTABLES WHERE TYPE = 'T' AND CREATOR != 'SYSIBM' and name like '%_salary_"+salaryid[i]+"'";
						break;
					case Constant.ORACEL:
						sql="SELECT TABLE_NAME FROM USER_TABLES WHERE LOWER(TABLE_NAME) LIKE '%_salary_"+salaryid[i]+"'";
						break;
				}
				if(sql.length()>0)
				{
					RowSet rowSet=dao.search(sql);
					while(rowSet.next())
					{
						String tableName=rowSet.getString(1);
						Table table=new Table(tableName);
						dbw.dropTable(table);
					}
				}
			}
			// 删除工资历史数据表相关纪录
			dao.delete("delete from salaryhistory where salaryid  in ("+whl.substring(1)+")",new ArrayList());
			// 删除工资历归档表相关纪录
			dao.delete("delete from salaryarchive where salaryid  in ("+whl.substring(1)+")",new ArrayList());
			// 删除工资税率归档表相关纪录
			dao.delete("delete from taxarchive where salaryid  in ("+whl.substring(1)+")",new ArrayList());
			// 删除薪资公式
			dao.delete("delete from salaryformula where salaryid  in ("+whl.substring(1)+")",new ArrayList());
			// 删除薪资临时变量
			dao.delete("delete from midvariable where cstate  in ("+whl1.substring(1)+")",new ArrayList());
			// 删除工资发放表记录
			dao.delete("delete from gz_extend_log where salaryid  in ("+whl.substring(1)+")",new ArrayList());
			// 删除个税明细
			dao.delete("delete from gz_tax_mx where salaryid  in ("+whl.substring(1)+")",new ArrayList());
			// 删除审核公式   zhaoxg add 2013-9-26
			dao.delete("delete from hrpchkformula where flag=1 and tabid  in ("+whl1.substring(1)+")",new ArrayList());
			
			// 删除薪资报表明细   zhaoxg add 2014-11-21
			dao.delete("delete from reportitem where rsdtlid  in (select rsdtlid from reportdetail where stid  in ("+whl.substring(1)+"))",new ArrayList());
			// 删除薪资报表  zhaoxg add 2014-11-21
			dao.delete("delete from reportdetail where stid  in ("+whl.substring(1)+")",new ArrayList());

			//清空内存中的薪资类别参数信息
			for(int i=0;i<salaryid.length;i++)
			{
				SalaryCtrlParamBo.docMap.remove(salaryid[i]);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	
	
	/**
	 * 新建工资类别
	 * 
	 * @param name
	 *            类别名称
	 * @author dengcan
	 * @throws GeneralException
	 */
	public void addSalaryTemplate(String name)throws GeneralException
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select "+Sql_switcher.isnull("max(seq)","0")+"+1 from salarytemplate");
			int seq=0;
			if(rowSet.next())
				seq=rowSet.getInt(1);
			else
				seq=1;
			RecordVo vo=new RecordVo("salarytemplate");
			int salaryid=DbNameBo.getPrimaryKey("salarytemplate","salaryid",this.conn);  // 取得主键值
			vo.setInt("salaryid",salaryid);
			vo.setString("cname",name);
			vo.setString("cbase","Usr,");
			vo.setInt("nmoneyid",0);
			vo.setInt("kzero",0);
			vo.setInt("nflag",0);
			vo.setInt("seq",seq);
			vo.setString("username",this.userview.getUserName());
			if(this.gz_type==1)
				vo.setString("cstate","1");
			dao.addValueObject(vo);
			addDefaultSalaryset(salaryid);  // 新增默认工资项目
			
			if(!(this.userview.isAdmin()&& "1".equals(this.userview.getGroupId())))
			{
				UserObjectBo user_bo=new UserObjectBo(this.conn);
				if(this.gz_type==0)
					user_bo.saveResource(String.valueOf(salaryid),this.userview,IResourceConstant.GZ_SET);
				else
					user_bo.saveResource(String.valueOf(salaryid),this.userview,IResourceConstant.INS_SET);
			}
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	/**
	 * 新增默认工资项目
	 * 
	 * @param salaryid
	 * @author dengcan
	 */
	public void addDefaultSalaryset(int salaryid)throws GeneralException
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			ArrayList list=new ArrayList();
			list.add(getSalarySetRecordvo(salaryid,0,"A00","NBASE","人员库标识",3,0,"0",2,0,"",3,1,"A"));
			list.add(getSalarySetRecordvo(salaryid,1,"A01","A0100","人员编号",8,0,"0",0,0,"",3,1,"A"));
			list.add(getSalarySetRecordvo(salaryid,2,"A01","A0000","人员序号",15,0,"0",1,0,"A0100",3,1,"N"));
			list.add(getSalarySetRecordvo(salaryid,3,"A00","A00Z2","发放日期",20,0,"0",3,10,"A0000",3,1,"D"));
			list.add(getSalarySetRecordvo(salaryid,4,"A00","A00Z3","发放次数",15,0,"0",4,10,"",3,1,"N"));
			list.add(getSalarySetRecordvo(salaryid,5,"A00","A00Z0","归属日期",20,0,"0",5,0,"",3,1,"D"));
			list.add(getSalarySetRecordvo(salaryid,6,"A00","A00Z1","归属次数",15,0,"0",6,0,"",3,1,"N"));
			list.add(getSalarySetRecordvo(salaryid,7,"A01","B0110","单位名称",30,0,"UN",7,20,"B0110",3,1,"A"));
			list.add(getSalarySetRecordvo(salaryid,8,"A01","E0122","部门",30,0,"UM",8,20,"E0122",3,1,"A"));
			list.add(getSalarySetRecordvo(salaryid,9,"A01","A0101","姓名",30,0,"0",9,10,"A0101",3,1,"A"));
			list.add(getSalarySetRecordvo(salaryid,10,"A01","A01Z0","停发标识",1,0,"ZZ",10,7,"A01Z0",3,1,"A"));
			dao.addValueObject(list);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	
	public RecordVo getSalarySetRecordvo(int salaryid,int fieldid,String fieldsetid,String itemid,String itemdesc,
			int itemlength,int decwidth,String codesetid,int sortid,int nwidth,String formula,int initflag,int nlock,String itemtype)
	{
		RecordVo vo=new RecordVo("salaryset");
		vo.setInt("salaryid",salaryid);
		vo.setInt("fieldid",fieldid);
		vo.setString("fieldsetid",fieldsetid);
		vo.setString("itemid",itemid.toUpperCase());
		vo.setString("itemdesc",itemdesc);
		vo.setInt("itemlength",itemlength);
		vo.setInt("decwidth",decwidth);
		vo.setString("codesetid",codesetid);
		vo.setInt("sortid",sortid);
		vo.setInt("nwidth",nwidth);
		//五项专项扣除，子女教育等添加指标时公式中应该加上中括号
		if(ResourceFactory.getProperty("label.gz.znjy").equals(formula) || ResourceFactory.getProperty("label.gz.jxjy").equals(formula) ||
				ResourceFactory.getProperty("label.gz.zfzj").equals(formula) || ResourceFactory.getProperty("label.gz.zfdk").equals(formula) ||
				ResourceFactory.getProperty("label.gz.sylr").equals(formula)) {
			formula = "[" + formula + "]";
		}
		vo.setString("formula",formula);
		vo.setInt("initflag",initflag);
		vo.setInt("nlock",nlock);
		vo.setString("itemtype",itemtype);
		return vo;
	}
	/**
	 * 导入薪资类别时，如果是覆盖方式，先删除所有与该薪资类别有关的数据
	 * 
	 * @param repeats
	 */
	public HashMap deleteRepeatRecord(String repeats)
	{
		HashMap map = new HashMap();
		try
		{
			if(repeats==null|| "".equals(repeats))
				return map;
    		String[] arr= repeats.substring(1).split(",");
    		StringBuffer ids=new StringBuffer();
    		for(int i=0;i<arr.length;i++)
    		{
     			if(arr[i]==null|| "".equals(arr[i]))
    				continue;
    			String[] temp=arr[i].split("`");
    			String salaryid = temp[0];
    			String repeatid=temp[1];
    			String oldid="";
    			if(temp.length>2)
    				oldid=temp[2];
    			map.put(salaryid,repeatid);
	    		if("1".equals(repeatid)&&!"".equals(oldid))// 覆盖原来的记录
	    		{
	    			ids.append(",");
    				ids.append(oldid);// 原来的，要被覆盖掉的薪资类别
    			}
     		}
	    	if(ids!=null&&ids.toString().trim().length()>0)
	    	{
	    		String salaryids = ids.toString().substring(1);
	    		String array[] =salaryids.split(",");
	    		this.deleteCoverSalaryTemplate(array);
     		}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
		
	}
	public int getMaxSalaryid()
	{
		int n=0;
		try
		{
			String sql = "select MAX(salaryid) as salaryid from salarytemplate";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql);
			while(rs.next())
			{
				n=rs.getInt("salaryid");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return n;
	}
	/**
	 * 将新追加的工资类别授权给当前用户
	 * 
	 * @param maxid
	 */
	public void saveSalarySetResource(String[] salarySetIDs)
	{
		try
		{
			
			for(int i=0;i<salarySetIDs.length;i++)
			{
				String salaryid = salarySetIDs[i];
				if(!(this.userview.isAdmin()&& "1".equals(this.userview.getGroupId())))
				{
					UserObjectBo user_bo=new UserObjectBo(this.conn);
					if(this.gz_type==0&&!this.userview.isHaveResource(IResourceConstant.GZ_SET, salaryid))
						user_bo.saveResource(String.valueOf(salaryid),this.userview,IResourceConstant.GZ_SET);
					else if(!this.userview.isHaveResource(IResourceConstant.INS_SET, salaryid))
						user_bo.saveResource(String.valueOf(salaryid),this.userview,IResourceConstant.INS_SET);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 同步临时表B0110_O,E0122_O字段
	 * @param salaryid salaryhistory的salaryid字段值
	 * @param tempTable 临时表名称
	 * @throws Exception
	 */
	public void synSalaryTable(String salaryid,String tempTable) throws Exception {
		boolean flag = false;//判断数据库表结构在程序动行中是否有变化
		DbWizard dbw=new DbWizard(this.conn);
		DBMetaModel dbmodel=new DBMetaModel(this.conn);
		Table table=new Table(tempTable.toUpperCase());		
		//判断临时表、salaryhistory中是否存在B0110_O,E0122_O字段
		RecordVo vo = new RecordVo(tempTable.toLowerCase());
		if(!vo.hasAttribute("b0110_o"))
		{
			flag = true;
			Field field=new Field("B0110_O","B0110_O");
			field.setDatatype(DataType.INT);
			field.setLength(4);
			table.addField(field);
		}
		if(!vo.hasAttribute( "e0122_o"))
		{
			flag = true;
			Field field=new Field("E0122_O","E0122_O");
			field.setDatatype(DataType.INT);
			field.setLength(4);
			table.addField(field);
		}
		if(!vo.hasAttribute("dbid"))
		{
			flag = true;
			Field field=new Field("dbid","dbid");
			field.setDatatype(DataType.INT);
			field.setLength(4);
			table.addField(field);
		}
		if(flag){
			dbw.addColumns(table);
			dbmodel.reloadTableModel(tempTable.toUpperCase());
		}
		flag = false;
		table=new Table("SalaryHistory".toUpperCase());	
		vo = new RecordVo("SalaryHistory".toUpperCase());
		if(!vo.hasAttribute("b0110_o"))
		{
			flag = true;
			Field field=new Field("B0110_O","B0110_O");
			field.setDatatype(DataType.INT);
			field.setLength(4);
			table.addField(field);
		}
		if(!vo.hasAttribute("e0122_o"))
		{
			flag = true;
			Field field=new Field("E0122_O","E0122_O");
			field.setDatatype(DataType.INT);
			field.setLength(4);
			table.addField(field);
		}
		if(!vo.hasAttribute("dbid"))
		{
			flag = true;
			Field field=new Field("dbid","dbid");
			field.setDatatype(DataType.INT);
			field.setLength(4);
			table.addField(field);
		}
		if(flag){
			dbw.addColumns(table);
			dbmodel.reloadTableModel("SalaryHistory".toUpperCase());
		}
		//同步历史表数据
		ContentDAO dao = new ContentDAO(this.conn);
		
		String sql = "update SalaryHistory set E0122_O=(select A0000 from organization where codesetid='UM' and organization.codeitemid=SalaryHistory.E0122) where SalaryHistory.salaryid="+salaryid;
		dao.update(sql);
		sql = "update SalaryHistory set B0110_O=(select A0000 from organization where codesetid='UN' and SalaryHistory.B0110=organization.codeitemid) where SalaryHistory.salaryid="+salaryid;
		dao.update(sql);
		sql = "update SalaryHistory set dbid=(select dbid from dbname where upper(SalaryHistory.nbase)=upper(dbname.pre)) where SalaryHistory.salaryid="+salaryid;
		dao.update(sql);
		//同步临时表数据
		sql = "update "+tempTable+" set B0110_O=(select A0000 from organization where codesetid='UN' and "+tempTable+".B0110=organization.codeitemid)";
		dao.update(sql);
		sql = "update "+tempTable+" set E0122_O=(select A0000 from organization where codesetid='UM' and organization.codeitemid="+tempTable+".E0122)";
		dao.update(sql);
		sql = "update "+tempTable+" set dbid=(select dbid from dbname where upper("+tempTable+".nbase)=upper(dbname.pre))";
		dao.update(sql);
	}



	public int getShow_reject() {
		return show_reject;
	}



	public void setShow_reject(int show_reject) {
		this.show_reject = show_reject;
	}



	public static FastHashMap getAllitem_hm() {
		return allitem_hm;
	}



	public static void setAllitem_hm(FastHashMap allitem_hm) {
		SalaryPkgBo.allitem_hm = allitem_hm;
	}

}
