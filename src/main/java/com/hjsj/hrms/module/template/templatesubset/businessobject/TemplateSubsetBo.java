package com.hjsj.hrms.module.template.templatesubset.businessobject;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hjsj.hrms.businessobject.general.template.TFieldFormat;
import com.hjsj.hrms.businessobject.general.template.TSubSetDomain;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.module.template.templatecard.businessobject.AttachmentBo;
import com.hjsj.hrms.module.template.utils.TemplateUtilBo;
import com.hjsj.hrms.module.template.utils.javabean.SubField;
import com.hjsj.hrms.module.template.utils.javabean.SubSetDomain;
import com.hjsj.hrms.module.template.utils.javabean.TemplateParam;
import com.hjsj.hrms.module.template.utils.javabean.TemplateSet;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import com.hrms.virtualfilesystem.VfsFileEntity;
import com.hrms.virtualfilesystem.service.VfsCategoryEnum;
import com.hrms.virtualfilesystem.service.VfsFiletypeEnum;
import com.hrms.virtualfilesystem.service.VfsModulesEnum;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.Map.Entry;

/**
 * 用于获取子集信息
 */
public class TemplateSubsetBo {

	private Connection conn;
	private UserView userview;

	private String tabid="";
	/**列表中子集对应的列*/
	private String columnName="";
	private SubSetDomain  subDomain=null;

    /** 区分报审、报备、加签
     * 1：报审 2：加签  3 报备
     *  报审：按照模板设置的指标显示。
     *  浏览打印：按照数据表保存的xml显示指标。 目前未做，以后考虑，需从数据xml解析指标，单指标设置取不到。
    */
    private String approveFlag="0";
    /*
    public TemplateSubsetBo(Connection conn,UserView userview,int tabid){
        this.conn=conn;
        this.userview=userview;
        this.tabid = tabid + "";
    }
    */

    public TemplateSubsetBo(Connection conn,UserView userview,String tabid,String columnName){
        this.conn=conn;
        this.userview=userview;
        this.tabid=tabid;
        this.columnName=columnName;
        this.approveFlag="1";
        initSub_domain();
    }
    /*
    public TemplateSubsetBo(Connection conn,UserView userview,String tabid,
            String columnName,String sub_domain){
        this.conn=conn;
        this.userview=userview;
        this.tabid=tabid;
        this.columnName=columnName;
        subDomain = new SubSetDomain(sub_domain);
    }
    */
    public TemplateSubsetBo(Connection conn,UserView userview,String tabid,
            String columnName,String sub_domain,String approveFlag){
        this.conn=conn;
        this.userview=userview;
        this.tabid=tabid;
        this.columnName=columnName;
        this.approveFlag = approveFlag;
        subDomain = new SubSetDomain(sub_domain);
    }


	/**
	 * 根据模板号和子集表名获取template_set表中对应的子集表头信息
	 * @param tabid 模板号
	 * @param columnName 子集单元格对应的子集表名
	 * @return
	 */
	private void initSub_domain(){
		String sub_domain="";
		String sub_domain_id="";
		String setName="";
		String chgState="";
		if(this.columnName.toLowerCase().startsWith("t_")){
			int start=this.columnName.indexOf("_");
			int end=this.columnName.lastIndexOf("_");
			if(end>start){
				setName = this.columnName.substring(start+1, end);
				chgState = this.columnName.substring(end+1);
				end=setName.lastIndexOf("_");
				if (end>0){
					sub_domain_id=setName.substring(end+1,setName.length());
					setName=setName.substring(0,end);
				}
			}
		}
		try {
			ContentDAO dao=new ContentDAO(this.conn);
			String sql="select sub_domain,hz  from Template_Set where TabId=? and subflag='1' and upper(setName)=? and ChgState=?";
			RowSet rset = dao.search(sql.toString(),Arrays.asList(new Object[] {Integer.valueOf(this.tabid),setName.toUpperCase(),chgState}));
			while(rset.next()){
				sub_domain=rset.getString("sub_domain");

				subDomain = new SubSetDomain(sub_domain);
				if (sub_domain_id.equals(subDomain.getSubDomainId())){
					break;
				}

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据人员编号和人员库查找数据表中的某个子集对应的数据
	 * @param tablename 要查询的数据表名
	 * @param columnName 要查询的字段
	 * @param a0100 人员编号
	 * @param basepre 人员库
	 * @return
	 * @throws GeneralException
	 */
	public String getSub_dataXml(String tablename,String objectid,String tabid,String ins_id) throws GeneralException{
		String Sub_dataXml="";
		RowSet rset = null;
		try {
			ContentDAO dao=new ContentDAO(this.conn);
			ArrayList paramList=new ArrayList();
			TemplateParam param = new TemplateParam(this.conn,this.userview,Integer.valueOf(tabid));
			StringBuffer sql = new StringBuffer("select "+this.columnName+" from "+tablename+" where ");
			if(param.getInfor_type() == 1){// =1 人员模板
				String[] arr = objectid.split("`");
				String basepre = arr[0];
				String a0100 = arr[1];
				sql.append("a0100=? and BasePre=? ");
				paramList.add(a0100);
				paramList.add(basepre);
			}else if(param.getInfor_type() == 2){//2 单位模板
				sql.append("B0110=? ");
				paramList.add(objectid);
			}else if(param.getInfor_type() == 3){//3 岗位模板
				sql.append("E01A1=? ");
				paramList.add(objectid);
			}
			if(StringUtils.isNotBlank(ins_id)&&!"0".equals(ins_id))
			{
				sql.append(" and ins_id=?");
				paramList.add(Integer.valueOf(ins_id));
			}
			rset = dao.search(sql.toString(),paramList);
			if(rset.next())
			{
				Sub_dataXml=Sql_switcher.readMemo(rset,this.columnName);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(rset);
		}
		return Sub_dataXml;
	}

	public void clearTempFile(String old_subXml, String data) throws Exception {
		try {
			ArrayList fieldidList = new ArrayList();
			if (StringUtils.isNotEmpty(old_subXml)) {
				Document doc = PubFunc.generateDom(old_subXml);
				Element records = doc.getRootElement();
				String columns = records.getAttributeValue("columns");
				if (columns.indexOf("attach") > -1) {
					int attach_index = Arrays.asList(columns.split("`")).indexOf("attach");
					List<Element> record = records.getChildren();
					for (Element el : record) {
						String[] values = el.getValue().split("`", -1);
						if (values[attach_index].trim().isEmpty()) {
							continue;
						}
						String[] attach_value=values[attach_index].split(",", -1);
						for(String value : attach_value) {
							if(StringUtils.isEmpty(value)) {
								continue;
							}
							fieldidList.add(value.split("\\|", -1)[1]);
						}

					}

					doc = PubFunc.generateDom(data);
					Element records_new = doc.getRootElement();
					List<Element> record_new = records_new.getChildren();
					for (Element el : record_new) {
						String[] values = el.getValue().split("`", -1);
						if (values[attach_index].trim().isEmpty()) {
							continue;
						}

						String[] attach_value=values[attach_index].split(",", -1);
						for(String value : attach_value) {
							if(StringUtils.isEmpty(value)) {
								continue;
							}
							if (fieldidList.contains(value.split("\\|", -1)[1])) {
								fieldidList.remove(value.split("\\|", -1)[1]);
							}
						}


					}
					for (Object obj : fieldidList) {
						String field_ = (String) obj;
						// 已删除附件的 执行删除操作
						VfsFileEntity enty = VfsService.getFileEntity(field_);
						// 人事异动模块允许删除
						if (enty.getModuleid().equals(VfsModulesEnum.RS.toString())) {
							// 人事异动提交表单的附件不删除
							if (StringUtils.isEmpty(enty.getFiletag())||!enty.getFiletag().equals(VfsModulesEnum.YG.toString())) {
								VfsService.deleteFile(this.userview.getUserName(), field_);
							}
						}

					}
				}
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据人员编号和人员库查找数据表中的某个子集对应的数据
	 * @param tablename 要查询的数据表名
	 * @param columnName 要查询的字段
	 * @param a0100 人员编号
	 * @param basepre 人员库
	 * @param taskid 任务号
	 * @return
	 */
	public boolean saveSub_dataXml(String tablename,String columnName,String objectid,String tabid,String xmldata,String ins_id){
		String Sub_dataXml="";
		try {
			ContentDAO dao=new ContentDAO(this.conn);
			ArrayList paramList=new ArrayList();
			TemplateParam param = new TemplateParam(this.conn,this.userview,Integer.valueOf(tabid));
			StringBuffer sql = new StringBuffer("update "+tablename+" set "+columnName+"=? where ");
			paramList.add(xmldata);
			//保存子集 lis edit 20160831 start
			if(param.getInfor_type() == 1){// =1 人员模板
				String[] arr = objectid.split("`");
				String basepre = arr[0];
				String a0100 = arr[1];
				sql.append("a0100=? and BasePre=? ");
				paramList.add(a0100);
				paramList.add(basepre);
			}else if(param.getInfor_type() == 2){//2 单位模板
				sql.append("B0110=? ");
				paramList.add(objectid);
			}else if(param.getInfor_type() == 3){//3 岗位模板
				sql.append("E01A1=? ");
				paramList.add(objectid);
			}
			//end
			if(ins_id!=null&&!"".equals(ins_id)&&!"0".equalsIgnoreCase(ins_id))
			{
				sql.append(" and ins_id=?");
				paramList.add(Integer.valueOf(ins_id));
			}
			int num= dao.update(sql.toString(),paramList);
			if(num>0){
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 根据模板定义得到指定格式的显示数据
	 * @param Sub_dataXml 子集存储的数据
	 * @param Xml_param 子集定义的格式
	 * @return
	 */
	public HashMap getSubDataMap(String Sub_dataXml){
		HashMap SubDataMap =new HashMap();  //将Sub_dataXml解析后得到的数据存储在map集合中
		String multimedia_file_flag = "0";
		ConstantXml constantXml = new ConstantXml(this.conn,"FILEPATH_PARAM");
        String rootdir = constantXml.getNodeAttributeValue("/filepath", "rootpath");
        rootdir=rootdir.replace("\\",File.separator);
        if (!rootdir.endsWith(File.separator)) rootdir =rootdir+File.separator;
        rootdir += "multimedia"+File.separator;
		try{
			if (!"".equals(Sub_dataXml) && Sub_dataXml!=null){
				Sub_dataXml = PubFunc.keyWord_reback(Sub_dataXml);
				Sub_dataXml=Sub_dataXml.replace("&", "＆");
				Sub_dataXml=Sub_dataXml.replace("^", "＾");
				Document doc=PubFunc.generateDom(Sub_dataXml);;
				Element eleRoot=null;  //xml解析得到/records对象
				Element element=null;  //xml解析得到/record对象
				XMLOutputter outputter = new XMLOutputter();
				Format format = Format.getPrettyFormat();
				format.setEncoding("UTF-8");
				outputter.setFormat(format);
				String xpath="/records";
				XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
				eleRoot =(Element) findPath.selectSingleNode(doc);
				if(eleRoot!=null){
					String columns=eleRoot.getAttributeValue("columns").toUpperCase(); //得到xml中columns中对应的值
					if(columns.toLowerCase().indexOf("attach")!=-1)
						multimedia_file_flag = this.checkAttach();
					List recordList=eleRoot.getChildren("record");
					ArrayList dataList=new ArrayList();  //存储每一条record对应的值和属性
					if(recordList!=null&&recordList.size()>0)
					{
						for(int i=0;i<recordList.size();i++)
						{
							//存储record中的属性和值，先将contentValue的值存储为i，得到最终的值后再进行替换
							HashMap recordMap=new HashMap();
							element=(Element)recordList.get(i);
							String I9999=element.getAttributeValue("I9999");
							recordMap.put("I9999", I9999);
							String state=element.getAttributeValue("state");
							recordMap.put("state", state);
							String edit = element.getAttributeValue("edit")==null?"1":element.getAttributeValue("edit");
							recordMap.put("edit", edit);
							String timestamp = element.getAttributeValue("timestamp")==null?"":element.getAttributeValue("timestamp");
							if("-1".equals(I9999)) {
								recordMap.put("timestamp", timestamp);
							}
							Date date=new Date();
							String record_key_id = element.getAttributeValue("record_key_id")==null?this.userview.getUserName()+System.currentTimeMillis()+(Math.round(Math.random()*100)*Math.round(Math.random()*100)):element.getAttributeValue("record_key_id");
							recordMap.put("record_key_id", record_key_id);
							String isHaveChange = element.getAttributeValue("isHaveChange")==null?"false":"true";
							recordMap.put("isHaveChange", isHaveChange);
							String contentValue=element.getValue();
							//存储column和其对应的值，lineNum为当前record的条数
							HashMap dataMap=new HashMap();
							if(contentValue!=null&&contentValue.length()>0)
							{
								String[] valueArr=contentValue.split("`",-1);
								String[] columnArr=columns.split("`");
								for(int j=0;j<columnArr.length;j++){
									String value = "";
									if("0".equals(multimedia_file_flag)&&columnArr[j].toLowerCase().indexOf("attach")!=-1)//不支持附件
										continue;
									else if("1".equals(multimedia_file_flag)&&columnArr[j].toLowerCase().indexOf("attach")!=-1){
										if(j<valueArr.length) {
											value= valueArr[j];
											if("".equals(value.trim())){
	                                        	value = "";
	                                        }
										}
										if(StringUtils.isNotBlank(value)){
											String [] valuearr = value.split(",");
											String attachmentValue = "";
											for(int m=0;m<valuearr.length;m++){
												String []attacharr = valuearr[m].split("\\|");
												String value_ = "";
												for(int k=0;k<attacharr.length;k++){
													if(k==0){
														String filename = attacharr[0];
														if(filename.indexOf(".")!=-1) {
															filename = PubFunc.encrypt(filename);
														}
														filename=" ";//StringUtils.isEmpty(filename)?" ":filename;
														value_+=filename;
													}else if(k==1){
														String filepath = attacharr[1];
														if(filepath.indexOf(File.separator)!=-1&&!StringUtils.isNumeric(PubFunc.decrypt(attacharr[1]))){
															//存储为路径时转存
															String fileid = this.pathConvertFileid(rootdir, attacharr[0], attacharr[1], attacharr[2]);
															filepath = fileid;
														}
														value_+="|"+filepath;
													}else
														value_+="|"+attacharr[k];
												}
												attachmentValue+=","+value_;
											}
											if(attachmentValue.length()>0)
												attachmentValue = attachmentValue.substring(1);
											dataMap.put(columnArr[j], attachmentValue);
										}else
											dataMap.put(columnArr[j], value);
										continue;
									}
									if(valueArr.length>j){
										value= valueArr[j];
                                        if("".equals(value.trim())){
                                        	value = "";
                                        }
									    dataMap.put(columnArr[j], value);
									}else{
										dataMap.put(columnArr[j], value);
									}
								}
							}
							//根据模板定义将数据表中的数据转换为要显示的格式
							String contValue=convertData(dataMap);
							recordMap.put("contentValue", contValue);
							dataList.add(recordMap);
						}
						//将得到的要显示的数据替换dataList中的contentValue暂时存储的i值
						//replaceData(showDataMap,dataList);
					}
					SubDataMap.put("records", dataList);
				}

			}
			else if(this.subDomain!=null) //模版临时增加一个子集，记录内容为空时需提供空格式数据，否则前台会报错
			{
				SubDataMap.put("records", new ArrayList());
			}
			String temp=this.subDomain.getSubFields();
			if(temp.indexOf("attach")!=-1)
				multimedia_file_flag = this.checkAttach();
			if("0".equals(multimedia_file_flag)&&temp.indexOf("attach")!=-1)//不支持附件
				SubDataMap.put("columns",temp.substring(0, temp.lastIndexOf("attach")-1));
			else//支持附件
				SubDataMap.put("columns",temp.substring(0, temp.length()-1));

		}catch(Exception e){
			e.printStackTrace();
		}
		return SubDataMap;
	}
	/**
	 * 文件转存 临时表中的附件改为vfs存储
	 * @param filename
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	private String pathConvertFileid(String rootPath,String filename,String filePath,String srcfilename)throws Exception {
		String fileid="";
		InputStream in=null;
		try {
			File file=new File(rootPath+filePath+filename);
			if(file.exists()) {
				in = new FileInputStream(file);
				fileid=VfsService.addFile(this.userview.getUserName(), VfsFiletypeEnum.multimedia,VfsModulesEnum.RS, VfsCategoryEnum.other,"", in, srcfilename, "", false);
			}
		} catch (Exception e) {
			throw e;
		}finally {
			PubFunc.closeIoResource(in);
		}
		return fileid;
	}

	/**
	 * 检查子集是否支持附件
	 * @param temp
	 */
	private String checkAttach() {
		ContentDAO dao=new ContentDAO(this.conn);
		String multimedia_file_flag = "0";
		try {
			RecordVo recordVo = new RecordVo("fieldset");
			recordVo.setString("fieldsetid", this.subDomain.getSetName());
			recordVo = dao.findByPrimaryKey(recordVo);
			multimedia_file_flag = recordVo.getString("multimedia_file_flag");
		} catch (GeneralException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return multimedia_file_flag;
	}
	/**
	 * 根据模板定义将数据表中的数据转换为要显示的格式
	 * @param dataMap 要处理的数据
	 * @return
	 */
	private String convertData(HashMap dataMap){
		String contentValue="";
		try{
			if(this.subDomain.getSubFieldList().size()>0){
				SubField subtable = null;
				//liuyz bug 26119 begin
				int fieldNum=0;
				//liuyz bug 26119 end
				for(int i=0;i<this.subDomain.getSubFieldList().size();i++){
					subtable=(SubField)this.subDomain.getSubFieldList().get(i);
					String fldName=subtable.getFieldname(); //指标名称
					FieldItem item=DataDictionary.getFieldItem(fldName);
					if(item==null|| !"1".equals(item.getUseflag())){//同getSubFieldsPropertys函数，需与表头保持一致,不然不同步。
					    if (!"attach".equalsIgnoreCase(fldName)){
					        continue;
					    }
					}
					String fldType="A";
					String codeSetId="0";
					FieldItem fieldItem= subtable.getFieldItem();
					if(!"attach".equalsIgnoreCase(fldName)){
						fldType=subtable.getFieldItem().getItemtype(); //数据类型
						codeSetId=fieldItem.getCodesetid();  //代码项
					}else{
						fldName = fldName.toUpperCase();
					}
					String Slop=subtable.getSlop();    //日期格式
					String value="";
					if(dataMap.get(fldName)!=null)
						value=(String) dataMap.get(fldName);
					else
						value=(String) dataMap.get(fldName.toLowerCase());
					if(value==null){
						value="";
					}
					if("D".equals(fldType)){
						 value=convertDatebySlop(value,Slop);
					}else if("A".equals(fldType)){
						if(!"0".equals(codeSetId) && value.length()>0){
							String text=AdminCode.getCodeName(codeSetId, value);
							if("UM".equalsIgnoreCase(codeSetId)&&StringUtils.isBlank(text)){//bug 39791关联um指标选择单位，显示是代码。
								text=AdminCode.getCodeName("UN", value);
							}
							if (!"".equals(text)){
							    value=text+"||"+value;
							}
						}
					}
					if(value==null){
						value="";
					}
					//liuyz bug 26119 begin
					if(fieldNum==0){
						contentValue=value;
					}else{
						contentValue+="`"+value;
					}
					fieldNum++;
					//liuyz bug 26119 end
				}// end subFieldList循环
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return contentValue;
	}

	/**
	 * 将时间的值根据时间格式转换
	 * @param value
	 * @param Slop
	 * @return  todoliuzy
	 */
	private String convertDatebySlop(String value,String Slop){
		String text=value;

		return text;
	}

    /**
	 * 解析子集表头的xml，再按指定的格式组装成xml
     * @param nodePriv
     * @param rwPriv2
	 * @param setBo
	 * @return
	 */
    public String getSubFieldsPropertys(String nodePriv, String rwPriv2)  {
        String newValue="";
        String multimedia_file_flag = "0";
         try {
        	TemplateParam paramBo = new TemplateParam(this.conn, this.userview, Integer.valueOf(this.tabid));
         	StringBuffer xml = new StringBuffer();
         	xml.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>");
         	xml.append("<fields columns=\"");
         	//处理一下不能显示在前台子集字段的数据
         	String temp = subDomain.getSubFields();
         	if(temp.indexOf("attach")!=-1)
         		multimedia_file_flag = this.checkAttach();
         	if("0".equals(multimedia_file_flag)&&temp.indexOf("attach")!=-1)//不支持附件
				temp = temp.substring(0, temp.lastIndexOf("attach")-1);
			else//支持附件
				temp = temp.substring(0, temp.length()-1);
         	xml.append(temp);
         	xml.append("\">");
         	xml.append("</fields>");
         	//HashMap map = getSubFieldsPropety(setBo);
             Document doc = PubFunc.generateDom(xml.toString());;
             Element eleRoot = null;
             XMLOutputter outputter = new XMLOutputter();
             Format format = Format.getPrettyFormat();
             format.setEncoding("UTF-8");
             outputter.setFormat(format);
             String xpath = "/fields";
             XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
             eleRoot = (Element) findPath.selectSingleNode(doc);
             String rwPriv = "0";
             String relationField="";
             if (eleRoot != null) {
            	 HashMap relationFieldMap=new HashMap();
             	for (int i=0;i< subDomain.getSubFieldList().size();i++){
             		SubField subTable= (SubField) subDomain.getSubFieldList().get(i);
             		String field= subTable.getFieldname();
             		if("0".equals(multimedia_file_flag)&&"attach".equals(field))//不支持附件
             			continue;
         		    FieldItem item=DataDictionary.getFieldItem(field);
                     if(item!=null&&"1".equals(item.getUseflag())){
             			Element eleField = new Element("field");
             			eleField.setAttribute("fldName",field);
             			if (subTable.getTitle()!=null && subTable.getTitle().length()>0)
             			   eleField.setAttribute("fldTitle",subTable.getTitle());
             			else
             				eleField.setAttribute("fldTitle",item.getItemdesc());
             			eleField.setAttribute("fldWidth",subTable.getTitleWidth());
             			if(item.getItemlength()>=255&&"0".equals(item.getCodesetid())&&"A".equalsIgnoreCase(item.getItemtype())) {
             				eleField.setAttribute("fldType","M");
             				eleField.setAttribute("fldInputType","0");
             			}else {
             				eleField.setAttribute("fldType",item.getItemtype());
             			}
             			eleField.setAttribute("codeSetId",item.getCodesetid());
             			eleField.setAttribute("fldLength",item.getItemlength()+"");
             			eleField.setAttribute("fldDecLength",item.getDecimalwidth()+"");
             			if("M".equalsIgnoreCase(item.getItemtype()))
             				eleField.setAttribute("fldInputType",item.getInputtype()+"");
             			//syl 20200206 57801 V771封版：人事异动模板中插入了子集，维护有小数维护的数值型指标无法输入小数了，自动给四舍五入成整数了，不对。
             			//syl 还原回去 58110 V77包：人事异动：业务模板中插入子集，子集中数值型指标的小数位数设置为0时，前台就应该为0，而不应显示为指标体系中的小数位数。
             			eleField.setAttribute("format",subTable.getSlop());
             			eleField.setAttribute("align",subTable.getAlign());
             			eleField.setAttribute("valign",subTable.getValign());
             			eleField.setAttribute("need",subTable.getNeed());
             			eleField.setAttribute("his_readonly",subTable.getHis_readonly());
             			eleField.setAttribute("imppeople",subTable.getImppeople());
             			//将codeSetId不为0的默认值以指定格式传递到前台
             			String codeSetId=item.getCodesetid();
             			String defValue=subTable.getDefaultvalue();
             			if(!"0".equals(codeSetId) && (!"".equals(defValue) || defValue!=null)){
             				String text=AdminCode.getCodeName(codeSetId, defValue);
             				defValue=defValue+"`"+text;
             			}
             			if("N".equalsIgnoreCase(item.getItemtype())) {
             				if(StringUtils.isNotBlank(defValue)) {
								String pattern = "###"; // 浮点数的精度
								int decimal = item.getDecimalwidth();
								int slop = 0;
								if(StringUtils.isNotBlank(subTable.getSlop())) {
									slop = Integer.parseInt(subTable.getSlop());
									if(slop<decimal)
										decimal = slop;
								}
								if (decimal > 0)
									pattern += ".";
								for (int ia = 0; ia < decimal; ia++)
									pattern += "0";
								double dValue = 0;
								dValue = Double.parseDouble(defValue);
								defValue = new DecimalFormat(pattern).format(dValue);
							}
             			}
             			eleField.setAttribute("defaultValue",defValue);
             			eleField.setAttribute("pre",subTable.getPre());
             			 if (TemplateUtilBo.isJobtitleVoteModule(this.userview) ){//如果是职称评审，则是只读权限，lis 20160617
             				rwPriv="1";
                         }else {
                        	 if("1".equals(nodePriv)){
                        		 rwPriv=rwPriv2;
                        	 }else{
                        		 /** 数据录入不判断子集和指标权限, 0判断(默认值),1不判断 */
                                 String insertDataCtrl = paramBo.getUnrestrictedMenuPriv_Input();
                                 if("1".equals(insertDataCtrl))
                                 	rwPriv="2";
                             	 else
                             		rwPriv = this.userview.analyseFieldPriv(field);
                        	 }
                         }

             			eleField.setAttribute("rwPriv",rwPriv);
             			relationField=subTable.getRelation_field();
             			if(StringUtils.isNotBlank(relationField)){
             				relationFieldMap.put(eleField.getAttributeValue("fldName"), relationField);
             			}
             			eleRoot.addContent(eleField);
             		}
                    if("attach".equals(field)){
                    	Element eleField = new Element("field");
             			eleField.setAttribute("fldName",field);
             			if (subTable.getTitle()!=null && subTable.getTitle().length()>0)
             			   eleField.setAttribute("fldTitle",subTable.getTitle());
             			else
             				eleField.setAttribute("fldTitle","附件");
             			eleField.setAttribute("fldWidth",subTable.getTitleWidth());
             			eleField.setAttribute("fldType","A");
             			eleField.setAttribute("codeSetId","0");
             			eleField.setAttribute("fldLength","80");
             			eleField.setAttribute("fldDecLength","0");
             			eleField.setAttribute("format",subTable.getSlop());
             			eleField.setAttribute("align",subTable.getAlign());
             			eleField.setAttribute("valign",subTable.getValign());
             			eleField.setAttribute("need",subTable.getNeed());
             			eleField.setAttribute("defaultValue",subTable.getDefaultvalue());
             			eleField.setAttribute("pre",subTable.getPre());
             			eleField.setAttribute("rwPriv","1");
             			eleField.setAttribute("his_readonly",subTable.getHis_readonly());
             			eleRoot.addContent(eleField);
             		}
             	}
             	if(eleRoot.getContentSize()>0&&relationFieldMap.size()>0){
             		Iterator iterator = relationFieldMap.entrySet().iterator();
    				while(iterator.hasNext()){
    					Entry entry=(Entry)	iterator.next();
    					String relationFieldStr = (String)entry.getValue();
    					String uniqueId=(String)entry.getKey();
    					String fatherRelationField="";
    					Element childFieldBean=null;
    					for(int i=0;i<eleRoot.getContentSize();i++){
    						Element fieldBean=(Element) eleRoot.getContent(i);
    						String fieldname =  fieldBean.getAttributeValue("fldName");
    						String childRelationField = (String) fieldBean.getAttributeValue("childRelationField");
    						if((!uniqueId.equalsIgnoreCase(fieldname))&&relationFieldStr.equalsIgnoreCase(fieldname)){
    							if(StringUtils.isBlank(childRelationField)){
    								childRelationField=uniqueId+",";
    							}else{
    								childRelationField+=uniqueId+",";
    							}
    							fatherRelationField=fieldname;
    							fieldBean.setAttribute("childRelationField", childRelationField);
    						}else if(uniqueId.equalsIgnoreCase(fieldname)){
    							childFieldBean=fieldBean;
    						}
    					}
    					if(childFieldBean!=null){
    						childFieldBean.setAttribute("fatherRelationField", fatherRelationField);
    					}
    				}
             	}
             }
             newValue = outputter.outputString(doc);
         } catch (Exception e) {
             e.printStackTrace();
         }
         return newValue;
     }

    /**处理数据
	 * xmldata：页面得到的xml
	 * */
	public String decodeData(String xmldata) throws GeneralException
	{
        try
        {
			if(!(xmldata==null||xmldata.length()==0))
			{
				TemplateSet setBo=new TemplateSet();
				String xml_param=setBo.getXml_param();
				TSubSetDomain domain=new TSubSetDomain(xml_param);

				//xmldata=SafeCode.decode(xmldata);
				xmldata = PubFunc.keyWord_reback(xmldata);
				xmldata=xmldata.replaceAll("&", "");//如果连续分隔符`，被替换成,号啦
				//isMustFill_sub(xmldata,domain);//验证是否必填  lis
			}
			return xmldata;
        }
        catch(Exception e)
        {
        	throw GeneralExceptionHandler.Handle(e);
        }
	}
	/**将变化后子集的xml中rwPriv、fieldsPriv、fieldsWidth、fieldsTitle这四个属性去掉。郭峰*/
	public String removeAttributesFromXml(String xmldata){
		String newxml = "";
		try{
			if (!"".equals(xmldata)){
				//开始从xml删除属性
				Document doc=PubFunc.generateDom(xmldata);;
				Element element=null;
				XMLOutputter outputter = new XMLOutputter();
				Format format = Format.getPrettyFormat();
				format.setEncoding("UTF-8");
				outputter.setFormat(format);
				String xpath="/records";
				XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
				element =(Element) findPath.selectSingleNode(doc);
				if(element!=null){
					if(element.getAttribute("rwPriv")!=null){
						element.removeAttribute("rwPriv");
					}
					if(element.getAttribute("fieldsPriv")!=null){
						element.removeAttribute("fieldsPriv");
					}
					if(element.getAttribute("fieldsWidth")!=null){
						element.removeAttribute("fieldsWidth");
					}
					if(element.getAttribute("fieldsTitle")!=null){//wangrd 2015-02-13
	                    element.removeAttribute("fieldsTitle");
	                }
				}
				newxml = outputter.outputString(doc);
			}

		}catch(Exception e){
			e.printStackTrace();
		}
		return newxml;
	}

	/**
	 * 模板子集是否有必填项没填
	 * @param value
	 * @param domain
	 * @return
	 */
	private boolean  isMustFill_sub(String value,TSubSetDomain domain) throws GeneralException
	{
		boolean flag=false;
		Document doc=null;
		Element element=null;
		TFieldFormat tf=null;
		try
		{
			if(value!=null&&value.length()>0)
			{
				doc=PubFunc.generateDom(value);
				String xpath="/records/record";
				XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
				List childlist=findPath.selectNodes(doc);
				if(childlist!=null&&childlist.size()>0)
				{
					for(int i=0;i<childlist.size();i++)
					{
						element=(Element)childlist.get(i);
						String contentValue=element.getValue();
						if(contentValue!=null&&contentValue.length()>0)
						{
							String[] temps=contentValue.split("`");
							ArrayList fieldList=domain.getFieldfmtlist();
							for(int j=0;j<fieldList.size();j++)
							{
								tf=(TFieldFormat)fieldList.get(j);
								if(tf.isBneed())
								{
									if(j>=temps.length)
									{
										flag=true;
										throw new Exception("该子集中 "+tf.getTitle()+" 为必填项!");

									}
									else if(temps[j].trim().length()==0)
									{
										flag=true;
										throw new Exception("该子集中 "+tf.getTitle()+" 为必填项!");
									}
								}

							}


						}

					}
				}
			}

		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

		return flag;
	}

	/**
	 * 比较数据一致性
	 * @param value
	 * @param setdomain
	 * @return
	 */
	private String compareData(String value,TSubSetDomain setdomain)
	{

		String data=value;
		Document doc=null;
		Element element=null;

		try
		{
			if(value!=null&&value.length()>0)
			{
				doc=PubFunc.generateDom(value);
				String xpath="/records";
				XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
				List childlist=findPath.selectNodes(doc);
				if(childlist!=null&&childlist.size()>0)
				{
					element=(Element)childlist.get(0);
					String columns="";
					if(element!=null&&element.getAttributeValue("columns")!=null)
						columns=element.getAttributeValue("columns");
					if(columns.length()>0&&columns.charAt(columns.length()-1)!='`')
						columns=columns+"`";
					if(!columns.equalsIgnoreCase(setdomain.getFields()))
					{
						columns=columns.substring(0,columns.length()-1);
						String[] temps=columns.split("`");

						String fields=setdomain.getFields();
						if(fields.length()>0)
							fields=fields.substring(0,fields.length()-1);
						String[] setTemps=fields.split("`");

						element.setAttribute("columns", setdomain.getFields().substring(0, setdomain.getFields().length()-1));
						childlist=element.getChildren();
						StringBuffer content=new StringBuffer("");
						for(int i=0;i<childlist.size();i++)
						{
							element=(Element)childlist.get(i);
							String temp_value=element.getText();
							String[] _temps=temp_value.split("`");
							HashMap valueMap=new HashMap();
							for(int j=0;j<temps.length;j++)
							{
								if(j<_temps.length)
									valueMap.put(temps[j].toLowerCase(), _temps[j]);
							}

							content.setLength(0);
							for(int j=0;j<setTemps.length;j++)
							{
								if(valueMap.get(setTemps[j].toLowerCase())!=null)
								{
									content.append((String)valueMap.get(setTemps[j].toLowerCase())+"`");
								}
								else
									content.append("`");
							}
							if(content.length()>0)
								content.setLength(content.length()-1);
							element.setText(content.toString());
						}

					}
					else
					{
							columns=columns.substring(0,columns.length()-1);
							element.setAttribute("columns", columns);
					}


					XMLOutputter outputter=new XMLOutputter();
					Format format=Format.getPrettyFormat();
					format.setEncoding("UTF-8");
					outputter.setFormat(format);
					data=outputter.outputString(doc);
				}
			}
			else
			{
				String xml=setdomain.outContentxml();
				data=xml;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return data;
	}

	/**
	 * @author lis
	 * @Description:  保存子集附件信息
	 * @date May 30, 2016
	 * @param Sub_dataXml
	 * @return 子集信息
	 * @throws GeneralException
	 */
	public String saveSubAttachment(String sub_dataXml) throws GeneralException{
		return String.valueOf(saveSubAttachments(sub_dataXml).get(0));
	}
	public ArrayList saveSubAttachments(String sub_dataXml) throws GeneralException{
		String multimedia_file_flag = "0";
		ArrayList xmllist = new ArrayList();
		String sub_dataXml_="";
		try{
			if (!"".equals(sub_dataXml) && sub_dataXml!=null){
			    sub_dataXml=sub_dataXml.replace("&", "＆");
				Document doc=PubFunc.generateDom(sub_dataXml);
				Element eleRoot=null;  //xml解析得到/records对象
				Element element=null;  //xml解析得到/record对象
				XMLOutputter outputter = new XMLOutputter();
				Format format = Format.getPrettyFormat();
				format.setEncoding("UTF-8");
				outputter.setFormat(format);
				String xpath="/records";
				XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
				eleRoot =(Element) findPath.selectSingleNode(doc);
				boolean isCdata = false;
				if(eleRoot!=null){
					StringBuffer contentValueBuf = new StringBuffer();
					String columns=eleRoot.getAttributeValue("columns").toUpperCase(); //得到xml中columns中对应的值
					String[] columnarr = columns.split("`");
					for(int j=0;j<columnarr.length;j++){
						FieldItem item=DataDictionary.getFieldItem(columnarr[j]);
						if(item!=null){
							int inputtype = item.getInputtype();
							if("M".equalsIgnoreCase(item.getItemtype())&&inputtype==1&&!isCdata){
								isCdata = true;
								break;
							}
						}
					}
					if(columns.toLowerCase().indexOf("attach")!=-1)
						multimedia_file_flag = this.checkAttach();
					List recordList=eleRoot.getChildren("record");
					ArrayList dataList=new ArrayList();  //存储每一条record对应的值和属性
					if(recordList!=null&&recordList.size()>0)
					{
						for(int i=0;i<recordList.size();i++)
						{
							contentValueBuf.setLength(0);
							//存储record中的属性和值，先将contentValue的值存储为i，得到最终的值后再进行替换
							HashMap recordMap=new HashMap();
							element=(Element)recordList.get(i);
							String contentValue=element.getValue();
							//存储column和其对应的值，lineNum为当前record的条数
							if(contentValue!=null&&contentValue.length()>0)
							{
								String[] valueArr=contentValue.split("`");
								String[] columnArr=columns.split("`");
								for(int j=0;j<columnArr.length;j++){
									if(j==0&&isCdata)
										contentValueBuf.append(" <![CDATA[");
									if("0".equals(multimedia_file_flag)&&columnArr[j].toLowerCase().indexOf("attach")!=-1)//不支持附件
										continue;
                                    String value="";
                                    if (valueArr.length>j){
                                        value= valueArr[j];
                                        if("".equals(value.trim())){
                                        	value = "";
                                        }
                                    }
                                    if("ATTACH".equalsIgnoreCase(columnArr[j])&&StringUtils.isNotBlank(value))
                                    {
                                    	ArrayList attachmentValue = getSubSetAttachAndEncrypt(value);
                                        contentValueBuf.append(attachmentValue.get(0)+"`");
                                        sub_dataXml=sub_dataXml.replace(value, (String)attachmentValue.get(1));
                                    }else{
                                        contentValueBuf.append(value+"`");
                                    }
								}
								if(contentValueBuf.length() > 0){
									String contentValueBuf_ = contentValueBuf.substring(0,contentValueBuf.length()-1);
									if(isCdata){
										contentValueBuf_+="]]>";
										contentValueBuf_ = contentValueBuf_.replace("<", "＜");
										contentValueBuf_ = contentValueBuf_.replace(">", "＞");
									}
									element.setText(contentValueBuf_);
								}
							}
						}
					}
				}
				sub_dataXml_ = outputter.outputString(doc);
				if(isCdata){
					sub_dataXml_ = sub_dataXml_.replace("＜","<");
					sub_dataXml_ = sub_dataXml_.replace("＞",">");
				}
			}
			xmllist.add(sub_dataXml_);
			xmllist.add(sub_dataXml=sub_dataXml.replace("＆", "&"));
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return xmllist;
	}

	/**
	 * @author lis
	 * @Description: 保存子集附件到新目录
	 * @date Jul 18, 2016
	 * @param attachmentValues
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList saveSubAttachmentToNewDir(String attachmentValues) throws GeneralException{
		ArrayList attachmentlist = new ArrayList();
		StringBuffer attachment = new StringBuffer();
		StringBuffer attachment_en = new StringBuffer();
		try {
			 AttachmentBo attachmentBo = new AttachmentBo(userview, conn,tabid);
			 if(StringUtils.isNotBlank(attachmentValues)){
				String[] attachmentValueArry = attachmentValues.split(",");
				for(String attachmentValue : attachmentValueArry){
					if(StringUtils.isBlank(attachmentValue))
						continue;
					String[] subDataArry = attachmentValue.split("\\|");
					String filename = subDataArry[0];
					String filepath = subDataArry[1];
					if(filename.indexOf(".")==-1){//bug 43844 上传子集附件之后，进行查看提示“找不到该文件”
						try{
							filename=PubFunc.decrypt(SafeCode.decode(filename));
						}catch(Exception ex){
							ex.printStackTrace();
						}
					}
					//filename = PubFunc.decrypt(filename);
					filepath = PubFunc.decrypt(filepath).replace("\\", File.separator).replace("/", File.separator);
					if(!filepath.endsWith(File.separator)) filepath += File.separator;
					String filePath = filepath + filename;
					filePath = filePath.replace("\\", File.separator).replace("/", File.separator);
					File file = new File(filePath);
					String middlepath = "";
					if("\\".equals(File.separator)){//证明是windows
						middlepath = "subdomain\\template_";
					}else if("/".equals(File.separator)){//证明是linux
						middlepath = "subdomain/template_";
					}
					StringBuffer tempValue = new StringBuffer();
					StringBuffer tempValue_en = new StringBuffer();
					if(file.exists() && filePath.indexOf(middlepath) < 0){
						HashMap valueMap = new HashMap();
						//保存文件到指定目录
						attachmentBo.setRealFileName(subDataArry[2]);
						valueMap = attachmentBo.SaveFileToDisk(file, middlepath);
						for(int i = 0; i < subDataArry.length; i++){
							if(i == 1){//filepath
								tempValue.append("|" + attachmentBo.getAbsoluteDir());
								tempValue_en.append("|" + PubFunc.encrypt(attachmentBo.getAbsoluteDir()));
							}else if(i==0){//filename
								tempValue.append("|" + filename);
								tempValue_en.append("|" + PubFunc.encrypt(filename));
							}else{
								tempValue.append("|" + subDataArry[i]);
								tempValue_en.append("|" + subDataArry[i]);
							}
						}
						file.delete();
					}else{
						attachmentBo.initParam(false);
						String rootdir = attachmentBo.getRootDir();
						if(filepath.startsWith(rootdir)) {
							int index = rootdir.length();
							filepath = filepath.substring(index);
						}
						for(int i = 0; i < subDataArry.length; i++){
							if(i == 1){//filepath
								tempValue.append("|" + filepath);
								tempValue_en.append("|" + PubFunc.encrypt(filepath));
							}else if(i==0){//filename
								tempValue.append("|" + filename);
								tempValue_en.append("|" + PubFunc.encrypt(filename));
							}else{
								tempValue.append("|" + subDataArry[i]);
								tempValue_en.append("|" + subDataArry[i]);
							}
						}
					}
					if(tempValue.length() > 0){
						attachment.append("," + tempValue.toString().substring(1));
					}
					if(tempValue_en.length() > 0){
						attachment_en.append("," + tempValue_en.toString().substring(1));
					}
				}
             }
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		if(attachment.length() > 0)
			attachmentlist.add(attachment.substring(1));
		if(attachment_en.length() > 0)
			attachmentlist.add(attachment_en.substring(1));
		return attachmentlist;
	}
	/**
	 * 解析子集附件，返回处理后的子集字符串
	 * @param attachmentValues
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getSubSetAttachAndEncrypt(String attachmentValues)throws GeneralException {
		ArrayList attachmentlist = new ArrayList();
		StringBuffer attachment = new StringBuffer();
		StringBuffer attachment_en = new StringBuffer();
		try {
			if(StringUtils.isNotBlank(attachmentValues)){
				String[] attachmentValueArry = attachmentValues.split(",");
				for(String attachmentValue : attachmentValueArry){
					if(StringUtils.isBlank(attachmentValue))
						continue;
					String[] subDataArry = attachmentValue.split("\\|",-1);
					String filename = subDataArry[0];
					if(!StringUtils.isNumeric(PubFunc.decrypt(filename))) {
						filename=PubFunc.decrypt(filename);
						//filename 有路径时去除路径，只保留文件名
						if(filename.indexOf(File.separator)>-1) {
							filename=filename.substring(filename.lastIndexOf(File.separator)+1);
						}
					}
					String filepath = subDataArry[1];
					//filepath = PubFunc.decrypt(filepath).replace("\\", File.separator).replace("/", File.separator);
					StringBuffer tempValue = new StringBuffer();
					StringBuffer tempValue_en = new StringBuffer();
					for(int i = 0; i < subDataArry.length; i++){
						if(i == 1){//filepath
							//当为vfs时 path默认为空，防止有乱码情况存在
							//判断附件是否是临时文件 如果是临时文件则附件转正
							VfsService.tempToForever(Arrays.asList(filepath));
							tempValue.append("|" + filepath);
							tempValue_en.append("|" + filepath);
						}else if(i==0){//filename
							tempValue.append("|" + filename);
							tempValue_en.append("|" +filename);
							/*if(!StringUtils.isNumeric(PubFunc.decrypt(filename))) {
								tempValue_en.append("|" + PubFunc.encrypt(filename));
							}else {
								tempValue_en.append("|" +filename);
							}*/
						}else{
							tempValue.append("|" + subDataArry[i]);
							tempValue_en.append("|" + subDataArry[i]);
						}
					}
					if(tempValue.length() > 0){
						attachment.append("," + tempValue.toString().substring(1));
					}
					if(tempValue_en.length() > 0){
						attachment_en.append("," + tempValue_en.toString().substring(1));
					}
				}
             }
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		if(attachment.length() > 0)
			attachmentlist.add(attachment.substring(1));
		if(attachment_en.length() > 0)
			attachmentlist.add(attachment_en.substring(1));
		return attachmentlist;
	}

	/**
	 *
	 */
	public String encryptOrDecryptAttachment(String sub_dataXml , String crypyType) throws GeneralException {
		Document doc=null;
		Element eleRoot=null;
		Element element = null;
		try
		{
			if(sub_dataXml!=null&&sub_dataXml.length()>0)
			{
				sub_dataXml=sub_dataXml.replace("&", "＆");
				doc=PubFunc.generateDom(sub_dataXml);
				XMLOutputter outputter = new XMLOutputter();
				String xpath="/records";
				XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
				eleRoot =(Element) findPath.selectSingleNode(doc);
				if(eleRoot!=null){
					String columns=eleRoot.getAttributeValue("columns").toUpperCase(); //得到xml中columns中对应的值
					List recordList=eleRoot.getChildren("record");
					if(recordList!=null&&recordList.size()>0)
					{
						for(int i=0;i<recordList.size();i++)
						{
							element=(Element)recordList.get(i);
							String contentValue=element.getValue();
							if(contentValue!=null&&contentValue.length()>0)
							{
								String[] valueArr=contentValue.split("`",-1);
								String[] columnArr=columns.split("`");
								String theEndValue = "";
								for(int j=0;j<columnArr.length;j++){
									if("ATTACH".equalsIgnoreCase(columnArr[j]))
                                    {
										String value = "";
										if(j<valueArr.length)
											value= valueArr[j];
										if(StringUtils.isNotBlank(value)){
											String [] valuearr = value.split(",");
											String attachmentValue = "";
											for(int m=0;m<valuearr.length;m++){
												String []attacharr = valuearr[m].split("\\|");
												String value_ = "";
												for(int k=0;k<attacharr.length;k++){
													if(k==0){
														String filename = attacharr[0];
														if(filename.indexOf(".")>-1) {
															filename = "0".equals(crypyType)?PubFunc.encrypt(filename):PubFunc.decrypt(filename);
														}
														value_+=filename;
													}else if(k==1){
														String filepath = attacharr[1];
														//判断filepath是否有分隔符 并且解密后有分隔符
														if(filepath.indexOf(File.separator)<=0&&PubFunc.encrypt(filepath).indexOf(File.separator)>-1) {
															filepath = "0".equals(crypyType)?PubFunc.encrypt(filepath):PubFunc.decrypt(filepath);
														}
														value_+="|"+filepath;
													}else
														value_+="|"+attacharr[k];
												}
												attachmentValue+=","+value_;
											}
											if(attachmentValue.length()>0)
												attachmentValue = attachmentValue.substring(1);
											theEndValue+=attachmentValue+"`";
										}else
											theEndValue+=value+"`";
                                    }else{
                                    	theEndValue+=valueArr[j]+"`";
                                    }
								}
								theEndValue = theEndValue.substring(0,theEndValue.length()-1);
								element.setText(theEndValue);
							}
						}
					}
				}
				sub_dataXml = outputter.outputString(doc);
				sub_dataXml=sub_dataXml.replace("＆", "&");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return sub_dataXml;
	}
	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public UserView getUserview() {
		return userview;
	}

	public void setUserview(UserView userview) {
		this.userview = userview;
	}

    public String getApproveFlag() {
        return approveFlag;
    }

    public void setApproveFlag(String approveFlag) {
        this.approveFlag = approveFlag;
    }
	public SubSetDomain getSubDomain() {
		return subDomain;
	}
	public void setSubDomain(SubSetDomain subDomain) {
		this.subDomain = subDomain;
	}

	public String data2Xml(ArrayList dataList) {
        String newXml = "";
        try {
            AttachmentBo attachmentBo=new AttachmentBo(this.userview, conn, tabid);
            attachmentBo.initParam(true);
            StringBuffer xml = new StringBuffer();
            xml.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>");
            xml.append("<records columns=\"");
            String temp = subDomain.getSubFields();
            xml.append(temp.substring(0,temp.length()-1));
            xml.append("\">");
            xml.append("</records>");
            SAXBuilder saxbuilder = new SAXBuilder();
            StringReader reader = new StringReader(xml.toString());
            Document doc = saxbuilder.build(reader);
            Element eleRoot = null;
            XMLOutputter outputter = new XMLOutputter();
            Format format = Format.getPrettyFormat();
            format.setEncoding("GBK");
            outputter.setFormat(format);
            String xpath = "/records";
            XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
            eleRoot = (Element) findPath.selectSingleNode(doc);
            if (eleRoot != null) {
                for(int j=0;j<dataList.size();j++) {
                    HashMap map = (HashMap) dataList.get(j);
                    Element element = new Element("record");
                    String record_key_id = (String) map.get("record_key_id");
                    String i9999 = (String) map.get("i9999");
                    String ishavechange = (boolean) map.get("ishavechange")?"true":"false";
                    //String edit = (boolean) map.get("readonly")?"0":"1";
                    element.setAttribute("record_key_id", record_key_id);
                    if(StringUtils.isEmpty(i9999)) {
                        i9999="-1";
                    }
                    element.setAttribute("I9999", i9999);
                    element.setAttribute("isHaveChange", ishavechange);
                    //element.setAttribute("edit", edit);
                    String value = "";
                    for (int i = 0; i < subDomain.getSubFieldList().size(); i++) {
                        SubField subTable = (SubField) subDomain.getSubFieldList().get(i);
                        String field = subTable.getFieldname();
                        if ("attach".equalsIgnoreCase(field)) {
                            if(StringUtils.isEmpty((String)map.get(field))) {
                                value += "`";
                            }else {
                                JsonArray arry = new JsonParser().parse((String)map.get(field)).getAsJsonArray();
                                value += parseAttach(arry,attachmentBo) + "`";
                            }
                        } else {
                            value += (StringUtils.isEmpty((String) map.get(field)) ? "" : ((String) map.get(field))) + "`";
                        }
                    }
                    value = value.substring(0,value.length()-1);
                    element.addContent(value);
                    eleRoot.addContent(element);
                }
            }
            newXml = outputter.outputString(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newXml;
    }
	 /**
     * 解析子集附件
     *  {
     *      fileid:"V8OseaQ7tZtUHJH9k40AnwPAATTP3HJDPAATTPPAATTP3HJDPAATTP",
     *      name: 'dssds.xls',
     *      size：xxx
     *      fromhistory:id,
     *      index:
     *      filetype:'F',
     *  }
     *  filename + "|" + path + "|" + srcfilename + "|" + fileSizeString + "|" + id + "|" + m + "|" + "type:" + filetype;
     *  filename+"|"+path+"|"+srcfilename+"|"+fileSizeString+"|"+id+"|"+m+"|"+"type:"+filetype
     *
     *  [{"path":"D:\\test\\multimedia\\subdomain\\template_8\\T479\\T223","size":"44.24K",
     *  "fromhistory":"50","name":"测试hi皇帝.png","index":"1","fileType":"F",
     *  "fileId":"rhIGbsSWFIeEdSXZ8qeMnlmRUCorPQByFnwtqgMDnNTUhviaRBPSa5gyLsdLNjibzK",
     *  "absolutepath":"Fl8YhsicQwqMONnzoRiaC5OsHw0nV0LqXKtQcL6lH9ILRooQAaoiboB3LP8Mxr4BHuZ4Tqjl73omT9xznIv5o0t8w"}]
     *
     * @param list
     * @return
     * @throws Exception
     */
    private  String parseAttach(JsonArray arry,AttachmentBo attachmentBo) throws Exception{
        String data="";
        int index = 0;
        for(JsonElement el:arry) {
            JsonObject obj = el.getAsJsonObject();
            String fileid=obj.get("fileId")!=null?obj.get("fileId").getAsString():"";
            String filepath=obj.get("filepath")!=null?PubFunc.decrypt(obj.get("filepath").getAsString()):"";
            //从刷新子集过来的数据
            if(StringUtils.isEmpty(filepath) || StringUtils.isNumeric(filepath)) {
                fileid=obj.get("fileId")!=null?obj.get("fileId").getAsString():"";
            }
            //以subdomain开头的路径代表为已转存完成的文件否则文件另存
            else if(!filepath.startsWith("subdomain")){
                String middlepath = "";
                if("\\".equals(File.separator)){//证明是windows
                    middlepath = "subdomain\\template_";
                }else if("/".equals(File.separator)){//证明是linux
                    middlepath = "subdomain/template_";
                }
                File file=new File(filepath);
                attachmentBo.SaveFileToDisk(file, middlepath);
                fileid=attachmentBo.getDestFileName();
            }
            String name=obj.get("name")!=null?obj.get("name").getAsString():"";
            String size=obj.get("size")!=null?obj.get("size").getAsString():"";
            String fromhistory=obj.get("fromhistory")!=null?obj.get("fromhistory").getAsString():"";
//            String index=obj.get("index")!=null?obj.get("index").getAsString():"";
            String filetype=obj.get("fileType")!=null?obj.get("fileType").getAsString():"";
            data+=","+" "+"|"+fileid+"|"+name+"|"+size+"|"+fromhistory+"|"+index+"|type:"+filetype;
            index++;
        }
        data=StringUtils.isEmpty(data)?"":data.substring(1);
        return data;
    }
}
