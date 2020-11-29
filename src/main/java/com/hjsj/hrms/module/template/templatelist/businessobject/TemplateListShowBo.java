package com.hjsj.hrms.module.template.templatelist.businessobject;

import com.hjsj.hrms.module.template.utils.TemplateDataBo;
import com.hjsj.hrms.module.template.utils.TemplateLayoutBo;
import com.hjsj.hrms.module.template.utils.TemplateUtilBo;
import com.hjsj.hrms.module.template.utils.javabean.TemplateItem;
import com.hjsj.hrms.module.template.utils.javabean.TemplatePage;
import com.hjsj.hrms.module.template.utils.javabean.TemplateParam;
import com.hjsj.hrms.module.template.utils.javabean.TemplateSet;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
/**
 * <p>Title:TemplateListShowBo.java</p>
 * <p>Description>:显示列表使用的类</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2016-5-10 下午03:08:51</p>
 * <p>@version: 7.x</p>
 */
public class TemplateListShowBo {
 
    private Connection conn = null;
    private UserView userView = null;
    private TemplateParam paramBo = null;
    private TemplateDataBo dataBo=null;
    private int tabId;
    private ContentDAO dao; 	
	private HashMap sub_Map = new HashMap();
	
    /** 区分报审、报备、加签 
     * 1：报审 2：加签  3 报备 
    */
    private String approveFlag="0";  
    /** 不显示的页签*/
    private String noshow_pageno = "";
    public TemplateListShowBo (){ 
       
    }
    //liuyz 我的申请不需要去获取节点权限
    public TemplateListShowBo (Connection conn,UserView userview,int tabid,TemplateParam paramBo){ 
        this.tabId=tabid;                            
        this.conn = conn;
        this.userView = userview;
        dao=new ContentDAO(this.conn);
        this.paramBo = paramBo;
        dataBo = new TemplateDataBo(this.conn,this.userView,this.paramBo);
       
    }
    public TemplateListShowBo (Connection conn,UserView userview,int tabid){ 
        this.tabId=tabid;                            
        this.conn = conn;
        this.userView = userview;
        dao=new ContentDAO(this.conn);
        this.paramBo = new TemplateParam(conn, userview, tabid);
        dataBo = new TemplateDataBo(this.conn,this.userView,this.paramBo);
       
    }
    
	
	/**   
	 * @Title: getTableHeadSetList   
	 * @Description: 获取表头   
	 * @param @param cellList 单元格
	 * @param @param privMap 指标权限
	 * @param @return 
	 * @return ArrayList 
	 * @throws   
	*/
	public ArrayList getTableHeadSetList(String taskId)
	{
		ArrayList list = new ArrayList();
		HashMap relationFieldMap=new HashMap();
        try {
            ArrayList allCellList =  dataBo.getUtilBo().getAllCell(tabId);
            HashMap privMap = dataBo.getFieldPrivMap(allCellList, taskId);
            String  strflds=",";
            //bug 32394节点设置指标必填，业务表单以列表方式展现看不到红星标识
            HashMap privMapNode=new HashMap();
            if(!"3".equals(this.paramBo.getReturnFlag())&&this.paramBo.getSp_mode()==0){
            	TemplateUtilBo utilBo=new TemplateUtilBo(this.conn, this.userView);
            	privMapNode = utilBo.getFieldPrivFillable(taskId.split(",")[0],tabId);
            }
            ArrayList<TemplatePage> pageList= dataBo.getUtilBo().getAllTemplatePage(tabId);
          //获得可以打印的页数集合 lis 20160607 add
            ArrayList<Integer> outPriPageList = new ArrayList<Integer>();
            for(TemplatePage pagebo: pageList)
 			{
            	if(!"".equals(noshow_pageno)){//如果有设置的不显示页签 优先走这个
					String pageid =  String.valueOf(pagebo.getPageId());
					String[] pagearr = noshow_pageno.split(",");
					boolean noprint = false;
					for(String pid:pagearr){
						if(pid.equalsIgnoreCase(pageid)){
							noprint = true;
							break;
						}
					}
					if(noprint)
						continue;
            	}else if(!pagebo.isShow()) {
					continue;
				}
            	if(pagebo.isMobile())
            		continue;
				outPriPageList.add(pagebo.getPageId());
 			}
            dataBo.getParamBo().setOutPriPageList(outPriPageList);
            ArrayList allTemplateItemList = dataBo.getAllTemplateItem(false);
            for (int i = 0; i < allTemplateItemList.size(); i++) {
                TemplateItem templateItem = (TemplateItem)allTemplateItemList.get(i);
                TemplateSet setBo = templateItem.getCellBo();
                FieldItem fldItem=templateItem.getFieldItem();
                if (//"F".equals(setBo.getFlag()) || 
                		"P".equals(setBo.getFlag())
                        ||"S".equals(setBo.getFlag())
                        ||"".equals(setBo.getFlag())){
                    continue;
                }
                strflds=strflds+setBo.getField_name()+",";
                String chgstate = setBo.getChgstate() + ""; // 1:变化前 2：变化后
                String subFlag = setBo.isSubflag() ? "1" : "0";
                int pageId = setBo.getPageId();
                String mode = setBo.getMode() + "";
                String setname = setBo.getSetname();
                String isVar = "0";
                if(!outPriPageList.contains(pageId))//当前页可打印，页面上的指标才会显示
                	continue;
                if (setBo.isVarItem())
                    isVar = "1";
                String state = "0";//可写可读标记
                if (setBo.isSpecialItem()) {
                    state = "2";
                } 
                String opinion_field = paramBo.getOpinion_field();
                if (privMap.get(setBo.getUniqueId()) != null) {
                    String rwPriv = (String)privMap.get(setBo.getUniqueId());
                    if (StringUtils.isNotBlank(opinion_field) && opinion_field.equals(setBo.getField_name())){//审批意见指标
                        if (!"0".equals(rwPriv)){
                            rwPriv="1";
                        }
                    }
                    if (!"1".equals(this.getApproveFlag()) || setBo.getChgstate()==1){//非起草报审 或者变化前
                        if (!"0".equals(rwPriv)){
                            rwPriv="1";//全部置为可读
                        }
                    }
                    state= rwPriv;
                }
                
                String desc = setBo.getHz();
                if ("2".equals(chgstate) && desc.indexOf("拟")==-1 
                		&& !"V".equalsIgnoreCase(setBo.getFlag()) && !"F".equalsIgnoreCase(setBo.getFlag()) 
                		&& !"S".equalsIgnoreCase(setBo.getFlag())) {
                	if (StringUtils.isNotBlank(opinion_field) && opinion_field.equals(setBo.getField_name())){//审批意见指标
                	}else	
                		desc = desc + "[拟]";
                }
                if ("1".equals(subFlag)) {
                    desc = desc.replaceAll("\\{", "").replaceAll("\\}", "");
                    desc = desc.replaceAll("\\{", "").replaceAll("\\}", "");
                }
                desc = desc.replaceAll("`", "");
                if ("".equals(desc)){
                    desc= fldItem.getItemdesc();
                }
                if("e01a1".equalsIgnoreCase(setBo.getField_name()) && ("职位名称".equals(desc))){//bs默认为岗位名称
                    desc="岗位名称";
                }
                
                LazyDynaBean headBean = new LazyDynaBean();
                String fildType = setBo.getField_type();
                String fieldName = setBo.getField_name();
                headBean.set("isLock", "false");//是否默认锁定此列
                if("F".equalsIgnoreCase(setBo.getFlag())){//原来列表不支持显示两个附件，现在加上"_k_"+setBo.getUniqueId()用于区分显示多个附件字段
                	headBean.set("item_id", setBo.getTableFieldName()+"_k_"+setBo.getUniqueId());//数据库表中的字段名称
                }else{
                	headBean.set("item_id", setBo.getTableFieldName());//数据库表中的字段名称
                }
                if(setBo.isSubflag()) {
                	FieldSet fieldSet=DataDictionary.getFieldSetVo(setname);
                	if(fieldSet!=null&&StringUtils.isNotEmpty(fieldSet.getExplain())) {
                		headBean.set("hintText", fieldSet.getExplain());
                	}else {
                		headBean.set("hintText", desc);
                	}
                }else {
                	FieldItem item=DataDictionary.getFieldItem(fieldName);
                    if(item!=null&&StringUtils.isNotEmpty(item.getExplain())) {
                    	headBean.set("hintText", item.getExplain());
                    }else {
                    	headBean.set("hintText", desc);
                    }
                }
                headBean.set("item_desc", desc);
                headBean.set("item_type", setBo.getOld_fieldType());
                headBean.set("setname", setBo.getSetname());
                headBean.set("field_name", fieldName);
                headBean.set("field_type", fildType);
                headBean.set("field_hz", setBo.getField_hz());
                headBean.set("codeid", setBo.getCodeid());
                headBean.set("chgstate", setBo.getChgstate() + "");
                headBean.set("subflag", subFlag);
                headBean.set("isvar", isVar);
                headBean.set("state", state);
                headBean.set("flag", setBo.getFlag());
                headBean.set("pageid", setBo.getPageId());
                headBean.set("gridno", setBo.getGridno());
                //bug 32394节点设置指标必填，业务表单以列表方式展现看不到红星标识
                Boolean isYneed=setBo.isYneed();
                if (!privMapNode.isEmpty()&&StringUtils.isNotBlank(fieldName)&&privMapNode.get(fieldName.toLowerCase()) != null) {
                    String rwPriv = (String)privMapNode.get(fieldName.toLowerCase());
                    if("3".equalsIgnoreCase(rwPriv))
                    	isYneed=true;
                }
                headBean.set("yneed",isYneed);//liuyz bug 26528 列表下必填项表头加红色星号提醒。 此参数确认是必填项。
                if (setBo.isBLimitManagePriv())                
                    headBean.set("limitManagePriv","1" );
                else 
                    headBean.set("limitManagePriv","0" ); 
                
                
                String itemLength="100";
                if(setBo.isABKItem()){                   
                    if(setBo.isSpecialItem()){
                        itemLength="50";
                    }
                    else{
                        if(fldItem!=null)
                          itemLength=fldItem.getItemlength()+"";
                    }
                }   
                headBean.set("itemlength", itemLength); 
                headBean.set("formula", setBo.getFormula());
                headBean.set("sub_domain", setBo.getXml_param());
                //linbz 27502 大文本状态下HTML编辑标识
                if(("M").equals(fildType) && fieldName.trim().length()>0)
        		{
        			FieldItem fielditem = DataDictionary.getFieldItem(setBo.getField_name());
        			if(fielditem!=null) {
        				headBean.set("inputType", fielditem.getInputtype());
        				headBean.set("limitlength", fielditem.getItemlength());//大文本限制长度
        			}else {
        				headBean.set("limitlength", 0);//大文本限制长度
        				headBean.set("inputType", 0);
        			}
        			
        		}
                headBean.set("disformat", setBo.getDisformat()+"");
                headBean.set("defaultValue", setBo.getDefaultValue()+"");
                headBean.set("imppeople", setBo.getImppeople());
                if(StringUtils.isNotBlank(setBo.getRelation_field())&&2==setBo.getChgstate()){
					relationFieldMap.put(headBean.get("item_id").toString(), setBo.getRelation_field());
				}
                list.add(headBean);
            }
            if(relationFieldMap.size()>0){
				Iterator iterator = relationFieldMap.entrySet().iterator();
				while(iterator.hasNext()){
					Entry entry=(Entry)	iterator.next();
					String relationField = (String)entry.getValue();
					String uniqueId=(String)entry.getKey();
					String fatherRelationField="";
					LazyDynaBean childFieldBean=null;
					for(int i=0;i<list.size();i++){
						LazyDynaBean fieldBean=(LazyDynaBean) list.get(i);
						String fieldUniqueId = (String) fieldBean.get("item_id");
						String fieldfldName = fieldBean.get("pageid")+"_"+fieldBean.get("gridno");
						String childRelationField = (String) fieldBean.get("childRelationField");
						if((!uniqueId.equalsIgnoreCase(fieldUniqueId))&&relationField.equalsIgnoreCase(fieldfldName)){
							if(StringUtils.isBlank(childRelationField)){
								childRelationField=uniqueId+",";
							}else{
								childRelationField+=uniqueId+",";
							}
							fatherRelationField=fieldUniqueId;
							fieldBean.set("childRelationField", childRelationField);
						}else if(uniqueId.equalsIgnoreCase(fieldUniqueId)){
							childFieldBean=fieldBean;
							
						}
					}
					if(childFieldBean!=null){
						childFieldBean.set("fatherRelationField", fatherRelationField);
					}
				}
			}
            
            //排序 单位名称、部门名称、岗位名称、姓名 排在前4位
            strflds=strflds.toLowerCase();
            if (strflds.contains(",a0101,")){
                list =sortHeadSetList(list,"a0101");
            }
            if (strflds.contains(",e01a1,")){
                list =sortHeadSetList(list,"e01a1");
            }
            if (strflds.contains(",e0122,")){
                list =sortHeadSetList(list,"e0122");
            }
            if (strflds.contains(",b0110,")){
                list =sortHeadSetList(list,"b0110");
            }
            

        } catch (Exception e) {
            e.printStackTrace();
        }
		
		return list;
	}
	
	
    /**   
     * @Title: sortHeadSetList   
     * @Description:  排序  
     * @param @param tempList
     * @param @param fld
     * @param @return 
     * @return ArrayList 
     * @throws   
    */
    public ArrayList sortHeadSetList(ArrayList tempList,String fld){
        ArrayList list = new ArrayList();
        try {
            for (int i = 0; i < tempList.size(); i++) {
                LazyDynaBean headBean = (LazyDynaBean)tempList.get(i);
                String itemid =(String)headBean.get("field_name");
                if (fld.equalsIgnoreCase(itemid)){
                    headBean.set("isLock", "true");
                    list.add(0,headBean);
                }
                else {
                    list.add(headBean);
                }
            }   
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        return list;
    }
    /**
     * 获取列头、表格渲染
     * 
     * @param tableHeadSetList
     * @param lastlist
     * @return
     * @throws GeneralException 
     */
    public ArrayList<ColumnsInfo> getColumnList(ArrayList tableHeadSetList) throws GeneralException {
        ArrayList<ColumnsInfo> columnTmp = new ArrayList<ColumnsInfo>();
        // 组号
        if (this.paramBo.getInfor_type() == 2 || this.paramBo.getInfor_type() == 3) {// 单位名称
            if (this.paramBo.getOperationType() == 8 || this.paramBo.getOperationType() == 9) {
                ColumnsInfo cname = TemplateLayoutBo.getColumnsInfo("to_id", "组号", "A", 50);
                cname.setRendererFunc("templateList_me.renderGroupColumn");
                cname.setEditableValidFunc("false");
                columnTmp.add(cname);
            }
        }
        
        for (int i = 0; i < tableHeadSetList.size(); i++) {
            LazyDynaBean headBean = (LazyDynaBean) tableHeadSetList.get(i);
            String columnId = headBean.get("item_id").toString();            
            String columnDesc = headBean.get("item_desc").toString();
            String fieldType = headBean.get("item_type").toString();
            String chgstate = headBean.get("chgstate").toString();
            String subflag = headBean.get("subflag").toString();
            String state = headBean.get("state").toString();
            String isLock = headBean.get("isLock").toString();
            String newFieldType = headBean.get("field_type").toString();
            String field_name = headBean.get("field_name").toString();  //用于组织机构判断特殊字段 lis 20160825
            String flag = headBean.get("flag").toString();
            String disformat = headBean.get("disformat").toString();
            String imppeople = headBean.get("imppeople").toString();
            String defaultValue = headBean.get("defaultValue").toString();
            //liuyz bug 26528 列表下必填项表头加红色星号提醒。
            Boolean yneed=Boolean.valueOf(headBean.get("yneed").toString());

            if ("0".equals(state)){//无权限 不显示
            	continue;
                //cname.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);// 不显示 
            }
            ColumnsInfo cname = getColumnsInfo(columnId, columnDesc, 100);// 由160改为80
            //liuyz bug 26528 列表下必填项表头加红色星号提醒。
            if(yneed!=null&&yneed)
            {
            	cname.setAllowBlank(false);
            	/**
            	 * xiegh 
            	 * 缺陷26659 
            	 * 去除+"&nbsp;<font color='red' face='宋体' style='FONT-SIZE:8pt'>*</font>"
            	 * ColumnsInfo对象中add  descsuffix
            	 */
            	cname.setDescSuffix("&nbsp;<font color='red' face='宋体' style='FONT-SIZE:8pt'>*</font>");
            	cname.setColumnDesc(columnDesc); 
/*            	cname.setHintText(columnDesc);//liuyz bug31757
*/            }
            if(headBean.get("hintText")!=null&&!"".equals(headBean.get("hintText").toString())) {
            	cname.setHintText(headBean.get("hintText").toString());
            }
            
            if ("1".equals(subflag)) {//子集
                String sub_domain = headBean.get("sub_domain").toString();
                sub_Map.put(columnId, sub_domain);
                cname.setColumnWidth(80);
                cname.setTextAlign("center");
                cname.setEditableValidFunc("false");
                cname.setFilterable(false);
                //cname.setLoadtype(ColumnsInfo.LOADTYPE_NOTLOAD);
                cname.setRendererFunc("templateList_me.showSubsetRender");
                cname.setColumnType("M");
                
                //当前子集是否有权限编辑
                ColumnsInfo cnameT = getColumnsInfo("sub_" + columnId, columnDesc, 100);
                cnameT.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);// 不显示 
                columnTmp.add(cnameT);
                //当前子集是变化前还是变化后
                ColumnsInfo cnameC = getColumnsInfo("chg_" + columnId, columnDesc, 100);
                cnameC.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);// 不显示 
                columnTmp.add(cnameC);
            } 
            else if("F".equals(flag)){//个人、公共附件
            	cname.setColumnWidth(80);
                cname.setTextAlign("center");
                cname.setEditableValidFunc("false");
                cname.setFilterable(false);
                cname.setRendererFunc("templateList_me.showAttachmentRender");
                cname.setColumnType("M");
                
                //当前附件是否有权限编辑
                ColumnsInfo cnameT = getColumnsInfo("att_" + columnId, columnDesc, 100);
                cnameT.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);// 不显示 
                columnTmp.add(cnameT);
        	}
            else {
            	if("A".equalsIgnoreCase(fieldType)){
            		if(headBean.get("codeid")!=null&&("0".equalsIgnoreCase(headBean.get("codeid").toString())||("".equalsIgnoreCase(headBean.get("codeid").toString())))){
	            		if(StringUtils.isNotBlank(imppeople)&&"true".equalsIgnoreCase(imppeople)){
	            			cname.setImppeople(imppeople);
	            		}
            		}else{
            			String fatherRelationField= (String) headBean.get("fatherRelationField");
            			String childRelationField= (String)headBean.get("childRelationField");
            			if(StringUtils.isNotBlank(fatherRelationField)){
	            			cname.setFatherRelationField(fatherRelationField);
	            		}
            			if(StringUtils.isNotBlank(childRelationField)){
            				cname.setChildRelationField(childRelationField);
            			}
            		}
            		if("2".equals(chgstate)) {
            			cname.setRendererFunc("templateList_me.showRender");
            		}
            	}
            	if("D".equals(fieldType)&&"2".equals(chgstate)){
            		cname.setRendererFunc("templateList_me.showRender");
            	}
                if ("D".equals(fieldType) || "N".equals(fieldType))// 日期型和数字型字段居右,其他字段默认居左
                    cname.setTextAlign("right");
                if ("N".equals(fieldType)&& !"M".equals(newFieldType)){// 数值类型小数点位数，lis 20160722
                	//syl 走模板设计中的指标长度。53010 V771封版：人事异动 数值型指标列表方式下走的是指标体系中的小数位长度，卡片方式下走的模板设计中的小数长度，不统一
            		cname.setDecimalWidth(Integer.valueOf((String)headBean.get("disformat")));
                	//北理工优化 有小数位显示，无小数位不显示.00
                	cname.setRendererFunc("templateList_me.showNumberRender");
                }
                cname.setColumnType(fieldType);
                if (headBean.get("itemlength") != null) {
                	if("D".equals(fieldType))//lis 20160720
                		cname.setColumnLength(getFormat(Integer.valueOf((String)headBean.get("disformat"))));
                	else
                		cname.setColumnLength(Integer.parseInt(headBean.get("itemlength") + ""));// 显示长度
                } else {
                    cname.setColumnLength(100);
                }
                if ("parentid".equalsIgnoreCase(field_name) || "codesetid".equalsIgnoreCase(field_name)) {// 是单位或部门模板中的特殊字段
                    cname.setCodesetId("0");
                    cname.setColumnWidth(120);
                    if ("codesetid".equalsIgnoreCase(field_name)) {
                        ArrayList<CommonData> list = new ArrayList<CommonData>();
                        list.add(new CommonData("UN", ResourceFactory.getProperty("tree.unroot.undesc")));
                        list.add(new CommonData("UM", ResourceFactory.getProperty("tree.umroot.umdesc")));
                        cname.setOperationData(list);
                        cname.setRendererFunc("templateList_me.codesetidRender");
                    }
                    else {
                    	 cname.setCodesetId("UM");
                    	 if ("1".equals(headBean.get("limitManagePriv"))){//50639
                             cname.setCtrltype("3");//业务范围
                         }else {
                        	 cname.setCtrltype("0");//不控制
                         }
                         cname.setNmodule("8");
                    }
                } else {
                    if (headBean.get("codeid") != null) {
                        if ("UN".equalsIgnoreCase(headBean.get("codeid").toString()) 
                                || "UM".equalsIgnoreCase(headBean.get("codeid").toString())
                                || "@K".equalsIgnoreCase(headBean.get("codeid").toString())){
                            if ("1".equals(headBean.get("limitManagePriv"))){
                                cname.setCtrltype("3");//业务范围
                            }else
                            	cname.setCtrltype("0");//不控制
                            cname.setNmodule("8");
                        }
                        
                        if("1".equals(chgstate) && "M".equals(newFieldType)) {//变化前 取多条记录 特殊处理  代码型
                            //if(!"0".equals(headBean.get("codeid")))  
                            cname.setValueTranslator("com.hjsj.hrms.module.template.templatelist.businessobject.TemplateListShowBo:transMutiCodeSet") ;
                            cname.setColumnType("M");
                        }
                        else {
                            cname.setCodesetId(headBean.get("codeid").toString()); 
                            cname.setCodeSetValid(false);
                            if("@K".equalsIgnoreCase(headBean.get("codeid").toString()))
                            	cname.setCodeSetValid(true);
                        }
                    } else {
                        cname.setCodesetId("0");
                    }
                }
                //linbz 27502 大文本并且是HTML编辑器
                if("M".equals(fieldType)){
                	String inputType = headBean.get("inputType").toString();
                	if("1".equals(inputType)){
                		if(StringUtils.isBlank(imppeople)||"false".equalsIgnoreCase(imppeople)){
	                		cname.setColumnWidth(80);
	                		cname.setTextAlign("center");
	                		cname.setEditableValidFunc("false");
	                		cname.setFilterable(false);
	                		cname.setRendererFunc("templateList_me.showHtmlEditRender");
	                		cname.setColumnType("M");
	                		cname.setInputType(1);//HTML编辑器标识
	                		
	                		//当前附件是否有权限编辑
	                		ColumnsInfo cnameT = getColumnsInfo("htm_" + columnId, columnDesc, 100);
	                		cnameT.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);// 不显示 
	                		columnTmp.add(cnameT);
                		}
                	}
                	if(StringUtils.isNotBlank(imppeople)&&"true".equalsIgnoreCase(imppeople)){
            			cname.setImppeople(imppeople);
            		}
                	String limitlength = headBean.get("limitlength").toString();
                	if(StringUtils.isNotBlank(limitlength)) {
                		cname.setLimitlength(Integer.parseInt(limitlength));
                	}
                }
                if ("1".equals(chgstate)) {
                    cname.setEditableValidFunc("false");
                } else if ("1".equals(state)) {
                    cname.setEditableValidFunc("false");
                }

                if ("true".equals(isLock)) {
                    cname.setLocked(true);
                }
                
            }
            columnTmp.add(cname);
        }

        /** 隐藏 */
        // 人员编号
        ColumnsInfo objectId = TemplateLayoutBo.getColumnsInfo("objectid", "人员编号", "A", 100);
        objectId.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);// 不显示
        objectId.setEncrypted(true);
        columnTmp.add(objectId);
        // 编号
        ColumnsInfo A0100_safe = getColumnsInfo("a0100", "人员编号", 0);
        A0100_safe.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);// 不显示
        columnTmp.add(A0100_safe);
        // 应用库前缀
        ColumnsInfo basepre_safe = getColumnsInfo("basepre", "应用库前缀", 0);
        basepre_safe.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);// 不显示
        columnTmp.add(basepre_safe);

        // 人员序列号
        ColumnsInfo seqnum_safe = getColumnsInfo("seqnum", "人员序列号", 0);
        seqnum_safe.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);// 不显示
        columnTmp.add(seqnum_safe);

        // 提交选择标志
        ColumnsInfo submitflag_safe = getColumnsInfo("submitflag2", "提交选择标志", 0);
        submitflag_safe.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);// 不显示
        columnTmp.add(submitflag_safe);

        // 任务编号
        ColumnsInfo task_id_safe = getColumnsInfo("realtask_id", "任务编号", 0);
        task_id_safe.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);// 不显示
        task_id_safe.setEncrypted(true);
        columnTmp.add(task_id_safe);

        // 流程编号
        ColumnsInfo ins_id_safe = getColumnsInfo("ins_id", "流程编号", 0);
        ins_id_safe.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);// 不显示
        columnTmp.add(ins_id_safe);

        ColumnsInfo state = getColumnsInfo("state", "来自通知单", 0);
        state.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);// 不显示
        columnTmp.add(state);

        return columnTmp;
    }

    /**
     * @author lis
     * @Description: 通过模板设置的日期格式获得指标对应的日期格式
     * @date Jul 20, 2016
     * @param templateSetFormat
     * @return
     */
    private int getFormat(int templateSetFormat){
    	int format = 0;
    	try {
			switch (templateSetFormat) {
			case 6:
				format = 10;
				break;
			case 7:
				format = 10;
				break;
			case 8:
				format = 7;
				break;
			case 9:
				format = 7;
				break;
			case 10:
				format = 7;
				break;
			case 11:
				format = 7;
				break;
			case 12:
				format = 10;
				break;
			case 13:
				format = 7;
				break;
			case 14:
				format = 10;
				break;
			case 15:
				format = 7;
				break;
			case 16:
				format = 10;
				break;
			case 17:
				format = 7;
				break;
			case 19:
				format = 4;
				break;
			case 22:
				format = 7;
				break;
			case 23:
				format = 10;
				break;
			case 24:
				format = 10;
				break;
			case 25:
				format = 16;
				break;
			default:
				format = 10;
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return format;
    }
    /**
     * 列头ColumnsInfo对象初始化
     * 
     * @param columnId
     *            id
     * @param columnDesc
     *            名称
     * @param columnDesc
     *            显示列宽
     * @return
     */
    private ColumnsInfo getColumnsInfo(String columnId, String columnDesc, int columnWidth) {

        ColumnsInfo columnsInfo = new ColumnsInfo();
        columnsInfo.setColumnId(columnId); // 设置列对应id
        columnsInfo.setColumnDesc(columnDesc); // 列头描述
        // columnsInfo.setCodesetId("");// 指标集
        // columnsInfo.setColumnType("M");// 类型N|M|A|D
        columnsInfo.setColumnWidth(columnWidth);// 显示列宽
        columnsInfo.setColumnLength(100);// 显示长度
        columnsInfo.setDecimalWidth(0);// 小数位
        columnsInfo.setAllowBlank(true);// 编辑时是否可以为空
        columnsInfo.setReadOnly(true);// 是否只读
        columnsInfo.setFromDict(false);// 是否从数据字典里来
        columnsInfo.setLocked(false);// 是否锁列
        columnsInfo.setSortable(true);//是否排序
        return columnsInfo;
    }
    
    /**
     * 
     * 取得复杂查询下拉中的字段
     * 
     * @return
     */
    public ArrayList getFieldsArray(JSONArray tableHeadSetList, ArrayList fieldsMap) {
        ArrayList fieldsArray = new ArrayList();
        for (int i = 0; i < tableHeadSetList.size(); i++) {
            JSONObject jobject = (JSONObject) tableHeadSetList.get(i);
            HashMap map = new HashMap();
            String itemid = jobject.get("columnId").toString();// 字段id
            String itemtype = jobject.get("columnType").toString();// 字段类型
            String codesetid = jobject.get("codesetId").toString();// 关联的代码
            String columndesc = jobject.get("columnDesc").toString();// 字段描述
            String loadtype = jobject.get("loadtype").toString();
            String ctrltype = jobject.get("ctrltype").toString();//管理范围权限
            //29433 linbz 加一层校验吧附件、大文本也从下拉列过滤
            if (!"1".equals(loadtype) || "M".equalsIgnoreCase(itemtype)){//0隐藏 1显示 || 附件、大文本
            	continue;
            }
            //28588 linbz 当有合并列头时会出现多个子列头
            JSONArray childColumnsList = (JSONArray)(JSONObject.fromObject(jobject).get("childColumns"));
            if(childColumnsList.size() > 0){
            	getChildColumnsFieldsArray(childColumnsList, fieldsArray, fieldsMap);
            	continue;
            }
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
            	int itemlen=(Integer) jobject.get("columnLength");
            	if(itemlen==4)
		          	format = "Y";
	            else if(itemlen==7)
	          	    format = "Y-m";
	            else if(itemlen==10)
	            	format = "Y-m-d";
	            else if(itemlen==16)
	          	    format = "Y-m-d H:i";
	            else if(itemlen==18)
	            	format = "Y-m-d H:i:s";
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
            // fieldsMap.put(newItemid,item);
            fieldsMap.add(item);
        }
        return fieldsArray;
    }
    
    /**
     * 获取表格合并后的列头的子列头
     * @param tableHeadSetList
     * @param fieldsArray
     * @param fieldsMap
     */
    public void getChildColumnsFieldsArray(JSONArray tableHeadSetList, ArrayList fieldsArray, ArrayList fieldsMap) {
        for (int i = 0; i < tableHeadSetList.size(); i++) {
            JSONObject jobject = (JSONObject) tableHeadSetList.get(i);
            HashMap map = new HashMap();
            String itemid = jobject.get("columnId").toString();// 字段id
            String itemtype = jobject.get("columnType").toString();// 字段类型
            String codesetid = jobject.get("codesetId").toString();// 关联的代码
            String columndesc = jobject.get("columnDesc").toString();// 字段描述
            String loadtype = jobject.get("loadtype").toString();
            String ctrltype = jobject.get("ctrltype").toString();//管理范围权限
            
            if (!"1".equals(loadtype) || "M".equalsIgnoreCase(itemtype)){//0隐藏 1显示 || 附件、大文本
            	continue;
            }
            JSONArray childColumnsList = (JSONArray)(JSONObject.fromObject(jobject).get("childColumns"));
            if(childColumnsList.size() > 0){
            	//如果子列头下还有合并列则继续调用该方法
            	getChildColumnsFieldsArray(childColumnsList, fieldsArray, fieldsMap);
            	continue;
            }
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
            map.put("format", "Y-m-d H:i:s");
            fieldsArray.add(map);

            String newItemid = itemid.toUpperCase();
            LazyDynaBean item = new LazyDynaBean();
            item.set("codesetid", codesetid);
            item.set("useflag", "1");
            item.set("itemtype", itemtype);
            item.set("itemid", newItemid);
            item.set("itemdesc", columndesc);
            // fieldsMap.put(newItemid,item);
            fieldsMap.add(item);
        }
    }
    
    /**   
     * @Title: transMutiCodeSet   
     * @Description: 取多条变化前记录 代码型指标，需要自定义方法取，按照表格工具定义的此方法。   
     * @param @param conn
     * @param @param userView
     * @param @param bean
     * @param @param itemid
     * @param @return 
     * @return String 
     * @throws   
    */
    public String transMutiCodeSet(Connection conn, UserView userView, LazyDynaBean bean,String oldItemid) {
        String retValue="";
        int k= oldItemid.indexOf("_");
        String itemId= oldItemid.substring(0,k);
        FieldItem fieldItem=DataDictionary.getFieldItem(itemId);
        if (fieldItem!=null){
            String oldValue =(String)bean.get(oldItemid) ;
            String [] arrValue = oldValue.split("`");
            for (int i =0 ; i<arrValue.length;i++ ){
                String id = arrValue[i];
                if(!"0".equals(fieldItem.getCodesetid())) {
                	String value = AdminCode.getCodeName(fieldItem.getCodesetid(),id);
                    if ("".equals(retValue)){
                        retValue=value;  
                    }
                    else {
                        retValue=retValue+"\r\n"+value;
                    }
                }else {
                	if ("".equals(retValue)){
                        retValue=id;  
                    }
                    else {
                        retValue=retValue+"\r\n"+id;
                    }
                }
            }
        }
        return retValue;
    }

    /**
     * @author lis
     * @Description: 栏目设置id
     * @date Jul 23, 2016
     * @param subModuleId
     * @return
     * @throws GeneralException
     */
    public int getSchemeId(String subModuleId) throws GeneralException {
		RowSet rowSet = null;
		int scheme_id = -1;
		if(StringUtils.isBlank(subModuleId))
			return scheme_id;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			ArrayList list = new ArrayList();
			String sql = "select scheme_id from t_sys_table_scheme  where submoduleid=? and username=?";
			list.add(PubFunc.decrypt(SafeCode.decode(subModuleId)));
			list.add(this.userView.getUserName());
			rowSet = dao.search(sql, list);
			if (rowSet.next())
				scheme_id = rowSet.getInt("scheme_id");
			return scheme_id;
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(rowSet);
		}
	}
    
    /**
	 * @Title: getSchemeItems
	 * @Description: TODO(从栏目设置中查找薪资项目)
	 * @param scheme_id 栏目设置id
	 * @param display 是否显示
	 * @return ArrayList<String>
	 * @author lis
	 * @throws GeneralException
	 * @date 2015-7-22 下午01:26:20
	 */
	public ArrayList<String> getSchemeItems(int schemeId,String display)
			throws GeneralException {
		RowSet rowSet = null;
		try {
			ArrayList<String> filedList = new ArrayList<String>();// 封装excel表头数据
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sql = new StringBuffer("select itemid from t_sys_table_scheme_item  where ");
			ArrayList list = new ArrayList();
			if(StringUtils.isNotBlank(display)){
				sql.append(" is_display=? and ");
				list.add(display);
			}
			sql.append(" scheme_id =? order by displayorder");
			list.add(schemeId);
			// 从表t_sys_table_scheme_item中查询itemid
			rowSet = dao.search(sql.toString(), list);
			while (rowSet.next()) {
				LazyDynaBean bean = new LazyDynaBean();
				String itemid = (String) rowSet.getString("itemid");
				filedList.add(itemid);
			}
			return filedList;
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(rowSet);
		}
	}
	
	/**
	 * @author lis
	 * @Description: 获得可编辑指标map
	 * @date Jul 23, 2016
	 * @param templateSetList
	 * @return
	 * @throws GeneralException
	 */
	public HashMap getTempleteSetMap(ArrayList templateSetList) throws GeneralException{
		HashMap map = new HashMap<String, LazyDynaBean>();
		try {
			for (int i = 0; i < templateSetList.size(); i++) {
				LazyDynaBean abean = (LazyDynaBean) templateSetList.get(i);
				String subflag = (String) abean.get("subflag");
				String isvar = (String) abean.get("isvar");
				String chgstate = (String) abean.get("chgstate");
				String state = (String) abean.get("state");// 是否可编辑
				String item_id = (String) abean.get("item_id");// 是否可编辑
				if ("1".equals(subflag))// 去掉子集项
					continue;
				if ("0".equals(isvar)) {
					if ("2".equals(chgstate)) {// 变化后
						if ("2".equals(state)){
							map.put(item_id, abean);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return map;
	}
    public HashMap getSub_Map() {
        return sub_Map;
    }
    public void setSub_Map(HashMap sub_Map) {
        this.sub_Map = sub_Map;
    }

    public TemplateParam getParamBo() {
        return paramBo;
    }

    public TemplateDataBo getDataBo() {
        return dataBo;
    }

    public String getApproveFlag() {
        return approveFlag;
    }

    public void setApproveFlag(String approveFlag) {
        this.approveFlag = approveFlag;
    }

	public String getNoshow_pageno() {
		return noshow_pageno;
	}

	public void setNoshow_pageno(String noshow_pageno) {
		this.noshow_pageno = noshow_pageno;
	}
}
