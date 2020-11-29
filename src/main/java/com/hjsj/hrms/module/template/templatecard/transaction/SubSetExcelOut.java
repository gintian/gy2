package com.hjsj.hrms.module.template.templatecard.transaction;
//子集模板导出导入

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.template.templatecard.businessobject.TempletChgLogBo;
import com.hjsj.hrms.module.template.templatesubset.businessobject.TemplateSubsetBo;
import com.hjsj.hrms.module.template.utils.TemplateFuncBo;
import com.hjsj.hrms.module.template.utils.TemplateUtilBo;
import com.hjsj.hrms.module.template.utils.javabean.TemplateParam;
import com.hjsj.hrms.module.template.utils.javabean.TemplateSet;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.virtualfilesystem.service.VfsService;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SubSetExcelOut extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		String flag=(String)this.getFormHM().get("flag");
		String tabid=(String)this.getFormHM().get("tabid");
		String ins_id=(String)this.getFormHM().get("ins_id");
		String task_id=(String)this.getFormHM().get("task_id");
		String realtask_id_e=(String)this.getFormHM().get("realtask_id_e");
		task_id= PubFunc.decrypt(SafeCode.decode(task_id));
		if(!"0".equalsIgnoreCase(task_id)){
			realtask_id_e= PubFunc.decrypt(SafeCode.decode(realtask_id_e));
		}
		if(StringUtils.isBlank(realtask_id_e)){
			realtask_id_e="0";
		}
		if(task_id.indexOf(",")==-1&&StringUtils.isNotBlank(task_id)){
			realtask_id_e=task_id;
		}
		if(StringUtils.isBlank(ins_id)||"null".equalsIgnoreCase(ins_id)){
			ins_id="0";
		}
		try {
			String fieldSet=(String)this.getFormHM().get("fieldSet");//子集
			ArrayList<MorphDynaBean> columnList=(ArrayList<MorphDynaBean>)this.getFormHM().get("column");
			if("1".endsWith(flag)) {//导出模板
				String fileName=createExcel(fieldSet,columnList);
				this.getFormHM().put("file", PubFunc.encrypt(fileName));
			}else {
				String path=(String)this.getFormHM().get("path");
				String xmldata=(String)this.getFormHM().get("xmldata");//前台取到的格式  
				ArrayList<Object> objList=analyzeSubSet(path, fieldSet, columnList);
				ArrayList<String> recoredList=(ArrayList<String>)objList.get(0);
				ArrayList<HashMap<String,String>> recoredMapList=(ArrayList<HashMap<String,String>>)objList.get(1);;
				ArrayList<String> recordKeyIdList=(ArrayList<String>)objList.get(2);
				if(recoredList.size()>0) {
					//解析数据 添加至xmldata数据中
					String oldxml=xmldata;//记录旧的子集数据用于对比子集变动
					xmldata=addChildElement(xmldata, recoredList,recordKeyIdList);
					String tablename=PubFunc.decrypt((String)this.getFormHM().get("table_name"));
					tabid=(String)this.getFormHM().get("tabid");
					String objid=PubFunc.decrypt((String)this.getFormHM().get("basepre"));
					String columnName=(String)this.getFormHM().get("_field_name");
					String viewtype=(String)this.getFormHM().get("viewtype");
					TemplateParam param=new TemplateParam(this.frameconn, this.userView,Integer.parseInt(tabid));
					TemplateUtilBo utilBo= new TemplateUtilBo(this.frameconn,this.userView);
					Boolean isAutoLog=param.getIsAotuLog();
					Boolean isRejectAutoLog=param.getIsRejectAotuLog();
					if(isRejectAutoLog&&!"0".equalsIgnoreCase(ins_id)){
						Boolean isHaveRejecttask=utilBo.isHaveRejectTaskByInsId(ins_id);
						if(isHaveRejecttask){
							isAutoLog=true;
						}
					}
					if(isAutoLog&&!("0".equalsIgnoreCase(ins_id)&&(param.getOperationType()==0||param.getOperationType()==5))){
						TempletChgLogBo chgLogBo=new TempletChgLogBo(this.frameconn, this.userView,param);
						 ArrayList cellList= utilBo.getAllCell(Integer.parseInt(tabid));
						 TemplateSet subsetBo =null;
						 for(int i=0;i<cellList.size();i++){
							 TemplateSet setBo = (TemplateSet) cellList.get(i);
							 if(setBo.isSubflag()&&columnName.equalsIgnoreCase(setBo.getTableFieldName())){
								 subsetBo=setBo;
							 }
						 }
						chgLogBo.insertOrUpdateOneSubsetLogger(columnName, oldxml, xmldata, ins_id, realtask_id_e, objid, tablename, param.getInfor_type(), subsetBo);
					}
					boolean state=false;
					if("list".equalsIgnoreCase(viewtype)) {
						TemplateSubsetBo subBo=new TemplateSubsetBo(this.getFrameconn(),this.userView,tabid,columnName);
					    xmldata = subBo.saveSubAttachment(xmldata);
						state=subBo.saveSub_dataXml(tablename, columnName, objid, tabid,xmldata,"0");
					}else {
						String uniqueId=(String)this.getFormHM().get("uniqueId");
						String[] arg=uniqueId.split("_"); 
						TemplateSet templateSet=utilBo.getCell(Integer.valueOf(tabid),Integer.valueOf(arg[1]),Integer.valueOf(arg[2]));
						TemplateSubsetBo  subsetBo=new TemplateSubsetBo(this.getFrameconn(),this.userView,tabid,templateSet.getTableFieldName(),templateSet.getXml_param(),"1"); 
						//解析xml 得到用于存储的子集xml信息
						ArrayList xmldata_ = subsetBo.saveSubAttachments(xmldata);
						state=subsetBo.saveSub_dataXml(tablename, columnName, objid, tabid,String.valueOf(xmldata_.get(0)),"0");
					}
                    if(state){
    					this.getFormHM().put("succeedFlag", true);
    					this.getFormHM().put("recoredlist", recoredMapList);
    					this.getFormHM().put("Msg",ResourceFactory.getProperty("template_new.inputSuccess")+recoredList.size()+ResourceFactory.getProperty("label.item")+ResourceFactory.getProperty("menu.record.label"));
    				}
				}else{//excel中没有数据
					this.getFormHM().put("succeedFlag", true);
					this.getFormHM().put("Msg",ResourceFactory.getProperty("template_new.inputSuccess")+recoredList.size()+ResourceFactory.getProperty("label.item")+ResourceFactory.getProperty("menu.record.label"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.getFormHM().put("errorMsg", e.getMessage());
			this.getFormHM().put("succeedFlag", false);
		}
		
	}
	
	//解析数据添加至xml中
	private String addChildElement(String xmldata,ArrayList<String> recoredlist,ArrayList<String> recordKeyIdList) {
		XMLOutputter outputter = new XMLOutputter();
		try {
			Document doc=PubFunc.generateDom(xmldata);;
			Element eleRoot=null;  //xml解析得到/records对象
		
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			String xpath="/records";
			XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点		
			eleRoot =(Element) findPath.selectSingleNode(doc);
			if(eleRoot!=null) {
				for(int num=0;num<recoredlist.size();num++) {
					String recored=recoredlist.get(num);
					String recordKeyId=recordKeyIdList.get(num);
					Element childEle=new Element("record");
					childEle.setAttribute("I9999", "-1");
					childEle.setAttribute("state", "");
					childEle.setAttribute("edit", "");
					childEle.setAttribute("record_key_id",recordKeyId);
					childEle.addContent(recored);
					eleRoot.addContent(childEle);
					
				}
			}
			return outputter.outputString(doc);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/***
	 * 解析excel 取出数据
	 * */
	private ArrayList<Object> analyzeSubSet(String path,String fieldSet,ArrayList<MorphDynaBean> columnList) throws Exception {
		ArrayList list=new ArrayList();
		RowSet rset=null;
		ArrayList<String> recoredList=new ArrayList<String>();
		ArrayList<String> recoredKeyIdList=new ArrayList<String>();
		ArrayList<HashMap<String,String>> recoredMapList=new ArrayList<HashMap<String,String>>();
		InputStream input=VfsService.getFile(path);
		ContentDAO dao=new ContentDAO(this.frameconn);
		try {
			ArrayList dbList=DataDictionary.getDbpreList();
			Boolean isSettingOnlyName=false;
			Boolean isHaveImppeople=false;
			Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.frameconn);
	        String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
	        String valid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "valid");
	        ArrayList imppeopleList=new ArrayList();
	        if ("0".equals(valid)) {
	        	isSettingOnlyName = false;
	        }else{
	        	FieldItem item = DataDictionary.getFieldItem(onlyname);
				if (item!=null){
					isSettingOnlyName = true;
				}
				else {
					isSettingOnlyName = false;
				}
	        }
	        HashMap chidlFatherMap=new HashMap();
	        for (MorphDynaBean bean : columnList) {
	        	if("true".equalsIgnoreCase(String.valueOf(bean.get("imppeople")))){
	        		isHaveImppeople=true;
	        		imppeopleList.add(String.valueOf(bean.get("fldName")));
	        	}
	        	if(StringUtils.isNotBlank((String)bean.get("fatherRelationField"))){
	        		chidlFatherMap.put(String.valueOf(bean.get("fldName")).toLowerCase(), String.valueOf(bean.get("fatherRelationField")).toLowerCase());
	        	}
	        }
	        if(isHaveImppeople&&!isSettingOnlyName){
        		throw new Exception("子集中有指标设置了启用人员组件，请设置并且启用唯一性指标。");

	        }
			HSSFWorkbook wb=new HSSFWorkbook(input);
			HSSFSheet sheet=wb.getSheetAt(0);
			//判断表头与导入子集是否一致
			HSSFRow firstRow=sheet.getRow(0);
			HashMap<String,HashMap<String,String>> codedescToIDMap=new HashMap<String, HashMap<String,String>>();
			HashMap<String,HashMap<String,String>> codeidToDescMap=new HashMap<String, HashMap<String,String>>();
			HashMap<String,HashMap<String,String>> endCodeMap=new HashMap<String, HashMap<String,String>>(); //末端代码 用于判断设置代码项是否设置仅显示末端代码
			int i=0;
			for (MorphDynaBean bean : columnList) {
				 if(!"2".equals((String)bean.get("fldPriv"))){
					 continue; 
				 }
				 if("attach".equals((String)bean.get("fldName")))
					 continue;
				HSSFCell cell=firstRow.getCell(i);
				HSSFComment comment=cell.getCellComment();
				String fldName=(String) bean.get("fldName");
				if(cell.getStringCellValue().equals(bean.get("flddesc"))&&comment.getString().toString().equals(fldName)) {
					if(!"0".equals(bean.get("codesetId"))) {
						codedescToIDMap.put(fldName, getCodeItemid((String)bean.get("codesetId")).get(0));
						codeidToDescMap.put(fldName, getCodeItemid((String)bean.get("codesetId")).get(1));
						if(!endCodeMap.containsKey((String)bean.get("codesetId"))) {
							HashMap<String,String> codekeyMap=getEndCodeItem((String)bean.get("codesetId"));
							if(codekeyMap!=null)
								endCodeMap.put((String)bean.get("codesetId"),codekeyMap);
						}
							
					}
					i++;
				}else {
					throw new Exception(ResourceFactory.getProperty("template_new.subSetNotSame"));
				}	
				
			}
			//日期型格式：yyyy-mm-dd yyyy.mm.dd  yyyy/mm/dd 正则表达式校验格式是否正确日期是否合法
			String rexp = "^(((\\d{2}(([02468][048])|([13579][26]))[\\-]((((0?[13578])|(1[02]))[\\-]((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-]((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-]((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-]((((0?[13578])|(1[02]))[\\-]((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-]((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-]((0?[1-9])|(1[0-9])|(2[0-8]))))))|"  
	                + "((\\d{2}(([02468][048])|([13579][26]))[\\/]((((0?[13578])|(1[02]))[\\/]((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\/]((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\/]((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\/]((((0?[13578])|(1[02]))[\\/]((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\/]((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\/]((0?[1-9])|(1[0-9])|(2[0-8]))))))|"
					+"((\\d{2}(([02468][048])|([13579][26]))[\\.]((((0?[13578])|(1[02]))[\\.]((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\.]((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\.]((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\.]((((0?[13578])|(1[02]))[\\.]((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\.]((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\.]((0?[1-9])|(1[0-9])|(2[0-8]))))))|"			
					+"((\\d{2}(([02468][048])|([13579][26]))[\\年]((((0?[13578])|(1[02]))[\\月]((0?[1-9])|([1-2][0-9])|(3[01]))[\\日])|(((0?[469])|(11))[\\月]((0?[1-9])|([1-2][0-9])|(30))[\\日])|(0?2[\\月]((0?[1-9])|([1-2][0-9]))[\\日])))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\年]((((0?[13578])|(1[02]))[\\月]((0?[1-9])|([1-2][0-9])|(3[01]))[\\日])|(((0?[469])|(11))[\\月]((0?[1-9])|([1-2][0-9])|(30))[\\日])|(0?2[\\月]((0?[1-9])|(1[0-9])|(2[0-8]))[\\日])))))";  			
			Pattern patt=Pattern.compile(rexp);
			i=1;
			HashMap<String,String> map=null;
			for(;i<=1000;i++) {//解析从第2行至1001行数据
				HashMap fatherCodeCadeMap=new HashMap();
				HSSFRow row=sheet.getRow(i);
				if(row==null){ //如果客户删除行，导致最后拿到的row是null
					continue;
				}
				int j=0; 
				String recored="";
				HashMap<String,String> seachdescToIdMap=null;
				HashMap<String,String> seachidTodescMap=null;
				
				map=new HashMap<String, String>();
				for (MorphDynaBean bean : columnList) {
					String str="";
					if(!"2".equals((String)bean.get("fldPriv"))){
						 recored+=str+"`";
						 map.put((String)bean.get("fldName"), "");
						 continue; 
					 }
					if("attach".equals((String)bean.get("fldName"))) {//子集附件列保存
						recored+=str+"`";
						map.put((String)bean.get("fldName"), "");
						continue;
					}
					HSSFCell cell=row.getCell(j);
					if("D".equals(bean.get("fldType"))) {
						if(cell.getDateCellValue()!=null) {
							SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
							str=sdf.format(cell.getDateCellValue());
						}else {
							str="";
						}
					}else if("N".equals(bean.get("fldType"))) {
						if(cell.getCellType()!=HSSFCell.CELL_TYPE_BLANK) {//判断单元格是否为空
							if("0".equals(bean.get("format")))
								str=(int)cell.getNumericCellValue()+"";
							else
								str=cell.getNumericCellValue()+"";
						}
					} else {
						try{
							str=cell.getStringCellValue();
						}catch(Exception ex){
							String message = ex.getMessage();
							if(message.indexOf("Cannot get a STRING value from a NUMERIC cell")>-1){
								throw new Exception("字符型和数字型数据不匹配");
							}else{
								throw ex;
							}
						}
					}
					
					if("D".equals(bean.get("fldType"))) {
						int format=Integer.parseInt((String)bean.get("format"));
						if(format!=12||format!=13||format!=14||format!=15) {
							if(StringUtils.isNotEmpty(str)) {
								Matcher matcher=patt.matcher(str);
								if(!matcher.matches()){
									throw new Exception(ResourceFactory.getProperty("template_new.sourceData")+"("+bean.get("flddesc")+")"+ResourceFactory.getProperty("label.page.serial")+(i+1)+ResourceFactory.getProperty("template_new.rowData")+"："+str+ResourceFactory.getProperty("template_new.unFormat")+"<br>"+ResourceFactory.getProperty("template_new.theFormat")+"!");
								}else {
									recored+=str.replace("-", ".").replace("/", ".").replace("年",".").replace("月",".").replace("日","")+"`";//将导入的合法日期格式替换成yyyy.mm.dd 
									map.put((String)bean.get("fldName"), str.replace(".", "-").replace("/", "-").replace("年",".").replace("月",".").replace("日",""));
									map.put((String)bean.get("fldName")+"_D", format+"");
								}
							}else {
								recored+=str+"`";
								map.put((String)bean.get("fldName"), str);
							}
							
						}else {
							 // 12：年龄  | 13：1991（年）| 14：1 （月）| 15:23 （日） 
							if(StringUtils.isNotEmpty(str)) {//直接校验是否是数字
								if(!StringUtils.isNumeric(str))
									throw new Exception(ResourceFactory.getProperty("template_new.sourceData")+"(" +bean.get("flddesc")+")"+ResourceFactory.getProperty("label.page.serial") + (i + 1) +ResourceFactory.getProperty("template_new.rowData")+":" + str + ResourceFactory.getProperty("template_new.unFormat")+"!;");
								else {
									map.put((String)bean.get("fldName"), str);
									recored+=str+"`";
								}
							}else {
								map.put((String)bean.get("fldName"), str);
								recored+=str+"`";
							}
						}
					}else if("N".equals(bean.get("fldType"))) {
						if(StringUtils.isNotEmpty(str)) {
							if(!str.matches("-[0-9]+(.[0-9]+)?|[0-9]+(.[0-9]+)?"))
								throw new Exception(ResourceFactory.getProperty("template_new.sourceData")+
													"(" +bean.get("flddesc")+")"+
													ResourceFactory.getProperty("label.page.serial") + 
													(i + 1) + ResourceFactory.getProperty("template_new.rowData")+":" 
													+ str + ResourceFactory.getProperty("template_new.unFormat")+"!;");
						}
							map.put((String)bean.get("fldName"), str);
							recored+=str+"`";
					} else if(!"0".equals(bean.get("codesetId"))){
						String str_="";//bug 39559 存户代码名称，不存储啊代码值。
						if(StringUtils.isNotEmpty(str)) {
							if(str.indexOf(":")>-1) {
								str=str.replace(":","`");//map.put((String)bean.get("fldName"), str.replace(":", "`"));
								String[] codeStr = str.split("`");//8`研究生学历只选要提示研究生学历。
								String code="";
								if(codeStr.length>=2){
								   str_=codeStr[1];
								   code=codeStr[0];
								}
								String codeName = AdminCode.getCodeName((String)bean.get("codesetId"), code.trim());//判断代码值是否和代码项中的一样，不一样清空
								if(StringUtils.isBlank(codeName)||StringUtils.isNotBlank(codeName)&&!codeName.equalsIgnoreCase(str_)){
									str="";
									str_=str;
								}
							}else {
								seachdescToIdMap=codedescToIDMap.get((String)bean.get("fldName"));
								seachidTodescMap=codeidToDescMap.get((String)bean.get("fldName"));
								if(StringUtils.isNotEmpty(seachdescToIdMap.get(str))) {
									str_=str;
									str=seachdescToIdMap.get(str)+"`"+str;
								}else if(StringUtils.isNotEmpty(seachidTodescMap.get(str))) {
									str_=seachidTodescMap.get(str);
									str=str+"`"+seachidTodescMap.get(str);
								}else {
									str="";
									str_=str;
								}
							}
							str=str.replace(" ", "");
							if(StringUtils.isNotEmpty(str)) {
								if(endCodeMap.containsKey((String)bean.get("codesetId")))
								  if(!endCodeMap.get((String)bean.get("codesetId")).containsKey((String)bean.get("codesetId")+"subSet"+str.split("`")[0]))
									throw new Exception(ResourceFactory.getProperty("template_new.sourceData")+"("+bean.get("flddesc")+")"+ResourceFactory.getProperty("template_new.onlyChooseCode")+","+ResourceFactory.getProperty("label.page.serial")+(i+1)+ResourceFactory.getProperty("template_new.rowData")+"："+str_+ResourceFactory.getProperty("template_new.unChoose"));
								map.put((String)bean.get("fldName"),str);//代码项`代码名称
								str=str.split("`")[0];
							}else {
								map.put((String)bean.get("fldName"), "");
							}
							recored+=str+"`";
							
						}else {
							recored+=str+"`";
							map.put((String)bean.get("fldName"), str);
						}
					}else {//字符型指标 添加判断内容长度是否超出指标设置长度
						if(StringUtils.isNotEmpty(str)&&!(TemplateFuncBo.getStrLength(str)<=Integer.parseInt((String)bean.get("fldLength")))) {
							throw new Exception(ResourceFactory.getProperty("template_new.sourceData")+"("+bean.get("flddesc")+")"+
												ResourceFactory.getProperty("label.page.serial")+(i+1)+ResourceFactory.getProperty("template_new.rowData")+","+
												ResourceFactory.getProperty("template_new.contextLong"));
						}
						recored+=str+"`";
						map.put((String)bean.get("fldName"), str);
					}
					fatherCodeCadeMap.put(((String)bean.get("fldName")).toLowerCase(), str+":"+(String)bean.get("flddesc"));
					if(StringUtils.isNotBlank(str)&&imppeopleList.contains((String)bean.get("fldName"))){
                    	String[] personList = str.split("、");
                    	for(int iNum=0;iNum<personList.length;iNum++){
                    		String person=personList[iNum];
                    		String[] personInfo = person.split(":");
                    		if(personInfo.length!=2){
                                throw new Exception("源数据(" +bean.get("flddesc")+ ")第" + (i + 1) + "行中数据:"+person+"信息有误，导入失败!");
                    		}
                    		String a0101=personInfo[0];
                    		String onlyKeyValue=personInfo[1];
                    		String searchSql="";
                    		for(int dbnum=0;dbnum<dbList.size();dbnum++){
        		        		if(StringUtils.isNotBlank(searchSql)){
        		        			searchSql+=" UNION  ";
        		        		}
        		        		searchSql+="select '"+dbList.get(dbnum)+"' as pre,a0100 from "+dbList.get(dbnum)+"a01 where lower(a0101)=lower('"+a0101+"') and "+onlyname+"='"+onlyKeyValue+"'";
        		        	}
                    		try{
                        		rset=dao.search(searchSql);
                    		}catch(Exception ex){
                    			ex.printStackTrace();
                    			throw new Exception("源数据(" +bean.get("flddesc") + ")第" + (i + 1) + "行中数据:"+person+"信息有误，导入失败!");
                    		}
                    		if(!rset.next()){
                    			throw new Exception("源数据(" +bean.get("flddesc") + ")第" + (i + 1) + "行中数据:"+person+"信息有误，导入失败!");
                    		}
                    	}
					}
					j++;
				}
				 Iterator iterator = chidlFatherMap.entrySet().iterator();
	                StringBuffer cascadeerror = new StringBuffer("");
	                while(iterator.hasNext()){
	                	Map.Entry entry=(Entry) iterator.next();
	                	String key = (String) entry.getKey();
	                	String fatherId=(String) entry.getValue();
	                	String childValut = (String) fatherCodeCadeMap.get(key);
	                	String childFieldName="";
	                	if(StringUtils.isNotBlank(childValut)){
	                		String[] split = childValut.split(":");
	                		childValut=split[0];
	                		childFieldName=split[1];
	                	}
	                	String fatherValut = (String) fatherCodeCadeMap.get(fatherId);
	                	String fatherFieldName="";
	                	if(StringUtils.isNotBlank(fatherValut)){
	                		String[] split = fatherValut.split(":");
	                		fatherValut=split[0];
	                		fatherFieldName=split[1];
	                	}
	                	if(StringUtils.isNotBlank(childValut)&&fatherValut!=null){
	                		if("".equalsIgnoreCase(fatherValut)||fatherValut==null){
	                			cascadeerror.append("源数据在第" + (i+1 ) + "行中维护的"+fatherFieldName+"、"+childFieldName+"间未关联，请修改！<br>");//bug 43491 提示的错误行数不正确
	                		}else if(childValut.indexOf(fatherValut)!=0){
	                			cascadeerror.append("源数据在第" + (i+1) + "行中维护的"+fatherFieldName+"、"+childFieldName+"间未关联，请修改！<br>");
	                		}else if(childValut.equalsIgnoreCase(fatherValut)){
	                			cascadeerror.append("源数据在第" + (i+1) + "行中维护的"+fatherFieldName+"、"+childFieldName+"无关联关系，请修改！<br>");
	                		}
	                	}
	                }
	                if(cascadeerror.length()>0){
	                	throw new Exception(cascadeerror.toString());
	                }
				if(recored.length()>columnList.size()) {//某一行数据全部为空时 不添加
					recoredList.add(recored);
				String recored_key_id= this.userView.getUserName()+System.currentTimeMillis()+(Math.round(Math.random()*100)*Math.round(Math.random()*100));//生成子集唯一值
					map.put("I9999","-1");
					map.put("canEdit","true");
				map.put("record_key_id",recored_key_id);
					recoredMapList.add(map);
				recoredKeyIdList.add(recored_key_id);
				}
			
			}
		} catch (Exception e) {
			throw e;
		}
		finally {
			PubFunc.closeDbObj(rset);
		}
		list.add(recoredList);//存储xml格式内容
		list.add(recoredMapList);//存储放置前台数据
		list.add(recoredKeyIdList);//存储子集记录唯一值
		return list;
	}
	
	//根据人事异动子集指标 导出excel模板
	private String createExcel(String fieldSet,ArrayList<MorphDynaBean> columnList) throws Exception {
		FileOutputStream fileOut=null;
		FieldSet set=DataDictionary.getFieldSetVo(fieldSet);
		String fileName=this.userView.getUserName()+"_"+set.getFieldsetdesc()+"_template.xls";
		try {
			if(StringUtils.isNotEmpty(fieldSet)) {
				ArrayList<HashMap<String, String>> fieldlist=new ArrayList<HashMap<String,String>>();
				HashMap<String,String> map=null;
				for (MorphDynaBean fieldItem : columnList) {
					 if(!"2".equals((String)fieldItem.get("fldPriv"))){
						 continue; 
					 }
					 if("attach".equals((String)fieldItem.get("fldName")))
						continue;
					map=new HashMap<String, String>();
					String itemid=(String)fieldItem.get("fldName");
					String text=(String)fieldItem.get("flddesc");
					FieldItem item = DataDictionary.getFieldItem(itemid,fieldSet);
					if(item==null) {
						throw new Exception(ResourceFactory.getProperty("system.param.sysinfosort.subset")+text+ResourceFactory.getProperty("template_new.subSetFieldNotFund"));
					}

					map.put("text",text);
					map.put("itemid", itemid);
					map.put("codesetid", item.getCodesetid());
					map.put("itemtype",item.getItemtype());
					map.put("format",(String)fieldItem.get("format"));
					map.put("width",(String)fieldItem.get("fldWidth"));
					fieldlist.add(map);
				}
				
				HSSFWorkbook wb=new HSSFWorkbook();
				HSSFSheet sheet=wb.createSheet(set.getFieldsetdesc());
				HSSFRow  row=sheet.createRow(0);
				HSSFComment comm = null;
				row.setHeight((short)800);
				
				HSSFCellStyle titleStyle=null;
				//表头
				FillPatternType fillPattern = FillPatternType.SOLID_FOREGROUND;
				short fillForegroundColor = IndexedColors.LIGHT_GREEN.index;
				int i=0;
				for (HashMap<String, String> fieldMap : fieldlist) {
					titleStyle=getCellStyle(wb);
					titleStyle.setFillPattern(fillPattern);
					titleStyle.setFillForegroundColor(fillForegroundColor);
					titleStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));
					HSSFCell cell=row.createCell(i);
					cell.setCellValue(fieldMap.get("text"));
					cell.setCellStyle(titleStyle);
					setCellComment(sheet, comm, cell, i, fieldMap.get("itemid"));
					sheet.setColumnWidth(i, Integer.parseInt(fieldMap.get("width"))*50);
					i++;
				}
				
				//表体代码型指标查询数据
				 HashMap<Object, Object> codeMapList=getCodeItemDescList(fieldlist);
				//表体
				HSSFCellStyle dateCellStyle=getCellStyle(wb);
			    HSSFCellStyle cellStyle=getCellStyle(wb);
				cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));
		        CreationHelper creationHelper = wb.getCreationHelper();  
				dateCellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("yyyy-MM-dd") );
//				dateCellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));
				//------数值型单元格样式 start
				HSSFCellStyle N0Style=getCellStyle(wb);
				N0Style.setDataFormat(getNumberDataFormat(wb,0));
				
				HSSFCellStyle N1Style=getCellStyle(wb);
				N1Style.setDataFormat(getNumberDataFormat(wb,1));
				
				HSSFCellStyle N2Style=getCellStyle(wb);
				N2Style.setDataFormat(getNumberDataFormat(wb,2));
				
				HSSFCellStyle N3Style=getCellStyle(wb);
				N3Style.setDataFormat(getNumberDataFormat(wb,3));
				
				HSSFCellStyle N4Style=getCellStyle(wb);
				N4Style.setDataFormat(getNumberDataFormat(wb,4));
				
				HSSFCellStyle N5Style=getCellStyle(wb);
				N5Style.setDataFormat(getNumberDataFormat(wb,5));
				
				HSSFCellStyle N6Style=getCellStyle(wb);
				N6Style.setDataFormat(getNumberDataFormat(wb,6));
				//------end
				HSSFRow bodyRow=null;
				i=1;
				for(;i<=1000;i++) {
					bodyRow=sheet.createRow(i);
					bodyRow.setHeight((short)500);
					int j=0;
					for (HashMap<String, String> fieldMap : fieldlist) {
						HSSFCell cell=bodyRow.createCell(j);
						
						if("D".equalsIgnoreCase(fieldMap.get("itemtype"))) {
							cell.setCellStyle(dateCellStyle);
						}else if("N".equals(fieldMap.get("itemtype"))){
							int format=Integer.parseInt(fieldMap.get("format"));
							switch (format) {
									case 1:
										cell.setCellStyle(N1Style);
										break;
									case 2:
										cell.setCellStyle(N2Style);
									    break;
									case 3:
										cell.setCellStyle(N3Style);
									    break;
									case 4:
										cell.setCellStyle(N4Style);
									    break;
									case 5:
										cell.setCellStyle(N5Style);
									    break;
									case 6:
										cell.setCellStyle(N6Style);
									    break;
									default:
										cell.setCellStyle(N0Style);
										break;
								}
						}else {
							cell.setCellStyle(cellStyle);
						}
						
						sheet.setColumnWidth(j, Integer.parseInt(fieldMap.get("width"))*50);
						if(i==1) {//只循环一遍 给代码指标列设置下拉
							if(!"0".equals(fieldMap.get("codesetid"))) {
								String[] codeArry=(String[])codeMapList.get(fieldMap.get("codesetid"));
								setHSSFValidation(wb,sheet, codeArry, i, 1000, j, j);
							}else if("D".equalsIgnoreCase(fieldMap.get("itemtype"))) {
								setDataValidation(sheet, i, 1000, j,j);
							}else if("N".equalsIgnoreCase(fieldMap.get("itemtype"))) {
								setNumberCellValida(sheet, i, 1000, j,j,fieldMap.get("format"));
							}
						}
						j++;
					}
				}
				
				
				
				File file=new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+ fileName);
				if(file.exists()) {
					file.delete();
				}
				fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+ fileName);
				wb.write(fileOut);
				fileOut.flush();
				
			}
			
			
		} catch (Exception e) {
			throw e;
		}finally {
			PubFunc.closeIoResource(fileOut);
		}
		
		return fileName;
	}
	/***
	 * 设置数值型指标代码精度
	 * */
	private short  getNumberDataFormat(HSSFWorkbook wb,int len) {
		StringBuffer decimal = new StringBuffer("0");
		if (len > 0)
		    decimal.append(".");
		for (int i = 0; i < len; i++)
		{
		    decimal.append("0");
		}
		decimal.append("_ ");
		HSSFDataFormat format=wb.createDataFormat();
		return format.getFormat(decimal.toString());
	}
	
	/***
	 * 末端代码查询
	 * */
	private HashMap<String,String> getEndCodeItem(String codesetid) throws Exception{
		ContentDAO dao=new ContentDAO(this.frameconn);
		RowSet rs=null;
		HashMap<String,String> map=new HashMap<String, String>();
		try {
				String sql="SELECT  cm.codesetid,cm.codeitemid ,cm.codeitemdesc FROM codeitem cm LEFT JOIN ( SELECT  COUNT(1) AS num ,codesetid,parentid FROM    codeitem WHERE codeitemid<>parentid and  "+Sql_switcher.sqlNow()+" BETWEEN start_date AND end_date   GROUP BY parentid ,codesetid)  cnum ON cm.codesetid = cnum.codesetid AND cm.codeitemid = cnum.parentid left join codeset c on cm.codesetid=c.codesetid WHERE "+Sql_switcher.isnull("cnum.num", "0")+"= 0 and upper(cm.codesetid) in('"+ codesetid + "') and "+Sql_switcher.isnull("c.leaf_node","0")+"='1'  ";
				rs=dao.search(sql);
				if(rs.next()) {
					do {
						map.put(rs.getString("codesetid")+"subSet"+rs.getString("codeitemid"),rs.getString("codeitemdesc"));
					   }while(rs.next());
					
				}else {
					return null;
				}
			
		} catch (Exception e) {
			throw e;
		}finally {
			PubFunc.closeDbObj(rs);
		}
		return map;
	}
 
	/***
	 * 查询代码指标下拉内容
	 * */
	private HashMap<Object, Object> getCodeItemDescList(ArrayList<HashMap<String, String>> fieldlist) throws Exception{
		HashMap<Object, Object> maplist=new HashMap<Object, Object>();//存放代码型指标 codesetId:codelist
		try {
			for (HashMap<String, String> map : fieldlist) {
				if(!"0".equals(map.get("codesetid"))) {
					maplist.put(map.get("codesetid"), this.getCodeItemDesc("", map.get("codesetid")));
				}
			}
			
		} catch (Exception e) {
			throw e;
		}
		return maplist;
	}
	//设置日期型单元格有效性校验
	private HSSFSheet setDataValidation(HSSFSheet sheet,int firstRow, int endRow, int firstCol,  
            int endCol)throws Exception {
		try {
			CellRangeAddressList addressList = new CellRangeAddressList(firstRow,endRow,firstCol, endCol);  
	        DVConstraint dvConstraint = DVConstraint.createDateConstraint(DVConstraint.OperatorType.BETWEEN, "1900-01-01",  
	                "5000-01-01", "yyyy-mm-dd");  
	        
	        HSSFDataValidation dataValidation = new HSSFDataValidation(addressList, dvConstraint);  
	        dataValidation.setSuppressDropDownArrow(false);  
	        dataValidation.createPromptBox("输入提示", "请填写日期格式");  
	        // 设置输入错误提示信息  
	        dataValidation.createErrorBox("日期格式错误提示", "你输入的日期格式不符合'yyyy-mm-dd'格式规范，请重新输入！");  
	        dataValidation.setShowPromptBox(true);  
			sheet.addValidationData(dataValidation);
		} catch (Exception e) {
			throw e;
		}
		return sheet;
	}
	
	private void setNumberCellValida(HSSFSheet sheet,int firstRow, int endRow, int firstCol,  
            int endCol,String format)throws Exception {
		try {
			DVConstraint dvConstraint = null;
			if("0".equals(format)) {
				dvConstraint=DVConstraint.createNumericConstraint(DVConstraint.ValidationType.INTEGER,  
						DVConstraint.OperatorType.BETWEEN, "-2147483648", "2147483647");  
			}else {
				dvConstraint=DVConstraint.createNumericConstraint(DVConstraint.ValidationType.DECIMAL,  
						DVConstraint.OperatorType.BETWEEN, "-2147483648", "2147483647");  
			}
		        // 设定在哪个单元格生效  
			   CellRangeAddressList addressList  = new CellRangeAddressList(firstRow, endRow, firstCol, endCol);  
		        HSSFDataValidation dataValidation = new HSSFDataValidation(addressList, dvConstraint);  
		        dataValidation.setSuppressDropDownArrow(false);  
		        // 设置输入错误提示信息  
		        if("0".equals(format)) {
		        	dataValidation.createPromptBox("输入提示","输入的内容必须为整型");
		        	dataValidation.createErrorBox("格式错误提示", "输入的数值格式必须为整型，请重新输入！");  
		        }else {
		        	dataValidation.createPromptBox("输入提示","输入的内容必须为数值型");
		        	dataValidation.createErrorBox("格式错误提示", "输入的内容必须为数值型，请重新输入！");  
		        }
		        dataValidation.setShowPromptBox(true);  
				sheet.addValidationData(dataValidation);
		} catch (Exception e) {
			throw e;
		}
	}
	
	/****
	 * 单元格添加下拉  单列为下拉列表值超过255会有问题 将下拉内容放置182列后 设置对应列的宽度为0隐藏
	 * */
	 private  HSSFSheet setHSSFValidation(HSSFWorkbook wb,HSSFSheet sheet,  
	            String[] textlist, int firstRow, int endRow, int firstCol,  
	            int endCol) {  
			 HSSFCell cell = null;
			 String[] lettersUpper ={ "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
			 int div = 0;
			 int mod = 0;	
			for(int i=0;i<textlist.length;i++) {
			  HSSFRow row=sheet.getRow(2000+i);
				  if(row==null)
					row=sheet.createRow(2000+i);
			 cell=row.createCell(182+firstCol);
			 cell.setCellValue(new HSSFRichTextString(textlist[i]));
			 
			} 
			div = firstCol/26;
			mod = firstCol%26;
			String strFormula = "$" +lettersUpper[6+div]+ lettersUpper[mod] + "$2000:$"+lettersUpper[6+div]+  lettersUpper[mod] + "$" + (2000+textlist.length);
	        // 加载下拉列表内容  
	        DVConstraint constraint = DVConstraint.createFormulaListConstraint(strFormula);  
	        // 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列  
	        CellRangeAddressList regions = new CellRangeAddressList(firstRow,endRow, firstCol, endCol);  
	        // 数据有效性对象  
	        HSSFDataValidation data_validation_list = new HSSFDataValidation(regions, constraint);  
	        sheet.addValidationData(data_validation_list);  
	        sheet.setColumnWidth(182+firstCol, 0);
	        return sheet;  
	    } 
	
	/****
	 * 
	 * 根据查询的代码组装  codeitemid:codeitemdesc
	 * */
	private String[] getCodeItemDesc(String itemid,String codesetId) throws Exception {
		String[] codeDescArry=null;
		try {
			ArrayList<LazyDynaBean> beanList= getCodeitemId(codesetId, "");
			codeDescArry=new String[beanList.size()];
			int i=0;
			for (LazyDynaBean bean : beanList) {
				codeDescArry[i]=bean.get("codeitemid")+":"+bean.get("codeitemdesc");
				i++;
			}
		} catch (Exception e) {
			throw e;
		}
		
		
		return codeDescArry;
	}
	/**
	 * 根据代码项名称 查询代码  codeitemid:codeitemdesc;codeitemdesc:codeitemid
	 * */
	private ArrayList<HashMap<String,String>> getCodeItemid(String codesetId) throws Exception{
		ArrayList<HashMap<String,String>> list=new ArrayList<HashMap<String,String>>();
		HashMap<String,String> descToid=new HashMap<String,String>();
		HashMap<String,String> idTodesc=new HashMap<String,String>();
		try {
			ArrayList<LazyDynaBean> beanList= getCodeitemId(codesetId, "");
			for (LazyDynaBean bean : beanList) {
				descToid.put((String)bean.get("codeitemdesc"),((String)bean.get("codeitemid")).replace(" ", ""));
				idTodesc.put(((String)bean.get("codeitemid")).replace(" ", ""),(String)bean.get("codeitemdesc"));
				
			}
			list.add(descToid);
			list.add(idTodesc);
		} catch (Exception e) {
			throw e;
		}
		return list;
	}
	
	
	
	/**
	 * 表列添加批注
	 * @param sheet
	 * @param patr
	 * @param comm
	 * @param cell
	 * @param i  列下标
	 * @param text 批注内容
	 */
	private void setCellComment(HSSFSheet sheet,HSSFComment comm,HSSFCell cell,int i,String text) {
		HSSFPatriarch patr = sheet.createDrawingPatriarch();
		comm = patr.createComment(new HSSFClientAnchor(0, 0, 0, 1, (short) (i + 1), 0, (short) (i + 2), 1));
		comm.setString(new HSSFRichTextString(text));
		cell.setCellComment(comm);
		
	}
	//设置单元格字体
	private HSSFCellStyle getCellStyle(HSSFWorkbook wb) {
		HSSFCellStyle cellstyle = wb.createCellStyle();
		
		HSSFFont font=wb.createFont();//宋体 16
		font.setFontHeightInPoints((short)10);
		font.setFontName("宋体");
		
        cellstyle.setAlignment(HorizontalAlignment.CENTER);//表头居中
        cellstyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellstyle.setBorderBottom(BorderStyle.THIN);
        cellstyle.setBorderTop(BorderStyle.THIN);
        cellstyle.setBorderLeft(BorderStyle.THIN);
        cellstyle.setBorderRight(BorderStyle.THIN);
        cellstyle.setFont(font);
        cellstyle.setLocked(false);
        return cellstyle;
	}
	
	/***
	 * 
	 * 获取代码项对应信息
	 * */
	private ArrayList<LazyDynaBean> getCodeitemId(String codesetid,String fieldName) throws Exception{
		ContentDAO dao=new ContentDAO(this.frameconn);
		RowSet rs=null;
		ArrayList<LazyDynaBean> list=new ArrayList<LazyDynaBean>();
		try {
			String sql="";
			String countSql="";
			boolean flag=false;
			if("UM".equalsIgnoreCase(codesetid)||"UN".equalsIgnoreCase(codesetid)||"@K".equalsIgnoreCase(codesetid)) {
				if("UN".equalsIgnoreCase(codesetid)) {
					sql="select codesetid,codeitemid,codeitemdesc,parentid,childid,grade layer from organization where codesetid='UN' and codeitemid=parentid  and " + Sql_switcher.sqlNow() + " between start_date and end_date order by a0000,codeitemid";
					countSql="select count(*) from organization where codesetid='UN'  and " + Sql_switcher.sqlNow() + " between start_date and end_date";
				}else if("UM".equalsIgnoreCase(codesetid)) {
					sql="select codesetid,codeitemid,codeitemdesc,parentid,childid,grade layer from organization where codesetid in ('UN','UM') and codeitemid=parentid  and " + Sql_switcher.sqlNow() + " between start_date and end_date order by a0000,codeitemid";
					countSql="select count(*) from organization where codesetid in ('UN','UM')  and " + Sql_switcher.sqlNow() + " between start_date and end_date";
				}else {
					sql="select codesetid,codeitemid,codeitemdesc,parentid,childid,grade layer from organization where codesetid in ('UN','UM','@K') and codeitemid=parentid  and " + Sql_switcher.sqlNow() + " between start_date and end_date order by a0000,codeitemid ";
					countSql="select count(*) from organization where codesetid in ('UN','UM','@K')  and " + Sql_switcher.sqlNow() + " between start_date and end_date";
				}
			}else {
				
				 rs=dao.search("select validateflag from codeset where codesetid='"+codesetid+"'");
				 if(rs.next()) {
					 if("1".equals(rs.getString("validateflag"))) {
						 flag=true;
					 }
				 }
				 sql="select codeitemid,codeitemdesc,parentid,childid,layer from codeitem where codeitemid=parentid and codesetid='"+codesetid+"' "+(flag?"":"and invalid='1'")+" and " + Sql_switcher.sqlNow() + " between start_date and end_date  order by a0000,codeitemid asc";
				 countSql="select count(*) from codeitem where codeitemid=parentid and codesetid='"+codesetid+"' "+(flag?"":"and invalid='1'")+" and " + Sql_switcher.sqlNow() + " between start_date and end_date";

			}
			rs=dao.search(countSql);
			int count=0;
			if(rs.next()) {
				count=rs.getInt(1);
			}
			if(count!=0) {
				rs=dao.search(sql);
				while(rs.next()) {
					LazyDynaBean bean=new LazyDynaBean();
					bean.set("codeitemid", " "+rs.getString("codeitemid"));
					bean.set("codeitemdesc", rs.getString("codeitemdesc"));
					bean.set("parentid", rs.getString("parentid"));
					bean.set("childid", rs.getString("childid"));
					list.add(bean);
					getChildList(codesetid,  rs.getString("codeitemid"), list,flag);
				}
			}
			
		} catch (Exception e) {
			throw e;
		}finally {
			PubFunc.closeDbObj(rs);
		}
		return list;
	}
	/***
	 * 获取子节点内容
	 * */
	private ArrayList<LazyDynaBean> getChildList(String codesetid,String codeitemid,ArrayList<LazyDynaBean> list,boolean flag) throws Exception{
		ContentDAO dao=new ContentDAO(this.frameconn);
		RowSet rs=null;
		try {
			String sql="";
			if("UM".equalsIgnoreCase(codesetid)||"UN".equalsIgnoreCase(codesetid)||"@K".equalsIgnoreCase(codesetid)) {
		        if(!"UN".equalsIgnoreCase(codesetid)){
		            if("UM".equalsIgnoreCase(codesetid)){
		               sql="select codesetid,codeitemid,codeitemdesc,parentid,childid,grade layer from organization where codesetid in ('UN','UM')  and parentid='"+codeitemid+ "'and parentid <> codeitemid  and "+Sql_switcher.sqlNow()+" between start_date and end_date order by a0000,codeitemid ";              
		            }else{
		                sql="select codesetid,codeitemid,codeitemdesc,parentid,childid,grade layer from organization where codesetid in ('UN','UM','@K')  and parentid='"+codeitemid+ "'and parentid <> codeitemid  and "+Sql_switcher.sqlNow()+" between start_date and end_date order by a0000,codeitemid ";
		            }
		        }else{
		            sql="select codesetid,codeitemid,codeitemdesc,parentid,childid,grade layer from organization where parentid='"+codeitemid+ "' and codesetid='UN' and parentid <> codeitemid  and "+Sql_switcher.sqlNow()+" between start_date and end_date order by a0000,codeitemid";  
		        }
		         
		    }else {
				 sql="select codeitemid,codeitemdesc,parentid,childid,layer from codeitem where codeitemid<>parentid and codesetid='"+codesetid+"' and parentid='"+codeitemid+"' "+(flag?"":"and invalid='1'")+" and " + Sql_switcher.sqlNow() + " between start_date and end_date  order by a0000,codeitemid asc";
			}

			rs=dao.search(sql);
			while(rs.next()) {
			    LazyDynaBean bean=new LazyDynaBean();
			    int layer=rs.getInt("layer");
			    String str="";
			    for(int i=0;i<layer;i++) {
			    	str+=" ";
			    }
			    bean.set("codeitemid", str+rs.getString("codeitemid"));
				bean.set("codeitemdesc",rs.getString("codeitemdesc"));
				bean.set("parentid", rs.getString("parentid"));
				bean.set("childid", rs.getString("childid"));
				list.add(bean);
				if(!rs.getString("codeitemid").equals(rs.getString("childid"))) {
					getChildList(codesetid, rs.getString("codeitemid"), list,flag);
				 }
			}
		

		} catch (Exception e) {
			throw e;
		}finally {
			PubFunc.closeDbObj(rs);
		}
		return list;
	}
	
	

}
