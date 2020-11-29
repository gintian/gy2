package com.hjsj.hrms.module.gz.salarytype.businessobject;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryPkgBo;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.RowSetToXmlBuilder;
import com.hjsj.hrms.businessobject.sys.logonuser.UserObjectBo;
import com.hjsj.hrms.interfaces.general.PendingTask;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryAccountBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.module.gz.salarytemplate.businessobject.ProcessMonitorBo;
import com.hjsj.hrms.module.gz.salarytype.businessobject.templateset.DownLoadXml;
import com.hjsj.hrms.module.gz.tax.businessobject.TaxMxBo;
import com.hjsj.hrms.module.gz.utils.SalarySetBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.upload.FormFile;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.*;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * 薪资类别 工具类
 * @createtime July 02, 2015 9:07:55 PM
 * @author chent
 */
public class SalaryTypeBo {
	
	// 基本属性
	private Connection conn = null;
	private UserView userview;
	
	/**
	 * 构造函数
	 * @param conn
	 * @param userview
	 */
	public SalaryTypeBo(Connection conn, UserView userview){
		this.conn = conn;
		this.userview=userview;
	}
	
	/**
	 * 获取列头、表格渲染
	 * @param viewtype 页面区分 0:薪资发放  1:审批  2:上报
	 * @param imodule 0:薪资  1:保险
	 * @return
	 */
	public ArrayList<ColumnsInfo> getColumnList(String imodule){
		
		/** 获取类型名称 */
		//String str = ResourceFactory.getProperty("gz.report.salary");//薪资
		/*if ("1".equals(imodule)){
			str = ResourceFactory.getProperty("gz.report.welfare");//保险
		}*/
		
		ArrayList<ColumnsInfo> columnsInfo = new ArrayList<ColumnsInfo>();
		/** 显示 */
		// 编号
		ColumnsInfo numColumn = this.getColumnsInfo("salaryid", ResourceFactory.getProperty("report.number"), 40);
		numColumn.setEditableValidFunc("false");
		columnsInfo.add(numColumn);
		
		// 薪资类别名称
		ColumnsInfo cnameColumn = this.getColumnsInfo("cname","类别名称", 300);
		if(((!"1".equals(imodule)&&this.userview.hasTheFunction("3240804"))||("1".equals(imodule)&&this.userview.hasTheFunction("3250504")))){
			cnameColumn.setAllowBlank(false);
			cnameColumn.setReadOnly(false);
			cnameColumn.setCodesetId("0");
			cnameColumn.setColumnType("A");// 类型N|M|A|D
			cnameColumn.setColumnLength(30);
		}else
			cnameColumn.setEditableValidFunc("false");
		columnsInfo.add(cnameColumn);
		
		// 属性
		ColumnsInfo property = getColumnsInfo("property", ResourceFactory.getProperty("label.gz.property"), 90);
		if((!"1".equals(imodule)&&this.userview.hasTheFunction("3240808"))||("1".equals(imodule)&&this.userview.hasTheFunction("3250508"))){
			property.setRendererFunc("salarytype_me.property");
		}else
			property.setLoadtype(ColumnsInfo.LOADTYPE_HIDDEN);
		property.setTextAlign("center");
		property.setEditableValidFunc("false");
		columnsInfo.add(property);

		// 临时变量
		ColumnsInfo tmpVar = getColumnsInfo("compare", ResourceFactory.getProperty("label.gz.variable"), 90);
		if((!"1".equals(imodule)&&this.userview.hasTheFunction("3240809"))||("1".equals(imodule)&&this.userview.hasTheFunction("3250509"))){
			tmpVar.setRendererFunc("salarytype_me.tmpVar");
		}else
			tmpVar.setLoadtype(ColumnsInfo.LOADTYPE_HIDDEN);
		tmpVar.setTextAlign("center");
		tmpVar.setEditableValidFunc("false");
		columnsInfo.add(tmpVar);

		// 计算公式
		ColumnsInfo countFormula = getColumnsInfo("compare", ResourceFactory.getProperty("gz.premium.countformula"), 90);
		if((!"1".equals(imodule)&&this.userview.hasTheFunction("3240810"))||("1".equals(imodule)&&this.userview.hasTheFunction("3250510"))){
			countFormula.setRendererFunc("salarytype_me.countFormula");
		}else
			countFormula.setLoadtype(ColumnsInfo.LOADTYPE_HIDDEN);
		countFormula.setTextAlign("center");
		countFormula.setEditableValidFunc("false");
		columnsInfo.add(countFormula);

		// 审核公式
		ColumnsInfo approvalFormula = getColumnsInfo("compare", ResourceFactory.getProperty("label.gz.shformula"), 90);
		if((!"1".equals(imodule)&&this.userview.hasTheFunction("3240812"))||("1".equals(imodule)&&this.userview.hasTheFunction("3250512"))){
			approvalFormula.setRendererFunc("salarytype_me.approvalFormula");
		}else
			approvalFormula.setLoadtype(ColumnsInfo.LOADTYPE_HIDDEN);
		approvalFormula.setTextAlign("center");
		approvalFormula.setEditableValidFunc("false");
		columnsInfo.add(approvalFormula);

		// 所属单位
		ColumnsInfo subordinateUnits = getColumnsInfo("subordinateunits", "所属单位", 120);
		if((!"1".equals(imodule)&&this.userview.hasTheFunction("3240815"))||("1".equals(imodule)&&this.userview.hasTheFunction("3250515"))){
			subordinateUnits.setRendererFunc("salarytype_me.subordinateUnits");
		}else
			subordinateUnits.setLoadtype(ColumnsInfo.LOADTYPE_HIDDEN);
		subordinateUnits.setTextAlign("left");
		subordinateUnits.setEditableValidFunc("false");
		columnsInfo.add(subordinateUnits);
		
		//应用机构
		ColumnsInfo appOrganization = getColumnsInfo("apporganization", ResourceFactory.getProperty("label.gz.appOrganization"), 90);
		if((!"1".equals(imodule)&&this.userview.hasTheFunction("3240816"))||("1".equals(imodule)&&this.userview.hasTheFunction("3250516"))){
			appOrganization.setRendererFunc("salarytype_me.salaryAppOrganization");
		}else
			appOrganization.setLoadtype(ColumnsInfo.LOADTYPE_HIDDEN);
		appOrganization.setTextAlign("center");
		appOrganization.setEditableValidFunc("false");
		columnsInfo.add(appOrganization);
		
		// 薪资项目
		ColumnsInfo salaryProject = getColumnsInfo("dealto", ResourceFactory.getProperty("label.gz.gzitem"), 90);
		if((!"1".equals(imodule)&&this.userview.hasTheFunction("3240811"))||("1".equals(imodule)&&this.userview.hasTheFunction("3250511"))){
			salaryProject.setRendererFunc("salarytype_me.salaryProject");
		}else
			salaryProject.setLoadtype(ColumnsInfo.LOADTYPE_HIDDEN);
		salaryProject.setTextAlign("center");
		salaryProject.setEditableValidFunc("false");
		columnsInfo.add(salaryProject);
		
		/** 隐藏 */
		// 编号
		ColumnsInfo salaryid_safe = getColumnsInfo("salaryid_safe", ResourceFactory.getProperty("report.number")+ResourceFactory.getProperty("gz_new.gz_accounting.safe"), 0);
		salaryid_safe.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);// 不显示
		columnsInfo.add(salaryid_safe);
		// 薪资保险区分
		ColumnsInfo imodule_safe = getColumnsInfo("imodule_safe", "imodule" + ResourceFactory.getProperty("gz_new.gz_accounting.safe"), 0);
		imodule_safe.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);// 不显示
		columnsInfo.add(imodule_safe);
		//排序号
		ColumnsInfo seq = getColumnsInfo("seq", "seq" , 0);
		seq.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);// 不显示
		columnsInfo.add(seq);
		//是否满足所属单位控制
		ColumnsInfo ishave = getColumnsInfo("ishave", "ishave" , 0);
		ishave.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);// 不显示
		columnsInfo.add(ishave);
		//所属单位的单位代码
		ColumnsInfo workunits = getColumnsInfo("workunits", "workunits", 0);
		workunits.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);// 不显示
		columnsInfo.add(workunits);
		
		return columnsInfo;
	}
	/**
	 * 取得权限范围的薪资列表 列表中存放是的LazyBean
	 * @param imodule  0:薪资  1:保险 
	 * @param valuesList 快速查询检索条件
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList<LazyDynaBean> getDataList(String imodule, ArrayList<String> valuesList) throws GeneralException{ 
		
		ContentDAO dao = new ContentDAO(this.conn);
		ArrayList<LazyDynaBean> list = new ArrayList<LazyDynaBean>();
		StringBuffer buf = new StringBuffer();
		try {
			//----------------------把排序字段为null的数据处理了，原有seq最大值加上薪资类别号；这样改保证了不会重复也不会相隔特别大  zhaoxg add2016-5-11------
			RowSet rs = dao.search("select max("+Sql_switcher.isnull("seq", "0")+") from salarytemplate");
			String seq = "0";
			if(rs.next()){
				seq = rs.getString(1);
			}
			dao.update("update salarytemplate set seq = salaryid+"+seq+" where seq is null ");
			//------------------------end---------------------------------
			DbWizard dbw = new DbWizard(this.conn);
			if(!dbw.isExistField("salarytemplate", "b0110", false)){
				Table table=new Table("salarytemplate");
				Field field=new Field("b0110","b0110");
				field.setDatatype(DataType.STRING);
				field.setLength(50);
				table.addField(field);	
				dbw.addColumns(table);
				
				DBMetaModel dm=new DBMetaModel(this.conn);
				dm.reloadTableModel("salarytemplate");
			}
			ArrayList<String> sqlList = new ArrayList<String>();
		 	buf.append("select salaryid,cname,cbase,seq,cond,b0110 from salarytemplate "); 
			if ("0".equals(imodule)){// 薪资类别
				buf.append(" where (cstate is null or cstate='')");
			}else {
				buf.append(" where cstate='1'");// 险种类别
			}
			// 快速查询
			for(int i = 0; i < valuesList.size(); i++){
				String queryVal = valuesList.get(i);
				queryVal = SafeCode.decode(queryVal);
				if(this.isInteger(queryVal)){
					if(i == 0){
						buf.append(" and (");
					}else{
						buf.append(" or ");
					}
					buf.append("(salaryid=? or cname like ?)");
					sqlList.add(queryVal);
					sqlList.add("%"+queryVal+"%");
				}else{
					if(i == 0){
						buf.append(" and (");
					}else{
						buf.append(" or ");
					}
					buf.append("cname like ?");
					sqlList.add("%"+queryVal+"%");
				}
				
				if(i == valuesList.size()-1) {
					buf.append(")");
				}
			}
			buf.append(" order by seq");
			RowSet rset = dao.search(buf.toString(), sqlList);
			String unitcodes=this.userview.getUnitIdByBusi("1");  //UM010101`UM010105` 
			String[] units = unitcodes.split("`");
			while(rset.next()){
				// 加上权限过滤 
				if ("0".equals(imodule)){
					if (!this.userview.isHaveResource(IResourceConstant.GZ_SET, rset.getString("salaryid")))
						continue;
				}else {
					if (!this.userview.isHaveResource(IResourceConstant.INS_SET, rset.getString("salaryid")))
						continue;
				}
				LazyDynaBean lazyvo = new LazyDynaBean();
				String salaryid = rset.getString("salaryid");
				lazyvo.set("salaryid", salaryid);
				lazyvo.set("salaryid_safe", SafeCode.encode(PubFunc.encrypt(salaryid)));
				lazyvo.set("seq", rset.getString("seq") != null ? rset.getString("seq") : "0");
				String cname = rset.getString("cname"); 
				String cond = Sql_switcher.readMemo(rset, "cond");
				String cbase = rset.getString("cbase");
				// 对条件进行转换,转成用户可阅读的格式 
				lazyvo.set("domain", "["+cbase+"]:["+cond+"]");
				lazyvo.set("cname", cname);
				
				/** 自动升级薪资类别 */
				syncSalaryStruct(rset.getInt("salaryid"));
				
				lazyvo.set("imodule_safe", SafeCode.encode(PubFunc.encrypt(imodule)));
				
				String ishave = "1";
				String b0110 = rset.getString("b0110");
				if(StringUtils.isBlank(b0110)||this.userview.isSuper_admin()|| "un`".equalsIgnoreCase(unitcodes)){//超级用户或者全部或具有全部权限则可以控制
					ishave = "0";
				}else{
					for(int i=0;i<units.length;i++)
					{
	    				String codeid=units[i];
	    				if(codeid==null|| "".equals(codeid))
	    					continue;
		    			if(codeid!=null&&codeid.trim().length()>2)
	    				{
		    				String privCode = codeid.substring(0,2);
		    				String privCodeValue = codeid.substring(2);
		    				if(b0110.length()>=privCodeValue.length()){//人的范围要大于等于操作单位才行，所以操作单位长
		    					if(b0110.substring(0, privCodeValue.length()).equalsIgnoreCase(privCodeValue)){
		    						ishave = "0";
		    					}
		    				}
	    				}
					}
				}
				lazyvo.set("ishave", ishave);//是否满足所属单位控制  0：满足 1：不满足  zhaoxg add 2016-12-13
				lazyvo.set("workunits", StringUtils.isBlank(b0110)?"":PubFunc.encrypt("UN"+b0110));//将所属单位的单位传入前台一边选人控件使用
				lazyvo.set("subordinateunits", StringUtils.isBlank(b0110)?"全部":(StringUtils.isBlank(AdminCode.getCodeName("UN",b0110))?AdminCode.getCodeName("UM",b0110):AdminCode.getCodeName("UN",b0110)));
				if("0".equals(ishave))//满足的显示，不满足的就不显示了  zhaoxg add 2016-12-30
					list.add(lazyvo);
			}
			if (rset != null){
				rset.close();
			}
		}catch (Exception ex){
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		
		return list;
	}
	/**
	 * 获取功能按钮
	 * @return
	 * @throws GeneralException 
	 */
	public ArrayList<ButtonInfo> getButtonList(String returnvalue,HashMap map) throws GeneralException{
		
		ArrayList<ButtonInfo> buttonList = new ArrayList<ButtonInfo>();
		VersionControl ver = new VersionControl();
		String imodule = (String) map.get("imodule");// 0:薪资  1:保险
		try{
			// 新增
			if ((this.userview.hasTheFunction("3240801")&&"0".equals(imodule)) || (this.userview.hasTheFunction("3250501")&&"1".equals(imodule))){
				ButtonInfo newButton = new ButtonInfo(ResourceFactory.getProperty("button.new.add"), "salarytype_me.addSalaryType");
				buttonList.add(newButton);
			}
			// 删除
			if ((this.userview.hasTheFunction("3240802")&&"0".equals(imodule)) || (this.userview.hasTheFunction("3250502")&&"1".equals(imodule))){
				ButtonInfo delButton = new ButtonInfo(ResourceFactory.getProperty("button.delete"), "salarytype_me.deleteSalaryType");
				buttonList.add(delButton);
			}
			// 另存为
			if ((this.userview.hasTheFunction("3240803")&&"0".equals(imodule)) || (this.userview.hasTheFunction("3250503")&&"1".equals(imodule))){
				ButtonInfo saveButton = new ButtonInfo(ResourceFactory.getProperty("button.other_save"), "salarytype_me.saveAs");
				buttonList.add(saveButton);
			}
			
			// 导出
			if ((this.userview.hasTheFunction("3240805")&&"0".equals(imodule)) || (this.userview.hasTheFunction("3250505")&&"1".equals(imodule))){
				ButtonInfo exportButton = new ButtonInfo(ResourceFactory.getProperty("button.export"), "salarytype_me.exportZip");
				buttonList.add(exportButton);
			}
			// 导入
			if ((this.userview.hasTheFunction("3240805")&&"0".equals(imodule)) || (this.userview.hasTheFunction("3250505")&&"1".equals(imodule))){
				ButtonInfo importButton = new ButtonInfo(ResourceFactory.getProperty("button.import"), "salarytype_me.importZip");
				buttonList.add(importButton);
			}
			// 币种维护
			if ((this.userview.hasTheFunction("3240806")&&"0".equals(imodule)) 
					//|| (this.userview.hasTheFunction("3250506")&&"1".equals(imodule))
				){
				ButtonInfo moneyTypeButton = new ButtonInfo(ResourceFactory.getProperty("button.money"), "salarytype_me.moneyMaintenance");
				buttonList.add(moneyTypeButton);
			}
			// 历史数据初始化
			if ((this.userview.hasTheFunction("3240807")&&"0".equals(imodule)) || (this.userview.hasTheFunction("3250507")&&"1".equals(imodule))){
				ButtonInfo initButton = new ButtonInfo(ResourceFactory.getProperty("button.gzdata.init"), "salarytype_me.historyDataInit");
				buttonList.add(initButton);
			}
			// 结构同步
			if ((this.userview.hasTheFunction("3240814")&&"0".equals(imodule)) || (this.userview.hasTheFunction("3250514")&&"1".equals(imodule))){
				ButtonInfo SynchronousButton = new ButtonInfo(ResourceFactory.getProperty("button.gzdata.synchronize"), "salarytype_me.structSynchro");
				buttonList.add(SynchronousButton);
			}
			// 返回
			if(!"1".equals(returnvalue)){
				ButtonInfo returnButton = new ButtonInfo(ResourceFactory.getProperty("button.return"), "");
				buttonList.add(returnButton);
			}

		}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		return buttonList;
	}
	/**
	 * 列头ColumnsInfo对象初始化
	 * @param columnId id
	 * @param columnDesc 名称
	 * @param columnDesc 显示列宽
	 * @return
	 */
	private ColumnsInfo getColumnsInfo(String columnId, String columnDesc, int columnWidth){
		
		ColumnsInfo columnsInfo = new ColumnsInfo();
		columnsInfo.setColumnId(columnId);
		columnsInfo.setColumnDesc(columnDesc);
		columnsInfo.setCodesetId("");// 指标集
		columnsInfo.setColumnType("M");// 类型N|M|A|D
		columnsInfo.setColumnWidth(columnWidth);//显示列宽
		columnsInfo.setColumnLength(100);// 显示长度 
		columnsInfo.setDecimalWidth(0);// 小数位
		columnsInfo.setAllowBlank(true);// 编辑时是否可以为空
		columnsInfo.setReadOnly(true);// 是否只读
		columnsInfo.setFromDict(false);// 是否从数据字典里来
		columnsInfo.setLocked(false);//是否锁列
		
		return columnsInfo;
	}
	/**
	 * 同步薪资类别，自助升级
	 * @param salaryid
	 * @throws GeneralException 
	 */
	private void syncSalaryStruct(int salaryid) throws GeneralException{
		
		if (!isNewStruct(salaryid)){
			try {
				/** 未升过级,增加字段 */
				ContentDAO dao = new ContentDAO(this.conn);
				int fieldid = 0; int sortid = 0;
				RowSet rowSet = dao.search("select max(fieldid),max(sortid) from salaryset where salaryid=" + salaryid);
				if (rowSet.next()){
					fieldid = rowSet.getInt(1);
					sortid = rowSet.getInt(2);
				}	
				
				ArrayList<RecordVo> list = new ArrayList<RecordVo>();
				list.add(getSalarySetRecordvo(salaryid, fieldid+1, "A00", "A00Z2", ResourceFactory.getProperty("gz_new.gz_accounting.send_time"), 20, 0, "0", sortid + 1, 10, "", 3, 1, "D"));
				list.add(getSalarySetRecordvo(salaryid, fieldid+2, "A00", "A00Z3", ResourceFactory.getProperty("label.gz.count"), 15, 0, "0", sortid + 2, 10, "", 3, 1, "N"));
				dao.addValueObject(list);
				/** 更新名称A00Z0归属日期,A00Z1归属次数 */
				list.clear();
				
				RecordVo vo = new RecordVo("salaryset");
				vo.setInt("salaryid", salaryid);
				vo.setString("itemdesc", ResourceFactory.getProperty("gz.columns.a00z0"));
				vo.setString("itemid", "A00Z0");
				list.add(vo);
				vo = new RecordVo("salaryset");
				vo.setInt("salaryid", salaryid);
				vo.setString("itemdesc", ResourceFactory.getProperty("gz.columns.a00z1"));
				vo.setString("itemid", "A00Z1");
				list.add(vo);
				dao.updateValueObject(list);
			}catch (Exception ex){
				ex.printStackTrace();
				throw GeneralExceptionHandler.Handle(ex);
			}
		}
	}
	
	/**
	 * 薪资类别是否为最近结构
	 * @param salaryid
	 * @return
	 * @throws GeneralException 
	 */
	private boolean isNewStruct(int salaryid) throws GeneralException{
		
		boolean bflag = false;
		StringBuffer buf = new StringBuffer();
		buf.append("select salaryid from salaryset where itemid = ? and salaryid = ?");
		ArrayList<Object> paralist = new ArrayList<Object>();
		paralist.add("A00Z2");
		paralist.add(Integer.valueOf(salaryid));
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			RowSet rset = dao.search(buf.toString(), paralist);
			if (rset.next()){
				bflag = true;
			}
			if (rset != null){
				rset.close();
			}
		}catch (Exception ex){
			ex.printStackTrace();
			bflag = false;
			throw GeneralExceptionHandler.Handle(ex);
		}
		
		return bflag;
	}
	private RecordVo getSalarySetRecordvo(int salaryid, int fieldid, String fieldsetid, String itemid, String itemdesc,
			int itemlength, int decwidth, String codesetid, int sortid, int nwidth, String formula, int initflag, int nlock, String itemtype){
		
		RecordVo vo = new RecordVo("salaryset");
		vo.setInt("salaryid", salaryid);
		vo.setInt("fieldid", fieldid);
		vo.setString("fieldsetid", fieldsetid);
		vo.setString("itemid", itemid.toUpperCase());
		vo.setString("itemdesc", itemdesc);
		vo.setInt("itemlength", itemlength);
		vo.setInt("decwidth", decwidth);
		vo.setString("codesetid", codesetid);
		vo.setInt("sortid", sortid);
		vo.setInt("nwidth", nwidth);
		vo.setString("formula", formula);
		vo.setInt("initflag", initflag);
		vo.setInt("nlock", nlock);
		vo.setString("itemtype", itemtype);
		
		return vo;
	}
	
	public String getValByStr(String url, String str) throws GeneralException{
		
		String val = "";
		try {
			url = url.substring(1);
			if(url.indexOf("encryptParam")!=-1)
			{
				url = url.replaceAll("b_query=link&encryptParam=", "");
				url = PubFunc.decrypt(url);
			}
			else
				url=url.replaceAll("b_query=link", "");
			int index = url.indexOf(str +"=");
			int startIndex = index + str.length() + 1;
			int endIndex = index + str.length() + 2;
			val = url.substring(startIndex, endIndex);
		}catch (Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		return val;
	}
	
	/**
     * 整型判断
     * @param str
     * @return
     */
    public boolean isInteger(String str){
     if(str==null )
      return false;
     Pattern pattern = Pattern.compile("[0-9]+");
     return pattern.matcher(str).matches();
    }
    
    /**
     * @author lis
     * @Description: 同步薪资类别里的薪资项
     * @date 2015-10-21
     * @param salaryid 薪资类别id
     * @throws GeneralException
     */
	public String synchronismSalarySet(int salaryid) throws GeneralException
	{
		RowSet rowSet = null;
		RowSet rowSetFor = null;//salaryformula
		RowSet rowSetMid = null;//midvariable
		RowSet rowSetSet = null;//salaryset
		RowSet rowSetTem = null;//salarytemplate
		String errorMessage = "";
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			
			ArrayList list=new ArrayList();
			StringBuffer itemid_str=new StringBuffer("");
			StringBuffer itemMid_where=new StringBuffer("");//组装临时变量的条件
			StringBuffer itemFor_where=new StringBuffer("");
			StringBuffer itemSet_where=new StringBuffer("");
			StringBuffer itemTem_where=new StringBuffer("");
			StringBuffer item=new StringBuffer("");
			FieldItem tempItem=null;
			boolean isdifferent=true;
			//排除系统项
			String whl=" and upper(itemid) not in ('B0110','A00Z1','A00Z0','A00Z2','A00Z3','A0000','A0100','NBASE'," +
					"'A01Z0' ,'E01A1')";//,'E0122' 放开部门 zhaoxg 2016-3-5
			rowSet = dao.search("select fieldid,itemid,itemdesc,itemlength,decwidth,codesetid,itemtype from salaryset where salaryid="+salaryid+" "+whl );
			while(rowSet.next())
			{
				int fieldid=rowSet.getInt("fieldid");
				String itemid=rowSet.getString("itemid");

				tempItem=this.isNullHashMap(DataDictionary.getFieldItem(rowSet.getString("itemid")));
				if(tempItem!=null&& "1".equals(tempItem.getUseflag())){
					RecordVo vo=new RecordVo("salaryset");
					vo.setInt("salaryid",salaryid);
					vo.setInt("fieldid", fieldid);
					vo=dao.findByPrimaryKey(vo);
					isdifferent=true;
					if(!rowSet.getString("itemdesc").equalsIgnoreCase(tempItem.getItemdesc())){
						vo.setString("itemdesc", tempItem.getItemdesc());
						isdifferent=false;
						item.append(","+tempItem.getItemdesc());
						itemMid_where.append("or cvalue like '%"+rowSet.getString("itemdesc")+"%' ");
						itemFor_where.append("or rexpr like '%"+rowSet.getString("itemdesc")+"%' ");
						itemSet_where.append("or formula like '%"+rowSet.getString("itemdesc")+"%' ");
						itemTem_where.append("or cond like '%"+rowSet.getString("itemdesc")+"%' or cexpr like '%"+rowSet.getString("itemdesc")+"%' ");
					}
					if(rowSet.getInt("itemlength")!=tempItem.getItemlength()){
						vo.setInt("itemlength",tempItem.getItemlength());
						isdifferent=false;
					}
					if(rowSet.getInt("decwidth")!=tempItem.getDecimalwidth()){
						vo.setInt("decwidth",tempItem.getDecimalwidth());
						isdifferent=false;
					}
					if(!rowSet.getString("codesetid").equalsIgnoreCase(tempItem.getCodesetid())){
						vo.setString("codesetid",tempItem.getCodesetid());
						isdifferent=false;
					}
					if(!rowSet.getString("itemtype").equalsIgnoreCase(tempItem.getItemtype())){
						vo.setString("itemtype",tempItem.getItemtype());
						isdifferent=false;
					}
					if(isdifferent)
						continue;
					list.add(vo);
					
				}else
				{
					itemid_str.append(",'"+itemid.toUpperCase()+"'");//oracle库分大小写，貌似指标都大写，这也改成大写即可  zhaoxg 2013-11-30
				}
			}
			
			//目的:对于修改了指标名称的，计算公式或者临时变量没有修改，在进入项目定义和薪资发放的时候给与提示 sunjian 2017-8-8
			int forCount = 0;
			int midCount = 0;
			int setCount = 0;
			int temCount = 0;
			if(!StringUtils.isBlank(itemMid_where.toString())) {
				rowSetFor = dao.search("select count(*) count from salaryformula where " + itemFor_where.toString().substring(2));
				rowSetMid = dao.search("select count(*) count from midvariable where " + itemMid_where.toString().substring(2));
				rowSetSet = dao.search("select count(*) count from salaryset where " + itemSet_where.toString().substring(2));
				rowSetTem = dao.search("select count(*) count from salarytemplate where " + itemTem_where.toString().substring(2));
				
				while(rowSetFor.next()) {
					forCount = rowSetFor.getInt("count");
				}
				while(rowSetMid.next()) {
					midCount = rowSetMid.getInt("count");
				}
				while(rowSetSet.next()) {
					setCount = rowSetSet.getInt("count");
				}
				while(rowSetTem.next()) {
					temCount = rowSetTem.getInt("count");
				}
				if(forCount != 0 || midCount != 0 || setCount != 0 || temCount != 0) {
					errorMessage = "由于您修改了"+item.substring(1)+"指标名称，请修改对应的计算公式";
				}
			}
			if(itemid_str.length()>0)
				dao.delete("delete from salaryset where salaryid="+salaryid+"  and upper(itemid) in ("+itemid_str.substring(1)+")",new ArrayList());
			if(list.size()>0)
				dao.updateValueObject(list);

		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(rowSet);
			PubFunc.closeResource(rowSetFor);
			PubFunc.closeResource(rowSetMid);
			PubFunc.closeResource(rowSetSet);
			PubFunc.closeResource(rowSetTem);
		}
		return errorMessage;
	}
	private FieldItem isNullHashMap(Object obj){
		return obj==null?null:(FieldItem)obj;
	}
	
	/**
	 * @author lis
	 * @Description: 新增工资项目
	 * @date 2015-10-21
	 * @param fielditemIDs 薪资项目id串
	 * @param salaryid 薪资类别id
	 * @throws GeneralException
	 */
	public void saveSalarySet(String fielditemIDs,int salaryid)throws GeneralException
	{
		RowSet rowSet = null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			int fieldid=0; int sortid=0;
			rowSet = dao.search("select max(fieldid),max(sortid) from salaryset where salaryid=?",Arrays.asList(salaryid));
			if(rowSet.next())
			{
				fieldid=rowSet.getInt(1);
				sortid=rowSet.getInt(2);
			}
			
			SalaryPkgBo bo=new SalaryPkgBo(this.conn,this.userview,0);
			ArrayList list=new ArrayList();
			String[] fielditemIDsArr = fielditemIDs.split("/");
			for(int i=0;i<fielditemIDsArr.length;i++)
			{
				String itemid = fielditemIDsArr[i];
				if(StringUtils.isBlank(itemid))
					continue;
				FieldItem tempItem=DataDictionary.getFieldItem(fielditemIDsArr[i].toLowerCase());
				int nwidth=20;
				if("N".equals(tempItem.getItemtype())|| "D".equals(tempItem.getItemtype()))
					nwidth=10;
				list.add(bo.getSalarySetRecordvo(salaryid,++fieldid,tempItem.getFieldsetid(),tempItem.getItemid(),tempItem.getItemdesc(),tempItem.getItemlength(),
						tempItem.getDecimalwidth(),tempItem.getCodesetid(),++sortid,nwidth,tempItem.getItemdesc(),2,0,tempItem.getItemtype()));				
			}
			dao.addValueObject(list);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(rowSet);
		}
	}
	
	/**
	 * @author lis
	 * @Description: 根据薪资类别id得到薪资名称
	 * @date 2015-10-21
	 * @param salaryid 薪资类别id
	 * @return
	 * @throws GeneralException
	 */
	public String getSalaryName(String salaryid) throws GeneralException{
		String str = "";
		RowSet rs = null;
		try{
			ContentDAO dao=new ContentDAO(conn);
			String sql="select cname from SALARYTEMPLATE where SALARYID="+salaryid+"";
			rs = dao.search(sql);
			if(rs.next()){
				str = rs.getString("cname");
			}
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(rs);
		}
		return str;
	}
	
	/**
	 * @author lis
	 * @Description:删除薪资项目
	 * @date 2015-10-21
	 * @param salarySetIDs 薪资id串
	 * @param salaryid 薪资项目id
	 * @throws GeneralException 
	 */
	public void delSalarySet(String salarySetIDs,String salaryid) throws GeneralException
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer whl=new StringBuffer("");
			String[] fielditemIDsArr = salarySetIDs.split("/");
			ArrayList list = new ArrayList();
			for(int i=0;i<fielditemIDsArr.length;i++){
				ArrayList datalist = new ArrayList();
				if(StringUtils.isBlank(fielditemIDsArr[i]))
					continue;
				else{
					datalist.add(salaryid);
					datalist.add(fielditemIDsArr[i]);
					list.add(datalist);
				}
			}
			dao.batchUpdate("delete from  salaryset where salaryid=? and fieldid=? ",list);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	/**
	 * 修改新资项目标识 
	 * @param salaryid 薪资id
	 * @param fieldid 薪资项目id
	 * @param flag  0：输入项标识  1：累计项标识  2：导入项标识
	 * @param formula   计算公式
	 * @param heapFlag  0:不累积 1：月内累积 2：季度内累积 3：年内累积 4：无条件累积 5：季度内同次累积 6：年内同次累积 7：同次累积
	 * @author dengcan
	 * @throws GeneralException
	 */
	public void updateSalarySetFlag(int salaryid,String fieldid,String flag,String formula,String heapFlag)throws GeneralException
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RecordVo vo=new RecordVo("salaryset");
			vo.setInt("salaryid",salaryid);
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
	
	/**
	 * 新建工资类别
	 * 
	 * @param name
	 *            类别名称
	 * @author dengcan
	 * @throws GeneralException
	 */
	public int addSalaryTemplate(String name,String gz_module)throws GeneralException
	{
		int salaryid = 0;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			this.controlNumberOfSalaryTemplate(gz_module,1);//判断账套数量是否达到上限
			String unitcodes=this.userview.getUnitIdByBusi("1");  //UM010101`UM010105` 
			String[] units = unitcodes.split("`");
			String b0110 = "";//默认是空，即全部
			for(int i=0;i<units.length;i++)
			{
				String codeid=units[i];
				if(codeid==null|| "".equals(codeid))
					continue;
    			if(codeid!=null&&codeid.trim().length()>2)
				{
    				b0110 = codeid.substring(2);
    				break;
				}
			}
			RowSet rowSet=dao.search("select "+Sql_switcher.isnull("max(seq)","0")+"+1 from salarytemplate");
			int seq=0;
			if(rowSet.next())
				seq=rowSet.getInt(1);
			else
				seq=1;
			RecordVo vo=new RecordVo("salarytemplate");
			salaryid = DbNameBo.getPrimaryKey("salarytemplate","salaryid",this.conn);  // 取得主键值
			vo.setInt("salaryid",salaryid);
			vo.setString("cname",name);
			vo.setString("cbase","Usr,");
			vo.setInt("nmoneyid",0);
			vo.setInt("kzero",0);
			vo.setInt("nflag",0);
			vo.setInt("seq",seq);
			vo.setString("b0110", b0110);
			vo.setString("username",this.userview.getUserName());
			if("1".equals(gz_module))
				vo.setString("cstate","1");
			dao.addValueObject(vo);
			addDefaultSalaryset(salaryid);  // 新增默认工资项目
			
			if(!(this.userview.isAdmin()&& "1".equals(this.userview.getGroupId())))
			{
				UserObjectBo user_bo=new UserObjectBo(this.conn);
				if("0".equals(gz_module))
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
		return salaryid;
	}

	/**
	 * 控制薪资账套最大数量。默认控制在200个账套
	 * @throws GeneralException
	 * @param gz_module 模块号，薪资0 保险1
	 * @param inputNum 插入数量
	 * @author ZhangHua
	 * @date 11:54 2018/1/19
	 */
	public void controlNumberOfSalaryTemplate(String gz_module,int inputNum) throws GeneralException {
		RowSet rowSet=null;
		try{
			ContentDAO dao=new ContentDAO(this.conn);
			if(StringUtils.isBlank(gz_module))
				gz_module="0";
			StringBuffer strSql=new StringBuffer("select count(1) as num from salarytemplate where ");
			strSql.append(Sql_switcher.isnull(" Cstate ","0"));
			strSql.append("=?");
			rowSet=dao.search(strSql.toString(),Arrays.asList(new Object[] {Integer.valueOf(gz_module) }));
			if(rowSet.next()){
				int maxNum=200;
				int num=rowSet.getInt("num");
				if(num+inputNum>maxNum)
					throw GeneralExceptionHandler.Handle(new Exception("账套数量上限为"+maxNum+"个，现已达到上限无法新建账套！"));
			}

		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeDbObj(rowSet);
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
			list.add(getSalarySetRecordvo(salaryid,0,"A00","NBASE",ResourceFactory.getProperty("gz_new.gz_nbase"),3,0,"0",2,0,"",3,1,"A"));//人员库标识
			list.add(getSalarySetRecordvo(salaryid,1,"A01","A0100",ResourceFactory.getProperty("a0100.label"),8,0,"0",0,0,"",3,1,"A"));//人员编号
			list.add(getSalarySetRecordvo(salaryid,2,"A01","A0000",ResourceFactory.getProperty("a0000.label"),15,0,"0",1,0,"A0100",3,1,"N"));//人员序号
			list.add(getSalarySetRecordvo(salaryid,3,"A00","A00Z2",ResourceFactory.getProperty("gz_new.gz_accounting.send_time"),20,0,"0",3,10,"A0000",3,1,"D"));//发放日期
			list.add(getSalarySetRecordvo(salaryid,4,"A00","A00Z3",ResourceFactory.getProperty("label.gz.count"),15,0,"0",4,10,"",3,1,"N"));//发放次数
			list.add(getSalarySetRecordvo(salaryid,5,"A00","A00Z0",ResourceFactory.getProperty("gz.columns.a00z0"),20,0,"0",5,0,"",3,1,"D"));//归属日期
			list.add(getSalarySetRecordvo(salaryid,6,"A00","A00Z1",ResourceFactory.getProperty("gz.columns.a00z1"),15,0,"0",6,0,"",3,1,"N"));//归属次数
			FieldItem field=DataDictionary.getFieldItem("b0110");
			list.add(getSalarySetRecordvo(salaryid,7,"A01","B0110",field.getItemdesc(),field.getItemlength(),0,"UN",7,20,"B0110",3,1,"A"));//单位名称
			field=DataDictionary.getFieldItem("E0122");
			list.add(getSalarySetRecordvo(salaryid,8,"A01","E0122",field.getItemdesc(),field.getItemlength(),0,"UM",8,20,"E0122",3,1,"A"));//部门
			field=DataDictionary.getFieldItem("a0101");
			list.add(getSalarySetRecordvo(salaryid,9,"A01","A0101",field.getItemdesc(),field.getItemlength(),0,"0",9,10,"A0101",3,1,"A"));//姓名
			list.add(getSalarySetRecordvo(salaryid,10,"A01","A01Z0",ResourceFactory.getProperty("gz.info.a01z0"),1,0,"ZZ",10,7,"A01Z0",3,1,"A"));//停发标识
			dao.addValueObject(list);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
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
			RecordVo vo=new RecordVo("salarytemplate");
			vo.setInt("salaryid",Integer.parseInt(salaryid));
			vo.setString("cname",rename);
			dao.updateValueObject(vo);
//			dao.update("update salarytemplate set cname=? where salaryid=?",Arrays.asList(rename,salaryid));
			
			//修改薪资类别名称后，滑动滚轮翻转到上一页后再翻回来，类别名称没保存上，还显示修改前的名称。修改的同时，修改缓存的list内容
			TableDataConfigCache tableCache = (TableDataConfigCache)userview.getHm().get("salaryType");
			ArrayList<LazyDynaBean> list = tableCache.getTableData();
			for(int i = 0; i < list.size(); i++) {
				String salaryids = (String)list.get(i).get("salaryid");
				if(salaryid.equalsIgnoreCase(salaryids)) {
					list.get(i).set("cname", rename);
				}
			}
			tableCache.setTableData(list);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	/**
	 * 删除薪资类别
	 * 
	 * @param salaryid 薪资类别id
	 * @throws GeneralException
	 * @author dengcan
	 */
	public void deleteSalaryTemplate(String[] salaryid)throws GeneralException
	{
		try
		{
			StringBuffer whl=new StringBuffer("");
			StringBuffer whl1=new StringBuffer("");
			StringBuffer whl2=new StringBuffer("");
			for(int i=0;i<salaryid.length;i++)
			{
				whl.append(","+salaryid[i]);
				whl1.append(",'"+salaryid[i]+"'");
				whl2.append(",'SALARY_"+salaryid[i]+"'");		
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
			PendingTask pt = new PendingTask();
			//清空内存中的薪资类别参数信息
			for(int i=0;i<salaryid.length;i++)
			{
				SalaryCtrlParamBo.docMap.remove(salaryid[i]);
				//---------------------删除薪资类别的时候，同时把待办---------------
				StringBuffer str = new StringBuffer();
				str.append(" where ext_flag like 'GZSP_%' and ext_flag like '%_"+salaryid[i]+"' ");
				RowSet rs = dao.search("select pending_id from t_hr_pendingtask "+str);
				while(rs.next()){
					pt.updatePending("G", "G"+rs.getString("pending_id"), 100, "薪资审批", this.userview);
				}
				dao.delete("delete from t_hr_pendingtask "+str, new ArrayList());
				//-----------------------end-----------------------------------
			}
			//删除对应栏目设置 zhanghua 2017-6-15
			dao.delete(" delete From t_sys_table_scheme_item where SCHEME_ID in (select SCHEME_ID from t_sys_table_scheme where upper(SUBMODULEID) in ("+whl2.substring(1)+") )",new ArrayList());
			dao.delete(" delete From t_sys_table_scheme where upper(SUBMODULEID) in ("+whl2.substring(1)+") ",new ArrayList());
			//删除应用机构下发的数据
			dao.delete("delete from gz_reporting_log where salaryid  in ("+whl.substring(1)+")",new ArrayList());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	/**
	 * @author lis
	 * @Description: 另存薪资类别
	 * @date 2015-11-18
	 * @param salaryid 薪资类别id
	 * @param name 薪资名称
	 * @param gz_type 模块号
	 * @throws GeneralException
	 */
	public void reSaveSalaryTemplate(String salaryid,String name,String gz_type)throws GeneralException
	{
		RowSet rowSet = null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			this.controlNumberOfSalaryTemplate(gz_type,1);//判断账套数量是否达到上限
			int a_salaryid=DbNameBo.getPrimaryKey("salarytemplate","salaryid",this.conn);  // 取得主键值
			name=name.replaceAll("'","’");
			name=name.replace("\"","”");
			StringBuffer sql=new StringBuffer("insert into salarytemplate (salaryid,cname,cbase,cond,cexpr,nmoneyid,kzero,cstate,nflag,lprogram,username,ctrl_param,B0110)");
			sql.append(" select "+a_salaryid+",'"+name+"',cbase,cond,cexpr,nmoneyid,kzero,cstate,nflag,lprogram,username,ctrl_param,B0110 from salarytemplate ");
			sql.append(" where salaryid="+salaryid);
			dao.update(sql.toString());
			
			rowSet = dao.search("select max(seq) from salarytemplate");
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
				if("0".equals(gz_type))
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
			
			//另存为的时候如果xml中有应用机构，给这些人附新的账套的资源权限
			ApplicationOrgBo aorgbo = new ApplicationOrgBo(this.conn,String.valueOf(a_salaryid),this.userview);
			ProcessMonitorBo processMonitorBo = new ProcessMonitorBo(this.conn, this.userview,gz_type, String.valueOf(a_salaryid));
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.conn,a_salaryid,this.userview);
			ArrayList<String> listDataXML = processMonitorBo.getGzReportingData("4");
			String manage = gzbo.getManager();
			
			for(String username : listDataXML) {
				// 0 业务用户 1 角色  4 自助用户
		        String role_id = "";
		        String flag = "";
		        int res_type = 12;//12:薪资，18：保险
		        if (StringUtils.isNotBlank(manage)) {//自助用户，
		            role_id = aorgbo.getA0100ByUsername(username, gz_type);
		            flag = "4";
		        } else {
		            role_id = username;
		            flag = "0";
		        }
		        if ("1".equals(gz_type)) {
		            res_type = 18;
		        }
				aorgbo.saveResource(role_id, flag, res_type);
			}
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}finally{
			PubFunc.closeResource(rowSet);
		}
	}
	
	/**
	 * @author lis
	 * @Description: 取得薪资类别中的排序号
	 * @date 2015-11-17
	 * @return
	 */
	public int getSeq()
	{
		int seq = 0;
		RowSet rs = null;
		try
		{
			String sql = "select max(seq) seq from hrpchkformula ";
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql);
			while(rs.next())
			{
				seq=rs.getInt("seq");
			}
			rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(rs);
		}
		return seq;
	}
	
	/**
	 * @param list
	 *            需要导出的薪资类别列表
	 * @return 导出的薪资类别包的文件目录及名称
	 * @throws GeneralException 
	 */
	public String exportPkg(ArrayList list) throws GeneralException
	{
		
		String filename=this.userview.getUserName()+"_SalaryTemplate.zip";
		FileOutputStream fileOut=null;
		ZipOutputStream outputStream = null;
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
			 
			 // 导出薪资标准表 gz_stand
			 writeFileOut(whl, "gz_stand");
			 
			 // 导出薪资标准明细表 gz_item
			 writeFileOut(whl, "gz_item");
			 
			 // 压缩文件
			 ArrayList fileNames = new ArrayList(); // 存放文件名,并非含有路径的名字
			 ArrayList files = new ArrayList(); // 存放文件对象
			 BufferedInputStream origin = null;	
			 try
			 {
				fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+filename);
				outputStream = new ZipOutputStream(fileOut);
				File rootFile = new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+"newdata");
				listFile(rootFile, fileNames, files);
				byte[] data = new byte[2048];
				
				for (int loop = 0; loop < files.size(); loop++) {
					String a_fileName=(String) fileNames.get(loop);
					if(!"salarytemplate.xml".equalsIgnoreCase(a_fileName)&&!"salaryset.xml".equalsIgnoreCase(a_fileName)
							&&!"salaryformula.xml".equalsIgnoreCase(a_fileName)&&!"midvariable.xml".equalsIgnoreCase(a_fileName)
							&&!"gz_tax_rate.xml".equalsIgnoreCase(a_fileName)&&!"gz_taxrate_item.xml".equalsIgnoreCase(a_fileName)
							&&!"moneystyle.xml".equalsIgnoreCase(a_fileName)&&!"moneyitem.xml".equalsIgnoreCase(a_fileName)
							&&!"gz_stand.xml".equalsIgnoreCase(a_fileName)&&!"gz_item.xml".equalsIgnoreCase(a_fileName)&&!"reportdetail.xml".equalsIgnoreCase(a_fileName)
							&&!"reportitem.xml".equalsIgnoreCase(a_fileName)&&!"hrpchkformula.xml".equalsIgnoreCase(a_fileName))
						continue;
					FileInputStream fileIn = null;
					try {
						fileIn = new FileInputStream((File) files
								.get(loop));
						
						origin = new BufferedInputStream(fileIn, 2048);
						outputStream.putNextEntry(new ZipEntry((String) fileNames
								.get(loop)));
						int count;
						while ((count = origin.read(data, 0, 2048)) != -1) {
							outputStream.write(data, 0, count);
						}
					} finally{
						PubFunc.closeResource(origin);
						PubFunc.closeResource(fileIn);
					}
				}
				outputStream.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			} 
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			if(fileOut!=null)
				PubFunc.closeIoResource(fileOut);
			PubFunc.closeIoResource(outputStream);
		}
		return filename;
	}
	
	/**
	 * 写入数据
	 * @param whl
	 * @param tableName
	 * @throws GeneralException 
	 */
	private void writeFileOut(StringBuffer whl,String tableName) throws GeneralException{
		FileOutputStream fileOut = null;
		RowSetToXmlBuilder builder=new RowSetToXmlBuilder(this.conn);
		try {
			fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+"newdata"+System.getProperty("file.separator")+tableName+".xml");
			fileOut.write(getFileContext(whl.substring(1),tableName,builder));
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(fileOut);
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
	 * @throws GeneralException 
	 */
	public byte[] getFileContext(String salaryids_whl,String tableName,RowSetToXmlBuilder builder) throws GeneralException
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
			throw GeneralExceptionHandler.Handle(e);
		}
		
		return context.getBytes();
	}
	
	// 产生newdata文件夹
	public static void produceFolder()
	{
		if(!(new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+"newdata/").isDirectory()))
		{
			new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+"newdata/").mkdir();
				
		}
	}
	
	//将xml文件放入压缩包
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
	 * 获得导入压缩文件中某文件的纪录（指定字段）信息
	 * @author lis
	 * @param fileName
	 *            文件名称
	 * @param primaryKey 薪资类别id
	 * @param cname
	 * @param nameMap 薪资类别id与薪资类别名称 
	 * @return
	 */
	public ArrayList getSalaryTemplateList(String fileid,String fileName,String primaryKey,String cname) throws GeneralException
	{
		ArrayList list=new ArrayList();
		InputStream input = null;
		try
		{
			input = VfsService.getFile(fileid);
			HashMap fileMap=extZipFileList(input) ;
			String  fileContext=(String)fileMap.get(fileName);
			if(fileContext==null||fileContext.length()==0)
				return list;
			
			Document standard_doc = PubFunc.generateDom(fileContext);
			Element root=standard_doc.getRootElement();
			List nodeList=root.getChildren();
			LazyDynaBean a_bean=null;
			HashMap templatemap = this.getAllSalarytemplate();//得到所有薪资类别id
			for(Iterator t=nodeList.iterator();t.hasNext();)
			{
				Element record=(Element)t.next();
				String id=record.getAttributeValue(primaryKey);
				XPath xPath0 = XPath.newInstance("./"+cname);
				Element nameNode = (Element) xPath0.selectSingleNode(record);
				String name=nameNode.getValue();
				
				a_bean=new LazyDynaBean();
				a_bean.set("name",name);
				a_bean.set("id",SafeCode.encode(PubFunc.encrypt(id)));
				if(templatemap.get(id)!=null)
				{
					a_bean.set("isrepeat","1");
					a_bean.set("oldid",SafeCode.encode(PubFunc.encrypt(id)));
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
		} finally {
            PubFunc.closeIoResource(input);
        }
		return list;
	}
	
	/**
	 * 读取压缩包里的文件
	 * 
	 * @param inputStream
	 * @return
	 * @throws GeneralException 
	 */
	static HashMap extZipFileList(InputStream inputStream) throws GeneralException  
	{   
		  HashMap fileMap=new HashMap();
		  try   
		  {   
				  ZipInputStream   in   =   new   ZipInputStream(inputStream);   
				  ZipEntry   entry   =   null;   
			      while   ((entry =in.getNextEntry())!=null)   
			      {     
					  if(entry.isDirectory())   {   
						  continue;
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
					}   
			  }  
			  in.close();
		    
		  }   
		  catch   (IOException   e)   {   
			  e.printStackTrace();
			  throw GeneralExceptionHandler.Handle(e);
		  }   
		  return fileMap;
	}   
	
	/**
	 * @author lis
	 * @Description: 取得所有的薪资类别
	 * @date 2015-11-28
	 * @return
	 * @throws GeneralException 
	 */
	public HashMap getAllSalarytemplate() throws GeneralException
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
			throw GeneralExceptionHandler.Handle(e);
		}
		return map;
	}
	
	/**
	 * 导入薪资类别时，如果是覆盖方式，先删除所有与该薪资类别有关的数据
	 * 
	 * @param repeats
	 * @throws GeneralException 
	 */
	public HashMap deleteRepeatRecord(String repeats) throws GeneralException
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
	    		String[] array =salaryids.split(",");
	    		this.deleteCoverSalaryTemplate(array);
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
	 * 覆盖导入薪资类别时，删除一些数据
	 * @param salaryid
	 * @throws GeneralException 
	 */
	public void deleteCoverSalaryTemplate(String[] salaryid) throws GeneralException
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
			throw GeneralExceptionHandler.Handle(e);
		}
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
			
			Document standard_doc = PubFunc.generateDom(fileContext);
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
	 * 导入薪资包
	 * 
	 * @param form_file
	 *            导入文件
	 * @param salaryids
	 *            指定导入的薪资类别id
	 * @return
	 */
	public boolean importPkg(String fileid,String[] salaryidS,HashMap map) throws GeneralException
	{
		boolean bflag=true;
		InputStream inputStream = null;
		try
		{
			StringBuffer salaryids=new StringBuffer("#");
			for(int i=0;i<salaryidS.length;i++)//要导入的工资类别
				salaryids.append(salaryidS[i]+"#");
			
			inputStream = VfsService.getFile(fileid);
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
	 * 将新追加的工资类别授权给当前用户
	 * 
	 * @param maxid
	 * @throws GeneralException 
	 */
	public void saveSalarySetResource(String[] salarySetIDs,int gz_type) throws GeneralException
	{
		try
		{
			
			for(int i=0;i<salarySetIDs.length;i++)
			{
				String salaryid = salarySetIDs[i];
				if(!(this.userview.isAdmin()&& "1".equals(this.userview.getGroupId())))
				{
					UserObjectBo user_bo=new UserObjectBo(this.conn);
					if(gz_type==0&&!this.userview.isHaveResource(IResourceConstant.GZ_SET, salaryid))
						user_bo.saveResource(String.valueOf(salaryid),this.userview,IResourceConstant.GZ_SET);
					else if(!this.userview.isHaveResource(IResourceConstant.INS_SET, salaryid))
						user_bo.saveResource(String.valueOf(salaryid),this.userview,IResourceConstant.INS_SET);
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
	 * 
	 * @param name
	 * @param gz_module
	 * @param type =0重命名，=1另存为
	 * @param salaryid
	 * @return
	 * @throws GeneralException 
	 */
	public boolean isHaveName(String name,String gz_module,String type,String salaryid) throws GeneralException
	{
		boolean flag = false;
		RowSet frowset = null;
		try
		{
			StringBuffer sql = new StringBuffer();
			sql.append("select * from salarytemplate where ");
			if("0".equalsIgnoreCase(gz_module))
				sql.append(" (cstate is null or cstate='')");
			else
				sql.append(" cstate='1'");
			sql.append(" and cname='"+name+"'");
			if("0".equals(type))
			{
				sql.append(" and salaryid<>"+salaryid);
			}
			ContentDAO dao = new ContentDAO(this.conn);
			frowset = dao.search(sql.toString());
			while(frowset.next())
			{
				flag=true;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(frowset);
		}
		return flag;
	}
	
	/**
	 * @author lis
	 * @Description: 同步薪资类别结构
	 * @date 2015-12-4
	 * @param salaryids 薪资类别id串
	 */
	public String synchronizeSalaryStruct(String salaryids)
	{
		String errorMessage = "";
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
				int salaryid=Integer.parseInt(PubFunc.decrypt(SafeCode.decode(salaryIDs[i])));
				
				SalarySetBo setBo = new SalarySetBo(conn, salaryid, userview); 
				SalaryCtrlParamBo ctrlparam=new SalaryCtrlParamBo(this.conn,salaryid);
				ArrayList<Field> fieldlist=setBo.searchGzItem2();//获取薪资项目集合
				//同步薪资类别里的薪资项
				errorMessage = synchronismSalarySet(salaryid);
				//同步临时表
				ArrayList tempTableList=getTempSalaryTableList(salaryid+"");
				for(int j=0;j<tempTableList.size();j++)
				{
					String gz_tablename=(String)tempTableList.get(j);
					synchronizeTempSalaryStruct(gz_tablename,fieldlist);
					
				}
				//同步计算公式名称  zhaoxg add 2013-10-14 
				synchronismSalaryFormula(salaryid);
				//同步历史数据表  薪资数据归档表
				upgradeGzHisTableStruct();
				dbmodel.reloadTableModel("salaryhistory");	
				upgradGzHisTableStruct(fieldlist,"salaryhistory");
				upgradGzHisTableStruct(fieldlist,"salaryarchive");
				dbmodel.reloadTableModel("salaryhistory");
				syncGzField("salaryhistory",fieldlist);
				dbmodel.reloadTableModel("salaryhistory");
				syncGzField("salaryarchive",fieldlist);
				dbmodel.reloadTableModel("salaryarchive");
			}
			//个税明细表结构同步
			this.upgradGzTaxMx(dbmodel);
		 	//解决8060问题  邓灿
			PubFunc.resolve8060(this.conn,"SalaryHistory");

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return errorMessage;
	}


	/**
	 * 个税明细表结构同步
	 * @param dbmodel
	 * @throws GeneralException
	 * @author ZhangHua
	 * @date 17:36 2018/7/12
	 */
	private void upgradGzTaxMx(DBMetaModel dbmodel) throws GeneralException {
		try{

			TaxMxBo taxMxBo=new TaxMxBo(this.conn,this.userview);
			ArrayList<Field> fieldlist=taxMxBo.searchDynaItemList();

			Field field=new Field("A0101", com.hrms.hjsj.sys.ResourceFactory.getProperty("gz.columns.a0101"));
			field.setDatatype(DataType.STRING);
			field.setLength(DataDictionary.getFieldItem("A0101").getItemlength());
			fieldlist.add(field);

			field=new Field("A0100","A0100");
			field.setDatatype(DataType.STRING);
			fieldlist.add(field);

			field=new Field("B0110",DataDictionary.getFieldItem("b0110").getItemdesc());
			field.setDatatype(DataType.STRING);
			field.setLength(DataDictionary.getFieldItem("B0110").getItemlength());
			fieldlist.add(field);

			field=new Field("E0122",DataDictionary.getFieldItem("e0122").getItemdesc());
			field.setDatatype(DataType.STRING);
			field.setLength(DataDictionary.getFieldItem("e0122").getItemlength());
			fieldlist.add(field);
			syncGzField("GZ_TAX_MX",fieldlist);//个税明细表
			dbmodel.reloadTableModel("GZ_TAX_MX");
			syncGzField("TAXARCHIVE",fieldlist);//个税归档表
			dbmodel.reloadTableModel("TAXARCHIVE");
		}catch (Exception e){
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	/**
	 * 升级历史薪资表（添加薪资类别中有的而历史表没有的字段）
	 */
	private void upgradGzHisTableStruct(ArrayList<Field> fieldlist,String tableName)throws GeneralException 
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer buf=new StringBuffer();
			
			buf.setLength(0);
			//for i loop end. 
			RowSet rowSet=dao.search("select * from "+tableName+" where 1=2");
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
			for(int i=0;i<fieldlist.size();i++)
			{
				Field field = fieldlist.get(i);
				String name=field.getName().toUpperCase();
				/**如果未找到，则追加*/
				if(buf.indexOf(name)==-1)
				{
					addlist.add(field);
				}
				buf0.append(name);
				buf0.append(",");
			}//for i loop end.
			 
			DbWizard dbw=new DbWizard(this.conn);
			Table table=new Table(tableName);
			for(int i=0;i<addlist.size();i++){
			//	System.out.println(":"+addlist.get(i));
				table.addField((Field)addlist.get(i));
			}
			if(addlist.size()>0)
				dbw.addColumns(table);
			 
			table.clear(); 
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
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
			if(voMap.get("SP_FLAG")==null)
			{
				table.clear();
				field=new Field("sp_flag","sp_flag");
				field.setDatatype(DataType.STRING);
				field.setLength(2);
				table.addField(field);
				dbw.addColumns(table);				
			}
			
			if(voMap.get("APPPROCESS")==null)
			{
				table.clear();
				field=new Field("appprocess","appprocess");
				field.setDatatype(DataType.CLOB);
				table.addField(field);
				dbw.addColumns(table);				
			}
			
			if(voMap.get("APPUSER")==null)
			{
				table.clear();
				field=new Field("Appuser","Appuser");
				field.setDatatype(DataType.STRING);
				field.setLength(200);
				table.addField(field);
				dbw.addColumns(table);				
			}
			
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
	 * 同步计算公式 zhaxg add 2013-10-14
	 * @throws GeneralException 
	 */
	public void synchronismSalaryFormula(int salaryid) throws GeneralException
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select * from salaryformula where salaryid="+salaryid+"");
			RecordVo vo=null;
			ArrayList list=new ArrayList();
			while(rowSet.next())
			{
				int itemid=rowSet.getInt("itemid");
				String itemname=rowSet.getString("itemname");
				vo=new RecordVo("salaryformula");
				vo.setInt("salaryid",salaryid);
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
			throw GeneralExceptionHandler.Handle(e);
		}
	}	
		
	//同步临时表结构
	private void synchronizeTempSalaryStruct(String tableName,ArrayList<Field> fieldlist) throws GeneralException
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			DbWizard dbWizard=new DbWizard(this.conn); 
			DBMetaModel dbmodel=new DBMetaModel(this.conn);
			dbmodel.reloadTableModel(tableName);	
			/**升级表结构*/
			upgradeGzTableStruct(tableName);		
			/**重新加载数据模型*/
			dbmodel.reloadTableModel(tableName);	
			RecordVo vo=new RecordVo(tableName);
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
			for(int e=0;e<fieldlist.size();e++)
			{
				Field field = fieldlist.get(e);
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
			Table table=new Table(tableName);
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
			syncGzField(tableName,fieldlist);
			/**重新加载数据模型*/
			dbmodel.reloadTableModel(tableName);	
			if(!isAddFlag)
			{
				String[] temp=tableName.split("_salary_");
				buf.setLength(0);
				int salaryid = Integer.valueOf(temp[1]);
				SalaryAccountBo accountBo = new SalaryAccountBo(conn, userview, salaryid);
				HashMap map=accountBo.getMaxYearMonthCount(null,false);
				/**需要审批的话,则分析当前处理的业务日期是否处理审批状态*/
				String a_currym=(String)map.get("ym");
				String a_currcount=(String)map.get("count");
				
				String sp_flag="01";
				buf.append("select sp_flag from gz_extend_log where salaryid="+salaryid);
				buf.append(" and upper(username)='"+temp[0].toUpperCase()+"'");
				buf.append(" and A00Z2=");
				buf.append(Sql_switcher.dateValue(a_currym));
				buf.append(" and A00Z3=");
				buf.append(a_currcount);
				RowSet rset=dao.search(buf.toString());
				if(rset.next())
					sp_flag=rset.getString("sp_flag");
				if("06".equals(sp_flag))
					dao.update("update "+tableName+" set add_flag=1");
				else
					dao.update("update "+tableName+" set add_flag=0");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	/**
	 *当指标长度或类型发生的变化同步 工资发放临时表 或 工资历史数据表
	 * @throws GeneralException 
	 */
	public void  syncGzField(String tableName,ArrayList<Field> fieldlist) throws GeneralException
	{
		try
		{
			 ContentDAO dao=new ContentDAO(this.conn);
			 HashMap map=new HashMap();
			 for(int i=0;i<fieldlist.size();i++)
			 {
				 Field field = fieldlist.get(i);
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
								case java.sql.Types.DATE:
									if(!"D".equals(tempItem.getItemtype()))
									{
										resetList.add(tempItem.cloneField());
									}
									break;
								case Types.BIGINT:{
									if("N".equals(tempItem.getItemtype()))
									{
										if(tempItem.getDecimalwidth()!=scale){
											alterList.add(tempItem.cloneField());
										}else if(precision<tempItem.getItemlength()) //2013-11-23  如果指标长度改大了，需同步结构
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

								}break;
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
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	
	public void syncGzOracleField(ResultSetMetaData data,HashMap map,String tableName) throws GeneralException
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
					int size=rs.getInt("data_length");
					int scale=rs.getInt("data_scale");
					if("NUMBER".equalsIgnoreCase(rs.getString("data_type"))){//数值型的 data_precision代表字段总长度
						size=rs.getInt("data_precision");
						if("N".equals(tempItem.getItemtype()))
						{
							if(tempItem.getDecimalwidth()!=scale)
								 alertColumn(tableName,tempItem,dbw,dao);
							else if((size-scale)<tempItem.getItemlength()) //2013-11-23  如果指标长度改大了，需同步结构
							{
								 alertColumn(tableName,tempItem,dbw,dao);
							}
						}else {
							if("A".equals(tempItem.getItemtype()))
								 alertColumn(tableName,tempItem,dbw,dao);
						}
					}else if("VARCHAR2".equalsIgnoreCase(rs.getString("data_type"))){//字符型的 data_length代表字段总长度
						size=rs.getInt("data_length");
						//columnType = DataType.STRING;
						if("A".equals(tempItem.getItemtype()))
						{
							if(tempItem.getItemlength()>size)
								 alertColumn(tableName,tempItem,dbw,dao);
						}
					}
					
					/*switch(columnType)
					{
						case DataType.INT:
							if(tempItem.getItemtype().equals("N"))
							{
								if(tempItem.getDecimalwidth()!=scale)
									 alertColumn(tableName,tempItem,dbw,dao);
								else if((size-scale)<tempItem.getItemlength()) //2013-11-23  如果指标长度改大了，需同步结构
								{
									 alertColumn(tableName,tempItem,dbw,dao);
								}
							}
							if(!tempItem.getItemtype().equals("N"))
							{
								if(tempItem.getItemtype().equals("A"))
									 alertColumn(tableName,tempItem,dbw,dao);
							}
							break;
						case DataType.STRING:
							if(tempItem.getItemtype().equals("A"))
							{
								if(tempItem.getItemlength()>size)
									 alertColumn(tableName,tempItem,dbw,dao);
							}
							break;
						case DataType.FLOAT:
							if(tempItem.getItemtype().equals("N"))
							{
								if(tempItem.getDecimalwidth()!=scale)
									 alertColumn(tableName,tempItem,dbw,dao);
								else if((size-scale)<tempItem.getItemlength()) //2013-11-23  如果指标长度改大了，需同步结构
								{
									 alertColumn(tableName,tempItem,dbw,dao);
								}
							}
							if(!tempItem.getItemtype().equals("N"))
							{
								if(tempItem.getItemtype().equals("A"))
									 alertColumn(tableName,tempItem,dbw,dao);
							}
							break;
					}*/
				}
			 }
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	public void alertColumn(String tableName,FieldItem _item,DbWizard dbw,ContentDAO dao) throws GeneralException
	{
		try
		{
			FieldItem item=(FieldItem)_item.cloneItem();
			Table table=new Table(tableName);
			String item_id=item.getItemid();
			item.setItemid(item_id+"_x");
			 
			 RowSet rowSet=dao.search("select * from "+tableName+" where 1=2");
			 ResultSetMetaData data=rowSet.getMetaData();
			 HashMap columnMap=new HashMap(); 
			 for(int i=1;i<=data.getColumnCount();i++)
			 {	 columnMap.put(data.getColumnName(i).toLowerCase().trim(),"1"); 
			 }
			  
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
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	/**
	 * 升级薪资表结构，临时用户表
	 * @throws GeneralException
	 */
	private void upgradeGzTableStruct(String gz_tablename)throws GeneralException
	{
		StringBuffer buf=new StringBuffer();
		try
		{ 
			
			DbWizard dbw=new DbWizard(this.conn);
			 
			ContentDAO dao=new ContentDAO(this.conn);
			RecordVo vo=new RecordVo(gz_tablename);	
			Table table=new Table(gz_tablename);
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
				buf.append(gz_tablename);
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
				dbw.dropPrimaryKey(gz_tablename);
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
			}else if(!(vo.hasAttribute("a00z3")||vo.hasAttribute("A00Z3"))) {

				field = new Field("A00Z3", ResourceFactory.getProperty("gz.columns.a00z3"));
				field.setDatatype(DataType.INT);
				table.addField(field);
				dbw.addColumns(table);
				/**正常情况下,发放日期=归属日期,发放次数=归属次数*/
				buf.append("update ");
				buf.append(gz_tablename);
				buf.append(" set A00Z3=A00Z1");
				dao.update(buf.toString());
				/**修改主键*/
				/**先删除主键索引*/
				dbw.dropPrimaryKey(gz_tablename);
				table.clear();
				field = new Field("NBASE", "NBASE");
				field.setKeyable(true);
				table.addField(field);
				field = new Field("A0100", "A0100");
				field.setKeyable(true);
				table.addField(field);
				field = new Field("A00Z0", "A00Z0");
				field.setKeyable(true);
				table.addField(field);
				field = new Field("A00Z1", "A00Z1");
				field.setKeyable(true);
				table.addField(field);
				dbw.addPrimaryKey(table);

			}
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
	 * @author lis
	 * @Description: 得到临时表
	 * @date 2015-12-4
	 * @param salaryid
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getTempSalaryTableList(String salaryid) throws GeneralException
	{
		ArrayList list=new ArrayList();
		RowSet rowSet = null;
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
					rowSet = dao.search(sql);
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
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(rowSet);
		}
		return list;
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
						
						dao.delete("delete from salary_mapping where salaryid="+salaryid[i], new ArrayList());//zhanghua 2017-4-18
						
						if(dbWizard.isExistTable("salaryarchive", false))
							dao.delete("delete from salaryarchive where salaryid="+salaryid[i],new ArrayList());
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
						dao.delete("delete from gz_reporting_log where salaryid="+salaryid[i], new ArrayList());//删除应用机构下发记录
						if(dbWizard.isExistTable("gz_tax_mx",false))
							dao.delete("delete from gz_tax_mx where salaryid="+salaryid[i], new ArrayList());  // 删除个人所得税中相关的数据
						if(dbWizard.isExistTable("taxarchive",false))
							dao.delete("delete from taxarchive where salaryid="+salaryid[i], new ArrayList());  // 删除个人所得税归档表中相关的数据
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
					
					StringBuffer sql5=new StringBuffer("delete from salary_mapping where salaryid in ("+whl.substring(1)+")");//zhanghua 2017-4-18
					StringBuffer sql6=new StringBuffer("delete from taxarchive where salaryid in ("+whl.substring(1)+") ");//添加删除税率归档表
					StringBuffer sql7=new StringBuffer("delete from gz_reporting_log where salaryid in ("+whl.substring(1)+") ");//删除应用机构下发记录
					if(startDate!=null&&startDate.length()>0)
					{
						sql.append(" and "+getDataValue("a00z2",">=",startDate));
						sql4.append(" and "+getDataValue("a00z2",">=",startDate));
						sql2.append(" and "+getDataValue("a00z2",">=",startDate));	
						sql3.append(" and "+getDataValue("a00z0",">=",startDate));
						sql5.append(" and "+getDataValue("a00z0",">=",startDate));
					}
					if(endDate!=null&&endDate.length()>0)
					{
						sql2.append(" and "+getDataValue("a00z2","<=",endDate));
						sql.append(" and "+getDataValue("a00z2","<=",endDate));
						sql4.append(" and "+getDataValue("a00z2","<=",endDate));
						sql3.append(" and "+getDataValue("a00z0","<=",endDate));
						sql5.append(" and "+getDataValue("a00z0","<=",endDate));
					}
					dao.delete(sql.toString(),new ArrayList());
					dao.delete(sql2.toString(),new ArrayList());
					dao.delete(sql3.toString(),new ArrayList());
					dao.delete(sql5.toString(),new ArrayList());
					dao.delete(sql6.toString(),new ArrayList());
					dao.delete(sql7.toString(),new ArrayList());
					
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
	 * @author lis
	 * @Description: 根据itemid得到数据
	 * @date 2015-12-4
	 * @param fielditemid
	 * @param operate
	 * @param value
	 * @return
	 */
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
	 * 取得权限范围内的所有需要审批的工资套
	 * @return
	 */
	public String getSalarySetList(int gz_module)
	{
		String salaryid = "";
		RowSet rs=null;
		try
		{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select salaryid,cname,ctrl_param from salarytemplate ");
			if(gz_module==0)
				sql.append(" where (cstate is null or cstate='')");// 薪资类别
			else
				sql.append(" where cstate='1'");// 险种类别
			ContentDAO dao = new ContentDAO(this.conn);
			SalaryCtrlParamBo ctrlparam=null;
			 rs= dao.search(sql.toString()+" order by seq");
			while(rs.next())
			{
				if(StringUtils.isNotBlank(salaryid)) 
					break;
				if(gz_module==0)
				{
					if(!this.userview.isHaveResource(IResourceConstant.GZ_SET, rs.getString("salaryid")))
						continue;
				}
				else
				{
					if(!this.userview.isHaveResource(IResourceConstant.INS_SET, rs.getString("salaryid")))
						continue;
				}
				salaryid = rs.getString("salaryid");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			PubFunc.closeDbObj(rs);
		}
		return salaryid;
	}
	
	/**
	 * 导入的时候对应用机构的赋予资源权限和业务范围
	 * @param gz_type
	 * @param nameMap需要导入的名称的集合
	 */
	public void saveResourceOfOrg(String gz_type,HashMap<String,String> nameMap) {
		RowSet rowSet = null;
		try {
			Iterator iter = nameMap.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				String cname = (String) entry.getValue();
				ContentDAO dao=new ContentDAO(this.conn);
				int maxID=0;
				String salaryid = "";
				//到这一步的时候已经把薪资张天塞入salarytemplate表中了，如果在塞得过程中添加，代码改动挺大，需要userView,但是那块没有，需要添加构造方法，修改参数等，这里在最后处理了
				rowSet=dao.search("select salaryid from salarytemplate where cname=?",Arrays.asList(new String[]{cname}));
				if(rowSet.next())
					salaryid=String.valueOf(rowSet.getInt(1));
				if(StringUtils.isBlank(salaryid))
					continue;
				
				//导入的时候如果xml中有应用机构，给这些人附新的账套的资源权限
				ApplicationOrgBo aorgbo = new ApplicationOrgBo(this.conn,salaryid,this.userview);
				ProcessMonitorBo processMonitorBo = new ProcessMonitorBo(this.conn, this.userview,String.valueOf(gz_type), salaryid);
				SalaryTemplateBo gzbo=new SalaryTemplateBo(this.conn,Integer.parseInt(salaryid),this.userview);
				ArrayList<String> listDataXML = processMonitorBo.getGzReportingData("1");
				String manage = gzbo.getManager();
				
				for(String gzReportingData : listDataXML) {
					String b0110XML = gzReportingData.substring(0, gzReportingData.indexOf("|"));
					String usernameXML = gzReportingData.substring((gzReportingData.indexOf("|",(gzReportingData.indexOf("|")+1)) + 1), gzReportingData.length());
					// 0 业务用户 1 角色  4 自助用户
			        String role_id = "";
			        String flag = "";
			        int res_type = 12;//12:薪资，18：保险
			        if (StringUtils.isNotBlank(manage)) {//自助用户，
			            role_id = aorgbo.getA0100ByUsername(usernameXML, String.valueOf(gz_type));
			            flag = "4";
			        } else {
			            role_id = usernameXML;
			            flag = "0";
			        }
			        if ("1".equals(gz_type)) {
			            res_type = 18;
			        }
			        if(StringUtils.isNotBlank(role_id)) {
			        	//保存资源权限
						aorgbo.saveResource(role_id, flag, res_type);
						
						if(StringUtils.isBlank(manage)) {
							RecordVo vo=new RecordVo("operuser");
				    		vo.setString("username", usernameXML);
							RecordVo vo1 = dao.findByPrimaryKey(vo);
							String groupid = vo1.getString("groupid");
							if("1".equals(groupid)) //超级管理员不需要找
								continue;
						}
						//保存业务范围
						aorgbo.saveManageRange(role_id, flag, b0110XML ,"add");
			        }
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
