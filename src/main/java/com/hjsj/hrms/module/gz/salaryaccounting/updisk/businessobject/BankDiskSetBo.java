package com.hjsj.hrms.module.gz.salaryaccounting.updisk.businessobject;

import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryAccountBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class BankDiskSetBo {
	private Connection conn = null;
	/** 薪资表名称 */
	private String gz_tablename;
	/** 薪资类别号 */
	private int salaryid = -1;
	/** 登录用户 */
	private UserView userview;
	private SalaryAccountBo salaryAccountBo = null;
	private SalaryTemplateBo salaryTemplateBo = null;
	/** 工资管理员，对共享类别有效* */
	private String manager = "";
	private String appdate = "";//业务日期，薪资审批里面会用到
	private String appCount="";//发放次数
	public BankDiskSetBo(Connection conn, int salaryid, UserView userview) {
		super();
		this.conn = conn;
		this.salaryid = salaryid;
		this.userview = userview;
		this.salaryAccountBo = new SalaryAccountBo(conn, this.userview,
				salaryid);
		this.salaryTemplateBo = this.salaryAccountBo.getSalaryTemplateBo();
		this.manager = this.salaryTemplateBo.getManager();
		this.gz_tablename = this.salaryTemplateBo.getGz_tablename();
	}

	/**
	 * 获取银行列表的方法
	 * @return 银行列表list
	 * @throws GeneralException
	 */
	public ArrayList getBankList() throws GeneralException {
		ArrayList list = new ArrayList();
		try {
			StringBuffer sql = new StringBuffer("");
			if (this.userview.isSuper_admin()) {// 超级管理员
				sql.append("select bank_id,bank_name,username from gz_bank order by bank_id");
			} else {
				sql.append("select bank_id,bank_name,username from gz_bank ");
				sql.append("where (scope=1 and username='");
				sql.append(this.userview.getUserName());
				sql.append("')");
				sql.append(" or (scope is null or scope=0) order by bank_id");
			}
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql.toString());
			while (rs.next()) {
				HashMap map = new HashMap();
				map.put("bankid", rs.getString("bank_id"));
				map.put("bankname", rs.getString("bank_name"));
				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

		return list;
	}

	/**
	 * 取得新建的银行模板的id
	 * 
	 * @return
	 * @throws GeneralException
	 */
	public String getFirstBank_id() throws GeneralException {
		String bank_id = "0";
		try {
			String sql = "select bank_id,bank_name from gz_bank ";
			if (this.userview.isSuper_admin()) {
				sql += " order by bank_id";
			} else {
				sql += " where (scope=1 and username='"+this.userview.getUserName()+"') or (scope is null or scope=0) order by bank_id";
			}
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(sql);
			while (rs.next()) {
				bank_id = rs.getString("bank_id");
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return bank_id;
	}

	/**
	 * 拼接列
	 * 
	 * @param bankid
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getFieldList(String bankid)
			throws GeneralException {
		StringBuffer sql = new StringBuffer();
		ArrayList fieldList = new ArrayList();
		 HashMap<String,String> salarySetMap = this.getSalarySetFields(salaryid);
		try {
			FieldItem info = new FieldItem();
			info.setItemid("aid");//人员编号
			info.setItemtype("M");
			info.setCodesetid("0");
			info.setAlign("center");
			info.setItemdesc("aid");
			info.setItemlength(100);
			fieldList.add(info);
			
			sql.append("select item_name,field_name,item_type,format from gz_bank_item ");
			sql.append(" where bank_id=");
			sql.append(bankid);
			sql.append(" order by norder");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql.toString());
				while (rs.next()) {
					String fieldName = rs.getString("field_name");
					if(salarySetMap.get(fieldName) == null)
						continue;
					info = new FieldItem();
					info.setUseflag("1");
					// item_type =0数值N =1日期D =2字符A 大文本M
					String itemtype = rs.getString("item_type");
					String type = "";
					String format = rs.getString("format");
					if ("0".equals(itemtype)) {
						type = "N";
//						type = "A";
						if(format!=null&&format.length()!=0){
							int decimalStr = format.indexOf(".");
							int length = decimalStr==-1?0:format.length()-decimalStr-1;
							info.setDecimalwidth(length);
							if(decimalStr!=-1)
								length=length+1;
							info.setItemlength(format.length()-length);
							info.setFormat(format);
						}
						else{
							if(DataDictionary.getFieldItem(fieldName)!=null){
							info.setItemlength(DataDictionary.getFieldItem(fieldName).getItemlength());
							info.setDecimalwidth(DataDictionary.getFieldItem(fieldName).getDecimalwidth());
							}else if(!"A00Z1".equalsIgnoreCase(fieldName) && !"A00Z3".equalsIgnoreCase(fieldName)){//A00Z1和A00Z3显示成了小数
								info.setItemlength(12);
								info.setDecimalwidth(4);
							}else {
								info.setItemlength(12);
								info.setDecimalwidth(0);
							}
						}
					} else if ("1".equals(itemtype)) {
						type = "D";
						info.setItemlength(10);
						if(format!=null&&format.length()>0){
							info.setItemlength(format.length());
							info.setFormat(format);
						}
					} else {
						type = "A";
						info.setItemlength(100);
					}
					String itemName = rs.getString("item_name");
					info.setItemid(fieldName);
					info.setItemtype(type);
					info.setItemdesc(itemName);
					StringBuffer buf = new StringBuffer();
					buf.append("select codesetid,fieldSetId from salaryset where itemid='");
					buf.append(fieldName);
					buf.append("'");
					RowSet rst = dao.search(buf.toString());
					String codesetid = "0";
					String fieldsetid="none";
					if(rst.next()) {
						codesetid = rst.getString("codesetid");
						if(!"A00".equalsIgnoreCase(rst.getString("fieldSetId")))
							fieldsetid=rst.getString("fieldSetId");
					}
					info.setFieldsetid(fieldsetid);
					info.setCodesetid(codesetid);
					info.setAlign("center");
					fieldList.add(info);
				}
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return fieldList;
	}

	/**
	 * @author lis
	 * @Description: 取得薪资类别的所有指标
	 * @date 2015-11-27
	 * @param salaryid
	 * @return
	 */
	public HashMap getSalarySetFields(int salaryid)
	{
		HashMap<String,String> map = new HashMap<String,String>();
		try
		{
			String sql = "select itemid from salaryset where salaryid="+salaryid;
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs= null;
			rs=dao.search(sql);
			while(rs.next())
			{
				map.put(rs.getString("itemid").toUpperCase(),rs.getString("itemid"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	/**
	 * 取得显示页面字段
	 * 
	 * @param fieldList
	 *            列表字段
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList<ColumnsInfo> toColumnsInfo(ArrayList<FieldItem> fieldList)
			throws GeneralException {
		ArrayList<ColumnsInfo> list = new ArrayList<ColumnsInfo>();
		try {
			for (int i = 0; i < fieldList.size(); i++) {
				FieldItem item = (FieldItem) fieldList.get(i);
				ColumnsInfo info = new ColumnsInfo(item);
				info.setColumnWidth(150);
				if ("aid".equalsIgnoreCase(item.getItemid())) {
					info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
				}
				
				if(!this.userview.isSuper_admin()&&!"1".equals(this.userview.getGroupId()))
				{
					if(item!=null&&("UN".equalsIgnoreCase(info.getCodesetId())|| "@K".equalsIgnoreCase(info.getCodesetId())|| "UM".equalsIgnoreCase(info.getCodesetId())))
					{
						info.setCtrltype("3");
						info.setNmodule("1");
					}
				} 
				list.add(info);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return list;
	}

	/**
	 * 获取银行报盘的按钮
	 * 
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getUpBankButtonList(String queryBoxText,boolean isedit) throws GeneralException {
		ArrayList buttonList = new ArrayList();
		try {
			buttonList
					.add(new ButtonInfo(ResourceFactory
							.getProperty("gz.bankdisk.updisk"),
							"gz_updisk.getBankList"));// 生成报盘
			buttonList.add(new ButtonInfo(ResourceFactory
					.getProperty("gz_new.gz_accounting.bankdisk.addupdisk"),
					"gz_updisk.addBankTemplate"));// 新建报盘
			
			ButtonInfo button = new ButtonInfo(ResourceFactory
					.getProperty("gz_new.gz_accounting.bankdisk.modupdisk"),
					"gz_updisk.editBankTemplate");
			button.setDisabled(!isedit);
			buttonList.add(button);// 编辑报盘
//			buttonList.add(new ButtonInfo(ResourceFactory
//					.getProperty("gz_new.gz_accounting.bankdisk.modupdisk"),
//					"gz_updisk.editBankTemplate"));// 编辑报盘
			button=new ButtonInfo(ResourceFactory
					.getProperty("gz_new.gz_accounting.bankdisk.delupdisk"),
					"gz_updisk.deleteBankTemplate");
			button.setDisabled(!isedit);
			buttonList.add(button);// 删除报盘
			// 加搜索条
			//buttonList.add(new ButtonInfo("<div id='fastsearch'> </div>"));
			
//			ButtonInfo button = new ButtonInfo(queryBoxText,ButtonInfo.TYPE_QUERYBOX,"GZ00000121");
//			button.setType(ButtonInfo.TYPE_QUERYBOX);
//			buttonList.add(button);

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return buttonList;
	}
	/**
	 * 获取是否具有修改权限
	 * @param bankid
	 * @return
	 * @throws GeneralException
	 */
	public boolean getBankIsEdit(String bankid) throws GeneralException {
		boolean bool=false;
		try{
			if(this.userview.isSuper_admin())
				return true;
			ContentDAO dao = new ContentDAO(this.conn);
			String strSql="select 1 from gz_bank where username='"+this.userview.getUserName()+"' and bank_id=? ";
			ArrayList datalist=new ArrayList();
			datalist.add(bankid);
			RowSet rs=dao.search(strSql, datalist);
			if(rs.next())
				bool=true;
			
		}catch (Exception e) {
			e.printStackTrace();
		throw GeneralExceptionHandler.Handle(e);
		}
		return bool;
	}
    /**
     * 创建临时表 
     * @param bank_id 银行模板id
     * @param dataList 需要插入数据列表
     * @param columns 列信息
     */
    public void createBankDiskTempTable(String bank_id,ArrayList dataList,ArrayList columns)
    {
    	try
    	{
    		String midtable="t#"+this.userview.getUserName()+"_gz";
    		DbWizard dbWizard=new DbWizard(this.conn);
    		Table table=new Table(midtable);
    		table.setCreatekey(false);
    		Field temp = null;
    		for(int i=0;i<columns.size();i++)
    		{
    			FieldItem fielditem  =(FieldItem)columns.get(i);
    			int length=100;
    			temp =new Field((String)fielditem.getItemid(),(String)fielditem.getItemdesc());
    			temp.setVisible(true);
    			temp.setKeyable(false);
    			temp.setNullable(true);
//    			if(fielditem.getItemtype().equals("A")){
//    				temp.setDatatype(DataType.STRING);
//    			}else if(fielditem.getItemtype().equals("D")){
//    				temp.setDatatype(DataType.DATE);
//    			}else if(fielditem.getItemtype().equals("N")){
//    				temp.setDatatype(DataType.DOUBLE);
//    			}else{
//    				temp.setDatatype(DataType.STRING);
//    			}
    			temp.setDatatype(DataType.STRING);//数据类型均为string
    			temp.setAlign("left");
    			temp.setSortable(true);
    			//设置字段长度 为页面输出长度最小100，大于100则加100，最大4000
    			if("un".equalsIgnoreCase(fielditem.getCodesetid())|| "um".equalsIgnoreCase(fielditem.getCodesetid()))
    				length=255;
    			else if(fielditem.getItemlength()>100&&fielditem.getItemlength()+length<4000)
    				length=fielditem.getItemlength()+length;
    			else if(fielditem.getItemlength()+length>4000)
    				length=4000;
    			temp.setLength(length);
    			//temp.setLength(fielditem.getItemlength());
    			//temp.setDecimalDigits(fielditem.getDecimalwidth());
    			table.addField(temp);
    		}
			if(dbWizard.isExistTable(table.getName(),false))
			{
				dbWizard.dropTable(table);
			}
			
			dbWizard.createTable(table);// table created
			/**import data*/
			importDataFromSalaryToTempTable(dataList,columns,midtable);

    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
    /**
     * 向临时表插入数据的方法
     * @param dataList
     * @param columns
     * @param midtable　临时表表名
     */
    private void importDataFromSalaryToTempTable(ArrayList dataList,ArrayList columns,String midtable)
    {
    	try
    	{
    		StringBuffer columnsBuf= new StringBuffer();
    		StringBuffer valueBuf = new StringBuffer();
    		StringBuffer insertSqlBuf= new StringBuffer();
    		ContentDAO dao = new ContentDAO(this.conn);
    		
    		if(dataList.size()==0)
    			return;
    		
    		ArrayList recordList=new ArrayList();
    		ArrayList beanList=new ArrayList();
    		for(int j=0;j<dataList.size();j++)
    		{
    			LazyDynaBean bean = (LazyDynaBean)dataList.get(j);
    			
    			beanList=new ArrayList();
    			for(int k=0;k<columns.size();k++)
    			{
    				FieldItem fielditem=(FieldItem)columns.get(k);
    				if(j==0)
    				{
    		    		columnsBuf.append(",");
        	    		columnsBuf.append((String)fielditem.getItemid());
        	    		
    				}
    				valueBuf.append(",");
    				valueBuf.append("?");
    				if(bean.get(fielditem.getItemid().toLowerCase()).toString()==null|| "".equals(bean.get((String)fielditem.getItemid().toLowerCase()).toString()))
    				{
    					if("N".equals(fielditem.getItemtype()))
    						beanList.add(0);
    					else
    						beanList.add("");
    				}
    				else
    				{
//    					if(fielditem.getItemtype().equals("D")){
//    						String value = (String)bean.get(((String)fielditem.getItemid()).toLowerCase());
//    						
//    						value=getDateFormat(value,fielditem.getFormat());
////    						Date date = null;
////    						if(fielditem.getFormat()!=null&&!fielditem.getFormat().equals(""))
////    						date = DateUtils.getSqlDate(value,fielditem.getFormat());
////    						else
////    							date = DateUtils.getSqlDate(value,"yyyy-MM-dd");
////							if(fielditem.getItemlength()>7)
////								date = DateUtils.getSqlDate(value,"yyyy-MM-dd");
////							else
////								date = DateUtils.getSqlDate(value,"yyyy-MM");
//    						beanList.add(value);
//    					}else{
    		    			beanList.add((String)bean.get(((String)fielditem.getItemid()).toLowerCase()));
    					//}
    				}
    			}
				if(j==0)
				{
	    			insertSqlBuf.append("insert into ");
	    			insertSqlBuf.append(midtable);
	    			insertSqlBuf.append(" (");
	    			insertSqlBuf.append(columnsBuf.toString().substring(1));
	    			insertSqlBuf.append(") values (");
	    			insertSqlBuf.append(valueBuf.toString().substring(1));
	    			insertSqlBuf.append(")");
				}
				recordList.add(beanList);
    		}
    		dao.batchInsert(insertSqlBuf.toString(),recordList);
    		
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
   
	/**
	 * 拼接sql
	 * 
	 * @param columnList
	 * @param bankid
	 * @return
	 * @throws GeneralException
	 */
	public String getTableListSql(ArrayList columnList, String bankid,String condSql,String tableName)
			throws GeneralException {
		StringBuffer sql = new StringBuffer();
		try {
			if (columnList == null || columnList.size() == 0)
				return sql.toString();
			StringBuffer colBuf = new StringBuffer();
			StringBuffer codebuf = new StringBuffer("");
			// 得到银行模板的格式
			HashMap map = this.getFormatMap(columnList, bankid);
			for (int j = 0; j < columnList.size(); j++) {
				FieldItem fielditem = (FieldItem) columnList.get(j);
				if (fielditem != null &&( "aid".equalsIgnoreCase(fielditem.getItemid())|| "A0000".equalsIgnoreCase(fielditem.getItemid()) )) {
					continue;
				}
				if (fielditem != null
						&& "N".equalsIgnoreCase(fielditem.getItemtype())
						&& !"A00Z1"
								.equalsIgnoreCase((String) fielditem.getItemid())) {
					colBuf.append(" ,sum(" + (String) fielditem.getItemid()
							+ ") as " + (String) fielditem.getItemid());
				}
				else {
					/*将备注类型的指标转换成字符类型  xiegh 20170519 bug27901 end*/
					if (DataDictionary.getFieldItem(fielditem.getItemid()) != null
							&& "M".equalsIgnoreCase(DataDictionary.getFieldItem(fielditem.getItemid()).getItemtype())) {
						if (Sql_switcher.searchDbServer() == Constant.MSSQL) {// ms sql
							colBuf.append(" ,max( convert(nvarchar(40)," + (String) fielditem.getItemid() + ")) as "
									+ (String) fielditem.getItemid());
						} else if (Sql_switcher.searchDbServer() == Constant.ORACEL) {// oracle
							colBuf.append(" ,max( to_char(" + (String) fielditem.getItemid() + ")) as "
									+ (String) fielditem.getItemid());
						}
					}else{
						colBuf.append(" ,max(" + (String) fielditem.getItemid()
								+ ") as " + (String) fielditem.getItemid());
					}
				}
				String format = (String) map.get(((String) fielditem
						.getItemid()).toUpperCase());
				if(format == null)
					format = "";
				if (format.indexOf("#2") != -1) {
					if (fielditem != null
							&& "A".equalsIgnoreCase(fielditem.getItemtype())
							&& !"0".equals(fielditem.getCodesetid())) {
						String codeName = "codeitem";
						if ("UN".equalsIgnoreCase(fielditem.getCodesetid())
								|| "UM".equalsIgnoreCase(
                                fielditem.getCodesetid())
								|| "@K".equalsIgnoreCase(
                                fielditem.getCodesetid())) {
							codeName = "organization";
						}
						colBuf.append(",max(" + fielditem.getItemid()
								+ "_code.corcode) as " + fielditem.getItemid()
								+ "_corcode ");
						codebuf
								.append(" left join (select corcode,codeitemid from ");
						codebuf.append(codeName + " where UPPER(codesetid)='"
								+ fielditem.getCodesetid().toUpperCase()
								+ "') ");
						codebuf.append(fielditem.getItemid() + "_code on "
								+ this.gz_tablename + "."
								+ fielditem.getItemid() + "="
								+ fielditem.getItemid() + "_code.codeitemid ");
					}
				}

			}
			sql.append("select dbid,max(A0000) as A0000,A0100 as aid");//增加 dbid max(A0000)用于排序2016-06-13
			sql.append(colBuf.toString());
			sql.append(" from ");
			sql.append(tableName);
			if (codebuf.length() > 0)
				sql.append(codebuf.toString());
			sql.append("  where 1=1");
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.conn,this.salaryid,this.userview);
			String manager = gzbo.getManager();
			if("salaryhistory".equals(tableName)) {//是历史表时，lis 20016-01-27
				sql.append(" and salaryid="+this.salaryid);
				sql.append(" and A00Z3=");
				sql.append(this.getAppCount());
				sql.append(" and A00Z2=");
				sql.append(Sql_switcher.dateValue(this.getAppdate()));
				sql.append(" and (( curr_user='").append(this.userview.getUserId()).append("' ) or ( ( (AppUser is null  ")
						.append(gzbo.getWhlByUnits("salaryhistory", true)).append("  )  or AppUser Like '%;")
						.append(this.userview.getUserName()).append(";%' ) ) ) ");
			}else if(StringUtils.isNotBlank(manager) && !this.userview.getUserName().equalsIgnoreCase(manager)){ // 共享非管理员
				sql.append(gzbo.getWhlByUnits(tableName, true));
			}
			if(condSql!=null&&condSql.length()>0){
				sql.append(" and ");
				sql.append(condSql);
			}
			sql.append(" group by NBASE,A0100,A00Z0,dbid ");
			sql.append(" order by dbid,A0000, A00Z0");//增加order by，增加group by dbid zhanghua 2016-06-13
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return sql.toString();

	}
	/**
	 * 获取临时表数据
	 * @param sql 
	 * @param columns
	 * @param columnsInfo
	 * @return
	 */
	 public ArrayList getFilterResult(String sql, ArrayList columns,
				ArrayList columnsInfo)
	    {
	    	ArrayList list = new ArrayList();
			try
			{ 
				if(columns==null||columns.size()==0)
					return list;
				boolean flag=true;
				boolean a0000flag=true;
				boolean a00z1=true;
				for(int j=0;j<columns.size();j++)
				{
					if("a0100".equalsIgnoreCase(((FieldItem)columns.get(j)).getItemid()))
						flag=false;
					if("a0000".equalsIgnoreCase(((FieldItem)columns.get(j)).getItemid()))
						a0000flag=false;
					if("a00z1".equalsIgnoreCase(((FieldItem)columns.get(j)).getItemid()))
						a00z1=false;
					
				}
				
				ContentDAO dao = new ContentDAO(this.conn);
				RowSet rs= null;
				rs=dao.search(sql.toString());
				while(rs.next())
				{
					LazyDynaBean bean = new LazyDynaBean();
					for(int i=0;i<columnsInfo.size();i++)
					{
						ColumnsInfo Info=(ColumnsInfo)columnsInfo.get(i);
						if("aid".equalsIgnoreCase(Info.getColumnId()))
							continue;
						if(flag&&("a0100".equalsIgnoreCase(Info.getColumnId())))
							continue;
						if(a0000flag&&("a0000".equalsIgnoreCase(Info.getColumnId())))
							continue;
						if(a00z1&&("A00Z1".equalsIgnoreCase(Info.getColumnId())))
							continue;
						if("a0000".equalsIgnoreCase(Info.getColumnId()))
						{
							String value=rs.getString(Info.getColumnId());
							bean.set(Info.getColumnId(),PubFunc.round(value,0));
						}
						else
			        	 	bean.set(Info.getColumnId(),rs.getString(Info.getColumnId())==null?"":rs.getString(Info.getColumnId()));
						if("N".equalsIgnoreCase(Info.getColumnId()))
						{
							bean.set("itemtype","N");
						}
						else
						{
							bean.set("itemtype","A");
						}
					}
					list.add(bean);
					
				}
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return list;
	    
	    } 
	/**
	 * 拼接format数据的方法
	 * @param sql
	 * @param columns 列头
	 * @param columnsInfo 列头信息
	 * @param model =0薪资发放 =1薪资审批
	 * @param bank_id
	 * @return
	 */
	public ArrayList getPersonInfoList(String sql, ArrayList columns,
			ArrayList columnsInfo, String model, String bank_id) {
		ArrayList list = new ArrayList();
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(sql.toString());
			int x = 0;
			HashMap map = this.getFormatMap(columns, bank_id);
			HashMap lengthMap = this.getDefault_length(bank_id);
			while (rs.next()) {
				x++;
				LazyDynaBean bean = new LazyDynaBean();
				for (int i = 0; i < columns.size(); i++) {
					FieldItem fielditem = (FieldItem) columns.get(i);
					int itemlength = fielditem.getItemlength();
					String itemid = fielditem.getItemid();
					if("aid".equalsIgnoreCase(itemid)){
						bean.set((String) fielditem.getItemid().toLowerCase(), rs.getString((String) fielditem.getItemid().toLowerCase()) == null ? "" : rs.getString((String) fielditem.getItemid().toLowerCase()));
						continue;
					}
					if("A0000".equalsIgnoreCase(itemid)){
						bean.set((String) fielditem.getItemid().toLowerCase(), rs.getString((String) fielditem.getItemid().toLowerCase()) == null ? "" : rs.getString((String) fielditem.getItemid().toLowerCase()));
						continue;
					}
					if("a00z1".equalsIgnoreCase(itemid)|| "a00z3".equalsIgnoreCase(itemid)){//若为 归属次数 发放次数
						if (map.get((String) fielditem.getItemid()) != null
								&& map.get((String) fielditem.getItemid()).toString().length() > 0) {//判断是否设置了格式
							String temp = this.getNumberFormat(rs.getString((String) fielditem.getItemid().toLowerCase()),
									(String) map.get((String) fielditem.getItemid()),itemlength);
							bean.set((String) fielditem.getItemid().toLowerCase(), temp);
						} else {
							String temp = "";
							if (rs.getString((String) fielditem.getItemid()) != null) {
								if (String.valueOf(fielditem.getDecimalwidth()) != null
										&& "0".equals(String.valueOf(fielditem.getDecimalwidth()))) {
									temp = this.getNumberFormat(rs.getString((String) fielditem.getItemid()), "0",itemlength);
								} else {
									temp = this.getNumberFormat(rs.getString((String) fielditem.getItemid()), "0.00",itemlength);
								}
							}
							bean.set((String) fielditem.getItemid().toLowerCase(), temp);
						}
						continue;
					}
					if("a00z0".equalsIgnoreCase(itemid)|| "a00z2".equalsIgnoreCase(itemid)){//若为归属日期 发放日期
						String temp = "";
						
						if (map.get(((String) fielditem.getItemid()).toUpperCase()) != null
								&& !"".equals((String) map.get(((String) fielditem.getItemid()).toUpperCase()))) {
							if (Sql_switcher.searchDbServer() == Constant.ORACEL) {
								bean.set((String) fielditem.getItemid().toLowerCase(),
												getDateFormat((rs.getDate((String) fielditem.getItemid()) == null ? "": rs.getDate((String) fielditem.getItemid()).toString()),
														(String) map.get(((String) fielditem.getItemid()).toUpperCase())));
							} else {
								bean.set((String) fielditem.getItemid().toLowerCase(),getDateFormat(
														rs.getString((String) fielditem.getItemid()) == null ? "": rs.getString((String) fielditem.getItemid()),
														(String) map.get(((String) fielditem.getItemid()).toUpperCase())));
							}
						}else{
							
							if (Sql_switcher.searchDbServer() == Constant.ORACEL) {
								temp = rs.getTimestamp((String) fielditem.getItemid().toLowerCase()).toString();
							} else {
								temp = rs.getString((String) fielditem.getItemid().toLowerCase());
							}
							
							if (temp != null && !"".equals(temp)) {
								if (temp.length() > 10) {
									temp = temp.substring(0, 10);
								}
							} else {
								temp = "";
							}
							bean.set((String) fielditem.getItemid().toLowerCase(), temp);
						}
						continue;
					}
				 if (("A01Z0".equalsIgnoreCase(itemid)|| "A".equalsIgnoreCase(DataDictionary.getFieldItem(itemid).getItemtype()))
							&& !"0".equalsIgnoreCase((String) fielditem.getCodesetid())) {//若为代码类 或是“停发标识”
						String format = (String) map.get((String) fielditem.getItemid().toUpperCase());
						String value = "";
						if (format == null || "#0".equals(format)|| "#".equals(format) || "".equals(format)) {
							value=AdminCode.getCodeName((String)fielditem.getCodesetid(),rs.getString(itemid.toUpperCase()));
							String codesetid=(String)fielditem.getCodesetid();
							itemid = fielditem.getItemid();
							if("UN".equalsIgnoreCase(codesetid)&&(value==null|| "".equals(value)))
							{
								value=AdminCode.getCodeName("UN",""+rs.getString(itemid.toUpperCase())+"");
							}
							if("UM".equalsIgnoreCase(codesetid)&&(value==null|| "".equals(value)))
							{
								value=AdminCode.getCodeName("UM",rs.getString(itemid));
							}
							bean.set(fielditem.getItemid().toLowerCase(), value);
						} else {
							if ("#1".equals(format)) {
								value = rs.getString((String) fielditem.getItemid());
								if (value == null)
									value = "";
								bean.set((String) fielditem.getItemid(), value);
							} else if ("#2".equals(format)) {
								value = rs.getString(((String) fielditem.getItemid())+ "_corcode");
								if (value == null)
									value = "";
								bean.set((String) fielditem.getItemid().toLowerCase(), value);
							} else {
								boolean hasValue = true;
								if (format.indexOf("#1") != -1) {
									value = rs.getString((String) fielditem.getItemid());
									if (value == null)
										value = "";
									format = format.replaceAll("#1", "");
									hasValue = false;
								}
								if (format.indexOf("#2") != -1) {
									value = rs.getString(((String) fielditem.getItemid())+ "_corcode");
									if (value == null)
										value = "";
									format = format.replaceAll("#2", "");
									hasValue = false;
								}
								if (hasValue) {
									String codesetid=(String)fielditem.getCodesetid();
									itemid = fielditem.getItemid();
									 value=AdminCode.getCodeName(codesetid,rs.getString(itemid));
									if (value == null)
										value = "";
								}
								String hh = (String) (lengthMap.get(((String) (fielditem.getItemid().toUpperCase() == null ? "0"
										: fielditem.getItemid().toUpperCase()))) == null ? "0": lengthMap.get(((String) (fielditem.getItemid().toUpperCase() == null ? "0"
										: fielditem.getItemid())).toUpperCase()));
								value=this.getCharacterFormat(value, format, Integer.parseInt(hh));//取代码的值进行格式化，并将值保存，不储存代码
								bean.set((String) fielditem.getItemid().toLowerCase(), value);
							}
						}
					}
					else if ("N"
							.equalsIgnoreCase(DataDictionary.getFieldItem(itemid).getItemtype())) {//针对数值型 若为null 则设置为0 zhanghua 2017-6-26
						if (map.get((String) fielditem.getItemid()) != null
								&& map.get((String) fielditem.getItemid()).toString().length() > 0) {//判断是否设置了格式
							String temp = this.getNumberFormat(rs.getString((String) fielditem.getItemid().toLowerCase()),
									(String) map.get((String) fielditem.getItemid()),itemlength);
							bean.set((String) fielditem.getItemid().toLowerCase(), temp);
						} else {
							String temp = "";
							if (rs.getString((String) fielditem.getItemid()) != null) {
								temp=rs.getString((String) fielditem.getItemid());
							}else
								temp="0";
							if (String.valueOf(fielditem.getDecimalwidth()) != null
									&& "0".equals(String.valueOf(fielditem.getDecimalwidth()))) {
								temp = this.getNumberFormat(temp, "0",itemlength);
							} else {
								temp = this.getNumberFormat(temp, "0.00",itemlength);
							}
							bean.set((String) fielditem.getItemid().toLowerCase(), temp);
						}
					} else if ("D"
							.equalsIgnoreCase(DataDictionary.getFieldItem(itemid).getItemtype())) {
						if (map.get(((String) fielditem.getItemid()).toUpperCase()) != null
								&& !"".equals((String) map.get(((String) fielditem.getItemid()).toUpperCase()))) {
							if (Sql_switcher.searchDbServer() == Constant.ORACEL) {
								bean.set((String) fielditem.getItemid().toLowerCase(),
												getDateFormat((rs.getDate((String) fielditem.getItemid()) == null ? "": rs.getDate((String) fielditem.getItemid()).toString()),
														(String) map.get(((String) fielditem.getItemid()).toUpperCase())));
							} else {
								bean.set((String) fielditem.getItemid().toLowerCase(),getDateFormat(
														rs.getString((String) fielditem.getItemid()) == null ? "": rs.getString((String) fielditem.getItemid()),
														(String) map.get(((String) fielditem.getItemid()).toUpperCase())));
							}
						} else {
							String temp = "";
							if (Sql_switcher.searchDbServer() == Constant.ORACEL) {
								temp = rs.getTimestamp((String) fielditem.getItemid().toLowerCase())!=null?rs.getTimestamp((String) fielditem.getItemid().toLowerCase()).toString():"";
							} else {
								temp = rs.getString((String) fielditem.getItemid().toLowerCase());
							}
							if (temp != null && !"".equals(temp)) {
								if (temp.length() > 10) {
									temp = temp.substring(0, 10);
								}
							} else {
								temp = "";
							}
							bean.set((String) fielditem.getItemid().toLowerCase(), temp);
						}
					} else if ("A".equalsIgnoreCase(DataDictionary.getFieldItem(itemid).getItemtype())&& "0".equalsIgnoreCase((String) fielditem.getCodesetid())) {//是否为字符类
						//if (StringUtils.isNotBlank((String) map.get(fielditem.getItemid().toUpperCase()))) {//是否具有格式
							String hh = (String) (lengthMap.get(((String) (fielditem.getItemid().toUpperCase() == null ? "0"
											: fielditem.getItemid().toUpperCase()))) == null ? "0": lengthMap.get(((String) (fielditem.getItemid().toUpperCase() == null ? "0"
											: fielditem.getItemid())).toUpperCase()));
							bean.set((String) fielditem.getItemid().toLowerCase(),getCharacterFormat(rs
											.getString((String) fielditem.getItemid()) == null ? ""
											: rs.getString((String) fielditem.getItemid()),
											(String) map.get(((String) fielditem.getItemid()).toUpperCase()),
											Integer.parseInt(hh)));
//						} else {
//							bean.set((String) fielditem.getItemid().toLowerCase(),rs.getString((String) fielditem.getItemid()) == null ? ""
//											: rs.getString((String) fielditem.getItemid().toLowerCase()));
//						}
					} else {
						bean.set((String) fielditem.getItemid().toLowerCase(), rs.getString((String) fielditem.getItemid().toLowerCase()) == null ? "" : rs.getString((String) fielditem.getItemid().toLowerCase()));
					}
				}
				list.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 取得默认格式的map
	 * @param bank_id
	 * @return
	 */
	private HashMap getDefault_length(String bank_id) {
		HashMap map = new HashMap();
		try {
			String sql = "select field_name,field_default from gz_bank_item where bank_id="
					+ bank_id;
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(sql);
			while (rs.next()) {
				map.put(rs.getString("field_name").toUpperCase(), rs
						.getString("field_default"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * get character format
	 * 
	 * @param value
	 * @param format
	 * @return
	 */
	private String getCharacterFormat(String value, String format_str,
			int length) {
		String return_str = "";
		try {
			if (value == null || "".equals(value)) {
				return return_str;
			}
			if (format_str == null || "".equals(format_str)) {
				if(value.length()>length){
					value=value.substring(0,length);
				}
				return value;
			}
			String format = "";
			String str = "";
			if (format_str.indexOf("^") != -1) {
				int index = format_str.indexOf("^");
				int indexT = format_str.lastIndexOf("^");
				// 从左取
				if (index == indexT) {
					String constant = format_str.substring(0, index);
					String zero = format_str.substring(index + 1);
					if ("".equals(zero))
						return_str = constant + value;
					else if (value.getBytes().length <= zero.length())
						return_str = constant + value;
					else
						return_str = constant
								+ value.substring(0, zero.length());
				} else {
					String constant = format_str.substring(0, index);
					String zero = format_str.substring(indexT + 1);
					if ("".equals(zero))
						return_str = constant + value;
					else if (value.getBytes().length <= zero.length())
						return_str = constant + value;
					else
						return_str = constant
								+ value.substring(value.length()
										- zero.length());
				}

			} else if (format_str.length() >= 2) {
				format = format_str.substring(0, 1);
				str = format_str.substring(1);
				if ("@".equals(format)) {
					if (value.length() < length) {
						int tmp = length - value.getBytes().length;
						int format_str_length = str.getBytes().length;
						if (tmp < format_str_length) {
							/** 是补全还是截断？ */
							return_str = str.substring(0, tmp) + value;
						} else if (tmp == format_str_length) {
							return_str = str + value;
						} else {
							int n = tmp / format_str_length;
							String temp = "";
							for (int j = 0; j < n; j++) {
								temp += str;
							}
							/** 是补全还是截断？ */
							int vv = tmp - format_str_length * n;
							temp += str.substring(0, (tmp - format_str_length
									* n));
							return_str = temp + value;

						}
					} else {
						return_str = value;
					}
				} else if ("&".equals(format)) {
					if (value.length() < length) {
						int tmp = length - value.getBytes().length;
						String temp = "";
						for (int j = 0; j < tmp; j++) {
							temp += str;
						}
						return_str = value + temp;
					} else {
						return_str = value;
					}
				} else {
					return_str = value;
				}

			} else {
				if ("&".equals(format_str)) {
					int tmp = length - value.getBytes().length;
					String temp = " ";
					for (int j = 0; j < tmp; j++) {
						str += temp;
					}
					return_str = value + str;
				} else if ("@".equals(format_str)) {
					int tmp = length - value.getBytes().length;
					String temp = " ";
					for (int j = 0; j < tmp; j++) {
						str += temp;
					}
					return_str = str + value;
				} else if ("<".equals(format_str)) {
					return_str = value.toLowerCase();
				} else if (">".equals(format_str)) {
					return_str = value.toUpperCase();
				} else {
					return_str = value;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return return_str;
	}

	/**
	 * get Date format
	 * 
	 * @param value
	 * @param format_str
	 * @return
	 */
	private String getDateFormat(String value, String format_str) {
		String return_str = "";
		try {
			if (value == null || "".equals(value))
				return return_str;
			String temp = "";
			if (value.length() > 10)
				temp = value.substring(0, 10);
			else
				temp = value;
			String oper = "";
			if (value.indexOf("-") != -1)
				oper = "-";
			else
				oper = ".";
			String[] arr = temp.split(oper);
			String year = "";
			String month = "";
			String day = "";
			int Cyear = Calendar.getInstance().get(Calendar.YEAR);
			if (arr.length >= 1)
				year = arr[0];
			if (arr.length >= 2)
				month = arr[1];
			if (arr.length >= 3)
				day = arr[2];

			if ("MM".equalsIgnoreCase(format_str)) {
				String temp_month = "";
				if (month.length() < 2 && Integer.parseInt(month) < 10) {
					temp_month = "0" + month;
				} else {
					temp_month = month;
				}
				return_str = temp_month;
			} else if ("MM月".equalsIgnoreCase(format_str)) {
				String temp_month = "";
				if (month.length() < 2 && Integer.parseInt(month) < 10) {
					temp_month = "0" + month;
				} else {
					temp_month = month;
				}
				temp_month += "月";
				return_str = temp_month;
			} else if ("YYYY".equalsIgnoreCase(format_str)) {
				if (year.length() <= 2) {
					String s = String.valueOf(Cyear);
					String t_1 = s.substring(0, 2);
					String t_2 = s.substring(2);
					if (Integer.parseInt(year) > Integer.parseInt(t_2)) {
						return_str = String.valueOf(Integer.parseInt(t_1) - 1)
								+ year;
					} else {
						return_str = t_1 + year;
					}

				} else
					return_str = year;
			} else if ("yyyy.mm".equalsIgnoreCase(format_str)
					|| "yyyy-mm".equalsIgnoreCase(format_str)) {
				String temp_year = "";
				String temp_month = "";
				if (year.length() <= 2) {
					String s = String.valueOf(Cyear);
					String t_1 = s.substring(0, 2);
					String t_2 = s.substring(2);

					if (Integer.parseInt(year) > Integer.parseInt(t_2)) {
						temp_year = String.valueOf(Integer.parseInt(t_1) - 1)
								+ year;
					} else {
						temp_year = t_1 + year;
					}
				} else {
					temp_year = year;
				}
				if (month.length() < 2 && Integer.parseInt(month) < 10) {
					temp_month = "0" + month;
				} else {
					temp_month = month;
				}
				if ("yyyy.mm".equalsIgnoreCase(format_str))
					return_str = temp_year + "." + temp_month;
				else
					return_str = temp_year + "-" + temp_month;
			} else if ("yy.mm.dd".equalsIgnoreCase(format_str)
					|| "yy-mm-dd".equalsIgnoreCase(format_str)) {
				String temp_year = "";
				String temp_month = "";
				String temp_day = "";
				if (year.length() <= 2)
					temp_year = year;
				else
					temp_year = year.substring(2);
				if (month.length() <= 1 && Integer.parseInt(month) < 10)
					temp_month = "0" + month;
				else
					temp_month = month;
				if (day.length() <= 1 && Integer.parseInt(day) < 10)
					temp_day = "0" + day;
				else
					temp_day = day;
				if ("yy.mm.dd".equalsIgnoreCase(format_str))

					return_str = temp_year + "." + temp_month + "." + temp_day;
				else
					return_str = temp_year + "-" + temp_month + "-" + temp_day;
			} else if ("yyyy-mm-dd".equalsIgnoreCase(format_str)) {
				String temp_year = "";
				String temp_month = "";
				String temp_day = "";
				if (year.length() <= 2) {
					String s = String.valueOf(Cyear);
					String t_1 = s.substring(0, 2);
					String t_2 = s.substring(2);

					if (Integer.parseInt(year) > Integer.parseInt(t_2)) {
						temp_year = String.valueOf(Integer.parseInt(t_1) - 1)
								+ year;
					} else {
						temp_year = t_1 + year;
					}
				} else {
					temp_year = year;
				}
				if (month.length() < 2 && Integer.parseInt(month) < 10) {
					temp_month = "0" + month;
				} else {
					temp_month = month;
				}
				if (day.length() <= 1 && Integer.parseInt(day) < 10)
					temp_day = "0" + day;
				else
					temp_day = day;
				return_str = temp_year + "-" + temp_month + "-" + temp_day;
			} else if ("yymm".equalsIgnoreCase(format_str)) {
				String temp_year = "";
				String temp_month = "";
				if (year.length() <= 2)
					temp_year = year;
				else
					temp_year = year.substring(2);
				if (month.length() <= 1 && Integer.parseInt(month) < 10)
					temp_month = "0" + month;
				else
					temp_month = month;
				return_str = temp_year + temp_month;
			} else if ("yyyymmdd".equalsIgnoreCase(format_str)) {
				String temp_year = "";
				String temp_month = "";
				String temp_day = "";
				if (year.length() <= 2) {
					String s = String.valueOf(Cyear);
					String t_1 = s.substring(0, 2);
					String t_2 = s.substring(2);

					if (Integer.parseInt(year) > Integer.parseInt(t_2)) {
						temp_year = String.valueOf(Integer.parseInt(t_1) - 1)
								+ year;
					} else {
						temp_year = t_1 + year;
					}
				} else {
					temp_year = year;
				}
				if (month.length() < 2 && Integer.parseInt(month) < 10) {
					temp_month = "0" + month;
				} else {
					temp_month = month;
				}
				if (day.length() <= 1 && Integer.parseInt(day) < 10)
					temp_day = "0" + day;
				else
					temp_day = day;
				return_str = temp_year + temp_month + temp_day;
			} else if ("yymmdd".equalsIgnoreCase(format_str)) {
				String temp_year = "";
				String temp_month = "";
				String temp_day = "";
				if (year.length() <= 2)
					temp_year = year;
				else
					temp_year = year.substring(2);
				if (month.length() < 2 && Integer.parseInt(month) < 10) {
					temp_month = "0" + month;
				} else {
					temp_month = month;
				}
				if (day.length() <= 1 && Integer.parseInt(day) < 10)
					temp_day = "0" + day;
				else
					temp_day = day;
				return_str = temp_year + temp_month + temp_day;
			} else if ("yy年mm月".equalsIgnoreCase(format_str)) {
				String temp_year = "";
				String temp_month = "";
				if (year.length() <= 2)
					temp_year = year;
				else
					temp_year = year.substring(2);
				if (month.length() <= 1 && Integer.parseInt(month) < 10)
					temp_month = "0" + month;
				else
					temp_month = month;
				return_str = temp_year + "年" + temp_month + "月";
			} else if (format_str.toLowerCase().indexOf("dd") != -1
					|| format_str.toLowerCase().indexOf("yy") != -1
					|| format_str.toLowerCase().indexOf("mm") != -1) {
				// 支持格式 yyyy|（aa）mm
				String temp_year = "";
				String temp_year2 = "";
				String temp_month = "";
				String temp_day = "";
				if (year.length() <= 2) {
					String s = String.valueOf(Cyear);
					String t_1 = s.substring(0, 2);
					String t_2 = s.substring(2);

					if (Integer.parseInt(year) > Integer.parseInt(t_2)) {
						temp_year = String.valueOf(Integer.parseInt(t_1) - 1)
								+ year;
					} else {
						temp_year = t_1 + year;
					}
				} else {
					temp_year = year;
				}
				if (year.length() <= 2)
					temp_year2 = year;
				else
					temp_year2 = year.substring(2);
				if (month.length() < 2 && Integer.parseInt(month) < 10) {
					temp_month = "0" + month;
				} else {
					temp_month = month;
				}
				if (day.length() <= 1 && Integer.parseInt(day) < 10)
					temp_day = "0" + day;
				else
					temp_day = day;

				if (format_str.indexOf("yyyy") != -1) {
					format_str = format_str.replaceAll("yyyy", temp_year);

				}
				if (format_str.indexOf("YYYY") != -1) {
					format_str = format_str.replaceAll("YYYY", temp_year);

				}
				if (format_str.indexOf("yy") != -1) {
					format_str = format_str.replaceAll("yy", temp_year2);

				}
				if (format_str.indexOf("YY") != -1) {
					format_str = format_str.replaceAll("YY", temp_year2);

				}

				if (format_str.indexOf("mm") != -1) {
					format_str = format_str.replaceAll("mm", temp_month);

				}
				if (format_str.indexOf("MM") != -1) {
					format_str = format_str.replaceAll("MM", temp_month);

				}

				if (format_str.indexOf("dd") != -1) {
					format_str = format_str.replaceAll("dd", temp_day);

				}
				if (format_str.indexOf("DD") != -1) {
					format_str = format_str.replaceAll("DD", temp_day);

				}
				return_str = format_str;
			} else {
				String temp_year = "";
				String temp_month = "";
				String temp_day = "";
				if (year.length() <= 2) {
					String s = String.valueOf(Cyear);
					String t_1 = s.substring(0, 2);
					String t_2 = s.substring(2);

					if (Integer.parseInt(year) > Integer.parseInt(t_2)) {
						temp_year = String.valueOf(Integer.parseInt(t_1) - 1)
								+ year;
					} else {
						temp_year = t_1 + year;
					}
				} else {
					temp_year = year;
				}
				if (month.length() < 2 && Integer.parseInt(month) < 10) {
					temp_month = "0" + month;
				} else {
					temp_month = month;
				}
				if (day.length() <= 1 && Integer.parseInt(day) < 10)
					temp_day = "0" + day;
				else
					temp_day = day;
				return_str = temp_year + oper + temp_month + oper + temp_day;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return return_str;

	}

	/**
	 * 取得薪资类别的所有指标
	 * 
	 * @param salaryid
	 * @return
	 * @throws GeneralException
	 */
	private HashMap getSalarySetFields() throws GeneralException {
		HashMap map = new HashMap();
		try {
			String sql = "select itemid from salaryset where salaryid="
					+ this.salaryid;
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(sql);
			while (rs.next()) {
				map.put(rs.getString("itemid").toUpperCase(), rs
						.getString("itemid"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return map;
	}

	/**
	 * 代发银行要求的数据的列的信息(列名,类型,长度等)
	 * 
	 * @param bank_id
	 * @param salaryid
	 * @param type
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getBankItemInfo(String bank_id, int type)
			throws GeneralException {
		ArrayList list = new ArrayList();
		try {
			StringBuffer sql = new StringBuffer();
			if (type == 1) {
				sql
						.append("select g.*,s.decwidth from gz_bank_item g left join (select distinct itemid,decwidth from salaryset ) s on g.field_name=s.itemid where g.bank_id=");
				sql.append(bank_id);
				sql.append("  order by g.norder");

			} else if (type == 3) {
				sql
						.append("select g.*,s.decwidth from gz_bank_item g left join (select distinct itemid,decwidth from salaryset ) s on g.field_name=s.itemid where g.bank_id=");
				sql.append("10000000");
				sql.append("  order by g.norder");
			} else {
				sql
						.append(" select g.item_id,g.bank_id,g.item_type,g.field_name,g.field_default,g.format,s.itemdesc, g.item_name,s.decwidth ,g.norder from");
				sql.append(" gz_bank_item g,salaryset s where ");
				sql.append(" g.bank_id=");
				sql.append(bank_id);
				sql.append(" and g.field_name=s.itemid and s.salaryid=");
				sql.append(salaryid + " order by g.norder");
			}
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(sql.toString());
			// 根据工资类别id得到类别下面的所有项目列表
			HashMap salarySetMap = this.getSalarySetFields();
			HashMap map = new HashMap();
			while (rs.next()) {
				if (salarySetMap.get(rs.getString("field_name").toUpperCase()) != null) {
					LazyDynaBean bean = new LazyDynaBean();
					bean.set("item_id", rs.getString("item_id"));
					bean.set("bank_id", rs.getString("bank_id"));
					bean.set("norder", rs.getString("norder"));
					// gz.bankdisk.sequencenumber 序号
					bean.set("itemdesc", "a0000"
							.equalsIgnoreCase(rs.getString("field_name")) ? ResourceFactory
							.getProperty("gz.bankdisk.sequencenumber") : rs
							.getString("item_name"));
					if (rs.getInt("item_type") == 0) {
						bean.set("itemtype", ResourceFactory
								.getProperty("system.item.ntype"));
						bean.set("item_type", "N");
					}
					if (rs.getInt("item_type") == 1) {
						bean.set("itemtype", ResourceFactory
								.getProperty("system.item.dtype"));// 日期型
						bean.set("item_type", "D");
					}
					if (rs.getInt("item_type") == 2) {
						bean.set("itemtype", ResourceFactory
								.getProperty("system.item.ctype"));// 字符型
						bean.set("item_type", "A");
					}
					bean.set("decwidth", (rs.getString("decwidth") == null
							|| "".equals(rs.getString("decwidth")) ? "0" : rs
							.getString("decwidth")));
					bean
							.set("itemid", rs.getString("field_name")
									.toUpperCase());// column name

					if (map.get(rs.getString("field_name").toLowerCase()) != null) {
						continue;
					}

					map.put(rs.getString("field_name").toLowerCase(), "1");

					bean.set("itemlength", rs.getString("field_default"));
					bean.set("format", rs.getString("format") == null ? "" : rs
							.getString("format"));
					list.add(bean);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);

		}
		return list;
	}

	/**
	 * 根据工资类别id得到类别下面的所有项目列表
	 * 
	 * @param bank_id
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getAllBankItem(ArrayList selectList, String bank_id)
			throws GeneralException {
		ArrayList list = new ArrayList();
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;

			StringBuffer buf = new StringBuffer();
			StringBuffer selected_item_buf = new StringBuffer();
			for (int j = 0; j < selectList.size(); j++) {
				selected_item_buf.append("'");
				selected_item_buf.append(selectList.get(j));
				selected_item_buf.append("',");

				if (selectList.size() > 0) {
					buf.setLength(0);
					buf
							.append("select itemid,itemdesc ,itemlength,itemtype from salaryset where salaryid=");
					buf.append(salaryid);
					if (selected_item_buf != null
							&& selected_item_buf.toString().length() > 0) {
						buf.append(" and UPPER(itemid) =");
						buf.append("'" + selectList.get(j) + "'");
					}
					rs = dao.search(buf.toString());
					while (rs.next()) {

						LazyDynaBean bean = new LazyDynaBean();
						if ("a0000".equalsIgnoreCase(rs.getString("itemid"))) {
							continue;
						} else {
							if ("0".equalsIgnoreCase(
                                    this.userview.analyseFieldPriv(
                                            rs.getString("itemid")))) {
								continue;
							}
							bean.set("itemid", rs.getString("itemid"));
							bean.set("itemdesc", rs.getString("itemdesc"));
							String type = rs.getString("itemtype");
							String typeH = ResourceFactory
									.getProperty("system.item.ntype");
							if ("N".equals(type)) {
								typeH = ResourceFactory
										.getProperty("system.item.ntype");// 数值型
							} else if ("D".equals(type)) {
								typeH = ResourceFactory
										.getProperty("system.item.dtype");// 日期型
							} else {
								typeH = ResourceFactory
										.getProperty("system.item.ctype");// 字符型
							}
							bean.set("item_type", type);
							bean.set("itemtype", typeH);
							bean.set("itemlength", rs.getString("itemlength"));
							bean.set("format", "");
						}
						bean.set("isSelect", "1");
						list.add(bean);
					}
				}

			}

			buf.setLength(0);
			buf
					.append("select itemid,itemdesc ,itemlength,itemtype from salaryset where salaryid=");
			buf.append(salaryid);
			buf.append(" and UPPER(itemid) not in(");
			if (selected_item_buf != null
					&& selected_item_buf.toString().length() > 0) {
				buf.append(selected_item_buf.toString().toUpperCase());
			}
			buf.append("'NBASE','A0100')");
			rs = dao.search(buf.toString());
			while (rs.next()) {
				LazyDynaBean bean = new LazyDynaBean();
				if ("a0000".equalsIgnoreCase(rs.getString("itemid"))) {
//					bean.set("itemid", rs.getString("itemid"));
//					bean.set("itemdesc", ResourceFactory
//							.getProperty("gz.bankdisk.sequencenumber"));// 序号
					continue;
				} else {
					if ("0"
							.equalsIgnoreCase(this.userview.analyseFieldPriv(rs.getString("itemid")))) {
						continue;
					}
					bean.set("itemid", rs.getString("itemid"));
					bean.set("itemdesc", rs.getString("itemdesc"));
					String type = rs.getString("itemtype");
					String typeH = ResourceFactory
							.getProperty("system.item.ntype");
					if ("N".equals(type)) {
						typeH = ResourceFactory
								.getProperty("system.item.ntype");// 数值型
					} else if ("D".equals(type)) {
						typeH = ResourceFactory
								.getProperty("system.item.dtype");// 日期型
					} else {
						typeH = ResourceFactory
								.getProperty("system.item.ctype");// 字符型
					}
					bean.set("item_type", type);
					bean.set("itemtype", typeH);
					bean.set("itemlength", rs.getString("itemlength"));
					bean.set("format", "");
				}
				bean.set("isSelect", "0");
				list.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;

	}

	/**
	 * 取得每一个银行模板项目的格式
	 * 
	 * @param ArrayList
	 *            columns
	 * @param String
	 *            bank_id
	 * @return HashMap
	 * @throws GeneralException
	 */
	public HashMap getFormatMap(ArrayList columns, String bank_id)
			throws GeneralException {
		HashMap map = new HashMap();
		if (columns.size() == 0) {
			return map;
		}
		try {
			StringBuffer sql = new StringBuffer();
			StringBuffer columnBuf = new StringBuffer();
			FieldItem info = new FieldItem();
			for (int i = 0; i < columns.size(); i++) {
				info = (FieldItem) columns.get(i);
				columnBuf.append(",'");
				columnBuf.append(info.getItemid());
				columnBuf.append("'");
			}
			sql
					.append("select field_name,format from gz_bank_item where bank_id=");
			sql.append(bank_id);
			sql.append(" and field_name in (");
			sql.append(columnBuf.toString().substring(1));
			sql.append(")");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(sql.toString());
			while (rs.next()) {
				map.put(rs.getString("field_name").toUpperCase(), rs
						.getString("format") == null ? "" : rs
						.getString("format"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return map;
	}

	/**
	 * 取得首末行标志和首末行输出串
	 * 
	 * @param bankcheck
	 * @param String
	 *            bank_id
	 * @return HashMap
	 * @throws GeneralException
	 */
	public HashMap getCheckAndFormat(String bank_id) throws GeneralException {
		HashMap map = new HashMap();
		try {
			StringBuffer sql = new StringBuffer(
					"select bankcheck,bankformat,bank_name,scope from gz_bank where bank_id='"
							+ bank_id + "'");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(sql.toString());
			while (rs.next()) {
				// bankcheck 0无 1首行 2末行
				map.put("bankcheck", rs.getString("bankcheck"));
				String format = rs.getString("bankformat") == null ? "" : rs
						.getString("bankformat");
				String bank_name = rs.getString("bank_name") == null ? "" : rs
						.getString("bank_name");
				String scope = rs.getString("scope") == null ? "0" : rs
						.getString("scope");
				map.put("bankformat", format);
				map.put("bank_name", bank_name);
				map.put("scope", scope);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return map;

	}

	/**
	 * 删除代发银行编辑之前的数据内容
	 * 
	 * @param itemList
	 * @param bank_id
	 * @throws GeneralException
	 */
	public void deleteItem(ArrayList itemList, String bank_id)
			throws GeneralException {
		try {
			StringBuffer buf = new StringBuffer("");
			for (int i = 0; i < itemList.size(); i++) {
				MorphDynaBean map = (MorphDynaBean) itemList.get(i);
				String itemid = (String) map.get("itemid");
				buf.append(",");
				buf.append("'" + itemid + "'");
			}
			if (buf.toString().length() > 0) {
				String sql = "delete from gz_bank_item where  bank_id =?";
				ArrayList list = new ArrayList();
				list.add(bank_id);
				ContentDAO dao = new ContentDAO(this.conn);
				dao.delete(sql, list);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

	/**
	 * 新增银行内容数据时，取得最大的项目id
	 * 
	 * @return
	 * @throws GeneralException
	 */
	private int getItemid() throws GeneralException {
		int n = 0;
		try {
			String sql = "select MAX(item_id) as item_id from gz_bank_item ";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql);
			while (rs.next()) {
				n = rs.getInt("item_id");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return (n + 1);
	}

	/**
	 * 保存为银行模板选择的项目
	 * 
	 * @param Arraylist
	 *            selectedFieldList
	 * @param String
	 *            bank_id
	 * @throws GeneralException
	 */
	public void saveTemplateItem(ArrayList selectedFieldList, String bank_id)
			throws GeneralException {
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sql = new StringBuffer();
			int item_id = this.getItemid();
			for (int i = 0; i < selectedFieldList.size(); i++) {
				MorphDynaBean bean = (MorphDynaBean) selectedFieldList.get(i);
				sql.append("insert into gz_bank_item ");
				sql.append("(bank_id,item_name,item_type,field_name,field_default,format");
				if (Sql_switcher.searchDbServer() != Constant.MSSQL) {
					sql.append(",item_id");
				}
				sql.append(",norder)");
				sql.append(" values (");
				sql.append(bank_id + ",'");
				sql.append((String) bean.get("itemdesc"));
				sql.append("','");
				if ("N".equalsIgnoreCase((String) bean.get("item_type")))
					sql.append("0','");
				if ("A".equalsIgnoreCase((String) bean.get("item_type")))
					sql.append("2','");
				if ("D".equalsIgnoreCase((String) bean.get("item_type")))
					sql.append("1','");
				sql.append((String) bean.get("itemid"));
				sql.append("','");
				sql.append((String) bean.get("itemlength"));
				sql.append("','");
				sql.append(PubFunc.keyWord_reback((String) bean.get("format")));
				sql.append("'");
				if (Sql_switcher.searchDbServer() != Constant.MSSQL) {
					sql.append("," + item_id);
				}
				sql.append("," + bean.get("norder") + ")");
				dao.insert(sql.toString(), new ArrayList());
				sql.setLength(0);
				item_id++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

	/**
	 * 取得最大的itemid
	 * 
	 * @return
	 * @throws GeneralException
	 */
	public String getMaxBank_id() throws GeneralException {
		String max_id = "";
		String sql = "select MAX(bank_id) from gz_bank ";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			rs = dao.search(sql);
			while (rs.next()) {
				max_id = rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return max_id;

	}

	/**
	 * 
	 * @Title: updateBankTemplate   
	 * @Description: 新增or修改银行报盘
	 * @param @param bankCheck
	 * @param @param bankFormat
	 * @param @param bankname
	 * @param @param scope
	 * @param @param bank_id
	 * @param @param type
	 * @param @throws GeneralException 
	 * @return void 
	 * @author:zhaoxg   
	 * @throws
	 */
	public void updateBankTemplate(String bankCheck, String bankFormat,String bankname, String scope, String bank_id, String type)throws GeneralException {
		try {
			ArrayList list = new ArrayList();
			StringBuffer sql = new StringBuffer();
			ContentDAO dao = new ContentDAO(this.conn);
			if ("1".equals(type)) {
				sql.append("update gz_bank set bankFormat=? ,bankCheck=?,bank_name=?,scope=? where bank_id=");
				sql.append(bank_id);
				list.add(bankFormat);
				list.add(bankCheck);
				list.add(bankname);
				list.add(scope);
			} else {
				if (Sql_switcher.searchDbServer() == Constant.MSSQL) {
					sql.append("insert into gz_bank (bankFormat,bankCheck,bank_name,scope,username) values(?,?,?,?,?)");
				} else 
				{
					sql.append("insert into gz_bank (bankFormat,bankCheck,bank_name,scope,username,bank_id) values (?,?,?,?,?,'"+ bank_id + "')");
				}
				list.add(bankFormat);
				list.add(bankCheck);
				list.add(bankname);
				list.add(scope);
				list.add(this.userview.getUserName());
				
				dao.delete("delete from gz_bank_item where bank_id='"+bank_id+"'",new ArrayList());//新增报盘时删除可能存在的脏数据。 zhanghua 2017-6-26
			}
			
			dao.update(sql.toString(), list);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}


	/**
	 * 导出为文本文件的拼接数据的方法
	 * 
	 * @param columns
	 *            列头信息
	 * @param dataList数据
	 * @param type类型
	 *            0制表符分隔的文本文件1空格分隔的文本文件 2无分隔的文本文件 4|分隔的文本文件 5逗号分隔的文本文件
	 * @return
	 * @throws GeneralException
	 */
	public String getDataStringBuffer(ArrayList columns, ArrayList dataList,
			String type) throws GeneralException {
		StringBuffer buf = new StringBuffer();
		try {
			for (int i = 0; i < dataList.size(); i++) {
				LazyDynaBean bean = (LazyDynaBean) dataList.get(i);
				for (int j = 0; j < columns.size(); j++) {
					FieldItem list = (FieldItem) columns.get(j);
					if("aid".equalsIgnoreCase(list.getItemid())){
						continue;
					}
					buf.append(bean.get((String) list.getItemid().toLowerCase()));
					if(j<columns.size()-1)//xiegh bug27902 20170519 每行的最后一个数据不用加type
						buf.append(type);
				}
				// 最后不添加换行了 【59956】导出银行报盘，无法直接导入财务系统里面。导出的文档中有空格
				if((dataList.size() - 1) != i) {
					buf.append("\r\n");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return buf.toString();
	}

	/**
	 * 文本文件导出时，对数据格式处理的方法
	 * 
	 * @param format
	 *            行首行末标识
	 * @param datalist需要导出的数据
	 * @param type类型
	 * @param a0100s
	 * @param model
	 *            =0薪资发放 =1薪资审批
	 * @param boscount
	 *            次数 薪资审批中有效
	 * @param bosdate
	 *            业务日期 薪资审批中有效
	 * @return
	 * @throws GeneralException
	 */
	public StringBuffer setFormat(String format, ArrayList datalist,
			String type, HashMap hm, String a0100s, String model,
			String boscount, String bosdate) throws GeneralException {
		StringBuffer buf = new StringBuffer();
		;
		try {
			if (format.indexOf("`") != -1) {
				String[] temp = format.split("`");
				for (int i = 0; i < temp.length; i++) {
					if (temp[i] == null || "".equals(temp[i]))
						continue;
					if (temp[i].indexOf(ResourceFactory.getProperty("gz_new.gz_accounting.bankdisk.totalPerson")) != -1) {// //总人数
						String t = datalist.size() + "";
						if (temp[i].indexOf("[") != -1) {
							if (temp[i].indexOf("]") != -1) {
								String format_str = temp[i]
										.substring(temp[i].indexOf("[") + 1,
												temp[i].indexOf("]"));
								if (format_str != null
										&& format_str.trim().length() > 0) {
									t = this.getNumberFormat(datalist.size()
											+ "", format_str, -1);
								}
							}
						}
						//buf.append(t + type); xiegh 20170519 bug27902 将总人数后面的'type'移除
						buf.append(t+" ");

					} else {
						String itemdesc = "";
						if (temp[i].trim().indexOf("[") != -1)
							itemdesc = temp[i].trim().substring(0,
									temp[i].trim().indexOf("["));
						else
							itemdesc = temp[i];
						LazyDynaBean bean = this.getItemInfo(itemdesc);
						if (bean != null) {
							String itemid = (String) bean.get("itemid");
							String value = temp[i];
							if (bean.get("itemtype") != null
									&& "N"
											.equalsIgnoreCase((String) bean.get("itemtype"))) {
								int itemlength = Integer
										.parseInt(((String) bean
												.get("itemlength")));
								double d = 0.00d;
								d = this.getSum(itemid, a0100s, model,
										boscount, bosdate);
								if (temp[i].indexOf("[") != -1
										&& temp[i].indexOf("]") != -1) {
									String for_str = temp[i].substring(temp[i]
											.indexOf("[") + 1, temp[i]
											.indexOf("]"));
									if (for_str == null || "".equals(for_str)) {
										if (hm.get(itemid.toUpperCase()) != null)
											for_str = (String) hm.get(itemid
													.toUpperCase());
									}
									if (for_str == null || "".equals(for_str))
										for_str = "0.00";

									value = this.getNumberFormat(d + "",
											for_str, itemlength);
								} else {
									String for_str = "0.00";
									if (hm.get(itemid.toUpperCase()) != null)
										for_str = (String) hm.get(itemid
												.toUpperCase());
									value = this.getNumberFormat(d + "",
											for_str, itemlength);
								}
							}else if(bean.get("itemtype") != null
									&& "D"
									.equalsIgnoreCase((String) bean.get("itemtype"))) {
								String date=this.getSalaryDate(itemid,model,boscount,bosdate);
								if(format.indexOf("[")!=-1&&format.indexOf("]")!=-1) {
									String for_str= format.substring(format.indexOf("[")+1,format.indexOf("]"));
									value = this.getDateFormat(date, for_str);
								}else{
									value=date;
								}

							}
							buf.append(value + type);
						} else {
							if(i<temp.length-1)//xiegh 20170519 BUG:27905
								buf.append(temp[i] + type);
							else
								buf.append(temp[i]);
						}
					}

				}
			} else {
				if (format
						.indexOf(ResourceFactory
								.getProperty("gz_new.gz_accounting.bankdisk.totalPerson")) != -1) {
					String t = datalist.size() + "";
					if (format.indexOf("[") != -1) {
						if (format.indexOf("]") != -1) {
							String format_str = format.substring(format
									.indexOf("[") + 1, format.indexOf("]"));
							if (format_str != null
									&& format_str.trim().length() > 0) {
								t = this.getNumberFormat(datalist.size() + "",
										format_str, -1);
							}
						}
					}
					//buf.append(t + type); xiegh 20170519 bug27902 将总人数后面的'type'移除
					buf.append(t + " ");
				} else {
					String itemdesc = "";
					if (format.trim().indexOf("[") != -1)
						itemdesc = format.trim().substring(0,
								format.trim().indexOf("["));
					else
						itemdesc = format;
					LazyDynaBean bean = this.getItemInfo(itemdesc);
					if (bean != null) {
						String itemid = (String) bean.get("itemid");
						String value = format;
						if (bean.get("itemtype") != null
								&& "N"
										.equalsIgnoreCase((String) bean.get("itemtype"))) {
							int itemlength = Integer.parseInt(((String) bean
									.get("itemlength")));
							double d = 0.00d;
							d = this.getSum(itemid, a0100s, model, boscount,
									bosdate);
							if (format.indexOf("[") != -1
									&& format.indexOf("]") != -1) {
								String for_str = format.substring(format
										.indexOf("[") + 1, format.indexOf("]"));
								if (for_str == null || "".equals(for_str)) {
									if (hm.get(itemid.toUpperCase()) != null)
										for_str = (String) hm.get(itemid
												.toUpperCase());
								}
								if (for_str == null || "".equals(for_str))
									for_str = "0.00";
								value = this.getNumberFormat(d + "", for_str,
										itemlength);
							} else {
								String for_str = "0.00";
								if (hm.get(itemid.toUpperCase()) != null)
									for_str = (String) hm.get(itemid
											.toUpperCase());
								value = this.getNumberFormat(d + "", for_str,
										itemlength);
							}
						}else if(bean.get("itemtype") != null
								&& "D"
								.equalsIgnoreCase((String) bean.get("itemtype"))) {
							String date=this.getSalaryDate(itemid,model,boscount,bosdate);
							if(format.indexOf("[")!=-1&&format.indexOf("]")!=-1) {
								String for_str= format.substring(format.indexOf("[") + 1, format.indexOf("]"));
								value = this.getDateFormat(date, for_str);
							}else{
								value=date;
							}

						}
						buf.append(value + type);
					} else {
						//buf.append(format + type);
						buf.append(format+" ");
					}

				}
			}
			buf.append("\r\n");
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return buf;
	}

	/**
	 * 拼接导出数据时的sql条件
	 * 
	 * @param model
	 * @param spSQL
	 * @return
	 * @throws GeneralException
	 */
	public String getA0100s(String model, String spSQL,String tableName) throws GeneralException {
		StringBuffer sql = new StringBuffer("");
		try {
			String sql_str = "select a0100,nbase from " + tableName
					+ " where 1=1 ";
			if ("1".equals(model)) {
				sql_str += " and (" + spSQL + ") ";
			}
			// 获取表格工具过滤以及页面模糊查询返回的sql片段
			sql_str += this.salaryTemplateBo.getfilter(gz_tablename);
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(sql_str);
			while (rs.next()) {
				sql.append(" or (T.a0100='");
				sql.append(rs.getString("a0100"));
				sql.append("' and UPPER(T.pre)='"
						+ rs.getString("nbase").toUpperCase() + "')");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		if (sql != null && sql.toString().trim().length() > 0)
			return sql.toString().substring(3);
		else
			return " 1=2 ";
	}

//	/**
//	 * 获取导出数量的方法
//	 * 
//	 * @param itemid
//	 *            导出的列
//	 * @param a0100s
//	 *            条件sql
//	 * @param model
//	 *            =0薪资发放 =1薪资审批
//	 * @param boscount
//	 * @param bosdate
//	 * @return
//	 * @throws GeneralException
//	 */
//	public double getCount(String itemid, String a0100s, String model,
//			String boscount, String bosdate) throws GeneralException {
//		double d = 0.0;
//		RowSet rs = null;
//		try {
//			String tableName=model.equals("1")?"salaryhistory":this.gz_tablename;
//				
//			StringBuffer spSQL = new StringBuffer("");
//			if (model.equals("1")) {// 薪资审批中
//				spSQL.append(" salaryid=" + salaryid);
//				spSQL.append(" and A00Z3=");
//				spSQL.append(boscount);
//				spSQL.append(" and A00Z2=");
//				spSQL.append(Sql_switcher.dateValue(bosdate));
//				spSQL
//						.append(" and (( curr_user='"
//								+ this.userview.getUserName()
//								+ "' and ( sp_flag='02' or sp_flag='07' ) ) or ( ( (AppUser is null    ) or AppUser Like '%;"
//								+ this.userview.getUserName()
//								+ ";%' ) and   sp_flag='06' ) ) ");
//
//			}
//			StringBuffer sql = new StringBuffer();
//			sql.append("select ");
//			sql.append("count(" + itemid + ") ");
//			sql.append(" from ");
//			sql.append(tableName + " T ");
//			sql.append(" where 1=1 ");
//			if (model.equals("1")) {
//				sql.append(" and (" + spSQL + ")");
//			}
//
//			sql.append(" and (");
//			sql.append(a0100s.toUpperCase().replaceAll("T.PRE", "T.NBASE"));
//			sql.append(") group by NBASE,A0100,A00Z0,dbid ");
//			ContentDAO dao = new ContentDAO(this.conn);
//			rs = dao.search(sql.toString());
//			while (rs.next()) {
//				d += rs.getDouble(1);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw GeneralExceptionHandler.Handle(e);
//		} finally {
//			if (rs != null) {
//				try {
//					rs.close();
//				} catch (Exception e) {
//					e.printStackTrace();
//					throw GeneralExceptionHandler.Handle(e);
//				}
//			}
//		}
//		return d;
//	}
	
    public double getSum(String itemid, String a0100s, String model,
			String boscount, String bosdate)
    {
    	double d=0.0;
    	RowSet rs = null;
    	try
    	{
    		StringBuffer spSQL=new StringBuffer("");
    		String tableName= "1".equals(model)?"salaryhistory":this.gz_tablename;
			if ("1".equals(model)) {// 薪资审批中
				spSQL.append(" salaryid=" + salaryid);
				spSQL.append(" and A00Z3=");
				spSQL.append(boscount);
				spSQL.append(" and A00Z2=");
				spSQL.append(Sql_switcher.dateValue(bosdate));
				spSQL.append(" and (( curr_user='"
								+ this.userview.getUserName()
								+ "' and ( sp_flag='02' or sp_flag='07' ) ) or ( ( (AppUser is null    ) or AppUser Like '%;"
								+ this.userview.getUserName()
								+ ";%' ) and   sp_flag='06' ) ) ");
				//spSQL.append(" and (( curr_user='"+view.getUserId()+"' and ( sp_flag='02' or sp_flag='07' ) ) 
//				or ( ( (AppUser is null  "+gzbo.getPrivWhlStr("")+"  ) or AppUser 
//						Like '%;"+view.getUserName()+";%' ) and   sp_flag='06' ) ) ");

			}
			StringBuffer sql = new StringBuffer();
			sql.append("select ");
			sql.append("sum("+itemid+") ");
			sql.append(" from ");
			sql.append(tableName+" T ");
			sql.append(" where 1=1 ");
			if("1".equals(model))
			{
				sql.append(" and ("+spSQL+")");
			}
			
    	    sql.append(" and (");
	    	sql.append(a0100s.toUpperCase().replaceAll("T.PRE", "T.NBASE"));
	    	sql.append(")");
	    	ContentDAO dao  = new ContentDAO(this.conn);
	    	rs= dao.search(sql.toString());
            while(rs.next())
            {
            	d += rs.getDouble(1);
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
    	return d;
    }

	/**
	 * 获取薪资里日期型指标的最大值，银行报批导出使用
	 * @param itemid
	 * @param model
	 * @param boscount
	 * @param bosdate
	 * @return
	 * @author ZhangHua
	 * @date 15:13 2019/1/8
	 */
	public String getSalaryDate(String itemid, String model, String boscount, String bosdate) {
		String date = null;
		RowSet rs = null;
		try {
			StringBuffer spSQL = new StringBuffer("");
			String tableName = "1".equals(model) ? "salaryhistory" : this.gz_tablename;
			if ("1".equals(model)) {// 薪资审批中
				spSQL.append(" salaryid=" + salaryid);
				spSQL.append(" and A00Z3=");
				spSQL.append(boscount);
				spSQL.append(" and A00Z2=");
				spSQL.append(Sql_switcher.dateValue(bosdate));
				spSQL.append(" and (( curr_user='"
						+ this.userview.getUserName()
						+ "' and ( sp_flag='02' or sp_flag='07' ) ) or ( ( (AppUser is null    ) or AppUser Like '%;"
						+ this.userview.getUserName()
						+ ";%' ) and   sp_flag='06' ) ) ");
			}
			StringBuffer sql = new StringBuffer();
			sql.append("select ");
			sql.append("max(" + itemid + ") ");
			sql.append(" from ");
			sql.append(tableName + " T ");
			sql.append(" where 1=1 ");
			if ("1".equals(model)) {
				sql.append(" and (" + spSQL + ")");
			}
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql.toString());
			while (rs.next()) {
				Date d = rs.getDate(1);
				if(d!=null){
					SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
					date=sdf.format(d);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return date;
	}

	/**
	 * 获取对应itemdesc的薪资数据
	 * 
	 * @param itemdesc
	 * @return
	 * @throws GeneralException
	 */
	private LazyDynaBean getItemInfo(String itemdesc) throws GeneralException {
		LazyDynaBean bean = null;
		try {
			String sql = "select itemtype,itemid,itemlength from salaryset where itemdesc='"
					+ itemdesc + "' and salaryid=" + this.salaryid;
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(sql);
			while (rs.next()) {
				bean = new LazyDynaBean();
				bean.set("itemtype", rs.getString("itemtype"));
				bean.set("itemid", rs.getString("itemid"));
				bean.set("itemlength", rs.getInt("itemlength") + "");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return bean;
	}

	/**
	 * 导出数据格式化的方法
	 * 
	 * @param value
	 *            导出值
	 * @param format
	 *            格式
	 * @param itemlength
	 *            长度
	 * @return
	 * @throws GeneralException
	 */
	private String getNumberFormat(String value, String format, int itemlength)
			throws GeneralException {
		String return_str = "";
		String before = "";
		String after = "";
		try {
			if (value == null || "".equals(value)) {
				value="0";
			}
			if (format == null || "".equals(format)) {
				return value;
			}
			String prefix = "";
			if (format.indexOf("@") != -1) {
				String sub_str = value;
				if (value.indexOf(".") != -1)
					sub_str = value.substring(0, value.indexOf("."));
				while (prefix.length() + sub_str.length() < itemlength)
					prefix += " ";
			}
			if (format.indexOf("\"") != -1) {
				String aformat = format;
				char[] achar = aformat.toCharArray();
				int y = 1;
				StringBuffer sb_format = new StringBuffer("");
				StringBuffer aa = new StringBuffer("");
				boolean isBefore = false;
				for (int i = 0; i < achar.length; i++) {
					if (achar[i] == '\"') {
						if (i == 0)
							isBefore = true;
						if (y == 1) {
							y = 2;
						} else {
							if (isBefore) {
								if (i != achar.length - 1) {
									before = aa.toString();
									aa.setLength(0);
								}
							}
							y = 1;
						}
					}
					if (y == 2)// 固定串
					{
						if (achar[i] != '\"')
							aa.append(achar[i]);
					} else {// 格式串
						if (achar[i] != '\"')
							sb_format.append(achar[i]);
					}
				}
				after = aa.toString();
				format = sb_format.toString();
			}
			if (format.indexOf("0") != -1
					&& (format.indexOf("!") == -1 && format.indexOf("#") == -1 && format
							.indexOf("%") == -1)) {
				if (format.indexOf(".") == -1) {
					DecimalFormat dcom = new DecimalFormat(format);
					return_str = dcom.format(Double.parseDouble(value));
				} else {
					String t_str = getXS(value, format.substring(
							format.indexOf(".") + 1).length());
					DecimalFormat dcom = new DecimalFormat(format);
					return_str = dcom.format(Double.parseDouble(t_str));
				}
			} else if (format.indexOf("!") != -1)// 00000000!根据格式判断是否显示小数位?
			{
				double d = Double.parseDouble(value);
				String t_format = format.substring(0,
						format.lastIndexOf("0") + 1);
				if (format.indexOf("%") != -1 && format.indexOf(".") != -1) {
					d = d * 100;
					t_format = format.substring(0, format.lastIndexOf("."))
							+ format.substring(format.indexOf(".") + 1, format
									.lastIndexOf("0") + 1);
				}
				DecimalFormat dcom = new DecimalFormat(t_format);
				return_str = dcom.format(d);

			} else if (format.indexOf("!") == -1
					&& (format.indexOf("%") != -1 && format.indexOf("#") != -1))// #0.00%
			{
				double d = Double.parseDouble(value);
				d = d * 100;
				String t_format = format.replaceAll("#", "");
				t_format = t_format.replaceAll("%", "");
				DecimalFormat dcom = new DecimalFormat(t_format);
				return_str = dcom.format(d) + "%";
			} else if (format.indexOf(",") != -1)// #,##00
			{
				int xs = 0;
				String t_value = value;
				String p_value = "";
				if (format.indexOf(".") != -1) {
					xs = format.substring(format.lastIndexOf(".") + 1).length();
				}
				t_value = getXS(value, xs);
				if (t_value.indexOf(".") != -1) {
					if (t_value.length() > t_value.substring(0,
							t_value.indexOf(".")).length() + 1) {
						p_value = t_value.substring(t_value.indexOf(".") + 1);// 小数部分
					}
					t_value = t_value.substring(0, t_value.indexOf("."));// 整数部分
				}
				if (t_value.length() % 3 == 0) {
					String[] t = new String[t_value.length() / 3];
					StringBuffer buf = new StringBuffer();
					for (int i = 0; i < t.length; i++) {
						t[i] = t_value.substring(i * 3, i * 3 + 3);
					}
					for (int j = 0; j < t.length; j++) {
						buf.append(t[j]);
						buf.append(",");
					}
					buf.setLength(buf.length() - 1);
					return_str = buf.toString()
							+ (p_value.length() == 0 ? "" : ("." + p_value));
				} else {
					String[] t = new String[(int) (t_value.length() / 3)];
					StringBuffer buf = new StringBuffer();
					String temp = t_value.substring(0, t_value.length() % 3);
					String tmp = t_value.substring(t_value.length() % 3);
					for (int i = 0; i < t.length; i++) {
						t[i] = tmp.substring(i * 3, i * 3 + 3);
					}
					buf.append(temp);
					for (int j = 0; j < t.length; j++) {
						buf.append(",");
						buf.append(t[j]);
					}
					return_str = buf.toString()
							+ (p_value.length() == 0 ? "" : ("." + p_value));
				}

			} else if (format.indexOf("#") != -1 && format.indexOf("%") == -1
					&& format.indexOf(",") == -1 && format.indexOf("!") == -1)// ####.##
			{
				if (value.indexOf(".") == -1) {
					return_str = getXS(value, 0);
				} else {
					String v_xs = value.substring(value.indexOf(".") + 1);
					if (format.indexOf(".") != -1) {
						String f_xs = format.substring(format.indexOf(".") + 1);
						if (v_xs.length() > f_xs.length()) {
							return_str = getXS(value, f_xs.length());
						} else {
							return_str = getXS(value, v_xs.length());
						}
					} else {
						return_str = getXS(value, v_xs.length());
					}
					while (return_str.endsWith("0") || return_str.endsWith(".")) {
						if (return_str.endsWith(".")) {
							return_str = return_str.substring(0, return_str
									.length() - 1);
							break;
						} else {
							return_str = return_str.substring(0, return_str
									.length() - 1);
						}

					}
				}
			} else {
				return_str = value;
			}
			return_str = return_str.replace("@", prefix);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return before+return_str+after;
	}

	/**
	 * 对小数处理的方法
	 * 
	 * @param str
	 * @param scale
	 * @return
	 */
	private String getXS(String str, int scale) {
		if (str == null || "null".equalsIgnoreCase(str) || "".equals(str))
			str = "0.00";
		BigDecimal m = new BigDecimal(str);
		BigDecimal one = new BigDecimal("1");
		return m.divide(one, scale, BigDecimal.ROUND_HALF_UP).toString();
	}

	/**
	 * 删除选择的银行的模板和项目信息
	 * 
	 * @param String
	 *            tableName
	 * @param String
	 *            bank_id
	 * @throws GeneralException
	 */
	public void deleteBankInfo(String tableName, String bank_id)
			throws GeneralException {
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("delete from ");
			sql.append(tableName);
			sql.append(" where bank_id =");
			sql.append(bank_id);
			ContentDAO dao = new ContentDAO(this.conn);
			dao.delete(sql.toString(), new ArrayList());

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	
	
	public StringBuffer setFormat(StringBuffer buf,String format,int datasize,HashMap hm,String type,String tableName,String a0100s,String model,String boscount,String bosdate)
	  {
		  StringBuffer new_buf = buf;
		  try
		  {
			 
			  if(format.indexOf("`")!=-1)
			  {
				  String[] temp=format.split("`");
				  for(int i=0;i<temp.length;i++)
				  {
					 if(temp[i]==null|| "".equals(temp[i]))
						 continue;
					 if(temp[i].indexOf(ResourceFactory
								.getProperty("gz_new.gz_accounting.bankdisk.totalPerson"))!=-1) 
					 {
						 String t = datasize+"";
						 if(temp[i].indexOf("[")!=-1)
						 {
							 if(temp[i].indexOf("]")!=-1)
							 {
								 String format_str = temp[i].substring(temp[i].indexOf("[")+1,temp[i].indexOf("]"));
								 if(format_str!=null&&format_str.trim().length()>0)
								 {
									// DecimalFormat dcom = new DecimalFormat(format_str);
									// t=dcom.format((double)datalist.size());
									 t=this.getNumberFormat(datasize+"", format_str,-1);
								 }
							 }
						 }
						 new_buf.append(t+type);
	
					 }
					 else 
					 {
						 String itemdesc="";
						 if(temp[i].trim().indexOf("[")!=-1)
						      itemdesc=temp[i].trim().substring(0,temp[i].trim().indexOf("["));
						 else
							 itemdesc= temp[i];
						 LazyDynaBean bean=this.getItemInfo2(itemdesc, String.valueOf(salaryid));
						 if(bean!=null)
					     {
						   String itemid = (String)bean.get("itemid");
						   String value = temp[i];
						  if(bean.get("itemtype")!=null&& "N".equalsIgnoreCase((String)bean.get("itemtype")))
						 {
							  int itemlength=Integer.parseInt(((String)bean.get("itemlength")));
							 double d=0.00d;
							 d=this.getSum( itemid, a0100s, model, boscount, bosdate);
							 if(temp[i].indexOf("[")!=-1&&temp[i].indexOf("]")!=-1)
							 {
								 String for_str= temp[i].substring(temp[i].indexOf("[")+1,temp[i].indexOf("]"));
								 if(for_str==null|| "".equals(for_str))
								 {
									 if(hm.get(itemid.toUpperCase())!=null)
								    	 for_str=(String)hm.get(itemid.toUpperCase());
								 }
								 if(for_str==null|| "".equals(for_str))
									 for_str="0.00";
									 
								 value=this.getNumberFormat(d+"",for_str,itemlength);
							 }
							 else
							 {
								 String for_str="0.00";
								 if(hm.get(itemid.toUpperCase())!=null)
							    	 for_str=(String)hm.get(itemid.toUpperCase());
								 value=this.getNumberFormat(d+"",for_str,itemlength);
							 }
					   	 }else if(bean.get("itemtype")!=null&& "D".equalsIgnoreCase((String)bean.get("itemtype"))){

							  String date=this.getSalaryDate(itemid,model,boscount,bosdate);
							  if(format.indexOf("[")!=-1&&format.indexOf("]")!=-1) {
								  String for_str= temp[i].substring(temp[i].indexOf("[")+1,temp[i].indexOf("]"));
								  value = this.getDateFormat(date, for_str);
							  }else{
								  value=date;
							  }

						  }
						  new_buf.append(value+type);
					 }
					 else
					 {
						 new_buf.append(temp[i]+type);
					 }
				  }
				  
			  }
			  }
			  else
			  {
				  if(format.indexOf(ResourceFactory
							.getProperty("gz_new.gz_accounting.bankdisk.totalPerson"))!=-1) 
					 {
					  String t = datasize+"";
						 if(format.indexOf("[")!=-1)
						 {
							 if(format.indexOf("]")!=-1)
							 {
								 String format_str = format.substring(format.indexOf("[")+1,format.indexOf("]"));
								 if(format_str!=null&&format_str.trim().length()>0)
								 {
									 //DecimalFormat dcom = new DecimalFormat(format_str);
									 //t=dcom.format((double)datalist.size());
									 t=this.getNumberFormat(datasize+"", format_str,-1);
								 }
							 }
						 }
						 new_buf.append(t+type);
					 }
					 else  
					 {
						 String itemdesc="";
						 if(format.trim().indexOf("[")!=-1)
						      itemdesc=format.trim().substring(0,format.trim().indexOf("["));
						 else
							 itemdesc= format;
						 LazyDynaBean bean=this.getItemInfo2(itemdesc, String.valueOf(salaryid));
						 if(bean!=null)
					     {
						   String itemid = (String)bean.get("itemid");
						   String value = format;
						  if(bean.get("itemtype")!=null&& "N".equalsIgnoreCase((String)bean.get("itemtype")))
						 {
							  int itemlength=Integer.parseInt(((String)bean.get("itemlength")));
							 double d=0.00d;
							 d=this.getSum( itemid, a0100s, model, boscount, bosdate);
							 if(format.indexOf("[")!=-1&&format.indexOf("]")!=-1)
							 {
								 String for_str= format.substring(format.indexOf("[")+1,format.indexOf("]"));
								 if(for_str==null|| "".equals(for_str))
								 {
									 if(hm.get(itemid.toUpperCase())!=null)
								    	 for_str=(String)hm.get(itemid.toUpperCase());
								 }
								 if(for_str==null|| "".equals(for_str))
									 for_str="0.00";
								 value=this.getNumberFormat(d+"",for_str,itemlength);
							 }
							 else
							 {
								 String for_str="0.00";
								 if(hm.get(itemid.toUpperCase())!=null)
							    	 for_str=(String)hm.get(itemid.toUpperCase());
								 value=this.getNumberFormat(d+"",for_str,itemlength);
							 }
						 }else if(bean.get("itemtype")!=null&& "D".equalsIgnoreCase((String)bean.get("itemtype"))){

							  String date=this.getSalaryDate(itemid,model,boscount,bosdate);
							  if(format.indexOf("[")!=-1&&format.indexOf("]")!=-1) {
								  String for_str= format.substring(format.indexOf("[")+1,format.indexOf("]"));
								  value = this.getDateFormat(date, for_str);
							  }else{
							  	value=date;
							  }

						  }
						  new_buf.append(value+type);
					 }
					 else
					 {
						 new_buf.append(format+type);
					 }
	
			  }
			  }
			  //new_buf.append("\r\n");
		  }
		  catch(Exception e)
		  {
			  e.printStackTrace();
		  }
		  return new_buf;
	  }
	public LazyDynaBean getItemInfo2(String itemdesc,String salaryid)
	{
		LazyDynaBean bean = null;
		try
		{
			String sql="select itemtype,itemid,itemlength from salaryset where itemdesc='"+itemdesc+"' and salaryid="+salaryid;
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs= null;
			rs=dao.search(sql);
			while(rs.next())
			{
				bean = new LazyDynaBean();
				bean.set("itemtype",rs.getString("itemtype"));
				bean.set("itemid",rs.getString("itemid"));
				bean.set("itemlength", rs.getInt("itemlength")+"");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return bean;
	} 
	
	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
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

	public UserView getUserview() {
		return userview;
	}

	public void setUserview(UserView userview) {
		this.userview = userview;
	}

	public SalaryAccountBo getSalaryAccountBo() {
		return salaryAccountBo;
	}

	public void setSalaryAccountBo(SalaryAccountBo salaryAccountBo) {
		this.salaryAccountBo = salaryAccountBo;
	}

	public SalaryTemplateBo getSalaryTemplateBo() {
		return salaryTemplateBo;
	}

	public void setSalaryTemplateBo(SalaryTemplateBo salaryTemplateBo) {
		this.salaryTemplateBo = salaryTemplateBo;
	}

	public String getManager() {
		return manager;
	}

	public void setManager(String manager) {
		this.manager = manager;
	}

	public String getAppdate() {
		return appdate;
	}

	public void setAppdate(String appdate) {
		this.appdate = appdate;
	}

	public String getAppCount() {
		return appCount;
	}

	public void setAppCount(String appCount) {
		this.appCount = appCount;
	}
}