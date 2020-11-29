package com.hjsj.hrms.utils.components.dataview;

import com.hjsj.hrms.module.template.utils.TemplateStaticDataBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.jdom.Document;
import org.jdom.Element;

import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CreateDataViewTrans extends IBusiness{

	//数据sql
	private String dataSql = "";
	//排序
	private String orderSql = "";
	//表格列集合
	private ArrayList columnsList = new ArrayList();
	//加载字段集合
	private ArrayList viewFieldList = new ArrayList();
	//列连接集合
	private HashMap columnLinkMap = new HashMap();
	//代码指标集合
	private HashMap codeFieldMap = new HashMap();
	//列显示控制方式
	private String viewCtrlType = "hide";
	//列显示控制字段
	private String viewCtrlField = "";
	
	public void execute() throws GeneralException {
		
		String reportid = (String)this.getFormHM().get("reportid");
		
		if(reportid!=null && reportid.length()>0){
			//解析xml获取报表设置参数
			boolean success = loadSimpleReportParam(reportid);
			if(!success) {
				return;
			}
		}else{
			loadComponentParam();
		}
		
		String setname = (String)this.formHM.get("setname");
		
		codeFieldMap.put("nbase",true);
		codeFieldMap.put("b0110",true);
		codeFieldMap.put("e0122",true);
		codeFieldMap.put("e01a1",true);
		
		//通过数据字典工具类查询数据源
		FieldSet set = DataDictionary.getFieldSetVo(setname);
		
		if(set==null){
			try {
				//不是字典表
				createCustomViewTable();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		}
		//如果fieldset存在，但是没有构库，不生成报表
		if("0".equals(set.getUseflag())){
			this.formHM.put("errorMsg", ResourceFactory.getProperty("customreport_nostrutsset"));
			return;
		}
		//人员、单位、岗位信息集
		if(set.getFromZd()==1 && (setname.startsWith("A") || setname.startsWith("B") || setname.startsWith("K"))){
			createA_B_KStrutsTable();
			return;
		}
		//非业务字典信息集，例如H01(基准岗位信息集)、Y01(党团信息集)等
		if(set.getFromZd()==1){
			this.getFormHM().put("privtype", "");
			this.getFormHM().put("infokind", "");
			this.getFormHM().put("privfield", "");
			createOtherStrutsTable();
			return;
		}
		//业务字典信息集
		createOtherStrutsTable();
	}
	
	private void createOtherStrutsTable() throws GeneralException{
		String source = (String)this.getFormHM().get("setname");
		String privtype = (String)this.formHM.get("privtype");
		String infokind = (String)this.formHM.get("infokind");
		String privfield = (String)this.formHM.get("privfield");
		
		ArrayList fieldList = DataDictionary.getFieldList(source, Constant.USED_FIELD_SET);
		
		boolean hasNbase = false;
		boolean hasA0100 = false;
		boolean hasB0110 = false;
		boolean hasE0122 = false;
		boolean hasE01A1 = false;
		
		FieldItem item = null;
		//生成ColumnsInfo对象
		for(int i=0;i<fieldList.size();i++){
			item = (FieldItem)fieldList.get(i);
			//如果设置不显示
			if(!item.isVisible() && 
				!"NBASE".equals(item.getItemid().toUpperCase()) &&
				!"A0100".equals(item.getItemid().toUpperCase()) &&
				!"B0110".equals(item.getItemid().toUpperCase()) &&
				!"B0110".equals(item.getItemid().toUpperCase()) &&
				!"E01A1".equals(item.getItemid().toUpperCase()))
				continue;
			
			viewFieldList.add(item.getItemid().toLowerCase());
			
			if(isColumnHide(item.getItemid()))
				continue;
			
			ColumnsInfo column = new ColumnsInfo(item);
			
			if("NBASE".equals(item.getItemid().toUpperCase())){
				hasNbase = true;
				column.setCodesetId("@@");
			}else if("A0100".equals(item.getItemid().toUpperCase())){
				hasA0100 = true;
			}else if("B0110".equals(item.getItemid().toUpperCase())){
				hasB0110 = true;
			}else if("E0122".equals(item.getItemid().toUpperCase())){
				hasE0122 = true;
			}else if("E01A1".equals(item.getItemid().toUpperCase())){
				hasE01A1 = true;
			}
			setColumnRenderer(column);
			columnsList.add(column);
			
			//如果是人员编号，加密处理
			if("A0100".equalsIgnoreCase(item.getItemid())){
				column.setEncrypted(true);
				column.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			}
			
			if(item.getCodesetid()!=null && !"0".equals(item.getCodesetid()))
				codeFieldMap.put(item.getItemid().toLowerCase(),true);
		}
		
		renameColumnDesc();
		
		if(privtype.length()>0 && infokind.length()>0){
			//如果需要权限校验，则检查表中是否有必要的指标
			if("A".equals(infokind) && !(hasNbase && hasA0100 && hasB0110 && hasE0122 && hasE01A1)){
				if(!hasNbase)
					this.formHM.put("errorMsg", ResourceFactory.getProperty("customreport_nonbasefield"));
				else if(!hasA0100)
					this.formHM.put("errorMsg", ResourceFactory.getProperty("customreport_noa0100field"));
				else if(!hasB0110)
					this.formHM.put("errorMsg", ResourceFactory.getProperty("customreport_nob0110field"));
				else if(!hasE0122)
					this.formHM.put("errorMsg", ResourceFactory.getProperty("customreport_noe0122field"));
				else if(!hasE01A1)
					this.formHM.put("errorMsg", ResourceFactory.getProperty("customreport_noe01a1field"));
				
					return;
			}else if("B".equals(infokind) && !hasB0110){
				this.formHM.put("errorMsg", ResourceFactory.getProperty("customreport_nob0110field"));
				return;
			}else if("K".equals(infokind) && !hasE01A1){
				this.formHM.put("errorMsg", ResourceFactory.getProperty("customreport_noe01a1field"));
				return;
			}
		}
		
		dataSql = createViewQuerySql(source,privtype,infokind,privfield).toString();
		groupSumHandle();
		createTableConfig();
		
	}
	
	private void createA_B_KStrutsTable() throws GeneralException{
		String source = (String)this.getFormHM().get("setname");
		//获取权限内指标集合
		ArrayList fieldList = userView.getPrivFieldList(source);
		//没有指标权限，退出
		if(fieldList==null || fieldList.size()<1){
			this.formHM.put("errorMsg", ResourceFactory.getProperty("customreport_nofieldpriv"));
			return;
		}
		
		String groupfield = (String)this.formHM.get("groupfield");
		if(groupfield.trim().length()>0){
			boolean hasPriv;
			String[] groups = groupfield.split(",");
			for(int i=0;i<groups.length;i++){
				hasPriv = false;
				if("B0110".equalsIgnoreCase(groups[i]) || "E0122".equalsIgnoreCase(groups[i]) || "E01A1".equalsIgnoreCase(groups[i]) || "A0100".equalsIgnoreCase(groups[i]) || "NBASE".equalsIgnoreCase(groups[i]))
					continue;
				for(int k=0;k<fieldList.size();k++){
					FieldItem fi = (FieldItem)fieldList.get(k);
					if(fi.getItemid().equalsIgnoreCase(groups[i])){
						hasPriv = true;
						break;
					}
				}
				if(!hasPriv){
					this.formHM.put("errorMsg", "groupfield > "+ResourceFactory.getProperty("customreport_nofieldpriv"));
					return;
				}
			}
		}
		
		FieldItem item = null;
		
		if(source.toUpperCase().startsWith("A")){// && !source.toUpperCase().equals("A01")
			this.formHM.put("infokind","A");
			ColumnsInfo column;
			//如果是人员子集，追加 姓名、单位、部门、岗位字段
			column = new ColumnsInfo();
			column.setColumnId("NBASE");
			column.setColumnDesc("人员库");
			column.setColumnType("A");
			column.setCodesetId("@@");
			column.setLoadtype(ColumnsInfo.LOADTYPE_ALWAYSLOAD);
			columnsList.add(column);
			
			item =  DataDictionary.getFieldItem("A0100");
			column = new ColumnsInfo(item);
			column.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			column.setEncrypted(true);
			columnsList.add(column);
			
			item =  DataDictionary.getFieldItem("B0110");
			column = new ColumnsInfo(item);
			column.setLocked(true);
			setColumnRenderer(column);
			//是否隐藏
			if(this.isColumnHide("b0110"))
				column.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			columnsList.add(column);
			
			item =  DataDictionary.getFieldItem("E0122");
			column = new ColumnsInfo(item);
			column.setLocked(true);
			setColumnRenderer(column);
			//是否隐藏
			if(this.isColumnHide("e0122"))
				column.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			columnsList.add(column);
			
			item =  DataDictionary.getFieldItem("E01A1");
			column = new ColumnsInfo(item);
			column.setLocked(true);
			setColumnRenderer(column);
			//是否隐藏
			if(this.isColumnHide("e01a1"))
				column.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			columnsList.add(column);
			
			item =  DataDictionary.getFieldItem("A0101");
			column = new ColumnsInfo(item);
			column.setLocked(true);
			setColumnRenderer(column);
			//是否隐藏
			if(this.isColumnHide("a0101"))
				column.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			columnsList.add(column);
			
		}else if(source.toUpperCase().startsWith("B")){
			this.formHM.put("infokind","B");
			//如果是单位子集，追加 单位字段
			item =  DataDictionary.getFieldItem("B0110");
			ColumnsInfo column = new ColumnsInfo(item);
			setColumnRenderer(column);
			//是否隐藏
			if(this.isColumnHide("b0110"))
				column.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			columnsList.add(column);
		}else if(source.toUpperCase().startsWith("K")){
			this.formHM.put("infokind","K");
			ColumnsInfo column;
			//如果是岗位信息集，追加 单位、部门字段
			item =  DataDictionary.getFieldItem("B0110");
			column = new ColumnsInfo(item);
			setColumnRenderer(column);
			//是否隐藏
			if(this.isColumnHide("b0110"))
				column.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			columnsList.add(column);
			//if(!source.toUpperCase().equals("K01")){
			item =  DataDictionary.getFieldItem("E0122");
			column = new ColumnsInfo(item);
			setColumnRenderer(column);
			//是否隐藏
			if(this.isColumnHide("e0122"))
				column.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			columnsList.add(column);
			
			item =  DataDictionary.getFieldItem("E01A1");
			column = new ColumnsInfo(item);
			setColumnRenderer(column);
			//是否隐藏
			if(this.isColumnHide("e01a1"))
				column.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			columnsList.add(column);
			//}
		}
		
		//生成ColumnsInfo对象
		for(int i=0;i<fieldList.size();i++){
			item = (FieldItem)fieldList.get(i);
			if(
					"a0101".equalsIgnoreCase(item.getItemid()) ||
					"a0100".equalsIgnoreCase(item.getItemid()) ||
					"b0110".equalsIgnoreCase(item.getItemid()) ||
					"e0122".equalsIgnoreCase(item.getItemid()) ||
					"e01a1".equalsIgnoreCase(item.getItemid())
					)
			{
				continue;
			}
			
			//判断是否要隐藏
			if(isColumnHide(item.getItemid()))
				continue;
			
			ColumnsInfo column = new ColumnsInfo(item);
			//添加渲染函数
			setColumnRenderer(column);
			columnsList.add(column);
			
			if(item.getCodesetid()!=null && !"0".equals(item.getCodesetid()))
				codeFieldMap.put(item.getItemid().toLowerCase(),true);
		}
		
		renameColumnDesc();
		
		String privtype = (String)this.formHM.get("privtype");
		dataSql = createStrutsQuerySql(fieldList,source,privtype).toString();
		
		groupSumHandle();
		
		createTableConfig();
	}
	
	private void setColumnRenderer(ColumnsInfo column){
		if(this.columnLinkMap.containsKey(column.getColumnId().toLowerCase())){
			column.setRendererFunc("DataViewComp_self.columnLinkRender");
		}
	}
	
	private boolean isColumnHide(String fieldid){
		if("show".equalsIgnoreCase(this.viewCtrlType)){
			if(!this.viewCtrlField.contains(","+fieldid.toLowerCase()+","))
				return true;
		}else{
			if(this.viewCtrlField.contains(","+fieldid.toLowerCase()+","))
				return true;
		}
		return false;
	}
	
	private StringBuffer createStrutsQuerySql(ArrayList fieldList,String source,String privtype) throws GeneralException{
		StringBuffer querySql = new StringBuffer();
		
		source = source.toUpperCase();
		
		String setPrefix = "";
		querySql.append("select ");
		if(source.toUpperCase().startsWith("A")){
			setPrefix = "Usr";
			querySql.append(" 'Usr' nbase,UsrA01.a0100,UsrA01.b0110,UsrA01.e0122,UsrA01.e01a1,UsrA01.a0101,UsrA01.a0000,");
		}
		
		
		for(int k=0;k<fieldList.size();k++){
			FieldItem fi = (FieldItem)fieldList.get(k);
			if(
					"b0110".equals(fi.getItemid()) ||
					"e0122".equals(fi.getItemid()) ||
					"e01a1".equals(fi.getItemid()) ||
					"a0100".equals(fi.getItemid()) ||
					"a0101".equals(fi.getItemid()) ||
					"a0000".equals(fi.getItemid())
			){
				continue;
			}
			querySql.append(setPrefix).append(source).append(".").append(fi.getItemid()).append(",");
		}
		querySql.deleteCharAt(querySql.length()-1);
		
		
		String privfield = (String)this.formHM.get("privfield");
			
		
		/**
		 * 人员信息集 查询sql
		 */
		if(source.toUpperCase().startsWith("A")){
			querySql.append(" from ");
			querySql.append("Usr"+source);
			//人员子集 join a01表
			if(!"A01".equalsIgnoreCase(source.toUpperCase())){
				querySql.append(" left join UsrA01 on ").append("Usr"+source).append(".a0100=UsrA01.a0100 ");
			}
			//没有人员库权限时
			List dblist = getDbList();
			if(dblist.size()<1){
				querySql.append(" where 1=2 ");
				return querySql;
			}
			
			//如果不控制权限
			if(userView.isSuper_admin() || "".equals(privtype)){
				StringBuffer finalSql = new StringBuffer();
				for(int i=0;i<dblist.size();i++){
					String sqlItem = querySql.toString().replace("Usr", dblist.get(i).toString());
					finalSql.append(sqlItem);
					finalSql.append(" union all ");
				}
				finalSql.delete(finalSql.length()-10, finalSql.length());
				
				return finalSql;
			}
			
			//管理范围+高级
			if("manage".equals(privtype)){
				String privSql = userView.getPrivSQLExpression("Usr",false);
				if(privSql.startsWith(" from UsrA01"));
					privSql = privSql.substring(12);
				querySql.append(privSql);
			}else{
				//获取操作单位权限
				String privOrg = userView.getUnit_id();
				//如果不是走操作单位，就是走业务范围
				if(!"unit".equals(privtype)){
					privOrg = getBusiPriv(privtype);
				}
				privOrg = "UN".equalsIgnoreCase(privOrg)?"":privOrg;
				if(privOrg.trim().length()<1){
					querySql.append(" where 1=2 ");
					return querySql;
				}
					
				String[] unit = privOrg.split("`");
				if(unit.length<1){
					querySql.append(" where 1=2 ");
					return querySql;
				}
				
				querySql.append(" where (");
				for(int i=0;i<unit.length;i++){
					String keyfield = unit[i].startsWith("UN")?"UsrA01.B0110":"UsrA01.E0122";
					keyfield = privfield.length()>0?privfield:keyfield;
					querySql.append(keyfield);
					querySql.append(" like '");
					querySql.append(unit[i].substring(2));
					querySql.append("%'  or ");
				}
				querySql.append(" 1=2 ) ");
			
			}
			
			//上面Sql默认为Usr人员库，此处按人员库权限处理一下
			StringBuffer finalSql = new StringBuffer();
			for(int i=0;i<dblist.size();i++){
				String sqlItem = querySql.toString().replace("Usr", dblist.get(i).toString());
				finalSql.append(sqlItem);
				finalSql.append(" union all ");
			}
			finalSql.delete(finalSql.length()-10, finalSql.length());
			
			return finalSql;
		}
		
		
		/**
		 * 单位信息集 查询sql
		 */
		if(source.toUpperCase().startsWith("B")){
			//if(!source.equals("B01")){
				querySql.append(",B0110 ");
			//}
			querySql.append(" from ").append(source);
			//如果不控制权限
			if(userView.isSuper_admin() || "".equals(privtype)){
				return querySql;
			}
			
			privfield = privfield.length()>0?privfield:"B0110";
			
			//管理范围
			if("manage".equals(privtype)){
				String orgType = userView.getManagePrivCode();
				String orgCode = userView.getManagePrivCodeValue();
				//如果orgType为UN并且orgCode为空，说明是顶级权限
				if("UN".equals(orgType) && orgCode.length()<1)
					return querySql;
				//如果没有权限
				if("".equals(orgType) && "".equals(orgCode)){
					querySql.append(" where 1=2 ");
					return querySql;
				}
				
				//添加权限条件
				querySql.append(" where ").append(privfield).append(" like '"+orgCode+"%' ");
			}else{
				//获取操作单位权限
				String privOrg = userView.getUnit_id();
				//如果不是走操作单位，就是走业务范围
				if(!"unit".equals(privtype))
					privOrg = getBusiPriv(privtype);
				privOrg = "UN".equalsIgnoreCase(privOrg)?"":privOrg;
				if(privOrg.trim().length()<1){
					querySql.append(" where 1=2 ");
					return querySql;
				}
				
				String[] unit = privOrg.split("`");
				//如果没有权限
				if(unit.length<1){
					querySql.append(" where 1=2 ");
					return querySql;
				}
				//添加权限条件
				querySql.append(" where (");
				for(int i=0;i<unit.length;i++){
					querySql.append(privfield);
					querySql.append(" like '");
					querySql.append(unit[i].substring(2));
					querySql.append("%'  or ");
				}
				querySql.append(" 1=2 ) ");
			}
			return querySql;
		}
		
		/**
		 * 岗位信息集 查询sql
		 */
		querySql.append(",E01A1");
		querySql.append(",(select max(codeitemid) b0110 from organization where codesetid='UN' and codeitemid="+Sql_switcher.substr(source+".E01A1", "1",Sql_switcher.length("codeitemid"))+") B0110 ");
		if(!"K01".equals(source)){
			querySql.append(",(select max(codeitemid) e0122 from organization where codesetid='UM' and codeitemid="+Sql_switcher.substr(source+".E01A1", "1",Sql_switcher.length("codeitemid"))+") E0122 ");
		}else{
			querySql.append(",E0122");
		}
		querySql.append(" from ").append(source);
		
		//如果不控制权限
		if(userView.isSuper_admin() || "".equals(privtype)){
			return querySql;
		}
		
		privfield = privfield.length()>0?privfield:"E01A1";
		
		if("manage".equals(privtype)){
			String orgType = userView.getManagePrivCode();
			String orgCode = userView.getManagePrivCodeValue();
			if("UN".equals(orgType) && orgCode.length()<1){
				return querySql;
			}
			//如果没有权限
			if("".equals(orgType) && "".equals(orgCode)){
				querySql.append(" where 1=2 ");
				return querySql;
			}
			querySql.append(" where ").append(privfield).append(" like '"+orgCode+"%' ");
			
		}else{
			//获取操作单位权限
			String privOrg = userView.getUnit_id();
			//如果不是走操作单位，就是走业务范围
			if(!"unit".equals(privtype))
				privOrg = getBusiPriv(privtype);
			privOrg = "UN".equalsIgnoreCase(privOrg)?"":privOrg;
			if(privOrg.trim().length()<1){
				querySql.append(" where 1=2 ");
				return querySql;
			}
			
			String[] unit = privOrg.split("`");
			if(unit.length<1){
				querySql.append(" where 1=2 ");
				return querySql;
			}
			
			querySql.append(" where (");
			for(int i=0;i<unit.length;i++){
				querySql.append(privfield);
				querySql.append(" like '");
				querySql.append(unit[i].substring(2));
				querySql.append("%'  or ");
			}
			querySql.append(" 1=2 ) ");
		}
		
		return querySql;
	}
	
	private void createCustomViewTable() throws Exception{
		String source = (String)this.getFormHM().get("setname");
		
		//如果表不存在，创建表
		DbWizard dbw = new DbWizard(this.frameconn);
		if(!dbw.isExistTable(source,false) && this.getFormHM().get("exesql").toString().length()>0){
			ContentDAO dao = new ContentDAO(this.frameconn);
			dao.update(this.getFormHM().get("exesql").toString());
		}
		
		boolean hasNbase = false;
		boolean hasA0100 = false;
		boolean hasB0110 = false;
		boolean hasE0122 = false;
		boolean hasE01A1 = false;
		
		
		//防止source参数sql注入，关键字符处理
		source = SafeCode.keyWord_filter(source);
		String sql = "select * from "+source+" where 1=2 ";
		ContentDAO dao = new ContentDAO(this.frameconn);
		this.frowset = dao.search(sql);
		ResultSetMetaData metaData = this.frowset.getMetaData();
		
		for(int i=1;i<=metaData.getColumnCount();i++){
			String columnName = metaData.getColumnName(i); //字段名称
			columnName = columnName.toLowerCase();
			
			viewFieldList.add(columnName);
			
			FieldItem fi = DataDictionary.getFieldItem(columnName);
			ColumnsInfo columnInfo = null;
			if(fi!=null) {
				columnInfo = new ColumnsInfo(fi);
			}else {
				columnInfo = new ColumnsInfo();
				columnInfo.setColumnId(columnName);
				columnInfo.setColumnDesc(columnName);
				int columnType = metaData.getColumnType(i);    //字段类型
				switch(columnType){
					case Types.INTEGER:
					case Types.NUMERIC:  //数值型
						columnInfo.setColumnType("N");
						columnInfo.setTextAlign("right");
						columnInfo.setColumnLength(10);
						if(columnType==Types.NUMERIC){
							int preci=metaData.getScale(i);
				 			if(Sql_switcher.searchDbServer()==Constant.ORACEL&&preci==-127)
				 			{
				 				preci=2;
				 			}
				 			columnInfo.setDecimalWidth(preci);
						}
						break;
					case Types.CLOB:        //大文本
					case Types.LONGVARCHAR:
						columnInfo.setColumnType("M");
						break;
					case Types.DATE:
					case Types.TIME:
					case Types.TIMESTAMP:   //时间
						columnInfo.setColumnType("D");
						columnInfo.setColumnLength(19);
						columnInfo.setTextAlign("right");
						break;
					default:					//字符型
						columnInfo.setColumnType("A");
				}
			}
			
			if("NBASE".equals(columnName.toUpperCase())){
				hasNbase = true;
				columnInfo.setCodesetId("@@");
				//nbase即使不展示，也是需要加载的
				columnInfo.setLoadtype(ColumnsInfo.LOADTYPE_ALWAYSLOAD);
			}else if("A0100".equals(columnName.toUpperCase())){
				hasA0100 = true;
				columnInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
				columnInfo.setEncrypted(true);
			}else if("B0110".equals(columnName.toUpperCase())){
				hasB0110 = true;
				columnInfo.setCodesetId("UN");
			}else if("E0122".equals(columnName.toUpperCase())){
				hasE0122 = true;
				columnInfo.setCodesetId("UM");
			}else if("E01A1".equals(columnName.toUpperCase())){
				hasE01A1 = true;
				columnInfo.setCodesetId("@K");
			}
			
			//是否隐藏
			if(this.isColumnHide(columnName))
				continue;
			//添加渲染方法
			setColumnRenderer(columnInfo);
			columnsList.add(columnInfo);
		}
		
		renameColumnDesc();
		
		String privtype = (String)this.formHM.get("privtype");
		String infokind = (String)this.formHM.get("infokind");
		String privfield = (String)this.formHM.get("privfield");
		
		if(privtype.length()>0 && infokind.length()>0){
			//如果需要权限校验，则检查表中是否有必要的指标
			if("A".equals(infokind) && !(hasNbase && hasA0100 && hasB0110 && hasE0122 && hasE01A1)){
				if(!hasNbase)
					this.formHM.put("errorMsg", ResourceFactory.getProperty("customreport_nonbasefield"));
				else if(!hasA0100)
					this.formHM.put("errorMsg", ResourceFactory.getProperty("customreport_noa0100field"));
				else if(!hasB0110)
					this.formHM.put("errorMsg", ResourceFactory.getProperty("customreport_nob0110field"));
				else if(!hasE0122)
					this.formHM.put("errorMsg", ResourceFactory.getProperty("customreport_noe0122field"));
				else if(!hasE01A1)
					this.formHM.put("errorMsg", ResourceFactory.getProperty("customreport_noe01a1field"));
				return;
			}else if("B".equals(infokind) && !hasB0110){
				this.formHM.put("errorMsg", ResourceFactory.getProperty("customreport_nob0110field"));
				return;
			}else if("K".equals(infokind) && !hasE01A1){
				this.formHM.put("errorMsg", ResourceFactory.getProperty("customreport_noe01a1field"));
				return;
			}
		}
		
		dataSql = createViewQuerySql(source,privtype,infokind,privfield).toString();
		
		groupSumHandle();
		
		createTableConfig();
	}
	
	private StringBuffer createViewQuerySql(String source,String privtype,String infokind,String privfield) throws GeneralException{
		
		StringBuffer privSql = new StringBuffer();
		privSql.append("select * from ");
		privSql.append(source);
		/**
		 * 1、超级用户，不控制权限
		 * 2、privtype为空（没有指定如何控制），不控制权限
		 * 3、infokind为空并且privfield为空，不控制权限
		 */
		if(userView.isSuper_admin() || "".equals(privtype) || ( "".equals(infokind) && "".equals(privfield) )){
			return privSql;
		}
		
		//如果是人员且走管理范围权限，并且没有指定固定的权限判断指标，走高级授权
		if("A".equals(infokind) && "manage".equals(privtype) && "".equals(privfield)){
			List dblist = getDbList();
			if(dblist.size()<1){
				privSql.append(" where 1=2 ");
				return privSql;
			}
			String privExpress = userView.getPrivExpression();
			
			if(privExpress==null || privExpress.length()<1){
				privSql.append(" where 1=2 ");
				return privSql;
			}
				
			
			String expression = privExpress.split("\\|")[0];
			String factors = privExpress.split("\\|")[1];
			FactorList factor = new FactorList(expression, factors, "");
			ArrayList list = factor.getFieldList();
			String fields = "";
			for(int i=0;i<list.size();i++){
				FieldItem item = (FieldItem)list.get(i);
				if(
						"b0110".equalsIgnoreCase(item.getItemid()) ||
						"e0122".equalsIgnoreCase(item.getItemid()) ||
						"e01a1".equalsIgnoreCase(item.getItemid()) ||
						"a0100".equalsIgnoreCase(item.getItemid())
					){
					
					list.remove(i);
					i--;
					continue;
				}
					
				if(viewFieldList.contains(item.getItemid().toLowerCase())){
					list.remove(i);
					i--;
					continue;
				}
					
				if(!"A01".equalsIgnoreCase(item.getFieldsetid())){
					list.remove(i);
					i--;
					continue;
				}
				
				fields+=",UsrA01."+item.getItemid();
			}
			
			//privSql.setLength(0);
			StringBuffer mainDataStr = new StringBuffer();
			mainDataStr.append("select ");
			mainDataStr.append(source).append(".* ");
			if(fields.length()>0){
				mainDataStr.append(fields);
			}
			mainDataStr.append(" from ");
			mainDataStr.append(source);
			mainDataStr.append(" left join UsrA01 on ");
			mainDataStr.append(source).append(".a0100 = UsrA01.a0100");
			mainDataStr.append(" where ").append(source).append(".nbase='Usr'");
			
			String managePrivSql = userView.getPrivSQLExpression("Usr", false);
			
			String mainPrivSql = " select * from ("+mainDataStr+") UsrA01 "+managePrivSql.substring(13); 
			
			
			privSql.setLength(0);
			privSql.append("select * from (");
			
			for(int i=0;i<dblist.size();i++){
				 String realSql = mainPrivSql.replace("Usr", (String)dblist.get(i));
				 privSql.append(realSql);
				 privSql.append(" union all ");
			 }
			
			privSql.delete(privSql.length()-10, privSql.length());
			 
			privSql.append(") unionTab");
			
			return privSql;
			
			/*
			privSql.append(" where nbase").append(Sql_switcher.concat()).append("A0100 in (");
			 String manageSql = userView.getPrivSQLExpression("Usr", false);
			 manageSql = "select 'Usr'"+Sql_switcher.concat()+"A0100 "+manageSql;
			 
			 for(int i=0;i<dblist.size();i++){
				 String realSql = manageSql.replace("Usr", (String)dblist.get(i));
				 privSql.append(realSql);
				 privSql.append(" union all ");
			 }
			 privSql.delete(privSql.length()-10, privSql.length());
			 
			 privSql.append(") ");
			
			 return privSql;
			 */
		}
		
		//不走高级的其他情况	
		privSql.append(" where 1=1 ");
		//如果是人员视图，需控制人员库
		if("A".equals(infokind)){
			List dblist = getDbList();
			if(dblist.size()<1){
				privSql.append(" and 1=2 ");
				return privSql;
			}
			
			privSql.append(" and (UPPER(nbase) in ( ");
			for(int i=0;i<dblist.size();i++){
				privSql.append("'").append(dblist.get(i).toString().toUpperCase()).append("',");
			}
			privSql.deleteCharAt(privSql.length()-1);
			privSql.append(") ");
			privSql.append(") ");
		}
		
		String privCode = "";
		if("manage".equals(privtype)){
			privCode = userView.getManagePrivCode()+userView.getManagePrivCodeValue();
			privCode = "UN".equals(privCode)?"UN%":privCode;
		}else if("unit".equals(privtype)){
			privCode = userView.getUnit_id();
		}else{
			privCode = getBusiPriv(privtype);
		}
		
		if(privCode==null || privCode.length()<1){
			privSql.append(" and 1=2 ");
			return privSql;
		}
		
		String[] unit = privCode.split("`");
		privSql.append(" and (");
		for(int i=0;i<unit.length;i++){
			if(unit[i].length()<2)
				continue;
			String key = privfield;
			if(key.length()<1){
				//如果是A(人员视图)，则根据权限机构前缀判断 查询指标，例如UN=B0110，UM=E0122
				key = "A".equals(infokind)?(unit[i].startsWith("UN")?"B0110":"E0122"):"B0110";
				//如果是B(单位视图)，指标为b0110
				key = "B".equals(infokind)?"B0110":key;
				//如果是B(单位视图)，指标为E01A1
				key = "K".equals(infokind)?"E01A1":key;
			}
			privSql.append(key).append(" like '").append(unit[i].substring(2)).append("%' or ");
		}
		privSql.append(" 1=2 ) ");
		
		return privSql;
	}
	
	/*只有报表关联才能使用分组，传参生成报表不支持分组*/
	private void groupSumHandle(){
		
		String group_sum_field = (String)this.formHM.get("groupfield");
		if(group_sum_field==null || group_sum_field.length()<1)
			return;
		//分组参数处理，转小写，方便判断
		group_sum_field = ","+group_sum_field+",";
		group_sum_field = group_sum_field.toLowerCase();
		//查询语句
		StringBuffer searchField = new StringBuffer("select ");
		//group语句
		StringBuffer groupField = new StringBuffer(" group by ");
		
		//对列对象进行处理，过滤掉处 分组字段和数值字段以外的列
		for(int i=0;i<columnsList.size();i++){
			ColumnsInfo info = (ColumnsInfo)columnsList.get(i);
			
			//如果是分组指标，添加到 查询语句 和 group语句中
			if(group_sum_field.indexOf(","+info.getColumnId().toLowerCase()+",")!=-1){
				searchField.append(info.getColumnId()).append(",");
				groupField.append(info.getColumnId()).append(",");
				continue;
			}
			//如果是数值型，拼加 sum 到 查询语句中。排除I9999
			if("N".equals(info.getColumnType()) && !"i9999".equals(info.getColumnId().toLowerCase())){
				/*oracle 涉及计算时 ResultSetMetaData取到的数值列小数精度会丢失，这里转一下 guodd 2019-08-02*/
				if(Sql_switcher.searchDbServer()==Constant.ORACEL)
					searchField.append(" cast(");
				searchField.append("sum(").append(info.getColumnId()).append(") ");
				if(Sql_switcher.searchDbServer()==Constant.ORACEL)
					searchField.append(" as number(10,"+info.getDecimalWidth()+"))");
				searchField.append(" as ").append(info.getColumnId()).append(","); 
				continue;
			}
			//其他指标不再显示
			columnsList.remove(info);
			i--;
		}
		//删除最后无用的逗号
		searchField.deleteCharAt(searchField.length()-1);
		groupField.deleteCharAt(groupField.length()-1);
		//组成新的sql语句
		/*String sql = dataSql.toString();
		dataSql.setLength(0);
		dataSql.append(searchField);
		dataSql.append(" from (");
		dataSql.append(sql);
		dataSql.append(" ) D ");
		dataSql.append(groupField);
		*/
		dataSql = searchField.toString()+" from ("+dataSql+") D "+groupField.toString();
		
		orderSql = groupField.toString().replace("group by","order by");
	}
	
	/**
	 * 读取参数
	 * @param reportid
	 * @return
	 * @throws GeneralException
	 */
	private boolean loadSimpleReportParam(String reportid) throws GeneralException{
		//报表名称
		String reportname,
		//数据表名称
		setname,
		//默认查询指标
		queryfield,
		//分组指标
		groupfield=null,
		exportSetUser=null,
		//权限类型
		privtype=null,
		//数据集类型
		infokind=null,
		//人员库
		nbase=null,
		//权限控制指标
		privfield = null,
		//业务模板
		templates = null,
		//默认条件
		filter = null;
		try{
			//查询该记录
			String sql = "select name,report_type,sqlfile from t_custom_report where id=? ";
			ArrayList values = new ArrayList();
			values.add(Integer.parseInt(reportid));
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset = dao.search(sql,values);
			//检查报表是否存在
			if(!this.frowset.next()){
				this.formHM.clear();
				this.formHM.put("errorMsg", ResourceFactory.getProperty("customreport_commonerror"));
				return false;
			}
			//检查报表类型是否正确
			int report_type = this.frowset.getInt("report_type");
			if(report_type!=4){
				this.formHM.clear();
				this.formHM.put("errorMsg", ResourceFactory.getProperty("customreport_commonerror"));
				return false;
			}
			
			reportname = this.frowset.getString("name");
			String sqlfile = Sql_switcher.readMemo(frowset, "sqlfile");
			
			if(sqlfile==null || sqlfile.trim().length()<1){
				this.formHM.clear();
				this.formHM.put("errorMsg", ResourceFactory.getProperty("customreport_commonerror"));
				return false;
			}
				
			Document doc = PubFunc.generateDom(sqlfile);
			
			Element sourceEle = doc.getRootElement().getChild("source");
			if(sourceEle==null){
				this.formHM.clear();
				this.formHM.put("errorMsg", ResourceFactory.getProperty("customreport_commonerror"));
				return false;
			}
			
			setname = sourceEle.getAttributeValue("setname");
			
			if(setname==null || setname.length()<1){
				this.formHM.clear();
				this.formHM.put("errorMsg", ResourceFactory.getProperty("customreport_commonerror"));
				return false;
			}
			
			queryfield = sourceEle.getAttributeValue("queryfield");
			groupfield = sourceEle.getAttributeValue("groupfield");
			exportSetUser = sourceEle.getAttributeValue("export_set_user");
			
			Element privEle = doc.getRootElement().getChild("privsetting");
			if(privEle!=null){
				privtype = privEle.getAttributeValue("type");
				infokind = privEle.getAttributeValue("infokind");
				nbase = privEle.getAttributeValue("nbase");
				privfield = privEle.getAttributeValue("privfield");
			}

			Element templatesEle = doc.getRootElement().getChild("templates");
			if(templatesEle!=null) {
				templates = templatesEle.getText();
			}

			Element filterEle = doc.getRootElement().getChild("filter");
			if(filterEle!=null){
				filter = filterEle.getText();
			}

			Element defineSourceEle = doc.getRootElement().getChild("define_source");
			Element columns=null,ctrlEle=null,exesqlEle=null;
			
			if(defineSourceEle!=null){
				columns = defineSourceEle.getChild("columns");
				ctrlEle = defineSourceEle.getChild("field_display_ctrl");
				exesqlEle = defineSourceEle.getChild("exesql");
			}
			
			HashMap fieldTextMap = new HashMap();
			if(columns!=null){
				List childs = columns.getChildren("column");
				for(int i=0;i<childs.size();i++){
					Element column = (Element)childs.get(i);
					String field = column.getAttributeValue("field");
					if(column.getAttributeValue("text")!=null && column.getAttributeValue("text").length()>0)
						fieldTextMap.put(field.toLowerCase(), column.getAttributeValue("text"));
					if(column.getAttributeValue("url")!=null && column.getAttributeValue("url").length()>0)
						columnLinkMap.put(field.toLowerCase(), column.getAttributeValue("url"));
				}
			}
			
			if(ctrlEle!=null){
				String type = ctrlEle.getAttributeValue("type");
				this.viewCtrlType = "hide".equalsIgnoreCase(type)?"hide":"show";
				this.viewCtrlField = ","+ctrlEle.getText().trim().toLowerCase()+",";
			}
			
			String exesql = "";
			if(exesqlEle!=null){
				exesql = exesqlEle.getText();
			}
			
			queryfield= queryfield==null?"":queryfield;
			groupfield= groupfield==null?"":groupfield;
			exportSetUser = exportSetUser==null?"":exportSetUser;
			privtype= privtype==null?"":privtype;
			infokind= infokind==null?"":infokind;
			nbase= nbase==null?"":nbase;
			privfield = privfield==null?"":privfield;
			exesql= exesql==null?"":exesql;
			templates = templates==null?"":templates;
			filter = filter==null?"":filter;

			this.formHM.put("title", reportname);
			this.formHM.put("setname", setname);
			this.formHM.put("queryfield", queryfield);
			this.formHM.put("groupfield", groupfield);
			this.formHM.put("exportSetUser", exportSetUser);
			this.formHM.put("privtype", privtype);
			this.formHM.put("infokind", infokind);
			this.formHM.put("nbase", nbase);
			this.formHM.put("privfield", privfield);
			this.formHM.put("fieldTextMap", fieldTextMap);
			this.formHM.put("exesql", exesql);
			this.formHM.put("templates", templates);
			this.formHM.put("filter", filter);
			this.formHM.put("subModuleId", "system_custom_report_"+reportid);
			
		} catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return true;
	}
	
	private void renameColumnDesc() {
		HashMap fieldTextMap = (HashMap)this.getFormHM().get("fieldTextMap");
		fieldTextMap = fieldTextMap==null?new HashMap():fieldTextMap;
		for(int k=0;k<columnsList.size();k++) {
			ColumnsInfo co = (ColumnsInfo)columnsList.get(k);
			String columnName = co.getColumnId();
			if(fieldTextMap.containsKey(columnName)){
				String columnText = (String)fieldTextMap.get(columnName);
				co.setColumnDesc(columnText);
				co.setColumnRealDesc(columnText);
			}
		}
	}
	
	private boolean loadComponentParam(){
		
		this.formHM.put("fieldTextMap", new HashMap());
		
		if(this.formHM.containsKey("fieldDisplayCtrl")){
			DynaBean displayCtrl = (DynaBean)this.formHM.get("fieldDisplayCtrl");
			HashMap newMap = PubFunc.DynaBean2Map(displayCtrl);
			String type = (String)newMap.get("type");
			this.viewCtrlType = "hide".equalsIgnoreCase(type)?"hide":"show";
			this.viewCtrlField = ","+(String)newMap.get("fields")+",";
		}
		
		return true;
	}
	
	private List getDbList(){
		String nbase = (String)this.formHM.get("nbase");
		List dblist = null;
		if("self".equals(nbase)){
			dblist = userView.getPrivDbList();
		}else if(nbase==null || "all".equals(nbase) || "".equals(nbase)){
			dblist = DataDictionary.getDbpreList();
		}else{
			dblist = Arrays.asList(nbase.split(","));
		}
		return dblist;
	}
	
	/**
	 * userview中没有获取纯业务范围的方法，通过反射获取业务范围数据，手动获取
	 * @param busiId
	 * @return
	 */
	private String getBusiPriv(String busiId){
		return this.userView.getBusiPriv(busiId);
	}
	
	private void createTableConfig(){
		String title = (String)this.formHM.get("title");
		String subModuleId = (String)this.formHM.get("subModuleId");
		//创建对象，传入必要参数
		TableConfigBuilder builder = new TableConfigBuilder(subModuleId, columnsList, 
															"EhrCommonTableCmp", userView, 
															this.frameconn);
		//报表名称
		builder.setTitle(title);
		//自动渲染全屏显示
		builder.setAutoRender(true);
		//传入数据sql
		builder.setDataSql(dataSql.toString());
		builder.setOrderBy(orderSql);
		
		ArrayList toolList = new ArrayList();
		//导出按钮
		ButtonInfo button = new ButtonInfo();
		button.setFunctype(ButtonInfo.FNTYPE_EXPORT);
		button.setText(ResourceFactory.getProperty("button.export"));
		button.setIcon("/images/outExcel.png");
		toolList.add(button);

		//页面设置 当前用户在xml中指定才可以设置
		String[] users = ((String)this.formHM.get("exportSetUser")).toUpperCase().split(",");
		List exportSetUserList = Arrays.asList(users);
		if(userView.isSuper_admin() || exportSetUserList.contains(userView.getUserName().toUpperCase())) {
			button = new ButtonInfo();
			button.setFunctype(ButtonInfo.FNTYPE_EXPORT_SETTING);
			button.setText(ResourceFactory.getProperty("muster.pageset"));
			button.setIcon("/images/img_o.gif");
			toolList.add(button);
		}

		//栏目设置
		button = new ButtonInfo();
		button.setText(ResourceFactory.getProperty("label.grid.scheme"));
		button.setFunctype(ButtonInfo.FNTYPE_SCHEME);
		toolList.add(button);
		
		builder.setTableTools(toolList);
		
		builder.setScheme(true);
		builder.setSchemePosition(TableConfigBuilder.SCHEME_POSITION_TOOLBAR);
		builder.setAnalyse(true);
		builder.setColumnFilter(true);
		builder.setSortable(true);
		builder.setLockable(true);
		builder.setFieldAnalyse(true);

		builder.setShowRowNumber(true);



		String infokind = (String)this.formHM.get("infokind");
		String templates = (String)this.formHM.get("templates");
		//为防止出错，如果没有配置infokind，不显示业务办理功能
		if(infokind.length()<1) {
			templates = "";
		}
		ArrayList templateList = new ArrayList();
		if(templates.length()>0) {
			String[] templateArray = templates.split(",");
			for(int i = 0; i < templateArray.length;i++) {
				if(!userView.isHaveResource(IResourceConstant.RSBD,templateArray[i]))
				{
					continue;
				}
				RecordVo vo = TemplateStaticDataBo.getTableVo(Integer.parseInt(templateArray[i]),this.frameconn);
				HashMap map = new HashMap();
				map.put("tempid",templateArray[i]);
				map.put("text",vo.getString("name"));
				templateList.add(map);
			}
			builder.setSelectable(true);
		}
		//生成表格参数
		String tableConfig = builder.createExtTableConfig();
		String filter = (String)this.formHM.get("filter");
		if(filter.length()>0){
			TableDataConfigCache cache = (TableDataConfigCache) this.userView.getHm().get(subModuleId);
			cache.setQuerySql(filter);
		}
		HashMap queryMap = createQueryData();
		this.formHM.clear();
		this.formHM.put("tableConfig", tableConfig);
		this.formHM.put("columnLinkMap",this.columnLinkMap);
		this.formHM.put("codeFieldMap",this.codeFieldMap);
		this.formHM.put("templateList",templateList);
		this.formHM.put("infokind",infokind);
		this.formHM.put("subModuleId", subModuleId);
		this.formHM.putAll(queryMap);
	}
	
	private HashMap createQueryData(){
		HashMap queryMap = new HashMap();
		ArrayList storageQueryField = new ArrayList();
		ArrayList defaultQueryField = new ArrayList();
		String queryfield = (String)this.formHM.get("queryfield");
		queryfield = queryfield==null?"":queryfield;
		queryfield = (","+queryfield+",").toLowerCase();
		for(int i=0;i<columnsList.size();i++){
			ColumnsInfo info = (ColumnsInfo)columnsList.get(i);
			
			HashMap field = new HashMap();
			field.put("itemid",info.getColumnId());
			field.put("itemdesc", info.getColumnDesc());
			field.put("itemtype", info.getColumnType());
			field.put("codesetid", info.getCodesetId());
			if("D".equals(info.getColumnType()))
				field.put("formatlength", info.getColumnLength());
			else if("N".equals(info.getColumnType()))
				field.put("formatlength",info.getDecimalWidth());
			
			storageQueryField.add(field);
			if(queryfield.indexOf(","+info.getColumnId().toLowerCase()+",")!=-1){
				defaultQueryField.add(field);
			}
		}
		queryMap.put("storageQueryField", storageQueryField);
		if(defaultQueryField.size()>0) {
			queryMap.put("defaultQueryField", defaultQueryField);
		}
		return queryMap;
	}
	
}
