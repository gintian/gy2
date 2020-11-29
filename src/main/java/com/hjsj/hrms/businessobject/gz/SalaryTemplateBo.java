/**
 * 
 */
package com.hjsj.hrms.businessobject.gz;


import com.hjsj.hrms.businessobject.attestation.AttestationUtils;
import com.hjsj.hrms.businessobject.attestation.mobile.NoteCheckSend;
import com.hjsj.hrms.businessobject.dingtalk.DTalkBo;
import com.hjsj.hrms.businessobject.gz.gz_analyse.GzAnalyseBo;
import com.hjsj.hrms.businessobject.gz.templateset.GzExcelBo;
import com.hjsj.hrms.businessobject.gz.templateset.GzItemVo;
import com.hjsj.hrms.businessobject.gz.templateset.SalaryStandardBo;
import com.hjsj.hrms.businessobject.gz.templateset.tax_table.CalcTaxBo;
import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.performance.kh_system.kh_template.KhTemplateBo;
import com.hjsj.hrms.businessobject.sys.*;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.gz.gzspcollect.businessobject.GzSpCollectBo;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.analyse.YearMonthCount;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hjsj.hrms.utils.sendmessage.weixin.WeiXinBo;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.struts.upload.FormFile;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;


/**
 *<p>Title:薪资类别业务类</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-6-29:下午04:08:28</p> 
 *@author cmq
 *@version 4.0
 */
public class SalaryTemplateBo implements GzContant {
	private Connection conn=null;
	/**薪资类别号*/
	private int salaryid=-1;
	/**薪资表名称*/
	private String gz_tablename;

	/**薪资项目列表,field对象列表，为了构建薪资表*/
	private ArrayList fieldlist;
	/**登录用户*/
	private UserView userview;
	/**薪资表数据过滤SQL*/
	private String sql;
	/**薪资控制参数*/
	private SalaryCtrlParamBo ctrlparam=null;
	/**薪资类别数据对象*/
	private RecordVo templatevo=null; 
	/**当前薪资类别涉及到的子集列表*/
	private ArrayList setlist=new ArrayList();
	/**薪资项目列表,GzItemVo类对象*/
	private ArrayList gzitemlist=new ArrayList();
	/**当前用户和本薪资类别处理到的年月标识和次数*/
	private String currym;
	private String currcount;
	/**薪资补发数据表*/
	private String gz_bf_table="templet_gzbf";
	
	/**薪资项目和临时变量列表*/
	private ArrayList fldvarlist=new ArrayList();
	
	/**项目过滤列表,主要为了不用每次都查询这些配置参数*/
	static ArrayList  itemlist;
	static HashMap    itemmap;
	/**人员过滤条件列表*/
	static ArrayList condlist;
	static HashMap   condmap;
	private HashMap  clearItemMap=new HashMap();
	private String   errorInfo="";
	
	private String   manager="";  //工资管理员，对共享类别有效;
	private String   standardGzItemStr="/";  //标准薪资表涉及到的字段。
	
	private String   filterWhl="";  //分批确认 条件
	private String   filterWhl2="";  //分批确认 条件(审批确认)
	public void setFilterWhl2(String filterWhl2) {
		this.filterWhl2 = filterWhl2;
	}



	private String   _history_condWhl=""; //历史表分批确认条件
	private String   sp_filterWhl="";
	private String onlyField="";//系统唯一性指标，变动比对时加入
	/**工资项目页面的查询值*/
	private String queryvalue="";
	/**
	 * 
	 */
	private String   from_module="";  //哪个模块访问   noPreData:新建薪资表时不取上次数据
	
	private String   controlByUnitcode="0";  //共享类别非管理员按操作单位控制权限
	private ArrayList add_delList = new ArrayList();//zhaoxg add 搜房网新增与减少人员需求 2013-11-13
	
	//gby添加，人员筛选条件
	private String screeningWhereSql = "";
	private String _withNoLock="";//搜房网 并发 死锁
	
	public SalaryTemplateBo(Connection conn, int salaryid,UserView userview) {
		super();
		if(Sql_switcher.searchDbServer()!=2) //针对SQLSERVER 无需考虑锁表
			_withNoLock=" WITH(NOLOCK) ";
		this.conn = conn;
		this.salaryid = salaryid;
		this.userview=userview;
		ctrlparam=new SalaryCtrlParamBo(this.conn,this.salaryid);
		this.manager=ctrlparam.getValue(SalaryCtrlParamBo.SHARE_SET, "user");
		initData();
		
		for(int i=0;i<this.fieldlist.size();i++)
		{
			Field field=(Field)this.getFieldlist().get(i);
			String name=field.getName().toUpperCase();
			standardGzItemStr+=name+"/";
		}
		standardGzItemStr+="USERFLAG/";
		standardGzItemStr+="SP_FLAG/";
		standardGzItemStr+="APPPROCESS/";
		
		this.controlByUnitcode=controlByUnitcode();
	}
	public SalaryTemplateBo(Connection conn)
	{
		this.conn = conn;
		if(Sql_switcher.searchDbServer()!=2) //针对SQLSERVER 无需考虑锁表
			_withNoLock=" WITH(NOLOCK) ";
	}
	 /**
     * 新增与减少人员的显示指标，
     * flag：1新增  2减少
     * @param dao
     * @throws GeneralException
     */
	public ArrayList add_del_rightList(int salaryid,String flag){
		ArrayList retlist=new ArrayList();
		SalaryCtrlParamBo ctrl_par = new SalaryCtrlParamBo(conn,salaryid);
		String rightvalue = "";

		if("1".equals(flag)){
			rightvalue = ctrl_par.getValue(SalaryCtrlParamBo.ADD_MAN_FIELD);
		}else if("2".equals(flag)){
			rightvalue = ctrl_par.getValue(SalaryCtrlParamBo.DEL_MAN_FIELD);
		}
		rightvalue=rightvalue!=null?rightvalue.replaceAll(",","','"):"";
		ContentDAO dao = new ContentDAO(conn);
		StringBuffer sql=new StringBuffer();
		sql.append("select itemid,itemdesc,sortid from salaryset where  salaryid=");
		sql.append(salaryid);
		sql.append(" and itemid in ('");
		sql.append(rightvalue);
		sql.append("') order by sortid");
		ArrayList dylist = null;
		try {
			dylist = dao.searchDynaList(sql.toString());
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				CommonData obj=new CommonData(dynabean.get("itemid").toString(),dynabean.get("itemdesc").toString());
				retlist.add(obj);
			}
		} catch (GeneralException e) {
			e.printStackTrace();
		}
		return retlist;
	}
	public ArrayList getadd_delList(String rightvalue,String salaryid,String onlyname){
		String cloumnStr="DBNAME,A0100,A0000,B0110,E0122,A0101,STATE";
		boolean addflag=this.isAddColumn(this.getOnlyField(), cloumnStr);
		ArrayList list = new ArrayList();
		rightvalue=rightvalue!=null?rightvalue.replaceAll(",","','"):"";
		ContentDAO dao = new ContentDAO(conn);
		StringBuffer sql=new StringBuffer();
		sql.append("select itemid,itemdesc,sortid from salaryset where initflag<>3 and salaryid=");
		sql.append(salaryid);
		sql.append(" and itemid in ('");
		sql.append(rightvalue);
		sql.append("') group by itemid,itemdesc,sortid order by sortid");
		ArrayList dylist = null;
		try {
			dylist = dao.searchDynaList(sql.toString());
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				FieldItem item = DataDictionary.getFieldItem(dynabean.get("itemid").toString());
				if((item!=null&&cloumnStr.indexOf(item.getItemid())==-1)&&!(onlyname.equalsIgnoreCase(item.getItemid())))//过滤重复字段
				{
					list.add(item);
				}
			}
		}catch (GeneralException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	
	/**
	 * 薪资审批保存审批指标  搜房网  zhaoxg 2013-11-18
	 * @param vo
	 * @param rightvalue
	 * @throws GeneralException
	 */
	public ArrayList saveSpXml(RecordVo vo,String rightvalue,boolean change) throws GeneralException{
		boolean _flag = false;
		ArrayList _list = new ArrayList();
		try
		{
		if(vo!=null&&vo.getString("lprogram")!=null){
			
			String lprogram= vo.getString("lprogram");
			if(lprogram==null||lprogram.trim().length()==0)
				lprogram="<?xml version=\"1.0\" encoding=\"GB2312\"?><Params></Params>";
    		Document doc;
			if(vo.getString("lprogram").indexOf("hidden_items")!=-1){
				if(vo.getString("lprogram").indexOf("hidden_item")!=-1){
					
						doc =PubFunc.generateDom(lprogram);
						_list.add(doc.clone());
						Element root = doc.getRootElement();
						Element hidden_items = root.getChild("hidden_items");
						if(!change){
							hidden_items.removeChildren("hidden_item");
						}
						List list = hidden_items.getChildren("hidden_item");
						if(list.size()>0)
							for(int i=0;i<list.size();i++){
								Element child =(Element)list.get(i);
								if(child!=null&&(child.getAttributeValue("user_name")==null||"".equals(child.getAttributeValue("user_name"))))
								{
									child.setText(rightvalue);
					        		 XMLOutputter outputter=new XMLOutputter();
					     			Format format=Format.getPrettyFormat();
					     			format.setEncoding("UTF-8");
					     			outputter.setFormat(format);
					     			String xml =outputter.outputString(doc);
					     			_list.add(doc.clone());
					        		  BankDiskSetBo bo = new BankDiskSetBo(this.conn);
					      		    bo.updateLprogram(salaryid+"",xml);
									_flag =true;
									break;
								}
				        		
							}
						if(!_flag){//新增一个用户
							Element child = new Element("hidden_item"); 
			        		 child.setText(rightvalue);
			        		 hidden_items.addContent(child);
			        		 XMLOutputter outputter=new XMLOutputter();
			     			Format format=Format.getPrettyFormat();
			     			format.setEncoding("UTF-8");
			     			outputter.setFormat(format);
			     			String xml =outputter.outputString(doc);
			     			_list.add(doc.clone());
			        		  BankDiskSetBo bo = new BankDiskSetBo(this.conn);
			      		    bo.updateLprogram(salaryid+"",xml);
							
						}
				}else{
					doc = PubFunc.generateDom(lprogram);
					_list.add(doc.clone());
					Element root = doc.getRootElement();
					Element hidden_items = root.getChild("hidden_items");
					Element child = new Element("hidden_item"); 
	        		 child.setText(rightvalue);
	        		 hidden_items.addContent(child);
	        		 XMLOutputter outputter=new XMLOutputter();
	     			Format format=Format.getPrettyFormat();
	     			format.setEncoding("UTF-8");
	     			outputter.setFormat(format);
	     			String xml =outputter.outputString(doc);
	     			_list.add(doc.clone());
	        		BankDiskSetBo bo = new BankDiskSetBo(this.conn);
	      		    bo.updateLprogram(salaryid+"",xml);
			
				}
			}else{
				//建元素
				
				
					doc = PubFunc.generateDom(lprogram);
					_list.add(doc.clone());
					Element root = doc.getRootElement();
					Element hidden_items = new Element("hidden_items"); 
					root.addContent(hidden_items);
					Element child = new Element("hidden_item"); 
	        		 child.setText(rightvalue);
	        		 hidden_items.addContent(child);
	        		 XMLOutputter outputter=new XMLOutputter();
	     			Format format=Format.getPrettyFormat();
	     			format.setEncoding("UTF-8");
	     			outputter.setFormat(format);
	     			String xml =outputter.outputString(doc);
	     			_list.add(doc.clone());
	        		BankDiskSetBo bo = new BankDiskSetBo(this.conn);
	      		    bo.updateLprogram(salaryid+"",xml);
					
				
	    		
			}
			_flag=true;
		}else{
			
		}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return _list;
	}
	public void saveSpXml(RecordVo vo,String rightvalue) throws GeneralException{
		boolean _flag = false;
		try
		{
		if(vo!=null&&vo.getString("lprogram")!=null){
			
			String lprogram= vo.getString("lprogram");
			if(lprogram==null||lprogram.trim().length()==0)
				lprogram="<?xml version=\"1.0\" encoding=\"GB2312\"?><Params></Params>";
 
    		Document doc;
			if(vo.getString("lprogram").indexOf("hidden_items")!=-1){
				if(vo.getString("lprogram").indexOf("hidden_item")!=-1){
					
						doc = PubFunc.generateDom(lprogram);
						Element root = doc.getRootElement();
						Element hidden_items = root.getChild("hidden_items");
						List list = hidden_items.getChildren("hidden_item");
						if(list.size()>0)
							for(int i=0;i<list.size();i++){
								Element child =(Element)list.get(i);
								if(child!=null&&(child.getAttributeValue("user_name")==null||"".equals(child.getAttributeValue("user_name"))))
								{
									child.setText(rightvalue);
					        		 XMLOutputter outputter=new XMLOutputter();
					     			Format format=Format.getPrettyFormat();
					     			format.setEncoding("UTF-8");
					     			outputter.setFormat(format);
					     			String xml =outputter.outputString(doc);
					        		  BankDiskSetBo bo = new BankDiskSetBo(this.conn);
					      		    bo.updateLprogram(salaryid+"",xml);
									_flag =true;
									break;
								}
				        		
							}
						if(!_flag){//新增一个用户
							Element child = new Element("hidden_item"); 
			        		 child.setText(rightvalue);
			        		 hidden_items.addContent(child);
			        		 XMLOutputter outputter=new XMLOutputter();
			     			Format format=Format.getPrettyFormat();
			     			format.setEncoding("UTF-8");
			     			outputter.setFormat(format);
			     			String xml =outputter.outputString(doc);
			        		  BankDiskSetBo bo = new BankDiskSetBo(this.conn);
			      		    bo.updateLprogram(salaryid+"",xml);
							
						}
				}else{
					doc =PubFunc.generateDom(lprogram);
					Element root = doc.getRootElement();
					Element hidden_items = root.getChild("hidden_items");
					Element child = new Element("hidden_item"); 
	        		 child.setText(rightvalue);
	        		 hidden_items.addContent(child);
	        		 XMLOutputter outputter=new XMLOutputter();
	     			Format format=Format.getPrettyFormat();
	     			format.setEncoding("UTF-8");
	     			outputter.setFormat(format);
	     			String xml =outputter.outputString(doc);
	        		  BankDiskSetBo bo = new BankDiskSetBo(this.conn);
	      		    bo.updateLprogram(salaryid+"",xml);
			
				}
			}else{
				//建元素
				
				
					doc = PubFunc.generateDom(lprogram);
					Element root = doc.getRootElement();
					Element hidden_items = new Element("hidden_items"); 
					root.addContent(hidden_items);
					Element child = new Element("hidden_item"); 
	        		 child.setText(rightvalue);
	        		 hidden_items.addContent(child);
	        		 XMLOutputter outputter=new XMLOutputter();
	     			Format format=Format.getPrettyFormat();
	     			format.setEncoding("UTF-8");
	     			outputter.setFormat(format);
	     			String xml =outputter.outputString(doc);
	        		  BankDiskSetBo bo = new BankDiskSetBo(this.conn);
	      		    bo.updateLprogram(salaryid+"",xml);
					
				
	    		
			}
			_flag=true;
		}else{
			
		}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	/**
	 * 获得审批关系
	 * @return
	 */
	public String getSpRelationId()
	{
		String relation_id="";
		try
		{
			String  sp_relation_id=ctrlparam.getValue(SalaryCtrlParamBo.FLOW_CTRL,"sp_relation_id");
			String  flow_ctrl=ctrlparam.getValue(SalaryCtrlParamBo.FLOW_CTRL,"flag");
			if(flow_ctrl==null||flow_ctrl.trim().length()==0|| "0".equals(flow_ctrl))
				sp_relation_id="";
			
			DbWizard dbw = new DbWizard(this.conn);
			if(dbw.isExistTable("t_wf_relation",false))
			{
				if(sp_relation_id!=null&&sp_relation_id.trim().length()>0)
				{
					ContentDAO dao=new ContentDAO(this.conn);
					String sql="select * from t_wf_relation where validflag=1 and actor_type='4' and relation_id="+sp_relation_id;
					RowSet rowSet=dao.search(sql);
					if(rowSet.next())
					{
						relation_id=sp_relation_id;
					}
				} 
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return relation_id;
	}
	
	
	
	/**
	 * 获得当前用户在审批关系中定义的直接领导信息
	 * @return
	 */
	public String getSpActorStr(String sp_relation_id)
	{
		StringBuffer str=new StringBuffer("");
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String sql="select usergroup.groupName,twm.mainbody_id,operuser.fullname from t_wf_mainbody twm,operuser,usergroup where twm.mainbody_id=operuser.username and" 
						+" operuser.groupid=usergroup.groupid and twm.relation_id="+sp_relation_id+" "
						+" and lower(twm.object_id)='"+this.userview.getUserName().toLowerCase()+"' and twm.sp_grade=9 order by twm.mainbody_id";
			RowSet rowSet=dao.search(sql);
			while(rowSet.next())
			{
						String name=rowSet.getString(3);
						if(name==null||name.trim().length()==0)
							name=rowSet.getString(2);
						str.append("`"+rowSet.getString(1)+"##"+name+"##"+rowSet.getString(2)); 
			} 
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		if(str.length()>0)
			return str.substring(1);
		return str.toString();
	}
	
	
	
	/**
	 * 如果薪资类别是共享类别，操作用户是非管理员，并且属性设置了归属单位或部门，则为：1   否则为：0
	 * @return
	 */
	public String controlByUnitcode()
	{
		String flag="0";
		String orgid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD,"orgid");
		orgid = orgid != null ? orgid : "";
		String deptid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid");
		deptid = deptid != null ? deptid : "";
		if(this.manager!=null&&this.manager.trim().length()>0)
		{
			if(!this.userview.getUserName().equalsIgnoreCase(this.manager))
			{
				if(orgid.length()>0||deptid.length()>0)
					flag="1";
			}
		}
		return flag;
	}
	
	public String getWhlByUnits_info()
	{
		String whereIN="";
		String unitcodes=this.userview.getUnit_id();  //UM010101`UM010105`
		if(unitcodes!=null&& "UN`".equalsIgnoreCase(unitcodes))
		{
			
		}
		else
		{
			if(unitcodes==null||unitcodes.length()==0|| "UN".equalsIgnoreCase(unitcodes))
			{
				String a_code=""; 
				if(!this.userview.isSuper_admin())
				{
					if(this.userview.getManagePrivCode().length()==0)
						a_code="1=2";
					else if("@K".equals(this.userview.getManagePrivCode()))
						a_code=getUnByPosition(this.userview.getManagePrivCodeValue());
					else
						a_code=this.userview.getManagePrivCode()+this.userview.getManagePrivCodeValue();
					
				}
				
				 if("1=2".equals(a_code))
					 whereIN+=" and 1=2 ";
				 else if(a_code.length()>0)
					 unitcodes=a_code+"`";
			}
			if(unitcodes.length()>0)
			{
				String[] temps=unitcodes.split("`");
				StringBuffer whl=new StringBuffer("");
				for(int n=0;n<temps.length;n++)
				{
					if(temps[n].trim().length()>0)
					{
						String code=temps[n].substring(0,2); 
						String codevalue=temps[n].substring(2);
						if("UN".equals(code))
						{
							 whl.append(" or b0110 like '"+codevalue+"%'  or e0122 like '"+codevalue+"%' ");
						}
						else if("UM".equals(code))
						{
							 whl.append(" or e0122 like '"+codevalue+"%'  ");
						}
					}
				} 
				
				if(whl.length()>0)
				{
					whereIN+=" and ( "+whl.substring(3)+" ) ";
				}
			} 
		}
		return whereIN;
	}
	
	
	
	/**
	 * 薪资审批模块  对与不走审批的数据需按管理范围控制
	 * @return
	 */
	public String getPrivWhlStr(String tableName)
	{
		String str="";
		if(tableName!=null&&tableName.trim().length()>0)
			str=tableName+".";
		StringBuffer whl=new StringBuffer(""); 
		String orgid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD,"orgid");
		orgid = orgid != null ? orgid : "";
		String deptid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid");
		deptid = deptid != null ? deptid : "";
		
		String unitcodes=this.userview.getUnit_id();  //UM010101`UM010105`
		if(unitcodes!=null&& "UN`".equalsIgnoreCase(unitcodes))
		{
			
		}
		else
		{
			if(unitcodes==null||unitcodes.length()==0|| "UN".equalsIgnoreCase(unitcodes))
			{
				String a_code=""; 
				if(!this.userview.isSuper_admin())
				{
					if(this.userview.getManagePrivCode().length()==0)
						a_code="1=2";
					else if("@K".equals(this.userview.getManagePrivCode()))
						a_code=getUnByPosition(this.userview.getManagePrivCodeValue());
					else
						a_code=this.userview.getManagePrivCode()+this.userview.getManagePrivCodeValue();
					
				}
				
				if("".equals(a_code))
					return "";
				else if("1=2".equals(a_code))
					return " and 1=2 ";
				else
					unitcodes=a_code+"`";
			}
			
			
			String[] temps=unitcodes.split("`");
			if(orgid.trim().length()>0&&deptid.trim().length()>0)
			{
				for(int i=0;i<temps.length;i++)
				{
					if(temps[i].trim().length()>0)
					{
						 whl.append(" or  "+str+deptid+" like '"+temps[i].substring(2)+"%' ");
					}
				}
				
				for(int i=0;i<temps.length;i++)
				{
					if(temps[i].trim().length()>0)
					{
						if("UN".equals(temps[i].substring(0,2)))
						{
							 whl.append(" or  "+str+orgid+" like '"+temps[i].substring(2)+"%' ");
						}
					}
				} 
			}
			else if(orgid.trim().length()>0)
			{
				for(int i=0;i<temps.length;i++)
				{
					if(temps[i].trim().length()>0)
					{
						if("UN".equals(temps[i].substring(0,2)))
						{
							 whl.append(" or  "+str+orgid+" like '"+temps[i].substring(2)+"%' ");
						}
					}
				} 
			}
			else if(deptid.trim().length()>0)
			{
				for(int i=0;i<temps.length;i++)
				{
					if(temps[i].trim().length()>0)
					{
						 whl.append(" or  "+str+deptid+" like '"+temps[i].substring(2)+"%' ");
					}
				}
			}
			else
			{
				
				for(int i=0;i<temps.length;i++)
				{
					if(temps[i].trim().length()>0)
					{
						if("UN".equalsIgnoreCase(temps[i].substring(0,2)))
						{
							 whl.append(" or  "+str+"b0110 like '"+temps[i].substring(2)+"%' ");
						}
						else if("UM".equalsIgnoreCase(temps[i].substring(0,2)))
						{
							 whl.append(" or  "+str+"E0122 like '"+temps[i].substring(2)+"%' ");
						}
					}
				} 
				
			}
			
			if(whl.length()==0)
				return " and 1=2 ";
		}
		String whl_str="";
		if(whl.length()>0)
			whl_str=" and ( "+whl.substring(3)+" ) ";
		return whl_str;
	}
	
	
	
	
	public String getWhlByUnits()
	{
		StringBuffer whl=new StringBuffer(""); 
		String orgid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD,"orgid");
		orgid = orgid != null ? orgid : "";
		String deptid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid");
		deptid = deptid != null ? deptid : "";
		
		String unitcodes=this.userview.getUnitIdByBusiOutofPriv("1");  //UM010101`UM010105`
		if(unitcodes!=null&& "UN`".equalsIgnoreCase(unitcodes))
		{
			
		}
		else
		{
			if(unitcodes==null||unitcodes.length()==0|| "UN".equalsIgnoreCase(unitcodes))
			{
				String a_code=""; 
				if(!this.userview.isSuper_admin())
				{
					if(this.userview.getManagePrivCode().length()==0)
						a_code="1=2";
					else if("@K".equalsIgnoreCase(this.userview.getManagePrivCode()))
						a_code=getUnByPosition(this.userview.getManagePrivCodeValue());
					else
						a_code=this.userview.getManagePrivCode()+this.userview.getManagePrivCodeValue();
					
				}
				
				if("".equals(a_code))
					return "";
				else if("1=2".equals(a_code))
					return " and 1=2 ";
				else
					unitcodes=a_code+"`";
			}
			
			
			String[] temps=unitcodes.split("`");
			if(orgid.trim().length()>0&&deptid.trim().length()>0)
			{
				for(int i=0;i<temps.length;i++)
				{
					if(temps[i].trim().length()>0)
					{
						 whl.append(" or "+this.gz_tablename+"."+deptid+" like '"+temps[i].substring(2)+"%' ");
					}
				}
				
				for(int i=0;i<temps.length;i++)
				{
					if(temps[i].trim().length()>0)
					{
						if("UN".equalsIgnoreCase(temps[i].substring(0,2)))
						{
							 whl.append(" or "+this.gz_tablename+"."+orgid+" like '"+temps[i].substring(2)+"%' ");
						}
					}
				} 
			}
			else if(orgid.trim().length()>0)
			{
				for(int i=0;i<temps.length;i++)
				{
					if(temps[i].trim().length()>0)
					{
						if("UN".equalsIgnoreCase(temps[i].substring(0,2)))
						{
							 whl.append(" or "+this.gz_tablename+"."+orgid+" like '"+temps[i].substring(2)+"%' ");
						}
					}
				} 
			}
			else if(deptid.trim().length()>0)
			{
				for(int i=0;i<temps.length;i++)
				{
					if(temps[i].trim().length()>0)
					{
						 whl.append(" or "+this.gz_tablename+"."+deptid+" like '"+temps[i].substring(2)+"%' ");
					}
				}
			}
			
			if(whl.length()==0)
				return " and 1=2 ";
		}
		String whl_str="";
		if(whl.length()>0)
			whl_str=" and ( "+whl.substring(3)+" ) ";
		return whl_str;
	}
	
	
	/**
	 * 根据职位找直属部门
	 * @param codeid
	 * @return
	 */
	public String getUnByPosition(String codeid)
	{
		String str="";
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select * from organization where codeitemid=(select parentid from organization where codeitemid='"+codeid+"')");
			if(rowSet.next())
			{
				str=rowSet.getString("codesetid")+rowSet.getString("codeitemid");
			}
			rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return str;
	}
	

	/**
	 *当前薪资类别是否需要审批
	 */
	public boolean isApprove()
	{
		boolean bflag=false;
		String flow_flag=ctrlparam.getValue(SalaryCtrlParamBo.FLOW_CTRL, "flag");
		if("1".equalsIgnoreCase(flow_flag))
			bflag=true;
		return bflag;
	}
	
	/**
	 * 判断是否满足提交数据条件（审批流程的工资类别）
	 *   如果所有记录都为结束则不允许再确认,
	 *   如果所有记录为已批或 有的是已批有的是确认,则可以提交
	 * @author dengcan
	 * @return
	 */
	public boolean isSubCondition()
	{
		boolean flag=false;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			
			StringBuffer sql=new StringBuffer("select a,b,c from ");
			sql.append(" (select count(a0100) a from "+this.gz_tablename+" where sp_flag='06' ) aa, ");
			sql.append(" (select count(a0100) b from "+this.gz_tablename+" ) bb,");
			sql.append(" (select count(a0100) c from "+this.gz_tablename+" where sp_flag='06' or sp_flag='03'  ) cc");	
			
		//	RowSet rowSet=dao.search("select count(a0100) from "+this.gz_tablename+" where sp_flag<>'03'");
			RowSet rowSet=dao.search(sql.toString());
			if(rowSet.next())
			{
				int a=rowSet.getInt("a");
				int b=rowSet.getInt("b");
				int c=rowSet.getInt("c");
				if(a==b)
					flag=false;
				else if(b==c)
					flag=true;
				
			//	if(rowSet.getInt(1)==0)
			//		flag=true;
			}
			rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
	
	
	
	/**
	 * 升级历史薪资表（添加薪资类别中有的而历史表没有的字段）
	 */
	private void upgradGzHisTableStruct2()throws GeneralException 
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
	//		RecordVo vo=new RecordVo("salaryhistory");	
			StringBuffer buf=new StringBuffer();
			
	//		if(vo==null)
	//			return;
			
	//		ArrayList list=vo.getModelAttrs();				
			buf.setLength(0);
	/*		for(int i=0;i<list.size();i++)
			{
				String name=(String)list.get(i);
				buf.append(name.toUpperCase());
				buf.append(",");
			}
			*/
			//for i loop end. 
			RowSet rowSet=dao.search("select * from salaryhistory where 1=2");
			ResultSetMetaData metaData=rowSet.getMetaData();
			for(int i=1;i<=metaData.getColumnCount();i++)
			{				
				buf.append(metaData.getColumnName(i).toUpperCase());
				buf.append(",");		
			}
			 
			metaData=null;
			rowSet.close();
			StringBuffer buf0=new StringBuffer();
			ArrayList addlist=new ArrayList();
			/**如果定义中有，而薪资历史表中没有，则增加此字段*/
			for(int i=0;i<this.fieldlist.size();i++)
			{
				Field field=(Field)this.getFieldlist().get(i);
				String name=field.getName().toUpperCase();
				/**如果未找到，则追加*/
				if(buf.indexOf(name+",")==-1)
				{
					addlist.add(field);
				}
				buf0.append(name);
				buf0.append(",");
			}//for i loop end.
			 
			DbWizard dbw=new DbWizard(this.conn);
			Table table=new Table("salaryhistory");
			for(int i=0;i<addlist.size();i++)
				table.addField((Field)addlist.get(i));
			if(addlist.size()>0)
				dbw.addColumns(table);
			 
			table.clear(); 
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 升级历史薪资表结构salaryhistory
	 * 增加A00Z2,A00Z3,sp_flag
	 * 修改主键字段
	 * @throws GeneralException
	 */
	private void upgradeGzHisTableStruct()throws GeneralException
	{
		StringBuffer buf=new StringBuffer();
		try
		{
			DbWizard dbw=new DbWizard(this.conn);
			ContentDAO dao=new ContentDAO(this.conn);
	//		RecordVo vo=new RecordVo("salaryhistory");	
			
			RowSet rowSet=dao.search("select * from salaryhistory where 1=2");
			ResultSetMetaData metaData=rowSet.getMetaData();			
			HashMap voMap=new HashMap();
			for(int i=1;i<=metaData.getColumnCount();i++)
			{				
				voMap.put(metaData.getColumnName(i).toUpperCase(),"1");			
			}
			metaData=null;
			rowSet.close();
			
			
			
			
			
			
			Table table=new Table("salaryhistory");
			Field field=null;
			/**A00Z2发放日期*/
			//if(!(vo.hasAttribute("A00Z2")||vo.hasAttribute("a00z2")))
			if(voMap.get("A00Z2")==null)
			{
				field=new Field("A00Z2",ResourceFactory.getProperty("gz.columns.a00z2"));
				field.setDatatype(DataType.DATE);
				table.addField(field);
				/**发放次数*/
				field=new Field("A00Z3",ResourceFactory.getProperty("gz.columns.a00z3"));
				field.setDatatype(DataType.INT);
				table.addField(field);
				dbw.addColumns(table);
				/**正常情况下,发放日期=归属日期,发放次数=归属次数*/
				buf.append("update salaryhistory");
				buf.append(" set A00Z2=A00Z0,A00Z3=A00Z1");
				dao.update(buf.toString());

				/**A00Z0,A00Z1,把可为空改成不能为空*/
				table.clear();
				field=new Field("A00Z1","A00Z1");
				field.setDatatype(DataType.INT);
				field.setLength(10);
				field.setNullable(false);
				table.addField(field);
				field=new Field("A00Z0","A00Z0");
				field.setDatatype(DataType.DATE);
				field.setNullable(false);
				table.addField(field);
				dbw.alterColumns(table);				
				/**修改主键*/
				/**先删除主键索引*/
				dbw.dropPrimaryKey("salaryhistory");
				table.clear();
				field=new Field("NBASE","NBASE");
				field.setKeyable(true);
				table.addField(field);
				field=new Field("A0100","A0100");
				field.setKeyable(true);
				table.addField(field);				
				field=new Field("A00Z0","A00Z0");
				field.setKeyable(true);
				table.addField(field);					
				field=new Field("A00Z1","A00Z1");
				field.setKeyable(true);
				table.addField(field);	
				field=new Field("salaryid","salaryid");
				field.setKeyable(true);
				table.addField(field);	
				dbw.addPrimaryKey(table);
			}//
			/**审批状态字段*/
		//	if(!vo.hasAttribute("sp_flag"))
			if(voMap.get("SP_FLAG")==null)
			{
				table.clear();
				field=new Field("sp_flag","sp_flag");
				field.setDatatype(DataType.STRING);
				field.setLength(2);
				table.addField(field);
				dbw.addColumns(table);				
			}
			
		//	if(!(vo.hasAttribute("appprocess")||vo.hasAttribute("APPPROCESS")))
			if(voMap.get("APPPROCESS")==null)
			{
				table.clear();
				field=new Field("appprocess","appprocess");
				field.setDatatype(DataType.CLOB);
				table.addField(field);
				dbw.addColumns(table);				
			}
			
		//	if(!(vo.hasAttribute("appuser")||vo.hasAttribute("APPUSER")))
			if(voMap.get("APPUSER")==null)
			{
				table.clear();
				field=new Field("Appuser","Appuser");
				field.setDatatype(DataType.STRING);
				field.setLength(200);
				table.addField(field);
				dbw.addColumns(table);				
			}
			
		//	if(!(vo.hasAttribute("curr_user")||vo.hasAttribute("CURR_USER")))
			if(voMap.get("CURR_USER")==null)
			{
				table.clear();
				field=new Field("Curr_user","Curr_user");
				field.setDatatype(DataType.STRING);
				field.setLength(50);
				table.addField(field);
				dbw.addColumns(table);				
			}
			
			if(voMap.get("E0122_O")==null)
			{
				table.clear();
				field=new Field("E0122_O","E0122_O");
				field.setDatatype(DataType.INT);
				field.setLength(10);
				table.addField(field);
				dbw.addColumns(table);				
			}
			
			if(voMap.get("B0110_O")==null)
			{
				table.clear();
				field=new Field("B0110_O","B0110_O");
				field.setDatatype(DataType.INT);
				field.setLength(10);
				table.addField(field);
				dbw.addColumns(table);				
			}
			
			if(voMap.get("DBID")==null)
			{
				table.clear();
				field=new Field("dbid","dbid");
				field.setDatatype(DataType.INT);
				field.setLength(10);
				table.addField(field);
				dbw.addColumns(table);				
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);				
		}
	}
	
	
	/**
	 * system.properties  salaryitem=false前台计算项不能编辑
	 * @param fieldlist
	 */
	public void setFieldlist_read2(ArrayList fieldlist)
	{
		try
		{
			ArrayList formulaList=getFormulaList(-1);
			HashMap map=new HashMap();
			for(int i=0;i<formulaList.size();i++)
			{
				  DynaBean dbean=(LazyDynaBean)formulaList.get(i);
				  String itemname=(String)dbean.get("itemname");
				  map.put(itemname.toLowerCase(),"1");
			}
			
			
			Field field=null;
			for(int i=0;i<fieldlist.size();i++)
			{
				field=(Field)fieldlist.get(i);
				if(map.get(field.getName().toLowerCase())!=null)
					field.setReadonly(true);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * 将薪资项目设为只读状态，不让操作数据集
	 * @param fieldlist
	 */
	public void setFieldlist_read(ArrayList fieldlist)
	{
		Field field=null;
		for(int i=0;i<fieldlist.size();i++)
		{
			field=(Field)fieldlist.get(i);
			field.setReadonly(true);
		}
		
	}
	

	/**
	 * 保存薪资数据提交方式
	 * @param setlist		需要归档提交的数据集列表
	 * @param typelist		数据集提交类型列表
	 * @return
	 * @throws GeneralException
	 */
	public boolean saveSubmitType(ArrayList setlist,ArrayList typelist,String items,String uptypes)throws GeneralException
	{
		boolean bflag=true;
		try
		{	
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer buf=new StringBuffer();
			HashMap itemUptype=getItemUpdateType(items,uptypes);
			
			for(int i=0;i<setlist.size();i++)
			{
				buf.append(setlist.get(i));
				buf.append("`");
				buf.append(typelist.get(i));
				buf.append(";");
			}
			
			for(Iterator t=itemUptype.keySet().iterator();t.hasNext();)
			{
				String key=(String)t.next();
				if("0".equals((String)itemUptype.get(key)))
				{	buf.append(key.toUpperCase());
					buf.append(";");
				}
			} 
			
			SalaryLProgramBo lpbo=new SalaryLProgramBo(this.templatevo.getString("lprogram"));
			lpbo.setValue(SalaryLProgramBo.CONFIRM_TYPE,buf.toString());
			String str=lpbo.outPutContent();
			this.templatevo.setString("lprogram", str);
			RecordVo vo=new RecordVo("salarytemplate");
			vo.setString("lprogram", str);
			vo.setInt("salaryid", this.salaryid);
			dao.updateValueObject(vo);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			bflag=false;
			throw GeneralExceptionHandler.Handle(ex);			
		}
		return bflag;		
	}
	
	/**
	 * 保存薪资数据提交方式
	 * @param setlist		需要归档提交的数据集列表
	 * @param typelist		数据集提交类型列表
	 * @return
	 * @throws GeneralException
	 */
	public String saveSubmitType(ArrayList setlist,ArrayList typelist,String items,String uptypes,
	           String subNoShowUpdateFashion,String subNoPriv,String allowEditSubdata)throws GeneralException
	{
		String bflag="";
		try
		{	
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer buf=new StringBuffer();
			HashMap itemUptype=getItemUpdateType(items,uptypes);
			
			for(int i=0;i<setlist.size();i++)
			{
				buf.append(setlist.get(i));
				buf.append("`");
				buf.append(typelist.get(i));
				buf.append(";");
			}
			
			for(Iterator t=itemUptype.keySet().iterator();t.hasNext();)
			{
				String key=(String)t.next();
				if("0".equals((String)itemUptype.get(key)))
				{	buf.append(key.toUpperCase());
					buf.append(";");
				}
			} 
			
			SalaryLProgramBo lpbo=new SalaryLProgramBo(this.templatevo.getString("lprogram"));
			lpbo.setValue(SalaryLProgramBo.CONFIRM_TYPE,buf.toString());
			lpbo.setValue(SalaryLProgramBo.CONFIRM_TYPE,"no_show",subNoShowUpdateFashion);
			lpbo.setValue(SalaryLProgramBo.CONFIRM_TYPE,"subNoPriv",subNoPriv);
			lpbo.setValue(SalaryLProgramBo.CONFIRM_TYPE,"allow_edit_subdata",allowEditSubdata);
			String str=lpbo.outPutContent();
			bflag = str;
			this.templatevo.setString("lprogram", str);
			RecordVo vo=new RecordVo("salarytemplate");
			vo.setString("lprogram", str);
			vo.setInt("salaryid", this.salaryid);
			dao.updateValueObject(vo);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);			
		}
		return bflag;		
	}
	
	
	
	
	
	
	/**
	 * 求对应的每个子集中指标项目列表
	 * @param setlist
	 * @return
	 */
	private ArrayList getUpdateFields(ArrayList setlist)
	{
		ArrayList fieldlist=new ArrayList();
		try
		{
			String subNoPriv=getLprogramAttri("subNoPriv",SalaryLProgramBo.CONFIRM_TYPE);
			if(subNoPriv==null||subNoPriv.trim().length()==0)
				subNoPriv="0";
			
			
			for(int i=0;i<setlist.size();i++)
			{
				StringBuffer buf=new StringBuffer();
				for(int j=0;j<gzitemlist.size();j++)
				{
					GzItemVo itemvo=(GzItemVo)gzitemlist.get(j);
					String fieldname=itemvo.getFldname();
					if(itemvo.getInitflag()==3&&!"A01Z0".equalsIgnoreCase(fieldname))
						continue;
					
					FieldItem fielditem=DataDictionary.getFieldItem(fieldname);
					if(fielditem==null|| "0".equalsIgnoreCase(fielditem.getUseflag()))
						continue;
					if("0".equals(subNoPriv))
					{
						if(!"2".equalsIgnoreCase(this.userview.analyseFieldPriv(fieldname)))
							continue;
					}
					if(fielditem.getFieldsetid().equalsIgnoreCase((String)setlist.get(i)))
					{
						buf.append(fieldname);
						buf.append(",");
					}
				}	//for j end.
				fieldlist.add(buf.toString());
			}//for i end.			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return fieldlist;
	}
	
	
	/**
	 * 薪资历史数据表取数条件  sql
	 * @param payDateBean
	 * @return
	 */
	public String getWhlConditionSql(LazyDynaBean payDateBean)
	{
		StringBuffer buf=new StringBuffer("");
		String codesetid=this.userview.getManagePrivCode();
		String value=this.userview.getManagePrivCodeValue();
		/*
		if(codesetid.equalsIgnoreCase("UN"))
		{
			buf.append(" and (b0110 like '");
			buf.append(value);
			buf.append("%'");
			if(value.equalsIgnoreCase(""))
			{
				buf.append(" or b0110 is null");
			}
			buf.append(")");
		}
		else if(codesetid.equalsIgnoreCase("UM"))
		{
			buf.append(" and e0122 like '");
			buf.append(value);
			buf.append("%'");
		}
		else 
			buf.append(" and 1=2 ");
		*/
		buf.append(" and "+Sql_switcher.year("a00z2")+"="+(String)payDateBean.get("year"));
		buf.append(" and "+Sql_switcher.month("a00z2")+"="+(Integer.parseInt((String)payDateBean.get("month"))));
		buf.append(" and "+Sql_switcher.day("a00z2")+"="+(String)payDateBean.get("day"));
		buf.append(" and a00z3="+(String)payDateBean.get("count"));
		buf.append(" and sp_flag='06' ");
		return buf.toString();
	}
	
	/**
	 * 取得数据集的年月标识
	 * @param setList
	 * @return
	 */
	public HashMap getFieldSetChangeflag(ArrayList setList)
	{
		HashMap map=new HashMap();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer whl=new StringBuffer("");
			for(int i=0;i<setList.size();i++)
			{
				LazyDynaBean setBean=(LazyDynaBean)setList.get(i);
				String setid=(String)setBean.get("setid");
				whl.append(",'"+setid+"'");
			}
			RowSet rowSet=dao.search("select fieldsetid,changeflag  from fieldset where fieldsetid in ("+whl.substring(1)+")");
			while(rowSet.next())
			{
				map.put(rowSet.getString(1).toLowerCase(),rowSet.getString(2));
			}
			rowSet.close();
		}
		catch(Exception ex)
		{
				ex.printStackTrace();	
		}
		return map;
	}
	
	
	/**
	 * 校验报批上来的数据与对应的原薪资发放中的数据是否一致
	 * @param whl
	 * @return
	 * @throws GeneralException
	 */
	public boolean validateInfo(String whl,ArrayList userFlagList)throws GeneralException
	{
		boolean bflag=true;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			HashMap map=new HashMap();
		//	System.out.println("select count(a0100),userflag from salaryhistory where 1=1 "+whl+" group by userflag ");
			RowSet rowSet=dao.search("select count(a0100),userflag from salaryhistory  "+this._withNoLock+" where 1=1 "+whl+" group by userflag ");
			while(rowSet.next())
				map.put(rowSet.getString(2),rowSet.getString(1));
			if(map.size()>0)
			{
				for(int i=0;i<userFlagList.size();i++)
				{
					String userFlag=(String)userFlagList.get(i);
					RowSet rowSet2=dao.search("select count(*) from "+userFlag+"_salary_"+this.salaryid);
					if(rowSet2.next())
					{
						if(rowSet2.getInt(1)!=Integer.parseInt((String)map.get(userFlag)))
						{
							rowSet2=dao.search("select * from operuser where upper(username)='"+userFlag.toUpperCase()+"'");
							if(rowSet2.next()&&rowSet2.getString("fullname")!=null&&rowSet2.getString("fullname").length()>0)
								throw GeneralExceptionHandler.Handle(new Exception("用户："+rowSet2.getString("fullname")+" 薪资发放中的人数与报批上来的人数不一致，不允许审批确认操作!"));	
							else
								throw GeneralExceptionHandler.Handle(new Exception("用户："+userFlag+" 薪资发放中的人数与报批上来的人数不一致，不允许审批确认操作!"));	
						}
						
					}
					rowSet2.close();
				}
			}
			rowSet.close();
			
		}
		catch(Exception ex)
		{
				ex.printStackTrace();
				bflag=false;
				throw GeneralExceptionHandler.Handle(ex);	
		}
		return bflag;
	}
	
	
	
	/**
	 * 将符合发放日期的薪资历史数据 提交到 档案库中（修改档案库中对应的数据）
	 * @param bosdate 业务日期(发放日期)
	 * @param count   发放次数
	 * @return
	 */
	public boolean submitGzDataFromHistory(ArrayList setlist,ArrayList typelist,String items,String uptypes,String bosdate,String count)throws GeneralException
	{
		boolean bflag=true;
		try
		{
			
			ContentDAO dao=new ContentDAO(this.conn);
	//		ArrayList setList=getSubmitTypeList();
			/** 取得数据集的年月标识 */
			LazyDynaBean payDateBean=getSalaryPayDate(bosdate,count);
			/** 薪资历史数据表取数条件 sql */
			StringBuffer buf=new StringBuffer("");
			buf.append(" and salaryhistory.salaryid="+this.salaryid+" ");
			buf.append(" and "+Sql_switcher.year("salaryhistory.a00z2")+"="+(String)payDateBean.get("year"));
			buf.append(" and "+Sql_switcher.month("salaryhistory.a00z2")+"="+(Integer.parseInt((String)payDateBean.get("month"))));
			buf.append(" and salaryhistory.a00z3="+(String)payDateBean.get("count"));	
			buf.append(" and ( ( salaryhistory.AppUser is null or salaryhistory.AppUser Like '%;"+this.userview.getUserName()+";%' ) ");
		
			this._history_condWhl=buf.toString();
			this._history_condWhl+=" and ( salaryhistory.sp_flag='03' or  salaryhistory.sp_flag='06' ) )";
			//this._history_condWhl+=" and salaryhistory.salaryid="+this.salaryid+" ";
			if(this.sp_filterWhl!=null&&this.sp_filterWhl.trim().length()>0)
				this._history_condWhl+=this.sp_filterWhl;
			
			buf.append(" and  salaryhistory.sp_flag='03' )");
			//buf.append(" and salaryhistory.salaryid="+this.salaryid+" ");
			if(this.sp_filterWhl!=null&&this.sp_filterWhl.trim().length()>0)
				buf.append(this.sp_filterWhl);
			String history_condWhl=buf.toString();	
			
			
			ArrayList userFlagList=new ArrayList();
			
			RowSet rowSet=null;
			SalaryCtrlParamBo ctrlparam=new SalaryCtrlParamBo(this.conn,this.salaryid);
			this.manager=ctrlparam.getValue(SalaryCtrlParamBo.SHARE_SET, "user");
			if(this.manager!=null&&this.manager.length()>0)
			{
				rowSet=dao.search("select distinct userflag from salaryhistory "+this._withNoLock+" where 1=1 "+history_condWhl);
				if(rowSet.next())
					userFlagList.add(this.manager);
			}
			else
			{
				rowSet=dao.search("select distinct userflag from salaryhistory "+this._withNoLock+" where 1=1 "+history_condWhl);
				while(rowSet.next())
					userFlagList.add(rowSet.getString(1));
			}	
			
			if(userFlagList.size()==0)
				throw GeneralExceptionHandler.Handle(new Exception("没有可审批确认的数据!"));	
		
			/** 同步历史表和薪资临时表 */
			
			try
			{
				String sql="update salaryhistory set appuser=';"+this.userview.getUserName()+";'"+Sql_switcher.concat()+"appuser  where 1=1 "+history_condWhl;
				dao.update(sql);
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
			
			for(int j=0;j<userFlagList.size();j++)
			{
				String userFlag=(String)userFlagList.get(j);
				this.gz_tablename=userFlag+"_salary_"+this.salaryid;
				//LazyDynaBean dataBean=getSalaryPayDate(null,null);  
				String tableName=userFlag+"_salary_"+this.salaryid;
				StringBuffer ss=new StringBuffer("delete from "+tableName+" where exists (select null from salaryhistory "+this._withNoLock+"  where ");
				ss.append("  salaryhistory.a00z0="+tableName+".a00z0 and salaryhistory.a00z1="+tableName+".a00z1 and upper(salaryhistory.nbase)=upper("+tableName+".nbase) and ");
				ss.append(" salaryhistory.a0100="+tableName+".a0100 "+history_condWhl+" and upper(userflag)='"+userFlag.toUpperCase()+"'  )");
				dao.delete(ss.toString(),new ArrayList());
			
				rowSet=dao.search("select * from "+tableName+" where 1=2");
				ResultSetMetaData metaData=rowSet.getMetaData();
				StringBuffer s1=new StringBuffer("");
				StringBuffer s2=new StringBuffer("");
				for(int i=1;i<=metaData.getColumnCount();i++)
				{				
						if(this.standardGzItemStr.indexOf("/"+metaData.getColumnName(i).toUpperCase()+"/")==-1)
							continue;
					
						if("sp_flag".equalsIgnoreCase(metaData.getColumnName(i)))
						{
							s1.append(","+metaData.getColumnName(i));
							s2.append(",'03'");
						}
						else
						{
								s1.append(","+metaData.getColumnName(i));
								s2.append(","+metaData.getColumnName(i));
						
						}
				}
				dao.update("insert into "+tableName+" ("+s1.substring(1)+") select "+s2.substring(1)+" from salaryhistory "+this._withNoLock+" where  upper(userflag)='"+userFlag.toUpperCase()+"'  "+history_condWhl);
				metaData=null;
			}
			 
			rowSet.close();
			StringBuffer ss=new StringBuffer("");
		
			DbWizard dbw=new DbWizard(this.conn);
			String tableName="t#"+this.userview.getUserName()+"_gz_3";  
		//	if(dbw.isExistTable(tableName,false))
				 dbw.dropTable(tableName);  
			String _sql="  from salaryhistory "+this._withNoLock+" where  1=1 "+history_condWhl;
			if(Sql_switcher.searchDbServer()==2)
				 dbw.execute("create table "+tableName+" as  select * "+_sql);	
			else
				 dbw.execute("select *   into "+tableName+_sql);
			
			
			
			
			/**取得人员库前缀列表*/
			String dbpres=this.templatevo.getString("cbase");
			/**应用库前缀*/
			String[] dbarr=StringUtils.split(dbpres, ",");
			String strNow=Sql_switcher.sqlNow(); 
			/**求得对应子集的指标串,比如AXXX1,AXXX0*/
			ArrayList updatelist=getUpdateFields(setlist);
			/**薪资表数据->档案库*/
			HashMap itemUptype=getItemUpdateType(items,uptypes);
			//判断提交的薪资类别是否包含了年月变化子集,并且为新增方式
			boolean isYMaddSet=isymChangeSet(setlist,typelist); 
			StringBuffer sub_sql=new StringBuffer("");
			ArrayList userFlagList_noRedo=new ArrayList();
			
			for(int i=0;i<userFlagList.size();i++)
			{
				String userFlag=(String)userFlagList.get(i);
				LazyDynaBean abean=getGzExtendLog(String.valueOf(this.salaryid),bosdate,count,userFlag);
				String isRedo=(String)abean.get("isRedo");
				String sp_flag=(String)abean.get("sp_flag");
				if(!"06".equals(sp_flag)&& "1".equals(isRedo)) //解决薪资重发问题
				{
					sub_sql.append(" and upper(salaryhistory.userflag)!='"+userFlag.toUpperCase()+"'" );
				}
				else
					userFlagList_noRedo.add(userFlag);
			}
			 
			if(userFlagList_noRedo.size()>0)
			{
				this.filterWhl=history_condWhl+sub_sql.toString();
				ArrayList z0z1List=getZ0z1List("salaryhistory"); 
				for(int i=0;i<dbarr.length;i++)
				{ 
					String cbase=dbarr[i];
					this.gz_tablename="salaryhistory";
					
					/** 处理只有一条薪资记录的用户一般子集数据 */
					dealwithSingleRecord_history(setlist,updatelist,typelist,dbarr,i,dbw,itemUptype,strNow);
					
					String tempName="t#"+this.userview.getUserName()+"_gz";  
					String strym="";
					/** 处理只有一条薪资记录的用户年月子集数据 */
					for(int j=0;j<z0z1List.size();j++)
					{
						LazyDynaBean abean=(LazyDynaBean)z0z1List.get(j);
						strym=(String)abean.get("strym"); 
					    String[] temp=strym.split("-"); 
						String singleRecord_where="(select count(a0100) c,a0100 from "+this.gz_tablename+" "+this._withNoLock+" where "; 
						singleRecord_where+=" lower("+this.gz_tablename+".NBASE)='"+cbase.toLowerCase()+"'  "+filterWhl+" ";
						singleRecord_where+=" and  "+Sql_switcher.year(this.gz_tablename+".a00z0")+"="+temp[0]+" and "+Sql_switcher.month(this.gz_tablename+".a00z0")+"="+temp[1]+"   group by a0100  having count(a0100)=1 ) aa";
					//     if(dbw.isExistTable(tempName,false))
					     { 
					    	 dbw.dropTable(tempName);
					     }
					     StringBuffer sql0=new StringBuffer("");
					     if(Sql_switcher.searchDbServer()==2)
					    	 sql0.append("create table "+tempName+" as ");
					     sql0.append("select "+this.gz_tablename+".a0100,"+this.gz_tablename+".a00z0,"+this.gz_tablename+".add_flag,"+this.gz_tablename+".a00z1");
					     if(Sql_switcher.searchDbServer()!=2)
					    	 sql0.append(" into "+tempName);
					     sql0.append(" from "+this.gz_tablename +this._withNoLock +","+singleRecord_where);
					     sql0.append(" where aa.a0100="+this.gz_tablename+".a0100 and  ");
					     sql0.append(Sql_switcher.year(this.gz_tablename+".a00z0")+"="+temp[0]+" and "+Sql_switcher.month(this.gz_tablename+".a00z0")+"="+temp[1]);
					     sql0.append(filterWhl+" and exists (select null from "+cbase+"A01 where "+cbase+"A01.a0100="+this.gz_tablename+".a0100 )    and lower("+this.gz_tablename+".NBASE)='"+cbase.toLowerCase()+"' ");
					     dao.update(sql0.toString());
					     
					     dealwithSingleRecord_ym_history(userFlagList_noRedo,setlist,true,isYMaddSet,updatelist,typelist,cbase,dbw,itemUptype,strNow,strym);	
					     
					}  
					
					dealwithMulRecord_history2(setlist,isYMaddSet,updatelist,typelist, dbarr,i,dbw,itemUptype);
					
				}
			}
			 
			for(int i=0;i<userFlagList.size();i++)
			{
				String userFlag=(String)userFlagList.get(i);
				this.gz_tablename=userFlag+"_salary_"+this.salaryid;
 
				
				ss.setLength(0);
				ss.append(" and exists (select null from salaryhistory "+this._withNoLock );
				ss.append(" where salaryhistory.a00z0="+this.gz_tablename+".a00z0 and salaryhistory.a00z1="+this.gz_tablename+".a00z1 and upper(salaryhistory.nbase)=upper("+this.gz_tablename+".nbase) and ");
				ss.append(" salaryhistory.a0100="+this.gz_tablename+".a0100 "+history_condWhl+" and upper(salaryhistory.userflag)='"+userFlag.toUpperCase()+"'  )");
				this.filterWhl=ss.toString();
				this.filterWhl2=history_condWhl+" and upper(salaryhistory.userflag)='"+userFlag.toUpperCase()+"'";
				
				LazyDynaBean abean=getGzExtendLog(String.valueOf(this.salaryid),bosdate,count,userFlag);
				String isRedo=(String)abean.get("isRedo");
				String sp_flag=(String)abean.get("sp_flag");
				ArrayList _setlist=(ArrayList)setlist.clone();
				ArrayList _typelist=(ArrayList)typelist.clone();
				String _items=items;
				String _uptypes=uptypes;
				
				if(!"06".equals(sp_flag)&& "1".equals(isRedo)) //解决薪资重发问题
				{
					_typelist=new ArrayList();
					for(int j=0;j<_setlist.size();j++)
					{
						 				
						String _setid=(String)_setlist.get(j);
						if(_setid.charAt(0)!='A'|| "A00".equalsIgnoreCase(_setid)|| "A01".equalsIgnoreCase(_setid))
						{
							_typelist.add("2");
						} else if("1".equals(typelist.get(j)))
							_typelist.add("3");//新增改为更新(薪资重发时) 解决重发时插入人导致归属次数重复 zhanghua 2018-2-28

						else
							_typelist.add("0"); 
					}
					_items="";
					_uptypes="";
				} 
				submitGzData2_history(_setlist,_typelist,_items,_uptypes,userFlag,sp_flag,isRedo); 
			
			} 
		 
		}
		catch(Exception ex)
		{
				ex.printStackTrace();
				bflag=false;
				throw GeneralExceptionHandler.Handle(ex);	
		}
		return bflag;	
	}
	
	
	/**
	 * 获得用户薪资发放处理状态和是否属于重发数据
	 * @param salaryid
	 * @param bosdate
	 * @param count
	 * @param username
	 * @return
	 * @throws GeneralException
	 */
	public LazyDynaBean getGzExtendLog(String salaryid,String bosdate,String count,String username)throws GeneralException
	{
		LazyDynaBean abean=new LazyDynaBean();
		try
		{  
				ContentDAO dao=new ContentDAO(this.conn);
				String[] temps=bosdate.split("\\.");  
				StringBuffer buf=new StringBuffer("select * from gz_extend_log where ");
				buf.append(" "+Sql_switcher.year("a00z2")+"="+temps[0]);
				buf.append(" and "+Sql_switcher.month("a00z2")+"="+Integer.parseInt(temps[1])); 
				buf.append(" and  a00z3="+count+" and salaryid="+salaryid);	
				buf.append(" and lower(username)='"+username.toLowerCase()+"'");
				
				RowSet rowSet=dao.search(buf.toString().toLowerCase());
				if(rowSet.next())
				{
					int isRedo=rowSet.getInt("isRedo");
					abean.set("sp_flag",rowSet.getString("sp_flag"));
					if(isRedo==1)
						abean.set("isRedo","1");
					else
						abean.set("isRedo","0");
				}
				rowSet.close();
			 
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return abean;
	}
	
	 
	
	
//	/**
//	 * 薪资数据提交
//	 * @param setlist				需要归档提交的数据集列表
//	 * @param typelist				数据集提交类型列表
//	 * @param items					更新指标集
//	 * @param uptypes				更新方式
//	 * @return
//	 * @throws GeneralException
//	 */
//	public boolean submitGzData2(ArrayList setlist,ArrayList typelist,String items,String uptypes,String userName)throws GeneralException
//	{
//		boolean bflag=true;
//		try
//		{
//			ContentDAO dao=new ContentDAO(this.conn);
//			/**求得对应子集的指标串,比如AXXX1,AXXX0*/
//			ArrayList updatelist=getUpdateFields(setlist);
//			/**薪资表数据->薪资历史表中去*/
//		//	submitDataInHistory();
//			/**薪资表数据->档案库*/
//			HashMap itemUptype=getItemUpdateType(items,uptypes);
//
//			submitDataInArchive_2(setlist,updatelist,typelist,itemUptype);
//
//			/**薪资表数据->薪资历史表中去*/
//			submitDataInHistory();
//
//			/**处理薪资补发情况*/
//			updateBF();
//			/**设置个税明表记录状态*/
//			setTaxflag();
//			/**把薪资发放记录表中起草状态的记录置为结束状态06*/
//			/** 修改工资数据表中记录状态 */
//			updateSalaryDataStatus("06");
//			StringBuffer buf=new StringBuffer();
//
//
//			/**
//			 * =01 正在处理
//			 * =05 执行状态
//			 * =06 结束状态（薪资数据已提交）
//			 */
//			RowSet rowSet=dao.search("select count(*) from "+this.gz_tablename+" where ( sp_flag<>'06' or sp_flag is null )");
//			int num=0;
//			if(rowSet.next())
//				num=rowSet.getInt(1);
//			if(num==0)
//			{
//				buf.append("update gz_extend_log set sp_flag='06',isredo=0 where ( sp_flag='01' or sp_flag='05' ) and salaryid=? and upper(username)=?");
//				ArrayList paralist=new ArrayList();
//				paralist.add(String.valueOf(this.salaryid));
//				paralist.add(userName.toUpperCase());
//				dao.update(buf.toString(),paralist);
//			}
//
//		}
//		catch(Exception ex)
//		{
//			ex.printStackTrace();
//			bflag=false;
//			throw GeneralExceptionHandler.Handle(ex);
//		}
//		return bflag;
//	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 将符合发放日期的薪资历史数据 提交到 档案库中（修改档案库中对应的数据）
	 * @param bosdate 业务日期(发放日期)
	 * @param count   发放次数
	 * @return
	 */
	public boolean submitGzDataFromHistory(String bosdate,String count,String[] records)throws GeneralException
	{
		boolean bflag=true;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			ArrayList setList=getSubmitTypeList();
			/** 取得数据集的年月标识 */
			HashMap setChangeflagMap=getFieldSetChangeflag(setList);
			LazyDynaBean payDateBean=getSalaryPayDate(bosdate,count);
			/** 薪资历史数据表取数条件 sql */
			String history_condWhl=getWhlConditionSql(payDateBean);
			String history_condWhl2=history_condWhl;
			/**取得人员库前缀列表*/
			String dbpres=this.templatevo.getString("cbase");
 
			ArrayList _salaryItemList=getSalaryItemList();
			ArrayList salaryItemList=new ArrayList();
			String subNoPriv=getLprogramAttri("subNoPriv",SalaryLProgramBo.CONFIRM_TYPE);
			if(subNoPriv==null||subNoPriv.trim().length()==0)
				subNoPriv="0";
			
			for(int e=0;e<_salaryItemList.size();e++)
			{
				LazyDynaBean itemBean=(LazyDynaBean)_salaryItemList.get(e);
				String a_setid=(String)itemBean.get("fieldsetid");
				String a_itemid=(String)itemBean.get("itemid"); 
				FieldItem fielditem=DataDictionary.getFieldItem(a_itemid.toLowerCase());
				
				if(fielditem!=null&&a_setid.charAt(0)=='A'&&!"A00".equalsIgnoreCase(a_setid)&&!"A0100".equalsIgnoreCase(a_itemid))
				{
					if("0".equalsIgnoreCase(fielditem.getUseflag()))
						continue;
					if("0".equals(subNoPriv))
					{
						if(!"2".equalsIgnoreCase(this.userview.analyseFieldPriv(a_itemid.toLowerCase())))
							continue;
					} 
				}
				salaryItemList.add(itemBean);
			}
				  
 
			/**应用库前缀*/
			String[] dbarr=StringUtils.split(dbpres, ",");
			DbWizard dbw=new DbWizard(this.conn);
			LazyDynaBean setBean=null;
			Calendar d=Calendar.getInstance();
			for(int i=0;i<dbarr.length;i++)
			{
				
				StringBuffer buf2=new StringBuffer("");
				StringBuffer buf3=new StringBuffer("");
				HashMap a0100Map=new HashMap();
				for(int f=0;f<records.length;f++)
				{
					if(records[f].length()>0)
					{
						String[] temp=records[f].split("/");
						if(temp[1].trim().equalsIgnoreCase(dbarr[i]))
						{
							
							d.setTimeInMillis(Long.parseLong(temp[2]));
							buf2.append(" or (A0100='"+temp[0]+"'  and "+Sql_switcher.year("A00Z0")+"="+d.get(Calendar.YEAR)+"  and "+Sql_switcher.month("A00Z0")+"="+(d.get(Calendar.MONTH)+1)+" and A00z1="+temp[3]+" )");
							if(a0100Map.get(temp[0])==null)
							{
								buf3.append(" or (A0100='"+temp[0]+"'  and "+Sql_switcher.year("A00Z0")+"="+d.get(Calendar.YEAR)+"  and "+Sql_switcher.month("A00Z0")+"="+(d.get(Calendar.MONTH)+1)+" and A00z1="+temp[3]+" )");
								a0100Map.put(temp[0],"1");
							}
							//	buf2.append(" and a0100='"+temp[0]+"' and   ");
						}
					}
				}
				if(buf2.length()>0)
				{
					history_condWhl=" and ("+buf2.substring(3)+") and salaryid="+this.salaryid+" "+history_condWhl;
					history_condWhl2=" and ("+buf3.substring(3)+") and salaryid="+this.salaryid+" "+history_condWhl2;
				}
				else
				{
					history_condWhl+=" and 1=2";
					history_condWhl2+=" and 1=2";
				}
				
				for(int j=0;j<setList.size();j++)
				{
					setBean=(LazyDynaBean)setList.get(j);
					String setid=(String)setBean.get("setid");
					String type=(String)setBean.get("type");
					//更新主集中的数据
					if("A01".equalsIgnoreCase(setid))
					{
						if("2".equalsIgnoreCase(type))//如果设置为不更新，则不再更新a01  zhanghua 2017-8-2
							continue;
						for(int e=0;e<salaryItemList.size();e++)
						{
							LazyDynaBean itemBean=(LazyDynaBean)salaryItemList.get(e);
							String a_setid=(String)itemBean.get("fieldsetid");
							String a_itemid=(String)itemBean.get("itemid");
							if("A01".equals(a_setid))
							{
								if("A0100".equalsIgnoreCase(a_itemid))
									continue;
								StringBuffer sql=new StringBuffer("update "+dbarr[i]+"a01 set "+a_itemid+"=(select "+a_itemid);
								 sql.append(" from salaryhistory where upper(nbase)='"+dbarr[i].toUpperCase()+"' "+history_condWhl2+" and salaryhistory.a0100="+dbarr[i]+"a01.a0100 ) ");
								 sql.append(" where a0100 in (select a0100 from salaryhistory where upper(nbase)='"+dbarr[i].toUpperCase()+"' "+history_condWhl2+" )");								 
								 dao.update(sql.toString());
							}
						}
					}
					else
						updateSubSetData(dbarr,setid,dao,salaryItemList,setChangeflagMap,i,history_condWhl);
				}
			}
	
		}
		catch(Exception ex)
		{
				ex.printStackTrace();
				bflag=false;
				throw GeneralExceptionHandler.Handle(ex);	
		}
		return bflag;	
	}
	
	
	/**
	 * 更新子集中的数据
	 * @param dbarr
	 * @param setid
	 * @param dao
	 * @param salaryItemList
	 * @param setChangeflagMap
	 * @param i
	 * @param history_condWhl
	 */
	public void updateSubSetData(String[] dbarr,String setid,ContentDAO dao,ArrayList salaryItemList,HashMap setChangeflagMap,int i,String history_condWhl)
	{
		DbSecurityImpl dbS = new DbSecurityImpl();
		try
		{
			String strNow=Sql_switcher.sqlNow();
			PreparedStatement pst=null;
			String sql = null;
			String tableName=dbarr[i]+setid;
			/** 子集是否有发放日期 */
			boolean is_payDate=isPayDateFlag(tableName,setid);
			//boolean is_payDate=false;  
			RowSet rowSet=dao.search("select max(i9999) i9999,a0100 from  "+tableName+"  where a0100 in (select a0100 from salaryhistory where upper(nbase)='"+dbarr[i].toUpperCase()+"' "+history_condWhl+" )  group by a0100 ");
			HashMap aToi9999_map=new HashMap();
			while(rowSet.next())
			{
				aToi9999_map.put(rowSet.getString(2),rowSet.getString(1));
			}
			StringBuffer sql_up=new StringBuffer("");
			StringBuffer sql_select=new StringBuffer("");
			int num=0;
			for(int e=0;e<salaryItemList.size();e++)
			{
				LazyDynaBean itemBean=(LazyDynaBean)salaryItemList.get(e);
				String a_setid=(String)itemBean.get("fieldsetid");
				String a_itemid=(String)itemBean.get("itemid");
				if(setid.equalsIgnoreCase(a_setid))
				{
					num++;
					sql_up.append(","+a_itemid+"=?");
					sql_select.append(","+a_itemid);
				}
			}
			
			if(is_payDate)
			{
				sql_up.append(",a00z2=?,a00z3=?");
				sql_select.append(",a00z2,a00z3");
				num+=2;
			}
			
			if(sql_up.length()>0)
			{
			
				if("0".equals(setChangeflagMap.get(setid.toLowerCase()))){
					sql= "update "+tableName+" set "+sql_up.substring(1)+" where a0100=? and i9999=? ";
					pst=this.conn.prepareStatement(sql);
				}else{
					sql="update "+tableName+" set "+sql_up.substring(1)+" where "+Sql_switcher.year(setid+"Z0")+"=? and  "+Sql_switcher.month(setid+"Z0")+"=? and "+Sql_switcher.day(setid+"Z0")+"=? and "+setid+"Z1=? and a0100=?";
					pst=this.conn.prepareStatement(sql);
				}	
				//	System.out.println("select "+sql_select.substring(1)+",a0100,a00z0,a00z1 from salaryhistory where  upper(nbase)='"+dbarr[i].toUpperCase()+"' "+history_condWhl);
					rowSet=dao.search("select "+sql_select.substring(1)+",a0100,a00z0,a00z1 from salaryhistory where  upper(nbase)='"+dbarr[i].toUpperCase()+"' "+history_condWhl);
					RowSet rowSet2=null;
					while(rowSet.next())
					{
						int index=0;
						for(int a=0;a<salaryItemList.size();a++)
						{
							LazyDynaBean itemBean=(LazyDynaBean)salaryItemList.get(a);
							String a_setid=(String)itemBean.get("fieldsetid");
							String a_itemid=(String)itemBean.get("itemid");
							String a_itemtype=(String)itemBean.get("itemtype");
							String a_decwidth=(String)itemBean.get("decwidth");
							if(setid.equalsIgnoreCase(a_setid))
							{
								index++;
								setPreparedStatementValue(pst,rowSet,a_itemtype,a_decwidth,a_itemid,index);
							}
						}
						if(is_payDate)
						{
							pst.setDate(++index,rowSet.getDate("a00z2"));
							pst.setInt(++index,rowSet.getInt("a00z3"));
						}
						
						
						if("0".equals(setChangeflagMap.get(setid.toLowerCase()))){
							pst.setString(num+1,rowSet.getString("a0100"));
						
							if(aToi9999_map.get(rowSet.getString("a0100"))==null)
							{
								String strIns=",createtime,createusername";
								String strvalue=","+strNow+",'"+this.userview.getUserName()+"'";
								StringBuffer buf=new StringBuffer("");
								buf.append("insert into ");
								buf.append(tableName);
								buf.append("(A0100,I9999");
								buf.append(strIns);
								buf.append(") values ('"+rowSet.getString("a0100")+"',1"+strvalue+") ");
								dao.update(buf.toString());
								aToi9999_map.put(rowSet.getString("a0100"), "1");
							//	continue;
							}
							pst.setInt(num+2,Integer.parseInt((String)aToi9999_map.get(rowSet.getString("a0100"))));
						}else{
							Date a00z0=rowSet.getDate("a00z0");
							int a00z1=rowSet.getInt("a00z1");
							Calendar d=Calendar.getInstance();
							d.setTimeInMillis(a00z0.getTime());
							
							
							String _sql="select * from "+tableName+" where a0100='"+rowSet.getString("a0100")+"' and ";
							_sql+=Sql_switcher.year(setid+"Z0")+"="+d.get(Calendar.YEAR)+" and  "+Sql_switcher.month(setid+"Z0")+"="+(d.get(Calendar.MONTH)+1);
							_sql+=" and "+setid+"Z1="+a00z1;
							rowSet2=dao.search(_sql);
							boolean flag=true;
							if(rowSet2.next())
							{
								flag=false;
							}
							if(flag)
							{
								StringBuffer buf=new StringBuffer("");
								String strIns=",createtime,createusername,"+setid+"Z0,"+setid+"Z1";
								String strvalue=","+strNow+",'"+this.userview.getUserName()+"'";
								
								if(aToi9999_map.get(rowSet.getString("a0100"))!=null)
								{
									buf.append("insert into "+tableName+" (A0100,I9999"+strIns+") "); 
									buf.append(" select a1.a0100,a2.i9999+1"+strvalue+",a1.a00z0,a1.a00z1 from ");
									buf.append(" (select a0100,a00z1,a00z0 from salaryhistory where salaryid="+this.salaryid+" and a0100='"+rowSet.getString("a0100")+"' and upper(nbase)='"+dbarr[i].toUpperCase()+"' ");
									buf.append(" and "+Sql_switcher.year("A00Z0")+"="+d.get(Calendar.YEAR)+" and  "+Sql_switcher.month("A00Z0")+"="+(d.get(Calendar.MONTH)+1)+"  and A00Z1="+a00z1+"  ) a1, ");
									buf.append("( select a0100,i9999 from "+tableName+" a where a.i9999=(select max(b.i9999) from "+tableName+" b where a.a0100=b.a0100 ) ) a2 ");
									buf.append("where a1.a0100=a2.a0100 ");
									dao.update(buf.toString());
						 		}
								else
								{
									 
									buf.append("insert into ");
									buf.append(tableName);
									buf.append("(A0100,I9999,createtime,createusername,"+setid+"Z1"); 
									buf.append(") values ('"+rowSet.getString("a0100")+"',1"+strvalue+","+a00z1+") ");
									dao.update(buf.toString());
									buf.setLength(0);
									buf.append("update "+tableName+" set "+setid+"Z0=(select a00z0 from salaryhistory  where "+tableName+".a0100=salaryhistory.a0100 and salaryid="+this.salaryid+" and a0100='"+rowSet.getString("a0100")+"' and upper(nbase)='"+dbarr[i].toUpperCase()+"' ");
									buf.append(" and "+Sql_switcher.year("A00Z0")+"="+d.get(Calendar.YEAR)+" and  "+Sql_switcher.month("A00Z0")+"="+(d.get(Calendar.MONTH)+1)+"  and A00Z1="+a00z1+"  ) where ");
									buf.append(" a0100='"+rowSet.getString("a0100")+"' and i9999=1 ");
									dao.update(buf.toString());
								
								}
							}
							pst.setInt(num+1,d.get(Calendar.YEAR));
							pst.setInt(num+2,d.get(Calendar.MONTH)+1);
							pst.setInt(num+3,d.get(Calendar.DATE));
							pst.setInt(num+4,a00z1);
							pst.setString(num+5,rowSet.getString("a0100"));
						}
						pst.addBatch();
					}
 
					// 打开Wallet
					dbS.open(conn, sql);
					pst.executeBatch();
					
					if(rowSet!=null)
						rowSet.close();
					if(pst!=null)
						pst.close();
					if(rowSet2!=null)
						rowSet2.close();
			}
  
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try {
				// 关闭Wallet
				dbS.close(conn);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
	
	
	/**
	 * 判断是否有 a00z2,a00z3 并且字段类型也符合 日期/整数 类型
	 * @param tableName
	 * @return
	 */
	public boolean isPayDateFlag(String tableName,String setid)
	{
		boolean flag=false;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RecordVo vo=new RecordVo(tableName.toLowerCase());
			if(vo.hasAttribute("a00z2")&&vo.hasAttribute("a00z3"))
			{
				boolean is_a00z2=false;
				boolean is_a00z3=false;
				RowSet rowSet=dao.search("select * from fielditem where useflag=1 and ((fieldsetid='"+setid+"' and itemid='a00z2' ) or (fieldsetid='"+setid+"' and itemid='a00z3' ) )");
				while(rowSet.next())
				{
					if("a00z2".equalsIgnoreCase(rowSet.getString("itemid"))&& "D".equals(rowSet.getString("itemtype")))
						is_a00z2=true;
					if("a00z3".equalsIgnoreCase(rowSet.getString("itemid"))&& "N".equals(rowSet.getString("itemtype"))&&rowSet.getInt("decimalwidth")==0)
						is_a00z3=true;
				}
				if(is_a00z2&&is_a00z3)
					flag=true;
				rowSet.close();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
	
	
	public void setPreparedStatementValue(PreparedStatement pst,RowSet rowSet,String a_itemtype,String a_decwidth,String a_itemid,int index)
	{
		try
		{
			if("A".equals(a_itemtype))
			{
				pst.setString(index,rowSet.getString(a_itemid));
			}
			else if("N".equals(a_itemtype))
			{
				if("0".equals(a_decwidth))
				{
					pst.setInt(index,rowSet.getInt(a_itemid));
				}
				else
				{
					pst.setDouble(index,rowSet.getDouble(a_itemid));
				}
			}
			else if("D".equals(a_itemtype))
			{
				pst.setDate(index,rowSet.getDate(a_itemid));
			}
			else if("M".equals(a_itemtype))
			{
				pst.setString(index,Sql_switcher.readMemo(rowSet,a_itemid));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * 薪资数据提交
	 * @param setlist				需要归档提交的数据集列表
	 * @param typelist				数据集提交类型列表
	 * @param items					更新指标集
	 * @param uptypes				更新方式
	 * @return 
	 * @throws GeneralException
	 */
	public boolean submitGzData(ArrayList setlist,ArrayList typelist,String items,String uptypes)throws GeneralException
	{
		boolean bflag=true;
		try
		{	
			/**求得对应子集的指标串,比如AXXX1,AXXX0*/
			ArrayList updatelist=getUpdateFields(setlist);
			/**薪资表数据->薪资历史表中去*/
		//	submitDataInHistory();
			
			/**薪资表数据->档案库*/
			HashMap itemUptype=getItemUpdateType(items,uptypes);
			submitDataInArchive_2(setlist,updatelist,typelist,itemUptype);			
			/**薪资表数据->薪资历史表中去*/
		//	submitDataInHistory();
			
			/**处理薪资补发情况*/
			updateBF();
			/**设置个税明表记录状态*/
			setTaxflag();
			/** 修改工资数据表中记录状态 */
			updateSalaryDataStatus("06");
			/**把薪资发放记录表中起草状态的记录置为结束状态06*/
			setExtendLogState();
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			bflag=false;
			throw GeneralExceptionHandler.Handle(ex);			
		}
		return bflag;		
	}
	
	
	/**
	 * 取得指标的更新方式
	 * @param items
	 * @param uptypes
	 * @return
	 */
	public HashMap getItemUpdateType(String items,String uptypes)
	{
		HashMap map=new HashMap();
		if(items.length()>0)
		{
			items = items.replaceAll("／", "/");
			uptypes = uptypes.replaceAll("／", "/");
			String[] item_arr=items.split("/");
			String[] uptype_arr=uptypes.split("/");
			for(int i=0;i<item_arr.length;i++)
			{
				if(item_arr[i].trim().length()>0)
				{
					map.put(item_arr[i].toLowerCase(),uptype_arr[i]);
				}
			}
		}
		return map;
	}
	
	
	/**
	 * 修改工资数据表中记录状态
	 * @param status
	 */
	public void updateSalaryDataStatus(String status)
	{
		try
		{
			DbWizard dbw=new DbWizard(this.conn);
		//	System.out.println("update "+gz_tablename+" set sp_flag='"+status+"' where 1=1 "+this.filterWhl);
			String sql0="update "+gz_tablename+" set sp_flag='"+status+"' ";
			if(this.manager!=null&&this.manager.trim().length()>0)
				sql0+=",sp_flag2='"+status+"' ";
			sql0+=" where 1=1 "+this.filterWhl;
			dbw.execute(sql0);
		//	if(this.manager!=null&&this.manager.trim().length()>0)
	//			dbw.execute("update "+gz_tablename+" set sp_flag2='"+status+"' where 1=1 "+this.filterWhl);
			StringBuffer sql=new StringBuffer("update salaryhistory   set sp_flag='"+status+"'");
			if(this.manager!=null&&this.manager.trim().length()>0)
				sql.append(",sp_flag2='"+status+"'");
			sql.append(" where salaryid="+this.salaryid+" and  exists ( select null from "+this.gz_tablename);
			sql.append(" where "+this.gz_tablename+".sp_flag='"+status+"'  and salaryhistory.a0100="+this.gz_tablename+".a0100   and salaryhistory.a00z0="+this.gz_tablename+".a00z0 ");
			sql.append(" and salaryhistory.a00z1="+this.gz_tablename+".a00z1 and "+this.gz_tablename+".userflag=salaryhistory.userflag ");
			sql.append(" and  upper(salaryhistory.nbase)=upper("+this.gz_tablename+".nbase) )");
			
			//sql.append(" where  exists  (select * from "+this.gz_tablename); 
			//sql.append(" where a0100=salaryhistory.a0100 "+this.filterWhl+" and  upper(nbase)=upper(salaryhistory.nbase) and  a00z0=salaryhistory.a00z0 ");
			//sql.append(" and  a00z1=salaryhistory.a00z1 and userflag=salaryhistory.userflag ) and salaryid="+this.salaryid);
			dbw.execute(sql.toString());
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
       /**   
     * @Title: isAllowEditSubdata   
     * @Description:  薪资发放 是否允许提交后更改数据；具有 “允许提交后更改数据”   
     * @param @return
     * @author wangrd
     * @param @throws GeneralException 
     * @return boolean    
     * @throws   
    */
    public boolean isAllowEditSubdata() throws GeneralException
    {
        boolean bAllowEditSubdata=false;   
        try
        {   
            String allowEditSubdata=getLprogramAttri("allow_edit_subdata",SalaryLProgramBo.CONFIRM_TYPE);            
            if(allowEditSubdata==null||allowEditSubdata.trim().length()==0)
                allowEditSubdata="0";  
           
            if ("1".equals(allowEditSubdata)){//允许提交后更改数据 且 具有提交权限,功能授权暂时不区分薪资保险，同薪资发放前台权限              
                /*
                if (this.userview.hasTheFunction("3250305")|| this.userview.hasTheFunction("3271305")
                        || this.userview.hasTheFunction("3240305")|| this.userview.hasTheFunction("3270305")){
                                        
                }
                */
                bAllowEditSubdata=true;   
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
                           
        }
        return bAllowEditSubdata;   
    }    
    
    /**   
     * @Title: isAllowEditSubdata_Sp   
     * @Description: 薪资审批 是否允许提交后更改数据；具有 “允许提交后更改数据”且 具有提交权限才可更改数据,功能授权暂时不区分薪资保险，同薪资发放前台权限    
     * @param @param gz_module
     * @param @return
     * @author wangrd
     * @param @throws GeneralException 
     * @return boolean    
     * @throws   
    */
    public boolean isAllowEditSubdata_Sp(String gz_module) throws GeneralException
    {
        boolean bAllowEditSubdata=false;   
        try {
            String allowEditSubdata=getLprogramAttri("allow_edit_subdata",SalaryLProgramBo.CONFIRM_TYPE);            
            if(allowEditSubdata==null||allowEditSubdata.trim().length()==0)
                allowEditSubdata="0";  
           
            if ("1".equals(allowEditSubdata)){//允许提交后更改数据 且 具有提交权限,功能授权暂时不区分薪资保险，同薪资发放前台权限              
                if ("1".equals(gz_module)){ //保险                  
                    if (this.userview.hasTheFunction("3250305")|| this.userview.hasTheFunction("3271305")){
                        bAllowEditSubdata=true;                        
                    }
                } else {//薪资
                    if (this.userview.hasTheFunction("3240305")|| this.userview.hasTheFunction("3270305")){
                        bAllowEditSubdata=true;                         
                    }
                }
             }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
                           
        }
        return bAllowEditSubdata;   
    } 
	/**
	 * 批量修改某个薪资项目数据
	 * @param itemid     目标指标
	 * @param formula    计算公式
	 * @param cond       修改条件
	 * @param whl        过滤条件
	 * @return
	 * @throws GeneralException
	 */
	public boolean batchUpdateItem(String itemid,String formula,String cond,String whl)throws GeneralException
	{
		boolean bflag=true;
		boolean _flag=true;//判断异常在哪出现的，zhaoxg add 2013-10-15
		try
		{	
			YksjParser yp=null;
			/**加载一次*/
			//if(fldvarlist.size()==0)
			//{
			 	fldvarlist.clear();
				fldvarlist.addAll(this.getMidVariableList());
				fldvarlist.addAll(this.getGzFieldList());
			//}			
			StringBuffer strwhere=new StringBuffer();
			if(cond.length()==0|| "undefined".equalsIgnoreCase(cond))
				strwhere.append(" where 1=1");
			else
			{
				strwhere.append(" where ");
				yp = new YksjParser( this.userview ,fldvarlist,
						YksjParser.forNormal, YksjParser.LOGIC,YksjParser.forPerson , "Ht", "");
				yp.run_where(cond);
				String strfilter=yp.getSQL();				
				strwhere.append(strfilter);
			}
			/**需要审批*/ 
			if(isApprove())
			{
				strwhere.append(" and sp_flag in('01','07')");
			}	
			else {
                // 控制已提交的数据是否能批量修改  wangrd  2013-11-14             
                if (!isAllowEditSubdata()) {
                    strwhere.append(" and sp_flag in('01','07')");   
                }
			}
			//共享薪资类别，其他操作人员引入数据 
			if(this.manager.length()>0&&!this.userview.getUserName().equalsIgnoreCase(this.manager))
			{
				strwhere.append(" and sp_flag2 in ('01','07')");
			}
			
			
			FieldItem fielditem=DataDictionary.getFieldItem(itemid);
			String datatype="A";
			if(fielditem==null)
			{
				if("A00Z0".equalsIgnoreCase(itemid))
					datatype="D";
				if("A00Z1".equalsIgnoreCase(itemid))
					datatype="N";
			}
			else
				datatype=fielditem.getItemtype();

			String strexpr="";
			if("D".equals(datatype)&&(formula.split("\\.").length==3||formula.split("-").length==3))
			{
				if(formula.charAt(0)!='#'||formula.charAt(formula.length()-1)!='#')
					throw GeneralExceptionHandler.Handle(new Exception("日期格式不正确,格式为 #yyyy-mm-dd#!"));	
				formula=formula.replaceAll("#","");
				String[] temp=null;
				if(formula.split("\\.").length==3)
					temp=formula.split("\\.");
				else
					temp=formula.split("-");
				Calendar d=Calendar.getInstance();
				try
				{
					d.set(Calendar.YEAR,Integer.parseInt(temp[0]));
					d.set(Calendar.MONTH,Integer.parseInt(temp[1])-1);
					d.set(Calendar.DATE,Integer.parseInt(temp[2]));
				}
				catch(Exception ee)
				{
					throw GeneralExceptionHandler.Handle(new Exception("日期格式不正确!"));		
				}
				
				String aflag=this.ctrlparam.getValue(SalaryCtrlParamBo.PRIV_MODE,"flag");  // 人员范围权限过滤标志  1：有  
				//共享薪资类别，其他操作人员引入数据 (dengcan 2008/6/26 )
				if(this.manager.length()>0&&!this.userview.getUserName().equalsIgnoreCase(this.manager)&&aflag!=null&& "1".equals(aflag))
				{
					String dbpres=this.templatevo.getString("cbase");
					//应用库前缀
					String[] dbarr=StringUtils.split(dbpres, ",");
					for(int i=0;i<dbarr.length;i++)
					{
						String pre=dbarr[i];
						StringBuffer buf=new StringBuffer();
						buf.append("update ");
						buf.append(this.gz_tablename);
						buf.append(" set ");
						buf.append(itemid);
						buf.append("=?");
						buf.append(strwhere.toString());
						if(whl!=null&&whl.trim().length()>0)
							buf.append(" "+whl);
						
						buf.append(" and upper(nbase)='"+pre.toUpperCase()+"'");
						//权限过滤
						
						if("1".equals(this.controlByUnitcode))
						{
							String whl_str=getWhlByUnits();
							if(whl_str.length()>0)
							{
								buf.append(whl_str);
							}
						}
						else
						{
							String whereIN=InfoUtils.getWhereINSql(this.userview,pre);
							whereIN="select a0100 "+whereIN;	
							buf.append(" and a0100 in ( "+whereIN+" )");
						}
						
						java.sql.Date date=new java.sql.Date(d.getTimeInMillis());
						ContentDAO dao=new ContentDAO(this.conn);
						ArrayList paramList = new ArrayList();
						paramList.add(date);
						dao.update(buf.toString(),paramList);
					}
				}
				else
				{
					StringBuffer buf=new StringBuffer();
					buf.append("update ");
					buf.append(this.gz_tablename);
					buf.append(" set ");
					buf.append(itemid);
					buf.append("=?");
					buf.append(strwhere.toString());
					if(whl!=null&&whl.trim().length()>0)
						buf.append(" "+whl);
					java.sql.Date date=new java.sql.Date(d.getTimeInMillis());
					ContentDAO dao=new ContentDAO(this.conn);
					ArrayList paramList = new ArrayList();
					paramList.add(date);
					dao.update(buf.toString(),paramList);
				}
				
			}
			else
			{
				/*
				if(datatype.equalsIgnoreCase("A"))
				{
					if(formula.length()>1&&formula.substring(0,1).equals("\"")&&formula.substring(formula.length()-1,formula.length()).equals("\""))
					{

					}
					else
					{
						bflag=false;
						throw GeneralExceptionHandler.Handle(new Exception("字符格式不正确,需用\"号括起来!"));	
					}
				}
				if(datatype.equalsIgnoreCase("N"))
				{
					if(!formula.trim().matches("[-]?[\\d]+[.]?[\\d]*"))
					{	
						bflag=false;
						throw GeneralExceptionHandler.Handle(new Exception("数字格式不正确!"));
					}
				}
				*/



				//----------------------------------
				ContentDAO dao=new ContentDAO(this.conn);
				RowSet rowSet=null;
				int length = 0;
				int decimalwidth = 0;
				if(!"A00Z0".equalsIgnoreCase(itemid)&&!"A00Z1".equalsIgnoreCase(itemid)){
					length=fielditem.getItemlength();
					decimalwidth = fielditem.getDecimalwidth();
				}else{
					length = 15;
					decimalwidth = 0;
				}
			 
				if("N".equalsIgnoreCase(datatype)&&isNum(formula)){
					if(formula.indexOf(".")!=-1){
						String[] temp=formula.split("\\.");
						if(decimalwidth<temp[1].length()){
							 _flag=false;						 
							 throw GeneralExceptionHandler.Handle(new Exception("输入内容的小数位不要长于"+decimalwidth+"位！"));
						}
						 if(temp[0].length()>length){
							 _flag=false;
							 throw GeneralExceptionHandler.Handle(new Exception("输入内容的整数位不要长于"+length+"个字符！"));
						 }
					}else{
						 if(formula.length()>length){
							 _flag=false;
							 throw GeneralExceptionHandler.Handle(new Exception("输入内容的长度不要长于"+length+"个字符！"));
						 }
					}

				}else if("A".equalsIgnoreCase(datatype)){
					 /*
					 if(len(formula)>length&&formula!=null&&!formula.equalsIgnoreCase("null")){
						 _flag=false;
						 throw GeneralExceptionHandler.Handle(new Exception("输入内容的长度不要长于"+length+"个字符！"));
					 }*/
				}
				//--------------------------------------
				
				yp=new YksjParser( this.userview ,fldvarlist,
						YksjParser.forNormal, getDataType(datatype),YksjParser.forPerson , "Ht", "");
				yp.run(formula,this.conn,cond,this.gz_tablename);
				/**单表计算*/
				strexpr=yp.getSQL();	
				/**为空不计算*/
				if(strexpr.trim().length()==0)
					return true;
				if(("a00z0".equalsIgnoreCase(itemid)|| "a00z1".equalsIgnoreCase(itemid))&& "NULL".equalsIgnoreCase(strexpr.trim()))
					return true;

				String aflag=this.ctrlparam.getValue(SalaryCtrlParamBo.PRIV_MODE,"flag");  // 人员范围权限过滤标志  1：有  
				//共享薪资类别，其他操作人员引入数据 (dengcan 2008/6/26 )
				if(this.manager.length()>0&&!this.userview.getUserName().equalsIgnoreCase(this.manager)&&aflag!=null&& "1".equals(aflag))
				{
					String dbpres=this.templatevo.getString("cbase");
					//应用库前缀
					String[] dbarr=StringUtils.split(dbpres, ",");
					StringBuffer buf1=new StringBuffer(""); 
					for(int i=0;i<dbarr.length;i++)
					{
						String pre=dbarr[i];
						StringBuffer buf=new StringBuffer();
						buf.append("update ");
						buf.append(this.gz_tablename);
						buf.append(" set ");
						buf.append(itemid);
						buf.append("=");
						buf.append(strexpr);
						 
						
						buf1.setLength(0);
						buf1.append(strwhere.toString());
						if(whl!=null&&whl.trim().length()>0)
							buf1.append(" "+whl);
						
						buf1.append(" and upper(nbase)='"+pre.toUpperCase()+"'");
						//权限过滤
						if("1".equals(this.controlByUnitcode))
						{
							String whl_str=getWhlByUnits();
							if(whl_str.length()>0)
							{
								buf1.append(whl_str);
							}
						}
						else
						{
							String whereIN=InfoUtils.getWhereINSql(this.userview,pre);
							whereIN="select a0100 "+whereIN;	
							buf1.append(" and a0100 in ( "+whereIN+" )");
						}
						
						
						if("A".equalsIgnoreCase(datatype))
						{  
							rowSet=dao.search("select "+strexpr+","+Sql_switcher.length(strexpr)+" a from "+this.gz_tablename+" "+buf1.toString()+" order by a desc");
							if(rowSet.next())
							{
								String _value=rowSet.getString(1)!=null?rowSet.getString(1):"";
								if(len(_value)>length)
								{
									 _flag=false;
									 throw GeneralExceptionHandler.Handle(new Exception("输入内容的长度不要长于"+length+"个字符！"));
								}
							} 
						}
//						ContentDAO dao=new ContentDAO(this.conn);
						dao.update(buf.toString()+buf1.toString());
						
					}
				}
				else
				{
					StringBuffer buf=new StringBuffer();
					StringBuffer buf1=new StringBuffer(""); 
					
					
					
					buf.append("update ");
					buf.append(this.gz_tablename);
					buf.append(" set ");
					buf.append(itemid);
					buf.append("=");
					buf.append(strexpr);
					buf1.append(strwhere.toString());
					if(whl!=null&&whl.trim().length()>0)
						buf1.append(" "+whl);
					
					if("A".equalsIgnoreCase(datatype))
					{  
						rowSet=dao.search("select "+strexpr+","+Sql_switcher.length(strexpr)+" a from "+this.gz_tablename+" "+buf1.toString()+" order by a desc");
						if(rowSet.next())
						{
							String _value=rowSet.getString(1)!=null?rowSet.getString(1):"";
							if(len(_value)>length)
							{
								 _flag=false;
								 throw GeneralExceptionHandler.Handle(new Exception("输入内容的长度不要长于"+length+"个字符！"));
							}
						} 
					}
					
					
					dao.update(buf.toString()+buf1.toString());
				}
				
				
			}
		}
		catch(Exception ex)
		{
		    ex.printStackTrace();
			bflag=false;
			if(_flag){
				if(ex.toString().indexOf("唯一")!=-1){
					throw GeneralExceptionHandler.Handle(new Exception("同一个人有多条薪资数据，不能执行归属次数的批量修改！"));
				}else{
					throw GeneralExceptionHandler.Handle(new Exception("输入内容格式不正确！"));
				}
				
			}else{
				throw GeneralExceptionHandler.Handle(ex);
			}
						
		}
		return bflag;	
	}
	/**
	 * 判断输入的是不是数字
	 * @param str
	 * @return
	 */
	public static boolean isNum(String str){
		return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");	
	}
	/**
	 * 汉字占2个字符，其他的占一个字符
	 * @param s
	 * @return
	 */
	public int len(String s) { 
		int l = 0; 
		String[] a = s.split(""); 
		for (int i=2;i<a.length-1;i++) {  //去掉前后的双引号，字符型
			if (a[i].charAt(0)<299) {   
				l++;  
			} else {   
				l+=2;  
			} 
		} 
		return l;
	}
	/**
	 * 查找薪资当前处于的状态
	 * @return
	 */
	public boolean findGzState(String state)
	{
		boolean bflag=false;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer buf=new StringBuffer();
			buf.append("select sp_flag from gz_extend_log ");
			buf.append(" where salaryid=");
			buf.append(this.salaryid);
			buf.append(" and ");
			buf.append(" upper(username)='");
			buf.append(this.userview.getUserName().toUpperCase());
			buf.append("'");			
			buf.append(" and A00Z3=");
			buf.append(currcount);
			buf.append(" and A00Z2=");
			buf.append(Sql_switcher.dateValue(currym));
			RowSet rset=dao.search(buf.toString());
			if(rset.next())
			{
				String sp_flag=rset.getString("sp_flag");
				if(sp_flag.equalsIgnoreCase(state)) 
					bflag=true;
			}
			rset.close();
		}
		catch(Exception ex)
		{
			bflag=false;
			ex.printStackTrace();
		}
		return bflag;
		
	}	
	
	/**
	 * 删除当前为起草状态的发放纪录
	 */
	private void deleteCurrentDraftRecord()
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer buf=new StringBuffer();
			buf.append("delete from gz_extend_log ");
			buf.append(" where salaryid=");
			buf.append(this.salaryid);
			buf.append(" and ");
			buf.append(" upper(username)='");
			buf.append(this.userview.getUserName().toUpperCase());
			buf.append("'");			
			buf.append(" and A00Z3=");
			buf.append(currcount);
			buf.append(" and A00Z2=");
			buf.append(Sql_switcher.dateValue(currym));
			buf.append(" and sp_flag='01'");
			dao.delete(buf.toString(), new ArrayList());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	/**
	 * 删除当前为起草状态的发放纪录
	 */
	private int DeleteCurrentDraftRecord()
	{
		int i = 0;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer buf=new StringBuffer();
			buf.append("delete from gz_extend_log ");
			buf.append(" where salaryid=");
			buf.append(this.salaryid);
			buf.append(" and ");
			buf.append(" upper(username)='");
			buf.append(this.userview.getUserName().toUpperCase());
			buf.append("'");			
			buf.append(" and A00Z3=");
			buf.append(currcount);
			buf.append(" and A00Z2=");
			buf.append(Sql_switcher.dateValue(currym));
			buf.append(" and sp_flag='01'");
			i = dao.delete(buf.toString(), new ArrayList());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return i;
	}
	
	
	/***
	 * 是否正处在审批状态中
	 * @return
	 */
	public boolean isApproving()
	{
		/**查找当前薪资表是否处于审批状态中*/
	//	return findGzState("05");
		boolean flag=true;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet	rowSet=dao.search("select count(a0100) from "+this.gz_tablename+" where sp_flag is not null and ( sp_flag='03' or sp_flag='02' ) ");
			if(rowSet.next())
			{
					if(rowSet.getInt(1)==0)
					{
								flag=false;
					}
			}
				
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
		
	}
	
	/***
	 * 是否正处在结束状态(针对当前用户操作的记录)
	 * @return
	 */
	public boolean isHistory()
	{
		/**当前薪资表中的发放日期和次数*/
		getCurrentGzTableYearMonthCount();
		/**查找当前薪资表是否处于结束状态中*/
		return findGzState("06");
	}

	
	//薪资类别（当前发放日期）是否有处于结束状态的记录 
	public boolean isHistory2()
	{
		/**当前薪资表中的发放日期和次数*/
		getCurrentGzTableYearMonthCount();
		/**查找当前薪资表是否处于结束状态中*/
		
		boolean bflag=false;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer buf=new StringBuffer();
			buf.append("select sp_flag from gz_extend_log ");
			buf.append(" where salaryid=");
			buf.append(this.salaryid);		
			buf.append(" and A00Z3=");
			buf.append(currcount);
			buf.append(" and A00Z2=");
			buf.append(Sql_switcher.dateValue(currym));
			buf.append(" and sp_flag='06'");
			buf.append(" and lower(username)='"+this.userview.getUserName().toLowerCase()+"' ");
			
			RowSet rset=dao.search(buf.toString());
			if(rset.next())
			{
					bflag=true;
			}
			rset.close();
		}
		catch(Exception ex)
		{
			bflag=false;
			ex.printStackTrace();
		}
		return bflag;
	}
	
	
	/**
	 * 重置业务日期
	 * @param year		年份
	 * @param month		月份
	 * @param count		次数
	 * @return
	 * @throws GeneralException
	 */
	public boolean reLoadHistoryData(String year,String month,String count)throws GeneralException
	{
		boolean bflag=true;
		String ym=year+"-"+month+"-01";
		
		StringBuffer buf=new StringBuffer();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			/**取得当前处理日期和次数*/
			//getYearMonthCount("01");
			HashMap map=getMaxYearMonthCount();
			/**需要审批的话,则分析当前处理的业务日期是否处理审批状态*/
			currym=(String)map.get("ym");
			currcount=(String)map.get("count");
			if(isApprove())
			{
				if(isApproving())
					throw new GeneralException("本期薪资发放业务正处于审批当中!");
			}

			
			//如果没有结束的记录 删除历史表中的数据
			int _count=0;
			RowSet	rowSet=dao.search("select count(a0100) from "+this.gz_tablename+" where sp_flag is not null and  sp_flag='06'  ");
			if(rowSet.next())
				_count=rowSet.getInt(1);
			if(_count==0)
			{
				String[] atemps=this.gz_tablename.toLowerCase().split("_salary_");
				
				StringBuffer sql=new StringBuffer("delete from salaryhistory   where exists (select null from ");
				sql.append(this.gz_tablename+" a where a.a00z0=salaryhistory.a00z0 and a.a00z1=salaryhistory.a00z1 and upper(a.nbase)=upper(salaryhistory.nbase) and ");
				sql.append(" a.a0100=salaryhistory.a0100  ) and salaryhistory.salaryid="+this.salaryid);
				sql.append(" and lower(userflag)='");  //20100323
				sql.append(atemps[0].toLowerCase());
				sql.append("'");
				
				dao.update(sql.toString());
			}
			//---------------------重置之前先删除税表  gz_tax_mx zhaoxg add 2014-12-15-------------
			StringBuffer sql = new StringBuffer();
	    	sql.append("delete from gz_tax_mx where salaryid="+this.salaryid+" and exists (select null from "+this.gz_tablename+" salary where gz_tax_mx.A00Z0=salary.A00Z0 and gz_tax_mx.A00Z1=salary.A00Z1 and gz_tax_mx.A0100=salary.A0100 ");
	    	sql.append(" and gz_tax_mx.NBASE=salary.NBASE and gz_tax_mx.flag=0");
	    	if(manager.length()>0&&!this.userview.getUserName().equalsIgnoreCase(manager))
	    		sql.append(" and ( lower(gz_tax_mx.userflag)='"+manager.toLowerCase()+"' or userflag is null )");
	    	else
	    		sql.append(" and ( lower(gz_tax_mx.userflag)='"+this.userview.getUserName().toLowerCase()+"' or userflag is null )");
	    	sql.append(" )");
	    	dao.delete(sql.toString(), new ArrayList());
	    	//-----------------------------end----------------------------
			/**清空当前薪资表中的数据*/
			buf.setLength(0);
			buf.append("delete from ");
			buf.append(this.gz_tablename);
			dao.update(buf.toString());
			
			
			/**分析当前输入的日期是否为历史数据*/
		//	if(isHaveHistroyLog(ym,count,"06"))	
			{
				deleteDrafeOutRecord();
				isHaveHistroyLog2(ym,count);  //重置业务日期，不能将原先的发放纪录状态改变
				
				/**所有项目*/
				StringBuffer fields=new StringBuffer();
				for(int i=0;i<gzitemlist.size();i++)
				{
					GzItemVo itemvo=(GzItemVo)gzitemlist.get(i);
					if(itemvo.getInitflag()==4)
						continue;
					if(fields.length()==0)
						fields.append(itemvo.getFldname());
					else
					{
						fields.append(",");
						fields.append(itemvo.getFldname());
					}
				}
				buf.setLength(0);
				buf.append("insert into ");
				buf.append(this.gz_tablename);
				buf.append("(add_flag,userflag,sp_flag,");
				if(isApprove()||StringUtils.isNotBlank(this.manager))
					buf.append("Appprocess,");
				if(this.manager!=null&&this.manager.trim().length()>0)
					buf.append("sp_flag2,");
				buf.append(fields.toString());
				buf.append(") select 1,userflag");
				buf.append(",sp_flag,");
				if(isApprove()||StringUtils.isNotBlank(this.manager))
					buf.append("Appprocess,");
				if(this.manager!=null&&this.manager.trim().length()>0)
					buf.append("'06',");
				buf.append(fields.toString());
				buf.append(" from salaryhistory where");
				buf.append(" A00Z2=");
				buf.append(Sql_switcher.dateValue(ym));
				buf.append(" and A00Z3=");
				buf.append(count);
				buf.append(" and salaryid=");
				buf.append(this.salaryid);
			//	if(!this.userview.isSuper_admin())
				{
					buf.append(" and lower(userflag)='");
					buf.append(this.userview.getUserName().toLowerCase());
					buf.append("'");
				}
				dao.update(buf.toString());
			}
			/**清空发放日志表起草状态的记录*/
			//deleteDrafeOutRecord();
			
			
			
			//写入薪资发放数据的映射表
			if (Sql_switcher.searchDbServer()==1){			    
			    dao.update("delete from salary_mapping where salaryid="+this.salaryid+" and USERFLAG='"+this.userview.getUserName().toLowerCase()+"'");
			}
			else {
			    dao.update("delete from salary_mapping where salaryid="+this.salaryid+" and lower(USERFLAG)='"+this.userview.getUserName().toLowerCase()+"'");
			}    
			dao.update("insert into salary_mapping (a0100,nbase,a00z0,a00z1,salaryid,userflag) select a0100,nbase,a00z0,a00z1,"+this.salaryid+",'"+this.userview.getUserName().toLowerCase()+"' from "+this.gz_tablename);
			 
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			bflag=false;
			throw GeneralExceptionHandler.Handle(ex);			
		}
		return bflag;		
	}
	
	
	/**
	 * 分析当前日期是否为历史记录(已发放过),
	 * @param ymd		年月日
	 * @param count     次数
	 * @return
	 */
	private boolean isHaveHistroyLog2(String ymd,String count)
	{
		boolean bflag=true;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer buf=new StringBuffer();
			ArrayList paralist=new ArrayList();
			buf.append("select id from gz_extend_log where salaryid=?");
			buf.append(" and upper(username)=?");
			buf.append(" and A00Z3=? and A00Z2=");
			buf.append(Sql_switcher.dateValue(ymd));
			paralist.add(String.valueOf(this.salaryid));			
			if(this.manager.length()==0)
				paralist.add(this.userview.getUserName().toUpperCase());
			else
				paralist.add(this.manager.toUpperCase());			
			paralist.add(count);
			RowSet rset=dao.search(buf.toString(),paralist);
			if(rset.next())
			{
				bflag=true;
			}
			else
			{
				//appendExtendLog(ymd,count);	
				DbNameBo.appendExtendLog(this.userview.getUserName(),this.salaryid,ymd,count,this.conn);
			
				
				
				bflag=false;			
			}
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
	 * 删除发放日志表中的当前用户起草\执行状态的记录
	 */
	private void deleteDrafeOutRecord()
	{
		StringBuffer buf=new StringBuffer();
		ArrayList paralist=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			buf.append("delete from gz_extend_log where salaryid=?");
			buf.append(" and upper(username)=?");
			buf.append(" and ( sp_flag='01' or sp_flag='05' ) ");
			paralist.add(String.valueOf(this.salaryid));
			if(this.manager.length()==0)
				paralist.add(this.userview.getUserName().toUpperCase());
			else
				paralist.add(this.manager.toUpperCase());			
			dao.update(buf.toString(),paralist);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	/**
	 * 分析当前日期是否为历史记录(已发放过),
	 * @param ymd		年月日
	 * @param count     次数
	 * @param state     状态 01 05 06 （起草|审批中|结束）
	 * @return
	 */
	private boolean isHaveHistroyLog(String ymd,String count,String state)
	{
		boolean bflag=true;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer buf=new StringBuffer();
			ArrayList paralist=new ArrayList();
			buf.append("select id from gz_extend_log where salaryid=?");
			buf.append(" and upper(username)=?");
		//	buf.append(" and sp_flag='06'");
			buf.append(" and A00Z3=? and A00Z2=");
			buf.append(Sql_switcher.dateValue(ymd));
			paralist.add(String.valueOf(this.salaryid));
			paralist.add(this.userview.getUserName());
			paralist.add(count);
			RowSet rset=dao.search(buf.toString(),paralist);
			if(rset.next())
			{
				bflag=true;
				/**把指定业务日期或次数的记录设置成起草状态*/
				RecordVo vo=new RecordVo("gz_extend_log");
				vo.setInt("id", rset.getInt("id"));
				
				
				vo.setString("sp_flag", state/*"01"*/);
				dao.updateValueObject(vo);
			}
			else
			{
				//appendExtendLog(ymd,count);	
				DbNameBo.appendExtendLog(this.userview.getUserName(),this.salaryid,ymd,count,this.conn);
				bflag=false;			
			}
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
	 * 是否有发放历史记录
	 * @return
	 */
	private boolean isHaveHistroyLog()
	{
		boolean bflag=true;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer buf=new StringBuffer();
			ArrayList paralist=new ArrayList();
			buf.append("select  id from gz_extend_log where salaryid="+this.salaryid);
			buf.append(" and upper(username)='"+this.userview.getUserName().toUpperCase()+"'");
			buf.append(" and A00Z2=");
			buf.append(Sql_switcher.dateValue(currym));
			buf.append(" and A00Z3=");
			buf.append(currcount);
			buf.append(" and sp_flag='06'");
			RowSet rset=dao.search(buf.toString());
			if(rset.next())
			{
				bflag=true;
			//	this.currym=PubFunc.FormatDate(rset.getDate("A00Z2"), "yyyy-MM-dd");
			//	this.currcount=rset.getString("A00Z3");
				/**把最后一次数据设置为当前处理状态*/
				RecordVo vo=new RecordVo("gz_extend_log");
				vo.setInt("id", rset.getInt("id"));
				vo.setString("sp_flag", "01");
				vo.setInt("isredo", 1);
				dao.updateValueObject(vo);
			}
			else
				bflag=false;
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
	 * 提交薪资表中的数据至历史数据表中去
	 * @return
	 * @throws GeneralException
	 */
	public boolean submitDataInHistory(String flow_flag)throws GeneralException
	{
		boolean bflag=true;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			if(!"1".equalsIgnoreCase(flow_flag))  //不需要审批
			{
				/**所有项目*/
				
				StringBuffer fields=new StringBuffer();
				StringBuffer buf=new StringBuffer();
				for(int i=0;i<gzitemlist.size();i++)
				{
					GzItemVo itemvo=(GzItemVo)gzitemlist.get(i);
					if(itemvo.getInitflag()==4)
						continue;
					if(fields.length()==0)
						fields.append(itemvo.getFldname());
					else
					{
						fields.append(",");
						fields.append(itemvo.getFldname());
					}
				}
				/**删除当前用户处理的历史记录*/
				deleteHistory();
				deleteHistory2();
				/**把数据归档到薪资历史表中去*/
				buf.setLength(0);
				buf.append("insert into salaryhistory");
				buf.append("(userflag,salaryid,sp_flag,Appprocess,");
				buf.append(fields.toString());
				buf.append(") select userflag,");
				buf.append(this.salaryid);
				buf.append(",sp_flag,Appprocess,");
				buf.append(fields.toString());
				buf.append(" from ");
				buf.append(this.gz_tablename);
				dao.update(buf.toString());
			}
			else
			{
				StringBuffer sql=new StringBuffer("update salaryhistory set sp_flag='06' where ");
				sql.append("  exists (select null from "+this.gz_tablename);
				sql.append(" where  salaryhistory.a00z0="+this.gz_tablename+".a00z0 and salaryhistory.a00z1="+this.gz_tablename+".a00z1 ");
				sql.append(" and upper(salaryhistory.nbase)=upper("+this.gz_tablename+".nbase) and salaryhistory.a0100="+this.gz_tablename+".a0100 ");
				sql.append(") and  salaryid="+this.salaryid);
				dao.update(sql.toString());
				
				deleteHistory2();
			}
			
		}
		catch(Exception ex)
		{
			bflag=false;
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);				
		}
		return bflag;
	}
	
	
	
	
	
	
	
	/**
	 * 提交薪资表中的数据至历史数据表中去
	 * @return
	 * @throws GeneralException
	 */
	public boolean submitDataInHistory()throws GeneralException
	{
		boolean bflag=true;
		try
		{
			String flow_flag=this.ctrlparam.getValue(SalaryCtrlParamBo.FLOW_CTRL, "flag");
		//	ContentDAO dao=new ContentDAO(this.conn);
			DbWizard dbw=new DbWizard(this.conn);
			if(!"1".equalsIgnoreCase(flow_flag))  //不需要审批
			{
				/**所有项目*/
				
				StringBuffer fields=new StringBuffer();
				StringBuffer buf=new StringBuffer();
				for(int i=0;i<gzitemlist.size();i++)
				{
					GzItemVo itemvo=(GzItemVo)gzitemlist.get(i);
					if(itemvo.getInitflag()==4)
						continue;
					if(fields.length()==0)
						fields.append(itemvo.getFldname());
					else
					{
						fields.append(",");
						fields.append(itemvo.getFldname());
					}
				}
				/**删除当前用户处理的历史记录*/
				deleteHistory();
			//	deleteHistory2();
				/**把数据归档到薪资历史表中去*/
				buf.setLength(0);
				buf.append("insert into salaryhistory");
				buf.append("(userflag,salaryid,sp_flag,e0122_o,b0110_o,dbid,");//Appprocess
				buf.append(fields.toString());
				buf.append(") select userflag,");
				buf.append(this.salaryid);
				buf.append(",sp_flag,e0122_o,b0110_o,dbid,");//Appprocess
				buf.append(fields.toString());
				buf.append(" from ");
				buf.append(this.gz_tablename+" where 1=1 "+filterWhl);
				dbw.execute(buf.toString());
			}
			else
			{
			/*	StringBuffer sql=new StringBuffer("update salaryhistory set sp_flag='06' where ");
				sql.append("  exists (select null from "+this.gz_tablename);
				sql.append(" where  salaryhistory.a00z0="+this.gz_tablename+".a00z0 and salaryhistory.a00z1="+this.gz_tablename+".a00z1 ");
				sql.append(" and upper(salaryhistory.nbase)=upper("+this.gz_tablename+".nbase) and salaryhistory.a0100="+this.gz_tablename+".a0100 ");
				sql.append("  "+filterWhl+"  ) and  salaryid="+this.salaryid);
				dbw.execute(sql.toString());
				
				deleteHistory2();*/
			}
			
		}
		catch(Exception ex)
		{
			bflag=false;
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);				
		}
		return bflag;
	}
	/**
	 * 分析档案是否存在当前业务日期的记录
	 * @param destsetid
	 * @param setid
	 * @param a0100
	 * @param chgflag
	 * @param strym
	 * @param strcount
	 * @return I9999,返回值如果为-1,则表示没有当前业务日期的记录
	 */
	private int isHaveCurrentDateRecord(String destsetid,String setid,String a0100,int chgflag,String strym,String strcount)
	{
		int i9999=-1;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String year=strym.substring(0, 4);
			String month=strym.substring(5, 7);
			String axxz0=setid+"Z0";
			String axxz1=setid+"Z1";
			StringBuffer buf=new StringBuffer();
			buf.append("select I9999 from ");
			buf.append(destsetid);
			buf.append(" where a0100=? and ");
			buf.append(Sql_switcher.year(axxz0));
			buf.append("=");
			buf.append(year);
			buf.append(" and ");
			buf.append(axxz1);
			buf.append("=");
			buf.append(strcount);			
			if(chgflag==1)
			{
				buf.append(" and ");
				buf.append(Sql_switcher.month(axxz0));
				buf.append("=");
				buf.append(month);
			}
			ArrayList paralist=new ArrayList();
			paralist.add(a0100);
			RowSet rset=dao.search(buf.toString(),paralist);
			if(rset.next())
				i9999=rset.getInt("I9999");
			rset.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return i9999;
	}
	
	/**
	 * 分析档案是否存在当前业务日期的记录
	 * @param destsetid
	 * @param setid
	 * @param a0100
	 * @param chgflag
	 * @param strym
	 * @param strcount
	 * @return I9999,返回值如果为-1,则表示没有当前业务日期的记录
	 */
	private int isHaveCurrentDateRecord_royalty(String destsetid,String setid,String a0100,ArrayList relationFieldList,HashMap values)
	{
		int i9999=-1;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn); 
			StringBuffer buf=new StringBuffer();
			buf.append("select I9999 from ");
			buf.append(destsetid);
			buf.append(" where a0100=?   ");
			FieldItem aitem=null;
			for(int i=0;i<relationFieldList.size();i++)
			{
				aitem=(FieldItem)relationFieldList.get(i); 
				if(!"null".equalsIgnoreCase((String)values.get(aitem.getItemid())))
				{
						if("D".equalsIgnoreCase(aitem.getItemtype()))
							buf.append(" and "+Sql_switcher.isnull(Sql_switcher.dateToChar(aitem.getItemid(),"YYYY-MM-DD"),"''")+"='"+(String)values.get(aitem.getItemid())+"' ");
						else
							buf.append(" and "+Sql_switcher.isnull(aitem.getItemid(),"''")+"='"+(String)values.get(aitem.getItemid())+"' "); 
				}
				else
				{
						if(Sql_switcher.searchDbServer()==Constant.ORACEL)
						{
							buf.append(" and "+ aitem.getItemid() +" is null "); 
						}
						else
							buf.append(" and "+Sql_switcher.isnull(aitem.getItemid(),"''")+"='' "); 
				}
					 
			} 
			ArrayList paralist=new ArrayList();
			paralist.add(a0100);
			RowSet rset=dao.search(buf.toString(),paralist);
			if(rset.next())
				i9999=rset.getInt("I9999");
			rset.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return i9999;
	}
	
	
	

	/**
	 * 自动创建一条和上条记录一样的记录
	 * @param destname			目标数据表
	 * @param setid				数据集，前面不带应用库前缀
	 * @param a0100				人员库编号
	 * @param fields			指标列表串，以逗号分开
	 * @param nclearzero		清零标志 =1处理清零 =2不处理清零
	 * @param chgflag			按月变化标志
	 * @param strym				年月
	 * @param strcount			次数
	 * @throws GeneralException
	 */
	private int autoAppendRecord_royalty(String destname,String setid,String a0100  ,int chgflag,String strym,String strcount,ArrayList relationList,HashMap values)throws GeneralException
	{
		String strNow=null;
		String axxz0=setid+"Z0";
		String axxz1=setid+"Z1";
		StringBuffer buf=new StringBuffer();
		strNow=Sql_switcher.sqlNow();
		String strIns=",createtime,createusername";
		String strvalue=","+strNow+",'"+this.userview.getUserName()+"'";
		
		FieldItem aitem=null;
		for(int i=0;i<relationList.size();i++)
		{
			aitem=(FieldItem)relationList.get(i);
			if(!"null".equals((String)values.get(aitem.getItemid().toLowerCase())))
			{
				strIns+=","+aitem.getItemid();
				String _str=(String)values.get(aitem.getItemid().toLowerCase());
				if("D".equalsIgnoreCase(aitem.getItemtype()))
				{
					strvalue+=","+Sql_switcher.dateValue(_str);
				}
				else
				{
					strvalue+=",'"+_str+"'";
				}
			}
		} 
		int i9999=-1;
		 
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			i9999=DbNameBo.getPrimaryKey(destname, "I9999", " where a0100='"+a0100+"'", conn);
			 
			if(chgflag==0)
			{
					buf.append("insert into ");
					buf.append(destname);
					buf.append("(A0100,I9999");
					buf.append(strIns);
					buf.append(") values ('");
					buf.append(a0100);
					buf.append("',");
					buf.append(i9999);
					buf.append(strvalue);
					buf.append(")");
			}
			else
			{
					buf.append("insert into ");
					buf.append(destname);
					buf.append("(A0100,I9999");
					buf.append(strIns);
					buf.append(",");
					buf.append(axxz0);
					buf.append(",");
					buf.append(axxz1);
					buf.append(") values ('");
					buf.append(a0100);
					buf.append("',");
					buf.append(i9999);
					buf.append(strvalue);
					buf.append(",");
					buf.append(Sql_switcher.dateValue(strym));
					buf.append(",");
					buf.append(strcount);
					buf.append(")");
			}//子集类型 	
			dao.update(buf.toString());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);				
		}
		return i9999;		
	}
	
	
	
	/**
	 * 自动创建一条和上条记录一样的记录
	 * @param destname			目标数据表
	 * @param setid				数据集，前面不带应用库前缀
	 * @param a0100				人员库编号
	 * @param fields			指标列表串，以逗号分开
	 * @param nclearzero		清零标志 =1处理清零 =2不处理清零
	 * @param chgflag			按月变化标志
	 * @param strym				年月
	 * @param strcount			次数
	 * @throws GeneralException
	 */
	private int autoAppendRecord(String destname,String setid,String a0100,String fields,int nclearzero,int chgflag,String strym,String strcount)throws GeneralException
	{
		String strNow=null;
		String axxz0=setid+"Z0";
		String axxz1=setid+"Z1";
		StringBuffer buf=new StringBuffer();
		strNow=Sql_switcher.sqlNow();
		String strIns=",createtime,createusername";
		String strvalue=","+strNow+",'"+this.userview.getUserName()+"'";
		int i9999=-1;
		/** sql语句有错误 ,edit by dengcan  */
		if(fields.length()>0&& ",".equals(fields.substring(fields.length()-1)))
			fields=fields.substring(0,fields.length()-1);
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			i9999=DbNameBo.getPrimaryKey(destname, "I9999", " where a0100='"+a0100+"'", conn);
			if(nclearzero==1)//清零
			{
				if(chgflag==0)
				{
					buf.append("insert into ");
					buf.append(destname);
					buf.append("(A0100,I9999");
					buf.append(strIns);
					buf.append(") values ('");
					buf.append(a0100);
					buf.append("',");
					buf.append(i9999);
					buf.append(strvalue);
					buf.append(")");
				}
				else
				{
					buf.append("insert into ");
					buf.append(destname);
					buf.append("(A0100,I9999");
					buf.append(strIns);
					buf.append(",");
					buf.append(axxz0);
					buf.append(",");
					buf.append(axxz1);
					buf.append(") values ('");
					buf.append(a0100);
					buf.append("',");
					buf.append(i9999);
					buf.append(strvalue);
					buf.append(",");
					buf.append(Sql_switcher.dateValue(strym));
					buf.append(",");
					buf.append(strcount);
					buf.append(")");
				}//子集类型
			}
			else
			{
					if(i9999==1)//此人子集无记录
					{
						buf.append("insert into ");
						buf.append(destname);
						buf.append("(a0100,I9999");
						buf.append(strIns);
						if(chgflag!=0) //按月或按年变化子集
						{
							buf.append(",");								
							buf.append(axxz0);
							buf.append(",");
							buf.append(axxz1);
						}
						buf.append(") values ('");
						buf.append(a0100);
						buf.append("',");
						buf.append(i9999);
						buf.append(strvalue);
						if(chgflag!=0)
						{
							buf.append(",");								
							buf.append(Sql_switcher.dateValue(strym));
							buf.append(",");
							buf.append(strcount);
						}
						buf.append(")");							
					}
					else
					{
						buf.append("insert into ");
						buf.append(destname);
						buf.append("(a0100,I9999");
						buf.append(strIns);
						if(fields.length()>0)
						{
							buf.append(",");
							buf.append(fields);
						}
						if(chgflag!=0) //按月或按年变化子集
						{
							buf.append(",");								
							buf.append(axxz0);
							buf.append(",");
							buf.append(axxz1);
						}							
						buf.append(") select a0100,");
						buf.append(i9999);
						buf.append(strvalue);
						if(fields.length()>0)
						{
							buf.append(",");
							buf.append(fields);
						}
						if(chgflag!=0)
						{
							buf.append(",");								
							buf.append(Sql_switcher.dateValue(strym));
							buf.append(",");
							buf.append(strcount);
						}							
						buf.append(" from ");
						buf.append(destname);
						buf.append(" ");
						/** sql语句有错误 ，注释掉 by dengcan  */
					//	buf.append(destname);   
					//	buf.append(" as");
						buf.append(" where I9999=");
						buf.append(i9999-1);
						buf.append(" and a0100='");
						buf.append(a0100);
						buf.append("'");
					}
			}			
			dao.update(buf.toString());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);				
		}
		return i9999;		
	}
	
	
	/**
	 * 求更新串
	 * @param destname		目标表
	 * @param srcname		源表
	 * @param updates		更新串
	 * @param chgflag		子集变化标识
	 * @return
	 */
	private String getUpdateSQL_royalty(String destname,String srcname,String updates,int chgflag,HashMap itemUptype)
	{
		StringBuffer buf=new StringBuffer();
		String[] fieldarr=StringUtils.split(updates,",");
		for(int i=0;i<fieldarr.length;i++)
		{
			buf.append(destname);
			buf.append(".");
			buf.append(fieldarr[i]);
			buf.append("=");
			buf.append(srcname);
			buf.append(".");
			buf.append(fieldarr[i]);
			buf.append("`");
			
		}//for i loop end.
		buf.append("modusername='");
		buf.append(this.userview.getUserName());
		buf.append("'`modtime=");
		buf.append(Sql_switcher.sqlNow());
		buf.append("`");
		if(buf.length()>0)
			buf.setLength(buf.length()-1);
		return buf.toString();
	}
	
	
	
	/**
	 * 求更新串
	 * @param destname		目标表
	 * @param srcname		源表
	 * @param updates		更新串
	 * @param chgflag		子集变化标识
	 * @return
	 */
	private String getUpdateSQL(String destname,String srcname,String updates,int chgflag,HashMap itemUptype)
	{
		StringBuffer buf=new StringBuffer();
		String[] fieldarr=StringUtils.split(updates,",");
		for(int i=0;i<fieldarr.length;i++)
		{
			buf.append(destname);
			buf.append(".");
			buf.append(fieldarr[i]);
			buf.append("=");
			
			/** 累加更新 */
			if(itemUptype.get(fieldarr[i].toLowerCase())!=null&& "0".equals((String)itemUptype.get(fieldarr[i].toLowerCase())))
			{
				buf.append("(");
				buf.append(Sql_switcher.isnull(destname+"."+fieldarr[i], "0"));
				buf.append("+");
				buf.append(Sql_switcher.isnull(srcname+"."+fieldarr[i], "0"));
				buf.append(")`");
				
			}
			else
			{
				buf.append(srcname);
				buf.append(".");
				buf.append(fieldarr[i]);
				buf.append("`");
			}
		}//for i loop end.
		buf.append("modusername='");
		buf.append(this.userview.getUserName());
		buf.append("'`modtime=");
		buf.append(Sql_switcher.sqlNow());
		buf.append("`");		
		/**按年或月变化子集*/
		if(chgflag==1||chgflag==2)
		{
			String setid=destname.substring(3);			
			String axxz0=setid+"Z0";
			String axxz1=setid+"Z1";
			buf.append(destname);
			buf.append(".");
			buf.append(axxz0);
			buf.append("=");
			buf.append(srcname);
			buf.append(".a00z0");
			buf.append("`");
			buf.append(destname);
			buf.append(".");
			buf.append(axxz1);
			buf.append("=");
			buf.append(srcname);
			buf.append(".a00z1");
			buf.append("`");
		}
		if(buf.length()>0)
			buf.setLength(buf.length()-1);
		return buf.toString();
	}
//	/**
//	 * 薪资数据提交至档案库中去...
//	 * @param setlist			子集列表
//	 * @param updatelist		更新指标串列表
//	 * @param typelist			数据提交方式列表
//	 * @return
//	 * @throws GeneralException
//	 */
//	private boolean submitDataInArchive(ArrayList setlist,ArrayList updatelist,ArrayList typelist,HashMap itemUptype)throws GeneralException
//	{
//		boolean bflag=true;
//		try
//		{
//			String a0100=null,cbase=null,strym=null,strcount=null,dessetid=null,supdate=null;
//			ContentDAO dao=new ContentDAO(this.conn);
//			int ni9999=-1;
//			int add_flag=0;
//			RowSet rset=null;
//			StringBuffer buf=new StringBuffer();
//			/**取得人员库前缀列表*/
//			String dbpres=this.templatevo.getString("cbase");
//			/**应用库前缀*/
//			String[] dbarr=StringUtils.split(dbpres, ",");
//			DbWizard dbw=new DbWizard(this.conn);
//
//			//判断提交的薪资类别是否包含了年月变化子集,并且为新增方式
//			boolean isYMaddSet=isymChangeSet(setlist,typelist);
//
//			for(int i=0;i<dbarr.length;i++)
//			{
//				buf.setLength(0);
//				buf.append("select a0100,nbase,a00z0,a00z1,add_flag from ");
//				buf.append(this.gz_tablename);
//				buf.append(" where a0100 in (select a0100 from ");
//				buf.append(dbarr[i]);
//				buf.append("A01) and lower(nbase)='");
//				buf.append(dbarr[i].toLowerCase());
//				buf.append("'");
//				rset=dao.search(buf.toString());
//				cbase=dbarr[i];
//
//				ArrayList updateList=new ArrayList();
//				while(rset.next())
//				{
//					int num=0;
//					a0100=rset.getString("a0100");
//					if(rset.getString("add_flag")!=null)
//						add_flag=rset.getInt("add_flag");
//					else
//						add_flag=0;
//
//					//cbase=rset.getString("nbase");
//					strym=PubFunc.FormatDate(rset.getDate("a00z0"), "yyyy-MM-dd");
//					strcount=rset.getString("a00z1");
//
//
//
//					/*查找薪资类别项目涉及各年月变化子集本月最大次数加1的值更新为临时表中对应记录的
//					* 归属次数并将工资数据临时表 中记录的add_flag值置为 1，当记录 add_flag值为1时，按旧的程序处理逻辑。*/
//					if(isYMaddSet&&add_flag==0&&num==0)
//					{
//									int z1=getNewA00z1(setlist,strym,dbarr[i],a0100,typelist);
//									num++;
//									if(Integer.parseInt(strcount)<z1)
//									{
//										synchronousZ1(strym,strcount,z1,a0100,dbarr[i],updateList);
//										strcount=String.valueOf(z1);
//									}
//					}
//
//
//
//					for(int j=0;j<setlist.size();j++)
//					{
//						String setid=(String)setlist.get(j);
//						if(setid.equalsIgnoreCase("A00"))
//							continue;
//						String fields=(String)updatelist.get(j);
//						/**(0,1,2,3)=(更新,新增,不变,更新(薪资重发时))*/
//						String type=(String)typelist.get(j);
//						if(type.equalsIgnoreCase("2"))//当前记录不变
//							continue;
//						/**子集未构库不提交*/
//						FieldSet fieldset=DataDictionary.getFieldSetVo(setid);
//						if(fieldset==null||fieldset.getUseflag().equalsIgnoreCase("0"))
//							continue;
//						/**(0,1,2)=(一般,按月变化,按年变化)*/
//						int nflag=Integer.parseInt(fieldset.getChangeflag());
//						int ntype=Integer.parseInt(type);
//						switch(setid.charAt(0))
//						{
//						case 'A'://人员库
//							if(!setid.equalsIgnoreCase("A01"))//处理非主集
//							{
//								dessetid=dbarr[i]+setid;
//								supdate=(String)updatelist.get(j);
//								switch(ntype)
//								{
//								case 0://更新
//									switch(nflag)
//									{
//									case 1://按月变化
//									case 2://按年变化
//										ni9999=isHaveCurrentDateRecord(dessetid, setid, a0100, nflag, strym, strcount);
//										/**如果没有记录，则自动创建一条记录和上条记录完全一条*/
//										if(ni9999==-1)
//											ni9999=autoAppendRecord(dessetid,setid,a0100,supdate,2,nflag,strym,strcount);
//										break;
//									case 0://一般子集
//										ni9999=DbNameBo.getPrimaryKey(dessetid, "I9999", " where a0100='"+a0100+"'", conn)-1;
//										break;
//									}
//									break;
//								case 1://追加记录
//									switch(nflag)
//									{
//									case 1://按月变化
//									case 2://按年变化
//										ni9999=isHaveCurrentDateRecord(dessetid, setid, a0100, nflag, strym, strcount);
//										/**如果没有记录，则自动创建一条记录和上条记录完全一条*/
//										if(ni9999==-1)
//											ni9999=autoAppendRecord(dessetid,setid,a0100,supdate,1,nflag,strym,strcount);
//										break;
//									case 0://一般子集
//										ni9999=autoAppendRecord(dessetid,setid,a0100,supdate,1,nflag,strym,strcount);
//										break;
//									}
//									break;
//								}//操作方式
//								if(nflag==0)//只有一般变化子集，才一个人一个人提交数据
//								{
//									String value=getUpdateSQL(dessetid,this.gz_tablename,fields,nflag,itemUptype);
//									StringBuffer strSWhere=new StringBuffer();
//									strSWhere.append(dessetid);
//									strSWhere.append(".A0100='");
//									strSWhere.append(a0100);
//									strSWhere.append("' and ");
//									strSWhere.append(dessetid);
//									strSWhere.append(".I9999=");
//									strSWhere.append(ni9999);
//									strSWhere.append(" and lower(");
//									strSWhere.append(this.gz_tablename);
//									strSWhere.append(".NBASE)='");
//									strSWhere.append(cbase.toLowerCase());
//									strSWhere.append("'");
//									StringBuffer strDWhere=new StringBuffer();
//									strDWhere.append(dessetid);
//									strDWhere.append(".A0100='");
//									strDWhere.append(a0100);
//									strDWhere.append("' and ");
//									strDWhere.append(dessetid);
//									strDWhere.append(".I9999=");
//									strDWhere.append(ni9999);
//									dbw.updateRecord(dessetid, gz_tablename,dessetid+".A0100="+gz_tablename+".A0100", value, strSWhere.toString(), strDWhere.toString());
//								}
//							}//if 非主集
//							break;
//						}//子集
//					}//for setlist 数据集loop end.
//				}//for while end.
//
//				//同步 薪资临时表 及 历史表里的a00z1
//				if(updateList.size()>0)
//				{
//					String sql="update "+this.gz_tablename+" set a00z1=?,add_flag=1 where lower(nbase)='"+dbarr[i].toLowerCase()+"' and a0100=? "
//					+" and "+Sql_switcher.year("a00z0")+"=? and "+Sql_switcher.month("a00z0")+"=? and a00z1=? ";
//					String sql2="update salaryhistory  set a00z1=? where lower(nbase)='"+dbarr[i].toLowerCase()+"' and a0100=? "
//					+" and "+Sql_switcher.year("a00z0")+"=? and "+Sql_switcher.month("a00z0")+"=? and a00z1=? and salaryid="+this.salaryid;
//					dao.batchUpdate(sql, updateList);
//					dao.batchUpdate(sql2, updateList);
//					dao.update("update "+this.gz_tablename+" set  add_flag=1 where lower(nbase)='"+dbarr[i].toLowerCase()+"'");
//				}
//
//				for(int j=0;j<setlist.size();j++)
//				{
//					String setid=(String)setlist.get(j);
//					if(setid.equalsIgnoreCase("A00"))
//						continue;
//					/**(0,1,2,3)=(更新,新增,不变,更新(薪资重发时))*/
//					String type=(String)typelist.get(j);
//					if(type.equalsIgnoreCase("2"))//当前记录不变
//						continue;
//					/**子集未构库不提交*/
//					FieldSet fieldset=DataDictionary.getFieldSetVo(setid);
//					if(fieldset==null||fieldset.getUseflag().equalsIgnoreCase("0"))
//						continue;
//					/**(0,1,2)=(一般,按月变化,按年变化)*/
//					int nflag=Integer.parseInt(fieldset.getChangeflag());
//					switch(setid.charAt(0))
//					{
//					case 'A'://人员库
//						if(setid.equalsIgnoreCase("A01"))
//						{
//								updateSQL(gz_tablename,cbase,setid,(String)updatelist.get(j),0,itemUptype);
//						}
//						else//如果子集，则只对按月或年变化的子集进行处理.
//						{
//							if(nflag>0)
//							{
//								updateSQL(gz_tablename,cbase,setid,(String)updatelist.get(j),nflag,itemUptype);
//							}
//						}
//						break;
//					}
//				}
//			}//for i loop end.人员库前缀列表
//			if(rset!=null)
//				rset.close();
//		}
//		catch(Exception ex)
//		{
//			bflag=false;
//			ex.printStackTrace();
//			throw GeneralExceptionHandler.Handle(ex);
//		}
//		return bflag;
//	}

	
	///////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * 批量插入子集纪录
	 */
	public void batchInsertSetRecord_royalty(String destname,String where_str,String where_str2,ArrayList relationfieldList,String cbase)
	{
		StringBuffer buf=new StringBuffer();
		String strNow=Sql_switcher.sqlNow();
		String strIns=",createtime,createusername";
		String strvalue=","+strNow+",'"+this.userview.getUserName()+"'";
		
		String relation_str="";
		String relation_str2="";
		String relation_str3="";
		for(int i=0;i<relationfieldList.size();i++)
		{
			relation_str+=","+(String)relationfieldList.get(i);
			relation_str2+=",c."+(String)relationfieldList.get(i);
			String itemid=(String)relationfieldList.get(i);
			if(Sql_switcher.searchDbServer()==2)
			{
				if("D".equalsIgnoreCase(DataDictionary.getFieldItem(itemid.trim()).getItemtype()))
						relation_str3+=" and "+Sql_switcher.isnull(Sql_switcher.dateToChar("c."+itemid,"YYYY-MM-DD"),"'-'")+"="+Sql_switcher.isnull(Sql_switcher.dateToChar(destname+"."+itemid,"YYYY-MM-DD"),"'-'") ; 
				else
						relation_str3+=" and "+Sql_switcher.isnull("c."+itemid,"'-'")+"="+Sql_switcher.isnull(destname+"."+itemid,"'-'") ; 
			}
			else
				relation_str3+=" and "+Sql_switcher.isnull("c."+(String)relationfieldList.get(i),"''")+"="+Sql_switcher.isnull(destname+"."+(String)relationfieldList.get(i),"''"); 
		 
		}
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			
			String sql_sub="( select c.* from "+this.gz_tablename+" c,"+where_str+" where c.a0100=aa.a0100 and lower(c.NBASE)='"+cbase.toLowerCase()+"' and not exists ( ";
			sql_sub+=" select null from "+destname+" where c.a0100="+destname+".a0100 "+relation_str3+" ) ) ";
			 
			
			buf.append("insert into ");
			buf.append(destname);
			buf.append("(A0100,I9999");
			buf.append(strIns+relation_str);
			buf.append(") ");
			buf.append(" select a.a0100,a.i9999+1 "+strvalue+relation_str2+" from ");
			buf.append(destname+" a,"+sql_sub+" c  where   a.a0100=c.a0100 and a.i9999=(select max(b.i9999) from ");
			buf.append(destname+" b where a.a0100=b.a0100)  ");
			dao.update(buf.toString());
			buf.setLength(0);
			
			buf.append("insert into ");
			buf.append(destname);
			buf.append("(A0100,I9999");
			buf.append(strIns+relation_str);
			buf.append(") ");
			buf.append(" select a.a0100,1 "+strvalue+relation_str2+" from ( ");
			buf.append(where_str2+" ) a,"+this.gz_tablename+" c  where   a.a0100=c.a0100  ");
			buf.append( " and lower(c.NBASE)='"+cbase.toLowerCase()+"' ");
			dao.update(buf.toString());
		 
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	
	/**
	 * 批量插入子集纪录
	 */
	public void batchInsertSetRecord(String destname,String where_str,String where_str2)
	{
		StringBuffer buf=new StringBuffer();
		String strNow=Sql_switcher.sqlNow();
		String strIns=",createtime,createusername";
		String strvalue=","+strNow+",'"+this.userview.getUserName()+"'";
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			buf.append("insert into ");
			buf.append(destname);
			buf.append("(A0100,I9999");
			buf.append(strIns);
			buf.append(") ");
			buf.append(" select a.a0100,a.i9999+1 "+strvalue+" from ");
			buf.append(destname+" a,"+where_str+"  where a.i9999=(select max(b.i9999) from ");
			buf.append(destname+" b where a.a0100=b.a0100) and aa.a0100=a.a0100 ");
			dao.update(buf.toString());
			buf.setLength(0);
			
			buf.append("insert into ");
			buf.append(destname);
			buf.append("(A0100,I9999");
			buf.append(strIns);
			buf.append(") ");
			buf.append(where_str2);
			dao.update(buf.toString());
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**对一个用户同时有多条薪资记录的一般子集数据进行处理*/
	public void dealwithMulRecord(ArrayList setlist,boolean isYMaddSet,ArrayList updatelist,ArrayList typelist,String[] dbarr,int i,DbWizard dbw,HashMap itemUptype)
	{
		String a0100=null,cbase=null,strym=null,strcount=null,dessetid=null,supdate=null;
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			String  royalty_valid=this.ctrlparam.getValue(SalaryCtrlParamBo.ROYALTIES,"valid");
			String royalty_relation_fields=this.ctrlparam.getValue(SalaryCtrlParamBo.ROYALTIES,"relation_fields");
			ArrayList relationfieldList=new ArrayList();
			String royalty_setid=this.ctrlparam.getValue(SalaryCtrlParamBo.ROYALTIES,"setid");
			StringBuffer relationFieldStr=new StringBuffer("");
			String relation_str="";
			 //数据提交入库不判断子集及指标权限
			String subNoPriv=getLprogramAttri("subNoPriv",SalaryLProgramBo.CONFIRM_TYPE);
			if(subNoPriv==null||subNoPriv.trim().length()==0)
		 		subNoPriv="0";
			
			if("1".equalsIgnoreCase(royalty_valid)&&royalty_relation_fields.length()>0)
			{
				String[] temps=royalty_relation_fields.toLowerCase().split(",");
				for(int n=0;n<temps.length;n++)
				{
					if(temps[n].trim().length()>0&&DataDictionary.getFieldItem(temps[n].trim())!=null)
					{ 
						relationfieldList.add(DataDictionary.getFieldItem(temps[n].toLowerCase()));
						relationFieldStr.append(","+temps[n]);
					}
				}
			}  
			cbase=dbarr[i];
		//	String singleRecord_where="select aa.a0100 from  (select count(a0100) c,a0100 from "+this.gz_tablename+" where lower("+this.gz_tablename+".NBASE)='"+cbase.toLowerCase()+"'  "+filterWhl+"  group by a0100  having count(a0100)>1 ) aa";
			String tempName="t#"+this.userview.getUserName()+"_gz";
		//	if(dbw.isExistTable(tempName,false))
			{	  
				 dbw.dropTable(tempName);
			}
		     StringBuffer sql0=new StringBuffer("");
		     if(Sql_switcher.searchDbServer()==2)
		    	 sql0.append("create table "+tempName+" as "); 
		     sql0.append("select aa.a0100 ");
		     if(Sql_switcher.searchDbServer()!=2)
		    	 sql0.append(" into "+tempName);
		     sql0.append(" from  (select count(a0100) c,a0100 from "+this.gz_tablename+" where lower("+this.gz_tablename+".NBASE)='"+cbase.toLowerCase()+"'  "+filterWhl+"  group by a0100  having count(a0100)>1 ) aa");
		     dao.update(sql0.toString());
		     String singleRecord_where="select a0100 from "+tempName;
		     
		     
		     
			
			StringBuffer buf=new StringBuffer("");
			if("1".equals(royalty_valid)&&royalty_relation_fields.length()>0)
				buf.append("select * from ");
			else
				buf.append("select a0100,nbase,a00z0,a00z1,add_flag from ");
			buf.append(this.gz_tablename);
			buf.append(" where a0100 in ("+singleRecord_where+") "+filterWhl+" and exists (select null from "+cbase+"A01 where "+cbase+"A01.a0100="+this.gz_tablename+".a0100 )  AND UPPER("+this.gz_tablename+".nbase)='"+cbase.toUpperCase()+"'");
	 
			RowSet rset=dao.search(buf.toString()+" order by a00z0,a00z1");
 
			ArrayList updateList=new ArrayList();
			int ni9999=-1;
			int add_flag=0;
			SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
			while(rset.next())
			{
				int num=0;
				a0100=rset.getString("a0100");
				if(rset.getString("add_flag")!=null)
					add_flag=rset.getInt("add_flag");
				else
					add_flag=0;
				
				//cbase=rset.getString("nbase");
				strym=PubFunc.FormatDate(rset.getDate("a00z0"), "yyyy-MM-dd");
				strcount=rset.getString("a00z1");
				
				
				
				/*查找薪资类别项目涉及各年月变化子集本月最大次数加1的值更新为临时表中对应记录的
				* 归属次数并将工资数据临时表 中记录的add_flag值置为 1，当记录 add_flag值为1时，按旧的程序处理逻辑。*/
				if(isYMaddSet&&add_flag==0&&num==0)
				{
								int z1=getNewA00z1(setlist,strym,dbarr[i],a0100,typelist);
								num++;
								if(Integer.parseInt(strcount)<z1)
								{
									int history_z1=getNewA00z1_history(strym,dbarr[i],a0100,strcount);
									if(history_z1>Integer.parseInt(strcount)&&history_z1>z1)
										z1=history_z1;
									synchronousZ1(strym,strcount,z1,a0100,dbarr[i],updateList);
									strcount=String.valueOf(z1);
								}
				}
				for(int j=0;j<setlist.size();j++)
				{
					String setid=(String)setlist.get(j);
					if("A00".equalsIgnoreCase(setid))
						continue;
					String fields=(String)updatelist.get(j);
					/**(0,1,2,3)=(更新,新增,不变,更新(薪资重发时))*/
					String type=(String)typelist.get(j);
					if("3".equals(type))
						type="0";
					
					if("1".equals(royalty_valid)&&royalty_setid.equalsIgnoreCase(setid))
						type="0";
					if("2".equalsIgnoreCase(type))//当前记录不变
						continue;
					/**子集未构库不提交*/
					FieldSet fieldset=DataDictionary.getFieldSetVo(setid);
					if(fieldset==null|| "0".equalsIgnoreCase(fieldset.getUseflag()))
						continue;
					if("0".equals(subNoPriv))
					{
						if(!"2".equals(this.userview.analyseTablePriv(setid.toUpperCase())))
							continue;
					}
					/**(0,1,2)=(一般,按月变化,按年变化)*/
					int nflag=Integer.parseInt(fieldset.getChangeflag());
					int ntype=Integer.parseInt(type);
					
					
					
					switch(setid.charAt(0))
					{
					case 'A'://人员库
						if(!"A01".equalsIgnoreCase(setid))//处理非主集
						{
							dessetid=dbarr[i]+setid;
							supdate=(String)updatelist.get(j);
							
							HashMap values=new HashMap();
							if("1".equals(royalty_valid)&&royalty_setid.equalsIgnoreCase(setid))
							{
								FieldItem aitem=null;
								for(int n=0;n<relationfieldList.size();n++)
								{
									aitem=(FieldItem)relationfieldList.get(n);
									if("D".equalsIgnoreCase(aitem.getItemtype()))
									{
										if(rset.getDate(aitem.getItemid())==null)//||rset.getString(aitem.getItemid()).trim().length()==0)
											values.put(aitem.getItemid().toLowerCase(),"null");
										else
											values.put(aitem.getItemid().toLowerCase(),df.format(rset.getDate(aitem.getItemid())));
											
									}
									else if("A".equalsIgnoreCase(aitem.getItemtype()))
									{
										if(rset.getString(aitem.getItemid())==null||rset.getString(aitem.getItemid()).trim().length()==0)
											values.put(aitem.getItemid().toLowerCase(),"null");
										else
											values.put(aitem.getItemid().toLowerCase(),rset.getString(aitem.getItemid()));
									} 
								}
							}
							
							
							
							
							switch(ntype)
							{
							case 0://更新
								switch(nflag)
								{
									case 1://按月变化
									case 2://按年变化
										if("1".equals(royalty_valid)&&royalty_setid.equalsIgnoreCase(setid))
											ni9999=isHaveCurrentDateRecord_royalty(dessetid,setid,a0100,relationfieldList,values);
										else
											ni9999=isHaveCurrentDateRecord(dessetid, setid, a0100, nflag, strym, strcount);
										/**如果没有记录，则自动创建一条记录和上条记录完全一条*/
										if(ni9999==-1)
										{
											if("1".equals(royalty_valid)&&royalty_setid.equalsIgnoreCase(setid))
												ni9999= autoAppendRecord_royalty(dessetid,setid,a0100,nflag,strym,strcount,relationfieldList,values);
											else
												ni9999=autoAppendRecord(dessetid,setid,a0100,supdate,2,nflag,strym,strcount);
										}
										break;
									case 0://一般子集
										if("1".equals(royalty_valid)&&royalty_setid.equalsIgnoreCase(setid))
										{
											ni9999=isHaveCurrentDateRecord_royalty(dessetid,setid,a0100,relationfieldList,values);
											if(ni9999==-1)
												ni9999= autoAppendRecord_royalty(dessetid,setid,a0100,nflag,strym,strcount,relationfieldList,values);
										}
										else
											ni9999=DbNameBo.getPrimaryKey(dessetid, "I9999", " where a0100='"+a0100+"'", conn)-1;
										break;
								}
								break;
							case 1://追加记录
								switch(nflag)
								{
								case 1://按月变化
								case 2://按年变化
									ni9999=isHaveCurrentDateRecord(dessetid, setid, a0100, nflag, strym, strcount);
									/**如果没有记录，则自动创建一条记录和上条记录完全一条*/
									if(ni9999==-1)
										ni9999=autoAppendRecord(dessetid,setid,a0100,supdate,1,nflag,strym,strcount);
									break;
								case 0://一般子集
									ni9999=autoAppendRecord(dessetid,setid,a0100,supdate,1,nflag,strym,strcount);
									break;
								}
								break;
							}//操作方式
							if(nflag==0)//只有一般变化子集，才一个人一个人提交数据
							{
								
								String year=strym.substring(0, 4);
								String month=strym.substring(5, 7); 
								
								String value=getUpdateSQL(dessetid,this.gz_tablename,fields,nflag,itemUptype);
								StringBuffer strSWhere=new StringBuffer();
								StringBuffer strDWhere=new StringBuffer();
								if(Sql_switcher.searchDbServer()==Constant.ORACEL)
								{
									strSWhere.append(dessetid);
									strSWhere.append(".A0100='");
									strSWhere.append(a0100);
									strSWhere.append("' and ");
									strSWhere.append(dessetid);
									strSWhere.append(".I9999=");
									strSWhere.append(ni9999);
									 
									
									strDWhere.append(dessetid);
									strDWhere.append(".A0100='");
									strDWhere.append(a0100);
									strDWhere.append("' and ");
									strDWhere.append(dessetid);
									strDWhere.append(".I9999=");
									strDWhere.append(ni9999);	
									strDWhere.append(" and lower(");
									strDWhere.append(this.gz_tablename);
									strDWhere.append(".NBASE)='");
									strDWhere.append(cbase.toLowerCase()+"'  ");
									
									strDWhere.append(" and "+Sql_switcher.year(this.gz_tablename+".a00z0"));
									strDWhere.append("=");
									strDWhere.append(year);
									strDWhere.append(" and "+Sql_switcher.month(this.gz_tablename+".a00z0"));
									strDWhere.append("=");
									strDWhere.append(month);
									strDWhere.append(" and "+this.gz_tablename+".a00z1");
									strDWhere.append("=");
									strDWhere.append(strcount);			
									 
									
									strDWhere.append(filterWhl);
									
								}
								else
								{
									strSWhere.append(dessetid);
									strSWhere.append(".A0100='");
									strSWhere.append(a0100);
									strSWhere.append("' and ");
									strSWhere.append(dessetid);
									strSWhere.append(".I9999=");
									strSWhere.append(ni9999);
									strSWhere.append(" and lower(");
									strSWhere.append(this.gz_tablename);
									strSWhere.append(".NBASE)='");
									strSWhere.append(cbase.toLowerCase()+"'  ");
									
									strSWhere.append(" and "+Sql_switcher.year(this.gz_tablename+".a00z0"));
									strSWhere.append("=");
									strSWhere.append(year);
									strSWhere.append(" and "+Sql_switcher.month(this.gz_tablename+".a00z0"));
									strSWhere.append("=");
									strSWhere.append(month);
									strSWhere.append(" and "+this.gz_tablename+".a00z1");
									strSWhere.append("=");
									strSWhere.append(strcount);		
									
									strSWhere.append(filterWhl);
									
									strDWhere.append(dessetid);
									strDWhere.append(".A0100='");
									strDWhere.append(a0100);
									strDWhere.append("' and ");
									strDWhere.append(dessetid);
									strDWhere.append(".I9999=");
									strDWhere.append(ni9999);	
								} 
								
								if("1".equals(royalty_valid)&&royalty_setid.equalsIgnoreCase(setid))
								{
									FieldItem aitem=null;
									for(int n=0;n<relationfieldList.size();n++)
									{
										aitem=(FieldItem)relationfieldList.get(n); 
										if(!"null".equalsIgnoreCase((String)values.get(aitem.getItemid())))
										{
												if("D".equalsIgnoreCase(aitem.getItemtype()))
													strDWhere.append(" and "+Sql_switcher.isnull(Sql_switcher.dateToChar(this.gz_tablename+"."+aitem.getItemid(),"YYYY-MM-DD"),"''")+"='"+(String)values.get(aitem.getItemid())+"' ");
												else
													strDWhere.append(" and "+Sql_switcher.isnull(this.gz_tablename+"."+aitem.getItemid(),"''")+"='"+(String)values.get(aitem.getItemid())+"' "); 
										}
										else
										{
												if(Sql_switcher.searchDbServer()==Constant.ORACEL)
													strDWhere.append(" and "+ this.gz_tablename+"."+aitem.getItemid() +" is null "); 
												else
													strDWhere.append(" and "+Sql_switcher.isnull(this.gz_tablename+"."+aitem.getItemid(),"''")+"='' "); 
										}
											 
									} 
								}
								
								
								
								dbw.updateRecord(dessetid, gz_tablename,dessetid+".A0100="+gz_tablename+".A0100", value, strSWhere.toString(), strDWhere.toString());
							}
						}//if 非主集
						break;
					}//子集
				}//for setlist 数据集loop end.
			}//for while end.
			if(rset!=null)
				rset.close();
//			同步 薪资临时表 及 历史表里的a00z1
			if(updateList.size()>0)
			{
				 String[] _temps=this.gz_tablename.split("_salary_");
				String sql="update "+this.gz_tablename+" set a00z1=?,add_flag=1 where lower(nbase)='"+dbarr[i].toLowerCase()+"' and a0100=? "
				+" and "+Sql_switcher.year("a00z0")+"=? and "+Sql_switcher.month("a00z0")+"=? and a00z1=? ";
				String sql2="update salaryhistory  set a00z1=? where lower(nbase)='"+dbarr[i].toLowerCase()+"' and lower(userflag)='"+_temps[0].toLowerCase()+"'  and a0100=? "
				+" and "+Sql_switcher.year("a00z0")+"=? and "+Sql_switcher.month("a00z0")+"=? and a00z1=? and salaryid="+this.salaryid;
				dao.batchUpdate(sql, updateList);
				dao.batchUpdate(sql2, updateList);
				
				sql2="update gz_tax_mx  set a00z1=? where lower(nbase)='"+dbarr[i].toLowerCase()+"' and lower(userflag)='"+_temps[0].toLowerCase()+"'  and a0100=? "
				+" and "+Sql_switcher.year("a00z0")+"=? and "+Sql_switcher.month("a00z0")+"=? and a00z1=? and salaryid="+this.salaryid;
				dao.batchUpdate(sql2, updateList);
				
			}
			dao.update("update "+this.gz_tablename+" set  add_flag=1 where lower(nbase)='"+dbarr[i].toLowerCase()+"'  "+filterWhl);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/** 处理只有一条薪资记录的用户一般子集数据 */
	public void dealwithSingleRecord(ArrayList setlist,ArrayList updatelist,ArrayList typelist,String[] dbarr,int i,DbWizard dbw,HashMap itemUptype,String strNow)
	{
		try
		{
			String cbase=dbarr[i];
			String dessetid="";
			String supdate="";
			ContentDAO dao=new ContentDAO(this.conn);
			
			 //数据提交入库不判断子集及指标权限
			String subNoPriv=getLprogramAttri("subNoPriv",SalaryLProgramBo.CONFIRM_TYPE);
			if(subNoPriv==null||subNoPriv.trim().length()==0)
		 		subNoPriv="0";
		
			
			String tableName="t#"+this.userview.getUserName()+"_gz_1";  
		//	if(dbw.isExistTable(tableName,false))
				 dbw.dropTable(tableName);  
			String _sql="  from "+this.gz_tablename+" where lower("+this.gz_tablename+".NBASE)='"+cbase.toLowerCase()+"' "+filterWhl+"  and exists (select null from "+cbase+"A01 where "+cbase+"A01.a0100="+this.gz_tablename+".a0100 )  group by a0100  having count(a0100)=1 ";
			if(Sql_switcher.searchDbServer()==2)
				 dbw.execute("create table "+tableName+" as  select a0100 "+_sql);	
			else
				 dbw.execute("select a0100   into "+tableName+_sql);
			 	
			
			
			/** 只针对薪资临时表中的单一记录的进行处理 */
	//		String singleRecord_where2="select aa.a0100 from  (select count(a0100) c,a0100 from "+this.gz_tablename+" where "; 
	//		singleRecord_where2+=" lower("+this.gz_tablename+".NBASE)='"+cbase.toLowerCase()+"' "+filterWhl+"  and exists (select null from "+cbase+"A01 where "+cbase+"A01.a0100="+this.gz_tablename+".a0100 )  group by a0100  having count(a0100)=1 ) aa";
			
	//		String singleRecord_where="(select count(a0100) c,a0100 from "+this.gz_tablename+" where "; 
	//		singleRecord_where+=" lower("+this.gz_tablename+".NBASE)='"+cbase.toLowerCase()+"' "+filterWhl+"  and exists (select null from "+cbase+"A01 where "+cbase+"A01.a0100="+this.gz_tablename+".a0100 )  group by a0100  having count(a0100)=1 ) aa";
			
			//提成薪资相关参数
			String  royalty_valid=this.ctrlparam.getValue(SalaryCtrlParamBo.ROYALTIES,"valid");
			String royalty_relation_fields=this.ctrlparam.getValue(SalaryCtrlParamBo.ROYALTIES,"relation_fields");
			ArrayList relationfieldList=new ArrayList();
			String royalty_setid=this.ctrlparam.getValue(SalaryCtrlParamBo.ROYALTIES,"setid");
			if("1".equals(royalty_valid)&&royalty_relation_fields.length()>0)
			{
				String[] temps=royalty_relation_fields.toLowerCase().split(",");
				for(int n=0;n<temps.length;n++)
				{
					if(temps[n].trim().length()>0&&DataDictionary.getFieldItem(temps[n].trim())!=null)
					{ 
						relationfieldList.add(temps[n]);
					}
				}
			}
			
			for(int j=0;j<setlist.size();j++)
			{
				String setid=(String)setlist.get(j);
				if("A00".equalsIgnoreCase(setid))
					continue;
				String fields=(String)updatelist.get(j);
				/**(0,1,2,3)=(更新,新增,不变,更新(薪资重发时))*/
				String type=(String)typelist.get(j);
				if("3".equals(type))
					type="0";
				if("2".equalsIgnoreCase(type))//当前记录不变
					continue;
				/**子集未构库不提交*/
				FieldSet fieldset=DataDictionary.getFieldSetVo(setid);
				if(fieldset==null|| "0".equalsIgnoreCase(fieldset.getUseflag()))
					continue;
				/**(0,1,2)=(一般,按月变化,按年变化)*/
				int nflag=Integer.parseInt(fieldset.getChangeflag());
				int ntype=Integer.parseInt(type);
				
				if(nflag==0&&setid.charAt(0)=='A'&&!"A01".equalsIgnoreCase(setid))  /**一般子集*/
				{
					dessetid=dbarr[i]+setid;
					supdate=(String)updatelist.get(j);
					if("1".equals(royalty_valid)&&royalty_setid.equalsIgnoreCase(setid))
						ntype=0;
					if(ntype==2)
						continue;
					if("0".equals(subNoPriv))
					{
						if(!"2".equals(this.userview.analyseTablePriv(setid.toUpperCase())))
							continue;
					}
					
					switch(ntype)
					{
						case 0://更新 
							if("1".equals(royalty_valid)&&royalty_setid.equalsIgnoreCase(setid))
							{
						//		String where_str=singleRecord_where; 
								String strvalue=","+strNow+",'"+this.userview.getUserName()+"'";
								String where_str2="select a0100 from "+this.gz_tablename+" where "; 
								where_str2+="  not exists (select null from "+dessetid+"  where "+this.gz_tablename+".a0100="+dessetid+".a0100  )";
								where_str2+=" and lower("+this.gz_tablename+".NBASE)='"+cbase.toLowerCase()+"' "+filterWhl+"   group by a0100  having count(a0100)=1";
								batchInsertSetRecord_royalty(dessetid,tableName+" aa",where_str2,relationfieldList,cbase);
								
							} 
							break;
						case 1://追加记录
						//	String where_str=singleRecord_where;
							 
							String strvalue=","+strNow+",'"+this.userview.getUserName()+"'";
							String where_str2="select a0100,1"+strvalue+"from "+this.gz_tablename+" where "; 
							where_str2+="  not exists (select null from "+dessetid+"  where "+this.gz_tablename+".a0100="+dessetid+".a0100  )";
							where_str2+=" and lower("+this.gz_tablename+".NBASE)='"+cbase.toLowerCase()+"' "+filterWhl+"   group by a0100  having count(a0100)=1";
							batchInsertSetRecord(dessetid,tableName+" aa",where_str2);
								
							break;
					}
					 
					String value="";
					HashMap relationFieldMap=new HashMap();
					StringBuffer sqlRelation=new StringBuffer("");
					StringBuffer sqlRelation2=new StringBuffer("");
					if("1".equals(royalty_valid)&&royalty_setid.equalsIgnoreCase(setid))
					{
						for(int n=0;n<relationfieldList.size();n++)
						{
							String key=(String)relationfieldList.get(n);
							relationFieldMap.put(key.toLowerCase(),"");
							FieldItem item=DataDictionary.getFieldItem(key.toLowerCase());
							if(Sql_switcher.searchDbServer()==2)
							{
								if("D".equalsIgnoreCase(item.getItemtype()))
								{
									sqlRelation.append(" and "+Sql_switcher.isnull(Sql_switcher.dateToChar(dessetid+"."+key,"YYYY-MM-DD"),"'-'")+"="+Sql_switcher.isnull(Sql_switcher.dateToChar("t#"+this.userview.getUserName()+"_gz"+"."+key,"YYYY-MM-DD"),"'-'") ); 
									sqlRelation2.append(" and "+Sql_switcher.isnull(Sql_switcher.dateToChar(dessetid+"."+key,"YYYY-MM-DD"),"'-'")+"="+Sql_switcher.isnull(Sql_switcher.dateToChar(this.gz_tablename+"."+key,"YYYY-MM-DD"),"'-'") ); 
								}
								else
								{
									sqlRelation.append(" and "+Sql_switcher.isnull(dessetid+"."+key,"'-'")+"="+Sql_switcher.isnull("t#"+this.userview.getUserName()+"_gz"+"."+key,"'-'") ); 
									sqlRelation2.append(" and "+Sql_switcher.isnull(dessetid+"."+key,"'-'")+"="+Sql_switcher.isnull(this.gz_tablename+"."+key,"'-'") ); 
								}
							}
							else		
							{
								sqlRelation.append(" and "+Sql_switcher.isnull(dessetid+"."+key,"''")+"="+Sql_switcher.isnull("t#"+this.userview.getUserName()+"_gz"+"."+key,"''"));
								sqlRelation2.append(" and "+Sql_switcher.isnull(dessetid+"."+key,"''")+"="+Sql_switcher.isnull(this.gz_tablename+"."+key,"''"));
							}
						}
						
						value=getUpdateSQL_royalty(dessetid,this.gz_tablename,fields,nflag,itemUptype);
					}
					else
						value=getUpdateSQL(dessetid,this.gz_tablename,fields,nflag,itemUptype);//求实际更新sql
					if(Sql_switcher.searchDbServer()==2)
					{
						 String strArr[] = StringUtils.split(value, "`");
					     StringBuffer sub_str=new StringBuffer("");
					     StringBuffer sub_str2=new StringBuffer("");
						 for(int e = 0; e < strArr.length; e++)
					     {
					            String temp = strArr[e];
					            String strtmp[] = StringUtils.split(temp, "=", 2);
					            if(strtmp[1].indexOf(this.gz_tablename)==-1)
					            {
					            	sub_str.append(","+strtmp[1]+" as "+strtmp[0]);
					            }
					            else
					            {
					            	sub_str.append(","+strtmp[1]);
					            	if(relationFieldMap.get(strtmp[1].replaceAll(this.gz_tablename+".","").toLowerCase())!=null)
					            		relationFieldMap.put(strtmp[1].replaceAll(this.gz_tablename+".","").toLowerCase(),"1");
					            }
					            
					            if(strtmp[0].indexOf(dessetid)==-1)
					            	sub_str2.append(","+strtmp[0]);
					            else
					            {
					            	String[] aa=strtmp[0].split("\\.");
					            	sub_str2.append(","+aa[1]);
					            	
					            }
					     }
                      
					    String tempName="t#"+this.userview.getUserName()+"_gz";  //this.userview.getUserName()+"_GzTempTable";
				//	     if(dbw.isExistTable(tempName,false))
					     {
					    	 dbw.dropTable(tempName); 
					     }
					     StringBuffer sql=new StringBuffer("create table "+tempName+" as ");
					     if("1".equals(royalty_valid)&&royalty_setid.equalsIgnoreCase(setid))
					     {
					    	 Set set=relationFieldMap.keySet();
					    	 for(Iterator t=set.iterator();t.hasNext();)
					    	 {
					    		 String key=(String)t.next(); 
					    		 if(!"1".equalsIgnoreCase((String)relationFieldMap.get(key)))
					    			 sub_str.append(","+key);
					    	 }
					     }
						 sql.append("select "+this.gz_tablename+".a0100"+sub_str.toString());
						 sql.append(" from "+this.gz_tablename+","+tableName+" aa");
						 sql.append(" where aa.a0100="+this.gz_tablename+".a0100   "+filterWhl+"  and lower("+this.gz_tablename+".NBASE)='"+cbase.toLowerCase()+"' ");
						 dao.update(sql.toString());
					      
						
					     sql.setLength(0);
					     
					     if("1".equals(royalty_valid)&&royalty_setid.equalsIgnoreCase(setid))
					     {
					    	 sql.append("Update  "+dessetid+" set ("+sub_str2.substring(1)+")=");
						     sql.append(" (select "+sub_str2.substring(1)+" from "+tempName);
						     sql.append(" where "+tempName+".a0100="+dessetid+".a0100  "+sqlRelation.toString()) ;
						     sql.append(" ) where   ");
						     sql.append("   exists (select null from "+tempName+" where "+tempName+".a0100="+dessetid+".a0100  "+sqlRelation.toString()+") ");
					    	 
					     }
					     else
					     { 
						     sql.append("Update  "+dessetid+" set ("+sub_str2.substring(1)+")=");
						     sql.append(" (select "+sub_str2.substring(1)+" from "+tempName);
						     sql.append(" where "+tempName+".a0100="+dessetid+".a0100  ") ;
						     sql.append(" ) where "+dessetid+".i9999=(select max(b.i9999) from "+dessetid+" b where b.a0100="+dessetid+".a0100 )  and ");
						     sql.append("   exists (select null from "+tempName+" where "+tempName+".a0100="+dessetid+".a0100 ) ");
					     }
					     dao.update(sql.toString());
					     
					}
					else
					{
						
						StringBuffer strSWhere=new StringBuffer();
						if("1".equals(royalty_valid)&&royalty_setid.equalsIgnoreCase(setid))
						{
						 
							strSWhere.append(dessetid+".A0100="+this.gz_tablename+".A0100 "+sqlRelation2.toString());
							strSWhere.append("  "+filterWhl+"  and lower("+this.gz_tablename+".NBASE)='"+cbase.toLowerCase()+"'");
							strSWhere.append(" and exists (select null from "+tableName+" aa where aa.a0100="+dessetid+".a0100) ");
						}
						else
						{
							strSWhere.append(dessetid+".i9999=(select max(b.i9999) from "+dessetid+" b where b.a0100="+dessetid+".a0100 )  ");
							strSWhere.append(" and "+dessetid+".A0100="+this.gz_tablename+".A0100 ");
							strSWhere.append("  "+filterWhl+"  and lower("+this.gz_tablename+".NBASE)='"+cbase.toLowerCase()+"'");
							strSWhere.append(" and exists (select null from "+tableName+" aa where aa.a0100="+dessetid+".a0100) ");
						}
						
						StringBuffer strDWhere=new StringBuffer();
						if("1".equals(royalty_valid)&&royalty_setid.equalsIgnoreCase(setid))
						{ 
							strDWhere.append(dessetid+".A0100="+this.gz_tablename+".A0100 "+sqlRelation2.toString());
							strDWhere.append("  "+filterWhl+"  and exists (select null from "+tableName+" aa where aa.a0100="+dessetid+".a0100) ");
						}
						else
						{
							strDWhere.append(dessetid+".i9999=(select max(b.i9999) from "+dessetid+" b where b.a0100="+dessetid+".a0100 ) and ");
							strDWhere.append(dessetid+".A0100="+this.gz_tablename+".A0100 ");
							strDWhere.append("  "+filterWhl+"  and exists (select null from "+tableName+" aa where aa.a0100="+dessetid+".a0100) ");
						}	
						
						
						
						if(Sql_switcher.searchDbServer()!=2)  //不为oracle
							dbw.updateRecord(dessetid, gz_tablename,dessetid+".A0100="+gz_tablename+".A0100", value, strSWhere.toString(),"");
						else
							dbw.updateRecord(dessetid, gz_tablename,dessetid+".A0100="+gz_tablename+".A0100", value, strSWhere.toString(), strDWhere.toString());
						
						 
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
	 * 取得薪资临时表中归属日期，次数
	 * @param gzTempTable
	 * @return
	 */
	public ArrayList  getZ0z1List(String gzTempTable)
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
		//	System.out.println("select distinct "+Sql_switcher.year("a00z0")+","+Sql_switcher.month("a00z0")+" from "+gzTempTable+" where 1=1 "+this.filterWhl+" order by "+Sql_switcher.year("a00z0")+","+Sql_switcher.month("a00z0")+"");
			RowSet rowSet=dao.search("select distinct "+Sql_switcher.year("a00z0")+","+Sql_switcher.month("a00z0")+" from "+gzTempTable+this._withNoLock+" where 1=1 "+this.filterWhl+" order by "+Sql_switcher.year("a00z0")+","+Sql_switcher.month("a00z0")+"");
			while(rowSet.next())
			{
				//String strym=PubFunc.FormatDate(rowSet.getDate("a00z0"), "yyyy-MM-dd");
				String year=rowSet.getString(1);
				String month=rowSet.getString(2);
				LazyDynaBean abean=new LazyDynaBean();
				abean.set("strym",year+"-"+month+"-01");
			
				list.add(abean);
			}
			rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	/** 取得年月变化子集各人员当月最大次数sql语句 
	 * 
	 * 	select  max(a41z1) z1,a0100  from usra41 where  a0100 in (select a0100 from 王成洁_salary_68 where lower(nbase)='usr') and year(a41z0)=2008 and month(a41z0)=2  group by a0100
		union all
		select max(a58z1) z1,a0100 from usra58 where  a0100 in (select a0100 from 王成洁_salary_68 where lower(nbase)='usr') and year(a58z0)=2008 and month(a58z0)=2 group by a0100	
	 * */
	public String getSetsMaxZ1_sql(String strym,String nbase)
	{
		StringBuffer sub_sql=new StringBuffer("");
		String[] temp=strym.split("-");
		for(int j=0;j<setlist.size();j++)
		{
			String setid=(String)setlist.get(j);
			if(setid.charAt(0)=='A')
			{
				if("A00".equalsIgnoreCase(setid))
					continue;
				FieldSet fieldset=DataDictionary.getFieldSetVo(setid);
				if(fieldset==null|| "0".equalsIgnoreCase(fieldset.getUseflag()))
					continue;
				int nflag=Integer.parseInt(fieldset.getChangeflag());
				if(nflag==1||nflag==2)
				{
					sub_sql.append(" union all ");
					sub_sql.append("select max("+setid+"z1) z1,a0100 from "+nbase+setid+" where a0100 in ( ");
					sub_sql.append("select a0100 from "+this.gz_tablename+" where lower(nbase)='"+nbase.toLowerCase()+"'");
					sub_sql.append(" ) and "+Sql_switcher.year(setid+"z0")+"="+temp[0]+" and "+Sql_switcher.month(setid+"z0")+"="+temp[1]+"  group by a0100 ");
				}
			}
		}
		return sub_sql.substring(10);
	}
	
	
	/**
	 * 处理 如果子集中有相应次数，自动加1 （针对orcale）
	 * @param strym
	 * @param nbase
	 * @param dbw
	 * @param dao
	 * @return
	 */
	public String executeMaxZ1Table(String strym,String nbase,DbWizard dbw,ContentDAO dao,ArrayList setlist,ArrayList typelist)
	{
		String tableName="t#"+this.userview.getUserName()+"_gz_1"; //this.userview.getUserName()+"_maxZ1";
		String tableName2="t#"+this.userview.getUserName()+"_gz_2"; //this.userview.getUserName()+"_maxZ1_2";
		try
		{
		//	 if(dbw.isExistTable(tableName,false))
				 dbw.dropTable(tableName);
			 {
				 Field field=null;
				 Table table=new Table(tableName);
				 
				 field=new Field("A0100","A0100");
				 field.setDatatype(DataType.STRING);
				 field.setLength(30);
				 table.addField(field);						
				 field=new Field("A00Z1","A00Z1");
				 field.setDatatype(DataType.INT);
				 field.setLength(10);
				 table.addField(field);	
				 field=new Field("A00Z0","A00Z0");
				 field.setDatatype(DataType.DATE);
				 table.addField(field);
				 
				 dbw.createTable(table);
			}
			if(!dbw.isExistField(tableName,"A00Z1_O",false))
			{
				Table table=new Table(tableName);
				Field field=new Field("A00Z1_O","A00Z1_O");
				field.setDatatype(DataType.INT);
				field.setLength(10);
				table.addField(field);	
				dbw.addColumns(table);
			}
				
			dbw.execute("delete from "+tableName);
			 
			
			 String tempName="t#"+this.userview.getUserName()+"_gz";  //this.userview.getUserName()+"_GzTempTable";
			 StringBuffer sql=new StringBuffer("insert into "+tableName+" (a0100,a00z1,A00Z1_O)");
			 sql.append(" select "+tempName+".a0100,0,"+tempName+".a00z1 from "+tempName);
			 sql.append(" where add_flag=0 or add_flag is null  ");
			 dao.update(sql.toString());
			
		//	 if(dbw.isExistTable(tableName2,false))
				 dbw.dropTable(tableName2);
		//	 if(!dbw.isExistTable(tableName2,false))
			 {
				 if(Sql_switcher.searchDbServer()==2)
					 dbw.execute("create table "+tableName2+" as  select a0100,a00z1 from "+this.gz_tablename+" where 1=2");	
				 else
					 dbw.execute("select a0100,a00z1  into "+tableName2+"  from "+this.gz_tablename+" where 1=2");
			 }	
			 
			 String[] temp=strym.split("-");
			 for(int j=0;j<setlist.size();j++)
			 {
					String setid=(String)setlist.get(j);
					/**(0,1,2,3)=(更新,新增,不变,更新(薪资重发时))*/
					String type=(String)typelist.get(j);
					if("2".equalsIgnoreCase(type)|| "0".equalsIgnoreCase(type))//当前记录不变
						continue;
					if(setid.charAt(0)=='A')
					{
						if("A00".equalsIgnoreCase(setid))
							continue;
						FieldSet fieldset=DataDictionary.getFieldSetVo(setid);
						if(fieldset==null|| "0".equalsIgnoreCase(fieldset.getUseflag()))
							continue;
						int nflag=Integer.parseInt(fieldset.getChangeflag());
						if(nflag==1||nflag==2)
						{ 
								dbw.execute("delete from "+tableName2);
								sql.setLength(0);
								sql.append("insert into "+tableName2+" (a00z1,a0100) select  MAX("+setid+"Z1) ,a0100 from "+nbase+setid+" where  a0100 in (select a0100 from "+tableName+")  and  "+Sql_switcher.year(setid+"z0")+"="+temp[0]+" and "+Sql_switcher.month(setid+"z0")+"="+temp[1]+"	GROUP BY A0100 ");
								dbw.execute(sql.toString());
								sql.setLength(0);
								sql.append("update "+tableName+" set a00z1=(select a00z1 from  ");
								sql.append(tableName2+"  where "+tableName2+".a0100="+tableName+".a0100 and "+tableName2+".a00z1>"+tableName+".a00z1 ");
								sql.append(" ) where exists( select  null from "+tableName2+" where "+tableName2+".a0100="+tableName+".a0100 and "+tableName2+".a00z1>"+tableName+".a00z1 ");
								sql.append(" )  ");
								dbw.execute(sql.toString());
						}
					}
			 }
			  String[] _temps=this.gz_tablename.split("_salary_");
			 //salaryhistory
			 {
				  
				 	dbw.execute("delete from "+tableName2);
					sql.setLength(0);
					sql.append("insert into "+tableName2+" (a00z1,a0100) select  MAX(a00Z1) ,a0100 from salaryhistory"+this._withNoLock+" where  a0100 in (select a0100 from "+tableName+") ");
					sql.append(" and  "+Sql_switcher.year("a00z0")+"="+temp[0]+" and "+Sql_switcher.month("a00z0")+"="+temp[1]+" and salaryid="+this.salaryid);
					sql.append(" and lower(userflag)<>'"+_temps[0].toLowerCase()+"'  and  lower(nbase)='"+nbase.toLowerCase()+"'	GROUP BY A0100 ");
					dbw.execute(sql.toString());
					sql.setLength(0);
					sql.append("update "+tableName+" set a00z1=(select a00z1 from  ");
					sql.append(tableName2+"  where "+tableName2+".a0100="+tableName+".a0100 and "+tableName2+".a00z1>"+tableName+".a00z1 ");
					sql.append(" ) where A00Z1_O<=a00z1 and exists( select  null from "+tableName2+" where "+tableName2+".a0100="+tableName+".a0100 and "+tableName2+".a00z1>"+tableName+".a00z1 ");
					sql.append(" )  ");
					dbw.execute(sql.toString());
			 }
			 
			 
			 
			 ArrayList updateList=new ArrayList();
			 sql.setLength(0);
			 sql.append("select a.a00z1+1,"+this.gz_tablename+".a0100,"+this.gz_tablename+".a00z1,"+this.gz_tablename+".a00z0 from "+tableName+" a,"+this.gz_tablename);
			 sql.append(" where a.a0100="+this.gz_tablename+".a0100 and lower("+this.gz_tablename+".nbase)='"+nbase.toLowerCase()+"'  ");
			 sql.append("  and  "+Sql_switcher.year(this.gz_tablename+".a00z0")+"="+temp[0]+" and "+Sql_switcher.month(this.gz_tablename+".a00z0")+"="+temp[1]+" ");
			 sql.append(filterWhl+"   and a.a00z1>="+this.gz_tablename+".a00z1 ");
			 RowSet rowSet=dao.search(sql.toString());
			 ArrayList tempList=null;
			 while(rowSet.next())
			 {
					tempList=new ArrayList();
					tempList.add(new Integer(rowSet.getInt(1)));
					tempList.add(rowSet.getString(2));
					tempList.add(new Integer(temp[0]));
					tempList.add(new Integer(temp[1]));
					tempList.add(new Integer(rowSet.getInt(3)));
					updateList.add(tempList);
			 }
			 
			  sql.setLength(0);
			  sql.append("update "+this.gz_tablename+" set a00z1=(select "+tableName+".a00z1+1 from  "+tableName);
			  sql.append(" where "+this.gz_tablename+".a0100="+tableName+".a0100 and "+tableName+".a00z1>="+this.gz_tablename+".a00z1  )");
		//    sql.append(" where exists (select null from "+tableName+" where "+this.gz_tablename+".a0100="+tableName+".a0100 and "+tableName+".a00z1>="+this.gz_tablename+".a00z1  )  "+filterWhl+"   and lower("+this.gz_tablename+".nbase)='"+nbase.toLowerCase()+"'");
			  sql.append(" where exists (select null from "+tableName+" where "+this.gz_tablename+".a0100="+tableName+".a0100 and "+tableName+".a00z1>="+this.gz_tablename+".a00z1  )  "+filterWhl);
			  sql.append("  and  "+Sql_switcher.year(this.gz_tablename+".a00z0")+"="+temp[0]+" and "+Sql_switcher.month(this.gz_tablename+".a00z0")+"="+temp[1]+" ");
			  sql.append("   and lower("+this.gz_tablename+".nbase)='"+nbase.toLowerCase()+"'");
			  dbw.execute(sql.toString());
			  dbw.execute("update "+this.gz_tablename+" set  add_flag=1 where exists (select null from "+tableName+" where "+tableName+".a0100="+gz_tablename+".a0100)   "+filterWhl+"   and lower(nbase)='"+nbase.toLowerCase()+"'  and "+Sql_switcher.year("a00z0")+"="+temp[0]+" and "+Sql_switcher.month("a00z0")+"="+temp[1]);
		
			 //	同步历史数据的z1
				if(updateList.size()>0)
				{
					String sql2="update gz_tax_mx  set a00z1=? where lower(nbase)='"+nbase.toLowerCase()+"' and lower(userflag)='"+_temps[0].toLowerCase()+"' and a0100=? "
					+" and "+Sql_switcher.year("a00z0")+"=? and "+Sql_switcher.month("a00z0")+"=? and a00z1=? and salaryid="+this.salaryid;
					dao.batchUpdate(sql2, updateList);
					
					String sql1="update salaryhistory  set a00z1=? where lower(nbase)='"+nbase.toLowerCase()+"' and a0100=? "
					+" and "+Sql_switcher.year("a00z0")+"=? and "+Sql_switcher.month("a00z0")+"=? and a00z1=? and lower(userflag)='"+_temps[0].toLowerCase()+"'  and salaryid="+this.salaryid;
					dao.batchUpdate(sql1, updateList);
					
				}
				if(rowSet!=null)
					rowSet.close();
			 
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return tableName;
	}
	
	
	
	/** 处理只有一条薪资记录的用户年月子集数据 */
	public void dealwithSingleRecord_ym(ArrayList setlist,boolean flag,boolean isYMaddSet,ArrayList updatelist,ArrayList typelist,String cbase,DbWizard dbw,HashMap itemUptype,String strNow,String strym)
	{
		try
		{
			//数据提交入库不判断子集及指标权限
			String subNoPriv=getLprogramAttri("subNoPriv",SalaryLProgramBo.CONFIRM_TYPE);
			if(subNoPriv==null||subNoPriv.trim().length()==0)
		 		subNoPriv="0";
		 		
			
			String[] temp=strym.split("-");
			ContentDAO dao=new ContentDAO(this.conn);
	//		String singleRecord_where="select aa.a0100 from  (select count(a0100) c,a0100 from "+this.gz_tablename+" where "; // a0100 in (select a0100 from "+cbase+"A01) and 
	//		singleRecord_where+=" lower("+this.gz_tablename+".NBASE)='"+cbase.toLowerCase()+"'  "+filterWhl+"  and exists (select null from "+cbase+"A01 where "+cbase+"A01.a0100="+this.gz_tablename+".a0100 )   group by a0100  having count(a0100)=1 ) aa";
			if(flag&&isYMaddSet)
			{
				
			//	if(Sql_switcher.searchDbServer()==2)
				//{
					executeMaxZ1Table(strym,cbase,dbw,dao,setlist,typelist);	
				//}
		/*		else
				{
					
					
					ArrayList updateList=new ArrayList();
					StringBuffer sql_str=new StringBuffer("select a.z1+1,"+this.gz_tablename+".a0100,"+this.gz_tablename+".a00z1,"+this.gz_tablename+".a00z0  from   ");
					sql_str.append(" ( select max(z1) z1,a0100 from ( ");
					sql_str.append(getSetsMaxZ1_sql(strym,cbase));
					sql_str.append(") aa group by a0100 ) a,"+this.gz_tablename+" ");
					sql_str.append("where a.a0100="+this.gz_tablename+".a0100 and  lower("+this.gz_tablename+".nbase)='"+cbase.toLowerCase()+"' ");
					sql_str.append(" and a.z1>="+this.gz_tablename+".a00z1"); 
					sql_str.append(" and "+this.gz_tablename+".a0100 in ( "+singleRecord_where+" ) ");
					sql_str.append(" and  lower("+this.gz_tablename+".nbase)='"+cbase.toLowerCase()+"'  "+filterWhl+"  and "+Sql_switcher.year("a00z0")+"="+temp[0]+" and "+Sql_switcher.month("a00z0")+"="+temp[1]+"  and ( add_flag=0 or add_flag is null ) ");
					RowSet rowSet=dao.search(sql_str.toString());
					while(rowSet.next())
					{
						ArrayList tempList=new ArrayList();
						tempList.add(new Integer(rowSet.getInt(1)));
						tempList.add(rowSet.getString(2));
						tempList.add(new Integer(temp[0]));
						tempList.add(new Integer(temp[1]));
						tempList.add(new Integer(rowSet.getInt(3)));
						updateList.add(tempList);
					}
					rowSet.close();
					sql_str.setLength(0);
					sql_str.append("update "+this.gz_tablename+" set a00z1=(select a.z1+1 from  ");
					sql_str.append(" ( select max(z1) z1,a0100 from ( ");
				    sql_str.append(getSetsMaxZ1_sql(strym,cbase));
					sql_str.append(") aa group by a0100 ) a ");
					sql_str.append(" where a.a0100="+this.gz_tablename+".a0100  "+filterWhl+"   and  lower("+this.gz_tablename+".nbase)='"+cbase.toLowerCase()+"' and a.z1>="+this.gz_tablename+".a00z1 ");
					sql_str.append(" ) where a0100 in ( ");
					sql_str.append(singleRecord_where);
					sql_str.append(") and a0100 in ( select a.a0100 from ( select max(z1) z1,a0100 from ( ");
					sql_str.append(getSetsMaxZ1_sql(strym,cbase));
					sql_str.append(" ) aa group by a0100 ) a,"+this.gz_tablename);
					sql_str.append(" where a.a0100="+this.gz_tablename+".a0100  "+filterWhl+"   and  lower("+this.gz_tablename+".nbase)='"+cbase.toLowerCase()+"' and a.z1>="+this.gz_tablename+".a00z1) and  lower("+this.gz_tablename+".nbase)='"+cbase.toLowerCase()+"' ");
					sql_str.append(" and "+Sql_switcher.year("a00z0")+"="+temp[0]+" and "+Sql_switcher.month("a00z0")+"="+temp[1]);
					sql_str.append("  and ( add_flag=0 or add_flag is null ) ");
				
					dbw.execute(sql_str.toString());
					dbw.execute("update "+this.gz_tablename+" set  add_flag=1 where a0100 in ("+singleRecord_where+") and lower(nbase)='"+cbase.toLowerCase()+"'  and "+Sql_switcher.year("a00z0")+"="+temp[0]+" and "+Sql_switcher.month("a00z0")+"="+temp[1]);
				    //同步历史数据的z1
					if(updateList.size()>0)
					{
						String sql2="update salaryhistory  set a00z1=? where lower(nbase)='"+cbase.toLowerCase()+"' and a0100=? "
						+" and "+Sql_switcher.year("a00z0")+"=? and "+Sql_switcher.month("a00z0")+"=? and a00z1=? and salaryid="+this.salaryid;
						dao.batchUpdate(sql2, updateList);
						
						sql2="update gz_tax_mx  set a00z1=? where lower(nbase)='"+cbase.toLowerCase()+"' and a0100=? "
						+" and "+Sql_switcher.year("a00z0")+"=? and "+Sql_switcher.month("a00z0")+"=? and a00z1=? and salaryid="+this.salaryid;
						dao.batchUpdate(sql2, updateList);
					}
					
					
				}*/
			}
			//判断子集中是否有当年当月当次记录，没有则追加
			String strIns=",createtime,createusername";
			String strvalue=","+strNow+",'"+this.userview.getUserName()+"'";
			for(int j=0;j<setlist.size();j++)
			{
				String setid=(String)setlist.get(j);
				/**(0,1,2,3)=(更新,新增,不变,更新(薪资重发时))*/
				String type=(String)typelist.get(j);
				if("3".equals(type))
					type="0";
				if("2".equalsIgnoreCase(type))//当前记录不变
					continue;
				if("0".equals(subNoPriv))
				{
					if(!"2".equals(this.userview.analyseTablePriv(setid.toUpperCase())))
						continue;
				}
				String destname=cbase+setid;
				String axxz0=setid+"z0";
				String axxz1=setid+"z1";
				if(setid.charAt(0)=='A')
				{
					if("A00".equalsIgnoreCase(setid))
						continue;
					FieldSet fieldset=DataDictionary.getFieldSetVo(setid);
					if(fieldset==null|| "0".equalsIgnoreCase(fieldset.getUseflag()))
						continue;
					int nflag=Integer.parseInt(fieldset.getChangeflag());
					if(nflag==1||nflag==2)
					{
					//	if(Sql_switcher.searchDbServer()==2)
						{
							insertSetInfo(cbase,strym,dbw,dao,setid,strIns,strvalue);
						}
				/*		else
						{
						
							StringBuffer sql=new StringBuffer("");
							sql.append("insert into "+destname+" (A0100,I9999"+strIns+","+axxz0+","+axxz1+") ");
							sql.append(" select a1.a0100,a2.i9999+1"+strvalue+",a1.a00z0,a1.a00z1 from ");
							sql.append("(select a0100,a00z0,a00z1 from "+this.gz_tablename+" where ");
							sql.append(" "+Sql_switcher.year(this.gz_tablename+".a00z0")+"="+temp[0]+"  "+filterWhl+"   and "+Sql_switcher.month(this.gz_tablename+".a00z0")+"="+temp[1]);
							sql.append(" and a0100 not in ( ");
							sql.append("select  "+this.gz_tablename+".a0100 from  "+this.gz_tablename+","+destname+" ");
							sql.append("where  "+this.gz_tablename+".a00z0="+destname+"."+axxz0+" and  "+this.gz_tablename+".a00z1="+destname+"."+axxz1+"  ");
							sql.append("and  "+this.gz_tablename+".a0100="+destname+".a0100  "+filterWhl+"  and lower( "+this.gz_tablename+".nbase)='"+cbase.toLowerCase()+"' ");
							sql.append(") and lower( "+this.gz_tablename+".nbase)='"+cbase.toLowerCase()+"' ) a1, ");
							sql.append("( select a0100,i9999 from "+destname+" a where a.i9999=(select max(b.i9999) from "+destname+" b where a.a0100=b.a0100 ) ) a2 ");
							sql.append("where a1.a0100=a2.a0100 and a1.a0100 in ("+singleRecord_where+") ");
							dbw.execute(sql.toString());
					
							sql.setLength(0);
							sql.append("insert into "+destname+" (A0100,I9999"+strIns+","+axxz0+","+axxz1+") ");
							sql.append(" select a1.a0100,1"+strvalue+",a1.a00z0,a1.a00z1 from ");
							sql.append("(select a0100,a00z0,a00z1 from "+this.gz_tablename+" where ");
							sql.append(" "+Sql_switcher.year(this.gz_tablename+".a00z0")+"="+temp[0]+" and "+Sql_switcher.month(this.gz_tablename+".a00z0")+"="+temp[1]);
							sql.append("  "+filterWhl+"   and a0100 not in ( ");
							sql.append("select "+this.gz_tablename+".a0100 from "+this.gz_tablename+", "+destname+" ");
							sql.append("where "+this.gz_tablename+".a00z0= "+destname+"."+axxz0+" and "+this.gz_tablename+".a00z1= "+destname+"."+axxz1+"  ");
							sql.append("and "+this.gz_tablename+".a0100= "+destname+".a0100 and lower("+this.gz_tablename+".nbase)='"+cbase.toLowerCase()+"' ");
							sql.append(")  "+filterWhl+"  and lower("+this.gz_tablename+".nbase)='"+cbase.toLowerCase()+"' ) a1 "); 
							sql.append("where a0100 not in ");
							sql.append("( select a0100 from  "+destname+") ");
							sql.append(" and a1.a0100 in ("+singleRecord_where+") ");
							dbw.execute(sql.toString());
						}*/
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
		
	
	
	
	public void insertSetInfo(String nbase,String strym,DbWizard dbw,ContentDAO dao,String setid,String strIns,String strvalue)
	{
		try
		{
			
			String  royalty_valid=this.ctrlparam.getValue(SalaryCtrlParamBo.ROYALTIES,"valid");
			String royalty_relation_fields=this.ctrlparam.getValue(SalaryCtrlParamBo.ROYALTIES,"relation_fields");
			ArrayList relationfieldList=new ArrayList();
			String royalty_setid=this.ctrlparam.getValue(SalaryCtrlParamBo.ROYALTIES,"setid");
			StringBuffer relationFieldStr=new StringBuffer("");
			String relation_str="";
			if("1".equalsIgnoreCase(royalty_valid)&&royalty_relation_fields.length()>0&&setid.equalsIgnoreCase(royalty_setid))
			{
				String[] temps=royalty_relation_fields.toLowerCase().split(",");
				for(int n=0;n<temps.length;n++)
				{
					if(temps[n].trim().length()>0&&DataDictionary.getFieldItem(temps[n].trim())!=null)
					{ 
						relationfieldList.add(temps[n]);
						relationFieldStr.append(","+temps[n]);
					}
				}
			} 
			String destname=nbase+setid;
			String tableName="t#"+this.userview.getUserName()+"_gz_1"; //this.userview.getUserName()+"_maxZ1";
		//	if(dbw.isExistTable(tableName,false))
				 dbw.dropTable(tableName);
			 
				 Field field=null;
				 Table table=new Table(tableName);
				 
				 field=new Field("A0100","A0100");
				 field.setDatatype(DataType.STRING);
				 field.setLength(30);
				 table.addField(field);						
				 field=new Field("A00Z1","A00Z1");
				 field.setDatatype(DataType.INT);
				 field.setLength(10);
				 table.addField(field);	
				 field=new Field("A00Z0","A00Z0");
				 field.setDatatype(DataType.DATE);
				 table.addField(field);
				 
				 field=new Field("A00Z1_O","A00Z1_O");
				 field.setDatatype(DataType.INT);
				 field.setLength(10);
				 table.addField(field);	
				 
				 for(int i=0;i<relationfieldList.size();i++)
				 {
					 String itemid=(String)relationfieldList.get(i);
					 FieldItem item=DataDictionary.getFieldItem(itemid.toLowerCase());
					 field=new Field(itemid,itemid);
					 if("D".equalsIgnoreCase(item.getItemtype()))
						 field.setDatatype(DataType.DATE);
					 if("A".equalsIgnoreCase(item.getItemtype()))
						 field.setDatatype(DataType.STRING);
					 field.setLength(item.getItemlength());
					 table.addField(field);	  
					 if(Sql_switcher.searchDbServer()==2)
					 {
						 if("D".equalsIgnoreCase(item.getItemtype()))
							 relation_str+=" and "+Sql_switcher.isnull(Sql_switcher.dateToChar("a1."+itemid,"YYYY-MM-DD"),"'-'")+"="+Sql_switcher.isnull(Sql_switcher.dateToChar(destname+"."+itemid,"YYYY-MM-DD"),"'-'") ; 
						 else
							 relation_str+=" and "+Sql_switcher.isnull("a1."+itemid,"'-'")+"="+Sql_switcher.isnull(destname+"."+itemid,"'-'") ; 
					 }
					 else	
						   relation_str+=" and "+Sql_switcher.isnull("a1."+itemid,"''")+"="+Sql_switcher.isnull(destname+"."+itemid,"''"); 
				 } 
				 dbw.createTable(table);
			
			
			
			
			
			String tempName="t#"+this.userview.getUserName()+"_gz"; //this.userview.getUserName()+"_GzTempTable";
			
			String axxz0=setid+"z0";
			String axxz1=setid+"z1";
			String[] temp=strym.split("-");
			dao.update("delete from "+tableName);
			StringBuffer sql=new StringBuffer("insert into "+tableName+" (A0100,a00z0,a00z1"+relationFieldStr+")");
			sql.append(" select "+this.gz_tablename+".A0100,"+this.gz_tablename+".a00z0,"+this.gz_tablename+".a00z1"+relationFieldStr+" from "+this.gz_tablename+this._withNoLock+" , "+tempName);
			sql.append(" where "+this.gz_tablename+".a0100="+tempName+".a0100 and lower("+this.gz_tablename+".nbase)='"+nbase.toLowerCase()+"'");
			sql.append("  "+filterWhl+"   and exists (select null from "+nbase+"A01 where "+nbase+"A01.a0100="+this.gz_tablename+".a0100 )    and  "+Sql_switcher.year(this.gz_tablename+".a00z0")+"="+temp[0]+" and "+Sql_switcher.month(this.gz_tablename+".a00z0")+"="+temp[1]);
			dbw.execute(sql.toString());
			sql.setLength(0);
			
			if("1".equalsIgnoreCase(royalty_valid)&&setid.equalsIgnoreCase(royalty_setid))
			{
				 
				sql.setLength(0);
				sql.append("insert into "+destname+" (A0100,I9999"+strIns+","+axxz0+","+axxz1+relationFieldStr+") ");
				sql.append(" select a1.a0100,a2.i9999+1"+strvalue+",a1.a00z0,a1.a00z1"+relationFieldStr +" from ");
				sql.append(tableName+" a1, ");
				sql.append("( select a0100,i9999 from "+destname+" a"+this._withNoLock+" where a.i9999=(select max(b.i9999) from "+destname+" b where a.a0100=b.a0100 ) ) a2 ");
				sql.append("where a1.a0100=a2.a0100 and not exists ( ");
				sql.append(" select null from "+destname+" where a1.a0100="+destname+".a0100 "+relation_str+" ) ");
				dbw.execute(sql.toString());
				
				sql.setLength(0);
				sql.append("delete from "+tableName+" where exists (select null from "+destname+" where ");
				sql.append(tableName+".a0100= "+destname+".a0100 ) ");
				dbw.execute(sql.toString());
				
				sql.setLength(0);
				sql.append("insert into "+destname+" (A0100,I9999"+strIns+","+axxz0+","+axxz1+relationFieldStr+") ");
				sql.append(" select a1.a0100,1"+strvalue+",a1.a00z0,a1.a00z1"+relationFieldStr+" from ");
				sql.append(tableName+" a1 "); 
				dbw.execute(sql.toString());
			} 
			else
			{
				//在临时表中删除子集中已经存在的人员记录
				sql.append("delete from "+tableName+" where exists (select null from "+destname+" where "+tableName+".a0100= "+destname+".a0100 ");
				sql.append(" and "+tableName+".a00z0= "+destname+"."+axxz0+" and "+tableName+".a00z1= "+destname+"."+axxz1+"  ");
				sql.append("   ) ");
				dbw.execute(sql.toString());
				
				//插入新增的人员记录取最大的i9999
				sql.setLength(0);
				sql.append("insert into "+destname+" (A0100,I9999"+strIns+","+axxz0+","+axxz1+") ");
				sql.append(" select a1.a0100,a2.i9999+1"+strvalue+",a1.a00z0,a1.a00z1 from ");
				sql.append(tableName+" a1, ");
				sql.append("( select a0100,i9999 from "+destname+" a where a.i9999=(select max(b.i9999) from "+destname+" b where a.a0100=b.a0100 ) ) a2 ");
				sql.append("where a1.a0100=a2.a0100 ");
				dbw.execute(sql.toString());
		
				//在临时表中删除 子集中存在的人员
				sql.setLength(0);
				sql.append("delete from "+tableName+" where exists (select null from "+destname+" where ");
				sql.append(tableName+".a0100= "+destname+".a0100 ) ");
				dbw.execute(sql.toString());
				
				//剩下的子集中没有的全部插入子集
				sql.setLength(0);
				sql.append("insert into "+destname+" (A0100,I9999"+strIns+","+axxz0+","+axxz1+") ");
				sql.append(" select a1.a0100,1"+strvalue+",a1.a00z0,a1.a00z1 from ");
				sql.append(tableName+" a1 "); 
				dbw.execute(sql.toString());
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	
	
	/**
	 * 薪资数据提交至档案库中去...(优化提交方案)
	 * @param setlist			子集列表
	 * @param updatelist		更新指标串列表
	 * @param typelist			数据提交方式列表
	 * @return
	 * @throws GeneralException
	 */
	private boolean submitDataInArchive_2(ArrayList setlist,ArrayList updatelist,ArrayList typelist,HashMap itemUptype)throws GeneralException
	{
		boolean bflag=true;
		try
		{
			String a0100=null,cbase=null,strym=null,strcount=null,dessetid=null,supdate=null;
			ContentDAO dao=new ContentDAO(this.conn);
			int ni9999=-1;
			int add_flag=0;
		//	RowSet rset=null;
			StringBuffer buf=new StringBuffer();
			/**取得人员库前缀列表*/
			String dbpres=this.templatevo.getString("cbase");
			/**应用库前缀*/
			String[] dbarr=StringUtils.split(dbpres, ",");
			DbWizard dbw=new DbWizard(this.conn);
	
			//判断提交的薪资类别是否包含了年月变化子集,并且为新增方式
			boolean isYMaddSet=isymChangeSet(setlist,typelist);
			String strNow=Sql_switcher.sqlNow();
			///////////////////////////////////////////////////////////////
			ArrayList z0z1List=getZ0z1List(this.gz_tablename);
			for(int i=0;i<dbarr.length;i++)//遍历人员库
			{
				
				cbase=dbarr[i];
				/** 处理只有一条薪资记录的用户一般子集数据 */
				dealwithSingleRecord(setlist,updatelist,typelist,dbarr,i,dbw,itemUptype,strNow);
				String tempName="t#"+this.userview.getUserName()+"_gz"; //this.userview.getUserName()+"_GzTempTable";
			//	if(Sql_switcher.searchDbServer()==2)
				//{
				/*	String singleRecord_where="(select count(a0100) c,a0100 from "+this.gz_tablename+" where "; 
					singleRecord_where+=" lower("+this.gz_tablename+".NBASE)='"+cbase.toLowerCase()+"'  "+filterWhl+"   group by a0100  having count(a0100)=1 ) aa";
				     if(dbw.isExistTable(tempName,false))
				    	 dao.update(" drop table "+tempName);
				     StringBuffer sql=new StringBuffer("");
				     if(Sql_switcher.searchDbServer()==2)
				    	 sql.append("create table "+tempName+" as ");
				     sql.append("select "+this.gz_tablename+".a0100,"+this.gz_tablename+".a00z0,"+this.gz_tablename+".add_flag,"+this.gz_tablename+".a00z1");
				     if(Sql_switcher.searchDbServer()!=2)
				    	 sql.append(" into "+tempName);
				     sql.append(" from "+this.gz_tablename+","+singleRecord_where);
				     sql.append(" where aa.a0100="+this.gz_tablename+".a0100   "+filterWhl+" and exists (select null from "+cbase+"A01 where "+cbase+"A01.a0100="+this.gz_tablename+".a0100 )    and lower("+this.gz_tablename+".NBASE)='"+cbase.toLowerCase()+"' ");
				     dao.update(sql.toString());*/
			//	} 
				/** 处理只有一条薪资记录的用户年月子集数据 */
				for(int j=0;j<z0z1List.size();j++)
				{
					LazyDynaBean abean=(LazyDynaBean)z0z1List.get(j);
					strym=(String)abean.get("strym");
					
					
				    String[] temp=strym.split("-"); 
					String singleRecord_where="(select count(a0100) c,a0100 from "+this.gz_tablename+" where "; 
					singleRecord_where+=" lower("+this.gz_tablename+".NBASE)='"+cbase.toLowerCase()+"'  "+filterWhl+" ";
					singleRecord_where+=" and  "+Sql_switcher.year(this.gz_tablename+".a00z0")+"="+temp[0]+" and "+Sql_switcher.month(this.gz_tablename+".a00z0")+"="+temp[1]+"   group by a0100  having count(a0100)=1 ) aa";
				     if(dbw.isExistTable(tempName,false))
				     {
				    	 dbw.dropTable(tempName); 
				     }
				     StringBuffer sql=new StringBuffer("");
				     if(Sql_switcher.searchDbServer()==2)
				    	 sql.append("create table "+tempName+" as ");
				     sql.append("select "+this.gz_tablename+".a0100,"+this.gz_tablename+".a00z0,"+this.gz_tablename+".add_flag,"+this.gz_tablename+".a00z1");
				     if(Sql_switcher.searchDbServer()!=2)
				    	 sql.append(" into "+tempName);
				     sql.append(" from "+this.gz_tablename+","+singleRecord_where);
				     sql.append(" where aa.a0100="+this.gz_tablename+".a0100 and  ");
				     sql.append(Sql_switcher.year(this.gz_tablename+".a00z0")+"="+temp[0]+" and "+Sql_switcher.month(this.gz_tablename+".a00z0")+"="+temp[1]);
				     sql.append(filterWhl+" and exists (select null from "+cbase+"A01 where "+cbase+"A01.a0100="+this.gz_tablename+".a0100 )    and lower("+this.gz_tablename+".NBASE)='"+cbase.toLowerCase()+"' ");
				     dao.update(sql.toString());
					dealwithSingleRecord_ym(setlist,true,isYMaddSet,updatelist,typelist,cbase,dbw,itemUptype,strNow,strym);	
				} 
				/**对一个用户同时有多条薪资记录的一般子集数据进行处理 (一般子集直接更新，年月子集生成框架)*/
				dealwithMulRecord(setlist,isYMaddSet,updatelist,typelist, dbarr,i,dbw,itemUptype);
				for(int j=0;j<setlist.size();j++)
				{
					String setid=(String)setlist.get(j);
					if("A00".equalsIgnoreCase(setid))
						continue;					
					/**(0,1,2,3)=(更新,新增,不变,更新(薪资重发时))*/
					String type=(String)typelist.get(j);
					if("3".equals(type))
						type="0";
					if("2".equalsIgnoreCase(type))//当前记录不变
						continue;
					/**子集未构库不提交*/
					FieldSet fieldset=DataDictionary.getFieldSetVo(setid);
					if(fieldset==null|| "0".equalsIgnoreCase(fieldset.getUseflag()))
						continue;					
					/**(0,1,2)=(一般,按月变化,按年变化)*/
					int nflag=Integer.parseInt(fieldset.getChangeflag());
					switch(setid.charAt(0))
					{
					case 'A'://人员库
						if("A01".equalsIgnoreCase(setid))
						{
								updateSQL(gz_tablename,cbase,setid,(String)updatelist.get(j),0,itemUptype);
						}
						else//如果子集，则只对按月或年变化的子集进行处理.
						{
							if(nflag>0)
							{
								if(Sql_switcher.searchDbServer()!=2)  //不为oracle
									updateSQL_history(gz_tablename,cbase,setid,(String)updatelist.get(j),nflag,itemUptype);
								else
									updateSQL(gz_tablename,cbase,setid,(String)updatelist.get(j),nflag,itemUptype);
							}
						}
						break;
					}
				}
			}
			
		//	if(Sql_switcher.searchDbServer()==2)
			{
			/*	String tableName=this.userview.getUserName()+"_maxZ1";
				if(dbw.isExistTable(tableName,false))
					dbw.dropTable(tableName);*/
			/*	tableName=this.userview.getUserName()+"_maxZ1_2";
				if(dbw.isExistTable(tableName,false))
					dbw.dropTable(tableName);*/
			/*	tableName=this.userview.getUserName()+"_GzTempTable";
				if(dbw.isExistTable(tableName,false))
					dbw.dropTable(tableName); */
			}
		}
		catch(Exception ex)
		{
			bflag=false;
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);				
		}
		return bflag;
	}
	
	
	
	//---------------------------------------  start----------------------------------------------------------
	
	
	/**
	 * 处理 如果子集中有相应次数，自动加1 （针对orcale）
	 * @param strym
	 * @param nbase
	 * @param dbw
	 * @param dao
	 * @return
	 */
	public String executeMaxZ1Table_history(ArrayList userFlagList,String strym,String nbase,DbWizard dbw,ContentDAO dao,ArrayList setlist,ArrayList typelist)
	{
		String tableName="t#"+this.userview.getUserName()+"_gz_1"; //this.userview.getUserName()+"_maxZ1";
		String tableName2="t#"+this.userview.getUserName()+"_gz_2"; //this.userview.getUserName()+"_maxZ1_2";
		try
		{
		//	 if(dbw.isExistTable(tableName,false))
				 dbw.dropTable(tableName);
			 {
				 Field field=null;
				 Table table=new Table(tableName);
				 
				 field=new Field("A0100","A0100");
				 field.setDatatype(DataType.STRING);
				 field.setLength(30);
				 table.addField(field);						
				 field=new Field("A00Z1","A00Z1");
				 field.setDatatype(DataType.INT);
				 field.setLength(10);
				 table.addField(field);	
				 field=new Field("A00Z0","A00Z0");
				 field.setDatatype(DataType.DATE);
				 table.addField(field);
				 
				 dbw.createTable(table);
			}
			if(!dbw.isExistField(tableName,"A00Z1_O",false))
			{
				Table table=new Table(tableName);
				Field field=new Field("A00Z1_O","A00Z1_O");
				field.setDatatype(DataType.INT);
				field.setLength(10);
				table.addField(field);	
				dbw.addColumns(table);
			}
				
			dbw.execute("delete from "+tableName);
			 
		 	 String salaryhistory_tmp="t#"+this.userview.getUserName()+"_gz_3";
			 String tempName="t#"+this.userview.getUserName()+"_gz";  //this.userview.getUserName()+"_GzTempTable";
			 StringBuffer sql=new StringBuffer("insert into "+tableName+" (a0100,a00z1,A00Z1_O)");
			 sql.append(" select "+tempName+".a0100,0,"+tempName+".a00z1 from "+tempName);
			 sql.append(" where add_flag=0 or add_flag is null  ");
			 dao.update(sql.toString());
			
		//	 if(dbw.isExistTable(tableName2,false))
				 dbw.dropTable(tableName2);
		//	 if(!dbw.isExistTable(tableName2,false))
			 {
				 if(Sql_switcher.searchDbServer()==2)
					 dbw.execute("create table "+tableName2+" as  select a0100,a00z1 from "+this.gz_tablename+" where 1=2");	
				 else
					 dbw.execute("select a0100,a00z1  into "+tableName2+"  from "+this.gz_tablename+this._withNoLock+" where 1=2");
			 }	
			 
			 String[] temp=strym.split("-");
			 for(int j=0;j<setlist.size();j++)
			 {
					String setid=(String)setlist.get(j);
					/**(0,1,2,3)=(更新,新增,不变,更新(薪资重发时))*/
					String type=(String)typelist.get(j);
					if("2".equalsIgnoreCase(type)|| "0".equalsIgnoreCase(type))//当前记录不变
						continue;
					if(setid.charAt(0)=='A')
					{
						if("A00".equalsIgnoreCase(setid))
							continue;
						FieldSet fieldset=DataDictionary.getFieldSetVo(setid);
						if(fieldset==null|| "0".equalsIgnoreCase(fieldset.getUseflag()))
							continue;
						int nflag=Integer.parseInt(fieldset.getChangeflag());
						if(nflag==1||nflag==2)
						{ 
								dbw.execute("delete from "+tableName2);
								sql.setLength(0);
								sql.append("insert into "+tableName2+" (a00z1,a0100) select  MAX("+setid+"Z1) ,a0100 from "+nbase+setid+this._withNoLock+" where  a0100 in (select a0100 from "+tableName+")  and  "+Sql_switcher.year(setid+"z0")+"="+temp[0]+" and "+Sql_switcher.month(setid+"z0")+"="+temp[1]+"	GROUP BY A0100 ");
								dbw.execute(sql.toString());
								sql.setLength(0);
								sql.append("update "+tableName+" set a00z1=(select a00z1 from  ");
								sql.append(tableName2+"  where "+tableName2+".a0100="+tableName+".a0100 and "+tableName2+".a00z1>"+tableName+".a00z1 ");
								sql.append(" ) where exists( select  null from "+tableName2+" where "+tableName2+".a0100="+tableName+".a0100 and "+tableName2+".a00z1>"+tableName+".a00z1 ");
								sql.append(" )  ");
								dbw.execute(sql.toString());
						}
					}
			 }
		
			 dbw.execute("delete from "+tableName2);
			 sql.setLength(0);
			 sql.append("insert into "+tableName2+" (a00z1,a0100) select  MAX(a00Z1) ,a0100 from salaryhistory"+this._withNoLock+" where  a0100 in (select a0100 from "+tableName+") ");
			 sql.append(" and  "+Sql_switcher.year("a00z0")+"="+temp[0]+" and "+Sql_switcher.month("a00z0")+"="+temp[1]+" and salaryid="+this.salaryid);
			 sql.append(" and not exists (select null from "+salaryhistory_tmp+" where salaryhistory.a0100="+salaryhistory_tmp+".a0100 and lower(salaryhistory.nbase)=lower("+salaryhistory_tmp+".nbase) ");
			 sql.append(" and salaryhistory.a00z0="+salaryhistory_tmp+".a00z0 and  salaryhistory.a00z1="+salaryhistory_tmp+".a00z1 )");
			 sql.append(" and  lower(nbase)='"+nbase.toLowerCase()+"'	GROUP BY A0100 ");
			 dbw.execute(sql.toString());
			 sql.setLength(0);
			 sql.append("update "+tableName+" set a00z1=(select a00z1 from  ");
			 sql.append(tableName2+"  where "+tableName2+".a0100="+tableName+".a0100 and "+tableName2+".a00z1>"+tableName+".a00z1 ");
			 sql.append(" ) where   exists( select  null from "+tableName2+" where "+tableName2+".a0100="+tableName+".a0100 and "+tableName2+".a00z1>"+tableName+".a00z1 ");
			 sql.append(" )  ");
			 dbw.execute(sql.toString());
		 
			 HashMap tabMap=new HashMap(); 
			 ArrayList tempList=null;
			 sql.setLength(0);
			 sql.append("select a.a00z1+1,"+this.gz_tablename+".a0100,"+this.gz_tablename+".a00z1,"+this.gz_tablename+".a00z0,"+this.gz_tablename+".userflag  from "+tableName+" a,"+this.gz_tablename+this._withNoLock);
			 sql.append(" where a.a0100="+this.gz_tablename+".a0100 and lower("+this.gz_tablename+".nbase)='"+nbase.toLowerCase()+"'  "+filterWhl+"   and a.a00z1>="+this.gz_tablename+".a00z1 ");
			 RowSet rowSet=dao.search(sql.toString());
			
			 while(rowSet.next())
			 {
				    tempList=new ArrayList();
					tempList.add(new Integer(rowSet.getInt(1)));
					tempList.add(rowSet.getString(2));
					tempList.add(new Integer(temp[0]));
					tempList.add(new Integer(temp[1]));
					tempList.add(new Integer(rowSet.getInt(3)));
					String userflag=rowSet.getString("userflag").toLowerCase();
				 	if(tabMap.get(userflag)==null)
				 	{
				 		ArrayList list=new ArrayList();
				 		list.add(tempList);
				 		tabMap.put(userflag,list);
				 	}
				 	else
				 	{
				 		ArrayList list=(ArrayList)tabMap.get(userflag);
				 		list.add(tempList);
				 	}
			 }
			 
			sql.setLength(0);
			sql.append("update "+this.gz_tablename+" set a00z1=(select "+tableName+".a00z1+1 from  "+tableName);
			sql.append(" where "+this.gz_tablename+".a0100="+tableName+".a0100 and "+tableName+".a00z1>="+this.gz_tablename+".a00z1  )");
			sql.append(" where exists (select null from "+tableName+" where "+this.gz_tablename+".a0100="+tableName+".a0100 and "+tableName+".a00z1>="+this.gz_tablename+".a00z1  )  "+filterWhl+"   and lower("+this.gz_tablename+".nbase)='"+nbase.toLowerCase()+"'");
			dbw.execute(sql.toString());
			dbw.execute("update "+this.gz_tablename+" set  add_flag=1 where exists (select null from "+tableName+" where "+tableName+".a0100="+gz_tablename+".a0100)   "+filterWhl+"   and lower(nbase)='"+nbase.toLowerCase()+"'  and "+Sql_switcher.year("a00z0")+"="+temp[0]+" and "+Sql_switcher.month("a00z0")+"="+temp[1]);
			    			
			 
			Set set=tabMap.keySet();
			for(Iterator t=set.iterator();t.hasNext();)
		    {
		    	String key=((String)t.next()).toLowerCase(); 
		    	ArrayList updateList=(ArrayList)tabMap.get(key);
			    String tabname=key+"_salary_"+this.salaryid;
			    dbw.execute("update "+tabname+" set  add_flag=1 where  exists (select null from "+tableName+" where "+tableName+".a0100="+tabname+".a0100)      and lower(nbase)='"+nbase.toLowerCase()+"'  and "+Sql_switcher.year("a00z0")+"="+temp[0]+" and "+Sql_switcher.month("a00z0")+"="+temp[1]); 
				
			    String sql2="update gz_tax_mx  set a00z1=? where lower(nbase)='"+nbase.toLowerCase()+"' and lower(userflag)='"+key+"' and a0100=? "
							+" and "+Sql_switcher.year("a00z0")+"=? and "+Sql_switcher.month("a00z0")+"=? and a00z1=? and salaryid="+this.salaryid;
				dao.batchUpdate(sql2, updateList);
							
				String sql1="update "+tabname+"  set a00z1=? where lower(nbase)='"+nbase.toLowerCase()+"' and a0100=? "
							+" and "+Sql_switcher.year("a00z0")+"=? and "+Sql_switcher.month("a00z0")+"=? and a00z1=? and lower(userflag)='"+key+"' ";
				dao.batchUpdate(sql1, updateList);	 
			 } 
			 if(rowSet!=null)
				rowSet.close();
			 
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return tableName;
	}
	
	public void synchronousZ1_history(String strym,String strcount,int z1,String a0100,String nbase,ArrayList updateList,String userflag)
	{
		String year=strym.substring(0, 4);
		String month=strym.substring(5, 7);
	/*	String sql="update su_salary_11 set a00z1=?,add_flag=1 where low(nbase)='"+nbase.toLowerCase()+"' and a0100=? "
			+" and "+Sql_switcher.year("a00z0")+"=? and "+Sql_switcher.month("a00z0")+"=? and a00z1=? ";*/
		ArrayList tempList=new ArrayList();
		tempList.add(new Integer(z1));
		tempList.add(userflag);
		tempList.add(a0100);
		tempList.add(new Integer(year));
		tempList.add(new Integer(month));
		tempList.add(new Integer(strcount));
		updateList.add(tempList);
	}
	
	
	/**对一个用户同时有多条薪资记录的一般子集数据进行处理*/
	public void dealwithMulRecord_history2(ArrayList setlist,boolean isYMaddSet,ArrayList updatelist,ArrayList typelist,String[] dbarr,int i,DbWizard dbw,HashMap itemUptype)
	{
		
		
		
		String a0100=null,cbase=null,strym=null,strcount=null,dessetid=null,supdate=null;
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			RowSet rset=null;
			String  royalty_valid=this.ctrlparam.getValue(SalaryCtrlParamBo.ROYALTIES,"valid");
			String royalty_relation_fields=this.ctrlparam.getValue(SalaryCtrlParamBo.ROYALTIES,"relation_fields");
			ArrayList relationfieldList=new ArrayList();
			String royalty_setid=this.ctrlparam.getValue(SalaryCtrlParamBo.ROYALTIES,"setid");
			StringBuffer relationFieldStr=new StringBuffer("");
			String relation_str="";
			 //数据提交入库不判断子集及指标权限
			String subNoPriv=getLprogramAttri("subNoPriv",SalaryLProgramBo.CONFIRM_TYPE);
			if(subNoPriv==null||subNoPriv.trim().length()==0)
		 		subNoPriv="0";
			
			if("1".equalsIgnoreCase(royalty_valid)&&royalty_relation_fields.length()>0)
			{
				String[] temps=royalty_relation_fields.toLowerCase().split(",");
				for(int n=0;n<temps.length;n++)
				{
					if(temps[n].trim().length()>0&&DataDictionary.getFieldItem(temps[n].trim())!=null)
					{ 
						relationfieldList.add(DataDictionary.getFieldItem(temps[n].toLowerCase()));
						relationFieldStr.append(","+temps[n]);
					}
				}
			}  
			cbase=dbarr[i];
		
			
			String tempName="t#"+this.userview.getUserName()+"_gz";  
	//		if(dbw.isExistTable(tempName,false))
			{
				 dbw.dropTable(tempName);
				//dao.update(" drop table "+tempName);
			}
			StringBuffer sql0=new StringBuffer("");
			if(Sql_switcher.searchDbServer()==2)
				sql0.append("create table "+tempName+" as ");
			sql0.append("select aa.a0100 ");
			if(Sql_switcher.searchDbServer()!=2)
				sql0.append(" into "+tempName);
			sql0.append(" from  (select count(a0100) c,a0100 from salaryhistory"+this._withNoLock+" where  1=1  "+filterWhl+"   and lower(salaryhistory.NBASE)='"+cbase.toLowerCase()+"'  group by a0100  having count(a0100)>1 ) aa");
			dao.update(sql0.toString());
			String singleRecord_where="select a0100 from "+tempName;
	//	    String singleRecord_where="select aa.a0100 from  (select count(a0100) c,a0100 from salaryhistory where  1=1  "+filterWhl+"   and lower(salaryhistory.NBASE)='"+cbase.toLowerCase()+"'  group by a0100  having count(a0100)>1 ) aa";
			
			
			
			
			
			
			StringBuffer buf=new StringBuffer("");
			if("1".equals(royalty_valid)&&royalty_relation_fields.length()>0)
				buf.append("select * from ");
			else
				buf.append("select a0100,nbase,a00z0,a00z1,add_flag,userflag from ");
			buf.append(this.gz_tablename);
			buf.append(" where a0100 in ("+singleRecord_where+") "+filterWhl+"  and lower("+this.gz_tablename+".NBASE)='"+cbase.toLowerCase()+"'  and exists (select null from "+cbase+"A01 where "+cbase+"A01.a0100="+this.gz_tablename+".a0100 )  ");
			 
			rset=dao.search(buf.toString()+" order by a00z0,a00z1"); 
			ArrayList updateList=new ArrayList();
			int ni9999=-1;
			int add_flag=0;
			SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
			
			while(rset.next())
			{
				int num=0;
				a0100=rset.getString("a0100");
				if(rset.getString("add_flag")!=null)
					add_flag=rset.getInt("add_flag");
				else
					add_flag=0;
				String userflag=rset.getString("userflag");
				
				//cbase=rset.getString("nbase");
				strym=PubFunc.FormatDate(rset.getDate("a00z0"), "yyyy-MM-dd");
				strcount=rset.getString("a00z1");
				String ori_count=strcount;
				
				/*查找薪资类别项目涉及各年月变化子集本月最大次数加1的值更新为临时表中对应记录的
				* 归属次数并将工资数据临时表 中记录的add_flag值置为 1，当记录 add_flag值为1时，按旧的程序处理逻辑。*/
				if(isYMaddSet&&add_flag==0&&num==0)
				{
								int z1=getNewA00z1(setlist,strym,dbarr[i],a0100,typelist);
								num++;
								if(Integer.parseInt(strcount)<z1)
								{
									int history_z1=getNewA00z1_history(strym,dbarr[i],a0100,strcount);
									if(history_z1>Integer.parseInt(strcount)&&history_z1>z1)
										z1=history_z1;
									synchronousZ1_history(strym,strcount,z1,a0100,dbarr[i],updateList,userflag.toLowerCase());
									strcount=String.valueOf(z1);
								}
				}
				
				for(int j=0;j<setlist.size();j++)
				{
					String setid=(String)setlist.get(j);
					if("A00".equalsIgnoreCase(setid))
						continue;
					String fields=(String)updatelist.get(j);
					/**(0,1,2,3)=(更新,新增,不变,更新(薪资重发时))*/
					String type=(String)typelist.get(j);
					
					if("1".equals(royalty_valid)&&royalty_setid.equalsIgnoreCase(setid))
						type="0";
					if("2".equalsIgnoreCase(type))//当前记录不变
						continue;
					/**子集未构库不提交*/
					FieldSet fieldset=DataDictionary.getFieldSetVo(setid);
					if(fieldset==null|| "0".equalsIgnoreCase(fieldset.getUseflag()))
						continue;
					if("0".equals(subNoPriv))
					{
						if(!"2".equals(this.userview.analyseTablePriv(setid.toUpperCase())))
							continue;
					}
					/**(0,1,2)=(一般,按月变化,按年变化)*/
					int nflag=Integer.parseInt(fieldset.getChangeflag());
					int ntype=Integer.parseInt(type);
					
					
					
					switch(setid.charAt(0))
					{
					case 'A'://人员库
						if(!"A01".equalsIgnoreCase(setid))//处理非主集
						{
							dessetid=dbarr[i]+setid;
							supdate=(String)updatelist.get(j);
							
							HashMap values=new HashMap();
							if("1".equals(royalty_valid)&&royalty_setid.equalsIgnoreCase(setid))
							{
								FieldItem aitem=null;
								for(int n=0;n<relationfieldList.size();n++)
								{
									aitem=(FieldItem)relationfieldList.get(n);
									if("D".equalsIgnoreCase(aitem.getItemtype()))
									{
										if(rset.getDate(aitem.getItemid())==null)//||rset.getString(aitem.getItemid()).trim().length()==0)
											values.put(aitem.getItemid().toLowerCase(),"null");
										else
											values.put(aitem.getItemid().toLowerCase(),df.format(rset.getDate(aitem.getItemid())));
											
									}
									else if("A".equalsIgnoreCase(aitem.getItemtype()))
									{
										if(rset.getString(aitem.getItemid())==null||rset.getString(aitem.getItemid()).trim().length()==0)
											values.put(aitem.getItemid().toLowerCase(),"null");
										else
											values.put(aitem.getItemid().toLowerCase(),rset.getString(aitem.getItemid()));
									} 
								}
							}
							
							
							
							
							switch(ntype)
							{
							case 0://更新
								switch(nflag)
								{
									case 1://按月变化
									case 2://按年变化
										if("1".equals(royalty_valid)&&royalty_setid.equalsIgnoreCase(setid))
											ni9999=isHaveCurrentDateRecord_royalty(dessetid,setid,a0100,relationfieldList,values);
										else
											ni9999=isHaveCurrentDateRecord(dessetid, setid, a0100, nflag, strym, strcount);
										/**如果没有记录，则自动创建一条记录和上条记录完全一条*/
										if(ni9999==-1)
										{
											if("1".equals(royalty_valid)&&royalty_setid.equalsIgnoreCase(setid))
												ni9999= autoAppendRecord_royalty(dessetid,setid,a0100,nflag,strym,strcount,relationfieldList,values);
											else
												ni9999=autoAppendRecord(dessetid,setid,a0100,supdate,2,nflag,strym,strcount);
										}
										break;
									case 0://一般子集
										if("1".equals(royalty_valid)&&royalty_setid.equalsIgnoreCase(setid))
										{
											ni9999=isHaveCurrentDateRecord_royalty(dessetid,setid,a0100,relationfieldList,values);
											if(ni9999==-1)
												ni9999= autoAppendRecord_royalty(dessetid,setid,a0100,nflag,strym,strcount,relationfieldList,values);
										}
										else
											ni9999=DbNameBo.getPrimaryKey(dessetid, "I9999", " where a0100='"+a0100+"'", conn)-1;
										break;
								}
								break;
							case 1://追加记录
								switch(nflag)
								{
								case 1://按月变化
								case 2://按年变化
									ni9999=isHaveCurrentDateRecord(dessetid, setid, a0100, nflag, strym, strcount);
									/**如果没有记录，则自动创建一条记录和上条记录完全一条*/
									if(ni9999==-1)
										ni9999=autoAppendRecord(dessetid,setid,a0100,supdate,1,nflag,strym,strcount);
									break;
								case 0://一般子集
									ni9999=autoAppendRecord(dessetid,setid,a0100,supdate,1,nflag,strym,strcount);
									break;
								}
								break;
							}//操作方式
							if(nflag==0)//只有一般变化子集，才一个人一个人提交数据
							{
								
								String year=strym.substring(0, 4);
								String month=strym.substring(5, 7); 
								
								String value=getUpdateSQL(dessetid,this.gz_tablename,fields,nflag,itemUptype);
								StringBuffer strSWhere=new StringBuffer();
								StringBuffer strDWhere=new StringBuffer();
								if(Sql_switcher.searchDbServer()==Constant.ORACEL)
								{
									strSWhere.append(dessetid);
									strSWhere.append(".A0100='");
									strSWhere.append(a0100);
									strSWhere.append("' and ");
									strSWhere.append(dessetid);
									strSWhere.append(".I9999=");
									strSWhere.append(ni9999);
									 
									
									strDWhere.append(dessetid);
									strDWhere.append(".A0100='");
									strDWhere.append(a0100);
									strDWhere.append("' and ");
									strDWhere.append(dessetid);
									strDWhere.append(".I9999=");
									strDWhere.append(ni9999);	
									strDWhere.append(" and lower(");
									strDWhere.append(this.gz_tablename);
									strDWhere.append(".NBASE)='");
									strDWhere.append(cbase.toLowerCase()+"'  ");
									
									strDWhere.append(" and "+Sql_switcher.year(this.gz_tablename+".a00z0"));
									strDWhere.append("=");
									strDWhere.append(year);
									strDWhere.append(" and "+Sql_switcher.month(this.gz_tablename+".a00z0"));
									strDWhere.append("=");
									strDWhere.append(month);
									strDWhere.append(" and "+this.gz_tablename+".a00z1");
									strDWhere.append("=");
								//	strDWhere.append(strcount);		
									strDWhere.append(ori_count);	
									
									
									strDWhere.append(filterWhl);
									
								}
								else
								{
									strSWhere.append(dessetid);
									strSWhere.append(".A0100='");
									strSWhere.append(a0100);
									strSWhere.append("' and ");
									strSWhere.append(dessetid);
									strSWhere.append(".I9999=");
									strSWhere.append(ni9999);
									strSWhere.append(" and lower(");
									strSWhere.append(this.gz_tablename);
									strSWhere.append(".NBASE)='");
									strSWhere.append(cbase.toLowerCase()+"'  ");
									
									strSWhere.append(" and "+Sql_switcher.year(this.gz_tablename+".a00z0"));
									strSWhere.append("=");
									strSWhere.append(year);
									strSWhere.append(" and "+Sql_switcher.month(this.gz_tablename+".a00z0"));
									strSWhere.append("=");
									strSWhere.append(month);
									strSWhere.append(" and "+this.gz_tablename+".a00z1");
									strSWhere.append("="); 	
//									strDWhere.append(strcount);		
									strSWhere.append(ori_count);

									strSWhere.append(filterWhl);
									
									strDWhere.append(dessetid);
									strDWhere.append(".A0100='");
									strDWhere.append(a0100);
									strDWhere.append("' and ");
									strDWhere.append(dessetid);
									strDWhere.append(".I9999=");
									strDWhere.append(ni9999);	
								} 
								
								if("1".equals(royalty_valid)&&royalty_setid.equalsIgnoreCase(setid))
								{
									FieldItem aitem=null;
									for(int n=0;n<relationfieldList.size();n++)
									{
										aitem=(FieldItem)relationfieldList.get(n); 
										if(!"null".equalsIgnoreCase((String)values.get(aitem.getItemid())))
										{
												if("D".equalsIgnoreCase(aitem.getItemtype()))
													strDWhere.append(" and "+Sql_switcher.isnull(Sql_switcher.dateToChar(this.gz_tablename+"."+aitem.getItemid(),"YYYY-MM-DD"),"''")+"='"+(String)values.get(aitem.getItemid())+"' ");
												else
													strDWhere.append(" and "+Sql_switcher.isnull(this.gz_tablename+"."+aitem.getItemid(),"''")+"='"+(String)values.get(aitem.getItemid())+"' "); 
										}
										else
										{
												if(Sql_switcher.searchDbServer()==Constant.ORACEL)
													strDWhere.append(" and "+ this.gz_tablename+"."+aitem.getItemid() +" is null "); 
												else
													strDWhere.append(" and "+Sql_switcher.isnull(this.gz_tablename+"."+aitem.getItemid(),"''")+"='' "); 
										}
											 
									} 
								}
								
								
								
								dbw.updateRecord(dessetid, gz_tablename,dessetid+".A0100="+gz_tablename+".A0100", value, strSWhere.toString(), strDWhere.toString());
							}
						}//if 非主集
						
						break;
					}//子集
				}//for setlist 数据集loop end.
				
				if(!ori_count.equals(strcount))
				{
					String year=strym.substring(0, 4);
					String month=strym.substring(5, 7);
					String sql="update "+userflag+"_salary_"+this.salaryid+" set a00z1="+strcount+",add_flag=1 where lower(nbase)='"+dbarr[i].toLowerCase()+"' and a0100='"+a0100+"'"
					+" and "+Sql_switcher.year("a00z0")+"="+year+" and "+Sql_switcher.month("a00z0")+"="+month+" and a00z1="+ori_count;
					dao.update(sql);
				}
				
				
			}//for while end.
			if(rset!=null)
				rset.close();
//			同步 薪资临时表 及 历史表里的a00z1
			if(updateList.size()>0)
			{
			//	 String[] _temps=this.gz_tablename.split("_salary_");
			//	String sql="update "+this.gz_tablename+" set a00z1=?,add_flag=1 where lower(nbase)='"+dbarr[i].toLowerCase()+"' and a0100=? "
			//	+" and "+Sql_switcher.year("a00z0")+"=? and "+Sql_switcher.month("a00z0")+"=? and a00z1=? ";
				String sql2="update salaryhistory  set a00z1=?,add_flag=1 where lower(nbase)='"+dbarr[i].toLowerCase()+"' and lower(userflag)=? and a0100=? "
				+" and "+Sql_switcher.year("a00z0")+"=? and "+Sql_switcher.month("a00z0")+"=? and a00z1=? and salaryid="+this.salaryid;
		//		dao.batchUpdate(sql, updateList);
				dao.batchUpdate(sql2, updateList);
				
				sql2="update gz_tax_mx  set a00z1=? where lower(nbase)='"+dbarr[i].toLowerCase()+"' and lower(userflag)=?  and a0100=? "
				+" and "+Sql_switcher.year("a00z0")+"=? and "+Sql_switcher.month("a00z0")+"=? and a00z1=? and salaryid="+this.salaryid;
				dao.batchUpdate(sql2, updateList);
				
			}
			
		//	dao.update("update "+this.gz_tablename+" set  add_flag=1 where lower(nbase)='"+dbarr[i].toLowerCase()+"'  "+filterWhl);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	/**对一个用户同时有多条薪资记录的一般子集数据进行处理*/
	public void dealwithMulRecord_history(ArrayList setlist,boolean isYMaddSet,ArrayList updatelist,ArrayList typelist,String[] dbarr,int i,DbWizard dbw,HashMap itemUptype)
	{
		String a0100=null,cbase=null,strym=null,strcount=null,dessetid=null,supdate=null;
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			String  royalty_valid=this.ctrlparam.getValue(SalaryCtrlParamBo.ROYALTIES,"valid");
			String royalty_relation_fields=this.ctrlparam.getValue(SalaryCtrlParamBo.ROYALTIES,"relation_fields");
			ArrayList relationfieldList=new ArrayList();
			String royalty_setid=this.ctrlparam.getValue(SalaryCtrlParamBo.ROYALTIES,"setid");
			StringBuffer relationFieldStr=new StringBuffer("");
			String relation_str="";
			 //数据提交入库不判断子集及指标权限
			String subNoPriv=getLprogramAttri("subNoPriv",SalaryLProgramBo.CONFIRM_TYPE);
			if(subNoPriv==null||subNoPriv.trim().length()==0)
		 		subNoPriv="0";
			
			if("1".equalsIgnoreCase(royalty_valid)&&royalty_relation_fields.length()>0)
			{
				String[] temps=royalty_relation_fields.toLowerCase().split(",");
				for(int n=0;n<temps.length;n++)
				{
					if(temps[n].trim().length()>0&&DataDictionary.getFieldItem(temps[n].trim())!=null)
					{ 
						relationfieldList.add(DataDictionary.getFieldItem(temps[n].toLowerCase()));
						relationFieldStr.append(","+temps[n]);
					}
				}
			}  
			cbase=dbarr[i];
			String singleRecord_where="select aa.a0100 from  (select count(a0100) c,a0100 from salaryhistory where  1=1 "+this._history_condWhl+"  and lower(salaryhistory.NBASE)='"+cbase.toLowerCase()+"'  group by a0100  having count(a0100)>1 ) aa";
			StringBuffer buf=new StringBuffer("");
			if("1".equals(royalty_valid)&&royalty_relation_fields.length()>0)
				buf.append("select * from ");
			else
				buf.append("select a0100,nbase,a00z0,a00z1,add_flag from ");
			buf.append(this.gz_tablename);
			buf.append(" where a0100 in ("+singleRecord_where+") "+filterWhl+"  and lower("+this.gz_tablename+".NBASE)='"+cbase.toLowerCase()+"'  and exists (select null from "+cbase+"A01 where "+cbase+"A01.a0100="+this.gz_tablename+".a0100 )  ");
			RowSet rset=dao.search(buf.toString()+" order by a00z0,a00z1");
			ArrayList updateList=new ArrayList();
			int ni9999=-1;
			int add_flag=0;
			SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
			while(rset.next())
			{
				int num=0;
				a0100=rset.getString("a0100");
				if(rset.getString("add_flag")!=null)
					add_flag=rset.getInt("add_flag");
				else
					add_flag=0;
				
				//cbase=rset.getString("nbase");
				strym=PubFunc.FormatDate(rset.getDate("a00z0"), "yyyy-MM-dd");
				strcount=rset.getString("a00z1");
				
				
				
				/*查找薪资类别项目涉及各年月变化子集本月最大次数加1的值更新为临时表中对应记录的
				* 归属次数并将工资数据临时表 中记录的add_flag值置为 1，当记录 add_flag值为1时，按旧的程序处理逻辑。*/
				if(isYMaddSet&&add_flag==0&&num==0)
				{
								int z1=getNewA00z1(setlist,strym,dbarr[i],a0100,typelist);
								num++;
								if(Integer.parseInt(strcount)<z1)
								{
									int history_z1=getNewA00z1_history(strym,dbarr[i],a0100,strcount);
									if(history_z1>Integer.parseInt(strcount)&&history_z1>z1)
										z1=history_z1;
									synchronousZ1(strym,strcount,z1,a0100,dbarr[i],updateList);
									strcount=String.valueOf(z1);
								}
				}
				for(int j=0;j<setlist.size();j++)
				{
					String setid=(String)setlist.get(j);
					if("A00".equalsIgnoreCase(setid))
						continue;
					String fields=(String)updatelist.get(j);
					/**(0,1,2,3)=(更新,新增,不变,更新(薪资重发时))*/
					String type=(String)typelist.get(j);
					if("3".equals(type))
						type="0";
					
					if("1".equals(royalty_valid)&&royalty_setid.equalsIgnoreCase(setid))
						type="0";
					if("2".equalsIgnoreCase(type))//当前记录不变
						continue;
					/**子集未构库不提交*/
					FieldSet fieldset=DataDictionary.getFieldSetVo(setid);
					if(fieldset==null|| "0".equalsIgnoreCase(fieldset.getUseflag()))
						continue;
					if("0".equals(subNoPriv))
					{
						if(!"2".equals(this.userview.analyseTablePriv(setid.toUpperCase())))
							continue;
					}
					/**(0,1,2)=(一般,按月变化,按年变化)*/
					int nflag=Integer.parseInt(fieldset.getChangeflag());
					int ntype=Integer.parseInt(type);
					
					
					
					switch(setid.charAt(0))
					{
					case 'A'://人员库
						if(!"A01".equalsIgnoreCase(setid))//处理非主集
						{
							dessetid=dbarr[i]+setid;
							supdate=(String)updatelist.get(j);
							
							HashMap values=new HashMap();
							if("1".equals(royalty_valid)&&royalty_setid.equalsIgnoreCase(setid))
							{
								FieldItem aitem=null;
								for(int n=0;n<relationfieldList.size();n++)
								{
									aitem=(FieldItem)relationfieldList.get(n);
									if("D".equalsIgnoreCase(aitem.getItemtype()))
									{
										if(rset.getDate(aitem.getItemid())==null)//||rset.getString(aitem.getItemid()).trim().length()==0)
											values.put(aitem.getItemid().toLowerCase(),"null");
										else
											values.put(aitem.getItemid().toLowerCase(),df.format(rset.getDate(aitem.getItemid())));
											
									}
									else if("A".equalsIgnoreCase(aitem.getItemtype()))
									{
										if(rset.getString(aitem.getItemid())==null||rset.getString(aitem.getItemid()).trim().length()==0)
											values.put(aitem.getItemid().toLowerCase(),"null");
										else
											values.put(aitem.getItemid().toLowerCase(),rset.getString(aitem.getItemid()));
									} 
								}
							}
							
							
							
							
							switch(ntype)
							{
							case 0://更新
								switch(nflag)
								{
									case 1://按月变化
									case 2://按年变化
										if("1".equals(royalty_valid)&&royalty_setid.equalsIgnoreCase(setid))
											ni9999=isHaveCurrentDateRecord_royalty(dessetid,setid,a0100,relationfieldList,values);
										else
											ni9999=isHaveCurrentDateRecord(dessetid, setid, a0100, nflag, strym, strcount);
										/**如果没有记录，则自动创建一条记录和上条记录完全一条*/
										if(ni9999==-1)
										{
											if("1".equals(royalty_valid)&&royalty_setid.equalsIgnoreCase(setid))
												ni9999= autoAppendRecord_royalty(dessetid,setid,a0100,nflag,strym,strcount,relationfieldList,values);
											else
												ni9999=autoAppendRecord(dessetid,setid,a0100,supdate,2,nflag,strym,strcount);
										}
										break;
									case 0://一般子集
										if("1".equals(royalty_valid)&&royalty_setid.equalsIgnoreCase(setid))
										{
											ni9999=isHaveCurrentDateRecord_royalty(dessetid,setid,a0100,relationfieldList,values);
											if(ni9999==-1)
												ni9999= autoAppendRecord_royalty(dessetid,setid,a0100,nflag,strym,strcount,relationfieldList,values);
										}
										else
											ni9999=DbNameBo.getPrimaryKey(dessetid, "I9999", " where a0100='"+a0100+"'", conn)-1;
										break;
								}
								break;
							case 1://追加记录
								switch(nflag)
								{
								case 1://按月变化
								case 2://按年变化
									ni9999=isHaveCurrentDateRecord(dessetid, setid, a0100, nflag, strym, strcount);
									/**如果没有记录，则自动创建一条记录和上条记录完全一条*/
									if(ni9999==-1)
										ni9999=autoAppendRecord(dessetid,setid,a0100,supdate,1,nflag,strym,strcount);
									break;
								case 0://一般子集
									ni9999=autoAppendRecord(dessetid,setid,a0100,supdate,1,nflag,strym,strcount);
									break;
								}
								break;
							}//操作方式
							if(nflag==0)//只有一般变化子集，才一个人一个人提交数据
							{
								
								String year=strym.substring(0, 4);
								String month=strym.substring(5, 7); 
								
								String value=getUpdateSQL(dessetid,this.gz_tablename,fields,nflag,itemUptype);
								StringBuffer strSWhere=new StringBuffer();
								StringBuffer strDWhere=new StringBuffer();
								if(Sql_switcher.searchDbServer()==Constant.ORACEL)
								{
									strSWhere.append(dessetid);
									strSWhere.append(".A0100='");
									strSWhere.append(a0100);
									strSWhere.append("' and ");
									strSWhere.append(dessetid);
									strSWhere.append(".I9999=");
									strSWhere.append(ni9999);
									 
									
									strDWhere.append(dessetid);
									strDWhere.append(".A0100='");
									strDWhere.append(a0100);
									strDWhere.append("' and ");
									strDWhere.append(dessetid);
									strDWhere.append(".I9999=");
									strDWhere.append(ni9999);	
									strDWhere.append(" and lower(");
									strDWhere.append(this.gz_tablename);
									strDWhere.append(".NBASE)='");
									strDWhere.append(cbase.toLowerCase()+"'  ");
									
									strDWhere.append(" and "+Sql_switcher.year(this.gz_tablename+".a00z0"));
									strDWhere.append("=");
									strDWhere.append(year);
									strDWhere.append(" and "+Sql_switcher.month(this.gz_tablename+".a00z0"));
									strDWhere.append("=");
									strDWhere.append(month);
									strDWhere.append(" and "+this.gz_tablename+".a00z1");
									strDWhere.append("=");
									strDWhere.append(strcount);			
									 
									
									strDWhere.append(filterWhl);
									
								}
								else
								{
									strSWhere.append(dessetid);
									strSWhere.append(".A0100='");
									strSWhere.append(a0100);
									strSWhere.append("' and ");
									strSWhere.append(dessetid);
									strSWhere.append(".I9999=");
									strSWhere.append(ni9999);
									strSWhere.append(" and lower(");
									strSWhere.append(this.gz_tablename);
									strSWhere.append(".NBASE)='");
									strSWhere.append(cbase.toLowerCase()+"'  ");
									
									strSWhere.append(" and "+Sql_switcher.year(this.gz_tablename+".a00z0"));
									strSWhere.append("=");
									strSWhere.append(year);
									strSWhere.append(" and "+Sql_switcher.month(this.gz_tablename+".a00z0"));
									strSWhere.append("=");
									strSWhere.append(month);
									strSWhere.append(" and "+this.gz_tablename+".a00z1");
									strSWhere.append("=");
									strSWhere.append(strcount);		
									
									strSWhere.append(filterWhl);
									
									strDWhere.append(dessetid);
									strDWhere.append(".A0100='");
									strDWhere.append(a0100);
									strDWhere.append("' and ");
									strDWhere.append(dessetid);
									strDWhere.append(".I9999=");
									strDWhere.append(ni9999);	
								} 
								
								if("1".equals(royalty_valid)&&royalty_setid.equalsIgnoreCase(setid))
								{
									FieldItem aitem=null;
									for(int n=0;n<relationfieldList.size();n++)
									{
										aitem=(FieldItem)relationfieldList.get(n); 
										if(!"null".equalsIgnoreCase((String)values.get(aitem.getItemid())))
										{
												if("D".equalsIgnoreCase(aitem.getItemtype()))
													strDWhere.append(" and "+Sql_switcher.isnull(Sql_switcher.dateToChar(this.gz_tablename+"."+aitem.getItemid(),"YYYY-MM-DD"),"''")+"='"+(String)values.get(aitem.getItemid())+"' ");
												else
													strDWhere.append(" and "+Sql_switcher.isnull(this.gz_tablename+"."+aitem.getItemid(),"''")+"='"+(String)values.get(aitem.getItemid())+"' "); 
										}
										else
										{
												if(Sql_switcher.searchDbServer()==Constant.ORACEL)
													strDWhere.append(" and "+ this.gz_tablename+"."+aitem.getItemid() +" is null "); 
												else
													strDWhere.append(" and "+Sql_switcher.isnull(this.gz_tablename+"."+aitem.getItemid(),"''")+"='' "); 
										}
											 
									} 
								}
								
								
								
								dbw.updateRecord(dessetid, gz_tablename,dessetid+".A0100="+gz_tablename+".A0100", value, strSWhere.toString(), strDWhere.toString());
							}
						}//if 非主集
						break;
					}//子集
				}//for setlist 数据集loop end.
			}//for while end.
			if(rset!=null)
				rset.close();
//			同步 薪资临时表 及 历史表里的a00z1
			if(updateList.size()>0)
			{
				 String[] _temps=this.gz_tablename.split("_salary_");
				String sql="update "+this.gz_tablename+" set a00z1=?,add_flag=1 where lower(nbase)='"+dbarr[i].toLowerCase()+"' and a0100=? "
				+" and "+Sql_switcher.year("a00z0")+"=? and "+Sql_switcher.month("a00z0")+"=? and a00z1=? ";
				String sql2="update salaryhistory  set a00z1=? where lower(nbase)='"+dbarr[i].toLowerCase()+"' and lower(userflag)='"+_temps[0].toLowerCase()+"'  and a0100=? "
				+" and "+Sql_switcher.year("a00z0")+"=? and "+Sql_switcher.month("a00z0")+"=? and a00z1=? and salaryid="+this.salaryid;
				dao.batchUpdate(sql, updateList);
				dao.batchUpdate(sql2, updateList);
				
				sql2="update gz_tax_mx  set a00z1=? where lower(nbase)='"+dbarr[i].toLowerCase()+"' and lower(userflag)='"+_temps[0].toLowerCase()+"'  and a0100=? "
				+" and "+Sql_switcher.year("a00z0")+"=? and "+Sql_switcher.month("a00z0")+"=? and a00z1=? and salaryid="+this.salaryid;
				dao.batchUpdate(sql2, updateList);
				
			}
			dao.update("update "+this.gz_tablename+" set  add_flag=1 where lower(nbase)='"+dbarr[i].toLowerCase()+"'  "+filterWhl);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	

	/** 处理只有一条薪资记录的用户年月子集数据 */
	public void dealwithSingleRecord_ym_history(ArrayList userFlagList,ArrayList setlist,boolean flag,boolean isYMaddSet,ArrayList updatelist,ArrayList typelist,String cbase,DbWizard dbw,HashMap itemUptype,String strNow,String strym)
	{
		try
		{
		 
			//数据提交入库不判断子集及指标权限
			String subNoPriv=getLprogramAttri("subNoPriv",SalaryLProgramBo.CONFIRM_TYPE);
			if(subNoPriv==null||subNoPriv.trim().length()==0)
		 		subNoPriv="0";
		 		
			
			String[] temp=strym.split("-");
			ContentDAO dao=new ContentDAO(this.conn); 
			if(flag&&isYMaddSet)
			{ 
				try
				{
					executeMaxZ1Table_history(userFlagList,strym,cbase,dbw,dao,setlist,typelist);	
				}
				catch(Exception ee)
				{
					ee.printStackTrace();
				}
			}
		 
			
			//判断子集中是否有当年当月当次记录，没有则追加
			String strIns=",createtime,createusername";
			String strvalue=","+strNow+",'"+this.userview.getUserName()+"'";
			for(int j=0;j<setlist.size();j++)
			{
				String setid=(String)setlist.get(j);
				/**(0,1,2,3)=(更新,新增,不变,更新(薪资重发时))*/
				String type=(String)typelist.get(j);
				if("3".equals(type))
					type="0";
				if("2".equalsIgnoreCase(type))//当前记录不变
					continue;
				if("0".equals(subNoPriv))
				{
					if(!"2".equals(this.userview.analyseTablePriv(setid.toUpperCase())))
						continue;
				}
				String destname=cbase+setid;
				String axxz0=setid+"z0";
				String axxz1=setid+"z1";
				if(setid.charAt(0)=='A')
				{
					if("A00".equalsIgnoreCase(setid))
						continue;
					FieldSet fieldset=DataDictionary.getFieldSetVo(setid);
					if(fieldset==null|| "0".equalsIgnoreCase(fieldset.getUseflag()))
						continue;
					int nflag=Integer.parseInt(fieldset.getChangeflag());
					if(nflag==1||nflag==2)
					{ 
						{
							insertSetInfo(cbase,strym,dbw,dao,setid,strIns,strvalue);
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
	 * 薪资数据提交
	 * @param setlist				需要归档提交的数据集列表
	 * @param typelist				数据集提交类型列表
	 * @param items					更新指标集
	 * @param uptypes				更新方式
	 * @return 
	 * @throws GeneralException
	 */
	public boolean submitGzData2_history(ArrayList setlist,ArrayList typelist,String items,String uptypes,String userName,String sp_flag,String isRedo)throws GeneralException
	{
		boolean bflag=true;
		try
		{	
			ContentDAO dao=new ContentDAO(this.conn);
			/**求得对应子集的指标串,比如AXXX1,AXXX0*/
			ArrayList updatelist=getUpdateFields(setlist);
			/**薪资表数据->薪资历史表中去*/
		//	submitDataInHistory();
			/**薪资表数据->档案库*/
			HashMap itemUptype=getItemUpdateType(items,uptypes);
			submitDataInArchive_2_history(setlist,updatelist,typelist,itemUptype,sp_flag,isRedo);
			
			/**薪资表数据->薪资历史表中去*/
			submitDataInHistory();
			
			/**处理薪资补发情况*/
			updateBF();
			/**设置个税明表记录状态*/
			setTaxflag();
			/**把薪资发放记录表中起草状态的记录置为结束状态06*/
			/** 修改工资数据表中记录状态 */
			updateSalaryDataStatus("06");
			StringBuffer buf=new StringBuffer();
			
			
			/**
			 * =01 正在处理
			 * =05 执行状态
			 * =06 结束状态（薪资数据已提交）
			 */
			RowSet rowSet=dao.search("select count(*) from "+this.gz_tablename+" where ( sp_flag<>'06' or sp_flag is null )");
			int num=0;
			if(rowSet.next())
				num=rowSet.getInt(1);
			if(num==0)
			{
				buf.append("update gz_extend_log set sp_flag='06',isredo=0 where ( sp_flag='01' or sp_flag='05' ) and salaryid=? and upper(username)=?");
				ArrayList paralist=new ArrayList();
				paralist.add(String.valueOf(this.salaryid));
				paralist.add(userName.toUpperCase());
				dao.update(buf.toString(),paralist);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			bflag=false;
			throw GeneralExceptionHandler.Handle(ex);			
		}
		return bflag;		
	}
	
	
	
	
	/**
	 * 薪资数据提交至档案库中去...(优化提交方案)
	 * @param setlist			子集列表
	 * @param updatelist		更新指标串列表
	 * @param typelist			数据提交方式列表
	 * @return
	 * @throws GeneralException
	 */
	private boolean submitDataInArchive_2_history(ArrayList setlist,ArrayList updatelist,ArrayList typelist,HashMap itemUptype,String sp_flag,String isRedo)throws GeneralException
	{
		boolean bflag=true;
		try
		{ 
			String a0100=null,cbase=null,strym=null,strcount=null,dessetid=null,supdate=null;
			ContentDAO dao=new ContentDAO(this.conn);
			int ni9999=-1;
			int add_flag=0;
		//	RowSet rset=null;
			StringBuffer buf=new StringBuffer();
			/**取得人员库前缀列表*/
			String dbpres=this.templatevo.getString("cbase");
			/**应用库前缀*/
			String[] dbarr=StringUtils.split(dbpres, ",");
			DbWizard dbw=new DbWizard(this.conn);
	
			//判断提交的薪资类别是否包含了年月变化子集,并且为新增方式
			boolean isYMaddSet=isymChangeSet(setlist,typelist);
			String strNow=Sql_switcher.sqlNow();
			///////////////////////////////////////////////////////////////
			ArrayList z0z1List=getZ0z1List(this.gz_tablename);
			for(int i=0;i<dbarr.length;i++)
			{
				
				cbase=dbarr[i];
				/** 处理只有一条薪资记录的用户一般子集数据 */
				if(!"06".equals(sp_flag)&& "1".equals(isRedo)) //解决薪资重发问题
					dealwithSingleRecord(setlist,updatelist,typelist,dbarr,i,dbw,itemUptype,strNow);
				String tempName="t#"+this.userview.getUserName()+"_gz"; //this.userview.getUserName()+"_GzTempTable";
		 	 
				/** 处理只有一条薪资记录的用户年月子集数据 */
				if(!"06".equals(sp_flag)&& "1".equals(isRedo)) //解决薪资重发问题
				{
					for(int j=0;j<z0z1List.size();j++)
					{
						LazyDynaBean abean=(LazyDynaBean)z0z1List.get(j);
						strym=(String)abean.get("strym");
						
						
					    String[] temp=strym.split("-"); 
						String singleRecord_where="(select count(a0100) c,a0100 from "+this.gz_tablename+" where "; 
						singleRecord_where+=" lower("+this.gz_tablename+".NBASE)='"+cbase.toLowerCase()+"'  "+filterWhl+" ";
						singleRecord_where+=" and  "+Sql_switcher.year(this.gz_tablename+".a00z0")+"="+temp[0]+" and "+Sql_switcher.month(this.gz_tablename+".a00z0")+"="+temp[1]+"   group by a0100  having count(a0100)=1 ) aa";
					//     if(dbw.isExistTable(tempName,false))
					     {
					    	 dbw.dropTable(tempName);
					    	// dao.update(" drop table "+tempName);
					     }
					     StringBuffer sql=new StringBuffer("");
					     if(Sql_switcher.searchDbServer()==2)
					    	 sql.append("create table "+tempName+" as ");
					     sql.append("select "+this.gz_tablename+".a0100,"+this.gz_tablename+".a00z0,"+this.gz_tablename+".add_flag,"+this.gz_tablename+".a00z1");
					     if(Sql_switcher.searchDbServer()!=2)
					    	 sql.append(" into "+tempName);
					     sql.append(" from "+this.gz_tablename+","+singleRecord_where);
					     sql.append(" where aa.a0100="+this.gz_tablename+".a0100 and  ");
					     sql.append(Sql_switcher.year(this.gz_tablename+".a00z0")+"="+temp[0]+" and "+Sql_switcher.month(this.gz_tablename+".a00z0")+"="+temp[1]);
					     sql.append(filterWhl+" and exists (select null from "+cbase+"A01 where "+cbase+"A01.a0100="+this.gz_tablename+".a0100 )    and lower("+this.gz_tablename+".NBASE)='"+cbase.toLowerCase()+"' ");
					     dao.update(sql.toString());
						dealwithSingleRecord_ym(setlist,true,isYMaddSet,updatelist,typelist,cbase,dbw,itemUptype,strNow,strym);	
					} 
				}
				/**对一个用户同时有多条薪资记录的一般子集数据进行处理*/
				if(!"06".equals(sp_flag)&& "1".equals(isRedo)) //解决薪资重发问题
					dealwithMulRecord(setlist,isYMaddSet,updatelist,typelist, dbarr,i,dbw,itemUptype);
		//		else
		//			dealwithMulRecord_history(setlist,isYMaddSet,updatelist,typelist, dbarr,i,dbw,itemUptype);
				for(int j=0;j<setlist.size();j++)
				{
					String setid=(String)setlist.get(j);
					if("A00".equalsIgnoreCase(setid))
						continue;					
					/**(0,1,2,3)=(更新,新增,不变,更新(薪资重发时))*/
					String type=(String)typelist.get(j);
					if("3".equals(type))
						type="0";
					if("2".equalsIgnoreCase(type))//当前记录不变
						continue;
					/**子集未构库不提交*/
					FieldSet fieldset=DataDictionary.getFieldSetVo(setid);
					if(fieldset==null|| "0".equalsIgnoreCase(fieldset.getUseflag()))
						continue;					
					/**(0,1,2)=(一般,按月变化,按年变化)*/
					int nflag=Integer.parseInt(fieldset.getChangeflag());
					switch(setid.charAt(0))
					{
					case 'A'://人员库
						if("A01".equalsIgnoreCase(setid))
						{
								updateSQL(gz_tablename,cbase,setid,(String)updatelist.get(j),0,itemUptype);
						}
						else//如果子集，则只对按月或年变化的子集进行处理.
						{
							if(nflag>0)
							{
								updateSQL(gz_tablename,cbase,setid,(String)updatelist.get(j),nflag,itemUptype);
							}
						}
						break;
					}
				}
			}
	 
		}
		catch(Exception ex)
		{
			bflag=false;
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);				
		}
		return bflag;
	}
	
	
	
	
	
	
	/** 处理只有一条薪资记录的用户一般子集数据 */
	public void dealwithSingleRecord_history(ArrayList setlist,ArrayList updatelist,ArrayList typelist,String[] dbarr,int i,DbWizard dbw,HashMap itemUptype,String strNow)
	{
		try
		{
			String cbase=dbarr[i];
			String dessetid="";
			String supdate="";
			ContentDAO dao=new ContentDAO(this.conn);
			
			 //数据提交入库不判断子集及指标权限
			String subNoPriv=getLprogramAttri("subNoPriv",SalaryLProgramBo.CONFIRM_TYPE);
			if(subNoPriv==null||subNoPriv.trim().length()==0)
		 		subNoPriv="0";
		
			
			String tableName="t#"+this.userview.getUserName()+"_gz_4";  
	//		if(dbw.isExistTable(tableName,false))
			{
				dbw.dropTable(tableName);  
			}
			String _sql="  from "+this.gz_tablename+this._withNoLock+" where lower("+this.gz_tablename+".NBASE)='"+cbase.toLowerCase()+"' "+filterWhl+"  and exists (select null from "+cbase+"A01 where "+cbase+"A01.a0100="+this.gz_tablename+".a0100 )  group by a0100  having count(a0100)=1 ";
			if(Sql_switcher.searchDbServer()==2)
				 dbw.execute("create table "+tableName+" as  select a0100 "+_sql);	
			else
				 dbw.execute("select a0100   into "+tableName+_sql);
			 	
			
			String  royalty_valid=this.ctrlparam.getValue(SalaryCtrlParamBo.ROYALTIES,"valid");
			String royalty_relation_fields=this.ctrlparam.getValue(SalaryCtrlParamBo.ROYALTIES,"relation_fields");
			ArrayList relationfieldList=new ArrayList();
			String royalty_setid=this.ctrlparam.getValue(SalaryCtrlParamBo.ROYALTIES,"setid");
			if("1".equals(royalty_valid)&&royalty_relation_fields.length()>0)
			{
				String[] temps=royalty_relation_fields.toLowerCase().split(",");
				for(int n=0;n<temps.length;n++)
				{
					if(temps[n].trim().length()>0&&DataDictionary.getFieldItem(temps[n].trim())!=null)
					{ 
						relationfieldList.add(temps[n]);
					}
				}
			}
			
			for(int j=0;j<setlist.size();j++)
			{
				String setid=(String)setlist.get(j);
				if("A00".equalsIgnoreCase(setid))
					continue;
				String fields=(String)updatelist.get(j);
				/**(0,1,2,3)=(更新,新增,不变,更新(薪资重发时))*/
				String type=(String)typelist.get(j);
				if("3".equals(type))
					type="0";
				if("2".equalsIgnoreCase(type))//当前记录不变
					continue;
				/**子集未构库不提交*/
				FieldSet fieldset=DataDictionary.getFieldSetVo(setid);
				if(fieldset==null|| "0".equalsIgnoreCase(fieldset.getUseflag()))
					continue;
				/**(0,1,2)=(一般,按月变化,按年变化)*/
				int nflag=Integer.parseInt(fieldset.getChangeflag());
				int ntype=Integer.parseInt(type);
				
				if(nflag==0&&setid.charAt(0)=='A'&&!"A01".equalsIgnoreCase(setid))  /**一般子集*/
				{
					dessetid=dbarr[i]+setid;
					supdate=(String)updatelist.get(j);
					if("1".equals(royalty_valid)&&royalty_setid.equalsIgnoreCase(setid))
						ntype=0;
					if(ntype==2)
						continue;
					if("0".equals(subNoPriv))
					{
						if(!"2".equals(this.userview.analyseTablePriv(setid.toUpperCase())))
							continue;
					}
					
					switch(ntype)
					{
						case 0://更新 
							if("1".equals(royalty_valid)&&royalty_setid.equalsIgnoreCase(setid))
							{
						//		String where_str=singleRecord_where; 
								String strvalue=","+strNow+",'"+this.userview.getUserName()+"'";
								String where_str2="select a0100 from "+this.gz_tablename+this._withNoLock+" where "; 
								where_str2+="  not exists (select null from "+dessetid+"  where "+this.gz_tablename+".a0100="+dessetid+".a0100  )";
								where_str2+=" and lower("+this.gz_tablename+".NBASE)='"+cbase.toLowerCase()+"' "+filterWhl+"   group by a0100  having count(a0100)=1";
								batchInsertSetRecord_royalty(dessetid,tableName+" aa",where_str2,relationfieldList,cbase);
								
							} 
							break;
						case 1://追加记录
						//	String where_str=singleRecord_where;
							 
							String strvalue=","+strNow+",'"+this.userview.getUserName()+"'";
							String where_str2="select a0100,1"+strvalue+"from "+this.gz_tablename+this._withNoLock+" where "; 
							where_str2+="  not exists (select null from "+dessetid+"  where "+this.gz_tablename+".a0100="+dessetid+".a0100  )";
							where_str2+=" and lower("+this.gz_tablename+".NBASE)='"+cbase.toLowerCase()+"' "+filterWhl+"   group by a0100  having count(a0100)=1";
							batchInsertSetRecord(dessetid,tableName+" aa",where_str2);
								
							break;
					}
					 
					String value="";
					HashMap relationFieldMap=new HashMap();
					StringBuffer sqlRelation=new StringBuffer("");
					StringBuffer sqlRelation2=new StringBuffer("");
					if("1".equals(royalty_valid)&&royalty_setid.equalsIgnoreCase(setid))
					{
						for(int n=0;n<relationfieldList.size();n++)
						{
							String key=(String)relationfieldList.get(n);
							relationFieldMap.put(key.toLowerCase(),"");
							FieldItem item=DataDictionary.getFieldItem(key.toLowerCase());
							if(Sql_switcher.searchDbServer()==2)
							{
								if("D".equalsIgnoreCase(item.getItemtype()))
								{
									sqlRelation.append(" and "+Sql_switcher.isnull(Sql_switcher.dateToChar(dessetid+"."+key,"YYYY-MM-DD"),"'-'")+"="+Sql_switcher.isnull(Sql_switcher.dateToChar("t#"+this.userview.getUserName()+"_gz"+"."+key,"YYYY-MM-DD"),"'-'") ); 
									sqlRelation2.append(" and "+Sql_switcher.isnull(Sql_switcher.dateToChar(dessetid+"."+key,"YYYY-MM-DD"),"'-'")+"="+Sql_switcher.isnull(Sql_switcher.dateToChar(this.gz_tablename+"."+key,"YYYY-MM-DD"),"'-'") ); 
								}
								else
								{
									sqlRelation.append(" and "+Sql_switcher.isnull(dessetid+"."+key,"'-'")+"="+Sql_switcher.isnull("t#"+this.userview.getUserName()+"_gz"+"."+key,"'-'") ); 
									sqlRelation2.append(" and "+Sql_switcher.isnull(dessetid+"."+key,"'-'")+"="+Sql_switcher.isnull(this.gz_tablename+"."+key,"'-'") ); 
								}
							}
							else		
							{
								sqlRelation.append(" and "+Sql_switcher.isnull(dessetid+"."+key,"''")+"="+Sql_switcher.isnull("t#"+this.userview.getUserName()+"_gz"+"."+key,"''"));
								sqlRelation2.append(" and "+Sql_switcher.isnull(dessetid+"."+key,"''")+"="+Sql_switcher.isnull(this.gz_tablename+"."+key,"''"));
							}
						}
						
						value=getUpdateSQL_royalty(dessetid,this.gz_tablename,fields,nflag,itemUptype);
					}
					else
						value=getUpdateSQL(dessetid,this.gz_tablename,fields,nflag,itemUptype);
					if(Sql_switcher.searchDbServer()==2)
					{
						 String strArr[] = StringUtils.split(value, "`");
					     StringBuffer sub_str=new StringBuffer("");
					     StringBuffer sub_str2=new StringBuffer("");
						 for(int e = 0; e < strArr.length; e++)
					     {
					            String temp = strArr[e];
					            String strtmp[] = StringUtils.split(temp, "=", 2);
					            if(strtmp[1].indexOf(this.gz_tablename)==-1)
					            {
					            	sub_str.append(","+strtmp[1]+" as "+strtmp[0]);
					            }
					            else
					            {
					            	sub_str.append(","+strtmp[1]);
					            	if(relationFieldMap.get(strtmp[1].replaceAll(this.gz_tablename+".","").toLowerCase())!=null)
					            		relationFieldMap.put(strtmp[1].replaceAll(this.gz_tablename+".","").toLowerCase(),"1");
					            }
					            
					            if(strtmp[0].indexOf(dessetid)==-1)
					            	sub_str2.append(","+strtmp[0]);
					            else
					            {
					            	String[] aa=strtmp[0].split("\\.");
					            	sub_str2.append(","+aa[1]);
					            	
					            }
					     }
                      
					    String tempName="t#"+this.userview.getUserName()+"_gz";  //this.userview.getUserName()+"_GzTempTable";
				//	     if(dbw.isExistTable(tempName,false))
					     {
					    	 dbw.dropTable(tempName);
					    //	 dao.update(" drop table "+tempName);
					     }
					     StringBuffer sql=new StringBuffer("create table "+tempName+" as ");
					     if("1".equals(royalty_valid)&&royalty_setid.equalsIgnoreCase(setid))
					     {
					    	 Set set=relationFieldMap.keySet();
					    	 for(Iterator t=set.iterator();t.hasNext();)
					    	 {
					    		 String key=(String)t.next(); 
					    		 if(!"1".equalsIgnoreCase((String)relationFieldMap.get(key)))
					    			 sub_str.append(","+key);
					    	 }
					     }
						 sql.append("select "+this.gz_tablename+".a0100"+sub_str.toString());
						 sql.append(" from "+this.gz_tablename+this._withNoLock+","+tableName+" aa");
						 sql.append(" where aa.a0100="+this.gz_tablename+".a0100   "+filterWhl+"  and lower("+this.gz_tablename+".NBASE)='"+cbase.toLowerCase()+"' ");
						 dao.update(sql.toString());
					      
						
					     sql.setLength(0);
					     
					     if("1".equals(royalty_valid)&&royalty_setid.equalsIgnoreCase(setid))
					     {
					    	 sql.append("Update  "+dessetid+" set ("+sub_str2.substring(1)+")=");
						     sql.append(" (select "+sub_str2.substring(1)+" from "+tempName);
						     sql.append(" where "+tempName+".a0100="+dessetid+".a0100  "+sqlRelation.toString()) ;
						     sql.append(" ) where   ");
						     sql.append("   exists (select null from "+tempName+" where "+tempName+".a0100="+dessetid+".a0100  "+sqlRelation.toString()+") ");
					    	 
					     }
					     else
					     { 
						     sql.append("Update  "+dessetid+" set ("+sub_str2.substring(1)+")=");
						     sql.append(" (select "+sub_str2.substring(1)+" from "+tempName);
						     sql.append(" where "+tempName+".a0100="+dessetid+".a0100  ") ;
						     sql.append(" ) where "+dessetid+".i9999=(select max(b.i9999) from "+dessetid+" b where b.a0100="+dessetid+".a0100 )  and ");
						     sql.append("   exists (select null from "+tempName+" where "+tempName+".a0100="+dessetid+".a0100 ) ");
					     }
					     dao.update(sql.toString());
					     
					}
					else
					{
						
						StringBuffer strSWhere=new StringBuffer();
						if("1".equals(royalty_valid)&&royalty_setid.equalsIgnoreCase(setid))
						{
						 
							strSWhere.append(dessetid+".A0100="+this.gz_tablename+".A0100 "+sqlRelation2.toString());
							strSWhere.append("  "+filterWhl+"  and lower("+this.gz_tablename+".NBASE)='"+cbase.toLowerCase()+"'");
							strSWhere.append(" and exists (select null from "+tableName+" aa where aa.a0100="+dessetid+".a0100) ");
						}
						else
						{
							strSWhere.append(dessetid+".i9999=(select max(b.i9999) from "+dessetid+" b where b.a0100="+dessetid+".a0100 )  ");
							strSWhere.append(" and "+dessetid+".A0100="+this.gz_tablename+".A0100 ");
							strSWhere.append("  "+filterWhl+"  and lower("+this.gz_tablename+".NBASE)='"+cbase.toLowerCase()+"'");
							strSWhere.append(" and exists (select null from "+tableName+" aa where aa.a0100="+dessetid+".a0100) ");
						}
						
						StringBuffer strDWhere=new StringBuffer();
						if("1".equals(royalty_valid)&&royalty_setid.equalsIgnoreCase(setid))
						{ 
							strDWhere.append(dessetid+".A0100="+this.gz_tablename+".A0100 "+sqlRelation2.toString());
							strDWhere.append("  "+filterWhl+"  and exists (select null from "+tableName+" aa where aa.a0100="+dessetid+".a0100) ");
						}
						else
						{
							strDWhere.append(dessetid+".i9999=(select max(b.i9999) from "+dessetid+" b where b.a0100="+dessetid+".a0100 ) and ");
							strDWhere.append(dessetid+".A0100="+this.gz_tablename+".A0100 ");
							strDWhere.append("  "+filterWhl+"  and exists (select null from "+tableName+" aa where aa.a0100="+dessetid+".a0100) ");
						}	
						
						
						
						if(Sql_switcher.searchDbServer()!=2)  //不为oracle
							dbw.updateRecord(dessetid, gz_tablename+this._withNoLock,dessetid+".A0100="+gz_tablename+".A0100", value, strSWhere.toString(),"");
						else
							dbw.updateRecord(dessetid, gz_tablename,dessetid+".A0100="+gz_tablename+".A0100", value, strSWhere.toString(), strDWhere.toString());
						
						 
					}
				}
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	// -------------------------------------- end----------------------------------------------------------------
	
	
	
	
	
	//判断提交的薪资类别是否包含了年月变化子集,并且为新增方式
	public boolean isymChangeSet(ArrayList setlist,ArrayList typelist)
	{
		boolean flag=false;
		for(int j=0;j<setlist.size();j++)
		{
			String setid=((String)setlist.get(j)).toUpperCase();
			String type=(String)typelist.get(j);
			if("2".equalsIgnoreCase(type))//当前记录不变
				continue;
			int ntype=Integer.parseInt(type);
			if((ntype==1||ntype==0||ntype==3)&&setid.charAt(0)=='A')
			{
				if("A00".equalsIgnoreCase(setid))
					continue;
				FieldSet fieldset=DataDictionary.getFieldSetVo(setid);
				if(fieldset==null|| "0".equalsIgnoreCase(fieldset.getUseflag())) //修改 当没有数据时没有新增。
					continue;
				int nflag=Integer.parseInt(fieldset.getChangeflag());
				if(nflag==1||nflag==2)
					flag=true;
			}
		}
		return flag;
	}
	
	
	public void synchronousZ1(String strym,String strcount,int z1,String a0100,String nbase,ArrayList updateList)
	{
		String year=strym.substring(0, 4);
		String month=strym.substring(5, 7);
	/*	String sql="update su_salary_11 set a00z1=?,add_flag=1 where low(nbase)='"+nbase.toLowerCase()+"' and a0100=? "
			+" and "+Sql_switcher.year("a00z0")+"=? and "+Sql_switcher.month("a00z0")+"=? and a00z1=? ";*/
		ArrayList tempList=new ArrayList();
		tempList.add(new Integer(z1));
		tempList.add(a0100);
		tempList.add(new Integer(year));
		tempList.add(new Integer(month));
		tempList.add(new Integer(strcount));
		updateList.add(tempList);
	}
	
	
	
	/**
	 *  查找薪资类别项目涉及各年月变化子集本月最大次数加1
	 * @param setList
	 * @param strym
	 * @param nbase
	 * @param a0100
	 * @return
	 */
	public int getNewA00z1_history(String strym,String nbase,String a0100,String _a00z1)
	{
		int a00z1=0;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer sql=new StringBuffer("");
			String year=strym.substring(0, 4);
			String month=strym.substring(5, 7);
			
			sql.append("select  MAX(a00Z1)  from salaryhistory"+this._withNoLock+" where  a0100='"+a0100+"'  and  lower(nbase)='"+nbase.toLowerCase()+"' ");
			sql.append(" and salaryid="+this.salaryid+"  and  "+Sql_switcher.year("a00z0")+"="+year+" and "+Sql_switcher.month("a00z0")+"="+month+" ");
			sql.append(" and a00z1<>"+_a00z1);
			
			RowSet rowSet=dao.search(sql.toString());
			if(rowSet.next())
			{
					if(rowSet.getString(1)!=null)
						a00z1=rowSet.getInt(1);
			}
			rowSet.close();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		a00z1++;
		return a00z1;
	}
	
	
	
	/**
	 *  查找薪资类别项目涉及各年月变化子集本月最大次数加1
	 * @param setList
	 * @param strym
	 * @param nbase
	 * @param a0100
	 * @return
	 */
	public int getNewA00z1(ArrayList setList,String strym,String nbase,String a0100,ArrayList typelist)
	{
		int a00z1=0;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer sub_sql=new StringBuffer("");
			String year=strym.substring(0, 4);
			String month=strym.substring(5, 7);
			for(int j=0;j<setList.size();j++)
			{
				String setid=((String)setList.get(j)).toUpperCase();
				if(setid.charAt(0)=='A')
				{
					if("A00".equalsIgnoreCase(setid))
						continue;
					
					/**(0,1,2,3)=(更新,新增,不变,更新(薪资重发时))*/
					String type=(String)typelist.get(j);
					if("2".equalsIgnoreCase(type)|| "0".equalsIgnoreCase(type))//当前记录不变
						continue;
					
					FieldSet fieldset=DataDictionary.getFieldSetVo(setid);
					if(fieldset==null|| "0".equalsIgnoreCase(fieldset.getUseflag()))
						continue;
					int nflag=Integer.parseInt(fieldset.getChangeflag());
					if(nflag==1||nflag==2)
					{
						sub_sql.append(" union all ");
						sub_sql.append("select max("+setid+"z1) z1 from "+nbase+setid+" where a0100='"+a0100+"' ");
						sub_sql.append(" and "+Sql_switcher.year(setid+"z0")+"="+year+" and "+Sql_switcher.month(setid+"z0")+"="+month);
					}
				}
			}
			if(sub_sql.length()>0)
			{
				RowSet rowSet=dao.search("select max(z1) from ( "+sub_sql.substring(11)+" ) b");
				if(rowSet.next())
				{
					if(rowSet.getString(1)!=null)
						a00z1=rowSet.getInt(1);
				}
				rowSet.close();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		a00z1++;
		return a00z1;
	}

	/**
	 * 更新税表明细数据
	 * @throws GeneralException
	 */
	private void setTaxflag()throws GeneralException
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer buf=new StringBuffer();
			
			/**如果系统中不存在个税明细表，则自动创建*/
			CalcTaxBo calctaxbo=new CalcTaxBo(this.conn,this.userview);
			calctaxbo.createTaxDetails();	
			
			/**
			 * =0,无效 计算过程中的数据
			 * =1,有效
			 */
			buf.append("update gz_tax_mx set flag=1");
			buf.append(" where exists (select * from ");
			buf.append(gz_tablename);
			buf.append(" A where upper(gz_tax_mx.nbase)=upper(A.nbase) and gz_tax_mx.a0100=A.a0100 and ");
			buf.append(" gz_tax_mx.a00z0=a.a00z0 "+this.filterWhl.replaceAll(this.gz_tablename,"A")+"  and gz_tax_mx.a00z1=a.a00z1)");
			buf.append(" and salaryid=");
			buf.append(this.salaryid);
			dao.update(buf.toString());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);			
		}
	}
	/**
	 * 更新补发数据
	 * @throws GeneralException
	 */
	private void updateBF()throws GeneralException
	{
		/**如果未定义薪资模板的话，则不用处理补发*/
		String templetes=this.ctrlparam.getValue(SalaryCtrlParamBo.TEMPLATE);
		String[] tmparr=StringUtils.split(templetes,",");
		if(tmparr==null||tmparr.length==0)
			return;
		try
		{
			DbWizard dbw=new DbWizard(this.conn);
			if(!dbw.isExistTable(gz_bf_table,false))
				return;
			StringBuffer buf=new StringBuffer();
			/**
			 * =0 正在处理
			 * =1 已生效
			 * =2 工资发放已使用 
			 * =3 发放确认
			 */
			buf.append("validflag=2");
			buf.append(" and tabid in (");
			buf.append(templetes);
			buf.append(")");
			ContentDAO dao=new ContentDAO(this.conn);
			dao.update("update "+gz_bf_table+" set validflag=3 where "+buf.toString() );
			//dbw.updateRecord(gz_bf_table, "validflag=3",buf.toString());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);				
		}
	}
	
	private boolean updateSQL_history(String srcName,String cbase,String setid,String fields,int chgflag,HashMap itemUptype)throws GeneralException
	{
		boolean bflag=true;
		try
		{
			String axxz0=setid+"z0";
			String axxz1=setid+"z1";
			String destname=cbase+setid;
			String updates="";
			
			StringBuffer strOn=new StringBuffer(); 
			StringBuffer strSWhere2=new StringBuffer(); 
			DbWizard dbw=new DbWizard(this.conn);
			String tempTable=gz_tablename;
			
		 
			{
				
				String  royalty_valid=this.ctrlparam.getValue(SalaryCtrlParamBo.ROYALTIES,"valid");
				String royalty_relation_fields=this.ctrlparam.getValue(SalaryCtrlParamBo.ROYALTIES,"relation_fields");
				ArrayList relationfieldList=new ArrayList();
				String royalty_setid=this.ctrlparam.getValue(SalaryCtrlParamBo.ROYALTIES,"setid");
				StringBuffer relationFieldStr=new StringBuffer("");
			 
				
				if("1".equalsIgnoreCase(royalty_valid)&&royalty_setid.equalsIgnoreCase(setid))
				{
					updates=getUpdateSQL(destname,srcName,fields,chgflag,itemUptype);
					strOn.append(destname);
					strOn.append(".A0100=");
					strOn.append(srcName);
					strOn.append(".A0100  "); 
					String[] temps=royalty_relation_fields.toLowerCase().split(",");
					for(int n=0;n<temps.length;n++)
					{
							if(temps[n].trim().length()>0&&DataDictionary.getFieldItem(temps[n].trim())!=null)
							{  
								if(Sql_switcher.searchDbServer()==2)
								{
									if("D".equalsIgnoreCase(DataDictionary.getFieldItem(temps[n].trim()).getItemtype()))
										strOn.append(" and "+Sql_switcher.isnull(Sql_switcher.dateToChar(destname+"."+temps[n],"YYYY-MM-DD"),"'-'")+"="+Sql_switcher.isnull(Sql_switcher.dateToChar(srcName+"."+temps[n],"YYYY-MM-DD"),"'-'")); 
									else
										strOn.append(" and "+Sql_switcher.isnull(destname+"."+temps[n],"'-'")+"="+Sql_switcher.isnull(srcName+"."+temps[n],"'-'")); 
								}
								else
									strOn.append(" and "+Sql_switcher.isnull(destname+"."+temps[n],"''")+"="+Sql_switcher.isnull(srcName+"."+temps[n],"''")); 
							}
					}   
					
					strSWhere2.append(" lower(");
					strSWhere2.append(srcName);
					strSWhere2.append(".nbase)='");
					strSWhere2.append(cbase.toLowerCase());
					strSWhere2.append("' "+filterWhl);
					
				}
				else
				{
					updates=getUpdateSQL(destname,srcName,fields,chgflag,itemUptype);
			//		updates=getUpdateSQL(destname,"salaryhistory",fields,chgflag,itemUptype); 20151105  dengcan  薪资发放提交数据为何要从历史表里找数呢
			//		srcName="salaryhistory";
					strOn.append(destname);
					strOn.append(".A0100=");
					strOn.append(srcName);
					strOn.append(".A0100 and ");
					strOn.append(destname);
					strOn.append(".");
					strOn.append(axxz0);
					strOn.append("=");
					strOn.append(srcName);
					strOn.append(".a00z0");
					strOn.append(" and ");
					strOn.append(destname);
					strOn.append(".");
					strOn.append(axxz1);
					strOn.append("=");
					strOn.append(srcName);
					strOn.append(".a00z1"); 
					
					
					strSWhere2.append(" lower(");
					strSWhere2.append(srcName);
					strSWhere2.append(".nbase)='");
					strSWhere2.append(cbase.toLowerCase()+"'"); 
			//		strSWhere2.append(" "+filterWhl2);
					strSWhere2.append(" "+filterWhl);
					tempTable=srcName; //"salaryhistory  WITH(NOLOCK) ";  20151105  dengcan  薪资发放提交数据为何要从历史表里找数呢
				} 
				
				
			}  
			dbw.updateRecord(destname, tempTable,strOn.toString(), updates,"", strSWhere2.toString());
		 
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			bflag=false;
			throw GeneralExceptionHandler.Handle(ex);					
		}
		return bflag;
	}
	
	/**
	 * 对按月或按年变化子集、主集指标批量更新
	 * @param srcName
	 * @param cbase
	 * @param setid
	 * @param fields
	 * @param chgflag
	 * @return
	 */
	private boolean updateSQL(String srcName,String cbase,String setid,String fields,int chgflag,HashMap itemUptype)throws GeneralException
	{
		boolean bflag=true;
		try
		{
			String axxz0=setid+"z0";
			String axxz1=setid+"z1";
			String destname=cbase+setid;
			String updates=getUpdateSQL(destname,srcName,fields,chgflag,itemUptype);
			StringBuffer strOn=new StringBuffer();
			StringBuffer strSWhere=new StringBuffer();
			StringBuffer strSWhere2=new StringBuffer();
			StringBuffer strDWhere=new StringBuffer();
			DbWizard dbw=new DbWizard(this.conn);
			String tempTable=gz_tablename;
			if("A01".equalsIgnoreCase(setid))
			{
				ContentDAO dao=new ContentDAO(this.conn);
				boolean isMultiple=false;
				if(Sql_switcher.searchDbServer()==2)
				{
					RowSet rowSet=dao.search("select a0100,nbase,count(a0100) from "+srcName+" group by a0100,nbase  having count(a0100)>1 ");
					if(rowSet.next())
						isMultiple=true;
					if(rowSet!=null)
						rowSet.close();
				}
				 
 
				strOn.append(destname);
				strOn.append(".A0100=");
				strOn.append(srcName);
				strOn.append(".A0100 ");
				
				 
				strSWhere2.append(" lower(");
				strSWhere2.append(srcName);
				strSWhere2.append(".nbase)='");
				strSWhere2.append(cbase.toLowerCase());
				strSWhere2.append("'  "+filterWhl);
				 
				
				if(isMultiple)  //oracle
				{
					strSWhere2.append(" and  exists ( select null from  (select a0100,nbase,a00z0,max(a00z1) a00z1 from "+srcName+"  group by a0100,nbase,a00z0 ) a ");
					strSWhere2.append("  where "+srcName+".a0100=a.a0100 and "+srcName+".nbase=a.nbase and "+srcName+".a00z0=a.a00z0 and "+srcName+".a00z1=a.a00z1   )");
					
				}

			//	strDWhere.append(destname);
			//	strDWhere.append(".A0100 in (select ");
				strDWhere.append(" exists (select ");
				strDWhere.append(srcName);
				strDWhere.append(".A0100 from ");
				strDWhere.append(srcName);
				strDWhere.append(" where  lower(");
				strDWhere.append(srcName);
				strDWhere.append(".nbase)='");
				strDWhere.append(cbase.toLowerCase());
				strDWhere.append("'  "+filterWhl+" and "+destname+".a0100="+srcName+".a0100  )");
				
			//	dbw.updateRecord(destname, gz_tablename,strOn.toString(), updates,strDWhere.toString(), strDWhere.toString());
				
			}
			else
			{
				
				String  royalty_valid=this.ctrlparam.getValue(SalaryCtrlParamBo.ROYALTIES,"valid");
				String royalty_relation_fields=this.ctrlparam.getValue(SalaryCtrlParamBo.ROYALTIES,"relation_fields");
				ArrayList relationfieldList=new ArrayList();
				String royalty_setid=this.ctrlparam.getValue(SalaryCtrlParamBo.ROYALTIES,"setid");
				StringBuffer relationFieldStr=new StringBuffer("");
			 
				
				if("1".equalsIgnoreCase(royalty_valid)&&royalty_setid.equalsIgnoreCase(setid))
				{
					strOn.append(destname);
					strOn.append(".A0100=");
					strOn.append(srcName);
					strOn.append(".A0100  "); 
					String[] temps=royalty_relation_fields.toLowerCase().split(",");
					for(int n=0;n<temps.length;n++)
					{
							if(temps[n].trim().length()>0&&DataDictionary.getFieldItem(temps[n].trim())!=null)
							{  
								if(Sql_switcher.searchDbServer()==2)
								{
									if("D".equalsIgnoreCase(DataDictionary.getFieldItem(temps[n].trim()).getItemtype()))
										strOn.append(" and "+Sql_switcher.isnull(Sql_switcher.dateToChar(destname+"."+temps[n],"YYYY-MM-DD"),"'-'")+"="+Sql_switcher.isnull(Sql_switcher.dateToChar(srcName+"."+temps[n],"YYYY-MM-DD"),"'-'")); 
									else
										strOn.append(" and "+Sql_switcher.isnull(destname+"."+temps[n],"'-'")+"="+Sql_switcher.isnull(srcName+"."+temps[n],"'-'")); 
								}
								else
									strOn.append(" and "+Sql_switcher.isnull(destname+"."+temps[n],"''")+"="+Sql_switcher.isnull(srcName+"."+temps[n],"''")); 
							}
					}  
//					strOn.append(" and ");
//					strOn.append(destname);
//					strOn.append(".I9999=");
//					strOn.append(srcName);
//					strOn.append(".A00Z1");
					
					strSWhere2.append(" lower(");
					strSWhere2.append(srcName);
					strSWhere2.append(".nbase)='");
					strSWhere2.append(cbase.toLowerCase());
					strSWhere2.append("' "+filterWhl);
					
					strSWhere.append(strOn.toString());
					strSWhere.append(" and lower(");
					strSWhere.append(srcName);
					strSWhere.append(".nbase)='");
					strSWhere.append(cbase.toLowerCase());
					strSWhere.append("'  "+filterWhl);
					
					strDWhere.append(" exists (select null from ");
					strDWhere.append(srcName);
					strDWhere.append(" where ");
					strDWhere.append(strSWhere.toString()+" )");
				//	strDWhere.append(" "+filterWhl+"   )");
					
					
				}
				else
				{
					strOn.append(destname);
					strOn.append(".A0100=");
					strOn.append(srcName);
					strOn.append(".A0100 and ");
					strOn.append(destname);
					strOn.append(".");
					strOn.append(axxz0);
					strOn.append("=");
					strOn.append(srcName);
					strOn.append(".a00z0");
					strOn.append(" and ");
					strOn.append(destname);
					strOn.append(".");
					strOn.append(axxz1);
					strOn.append("=");
					strOn.append(srcName);
					strOn.append(".a00z1");
//					strOn.append(" and ");
//					strOn.append(destname);
//					strOn.append(".I9999=");
//					strOn.append(srcName);
//					strOn.append(".A00Z1");
					
					strSWhere2.append(" lower(");
					strSWhere2.append(srcName);
					strSWhere2.append(".nbase)='");
					strSWhere2.append(cbase.toLowerCase()+"'");
					if(Sql_switcher.searchDbServer()!=2)  //20150604 dengcan  sqlserver薪资审批 下一次提交30000条所死
					{
						tempTable=" (select "+gz_tablename+".* from "+gz_tablename+this._withNoLock+"  where 1=1 "+filterWhl+" )  "+gz_tablename;
					}
					else
						strSWhere2.append(" "+filterWhl);
					
					strSWhere.append(strOn.toString());
					strSWhere.append(" and lower(");
					strSWhere.append(srcName);
					strSWhere.append(".nbase)='");
					strSWhere.append(cbase.toLowerCase());
					strSWhere.append("'  "+filterWhl);
					
					strDWhere.append(" exists (select null from ");
					strDWhere.append(srcName);
					strDWhere.append(" where ");
					strDWhere.append(strSWhere.toString()+" ) ");
			//		strDWhere.append(" "+filterWhl+"   )");
				}
				
				
			}
		//	dbw.updateRecord(destname, gz_tablename,strOn.toString(), updates,strDWhere.toString(), strSWhere2.toString());
			if(Sql_switcher.searchDbServer()!=2)  //不为oracle
				dbw.updateRecord(destname, tempTable,strOn.toString(), updates,"", strSWhere2.toString());
			else
				dbw.updateRecord(destname, gz_tablename,strOn.toString(), updates,strDWhere.toString(), strSWhere2.toString());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			bflag=false;
			throw GeneralExceptionHandler.Handle(ex);					
		}
		return bflag;
	}
	/**
	 * 重发最后一次的薪资
	 * @return
	 * @throws GeneralException
	 */
	public boolean reExtendTheLastOne(String bosdate,String count)throws GeneralException
	{
		boolean bflag=true;
		try
		{
			StringBuffer buf=new StringBuffer();
			ContentDAO dao=new ContentDAO(this.conn);
			
			SalaryPkgBo pgkbo=new SalaryPkgBo(this.conn,this.userview,0);
			String username=this.userview.getUserName();
			if(this.manager!=null&&this.manager.length()>0)
				username=this.manager;
			bosdate=bosdate.replaceAll("\\.","-");
			LazyDynaBean abean=pgkbo.searchCurrentDate2(String.valueOf(this.salaryid),username);
			String strYm=(String)abean.get("strYm");
			String strC=(String)abean.get("strC");
			if(strYm.trim().length()>0)
			{
				/**需要审批的话,则分析当前处理的业务日期是否处理审批状态*/
				/**当前处理的到的年月标识和次数*/
				currym=strYm;
				currcount=strC;
				if(isApprove())
				{
					if(isApproving())
						throw new GeneralException("本期薪资发放业务正处于审批当中!");
				}
				int i = DeleteCurrentDraftRecord(); 
				if(i>0){
					/**清空当前薪资表中的数据*/
					buf.append("delete from ");
					buf.append(this.gz_tablename);
					dao.update(buf.toString());
				}
				//getYearMonthCount("06");
			}
			currym=bosdate;
			currcount=count;
			
			/**分析薪资历史库是否有数据*/
			if(!isHaveHistroyLog())
				throw new GeneralException("薪资历史库中没有提交的数据!");
			
			/**清空当前薪资表中的数据*/
			buf.setLength(0);
			buf.append("delete from ");
			buf.append(this.gz_tablename);
			dao.update(buf.toString());
			/**所有项目*/
			StringBuffer fields=new StringBuffer();
			for(int i=0;i<gzitemlist.size();i++)
			{
				GzItemVo itemvo=(GzItemVo)gzitemlist.get(i);
				if(itemvo.getInitflag()==4)
					continue;
				if(fields.length()==0)
					fields.append(itemvo.getFldname());
				else
				{
					fields.append(",");
					fields.append(itemvo.getFldname());
				}
			}
			buf.setLength(0);
			buf.append("insert into ");
			buf.append(this.gz_tablename);
			buf.append("(add_flag,userflag,sp_flag,");
			if(this.manager!=null&&this.manager.trim().length()>0)
				buf.append("sp_flag2,");
			buf.append(fields.toString());
			buf.append(") select 1,userflag");
			buf.append(",'01',");
			if(this.manager!=null&&this.manager.trim().length()>0)
				buf.append("'01',");
			buf.append(fields.toString());
			buf.append(" from salaryhistory where");
			buf.append(" A00Z2=");
			buf.append(Sql_switcher.dateValue(currym));
			buf.append(" and A00Z3=");
			buf.append(currcount);
			buf.append(" and salaryid=");
			buf.append(this.salaryid);
	//		if(!this.userview.isSuper_admin())
			{
				buf.append(" and lower(userflag)='");
				buf.append(this.userview.getUserName().toLowerCase());
				buf.append("'");
			}
			dao.update(buf.toString());
			/**删除最后一次薪资数据*/
			deleteHistory();
			/**删除个税明细表中的数据*/
			deleteTaxMx();
			
			
			//写入薪资发放数据的映射表
			dao.update("delete from salary_mapping where salaryid="+this.salaryid+" and lower(USERFLAG)='"+this.userview.getUserName().toLowerCase()+"'");
			dao.update("insert into salary_mapping (a0100,nbase,a00z0,a00z1,salaryid,userflag) select a0100,nbase,a00z0,a00z1,"+this.salaryid+",'"+this.userview.getUserName().toLowerCase()+"' from "+this.gz_tablename);
			 
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			bflag=false;
			throw GeneralExceptionHandler.Handle(ex);			
		}
		return bflag;			
	}
	
	
	
	/**
	 * 删除工资发放表中（走审批流程，不为起草状态，薪资发放表里有而历史数据表里每有的）的垃圾数据
	 * 产生的情况： 两人同时上报一个工资套，拥有交叉权限，后者报批的数据将前者的数据在历史表中删掉。
	 * 造成临时表中有无法处理报批的数据。
	 * @author dengcan
	 */
	public void deleteSpareData()
	{
		try
		{
			if(isApprove())
			{
				ContentDAO dao=new ContentDAO(this.conn);
				StringBuffer sql=new StringBuffer("delete from "+this.gz_tablename+" where  exists  (select * from salaryhistory "+this._withNoLock);
				sql.append(" where a0100="+this.gz_tablename+".a0100 and  upper(nbase)=upper("+this.gz_tablename+".nbase) and  a00z0="+this.gz_tablename+".a00z0 ");
				sql.append(" and  a00z1="+this.gz_tablename+".a00z1 and salaryid="+this.salaryid+" and userflag<>"+this.gz_tablename+".userflag ) and sp_flag='02'");
				dao.delete(sql.toString(),new ArrayList());
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 删除当前用户历史表的数据
	 */
	public void deleteHistory()
	{
		StringBuffer buf=new StringBuffer();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			buf.append("delete from salaryhistory ");
			buf.append(" where exists (select null from ");
			buf.append(this.gz_tablename);
			buf.append(" a where salaryhistory.a00z0=a.a00z0 and salaryhistory.a00z1=a.a00z1 and upper(salaryhistory.nbase)=upper(a.nbase) and ");
			buf.append(" salaryhistory.a0100=a.a0100 "+filterWhl.replaceAll(this.gz_tablename,"a")+"  )");
			buf.append(" and salaryid=");
			buf.append(this.salaryid);
			
			String[] atemps=this.gz_tablename.toLowerCase().split("_salary_");  //20100323
			buf.append(" and lower(userflag)='");
			buf.append(atemps[0].toLowerCase());
			buf.append("'");
			
		/*	if(!this.userview.isSuper_admin())
			{
				buf.append(" and lower(userflag)='");
				buf.append(this.userview.getUserName().toLowerCase());
				buf.append("'");
			}*/
			dao.update(buf.toString());
			
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	/**
	 * 删除用户历史表不在当前临时表中的数据(重发时，删掉了一些记录)
	 */
	public void deleteHistory2()
	{
		StringBuffer buf=new StringBuffer();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			
			
			RowSet rowSet=dao.search("select A00z2,A00z3 from "+this.gz_tablename+" where 1=1  "+filterWhl);
			String strYm="";
			String strC="";
			if(rowSet.next())
			{
				strYm=rowSet.getDate("A00Z2")!=null?PubFunc.FormatDate(rowSet.getDate("A00Z2"), "yyyy-MM-dd"):"";
				strC=rowSet.getString("A00Z3")!=null?rowSet.getString("A00Z3"):"";
			}
			rowSet.close();
			
			buf.append("delete from salaryhistory ");
			buf.append(" where not exists (select null from ");
			buf.append(this.gz_tablename);
			buf.append(" a where salaryhistory.a00z0=a.a00z0 and salaryhistory.a00z1=a.a00z1 and upper(salaryhistory.nbase)=upper(a.nbase) and ");
			buf.append(" salaryhistory.a0100=a.a0100  "+filterWhl.replaceAll(this.gz_tablename,"a")+"  )");
			buf.append(" and salaryid=");
			buf.append(this.salaryid);
			buf.append(" and A00Z2=");
			buf.append(Sql_switcher.dateValue(strYm));
			buf.append(" and A00Z3=");
			buf.append(strC);
			buf.append(" and lower(userflag)='");
			
			String[] atemps=this.gz_tablename.toLowerCase().split("_salary_");
			buf.append(atemps[0].toLowerCase());
		//	buf.append(this.userview.getUserName().toLowerCase());
			buf.append("'");
			dao.update(buf.toString());
			
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	
	
	/**删除税表明细数据*/
	private void deleteTaxMx()
	{
		StringBuffer buf=new StringBuffer();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);			
			buf.append("delete from gz_tax_mx where exists(select * from ");
			buf.append(this.gz_tablename);
			buf.append(" a where upper(gz_tax_mx.nbase)=upper(a.nbase) and gz_tax_mx.a0100=a.a0100");
			buf.append(" and gz_tax_mx.a00z0=a.a00z0 and gz_tax_mx.a00z1=a.a00z1)");
			buf.append(" and salaryid=");
			buf.append(this.salaryid);
			dao.update(buf.toString());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	
	
	
	/**
	 * 批量引入数据
	 * @param type     =1同月上次   =2上月同次数据 =3档案库数据　=4某年某月某次
	 * @param itemlist 引入的薪资项目列表
	 * @return
	 */		
	public boolean batchImport_history(HashMap map,String type,ArrayList itemlist,String year,String month,String count)throws GeneralException
	{
		if("3".equalsIgnoreCase(type))
			return batchImportFromArchive_history(map,type,itemlist);
		else
			return batchImportFromHisGzTable_history(map,type,itemlist,year,month,count);
	}
	
	
	/**
	 * 审批中批量引入数据后需同步临时表中的相应记录数据
	 * @param itemlist
	 * @param year
	 * @param month
	 * @param count
	 * @throws GeneralException
	 */
	public void  batchUpdateTempData(ArrayList itemlist,HashMap map)throws GeneralException
	{
		try
		{
			if(itemlist.size()==0)
				return;
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer strwhere=new StringBuffer(" and ( salaryhistory.curr_user='"+this.userview.getUserId()+"' and ( salaryhistory.sp_flag='02' or salaryhistory.sp_flag='07' ) )");
			/**求得当前处理年月和次数*/
			String currym=(String)map.get("ym");
			String currcount=(String)map.get("count");
			strwhere.append(" and salaryhistory.a00z2="+Sql_switcher.dateValue(currym)+" and salaryhistory.a00z3="+currcount+" and salaryhistory.salaryid="+this.salaryid);
			
			ArrayList userFlagList=new ArrayList();
			RowSet rowSet=null;
			SalaryCtrlParamBo ctrlparam=new SalaryCtrlParamBo(this.conn,this.salaryid);
			this.manager=ctrlparam.getValue(SalaryCtrlParamBo.SHARE_SET, "user");
			if(this.manager!=null&&this.manager.length()>0)
			{
				rowSet=dao.search("select distinct userflag from salaryhistory"+this._withNoLock+" where 1=1 "+strwhere.toString());
				if(rowSet.next())
					userFlagList.add(this.manager);
			}
			else
			{
				rowSet=dao.search("select distinct userflag from salaryhistory"+this._withNoLock+" where 1=1 "+strwhere.toString());
				while(rowSet.next())
					userFlagList.add(rowSet.getString(1));
			}	
			
			StringBuffer up_str=new StringBuffer("");
			String read_field=this.ctrlparam.getValue(SalaryCtrlParamBo.READ_FIELD);
	        if(read_field==null|| "".equals(read_field))
	        	read_field="0";
			for(int j=0;j<userFlagList.size();j++)
			{
				String userFlag=(String)userFlagList.get(j);
				LazyDynaBean dataBean=getSalaryPayDate(null,null);  
				String tableName=userFlag+"_salary_"+this.salaryid;
				up_str.setLength(0);
				StringBuffer strupdate=new StringBuffer();
				StringBuffer strupdate2=new StringBuffer();
				for(int i=0;i<itemlist.size();i++)
				{
					String _itemid=((String)itemlist.get(i)).toLowerCase().trim();
					if(DataDictionary.getFieldItem(_itemid)!=null)
					{
						if("0".equals(read_field))
						{
					    	if(!"2".equals(this.userview.analyseFieldPriv(_itemid)))
						    	continue;
						}else{
							if(!"2".equals(this.userview.analyseFieldPriv(_itemid))&&!"1".equals(this.userview.analyseFieldPriv(_itemid)))
						    	continue;
						}
					}
					
					if(Sql_switcher.searchDbServer()!=2)  //不为oracle
					{
						strupdate.append(","+tableName);
						strupdate.append(".");
						strupdate.append(itemlist.get(i));
						strupdate.append("=");				
						strupdate.append("salaryhistory");
						strupdate.append(".");
						strupdate.append(itemlist.get(i)); 
					}
					else
					{
						strupdate.append(","+tableName+"."+itemlist.get(i));
						strupdate2.append(",salaryhistory."+itemlist.get(i));
					}
				}
				if(strupdate.length()==0)
					continue;
				
				if(Sql_switcher.searchDbServer()!=2)  //不为oracle
				{
					up_str.append("update "+tableName+" set "+strupdate.substring(1)+" from "+tableName+",salaryhistory");
					up_str.append(" where "+tableName+".a0100=salaryhistory.a0100 and upper("+tableName+".nbase)=upper(salaryhistory.nbase )");
					up_str.append(" and "+tableName+".a00z0=salaryhistory.a00z0 and "+tableName+".a00z1=salaryhistory.a00z1 ");
					up_str.append(strwhere.toString());
				}
				else
				{
					up_str.append("update "+tableName+" set ("+strupdate.substring(1)+")=");
					up_str.append("(select "+strupdate2.substring(1)+" from salaryhistory where "+tableName+".a0100=salaryhistory.a0100 and upper("+tableName+".nbase)=upper(salaryhistory.nbase ) "  );
					up_str.append(" and "+tableName+".a00z0=salaryhistory.a00z0 and "+tableName+".a00z1=salaryhistory.a00z1 ");
					up_str.append(strwhere.toString()+" ) where exists ");
					up_str.append(" ( select null from salaryhistory where "+tableName+".a0100=salaryhistory.a0100 and upper("+tableName+".nbase)=upper(salaryhistory.nbase ) "  );
					up_str.append(" and "+tableName+".a00z0=salaryhistory.a00z0 and "+tableName+".a00z1=salaryhistory.a00z1 ");
					up_str.append(strwhere.toString()+" ) ");
					
				}
				dao.update(up_str.toString());	 
				 
			}
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * 批量从档案库取得已发放的数据
	 * @param type     =3档案库数据
	 * @param itemlist 引入的薪资项目列表
	 * @return
	 */	
	public boolean batchImportFromArchive_history(HashMap map,String type,ArrayList itemlist)throws GeneralException
	{
		boolean bflag=true;
		int ninit=0;
		try
		{
			/**求得当前处理年月和次数,为了后面导入数据*/
			
			StringBuffer strwhere=new StringBuffer(" ( salaryhistory.curr_user='"+this.userview.getUserId()+"' and ( salaryhistory.sp_flag='02' or salaryhistory.sp_flag='07' ) )");
			/**求得当前处理年月和次数*/
			String currym=(String)map.get("ym");
			String currcount=(String)map.get("count");
			strwhere.append(" and salaryhistory.a00z2="+Sql_switcher.dateValue(currym)+" and salaryhistory.a00z3="+currcount+" and salaryhistory.salaryid="+this.salaryid);
		
			
			String  items=Arrays.toString(itemlist.toArray()); 
			items=items.toUpperCase();	
			ArrayList allUsedFields = DataDictionary.getAllFieldItemList(
					Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
			FieldItem Field=null;
			HashMap allFieldMap=new HashMap();
			for(int i=0;i<allUsedFields.size();i++)
			{
				Field = (FieldItem) allUsedFields.get(i);
				allFieldMap.put(Field.getItemdesc().toLowerCase(),Field);
			}
			//将导入当前记录数据的项目先批量处理
			ArrayList itemList=new ArrayList();
			HashMap setMap=new HashMap();
			
			this.gz_tablename="salaryhistory";
			this.currym=currym;
			this.currcount=currcount;
			
			for(int i=0;i<gzitemlist.size();i++)
			{
				GzItemVo itemvo=(GzItemVo)gzitemlist.get(i);
				String setid=itemvo.getSetname();
				/**变量不处理*/
				if(itemvo.getIsvar()==1)
					continue;
				/**过滤未选中的薪资项目*/
				if(items.indexOf(itemvo.getFldname().toUpperCase())==-1)
					continue;
				/**单位指标或职位指标*/
				if(setid.charAt(0)=='A')
				{
					int nlock=itemvo.getLock();
					int ainit=itemvo.getInitflag();
					int nheap=itemvo.getHeapflag();
					String formula=itemvo.getFormula();
					/**=0不锁,=1锁住    2:导入项  */
					if(nlock==0&&nheap==0&&(ainit==1||ainit==2))
					{
						if(allFieldMap.get(formula.trim().toLowerCase())!=null||DataDictionary.getFieldItem(formula.trim())!=null)
						{
							FieldItem field=(FieldItem)allFieldMap.get(formula.trim().toLowerCase());
							if(field==null)
								field=(FieldItem)DataDictionary.getFieldItem(formula.trim());
							String a_setid=field.getFieldsetid();
							if("e0122".equalsIgnoreCase(field.getItemid())|| "e01a1".equalsIgnoreCase(field.getItemid())|| "b0110".equalsIgnoreCase(field.getItemid()))
							{ 
								field=(FieldItem)DataDictionary.getFieldItem(field.getItemid());
								a_setid=field.getFieldsetid();
							}
							if(setMap.get(a_setid.toUpperCase())!=null)
							{
								ArrayList tempList=(ArrayList)setMap.get(a_setid.toUpperCase());
								tempList.add(itemvo.getFldname()+"`"+field.getItemid());
								setMap.put(a_setid.toUpperCase(), tempList);
							}
							else
							{
								ArrayList tempList=new ArrayList();
								tempList.add(itemvo.getFldname()+"`"+field.getItemid());
								setMap.put(a_setid.toUpperCase(), tempList);
							}
						}
						else
							itemList.add(itemvo);
					}
					else
					{
						itemList.add(itemvo);
					}
				}
				else
					itemList.add(itemvo);
			}
			String dbpres=this.templatevo.getString("cbase");
			String strpre=null;
			/**应用库前缀*/
			String[] dbarr=StringUtils.split(dbpres, ",");		
			String tempWhere=strwhere.toString();
			for(int j=0;j<dbarr.length;j++)
			{
				strpre=dbarr[j];
				
				strwhere.setLength(0);
				strwhere.append(tempWhere);
				batchImportGzItems_history(setMap,strwhere.toString(),strpre);
	

				boolean isE01A1=false; //是否有岗位名称
				for(int i=0;i<gzitemlist.size();i++)
				{
					GzItemVo itemvo=(GzItemVo)gzitemlist.get(i);
					String fldname=itemvo.getFldname();
					if("E01A1".equalsIgnoreCase(fldname))
						isE01A1=true;
				}
				if(isE01A1)
				{
					ContentDAO dao=new ContentDAO(this.conn);
					String sql="update "+gz_tablename+" set E01A1=(select E01A1 from "+strpre+"A01 where "+strpre+"A01.a0100="+gz_tablename+".a0100  ) where exists ";
					sql+=" (select null from "+strpre+"A01 where "+strpre+"A01.a0100="+gz_tablename+".a0100  ) and lower(nbase)='"+strpre.toLowerCase()+"' ";
					if(strwhere!=null&&strwhere.toString().trim().length()>0)
						sql+=" and "+strwhere.toString();
					dao.update(sql);
				}
			
				/**所有项目*/

				for(int i=0;i<itemList.size();i++)
				{
					GzItemVo itemvo=(GzItemVo)itemList.get(i);
					/**过滤未选中的薪资项目*/
					if(items.indexOf(itemvo.getFldname().toUpperCase())==-1)
						continue;
					String setid=itemvo.getSetname();
					/**变量不处理*/
					if(itemvo.getIsvar()==1)
						continue;
					/**单位指标或职位指标*/
					if(setid.charAt(0)!='A')
					{
						computingImportUnitItem(itemvo,strwhere.toString(),strpre,isE01A1);
						//...
					}
					else
					{
						
							/**=0不锁,=1锁住*/
							int nlock=itemvo.getLock();
							if(nlock==0|| "A01z0".equalsIgnoreCase(itemvo.getFldname()))
							{
					
								//有记录才进行下一步计算 2008-11-12  dengcan
								ContentDAO dao=new ContentDAO(this.conn);
								RowSet rowSet=dao.search("select count(nbase) from salaryhistory where lower(nbase)='"+strpre.toLowerCase()+"' and "+strwhere.toString());
								if(rowSet.next())
								{
									if(rowSet.getInt(1)>0)
									{
								
										
									
										ninit=itemvo.getInitflag();
										switch(ninit)
										{
										case 0://清零项，不管它
											break;
										case 1:  //累积项
											computingImportItem(itemvo,strwhere.toString(),strpre);						
											break;
										case 2:  //导入项
											computingImportItem(itemvo,strwhere.toString(),strpre);
											break;
										case 3:  //系统项
											computingImportItem(itemvo,strwhere.toString(),strpre);
											break;
										}
									}
								}
								if(rowSet!=null)
									rowSet.close();
								
							}// nlock end.
					}
					
				}//for i loop end.			
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			bflag=false;
			throw GeneralExceptionHandler.Handle(ex);			
		}
		return bflag;		
	}
	
	
	/**
	 * 批量导入当前记录数据的项目
	 * @param setMap
	 */
	public boolean batchImportGzItems_history(HashMap setMap,String strWhere,String strPre) throws GeneralException
	{
		boolean bflag=true;
		try
		{
			Set set=setMap.keySet();
			DbWizard dbw=new DbWizard(this.conn);
			for(Iterator t=set.iterator();t.hasNext();)
			{
				String key=(String)t.next();
				ArrayList itemList=(ArrayList)setMap.get(key);
				String tablename=strPre+key;
			//	if(key.equalsIgnoreCase("K01")||key.equalsIgnoreCase("B01"))
			//		tablename=key;
				StringBuffer updStr=new StringBuffer("");
				for(int i=0;i<itemList.size();i++)
				{
					String temp=(String)itemList.get(i);
					String[] temps=temp.split("`");
					updStr.append("`salaryhistory."+temps[0]+"="+tablename+"."+temps[1]);
				}
				
				StringBuffer buf=new StringBuffer("");
				if(strWhere.length()==0)
				{
					buf.append(" upper(salaryhistory.nbase)='");
					buf.append(strPre.toUpperCase());
					buf.append("'");
				}
				else
				{
					buf.append(strWhere);
					buf.append(" and upper(nbase)='");
					buf.append(strPre.toUpperCase());
					buf.append("'");
				}
				
				String srcTab=tablename;
				if(!"A01".equalsIgnoreCase(key))
				{
					srcTab="(select * from "+tablename+" a where a.i9999=(select max(b.i9999) from "+tablename+" b where a.a0100=b.a0100  ) ) "+tablename;
				}
				String joinStr="salaryhistory.A0100="+tablename+".A0100";
			//	long a=System.currentTimeMillis();
				dbw.updateRecord("salaryhistory",srcTab,joinStr,updStr.substring(1), buf.toString(),"");
			//	System.out.println(tablename+" : "+(System.currentTimeMillis()-a));
			}
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			bflag=false;
			throw GeneralExceptionHandler.Handle(ex);
		}
		return bflag;
	}
	

	/**
	 * 批量从薪资历史数据表取得已发放的数据(审批引入功能)
	 * @param type     =1同月上次   =2上月同次数据 =4某年某月某次
	 * @param itemlist 引入的薪资项目列表
	 * @return
	 */
	public boolean batchImportFromHisGzTable_history(HashMap map,String type,ArrayList itemlist,String year,String month,String count)throws GeneralException
	{
		boolean bflag=true;
		try
		{
			String strym=null;
			int nc=0,ny=0,nm=0;
			/**求得当前处理年月和次数*/
			String currym=(String)map.get("ym");
			String currcount=(String)map.get("count");
			if("1".equals(type))//=1同月上次
			{
				nc=Integer.parseInt(currcount)-1;
				strym=currym;
			}
			else if("2".equals(type))
			{
				nc=Integer.parseInt(currcount);
				String[] tmp=StringUtils.split(currym,"-");
				ny=Integer.parseInt(tmp[0]);
				nm=Integer.parseInt(tmp[1]);
				if(nm==1)
				{
					ny=ny-1;
					nm=12;
				}
				else
				{
					nm=nm-1;
				}
				strym=String.valueOf(ny)+"-"+String.valueOf(nm)+"-"+"01";
			}
			else if("4".equals(type))
			{
				nc=Integer.parseInt(count);
				ny=Integer.parseInt(year);
				nm=Integer.parseInt(month);
				strym=String.valueOf(ny)+"-"+String.valueOf(nm)+"-"+"01";
			}
			/**更新串*/
			StringBuffer strupdate=new StringBuffer();
			String read_field=this.ctrlparam.getValue(SalaryCtrlParamBo.READ_FIELD);
			if(read_field==null|| "".equals(read_field))
	        	read_field="0";
			for(int i=0;i<itemlist.size();i++)
			{
				String _itemid=((String)itemlist.get(i)).toLowerCase().trim();
				if(DataDictionary.getFieldItem(_itemid)!=null)
				{
					if("0".equals(read_field))
					{
				    	if(!"2".equals(this.userview.analyseFieldPriv(_itemid)))
				    		continue;
					}else{
						if(!"2".equals(this.userview.analyseFieldPriv(_itemid))&&!"1".equals(this.userview.analyseFieldPriv(_itemid)))
							continue;
					}
				}
				
				strupdate.append("salaryhistory");
				strupdate.append(".");
				strupdate.append(itemlist.get(i));
				strupdate.append("=");				
				strupdate.append("a");
				strupdate.append(".");
				strupdate.append(itemlist.get(i));
				strupdate.append("`");
			}//for loop end.
			if(strupdate.length()>0)
				strupdate.setLength(strupdate.length()-1);
			else
				return bflag;
			
			{
				/**连接串*/
				StringBuffer strjoin=new StringBuffer();
				strjoin.append("salaryhistory.A0100=");
				strjoin.append("a");
				strjoin.append(".A0100 and upper(");
				strjoin.append("salaryhistory.NBASE)=upper(");
				strjoin.append("a");
				strjoin.append(".NBASE) ");
				
				
				
				/**条件串*/
				StringBuffer strwhere=new StringBuffer();
				strwhere.append("a.A00Z2=");
				strwhere.append(Sql_switcher.dateValue(strym));
				strwhere.append(" and a.A00Z3=");
				strwhere.append(nc);
				strwhere.append(" and a.salaryid=");
				strwhere.append(this.salaryid);
				
				DbWizard dbw=new DbWizard(this.conn);
				StringBuffer stdwhere=new StringBuffer(" salaryhistory.a00z2="+Sql_switcher.dateValue(currym)+" and salaryhistory.a00z3="+currcount+" and salaryhistory.salaryid="+this.salaryid);
				stdwhere.append(" and ( salaryhistory.curr_user='"+this.userview.getUserId()+"' and ( salaryhistory.sp_flag='02' or salaryhistory.sp_flag='07' ) )");
				
				dbw.updateRecord("salaryhistory","salaryhistory a", strjoin.toString(), strupdate.toString(),stdwhere.toString(),strwhere.toString());
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			bflag=false;
			throw GeneralExceptionHandler.Handle(ex);			
		}
		return bflag;
	}
	
	
	
	
	
	
	
	/**
	 * 批量引入数据
	 * @param type     =1同月上次   =2上月同次数据 =3档案库数据　=4某年某月某次
	 * @param itemlist 引入的薪资项目列表
	 * @return
	 */		
	public boolean batchImport(String type,ArrayList itemlist,String year,String month,String count)throws GeneralException
	{
		String  royalty_valid=ctrlparam.getValue(SalaryCtrlParamBo.ROYALTIES,"valid");
		if(royalty_valid!=null&& "1".equals(royalty_valid))
		{
			
			reImportRoyaltyData(itemlist);
			delNoConditionData2(gz_tablename);
			return true;
		}
		else
		{
			if("3".equalsIgnoreCase(type))
				return batchImportFromArchive(type,itemlist);
			else
				return batchImportFromHisGzTable2(type,itemlist,year,month,count);
		}
	}
	/**
	 * 批量从档案库取得已发放的数据
	 * @param type     =3档案库数据
	 * @param itemlist 引入的薪资项目列表
	 * @return
	 */	
	public boolean batchImportFromArchive(String type,ArrayList itemlist)throws GeneralException
	{
		boolean bflag=true;
		int ninit=0;
		try
		{
			/**求得当前处理年月和次数,为了后面导入数据*/
			//getYearMonthCount("01");			
			getYearMonthCount2();   //dengcan
			StringBuffer strwhere=new StringBuffer();

			String  items=Arrays.toString(itemlist.toArray()); 
			
			items=items.toUpperCase();
			String payitem=ctrlparam.getValue(SalaryCtrlParamBo.PAY_FLAG);
			/**仅导入正常发薪记录*/
			if(payitem.length()!=0)
			{
				strwhere.append("(");
				strwhere.append(this.gz_tablename);
				strwhere.append(".");
				strwhere.append(payitem);
				strwhere.append("='0' ");
				strwhere.append(" or ");
				strwhere.append(this.gz_tablename);
				strwhere.append(".");
				strwhere.append(payitem);				
				strwhere.append("='' or ");
				strwhere.append(this.gz_tablename);
				strwhere.append(".");
				strwhere.append(payitem);
				strwhere.append(" is null)");
			}
			/**需要审批,仅导入起草和驳回记录*/ 
			if(isApprove())
			{
				if(strwhere.length()!=0)
					strwhere.append(" and ");
				strwhere.append(this.gz_tablename);
				strwhere.append(".");
				strwhere.append("sp_flag in('01','07')");				
			}	
			else {
                //控制已提交的数据是否能批量引入  wangrd  2013-11-14      
                if (!isAllowEditSubdata()) {                    
                    if(strwhere.length()!=0)
                        strwhere.append(" and ");
                    strwhere.append(this.gz_tablename);
                    strwhere.append(".");
                    strwhere.append("sp_flag in('01','07')");   
                }
			}
//			共享薪资类别，其他操作人员引入数据 
			if(this.manager.length()>0&&!this.userview.getUserName().equalsIgnoreCase(this.manager))
			{
				if(strwhere.length()!=0)
					strwhere.append(" and ");
				strwhere.append(" sp_flag2 in ('01','07')");
				
			}
			
			
			
			////////////////////////////
			ArrayList allUsedFields = DataDictionary.getAllFieldItemList(
					Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
			FieldItem Field=null;
			HashMap allFieldMap=new HashMap();
			for(int i=0;i<allUsedFields.size();i++)
			{
				Field = (FieldItem) allUsedFields.get(i);
				allFieldMap.put(Field.getItemdesc().toLowerCase(),Field);
			}
	
			//将导入当前记录数据的项目先批量处理
			ArrayList itemList=new ArrayList();
			HashMap setMap=new HashMap();
			for(int i=0;i<gzitemlist.size();i++)
			{
				GzItemVo itemvo=(GzItemVo)gzitemlist.get(i);
				String setid=itemvo.getSetname();
				/**变量不处理*/
				if(itemvo.getIsvar()==1)
					continue;
				/**过滤未选中的薪资项目*/
				if(items.indexOf(itemvo.getFldname().toUpperCase())==-1)
					continue;
				/**单位指标或职位指标*/
				if(setid.charAt(0)=='A')
				{
					int nlock=itemvo.getLock();
					int ainit=itemvo.getInitflag();
					int nheap=itemvo.getHeapflag();
					String formula=itemvo.getFormula();
					/**=0不锁,=1锁住    2:导入项  */
					if(nlock==0&&nheap==0&&(ainit==1||ainit==2))
					{
						
						if(allFieldMap.get(formula.trim().toLowerCase())!=null||DataDictionary.getFieldItem(formula.trim())!=null)
						{
							FieldItem field=null;
							if(allFieldMap.get(formula.trim().toLowerCase())!=null) //20141209 dengcan
								field=(FieldItem)((FieldItem)allFieldMap.get(formula.trim().toLowerCase())).clone();
							if(field==null)
								field=(FieldItem)DataDictionary.getFieldItem(formula.trim()).clone();
							
							//公式定义成部门、岗位时，得到的fielditem的setid为K01    DENGCAN 20141025
							if("e0122".equalsIgnoreCase(field.getItemid())|| "e01a1".equalsIgnoreCase(field.getItemid())|| "b0110".equalsIgnoreCase(field.getItemid()))
							{
								field.setFieldsetid("A01");
							}
							
							String a_setid=field.getFieldsetid();
							
							
							if(setMap.get(a_setid.toUpperCase())!=null)
							{
								ArrayList tempList=(ArrayList)setMap.get(a_setid.toUpperCase());
								tempList.add(itemvo.getFldname()+"`"+field.getItemid());
								setMap.put(a_setid.toUpperCase(), tempList);
							}
							else
							{
								ArrayList tempList=new ArrayList();
								tempList.add(itemvo.getFldname()+"`"+field.getItemid());
								setMap.put(a_setid.toUpperCase(), tempList);
							}
						}
						else
							itemList.add(itemvo);
					}
					else
					{
						itemList.add(itemvo);
					}
				}
				else
					itemList.add(itemvo);
			}
			
			
			
			String dbpres=this.templatevo.getString("cbase");
			String strpre=null;
			/**应用库前缀*/
			String[] dbarr=StringUtils.split(dbpres, ",");	
			
			String tempWhere=strwhere.toString();
			
			for(int j=0;j<dbarr.length;j++)
			{
				strpre=dbarr[j];
				
				strwhere.setLength(0);
				strwhere.append(tempWhere);
				
				importMenSql_where="";
				importMenSql_where+=" select A0100 from ";
				importMenSql_where+=this.gz_tablename;
				importMenSql_where+=" where  lower(nbase)='";
				importMenSql_where+=strpre.toLowerCase();
				importMenSql_where+="'";
				
				String aflag=this.ctrlparam.getValue(SalaryCtrlParamBo.PRIV_MODE,"flag");  // 人员范围权限过滤标志  1：有  
				//共享薪资类别，其他操作人员引入数据 (dengcan 2008/6/26 )
				if(this.manager.length()>0&&!this.userview.getUserName().equalsIgnoreCase(this.manager)) //&&aflag!=null&&aflag.equals("1"))
				{
					
					//权限过滤 
					if("1".equals(this.controlByUnitcode))
					{
					 
						String whl_str=getWhlByUnits();
						if(whl_str.length()>0)
						{
							if(strwhere.length()>0)
							{
								strwhere.append(whl_str);
								importMenSql_where+=whl_str;
							}
							else
							{
								strwhere.append(" 1=1 "+whl_str);
								importMenSql_where+=whl_str;
							}
							
						}
					}
					else
					{
						String whereIN=InfoUtils.getWhereINSql(this.userview,strpre);
						whereIN="select a0100 "+whereIN;	
						if(strwhere.length()>0)
							strwhere.append(" and ");
						strwhere.append(" "+this.gz_tablename+".a0100 in ( "+whereIN+" )");
						importMenSql_where+=" and a0100 in ( "+whereIN+" )";
					}
					
					
					
					
				}
				
				batchImportGzItems(setMap,strwhere.toString(),strpre);
				
				boolean isE01A1=false; //是否有岗位名称
				for(int i=0;i<gzitemlist.size();i++)
				{
					GzItemVo itemvo=(GzItemVo)gzitemlist.get(i);
					String fldname=itemvo.getFldname();
					if("E01A1".equalsIgnoreCase(fldname))
						isE01A1=true;
				}
				if(isE01A1)
				{
					ContentDAO dao=new ContentDAO(this.conn);
					String sql="update "+gz_tablename+" set E01A1=(select E01A1 from "+strpre+"A01 where "+strpre+"A01.a0100="+gz_tablename+".a0100  ) where exists ";
					sql+=" (select null from "+strpre+"A01 where "+strpre+"A01.a0100="+gz_tablename+".a0100  ) and lower(nbase)='"+strpre.toLowerCase()+"' ";
					if(strwhere!=null&&strwhere.toString().trim().length()>0)
						sql+=" and "+strwhere.toString();
					dao.update(sql);
				}
				
				
				/**所有项目*/
			//	for(int i=0;i<gzitemlist.size();i++)
				for(int i=0;i<itemList.size();i++)
				{
					GzItemVo itemvo=(GzItemVo)itemList.get(i);
					//GzItemVo itemvo=(GzItemVo)gzitemlist.get(i);
					/**过滤未选中的薪资项目*/
					if(items.indexOf(itemvo.getFldname().toUpperCase())==-1)
						continue;
					String setid=itemvo.getSetname();
					/**变量不处理*/
					if(itemvo.getIsvar()==1)
						continue;
					/**单位指标或职位指标*/
					if(setid.charAt(0)!='A')
					{
						computingImportUnitItem(itemvo,strwhere.toString(),strpre,isE01A1);
						//...
					}
					else
					{
						
							/**=0不锁,=1锁住*/
							int nlock=itemvo.getLock();
							if(nlock==0|| "A01z0".equalsIgnoreCase(itemvo.getFldname()))
							{
					
								//有记录才进行下一步计算 2008-11-12  dengcan
								ContentDAO dao=new ContentDAO(this.conn);
								RowSet rowSet=dao.search("select count(nbase) from "+this.gz_tablename+" where lower(nbase)='"+strpre.toLowerCase()+"'");
								if(rowSet.next())
								{
									if(rowSet.getInt(1)>0)
									{
								
										
									
										ninit=itemvo.getInitflag();
										switch(ninit)
										{
										case 0://清零项，不管它
											break;
										case 1:  //累积项
											computingImportItem(itemvo,strwhere.toString(),strpre);						
											break;
										case 2:  //导入项
											computingImportItem(itemvo,strwhere.toString(),strpre);
											break;
										case 3:  //系统项
											computingImportItem(itemvo,strwhere.toString(),strpre);
											break;
										}
									}
								}
								if(rowSet!=null)
									rowSet.close();
								
							}// nlock end.
					}
					
				}//for i loop end.			
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			bflag=false;
			throw GeneralExceptionHandler.Handle(ex);			
		}
		return bflag;		
	}
	
	
	
	
	

	public boolean batchImportFromHisGzTable2(String type,ArrayList itemlist,String year,String month,String count)throws GeneralException
	{
		boolean bflag=true;
		try
		{
			
			if(this.manager!=null&&this.manager.length()>0)
			{
				HashMap map=getMaxYearMonthCount();
				String whl_str=getWhlByUnits();
				boolean isApprove=isApprove();
				bflag=DbNameBo.batchImportFromHisGzTable(conn, map,this.userview, this.gz_tablename, this.salaryid, this.manager, this.templatevo,
						this.controlByUnitcode, whl_str, isApprove, type, itemlist, year, month, count,this.ctrlparam);
			}
			else
				bflag=batchImportFromHisGzTable(type,itemlist,year,month,count);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			bflag=false;
			throw GeneralExceptionHandler.Handle(ex);			
		}
		return bflag;
	}
	
	
	/**
	 * 批量从薪资历史数据表取得已发放的数据
	 * @param type     =1同月上次   =2上月同次数据 =4某年某月某次
	 * @param itemlist 引入的薪资项目列表
	 * @return
	 */
	public boolean batchImportFromHisGzTable(String type,ArrayList itemlist,String year,String month,String count)throws GeneralException
	{
		boolean bflag=true;
		String tablename="salaryhistory";
		try
		{
			String tempTable="t#"+this.userview.getUserName()+"_gz";
			String strym=null;
			int nc=0,ny=0,nm=0;
			/**求得当前处理年月和次数*/
			//getYearMonthCount("01");
			HashMap map=getMaxYearMonthCount();
			currym=(String)map.get("ym");
			currcount=(String)map.get("count");
			if("1".equals(type))//=1同月上次
			{
				nc=Integer.parseInt(currcount)-1;
				strym=currym;
			}
			else if("2".equals(type))
			{
				nc=Integer.parseInt(currcount);
				String[] tmp=StringUtils.split(currym,"-");
				ny=Integer.parseInt(tmp[0]);
				nm=Integer.parseInt(tmp[1]);
				if(nm==1)
				{
					ny=ny-1;
					nm=12;
				}
				else
				{
					nm=nm-1;
				}
				strym=String.valueOf(ny)+"-"+String.valueOf(nm)+"-"+"01";
			}
			else if("4".equals(type))
			{
				nc=Integer.parseInt(count);
				ny=Integer.parseInt(year);
				nm=Integer.parseInt(month);
				strym=String.valueOf(ny)+"-"+String.valueOf(nm)+"-"+"01";
			}
			/**更新串*/
			StringBuffer strupdate=new StringBuffer();
			StringBuffer field_str=new StringBuffer("");
			String read_field=this.ctrlparam.getValue(SalaryCtrlParamBo.READ_FIELD);
		    if(read_field==null|| "".equals(read_field))
		        read_field="0";
			for(int i=0;i<itemlist.size();i++)
			{
				String _itemid=((String)itemlist.get(i)).toLowerCase().trim();
				if(DataDictionary.getFieldItem(_itemid)!=null)
				{
					if("0".equals(read_field))
					{
				    	if(!"2".equals(userview.analyseFieldPriv(_itemid)))
					    	continue;
					}else
					{
						if(!"2".equals(userview.analyseFieldPriv(_itemid))&&!"1".equals(userview.analyseFieldPriv(_itemid)))
					    	continue;
					}
				}
				
				strupdate.append(this.gz_tablename);
				strupdate.append(".");
				strupdate.append(itemlist.get(i));
				strupdate.append("=");				
				strupdate.append(tempTable);
				strupdate.append(".");
				strupdate.append(itemlist.get(i));
				strupdate.append("`");
				
				field_str.append(","+itemlist.get(i));
			}//for loop end.
			 
			if(strupdate.length()>0)
				strupdate.setLength(strupdate.length()-1);
			else
				return bflag;
			String aflag=this.ctrlparam.getValue(SalaryCtrlParamBo.PRIV_MODE,"flag");  // 人员范围权限过滤标志  1：有  
			
			String strwhere1=" salaryhistory.A00Z2="+Sql_switcher.dateValue(strym)+" and salaryhistory.A00Z3="+nc+" and salaryhistory.salaryid="+this.salaryid+"";
			boolean temp=this.isHaveHistory(strwhere1);
			if(!temp){
				tablename="salaryarchive";
			}
			
			//建临时表优化效率
			DbWizard dbw=new DbWizard(this.conn);
			
	//		if(dbw.isExistTable(tempTable, false))	// modify by xiaoyun 添加参数，解决报错的问题 2014-9-30
				dbw.dropTable(tempTable);
			StringBuffer str_sql=new StringBuffer("");
			StringBuffer sql=new StringBuffer("");
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				str_sql.append("create Table "+tempTable+" as "); 
			str_sql.append(" select "+field_str.substring(1)+",A0100,NBASE ");
			if(Sql_switcher.searchDbServer()!=Constant.ORACEL)
				str_sql.append(" into "+tempTable);
			str_sql.append(" from "+tablename+" where ");
			str_sql.append(""+tablename+".A00Z2=");
			str_sql.append(Sql_switcher.dateValue(strym));
			str_sql.append(" and "+tablename+".A00Z3=");
			str_sql.append(nc);
			str_sql.append(" and "+tablename+".salaryid=");
			str_sql.append(this.salaryid);
			
			
			//当上次发放某人有多条记录时会出错
			str_sql.append(" and exists (select null from (");
			str_sql.append(" select max(a00z1) a00z1, a0100,lower(nbase) nbase  from "+tablename+" where "+tablename+".A00Z2="+Sql_switcher.dateValue(strym));  
			str_sql.append(" and "+tablename+".userflag='"+this.userview.getUserName()+"' and "+tablename+".A00Z3="+nc+" and "+tablename+".salaryid="+salaryid+" group by a0100,lower(nbase)");
			str_sql.append(" ) a where "+tablename+".a00z1=a.a00z1 and "+tablename+".a0100=a.a0100 and lower("+tablename+".nbase)=lower(a.nbase) )");
			

			sql.append("insert into "+tempTable);
			sql.append(" ("+field_str.substring(1)+",A0100,NBASE) select "+field_str.substring(1)+",A0100,NBASE ");
			sql.append(" from "+tablename+" where ");
			sql.append(""+tablename+".A00Z2=");
			sql.append(Sql_switcher.dateValue(strym));
			sql.append(" and "+tablename+".A00Z3=");
			sql.append(nc);
			sql.append(" and "+tablename+".salaryid=");
			sql.append(this.salaryid);
			//↓ 把所有符合的全拿过来
			sql.append(" and exists (select null from (");
			sql.append(" select max(a00z1) a00z1, a0100,lower(nbase) nbase  from "+tablename+" where "+tablename+".A00Z2="+Sql_switcher.dateValue(strym));  
			sql.append(" and "+tablename+".A00Z3="+nc+" and "+tablename+".salaryid="+salaryid+" group by a0100,lower(nbase)");
			sql.append(" ) a where "+tablename+".a00z1=a.a00z1 and "+tablename+".a0100=a.a0100 and lower("+tablename+".nbase)=lower(a.nbase) )");
			//↓ 把之前自己插入的数据过滤掉
 			sql.append(" and  not exists (select a0100 from "+tempTable+"  ");
			sql.append(" where lower("+tablename+".a0100)= lower("+tempTable+".a0100) and  lower("+tablename+".nbase)=lower("+tempTable+".nbase)  ) ");
				
					
			if(this.manager.length()>0&&!this.userview.getUserName().equalsIgnoreCase(this.manager))
			{
				if(Sql_switcher.searchDbServer()==Constant.ORACEL)
					dbw.execute("create Table "+tempTable+" as select "+field_str.substring(1)+",A0100,NBASE from "+tablename+" where 1=2");
				else
					dbw.execute(" select "+field_str.substring(1)+",A0100,NBASE into "+tempTable+" from "+tablename+" where 1=2");
			}
			else{
				dbw.execute(str_sql.toString());
				dbw.execute(sql.toString());
			}
			
			
			//共享薪资类别，其他操作人员引入数据 (dengcan 2008/6/26 )
			if(this.manager.length()>0&&!this.userview.getUserName().equalsIgnoreCase(this.manager))//&&aflag!=null&&aflag.equals("1"))
			{
				String dbpres=this.templatevo.getString("cbase");
				//应用库前缀
				String[] dbarr=StringUtils.split(dbpres, ",");
				for(int i=0;i<dbarr.length;i++)
				{
					String pre=dbarr[i];
				    
					dbw.execute("delete from "+tempTable);
					StringBuffer _sql=new StringBuffer("");
					_sql.append("insert into "+tempTable+" ("+field_str.substring(1)+",A0100,NBASE) select "+field_str.substring(1)+",A0100,NBASE from "+tablename+" where ");
					_sql.append(""+tablename+".A00Z2=");
					_sql.append(Sql_switcher.dateValue(strym));
					_sql.append(" and "+tablename+".A00Z3=");
					_sql.append(nc);
					_sql.append(" and "+tablename+".salaryid=");
					_sql.append(this.salaryid+" and upper("+tablename+".NBASE)='"+pre.toUpperCase()+"'");
					
					//当上次发放某人有多条记录时会出错
					
					_sql.append(" and exists (select null from (");
					_sql.append(" select max(a00z1) a00z1, a0100   from "+tablename+" where "+tablename+".A00Z2="+Sql_switcher.dateValue(strym));  
					_sql.append(" and "+tablename+".A00Z3="+nc+" and "+tablename+".salaryid="+salaryid+"  and upper("+tablename+".NBASE)='"+pre.toUpperCase()+"' group by a0100 ");
					_sql.append(" ) a where "+tablename+".a00z1=a.a00z1 and "+tablename+".a0100=a.a0100   )");
					
					
					
					//权限过滤
					String _whereIN="";
					if("1".equals(this.controlByUnitcode))
					{
					 
						String whl_str=getWhlByUnits();
						if(whl_str.length()>0)
							_sql.append(whl_str);
					}
					else
					{
						_whereIN=InfoUtils.getWhereINSql(this.userview,pre);
						_whereIN="select a0100 "+_whereIN;	
						_sql.append(" and "+tablename+".a0100 in ( "+_whereIN+" )");
					}
					dbw.execute(_sql.toString()); 
					
					
					//连接串
					StringBuffer strjoin=new StringBuffer();
					strjoin.append(this.gz_tablename);
					strjoin.append(".A0100=");
					strjoin.append(tempTable);
					strjoin.append(".A0100 and upper(");	
					strjoin.append(this.gz_tablename);
					strjoin.append(".NBASE)='"+pre.toUpperCase()+"'");
				//	strjoin.append(" and upper(salaryhistory.NBASE)='"+pre.toUpperCase()+"'");
					
					//条件串
					StringBuffer strwhere=new StringBuffer(" 1=1 ");
			/*		strwhere.append("salaryhistory.A00Z2=");
					strwhere.append(Sql_switcher.dateValue(strym));
					strwhere.append(" and salaryhistory.A00Z3=");
					strwhere.append(nc);
					strwhere.append(" and salaryhistory.salaryid=");
					strwhere.append(this.salaryid);
					//权限过滤
					String whereIN="";
					if(this.controlByUnitcode.equals("1"))
					{
					 
						String whl_str=getWhlByUnits();
						if(whl_str.length()>0)
							strwhere.append(whl_str);
					}
					else
					{
						whereIN=InfoUtils.getWhereINSql(this.userview,pre);
						whereIN="select a0100 "+whereIN;	
						strwhere.append(" and salaryhistory.a0100 in ( "+whereIN+" )");
					}
					*/
					//需要审批
					if(isApprove())
					{
						strwhere.append(" and ");
						strwhere.append(this.gz_tablename);
						strwhere.append(".");
						strwhere.append("sp_flag in('01','07')");				
					}
					else {
	                    //控制已提交的数据是否能批量引入  wangrd  2013-11-14		     
		                if (!isAllowEditSubdata()) {
	                        strwhere.append(" and ");
	                        strwhere.append(this.gz_tablename);
	                        strwhere.append(".");
	                        strwhere.append("sp_flag in('01','07')");   
	                    }
					}
//					共享薪资类别，其他操作人员引入数据 
					if(this.manager.length()>0&&!this.userview.getUserName().equalsIgnoreCase(this.manager))
					{
						strwhere.append(" and "+this.gz_tablename+".sp_flag2 in ('01','07')");
					}
					
					
					StringBuffer stdwhere=new StringBuffer(" exists ( select null from "+tempTable+" where  "+strjoin.toString());
					stdwhere.append(" and "+strwhere.toString()+" )");
					
					dbw.updateRecord(this.gz_tablename,tempTable, strjoin.toString(), strupdate.toString(),stdwhere.toString(),strwhere.toString());
					
					
				}
			}
			else  
			{
				/**连接串*/
				StringBuffer strjoin=new StringBuffer();
				strjoin.append(this.gz_tablename);
				strjoin.append(".A0100=");
				strjoin.append(tempTable);
				strjoin.append(".A0100 and upper(");	
				strjoin.append(this.gz_tablename);
				strjoin.append(".NBASE)=upper(");
				strjoin.append(tempTable);
				strjoin.append(".NBASE) ");
				/**条件串*/
				StringBuffer strwhere=new StringBuffer(" 1=1 ");
			/*	strwhere.append("salaryhistory.A00Z2=");
				strwhere.append(Sql_switcher.dateValue(strym));
				
				strwhere.append(" and salaryhistory.A00Z3=");
				strwhere.append(nc);
				strwhere.append(" and salaryhistory.salaryid=");
				strwhere.append(this.salaryid);*/
				/**需要审批*/ 
				if(isApprove())
				{
					strwhere.append(" and ");
					strwhere.append(this.gz_tablename);
					strwhere.append(".");
					strwhere.append("sp_flag in('01','07')");				
				}
				else {
	                //控制已提交的数据是否能批量引入  wangrd  2013-11-14        
	                if (isAllowEditSubdata()) {
	                    strwhere.append(" and ");
	                    strwhere.append(this.gz_tablename);
	                    strwhere.append(".");
	                    strwhere.append("sp_flag in('01','07','06')");                     
	                }
	                else {
	                    strwhere.append(" and ");
	                    strwhere.append(this.gz_tablename);
	                    strwhere.append(".");
	                    strwhere.append("sp_flag in('01','07')");   
	                }
				}
//				共享薪资类别，其他操作人员引入数据 
				if(this.manager.length()>0&&!this.userview.getUserName().equalsIgnoreCase(this.manager))
				{
					strwhere.append(" and "+this.gz_tablename+".sp_flag2 in ('01','07')");
				} 
				StringBuffer stdwhere=new StringBuffer(" exists ( select null from "+tempTable+" where  "+strjoin.toString());
				stdwhere.append(" and "+strwhere.toString()+" )");
				
				dbw.updateRecord(this.gz_tablename,tempTable, strjoin.toString(), strupdate.toString(),stdwhere.toString(),strwhere.toString());
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			bflag=false;
			throw GeneralExceptionHandler.Handle(ex);			
		}
		return bflag;
	}
	/**
	 * 判断历史表里面是否有对应条件的数据，否则去归档表取值 zhaoxg bug 0040839
	 * @param strwhere
	 * @return
	 * @throws GeneralException
	 */
	public boolean isHaveHistory(String strwhere) throws GeneralException{
		boolean flag=false;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String sql = "select salaryid from salaryhistory where "+strwhere+"";
			RowSet rs=dao.search(sql);
			if(rs.next()){
				flag=true;
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);			
		}
		return flag;
	}
	/**
	 * 查询标准表指标列表
	 * @return
	 * @throws GeneralException
	 */
	private ArrayList searchStdTableFieldList()throws GeneralException
	{
		ArrayList fieldlist=new ArrayList();
		try
		{
			ArrayList stdlist=new ArrayList();
			StringBuffer buf=new StringBuffer();
			/**查询执行标准的计算公式*/
			buf.append("select standid from salaryformula where salaryid=");
			buf.append(this.salaryid);
			buf.append(" and standid>0 and runflag=1");
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rset=dao.search(buf.toString());
			while(rset.next())
				stdlist.add(rset.getString("standid"));
			if(stdlist.size()==0)
				return fieldlist;
			if(rset!=null)
				rset.close();
			StringBuffer stdbuf=new StringBuffer();
			for(int i=0;i<stdlist.size();i++)
			{
				stdbuf.append(stdlist.get(i));
				stdbuf.append(",");
			}//for i loop end.
			stdbuf.setLength(stdbuf.length()-1);
			buf.setLength(0);
			/**薪资标准表*/
			buf.append("select id from gz_stand where id in(");
			buf.append(stdbuf.toString());
			buf.append(")");
			rset=dao.search(buf.toString());
			while(rset.next())
			{
				//...薪资标准表涉及到指标列表				
				SalaryStandardBo stdbo=new SalaryStandardBo(this.conn,rset.getString("id"),"");
				fieldlist.addAll(stdbo.getGzStandFactorList(1));
				fieldlist.addAll(stdbo.getGzStandFactorList(2));

			}// while loop end.
			rset.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return fieldlist;
	}
	/**
	 * 升级薪资表结构，临时用户表
	 * @throws GeneralException
	 */
	private void upgradeGzTableStruct()throws GeneralException
	{
		StringBuffer buf=new StringBuffer();
		try
		{ 
			
			DbWizard dbw=new DbWizard(this.conn);
			 
			ContentDAO dao=new ContentDAO(this.conn);
			RecordVo vo=new RecordVo(this.gz_tablename);	
			Table table=new Table(this.gz_tablename);
			Field field=null;
			 
			/**A00Z2发放日期*/
			if(!(vo.hasAttribute("a00z2")||vo.hasAttribute("A00Z2")))
			{
				field=new Field("A00Z2",ResourceFactory.getProperty("gz.columns.a00z2"));
				field.setDatatype(DataType.DATE);
				table.addField(field);
				/**发放次数*/
				field=new Field("A00Z3",ResourceFactory.getProperty("gz.columns.a00z3"));
				field.setDatatype(DataType.INT);
				table.addField(field);
				dbw.addColumns(table);
				/**正常情况下,发放日期=归属日期,发放次数=归属次数*/
				buf.append("update ");
				buf.append(this.gz_tablename);
				buf.append(" set A00Z2=A00Z0,A00Z3=A00Z1");
				dao.update(buf.toString());					
				/**A00Z0,A00Z1,把可为空改成不能为空*/
				table.clear();
				field=new Field("A00Z1","A00Z1");
				field.setDatatype(DataType.INT);
				field.setLength(10);
				field.setNullable(false);
				table.addField(field);
				field=new Field("A00Z0","A00Z0");
				field.setDatatype(DataType.DATE);
				field.setNullable(false);
				table.addField(field);
				dbw.alterColumns(table);
				
				/**修改主键*/
				/**先删除主键索引*/
				dbw.dropPrimaryKey(this.gz_tablename);
				table.clear();
				field=new Field("NBASE","NBASE");
				field.setKeyable(true);
				table.addField(field);
				field=new Field("A0100","A0100");
				field.setKeyable(true);
				table.addField(field);				
				field=new Field("A00Z0","A00Z0");
				field.setKeyable(true);
				table.addField(field);					
				field=new Field("A00Z1","A00Z1");
				field.setKeyable(true);
				table.addField(field);			
				dbw.addPrimaryKey(table);
			}//
			/**审批状态字段*/
			if(!(vo.hasAttribute("sp_flag")||vo.hasAttribute("SP_FLAG")))
			{
				table.clear();
				field=new Field("sp_flag","sp_flag");
				field.setDatatype(DataType.STRING);
				field.setLength(2);
				table.addField(field);
				dbw.addColumns(table);				
			}
			
			
			if(!(vo.hasAttribute("appprocess")||vo.hasAttribute("APPPROCESS")))
			{
				table.clear();
				field=new Field("appprocess","appprocess");
				field.setDatatype(DataType.CLOB);
				table.addField(field);
				dbw.addColumns(table);				
			}
			
			if(!(vo.hasAttribute("userflag")||vo.hasAttribute("userflag")))
			{
				table.clear();
				field=new Field("userflag","userflag");
				field.setDatatype(DataType.STRING);
				field.setLength(50);
				table.addField(field);
				dbw.addColumns(table);			
			}
			
			if(!(vo.hasAttribute("e0122_o")||vo.hasAttribute("E0122_O")))
			{
				table.clear();
				field=new Field("E0122_O","E0122_O");
				field.setDatatype(DataType.INT);
				field.setLength(10);
				table.addField(field);
				dbw.addColumns(table);				
			}
			
			if(!(vo.hasAttribute("b0110_o")||vo.hasAttribute("B0110_O")))
			{
				table.clear();
				field=new Field("B0110_O","B0110_O");
				field.setDatatype(DataType.INT);
				field.setLength(10);
				table.addField(field);
				dbw.addColumns(table);				
			}
			
			if(!(vo.hasAttribute("dbid")||vo.hasAttribute("DBID")))
			{
				table.clear();
				field=new Field("dbid","dbid");
				field.setDatatype(DataType.INT);
				field.setLength(10);
				table.addField(field);
				dbw.addColumns(table);				
			} 
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);				
		}
	}
	/**
	 * 薪资类别中项目定义和已生成薪资表结构进行分析，保持定义和
	 * 薪资表结构一致,包括两边不同指标，或数据类型发生变化的指标
	 * @throws GeneralException
	 */
	private void updateGzDataTable() throws GeneralException
	{
		try
		{ 
			
			DBMetaModel dbmodel=new DBMetaModel(this.conn);
			dbmodel.reloadTableModel(gz_tablename);	
			/**升级表结构*/
			upgradeGzTableStruct();		
			/**升级薪资历史数据表*/
			upgradeGzHisTableStruct();
			/**重新加载数据模型*/
			 
			dbmodel.reloadTableModel(gz_tablename);	
			dbmodel.reloadTableModel("salaryhistory");	
			upgradGzHisTableStruct2();
			 
			 
			dbmodel.reloadTableModel("salaryhistory");
			RecordVo vo=new RecordVo(this.gz_tablename);
			ArrayList list=vo.getModelAttrs();
			StringBuffer buf=new StringBuffer();
			 
			for(int i=0;i<list.size();i++)
			{
				String name=(String)list.get(i);
				buf.append(name.toUpperCase());
				buf.append(",");
			}//for i loop end.
			StringBuffer buf0=new StringBuffer();
			ArrayList addlist=new ArrayList();
			boolean isAddFlag=true;  //临时表是否有追加字段
			/**如果定义中有，而薪资表中没有，则增加此字段*/
			for(int i=0;i<this.fieldlist.size();i++)
			{
				Field field=(Field)this.getFieldlist().get(i);
				String name=field.getName().toUpperCase();
				/**如果未找到，则追加*/
				if(buf.indexOf(name)==-1)
				{
					addlist.add(field);
					if("add_flag".equalsIgnoreCase(name))
						isAddFlag=false;
				}
				buf0.append(name);
				buf0.append(",");
			}//for i loop end.
			 
			//临时变量
			ArrayList midVariableList=getMidVariableList();
			for(int i=0;i<midVariableList.size();i++)
			{
				FieldItem item=(FieldItem)midVariableList.get(i);
				String fieldname=item.getItemid();
				/**变量如果未加，则构建*/		
				if(buf.indexOf(fieldname.toUpperCase()+",")==-1)
				{
					Field field=item.cloneField();
					addlist.add(field);
				}//if end.
			}//for i loop end.
			
			 
			/**如果定义中没有，而薪资表中有则删除此字段*/
			ArrayList dellist=new ArrayList();
			for(int i=0;i<list.size();i++)
			{
				String name=((String)list.get(i)).toUpperCase();
				if("userflag".equalsIgnoreCase(name))
					continue;
				if("sp_flag".equalsIgnoreCase(name))
					continue;	
				if("sp_flag2".equalsIgnoreCase(name))
					continue;	
				if("appprocess".equalsIgnoreCase(name))
					continue;					
				if(buf0.indexOf(name)==-1)
				{
					dellist.add(name);
				}
			}//for i loop end.			
			DbWizard dbw=new DbWizard(this.conn);
			Table table=new Table(this.gz_tablename);
			for(int i=0;i<addlist.size();i++)
				table.addField((Field)addlist.get(i));
			if(addlist.size()>0)
				dbw.addColumns(table);
			 
			table.clear();
			for(int i=0;i<dellist.size();i++)
			{
				Field field=new Field((String)dellist.get(i),(String)dellist.get(i));
				table.addField(field);
			}
	//		if(dellist.size()>0)
	//			dbw.dropColumns(table);
			/**两边都有的指标，有可能长度或类型发生的变化*/
			 
			syncGzField(gz_tablename);
			syncGzField("salaryhistory");
			dbmodel.reloadTableModel("salaryhistory");	
			/**重新加载数据模型*/
			dbmodel.reloadTableModel(gz_tablename);	
			 
			if(!isAddFlag)
			{
				setDefineAddFlagValue();
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);				
		}
	}
	
	
	/**
	 * 当gz_extend_log对应的发放记录审批状态不为结束状态(06) 默认值为0；否则默认值为1
	 */
	private void setDefineAddFlagValue()
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			
			String userName=this.userview.getUserName();
			if(this.manager!=null&&this.manager.length()>0&&!this.manager.equals(this.userview.getUserName()))
			{
				userName=this.manager;
			}
			
			StringBuffer buf=new StringBuffer();
			HashMap map=getMaxYearMonthCount();
			/**需要审批的话,则分析当前处理的业务日期是否处理审批状态*/
			String a_currym=(String)map.get("ym");
			String a_currcount=(String)map.get("count");
			
			String sp_flag="01";
			buf.append("select sp_flag from gz_extend_log where salaryid="+this.salaryid);
			buf.append(" and upper(username)='"+userName.toUpperCase()+"'");
			buf.append(" and A00Z2=");
			buf.append(Sql_switcher.dateValue(a_currym));
			buf.append(" and A00Z3=");
			buf.append(a_currcount);
			RowSet rset=dao.search(buf.toString());
			if(rset.next())
				sp_flag=rset.getString(1);
			if("06".equals(sp_flag))
				dao.update("update "+this.gz_tablename+" set add_flag=1");
			else
				dao.update("update "+this.gz_tablename+" set add_flag=0");
			rset.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	
	
	public void alertColumn(String tableName,FieldItem _item,DbWizard dbw,ContentDAO dao)
	{
	//	 System.out.println(tableName+"-oracle--="+_item.getItemid());
		
		try
		{
			FieldItem item=(FieldItem)_item.cloneItem();
			Table table=new Table(tableName);
			 String item_id=item.getItemid();
			 item.setItemid(item_id+"_x");
			 //TableModel tm=new TableModel(tableName);
			 
			 RowSet rowSet=dao.search("select * from "+tableName+" where 1=2");
			 ResultSetMetaData data=rowSet.getMetaData();
			 HashMap columnMap=new HashMap(); 
			 for(int i=1;i<=data.getColumnCount();i++)
			 {	 columnMap.put(data.getColumnName(i).toLowerCase().trim(),"1"); 
			 }
			  
			// if(!dbw.isExistField(tableName, item_id+"_x"))
			 if(columnMap.get(item_id.toLowerCase().trim()+"_x")==null)  
			 {
		    	 table.addField(item.cloneField());
		    	 dbw.addColumns(table);
			 }
			 
			 if("N".equalsIgnoreCase(item.getItemtype()))
			 {
				 int dicimal=item.getDecimalwidth();
				 dao.update("update "+tableName+" set "+item_id+"_x=ROUND("+item_id+","+dicimal+")");
			 }
			 if("A".equalsIgnoreCase(item.getItemtype()))
			 {
				 int length=item.getItemlength();
				 dao.update("update "+tableName+" set "+item_id+"_x=substr(to_char("+item_id+"),0,"+length+")");
			 }
			 table.clear();
			 
			 item.setItemid(item_id);
			 table.addField(item.cloneField());
			 dbw.dropColumns(table);
			 dbw.addColumns(table);
			 
			 dao.update("update "+tableName+" set "+item_id+"="+item_id+"_x");
			 table.clear();
			 item.setItemid(item_id+"_x");
			 table.addField(item.cloneField());
			 dbw.dropColumns(table);
			 item.setItemid(item_id);
			 if(rowSet!=null)
				 rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	public void syncGzOracleField(ResultSetMetaData data,HashMap map,String tableName)
	{
		try
		{
			 DbWizard dbw=new DbWizard(this.conn);
			 ContentDAO dao=new ContentDAO(this.conn);
			 String sql = "select column_name,data_type,data_length,data_precision,data_scale from user_tab_columns where Table_Name='"+tableName.toUpperCase()+"'";
			 RowSet rs = dao.search(sql);
			 
			 while(rs.next()){
				 if("DATE".equalsIgnoreCase(rs.getString("data_type"))||"CLOB".equalsIgnoreCase(rs.getString("data_type"))){//日期型和大文本的不需要同步
//					 System.out.println(tableName+":date||clob:"+rs.getString("column_name"));
					 continue;
				 }

				String columnName=rs.getString("column_name");
				if(map.get(columnName.toLowerCase())!=null)
				{
					FieldItem tempItem=(FieldItem)map.get(columnName.toLowerCase());//map中的数据是salaryset里面取到的
					Field field = tempItem.cloneField();
					int columnType=field.getDatatype();
					int size=rs.getInt("data_length");
					 if("NUMBER".equalsIgnoreCase(rs.getString("data_type"))){//数值型的 data_precision代表字段总长度
						 size=rs.getInt("data_precision");
					 }else if("VARCHAR2".equalsIgnoreCase(rs.getString("data_type"))){//字符型的 data_length代表字段总长度
						 size=rs.getInt("data_length");
					 }
					int scale=rs.getInt("data_scale");
					switch(columnType)
					{
						case DataType.INT:
							if("N".equals(tempItem.getItemtype()))
							{
								if(tempItem.getDecimalwidth()!=scale)
									 alertColumn(tableName,tempItem,dbw,dao);
								else if((size-scale)<tempItem.getItemlength()) //2013-11-23  如果指标长度改大了，需同步结构
								{
									 alertColumn(tableName,tempItem,dbw,dao);
								}
							}
							if(!"N".equals(tempItem.getItemtype()))
							{
								if("A".equals(tempItem.getItemtype()))
									 alertColumn(tableName,tempItem,dbw,dao);
							}
							break;
						case DataType.STRING:
							if("A".equals(tempItem.getItemtype()))
							{
								if(tempItem.getItemlength()>size)
									 alertColumn(tableName,tempItem,dbw,dao);
							}
							break;
						case DataType.FLOAT:
							if("N".equals(tempItem.getItemtype()))
							{
								if(tempItem.getDecimalwidth()!=scale)
									 alertColumn(tableName,tempItem,dbw,dao);
								else if((size-scale)<tempItem.getItemlength()) //2013-11-23  如果指标长度改大了，需同步结构
								{
									 alertColumn(tableName,tempItem,dbw,dao);
								}
							}
							if(!"N".equals(tempItem.getItemtype()))
							{
								if("A".equals(tempItem.getItemtype()))
									 alertColumn(tableName,tempItem,dbw,dao);
							}
							break;
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
	 *当指标长度或类型发生的变化同步 工资发放临时表 或 工资历史数据表
	 */
	public void  syncGzField(String tableName)
	{
		try
		{
			 ContentDAO dao=new ContentDAO(this.conn);
			 HashMap map=new HashMap();
			 for(int i=0;i<this.fieldlist.size();i++)
			 {
					Field field=(Field)this.getFieldlist().get(i);
					String name=field.getName().toLowerCase();
					if("nbase".equals(name)|| "a0100".equals(name)|| "a0000".equals(name)|| "a00z0".equals(name)|| "a00z1".equals(name)
							|| "a00z2".equals(name)|| "a00z3".equals(name)|| "b0110".equals(name)|| "e0122".equals(name)|| "a01z0".equals(name))
						continue;
					FieldItem tempItem=DataDictionary.getFieldItem(name);
					map.put(name, tempItem);
			 }//for i loop end.
			 RowSet rowSet=dao.search("select * from "+tableName+" where 1=2");
			 ResultSetMetaData data=rowSet.getMetaData();
			 

			 ArrayList alterList=new ArrayList();
			 ArrayList resetList=new ArrayList();
			
			 
				 for(int i=1;i<=data.getColumnCount();i++)
				 {
						String columnName=data.getColumnName(i).toLowerCase();
						if(map.get(columnName)!=null)
						{
							FieldItem tempItem=(FieldItem)map.get(columnName);
							int columnType=data.getColumnType(i);
							int size=data.getColumnDisplaySize(i);//长度
							int precision=data.getPrecision(i);//精度
							int scale=data.getScale(i);
							switch(columnType)
							{
								case java.sql.Types.INTEGER:
									if("N".equals(tempItem.getItemtype()))
									{
										if(tempItem.getDecimalwidth()!=scale){
											alterList.add(tempItem.cloneField());
										}else if(precision<tempItem.getItemlength()&&tempItem.getItemlength()<=10) //2013-11-23  如果指标长度改大了，需同步结构
										{
											alterList.add(tempItem.cloneField());
										}

									}
									if(!"N".equals(tempItem.getItemtype()))
									{
										if("A".equals(tempItem.getItemtype()))
											alterList.add(tempItem.cloneField());
										else		
											resetList.add(tempItem.cloneField());
									}
									break;
								case java.sql.Types.TIMESTAMP:
									if(!"D".equals(tempItem.getItemtype()))
									{
										resetList.add(tempItem.cloneField());
									}
									break;
								case java.sql.Types.VARCHAR:
									if("A".equals(tempItem.getItemtype()))
									{
										if(tempItem.getItemlength()>size)
											alterList.add(tempItem.cloneField());
									}
									else 
										resetList.add(tempItem.cloneField());
									break;
								case java.sql.Types.DOUBLE:
									if("N".equals(tempItem.getItemtype()))
									{
										if(tempItem.getDecimalwidth()!=scale)
											alterList.add(tempItem.cloneField());
										else if((precision-scale)<tempItem.getItemlength()) //2013-11-23  如果指标长度改大了，需同步结构
										{
											alterList.add(tempItem.cloneField());
										}
									}
									if(!"N".equals(tempItem.getItemtype()))
									{
										if("A".equals(tempItem.getItemtype()))
											alterList.add(tempItem.cloneField());
										else		
											resetList.add(tempItem.cloneField());
									}
									
									
									break;
								case java.sql.Types.NUMERIC:
									if("N".equals(tempItem.getItemtype()))
									{
										if(tempItem.getDecimalwidth()!=scale)
											alterList.add(tempItem.cloneField());
										else if((precision-scale)<tempItem.getItemlength()) //2013-11-23  如果指标长度改大了，需同步结构
										{
											alterList.add(tempItem.cloneField());
										}
									}
									if(!"N".equals(tempItem.getItemtype()))
									{
										if("A".equals(tempItem.getItemtype()))
											alterList.add(tempItem.cloneField());
										else		
											resetList.add(tempItem.cloneField());
									}
									break;	
								case java.sql.Types.LONGVARCHAR:
									if(!"M".equals(tempItem.getItemtype()))
									{
										resetList.add(tempItem.cloneField());
									}
									break;
							}
						}
					}
			 	 
			 
				rowSet.close();
				DbWizard dbw=new DbWizard(this.conn);
			    Table table=new Table(tableName);
			    if(Sql_switcher.searchDbServer()!=2)  //不为oracle
			    {
				    for(int i=0;i<alterList.size();i++)
							table.addField((Field)alterList.get(i));
					if(alterList.size()>0)
							dbw.alterColumns(table);
					 table.clear();
			    }
			    else
			    	syncGzOracleField(data,map,tableName);
				 for(int i=0;i<resetList.size();i++)
						table.addField((Field)resetList.get(i));
				 if(resetList.size()>0)
				 {
					 dbw.dropColumns(table);
					 dbw.addColumns(table);
				 }
				 
			//	 System.out.println(tableName+"-sqlserver--alterList.size="+alterList.size());
			//	 System.out.println(tableName+"-sqlserver--resetList.size="+resetList.size());
			 
			 
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * 创建薪资数据表
	 */
	public void createGzDataTable() throws GeneralException
	{
		try
		{
			Field field=null;
			StringBuffer buf=new StringBuffer();
			buf.append("A0100,NBASE,A00Z0,A00Z1");
			DbWizard dbw=new DbWizard(this.conn);
			Table table=new Table(gz_tablename);
			boolean is_spflag=false;
			boolean is_appprocess=false;
			for(int i=0;i<this.fieldlist.size();i++)
			{
				field=(Field)this.fieldlist.get(i);
				if(buf.indexOf(field.getName())!=-1)
				{
					field.setNullable(false);
					field.setKeyable(true);
				}
				if("sp_flag".equalsIgnoreCase(field.getName()))
					is_spflag=true;
				if("appprocess".equalsIgnoreCase(field.getName()))
					is_appprocess=true;
				table.addField(field);
			}//for i loop end.
			/**UserFlag*/
			field=new Field("userflag","userflag");
			field.setDatatype(DataType.STRING);
			field.setLength(50);
			table.addField(field);
			
			if(!is_appprocess)
			{
			field=new Field("appprocess","appprocess");
			field.setDatatype(DataType.CLOB);
			table.addField(field);
			}
			
			if(!is_spflag)
			{
				field=new Field("sp_flag","sp_flag");
				field.setDatatype(DataType.STRING);
				field.setLength(2);
				table.addField(field);
			}
			
			
			field=new Field("E0122_O","E0122_O");
			field.setDatatype(DataType.INT);
			field.setLength(10);
			table.addField(field);
						
			field=new Field("B0110_O","B0110_O");
			field.setDatatype(DataType.INT);
			field.setLength(10);
			table.addField(field);
			
			field=new Field("dbid","dbid");
			field.setDatatype(DataType.INT);
			field.setLength(10);
			table.addField(field);
			
			
			//临时变量
			ArrayList midVariableList=getMidVariableList();
			for(int i=0;i<midVariableList.size();i++)
			{
				FieldItem item=(FieldItem)midVariableList.get(i);
				String fieldname=item.getItemid();
				field=item.cloneField();
				table.addField(field);
				
			}//for i loop end.
			
			
			dbw.createTable(table);
			/**重新加载数据模型*/
			DBMetaModel dbmodel=new DBMetaModel(this.conn);
			dbmodel.reloadTableModel(gz_tablename);	 
			dbw.execute("create index  "+gz_tablename.toUpperCase()+"_id on "+gz_tablename+" (A0100)");  // 用_A0100 对于5个汉字作用户名的太长
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);			
		}
	}
	/**
	 * 同步薪资表结构
	 *
	 */
	public void syncGzTableStruct() throws GeneralException
	{
		try
		{
			DbWizard dbw=new DbWizard(this.conn);
			if(dbw.isExistTable(this.gz_tablename, false))
			{
				updateGzDataTable();
			}
			else
			{
				createGzDataTable();
			}
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	
	public void update(String str_sql)
	{
		try
		{
			 ContentDAO dao=new ContentDAO(this.conn);
			 dao.update(str_sql);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 查询薪资类别项目来源的子集列表
	 * @return
	 */
	private ArrayList searchSetList()
	{
		  ArrayList list=new ArrayList();
		  StringBuffer buf=new StringBuffer();
		  buf.append("select fieldsetid from salaryset where salaryid=?");
		  buf.append(" group by fieldsetid order by fieldsetid");
		  ArrayList paralist=new ArrayList();
		  paralist.add(new Integer(this.salaryid));
		  RowSet rset=null;
		  try
		  {
			  ContentDAO dao=new ContentDAO(this.conn);
			  rset=dao.search(buf.toString(),paralist);
			  while(rset.next())
			  {
				  list.add(rset.getString("fieldsetid").toUpperCase());
			  }//
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
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
		  }			  
		  return list;
	}
	/**
	 * 薪资类别数据初始化
	 */
	private void initData()
	{
	  try
	  {
		if(this.manager.length()==0)
			this.gz_tablename=this.userview.getUserName()+"_salary_"+this.salaryid;
		else
			this.gz_tablename=this.manager+"_salary_"+this.salaryid;
		this.fieldlist=searchGzItem();
		templatevo=new RecordVo("salarytemplate");
		templatevo.setInt("salaryid", this.salaryid);
		ContentDAO dao=new ContentDAO(this.conn);
		templatevo=dao.findByPrimaryKey(templatevo);
		this.setlist=searchSetList();
	  }
	  catch(Exception ex)
	  {
		  ex.printStackTrace();
	  }
	}
	/**
	 * 取得薪资项目对象VO
	 * @param rset
	 * @return
	 */
	private GzItemVo getGzItemVo(RowSet rset)throws GeneralException
	{
		GzItemVo itemvo=new GzItemVo();
		try
		{
			itemvo.setSetname(rset.getString("fieldsetid"));
			itemvo.setFldname(rset.getString("itemid"));
			itemvo.setFldtype(rset.getString("Itemtype"));
			itemvo.setCodeid(rset.getString("codesetid"));
			itemvo.setHz(rset.getString("itemdesc"));
			itemvo.setFlddec(rset.getInt("Decwidth"));
			
			itemvo.setLen(rset.getInt("Itemlength"));
			itemvo.setInitflag(rset.getInt("Initflag"));
			itemvo.setHeapflag(rset.getInt("Heapflag"));
			itemvo.setLock(rset.getInt("Nlock"));
			itemvo.setChangeflag(rset.getInt("changeflag"));
			itemvo.setFormula(Sql_switcher.readMemo(rset, "formula"));
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return itemvo;
	}
	/**
	 * 查询薪资类别对应的项目
	 * @return
	 */
	private ArrayList searchGzItem() 
	{
		ArrayList list=new ArrayList();
		StringBuffer strread=new StringBuffer();
		/**只读字段*/
		strread.append("SP_FLAG,B0110,E0122,A0101,A00Z2,A00Z3,E01A1");
		StringBuffer format=new StringBuffer();	
		format.append("###################");		
		StringBuffer buf=new StringBuffer();
		buf.append("select * from salaryset where salaryid=?");
		buf.append(" order by sortid");
		Field field=null;
		RowSet rset=null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			ArrayList paralist=new ArrayList();
			paralist.add(String.valueOf(getSalaryid()));

			rset=dao.search(buf.toString(),paralist);
		
			boolean isOk=false;
//			加上报审标识
			if(this.manager.length()>0)
			{
				field=new Field("sp_flag2","报审状态");
				field.setLength(50);
				field.setCodesetid("23");
				field.setDatatype(DataType.STRING);
				field.setReadonly(true);
				list.add(field);
				
				isOk=true;
			}
			if(isApprove())
			{
				
				/**加上审批标识*/
				field=new Field("sp_flag",ResourceFactory.getProperty("label.gz.sp"));
				field.setLength(50);
				field.setCodesetid("23");
				field.setDatatype(DataType.STRING);
				field.setReadonly(true);
				list.add(field);
				
				isOk=true;
			}
			if(isOk)
			{
				/**加上审批意见*/
				field=new Field("appprocess","审批意见");
				field.setDatatype(DataType.CLOB);
				field.setAlign("left");		
				field.setReadonly(true);
				list.add(field);
			}
			
			
			
			
			//追加标记
			field=new Field("add_flag","追加标记");
			field.setDatatype(DataType.INT);
			field.setAlign("left");
			field.setVisible(false);
			list.add(field);
			String temp_str="'B0110','A00Z1','A00Z0','A00Z2','A00Z3','A0000','A0100','NBASE','A01Z0','A0101','E0122','E01A1'";
			while(rset.next())
			{
				String itemid=rset.getString("itemid");
				if(temp_str.indexOf("'"+itemid.toUpperCase()+"'")==-1)
				{
					FieldItem _tempItem=DataDictionary.getFieldItem(itemid.toLowerCase());
					if(_tempItem==null)
						continue;
					
				}
				/**取得薪资项目对象*/
				this.gzitemlist.add(getGzItemVo(rset));
				/**指标隐藏时，把此字段设置为0*/
				int nwidth=rset.getInt("nwidth");
				if("a01z0".equalsIgnoreCase(itemid)&&this.ctrlparam!=null)
				{
					String a01z0Flag=this.ctrlparam.getValue(SalaryCtrlParamBo.A01Z0,"flag");  // 是否显示停发标识  1：有  
					if(a01z0Flag==null|| "0".equals(a01z0Flag))
					{
						nwidth=0;
					}
				}
				field=new Field(itemid,rset.getString("itemdesc"));
				
				String type=rset.getString("itemtype");
				String codesetid=rset.getString("codesetid");
				field.setCodesetid(codesetid);
				/**字段为代码型,长度定为50*/
				if("A".equals(type))
				{
					field.setDatatype(DataType.STRING);

					if(codesetid==null|| "0".equals(codesetid)|| "".equals(codesetid))
						field.setLength(rset.getInt("itemlength"));						
					else
						field.setLength(50);
					field.setAlign("left");
				}
				else if("M".equals(type))
				{
					field.setDatatype(DataType.CLOB);
					field.setAlign("left");					
				}
				else if("N".equals(type))
				{

					field.setLength(rset.getInt("itemlength"));
					int ndec=rset.getInt("decwidth");
					field.setDecimalDigits(ndec);					
					if(ndec>0)
					{
						field.setDatatype(DataType.FLOAT);						
						//format.setLength(ndec);
						field.setFormat("####."+format.toString().substring(0,ndec));
					}
					else
					{
						field.setDatatype(DataType.INT);							
						field.setFormat("####");						
					}
					field.setAlign("right");					
				}	
				else if("D".equals(type))
				{
					field.setLength(20);
					//field.setDatatype(DataType.STRING);
					field.setDatatype(DataType.DATE);
					field.setFormat("yyyy.MM.dd");
					field.setAlign("right");						
				}	
				else
				{
					field.setDatatype(DataType.STRING);
					field.setLength(rset.getInt("itemlength"));
					field.setAlign("left");						
				}
				/**对人员库标识，采用“@@”作为相关代码类*/
				if("nbase".equalsIgnoreCase(itemid))
				{
					field.setCodesetid("@@");
					field.setReadonly(true);
				}
				if(nwidth==0)
					field.setVisible(false);


				field.setSortable(true);
				/**设置只读字段*/
				int idx=strread.indexOf(itemid.toUpperCase());
				if(idx!=-1)
					field.setReadonly(true);
				else
				{
					/**分析指标权限*/
					if("1".equalsIgnoreCase(this.userview.analyseFieldPriv(itemid)))
					{
						field.setReadonly(true); //读权限
					}
					if(!("a00z0".equalsIgnoreCase(itemid)|| "a00z1".equalsIgnoreCase(itemid)|| "nbase".equalsIgnoreCase(itemid))&& "0".equalsIgnoreCase(this.userview.analyseFieldPriv(itemid)))
					{
						field.setVisible(false);//无权限
					}	
					if("a00z0".equalsIgnoreCase(itemid)|| "a00z1".equalsIgnoreCase(itemid))
						field.setVisible(true);
					
				}
				list.add(field);
			}//loop end.

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			//throw GeneralExceptionHandler.Handle(ex);
		}
		finally
		{
			try
			{
				if(rset!=null)
					rset.close();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}		
		return list;
	}	
	
	/**
	 * 引入手工选择人员
	 * @param right_fields
	 * @author dengcan
	 */
	public void importHandSelectedMen(String[] right_fields)throws GeneralException
	{
		/**临时表名*/
		String tablename="t#"+this.userview.getUserName()+"_gz_Ins";
		try
		{
			if(right_fields==null)
				return;
			ContentDAO dao=new ContentDAO(this.conn);
			createInsDecTableStruct(tablename);
			DbSecurityImpl dbS = new DbSecurityImpl();
			boolean flag = dbS.encryptTableName(this.conn, tablename);//创建临时表时需注册表
			DbWizard dbw=new DbWizard(this.conn);
			/** 用于判断是否在临时表中存在 */
			Table table=new Table(tablename);
			Field field=new Field("isFlag","isFlag");
			field.setDatatype(DataType.STRING);
			field.setLength(10);			
			table.addField(field);
			dbw.addColumns(table);
			
			
			for(int i=0;i<right_fields.length;i++)
			{
				right_fields[i]=PubFunc.hireKeyWord_filter_reback(right_fields[i]); //20140909 dengcan
			}
			
			
			/**导入数据*/
			HashSet set=new HashSet();
			for(int i=0;i<right_fields.length;i++)
			{
				set.add(PubFunc.decrypt(right_fields[i].split("/")[1]));
			}
			for(Iterator t=set.iterator();t.hasNext();)
			{
				String dbName=(String)t.next();
				if(dbName==null||dbName.length()==0)
					continue;
				StringBuffer where=new StringBuffer("");
				for(int i=0;i<right_fields.length;i++)
				{
					if(PubFunc.decrypt(right_fields[i].split("/")[1]).equalsIgnoreCase(dbName))
						where.append(" or a0100='"+PubFunc.decrypt(right_fields[i].split("/")[0])+"'");
				}
				
				StringBuffer buf=new StringBuffer("");
				buf.append("insert into ");
				buf.append(tablename);
				buf.append("(DBNAME,A0100,A0000,B0110,E0122,A0101,STATE,isFlag)");
				buf.append(" select '");
				buf.append(dbName);
				buf.append("' as DBNAME,");
				buf.append("A0100,A0000,B0110,E0122,A0101,'1' as STATE ,'0' as isFlag ");
				buf.append(" from "+dbName+"A01 where ("+where.substring(3)+")");
				dao.update(buf.toString());
			}
			/**新增人员*/
			importAddManData(true);
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			String message=e.toString();
			if(message.indexOf("最大")!=-1&&message.indexOf("8060")!=-1&&Sql_switcher.searchDbServer()==1)
			{ 
				PubFunc.resolve8060(this.conn,this.gz_tablename);
				throw GeneralExceptionHandler.Handle(new Exception("请重新执行人员引入操作!"));
			} 
		}
	}
	/**
	 * 引入手工选择人员(高级查询的全部引入)
	 * @param expr
	 * @param factor
	 * @param history
	 * @param isSalaryManager
	 */
	
	public void importHandSelectedMenHQuery(String expr,String factor,boolean ishistory,String isSalaryManager)
	{
		/**临时表名*/
		String tablename="t#"+this.userview.getUserName()+"_gz_Ins";
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			createInsDecTableStruct(tablename);
			DbSecurityImpl dbS = new DbSecurityImpl();
			boolean flag = dbS.encryptTableName(this.conn, tablename);//创建临时表时需注册表
			DbWizard dbw=new DbWizard(this.conn);
			/** 用于判断是否在临时表中存在 */
			Table table=new Table(tablename);
			Field field=new Field("isFlag","isFlag");
			field.setDatatype(DataType.STRING);
			field.setLength(10);			
			table.addField(field);
			dbw.addColumns(table);
			String cbase=this.templatevo.getString("cbase");
			cbase=cbase.substring(0,cbase.length()-1);
			cbase=cbase.replaceAll(",","','");
			RowSet rowSet=dao.search("select * from dbname where UPPER(pre) in ('"+cbase.toUpperCase()+"')");
			while(rowSet.next())
			{
				String dbname=rowSet.getString("dbname");
				String pre=rowSet.getString("pre");
				String a01=pre+"A01";
				StringBuffer sql=new StringBuffer("");
				String condSql="";
				ArrayList fieldlist=new ArrayList();
				if(this.userview.isSuper_admin())
					condSql=userview.getPrivSQLExpression(PubFunc.keyWord_reback(SafeCode.decode(expr))+"|"+PubFunc.reBackWord(SafeCode.decode(factor)),pre,ishistory,false,true,fieldlist);
				else
				{
					FactorList factorslist=new FactorList(PubFunc.keyWord_reback(SafeCode.decode(expr)),PubFunc.reBackWord(SafeCode.decode(factor)),pre,ishistory ,false,true,1,userview.getUserId());
		        	fieldlist=factorslist.getFieldList();
		        	condSql=factorslist.getSqlExpression();
				}
				if(condSql.length()>0)
				{
					sql.append(condSql);
				}
				if("N".equalsIgnoreCase(isSalaryManager))
			    {
			    	String privSQL=InfoUtils.getWhereINSql(this.userview, pre);
			    	sql.append(" and "+a01+".a0100 in (select "+pre+"a01.a0100 "+(privSQL.length()>0?privSQL:(" from "+a01))+")");
			    }
				sql.append(" and "+a01+".a0100 not in (select a0100 from "+this.gz_tablename+" where  upper(nbase)='"+pre.toUpperCase()+"') ");

				StringBuffer buf=new StringBuffer("");
				buf.append("insert into ");
				buf.append(tablename);
				buf.append("(DBNAME,A0100,A0000,B0110,E0122,A0101,STATE,isFlag)");
				buf.append(" select '");
				buf.append(pre);
				buf.append("' as DBNAME,");
				buf.append("A0100,A0000,B0110,E0122,A0101,'1' as STATE ,'0' as isFlag ");
				buf.append(" from "+pre+"A01 where A0100 in ( select "+pre+"A01.A0100 "+sql.toString()+")");
				dao.update(buf.toString());
			}
			/**新增人员*/
			importAddManData(true);
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 把新增人员，生成一张临时表(将指定日期里历史表中的数据导入到临时表中)
	 * 临时表包括如下字段DBName,A0100,A0000,B0110,E0122,A0101,state
	 * 主键字段：DBNAME,A0100
	 * @return 用户名+InsPeoples 表名,如果创建不成功，返回空串
	 */
	public String createAddManTable(LazyDynaBean dataBean){
		/**临时表名*/
		String tablename="t#"+this.userview.getUserName()+"_gz_Ins";
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			createInsDecTableStruct(tablename);
			DbSecurityImpl dbS = new DbSecurityImpl();
			boolean flag = dbS.encryptTableName(this.conn, tablename);//创建临时表时需注册表
			/**导入数据*/
			String[] temps=((String)dataBean.get("ym")).split("-");
			String count=(String)dataBean.get("count");
			
			StringBuffer buf=new StringBuffer("");
			buf.append("insert into ");
			buf.append(tablename);
			buf.append("(DBNAME,A0100,A0000,B0110,E0122,A0101,STATE)");
			buf.append(" select distinct nbase,");
			buf.append("A0100,A0000,B0110,E0122,A0101,'1' as STATE  ");
			buf.append(" from salaryhistory where lower(userflag)='"+this.userview.getUserName().toLowerCase()+"' and salaryid="+this.salaryid);
			buf.append(" and "+Sql_switcher.year("a00z2")+"="+temps[0]);
			buf.append(" and "+Sql_switcher.month("a00z2")+"="+temps[1]);
			buf.append(" and a00z3="+count);
			dao.update(buf.toString());
			
			String a01z0Flag=this.ctrlparam.getValue(SalaryCtrlParamBo.A01Z0,"flag");  // 是否显示停发标识  1：有  
			if(a01z0Flag!=null&& "1".equals(a01z0Flag))
			{
				/**导入数据*/
				String dbpres=this.templatevo.getString("cbase");
				/**应用库前缀*/
				String[] dbarr=StringUtils.split(dbpres, ",");
				for(int i=0;i<dbarr.length;i++)
				{
					String pre=dbarr[i];
					StringBuffer _sql=new StringBuffer("delete from "+tablename+" where lower(dbname)='"+pre.toLowerCase()+"' and  exists (select null from ");
					_sql.append(pre+"A01 where "+pre+"A01.a0100="+tablename+".a0100  and A01Z0<>'1' and A01Z0<>'' and A01Z0 is not null  ) ");
					dao.update(_sql.toString());
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			tablename="";
		}
		return tablename;
	}
	
	
	

	
	
	/**
	 * 把新增人员，生成一张临时表
	 * 临时表包括如下字段DBName,A0100,A0000,B0110,E0122,A0101,state
	 * 主键字段：DBNAME,A0100
	 * @return 用户名+InsPeoples 表名,如果创建不成功，返回空串
	 */
	public String createAddManTable(){
		/**临时表名*/
		String tablename="t#"+this.userview.getUserName()+"_gz_Ins";
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			ArrayList list = add_del_rightList(salaryid,"1");
			createInsDecTableStruct(tablename,"1",list);
			DbSecurityImpl dbS = new DbSecurityImpl();
			dbS.encryptTableName(this.conn, tablename);//创建临时表时需注册表
			/**导入数据*/
			String dbpres=this.templatevo.getString("cbase");
			/**应用库前缀*/
			String cloumnStr="DBNAME,A0100,A0000,B0110,E0122,A0101,STATE";
			boolean addflag=this.isAddColumn(this.getOnlyField(), cloumnStr);
			String[] dbarr=StringUtils.split(dbpres, ",");
			StringBuffer buf=new StringBuffer();
			
			ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
			String _flag=this.ctrlparam.getValue(SalaryCtrlParamBo.COND_MODE,"flag");  // "":没条件 0：简单条件 1：复杂条件
			String cond=this.templatevo.getString("cond");
			String cexpr=this.templatevo.getString("cexpr");
			StringBuffer column = new StringBuffer();//新增人员的相关字段信息存入临时表，搜房网  zhaoxg add 2013-11-14
			if(list.size()>0){
				for(int i=0;i<list.size();i++){
					CommonData obj = (CommonData)list.get(i);
					if(cloumnStr.toLowerCase().indexOf(obj.getDataValue().toLowerCase())!=-1){//排除默认项
						continue;
					}
					if(addflag&&this.getOnlyField().toLowerCase().indexOf(obj.getDataValue().toLowerCase())!=-1){//如果有唯一指标也排除  搜房网  zhaoxg add
						continue;
					}
					column.append(","+obj.getDataValue().toLowerCase()+"");
				}
			}
			for(int i=0;i<dbarr.length;i++)
			{
				String pre=dbarr[i];
				if(!this.userview.isSuper_admin()&&this.userview.getDbpriv().toString().toLowerCase().indexOf(","+pre.toLowerCase()+",")==-1)
					continue;
				//String srctable=pre+"A01";
				buf.setLength(0);
				buf.append("insert into ");
				buf.append(tablename);
				buf.append("(DBNAME,A0100,A0000,B0110,E0122,A0101,STATE");
				if(addflag)
					buf.append(","+this.getOnlyField());

				buf.append(")");
				buf.append(" select '");
				buf.append(pre);
				buf.append("' as DBNAME,");
				buf.append(pre+"A01.A0100,A0000,B0110,E0122,A0101,'1' as STATE  ");
				if(addflag)
					buf.append(","+this.getOnlyField());

				String flag=this.ctrlparam.getValue(SalaryCtrlParamBo.PRIV_MODE,"flag");  // 人员范围权限过滤标志  1：有  
				
				
				
				
				if(!"monthPremium".equalsIgnoreCase(from_module)&& "1".equals(this.controlByUnitcode))
				{
					
					if(_flag!=null&& "0".equals(_flag)&&cond.length()>0)  //0：简单条件
					{
						FactorList factor = new FactorList(cexpr, cond,pre, false, false, true, 1, "su");				
						String strSql = factor.getSqlExpression();
						buf.append(" from "+pre+"A01 where 1=1 "); 
						buf.append(" and "+pre+"A01.a0100 in ( select "+pre+"A01.a0100 "+strSql+")"); 
						/*
						if(strSql.toLowerCase().indexOf("where")==-1)
						{
							buf.append(strSql+" where 1=1");
						}
						else
						{
							buf.append(strSql);
						}
						*/
						
					}
					else
						buf.append(" from "+pre+"A01 where 1=1 "); 
					String whl=getWhlByUnits_info();
					if(whl.length()>0)
						buf.append(whl);
				}
				else if(this.manager!=null&&this.manager.length()>0&&!this.manager.equalsIgnoreCase(this.userview.getUserName()))
				{
						buf.append(this.userview.getPrivSQLExpression(pre, false));	
						if(_flag!=null&& "0".equals(_flag)&&cond.length()>0)  //0：简单条件
						{
							FactorList factor = new FactorList(cexpr, cond,pre, false, false, true, 1, userview.getUserId());				
							String strSql = factor.getSqlExpression();
							buf.append(" and "+pre+"A01.a0100 in ( select "+pre+"A01.a0100 "+strSql+")"); 
						}
				}
				else if(flag!=null&& "1".equals(flag)&&!"monthPremium".equalsIgnoreCase(from_module))
				{
					
						buf.append(this.userview.getPrivSQLExpression(pre, false));	
						
						if(_flag!=null&& "0".equals(_flag)&&cond.length()>0)  //0：简单条件
						{
							FactorList factor = new FactorList(cexpr, cond,pre, false, false, true, 1, userview.getUserId());				
							String strSql = factor.getSqlExpression();
							buf.append(" and "+pre+"A01.a0100 in ( select "+pre+"A01.a0100 "+strSql+")"); 
						}
				}
				else
				{
					if(_flag!=null&& "0".equals(_flag)&&cond.length()>0)  //0：简单条件
					{
						FactorList factor = new FactorList(cexpr, cond,pre, false, false, true, 1, "su");				
						String strSql = factor.getSqlExpression();
						if(strSql.toLowerCase().indexOf("where")==-1)
						{
							buf.append(strSql+" where 1=1");
						}
						else
						{
							buf.append(strSql);
						}
					}
					else
						buf.append(" from "+pre+"A01 where 1=1 ");
				}
				
				 
				
				
				/**停发标志*/
				String a01z0Flag=this.ctrlparam.getValue(SalaryCtrlParamBo.A01Z0,"flag");  // 是否显示停发标识  1：有  
				if(a01z0Flag!=null&& "1".equals(a01z0Flag))
					buf.append(" and (A01Z0='1' or A01Z0='' or A01Z0 is null)");
				//buf.append(srctable);
				//buf.append(" where ");
			
				
				/** 由于效率低下，取出改为exists
				buf.append(" and A0100 not in (select A0100 from ");
				buf.append(this.gz_tablename);
				buf.append(" where upper(NBASE)='");
				buf.append(pre.toUpperCase());
				buf.append("')");
				*/
				
				buf.append(" and  not exists (select null from "+this.gz_tablename+" where upper(NBASE)='"+pre.toUpperCase()+"' and "+this.gz_tablename+".a0100="+pre+"A01.a0100  ) ");
				
				
				
					
				 
				if(_flag!=null&&_flag.length()>0)
				{
					/*
						if(_flag.equals("0")&&cond.length()>0)  //0：简单条件
						{
							FactorList factor = new FactorList(cexpr, cond,pre, false, false, true, 1, "su");				
							String strSql = factor.getSqlExpression();
							
							
							sql="delete from "+tablename+" where upper(dbname)='"+pre.toUpperCase()+"' and ";
							if(!tablename.equalsIgnoreCase("t#"+this.userview.getUserName()+"_DecPeoples"))
								sql+="not ";
						  
							if(strSql.toLowerCase().indexOf("where")==-1)
								sql+=" exists (select null "+strSql+" where "+pre+"a01.a0100="+tablename+".a0100 )";
							else
								sql+=" exists (select null "+strSql+" and "+pre+"a01.a0100="+tablename+".a0100 )";
							dao.delete(sql,new ArrayList());
						}
					*/
						if("1".equals(_flag)&&cond.length()>0)  // 1：复杂条件
						{
							
							String tempTableName ="";
							String w ="";
							
							if(complexWhlMap.get(pre.toLowerCase()+"_whl")==null)
							{
							
							
								int infoGroup = 0; // forPerson 人员
								int varType = 8; // logic	
								
								String whereIN="select "+pre+"A01.a0100 from "+pre+"A01";
								String aflag=this.ctrlparam.getValue(SalaryCtrlParamBo.PRIV_MODE,"flag");  // 人员范围权限过滤标志  1：有  
								
								if("1".equals(this.controlByUnitcode))
								{ 
									String whl=getWhlByUnits_info();
									if(whl.length()>0)
										whereIN+=" where 1=1 "+whl;
								}
								else if(this.manager!=null&&this.manager.length()>0&&!this.manager.equalsIgnoreCase(this.userview.getUserName()))
								{
									whereIN=InfoUtils.getWhereINSql(this.userview,pre);
									whereIN="select "+pre+"A01.a0100 "+whereIN;	
								}
								if(aflag!=null&& "1".equals(aflag))
								{
									whereIN=InfoUtils.getWhereINSql(this.userview,pre);
									whereIN="select "+pre+"A01.a0100 "+whereIN;	
								}
								
								if(currym!=null)
								{
									cond=cond.replaceAll("归属日期\\(\\)","#"+currym+"#");
									cond=cond.replaceAll("归属日期","#"+currym+"#");
								}
								else
								{
									HashMap ycmap=getYearMonthCount2();   //dengcan
									/**年月和次数*/
									String ym=(String)ycmap.get("ym"); 
									cond=cond.replaceAll("归属日期\\(\\)","#"+ym+"#");
									cond=cond.replaceAll("归属日期","#"+ym+"#");
								}
								
								
								alUsedFields.addAll(this.getMidVariableList());
								YksjParser yp = new YksjParser(this.userview ,alUsedFields,
										YksjParser.forSearch, varType, infoGroup, "Ht",pre.toString()); 
								YearMonthCount ymc=null;							
								yp.run_Where(cond, ymc,"","hrpwarn_result", dao, whereIN,this.conn,"A", null);
								tempTableName = yp.getTempTableName();
								w = yp.getSQL();
							 
							}
							else
							{
							 
									tempTableName="t#"+this.userview.getUserName()+"_gz_"+pre.toLowerCase()+"_cond";
									w=(String)complexWhlMap.get(pre.toLowerCase()+"_whl");
									
									String whereIN="select "+pre+"A01.a0100 from "+pre+"A01";
									String aflag=this.ctrlparam.getValue(SalaryCtrlParamBo.PRIV_MODE,"flag");  // 人员范围权限过滤标志  1：有  
									if("1".equals(this.controlByUnitcode))
									{ 
										String whl=getWhlByUnits_info();
										if(whl.length()>0)
											whereIN+=" where 1=1 "+whl;
									}
									else if(this.manager!=null&&this.manager.length()>0&&!this.manager.equalsIgnoreCase(this.userview.getUserName()))
									{
										whereIN=InfoUtils.getWhereINSql(this.userview,pre);
										whereIN="select "+pre+"A01.a0100 "+whereIN;	
									}
									if(aflag!=null&& "1".equals(aflag))
									{
										whereIN=InfoUtils.getWhereINSql(this.userview,pre);
										whereIN="select "+pre+"A01.a0100 "+whereIN;	
									}
									w+=" and a0100 in ("+whereIN+")";
								 
								
							}
							
							
							if(w!=null&&w.trim().length()>0)
							{
								
								buf.append(" and exists (select null from "+tempTableName+" where "+tempTableName+".a0100="+pre+"A01.a0100 and ( "+w+" ))");
								 
							}
						}
					
				}
				
				
				
				
				
				
				
				
				
				
				dao.update(buf.toString());
				if(column.length()>0){
				String[] temp=column.substring(1).split(",");
				for(int j=0;j<temp.length;j++){
					StringBuffer _sql=new StringBuffer();	
					String fieldsetid=getFieldSetid(temp[j]);
					if("a01".equalsIgnoreCase(fieldsetid)){
						_sql.append("update "+tablename+" set ");
						_sql.append(temp[j]+"=(select "+temp[j]+" from ");
						_sql.append(""+pre+"a01");
						_sql.append("   where  a0100="+tablename+".a0100) where "+tablename+".dbname='"+pre+"'");
						_sql.append("and exists (");
						_sql.append("select null from "+pre+"a01  where  a0100="+tablename+".a0100  ");
						_sql.append(")");
					}else{
						_sql.append("update "+tablename+" set ");	
						_sql.append(temp[j]+"=(select "+temp[j]+" from");
						_sql.append("(select a0100,"+temp[j]+" from "+pre+fieldsetid+" a where a.i9999=(select max(i9999) from "+pre+fieldsetid+" b where a.a0100=b.a0100 ) )");
						_sql.append(" c where c.a0100="+tablename+".a0100) where "+tablename+".dbname='"+pre+"'");
						_sql.append("and exists (");
						_sql.append("select null from (select a0100,"+temp[j]+" from "+pre+fieldsetid+" a where a.i9999=(select max(i9999) from "+pre+fieldsetid+" b where a.a0100=b.a0100 ) ) c where c.a0100="+tablename+".a0100");
						_sql.append(")");
					}
					dao.update(_sql.toString());
				}
				}
			}//for i loop end.
			
			/**薪资类别定义的条件，删除不在条件范围中的人员*/
		//	delNoConditionData(tablename);
			
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			tablename="";
		}
		return tablename;
	}
	
	public String getFieldSetid(String itemid){
		String fieldsetid="A01";
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			String sql="select fieldsetid from fielditem where itemid = '"+itemid.toUpperCase()+"'";
			RowSet rs=dao.search(sql);
			if(rs.next()){
				fieldsetid=rs.getString("fieldsetid");
			}
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return fieldsetid;
	}
	/**
	 * 薪资类别定义的条件，删除不在条件范围中的人员
	 * @param tableName
	 */
	public void delNoConditionData(String tablename)
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			/**导入数据*/
			String dbpres=this.templatevo.getString("cbase");
			/**应用库前缀*/
			String[] dbarr=StringUtils.split(dbpres, ",");
			String flag=this.ctrlparam.getValue(SalaryCtrlParamBo.COND_MODE,"flag");  // "":没条件 0：简单条件 1：复杂条件
			String cond=this.templatevo.getString("cond");
			String cexpr=this.templatevo.getString("cexpr");		
			String sql="";
			ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
			if(flag!=null&&flag.length()>0)
			{
				for(int i=0;i<dbarr.length;i++)
				{
					String pre=dbarr[i];
					if("0".equals(flag)&&cond.length()>0)  //0：简单条件
					{
						FactorList factor = new FactorList(cexpr, cond,pre, false, false, true, 1, "su");				
						String strSql = factor.getSqlExpression();
						sql="delete from "+tablename+" where upper(dbname)='"+pre.toUpperCase()+"' and "; //  a0100 ";
						if(!tablename.equalsIgnoreCase("t#"+this.userview.getUserName()+"_gz_Dec"))
							sql+="not ";
					   //	sql+=" in (select "+pre+"a01.a0100 "+strSql+" )";
						if(strSql.toLowerCase().indexOf("where")==-1)
							sql+=" exists (select null "+strSql+" where "+pre+"a01.a0100="+tablename+".a0100 )";
						else
							sql+=" exists (select null "+strSql+" and "+pre+"a01.a0100="+tablename+".a0100 )";
						dao.delete(sql,new ArrayList());
					}
					if("1".equals(flag)&&cond.length()>0)  // 1：复杂条件
					{
						
						
						String tempTableName ="";
						String w ="";
						
						if(complexWhlMap.get(pre.toLowerCase()+"_whl")==null)
						{
							
							int infoGroup = 0; // forPerson 人员
							int varType = 8; // logic	
							
							String whereIN="select "+pre+"A01.a0100 from "+pre+"A01";
							String aflag=this.ctrlparam.getValue(SalaryCtrlParamBo.PRIV_MODE,"flag");  // 人员范围权限过滤标志  1：有  
							
							if("1".equals(this.controlByUnitcode))
							{ 
								String whl=getWhlByUnits_info();
								if(whl.length()>0)
									whereIN+=" where 1=1 "+whl;
							}
							else if(this.manager!=null&&this.manager.length()>0&&!this.manager.equalsIgnoreCase(this.userview.getUserName()))
							{
								whereIN=InfoUtils.getWhereINSql(this.userview,pre);
								whereIN="select "+pre+"A01.a0100 "+whereIN;	
							}
							if(aflag!=null&& "1".equals(aflag))
							{
								whereIN=InfoUtils.getWhereINSql(this.userview,pre);
								whereIN="select "+pre+"A01.a0100 "+whereIN;	
							}
							
							
							if(currym!=null)
							{
								cond=cond.replaceAll("归属日期\\(\\)","#"+currym+"#");
								cond=cond.replaceAll("归属日期","#"+currym+"#");
							}
							else
							{
								HashMap ycmap=getYearMonthCount2();   //dengcan
								/**年月和次数*/
								String ym=(String)ycmap.get("ym"); 
								cond=cond.replaceAll("归属日期\\(\\)","#"+ym+"#");
								cond=cond.replaceAll("归属日期","#"+ym+"#");
							}
							
							
							alUsedFields.addAll(this.getMidVariableList());
							YksjParser yp = new YksjParser(this.userview ,alUsedFields,
									YksjParser.forSearch, varType, infoGroup, "Ht",pre.toString());
							YearMonthCount ymc=null;		 
							yp.run_Where(cond, ymc,"","hrpwarn_result", dao, whereIN,this.conn,"A", null);
							tempTableName = yp.getTempTableName();
							w = yp.getSQL();
							
						}
						else
						{
							tempTableName="t#"+this.userview.getUserName()+"_gz_"+pre.toLowerCase()+"_cond";
							w=(String)complexWhlMap.get(pre.toLowerCase()+"_whl");
							
							String whereIN="select "+pre+"A01.a0100 from "+pre+"A01";
							String aflag=this.ctrlparam.getValue(SalaryCtrlParamBo.PRIV_MODE,"flag");  // 人员范围权限过滤标志  1：有  
							if("1".equals(this.controlByUnitcode))
							{ 
								String whl=getWhlByUnits_info();
								if(whl.length()>0)
									whereIN+=" where 1=1 "+whl;
							}
							else if(this.manager!=null&&this.manager.length()>0&&!this.manager.equalsIgnoreCase(this.userview.getUserName()))
							{
								whereIN=InfoUtils.getWhereINSql(this.userview,pre);
								whereIN="select "+pre+"A01.a0100 "+whereIN;	
							}
							if(aflag!=null&& "1".equals(aflag))
							{
								whereIN=InfoUtils.getWhereINSql(this.userview,pre);
								whereIN="select "+pre+"A01.a0100 "+whereIN;	
							}
							w+=" and a0100 in ("+whereIN+")";
						}
						
						
						
						if(w!=null&&w.trim().length()>0)
						{
							sql="delete from "+tablename+" where upper(dbname)='"+pre.toUpperCase()+"' and ";// a0100 ";
							if(!tablename.equalsIgnoreCase("t#"+this.userview.getUserName()+"_gz_Dec"))
								sql+="not ";
						//	sql+=" in (select a0100 from "+tempTableName+" where "+w+" )";
							sql+=" exists (select null from "+tempTableName+" where "+tempTableName+".a0100="+tablename+".a0100 and ( "+w+" ))";
							
							dao.delete(sql,new ArrayList());
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
	 * 删除不在条件范围中的人员
	 * @param tableName
	 * @author dengcan
	 */
	public void delNoConditionData2(String tablename)
	{
		try
		{
			
			ContentDAO dao=new ContentDAO(this.conn);
			/**导入数据*/
			String dbpres=this.templatevo.getString("cbase");
			/**应用库前缀*/
			String[] dbarr=StringUtils.split(dbpres, ",");
			String flag=this.ctrlparam.getValue(SalaryCtrlParamBo.COND_MODE,"flag");  // "":没条件 0：简单条件 1：复杂条件
			String aflag=this.ctrlparam.getValue(SalaryCtrlParamBo.PRIV_MODE,"flag");  // 人员范围权限过滤标志  1：有  
			
			String cond=this.templatevo.getString("cond");
			String cexpr=this.templatevo.getString("cexpr");		
			String sql="";
			ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
			/**需要审批,仅导入起草和驳回记录*/ 
			String whl2="";
			if(isApprove())
			{ 
				whl2+=" and "+ tablename+"."+"sp_flag in('01','07')";				
			}	
			
			
			for(int i=0;i<dbarr.length;i++)
			{
					String pre=dbarr[i];
					
					if(aflag!=null&& "1".equals(aflag))
					{
						String asql="delete from "+tablename+" where upper(nbase)='"+pre.toUpperCase()+"' and a0100 not in (select a0100 "+this.userview.getPrivSQLExpression(pre, false)+" )"+whl2;
						dao.delete(asql,new ArrayList());
					}
					
					if(flag!=null&& "0".equals(flag)&&cond.length()>0)  //0：简单条件
					{
						FactorList factor = new FactorList(cexpr, cond,pre, false, false, true, 1, "su");				
						String strSql ="";
						if(factor.size()>0)
						{
							strSql=factor.getSqlExpression();				
							sql="delete from "+tablename+" where upper(nbase)='"+pre.toUpperCase()+"' and a0100 ";
							sql+="not in (select "+pre+"a01.a0100 "+strSql+" )"+whl2;
							dao.delete(sql,new ArrayList());
						}
					}
					
					if(flag!=null&& "1".equals(flag)&&cond.length()>0)  // 1：复杂条件
					{
						
						int infoGroup = 0; // forPerson 人员
						int varType = 8; // logic	
						
						String whereIN="select a0100 from "+pre+"A01";
						alUsedFields.addAll(this.getMidVariableList());
						YksjParser yp = new YksjParser(this.userview ,alUsedFields,
								YksjParser.forSearch, varType, infoGroup, "Ht",pre.toString());
						YearMonthCount ymc=null;							
						yp.run_Where(cond, ymc,"","hrpwarn_result", dao, whereIN,this.conn,"A", null);
						String tempTableName = yp.getTempTableName();
						String w = yp.getSQL();
						if(w!=null&&w.trim().length()>0)
						{
							sql="delete from "+tablename+" where upper(nbase)='"+pre.toUpperCase()+"' and a0100 ";
							sql+="not ";
							sql+=" in (select a0100 from "+tempTableName+" where "+w+" )"+whl2;
							dao.delete(sql,new ArrayList());
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
	 * 导入时删除不在条件范围中的人员
	 * @param tableName
	 * @author dengcan
	 */
	public int delNoConditionData3(String tablename,HashMap insertRecordMap)
	{
		int num=0;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=null;
			/**导入数据*/
			String dbpres=this.templatevo.getString("cbase");
			/**应用库前缀*/
			String[] dbarr=StringUtils.split(dbpres, ",");
			String flag=this.ctrlparam.getValue(SalaryCtrlParamBo.COND_MODE,"flag");  // "":没条件 0：简单条件 1：复杂条件
			String aflag=this.ctrlparam.getValue(SalaryCtrlParamBo.PRIV_MODE,"flag");  // 人员范围权限过滤标志  1：有  
			
			String cond=this.templatevo.getString("cond");
			String cexpr=this.templatevo.getString("cexpr");		
			String sql="";
			ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
			HashSet keySet=new HashSet();
			SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
			for(int i=0;i<dbarr.length;i++)
			{
					String pre=dbarr[i];
					
					if(aflag!=null&& "1".equals(aflag))
					{
						String asql="select a00z0,a00z1,nbase,a0100 from "+tablename+" where upper(nbase)='"+pre.toUpperCase()+"' and a0100 not in (select a0100 "+this.userview.getPrivSQLExpression(pre, false)+" )";
						rowSet=dao.search(asql);
						while(rowSet.next())
						{
							String key=pre.toLowerCase()+"|"+rowSet.getString("a0100")+"|"+df.format(rowSet.getDate("a00z0"))+"|"+rowSet.getString("a00z1");
							keySet.add(key);
						}
						
						asql="delete from "+tablename+" where upper(nbase)='"+pre.toUpperCase()+"' and a0100 not in (select a0100 "+this.userview.getPrivSQLExpression(pre, false)+" )";
						dao.delete(asql,new ArrayList());
					}
					
					if(flag!=null&& "0".equals(flag)&&cond.length()>0)  //0：简单条件
					{
						FactorList factor = new FactorList(cexpr, cond,pre, false, false, true, 1, "su");				
						String strSql ="";
						if(factor.size()>0)
						{
							strSql=factor.getSqlExpression();	
							
							sql="select a00z0,a00z1,nbase,a0100 from "+tablename+" where upper(nbase)='"+pre.toUpperCase()+"' and a0100 ";
							sql+="not in (select "+pre+"a01.a0100 "+strSql+" )";
							rowSet=dao.search(sql);
							while(rowSet.next())
							{
									String key=pre.toLowerCase()+"|"+rowSet.getString("a0100")+"|"+df.format(rowSet.getDate("a00z0"))+"|"+rowSet.getString("a00z1");
									keySet.add(key);
							} 
							
							sql="delete from "+tablename+" where upper(nbase)='"+pre.toUpperCase()+"' and a0100 ";
							sql+="not in (select "+pre+"a01.a0100 "+strSql+" )";
							dao.delete(sql,new ArrayList());
						}
					}
					if(flag!=null&& "1".equals(flag)&&cond.length()>0)  // 1：复杂条件
					{
						
						int infoGroup = 0; // forPerson 人员
						int varType = 8; // logic	
						
						String whereIN="select a0100 from "+pre+"A01";
						alUsedFields.addAll(this.getMidVariableList());
						YksjParser yp = new YksjParser(this.userview ,alUsedFields,
								YksjParser.forSearch, varType, infoGroup, "Ht",pre.toString());
						YearMonthCount ymc=null;							
						yp.run_Where(cond, ymc,"","hrpwarn_result", dao, whereIN,this.conn,"A", null);
						String tempTableName = yp.getTempTableName();
						String w = yp.getSQL();
						if(w!=null&&w.trim().length()>0)
						{
							
							sql="select a00z0,a00z1,nbase,a0100 from "+tablename+" where upper(nbase)='"+pre.toUpperCase()+"' and a0100 ";
							sql+="not ";
							sql+=" in (select a0100 from "+tempTableName+" where "+w+" )";
							rowSet=dao.search(sql);
							while(rowSet.next())
							{
									String key=pre.toLowerCase()+"|"+rowSet.getString("a0100")+"|"+df.format(rowSet.getDate("a00z0"))+"|"+rowSet.getString("a00z1");
									keySet.add(key);
							} 
							
							sql="delete from "+tablename+" where upper(nbase)='"+pre.toUpperCase()+"' and a0100 ";
							sql+="not ";
							sql+=" in (select a0100 from "+tempTableName+" where "+w+" )";
							dao.delete(sql,new ArrayList());
						}
					}
			}
		
			for(Iterator t=keySet.iterator();t.hasNext();)
			{
				String str=(String)t.next();
				if(insertRecordMap.get(str)!=null)
					insertRecordMap.remove(str);
					num++;
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return num;
	}
	
	/**
	 * 创建新增或减少人员及停发表结构
	 * flag： 1新增  2减少 zhaoxg add
	 * @param tablename
	 * @throws GeneralException
	 */
	private void createInsDecTableStruct(String tablename,String flag,ArrayList list) throws GeneralException {
		try
		{
			DbWizard dbw=new DbWizard(this.conn);
			Table table=new Table(tablename);
			ContentDAO dao=new ContentDAO(this.conn);
			String cloumnStr="DBNAME,A0100,A0000,B0110,E0122,A01Z0,A0101,STATE";
			boolean addflag=this.isAddColumn(this.getOnlyField(), cloumnStr);
			StringBuffer column = new StringBuffer();
			if("1".equals(flag)&&list.size()>0){
				for(int i=0;i<list.size();i++){
					CommonData obj = (CommonData)list.get(i);
					column.append(","+obj.getDataValue().toLowerCase()+"");
				}
			}else if("2".equals(flag)&&list.size()>0){
				for(int i=0;i<list.size();i++){
					CommonData obj = (CommonData)list.get(i);
					column.append(","+obj.getDataValue().toLowerCase()+"");
				}
			}


			if(dbw.isExistTable(tablename, false))
			{
				/**当加入唯一性指标的话，因为唯一性指标时灵活定义的，这样就必须重新建表了*/
				/*RowSet rowSet=dao.search("select * from "+tablename+" where 1=2");
				ResultSetMetaData meta=rowSet.getMetaData();
				if((meta.getColumnCount()==8&&!this.isAddColumn(this.getOnlyField(), cloumnStr))||(meta.getColumnCount()==9&&this.isAddColumn(this.getOnlyField(), cloumnStr)))
					dao.delete("delete from "+tablename, new ArrayList());
				else
				{*/
					dbw.dropTable(table);
					Field field=new Field("DBNAME","DBNAME");
					field.setDatatype(DataType.STRING);
					field.setLength(3);
					field.setNullable(false);
					field.setKeyable(true);
					table.addField(field);
					field=new Field("A0100","A0100");
					field.setDatatype(DataType.STRING);
					field.setLength(8);
					field.setNullable(false);
					field.setKeyable(true);	
					table.addField(field);
					field=new Field("A0000","A0000");
					field.setDatatype(DataType.INT);
					table.addField(field);
					field=new Field("B0110","B0110");
					field.setDatatype(DataType.STRING);
					field.setLength(30);			
					table.addField(field);			
					field=new Field("E0122","E0122");
					field.setDatatype(DataType.STRING);
					field.setLength(30);			
					table.addField(field);	
					field=new Field("A01Z0","A01Z0");
					field.setDatatype(DataType.STRING);
					field.setLength(2);			
					table.addField(field);		
					field=new Field("A0101","A0101");
					field.setDatatype(DataType.STRING); 
					field.setLength(DataDictionary.getFieldItem("a0101").getItemlength());			
					table.addField(field);	
					field=new Field("STATE","STATE");
					field.setDatatype(DataType.STRING);
					field.setLength(10);			
					table.addField(field);
					/**如果系统设置唯一性指标，则加入*/
					if(this.isAddColumn(this.getOnlyField(), cloumnStr))
					{
						FieldItem item = DataDictionary.getFieldItem(this.getOnlyField());
						if(item!=null)
						{
							field = item.cloneField();
							table.addField(field);
						}
					}
					if(column.length()>0){//新增或减少人员设置的字段加进临时表，搜房网   zhaoxg add 2013-11-13
						for(int i=0;i<column.substring(1).split(",").length;i++){
							FieldItem item = DataDictionary.getFieldItem(column.substring(1).split(",")[i]);
							if((item!=null&&cloumnStr.indexOf(item.getItemid())==-1)&&!(addflag&&this.getOnlyField().equalsIgnoreCase(item.getItemid())))//过滤重复字段
							{
								field = item.cloneField();
								//由于薪资临时表代码型长度固定为50 所以这里需要同步 zhanghua 2017-7-25
								if(StringUtils.isNotBlank(field.getCodesetid())&&!"0".equalsIgnoreCase(field.getCodesetid())&&field.getLength()<50)
									field.setLength(50);
								table.addField(field);
							}
						}
					}
					dbw.createTable(table);
				/*}*/
				/*rowSet.close();*/
			}
			else
			{
				Field field=new Field("DBNAME","DBNAME");
				field.setDatatype(DataType.STRING);
				field.setLength(3);
				field.setNullable(false);
				field.setKeyable(true);
				table.addField(field);
				field=new Field("A0100","A0100");
				field.setDatatype(DataType.STRING);
				field.setLength(8);
				field.setNullable(false);
				field.setKeyable(true);	
				table.addField(field);
				field=new Field("A0000","A0000");
				field.setDatatype(DataType.INT);
				table.addField(field);
				field=new Field("B0110","B0110");
				field.setDatatype(DataType.STRING);
				field.setLength(30);			
				table.addField(field);			
				field=new Field("E0122","E0122");
				field.setDatatype(DataType.STRING);
				field.setLength(30);			
				table.addField(field);	
				field=new Field("A01Z0","A01Z0");
				field.setDatatype(DataType.STRING);
				field.setLength(2);			
				table.addField(field);		
				field=new Field("A0101","A0101");
				field.setDatatype(DataType.STRING);
				field.setLength(DataDictionary.getFieldItem("a0101").getItemlength());			
				table.addField(field);	
				field=new Field("STATE","STATE");
				field.setDatatype(DataType.STRING);
				field.setLength(10);			
				table.addField(field);
				if(this.isAddColumn(this.getOnlyField(), cloumnStr))
				{
					FieldItem item = DataDictionary.getFieldItem(this.getOnlyField());
					if(item!=null)
					{
						field = item.cloneField();
						table.addField(field);
					}
				}
				if(column.length()>0){//新增或减少人员设置的字段加进临时表，搜房网   zhaoxg add 2013-11-13
					for(int i=0;i<column.substring(1).split(",").length;i++){
						FieldItem item = DataDictionary.getFieldItem(column.substring(1).split(",")[i]);
						if((item!=null&&cloumnStr.indexOf(item.getItemid())==-1)&&!(addflag&&this.getOnlyField().equalsIgnoreCase(item.getItemid())))//过滤重复字段
						{
							field = item.cloneField();
							//由于薪资临时表代码型长度固定为50 所以这里需要同步 zhanghua 2017-7-25
							if(StringUtils.isNotBlank(field.getCodesetid())&&!"0".equalsIgnoreCase(field.getCodesetid())&&field.getLength()<50)
								field.setLength(50);
							table.addField(field);
						}
					}
				}
				dbw.createTable(table);
			}

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 创建新增或减少人员及停发表结构
	 * @param tablename
	 * @throws GeneralException
	 */
	private void createInsDecTableStruct(String tablename) throws GeneralException {
		try
		{
			DbWizard dbw=new DbWizard(this.conn);
			Table table=new Table(tablename);
			ContentDAO dao=new ContentDAO(this.conn);
			String cloumnStr="DBNAME,A0100,A0000,B0110,E0122,A01Z0,A0101,STATE";


			if(dbw.isExistTable(tablename, false))
			{
				/**当加入唯一性指标的话，因为唯一性指标时灵活定义的，这样就必须重新建表了*/
				/*RowSet rowSet=dao.search("select * from "+tablename+" where 1=2");
				ResultSetMetaData meta=rowSet.getMetaData();
				if((meta.getColumnCount()==8&&!this.isAddColumn(this.getOnlyField(), cloumnStr))||(meta.getColumnCount()==9&&this.isAddColumn(this.getOnlyField(), cloumnStr)))
					dao.delete("delete from "+tablename, new ArrayList());
				else
				{*/
					dbw.dropTable(table);
					Field field=new Field("DBNAME","DBNAME");
					field.setDatatype(DataType.STRING);
					field.setLength(3);
					field.setNullable(false);
					field.setKeyable(true);
					table.addField(field);
					field=new Field("A0100","A0100");
					field.setDatatype(DataType.STRING);
					field.setLength(8);
					field.setNullable(false);
					field.setKeyable(true);	
					table.addField(field);
					field=new Field("A0000","A0000");
					field.setDatatype(DataType.INT);
					table.addField(field);
					field=new Field("B0110","B0110");
					field.setDatatype(DataType.STRING);
					field.setLength(30);			
					table.addField(field);			
					field=new Field("E0122","E0122");
					field.setDatatype(DataType.STRING);
					field.setLength(30);			
					table.addField(field);	
					field=new Field("A01Z0","A01Z0");
					field.setDatatype(DataType.STRING);
					field.setLength(2);			
					table.addField(field);		
					field=new Field("A0101","A0101");
					field.setDatatype(DataType.STRING); 
					field.setLength(DataDictionary.getFieldItem("a0101").getItemlength());			
					table.addField(field);	
					field=new Field("STATE","STATE");
					field.setDatatype(DataType.STRING);
					field.setLength(10);			
					table.addField(field);
					/**如果系统设置唯一性指标，则加入*/
					if(this.isAddColumn(this.getOnlyField(), cloumnStr))
					{
						FieldItem item = DataDictionary.getFieldItem(this.getOnlyField());
						if(item!=null)
						{
							field = item.cloneField();
							table.addField(field);
						}
					}
					dbw.createTable(table);
				/*}*/
				/*rowSet.close();*/
			}
			else
			{
				Field field=new Field("DBNAME","DBNAME");
				field.setDatatype(DataType.STRING);
				field.setLength(3);
				field.setNullable(false);
				field.setKeyable(true);
				table.addField(field);
				field=new Field("A0100","A0100");
				field.setDatatype(DataType.STRING);
				field.setLength(8);
				field.setNullable(false);
				field.setKeyable(true);	
				table.addField(field);
				field=new Field("A0000","A0000");
				field.setDatatype(DataType.INT);
				table.addField(field);
				field=new Field("B0110","B0110");
				field.setDatatype(DataType.STRING);
				field.setLength(30);			
				table.addField(field);			
				field=new Field("E0122","E0122");
				field.setDatatype(DataType.STRING);
				field.setLength(30);			
				table.addField(field);	
				field=new Field("A01Z0","A01Z0");
				field.setDatatype(DataType.STRING);
				field.setLength(2);			
				table.addField(field);		
				field=new Field("A0101","A0101");
				field.setDatatype(DataType.STRING);
				field.setLength(DataDictionary.getFieldItem("a0101").getItemlength());			
				table.addField(field);	
				field=new Field("STATE","STATE");
				field.setDatatype(DataType.STRING);
				field.setLength(10);			
				table.addField(field);
				if(this.isAddColumn(this.getOnlyField(), cloumnStr))
				{
					FieldItem item = DataDictionary.getFieldItem(this.getOnlyField());
					if(item!=null)
					{
						field = item.cloneField();
						table.addField(field);
					}
				}
				dbw.createTable(table);
			}

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 创建信息变动临时表
	 * @param tablename
	 * @throws GeneralException
	 */
	private void createChangeInfoTableStruct(String tablename,String fieldStr,String filterid) throws GeneralException {
		DbWizard dbw=new DbWizard(this.conn);
		Table table=new Table(tablename);
		dbw.dropTable(table);
		
		Field field=new Field("DBNAME","DBNAME");
		field.setDatatype(DataType.STRING);
		field.setLength(3);
		field.setNullable(false);
		field.setKeyable(true);
		table.addField(field);
		field=new Field("A0100","A0100");
		field.setDatatype(DataType.STRING);
		field.setLength(8);
		field.setNullable(false);
		field.setKeyable(true);	
		table.addField(field);
		field=new Field("A0000","A0000");
		field.setDatatype(DataType.INT);
		table.addField(field);
		field=new Field("B0110","B0110");
		field.setDatatype(DataType.STRING);
		field.setLength(30);			
		table.addField(field);	
		field=new Field("B01101","B01101");
		field.setDatatype(DataType.STRING);
		field.setLength(30);			
		table.addField(field);		
		field=new Field("E0122","E0122");
		field.setDatatype(DataType.STRING);
		field.setLength(30);			
		table.addField(field);	
		field=new Field("E01221","E01221");
		field.setDatatype(DataType.STRING);
		field.setLength(30);			
		table.addField(field);			
		field=new Field("A0101","A0101");
		field.setDatatype(DataType.STRING);
		field.setLength(DataDictionary.getFieldItem("a0101").getItemlength());			
		table.addField(field);	
		field=new Field("A01011","A01011");
		field.setDatatype(DataType.STRING);
		field.setLength(DataDictionary.getFieldItem("a0101").getItemlength());			
		table.addField(field);			
		field=new Field("STATE","STATE");
		field.setDatatype(DataType.STRING);
		field.setLength(10);			
		table.addField(field);
		/**加入自定义的指标*/
		ArrayList f_compare_field=this.getField(SalaryCtrlParamBo.COMPARE_FIELD);
		String cloumnStr="DBNAME,A0100,A0000,B0110,E0122,A0101,STATE";
		if(f_compare_field!=null&&f_compare_field.size()>0)
		{
			for(int i=0;i<f_compare_field.size();i++)
			{
				FieldItem item = (FieldItem)f_compare_field.get(i);
				//由于薪资临时表代码型长度固定为50 所以这里需要同步 zhanghua 2017-7-25
				if(StringUtils.isNotBlank(item.getCodesetid())&&!"0".equalsIgnoreCase(item.getCodesetid())&&item.getItemlength()<50)
					item.setItemlength(50);
				if(!"null".equalsIgnoreCase(filterid)&&!"all".equalsIgnoreCase(filterid))
				{
					if(fieldStr.toUpperCase().indexOf(item.getItemid().toUpperCase())==-1)
						continue;
				}
				cloumnStr+=","+item.getItemid();
				field=new Field(item.getItemid(),item.getItemdesc());
				if("N".equalsIgnoreCase(item.getItemtype()))
				{
					if(item.getDecimalwidth()>0)
					{
						field.setDatatype(DataType.DOUBLE);
						field.setDecimalDigits(item.getDecimalwidth());
					}
					else
					{
						field.setDatatype(DataType.INT);
					}
				}else if("D".equalsIgnoreCase(item.getItemtype()))
				{
					field.setDatatype(DataType.DATE);
				}
				else
					field.setDatatype(DataType.STRING);
				field.setLength(item.getItemlength());			
				table.addField(field);	
				/**现在的*/
				field=new Field(item.getItemid()+"1",item.getItemdesc());
				if("N".equalsIgnoreCase(item.getItemtype()))
				{
					if(item.getDecimalwidth()>0)
					{
						field.setDatatype(DataType.DOUBLE);
						field.setDecimalDigits(item.getDecimalwidth());
					}
					else
					{
						field.setDatatype(DataType.INT);
					}
				}else if("D".equalsIgnoreCase(item.getItemtype()))
				{
					field.setDatatype(DataType.DATE);
				}
				else
					field.setDatatype(DataType.STRING);
				field.setLength(item.getItemlength());			
				table.addField(field);		
				//标识子集是否有数据（0：没有）
				field=new Field(item.getItemid()+"0",item.getItemdesc());
				field.setDatatype(DataType.INT);
				table.addField(field);	
				
			}
		}
		boolean addflag=this.isAddColumn(this.getOnlyField(), cloumnStr);
		if(addflag)
		{
			FieldItem item = DataDictionary.getFieldItem(this.getOnlyField());
			table.addField(item.cloneField());
		}
		dbw.createTable(table);
		
	}
	
	
	/**
	 * 判断薪资类别属性中设置的人员范围是否有效
	 * @param salaryid
	 */
	public void  updateSalarySetDbpres(String salaryid)
	{
		try
		{
			DbWizard dbw=new DbWizard(this.conn);
			String dbpres=this.templatevo.getString("cbase");
			/**应用库前缀*/
			String[] dbarr=StringUtils.split(dbpres, ",");
			String newDbpres="";
			boolean flag=false;
			for(int i=0;i<dbarr.length;i++)
			{
				String pre=dbarr[i];
				if(dbw.isExistTable(pre+"a01",false))
				{
					newDbpres+=pre+",";
				}
				else
					flag=true;
			}
			if(!newDbpres.equalsIgnoreCase(dbpres)&&flag)
			{
				this.templatevo.setString("cbase", newDbpres);
				dbw.execute("update salarytemplate set cbase='"+newDbpres+"' where salaryid="+salaryid); 
			}
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	
	
	private HashMap complexWhlMap=new HashMap();
	
	/**
	 * 把得减少人员生成一张临时表
	 * @return 用户名+DecPeoples
	 */
	public String createDelManTable(){
		/**临时表名*/
		String tablename="t#"+this.userview.getUserName()+"_gz_Dec";
		
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			ArrayList list = add_del_rightList(salaryid,"2");
			createInsDecTableStruct(tablename,"2",list);
			DbSecurityImpl dbS = new DbSecurityImpl();
			dbS.encryptTableName(this.conn, tablename);//创建临时表时需注册表
			/**导入数据*/
			String dbpres=this.templatevo.getString("cbase");
			/**应用库前缀*/
			String[] dbarr=StringUtils.split(dbpres, ",");
			StringBuffer buf=new StringBuffer();
			String cloumnStr="DBNAME,A0100,A0000,B0110,E0122,A0101,STATE";
			boolean addflag=this.isAddColumn(this.getOnlyField(), cloumnStr);
			String flag=this.ctrlparam.getValue(SalaryCtrlParamBo.COND_MODE,"flag");  // "":没条件 0：简单条件 1：复杂条件
			String cond=this.templatevo.getString("cond");
			String cexpr=this.templatevo.getString("cexpr");
			ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
			DbWizard dbw=new DbWizard(this.conn);
			complexWhlMap=new HashMap();
			
			RowSet _rowSet=dao.search("select count(a0100) from "+this.gz_tablename);
			int count=0;
			if(_rowSet.next())
				count=_rowSet.getInt(1);
			if(count>0)
			{
				StringBuffer column = new StringBuffer();//减少人员的相关字段信息存入临时表，搜房网  zhaoxg add 2013-11-14
				if(list.size()>0){
					for(int i=0;i<list.size();i++){
						CommonData obj = (CommonData)list.get(i);
						if(cloumnStr.toLowerCase().indexOf(obj.getDataValue().toLowerCase())!=-1){//排除默认项
							continue;
						}
						if(addflag&&this.getOnlyField().toLowerCase().indexOf(obj.getDataValue().toLowerCase())!=-1){//如果有唯一指标也排除  搜房网  zhaoxg add
							continue;
						}
						column.append(","+obj.getDataValue().toLowerCase()+"");
					}
				}
			for(int i=0;i<dbarr.length;i++)
			{
				String pre=dbarr[i];
				
				if(!this.userview.isSuper_admin()&&this.userview.getDbpriv().toString().toLowerCase().indexOf(","+pre.toLowerCase()+",")==-1)
					continue;
				//String srctable=pre+"A01";
				buf.setLength(0);
				buf.append("insert into ");
				buf.append(tablename);
				buf.append("(DBNAME,A0100,A0000,B0110,E0122,A0101,STATE"+column);
				buf.append(")"); 
				buf.append(" select  ");//distinct
				buf.append("NBASE,A0100,A0000,B0110,E0122,A0101,'1' as STATE "+column);
				buf.append(" from ");
				buf.append("(select * from (select * from "+this.gz_tablename+" where A00Z0=(select MAX(A00Z0) from "+this.gz_tablename+" a where a.A0100= "+this.gz_tablename+".A0100)) b ");
				buf.append("where A00Z1=(select MAX(A00Z1) from (select * from "+this.gz_tablename+" where A00Z0=(select MAX(A00Z0) from "+this.gz_tablename+" a where a.A0100= "+this.gz_tablename+".A0100)) c where b.A0100=c.A0100)) ");
				buf.append(this.gz_tablename);
				buf.append(" where ( (");
			//	buf.append(" A0100 not in (select A0100  ");
				buf.append(" not exists (select null ");
				
				String aflag=this.ctrlparam.getValue(SalaryCtrlParamBo.PRIV_MODE,"flag");  // 人员范围权限过滤标志  1：有  
				if(this.manager!=null&&this.manager.length()>0&&!this.manager.equalsIgnoreCase(this.userview.getUserName()))
				{
					buf.append(" from "+pre+"A01 where 1=1 ");
				}
				else
				{
					if(aflag!=null&& "1".equals(aflag))
						buf.append(this.userview.getPrivSQLExpression(pre, false));				
					else
						buf.append(" from "+pre+"A01 where 1=1 ");
				}
				buf.append(" and "+this.gz_tablename+".a0100="+pre+"A01.a0100");
				
				buf.append(") and upper(NBASE)='");
				buf.append(pre.toUpperCase());
				buf.append("' )");
				
				
				
				if("0".equals(flag)&&cond.length()>0)  //0：简单条件
				{
					FactorList factor = new FactorList(cexpr, cond,pre, false, false, true, 1, "su");				
					String strSql = factor.getSqlExpression();
					 
				//	buf.append(" or ( a0100 not   in (select "+pre+"a01.a0100 "+strSql+" )  and upper(NBASE)='"+pre.toUpperCase()+"' )");
					if(strSql.toLowerCase().indexOf("where")==-1)
						buf.append(" or (   not exists   (select null "+strSql+" where "+pre+"a01.a0100="+this.gz_tablename+".a0100  )  and upper(NBASE)='"+pre.toUpperCase()+"' )");
					else
						buf.append(" or (   not exists   (select null "+strSql+" and "+pre+"a01.a0100="+this.gz_tablename+".a0100  )  and upper(NBASE)='"+pre.toUpperCase()+"' )");
					
				}
				if("1".equals(flag)&&cond.length()>0)  // 1：复杂条件
				{
					
					int infoGroup = 0; // forPerson 人员
					int varType = 8; // logic								
					String whereIN="";
		 	
					
			//		whereIN=InfoUtils.getWhereINSql(this.userview,pre);
			//		whereIN="select "+pre+"A01.a0100 "+whereIN;	
					
					
					
					alUsedFields.addAll(this.getMidVariableList());
					YksjParser yp = new YksjParser(this.userview ,alUsedFields,
							YksjParser.forSearch, varType, infoGroup, "Ht",pre.toString());
					YearMonthCount ymc=null;	

					
					if(currym!=null)
					{
						cond=cond.replaceAll("归属日期\\(\\)","#"+currym+"#");
						cond=cond.replaceAll("归属日期","#"+currym+"#");
					}
					else
						
					{
						HashMap ycmap=getYearMonthCount2();   //dengcan
						/**年月和次数*/
						String ym=(String)ycmap.get("ym"); 
						cond=cond.replaceAll("归属日期\\(\\)","#"+ym+"#");
						cond=cond.replaceAll("归属日期","#"+ym+"#");
					}
		
					yp.run_Where(cond, ymc,"","hrpwarn_result", dao, whereIN,this.conn,"A", null);
					String tempTableName = yp.getTempTableName();
					String w = yp.getSQL();
					if(w!=null&&w.trim().length()>0)
					{
						String _tempName="t#"+this.userview.getUserName()+"_gz_"+pre.toLowerCase()+"_cond"; 
						complexWhlMap.put(pre.toLowerCase()+"_whl",w.replaceAll(tempTableName, _tempName));//2014-08-12 dengcan
						
				//		if (dbw.isExistTable(_tempName, false)) 
						{
							dbw.dropTable(_tempName);
						//	dbw.execute("drop table "+_tempName);
						}
						if(Sql_switcher.searchDbServer()==2)
							dao.update("create table "+_tempName+" as select * from "+tempTableName);
						else 
							dao.update("select * into "+_tempName+" from "+tempTableName);
							  
						//buf.append(" or ( a0100 not   in (select a0100 from "+tempTableName+" where "+w+" )  and upper(NBASE)='"+pre.toUpperCase()+"'  )");
						buf.append(" or (   not exists   (select null  from "+tempTableName+" where ("+w+") and "+tempTableName+".a0100="+this.gz_tablename+".a0100  )  and upper(NBASE)='"+pre.toUpperCase()+"' )");
						
					}
				}
				
				buf.append(" ) ");
				if(this.manager!=null&&this.manager.length()>0&&!this.manager.equalsIgnoreCase(this.userview.getUserName()))
				{
				//	buf.append(" and (  a0100 in (select a0100 "+this.userview.getPrivSQLExpression(pre, false)+" )  and upper(NBASE)='"+pre.toUpperCase()+"' )");
					String showUnitCodeTree=controlByUnitcode();
					if("1".equals(showUnitCodeTree))
					{
						String whl_str=getWhlByUnits();
						if(whl_str.length()>0)
						{
							buf.append(whl_str);
						}
					}
					else
						buf.append(" and (  exists (select null "+this.userview.getPrivSQLExpression(pre, false)+" and "+pre+"A01.a0100="+this.gz_tablename+".a0100 )  and upper(NBASE)='"+pre.toUpperCase()+"' )");
					
					
				}
			//	System.out.println(buf.toString());
				dao.update(buf.toString());
			}//for i loop end.
			
			//将不在薪资类别定义中的人员库范围数据插入减少人员表中
			RowSet rowSet=dao.search("select distinct nbase from "+this.gz_tablename);
			while(rowSet.next())
			{
				String nbase=rowSet.getString("nbase");
				boolean isNoExist=true;
				for(int i=0;i<dbarr.length;i++)
				{
					String pre=dbarr[i];
					if(nbase.equalsIgnoreCase(pre))
					{
						isNoExist=false;
						break;
					}
				}
				if(isNoExist)
				{
					buf=new StringBuffer("");
					buf.append("insert into ");
					buf.append(tablename);
					buf.append("(DBNAME,A0100,A0000,B0110,E0122,A0101,STATE"+column);
					buf.append(") select ");
					buf.append("NBASE,A0100,A0000,B0110,E0122,A0101,'1' as STATE "+column);
					buf.append(" from "+this.gz_tablename+" where upper(nbase)='"+nbase.toUpperCase()+"'");
					dao.update(buf.toString());
				}
			}
			rowSet.close();
			
			
			/**薪资类别定义的条件，删除不在条件范围中的人员*/
			delNoConditionData(tablename);
			// 报批的数据不能出现在减少人员面板里
			String flow_flag=this.ctrlparam.getValue(SalaryCtrlParamBo.FLOW_CTRL, "flag");			
			if(flow_flag!=null&& "1".equalsIgnoreCase(flow_flag))  //需要审批
			{
				buf.setLength(0);
				buf.append("delete from "+tablename+" where not exists (select null from "+this.gz_tablename);
				buf.append("  where   "+tablename+".a0100="+this.gz_tablename+".a0100 and lower("+tablename+".dbname)=lower("+this.gz_tablename+".nbase) ");
				buf.append(" and  ( sp_flag='01' or sp_flag='07' or sp_flag='06' ) ");
				buf.append(")");
				dao.update(buf.toString());
			}
			/**导入唯一性指标的值*/
			if(addflag)
			{
				String asql = " select distinct DBNAME from "+tablename;
				RowSet rs = dao.search(asql);
				StringBuffer abuf = new StringBuffer("");
				while(rs.next())
				{
					abuf.append(rs.getString("DBNAME")+",");
				}
				rs.close();
				String[] arr=abuf.toString().split(",");
				StringBuffer sql_buf=new StringBuffer("");
				for(int j=0;j<arr.length;j++)
				{
					if(arr[j]==null|| "".equals(arr[j]))
						continue;
					if(!dbw.isExistTable(arr[j]+"A01",false))
						continue;
					sql_buf.setLength(0);
					sql_buf.append(" update "+tablename+" set ");
					sql_buf.append(this.getOnlyField()+"=(select "+this.getOnlyField());
					sql_buf.append(" from "+arr[j]+"A01 where "+tablename+".a0100="+arr[j]+"a01.a0100 and ");
					sql_buf.append("UPPER("+tablename+".DBNAME)='"+arr[j].toUpperCase()+"') where exists(");
					sql_buf.append(" select null from "+arr[j]+"a01 where "+tablename+".a0100="+arr[j]+"a01.a0100 and ");
					sql_buf.append("UPPER("+tablename+".dbname)='"+arr[j].toUpperCase()+"')");
					dao.update(sql_buf.toString());
				}
				
			}
			
			}
			_rowSet.close();
			
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			tablename="";
		}
		
		return tablename;
	}
	
	
	/**
	 * 判断表中是否有数据
	 * @param tableName
	 * @return
	 */
	public boolean getTableIsData(String tableName)
	{
		boolean flag=false;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select count(*) from "+tableName);
			if(rowSet.next()&&rowSet.getInt(1)>0)
				flag=true;
			rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return flag;
	}
	
	
	
	
	/**
	 * 把停发人员，生成一张临时表
	 * @return 用户名+tfPeoples
	 */
	public String createA01Z0ChangeManTable(){
		/**临时表名*/
		String tablename="t#"+this.userview.getUserName()+"_gz_Tf";
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			createInsDecTableStruct(tablename);
			DbSecurityImpl dbS = new DbSecurityImpl();
			dbS.encryptTableName(this.conn, tablename);
			String cloumnStr="DBNAME,A0100,A0000,B0110,E0122,A01Z0,A0101,STATE";
			/**导入数据*/
			String dbpres=this.templatevo.getString("cbase");
			/**应用库前缀*/
			String[] dbarr=StringUtils.split(dbpres, ",");
			StringBuffer buf=new StringBuffer();
			for(int i=0;i<dbarr.length;i++)
			{
				String pre=dbarr[i];
				if(!this.userview.isSuper_admin()&&this.userview.getDbpriv().toString().toLowerCase().indexOf(","+pre.toLowerCase()+",")==-1)
					continue;
				
				String srctable=pre+"A01";
				buf.setLength(0);
				buf.append("insert into ");
				buf.append(tablename);
				buf.append("(DBNAME,A0100,A0000,B0110,E0122,A0101,A01Z0,STATE");
				if(this.isAddColumn(this.getOnlyField(), cloumnStr))
				{
					buf.append(","+this.getOnlyField());
				}
				buf.append(")");
				buf.append(" select ");
				buf.append("'");
				buf.append(pre);
				buf.append("'");
				buf.append(" as NBASE,");
				buf.append(srctable);
				buf.append(".A0100,");
				buf.append(srctable);
				buf.append(".A0000,");
				buf.append(srctable);
				buf.append(".B0110,");	
				buf.append(srctable);
				buf.append(".E0122,");	
				buf.append(srctable);
				buf.append(".A0101,");	
				buf.append(srctable);
				buf.append(".A01Z0,");				
				buf.append("'1' as STATE ");
				if(this.isAddColumn(this.getOnlyField(), cloumnStr))
				{
					buf.append(","+srctable+"."+this.getOnlyField());
				}
				buf.append(" from ");
				buf.append(srctable);
				buf.append(" left join ");
				buf.append(this.gz_tablename);
				buf.append(" on ");
				buf.append(srctable);
				buf.append(".A0100=");
				buf.append(this.gz_tablename);
				buf.append(".A0100");
				buf.append(" where upper(");
				buf.append(this.gz_tablename);
				buf.append(".NBASE)=");
				buf.append("'");
				buf.append(pre.toUpperCase());
				buf.append("'");
				buf.append(" and ");
			
				buf.append(" case when  "+this.gz_tablename+".A01Z0 is null then '1' ");
				buf.append(" when  "+this.gz_tablename+".A01Z0 ='' then '1' ");
				buf.append(" else "+this.gz_tablename+".A01Z0 end <>");
				buf.append(" case when  "+srctable+".A01Z0 is null then '1' ");
				buf.append(" when  "+srctable+".A01Z0 ='' then '1' ");
				buf.append(" else "+srctable+".A01Z0 end");

			//	buf.append(Sql_switcher.isnull(this.gz_tablename+".A01Z0","'1'")+"<>");				
			//	buf.append(Sql_switcher.isnull(srctable+".A01Z0","'1'"));				
				
				/*
				buf.append(" A0100 in (select A0100  ");
				buf.append(this.userview.getPrivSQLExpression(pre, false));				
				buf.append(" and A01Z0='0') and NBASE='");
				buf.append(pre);
				buf.append("'");
				*/
				dao.update(buf.toString());
			}//for i loop end.
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			tablename="";
		}
		return tablename;
	}
	/**
	 * 把信息变动人员生成一张临时表
	 * 单位、部门及姓名|B0110、E0122及A0101信息变动时，在临时中增加记录
	 * 临时表包括如下字段DBName,A0100,A0000,B0110,B01101,E0122,E01221,A0101,A01011,state
	 * 主键字段：DBNAME,A0100
	 * @return 用户名+BdPeoples
	 */
	public String createChangeInfoManTable(String fieldstr,String filterid,String gz_module){
		/**临时表名*/
		String tablename="t#"+this.userview.getUserName()+"_gz_Bd";
		try
		{
			SalaryPropertyBo bo=new SalaryPropertyBo(this.conn,salaryid+"",Integer.parseInt(gz_module),this.userview);
			String priv_mode=bo.getCtrlparam().getValue(SalaryCtrlParamBo.PRIV_MODE, "flag");
			if(priv_mode==null|| "".equals(priv_mode))
			{
				priv_mode = "0";
			}
			String manager=this.getCtrlparam().getValue(SalaryCtrlParamBo.SHARE_SET, "user");
			StringBuffer sb = new StringBuffer("");
		//	if(!this.userview.isSuper_admin())
			{
				if(manager==null||manager.length()==0||this.userview.getUserName().equals(manager))
				{
					
				}
				else
				{
					if("1".equals(this.controlByUnitcode))
					{
						String whl_str=getWhlByUnits();
						sb.append(" where 1=1 "+whl_str);
					}
					else if(manager!=null&&manager.length()>0&&!manager.equalsIgnoreCase(this.userview.getUserName())&&!this.userview.isSuper_admin())
					{
						
						/**导入数据*/
						String dbpres=getTemplatevo().getString("cbase");
						/**应用库前缀*/
						String[] dbarr=StringUtils.split(dbpres, ",");
						StringBuffer sub_str=new StringBuffer("");
						for(int i=0;i<dbarr.length;i++)
						{
							String pre=dbarr[i];
							if(!this.userview.isSuper_admin()&&this.userview.getDbpriv().toString().toLowerCase().indexOf(","+pre.toLowerCase()+",")==-1)
							{
								sub_str.append(" or (upper("+getGz_tablename()+".nbase)='"+pre.toUpperCase()+"'  and 1=2 )");
							}
							else
							{
								sub_str.append(" or (upper("+getGz_tablename()+".nbase)='"+pre.toUpperCase()+"'  and "+getGz_tablename()+".a0100 in (select a0100 "+this.userview.getPrivSQLExpression(pre, false)+" ) )");
							}
							
						}
						if(sub_str.length()>0)
						{
							sb.append(" where ( "+sub_str.substring(3)+" )"); 
						}
					}
					
					/*
					if(priv_mode.equals("1"))
					{
						String code=this.userview.getManagePrivCode();
						String value=this.userview.getManagePrivCodeValue();
						if(code!=null&&!code.equals(""))
						{
							if(code.equalsIgnoreCase("UN"))
							{
								sb.append(" where ("+this.gz_tablename+".b0110 like '"+(value==null?"":value)+"%'");
								if(value==null)
									sb.append(" or "+this.gz_tablename+".b0110 is null");
								sb.append(")");
							}
							if(code.equalsIgnoreCase("UM"))
							{
								sb.append(" where ("+this.gz_tablename+".e0122 like '"+(value==null?"":value)+"%'");
								if(value==null)
									sb.append(" or "+this.gz_tablename+".e0122 is null");
								sb.append(")");
							}
							if(code.equalsIgnoreCase("@K"))
							{
								sb.append(" where ("+this.gz_tablename+".e01a1 like '"+(value==null?"":value)+"%'");
								if(value==null)
									sb.append(" or "+this.gz_tablename+".e01a1 is null");
								sb.append(")");
							}
						}
						else 
						{
							sb.append(" where 1=2 ");
						}
					}
					*/
					
				}
			}
			if(sb.length()<=0)
				sb.append(" where 1=1 ");
			ContentDAO dao=new ContentDAO(this.conn);
			createChangeInfoTableStruct(tablename,fieldstr,filterid);
			DbSecurityImpl dbS = new DbSecurityImpl();
			dbS.encryptTableName(this.conn, tablename);
			RowSet _rowSet=dao.search("select count(a0100) from "+this.gz_tablename);
			int count=0;
			if(_rowSet.next())
				count=_rowSet.getInt(1);
			if(count>0)
			{
				
			
			/**格式e5881,e5884,e5880*/
			String rightvalue=getvalue(String.valueOf(salaryid),SalaryCtrlParamBo.COMPARE_FIELD);		
			/**导入数据*/
			String dbpres=this.templatevo.getString("cbase");
			/**应用库前缀*/
			String[] dbarr=StringUtils.split(dbpres, ",");
			/**选择了比对指标*/
			if(rightvalue!=null&&!"".equals(rightvalue.trim()))
			{
				/**创建一张临时表，做数据比对时用*/
				String tempTableName="t#"+this.userview.getUserName()+"_gz_Bd2";
				createChangeInfoTableStruct(tempTableName,fieldstr,filterid);
				dbS.encryptTableName(this.conn, tempTableName);
				ArrayList f_compare_field=this.getField(SalaryCtrlParamBo.COMPARE_FIELD);
				StringBuffer insert_cloumn_buf = new StringBuffer("dbname,a0100,b01101,e01221,a01011,state");
				StringBuffer select_cloumn_buf= new StringBuffer("nbase as dbname,a0100,b0110,e0122,a0101,'1' as state");
				StringBuffer insert=new StringBuffer("dbname,a0000,a0100,b0110,b01101,e0122,e01221,a0101,a01011,state");
				StringBuffer where_buf = new StringBuffer();
				where_buf.append("(b0110<>b01101 or (b0110 is null and b01101 is not null) or (b01101 is null and b0110 is not null))");
				where_buf.append(" or ");
				where_buf.append("(e0122<>e01221 or (e0122 is null and e01221 is not null) or (e01221 is null and e0122 is not null))");
				where_buf.append(" or ");
				where_buf.append("(a0101<>a01011 or (a0101 is null and a01011 is not null) or (a01011 is null and a0101 is not null))");
				for(int i=0;i<f_compare_field.size();i++)
				{
					FieldItem item = (FieldItem)f_compare_field.get(i);
					if(!"null".equalsIgnoreCase(filterid)&&!"all".equalsIgnoreCase(filterid))
					{
						if(fieldstr.toUpperCase().indexOf(item.getItemid().toUpperCase())==-1)
							continue;
					}
					insert_cloumn_buf.append(","+item.getItemid()+"1");
					select_cloumn_buf.append(","+item.getItemid());
					insert.append(","+item.getItemid()+","+item.getItemid()+"1");
					if("n".equalsIgnoreCase(item.getItemtype()))
					{
						//20140905 dengcan  增加子集必须有数据才校验条件
						where_buf.append(" or ("+Sql_switcher.isnull(item.getItemid(), "0")+"<>"+Sql_switcher.isnull(item.getItemid()+"1","0")+" and "+Sql_switcher.isnull(item.getItemid()+"0","0")+"=1 )");
					}
					else
					{
						//20140905 dengcan  增加子集必须有数据才校验条件
				    	where_buf.append(" or ( ( "+item.getItemid()+"<>"+item.getItemid()+"1 or (");
			    		where_buf.append(item.getItemid()+" is null and "+item.getItemid()+"1 is not null) or (");
			    		where_buf.append(item.getItemid()+"1 is null and "+item.getItemid()+" is not null) )  and "+Sql_switcher.isnull(item.getItemid()+"0","0")+"=1  )");
					}
					
				}
				StringBuffer insert_sql_buf = new StringBuffer();
				for(int i=0;i<dbarr.length;i++)
	    		{
	         		String pre=dbarr[i];
		    		if(!this.userview.isSuper_admin()&&this.userview.getDbpriv().toString().toLowerCase().indexOf(","+pre.toLowerCase()+",")==-1)
		    			continue;
		     		
			    	insert_sql_buf.append(" insert into "+tempTableName+"("+insert_cloumn_buf.toString()+") ");
			    	insert_sql_buf.append("select  "+select_cloumn_buf+" from (select * from "+this.gz_tablename+" "+sb.toString()+"   ");
			    	insert_sql_buf.append(" and UPPER("+this.gz_tablename+".nbase)='"+pre.toUpperCase()+"' and ");
			    	insert_sql_buf.append(this.gz_tablename+".A0100 in (select A0100 from "+pre+"a01)");
			    	insert_sql_buf.append(" and  A00Z0=(select MAX(A00Z0) from "+this.gz_tablename+" a where a.A0100= "+this.gz_tablename+".A0100)");
			    	insert_sql_buf.append(" ) b ");
			    	insert_sql_buf.append("where A00Z1=(select MAX(A00Z1) from (select * from "+this.gz_tablename+" where A00Z0=(select MAX(A00Z0) from "+this.gz_tablename+" a where a.A0100= "+this.gz_tablename+".A0100)) c where b.A0100=c.A0100) ");

			    	
			    	
//			    	insert_sql_buf.append(" select distinct "+select_cloumn_buf+" from "+this.gz_tablename+" "+sb.toString());
//			    	insert_sql_buf.append(" and UPPER("+this.gz_tablename+".nbase)='"+pre.toUpperCase()+"' and ");
//			    	insert_sql_buf.append(this.gz_tablename+".A0100 in (select A0100 from "+pre+"a01)");
			    	/**先将工资表中的所有数据导入临时表*/
			    	dao.update(insert_sql_buf.toString());
			    	insert_sql_buf.setLength(0);
	    		}
				
				HashMap map = this.getSetItemList(rightvalue,filterid,fieldstr);
				Set keyset = map.keySet();
				for(int i=0;i<dbarr.length;i++)
	    		{
	         		String pre=dbarr[i];
		    		if(!this.userview.isSuper_admin()&&this.userview.getDbpriv().toString().toLowerCase().indexOf(","+pre.toLowerCase()+",")==-1)
		    			continue;
		    		StringBuffer deleteFormBuffer = new StringBuffer(" from "+pre+"A01 ");
		    		StringBuffer deleteWhereBuffer = new StringBuffer(" where 1=1 ");
		    		boolean isMainSet=false;
		    		for(Iterator t=keyset.iterator();t.hasNext();)
		    		{
		    			String key=(String)t.next();
		    			insert_cloumn_buf.setLength(0);
		    			select_cloumn_buf.setLength(0);
		    			insert_sql_buf.setLength(0);
		    			ArrayList itemList = (ArrayList)map.get(key.toUpperCase());
		    			/**主集，更新单位等信息*/
		    			if("a01".equalsIgnoreCase(key))
		    			{
		    				isMainSet = true;
		    				if(Sql_switcher.searchDbServer()==Constant.MSSQL)
		    				{
		    					String cloumnStr="a0100,b0100,e0122,a0101";
		    	    			insert_cloumn_buf.append("UPDATE "+tempTableName+" SET a0000=S.a0000,b0110=S.b0110");
		    	    			insert_cloumn_buf.append(",e0122=S.e0122,a0101=S.a0101");
		    	    			for(int j=0;j<itemList.size();j++)
		    	    			{
		    	    				FieldItem it=(FieldItem)itemList.get(j);
		    	    				insert_cloumn_buf.append(","+it.getItemid()+"=S."+it.getItemid());
		    	    				insert_cloumn_buf.append(","+it.getItemid()+"0=1"); //20140905 dengcan  增加子集必须有数据才校验条件
		    	    				cloumnStr+=","+it.getItemid();
		    	    			}
		    	    			boolean addflag=this.isAddColumn(this.getOnlyField(), cloumnStr);
		    	    			if(addflag)
		    	    			{
		    	    				insert_cloumn_buf.append(","+this.getOnlyField()+"=S."+this.getOnlyField());
		    	    			}
		    	    			insert_cloumn_buf.append(" FROM "+tempTableName+" LEFT JOIN "+pre+key+" S ON "+tempTableName+".a0100=S.a0100 WHERE ");
		    	    			insert_cloumn_buf.append(" UPPER("+tempTableName+".dbname)='"+pre.toUpperCase()+"'");
		    				}
		    				else
		    				{
		    					String cloumnStr="a0100,b0100,e0122,a0101";
		    					insert_cloumn_buf.append("UPDATE "+tempTableName+" e set (e.a0000,e.b0110,e.e0122,e.a0101");
		    					select_cloumn_buf.append(" K.a0000,K.b0110,K.e0122,K.a0101");
		    					for(int j=0;j<itemList.size();j++)
		    	    			{
		    	    				FieldItem it=(FieldItem)itemList.get(j);
		    	    				insert_cloumn_buf.append(",e."+it.getItemid()+",e."+it.getItemid()+"0"); //20140905 dengcan  增加子集必须有数据才校验条件
		    	    				select_cloumn_buf.append(",K."+it.getItemid()+",1");
		    	    				cloumnStr+=","+it.getItemid();
		    	    			}
		    					boolean addflag=this.isAddColumn(this.getOnlyField(), cloumnStr);
		    					if(addflag)
		    						insert_cloumn_buf.append(",e."+this.getOnlyField());
		    					insert_cloumn_buf.append(")=(select "+select_cloumn_buf.toString());
		    					if(addflag)
		    						insert_cloumn_buf.append(",K."+this.getOnlyField());
		    					insert_cloumn_buf.append(" from "+pre+key+" K where ");
		    					insert_cloumn_buf.append(" K.a0100=e.a0100 and UPPER(e.dbname)='"+pre.toUpperCase()+"')");
		    					insert_cloumn_buf.append(" where exists (");
		    					insert_cloumn_buf.append("select null from "+pre+key+" K where ");
		    					insert_cloumn_buf.append(" K.a0100=e.a0100 and UPPER(e.dbname)='"+pre.toUpperCase()+"')");
		    					
		    				}
		    				
		    			}
		    			else
		    			{
		    				deleteWhereBuffer.append(" and "+pre+"A01.a0100="+pre+key+".a0100 ");
		    				deleteFormBuffer.append(","+pre+key);
		    				if(Sql_switcher.searchDbServer()==Constant.MSSQL)
		    				{
		    	    			insert_cloumn_buf.append("UPDATE "+tempTableName+" SET ");
		    	    			for(int j=0;j<itemList.size();j++)
		    	    			{
		    	    				FieldItem it=(FieldItem)itemList.get(j);
		    	    				if(j!=0)
		    	    					insert_cloumn_buf.append(",");
		    	    				insert_cloumn_buf.append(it.getItemid()+"=S."+it.getItemid());
		    	    				insert_cloumn_buf.append(","+it.getItemid()+"0=1"); //20140905 dengcan  增加子集必须有数据才校验条件
		    	    			}
		    	    			insert_cloumn_buf.append(" FROM "+tempTableName+" LEFT JOIN ");
		    	    			insert_cloumn_buf.append("(select a.* from "+pre+key+" a,(select max(a0100) as a0100");
		    	    			insert_cloumn_buf.append(",max(i9999) as i9999 from "+pre+key+" group by a0100) b ");
		    	    			insert_cloumn_buf.append(" where a.a0100=b.a0100 and a.i9999=b.i9999");
		    	    			insert_cloumn_buf.append(") S ON "+tempTableName+".a0100=S.a0100 WHERE ");
		    	    			insert_cloumn_buf.append(" UPPER("+tempTableName+".dbname)='"+pre.toUpperCase()+"'");
		    				}
		    				else
		    				{
		    					insert_cloumn_buf.append("UPDATE "+tempTableName+" e set (");
		    					for(int j=0;j<itemList.size();j++)
		    	    			{
		    	    				FieldItem it=(FieldItem)itemList.get(j);
		    	    				if(j!=0)
		    	    				{
		    	    					insert_cloumn_buf.append(",");
		    	    					select_cloumn_buf.append(",");
		    	    				}
		    	    				insert_cloumn_buf.append("e."+it.getItemid()+","+"e."+it.getItemid()+"0"); //20140905 dengcan  增加子集必须有数据才校验条件
		    	    				select_cloumn_buf.append("K."+it.getItemid()+",1");
		    	    			}
		    					insert_cloumn_buf.append(")=(select "+select_cloumn_buf.toString()+" from ");
		    					insert_cloumn_buf.append(pre+key+" K where K.i9999=(select max(i9999) from "+pre+key+" ");
		    					insert_cloumn_buf.append(" b where K.a0100=b.a0100) and K.a0100=e.a0100 and UPPER(e.dbname)='"+pre.toUpperCase()+"')");
		    					insert_cloumn_buf.append(" where exists (");
		    					insert_cloumn_buf.append("select null from "+pre+key+" K where K.i9999=(select max(i9999) from "+pre+key+" ");
		    					insert_cloumn_buf.append(" b where K.a0100=b.a0100) and K.a0100=e.a0100 and UPPER(e.dbname)='"+pre.toUpperCase()+"')");
		    					
		    				}
		    			}
		    			dao.update(insert_cloumn_buf.toString());
		    		}
		    		/**删除子集中还没有记录（没发过工资的人）*/
		    	//	dao.delete(" delete from "+tempTableName+" where UPPER(dbname)='"+pre.toUpperCase()+"' and a0100 not in (select "+pre+"A01.a0100 "+deleteFormBuffer+deleteWhereBuffer+")", new ArrayList());
		    		insert_cloumn_buf.setLength(0);
	    			select_cloumn_buf.setLength(0);
	    			insert_sql_buf.setLength(0);
	    			deleteFormBuffer.setLength(0);
                    deleteWhereBuffer.setLength(0);
		    		/**选中指标没有主集指标，单独处理*/
		    		if(!isMainSet)
		    		{
		    			String cloumnStr="a0100,b0100,e0122,a0101";
		    			boolean addflag=this.isAddColumn(this.getOnlyField(), cloumnStr);
		    			if(Sql_switcher.searchDbServer()==Constant.MSSQL)
	    				{
	    	    			insert_cloumn_buf.append("UPDATE "+tempTableName+" SET a0000=S.a0000,b0110=S.b0110");
	    	    			insert_cloumn_buf.append(",e0122=S.e0122,a0101=S.a0101");
	    	    			if(addflag)
	    	    				insert_cloumn_buf.append(","+this.getOnlyField()+"=S."+this.getOnlyField());
	    	    			insert_cloumn_buf.append(" FROM "+tempTableName+" LEFT JOIN "+pre+"a01 S ON "+tempTableName+".a0100=S.a0100 WHERE ");
	    	    			insert_cloumn_buf.append(" UPPER("+tempTableName+".dbname)='"+pre.toUpperCase()+"'");
	    				}
	    				else
	    				{
	    					/* UPDATE TEMP_BdPeoples e set (e.A5805,e.E5807,e.E5809)
    					    =
    					    (
    					    select K.A5805,K.E5807,K.E5809 from
    					     UsrA58 K where K.i9999=(select max(i9999) from usra58 b where K.a0100=b.a0100 )  
    					     and K.A0100=e.A0100 and e.dbname='usr' )
    					       
    					       where exists
    					      (
    					      select null from
    					     UsrA58 K where K.i9999=(select max(i9999) from usra58 b where K.a0100=b.a0100 ) 
    					      and K.A0100=e.A0100 ) 
    					     */
	    					insert_cloumn_buf.append("UPDATE "+tempTableName+" e set (e.a0000,e.b0110,e.e0122,e.a0101");
	    					if(addflag)
	    						insert_cloumn_buf.append(",e."+this.getOnlyField());
	    					select_cloumn_buf.append(" K.a0000,K.b0110,K.e0122,K.a0101");
	    					if(addflag)
	    						select_cloumn_buf.append(",K."+this.getOnlyField());
	    					insert_cloumn_buf.append(")=(select "+select_cloumn_buf.toString()+" from ");
	    					insert_cloumn_buf.append(pre+"a01 K where e.a0100=K.a0100 and UPPER(e.dbname)='"+pre.toUpperCase()+"') ");
	    					insert_cloumn_buf.append(" where exists (");
	    					insert_cloumn_buf.append("select null from "+pre+"a01 K where e.a0100=K.a0100 ");
	    					insert_cloumn_buf.append(" and UPPER(e.dbname)='"+pre.toUpperCase()+"')");
	    				}
		    			dao.update(insert_cloumn_buf.toString());
		    		}
	    		}//dbarr for loop end 
				StringBuffer insertSQL = new StringBuffer();
				boolean addflag=this.isAddColumn(this.getOnlyField(), insert.toString());
				if(addflag)
					insert.append(","+this.getOnlyField());
				insertSQL.append(" insert into "+tablename+"("+insert.toString()+") select "+insert.toString());
				insertSQL.append(" from "+tempTableName+" where ("+where_buf.toString()+")");
				dao.update(insertSQL.toString());
				DbWizard dbw=new DbWizard(this.conn);
				Table table=new Table(tempTableName);
				dbw.dropTable(table);
			}
			/**如果没选择比对指标，按原来处理，只比单位部门姓名*/
			else
			{
	     		StringBuffer buf=new StringBuffer();
	    		for(int i=0;i<dbarr.length;i++)
	    		{
	         		String pre=dbarr[i];
		    		if(!this.userview.isSuper_admin()&&this.userview.getDbpriv().toString().toLowerCase().indexOf(","+pre.toLowerCase()+",")==-1)
		    			continue;
				
		    		String cloumnStr="dbname,a0100,a0000,b0110,e0122,a0101,b01101,e01221,a01011,state";
		    		boolean addflag=this.isAddColumn(this.getOnlyField(), cloumnStr);
		    		
		    		String srctable=pre+"A01";
		    		buf.setLength(0);
		    		buf.append("insert into ");
		    		buf.append(tablename);
		    		buf.append("(dbname,a0100,a0000,b0110,e0122,a0101,b01101,e01221,a01011,state");
		   		    if(addflag)
		   		    	buf.append(","+this.getOnlyField());
			    	buf.append(" )select distinct '");
		    		buf.append(pre);
			    	buf.append("' as dbname,");
			    	buf.append(srctable);
			    	buf.append(".A0100,");
			    	buf.append(srctable);
		    		buf.append(".A0000,");
		    		buf.append(srctable);
		    		buf.append(".B0110 ,");
		    		buf.append(srctable);
			    	buf.append(".E0122 as E0122,");
			    	buf.append(srctable);
			    	buf.append(".A0101 as A0101,");
			    	/**现在的是1，档案中的是指标名*/
			    	buf.append(this.gz_tablename);
			    	buf.append(".B0110 as B01101,");
			    	buf.append(this.gz_tablename);
			    	buf.append(".E0122 as E01221,");
			    	buf.append(this.gz_tablename);
		     		buf.append(".A0101 as A01011 ,");
		    		buf.append("'1' as state ");
		    		if(addflag)
		    			buf.append(","+srctable+"."+this.getOnlyField());
		    		buf.append(" from "+srctable);
		    		buf.append(" LEFT JOIN ");
		    		buf.append(this.gz_tablename);
			    	buf.append(" ON ");
			    	buf.append(srctable);
	    			buf.append(".A0100=");
		     		buf.append(this.gz_tablename);
		    		buf.append(".A0100 ");
		    		if(sb.toString().length()>0)
		    		{
		    			buf.append(sb.toString()+" and ");
		    		}
		    		else
		    			buf.append(" where ");
		    		buf.append(" upper(");
		    		buf.append(this.gz_tablename);
	    			buf.append(".NBASE)='");
	    			buf.append(pre.toUpperCase());
	     			buf.append("' and ");
		    		buf.append("((");
		    		buf.append(srctable);
		     		buf.append(".B0110<>");
		    		buf.append(this.gz_tablename);
		    		buf.append(".B0110) OR (");
		    		/* 标识：4380 客户：薪资发放变动比对部门为空的人员信息变动有问题。 xiaoyun 2014-10-20 start */
		    		buf.append("nullif(");
		    		buf.append(srctable);
		     		//buf.append(".B0110 IS NOT NULL and ");
		    		buf.append(".B0110,'') IS NOT NULL and ");
		    		buf.append("nullif(");
	    			buf.append(this.gz_tablename);
		    		//buf.append(".B0110 IS NULL) OR  (");
	    			buf.append(".B0110,'') IS NULL) OR  (nullif(");
		    		buf.append(srctable);
		    		//buf.append(".B0110 IS NULL and ");
		    		buf.append(".B0110,'') IS NULL and nullif(");
			    	buf.append(this.gz_tablename);
			    	//buf.append(".B0110 IS NOT NULL) OR  ");				
			    	buf.append(".B0110,'') IS NOT NULL) OR  ");
		    		buf.append("(nullif(");
		    		buf.append(srctable);
		    		buf.append(".E0122,'')<>nullif(");
		    		buf.append(this.gz_tablename);
			    	buf.append(".E0122,'') OR nullif(");
		    		buf.append(srctable);
			    	buf.append(".E0122,'') IS NOT NULL and nullif(");
		    		buf.append(this.gz_tablename);
			    	buf.append(".E0122,'') IS NULL) OR  (nullif(");
		    		buf.append(srctable);
			    	buf.append(".E0122,'') IS NULL and nullif(");
		 	    	buf.append(this.gz_tablename);
			    	buf.append(".E0122,'') IS NOT NULL) OR  ");				
		    		/* 标识：4380 客户：薪资发放变动比对部门为空的人员信息变动有问题。 xiaoyun 2014-10-20 end */

		    		buf.append("(");
		     		buf.append(srctable);
		    		buf.append(".A0101<>");
		    		buf.append(this.gz_tablename);
			    	buf.append(".A0101) OR (");
			    	buf.append(srctable);
		    		buf.append(".A0101 IS NOT NULL and ");
		    		buf.append(this.gz_tablename);
		    		buf.append(".A0101 IS NULL) OR  (");
		    		buf.append(srctable);
		    		buf.append(".A0101 IS NULL and ");
		    		buf.append(this.gz_tablename);
		    		buf.append(".A0101 IS NOT NULL))");						
                
		    		dao.update(buf.toString());
	    		}//for i loop end.
			}	
			
			
			}
			_rowSet.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			tablename="";
		}
		return tablename;

	}
	/**
	 * 设置所有的记录有效状态标识
	 * @param chgtype add del chginf chgA01Z0
	 * @param flag =1选中 =0清空
	 */
	public void setAllRecordValid(String chgtype,String flag)throws GeneralException
	{
		String tablename="";
		try
		{
			if("add".equalsIgnoreCase(chgtype))
				tablename="t#"+this.userview.getUserName()+"_gz_Ins";
			if("del".equalsIgnoreCase(chgtype))
				tablename="t#"+this.userview.getUserName()+"_gz_Dec";
			if("chgA01Z0".equalsIgnoreCase(chgtype))
				tablename="t#"+this.userview.getUserName()+"_gz_Tf";
			if("chginfo".equalsIgnoreCase(chgtype))
				tablename="t#"+this.userview.getUserName()+"_gz_Bd";
			if("".equalsIgnoreCase(tablename))
				return;
			StringBuffer buf=new StringBuffer();
			buf.append("update ");
			buf.append(tablename);
			buf.append(" set state=?");
			ArrayList paralist=new ArrayList();
			paralist.add(flag);
			ContentDAO dao=new ContentDAO(this.conn);
			dao.update(buf.toString(),paralist);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	/**
	 * 设置记录状态,对单个记录进行状态设置
	 * @param chgtype
	 * @param flag
	 * @param dbname
	 * @param a0100
	 * @throws GeneralException
	 */
	public void setRecordValid(String chgtype,String flag,String dbname,String a0100)throws GeneralException
	{
		String tablename="";
		try
		{
			if("add".equalsIgnoreCase(chgtype))
				tablename="t#"+this.userview.getUserName()+"_gz_Ins";
			if("del".equalsIgnoreCase(chgtype))
				tablename="t#"+this.userview.getUserName()+"_gz_Dec";
			if("chgA01Z0".equalsIgnoreCase(chgtype))
				tablename="t#"+this.userview.getUserName()+"_gz_Tf";
			if("chginfo".equalsIgnoreCase(chgtype))
				tablename="t#"+this.userview.getUserName()+"_gz_Bd";
			if("".equalsIgnoreCase(tablename))
				return;
			StringBuffer buf=new StringBuffer();
			buf.append("update ");
			buf.append(tablename);
			buf.append(" set state=?");
			buf.append(" where upper(dbname)=? and a0100=?");
			ArrayList paralist=new ArrayList();
			paralist.add(flag);
			paralist.add(dbname.toUpperCase());
			paralist.add(a0100);
			ContentDAO dao=new ContentDAO(this.conn);
			dao.update(buf.toString(),paralist);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	/**
	 * 输入的业务日期是否进行过薪资发放
	 * @param date
	 * @return
	 */
	public HashMap dateIsExended(String date)
	{
		HashMap mp=new HashMap();
		String strYm=null;
		String strC="0";
		StringBuffer buf=new StringBuffer();
		buf.append("select max(A00Z2) A00Z2 from gz_extend_log");
		buf.append(" where sp_flag='06' and salaryid=");
		buf.append(this.salaryid);
		buf.append(" and ");
		buf.append(" upper(username)='");
		buf.append(this.userview.getUserName().toUpperCase());
		buf.append("'");
		buf.append(" and A00Z2=");
		buf.append(Sql_switcher.dateValue(date));		
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rset=dao.search(buf.toString());
			if(rset.next())
				strYm=PubFunc.FormatDate(rset.getDate("A00Z2"), "yyyy-MM-dd");
			if("".equalsIgnoreCase(strYm))
			{
				strYm=date;
				strC="0";
				//appendExtendLog(strYm, "1");//在发放日志增加一条记录
			}
			else
			{
				buf.setLength(0);
				buf.append("select max(A00Z3) A00Z3 from gz_extend_log");
				buf.append(" where salaryid=");
				buf.append(this.salaryid);
				buf.append(" and sp_flag='06' and");
				buf.append(" upper(username)='");
				buf.append(this.userview.getUserName().toUpperCase());
				buf.append("'");
				buf.append(" and A00Z2=");
				buf.append(Sql_switcher.dateValue(date));				
				rset=dao.search(buf.toString());
				if(rset.next())
					strC=rset.getString("A00Z3");
			}
			mp.put("ym",strYm);
			mp.put("count", strC);
			rset.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return mp;
	}
	
	/**
	 * 判断薪资发放记录表中是否有没提交的工资
	 * @return
	 */
	public boolean isSalaryPayed()
	{
		boolean flag=false;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String sql="select * from gz_extend_log where username='"+this.userview.getUserName()+"' and sp_flag<>'06' and salaryid="+this.salaryid;
			String userName=this.userview.getUserName();
			if(this.manager!=null&&this.manager.length()>0&&!this.manager.equals(this.userview.getUserName()))
			{
				sql="select * from gz_extend_log where username='"+this.manager+"' and sp_flag<>'06' and salaryid="+this.salaryid;
				userName=this.manager;
			}
			RowSet rowSet=dao.search(sql);
			if(rowSet.next())
			{
				flag=true;
			}
			rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
	
	/**
	 * 判断薪资发放记录表中是否有没提交的工资
	 * @return
	 */
	public boolean isSalaryPayed2()
	{
		boolean flag=false;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String sql="select * from gz_extend_log where username='"+this.userview.getUserName()+"' and sp_flag<>'06' and salaryid="+this.salaryid;
			String userName=this.userview.getUserName();
			if(this.manager!=null&&this.manager.length()>0&&!this.manager.equals(this.userview.getUserName()))
			{
				sql="select * from gz_extend_log where username='"+this.manager+"' and sp_flag<>'06' and salaryid="+this.salaryid;
				userName=this.manager;
			}
			RowSet rowSet=dao.search(sql);
			if(rowSet.next())
			{
				flag=true;
			}
			
			//如果 初始化没有记录
			rowSet=dao.search("select count(id) from gz_extend_log where username='"+userName+"' and salaryid="+this.salaryid);
			if(rowSet.next())
			{
				if(rowSet.getInt(1)==0)
					flag=true;
			}
			rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
	
	/**
	 * 取得当前次数的前一次发放日期和次数
	 * @param date
	 * @param count
	 * @return
	 */
	public LazyDynaBean getPreCountDate(String date,String count,ContentDAO dao)
	{
		LazyDynaBean abean=null;
		try
		{
			SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
			String[] temps=date.split("-");
			RowSet rowSet=null;
			StringBuffer sql=new StringBuffer("");
			boolean flag=false;
			
			sql.append("select * from gz_extend_log where username='"+this.userview.getUserName()+"' and salaryid="+this.salaryid);
			sql.append(" and (  ("+Sql_switcher.year("a00z2")+"="+temps[0]+" and "+Sql_switcher.month("a00z2")+"="+temps[1]+"  and a00z3<"+count+" ) ");
			sql.append(" or ( "+Sql_switcher.year("a00z2")+"="+temps[0]+" and "+Sql_switcher.month("a00z2")+"<"+temps[1]+" ) ");
			sql.append(" or ( "+Sql_switcher.year("a00z2")+"<"+temps[0]+" ) ) order by a00z2 desc,a00z3 desc ");
				
			rowSet=dao.search(sql.toString());
			if(rowSet.next())
			{
					abean=new LazyDynaBean();
					abean.set("ym",df.format(rowSet.getDate("a00z2")));
					abean.set("count",rowSet.getString("a00z3"));

			}
			
			
			if(abean!=null)
			{
				temps=((String)abean.get("ym")).split("-");
				count=(String)abean.get("count");
				StringBuffer buf=new StringBuffer("");
				buf.append("select count(a0100) from salaryhistory where lower(userflag)='"+this.userview.getUserName().toLowerCase()+"' and salaryid="+this.salaryid);
				buf.append(" and "+Sql_switcher.year("a00z2")+"="+temps[0]);
				buf.append(" and "+Sql_switcher.month("a00z2")+"="+temps[1]);
				buf.append(" and a00z3="+count);
				rowSet=dao.search(sql.toString());
				if(rowSet.next())
				{
					if(rowSet.getInt(1)==0)
						abean=null;
				}
			}
			
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return abean;
	}
	
	/**
	 * 创建新薪资表
	 * @param year 		年
	 * @param month		月
	 * @throws GeneralException
	 */
	public void createNewGzTable(String year,String month)throws GeneralException
	{
		
		
		StringBuffer buf=new StringBuffer();
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			/**归属日期及归属次数*/
			String date=year+"-"+month+"-1";
			String count="1";
			/** 判断薪资发放记录表中是否有没提交的工资 */
			if(isSalaryPayed())
			{
				throw GeneralExceptionHandler.Handle(new Exception("数据未提交,不能生成新的数据表!"));
			}
			
			/**给定的业务日期是否进行过薪资发放?*/
			HashMap map=dateIsExended(date);
			count=(String)map.get("count");
			int ncount=Integer.parseInt(count)+1;
			count=String.valueOf(ncount);
			/**当前处理的到的年月标识和次数*/
			currym=date;
			currcount=count;
			/**在薪资发放历史记录表中增加一条发放记录*/
			DbNameBo.appendExtendLog(this.userview.getUserName(),this.salaryid,currym, currcount,this.conn);
			
			
			/**第一步先清空当前表中的数据*/
		/*	buf.append("delete from ");
			buf.append(this.gz_tablename);
			dao.update(buf.toString());
			*/
			DbWizard dbw=new DbWizard(this.conn);
			Table table=new Table(gz_tablename);
			dbw.dropTable(table);
			createGzDataTable();
			DbSecurityImpl dbS = new DbSecurityImpl();
			boolean flag = dbS.encryptTableName(this.conn, gz_tablename);//创建临时表时需注册表
			/**第二步根据薪资类别适用范围，过滤人员范围以及生成薪资已停发人员*/
			 
			String  royalty_valid=ctrlparam.getValue(SalaryCtrlParamBo.ROYALTIES,"valid");
			if(royalty_valid!=null&& "1".equals(royalty_valid))
			{
				
				importAddManData_royalty(year,month);
				delNoConditionData2(gz_tablename);
			}
			else
			{
				LazyDynaBean preDateBean= getPreCountDate(currym,currcount,dao);
				if(preDateBean==null||(from_module!=null&& "monthPremium".equalsIgnoreCase(from_module))||(from_module!=null&& "noPreData".equalsIgnoreCase(from_module)))
				{
					createAddManTable();
					/**导入新增人员*/
					importAddManData(false);
					/**删除薪资停发人员*/
					removeA01Z0ManData();
				}
				else
				{
					createAddManTable(preDateBean);
					/**导入新增人员*/
					importAddManData(false);
				}
			 
			
			
				/**第三步设置A00Z0,A00Z1的值*/
				buf.setLength(0);
				buf.append("update ");
				buf.append(this.gz_tablename);
				buf.append(" set A00Z0=?,A00Z1=?");
				
				Date src_d=DateUtils.getDate(date,"yyyy-MM-dd");
				java.sql.Date d=new java.sql.Date(src_d.getTime());
				ArrayList paramList=new ArrayList();
				paramList.add(d);
				paramList.add(Integer.parseInt(count));
				dao.update(buf.toString(),paramList);
				
			/*	ArrayList paralist=new ArrayList();
				paralist.add(DateUtils.getDate(date,"yyyy-MM-dd"));
				paralist.add(new Integer(count));
				dao.update(buf.toString(), paralist);*/
				
				
				
				/**发放日期和发放次数*/
				buf.setLength(0);
				buf.append("update ");
				buf.append(this.gz_tablename);
				buf.append(" set A00Z2=A00Z0,A00Z3=A00Z1,sp_flag='");
				buf.append(SP_STATE_DRAFT);
				buf.append("'");
				if(this.manager.length()>0)
				{
					buf.append(",sp_flag2='01'");
				}
				dao.update(buf.toString());
				
				/** 如果发现历史表中有当月相同的次数，归属次数就自动加1 */
				buf.setLength(0);
				buf.append("update "+this.gz_tablename+" set a00z1=(select a00z1+1 from salaryhistory where  "+this.gz_tablename+".a0100=salaryhistory.a0100 ");
				buf.append(" and "+this.gz_tablename+".nbase=salaryhistory.nbase and "+this.gz_tablename+".a00z0=salaryhistory.a00z0 and "+this.gz_tablename+".a00z1=salaryhistory.a00z1 and salaryid="+this.salaryid);
				buf.append(" ) where exists (select null from salaryhistory where  "+this.gz_tablename+".a0100=salaryhistory.a0100 ");
				buf.append(" and "+this.gz_tablename+".nbase=salaryhistory.nbase and "+this.gz_tablename+".a00z0=salaryhistory.a00z0 and "+this.gz_tablename+".a00z1=salaryhistory.a00z1 and salaryid="+this.salaryid+"  ) ");
				dao.update(buf.toString());
				buf.setLength(0);
				buf.append("update "+this.gz_tablename+" set b0110_o=(select a0000 from organization where organization.codeitemid="+this.gz_tablename+".b0110 and organization.codesetid='UN' )");
				buf.append(" where exists (select null from organization where organization.codeitemid="+this.gz_tablename+".b0110 and organization.codesetid='UN' )");
				dao.update(buf.toString());
				buf.setLength(0);
				buf.append("update "+this.gz_tablename+" set e0122_o=(select a0000 from organization where organization.codeitemid="+this.gz_tablename+".e0122 and organization.codesetid='UM' )");
				buf.append(" where exists (select null from organization where organization.codeitemid="+this.gz_tablename+".e0122 and organization.codesetid='UM' )");
				dao.update(buf.toString());
				buf.setLength(0);
				buf.append("update "+this.gz_tablename+" set dbid=(select dbid from dbname where upper(dbname.pre)=upper("+this.gz_tablename+".nbase)  )");
				buf.append(" where exists (select null from dbname where upper(dbname.pre)=upper("+this.gz_tablename+".nbase) )");
				dao.update(buf.toString());
			}
			
			//写入薪资发放数据的映射表
			if (Sql_switcher.searchDbServer()==1){               
                dao.update("delete from salary_mapping where salaryid="+this.salaryid+" and USERFLAG='"+this.userview.getUserName().toLowerCase()+"'");
            }
			else {
			    dao.update("delete from salary_mapping where salaryid="+this.salaryid+" and lower(USERFLAG)='"+this.userview.getUserName().toLowerCase()+"'");
			}
			dao.update("insert into salary_mapping (a0100,nbase,a00z0,a00z1,salaryid,userflag) select a0100,nbase,a00z0,a00z1,"+this.salaryid+",'"+this.userview.getUserName().toLowerCase()+"' from "+this.gz_tablename);
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}		
	}
	/**
	 * 把新增、删除、信息变动、停发标识人员数据和
	 * 薪资表进行同步
	 */
	public void importDataIntoGzTable()throws GeneralException
	{
		try
		{
			/**新增人员*/
			importAddManData(true);
			/**薪资停发人员*/
			removeA01Z0ManData();
			/**不在档案库中的人员*/
		    removeDelManData();
			/**信息变动人员*/
			updateChgInfoManData();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	/**
	 * 插入发放记录
	 * 包括起草01，执行中05(审批中)，结束06三种状态
	 * @param ymd
	 * @count count
	 */
	private void appendExtendLog(String ymd,String count)
	{
		StringBuffer buf=new StringBuffer();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			/**先删除当前用户，当前薪资类别正在处于起草状态的记录*/
			buf.append("delete from gz_extend_log where salaryid=? and username=? and sp_flag='01'");
			ArrayList paralist=new ArrayList();
			paralist.add(String.valueOf(this.salaryid));
			paralist.add(this.userview.getUserName());
			dao.update(buf.toString(),paralist);
			/**增加一条起草状态的记录*/
			int maxid=DbNameBo.getPrimaryKey("gz_extend_log", "id", conn);
			RecordVo vo=new RecordVo("gz_extend_log");
			vo.setInt("id", maxid);
			vo.setString("username", this.userview.getUserName());
			vo.setString("sp_flag", "01");
			vo.setInt("salaryid", this.salaryid);
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
	 * 增加一条薪资发放记录,每个用户对于一个薪资类别仅
	 * 只有一个处于当前处理状态,也即起草状态。
	 * @throws GeneralException
	 */
	private void setExtendLogState()throws GeneralException
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer buf=new StringBuffer();
			/**
			 * =01 正在处理
			 * =05 执行状态
			 * =06 结束状态（薪资数据已提交）
			 */
			RowSet rowSet=dao.search("select count(*) from "+this.gz_tablename+" where ( sp_flag<>'06' or sp_flag is null )");
			int num=0;
			if(rowSet.next())
				num=rowSet.getInt(1);
			if(num==0)
			{ 
				SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
				rowSet=dao.search("select a00z2,a00z3 from "+this.gz_tablename);
				String ym="";
				String count="";
				if(rowSet.next())
				{
					if(rowSet.getDate("a00z2")!=null)
					{
						ym=df.format(rowSet.getDate("a00z2"));
						count=rowSet.getString("a00z3");
					}
				}
				if(ym.length()>0)
				{
					String[] temps=ym.split("-");
					String sql="select count(*) from gz_extend_log  where  salaryid="+this.salaryid+" and upper(username)='"+this.userview.getUserName().toUpperCase()+"' ";
					sql+=" and "+Sql_switcher.year("a00z2")+"="+temps[0]+" and "+Sql_switcher.month("a00z2")+"="+Integer.parseInt(temps[1])+" and a00z3="+count;
					rowSet=dao.search(sql);
					num=0;
					if(rowSet.next())
					{
						num=rowSet.getInt(1);
					}
					if(num==0)
					{ 
						DbNameBo.appendExtendLog(this.userview.getUserName(),this.salaryid,ym, count,this.conn); 
					}
				}
				buf.append("update gz_extend_log set sp_flag='06',isredo=0 where ( sp_flag='01' or sp_flag='05' ) and salaryid=? and upper(username)=?");
				ArrayList paralist=new ArrayList();
	
				paralist.add(String.valueOf(this.salaryid));
				paralist.add(this.userview.getUserName().toUpperCase());
				dao.update(buf.toString(),paralist);
			}
			
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);			
		}
	}
	
	
	/**
	 * 修改薪资发放记录状态。
	 * @throws GeneralException
	 */
	private void setExtendLogState(String state,String username,String ym,String count)throws GeneralException
	{
		try
		{ 
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer buf=new StringBuffer();
			buf.append("update gz_extend_log set sp_flag='"+state+"' where salaryid=");
			buf.append(this.salaryid);
			buf.append(" and ");
			buf.append(" upper(username)='");
			buf.append(username.toUpperCase());
			buf.append("'");			
			buf.append(" and A00Z3=");
			buf.append(count);
			buf.append(" and A00Z2=");
			buf.append(Sql_switcher.dateValue(ym));
			dao.update(buf.toString());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);			
		}
	}
	
	
	/**
	 * 修改薪资发放记录状态。
	 * @throws GeneralException
	 */
	private void setExtendLogState(String state,String ym,String count)throws GeneralException
	{
		try
		{ 
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer buf=new StringBuffer();
			buf.append("update gz_extend_log set sp_flag='"+state+"' where salaryid=");
			buf.append(this.salaryid);
			buf.append(" and ");
			buf.append(" upper(username)='");
			buf.append(this.userview.getUserName().toUpperCase());
			buf.append("'");			
			buf.append(" and A00Z3=");
			buf.append(count);
			buf.append(" and A00Z2=");
			buf.append(Sql_switcher.dateValue(ym));
			dao.update(buf.toString());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);			
		}
	}
	
	
	
	/**
	 * 取得当前薪资表处理的发放日期和次数
	 * 如果初次使用,历史记录表为空，当前处到的日期为系统日期
	 * 当前处理次数为1.
	 * 从返回HashMap 取得ym(业务日期),yyyy-MM-dd
	 *              取得count(发放次数),
	 * @param  dengcan
	 * @return
	 */
	public HashMap getYearMonthCount2()
	{
		HashMap mp=new HashMap();
		
		try
		{
			SalaryPkgBo pgkbo=new SalaryPkgBo(this.conn,this.userview,0);
			String username=this.userview.getUserName();
			if(this.manager!=null&&this.manager.length()>0)
				username=this.manager;
			LazyDynaBean abean=pgkbo.searchCurrentDate2(String.valueOf(this.salaryid),username);
			String strYm=(String)abean.get("strYm");
			String strC=(String)abean.get("strC");			
			if("".equalsIgnoreCase(strYm))
			{
				String appdate=ConstantParamter.getAppdate(this.userview.getUserName());
				if(appdate==null||appdate.trim().length()==0)
				{
					strYm=DateUtils.format(new Date(), "yyyy-MM-dd");
				}
				else
					strYm=appdate.replaceAll("\\.","-");
				
				String[] tmp=StringUtils.split(strYm, "-");
				strYm=tmp[0]+"-"+tmp[1]+"-01";
				strC="1";
			//	appendExtendLog(strYm,strC);
				DbNameBo.appendExtendLog(this.userview.getUserName(),this.salaryid,strYm,strC,this.conn);
			}
			mp.put("ym",strYm);
			mp.put("count", strC);
			/**当前处理的到的年月标识和次数*/
			currym=strYm;
			currcount=strC;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return mp;
	}
	
	/**
	 * 类似getYearMonthCount2()，区别：年月标识为空时，不往gz_extend_log加记录。
	 * @see #getYearMonthCount2()
	 */
	public boolean getYearMonthCount3()
	{
		boolean result=false;
		try
		{
			SalaryPkgBo pgkbo=new SalaryPkgBo(this.conn,this.userview,0);
			String username=this.userview.getUserName();
			if(this.manager!=null&&this.manager.length()>0)
				username=this.manager;
			LazyDynaBean abean=pgkbo.searchCurrentDate2(String.valueOf(this.salaryid),username);
			String strYm=(String)abean.get("strYm");
			String strC=(String)abean.get("strC");			
			if(strYm.length()>0)
			{
				/**当前处理的到的年月标识和次数*/
				currym=strYm;
				currcount=strC;
				result=true;
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return result;
	}		
	
	/**
	 * 从薪资发放历史记录表取得当前处理到的发放日期和次数
	 * 如果初次使用,历史记录表为空，当前处到的日期为系统日期
	 * 当前处理次数为1.
	 * @param flag=01表示正在处理的，06最后结束状态
	 * 从返回HashMap 取得ym(业务日期),yyyy-MM-dd
	 *              取得count(发放次数),
	 * @return
	 */
	public HashMap getYearMonthCount(String state)
	{
		HashMap mp=new HashMap();
		String strYm=null;
		String strC=null;
		StringBuffer buf=new StringBuffer();
		buf.append("select max(A00Z2) A00Z2 from gz_extend_log");
		buf.append(" where salaryid=");
		buf.append(this.salaryid);
		buf.append(" and ");
		buf.append(" upper(username)='");
		buf.append(this.userview.getUserName().toUpperCase());
		buf.append("'");
		/**结束的*/
		buf.append(" and sp_flag='");
		buf.append(state);
		buf.append("'");
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
				strYm=tmp[0]+"-"+tmp[1]+"-01";
				strC="1";
				//appendExtendLog(strYm,strC);
				DbNameBo.appendExtendLog(this.userview.getUserName(),this.salaryid,strYm,strC,this.conn);
			}
			else
			{
				buf.setLength(0);
				buf.append("select max(A00Z3) A00Z3 from gz_extend_log");
				buf.append(" where salaryid=");
				buf.append(this.salaryid);
				buf.append(" and ");
				buf.append(" upper(username)='");
				buf.append(this.userview.getUserName().toUpperCase());
				buf.append("'");
				buf.append(" and A00Z2=");
				buf.append(Sql_switcher.dateValue(strYm));
				/**结束的*/
				buf.append(" and sp_flag='");
				buf.append(state);
				buf.append("'");				
				
				rset=dao.search(buf.toString());
				if(rset.next()){
					strC=rset.getString("A00Z3");
					strC=strC!=null&&strC.trim().length()>0?strC:"1";
				}
			}
			rset.close();
			mp.put("ym",strYm);
			mp.put("count", strC);
			/**当前处理的到的年月标识和次数*/
			currym=strYm;
			currcount=strC;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return mp;
	}
	
	public String getMaxYearMonth()
	{
		String strYm="";
		StringBuffer buf=new StringBuffer();
		buf.append("select max(A00Z2) A00Z2 from gz_extend_log");
		buf.append(" where salaryid=");
		buf.append(this.salaryid);
		buf.append(" and ");
		buf.append(" upper(username)='");
		buf.append(this.userview.getUserName().toUpperCase());
		buf.append("'");

		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rset=dao.search(buf.toString());
			if(rset.next())
				strYm=PubFunc.FormatDate(rset.getDate("A00Z2"), "yyyy-MM");
			rset.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return strYm;
	}
	
	
	/**
	 * 求当前处理到的最大业务日期和次数
	 * @param state
	 * @return
	 */
	public HashMap getMaxYearMonthCount()
	{
		
		HashMap mp=new HashMap();
		String strYm=null;
		String strC=null;
		StringBuffer buf=new StringBuffer();
		buf.append("select max(A00Z2) A00Z2 from gz_extend_log");
		buf.append(" where salaryid=");
		buf.append(this.salaryid);
		buf.append(" and ");
		buf.append(" upper(username)='");
		if(this.manager.length()==0)
			buf.append(this.userview.getUserName().toUpperCase());
		else
			buf.append(this.manager.toUpperCase());
		buf.append("'");

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
				strYm=tmp[0]+"-"+tmp[1]+"-01";
				strC="1";
				/** 在新建薪资发放时会在历史记录表中增加一条发放记录,并判断是否有未发放的薪资,所以此处注销 ----dengcan */
			//	appendExtendLog(strYm,strC);
			}
			else
			{
				buf.setLength(0);
				buf.append("select max(A00Z3) A00Z3 from gz_extend_log");
				buf.append(" where salaryid=");
				buf.append(this.salaryid);
				buf.append(" and ");
				buf.append(" upper(username)='");
				if(this.manager.length()==0)
					buf.append(this.userview.getUserName().toUpperCase());
				else
					buf.append(this.manager.toUpperCase());
				buf.append("'");
				buf.append(" and A00Z2=");
				buf.append(Sql_switcher.dateValue(strYm));
				
				rset=dao.search(buf.toString());
				if(rset.next())
					strC=rset.getString("A00Z3");
			}
			rset.close();
			mp.put("ym",strYm);
			mp.put("count", strC);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return mp;
	}
	
	
	/**
	 * 求当前处理到的最大业务日期和次数
	 * @param state
	 * @return
	 */
	public HashMap getMaxYearMonthCount(String username)
	{
		
		HashMap mp=new HashMap();
		String strYm=null;
		String strC=null;
		StringBuffer buf=new StringBuffer();
		buf.append("select max(A00Z2) A00Z2 from gz_extend_log");
		buf.append(" where salaryid=");
		buf.append(this.salaryid);
		buf.append(" and ");
		buf.append(" upper(username)='");
		buf.append(username.toUpperCase());
		buf.append("'");

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
				strYm=tmp[0]+"-"+tmp[1]+"-01";
				strC="1";
				/** 在新建薪资发放时会在历史记录表中增加一条发放记录,并判断是否有未发放的薪资,所以此处注销 ----dengcan */
			//	appendExtendLog(strYm,strC);
			}
			else
			{
				buf.setLength(0);
				buf.append("select max(A00Z3) A00Z3 from gz_extend_log");
				buf.append(" where salaryid=");
				buf.append(this.salaryid);
				buf.append(" and ");
				buf.append(" upper(username)='");
				buf.append(username.toUpperCase());
				buf.append("'");
				buf.append(" and A00Z2=");
				buf.append(Sql_switcher.dateValue(strYm));
				rset=dao.search(buf.toString());
				if(rset.next())
					strC=rset.getString("A00Z3");
			}
			rset.close();
			mp.put("ym",strYm);
			mp.put("count", strC);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return mp;
	}
	
	
	
	
	/**
	 * 求当前薪资类别处理的业务日期和次数
	 * @return
	 */
	private HashMap getCurrentGzTableYearMonthCount()
	{
		HashMap mp=new HashMap();
		String strYm="";
		String strC="";
		StringBuffer buf=new StringBuffer();
		buf.append("select A00Z2, A00Z3 from ");
		buf.append(this.gz_tablename);
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rset=dao.search(buf.toString());
			if(rset.next())
			{
				strYm=PubFunc.FormatDate(rset.getDate("A00Z2"), "yyyy-MM-dd");
				strC=rset.getString("A00Z3");
			}
			if("".equalsIgnoreCase(strYm))
			{
				strYm=DateUtils.format(new Date(), "yyyy-MM-dd");
				strC="1";
			}
			rset.close();
			mp.put("ym",strYm);
			mp.put("count", strC);
			/**当前处理的到的年月标识和次数*/
			currym=strYm;
			currcount=strC;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return mp;
	}
	
	/**
	 * 求得当前薪资类别处理到的年月次数,老版本
	 * @param tablename 薪资表或薪资历史表
	 * @return 返回HashMap ,包含年月和次数
	 */
	/*
	private HashMap getYearMonthCount(String tablename)
	{
		HashMap mp=new HashMap();
		String strYm=null;
		String strC=null;
		StringBuffer buf=new StringBuffer();
		buf.append("select max(A00Z2) A00Z2 from ");
		buf.append(tablename);
		if(tablename.equalsIgnoreCase("salaryhistory"))
		{
			buf.append(" where salaryid=");
			buf.append(this.salaryid);
			buf.append(" and ");
			buf.append(" upper(userflag)='");
			buf.append(this.userview.getUserName().toUpperCase());
			buf.append("'");
		}
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rset=dao.search(buf.toString());
			if(rset.next())
				strYm=PubFunc.FormatDate(rset.getDate("A00Z2"), "yyyy-MM-dd");
			if(strYm.equalsIgnoreCase(""))
			{
				strYm=DateUtils.format(new Date(), "yyyy-MM-dd");
				strC="1";
			}
			else
			{
				buf.setLength(0);
				buf.append("select max(A00Z3) A00Z3 from ");
				buf.append(tablename);
				if(tablename.equalsIgnoreCase("salaryhistory"))
				{
					buf.append(" where salaryid=");
					buf.append(this.salaryid);
					buf.append(" and ");
					buf.append(" upper(userflag)='");
					buf.append(this.userview.getUserName().toUpperCase());
					buf.append("'");
				}
				rset=dao.search(buf.toString());
				if(rset.next())
					strC=rset.getString("A00Z3");
			}
			mp.put("ym",strYm);
			mp.put("count", strC);
			currym=strYm;
			currcount=strC;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return mp;
	}
	*/
	
	/**
	 * 求得更新串
	 * @param strTable
	 * @param strDest
	 * @param setid
	 * @return
	 */
	private String getUpdateFieldSQL(String strSrc,String strDest,String setid)
	{
		StringBuffer buf=new StringBuffer();
		for(int i=0;i<this.gzitemlist.size();i++)
		{
			GzItemVo itemvo=(GzItemVo)gzitemlist.get(i);
			if(!(setid.equalsIgnoreCase(itemvo.getSetname())))
				continue;
			String strfld=itemvo.getFldname();
			String strhz=itemvo.getHz();
			String strformula=itemvo.getFormula();			
			/**导入项，仅导入当前*/
			if(itemvo.getInitflag()==2)
			{
				/**未定义计算公式*/
				if(strfld.equalsIgnoreCase(strformula)||strhz.equalsIgnoreCase(strformula))
				{
					buf.append(strDest);
					buf.append(".");
					buf.append(strfld);
					buf.append("=");
					buf.append(strSrc);
					buf.append(".");					
					buf.append(strfld);
					buf.append("`");
				}
			}
			/**执行工资标准项*/
			if(itemvo.getInitflag()==4)
			{
				buf.append(strDest);
				buf.append(".");
				buf.append(strfld);
				buf.append("=");
				buf.append(strSrc);
				buf.append(strfld);
				buf.append("`");				
			}
		}//for i loop end.
		if(buf.length()>0)
			buf.setLength(buf.length()-1);
		return buf.toString();
	}
	/**
	 * 求插入更新串列表
	 * @param strname
	 * @return
	 */
	private String getInsFieldSQL(String setid)
	{
		StringBuffer buf=new StringBuffer();
		for(int i=0;i<gzitemlist.size();i++)
		{
			GzItemVo itemvo=(GzItemVo)gzitemlist.get(i);
			if(!(setid.equalsIgnoreCase(itemvo.getSetname())))
				continue;
			if(itemvo.getInitflag()==0)
				continue;
			buf.append(itemvo.getFldname());
			buf.append(",");
		}
		if(buf.length()>0)
			buf.setLength(buf.length()-1);
		return buf.toString();
	}
	
	/**
	 * 求插入更新串列表
	 * @param strname
	 * @return
	 */
	private String getInsFieldSQL(String setid,String pay_flag)
	{
		StringBuffer buf=new StringBuffer();
		for(int i=0;i<gzitemlist.size();i++)
		{
			GzItemVo itemvo=(GzItemVo)gzitemlist.get(i);
			if(!(setid.equalsIgnoreCase(itemvo.getSetname())))
				continue;
			if(itemvo.getInitflag()==0)
				continue;
			if(pay_flag!=null&&pay_flag.equalsIgnoreCase(itemvo.getFldname()))
				continue;
			buf.append(itemvo.getFldname());
			buf.append(",");
		}
		if(buf.length()>0)
			buf.setLength(buf.length()-1);
		return buf.toString();
	}
	
	
	/**
	 * 取得更新指标列表
	 * @param sets
	 * @return
	 */
	public ArrayList getUpdateItemList(String[] sets)
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer whl=new StringBuffer("");
			for(int i=0;i<sets.length;i++)
			{
				whl.append(",'"+sets[i]+"'");
			}
			String sql="select * from salaryset where fieldsetid in ("+whl.substring(1)+") and itemtype='N' and itemid!='A0000' and itemid!='A00Z1'  and salaryid="+this.salaryid+" order by sortid";
			RowSet rowset=dao.search(sql);
			LazyDynaBean abean=null;
			
			SalaryLProgramBo lpbo=new SalaryLProgramBo(this.templatevo.getString("lprogram"));
			String  str=lpbo.getValue(SalaryLProgramBo.CONFIRM_TYPE).toLowerCase();

			while(rowset.next())
			{
				abean=new LazyDynaBean();
				abean.set("itemid",rowset.getString("itemid"));
				abean.set("itemdesc",rowset.getString("itemdesc"));
				if(str.indexOf(";"+rowset.getString("itemid").toLowerCase()+";")==-1)
					abean.set("flag","1");
				else
					abean.set("flag","0");
				list.add(abean);
			}
			rowset.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	
	public String getLprogramAttri(String attriName,int nodeValue)
	{
		String value="";
		SalaryLProgramBo lpbo=new SalaryLProgramBo(this.templatevo.getString("lprogram"));
		value=lpbo.getValue(nodeValue,attriName);
		return value;
	}
	
	
	/**
	 * 求薪资数据各子集提交方式,数据提交至档案库USRAXX
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getSubmitTypeList()throws GeneralException
	{
		ArrayList list=new ArrayList();
		try
		{
			StringBuffer buf=new StringBuffer();
			for(int i=0;i<gzitemlist.size();i++)
			{
				GzItemVo itemvo=(GzItemVo)gzitemlist.get(i);
				String setid=itemvo.getSetname();
				if(setid.charAt(0)!='A')
					continue;
				if("A00".equalsIgnoreCase(setid))
					continue;
				if(buf.indexOf(setid)==-1)
				{
					buf.append(setid);
					buf.append(",");
				}
			}//for i loop end.
			SalaryLProgramBo lpbo=new SalaryLProgramBo(this.templatevo.getString("lprogram"));
			HashMap map=lpbo.getSubmitMap();
			String[] seta=StringUtils.split(buf.toString(),",");
			
			for(int i=0;i<seta.length;i++)
			{
				String setid=seta[i];
				FieldSet fieldset=DataDictionary.getFieldSetVo(setid);
				if(fieldset==null)
					continue;
				if("0".equalsIgnoreCase(fieldset.getUseflag()))
					continue;
				LazyDynaBean dynabean=new LazyDynaBean();
				dynabean.set("setid", setid);
				dynabean.set("name", fieldset.getCustomdesc());
				String type=(String)map.get(setid);
				if(type==null||type.length()==0)
				{
					if("0".equals(fieldset.getChangeflag()))
					{
						dynabean.set("type", "2");
					}
					else
						dynabean.set("type", "1");
				}
				else
					dynabean.set("type", type);
				list.add(dynabean);
			}//for i loop end.
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);			
		}
		return list;		
	}
	
	
	/**
	 * 取得当前薪资类别计算公式列表
	 * @param flag =1 (有效计算公式) =-1全部的计算公式
	 * 
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getFormulaList(ArrayList itemids)throws GeneralException
	{
		ArrayList list=new ArrayList();
		StringBuffer buf=new StringBuffer();
		RowSet rset=null;
		try
		{
			if(itemids.size()>0)
			{
				buf.append("select hzName,itemname,useflag,itemid,rexpr,cond,standid,itemtype,runflag from salaryformula  where salaryid=");
				buf.append(this.salaryid);
				
				StringBuffer str=new StringBuffer("");
				for(int i=0;i<itemids.size();i++)
				{
					str.append(","+(String)itemids.get(i));
				}
				buf.append(" and itemid in ("+str.substring(1)+")");
				
				buf.append(" order by salaryid,sortid");
				ContentDAO dao=new ContentDAO(this.conn);
				rset=dao.search(buf.toString());
				list=dao.getDynaBeanList(rset);
				if(rset!=null)
					rset.close();
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
				if(rset!=null)
					rset.close();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}			
		return list;
	}
	
	
	
	/**
	 * 取得当前薪资类别计算公式列表
	 * @param flag =1 (有效计算公式) =-1全部的计算公式
	 * 
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getFormulaList(int flag)throws GeneralException
	{
		ArrayList list=new ArrayList();
		StringBuffer buf=new StringBuffer();
		try
		{
			buf.append("select hzName,itemname,useflag,itemid,rexpr,cond,standid,itemtype,runflag from salaryformula  where salaryid=");
			buf.append(this.salaryid);
			/**过滤有效的计算公式*/
			if(flag==1)
				buf.append(" and useflag=1");
			buf.append(" order by salaryid,sortid");
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rset=dao.search(buf.toString());
			list=dao.getDynaBeanList(rset);
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
	 * 查询薪资类别中的指标列表
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getGzFieldList()throws GeneralException
	{
		ArrayList fieldlist=new ArrayList();
		RowSet rset=null;
		try
		{
			StringBuffer buf=new StringBuffer();
			buf.append("select * from salaryset where salaryid=");
			buf.append(this.salaryid);
			buf.append(" order by sortid");
			ContentDAO dao=new ContentDAO(this.conn);
			rset=dao.search(buf.toString());
			while(rset.next())
			{
				FieldItem item=new FieldItem();
				item.setFieldsetid(rset.getString("fieldsetid"));
				item.setItemid(rset.getString("itemid"));
				item.setItemdesc(rset.getString("itemdesc"));
				item.setItemtype(rset.getString("itemtype"));
				item.setItemlength(rset.getInt("itemlength"));
				item.setDisplaywidth(rset.getInt("nwidth"));
				item.setDecimalwidth(rset.getInt("decwidth"));
				item.setCodesetid(rset.getString("codesetid"));
				item.setFormula(Sql_switcher.readMemo(rset,"formula"));
				item.setVarible(0);
				fieldlist.add(item);
			}//while loop end.
			
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
				if(rset!=null)
					rset.close();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}			
		return fieldlist;
	}
	
	
	
	/**
	 * 从临时变量中取得对应指标列表
	 * @return FieldItem对象列表
	 * @throws GeneralException
	 */
	private ArrayList getMidVariableList2()throws GeneralException
	{
		ArrayList fieldlist=new ArrayList();
		ArrayList new_fieldList=new ArrayList();
		RowSet rset=null;
		try
		{
			StringBuffer buf=new StringBuffer();
			buf.append("select cname,chz,ntype,cvalue,fldlen,flddec,codesetid from ");
			buf.append(" midvariable where nflag=0 and templetid=0 ");
			buf.append(" and (cstate is null or cstate='");
			buf.append(this.salaryid);
			buf.append("') order by sorting");
			ContentDAO dao=new ContentDAO(this.conn);
			rset=dao.search(buf.toString());
			while(rset.next())
			{
				FieldItem item=new FieldItem();
				item.setItemid(rset.getString("cname"));
				item.setFieldsetid(/*"A01"*/"");//没有实际含义
				item.setItemdesc(rset.getString("chz"));
				item.setItemlength(rset.getInt("fldlen"));
				item.setDecimalwidth(rset.getInt("flddec"));
				item.setFormula(Sql_switcher.readMemo(rset, "cvalue"));
				item.setCodesetid(rset.getString("codesetid"));
				switch(rset.getInt("ntype"))
				{
				case 1://
					item.setItemtype("N");
					break;
				case 2:
				case 4://代码型					
					item.setItemtype("A");
					break;
				case 3:
					item.setItemtype("D");
					break;
				}
				item.setVarible(1);
				fieldlist.add(item);
			}// while loop end.
			
			
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
				if(rset!=null)
					rset.close();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}			
		return fieldlist;

	}
	
	
	
	
	/**
	 * 从临时变量中取得对应指标列表
	 * @return FieldItem对象列表
	 * @throws GeneralException
	 */
	public ArrayList getMidVariableList()throws GeneralException
	{
		ArrayList fieldlist=new ArrayList();
		ArrayList new_fieldList=new ArrayList();
		RowSet rset=null;
		try
		{
			StringBuffer buf=new StringBuffer();
			buf.append("select cname,chz,ntype,cvalue,fldlen,flddec,codesetid from ");
			buf.append(" midvariable where nflag=0 and templetid=0 ");
			buf.append(" and (cstate is null or cstate='");
			buf.append(this.salaryid);
			buf.append("') order by sorting");
			ContentDAO dao=new ContentDAO(this.conn);
			rset=dao.search(buf.toString());
			while(rset.next())
			{
				FieldItem item=new FieldItem();
				item.setItemid(rset.getString("cname"));
				item.setFieldsetid(/*"A01"*/"");//没有实际含义
				item.setItemdesc(rset.getString("chz"));
				item.setItemlength(rset.getInt("fldlen"));
				item.setDecimalwidth(rset.getInt("flddec"));
				item.setFormula(Sql_switcher.readMemo(rset, "cvalue"));
				item.setCodesetid(rset.getString("codesetid"));
				switch(rset.getInt("ntype"))
				{
				case 1://
					item.setItemtype("N");
					break;
				case 2:
				case 4://代码型					
					item.setItemtype("A");
					break;
				case 3:
					item.setItemtype("D");
					break;
				}
				item.setVarible(1);
				fieldlist.add(item);
			}// while loop end.
			
			HashMap varMap = new HashMap();
			//过滤薪资类别  计算公式用不到的临时变量
			ArrayList formulaList=getFormulaList(1);
			FieldItem item=null;
			HashMap map=new HashMap();
			for(int i=0;i<formulaList.size();i++)
			{
				  DynaBean dbean=(LazyDynaBean)formulaList.get(i);
	              String formula=((String)dbean.get("rexpr")).toLowerCase();
	              String cond=((String)dbean.get("cond")).toLowerCase();
	              for(int j=0;j<fieldlist.size();j++)
	              {
	            	  item=(FieldItem)fieldlist.get(j);
	            	  String item_id=item.getItemid(); //.toLowerCase();
	            	  if(item.getItemdesc()==null)
	            		  continue;
	            	  String item_desc=item.getItemdesc().trim().toLowerCase();
	            	  if(formula.indexOf(item_desc)!=-1&&map.get(item_id)==null)
	            	  {
	            		 // new_fieldList.add(item);
	            		  varMap.put(item_id, "1");
	            		  searchVar(fieldlist,item.getFormula(),varMap);
	            		  map.put(item_id, "1");
	            	  }
	            	  
	            	  if(cond!=null&&cond.trim().length()>0)
	            	  {
		            	  if(cond.indexOf(item_desc)!=-1&&map.get(item_id)==null)
		            	  {
		            		 // new_fieldList.add(item);
		            		  varMap.put(item_id, "1");
		            		  searchVar(fieldlist,item.getFormula(),varMap);
		            		  map.put(item_id, "1");
		            	  }
	            	  }
	              }
			}
			
			
			
			if (varMap.size() > 0) {
				Set keySet = varMap.keySet();
				StringBuffer _str = new StringBuffer("");
				for (Iterator t = keySet.iterator(); t.hasNext();) {
					_str.append(",'" + (String) t.next() + "'");
				}
				
				buf.setLength(0);
				buf.append("select cname,chz,ntype,cvalue,fldlen,flddec,codesetid from ");
				buf.append(" midvariable where nflag=0 and templetid=0 ");
				buf.append(" and (cstate is null or cstate='");
				buf.append(this.salaryid);
				buf.append("')   and cname in ("+ _str.substring(1) + ")  order by sorting");
				
				rset = dao.search(buf.toString());
				while (rset.next()) {
					item = new FieldItem();
					item.setItemid(rset.getString("cname"));
					item.setFieldsetid("");// 没有实际含义
					item.setItemdesc(rset.getString("chz"));
					item.setItemlength(rset.getInt("fldlen"));
					item.setDecimalwidth(rset.getInt("flddec"));
					item.setFormula(Sql_switcher.readMemo(rset, "cvalue"));
					item.setCodesetid(rset.getString("codesetid"));
					switch (rset.getInt("ntype")) {
					case 1://
						item.setItemtype("N");
						break;
					case 2:
					case 4:// 代码型
						item.setItemtype("A");
						break;
					case 3:
						item.setItemtype("D");
						break;
					}
					item.setVarible(1);
					new_fieldList.add(item);
				}
			}
			
			
			
			/*
			
			HashSet set=new HashSet();
			for(int i=0;i<new_fieldList.size();i++)
			{
				 item=(FieldItem)new_fieldList.get(i);
				 String formula= item.getFormula();
				 if(formula.indexOf("取自于")!=-1)
				 {
					 int from= formula.indexOf("(");
					 int to= formula.indexOf(")");
					 set.add(formula.substring(from+1,to).trim());
				 }
			}
			StringBuffer ss=new StringBuffer("");
			for(Iterator t=set.iterator();t.hasNext();)
			{
				ss.append(",'"+((String)t.next()).toLowerCase()+"'");
			}
			
			if(ss.length()>0)
			{
				buf.setLength(0);
				buf.append("select cname,chz,ntype,cvalue,fldlen,flddec,codesetid from ");
				buf.append(" midvariable where nflag=0 and templetid=0 and lower(chz) in ("+ss.substring(1)+")");
				rset=dao.search(buf.toString());
				while(rset.next())
				{
					item=new FieldItem();
					item.setItemid(rset.getString("cname"));
					item.setFieldsetid("");//没有实际含义
					item.setItemdesc(rset.getString("chz"));
					item.setItemlength(rset.getInt("fldlen"));
					item.setDecimalwidth(rset.getInt("flddec"));
					item.setFormula(Sql_switcher.readMemo(rset, "cvalue"));
					item.setCodesetid(rset.getString("codesetid"));
					switch(rset.getInt("ntype"))
					{
					case 1://
						item.setItemtype("N");
						break;
					case 2:
					case 4://代码型					
						item.setItemtype("A");
						break;
					case 3:
						item.setItemtype("D");
						break;
					}
					item.setVarible(1);
					new_fieldList.add(0,item);
				}
			}
			
			
			*/
			
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
				if(rset!=null)
					rset.close();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}			
		//return fieldlist;
		return new_fieldList;
	}
	
	
	public void searchVar(ArrayList midList, String formualr_str, HashMap varMap) {
		FieldItem item;
		for (int j = 0; j < midList.size(); j++) {
			item = (FieldItem) midList.get(j);
			String item_id = item.getItemid();//.toLowerCase();
			String item_desc = item.getItemdesc().trim().toLowerCase();
			String formula = item.getFormula();
			if (formualr_str.toLowerCase().indexOf(item_desc) != -1
					&& varMap.get(item_id) == null) {
				varMap.put(item_id, "1");
				searchVar(midList, formula, varMap);

			}

		}

	}
	
	
	public static void main(String[] arg)
	{
		
		System.out.println("111122");
		String ss="取自于( yk3 )";
		int from= ss.indexOf("(");
		int to= ss.indexOf(")");
		System.out.println(ss.substring(from+1,to).trim());
		
	}
	
	
	
	
	/**
	 * 取得 引入 单位\部门 变动人员 下拉列表信息
	 * @param context
	 * @return
	 */
	public ArrayList getBelowItemList(String codesetid)
	{
		ArrayList list=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
		    if("a01".equalsIgnoreCase(codesetid))
		    {
		    	CommonData cd = new CommonData();
		    	cd.setDataName(ResourceFactory.getProperty("b0110.label"));
		    	cd.setDataValue("B0110/A/UN");
		    	list.add(cd);
		    	cd=new CommonData();
		    	cd.setDataName(ResourceFactory.getProperty("e01a1.label"));
		    	cd.setDataValue("E01A1/A/@K");
		    	list.add(cd);
		    }
			 
			RowSet rowSet=dao.search("select * from fielditem where fieldsetid='"+codesetid+"' and useflag='1' ORDER BY displayid ");
			while(rowSet.next())
			{
				if(this.userview!=null&& "0".equals(this.userview.analyseFieldPriv(rowSet.getString("itemid"))))
					continue;
				CommonData d=new CommonData();
				d.setDataName(rowSet.getString("itemdesc"));
				d.setDataValue(rowSet.getString("itemid")+"/"+rowSet.getString("itemType")+"/"+rowSet.getString("codesetid"));
				list.add(d);
			}
			rowSet.close();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	
	/**
	 * 取得 引入 单位\部门 变动人员 数据列表
	 * @param tableHeadList
	 * @param fieldSetId
	 * @param fieldItemId
	 * @param p_value
	 * @param n_value
	 * @return
	 */
	public HashMap getTableDataMap(ArrayList tableHeadList,String fieldSetId,String fieldItemId,String p_value,String n_value,String querytype,String expr,String factor,boolean ishistory,String isSalaryManager)
	{
		HashMap map = new HashMap();
		ArrayList list=new ArrayList();
	    StringBuffer _right_field = new StringBuffer("");
		String cbase=this.templatevo.getString("cbase");
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			cbase=cbase.substring(0,cbase.length()-1);
			cbase=cbase.replaceAll(",","','");
			RowSet rowSet=dao.search("select * from dbname where UPPER(pre) in ('"+cbase.toUpperCase()+"')");
			
			while(rowSet.next())
			{
				ArrayList valueList=new ArrayList();
				String dbname=rowSet.getString("dbname");
				String pre=rowSet.getString("pre");
				String a01=pre+"A01";
				String other=pre+fieldSetId;
				StringBuffer sql=new StringBuffer("");
				if(other.equalsIgnoreCase(a01))
				{
					sql.append("select * ");//from "+a01+" where 1=1
				}
				else
				{
					sql.append("select "+a01+".b0110,"+a01+".E0122,"+a01+".e01a1,"+a01+".a0101");
					sql.append(","+other+".* ");//from "+a01+","+other+" where "+a01+".a0100="+other+".a0100 
				}
				if("0".equals(querytype))
				{
					if(other.equalsIgnoreCase(a01))
					{
						sql.append(" from "+a01+" where 1=1");
					}
					else
					{
						sql.append(" from "+a01+","+other+" where "+a01+".a0100="+other+".a0100");
						sql.append(" AND ("+pre+fieldSetId+".I9999=(select max(I9999) from "+pre+fieldSetId+" WHERE "+pre+fieldSetId+".A0100="+pre+"A01.A0100))");//如果一个人有多条记录，防止查出多条  zhaoxg 2015-8-31
					}
			    	if((p_value==null||p_value.trim().length()==0)&&(n_value==null||n_value.trim().length()==0))
			    		sql.append(" and 1=2 ");
		    		else
		    		{
			    		String[] temps=PubFunc.hireKeyWord_filter_reback(fieldItemId).split("/");
			    		if("A".equalsIgnoreCase(temps[1])|| "M".equalsIgnoreCase(temps[1]))
				    	{
				    		if("0".equals(temps[2]))
				    		{
				    			sql.append(" and "+temps[0]+" like '%"+p_value+"%'");
				    		}
				    		else
					    	{
				    			sql.append(" and "+temps[0]+" ='"+p_value+"'");
					    	}
				    	}
				    	if("N".equalsIgnoreCase(temps[1]))
				    		sql.append(" and "+temps[0]+" ="+p_value);
				    	if("D".equalsIgnoreCase(temps[1]))
				    	{
				    		if(p_value!=null&&p_value.length()>0)
				    			sql.append(PubFunc.getDateSql(">=",temps[0],p_value.trim()));
					    	if(n_value!=null&&n_value.length()>0)
				    			sql.append(PubFunc.getDateSql("<=",temps[0],n_value.trim()));		
				    	}
			    	}
			    	if("N".equalsIgnoreCase(isSalaryManager))
			    	{
			    		String privSQL=InfoUtils.getWhereINSql(this.userview, pre);
			    		sql.append(" and "+a01+".a0100 in (select "+pre+"a01.a0100 "+(privSQL.length()>0?privSQL:(" from "+a01))+")");
			    	}
				}
				else
				{
					String condSql="";
					 ArrayList fieldlist=new ArrayList();
					if(this.userview.isSuper_admin())
						condSql=userview.getPrivSQLExpression(PubFunc.keyWord_reback(SafeCode.decode(expr))+"|"+PubFunc.reBackWord(SafeCode.decode(factor)),pre,ishistory,false,true,fieldlist);
					else
					{
						FactorList factorslist=new FactorList(PubFunc.keyWord_reback(SafeCode.decode(expr)),PubFunc.reBackWord(SafeCode.decode(factor)),pre,ishistory ,false,true,1,userview.getUserId());
		        		fieldlist=factorslist.getFieldList();
		        		condSql=factorslist.getSqlExpression();
					}
					if(condSql.length()>0)
					{
						//if(!other.equalsIgnoreCase(a01))
							//sql.append(" AND ("+pre+fieldSetId+".I9999=(select max(I9999) from "+pre+fieldSetId+" WHERE "+pre+fieldSetId+".A0100="+pre+"A01.A0100))");
						sql.append(condSql);
					}
					if("N".equalsIgnoreCase(isSalaryManager))
			    	{
			    		String privSQL=InfoUtils.getWhereINSql(this.userview, pre);
			    		sql.append(" and "+a01+".a0100 in (select "+pre+"a01.a0100 "+(privSQL.length()>0?privSQL:(" from "+a01))+")");
			    	}
				}
				sql.append(" and "+other+".a0100 not in (select a0100 from "+this.gz_tablename+" where  upper(nbase)='"+pre.toUpperCase()+"') order by "+a01+".a0000");
				RowSet rowSet2=dao.search(sql.toString());
				LazyDynaBean abean=null;
				SimpleDateFormat fm=new SimpleDateFormat("yyyy-MM-dd");
				int j=0;
				while(rowSet2.next()){
					abean=new LazyDynaBean();
					abean.set("a0100", PubFunc.encrypt(rowSet2.getString("a0100"))); //20140915 dengcan 薪资发放人员引入
					abean.set("nbase", PubFunc.encrypt(pre));  //20140915 dengcan 薪资发放人员引入
					abean.set("CBASE", dbname);
					abean.set("B0110", rowSet2.getString("b0110")!=null?AdminCode.getCodeName("UN", rowSet2.getString("b0110")):"");
					abean.set("E0122", rowSet2.getString("E0122")!=null?AdminCode.getCodeName("UM", rowSet2.getString("E0122")):"");
					abean.set("E01A1", rowSet2.getString("E01A1")!=null?AdminCode.getCodeName("@K", rowSet2.getString("E01A1")):"");
					abean.set("A0101", rowSet2.getString("a0101"));
					//if(j!=0)
					_right_field.append(",");
					_right_field.append(PubFunc.encrypt(rowSet2.getString("a0100"))+"/"+PubFunc.encrypt(pre));
					j++;
					for(int i=5;i<tableHeadList.size();i++)
					{
						LazyDynaBean a_bean=(LazyDynaBean)tableHeadList.get(i);
						String itemid=(String)a_bean.get("itemid");
						String codesetid=(String)a_bean.get("codesetid");
						String itemtype=(String)a_bean.get("itemtype");
						if("M".equalsIgnoreCase(itemtype))
						{
							abean.set(itemid,Sql_switcher.readMemo(rowSet2, itemid));
						}
						else if("A".equalsIgnoreCase(itemtype))
						{
							if(rowSet2.getString(itemid)==null)
								abean.set(itemid,"");
							else
							{
								if("0".equals(codesetid))
									abean.set(itemid,rowSet2.getString(itemid));
								else
									abean.set(itemid,AdminCode.getCodeName(codesetid,rowSet2.getString(itemid)));
							}
						}
						else if("N".equalsIgnoreCase(itemtype))
						{
							if(rowSet2.getString(itemid)==null)
								abean.set(itemid,"");
							else
								abean.set(itemid,rowSet2.getString(itemid));
						}
						else if("D".equalsIgnoreCase(itemtype))
						{
							if(rowSet2.getDate(itemid)==null)
								abean.set(itemid,"");
							else
								abean.set(itemid,fm.format(rowSet2.getDate(itemid)));
						}
						
					}
					list.add(abean);
				}
				if(rowSet2!=null)
					rowSet2.close();
				
			}
			rowSet.close();
			map.put("list",list);
			map.put("allright",_right_field.toString().length()>0?_right_field.toString().substring(1):"");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	
	/**
	 * 取得 引入 单位\部门 变动人员 表头信息列表
	 * @param context
	 * @return
	 */
	public ArrayList getTableHeadList(String context)
	{
		ArrayList list=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			list.add(getLazyDynaBean("CBASE","人员库","0","A"));
			list.add(getLazyDynaBean("B0110","单位","UN","A"));
			list.add(getLazyDynaBean("E0122","部门","UM","A"));
			list.add(getLazyDynaBean("E01A1","岗位名称","@K","A"));
			list.add(getLazyDynaBean("A0101","姓名","0","A"));
			context=context.replaceAll(",","','");
			RowSet rowSet=dao.search("select * from fielditem where UPPER(itemid) in ('"+context.toUpperCase()+"') and UPPER(itemid) not in('B0110','E0122','E01A1','A0101','CBASE') ORDER BY displayid ");
			while(rowSet.next())
			{
				list.add(getLazyDynaBean(rowSet.getString("itemid"),rowSet.getString("itemdesc"),rowSet.getString("codesetid"),rowSet.getString("itemType")));
			}
			rowSet.close();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	
	private LazyDynaBean getLazyDynaBean(String itemid,String itemdesc,String codesetid,String itemtype)
	{
		LazyDynaBean abean=new LazyDynaBean();
		abean.set("itemid", itemid);
		abean.set("itemdesc", itemdesc);
		abean.set("codesetid", codesetid);
		abean.set("itemtype", itemtype);
		
		return abean;
	}
	
	
	/**
	 * 把临时变量增加到薪资表中去。
	 */
	public void addMidVarIntoGzTable(String strWhere,ArrayList midVariableList)throws GeneralException
	{
		ArrayList fieldlist=midVariableList;
		ArrayList midList=getMidVariableList2();
		try
		{
			DBMetaModel dbmodel=new DBMetaModel(this.conn);
			dbmodel.reloadTableModel(gz_tablename);
			RecordVo vo=new RecordVo(this.gz_tablename);
			DbWizard dbw=new DbWizard(this.conn);
			Table table=new Table(this.gz_tablename);
			String tablename="t#"+this.userview.getUserName()+"_gz_mid1";  //this.userview.getUserName()+"midtable";
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer buf=new StringBuffer();
			boolean bflag=false;
			HashMap existMidFieldMap=new HashMap();
			for(int i=0;i<fieldlist.size();i++)
			{
				FieldItem item=(FieldItem)fieldlist.get(i);
				String fieldname=item.getItemid();
				/**变量如果未加，则构建*/
				if(!vo.hasAttribute(fieldname.toLowerCase()))
				{
					Field field=item.cloneField();
					bflag=true;
					table.addField(field);
				}
				else
					existMidFieldMap.put(fieldname.toLowerCase(),item.cloneItem());
				//if end.
			}//for i loop end.
			
			if(bflag)
			{
				dbw.addColumns(table);
				dbmodel.reloadTableModel(gz_tablename);					
			}
			
			if(existMidFieldMap.size()>0) //同步表结构
			{
				syncGzField2(gz_tablename,existMidFieldMap);
			}
			
			
			/**导入计算后的临时变量的值*/
			String dbpres=this.templatevo.getString("cbase");
			/**应用库前缀*/
			String[] dbarr=StringUtils.split(dbpres, ",");
			String stry=currym.substring(0, 4);
			String strm=currym.substring(5, 7);
			String strc=currcount;
			YearMonthCount ymc=new YearMonthCount(Integer.parseInt(stry),Integer.parseInt(strm),Integer.parseInt(strc));
			/**按人员分库进行批量计算*/
			for(int i=0;i<dbarr.length;i++)
			{
				String dbpre=dbarr[i];
				for(int j=0;j<fieldlist.size();j++)
				{
					StringBuffer strFilter=new StringBuffer();

					FieldItem item=(FieldItem)fieldlist.get(j);
					String fldtype=item.getItemtype();
					String fldname=item.getItemid();
					String formula= item.getFormula();
					if(formula.indexOf("取自于")!=-1)
					{
					
						continue;
					}
					ArrayList usedlist=initUsedFields();
					ArrayList allUsedFields = DataDictionary.getAllFieldItemList(
							Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
					allUsedFields.addAll(midList);  //临时变量调用临时变量
					
					
					YksjParser yp = new YksjParser(this.userview, allUsedFields,
							YksjParser.forSearch, getDataType(fldtype), YksjParser.forPerson, "Ht", dbpre);
					yp.setStdTmpTable(this.gz_tablename);
					yp.setTargetFieldDecimal(item.getDecimalwidth());
					/**追加公式中使用的指标*/
					appendUsedFields(fieldlist,usedlist);
					/**增加一个计算公式用的临时字段*/
					FieldItem fielditem=new FieldItem("A01","AAAAA");
					fielditem.setItemdesc("AAAAA");
					fielditem.setCodesetid(item.getCodesetid());
					fielditem.setItemtype(fldtype);
					fielditem.setItemlength(item.getItemlength());
					fielditem.setDecimalwidth(item.getDecimalwidth());
					usedlist.add(fielditem);					
					/**创建计算用临时表*/
					String tmptable="t#"+this.userview.getUserName()+"_gz_mid1"; //this.userview.getUserName()+"midtable";
					if(createMidTable(usedlist,tmptable,"A0100"))
					{
						/**导入人员主集数据A0100,A0000,B0110,E0122,A0101*/
						buf.setLength(0);
						buf.append("insert into ");
						buf.append(tablename);
						buf.append("(A0000,A0100,B0110,E0122,A0101) select A0000,A0100,B0110,E0122,A0101 FROM ");
						buf.append(dbpre+"A01");
						buf.append(" where A0100 in (select A0100 from ");
						buf.append(this.gz_tablename);
						if(strWhere.length()==0)
						{
							buf.append(" where upper(nbase)='");
							buf.append(dbpre.toUpperCase());
							buf.append("'");
							
							/**计算临时变量的导入人员范围条件*/
							strFilter.append(" (select a0100 from ");
							strFilter.append(this.gz_tablename);
							strFilter.append(" where upper(nbase)='");
							strFilter.append(dbpre.toUpperCase());
							strFilter.append("')");	
						}
						else
						{
							
							buf.append(strWhere);
							buf.append(" and upper(nbase)='");
							buf.append(dbpre.toUpperCase());
							buf.append("'");
							
							/**计算临时变量的导入人员范围条件*/
							strFilter.append(" (select a0100 from ");
							strFilter.append(this.gz_tablename);
							strFilter.append(" ");
							strFilter.append(strWhere);
							strFilter.append(" and upper(nbase)='");
							strFilter.append(dbpre.toUpperCase());
							strFilter.append("')");	
						}
						buf.append(")");
						dao.update(buf.toString());
					}// 创建临时表结束.
					
					
					
					String dd = item.getFormula();
					yp.run(item.getFormula(),ymc,"AAAAA",tmptable,dao,strFilter.toString(),this.conn,fldtype,fielditem.getItemlength(),1,item.getCodesetid());
					
					buf.setLength(0);
					if(strWhere.length()==0)
					{
						buf.append("where upper(nbase)='");
						buf.append(dbpre.toUpperCase());
						buf.append("'");
					}
					else
					{
						buf.append(strWhere);
						buf.append(" and upper(nbase)='");
						buf.append(dbpre.toUpperCase());
						buf.append("'");
					}
			
					/**前面去掉WHERE*/
					String strcond=buf.substring(6);
					
					if(yp.isStatMultipleVar())
					{
						StringBuffer set_str=new StringBuffer("");
						StringBuffer set_st2=new StringBuffer("");
						for(int e=0;e<yp.getStatVarList().size();e++)
						{
							String temp=(String)yp.getStatVarList().get(e);
							set_st2.append(","+temp+"=null");
							set_str.append(gz_tablename+"."+temp+"="+tablename+"."+temp);
							if(Sql_switcher.searchDbServer()==2)
								set_str.append("`");
							else
								set_str.append(",");
						}
						if(set_str.length()>0)
							set_str.setLength(set_str.length()-1);
						else
							continue;
						
						dao.update("update "+gz_tablename+" set "+set_st2.substring(1)+"   "+buf.toString());
						dbw.updateRecord(gz_tablename,tablename,gz_tablename+".A0100="+tablename+".A0100", set_str.toString(), strcond, strcond);
					}
					else
						dbw.updateRecord(gz_tablename,tablename,gz_tablename+".A0100="+tablename+".A0100", gz_tablename+"."+fldname+"="+tablename+".AAAAA", strcond, strcond);
				}//for j loop end.
			}//for i loop end.
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	
	/**
	 * 同步表结构(判断临时变量字段)
	 * @param gz_tablename
	 * @param existMidFieldList
	 */
	private void  syncGzField2(String tableName,HashMap existMidFieldMap)
	{
		try
		{
			 ContentDAO dao=new ContentDAO(this.conn);
			 DbWizard dbw=new DbWizard(this.conn);
			 RowSet rowSet=dao.search("select * from "+tableName+" where 1=2");
			 ResultSetMetaData data=rowSet.getMetaData();
			 
			 ArrayList alterList=new ArrayList();
			 ArrayList resetList=new ArrayList();
			 for(int i=1;i<=data.getColumnCount();i++)
			 {
					String columnName=data.getColumnName(i).toLowerCase();
					if(existMidFieldMap.get(columnName)!=null)
					{
						FieldItem tempItem=(FieldItem)existMidFieldMap.get(columnName);
						int columnType=data.getColumnType(i);	
						int size=data.getColumnDisplaySize(i);
						int scale=data.getScale(i);
						switch(columnType)
						{
							case java.sql.Types.INTEGER:
								if("N".equals(tempItem.getItemtype()))
								{
									if(tempItem.getDecimalwidth()!=scale)
									{
										if(Sql_switcher.searchDbServer()!=2)  //不为oracle
											alterList.add(tempItem.cloneField());
										else
											alertColumn(tableName,tempItem,dbw,dao);
									}
								}
								if(!"N".equals(tempItem.getItemtype()))
								{
									if("A".equals(tempItem.getItemtype()))
									{
										
										if(Sql_switcher.searchDbServer()!=2)  //不为oracle
											alterList.add(tempItem.cloneField());
										else
											alertColumn(tableName,tempItem,dbw,dao);
									}
									else		
										resetList.add(tempItem.cloneField());
								}
								break;
							case java.sql.Types.TIMESTAMP:
								if(!"D".equals(tempItem.getItemtype()))
								{
									resetList.add(tempItem.cloneField());
								}
								break;
							case java.sql.Types.VARCHAR:
								if("A".equals(tempItem.getItemtype()))
								{
									if(tempItem.getItemlength()>size)
									{
										
										if(Sql_switcher.searchDbServer()!=2)  //不为oracle
											alterList.add(tempItem.cloneField());
										else
											alertColumn(tableName,tempItem,dbw,dao);
										
									}
								}
								else 
									resetList.add(tempItem.cloneField());
								break;
							case java.sql.Types.DOUBLE:
								if("N".equals(tempItem.getItemtype()))
								{
									if(tempItem.getDecimalwidth()>scale)
									{
										if(Sql_switcher.searchDbServer()!=2)  //不为oracle
											alterList.add(tempItem.cloneField());
										else
											alertColumn(tableName,tempItem,dbw,dao);
									}
								}
								if(!"N".equals(tempItem.getItemtype()))
								{
									if("A".equals(tempItem.getItemtype()))
									{
										
										if(Sql_switcher.searchDbServer()!=2)  //不为oracle
											alterList.add(tempItem.cloneField());
										else
											alertColumn(tableName,tempItem,dbw,dao);
									}
									else		
										resetList.add(tempItem.cloneField());
								}
								
								
								break;
							case java.sql.Types.NUMERIC:
								if("N".equals(tempItem.getItemtype()))
								{
									if(tempItem.getDecimalwidth()>scale)
									{
										
										if(Sql_switcher.searchDbServer()!=2)  //不为oracle
											alterList.add(tempItem.cloneField());
										else
											alertColumn(tableName,tempItem,dbw,dao);
									}
								}
								if(!"N".equals(tempItem.getItemtype()))
								{
									if("A".equals(tempItem.getItemtype()))
									{
										if(Sql_switcher.searchDbServer()!=2)  //不为oracle
											alterList.add(tempItem.cloneField());
										else
											alertColumn(tableName,tempItem,dbw,dao);
										
									}
									else		
										resetList.add(tempItem.cloneField());
								}
								break;	
							case java.sql.Types.LONGVARCHAR:
								if(!"M".equals(tempItem.getItemtype()))
								{
									resetList.add(tempItem.cloneField());
								}
								break;
						}
					}
				}
				rowSet.close();
				
			    Table table=new Table(tableName);
			    if(Sql_switcher.searchDbServer()!=2)  //不为oracle
			    {
				    for(int i=0;i<alterList.size();i++)
							table.addField((Field)alterList.get(i));
					if(alterList.size()>0)
							dbw.alterColumns(table);
					 table.clear();
			    }
			//  else
			//  	syncGzOracleField(data,map,tableName);
			     table.clear();
				 for(int i=0;i<resetList.size();i++)
						table.addField((Field)resetList.get(i));
				 if(resetList.size()>0)
				 {
					 dbw.dropColumns(table);
					 dbw.addColumns(table);
				 }
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	
	/**
	 * 把临时变量增加历史记录临时中去。
	 */
	public void addMidVarIntoGzTable(String tableName,String strWhere,ArrayList midVariableList)throws GeneralException
	{
		ArrayList fieldlist=midVariableList;
		try
		{
			DBMetaModel dbmodel=new DBMetaModel(this.conn);
			dbmodel.reloadTableModel(tableName);
			RecordVo vo=new RecordVo(tableName);
			DbWizard dbw=new DbWizard(this.conn);
			Table table=new Table(tableName);
			String tablename="t#"+this.userview.getUserName()+"_gz_mid"; //this.userview.getUserName()+"midtable";
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer buf=new StringBuffer();
			boolean bflag=false;
			for(int i=0;i<fieldlist.size();i++)
			{
				FieldItem item=(FieldItem)fieldlist.get(i);
				String fieldname=item.getItemid();
				/**变量如果未加，则构建*/
				if(!vo.hasAttribute(fieldname.toLowerCase()))
				{
					Field field=item.cloneField();
					bflag=true;
					table.addField(field);
				}//if end.
			}//for i loop end.
			
			if(bflag)
			{
				dbw.addColumns(table);
				dbmodel.reloadTableModel(tableName);					
			}
			/**导入计算后的临时变量的值*/
			String dbpres=this.templatevo.getString("cbase");
			/**应用库前缀*/
			String[] dbarr=StringUtils.split(dbpres, ",");
			String stry=currym.substring(0, 4);
			String strm=currym.substring(5, 7);
			String strc=currcount;
			YearMonthCount ymc=new YearMonthCount(Integer.parseInt(stry),Integer.parseInt(strm),Integer.parseInt(strc));
			/**按人员分库进行批量计算*/
			for(int i=0;i<dbarr.length;i++)
			{
				String dbpre=dbarr[i];
				for(int j=0;j<fieldlist.size();j++)
				{
					StringBuffer strFilter=new StringBuffer();

					FieldItem item=(FieldItem)fieldlist.get(j);
					String fldtype=item.getItemtype();
					String fldname=item.getItemid();
					
					ArrayList usedlist=initUsedFields();
					ArrayList allUsedFields = DataDictionary.getAllFieldItemList(
							Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
					YksjParser yp = new YksjParser(this.userview, allUsedFields,
							YksjParser.forSearch, getDataType(fldtype), YksjParser.forPerson, "Ht", dbpre);
					yp.setStdTmpTable(tableName);
					yp.setTargetFieldDecimal(item.getDecimalwidth());
					/**追加公式中使用的指标*/
					appendUsedFields(fieldlist,usedlist);
					/**增加一个计算公式用的临时字段*/
					FieldItem fielditem=new FieldItem("A01","AAAAA");
					fielditem.setItemdesc("AAAAA");
					fielditem.setCodesetid(item.getCodesetid());
					fielditem.setItemtype(fldtype);
					fielditem.setItemlength(item.getItemlength());
					fielditem.setDecimalwidth(item.getDecimalwidth());
					usedlist.add(fielditem);					
					/**创建计算用临时表*/
					String tmptable="t#"+this.userview.getUserName()+"_gz_mid"; //this.userview.getUserName()+"midtable";
					if(createMidTable(usedlist,tmptable,"A0100"))
					{
						/**导入人员主集数据A0100,A0000,B0110,E0122,A0101*/
						buf.setLength(0);
						buf.append("insert into ");
						buf.append(tablename);
						buf.append("(A0000,A0100,B0110,E0122,A0101) select A0000,A0100,B0110,E0122,A0101 FROM ");
						buf.append(dbpre+"A01");
						buf.append(" where A0100 in (select A0100 from ");
						buf.append(tableName);
						if(strWhere.length()==0)
						{
							buf.append(" where upper(nbase)='");
							buf.append(dbpre.toUpperCase());
							buf.append("'");
							
							/**计算临时变量的导入人员范围条件*/
							strFilter.append(" (select a0100 from ");
							strFilter.append(tableName);
							strFilter.append(" where upper(nbase)='");
							strFilter.append(dbpre.toUpperCase());
							strFilter.append("')");	
						}
						else
						{
							
							buf.append(strWhere);
							buf.append(" and upper(nbase)='");
							buf.append(dbpre.toUpperCase());
							buf.append("'");
							
							/**计算临时变量的导入人员范围条件*/
							strFilter.append(" (select a0100 from ");
							strFilter.append(tableName);
							strFilter.append(" ");
							strFilter.append(strWhere);
							strFilter.append(" and upper(nbase)='");
							strFilter.append(dbpre.toUpperCase());
							strFilter.append("')");	
						}
						buf.append(")");
						dao.update(buf.toString());
					}// 创建临时表结束.
					
					
					
					
					yp.run(item.getFormula(),ymc,"AAAAA",tmptable,dao,strFilter.toString(),this.conn,fldtype,fielditem.getItemlength(),1,item.getCodesetid());
					
					buf.setLength(0);
					if(strWhere.length()==0)
					{
						buf.append("where upper(nbase)='");
						buf.append(dbpre.toUpperCase());
						buf.append("'");
					}
					else
					{
						buf.append(strWhere);
						buf.append(" and upper(nbase)='");
						buf.append(dbpre.toUpperCase());
						buf.append("'");
					}
			
					/**前面去掉WHERE*/
					String strcond=buf.substring(6);
					dbw.updateRecord(tableName,tablename,tableName+".A0100="+tablename+".A0100", tableName+"."+fldname+"="+tablename+".AAAAA", strcond, strcond);
				}//for j loop end.
			}//for i loop end.
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	/**
	 * 取得字段列表中包括子集名列表
	 * @param fieldlist
	 * @return
	 */
	private List getSetListByStd(ArrayList fieldlist)
	{
		List setlist=null;
		StringBuffer buf=new StringBuffer();
		for(int i=0;i<fieldlist.size();i++)
		{
			FieldItem item=(FieldItem)fieldlist.get(i);
			String setid=item.getFieldsetid();
			if(buf.indexOf(setid)==-1)
			{
				buf.append(setid);
				buf.append(",");
			}//if end.
		}//for i loop end.
		if(buf.length()>0)
		{
			String[] setarr=StringUtils.split(buf.toString(),",");
			setlist=Arrays.asList(setarr);
		}
		return setlist;
	}
	
	/**
	 * 求标准表中，更新SQL语句
	 * @param fieldlist
	 * @param strS
	 * @param setid
	 * @return
	 */
	private String getStdUpdateSQL(ArrayList fieldlist,String strS,String setid)
	{
		StringBuffer buf=new StringBuffer();
		StringBuffer fields=new StringBuffer();		
		for(int i=0;i<fieldlist.size();i++)
		{
			FieldItem item=(FieldItem)fieldlist.get(i);

			/**子集名相同*/
			if(item.getFieldsetid().equalsIgnoreCase(setid))
			{
				if(fields.indexOf(item.getItemid())!=-1)
					continue;				
				String fieldname=item.getItemid();
				buf.append(this.gz_tablename);
				buf.append(".");
				buf.append(fieldname);
				buf.append("=");
				buf.append(strS);
				buf.append(".");
				buf.append(fieldname);
				if(Sql_switcher.searchDbServer()==2)
					buf.append("`");
				else
					buf.append(",");
				/**过滤掉相同的指标项*/
				fields.append(fieldname);
				fields.append(",");
			}
		}//for i loop end.
		if(buf.length()>0)
			buf.setLength(buf.length()-1);
		return buf.toString();
	}

	/**
	 * 把标准表中指标加入薪资发放表中，从档案中取得标准表对应的指标值
	 * @param fieldlist
	 * @return
	 */
	private void addStdFieldIntoGzTable(String strWhere)throws GeneralException
	{
		try
		{
			/**薪资标准计算*/
			ArrayList fieldlist=this.searchStdTableFieldList();
			
			if(fieldlist.size()==0)
				return;
			List setlist=getSetListByStd(fieldlist);			
			RecordVo vo=new RecordVo(this.gz_tablename);
			DbWizard dbw=new DbWizard(this.conn);
			Table table=new Table(this.gz_tablename);
			String midtable="t#"+this.userview.getUserName()+"_gz"; //this.userview.getUserName()+"midtable";			
			/**
			 * 把标准中涉及到的工资表中没有的指标加入至薪资表结构表中,
			 */
			DBMetaModel dbmodel=new DBMetaModel(this.conn);
			boolean bflag=false;
			ArrayList notusedlist=new ArrayList();
			for(int i=0;i<fieldlist.size();i++)
			{
				FieldItem item=(FieldItem)fieldlist.get(i);
				String fieldname=item.getItemid();
				/**变量如果未加，则构建*/
				notusedlist.add(item);
				if(!vo.hasAttribute(fieldname.toLowerCase()))
				{
					Field field=item.cloneField();
					bflag=true;
					table.addField(field);
				}//if end.
			}//for i loop end.
			if(bflag)
			{
				dbw.addColumns(table);
				
				dbmodel.reloadTableModel(gz_tablename);						
			}
			String dbpres=this.templatevo.getString("cbase");
			/**应用库前缀*/
			String[] dbarr=StringUtils.split(dbpres, ",");
			/**从档案表中导入有关标准表涉及到的数据*/
			for(int i=0;i<dbarr.length;i++)
			{
				String dbpre=dbarr[i];
				for(int j=0;j<setlist.size();j++)
				{
					String setid=(String)setlist.get(j);
					if("A00".equalsIgnoreCase(setid))//多媒体子集
						continue;
					char cc=setid.charAt(0);
					switch(cc)
					{
					case 'A': //人员信息
							String strS=dbpre+setid;		
							if("A01".equalsIgnoreCase(setid)) //主集
							{
								String strupdate=getStdUpdateSQL(notusedlist, strS, setid);
								if(strupdate.length()>0)
									dbw.updateRecord(gz_tablename,strS,gz_tablename+".A0100="+strS+".A0100", strupdate, "upper("+gz_tablename+".NBASE)='"+dbpre.toUpperCase()+"'", "");
							}
							else//子集
							{
								String strupdate=getStdUpdateSQL(notusedlist, midtable, setid);
								if(strupdate.length()==0)
									continue;
								String strfields=getStdFieldNameList(notusedlist, setid);
								/**子集当前子录生成临时表*/
								String tempt="t#"+this.userview.getUserName()+"_gz_1"; //this.userview.getUserName()+"midtable1";
						//		if(dbw.isExistTable(tempt, false))
									dbw.dropTable(tempt);
								dbw.createTempTable(strS, tempt,"A0100 as A0000,Max(I9999) as midid", "","A0100");
						//		if(dbw.isExistTable(midtable, false))
									dbw.dropTable(midtable);
								dbw.createTempTable(strS+" Left join "+tempt+" On "+strS+".A0100="+tempt+".A0000",midtable, "A0100,"+strfields,strS+".I9999="+tempt+".midid","");
								dbw.updateRecord(this.gz_tablename,midtable,this.gz_tablename+".A0100="+midtable+".A0100",strupdate, "upper("+this.gz_tablename+".NBASE)='"+dbpre.toUpperCase()+"'", strWhere);
							}
							break;
					case 'B'://单位信息
							break;
					case 'K'://职位信息
							break;
					}
				}//for j 子集数据处理
			}//for i loop end.
			/**对日期型和数值型区域值进行处理*/
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);			
		}
	}
	/**
	 * 取得标准表字段列表
	 * @param fieldlist
	 * @param setid
	 * @return for examples a0xxx,a2000
	 */
	private String getStdFieldNameList(ArrayList fieldlist,String setid)
	{
		StringBuffer buf=new StringBuffer();
		for(int i=0;i<fieldlist.size();i++)
		{
			FieldItem fielditem=(FieldItem)fieldlist.get(i);
			if(fielditem.getFieldsetid().equalsIgnoreCase(setid))
			{
				if(buf.indexOf(fielditem.getItemid())!=-1)
					continue;
				buf.append(fielditem.getItemid());
				buf.append(",");
			}
		}//for i loop end.
		if(buf.length()>0)
			buf.setLength(buf.length()-1);
		return buf.toString();
	}
	/**
	 * 对选中的公式进行计算 
	 * @param formulalist 计算公式列表
	 * @param strWhere    计算范围（也即过滤条件）
	 * @return
	 * @throws GeneralException
	 */
	public boolean secondComputing(ArrayList formulalist,String strWhere)throws GeneralException
	{
		boolean bflag=false;
		try
		{
			int nrunflag=0;
			RecordVo vo=new RecordVo(this.getGz_tablename().toLowerCase());
			
			
			//清除所有数据
			for(int i=0;i<formulalist.size();i++)
			{
                DynaBean dbean=(LazyDynaBean)formulalist.get(i);
                nrunflag=Integer.parseInt((String)dbean.get("runflag"));
                String formula=(String)dbean.get("rexpr");
                String cond=(String)dbean.get("cond");
                String fieldname=(String)dbean.get("itemname");
                String strStdId=(String)dbean.get("standid");
                /**系统项不用计算*/
                if(isSystemItem(fieldname)&&!"A00Z0".equalsIgnoreCase(fieldname)&&!"a00z1".equalsIgnoreCase(fieldname))//归属次数和归属日期也计算，zhaoxg add 2013-10-16
                	continue;
                /**分析左边项是否在工资表中存在*/
                if(!vo.hasAttribute(fieldname.toLowerCase()))
                	continue;
                switch(nrunflag)
                {
                	case 2://税率表
                		deleteTaxMx(dbean,strWhere);
	                	break;
                }
			}//for i loop end.
			
			
			 
			
			
			
			
			
			
			
			for(int i=0;i<formulalist.size();i++)
			{
                DynaBean dbean=(LazyDynaBean)formulalist.get(i);
                nrunflag=Integer.parseInt((String)dbean.get("runflag"));
                String formula=(String)dbean.get("rexpr");
                String cond=(String)dbean.get("cond");
                String fieldname=(String)dbean.get("itemname");
                String strStdId=(String)dbean.get("standid");
                /**系统项不用计算*/
                if(isSystemItem(fieldname)&&!"A00Z0".equalsIgnoreCase(fieldname)&&!"a00z1".equalsIgnoreCase(fieldname))//归属次数和归属日期也计算，zhaoxg add 2013-10-16
                	continue;
                /**分析左边项是否在工资表中存在*/
                if(!vo.hasAttribute(fieldname.toLowerCase()))
                	continue;
                switch(nrunflag)
                {
                case 1://执行工资标准
                	calcGzStandard(Integer.parseInt(strStdId),fieldname,strWhere);
                	break;
                case 2://税率表
                	calcTax(dbean,strWhere);
                	break;
                case 0://执行计算公式
                	calcFormula(formula,cond,fieldname,strWhere);
                	break;
                }
			}//for i loop end.
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return bflag;
	}
	
	
	private void deleteTaxMx(DynaBean dbean,String strWhere)throws GeneralException
	{
	      try
	      {
	    	    String cond=(String)dbean.get("cond");
				if(!(cond==null|| "".equalsIgnoreCase(cond)))
				{
					YksjParser yp = new YksjParser( this.userview ,fldvarlist,
							YksjParser.forNormal, YksjParser.LOGIC,YksjParser.forPerson , "Ht", "");
					yp.run_where(cond);
					cond=yp.getSQL();
				}	    	
				if(cond.length()>0&&strWhere.length()>0)
					strWhere=strWhere+" and ( "+cond+" )";
				else if((strWhere==null||strWhere.trim().length()==0)&&cond!=null&&cond.trim().length()>0)
					strWhere=" ( "+cond+" ) ";
				
	    	    CalcTaxBo calcbo=new CalcTaxBo(this.salaryid,this.conn,this.userview);
	    	    calcbo.setGz_tablename(this.gz_tablename);
	    	    calcbo.delTaxMx(strWhere, dbean);
	      }
	      catch(Exception ex)
	      {
	    	  ex.printStackTrace();	 
	    	  throw GeneralExceptionHandler.Handle(ex);
	      }
	}
	
	
	/**
	 * 个人所得税计算
	 * @param taxid
	 * @param strWhere
	 */
	private void calcTax(DynaBean dbean,String strWhere)throws GeneralException
	{
		  CalcTaxBo calcbo=new CalcTaxBo(this.salaryid,this.conn,this.userview);
	      try
	      {
	    	    ContentDAO dao=new ContentDAO(this.conn);
	    	    String cond=(String)dbean.get("cond");
				if(!(cond==null|| "".equalsIgnoreCase(cond)))
				{
					YksjParser yp = new YksjParser( this.userview ,fldvarlist,
							YksjParser.forNormal, YksjParser.LOGIC,YksjParser.forPerson , "Ht", "");
					yp.run_where(cond);
					cond=yp.getSQL();
				}	    	
				if(cond.length()>0&&strWhere.length()>0)
					strWhere=strWhere+" and ( "+cond+" )";
				else if((strWhere==null||strWhere.trim().length()==0)&&cond!=null&&cond.trim().length()>0)
					strWhere=cond;
				
	    	  
	    	    calcbo.setGz_tablename(this.gz_tablename);
	    	   
	    	    String itemid=(String)dbean.get("itemname");
	    	    if(clearItemMap.get(itemid.toLowerCase())!=null)
	    	    {
	    	    		calcbo.setIsClearItem("0");
	    	    }
	    	    else
	    	    	clearItemMap.put(itemid.toLowerCase(),"1");
	    	   
	    	    //
	    	    String _str=getMultipleDataOneMenStr();
	    	    if(_str.length()>0)
	    	    {
	    	    	String _strWhere=strWhere;
	    	    	if(_strWhere==null||_strWhere.trim().length()==0)
	    	    		_strWhere=" not ("+_str.substring(3)+")";
	    	    	else
	    	    		_strWhere+=" and not ("+_str.substring(3)+")";
	    	    	calcbo.calc(_strWhere, dbean);
	    	    	
	    	    	
	    	    	RowSet rowSet=dao.search("select * from "+this.gz_tablename+" where ("+_str.substring(3)+") order by nbase,a0100,a00z0,a00z1");
	    	    	while(rowSet.next())
	    	    	{ 
	    	    		_strWhere=strWhere;
	    	    		Date d=rowSet.getDate("a00z0");
	    	    		Calendar cd=Calendar.getInstance();
	    	    		cd.setTime(d);
	    	    		
	    	    		String _sql=" ( "+Sql_switcher.year("a00z0")+"="+cd.get(Calendar.YEAR)+" and "+Sql_switcher.month("a00z0")+"="+(cd.get(Calendar.MONTH)+1)
	    	    					+" and a00z1="+rowSet.getString("a00z1")+" and upper(nbase)='"+rowSet.getString("nbase").toUpperCase()
	    	    					+"' and a0100='"+rowSet.getString("a0100")+"' )";
	    	    		if(_strWhere==null||_strWhere.trim().length()==0)
		    	    		_strWhere=_sql;
		    	    	else
		    	    		_strWhere+=" and "+_sql;
	    	    		calcbo.calc(_strWhere, dbean);
	    	    	}
	    	    	
	    	    }
	    	    else
	    	    	calcbo.calc(strWhere, dbean);
	    	    
	    	    
	    	    errorInfo=calcbo.getErrorInfo();
	      }
	      catch(Exception ex)
	      {
	    	  errorInfo=calcbo.getErrorInfo();
	    	  ex.printStackTrace();	 
	    	  throw GeneralExceptionHandler.Handle(ex);
	      }
	}
	
	
	/**
	 * 是否一个人在同一类别下有多条记录
	 * @return
	 */
	private String getMultipleDataOneMenStr()
	{
		StringBuffer _str=new StringBuffer("");
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select count(a0100),a0100,nbase from "+this.gz_tablename+" group by nbase,a0100 having count(a0100)>1");//,a00z0 having count(a0100)>1"); 2013-11-9
			while(rowSet.next())
			{
				_str.append(" or (a0100='"+rowSet.getString("a0100")+"' and lower(nbase)='"+rowSet.getString("nbase").toLowerCase()+"')");
			}
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception ex)
	    {
	    	  ex.printStackTrace();	
	    }
		return _str.toString();
	}
	
	
	/**
	 * 执行薪资标准
	 * @param standid	标准号
	 * @param fieldname 目标指标
	 * @param strWhere  条件
	 */
	private void calcGzStandard(int standid,String fieldname,String strWhere)throws GeneralException
	{
      try
      {
		SalaryStandardBo stdbo=new SalaryStandardBo(this.conn,String.valueOf(standid),"");
		/**如果标准不存在，则退出*/
		if(!stdbo.isExist())
			return;
		/**重新计算相关日期型或数值型区间范围的值*/
		
		StringBuffer buf=new StringBuffer();
		if(!stdbo.checkHVField(buf))
			throw new GeneralException(buf.toString());
		/**把标准横纵坐标为日期型或数值型指标，加至薪资表中*/
		ArrayList list=stdbo.addStdItemIntoTable(this.gz_tablename);
		stdbo.updateStdItem(list, this.gz_tablename);
		/**关联更新串*/
		String joinon=stdbo.getStandardJoinOn(this.gz_tablename);
		FieldItem item=DataDictionary.getFieldItem(fieldname);
		DbWizard dbw=new DbWizard(this.conn);		
		
		String aflag=this.ctrlparam.getValue(SalaryCtrlParamBo.PRIV_MODE,"flag");  // 人员范围权限过滤标志  1：有  
		//共享薪资类别，其他操作人员引入数据 (dengcan 2008/6/26 )
		String atableName="t#"+this.userview.getUserName()+"_gzsp";  //this.userview.getUserName()+"_sp_data";
		if(!this.gz_tablename.equalsIgnoreCase(atableName)&&this.manager.length()>0&&!this.userview.getUserName().equalsIgnoreCase(this.manager)&&aflag!=null&& "1".equals(aflag))
		{
			String dbpres=this.templatevo.getString("cbase");
			//应用库前缀
			String[] dbarr=StringUtils.split(dbpres, ",");
			for(int i=0;i<dbarr.length;i++)
			{
				String pre=dbarr[i];
				StringBuffer temp=new StringBuffer("");
				if(strWhere!=null&&strWhere.trim().length()>0)
					temp.append(" and ");
				temp.append(" upper(nbase)='"+pre.toUpperCase()+"'") ;
				//					权限过滤
				if("1".equals(this.controlByUnitcode))
				{
					String whl_str=getWhlByUnits();
					if(whl_str.length()>0)
					{
						temp.append(whl_str);
					}
				}
				else
				{
					String whereIN=InfoUtils.getWhereINSql(this.userview,pre);
					whereIN="select a0100 "+whereIN;	
					temp.append(" and a0100 in ( "+whereIN+" )");
				}
				switch(Sql_switcher.searchDbServer())
				{
					case 1: //MSSQL
						dbw.updateRecord(this.gz_tablename, "gz_item",joinon,this.gz_tablename+"."+fieldname+"=gz_item.standard", strWhere+temp.toString(), "");
						break;
					case 2://oracle
						if("N".equalsIgnoreCase(item.getItemtype()))
							dbw.updateRecord(this.gz_tablename, "gz_item",joinon,this.gz_tablename+"."+fieldname+"=to_number(gz_item.standard)", strWhere+temp.toString(), "");
						else
							dbw.updateRecord(this.gz_tablename, "gz_item",joinon,this.gz_tablename+"."+fieldname+"=gz_item.standard", strWhere+temp.toString(), "");
						break;
					case 3://db2
						if("N".equalsIgnoreCase(item.getItemtype()))
							dbw.updateRecord(this.gz_tablename, "gz_item",joinon,this.gz_tablename+"."+fieldname+"=double(gz_item.standard)", strWhere+temp.toString(), "");
						else
							dbw.updateRecord(this.gz_tablename, "gz_item",joinon,this.gz_tablename+"."+fieldname+"=gz_item.standard", strWhere+temp.toString(), "");
						break;
				}
			}
		}
		else
		{
			switch(Sql_switcher.searchDbServer())
			{
				case 1: //MSSQL
					if("N".equalsIgnoreCase(item.getItemtype()))
						dbw.updateRecord(this.gz_tablename, "gz_item",joinon,this.gz_tablename+"."+fieldname+"="+Sql_switcher.isnull("nullif(gz_item.standard,'')","0"), strWhere, "");
					else
						dbw.updateRecord(this.gz_tablename, "gz_item",joinon,this.gz_tablename+"."+fieldname+"=gz_item.standard", strWhere, "");
					break;
				case 2://oracle
					if("N".equalsIgnoreCase(item.getItemtype()))
						dbw.updateRecord(this.gz_tablename, "gz_item",joinon,this.gz_tablename+"."+fieldname+"=to_number(gz_item.standard)", strWhere, "");
					else
						dbw.updateRecord(this.gz_tablename, "gz_item",joinon,this.gz_tablename+"."+fieldname+"=gz_item.standard", strWhere, "");
					break;
				case 3://db2
					if("N".equalsIgnoreCase(item.getItemtype()))
						dbw.updateRecord(this.gz_tablename, "gz_item",joinon,this.gz_tablename+"."+fieldname+"=double(gz_item.standard)", strWhere, "");
					else
						dbw.updateRecord(this.gz_tablename, "gz_item",joinon,this.gz_tablename+"."+fieldname+"=gz_item.standard", strWhere, "");
					break;
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
	 * 
	 * @param formula    计算公式
	 * @param cond       计算条件
	 * @param fieldname  计算项目
	 * @param strWhere   整个人员过滤条件
	 * @throws GeneralException 
	 */
	private void calcFormula(String formula,String cond,String fieldname,String strWhere) throws GeneralException
	{
		YksjParser yp=null;
		try
		{
			String strfilter="";
	        
			ContentDAO dao=new ContentDAO(this.conn);
			/**先对计算公式的条件进行分析*/
			if(!(cond==null|| "".equalsIgnoreCase(cond)))
			{ 
				
				yp = new YksjParser( this.userview ,fldvarlist,
						YksjParser.forNormal, YksjParser.LOGIC,YksjParser.forPerson , "Ht", "");
				if(this.currym!=null&&currym.trim().length()>0&&this.currcount!=null&&currcount.trim().length()>0)
				{
					String stry=currym.substring(0, 4);
					String strm=currym.substring(5, 7);
					String strc=currcount;
					YearMonthCount ymc=new YearMonthCount(Integer.parseInt(stry),Integer.parseInt(strm),Integer.parseInt(strc));
					yp.setYmc(ymc);
				}
				yp.run_where(cond);
				strfilter=yp.getSQL();
			}
			StringBuffer strcond=new StringBuffer();
			if(!(strWhere==null|| "".equalsIgnoreCase(strWhere)))
				strcond.append(strWhere);
			if(!("".equalsIgnoreCase(strfilter)))
			{
				if(strcond.length()>0)
					strcond.append(" and ");
				strcond.append(strfilter);
			}
			
			if(!"归属日期()".equals(formula.trim())&&!"归属日期".equals(formula.trim()))
			{
				/**进行公式计算*/
				if("A00Z0".equalsIgnoreCase(fieldname)){
					yp=new YksjParser( this.userview ,fldvarlist,
							YksjParser.forNormal, getDataType("D"),YksjParser.forPerson , "Ht", "");
				}else if("a00z1".equalsIgnoreCase(fieldname)){
					yp=new YksjParser( this.userview ,fldvarlist,
							YksjParser.forNormal, getDataType("N"),YksjParser.forPerson , "Ht", "");
				}else{
					FieldItem item=DataDictionary.getFieldItem(fieldname);
					yp=new YksjParser( this.userview ,fldvarlist,
							YksjParser.forNormal, getDataType(item.getItemtype()),YksjParser.forPerson , "Ht", "");
				}
				yp.run(formula,this.conn,strcond.toString(),this.gz_tablename);
			}
			/**单表计算*/
			String strexpr="";
			if(!"归属日期()".equals(formula.trim())&&!"归属日期".equals(formula.trim()))
				strexpr=yp.getSQL();
			else
				strexpr=Sql_switcher.dateValue(currym);
			StringBuffer strsql=new StringBuffer();
			strsql.append("update ");
			strsql.append(this.gz_tablename);
			strsql.append(" set ");
			strsql.append(fieldname);
			strsql.append("=");
			strsql.append(strexpr);
			strsql.append(" where 1=1 ");
			if(strcond.length()>0)
			{
				strsql.append(" and ");
				strsql.append(strcond.toString());
			}
	//		System.out.println(strsql.toString());
			String aflag=this.ctrlparam.getValue(SalaryCtrlParamBo.PRIV_MODE,"flag");  // 人员范围权限过滤标志  1：有  
			//共享薪资类别，其他操作人员引入数据 (dengcan 2008/6/26 )
			
			String tableName="t#"+this.userview.getUserName()+"_gzsp"; //this.userview.getUserName()+"_sp_data";
			if(!tableName.equalsIgnoreCase(this.gz_tablename)&&this.manager.length()>0&&!this.userview.getUserName().equalsIgnoreCase(this.manager)&&aflag!=null&& "1".equals(aflag))
			{

					StringBuffer tempSql=new StringBuffer();
					String b_units=this.userview.getUnitIdByBusiOutofPriv("1");// 1:工资发放  2:工资总额  3:所得税
					String whl_str=this.getPrivSQL("", "", salaryid+"", b_units);
					if(whl_str.length()>0)
					{
						tempSql.append(" and "+whl_str);				
					}
					dao.update(strsql.toString()+tempSql.toString());
				
			}
			else
				dao.update(strsql.toString());
		}
		catch(Exception ex)
		{
			String message=ex.toString();
			if(message.indexOf("最大")!=-1&&message.indexOf("8060")!=-1&&Sql_switcher.searchDbServer()==1)
			{ 
				PubFunc.resolve8060(this.conn,this.gz_tablename);
				throw GeneralExceptionHandler.Handle(new Exception("请重新执行计算操作!"));
			}
			if("a00z1".equalsIgnoreCase(fieldname)&&ex.toString().indexOf("唯一")!=-1){
			  	ex.printStackTrace();
      	    	throw GeneralExceptionHandler.Handle(new Exception("同一人有多条薪资数据，不能执行归属次数的计算公式！"));
			}

		}finally{ 
			if("a00z1".equalsIgnoreCase(fieldname)){
				
			}else{
				yp=null;
			}
			
		} 
	}
	
	/**
	 * 分析此指标是否为系统项
	 * @param fieldname
	 * @return
	 */
	private boolean isSystemItem(String fieldname)
	{
		if("A01Z0".equalsIgnoreCase(fieldname)){//停放标识放开了，可以计算  zhaoxg add 2015-4-10
			return false;
		}
		boolean bflag=false;
		for(int i=0;i<this.gzitemlist.size();i++)
		{
			GzItemVo itemvo=(GzItemVo)this.gzitemlist.get(i);
			if(itemvo.getFldname().equalsIgnoreCase(fieldname))
			{
				if(itemvo.getInitflag()==3)
				{
					bflag=true;
					break;
				}
			}
		}
		return bflag;
	}
	
	
	/**
	 * 新建审批数据临时表
	 *
	 */
	public void copyDataToSpTempTable(String strWhere,String tableName)
	{
		try
		{
			
			ContentDAO dao=new ContentDAO(this.conn);
			DbWizard dbw=new DbWizard(this.conn);
		//	if(dbw.isExistTable(tableName, false))
				dbw.dropTable(tableName);
			
			String a_sql="";
			strWhere=PubFunc.keyWord_reback(strWhere);
			if(Sql_switcher.searchDbServer()==2)
				a_sql="create table "+tableName+" as select * from salaryhistory where "+strWhere;
			else 
				a_sql="select *  into "+tableName+"  from salaryhistory where "+strWhere;
			dao.update(a_sql);
			DBMetaModel dbmodel=new DBMetaModel(this.conn);
			dbmodel.reloadTableModel(tableName);	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 取得当前审批记录的发起用户
	 * @param strWhere
	 * @return
	 */
	public ArrayList getUsersFromHistory(String strWhere)
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select distinct userflag from salaryhistory where "+strWhere);
			while(rowSet.next())
				list.add(rowSet.getString(1));
			rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	
	
	/**
	 * 审批公式批量计算
	 * @param strWhere
	 * @param itemids
	 * @return
	 * @throws GeneralException
	 */
	public boolean sp_computing(String strWhere,ArrayList itemids,String strYm,String strC)throws GeneralException
	{
		boolean bflag=false;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			/**取得需要的计算公式列表*/
			ArrayList formulalist=this.getFormulaList(itemids);  //this.getFormulaList(1);
			if(formulalist.size()==0)
				return true;
			/**当前处理的到的年月标识和次数*/
			currym=strYm;
			currcount=strC;
			fldvarlist.clear();
			fldvarlist.addAll(this.getMidVariableList());
			fldvarlist.addAll(this.getGzFieldList());
			
			String t_name=this.gz_tablename;
			String tableName="t#"+this.userview.getUserName()+"_gzsp"; //this.userview.getUserName()+"_sp_data";
			this.gz_tablename=tableName;
			copyDataToSpTempTable(strWhere,tableName);
			ArrayList userList=getUsersFromHistory(strWhere);
			
			strWhere=strWhere.replaceAll("salaryhistory", tableName);
			/**把临时变量加到薪资表中去*/
			ArrayList midVariableList=getMidVariableList();
			String pre_str="";
			if(strWhere.length()>0)
				pre_str=" where ";
			addMidVarIntoGzTable(pre_str+strWhere,midVariableList);
			/**标准表数据初始化*/
			addStdFieldIntoGzTable(strWhere);
			/**执行计算公式*/
			secondComputing(formulalist,strWhere);
			
			StringBuffer sql_str=new StringBuffer("update gz_tax_mx set flag=( ");
			sql_str.append(" select 0 from "+tableName+" where gz_tax_mx.salaryid="+tableName+".salaryid ");
			sql_str.append(" and gz_tax_mx.nbase="+tableName+".nbase and gz_tax_mx.a0100="+tableName+".a0100  ");
			sql_str.append(" and gz_tax_mx.a00z0="+tableName+".a00z0 and gz_tax_mx.a00z1="+tableName+".a00z1 ");
			sql_str.append(" and "+tableName+".sp_flag<>'06') where exists ( ");
			sql_str.append(" select null from "+tableName+" where gz_tax_mx.salaryid="+tableName+".salaryid ");
			sql_str.append(" and gz_tax_mx.nbase="+tableName+".nbase and gz_tax_mx.a0100="+tableName+".a0100  ");
			sql_str.append(" and gz_tax_mx.a00z0="+tableName+".a00z0 and gz_tax_mx.a00z1="+tableName+".a00z1 ");
			sql_str.append(" and "+tableName+".sp_flag<>'06' ");
			sql_str.append(" )");
			dao.update(sql_str.toString());
			
			strWhere=strWhere.replaceAll(tableName,"salaryhistory");
			/** 同步薪资历史表 和 相对应的临时表 */
			RecordVo vo=new RecordVo(this.getGz_tablename().toLowerCase());
			StringBuffer sql=new StringBuffer("");
			for(int i=0;i<formulalist.size();i++)
			{
                DynaBean dbean=(LazyDynaBean)formulalist.get(i);
                String formula=(String)dbean.get("rexpr");
                String cond=(String)dbean.get("cond");
                String fieldname=(String)dbean.get("itemname");
                String strStdId=(String)dbean.get("standid");
                /**系统项不用计算*/
                if(isSystemItem(fieldname))
                	continue;
                /**分析左边项是否在工资表中存在*/
                if(!vo.hasAttribute(fieldname.toLowerCase()))
                	continue;
                sql.setLength(0);
                sql.append("update salaryhistory set "+fieldname+"=(");
                sql.append(" select "+fieldname+" from "+tableName+" where salaryhistory.salaryid="+tableName+".salaryid ");
                sql.append(" and salaryhistory.nbase="+tableName+".nbase and salaryhistory.a0100="+tableName+".a0100  ");
                sql.append(" and salaryhistory.a00z0="+tableName+".a00z0 and salaryhistory.a00z1="+tableName+".a00z1 ");
            //    sql.append(" ) where "+strWhere);
                sql.append(" ) where exists ( select null from "+tableName+" where salaryhistory.salaryid="+tableName+".salaryid ");
                sql.append(" and salaryhistory.nbase="+tableName+".nbase and salaryhistory.a0100="+tableName+".a0100  ");
                sql.append(" and salaryhistory.a00z0="+tableName+".a00z0 and salaryhistory.a00z1="+tableName+".a00z1) and  "+strWhere);
                dao.update(sql.toString());
                
                for(int j=0;j<userList.size();j++)
                {
                	String username=(String)userList.get(j);
                	String tb_name=username+"_salary_"+this.salaryid;
                	
                	sql.setLength(0);
                    sql.append("update "+tb_name+" set "+fieldname+"=(");
                    sql.append(" select "+fieldname+" from "+tableName+" where ");
                    sql.append("  "+tb_name+".nbase="+tableName+".nbase and "+tb_name+".a0100="+tableName+".a0100  ");
                    sql.append(" and "+tb_name+".a00z0="+tableName+".a00z0 and "+tb_name+".a00z1="+tableName+".a00z1 ");
                    sql.append(" ) where exists (select null from "+tableName+" where ");
                    sql.append("  "+tb_name+".nbase="+tableName+".nbase and "+tb_name+".a0100="+tableName+".a0100  ");
                    sql.append(" and "+tb_name+".a00z0="+tableName+".a00z0 and "+tb_name+".a00z1="+tableName+".a00z1 ");
                    sql.append(" ) ");
                    dao.update(sql.toString());
                }
			}
			
			
			
			
			
			this.gz_tablename=t_name;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return bflag;
	}
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 公式批量计算
	 * @param strWhere 计算过滤条件也即计算条件范围
	 * @return
	 * @throws GeneralException
	 */
	public boolean computing(String strWhere,ArrayList itemids)throws GeneralException
	{
		boolean bflag=false;
		/**取得需要的计算公式列表*/
		ArrayList formulalist=this.getFormulaList(itemids);  //this.getFormulaList(1);
		if(formulalist.size()==0)
			return true;
		//getYearMonthCount("01");
		getYearMonthCount2();   //dengcan
		
		/**加载一次,每次计算都加载吧*/
		//if(fldvarlist.size()==0)
		//{
			fldvarlist.clear();
			fldvarlist.addAll(this.getMidVariableList());
			fldvarlist.addAll(this.getGzFieldList());
		//}
		try
		{
			/**把临时变量加到薪资表中去*/
			ArrayList midVariableList=getMidVariableList();
			addMidVarIntoGzTable(strWhere,midVariableList);
			/**标准表数据初始化*/
			if(strWhere.length()==0)
				addStdFieldIntoGzTable(strWhere);
			else
				addStdFieldIntoGzTable(strWhere.substring(6));
			/**执行计算公式*/
			
			if(strWhere.length()==0)
				secondComputing(formulalist,strWhere);
			else
				secondComputing(formulalist,strWhere.substring(6));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return bflag;
	}
	
	
	/**
	 * 首次计算
	 * @param strWhere 条件
	 * @param strPre   应用库前缀
	 * @param bMain    =true 人员主集指标也导，=false 人员主集指标不导
	 * @param exceptFlds 不导数据的指标 暂时未用到
	 * @return
	 */
	public boolean firstComputing(String strWhere,String strPre,boolean bMain,ArrayList exeptFlds)throws GeneralException
	{
		  
		boolean bflag=true;
		int ninit=0;
		try
		{
			ArrayList allUsedFields = DataDictionary.getAllFieldItemList(
					Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
			FieldItem Field=null;
			HashMap allFieldMap=new HashMap();
			
			HashMap fieldMap=new HashMap();
			GzItemVo _itemvo=null;
			for(int i=0;i<this.gzitemlist.size();i++)
			{
				_itemvo=(GzItemVo)this.gzitemlist.get(i);
				fieldMap.put(_itemvo.getHz().toLowerCase(),_itemvo);
			}
			
			for(int i=0;i<allUsedFields.size();i++)
			{
				Field = (FieldItem)((FieldItem) allUsedFields.get(i)).cloneItem();
				String desc=Field.getItemdesc().trim().toLowerCase();
				if(fieldMap.get(desc)!=null)
				{
					_itemvo=(GzItemVo)fieldMap.get(desc);
					Field.setFieldsetid(_itemvo.getSetname());
				}
				allFieldMap.put(desc,Field);
			}
	
			//将导入当前记录数据的项目先批量处理
			ArrayList itemList=new ArrayList();
			HashMap setMap=new HashMap();
			for(int i=0;i<gzitemlist.size();i++)
			{
				GzItemVo itemvo=(GzItemVo)gzitemlist.get(i);
				String setid=itemvo.getSetname();
				/**变量不处理*/
				if(itemvo.getIsvar()==1)
					continue;
				if(!bMain&& "A01".equalsIgnoreCase(setid))
					continue;
				/**单位指标或职位指标*/
				if(setid.charAt(0)=='A')
				{
					int nlock=itemvo.getLock();
					int ainit=itemvo.getInitflag();
					int nheap=itemvo.getHeapflag();
					String formula=itemvo.getFormula();
					/**=0不锁,=1锁住    2:导入项  */
					if(nlock==0&&nheap==0&&(ainit==1||ainit==2))
					{
						if(allFieldMap.get(formula.trim().toLowerCase())!=null||DataDictionary.getFieldItem(formula.trim())!=null)
						{
							FieldItem field=(FieldItem)allFieldMap.get(formula.trim().toLowerCase());
							if(field==null)
								field=(FieldItem)DataDictionary.getFieldItem(formula.trim());
							String a_setid=field.getFieldsetid();
							
							
							if(setMap.get(a_setid.toUpperCase())!=null)
							{
								ArrayList tempList=(ArrayList)setMap.get(a_setid.toUpperCase());
								tempList.add(itemvo.getFldname()+"`"+field.getItemid());
								setMap.put(a_setid.toUpperCase(), tempList);
							}
							else
							{
								ArrayList tempList=new ArrayList();
								tempList.add(itemvo.getFldname()+"`"+field.getItemid());
								setMap.put(a_setid.toUpperCase(), tempList);
							}
						}
						else
							itemList.add(itemvo);
					}
					else
					{
						itemList.add(itemvo);
					}
				}
				else
					itemList.add(itemvo);
			}
			batchImportGzItems(setMap,strWhere,strPre);
			
			boolean isE01A1=false; //是否有岗位名称
			for(int i=0;i<gzitemlist.size();i++)
			{
				GzItemVo itemvo=(GzItemVo)gzitemlist.get(i);
				String fldname=itemvo.getFldname();
				if("E01A1".equalsIgnoreCase(fldname))
					isE01A1=true;
			}
			if(isE01A1)
			{
				ContentDAO dao=new ContentDAO(this.conn);
				String sql="update "+gz_tablename+" set E01A1=(select E01A1 from "+strPre+"A01 where "+strPre+"A01.a0100="+gz_tablename+".a0100  ) where exists ";
				sql+=" (select null from "+strPre+"A01 where "+strPre+"A01.a0100="+gz_tablename+".a0100  ) and lower(nbase)='"+strPre.toLowerCase()+"' ";
				if(strWhere!=null&&strWhere.trim().length()>0)
					sql+=" and "+strWhere;
				dao.update(sql);
			}
			
			
			
			for(int i=0;i<itemList.size();i++)
			{
				GzItemVo itemvo=(GzItemVo)itemList.get(i);
				String setid=itemvo.getSetname();
				/**变量不处理*/
				if(itemvo.getIsvar()==1)
					continue;
				if(!bMain&& "A01".equalsIgnoreCase(setid))
					continue;
				/**单位指标或职位指标*/
				if(setid.charAt(0)!='A')
				{
					computingImportUnitItem(itemvo,strWhere,strPre,isE01A1);
					//...
				}
				else//人员库
				{
					/**=0不锁,=1锁住*/
					int nlock=itemvo.getLock();
					if(nlock==0)
					{
						ninit=itemvo.getInitflag();
						switch(ninit)
						{
						case 0://清零项，不管它
							break;
						case 1:  //累积项
							computingImportItem(itemvo,strWhere,strPre);						
							break;
						case 2:  //导入项
							computingImportItem(itemvo,strWhere,strPre);
							break;
						}
					}// nlock end.
				}
			}//for i loop end.
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			bflag=false;
			throw GeneralExceptionHandler.Handle(ex);
		}
		
		return bflag;
	}
	
	
	
	/**
	 * 批量导入当前记录数据的项目
	 * @param setMap
	 */
	public boolean batchImportGzItems(HashMap setMap,String strWhere,String strPre) throws GeneralException
	{
		boolean bflag=true;
		try
		{
			Set set=setMap.keySet();
			DbWizard dbw=new DbWizard(this.conn);
			for(Iterator t=set.iterator();t.hasNext();)
			{
				String key=(String)t.next();
				ArrayList itemList=(ArrayList)setMap.get(key);
				/**过滤掉K..,B..开头的指标项目,chenmengqing added at 20100418*/
				if(!(key.charAt(0)=='A'))
					continue;
				/**end*/				
				String tablename=strPre+key;
				StringBuffer updStr=new StringBuffer("");
				for(int i=0;i<itemList.size();i++)
				{
					String temp=(String)itemList.get(i);
					String[] temps=temp.split("`");
					updStr.append("`"+gz_tablename+"."+temps[0]+"="+tablename+"."+temps[1]);
				}
				
				StringBuffer buf=new StringBuffer("");
				if(strWhere.length()==0)
				{
					buf.append(" upper(nbase)='");
					buf.append(strPre.toUpperCase());
					buf.append("'");
				}
				else
				{
					buf.append(strWhere);
					buf.append(" and upper(nbase)='");
					buf.append(strPre.toUpperCase());
					buf.append("'");
				}
				
				//gby,根据人员筛选条件，限制'重新导入'范围
				if(!("".equalsIgnoreCase(this.screeningWhereSql) || this.screeningWhereSql == null))
					buf.append(this.getScreeningWhereSql());
				
				String srcTab=tablename;
				if(!"A01".equalsIgnoreCase(key))
				{
					srcTab="(select * from "+tablename+" a where a.i9999=(select max(b.i9999) from "+tablename+" b where a.a0100=b.a0100  ) ) "+tablename;
				}
				String joinStr=gz_tablename+".A0100="+tablename+".A0100";
			//	long a=System.currentTimeMillis();
				dbw.updateRecord(gz_tablename,srcTab,joinStr,updStr.substring(1), buf.toString(),buf.toString());
			//	System.out.println(tablename+" : "+(System.currentTimeMillis()-a));
			}
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			bflag=false;
			throw GeneralExceptionHandler.Handle(ex);
		}
		return bflag;
	}
	
	
	
	
	
	
	
	/**
	 * 数值类型进行转换
	 * @param type
	 * @return
	 */
	private int getDataType(String type)
	{
		int datatype=0;
		switch(type.charAt(0))
		{
		case 'A':  
			datatype=YksjParser.STRVALUE;
			break;
		case 'M':  
			datatype=YksjParser.STRVALUE;
			break;
		case 'D':
			datatype=YksjParser.DATEVALUE;
			break;
		case 'N':
			datatype=YksjParser.FLOAT;
			break;
		}
		return datatype;
	}
	/**
	 * 计算单位导入项和累积项
	 * @param itemvo
	 * @param strWhere
	 * @param strPre
	 * @param isE01A1 是否已导入岗位名称数据
	 * @return
	 * @throws GeneralException
	 */
	private boolean computingImportUnitItem(GzItemVo itemvo,String strWhere,String strPre,boolean isE01A1)throws GeneralException
	{
		boolean bflag=true;
		try
		{
			String nbase=strPre;
			strPre="";
			String setid=itemvo.getSetname();
			/**单位指标或职位指标  20150215 dengcan*/
		//	if(setid.charAt(0)=='K'&&!isE01A1)
		//		return bflag;
			
			StringBuffer buf=new StringBuffer();
			ContentDAO dao=new ContentDAO(this.conn);
			String tablename="t#"+this.userview.getUserName()+"_gz"; //this.userview.getUserName()+"midtable";
			DbWizard dbw=new DbWizard(this.conn);
			String formula=itemvo.getFormula();
			String fldname=itemvo.getFldname();
			String fldtype=itemvo.getFldtype();

			/**公式计算*/
			ArrayList usedlist=initUsedFields();
			ArrayList allUsedFields = DataDictionary.getAllFieldItemList(
					Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
			YksjParser yp =null;
			if(setid.charAt(0)=='K')
				yp=new YksjParser(this.userview, allUsedFields,
						YksjParser.forSearch, getDataType(fldtype), YksjParser.forPosition, "Ht", strPre);
			else
				yp=new YksjParser(this.userview, allUsedFields,
					YksjParser.forSearch, getDataType(fldtype), YksjParser.forUnit, "Ht", strPre);
			yp.setCon(this.conn);
			ArrayList fieldlist=null;
			try
			{
				fieldlist=yp.getFormulaFieldList(formula);
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
				return bflag;
			}
			
			
			//yp.run(formula);
			/**追加公式中使用的指标*/
			appendUsedFields(fieldlist,usedlist);
			/**增加一个计算公式用的临时字段*/
			FieldItem fielditem=new FieldItem("A01","AAAAA");
			fielditem.setItemdesc("AAAAA");
			fielditem.setCodesetid("0");
			fielditem.setItemtype(fldtype);
			fielditem.setItemlength(itemvo.getLen());
			fielditem.setDecimalwidth(itemvo.getFlddec()/*原参数:0*/);  // FixBug:0033205 导入单位指标无小数问题
			yp.setTargetFieldDecimal(fielditem.getDecimalwidth());     // FixBug:0033205
			usedlist.add(fielditem);
			/**创建计算用临时表*/
			if(setid.charAt(0)=='K')
			{
				fielditem=new FieldItem("A01","E01A1");
				fielditem.setItemdesc("职位名称");
				fielditem.setCodesetid("@K");
				fielditem.setItemtype("A");
				fielditem.setItemlength(30);
				fielditem.setDecimalwidth(0);
				usedlist.add(fielditem);
				if(createMidTable(usedlist,tablename,"E01A1"))
				{
					/**导入单位主集数据B0110*/
					buf.setLength(0);
					buf.append("insert into ");
					buf.append(tablename);
					buf.append("(E01A1) select E01A1 FROM K01");
					dao.update(buf.toString());
				}// 创建临时表结束.
			}
			else
			{
				if(createMidTable(usedlist,tablename,"B0110"))
				{
					/**导入单位主集数据B0110*/
					buf.setLength(0);
					buf.append("insert into ");
					buf.append(tablename);
					buf.append("(B0110) select B0110 FROM B01");
					dao.update(buf.toString());
				}// 创建临时表结束.
			}
			String stry=currym.substring(0, 4);
			String strm=currym.substring(5, 7);
			String strc=currcount;
			YearMonthCount ymc=new YearMonthCount(Integer.parseInt(stry),Integer.parseInt(strm),Integer.parseInt(strc));
			yp.run(formula,ymc,"AAAAA",tablename,dao,"",this.conn,fldtype,fielditem.getItemlength(),1,itemvo.getCodeid());
			
			if(setid.charAt(0)=='K')
			{
				if(!isE01A1) //薪资类别没有引入岗位指标时通过人员库信息匹配
				{ 
					StringBuffer sql=new StringBuffer("update "+gz_tablename+" set "+fldname+"=(select  "+tablename+".AAAAA from "+tablename);
					sql.append(","+nbase+"a01 where "+gz_tablename+".a0100="+nbase+"a01.a0100 and  "+nbase+"a01.E01A1="+tablename+".E01A1 and lower("+gz_tablename+".nbase)='"+nbase.toLowerCase()+"' ) where exists (select null from "+tablename);
					sql.append(","+nbase+"a01 where "+gz_tablename+".a0100="+nbase+"a01.a0100 and  "+nbase+"a01.E01A1="+tablename+".E01A1 and lower("+gz_tablename+".nbase)='"+nbase.toLowerCase()+"' ) and lower("+gz_tablename+".nbase)='"+nbase.toLowerCase()+"' ");
					if(strWhere!=null&&strWhere.length()>0)
						sql.append(" and "+strWhere);
					dbw.execute(sql.toString());
				}
				else
				{
					StringBuffer sql=new StringBuffer("update "+gz_tablename+" set "+fldname+"=(select  "+tablename+".AAAAA from "+tablename);
					sql.append(" where "+gz_tablename+".E01A1="+tablename+".E01A1 ) where exists (select null from "+tablename);
					sql.append(" where "+gz_tablename+".E01A1="+tablename+".E01A1 ) ");
					if(strWhere!=null&&strWhere.length()>0)
						sql.append(" and "+strWhere);
					dbw.execute(sql.toString());
				}
			}
			else
			{
			//	dbw.updateRecord(gz_tablename,tablename,gz_tablename+".B0110="+tablename+".B0110", gz_tablename+"."+fldname+"="+tablename+".AAAAA", "","("+gz_tablename+"."+fldname+" IS NULL)");
			//	dbw.updateRecord(gz_tablename,tablename,gz_tablename+".E0122="+tablename+".B0110", gz_tablename+"."+fldname+"="+tablename+".AAAAA","",/*strWhere.toString()*/"");
			/*	StringBuffer sql=new StringBuffer("update "+gz_tablename+" set "+fldname+"=(select  "+tablename+".AAAAA from "+tablename);
				sql.append(" where "+gz_tablename+".B0110="+tablename+".B0110 ) where exists (select null from "+tablename);
				sql.append(" where "+gz_tablename+".B0110="+tablename+".B0110 ) ");
				if(strWhere!=null&&strWhere.length()>0)
					sql.append(" and "+strWhere);
				dbw.execute(sql.toString());
				sql.setLength(0);
				sql.append("update "+gz_tablename+" set "+fldname+"=(select  "+tablename+".AAAAA from "+tablename);
				sql.append(" where "+gz_tablename+".E0122="+tablename+".B0110 ) where exists (select null from "+tablename);
				sql.append(" where "+gz_tablename+".E0122="+tablename+".B0110 ) ");
				if(strWhere!=null&&strWhere.length()>0)
					sql.append(" and "+strWhere);
				dbw.execute(sql.toString()); */
				// 先处理部门，再处理单位，即，部门有值用部门值，部门没值用单位值。(FixBug0033282)
				StringBuffer sql=new StringBuffer("update "+gz_tablename+" set "+fldname+"=NULL");
				if(strWhere!=null&&strWhere.length()>0)
					sql.append(" where "+strWhere);
				dbw.execute(sql.toString());
				// 部门
				String cond=null;
				if("N".equalsIgnoreCase(fldtype))
					cond="AAAAA<>0";
				else if("D".equalsIgnoreCase(fldtype))
					cond="NOT AAAAA IS NULL";
				else
				{
					if(Sql_switcher.searchDbServer() < Constant.ORACEL)
						cond="AAAAA<>''";
					else
						cond="NOT AAAAA IS NULL";
				}
				sql.setLength(0);
				sql.append("update "+gz_tablename+" set "+fldname+"="+
						     "(select  "+tablename+".AAAAA from "+tablename+
				             " where "+gz_tablename+".E0122="+tablename+".B0110 and "+cond+")"+
				           " where exists (select null from "+tablename+
				                          " where "+gz_tablename+".E0122="+tablename+".B0110)");
				if(strWhere!=null&&strWhere.length()>0)
					sql.append(" and "+strWhere);
				dbw.execute(sql.toString());

				// 单位
				cond=gz_tablename+"."+fldname+" IS NULL";
				sql.setLength(0);
				sql.append("update "+gz_tablename+" set "+fldname+"="+
						        "(select  "+tablename+".AAAAA from "+tablename+
				                " where "+gz_tablename+".B0110="+tablename+".B0110)"+
				           " where "+cond+" and exists (select null from "+tablename+
				                          " where "+gz_tablename+".B0110="+tablename+".B0110)");
				if(strWhere!=null&&strWhere.length()>0)
					sql.append(" and "+strWhere);
				dbw.execute(sql.toString());				
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			bflag=false;
			throw GeneralExceptionHandler.Handle(ex);
		}
		return bflag;		
	}
	/**
	 * 计算人员导入项和累积项
	 * @param itemvo
	 * @param strWhere
	 * @param strPre
	 * @return
	 */
	private boolean computingImportItem(GzItemVo itemvo,String strWhere,String strPre)throws GeneralException
	{
		boolean bflag=true;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String tablename="t#"+this.userview.getUserName()+"_gz"; //this.userview.getUserName()+"midtable";
			DbWizard dbw=new DbWizard(this.conn);
			int ninit=itemvo.getInitflag();
			String formula=itemvo.getFormula();
			String fldname=itemvo.getFldname();
			String setid=itemvo.getSetname();
			String fldtype=itemvo.getFldtype();
		
			String stry=currym.substring(0, 4);
			String strm=currym.substring(5, 7);
			int nM=Integer.parseInt(strm);
			int nheap=itemvo.getHeapflag();
			String axxz1=null;
			String axxz0=null;
			String strQ=null;
			if((nM>=1) && (nM<=3))
				strQ="1";
			else if((nM>=4) && (nM<=6))
				strQ="2";
			else if((nM>=7) && (nM<=9))
				strQ="3";
			else
				strQ="4";
			StringBuffer buf=new StringBuffer();
			
			
			ArrayList allUsedFields = DataDictionary.getAllFieldItemList(
					Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
			YksjParser yp = new YksjParser(this.userview, allUsedFields,
					YksjParser.forSearch, getDataType(fldtype), YksjParser.forPerson, "Ht", strPre);
			yp.setCon(this.conn);
			
			
			/**对累积项,组成分析用的计算公式*/
			if(ninit==1)
			{
				axxz1=setid+"Z1";//所属次数指标
				axxz0=setid+"Z0";//所属期指标
				switch(nheap)
				{
				case 0://不累积
					break;
				case 1://月内累积
					buf.append("select(");
					buf.append(formula);
					buf.append(",Month(");
					buf.append(axxz0);
					buf.append(")=");
					buf.append(strm);
					buf.append(" and year(");
					buf.append(axxz0);
					buf.append(")=");
					buf.append(stry);
					buf.append(",SUM)");
					break;
				case 2://季度内累积
					buf.append("select(");
					buf.append(formula);
					buf.append(",QUARTER(");
					buf.append(axxz0);
					buf.append(")=");
					buf.append(strQ);
					buf.append(" and year(");
					buf.append(axxz0);
					buf.append(")=");
					buf.append(stry);
					buf.append(",SUM)");					
					break;
				case 3://年内累积
					buf.append("select(");
					buf.append(formula);
					buf.append(",year(");
					buf.append(axxz0);
					buf.append(")=");
					buf.append(stry);
					buf.append(",SUM)");
					break;
				case 4://无条件累积
					buf.append("select(");
					buf.append(formula);
					buf.append(",1=1");
					buf.append(",SUM)");					
					break;
				case 5://季度内同次累积
					buf.append("select(");
					buf.append(formula);
					buf.append(",QUARTER(");
					buf.append(axxz0);
					buf.append(")=");
					buf.append(strQ);
					buf.append(" and year(");
					buf.append(axxz0);
					buf.append(")=");
					buf.append(stry);	
					buf.append(" and ");
					buf.append(axxz1);
					buf.append("=");
					buf.append(currcount);	
					buf.append(",SUM)");
					break;
				case 6://年内同次累积
					buf.append("select(");
					buf.append(formula);
					buf.append(",year(");
					buf.append(axxz0);
					buf.append(")=");
					buf.append(stry);	
					buf.append(" and ");
					buf.append(axxz1);
					buf.append("=");
					buf.append(currcount);	
					buf.append(",SUM)");					
					break;
				case 7://同次累积
					buf.append("select(");
					buf.append(formula);
					buf.append(",");
					buf.append(axxz1);
					buf.append("=");
					buf.append(currcount);	
					buf.append(",SUM)");						
					break;
				case 8://小于本次的月内累积
					buf.append("select(");
					buf.append(formula);
					buf.append(",Month(");
					buf.append(axxz0);
					buf.append(")=");
					buf.append(strm);
					buf.append(" and year(");
					buf.append(axxz0);
					buf.append(")=");
					buf.append(stry);
					buf.append(" and ");
					buf.append(axxz1);
					buf.append("<");
					buf.append(currcount);
					buf.append(",SUM)");					
					break;
				}
				formula=buf.toString();
			}//if ninit end.
			else if(ninit==2)
			{
				
				axxz1=setid+"Z1";//所属次数指标
				axxz0=setid+"Z0";//所属期指标
				if(nheap==1||nheap==2||nheap==3||nheap==4)
				{
					try
					{
						ArrayList a_fieldlist=yp.getFormulaFieldList(formula);
						HashSet aset=new HashSet();
						String asetid="";
						for(int i=0;i<a_fieldlist.size();i++)
						{
							FieldItem field=(FieldItem)a_fieldlist.get(i);
							aset.add(field.getFieldsetid());
							asetid=field.getFieldsetid();
						}
						if(aset.size()==1)
						{
							axxz1=asetid+"Z1";//所属次数指标
							axxz0=asetid+"Z0";//所属期指标
						}
					}
					catch(Exception ee)
					{
						ee.printStackTrace();
						return bflag;
					}
				
				}
				
				switch(nheap)
				{
					case 0:// 当前记录
						break;
					case 1:  // 月内最初第一条
						formula="SELECT("+formula+",Month("+axxz0+")="+strm+" AND Year("+axxz0+")="+stry+",FIRST)";
						 break;
					case 2:  // 月内最近第一条
						formula="SELECT("+formula+",Month("+axxz0+")="+strm+" AND Year("+axxz0+")="+stry+",LAST)";
						 break;
					case 3:  // 小于本次月内最初第一条
						formula="SELECT("+formula+",Month("+axxz0+")="+strm+" AND Year("+axxz0+")="+stry
						                          +" AND "+axxz1+"<"+currcount+",FIRST)";
						break;
					case 4:  // 小于本次月内最近第一条
						formula="SELECT("+formula+",Month("+axxz0+")="+strm+" AND Year("+axxz0+")="+stry
													+" AND "+axxz1+"<"+currcount+",LAST)";
						break;
				}
				
			}
			/**公式计算*/
			ArrayList usedlist=initUsedFields();
			
			ArrayList fieldlist=null;
			try
			{
				fieldlist=yp.getFormulaFieldList(formula);
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
				return bflag;
			}
			
			/**追加公式中使用的指标*/
			appendUsedFields(fieldlist,usedlist);
			/**增加一个计算公式用的临时字段*/
			FieldItem fielditem=new FieldItem("A01","AAAAA");
			fielditem.setItemdesc("AAAAA");
			fielditem.setCodesetid("0");
			fielditem.setItemtype(fldtype);
			fielditem.setItemlength(itemvo.getLen());
			fielditem.setDecimalwidth(itemvo.getFlddec());
			usedlist.add(fielditem);
			yp.setTargetFieldDecimal(fielditem.getDecimalwidth());
			/**创建计算用临时表*/
			if(createMidTable(usedlist,tablename,"A0100"))
			{
				/**导入人员主集数据A0100,A0000,B0110,E0122,A0101*/
				buf.setLength(0);
				buf.append("insert into ");
				buf.append(tablename);
				buf.append("(A0000,A0100,B0110,E0122,A0101) select A0000,A0100,B0110,E0122,A0101 FROM ");
				buf.append(strPre+"A01");
				buf.append(" where exists (select A0100 from ");
				buf.append(this.gz_tablename);
				if(strWhere.length()==0)
				{
					buf.append(" where upper(nbase)='");
					buf.append(strPre.toUpperCase());
					buf.append("'");
				}
				else
				{
					buf.append(" where ");					
					buf.append(strWhere);
					buf.append(" and upper(nbase)='");
					buf.append(strPre.toUpperCase());
					buf.append("'");
				}
				buf.append(" and "+strPre+"A01.a0100="+this.gz_tablename+".a0100 ");
				
				buf.append(")");
				dao.update(buf.toString());
			}// 创建临时表结束.
			/**执行返回的SQLS*/

			YearMonthCount ymc=new YearMonthCount(Integer.parseInt(stry),Integer.parseInt(strm),Integer.parseInt(currcount));
			
		//	if(fldtype.equals("D"))
		//			System.out.println(formula+"   "+fldname+"  "+setid+"  "+fldtype);
			if(("归属日期()".equalsIgnoreCase(formula.trim())|| "归属日期".equalsIgnoreCase(formula.trim()))&& "D".equalsIgnoreCase(fldtype))
			{
				Calendar d=Calendar.getInstance();
				d.set(Calendar.YEAR,ymc.getYear());
				d.set(Calendar.MONTH,ymc.getMonth()-1);
				d.set(Calendar.DATE,1);
				java.sql.Date dd=new java.sql.Date(d.getTimeInMillis());
				ArrayList paramList = new ArrayList();
				paramList.add(dd);
				dao.update("update "+tablename+" set AAAAA=? ",paramList);
			}
			else
			{
				//importMenSql_where="";
				if(nheap==5&&ninit==2) //导入公式 同月同次
					yp.setYearMonthCount(true);
				yp.run(formula,ymc,"AAAAA",tablename,dao,importMenSql_where,this.conn,fldtype,fielditem.getItemlength(),1,itemvo.getCodeid());
				yp.setYearMonthCount(false);
			}

			
			buf.setLength(0);
			if(strWhere.length()==0)
			{
				buf.append(" upper(nbase)='");
				buf.append(strPre.toUpperCase());
				buf.append("'");
			}
			else
			{
				buf.append(strWhere);
				buf.append(" and upper(nbase)='");
				buf.append(strPre.toUpperCase());
				buf.append("'");
			}
			
			//String a_buf=buf.toString().replace("where"," ");   //dengcan  2008-02-03  添加where,报错
			//String strcond=buf.substring(6);
			dbw.updateRecord(gz_tablename,tablename,gz_tablename+".A0100="+tablename+".A0100", gz_tablename+"."+fldname+"="+tablename+".AAAAA", buf.toString(),buf.toString());
	
			 if(ninit==2&&nheap==6) //导入项|扣减同月已发金额
			 {
				// 多发的说法
				 StringBuffer sql_str=new StringBuffer("");
				 sql_str.append("update "+gz_tablename+" set "+fldname+"=(select ("+gz_tablename+"."+fldname+"-"+Sql_switcher.isnull("a."+fldname, "0")+") ");
				 sql_str.append(" from ( select sum(salaryhistory."+fldname+") as "+fldname+",a0100,nbase from salaryhistory where salaryid="+this.salaryid+" "); 
				 sql_str.append(" and "+Sql_switcher.year("a00z2")+"="+stry+" and "+Sql_switcher.month("a00z2")+"="+nM+" and sp_flag='06'  group  by a0100,nbase   ) a ");
				 sql_str.append(" where a.a0100="+gz_tablename+".a0100 and lower(a.nbase)=lower("+gz_tablename+".nbase) ) ");
				 sql_str.append("  where exists (  select null  from ( ");
				 sql_str.append(" select sum(salaryhistory."+fldname+") as "+fldname+",a0100,nbase from salaryhistory where salaryid="+this.salaryid+"  ");
				 sql_str.append(" and "+Sql_switcher.year("a00z2")+"="+stry+" and "+Sql_switcher.month("a00z2")+"="+nM+" and sp_flag='06'  group  by a0100,nbase   ) a ");
				 sql_str.append("  where a.a0100="+gz_tablename+".a0100 and lower(a.nbase)=lower("+gz_tablename+".nbase) ");
				 sql_str.append("  ) ");
				 dbw.execute(sql_str.toString());
			 }
		
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			bflag=false;
			throw GeneralExceptionHandler.Handle(ex);
		}
		return bflag;
	}
	/**
	 * 创建计算用的临时表
	 * @param fieldlist
	 * @param tablename
	 * @param keyfield
	 * @return
	 */
	private boolean createMidTable(ArrayList fieldlist,String tablename,String keyfield)
	{
		boolean bflag=true;
		try
		{
			DbWizard dbw=new DbWizard(this.conn);
		//	if(dbw.isExistTable(tablename, false))
				dbw.dropTable(tablename);
			Table table=new Table(tablename);
			for(int i=0;i<fieldlist.size();i++)
			{
				FieldItem fielditem=(FieldItem)fieldlist.get(i);
				Field field=fielditem.cloneField();
				if(field.getName().equalsIgnoreCase(keyfield))
				{
					field.setNullable(false);
					field.setKeyable(true);
				}
				table.addField(field);
			}//for i loop end.
			Field field=new Field("userflag","userflag");
			field.setLength(50);
			field.setDatatype(DataType.STRING);
			table.addField(field);
			dbw.createTable(table);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			bflag=false;
		}
		return bflag;
	}
	/**
	 * 追加不同的指标
	 * @param slist
	 * @param dlist
	 */
	private void appendUsedFields(ArrayList slist,ArrayList dlist)
	{
		boolean bflag=false;
		for(int i=0;i<slist.size();i++)
		{
			FieldItem fielditem=(FieldItem)slist.get(i);
			String itemid=fielditem.getItemid();
			for(int j=0;j<dlist.size();j++)
			{
				bflag=false;
				FieldItem fielditem0=(FieldItem)dlist.get(j);
				String ditemid=fielditem0.getItemid();
				if(itemid.equalsIgnoreCase(ditemid))
				{
					bflag=true;
					break;
				}

			}//for j loop end.
			if(!bflag)
				dlist.add(fielditem);			
		}//for i loop end.
	}
	
	/**
	 * 初始设置使用字段列表
	 * @return
	 */
	private ArrayList initUsedFields()
	{
		ArrayList fieldlist=new ArrayList();
		/**人员排序号*/
		FieldItem fielditem=new FieldItem("A01","A0000");
		fielditem.setItemdesc("a0000");
		fielditem.setCodesetid("0");
		fielditem.setItemtype("N");
		fielditem.setItemlength(9);
		fielditem.setDecimalwidth(0);
		fieldlist.add(fielditem);
		/**人员编号*/
		fielditem=new FieldItem("A01","A0100");
		fielditem.setItemdesc("a0100");
		fielditem.setCodesetid("0");
		fielditem.setItemtype("A");
		fielditem.setItemlength(8);
		fielditem.setDecimalwidth(0);
		fieldlist.add(fielditem);
		/**单位名称*/
		fielditem=new FieldItem("A01","B0110");
		fielditem.setItemdesc("单位名称");
		fielditem.setCodesetid("UN");
		fielditem.setItemtype("A");
		fielditem.setItemlength(30);
		fielditem.setDecimalwidth(0);
		fieldlist.add(fielditem);
		/**姓名*/
		fielditem=new FieldItem("A01","A0101");
		FieldItem item=DataDictionary.getFieldItem("a0101");
		fielditem.setItemdesc("姓名");
		fielditem.setCodesetid("0");
		fielditem.setItemtype("A");
		fielditem.setItemlength(item.getItemlength());
		fielditem.setDecimalwidth(0);
		fieldlist.add(fielditem);
		/**人员排序号*/
		fielditem=new FieldItem("A01","I9999");
		fielditem.setItemdesc("I9999");
		fielditem.setCodesetid("0");
		fielditem.setItemtype("N");
		fielditem.setItemlength(9);
		fielditem.setDecimalwidth(0);
		fieldlist.add(fielditem);
		/**部门名称*/
		fielditem=new FieldItem("A01","E0122");
		fielditem.setItemdesc("部门");
		fielditem.setCodesetid("UM");
		fielditem.setItemtype("A");
		fielditem.setItemlength(30);
		fielditem.setDecimalwidth(0);
		fieldlist.add(fielditem);		
		return fieldlist;
	}
	/**
	 * 从薪资表中删除档案库中不存在的人员
	 * @throws GeneralException
	 */
	private void removeDelManData()throws GeneralException
	{
		StringBuffer buf=new StringBuffer();
		String tablename="t#"+this.userview.getUserName()+"_gz_Dec";
		DbSecurityImpl dbS = new DbSecurityImpl();
		RowSet rowSet = null;
		PreparedStatement ps = null;
		try
		{
			int rows=getRows(tablename);
			if(rows==0)
				return;				
			buf.append("delete from ");
			buf.append(this.gz_tablename);
			buf.append(" where exists(select * from ");
			buf.append(tablename);
			buf.append(" where state='1' and upper(");
			buf.append(this.gz_tablename);
			buf.append(".nbase)=upper(");
			buf.append(tablename);
			buf.append(".dbname) and ");
			buf.append(this.gz_tablename);
			buf.append(".A0100=");
			buf.append(tablename);
			buf.append(".A0100)");
			ContentDAO dao=new ContentDAO(this.conn);
			
			
			
			/** 总额计算  */
			ArrayList dateList=new ArrayList();
			SalaryTotalBo bo=new SalaryTotalBo(this.conn,this.userview,String.valueOf(salaryid));
			SalaryCtrlParamBo ctrlparam=new SalaryCtrlParamBo(this.conn,salaryid);
			String isControl=ctrlparam.getValue(SalaryCtrlParamBo.AMOUNT_CTRL,"flag");   //该工资类别是否进行总额控制
			if("1".equals(isControl))
			{
				StringBuffer sql=new StringBuffer("");
				sql.append(" select distinct "+this.gz_tablename+".a00z0,"+this.gz_tablename+".a00z1 from "+tablename+","+this.gz_tablename+" where upper("+this.gz_tablename+".nbase)=upper("+tablename+".dbname) ");
				sql.append(" and "+this.gz_tablename+".A0100="+tablename+".A0100 ");
				sql.append(" and "+tablename+".state='1'  and "+this.gz_tablename+".sp_flag='07'  ");
				rowSet=dao.search(sql.toString());
				Calendar d=Calendar.getInstance();
				HashSet dateSet=new HashSet();
				while(rowSet.next())
				{
					d.setTime(rowSet.getDate(1));
					int a00z1=rowSet.getInt(2);
					dateSet.add(d.get(Calendar.YEAR)+"-"+(d.get(Calendar.MONTH)+1)+"-"+a00z1);
				}
				
				StringBuffer buf2=new StringBuffer("");
				buf2.append(" and  exists(select * from ");
				buf2.append(tablename);
				buf2.append(" where state='1' and upper(");
				buf2.append(this.gz_tablename);
				buf2.append(".nbase)=upper(");
				buf2.append(tablename);
				buf2.append(".dbname) and ");
				buf2.append(this.gz_tablename);
				buf2.append(".A0100=");
				buf2.append(tablename);
				buf2.append(".A0100 and "+this.gz_tablename+".sp_flag='07'   )");
				
				dateList=bo.getDateList(buf2.toString(),dateSet,false);
			}
			
			//删除税率明细 
			StringBuffer buf1=new StringBuffer("");
			buf1.append("delete from gz_tax_mx where salaryid="+salaryid+" and lower(nbase)=? and a0100=? and "+Sql_switcher.year("a00z0")+"=? and "+Sql_switcher.month("a00z0")+"=? and a00z1=?");
	    	if(manager.length()>0&&!this.userview.getUserName().equalsIgnoreCase(manager))
	    		buf1.append(" and ( lower(userflag)='"+manager.toLowerCase()+"' or userflag is null )");
	    	else
	    		buf1.append(" and ( lower(userflag)='"+this.userview.getUserName().toLowerCase()+"' or userflag is null )");
	    	
	    	String sub_str="select * from "+this.gz_tablename+" where exists(select * from "+tablename+" where state='1' and upper("+this.gz_tablename+".nbase)=upper(";
	    	sub_str+=tablename+".dbname) and "+this.gz_tablename+".A0100="+tablename+".A0100)";
	    	ps=this.conn.prepareStatement(buf1.toString());
	    	rowSet=dao.search(sub_str);
	    	while(rowSet.next())
	    	{
	    		 
	    		Calendar d=Calendar.getInstance();
	    		d.setTime(rowSet.getDate("a00z0")); 
	    		ps.setString(1,rowSet.getString("nbase").toLowerCase());
	    		ps.setString(2,rowSet.getString("a0100"));
	    		ps.setInt(3, d.get(Calendar.YEAR));
	    		ps.setInt(4, (d.get(Calendar.MONTH)+1));
	    		ps.setInt(5,rowSet.getInt("a00z1"));
	    		ps.addBatch();
	    	}
	    	
	    	// 打开Wallet
			dbS.open(conn, buf1.toString());
	    	ps.executeBatch();
	    	 
			dao.update(buf.toString());
			
			//同步薪资发放数据的映射表
			buf.setLength(0);
			String username=this.userview.getUserName().toLowerCase();
			if(this.manager.length()>0)
				username=this.manager.toLowerCase();	
			buf.append("delete from salary_mapping where salaryid="+this.salaryid+" and lower(userflag)='"+username+"' and  exists (select * from ");
			buf.append(tablename);
			buf.append(" where state='1' and upper(salary_mapping.nbase)=upper(");
			buf.append(tablename);
			buf.append(".dbname) and salary_mapping.A0100=");
			buf.append(tablename);
			buf.append(".A0100)");
			dao.update(buf.toString());
			
			
			/** 总额计算  */
			bo.calculateTotalSum(dateList);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}finally {
			PubFunc.closeResource(ps);
			PubFunc.closeResource(rowSet);
			try {
				// 关闭Wallet
				dbS.close(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * 是否有可报批的数据
	 * @return  0:无可报批的数据  1：有可报批的数据
	 */
	public String getIsAppealData()throws GeneralException
	{
		String isData="0";
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RecordVo vo=new RecordVo(this.gz_tablename.toLowerCase());
			if(vo.hasAttribute("sp_flag"))
			{
				RowSet rowSet=dao.search("select count(A0100) from "+this.gz_tablename+" where Sp_flag='01' or Sp_flag='07'");
				if(rowSet.next())
				{
					if(rowSet.getInt(1)>0)
						isData="1";
				}
				rowSet.close();
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return isData;
	}
	
	/**
	 * 报批时更新历史表记录的 审批过程、审批人、审批状态、当前处理人
	 * @param temps_
	 * @param count
	 * @param opt
	 * @param whl_str
	 * @param appealObject
	 * @param currentTime
	 * @param groupName
	 * @param content
	 */
	public void  updateSalaryhistoryData(String[] temps_,String count,String opt,String whl_str,String appealObject,String currentTime,String groupName,String content)
	{
		DbSecurityImpl dbS = new DbSecurityImpl();
		PreparedStatement pstmt2 = null;
		RowSet rowSet = null;
		try
		{ 
			String toName=getNameByUsername(appealObject);
			String fromName=getNameByUsername(this.userview.getUserName());
			StringBuffer sql=new StringBuffer("");
			sql.append("update salaryhistory set appuser=");
			if("appealAll".equals(opt))	  //报批
			{
				sql.append("'"+userview.getUserName()+";'"+Sql_switcher.concat()+Sql_switcher.isnull("appuser","''")); 
				if(Sql_switcher.searchDbServer()!=2)
				{
					sql.append(",appprocess="); 
					sql.append(Sql_switcher.isnull(Sql_switcher.sqlToChar("appprocess"),"''")+Sql_switcher.concat()+"'   \r\n报批: "+currentTime+"\n  "+groupName+" "+fromName+" 报批给 "+toName+"'"); 
				}
				sql.append(",curr_user='"+appealObject+"',sp_flag='02'");
					
			}
			else if("confirmAll".equals(opt)) //批准
			{
				sql.append("';"+userview.getUserName()+";'"+Sql_switcher.concat()+Sql_switcher.isnull("appuser","''")); 
				if(Sql_switcher.searchDbServer()!=2)
				{
					sql.append(",appprocess=");
					sql.append(Sql_switcher.isnull(Sql_switcher.sqlToChar("appprocess"),"''")+Sql_switcher.concat()+"'   \r\n批准: "+currentTime+"\n  "+groupName+" "+fromName+"\n  "+content+"'"); 
				}
				sql.append(",curr_user='',sp_flag='03'");
			} 
			StringBuffer sql2=new StringBuffer("");			
			sql2.append(" where  "+Sql_switcher.year("A00Z2")+"="+temps_[0]+" and Salaryid="+this.salaryid);
			sql2.append(" and "+Sql_switcher.month("A00Z2")+"="+Integer.parseInt(temps_[1])+"  and "+Sql_switcher.day("A00Z2")+"="+Integer.parseInt(temps_[2]));
			sql2.append(" and A00Z3="+count+"  and  lower(curr_user)='"+this.userview.getUserName().toLowerCase()+"'");
			if("confirmAll".equalsIgnoreCase(opt))
			{
				sql2.append(" and sp_flag='02' ");
				if(whl_str.length()>0)
					sql2.append(whl_str);
			}
			else if("appealAll".equalsIgnoreCase(opt))
			{
				sql2.append(" and ( sp_flag='02' or sp_flag='07' ) ");
				if(whl_str.length()>0)
					sql2.append(whl_str);
			}
			ContentDAO dao = new ContentDAO(this.conn);
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			if(Sql_switcher.searchDbServer()==2) //oracle 2014-04-02 dengcan
			{
				String appuser,appprocess,nbase,a0100,year,month,z1;
				StringBuffer sql3=new StringBuffer("select appuser,appprocess,a0100,nbase,a00z0,a00z1 from salaryhistory"+sql2.toString());
				rowSet=dao.search(sql3.toString());
				sql3.setLength(0);
				sql3.append("update salaryhistory set appuser=?,curr_user=?,sp_flag=?,appprocess=? where a0100=? and lower(nbase)=? and  "+Sql_switcher.year("a00z0")+"=?");
				sql3.append(" and  "+Sql_switcher.month("a00z0")+"=? and a00z1=? and salaryid="+this.salaryid);
				pstmt2=this.conn.prepareStatement(sql3.toString());
				while(rowSet.next())
				{
					appuser=rowSet.getString("appuser")!=null?rowSet.getString("appuser"):"";
					appprocess=Sql_switcher.readMemo(rowSet,"appprocess");
					a0100=rowSet.getString("a0100");
					nbase=rowSet.getString("nbase");
					year=df.format(rowSet.getDate("a00z0")).split("-")[0];
					month=df.format(rowSet.getDate("a00z0")).split("-")[1];
					z1=rowSet.getString("a00z1");
					if("appealAll".equals(opt))	  //报批
						appprocess+="   \r\n报批: "+currentTime+"\n  "+groupName+" "+fromName+" 报批给 "+toName;
					else if("confirmAll".equals(opt)) //批准
						appprocess+="   \r\n批准: "+currentTime+"\n  "+groupName+" "+fromName+"\n  "+content;
						
					java.io.Reader clobReader = new StringReader(appprocess);
					
					if("appealAll".equals(opt))	  //报批
						appuser=userview.getUserName()+";"+appuser;
					else if("confirmAll".equals(opt)) //批准
						appuser=";"+userview.getUserName()+";"+appuser;
					pstmt2.setString(1,appuser);
					if("appealAll".equals(opt))	  //报批
					{
						pstmt2.setString(2,appealObject);
						pstmt2.setString(3,"02");
					}
					else if("confirmAll".equals(opt)) //批准
					{
						pstmt2.setString(2,"");
						pstmt2.setString(3,"03");
					
					}	
					pstmt2.setCharacterStream(4, clobReader, appprocess.length()); 
					pstmt2.setString(5,a0100);
					pstmt2.setString(6,nbase.toLowerCase());
					pstmt2.setInt(7,Integer.parseInt(year));
					pstmt2.setInt(8,Integer.parseInt(month));
					pstmt2.setInt(9,Integer.parseInt(z1));
					pstmt2.addBatch();
				}
				// 打开Wallet
				dbS.open(conn, sql3.toString());
				pstmt2.executeBatch();
			} 
			else
				dao.update(sql.toString()+sql2.toString());
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			try
			{
				// 关闭Wallet
				dbS.close(conn);
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			PubFunc.closeResource(rowSet);
			PubFunc.closeResource(pstmt2);
		}
	}
	
	public void  updateSalaryhistoryData(String sql0,HashMap appprocessMap,String appealObject,String opt,String currentTime,String groupName,String content)
	{
		DbSecurityImpl dbS = new DbSecurityImpl();
		PreparedStatement pstmt = null;
		RowSet rowSet=null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			Calendar d = Calendar.getInstance();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			StringBuffer sql=new StringBuffer("");
			sql.append("update salaryhistory set appuser=?,appprocess=?,curr_user=?,sp_flag=?  where a00z1=?");
			sql.append(" and upper(nbase)=? and salaryid="+this.salaryid+" and a0100=? ");
			sql.append(" and "+Sql_switcher.year("a00z0")+"=? and "+Sql_switcher.month("a00z0")+"=? and "+Sql_switcher.day("a00z0")+"=?");		
			pstmt=this.conn.prepareStatement(sql.toString());
			String toName=getNameByUsername(appealObject);
			String fromName=getNameByUsername(this.userview.getUserName());
			rowSet=dao.search(sql0);
			while(rowSet.next()){
					
					d.setTimeInMillis(rowSet.getDate("a00z0").getTime());
					String key=rowSet.getString("a0100").toLowerCase()+ "/"+ rowSet.getString("nbase").toLowerCase()+ "/"+ df.format(d.getTime()) + "/" +rowSet.getString("a00z1");
					String temp = (String) appprocessMap.get(key);
				
					String[] temps = temp.split("~");
					String appuser = "";
					String appprocess ="";
					String curr_user="";
					String sp_flag="02";
					if("appealAll".equals(opt))	  //报批
					{
						sp_flag="02";
						curr_user=appealObject;
						appuser=this.userview.getUserName() + ";" + temps[1];
						appprocess=temps[0] + "   \r\n报批: " + currentTime+ "\n  " + groupName + " " +fromName+ " 报批给 " + toName;
					}
					else if("confirmAll".equals(opt)) //批准
					{
						curr_user="";
						sp_flag="03";
						appuser=";"+this.userview.getUserName() + ";" + temps[1];
						appprocess=temps[0] + "   \r\n批准: " + currentTime+ "\n  " + groupName + " " +fromName+"\n  "+content;
					}
					else if("rejectAll".equals(opt)) //驳回
					{
						sp_flag="07";
						if(temps[1].indexOf(";")!=-1)
						{
							curr_user=temps[1].substring(0,temps[1].indexOf(";"));
							
							if((temps[1].indexOf(";")+1)<temps[1].length())
								appuser=temps[1].substring(temps[1].indexOf(";")+1);
							
						}
						appprocess=temps[0] + "   \r\n驳回: " + currentTime+ "\n  " + groupName + " " +fromName+ " 驳回审批。\n  驳回原因："+content;
					}
					
					pstmt.setString(1,appuser);
					if(Sql_switcher.searchDbServer()==2) 
					{ 
						java.io.Reader clobReader = new StringReader(appprocess);
						pstmt.setCharacterStream(2, clobReader, appprocess.length());  
					}
					else
						pstmt.setString(2,appprocess);
					pstmt.setString(3,curr_user);
					pstmt.setString(4, sp_flag);
					pstmt.setInt(5,rowSet.getInt("a00z1"));
					pstmt.setString(6,rowSet.getString("nbase").toUpperCase());
					pstmt.setString(7,rowSet.getString("a0100"));
					pstmt.setInt(8, d.get(Calendar.YEAR));
					pstmt.setInt(9, d.get(Calendar.MONTH)+1);
					pstmt.setInt(10,d.get(Calendar.DATE));
					pstmt.addBatch();
			
			}
			// 打开Wallet
			dbS.open(conn, sql.toString());
			pstmt.executeBatch();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(pstmt);
			PubFunc.closeResource(rowSet);
			try {
				// 关闭Wallet
				dbS.close(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	

	
	/**
	 * 工资多级报批/驳回/批准 (汇总审批)
	 * @param appealObject  报批人
	 * @param bosdate		业务日期(发放日期)
	 * @param count			发放次数
	 * @param where_sql
	 * @param opt  appeal:报批  reject:驳回  confirm:批准    
	 * @param content 驳回原因
	 * @throws GeneralException
	 */
	public void gzGradeAppeal_group(String appealObject, String bosdate,
			String count, String where_sql,String opt,String content,String sendMen) throws GeneralException {
		DbSecurityImpl dbS = new DbSecurityImpl();
		PreparedStatement pstmt=null;
		RowSet rowSet =null;
		PreparedStatement pstmt2 = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			String toName=getNameByUsername(appealObject);
			HashMap appprocessMap = getCurrentSalaryhistoryData_group(where_sql, bosdate, count,opt);	
			LazyDynaBean dataBean = getSalaryPayDate(bosdate, count);
			HashSet rejectObjectSet=new HashSet();
			ArrayList primitiveDataTables= getPrimitiveDataTable_group(dataBean, where_sql,opt);
			String spFlag="";
			if("appeal".equals(opt))	  //报批
			{
				spFlag="02";
			}
			else if("reject".equals(opt)) //驳回
			{
				spFlag="07";
			}
			else if("confirm".equals(opt)) //批准
			{
				spFlag="03";
			}
			
			
			String groupName =getGroupName(this.userview); // "";

			/*
			RowSet rowSet = dao.search("select groupName from operuser,usergroup where operuser.groupid=usergroup.groupid and username='"
					+this.userview.getUserName()+ "'");
			if (rowSet.next())
				groupName = rowSet.getString(1);
			 */
			
			Calendar d = Calendar.getInstance();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			Calendar d1 = Calendar.getInstance();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
			String currentTime = dateFormat.format(d1.getTime()); // 当前时间

			
			
			StringBuffer sql=new StringBuffer("");
			sql.append("update salaryhistory set appuser=?,appprocess=?,curr_user=?,sp_flag=?  where a00z1=?");
			sql.append(" and upper(nbase)=? and salaryid="+this.salaryid+" and a0100=? ");
			sql.append(" and "+Sql_switcher.year("a00z0")+"=? and "+Sql_switcher.month("a00z0")+"=? and "+Sql_switcher.day("a00z0")+"=?");		
			pstmt=this.conn.prepareStatement(sql.toString());
				
				
			
			StringBuffer sql2=new StringBuffer("");
			sql2.append("select  a00z0,a00z1,nbase,a0100 from salaryhistory ");
			sql2.append(" where exists( "+where_sql+" )");
			sql2.append(" and "+Sql_switcher.year("A00Z2")+"="+(String)dataBean.get("year") );
			sql2.append(" and "+Sql_switcher.month("A00Z2")+"="+(String)dataBean.get("month") );
			sql2.append(" and "+Sql_switcher.day("A00Z2")+"="+(String)dataBean.get("day") );
			sql2.append(" and A00Z3="+count);
			sql2.append(" and Salaryid="+this.salaryid);
			if("appeal".equalsIgnoreCase(opt)|| "reject".equalsIgnoreCase(opt))
				sql2.append(" and curr_user='"+this.userview.getUserId()+"' and ( sp_flag='02' or sp_flag='07' )  ");
			if("confirm".equalsIgnoreCase(opt))
				sql2.append(" and curr_user='"+this.userview.getUserId()+"' and  sp_flag='02'  ");

			
			boolean isOk=true;
			String  reject_mode="1";
			if("reject".equals(opt))
			{
			    reject_mode=this.ctrlparam.getValue(SalaryCtrlParamBo.FLOW_CTRL,"reject_mode");
				if(reject_mode==null||reject_mode.trim().length()==0)
					reject_mode="1";  //驳回方式  1:逐级驳回  2：驳回到发起人
				if("2".equals(reject_mode))
					isOk=false;
			}
			
			
			{
				for(int e=0;e<primitiveDataTables.size();e++)
				{
					String primitiveDataTable=(String)primitiveDataTables.get(e);
					String[] atemps=primitiveDataTable.split("_salary_");
					StringBuffer update_sql=new StringBuffer("");
					update_sql.append("update "+primitiveDataTable+" set sp_flag=?,appprocess=?  where a00z1=?");
					update_sql.append(" and upper(nbase)=? and a0100=? ");
					update_sql.append(" and "+Sql_switcher.year("a00z0")+"=? and "+Sql_switcher.month("a00z0")+"=? and "+Sql_switcher.day("a00z0")+"=?");
					pstmt2=this.conn.prepareStatement(update_sql.toString());
					
					rowSet=dao.search(sql2.toString()+" and lower(userflag)='"+atemps[0].toLowerCase()+"' ");
					while(rowSet.next()){
							
							d.setTimeInMillis(rowSet.getDate("a00z0").getTime());
							String key=rowSet.getString("a0100").toLowerCase()+ "/"+ rowSet.getString("nbase").toLowerCase()+ "/"+ df.format(d.getTime()) + "/" +rowSet.getString("a00z1");
							String temp = (String) appprocessMap.get(key);
						
							String[] temps = temp.split("~");
							String appuser = "";
							String appprocess ="";
							String curr_user="";
							
							
							boolean isRejectSalaryData=true;  //是否驳回到工资发放表
							if("appeal".equals(opt))	  //报批
							{
								curr_user=appealObject;
								appuser=this.userview.getUserName() + ";" + temps[1];
								appprocess=temps[0] + "   \r\n报批: " + currentTime+ "\n  " + groupName + " " + this.userview.getUserFullName()+ " 报批给 " + toName;
							}
							else if("reject".equals(opt)) //驳回
							{
								if(temps[1].split(";").length>2)
								{
									curr_user=temps[1].substring(0,temps[1].indexOf(";"));
									appuser=temps[1].substring(temps[1].indexOf(";")+1);
									isRejectSalaryData=false;
								}
								appprocess=temps[0] + "   \r\n驳回: " + currentTime+ "\n  " + groupName + " " + this.userview.getUserFullName()+ " 驳回审批。\n  驳回原因："+content;
							}
							else if("confirm".equals(opt)) //批准
							{
								curr_user="";
							//	appuser="";
								appuser=";"+this.userview.getUserName() + ";" + temps[1];
								appprocess=temps[0] + "   \r\n批准: " + currentTime+ "\n  " + groupName + " " + this.userview.getUserFullName()+"\n  "+content;
							}
							
							
							
					
							if("reject".equals(opt))
							{
								if(isRejectSalaryData|| "2".equals(reject_mode))
									pstmt2.setString(1,"07");
								else
									pstmt2.setString(1,"02");
								pstmt2.setString(2,appprocess);
							}
							else if("confirm".equals(opt)) //批准
							{
								pstmt2.setString(1,"03");
								pstmt2.setString(2,appprocess);
							}
							else if("appeal".equals(opt)) //报批
							{
								pstmt2.setString(1,"02");
								pstmt2.setString(2,appprocess);
							}
							
							pstmt2.setInt(3,rowSet.getInt("a00z1"));
							pstmt2.setString(4,rowSet.getString("nbase").toUpperCase());
							pstmt2.setString(5,rowSet.getString("a0100"));
							pstmt2.setInt(6,d.get(Calendar.YEAR));
							pstmt2.setInt(7, d.get(Calendar.MONTH)+1);
							pstmt2.setInt(8,d.get(Calendar.DATE));
							pstmt2.addBatch();
					}
					
					// 打开Wallet
					dbS.open(conn, update_sql.toString());
					pstmt2.executeBatch();
					// 关闭Wallet
					dbS.close(conn);
					
					if("reject".equals(opt)) //(reject_mode.equals("2")) //驳回到发起人
					{
						StringBuffer sql0=new StringBuffer("delete from salaryhistory   where exists (select null from ");
						sql0.append(primitiveDataTable+" a where a.a00z0=salaryhistory.a00z0 and a.a00z1=salaryhistory.a00z1 and upper(a.nbase)=upper(salaryhistory.nbase) and ");
						sql0.append(" a.a0100=salaryhistory.a0100 and a.sp_flag='07' )   and lower(salaryhistory.userflag)='"+atemps[0].toLowerCase()+"'  and salaryhistory.salaryid="+this.salaryid);
						dao.update(sql0.toString());
					}
					
				}
			}
			if(isOk)
			{
				rowSet=dao.search(sql2.toString());
				while(rowSet.next()) {
							d.setTimeInMillis(rowSet.getDate("a00z0").getTime());
							String key=rowSet.getString("a0100").toLowerCase()+ "/"+ rowSet.getString("nbase").toLowerCase()+ "/"+ df.format(d.getTime()) + "/" +rowSet.getString("a00z1");
							
							String temp = (String) appprocessMap.get(key);
						
							String[] temps = temp.split("~");
							String appuser = "";
							String appprocess ="";
							String curr_user="";
							boolean isRejectSalaryData=true;  //是否驳回到工资发放表
							if("appeal".equals(opt))	  //报批
							{
								curr_user=appealObject;
								appuser=this.userview.getUserName() + ";" + temps[1];
								appprocess=temps[0] + "   \r\n报批: " + currentTime+ "\n  " + groupName + " " + this.userview.getUserFullName()+ " 报批给 " + toName;
							}
							else if("reject".equals(opt)) //驳回
							{
								if(temps[1].split(";").length>2)
								{
									isRejectSalaryData=false;
								}
								if(temps[1].indexOf(";")!=-1)
								{
									curr_user=temps[1].substring(0,temps[1].indexOf(";"));
									
									if((temps[1].indexOf(";")+1)<temps[1].length())
										appuser=temps[1].substring(temps[1].indexOf(";")+1);
									rejectObjectSet.add(curr_user);
								}
								
								
								appprocess=temps[0] + "   \r\n驳回: " + currentTime+ "\n  " + groupName + " " + this.userview.getUserFullName()+ " 驳回审批。\n  驳回原因："+content;
							}
							else if("confirm".equals(opt)) //批准
							{
								curr_user="";
							//	appuser="";
								appuser=";"+this.userview.getUserName() + ";" + temps[1];
								appprocess=temps[0] + "   \r\n批准: " + currentTime+ "\n  " + groupName + " " + this.userview.getUserFullName()+"\n  "+content;
							}
							
							pstmt.setString(1,appuser);
							pstmt.setString(2,appprocess);
							pstmt.setString(3,curr_user);
							pstmt.setString(4, spFlag);
							
							pstmt.setInt(5,rowSet.getInt("a00z1"));
							pstmt.setString(6,rowSet.getString("nbase").toUpperCase());
							pstmt.setString(7,rowSet.getString("a0100"));
							pstmt.setInt(8, d.get(Calendar.YEAR));
							pstmt.setInt(9, d.get(Calendar.MONTH)+1);
							pstmt.setInt(10,d.get(Calendar.DATE));
							pstmt.addBatch();
				}
				
				// 打开Wallet
				dbS.open(conn, sql.toString());
				pstmt.executeBatch();
				pstmt.close();
				// 关闭Wallet
				dbS.close(conn);
			
			}
			
			if("appeal".equalsIgnoreCase(opt))
			{
				//发送 邮件 和 短信通知
				if(ctrlparam.getValue(SalaryCtrlParamBo.NOTE)!=null&&ctrlparam.getValue(SalaryCtrlParamBo.NOTE).length()>0)
				{
					sendMessage(appealObject,"","薪资报批");
				}
				
			}
			if("confirm".equalsIgnoreCase(opt)|| "reject".equals(opt))
			{
//				发送 邮件 和 短信通知
				boolean flag=false;
				if(ctrlparam.getValue(SalaryCtrlParamBo.NOTE,"mail")!=null&& "1".equals(ctrlparam.getValue(SalaryCtrlParamBo.NOTE,"mail")))
					flag=true;
				if(ctrlparam.getValue(SalaryCtrlParamBo.NOTE,"sms")!=null&& "1".equals(ctrlparam.getValue(SalaryCtrlParamBo.NOTE,"sms")))
					flag=true;
				if(flag)
				{
					if("reject".equals(opt))
					{
						if(rejectObjectSet.size()>0)
						{
							StringBuffer sendMens=new StringBuffer("");
							for(Iterator t=rejectObjectSet.iterator();t.hasNext();)
							{
								sendMens.append((String)t.next()+",");
							}
							sendMessage(sendMens.toString(),content,"薪资驳回");
						}
					}
					else
					{
						if(sendMen!=null&&sendMen.length()>0)
							sendMessage(sendMen,content,"薪资批准");
					}
				}
				
			}
			
			
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		finally {
			PubFunc.closeResource(rowSet);
			PubFunc.closeResource(pstmt2);
			PubFunc.closeResource(pstmt);
		}
	}
	
	
	
	
	
	
	
	
	/**
	 * 工资多级报批/驳回/批准
	 * @param appealObject  报批人
	 * @param bosdate		业务日期(发放日期)
	 * @param count			发放次数
	 * @param selectGzRecords  选择的记录
	 * @param opt  appeal:报批  reject:驳回  confirm:批准   confirmAll:批准
	 * @param content 驳回原因
	 * @throws GeneralException
	 */
	public void gzGradeAppeal(String appealObject, String bosdate,
			String count, String selectGzRecords,String opt,String content,String sendMen,String whl_str) throws GeneralException {
		DbSecurityImpl dbS = new DbSecurityImpl();
		PreparedStatement pstmt2=null;
		RowSet rowSet = null;
		PreparedStatement pstmt=null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			String toName=getNameByUsername(appealObject);
			String fromName=getNameByUsername(userview.getUserName());
			HashMap appprocessMap =new HashMap();
			if(!("appealAll".equalsIgnoreCase(opt)|| "confirmAll".equals(opt)))
				appprocessMap=getCurrentSalaryhistoryData(selectGzRecords, bosdate, count,opt,whl_str);	
			LazyDynaBean dataBean = getSalaryPayDate(bosdate, count);
			HashSet rejectObjectSet=new HashSet();
		//	ArrayList primitiveDataTables=deleteData(dataBean, selectGzRecords,opt);
			ArrayList primitiveDataTables= getPrimitiveDataTable(dataBean, selectGzRecords,opt,whl_str);
			String spFlag="";
			if("appeal".equals(opt)|| "appealAll".equals(opt))	  //报批
			{
				spFlag="02";
			}
			else if("reject".equals(opt)|| "rejectAll".equals(opt)) //驳回
			{
				spFlag="07";
			}
			else if("confirm".equals(opt)|| "confirmAll".equals(opt)) //批准
			{
				spFlag="03";
			}
			String groupName =getGroupName(this.userview);
			/*
			String groupName = "";
			RowSet rowSet = dao.search("select groupName from operuser,usergroup where operuser.groupid=usergroup.groupid and username='"
					+this.userview.getUserName()+ "'");
			if (rowSet.next())
				groupName = rowSet.getString(1);
			 */
		//	insertSalaryHistoryData(dataBean, s1.substring(1), s2.substring(1),selectGzRecords,primitiveDataTables);
			
			String[] selects = selectGzRecords.split("#");
			int num = selects.length / 15 + 1;
			StringBuffer whl = new StringBuffer("");
			Calendar d = Calendar.getInstance();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			Calendar d1 = Calendar.getInstance();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
			String currentTime = dateFormat.format(d1.getTime()); // 当前时间
			
			boolean isOk=true;
			String  reject_mode="1";
			if("reject".equals(opt)|| "rejectAll".equals(opt))
			{
			    reject_mode=this.ctrlparam.getValue(SalaryCtrlParamBo.FLOW_CTRL,"reject_mode");
				if(reject_mode==null||reject_mode.trim().length()==0)
					reject_mode="1";  //驳回方式  1:逐级驳回  2：驳回到发起人
				if("2".equals(reject_mode))
					isOk=false;
			}
						
			if("confirmAll".equalsIgnoreCase(opt)|| "appealAll".equalsIgnoreCase(opt)|| "rejectAll".equals(opt))
			{
				
				/**----------------------------验证是否存在可以操作的数据------------------------------**/
				String[] temps_=bosdate.split("\\."); 
				StringBuffer sqlcheck=new StringBuffer("");
				String errmessage="";
				sqlcheck.append("select  count(1) as num from salaryhistory ");
				sqlcheck.append(" where  "+Sql_switcher.year("A00Z2")+"="+temps_[0]+" and Salaryid="+this.salaryid);
				sqlcheck.append(" and "+Sql_switcher.month("A00Z2")+"="+Integer.parseInt(temps_[1])+"  and "+Sql_switcher.day("A00Z2")+"="+Integer.parseInt(temps_[2]));
				sqlcheck.append(" and A00Z3="+count+"  and  lower(curr_user)='"+this.userview.getUserName().toLowerCase()+"'");
				if("confirmAll".equalsIgnoreCase(opt))
				{
					sqlcheck.append(" and sp_flag='02' ");
					if(whl_str.length()>0)
						sqlcheck.append(whl_str);
					errmessage="批准";
				}
				else if("appealAll".equalsIgnoreCase(opt)|| "rejectAll".equalsIgnoreCase(opt))
				{
					if("appealAll".equalsIgnoreCase(opt))
						errmessage="报批";
					else
						errmessage="驳回";
					
					sqlcheck.append(" and ( sp_flag='02' or sp_flag='07' ) ");
					if(whl_str.length()>0)
						sqlcheck.append(whl_str);
				}
				rowSet=dao.search(sqlcheck.toString());
				if(rowSet.next()){
					int i=rowSet.getInt("num");
					if(i==0){
						throw GeneralExceptionHandler.Handle(new Throwable("没有可以"+errmessage+"的数据！"));
					}
				}
				/**----------------------------验证结束------------------------------**/
				
			
				for(int e=0;e<primitiveDataTables.size();e++)
				{
						String primitiveDataTable=(String)primitiveDataTables.get(e);
						String[] atemps=primitiveDataTable.split("_salary_");
						
						if("rejectAll".equals(opt)) //驳回
						{ 
							StringBuffer sql2=new StringBuffer("");
							sql2.append("update "+primitiveDataTable+" set sp_flag=?,appprocess=?  where a00z1=?");
							sql2.append(" and upper(nbase)=? and a0100=? ");
							sql2.append(" and "+Sql_switcher.year("a00z0")+"=? and "+Sql_switcher.month("a00z0")+"=? and "+Sql_switcher.day("a00z0")+"=?");
							pstmt2=this.conn.prepareStatement(sql2.toString());
							
							sql2.setLength(0);
							sql2.append("select  a00z0,a00z1,nbase,a0100 from salaryhistory ");
							sql2.append(" where  "+Sql_switcher.year("A00Z2")+"="+temps_[0]+" and Salaryid="+this.salaryid);
							sql2.append(" and "+Sql_switcher.month("A00Z2")+"="+Integer.parseInt(temps_[1])+"  and "+Sql_switcher.day("A00Z2")+"="+Integer.parseInt(temps_[2]));
							sql2.append(" and A00Z3="+count);
						//	if(!(this.manager!=null&&this.manager.length()>0))
								sql2.append(" and lower(userflag)='"+atemps[0].toLowerCase()+"' ");
							sql2.append("  and  lower(curr_user)='"+this.userview.getUserName().toLowerCase()+"' ");
							if("confirmAll".equalsIgnoreCase(opt))
							{	
								sql2.append(" and sp_flag='02' ");
								if(whl_str.length()>0)
									sql2.append(whl_str);
							}
							else if("appealAll".equalsIgnoreCase(opt)|| "rejectAll".equals(opt))
							{
								sql2.append(" and ( sp_flag='02' or sp_flag='07' ) ");
								if(whl_str.length()>0)
									sql2.append(whl_str);
							}
							rowSet=dao.search(sql2.toString());	
							while(rowSet.next()){
									d.setTimeInMillis(rowSet.getDate("a00z0").getTime());
									String key=rowSet.getString("a0100").toLowerCase()+ "/"+ rowSet.getString("nbase").toLowerCase()+ "/"+ df.format(d.getTime()) + "/" +rowSet.getString("a00z1");
									String temp = (String) appprocessMap.get(key);
									String[] temps = temp.split("~");
									String appuser = "";
									String appprocess ="";
									String curr_user="";
									boolean isRejectSalaryData=true;  //是否驳回到工资发放表
									if("appealAll".equals(opt))	  //报批
									{
										curr_user=appealObject;
										appuser=this.userview.getUserName() + ";" + temps[1];
										appprocess=temps[0] + "   \r\n报批: " + currentTime+ "\n  " + groupName + " " + fromName+ " 报批给 " + toName;
										pstmt2.setString(1,spFlag);
									}
									else if("confirmAll".equals(opt)) //批准
									{
										curr_user="";
										appuser=";"+this.userview.getUserName() + ";" + temps[1];
										appprocess=temps[0] + "   \r\n批准: " + currentTime+ "\n  " + groupName + " " + fromName+"\n  "+content;
										pstmt2.setString(1,spFlag);
									}
									else if("rejectAll".equals(opt)) //驳回
									{
										if(temps[1].split(";").length>2)//原来是1 因为后来改过appusr这个字段用；开头，所以这块应该改成2
										{
											isRejectSalaryData=false;
										}
										if(temps[1].indexOf(";")!=-1)
										{
											curr_user=temps[1].substring(0,temps[1].indexOf(";"));
											
											if((temps[1].indexOf(";")+1)<temps[1].length())
												appuser=temps[1].substring(temps[1].indexOf(";")+1);
											
										}
										appprocess=temps[0] + "   \r\n驳回: " + currentTime+ "\n  " + groupName + " " + fromName+ " 驳回审批。\n  驳回原因："+content;
									
										if(isRejectSalaryData|| "2".equals(reject_mode))
										{
											pstmt2.setString(1,"07");
											rejectObjectSet.add(atemps[0]);
										}
										else
										{
											pstmt2.setString(1,"02");
											rejectObjectSet.add(curr_user);
										}
									
									}
									if(Sql_switcher.searchDbServer()==2) 
									{
										java.io.Reader clobReader = new StringReader(appprocess);
										pstmt2.setCharacterStream(2, clobReader, appprocess.length()); 
									}
									else
										pstmt2.setString(2,appprocess);
									pstmt2.setInt(3,rowSet.getInt("a00z1"));
									pstmt2.setString(4,rowSet.getString("nbase").toUpperCase());
									pstmt2.setString(5,rowSet.getString("a0100"));
									pstmt2.setInt(6,d.get(Calendar.YEAR));
									pstmt2.setInt(7, d.get(Calendar.MONTH)+1);
									pstmt2.setInt(8,d.get(Calendar.DATE));
									pstmt2.addBatch();
							}
							// 打开Wallet
							dbS.open(conn, sql2.toString());
							pstmt2.executeBatch();
							// 关闭Wallet
							dbS.close(conn);
							
							if("rejectAll".equals(opt)) //(reject_mode.equals("2")) //驳回到发起人
							{
								StringBuffer sql=new StringBuffer("delete from salaryhistory   where exists (select null from ");
								sql.append(primitiveDataTable+" a where a.a00z0=salaryhistory.a00z0 and a.a00z1=salaryhistory.a00z1 and upper(a.nbase)=upper(salaryhistory.nbase) and ");
								sql.append(" a.a0100=salaryhistory.a0100 and a.sp_flag='07' )   and lower(salaryhistory.userflag)='"+atemps[0].toLowerCase()+"'  and salaryhistory.salaryid="+this.salaryid);
								dao.update(sql.toString());
							}
						}
						else
						{
							 
							StringBuffer sql2=new StringBuffer(""); 
							sql2.append("update "+primitiveDataTable+" set sp_flag='"+spFlag+"'"); 
							if(Sql_switcher.searchDbServer()!=2) 
							{
								sql2.append(",appprocess=");
								if("appealAll".equals(opt))	  //报批
									sql2.append(Sql_switcher.isnull(Sql_switcher.sqlToChar("appprocess"),"''")+Sql_switcher.concat()+"'   \r\n报批: "+currentTime+"\n  "+groupName+" "+fromName+" 报批给 "+toName+"'"); 
								else if("confirmAll".equals(opt)) //批准
									sql2.append(Sql_switcher.isnull(Sql_switcher.sqlToChar("appprocess"),"''")+Sql_switcher.concat()+"'   \r\n批准: "+currentTime+"\n  "+groupName+" "+fromName+"\n  "+content+"'"); 
							}	
							StringBuffer sub_sql=new StringBuffer("");
							sub_sql.append(" where exists (select null from salaryhistory where  Salaryid="+this.salaryid+" ");
							sub_sql.append(" and salaryhistory.a0100="+primitiveDataTable+".a0100  and salaryhistory.a00z0="+primitiveDataTable+".a00z0  and salaryhistory.a00z1="+primitiveDataTable+".a00z1  and upper(salaryhistory.nbase)=upper("+primitiveDataTable+".nbase)  ");							
							sub_sql.append(" and  "+Sql_switcher.year("A00Z2")+"="+temps_[0] );
							sub_sql.append(" and "+Sql_switcher.month("A00Z2")+"="+Integer.parseInt(temps_[1])+"  and "+Sql_switcher.day("A00Z2")+"="+Integer.parseInt(temps_[2]));
							sub_sql.append(" and A00Z3="+count);
							sub_sql.append(" and lower(userflag)='"+atemps[0].toLowerCase()+"' ");
							sub_sql.append("  and  lower(curr_user)='"+this.userview.getUserName().toLowerCase()+"' ");
							if("confirmAll".equalsIgnoreCase(opt))
							{	
								sub_sql.append(" and sp_flag='02' ");
								if(whl_str.length()>0)
									sub_sql.append(whl_str);
							}
							else if("appealAll".equalsIgnoreCase(opt))
							{
								sub_sql.append(" and ( sp_flag='02' or sp_flag='07' ) ");
								if(whl_str.length()>0)
									sub_sql.append(whl_str);
							} 
							sub_sql.append(" ) "); 
							
							if(Sql_switcher.searchDbServer()==2) //oracle  2014-04-02 dengcan
							{
								String appprocess,nbase,a0100,year,month,z1;
								StringBuffer sql3=new StringBuffer("select appprocess,a0100,nbase,a00z0,a00z1 from "+primitiveDataTable+sub_sql.toString());
								rowSet=dao.search(sql3.toString());
								sql3.setLength(0);
								sql3.append("update "+primitiveDataTable+" set sp_flag=?,appprocess=? where a0100=? and lower(nbase)=? and  "+Sql_switcher.year("a00z0")+"=?");
								sql3.append(" and  "+Sql_switcher.month("a00z0")+"=? and a00z1=?");
								pstmt2=this.conn.prepareStatement(sql3.toString());
								while(rowSet.next())
								{
									appprocess=Sql_switcher.readMemo(rowSet,"appprocess");
									a0100=rowSet.getString("a0100");
									nbase=rowSet.getString("nbase");
									year=df.format(rowSet.getDate("a00z0")).split("-")[0];
									month=df.format(rowSet.getDate("a00z0")).split("-")[1];
									z1=rowSet.getString("a00z1");
									if("appealAll".equals(opt))	  //报批
										appprocess+="   \r\n报批: "+currentTime+"\n  "+groupName+" "+fromName+" 报批给 "+toName;
									else if("confirmAll".equals(opt)) //批准
										appprocess+="   \r\n批准: "+currentTime+"\n  "+groupName+" "+fromName+"\n  "+content;
										
									java.io.Reader clobReader = new StringReader(appprocess);
									pstmt2.setString(1,spFlag); 
									pstmt2.setCharacterStream(2, clobReader, appprocess.length()); 
									pstmt2.setString(3,a0100);
									pstmt2.setString(4,nbase.toLowerCase());
									pstmt2.setInt(5,Integer.parseInt(year));
									pstmt2.setInt(6,Integer.parseInt(month));
									pstmt2.setInt(7,Integer.parseInt(z1));
									pstmt2.addBatch();
								}
								// 打开Wallet
								dbS.open(conn, sql3.toString());
								pstmt2.executeBatch();
								// 关闭Wallet
								dbS.close(conn);
							} 
							else
								dao.update(sql2.toString()+sub_sql.toString()); 
						}
				}
				
				
				if(isOk)
				{
					StringBuffer sql=new StringBuffer("");
					sql.append("select  a00z0,a00z1,nbase,a0100 from salaryhistory ");
					sql.append(" where  "+Sql_switcher.year("A00Z2")+"="+temps_[0]+" and Salaryid="+this.salaryid);
					sql.append(" and "+Sql_switcher.month("A00Z2")+"="+Integer.parseInt(temps_[1])+"  and "+Sql_switcher.day("A00Z2")+"="+Integer.parseInt(temps_[2]));
					sql.append(" and A00Z3="+count+"  and  lower(curr_user)='"+this.userview.getUserName().toLowerCase()+"'");
					if("confirmAll".equalsIgnoreCase(opt))
					{
						sql.append(" and sp_flag='02' ");
						if(whl_str.length()>0)
							sql.append(whl_str);
					}
					else if("appealAll".equalsIgnoreCase(opt)|| "rejectAll".equalsIgnoreCase(opt))
					{
						sql.append(" and ( sp_flag='02' or sp_flag='07' ) ");
						if(whl_str.length()>0)
							sql.append(whl_str);
					}
					
					 if("rejectAll".equals(opt)) //驳回
						 updateSalaryhistoryData(sql.toString(),appprocessMap,appealObject,opt,currentTime,groupName,content);	
					 else
						 updateSalaryhistoryData(temps_,count,opt,whl_str,appealObject,currentTime,groupName,content);
				}
				
				
				
				
				//同步临时表审批流程（appprocess）信息
				/*
				for(int e=0;e<primitiveDataTables.size();e++)
				{
					String primitiveDataTable=(String)primitiveDataTables.get(e);
					
					StringBuffer sql0=new StringBuffer("");
					if(Sql_switcher.searchDbServer()==Constant.ORACEL)
					{
						sql0.append("update "+primitiveDataTable+" set appprocess=(select appprocess from salaryhistory where ");
						sql0.append("  salaryhistory.a0100="+primitiveDataTable+".a0100 and  upper(salaryhistory.nbase)=upper("+primitiveDataTable+".nbase) and  salaryhistory.a00z0="+primitiveDataTable+".a00z0 and  salaryhistory.a00z1="+primitiveDataTable+".a00z1 "); 
						sql0.append(" and salaryid="+this.salaryid+" ) where exists (select null  from salaryhistory where ");
						sql0.append(" salaryhistory.a0100="+primitiveDataTable+".a0100 and  upper(salaryhistory.nbase)=upper("+primitiveDataTable+".nbase) and  salaryhistory.a00z0="+primitiveDataTable+".a00z0 and  salaryhistory.a00z1="+primitiveDataTable+".a00z1 and salaryid="+this.salaryid+" )"); 
					}
					else
					{
						sql0.append("update  "+primitiveDataTable+"   set  "+primitiveDataTable+".appprocess= salaryhistory.appprocess "); 
						sql0.append(" from   salaryhistory");
						sql0.append(" where  salaryhistory.a0100="+primitiveDataTable+".a0100 and ");
						sql0.append(" upper(salaryhistory.nbase)=upper("+primitiveDataTable+".nbase) ");
						sql0.append(" and  salaryhistory.a00z0="+primitiveDataTable+".a00z0 and ");
						sql0.append(" salaryhistory.a00z1="+primitiveDataTable+".a00z1  and salaryhistory.salaryid="+this.salaryid);
						
					}
					dao.update(sql0.toString());
				}*/
			
			}
			else
			{
				if(isOk)
				{
					StringBuffer sql=new StringBuffer("");
					sql.append("update salaryhistory set appuser=?,appprocess=?,curr_user=?,sp_flag=?  where a00z1=?");
					sql.append(" and upper(nbase)=? and salaryid="+this.salaryid+" and a0100=? ");
					sql.append(" and "+Sql_switcher.year("a00z0")+"=? and "+Sql_switcher.month("a00z0")+"=? and "+Sql_switcher.day("a00z0")+"=?");		
					pstmt=this.conn.prepareStatement(sql.toString());
					for (int n = 0; n < num; n++) {
						whl.setLength(0);
						for (int i = n * 15; i < selects.length; i++) {
							String[] a_selects = selects[i].split("/");
							d.setTimeInMillis(Long.parseLong(a_selects[2]));
		
							String key=a_selects[0].toLowerCase()+ "/"+ a_selects[1].toLowerCase()+ "/"+ df.format(d.getTime()) + "/" + a_selects[3];
							String temp = (String) appprocessMap.get(key);
						
							String[] temps = temp.split("~");
							String appuser = "";
							String appprocess ="";
							String curr_user="";
							boolean isRejectSalaryData=true;  //是否驳回到工资发放表
							if("appeal".equals(opt))	  //报批
							{
								curr_user=appealObject;
								appuser=this.userview.getUserName() + ";" + temps[1];
								appprocess=temps[0] + "   \r\n报批: " + currentTime+ "\n  " + groupName + " " + fromName+ " 报批给 " + toName;
							}
							else if("reject".equals(opt)) //驳回
							{
								if(temps[1].split(";").length>2)
								{
									isRejectSalaryData=false;
								}
								if(temps[1].indexOf(";")!=-1)
								{
									curr_user=temps[1].substring(0,temps[1].indexOf(";"));
									
									if((temps[1].indexOf(";")+1)<temps[1].length())
										appuser=temps[1].substring(temps[1].indexOf(";")+1);
									rejectObjectSet.add(curr_user);
								}
								
								
								appprocess=temps[0] + "   \r\n驳回: " + currentTime+ "\n  " + groupName + " " +fromName+ " 驳回审批。\n  驳回原因："+content;
							}
							else if("confirm".equals(opt)) //批准
							{
								curr_user="";
							//	appuser="";
								appuser=";"+this.userview.getUserName() + ";" + temps[1];
								appprocess=temps[0] + "   \r\n批准: " + currentTime+ "\n  " + groupName + " " +fromName+"\n  "+content;
							}
							
							pstmt.setString(1,appuser);
							
							if(Sql_switcher.searchDbServer()==2) 
							{
								java.io.Reader clobReader = new StringReader(appprocess);
								pstmt.setCharacterStream(2, clobReader, appprocess.length()); 
							}
							else
								pstmt.setString(2,appprocess);
							pstmt.setString(3,curr_user);
							pstmt.setString(4, spFlag);
							
							
							pstmt.setInt(5,Integer.parseInt(a_selects[3]));
							pstmt.setString(6,a_selects[1].toUpperCase());
							pstmt.setString(7,a_selects[0]);
							pstmt.setInt(8, d.get(Calendar.YEAR));
							pstmt.setInt(9, d.get(Calendar.MONTH)+1);
							pstmt.setInt(10,d.get(Calendar.DATE));
							
							pstmt.addBatch();
						}
					}
					// 打开Wallet
					dbS.open(conn, sql.toString());
					int [] exeitem=pstmt.executeBatch();
					// 关闭Wallet
					dbS.close(conn);
				}
			}
			
			if(!"confirmAll".equalsIgnoreCase(opt)&&!"appealAll".equalsIgnoreCase(opt)&&!"rejectAll".equalsIgnoreCase(opt))
			{
				
				
				for(int e=0;e<primitiveDataTables.size();e++)
				{
					String primitiveDataTable=(String)primitiveDataTables.get(e);
					/**主要用于取得用户名*/
					String[] atemps=primitiveDataTable.split("_salary_");  //changed at 20091203 "_"改成"_salary_"

					StringBuffer sql2=new StringBuffer("");
					sql2.append("update "+primitiveDataTable+" set sp_flag=?,appprocess=?  where a00z1=?");
					sql2.append(" and upper(nbase)=? and a0100=? ");
					sql2.append(" and "+Sql_switcher.year("a00z0")+"=? and "+Sql_switcher.month("a00z0")+"=? and "+Sql_switcher.day("a00z0")+"=?");
					pstmt2=this.conn.prepareStatement(sql2.toString());
					
					
					for (int n = 0; n < num; n++) {
						whl.setLength(0);
						for (int i = n * 15; i < selects.length; i++) {
							String[] a_selects = selects[i].split("/");
							d.setTimeInMillis(Long.parseLong(a_selects[2]));
	
							String key=a_selects[0].toLowerCase()+ "/"+ a_selects[1].toLowerCase()+ "/"+ df.format(d.getTime()) + "/" + a_selects[3];
							String temp = (String) appprocessMap.get(key);
						
							String[] temps = temp.split("~");
							String appuser = "";
							String appprocess ="";
							String curr_user="";
							
							if(!(this.manager!=null&&this.manager.length()>0))
							{
								if(!atemps[0].equalsIgnoreCase(a_selects[4]))
									continue;
							}
							
							boolean isRejectSalaryData=true;  //是否驳回到工资发放表
							if("appeal".equals(opt))	  //报批
							{
								curr_user=appealObject;
								appuser=this.userview.getUserName() + ";" + temps[1];
								appprocess=temps[0] + "   \r\n报批: " + currentTime+ "\n  " + groupName + " " + fromName+ " 报批给 " + toName;
							}
							else if("reject".equals(opt)) //驳回
							{
								if(temps[1].split(";").length>2)
								{
									curr_user=temps[1].substring(0,temps[1].indexOf(";"));
									appuser=temps[1].substring(temps[1].indexOf(";")+1);
									isRejectSalaryData=false;
								}
								appprocess=temps[0] + "   \r\n驳回: " + currentTime+ "\n  " + groupName + " " +fromName+ " 驳回审批。\n  驳回原因："+content;
							}
							else if("confirm".equals(opt)) //批准
							{
								curr_user="";
							//	appuser="";
								appuser=";"+this.userview.getUserName() + ";" + temps[1];
								appprocess=temps[0] + "   \r\n批准: " + currentTime+ "\n  " + groupName + " " +fromName+"\n  "+content;
							}
							
							
							
					
							if("reject".equals(opt))
							{
								if(isRejectSalaryData|| "2".equals(reject_mode))
								{
									pstmt2.setString(1,"07");
									rejectObjectSet.add(atemps[0]);
								}
								else
									pstmt2.setString(1,"02"); 
							}
							else if("confirm".equals(opt)) //批准
							{
								pstmt2.setString(1,"03");
							 
							}
							else if("appeal".equals(opt)) //报批
							{
								pstmt2.setString(1,"02");
								 
							}
							
							if(Sql_switcher.searchDbServer()==2) 
							{ 
								java.io.Reader clobReader = new StringReader(appprocess);
								pstmt2.setCharacterStream(2, clobReader, appprocess.length()); 
							}
							else
								pstmt2.setString(2,appprocess);
							pstmt2.setInt(3,Integer.parseInt(a_selects[3]));
							pstmt2.setString(4,a_selects[1].toUpperCase());
							pstmt2.setString(5,a_selects[0]);
							pstmt2.setInt(6,d.get(Calendar.YEAR));
							pstmt2.setInt(7, d.get(Calendar.MONTH)+1);
							pstmt2.setInt(8,d.get(Calendar.DATE));
							pstmt2.addBatch();
						}
					}
					// 打开Wallet
					dbS.open(conn, sql2.toString());
					pstmt2.executeBatch();
					// 关闭Wallet
					dbS.close(conn);
					
					if("reject".equals(opt))//(reject_mode.equals("2")) //驳回到发起人
					{
						StringBuffer sql=new StringBuffer("delete from salaryhistory   where exists (select null from ");
						sql.append(primitiveDataTable+" a where a.a00z0=salaryhistory.a00z0 and a.a00z1=salaryhistory.a00z1 and upper(a.nbase)=upper(salaryhistory.nbase) and ");
						sql.append(" a.a0100=salaryhistory.a0100 and a.sp_flag='07' )  and lower(salaryhistory.userflag)='"+atemps[0].toLowerCase()+"'  and salaryhistory.salaryid="+this.salaryid);
						dao.update(sql.toString());
					}
					
				}
				
			/*	if(opt.equals("reject")||opt.equals("rejectAll")) //驳回  //当要驳回给临时表时，历史表中的记录要删掉
				{
				    if(!reject_mode.equals("2")) //驳回到发起人
						dao.update("delete from salaryhistory where salaryid="+this.salaryid+" and sp_flag='07' and ( appuser is null or "+Sql_switcher.length(Sql_switcher.trim("appuser"))+"=0 )");
				}
				*/
			}
			
			
			if("appeal".equalsIgnoreCase(opt)|| "appealAll".equalsIgnoreCase(opt))
			{
				//发送 邮件 和 短信通知
				if(ctrlparam.getValue(SalaryCtrlParamBo.NOTE)!=null&&ctrlparam.getValue(SalaryCtrlParamBo.NOTE).length()>0)
				{
					sendMessage(appealObject,"","薪资报批");
				}
				
			}
			if("confirmAll".equalsIgnoreCase(opt)|| "confirm".equalsIgnoreCase(opt)|| "reject".equals(opt)|| "rejectAll".equals(opt))
			{
//				发送 邮件 和 短信通知
				boolean flag=false;
				if(ctrlparam.getValue(SalaryCtrlParamBo.NOTE,"mail")!=null&& "1".equals(ctrlparam.getValue(SalaryCtrlParamBo.NOTE,"mail")))
					flag=true;
				if(ctrlparam.getValue(SalaryCtrlParamBo.NOTE,"sms")!=null&& "1".equals(ctrlparam.getValue(SalaryCtrlParamBo.NOTE,"sms")))
					flag=true;
				if(flag)
				{
					if("reject".equals(opt)|| "rejectAll".equals(opt))
					{
						if(rejectObjectSet.size()>0)
						{
							StringBuffer sendMens=new StringBuffer("");
							for(Iterator t=rejectObjectSet.iterator();t.hasNext();)
							{
								sendMens.append((String)t.next()+",");
							}
							sendMessage(sendMens.toString(),content,"薪资驳回");
						}
					}
					else
					{
						if(sendMen!=null&&sendMen.length()>0)
							sendMessage(sendMen,content,"薪资批准");
					}
				}
				
			}
			if(("confirm".equals(opt)|| "confirmAll".equals(opt))&& "huayu".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))
			{
				String clientName = SystemConfig.getPropertyValue("clientName");
				if(!(clientName==null||clientName.trim().length()==0))
				{
					ResourceBundle    myResource = ResourceBundle.getBundle(clientName);
					if(myResource!=null){
						String salaryid="";
						try{
				           salaryid = myResource.getString("S"+this.salaryid);  //绩效薪资类别ID
				          
						}catch(Exception e){
							salaryid="";
						}
						 if(salaryid!=null&&!"".equals(salaryid)&&this.salaryid==Integer.parseInt(salaryid))
					     {
							whl_str=whl_str.replaceAll(" and salaryhistory.sp_flag='02'","");
							String xml=this.getSpSendXML(selectGzRecords, bosdate, count, opt, whl_str);
							GzServiceBo bo = new GzServiceBo();
							bo.sendMessage(xml);
					      }
					}
				}
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}finally {
			PubFunc.closeResource(pstmt2);
			PubFunc.closeResource(pstmt);
			PubFunc.closeResource(rowSet);
		}
	}
	
	/** 更新 工资数据表的状态 及 审批意见*/
	public void updateSalaryData(String tableName,String sp_flag,String appprocess,String[] a_selects,Calendar d)throws GeneralException
	{
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sql=new StringBuffer("");
			sql.append("update "+tableName+" set sp_flag=?,appprocess=?  where a00z1="+a_selects[3]);
			sql.append(" and upper(nbase)='"+a_selects[1].toUpperCase()+"' and a0100='"+a_selects[0]+"' ");
			sql.append(" and "+Sql_switcher.year("a00z0")+"="+d.get(Calendar.YEAR)+" and "+Sql_switcher.month("a00z0")+"="+(d.get(Calendar.MONTH)+1)+" and "+Sql_switcher.day("a00z0")+"="+d.get(Calendar.DATE));
			ArrayList paramList=new ArrayList();
			paramList.add(sp_flag);
			paramList.add(appprocess);
			dao.update(sql.toString(),paramList);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	
	public void insertSalaryHistoryData(LazyDynaBean dataBean,String s1,String s2,String selectGzRecords,ArrayList primitiveDataTables)
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String[] selects=selectGzRecords.split("#");
			int num=selects.length/16+1;
			StringBuffer whl=new StringBuffer("");
			StringBuffer whl0=new StringBuffer(" where ");
			whl0.append(" "+Sql_switcher.year("A00Z2")+"="+(String)dataBean.get("year") );
			whl0.append(" and "+Sql_switcher.month("A00Z2")+"="+(String)dataBean.get("month") );
			whl0.append(" and "+Sql_switcher.day("A00Z2")+"="+(String)dataBean.get("day") );
			whl0.append(" and A00Z3="+(String)dataBean.get("count"));
			Calendar d=Calendar.getInstance();
			for(int j=0;j<primitiveDataTables.size();j++)
			{
				String primitiveDataTable=(String)primitiveDataTables.get(j);
				String[] temp=primitiveDataTable.split("_salary_");
				for(int n=0;n<num;n++)
				{
					whl.setLength(0);
					int a=0;
					for(int i=n*15;i<selects.length;i++)
					{
						++a;
						String[] a_selects=selects[i].split("/");
						d.setTimeInMillis(Long.parseLong(a_selects[2]));
						if(temp[0].equalsIgnoreCase(a_selects[4]))
							whl.append(" or (A0100='"+a_selects[0]+"' and upper(nbase)='"+a_selects[1].toUpperCase()+"' and "+Sql_switcher.year("A00Z0")+"="+d.get(Calendar.YEAR)+"  and "+Sql_switcher.month("A00Z0")+"="+(d.get(Calendar.MONTH)+1)+" and A00z1="+a_selects[3]+" )");
						if(a>=15)
							break;
					}
					if(whl.length()>0)
					{
						String sql="insert into salaryhistory ("+s1+") select "+s2+" from "+primitiveDataTable+whl0.toString()+" and ( "+whl.substring(3)+" )";
						//System.out.println(sql);
						dao.update(sql);
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
	 * 查询薪资发放临时表中是否还含有没报审的记录
	 * @return
	 */
	public String  getIsNotSpFlag2Records()
	{
		String flag="0";
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			if(this.manager.length()>0)
			{
				RowSet rowSet=dao.search("select count(a0100) from "+this.gz_tablename+" where sp_flag2<>'02' or sp_flag2 is null");
				if(rowSet.next())
				{
					if(rowSet.getInt(1)>0)
						flag="1";
				}
				rowSet.close();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
	
	/**
	 * 工资数据报审驳回
	 * @param selectRecords
	 * @throws GeneralException
	 */
	public void gzDataReportReject(String selectRecords,String content)throws GeneralException
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String fromName=getNameByUsername(userview.getUserName());
			String groupName =getGroupName(this.userview);
			RowSet rowSet = null;
			/*
			String groupName = "";
			RowSet rowSet = dao.search("select groupName from operuser,usergroup where operuser.groupid=usergroup.groupid and username='"
					+this.userview.getUserName()+ "'");
			if (rowSet.next())
				groupName = rowSet.getString(1);
			*/
			Calendar d1 = Calendar.getInstance();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
			String currentTime = dateFormat.format(d1.getTime()); // 当前时间
			if("huayu".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName"))){
				ResourceBundle    myResource = ResourceBundle.getBundle(SystemConfig.getPropertyValue("clientName"));
				if(myResource!=null){
			        String salaryid="";
			        try{
			        	salaryid=myResource.getString("S"+this.salaryid);  //绩效薪资类别ID
			        }catch(Exception e){
			        	salaryid="";
			        }
			        if(salaryid!=null&&!"".equals(salaryid)&&this.salaryid==Integer.parseInt(salaryid))
			        {
						String xml=this.getRjSendXML(selectRecords, content);
						GzServiceBo bo = new GzServiceBo();
						bo.sendMessage(xml);
			        }
				}
			}
			String[] selects=selectRecords.split("#");
			Calendar d=Calendar.getInstance();
			StringBuffer whl=new StringBuffer("");
			for(int i=0;i<selects.length;i++)
			{
				String[] a_selects=selects[i].split("/");
				d.setTimeInMillis(Long.parseLong(a_selects[2]));
				whl.append(" or (A0100='"+a_selects[0]+"' and upper(nbase)='"+a_selects[1].toUpperCase()+"' and "+Sql_switcher.year("A00Z0")+"="+d.get(Calendar.YEAR)+"  and "+Sql_switcher.month("A00Z0")+"="+(d.get(Calendar.MONTH)+1)+" and A00z1="+a_selects[3]+" )");
			}
			rowSet=dao.search("select * from "+this.gz_tablename+" where ( "+whl.substring(3)+" ) and (sp_flag='01' or sp_flag='07')");
			ArrayList dataList=new ArrayList();
			ArrayList recordList=null;
			while(rowSet.next())
			{
				String appprocess=Sql_switcher.readMemo(rowSet,"appprocess");
				recordList=new ArrayList();
				recordList.add(appprocess+"\r\n驳回: " + currentTime+ "\n  " + groupName + " " + fromName+ " 驳回审批。\n  驳回原因："+content);
				recordList.add(rowSet.getString("nbase"));
				recordList.add(rowSet.getString("a0100"));
				recordList.add(new Integer(rowSet.getInt("a00z1")));
				recordList.add(rowSet.getDate("a00z0"));
				dataList.add(recordList);
			}
			dao.batchUpdate("update "+this.gz_tablename+" set appprocess=?,sp_flag2='07' where nbase=? and a0100=? and a00z1=? and a00z0=?",dataList);
			rowSet.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	/**
	 * 工资报审
	 * @throws GeneralException
	 */
	public void gzDataReport()throws GeneralException
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String groupName =getGroupName(this.userview);
			RowSet rowSet = null;
			/*
			String groupName = "";
			RowSet rowSet = dao.search("select groupName from operuser,usergroup where operuser.groupid=usergroup.groupid and username='"
					+this.userview.getUserName()+ "'");
			if (rowSet.next())
				groupName = rowSet.getString(1);
			*/
			Calendar d1 = Calendar.getInstance();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
			String currentTime = dateFormat.format(d1.getTime()); // 当前时间
			
		//	String value="char(appprocess)"+Sql_switcher.concat()+"'   \r\n报批: " + currentTime+ "\n  " + groupName + " " + this.userview.getUserName()+ " 报批给 " + this.manager+"'";
			
			String gz_filterWhl = (String) this.userview.getHm().get("gz_filterWhl");//现在根据gz_filterWhl进行报审，gz_filterWhl是数据上报界面传过来的显示数据sql
			StringBuffer sql=new StringBuffer("update "+this.gz_tablename+" set sp_flag2='02' where 1=1 ");//更新状态
			StringBuffer sql2=new StringBuffer("select count(a0100) from "+this.gz_tablename+" where 1=1 ");//查看是否有报审的数据
			StringBuffer sql3=new StringBuffer("select * from "+this.gz_tablename+" where 1=1 ");//先查出来再追加更新appprocess
			sql.append(gz_filterWhl);
			sql2.append(gz_filterWhl);
			sql3.append(gz_filterWhl);
			if(StringUtils.isBlank(gz_filterWhl) && manager!=null&&manager.length()>0&&!manager.equalsIgnoreCase(this.userview.getUserName())&&!this.userview.isSuper_admin()){
				String unitIdByBusiOutofPriv = SystemConfig.getPropertyValue("unitIdByBusiOutofPriv");
				String b_units=this.userview.getUnitIdByBusiOutofPriv("1");// 1:工资发放  2:工资总额  3:所得税
				if(b_units.length()==0&&(unitIdByBusiOutofPriv!=null&& "1".equals(unitIdByBusiOutofPriv))){
					/**导入数据*/
					String dbpres=this.templatevo.getString("cbase");
					/**应用库前缀*/
					String[] dbarr=StringUtils.split(dbpres, ",");
					StringBuffer sub_str=new StringBuffer("");
					for(int i=0;i<dbarr.length;i++)
					{
						String pre=dbarr[i];
						if(!this.userview.isSuper_admin()&&this.userview.getDbpriv().toString().toLowerCase().indexOf(","+pre.toLowerCase()+",")==-1)
						{
							sub_str.append(" or (upper(nbase)='"+pre.toUpperCase()+"'  and 1=2 )");
						}
						else
						{
							sub_str.append(" or (upper(nbase)='"+pre.toUpperCase()+"'  and a0100 in (select a0100 "+this.userview.getPrivSQLExpression(pre, false)+" ) )");
						}
						
					}
					if(sub_str.length()>0)
					{
						sql.append(" and ( "+sub_str.substring(3)+" )");
						sql2.append(" and ( "+sub_str.substring(3)+" )");
						sql3.append(" and ( "+sub_str.substring(3)+" )");
					}
				}else{
					String privsql = this.getPrivSQL("", "", salaryid+"", b_units);
					sql.append(" and ("+privsql+")");
					sql2.append(" and ("+privsql+")");
					sql3.append(" and ("+privsql+")");
				}
			}
			
//			if(this.controlByUnitcode.equals("1"))
//			{
//				String whl_str=getWhlByUnits();
//				if(whl_str.length()>0)
//				{
//					sql.append(whl_str);
//					sql2+=whl_str;
//					sql3+=whl_str;
//				}
//			}
//			else if(this.manager!=null&&this.manager.length()>0&&!this.manager.equalsIgnoreCase(this.userview.getUserName())&&!this.userview.isSuper_admin())
//			{
//				
//				/**导入数据*/
//				String dbpres=this.templatevo.getString("cbase");
//				/**应用库前缀*/
//				String[] dbarr=StringUtils.split(dbpres, ",");
//				StringBuffer sub_str=new StringBuffer("");
//				for(int i=0;i<dbarr.length;i++)
//				{
//					String pre=dbarr[i];
//					if(!this.userview.isSuper_admin()&&this.userview.getDbpriv().toString().toLowerCase().indexOf(","+pre.toLowerCase()+",")==-1)
//					{
//						sub_str.append(" or (upper(nbase)='"+pre.toUpperCase()+"'  and 1=2 )");
//					}
//					else
//					{
//						sub_str.append(" or (upper(nbase)='"+pre.toUpperCase()+"'  and a0100 in (select a0100 "+this.userview.getPrivSQLExpression(pre, false)+" ) )");
//					}
//					
//				}
//				if(sub_str.length()>0)
//				{
//					sql.append(" and ( "+sub_str.substring(3)+" )");
//					sql2+=" and ( "+sub_str.substring(3)+" )";
//					sql3+=" and ( "+sub_str.substring(3)+" )";
//				}
//			}
			
			sql2.append(" and ( sp_flag2='01' or sp_flag2='07' )  and  sp_flag!='02' and sp_flag!='03' and sp_flag!='06'  ");
			sql3.append(" and ( sp_flag2='01' or sp_flag2='07' )  and  sp_flag!='02' and sp_flag!='03' and sp_flag!='06' ");
			
			
			
			
			
			rowSet=dao.search(sql2.toString());
			if(rowSet.next())
			{
				if(rowSet.getInt(1)==0)
					throw GeneralExceptionHandler.Handle(new Exception("没有可报审的记录"));
			}
			else
				throw GeneralExceptionHandler.Handle(new Exception("没有可报审的记录"));
			
			String toName=getNameByUsername(this.manager);
			String fromName=getNameByUsername(this.userview.getUserName());
			ArrayList dataList=new ArrayList();
			ArrayList recordList=null;
			rowSet=dao.search(sql3.toString());
			while(rowSet.next())
			{
				String appprocess=Sql_switcher.readMemo(rowSet,"appprocess");
				String value="";
				if(appprocess.trim().length()>0)
					value="   \r\n";
				recordList=new ArrayList();
				recordList.add(appprocess+value+"报批: " + currentTime+ "\n  " + groupName + " " +fromName+ " 报批给 " + toName);
				recordList.add(rowSet.getString("nbase"));
				recordList.add(rowSet.getString("a0100"));
				recordList.add(new Integer(rowSet.getInt("a00z1")));
				recordList.add(rowSet.getDate("a00z0"));
				dataList.add(recordList);
			}
			dao.batchUpdate("update "+this.gz_tablename+" set appprocess=? where nbase=? and a0100=? and a00z1=? and a00z0=?",dataList);
			dao.update(sql.toString()+"  and ( sp_flag2='01' or sp_flag2='07' ) and  sp_flag!='02' and sp_flag!='03' and sp_flag!='06' ");
			rowSet.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	/**
	 * 人员月奖金上报
	 * @throws GeneralException
	 */
	public void gzDataReport2()throws GeneralException
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String groupName =getGroupName(this.userview);
			RowSet rowSet = null;
			/*
			String groupName = "";
			RowSet rowSet = dao.search("select groupName from operuser,usergroup where operuser.groupid=usergroup.groupid and username='"
					+this.userview.getUserName()+ "'");
			if (rowSet.next())
				groupName = rowSet.getString(1);
			*/
			Calendar d1 = Calendar.getInstance();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
			String currentTime = dateFormat.format(d1.getTime()); // 当前时间
			
		//	String value="char(appprocess)"+Sql_switcher.concat()+"'   \r\n报批: " + currentTime+ "\n  " + groupName + " " + this.userview.getUserName()+ " 报批给 " + this.manager+"'";
			
			StringBuffer sql=new StringBuffer("update "+this.gz_tablename+" set sp_flag2='02' ");
			sql.append(" where 1=1 ");
			String sql2="select count(a0100) from "+this.gz_tablename+" where 1=1 ";
			String sql3="select * from "+this.gz_tablename+" where 1=1 ";
			if(this.manager!=null&&this.manager.length()>0&&!this.manager.equalsIgnoreCase(this.userview.getUserName()))
			{
				
				/**导入数据*/
				String dbpres=this.templatevo.getString("cbase");
				/**应用库前缀*/
				String[] dbarr=StringUtils.split(dbpres, ",");
				StringBuffer sub_str=new StringBuffer("");
				for(int i=0;i<dbarr.length;i++)
				{
					String pre=dbarr[i];
					
					if(!this.userview.isSuper_admin()&&this.userview.getDbpriv().toString().toLowerCase().indexOf(","+pre.toLowerCase()+",")==-1)
					{
						sub_str.append(" or (upper(nbase)='"+pre.toUpperCase()+"'  and 1=2 )");
					}
					else
					{
						sub_str.append(" or (upper(nbase)='"+pre.toUpperCase()+"'  and a0100 in (select a0100 "+this.userview.getPrivSQLExpression(pre, false)+" ) )");
					}
					
				}
				if("1".equals(this.controlByUnitcode))
				{
					String whl_str=getWhlByUnits();
					if(whl_str.length()>0)
					{
						sql.append(whl_str);
						sql2+=whl_str;
						sql3+=whl_str;
					}
				}
				else
				{
					if(sub_str.length()>0)
					{
						sql.append(" and ( "+sub_str.substring(3)+" )");
						sql2+=" and ( "+sub_str.substring(3)+" )";
						sql3+=" and ( "+sub_str.substring(3)+" )";
					}
				}
			}
			
			sql2+=" and ( sp_flag2='01' or sp_flag2='07' )";
			sql3+=" and ( sp_flag2='01' or sp_flag2='07' )";
			rowSet=dao.search(sql2);
			if(rowSet.next())
			{
				if(rowSet.getInt(1)==0) return;
//					throw GeneralExceptionHandler.Handle(new Exception("没有可报审的记录"));
			}
			else
			    return;
//				throw GeneralExceptionHandler.Handle(new Exception("没有可报审的记录"));
			
			
			ArrayList dataList=new ArrayList();
			ArrayList recordList=null;
			rowSet=dao.search(sql3);
			String toName=getNameByUsername(this.manager);
			String fromName=getNameByUsername(this.userview.getUserName());
			while(rowSet.next())
			{
				String appprocess=Sql_switcher.readMemo(rowSet,"appprocess");
				String value="";
				if(appprocess.trim().length()>0)
					value="   \r\n";
				recordList=new ArrayList();
				recordList.add(appprocess+value+"报批: " + currentTime+ "\n  " + groupName + " " +fromName+ " 报批给 " + toName);
				recordList.add(rowSet.getString("nbase"));
				recordList.add(rowSet.getString("a0100"));
				recordList.add(new Integer(rowSet.getInt("a00z1")));
				recordList.add(rowSet.getDate("a00z0"));
				dataList.add(recordList);
			}
			dao.batchUpdate("update "+this.gz_tablename+" set appprocess=? where nbase=? and a0100=? and a00z1=? and a00z0=?",dataList);
			dao.update(sql.toString()+"  and ( sp_flag2='01' or sp_flag2='07' ) ");
			rowSet.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	/**
	 * 当发现个税表里有非自己建的当月当次数据，需z1自动加1
	 * @throws GeneralException
	 */
	public  void autoAddZ1()throws GeneralException
	{
		try
		{
			ContentDAO dao=new ContentDAO(conn);
			String table="t#"+userview.getUserName()+"_gz_1"; //userview.getUserName()+"_tempZ1Table";
			StringBuffer strsql=new StringBuffer("");
			if(Sql_switcher.searchDbServer()==2)
				strsql.append("create table "+table+" as select ss.nbase,ss.a0100,ss.a00z1,ss.a00z0,ss.a00z3 ");
			else 
				strsql.append("select ss.nbase,ss.a0100,ss.a00z1,ss.a00z0,ss.a00z3  into "+table);
			strsql.append("  from  "+gz_tablename+" ss,salaryhistory gm where ");
			strsql.append(" gm.salaryid="+salaryid+" and ss.nbase=gm.nbase and ss.a0100=gm.a0100 and ss.a00z0=gm.a00z0 and ss.a00z1=gm.a00z1 ");
			strsql.append(" and lower(gm.userflag)<>'"+userview.getUserName().toLowerCase()+"' and ( ss.sp_flag='07' or ss.sp_flag='01')  ");
			
			
			DbWizard dbw=new DbWizard(conn);
	//		if(dbw.isExistTable(table,false))
			{
				dbw.dropTable(table);
				/*
				dbw.execute("delete from "+table);
				strsql.setLength(0);
				strsql.append("insert into "+table+" (nbase,a0100,a00z1,a00z0,a00z3) ");
				strsql.append("select ss.nbase,ss.a0100,ss.a00z1,ss.a00z0,ss.a00z3  ");
				strsql.append("  from  "+gz_tablename+" ss,salaryhistory gm where ");
				strsql.append(" gm.salaryid="+salaryid+" and ss.nbase=gm.nbase and ss.a0100=gm.a0100 and ss.a00z0=gm.a00z0 and ss.a00z1=gm.a00z1 ");
				strsql.append(" and lower(gm.userflag)<>'"+userview.getUserName().toLowerCase()+"' and ( ss.sp_flag='07' or ss.sp_flag='01')  ");
				dbw.execute(strsql.toString());*/
			}
		//	else
			{
				dao.update(strsql.toString());
			}
			
			
			
			strsql.setLength(0);
			strsql.append("update "+table+" set a00z3=(select max(salaryhistory.a00z1) from salaryhistory where salaryid="+salaryid+" and  "+table+".a0100=salaryhistory.a0100 "); 
			strsql.append(" and "+table+".a00z0=salaryhistory.a00z0 and "+table+".nbase=salaryhistory.nbase group by salaryhistory.a0100	) ");
			dao.update(strsql.toString());
			dao.update("update "+table+" set a00z3=a00z3+1");
			
			strsql.setLength(0);
			strsql.append("update "+gz_tablename+" set a00z1=(select a00z3 from "+table+" where   "+table+".a0100="+gz_tablename+".a0100 "); 
			strsql.append(" and "+table+".a00z1="+gz_tablename+".a00z1 and "+table+".a00z0="+gz_tablename+".a00z0 and "+table+".nbase="+gz_tablename+".nbase) ");
			strsql.append(" where exists (select null from "+table+" where   "+table+".a0100="+gz_tablename+".a0100 "); 
			strsql.append(" and "+table+".a00z1="+gz_tablename+".a00z1 and "+table+".a00z0="+gz_tablename+".a00z0 and "+table+".nbase="+gz_tablename+".nbase) ");
			dao.update(strsql.toString());
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	
	
	
	
	/**
	 * 工资报批
	 * @param appealObject  报批人
	 * @throws GeneralException
	 */
	public void gzDataAppeal(String appealObject,String filterWhl)throws GeneralException
	{
	
		
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			/*if(this.manager!=null&&this.manager.length()>0)
				dao.update("update "+this.gz_tablename+" set userflag='"+this.manager+"'");	*/	
		//	SalaryTemplateBo.autoAddZ1(conn, userview, gz_tablename, salaryid+"");
			/* 取得 工资数据表 的当前 发放时间 */
			//LazyDynaBean dataBean=getSalaryPayDate(null,null);  
			/*RowSet rowSet=dao.search("select * from "+this.gz_tablename+" where 1=2");
			ResultSetMetaData metaData=rowSet.getMetaData();
			StringBuffer s1=new StringBuffer(",salaryid,curr_user");
			StringBuffer s2=new StringBuffer(","+this.salaryid+",'"+appealObject+"'");
			for(int i=1;i<=metaData.getColumnCount();i++)
			{				
				    if(this.standardGzItemStr.indexOf("/"+metaData.getColumnName(i).toUpperCase()+"/")==-1)
					    continue;
				    
				    
					if(metaData.getColumnName(i).equalsIgnoreCase("sp_flag"))
					{
						s1.append(","+metaData.getColumnName(i));
						s2.append(",'02'");
					}
					else
					{
						
							s1.append(","+metaData.getColumnName(i));
							s2.append(","+metaData.getColumnName(i));
					
					}
			}
			
		//	SalaryTemplateBo.autoAddZ1(conn, userview, gz_tablename, salaryid+"");
			StringBuffer del=new StringBuffer("delete from salaryhistory where exists (select * from "+this.gz_tablename);
			del.append(" where a0100=salaryhistory.a0100 ");
			if(filterWhl.length()>0)
				del.append(filterWhl);
			del.append(" and  upper(nbase)=upper(salaryhistory.nbase) and  a00z0=salaryhistory.a00z0 and  a00z1=salaryhistory.a00z1  and ( sp_flag='07' or sp_flag='01') ) ");
			del.append(" and salaryid="+this.salaryid);
			dao.delete(del.toString(),new ArrayList());
			
			String sql0="insert into salaryhistory ("+s1.substring(1)+") select "+s2.substring(1)+" from "+this.gz_tablename+" where  ( sp_flag='07' or sp_flag='01' )";
			if(filterWhl.length()>0)
				sql0+=filterWhl;
			dao.update(sql0);*/
			
			
			/*
			
			HashMap appprocessMap=getCurrentSalaryhistoryData();
			
			String groupName="";
			RowSet rowSet=dao.search("select groupName from operuser,usergroup where operuser.groupid=usergroup.groupid and username='"+this.userview.getUserName()+"'");
			if(rowSet.next())
				groupName=rowSet.getString(1);
			String sql0="select * from "+this.gz_tablename+"  where ( sp_flag='07' or sp_flag='01' ) ";
			if(filterWhl.length()>0)
				sql0+=filterWhl;
			rowSet=dao.search(sql0);
			Calendar d=Calendar.getInstance();
			SimpleDateFormat dateFormat =new SimpleDateFormat("yyyy.MM.dd HH:mm");
			String currentTime=dateFormat.format(d.getTime()); //当前时间
			StringBuffer sql=new StringBuffer("");
	//		RecordVo vo=new RecordVo("salaryhistory");
			SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
			
			StringBuffer sql2=new StringBuffer("");
			sql2.append("update salaryhistory set appuser=?,appprocess=?  where a00z1=?");
			sql2.append(" and upper(nbase)=? and salaryid=? and  a0100=? ");
			sql2.append(" and "+Sql_switcher.year("a00z0")+"=? and "+Sql_switcher.month("a00z0")+"=? and "+Sql_switcher.day("a00z0")+"=?");
			PreparedStatement pstmt2=this.conn.prepareStatement(sql2.toString());
			Calendar cd=Calendar.getInstance();
			while(rowSet.next())
			{
				String a0100=rowSet.getString("a0100");
				String nbase=rowSet.getString("nbase");	
	
				Date dd=rowSet.getDate("a00z0");
				cd.setTime(dd);
				String a00z0=df.format(rowSet.getDate("a00z0"));
				String a00z1=rowSet.getString("a00z1");

				String temp=(String)appprocessMap.get(a0100.toLowerCase()+"/"+nbase.toLowerCase()+"/"+a00z0+"/"+a00z1);
				String[] temps={"","",""};
				if(temp!=null)
					temps=temp.split("~");
				if(temps.length>1&&temps[1].equalsIgnoreCase("null"))
					temps[1]="";
				String appuser="";
				if(temps.length>1)
					appuser=this.userview.getUserName()+";"+temps[1];
				else
					appuser=this.userview.getUserName()+";";
				String appprocess=temps[0];
				if(temps[0]!=null&&temps[0].trim().length()>0)
					appprocess+="   \r\n报批: ";
				else
					appprocess+="   报批: ";
				appprocess+=currentTime+"\n  "+groupName+" "+this.userview.getUserName()+" 报批给 "+appealObject;
	
				pstmt2.setString(1, appuser);
				pstmt2.setString(2, appprocess);
				pstmt2.setInt(3, rowSet.getInt("a00z1"));
				pstmt2.setString(4, nbase.toUpperCase());
				pstmt2.setInt(5, this.salaryid);
				pstmt2.setString(6, a0100);
				pstmt2.setInt(7,cd.get(Calendar.YEAR));
				pstmt2.setInt(8, cd.get(Calendar.MONTH)+1);
				pstmt2.setInt(9,cd.get(Calendar.DATE));
				pstmt2.addBatch();
				
			}
			pstmt2.executeBatch();
			pstmt2.close();
			rowSet.close();
			*/
			
			StringBuffer sql=new StringBuffer("");
			
			//			更改 工资数据表中的审批状态 
			String sql0="update "+this.gz_tablename+" set Sp_flag='02' where ( sp_flag='07' or sp_flag='01') ";
			sql0+=" and exists (select null from salaryhistory where salaryid="+this.salaryid+"  and  salaryhistory.a0100="+this.gz_tablename+".a0100 ";
			sql0+=" and  upper(salaryhistory.nbase)=upper("+this.gz_tablename+".nbase) and  salaryhistory.a00z0="+this.gz_tablename+".a00z0 and  salaryhistory.a00z1="+this.gz_tablename+".a00z1  ) ";
			if(filterWhl.length()>0)
				sql0+=filterWhl;
			
			dao.update(sql0);
			//将工资发放记录表对应的纪录改为 执行中状态
			SalaryPkgBo pgkbo=new SalaryPkgBo(this.conn,this.userview,0);
			String username=this.userview.getUserName();
			if(this.manager!=null&&this.manager.length()>0)
				username=this.manager;
			LazyDynaBean abean=pgkbo.searchCurrentDate2(String.valueOf(this.salaryid),username);
			String strYm=(String)abean.get("strYm");
			String strC=(String)abean.get("strC");
			setExtendLogState("05",strYm,strC);  
			
			sql.setLength(0);
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
			{
				sql.append("update "+this.gz_tablename+" set appprocess=(select appprocess from salaryhistory where ");
				sql.append("  salaryhistory.a0100="+this.gz_tablename+".a0100 and  upper(salaryhistory.nbase)=upper("+this.gz_tablename+".nbase) and  salaryhistory.a00z0="+this.gz_tablename+".a00z0 and  salaryhistory.a00z1="+this.gz_tablename+".a00z1 "); 
				sql.append(" and salaryid="+this.salaryid+") where exists (select null  from salaryhistory where ");
				sql.append("  salaryhistory.a0100="+this.gz_tablename+".a0100 and  upper(salaryhistory.nbase)=upper("+this.gz_tablename+".nbase) and  salaryhistory.a00z0="+this.gz_tablename+".a00z0 and  salaryhistory.a00z1="+this.gz_tablename+".a00z1  and salaryid="+this.salaryid+" )"); 
				if(filterWhl.length()>0)
					sql.append(filterWhl);
			
			}
			else
			{
				sql.append("update    "+this.gz_tablename+"   set   "+this.gz_tablename+".appprocess= salaryhistory.appprocess "); 
				sql.append(" from   salaryhistory");
				sql.append(" where  salaryhistory.a0100="+this.gz_tablename+".a0100 and ");
				sql.append(" upper(salaryhistory.nbase)=upper("+this.gz_tablename+".nbase) ");
				sql.append(" and  salaryhistory.a00z0="+this.gz_tablename+".a00z0 and ");
				sql.append(" salaryhistory.a00z1="+this.gz_tablename+".a00z1  and salaryhistory.salaryid="+this.salaryid);
				if(filterWhl.length()>0)
					sql.append(filterWhl);
			}
				
			dao.update(sql.toString());
			//发送 邮件 和 短信通知
			if(ctrlparam.getValue(SalaryCtrlParamBo.NOTE)!=null&&ctrlparam.getValue(SalaryCtrlParamBo.NOTE).length()>0)
			{
				sendMessage(appealObject,"","薪资报批");
			}
			
			
			
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	/**
	 * 求移动电话号码
	 * @return
	 */
	private String getMobileNumber()
	{
        RecordVo vo=ConstantParamter.getConstantVo("SS_MOBILE_PHONE");
        if(vo==null)
        	return "";
        String field_name=vo.getString("str_value");
        if(field_name==null|| "".equals(field_name))
        	return "";
        FieldItem item=DataDictionary.getFieldItem(field_name);
        if(item==null)
        	return "";
        /**分析是否构库*/
        if("0".equals(item.getUseflag()))
        	return "";
        return field_name; 
	}
	
	/**
	 * 从系统邮件服务器设置中得到发送邮件的地址
	 * @return
	 */
	public String getFromAddr() throws GeneralException 
	{
		String str = "";
        RecordVo stmp_vo=ConstantParamter.getConstantVo("SS_STMP_SERVER");
        if(stmp_vo==null)
        	return "";
        String param=stmp_vo.getString("str_value");
        if(param==null|| "".equals(param))
        	return "";
        try
        {
	        Document doc = PubFunc.generateDom(param);
	        Element root = doc.getRootElement();
	        Element stmp=root.getChild("stmp");	
	        str=stmp.getAttributeValue("from_addr");
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        	throw GeneralExceptionHandler.Handle(ex);
        }  
        return str;
	}
	
	
	/**
	 * 根据部门id找单位id
	 * @param codeid
	 * @return
	 */
	public String getUnByUm(String codeid)
	{
		String desc="";
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=null;
			while(true)
			{
				rowSet=dao.search("select * from organization where codeitemid=(select parentid from organization where codeitemid='"+codeid+"')");
				if(rowSet.next())
				{
					if("UN".equalsIgnoreCase(rowSet.getString("codesetid")))
					{
						desc=rowSet.getString("codeitemdesc");
						break;
					}
					else
						codeid=rowSet.getString("codeitemid");
				}
				else
					break;
				rowSet.close();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return desc;
	}
	
	
	
	
	/**
	 * 根据用户名获得关联用户的姓名，如没有关联用户则获得用户的全称，否则得到用户名
	 * @param username
	 * @author dengc
	 * @serialData 2013-11-27
	 * @return
	 */
	private String getNameByUsername(String username)
	{
		String name=username;
		RowSet rowSet=null; 
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			rowSet=dao.search("select a0100,nbase,fullname   from operuser,usergroup where operuser.groupid=usergroup.groupid and username='"+username+"'");
			if(rowSet.next())
			{
				String a0100=rowSet.getString("a0100")!=null?rowSet.getString("a0100").trim():"";
				String nbase=rowSet.getString("nbase")!=null?rowSet.getString("nbase").trim():"";
				String fullname=rowSet.getString("fullname")!=null?rowSet.getString("fullname").trim():""; 
				if(a0100.length()>0&&nbase.length()>0)
				{
					rowSet=dao.search("select a0101 from "+nbase+"A01 where a0100='"+a0100+"'");
					if(rowSet.next())
						name=rowSet.getString("a0101");
				}
				else if(fullname.length()>0)
				{
					name=fullname;
				}
				
			}
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return name;
	}
	
	
	/**
	 * 获得当前用户的组号，如果关联了自助用户，则为所在部门的名称->单位名称
	 * @param _userview
	 * @author dengc
	 * @serialData 2013-11-27
	 * @return
	 */
	private String getGroupName(UserView _userview)
	{
		RowSet rowSet=null;
		String groupName="";
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			if(_userview.getA0100()!=null&&_userview.getA0100().trim().length()>0)
				rowSet=dao.search("select b0110,e0122 from "+_userview.getDbname()+"A01 where a0100='"+_userview.getA0100()+"'");
			else
				rowSet=dao.search("select groupName from operuser,usergroup where operuser.groupid=usergroup.groupid and username='"+_userview.getUserName()+"'");
			if(rowSet.next())
			{
				if(_userview.getA0100()!=null&&_userview.getA0100().trim().length()>0)
				{
					String b0110=rowSet.getString("b0110")!=null?rowSet.getString("b0110").trim():"";
					String e0122=rowSet.getString("e0122")!=null?rowSet.getString("e0122").trim():"";
					if(e0122.length()>0)
						groupName=AdminCode.getCodeName("UM",e0122);
					else if(b0110.length()>0)
						groupName=AdminCode.getCodeName("UN",b0110);
				}
				else
					groupName=rowSet.getString(1);
			}
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return groupName;
	}
	/**
	 * //有fullname优先fullname 其次是a0101 最后才是usrname
	 * @param usrname
	 * @param con
	 * @return
	 * @throws GeneralException
	 */
	public String getFullName(String usrname,Connection con) throws GeneralException{
		String name="";
		try{
			ContentDAO dao = new ContentDAO(con);			
			String sql="select fullname from operuser where username='"+usrname+"'";
			String _sql="select a0101 from usra01 where username='"+usrname+"'";
			RowSet rs=dao.search(sql);
			RowSet rs1=dao.search(_sql);
			if(rs.next()){
				if(rs.getString("fullname")!=null&&!"".equals(rs.getString("fullname"))){//有fullname优先fullname
					name=rs.getString("fullname");
				}else if(rs1.next()){
					if(rs1.getString("a0101")!=null&&!"".equals(rs1.getString("a0101"))){//其次是a0101
						name=rs.getString("a0101");
					}else{//都没有就是登录名
						name=usrname;
					}
				}else{
					name=usrname;
				}
			}else{
				name=usrname;
			}
		}		
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return name;
	}
	
	
	
	/**
	 * 给当前审批人发送邮件及短信
	 * @throws GeneralException
	 */
	private void sendMessage(String appealObject,String content,String title)throws GeneralException
	{
		try
		{
			
			if(templatevo!=null&&templatevo.getString("cstate")!=null&& "1".equals(templatevo.getString("cstate")))
				title=title.replaceAll("薪资","保险");
			ContentDAO dao=new ContentDAO(this.conn);			
//			消息内容
			/* 薪资发放-报批 邮件格式没有按照自定义主题来发送 xiaoyun 2014-10-21 start */
			if(appealObject.indexOf(",")==-1&&ctrlparam.getValue(SalaryCtrlParamBo.NOTE) != null && ctrlparam.getValue(SalaryCtrlParamBo.NOTE).trim().length() > 0)
			{
				RowSet rowSet=dao.search("select subject,content from email_name where id="+ctrlparam.getValue(SalaryCtrlParamBo.NOTE));
				/* 薪资发放-报批 邮件格式没有按照自定义主题来发送 xiaoyun 2014-10-21 end */
				SimpleDateFormat f=new SimpleDateFormat("yyyy-MM-dd");
				Date d=new Date();
				if(rowSet.next())
				{
					content=content.replaceAll("＃", "#");
					content=Sql_switcher.readMemo(rowSet,"content");
					
					/* 薪资发放-报批 邮件格式没有按照自定义主题来发送 xiaoyun 2014-10-21 start */
					String tempTitle = rowSet.getString("subject");
					if(tempTitle != null && tempTitle.trim().length()>0) {
						title = tempTitle;
					}
					/* 薪资发放-报批 邮件格式没有按照自定义主题来发送 xiaoyun 2014-10-21 end */
					
					content=content.replaceAll("#用户名#",getFullName(appealObject,conn));//this.userview.getUserFullName()   这个名字写反了   zhaoxg add 2014-8-8
					content=content.replaceAll("#日期#",f.format(d));
					f=new SimpleDateFormat("hh:mm");
					content=content.replaceAll("#时间#",f.format(d));
			/*		if(userview.getUserOrgId()!=null&&userview.getUserOrgId().length()>0)
					{
						content=content.replaceAll("#单位名称#",AdminCode.getCodeName("UN", userview.getUserOrgId()));
					}
					*/
					String privCode=userview.getManagePrivCode();
					String privCodeValue=userview.getManagePrivCodeValue();
					if(privCodeValue!=null&&privCodeValue.trim().length()>0)
					{
						if("UN".equalsIgnoreCase(privCode))
						{
							content=content.replaceAll("#单位名称#",AdminCode.getCodeName("UN", privCodeValue));
							content=content.replaceAll("#部门名称#","");
						}
						if("UM".equalsIgnoreCase(privCode))
						{
							content=content.replaceAll("#部门名称#",AdminCode.getCodeName("UM", privCodeValue));
							if(content.indexOf("#单位名称#")!=-1)
								content=content.replaceAll("#单位名称#",getUnByUm(privCodeValue));
							
						}
					}
					
				/*	if(userview.getUserDeptId()!=null&&userview.getUserDeptId().length()>0)
					{
						content=content.replaceAll("#部门名称#",AdminCode.getCodeName("UM", userview.getUserDeptId()));
					}*/
					content=content.replaceAll("#工资类别名称#",this.templatevo.getString("cname"));
				}
				rowSet.close();
				
			}
			
			String[] receiveMens=null;
			if(appealObject.indexOf(",")!=-1)
			{
				receiveMens=appealObject.split(",");
				String name=this.userview.getUserFullName();
				if(name==null||name.trim().length()==0)
					name=this.userview.getUserName();
				
			}
			else
			{
				receiveMens=new String[1];
				receiveMens[0]=appealObject;
			}
			
			for(int i=0;i<receiveMens.length;i++)
			{
				
				if(receiveMens[i]==null||receiveMens[i].trim().length()==0)
					continue;
				appealObject=receiveMens[i];
				String email="";
				String phone="";
				RecordVo vo=new RecordVo("operuser");
				vo.setString("username",appealObject);
				vo=dao.findByPrimaryKey(vo);
				String dbase=vo.getString("nbase");
				String a0100=vo.getString("a0100");
				String password=vo.getString("password");
				if(a0100==null|| "".equalsIgnoreCase(a0100))
				{
					email=vo.getString("email");
					phone=vo.getString("phone");
				}
				else
				{
					RecordVo avo=ConstantParamter.getRealConstantVo("SS_EMAIL");
					String email_field=avo.getString("str_value");
					String field_name=getMobileNumber();
					String sql= "select "+email_field;
					if(email_field!=null&&email_field.length()>0&&field_name.length()>0)
						sql+=",";
					if(field_name.length()>0)	
						sql+=field_name;
					sql+=" from "+dbase+"A01 where a0100='"+a0100+"'";
					if((email_field!=null&&email_field.length()>0)||field_name.length()>0)
					{
						RowSet rset=dao.search(sql);
						if(rset.next())
						{
							if(email_field!=null&&email_field.length()>0)							
								email=rset.getString(email_field);
							if(field_name!=null&&field_name.length()>0)
								phone=rset.getString(field_name);
						}
						rset.close();
					}
				}
				
				
				if(ctrlparam.getValue(SalaryCtrlParamBo.NOTE,"sms")!=null&& "1".equals(ctrlparam.getValue(SalaryCtrlParamBo.NOTE,"sms")))
				{
					if(phone!=null&&phone.length()>0)
					{
						if(SystemConfig.getPropertyValue("clientName")!=null&& "bjyd".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))
						{
							NoteCheckSend sendBo=new NoteCheckSend();
							sendBo.sendMesg(phone,content);
						}
						else
						{
							SmsBo smsbo=new SmsBo(this.conn);
							smsbo.sendMessage2(this.userview.getUserFullName()!=null?this.userview.getUserFullName():this.userview.getUserName(),vo.getString("fullname")!=null?vo.getString("fullname"):vo.getString("username"),phone,content);
						}
					}
				}
				
				if(ctrlparam.getValue(SalaryCtrlParamBo.NOTE,"mail")!=null&& "1".equals(ctrlparam.getValue(SalaryCtrlParamBo.NOTE,"mail")))
				{
					if(email!=null&&email.length()>0)
					{
						EMailBo bo = new EMailBo(this.conn,true,"");
						String fromaddr=this.getFromAddr();
						
				//		content=content.replaceAll(" ","&nbsp;&nbsp;");
						content=content.replaceAll("\r\n","<br>");
						String weixincontent = content;
						
						//#自动登录连接#
						String url="<a href='"+ this.userview.getServerurl() +"/gz/gz_accounting/gz_sp_orgtree.do?b_query=link&ori=0&zjjt=1&salaryid="+this.salaryid+"&appfwd=1&etoken="+PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(appealObject+","+password))+"' >自动登录链接</a>";
						content=content.replaceAll("#自动登录链接#",url);
						weixincontent = weixincontent.replaceAll("#自动登录链接#","");
						//content+="<br>"+url;
						bo.sendEmail(title,content,"",fromaddr,email);
						String corpid = (String) ConstantParamter.getAttribute("wx","corpid");  
						if(corpid!=null&&corpid.length()>0){//推送微信公众号  zhaoxg add 2015-5-5
							String username = this.getZizhuUsername(appealObject);
							WeiXinBo.sendMsgToPerson(username, title, weixincontent, "http://www.hjsoft.com.cn:8089/UserFiles/Image/tongzhi.png", "");
						}
						String dd_corpid = (String) ConstantParamter.getAttribute("DINGTALK","corpid");  
						if(dd_corpid!=null&&dd_corpid.length()>0){//推送钉钉  dengcan add 2017-6-1
							String username = this.getZizhuUsername(appealObject);
							DTalkBo.sendMessage(username, title, weixincontent, "http://www.hjsoft.com.cn:8089/UserFiles/Image/tongzhi.png", "");
						}
						
						
					}

				}
				
			}
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	private LazyDynaBean getUserNamePassword(String value)
	{
		if(value==null||value.length()<=0)
		{
			return null;
		}
		String nbase=value.substring(0,3);
		String a0100=value.substring(3);
		AttestationUtils utils=new AttestationUtils();
		LazyDynaBean fieldbean=utils.getUserNamePassField();
		String username_field=(String)fieldbean.get("name");
	    String password_field=(String)fieldbean.get("pass");
	    StringBuffer sql=new StringBuffer();
	    sql.append("select a0101,"+username_field+" username,"+password_field+" password,a0101 from "+nbase+"A01");
	    sql.append(" where a0100='"+a0100+"'");
	    List rs=ExecuteSQL.executeMyQuery(sql.toString());
	    LazyDynaBean rec=null;
	    if(rs!=null&&rs.size()>0)
	    {
	    	rec=(LazyDynaBean)rs.get(0);	    	
	    }
	    return rec;
	}
	
	
	
	
	/**
	 * 取得薪资历史表中 涉及到的薪资发放临时表
	 * @param dataBean
	 * @param selectGzRecords
	 * @param opt
	 */
	public ArrayList getPrimitiveDataTable_group(LazyDynaBean dataBean,String where_sql,String opt)
	{
		ArrayList primitiveDataTable=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			
			if(this.manager!=null&&this.manager.trim().length()>0)
			{
				primitiveDataTable.add(this.manager+"_salary_"+salaryid);
			}
			else
			{
				StringBuffer sql2=new StringBuffer("select distinct userflag ");
				StringBuffer temp_sql=new StringBuffer("");
				temp_sql.append(" from salaryhistory where ");
				temp_sql.append(" exists ("+where_sql+") ");			
				temp_sql.append(" and Salaryid="+this.salaryid);
				temp_sql.append(" and "+Sql_switcher.year("A00Z2")+"="+(String)dataBean.get("year") );
				temp_sql.append(" and "+Sql_switcher.month("A00Z2")+"="+(String)dataBean.get("month") );
				temp_sql.append(" and "+Sql_switcher.day("A00Z2")+"="+(String)dataBean.get("day") );
				temp_sql.append(" and A00Z3="+(String)dataBean.get("count"));

				if("appeal".equalsIgnoreCase(opt)|| "reject".equalsIgnoreCase(opt))
					temp_sql.append(" and curr_user='"+this.userview.getUserId()+"' and ( sp_flag='02' or sp_flag='07' )  ");
				if("confirm".equalsIgnoreCase(opt))
					temp_sql.append(" and curr_user='"+this.userview.getUserId()+"' and  sp_flag='02'  ");

				sql2.append(temp_sql.toString());
				
				RowSet rowSet=dao.search(sql2.toString());
				while(rowSet.next())
				{
					primitiveDataTable.add(rowSet.getString(1)+"_salary_"+salaryid);
				}
				rowSet.close();
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return primitiveDataTable;
	}
	
	
	
	
	
	
	
	
	/**
	 * 取得薪资历史表中 涉及到的薪资发放临时表
	 * @param dataBean
	 * @param selectGzRecords
	 * @param opt
	 */
	public ArrayList getPrimitiveDataTable(LazyDynaBean dataBean,String selectGzRecords,String opt,String whl_str)
	{
		ArrayList primitiveDataTable=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			
			if(this.manager!=null&&this.manager.trim().length()>0)
			{
				primitiveDataTable.add(this.manager+"_salary_"+salaryid);
			}
			else
			{
				StringBuffer sql2=new StringBuffer("select distinct userflag ");
				StringBuffer temp_sql=new StringBuffer("");
				temp_sql.append(" from salaryhistory "+this._withNoLock+" where Salaryid="+this.salaryid);
				temp_sql.append(" and "+Sql_switcher.year("A00Z2")+"="+(String)dataBean.get("year") );
				temp_sql.append(" and "+Sql_switcher.month("A00Z2")+"="+(String)dataBean.get("month") );
				temp_sql.append(" and "+Sql_switcher.day("A00Z2")+"="+(String)dataBean.get("day") );
				temp_sql.append(" and A00Z3="+(String)dataBean.get("count"));
	
				sql2.append(temp_sql.toString());
				
				if("confirmGroup".equalsIgnoreCase(opt))
				{
					sql2.append(" and lower(curr_user)='"+this.userview.getUserName().toLowerCase()+"' and sp_flag='02' ");
					sql2.append(" and exists( "+selectGzRecords+" ) ");
					
					RowSet rowSet=dao.search(sql2.toString());
					while(rowSet.next())
					{
						primitiveDataTable.add(rowSet.getString(1)+"_salary_"+salaryid);
					}
					rowSet.close();
				}
				else if("confirmAll".equals(opt)|| "appealAll".equals(opt)|| "rejectAll".equals(opt))
				{
					if("confirmAll".equals(opt))
					{
						sql2.append(" and lower(curr_user)='"+this.userview.getUserName().toLowerCase()+"' and sp_flag='02' ");
						if(whl_str.length()>0)
							sql2.append(whl_str);
					}
					else if("appealAll".equals(opt)|| "rejectAll".equals(opt))
					{
						sql2.append(" and lower(curr_user)='"+this.userview.getUserName().toLowerCase()+"' and ( sp_flag='02' or sp_flag='07' ) ");
						if(whl_str.length()>0)
							sql2.append(whl_str);
					}
					RowSet rowSet=dao.search(sql2.toString());
					while(rowSet.next())
					{
						primitiveDataTable.add(rowSet.getString(1)+"_salary_"+salaryid);
					}
					rowSet.close();
					
				}
				else
				{
					if(selectGzRecords==null)
					{
						primitiveDataTable.add(this.gz_tablename);
					}
					else
					{
						HashSet set=new HashSet();
						String[] selects=selectGzRecords.split("#");
						int num=selects.length/16+1;
						StringBuffer whl=new StringBuffer("");
						Calendar d=Calendar.getInstance();
						for(int n=0;n<num;n++)
						{
							whl.setLength(0);
							int a=0;
							for(int i=n*15;i<selects.length;i++)
							{
								a++;
								String[] a_selects=selects[i].split("/");
								set.add(a_selects[4]);
								if(a>=15)
									break;
							}
							
						}	
						
						
						for(Iterator t=set.iterator();t.hasNext();)
						{
							String key=(String)t.next();
							primitiveDataTable.add(key+"_salary_"+salaryid);
						}
						
					}
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return primitiveDataTable;
	}
	
	
	public ArrayList getTable(LazyDynaBean dataBean,String selectGzRecords,String opt,String whl_str)
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);		
				StringBuffer sql2=new StringBuffer("select distinct userflag,appuser ");
				StringBuffer temp_sql=new StringBuffer("");
				temp_sql.append(" from salaryhistory "+this._withNoLock+" where Salaryid="+this.salaryid);
				temp_sql.append(" and "+Sql_switcher.year("A00Z2")+"="+(String)dataBean.get("year") );
				temp_sql.append(" and "+Sql_switcher.month("A00Z2")+"="+(String)dataBean.get("month") );
				temp_sql.append(" and "+Sql_switcher.day("A00Z2")+"="+(String)dataBean.get("day") );
				temp_sql.append(" and A00Z3="+(String)dataBean.get("count"));	
				sql2.append(temp_sql.toString());
				
				String  reject_mode="1";
				if("reject".equals(opt)|| "rejectAll".equals(opt))
				{
				    reject_mode=this.ctrlparam.getValue(SalaryCtrlParamBo.FLOW_CTRL,"reject_mode");
					if(reject_mode==null||reject_mode.trim().length()==0)
						reject_mode="1";  //驳回方式  1:逐级驳回  2：驳回到发起人
				}
				
				if("rejectAll".equals(opt)){

//						if("1".equals(reject_mode)){
//							sql2=new StringBuffer("select distinct appuser ");
//							sql2.append(temp_sql.toString());
//						}
						sql2.append(" and lower(curr_user)='"+this.userview.getUserName().toLowerCase()+"' and ( sp_flag='02' or sp_flag='07' ) ");
						if(whl_str.length()>0)
							sql2.append(whl_str);


					RowSet rowSet=dao.search(sql2.toString());
					if("1".equals(reject_mode)){//逐级驳回，找驳回给谁
						while(rowSet.next())
						{
							String temp=rowSet.getString("appuser");
							list.add(temp.substring(0, temp.indexOf(";"))+"#"+rowSet.getString("userflag"));
						}
					}else{
						while(rowSet.next())
						{
							String temp=rowSet.getString("userflag");
							list.add(temp+"#"+rowSet.getString("userflag"));
						}
					}
					rowSet.close();					
				}else if("reject".equals(opt)){//个别驳回
					if("1".equals(reject_mode)){//逐级驳回，找驳回给谁
						String[] selects=selectGzRecords.split("#");
						StringBuffer whl=new StringBuffer("");
						for(int i=0;i<selects.length;i++){
							String[] a_selects=selects[i].split("/");
							if(i==0){
								whl.append(a_selects[0]);
							}else{
								whl.append(",");
								whl.append(a_selects[0]);
							}
						}
						sql2=new StringBuffer("select distinct userflag,appuser ");
						sql2.append(temp_sql.toString());
						sql2.append(" and lower(curr_user)='"+this.userview.getUserName().toLowerCase()+"' and ( sp_flag='02' or sp_flag='07' ) ");
						sql2.append(" and a0100 in ("+whl+")");
						if(whl_str.length()>0)
							sql2.append(whl_str);
						RowSet rowSet=dao.search(sql2.toString());
						while(rowSet.next())
						{
							String temp=rowSet.getString("appuser");
							list.add(temp.substring(0, temp.indexOf(";"))+"#"+rowSet.getString("userflag"));
						}
					}else{
						HashSet set=new HashSet();
						String[] selects=selectGzRecords.split("#");
						int num=selects.length/16+1;
						StringBuffer whl=new StringBuffer("");
						for(int n=0;n<num;n++)
						{
							whl.setLength(0);
							int a=0;
							for(int i=n*15;i<selects.length;i++)
							{
								a++;
								String[] a_selects=selects[i].split("/");
								set.add(a_selects[4]);
								if(a>=15)
									break;
							}							
						}												
						for(Iterator t=set.iterator();t.hasNext();)
						{
							String key=(String)t.next();
							list.add(key+"#"+key);
						}		
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
	 * 取得 工资数据表 的当前 归属时间
	 * @return
	 * @throws GeneralException
	 */
	public LazyDynaBean getSalaryPigeonholeDateFromHistory(String whl)throws GeneralException
	{
		LazyDynaBean abean=new LazyDynaBean();
		try
		{
			
				ContentDAO dao=new ContentDAO(this.conn);
				RowSet rowSet=dao.search("select a00z0,a00z1 from salaryhistory "+this._withNoLock+" where 1=1 "+whl);
				if(rowSet.next())
				{
					Calendar c=Calendar.getInstance();
					Date d=rowSet.getDate(1);
					c.setTime(d);
					String a00z1=rowSet.getString(2);
					abean.set("year",String.valueOf(c.get(Calendar.YEAR)));
					abean.set("month",String.valueOf(c.get(Calendar.MONTH)+1));
					abean.set("day",String.valueOf(c.get(Calendar.DATE)));
					abean.set("count",a00z1);
				}
				rowSet.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return abean;
	}
	
	
	
	
	/**
	 * 取得 工资数据表 的当前 归属时间
	 * @return
	 * @throws GeneralException
	 */
	public LazyDynaBean getSalaryPigeonholeDate()throws GeneralException
	{
		LazyDynaBean abean=new LazyDynaBean();
		try
		{
			
				ContentDAO dao=new ContentDAO(this.conn);
				RowSet rowSet=dao.search("select a00z0,a00z1 from "+this.gz_tablename);
				if(rowSet.next())
				{
					Calendar c=Calendar.getInstance();
					Date d=rowSet.getDate(1);
					c.setTime(d);
					String a00z1=rowSet.getString(2);
					abean.set("year",String.valueOf(c.get(Calendar.YEAR)));
					abean.set("month",String.valueOf(c.get(Calendar.MONTH)+1));
					abean.set("day",String.valueOf(c.get(Calendar.DATE)));
					abean.set("count",a00z1);
				}
				rowSet.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return abean;
	}
	
	
	
	/**
	 * 取得 工资数据表 的当前 发放时间
	 * @return
	 * @throws GeneralException
	 */
	public LazyDynaBean getSalaryPayDate(String bosdate,String count)throws GeneralException
	{
		LazyDynaBean abean=new LazyDynaBean();
		try
		{
			if(bosdate==null)
			{
				ContentDAO dao=new ContentDAO(this.conn);
				RowSet rowSet=dao.search("select a00z2,a00z3 from "+this.gz_tablename);
				if(rowSet.next())
				{
					Calendar c=Calendar.getInstance();
					Date d=rowSet.getDate(1);
					c.setTime(d);
					String a00z3=rowSet.getString(2);
					abean.set("year",String.valueOf(c.get(Calendar.YEAR)));
					abean.set("month",String.valueOf(c.get(Calendar.MONTH)+1));
					abean.set("day",String.valueOf(c.get(Calendar.DATE)));
					abean.set("count",a00z3);
				}
				rowSet.close();
			}
			else
			{
				String[] temps=bosdate.split("\\.");
				abean.set("year",temps[0]);
				abean.set("month",String.valueOf(Integer.parseInt(temps[1])));
				abean.set("day",String.valueOf(Integer.parseInt(temps[2])));
				abean.set("count",count);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return abean;
	}
	
	
	
	/**
	 * 根据条件取得工资历史数据表中审批意见 (汇总审批)
	 * where_sql: (select null from XXX where XXXX.a00z0=salaryhistory.a00z0 and  XXXX.a00z1=salaryhistory.a00z1 XXXX.a0100=salaryhistory.a0100 and lower(XXXX.nbase)=lower(salaryhistory.nbase) and XXXXXX )
	 * opt : appeal or reject or confirm
	 * @return
	 */
	public HashMap getCurrentSalaryhistoryData_group(String where_sql,String bosdate,String count,String opt)throws GeneralException
	{
		HashMap appprocessMap=new HashMap();
		try
		{
			
			ContentDAO dao=new ContentDAO(this.conn);
			String[] temps=bosdate.split("\\.");
			StringBuffer sql=new StringBuffer("select  a0100,nbase,appprocess,appuser,curr_user,a00z0,a00z1 from salaryhistory ");
						 sql.append(" where exists ("+where_sql+") ");
						 sql.append(" and "+Sql_switcher.year("A00Z2")+"="+temps[0]+" and Salaryid="+this.salaryid);
						 sql.append(" and "+Sql_switcher.month("A00Z2")+"="+Integer.parseInt(temps[1])+"  and "+Sql_switcher.day("A00Z2")+"="+Integer.parseInt(temps[2]));
						 sql.append(" and A00Z3="+count);
			 if("appeal".equalsIgnoreCase(opt)|| "reject".equalsIgnoreCase(opt))
						 sql.append(" and curr_user='"+this.userview.getUserId()+"' and ( sp_flag='02' or sp_flag='07' )  ");
			 if("confirm".equalsIgnoreCase(opt))
				 		sql.append(" and curr_user='"+this.userview.getUserId()+"' and  sp_flag='02'  ");
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			Calendar d = Calendar.getInstance();

			RowSet rowSet = dao.search(sql.toString());
			String a0100 = "";
			String nbase = "";
			String a00z0 = "";
			String a00z1 = "";
			String appprocess = "";
			String appuser = "";
			while (rowSet.next()) {
				a0100 = rowSet.getString("a0100");
				nbase = rowSet.getString("nbase");
				a00z0 = df.format(rowSet.getDate("a00z0"));
				a00z1 = rowSet.getString("a00z1");
				appprocess = Sql_switcher.readMemo(rowSet, "appprocess");
				appuser = rowSet.getString("appuser")!=null?rowSet.getString("appuser"):"";
				appprocessMap.put(a0100.toLowerCase() + "/"
						+ nbase.toLowerCase() + "/" + a00z0 + "/" + a00z1,
						appprocess + "~" + appuser + "~"
								+ rowSet.getString("curr_user"));
			}
			rowSet.close();
		
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return appprocessMap;
	}
	
	/**<?xml version="1.0" encoding = "GB2312" ?>
	<msg>
		<gzperiod>工资年月标识，格式：20120701</ gzperiod >
		<appresult>0或1，1表示批准，0表示驳回</appresult>
		<allow>单据ID1;单据ID2;单据ID3</allow >  --批准单据
		<disallow>单据ID1(人员编号,人员编号);单据ID2(人员编号,人员编号);单据ID3(人员编号,人员编号)</disallow>  --驳回单据
		<disallowdesc>驳回原因</disallowdesc >
	</msg>
    */
	public String getRjSendXML(String selectRecords,String content){
		StringBuffer buf = new StringBuffer();
		RowSet rowSet = null;
		try{
            ContentDAO dao=new ContentDAO(this.conn);
			String[] selects=selectRecords.split("#");
			Calendar d=Calendar.getInstance();
			StringBuffer whl=new StringBuffer("");
			String clientName = SystemConfig.getPropertyValue("clientName");
			if(clientName==null||clientName.trim().length()==0)
				throw GeneralExceptionHandler.Handle(new Exception("系统没有设置clientName，请在system.properties文件中设置！"));
			ResourceBundle    myResource = ResourceBundle.getBundle(clientName);
	        String salaryid=myResource.getString("S"+this.salaryid);  //绩效薪资类别ID
	        if(salaryid==null||salaryid.trim().length()==0)
	        	throw GeneralExceptionHandler.Handle(new Exception(clientName+".properties文件中没有设置绩效薪资类别！"));
	        String jobId=myResource.getString("jobId");
	        if(jobId==null||jobId.trim().length()==0)
	        	throw GeneralExceptionHandler.Handle(new Exception(clientName+".properties文件中没有设置数据导入映射关系的作业类标识！"));
	        ConstantXml constantXml = new ConstantXml(conn, "IMPORTINFO","params");
    		List listEl = constantXml.getAllChildren("/params");
    		String dj="";
    		String ryh="";
        	for (int i = 0; i < listEl.size(); i++) {
    			Element els = (Element) listEl.get(i);
         		String id = els.getAttributeValue("id");
    	    	Map connMap = new HashMap();
	    	    // 作业类
	        	String jobClass = constantXml.getTextValue("/params/param[@id='" + id+ "']/jobclass");
	        	// 是否启用
	        	String enable = constantXml.getTextValue("/params/param[@id='"+ id + "']/enable");
	        	if (!"1".equals(enable) || !jobId.equalsIgnoreCase(jobClass)) {
	         		continue;
	        	}
	        	// 名称
	        	String name = constantXml.getTextValue("/params/param[@id='"+ id + "']/name");
    	    	// 人员库
    	    	String nbase = constantXml.getTextValue("/params/param[@id='"+ id + "']/nbase");
	        	// 数据库类型
	        	String dbType = constantXml.getTextValue("/params/param[@id='"+ id + "']/dbtype");
	        	connMap.put("dbType", dbType);
	        	// 指标关联
	        	List list = constantXml.getAllChildren("/params/param[@id='"+ id + "']/mappings");
	        	for (int j = 0; j < list.size(); j++) {
	    	    	Element el = (Element) list.get(j);
		        	if ("fieldref".equals(el.getName())) {
		    	    	String hrfield = el.getAttributeValue("hrfield");
		    	    	String extfield = el.getAttributeValue("extfield");
		    	    	if("pk_sjxgzzhd_h".equalsIgnoreCase(extfield))
		    	    	{
		    	    		dj=hrfield;
		    	    	}
		    	    	if("psncode".equalsIgnoreCase(extfield)){
		    	    		ryh=hrfield;
		    	    	}
		        	}
	    		}
    		}
			for(int i=0;i<selects.length;i++)
			{
				String[] a_selects=selects[i].split("/");
				d.setTimeInMillis(Long.parseLong(a_selects[2]));
				whl.append(" or (A0100='"+a_selects[0]+"' and upper(nbase)='"+a_selects[1].toUpperCase()+"' and "+Sql_switcher.year("A00Z0")+"="+d.get(Calendar.YEAR)+"  and "+Sql_switcher.month("A00Z0")+"="+(d.get(Calendar.MONTH)+1)+" and A00z1="+a_selects[3]+" )");
			}
			rowSet=dao.search("select  "+dj+","+ryh+" from "+this.gz_tablename+" where ( "+whl.substring(3)+" ) and (sp_flag='01' or sp_flag='07')");
			StringBuffer disallow = new StringBuffer("");
			HashMap map = new HashMap();
			while(rowSet.next())
			{
				String dj_d=rowSet.getString(dj);
				String ryh_d=rowSet.getString(ryh);
				if(map.get(dj_d.toUpperCase())!=null)
				{
					StringBuffer t_b = (StringBuffer)map.get(dj_d.toUpperCase());
					t_b.append(","+ryh_d);
					map.put(dj_d.toUpperCase(),t_b);
				}else{
					StringBuffer t_b=new StringBuffer();
					t_b.append(ryh_d);
					map.put(dj_d.toUpperCase(),t_b);
				}
			}
			Set keySet = map.keySet();
			for(Iterator it=keySet.iterator();it.hasNext();){
				String key=(String)it.next();
				String value=((StringBuffer)map.get(key)).toString();
				disallow.append(";"+key.trim()+"("+value+")");
			}
			if(disallow.toString().length()>0){
				 buf.append("<?xml version=\"1.0\" encoding = \"GB2312\" ?>");
	    		 buf.append("<msg>");
	    		 buf.append("<gzperiod>");
	    		 buf.append(d.get(Calendar.YEAR)+""+((d.get(Calendar.MONTH)+1)>9?(((d.get(Calendar.MONTH)+1))+""):("0"+(d.get(Calendar.MONTH)+1)))+""+(d.get(Calendar.DAY_OF_MONTH)>9?((""+d.get(Calendar.DAY_OF_MONTH))):("0"+d.get(Calendar.DAY_OF_MONTH))));
	    		 buf.append("</gzperiod>");
	    		 buf.append("<appresult>0</appresult>");
	    		 buf.append("<disallow>");
	    		 buf.append(disallow.toString().substring(1));
	    		 buf.append("</disallow>");
	    		 buf.append("<disallowdesc>"+content+"</disallowdesc>");
	    		 buf.append("</msg>");
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rowSet!=null)
					rowSet.close();
					
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return buf.toString();
	}
	/**
	 * 华宇工程接口，批准时，发送消息
	 * @param selectGzRecords
	 * @param bosdate
	 * @param count
	 * @param opt
	 * @param where
	 * @return
	 */
	public String getSpSendXML(String selectGzRecords,String bosdate,String count,String opt,String where){
		StringBuffer buf = new StringBuffer("");
		RowSet rowSet= null;
		try{
			SalaryCtrlParamBo ctrlparam=new SalaryCtrlParamBo(this.conn,this.salaryid);
			/**共享工资类别的归属单位和归属部门，如果设置了归属单位和归属部门，按归属部门走，否则，设置了哪个就按哪个走*/
			String deptid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid");
			String orgid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD, "orgid");
			HashMap deptMap = new HashMap();
			String tempStr="";
			if(orgid!=null&&orgid.trim().length()>0&&deptid!=null&&deptid.trim().length()>0)
				tempStr=deptid;
			else if(deptid!=null&&deptid.trim().length()>0)
				tempStr=deptid;
			else if(orgid!=null&&orgid.trim().length()>0)
				tempStr=orgid;
			if("".equals(tempStr))
				throw GeneralExceptionHandler.Handle(new Exception("请为工资类别定义归属单位或归属部门!"));
			String[] selects=selectGzRecords.split("#");
			ContentDAO dao=new ContentDAO(this.conn);
			String[] temps=bosdate.split("\\.");
			StringBuffer sql=new StringBuffer("select  distinct "+tempStr+" from salaryhistory ");
						 sql.append(" where  "+Sql_switcher.year("A00Z2")+"="+temps[0]+" and Salaryid="+this.salaryid);
						 sql.append(" and "+Sql_switcher.month("A00Z2")+"="+Integer.parseInt(temps[1])+"  and "+Sql_switcher.day("A00Z2")+"="+Integer.parseInt(temps[2]));
						 sql.append(" and A00Z3="+count);
			int num=selects.length/15+1;
			StringBuffer whl=new StringBuffer("");
			Calendar d=Calendar.getInstance();
			if("confirmAll".equals(opt))
			{
				sql.append("  and sp_flag='03' ");
				if(where.length()>0)
					sql.append(where);
				rowSet=dao.search(sql.toString());
				while(rowSet.next())
				{
					deptMap.put(rowSet.getString(tempStr),rowSet.getString(tempStr));
				}
			}
			else
			{
				for(int n=0;n<num;n++)
				{
					whl.setLength(0);
					int a=0;
					for(int i=n*15;i<selects.length;i++)
					{
						a++;
						String[] a_selects=selects[i].split("/");
						d.setTimeInMillis(Long.parseLong(a_selects[2]));
						whl.append(" or (A0100='"+a_selects[0]+"' and upper(nbase)='"+a_selects[1].toUpperCase()+"' and "+Sql_switcher.year("A00Z0")+"="+d.get(Calendar.YEAR)+"  and "+Sql_switcher.month("A00Z0")+"="+(d.get(Calendar.MONTH)+1)+" and A00z1="+a_selects[3]+" )");
						if(a>=15)
							break;
					}
					if(whl.length()>0)
					{
						rowSet=dao.search(sql.toString()+" and ( "+whl.substring(3)+" )");
						while(rowSet.next())
						{
							deptMap.put(rowSet.getString(tempStr),rowSet.getString(tempStr));
						}
						rowSet.close();
					}
				}
			}
			if(deptMap.size()>0){
				StringBuffer allow = new StringBuffer("");
				Set keySet =deptMap.keySet();
				String clientName = SystemConfig.getPropertyValue("clientName");
				if(clientName==null||clientName.trim().length()==0)
					throw GeneralExceptionHandler.Handle(new Exception("系统没有设置clientName，请在system.properties文件中设置！"));
				ResourceBundle    myResource = ResourceBundle.getBundle(clientName);
		        String salaryid=myResource.getString("S"+this.salaryid);  //绩效薪资类别ID
		        if(salaryid==null||salaryid.trim().length()==0)
		        	throw GeneralExceptionHandler.Handle(new Exception(clientName+".properties文件中没有设置绩效薪资类别！"));
		        String jobId=myResource.getString("jobId");
		        if(jobId==null||jobId.trim().length()==0)
		        	throw GeneralExceptionHandler.Handle(new Exception(clientName+".properties文件中没有设置数据导入映射关系的作业类标识！"));
		        ConstantXml constantXml = new ConstantXml(conn, "IMPORTINFO","params");
        		List listEl = constantXml.getAllChildren("/params");
        		String dj="";
	        	for (int i = 0; i < listEl.size(); i++) {
        			Element els = (Element) listEl.get(i);
	         		String id = els.getAttributeValue("id");
	    	    	Map connMap = new HashMap();
		    	    // 作业类
		        	String jobClass = constantXml.getTextValue("/params/param[@id='" + id+ "']/jobclass");
		        	// 是否启用
		        	String enable = constantXml.getTextValue("/params/param[@id='"+ id + "']/enable");
		        	if (!"1".equals(enable) || !jobId.equalsIgnoreCase(jobClass)) {
		         		continue;
		        	}
		        	// 名称
		        	String name = constantXml.getTextValue("/params/param[@id='"+ id + "']/name");
	    	    	// 人员库
	    	    	String nbase = constantXml.getTextValue("/params/param[@id='"+ id + "']/nbase");
		        	// 数据库类型
		        	String dbType = constantXml.getTextValue("/params/param[@id='"+ id + "']/dbtype");
		        	connMap.put("dbType", dbType);
		        	// 指标关联
		        	List list = constantXml.getAllChildren("/params/param[@id='"+ id + "']/mappings");
		        	for (int j = 0; j < list.size(); j++) {
		    	    	Element el = (Element) list.get(j);
			        	if ("fieldref".equals(el.getName())) {
			    	    	String hrfield = el.getAttributeValue("hrfield");
			    	    	String extfield = el.getAttributeValue("extfield");
			    	    	if("pk_sjxgzzhd_h".equalsIgnoreCase(extfield))
			    	    	{
			    	    		dj=hrfield;
			    	    		break;
			    	    	}
			        	}
		    		}
	    		}
		    	 for(Iterator it=keySet.iterator();it.hasNext();)
		    	 {
		    		String key=(String)it.next();
		    		StringBuffer sql_str=new StringBuffer("select  a0100 from salaryhistory ");
		    		sql_str.append(" where  "+Sql_switcher.year("A00Z2")+"="+temps[0]+" and Salaryid="+this.salaryid);
		    		sql_str.append(" and "+Sql_switcher.month("A00Z2")+"="+Integer.parseInt(temps[1])+"  and "+Sql_switcher.day("A00Z2")+"="+Integer.parseInt(temps[2]));
		    		sql_str.append(" and A00Z3="+count+" and UPPER("+tempStr+")='"+key.toUpperCase()+"'");
		    		sql_str.append(" and sp_flag<>'03'");
		    		rowSet = dao.search(sql_str.toString());
		    		if(rowSet.next()){
		    			
		    		}else{
		    			sql_str.setLength(0);
		    			sql_str.append("select distinct "+dj+" from salaryhistory");
		    			sql_str.append(" where  "+Sql_switcher.year("A00Z2")+"="+temps[0]+" and Salaryid="+this.salaryid);
			    		sql_str.append(" and "+Sql_switcher.month("A00Z2")+"="+Integer.parseInt(temps[1])+"  and "+Sql_switcher.day("A00Z2")+"="+Integer.parseInt(temps[2]));
			    		sql_str.append(" and A00Z3="+count+" and UPPER("+tempStr+")='"+key.toUpperCase()+"'");
			    		rowSet = dao.search(sql_str.toString());
			    		while(rowSet.next()){
			    			allow.append(";");
			    			allow.append(rowSet.getString(dj).trim());
			    		}
		    		}
		    	}
		    	 if(allow.toString().length()>0){
		    		 buf.append("<?xml version=\"1.0\" encoding = \"GB2312\" ?>");
		    		 buf.append("<msg>");
		    		 buf.append("<gzperiod>");
		    		 buf.append(temps[0]+temps[1]+temps[2]);
		    		 buf.append("</gzperiod>");
		    		 buf.append("<appresult>1</appresult>");
		    		 buf.append("<allow>");
		    		 buf.append(allow.toString().substring(1));
		    		 buf.append("</allow>");
		    		 buf.append("</msg>");
		    	 }
	    	}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rowSet!=null)
					rowSet.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return buf.toString();
	}
	/**
	 * 根据条件取得工资历史数据表中审批意见 
	 * opt confirmAll
	 * @return
	 */
	public HashMap getCurrentSalaryhistoryData(String selectGzRecords,String bosdate,String count,String opt,String where)throws GeneralException
	{
		HashMap appprocessMap=new HashMap();
		try
		{
			String[] selects=selectGzRecords.split("#");
			ContentDAO dao=new ContentDAO(this.conn);
			String[] temps=bosdate.split("\\.");
			StringBuffer sql=new StringBuffer("select  a0100,nbase,appprocess,appuser,curr_user,a00z0,a00z1 from salaryhistory ");
						 sql.append(" where  "+Sql_switcher.year("A00Z2")+"="+temps[0]+" and Salaryid="+this.salaryid);
						 sql.append(" and "+Sql_switcher.month("A00Z2")+"="+Integer.parseInt(temps[1])+"  and "+Sql_switcher.day("A00Z2")+"="+Integer.parseInt(temps[2]));
						 sql.append(" and A00Z3="+count);
				
			int num=selects.length/15+1;
			StringBuffer whl=new StringBuffer("");
			SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
			Calendar d=Calendar.getInstance();
			
			if("confirmAll".equals(opt)|| "appealAll".equals(opt)|| "rejectAll".equals(opt))
			{
				if("confirmAll".equals(opt))
				{
					sql.append(" and curr_user='"+this.userview.getUserName()+"' and sp_flag='02' ");
					if(where.length()>0)
						sql.append(where);
				}
				else if("appealAll".equals(opt)|| "rejectAll".equals(opt))
				{
					sql.append(" and curr_user='"+this.userview.getUserName()+"' and ( sp_flag='02' or  sp_flag='07' ) ");
					if(where.length()>0)
						sql.append(where);
				}
					
				RowSet rowSet=dao.search(sql.toString());
				String a0100="";
				String nbase="";
				String a00z0="";
				String a00z1="";
				String appprocess="";
				String appuser="";
				while(rowSet.next())
				{
					a0100=rowSet.getString("a0100");
					nbase=rowSet.getString("nbase");
					a00z0=df.format(rowSet.getDate("a00z0"));
					a00z1=rowSet.getString("a00z1");
					appprocess=Sql_switcher.readMemo(rowSet,"appprocess");
					appuser=rowSet.getString("appuser")!=null?rowSet.getString("appuser"):"";
					appprocessMap.put(a0100.toLowerCase()+"/"+nbase.toLowerCase()+"/"+a00z0+"/"+a00z1,appprocess+"~"+appuser+"~"+rowSet.getString("curr_user"));
				 
				}
				rowSet.close();
			}
			else
			{
			
				for(int n=0;n<num;n++)
				{
					whl.setLength(0);
					int a=0;
					for(int i=n*15;i<selects.length;i++)
					{
						a++;
						String[] a_selects=selects[i].split("/");
						d.setTimeInMillis(Long.parseLong(a_selects[2]));
						whl.append(" or (A0100='"+a_selects[0]+"' and upper(nbase)='"+a_selects[1].toUpperCase()+"' and "+Sql_switcher.year("A00Z0")+"="+d.get(Calendar.YEAR)+"  and "+Sql_switcher.month("A00Z0")+"="+(d.get(Calendar.MONTH)+1)+" and A00z1="+a_selects[3]+" )");
						if(a>=15)
							break;
					}
					if(whl.length()>0)
					{
						RowSet rowSet=dao.search(sql.toString()+" and ( "+whl.substring(3)+" )");
						String a0100="";
						String nbase="";
						String a00z0="";
						String a00z1="";
						String appprocess="";
						String appuser="";
						while(rowSet.next())
						{
							a0100=rowSet.getString("a0100");
							nbase=rowSet.getString("nbase");
							a00z0=df.format(rowSet.getDate("a00z0"));
							a00z1=rowSet.getString("a00z1");
							appprocess=Sql_switcher.readMemo(rowSet,"appprocess");
							appuser=rowSet.getString("appuser")!=null?rowSet.getString("appuser"):"";
							appprocessMap.put(a0100.toLowerCase()+"/"+nbase.toLowerCase()+"/"+a00z0+"/"+a00z1,appprocess+"~"+appuser+"~"+rowSet.getString("curr_user"));
						 
						}
						rowSet.close();
					}
				
				}
			}
			
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return appprocessMap;
	}
	
	
	/**
	 * 取得 工资历史数据表中 与 工资数据表- 归属日期/ 归属次数 相对应的 审批意见 
	 * @return
	 */
	public HashMap getCurrentSalaryhistoryData()throws GeneralException
	{
		HashMap appprocessMap=new HashMap();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String value=this.userview.getManagePrivCodeValue();
			StringBuffer sql=new StringBuffer("select a.a0100,a.nbase,b.appprocess,a.appuser,a.curr_user,a.a00z0,a.a00z1 from salaryhistory a,"+this.gz_tablename+" b ");
						 sql.append(" where a.a0100=b.a0100 and upper(a.nbase)=upper(b.nbase) and "+Sql_switcher.year("a.A00Z0")+"="+Sql_switcher.year("b.A00Z0")+" and a.Salaryid="+this.salaryid);
						 sql.append(" and "+Sql_switcher.month("a.A00Z0")+"="+Sql_switcher.month("b.A00Z0")+"  and "+Sql_switcher.day("a.A00Z0")+"="+Sql_switcher.day("b.A00Z0")+"  and a.A00Z1=b.A00Z1 ");
						 
			//			 sql.append(" and a.sp_flag='07'");
			RowSet rowSet=dao.search(sql.toString());
			String a0100="";
			String nbase="";
			String appprocess="";
			String appuser="";
			String a00z0="";
			String a00z1="";
			SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
			while(rowSet.next())
			{
				a0100=rowSet.getString("a0100");
				nbase=rowSet.getString("nbase");
				a00z0=df.format(rowSet.getDate("a00z0"));
				a00z1=rowSet.getString("a00z1");
				appprocess=Sql_switcher.readMemo(rowSet,"appprocess");
				appuser=rowSet.getString("appuser")!=null?rowSet.getString("appuser"):"";
				appprocessMap.put(a0100.toLowerCase()+"/"+nbase.toLowerCase()+"/"+a00z0+"/"+a00z1,appprocess+"~"+appuser+"~"+rowSet.getString("curr_user"));
			}
			rowSet.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return appprocessMap;
	}
	
	
	
	/**
	 * 从薪资表中删除薪资停发的人员
	 * @throws GeneralException
	 */
	private void removeA01Z0ManData() throws GeneralException
	{
		DbSecurityImpl dbS = new DbSecurityImpl();
		StringBuffer buf=new StringBuffer();
		String tablename="t#"+this.userview.getUserName()+"_gz_Tf";
		PreparedStatement ps=null;
		RowSet rowSet = null;
		try
		{
			String a01z0Flag=this.getCtrlparam().getValue(SalaryCtrlParamBo.A01Z0,"flag");  // 是否显示停发标识  1：有  
			if(a01z0Flag!=null&& "1".equals(a01z0Flag))
			{
			
				DbWizard dbw=new DbWizard(this.conn);
				Table table=new Table(tablename);
				if(!dbw.isExistTable(tablename, false))
					return;
				int rows=getRows(tablename);
				if(rows==0)
					return;				
				buf.append("delete from ");
				buf.append(this.gz_tablename);
				buf.append(" where exists(select * from ");
				buf.append(tablename);
				buf.append(" where state='1' and upper(");
				buf.append(this.gz_tablename);
				buf.append(".nbase)=upper(");
				buf.append(tablename);
				buf.append(".dbname) and ");
				buf.append(this.gz_tablename);
				buf.append(".A0100=");
				buf.append(tablename);
				buf.append(".A0100)");
				ContentDAO dao=new ContentDAO(this.conn);
				
				
				
				
				//删除税率明细 
				StringBuffer buf1=new StringBuffer("");
				buf1.append("delete from gz_tax_mx where salaryid="+salaryid+" and lower(nbase)=? and a0100=? and "+Sql_switcher.year("a00z0")+"=? and "+Sql_switcher.month("a00z0")+"=? and a00z1=?");
		    	if(manager.length()>0&&!this.userview.getUserName().equalsIgnoreCase(manager))
		    		buf1.append(" and ( lower(userflag)='"+manager.toLowerCase()+"' or userflag is null )");
		    	else
		    		buf1.append(" and ( lower(userflag)='"+this.userview.getUserName().toLowerCase()+"' or userflag is null )");
		    	
		    	String sub_str="select * from "+this.gz_tablename+" where exists(select * from "+tablename+" where state='1' and upper("+this.gz_tablename+".nbase)=upper(";
		    	sub_str+=tablename+".dbname) and "+this.gz_tablename+".A0100="+tablename+".A0100)";
		    	ps=this.conn.prepareStatement(buf1.toString());
		    	rowSet=dao.search(sub_str);
		    	while(rowSet.next())
		    	{
		    		 
		    		Calendar d=Calendar.getInstance();
		    		d.setTime(rowSet.getDate("a00z0")); 
		    		ps.setString(1,rowSet.getString("nbase").toLowerCase());
		    		ps.setString(2,rowSet.getString("a0100"));
		    		ps.setInt(3, d.get(Calendar.YEAR));
		    		ps.setInt(4, (d.get(Calendar.MONTH)+1));
		    		ps.setInt(5,rowSet.getInt("a00z1"));
		    		ps.addBatch();
		    	}
		    	
		    	// 打开Wallet
				dbS.open(conn, buf1.toString());
		    	ps.executeBatch();
				 
				dao.update(buf.toString());
				
				//同步薪资发放数据的映射表
				buf.setLength(0);
				String username=this.userview.getUserName().toLowerCase();
				if(this.manager.length()>0)
					username=this.manager.toLowerCase();	
				buf.append("delete from salary_mapping where salaryid="+this.salaryid+" and lower(userflag)='"+username+"' and  exists(select * from ");
				buf.append(tablename);
				buf.append(" where state='1' and upper(salary_mapping.nbase)=upper(");
				buf.append(tablename);
				buf.append(".dbname) and salary_mapping.A0100=");
				buf.append(tablename);
				buf.append(".A0100)");
				dao.update(buf.toString());
				
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}finally {
			PubFunc.closeResource(rowSet);
			PubFunc.closeResource(ps);
			try {
				// 关闭Wallet
				dbS.close(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 更新信息发生变化的人员信息至薪资表中
	 * @throws GeneralException
	 */
	private void updateChgInfoManData()throws GeneralException
	{
		String tablename="t#"+this.userview.getUserName()+"_gz_bd";
		int rows=getRows(tablename);
		if(rows==0)
			return;		
		/**更新串*/
		StringBuffer strupdate=new StringBuffer();
		strupdate.append(this.gz_tablename);
		strupdate.append(".B0110=");
		strupdate.append(tablename);
		strupdate.append(".B0110`");
		strupdate.append(this.gz_tablename);
		strupdate.append(".E0122=");
		strupdate.append(tablename);
		strupdate.append(".E0122`");
		strupdate.append(this.gz_tablename);
		strupdate.append(".A0101=");
		strupdate.append(tablename);
		strupdate.append(".A0101 ");
		
		StringBuffer strwhere=new StringBuffer();
		strwhere.append(" exists(select * from ");
		strwhere.append(tablename);
		strwhere.append(" where ");
		strwhere.append(this.gz_tablename);
		strwhere.append(".A0100=");
		strwhere.append(tablename);
		strwhere.append(".A0100");
		strwhere.append(" and upper(");
		strwhere.append(this.gz_tablename);
		strwhere.append(".nbase)=upper(");
		strwhere.append(tablename);
		strwhere.append(".dbname)");
		strwhere.append(" and state='1')");
		
		try
		{
			DbWizard dbw=new DbWizard(this.conn);
			/**更新薪资表*/
			dbw.updateRecord(gz_tablename,tablename,gz_tablename+".A0100="+tablename+".A0100 and UPPER("+this.gz_tablename+".nbase)=UPPER("+tablename+".dbname)", strupdate.toString(), strwhere.toString(), strwhere.toString());
			/**更新薪资历史数据表,A0101*/
			strupdate.setLength(0);
			strupdate.append("salaryhistory");
			strupdate.append(".B0110=");
			strupdate.append(tablename);
			strupdate.append(".B0110`");
			strupdate.append("salaryhistory");
			strupdate.append(".E0122=");
			strupdate.append(tablename);
			strupdate.append(".E0122`");
			strupdate.append("salaryhistory");
			strupdate.append(".A0101=");
			strupdate.append(tablename);
			strupdate.append(".A0101 ");
			
			strwhere.setLength(0);
			strwhere.append(" exists(select * from ");
			strwhere.append(tablename);
			strwhere.append(" where ");
			strwhere.append("salaryhistory");
			strwhere.append(".A0100=");
			strwhere.append(tablename);
			strwhere.append(".A0100");
			strwhere.append(" and ");
			strwhere.append("upper(salaryhistory");
			strwhere.append(".nbase)=upper(");
			strwhere.append(tablename);
			strwhere.append(".dbname)");
			strwhere.append(" and state='1')");	
			
			String username=this.userview.getUserName().toLowerCase();
			if(this.manager!=null&&this.manager.trim().length()>0)
				username=this.manager;
			String table_name=username+"_salary_"+this.salaryid;
			
			
			
			strwhere.append("  and salaryhistory.salaryid="+this.salaryid+"  and exists (select null from ");
			strwhere.append(table_name);
			strwhere.append(" where ");
			strwhere.append("salaryhistory");
			strwhere.append(".A0100=");
			strwhere.append(table_name);
			strwhere.append(".A0100");
			strwhere.append(" and ");
			strwhere.append("upper(salaryhistory");
			strwhere.append(".nbase)=upper(");
			strwhere.append(table_name);
			strwhere.append(".nbase)");
			strwhere.append(" and  salaryhistory.a00z0="+table_name+".a00z0  and  salaryhistory.a00z1="+table_name+".a00z1   )");	
			dbw.updateRecord("salaryhistory",tablename,"salaryhistory"+".A0100="+tablename+".A0100 and upper(salaryhistory.nbase)=upper("+tablename+".dbname)", strupdate.toString(), strwhere.toString(), strwhere.toString());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	/**
	 * 求当前表的记录数
	 * @param tablename
	 * @return
	 */
	private  int getRows(String tablename)
	{
		int maxrows=0;
		try
		{
			StringBuffer buf=new StringBuffer();
			buf.append("select count(*) as nrow from ");
			buf.append(tablename);
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rset=dao.search(buf.toString());
			if(rset.next())
				maxrows=rset.getInt("nrow");
			rset.close();
		}
		catch(Exception  ex)
		{
			ex.printStackTrace();
		}
		return maxrows;
	}
	
	
	private String importMenSql_where=""; //导入的新增人员条件
	private boolean  isImportMen=false;  //是不是人员引入模块调用的
	
	
	
	
	
	
	/**
	 * 获得共享薪资类别非管理权限控制代码
	 * @param pre
	 */
	private String getPrivStr(String pre)
	{
		String whl2="";
		if(this.manager.length()>0&&!this.userview.getUserName().equalsIgnoreCase(this.manager)) 
		{		
	 			String _whereIN=""; 
				if("1".equals(this.controlByUnitcode))
				{
				 
					String whl_str=getWhlByUnits();
					if(whl_str.length()>0)
						whl2+=whl_str;
				} 
				else
				{
					_whereIN=InfoUtils.getWhereINSql(this.userview,pre);
					_whereIN="select a0100 "+_whereIN;	
					whl2+=" and "+this.gz_tablename+".a0100 in ( "+_whereIN+" )";
				}
		}	
		return whl2;
	}
	
	
	
	
	
	/**
	 * 提成工资重新导入（无则新增；有则修改；多则删除）
	 * @param itemList
	 * @throws GeneralException
	 */
	private void reImportRoyaltyData(ArrayList aitemList)throws GeneralException
	{ 
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			
			HashMap map=getMaxYearMonthCount();
			currym=(String)map.get("ym");
			currcount=(String)map.get("count");
			String[] temps=currym.split("-");
			String year=temps[0];
			String month=temps[1];
			 
		 
			String royalty_setid=ctrlparam.getValue(SalaryCtrlParamBo.ROYALTIES,"setid");
			String royalty_date=ctrlparam.getValue(SalaryCtrlParamBo.ROYALTIES,"date");
			String royalty_period=ctrlparam.getValue(SalaryCtrlParamBo.ROYALTIES,"period");
		 	String royalty_relation_fields=ctrlparam.getValue(SalaryCtrlParamBo.ROYALTIES,"relation_fields");
		 	String strExpression=this.ctrlparam.getValue(SalaryCtrlParamBo.ROYALTIES,"strExpression");
		 	String whl="";
		 	String whl2="";
		 	String strpre=null; 
		 	String strset=null;
			String strc=null;
		 	if(strExpression!=null&&strExpression.trim().length()>0)
			{ 
				 
				YksjParser yp = new YksjParser( this.userview ,DataDictionary.getFieldList(royalty_setid, 1),
						YksjParser.forNormal, YksjParser.LOGIC,YksjParser.forPerson , "Ht", "");
				yp.run_where(strExpression);
				whl=yp.getSQL();
				  
			}
		 	 
		 	
		 	/**需要审批,仅导入起草和驳回记录*/ 
			if(isApprove())
			{ 
				whl2+=" and "+ this.gz_tablename+"."+"sp_flag in('01','07')";				
			}	 
		 	  
		 	
		 	String dbpres=this.templatevo.getString("cbase");
			/**应用库前缀*/
			String[] dbarr=StringUtils.split(dbpres, ",");
		 	
		 	
			ArrayList relationfieldList=new ArrayList();
			String relation_str="";
			if(royalty_relation_fields.length()>0)
			{
				temps=royalty_relation_fields.toLowerCase().split(",");
				for(int n=0;n<temps.length;n++)
				{
					if(temps[n].trim().length()>0&&DataDictionary.getFieldItem(temps[n].trim())!=null)
					{ 
						relationfieldList.add(DataDictionary.getFieldItem(temps[n].toLowerCase()));
					}
				}
			} 
			//删除多于人员
			FieldItem item=null;
			StringBuffer sql=new StringBuffer("");
			for(int i=0;i<dbarr.length;i++)
			{
				sql.setLength(0);
				strpre=dbarr[i];
				strc=strpre+royalty_setid;
				sql.append("delete from "+this.gz_tablename+" where lower(nbase)='"+strpre.toLowerCase()+"' and  not exists(select null from "+strc+" where ");
				sql.append(" "+this.gz_tablename+".a0100="+strc+".a0100 ");
				for(int j=0;j<relationfieldList.size();j++)
				{
					item=(FieldItem)relationfieldList.get(j);
					sql.append(" and  "+this.gz_tablename+"."+item.getItemid()+"="+strc+"."+item.getItemid());
				}
				
				int[] months=getMonth(month,royalty_period);
				if("1".equals(royalty_period)) //周期 (1|2|3|4)=( 月|季|半年|年)
					sql.append(" and  "+Sql_switcher.year(royalty_date)+"="+year+" and "+Sql_switcher.month(royalty_date)+"="+month);
				if("2".equals(royalty_period)|| "3".equals(royalty_period)) //周期 (1|2|3|4)=( 月|季|半年|年)
				{
					sql.append(" and  "+Sql_switcher.year(royalty_date)+"="+year+" and "+Sql_switcher.month(royalty_date)+" in ( 100");
					for(int n=0;n<months.length;n++)
					{
						sql.append(","+months[n]);
					}
					sql.append(" )");
				}
				if("4".equals(royalty_period)) //周期 (1|2|3|4)=( 月|季|半年|年)
					sql.append(" and  "+Sql_switcher.year(royalty_date)+"="+year);
				
				if(whl!=null&&whl.length()>0)
					sql.append(" and ( "+whl+" )"); 
				sql.append("  ) "+whl2+getPrivStr(strpre)); 
				dao.update(sql.toString()); 
			}
		 	
		 	
			//新增人员
			StringBuffer buf=new StringBuffer("");
			String pay_flag=ctrlparam.getValue(SalaryCtrlParamBo.PAY_FLAG);
			HashMap ycmap=getYearMonthCount2();   //dengcan
			/**年月和次数*/
			String ym=(String)ycmap.get("ym"); 
			String szC=(String)ycmap.get("count");
			szC=szC!=null&&szC.trim().length()>0?szC:"1";
			boolean  payFlag_isExist=false;
			for(int e=0;e<gzitemlist.size();e++)
			{
				GzItemVo itemvo=(GzItemVo)gzitemlist.get(e);
				if(itemvo.getInitflag()==0)
					continue;
				if(pay_flag!=null&&pay_flag.equalsIgnoreCase(itemvo.getFldname()))
					payFlag_isExist=true;
			}
			
			
			if(!(this.manager.length()>0&&!this.userview.getUserName().equalsIgnoreCase(this.manager))) 
			{
				for(int i=0;i<dbarr.length;i++)
				{
					strpre=dbarr[i];
					buf.setLength(0);
					String strlst="";
					for(int j=0;j<setlist.size();j++)
					{
						strset=(String)setlist.get(j);
						if("A01".equalsIgnoreCase(strset))
						{
							strlst+=","+getInsFieldSQL(strset,pay_flag); 
						}
						if(strset.equalsIgnoreCase(royalty_setid))
						{
							strlst+=","+getInsFieldSQL(strset,pay_flag);
						} 
					}
					if(strlst.length()>0)
						strlst=strlst.substring(1);
					 
					buf.append("insert into ");
					buf.append(this.gz_tablename);
					buf.append(" (userflag,nbase,A00Z2,A00Z3,A00Z0,A00Z1,sp_flag,");
					if(this.manager.length()>0)
						buf.append("sp_flag2,");
					if(payFlag_isExist&&pay_flag.length()!=0)
					{
						
						buf.append(pay_flag);
						buf.append(",");
					}
					buf.append(strlst);
					buf.append(") select '");
					buf.append(this.userview.getUserName());
					buf.append("','" );
					buf.append(strpre.toUpperCase());
					buf.append("',");
					buf.append(Sql_switcher.dateValue(ym));
					buf.append(",");
					buf.append(szC);
					buf.append(",");
					buf.append(Sql_switcher.dateValue(ym));
					buf.append(",");
					buf.append(strpre+royalty_setid+".i9999");
					buf.append(",'");
					buf.append(SP_STATE_DRAFT);
					buf.append("',");
					if(this.manager.length()>0)
						buf.append("'01',");
					if(payFlag_isExist&&pay_flag.length()!=0)
					{
						buf.append("'");
						buf.append(PAY_FLAG_NORMAL);
						buf.append("',");
					}
					strlst=strlst.replaceAll("A0100", strpre+"A01.A0100");
					buf.append(strlst);
					
					strc=strpre+royalty_setid;
					
					buf.append(" from ");
					buf.append(strc+","+strpre+"A01");
					buf.append(" where "+strc+".a0100="+strpre+"A01.a0100 ");
					int[] months=getMonth(month,royalty_period);
					if("1".equals(royalty_period)) //周期 (1|2|3|4)=( 月|季|半年|年)
						buf.append(" and  "+Sql_switcher.year(royalty_date)+"="+year+" and "+Sql_switcher.month(royalty_date)+"="+month);
					if("2".equals(royalty_period)|| "3".equals(royalty_period)) //周期 (1|2|3|4)=( 月|季|半年|年)
					{
						buf.append(" and  "+Sql_switcher.year(royalty_date)+"="+year+" and "+Sql_switcher.month(royalty_date)+" in ( 100");
						for(int n=0;n<months.length;n++)
						{
							buf.append(","+months[n]);
						}
						buf.append(" )");
					}
					if("4".equals(royalty_period)) //周期 (1|2|3|4)=( 月|季|半年|年)
						buf.append(" and  "+Sql_switcher.year(royalty_date)+"="+year);
					
					if(whl!=null&&whl.length()>0)
						buf.append(" and ( "+whl+" )");
					
					
					buf.append("and  not exists ( select null from "+this.gz_tablename+" where lower(nbase)='"+strpre.toLowerCase()+"' ");
					buf.append(" and "+this.gz_tablename+".a0100="+strc+".a0100 ");
					for(int j=0;j<relationfieldList.size();j++)
					{
								item=(FieldItem)relationfieldList.get(j);
								buf.append(" and  "+this.gz_tablename+"."+item.getItemid()+"="+strc+"."+item.getItemid());
					}
					buf.append(" ) "); 
					dao.update(buf.toString());
				}
			}
			//修改
		//  tyytt
			String read_field=this.ctrlparam.getValue(SalaryCtrlParamBo.READ_FIELD);
	        if(read_field==null|| "".equals(read_field))
	        	read_field="0";
			for(int i=0;i<dbarr.length;i++)
			{
				
				strpre=dbarr[i];
				for(int j=0;j<aitemList.size();j++)
				{
					sql.setLength(0);
					String _itemid=((String)aitemList.get(j)).toLowerCase().trim();
					item=DataDictionary.getFieldItem(_itemid);
					if(item!=null)
					{
						if("0".equals(read_field))
						{
					    	if(!"2".equals(userview.analyseFieldPriv(_itemid)))
						    	continue;
						}else
						{
							if(!"2".equals(userview.analyseFieldPriv(_itemid))&&!"1".equals(userview.analyseFieldPriv(_itemid)))
						    	continue;
						}
						
						if("A01".equalsIgnoreCase(item.getFieldsetid())||item.getFieldsetid().equalsIgnoreCase(royalty_setid))
						{
							sql.append("update "+this.gz_tablename+" set "+_itemid+"=(select "+_itemid+" from "+strpre+item.getFieldsetid()+" where  ");
							if("A01".equalsIgnoreCase(item.getFieldsetid()))
							{
							
								sql.append(" "+this.gz_tablename+".a0100="+strpre+"A01.a0100 ");
								
							}
							else
							{
								
								sql.append(" "+this.gz_tablename+".a0100="+strpre+item.getFieldsetid()+".a0100  and "+this.gz_tablename+".nbase='"+strpre.toUpperCase()+"'");//通过a0100和i9999来筛选  zhaoxg add 2013-10-17 and "+this.gz_tablename+".a00z1="+strpre+item.getFieldsetid()+".i9999
								for(int e=0;e<relationfieldList.size();e++)
								{
											item=(FieldItem)relationfieldList.get(e);
											sql.append(" and  "+this.gz_tablename+"."+item.getItemid()+"="+strpre+royalty_setid+"."+item.getItemid());
								}
								
							}
							sql.append(" ) where lower(nbase)='"+strpre.toLowerCase()+"' "+whl2+" and exists (select null from "+strpre+item.getFieldsetid()+" where  ");
							sql.append(" "+this.gz_tablename+".a0100="+strpre+item.getFieldsetid()+".a0100 ");
							if(item.getFieldsetid().equalsIgnoreCase(royalty_setid))
							{
								for(int e=0;e<relationfieldList.size();e++)
								{
											item=(FieldItem)relationfieldList.get(e);
											sql.append(" and  "+this.gz_tablename+"."+item.getItemid()+"="+strpre+royalty_setid+"."+item.getItemid());
								}
							}	 
							sql.append("  ) ");
 						    dao.update(sql.toString());
						
						}
					}
					else
						continue; 
				}
			}
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
	}
	
	
	
	
	
	/**
	 * 导入新增人员的数据(提成工资)
	 * @throws GeneralException
	 */
	private void importAddManData_royalty(String year,String month)throws GeneralException
	{ 
		try
		{
			DbWizard dbw=new DbWizard(this.conn);
			String royalty_setid=ctrlparam.getValue(SalaryCtrlParamBo.ROYALTIES,"setid");
			String royalty_date=ctrlparam.getValue(SalaryCtrlParamBo.ROYALTIES,"date");
			String royalty_period=ctrlparam.getValue(SalaryCtrlParamBo.ROYALTIES,"period");
			String royalty_relation_fields=ctrlparam.getValue(SalaryCtrlParamBo.ROYALTIES,"relation_fields");
			String strExpression=this.ctrlparam.getValue(SalaryCtrlParamBo.ROYALTIES,"strExpression");
			String strset=null;
			String strc=null;
			String strpre=null;
			String dbpres=this.templatevo.getString("cbase");
			/**应用库前缀*/
			String[] dbarr=StringUtils.split(dbpres, ",");
			StringBuffer buf=new StringBuffer("");
			String pay_flag=ctrlparam.getValue(SalaryCtrlParamBo.PAY_FLAG);
			HashMap ycmap=getYearMonthCount2();   //dengcan
			/**年月和次数*/
			String ym=(String)ycmap.get("ym"); 
			String szC=(String)ycmap.get("count");
			szC=szC!=null&&szC.trim().length()>0?szC:"1";
			boolean  payFlag_isExist=false;
			for(int e=0;e<gzitemlist.size();e++)
			{
				GzItemVo itemvo=(GzItemVo)gzitemlist.get(e);
				if(itemvo.getInitflag()==0)
					continue;
				if(pay_flag!=null&&pay_flag.equalsIgnoreCase(itemvo.getFldname()))
					payFlag_isExist=true;
			}
			
			
			
			for(int i=0;i<dbarr.length;i++)
			{
				strpre=dbarr[i];
				buf.setLength(0);
				String strlst="";
				for(int j=0;j<setlist.size();j++)
				{
					strset=(String)setlist.get(j);
					if("A01".equalsIgnoreCase(strset))
					{
						strlst+=","+getInsFieldSQL(strset,pay_flag); 
					}
					if(strset.equalsIgnoreCase(royalty_setid))
					{
						strlst+=","+getInsFieldSQL(strset,pay_flag);
					} 
				}
				if(strlst.length()>0)
					strlst=strlst.substring(1);
				 
				buf.append("insert into ");
				buf.append(this.gz_tablename);
				buf.append(" (userflag,nbase,A00Z2,A00Z3,A00Z0,A00Z1,sp_flag,");
				if(this.manager.length()>0)
					buf.append("sp_flag2,");
				if(payFlag_isExist&&pay_flag.length()!=0)
				{
					
					buf.append(pay_flag);
					buf.append(",");
				}
				buf.append(strlst);
				buf.append(") select '");
				buf.append(this.userview.getUserName());
				buf.append("','" );
				buf.append(strpre.toUpperCase());
				buf.append("',");
				buf.append(Sql_switcher.dateValue(ym));
				buf.append(",");
				buf.append(szC);
				buf.append(",");
				buf.append(Sql_switcher.dateValue(ym));
				buf.append(",");
				buf.append(strpre+royalty_setid+".i9999");
				buf.append(",'");
				buf.append(SP_STATE_DRAFT);
				buf.append("',");
				if(this.manager.length()>0)
					buf.append("'01',");
				if(payFlag_isExist&&pay_flag.length()!=0)
				{
					buf.append("'");
					buf.append(PAY_FLAG_NORMAL);
					buf.append("',");
				}
				strlst=strlst.replaceAll("A0100", strpre+"A01.A0100");
				buf.append(strlst);
				
				strc=strpre+royalty_setid;
				
				buf.append(" from ");
				buf.append(strc+","+strpre+"A01");
				buf.append(" where "+strc+".a0100="+strpre+"A01.a0100 ");
				int[] months=getMonth(month,royalty_period);
				if("1".equals(royalty_period)) //周期 (1|2|3|4)=( 月|季|半年|年)
					buf.append(" and  "+Sql_switcher.year(royalty_date)+"="+year+" and "+Sql_switcher.month(royalty_date)+"="+month);
				if("2".equals(royalty_period)|| "3".equals(royalty_period)) //周期 (1|2|3|4)=( 月|季|半年|年)
				{
					buf.append(" and  "+Sql_switcher.year(royalty_date)+"="+year+" and "+Sql_switcher.month(royalty_date)+" in ( 100");
					for(int n=0;n<months.length;n++)
					{
						buf.append(","+months[n]);
					}
					buf.append(" )");
				}
				if("4".equals(royalty_period)) //周期 (1|2|3|4)=( 月|季|半年|年)
					buf.append(" and  "+Sql_switcher.year(royalty_date)+"="+year);
				
				
				if(strExpression!=null&&strExpression.trim().length()>0)
				{ 
					try
					{
					 
						YksjParser yp = new YksjParser( this.userview ,DataDictionary.getFieldList(royalty_setid, 1),
								YksjParser.forNormal, YksjParser.LOGIC,YksjParser.forPerson , "Ht", "");
						yp.run_where(strExpression);
						String whl=yp.getSQL();
						buf.append(" and ( "+whl+" )");
					}
					catch(Exception ee)
					{
						throw new Exception("薪资类别数据范围定义错误!");
					}
				}
				
				dbw.execute(buf.toString());
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	private int[] getMonth(String month,String royalty_period)
	{
		int[] months=null;
		int a_month=Integer.parseInt(month);
		if("2".equals(royalty_period)) //季
		{
			months=new int[3];
			if(a_month<=3)
			{
				months[0]=1;months[0]=2;months[0]=3;
			}
			else if(a_month>3&&a_month<=6)
			{
				months[0]=4;months[0]=5;months[0]=6;
			}
			else if(a_month>6&&a_month<=9)
			{
				months[0]=7;months[0]=8;months[0]=9;
			}
			else if(a_month>9&&a_month<=12)
			{
				months[0]=10;months[0]=11;months[0]=12;
			}
		}
		else if("3".equals(royalty_period)) //周期 (1|2|3|4)=( 月|季|半年|年)
		{
			months=new int[6];
			if(a_month<=6)
			{
				months[0]=1;months[0]=2;months[0]=3;months[0]=4;months[0]=5;months[0]=6;
			}
			else 
			{
				months[0]=7;months[0]=8;months[0]=9;months[0]=10;months[0]=11;months[0]=12;
			}
		}
		return months;
	}
	
	
	/**
	 * 导入新增人员的数据
	 * @throws GeneralException
	 */
	private void importAddManData(boolean flag)throws GeneralException
	{
		
		String tablename="t#"+this.userview.getUserName()+"_gz_Ins";
		try
		{
			
			
			int rows=getRows(tablename);
			if(rows==0)
				return;
			DbWizard dbw=new DbWizard(this.conn);
			/**取得当前处理到的年月及次数*/
			//HashMap ycmap=getYearMonthCount("01");  //getYearMonthCount(gz_tablename);
			HashMap ycmap=getYearMonthCount2();   //dengcan
			/**年月和次数*/
			String ym=(String)ycmap.get("ym"); 
			String szC=(String)ycmap.get("count");
			szC=szC!=null&&szC.trim().length()>0?szC:"1";
			String dbpres=this.templatevo.getString("cbase");
			/**应用库前缀*/
			String[] dbarr=StringUtils.split(dbpres, ",");
			String strset=null;
			String strc=null;
			String strpre=null;
			String pay_flag=ctrlparam.getValue(SalaryCtrlParamBo.PAY_FLAG);
			StringBuffer buf=new StringBuffer();
			StringBuffer strWhere=new StringBuffer();
			for(int i=0;i<dbarr.length;i++)
			{
				strpre=dbarr[i];
				buf.setLength(0);
				for(int j=0;j<setlist.size();j++)
				{
					strset=(String)setlist.get(j);
					/**NBASE,A00Z0,A00Z1,A00Z2,A00Z3的子集代码为A00*/
					if("A00".equalsIgnoreCase(strset))
						continue;
					/**先导入人员主集*/
					strc=strpre+strset;
					if("A01".equalsIgnoreCase(strset))
					{
						
						
						boolean  payFlag_isExist=false;
						for(int e=0;e<gzitemlist.size();e++)
						{
							GzItemVo itemvo=(GzItemVo)gzitemlist.get(e);
							if(itemvo.getInitflag()==0)
								continue;
							if(pay_flag!=null&&pay_flag.equalsIgnoreCase(itemvo.getFldname()))
								payFlag_isExist=true;
						}
						
						
						String strlst=getInsFieldSQL(strset,pay_flag);
						buf.append("insert into ");
						buf.append(this.gz_tablename);
						buf.append(" (userflag,nbase,A00Z2,A00Z3,A00Z0,A00Z1,sp_flag,");
						if(this.manager.length()>0)
							buf.append("sp_flag2,");
						if(payFlag_isExist&&pay_flag.length()!=0)
						{
							
							buf.append(pay_flag);
							buf.append(",");
						}
						buf.append(strlst);
						buf.append(") select '");
						buf.append(this.userview.getUserName());
						buf.append("','" );
						buf.append(strpre.toUpperCase());
						buf.append("',");
						buf.append(Sql_switcher.dateValue(ym));
						buf.append(",");
						buf.append(szC);
						buf.append(",");
						buf.append(Sql_switcher.dateValue(ym));
						buf.append(",");
						buf.append(szC);
						buf.append(",'");
						buf.append(SP_STATE_DRAFT);
						buf.append("',");
						if(this.manager.length()>0)
							buf.append("'01',");
						if(payFlag_isExist&&pay_flag.length()!=0)
						{
							buf.append("'");
							buf.append(PAY_FLAG_NORMAL);
							buf.append("',");
						}
						buf.append(strlst);
						
						buf.append(" from ");
						buf.append(strc);
						buf.append(" where exists (select null from ");
						buf.append(tablename);
						buf.append(" where state='1' and lower(dbname)='");
						buf.append(strpre.toLowerCase());
						buf.append("' and "+tablename+".a0100="+strc+".a0100   )");
						if(this.isImportMen)  //如果是人员引入模块
						{
						//	buf.append(" and a0100 not in (select a0100 from ");
						//	buf.append(this.gz_tablename+" )");
							buf.append(" and  not exists (select null from ");
							buf.append(this.gz_tablename+" where "+this.gz_tablename+".a0100="+strc+".a0100 ");
							buf.append(" and lower("+this.gz_tablename+".nbase)='"+strpre.toLowerCase()+"' ) ");
							
							StringBuffer buf2=new StringBuffer("update "+tablename+" set isFlag='1' where ");
							buf2.append(" exists (select null from ");
							buf2.append(this.gz_tablename+" where "+this.gz_tablename+".a0100="+tablename+".a0100 ");
							buf2.append(" and lower("+this.gz_tablename+".nbase)='"+strpre.toLowerCase()+"' ) and  lower("+tablename+".DBNAME)='"+strpre.toLowerCase()+"'");
							dbw.execute(buf2.toString());  
						}
				// 		System.out.println(this.userview.getUserName()+":"+buf.toString());
						dbw.execute(buf.toString());
				//		System.out.println(this.userview+":end");
						
				//		System.out.println("1."+(System.currentTimeMillis()-a));
						
						if(this.isImportMen)  //如果是人员引入模块调用
						{
							buf.setLength(0);
							buf.append("insert into ");
							buf.append(this.gz_tablename);
							buf.append(" (userflag,nbase,A00Z2,A00Z3,A00Z0,A00Z1,sp_flag,");
							if(this.manager.length()>0)
								buf.append("sp_flag2,");
							if(payFlag_isExist&&pay_flag.length()!=0)
							{
								
								buf.append(pay_flag);
								buf.append(",");
							}
							buf.append(strlst);
							buf.append(") select '");
							buf.append(this.userview.getUserName());
							buf.append("','" );
							buf.append(strpre.toUpperCase());
							buf.append("',");
							buf.append(Sql_switcher.dateValue(ym));
							buf.append(",");
							buf.append(szC);
							buf.append(",");
							buf.append(Sql_switcher.dateValue(ym));
							buf.append(",");
							buf.append("b.count");
							buf.append(",'");
							buf.append(SP_STATE_DRAFT);
							buf.append("',");
							if(this.manager.length()>0)
								buf.append("'01',");
							if(payFlag_isExist&&pay_flag.length()!=0)
							{
								buf.append("'");
								buf.append(PAY_FLAG_NORMAL);
								buf.append("',");
							}
							buf.append(strc+"."+strlst.replaceAll(",", ","+strc+"."));
							
							buf.append(" from ");
							buf.append(strc);
							buf.append(",(select max(a00z1)+1 count ,a0100 from "+this.gz_tablename+" where lower(nbase)='"+strpre.toLowerCase()+"' and a0100 in (select A0100 from ");
							buf.append(tablename);
							buf.append(" where state='1' and lower(dbname)='");
							buf.append(strpre.toLowerCase());
							buf.append("') group by a0100) b");
							buf.append(" where  "+strc+".a0100=b.a0100  and  "+strc+".A0100 in (select A0100 from ");
							buf.append(tablename);
							buf.append(" where state='1' and isFlag='1'  and lower(dbname)='");
							buf.append(strpre.toLowerCase());
							buf.append("')");
							dbw.execute(buf.toString());
						}
						
						/** 如果发现历史表中有当月相同的次数，归属次数就自动加1 */
						if(flag)
						{
							
							DbNameBo.autoAddZ1_ff(this.conn, this.userview,this.gz_tablename,String.valueOf(salaryid),this.manager,false);
							/*
							StringBuffer buf2=new StringBuffer("");
							buf2.append("update "+this.gz_tablename+" set a00z1=(select a00z1+1 from salaryhistory where  "+this.gz_tablename+".a0100=salaryhistory.a0100 ");
							buf2.append(" and lower("+this.gz_tablename+".nbase)=lower(salaryhistory.nbase) and "+this.gz_tablename+".a00z0=salaryhistory.a00z0 and "+this.gz_tablename+".a00z1=salaryhistory.a00z1 and salaryid="+this.salaryid);
							buf2.append(" ) where exists (select null from salaryhistory where  "+this.gz_tablename+".a0100=salaryhistory.a0100 ");
							buf2.append(" and "+this.gz_tablename+".nbase=salaryhistory.nbase and "+this.gz_tablename+".a00z0=salaryhistory.a00z0 and "+this.gz_tablename+".a00z1=salaryhistory.a00z1 and salaryid="+this.salaryid+"  ) ");
							buf2.append(" and exists (select null from ");
							buf2.append(tablename);
							buf2.append(" where "+tablename+".a0100="+this.gz_tablename+".a0100  and state='1' and lower(dbname)='");
							buf2.append(strpre.toLowerCase());
							buf2.append("')");
							dbw.execute(buf2.toString());
							*/
							
						}
						
						
					}//主集处理结束
					else
					{
						/**人员信息集*/
						if(strset.charAt(0)=='A')
						{
					
						    String strupdate=getUpdateFieldSQL(strc, this.gz_tablename, strset);
							
							if(strupdate.length()==0)
								continue;
							String temp1="(select * from "+strc+" a where a.i9999=(select max(b.i9999) from "+strc+" b where a.a0100=b.a0100  ) ) "+strc;
								
							strWhere.setLength(0);
							strWhere.append(" exists (select null from ");
							strWhere.append(tablename);
							strWhere.append(" where "+gz_tablename+".a0100="+tablename+".a0100  and state='1' and lower(dbname)='");
							strWhere.append(strpre.toLowerCase());
							strWhere.append("') and upper(");
							strWhere.append(gz_tablename);
							strWhere.append(".nbase)='");
							strWhere.append(strpre.toUpperCase());
							strWhere.append("'");
							
							if(this.isImportMen)
							{
								strWhere.append(" and exists (select null from ( select max(a00z1) a00z1,nbase,a0100 from "+this.gz_tablename+" group by a0100,nbase ) a where a.a0100="+this.gz_tablename+".a0100 ");
								strWhere.append(" and a.nbase="+this.gz_tablename+".nbase and a.a00z1="+this.gz_tablename+".a00z1 ) ");
							}
							
							
							dbw.updateRecord(gz_tablename,temp1,gz_tablename+".A0100="+strc+".A0100", strupdate, strWhere.toString(),strWhere.toString());
							
						
						}
					}
				}//for j loop end.
				/**计算导入项和累积项*/
				
				
				if(this.isImportMen)
				{
					if(!dbw.isExistField(tablename, "a00z1",false))
					{
						Table table=new Table(tablename);
						Field field=new Field("a00z1","a00z1");
						field.setDatatype(DataType.INT); 		
						table.addField(field);
						dbw.addColumns(table);
						DbSecurityImpl dbS = new DbSecurityImpl();
						dbS.encryptTableName(this.conn, tablename);//创建临时表时需注册表
					}
					String temp_sql="update "+tablename+" set a00z1=(select a00z1 from (select max(a00z1) a00z1,nbase,a0100 from "+this.gz_tablename+" group by a0100,nbase ) a "
					+" where a.a0100="+tablename+".a0100   and lower(a.nbase)=lower("+tablename+".dbname)   ) where  lower(dbname)='"+strpre.toLowerCase()+"' ";
					dbw.execute(temp_sql);
					
					strWhere.setLength(0);
					strWhere.append(" exists (select null from ");
					strWhere.append(tablename);
					strWhere.append(" where "+gz_tablename+".a0100="+tablename+".a0100 and state='1' and lower(dbname)='");
					strWhere.append(strpre.toLowerCase());
				    strWhere.append("' and lower("+tablename+".dbname)=lower("+this.gz_tablename+".nbase)   and "+tablename+".a00z1="+this.gz_tablename+".a00z1  )");
					 
					
				}
				else
				{
					strWhere.setLength(0);
					strWhere.append(" exists (select null from ");
					strWhere.append(tablename);
					strWhere.append(" where "+gz_tablename+".a0100="+tablename+".a0100 and state='1' and lower(dbname)='");
					strWhere.append(strpre.toLowerCase());
					strWhere.append("')");
			 /*
					if(this.isImportMen)
					{
						strWhere.append(" and exists (select null from ( select max(a00z1) a00z1,nbase,a0100 from "+this.gz_tablename+" group by a0100,nbase ) a where a.a0100="+this.gz_tablename+".a0100 ");
						strWhere.append(" and a.nbase="+this.gz_tablename+".nbase and a.a00z1="+this.gz_tablename+".a00z1 ) ");
					} */
				}
				
				importMenSql_where="";
				importMenSql_where+=" select A0100 from ";
				importMenSql_where+=tablename;
				importMenSql_where+=" where state='1' and upper(dbname)='";
				importMenSql_where+=strpre.toUpperCase();
				importMenSql_where+="'";
				
				
				//有记录才进行下一步计算 2008-11-12  dengcan
				ContentDAO dao=new ContentDAO(this.conn);
				RowSet rowSet=dao.search("select count(nbase) from "+this.gz_tablename+" where lower(nbase)='"+strpre.toLowerCase()+"'");
				if(rowSet.next())
				{
					if(rowSet.getInt(1)>0)
						firstComputing(strWhere.toString(), strpre,true,new ArrayList());
				}
				rowSet.close();
			}//for i loop end.
			
			ContentDAO dao=new ContentDAO(this.conn);
			buf.setLength(0);
			buf.append("update "+this.gz_tablename+" set b0110_o=(select a0000 from organization where organization.codeitemid="+this.gz_tablename+".b0110 and organization.codesetid='UN' )");
			buf.append(" where exists (select null from organization where organization.codeitemid="+this.gz_tablename+".b0110 and organization.codesetid='UN' )");
			dao.update(buf.toString());
			buf.setLength(0);
			buf.append("update "+this.gz_tablename+" set e0122_o=(select a0000 from organization where organization.codeitemid="+this.gz_tablename+".e0122 and organization.codesetid='UM' )");
			buf.append(" where exists (select null from organization where organization.codeitemid="+this.gz_tablename+".e0122 and organization.codesetid='UM' )");
			dao.update(buf.toString());
			buf.setLength(0);
			buf.append("update "+this.gz_tablename+" set dbid=(select dbid from dbname where upper(dbname.pre)=upper("+this.gz_tablename+".nbase)  )");
			buf.append(" where exists (select null from dbname where upper(dbname.pre)=upper("+this.gz_tablename+".nbase) )");
			dao.update(buf.toString());
			
			//同步薪资发放数据的映射表
			if(flag)
			{
				String username=this.userview.getUserName().toLowerCase();
				if(this.manager.length()>0)
					username=this.manager.toLowerCase();		
				
				if (Sql_switcher.searchDbServer()==1){//sql server 并发资源死锁 一个个删除。不用lower函数
				    String sql="delete from salary_mapping where salaryid="+this.salaryid+" and userflag='"+username
				         +"' and nbase=? and a0100=?";
				    RowSet rset = dao.search("select count(*) from "+tablename+" where state='1'");
				    int ncount=0;
				    if (rset.next()){
				        ncount=rset.getInt(1);
				    }
				    if (ncount>100){
				        buf.setLength(0);
	                    buf.append("delete from  salary_mapping ");
	                    buf.append(" where  salaryid="+this.salaryid+" and userflag='"+username+"' and  exists (select null from ");
	                    buf.append(tablename);
	                    buf.append(" where state='1' and dbname=salary_mapping.nbase and "+tablename+".a0100=salary_mapping.a0100   )");
	                    dao.update(buf.toString());  
				    }
				    else {//人少 人员引入并发
				        ArrayList valuesList =new ArrayList();
				        rset = dao.search("select dbname,a0100 from "+tablename+" where state='1'");
				        while (rset.next()){
				            ArrayList paramList =new ArrayList(); 
				            paramList.add(rset.getString("dbname").toLowerCase());
				            paramList.add(rset.getString("a0100"));
				            valuesList.add(paramList);				       
				        }
				        try{
				            dao.batchUpdate(sql, valuesList);
				        }
				        catch(Exception ex)
				        {				        
				        }   
				        finally{
				            valuesList=null;
				        }
				    }
				    
				    
				}
				else {
				    buf.setLength(0);
				    buf.append("delete from  salary_mapping ");
				    buf.append(" where  salaryid="+this.salaryid+" and lower(userflag)='"+username+"' and  exists (select null from ");
				    buf.append(tablename);
				    buf.append(" where state='1' and lower(dbname)=lower(salary_mapping.nbase) and "+tablename+".a0100=salary_mapping.a0100   )");
				    dao.update(buf.toString());
				}
				
				buf.setLength(0);
				buf.append("insert into salary_mapping (a0100,nbase,a00z0,a00z1,salaryid,userflag) ");
				buf.append(" select a0100,nbase,a00z0,a00z1,"+this.salaryid+",'"+username+"' from "+this.gz_tablename);
				buf.append(" where exists (select null from ");
				buf.append(tablename);
				buf.append(" where state='1' and lower(dbname)=lower("+this.gz_tablename+".nbase) and "+tablename+".a0100="+this.gz_tablename+".a0100   )");
				dao.update(buf.toString());
				
			}
			
			
			if(flag&& "1".equals(this.controlByUnitcode))
			{
				autoImpUnticode(tablename,dao);
			}
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}		
	}
	
	
	
	/**
	 * 引入数据时，非管理员需自动附上操作单位的值
	 * @param tablename
	 * @param dao
	 */
	private void autoImpUnticode(String tablename,ContentDAO dao)
	{
		try
		{
			StringBuffer buf=new StringBuffer(""); 
			String orgid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD,"orgid");
			orgid = orgid != null ? orgid : "";
			String deptid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid");
			deptid = deptid != null ? deptid : "";
			String unitcodes=this.userview.getUnit_id();  //UM010101`UM010105`
		 
			if(unitcodes!=null&& "UN`".equalsIgnoreCase(unitcodes))
			{
				
			}
			else
			{
				if(unitcodes==null||unitcodes.length()==0|| "UN".equalsIgnoreCase(unitcodes))
				{
					String a_code=""; 
					if(!this.userview.isSuper_admin())
					{
						if("@K".equals(this.userview.getManagePrivCode()))
							a_code=getUnByPosition(this.userview.getManagePrivCodeValue());
						else
							a_code=this.userview.getManagePrivCode()+this.userview.getManagePrivCodeValue();
					}
					if(a_code.length()>0)
						unitcodes=a_code+"`";
					else
						unitcodes=""; 
				} 
				if(unitcodes.length()>0)
				{
					String[] temps=unitcodes.split("`");
					for(int i=0;i<temps.length;i++)
					{
						String value=temps[i];
						String code=value.substring(0,2);
						String codevalue=value.substring(2);
					
						if(orgid.length()>0&&deptid.length()>0)
						{
							if("UN".equalsIgnoreCase(code))
							{
								buf.setLength(0);
								buf.append("update "+this.gz_tablename+" set "+orgid+"='"+codevalue+"'");
								buf.append(" where exists (select null from ");
								buf.append(tablename);
								buf.append(" where state='1' and lower(dbname)=lower(");
								buf.append(this.gz_tablename);
								buf.append(".nbase) and "+tablename+".a0100="+this.gz_tablename+".a0100   )");
								
								buf.append(" and exists (select null from ( select max(a00z1) a00z1,nbase,a0100 from "+this.gz_tablename+" group by a0100,nbase ) a where a.a0100="+this.gz_tablename+".a0100 ");
								buf.append(" and a.nbase="+this.gz_tablename+".nbase and a.a00z1="+this.gz_tablename+".a00z1 ) ");
								
								dao.update(buf.toString());
								if(unitcodes.toUpperCase().indexOf("UM")==-1)
								{
									buf.setLength(0);
									buf.append("update "+this.gz_tablename+" set "+deptid+"=null");
									buf.append(" where exists (select null from ");
									buf.append(tablename);
									buf.append(" where state='1' and lower(dbname)=lower(");
									buf.append(this.gz_tablename);
									buf.append(".nbase) and "+tablename+".a0100="+this.gz_tablename+".a0100   )");
									
									buf.append(" and exists (select null from ( select max(a00z1) a00z1,nbase,a0100 from "+this.gz_tablename+" group by a0100,nbase ) a where a.a0100="+this.gz_tablename+".a0100 ");
									buf.append(" and a.nbase="+this.gz_tablename+".nbase and a.a00z1="+this.gz_tablename+".a00z1 ) ");
									
									dao.update(buf.toString());
								}
							}
							else
							{
								buf.setLength(0);
								
								String un_value=getUnvalueByUm(codevalue); 
								buf.append("update "+this.gz_tablename+" set "+deptid+"='"+codevalue+"',"+orgid+"='"+un_value+"'");
								buf.append(" where exists (select null from ");
								buf.append(tablename);
								buf.append(" where state='1' and lower(dbname)=lower(");
								buf.append(this.gz_tablename);
								buf.append(".nbase) and "+tablename+".a0100="+this.gz_tablename+".a0100   )");
								
								buf.append(" and exists (select null from ( select max(a00z1) a00z1,nbase,a0100 from "+this.gz_tablename+" group by a0100,nbase ) a where a.a0100="+this.gz_tablename+".a0100 ");
								buf.append(" and a.nbase="+this.gz_tablename+".nbase and a.a00z1="+this.gz_tablename+".a00z1 ) ");
								dao.update(buf.toString());
							}
							break;
						}
						else if(orgid.length()>0)
						{
							if("UN".equalsIgnoreCase(code))
							{
								buf.setLength(0);
								buf.append("update "+this.gz_tablename+" set "+orgid+"='"+codevalue+"'");
								buf.append(" where exists (select null from ");
								buf.append(tablename);
								buf.append(" where state='1' and lower(dbname)=lower(");
								buf.append(this.gz_tablename);
								buf.append(".nbase) and "+tablename+".a0100="+this.gz_tablename+".a0100   )");
								
								buf.append(" and exists (select null from ( select max(a00z1) a00z1,nbase,a0100 from "+this.gz_tablename+" group by a0100,nbase ) a where a.a0100="+this.gz_tablename+".a0100 ");
								buf.append(" and a.nbase="+this.gz_tablename+".nbase and a.a00z1="+this.gz_tablename+".a00z1 ) ");
								dao.update(buf.toString());
							}
							else
							{
								buf.setLength(0);
								
								String un_value=getUnvalueByUm(codevalue); 
								buf.append("update "+this.gz_tablename+" set "+orgid+"='"+un_value+"'");
								buf.append(" where exists (select null from ");
								buf.append(tablename);
								buf.append(" where state='1' and lower(dbname)=lower(");
								buf.append(this.gz_tablename);
								buf.append(".nbase) and "+tablename+".a0100="+this.gz_tablename+".a0100   )");
								
								buf.append(" and exists (select null from ( select max(a00z1) a00z1,nbase,a0100 from "+this.gz_tablename+" group by a0100,nbase ) a where a.a0100="+this.gz_tablename+".a0100 ");
								buf.append(" and a.nbase="+this.gz_tablename+".nbase and a.a00z1="+this.gz_tablename+".a00z1 ) ");
								dao.update(buf.toString());
							}
							break;
						}
						else if(deptid.length()>0)
						{
						//	if(code.equalsIgnoreCase("UM"))
							{
								buf.setLength(0);
								buf.append("update "+this.gz_tablename+" set "+deptid+"='"+codevalue+"'");
								buf.append(" where exists (select null from ");
								buf.append(tablename);
								buf.append(" where state='1' and lower(dbname)=lower(");
								buf.append(this.gz_tablename);
								buf.append(".nbase) and "+tablename+".a0100="+this.gz_tablename+".a0100   )");
								
								buf.append(" and exists (select null from ( select max(a00z1) a00z1,nbase,a0100 from "+this.gz_tablename+" group by a0100,nbase ) a where a.a0100="+this.gz_tablename+".a0100 ");
								buf.append(" and a.nbase="+this.gz_tablename+".nbase and a.a00z1="+this.gz_tablename+".a00z1 ) ");
								dao.update(buf.toString());
								break;
							} 
						} 
						
					} 
				}
			}
		
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		}
		
	}
	
	
	
	private String getUnvalueByUm(String um_value)
	{
		String un_value="";
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rset=null;
		try
		{
			int n=0;
			while(true)
			{
				rset=dao.search("select * from organization where codeitemid=(select parentid from organization where codeitemid='"+um_value+"')");
				if(rset.next())
				{
					String codesetid=rset.getString("codesetid");
					String codeitemid=rset.getString("codeitemid");
					if("UN".equalsIgnoreCase(codesetid))
					{
						un_value=codeitemid;
						break;
					}
					else 
						um_value=codeitemid;
				}
				n++;
				if(n>50)
					break;
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
			}
			catch(Exception ee)
			{
				
			}
		}
		
		
		return un_value;
	}
	
	
	
	
	/**
	 * 求当前薪资类别在薪资历史数据表中已发放业务次数
	 * @param date
	 * @return
	 */
	public ArrayList getOperationCountList(String date)
	{
		ArrayList list=new ArrayList();
		try
		{
			StringBuffer buf=new StringBuffer();
			buf.append("select distinct A00Z3 from salaryhistory where  ");
			buf.append(" A00Z2=");
			buf.append(Sql_switcher.dateValue(date)+"  and salaryid="+this.salaryid+" order by A00Z3");
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rset=dao.search(buf.toString());
			CommonData temp=null;
			while(rset.next())
			{
				String strdate=rset.getString("A00Z3");
				temp=new CommonData(strdate,strdate);
				list.add(temp);
			}
			rset.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return list;
	
	}
	/**
	 * 求当前薪资类别在薪资历史数据表中已发放业务次数 zhaoxg add 2013-10-23
	 * @param date
	 * @return
	 */
	public ArrayList getRfCountList(String date)
	{
		ArrayList list=new ArrayList();
		try
		{
			StringBuffer buf=new StringBuffer();
			buf.append("select distinct A00Z3 from gz_extend_Log where  ");
			buf.append(" A00Z2=");
			buf.append(Sql_switcher.dateValue(date)+"  and salaryid="+this.salaryid+" ");
			buf.append(" and upper(username)='"+this.userview.getUserName().toUpperCase()+"'");
			buf.append(" order by A00Z3");
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rset=dao.search(buf.toString());
			CommonData temp=null;
			while(rset.next())
			{
				String strdate=rset.getString("A00Z3");
				temp=new CommonData(strdate,strdate);
				list.add(temp);
			}
			rset.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return list;
	
	}
	
	/**
	 * 获取汇总发放次数
	 * @param date 发放日期
	 * @return
	 */
	public ArrayList getCollectCountList(String date)
	{
		ArrayList list=new ArrayList();
		try
		{
			StringBuffer buf=new StringBuffer();
			buf.append("select distinct A00Z3 from salaryhistory where salaryid=");
			buf.append(this.salaryid);
			buf.append(" and A00Z2=");
			buf.append(Sql_switcher.dateValue(date));
			buf.append(" and curr_user='"+this.userview.getUserName()+"' and sp_flag in('02','07')");
			ContentDAO dao=new ContentDAO(this.conn);
			String str = buf.toString();
			RowSet rset=dao.search(buf.toString());
			CommonData temp=null;
			while(rset.next())
			{
				String strdate=rset.getString("A00Z3");
				temp=new CommonData(strdate,strdate);
				list.add(temp);
			}
			rset.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return list;
	
	}
	
	/**
	 * 求当前薪资类别在薪资历史数据表中已发放的
	 * 历史业务日期列表
	 * @return
	 */
	public ArrayList getOperationDateList()
	{
		ArrayList list=new ArrayList();
		try
		{
			StringBuffer buf=new StringBuffer();
			buf.append("select distinct A00Z2 from salaryhistory where ");
			buf.append(" ( sp_flag='06' or sp_flag='03' or curr_user='"+this.userview.getUserName()+"') and salaryid="+this.salaryid+"  order by A00Z2 desc");
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rset=dao.search(buf.toString());
			CommonData temp=null;
			while(rset.next())
			{
				String strdate=PubFunc.FormatDate(rset.getDate("A00Z2"), "yyyy.MM.dd");
				temp=new CommonData(strdate,strdate);
				list.add(temp);
			}
			rset.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return list;
	}
	
	
	
	
	
	/**
	 * 求当前薪资类别在薪资历史数据表中已发放的
	 * 历史业务日期列表
	 * @return
	 */
	public ArrayList getSubDateList()
	{
		ArrayList list=new ArrayList();
		try
		{
			StringBuffer buf=new StringBuffer();
			buf.append("select distinct A00Z2   from gz_extend_Log  where lower(username)='"+this.userview.getUserName().toLowerCase()+"' and sp_Flag='06' ");
			buf.append("   and salaryid="+this.salaryid+"  order by A00Z2 desc");
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rset=dao.search(buf.toString());
			CommonData temp=null;
			while(rset.next())
			{
				String strdate=PubFunc.FormatDate(rset.getDate("A00Z2"), "yyyy.MM.dd");
				temp=new CommonData(strdate,strdate);
				list.add(temp);
			}
			rset.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return list;
	}
	
	
	/**
	 * 获取汇总日期
	 * @return
	 */
	public ArrayList getCoolectDateList()
	{
		ArrayList list=new ArrayList();
		try
		{
			StringBuffer buf=new StringBuffer();
			buf.append("select distinct A00Z2 from salaryhistory where salaryid=");
			buf.append(this.salaryid);
			buf.append(" and ( sp_flag='07' or sp_flag='02' ) and curr_user='"+this.userview.getUserName()+"'   order by A00Z2 desc");
	//		buf.append(" and (( curr_user='"+this.userview.getUserId()+"' and ( sp_flag='02' or sp_flag='07' ) ) or ( ( (AppUser is null  "+this.getPrivWhlStr("")+"  )  or AppUser Like '%;"+this.userview.getUserName()+";%' ) and  ( sp_flag='06' or  sp_flag='03' ) ) )  order by A00Z2 desc");
			
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rset=dao.search(buf.toString());
			CommonData temp=null;
			while(rset.next())
			{
				String strdate=PubFunc.FormatDate(rset.getDate("A00Z2"), "yyyy.MM.dd");
				temp=new CommonData(strdate,strdate);
				list.add(temp);
			}
			rset.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 当前工资数据是否可以发放
	 * @return
	 */
	public String getSubFlag()
	{
		String subFlag="false";
		try
		{
			if(!isApprove())   /** 是否需要审批 */
				subFlag="true";
			else
			{
				ContentDAO dao=new ContentDAO(this.conn);
				RowSet rset=dao.search("select count(a0100) from "+this.gz_tablename+" where sp_flag<>03");
				if(rset.next())
				{
					if(rset.getInt(1)==0)
						subFlag="true";
				}
				rset.close();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return subFlag;
	}
	
	
	
	
	/**
	 * 取得SQL过滤条件
	 * @param condid
	 * @param tablename
	 * @return
	 */
	public String getFilterWhere(String condid,String tablename)
	{
	  String strwhere="";
	  try
	  {
		/**
		 * 表达式|因子
		 */
		String value=(String)condmap.get(condid);
		if(value==null|| "".equalsIgnoreCase(value))
		{
			
			//return "";
			templatevo=new RecordVo("salarytemplate");
			templatevo.setInt("salaryid", this.salaryid);
			ContentDAO dao=new ContentDAO(this.conn);
			templatevo=dao.findByPrimaryKey(templatevo);
			String lpro=this.templatevo.getString("lprogram");
			if(!(lpro==null|| "".equalsIgnoreCase(lpro)))
			{
				SalaryLProgramBo lprgbo=new SalaryLProgramBo(lpro);
				condmap=lprgbo.getServiceItemMap();
				value=(String)condmap.get(condid);
				if(value==null|| "".equalsIgnoreCase(value))
					return "";
			}
			else
				return "";
		}
		int idx=0;
		idx=value.indexOf("|");
		String expr=value.substring(0, idx);
		String factor=value.substring(idx+1).toUpperCase();
		/**处理当用审批状态字段作为条件时，报sp_flag未构库或者此指标已删除错误*/
		BankDiskSetBo bo = new BankDiskSetBo(this.conn);
		HashMap fieldItemMap=bo.getFieldItemMap(salaryid,this.userview);
		String manager=this.getCtrlparam().getValue(SalaryCtrlParamBo.SHARE_SET, "user");
		if("salaryarchive".equalsIgnoreCase(tablename)||(manager!=null&&manager.length()>0))
		{
		    if(fieldItemMap.get("SP_FLAG")==null)
		    {
			FieldItem item = new FieldItem();
			item.setCodesetid("23");
			item.setUseflag("1");
			item.setItemtype("A");
			item.setItemid("SP_FLAG");
			item.setAlign("left");
			item.setItemdesc("审批状态");
			fieldItemMap.put("SP_FLAG",item);
		    }
		}
		if(manager!=null&&manager.length()>0)
		{
			if(fieldItemMap.get("SP_FLAG2")==null)
		    {
			FieldItem item = new FieldItem();
			item.setCodesetid("23");
			item.setUseflag("1");
			item.setItemtype("A");
			item.setItemid("SP_FLAG2");
			item.setAlign("left");
			item.setItemdesc("报审状态");
			fieldItemMap.put("SP_FLAG2",item);
		    }
		}
		FactorList factor_bo=new FactorList(expr,factor,this.userview.getUserId(),fieldItemMap);
		strwhere=factor_bo.getSingleTableSqlExpression(tablename);
		/*FactorList factorlist=new FactorList(expr,factor,"");
		strwhere=factorlist.getSingleTableSqlExpression(tablename);*/
	  }
	  catch(Exception ex)
	  {
		  ex.printStackTrace();
	  }
	  return strwhere;
	}
	/**
	 * 根据定义的项目过滤编号，进行项目过滤
	 * @param list     薪资类别中的全部指标
	 * @param itemid   项目过滤编号
	 * @return
	 */
	public ArrayList filterItemList(ArrayList list,String itemid)
	{
		ArrayList filterlist=new ArrayList();
		try
		{
			StringBuffer strread=new StringBuffer();
			/**只读字段*/
			strread.append("NBASE,A0100,A00Z0,A00Z1,");			
			String fields=(String)itemmap.get(itemid);
			if(fields==null)
				fields="";
			fields="NBASE,A0100,A00Z0,A00Z1,"+fields;//这四项传到前台做主键，zhaoxg add
			for(int i=0;i<list.size();i++)
			{
				Field field=(Field)list.get(i);
				String fieldname=field.getName();
				if(fields.indexOf(fieldname)==-1)
				{
					//if(strread.indexOf(fieldname)!=-1)
						field.setVisible(false);
					//else
						continue;
				}

				filterlist.add(field);
			}//for i loop end.
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return filterlist;		
	}
	/**
	 * 根据定义的项目过滤编号，进行项目过滤
	 * @param list     薪资类别中的全部指标
	 * @param itemid   项目过滤编号
	 * @return
	 */
	public ArrayList filterList(ArrayList list,String flag)
	{
		ArrayList filterlist=new ArrayList();
		try
		{
			String fields = "";
			String arr[] = flag.split(",");
			if(arr.length==2){
				if(this.manager.length()>0)
				{
					
				}
				if("0".equals(arr[0])){
					fields+="sp_flag,";
				}
				if("0".equals(arr[1])){
					fields+="appprocess,";
				}
				fields=fields.toUpperCase();
				for(int i=0;i<list.size();i++)
				{
					Field field=(Field)list.get(i);
					String fieldname=field.getName();
					if(fields.indexOf(fieldname.toUpperCase())==-1){
						filterlist.add(list.get(i));
					}
				}//for i loop end.
			}else{
				filterlist.addAll(list);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return filterlist;		
	}
	/**
	 * 查询项目过滤列表
	 * @return
	 */
	private ArrayList searchItemFilter(String ids)
	{
		ArrayList list=new ArrayList();
		if(itemmap==null)
			itemmap=new HashMap();
		try
		{
			StringBuffer buf=new StringBuffer();
			buf.append("select id,chz,cfldname,scope,username from gzitem_filter where ");
			if(ids==null|| "".equals(ids))
			{
				buf.append(" 1=2 ");
			}
			else
			{
	    		buf.append("id in (");
	    		buf.append(ids);
	    		buf.append(")");
			}
			buf.append(" order by norder ");
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rset=dao.search(buf.toString());
			/**所有项目*/
			CommonData temp=new CommonData("all",ResourceFactory.getProperty("label.gz.allitem"));
			list.add(temp);
			while(rset.next())
			{
				if(this.userview.getUserName().equals(rset.getString("username")==null?"":rset.getString("username"))){
					temp=new CommonData(rset.getString("id"),rset.getString("chz"));
					list.add(temp);
				}else{
					if(rset.getInt("scope")==0){
						temp=new CommonData(rset.getString("id"),rset.getString("chz"));
						list.add(temp);
					}else{//超级用户组能看到私有过滤项目
				/*		if(this.userview!=null&&this.userview.isSuper_admin()){
							temp=new CommonData(rset.getString("id"),rset.getString("chz"));
							list.add(temp);
						}*/
						
					}
				}
				//增加两个字段 xieguiquan
				/**为了快速查找*/
				itemmap.put(rset.getString("id"), Sql_switcher.readMemo(rset, "cfldname"));
			}
			/**新建*/
			if(this.userview.hasTheFunction("3240223")||this.userview.hasTheFunction("3240323")||this.userview.hasTheFunction("3250323")||this.userview.hasTheFunction("3250223")||this.userview.hasTheFunction("031404")){
			temp=new CommonData("new",ResourceFactory.getProperty("label.gz.new"));
			list.add(temp);
			}
			rset.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return list;
		
	}
	/**
	 * 取得薪资类别项目过滤列表，可考滤一次查询，多次使用
	 * @return
	 */
	public ArrayList getItemFilterList(String ids)
	{
//		if(itemlist==null)  // FengXiBin Changed 2007.09.30
			itemlist=searchItemFilter(ids);
		return itemlist;
	}
	/**
	 * 获取共享的过滤项目，zhaoxg add 2013-12-12
	 * @param fiterItemIds
	 * @return
	 */
    public String getFiterItem()
    { 
    	StringBuffer str=new StringBuffer();
        try
        {
            StringBuffer buf=new StringBuffer();
            buf.append("select id,chz,cfldname,scope,username from gzitem_filter where ");
            buf.append(" scope =0 order by norder ");//共享的
            ContentDAO dao=new ContentDAO(this.conn);
            RowSet rset=dao.search(buf.toString());
            
            while(rset.next()){         
                str.append(rset.getString("id"));
                str.append(",");
            }  
            rset.close();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return str.toString();
        
    }
	/**
	 * 查询人员过滤条件
	 * @return
	 */
	private ArrayList searchManFilter()
	{
		ArrayList list=new ArrayList();
		try
		{
			CommonData temp=new CommonData("all",ResourceFactory.getProperty("label.gz.allman"));
			list.add(temp);			
			String lpro=this.templatevo.getString("lprogram");
			if(!(lpro==null|| "".equalsIgnoreCase(lpro)))
			{
				SalaryLProgramBo lprgbo=new SalaryLProgramBo(lpro,this.userview); // xieguiquan 20100828 add this.userview 
				list.addAll(lprgbo.getServiceItemList2());
				/**求人员过滤条件*/
//				if(condmap==null) // FengXiBin Changed 2007.09.29
					condmap=lprgbo.getServiceItemMap();
			}//if end.
			temp=new CommonData("new",ResourceFactory.getProperty("label.gz.new"));
			list.add(temp);			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return list;		
		
	}
	
	
	/** 取得薪资类别项目的数据类型 map */
	public HashMap getSalaryItemMap()
	{
		HashMap map=new HashMap();
		try
		{
			ArrayList list=getSalaryItemList2();
			for(int i=0;i<list.size();i++)
			{
				LazyDynaBean abean=(LazyDynaBean)list.get(i);
				map.put((String)abean.get("itemid"),abean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	
	
	/**
	 * 根据工资类别id得到类别下面的所有项目列表
	 * @author dengcan
	 * @return
	 */
	public ArrayList getSalaryItemList()
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer buf = new StringBuffer("select * from salaryset where salaryid="+this.salaryid);
			if(this.getQueryvalue()!=null&&!"".equals(this.getQueryvalue()))
				buf.append("and ("+this.getQueryvalue()+")");
		//	buf.append(" order by initflag desc,sortid");
			buf.append(" order by sortid");
			RowSet rowSet=dao.search(buf.toString());
			LazyDynaBean abean=null;
			while(rowSet.next())
			{
				abean=new LazyDynaBean();
				abean.set("salaryid",rowSet.getString("salaryid"));
				abean.set("fieldid",rowSet.getString("fieldid"));
				abean.set("itemdesc",rowSet.getString("itemdesc"));
				abean.set("initflag",rowSet.getString("initflag"));
				int initFlag=rowSet.getInt("initflag");
				if(initFlag==0||initFlag==1||initFlag==2)
				{
					StringBuffer selectStr=new StringBuffer("<select name="+rowSet.getString("salaryid")+"/"+rowSet.getString("fieldid")+" onchange='changeValue(this)'  >");
					selectStr.append("<option value='0' "+(initFlag==0?"selected":"")+" >输入项</option>");
					if("N".equals(rowSet.getString("itemtype")))
						selectStr.append("<option value='1' "+(initFlag==1?"selected":"")+" >累计项</option>");
					selectStr.append("<option value='2' "+(initFlag==2?"selected":"")+" >导入项</option>");
					selectStr.append("</select>");
					abean.set("manageFashion",selectStr.toString());
				}
				else if(initFlag==3)
					abean.set("manageFashion","系统项");
				abean.set("fieldsetid",rowSet.getString("fieldsetid"));
				abean.set("itemtype",rowSet.getString("itemtype"));
				abean.set("decwidth",rowSet.getString("decwidth"));
				abean.set("codesetid",rowSet.getString("codesetid"));
				abean.set("itemid",rowSet.getString("itemid"));
				abean.set("nlock",rowSet.getString("nlock"));
				list.add(abean);
			}
			rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	
	/**
	 * 根据工资类别id得到类别下面的所有项目列表
	 * @author dengcan
	 * @return
	 */
	public ArrayList getSalaryItemList2()
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select * from salaryset where salaryid="+this.salaryid+" order by sortid");
			LazyDynaBean abean=null;
			while(rowSet.next())
			{
				abean=new LazyDynaBean();
				abean.set("salaryid",rowSet.getString("salaryid"));
				abean.set("fieldid",rowSet.getString("fieldid"));
				abean.set("itemdesc",rowSet.getString("itemdesc"));
				abean.set("initflag",rowSet.getString("initflag"));
				abean.set("fieldsetid",rowSet.getString("fieldsetid"));
				abean.set("itemtype",rowSet.getString("itemtype"));
				abean.set("decwidth",rowSet.getString("decwidth"));
				abean.set("codesetid",rowSet.getString("codesetid"));
				abean.set("itemid",rowSet.getString("itemid"));
				abean.set("itemlength",rowSet.getString("itemlength"));
				if("A".equalsIgnoreCase(rowSet.getString("itemtype"))&&!"0".equals(rowSet.getString("codesetid")))
				{
					String codesetid=rowSet.getString("codesetid");
					HashMap map=new HashMap();
					if("UN".equals(codesetid)|| "@K".equals(codesetid)|| "UM".equals(codesetid))
					{
						RowSet recset=dao.search("select * from organization where codesetid='"+rowSet.getString("codesetid")+"'");
						while(recset.next())
						{
							map.put(recset.getString("codeitemdesc").toLowerCase(), recset.getString("codeitemid"));
							map.put(recset.getString("codeitemid").toLowerCase(), recset.getString("codeitemid"));
						}
						recset.close();
					}
					else
					{
						RowSet recset=dao.search("select * from codeitem where codesetid='"+rowSet.getString("codesetid")+"'");						
						while(recset.next())
						{
							map.put(recset.getString("codeitemdesc").toLowerCase(), recset.getString("codeitemid"));
							map.put(recset.getString("codeitemid").toLowerCase(), recset.getString("codeitemid"));
						}
						recset.close();
					}
					abean.set("options",map);
				}
				else
				{
					abean.set("options",new HashMap());
				}
				list.add(abean);
			}
			rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	
	/**
	 * 删除工资项目
	 * @param salarySetIDs
	 */
	public void delSalarySet(String[] salarySetIDs)
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer whl=new StringBuffer("");
			for(int i=0;i<salarySetIDs.length;i++)
				whl.append(","+salarySetIDs[i]);
			dao.delete("delete from  salaryset where salaryid="+this.salaryid+" and fieldid in ("+whl.substring(1)+")",new ArrayList());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 新增工资项目
	 * @param fielditemIDs
	 * @author dengcan
	 * @throws GeneralException
	 */
	public void saveSalarySet(String[] fielditemIDs)throws GeneralException
	{
		try
		{
			StringBuffer selectedID=new StringBuffer("");
			ContentDAO dao=new ContentDAO(this.conn);
			int fieldid=0; int sortid=0;
			RowSet rowSet=dao.search("select max(fieldid),max(sortid) from salaryset where salaryid="+this.salaryid);
			if(rowSet.next())
			{
				fieldid=rowSet.getInt(1);
				sortid=rowSet.getInt(2);
			}
			
			SalaryPkgBo bo=new SalaryPkgBo(this.conn,this.userview,0);
			ArrayList list=new ArrayList();
			for(int i=0;i<fielditemIDs.length;i++)
			{
				FieldItem tempItem=DataDictionary.getFieldItem(fielditemIDs[i].toLowerCase());
				int nwidth=20;
				if("N".equals(tempItem.getItemtype())|| "D".equals(tempItem.getItemtype()))
					nwidth=10;
				list.add(bo.getSalarySetRecordvo(this.salaryid,++fieldid,tempItem.getFieldsetid(),tempItem.getItemid(),tempItem.getItemdesc(),tempItem.getItemlength(),
						tempItem.getDecimalwidth(),tempItem.getCodesetid(),++sortid,nwidth,tempItem.getItemdesc(),2,0,tempItem.getItemtype()));				
			}
			dao.addValueObject(list);
			rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	
	/**
	 * 同步薪资类别结构
	 * @param salaryids
	 */
	public void synchronizeSalaryStruct(String salaryids)
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			DbWizard dbWizard=new DbWizard(this.conn); 
			DBMetaModel dbmodel=new DBMetaModel(this.conn);
			String[] salaryIDs=salaryids.split(",");
			for(int i=0;i<salaryIDs.length;i++)
			{
				if(salaryIDs[i].length()==0)
					continue;
				this.salaryid=Integer.parseInt(salaryIDs[i]);
				ctrlparam=new SalaryCtrlParamBo(this.conn,this.salaryid);
				this.fieldlist=searchGzItem();
				synchronismSalarySet();
				//同步临时表
				ArrayList tempTableList=getTempSalaryTableList(salaryIDs[i]);
				for(int j=0;j<tempTableList.size();j++)
				{
					this.gz_tablename=(String)tempTableList.get(j);
					synchronizeTempSalaryStruct(this.gz_tablename);
					
				}
				synchronismSalaryFormula();//同步计算公式名称  zhaoxg add 2013-10-14 
				//同步历史数据表
				upgradeGzHisTableStruct();
				dbmodel.reloadTableModel("salaryhistory");	
				upgradGzHisTableStruct2();
				dbmodel.reloadTableModel("salaryhistory");
				syncGzField("salaryhistory");
				dbmodel.reloadTableModel("salaryhistory");
				syncGzField("salaryarchive");
				dbmodel.reloadTableModel("salaryarchive");
			}
			
			
		 	//解决8060问题  邓灿
			PubFunc.resolve8060(this.conn,"SalaryHistory");

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	//同步临时表结构
	private void synchronizeTempSalaryStruct(String tableName)
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			DbWizard dbWizard=new DbWizard(this.conn); 
			DBMetaModel dbmodel=new DBMetaModel(this.conn);
			dbmodel.reloadTableModel(gz_tablename);	
			/**升级表结构*/
			upgradeGzTableStruct();		
			/**重新加载数据模型*/
			dbmodel.reloadTableModel(gz_tablename);	
			RecordVo vo=new RecordVo(this.gz_tablename);
			ArrayList list=vo.getModelAttrs();
			StringBuffer buf=new StringBuffer();
			for(int e=0;e<list.size();e++)
			{
				String name=(String)list.get(e);
				buf.append(name.toUpperCase());
				buf.append(",");
			}//for i loop end.
			StringBuffer buf0=new StringBuffer();
			ArrayList addlist=new ArrayList();
			boolean isAddFlag=true;  //临时表是否有追加字段
			/**如果定义中有，而薪资表中没有，则增加此字段*/
			for(int e=0;e<this.fieldlist.size();e++)
			{
				Field field=(Field)this.getFieldlist().get(e);
				String name=field.getName().toUpperCase();
				/**如果未找到，则追加*/
				if(buf.indexOf(name+",")==-1)
				{
					addlist.add(field);
					if("add_flag".equalsIgnoreCase(name))
						isAddFlag=false;
				}
				buf0.append(name);
				buf0.append(",");
			}//for i loop end.
			/**如果定义中没有，而薪资表中有则删除此字段*/
			ArrayList dellist=new ArrayList();
			for(int e=0;e<list.size();e++)
			{
				String name=((String)list.get(e)).toUpperCase();
				if("userflag".equalsIgnoreCase(name))
					continue;
				if("sp_flag".equalsIgnoreCase(name))
					continue;	
				if("sp_flag2".equalsIgnoreCase(name))
					continue;	
				if("appprocess".equalsIgnoreCase(name))
					continue;					
				if(buf0.indexOf(name)==-1)
				{
					dellist.add(name);
				}
			}//for i loop end.			
			DbWizard dbw=new DbWizard(this.conn);
			Table table=new Table(this.gz_tablename);
			for(int e=0;e<addlist.size();e++)
				table.addField((Field)addlist.get(e));
			if(addlist.size()>0)
				dbw.addColumns(table);
			
			table.clear();
			for(int e=0;e<dellist.size();e++)
			{
				Field field=new Field((String)dellist.get(e),(String)dellist.get(e));
				table.addField(field);
			}
			/**两边都有的指标，有可能长度或类型发生的变化*/
			syncGzField(gz_tablename);
			/**重新加载数据模型*/
			dbmodel.reloadTableModel(gz_tablename);	
			if(!isAddFlag)
			{
				String[] temp=this.gz_tablename.split("_salary_");
				buf.setLength(0);
				HashMap map=getMaxYearMonthCount(temp[0]);
				/**需要审批的话,则分析当前处理的业务日期是否处理审批状态*/
				String a_currym=(String)map.get("ym");
				String a_currcount=(String)map.get("count");
				
				String sp_flag="01";
				buf.append("select sp_flag from gz_extend_log where salaryid="+this.salaryid);
				buf.append(" and upper(username)='"+temp[0].toUpperCase()+"'");
				buf.append(" and A00Z2=");
				buf.append(Sql_switcher.dateValue(a_currym));
				buf.append(" and A00Z3=");
				buf.append(a_currcount);
				RowSet rset=dao.search(buf.toString());
				if(rset.next())
					sp_flag=rset.getString(1);
				if("06".equals(sp_flag))
					dao.update("update "+this.gz_tablename+" set add_flag=1");
				else
					dao.update("update "+this.gz_tablename+" set add_flag=0");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public ArrayList getTempSalaryTableList(String salaryid)
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String sql="";
			switch(Sql_switcher.searchDbServer())
			{
					case Constant.MSSQL:
						sql="select name from sysobjects where type='u' and lower(name) like '%_salary_"+salaryid+"'";    //syscolumns 
						break;
					case Constant.DB2:
						sql="SELECT NAME FROM SYSIBM.SYSTABLES WHERE TYPE = 'T' AND CREATOR != 'SYSIBM' and name like '%_salary_"+salaryid+"'";
						break;
					case Constant.ORACEL:
						sql="SELECT TABLE_NAME FROM USER_TABLES WHERE LOWER(TABLE_NAME) LIKE '%_salary_"+salaryid+"'";  //all_tab_columns
						break;
			}
			if(sql.length()>0)
			{
					RowSet rowSet=dao.search(sql);
					while(rowSet.next())
					{
						String tableName=rowSet.getString(1);
						list.add(tableName);
					}
			}
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	
	
	
	
	/**
	 * 同步工资类别里的工资项
	 * @param salaryid
	 */
	public void synchronismSalarySet()
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String whl=" and upper(itemid) not in ('B0110','A00Z1','A00Z0','A00Z2','A00Z3','A0000','A0100','NBASE'," +
					"'A01Z0' ,'E01A1')";//,'E0122'放开部门，部门名称可能会别改  需要同步一下
			RowSet rowSet=dao.search("select fieldid,itemid from salaryset where salaryid="+this.salaryid+" "+whl );
			RecordVo vo=null;
			ArrayList list=new ArrayList();
			StringBuffer itemid_str=new StringBuffer("");
			boolean flag=false;
			StringBuffer str= new StringBuffer();
			while(rowSet.next())
			{
				str.append("'"+rowSet.getString("itemid").toUpperCase()+"'");
				str.append(",");
				flag=true;
			}
			
			HashMap map=new HashMap();
			if(str!=null&&str.length()>0){
				String sql="select * from fielditem where upper(itemid) in ("+str.substring(0, str.length()-1)+")";
				RowSet rs=dao.search(sql);
				while(rs.next()){
					LazyDynaBean bean = new LazyDynaBean();
					bean.set("itemdesc", rs.getString("itemdesc"));
					bean.set("itemlength", rs.getString("itemlength"));
					bean.set("decwidth", rs.getString("decimalwidth"));
					bean.set("codesetid", rs.getString("codesetid"));
					bean.set("itemtype", rs.getString("itemtype"));
					bean.set("useflag", rs.getString("useflag"));
					map.put(rs.getString("itemid").toLowerCase(), bean);
				}
			}

			if(flag){//没指标则不执行任何操作
				rowSet.beforeFirst();//返回第一位光标之前，而不是第一个，否则next就从第二个开始了  zhaoxg 2015-4-8
				while(rowSet.next())
				{
					int fieldid=rowSet.getInt("fieldid");
					String itemid=rowSet.getString("itemid");
					vo=new RecordVo("salaryset");
					vo.setInt("salaryid",this.salaryid);
					vo.setInt("fieldid", fieldid);
					vo=dao.findByPrimaryKey(vo);
					LazyDynaBean tempItem=null;
					if(!map.isEmpty()){
						tempItem=(LazyDynaBean) map.get(itemid.toLowerCase());
					}

					if(tempItem!=null&& "1".equals(tempItem.get("useflag"))){
						vo.setString("itemdesc", (String) tempItem.get("itemdesc"));
						vo.setInt("itemlength",Integer.parseInt((String) tempItem.get("itemlength")));
						vo.setInt("decwidth",Integer.parseInt((String) tempItem.get("decwidth")));
						vo.setString("codesetid",(String) tempItem.get("codesetid"));
						vo.setString("itemtype",(String) tempItem.get("itemtype"));
					}
					else
					{
						itemid_str.append(",'"+itemid.toUpperCase()+"'");
					}
					list.add(vo);
					if(itemid_str.length()>0)
					{
						dao.delete("delete from salaryset where salaryid="+this.salaryid+"  and upper(itemid) in ("+itemid_str.substring(1)+")",new ArrayList());
					}
				}
			}
			

			dao.updateValueObject(list);
			rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	/**
	 * 
	 * @Title: SalarySet   
	 * @Description:    判断哪些字段改变了需要同步
	 * @param  
	 * @return void 
	 * @author:zhaoxg   
	 * @throws GeneralException 
	 * @throws
	 */
	public void SalarySet() throws GeneralException
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String whl=" and itemid not in ('B0110','A00Z1','A00Z0','A00Z2','A00Z3','A0000','A0100','NBASE'," +
					"'A01Z0' ,'E01A1')";//,'E0122'放开部门，部门名称可能会别改  需要同步一下
			RowSet rowSet=dao.search("select fieldid,itemid from salaryset where salaryid="+this.salaryid+" "+whl );
			RecordVo vo=null;
			boolean flag=false;
			StringBuffer str= new StringBuffer();
			StringBuffer itemname = new StringBuffer();
			while(rowSet.next())
			{
				str.append("'"+rowSet.getString("itemid").toUpperCase()+"'");
				str.append(",");
				flag=true;
			}
			HashMap map=new HashMap();
			if(str!=null&&str.length()>0){
				String sql="select * from fielditem where upper(itemid) in ("+str.substring(0, str.length()-1)+")";
				RowSet rs=dao.search(sql);
				while(rs.next()){
					LazyDynaBean bean = new LazyDynaBean();
					bean.set("itemdesc", rs.getString("itemdesc")==null?"":rs.getString("itemdesc"));
					bean.set("itemlength", rs.getString("itemlength")==null?"0":rs.getString("itemlength"));
					bean.set("decwidth", rs.getString("decimalwidth")==null?"0":rs.getString("decimalwidth"));
					bean.set("codesetid", rs.getString("codesetid")==null?"":rs.getString("codesetid"));
					bean.set("itemtype", rs.getString("itemtype")==null?"":rs.getString("itemtype"));
					bean.set("useflag", rs.getString("useflag")==null?"":rs.getString("useflag"));
					map.put(rs.getString("itemid").toLowerCase(), bean);
				}
			}
			if(flag){//没指标则不执行任何操作
				rowSet.beforeFirst();//返回第一位光标之前，而不是第一个，否则next就从第二个开始了  zhaoxg 2015-4-8
				while(rowSet.next())
				{
					int fieldid=rowSet.getInt("fieldid");
					String itemid=rowSet.getString("itemid");
					vo=new RecordVo("salaryset");
					vo.setInt("salaryid",this.salaryid);
					vo.setInt("fieldid", fieldid);
					vo=dao.findByPrimaryKey(vo);
					LazyDynaBean tempItem=null;
					if(!map.isEmpty()){
						tempItem=(LazyDynaBean) map.get(itemid.toLowerCase());
					}
					if(tempItem!=null&& "1".equals(tempItem.get("useflag"))){
						if(!(vo.getInt("itemlength")==Integer.parseInt((String) tempItem.get("itemlength")))
								||!(vo.getInt("decwidth")==Integer.parseInt((String) tempItem.get("decwidth")))
								||!vo.getString("codesetid").equalsIgnoreCase((String) tempItem.get("codesetid"))
								||!vo.getString("itemtype").equalsIgnoreCase((String) tempItem.get("itemtype"))
								){
							itemname.append((String) tempItem.get("itemdesc")+",");
						}
					}
				}
			}
			if(itemname.length()>0){
				throw GeneralExceptionHandler.Handle(new Exception(itemname+"指标发生变化了，请到薪资类别中进行结构同步后再使用！"));
			}
			rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	/**
	 * 同步计算公式 zhaxg add 2013-10-14
	 */
	public void synchronismSalaryFormula()
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select * from salaryformula where salaryid="+this.salaryid+"");
			RecordVo vo=null;
			ArrayList list=new ArrayList();
			while(rowSet.next())
			{
				int itemid=rowSet.getInt("itemid");
				String itemname=rowSet.getString("itemname");
				vo=new RecordVo("salaryformula");
				vo.setInt("salaryid",this.salaryid);
				vo.setInt("itemid", itemid);
				vo=dao.findByPrimaryKey(vo);
				FieldItem tempItem=DataDictionary.getFieldItem(itemname.toLowerCase());
				if(tempItem!=null&& "1".equals(tempItem.getUseflag())){
					vo.setString("hzname", tempItem.getItemdesc());
				}
				list.add(vo);
			}
			dao.updateValueObject(list);
			rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	
	/**
	 * 修改新资项目标识 
	 * @param fieldid
	 * @param flag  0：输入项标识  1：累计项标识  2：导入项标识
	 * @param formula   计算公式
	 * @param heapFlag  0:不累积 1：月内累积 2：季度内累积 3：年内累积 4：无条件累积 5：季度内同次累积 6：年内同次累积 7：同次累积
	 * @author dengcan
	 * @throws GeneralException
	 */
	public void updateSalarySetFlag(String fieldid,String flag,String formula,String heapFlag)throws GeneralException
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RecordVo vo=new RecordVo("salaryset");
			vo.setInt("salaryid",this.salaryid);
			vo.setInt("fieldid",Integer.parseInt(fieldid));
			vo=dao.findByPrimaryKey(vo);
			
		    if("0".equals(flag))
		    {
		    	vo.setInt("initflag",0);	    	
		    }
		    else if("1".equals(flag))
		    {
		    	vo.setInt("initflag",1);
		    	vo.setString("formula",formula);
		    	vo.setString("heapflag",heapFlag);		    	
		    }
		    else if("2".equals(flag))
		    {
		    	vo.setInt("initflag",2);
		    	vo.setString("formula",formula);
		    	vo.setString("heapflag",heapFlag);
		    }
		    dao.updateValueObject(vo);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	
	
	public Map<String, ArrayList>  validateData(String[] oppositeItem,String[] relationItem,FormFile form_file,ArrayList originalDataList)
	{
		String msg="";
		InputStream stream = null;
		Map<String, ArrayList> map = new HashMap();
		HashMap allExistDataMap=null;
		try
		{
			stream = form_file.getInputStream();
			GzExcelBo gzExbo=new GzExcelBo(this.conn);
			gzExbo.getSelfAttribute(stream);
//			int rowNums=gzExbo.getTotalDataRows();   //数据总行数
			int rowNums=gzExbo.getTotalDataRows2();   //数据总行数
			int pageNum=rowNums/100+1;
			LazyDynaBean abean=null;
			HashMap salaryItemTypeMap=getSalaryItemMap();  //薪资项目数据类型 map
			String updateSql=getBatchUpdateSQL(oppositeItem,relationItem);
			ArrayList updateDateList=new ArrayList();     //更新数据
			
			String[] ori_item=new String[relationItem.length];
			for(int j=0;j<relationItem.length;j++)
			{
				String temp=relationItem[j];
				temp = PubFunc.keyWord_reback(temp);
				String[] temps=temp.split("=");
				ori_item[j]=temps[0].trim();
			}
			if (rowNums>1500) 
				  allExistDataMap=getAllDataMap(relationItem,salaryItemTypeMap);
			for(int i=1;i<=pageNum;i++)
			{
				int from=(i-1)*100+1;
				int to=i*100;
				if(to>rowNums)
					to=rowNums;
				ArrayList dataList=gzExbo.getDefineData(from,to,originalDataList);
				HashMap existDataMap=null;
				if (allExistDataMap!=null)
				    existDataMap=allExistDataMap;
				else   
				    existDataMap=getExistDataMap(dataList,relationItem,salaryItemTypeMap);
				
				
				ArrayList a_dataList=null;
				StringBuffer temp=new StringBuffer("");
				for(int e=0;e<dataList.size();e++)
				{
					abean=(LazyDynaBean)dataList.get(e);
					
					temp.setLength(0);
					for(int f=0;f<ori_item.length;f++)
						temp.append("/"+(String)abean.get(ori_item[f]));
					
				 	if(existDataMap.get(temp.toString())==null)
				  			continue;
				 	
				 	//获得更新数据
				 	a_dataList=new ArrayList();
					setListData(oppositeItem,abean,salaryItemTypeMap,a_dataList);
					setListData(relationItem,abean,salaryItemTypeMap,a_dataList);
					updateDateList.add(a_dataList);
					
					a_dataList=new ArrayList();
					msg=validateListData(oppositeItem,abean,salaryItemTypeMap,a_dataList);
					if(msg.trim().length()>0)
						break;
					msg=validateListData(relationItem,abean,salaryItemTypeMap,a_dataList);
					if(msg.trim().length()>0)
						break;
				}
			}
			map.put(msg, updateDateList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
		}finally{
		    allExistDataMap=null;
			PubFunc.closeIoResource(stream);
		}
		return map;
	}
	
	
	   public HashMap getAllDataMap(String[] relationItem,HashMap salaryItemTypeMap)throws GeneralException
	   {
	        HashMap map=new HashMap();
	        RowSet rowSet2=null;
	        try
	        {
	            ContentDAO dao=new ContentDAO(this.conn);
	            String[] ori_item=new String[relationItem.length];
	            String[] aim_item=new String[relationItem.length];
	            
	            StringBuffer select_str=new StringBuffer("");
	            
	            //HashMap salaryItemTypeMap=getSalaryItemMap();  //薪资项目数据类型 map
	            for(int i=0;i<relationItem.length;i++)
	            {
	                String temp=relationItem[i];
	                temp = PubFunc.keyWord_reback(temp);
	                String[] temps=temp.split("=");
	                ori_item[i]=temps[0].trim();
	                aim_item[i]=temps[1].trim();
	                select_str.append(","+aim_item[i].trim());
	            }
	            
	            if(select_str.length()>1)
	            {
	                String sql="select * from "+this.gz_tablename+" where ( 1=1 ) ";
	                if(manager!=null&&manager.length()>0&&!this.userview.getUserName().equals(manager))
	                {
	                    
	                    if("1".equals(this.controlByUnitcode))
	                    {
	                        String whl_str=getWhlByUnits();
	                        if(whl_str.length()>0)
	                        {
	                            sql+=whl_str;
	                        }
	                    }
	                    else if(!this.userview.isSuper_admin())
	                    {
	                        String codesetid=this.userview.getManagePrivCode();
	                        String value=this.userview.getManagePrivCodeValue();
	                        StringBuffer buf=new StringBuffer("");
	                        
	                        if(codesetid.length()==0)
	                        {
	                            buf.append(" and b0110='-1'");
	                        }
	                        if("@K".equalsIgnoreCase(codesetid))
	                        {
	                            String str=getUnByPosition(value);
	                            codesetid=str.substring(0,2);
	                            value=str.substring(2);
	                        }
	    
	                        if("UN".equalsIgnoreCase(codesetid))
	                        {
	                            buf.append(" and (B0110 like '");
	                            buf.append(value);
	                            buf.append("%'");   
	                            if("".equalsIgnoreCase(value))
	                            {
	                                buf.append(" or B0110 is null");
	                                
	                            }
	                            buf.append(")");
	                        }
	                        if("UM".equalsIgnoreCase(codesetid))
	                        {
	                            buf.append(" and E0122 like '");
	                            buf.append(value);
	                            buf.append("%'");
	                        }
	                        if(buf.length()>0)
	                        {
	                            sql+=buf.toString();
	                        }
	                    }
	                }
	                
	                String dataSql=sql.replace("*", select_str.substring(1));
	                String countSql=sql.replace("*", "count(*)");
	                    
	                    
	                rowSet2=dao.search(countSql);
	                if (rowSet2.next()){
	                    int num=rowSet2.getInt(1);
	                    if (num>6000) 
	                        return null;
	                }
	                StringBuffer temp=new StringBuffer("");
	                SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
	                rowSet2=dao.search(dataSql);
	                while(rowSet2.next())
	                {
	                    temp.setLength(0);
	                    for(int i=0;i<relationItem.length;i++)
	                    {
	                        LazyDynaBean columnBean=(LazyDynaBean)salaryItemTypeMap.get(aim_item[i].trim());
	                        String decwidth=(String)columnBean.get("decwidth");
	                        String itemtype=(String)columnBean.get("itemtype");
	                        if("D".equals(itemtype))
	                        {
	                            if(rowSet2.getDate(i+1)!=null)
	                                temp.append("/"+sf.format(rowSet2.getDate(i+1)));
	                            else
	                                temp.append("/");
	                        }
	                        else
	                            temp.append("/"+rowSet2.getString(i+1));
	                    }
	                    
	                    map.put(temp.toString(),"1");
	                }
	            }
	        }
	        catch(Exception e)
	        {
	            e.printStackTrace();
	            throw GeneralExceptionHandler.Handle(e);
	        }
	        finally{
	            PubFunc.closeDbObj(rowSet2);
	        }
	        return map;
	}
	public String validateListData(String[] oppositeItem,LazyDynaBean abean,HashMap salaryItemTypeMap,ArrayList a_dataList)
	{
		String msg="";
		try
		{
			
			for(int j=0;j<oppositeItem.length;j++)
			{
				 String temp=oppositeItem[j];
	             temp = PubFunc.keyWord_reback(temp);
			    String[] temps=temp.split("=");
				String tempValue=((String)abean.get(temps[0].trim())).trim();
				if(tempValue==null||tempValue.trim().length()==0)
					a_dataList.add(null);
				else
				{
					LazyDynaBean columnBean=(LazyDynaBean)salaryItemTypeMap.get(temps[1].trim());
					String decwidth=(String)columnBean.get("decwidth");
					String itemtype=(String)columnBean.get("itemtype");
					String codesetid=(String)columnBean.get("codesetid");
					int itemlength=Integer.parseInt((String)columnBean.get("itemlength"));
					if("N".equals(itemtype))
					{
						if(!isDataType(decwidth,itemtype,tempValue))
						{
							msg="源数据("+temps[0].trim()+")中数据:"+tempValue+" 不符合格式!";
							break;
						}
					}
					else if("D".equals(itemtype))
					{
						if(!isDataType(decwidth,itemtype,tempValue))
						{
							msg="源数据("+temps[0].trim()+")中数据:"+tempValue+" 不符合格式!";
							break;
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
		}
		return msg;
	}
	
	
	/**
	 * 将excel数据导入工资数据表
	 * @param salaryid		 薪资类别号
	 * @param oppositeItem   对应指标关系
	 * @param relationItem	 关联指标关系
	 * @param form_file		 导入文件	
	 * @return
	 * @throws GeneralException
	 */
	public int importFileDataToSalary(String[] oppositeItem,String[] relationItem,FormFile form_file,ArrayList originalDataList,ArrayList updateDateList)throws GeneralException
	{
		int rowNum=0;
		InputStream is =null;
		try
		{
			String updateSql=getBatchUpdateSQL(oppositeItem,relationItem);
			//lis 判断是否updateDateList存在
			if(updateDateList == null || updateDateList.size()==0){
				is = form_file.getInputStream();
				GzExcelBo gzExbo=new GzExcelBo(this.conn);
				gzExbo.getSelfAttribute(is);
	//			int rowNums=gzExbo.getTotalDataRows();   //数据总行数
				int rowNums=gzExbo.getTotalDataRows2();   //数据总行数
				int pageNum=rowNums/100+1;
				LazyDynaBean abean=null;
				HashMap salaryItemTypeMap=getSalaryItemMap();  //薪资项目数据类型 map
				//String updateSql=getBatchUpdateSQL(oppositeItem,relationItem);
				//ArrayList updateDateList=new ArrayList();     //更新数据
				
				String[] ori_item=new String[relationItem.length];
				for(int j=0;j<relationItem.length;j++)
				{
					String temp=relationItem[j];
					/* 薪资发放-导入-未成功导入数据 xiaoyun 2014-9-22 start */
					temp = PubFunc.keyWord_reback(temp);
					/* 薪资发放-导入-未成功导入数据 xiaoyun 2014-9-22 end */
					String[] temps=temp.split("=");
					ori_item[j]=temps[0].trim();
				}
				
				for(int i=1;i<=pageNum;i++)
				{
					int from=(i-1)*100+1;
					int to=i*100;
					if(to>rowNums)
						to=rowNums;
					ArrayList dataList=gzExbo.getDefineData(from,to,originalDataList);
					
					//////////
			/*		for(int j=0;j<dataList.size();j++)
					{
						LazyDynaBean aabean =(LazyDynaBean)dataList.get(j);
						
						CommonData data=null;
						String values="";
						for (short c = 0; c < originalDataList.size(); c++) {
							data = (CommonData) originalDataList.get(c);
							String columnName = data.getDataValue();
							values+=" | "+(String)aabean.get(columnName);
						}
						Category.getInstance("com.hrms.frame.dao.ContentDAO").error(values);
					}*/
					////////////
					
					HashMap existDataMap=getExistDataMap(dataList,relationItem,salaryItemTypeMap);
					
					ArrayList a_dataList=null;
					StringBuffer temp=new StringBuffer("");
					for(int e=0;e<dataList.size();e++)
					{
						abean=(LazyDynaBean)dataList.get(e);
						
						temp.setLength(0);
						for(int f=0;f<ori_item.length;f++)
							temp.append("/"+(String)abean.get(ori_item[f]));
						
					 	if(existDataMap.get(temp.toString())==null)
					  			continue;
						a_dataList=new ArrayList();
						setListData(oppositeItem,abean,salaryItemTypeMap,a_dataList);
						setListData(relationItem,abean,salaryItemTypeMap,a_dataList);
						
						updateDateList.add(a_dataList);
					}
				}
			}
			ContentDAO dao=new ContentDAO(this.conn);
			if(updateSql.length()>0)
			{				
				ArrayList a_updList=new ArrayList();
			    for (int i=0;i<updateDateList.size();i++){			        
			        if (a_updList.size()<1000){
			            a_updList.add((ArrayList)updateDateList.get(i));
			        }
			        else {
			        	a_updList.add((ArrayList)updateDateList.get(i));//此处要加上  否则每1000条会丢一条 zhaoxg add 2016-1-6
			            int[] _rowNums=dao.batchUpdate(updateSql,a_updList);
			            rowNum=rowNum+_rowNums.length;
			            a_updList.clear();
			        }
			    }
			    if (a_updList.size()>0){
			        int[] _rowNums=dao.batchUpdate(updateSql,a_updList);
                    rowNum=rowNum+_rowNums.length;
			    }
				
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeIoResource(is);
		}
		return rowNum;
	}
	
	
	
	public void setListData(String[] oppositeItem,LazyDynaBean abean,HashMap salaryItemTypeMap,ArrayList a_dataList)throws GeneralException
	{
		try
		{
			for(int j=0;j<oppositeItem.length;j++)
			{
				/* 薪资发放-导入失败问题 xiaoyun 2014-9-22 start */
				//String[] temps=oppositeItem[j].split("=");
				String[] temps = PubFunc.keyWord_reback(oppositeItem[j]).split("=");
				/* 薪资发放-导入失败问题 xiaoyun 2014-9-22 end */
				String tempValue=((String)abean.get(temps[0].trim())).trim();
				if(tempValue==null||tempValue.trim().length()==0)
				{
			//		Category.getInstance("com.hrms.frame.dao.ContentDAO").error("null");
					a_dataList.add(null);
				}
				else
				{
					LazyDynaBean columnBean=(LazyDynaBean)salaryItemTypeMap.get(temps[1].trim());
					String decwidth=(String)columnBean.get("decwidth");
					String itemtype=(String)columnBean.get("itemtype");
					String codesetid=(String)columnBean.get("codesetid");
					int itemlength=Integer.parseInt((String)columnBean.get("itemlength"));
					if("N".equals(itemtype))
					{
						String value="";
						if(isDataType(decwidth,itemtype,tempValue))
						{
							if("0".equals(decwidth))
							{
								String a_value=PubFunc.round(tempValue,0);
								a_dataList.add(new Integer(a_value));
								
								value=a_value;
								if(value.length()>itemlength){
									throw GeneralExceptionHandler.Handle(new Exception("源数据("+temps[0]+")中数据:"+tempValue+" 位数超过最大允许值"+itemlength+"位!"));
								}
							}
							else
							{
								String a_value=tempValue;
								a_dataList.add(new Double(a_value));
								
								value=tempValue;
								if(value.split("\\.")[0].length()>itemlength){
									throw GeneralExceptionHandler.Handle(new Exception("源数据("+temps[0]+")中数据:"+tempValue+" 整数位数超过最大允许值"+itemlength+"位!"));
								}
								if(value.split("\\.").length==2&&value.split("\\.")[1].length()>Integer.parseInt(decwidth)){
									throw GeneralExceptionHandler.Handle(new Exception("源数据("+temps[0]+")中数据:"+tempValue+" 小数位数超过最大允许值"+decwidth+"位!"));
								}
							}
							
						}
						else
							throw GeneralExceptionHandler.Handle(new Exception("源数据("+temps[0]+")中数据:"+tempValue+" 不符合格式!"));
					
					//	Category.getInstance("com.hrms.frame.dao.ContentDAO").error("N:decwidth:"+decwidth+"  "+tempValue+" | "+value);
					
					}
					else if("D".equals(itemtype))
					{
						if(isDataType(decwidth,itemtype,tempValue))
						{
							Calendar d=Calendar.getInstance();
							d.set(Calendar.YEAR,Integer.parseInt(tempValue.substring(0,4)));
							d.set(Calendar.MONTH,Integer.parseInt(tempValue.substring(5,7))-1);
							d.set(Calendar.DATE,Integer.parseInt(tempValue.substring(8)));
							java.sql.Date dd=new java.sql.Date(d.getTimeInMillis());
							a_dataList.add(dd);
							//a_dataList.add(d.getTime());
						}
						else
							throw GeneralExceptionHandler.Handle(new Exception("源数据("+temps[0]+")中数据:"+tempValue+" 不符合格式!"));
					}
					else if("A".equals(itemtype)&&!"0".equals(codesetid))
					{
						HashMap valueMap=(HashMap)columnBean.get("options");
						if("un".equalsIgnoreCase(codesetid)||"um".equalsIgnoreCase(codesetid)||"@k".equalsIgnoreCase(codesetid)){
							tempValue = tempValue.split(":")[0];
						}
						if(valueMap.get(tempValue.trim().toLowerCase())!=null)
						{
							a_dataList.add((String)valueMap.get(tempValue.trim().toLowerCase()));
						}
						else
							a_dataList.add("");
					}
					else if("N".equals(itemtype)){
						a_dataList.add(tempValue);
					}else if("M".equals(itemtype)){
						a_dataList.add(tempValue);
					}
					else
					{
					//	Category.getInstance("com.hrms.frame.dao.ContentDAO").error("A:   "+tempValue);
						if(tempValue.getBytes().length<=itemlength)	
							a_dataList.add(tempValue);
						else
							a_dataList.add("");
					}
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
	 * 判断 值类型是否与 要求的类型一致
	 * @param columnBean
	 * @param itemid
	 * @param value
	 * @return
	 */
	public boolean isDataType(String decwidth,String itemtype,String value)
	{
		boolean flag=true;
		if("N".equals(itemtype))
		{
		/*	if(decwidth.equals("0"))
			{
				flag=value.matches("^[+-]?[\\d]+$");
			}
			else*/
			{
				flag=value.matches("^[+-]?[\\d]*[.]?[\\d]+");
			}
			
		}
		else if("D".equals(itemtype))
		{
			flag=value.matches("[0-9]{4}[#-.][0-9]{2}[#-.][0-9]{2}");
		}
		return flag;
	}
	
	
	/** 取得 update语句 */
	public String getBatchUpdateSQL(String[] oppositeItem,String[] relationItem)
	{
	//	StringBuffer sql=new StringBuffer("update "+this.userview.getUserName()+"_salary_"+this.salaryid+" set ");
		StringBuffer sql=new StringBuffer("update "+this.gz_tablename+" set ");
		StringBuffer set=new StringBuffer("");
		int n=0;
		for(int i=0;i<oppositeItem.length;i++)
		{
			/* 薪资发放：新建/导入：设置对应指标页面，点击【保存对应】、【没对应数据】、设置方案后点击【确定】都保存报错 ArrayIndexOutException xiaoyun 2014-9-19 start */
			String[] temps = PubFunc.keyWord_reback(oppositeItem[i]).split("=");
			// String[] temps=oppositeItem[i].split("=");
			/* 薪资发放：新建/导入：设置对应指标页面，点击【保存对应】、【没对应数据】、设置方案后点击【确定】都保存报错 ArrayIndexOutException xiaoyun 2014-9-19 end */

			set.append(","+temps[1].trim()+"=?");
			n++;
		}
		sql.append(set.substring(1));
		sql.append(" where ");
		set.setLength(0);
		for(int i=0;i<relationItem.length;i++)
		{
			/* 薪资发放：新建/导入：设置对应指标页面，点击【保存对应】、【没对应数据】、设置方案后点击【确定】都保存报错 ArrayIndexOutException xiaoyun 2014-9-19 start */
			//String[] temps=relationItem[i].split("=");
			String[] temps = PubFunc.keyWord_reback(relationItem[i]).split("=");
			/* 薪资发放：新建/导入：设置对应指标页面，点击【保存对应】、【没对应数据】、设置方案后点击【确定】都保存报错 ArrayIndexOutException xiaoyun 2014-9-19 end */
			set.append(" and "+temps[1].trim()+"=?");
		}
		sql.append(set.substring(4));
		
		//增加导入修改数据的限制
		sql.append(getImportWhere());
		
		if(n==0)
			sql.setLength(0);
		return sql.toString();
	}
	/**获得导入数据的限制条件*/
	public String getImportWhere()
	{
	    StringBuffer sql = new StringBuffer();
//	  增加导入修改数据的限制
		boolean isShare = true;
		boolean isGZmanager = false;
		boolean isApprove = false;// 是否走审批
		
		if (manager == null || (manager != null && manager.length() == 0))// 不共享
			isShare = false;
		if (this.userview.getUserName().equals(manager))
			isGZmanager = true;
		isApprove = this.isApprove();
		
		   if (isShare)// 薪资发放-共享
		    {
			if (isGZmanager == false)// 薪资发放-共享-非管理员
			{
			    // 要控制人员范围
				
				/*
			    String a_code = this.userview.getManagePrivCode() + this.userview.getManagePrivCodeValue();
			    if (a_code.length() >= 2)
			    {
				String codesetid = a_code.substring(0, 2);
				String value = a_code.substring(2);
				if (codesetid.equalsIgnoreCase("UN"))
				{
				    sql.append(" and (B0110 like '");
				    sql.append(value);
				    sql.append("%'");
				    if (value.equalsIgnoreCase(""))
					sql.append(" or B0110 is null");
				    sql.append(")");
				} else if (codesetid.equalsIgnoreCase("UM"))
				{
				    sql.append(" and E0122 like '");
				    sql.append(value);
				    sql.append("%'");
				}
			    }else if(a_code.trim().length()==0)//没有管理权限
				 sql.append(" and 1=2 ");
			   */
				try
				{
					if("1".equals(getControlByUnitcode()))
					{ 
						String whl_str=getWhlByUnits();
						sql.append(whl_str);
						 
					}
					else  
					{
						
						/**导入数据*/
						String dbpres=getTemplatevo().getString("cbase");
						/**应用库前缀*/
						String[] dbarr=StringUtils.split(dbpres, ",");
						StringBuffer sub_str=new StringBuffer("");
						for(int i=0;i<dbarr.length;i++)
						{
							String pre=dbarr[i];
							if(!this.userview.isSuper_admin()&&this.userview.getDbpriv().toString().toLowerCase().indexOf(","+pre.toLowerCase()+",")==-1)
							{
								sub_str.append(" or (upper("+this.getGz_tablename()+".nbase)='"+pre.toUpperCase()+"'  and 1=2 )");
							}
							else
							{
								sub_str.append(" or (upper("+this.getGz_tablename()+".nbase)='"+pre.toUpperCase()+"'  and a0100 in (select a0100 "+this.userview.getPrivSQLExpression(pre, false)+" ) )");
							}
							
						}
						if(sub_str.length()>0)
						{
							sql.append(" and ( "+sub_str.substring(3)+" )"); 
						}
					}
				}
				catch(Exception e)
				{
					
				}
			    
			    // 只能修改sp_flag2为起草（01）和驳回（07）的记录
			    sql.append(" and sp_flag2 in ('01','07')");
			} else
			// 薪资发放-共享-管理员
			{
			    if (isApprove)// 走审批 只能修改起草和驳回
			    {
				sql.append(" and sp_flag in ('01','07')");
			    }
			}
		    } else
		    // 薪资发放-不共享
		    {
			if (isApprove)// 走审批
			{
			    sql.append(" and sp_flag in ('01','07')");
			}
		    }
		   return sql.toString();
	}
	
	
	/**
	 * 保存导入数据对应方案
	 * @param name 方案名称
	 * @param oppositeItem  对应指标 
	 * @param relationItem  关联指标
	 * @return
	 */
	public boolean saveRelationScheme(String name,String[] oppositeItem,String[] relationItem)throws GeneralException
	{
		boolean flag=false;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			Table table=new Table("gz_relation");
			DbWizard dbWizard=new DbWizard(this.conn);
			if(!dbWizard.isExistTable(table.getName(),false))
			{
				table=getGzRelationTable();
				dbWizard.createTable(table);
				//dbWizard.addPrimaryKey(table);
				DBMetaModel dbmodel=new DBMetaModel(this.conn);
				dbmodel.reloadTableModel("gz_relation");
			}else
			{
				if (!dbWizard.isExistField("gz_relation", "seq"))
				{
					Field temp4=new Field("seq","序号");
					temp4.setDatatype(DataType.INT);
					temp4.setNullable(true);
					temp4.setKeyable(false);	
					table.addField(temp4);	
					dbWizard.addColumns(table);// 更新列
				}
				
			}
			
			StringBuffer rel=new StringBuffer("");
			if(oppositeItem!=null)
			{
				for(int i=0;i<oppositeItem.length;i++)
				{
					rel.append(oppositeItem[i]+",");
				}
			}
			rel.append("|");
			if(relationItem!=null)
			{
				for(int i=0;i<relationItem.length;i++)
				{
					rel.append(","+relationItem[i]+",");
				}
			}			
			
			RecordVo vo=new RecordVo("gz_relation");
			int id=DbNameBo.getPrimaryKey("gz_relation","id",this.conn);  //取得主键值
			vo.setInt("id",id);
			vo.setString("name",name);
			vo.setString("rel",rel.toString());
			vo.setInt("seq",this.getSeq());
			dao.addValueObject(vo);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
	
	/**取得seq*/
	 public int getSeq()
	 {
		ContentDAO dao = new ContentDAO(this.conn);
		StringBuffer strSql = new StringBuffer();
		strSql.append("select ");
		strSql.append(Sql_switcher.isnull("max(seq)","0"));
		strSql.append(" from gz_relation ");
		
		int seq=1;
		try
		{
		    RowSet rs = dao.search(strSql.toString());
		    if(rs.next())
		    	seq = rs.getInt(1)+1;
		} catch (SQLException e)
		{
		    e.printStackTrace();
		}
		return seq;	
	}
	 
	public Table getGzRelationTable()
	{
		Table table=new Table("gz_relation");
		Field temp=new Field("id","方案号");
		temp.setNullable(false);
		temp.setKeyable(true);
		temp.setDatatype(DataType.INT);
	    temp.setSortable(true);		
		table.addField(temp);
		
		
		Field temp2=new Field("name","名称");
		temp2.setNullable(true);
		temp2.setKeyable(false);
		temp2.setDatatype(DataType.STRING);
		temp2.setLength(40);	
		table.addField(temp2);
		
		
		Field temp3=new Field("rel","对应关系");
		temp3.setNullable(true);
		temp3.setKeyable(false);
		temp3.setDatatype(DataType.CLOB);	
		table.addField(temp3);
		
		Field temp4=new Field("seq","序号");
		temp4.setDatatype(DataType.INT);
		temp4.setNullable(true);
		temp4.setKeyable(false);	
		table.addField(temp4);		
		
		return table;
	}
	
	
	//导入数据（关联指标）根据 下拉框的值 得到 对应的下拉框 text列表
	public ArrayList getArrayList(String[] itemList)
	{
 		ArrayList list=new ArrayList();
		if(itemList==null)
			return list;
		CommonData data=null;
		for(int i=0;i<itemList.length;i++)
		{
			String value=this.removeBlank(itemList[i]);
			/* 薪资发放：新建/导入：设置对应指标页面，点击【保存对应】、【没对应数据】、设置方案后点击【确定】都保存报错 ArrayIndexOutException xiaoyun 2014-9-19 start */
			value = PubFunc.keyWord_reback(value);
			/* 薪资发放：新建/导入：设置对应指标页面，点击【保存对应】、【没对应数据】、设置方案后点击【确定】都保存报错 ArrayIndexOutException xiaoyun 2014-9-19 end */
			String[] temps=value.split("=");
			String itemDesc="";
			if("NBASE".equalsIgnoreCase(temps[1].trim()))
				itemDesc="人员库标识";
			else if("A0100".equalsIgnoreCase(temps[1].trim()))
				itemDesc="人员编号";
			else if("A0000".equalsIgnoreCase(temps[1].trim()))
				itemDesc="人员序号";
			else if("A00Z2".equalsIgnoreCase(temps[1].trim()))
				itemDesc="发放日期";
			else if("A00Z3".equalsIgnoreCase(temps[1].trim()))
				itemDesc="发放次数";
			else if("A00Z0".equalsIgnoreCase(temps[1].trim()))
				itemDesc="归属日期";
			else if("A00Z1".equalsIgnoreCase(temps[1].trim()))
				itemDesc="归属次数";
			else 
			{
				FieldItem item=DataDictionary.getFieldItem(temps[1].trim());
				itemDesc=item.getItemdesc();
			}
			
			FieldItem item=DataDictionary.getFieldItem(temps[1].trim());
			String name=temps[0].trim()+"="+itemDesc;
			data=new CommonData(value,name);
			list.add(data);
		}
		return list;
	}
	
	
	public void createSameTable(FormFile form_file,String[] relationItem,ArrayList originalDataList)
	{
		InputStream stream = null;
		try
		{
			stream = form_file.getInputStream();
			GzExcelBo gzExbo=new GzExcelBo(this.conn);
			gzExbo.getSelfAttribute(stream);
			int rowNums=gzExbo.getTotalDataRows();   //数据总行数
			ArrayList dataList=gzExbo.getDefineData(1,rowNums,originalDataList);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			PubFunc.closeIoResource(stream);
		}
		
	}
	
	
	/**
	 * 将导入数据插入临时表中
	 * @param form_file
	 * @param relationItem
	 * @param originalDataList
	 */
	public void  insertTempData(FormFile form_file,String[] relationItem,ArrayList originalDataList)
	{
		InputStream stream = null;
		try
		{
			stream = form_file.getInputStream();
			DbWizard dbWizard=new DbWizard(this.conn);
			DBMetaModel dbmodel=new DBMetaModel(this.conn);
			ContentDAO dao=new ContentDAO(this.conn);
			GzExcelBo gzExbo=new GzExcelBo(this.conn);
			gzExbo.getSelfAttribute(stream);
			
//			int rowNums=gzExbo.getTotalDataRows();   //数据总行数
			int rowNums=gzExbo.getTotalDataRows2();   //数据总行数
			ArrayList dataList=gzExbo.getDefineData(1,rowNums,originalDataList);
			
			Table table=new Table("sameDataTable");
		//	if(dbWizard.isExistTable("sameDataTable", false))
				dbWizard.dropTable("sameDataTable");
			for(int i=0;i<relationItem.length;i++)
			{

				Field atemp=new Field("a"+i,"a"+i);
				atemp.setDatatype(DataType.STRING);
				atemp.setLength(200);
				table.addField(atemp);
			}
			dbWizard.createTable(table);
			dbmodel.reloadTableModel("sameDataTable");
			ArrayList data_list=new ArrayList();
			LazyDynaBean abean=null;
			RecordVo vo=null;
			
			for(int i=0;i<dataList.size();i++)
			{
				vo=new RecordVo("samedatatable");
				abean=(LazyDynaBean)dataList.get(i);
				int temp1 = 0;
				for(int j=0;j<relationItem.length;j++)
				{
					String temp=this.removeBlank(relationItem[j]);
					String[] temps=temp.split("=");
					vo.setString("a"+j,(String)abean.get(temps[0]));
					if(abean.get(temps[0])==null|| "".equals(abean.get(temps[0]))){
						temp1++;
					}
				}
				if(temp1!=relationItem.length)
				data_list.add(vo);
			}
			dao.addValueObject(data_list);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			PubFunc.closeIoResource(stream);
		}
		
	}
	
	
	/**
	 * 得到源数据 中的 同号数据  
	 * @param form_file  源数据文件
	 * @param relationItem  关联指标
	 * @param originalDataList  源数据表头列信息列表
	 * @author dengcan
	 * @return
	 */
	public ArrayList getOriDataList(FormFile form_file,String[] relationItem,ArrayList originalDataList)throws GeneralException
	{
		ArrayList list=new ArrayList();
		try
		{
			insertTempData(form_file,relationItem,originalDataList);
			ContentDAO dao=new ContentDAO(this.conn);
			String select_str="";
			String group_str="";
			for(int j=0;j<relationItem.length;j++)
			{
				select_str+=",a"+j;
				group_str+=",a"+j;
			}
			//System.out.println("select "+select_str.substring(1)+",count(*) acount from samedatatable group by "+group_str.substring(1));
			String s = "select "+select_str.substring(1)+",count(*) acount from samedatatable group by "+group_str.substring(1)+" having count(*)>1";
			RowSet rowSet=dao.search("select "+select_str.substring(1)+",count(*) acount from samedatatable group by "+group_str.substring(1)+" having count(*)>1");
			while(rowSet.next())
			{
				LazyDynaBean abean=new LazyDynaBean();
				for(int j=0;j<relationItem.length;j++)
				{
					String temp=this.removeBlank(relationItem[j]);
					String[] temps=temp.split("=");
					FieldItem fielditem=DataDictionary.getFieldItem(temps[1].toLowerCase());
					String value="";
					if(rowSet.getString("a"+j)!=null)
					{
						value=rowSet.getString("a"+j);
					}
					abean.set("a"+j,value);
				}
				abean.set("acount",rowSet.getString("acount"));
				list.add(abean);
			}
			rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}

	/**
	 * 得到源数据 中的 没对应数据  
	 * @param form_file  源数据文件
	 * @param flag       1：没对应  2：同号数据
	 * @param relationItem  关联指标
	 * @param originalDataList  源数据表头列信息列表
	 * @author dengcan
	 * @return
	 */
	public ArrayList getOriDataList(FormFile form_file,String flag,String[] relationItem,ArrayList originalDataList)throws GeneralException
	{
		ArrayList list=new ArrayList();
		InputStream ism = null;
		try
		{
			ism = form_file.getInputStream();
			GzExcelBo gzExbo=new GzExcelBo(this.conn);
			gzExbo.getSelfAttribute(ism);
			
//			int rowNums=gzExbo.getTotalDataRows();   //数据总行数
			int rowNums=gzExbo.getTotalDataRows2();   //数据总行数
			HashMap salaryItemTypeMap=getSalaryItemMap();  //薪资项目数据类型 map
			int pageNum=rowNums/100+1;
			LazyDynaBean abean=null;
			HashMap map=new HashMap();
			for(int i=1;i<=pageNum;i++)
			{
				int from=(i-1)*100+1;
				int to=i*100;
				if(to>rowNums)
					to=rowNums;
				ArrayList dataList=gzExbo.getDefineData(from,to,originalDataList);
				HashMap existDataMap=getExistDataMap(dataList,relationItem,salaryItemTypeMap);
				
				String[] ori_item=new String[relationItem.length];
				for(int j=0;j<relationItem.length;j++)
				{
					String temp=relationItem[j];
					String[] temps=temp.split("=");
					ori_item[j]=temps[0].trim();
				}
				
				StringBuffer temp=new StringBuffer("");
				for(int j=0;j<dataList.size();j++)
				{
					abean=(LazyDynaBean)dataList.get(j);
					temp.setLength(0);
					for(int e=0;e<ori_item.length;e++)
						temp.append("/"+(String)abean.get(ori_item[e]));
					if("1".equals(flag))  // 1：没对应
					{
						if(existDataMap.get(temp.toString())==null)
							list.add(abean);
					}
					else if("2".equals(flag)) // 2：同号数据
					{
						
						if(map.get(temp.toString())==null)
							map.put(temp.toString(),"1");
						else
							list.add(abean);
						
					}
				}
				
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		finally{
			PubFunc.closeIoResource(ism);
		}
		return list;
	}
	
	/**
	 *  根据关联关系取得 源数据存在于数据库表中的数据 map
	 * @param dataList
	 * @param relationItem
	 * @param originalDataList
	 * @return
	 */
	public HashMap getExistDataMap(ArrayList dataList,String[] relationItem,HashMap salaryItemTypeMap)throws GeneralException
	{
		HashMap map=new HashMap();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String[] ori_item=new String[relationItem.length];
			String[] aim_item=new String[relationItem.length];
			
			StringBuffer select_str=new StringBuffer("");
			
			//HashMap salaryItemTypeMap=getSalaryItemMap();  //薪资项目数据类型 map
			for(int i=0;i<relationItem.length;i++)
			{
				String temp=relationItem[i];
				/* 薪资发放：新建/导入：设置对应指标页面，点击【保存对应】、【没对应数据】、设置方案后点击【确定】都保存报错 ArrayIndexOutException xiaoyun 2014-9-19 start */
				temp = PubFunc.keyWord_reback(temp);
				/* 薪资发放：新建/导入：设置对应指标页面，点击【保存对应】、【没对应数据】、设置方案后点击【确定】都保存报错 ArrayIndexOutException xiaoyun 2014-9-19 end */
				String[] temps=temp.split("=");
				ori_item[i]=temps[0].trim();
				aim_item[i]=temps[1].trim();
				select_str.append(","+aim_item[i].trim());
			}
			StringBuffer whl=new StringBuffer("");
			LazyDynaBean abean=null;
			for(int i=0;i<dataList.size();i++)
			{
				abean=(LazyDynaBean)dataList.get(i);
				StringBuffer temp_whl=new StringBuffer("");
				
				
				for(int j=0;j<relationItem.length;j++)
				{
					LazyDynaBean columnBean=(LazyDynaBean)salaryItemTypeMap.get(aim_item[j].trim());
					String decwidth=(String)columnBean.get("decwidth");
					String itemtype=(String)columnBean.get("itemtype");
					String codesetid = (String)columnBean.get("codesetid");
					if("A".equals(itemtype))
					{
						String x=aim_item[j];
						String y=ori_item[j];
						String z = (String)abean.get(ori_item[j]);
						if("0".equalsIgnoreCase(codesetid)){
							temp_whl.append(" and "+aim_item[j]+"='"+((String)abean.get(ori_item[j])).trim()+"'");
						}else{
							HashMap optionMap = (HashMap) columnBean.get("options");
							String value = ((String)abean.get(ori_item[j])).trim();
							if("un".equalsIgnoreCase(codesetid)||"um".equalsIgnoreCase(codesetid)||"@k".equalsIgnoreCase(codesetid)){
								value = value.split(":")[0].toLowerCase();
							}
							value = (String) (optionMap.get(value)==null?"":optionMap.get(value));
							temp_whl.append(" and "+aim_item[j]+"='"+value+"'");
						}
					}
					if("D".equals(itemtype))
					{
						
						temp_whl.append(" and "+Sql_switcher.dateToChar(aim_item[j],"YYYY-MM-DD")+"='"+((String)abean.get(ori_item[j])).trim()+"'");
					}
					if("N".equals(itemtype))
					{
						String tempValue=((String)abean.get(ori_item[j])).trim();
						if(isDataType(decwidth,itemtype,tempValue))
						{
							temp_whl.append(" and "+aim_item[j]+"="+tempValue);
						}
						else
							throw GeneralExceptionHandler.Handle(new Exception("源数据("+ori_item[j]+")中数据:"+tempValue+" 不符合格式!"));
						
					}
				}
				whl.append(" or ( "+temp_whl.substring(4)+" )");
			}
			
			if(select_str.length()>1&&whl.length()>1)
			{
				String sql="select "+select_str.substring(1)+" from "+this.gz_tablename+" where ( "+whl.substring(3)+" ) ";
				if(manager!=null&&manager.length()>0&&!this.userview.getUserName().equals(manager))
				{
					
					if("1".equals(this.controlByUnitcode))
					{
						String whl_str=getWhlByUnits();
						if(whl_str.length()>0)
						{
							sql+=whl_str;
						}
					}
					else if(!this.userview.isSuper_admin())
					{
						String codesetid=this.userview.getManagePrivCode();
						String value=this.userview.getManagePrivCodeValue();
						StringBuffer buf=new StringBuffer("");
						
						if(codesetid.length()==0)
						{
							buf.append(" and b0110='-1'");
						}
						if("@K".equalsIgnoreCase(codesetid))
						{
							String str=getUnByPosition(value);
							codesetid=str.substring(0,2);
							value=str.substring(2);
						}
						
						if("UN".equalsIgnoreCase(codesetid))
						{
							buf.append(" and (B0110 like '");
							buf.append(value);
							buf.append("%'");	
							if("".equalsIgnoreCase(value))
							{
								buf.append(" or B0110 is null");
								
							}
							buf.append(")");
						}
						if("UM".equalsIgnoreCase(codesetid))
						{
							buf.append(" and E0122 like '");
							buf.append(value);
							buf.append("%'");
						}
						if(buf.length()>0)
						{
							sql+=buf.toString();
						}
					}
				}
				
				RowSet rowSet2=dao.search(sql);
				StringBuffer temp=new StringBuffer("");
				SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
				while(rowSet2.next())
				{
					temp.setLength(0);
					StringBuffer codetemp = new StringBuffer();
					for(int i=0;i<relationItem.length;i++)
					{
						LazyDynaBean columnBean=(LazyDynaBean)salaryItemTypeMap.get(aim_item[i].trim());
						String decwidth=(String)columnBean.get("decwidth");
						String itemtype=(String)columnBean.get("itemtype");
						String codesetid = (String)columnBean.get("codesetid");
						if("D".equals(itemtype))
						{
							if(rowSet2.getDate(i+1)!=null)
								temp.append("/"+sf.format(rowSet2.getDate(i+1)));
							else
								temp.append("/");
						}
						else{
							if("0".equalsIgnoreCase(codesetid)){
								temp.append("/"+rowSet2.getString(i+1));
								codetemp.append("/"+rowSet2.getString(i+1));
							}else{
								temp.append("/"+rowSet2.getString(i+1));
								codetemp.append("/"+rowSet2.getString(i+1)+":"+AdminCode.getCodeName(codesetid,rowSet2.getString(i+1)));
							}
						}
					}
					if(codetemp.length()>0){
						map.put(codetemp.toString(),"1");
					}
					map.put(temp.toString(),"1");
				}
				rowSet2.close();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return map;
	}
	
	
	/**
	 * 取得权限范围内的工资数据
	 * @param salaryid
	 * @param a_code
	 * @return
	 */
	public ArrayList getDataList(String salaryid,String a_code,ArrayList salaryItemList,String condid)
	{
		ArrayList dataList=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			ResultSet rowSet=dao.search(getSql(a_code,"",condid).toString());			
			SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
		//	LazyDynaBean abean=new LazyDynaBean();
			Hashtable abean=null;
			LazyDynaBean bean=new LazyDynaBean();
			Date d=null;

			String itemid="";
			String itemtype="";
			String itemdesc="";
			String codesetid="";
			String decwidth="";
			
			while(rowSet.next())
			{
				
				//abean=new LazyDynaBean();
				abean=new Hashtable();
				for(int i=0;i<salaryItemList.size();i++)
				{
					bean=(LazyDynaBean)salaryItemList.get(i);
					itemid=(String)bean.get("itemid");
					itemtype=(String)bean.get("itemtype");
					itemdesc=(String)bean.get("itemdesc");
					codesetid=(String)bean.get("codesetid");
					decwidth=(String)bean.get("decwidth");
					if("D".equals(itemtype))
					{
						d=null;
						d=rowSet.getDate(itemid);
						if(d!=null)
							abean.put(itemid,df.format(d));
						else
							abean.put(itemid,"");
					}
					else if("M".equals(itemtype))
					{
						abean.put(itemid,Sql_switcher.readMemo(rowSet,itemid));
					}
					else if("A".equals(itemtype)&&!"0".equals(codesetid))
					{
						String value=rowSet.getString(itemid);
						if(value!=null)
						{
							if("um".equalsIgnoreCase(codesetid))//此处加此判断是为了适应潍柴的特殊情况，潍柴会在部门字段里面保存单位的代码值
							{
								String theUM = (AdminCode.getCodeName("UM",value)==null || (AdminCode.getCodeName("UM",value)!=null && AdminCode.getCodeName("UM",value).trim().length()==0))?AdminCode.getCodeName("UN",value): AdminCode.getCodeName("UM",value);
								abean.put(itemid,theUM);
							}else								
								abean.put(itemid,AdminCode.getCodeName(codesetid,value));
						}
						else
						{
							abean.put(itemid,"");
						}
					}
					else if("N".equals(itemtype))
					{
						if(rowSet.getString(itemid)!=null)
						{
							if(decwidth==null||decwidth.length()==0)
								decwidth="0";
							abean.put(itemid,PubFunc.round(rowSet.getString(itemid), Integer.parseInt(decwidth)));
						}
						else
							abean.put(itemid,"");
					}
					else
					{
						if(rowSet.getString(itemid)!=null)
							abean.put(itemid,rowSet.getString(itemid));
						else
							abean.put(itemid,"");
					}
				}
				dataList.add(abean);
			}
			rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return dataList;
	}
	public ArrayList getDataList2(String salaryid,String a_code,ArrayList salaryItemList,String condid,String filterWhl,String order_by)
	{
		ArrayList dataList=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			ResultSet rowSet=dao.search(getSql2(a_code,"",condid,filterWhl,order_by).toString());			
			SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
		//	LazyDynaBean abean=new LazyDynaBean();
			Hashtable abean=null;
			LazyDynaBean bean=new LazyDynaBean();
			Date d=null;

			String itemid="";
			String itemtype="";
			String itemdesc="";
			String codesetid="";
			String decwidth="";
			
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
			String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
			if(display_e0122==null|| "00".equals(display_e0122)|| "".equals(display_e0122))
				display_e0122="0";			
			
			while(rowSet.next())
			{
				
				//abean=new LazyDynaBean();
				abean=new Hashtable();
				for(int i=0;i<salaryItemList.size();i++)
				{
					bean=(LazyDynaBean)salaryItemList.get(i);
					itemid=(String)bean.get("itemid");
					itemtype=(String)bean.get("itemtype");
					itemdesc=(String)bean.get("itemdesc");
					codesetid=(String)bean.get("codesetid");
					decwidth=(String)bean.get("decwidth");
					if("D".equals(itemtype))
					{
						d=null;
						d=rowSet.getDate(itemid);
						if(d!=null)
							abean.put(itemid,df.format(d));
						else
							abean.put(itemid,"");
					}
					else if("M".equals(itemtype))
					{
						abean.put(itemid,Sql_switcher.readMemo(rowSet,itemid));
					}
					else if("A".equals(itemtype)&&!"0".equals(codesetid))
					{
						String value=rowSet.getString(itemid);
						if(value!=null)
						{
							if("UM".equalsIgnoreCase(codesetid))//此处加此判断是为了适应潍柴的特殊情况，潍柴会在部门字段里面保存单位的代码值
							{
								String theUM="";
								if(AdminCode.getCodeName("UM",value)==null || (AdminCode.getCodeName("UM",value)!=null && AdminCode.getCodeName("UM",value).trim().length()==0))
									theUM=AdminCode.getCodeName("UN",value);
//								if(itemid.equalsIgnoreCase("e0122"))
								if(theUM.length()==0)
								{
									if(Integer.parseInt(display_e0122)==0)
										theUM=AdminCode.getCodeName("UM",value);
									else
									{
										CodeItem item=AdminCode.getCode("UM",value,Integer.parseInt(display_e0122));
						    	    	if(item!=null)
						    	    	{
						    	    		theUM=item.getCodename();
						        		}
						    	    	else
						    	    	{
						    	    		theUM=AdminCode.getCodeName("UM",value);
						    	    	}
									}
									
								}
								//else															
								//	theUM = (AdminCode.getCodeName("UM",value)==null || (AdminCode.getCodeName("UM",value)!=null && AdminCode.getCodeName("UM",value).trim().length()==0))?AdminCode.getCodeName("UN",value): AdminCode.getCodeName("UM",value);
								abean.put(itemid,theUM);
							}else								
								abean.put(itemid,AdminCode.getCodeName(codesetid,value));
						}
						else
						{
							abean.put(itemid,"");
						}
					}
					else if("N".equals(itemtype))
					{
						if(rowSet.getString(itemid)!=null)
						{
							if(decwidth==null||decwidth.length()==0)
								decwidth="0";
							abean.put(itemid,PubFunc.round(rowSet.getString(itemid), Integer.parseInt(decwidth)));
						}
						else
							abean.put(itemid,"");
					}
					else
					{
						if(rowSet.getString(itemid)!=null)
							abean.put(itemid,rowSet.getString(itemid));
						else
							abean.put(itemid,"");
					}
				}
				dataList.add(abean);
			}
			rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return dataList;
	}
	
	public ArrayList getDataList3(String salaryid,String a_code,ArrayList salaryItemList,String condid,String filterWhl,String order_by,String sql)
	{
		ArrayList dataList=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			ResultSet rowSet=dao.search(sql);			
			SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
		//	LazyDynaBean abean=new LazyDynaBean();
			Hashtable abean=null;
			LazyDynaBean bean=new LazyDynaBean();
			Date d=null;

			String itemid="";
			String itemtype="";
			String itemdesc="";
			String codesetid="";
			String decwidth="";
			
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
			String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
			if(display_e0122==null|| "00".equals(display_e0122)|| "".equals(display_e0122))
				display_e0122="0";			
			
			while(rowSet.next())
			{
				
				//abean=new LazyDynaBean();
				abean=new Hashtable();
				for(int i=0;i<salaryItemList.size();i++)
				{
					bean=(LazyDynaBean)salaryItemList.get(i);
					itemid=(String)bean.get("itemid");
					itemtype=(String)bean.get("itemtype");
					itemdesc=(String)bean.get("itemdesc");
					codesetid=(String)bean.get("codesetid");
					decwidth=(String)bean.get("decwidth");
					if("D".equals(itemtype))
					{
						d=null;
						d=rowSet.getDate(itemid);
						if(d!=null)
							abean.put(itemid,df.format(d));
						else
							abean.put(itemid,"");
					}
					else if("M".equals(itemtype))
					{
						abean.put(itemid,Sql_switcher.readMemo(rowSet,itemid));
					}
					else if("A".equals(itemtype)&&!"0".equals(codesetid))
					{
						String value=rowSet.getString(itemid);
						if(value!=null)
						{
							if("um".equalsIgnoreCase(codesetid))//此处加此判断是为了适应潍柴的特殊情况，潍柴会在部门字段里面保存单位的代码值
							{
								String theUM="";
								if(AdminCode.getCodeName("UM",value)==null || (AdminCode.getCodeName("UM",value)!=null && AdminCode.getCodeName("UM",value).trim().length()==0))
									theUM=AdminCode.getCodeName("UN",value);								
//								if(itemid.equalsIgnoreCase("e0122"))
								if(theUM.length()==0)
								{
									if(Integer.parseInt(display_e0122)==0)
										theUM=AdminCode.getCodeName("UM",value);
									else
									{
										CodeItem item=AdminCode.getCode("UM",value,Integer.parseInt(display_e0122));
						    	    	if(item!=null)
						    	    	{
						    	    		theUM=item.getCodename();
						        		}
						    	    	else
						    	    	{
						    	    		theUM=AdminCode.getCodeName("UM",value);
						    	    	}
									}								
								}
								//else															
//									theUM = (AdminCode.getCodeName("UM",value)==null || (AdminCode.getCodeName("UM",value)!=null && AdminCode.getCodeName("UM",value).trim().length()==0))?AdminCode.getCodeName("UN",value): AdminCode.getCodeName("UM",value);
								abean.put(itemid,theUM);
							}else								
								abean.put(itemid,AdminCode.getCodeName(codesetid,value));
						}
						else
						{
							abean.put(itemid,"");
						}
					}
					else if("N".equals(itemtype))
					{
						if(rowSet.getString(itemid)!=null)
						{
							if(decwidth==null||decwidth.length()==0)
								decwidth="0";
							abean.put(itemid,PubFunc.round(rowSet.getString(itemid), Integer.parseInt(decwidth)));
						}
						else
							abean.put(itemid,"");
					}
					else
					{
						if(rowSet.getString(itemid)!=null)
							abean.put(itemid,rowSet.getString(itemid));
						else
							abean.put(itemid,"");
					}
				}
				dataList.add(abean);
			}
			rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return dataList;
	}
	public String getSql(String a_code,String itemids,String condid)
	{
		StringBuffer buf=new StringBuffer();
		StringBuffer temp_str=new StringBuffer("");
		if(itemids.length()>0)
		{
			String[] temps=itemids.split("/");
			for(int i=0;i<temps.length;i++)
			{
				if(temps[i]!=null&&temps[i].trim().length()>0)
					temp_str.append(","+temps[i]);
			
			}
		}
		else
			temp_str.append("**");
		buf.append("select "+temp_str.substring(1)+" from ");
		buf.append(getGz_tablename());
		buf.append(",dbname where upper("+getGz_tablename()+".nbase)=upper(dbname.pre) ");
		if(!(a_code==null|| "".equalsIgnoreCase(a_code)))
		{
			String codesetid=a_code.substring(0, 2);
			String value=a_code.substring(2);
			if("UN".equalsIgnoreCase(codesetid))
			{
				buf.append(" and (b0110 like '");
				buf.append(value);
				buf.append("%'");
				if("".equalsIgnoreCase(value))
				{
					buf.append(" or b0110 is null");
				}
				buf.append(")");
			}
			if("UM".equalsIgnoreCase(codesetid))
			{
				buf.append(" and e0122 like '");
				buf.append(value);
				buf.append("%'");
			}
		}
		
		if(manager!=null&&manager.length()>0&&!this.userview.getUserName().equals(manager))
		{
			
			if("1".equals(this.controlByUnitcode))
			{
				String whl_str=getWhlByUnits();
				if(whl_str.length()>0)
				{
					buf.append(whl_str);
				}
			}
			else if(!this.userview.isSuper_admin())
			{
				String codesetid=this.userview.getManagePrivCode();
				String value=this.userview.getManagePrivCodeValue();
				if(codesetid.length()==0)
				{
					buf.append(" and b0110='-1'");
				}
				if("@K".equalsIgnoreCase(codesetid))
				{
					String str=getUnByPosition(value);
					codesetid=str.substring(0,2);
					value=str.substring(2);
				}
				
				
				if("UN".equalsIgnoreCase(codesetid))
				{
					buf.append(" and (b0110 like '");
					buf.append(value);
					buf.append("%'");
					if("".equalsIgnoreCase(value))
					{
						buf.append(" or b0110 is null");
					}
					buf.append(")");
				}
				if("UM".equalsIgnoreCase(codesetid))
				{
					buf.append(" and e0122 like '");
					buf.append(value);
					buf.append("%'");
				}
			}
		}
		
		
		
		
		
		if(condid!=null&&condid.length()>0&&!"new".equals(condid)&&!"all".equals(condid))
		{
			String swhere=getFilterWhere(condid,this.getGz_tablename());
			buf.append(" and ( "+swhere+" )");
		}
		
		
		/**人员排序*/
		
		buf.append(" order by   dbname.dbid,a0000, A00Z0, A00Z1");
		return buf.toString();
	}
	public String getSql2(String a_code,String itemids,String condid,String filterWhl,String orderby)
	{
		StringBuffer buf=new StringBuffer();
		StringBuffer temp_str=new StringBuffer("");
		if(itemids.length()>0)
		{
			String[] temps=itemids.split("/");
			for(int i=0;i<temps.length;i++)
			{
				if(temps[i]!=null&&temps[i].trim().length()>0)
					temp_str.append(","+temps[i]);
			
			}
		}
		else
			temp_str.append("**");
		buf.append("select "+temp_str.substring(1)+" from ");
		buf.append(getGz_tablename());
		buf.append(",dbname where upper("+getGz_tablename()+".nbase)=upper(dbname.pre) ");
		if(!(a_code==null|| "".equalsIgnoreCase(a_code)))
		{
			String codesetid=a_code.substring(0, 2);
			String value=a_code.substring(2);
			if("UN".equalsIgnoreCase(codesetid))
			{
				
				if("1".equals(this.controlByUnitcode()))
				{
					String orgid = getCtrlparam().getValue(SalaryCtrlParamBo.SUM_FIELD,"orgid");
					orgid = orgid != null ? orgid : "";
					String deptid = getCtrlparam().getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid");
					deptid = deptid != null ? deptid : "";
					StringBuffer tempBuf=new StringBuffer("");
					tempBuf.append("");
					if(orgid.length()>0)
					{
						tempBuf.append(" or "+orgid+" like '"+value+"%' ");
					}
					if(deptid.length()>0)
					{
						tempBuf.append(" or "+deptid+" like '"+value+"%' ");
					}
					if(tempBuf.length()>0)
					{
						buf.append(" and ( "+tempBuf.substring(3)+" )"); 
					}
				}
				else
				{
					buf.append(" and (b0110 like '");
					buf.append(value);
					buf.append("%'");
					if("".equalsIgnoreCase(value))
					{
						buf.append(" or b0110 is null");
					}
					buf.append(")");
				}
			}
			if("UM".equalsIgnoreCase(codesetid))
			{
				
				if("1".equals(this.controlByUnitcode()))
				{
					String deptid = getCtrlparam().getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid");
					deptid = deptid != null ? deptid : "";
					StringBuffer tempBuf=new StringBuffer("");
					tempBuf.append("");
					if(deptid.length()>0)
					{
						tempBuf.append(" or "+deptid+" like '"+value+"%' ");
					}
					if(tempBuf.length()>0)
					{
						buf.append(" and ( "+tempBuf.substring(3)+" )"); 
					}
				}
				else
				{
					buf.append(" and e0122 like '");
					buf.append(value);
					buf.append("%'");
				}
			}
		}
		 
		try
		{
			if("1".equals(this.controlByUnitcode()))
			{
				 
				String whl_str=getWhlByUnits();
				buf.append(whl_str);
				 
			}
			else if(manager!=null&&manager.length()>0&&!manager.equalsIgnoreCase(this.userview.getUserName())&&!this.userview.isSuper_admin())
			{
				
				/**导入数据*/
				String dbpres=getTemplatevo().getString("cbase");
				/**应用库前缀*/
				String[] dbarr=StringUtils.split(dbpres, ",");
				StringBuffer sub_str=new StringBuffer("");
				for(int i=0;i<dbarr.length;i++)
				{
					String pre=dbarr[i];
					if(!this.userview.isSuper_admin()&&this.userview.getDbpriv().toString().toLowerCase().indexOf(","+pre.toLowerCase()+",")==-1)
					{
						sub_str.append(" or (upper("+getGz_tablename()+".nbase)='"+pre.toUpperCase()+"'  and 1=2 )");
					}
					else
					{
						sub_str.append(" or (upper("+getGz_tablename()+".nbase)='"+pre.toUpperCase()+"'  and a0100 in (select a0100 "+this.userview.getPrivSQLExpression(pre, false)+" ) )");
					}
					
				}
				if(sub_str.length()>0)
				{
					buf.append(" and ( "+sub_str.substring(3)+" )");
					
				}
			}
		}
		catch(Exception e)
		{
			
		}
		/*
		if(manager!=null&&manager.length()>0&&!this.userview.getUserName().equals(manager)&&!this.userview.isSuper_admin())
		{
			String codesetid=this.userview.getManagePrivCode();
			String value=this.userview.getManagePrivCodeValue();
			if(codesetid.length()==0)
			{
				buf.append(" and b0110='-1'");
			}
			if(codesetid.equalsIgnoreCase("@K"))
			{
				String str=getUnByPosition(value);
				codesetid=str.substring(0,2);
				value=str.substring(2);
			}
			
			
			if(codesetid.equalsIgnoreCase("UN"))
			{
				buf.append(" and (b0110 like '");
				buf.append(value);
				buf.append("%'");
				if(value.equalsIgnoreCase(""))
				{
					buf.append(" or b0110 is null");
				}
				buf.append(")");
			}
			if(codesetid.equalsIgnoreCase("UM"))
			{
				buf.append(" and e0122 like '");
				buf.append(value);
				buf.append("%'");
			}
		}
		
		*/
		
		
		
		
		
		
		
		
		
		
		if(condid!=null&&condid.length()>0&&!"new".equals(condid)&&!"all".equals(condid))
		{
			String swhere=getFilterWhere(condid,this.getGz_tablename());
			buf.append(" and ( "+swhere+" )");
		}else if(condid!=null&&condid.length()>0&& "new".equals(condid))
		{			
			buf.append(filterWhl);
		}
		
		
		/**人员排序*/
		if(orderby!=null&&orderby.trim().length()>0){
			orderby = orderby.replaceAll("dbid", "dbname.dbid");
			buf.append(" order by "+orderby);
		}else
		{
			String order_str=this.getCtrlparam().getValue(SalaryCtrlParamBo.DEFAULT_ORDER,this.userview);
			if(order_str!=null&&order_str.trim().length()>0){
				order_str = order_str.replaceAll("dbid", "dbname.dbid");
				buf.append(" order by "+order_str);
			}else
				buf.append(" order by   dbname.dbid,a0000, A00Z0, A00Z1");
		}
		return buf.toString();
	}
	
	/**
	 * 导出文件
	 * @param salaryid
	 * @param flag 1：excel  2：text
	 * @param a_code 
	 * @return
	 */
	public String exportFile(String salaryid,String flag,String a_code,String itemids,String condid)throws GeneralException
	{
		String fileName="";
		FileOutputStream fileOut = null;
		try
		{
			ArrayList salaryItemList=getSalaryItemList();
			Date d=new Date();
			if("1".equals(flag))
			{
				ArrayList dataList=getDataList(salaryid,a_code,salaryItemList,condid);
				ArrayList list=new ArrayList();
				for(int i=0;i<salaryItemList.size();i++ )
				{
					LazyDynaBean abean=(LazyDynaBean)salaryItemList.get(i);
					String itemid=((String)abean.get("itemid")).toUpperCase();
					if(itemids.indexOf("/"+itemid+"/")!=-1)
						list.add(abean);
				}
				
				fileName="salaryData"+d.getTime()+PubFunc.getStrg()+".xls";
				GzExcelBo bo=new GzExcelBo(this.conn);
				bo.exportGzData(fileName,dataList,list);
			}
			else
			{
				RowSetToXmlBuilder builder=new RowSetToXmlBuilder(this.conn);
				fileName="salaryData"+d.getTime()+PubFunc.getStrg()+".xml";
				fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+fileName);
				ContentDAO dao=new ContentDAO(this.conn);
				String sql_str=getSql(a_code,itemids,condid);
				RowSet rowSet=dao.search(sql_str);
				fileOut.write((builder.outPutXml2(rowSet,this.gz_tablename,itemids)).getBytes());
				//fileOut.close();
				rowSet.close();

			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		finally{
		    PubFunc.closeIoResource(fileOut);
		}
		return fileName;
	}
	/**
	 * 导出文件 为了与界面的字段顺序一致 所以修改了exportFile（）方法
	 * @param salaryid
	 * @param flag 1：excel  2：text
	 * @param a_code 
	 * @return
	 */
	public String exportFile2(String salaryid,String flag,String a_code,String itemids,String condid,String filterWhl,String order_by)throws GeneralException
	{
		String fileName="";
		try
		{
			ArrayList salaryItemList2=getSalaryItemList();
			HashMap salaryItemMap = new HashMap();
			for(int i=0;i<salaryItemList2.size();i++ )
			{
				LazyDynaBean abean=(LazyDynaBean)salaryItemList2.get(i);
				String itemid=((String)abean.get("itemid")).toUpperCase();
				salaryItemMap.put(itemid, abean);
			}
			
			LazyDynaBean bean = new LazyDynaBean();
			bean.set("itemid", "appprocess");
			bean.set("itemdesc", "审批意见");
			bean.set("itemtype", "M");
			salaryItemMap.put("APPPROCESS", bean);
			
			ArrayList salaryItemList3=this.getFieldlist();//与界面的字段顺序一致	
			
			ArrayList salaryItemList = new ArrayList(); //放置排好序的列
			for(int i=0;i<salaryItemList3.size();i++ )
			{
				Field field=(Field)salaryItemList3.get(i);
				String itemid=field.getName().toUpperCase();					
				
				if(salaryItemMap.get(itemid)!=null)
					salaryItemList.add(salaryItemMap.get(itemid));
			}		
			
			Date d=new Date();
			if("1".equals(flag))
			{
				ArrayList dataList=getDataList2(salaryid,a_code,salaryItemList,condid,filterWhl,order_by);
				if(dataList.size()==0)
				{
					throw GeneralExceptionHandler.Handle(new Exception("无数据导出!"));
				}
				
				ArrayList list=new ArrayList();
				for(int i=0;i<salaryItemList.size();i++ )
				{
					LazyDynaBean abean=(LazyDynaBean)salaryItemList.get(i);
					String itemid=((String)abean.get("itemid")).toUpperCase();
					if(itemids.indexOf("/"+itemid+"/")!=-1)
						list.add(abean);
				}
				
				
				fileName=this.userview.getUserName()+"_"+PubFunc.getStrg()+".xls";
				GzExcelBo bo=new GzExcelBo(this.conn);
				bo.exportGzData(fileName,dataList,list);
			}
			else
			{
				RowSetToXmlBuilder builder=new RowSetToXmlBuilder(this.conn);
				fileName=this.userview.getUserName()+"_"+PubFunc.getStrg()+".xml";
				FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+fileName);
				RowSet rowSet =null;
				try {					
					ContentDAO dao=new ContentDAO(this.conn);
					String sql_str=getSql(a_code,itemids,condid);
					rowSet=dao.search(sql_str);
					fileOut.write((builder.outPutXml2(rowSet,this.gz_tablename,itemids)).getBytes());
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					PubFunc.closeIoResource(fileOut);
					PubFunc.closeIoResource(rowSet);
				}
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return fileName;
	}
	
	public String exportFile3(String salaryid,String flag,String a_code,String itemids,String condid,String filterWhl,String order_by,String sql)throws GeneralException
	{
		String fileName="";
		try
		{
			ArrayList salaryItemList2=getSalaryItemList();
			HashMap salaryItemMap = new HashMap();
			for(int i=0;i<salaryItemList2.size();i++ )
			{
				LazyDynaBean abean=(LazyDynaBean)salaryItemList2.get(i);
				String itemid=((String)abean.get("itemid")).toUpperCase();
				salaryItemMap.put(itemid, abean);
			}
			
			LazyDynaBean bean = new LazyDynaBean();
			bean.set("itemid", "appprocess");
			bean.set("itemdesc", "审批意见");
			bean.set("itemtype", "M");
			salaryItemMap.put("APPPROCESS", bean);
			
			ArrayList salaryItemList3=this.getFieldlist();//与界面的字段顺序一致	
			
			ArrayList salaryItemList = new ArrayList(); //放置排好序的列
			for(int i=0;i<salaryItemList3.size();i++ )
			{
				Field field=(Field)salaryItemList3.get(i);
				String itemid=field.getName().toUpperCase();					
				
				if(salaryItemMap.get(itemid)!=null)
					salaryItemList.add(salaryItemMap.get(itemid));
			}		
			
			Date d=new Date();
			if("1".equals(flag))
			{
				ArrayList dataList=getDataList3(salaryid,a_code,salaryItemList,condid,filterWhl,order_by,sql);
				ArrayList list=new ArrayList();
				for(int i=0;i<salaryItemList.size();i++ )
				{
					LazyDynaBean abean=(LazyDynaBean)salaryItemList.get(i);
					String itemid=((String)abean.get("itemid")).toUpperCase();
					if(itemids.indexOf("/"+itemid+"/")!=-1)
						list.add(abean);
				}
				
				if(dataList.size()==0)
				{
					throw GeneralExceptionHandler.Handle(new Exception("无数据导出!"));
				}
				
				fileName=this.userview.getUserName()+"_"+PubFunc.getStrg()+".xls";
				GzExcelBo bo=new GzExcelBo(this.conn);
				bo.exportGzData(fileName,dataList,list);
			}
			else
			{
				RowSetToXmlBuilder builder=new RowSetToXmlBuilder(this.conn);
				fileName=this.userview.getUserName()+"_"+PubFunc.getStrg()+".xml";
				FileOutputStream fileOut = null;
				RowSet rowSet = null;
				try {					
					fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+fileName);
					ContentDAO dao=new ContentDAO(this.conn);
//					String sql_str=getSql(a_code,itemids,condid);
					rowSet=dao.search(sql);
					fileOut.write((builder.outPutXml2(rowSet,this.gz_tablename,itemids)).getBytes());
				}finally{
					PubFunc.closeIoResource(fileOut);
				}
				rowSet.close();

			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return fileName;
	}
	/**
	 * 取得导入文件中列指标列表
	 * @param form_file
	 * @author dengcan
	 * @return
	 */
	public ArrayList getOriginalDataFiledList(FormFile form_file)throws GeneralException
	{
		ArrayList list=new ArrayList();
		InputStream stream = null;
		try
		{
			stream = form_file.getInputStream();
			GzExcelBo gebo=new GzExcelBo(this.conn);
			gebo.getSelfAttribute(stream);
			list=gebo.getRowAllInfo(0);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeIoResource(stream);
		}
		return list;
	}
	
	/**
	 * 取得 薪资类别中的系统项薪资项目列表
	 * @return
	 */
	public HashMap getSystemItems()
	{
	    HashMap map = new HashMap();
	    String sql = "select * from salaryset where salaryid=" + this.salaryid + " and Initflag=3";
	    ContentDAO dao = new ContentDAO(this.conn);
	    try
	    {
		RowSet rset = dao.search(sql);
		while (rset.next())
		{
		    String itemid = rset.getString("itemid");
		    map.put(itemid.toLowerCase(), rset.getString("itemdesc"));
		}
	    } catch (SQLException e)
	    {
		e.printStackTrace();
	    }		    
	    return map;	    
	}
	
	
	
	/**
	 * 取得 薪资类别中的薪资项目列表
	 * @return
	 */
	public ArrayList getAimDataFieldList()
	{
		ArrayList list=new ArrayList();
		ArrayList salaryItemList=getSalaryItemList();
//		HashMap map = this.getSystemItems();
		for(int i=0;i<salaryItemList.size();i++)
		{
			LazyDynaBean abean=(LazyDynaBean)salaryItemList.get(i);
			String itemid=(String)abean.get("itemid");
//			  if (map.get(itemid.toLowerCase()) != null)
//				continue;
			  if("a0100".equalsIgnoreCase(itemid) || "a0000".equalsIgnoreCase(itemid))
				continue;
		//	if(manager!=null&&manager.length()>0&&!this.userview.getUserName().equals(manager))
			if(!this.userview.isSuper_admin())
			{
				if(!"3".equals(abean.get("initflag")))
				{
					if("0".equalsIgnoreCase(this.userview.analyseFieldPriv(itemid)))
						continue;
				}
			}
			String itemdesc=(String)abean.get("itemdesc");
			String itemtype=(String)abean.get("itemtype");
			String typeDesc="字符";
			if("N".equalsIgnoreCase(itemtype))
				typeDesc="数值";
			if("D".equalsIgnoreCase(itemtype))
				typeDesc="日期";
			String flag=(String)abean.get("initflag");
			
			if(manager.length()==0||manager.equalsIgnoreCase(this.userview.getUserName()))
			{
				list.add(new CommonData(itemid,itemid+"       "+itemdesc+"       ( "+typeDesc+" )"));
			}
			else
			{
				if("3".equals(flag))
				{
					list.add(new CommonData(itemid,itemid+"       "+itemdesc+"       ( "+typeDesc+" )"));
				}
				else if(!"3".equals(flag)&&!"0".equals(this.userview.analyseFieldPriv(itemid)))
				{
					list.add(new CommonData(itemid,itemid+"       "+itemdesc+"       ( "+typeDesc+" )"));
				}
			}
			
				
		}
		return list;
	}
	
	
	/**
	 * 将流写入文件
	 * @param stream
	 */
	public String writeFile(InputStream stream)
	{
		String fileName = "importGzData_"+PubFunc.getStrg()+".xls";
	        File newFile = new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")
	    			+fileName);
	        FileOutputStream outPutStream = null;
	        try {	          
	            outPutStream = new FileOutputStream(newFile);
	            byte[] byteArr = new byte[512];
	            while (stream.read(byteArr) > 0) {
	                outPutStream.write(byteArr);
	                outPutStream.flush();
	            }
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        }finally{
	        	PubFunc.closeIoResource(outPutStream);
	        }
	        return fileName;
	}
	/**当导入excel文件为.xlsx格式时候用上面的方法就打不开了所以改为下面的Row row = sheet.getRow(0);方法*/
	public String writeFile2(InputStream stream)
	{
		
		HSSFWorkbook nwb = new HSSFWorkbook(); // 创建新的Excel 工作簿
		HSSFSheet nsheet = nwb.createSheet();
		
		Workbook owb = null;
		Sheet osheet = null;
		FileOutputStream fileOut = null;
		String fileName = "importGzData_"+PubFunc.getStrg()+".xls";
		try
		{
		 owb = WorkbookFactory.create(stream);
		 osheet =owb.getSheetAt(0);
		Row orow = osheet.getRow(0);
		int cols = orow.getPhysicalNumberOfCells();
		int rows = osheet.getPhysicalNumberOfRows();
		
		for (int j = 0; j < rows; j++)
		{
		    orow = osheet.getRow(j);
		    HSSFRow nrow = nsheet.createRow(j);
		    if (orow != null)
		    {
				for (short c = 0; c < cols; c++) 
				{
					String value = "";
					Cell cell = orow.getCell(c);
					HSSFCell ncell = nrow.createCell(c);
					if (cell != null) 
					{
						switch (cell.getCellType()) 
						{
							case Cell.CELL_TYPE_FORMULA:							   
								break;
							case Cell.CELL_TYPE_NUMERIC:
								ncell.setCellValue(cell.getNumericCellValue());								
								break;
							case Cell.CELL_TYPE_STRING:
								ncell.setCellValue(cell.getStringCellValue());	
								break;
							default:
								ncell.setCellValue("");	
						}
					}
					
				}
		    }
		}
		    fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + fileName);
		    nwb.write(fileOut);
		} catch (Exception e)
		{
		    e.printStackTrace();
		} finally{
			PubFunc.closeResource(owb);
			PubFunc.closeResource(nwb);
			PubFunc.closeIoResource(fileOut);
		}
	   return fileName;
	}
	
	/**
	 * 取得薪资类别的人员过滤条件
	 * @return
	 */
	public ArrayList getManFilterList()
	{
//		if(condlist==null)
			condlist=searchManFilter();
		return condlist;
	}
	
	public ArrayList getFieldlist() {
		return fieldlist;
	}

	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}


	public String getGz_tablename() {
		return gz_tablename;
	}

	public void setGz_tablename(String gz_tablename) {
		this.gz_tablename = gz_tablename;
	}

	public int getSalaryid() {
		return salaryid;
	}

	public void setSalaryid(int salaryid) {
		this.salaryid = salaryid;
	}

	public RecordVo getTemplatevo() {
		return templatevo;
	}

	public void setTemplatevo(RecordVo templatevo) {
		this.templatevo = templatevo;
	}
	
	
	/**FengXiBin add*/
	/**
	 * 取库前缀
	 * @param salaryid
	 * @param time
	 * @return
	 */
	public ArrayList getnbase(String salaryid,String time)
	{
		RowSet rs;
		ArrayList retlist = new ArrayList();
		StringBuffer sqlsb = new StringBuffer();
		sqlsb.append(" select distinct NBase FROM SalaryHistory ");
		sqlsb.append("where SalaryHistory.salaryid = "+salaryid+"");
		sqlsb.append(" and SalaryHistory.A00Z2 =");
		sqlsb.append(Sql_switcher.dateValue(time));
//		System.out.println(sqlsb.toString());
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sqlsb.toString());
			while(rs.next())
			{
				String nbase = rs.getString("NBase");
				retlist.add(nbase);
			}
			rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return retlist;
	}
	/**FengXiBin add*/
	/**
	 * 取库前缀
	 * @param salaryid
	 * @param time
	 * @return
	 */
	public ArrayList getnbase(String salaryid)
	{
		RowSet rs;
		ArrayList retlist = new ArrayList();
		StringBuffer sqlsb = new StringBuffer();
		sqlsb.append(" select distinct NBase FROM SalaryHistory ");
		sqlsb.append("where SalaryHistory.salaryid = "+salaryid+"");
//		System.out.println(sqlsb.toString());
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sqlsb.toString());
			while(rs.next())
			{
				String nbase = rs.getString("NBase");
				retlist.add(nbase);
			}
			rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return retlist;
	}
	public ArrayList getGz_Nbase(String username,String salaryid)
	{
		RowSet rs;
		ArrayList retlist = new ArrayList();
		StringBuffer sqlsb = new StringBuffer();
	//	sqlsb.append(" select distinct NBase FROM "+username+"_salary_"+salaryid);
		sqlsb.append(" select distinct NBase FROM "+this.gz_tablename);
		//		System.out.println(sqlsb.toString());
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sqlsb.toString());
			while(rs.next())
			{
				String nbase = rs.getString("NBase");
				retlist.add(nbase);
			}
			rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return retlist;
	}
	/**FengXiBin add*/
	/**
	 * 更新薪资历史表中的人员，实现人员同步
	 * @param nbaselist
	 * @param salaryid
	 * @param time
	 */
	public void syncGzSpEmp(String salaryid,String time)
	{
		ArrayList nbaselist = this.getnbase(salaryid, time);
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			if(nbaselist != null)
			{
				for(int i=0;i<nbaselist.size();i++)
				{
					String nbase = (String)nbaselist.get(i);
					StringBuffer sqlsb = new StringBuffer();
					sqlsb.append(" UPDATE SalaryHistory SET  ");
					sqlsb.append(" SalaryHistory.A0000=( select "+nbase+"A01.A0000 ");
					sqlsb.append("FROM "+nbase+"A01 WHERE ");
					sqlsb.append("SalaryHistory.A0100="+nbase+"A01.A0100 )");
					sqlsb.append(" where upper(SalaryHistory.NBase)='"+nbase.toUpperCase()+"'");
					sqlsb.append(" AND SalaryHistory.salaryid ="+salaryid);
					sqlsb.append(" and SalaryHistory.A00Z2 = ");
					sqlsb.append(Sql_switcher.dateValue(time));
//					System.out.println(sqlsb.toString());
					dao.update(sqlsb.toString());
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	public void syncGzEmp(String username,String salaryid)
	{
		ArrayList nbaselist = this.getGz_Nbase(username,salaryid);
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			if(nbaselist != null)
			{
				for(int i=0;i<nbaselist.size();i++)
				{
					String nbase = (String)nbaselist.get(i);
					StringBuffer sqlsb = new StringBuffer();
					sqlsb.append(" UPDATE "+this.gz_tablename+" SET  ");
					sqlsb.append(this.gz_tablename+".A0000=( select "+nbase+"A01.A0000 ");
					sqlsb.append("FROM "+nbase+"A01 WHERE ");
					sqlsb.append(this.gz_tablename+".A0100="+nbase+"A01.A0100 )");
					sqlsb.append(" where upper("+this.gz_tablename+".NBase)='"+nbase.toUpperCase()+"'");
					sqlsb.append(" and "+this.gz_tablename+".a0100 in ");
					sqlsb.append(" (select "+nbase+"A01.a0100 from "+nbase+"A01)");
//					System.out.println(sqlsb.toString());
					dao.update(sqlsb.toString());
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public String getorderbystr(String sort_fields) 
	{
		StringBuffer fieldsb = new StringBuffer();
		if(sort_fields!=null)
		{
			String[] temps = sort_fields.split("`");
			for(int i=0;i<temps.length;i++)
			{
				String[] arr = temps[i].split(":");
				String sortmode = "";
				if(arr.length>=2)
				{
		    		if("1".equalsIgnoreCase(arr[2]))
		    		{
		    			sortmode = "asc";
		    		}else{
		    			sortmode = "desc";
		    		}
				}
				fieldsb.append(","+arr[0]+" "+sortmode+" ");
			}
		}
		return fieldsb.substring(1).toString();
	}
	public void sortemp(String sort_fields,String username,String salaryid)
	{
		//现在增加了默认排序，所以这个重新数据顺序 就没什么用啦,反而会跟默认排序造成冲突
		/*
		RowSet rs;
		ContentDAO dao = new ContentDAO(this.conn);
		StringBuffer sqlsb = new StringBuffer();
		try
		{
				String field = this.getorderbystr(sort_fields);
				sqlsb.append(" select "+this.gz_tablename+".* ");
				sqlsb.append(" from "+this.gz_tablename+",dbname where upper("+this.gz_tablename+".nbase)=upper(dbname.pre) ");
				sqlsb.append(" order by "+field);				

				rs = dao.search(sqlsb.toString());
				int i=1;
				
				StringBuffer update_str=new StringBuffer("update "+this.gz_tablename+" set a0000=? where upper(nbase) =? ");
				update_str.append(" and A0100 =? and "+Sql_switcher.year("A00Z0")+"=? AND "+Sql_switcher.month("A00Z0")+"=? AND A00Z1=?");
				PreparedStatement pt=this.conn.prepareStatement(update_str.toString());
				while(rs.next())
				{
					String A0000 = rs.getString("A0000");
					String nbase = rs.getString("nbase");
					String A0100 = rs.getString("A0100");
					String A00Z0 = rs.getString("A00Z0");
					Date d=rs.getDate("A00Z0");
					String A00Z1 = rs.getString("A00Z1");
				
					Calendar dd=Calendar.getInstance();
					dd.setTime(d);
					pt.setInt(1,i*10);
					pt.setString(2,nbase.toUpperCase());
					pt.setString(3, A0100);
					pt.setInt(4,dd.get(Calendar.YEAR));
					pt.setInt(5,dd.get(Calendar.MONTH)+1);
					pt.setInt(6, Integer.parseInt(A00Z1));
					pt.addBatch();
					i++;
					
				}
				pt.executeBatch();
				pt.close();
				rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}*/
	}
	/**
	 * 根据定义的项目过滤字符串，进行项目过滤
	 * @param list     薪资类别中的全部指标
	 * @param fields   项目过滤字符串
	 * @return
	 */
	public ArrayList filterItemsList(ArrayList list,String fields)
	{
		ArrayList filterlist=new ArrayList();
		
		try
		{
			StringBuffer strread=new StringBuffer();
			/**只读字段*/
			strread.append("NBASE,A0100,A00Z0,A00Z1,");			
			if(fields==null)
				fields="";
			fields="NBASE,A0100,A00Z0,A00Z1,"+fields;//这四项传到前台做主键，zhaoxg add
			for(int i=0;i<list.size();i++)
			{
				Field field=(Field)list.get(i);
				String fieldname=field.getName();
				if(fields.indexOf(fieldname)==-1)
				{
					//if(strread.indexOf(fieldname)!=-1)
						field.setVisible(false);
					//else
						continue;//放开continue，解决刚建完项目过滤后还受显示隐藏指标控制的问题，zhaoxg 2013-12-10
				}

				filterlist.add(field);
			}//for i loop end.
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return filterlist;		
	}
	/**
	 * 
	 * @param str
	 * @return
	 */
	public String splitStr(String str)
	{
		String ret = "";
		String[] arr = str.split("\\.");
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<arr.length;i++)
		{
			ret = arr[i];
			if(i!=arr.length-1)
			{
				if(arr[i].lastIndexOf("(")!=-1)
				{
					arr[i]=arr[i].substring(0,arr[i].lastIndexOf("(")+1);
					sb.append(arr[i]+" ");
				}else if(arr[i].lastIndexOf(" OR ")!=-1 || arr[i].lastIndexOf(" or ")!=-1 )
				{
					if(arr[i].lastIndexOf(" OR ")!=-1 )
					{
						arr[i]=arr[i].substring(0,arr[i].lastIndexOf(" OR "));
						sb.append(arr[i]+" OR ");
					}else
					{
						arr[i]=arr[i].substring(0,arr[i].lastIndexOf(" or "));
						sb.append(arr[i]+" or ");
					}
					
				}
			}else
			{
				sb.append(arr[i]);
			}
		}
		ret = sb.toString();
		return ret;
	}
	public String getFiltersIds(String salaryid)
	{
		String ret_str = "";
		try
		{
			BankDiskSetBo bo = new BankDiskSetBo(this.conn);
			String xml=bo.getCondXML(salaryid);
			SalaryLProgramBo sLPBo = new SalaryLProgramBo(xml);
			ret_str = sLPBo.getValue(SalaryLProgramBo.FILTERS);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return ret_str;
	}



	public String getErrorInfo() {
		return errorInfo;
	}



	public void setErrorInfo(String errorInfo) {
		this.errorInfo = errorInfo;
	}
	public ArrayList getField(int type){
		String rightvalue = getvalue(String.valueOf(salaryid),type);
		ArrayList list = new ArrayList();
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("select itemid,itemdesc,itemlength,decwidth,codesetid,itemtype");
		sqlstr.append(" from salaryset where UPPER(itemid) in('");
		sqlstr.append(rightvalue.toUpperCase().replaceAll(",","','"));
		sqlstr.append("') and salaryid ="+salaryid);
		
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sqlstr.toString());
			while(rs.next()){
				FieldItem fielditem = new FieldItem();
				fielditem.setItemid(rs.getString("itemid"));
				fielditem.setItemdesc(rs.getString("itemdesc"));
				fielditem.setItemlength(rs.getInt("itemlength"));
				fielditem.setDecimalwidth(rs.getInt("decwidth"));
				fielditem.setCodesetid(rs.getString("codesetid"));
				fielditem.setItemtype(rs.getString("itemtype"));
				list.add(fielditem);
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return list;
	}
	public HashMap getSetItemList(String rightvalue,String filterid,String fieldstr)
	{
		HashMap map = new HashMap();
		try
		{
			StringBuffer  buf = new StringBuffer();
			/*sqlstr.append("select itemid,itemdesc,itemlength,decwidth,codesetid,itemtype");
			sqlstr.append(" from salaryset where itemid in('");
			sqlstr.append(rightvalue.replaceAll(",","','"));
			sqlstr.append("') and salaryid ="+salaryid);*/
			/* 薪资发放-变动对比-岗位名称没有比对出来的问题 xiaoyun 2014-10-8 start */
			buf.append("select a.itemid,a.itemdesc,a.itemlength,a.decwidth,a.codesetid,");
			buf.append("a.itemtype,b.fieldsetid from salaryset a left join fielditem b on UPPER(a.itemid)=UPPER(b.itemid) where UPPER(a.itemid) in ('");
			buf.append(rightvalue.toUpperCase().replaceAll(",","','")+"') and a.salaryid="+salaryid);
			/* 薪资发放-变动对比-岗位名称没有比对出来的问题 xiaoyun 2014-10-8 end */
			buf.append(" order by b.fieldsetid");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(buf.toString());
			while(rs.next())
			{
				/* 薪资发放-变动对比-岗位名称没有比对出来的问题 xiaoyun 2014-10-8 start */
				String fieldsetid = rs.getString("fieldsetid");
				/* 薪资发放-变动对比-岗位名称没有比对出来的问题 xiaoyun 2014-10-8 end */
				if(!"null".equalsIgnoreCase(filterid)&&!"all".equalsIgnoreCase(filterid)&&!"dbname".equalsIgnoreCase(rs.getString("itemid"))&&!"b0110".equalsIgnoreCase(rs.getString("itemid"))
						&&!"e0122".equalsIgnoreCase(rs.getString("itemid"))&&!"a0101".equalsIgnoreCase(rs.getString("itemid"))
						&&!"state".equalsIgnoreCase(rs.getString("itemid"))&&!"a0100".equalsIgnoreCase(rs.getString("itemid")))
				{
					if(fieldstr.toUpperCase().indexOf(rs.getString("itemid").toUpperCase())==-1)
						continue;
				}
				/* 薪资发放-变动对比-岗位名称没有比对出来的问题 xiaoyun 2014-10-8 start */
				if(fieldsetid==null && !"e01a1".equalsIgnoreCase(rs.getString("itemid"))){
					continue;
				}
				if("e01a1".equalsIgnoreCase(rs.getString("itemid"))){
					fieldsetid = "A01";
				}
				/* 薪资发放-变动对比-岗位名称没有比对出来的问题 xiaoyun 2014-10-8 end */
				
				FieldItem fielditem = new FieldItem();
				fielditem.setItemid(rs.getString("itemid"));
				fielditem.setItemdesc(rs.getString("itemdesc"));
				fielditem.setItemlength(rs.getInt("itemlength"));
				fielditem.setDecimalwidth(rs.getInt("decwidth"));
				fielditem.setCodesetid(rs.getString("codesetid"));
				fielditem.setItemtype(rs.getString("itemtype"));
				fielditem.setFieldsetid(fieldsetid);
				if(map.get(fieldsetid.toUpperCase())==null)
				{
					ArrayList alist = new ArrayList();
					alist.add(fielditem);
					map.put(fieldsetid.toUpperCase(), alist);
				}
				else
				{
					ArrayList alist = (ArrayList)map.get(fieldsetid.toUpperCase());
					alist.add(fielditem);
					map.put(fieldsetid.toUpperCase(), alist);
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
	public String getvalue(String salaryid,int type){
		SalaryCtrlParamBo ctrl_par = new SalaryCtrlParamBo(this.conn,Integer.parseInt(salaryid));
		return ctrl_par.getValue(type);
	}
	
	
	/**
	 * 得到变动比对 表头信息
	 * @param opt /add  del  change  stop
	 * @return
	 */
	public ArrayList getChangeInfoHeadList(String opt)
	{
		ArrayList list=new ArrayList();
		list.add(new CommonData("serial","序号"));
		if("add".equals(opt)|| "del".equals(opt)|| "stop".equals(opt))
		{
			list.add(new CommonData("nbase","人员库"));
			list.add(new CommonData("b0110","单位名称"));
			list.add(new CommonData("e0122","部门"));
			if("stop".equals(opt))
			{
				list.add(new CommonData("stop","停发标识"));
			}
			list.add(new CommonData("a0101","姓名"));
			if(this.add_delList.size()>0){
				for(int i=0;i<this.add_delList.size();i++){
					FieldItem _item =(FieldItem)add_delList.get(i);
					if(_item!=null){
						list.add(new CommonData(_item.getItemid(),_item.getItemdesc()));
					}
				}
			}
		}
		else if("change".equals(opt))
		{
			list.add(new CommonData("nbase","人员库"));
			list.add(new CommonData("b0110","原单位"));
			 list.add(new CommonData("b01101","现单位"));
			list.add(new CommonData("e0122","原部门"));
			list.add(new CommonData("e01221","现部门"));
			list.add(new CommonData("a0101","原姓名"));
			list.add(new CommonData("a01011","现姓名"));
			
		}
		return list;
	}
	
	
	/**
	 * 得到变动比对 信息
	 * @param opt
	 * @param salaryid
	 * @param username
	 * @return
	 */
	public ArrayList getChangeInfoDataList(String opt,String salaryid,String username,String fieldstr,String filterid)
	{
		ArrayList list=new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{

			int index = 0;
			String sql = "";
			if ("add".equals(opt)) // 新增人员
				sql = "select * from t#" + username + "_gz_Ins order by DBNAME,A0000,b0110,e0122";
			else if ("del".equals(opt)) // 减少人员
				sql = "select * from t#" + username + "_gz_Dec order by DBNAME,A0000,b0110,e0122";
			else if ("stop".equals(opt)) // 停发人员
				sql = "select * from t#" + username + "_gz_Tf order by DBNAME,A0000,b0110,e0122";
			else if ("change".equals(opt)) // 单位部门姓名信息变动人员
				sql = "select * from t#" + username + "_gz_Bd order by DBNAME,A0000,b0110,e0122";

			RowSet rowSet = dao.search(sql);
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
			String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
			if(display_e0122==null|| "00".equals(display_e0122)|| "".equals(display_e0122))
				display_e0122="0";
			LazyDynaBean abean = null;
			FieldItem onlyitem=null;
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			if(this.getOnlyField()!=null&&!"".equals(this.getOnlyField()))
			{
			    onlyitem=DataDictionary.getFieldItem(this.getOnlyField().toLowerCase());
			}
			while (rowSet.next()) {
				index++;
				abean = new LazyDynaBean();
				abean.set("serial", String.valueOf(index));
				abean.set("nbase", AdminCode.getCodeName("@@", rowSet.getString("dbname")));
				if(!"change".equals(opt))
				{
	    			abean.set("b0110", AdminCode.getCodeName("UN", rowSet.getString("b0110")));
	    			CodeItem ct=AdminCode.getCode("UM", rowSet.getString("e0122"),Integer.parseInt(display_e0122));
			     	abean.set("e0122", ct!=null?ct.getCodename():AdminCode.getCodeName("UM", rowSet.getString("e0122")));
			    	abean.set("a0101", rowSet.getString("a0101")!=null?rowSet.getString("a0101"):"");
					if(this.add_delList.size()>0){
						for(int i=0;i<this.add_delList.size();i++){
							FieldItem _item =(FieldItem)add_delList.get(i);
							if(_item!=null){
								if(!"0".equals(_item.getCodesetid())){
									abean.set(_item.getItemid(), AdminCode.getCodeName(_item.getCodesetid(), rowSet.getString(_item.getItemid())));
								}else if("A".equalsIgnoreCase(_item.getItemtype())){
									abean.set(_item.getItemid(), rowSet.getString(_item.getItemid())!=null?rowSet.getString(_item.getItemid()):"");
								}else if("D".equalsIgnoreCase(_item.getItemtype())){
									abean.set(_item.getItemid(), rowSet.getDate(_item.getItemid())!=null?format.format(rowSet.getDate(_item.getItemid())):"");
								}else{
									abean.set(_item.getItemid(), rowSet.getString(_item.getItemid())!=null?rowSet.getString(_item.getItemid()):"");
								}
								
								//list.add(new CommonData(_item.getItemid(),_item.getItemdesc()));
							}
						}
					}
				}
				else
				{
					abean.set("b01101", AdminCode.getCodeName("UN", rowSet.getString("b0110")));
					CodeItem ct=AdminCode.getCode("UM", rowSet.getString("e0122"),Integer.parseInt(display_e0122));
			     	abean.set("e01221", ct!=null?ct.getCodename():AdminCode.getCodeName("UM", rowSet.getString("e0122")));
			     	//abean.set("e01221", AdminCode.getCodeName("UM", rowSet.getString("e0122")));
			    	abean.set("a01011", rowSet.getString("a0101")!=null?rowSet.getString("a0101"):"");
				}
				
				if ("stop".equals(opt))
					abean.set("stop", AdminCode.getCodeName("ZZ", rowSet.getString("a01z0")));
				if("change".equals(opt))
				{
					abean.set("b0110", AdminCode.getCodeName("UN", rowSet.getString("b01101")));
					CodeItem ct=AdminCode.getCode("UM", rowSet.getString("e01221"),Integer.parseInt(display_e0122));
			     	abean.set("e0122", ct!=null?ct.getCodename():AdminCode.getCodeName("UM", rowSet.getString("e01221")));
					//abean.set("e0122", AdminCode.getCodeName("UM", rowSet.getString("e01221")));
					abean.set("a0101", rowSet.getString("a01011")!=null?rowSet.getString("a01011"):"");
					ArrayList f_compare_field=this.getField(SalaryCtrlParamBo.COMPARE_FIELD);
				    for(int i=0;i<f_compare_field.size();i++)
				    {
				    	FieldItem item = (FieldItem)f_compare_field.get(i);
				    	if(!"null".equalsIgnoreCase(filterid)&&!"all".equalsIgnoreCase(filterid)&&!"dbname".equalsIgnoreCase(item.getItemid())&&!"b0110".equalsIgnoreCase(item.getItemid())
								&&!"e0122".equalsIgnoreCase(item.getItemid())&&!"a0101".equalsIgnoreCase(item.getItemid())
								&&!"state".equalsIgnoreCase(item.getItemid())&&!"a0100".equalsIgnoreCase(item.getItemid()))
						{
							if(fieldstr.toUpperCase().indexOf(item.getItemid().toUpperCase())==-1)
								continue;
						}
				    	String itemtype=item.getItemtype();
				    	String itemid=item.getItemid();
				    	String codesetid=item.getCodesetid();
				    	if("N".equalsIgnoreCase(itemtype))
				    	{
				    		abean.set(itemid, rowSet.getString(itemid)!=null?rowSet.getString(itemid):"");
				    		abean.set(itemid+"1", rowSet.getString(itemid+"1")!=null?rowSet.getString(itemid+"1"):"");
				    	}
				    	else if("D".equalsIgnoreCase(itemtype))
				    	{
				    		abean.set(itemid, rowSet.getDate(itemid)!=null?format.format(rowSet.getDate(itemid)):"");
				    		abean.set(itemid+"1", rowSet.getDate(itemid+"1")!=null?format.format(rowSet.getDate(itemid+"1")):"");
				    	}
				    	else
				    	{
				    		if(!"0".equals(codesetid))
				    		{
				    			abean.set(itemid, AdminCode.getCodeName(codesetid, rowSet.getString(itemid)));
				    			abean.set(itemid+"1", AdminCode.getCodeName(codesetid, rowSet.getString(itemid+"1")));
				    		}
				    		else
				    		{
				    			abean.set(itemid, rowSet.getString(itemid)!=null?rowSet.getString(itemid):"");
					    		abean.set(itemid+"1", rowSet.getString(itemid+"1")!=null?rowSet.getString(itemid+"1"):"");
				    		}
				    	}
				    }
				}
                 if(onlyitem!=null)
                 {
                	 if("N".equalsIgnoreCase(onlyitem.getItemtype()))
				    {
				    	abean.set(onlyitem.getItemid(), rowSet.getString(onlyitem.getItemid())!=null?rowSet.getString(onlyitem.getItemid()):"");
				    }
                	else if("D".equalsIgnoreCase(onlyitem.getItemtype()))
                	{
                		 abean.set(onlyitem.getItemid(), rowSet.getDate(onlyitem.getItemid())!=null?format.format(rowSet.getDate(onlyitem.getItemid())):"");
                	}else if(!"0".equals(onlyitem.getCodesetid()))
                	{
                		abean.set(onlyitem.getItemid(), AdminCode.getCodeName(onlyitem.getCodesetid(), rowSet.getString(onlyitem.getItemid())));
                	}
                	else{
                		abean.set(onlyitem.getItemid(), rowSet.getString(onlyitem.getItemid())!=null?rowSet.getString(onlyitem.getItemid()):"");
                	}
                 }
				list.add(abean);
			}
			rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	

	public String createExcel(String opt,ArrayList headList,ArrayList dataList,String filterid,String fieldstr,UserView view)
	{
		
		String outputFile ="changeInfo_"+PubFunc.getStrg()+".xls"; // PubFunc.getStrg() +".xls";
		HSSFWorkbook workbook = null;
		FileOutputStream fileOut = null;
		try
		{
			String name="";
			if("add".equals(opt))
				name="新增人员名单";
			else if("del".equals(opt))
				name="减少人员名单";
			else if("stop".equals(opt))
				name="停发人员名单";
			else if("change".equals(opt))
				name="信息变动人员名单";
			workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet();
			workbook.setSheetName(0, name);
			HSSFRow row=null;
			HSSFCell csCell=null;
			short n=0;
			FieldItem onlyitem=null;
			if(this.getOnlyField()!=null&&!"".equals(this.getOnlyField()))
    		{
    			 onlyitem=DataDictionary.getFieldItem(this.getOnlyField().toLowerCase());
    		}
			row = sheet.createRow(0);	
			if(!"change".equals(opt))
			{
	    		for(short i=0;i<headList.size();i++)
	    		{
	    			CommonData d=(CommonData)headList.get(i);
		    		csCell =row.createCell(i);
		    		
		    		csCell.setCellValue(d.getDataName());
	    		}
	    		if(onlyitem!=null)
	    		{
	    			csCell =row.createCell(headList.size());
			    	csCell.setCellValue(onlyitem.getItemdesc());
	    		}
	    		for(int i=0;i<dataList.size();i++)
				{
					LazyDynaBean abean=(LazyDynaBean)dataList.get(i);
					row = sheet.getRow(i+1);	
					if(row==null)
						row = sheet.createRow(i+1);	
					for(short j=0;j<headList.size();j++)
					{
						CommonData d=(CommonData)headList.get(j);
						csCell =row.createCell(j);
						if("serial".equalsIgnoreCase(d.getDataValue())){
							csCell.setCellValue((String)abean.get(d.getDataValue()));
						}else{
							FieldItem item = DataDictionary.getFieldItem(d.getDataValue());		
						    if("N".equalsIgnoreCase(item.getItemtype()))
						    {
						    	String value=(String)abean.get(d.getDataValue());
						    	if(value==null|| "".equals(value))
						    		value="0";
						    	csCell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						    	csCell.setCellValue(Double.parseDouble(value));
						    }
						    else
						    {
						    	csCell.setCellValue((String)abean.get(d.getDataValue()));
						    }
						}					
					}
					if(onlyitem!=null)
					{
						csCell =row.createCell(headList.size());
						csCell.setCellValue((String)abean.get(onlyitem.getItemid()));
					}
				}
	    		
			}
			else
			{
				KhTemplateBo bo = new KhTemplateBo();
				HSSFCellStyle title=bo.style(workbook, 5);
	    		ArrayList f_compare_field=this.getField(SalaryCtrlParamBo.COMPARE_FIELD);
	    		csCell =row.createCell((short)0);
	    		
	    		csCell.setCellStyle(title);
	      		csCell.setCellValue("序号");
	    		ExportExcelUtil.mergeCell(sheet, 0, (short)0, 1, (short)(0));
			
		    	csCell =row.createCell((short)1);
		    	
		    	csCell.setCellStyle(title);
		    	csCell.setCellValue("人员库");
		    	ExportExcelUtil.mergeCell(sheet, 0, (short)1, 1, (short)(1));
			
		    	csCell =row.createCell((short)2);
		    	
		    	csCell.setCellStyle(title);
		    	csCell.setCellValue("单位");
		    	ExportExcelUtil.mergeCell(sheet, 0, (short)2, 0, (short)(3));
			
		    	csCell =row.createCell((short)4);
		    	
		    	csCell.setCellStyle(title);
		    	csCell.setCellValue("部门");
		    	ExportExcelUtil.mergeCell(sheet, 0, (short)4, 0, (short)(5));
			
		    	csCell =row.createCell((short)6);
			    
			    csCell.setCellStyle(title);
			    csCell.setCellValue("姓名");
			    ExportExcelUtil.mergeCell(sheet, 0, (short)6, 0, (short)(7));
			    for(int size=0;size<f_compare_field.size();size++)
			    {
			    	FieldItem item =(FieldItem)f_compare_field.get(size);
			    	if(!"null".equalsIgnoreCase(filterid)&&!"all".equalsIgnoreCase(filterid)&&!"dbname".equalsIgnoreCase(item.getItemid())&&!"b0110".equalsIgnoreCase(item.getItemid())
							&&!"e0122".equalsIgnoreCase(item.getItemid())&&!"a0101".equalsIgnoreCase(item.getItemid())
							&&!"state".equalsIgnoreCase(item.getItemid())&&!"a0100".equalsIgnoreCase(item.getItemid()))
					{
						if(fieldstr.toUpperCase().indexOf(item.getItemid().toUpperCase())==-1)
							continue;
					}
			    	csCell =row.createCell((short)(6+(size+1)*2));
			    	csCell.setCellStyle(title);
				    
				    csCell.setCellValue(item.getItemdesc());
				    ExportExcelUtil.mergeCell(sheet, 0, (short)(6+(size+1)*2), 0, (short)(7+(size+1)*2));
			    }
			    if(onlyitem!=null)
		      	 {
                    csCell =row.createCell((short)(f_compare_field.size()*2+8));
		    		 csCell.setCellStyle(title);
		      		 csCell.setCellValue(onlyitem.getItemdesc());
		      		 ExportExcelUtil.mergeCell(sheet, 0, (short)(f_compare_field.size()*2+8), 1, (short)(f_compare_field.size()*2+8));
		      	 }
			    row = sheet.getRow(1);
			    if(row==null)
			    	 row = sheet.createRow(1);
			    csCell =row.createCell((short)2);
	    		
	    		csCell.setCellStyle(title);
	      		csCell.setCellValue("原单位");
				csCell =row.createCell((short)3);
		    	
		    	csCell.setCellStyle(title);
		      	csCell.setCellValue("现单位");
		      	
		      	csCell =row.createCell((short)4);
	    		
	    		csCell.setCellStyle(title);
	      		csCell.setCellValue("原部门");
				csCell =row.createCell((short)5);
		    	
		    	csCell.setCellStyle(title);
		      	csCell.setCellValue("现部门");
		      	
		      	csCell =row.createCell((short)6);
	    		
	    		csCell.setCellStyle(title);
	      		csCell.setCellValue("原姓名");
				csCell =row.createCell((short)7);
		    	
		    	csCell.setCellStyle(title);
		      	csCell.setCellValue("现姓名");
		      	 for(int size=0;size<f_compare_field.size();size++)
				 {
		      		
		      		 FieldItem item = (FieldItem)f_compare_field.get(size);
		      		if(!"null".equalsIgnoreCase(filterid)&&!"all".equalsIgnoreCase(filterid)&&!"dbname".equalsIgnoreCase(item.getItemid())&&!"b0110".equalsIgnoreCase(item.getItemid())
							&&!"e0122".equalsIgnoreCase(item.getItemid())&&!"a0101".equalsIgnoreCase(item.getItemid())
							&&!"state".equalsIgnoreCase(item.getItemid())&&!"a0100".equalsIgnoreCase(item.getItemid()))
					{
						if(fieldstr.toUpperCase().indexOf(item.getItemid().toUpperCase())==-1)
							continue;
					}
		      		 csCell =row.createCell((short)(size*2+8));
		    		 
		    		 csCell.setCellStyle(title);
		      		 csCell.setCellValue("原"+item.getItemdesc());
		      		 
					 csCell =row.createCell((short)(size*2+9));
			    	 
			    	 csCell.setCellStyle(title);
			      	 csCell.setCellValue("现"+item.getItemdesc());
				 }
		      	 
		      	for(int i=0;i<dataList.size();i++)
				{
					LazyDynaBean abean=(LazyDynaBean)dataList.get(i);
					row = sheet.createRow(i+2);	
					for(short j=0;j<headList.size();j++)
					{
						CommonData d=(CommonData)headList.get(j);
						csCell =row.createCell(j);
								
						csCell.setCellValue((String)abean.get(d.getDataValue()));
					}
					for(int size=0;size<f_compare_field.size();size++)
				    {
						
				    	FieldItem item =(FieldItem)f_compare_field.get(size);
				    	if(!"null".equalsIgnoreCase(filterid)&&!"all".equalsIgnoreCase(filterid)&&!"dbname".equalsIgnoreCase(item.getItemid())&&!"b0110".equalsIgnoreCase(item.getItemid())
								&&!"e0122".equalsIgnoreCase(item.getItemid())&&!"a0101".equalsIgnoreCase(item.getItemid())
								&&!"state".equalsIgnoreCase(item.getItemid())&&!"a0100".equalsIgnoreCase(item.getItemid()))
						{
							if(fieldstr.toUpperCase().indexOf(item.getItemid().toUpperCase())==-1)
								continue;
						}
				    	csCell =row.createCell((short)(headList.size()+size*2));
					    
					    if("N".equalsIgnoreCase(item.getItemtype()))
					    {
					    	String value=(String)abean.get(item.getItemid());
					    	if(value==null|| "".equals(value))
					    		value="0";
					    	csCell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					    	csCell.setCellValue(Double.parseDouble(value));
					    }
					    else
					    {
				    	    csCell.setCellValue((String)abean.get(item.getItemid()));
					    }
					    csCell =row.createCell((short)(headList.size()+size*2+1));
					    
					    if("N".equalsIgnoreCase(item.getItemtype()))
					    {
					    	String value=(String)abean.get(item.getItemid()+"1");
					    	if(value==null|| "".equals(value))
					    		value="0";
					    	csCell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					    	csCell.setCellValue(Double.parseDouble(value));
					    }
					    else
					    {
				    	    csCell.setCellValue((String)abean.get(item.getItemid()+"1"));
					    }
				    }
					if(onlyitem!=null)
					{
						csCell=row.createCell(f_compare_field.size()*2+8);
						csCell.setCellValue((String)abean.get(onlyitem.getItemid()));
					}
				}
		      	int columnSize=8+f_compare_field.size()*2;
		      	if(onlyitem!=null)
		      		columnSize++;
		      	for (int i = 1; i <=columnSize; i++)
				{
					sheet.setColumnWidth(Short.parseShort(String.valueOf(i)),(short)5000);
				}
				for (int i = 0; i <=dataList.size()+2; i++)
				{
				    row = sheet.getRow(i);
				    if(row==null)
				    	row = sheet.createRow(i);
				    row.setHeight((short) 400);
				}
		      	 
			}
			
			fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+outputFile);
			workbook.write(fileOut);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(fileOut);
			PubFunc.closeResource(workbook);
		}
		return outputFile;
	}
	/**
	 * 删除薪资临时表中的临时变量
	 * @param templist 要删除的临时变量　（list中包含FieldItem）
	 */
	public void dropCloumTemp(ArrayList templist){
		try {
			DbWizard dbw=new DbWizard(this.conn);
			RecordVo vo=new RecordVo(this.gz_tablename);
			if(dbw.isExistTable(gz_tablename, false)){
				Table table=new Table(this.gz_tablename);
				boolean bflag=false;
				for(int i=0;i<templist.size();i++){
					FieldItem item=(FieldItem)templist.get(i);
					String fieldname=item.getItemid();
					/**变量如果未加，则构建*/
					if(vo.hasAttribute(fieldname.toLowerCase())){
						Field field=item.cloneField();
						table.addField(field);
						bflag=true;
					}
				}
				if(bflag){
					dbw.dropColumns(table);
				}
			}
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 删除薪资临时表中的临时变量
	 * @param templist 要删除的临时变量　（list中包含FieldItem）
	 */
	public void dropCloumTemp(String tableName,ArrayList templist){
		try {
			DbWizard dbw=new DbWizard(this.conn);
			RecordVo vo=new RecordVo(tableName);
			if(dbw.isExistTable(tableName, false)){
				Table table=new Table(tableName);
				boolean bflag=false;
				for(int i=0;i<templist.size();i++){
					FieldItem item=(FieldItem)templist.get(i);
					String fieldname=item.getItemid();
					/**变量如果未加，则构建*/
					if(vo.hasAttribute(fieldname.toLowerCase())){
						Field field=item.cloneField();
						table.addField(field);
						bflag=true;
					}
				}
				if(bflag){
					dbw.dropColumns(table);
				}
			}
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 同步推送待办表   zhaoxg add 2014-7-25
	 * @param conn
	 * @param userview
	 * @param receiver 接收者名
	 * @param salaryid
	 * @param flag    1:报批  2：驳回  3：批准  4：阅读
	 * @param bean   发放日期、次数   待办模块 名称 等信息
	 */
	synchronized static  public LazyDynaBean updatePendingTask(Connection conn,UserView userview,String receiver,String salaryid,LazyDynaBean bean,String flag) {
		LazyDynaBean _bean=new LazyDynaBean();
		if (receiver == null || receiver.length() ==0)
			return _bean;
		
		RowSet rs = null;
		try {
			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
			String date = df.format(calendar.getTime());

			String sender = userview.getUserName();

			ContentDAO dao = new ContentDAO(conn);
			StringBuffer sql=new StringBuffer();
			String name=bean.get("year")+"年"+bean.get("month")+"月"+bean.get("count")+"次  "+bean.get("name");//待办名  “2014年06月1次 月度奖金（薪资）”
			String ext_flag="GZSP_"+bean.get("year")+bean.get("month")+bean.get("count")+"_"+salaryid;

			HashMap map=isHavePendingtask(receiver,conn,ext_flag);
			String pending_type="";
			String str2="";

				pending_type= "0".equals(bean.get("cstate"))?"34":"39";
				if("0".equals(bean.get("cstate"))){
					str2="&gz_module=0";
				}else{
					str2="&gz_module=1";
				}

			String tempreceiver=receiver;
			if(receiver.indexOf("#")!=-1){
				String[] _receiver=receiver.split("#");
				tempreceiver=_receiver[0];
			}
			if ((map==null||map.size()==0)&&("1".equals(flag)|| "2".equals(flag))) {//在待办任务表中新增待办数据
				IDGenerator idg = new IDGenerator(2, conn);
				String url="";
				SalaryCtrlParamBo ctrl_par = new SalaryCtrlParamBo(conn,Integer.parseInt(salaryid));
				String collectPoint = ctrl_par.getValue(SalaryCtrlParamBo.SUM_FIELD, "collect_field");
//				if(collectPoint!=null&&collectPoint.length()>0){
//					collectPoint = "/gz/gz_sp_collect/gz_sp_collect.do";
//				}else{
					collectPoint = "/gz/gz_accounting/gz_sp_orgtree.do";
//				}
				if("1".equals(flag)){
					url=""+collectPoint+"?b_query=link&fromModel=wdxx&a00z2="+bean.get("a00z2")+"&zjjt=1&a00z3="+bean.get("count")+"&ori=1&salaryid="+salaryid;
				}else if("2".equals(flag)){
					String[] _receiver=receiver.split("#");
					if(_receiver[0].equals(_receiver[1])){//驳回到发起人了
						url="/gz/gz_accounting/gz_org_tree.do?b_query=link&ff_bosdate="+bean.get("a00z2")+"&zjjt=1&ff_count="+bean.get("count")+str2+"&salaryid="+salaryid;
					}else{
						url=""+collectPoint+"?b_query=link&fromModel=wdxx&a00z2="+bean.get("a00z2")+"&zjjt=1&a00z3="+bean.get("count")+"&ori=1&salaryid="+salaryid;
					}		
					receiver=_receiver[0];
				}
				String pending_id = idg.getId("pengdingTask.pengding_id");
				RecordVo vo = new RecordVo("t_hr_pendingtask");
				vo.setString("pending_id", pending_id);
				vo.setDate("create_time", date);
				vo.setDate("lasttime", date);
				vo.setString("sender", sender);
				vo.setString("pending_type", pending_type);
				vo.setString("pending_title",name);
				vo.setString("pending_url", url);
				vo.setString("pending_status", "0");
				vo.setString("pending_level", "1");
				vo.setInt("bread", 0);
				vo.setString("receiver", receiver);
				vo.setString("ext_flag", ext_flag);
				dao.addValueObject(vo);
				_bean.set("receiver", receiver);
				_bean.set("pending_id", pending_id.replaceAll("^(0+)", ""));//去掉前面的0，因为入库以后是int型的
				_bean.set("flag", "add");
				_bean.set("url", url);
			} else if ((tempreceiver.equals(map.get("receiver")))&&("1".equals(flag)|| "2".equals(flag))) {//在待办任务表中存在对应的待办数据但状态不是待办

				sql.delete(0, sql.length());
				sql.append("update t_hr_pendingtask set Pending_status='0',bread='0',Lasttime="+Sql_switcher.dateValue(date)+",pending_title='"+name+"',");
				sql.append(" sender='" + sender + "'");
				sql.append(" where Pending_type='"+pending_type+"'");
				sql.append(" and Receiver='" + tempreceiver + "'");
				sql.append(" and pending_id="+map.get("pending_id")+"");
				dao.update(sql.toString());
				_bean.set("pending_id", map.get("pending_id"));
				_bean.set("flag", "update");
			} else if("4".equals(flag)){//改成已阅
				sql.delete(0, sql.length());
				if("0".equals(map.get("bread"))){
					sql.append("update t_hr_pendingtask set bread='1',Lasttime="+Sql_switcher.dateValue(date)+" where ");
					sql.append(" Pending_status='0' and pending_id="+map.get("pending_id")+"");
					dao.update(sql.toString());
					_bean.set("pending_id", map.get("pending_id"));
					_bean.set("flag", "update");
				}else{
					_bean.set("flag", "xxxx");//如果是已阅就不调外部接口
				}
			}
			
			HashMap _map=isHavePendingtask(sender,conn,ext_flag);
			if(_map!=null&&"0".equals(_map.get("pending_status"))&&bean.get("sql")!=null&&bean.get("sql").toString().length()>0){//如果是驳回再报，那么自己肯定也有条对应待办   改成已办
				RowSet _rs=dao.search((String) bean.get("sql"));
				if(!_rs.next()){
					sql.delete(0, sql.length());
					sql.append("update t_hr_pendingtask set Pending_status='1',Lasttime="+Sql_switcher.dateValue(date)+"");
					sql.append(" where Pending_type='"+pending_type+"'");
					sql.append(" and Receiver='" + sender + "'");
					sql.append(" and Pending_status='0' and pending_id="+_map.get("pending_id")+"");
					dao.update(sql.toString());
					_bean.set("selfpending_id", _map.get("pending_id"));
					_bean.set("selfflag", "update");
				}			
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return _bean;
	}
	/**
	 * 判断是否已有待办，且返回待办状态   zhaoxg add 2014-7-25
	 * @param receiver 接收者名
	 * @param conn
	 * @param ext_flag  扩展标记  用来区分已有待办
	 * @return
	 */
	public static HashMap isHavePendingtask(String receiver,Connection conn,String ext_flag){
		HashMap map=new HashMap();
		try{
			if(receiver.indexOf("#")!=-1){
				String[] _receiver=receiver.split("#");
				receiver=_receiver[0];
			}
			String withNoLock="";
			if(Sql_switcher.searchDbServer()!=2) //针对SQLSERVER 无需考虑锁表
				withNoLock=" WITH(NOLOCK) ";
			ContentDAO dao = new ContentDAO(conn);
			String sql="select pending_id,ext_flag,pending_status,bread,receiver from t_hr_pendingtask "+withNoLock+" where (Pending_type='34' or Pending_type='39') and receiver='"+receiver+"' and pending_status<>1 and ext_flag='"+ext_flag+"'";
			RowSet rs=dao.search(sql);
			while(rs.next()){
				map.put("pending_status", rs.getString("pending_status"));//待办处理状态
				map.put("bread", rs.getString("bread"));//是否阅读
				map.put("receiver", rs.getString("receiver"));//接收者账号
				map.put("pending_id", rs.getString("pending_id"));//主键
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 取得 工资数据表 的当前 发放时间 次数 薪资（保险）类别名 zhaoxg add 2014-7-25
	 * @return
	 * @throws GeneralException
	 */
	public LazyDynaBean getSalaryPayDate(Connection conn,String tablename,String salaryid)throws GeneralException
	{
		LazyDynaBean abean=new LazyDynaBean();
		try
		{
				ContentDAO dao=new ContentDAO(conn);
				String sql="select cname,cstate from salarytemplate where salaryid="+salaryid+"";
				String name="";
				String flag="";
				RowSet rs=dao.search(sql);
				if(rs.next()){
					name=rs.getString("cname");
					if("1".equals(rs.getString("cstate"))){
						flag="保险";
					}else{
						flag="薪资";
					}					
				}
				RowSet rowSet=dao.search("select a00z2,a00z3 from "+tablename);
				if(rowSet.next())
				{
					SimpleDateFormat df=new SimpleDateFormat("yyyy.MM.dd");
					Calendar c=Calendar.getInstance();
					Date d=rowSet.getDate(1);
					c.setTime(d);
					String a00z2=df.format(rowSet.getDate("a00z2"));
					String a00z3=rowSet.getString(2);
					String[] _a00z2=a00z2.split("\\.");
					abean.set("year",_a00z2[0]);
					abean.set("month",_a00z2[1]);
					abean.set("day",_a00z2[2]);
					abean.set("a00z2",a00z2);
					abean.set("count",a00z3);
					abean.set("name", name);
					abean.set("cstate", "1".equals(rs.getString("cstate"))?"1":"0");
				}
				rowSet.close();
				rs.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return abean;
	}
	/**
	 * 取得 薪资（保险）类别名 zhaoxg add 2014-7-28
	 * @return
	 * @throws GeneralException
	 */
	public LazyDynaBean getSalaryName(Connection conn,String salaryid)throws GeneralException
	{
		LazyDynaBean abean=new LazyDynaBean();
		try
		{
				ContentDAO dao=new ContentDAO(conn);
				String sql="select cname,cstate from salarytemplate where salaryid="+salaryid+"";
				String name="";
				String flag="";
				String cstate="";
				RowSet rs=dao.search(sql);
				if(rs.next()){
					name=rs.getString("cname");
					cstate=rs.getString("cstate");
					if("1".equals(cstate)){
						flag="保险";
					}else{
						flag="薪资";
					}
					abean.set("name", name);
					abean.set("flag", flag);
					abean.set("cstate", cstate==null||"".equals(cstate)?"0":cstate);
				}
				rs.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return abean;
	}

	public SalaryCtrlParamBo getCtrlparam() {
		return ctrlparam;
	}



	public void setCtrlparam(SalaryCtrlParamBo ctrlparam) {
		this.ctrlparam = ctrlparam;
	}



	public String getManager() {
		return manager;
	}



	public void setManager(String manager) {
		this.manager = manager;
	}
	
	/*************************************************************************
	 * 北京移动需求，加入审批公式                                                 *
	 * LiZhenWei add 2009-03-20                                              *
	 *************************************************************************/
		public SalaryTemplateBo(Connection conn, int salaryid) {
			super();
			this.conn=conn;
			this.salaryid=salaryid;
			ctrlparam=new SalaryCtrlParamBo(this.conn,this.salaryid);
			if(Sql_switcher.searchDbServer()!=2) //针对SQLSERVER 无需考虑锁表
				_withNoLock=" WITH(NOLOCK) ";
		}
		/**
		 * 取得某工资类别的审批公式列表
		 * @param salaryid
		 * @return
		 */
		public ArrayList getSpFormulaList(String salaryid)
		{
			ArrayList list = new ArrayList();
			try
			{
				StringBuffer sql = new StringBuffer();
				sql.append("select chkid,name,validflag,formula  from hrpchkformula where flag=1 and tabid='"+salaryid+"' order by seq");
				ContentDAO dao = new ContentDAO(this.conn);
				RowSet rs = null;
				rs=dao.search(sql.toString());
				while(rs.next())
				{
					LazyDynaBean bean = new LazyDynaBean();
					bean.set("chkid",rs.getString("chkid"));
					bean.set("name",rs.getString("name"));
					bean.set("validflag", rs.getString("validflag"));
					//bean.set("formula", )
					list.add(bean);
				}
				rs.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return list;
		}
		/**
		 * Get formula by keyword
		 * @param chkid
		 * @return
		 */
		public String getSpFormula(String chkid)
		{
			String formula="";
			try
			{
				String sql = "select formula from hrpchkformula where chkid="+chkid;
				ContentDAO dao = new ContentDAO(this.conn);
				RowSet rs = dao.search(sql);
				while(rs.next())
				{
					formula=Sql_switcher.readMemo(rs, "formula");
				}
				rs.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return formula;
		}
		/**
		 * 取审批公式的公式名称和提示信息
		 * @param chkid
		 * @return
		 */
		public LazyDynaBean getSpFormulaInfo(String chkid)
		{
			LazyDynaBean bean = new LazyDynaBean();
			try
			{
				String sql = "select name,information from hrpchkformula where chkid="+chkid;
				ContentDAO dao = new ContentDAO(this.conn);
				RowSet rs = dao.search(sql);
				while(rs.next())
				{
					bean.set("name", rs.getString("name"));
					bean.set("information",Sql_switcher.readMemo(rs,"information"));
				}
				rs.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return bean;
		}
		/**
		 * 取工资项目列表
		 * @param salaryid
		 * @return
		 */
		public ArrayList getSalaryItemList(String salaryid)
		{
			ArrayList list = new ArrayList();
			try
			{
				FormulaBo formulsbo = new FormulaBo();
				ArrayList alist=formulsbo.subStandardList(conn, salaryid);
				if(alist!=null)
				{
					for(int i=0;i<alist.size()-1;i++)
					{
						CommonData data=(CommonData)alist.get(i);
						list.add(data);
					}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return list;
			
		}
		public ArrayList getSpFormulaList2(String salaryid) //,String chkid) 2014-4-24 dengcan  
		{
			ArrayList list = new ArrayList();
			try
			{
				/*
				String[] temp=chkid.split(",");
				StringBuffer in_str=new StringBuffer("");
				if(temp!=null&&temp.length>0)
				{
					int zheng = temp.length/999;
					int yu = temp.length%999;
					in_str.append("( ");
					for(int j=0;j<zheng;j++){
						if(j!=0){
							in_str.append("or ");
						}
						in_str.append("chkid in (");
						for(int i=j*999;i<(j+1)*999;i++){
							if(i!=j*999){
								in_str.append(",");
							}
							in_str.append(temp[i]);
						}
						in_str.append(")");
					}
					if(zheng==0){
						if(yu>0){
							in_str.append(" chkid in (");
							for(int i=zheng*999;i<zheng*999+yu;i++){
								if(i!=zheng*999){
									in_str.append(",");
								}
								in_str.append(temp[i]);
							}
							in_str.append(")");
						}
					}else{
						if(yu>0){
							in_str.append("or chkid in (");
							for(int i=zheng*999;i<zheng*999+yu;i++){
								if(i!=zheng*999){
									in_str.append(",");
								}
								in_str.append(temp[i]);
							}
							in_str.append(")");
						}
					}

					in_str.append(")");
				}*/
				StringBuffer sql = new StringBuffer();
				sql.append("select chkid,name,validflag,formula,information from hrpchkformula where flag=1 and validflag=1 and tabid='"+salaryid+"'");
		//		if(chkid.equals("-1"))
				{
		    		sql.append(" order by seq");
				}
		/*		else
				{
					sql.append(" and "+in_str+" order by seq");//这貌似不用加这个chkid的条件，没看懂，上面加个防止超过一千条的处理  zhaoxg add 2014-1-3
				} */
				ContentDAO dao = new ContentDAO(this.conn);
				RowSet rs = null;
				rs=dao.search(sql.toString());
				while(rs.next())
				{
					LazyDynaBean bean = new LazyDynaBean();
					bean.set("chkid",rs.getString("chkid"));
					bean.set("name",rs.getString("name"));
					bean.set("validflag", rs.getString("validflag"));
					bean.set("formula", Sql_switcher.readMemo(rs,"formula"));
					bean.set("information",Sql_switcher.readMemo(rs, "information"));
					list.add(bean);
				}
				rs.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return list;
		}
		public HashMap isHaveField(String field,String salaryid)
		{
			HashMap map = new HashMap();
			try
			{
				if(field==null|| "".equals(field))
				{
					map.put("flag", "0");
					return map;
				}
				String sql = "select * from salaryset where salaryid="+salaryid+" and UPPER(itemid)='"+field.toUpperCase()+"'";
				ContentDAO dao = new ContentDAO(this.conn);
				RowSet rs = dao.search(sql);
				String itemdesc="";
				String flag="0";
				String codesetid="0";
				String itemtype="A";
				if (rs.next())
				{
					itemdesc=rs.getString("itemdesc");
					flag="1";
					codesetid=rs.getString("codesetid");
					itemtype=rs.getString("itemtype");
	                map.put("itemid",rs.getString("itemid"));
	                map.put("itemdesc",itemdesc);
	                map.put("codesetid",codesetid);
	                map.put("itemtype",itemtype);
	                map.put("itemlength",rs.getString("itemlength"));
	                map.put("decwidth",rs.getString("decwidth"));
				}
                map.put("flag",flag);

				rs.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return map;
		}
		
		public HashMap getShPersonsInfo(String salaryid,UserView view,String condid,String a_code,String type,String a00z1,String a00z0,String reportSQL) throws GeneralException
		{
			HashMap returnMap = new HashMap();
			ResultSet rs = null;
			try
			{
				//ArrayList list = new ArrayList();
			    boolean flag=true;
			    ArrayList outItemList = new ArrayList();

				String outname=this.userview.getUserName()+"_"+PubFunc.getStrg()+".xls";
				String checkinfor="";
				
				SalaryPkgBo pgkbo=new SalaryPkgBo(this.conn,this.userview,0);
				String username=this.userview.getUserName();
				if(this.manager!=null&&this.manager.length()>0)
					username=this.manager;
				LazyDynaBean abean=pgkbo.searchCurrentDate2(String.valueOf(this.salaryid),username);
				String strYm=(String)abean.get("strYm");
				String strC=(String)abean.get("strC");			
				if("".equalsIgnoreCase(strYm)&&!"1".equals(type))
				{
					returnMap.put("msg", "no");
					return returnMap;
				}				
				this.currym=strYm;
				this.currcount=strC;
				
                SalaryCtrlParamBo ctrlparam=new SalaryCtrlParamBo(conn,Integer.parseInt(salaryid));
                String verify_item = ctrlparam.getValue(SalaryCtrlParamBo.VERIFY_CTRL, "verify_item");
                String manager=ctrlparam.getValue(SalaryCtrlParamBo.SHARE_SET, "user");
                verify_item = verify_item==null?"":verify_item;
            
    			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
    			String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","valid");//是否定义唯一性指标 0：没定义
    			String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");//唯一性指标值
                
                String checkedField=SystemConfig.getPropertyValue("checkoutfield");
                checkedField = checkedField==null?"":checkedField;
                HashMap cf;
                cf=this.isHaveField("B0110", salaryid);
                if ( "1".equals((String)cf.get("flag"))){
                    outItemList.add(cf);
                }     
                cf=this.isHaveField("E0122", salaryid);
                if ( "1".equals((String)cf.get("flag"))){
                    outItemList.add(cf);
                }     
                cf=this.isHaveField("A0101", salaryid);
                if ( "1".equals((String)cf.get("flag"))){
                    outItemList.add(cf);
                }                    
                cf=this.isHaveField(checkedField, salaryid);
                if ( "1".equals((String)cf.get("flag"))){
                    outItemList.add(cf);
                }
                
                String [] arrVarifyItem = verify_item.split(",");
                for (int i=0;i<arrVarifyItem.length;i++){                    
                     String itemid = arrVarifyItem[i];
                     if ("".equals(itemid)|| ",".equals(itemid)|| itemid.equals(checkedField))
                         continue;
                     cf=this.isHaveField(itemid, salaryid); 
                     if ( "1".equals((String)cf.get("flag"))){
                         outItemList.add(cf);
                     }
                }
                cf=this.isHaveField("A0100", salaryid);
                if ( "1".equals((String)cf.get("flag"))){
                    outItemList.add(cf);
                } 
                
                int itemCount = outItemList.size();
                String outItemIds="";
                for (int i=0;i<outItemList.size();i++){
                    cf = (HashMap)outItemList.get(i);
                    String itemid = (String)cf.get("itemid");
                    if("a0100".equalsIgnoreCase(itemid)){
        				if("0".equals(uniquenessvalid)){
                            if ("".equals(outItemIds)) 
                                outItemIds = "a."+itemid;
                            else 
                                outItemIds = outItemIds+",a."+itemid;  
        				}else if(onlyname==null|| "".equals(onlyname)){
                            if ("".equals(outItemIds)) 
                                outItemIds = "a."+itemid;
                            else 
                                outItemIds = outItemIds+",a."+itemid;  
        				}else{
                            if ("".equals(outItemIds)) 
                                outItemIds = "b."+onlyname+" onlyname ";
                            else 
                                outItemIds = outItemIds+",b."+onlyname+" onlyname ";  
        				}       				
                    }else{
                        if ("".equals(outItemIds)) 
                            outItemIds = "a."+itemid;
                        else 
                            outItemIds = outItemIds+",a."+itemid;   
                    }                    
                }
				ArrayList formulaList = this.getSpFormulaList2(salaryid); //, chkid);
				ArrayList midVariableList=getSpMidVariableList(formulaList);
				DbWizard db = new DbWizard(this.conn);
				
				String sutable="";
				boolean isOper=false;
				if(manager.length()==0||view.getUserName().equalsIgnoreCase(manager))
				{
				    sutable=view.getUserName()+"_salary_"+salaryid;
				   isOper=true;
				}
				else
				    sutable=manager+"_salary_"+salaryid;
				if("1".equals(type))
					sutable="T#"+view.getUserName()+"_gz";
				
				HashMap map=this.getCondAndPrivSQL(view, sutable, salaryid, condid, a_code,isOper,type);
				String pSQL="";
				String mSQL="";
				if(manager.length()==0||view.getUserName().equalsIgnoreCase(manager))
					mSQL=" 1=1 ";
				else
				{
					if(map.get("mSQL")!=null)
						mSQL=(String)map.get("mSQL");
				}
				pSQL=(String)map.get("pSQL");	
				
				if("1".equals(type))
				{
			    	this.gz_tablename="T#"+view.getUserName()+"_gz";
			   // 	if(db.isExistTable("T#"+view.getUserName()+"_gz", false))
			    		db.dropTable("T#"+view.getUserName()+"_gz");
				//6900的方
			    	String temp_sql=reportSQL;

			    	//加上左侧机构数条件及权限范围  搜房网慢 wangrd 2015-07-31
			    	if (pSQL!=null && pSQL.length()>0){
			    		temp_sql =temp_sql+" and (" + pSQL+")";
			    	}
					if(temp_sql.indexOf("T#"+view.getUserName()+"_gz")!=-1)
						temp_sql=temp_sql.replaceAll("T#"+view.getUserName()+"_gz", "salaryhistory");
			    	this.createShTempTable(gz_tablename, a00z0, a00z1, view, salaryid,temp_sql);
			    	this.currym=a00z1;
			    	this.currcount=a00z0;
				}
				
				addMidVarIntoGzTable("",midVariableList);


				
				StringBuffer sql = new StringBuffer();
				sql.append("select distinct nbase from "+sutable);
				if("1".equals(type))
					sql.append(" where salaryid="+salaryid);
				ContentDAO dao = new ContentDAO(this.conn);
				rs = dao.search(sql.toString());
				ArrayList nbaseList = new ArrayList();
				while(rs.next())
				{
					nbaseList.add(rs.getString("nbase"));
				}

				GzAnalyseBo bo = new GzAnalyseBo(conn,view);
				ArrayList varlist = new ArrayList();
				varlist.addAll(midVariableList);
				varlist.addAll(this.getGzFieldList());
				HSSFWorkbook workbook = new HSSFWorkbook();
				HSSFSheet sheet=null;
				for(int i=0;i<formulaList.size();i++)
				{
					LazyDynaBean bean = (LazyDynaBean)formulaList.get(i);
					String formula=(String)bean.get("formula");
					String formulaname=(String)bean.get("name");
                    String information=(String)bean.get("information");
					if(formula==null|| "".equals(formula))
						continue;
					HSSFRow row=null;
					HSSFCell csCell=null;
					HSSFCellStyle titlestyle = style(workbook,0);
					HSSFCellStyle centerstyle = style(workbook,1);
					HSSFCellStyle cloumnstyle=style(workbook,2);
					HSSFCellStyle bordernone=style(workbook,3);
					HSSFCellStyle bordertop=style(workbook,4);
					centerstyle.setWrapText(true);
					short rows=0;
				
					YksjParser yp=null;
					int x=1;
					int y=0;
					bo.setTableName(sutable);
					for(int j=0;j<nbaseList.size();j++)
					{
						String nbase=(String)nbaseList.get(j);
						String preSQL=bo.getDbSQL(nbase,salaryid);
						yp = new YksjParser(view ,varlist,YksjParser.forNormal, YksjParser.LOGIC,YksjParser.forPerson , "Ht", "");
						//yp.run_where(formula);
						yp.setStdTmpTable(sutable);
						yp.setTempTableName(sutable);
						yp.setCon(conn);
						boolean b = yp.Verify_where(formula.trim());
						if (!b) {
							checkinfor =formulaname+ResourceFactory.getProperty("workdiary.message.review.failure")+"!\n\n";
							checkinfor += yp.getStrError();
							throw GeneralExceptionHandler.Handle(new Exception(checkinfor));
						} 
						//yp.run(formula,this.conn,"",sutable);
						yp.setVerify(false);
						yp.run(formula.trim());
//						ArrayList usrlist = yp.getUsedSets();
//						String tempTable = yp.getTempTableName();
						String wherestr = yp.getSQL();//公式的结果
//						HashMap hashmap = yp.getMapUsedFieldItems();
//						Iterator it =hashmap.values().iterator();
						sql.setLength(0);
						sql.append("select *");
//						sql.append(outItemIds);
						sql.append(" from "+sutable+" where ("+wherestr+")");
						sql.append(" and UPPER(nbase)='"+nbase.toUpperCase()+"'");
						if((type==null||!"1".equals(type))&&!isOper&& "1".equals(this.controlByUnitcode))
						{ 
							
						}
						else if(!isOper)
						{
							sql.append(" and ("+preSQL+")");
						}
						if(!"".equals(mSQL))
							sql.append(" and ("+mSQL+")");
						if(!"".equals(pSQL))
							sql.append(" and ("+pSQL+")");
						if("1".equals(type))
						{
							if(a00z0!=null&&!"".equals(a00z0))
							{
								sql.append(" and A00Z3=");
								sql.append(a00z0);	
							}
							if(a00z1!=null&&!"".equals(a00z1))
							{
								a00z1=a00z1.replaceAll("\\.","-");
				     			String[] temp=a00z1.split("-");
					    		sql.append(" and "+Sql_switcher.year("a00z2")+"="+temp[0]+" and ");
				     			sql.append(Sql_switcher.month("a00z2")+"="+temp[1]);	
							}
						}
						if("0".equals(type)&&reportSQL!=null&&!"".equals(reportSQL))
							sql.append(reportSQL);
						
						StringBuffer str = new StringBuffer();
						str.append("select ");
						str.append(outItemIds);
						str.append(" from ("+sql+") a,"+nbase.toUpperCase()+"a01 b where a.a0100=b.a0100 ");
						str.append(" order by a.a0000, a.A00Z0, a.A00Z1");
						rs=dao.search(str.toString());
						while(rs.next())
						{
							if(y==0)
							{
								sheet = workbook.createSheet((i+1)+"");
								row=sheet.createRow(rows);
								csCell =row.createCell((short)0);
								HSSFRichTextString  titlecontext = new HSSFRichTextString(ResourceFactory.getProperty("label.gz.shresult"));
								csCell.setCellStyle(titlestyle);
								csCell.setCellValue(titlecontext);
								ExportExcelUtil.mergeCell(sheet, 0, (short)0,0, (short)itemCount);
								rows++;
								row=sheet.createRow(rows);
								HSSFRichTextString  context = new HSSFRichTextString((i+1)+":"+formulaname);
								csCell =row.createCell((short)0);
								csCell.setCellValue(context);
								csCell.setCellStyle(bordernone);
								ExportExcelUtil.mergeCell(sheet, rows, (short)0,rows, (short)itemCount);
								rows++;
								row=sheet.createRow(rows);
								HSSFRichTextString  text = new HSSFRichTextString(ResourceFactory.getProperty("workdiary.message.message")+"："+information);
								csCell =row.createCell((short)0);
								csCell.setCellValue(text);
								csCell.setCellStyle(bordertop);
								ExportExcelUtil.mergeCell(sheet, rows, (short)0,rows, (short)itemCount);
								rows++;
								row=sheet.createRow(rows);
                                HSSFRichTextString  one = new HSSFRichTextString(ResourceFactory.getProperty("gz.bankdisk.sequencenumber"));
                                csCell =row.createCell((short)0);
                                csCell.setCellStyle(cloumnstyle);
                                csCell.setCellValue(one);
								for (int itemCol=0;itemCol<outItemList.size();itemCol++){								    
								    cf = (HashMap)outItemList.get(itemCol);  
								    String itemDesc = (String)cf.get("itemdesc");
				                    String itemid = (String)cf.get("itemid");
				                    if("a0100".equalsIgnoreCase(itemid)){
				        				if("0".equals(uniquenessvalid)){
				                            
				        				}else if(onlyname==null|| "".equals(onlyname)){
				                             
				        				}else{
				        					FieldItem item = DataDictionary.getFieldItem(onlyname);
				        					if(item!=null){
				        						itemDesc = item.getItemdesc();
				        					}else{
				        						itemDesc = "唯一性指标"; 
				        					}				        					
				        				}       				
				                    }
	                                HSSFRichTextString  value = new HSSFRichTextString(itemDesc);
	                                csCell =row.createCell((short)itemCol+1);
	                                csCell.setCellStyle(cloumnstyle);
	                                csCell.setCellValue(value);
								}
								/*
								HSSFRichTextString  two = new HSSFRichTextString(ResourceFactory.getProperty("lable.hiremanage.org_id"));
								csCell =row.createCell((short)1);
								csCell.setCellStyle(cloumnstyle);
								csCell.setCellValue(two);
								HSSFRichTextString  three = new HSSFRichTextString(ResourceFactory.getProperty("lable.hiremanage.dept_id"));
								csCell =row.createCell((short)2);
								csCell.setCellStyle(cloumnstyle);
								csCell.setCellValue(three);
								HSSFRichTextString  four = new HSSFRichTextString(ResourceFactory.getProperty("label.title.name"));
								csCell =row.createCell((short)3);
								csCell.setCellStyle(cloumnstyle);
								csCell.setCellValue(four);
								if(isHave.equals("1"))
								{
									HSSFRichTextString  five = new HSSFRichTextString((String)cf.get("itemdesc"));
									csCell =row.createCell((short)4);
									csCell.setCellStyle(cloumnstyle);
									csCell.setCellValue(five);

								}
								*/
								rows++;
							}
							flag=false;
							row=sheet.createRow(rows);
							HSSFRichTextString  on = new HSSFRichTextString(x+"");
							csCell =row.createCell((short)0);
							csCell.setCellStyle(centerstyle);
							csCell.setCellValue(on);
							
                            for (int itemCol=0;itemCol<outItemList.size();itemCol++){                                 
                                cf = (HashMap)outItemList.get(itemCol);  
                                String itemid = (String)cf.get("itemid");
                                String itemtype=(String)cf.get("itemtype");
                                String codeSetid=(String)cf.get("codesetid");
                                String value="";
                                if("a0100".equalsIgnoreCase(itemid)){
                    				if("0".equals(uniquenessvalid)){
                    					value=rs.getString(itemid);
                    				}else if(onlyname==null|| "".equals(onlyname)){
                    					value=rs.getString(itemid);
                    				}else{
                    					value=rs.getString("onlyname"); 
                    				}  
                                }else{
                                    if("A".equalsIgnoreCase(itemtype))
                                    {
                                        value=rs.getString(itemid);
                                        if(!("".equals(codeSetid)) && !("0".equals(codeSetid)))
                                            value=AdminCode.getCodeName((String)cf.get("codesetid"),value);
                                    }else if ("N".equalsIgnoreCase(itemtype))
                                    {
                                        value=PubFunc.DoFormatDecimal(rs.getString(itemid),Integer.parseInt((String)cf.get("decwidth")));
                                     }
                                    else if("D".equalsIgnoreCase(itemtype))
                                    {
                                        if(rs.getDate(itemid)!=null)
                                            value=(new SimpleDateFormat("yyyy-MM-dd")).format(rs.getDate(itemid));
                                    }
                                    else if("M".equalsIgnoreCase(itemtype))
                                    {
                                        value=Sql_switcher.readMemo(rs,itemid);
                                    }                                 
                                }
                                HSSFRichTextString richValue=new HSSFRichTextString(value);
                                csCell =row.createCell((short)itemCol+1);
                                csCell.setCellStyle(centerstyle);
                                csCell.setCellValue(richValue);
                            }
                            /*
							HSSFRichTextString  tw = new HSSFRichTextString(AdminCode.getCodeName("UN", rs.getString("b0110")==null?"":rs.getString("b0110")));
							csCell =row.createCell((short)1);
							csCell.setCellStyle(centerstyle);
							csCell.setCellValue(tw);
							HSSFRichTextString  th = new HSSFRichTextString(AdminCode.getCodeName("UM", rs.getString("e0122")==null?"":rs.getString("e0122")));
							csCell =row.createCell((short)2);
							csCell.setCellStyle(centerstyle);
							csCell.setCellValue(th);
							HSSFRichTextString  fou = new HSSFRichTextString(rs.getString("a0101")==null?"":rs.getString("a0101"));
							csCell =row.createCell((short)3);
							csCell.setCellStyle(centerstyle);
							csCell.setCellValue(fou);
							if(isHave.equals("1"))
							{
								HSSFRichTextString  five = null;
								
								String itemtype=(String)cf.get("itemtype");
								String value="";
								if(itemtype.equalsIgnoreCase("A")||itemtype.equalsIgnoreCase("N"))
								{
									if(rs.getString(checkedField)!=null)
										value=rs.getString(checkedField);
									if(!((String)cf.get("codesetid")).equals("0"))
										value=AdminCode.getCodeName((String)cf.get("codesetid"),value);
								}
								else if(itemtype.equalsIgnoreCase("D"))
								{
									if(rs.getDate(checkedField)!=null)
										value=(new SimpleDateFormat("yyyy-MM-dd")).format(rs.getDate(checkedField));
								}
								else if(itemtype.equalsIgnoreCase("M"))
								{
									value=Sql_switcher.readMemo(rs,checkedField);
								}
								 
							    five=new HSSFRichTextString(value);
								csCell =row.createCell((short)4);
								csCell.setCellStyle(centerstyle);
								csCell.setCellValue(five);
							}
							*/
							rows++;
							x++;
							y++;
						}				
					}
					if(sheet!=null)
					{
			    		for (int j = 0; j <=itemCount; j++)
			    		{
			    			sheet.setColumnWidth(Short.parseShort(String.valueOf(j)),(short)6000);
			    		}
			    		for (int j = 0; j <=rows; j++)
			    		{
			    			row=sheet.getRow(j);
			    			if(row==null)
			    	    	    row = sheet.createRow(j);
			    		    row.setHeight((short) 400);
			    		}
			     	}
				}
				
				FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+outname);
				workbook.write(fileOut);
				fileOut.close();	
				sheet=null;
				workbook=null;
				//returnMap.put("list",list);
				/* 安全问题：文件下载 薪资发放-审核 xiaoyun 2014-9-29 start */
				//outname=outname.replace(".xls","#");
				/* 安全问题：文件下载 薪资发放-审核 xiaoyun 2014-9-29 end */
				returnMap.put("filename",outname);
				if(flag)
				{
					returnMap.put("msg", "no");
				}
				else
				{
					returnMap.put("msg", "yes");
				}
				rs.close();
				if("1".equals(type))
				{
					/**用完临时表，删除*/
			 //   	if(db.isExistTable("T#"+view.getUserName()+"_gz", false))
			    		db.dropTable("T#"+view.getUserName()+"_gz");
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
			return returnMap;
		}
		public HashMap getCondAndPrivSQL(UserView view,String tableName,String salaryid,String condid,String a_code,boolean isOper,String type)
		{
			HashMap map = new HashMap();
			try
			{
				StringBuffer SQL = new StringBuffer("");
				String sql = "select salaryid,lprogram from salarytemplate where salaryid="+salaryid;
				String str_path="/Params/Serive/SeiveItem[@ID="+condid+"]";
				ContentDAO dao = new ContentDAO(this.conn);
				RowSet rs = null;
				String exp_factor="";
				if(!"all".equalsIgnoreCase(condid)&&!"new".equalsIgnoreCase(condid))
				{
					rs = dao.search(sql);
		    		while(rs.next())
		    		{
		        		String lpro=Sql_switcher.readMemo(rs, "lprogram");
			    		Document doc=PubFunc.generateDom(lpro);	
				    	XPath xpath=XPath.newInstance(str_path);
				      	List childlist=xpath.selectNodes(doc);
				    	Element element=null;
				    	if(childlist.size()!=0)
			    		{
				    		for(int i=0;i<childlist.size();i++)
			    			{
				    			element=(Element)childlist.get(i);
				    			exp_factor=element.getAttributeValue("Expr")+"|"+element.getAttributeValue("Factor");
				    		}//for end.
				    	}
		    		}
		    		rs.close();
				}
				/**人员筛选的sql*/
				String filterSQL="";
				if(!"".equals(exp_factor))
				{
	     			int idx=0;
			    	idx=exp_factor.indexOf("|");
		    		String expr=exp_factor.substring(0, idx);
		    		String factor=exp_factor.substring(idx+1).toUpperCase();
		    		BankDiskSetBo bo = new BankDiskSetBo(this.conn);
		    		HashMap fieldItemMap=bo.getFieldItemMap(Integer.parseInt(salaryid),view);
					FactorList factor_bo=new FactorList(expr.toString(),factor.toString(),view.getUserId(),fieldItemMap);
					filterSQL=factor_bo.getSingleTableSqlExpression(tableName);
			    	/*FactorList factorlist=new FactorList(expr,factor,"");
			    	filterSQL=factorlist.getSingleTableSqlExpression(tableName);*/
				}
				/**机构限制*/
				StringBuffer codeSQL=new StringBuffer("");
				if(a_code!=null&&!"".equals(a_code))
				{
					if(a_code.indexOf("un")!=-1||a_code.indexOf("UN")!=-1)
					{
						codeSQL.append(" (b0110 like '");
						codeSQL.append(a_code.substring(2));
						codeSQL.append("%'");
						if("".equals(a_code.substring(2)))
						{
							codeSQL.append(" or b0110 is null");
						}
						codeSQL.append(")");
					}
					if(a_code.indexOf("um")!=-1||a_code.indexOf("UM")!=-1)
					{
						codeSQL.append(" (e0122 like '");
						codeSQL.append(a_code.substring(2));
						codeSQL.append("%'");
						if("".equals(a_code.substring(2)))
						{
							codeSQL.append(" or e0122 is null");
						}
						codeSQL.append(")");
					}
				}
				/**管理范围权限*/
				StringBuffer mSQL=new StringBuffer("");
				
				if((type==null||!"1".equals(type))&&!isOper&& "1".equals(this.controlByUnitcode))
				{
					String whl=this.getWhlByUnits();
					if(whl.length()>0)
					{
						mSQL.append(" 1=1 "+whl);
					}
					
				}
				else if(!view.isSuper_admin()&&!"1".equals(view.getGroupId())&&!isOper)
				{
					String code=view.getManagePrivCode();
					String codevalue=view.getManagePrivCodeValue();
					if(code==null|| "".equals(code))
						mSQL.append(" 1=2");
					else
					{
						if("UN".equalsIgnoreCase(code))
						{
							mSQL.append(" (b0110 like '");
							mSQL.append(codevalue);
							mSQL.append("%'");
							if("".equals(codevalue))
							{
								mSQL.append(" or b0110 is null");
							}
							mSQL.append(")");
						}
						else
						{
							mSQL.append(" (e0122 like '");
							mSQL.append(codevalue);
							mSQL.append("%'");
							if("".equals(codevalue))
							{
								mSQL.append(" or e0122 is null");
							}
							mSQL.append(")");
						}
					}
				}
				if(!"".equals(filterSQL))
				{
					if(SQL.toString().length()>0)
						SQL.append(" and ");
					SQL.append(filterSQL);
				}
				if(!"".equals(codeSQL.toString()))
				{
					if(SQL.toString().length()>0)
						SQL.append(" and ");
					SQL.append(codeSQL);
				}
				if(!"".equals(mSQL.toString()))
				{
					map.put("mSQL",mSQL.toString());
				}
				map.put("pSQL",SQL.toString());
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return map;
		}
			public HSSFCellStyle style(HSSFWorkbook workbook,int styles){
			HSSFCellStyle style = workbook.createCellStyle();
			
			
			switch(styles){
			
			case 0:
					HSSFFont fonttitle = fonts(workbook,ResourceFactory.getProperty("gz.gz_acounting.black.font"),15);
					fonttitle.setBold(true);//加粗 
					style.setFont(fonttitle);
					style.setBorderBottom(BorderStyle.NONE);
					style.setBorderLeft(BorderStyle.NONE);
					style.setBorderRight(BorderStyle.NONE);
					style.setBorderTop(BorderStyle.NONE);
					style.setAlignment(HorizontalAlignment.CENTER );
			        break;			
			case 1:
					style.setFont(fonts(workbook,ResourceFactory.getProperty("gz.gz_acounting.m.font"),12));
					style.setBorderBottom(BorderStyle.THIN);
					style.setBorderLeft(BorderStyle.THIN);
					style.setBorderRight(BorderStyle.THIN);
					style.setBorderTop(BorderStyle.THIN);
					style.setVerticalAlignment(VerticalAlignment.CENTER);
					style.setAlignment(HorizontalAlignment.CENTER );
					break;
			case 2:
					style.setFont(fonts(workbook,ResourceFactory.getProperty("gz.gz_acounting.m.font"),12));
					style.setBorderBottom(BorderStyle.THIN);
					style.setBorderLeft(BorderStyle.THIN);
					style.setBorderRight(BorderStyle.THIN);
					style.setBorderTop(BorderStyle.THIN);	
					style.setAlignment(HorizontalAlignment.LEFT);
					//style.setFillBackgroundColor(HSSFColor.YELLOW.index);
					style.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index); 
					style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
					break;
			case 3:
					style.setFont(fonts(workbook,ResourceFactory.getProperty("gz.gz_acounting.m.font"),12));
					style.setBorderBottom(BorderStyle.NONE);
					style.setBorderLeft(BorderStyle.NONE);
					style.setBorderRight(BorderStyle.NONE);
					style.setBorderTop(BorderStyle.NONE);
					//style.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index); 
					//style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);              	
					break;		
			case 4:
					style.setFont(fonts(workbook,ResourceFactory.getProperty("gz.gz_acounting.m.font"),12));
					style.setBorderBottom(BorderStyle.NONE);
					style.setBorderLeft(BorderStyle.NONE);
					style.setBorderRight(BorderStyle.NONE);
					style.setBorderTop(BorderStyle.NONE);
					//style.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index); 
					//style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);   
				  break;
			default:		
					style.setFont(fonts(workbook,ResourceFactory.getProperty("gz.gz_acounting.m.font"),12));
					style.setAlignment(HorizontalAlignment.LEFT );
					style.setBorderBottom(BorderStyle.THIN);
					style.setBorderLeft(BorderStyle.THIN);
					style.setBorderRight(BorderStyle.THIN);
					style.setBorderTop(BorderStyle.THIN);	 
					break;
			}
			return style;
		}
		public HSSFFont fonts(HSSFWorkbook workbook,String fonts,int size){
			HSSFFont font = workbook.createFont();
			font.setFontHeightInPoints((short)size);
			font.setFontName(fonts);
			return font;
		}
		
		
		public ArrayList getSpMidVariableList(ArrayList formulaList) throws GeneralException
	{
		ArrayList fieldlist=new ArrayList();
		ArrayList new_fieldList=new ArrayList();
		try
		{
			StringBuffer buf=new StringBuffer();
			buf.append("select cname,chz,ntype,cvalue,fldlen,flddec,codesetid from ");
			buf.append(" midvariable where nflag=0 and templetid=0 ");
			buf.append(" and (cstate is null or cstate='");
			buf.append(this.salaryid);
			buf.append("') order by sorting");
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rset=dao.search(buf.toString());
			while(rset.next())
			{
				FieldItem item=new FieldItem();
				item.setItemid(rset.getString("cname"));
				item.setFieldsetid(/*"A01"*/"");//没有实际含义
				item.setItemdesc(rset.getString("chz"));
				item.setItemlength(rset.getInt("fldlen"));
				item.setDecimalwidth(rset.getInt("flddec"));
				item.setFormula(Sql_switcher.readMemo(rset, "cvalue"));
				item.setCodesetid(rset.getString("codesetid"));
				switch(rset.getInt("ntype"))
				{
				case 1://
					item.setItemtype("N");
					break;
				case 2:
				case 4://代码型					
					item.setItemtype("A");
					break;
				case 3:
					item.setItemtype("D");
					break;
				}
				item.setVarible(1);
				fieldlist.add(item);
			}// while loop end.
			rset.close();
			//过滤薪资类别  计算公式用不到的临时变量
			FieldItem item=null;
			HashMap map=new HashMap();
			for(int i=0;i<formulaList.size();i++)
			{
				  DynaBean dbean=(LazyDynaBean)formulaList.get(i);
				  LazyDynaBean bean = (LazyDynaBean)formulaList.get(i);
				  String formula=((String)bean.get("formula")).toLowerCase();
				  if(formula==null|| "".equals(formula))
						continue;
	              for(int j=0;j<fieldlist.size();j++)
	              {
	            	  item=(FieldItem)fieldlist.get(j);
	            	  String item_id=item.getItemid().toLowerCase();
	            	  String item_desc=item.getItemdesc().trim().toLowerCase();
	            	  if(formula.indexOf(item_desc)!=-1&&map.get(item_id)==null)
	            	  {
	            		  new_fieldList.add(item);
	            		  map.put(item_id, "1");
	            	  }
	            		  
	              }
			}
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		//return fieldlist;
		return new_fieldList;
	}
		public void createShTempTable(String tableName,String a00z0,String a00z1,UserView viewe,String salaryid,String reportSQL)
		{
			try
			{
				StringBuffer sql = new StringBuffer();
				//" and (( curr_user='"+this.userView.getUserId()+"' and ( sp_flag='02' or sp_flag='07' ) ) or ( ( AppUser is null or AppUser Like '%;"+this.userView.getUserName()+";%' ) and   sp_flag='06' ) ) "
				sql.append(" salaryid="+salaryid);
				sql.append(" and ((lower(curr_user)='"+viewe.getUserId().toLowerCase()+"' and (sp_flag='02' or sp_flag='07')) or ( ( (AppUser is null  "+getPrivWhlStr("")+"  )  or AppUser Like '%;"+viewe.getUserName()+";%' ) and  (sp_flag='06' or  sp_flag='03') ))");
     			if(a00z0!=null&&!"".equals(a00z0))
	    		{
	    			sql.append(" and A00Z3=");
	    			sql.append(a00z0);	
	    		}
	     		if(a00z1!=null&&!"".equals(a00z1))
	    		{
	     			a00z1=a00z1.replaceAll("\\.","-");
	     			String[] temp=a00z1.split("-");
		    		sql.append(" and "+Sql_switcher.year("a00z2")+"="+temp[0]+" and ");
	     			sql.append(Sql_switcher.month("a00z2")+"="+temp[1]);	
		    	}
	     		if(reportSQL!=null&&!"".equals(reportSQL))
	     		{
	     			sql.append(reportSQL);
	     		}
	     		copyDataToSpTempTable(sql.toString(),tableName);
	     		
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}

    public ArrayList getFilterField(String id)
    {
	  ArrayList list  = new ArrayList();
	  try
	  {
		  String sql = "select * from gzitem_filter where id="+id;
		  ContentDAO dao = new ContentDAO(this.conn);
		  RowSet rs=dao.search(sql);
		  String cfldName="";
		  while(rs.next())
		  {
			  cfldName=rs.getString("cfldName");
		  }
		  rs.close();
		  if(cfldName!=null&&!"".equals(cfldName))
		  {
			  String[] arr=cfldName.split(",");
			  for(int i=0;i<arr.length;i++)
			  {
				  String fieldName=arr[i];
				  for(int j=0;j<this.fieldlist.size();j++)
				  {
					  Field field=(Field)this.fieldlist.get(j);
					  if(fieldName.equalsIgnoreCase(field.getName()))
					  {
						  CommonData cd=new CommonData(field.getName(),field.getLabel());
						  list.add(cd);
					  }
				  }
			  }
		  }
	  }
	  catch(Exception e)
	  {
		  e.printStackTrace();
	  }
      return list;
    }
    /**
     * 判断是否用加唯一性指标字段
     * @param field
     * @param fields
     * @return
     */
    public boolean isAddColumn(String field,String fields)
    {
    	boolean flag=true;
    	if(field==null|| "".equals(field))
    		flag=false;
    	else
    	{
    		if((","+fields+",").toUpperCase().indexOf(","+field.toUpperCase()+",")!=-1)
    			flag=false;
    	}
    	return flag;
    }
    public ArrayList getQueryFieldList(String salaryid)
    {
    	ArrayList list = new ArrayList();
    	try
    	{
    		LazyDynaBean bean = new LazyDynaBean();
    		bean.set("itemid", "A0101");
    		bean.set("codesetid", "0");
    		bean.set("itemtype","A");
    		bean.set("value", "");
    		bean.set("viewvalue", "");
    		bean.set("itemdesc", "姓名");
    		list.add(bean);
    		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
			String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");
			String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","valid");
			if(uniquenessvalid!=null&&!"".equals(uniquenessvalid)&&!"0".equals(uniquenessvalid)&&onlyname!=null&&!"".equals(onlyname)&&!"a0101".equalsIgnoreCase(onlyname))
			{
				String sql = "select * from salaryset where salaryid="+salaryid+" and UPPER(itemid)='"+onlyname.toUpperCase()+"'";
				ContentDAO dao = new ContentDAO(this.conn);
				RowSet rs = dao.search(sql);
				while(rs.next())
				{
					LazyDynaBean abean = new LazyDynaBean();
					abean.set("itemid", rs.getString("itemid"));
					abean.set("codesetid",rs.getString("codesetid"));
					abean.set("itemdesc", rs.getString("itemdesc"));
					abean.set("value", "");
					abean.set("viewvalue", "");
					abean.set("itemtype", rs.getString("itemtype").toUpperCase());
					list.add(abean);
				}
			}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return list;
    }
    public String getQuerySql(String tableName,ArrayList list) throws GeneralException
    {
    	String sql="";
    	try
    	{
    		StringBuffer buf = new StringBuffer("");
    		for(int i=0;i<list.size();i++)
    		{
    			LazyDynaBean bean=(LazyDynaBean)list.get(i);
    			String itemid=(String)bean.get("itemid");
    			String value=(String)bean.get("value");
//    			if(value!=null&&!value.equals("")){
//        			if(value.replaceAll("[a-z]*[A-Z]*\\d*-*_*\\s*", "").length()==0){
//        				
//        			}else{
//        				throw GeneralExceptionHandler.Handle(new Exception("查询内容不能包含特殊字符！"));
//        			}
//    			}

    				
    			if(value==null|| "".equals(value.trim()))
    				buf.append(" AND 1=1 ");
    			else
    			{
    				if("a0101".equalsIgnoreCase(itemid))
    				{
    					buf.append(" and "+tableName+".A0101 like '%"+Str(PubFunc.keyWord_reback(value))+"%'");
    				}
    				else
    				{
    					buf.append(" and "+tableName+"."+itemid+" like '%"+Str(PubFunc.keyWord_reback(value))+"%'");//身份证也支持 %数字% 式的模糊查询 zhaoxg 2013-6-20
    				}
    			}
    		}
    		if(buf.toString().length()>0)
    			sql=buf.toString();
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
    	}
    	return sql;
    }
    public String Str(String str){
		  if(str==null)
			  return str;
		  String temp=str.replaceAll("'","");
		  temp=temp.replaceAll("‘","");
		  temp=temp.replaceAll("“","");
		  temp=temp.replaceAll("\"","");
		  temp=temp.replaceAll("＇","");
		  return temp;
    }


		public boolean isImportMen() {
			return isImportMen;
		}



		public void setImportMen(boolean isImportMen) {
			this.isImportMen = isImportMen;
		}

		public String getFilterWhl() {
			return filterWhl;
		}

		public void setFilterWhl(String filterWhl) {
			this.filterWhl = filterWhl;
		}

		public String getSp_filterWhl() {
			return sp_filterWhl;
		}

		public void setSp_filterWhl(String sp_filterWhl) {
			this.sp_filterWhl = sp_filterWhl;
		}

		public String getFrom_module() {
			return from_module;
		}

		public void setFrom_module(String from_module) {
			this.from_module = from_module;
		}

		public UserView getUserview() {
			return userview;
		}

		public void setUserview(UserView userview) {
			this.userview = userview;
		}

		public String getStandardGzItemStr() {
			return standardGzItemStr;
		}

		public void setStandardGzItemStr(String standardGzItemStr) {
			this.standardGzItemStr = standardGzItemStr;
		}

		public String getOnlyField() {
			return onlyField;
		}

		public void setOnlyField(String onlyField) {
			this.onlyField = onlyField;
		}
		/** 
		* 去掉字符串中的空格 
		* @param str 
		* @return String 
		*/ 
		public  String removeBlank(String str){ 
		StringBuilder sb = new StringBuilder(); 
		char c = ' '; 
		for(int i = 0 ; i < str.length() ; i++){ 
		char ch = str.charAt(i); 
		if(ch != c){ 
		sb.append(ch); 
		} 
		} 
		return sb.toString(); 
		}
		
		
		
		
		/**
		 * 取得权限过滤语句
		 * @param busiUnit  1:工资发放  2:工资总额  3:所得税
		 * @return
		 */
		public String getPrivPre(String busiUnit)
		{
			StringBuffer pre=new StringBuffer("");
			try
			{
				ArrayList list = this.userview.getPrivDbList();		
				if(this.userview.isSuper_admin()|| "1".equals(this.userview.getGroupId()))
				{
					pre.append(" 1=1" );
				}
				else if(list==null||list.size()<=0)
				{
					pre.append(" 1=2");
				}
				else
				{
					String b_units=this.userview.getUnitIdByBusi(busiUnit);
					String units=this.userview.getUnit_id();
					if(b_units!=null&&b_units.length()>0&&!"UN".equalsIgnoreCase(b_units)) //模块操作单位
					{
						String unitarr[] =b_units.split("`");
	    				for(int i=0;i<unitarr.length;i++)
	    				{
		    				String codeid=unitarr[i];
		    				if(codeid==null|| "".equals(codeid))
		    					continue;
			    			if(codeid!=null&&codeid.trim().length()>2)
		    				{
			    				if("UN".equalsIgnoreCase(codeid.substring(0,2)))
			    				{
		                 		   pre.append(" or b0110 like '"+codeid.substring(2)+"%' ");
			    				}
			    				else if("UM".equalsIgnoreCase(codeid.substring(0,2)))
			    				{
			    					pre.append(" or e0122 like '"+codeid.substring(2)+"%'");
			    				}
		                 	}
			    			else if(codeid!=null&& "UN".equalsIgnoreCase(codeid))
			    			{
			    				pre.append(" or 1=1 ");
		                 	}	
			    		}
			    		if(pre.toString().length()>0)
		    			{
		     				String str=pre.toString().substring(3);
		    				pre.setLength(0);
		    				pre.append(str);
		    			}
						
					}
					else if(units!=null&&units.length()>0&&!"UN".equalsIgnoreCase(units)) //操作单位
					{
						String unitarr[] =units.split("`");
	    				for(int i=0;i<unitarr.length;i++)
	    				{
		    				String codeid=unitarr[i];
		    				if(codeid==null|| "".equals(codeid))
		    					continue;
			    			if(codeid!=null&&codeid.trim().length()>2)
		    				{
			    				if("UN".equalsIgnoreCase(codeid.substring(0,2)))
			    				{
		                 		   pre.append(" or b0110 like '"+codeid.substring(2)+"%' ");
			    				}
			    				else if("UM".equalsIgnoreCase(codeid.substring(0,2)))
			    				{
			    					pre.append(" or e0122 like '"+codeid.substring(2)+"%'");
			    				}
		                 	}
			    			else if(codeid!=null&& "UN".equalsIgnoreCase(codeid))
			    			{
			    				pre.append(" or 1=1 ");
		                 	}	
			    		}
			    		if(pre.toString().length()>0)
		    			{
		     				String str=pre.toString().substring(3);
		    				pre.setLength(0);
		    				pre.append(str);
		    			}
						
					}
					else  //管理权限
					{
						for(int i=0;i<list.size();i++)
		    	    	{
		    	    		String nbase=(String)list.get(i);
			        		if (i == 0) {
			        			pre.append("(");
    		        		}
			        		/**加入高级授权*/
		            		StringBuffer sql = new StringBuffer("");
				        	String priStrSql = InfoUtils.getWhereINSql(this.userview, nbase);
			     	    	sql.append("select "+nbase+"a01.A0100 ");
				        	if (priStrSql.length() > 0)
				        		sql.append(priStrSql);
				        	else
				        		sql.append(" from "+nbase+"a01");
				  	
    			        	pre.append("(upper(nbase)='");
    		        		pre.append(nbase.toUpperCase()+"'");
    			        	pre.append(" and a0100 in ("+sql.toString()+"))");
    			        	if (i != list.size() - 1) {
	    		        		pre.append(" OR ");
    			        	} else
     			        		pre.append(")");
		    	    	}
					}
					
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return pre.toString();
		}
//   start xieguiquan
	/**
	 * 获得 salarytemplate对象   
	 * @param salaryid
	 * @param conn
	 * @return
	 */	
		public  RecordVo getRealConstantVo(Connection conn,int salaryid)
		  {
		   
		    RecordVo vo = null;
		   
		    	vo=new RecordVo("salarytemplate");
		    	vo.setInt("salaryid", salaryid);
				ContentDAO dao=new ContentDAO(conn);
				try {
					try {
						vo=dao.findByPrimaryKey(vo);
					} catch (SQLException e) {	
						e.printStackTrace();
					}
				} catch (GeneralException e) {
					e.printStackTrace();
				}
		    
		  
		    return vo;
		  }

		
	
	// end xieguiquan	
		/**
		 * 设置单位部门变动子集功能加上授权，判断是否出现引入单位部门变动人员菜单该菜单
		 * @return 1出现，0不出现
		 */
		public String isVisibleItem()
		{
			String flag="0";
			GzAmountXMLBo bo=new GzAmountXMLBo(this.conn,1);
			HashMap map=bo.getValuesMap();
			if(map!=null)
			{
				//设置了变动子集
				if(map.get("chg_set")!=null&&((String)map.get("chg_set")).length()>0)
				{
					flag="1";
				}
			}
			if("0".equals(flag))
			{
				if(this.userview.isSuper_admin()|| "1".equals(this.userview.getGroupId()))
					flag="1";
				else if(this.userview.hasTheFunction("324021202")||this.userview.hasTheFunction("325021202")||this.userview.hasTheFunction("327021202")||this.userview.hasTheFunction("327121202"))
					flag="1";
			}
			return flag;
		}
		public String getQueryvalue() {
			return queryvalue;
		}

		public void setQueryvalue(String queryvalue) {
			this.queryvalue = queryvalue;
		}

		public String getControlByUnitcode() {
			return controlByUnitcode;
		}

		public void setControlByUnitcode(String controlByUnitcode) {
			this.controlByUnitcode = controlByUnitcode;
		}

		public String getCurrym() {
			return currym;
		}

		public void setCurrym(String currym) {
			this.currym = currym;
		} 
		
		public String getCurrcount() {
			return currcount;
		}

		public void setCurrcount(String currcount) {
			this.currcount = currcount;
		}

		public ArrayList getSetlist() {
			return setlist;
		}

		public void setSetlist(ArrayList setlist) {
			this.setlist = setlist;
		}

		public ArrayList getAdd_delList() {
			return add_delList;
		}

		public void setAdd_delList(ArrayList add_delList) {
			this.add_delList = add_delList;
		} 
		
		public String getScreeningWhereSql() {
			return screeningWhereSql;
		}
		public void setScreeningWhereSql(String screeningWhereSql) {
			this.screeningWhereSql = screeningWhereSql;
		}
	/**
	 * 薪资汇总审批获取需要操作的数据范围  zhaoxg add 2015-1-22
	 * @param bosdate
	 * @param count
	 * @param record
	 * @param collectPoint
	 * @return
	 */
	public String getCollectSPPriv(String bosdate,String count,String record,String collectPoint){
		StringBuffer str = new StringBuffer();
		try{
			StringBuffer buf=new StringBuffer();
			GzSpCollectBo spbo = new GzSpCollectBo(this.userview,this.conn);
			String b0110 = "b0110";
			String e0122 = "e0122";
			if("UNUM".equals(collectPoint)){//单位+部门
				String orgid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD, "orgid"); //归属单位
				String deptid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid");//归属部门
				if(orgid.length()>0){
					b0110 = orgid;
				}
				if(deptid.length()>0){
					e0122 = deptid;
				}
				collectPoint = spbo.getCollectPointSql(b0110, e0122,"");
			}else //可能出现指标为空和null的
				collectPoint = "nullif("+collectPoint+",'')";
			
			String[] records = record.split("#");
			for(int i=0;i<records.length;i++){
				buf.append(" or  "+collectPoint+" like '");
				buf.append(records[i]);
				buf.append("%'");
			}			
			String privWhlStr = this.getPrivWhlStr("");
//			str.append(" and (((((AppUser is null  "+privWhlStr+" ) or AppUser Like '%;"+this.userview.getUserName()+";%' ) and (sp_flag='06' or sp_flag='03')) or curr_user='"+this.userview.getUserName()+"')");
//			str.append(" and salaryid='"+this.salaryid+"' and a00z2="+Sql_switcher.dateValue(bosdate)+" and a00z3="+count+" ");
			str.append(" and ("+buf.substring(3)+")");
		}catch(Exception e){
			e.printStackTrace();
		}
		return str.toString();
	}
	
	/**
	 * 添加参数gz_module，判断是薪资还是保险,lis修改
	 * @param name
	 * @param gz_module
	 * @return
	 */
	public static String getAttributeName(String name,String gz_module){
		String str="";
		HashMap map = new HashMap();
		try{
			map.put("/param/cond_mode/flag", "人员范围");
			map.put("/param/manager/user", "共享方式");
			map.put("/param/priv_mode/flag", "限制用户管理范围");
			map.put("/param/tax_date_field", "计税时间指标");
			map.put("/param/declare_tax", "报税时间指标");
			map.put("/param/pay_flag", "发薪标识指标");
			map.put("/param/tax_mode", "计税方式指标");
			map.put("/param/tax_desc", "纳税项目说明");
			map.put("/param/flow_ctrl/flag", "审批方式");
			map.put("/param/flow_ctrl/reject_mode", "驳回方式");
			map.put("/param/flow_ctrl/sp_relation_id", "审批关系");
			map.put("/param/flow_ctrl/default_filterid", "默认审批项目");
			map.put("/param/sum_field/orgid", "归属单位指标");
			map.put("/param/sum_field/deptid", "归属部门指标");
			map.put("/param/note/sms", "短信通知");
			map.put("/param/note/mail", "邮件通知");
			map.put("/param/note", "邮件模板");
			map.put("/param/amount_ctrl/amount_ctrl_ff", "薪资总额控制/控制范围/控制发放");
			map.put("/param/amount_ctrl/amount_ctrl_sp", "薪资总额控制/控制范围/控制审批");
			map.put("/param/amount_ctrl/ctrl_type", "控制方式");
			map.put("/param/amount_ctrl/flag", "是否进行总额控制");
			String ff="1".equals(gz_module)?"保险":"薪资";
			map.put("/param/verify_ctrl/verify_ctrl_ff", "审核公式控制/控制范围/控制"+ff+"发放");
			map.put("/param/verify_ctrl/verify_ctrl_sp", "审核公式控制/控制范围/控制"+ff+"审批");
			map.put("/param/verify_ctrl", "是否进行审核公式控制");
			
			map.put("/param/a01z0/flag", "显示停放标识");
			
			map.put("/param/fieldpriv", "非写权限指标参与计算");
			map.put("/param/readfield", "读权限指标允许重新引入");
			
			map.put("/param/royalties/valid", "提成薪资");
			map.put("/param/royalties/strExpression", "提成薪资/数据范围");
			map.put("/param/royalties/date", "提成薪资/计划日期指标");
			map.put("/param/royalties/period", "提成薪资/周期");
			map.put("/param/royalties/setid", "提成薪资/提成数据子集");
			map.put("/param/royalties/relation_fields", "提成薪资/关联指标");
			
			map.put("/param/piecepay/valid", "计件薪资");
			map.put("/param/piecepay/strExpression", "计件薪资/数据范围");
			map.put("/param/piecepay/period", "计件薪资/周期");
			map.put("/param/piecepay/firstday", "计件薪资/周期/月");
			map.put("/param/piecepay/relation_field", "计件薪资/引入指标");
			
			map.put("/Params/ConfirmType/allow_edit_subdata", "允许修改已归档数据");
			map.put("/Params/ConfirmType/subNoPriv", "数据提交入库不判断子集及指标权限");
			map.put("/Params/ConfirmType/no_show", "提交时不显示数据操作方式设置");
			map.put("/Params/ConfirmType", "数据提交方式");
			
			map.put("/Params/hidden_items/hidden_item", "审批指标");
			str=(String) map.get(name);
			str = str==null||str.length()==0?name:str;
		}catch(Exception e){
			e.printStackTrace();
		}
		return str;
	}
	public static String getAttributeValue(String ul,String name,Connection con,String salaryid){
		String str="";
		HashMap map = new HashMap();
		try{
			map.put("/param/cond_mode/flag0", "简单条件");//人员范围
			map.put("/param/cond_mode/flag1", "复杂条件");//人员范围
			map.put("/param/priv_mode/flag1", "勾选");//限制用户管理范围
			map.put("/param/priv_mode/flag0", "取消勾选");//限制用户管理范围
			map.put("/param/manager/user", "不共享");
			map.put("/param/flow_ctrl/flag1", "需要审批");
			map.put("/param/flow_ctrl/flag0", "不需要审批");
			map.put("/param/flow_ctrl/reject_mode1", "逐级驳回");
			map.put("/param/flow_ctrl/reject_mode2", "驳回发起人");
			map.put("/param/note/sms1", "勾选");
			map.put("/param/note/sms0", "取消勾选");
			map.put("/param/note/mail1", "勾选");
			map.put("/param/note/mail0", "取消勾选");
			map.put("/param/amount_ctrl/amount_ctrl_ff1", "勾选");
			map.put("/param/amount_ctrl/amount_ctrl_ff-1", "取消勾选");
			map.put("/param/amount_ctrl/amount_ctrl_sp1", "勾选");
			map.put("/param/amount_ctrl/amount_ctrl_sp-1", "取消勾选");
			map.put("/param/amount_ctrl/ctrl_type1", "强行控制");
			map.put("/param/amount_ctrl/ctrl_type0", "预警提示");
			map.put("/param/amount_ctrl/flag1", "勾选");
			map.put("/param/amount_ctrl/flag0", "取消勾选");
			
			map.put("/param/verify_ctrl/verify_ctrl_ff1", "勾选");
			map.put("/param/verify_ctrl/verify_ctrl_ff-1", "取消勾选");
			map.put("/param/verify_ctrl/verify_ctrl_sp1", "勾选");
			map.put("/param/verify_ctrl/verify_ctrl_sp-1", "取消勾选");
			map.put("/param/verify_ctrl1", "勾选");
			map.put("/param/verify_ctrl-1", "取消勾选");
			
			map.put("/param/a01z0/flag1", "勾选");
			map.put("/param/a01z0/flag0", "取消勾选");
			
			map.put("/param/fieldpriv1", "勾选");
			map.put("/param/fieldpriv0", "取消勾选");
			
			map.put("/param/readfield1", "勾选");
			map.put("/param/readfield0", "取消勾选");
			
			map.put("/param/royalties/valid1", "勾选");
			map.put("/param/royalties/valid0", "取消勾选");
			
			map.put("/param/piecepay/valid1", "勾选");
			map.put("/param/piecepay/valid0", "取消勾选");
			map.put("/param/piecepay/firstday1", "自然月份");
			
			map.put("/Params/ConfirmType/allow_edit_subdata1", "勾选");
			map.put("/Params/ConfirmType/allow_edit_subdata-1", "取消勾选");
			map.put("/Params/ConfirmType/subNoPriv1", "勾选");
			map.put("/Params/ConfirmType/subNoPriv0", "取消勾选");
			map.put("/Params/ConfirmType/no_show1", "勾选");
			map.put("/Params/ConfirmType/no_show0", "取消勾选");
			if("/Params/ConfirmType".equalsIgnoreCase(ul)){
				StringBuffer buf = new StringBuffer();
				if(name!=null&&name.length()!=0){
					String[] _name = name.split(";");
					for(int i=0;i<_name.length;i++){
//						System.out.println(_name);
						if(_name[i].indexOf("`")!=-1){
							String[] s = _name[i].split("`");
							FieldSet fieldset=DataDictionary.getFieldSetVo(s[0]);
							String temp = s[1];
							if("2".equals(s[1])){
								temp = ResourceFactory.getProperty("label.gz.notchange");
							}else if("0".equals(s[1])){
								temp = ResourceFactory.getProperty("label.gz.update");
							}else{
								temp = ResourceFactory.getProperty("label.gz.append");
							}
							buf.append(fieldset.getCustomdesc()+":"+temp+"\r");
						}else{
							buf.append("||"+SalaryTemplateBo.getSalarySet(_name[i])+"(累计更新)");
						}
					}					
				}
				map.put("/Params/ConfirmType"+name, buf.toString());
			}
			
			if("/param/tax_date_field".equalsIgnoreCase(ul)){
				ContentDAO dao=new ContentDAO(con);
				RowSet rs = dao.search("select itemdesc from salaryset where salaryid="+salaryid+" and itemid='"+name+"' ");
				if(rs.next()){
					map.put("/param/tax_date_field"+name, rs.getString("itemdesc"));
				}				
			}
			if("/param/declare_tax".equalsIgnoreCase(ul)){
				ContentDAO dao=new ContentDAO(con);
				RowSet rs = dao.search("select itemdesc from salaryset where salaryid="+salaryid+" and itemid='"+name+"' ");
				if(rs.next()){
					map.put("/param/declare_tax"+name, rs.getString("itemdesc"));
				}
			}
			if("/param/pay_flag".equalsIgnoreCase(ul)){
				ContentDAO dao=new ContentDAO(con);
				RowSet rs = dao.search("select itemdesc from salaryset where salaryid="+salaryid+" and itemid='"+name+"' ");
				if(rs.next()){
					map.put("/param/pay_flag"+name, rs.getString("itemdesc"));
				}
			}
			if("/param/tax_mode".equalsIgnoreCase(ul)){
				ContentDAO dao=new ContentDAO(con);
				RowSet rs = dao.search("select itemdesc from salaryset where salaryid="+salaryid+" and itemid='"+name+"' ");
				if(rs.next()){
					map.put("/param/tax_mode"+name, rs.getString("itemdesc"));
				}
			}
			if("/param/tax_desc".equalsIgnoreCase(ul)){
				ContentDAO dao=new ContentDAO(con);
				RowSet rs = dao.search("select itemdesc from salaryset where salaryid="+salaryid+" and itemid='"+name+"' ");
				if(rs.next()){
					map.put("/param/tax_desc"+name, rs.getString("itemdesc"));
				}
			}
			if("/param/flow_ctrl/sp_relation_id".equals(ul)){
				DbWizard dbw = new DbWizard(con);
				if(dbw.isExistTable("t_wf_relation",false))
				{
					ContentDAO dao=new ContentDAO(con);
					String sql="select * from t_wf_relation where validflag=1 and actor_type='4' and relation_id = '"+name+"'";
					
					RowSet rowSet=dao.search(sql);
					if(rowSet.next())
					{
						map.put("/param/flow_ctrl/sp_relation_id"+name, rowSet.getString("cname"));
					}
					if(rowSet!=null)
						rowSet.close();
				}
			}
			if("/param/flow_ctrl/default_filterid".equals(ul)){
	            StringBuffer buf=new StringBuffer();
	            buf.append("select id,chz,cfldname,scope,username from gzitem_filter where ");
	            buf.append("id='"+name+"'");
	            buf.append(" and scope =0 order by norder ");//共享的
	            ContentDAO dao=new ContentDAO(con);
	            RowSet rset=dao.search(buf.toString());
				if(rset.next())
				{
					map.put("/param/flow_ctrl/default_filterid"+name, rset.getString("chz"));
				}
			}
			if("/param/sum_field/orgid".equals(ul)){
				map.put("/param/sum_field/orgid"+name,SalaryTemplateBo.getSalarySet(name));
			}
			if("/param/sum_field/deptid".equals(ul)){
				map.put("/param/sum_field/deptid"+name,SalaryTemplateBo.getSalarySet(name));
			}
			if("/param/note".equals(ul)){
				String sql = "select id,name from email_name where nmodule=5 and id='"+name+"'";
				ContentDAO dao = new ContentDAO(con);
				RowSet rs = dao.search(sql);
				if(rs.next()){
					map.put("/param/note"+name,rs.getString("name"));
				}
			}
			if("/Params/hidden_items/hidden_item".equals(ul)){
				map.put("/Params/hidden_items/hidden_item"+name,SalaryTemplateBo.getSalarySet(name));
			}
			if("/param/royalties/date".equals(ul)){
				map.put("/param/royalties/date"+name,SalaryTemplateBo.getSalarySet(name));
			}
			if("/param/royalties/relation_fields".equals(ul)){
				map.put("/param/royalties/relation_fields"+name,SalaryTemplateBo.getSalarySet(name));
			}
			if("/param/royalties/period".equals(ul)){
				if("1".equals(name))map.put("/param/royalties/period"+name,"月");
				else if("2".equals(name))map.put("/param/royalties/period"+name,"季");
				else if("3".equals(name))map.put("/param/royalties/period"+name,"半年");
				else map.put("/param/royalties/period"+name,"年");
			}
			if("/param/piecepay/period".equals(ul)){
				if("1".equals(name))map.put("/param/piecepay/period"+name,"月");
				else if("2".equals(name))map.put("/param/piecepay/period"+name,"季");
				else if("3".equals(name))map.put("/param/piecepay/period"+name,"半年");
				else map.put("/param/piecepay/period"+name,"年");
			}
			if("/param/piecepay/relation_field".equals(ul)){
				if(name!=null&&name.length()>0){
					name = PubFunc.keyWord_reback(name);
					StringBuffer context = new StringBuffer();
					String[] fields = name.split(",");
					for(int i=0;i<fields.length;i++){
						String[] field = fields[i].split("=");
						context.append(SalaryTemplateBo.getSalarySet(field[0]));
						context.append("=");
						context.append(SalaryTemplateBo.getSalarySet(field[1]));
						context.append(",");
					}
					map.put("/param/piecepay/relation_field"+name,context.toString());
				}				
			}
			
			str=(String) map.get(ul+name);
			str = str==null||str.length()==0?name:str;
		}catch(Exception e){
			e.printStackTrace();
		}
		return str;
	}
	public String getSalaryName(String salaryid){
		String str = "";
		try{
			ContentDAO dao=new ContentDAO(conn);
			String sql="select cname from SALARYTEMPLATE where SALARYID="+salaryid+"";
			RowSet rs = dao.search(sql);
			if(rs.next()){
				str = rs.getString("cname");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return str;
	}
	public static String getSalarySet(String value){
		StringBuffer str = new StringBuffer();
		if(value==null||value.length()==0){
			return str.toString();
		}
		try{
			String[] values = value.split(",");
			for(int i=0;i<values.length;i++){
				if(values[i]!=null&&values[i].length()>0){
					FieldItem item = DataDictionary.getFieldItem(values[i]);
					str.append(item==null?values[i]:item.getItemdesc());
					if(i!=values.length-1)
						str.append(",");
				}
			}			
		}catch(Exception e){
			e.printStackTrace();
		}
		return str.toString();		
	}
	/**
	 * 根据业务用户名取得关联的自助用户名 zhaoxg add 2015-5-22
	 * @param name
	 * @return
	 */
	public String getZizhuUsername(String name){
		StringBuffer str = new StringBuffer();
		try{
			ContentDAO dao=new ContentDAO(conn);
			String sql = "select UserName from UsrA01 where A0100 in (select A0100 from OperUser where UserName='"+name+"')";
			RowSet rs = dao.search(sql);
			if(rs.next()){
				str.append(rs.getString("UserName"));
			}
			if(str.length()==0){
				str.append(name);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return str.toString();
	}
	/**
	 * 权限限制sql语句
	 * @param role
	 * @param privCode
	 * @param privCodeValue
	 * @param tablename
	 * @return
	 */
	public String getPrivSQL(String role,String tablename,String salaryid,String b_units)
	{
		StringBuffer buf = new StringBuffer("");
		String[] temp = salaryid.split(",");
		if("1".equals(role))//如果是树节点传进来的，那么此处role可传空  role=1 代表超级用户 if(this.userView.isSuper_admin()||this.userView.getGroupId().equals("1"))
		{
			buf.append( "  1=1 ");
		}else
		{			
			
	     	HashMap map = new HashMap();
			for (int j= 0; j < temp.length; j++){
				String b0110_item="b0110";
				String e0122_item="e0122";
				SalaryCtrlParamBo ctrlparam=new SalaryCtrlParamBo(this.conn,Integer.parseInt(temp[j])); 
				String orgid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD, "orgid"); //归属单位
				String deptid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid");//归属部门
				if(deptid!=null&&deptid.trim().length()>0)//设置了归属部门
				{ 
					e0122_item=deptid;
					if(orgid!=null&&orgid.length()>0)
						b0110_item=orgid;
				}else if(orgid!=null&&orgid.trim().length()>0)//没设置归属部门，只设置了归属单位，走归属单位
				{ 
					b0110_item=orgid;
				}
				String item = (String) map.get(e0122_item+"/"+b0110_item);
		    	if(item!=null&&item.length()>0){
		    		map.put(e0122_item+"/"+b0110_item, item+",'"+temp[j]+"'");
		    	}else{
		    		map.put(e0122_item+"/"+b0110_item, "'"+temp[j]+"'");
		    	}	

			}			
			if(b_units!=null&&b_units.length()>2&&!"UN".equalsIgnoreCase(b_units)&&!"UN`".equalsIgnoreCase(b_units)) //模块操作单位
			{
				String unitarr[] =b_units.split("`");				
				Iterator iter = map.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					Object key = entry.getKey();
					Object val = entry.getValue();
					String[] str = key.toString().split("/");
					buf.append("((");
					for(int i=0;i<unitarr.length;i++)
					{
	    				String codeid=unitarr[i];
	    				if(codeid==null|| "".equals(codeid))
	    					continue;
		    			if(codeid!=null&&codeid.trim().length()>2)
	    				{
		    				String privCode = codeid.substring(0,2);
		    				String privCodeValue = codeid.substring(2);							  
							if(privCode!=null&&!"".equals(privCode))
							{		
								buf.append(" ( case");
								if(!"e0122".equalsIgnoreCase(str[0])&&!"b0110".equalsIgnoreCase(str[1])){//归属单位和部门均设置了
									buf.append("  when  nullif("+tablename+str[0]+",'') is not null  then "+tablename+str[0]+" ");
									buf.append("  when (nullif("+tablename+str[0]+",'') is  null ) and nullif("+tablename+str[1]+",'') is not null then "+tablename+str[1]+" ");
									buf.append("  when (nullif("+tablename+str[0]+",'') is  null ) and (nullif("+tablename+str[1]+",'') is null) and nullif("+tablename+"e0122,'') is not null then "+tablename+"e0122 ");
									buf.append(" else "+tablename+"b0110 end ");
									buf.append(" like '"+privCodeValue+"%' ");
									buf.append(") or");
								}else if(!"e0122".equalsIgnoreCase(str[0])&& "b0110".equalsIgnoreCase(str[1])){//设置了归属部门，没设置归属单位
									buf.append("  when nullif("+tablename+str[0]+",'') is not null then "+tablename+str[0]+" ");
									buf.append("  when (nullif("+tablename+str[0]+",'') is  null) and nullif("+tablename+"e0122,'') is not null then "+tablename+"e0122 ");
									buf.append(" else "+tablename+"b0110 end ");
									buf.append(" like '"+privCodeValue+"%' ");
									buf.append(") or");
								}else if("e0122".equalsIgnoreCase(str[0])&&!"b0110".equalsIgnoreCase(str[1])){//没设置归属部门，设置了归属单位
									buf.append("  when nullif("+tablename+str[1]+",'') is not null then "+tablename+str[1]+" ");
									buf.append("  when (nullif("+tablename+str[1]+",'') is null) and nullif("+tablename+"e0122,'') is not null then "+tablename+"e0122 ");
									buf.append(" else "+tablename+"b0110 end ");
									buf.append(" like '"+privCodeValue+"%' ");
									buf.append(") or");
								}else if("e0122".equalsIgnoreCase(str[0])&& "b0110".equalsIgnoreCase(str[1])){//啥都没设置
									buf.append("  when nullif("+tablename+"e0122,'') is not null then "+tablename+"e0122 ");
									buf.append(" else "+tablename+"b0110 end ");
									buf.append(" like '"+privCodeValue+"%' ");
									buf.append(") or");
								}
							}
	    				}
					}
					String _str = buf.toString();
					buf.setLength(0);
					buf.append(_str.substring(0, _str.length()-3));
					buf.append(")) or");
				}
				String str = buf.toString();
				buf.setLength(0);
				buf.append("("+str.substring(0, str.length()-3)+")");
			}else if("UN`".equalsIgnoreCase(b_units)){
				buf.append( "  1=1 ");
			}
			else
			{
				buf.append( "  1=2 ");
			}
		}
		return buf.toString();
	}
	
	/**
	 * 创建薪资导入模板数据临时表
	 * @param tablename
	 * @throws GeneralException
	 */
	public void createImportTable(String tablename) throws GeneralException {
		try
		{
			DbWizard dbw=new DbWizard(this.conn);
			Table table=new Table(tablename);
		//	if(dbw.isExistTable(tablename, false))
			{
				dbw.dropTable(table);
			}
			
			Field field=new Field("NBASE","NBASE");
			field.setDatatype(DataType.STRING);
			field.setLength(3);
			field.setNullable(false);
			field.setKeyable(true);
			table.addField(field);
			
			//人员编号
			field=new Field("A0100","A0100");
			field.setDatatype(DataType.STRING);
			field.setLength(20);
			field.setNullable(false);
			field.setKeyable(true);	
			table.addField(field);

			//姓名
			field=new Field("A0101","A0101");
			field.setDatatype(DataType.STRING); 
			field.setLength(DataDictionary.getFieldItem("a0101").getItemlength());
			field.setLength(20);
			field.setNullable(false);
			field.setKeyable(true);
			table.addField(field);	
			
			//归属日期
			field=new Field("A00Z0","A00Z0");
			field.setDatatype(DataType.DATE);
			field.setLength(20);
			field.setNullable(false);
			field.setKeyable(true);
			table.addField(field);
			
			//归属次数
			field=new Field("A00Z1","A00Z1");
			field.setDatatype(DataType.INT);
			field.setNullable(false);
			field.setLength(20);
			field.setKeyable(true);
			table.addField(field);
			
			//部门
			field=new Field("B0110","B0110");
			field.setDatatype(DataType.STRING);
			field.setLength(30);			
			table.addField(field);	
			
			//单位
			field=new Field("E0122","E0122");
			field.setDatatype(DataType.STRING);
			field.setLength(30);			
			table.addField(field);	
			
			//人员库id
			field=new Field("dbid","dbid");
			field.setDatatype(DataType.INT);
			field.setLength(8);			
			table.addField(field);	
			
			//人员排序
			field=new Field("A0000","A0000");
			field.setDatatype(DataType.INT);
			field.setLength(10);			
			table.addField(field);	
			
			dbw.createTable(table);

		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	
	public ArrayList getGzitemlist()
	{
		return this.gzitemlist;
	}
}
