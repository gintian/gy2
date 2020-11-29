package com.hjsj.hrms.module.template.historydata.formcorrelation.templatesubset.businessobject;

import com.hjsj.hrms.businessobject.general.template.TSubSetDomain;
import com.hjsj.hrms.module.template.historydata.formcorrelation.utils.TemplateDataBo;
import com.hjsj.hrms.module.template.historydata.formcorrelation.utils.TemplateUtilBo;
import com.hjsj.hrms.module.template.historydata.formcorrelation.utils.javabean.SubField;
import com.hjsj.hrms.module.template.historydata.formcorrelation.utils.javabean.SubSetDomain;
import com.hjsj.hrms.module.template.historydata.formcorrelation.utils.javabean.TemplateParam;
import com.hjsj.hrms.module.template.historydata.formcorrelation.utils.javabean.TemplateSet;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

/**
 * 用于获取子集信息
* @Title: TemplateSubsetBo
* @Description:
* @author: hej
* @date 2019年11月21日 上午10:05:59
* @version
 */
public class TemplateSubsetBo {

	private Connection conn;
	private UserView userview;	
	
	private String tabid="";
	/**列表中子集对应的列*/
	private String columnName=""; 
	private SubSetDomain  subDomain=null;  
	private String archive_id = "";
	
    /** 区分报审、报备、加签 
     * 1：报审 2：加签  3 报备 
     *  报审：按照模板设置的指标显示。
     *  浏览打印：按照数据表保存的xml显示指标。 目前未做，以后考虑，需从数据xml解析指标，单指标设置取不到。
    */
    private String approveFlag="0"; 
    
    public TemplateSubsetBo(Connection conn,UserView userview,String tabid,String columnName,String archive_id){
        this.conn=conn;
        this.userview=userview;
        this.tabid=tabid;
        this.columnName=columnName;
        this.approveFlag="1";
        this.archive_id = archive_id;
        initSub_domain(); 
    }

	/**
	 * 根据模板号和子集表名获取template_set表中对应的子集表头信息
	 * @param tabid 模板号
	 * @param columnName 子集单元格对应的子集表名
	 * @return
	 */
	private void initSub_domain(){		
		String sub_domain="";
		try {
			TemplateUtilBo templateUtilBo = new TemplateUtilBo(conn, userview);
			ArrayList cellList = templateUtilBo.getArchiveCell(Integer.parseInt(tabid), archive_id, -1);
			for(int i=0;i<cellList.size();i++){
				TemplateSet setbo = (TemplateSet) cellList.get(i);
				String fieldname = setbo.getTableFieldName();
				if(setbo.isSubflag()&&fieldname.equalsIgnoreCase(columnName)) {
					sub_domain=setbo.getXml_param();
					subDomain = new SubSetDomain(sub_domain);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 查找数据表中的某个子集对应的数据
	 * @param record_id
	 * @param archive_year
	 * @return
	 * @throws GeneralException
	 */
	public String getSub_dataXml(String record_id,String archive_year) throws GeneralException{
		String Sub_dataXml="";
		TemplateDataBo dataBo = new TemplateDataBo(conn, userview, Integer.parseInt(tabid), archive_id);
		HashMap dataMap = dataBo.analysisJson2Map(record_id, archive_year);
		if(dataMap.containsKey(columnName.toLowerCase())) {
			Sub_dataXml = (String) dataMap.get(columnName.toLowerCase());
		}
		return Sub_dataXml;
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
		try{
			if (!"".equals(Sub_dataXml) && Sub_dataXml!=null){
				Sub_dataXml = PubFunc.keyWord_reback(Sub_dataXml);
				Sub_dataXml=Sub_dataXml.replace("&", "＆");
				Sub_dataXml=Sub_dataXml.replace("^", "＾");
				Document doc=PubFunc.generateDom(Sub_dataXml);
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
							String record_key_id = element.getAttributeValue("record_key_id")==null?this.userview.getUserName()+System.currentTimeMillis()+(Math.round(Math.random()*100)*Math.round(Math.random()*100)):element.getAttributeValue("record_key_id");
							recordMap.put("record_key_id", record_key_id);		
							String isHaveChange = element.getAttributeValue("isHaveChange")==null?"false":"true";
							recordMap.put("isHaveChange", isHaveChange);	
							String contentValue=element.getValue();
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
														if(filename.indexOf(".")!=-1)
															filename = PubFunc.encrypt(filename);
														value_+=filename;
													}else if(k==1){
														String filepath = attacharr[1];
														if(filepath.indexOf(File.separator)!=-1)
															filepath = PubFunc.encrypt(filepath);
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
							String contValue=convertData(dataMap);
							recordMap.put("contentValue", contValue);
							dataList.add(recordMap);
						}
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
		String newValue = "";
		String multimedia_file_flag = "0";
		try {
			TemplateParam paramBo = new TemplateParam(this.conn, this.userview, Integer.valueOf(this.tabid),
					archive_id);
			StringBuffer xml = new StringBuffer();
			xml.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>");
			xml.append("<fields columns=\"");
			// 处理一下不能显示在前台子集字段的数据
			String temp = subDomain.getSubFields();
			if (temp.indexOf("attach") != -1)
				multimedia_file_flag = this.checkAttach();
			if ("0".equals(multimedia_file_flag) && temp.indexOf("attach") != -1)// 不支持附件
				temp = temp.substring(0, temp.lastIndexOf("attach") - 1);
			else// 支持附件
				temp = temp.substring(0, temp.length() - 1);
			xml.append(temp);
			xml.append("\">");
			xml.append("</fields>");
			Document doc = PubFunc.generateDom(xml.toString());
			Element eleRoot = null;
			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			String xpath = "/fields";
			XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
			eleRoot = (Element) findPath.selectSingleNode(doc);
			String rwPriv = "0";
			String relationField = "";
			if (eleRoot != null) {
				HashMap relationFieldMap = new HashMap();
				for (int i = 0; i < subDomain.getSubFieldList().size(); i++) {
					SubField subTable = (SubField) subDomain.getSubFieldList().get(i);
					String field = subTable.getFieldname();
					if ("0".equals(multimedia_file_flag) && "attach".equals(field))// 不支持附件
						continue;
					FieldItem item = DataDictionary.getFieldItem(field);
					if (item != null && "1".equals(item.getUseflag())) {
						Element eleField = new Element("field");
						eleField.setAttribute("fldName", field);
						if (subTable.getTitle() != null && subTable.getTitle().length() > 0)
							eleField.setAttribute("fldTitle", subTable.getTitle());
						else
							eleField.setAttribute("fldTitle", item.getItemdesc());
						eleField.setAttribute("fldWidth", subTable.getTitleWidth());
						if (item.getItemlength() >= 255 && "0".equals(item.getCodesetid())
								&& "A".equalsIgnoreCase(item.getItemtype())) {
							eleField.setAttribute("fldType", "M");
							eleField.setAttribute("fldInputType", "0");
						} else {
							eleField.setAttribute("fldType", item.getItemtype());
						}
						eleField.setAttribute("codeSetId", item.getCodesetid());
						eleField.setAttribute("fldLength", item.getItemlength() + "");
						eleField.setAttribute("fldDecLength", item.getDecimalwidth() + "");
						if ("M".equalsIgnoreCase(item.getItemtype()))
							eleField.setAttribute("fldInputType", item.getInputtype() + "");
						eleField.setAttribute("format", subTable.getSlop());
						eleField.setAttribute("align", subTable.getAlign());
						eleField.setAttribute("valign", subTable.getValign());
						eleField.setAttribute("need", subTable.getNeed());
						eleField.setAttribute("his_readonly", subTable.getHis_readonly());
						eleField.setAttribute("imppeople", subTable.getImppeople());
						// 将codeSetId不为0的默认值以指定格式传递到前台
						String codeSetId = item.getCodesetid();
						String defValue = subTable.getDefaultvalue();
						if (!"0".equals(codeSetId) && (!"".equals(defValue) || defValue != null)) {
							String text = AdminCode.getCodeName(codeSetId, defValue);
							defValue = defValue + "`" + text;
						}
						eleField.setAttribute("defaultValue", defValue);
						eleField.setAttribute("pre", subTable.getPre());
						if ("1".equals(nodePriv)) {
							rwPriv = rwPriv2;
						} else {
							/** 数据录入不判断子集和指标权限, 0判断(默认值),1不判断 */
							String insertDataCtrl = paramBo.getUnrestrictedMenuPriv_Input();
							if ("1".equals(insertDataCtrl))
								rwPriv = "2";
							else
								rwPriv = this.userview.analyseFieldPriv(field);
						}

						eleField.setAttribute("rwPriv", rwPriv);
						relationField = subTable.getRelation_field();
						if (StringUtils.isNotBlank(relationField)) {
							relationFieldMap.put(eleField.getAttributeValue("fldName"), relationField);
						}
						eleRoot.addContent(eleField);
					}
					if ("attach".equals(field)) {
						Element eleField = new Element("field");
						eleField.setAttribute("fldName", field);
						if (subTable.getTitle() != null && subTable.getTitle().length() > 0)
							eleField.setAttribute("fldTitle", subTable.getTitle());
						else
							eleField.setAttribute("fldTitle", "附件");
						eleField.setAttribute("fldWidth", subTable.getTitleWidth());
						eleField.setAttribute("fldType", "A");
						eleField.setAttribute("codeSetId", "0");
						eleField.setAttribute("fldLength", "80");
						eleField.setAttribute("fldDecLength", "0");
						eleField.setAttribute("format", subTable.getSlop());
						eleField.setAttribute("align", subTable.getAlign());
						eleField.setAttribute("valign", subTable.getValign());
						eleField.setAttribute("need", subTable.getNeed());
						eleField.setAttribute("defaultValue", subTable.getDefaultvalue());
						eleField.setAttribute("pre", subTable.getPre());
						eleField.setAttribute("rwPriv", "1");
						eleField.setAttribute("his_readonly", subTable.getHis_readonly());
						eleRoot.addContent(eleField);
					}
				}
				if (eleRoot.getContentSize() > 0 && relationFieldMap.size() > 0) {
					Iterator iterator = relationFieldMap.entrySet().iterator();
					while (iterator.hasNext()) {
						Entry entry = (Entry) iterator.next();
						String relationFieldStr = (String) entry.getValue();
						String uniqueId = (String) entry.getKey();
						String fatherRelationField = "";
						Element childFieldBean = null;
						for (int i = 0; i < eleRoot.getContentSize(); i++) {
							Element fieldBean = (Element) eleRoot.getContent(i);
							String fieldname = fieldBean.getAttributeValue("fldName");
							String childRelationField = (String) fieldBean.getAttributeValue("childRelationField");
							if ((!uniqueId.equalsIgnoreCase(fieldname))
									&& relationFieldStr.equalsIgnoreCase(fieldname)) {
								if (StringUtils.isBlank(childRelationField)) {
									childRelationField = uniqueId + ",";
								} else {
									childRelationField += uniqueId + ",";
								}
								fatherRelationField = fieldname;
								fieldBean.setAttribute("childRelationField", childRelationField);
							} else if (uniqueId.equalsIgnoreCase(fieldname)) {
								childFieldBean = fieldBean;
							}
						}
						if (childFieldBean != null) {
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
				Document doc=PubFunc.generateDom(xmldata);
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
														filename = "0".equals(crypyType)?PubFunc.encrypt(filename):PubFunc.decrypt(filename);
														value_+=filename;
													}else if(k==1){
														String filepath = attacharr[1];
														filepath = "0".equals(crypyType)?PubFunc.encrypt(filepath):PubFunc.decrypt(filepath);
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
	public String getArchive_id() {
		return archive_id;
	}
	public void setArchive_id(String archive_id) {
		this.archive_id = archive_id;
	}
	
}
