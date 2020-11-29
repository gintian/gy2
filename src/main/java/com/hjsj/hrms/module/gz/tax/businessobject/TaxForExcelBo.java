package com.hjsj.hrms.module.gz.tax.businessobject;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.*;
import org.jdom.Document;
import org.jdom.Element;

import javax.sql.RowSet;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class TaxForExcelBo {
	
	private Connection conn;
	private UserView userView;
	private Sheet sheet;
	private int tax_max_id = 1;
	public TaxForExcelBo(Connection frameconn, UserView userView) {
		this.conn = frameconn;
		this.userView = userView;
	}
	
	/**
	 * 取得导入文件中列指标列表
	 * @param form_file
	 * @author dengcan
	 * @return ArrayList<Map>
	 * @throws FileNotFoundException
	 */
	public ArrayList<LazyDynaBean> getOriginalDataFiledList(String fileid)
			throws GeneralException, FileNotFoundException {
		ArrayList<LazyDynaBean> list = new ArrayList<LazyDynaBean>();
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			HashMap<String, String> oppositeItem = new HashMap<String, String>();
			HashMap<String, String> relationItem = new HashMap<String, String>();
			this.getSheet(fileid);
			ArrayList<CommonData> list2 = this.getRowAllInfo(0);// 得到excel表格第一行数据，即表头
			CommonData commonData = null;
			LazyDynaBean bean = null;
			String msg = "、" + ResourceFactory.getProperty("label.gz.znjy") + "、" + ResourceFactory.getProperty("label.gz.jxjy")
							+ "、" + ResourceFactory.getProperty("label.gz.zfzj") + "、" + ResourceFactory.getProperty("label.gz.zfdk")
							+ "、" + ResourceFactory.getProperty("label.gz.sylr");
			int count = 0;
			for (int i = 0; i < list2.size(); i++) {
				hashMap = new HashMap();
				bean = new LazyDynaBean();
				commonData = list2.get(i);
				if (commonData != null) {
					bean.set("itemid", commonData.getDataValue());
					bean.set("itemdesc", commonData.getDataName());
					if(ResourceFactory.getProperty("label.gz.znjy").equals(commonData.getDataName())) {
						msg = msg.replace("、" + ResourceFactory.getProperty("label.gz.znjy"), "");
						count++;
					}else if(ResourceFactory.getProperty("label.gz.jxjy").equals(commonData.getDataName())) {
						msg = msg.replace("、" + ResourceFactory.getProperty("label.gz.jxjy"), "");
						count++;
					}else if(ResourceFactory.getProperty("label.gz.zfzj").equals(commonData.getDataName())) {
						msg = msg.replace("、" + ResourceFactory.getProperty("label.gz.zfzj"), "");
						count++;
					}else if(ResourceFactory.getProperty("label.gz.zfdk").equals(commonData.getDataName())) {
						msg = msg.replace("、" + ResourceFactory.getProperty("label.gz.zfdk"), "");
						count++;
					}else if(ResourceFactory.getProperty("label.gz.sylr").equals(commonData.getDataName())) {
						msg = msg.replace("、" + ResourceFactory.getProperty("label.gz.sylr"), "");
						count++;
					}
					if(oppositeItem.containsKey(commonData.getDataValue()))
						bean.set("itemid1", oppositeItem.get(commonData.getDataValue()));
					else
						bean.set("itemid1", "");
					if(relationItem.containsKey(commonData.getDataValue()))
						bean.set("itemid2", relationItem.get(commonData.getDataValue()));
					else 
						bean.set("itemid2", "");
				}
				list.add(bean);
			}
			if(count < 5) {
				//由于计算的时候需要gz_tax_mx中对应的隐藏指标znjy等，这里必须指定指标是子女教育，
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("label.gz.importJudge").replace("{0}", " " + msg.substring(1) + " ")));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}
	
	/**
	  * 取得 导入excel表里 rowNum行所有的信息
	  * @param rowNum 第几行
	  * @return
	  */
	public ArrayList getRowAllInfo(int rowNum)throws GeneralException
	{
		ArrayList list=new ArrayList();
		int rows = this.sheet.getPhysicalNumberOfRows();
		if(rowNum>rows)
			return list;
		Row row = this.sheet.getRow(rowNum);
		if (row != null) 
		{
			int cells = row.getPhysicalNumberOfCells();
			for (short c = 0; c < cells; c++) 
			{
				String value = "";
				Cell cell = row.getCell(c);
				if (cell != null) 
				{
					switch (cell.getCellType()) 
					{
						case Cell.CELL_TYPE_FORMULA:
							break;
						case Cell.CELL_TYPE_NUMERIC:
							value= String.valueOf((long) cell.getNumericCellValue());
							break;
						case Cell.CELL_TYPE_STRING:
							value= cell.getStringCellValue();
							break;
						default:
							value= "";
					}
				}
				if(c==0)
				{
					if("".equals(value))
						throw GeneralExceptionHandler.Handle(new Exception("文件格式不正确，第一行为导入的文件列名，不能为空"));
				}
				if("".equals(value))
					continue;
				list.add(new CommonData(value.toUpperCase(),value.toUpperCase()));
			}
		}
		return list;
	}
	
	/**
	 * 获取导入的sheet
	 * @return 拿到要导入的Excel
	 * @throws GeneralException
	 */
	private void getSheet(String fileid) throws GeneralException 
	{
		InputStream input = null;
		Workbook work = null;
		Sheet sheet = null;
		try {
			input = VfsService.getFile(fileid);
			work = WorkbookFactory.create(input);
			this.sheet = sheet = work.getSheetAt(0);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeIoResource(input);
			PubFunc.closeIoResource(work);
		}
	}
	
	/**
	 * 取得 薪资类别中的薪资项目列表
	 * @return
	 */
	public ArrayList getTaxMxItemList()
	{
		ArrayList list=new ArrayList();
		TaxMxBo taxBo = new TaxMxBo(conn, userView);
		//个税明细表获取固定字段
        ArrayList<Field> itemList = taxBo.searchCommonItemList();
        
        itemList.add(taxBo.getTaxUnitField(true));
        //个税明细表动态维护的指标
        ArrayList<Field> chglist = taxBo.searchDynaItemList();
        if(chglist.size()>0){
        	itemList.addAll(chglist);
        }
        
        /*ArrayList<Field> hiddenList = taxBo.getHiddenField();
        if(hiddenList.size()>0){
        	itemList.addAll(hiddenList);
        }*/
        
        LazyDynaBean bean = new LazyDynaBean();
		bean.set("itemid", "blank");
		bean.set("itemdesc","（空）");
		list.add(bean);
		for(int i=0;i<itemList.size();i++)
		{
			Field fi = (Field)itemList.get(i);
			String itemid=(String)fi.getName();
			String itemdesc=(String)fi.getLabel();
			if(!("a0100".equalsIgnoreCase(itemid) || "flag".equalsIgnoreCase(itemid)|| "Tax_max_id".equalsIgnoreCase(itemid)))
			{
				String typeDesc="字符";
				if(fi.getDatatype()==DataType.DATE)
				{
					typeDesc="日期";
				}
				else if(fi.getDatatype()==DataType.INT||fi.getDatatype()==DataType.FLOAT)
				{
					typeDesc="数值";
				}
				bean = new LazyDynaBean();
				bean.set("itemid",itemid);
				bean.set("itemdesc",itemid+" : "+itemdesc+" ( "+typeDesc+" )");
				list.add(bean);
			}		
		}
		return list;
	}
	
	 	/**
		 * 选择关联指标
		 * @param itemdesc
		 * @param dao
		 * @return
		 */
		public ArrayList<LazyDynaBean> getRelation(ArrayList<LazyDynaBean> list)
		{
			RowSet rs = null;
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sql = new StringBuffer();
			sql.append(" select itemid,fieldsetid from fielditem");
			sql.append(" where itemdesc = ?");
			ArrayList<LazyDynaBean> relation = new ArrayList<LazyDynaBean>();
			ArrayList idlist = new ArrayList();
			LazyDynaBean bean = new LazyDynaBean();
			bean.set("itemid", "blank");
			bean.set("itemdesc","（空）");
			relation.add(bean);
			try {
				for(int i=0;i<list.size();i++){
					idlist = new ArrayList();
					String itemdesc = (String) list.get(i).get("itemid");
					//单位单独判断，否则没有唯一性指标的时候，不能使用单位进行关联
					if(itemdesc.equals(ResourceFactory.getProperty("label.query.unit")) || itemdesc.equals(ResourceFactory.getProperty("lable.statistic.companyname"))) {
						bean = new LazyDynaBean();
						bean.set("itemid", "b0110");
						bean.set("itemdesc", "B0110 ：" + itemdesc + " ( 字符 )");
						relation.add(bean);
						continue;
					}else {
						idlist.add(itemdesc);
					}
					rs = dao.search(sql.toString(),idlist);
					if(rs.next())
					{
						if("a01".equalsIgnoreCase(rs.getString("fieldsetid")))
						{
							FieldItem fieldItem = DataDictionary.getFieldItem(rs.getString("itemid"));
							itemdesc = fieldItem.getItemdesc();
							String itemid = fieldItem.getItemid();
							String itemtype = fieldItem.getItemtype();
							String typeDesc = "字符";//字符
							if ("N".equalsIgnoreCase(itemtype))
								typeDesc = "数值";//数值
							if ("D".equalsIgnoreCase(itemtype))
								typeDesc = "日期";//日期

							bean = new LazyDynaBean();
							bean.set("itemid", itemid);
							bean.set("itemdesc", itemid.toUpperCase() + "：" + itemdesc + " ( " + typeDesc + " )");
							relation.add(bean);
						}
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}finally{
				PubFunc.closeResource(rs);
			}
			return relation;
		}
	

	/**
	 * @param relationItem 关联指标
	 * @param oppositeItem 更新指标
	 * @param path 上传文件路劲
	 * @param filename 文件名
	 * @return
	 */
	public int importFileDataToTaxMx(ArrayList<String> relationItem, ArrayList<String> oppositeItem, String fileid) {
		int rows = 0;
		try {
			this.getSheet(fileid);
			rows = getTotalDataRows();
			ArrayList rowAllInfo = getRowAllInfo(0);
			ArrayList<LazyDynaBean> importData = this.getImportData(rows,rowAllInfo,relationItem);
			String toTable = "GZ_TAX_MX";
			this.iteratExcelData(oppositeItem, importData, relationItem,toTable);
		} catch (GeneralException e) {
			e.printStackTrace();
		}
		
		return rows;
	}
	
	/**
	 * 获得数据总行数
	 * @return
	 */
	public int getTotalDataRows()
	{
		return this.sheet.getPhysicalNumberOfRows()-1;
	}
	
	/**
	 * 获得要导入的所有数据 
	 * @param allRow 数据行数
	 * @param columnDataList 表头
	 * @param relationItem 关联指标
	 * @return
	 */
	public ArrayList<LazyDynaBean> getImportData(int allRow,ArrayList columnDataList,ArrayList relationItem)
	{
		HashMap keyMap = new HashMap();
		ArrayList<LazyDynaBean> importDataList = new ArrayList<LazyDynaBean>();
		for(int i=0;i<relationItem.size();i++)
		{
			keyMap.put(relationItem.get(i),relationItem.get(i));
		}
		try
		{
			LazyDynaBean abean=null;
			CommonData data=null;
			SimpleDateFormat dateformat=new SimpleDateFormat("yyyy-MM-dd");
			for (int r = 1; r <= allRow; r++) {
				Row row = this.sheet.getRow(r);
				if (row != null) {
					StringBuffer getkey = new StringBuffer();
					abean = new LazyDynaBean();
					for (short c = 0; c < columnDataList.size(); c++) {
						data = (CommonData) columnDataList.get(c);
						String columnName = data.getDataValue();
						String value = "";
						Cell cell = row.getCell(c);
						if (cell != null) {
							switch (cell.getCellType()) {
							case Cell.CELL_TYPE_FORMULA:
								break;
							case Cell.CELL_TYPE_NUMERIC:
								if (HSSFDateUtil.isCellDateFormatted(cell)) { //判断是日期类型 统一转换成yyyy-MM-dd格式入库  zhaoxg add 2013-12-4
									Date dt = HSSFDateUtil.getJavaDate(cell.getNumericCellValue());//获取成DATE类型 
									value = dateformat.format(dt); 
								}else{
									value = String.valueOf((double) cell.getNumericCellValue());
									value = moveZero(value);
								}
								break;
							case Cell.CELL_TYPE_STRING:
								if(isValidDate(cell.getStringCellValue())){//判断是日期类型 统一转换成yyyy-MM-dd格式入库  zhaoxg add 2013-12-4
									value = dateformat.format(dateformat.parse(cell.getStringCellValue())); 
								}else{
									value = cell.getStringCellValue();
								}
								break;
							default:
								value = "";
							} 
						}
						// 如果人员标识列，去其值作为Map的Key，如身份证号码
						if(keyMap.containsKey(columnName))
						{
							getkey.append(value.trim());
						}
						abean.set(columnName, value.trim());						
					}
					importDataList.add(abean);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return importDataList;
	}
		//移除小数点后面的零 是零就不显示了
	  public String moveZero(String number)
	    {

		DecimalFormat df = new DecimalFormat("###############.##########");
		if (number == null || number.length() == 0)
		    return "";
		if (Float.parseFloat(number) == 0)
		    return "";
		return df.format(Double.parseDouble(number));
	    }
	  
	  /**
		 * 判断字符串是否是日期格式 zhaoxg add 2013-12-4
		 * @param s
		 * @return
		 */
		public static boolean isValidDate(String s)
		{
			try{
				SimpleDateFormat dateformat=new SimpleDateFormat("yyyy-MM-dd");
				dateformat.parse(s);
				return true;
				}
			catch (Exception e){
				// 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
				return false;
				}
		}
		
	/**初始化所有代码型指标，以便导入代码值，而不是汉字*/
	public HashMap<String, String> initPreMap(ArrayList<String> oppositeItem)
	{
		HashMap<String, String> codeMap = new HashMap<String, String>();
		RowSet rs = null;
		try
    	{
           ContentDAO dao = new ContentDAO(this.conn);
           HashMap<String, Field> fixedFieldMap = initFixedFieldMap();
           String[] excelItem = new String[oppositeItem.size()];
           String[] importItem =  new String[oppositeItem.size()];
			for(int i=0;i<oppositeItem.size();i++)
			{
				String[] temp =  oppositeItem.get(i).split("=");
				excelItem[i] = temp[0];               
				importItem[i] = temp[1].toLowerCase();
			}
			// 取得要更新字段的数据类型
			StringBuffer sql_buf=new StringBuffer("");
			for(int i=0;i<oppositeItem.size();i++)
			{
				FieldItem fi = DataDictionary.getFieldItem(importItem[i]);
				if(fi!=null)
				{
					if("a".equalsIgnoreCase(fi.getItemtype())&&!"0".equalsIgnoreCase(fi.getCodesetid()))
					{
						sql_buf.append(",'"+fi.getCodesetid()+"'");
					}
				}else
				{ 
				    if(fixedFieldMap.get(importItem[i].toUpperCase())!=null)
				    {
				    	Field field = (Field)fixedFieldMap.get(importItem[i].toUpperCase());
				    	if(field.getDataType()==DataType.STRING&&!"0".equals(field.getCodesetid()))
				    	{
				    		sql_buf.append(",'"+field.getCodesetid()+"'");
				    	}
				    }
				}
			}
			if(sql_buf.toString().length()>0)
			{
	    		String sql="select codeitemid,codeitemdesc from codeitem where codesetid in("+sql_buf.toString().substring(1)+")";
	    		rs = dao.search(sql);
	    		while(rs.next())
	    		{
	    			codeMap.put(rs.getString("codeitemdesc"),rs.getString("codeitemid"));
	    		}
			}
			
    	}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
		}
		return codeMap;
	}
	
	/**
	 * 取得所有要导入的数据
	 * @param oppositeItem 指标对应（姓名=a0101）
	 * @param allImportDataMap
	 * @param allImportDataList bean（汉字，值）
	 * @param nbaseItem 人员标识
	 * @param dbnamestr 人员库前缀串
	 * @param dao
	 */
	public void iteratExcelData(ArrayList<String> oppositeItem,ArrayList allImportDataList,ArrayList<String> relationItem ,String toTable)
	{
		try
		{	
			ContentDAO dao = new ContentDAO(conn);
			HashMap<String, String> codeSetMap = this.initPreMap(oppositeItem);
			int length = relationItem.size();
			String[] itemid = new String[length];
			String[] itemtype = new String[length];
			String[] decwidth = new String[length];	
			String[] codeset=new String[length];
			String[] desc_ = new String[length];
			tax_max_id=this.getTaxMaxId(dao,toTable);
			String[] desc  = null;
			/**人员标识字段*/
			for(int i=0;i<length;i++){
				String itemdesc = relationItem.get(i);
				desc = itemdesc.split("=");
				String getitemid = this.getItemId(desc[0],dao);
				if(StringUtils.isBlank(getitemid) && (desc[0].equals(ResourceFactory.getProperty("label.query.unit")) || desc[0].equals(ResourceFactory.getProperty("lable.statistic.companyname")))) {
					desc_[i]=itemdesc.split("=")[0];
					itemid[i]="B0110";
					itemtype[i]="A";
					decwidth[i] = 0+"";
					codeset[i]="UN";
				}else {
					FieldItem fi = DataDictionary.getFieldItem(getitemid);				
					desc_[i]=itemdesc.split("=")[0];
					itemid[i]=getitemid;
					itemtype[i]=fi.getItemtype();
					decwidth[i] = fi.getDecimalwidth()+"";
					codeset[i]=fi.getCodesetid();
				}
			}
			ArrayList<RecordVo> addlist = new ArrayList();
			ArrayList deleteList = new ArrayList();
			int allnums = allImportDataList.size();
			int iteratNum = allnums/30+1;	
			StringBuffer where = new StringBuffer();
			int initNum = 0;
			/**所有记录bean*/
			for(int t=0;t<allImportDataList.size();t++)
			{
				StringBuffer whereTemp = new StringBuffer();
				LazyDynaBean abean = (LazyDynaBean)allImportDataList.get(t);
				for(int x=0;x<length;x++)
				{
					// 条件对应的值，如A0177=430651196277324468
					if("A".equals(itemtype[x])){
						if("0".equals(codeset[x]))
					    	whereTemp.append(" and "+itemid[x]+"='"+((String)abean.get(desc_[x].trim())).trim()+"'");
						else
						{
							if(codeSetMap==null||codeSetMap.get(codeset[x].toUpperCase())==null)
							{
								codeSetMap.put(codeset[x].toUpperCase(), "1");
							}
							HashMap<String, String> codeItemidMap = this.getCodeitemIdByCodeSetID(codeset[x]);
							whereTemp.append(" and "+itemid[x]+"='"+(codeItemidMap.get(((String)abean.get(desc_[x].trim())))==null?"":codeItemidMap.get(((String)abean.get(desc_[x].trim()))))+"'");
						}
					}
					if("N".equals(itemtype[x])){
						String tempValue=((String)abean.get(desc_[x].trim())).trim();
							whereTemp.append(" and "+itemid[x]+"="+tempValue);
					}

				}
				this.setPersonRecordVo(itemid,oppositeItem,abean, whereTemp, dao,toTable,codeSetMap,addlist);
			}
			dao.addValueObject(addlist);
		}catch(Exception e){
			e.printStackTrace();
		}

	}
	
	/**
	 * 取得最大的主键值
	 * @param dao
	 * @return
	 */
	public int getTaxMaxId(ContentDAO dao,String toTable)
	{
		int tax_max_id=1;
		try
		{
			RowSet rs;
			String sql = "select max(tax_max_id) as tax_max_id from "+toTable+" ";
			rs = dao.search(sql);
			if(rs.next())
			{
				tax_max_id = rs.getInt("tax_max_id");
				if(tax_max_id==0)
					tax_max_id=1;
				else
					tax_max_id=tax_max_id+1;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return tax_max_id;
	}
	
	/**
	 * 获取ItemId
	 * @param itemdesc
	 * @param dao
	 * @return
	 */
	public String getItemId(String itemdesc,ContentDAO dao)
	{
		RowSet rs;
		StringBuffer sql = new StringBuffer();
		sql.append(" select itemid,fieldsetid from fielditem");
		sql.append(" where itemdesc like '"+itemdesc+"'");
		String retstr = "";
		try
		{
			rs = dao.search(sql.toString());
			if(rs.next())
			{
				retstr = rs.getString("fieldsetid");
				if("a01".equalsIgnoreCase(retstr))
				{
					retstr = rs.getString("itemid");
				}else{
					retstr = "";
				}
			}			
		}catch(Exception e){
			e.printStackTrace();
		}
		return retstr;
	}
	
	public HashMap<String, String> getCodeitemIdByCodeSetID(String codesetid)
	{
		HashMap<String, String> codeItemidMap = new HashMap<String, String>();
		try
		{
			StringBuffer sql = new StringBuffer("");
			if("UM".equalsIgnoreCase(codesetid)|| "@K".equalsIgnoreCase(codesetid)|| "UN".equalsIgnoreCase(codesetid))
			{
				sql.append("select codeitemid,codeitemdesc from organization where UPPER(codesetid)='"+codesetid.toUpperCase()+"'");
			}
			else
			{
				sql.append("select codeitemid,codeitemdesc from codeitem where UPPER(codesetid)='"+codesetid.toUpperCase()+"'");
			}
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql.toString());
			while(rs.next())
			{
				String codeitemid=rs.getString("codeitemid");
				String codeitemdesc=rs.getString("codeitemdesc");
				codeItemidMap.put(codeitemdesc.toUpperCase(),codeitemid);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return codeItemidMap;
	}
	
	/**
	 * 取得人员数据
	 * @param itemid
	 * @param oppositeItem
	 * @param allImportDataMap
	 * @param dbnamestr
	 * @param where
	 * @param dao
	 * @return
	 */
	public void setPersonRecordVo(String[] itemid, ArrayList<String> oppositeItem, LazyDynaBean dataBean, StringBuffer where, ContentDAO dao, String toTable, HashMap<String, String> codeSetMap, ArrayList<RecordVo> addlist)
	{
		try
		{
			RowSet rs;			
			String fieldstr = "";
			// 循环各个人员库
			StringBuffer dbnamestr= new StringBuffer();
			String sql = "select pre from dbname";
			rs = dao.search(sql);
			while(rs.next())
			{
				dbnamestr.append(","+rs.getString("pre"));
			}
			String[] dbPre = dbnamestr.substring(1).toString().split(",");	
			int waipin = 0;
			String temp = this.getDynaItemId();
			StringBuffer dynasb = new StringBuffer();
			if(!(temp==null || "".equals(temp)))
			{
				String[] dynatemp = temp.split(",");
				for(int i=0;i<dynatemp.length;i++)
				{
					FieldItem fi= DataDictionary.getFieldItem(dynatemp[i]);
					if(fi!=null && "a01".equalsIgnoreCase(fi.getFieldsetid()))
						dynasb.append(","+dynatemp[i]);
				}
			}		
			HashMap allreadyMap = new HashMap();
			if(dbPre!=null && dbPre.length>0)
			{
				// 循环各个人员库
				for(int j=0;j<dbPre.length;j++)
				{					
					StringBuffer sb = new StringBuffer();				
					fieldstr = "a0100,a0101,e0122,b0110"+dynasb.toString();				
					sb.append(" select "+fieldstr+","+itemid[0]);
					sb.append(" from "+dbPre[j]+"a01");
					sb.append(" where 1=1 "+where.toString());
					rs = dao.search(sb.toString());
					while(rs.next()){
						waipin++;
						RecordVo vo = new RecordVo(toTable);
						String[] field = fieldstr.split(",");
						vo.setInt("tax_max_id",tax_max_id);
						// 设置要各个人员字段
						for(int i=0;i<field.length;i++)
						{						
							FieldItem fi = DataDictionary.getFieldItem(field[i]);
							String itemtype = fi.getItemtype();
							if("A".equals(fi.getItemtype())){
								vo.setString(fi.getItemid(),rs.getString(field[i]));
							}else if("D".equals(fi.getItemtype())){
								if(rs.getDate(field[i])!=null)
					    			vo.setDate(fi.getItemid(),rs.getDate(field[i]));
							}else if("N".equals(fi.getItemtype())){
								if(fi.getDecimalwidth()==0){
									vo.setInt(fi.getItemid(),rs.getInt(field[i]));
								}else{
									vo.setDouble(fi.getItemid(),rs.getFloat(field[i]));
								}
							}
							allreadyMap.put(fi.getItemid().toUpperCase(), "1");
						}
						// 设置人员库标识字段
						vo.setString("nbase",dbPre[j]);
						allreadyMap.put("nbase".toUpperCase(), "1");
						vo.setString("a0100", rs.getString("a0100"));
						allreadyMap.put("a0100".toUpperCase(), "1");
						this.setExcelDataRecordVo(oppositeItem, vo, dataBean,allreadyMap,codeSetMap);
						/**使用标识默认为已使用*/
						vo.setString("flag", "1");
						addlist.add(vo);
						tax_max_id++;
					}
				}
				if(waipin == 0)
				{
					   
						RecordVo vo = new RecordVo(toTable);
						vo.setInt("tax_max_id",tax_max_id); 
						this.setOutOfA01RecordVo(itemid,oppositeItem,vo,dataBean,codeSetMap);
						vo.setString("flag","1");
						addlist.add(vo);
						tax_max_id++;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * 获得维护字段的Id
	 * @return
	 */
	public String getDynaItemId()
	{
		String dynaItemid = "";
		TaxMxBo bo = new TaxMxBo(conn, userView);
		Document doc = bo.getDoc();
		Element el = bo.getSingleNode(doc,"/param/items");
		if(el!=null)
		{
			dynaItemid = el.getText();
			if(!(dynaItemid==null || "".equals(dynaItemid)))
			{
				dynaItemid = ","+dynaItemid;
			}
		}
		return dynaItemid;
	}
	
	/**
	 * 取得要导入的个税数据
	 * @param oppositeItem
	 * @param vo
	 * @param allImportDataMap
	 * @return
	 */
	public RecordVo setExcelDataRecordVo(ArrayList<String> oppositeItem,RecordVo vo,LazyDynaBean dataBean,HashMap allreadyMap,HashMap<String, String> codeSetMap)
	{
		try
		{
			int length = oppositeItem.size();
			String[] excelItem = new String[length];
			String[] importItem =  new String[length];
			for(int i=0;i<length;i++)
			{
				String[] temp =  oppositeItem.get(i).split("=");
				excelItem[i] = temp[0];
				importItem[i] = temp[1].toLowerCase();
			}
			TaxMxBo taxBo = new TaxMxBo(this.conn, this.userView);
			//先查询出税率表固定字段，对于导入taxunit也是必须可以进行导入的
			HashMap<String, Field> fixedFieldMap = this.initFixedFieldMap();
			Field tax_field = taxBo.getTaxUnitField(true);
			if(tax_field != null) {
				fixedFieldMap.put(tax_field.getName().toUpperCase(), tax_field);
			}
			
			// 取得要更新字段的数据类型
			for(int i=0;i<length;i++)
			{
				/**已经赋值的指标，不要重新在赋值*/
				if(allreadyMap.get(importItem[i].toUpperCase())!=null)
					continue;
				FieldItem fi = DataDictionary.getFieldItem(importItem[i]);
				String decwidth ="";
				String itemtype ="";
				String codesetid="";
				if(fi!=null)
				{
					decwidth = fi.getDecimalwidth()+"";
					itemtype = fi.getItemtype();
					codesetid=fi.getCodesetid();
				}else{ 
					if(fixedFieldMap.get(importItem[i].toUpperCase())!=null)
					{
						Field field=(Field)fixedFieldMap.get(importItem[i].toUpperCase());
						decwidth = field.getDecimalDigits()+"";
						codesetid=field.getCodesetid();
						if(field.getDatatype()==DataType.DATE)
							itemtype = "D";
						else if(field.getDatatype()==DataType.STRING)
							itemtype = "A";
						else if(field.getDatatype()==DataType.INT||field.getDatatype()==DataType.FLOAT)
							itemtype = "N";
						else 
							itemtype = "A";
					}
				}
				String tempValue = (String)dataBean.get(excelItem[i]); 	
				// 根据数据类型，set RecordVo对象
				if("A".equals(itemtype) || "M".equals(itemtype)){
					if(!"0".equals(codesetid))
					{
						if("un".equalsIgnoreCase(codesetid)|| "um".equalsIgnoreCase(codesetid)|| "@K".equalsIgnoreCase(codesetid))
						{
							if(codeSetMap.get("ISORG")==null) {
								codeSetMap = this.initOrg(codeSetMap);
							}
							if(codeSetMap.get(tempValue)!=null)
							{
								vo.setString(importItem[i],(String)codeSetMap.get(tempValue));
							}
							else
							{
								vo.setString(importItem[i],tempValue);
							}
						}
						else if("@@".equalsIgnoreCase(codesetid))
						{
							HashMap<String, String> preMap = this.getPreMap();
							if(preMap.get(tempValue)!=null)
							{
								vo.setString(importItem[i],(String)preMap.get(tempValue));
							}
							else
							{
//								vo.setString(importItem[i],tempValue);
								throw GeneralExceptionHandler.Handle(new Exception("源数据("+importItem[i]+")中数据:"+tempValue+" 不存在!"));
							}
						}
						else if(codeSetMap.get(tempValue)!=null)
						{
							vo.setString(importItem[i],(String)codeSetMap.get(tempValue));
						}
						else
						{
							vo.setString(importItem[i],tempValue);
						}
					}
					else
		    			vo.setString(importItem[i],tempValue);
				}
				else if("N".equals(itemtype))
				{
					int temp_v_int = 0;
					double temp_v_dou = 0;
							
					if(!(tempValue==null || tempValue.trim().length()==0))
					{
							if("0".equals(decwidth))
							{
								if(tempValue.indexOf(".")!=-1) {
									temp_v_int = Integer.parseInt(tempValue.substring(0,tempValue.indexOf(".")));
							   		vo.setInt(importItem[i], temp_v_int);
								}else {
									temp_v_int = Integer.parseInt(tempValue);
									vo.setInt(importItem[i], temp_v_int);
								}
							}
							else {
								temp_v_dou = Double.parseDouble(tempValue);
								vo.setDouble(importItem[i], temp_v_dou);
							}
					}else
					{
						if("0".equals(decwidth)) {
							temp_v_int = 0;
							vo.setInt(importItem[i],0);
						}else {
							temp_v_dou = 0;
							vo.setDouble(importItem[i],0);
						}
					}
					
					String temp_val = getTemp_fielditem(excelItem[i]);
					//如果是子女教育等指标，需要赋值
					if(StringUtils.isNotBlank(temp_val)) {
						if("0".equals(decwidth)) {
							vo.setInt(temp_val, temp_v_int);
						}else {
							vo.setDouble(temp_val, temp_v_dou);
						}
					}
				}
				else if("D".equals(itemtype))
				{
					if(!(tempValue==null || "".equals(tempValue)))
					{
						if(isDataType(decwidth,itemtype,tempValue))
						{
							Calendar d=Calendar.getInstance();
							d.set(Calendar.YEAR,Integer.parseInt(tempValue.substring(0,4)));
							d.set(Calendar.MONTH,Integer.parseInt(tempValue.substring(5,7))-1);
							d.set(Calendar.DATE,Integer.parseInt(tempValue.substring(8)));
							vo.setDate(importItem[i],d.getTime());
						}
						else
							throw GeneralExceptionHandler.Handle(new Exception("源数据("+importItem[i]+")中数据:"+tempValue+" 不符合格式!"));
					}
					
				}
			
			}	

		}catch(Exception e){
			e.printStackTrace();
		}
		return vo;
	}
	
	private String getTemp_fielditem(String column_desc) {
		String temp_val = "";
		if(column_desc.equalsIgnoreCase(ResourceFactory.getProperty("label.gz.znjy"))) {
			temp_val = "znjy";
		}else if(column_desc.equalsIgnoreCase(ResourceFactory.getProperty("label.gz.jxjy"))) {
			temp_val = "jxjy";
		}else if(column_desc.equalsIgnoreCase(ResourceFactory.getProperty("label.gz.zfzj"))) {
			temp_val = "zfzj";
		}else if(column_desc.equalsIgnoreCase(ResourceFactory.getProperty("label.gz.zfdk"))) {
			temp_val = "zfdklx";
		}else if(column_desc.equalsIgnoreCase(ResourceFactory.getProperty("label.gz.sylr"))) {
			temp_val = "sylr";
		}
		
		return temp_val;
	}
	/**
	 * 取得要导入的个税数据
	 * @param oppositeItem
	 * @param vo
	 * @param allImportDataMap
	 * @return
	 */
	public RecordVo setOutOfA01RecordVo(String[] itemid,ArrayList<String> oppositeItem,RecordVo vo,LazyDynaBean dataBean,HashMap<String, String> codeSetMap)
	{

		try
		{
			HashMap<String, Field> fixedFieldMap = this.initFixedFieldMap();
			HashMap<String, String> preMap = this.getPreMap();
			int length = oppositeItem.size();
			String[] excelItem = new String[length];
			String[] importItem =  new String[length];
			for(int i=0;i<length;i++)
			{
				String[] temp =  oppositeItem.get(i).split("=");
				excelItem[i] = temp[0];               
				importItem[i] = temp[1].toLowerCase();
			}
			// 取得要更新字段的数据类型
			for(int i=0;i<length;i++)
			{
				FieldItem fi = DataDictionary.getFieldItem(importItem[i]);
				String decwidth ="";
				String itemtype ="";
				String codesetid="";
				if(fi!=null)
				{
					decwidth = fi.getDecimalwidth()+"";
					itemtype = fi.getItemtype();
					codesetid=fi.getCodesetid();
					if("nbase".equalsIgnoreCase(fi.getItemid()))
						codesetid="@@";
				}else
				{ 
					if(fixedFieldMap.get(importItem[i].toUpperCase())!=null)
					{
						Field field=(Field)fixedFieldMap.get(importItem[i].toUpperCase());
						decwidth = field.getDecimalDigits()+"";
						codesetid=field.getCodesetid();
						if(field.getDatatype()==DataType.DATE)
							itemtype = "D";
						else if(field.getDatatype()==DataType.STRING)
							itemtype = "A";
						else if(field.getDatatype()==DataType.INT||field.getDatatype()==DataType.FLOAT)
							itemtype = "N";
						else 
							itemtype = "A";
					}
				}
				// 在Map中得到该记录对应的 动态Bean
				String tempValue = (String)dataBean.get(excelItem[i]); 	
				// 根据数据类型，set RecordVo对象
				if("A".equals(itemtype)){
					if(!"0".equals(codesetid))
					{
						if("un".equalsIgnoreCase(codesetid)|| "um".equalsIgnoreCase(codesetid)|| "@K".equalsIgnoreCase(codesetid))
						{
							if(codeSetMap.get("ISORG")==null) {
								codeSetMap = this.initOrg(codeSetMap);
							}
							if(codeSetMap.get(tempValue)!=null)
							{
								vo.setString(importItem[i],(String)codeSetMap.get(tempValue));
							}
							else
							{
								vo.setString(importItem[i],tempValue);
							}
						}
						else if("@@".equalsIgnoreCase(codesetid))
						{
							if(preMap.get(tempValue)!=null)
							{
								vo.setString(importItem[i],(String)preMap.get(tempValue));
							}
							else
							{
//								vo.setString(importItem[i],tempValue);
								throw GeneralExceptionHandler.Handle(new Exception("源数据("+importItem[i]+")中数据:"+tempValue+" 不存在!"));
							}
						}
						else if(codeSetMap.get(tempValue)!=null)
						{
							vo.setString(importItem[i],(String)codeSetMap.get(tempValue));
						}
						else
						{
							vo.setString(importItem[i],tempValue);
						}
					}
					else
		    			vo.setString(importItem[i],tempValue);
				}
				else if("N".equals(itemtype))
				{
					if(!(tempValue==null || tempValue.trim().length()==0))
					{
						if("0".equals(decwidth))
						{  
							if(tempValue.indexOf(".")!=-1)
					   		   vo.setInt(importItem[i],Integer.parseInt(tempValue.substring(0,tempValue.indexOf("."))));
							else
							   vo.setInt(importItem[i],Integer.parseInt(tempValue));
						}
						else
							vo.setDouble(importItem[i],Double.parseDouble(tempValue));
					}else
					{
						if("0".equals(decwidth))
							vo.setInt(importItem[i],0);
						else
							vo.setDouble(importItem[i],0);
					}
					
				}
				else if("D".equals(itemtype))
				{
					if(!(tempValue==null || "".equals(tempValue)))
					{
						if(isDataType(decwidth,itemtype,tempValue))
						{
							Calendar d=Calendar.getInstance();
							d.set(Calendar.YEAR,Integer.parseInt(tempValue.substring(0,4)));
							d.set(Calendar.MONTH,Integer.parseInt(tempValue.substring(5,7))-1);
							d.set(Calendar.DATE,Integer.parseInt(tempValue.substring(8)));
							vo.setDate(importItem[i],d.getTime());
						}
						else
							throw GeneralExceptionHandler.Handle(new Exception("源数据("+importItem[i]+")中数据:"+tempValue+" 不符合格式!"));
					}
					
				}
				
			}	
			
			for(int i=0;i<itemid.length;i++)
			{
				itemid[i]=itemid[i].toLowerCase();
				FieldItem fi = DataDictionary.getFieldItem(itemid[i]);
				String decwidth ="";
				String itemtype ="";
				String codesetid="";
				if(fi!=null)
				{
					decwidth = fi.getDecimalwidth()+"";
					itemtype = fi.getItemtype();
					codesetid=fi.getCodesetid();
				}else
				{ 
					if(fixedFieldMap.get(itemid[i].toUpperCase())!=null)
					{
						Field field=(Field)fixedFieldMap.get(itemid[i].toUpperCase());
						decwidth = field.getDecimalDigits()+"";
						codesetid=field.getCodesetid();
						if(field.getDatatype()==DataType.DATE)
							itemtype = "D";
						else if(field.getDatatype()==DataType.STRING)
							itemtype = "A";
						else if(field.getDatatype()==DataType.INT||field.getDatatype()==DataType.FLOAT)
							itemtype = "N";
						else 
							itemtype = "A";
					}
				}
				// 在Map中得到该记录对应的 动态Bean
				String itemvalue = DataDictionary.getFieldItem(itemid[i]).getItemdesc();
				String tempValue = (String)dataBean.get(itemvalue); 	
				// 根据数据类型，set RecordVo对象
				if("A".equals(itemtype)){
					if(!"0".equals(codesetid))
					{
						if("un".equalsIgnoreCase(codesetid)|| "um".equalsIgnoreCase(codesetid)|| "@K".equalsIgnoreCase(codesetid))
						{
							if(codeSetMap.get("ISORG")==null)
								codeSetMap = this.initOrg(codeSetMap);
							if(codeSetMap.get(tempValue)!=null)
							{
								vo.setString(itemid[i],(String)codeSetMap.get(tempValue));
							}
							else
							{
								vo.setString(itemid[i],tempValue);
							}
						}
						else if("@@".equalsIgnoreCase(codesetid))
						{
							if(preMap.get(tempValue)!=null)
							{
								vo.setString(itemid[i],(String)preMap.get(tempValue));
							}
							else
							{
//								vo.setString(itemid[i],tempValue);
								throw GeneralExceptionHandler.Handle(new Exception("源数据("+importItem[i]+")中数据:"+tempValue+" 不存在!"));
							}
						}
						else if(codeSetMap.get(tempValue)!=null)
						{
							vo.setString(itemid[i],(String)codeSetMap.get(tempValue));
						}
						else
						{
							vo.setString(itemid[i],tempValue);
						}
					}
					else
		    			vo.setString(itemid[i],tempValue);
				}
				else if("N".equals(itemtype))
				{
					if(!(tempValue==null || tempValue.trim().length()==0))
					{
						if("0".equals(decwidth))
						{
							if(tempValue.indexOf(".")!=-1)
						   		   vo.setInt(importItem[i],Integer.parseInt(tempValue.substring(0,tempValue.indexOf("."))));
								else
								   vo.setInt(importItem[i],Integer.parseInt(tempValue));
						}
						else
							vo.setDouble(itemid[i],Double.parseDouble(tempValue));
					}else
					{
						if("0".equals(decwidth))
							vo.setInt(itemid[i],0);
						else
							vo.setDouble(itemid[i],0);
					}
					
				}
				else if("D".equals(itemtype))
				{
					if(!(tempValue==null || "".equals(tempValue)))
					{
						if(isDataType(decwidth,itemtype,tempValue))
						{
							Calendar d=Calendar.getInstance();
							d.set(Calendar.YEAR,Integer.parseInt(tempValue.substring(0,4)));
							d.set(Calendar.MONTH,Integer.parseInt(tempValue.substring(5,7))-1);
							d.set(Calendar.DATE,Integer.parseInt(tempValue.substring(8)));
							vo.setDate(itemid[i],d.getTime());
						}
						else
							throw GeneralExceptionHandler.Handle(new Exception("源数据("+itemid[i]+")中数据:"+tempValue+" 不符合格式!"));
					}
					
				}
			
			}

		}catch(Exception e){
			e.printStackTrace();
		}
		return vo;
	
	}
	/**
	 * 固定列指标转换为map方便使用
	 * @return
	 */
	public HashMap<String, Field> initFixedFieldMap()
	{
		HashMap<String, Field> fixedFieldMap = null;
		try
		{
			TaxMxBo taxBo = new TaxMxBo(this.conn, this.userView);
			fixedFieldMap = new HashMap<String, Field>();
			ArrayList<Field> itemList = taxBo.searchCommonItemList();
			for(int t=0;t<itemList.size();t++)
			{
				Field field = (Field)itemList.get(t);
				fixedFieldMap.put(field.getName().toUpperCase(), field);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return fixedFieldMap;
	}
	
	public HashMap<String, String> initOrg(HashMap<String, String> codeSetMap)
	{
		try
		{
			if(codeSetMap==null)
				codeSetMap=new HashMap();
			String sql = "select * from organization";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs= dao.search(sql);
			while(rs.next())
			{
				codeSetMap.put(rs.getString("codeitemdesc"),rs.getString("codeitemid"));
			}
			codeSetMap.put("ISORG", "1");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return codeSetMap;
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
			flag=value.matches("/^-?\\d+(\\.\\d)?/");
		}
		else if("D".equals(itemtype))
		{
			flag=value.matches("[0-9]{4}[#-.][0-9]{2}[#-.][0-9]{2}");
		}
		return flag;
	}
	
	public HashMap<String,String> getPreMap(){
		RowSet rs = null;
		HashMap<String,String> preMap = null;
		try {
			String sql="select pre,dbname from dbname";
	 	    ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql);
			preMap = new HashMap<String,String>();
			while(rs.next())
			{
				preMap.put(rs.getString("dbname"),rs.getString("pre")+rs.getString("dbname"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return preMap;
	}

}
