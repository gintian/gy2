package com.hjsj.hrms.module.jobtitle.configfile.businessobject;
/**
 * 导出代表作参数设置及导出
 * @author Administrator
 *
 */

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.template.templatetoolbar.printout.businessobject.OutWordBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.sql.RowSet;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RepresentativeMaterialsBo {

	private UserView userview = null;
	private Connection conn = null;
	private static HashMap paramMap= new HashMap();// 存储配置参数信息

	public static HashMap getParamMap() {
		return paramMap;
	}

	public RepresentativeMaterialsBo(UserView userview, Connection conn) {
		this.userview = userview;
		this.conn = conn;
	}

	/**
	 * 上传模板，将模板存放至系统设置文件存放路径 职称评审路径下 
	 * @param file
	 * @param filename
	 * @param type  1  申报人信息模板  2  代表作摘要模板 
	 * @throws Exception
	 */
	public void saveFile(File file,String filename,int type )throws Exception {
		ConstantXml constantXml = new ConstantXml(this.conn, "FILEPATH_PARAM");
		String rootDir = constantXml.getNodeAttributeValue("/filepath", "rootpath");
		Boolean bl = true;
		String arr = rootDir.replace(File.separator, "");
		if(arr.indexOf(":")==1){
			arr = arr.substring(0,1)+arr.substring(2, arr.length());
		}
		Pattern p = Pattern.compile("[\\/*:?<>\"]+");
		Matcher m = p.matcher(arr);
		if(m.find()){
			bl = false;
		}
		if(bl){
			File tempDir = new File(rootDir.replace("\\", File.separator));
			if(!tempDir.exists()){
				tempDir.mkdirs();
			}
			if(!tempDir.exists()){//检查文件夹是否存在，不存在，则路径无效
				bl = false;
			}
		}
		if (rootDir == null || "".equals(rootDir)) {
			throw new Exception("没有配置多媒体存储路径！"); 
		}

		rootDir = rootDir.replace("\\", File.separator);
		if (!rootDir.endsWith(File.separator))
			rootDir = rootDir + File.separator;
		if(!bl){
			throw new Exception("多媒体存储路径不正确！");
		}
		
		String absdir = rootDir + "multimedia" + File.separator;
		File dir1 = new File(absdir);

		if (!dir1.exists()){
			dir1.mkdirs();
		}
		
		String topath = absdir+"jobtitle"+File.separator+"representativeParam";
		File dir =new File(topath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		String filePath=topath+File.separator+filename;
		String file_path="multimedia" + File.separator+"jobtitle"+File.separator+"representativeParam"+File.separator+filename;
		
		File newFile=new File(filePath);
		if(newFile.exists())
			newFile.delete();
		Workbook workbook =null;
		InputStream  in=null;
		OutputStream out=null;
		FileInputStream is=null;
		FileOutputStream os=null;
		try {
			in=new FileInputStream(file);
			out=new FileOutputStream(newFile); 
			byte[] buf = new byte[1024];        
	        int bytesRead;        
	        while ((bytesRead = in.read(buf)) > 0) {
	            out.write(buf, 0, bytesRead);
	        }
	        //复制文件后清除文件标题下内容
	        is= new FileInputStream(newFile);
            if (file.getName().lastIndexOf(".xlsx") != -1) {// xls 格式文件
            	workbook = new XSSFWorkbook(is);
    		} else if (file.getName().lastIndexOf(".xls") != -1) {// xlsx 格式文件
    			workbook = new HSSFWorkbook(is);
    		}
            Sheet sheet = workbook.getSheetAt(1);
            int startRow=2;
            if(type==2)
            	startRow=1;
            boolean flag=true;
            while(flag) {
            	Row indexRow=sheet.getRow(startRow);
            	if(indexRow==null)
            		break;
            	for(int i=indexRow.getFirstCellNum();i<indexRow.getLastCellNum();i++) {
            		Cell cell=indexRow.getCell(i);
            		if(cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
            			sheet.removeRow(indexRow);
            			break;
            		}
            	}
            	startRow++;
            }
            os= new FileOutputStream(newFile);
            workbook.write(os);
            is.close();
            os.close();
	        
		} finally {
			PubFunc.closeIoResource(in);
			PubFunc.closeIoResource(out);
			PubFunc.closeIoResource(os);
			PubFunc.closeIoResource(is);
			PubFunc.closeIoResource(workbook);
		}
		if(type==1)
			paramMap.put("application_file_path", file_path);
		else if(type==2)
			paramMap.put("representative_file_path", file_path);
			
	}
	
	/*
	 * 获得云平台代表作模板内容
	 * 
	 * @param type: 1:申报人基础信息模板 2：代表作摘要模板
	 * 
	 * @param fileName 上传文件名称
	 * 
	 * @return { //列头信息 column_list : [ { column_name:列名 , required:true|false 是否必填
	 * } ,…… ] // 代码信息 codeList：[ { column_name:列名 , code_list:[ ‘代码名称1’,’ 代码名称2’，…
	 * ] } ,{…} ] }
	 */
	public HashMap getYunTemplateContext(File file, String fileName, int type) throws Exception {
		InputStream in = new FileInputStream(file);
		//模板存放 方便导出时直接复制相应模板写入数据
		this.saveFile(file, fileName, type);
		LinkedList<String> colList = new LinkedList<String>();// 解析excel列 列名称
		HashMap<String, LinkedList<String>> codeMap=new HashMap<String, LinkedList<String>>();//存放代码项
		Workbook wb = null;
		try {
			if (fileName.lastIndexOf(".xlsx") != -1) {// xls 格式文件
				wb = new XSSFWorkbook(in);
			} else if (fileName.lastIndexOf(".xls") != -1) {// xlsx 格式文件
				wb = new HSSFWorkbook(in);
			}
			Sheet sheet = wb.getSheetAt(1);
			Row row = null;
			if(type==2)
				row = sheet.getRow(0);
			else 
				row = sheet.getRow(1);
			int colTotal = row.getLastCellNum();
			
			for (int i = 0; i < colTotal; i++) {
				Cell cell = row.getCell(i);
				String cellValue = cell.getStringCellValue();
				if(wb instanceof HSSFWorkbook) {
					HSSFCellStyle style=(HSSFCellStyle) cell.getCellStyle();
					HSSFFont font=style.getFont(wb);
					if(font.getColor()==(short)10) {
						cellValue+="*";
					}
				}
				else {
					XSSFCellStyle style=(XSSFCellStyle) cell.getCellStyle();
					XSSFFont font=style.getFont();
					XSSFColor color=font.getXSSFColor();
					if("FFFF0000".equalsIgnoreCase(color.getARGBHex())) {
						cellValue+="*";
					}
					
				}
				colList.add(cellValue);
			}
			List<DataValidation> list = (List<DataValidation>) sheet.getDataValidations();
			for (DataValidation dataVali : list) {
				DataValidationConstraint constraint = dataVali.getValidationConstraint();
				String formula = constraint.getFormula1();// sheet页签名 说明!$CE$6690:$CE$6812 来源！ $起始列$起始行$结束列$结束行
				if (StringUtils.isNotEmpty(formula)) {
					CellRangeAddressList regions = dataVali.getRegions();
					CellRangeAddress aa = regions.getCellRangeAddresses()[0];
					String name = sheet.getRow(1).getCell(aa.getFirstColumn()).getStringCellValue();// 获取设置代码项的列
					Sheet codeSheet = wb.getSheet(formula);// 代码项数据源
					if (codeSheet != null) {
						LinkedList<String> codeList=new LinkedList<String>();
						for (int i = 0; i < codeSheet.getLastRowNum(); i++) {
							String code=codeSheet.getRow(i).getCell(0).getStringCellValue();
							if(code.startsWith("    ")) {//第三层级
								codeList.add(3+"`"+code.trim());
							}else if(code.startsWith("  ")) {//第二层级
								codeList.add(2+"`"+code.trim());
							}else{//第一层级
								codeList.add(1+"`"+code.trim());
							}
						}
						codeMap.put(name, codeList);
					}
				}
			}
		
			
			if(paramMap==null)
				paramMap=new HashMap();
			codeMap.put("fieldlist", colList);
			if (type == 1) {
				paramMap.put("application_materials", codeMap);
				paramMap.put("application_fileName", fileName);
				
			} else if (type == 2) {
				paramMap.put("representative_materials", codeMap);
				paramMap.put("representative_fileName", fileName);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeIoResource(wb);
		}
		
		return null;
	}

	
	/**
	 * 
	 * @param tempid  id:名称，id:名称....
	 */
	public void setTemplateMap(String tempid) {
		List<String> list=new ArrayList<String>();
		if(StringUtils.isNotEmpty(tempid)) {
			if(tempid.startsWith(",")) {
				tempid=tempid.substring(1);
			}
			if(paramMap==null)
				paramMap=new HashMap();
			paramMap.put("ids", Arrays.asList(tempid.split(",")));
			
		}else {
			paramMap.put("ids",list );
		}
			
	}
	
	/*
	 * 获得职称申报模板
	 * 
	 * @return ArrayList:<LazyDynaBean> name:名称 id:模板ID
	 */
	public ArrayList getPositionalTemplates() {
		return null;
	}

	/*
	 * 获取模板所有指标或某个子集的指标
	 * 
	 * @param: template_id 模板ID
	 * 
	 * @param:type 1：取指标 2：取子集
	 * 
	 * @return:[{ itemid: 指标ID ,item_desc:指标名称, item_type:指标类型 , codesetid:代码类},…]
	 */
	public ArrayList getTemplateItemInfo(int template_id, int type) throws Exception {
		
		if(type==2) {//取人事异动模板子集  chagstate
			
		}
		
		return null;
	}

	/*
	 * 获取模板所有子集信息
	 * 
	 * @param: template_id 模板ID
	 * 
	 * @return:[{ setid: 子集ID ,set_desc:子集名称,change_flag：1变化前,2变化后},…]
	 */
	public ArrayList getTemplateSetInfo(String template_id) throws Exception {
		int tempId=Integer.parseInt(template_id.split(":")[0]);
		ArrayList subSetlist=new ArrayList();
		List<LazyDynaBean> list=ExecuteSQL.executeMyQuery("select pageid,gridno,setname,Hz,sub_domain,chgstate from Template_Set where tabid="+tempId+" and subflag=1", conn);
		HashMap subMap=null;
		boolean flag=false;//判断paramMap是否已存在模板, 校验paramMap中已存储的子集数据与查询的模板子集是否发生变动，有变动更新map
		ArrayList mapList=new ArrayList();
		if(paramMap.containsKey(template_id)) {
			flag=true;
			mapList=(ArrayList) paramMap.get(template_id);
		}
		for(LazyDynaBean bean:list) {
			String hz=(String)bean.get("hz");
			int state=Integer.parseInt((String)bean.get("chgstate"));// 变化类型 1 变化前 2 变化后
				hz=hz.substring(0, hz.length()-1).replace("{", "").replace("}", "");
			if(state==1) {
				hz="现"+hz;
			}else {
				hz="拟"+hz;
			}
			String pageid=(String)bean.get("pageid");
			String gridno=(String)bean.get("gridno");
			String setname=(String)bean.get("setname");
			String sub_domain=(String)bean.get("sub_domain");
			String only_key= tempId+"_"+pageid+"_"+gridno;
			HashMap map=detilTemplateSub(sub_domain, state,setname);
			ArrayList<String> subList=(ArrayList<String>)map.get("list");
			if(flag&&mapList.size()>0) {
				boolean containKey=false;
				for(int i=0;i<mapList.size();i++) {
					HashMap submap=(HashMap)mapList.get(i);
					if(only_key.equals(submap.get("only_key"))) {//全局变量中有相同参数时
						containKey=true;
						if(map.containsKey("subID"))
							submap.put("setid", setname+"_"+map.get("subID"));
						else
							submap.put("setid", setname);
						submap.put("set_desc", hz);
						submap.put("change_flag", state+"");//变化前 变化后
						submap.put("fieldlist", subList);
						if(submap.containsKey("check_flag"))
							submap.put("check_flag", (Boolean)submap.get("check_flag"));
						else
							submap.put("check_flag", false);
						submap.put("flag", (Boolean)map.get("flag"));//子集指标是否存在 代码项为“是否”的代码
						submap.put("check_filed",submap.get("check_filed") );//存储选择的子集标识指标
						submap.put("only_key",only_key);//子集唯一标识
						subSetlist.add(submap);
						break;
					}
				}
				if(!containKey) {
					subMap=new HashMap();
					if(map.containsKey("subID"))
						subMap.put("setid", setname+"_"+map.get("subID"));
					else
						subMap.put("setid", setname);
					subMap.put("set_desc", hz);
					subMap.put("change_flag", state+"");//变化前 变化后
					subMap.put("fieldlist", subList);
					subMap.put("check_flag", false);
					subMap.put("check_filed", "");//存储选择的子集标识指标
					subMap.put("flag", (Boolean)map.get("flag"));//子集指标是否存在 代码项为“是否”的代码
					subMap.put("only_key",only_key);//子集唯一标识
					subSetlist.add(subMap);
				}
			}else {
				subMap=new HashMap();
				if(map.containsKey("subID"))
					subMap.put("setid", setname+"_"+map.get("subID"));
				else
					subMap.put("setid", setname);
				subMap.put("set_desc", hz);
				subMap.put("change_flag", state+"");//变化前 变化后
				subMap.put("fieldlist", subList);
				subMap.put("check_flag", false);
				subMap.put("check_filed", "");//存储选择的子集标识指标
				subMap.put("flag", (Boolean)map.get("flag"));//子集指标是否存在 代码项为“是否”的代码
				subMap.put("only_key",only_key);//子集唯一标识
				subSetlist.add(subMap);
			}
		}
		return subSetlist;
	}

	/**
	 * 解析子集xml 
	 * @param sub_domain
	 * @param state 变化类型 1 变化前 2 变化后
	 */
	private HashMap detilTemplateSub(String sub_domain,int state,String setName) throws Exception{
		HashMap map=new HashMap();
		ArrayList<String> list=new ArrayList<String>();
		org.dom4j.Document doc=org.dom4j.DocumentHelper.parseText(sub_domain);
		org.dom4j.Element rootEl=doc.getRootElement();
		List<org.dom4j.Element> elList=rootEl.elements();
		boolean flag=false;//子集指标是否存在 代码项为“是否”的代码
		for(org.dom4j.Element el:elList) {
			if("para".equalsIgnoreCase(el.getName())) {
				String id=el.attributeValue("id");//模板是否存在重复子集
				if(StringUtils.isNotEmpty(id))
					map.put("subID", id);
				//el.attributeValue("fields");
			}else if("field".equalsIgnoreCase(el.getName())){
				FieldItem item=DataDictionary.getFieldItem(el.attributeValue("name"), setName);
				if(item!=null&&"45".equals(item.getCodesetid())&&!false) {//子集指标维护代码项为“是否”的代码项
					flag=true;
				}
				list.add(el.attributeValue("name")+"`"
						+el.attributeValue("title")+"`"
						+(item==null||item.getItemtype()==null?"A":item.getItemtype())+"`"
						+(item==null?"0":item.getCodesetid()));
			}
		}
		map.put("flag", flag);
		map.put("list", list);
		return map;
	}

	/*
	 * 保存代表作匹配参数
	 */
	public boolean saveRepresentativeMaterialsParam(HashMap paramMap) {
		return false;
	}

	/*
	 * 导出代表作材料压缩文件
	 * 
	 * @param type 1: 选择的是分组ID 2：选择的是人员上会材料记录ID
	 * 
	 * @param meetingID 会议ID
	 * 
	 * @param selectIDs 所选记录ID
	 * 
	 * @return 文件名
	 */
	public String exportRepresentativeMaterialsFile(int type, int meetingID, String selectIDs) {
		return "";
	}
	
	/**
	 * 获得指标
	 * @param tabids
	 * @return
	 */
	public ArrayList getFieldList(String tabids) {
		//初始化下拉列表store
		RowSet rset = null;
		ArrayList itemlist = new ArrayList();
		try {
			if(StringUtils.isNotBlank(tabids)) {
				ContentDAO dao=new ContentDAO(this.conn);
				String sql = "select count(field_name) num,field_name,field_type,codeid,chgstate,field_hz from template_set where tabid in ("+tabids+") and flag not in ('H','S','P','V','F') group by field_name,field_type,codeid,chgstate,field_hz";
				rset = dao.search(sql);
				HashSet set = new HashSet();
				String[] tabidarr = tabids.split(",");
				int length = tabidarr.length;
				while(rset.next()) {
					String field_hz = rset.getString("field_hz");
					String field_name = rset.getString("field_name");
					String field_type = rset.getString("field_type");
					String codeid = rset.getString("codeid");
					String chgstate = rset.getString("chgstate");
					int num = rset.getInt("num");
					if(!set.contains(field_name+"_"+chgstate)) {
						HashMap itemmap = new HashMap();
						if(StringUtils.isNotBlank(field_hz)&&StringUtils.isNotBlank(field_name)&&num>=length) {
							set.add(field_name+"_"+chgstate);
							itemmap.put("fieldname", field_hz+("1".equals(chgstate)?"[现]":"[拟]"));
							itemmap.put("fieldvalue", field_name+"_"+chgstate);
							itemmap.put("fieldtype", field_type);
							itemmap.put("codesetid", codeid);
							itemlist.add(itemmap);
						}
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rset);
		}
		return itemlist;
	}
	/**
	 * 获得代码
	 * @param codesetid
	 * @return
	 */
	public ArrayList getCodeList(String codesetid) {
		RowSet rset = null;
		ArrayList itemlist = new ArrayList();
		try {
			ContentDAO dao=new ContentDAO(this.conn);
			String sql = "";
			if("UN".equals(codesetid)||"UM".equals(codesetid)||"@K".equals(codesetid)) {
				sql = "select codeitemid,codeitemdesc from organization where codesetid='"+codesetid+"' and codeitemid=parentid order by codeitemid";
			}else {
				sql = "select codeitemid,codeitemdesc from codeitem where codesetid='"+codesetid+"' and codeitemid=parentid order by codeitemid";
			}
			rset = dao.search(sql);
			//第一层 layer=1
			String codeitemid1 = "";
			int layer = 1;
			while(rset.next()) {
				String codeitemid = rset.getString("codeitemid");
				String codeitemdesc = rset.getString("codeitemdesc");
				HashMap itemmap = new HashMap();
				if(StringUtils.isNotBlank(codeitemid)&&StringUtils.isNotBlank(codeitemdesc)) {
					itemmap.put("codeitemid", codeitemid);
					itemmap.put("codeitemdesc", codeitemdesc);
					itemmap.put("layer", layer+"");
					itemlist.add(itemmap);
					codeitemid1+="'"+codeitemid+"',";
				}
			}
			//第二层 layer=2
			this.getLayerDesc(codesetid,codeitemid1,itemlist,layer);
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rset);
		}
		return itemlist;
	}
	private void getLayerDesc(String codesetid, String codeitemids, ArrayList itemlist, int layer) {
		RowSet rset = null;
		try {
			ContentDAO dao=new ContentDAO(this.conn);
			if(StringUtils.isNotBlank(codeitemids)) {
				layer = layer+1;
				codeitemids = codeitemids.substring(0, codeitemids.length()-1);
				String[] codeArry=codeitemids.split(",");
				ArrayList<String> list=new ArrayList<String>();
				String itemid="";
				for (int i = 0; i < codeArry.length; i++) {// 解决代码类超出1000个 sql查询报错问题
					if((i+1)%1000==0) {
						itemid+=codeArry[i]+",";
						list.add(itemid);
						itemid="";
					}else {
						itemid+=codeArry[i]+",";
					}
				}
				String sql = "";
				if("UN".equals(codesetid)||"UM".equals(codesetid)||"@K".equals(codesetid)) {
					if(list.size()>0) {
						StringBuffer sbf=new StringBuffer();
						for(String str:list) {
							sbf.append("select codeitemid,codeitemdesc from organization  where codesetid='"+codesetid+"' and parentid in ("+str.substring(0, str.length()-1)+") and codeitemid<>parentid");
							sbf.append(" union all ");
						}
						sql=sbf.toString().substring(0, sbf.length()-10)+" order by codeitemid ";
					}else {
						sql = "select codeitemid,codeitemdesc from organization  where codesetid='"+codesetid+"' and parentid in ("+codeitemids+") and codeitemid<>parentid order by codeitemid";
					}
				}else {
					if(list.size()>0) {
						StringBuffer sbf=new StringBuffer();
						for(String str:list) {
							sbf.append("select codeitemid,codeitemdesc from codeitem  where codesetid='"+codesetid+"' and parentid in ("+str.substring(0, str.length()-1)+") and codeitemid<>parentid");
							sbf.append(" union all ");
						}
						sql=sbf.toString().substring(0, sbf.length()-10)+" order by codeitemid ";
					}else {
						sql = "select codeitemid,codeitemdesc from codeitem  where codesetid='"+codesetid+"' and parentid in ("+codeitemids+") and codeitemid<>parentid order by codeitemid";
					}
				}
				rset = dao.search(sql);
				String codeitemid1 = "";
				while(rset.next()) {
					String codeitemid = rset.getString("codeitemid");
					String codeitemdesc = rset.getString("codeitemdesc");
					HashMap itemmap = new HashMap();
					if(StringUtils.isNotBlank(codeitemid)&&StringUtils.isNotBlank(codeitemdesc)) {
						itemmap.put("codeitemid", codeitemid);
						itemmap.put("codeitemdesc", codeitemdesc);
						itemmap.put("layer", layer+"");
						itemlist.add(itemmap);
						codeitemid1+="'"+codeitemid+"',";
					}
				}
				this.getLayerDesc(codesetid,codeitemid1,itemlist,layer);
			}else {
				return;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rset);
		}
	}

	/**
	 * 获得模板以及页签
	 * @param tabids
	 * @return
	 */
	public HashMap getOtherMateriaList(String tabids) {
		RowSet rset = null;
		HashMap map = new HashMap();
		try {
			ContentDAO dao=new ContentDAO(this.conn);
			String sql ="select a.tabid,a.pageid,a.title,b.name from template_page a,template_table b where a.tabid=b.tabid and  a.tabid in("+tabids+") and "+Sql_switcher.isnull("a.isshow", "1")+"<>0 ";
			rset = dao.search(sql);
			while(rset.next()) {
				String tabid = rset.getString("tabid");
				String pageid = rset.getString("pageid");
				String title = rset.getString("title");
				String name = rset.getString("name");
				String tabname = tabid+"."+name;
				String pageids = "";
				if(map.containsKey(tabname)) {
					pageids = map.get(tabname)+","+pageid+"`"+title;
					map.put(tabname, pageids);
				}else
					map.put(tabname, pageid+"`"+title);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rset);
		}
		return map;
	}
    /**
     * 分析静态变量map，存储xml
     */
	public void analysisParamMap() {
		RowSet rs = null;
		String xmlDoc="";
		ContentDAO dao=new ContentDAO(this.conn);
		try {
			RecordVo paramsVo=ConstantParamter.getConstantVo("JOBTITLE_CONFIG");
			if(null != paramsVo){
				xmlDoc = paramsVo.getString("str_value");
			}else {
				rs = dao.search("select Str_Value from constant where Constant=?",
						Arrays.asList(new String[]{"JOBTITLE_CONFIG"}));
				if(rs.next()){
					xmlDoc=rs.getString("Str_Value");
				}else {
					paramsVo = new RecordVo("Constant");
					paramsVo.setString("constant", "JOBTITLE_CONFIG");
					paramsVo.setString("describe", "职称评审配置参数");
					paramsVo.setString("type", "A");
				}
			}
			if (xmlDoc!=null && xmlDoc.length()>0){
		        try {
		            Document doc = PubFunc.generateDom(xmlDoc);
		            XMLOutputter outputter = new XMLOutputter();
		            Format format = Format.getPrettyFormat();
					format.setEncoding("UTF-8");
					outputter.setFormat(format);
		            Element root = doc.getRootElement();
		            Element magnum_opus = root.getChild("magnum_opus");
		            Element application_materials = null;
		            Element other_materials = null;
		            Element representative_materials=null;//代表作摘要节点
		            Element templateID=null;//存储模板
		          
		            
		            if(magnum_opus!=null) {
		            	application_materials = magnum_opus.getChild("application_materials");
		            	if(application_materials!=null) {
		            		magnum_opus.removeChild("application_materials");
		            	}else
		            		application_materials = new Element("application_materials");
		            	
		            	representative_materials=magnum_opus.getChild("representative_materials");
		            	if(representative_materials!=null) {
		            		magnum_opus.removeChild("representative_materials");
		            	}else {
		            		representative_materials=new Element("representative_materials");
		            	}
		            	templateID=magnum_opus.getChild("template");
		            	if(templateID!=null) {
		            		magnum_opus.removeChild("template");
		            	}else {
		            		templateID=new Element("template");
		            	}
		            	
		            	other_materials = magnum_opus.getChild("other_materials");
		            	if(other_materials!=null) {
		            		magnum_opus.removeChild("other_materials");
		            	}else
		            		other_materials = new Element("other_materials");
		            }else {
		            	magnum_opus=new Element("magnum_opus");
		            	root.addContent(magnum_opus);
		            	application_materials = new Element("application_materials");
		            	other_materials = new Element("other_materials");
		            	representative_materials=new Element("representative_materials");
		            	templateID=new Element("template");
		            }
		            
		            //存储模板指标数据map 转成xml
		            this.mapToXml(1, magnum_opus);
		            
		            List<String> teplatelist=(List<String>) paramMap.get("ids");
		            String id="";
		            String idName="";
		            for (String value : teplatelist) {
						idName+=value+",";
						id+=value.split(":")[0]+",";
					}
		            if(idName.length()>0) {
		            	templateID.setAttribute("ids", id.substring(0, id.length()-1));
		            	templateID.setAttribute("value",idName.substring(0,idName.length()-1));
		            	magnum_opus.addContent(templateID);
		            }
		            //申报人信息匹配与代表作摘要模板相关信息存储
		            //field_mapping 节点
		            HashMap fieldmappingmap = (HashMap) paramMap.get("field_mapping");
		    		Element fieldmappingele = null;
		    		if(application_materials!=null) {
		    			fieldmappingele = application_materials.getChild("field_mapping");
		    			if(fieldmappingele!=null) {
		    				application_materials.removeChild("field_mapping");
		    				//application_materials.removeChild(name)
		    			}
		    		}
		    		Element fieldmappingelement = new Element("field_mapping");
		    		if(!fieldmappingmap.isEmpty()) {
		    			for (Object key : fieldmappingmap.keySet()) {
		    				Element fieldelement = new Element("field");
		    				String hjsoftcloundname = (String)key;
		    				String required = "flase";
		    				if(hjsoftcloundname.endsWith("*")) {//必填项
		    					hjsoftcloundname = hjsoftcloundname.substring(0,hjsoftcloundname.length()-1);
		    					required = "true";
		    				}
		    				String value = (String)fieldmappingmap.get(key);
		    				String[] valuearr = value.split("`");
		    				String fielddesc = valuearr[0];
		    				String fieldname = valuearr[1];
		    				String codesetid = valuearr[3];
		    				fieldelement.setAttribute("id", fieldname);
		    				fieldelement.setAttribute("required", required);
		    				fieldelement.setAttribute("desc", fielddesc);
		    				fieldelement.setAttribute("codesetid", codesetid);
		    				fieldelement.setText(hjsoftcloundname);
		    				fieldmappingelement.addContent(fieldelement);
		    			}
		    			application_materials.addContent(fieldmappingelement);
		    		}
		    		//code_set 节点   <code id="01" layer="1" desc="哲学">哲学</code>  desc="哲学"：系统代码项名称  text：云代码项描述 
		    		Element code_set = null;
		    		if(application_materials!=null) {
		    			code_set = application_materials.getChild("code_set");
		    			if(code_set!=null) {
		    				application_materials.removeChildren("code_set");
		    			}
		    		}
		    		HashMap code_setgmap = (HashMap) paramMap.get("code_set");
		    		if(code_setgmap!=null&&code_setgmap.size()>0) {
		    			for (Object key : code_setgmap.keySet()) {
		    				Element code_setele = new Element("code_set");
		    				String codesetid = (String)key;
		    				code_setele.setAttribute("codesetid", codesetid);
		    				ArrayList codesetlist = (ArrayList) code_setgmap.get(key);
		    				for(int i=0;i<codesetlist.size();i++) {
		    					HashMap map = (HashMap) codesetlist.get(i);
		    					Element code = new Element("code");
		    					for(Object key1:map.keySet()) {
		    						String code_ = (String) map.get(key1);
		    						String codevalue = (String)key1;
		    						String layer = "1";
		    						if(codevalue.indexOf("&nbsp;")==-1) {
		    							layer = "1";
		    						}else {
		    							String c = "&nbsp;";
		    							String[] ch = codevalue.split("&nbsp;");
		    							int t = 0;
		    							for (int j = 0; j < ch.length; j++) {
		    								String s = ch[j];
		    								if ("".equals(s)) {
		    									t++;
		    								}
		    							}
		    							if(t%2==0) {
		    								layer=(t/2+1)+"";
		    							}
		    						}
		    						String codeid = code_.split("`")[0];
		    						String codedesc = code_.split("`")[1];
		    						code.setAttribute("id", codeid);
		    						code.setAttribute("layer", layer);
		    						code.setAttribute("desc", codedesc);
		    						codevalue = codevalue.replace("&nbsp;", "");
		    						code.setText(codevalue);
		    					}
		    					code_setele.addContent(code);
		    				}	
		    				application_materials.addContent(code_setele);
		    			}
		    		}
		    		magnum_opus.addContent(application_materials);
		    		//代表作摘要节点 start
//		    		representative_materials
		    		representative_materials.removeChildren("template");
		    		List<String> list=(List<String>) paramMap.get("ids");
		    		for(int i=0;i<list.size();i++) {
		    			String idKey=list.get(i);
		    			ArrayList<HashMap<String,Object>> sublist=(ArrayList<HashMap<String,Object>>) paramMap.get(idKey);
		    			Element el=new Element("template");
		    			el.setAttribute("id", idKey.split(":")[0]);
		    			el.setAttribute("name", idKey.split(":")[1]);
		    			for (HashMap<String,Object> hashMap : sublist) {
		    				Element setEl=new Element("set");
							boolean check_flag=(Boolean)hashMap.get("check_flag");//是否选中
							//boolean flag=(Boolean)hashMap.get("flag");//模板子集是否包含45号代码项 
							String change_flag=(String)hashMap.get("change_flag");
							if(check_flag) {
								String check_filed=(String)hashMap.get("check_filed");
								String key="t_"+(String)hashMap.get("setid")+"_"+(String)hashMap.get("change_flag");
								setEl.setAttribute("setid",(String)hashMap.get("setid"));
								setEl.setAttribute("check_filed",check_filed==null?"":check_filed);
								setEl.setAttribute("check_flag",true+"");
								setEl.setAttribute("change_flag", change_flag);
								setEl.setAttribute("only_key", (String)hashMap.get("only_key"));	
								if(StringUtils.isNotEmpty(check_filed)) {
									setEl.setAttribute("flag", "1");//是否启用代表作标识
								}else {
									setEl.setAttribute("flag", "0");
								}
								
								el.addContent(setEl);
							}
						}
		    			representative_materials.addContent(el);
		    		}
		    		HashMap<String,HashMap<String,String>> subMap=(HashMap<String,HashMap<String,String>>) paramMap.get("sub_filedMap");
		    		representative_materials.removeChildren("field_mapping");
		    		Element fieldMapp=new Element("field_mapping");
		    		for(String key:subMap.keySet()) {
		    			Element fieldSetEl=new Element("fieldset");
		    			fieldSetEl.setAttribute("setid", key);
		    			HashMap<String,String> fieldMap=subMap.get(key);
		    			for(String field:fieldMap.keySet()) {
		    				Element fieldEl=new Element("field");
		    				String fieldvalue=fieldMap.get(field);
		    				if(StringUtils.isNotEmpty(fieldvalue)) {
		    					fieldEl.setAttribute("id",fieldMap.get(field).split("`")[0] );
			    				fieldEl.setAttribute("name", fieldMap.get(field).split("`")[1]);
		    				}else {
		    					fieldEl.setAttribute("id","" );
			    				fieldEl.setAttribute("name", "");
		    				}
		    				
		    				fieldEl.setText(field);
		    				fieldSetEl.addContent(fieldEl);
		    			}
		    			fieldMapp.addContent(fieldSetEl);
		    		}
		    		representative_materials.addContent(fieldMapp);
		    		magnum_opus.addContent(representative_materials);
		    		//代表作摘要节点 end
		    		//其他材料节点 template
		    		HashMap other_materialsmap = (HashMap) paramMap.get("other_materials");
		    		Element templateele = null;
		    		if(other_materials!=null) {
		    			templateele = other_materials.getChild("template");
		    			if(templateele!=null) {
		    				other_materials.removeChildren("template");
		    			}
		    		}
		    		
		    		for(Object key : other_materialsmap.keySet()) {
		    			String tabid = (String)key;
		    			boolean flag=false;
		    			for(String templatename:list) {
		    				if(tabid.equals(templatename.split(":")[0])) {
		    					flag=true;
		    				}
		    			}
		    			if(flag) {
		    				Element template = new Element("template");
		    				String pages = (String) other_materialsmap.get(key);
		    				template.setAttribute("id", tabid);
		    				template.setText(pages);
		    				other_materials.addContent(template);
		    			}
		    		}
		    		magnum_opus.addContent(other_materials);
		    		xmlDoc = outputter.outputString(doc);
		        } catch (JDOMException e) {
		            e.printStackTrace();
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		        paramsVo.setString("str_value", xmlDoc);
				dao.updateValueObject(paramsVo);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 上传申报人模板 与代表作摘要模板解析
	 * @param type 1 map 转xml  2 xml 转 map
	 * @param el
	 */
	private void mapToXml(int type,Element el) throws Exception {
		if(type==1&&el!=null) {
			if(el.getChild("application")!=null)
				el.removeChild("application");
			String application_fileName=(String) paramMap.get("application_fileName");
			String application_file_path=(String) paramMap.get("application_file_path");
			HashMap<String,Object> applicationMap=(HashMap) paramMap.get("application_materials");
			el.addContent(this.mapToElement(applicationMap, "application", application_fileName,application_file_path));
			
			if(el.getChild("representative")!=null)
				el.removeChild("representative");
			String representative_fileName=(String) paramMap.get("representative_fileName");
			String representative_file_path=(String) paramMap.get("representative_file_path");
			HashMap<String,Object> representativeMap=(HashMap<String,Object>) paramMap.get("representative_materials");
			el.addContent(this.mapToElement(representativeMap, "representative", representative_fileName,representative_file_path));
		}else if(type==2&&el!=null){
			Element application=el.getChild("application");
			if(application!=null) {
				String application_fileName=application.getAttributeValue("name");
				String application_file_path=application.getAttributeValue("filePath");
				List<Element> list=application.getChildren();
				paramMap.put("application_materials",this.elementToMap(list));
				paramMap.put("application_fileName",application_fileName);
				paramMap.put("application_file_path", application_file_path);
			}
			Element representative=el.getChild("representative");
			if(representative!=null) {
				String representative_fileName=representative.getAttributeValue("name");
				String representative_file_path=representative.getAttributeValue("filePath");
				List<Element> list=representative.getChildren();
				paramMap.put("representative_materials",this.elementToMap(list));
				paramMap.put("representative_fileName",representative_fileName);
				paramMap.put("representative_file_path",representative_file_path);
			}
		}
	}
	
	private Element mapToElement(HashMap<String,Object> map,String elementName,String fileName,String filePath)throws Exception {
		Element el=new Element(elementName);
		el.setAttribute("name", fileName);
		el.setAttribute("filePath", filePath);
		for(String key:map.keySet()) {
			if("fieldlist".equals(key)) {
				LinkedList list=(LinkedList)map.get("fieldlist");
				Element fieldEl=new Element("fieldlist");
				String str="";
				for(int i=0;i<list.size();i++) {
					str+=list.get(i)+",";
				}
				if(str.length()>0)
					fieldEl.setText(str.substring(0, str.length()-1));
				else
					fieldEl.setText(str);
				el.addContent(fieldEl);
			}else {
				Element codeEl=new Element("codeItem");
				LinkedList list=(LinkedList)map.get(key);
				String str="";
				for(int i=0;i<list.size();i++) {
					str+=list.get(i)+",";
				}
				if(str.length()>0)
					codeEl.setText(str.substring(0, str.length()-1));
				else
					codeEl.setText(str);
				codeEl.setAttribute("name", key);
				el.addContent(codeEl);
			}
		}
		return el;
	}
	
	private HashMap elementToMap(List<Element> list)throws Exception {
		HashMap codeMap=new HashMap();
		for(Element childEl:list) {
			String name=childEl.getName();
			if("fieldlist".equals(name)) {
				String[] arry=childEl.getText().split(",");
				LinkedList<String> fieldlist=new LinkedList<String>();
				if(arry.length>0) {
					for (int i = 0; i < arry.length; i++) {
						fieldlist.add(arry[i]);
					}
				}
				codeMap.put("fieldlist", fieldlist);
			}else {
			   String key=childEl.getAttributeValue("name");
			   String[] arry=childEl.getText().split(",");
			   LinkedList<String> codelist=new LinkedList<String>();
			   if(arry.length>0) {
				   for (int i = 0; i < arry.length; i++) {
					   codelist.add(arry[i]);
				   }
				   codeMap.put(key, codelist);
			   }
			}
		}
		return codeMap;
	}
	
	/**
	 * 从表中查找xml解析出对应数据
	 * @param string
	 * @return
	 */
	public void getParamValue() {
		  ContentDAO dao=new ContentDAO(this.conn);
		  RowSet rs = null;
		  try {
			  String xmlDoc = "";
			  RecordVo paramsVo=ConstantParamter.getConstantVo("JOBTITLE_CONFIG");
			  if(null != paramsVo){
				  xmlDoc = paramsVo.getString("str_value");
			  }else {
				  rs = dao.search("select Str_Value from constant where Constant=?",
						  Arrays.asList(new String[]{"JOBTITLE_CONFIG"}));
				  if(rs.next()){
					  xmlDoc=rs.getString("Str_Value");
				  }
			  }
			  if (xmlDoc!=null && xmlDoc.length()>0){
				  paramMap.clear();
		             Document doc = PubFunc.generateDom(xmlDoc);
		             Element root = doc.getRootElement();
		             //field_mapping 申报人信息匹配
		             Element magnum_opus = root.getChild("magnum_opus");
		             if(magnum_opus==null) {
		            	 magnum_opus=new Element("magnum_opus");
		             }
		             Element templateEl=magnum_opus.getChild("template");
		             if(templateEl!=null) {
		            	 String value=templateEl.getAttributeValue("value");
		            	 paramMap.put("ids", Arrays.asList(value.split(",")));
		             }
		             this.mapToXml(2, magnum_opus);
		             
		             Element application_materials = magnum_opus.getChild("application_materials");
		             if(application_materials!=null) {
		            	 HashMap fieldmapping = new HashMap();
			             Element field_mapping = application_materials.getChild("field_mapping");
			             if(field_mapping!=null) {
			            	 List fieldlist = field_mapping.getChildren("field");
				             for(int i=0;i<fieldlist.size();i++) {
				              Element field = (Element) fieldlist.get(i);
				              String id = field.getAttributeValue("id");
				              String required = field.getAttributeValue("required");
				              String desc = field.getAttributeValue("desc");
				              String codesetid = field.getAttributeValue("codesetid");
				              String hjcloundname = field.getValue();
				              if("true".equals(required)) {
				               hjcloundname = hjcloundname+"*";
				              }
				              String value = desc+"`"+id+"`"+"A"+"`"+codesetid;
				              fieldmapping.put(hjcloundname, value);
				             }
				             paramMap.put("field_mapping", fieldmapping);
			             }
			             //指标代码匹配 code_set
			             List code_setlist = application_materials.getChildren("code_set");
			             if(code_setlist.size()>0){
			            	 HashMap codesetmap = new HashMap();
				             for(int i=0;i<code_setlist.size();i++) {
				              Element code_set = (Element) code_setlist.get(i);
				              String codesetid = code_set.getAttributeValue("codesetid");
				              ArrayList codemdblist = new ArrayList();
				              List codelist = code_set.getChildren("code");
				              for(int j=0;j<codelist.size();j++) {
				               Element code = (Element) codelist.get(j);
				               String id = code.getAttributeValue("id");
				               String layer = code.getAttributeValue("layer");
				               String desc = code.getAttributeValue("desc");
				               String value = code.getValue();
				               String kongge = "";
				               int n = Integer.parseInt(layer);
				               for(int k=0;k<(n-1)*2;k++){
				                kongge+="&nbsp;";
				               }
				               value = kongge+value;
				               HashMap codemap = new HashMap();
				               codemap.put(value, id+"`"+desc);
				               codemdblist.add(codemap);
				              }
				              codesetmap.put(codesetid, codemdblist);
				             }
				             paramMap.put("code_set", codesetmap);
			             }
		             }
		             
		             //代表作摘要 representative_materials
		             Element representative=magnum_opus.getChild("representative_materials");
		             if(representative!=null) {
		            	 List<Element> templatesList=representative.getChildren("template");
		            	 ArrayList list=null;
		            	 for (Element templatesEl : templatesList) {
		            		 String id=templatesEl.getAttributeValue("id");
		            		 String name=templatesEl.getAttributeValue("name");
		            		 String key=id+":"+name;
		            		 List<Element> setElList=templatesEl.getChildren("set");
		            		 list=new ArrayList();
		            		 HashMap setMap=null;
		            		 for(Element setEl:setElList) {
		            			setMap=new HashMap();
		            			String setid=setEl.getAttributeValue("setid");
		            			String check_filed=setEl.getAttributeValue("check_filed");
		            			String only_key=setEl.getAttributeValue("only_key");
		            			String flag=setEl.getAttributeValue("flag");
		            			setMap.put("setid", setid);
		            			setMap.put("change_flag", setEl.getAttributeValue("change_flag"));
		            			setMap.put("check_filed", check_filed);
		            			setMap.put("check_flag", true);//保存的全是选中状态的
		            			
		            			setMap.put("only_key", only_key);
		            			if(StringUtils.isNotEmpty(check_filed))
		            				setMap.put("flag", true);
		            			else
		            				setMap.put("flag", false);
		            			list.add(setMap);
		            		 }
		            		 paramMap.put(key, list);
						}
		             }
		             if(representative!=null) {
		            	 Element sub_filedEl=representative.getChild("field_mapping");
			             if(sub_filedEl!=null) {
			            	 Element elSet=sub_filedEl.getChild("fieldset");
			            	 List<Element> setlist=sub_filedEl.getChildren("fieldset");
			            	 HashMap subMap=null;
			            	 ArrayList list=new ArrayList();
			            	 subMap=new HashMap();
			            	 for (Element element : setlist) {
								String setid=element.getAttributeValue("setid");
								List<Element> fieldMapplist=element.getChildren("field");
								HashMap<String,String> map=new HashMap<String,String>();	
								for (Element mappfieldEl : fieldMapplist) {
									String value=mappfieldEl.getText();
									String id=mappfieldEl.getAttributeValue("id");
									String name=mappfieldEl.getAttributeValue("name");
									if(StringUtils.isEmpty(id)||StringUtils.isEmpty(name))
										map.put(value, "");
									else
										map.put(value, id+"`"+name);
								}
								subMap.put(setid, map);
//								list.add(subMap); 
							}
			            	 paramMap.put("sub_filedMap", subMap);
			             }
		             }
		            
		             //代表作摘要 end
		             
		             //其他参数配置 other_materials
		             Element other_materials = magnum_opus.getChild("other_materials");
		             if(other_materials!=null) {
		            	 List templatelist = other_materials.getChildren("template");
			             HashMap other_materialsmap = new HashMap();
			             for(int i=0;i<templatelist.size();i++) {
			              Element template = (Element) templatelist.get(i);
			              String id = template.getAttributeValue("id");
			              String value = template.getValue();
			              other_materialsmap.put(id, value);
			             }
			             paramMap.put("other_materials", other_materialsmap);
		             }
		   		}
		  }catch (Exception e) {
			  e.printStackTrace();
		  }
	}
	/**
	 * 清除parammap中对应的key
	 * @param keyvalue
	 */
	public void delParamValueByKey(String keyvalue) {
		paramMap.remove(keyvalue);
	}
	
	/***
	 * 保存后的代表作摘要解析
	 * */
	public void updateRepresentativeParaMap(HashMap<String,ArrayList> template_map,
			HashMap<String,HashMap<String,String>> sub_filedMap) throws Exception {
		for(String tempid:template_map.keySet()) {
			ArrayList list=template_map.get(tempid);
			ArrayList convertList=new ArrayList();
			for(int i=0;i<list.size();i++) {
				HashMap map=PubFunc.DynaBean2Map((MorphDynaBean)list.get(i));
				convertList.add(map);
			}
			paramMap.put(tempid, convertList);
		}
		paramMap.put("sub_filedMap", sub_filedMap);
	}
	/**
	 * 判断选择的模板是否改变了
	 * @param tempid
	 * @return
	 */
	public boolean compareTemid(String tempid) {
		boolean isSame = false;
		try {
			String[] sortlist1 = null;
			String[] sortlist2 = null;
			if(paramMap.containsKey("ids")) {
				List idList=(List) paramMap.get("ids");
				sortlist1 = new String[idList.size()];
				for(int i=0;i<idList.size();i++) {
					String id = (String) idList.get(i);
					if(StringUtils.isNotBlank(id)) {
						String tabid = id.split(":")[0];
						sortlist1[i]=tabid;
					}
				}
			}
			if(StringUtils.isNotBlank(tempid)) {
				//去掉前一个
				if(tempid.indexOf(",")==0) {
					tempid= tempid.substring(1, tempid.length());
				}
				String[] temparr = tempid.split(",");
				sortlist2 = new String[temparr.length];
				for(int i=0;i<temparr.length;i++) {
					String id = temparr[i];
					if(StringUtils.isNotBlank(id)) {
						String tabid = id.split(":")[0];
						sortlist2[i]=tabid;
					}
				}
			}
			if(sortlist1!=null&&sortlist2!=null) {
				Arrays.sort(sortlist1);
				Arrays.sort(sortlist2);
				isSame = Arrays.equals(sortlist1,sortlist2);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return isSame;
	}
	
	/**
	 * 导出申报人和代表作.zip
	 * @param personList tabid:  ins_id:  taskid:
	 * @return
	 * @throws Exception
	 */
	public String getOutFile(ArrayList<HashMap> personList)throws Exception {
		HashMap<String,ArrayList<HashMap<String,String>>> maplist=new HashMap<String,ArrayList<HashMap<String,String>>>();
		HashMap<String,String> map=null;
		if(paramMap==null|| paramMap.size()<=0) {
			this.getParamValue();
		}
		if(paramMap==null|| paramMap.size()<=0)
			throw new Exception("导出代表作参数设置未配置，请检查配置！");

		try {
			//相同模板 list
			for (HashMap bean : personList) {
				String tabid=(String)bean.get("tabid");
				map=new HashMap<String, String>();
				map.put("a0100", (String)bean.get("a0100"));
				map.put("ins_id",(String)bean.get("ins_id"));
				if(StringUtils.isNotEmpty((String)bean.get("task_id")))	
					map.put("taskid",PubFunc.decrypt((String)bean.get("task_id")));
				else
					map.put("taskid","");
				if(maplist.containsKey(tabid)) {
					ArrayList list=maplist.get(tabid);
					list.add(map);
				}else {
					ArrayList<HashMap<String,String>> list=new ArrayList<HashMap<String,String>>();
					list.add(map);
					maplist.put(tabid, list);
				}
			}
			List<LazyDynaBean> list=this.getApplicationFile(maplist);
			//创建临时文件夹
			String dirPath=System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+ this.userview.getUserName()+"_templet_represent";
			File file=new File(dirPath);
			if(file.exists()) {
				this.deleteDirOrFile(dirPath);
			}
			file.mkdirs();

			//生成工号模板
			File appFile=getRepresentativeFile(1, dirPath);
			this.detailFile(1, list, appFile);
			//生成代表作摘要模板
			File repreFile=getRepresentativeFile(2, dirPath);
			//查询人员子集数据
			HashMap dataMap=templateSubStates(list);
			ArrayList<LazyDynaBean> datalist=(ArrayList<LazyDynaBean>)dataMap.get("data");
			this.detailFile(2, datalist, repreFile);
			HashMap<String,ArrayList> attachMap=(HashMap<String,ArrayList>)dataMap.get("attachMap");
			this.moveFile(attachMap, dirPath);
			//导出其他材料
			this.createTemplatePage(dirPath, list,maplist);
			String zipName=this.createZipFile(dirPath);
			return zipName;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * 删除子文件夹及文件夹内文件
	 * **/
	private void deleteDirOrFile(String path){
		File file=new File(path);
		if(file.exists()){
			if(file.isDirectory()){
				File[] listFile=file.listFiles();
				for (int i = 0; i < listFile.length; i++) {
					String childpath=listFile[i].getAbsolutePath();
					if(listFile[i].isFile()){
						File childFile=new File(childpath);
						childFile.delete();
					}
				}
				file.delete();
			}else{
				file.delete();
			}
		}
	}
	
	/**
	 * 导出附件  工号+D_1+个人评估报告
	 * @param attachMap 工号标识：附件list 
	 */
	private void moveFile(HashMap<String,ArrayList> attachMap,String dirPath)throws Exception{
		ConstantXml constantXml = new ConstantXml(this.conn, "FILEPATH_PARAM");
		String rootDir = constantXml.getNodeAttributeValue("/filepath", "rootpath");
		try {
			for(String only_key:attachMap.keySet()) {
				ArrayList fileList=attachMap.get(only_key);
				int index=0;
				for(int i=0;i<fileList.size();i++) {
					if(StringUtils.isNotEmpty((String)fileList.get(i))) {
						String filepath=(String)fileList.get(i);
						String[] fileArry=filepath.split("\\|");
						/*if(fileArry.length>=2)
							continue;*/
						if(fileArry[0].lastIndexOf(".")<0) {
							 String filename_=fileArry[0];
							 try{
						         filename_=SafeCode.decode(filename_);
						        }catch(Exception ex){
						         filename_=fileArry[0];
						        }
							 	fileArry[0]=PubFunc.decrypt(filename_);
						}
						// 保存后的文件名|文件路径|原文件名
						File oldFile=null;
						
						if(fileArry[1].startsWith(rootDir)) {//附件路径存储的为绝对路径
							oldFile=new File(fileArry[1]+File.separator+fileArry[0]);
						}else {//附件路径存储的为相对路径
							oldFile=new File(rootDir+File.separator+"multimedia"+File.separator+fileArry[1]+File.separator+fileArry[0]);
						}
						String fileType=fileArry[0].substring(fileArry[0].lastIndexOf("."), fileArry[0].length());
						index++;
						File newFile=new File(dirPath+File.separator+only_key+"_D"+index+"_个人评估报告v1.5"+fileType);
						//if(oldFile.exists()&&newFile.exists())
							copyFile(oldFile, newFile);
						
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
	}
	
	
	/***
	 * 文件夹压缩
	 * */
	private String createZipFile(String sourceFilePath)throws Exception{
		String tmpFileName=this.userview.getUserName()+"_templet.zip";
        File sourceFile = new File(sourceFilePath);  
        java.io.FileInputStream fis = null;  
        java.io.BufferedInputStream bis = null;  
        FileOutputStream fos = null;  
        java.util.zip.ZipOutputStream zos = null;  
        File zipFile = new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator") + tmpFileName);  
        try {  //System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+userview.getUserName()+"_tempCard"
	        if(sourceFile.exists() == false){  
	            throw GeneralExceptionHandler.Handle(new Exception("压缩文件夹不存在！"));
	        }else{  
                if(zipFile.exists()){  
                    zipFile.delete();
                }
                
                File[] sourceFiles = sourceFile.listFiles();  
                if(null == sourceFiles || sourceFiles.length<1){ 
                	throw GeneralExceptionHandler.Handle(new Exception("待压缩的文件目录：" + sourceFilePath + "里面不存在文件，无需压缩."));
                }else{  
                    fos = new FileOutputStream(zipFile);  
                    zos = new java.util.zip.ZipOutputStream(new java.io.BufferedOutputStream(fos));  
                    byte[] bufs = new byte[1024*10];  
                    for(int i=0;i<sourceFiles.length;i++){  
                        //创建ZIP实体，并添加进压缩包  
                        java.util.zip.ZipEntry zipEntry = new java.util.zip.ZipEntry(sourceFiles[i].getName());  
                        zos.putNextEntry(zipEntry);  
                        //读取待压缩的文件并写进压缩包里  
                        fis = new java.io.FileInputStream(sourceFiles[i]);  
                        bis = new java.io.BufferedInputStream(fis, 1024*10);  
                        int read = 0;  
                        while((read=bis.read(bufs, 0, 1024*10)) != -1){  
                            zos.write(bufs,0,read);  
                        }
                        fis.close();
                        bis.close();
                    }
                    zos.closeEntry();
                }  
            }  
        }catch (java.io.IOException e) {  
            e.printStackTrace();  
            throw GeneralExceptionHandler.Handle(e);
        } finally{  
            //关闭流  
            	PubFunc.closeIoResource(bis);
            	PubFunc.closeIoResource(zos);
                PubFunc.closeIoResource(fis);
                PubFunc.closeIoResource(fos);
        } 
        if(zipFile.exists()) {
        	if(zipFile.length()>1073741824l){
        		tmpFileName="";
        		throw new Exception("云上传申报人信息与代表作仅支持上传1G大小文件，请拆分人员分次导出！");
        	}
        }
		return tmpFileName;
	}
	
	private List<LazyDynaBean> getPersonSubList() throws Exception{
		HashMap fieldMapping=(HashMap) paramMap.get("sub_filedMap");
//		fieldMapping.get(key)
		return null;
	}
	
	/**
	 * 人事异动子集附件拷贝至 导出代表作文件夹
	 * @param oldFile
	 * @param newFile
	 * @throws Exception
	 */
	private void copyFile(File oldFile,File newFile)throws Exception {
		FileChannel in=null;
		FileChannel out=null;
		FileInputStream inStream=null;
		FileOutputStream outStream=null;
		try {
			inStream=new FileInputStream(oldFile);
			outStream=new FileOutputStream(newFile);
			in=inStream.getChannel();
			out=outStream.getChannel();
			ByteBuffer buffer=ByteBuffer.allocate(1024*10);
			while(true) {
				buffer.clear();
				int leng=in.read(buffer);
				if(leng==-1) {
					break;
				}
				buffer.flip();
				out.write(buffer);
			}
		} catch (Exception e) {
			throw e;
		}finally {
			PubFunc.closeIoResource(inStream);
			PubFunc.closeIoResource(outStream);
			PubFunc.closeIoResource(in);
			PubFunc.closeIoResource(out);
		}
	}
	
	
	/**
	 * type 1 工号唯一模板 2 代表作摘要
	 * @param type
	 * @param list  查询数据写入excel中
	 * @param file
	 * @throws Exception
	 */
	private void detailFile(int type,List<LazyDynaBean> list,File file)throws Exception {
		InputStream in=null;
		FileOutputStream os=null;
		Workbook wb=null;
		try {
			LinkedList<String> fieldlist=new LinkedList<String>();
			if(type==1) {
				HashMap appMap=(HashMap) paramMap.get("application_materials");
				fieldlist=(LinkedList<String>)appMap.get("fieldlist");
			}else {
				HashMap repreMap=(HashMap) paramMap.get("representative_materials");
				fieldlist=(LinkedList<String>)repreMap.get("fieldlist");
			}
			in=new FileInputStream(file);
			if (file.getName().lastIndexOf(".xlsx") != -1) {// xls 格式文件
				wb = new XSSFWorkbook(in);
			} else if (file.getName().lastIndexOf(".xls") != -1) {// xlsx 格式文件
				wb = new HSSFWorkbook(in);
			}
			Sheet sheet=wb.getSheetAt(1);
			int startRow=2;
			if(type==2)
				startRow=1;
			Font font2=wb.createFont();
			font2.setFontHeightInPoints((short)11);
			font2.setFontName("宋体");
			CellStyle contentStyle = wb.createCellStyle();
			contentStyle.setAlignment(HorizontalAlignment.CENTER);// 表头居中
			contentStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			contentStyle.setBorderBottom(BorderStyle.THIN);
			contentStyle.setBorderTop(BorderStyle.THIN);
			contentStyle.setBorderLeft(BorderStyle.THIN);
			contentStyle.setBorderRight(BorderStyle.THIN);
			contentStyle.setWrapText(true);
			contentStyle.setFont(font2);
			
			DataFormat format=wb.createDataFormat();
			CellStyle dateStyle=wb.createCellStyle();
			dateStyle.setAlignment(HorizontalAlignment.CENTER);// 表头居中
			dateStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			dateStyle.setBorderBottom(BorderStyle.THIN);
			dateStyle.setBorderTop(BorderStyle.THIN);
			dateStyle.setBorderLeft(BorderStyle.THIN);
			dateStyle.setBorderRight(BorderStyle.THIN);
			dateStyle.setDataFormat(format.getFormat("yyyy年mm月dd日"));
			dateStyle.setFont(font2);
			
			CreationHelper creationHelper = wb.getCreationHelper();
			SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd");
			for(LazyDynaBean bean:list) {
				Row row=sheet.getRow(startRow);
				if(row==null)
				   row=sheet.createRow(startRow);
				row.setHeight((short)800);
				for(int i=0;i<fieldlist.size();i++) {
					Cell cell=row.createCell(i);
					if("发表或出版时间".equals(fieldlist.get(i).toLowerCase().replace("*", ""))) {
						cell.setCellStyle(dateStyle);
					}else
						cell.setCellStyle(contentStyle);
					if("序号".equals(fieldlist.get(i).replace("*", "")))
						cell.setCellValue((Integer)bean.get(fieldlist.get(i).replace("*", ""))+"");
					else {
						if("发表或出版时间".equals(fieldlist.get(i).toLowerCase().replace("*", ""))) {
							String date=(String)bean.get(fieldlist.get(i).toLowerCase().replace("*", ""));
							if(StringUtils.isNotEmpty(date)) {
								date=date.replace(".", "-");
								Date date_=sdf1.parse(date);
								cell.setCellValue(date_);
							}else
								cell.setCellValue("");
						}else {
							if(bean.get(fieldlist.get(i).toLowerCase().replace("*", ""))==null) {
								cell.setCellValue("");
							}else {
								cell.setCellValue((String)bean.get(fieldlist.get(i).toLowerCase().replace("*", "")));
							}
						}
					}
					
				}
				startRow++;
			}
			os= new FileOutputStream(file);
	        wb.write(os);
	        wb.close();
	        os.flush();
		} finally {
			PubFunc.closeIoResource(in);
			PubFunc.closeIoResource(os);
			PubFunc.closeIoResource(wb);
		}
        
	}
	
	/**
	 * 
	 * @param codeId
	 * @param value
	 * @return
	 * @throws Exception
	 */
	private String getCodeValue(String codeSet,String value)throws Exception{
		HashMap code_set=(HashMap) paramMap.get("code_set");
		if(code_set==null||!code_set.containsKey(codeSet))
			return "";
		ArrayList<HashMap<String,String>> list=(ArrayList<HashMap<String,String>>)code_set.get(codeSet);
		if(list==null||list.size()<=0)
			return "";
		String codeValue="";
		label:for(HashMap<String,String> map:list) {
				for(String key:map.keySet()) {
					String code=map.get(key);
					String code_=code.split("`")[0];
					if(value.equals(code_)) {
						codeValue=code.split("`")[1];
						break label;
					}
				}
		}
		return codeValue.replace("&nbsp;", " ");
	}
	
	//根据选择的单位部门指标 
	public String getCodeName(String id,String codeId){
	
		String str="";
		while(id.length()>0){
			if(StringUtils.isNotEmpty(AdminCode.getCodeName(codeId.toUpperCase(), id)))
				str=AdminCode.getCodeName(codeId.toUpperCase(), id)+"/"+str;
			id=id.substring(0,id.length()-1);
		}
		if(StringUtils.isNotEmpty(str))
			return str.substring(0, str.length()-1);
		else
			return "";
	}
	
	/***
	 * 工号模板不能为空 可选模板内指标或者系统设置的唯一标识指标
	 * 查询工号模板与代表作子集数据
	 * */
	private List<LazyDynaBean> getApplicationFile(HashMap<String,ArrayList<HashMap<String,String>>> maplist) throws Exception {
		HashMap<String,String> codeMap=new HashMap<String,String>();//存储选择指标对应代码项
		//唯一工号模板路径
		String appFile_path=(String) paramMap.get("application_file_path");
		HashMap appMap=(HashMap) paramMap.get("application_materials");
		LinkedList<String> appFieldlist=(LinkedList<String>)appMap.get("fieldlist");
		HashMap<String,String> appMapping=(HashMap<String,String>) paramMap.get("field_mapping");
		List<String> ids=(List<String>) paramMap.get("ids");
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
        String chk = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");//唯一性指标
        boolean chkFlag=false;
		StringBuffer fieldSql=new StringBuffer(" basepre,a0100,ins_id,task_id, ");
		List<LazyDynaBean> appBeanList=new ArrayList<LazyDynaBean>();
		for(int i=0;i<appFieldlist.size();i++) {
			String field=appFieldlist.get(i);
			//{姓名*=姓名`A0101_1`A`0, 单位=单位名称`B0110_1`A`UN, 一级学科*=一级学科`A01A5_1`A`I2, 二级学科=二级学科`A01A6_2`A`I3}
			if(appMapping.containsKey(field)) {
				String fieldValue=appMapping.get(field);
				if(fieldValue.length()>0&&fieldValue.indexOf("`")>-1) {
					if("工号".equals(field)&&"唯一标识".equals(fieldValue.split("`")[0])) {//工号特殊处理
							chkFlag=true;
							continue;
					}else {
					//选择指标串长度为5 代表对应关系为空:``A`0
						if(fieldValue.split("`").length==4) {
							if(StringUtils.isEmpty(fieldValue.split("`")[0])) {
								fieldSql.append(" '' as \""+field.replace("*", "")+"\" ");
							}else {
								fieldSql.append(fieldValue.split("`")[1]+" as \""+field.replace("*", "")+"\" ");
								if(!codeMap.containsKey(fieldValue.split("`")[1]))
									codeMap.put(field.replace("*", ""), fieldValue.split("`")[3]);
							}
						}	
						
					}
					
				}
			}else {
				fieldSql.append(" '' as \""+field.replace("*", "")+"\" ");
			}
			if(i<appFieldlist.size()-1) {
				fieldSql.append(" , ");
			}
		}
		
		if (ids.size() > 0) {
			for(String id_key:ids) {
				StringBuffer sbf=new StringBuffer();
				String tabid=id_key.split(":")[0];
				if(!maplist.containsKey(tabid))
					continue;
				sbf.append("select '"+tabid+"' as tabid, "+fieldSql.toString()+this.getSubRepresentative(id_key)+ " from templet_"+tabid);
				ArrayList<HashMap<String,String>> personlist=maplist.get(tabid);
				String a0100Sql="";
				String ins_taskSql="";
				for(HashMap<String,String> map:personlist) {
					a0100Sql+="'"+map.get("a0100")+"',";
					ins_taskSql+="(ins_id="+map.get("ins_id")+/*" and task_id="+map.get("taskid")+*/" ) or";
				}
				sbf.append(" where ");
				if(StringUtils.isNotEmpty(a0100Sql))
					sbf.append(" A0100 in ("+a0100Sql.substring(0, a0100Sql.length()-1)+")");
				if(StringUtils.isNotEmpty(ins_taskSql))
					sbf.append(" and ("+ins_taskSql.substring(0, ins_taskSql.length()-2)+") ");
				
				if(sbf.length()>0) {
					appBeanList.addAll(ExecuteSQL.executeMyQuery(sbf.toString()));
					
				}
				
			}
		}
		
		RowSet rs=null;
		ContentDAO dao=new ContentDAO(conn);
		try {
			for(LazyDynaBean bean:appBeanList) {
				//代码类翻译
				for(String field:codeMap.keySet()) {
					if(StringUtils.isNotEmpty(codeMap.get(field))&&!"0".equals(codeMap.get(field))&&bean.get(field)!=null) {
						String value=(String)bean.get(field);
						if("UM".equalsIgnoreCase(codeMap.get(field))||"UN".equalsIgnoreCase(codeMap.get(field))) {
							bean.set(field, this.getCodeName(value, codeMap.get(field)));
						}else {
							bean.set(field, this.getCodeValue(codeMap.get(field), value));
						}
					}
				}
				
				if(chkFlag||StringUtils.isEmpty((String)bean.get("工号"))) {//选择唯一标识指标
					String a0100=(String)bean.get("a0100");
					String nbase=(String)bean.get("basepre");
					rs=dao.search(" select "+chk+" from "+nbase+"A01 where A0100='"+a0100+"'");
					while(rs.next()) {
						bean.set("工号", rs.getString(1)==null?"":rs.getString(1));
					}
				}
			}
		} finally {
			PubFunc.closeDbObj(rs);
		}
		
		return appBeanList;
	}
	/**
	 * 根据模板id查找选中的子集
	 * @param id
	 * @return
	 */
	private String getSubRepresentative(String id)throws Exception {
		ArrayList<HashMap<String,Object>> list=(ArrayList<HashMap<String,Object>>) paramMap.get(id);
		String subFileds="";
		for(HashMap<String,Object> map:list) {
			if((Boolean)map.get("check_flag")) {//选中的子集 返回子集名称
				subFileds+=","+"t_"+(String)map.get("setid")+"_"+(String)map.get("change_flag");
			}
		}
		if(StringUtils.isNotEmpty(subFileds))
			return subFileds;
		else
			return "";
	}
	
	private HashMap templateSubStates(List<LazyDynaBean> personlist)throws Exception {
		List<String> idList=(List<String>) paramMap.get("ids");
		HashMap<String,HashMap<String,String>> fieldsMapp=(HashMap<String,HashMap<String,String>>) paramMap.get("sub_filedMap");
		HashMap sub_map=new HashMap();
		HashMap idMap=new HashMap();
		for(String id:idList) {
			idMap.put(id.split(":")[0], id);
		}
		HashMap<String,ArrayList> attachMap=new HashMap<String, ArrayList>();
		ArrayList<LazyDynaBean> allDataList=new ArrayList<LazyDynaBean>();
		for(LazyDynaBean bean:personlist) {
			String id=(String)bean.get("tabid");
			ArrayList<LazyDynaBean> dataList=new ArrayList<LazyDynaBean>();
			ArrayList<HashMap<String,Object>> subList=(ArrayList<HashMap<String,Object>>) paramMap.get(idMap.get(id));
			int index=0;
			for(int i=0;i<subList.size();i++) {
				HashMap<String,Object> map=subList.get(i);
				if((Boolean)map.get("check_flag")) {//选中的子集
					String  subName="t_"+map.get("setid")+"_"+map.get("change_flag");
					String sub_domain=(String)bean.get(subName.toLowerCase());
					String check_filed=(String)map.get("check_filed");
					check_filed=StringUtils.isNotEmpty(check_filed)?check_filed.split("`")[0]:"";
					if(StringUtils.isNotEmpty(sub_domain)) {
						dataList.addAll(this.deatilSub(attachMap,bean,sub_domain, subName, check_filed, index));
					}
						
				}
			}
			allDataList.addAll(dataList);
		}
		
		sub_map.put("data", allDataList);
		sub_map.put("attachMap", attachMap);
		return sub_map;
	}
	//解析子集xml  <record state="D"  已删除 不需要
	private ArrayList<LazyDynaBean> deatilSub(HashMap<String,ArrayList> attachMap,LazyDynaBean personBean,String sub_domain,String sub_name,String check_filed,int index) throws Exception{
		check_filed=check_filed.toLowerCase();
		ArrayList<LazyDynaBean> dataList=new ArrayList<LazyDynaBean>();
		Document doc = PubFunc.generateDom(sub_domain);
		XMLOutputter outputter = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);
		Element root = doc.getRootElement();
		String columns=root.getAttributeValue("columns");
		List<Element> list=root.getChildren("record");
		HashMap<String,HashMap<String,String>> sub_fildMap=(HashMap<String,HashMap<String,String>>) paramMap.get("sub_filedMap");
		LazyDynaBean bean=null;
		
		for(Element el:list) {
			bean=new LazyDynaBean();
			String state=el.getAttributeValue("state");
			List columList=Arrays.asList(columns.toLowerCase().split("`"));
			if(StringUtils.isEmpty(el.getText())||el.getText().length()<=columList.size())//子集记录为空的不记录/子集空记录不记录
				continue;
			
			if(!"D".equalsIgnoreCase(state)) {//去除标记为D的子集
				int field_index=columList.indexOf(check_filed);
			
				String[] valueArry=el.getText().split("`",-1);
				if(StringUtils.isNotEmpty(check_filed)&&field_index>-1) {//标记 是 否 列 取是的记录
					if(!"1".equals(valueArry[field_index]))
						continue;
				}
				SimpleDateFormat sdf=new SimpleDateFormat("YYYY.MM.DD");
				Calendar cal=Calendar.getInstance();
				HashMap<String,String> map=sub_fildMap.get(sub_name);//字段 对应 内容
				for(String field:map.keySet()) {
					String field_Mapp=map.get(field);
					if(StringUtils.isNotEmpty(field_Mapp)&&!"工号*".equals(field)&&!"序号*".equals(field)) {
						int value_index=columList.indexOf(field_Mapp.split("`")[0].toLowerCase());
						FieldItem item=DataDictionary.getFieldItem(field_Mapp.split("`")[0], sub_name.split("_")[1]);
						if(item!=null&&item.isCode()) {
							bean.set(field.replace("*", ""), AdminCode.getCodeName(item.getCodesetid(), valueArry[value_index]));
						}else {
							if("D".equalsIgnoreCase(item.getItemtype())) {
								if(StringUtils.isNotEmpty(valueArry[value_index])) {
									bean.set(field.replace("*", ""), valueArry[value_index]);
//									Date date=sdf.parse(valueArry[value_index]);
//									cal.setTime(date);
//									bean.set(field.replace("*", ""), cal.get(Calendar.YEAR)+"-"+(cal.get(Calendar.MONTH)+1)+"-"+cal.get(Calendar.DATE)+"");	
								}else {
									bean.set(field.replace("*", ""), "");	
								}
							}else
								bean.set(field.replace("*", ""), valueArry[value_index]);
						}
							
					}
				}
				//子集数据不为空时 添加至list 
				if(!bean.getMap().isEmpty()) {//不为空时 添加工号与序号
					bean.set("工号", personBean.get("工号"));
					index+=1;
					bean.set("序号", index);
					dataList.add(bean);
				}
				//子集附件考虑 存储路径
				if(columList.contains("attach")) {
					field_index=columList.indexOf("attach");
					if(StringUtils.isNotEmpty(valueArry[field_index])) {
						String[] attach=valueArry[field_index].split(",");
						if(attachMap.containsKey((String)personBean.get("工号"))) {
							ArrayList fileList=attachMap.get((String)personBean.get("工号")); 
							fileList.addAll(Arrays.asList(attach));
						}else {
							ArrayList fileList=new ArrayList();
							fileList.addAll(Arrays.asList(attach));
							attachMap.put((String)personBean.get("工号"), fileList);
						}
					}
				}
			}
		}
		
		return dataList;
	}
	
	/**
	 * 存储附件复制到临时文件夹内
	 * @param type 1 唯一工号模板  2 代表作
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	private File getRepresentativeFile(int type,String filePath)throws Exception{
		ConstantXml constantXml = new ConstantXml(this.conn, "FILEPATH_PARAM");
		String rootDir = constantXml.getNodeAttributeValue("/filepath", "rootpath");
		File tempDir = new File(rootDir.replace("\\", File.separator));
		File newFile=null;
		if(!tempDir.exists())
			throw new Exception("多媒体路径配置不正确，请检查配置！");
		String abs_path="";
		if(type==1) {
			//唯一工号模板
			abs_path=rootDir+File.separator+(String) paramMap.get("application_file_path");
			newFile=new File(filePath+File.separator+(String) paramMap.get("application_fileName"));
		}else if(type==2) {
			//代表作模板路径
			abs_path=rootDir+File.separator+(String) paramMap.get("representative_file_path");
			newFile=new File(filePath+File.separator+(String) paramMap.get("representative_fileName"));
		}
		File file=new File(abs_path.replace("\\", File.separator));
		
		if(newFile.exists())
			newFile.delete();
		InputStream  in=null;
		OutputStream out=null;
		try {
			in=new FileInputStream(file);
			out=new FileOutputStream(newFile); 
			byte[] buf = new byte[1024];        
	        int bytesRead;        
	        while ((bytesRead = in.read(buf)) > 0) {
	            out.write(buf, 0, bytesRead);
	        }
		} finally {
			PubFunc.closeIoResource(in);
			PubFunc.closeIoResource(out);
		}
		return newFile;
	}
	/**
	 * 生成模板页  XX_C1_个人评估报告v1.5
	 * @param filePath
	 * @param maplist
	 */
	private void createTemplatePage(String filePath, List<LazyDynaBean> list,
			HashMap<String, ArrayList<HashMap<String, String>>> maplist) throws Exception {
		HashMap<String, Object> otherMap = (HashMap<String, Object>) paramMap.get("other_materials");
		// 设置打印页后 查询模板不打印页签
		try {
			HashMap<String, String> pageNoMap = getNoPritPageNo(otherMap);
			ArrayList<String> objlist = null;
			ArrayList<String> inslist = null;
			for (LazyDynaBean bean : list) {
				String tabid = (String) bean.get("tabid");// 人员模板号
				String basepre = (String) bean.get("basepre");
				String a0100 = (String) bean.get("a0100");
				String task_id = (String) bean.get("task_id");
				String ins_id = (String) bean.get("ins_id");
				if (StringUtils.isEmpty(task_id)) {//// 查询template_模板号表中task_id 存在为空的 改为从maplist中找对应task_id
					ArrayList<HashMap<String, String>> personlist = maplist.get(tabid);
					if (personlist != null && personlist.size() > 0) {
						for (HashMap<String, String> personMap : personlist) {
							if (a0100.equals(personMap.get("a0100")) && ins_id.equals(personMap.get("ins_id"))) {
								task_id = personMap.get("taskid");
								break;
							}
						}
					}
				}
				objlist = new ArrayList<String>();
				objlist.add(basepre + a0100);
				inslist = new ArrayList<String>();
				inslist.add(ins_id);
				OutWordBo owbo = new OutWordBo(this.conn, this.userview, Integer.parseInt(tabid), task_id);
				owbo.setNoshow_pageno(pageNoMap.get(tabid));// 去除模板设置设置某页是否打印
				owbo.setOuttype("0");
				if(otherMap==null||otherMap.get(tabid)==null||otherMap.get(tabid).toString().indexOf("`")<0||"`".equals(otherMap.get(tabid).toString()))
					continue;
				if (otherMap.get(tabid).toString().indexOf("`") > -1)
					owbo.setShow_pageno(otherMap.get(tabid).toString().split("`")[0]);
				else
					owbo.setShow_pageno(otherMap.get(tabid).toString());
				String othFileName = owbo.outword(objlist, 1, inslist);
				File oldOthFile = new File(
						System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + othFileName);
				File newOthFile = new File(filePath + System.getProperty("file.separator") + (String) bean.get("工号")
						+ "_C1_个人评估报告v1.5.pdf");
				if (newOthFile.exists()) {
					newOthFile.delete();
				}
				oldOthFile.renameTo(newOthFile);

			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}
	
	// 设置某页打印 返回其他不打印页的页码
	private HashMap<String,String> getNoPritPageNo(HashMap<String, Object> othFileMap)throws Exception{
			HashMap<String,String> pageMap=new HashMap<String, String>();
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rs=null;
			try {
				ArrayList list=null;
				for (String tabid:othFileMap.keySet()) {
					String pageno="";
					rs=null;
					list=new ArrayList();
					list.add(tabid);
					rs=dao.search("select * from Template_Page where tabid=?",list);
					if(othFileMap.get(tabid).toString().indexOf("`")<0||"`".equals(othFileMap.get(tabid).toString()))
						continue;
					String[] pritArry=othFileMap.get(tabid).toString().split("`")[0].split(",");
					ArrayList printlist=new ArrayList();
					for (int i = 0; i < pritArry.length; i++) {
						printlist.add(pritArry[i]);
					}
					while(rs.next()) {
						if(printlist.contains(rs.getString("pageid"))) {//应打印的不统计
							continue;
						}
						pageno+=","+rs.getString("pageid");
					}
					if(StringUtils.isNotEmpty(pageno))
						pageno=pageno.substring(1);
					pageMap.put(tabid, pageno);
				}
			}catch (Exception e) {
				e.printStackTrace();
				throw e;
			}finally {
				PubFunc.closeDbObj(rs);
			}
			return pageMap;
		}
	
}
