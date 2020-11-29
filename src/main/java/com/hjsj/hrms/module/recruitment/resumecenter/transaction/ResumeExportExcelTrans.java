package com.hjsj.hrms.module.recruitment.resumecenter.transaction;

import com.hjsj.hrms.module.recruitment.interfaces.ResumeInterface;
import com.hjsj.hrms.module.recruitment.resumecenter.businessobject.ResumeCenterBo;
import com.hjsj.hrms.module.recruitment.resumecenter.businessobject.ResumeExportExcelBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * 导出excel文件
 * 
 * @Title: ResumeExportExcelTrans.java
 * @Description:
 * @Company: hjsj
 * @Create time: 2016-11-25 上午11:59:40
 * @author chenxg
 * @version 1.0
 */
public class ResumeExportExcelTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        try{
            //导出数据是否包含子集的历史记录
            String historyFlag = (String) this.getFormHM().get("historyFlag");
            //是否导出简历附件
            String attachmentFlag = (String) this.getFormHM().get("attachmentFlag");
            //是否导出简历登记表
            String registration = (String) this.getFormHM().get("registration");
            //导出位置
            String from = (String) this.getFormHM().get("from");
            
            String a0100_es = (String) this.getFormHM().get("a0100_es");
            String z0301_es = (String) this.getFormHM().get("z0301_es");
            
            String[] z0301s = null;
            String[] a0100s = a0100_es.split(",");
            if(StringUtils.isNotEmpty(z0301_es))
            	z0301s = z0301_es.split(",");
            
            StringBuffer a0100str = new StringBuffer("a0100 in(");
            for (String temp : a0100s) {
            	a0100str.append("'");
            	a0100str.append(PubFunc.decrypt(temp));
            	a0100str.append("',");
			}
            if(a0100_es.length()>0){
            	a0100str.setLength(a0100str.length()-1);
            	a0100str.append(")");
            }else{
            	a0100str.setLength(0);
            }
            
            StringBuffer z0301str = new StringBuffer("z0301 in(");
            if(StringUtils.isNotEmpty(z0301_es)) {
            	for (String temp : z0301s) {
            		if(StringUtils.isEmpty(PubFunc.decrypt(temp)))
            			continue;
            		
                	z0301str.append("'");
                	z0301str.append(PubFunc.decrypt(temp));
                	z0301str.append("',");
    			}
                if(z0301_es.length()>0){
                	z0301str.setLength(z0301str.length()-1);
                	z0301str.append(")");
                	
                	if("z0301 in)".equalsIgnoreCase(z0301str.toString()))
                		z0301str.setLength(0);

                }else{
                	z0301str.setLength(0);
                }
            }else{
            	z0301str.setLength(0);
            }
            
            ResumeCenterBo resumeCenterBo = new ResumeCenterBo(this.frameconn,this.userView,"resumeCenter");
            TableDataConfigCache tableCache = null;
            String scheme_id = "zp_resume_191130_00001";//简历中心简历导出
            if("process".equals(from))//职位候选人简历导出
            	scheme_id = "zp_recruit_00001";
            
            tableCache = (TableDataConfigCache) this.userView.getHm().get(scheme_id);
            
            String filterSql = tableCache.getFilterSql();
            //表头字段列表
            ArrayList columns = tableCache.getTableColumns();
            
            for (int i = 0; i < columns.size(); i++) {
			    Object obj = columns.get(i);
			    ColumnsInfo column = (ColumnsInfo) obj;
		        if("resume_flag1".equals(column.getColumnId())){
		        	columns.remove(obj);
		        	break;
		        }
		    }
            
            //表头字段列表
			ArrayList<LazyDynaBean> mergedCellList = resumeCenterBo.getExcleMergedList(columns);
            ArrayList<LazyDynaBean> headList = resumeCenterBo.getExcleHeadList(columns, mergedCellList, false, 0);   
            int rowNum = 0;
            if(mergedCellList != null && mergedCellList.size() > 0)
                rowNum = 1;
            
            String sql = (String)this.userView.getHm().get("export_sql");
            String sortSql = (String)this.userView.getHm().get("sortSql");
            sortSql = StringUtils.isEmpty(sortSql)?"":sortSql;
            String export_sql_field = (String)this.userView.getHm().get("export_sql_field");
            
            RecordVo vo=ConstantParamter.getConstantVo("ZP_DBNAME");
            String dbname="";  //应聘人员库
            if(vo!=null)
            	dbname=vo.getString("str_value");
            else
            	throw GeneralExceptionHandler.Handle(new Exception("请在参数设置中配置招聘人才库！"));
            
            String table_prefix = dbname+"A01";
            if(a0100str.length()>0) {
            	sql = " select * from ( " + sql + ")t where " + a0100str;
            	table_prefix = "t";
            }
            
            if(z0301s!=null && z0301s.length>0 && StringUtils.isNotEmpty(z0301str.toString())) {
            	sql = sql +" AND " + z0301str;
            }
            
            String personSql = "";
            if("1".equalsIgnoreCase(historyFlag))
                personSql = "select a01.a0100 from ("+sql+")a01";
            
            if("process".equals(from))
           	 	personSql = " select  "+table_prefix+".a0100 "+sql.substring(sql.indexOf("from"));
			
			if("1".equals(attachmentFlag) || "1".equalsIgnoreCase(registration)){
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("itemid", "guidkey");
				bean.set("content", "附件");
				bean.set("codesetid", "0");
				bean.set("colType", "A");
				bean.set("decwidth", "100");
				headList.add(bean);
			}
			
            
            if(tableCache.getCustomParamHM() != null) {
                String where = (String) tableCache.getCustomParamHM().get("fastQuerySql");
                where = where == null?"":where;
                String querySql = (String) tableCache.getCustomParamHM().get("pubQuerySql");
                if(StringUtils.isNotEmpty(querySql))
                    where += querySql;
                
                if(StringUtils.isNotEmpty(where))
                    sql = "select * from (" + sql + ") myGridData where 1=1 " + where;
            }
            
            if(StringUtils.isNotEmpty(filterSql)) {
                if(sql.indexOf("myGridData") > -1)
                    sql += filterSql;
                else
                    sql = "select * from (" + sql + ") myGridData where 1=1 " + filterSql;
            }
            
            String orderSql = resumeCenterBo.getOrderSql(dbname, scheme_id);
            String tempSql = sql;
           
            if(StringUtils.isNotEmpty(orderSql)){
            	 //在勾选了部分数据之后，判断排序字段是否带有前缀，如果就并去掉前缀
         		if(orderSql.indexOf(".")!=-1 )
                     orderSql = orderSql.substring(orderSql.indexOf(".")+1);
            	 sql += " order by " + orderSql;
            }
            
            ArrayList<String> empList = new ArrayList<String>();
        	ContentDAO dao = new ContentDAO(this.frameconn);
        	String sqlStr = "select a0100 from ("+tempSql+")temp";
        	this.frowset = dao.search(sqlStr);
        	while(this.frowset.next()){
        		empList.add(dbname+this.frowset.getString(1));
        	}
        	
            // 导出工具类
        	ResumeExportExcelBo excelBo = new ResumeExportExcelBo(this.frameconn);
        	excelBo.setNbase(dbname);
        	RecordVo onlyFieldVo = ConstantParamter.getConstantVo("ZP_ONLY_FIELD");
        	String field = onlyFieldVo.getString("str_value");
        	excelBo.setField(field);
        	excelBo.setAttachmentFlag(attachmentFlag);
        	excelBo.setRegistration(registration);
            //文件名称
            String fileName = this.userView.getUserName() + "_zp.xls";
            if("1".equalsIgnoreCase(historyFlag)) {
                HashMap<String, LazyDynaBean> subValueMap = new HashMap<String, LazyDynaBean>();
                LazyDynaBean personBean = new LazyDynaBean();
                HashMap<String, String> subFieldMap = resumeCenterBo.getSubFieldMap(columns);
                Iterator<Entry<String, String>> iter = subFieldMap.entrySet().iterator();
                while(iter.hasNext()){
                    Entry<String, String> entry = iter.next();
                    String key = entry.getKey();
                    String value = entry.getValue();
                    if(StringUtils.isEmpty(value))
                        continue;
                        
                    String subSql = "SELECT A0100,I9999" + value + " FROM " + dbname + key + " WHERE A0100 IN (" + personSql + ")";
                    if(a0100str.length()>0)
                    	subSql += " and " + a0100str;
                    subSql = subSql + " ORDER BY A0100 DESC, I9999 ASC";
                    
                    
                    String personid = "";
                    int num = 0;
                    String[] values = value.split(",");
                    this.frowset = dao.search(subSql);
                    while(this.frowset.next()) {
                        num++;
                        String a0100 = this.frowset.getString("A0100");
                        if(StringUtils.isEmpty(personid)) {
                            personid = a0100;                            
                            personBean = subValueMap.get(a0100);
                            if(personBean == null)
                                personBean = new LazyDynaBean();
                            
                        } else if(!personid.equalsIgnoreCase(a0100)){
                            subValueMap.put(personid, personBean);
                            
                            personid = a0100;
                            personBean = subValueMap.get(a0100);
                            if(personBean == null)
                                personBean = new LazyDynaBean();
                            
                            num = 1;
                        }
                        
                        for(int m = 0; m < values.length; m++) {                            
                            String itemid = values[m];
                            if(StringUtils.isEmpty(itemid))
                                continue;
                            
                            FieldItem fi = DataDictionary.getFieldItem(itemid, key);
                            String itemValue = ""; 
                            
                            if("D".equalsIgnoreCase(fi.getItemtype())) {
                                String format = resumeCenterBo.getDateFromat(fi);
                                Date date = this.frowset.getDate(itemid);
                                if(date != null)
                                    itemValue = DateUtils.format(date, format);
                            }else
                            	itemValue = this.frowset.getString(itemid); 
                                
                            
                            String codesetid = DataDictionary.getFieldItem(itemid, key).getCodesetid();
                            if(StringUtils.isNotEmpty(codesetid) && !"0".equalsIgnoreCase(codesetid) && StringUtils.isNotEmpty(itemValue))
                                itemValue = AdminCode.getCodeName(codesetid, itemValue);
                            
                            itemValue = StringUtils.isEmpty(itemValue) ? "" : itemValue;
                            
                            String itemValues = (String) personBean.get(itemid);
                            if(StringUtils.isEmpty(itemValues))
                                itemValues = num + "、" + itemValue;
                            else
                                itemValues = itemValues + "\r\n" + num + "、" + itemValue;
                            
                            personBean.set(itemid, itemValues);
                        }
                    }
                    
                    if(personBean != null)
                        subValueMap.put(personid, personBean);

                }
                
                LazyDynaBean a0100Bean = new LazyDynaBean();
                a0100Bean.set("codesetid", "0");
                a0100Bean.set("content", "人员编号");
                a0100Bean.set("colType", "A");
                a0100Bean.set("decwidth", "0");
                a0100Bean.set("itemid", "a0100");
                ArrayList list = new ArrayList();
                list.add(a0100Bean);
                list.addAll(headList);
                ArrayList<LazyDynaBean> dateList = excelBo.getExportData(list, sql);
                for(int n = 0; n < dateList.size(); n++){
                    LazyDynaBean bean = dateList.get(n);
                    LazyDynaBean a0100 = (LazyDynaBean) bean.get("a0100");
                    String personId = (String) a0100.get("content");
                    iter = subFieldMap.entrySet().iterator();
                    while(iter.hasNext()){
                        Entry<String, String> entry = iter.next();
                        personBean = subValueMap.get(personId);
                        String value = entry.getValue();
                        if(StringUtils.isEmpty(value) || personBean == null)
                            continue;
                        
                        String[] values = value.split(",");
                        for(int m = 0; m < values.length; m++) {                            
                            String itemid = values[m];
                            if(StringUtils.isEmpty(itemid))
                                continue;
                            
                            String itemValue = (String) personBean.get(itemid);
                            if(StringUtils.isEmpty(itemValue))
                                continue;
                            
                            LazyDynaBean itemBean = (LazyDynaBean) bean.get(itemid);
                            if(itemBean == null)
                            	continue;
                            itemBean.set("content", itemValue);
                            bean.set(itemid, itemBean);
                        }
                            
                    }
                }
                excelBo.exportExcel(fileName, null, mergedCellList, headList, dateList, null, rowNum);
            } else
                /** 导出excel */
                excelBo.exportExcelBySql(fileName, null, mergedCellList, headList, sql+sortSql, null, rowNum);
            
            if("1".equals(attachmentFlag) || "1".equalsIgnoreCase(registration)){
            	ResumeInterface bo = new ResumeInterface(this.frameconn, this.userView);
            	int flag = 0;
            	if("1".equals(attachmentFlag) && "1".equalsIgnoreCase(registration))
            		flag = 2;
            	else if(!"1".equals(attachmentFlag) && "1".equalsIgnoreCase(registration))
            		flag = 1;
            	fileName = bo.ExportResumeInfoAndZip(empList, fileName, flag);
            }
            
            this.getFormHM().put("fileName", PubFunc.encrypt(fileName));
            this.getFormHM().put("flag", "true");
        }catch (Exception e) {
            this.getFormHM().put("flag", "false");
            e.printStackTrace();
        }
    }

}
