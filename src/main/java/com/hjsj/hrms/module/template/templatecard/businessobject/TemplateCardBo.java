/**
 * 
 */
package com.hjsj.hrms.module.template.templatecard.businessobject;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.businessobject.ykcard.TSyntax;
import com.hjsj.hrms.module.template.templatesubset.businessobject.TemplateSubsetBo;
import com.hjsj.hrms.module.template.utils.TemplateDataBo;
import com.hjsj.hrms.module.template.utils.TemplateUtilBo;
import com.hjsj.hrms.module.template.utils.javabean.TemplateParam;
import com.hjsj.hrms.module.template.utils.javabean.TemplateSet;
import com.hjsj.hrms.servlet.ServletUtilities;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.ykcard.TRecParamView;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;
import com.hrms.virtualfilesystem.service.VfsCategoryEnum;
import com.hrms.virtualfilesystem.service.VfsFiletypeEnum;
import com.hrms.virtualfilesystem.service.VfsModulesEnum;
import com.hrms.virtualfilesystem.service.VfsService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.io.InputStream;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;


/**
 * <p>Title:TemplatePageBo.java</p>
 * <p>Description>:</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2015-12-18 上午10:13:22</p>
 * <p>@version: 7.0</p>
 */
public class TemplateCardBo {
    private Connection conn=null;
    private UserView userView = null;
	private int tabId=-1;
	private String task_id="";//批量审批时，会有多个id
    private String selfApply="0";// =1 自助用户申请
    
    private String basePre="";
    private String a0100="";
    private String b0110="";
	private String e01a1="";
	private String objectId="";//当前查看的人员
	private String curTaskId="";//当前查看的人员的流程号
    /** 区分报审、报备、加签 
     * 1：报审 2：加签  3 报备 
    */
    private String approveFlag="0";      
	
    private HashMap fieldValueMap =null;//存放各个input中的数据
	
    private ArrayList cardCellList=null;//当前模板页所有单元格 不要直接调用this.
    
    private TemplateParam paramBo;
    private TemplateUtilBo utilBo;    
	private HashMap changeInfoLMap=new HashMap();//变动字段记录

    /**
     * @param conn
     * @param tabid
     * @param pageid
     * @return 
     */
    public TemplateCardBo(Connection conn,UserView userview,int tabid ) {
        this.conn = conn;
        this.userView = userview;
        this.tabId = tabid;
        this.paramBo=new TemplateParam(this.conn,this.userView,tabid);
		utilBo = new TemplateUtilBo(this.conn,this.userView);
    }
    
    public TemplateCardBo(Connection conn,UserView userview,TemplateParam paramBo) {
        this.conn = conn;
        this.userView = userview;
        this.paramBo=paramBo;
        this.tabId = paramBo.getTabId();
        utilBo = new TemplateUtilBo(this.conn,this.userView);
    }
    
    
	  /** 
	* @Title: getAllCell 
	* @Description: 
	* @param @return
	* @return ArrayList
	*/ 
    public ArrayList getAllCell() { 
		if (cardCellList==null)
			cardCellList=  utilBo.getPageCell(this.tabId,-1); 
	      return cardCellList;
	  }  
	
    
    /** 
     * @Title: getFieldList 
     * @Description: 获取前台使用指标类型指标模型。存储指标的各种值：类型、权限等
     * @param @return
     * @param @throws Exception
     * @return ArrayList
     */ 
     public ArrayList getFieldList()throws Exception {
		ArrayList fieldList = new ArrayList();
		try {
			HashMap relationFieldMap=new HashMap();
			ArrayList cellList = getAllCell();
			LazyDynaBean bean = new LazyDynaBean();
			HashMap privMap = getFieldPrivMap(cellList);
			for (int i = 0; i < cellList.size(); i++) {
				TemplateSet setBo = (TemplateSet) cellList.get(i);
				String flag = setBo.getFlag();
				if ("".equals(flag) || "H".equalsIgnoreCase(flag)) {
					continue;
				} 
				if ("P".equalsIgnoreCase(flag)) { // 添加照片
					bean = getInnerFieldBean(setBo.getPageId()+"",setBo.getUniqueId(), "photo",
							"photo", "blob", false, "", "0",setBo.getGridno());
					fieldList.add(bean);
					if (!"1".equals(this.getApproveFlag())){//非起草报审
					    bean.set("rwPriv","1"); 
		            }
					else {
					    bean.set("rwPriv","2"); 
					}   					   
					bean = getInnerFieldBean(setBo.getPageId()+"",setBo.getUniqueId() + "_ext",
							"ext", "ext", "string", false, "", "0",setBo.getGridno());
					fieldList.add(bean);
					continue;
				} 
				if (setBo.isVarItem() && setBo.getVarVo()==null){
				    continue;
				}
				
				bean = getFieldBean(setBo,privMap);
				if(StringUtils.isNotBlank(setBo.getRelation_field())&&2==setBo.getChgstate()){
					relationFieldMap.put(setBo.getUniqueId(), setBo.getRelation_field());
				}
				if(StringUtils.isNotBlank(setBo.getDefaultValue())){
					bean.set("defaultValue",setBo.getDefaultValue());
				}
				if(StringUtils.isNotBlank(setBo.getIsMobile())){
					bean.set("ismobile",setBo.getIsMobile());
				}
				fieldList.add(bean);
			}
			if(relationFieldMap.size()>0){
				Iterator iterator = relationFieldMap.entrySet().iterator();
				while(iterator.hasNext()){
					Entry entry=(Entry)	iterator.next();
					String relationField = (String)entry.getValue();
					String uniqueId=(String)entry.getKey();
					String fatherRelationUniqueId="";
					LazyDynaBean childFieldBean=null;
					for(int i=0;i<fieldList.size();i++){
						LazyDynaBean fieldBean=(LazyDynaBean) fieldList.get(i);
						String fieldUniqueId = (String) fieldBean.get("uniqueId");
						String fieldfldName = fieldBean.get("pageId")+"_"+fieldBean.get("gridno");
						String childRelationField = (String) fieldBean.get("childRelationField");
						if((!uniqueId.equalsIgnoreCase(fieldUniqueId))&&relationField.equalsIgnoreCase(fieldfldName)){
							if(StringUtils.isBlank(childRelationField)){
								childRelationField=uniqueId+",";
							}else{
								childRelationField+=uniqueId+",";
							}
							fatherRelationUniqueId=fieldUniqueId;
							fieldBean.set("childRelationField", childRelationField);
							}else if(uniqueId.equalsIgnoreCase(fieldUniqueId)){
								childFieldBean=fieldBean;
							}
					}
					if(childFieldBean!=null){
						childFieldBean.set("fatherRelationField", fatherRelationUniqueId);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return fieldList;
     }
     
     public String getCodeDesc(String codesetid,String codeValue) {
         String retValue="";
         String [] arrValue = codeValue.split("`");
         for (int i =0 ; i<arrValue.length;i++ ){
             String id = arrValue[i];
             String value = AdminCode.getCodeName(codesetid,id);
             if ("".equals(retValue)){
                 retValue=value;  
             }
             else {
                 retValue=retValue+"<br>"+value;
             }
         }
         return retValue;
     }
 
    /** 
     * @Title: getPageFieldValueList 
     * @Description:获取前台用到的值，填充所有单元格及子集 
     * @param @return
     * @param @throws Exception
     * @return ArrayList
     */ 
	public ArrayList getFieldValueList() throws Exception {
		ArrayList fieldValueList = new ArrayList();
		try {
			HashMap valueMap = getFieldValueMap();
			LazyDynaBean bean = new LazyDynaBean();
			String fldValue = "";
			String staitic_="static";
            if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
            	staitic_="static_o";
            }
			try {
				ArrayList cellList = getAllCell();
				for (int i = 0; i < cellList.size(); i++) {
					String disValue = "";
					Boolean modified=false;
					TemplateSet setBo = (TemplateSet) cellList.get(i);
					String flag = setBo.getFlag();
					if ("".equals(flag) || "H".equalsIgnoreCase(flag)) {
						continue;
					}
					if ("S".equalsIgnoreCase(flag)
							|| "T".equalsIgnoreCase(flag)) {
						continue;
					} 
					//判断是否构库
					if (setBo.isABKItem() || setBo.isVarItem()){
					    if (!setBo.isExistsThisField()){
					        continue;
					    }
					}

					if ("P".equalsIgnoreCase(flag)) {// 照片
						String ext = (String) valueMap.get("ext") == null ? ""
								: (String) valueMap.get("ext");
						fldValue = "/images/photo.jpg";
						if ("nophoto".equalsIgnoreCase(ext)
								|| "".equalsIgnoreCase(ext)) {
							ext = "";
						} else {
							fldValue = (String) valueMap.get("photo");
						}
						bean = getFieldValueBean(setBo.getPageId()+"",setBo.getUniqueId() + "_ext",
								"ext", ext, ext);
						fieldValueList.add(bean);
						bean = getFieldValueBean(setBo.getPageId()+"",setBo.getUniqueId(), "photo",
								fldValue, fldValue);
						fieldValueList.add(bean);
					}	else if ("C".equals(flag)){
					    fldValue = getValue(valueMap, setBo.getTableFieldName());
					    bean = getFieldValueBean(setBo.getPageId()+"",setBo.getUniqueId(), setBo.getTableFieldName(),
                                fldValue, fldValue);
                        fieldValueList.add(bean);
					}
					else {
						String Filedtype = setBo.getField_type() == null ? "": setBo.getField_type();
						boolean isSubflag = setBo.isSubflag();
						String tabFldName = setBo.getTableFieldName();
						fldValue = getValue(valueMap, tabFldName);
						disValue = fldValue;
						if (isSubflag) {
							TemplateSubsetBo  subsetBo=new TemplateSubsetBo(this.conn,this.userView,
									String.valueOf(this.tabId),tabFldName,setBo.getXml_param(),this.approveFlag);
							HashMap subDataMap=subsetBo.getSubDataMap(fldValue);
							fldValue = subsetBo.encryptOrDecryptAttachment(fldValue,"0");
							JSONObject subDatajson = JSONObject.fromObject(subDataMap); 
							disValue = subDatajson.toString().replace("~", "～");//天津工业大学子集中有~子集不显示。
							disValue = subDatajson.toString().replace("^", "＾");//子集中有^子集不显示。
							/*fldValue = SafeCode.encode(fldValue);
							disValue = SafeCode.encode(disValue);*/
							
							bean = getFieldValueBean(setBo.getPageId()+"",setBo.getUniqueId() + "",
									tabFldName, fldValue, disValue);
							fieldValueList.add(bean);
						} else {
							if ("N".equals(Filedtype)) {
								if (fldValue == null) {
									fldValue = "";
								} else if (fldValue.length() > 0) {
									if (Float.parseFloat(fldValue) == 0) {// 为了解决数字型代码没有值的时候不存0
										fldValue = "0";
									}
								}
							} else if ("M".equals(Filedtype)) {
								fldValue = fldValue.replaceAll(",", "````");// 豆号“,”为字符串的分隔符
                            } else {
								fldValue = fldValue.replaceAll(",", "````");// 豆号“,”为字符串的分隔符
							}
							disValue = fldValue;
							//按格式显示
							if ("D".equals(Filedtype)) {
							    disValue=utilBo.getFormatDate(fldValue,setBo.getDisformat());
						    } else if ("N".equals(Filedtype)) {
						    	//disValue =PubFunc.DoFormatDecimal(fldValue,setBo.getDisformat());
						    	FieldItem fielditem = DataDictionary.getFieldItem(setBo.getField_name());
						    	if(fielditem!=null) {
									int decimal = fielditem.getDecimalwidth();
									int slop = setBo.getDisformat();
									if(slop<decimal)
										decimal = slop;
									fldValue=PubFunc.DoFormatDecimal(fldValue,decimal);
						    	}
						    	disValue = fldValue;
							}
						    else {
						        if (setBo.isBcode()) {
						        	if("parentid".equals(setBo.getField_name())){//特殊字段,上级机构 lis 20160706
						        		disValue= getCodeDesc(setBo.getCodeid(),fldValue);
						        		if("UM".equalsIgnoreCase(setBo.getCodeid())){
					        				Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
					        				String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
					        				if(display_e0122==null|| "00".equals(display_e0122)|| "".equals(display_e0122))
					        					display_e0122="0";
					        				if(Integer.parseInt(display_e0122)>0){
					        					CodeItem item=AdminCode.getCode("UM",fldValue,Integer.parseInt(display_e0122));
					        					if(item!=null){
					        						disValue = item.getCodename();
					        		    		}else{
					        		    			disValue = AdminCode.getCodeName("UM",fldValue);
					        		    			if(StringUtils.isBlank(disValue))//部门选择单位，前台不显示单位名称
									        			disValue= getCodeDesc("UN",fldValue);
									        		else if(StringUtils.isBlank(disValue))
									        			disValue= getCodeDesc("@K",fldValue);
					        			    	}
					        				}
					        			}
						        		if(StringUtils.isBlank(disValue))
						        			disValue= getCodeDesc("UN",fldValue);
						        		else if(StringUtils.isBlank(disValue))
						        			disValue= getCodeDesc("@K",fldValue);
						        	}else{
						        		disValue= getCodeDesc(setBo.getCodeid(),fldValue);
						        		if("UM".equals(setBo.getCodeid())&&"".equals(disValue)){//兼容关联UM 选择UN代码的情况
						        			disValue = getCodeDesc("UN",fldValue);
						        		}
						        		//部门设置层级显示的情况
						        		if("E0122".equalsIgnoreCase(setBo.getField_name())&&"UM".equalsIgnoreCase(setBo.getCodeid())){
					        				Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
					        				String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
					        				if(display_e0122==null|| "00".equals(display_e0122)|| "".equals(display_e0122))
					        					display_e0122="0";
					        				if(Integer.parseInt(display_e0122)>0){
					        					CodeItem item=AdminCode.getCode("UM",fldValue,Integer.parseInt(display_e0122));
					        					if(item!=null){
					        						disValue = item.getCodename();
					        		    		}else{
					        		    			if(StringUtils.isBlank(disValue))//部门选择单位，前台不显示单位名称
									        			disValue= getCodeDesc("UN",fldValue);
									        		else if(StringUtils.isBlank(disValue))
									        			disValue= getCodeDesc("@K",fldValue);
					        			    	}
					        				}
					        			}
						        	}
						        }else if(setBo.isSpecialItem()){// 判断是特殊字段 lis 20160706
						        	if("codesetid".equals(setBo.getField_name())){//组织机构类型
						        		if("UN".equals(fldValue))
						        			disValue = ResourceFactory.getProperty("label.codeitemid.un");//"单位";
						        		else if("UM".equals(fldValue))
						        			disValue = ResourceFactory.getProperty("label.codeitemid.um");//"部门";
						        	}
						        }
						    }
							
							bean = getFieldValueBean(setBo.getPageId()+"",setBo.getUniqueId() + "",
									tabFldName, fldValue, disValue,modified);
							if(setBo.getTableFieldName()!=null&&(this.paramBo.getIsAotuLog()||this.paramBo.getIsRejectAotuLog())&&this.changeInfoLMap.containsKey(setBo.getField_name().toLowerCase())&&setBo.getChgstate()==2){//判断是否在变动记录中
								bean.set("isAutoLog", true);
							}else{
								bean.set("isAutoLog", false);
							}
							if("M".equals(Filedtype)){
								FieldItem fieldItem = DataDictionary.getFieldItem(setBo.getField_name().toLowerCase());
								bean.set("length", fieldItem.getItemlength());
							}
							fieldValueList.add(bean);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (this.paramBo.getTable_vo().getInt(staitic_) == 10) {// 单位
				fldValue = getValue(valueMap, "b0110");
				bean = getFieldValueBean("","", "b0110", fldValue, fldValue);
				fieldValueList.add(bean);
			} else if (this.paramBo.getTable_vo().getInt(staitic_) == 11) {// 岗位
				fldValue = getValue(valueMap, "e01a1");
				bean = getFieldValueBean("","", "e01a1", fldValue, fldValue);
				fieldValueList.add(bean);
			}else{
			    fldValue = getValue(valueMap, "basepre");
                bean = getFieldValueBean("","", "basepre", fldValue, fldValue);
                fieldValueList.add(bean);
                fldValue = getValue(valueMap, "a0100");
                bean = getFieldValueBean("","", "a0100", fldValue, fldValue);
                fieldValueList.add(bean);
			
			}
			if (!"0".equals(this.task_id)) {
			    fldValue = getValue(valueMap, "ins_id");
                bean = getFieldValueBean("","", "ins_id", fldValue, fldValue);
                fieldValueList.add(bean);
            }
			//电子签章
			fldValue = getValue(valueMap, "signature");
			//得到签章显示的具体属性
			if(!"".equals(fldValue)){
				fldValue = SafeCode.encode(fldValue);
			}
            bean = getFieldValueBean("","", "signature", fldValue, fldValue);
            fieldValueList.add(bean);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return fieldValueList;
	}
	/** 
     * @Title: getFieldPriv 
     * @Description: 
     * @param @param allCellList
     * @param @return
     * @return HashMap
     */ 
     private HashMap getFieldPrivMap(ArrayList allCellList) {
        TemplateDataBo dataBo = new TemplateDataBo(this.conn,this.userView,this.paramBo);
        HashMap filedPrivMap=dataBo.getFieldPrivMap(allCellList, this.task_id);
		return filedPrivMap;
     }

   
     
     //
     /** 
     * @Title: getInnerFieldBean 
     * @Description: 返回指标类型Bean
     * @param @param uniqueId 指标唯一值
     * @param @param fldName 指标名称
     * @param @param fldDesc 指标名称
     * @param @param fldType 指标类型
     * @param @param subFlag 是否子集
     * @param @param codeset 代码类
     * @param @param format 指标显示格式
     * @param @return
     * @return LazyDynaBean
     */ 
     private LazyDynaBean getInnerFieldBean(String pageId,String uniqueId,String fldName,String fldDesc,String fldType,
     		boolean subFlag,String codeset,String format,int gridno) {
 	    LazyDynaBean bean = new LazyDynaBean();
 	    bean.set("pageId", pageId);
 	    bean.set("uniqueId", uniqueId);
 	    bean.set("fldName", fldName);
 	    bean.set("fldType",fldType); 
 	    bean.set("fldDesc",fldDesc); 	    
 	    bean.set("format",format); 
 	    bean.set("visible","true"); 
 	    bean.set("codeSetId",codeset); 	    
 	    bean.set("subFlag",subFlag); 
 	    bean.set("subXml",""); 
 	    bean.set("fldLength","30"); 
 	    bean.set("fldDecLength","2"); 	    
 	    bean.set("rwPriv","0"); 	    
 	    bean.set("chgState",""); 	    
 	    bean.set("attachmentType","0"); 	    
 	    bean.set("fatherRelationField","");//联动父级指标
 	    bean.set("childRelationField","");//联动孩子指标
		bean.set("defaultValue","");//默认值
		bean.set("gridno",gridno);
		bean.set("ismobile","");
         return bean;
     } 

	/**
	 * @Title: getFieldBean
	 * @Description: 返回指标类型Bean
	 * @param @param setBo
	 * @param @return
	 * @return LazyDynaBean
	 */
	private LazyDynaBean getFieldBean(TemplateSet setBo, HashMap privMap) {
		String codeSetId = "";
		int format = setBo.getDisformat();
		String fldType = setBo.getField_type();
		String fieldName = setBo.getField_name();
		String flag = setBo.getFlag();
		if (setBo.isSubflag()) {// 子集数据
			fldType = "clob";
			fieldName = setBo.getSetname();
			// 判断子集是否取消构库了 todo
		} else {
			if (setBo.isABKItem() || "V".equalsIgnoreCase(setBo.getFlag())) {
				if ("A".equals(fldType)) {// 字符型
					if ("codesetid".equals(fieldName)) {
						codeSetId = "UK";
					} else if ("parentid".equals(fieldName)) {// 上级结点排除
						codeSetId = "UM";
					} else if ("codeitemdesc".equals(fieldName)
							|| "corcode".equals(fieldName)) {// 排除特定结点
						codeSetId = "";
					} else {// 不是特殊结点的
						if ("V".equalsIgnoreCase(flag)) {// 如果是临时变量
							codeSetId = setBo.getVarVo().getString("codesetid");
						} else {// 不是临时变量不是特殊结点
							FieldItem fielditem = DataDictionary
									.getFieldItem(setBo.getField_name());
							if (fielditem != null) {
								codeSetId = fielditem.getCodesetid();
							}
						}
					}
				} else {// 不是字符型
					if (!"V".equalsIgnoreCase(flag) && !setBo.isSpecialItem()) {
						FieldItem item = DataDictionary.getFieldItem(setBo
								.getField_name());
						if (item != null) {
							codeSetId = item.getCodesetid();
						}
					}
				}

			}
		}
		//显示格式
		String strformat = format + "";
		if ("D".equals(fldType)) {
			//String disformat = TemplateFuncBo.getDataFormatByDis(format);
			//strformat = disformat;
		}
		//增加字段属性
		LazyDynaBean bean = new LazyDynaBean();
		bean = getInnerFieldBean(setBo.getPageId()+"",setBo.getUniqueId(), fieldName, setBo.
				getField_hz(), fldType, setBo.isSubflag(), codeSetId,strformat,setBo.getGridno());
		if("M".equals(fldType)&&setBo.getField_name().trim().length()>0)
		{
			FieldItem fielditem = DataDictionary.getFieldItem(setBo.getField_name());
			if(fielditem!=null) {
				bean.set("inputType", fielditem.getInputtype());
				bean.set("limitlength", fielditem.getItemlength());//大文本限制长度
			}
			bean.set("imppeople", setBo.getImppeople());//设置启用选人组件
			
		}
		if("A".equalsIgnoreCase(setBo.getField_type())&&"0".equalsIgnoreCase(setBo.getCodeid())){
			bean.set("imppeople", setBo.getImppeople());//设置启用选人组件
			FieldItem fielditem = DataDictionary.getFieldItem(setBo.getField_name());
			if(fielditem!=null) {
				if(fielditem.getItemlength()>=255){
					bean.set("limitlength", fielditem.getItemlength());//大文本限制长度
				}
			}
		}
		bean.set("flag", setBo.getFlag());
		bean.set("attachmentType", setBo.getAttachmentType());
		bean.set("chgState", setBo.getChgstate() + "");
		if (setBo.isSubflag()) {// 添加子集指标属性
			/*不需要这个逻辑了
			TemplateSubsetBo subsetBo = new TemplateSubsetBo(this.conn,
					this.userView, String.valueOf(this.tabId), fieldName, setBo
							.getXml_param(),this.approveFlag);
			String subXml = subsetBo.getSubFieldsPropertys();
			subXml = SafeCode.encode(subXml);
			bean.set("subXml", subXml);
			*/
		}

		if (privMap.get(setBo.getUniqueId()) != null) {
		    String rwPriv = (String)privMap.get(setBo.getUniqueId());
		    String opinion_field = this.paramBo.getOpinion_field();
		    if (StringUtils.isNotBlank(opinion_field) && opinion_field.equals(setBo.getField_name())){//审批意见指标
		        if (!"0".equals(rwPriv)){
                    rwPriv="1";
                }
		    }
		    if ((setBo.getChgstate()==1 
		    		&& !"F".equalsIgnoreCase(setBo.getFlag())&& !"S".equalsIgnoreCase(setBo.getFlag()))){//非起草报审 或者变化前
		        if (!"0".equals(rwPriv)){
		            rwPriv="1";//全部置为可读
		        }
		    }
		    if (!"1".equals(this.getApproveFlag())){//浏览打印 全部置为可读
		        if (!"0".equals(rwPriv)){
		            rwPriv="1";//
		        }
		    }
			bean.set("rwPriv",rwPriv );
		}
		//linbz  方案查询用
		bean.set("tableFieldName",setBo.getTableFieldName());
		String nameHz = setBo.getHz().toString();
		//29177 模板设定的指标名称后会带 ` 字符，这里全部替换为空
		nameHz = nameHz.replaceAll("`", "");
		bean.set("hz",nameHz);
		/**单位、部门、岗位是否按管理范围控制 =0不走，=3走*/
		bean.set("ctrltype", setBo.isBLimitManagePriv()?"3":"0");
		//29492  用于过滤主集指标
		bean.set("setName", setBo.getSetname());
		return bean;
	}
     
 
      /** 
       * @Title: getFieldValueBean 
       * @Description:   //返回指标值的bean。
       * @param @param fieldName 指标名称 带变化前
       * @param @param fieldValue
       * @param @param disValue
       * @param @return
       * @return LazyDynaBean
       */ 
       private LazyDynaBean getFieldValueBean(String pageId,String uniqueId,String fldName,String fldValue,String disValue) {
   	    LazyDynaBean bean = new LazyDynaBean();
   	    bean.set("pageId", pageId);
   	    bean.set("uniqueId", uniqueId);
   	    bean.set("fldName", fldName);
   	    bean.set("keyValue",fldValue); 
   	    bean.set("disValue",disValue); 
   	    bean.set("modified",false); 
           return bean;
       }
       private LazyDynaBean getFieldValueBean(String pageId,String uniqueId,String fldName,String fldValue,String disValue,boolean modified) {
      	    LazyDynaBean bean = new LazyDynaBean();
      	    bean.set("pageId", pageId);
      	    bean.set("uniqueId", uniqueId);
      	    bean.set("fldName", fldName);
      	    bean.set("keyValue",fldValue); 
      	    bean.set("disValue",disValue); 
      	    bean.set("modified",modified); 
              return bean;
       }   
       /** 
       * @Title: getValue 
       * @Description: 从valueMap中取到相应字段的值。
       * @param @param fldName
       * @param @return
       * @return String
       */ 
       private String getValue(HashMap valueMap ,String fldName)  {
       	String fldValue="";
       	 if (valueMap.size() > 0) {
       		 if (valueMap.get(fldName)!=null){
       			 fldValue=(String)valueMap.get(fldName); 
       			 if (fldValue==null) fldValue="";
       		 }
       	 }
       	return fldValue;
       }

       /**
        * 
        * @Title: getQuerySql
        * @Description: 得到查询数据的sql语句
        * @return String 返回该sql语句
        * @throws
        */
       private String getQuerySql() {
        StringBuffer sqlbuffer = new StringBuffer();
        StringBuffer wherebuffer = new StringBuffer();
        String sql = "";
        try {
        	String ins_id = utilBo.getInsId(this.getCurTaskId());
        	String staitic_="static";
            if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
            	staitic_="static_o";
            }
            sqlbuffer.append("select * ");
            if (this.paramBo.getTable_vo().getInt(staitic_) == 10) {
                if (!"".equals(this.b0110)) {
                    wherebuffer.append(" where b0110='" + this.b0110 + "'");
                } else {
                    wherebuffer.append(" where 1=2");
                }
            } else if (this.paramBo.getTable_vo().getInt(staitic_) == 11) {
                if (!"".equals(this.e01a1)) {
                    wherebuffer.append(" where e01a1='" + this.e01a1 + "'");
                } else {
                    wherebuffer.append(" where 1=2");
                }
            } else {
                if (!"".equals(this.a0100) && !"".equals(this.basePre)) {
                    wherebuffer.append(" where a0100='" + this.a0100 + "' and lower(basepre)='" + this.basePre.toLowerCase() + "'");
                } else {
                    wherebuffer.append(" where 1=2");
                }
            }
            if (!"0".equals(ins_id)) {
                wherebuffer.append(" and ins_id ="+ins_id);
            }
            sqlbuffer.append(" from ");
            sqlbuffer.append("templet_" + this.tabId);
            if (wherebuffer.length() > 0) {
                sqlbuffer.append(wherebuffer.toString());
            } else {
                sqlbuffer.append(" where 1=2");
            }
            sqlbuffer.append(" order by a0000");
            sql = sqlbuffer.toString();
            if ("0".equals(this.task_id)) {
                if (!"0".equals(this.selfApply)) {// 个人业务申请 查询数据 "g_templet_"
                                                    // + tabid
                    sql = sql.replaceAll("templet_" + this.tabId, "g_templet_" + this.tabId);
                } else {
                    sql = sql.replaceAll("templet_" + this.tabId, this.userView.getUserName() + "templet_" + this.tabId);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return sql;
       }
      /**
         * 
         * @Title: getFieldValueMap
         * @Description: 得到查询的数据
         * @param allCellList
         *            存放的是一个一个单元格的相关属性
         * @return HashMap 存放查询到的数据
         * @throws
         */
	private HashMap getFieldValueMap() {
		HashMap valueMap = new HashMap();
		InputStream in = null;
		try {
			String staitic_="static";
            if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
            	staitic_="static_o";
            }
			ContentDAO dao = new ContentDAO(this.conn);
			String querysql = getQuerySql();
			String tableName = "templet_" + this.tabId;
			if ("0".equals(this.task_id)) {
                if (!"0".equals(this.selfApply)) {// 个人业务申请 查询数据 "g_templet_"
                     tableName = "g_templet_" + this.tabId;                           
                } else {
                     tableName = this.userView.getUserName() + "templet_" + this.tabId;
                }
            }
			RowSet rset = dao.search(querysql);
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
			if (rset.next()) {
				// 再向map中存储一些特殊的字段
				if (this.paramBo.getTable_vo().getInt(staitic_) == 10) {
					valueMap.put("b0110", rset.getString("b0110"));
					if (this.paramBo.getOperationType() == 8
							|| this.paramBo.getOperationType() == 9) {// 如果是合并和划转才会有
						// to_id这个字段否则会报列名无效
						valueMap.put("to_id", rset.getString("to_id"));
					}
				} else if (this.paramBo.getTable_vo().getInt(staitic_) == 11) {
					valueMap.put("e01a1", rset.getString("e01a1"));
					if (this.paramBo.getOperationType() == 8
							|| this.paramBo.getOperationType() == 9) {// 如果是合并和划转才会有
						// to_id这个字段否则会报列名无效
						valueMap.put("to_id", rset.getString("to_id"));
					}
				} else {
					valueMap.put("a0100", rset.getString("a0100"));
					valueMap.put("basepre", rset.getString("basepre"));
					//valueMap.put("basepre", rset.getString("basepre"));
				}
				if (!"0".equals(this.task_id)) {
				    valueMap.put("ins_id", rset.getString("ins_id"));
	            }

				ArrayList cellList = getAllCell();
				HashMap recParamMap=new HashMap();//用于存储每页上数值型指标
				for (int i = 0; i < cellList.size(); i++) {
					TemplateSet setBo = (TemplateSet) cellList.get(i);
					String flag = setBo.getFlag();
					int disformat=setBo.getDisformat(); //数据输出格式
					if (flag == null || "H".equals(flag) || "".equals(flag)) {// 汉字描述的数据不用从数据库中查询
						continue;
					}
					if ("F".equalsIgnoreCase(flag)
							//|| flag.equalsIgnoreCase("S")
							|| "C".equalsIgnoreCase(flag)
							|| "T".equalsIgnoreCase(flag)) {
						continue;
					} 
					
					if ("P".equalsIgnoreCase(flag)) {// 照片
						String ext = rset.getString("ext");
						if (ext == null || "".equalsIgnoreCase(ext)) {
							valueMap.put("ext", "nophoto");
							valueMap.put("photo", "nophoto");
						} else {
							/*String filename = ServletUtilities.createOleFile(
									"photo", "ext", rset);*/
							String filename=rset.getString("fileid");
							if (!(filename == null || "".equals(filename))) {// 安全平台改造，将filename加密
								filename = "/servlet/vfsservlet?fromjavafolder=true&fileid="
										+ filename;
							}else {
								in = rset.getBinaryStream("photo");
								if(in!=null) {
									//照片附件转存为vfs保存
									filename = VfsService.addFile(this.userView.getUserName(), VfsFiletypeEnum.multimedia, VfsModulesEnum.RS, VfsCategoryEnum.other, "", in, ServletUtilities.tempFilePrefix+ext, "", false);
									String a0100 = rset.getString("a0100");
									String basePre = rset.getString("basepre");
									String seqnum = rset.getString("seqnum");
									dao.update(" update "+tableName+"  set fileid='"+filename+"' where a0100='"+a0100+"' and basepre='"+basePre+"' and seqnum='"+seqnum+"'  ");
									filename = "/servlet/vfsservlet?fromjavafolder=true&fileid="
											+ filename;
								}else {
									filename = "blank";// 没有照片时
								}
							}
							valueMap.put("ext", ext);
							valueMap.put("photo", filename);
							valueMap.put("fileid",filename );
						}
						continue;
					}
					String fieldType = setBo.getField_type() == null ? "" : setBo
							.getField_type();
					String old_FieldType=setBo.getOld_fieldType()==null?"":setBo.getOld_fieldType();//bug27203 liuyz 最近第几条filedType会存为M,old_fieldType存放字段的原始类型，与数据库中相同。
					String queryColumnName = setBo.getTableFieldName();
					//兼容指标未构库的情况 临时变量或指标引入后 又删除了
					try{
					    rset.getObject(queryColumnName);
					}catch(Exception e){
					    e.printStackTrace();
					    continue;  
					}
					String value="";
					if ("D".equalsIgnoreCase(fieldType)) {
						if (rset.getDate(queryColumnName) != null) {
							if(disformat==25){
								value=dateFormat.format(rset.getTimestamp(queryColumnName)); 
							}
							else{
							//	Date date = rset.getDate(queryColumnName);
							//	value = date.toString();
								value=dateFormat2.format(rset.getTimestamp(queryColumnName)); 
							}
						}
					} else if ("N".equalsIgnoreCase(fieldType)) {
						value = rset.getString(queryColumnName);					
						//计算项要用，先存储。
                        TRecParamView recP = new TRecParamView();
                        recP.setBflag(true);
                        String fValue=value;
                    	if(StringUtils.isBlank(fValue)){
                        		fValue = "0";
                        }
                        recP.setFvalue(fValue);
                        recP.setNid(setBo.getGridno());
                        if(  recParamMap.containsKey(setBo.getPageId())){
                      	  ArrayList recParam=(ArrayList) recParamMap.get(setBo.getPageId());
                      	  recParam.add(recP);
                        }else{
                      	  ArrayList recParam=new ArrayList(); //用于计算公式 
                            recParam.add(recP);
                            recParamMap.put(setBo.getPageId(), recParam);
                        }
					} else if ("M".equalsIgnoreCase(fieldType)) {
						value = Sql_switcher.readMemo(rset,queryColumnName);
						if("N".equalsIgnoreCase(old_FieldType))//bug27203 liuyz 最近第几条filedType会存为M,old_fieldType存放字段的原始类型，与数据库中相同。
						{
							//计算项要用，先存储。
	                        TRecParamView recP = new TRecParamView();
	                        recP.setBflag(true);
	                        String fValue=value;
	                    	if(StringUtils.isBlank(fValue)){
	                        		fValue = "0";
	                        }
	                        recP.setFvalue(fValue);
	                        recP.setNid(setBo.getGridno());
	                        if(  recParamMap.containsKey(setBo.getPageId())){//获取计算公式插入页上数值型指标
		                      	  ArrayList recParam=(ArrayList) recParamMap.get(setBo.getPageId());
		                      	  recParam.add(recP);
		                        }else{
		                      	  ArrayList recParam=new ArrayList(); //用于计算公式 
		                            recParam.add(recP);
		                            recParamMap.put(setBo.getPageId(), recParam);
		                        }
						}
					} else {
					    /*
						if (setBo.isSubflag()) {
							value = Sql_switcher.readMemo(rset, queryColumnName);
						} else {
							value = rset.getString(queryColumnName);
							if (value != null) {
								char s = '\000';
								String ss = String.valueOf(s);
								value = value.replaceAll(ss, " ");
								
							}
						}
						*/
						value = rset.getString(queryColumnName);
					}
					if (value == null) {
						value = "";
					}
					
					if ("D".equalsIgnoreCase(fieldType)) {
						if(valueMap.get(queryColumnName) == null)
							valueMap.put(queryColumnName, value);
						else{
							String tempValue = (String)valueMap.get(queryColumnName);
							if(value.length() > tempValue.length())
								valueMap.put(queryColumnName, value);
						}
					}else{
						valueMap.put(queryColumnName, value);
					}
				}
				//处理计算项
				for (int i = 0; i < cellList.size(); i++) {
                    TemplateSet setBo = (TemplateSet) cellList.get(i);
                    String flag = setBo.getFlag();
                    if ("C".equals(flag)) {
                        String pattern = "###"; 
                        TSyntax tsyntax = new TSyntax();    
                        tsyntax.Lexical(setBo.getFormula());
                        if(  recParamMap.containsKey(setBo.getPageId())){
                        	ArrayList recParam=(ArrayList) recParamMap.get(setBo.getPageId());
                        	tsyntax.SetVariableValue(recParam);
                        }else{
                        	ArrayList recParam=new ArrayList(); //用于计算公式 
                        	tsyntax.SetVariableValue(recParam);
                        }
                        tsyntax.DoWithProgram();
                        int decimal = setBo.getDisformat();
                        pattern = "###"; //浮点数的精度
                        if (decimal > 0)
                            pattern += ".";
                        for (int j = 0; j < decimal; j++)
                            pattern += "0";
                        double dValue =0;
                        if (tsyntax.m_strResult != null && tsyntax.m_strResult.length() > 0)
                          dValue =Double.parseDouble(tsyntax.m_strResult);
                        String cCalcValue = PubFunc.DoFormatDecimal(String.valueOf(dValue), decimal);
                        valueMap.put(setBo.getTableFieldName(), cCalcValue);
                    }
                    
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeIoResource(in);
		}
		this.fieldValueMap=valueMap;
		return valueMap;

	}

	/**   
     * @Title: isReadOnly   
     * @Description: 判断此模板页是只读还是可编辑，liuyz优化。   
     * @param @return 
     * @return boolean 
     * @throws   
    */
    public boolean isReadOnly(String pageId) {
        boolean isReadOnly = true;
        try {
        	if (!"1".equals(this.getApproveFlag())){//浏览打印 全部置为可读
		        	isReadOnly = true;//
		        	return isReadOnly;
		    }
        	ArrayList cellList =  utilBo.getPageCell(this.tabId,Integer.parseInt(pageId)); 
            HashMap privMap = getFieldPrivMap(cellList);
            for (int i = 0; i < cellList.size(); i++) {
                TemplateSet setBo = (TemplateSet) cellList.get(i);
                String flag = setBo.getFlag();
                if ("".equals(flag) || "H".equalsIgnoreCase(flag)||"C".equalsIgnoreCase(flag)) {
                	continue;
                }
                if ("P".equalsIgnoreCase(flag)) {
                	if (!"1".equals(this.getApproveFlag())){//非起草报审
                		isReadOnly = true;
		            }
					else {
						if(!"".equals(paramBo.getDest_base())||paramBo.getOperationType()==10){//bug 33766 有提交目标库或者其他变动可以上传照片
							isReadOnly = false;
							break;
						}
					}   					 
                }
            	if (privMap.get(setBo.getUniqueId()) != null) {
                	String rwPriv = (String) privMap.get(setBo.getUniqueId());
                	//String rwPriv = (String)privMap.get(setBo.getUniqueId());
                	String opinion_field = this.paramBo.getOpinion_field();//审批意见指标也是只读
                	if (setBo.isABKItem()&&"2".equals(rwPriv)&&2==setBo.getChgstate()&&!(setBo.isSubflag()==false&&opinion_field!=null&&opinion_field.equalsIgnoreCase(setBo.getField_name()))||
                			"V".equalsIgnoreCase(flag)&&"2".equals(rwPriv)||"S".equalsIgnoreCase(flag)||
                			"F".equalsIgnoreCase(flag)&&"2".equals(rwPriv)) {
                			isReadOnly = false;
                			break;
                	}   		            		    
            	}
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return isReadOnly;
     }
    /**   
     * @Title: isHaveReadFieldPriv   
     * @Description: 判断此模板页是否显示，
     * （1）有插入指标，且插入指标全部为无权限。   
     * @param @return 
     * @return boolean 
     * @throws   
    */
    public boolean isHaveReadFieldPriv(String pageId) {
        boolean bHavePriv = true;
        try {
        	ArrayList cellList =  utilBo.getPageCell(this.tabId,Integer.parseInt(pageId)); 
            HashMap privMap = getFieldPrivMap(cellList);
            for (int i = 0; i < cellList.size(); i++) {
                TemplateSet setBo = (TemplateSet) cellList.get(i);
                String flag = setBo.getFlag();
                if ("".equals(flag) || "H".equalsIgnoreCase(flag)) {
                    continue;
                }
                if (setBo.isABKItem()||"F".equalsIgnoreCase(flag)) {
                    bHavePriv = false;
                    if (privMap.get(setBo.getUniqueId()) != null) {
                        String rwPriv = (String) privMap.get(setBo.getUniqueId());
                        if ("1".equals(rwPriv) || "2".equals(rwPriv)) {
                            bHavePriv = true;
                            break;
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bHavePriv;
     }

    /**
     * 卡片状态下
     * 取得复杂查询下拉中的字段
     * linbz
     * @return
     */
    public ArrayList getFieldsArray(JSONArray tableHeadSetList, ArrayList fieldsMap) {
        ArrayList fieldsArray = new ArrayList();
        for (int i = 0; i < tableHeadSetList.size(); i++) {
            JSONObject jobject = (JSONObject) tableHeadSetList.get(i);
            HashMap map = new HashMap();
            String itemid = jobject.get("tableFieldName").toString();// 字段id
            String itemtype = jobject.get("fldType").toString();// 字段类型
            String codesetid = jobject.get("codeSetId").toString();// 关联的代码
            String columndesc = jobject.get("hz").toString();// 字段描述
            String chgState = jobject.get("chgState").toString();// =1变化前，=2变化后
            String ctrltype = jobject.get("ctrltype").toString();
            String setName = jobject.get("setName").toString();
            //29492 卡片下过滤主集的重复指标
            for (int j=tableHeadSetList.size()-1;j>i;j--){  
            	JSONObject jobjectJ = (JSONObject) tableHeadSetList.get(j);
            	String itemidJ = jobjectJ.get("tableFieldName").toString();// 字段id
            	String setNameJ = jobjectJ.get("setName").toString();
            	if("A01".equalsIgnoreCase(setName) && "A01".equalsIgnoreCase(setNameJ)
            			&& itemid.equalsIgnoreCase(itemidJ)){
            		tableHeadSetList.remove(j);
            	}
            }
            //29030 区分变化后指标名称加个[拟]
            if("2".equals(chgState))
            	columndesc = columndesc+"[拟]";
            
            map.put("type", itemtype);
            map.put("itemid", itemid.toUpperCase());
            map.put("itemdesc", columndesc);
            map.put("codesetid", codesetid);
            if("UN".equalsIgnoreCase(codesetid)|| "UM".equalsIgnoreCase(codesetid)|| "@K".equalsIgnoreCase(codesetid)){
            	map.put("codesource", "");
            	//29031管理范围权限0全部，3按模板设置走
        		map.put("ctrltype", ctrltype);
            	map.put("nmodule", "8");
            	map.put("parentid", "root");
            }
            //29018 linbz 节点权限 查询值可以选择非叶子节点
            map.put("codesetValid", false);
            //liuyz 31270 下拉菜单选择日期型指标查询，出现了时分秒，需修订
            if("D".equalsIgnoreCase(itemtype))
            {
            	String format="Y-m-d H:i:s";
            	int itemlen=Integer.parseInt(jobject.get("format").toString());
            	switch (itemlen) {
                case 8:// 1991.2
                case 9:// 1992.02
                case 10:// 92.2
                case 11:// 98.02
                case 13:// 一九九一年一月
                case 15:// 1991年1月
                case 17:// 91年1月
                case 22:// 1999年02月
                	format="Y-m";
                    break;
                case 6: // 1991.12.3 按 1991.12.03处理 都统一改了
                case 7: // 91.12.3
                case 12:// 一九九一年一月二日
                case 14:// 1991年1月2日
                case 16:// 91年1月2日
                case 19:// 1991（年）
                case 20:// 1 （月）
                case 21:// 23 （日）
                case 23:// 1999年02月03日
                case 24:// 1992.02.01
                	format="Y-m-d";
                    break;
                case 25:// 1992.02.01 10:30
                	format="Y-m-d H:i";
                    break;
                default:
                	format="Y-m-d H:i:s";
                    break;
                }
            	map.put("format", format);
            }
            else
            {
            	map.put("format", "Y-m-d H:i:s");
            }
            fieldsArray.add(map);

            String newItemid = itemid.toUpperCase();
            LazyDynaBean item = new LazyDynaBean();
            item.set("codesetid", codesetid);
            item.set("useflag", "1");
            item.set("itemtype", itemtype);
            item.set("itemid", newItemid);
            item.set("itemdesc", columndesc);
            fieldsMap.add(item);
        }
        return fieldsArray;
    }

	public int getTabid() {
		return tabId;
	}


	public void setTabid(int tabid) {
		this.tabId = tabid;
	}

	public String getTask_id() {
		return task_id;
	}

	public void setTask_id(String taskId) {
		task_id = taskId;
	}

	public String getSelfApply() {
		return selfApply;
	}

	public void setSelfApply(String selfApply) {
		this.selfApply = selfApply;
	}

    public String getObjectId() {
        return objectId;
    }


    public void setObjectId(String objectId) {
        this.objectId = objectId;        
        if (this.paramBo.getInfor_type()==1){
            int i = this.objectId.indexOf("`");
            if (i>0){
                String basepre=this.objectId.substring(0,i);
                String a0100=this.objectId.substring(i+1);
                this.a0100 = a0100;
                this.basePre = basepre;
            }
        }
        else if (this.paramBo.getInfor_type()==2){
            this.b0110=this.objectId;
        }
        else if (this.paramBo.getInfor_type()==3){
            this.e01a1=this.objectId;
        }  
        
    }

    public String getApproveFlag() {
        return approveFlag;
    }

    public void setApproveFlag(String approveFlag) {
        this.approveFlag = approveFlag;
    }

    public String getCurTaskId() {
        return curTaskId;
    }

    public void setCurTaskId(String curTaskId) {
        this.curTaskId = curTaskId;
    }
    
    public HashMap getChangeInfoLMap() {
		return changeInfoLMap;
	}

	public void setChangeInfoLMap(HashMap changeInfoLMap) {
		this.changeInfoLMap = changeInfoLMap;
	}
}
