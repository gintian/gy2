/**
 * 
 */
package com.hjsj.hrms.module.template.historydata.formcorrelation.utils.javabean;

import com.hjsj.hrms.utils.PubFunc;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import java.util.ArrayList;
import java.util.List;

/**
 * 子集区域类，包含子集区域所有的参数及其他单元格的参数
* @Title: SubSetDomain
* @Description:
* @author: hej
* @date 2019年11月19日 下午5:16:34
* @version
 */
public class SubSetDomain {
	private String setName;//子集名称
	private boolean bHLine;//有横线
	private boolean bVLine;//有竖线
	private boolean bColHead;//启用列标题设置
	private int colHeadHeight = 0;//行高
	/**子集显示指标 逗号分隔*/
	private String subFields="";
	private ArrayList subFieldList = new ArrayList();
	private int dataRowCount = 0;//数据行数
	private String subDomain;	
	/**多个同名指标的标识*/
	private String subDomainId="";
	/**单位、部门、岗位是否按管理范围控制*/
	private boolean bLimitManagePriv=false;
	/**附件类型 ，个人：0 ，    公共： 1*/
	private String attachmentType="0";
	/** 临时变量 只读控制 1:只读 2：可编辑*/
	private String readOnly = "2";//默认可编辑
	/** 子集历史记录（引入的）是否可编辑  1：可编辑  0：不可编辑 */
	private String his_edit = "1";//默认可编辑
	/**允许删除历史记录 "0"不允许（默认）  "1"允许 */
    private String allow_del_his = "0";
    private String file_type="";//附件归档到哪个分类
	private String mustfillrecord="false";//子集记录必填	
	private String default_value="";
	private String imppeople="";//启用选人
	private String relation_field="";//关联指标
	public String getDefault_value() {
		return default_value;
	}

	public String getImppeople() {
		return imppeople;
	}

	public String getRelation_field() {
		return relation_field;
	}
	
	public String getMustfillrecord() {
		return mustfillrecord;
	}

	public void setMustfillrecord(String mustfillrecord) {
		this.mustfillrecord = mustfillrecord;
	}

	public SubSetDomain(String xmls)
	{
		this.subDomain=xmls;
		parse_subdomain();
	}	

	/**
	 * 解释子集区域定义
	 * @param xmls
	 */
	private void parse_subdomain()
	{
		if(this.subDomain==null|| "".equals(this.subDomain)|| "null".equals(this.subDomain))
			return;
		Document doc=null;
		Element element=null;
		try
		{
			doc=PubFunc.generateDom(subDomain);
			String xpath="/sub_para/para";
			XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
			List childlist=findPath.selectNodes(doc);	
			if(childlist!=null&&childlist.size()>0)
			{
				element=(Element)childlist.get(0);
				this.setName=(String)element.getAttributeValue("setname");
				this.subFields=(String)element.getAttributeValue("fields");
				this.dataRowCount =0;
				this.colHeadHeight =0;
				if (element.getAttributeValue("datarowcount")!=null)
				  this.dataRowCount = Integer.parseInt((String)element.getAttributeValue("datarowcount"));
				if (element.getAttributeValue("colheadheight")!=null)
					this.colHeadHeight = Integer.parseInt((String)element.getAttributeValue("colheadheight"));
				this.bHLine=Boolean.parseBoolean((String)element.getAttributeValue("hl"));				
				this.bVLine=Boolean.parseBoolean((String)element.getAttributeValue("vl"));				
				this.bColHead=Boolean.parseBoolean((String)element.getAttributeValue("colhead"));	
				
				subDomainId=""; 
				if (element.getAttributeValue("id") != null) {
					subDomainId = (String) element.getAttributeValue("id");
	            }
				if (element.getAttributeValue("limit_manage_priv") != null) {
				    bLimitManagePriv = "1".equals((String) element.getAttributeValue("limit_manage_priv"));
				}
				if (element.getAttributeValue("attachmentType") != null) {
					attachmentType = (String) element.getAttributeValue("attachmentType");
				}
				if (element.getAttributeValue("readOnly") != null) {
					readOnly = (String) element.getAttributeValue("readOnly");
				}
				if (element.getAttributeValue("his_edit") != null) {
					his_edit = (String) element.getAttributeValue("his_edit");
				}
				if (element.getAttributeValue("allow_del_his") != null) {
					allow_del_his = (String) element.getAttributeValue("allow_del_his");
				}
				if (element.getAttributeValue("mustfillrecord") != null) {
					mustfillrecord = (String) element.getAttributeValue("mustfillrecord");
				}
				if (element.getAttributeValue("file_type") != null) {
					file_type = (String) element.getAttributeValue("file_type");
				}
				if (element.getAttributeValue("relation_field") != null) {
					relation_field = (String) element.getAttributeValue("relation_field");
				}
				if (element.getAttributeValue("default_value") != null) {
					default_value = (String) element.getAttributeValue("default_value");
				}
				if (element.getAttributeValue("imppeople") != null) {
					imppeople = (String) element.getAttributeValue("imppeople");
				}
			}
			xpath="/sub_para/field";
			findPath = XPath.newInstance(xpath);// 取得符合条件的节点
			childlist=findPath.selectNodes(doc);			
			if(childlist!=null&&childlist.size()>0)
			{
				 ArrayList sublist = new ArrayList();
				  for (int i = 0; i < childlist.size(); i++) {
	                     element = (Element) childlist.get(i);
	                     SubField subtable = new SubField();
	                     subtable.setFieldname(element.getAttributeValue("name") == null ? "" : element.getAttributeValue("name"));
	                     subtable.setAlign(element.getAttributeValue("align") == null ? "" : element.getAttributeValue("align"));
	                     subtable.setDefaultvalue(element.getAttributeValue("default") == null ? "" : element.getAttributeValue("default"));
	                     subtable.setNeed(element.getAttributeValue("need") == null ? "" : element.getAttributeValue("need"));
	                     subtable.setPre(element.getAttributeValue("pre") == null ? "" : element.getAttributeValue("pre"));
	                     subtable.setSlop(element.getAttributeValue("slop") == null ? "" : element.getAttributeValue("slop"));
	                     subtable.setTitle(element.getAttributeValue("title") == null ? "" : element.getAttributeValue("title"));
	                     subtable.setValign(element.getAttributeValue("valign") == null ? "" : element.getAttributeValue("valign"));
	                     subtable.setImppeople(element.getAttributeValue("imppeople") == null ? "false" : element.getAttributeValue("imppeople"));
	                     //子集计算公式定义
	                     List chileele = element.getChildren();
	                     for(int j=0;j<chileele.size();j++) {
	                    	 Element element_ =  (Element) chileele.get(j);
	                    	 String name = element_.getName();
	                    	 if("formula".equalsIgnoreCase(name)) {
	                    		 subtable.setFormula(element_.getValue() == null ? "" : element_.getValue());
	                    	 }
	                    	 else if("cond".equalsIgnoreCase(name)) {
	                    		 subtable.setCond(element_.getValue() == null ? "" : element_.getValue());
	                    	 }
	                     }
	                     //显示合计
	                     subtable.setTotal(element.getAttributeValue("total") == null ? "" : element.getAttributeValue("total"));
	                     
	                     String width =element.getAttributeValue("width")==null?"0":element.getAttributeValue("width");
	                     int pt =(Integer.valueOf(width).intValue())*8;
	                     subtable.setTitleWidth(String.valueOf(pt));
	                     subtable.setTitleHeight(String.valueOf(colHeadHeight));
	                     String his_readonly = (String)element.getAttributeValue("his_readonly");
	 					 if(StringUtils.isBlank(his_readonly))
	 						 his_readonly = "false";
	 					 if("true".equals(his_readonly)&&"1".equals(this.his_edit))
	 						 this.his_edit = "0";
	 					 subtable.setHis_readonly(his_readonly);
	 					 subtable.setRelation_field(element.getAttributeValue("relation_field") == null ? "" : element.getAttributeValue("relation_field"));
	                     sublist.add(subtable);
	                 }
				  //按fields循环，使得sublist的数据中的Fieldname跟fields中的数据一一对应
	                 ArrayList newSublist = new ArrayList();
	                 String []columns=this.subFields.split("`");
	                 for(int i=0;i<columns.length;i++){
	                	 for(int j=0;j<sublist.size();j++){
	                		 SubField subtable = (SubField)sublist.get(j);
	                		 if(columns[i].toUpperCase().equals(subtable.getFieldname().toUpperCase())){
	                			 newSublist.add(subtable);
	                			 break;
	                		 }
	                	 }
	                 }
	                 this.setSubFieldList(newSublist);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}	
	}


	public String getSetName() {
		return setName;
	}


	public void setSetName(String setName) {
		this.setName = setName;
	}


	public boolean isHasHLine() {
		return bHLine;
	}

	public boolean isHasVLine() {
		return bVLine;
	}

	public boolean isbColHead() {
		return bColHead;
	}


	public int getColHeadHeight() {
		return colHeadHeight;
	}


	public void setColHeadHeight(int colHeadHeight) {
		this.colHeadHeight = colHeadHeight;
	}


	public String getSubFields() {
		return subFields;
	}


	public void setSubFields(String subFields) {
		this.subFields = subFields;
	}


	public ArrayList getSubFieldList() {
		return subFieldList;
	}


	public void setSubFieldList(ArrayList subFieldList) {
		this.subFieldList = subFieldList;
	}


	public int getDataRowCount() {
		return dataRowCount;
	}


	public void setDataRowCount(int dataRowCount) {
		this.dataRowCount = dataRowCount;
	}


	public String getSubDomain() {
		return subDomain;
	}


	public void setSubDomain(String subDomain) {
		this.subDomain = subDomain;
	}


	public String getSubDomainId() {
		return subDomainId;
	}


	public void setSubDomainId(String subDomainId) {
		this.subDomainId = subDomainId;
	}


    public boolean isBLimitManagePriv() {
        return bLimitManagePriv;
    }


    public void setBLimitManagePriv(boolean limitManagePriv) {
        bLimitManagePriv = limitManagePriv;
    }


	public String getAttachmentType() {
		return attachmentType;
	}


	public void setAttachmentType(String attachmentType) {
		this.attachmentType = attachmentType;
	}


	public String getReadOnly() {
		return readOnly;
	}


	public void setReadOnly(String readOnly) {
		this.readOnly = readOnly;
	}


	public String getHis_edit() {
		return his_edit;
	}


	public void setHis_edit(String his_edit) {
		this.his_edit = his_edit;
	}
	
	
	public String getAllow_del_his() {
		return allow_del_his;
	}


	public void setAllow_del_his(String allow_del_his) {
		this.allow_del_his = allow_del_his;
	}

	public String getFile_type() {
		return file_type;
	}


	public void setFile_type(String file_type) {
		this.file_type = file_type;
	}
}
