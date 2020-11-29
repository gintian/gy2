package com.hjsj.hrms.businessobject.performance.data_collect;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.analyse.YearMonthCount;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
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
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
/**
 * 
* 
* 类名称：DataCollectBo   
* 类描述：   
* 创建人：zhaoxg   
* 创建时间：Aug 21, 2013 11:54:39 AM   
* 修改人：zhaoxg   
* 修改时间：Aug 21, 2013 11:54:39 AM   
* 修改备注：   数据采集业务类
* @version    
*
 */
public class DataCollectBo {

	private Connection conn = null;
	private ArrayList fieldlist = new ArrayList();
	private UserView view;
	private HSSFWorkbook wb = null;
	private HSSFSheet sheet = null;
	private HSSFCellStyle style = null;
	private HSSFCellStyle style_l = null;
	private HSSFCellStyle style_r = null;
	private HSSFCellStyle style_title = null;
	private HSSFCellStyle style_thead = null;
	HSSFDataFormat dataformat = null;
	private HSSFCellStyle style_r_1 = null;
	private HSSFCellStyle style_r_2 = null;
	private HSSFCellStyle style_r_3 = null;
	private HSSFCellStyle style_r_4 = null;
	private String param;
	private Document doc;
	private String xml;
	short rowNum = 1;
	/**薪资项目和临时变量列表*/
	private ArrayList fldvarlist=new ArrayList();
	public DataCollectBo(Connection conn,UserView view) {
		this.conn=conn;
		this.view=view;
	}
	/**
	 * 初始化不同的根接点xml的内容
	 * 
	 * @param conn
	 * @param constant
	 *            //constant字段内容
	 * @param param
	 *            //接点路径 例如：voucher
	 */
	public DataCollectBo(Connection conn,String param) {
		this.conn = conn;
		initXML(param);
		try {
			doc = PubFunc.generateDom(xml);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 取字段str_value的内容
	 * 
	 * @param constant
	 *            //constant字段内容
	 * @param param
	 *            //接点路径 例如：Params
	 */
	private void initXML(String param) {
		StringBuffer temp_xml = new StringBuffer();
		param = param != null && param.trim().length() > 0 ? param : "Params";
		temp_xml.append("<?xml version='1.0' encoding='GB2312' ?>");
		temp_xml.append("<" + param + ">");
		temp_xml.append("</" + param + ">");
		try {
			ContentDAO dao = new ContentDAO(conn);
			RowSet rs = dao.search("select * from constant where Constant = 'DATA_COLLECT_SCOPE'");
			if (rs.next())
				xml = rs.getString("str_value");
			if (xml == null || "".equals(xml)) {
				xml = temp_xml.toString();
			}
			doc = PubFunc.generateDom(xml);
			rs.close();
		} catch (Exception ex) {
			xml = temp_xml.toString();
		}
	}
	/**
	 * 获取xml中的属性值
	 * 
	 * @return
	 */
	public String getXmlValue1(String _value,String set_id) {
		String value = "";
		try {
			String str_path="/Params";
			XPath xpath=XPath.newInstance(str_path);
			List childlist=xpath.selectNodes(doc);
			Element ele = null;
			if(childlist.size()!=0){
				for(int i = 0;i<childlist.size();i++){
					List child=null;
					ele=(Element)childlist.get(i);
					if (ele != null) {
						child = ele.getChildren("scope");
						if (child != null) {
							for(int j=0;j<child.size();j++){
								Element children = (Element) child.get(j);
								if(children.getAttributeValue("set_id").equals(set_id)){
									value = children.getAttributeValue(_value);
								}			
							}
	
						}
					}
				}
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		}
		return value;
	}
	/**
	 * 读取参数的值
	 * @param param_type
	 * @return
	 */
	public String getValue(String set_id)
	{
		String value="";
		  try
		  {
			String str_path="/Params";
			XPath xpath=XPath.newInstance(str_path);
			List childlist=xpath.selectNodes(doc);
			Element ele=null;
			if(childlist.size()!=0)
			{
				for(int i = 0;i<childlist.size();i++){
					List child=null;
					ele=(Element)childlist.get(i);
					if (ele != null) {
						child = ele.getChildren("scope");
						if (child != null) {
							for(int j=0;j<child.size();j++){
								Element children = (Element) child.get(j);
								if(children.getAttributeValue("set_id").equals(set_id)){
									value = children.getText();
								}			
							}
	
						}
					}
				}
			}
			if(value!=null&& "#".equals(value))
				value="";
		  }
		  catch(Exception ex)
		  {
			  ex.printStackTrace();
		  }		
		return value;		
	}
	/**
	 * 设置对应节点属性的值
	 * 
	 * @param str_path
	 *            保存路径 例如：/voucher/items
	 * @param value
	 *            //值
	 * @return//此处由于是用set_id判断是否存在对应子集信息，所以一定要先存子集编号，否则产生的xml格式不对
	 */
	public void setAttributeValue( String attributeName,
			String attributeValue,String set_id) {
		try {
			String str_path="/Params";
			XPath xpath=XPath.newInstance(str_path);
			List childlist=xpath.selectNodes(doc);
			Element element=null;
			boolean flag = true;
			if(childlist.size()!=0)
			{
				for(int i = 0;i<childlist.size();i++){
					List child=null;
					element=(Element)childlist.get(i);
					if (element != null) {
						child = element.getChildren("scope");
						if (child != null) {
							for(int j=0;j<child.size();j++){
								Element children = (Element) child.get(j);
								if(children.getAttributeValue("set_id").equals(set_id)){
									children.setAttribute(attributeName, attributeValue);
									flag=false;
								}	
							}		
						}
					}
				}
				if(flag){
					Element bbElement = (Element) xpath.selectSingleNode(doc);
					element = null;
					element = new Element("scope");
					element.setAttribute(attributeName,attributeValue);
					bbElement.addContent(element);									
				}
			}		
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 设置对应节点参数的值
	 * 
	 * @param str_path
	 *            保存路径 例如：/voucher/items
	 * @param value
	 *            //值
	 * @return
	 */
	public void setTextValue(String value,String set_id) {
		try {
			String str_path="/Params";
			XPath xpath=XPath.newInstance(str_path);
			List childlist=xpath.selectNodes(doc);
			Element element=null;
			boolean flag = true;
			if(childlist.size()!=0)
			{
				for(int i = 0;i<childlist.size();i++){
					List child=null;
					element=(Element)childlist.get(i);
					if (element != null) {
						child = element.getChildren("scope");
						if (child != null) {
							for(int j=0;j<child.size();j++){
								Element children = (Element) child.get(j);
								if(children.getAttributeValue("set_id").equals(set_id)){
									children.setText(value);
									flag=false;
								}	
							}		
						}
					}
				}
				if(flag){
					Element bbElement = (Element) xpath.selectSingleNode(doc);
					element = null;
					element = new Element("scope");
					element.setText(value);
					bbElement.addContent(element);									
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 返回要保存的xml内容
	 * 
	 */
	public String saveStrValue() {
		StringBuffer buf = new StringBuffer();
		XMLOutputter outputter = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);
		buf.append(outputter.outputString(doc));
		return buf.toString();
	}
	public ArrayList getDbList(String pre){
		ArrayList list = new ArrayList();
		  try
		  {	
				StringBuffer sql=new StringBuffer();
				String[] _pre = pre.split(",");
				StringBuffer temp = new StringBuffer();
				for(int i=0;i<_pre.length;i++){
					temp.append("'"+_pre[i]+"'");
					temp.append(",");
				}
				
				sql.append("select dbid,dbname,pre from dbname where pre in("+temp.toString().substring(0, temp.toString().length()-1)+")");
				ContentDAO dao=new ContentDAO(this.conn);
				RowSet rs=dao.search(sql.toString());
				while(rs.next()){
					list.add(new CommonData(rs.getString("pre"),rs.getString("dbname")));
				}
		  }
		  catch(Exception ex)
		  {
			  ex.printStackTrace();
		  }
		return list;
	}
	  /**
	   * 取得审批状态的过滤列表
	   * @return
	   */
	  public ArrayList getSpTypeList()
	  {
		  ArrayList list = new ArrayList();
		  list.add(new CommonData("0",ResourceFactory.getProperty("label.all")));
		  
		  list.add(new CommonData("01",ResourceFactory.getProperty("label.hiremanage.status1")));
		  list.add(new CommonData("02",ResourceFactory.getProperty("workdiary.message.apped")));
		  list.add(new CommonData("03",ResourceFactory.getProperty("label.hiremanage.status3")));
		  list.add(new CommonData("07",ResourceFactory.getProperty("button.reject")));
		  
		  return list;
	  }
	  /**
	   * 取得月份过滤列表
	   * @param ctrl_peroid
	   * @return
	   */
	  public ArrayList getFilterList()
	  {
		  ArrayList list = new ArrayList();
		  list.add(new CommonData("0",ResourceFactory.getProperty("label.all")));

			  list.add(new CommonData("1",ResourceFactory.getProperty("date.month.january")));
			  list.add(new CommonData("2",ResourceFactory.getProperty("date.month.february")));
			  list.add(new CommonData("3",ResourceFactory.getProperty("date.month.march")));
			  list.add(new CommonData("4",ResourceFactory.getProperty("date.month.april")));
			  list.add(new CommonData("5",ResourceFactory.getProperty("date.month.may")));
			  list.add(new CommonData("6",ResourceFactory.getProperty("date.month.june")));
			  list.add(new CommonData("7",ResourceFactory.getProperty("date.month.july")));
			  list.add(new CommonData("8",ResourceFactory.getProperty("date.month.auguest")));
			  list.add(new CommonData("9",ResourceFactory.getProperty("date.month.september")));
			  list.add(new CommonData("10",ResourceFactory.getProperty("date.month.october")));
			  list.add(new CommonData("11",ResourceFactory.getProperty("date.month.november")));
			  list.add(new CommonData("12",ResourceFactory.getProperty("date.month.december")));
	
		  return list;
	  }
		/**
		 * 取表头
		 * @return
		 */
		public ArrayList getfieldlist(String fieldsetid,String state_id) 
		{
			ArrayList list=new ArrayList();
			StringBuffer strread=new StringBuffer();
			/**只读字段*/
			strread.append("SP_FLAG,B0110,E0122,A0101,A00Z2,A00Z3,");
			StringBuffer format=new StringBuffer();	
			format.append("###################");		
			StringBuffer buf=new StringBuffer();
			buf.append("select * from fielditem where fieldsetid='"+fieldsetid+"' and useflag='1'");
			Field field=null;
			RowSet rset=null;
			try
			{
				ContentDAO dao=new ContentDAO(this.conn);
				rset=dao.search(buf.toString());
								
				field=new Field("B0110","单位");
				field.setDatatype(DataType.STRING);
				field.setLength(30);
				field.setCodesetid("UN");
				field.setReadonly(true);		
				field.setVisible(true);
				list.add(field);
				
				field=new Field("E0122","部门");
				field.setLength(50);
				field.setCodesetid("UM");
				field.setDatatype(DataType.STRING);
				field.setReadonly(true);
				field.setVisible(true);
				list.add(field);
				
				field=new Field("E01A1","岗位");
				field.setLength(50);
				field.setCodesetid("@K");
				field.setDatatype(DataType.STRING);
				field.setReadonly(true);
				field.setVisible(true);
				list.add(field);
				
				field=new Field("A0101","姓名");
				field.setLength(50);
				field.setDatatype(DataType.STRING);
				field.setReadonly(true);
				field.setVisible(true);
				list.add(field);
				
				while(rset.next())
				{
					String itemid=rset.getString("itemid");
					String state=this.view.analyseFieldPriv(itemid);
					if(!"2".equals(state)&&!this.view.isSuper_admin()){
						continue;
					}
					if(state_id.equals(itemid)){

						continue;
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
						field.setVisible(true);
					}
					else if("M".equals(type))
					{
						field.setDatatype(DataType.CLOB);
						field.setAlign("left");					
					}
					else if("N".equals(type))
					{

						field.setLength(rset.getInt("itemlength"));
						int ndec=rset.getInt("decimalwidth");
						field.setDecimalDigits(ndec);					
						if(ndec>0)
						{
							field.setDatatype(DataType.FLOAT);						
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
						int length = rset.getInt("itemlength");
						if(itemid.equalsIgnoreCase(fieldsetid+"z0")){
							field.setFormat("yyyy.MM");
						}else if(length==18){
							field.setFormat("yyyy.MM.dd HH.mm.ss");
						}else if(length==4){
							field.setFormat("yyyy");
						}else if(length==10){
							field.setFormat("yyyy.MM.dd");
						}else if(length==7){
							field.setFormat("yyyy.MM");
						}
						
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
					if(itemid.equalsIgnoreCase(fieldsetid+"z1")||itemid.equalsIgnoreCase(fieldsetid+"z0"))
					{
						field.setReadonly(true);
					}else if("1".equalsIgnoreCase(this.view.analyseFieldPriv(itemid)))
					{
						field.setReadonly(true); //读权限
					}
					field.setSortable(true);
					list.add(field);
				}
				field=new Field("zt","状态");
				field.setLength(50);
				field.setCodesetid("23");
				field.setDatatype(DataType.STRING);
				field.setReadonly(true);
				field.setVisible(true);
				list.add(field);
				
				field=new Field("i9999","主键");
				field.setLength(50);
				field.setDatatype(DataType.INT);							
				field.setFormat("####");	
				field.setReadonly(true);
				field.setVisible(false);
				list.add(field);
				
				field=new Field("a0100","主键");
				field.setLength(50);
				field.setDatatype(DataType.STRING);
				field.setReadonly(true);
				field.setVisible(false);
				list.add(field);
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
		 * 导出excel的表头
		 * @param fieldsetid
		 * @param state_id
		 * @return
		 */
		public ArrayList getExcelList(String fieldsetid,String state_id){
			ArrayList list = new ArrayList();
			try{
				LazyDynaBean abean = null;
				abean = new LazyDynaBean();
				abean.set("id", "1");
				abean.set("code", "pre");
				abean.set("name", "主键");
				abean.set("type", "A");
				abean.set("codesetid", "0");
				list.add(abean);
				
				abean = new LazyDynaBean();
				abean.set("id", "2");
				abean.set("code", "B0110");
				abean.set("name", "单位");
				abean.set("type", "A");
				abean.set("codesetid", "UN");
				list.add(abean);

				abean = new LazyDynaBean();
				abean.set("id", "3");
				abean.set("code", "E0122");
				abean.set("name", "部门");
				abean.set("type", "A");
				abean.set("codesetid", "UM");
				list.add(abean);

				abean = new LazyDynaBean();
				abean.set("id", "4");
				abean.set("code", "E01A1");
				abean.set("name", "岗位");
				abean.set("type", "A");
				abean.set("codesetid", "@K");
				list.add(abean);
				
				abean = new LazyDynaBean();
				abean.set("id", "5");
				abean.set("code", "A0101");
				abean.set("name", "姓名");
				abean.set("type", "A");
				abean.set("codesetid", "0");
				list.add(abean);
				String sql = "select * from fielditem where fieldsetid='"+fieldsetid+"'  and useflag='1'";
				ContentDAO dao=new ContentDAO(this.conn);
				RowSet rs=dao.search(sql);
				int n=5;
				while(rs.next()){
					if(!rs.getString("itemid").equals(state_id)){
						abean = new LazyDynaBean();
						abean.set("id", ++n+"");
						abean.set("code", rs.getString("itemid"));
						abean.set("name", rs.getString("itemdesc"));
						abean.set("type", rs.getString("itemtype"));
						abean.set("decwidth", rs.getString("decimalwidth"));
						abean.set("length", rs.getInt("itemlength")+"");
						abean.set("codesetid", rs.getString("codesetid"));
						list.add(abean);
					}
				}
				abean = new LazyDynaBean();
				abean.set("id", ++n+"");
				abean.set("code", state_id);
				abean.set("name", "状态");
				abean.set("type", "A");
				abean.set("codesetid", "23");
				list.add(abean);
				
			}catch(Exception ex)
			{
				ex.printStackTrace();
			}
			return list;
		}
		/**
		 * 获取excel数据区
		 * @param ItemList
		 * @param sql
		 * @param dao
		 * @return
		 */
		public ArrayList getDataList(ArrayList ItemList, String sql,
				ContentDAO dao) {
			ArrayList list = new ArrayList();
			try {
				RowSet rs = dao.search(sql);
				LazyDynaBean bean = new LazyDynaBean();
				Hashtable abean = null;
				String id = "";
				String code = "";
				String name = "";
				String type = "";
				String value = "";
				String codesetid = "";
				SimpleDateFormat dateformat=null;
				while (rs.next()) {
					abean = new Hashtable();
					for (int i = 0; i < ItemList.size(); i++) {
						bean = (LazyDynaBean) ItemList.get(i);
						id = (String) bean.get("id");
						code = (String) bean.get("code");
						name = (String) bean.get("name");
						type = (String) bean.get("type");
						codesetid = (String) bean.get("codesetid");
						if ("A".equals(type)) {
							value = rs.getString(code);
						} else if ("N".equals(type)) {
							value = rs.getString(code);
						}else if("D".equals(type)){
							String length = (String) bean.get("length");
							if(code.toLowerCase().indexOf("z0")!=-1){
								dateformat=new SimpleDateFormat("yyyy.MM");
							}else if("18".equals(length)){
								dateformat=new SimpleDateFormat("yyyy.MM.dd HH.mm.ss");
							}else if("4".equals(length)){
								dateformat=new SimpleDateFormat("yyyy");
							}else if("10".equals(length)){
								dateformat=new SimpleDateFormat("yyyy.MM.dd");
							}else if("7".equals(length)){
								dateformat=new SimpleDateFormat("yyyy.MM");
							}else{
								dateformat=new SimpleDateFormat("yyyy.MM");
							}
							
							if(rs.getDate(code)!=null&&!"".equals(rs.getDate(code))){
								value = dateformat.format(rs.getDate(code));
							}else{
								value = "";
							}
							
						}else{
							value = rs.getString(code);
						}
						if (value != null) {
							abean.put(code, value);
							abean.put("type", type);
							abean.put("codesetid", codesetid);
							abean.put("id", id);
						} else {
							abean.put(code, "");
							abean.put("type", type);
							abean.put("id", id);
						}

					}
					list.add(abean);
				}
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return list;

		}
		/**
		 * 导出完整的excel
		 * @param fileName
		 * @param dataList
		 * @param List
		 * @param url
		 */
		public void exportData(String fileName, ArrayList dataList,
				ArrayList List, String url,String state_id) {
			this.wb = new HSSFWorkbook();
			this.style = getStyle("c", wb);
			this.style_l = getStyle("l", wb);
			this.style_r = getStyle("r", wb);
			this.style_title = getStyle("title", wb);
			this.style_r = getStyle("r", wb);
			int page = 1;
			int nrows = 20000;
			String _id = "";
			Hashtable dataBean = null;
			LazyDynaBean bean = null;
			ArrayList codeCols = new ArrayList();
			FileOutputStream fileOut= null;
			try {
				for (int i = 0; i < dataList.size(); i++) {
					if (i == 0 || (i != 1 && i % nrows == 1)) {
						this.sheet = wb.createSheet(page + "");
						page++;
						if (i == 0) {
							this.rowNum = 0;
						} else {
							this.rowNum = 0;
						}
						for (int index = 0; index < List.size(); index++) {
							LazyDynaBean headbean = (LazyDynaBean) List.get(index);
							String itemdesc = (String) headbean.get("name");
							executeCell2(Short.parseShort(String.valueOf(index)),
									itemdesc, "title");
						}
						this.rowNum++;
					}
					dataBean = (Hashtable) dataList.get(i);
					
					for (int j = 0; j < List.size(); j++) {
						bean = (LazyDynaBean) List.get(j);
						String code = (String) bean.get("code");
						String type = (String) bean.get("type");
						String codesetid = (String) bean.get("codesetid");
						String id = (String) bean.get("id");
						boolean flag = false;
						if(code.toLowerCase().indexOf("z0")!=-1||code.toLowerCase().indexOf("z1")!=-1|| "a0101".equals(code.toLowerCase())|| "pre".equals(code.toLowerCase())){
							flag = true;
						}
						_id=id;
						if("0".equals(codesetid)){
							if ("N".equals(type)) {
								String deciwidth = (String) bean.get("decwidth");
								if (deciwidth == null || "".equals(deciwidth.trim()))
									deciwidth = "0";
								HSSFCellStyle style = null;
								style = getStyle("r", wb);
								style.setLocked(flag);
								executeCellN(Short.parseShort(String.valueOf(j)),
										(String) dataBean.get(code), style,
										Integer.parseInt(deciwidth));
							} else {
								executeCell2(Short.parseShort(String.valueOf(j)),
										(String) dataBean.get(code), "L", "A",flag);
							}
						}else{
							executeCell3(Short.parseShort(String.valueOf(j)),
									(String) dataBean.get(code), codesetid, id);
							if(i==0&&!"UN".equals(codesetid)&&!"UM".equals(codesetid)&&!"@K".equals(codesetid)&&!"23".equals(codesetid)){
								codeCols.add(codesetid+":"+(Integer.parseInt(_id)-1)+"");
							}						
						}
					}
					this.rowNum++;

				}
				int index = 0;
				String[] lettersUpper =
				{ "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
				
				int div = 0;
				int mod = 0;
				ContentDAO dao=new ContentDAO(this.conn);
				
//				codeCols.add("UN:1");
//				codeCols.add("UM:2");
//				codeCols.add("@K:3");
//				codeCols.add("23:"+(Integer.parseInt(_id)-1)+"");
				for (int n = 0; n < codeCols.size(); n++)
				{
					String codeCol = (String) codeCols.get(n);
					String[] temp = codeCol.split(":");
					 String codesetid = temp[0];
					int codeCol1 = Integer.valueOf(temp[1]).intValue();
					StringBuffer codeBuf = new StringBuffer();
					if (!"UM".equals(codesetid) && !"UN".equals(codesetid) && !"@K".equalsIgnoreCase(codesetid))
					{
						if("23".equalsIgnoreCase(codesetid)){
							codeBuf.append("select codesetid,codeitemid,codeitemdesc from codeitem where codesetid='23' and codeitemid in ('01','02','03','07')");
						}else{
							codeBuf.append("select codesetid,codeitemid,codeitemdesc from codeitem where codesetid='" + codesetid + "'");
						}
						
					} else
					{
						if (!"UN".equals(codesetid))
						{
							if("UM".equalsIgnoreCase(codesetid))
								codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization where ( codesetid='UM' OR codesetid='UN' ) "
										+" order by codeitemid");
							else
								codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization where codesetid='" + codesetid
									+ "' and  codeitemid not in (select parentid from organization where codesetid='" + codesetid + "')");
						}
						else if ("UN".equals(codesetid))
						{
							codeBuf.append("select count(*) from organization where codesetid='UN'");
							RowSet rs = dao.search(codeBuf.toString());
							if (rs.next())
								if (rs.getInt(1) == 1)
								{
									codeBuf.setLength(0);
									codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization where codesetid='UN'");
								} else if (rs.getInt(1) > 1)
								{
									codeBuf.setLength(0);
									codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization where codesetid='" + codesetid
											+ "' and  codeitemid not in (select parentid from organization where codesetid='" + codesetid + "')");
									codeBuf.append(" union all ");
									codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization where codesetid='UN' and  codeitemid=parentid and childid in (select codeitemid from organization where codesetid!='UN')");
								}
						}
					}

					RowSet rs = dao.search(codeBuf.toString());

					int m = 0;
					while (rs.next())
					{
						row = sheet.getRow(m + 0);
						if (row == null)
							row = sheet.createRow(m + 0);
						cell = row.createCell((short) (208 + index));
						if (!"UM".equals(codesetid) && !"UN".equals(codesetid) && !"@K".equalsIgnoreCase(codesetid))
							cell.setCellValue(new HSSFRichTextString(rs.getString("codeitemid")+":"+rs.getString("codeitemdesc")));
						else
							cell.setCellValue(new HSSFRichTextString(rs.getString("codeitemdesc")));
						m++;
					}
					if(m==0)
						m=2;
					sheet.setColumnWidth((short) (208 + index), (short) 0);
					div = index/26;
					mod = index%26;
					String strFormula = "$" +lettersUpper[7+div]+ lettersUpper[mod] + "$1:$"+lettersUpper[7+div]+  lettersUpper[mod] + "$" + Integer.toString(m); // 表示BA列1-m行作为下拉列表来源数据
				 
					CellRangeAddressList addressList = new CellRangeAddressList(1, rowNum-1, codeCol1, codeCol1);
					DVConstraint dvConstraint = DVConstraint.createFormulaListConstraint(strFormula);
					HSSFDataValidation dataValidation = new HSSFDataValidation(addressList, dvConstraint);
					dataValidation.setSuppressDropDownArrow(false);
					sheet.addValidationData(dataValidation);

					index++;
				}
				sheet.setColumnWidth(0, 0);
				sheet.protectSheet("");  //全文写保护  密码为空  
				fileOut = new FileOutputStream(url);
				this.wb.write(fileOut);
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				PubFunc.closeResource(fileOut);
				PubFunc.closeResource(this.wb);
			}

		}

		/**
		 * excel表中每个格数据的布局
		 * 
		 * @param align
		 * @param wb
		 * @return
		 */
		public HSSFCellStyle getStyle(String align, HSSFWorkbook wb) {
			HSSFCellStyle a_style = wb.createCellStyle();
			a_style.setBorderBottom(BorderStyle.THIN);
			a_style.setBottomBorderColor(HSSFColor.BLACK.index);
			a_style.setBorderLeft(BorderStyle.THIN);
			a_style.setLeftBorderColor(HSSFColor.BLACK.index);
			a_style.setBorderRight(BorderStyle.THIN);
			a_style.setRightBorderColor(HSSFColor.BLACK.index);
			a_style.setBorderTop(BorderStyle.THIN);
			a_style.setTopBorderColor(HSSFColor.BLACK.index);
			a_style.setVerticalAlignment(VerticalAlignment.CENTER);

			if ("c".equals(align))
				a_style.setAlignment(HorizontalAlignment.CENTER);
			else if ("l".equals(align))
				a_style.setAlignment(HorizontalAlignment.LEFT);
			else if ("r".equals(align))
				a_style.setAlignment(HorizontalAlignment.RIGHT);
			else if ("title".equals(align)) {
				a_style.setAlignment(HorizontalAlignment.CENTER);
				a_style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				a_style.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
			}
			return a_style;
		}

		public void executeCell2(short columnIndex, String value, String style) {
			HSSFRow row = this.sheet.getRow(rowNum);
			if (row == null)
				row = this.sheet.createRow(rowNum);
			HSSFCell cell = row.getCell(columnIndex);
			if (cell == null)
				cell = row.createCell(columnIndex);
			if ("c".equalsIgnoreCase(style))
				cell.setCellStyle(this.style);
			else if ("l".equalsIgnoreCase(style))
				cell.setCellStyle(this.style_l);
			else if ("R".equalsIgnoreCase(style)) {
				cell.setCellStyle(this.style_r);
			} else if ("title".equalsIgnoreCase(style)) {
				cell.setCellStyle(this.style_title);
			}
			cell.setCellValue(value);
		}

		HSSFRichTextString richTextString = null;
		HSSFRow row = null;
		HSSFCell cell = null;

		public void executeCellN(short columnIndex, String value,
				HSSFCellStyle style, int scale) {
			row = this.sheet.getRow(rowNum);
			if (row == null)
				row = this.sheet.createRow(rowNum);
			cell = row.createCell(columnIndex);
			cell.setCellStyle(style);
			if (value == null || "".equals(value.trim()))
				value = "0";
			cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
			BigDecimal bd = new BigDecimal(value);
			BigDecimal bd2 = bd.setScale(scale, bd.ROUND_HALF_UP);
			cell.setCellValue(bd2.doubleValue());

		}

		public void executeCell2(short columnIndex, String value, String style,
				String type,boolean flag) {
			row = this.sheet.getRow(rowNum);
			if (row == null)
				row = this.sheet.createRow(rowNum);
			cell = row.createCell(columnIndex);
			if ("c".equalsIgnoreCase(style)){
				this.style.setLocked(flag);		
				cell.setCellStyle(this.style);
			}
			else if ("l".equalsIgnoreCase(style)){
				HSSFCellStyle sl = null;
				sl=getStyle("l", wb);
				sl.setLocked(flag);			
				cell.setCellStyle(sl);
			}
			else if ("R".equalsIgnoreCase(style)){
				this.style_r.setLocked(flag);			
				cell.setCellStyle(this.style_r);
			}
			if (value != null && value.trim().length() > 0
					&& "N".equalsIgnoreCase(type)) {
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellValue(Double.parseDouble(value));
			} else {
				if (value == null)
					value = "";
				richTextString = new HSSFRichTextString(value);
				cell.setCellValue(richTextString);
			}

		}
		public void executeCell3(short columnIndex, String value, String codesetid,
				String id) {
			try{
			String codevalue = value;
			row = this.sheet.getRow(rowNum);
			if (row == null)
				row = this.sheet.createRow(rowNum);
			cell = row.createCell(columnIndex);
			value = AdminCode.getCode(codesetid, codevalue) != null ? codevalue+":"+AdminCode.getCode(codesetid, codevalue).getCodename() : "";
			if(value!=null&&!"".equals(value)&&("UM".equals(codesetid)|| "UN".equals(codesetid)|| "@K".equals(codesetid))){
				value = value.split(":")[1];
			}
			cell.setCellValue(new HSSFRichTextString(value));
			HSSFFont font2 = wb.createFont();
			font2.setFontHeightInPoints((short) 10);
			HSSFCellStyle style1 = wb.createCellStyle();
			style1.setFont(font2);
			style1.setAlignment(HorizontalAlignment.CENTER);
			style1.setVerticalAlignment(VerticalAlignment.CENTER);
			style1.setWrapText(true);
			style1.setBorderBottom(BorderStyle.THIN);
			style1.setBorderLeft(BorderStyle.THIN);
			style1.setBorderRight(BorderStyle.THIN);
			style1.setBorderTop(BorderStyle.THIN);
			style1.setBottomBorderColor((short) 8);
			style1.setLeftBorderColor((short) 8);
			style1.setRightBorderColor((short) 8);
			style1.setTopBorderColor((short) 8);
			style1.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));// 文本格式
			if(!"UM".equals(codesetid)&&!"UN".equals(codesetid)&&!"@K".equals(codesetid)&&!"23".equals(codesetid)){
				style1.setLocked(false);
			}
			cell.setCellStyle(style1);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		/**
		 * 新增完修改i9999和z1字段
		 */
		public void UpdateIZ(String pre,String fieldsetid,String ym){
			ContentDAO dao=new ContentDAO(this.conn);
			try
			{		
				ArrayList list = new ArrayList();
				String sql = "select a0100 from "+pre+fieldsetid+" where "+fieldsetid+"z1 = '0'";
				RowSet rs = dao.search(sql);
				while(rs.next()){
					list.add(rs.getString("a0100"));
				}
				for(int i=0;i<list.size();i++){
					int temp = getA0100(pre,fieldsetid,ym,(String) list.get(i));
					int i9999 = getMaxI9999(pre,fieldsetid,(String) list.get(i));
					String sql1 = "update "+pre+fieldsetid+" set "+fieldsetid+"z1 = '"+temp+"',i9999 = ("+i9999+") where "+fieldsetid+"z0 = "+ym+" and a0100 = '"+(String) list.get(i)+"' and "+fieldsetid+"z1 = '0'";

					dao.update(sql1);
				}
			}catch(Exception ex)
			{
				ex.printStackTrace();
			}		
		}
		public int getA0100(String pre,String fieldsetid,String ym,String _a0100){
			int a0100 = 1;
			ContentDAO dao=new ContentDAO(this.conn);
			try
			{
				String sql = "select max("+fieldsetid+"z1) as z1  from "+pre+fieldsetid+" where "+fieldsetid+"z0 = "+ym+" and a0100 = '"+_a0100+"'";
				RowSet rs = dao.search(sql);
				if(rs.next()){
					a0100 = rs.getInt("z1")+1;
				}
			}catch(Exception ex)
			{
				ex.printStackTrace();
			}
			return a0100;
		}
		public int getMaxI9999(String pre,String fieldsetid,String a0100){
			ContentDAO dao=new ContentDAO(this.conn);
			int i9999=1;
			try
			{
			String sql = "select max(i9999) as i9999 from "+pre+fieldsetid+" where a0100 = '"+a0100+"'";
			RowSet rs = dao.search(sql);
			if(rs.next()){
				i9999=rs.getInt("i9999")+1;
			}
			}catch(Exception ex)
			{
				ex.printStackTrace();
			}
			return i9999;
		}
		/**
		 * 从临时变量中取得对应指标列表
		 * @return FieldItem对象列表
		 * @throws GeneralException
		 */
		public ArrayList getMidVariableList(String fieldsetid)throws GeneralException
		{
			ArrayList fieldlist=new ArrayList();
			ArrayList new_fieldList=new ArrayList();
			RowSet rset=null;
			try
			{
				StringBuffer buf=new StringBuffer();
				buf.append("select cname,chz,ntype,cvalue,fldlen,flddec,codesetid from ");
				buf.append(" midvariable where nflag=5 and templetid=0 ");
				buf.append(" and (cstate is null or cstate='");
				buf.append(fieldsetid);
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
			return fieldlist;
		}
		/**
		 * 公式批量计算
		 * @param strWhere 计算过滤条件也即计算条件范围
		 * @return
		 * @throws GeneralException
		 */
		public boolean computing(String strWhere,ArrayList itemids,String fieldsetid,String tablename,String dbname,YearMonthCount ymc)throws GeneralException
		{
			boolean bflag=false;
			/**取得需要的计算公式列表*/
			ArrayList formulalist=this.getFormulaList(itemids);  //this.getFormulaList(1);
			if(formulalist.size()==0)
				return true;
			fldvarlist.clear();
			fldvarlist.addAll(this.getMidVariableList(fieldsetid));
			fldvarlist.addAll(this.getGzFieldList(fieldsetid));
			try
			{
				ArrayList midVariableList=getMidVariableList(fieldsetid);
				addMidVarIntoGzTable(strWhere,midVariableList,tablename,dbname,ymc);
				/**执行计算公式*/			
				secondComputing(formulalist,strWhere.substring(6),tablename,ymc);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				throw GeneralExceptionHandler.Handle(ex);
			}
			return bflag;
		}
		/**
		 * 取得当前计算公式列表
		 * @param 
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
					buf.append("'-2'");
					
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
		 * 把临时变量增加到薪资表中去。
		 */
		public void addMidVarIntoGzTable(String strWhere,ArrayList midVariableList,String _tablename,String dbname,YearMonthCount ymc)throws GeneralException
		{
			ArrayList fieldlist=midVariableList;
			ArrayList midList=midVariableList;
			try
			{
				DBMetaModel dbmodel=new DBMetaModel(this.conn);
				dbmodel.reloadTableModel(_tablename);
				RecordVo vo=new RecordVo(_tablename);
				DbWizard dbw=new DbWizard(this.conn);
				Table table=new Table(_tablename);
				String tablename="t#"+this.view.getUserName()+"_date_collect"; 
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
					dbmodel.reloadTableModel(_tablename);					
				}
				
				if(existMidFieldMap.size()>0) //同步表结构
				{
					syncGzField2(_tablename,existMidFieldMap);
				}
				
				

					String dbpre=dbname;
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
						
						
						YksjParser yp = new YksjParser(this.view, allUsedFields,
								YksjParser.forSearch, getDataType(fldtype), YksjParser.forPerson, "Ht", dbpre);
						yp.setStdTmpTable(_tablename);
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
						String tmptable="t#"+this.view.getUserName()+"_date_collect"; 
						if(createMidTable(usedlist,tmptable,"A0100"))
						{
							/**导入人员主集数据A0100,A0000,B0110,E0122,A0101*/
							buf.setLength(0);
							buf.append("insert into ");
							buf.append(tablename);
							buf.append("(A0000,A0100,B0110,E0122,A0101) select A0000,A0100,B0110,E0122,A0101 FROM ");
							buf.append(dbpre+"A01");
							buf.append(" where A0100 in (select A0100 from ");
							buf.append(_tablename);
							if(strWhere.length()==0)
							{
								buf.append(" where upper(nbase)='");
								buf.append(dbpre.toUpperCase());
								buf.append("'");
								
								/**计算临时变量的导入人员范围条件*/
								strFilter.append(" (select a0100 from ");
								strFilter.append(_tablename);
								strFilter.append(" where upper(nbase)='");
								strFilter.append(dbpre.toUpperCase());
								strFilter.append("')");	
							}
							else
							{
								
								
								/**计算临时变量的导入人员范围条件*/
								strFilter.append(" (select a0100 from ");
								strFilter.append(_tablename);
								strFilter.append(" ");
								strFilter.append(strWhere);
								strFilter.append(")");	
							}
							buf.append(")");
							dao.update(buf.toString());
						}// 创建临时表结束.
						String dd = item.getFormula();
						if(!"".equals(dd))//防止表达式为空的时候后台报错
						yp.run(item.getFormula(),ymc,"AAAAA",tmptable,dao,strFilter.toString(),this.conn,fldtype,fielditem.getItemlength(),1,item.getCodesetid());
						buf.setLength(0);
						if(strWhere.length()==0)
						{

						}
						else
						{
							buf.append(strWhere);

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
								set_str.append(_tablename+"."+temp+"="+tablename+"."+temp);
								if(Sql_switcher.searchDbServer()==2)
									set_str.append("`");
								else
									set_str.append(",");
							}
							if(set_str.length()>0)
								set_str.setLength(set_str.length()-1);
							else
								continue;
							
							dao.update("update "+_tablename+" set "+set_st2.substring(1)+"   "+buf.toString());
							dbw.updateRecord(_tablename,tablename,_tablename+".A0100="+tablename+".A0100", set_str.toString(), strcond, strcond);
						}
						else
							dbw.updateRecord(_tablename,tablename,_tablename+".A0100="+tablename+".A0100", _tablename+"."+fldname+"="+tablename+".AAAAA", strcond, strcond);
					}
				
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
		
		public void alertColumn(String tableName,FieldItem _item,DbWizard dbw,ContentDAO dao)
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
			}
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
				if(dbw.isExistTable(tablename, false))
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
				}
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
			fielditem.setItemdesc("姓名");
			fielditem.setCodesetid("0");
			fielditem.setItemtype("A");
			fielditem.setItemlength(30);
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
		 * 对选中的公式进行计算 
		 * @param formulalist 计算公式列表
		 * @param strWhere    计算范围（也即过滤条件）
		 * @return
		 * @throws GeneralException
		 */
		public boolean secondComputing(ArrayList formulalist,String strWhere,String tablename,YearMonthCount ymc)throws GeneralException
		{
			boolean bflag=false;
			try
			{
				int nrunflag=0;
				RecordVo vo=new RecordVo(tablename.toLowerCase());		
				for(int i=0;i<formulalist.size();i++)
				{
	                DynaBean dbean=(LazyDynaBean)formulalist.get(i);
	                nrunflag=Integer.parseInt((String)dbean.get("runflag"));
	                String formula=(String)dbean.get("rexpr");
	                String cond=(String)dbean.get("cond");
	                String fieldname=(String)dbean.get("itemname");
	                String strStdId=(String)dbean.get("standid");

	                /**分析左边项是否在工资表中存在*/
	                if(!vo.hasAttribute(fieldname.toLowerCase()))
	                	continue;
	                switch(nrunflag)
	                {
	                case 0://执行计算公式
	                	calcFormula(formula,cond,fieldname,strWhere,tablename,ymc);
	                	break;
	                }
				}//for i loop end.
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			return bflag;
		}
		/**
		 * 
		 * @param formula    计算公式
		 * @param cond       计算条件
		 * @param fieldname  计算项目
		 * @param strWhere   整个人员过滤条件
		 */
		private void calcFormula(String formula,String cond,String fieldname,String strWhere,String tablename,YearMonthCount ymc)
		{
			YksjParser yp=null;
			try
			{
				String strfilter="";
		        
				ContentDAO dao=new ContentDAO(this.conn);
				/**先对计算公式的条件进行分析*/
				if(!(cond==null|| "".equalsIgnoreCase(cond)))
				{ 
					
					yp = new YksjParser( this.view ,fldvarlist,
							YksjParser.forNormal, YksjParser.LOGIC,YksjParser.forPerson , "Ht", "");
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
					FieldItem item=DataDictionary.getFieldItem(fieldname);
					yp=new YksjParser( this.view ,fldvarlist,
							YksjParser.forNormal, getDataType(item.getItemtype()),YksjParser.forPerson , "Ht", "");
					yp.setYmc(ymc);
					yp.run(formula,this.conn,strcond.toString(),tablename);
				}
				String strexpr=yp.getSQL();
				StringBuffer strsql=new StringBuffer();
				strsql.append("update ");
				strsql.append(tablename);
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

					dao.update(strsql.toString());
			}
			catch(Exception ex)
			{

			}finally{ 
				yp=null;
			} 
		}
		public ArrayList getGzFieldList(String fieldsetid)throws GeneralException
		{
			ArrayList fieldlist=new ArrayList();
			RowSet rset=null;
			try
			{
				StringBuffer buf=new StringBuffer();
				buf.append("select * from fielditem where fieldsetid='");
				buf.append(fieldsetid);
				buf.append("'");
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
					item.setDisplaywidth(rset.getInt("displaywidth"));
					item.setDecimalwidth(rset.getInt("decimalwidth"));
					item.setCodesetid(rset.getString("codesetid"));
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
		public void appeal(ContentDAO dao,String tablename,String state_id,String strwhere){
			try
			{			
				String sql="update "+tablename+" set "+state_id+"='02'  "+strwhere+"";
				dao.update(sql);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();	
			}
		}
		public void approve(ContentDAO dao,String tablename,String state_id,String strwhere){
			try
			{			
				String sql="update "+tablename+" set "+state_id+"='03'  "+strwhere+"";
				dao.update(sql);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();	
			}
		}
		public boolean isHaveItem(String fieldsetid){
			boolean flag = true;
			try{
				StringBuffer buf=new StringBuffer();
				buf.append("select * from fielditem where fieldsetid='"+fieldsetid+"' and useflag='1'");
				RowSet rset=null;
				ContentDAO dao=new ContentDAO(this.conn);
				rset=dao.search(buf.toString());
				while(rset.next()){
					String itemid=rset.getString("itemid");
					String state=this.view.analyseFieldPriv(itemid);
					if(!"2".equals(state)&&!this.view.isSuper_admin()){
						flag = false;
					}
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
			return flag;
		}
}
