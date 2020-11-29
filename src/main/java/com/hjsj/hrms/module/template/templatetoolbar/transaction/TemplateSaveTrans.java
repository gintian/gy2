package com.hjsj.hrms.module.template.templatetoolbar.transaction;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.template.templatecard.businessobject.TempletChgLogBo;
import com.hjsj.hrms.module.template.templatesubset.businessobject.TemplateSubsetBo;
import com.hjsj.hrms.module.template.utils.TemplateBo;
import com.hjsj.hrms.module.template.utils.TemplateDataBo;
import com.hjsj.hrms.module.template.utils.TemplateFuncBo;
import com.hjsj.hrms.module.template.utils.TemplateUtilBo;
import com.hjsj.hrms.module.template.utils.javabean.TemplateFrontProperty;
import com.hjsj.hrms.module.template.utils.javabean.TemplateItem;
import com.hjsj.hrms.module.template.utils.javabean.TemplateParam;
import com.hjsj.hrms.module.template.utils.javabean.TemplateSet;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.sql.RowSet;
import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class TemplateSaveTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
        HashMap hm=this.getFormHM();
        TemplateFrontProperty frontProperty = new TemplateFrontProperty(this.getFormHM());
        String moduleId = frontProperty.getModuleId();
        String tabId = frontProperty.getTabId();
        String taskId = frontProperty.getTaskId();
        String inforType = frontProperty.getInforType();
        String viewType = frontProperty.getViewType();
        boolean bBatchApprove = frontProperty.isBatchApprove();
        ArrayList list=(ArrayList)hm.get("savedata"); //获取变化的数据
        String noHint=(String)hm.get("noHint"); //是否不显示提示信息
        String isCompute=(String)hm.get("isCompute"); //是否不显示提示信息
        if ("".equals(taskId)){
            return;
        }

        TemplateParam paramBo=new TemplateParam(this.getFrameconn(),this.userView,Integer.parseInt(tabId));
        TemplateUtilBo utilBo= new TemplateUtilBo(this.frameconn,this.userView); 
        TemplateBo templateBo=new TemplateBo(this.getFrameconn(),this.userView,paramBo);
        templateBo.setModuleId(moduleId);
        templateBo.setTaskId(taskId);
        
        if((list==null||list.size()==0))
        {
        	String autoCompute = this.batchCompute(taskId,templateBo,paramBo,utilBo,isCompute,noHint);          
            this.getFormHM().put("autoCompute",autoCompute);
            return;
        }
        TemplateDataBo tableDataBo=new TemplateDataBo(this.getFrameconn(),this.userView,paramBo);
        
        String tableName=utilBo.getTableName(moduleId,Integer.parseInt(tabId), taskId);     
        
        /**数据集字段列表*/
        ArrayList templateSetList =null;
        ArrayList cellList= utilBo.getAllCell(Integer.parseInt(tabId));
        
        // 查找变化前的历史记录单元格,保存时把这部分单元格的内容过滤掉，不作处理
        HashMap filedPrivMap =tableDataBo.getFieldPrivMap(cellList, taskId);
        ArrayList fieldList=filterTemplateSetList(cellList,filedPrivMap);
        HashMap fieldMap = new HashMap();
        for (int i=0;i<fieldList.size();i++){
            TemplateSet setBo =(TemplateSet)fieldList.get(i);
            fieldMap.put(setBo.getTableFieldName(), "1");
        }
        
        boolean needCompute=false;//有变化后指标保存时才触发自动计算，其他不触发，稍提高速度。
        String blacklist_per="";//黑名单人员库
        String blacklist_field="";//黑名单人员指标     
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        ArrayList<LazyDynaBean> subDataList = new ArrayList<LazyDynaBean>();
        try
        {
            if(paramBo.getOperationType()==0){//人员调入模板
                Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
                blacklist_per=sysbo.getAttributeValues(Sys_Oth_Parameter.BLACKLIST,"base");//黑名单人员库
                blacklist_field=sysbo.getAttributeValues(Sys_Oth_Parameter.BLACKLIST,"field");//黑名单人员指标
            }
             
            for(int i=0;i<list.size();i++){
                MorphDynaBean bean=(MorphDynaBean)list.get(i);
                HashMap map = PubFunc.DynaBean2Map(bean);
                               
                //判断是否是黑名单里的人物
                if(blacklist_per!=null&&blacklist_field!=null&&blacklist_per.trim().length()>0&&blacklist_field.trim().length()>0)
                {
                    if(fieldMap.get(blacklist_field+"_2")!=null)
                    {
                        String value=(String)map.get(blacklist_field+"_2");
                        if(value!=null&&value.trim().length()>0)
                        {
                            if(templateBo.validateIsBlackList(blacklist_per,blacklist_field,value))
                            {
                                throw new Exception(map.get("a0101_1")+"在黑名单库有记录，不允许保存!");
                            }
                        }
                    }
                }

                ArrayList updFieldList=new ArrayList();//要修改的字段
                ArrayList updDataList=new ArrayList(); //要修改字段对应的数据
                ArrayList updAutoLogSetBoList=new ArrayList();
                String updAutoLotObjectid="";
                String ins_id="0";

                for(int j=0;j<fieldList.size();j++)
                {
                	boolean bUpdA0101_1=false;//是否需要同步更改变化前姓名 人员调入、新增机构模板需要 wangrd 20160908
                    TemplateSet setBo =(TemplateSet)fieldList.get(j);
                    if ("C".equals(setBo.getFlag())||"P".equals(setBo.getFlag())){
                        continue;
                    }
                    if (frontProperty.isListView()){//列表不需要更新子集。
                        if (setBo.isSubflag())
                            continue;
                    }
                    String fieldName= setBo.getTableFieldName();
                    if (updFieldList.contains(fieldName)){//排除多个单元格指定同一指标的情况。
                        continue;
                    }
                    
                    int updDataSize =updDataList.size();
                    //liuyz 28807 首先先判断用户是否修改了这个字段，数值型表格控件删除指后传过来的值为null，手工在这里修改一下让用户清空值能保存上,否则用户无法保存清空值。
                    if(map.containsKey(fieldName)&&map.get(fieldName)==null&&"N".equals(setBo.getField_type()))
                    {
                    	map.put(fieldName,"");
                    }
                    if(map.get(fieldName)!=null){//record有此指标的值
                    	String data = "";
                        if("signature".equals(fieldName)){//签章
                        	data=map.get(fieldName)+"";
                        	if(!"".equals(data)){
                        		int signatureType = paramBo.getTemplateModuleParam().getSignatureType();
                        		data = analysisSignatureXml(dao,data,signatureType);
                        	}
                        }else{
                        	data=map.get(fieldName)+"";
                        }
                        if (data== null) data="";
                        
                        if (setBo.isABKItem() && !setBo.isSubflag()){//普通指标
                        	TemplateItem templateItem = utilBo.convertTemplateSetToTemplateItem(setBo);//lis 20160705
                            FieldItem fldItem = templateItem.getFieldItem();
                            if (fldItem==null) 
                                continue;
                            if(setBo.isBcode()){//代码型
                                if (data!=null&&data.length()>0){
                                    String []  arrData= data.split("`");
                                    if (arrData.length>0){
                                        data=arrData[0];
                                    }
                                    else {//兼容data="`"的情况
                                        data=data.replace("`", "");
                                    }
                                    
                                }
                                updDataList.add(data);
                            }else if("D".equals(fldItem.getItemtype())){//
                                String disformat=setBo.getDisformat()+"";   //disformat=25: 1990.01.01 10:30
                                if(StringUtils.isNotBlank(data)){
                                	if("card".equals(viewType)){
	                                    if(!"25".equals(disformat))
	                                    {
	                                        java.sql.Date date = null;
	                                        String dateStr = data;
	                                        if(dateStr.indexOf("-")<0)
	                                            date = DateUtils.getSqlDate(data,"yyyy.MM.dd");
	                                        else 
	                                            date = DateUtils.getSqlDate(data,"yyyy-MM-dd");
	                                        updDataList.add(date);
	                                    }
	                                    else
	                                    {
	                                        Timestamp datetime=DateUtils.getTimestamp(data+":00","yyyy-MM-dd HH:mm:ss");
	                                        updDataList.add(datetime);
	                                    }
                                	}else{
                                		int dateformat = getFormat(setBo.getDisformat());//4,7,10,16
                                		Timestamp datetime = null;
                                		Calendar now = Calendar.getInstance();  
                                        int month = now.get(Calendar.MONTH) + 1;  
                                        int day = now.get(Calendar.DAY_OF_MONTH);
                                		if(dateformat==4){//年
                                			if(data.indexOf("-")<0)
                                				datetime = DateUtils.getTimestamp(data+"."+month+"."+day,"yyyy.MM.dd");
	                                        else 
	                                        	datetime = DateUtils.getTimestamp(data+"-"+month+"-"+day,"yyyy-MM-dd");
	                                        updDataList.add(datetime);
                                		}
                                		else if(dateformat==7){//年月
                                			if(data.indexOf("-")<0)
                                				datetime = DateUtils.getTimestamp(data+"."+day,"yyyy.MM.dd");
	                                        else 
	                                        	datetime = DateUtils.getTimestamp(data+"-"+day,"yyyy-MM-dd");
	                                        updDataList.add(datetime);
                                		}
                                		else if(dateformat==10){//年月日
                                			if(data.indexOf("-")<0)
                                				datetime = DateUtils.getTimestamp(data,"yyyy.MM.dd");
	                                        else 
	                                        	datetime = DateUtils.getTimestamp(data,"yyyy-MM-dd");
	                                        updDataList.add(datetime);
                                		}
                                		else if(dateformat==16){//年月日时分
                                			if(data.indexOf("-")<0)
                                				datetime = DateUtils.getTimestamp(data+":00","yyyy.MM.dd HH:mm:ss");
	                                        else 
	                                        	datetime = DateUtils.getTimestamp(data+":00","yyyy-MM-dd HH:mm:ss");
	                                        updDataList.add(datetime);
                                		}
                                	}
                                }else{
                                    updDataList.add(null);
                                }
                            }else if("N".equals(fldItem.getItemtype())){
                                if(data.indexOf(".")!=-1){
                                    if(data.split("\\.")[0].length()>fldItem.getItemlength()){
                                        String valueLengthError=fldItem.getItemdesc()
                                        +ResourceFactory.getProperty("templa.value.lengthError")
                                        +fldItem.getItemlength()+","+ResourceFactory.getProperty("templa.value.fix");
                                        throw new Exception(valueLengthError.toString());
                                    }
                                }else{
                                    if(data.length()>fldItem.getItemlength()){
                                        String valueLengthError=fldItem.getItemdesc()
                                        +ResourceFactory.getProperty("templa.value.lengthError")
                                                 +fldItem.getItemlength()+","+ResourceFactory.getProperty("templa.value.fix");
                                         throw new Exception(valueLengthError.toString());
                                    }
                                }
                                if(fldItem.getDecimalwidth()==0){
                                	//liuyz bug26865 指标设置的长度和模版设置的小数长度不同，导致用户可以输入带小数的值，这里fldItem取的是指标小数位数，所以转换时会出现异常。给出提示。
                                	try{
                                		updDataList.add(data.length()==0?null:Integer.parseInt(data));
                                	}
                                	catch (Exception e) {
                                		throw new Exception("指标项："+fldItem.getItemdesc()+"不允许有小数位");
									}
                                }else{
                                    String value = PubFunc.DoFormatDecimal(data==null||data.length()==0?"":data, fldItem.getDecimalwidth());
                                    updDataList.add(value.length()==0?null:PubFunc.parseDouble(value));
                                }
                            }   else  if("M".equals(fldItem.getItemtype())){
                            	String opinion_field = paramBo.getOpinion_field();
                            	//liuyz 大文本html编辑器不需要检测是否超过字数限制。但是fldItem中的inputType不对，需要重新获取。
                            	FieldItem fielditem = DataDictionary.getFieldItem(fldItem.getItemid());
                    			if(fielditem!=null)
                    				fldItem.setInputtype(fielditem.getInputtype());
                            	if(StringUtils.isNotBlank(opinion_field) && !opinion_field.equalsIgnoreCase(fldItem.getItemid())&&fldItem.getItemlength()!=10&&fldItem.getItemlength()!=0&&fldItem.getInputtype()!=1){
                            	//if(StringUtils.isNotBlank(opinion_field) && !opinion_field.equalsIgnoreCase(fldItem.getItemid())&&fldItem.getItemlength()!=10&&fldItem.getItemlength()!=0&&fldItem.getInputtype()!=1){
                            		  if(data.length()>fldItem.getItemlength()){
	                            			  StringBuffer valueLengthError = new StringBuffer();
	                                      	
	                                          valueLengthError.append(ResourceFactory.getProperty("template_new.filed"));
	                                          valueLengthError.append("[");
	  	                                      valueLengthError.append(fldItem.getItemdesc());
	  	                                      valueLengthError.append("]");
	                                          valueLengthError.append(ResourceFactory.getProperty("template_new.allowMaxInputLength"));
	                                          valueLengthError.append(fldItem.getItemlength());
	                                          valueLengthError.append(ResourceFactory.getProperty("template_new.char"));
	                                          throw new Exception(valueLengthError.toString());
	                                    }
                            	}
                            	updDataList.add(data);
                         }  
                          else {
                                if(TemplateFuncBo.getStrLength(data)>fldItem.getItemlength()){
	                                    StringBuffer valueLengthError = new StringBuffer();
	                                    	
	                                    valueLengthError.append(ResourceFactory.getProperty("template_new.filed"));
	                                    valueLengthError.append("[");
	                                    valueLengthError.append(fldItem.getItemdesc());
	                                    valueLengthError.append("]");
	                                    valueLengthError.append(ResourceFactory.getProperty("template_new.allowMaxInputLength"));
	                                    valueLengthError.append(fldItem.getItemlength());
	                                    valueLengthError.append(ResourceFactory.getProperty("template_new.char"));
	                                    throw new Exception(valueLengthError.toString());
                                }
                                updDataList.add(data);
                                if("a0101_2".equalsIgnoreCase(fieldName)){//变化后姓名
                                	if (paramBo.getOperationType() ==0){//人员调入模板
                                		updDataList.add(data);
                                		bUpdA0101_1=true;
                                	}
                                	
                                }else if("codeitemdesc_2".equalsIgnoreCase(fieldName)){//变化后机构名称
                                	if (paramBo.getOperationType() ==5){//新增机构、岗位
                                		updDataList.add(data);
                                		bUpdA0101_1=true;
                                	}
                                }
                            }
                        }
                        else if (setBo.isSubflag()) {//子集 
                          //  data = SafeCode.decode(data);//前台没有encode。
                            if("card".equals(viewType)){//卡片模式保存子集，如果有上传附件则保存到指定目录
                            	TemplateSubsetBo subBo=new TemplateSubsetBo(this.getFrameconn(),this.userView,tabId,setBo.getTableFieldName());
                            	String objectid="";
                            	if(paramBo.getInfor_type()==1) {
                            		objectid=map.get("basepre")+"`"+map.get("a0100");
                            	}else if(paramBo.getInfor_type()==2){
                            		objectid=(String) map.get("b0110");
                            	}else if(paramBo.getInfor_type()==3) {
                            		objectid=(String) map.get("e01a1");
                            	}
                            	String old_subXml = subBo.getSub_dataXml(tableName, objectid, tabId, ins_id);
                            	data= PubFunc.keyWord_reback(SafeCode.decode(data));//参数全角转半角
                            	//对比新旧xml附件 清除无效附件
                            	subBo.clearTempFile(old_subXml, data);
                            	data = subBo.saveSubAttachment(data);
                            	String fldValue =data;
                            	String disValue = fldValue;
                            	HashMap subDataMap=subBo.getSubDataMap(fldValue);
                            	fldValue = subBo.encryptOrDecryptAttachment(fldValue,"0");
    							JSONObject subDatajson = JSONObject.fromObject(subDataMap); 
    							disValue = subDatajson.toString();
    							fldValue = SafeCode.encode(fldValue);
    							disValue = SafeCode.encode(disValue);
    							LazyDynaBean dynaBean = new LazyDynaBean();
    							dynaBean.set("pageId", String.valueOf(setBo.getPageId()));
    							dynaBean.set("uniqueId", setBo.getUniqueId());
    							dynaBean.set("fldName", setBo.getTableFieldName());
    							dynaBean.set("keyValue",fldValue); 
    							dynaBean.set("disValue",disValue); 
    					   	 	subDataList.add(dynaBean);
                            }
                            updDataList.add(data);                              
                        }
                        else if("S".equalsIgnoreCase(setBo.getFlag())){//签章
                        	updDataList.add(data);
                        }
                        else {//临时变量
                        	
                        	 if(setBo.isBcode()){//代码型
                                 if (data!=null&&data.length()>0){
                                     String []  arrData= data.split("`");
                                     if (arrData.length>0){
                                         data=arrData[0];
                                     }
                                     else {//兼容data="`"的情况
                                         data=data.replace("`", "");
                                     }
                                     
                                 }
                                 updDataList.add(data);
                             }else if("D".equals(setBo.getField_type())){//
                                 if(StringUtils.isNotBlank(data)){
                                       java.sql.Date date = null;
                                       String dateStr = data;
                                       if(dateStr.indexOf("-")<0)
                                             date = DateUtils.getSqlDate(data,"yyyy.MM.dd");
                                       else 
                                             date = DateUtils.getSqlDate(data,"yyyy-MM-dd");
                                        updDataList.add(date); 
                                 }else{
                                     updDataList.add(null);
                                 }
                             }else if("N".equals(setBo.getField_type())){
                            	 int flddec=setBo.getVarVo().getInt("flddec");
                            	 int fldlen=setBo.getVarVo().getInt("fldlen");
                            	 String chz=setBo.getVarVo().getString("chz");
                            	 if(data.indexOf(".")!=-1){
                                     if(data.split("\\.")[0].length()>fldlen){
                                         String valueLengthError=chz
                                         +ResourceFactory.getProperty("templa.value.lengthError")
                                         +fldlen+","+ResourceFactory.getProperty("templa.value.fix");
                                         throw new Exception(valueLengthError.toString());
                                     }
                                 }else{
                                     if(data.length()>fldlen){
                                         String valueLengthError=chz
                                         +ResourceFactory.getProperty("templa.value.lengthError")
                                                  +fldlen+","+ResourceFactory.getProperty("templa.value.fix");
                                          throw new Exception(valueLengthError.toString());
                                     }
                                 }
                                 if(flddec==0){
                                     updDataList.add(data.length()==0?null:Integer.parseInt(data));
                                 }else{
                                     String value = PubFunc.DoFormatDecimal(data==null||data.length()==0?"":data,flddec);
                                     updDataList.add(value.length()==0?null:PubFunc.parseDouble(value));
                                 }
                             }else if("A".equals(setBo.getField_type())&&"0".equals(setBo.getCodeid())) {//增加临时变量字符型长度校验
                            	 int fldlen=setBo.getVarVo().getInt("fldlen");
                            	 String chz=setBo.getVarVo().getString("chz");
                            	 if(TemplateFuncBo.getStrLength(data)>fldlen){
                            		 StringBuffer valueLengthError = new StringBuffer();
                            		 valueLengthError.append(ResourceFactory.getProperty("label.gz.variable"));
	                                 valueLengthError.append("[");
	                                 valueLengthError.append(chz);
	                                 valueLengthError.append("]");
	                                 valueLengthError.append(ResourceFactory.getProperty("template_new.allowMaxInputLength"));
	                                 valueLengthError.append(fldlen);
	                                 valueLengthError.append(ResourceFactory.getProperty("template_new.char"));
                                     throw new Exception(valueLengthError.toString());
                                 }
                                 updDataList.add(data);   
                             }
                             else 
                            	 updDataList.add(data);   
                        }
                        
                        if(updDataList.size()>updDataSize){//datalist放入数据了 fieldlist也得相应增加
                            updFieldList.add(fieldName);
                            updAutoLogSetBoList.add(setBo);
                            if(fieldName.indexOf("_2")>0){
                            	needCompute=true;
                            }
                            if (bUpdA0101_1){
                            	if("a0101_2".equalsIgnoreCase(fieldName)){//变化后姓名
                            		updFieldList.add("a0101_1");
                            		updAutoLogSetBoList.add(setBo);
                                }else if("codeitemdesc_2".equalsIgnoreCase(fieldName)){//变化后机构名称
                                	updFieldList.add("codeitemdesc_1");
                                	updAutoLogSetBoList.add(setBo);
                                }
                            }
                        }
                    }
                }
                
                String updateSql="update "+tableName+" set ";
                String fieldName = null;
                StringBuffer updateFields = new StringBuffer();
                for(int j=0;j<updFieldList.size();j++){
                    fieldName = (String)updFieldList.get(j);
                    updateFields.append("," + fieldName+"=?");
                }
                if(updateFields.length() > 1)
                	updateSql += updateFields.substring(1);
                if (paramBo.getInfor_type()== 1){
                	String basepre = "";
                	String a0100 = "";
                	if("card".equals(viewType)){
                		basepre=(String)map.get("basepre");
                        a0100=(String)map.get("a0100");
                	}else{
                		String objectid = (String)map.get("objectid_e");
                		objectid = PubFunc.decrypt(SafeCode.decode(objectid));
                		String[] arr = objectid.split("`");
                		basepre=arr[0];
                        a0100=arr[1];
                	}
                	updAutoLotObjectid=basepre+"`"+a0100;
                    updateSql+=" where A0100='"+a0100+"' and BasePre='"+basepre+"'";
                }
                else if (paramBo.getInfor_type()== 2){
                	String b0110 = "";
                	if("card".equals(viewType)){
                		b0110=(String)map.get("b0110");
                	}else{
                		b0110=(String)map.get("objectid_e");
                        b0110 = PubFunc.decrypt(SafeCode.decode(b0110));
                	}
                	updAutoLotObjectid=b0110;
                    updateSql+=" where b0110='"+b0110+"'";
                }
                else {
                	String e01a1 = "";
                	if("card".equals(viewType)){
                		e01a1=(String)map.get("e01a1");
                	}else{
                		e01a1=(String)map.get("objectid_e");
                		e01a1 = PubFunc.decrypt(SafeCode.decode(e01a1));
                	}
                	updAutoLotObjectid=e01a1;
                    updateSql+=" where e01a1='"+e01a1+"'"; 
                }
                if (!"0".equals(taskId)){
                    ins_id=(String)map.get("ins_id");
                    updateSql+=" and ins_id="+ins_id+"";
                }
               // String signature = (String)map.get("signature");
               // signature = analysisSignatureXml(dao,signature);
                //updateSql+=" and signature="+signature+"";
                if (updFieldList.size()>0){
                	Boolean isAotuLog = paramBo.getIsAotuLog();
        			Boolean isRejectAotuLog = paramBo.getIsRejectAotuLog();
        			if(isRejectAotuLog==true&&!"0".equalsIgnoreCase(ins_id)){
        				Boolean haveReject= utilBo.isHaveRejectTaskByInsId(ins_id);
        				if(haveReject){
        					isAotuLog=true;
        				}
        			}
                	if(isAotuLog&&!("0".equalsIgnoreCase(ins_id)&&(paramBo.getOperationType()==0||paramBo.getOperationType()==5))){
                		TempletChgLogBo chgLogBo=new TempletChgLogBo(this.frameconn,this.userView,paramBo);
                		chgLogBo.createTemplateChgLogTable("templet_chg_log");
                		String realTask_id=(String) map.get("realtask_id_e");
                		realTask_id=PubFunc.decrypt(realTask_id);
                		if(StringUtils.isBlank(realTask_id)){
                			realTask_id="0";
                		}
                		if(taskId.indexOf(",")==-1&&StringUtils.isNotBlank(taskId)){
                			realTask_id=taskId;
                		}
                		chgLogBo.insertOrUpdateAllLogger(updFieldList,updAutoLogSetBoList,updDataList,ins_id,realTask_id,updAutoLotObjectid,tableName,paramBo.getInfor_type());
                	}
                    dao.update(updateSql, updDataList);
                }
                this.getFormHM().put("updateSize",updFieldList.size()); 
            }
            
            String autoCompute = this.batchCompute(taskId,templateBo,paramBo,utilBo,isCompute,noHint);            
            this.getFormHM().put("autoCompute",autoCompute);      
            this.getFormHM().put("subDataList",subDataList);      
        } 
        catch(Exception ex)
        {
            ex.printStackTrace();
            //解决保存提示8060问题
            String message=ex.toString();
			if(message.indexOf("最大")!=-1&&message.indexOf("8060")!=-1&&Sql_switcher.searchDbServer()==1)
			{  
				PubFunc.resolve8060(this.getFrameconn(),tableName);
				throw GeneralExceptionHandler.Handle(new Exception("请重新操作!"));
			}
			else
				throw GeneralExceptionHandler.Handle(ex);
        }       

    }
    
    private String batchCompute(String taskId,TemplateBo templateBo,TemplateParam paramBo,TemplateUtilBo utilBo,String isCompute,String noHint) throws GeneralException {
    	String autoCompute="false";
    	try {
	    	Boolean bCalc=false;	
	        if("0".equals(taskId)|| "1".equals(templateBo.isStartNode(taskId))){
				if(paramBo.getAutoCaculate().length()==0){
					if("true".equalsIgnoreCase(SystemConfig.getPropertyValue("templateAutoCompute"))){
						bCalc=true;
					}
				}
				else if("1".equals(paramBo.getAutoCaculate())){
					bCalc=true;
				}	
			}else {
				if(paramBo.getSpAutoCaculate().length()==0){
					if("true".equalsIgnoreCase(SystemConfig.getPropertyValue("templateAutoCompute"))){
						bCalc=true;
					}
				}
				else if("1".equals(paramBo.getSpAutoCaculate())){
					bCalc=true;
				}
			}
			
			if(bCalc &&"true".equalsIgnoreCase(isCompute)){//不再根据是否有变化后指标修改判断是否需要计算。按照保存计算，切人、切页不计算
				ArrayList formulalist=templateBo.readFormula();
				formulalist.addAll(templateBo.readSubsetFormula());
	    		if(formulalist.size()>0)
	    		{
	    			String[] taskids = taskId.split(","); 
	    			String ins_ids="";
	    			for(int i=0;i<taskids.length;i++){
	    				String ins_id =utilBo.getInsId(taskids[i]); 
	    				ins_ids=ins_ids+","+ins_id;
	    			}
	    			if("true".equals(noHint))
	    				templateBo.setThrow(false);
	    			templateBo.setInsid(ins_ids.substring(1));
	    			templateBo.batchCompute(ins_ids.substring(1));
	    			autoCompute="true";  
	    		}
			}
    	}catch(Exception e) {
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
    	}
    	return autoCompute;
    }
    
    private String analysisSignatureXml(ContentDAO dao,String signature,int signatureType) {
    	RowSet rowSet=null;
    	Document doc = null;
    	try {
			doc = PubFunc.generateDom(signature);
			List<Element> elelist = doc.getRootElement().getChildren();
			for(int j = 0; j < elelist.size(); j++){
				Element ele = elelist.get(j);
				String documentid = ele.getAttributeValue("DocuemntID");
				if(signatureType==1){//BJCA
					if("BJCA".equals(documentid)){
						List<Element> list = ele.getChildren();
						for (int i = 0; i < list.size(); i++) {
							Element e = list.get(i);
							if("item".equals(e.getName())){
								String SignatureID = e.getAttributeValue("SignatureID");
								String delflag = e.getAttributeValue("delflag");
								if(delflag!=null&&"true".equals(delflag)){ 
									String sql = "delete from HTMLSignature where signatureid='"+SignatureID+"' and documentid='"+documentid+"'";
									dao.delete(sql, new ArrayList());
									File tempFile = new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+SignatureID+".gif");
								    if (!tempFile.exists()) {  
								    	 continue;
								    }  
								    tempFile.getAbsoluteFile().delete();
								    ele.removeContent(e);
								}
								rowSet = dao.search("select * from HTMLSignature where signatureid='"+SignatureID+"' and documentid='"+documentid+"'");
								while(rowSet.next()){
									String username = rowSet.getString("username");
									username = username==null?"":username;
									e.setAttribute("UserName", username);
								}
							}
						}
					}
				}else if(signatureType==0||signatureType==3){//金格科技
					if(!"BJCA".equals(documentid)){
						List<Element> ele2list = ele.getChildren("item");
						if(ele2list!=null&&ele2list.size()>0){
							for(int k=0;k<ele2list.size();k++){
								Element ele2=(Element)ele2list.get(k);
								String SignatureID = ele2.getAttributeValue("SignatureID");
								rowSet = dao.search("select * from HTMLSignature where signatureid='"+SignatureID+"' and documentid='"+documentid+"'");
								if(rowSet.next()){
									String username = rowSet.getString("username");
									username = username==null?"":username;
									ele2.setAttribute("UserName", username);
								}else{
									String delflag = ele2.getAttributeValue("delflag");
									if("true".equals(delflag)) {
										
									}else {
										ele.removeContent(ele2);
									}
								}
							}
						}
					}
				}
			}
    	}catch (Exception e) {
			e.printStackTrace();
		}
    	XMLOutputter outputter=new XMLOutputter();
		Format format=Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);
		return outputter.outputString(doc);
	}

   

    /**   
     * @Title: filterTemplateSetList   
     * @Description: 过滤掉无权限的指标
     * @param @param templateSetList
     * @param @return 
     * @return ArrayList 
     * @throws   
    */
    private ArrayList filterTemplateSetList(ArrayList cellList,HashMap filedPrivMap)
    {
        ArrayList fieldList = new ArrayList();
        for (int i = cellList.size() - 1; i >= 0; i--) {
            TemplateSet setBo = (TemplateSet) cellList.get(i);
            String fieldname = setBo.getTableFieldName();
            /*
            if ("a0101_1".equals(fieldname) || "codeitemdesc_1".equals(fieldname)) {//以下已经有对a0101_1的处理 不要加了
                fieldList.add(setBo);
                continue;
                
            }
            */
            if ("signature".equals(fieldname)) {
            	fieldList.add(setBo);
            	continue;
            }
            
            if (setBo.isSubflag()) {//子集变化前也可以保存 wangrd 20160829 不区分权限了。
            	fieldList.add(setBo);
            	continue;
            }

            if ("".equals(fieldname)) {
                continue;
            }
            if (setBo.getChgstate() == 1 && (!"V".equals(setBo.getFlag()))) {// 变化前 临时变量
                continue;
            }
            if (filedPrivMap.get(setBo.getUniqueId()) != null) {
                String rwPriv = (String) filedPrivMap.get(setBo.getUniqueId());
                if (!"2".equals(rwPriv)) {
                    continue;
                }
            }
            fieldList.add(setBo);
        }
        return fieldList;
    }
    
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
    
}
