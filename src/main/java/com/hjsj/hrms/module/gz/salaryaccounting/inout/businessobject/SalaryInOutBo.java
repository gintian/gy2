/**
 * @author lisd
 * @date 2015-7-3
 */
package com.hjsj.hrms.module.gz.salaryaccounting.inout.businessobject;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryLProgramBo;
import com.hjsj.hrms.businessobject.gz.templateset.GzExcelBo;
import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.RowSetToXmlBuilder;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryAccountBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.module.gz.utils.SalarySetBo;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.analyse.YearMonthCount;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import javax.sql.RowSet;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *Title:SalaryInOutBo
 * Description:薪资发放导入导出
 * Company:HJHJ
 * Create time:2015-7-3
 * @author lis
 */
public class SalaryInOutBo {

    private Connection conn = null;
    /** 薪资类别号 */
    private int salaryid = -1;
    /** 登录用户 */
    private UserView userview;

    private HashMap<String,HSSFFont> fontMap = new HashMap<String, HSSFFont>();
    private HSSFWorkbook  wb = new HSSFWorkbook();
    private HSSFSheet sheet=null;

    public SalaryInOutBo(Connection conn, int salaryid, UserView userview) {
        this.setConn(conn);
        this.setSalaryid(salaryid);
        this.setUserview(userview);
    }

    /**
     * 导出文件
     *
     * @author lis
     * @date 2015-7-2
     * @param salaryid
     *            薪资类别id
     * @param flag
     *            1是导出excel，否则导出XML格式
     * @param itemids
     *            要导出的薪资项目
     * @return 文件名
     * @throws GeneralException
     */
    public String exportFile(String tabelName,String sql, String flag, String itemids,ArrayList<LazyDynaBean> salaryItemList,String appdate,String imodule)
            throws GeneralException {
        String fileName = "";
        try {
            ArrayList tempSalaryItemList = new ArrayList();
            ArrayList<LazyDynaBean> headList = new ArrayList<LazyDynaBean>();// 封装excel表头数据
            ArrayList<LazyDynaBean> mergeList = new ArrayList<LazyDynaBean>();// 封装excel表头合并列数据
            String dateString="";//解析业务日期变为yyyy年mm月格式 用于文件名

            if(!"".equals(appdate)&&appdate.length()!=0){
                appdate=appdate.replace('.', '-');//统一格式
                String []str=appdate.split("-");
                if(str.length>0)
                    dateString=str[0]+"年"+str[1]+"月";
            }

            if ("1".equals(flag)) {// 导出excel格式
            	//先找出是否有合并列
                boolean hasMerge = false;
                for (int i = 0; i < salaryItemList.size(); i++) {
                    LazyDynaBean abean = salaryItemList.get(i);
                    String itemid = ((String) abean.get("itemid")).toUpperCase();
                    String mergedesc = ((String) abean.get("mergedesc"));
                    
                    if (itemids.indexOf("/" + itemid + "/") != -1 && StringUtils.isNotBlank(mergedesc)) {// 进行过滤，只选择勾选的薪资项目
                    	hasMerge = true;
                    	break;
                    }
                }
                
                LazyDynaBean serialNumber=new LazyDynaBean();
                serialNumber.set("content", "序号");
                serialNumber.set("itemid", "rowNum");
                serialNumber.set("codesetid", "0");
                serialNumber.set("decwidth", "0");
                serialNumber.set("colType", "N");
                if(hasMerge) {
                    //设置非合并列中包含的指标起始与终止的行
                	serialNumber.set("fromRowNum", 0);
                	serialNumber.set("toRowNum", 1);
                    //设置指标的起始与终止的列
                	serialNumber.set("fromColNum", 0);
                	serialNumber.set("toColNum", 0);
                }
                headList.add(serialNumber);
                
                // 合并列的开始列
                int mergeNum = 0;
                String mergedesc_old = "";
                
                for (int i = 0; i < salaryItemList.size(); i++) {
                    LazyDynaBean abean = salaryItemList.get(i);
                    String itemid = ((String) abean.get("itemid"))
                            .toUpperCase();
                    String itemdesc = ((String) abean.get("itemdesc"));
                    String codesetid = ((String) abean.get("codesetid"));
                    String mergedesc = ((String) abean.get("mergedesc"));
                    
                    if (itemids.indexOf("/" + itemid + "/") != -1) {// 进行过滤，只选择勾选的薪资项目
                    	// 如果如果前面是和斌列，则在列不同的时候加上合并列的list
                    	if(StringUtils.isNotBlank(mergedesc_old) && !mergedesc.equalsIgnoreCase(mergedesc_old)) {
                    		mergeList.add(getMegerBean(mergeNum, headList.size() - 1, mergedesc_old));
                    		mergeNum = headList.size();
                    	// 如果前面没有合并列，但是在这一列开始有，记录下开始的列号
                    	}else if(StringUtils.isBlank(mergedesc_old) && StringUtils.isNotBlank(mergedesc)) {
                    		mergeNum = headList.size();
                    	}
                    	mergedesc_old = mergedesc;
                    	
                        tempSalaryItemList.add(abean);
                        LazyDynaBean bean = new LazyDynaBean();
                        
                        // 设置非合并列
                        if(hasMerge) {
	                        //设置非合并列中包含的指标起始与终止的行
		                    bean.set("fromRowNum", StringUtils.isNotBlank(mergedesc)?1:0);
		                    bean.set("toRowNum", 1);
		                    //设置指标的起始与终止的列
		                    bean.set("fromColNum", headList.size());
		                    bean.set("toColNum", headList.size());
                        }
                        String itemtype = ((String) abean.get("itemtype"));
                        HashMap headStyleMap = new HashMap();//表头格式
                        HashMap colStyleMap = new HashMap();//内容单元格格式
                        if(abean.get("displaywidth") != null) {//在没有栏目设置的时候不传displaywidth，按照excel控件默认的宽度显示
                            headStyleMap.put("columnWidth", abean.get("displaywidth"));
                            headStyleMap.put("isFontBold", true);
                            bean.set("headStyleMap", headStyleMap);// 该列宽度

                            colStyleMap.put("columnWidth", abean.get("displaywidth"));
                            colStyleMap.put("align", abean.get("align"));
                            bean.set("colStyleMap", colStyleMap);// 该列宽度
                        }


                        String decwidth = (String) abean.get("decwidth");
                        bean.set("content", itemdesc);// 列头名称
                        bean.set("itemid", itemid);// 列头代码
                        bean.set("codesetid", codesetid);// 列头代码
                        //添加合计标识
                        if(abean.getMap().containsKey("is_sum")&& "1".equalsIgnoreCase((String)abean.get("is_sum"))){
                            bean.set("total", "1");
                        }
                        // bean.set("comment", itemid);//列头注释
                        bean.set("decwidth", decwidth);// 列小数点后面位数
                        bean.set("colType", itemtype);// 该列数据类型
                        if("a00z2".equalsIgnoreCase(itemid))
                            bean.set("dateFormat", "yyyy-MM");
                        else if("D".equalsIgnoreCase(itemtype)) {
                        	String itemlength = (String) abean.get("itemlength");
                        	bean.set("dateFormat", getDateFormatFromLength(Integer.parseInt(itemlength), 0).replace(".", "-"));
                        }
                        headList.add(bean);
                    }
                }
                // 如果最后一列是合并列，则在最后需要加上改合并列
                if(StringUtils.isNotBlank(mergedesc_old)) {
                	mergeList.add(getMegerBean(mergeNum, headList.size() - 1, mergedesc_old));
                }

                ExportExcelUtil excelUtil = new ExportExcelUtil(this.conn,this.userview);
                if(itemids.indexOf("/APPPROCESS/") != -1) //如果有审批意见列设置固定高度
                    excelUtil.setRowHeight((short)750);
                
                excelUtil.setHeadRowHeight((short)820);
                //excelUtil.setProtect(true);//是否锁定excel页面
                if(!"1".equals(imodule))
                    fileName = dateString+"工资明细表_"+this.userview.getUserName() + ".xls";
                else
                    fileName = dateString+"保险明细表_"+this.userview.getUserName() +".xls";
                // 导出excel
                fileName=excelUtil.exportExcelBySql(fileName,"",mergeList, headList,sql, null, hasMerge?1:0);
            } else {
                RowSetToXmlBuilder builder = new RowSetToXmlBuilder(this.conn);
                if(!"1".equals(imodule))
                    fileName = dateString+"工资明细表_"+this.userview.getUserName() + ".xml";
                else
                    fileName = dateString+"保险明细表_"+this.userview.getUserName() +".xml";
                FileOutputStream fileOut = new FileOutputStream(System
                        .getProperty("java.io.tmpdir")
                        + System.getProperty("file.separator") + fileName);
                RowSet rowSet = null;
                try {
                    ContentDAO dao = new ContentDAO(this.conn);
                    rowSet = dao.search(sql);
                    fileOut.write((builder.outPutXml2(rowSet, tabelName,itemids)).getBytes());
                } finally {
                    PubFunc.closeIoResource(fileOut);
                    PubFunc.closeDbObj(rowSet);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return fileName;
    }
    
    private LazyDynaBean getMegerBean(int mergeFromNum, int mergeToNum, String mergedesc) {
    	LazyDynaBean bean = new LazyDynaBean();
        //设置合并列的起始行
        bean.set("fromRowNum", 0);
        //设置合并列的起始列
        bean.set("fromColNum", mergeFromNum);
        //设置合并列的终止行
        bean.set("toRowNum", 0);
        //设置合并列的终止列
        bean.set("toColNum", mergeToNum);
        //设置合并列的名称
        bean.set("content", mergedesc);
		
        return bean;
    }
    /**
     * 导出审批总计表
     * @param tabelName
     * @param dataList
     * @param flag
     * @param itemidList 导出列id
     * @param itemText	导出列名
     * @param appdate
     * @param imodule//薪资和保险区分标识  1：保险  否则是薪资
     * @return
     * @throws GeneralException
     */
    public String exportFile(String tabelName,ArrayList<LazyDynaBean> dataList, String [] itemidList,ArrayList<String> itemText,String appdate,String imodule)
            throws GeneralException {
        String fileName = "";
        try {
            ArrayList tempSalaryItemList = new ArrayList();
            ArrayList<LazyDynaBean> headList = new ArrayList<LazyDynaBean>();// 封装excel表头数据
            String dateString="";//解析业务日期变为yyyy年mm月格式 用于文件名

            if(!"".equals(appdate)&&appdate.length()!=0){
                appdate=appdate.replace('.', '-');//统一格式
                String []str=appdate.split("-");
                if(str.length>0)
                    dateString=str[0]+"年"+str[1]+"月";
            }

            for ( int i=0;i<itemidList.length;i++) {
                String str =itemidList[i];
                LazyDynaBean bean = new LazyDynaBean();
                if("text".equalsIgnoreCase(str)){
                    bean.set("content", itemText.get(i));// 列头名称
                    bean.set("itemid", str);// 列头代码
                    bean.set("codesetid", "");// 列头代码
                    bean.set("decwidth", "0");// 列小数点后面位数
                    bean.set("colType", "A");// 该列数据类型
                    HashMap headStyleMap = new HashMap();
                    headStyleMap.put("columnWidth", 7000);
                    headStyleMap.put("isFontBold", true);
                    bean.set("headStyleMap", headStyleMap);// 该列宽度
                    headList.add(bean);
                    continue;
                }
                if("num".equalsIgnoreCase(str)){
                    bean.set("content", itemText.get(i));// 列头名称
                    bean.set("itemid", str);// 列头代码
                    bean.set("codesetid", "");// 列头代码
                    bean.set("decwidth", "0");// 列小数点后面位数
                    bean.set("colType", "N");// 该列数据类型
                    headList.add(bean);
                    continue;
                }

                FieldItem item=DataDictionary.getFieldItem(str);
                String itemtype =item.getItemtype() ;
                String decwidth = String.valueOf(item.getDecimalwidth());
                bean.set("content", itemText.get(i));// 列头名称
                bean.set("itemid", str);// 列头代码
                bean.set("codesetid", "");// 列头代码
                bean.set("decwidth", decwidth);// 列小数点后面位数
                bean.set("colType", itemtype);// 该列数据类型
                if("a00z2".equalsIgnoreCase(str))
                    bean.set("dateFormat", "yyyy-MM");
                headList.add(bean);
            }
            ArrayList newList=new ArrayList();
            for(LazyDynaBean bean:dataList){

                ArrayList list=new ArrayList();
                for(String str :itemidList){
                    String strId=str.split(":")[0];
                    list.add(bean.get(strId));

                }
                newList.add(list);
            }

            ExportExcelUtil excelUtil = new ExportExcelUtil(this.conn,this.userview);
            //excelUtil.setProtect(true);//是否锁定excel页面
            if(!"1".equals(imodule))
                fileName = dateString+"工资汇总表_"+this.userview.getUserName() + ".xls";
            else
                fileName = dateString+"保险汇总表_"+this.userview.getUserName() + ".xls";
            // 导出excel
            excelUtil.exportExcel(fileName, null, headList, newList, null, 0);
            excelUtil.exportExcel(fileName);

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return fileName;

    }


    /**
     * @author lis
     * @Description: 得到审批薪资数据sql
     * @date 2016-1-6
     * @param accountingdate 发放日期
     * @param accountingcount 发放次数
     * @param gzbo
     * @param need_rownum 是否需要序号，薪资导出模板和导入都完全和序号没关系，不需要序号//2019-05-20新增参数，涉及文件：ExpDataTrans.java/ExpTemplateTrans.java/GetTemplDataTrans.java
     * @return
     * @throws GeneralException
     */
    public String getSpSql(String accountingdate,String accountingcount,String cound,SalaryTemplateBo gzbo,SalarySetBo setbo, boolean need_rownum) throws GeneralException{
        StringBuffer buf=new StringBuffer();
        try {
            ArrayList fieldlist=setbo.searchGzItem();
            String noHave = ",sp_flag2,";//由于取的是薪资项目字段，sp_flag2这样的字段不会出现在历史表中，故去掉 zhaoxg add 2016-12-5
            StringBuffer sqlstr = new StringBuffer();
            for(int i=0;i<fieldlist.size();i++){
                FieldItem item = (FieldItem) fieldlist.get(i);
                if(noHave.indexOf(item.getItemid().toLowerCase())!=-1){
                    continue;
                }
                sqlstr.append(",");
                sqlstr.append(item.getItemid().toLowerCase());
            }


            StringBuffer strSql=new StringBuffer();
            buf.append("select dbid,"+ sqlstr.substring(1) +" from salaryhistory");//此处加上dbid  否则过滤的时候表格工具会报错
            buf.append(" where 1=1 ");
            if(cound!=null&&cound.length()>0&&!"all".equalsIgnoreCase(cound)){
                buf.append(" and UserFlag='"+cound+"'");
            }
            buf.append(this.getSpExportWhere(accountingdate, accountingcount, gzbo));
            
            String order_by = " order by  dbid,a0000, A00Z0, A00Z1";
            if(need_rownum) {
	            if(Sql_switcher.searchDbServer()==2) {//是oracle
	                strSql.append(" select rownum,fff.*  from (");
	                strSql.append(buf);
	                strSql.append(order_by + ") fff");
	            }else {
	                strSql.append(" select  ROW_NUMBER() OVER(order by dbid,a0000, A00Z0, A00Z1) as rownum,fff.*  from (");
	                strSql.append(buf).append(") fff ");
	                strSql.append(order_by);
	            }
            }else {
            	strSql.append(buf);
                strSql.append(order_by);
            }

            buf=strSql;
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return buf.toString();
    }

    /**
     * @author lis
     * @Description: 获取薪资审批导出查询数据where条件
     * @date 2016-1-7
     * @param accountingdate
     * @param accountingcount
     * @param gzbo
     * @return
     * @throws GeneralException
     */
    public String getSpExportWhere(String accountingdate,String accountingcount,SalaryTemplateBo gzbo) throws GeneralException{
        StringBuffer buf=new StringBuffer();
        try {
            String privWhlStr = gzbo.getWhlByUnits("salaryhistory",true);
            buf.append(" and ((((AppUser is null  "+privWhlStr+" ) or AppUser Like '%;"+this.userview.getUserName()+";%' ) and (sp_flag='06' or sp_flag='03' or sp_flag='02')) or curr_user='"+this.userview.getUserName()+"')");
            if(StringUtils.isNotBlank(accountingdate) && StringUtils.isNotBlank(accountingcount))
                buf.append(" and salaryid="+salaryid+" and a00z2="+Sql_switcher.dateValue(accountingdate)+" and a00z3="+accountingcount+"");
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return buf.toString();
    }

    /**
     * @author lis
     * @Description: 获取薪资审批导出查询数据where条件
     * @date 2016-1-7
     * @param accountingdate
     * @param accountingcount
     * @param gzbo
     * @return
     * @throws GeneralException
     */
    public String getSpUpdateWhere(String accountingdate,String accountingcount,SalaryTemplateBo gzbo,String gz_module) throws GeneralException{
        StringBuffer buf=new StringBuffer();
        try {
            String privWhlStr = gzbo.getWhlByUnits("salaryhistory",true);
            buf.append(" and salaryhistory.salaryid=" + this.salaryid );
            buf.append(" and (((salaryhistory.sp_flag='02' or salaryhistory.sp_flag='07') and salaryhistory.curr_user='"+this.userview.getUserName()+"')");
            if (gzbo.isAllowEditSubdata_Sp(gz_module)){  //原来状态是sp_flag='06' or  sp_flag='03'， 应该只有06才对 wangrd 2013-11-15
                buf.append(" or (((salaryhistory.AppUser is null  "
                        +privWhlStr+") or salaryhistory.AppUser Like '%;"+this.userview.getUserName()
                        +";%') and (salaryhistory.sp_flag='06')) ");
            }
            buf.append(")");
            if(StringUtils.isNotBlank(accountingdate) && StringUtils.isNotBlank(accountingcount))
                buf.append(" and salaryhistory.a00z2="+Sql_switcher.dateValue(accountingdate)+" and salaryhistory.a00z3="+accountingcount+"");
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return buf.toString();
    }

    /**
     * 取得导入文件中列指标列表
     * @param form_file
     * @author dengcan
     * @return ArrayList<Map>
     * @throws FileNotFoundException
     */
    public ArrayList<LazyDynaBean> getOriginalDataFiledList(InputStream stream,String id,ArrayList salarylist)
            throws GeneralException, FileNotFoundException {
        ArrayList<LazyDynaBean> list = new ArrayList<LazyDynaBean>();
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        SalaryTemplateBo gzbo=new SalaryTemplateBo(this.conn,this.userview);
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet frowset = null;
        try {
            HashMap<String, String> oppositeItem = new HashMap<String, String>();
            HashMap<String, String> relationItem = new HashMap<String, String>();
            String onlyName="";
            if(StringUtils.isNotBlank(id)){//是读取方案
                String context = "";
                ArrayList listdata = new ArrayList();
                listdata.add(id);
                frowset = dao.search("select * from gz_relation where id=?",listdata);
                if (frowset.next())
                    context = Sql_switcher.readMemo(frowset, "rel");

                String[] temps = context.split("\\|");
                //获取当前薪资类别表中的所有字段，如果默认方案中存在已经删除的指标，会导致导入报错
                ArrayList listField = gzbo.searchItemList(salaryid);
                if (temps.length > 0)
                {
                	StringBuffer itemdesc = new StringBuffer();
                    String[] oppositeItemArr = temps[0].split("\\,");
                    for (int i = 0; i < oppositeItemArr.length; i++)
                    {
                        String oppositeItemTemp = oppositeItemArr[i];
                        if (StringUtils.isNotBlank(oppositeItemTemp)){
                        	oppositeItemTemp = oppositeItemTemp.trim().replaceAll("＝", "=");
                        	oppositeItemTemp = oppositeItemTemp.trim().replaceAll("=", ":");
                        	//如果描述就是以都好分隔，那就有问题了，这里先判断每个分隔的结尾是不是:XXXX结尾的，如果不是，说明就描述中包含了逗号，这里拼接
                        	if(oppositeItemTemp.length() >= 6) {
                        		String res = oppositeItemTemp.substring(oppositeItemTemp.length()-6, oppositeItemTemp.length());
                        		if(!res.matches(":[a-zA-Z0-9]{5}")) {
                        			itemdesc.append(oppositeItemTemp+",");
                        			continue;
                        		}
                        	}else {
                        		itemdesc.append(oppositeItemTemp+",");
                        	}
                        	// 针对描述中有冒号的，找到最后一个冒号，进行截取
                        	int lastIndexOfFieldItem = oppositeItemTemp.lastIndexOf(":");
                        	String itemid = oppositeItemTemp.substring(lastIndexOfFieldItem+1);
                        	if(!listField.contains(itemid))
                        		continue;
                            oppositeItem.put(itemdesc.toString() + oppositeItemTemp.substring(0, lastIndexOfFieldItem),itemid);
                            itemdesc = new StringBuffer();

                        }
                    }
                }

                if (temps.length > 1)
                {
                	StringBuffer itemdesc = new StringBuffer();
                    String[] relationItemArr = temps[1].split("\\,");
                    for (int i = 0; i < relationItemArr.length; i++)
                    {
                        String relationItemTemp = relationItemArr[i];
                        if (StringUtils.isNotBlank(relationItemTemp))
                        {
                        	relationItemTemp = relationItemTemp.trim().replaceAll("＝", "=");
                        	relationItemTemp = relationItemTemp.trim().replaceAll("=", ":");
                        	//如果描述就是以都好分隔，那就有问题了，这里先判断每个分隔的结尾是不是:XXXX结尾的，如果不是，说明就描述中包含了逗号，这里拼接
                        	if(relationItemTemp.length() >= 6) {
                        		String res = relationItemTemp.substring(relationItemTemp.length()-6, relationItemTemp.length());
                        		if(!res.matches(":[a-zA-Z0-9]{5}")) {
                        			itemdesc.append(relationItemTemp+",");
                        			continue;
                        		}
                        	}else {
                        		itemdesc.append(relationItemTemp+",");
                        	}
                        	
                        	int lastIndexOfFieldItem = relationItemTemp.lastIndexOf(":");
                        	String itemid = relationItemTemp.substring(lastIndexOfFieldItem+1);
                        	if(!listField.contains(itemid))
                        		continue;
                            relationItem.put(itemdesc.toString() + relationItemTemp.substring(0, lastIndexOfFieldItem),itemid);
                            if("onlyName".equalsIgnoreCase(relationItemTemp.split(":")[0]))
                                onlyName=itemid;
                            itemdesc = new StringBuffer();
                        }
                    }
                }
            }

            GzExcelBo gebo = new GzExcelBo(this.conn);
            gebo.getSelfAttribute(stream);
            ArrayList<CommonData> list2 = gebo.getRowAllInfo(0);// 得到excel表格第一行数据，即表头
            CommonData commonData = null;
            LazyDynaBean bean = null;
            if(list2.size()==0){
                throw GeneralExceptionHandler.Handle(new Exception("导入文件格式有误，第一行应为列名称！"));
            }

            boolean isHaveonlyName=false;
            for (int i = 0; i < list2.size(); i++) {
                LazyDynaBean tempBean=null;
                hashMap = new HashMap();
                bean = new LazyDynaBean();
                commonData = list2.get(i);
                if (commonData != null) {
                    bean.set("itemid", commonData.getDataValue());
                    bean.set("itemdesc", commonData.getDataName());
                    for(int j=0;j<salarylist.size();j++){
                        LazyDynaBean typebean = (LazyDynaBean) salarylist.get(j);
                        if(commonData.getDataValue().equals(typebean.get("itemdesc"))){
                            tempBean=typebean;
                            if("N".equalsIgnoreCase((String) typebean.get("itemtype"))){//数值型居右
                                bean.set("align", "right");
                                break;
                            }else{
                                bean.set("align", "left");
                            }
                        }
                    }
                    if(oppositeItem.containsKey(commonData.getDataValue()))
                        bean.set("itemid1", oppositeItem.get(commonData.getDataValue()));
                    else if(StringUtils.isBlank(id)&&tempBean!=null){//若不存在方案，并且excel文件中列名和系统中相同，则默认添加到对应项中。

                        bean.set("itemid1", ((String)tempBean.get("itemid")).toUpperCase());
                    }
                    else
                        bean.set("itemid1", "");
                    if(relationItem.containsKey(commonData.getDataValue()))
                        bean.set("itemid2", relationItem.get(commonData.getDataValue()));
                    else
                        bean.set("itemid2", "");

                    if(!"".equals(onlyName.trim())&&onlyName.equalsIgnoreCase(commonData.getDataValue()))
                        isHaveonlyName=true;
                }
                list.add(bean);
            }

            if(!"".equals(onlyName.trim())&&isHaveonlyName){
                bean = new LazyDynaBean();
                bean.set("itemid", "onlyName");
                bean.set("itemdesc", onlyName);
                bean.set("itemid1", "");
                bean.set("itemid2", "");
                list.add(bean);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeIoResource(stream);
        }
        return list;
    }

    /**
     * @Title: getOriginalDataFiledList2
     * @Description: TODO(取得导入文件中列指标列表)
     * @param file
     *            上传的文件
     * @throws GeneralException
     * @throws FileNotFoundException
     * @author lis
     * @date 2015-7-15 下午05:28:38
     */
    public ArrayList<CommonData> getOriginalDataFiledList2(InputStream stream)
            throws GeneralException, FileNotFoundException {
        ArrayList<CommonData> list = new ArrayList<CommonData>();
        try {
            GzExcelBo gebo = new GzExcelBo(this.conn);
            gebo.getSelfAttribute(stream);
            list = gebo.getRowAllInfo(0);// 得到excel中第一行数据
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeIoResource(stream);
        }
        return list;
    }

    /**
     * 得到源数据 中的 同号数据
     *
     * @param form_file
     *            源数据文件
     * @param relationItem
     *            关联指标
     * @param originalDataList
     *            源数据表头列信息列表
     * @author dengcan
     * @return ArrayList<LazyDynaBean>
     */
    public ArrayList<LazyDynaBean> getRepeatDataList(InputStream stream, ArrayList<String> relationItem
    		, ArrayList originalHeadList,String tableName)
            throws GeneralException {
        ArrayList list = new ArrayList();
        RowSet rowSet = null;
        try {
            insertTempData(stream, relationItem, originalHeadList,tableName);
            ContentDAO dao = new ContentDAO(this.conn);
            String select_str = "";
            String group_str = "";
            for (int j = 0; j < relationItem.size(); j++) {
                select_str += ",a" + j;
                group_str += ",a" + j;
            }
            String sql = "select " + select_str.substring(1)
                    + ",count(*) acount from "+tableName+" group by "
                    + group_str.substring(1) + " having count(*)>1";
            rowSet = dao.search(sql);
            while (rowSet.next()) {
                LazyDynaBean abean = new LazyDynaBean();
                for (int j = 0; j < relationItem.size(); j++) {
                    String temp = relationItem.get(j);
                    String[] temps = temp.split("=");
                    FieldItem fielditem = DataDictionary.getFieldItem(temps[1]
                            .trim().toLowerCase());
                    String value = "";
                    if (rowSet.getString("a" + j) != null) {
                        value = rowSet.getString("a" + j);
                    }
                    abean.set("a" + j, value);
                }
                abean.set("acount", rowSet.getString("acount"));
                list.add(abean);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeResource(rowSet);
        }
        return list;
    }

    /**
     * @Title: insertTempData
     * @Description: TODO( 将导入数据插入临时表中)
     * @param form_file
     *            源文件
     * @param relationItem
     *            关联关系
     * @param originalHeadList
     *            文件表格头部列表
     * @author lis
     * @throws GeneralException
     * @date 2015-7-17 下午05:49:08
     */
    private void insertTempData(InputStream stream, ArrayList<String> relationItem,
                                ArrayList originalHeadList,String tableName) throws GeneralException {
        try {
            DbWizard dbWizard = new DbWizard(this.conn);
            DBMetaModel dbmodel = new DBMetaModel(this.conn);
            ContentDAO dao = new ContentDAO(this.conn);
            GzExcelBo gzExbo = new GzExcelBo(this.conn);
            gzExbo.getSelfAttribute(stream);

            int rowNums = gzExbo.getTotalDataRows2(); // 数据总行数
            ArrayList dataList = gzExbo.getDefineData(1, rowNums,
                    originalHeadList);// 取得从1到rowNums的数据


            Table table = new Table(tableName);
            if (dbWizard.isExistTable(tableName, false))
                dbWizard.dropTable(tableName);
            // 生成数据库表的列名
            for (int i = 0; i < relationItem.size(); i++) {

                Field atemp = new Field("a" + i, "a" + i);
                atemp.setDatatype(DataType.STRING);
                atemp.setLength(200);
                table.addField(atemp);
            }
            dbWizard.createTable(table);// 创建数据库表
            dbmodel.reloadTableModel(tableName);
            ArrayList data_list = new ArrayList();
            LazyDynaBean abean = null;
            RecordVo vo = null;

            // 生成数据库表数据
            for (int i = 0; i < dataList.size(); i++) {
                vo = new RecordVo(tableName);
                abean = (LazyDynaBean) dataList.get(i);
                int temp1 = 0;
                for (int j = 0; j < relationItem.size(); j++) {
                    String temp = relationItem.get(j);
                    String[] temps = temp.split("=");
                    vo.setString("a" + j, (String) abean.get(temps[0].trim()));
                    // 判断excel中该行数据是不是全部未空
                    if (abean.get(temps[0]) == null
                            || "".equals(abean.get(temps[0].trim()))) {
                        temp1++;
                    }
                }
                if (temp1 != relationItem.size())// excel中该行数据不全为空时
                    data_list.add(vo);
            }
            dao.addValueObject(data_list);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeIoResource(stream);
        }
    }

    /**
     * @Title: getReadRelations
     * @Description: TODO(读取方案数据集合)
     * @author lis
     * @throws GeneralException
     * @date 2015-7-15 下午05:19:03
     */
    public ArrayList<LazyDynaBean> getReadRelations() throws GeneralException {
        ArrayList<LazyDynaBean> schemeList = new ArrayList<LazyDynaBean>();
        RowSet rowSet = null;
        try {
            DbWizard dbWizard = new DbWizard(this.conn);
            LazyDynaBean bean = null;
            if (dbWizard.isExistTable("gz_relation", false)) {
                ContentDAO dao = new ContentDAO(this.conn);
                Table table = new Table("gz_relation");

                if (!dbWizard.isExistField("gz_relation", "salaryid",false)) {
                    Field temp4 = new Field("salaryid", "薪资类别号");//薪资类别号
                    temp4.setDatatype(DataType.INT);
                    temp4.setNullable(true);
                    temp4.setKeyable(false);
                    table.addField(temp4);
                    dbWizard.addColumns(table);// 更新列
                    DBMetaModel dbmodel = new DBMetaModel(this.conn);
                    dbmodel.reloadTableModel("gz_relation");
                }

                if (!dbWizard.isExistField("gz_relation", "userflag",false)) {
                    Field temp4 = new Field("userflag", "用户名");//用户名
                    temp4.setDatatype(DataType.STRING);
                    temp4.setLength(50);
                    temp4.setNullable(true);
                    temp4.setKeyable(false);
                    table.addField(temp4);
                    dbWizard.addColumns(table);// 更新列
                    DBMetaModel dbmodel = new DBMetaModel(this.conn);
                    dbmodel.reloadTableModel("gz_relation");
                }
                if (!dbWizard.isExistField("gz_relation", "seq",false)) {
                    Field temp4 = new Field("seq", "排序号");//排序号
                    temp4.setDatatype(DataType.INT);
                    temp4.setNullable(true);
                    temp4.setKeyable(false);
                    table.addField(temp4);
                    dbWizard.addColumns(table);// 更新列
                    DBMetaModel dbmodel = new DBMetaModel(this.conn);
                    dbmodel.reloadTableModel("gz_relation");
                }
                ArrayList list = new ArrayList();
                list.add(this.salaryid);
                list.add(this.userview.getUserName());
                String str="select * from gz_relation where "+Sql_switcher.isnull("salaryid", "'"+this.salaryid+"'")+"=? and "+Sql_switcher.isnull("userflag", "'"+this.userview.getUserName()+"'")+"=? order by id";
                rowSet = dao.search(str,list);
                while (rowSet.next()) {
                    if(rowSet.getString("name")==null)
                        continue;
                    String temp = rowSet.getString("name").replaceAll("\\\\",
                            "\\\\\\\\");// 由于java里面反斜杠是特殊的，把\转换成\\前台即可正常显示
                    // zhaoxg 2013-6-25
                    bean = new LazyDynaBean();
                    bean.set("id", rowSet.getString("id"));
                    bean.set("name", rowSet.getString("id") + "." + temp);
                    bean.set("seq", rowSet.getString("seq"));
                    schemeList.add(bean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeResource(rowSet);
        }
        return schemeList;
    }

    /**
     * @Description:取得 薪资类别中的薪资项目列表
     * @author lis
     * @date 2015-7-3
     * @param salaryItemList
     *            薪资项目列表
     * @throws GeneralException
     */
    public ArrayList<LazyDynaBean> getAimDataFieldList(ArrayList salaryItemList,String type)
            throws GeneralException {
        ArrayList<LazyDynaBean> list = new ArrayList();
        try {
            LazyDynaBean bean = null;
            bean = new LazyDynaBean();
            bean.set("itemid"+type, "blank");
            bean.set("itemdesc"+type,"（空）");
            list.add(bean);
//			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
//			String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","valid");//是否定义唯一性指标 0：没定义
//			String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");//唯一性指标值
//			if(!uniquenessvalid.equals("0")){
//				FieldItem fm=DataDictionary.getFieldItem(onlyname);
//				bean = new LazyDynaBean();
//				bean.set("itemid"+type, "onlyname/"+onlyname);
//				bean.set("itemdesc"+type,fm.getItemid().toUpperCase()+"："+fm.getItemdesc()+"（唯一性指标）");
//				list.add(bean);
//			}
            for (int i = 0; i < salaryItemList.size(); i++) {
                LazyDynaBean abean = (LazyDynaBean) salaryItemList.get(i);
                String itemid = (String) abean.get("itemid");
                String flag = (String) abean.get("initflag");
                /*
                 * A0000：顺序字段，A0100：人员编号，NBASE：人员库标识,这三个不显示
                 * 如果是超户则全部显示，如果不是，系统项全部显示，不是系统项的则要看当前用户有没有该权限
                 */
                if ("a0100".equalsIgnoreCase(itemid) || "a00z2".equalsIgnoreCase(itemid) || "a00z3".equalsIgnoreCase(itemid)
                        || "a0000".equalsIgnoreCase(itemid) || "b0110".equalsIgnoreCase(itemid)
                        || "NBASE".equals(itemid) || "e0122".equalsIgnoreCase(itemid))
                    continue;
                if (!this.userview.isSuper_admin()) {
                    // 0：输入项,1：累积项, 2：导入, 3：系统项
                    if (!"3".equals(flag)) {
                        // 当前用户是否拥有该薪资项目的权限
                        if ("0"
                                .equalsIgnoreCase(this.userview.analyseFieldPriv(itemid)))
                            continue;
                    }
                }
                String itemdesc = (String) abean.get("itemdesc");
                String itemtype = (String) abean.get("itemtype");
                String typeDesc = ResourceFactory.getProperty("kq.formula.character");//字符
                if ("N".equalsIgnoreCase(itemtype))
                    typeDesc = ResourceFactory.getProperty("kq.formula.counts");//数值
                if ("D".equalsIgnoreCase(itemtype))
                    typeDesc = ResourceFactory.getProperty("kq.wizard.riqi");//日期

                bean = new LazyDynaBean();
                bean.set("itemid"+type, itemid);
                bean.set("itemdesc"+type, itemid + "：" + itemdesc + " ( " + typeDesc + " )");
                list.add(bean);
            }

            SalaryTemplateBo gzbo=new SalaryTemplateBo(this.conn,salaryid,this.userview);
            int schemeId = gzbo.getSchemeId("salary_"+salaryid);
			//headItemList = inOutBo.getHeadItemList(itemSetList, column);
			// 从数据库中得到可以显示的薪资项目代码
			if(schemeId > 0){
				ArrayList<HashMap> itemIdList = gzbo.getTableItemsToMap(schemeId,"");
				list = getSchemedHeadHashMap(list, itemIdList, type);
			}
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return list;
    }
    
    /**
	 * 通过栏目设置生成的map 进行排序
	 */
	private ArrayList<LazyDynaBean> getSchemedHeadHashMap(ArrayList<LazyDynaBean> itemSetList,ArrayList<HashMap> itemIdList, String type) throws GeneralException {
		ArrayList<LazyDynaBean> itemList = new ArrayList<LazyDynaBean>();
		try {
			for(HashMap map :itemIdList){
				int i=0;
				String itemid=(String)map.get("itemid");
				while(i<itemSetList.size()){
					LazyDynaBean bean=itemSetList.get(i);
					if(itemid.equalsIgnoreCase((String)bean.get("itemid" + type))){
						itemList.add(bean);
						break;
					}
					i++;
				}
				if(i!=itemSetList.size())
					itemSetList.remove(i);
				
			}
			itemList.addAll(itemSetList);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return itemList;
	}

    /**
     * @Description：根据关联指标得到excel有的但是数据库中没有的
     * @author lis
     * @date 2015-7-9
     * @param form_file
     *            上传文件
     * @param relationItem
     *            关联关系
     * @param originalDataList
     *            excel表头
     * @return
     * @throws GeneralException
     */
    public ArrayList<LazyDynaBean> getOriDataList(InputStream inputStream, ArrayList<String> relationItem
    		, ArrayList<CommonData> originalDataList) throws GeneralException {
        ArrayList<LazyDynaBean> list = new ArrayList<LazyDynaBean>();
        try {
            GzExcelBo gzExbo = new GzExcelBo(this.conn);
            gzExbo.getSelfAttribute(inputStream);

            int rowNums = gzExbo.getTotalDataRows2(); // 数据总行数
            int pageNum = rowNums / 100 + 1;
            LazyDynaBean abean = null;
            for (int i = 1; i <= pageNum; i++) {
                int from = (i - 1) * 100 + 1;
                int to = i * 100;
                if (to > rowNums)
                    to = rowNums;
                /* 从excel中取得指定的从from行至to行的数据 */
                ArrayList dataList = gzExbo.getDefineData(from, to,
                        originalDataList);

                /* 根据关联关系取得 源数据存在于数据库表中的数据 */
                HashMap existDataMap = getExistDataMap(dataList, relationItem,
                        this.getSalaryItemMap(),originalDataList);

                ArrayList<String> ori_item = new ArrayList();
                for (int j = 0; j < relationItem.size(); j++) {
                    String temp = relationItem.get(j);
                    String[] temps = temp.split(":");
                    ori_item.add(temps[0].trim());
                }

                // 排除excel中已存在数据库中的数据
                StringBuffer temp = new StringBuffer("");
                for (int j = 0; j < dataList.size(); j++) {
                    abean = (LazyDynaBean) dataList.get(j);
                    temp.setLength(0);
                    for (int e = 0; e < ori_item.size(); e++)
                        temp.append("/" + (String) abean.get(ori_item.get(e)));
                    if (existDataMap.get(temp.toString()) == null)
                        list.add(abean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return list;
    }

    /**
     * @Title: getSalaryItemMap
     * @Description: TODO(取得薪资类别项目的数据类型 map，方便判断是否存在)
     * @author lis
     * @throws GeneralException
     * @date 2015-7-15 下午05:35:10
     */
    private HashMap getSalaryItemMap() throws GeneralException {
        HashMap map = new HashMap();
        try {
            SalaryTemplateBo gzbo = new SalaryTemplateBo(conn, this.userview);
            ArrayList list = gzbo.getSalaryItemList("", this.salaryid + "",1);
            for (int i = 0; i < list.size(); i++) {
                LazyDynaBean abean = (LazyDynaBean) list.get(i);
                map.put(abean.get("itemid"), abean);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return map;
    }

    /**
     * 获取按方案导入时需要的代码map
     * @param codeSetIds
     * @return
     * @throws GeneralException
     */
    private HashMap<String ,HashMap<String,String>> getCodeItemMap(ArrayList<String> codeSetIds) throws GeneralException {
        HashMap<String ,HashMap<String,String>> codeMap = new HashMap<String ,HashMap<String,String>>();
        try {
            for (String codeSetId : codeSetIds) {
                HashMap<String,String> map=new HashMap<String, String>();

                if(AdminCode.getCodeItemList(codeSetId)==null){
                    continue;
                }
                ArrayList<CodeItem> list=(ArrayList<CodeItem>) AdminCode.getCodeItemList(codeSetId).clone();
                for (CodeItem codeItem : list) {
                	//获取名称和代码的map和代码和代码的map，这样根据名称找到对应的代码插入
                    map.put(codeItem.getCodename(),codeItem.getCodeitem());
                    map.put(codeItem.getCodeitem(),codeItem.getCodeitem());
                }
                codeMap.put(codeSetId,map);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return codeMap;
    }

    /**
     * 根据代码类id获取其中设置了"仅可选择叶子节点"的代码类叶子节点
     * @param codeList
     * @return
     * @throws GeneralException
     */
    public HashMap<String,HashMap<String,String>> getLeafNodeMap(ArrayList<String> codeList) throws GeneralException {
        RowSet rs=null,rowSet=null;
        HashMap<String,HashMap<String,String>> leafCodeColMap=new HashMap<String, HashMap<String, String>>();
        try{


            ContentDAO dao=new ContentDAO(this.conn);
            StringBuffer sql=new StringBuffer();
            StringBuffer codeBuf=new StringBuffer();

            for (int i = 0; i < codeList.size(); i++) {
                codeBuf.append("?,");
            }
            codeBuf.deleteCharAt(codeBuf.length()-1);

            sql.append("SELECT codesetid FROM codeset where codesetid in (").append(codeBuf).append(" )");
            sql.append(" and ");
            sql.append(Sql_switcher.isnull("leaf_node", "0")).append("=1 ");
            rs=dao.search(sql.toString(),codeList);

            sql.setLength(0);
            sql.append(" SELECT codeitemid,codeitemdesc FROM codeitem WHERE codesetid=? ");
            sql.append(" AND ").append(Sql_switcher.sqlNow()).append(" BETWEEN start_date AND end_date ");
            sql.append(" AND codeitemid NOT IN ( SELECT parentid FROM codeitem WHERE codesetid=? AND parentid<>codeitemid ");
            sql.append(" AND ").append(Sql_switcher.sqlNow()).append(" BETWEEN start_date AND end_date )");

            while(rs.next()){
                codeList.clear();
                String codesetid=rs.getString("codesetid");
                codeList.add(codesetid);
                codeList.add(codesetid);
                rowSet=dao.search(sql.toString(),codeList);
                while (rowSet.next()){
                    if(leafCodeColMap.containsKey(codesetid)) {
                        leafCodeColMap.get(codesetid).put(rowSet.getString("codeitemdesc"),rowSet.getString("codeitemid"));
                    }else{
                        HashMap<String,String> tempMap=new HashMap<String, String>();
                        tempMap.put(rowSet.getString("codeitemdesc"),rowSet.getString("codeitemid"));
                        leafCodeColMap.put(codesetid,tempMap);
                    }
                }
            }
            // 通过一次查询获取map的方法，由于可读性差，已废弃
//            sql.append("SELECT cm.codesetid,cm.codeitemid,cm.codeitemdesc FROM codeitem cm  inner join codeset on cm.codesetid=codeset.codesetid  LEFT JOIN (");
//            sql.append(" SELECT COUNT(1) AS num ,codesetid,parentid FROM codeitem WHERE  codeitemid<>parentid AND ");
//            sql.append(Sql_switcher.sqlNow()).append(" BETWEEN start_date AND end_date  and upper(codesetid) in (");
//            sql.append(codeBuf.toString()).append(") GROUP BY parentid,codesetid ");
//            sql.append(") cnum ON cm.codesetid =cnum.codesetid AND cm.codeitemid=cnum.parentid ");
//            sql.append(" WHERE ").append(Sql_switcher.isnull("cnum.num", "0"));
//            sql.append("=0 and upper(cm.codesetid) in (").append(codeBuf.toString()).append(") and ");
//            sql.append(Sql_switcher.isnull("leaf_node", "''")).append("=1 AND ");
//            sql.append(Sql_switcher.sqlNow()).append(" BETWEEN cm.start_date AND cm.end_date");

        }catch (Exception e){
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally {
            PubFunc.closeDbObj(rowSet);
            PubFunc.closeDbObj(rs);
        }
        return  leafCodeColMap;
    }

    /**
     * @Title: getExistDataMap
     * @Description: TODO(根据关联关系取得 源数据存在于数据库表中的数据 map)
     * @param dataList
     *            excel中数据
     * @param relationItem
     *            关联关系
     * @param salaryItemTypeMap
     *            薪资项目
     * @throws GeneralException
     * @author lis
     * @date 2015-7-17 下午03:53:36
     */
    private HashMap getExistDataMap(ArrayList<LazyDynaBean> dataList,
                                    ArrayList<String> relationItem, HashMap salaryItemTypeMap,ArrayList<CommonData> originalDataList)
            throws GeneralException {
        HashMap map = new HashMap();
        RowSet rowSet = null;
        try {
            ArrayList list = new ArrayList();
            SalaryTemplateBo gzbo = new SalaryTemplateBo(this.conn,
                    this.salaryid, this.userview);
            ContentDAO dao = new ContentDAO(this.conn);
            ArrayList<String> ori_item = new ArrayList<String>();// 源数据指标集合(汉字)
            ArrayList<String> aim_item = new ArrayList<String>();// 数据库指标集合(代码)

            StringBuffer select_str = new StringBuffer("");

            for (int i = 0; i < relationItem.size(); i++) {
                String temp = (String) relationItem.get(i);
                /*
                 * 薪资发放：新建/导入：设置对应指标页面，点击【保存对应】、【没对应数据】、设置方案后点击【确定】都保存报错
                 * ArrayIndexOutException xiaoyun 2014-9-19 start
                 */
                temp = PubFunc.keyWord_reback(temp);
                /*
                 * 薪资发放：新建/导入：设置对应指标页面，点击【保存对应】、【没对应数据】、设置方案后点击【确定】都保存报错
                 * ArrayIndexOutException xiaoyun 2014-9-19 end
                 */
                ori_item.add(temp.substring(0,temp.lastIndexOf(":")).trim());
                aim_item.add(temp.substring(temp.lastIndexOf(":")+1).trim());
                select_str.append("," + temp.substring(temp.lastIndexOf(":")+1).trim());
            }
            StringBuffer whl = new StringBuffer("");
            LazyDynaBean abean = null;
            for (int i = 0; i < dataList.size(); i++) {
                abean = dataList.get(i);
                StringBuffer temp_whl = new StringBuffer("");
                //去除空行
                boolean isValid=false;
                for(CommonData tempdata:originalDataList){
                    Object t=abean.get(tempdata.getDataName());
                    if(t!=null&&StringUtils.isNotBlank(t.toString())){
                        isValid=true;
                        break;
                    }
                }
                if(!isValid)
                    continue;

                for (int j = 0; j < relationItem.size(); j++) {
                    LazyDynaBean columnBean = (LazyDynaBean) salaryItemTypeMap
                            .get(((String) aim_item.get(j)).trim());
                    String decwidth = (String) columnBean.get("decwidth");
                    String itemtype = (String) columnBean.get("itemtype");
                    if ("A".equals(itemtype)) {
                        temp_whl.append(" and " + aim_item.get(j) + "=?");
                        list.add(((String) abean.get(ori_item.get(j))).trim());
                    }
                    if ("D".equals(itemtype)) {
                        temp_whl.append(" and "
                                + Sql_switcher.dateToChar(aim_item.get(j),
                                "YYYY-MM-DD") + "=?");
                        list.add(((String) abean.get(ori_item.get(j))).trim());
                    }
                    if ("N".equals(itemtype)) {
                        String tempValue = ((String) abean.get(ori_item.get(j)))
                                .trim();
                        if (this.isDataType(decwidth, itemtype, tempValue)) {
                            temp_whl.append(" and " + aim_item.get(j) + "=?");
                            list.add(tempValue);
                        } else
                            throw GeneralExceptionHandler.Handle(new Exception(
                                    "源数据(" + ori_item.get(j) + ")中数据:"
                                            + tempValue + " 不符合格式!"));

                    }
                }
                whl.append(" or ( " + temp_whl.substring(4) + " )");
            }

            if (select_str.length() > 1 && whl.length() > 1) {
                String sql = "select " + select_str.substring(1) + " from "
                        + gzbo.getGz_tablename() + " where ( "
                        + whl.substring(3) + " ) ";
                sql += getImportWhere(salaryid);

                rowSet = dao.search(sql, list);
                StringBuffer temp = new StringBuffer("");
                SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
                while (rowSet.next()) {
                    temp.setLength(0);
                    for (int i = 0; i < relationItem.size(); i++) {
                        LazyDynaBean columnBean = (LazyDynaBean) salaryItemTypeMap
                                .get(aim_item.get(i).trim());
                        String decwidth = (String) columnBean.get("decwidth");
                        String itemtype = (String) columnBean.get("itemtype");
                        if ("D".equals(itemtype)) {
                            if (rowSet.getDate(i + 1) != null)
                                temp.append("/"
                                        + sf.format(rowSet.getDate(i + 1)));
                            else
                                temp.append("/");
                        } else
                            temp.append("/" + rowSet.getString(i + 1));
                    }

                    map.put(temp.toString(), "1");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeResource(rowSet);
        }
        return map;
    }

    /**
     * @Title: moveUpDown
     * @Description: 对应方案上下移动
     * @param ori_id 移动对象id
     * @param ori_seq 移动对象序号
     * @param to_id 目标对象id
     * @param to_seq 目标对象序号
     * @param dropPosition 上移或下移
     * @throws SQLException
     * @throws GeneralException
     * @author lis
     * @date 2015-9-1 下午03:04:29
     */
    public void moveUpDown(String ori_id,String ori_seq, String to_id,String to_seq) throws SQLException,
            GeneralException {
        try {
            ContentDAO dao=new ContentDAO(this.conn);
            StringBuffer str = new StringBuffer();
            ArrayList list = new ArrayList();

            String dropPosition = "";
            if(Integer.valueOf(ori_seq) > Integer.valueOf(to_seq))
                dropPosition = "before";
            else if(Integer.valueOf(ori_seq) < Integer.valueOf(to_seq))
                dropPosition = "after";
            else if(Integer.valueOf(ori_id) < Integer.valueOf(to_id))
                dropPosition = "before";
            else if(Integer.valueOf(ori_id) > Integer.valueOf(to_id))
                dropPosition = "after";

            if("before".equals(dropPosition)){//上移
                //将上移对象的seq替换成目标对象的
                str.append("update gz_relation set seq=? where id=?");
                list.add(to_seq);
                list.add(ori_id);
                dao.update(str.toString(),list);
                str.setLength(0);
                list = new ArrayList();
                //在移动对象和目标对象之间的对象seq都加1.
                str.append("update gz_relation set seq = seq+1 where seq>=? and seq<=?  and id<>?");
                list.add(to_seq);
                list.add(ori_seq);
                list.add(ori_id);
                dao.update(str.toString(),list);
            }else if("after".equals(dropPosition)){//下移
                //将下移对象的seq替换成目标对象的
                str.append("update gz_relation set seq =? where id=?");
                list.add(to_seq);
                list.add(ori_id);
                dao.update(str.toString(),list);
                str.setLength(0);
                list = new ArrayList();
                //在移动对象和目标对象之间的对象seq都减1.
                str.append("update gz_relation set seq = seq-1 where seq>=? and seq<=?  and id<>?");
                list.add(ori_seq);
                list.add(to_seq);
                list.add(ori_id);
                dao.update(str.toString(),list);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * @Title: saveRelationScheme
     * @Description: TODO(保存导入数据对应方案)
     * @param name
     *            方案名称
     * @param oppositeItem
     *            对应指标
     * @param relationItem
     *            关联指标
     * @throws GeneralException
     * @author lis
     * @date 2015-7-17 下午06:00:36
     */
    public boolean saveRelationScheme(String name,
                                      ArrayList<String> oppositeItem, ArrayList<String> relationItem)
            throws GeneralException {
        boolean flag = false;
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            Table table = new Table("gz_relation");
            DbWizard dbWizard = new DbWizard(this.conn);
            if (!dbWizard.isExistTable(table.getName(), false))// 判断表是否存在
            {
                table = getGzRelationTable();
                dbWizard.createTable(table);
                DBMetaModel dbmodel = new DBMetaModel(this.conn);
                dbmodel.reloadTableModel("gz_relation");
            } else {

                if (!dbWizard.isExistField("gz_relation", "salaryid",false)) {
                    Field temp4 = new Field("salaryid", "薪资类别号");//薪资类别号
                    temp4.setDatatype(DataType.INT);
                    temp4.setNullable(true);
                    temp4.setKeyable(false);
                    table.addField(temp4);
                    dbWizard.addColumns(table);// 更新列
                    DBMetaModel dbmodel = new DBMetaModel(this.conn);
                    dbmodel.reloadTableModel("gz_relation");
                    table = new Table("gz_relation");
                }

                if (!dbWizard.isExistField("gz_relation", "userflag",false)) {
                    Field temp4 = new Field("userflag", "用户名");//用户名
                    temp4.setDatatype(DataType.STRING);
                    temp4.setLength(50);
                    temp4.setNullable(true);
                    temp4.setKeyable(false);
                    table.addField(temp4);
                    dbWizard.addColumns(table);// 更新列
                }
                DBMetaModel dbmodel = new DBMetaModel(this.conn);
                dbmodel.reloadTableModel("gz_relation");
            }

            StringBuffer rel = new StringBuffer("");
            if (oppositeItem != null) {
                for (int i = 0; i < oppositeItem.size(); i++) {
                    rel.append(oppositeItem.get(i) + ",");
                }
            }
            rel.append("|");
            if (relationItem != null) {
                for (int i = 0; i < relationItem.size(); i++) {
                    rel.append(relationItem.get(i) + ",");
                }
            }

            RecordVo vo = new RecordVo("gz_relation");
            int id = DbNameBo.getPrimaryKey("gz_relation", "id", this.conn); // 取得主键值
            vo.setInt("id", id);
            vo.setString("name", name);
            vo.setString("rel", rel.toString());
            vo.setInt("salaryid", this.salaryid);
            vo.setString("userflag", this.userview.getUserName());
            dao.addValueObject(vo);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return flag;
    }

    /**
     * @Title: getSeq
     * @Description: TODO(取得最大排序号)
     * @return 排序号
     * @author lis
     * @throws GeneralException
     * @date 2015-7-17 下午04:18:26
     */
    private int getSeq() throws GeneralException {
        RowSet rowSet = null;
        int seq = 1;
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            StringBuffer strSql = new StringBuffer();
            strSql.append("select ");
            strSql.append(Sql_switcher.isnull("max(seq)", "0"));
            strSql.append(" from gz_relation ");
            rowSet = dao.search(strSql.toString());
            if (rowSet.next())
                seq = rowSet.getInt(1) + 1;
        } catch (SQLException e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeResource(rowSet);
        }
        return seq;
    }

    /**
     * @Title: getGzRelationTable
     * @Description: TODO(生成方案数据库表模型)
     * @author lis
     * @throws GeneralException
     * @date 2015-7-17 下午04:19:40
     */
    public Table getGzRelationTable() throws GeneralException {
        Table table = new Table("gz_relation");
        try {
            Field temp = new Field("id", ResourceFactory.getProperty("gz_new.gz_relationId"));//方案号
            temp.setNullable(false);
            temp.setKeyable(true);
            temp.setDatatype(DataType.INT);
            temp.setSortable(true);
            table.addField(temp);

            Field temp2 = new Field("name", ResourceFactory.getProperty("gz_new.gz_relationName"));//名称
            temp2.setNullable(true);
            temp2.setKeyable(false);
            temp2.setDatatype(DataType.STRING);
            temp2.setLength(40);
            table.addField(temp2);

            Field temp3 = new Field("rel", ResourceFactory.getProperty("gz_new.gz_relation"));//对应关系
            temp3.setNullable(true);
            temp3.setKeyable(false);
            temp3.setDatatype(DataType.CLOB);
            table.addField(temp3);

            Field temp5 = new Field("salaryid", "薪资类别号");//薪资类别号
            temp5.setDatatype(DataType.INT);
            temp5.setNullable(true);
            temp5.setKeyable(false);
            table.addField(temp5);

            Field temp6 = new Field("userflag", "用户名");//用户名
            temp6.setDatatype(DataType.STRING);
            temp6.setLength(50);
            temp6.setNullable(true);
            temp6.setKeyable(false);
            table.addField(temp6);

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

        return table;
    }

    /**
     * @Title: isDataType
     * @Description: TODO(判断 值类型是否与 要求的类型一致)
     * @param decwidth
     *            值宽度
     * @param itemtype
     *            值类型，日期或数字
     * @param value
     *            值
     * @author lis
     * @throws GeneralException
     * @date 2015-7-17 下午06:02:17
     */
    private boolean isDataType(String decwidth, String itemtype, String value)
            throws GeneralException {
        try {
            boolean flag = true;
            if ("N".equals(itemtype)) {
                flag = value.matches("^[+-]?[\\d]*[.]?[\\d]+");
            } else if ("D".equals(itemtype)) {
                flag = value.matches("[0-9]{4}[#-.][0-9]{2}[#-.][0-9]{2}");
            }
            return flag;
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * @Title: getColumnList
     * @Description: TODO(获取列头、表格渲染)
     * @param originalHeadList
     *            excel源数据的表头
     * @author lis
     * @throws GeneralException
     * @date 2015-7-17 下午06:03:34
     */
    public ArrayList<ColumnsInfo> getColumnList(
            ArrayList<CommonData> originalHeadList) throws GeneralException {
        try {
            ArrayList<ColumnsInfo> columnsInfos = new ArrayList<ColumnsInfo>();
            ColumnsInfo columnsInfo = null;
            for (CommonData commonData : originalHeadList) {
                columnsInfo = this
                        .getColumnsInfo((String) commonData.getDataValue(),
                                (String) commonData.getDataName(), 100);
                columnsInfos.add(columnsInfo);
            }
            return columnsInfos;
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * @Title: getColumnsInfo
     * @Description: TODO( 获得ColumnsInfo对象初始化)
     * @param columnId
     *            列id
     * @param columnDesc
     *            名称
     * @param columnWidth
     *            宽度
     * @author lis
     * @throws GeneralException
     * @date 2015-7-17 下午06:04:42
     */
    private ColumnsInfo getColumnsInfo(String columnId, String columnDesc,
                                       int columnWidth) throws GeneralException {
        try {
            ColumnsInfo columnsInfo = new ColumnsInfo();

            columnsInfo.setColumnId(columnId);
            columnsInfo.setColumnDesc(columnDesc);
            columnsInfo.setCodesetId("");// 指标集
            columnsInfo.setColumnType("M");// 类型N|M|A|D
            columnsInfo.setColumnWidth(columnWidth);// 显示列宽
            columnsInfo.setColumnLength(100);// 显示长度
            columnsInfo.setDecimalWidth(0);// 小数位
            columnsInfo.setAllowBlank(true);// 编辑时是否可以为空
            columnsInfo.setReadOnly(true);// 是否只读
            columnsInfo.setFromDict(false);// 是否从数据字典里来
            columnsInfo.setLocked(false);// 是否锁列

            return columnsInfo;
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * @Title: importFileDataToSalary
     * @Description: TODO(将excel数据导入工资数据表)
     * @param oppositeItem
     *            对应指标关系
     * @param relationItem
     *            关联指标关系
     * @param form_file
     *            导入文件
     * @param gz_table
     *            数据库表名
     * @param updateDateList
     *            要更新的数据
     * @return
     * @throws GeneralException
     * @author lis
     * @date 2015-7-17 下午06:07:04
     */
    public int importFileDataToSalary(ArrayList<String> oppositeItem,
                                      ArrayList<String> relationItem, File form_file, String gz_table,
                                      ArrayList updateDateList) throws GeneralException {
        int rowNum = 0;
        InputStream is = null;
        try {
            ArrayList<String> fieldName=new ArrayList<String>();
            // 生成更新sql语句
//			String tablename=this.creatTempTable(fieldName);
//			String sql="select * From "+tablename;
//			RowSet rs=null;
////			String updateSql = getBatchUpdateSQL(oppositeItem, relationItem,
////					gz_table);
//			ContentDAO dao = new ContentDAO(this.conn);
//			rs=dao.search(sql);
//
//			while(rs.next()){
//				String id=rs.getString("id");
//				System.out.println(id);
//			}
//			if (updateSql.length() > 0) {
//				ArrayList a_updList = new ArrayList();
//				for (int i = 0; i < updateDateList.size(); i++) {
//					if (a_updList.size() < 1000) {
//						a_updList.add((ArrayList) updateDateList.get(i));
//					} else {
//						int[] _rowNums = dao.batchUpdate(updateSql, a_updList);
//						rowNum = rowNum + _rowNums.length;
//						a_updList.clear();
//					}
//				}
//				if (a_updList.size() > 0) {
//					int[] _rowNums = dao.batchUpdate(updateSql, a_updList);
//					rowNum = rowNum + _rowNums.length;
//				}
//			}
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeIoResource(is);
        }
        return rowNum;
    }

    /**========================================新程序====================================================*/
    /**
     * 构建导入临时表
     * @param fieldName 列头数组
     * @return
     * @throws GeneralException
     */
    public String creatTempTable(ArrayList<String> fieldName) throws GeneralException {
        String tableName="";
        try{
            ContentDAO dao = new ContentDAO(this.conn);
            tableName="t#gz_importdata"+this.userview.getUserName();//zhanghua 2017-9-21 由于个别用户名称过长导致oracle无法创建，缩短临时表名
            //删除之前临时表
            if(Sql_switcher.searchDbServer()==2)//是oracle
                dao.update("declare num   number; begin select count(1) into num from user_tables where TABLE_NAME = UPPER('"+tableName+"'); if   num=1   then execute immediate 'drop table "+tableName+"'; end   if; end;");
            else
            {
                dao.update("IF EXISTS (SELECT 1 FROM dbo.SysObjects WHERE ID = object_id('"+tableName+"')) drop table "+tableName);
            }
            String fieldType="";
            if(Sql_switcher.searchDbServer()==2)
                fieldType="varchar2";
            else
                fieldType="varchar";
            StringBuilder strSql=new StringBuilder("Create  table "+tableName+" (");
            for(String str:fieldName){
                str=PubFunc.keyWord_reback(str);
                String item_id=str.split("=")[1];
                int lenght=200;
                if(!item_id.startsWith("f_useless")){
                    if(DataDictionary.getFieldItem(item_id)!=null){
                        FieldItem fieldItem=DataDictionary.getFieldItem(item_id);
                        if("A".equalsIgnoreCase(fieldItem.getItemtype())){
                            lenght=fieldItem.getItemlength()+50;

                        }else if("N".equalsIgnoreCase(fieldItem.getItemtype())){
                            lenght=fieldItem.getItemlength()+10;

                        }else if("D".equalsIgnoreCase(fieldItem.getItemtype())){
                            lenght=fieldItem.getItemlength()+10;
                        }
                    }
                }
                strSql.append(str.split("=")[2]+" "+fieldType+"("+lenght+"),");
            }
            strSql.append("updateflag  "+fieldType+"(200) ,occtime int , errmsg "+fieldType+"(2000)");
            //updateflag 更新标识  occtime 唯一性指标出现次数 errmsg错误信息
            strSql.append(")");

            dao.update(strSql.toString());
        }catch(Exception e){
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return tableName;
    }

    /**
     * @Title: setListData
     * @Description: TODO(检验excel中数据) 并插入错误标记
     * @param items 对应指标或关联关系指标
     * @param abean 当前数据对象
     * @param salaryItemTypeMap 薪资项目map
     * @param a_dataList 可以导入数据的集合
     * @param codeItemMap 涉及到的代码map
     * @param leafCodeItemMap 涉及到的设置了仅可选择末级的代码map
     * @throws GeneralException
     * @author lis
     * @date 2015-7-17 下午06:12:42
     */
    private String setListData(ArrayList<String> items, LazyDynaBean abean,
                               HashMap salaryItemTypeMap, ArrayList a_dataList,HashMap<String ,HashMap<String,String>> codeItemMap,
                               HashMap<String ,HashMap<String,String>> leafCodeItemMap)
            throws GeneralException {
        StringBuffer msg = new StringBuffer();
        boolean haveMore=false;
        int errColNum=0;
        StringBuffer errtext=new StringBuffer();
        try {
            for (int j = 0; j < items.size(); j++) {
                /* 薪资发放-导入失败问题 xiaoyun 2014-9-22 start */
                String[] temps = PubFunc.keyWord_reback(items.get(j))
                        .split("=");
                /* 薪资发放-导入失败问题 xiaoyun 2014-9-22 end */
                String tempValue = ((String) abean.get(temps[0].trim())).trim();
                if (tempValue == null || tempValue.trim().length() == 0) {
                    a_dataList.add(null);
                } else {
                    LazyDynaBean columnBean = (LazyDynaBean) salaryItemTypeMap
                            .get(temps[1].trim().toUpperCase());
                    String decwidth = (String) columnBean.get("decwidth");
                    String itemtype = (String) columnBean.get("itemtype");
                    String codesetid = (String) columnBean.get("codesetid");
                    int itemlength = Integer.parseInt((String) columnBean
                            .get("itemlength"));
                    if ("N".equals(itemtype)) {
                        String value = "";
                        if (isDataType(decwidth, itemtype, tempValue)) {
                            if ("0".equals(decwidth)) {
                                String a_value = PubFunc.round(tempValue, 0);
                                a_dataList.add(new Integer(a_value));

                                value = a_value;
                                if (value.length() > itemlength) {
                                    errtext.setLength(0);
                                    errtext.append("(").append(temps[0]).append(")中数据").append(tempValue).append(" 位数超过最大允许值").append(itemlength).append("位!\r\n");
                                    if((errtext.toString().getBytes("gbk").length+msg.toString().getBytes("gbk").length)>=1900||errColNum>=20)
                                        haveMore=true;
                                    else
                                        msg.append(errtext);
                                    errColNum++;
                                }
                            } else {
                                String a_value = tempValue;
                                a_dataList.add(String.valueOf(new Double(a_value)));
                                errtext.setLength(0);
                                errtext.append("(").append(temps[0]).append(")中数据:").append(tempValue).append(" 整数位数超过最大允许值").append(itemlength).append("位!\r\n");
                                value = tempValue;
                                if (value.split("\\.")[0].length() > itemlength) {
                                    if((errtext.toString().getBytes("gbk").length+msg.toString().getBytes("gbk").length)>=1950||errColNum>=20)
                                        haveMore=true;
                                    else
                                        msg.append(errtext);
                                    errColNum++;
                                }
                                if (value.split("\\.").length == 2
                                        && value.split("\\.")[1].length() > Integer
                                        .parseInt(decwidth)) {
                                    errtext.setLength(0);
                                    errtext.append("(").append(temps[0]).append(")中数据:").append(tempValue).append(" 小数位数超过最大允许值").append(decwidth).append("位!\r\n");
                                    if((errtext.toString().getBytes("gbk").length+msg.toString().getBytes("gbk").length)>=1950||errColNum>=20)
                                        haveMore=true;
                                    else
                                        msg.append(errtext);
                                    errColNum++;
                                    //break;
                                }
                            }

                        } else {
                            a_dataList.add(tempValue);
                            errtext.setLength(0);
                            errtext.append("(").append(temps[0]).append(")中数据:").append(tempValue).append(" 不符合格式!\r\n");
                            if((errtext.toString().getBytes("gbk").length+msg.toString().getBytes("gbk").length)>=1950||errColNum>=20)
                                haveMore=true;
                            else
                                msg.append(errtext);
                            errColNum++;
                        }
                    } else if ("D".equals(itemtype)) {
                        String date=this.checkDateFormat(tempValue, itemlength);
//						if (isDataType(decwidth, itemtype, tempValue)) {
//							Calendar d = Calendar.getInstance();
//							d.set(Calendar.YEAR, Integer.parseInt(tempValue
//									.substring(0, 4)));
//							d.set(Calendar.MONTH, Integer.parseInt(tempValue
//									.substring(5, 7)) - 1);
//							d.set(Calendar.DATE, Integer.parseInt(tempValue
//									.substring(8)));
//							java.sql.Date dd = new java.sql.Date(d.
//									getTimeInMillis());
//							a_dataList.add( DateFormat.getDateInstance(DateFormat.DEFAULT).format(dd));
//						}
                        if(StringUtils.isNotBlank(date)){
                            a_dataList.add(date);
                        }
                        else {
                            errtext.setLength(0);
                            errtext.append("(").append(temps[0]).append(")中数据:").append(tempValue).append(" 不符合格式!\r\n");
                            if((errtext.toString().getBytes("gbk").length+msg.toString().getBytes("gbk").length)>=1950||errColNum>=20)
                                haveMore=true;
                            else
                                msg.append(errtext);
                            errColNum++;
                            a_dataList.add(tempValue);
                            //break;
                        }
                    } else if ("A".equals(itemtype) && !"0".equals(codesetid)) {
                        if(codeItemMap.containsKey(codesetid.toUpperCase())) {
                            HashMap<String,String> valueMap = codeItemMap.get(codesetid.toUpperCase());
                            if ("un".equalsIgnoreCase(codesetid) || "um".equalsIgnoreCase(codesetid) || "@k".equalsIgnoreCase(codesetid)) {
                                tempValue = tempValue.split(":")[0];
                            }
                            if(leafCodeItemMap.containsKey(codesetid.toUpperCase())){
                                if(!leafCodeItemMap.get(codesetid.toUpperCase()).containsKey(tempValue.trim().toUpperCase())){

                                    errtext.setLength(0);
                                    errtext.append("(").append(temps[0]).append(")列仅可选择末端代码:").append(tempValue).append(" 不可选择!\r\n");
                                    if((errtext.toString().getBytes("gbk").length+msg.toString().getBytes("gbk").length)>=1950||errColNum>=20)
                                        haveMore=true;
                                    else
                                        msg.append(errtext);
                                    errColNum++;

                                }
                            }
                            if (valueMap.get(tempValue.trim().toUpperCase()) != null) {
                                a_dataList.add( valueMap.get(tempValue.trim().toUpperCase()));
                            } else
                                a_dataList.add("");
                        }else{
                            a_dataList.add("");
                        }

                    } else if ("N".equals(itemtype)) {
                        a_dataList.add(tempValue);
                    } else if ("M".equals(itemtype)) {
                        a_dataList.add(tempValue);
                    } else {
                        if (tempValue.getBytes().length <= itemlength)
                            a_dataList.add(tempValue);
                        else
                            a_dataList.add("");
                    }
                }
            }
            if(msg.length()>0){
                a_dataList.add(0, "6");//插入错误标记
            }else
                a_dataList.add(0, "");

            if(haveMore)
                msg.append("。。。");
            a_dataList.add(1,msg.toString());//插入错误信息。
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return msg.toString();
    }

    /**
     * @Title: validateData
     * @Description: excel
     * @param fieldList
     *            数据对应指标
     * @param uselessList
     *            关联关系
     * @param form_file
     *            源文件
     * @param originalHeadList
     *            源文件表格头部集合
     * @param salaryid
     *            薪资类别id
     * @author lis
     * @throws GeneralException
     * @date 2015-7-17 下午06:13:14
     */
    public void setExcelData(ArrayList<String> fieldList,ArrayList<String> uselessList, InputStream stream
    		, ArrayList<CommonData> originalHeadList, int salaryid,String tableName,String onlyName) throws GeneralException {
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            GzExcelBo gzExbo = new GzExcelBo(this.conn);
            gzExbo.getSelfAttribute(stream); // 初始化导入文件
            int rowNums = gzExbo.getTotalDataRows2(); // 数据总行数
            int pageNum = rowNums / 100 + 1;
            LazyDynaBean abean = null;
            HashMap salaryItemTypeMap = getSalaryItemMap(); // 薪资项目数据类型 map
            ArrayList<String> codeSetIds=new ArrayList<String>();

            Iterator iterator=salaryItemTypeMap.entrySet().iterator();
            while(iterator.hasNext()){
                Map.Entry entry=(Map.Entry)iterator.next();
                LazyDynaBean bean=(LazyDynaBean) entry.getValue();
                String codeSetid= (String) bean.get("codesetid");
                if(StringUtils.isNotBlank(codeSetid)&&!"0".equals(codeSetid)){
                    codeSetIds.add(codeSetid);
                }
            }

            HashMap<String ,HashMap<String,String>> codeItemMap = getCodeItemMap(codeSetIds); // 获取所有涉及到的代码 map
            HashMap<String ,HashMap<String,String>> leafCodeItemMap = getLeafNodeMap(codeSetIds); // 获取所有设置了仅可选择末级节点的代码 map


            ArrayList updateDateList = new ArrayList(); // 更新数据
            String onlyNameField="";//唯一性指标所对应的excel中列名
            String onlydata="";

            StringBuilder strSql=new StringBuilder();
            StringBuilder strValue=new StringBuilder();
            strSql.append("insert into "+tableName+" ( updateflag,errmsg, ");
            strValue.append("?,?,");

            for (int j = 0; j < fieldList.size(); j++) {//拼接参与计算的sql
                String temp = fieldList.get(j);
                temp = PubFunc.keyWord_reback(temp);
                String[] temps = temp.split("=");
                strSql.append(temps[2].trim()+",");
                if(temps[2].trim().equals(onlyName))//获取唯一性指标列的数组下标，获取唯一性指标值使用
                    onlyNameField=temps[0];
                strValue.append("?,");
            }
            for (int j = 0; j < uselessList.size(); j++) {//拼接仅用于excel导出的sql
                String temp = uselessList.get(j);
                temp = PubFunc.keyWord_reback(temp);
                String[] temps = temp.split("=");
                strSql.append(temps[1].trim()+",");
                strValue.append("?,");
            }
            strSql.deleteCharAt(strSql.length()-1);
            strValue.deleteCharAt(strValue.length()-1);
            if(onlyName.trim().length()!=0)//若具有唯一性指标，则添加唯一性指标出现次数列occtime
                strSql.append(",occtime");
            strSql.append(") values ("+strValue.toString());
            if(onlyName.trim().length()!=0){
                strSql.append(",?");
            }
            strSql.append(")");
            //储存唯一性指标出现次数 用于计算人员归属次数
            HashMap hm=new HashMap<String, Integer>();
            for (int i = 1; i <= pageNum; i++) {
                int from = (i - 1) * 100 + 1;
                int to = i * 100;
                if (to > rowNums)
                    to = rowNums;
                // 从excel中截取from到to的数据
                ArrayList dataList = gzExbo.getDefineData(from, to,
                        originalHeadList);

                updateDateList.clear();


                ArrayList a_dataList = null;
                for (int e = 0; e < dataList.size(); e++) {
                    abean = (LazyDynaBean) dataList.get(e);

                    //通过遍历数据集排除空行
                    boolean isValid=false;
                    for(CommonData tempdata:originalHeadList){
                        Object t=abean.get(tempdata.getDataName());
                        if(t!=null&&StringUtils.isNotBlank(t.toString())){
                            isValid=true;
                            break;
                        }
                    }
                    if(!isValid)
                        continue;
                    // 获得可以更新数据
                    a_dataList = new ArrayList();
                    // 校验数据(若出现错误不终止导入，在此方法中插入错误标记6后导出excel时在文件中写入错误信息) zhanghua 2017-4-17
                    this.setListData(fieldList, abean, salaryItemTypeMap,
                            a_dataList,codeItemMap,leafCodeItemMap);
                    for(String str:uselessList){
                        a_dataList.add(String.valueOf(abean.get(str.split("=")[0].trim())));
                    }

                    //计算唯一性指标出现次数.用于计算归属次数
                    if(StringUtils.isNotBlank(onlyNameField)&&StringUtils.isNotBlank(onlyName)){
                        onlydata=(String) abean.get(onlyNameField);
                        if(onlydata!=null&&onlydata.trim().length()!=0){
                            if(hm.get(onlydata)!=null){
                                hm.put(onlydata, Integer.parseInt(hm.get(onlydata).toString())+1);
                            }else{
                                hm.put(onlydata, 1);
                            }

                            a_dataList.add(Integer.parseInt(hm.get(onlydata).toString()));
                        }else
                            a_dataList.add(0);
                    }

                    updateDateList.add(a_dataList);
                }

                dao.batchInsert(strSql.toString(), updateDateList);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);

        } finally {
            PubFunc.closeIoResource(stream);
        }

    }
    /**
     * 检查导入数据
     * @param salaryTableName 薪资表名
     * @param tempTableName 临时表名
     * @param relationItem 关联关系
     * @param onlyName 唯一性指标
     * @param ff_date 业务日期
     * @throws GeneralException
     */
    public void checkTempDate(String salaryTableName,String tempTableName,ArrayList<String> relationItem,String onlyName,String ff_date)throws GeneralException{
        try{
            ContentDAO dao = new ContentDAO(this.conn);
            SalaryTemplateBo gzbo=new SalaryTemplateBo(this.conn,this.salaryid,this.userview);
            com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo ctrlparam=gzbo.getCtrlparam();
            String manager=ctrlparam.getValue(SalaryCtrlParamBo.SHARE_SET, "user");    //共享薪资帐套的管理员帐号
            String strpre="";
            if(manager!=null&&manager.length()>0&&!manager.equalsIgnoreCase(this.userview.getUserName()))//是共享非管理员
                strpre=gzbo.getWhlByUnits(salaryTableName,false);

            //updateflag 0 重复 1更新 2插入 3无法匹配 4源数据唯一性指标重复 5 非起草或驳回数据 6 数据格式有误。
            String strSql=" ";
            String temp1="";
            String strWhere="";
            if(Sql_switcher.searchDbServer()==2)//是oracle
                temp1="t1.";
            else
                temp1=tempTableName+".";

            String str1="",str2="",str3="",str4="";

            HashMap itemMap=this.getFieldMap(relationItem);


            //拼接关联关系条件
            for(String str:relationItem){
                String strField=str.split("=")[2];
                String salaryField=str.split("=")[1];
                if("only".equalsIgnoreCase(strField.split("_")[1]))
                    continue;
                str4+=salaryTableName+"."+salaryField+",";
                str1+=salaryTableName+"."+salaryField+" as "+strField+",";
                if( !"N".equalsIgnoreCase(((FieldItem)itemMap.get(salaryField.toUpperCase())).getItemtype()) ){
                    str2+=" "+Sql_switcher.isnull(salaryTableName+"."+salaryField, "0")+"="+Sql_switcher.isnull("t."+strField, "0")+" and";
                    str3+=" "+Sql_switcher.isnull(temp1+strField, "0")+"="+Sql_switcher.isnull("a."+strField, "0")+" and";
                }else{
                    str2+=" "+Sql_switcher.charToFloat(Sql_switcher.isnull(salaryTableName+"."+salaryField,"0"))+"="+Sql_switcher.charToFloat(Sql_switcher.isnull("t."+strField,"0"))+" and";
                    str3+=" "+Sql_switcher.charToFloat(Sql_switcher.isnull(temp1+strField,"0"))+"="+Sql_switcher.charToFloat(Sql_switcher.isnull("a."+strField,"0"))+" and";
                }
            }

            /********************************* 判断是否起草或驳回数据*********************************/
            Boolean isApprove=false;
            String flow_flag = gzbo.getCtrlparam().getValue(
                    SalaryCtrlParamBo.FLOW_CTRL, "flag");
            if ("1".equalsIgnoreCase(flow_flag))
                isApprove = true;
            if (isApprove)// 走审批
            {
                strWhere="  sp_flag not in ('01','07')";// 起草或已批
            }else {//不走审批的需要控制提交后不能导入
                if (!this.isAllowEditSubdata()){
                    strWhere="  sp_flag not in ('01','07')";
                }
            }
            if(StringUtils.isNotBlank(manager)&&!manager.equalsIgnoreCase(this.userview.getUserName())) {//是共享非管理员
                if(StringUtils.isNotBlank(strWhere)) {
                    strWhere += " or ";
                }
                strWhere += " sp_flag2 not in ('01','07') ";
            }
            if(StringUtils.isNotBlank(strWhere)){
                strWhere=" and ( "+strWhere+" ) ";
                if(Sql_switcher.searchDbServer()==2)//是oracle
                    strSql="update "+tempTableName+" t1 set updateflag='5' where (updateflag is null or updateflag ='') and EXISTS ( select 1 from ";
                else
                    strSql="update "+tempTableName+" set updateflag='5' where (updateflag is null or updateflag ='') and EXISTS ( select 1 from ";

                strSql+="(select "+str1.substring(0, str1.length()-1)+"  from "+salaryTableName+" inner join "+tempTableName+" t on "+str2.substring(0, str2.length()-4)+" where (updateflag is null or updateflag ='')   "+strWhere;
                strSql+=") a where "+str3+"  (updateflag is null or updateflag =''))";
                dao.update(strSql);
            }
            /********************************* 判断是否起草或驳回数据结束*********************************/


            /************************************ 计算重复行数量**********************************/
            if(Sql_switcher.searchDbServer()==2)//是oracle
            {
                strSql="update "+tempTableName+" t1 set updateflag='0'  where (updateflag is null or updateflag ='') and EXISTS ( select 1 from ";

            }else{
                strSql="update "+tempTableName+" set updateflag='0'  where (updateflag is null or updateflag ='') and EXISTS ( select 1 from ";

            }
            strSql+="(select "+str1+" count(1) as num from "+salaryTableName+"  inner join "+tempTableName+" t on "+str2.substring(0, str2.length()-4)+" where  (updateflag is null or updateflag ='') "+strpre+" group by "+str4.substring(0,str4.length()-1);
            strSql+=") a where "+str3+" num>1 and  (updateflag is null or updateflag =''))";
            dao.update(strSql);
            /************************************ 计算重复行数量结束**********************************/
            /************************************ 计算更新行数量**************************************/
            strSql="";
            if(Sql_switcher.searchDbServer()==2)//是oracle
                strSql="update "+tempTableName+" t1 set updateflag='1' where (updateflag is null or updateflag ='') and  EXISTS ( select 1 from ";
            else
                strSql="update "+tempTableName+" set updateflag='1' where (updateflag is null or updateflag ='') and EXISTS ( select 1 from ";

            strSql+="(select "+str1+"count(1) as num from "+salaryTableName+" inner join "+tempTableName+" t on "+str2.substring(0, str2.length()-4)+" where  (updateflag is null or updateflag ='') "+strpre+" group by "+str4.substring(0,str4.length()-1);
            strSql+=") a where "+str3+" num=1 and  (updateflag is null or updateflag =''))";
            dao.update(strSql);
            /************************************ 计算更新行数量结束**************************************/

            /************************************ 计算新增行数量**********************************/
            if(!"".equals(onlyName.trim())){//如果存在唯一性指标 则进行新增判断。

                String _flag=ctrlparam.getValue(SalaryCtrlParamBo.COND_MODE,"flag");  // "":没条件 0：简单条件 1：复杂条件
                RecordVo templateVo=gzbo.getTemplatevo();
                String cond=templateVo.getString("cond");
                String cexpr=templateVo.getString("cexpr");
                String flag=ctrlparam.getValue(SalaryCtrlParamBo.PRIV_MODE,"flag");
                String cbase= gzbo.getTemplatevo().getString("cbase");
                String [] itemBase=cbase.split(",");

                for(String pre:itemBase){
                    if("".equals(pre.trim()))
                        continue;
                    StringBuilder buf=new StringBuilder();
                    StringBuilder insertSql=new StringBuilder();

                    if(manager!=null&&manager.length()>0&&!manager.equalsIgnoreCase(this.userview.getUserName()))
                    {
                        buf.append(" from "+pre+"A01 where 1=1 "+gzbo.getWhlByUnits(pre+"A01",false));

                    }
                    else if(flag!=null&& "1".equals(flag))  // 人员范围权限过滤标志  1：有
                    {
                        buf.append(this.userview.getPrivSQLExpression(pre, false));
                    }
                    else
                    {
                        buf.append(" from "+pre+"A01 where 1=1 ");
                    }


                    if("0".equals(_flag)&&cond.length()>0)  //0：简单条件
                    {
                        FactorList factor = new FactorList(cexpr, cond,pre, false, false, true, 1, "su");
                        String strSql1 = factor.getSqlExpression();
                        buf.append(" and "+pre+"A01.a0100 in ( select "+pre+"A01.a0100 "+strSql1+")");

                    }
                    else if("1".equals(_flag)&&cond.length()>0)  // 1：复杂条件
                    {
                        if(ff_date.length()==7)
                            ff_date=ff_date+"-01";
                        HashMap paramMap=new HashMap();
                        paramMap.put("pre",pre);               //人员库
                        paramMap.put("ff_date",ff_date);  //发放日期
                        paramMap.put("cond",cond);    //高级条件
                        buf.append(getComplexCondSql(ctrlparam,paramMap));
                    }

                    /**停发标志*/
                    String a01z0Flag=ctrlparam.getValue(SalaryCtrlParamBo.A01Z0,"flag");  // 是否显示停发标识  1：有
                    if(a01z0Flag!=null&& "1".equals(a01z0Flag))
                        buf.append(" and (1TABLE1.A01Z0='1' or 1TABLE1.A01Z0='' or 1TABLE1.A01Z0 is null)");

                    String tstr="";
                    if(Sql_switcher.searchDbServer()==2){//是oracle
                        insertSql.append("update "+tempTableName+" gzt set (updateflag,Nbase,A0100,A0000,B0110,E0122,A0101,A01Z0)=(select '2','"+pre+"',A0100,A0000,B0110,E0122,A0101,A01Z0 ");
                        tstr="update "+tempTableName+" te set updateflag='4' where Exists( select 1 from (select "+onlyName+",count(1) as num "+buf.toString()+" group by "+onlyName+") a where a.num>1 and  te."+onlyName+"_only=a."+onlyName+")";
                        buf.append(" and gzt."+onlyName+"_only="+pre+"A01."+onlyName+" and gzt."+onlyName+"_only is not null) ");
                        insertSql.append(buf.toString().replaceAll("1TABLE1.",""));
                        insertSql.append(" where (gzt.updateflag is null or gzt.updateflag ='') and exists (select 1");
                        insertSql.append(buf.toString().replaceAll("1TABLE1",pre+"A01"));

                    }else{

                        tstr="update "+tempTableName+" set updateflag='4' where Exists( select 1 from (select "+onlyName+",count(1) as num "+buf.toString().replaceAll("1TABLE1.","")+" group by "+onlyName+") a where a.num>1 and  "+tempTableName+"."+onlyName+"_only=a."+onlyName+")";

                        insertSql.append("update "+tempTableName+" set updateflag='2',Nbase='"+pre+"',A0100="+pre+"A01.A0100,A0000="+pre+"A01.A0000,B0110="+pre+"A01.B0110,E0122="+pre+"A01.E0122,A0101="+pre+"A01.A0101, "+tempTableName+".A01Z0="+pre+"A01.A01Z0 ");

                        insertSql.append(buf.toString().replaceAll("1TABLE1",tempTableName));
                        insertSql.append(" and "+tempTableName+"."+onlyName+"_only="+pre+"A01."+onlyName+" and "+tempTableName+"."+onlyName+"_only is not null and ("+tempTableName+".updateflag is null or "+tempTableName+".updateflag ='')");

                    }
                    dao.update(tstr.replaceAll("1TABLE1",pre+"A01"));//首先计算 源数据唯一性指标重复的行

                    dao.update(insertSql.toString());//计算新增行
                }

            }


        }catch(Exception e){
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 获取列属性
     * @param relationItem 对应关系
     * @return
     */
    private HashMap<String,FieldItem> getFieldMap(ArrayList<String> relationItem){
        HashMap<String,FieldItem> map=new HashMap<String,FieldItem>();
        try{

            FieldItem field=new FieldItem();
            for(String str:relationItem){
                String strField=str.split("=")[2];
                String salaryField=str.split("=")[1];
                if("only".equalsIgnoreCase(strField.split("_")[1]))
                    continue;

                if("a00z0".equalsIgnoreCase(salaryField)|| "a00z2".equalsIgnoreCase(salaryField)){
                    field=new FieldItem();
                    field.setItemtype("D");
                    field.setItemdesc("a00z2".equalsIgnoreCase(salaryField)?"发放日期":"归属日期");
                    field.setItemid(salaryField);
                    field.setFormat("yyyy-mm-dd");
                    field.setDecimalwidth(0);
                }else if("a00z1".equalsIgnoreCase(salaryField)|| "a00z3".equalsIgnoreCase(salaryField)){
                    field=new FieldItem();
                    field.setItemdesc("a00z3".equalsIgnoreCase(salaryField)?"发放次数":"归属次数");
                    field.setItemtype("N");
                    field.setItemid(salaryField);
                    field.setFormat("yyyy-mm-dd");
                    field.setDecimalwidth(0);
                }
                else
                    field=DataDictionary.getFieldItem(salaryField);

                map.put(salaryField.toUpperCase(), field);
            }



        }catch(Exception e){
            e.printStackTrace();
        }
        return map;
    }



    /**
     * 执行更新操作
     * @param oppositeItem 更新列
     * @param relationItem 关联关系
     * @param tempTableName 临时表名
     * @param tableName 薪资表名
     * @return
     */
    public int doUpdateImportData(ArrayList<String> oppositeItem,ArrayList<String> relationItem,String tempTableName,String tableName){
        int i=0;
        try{
            StringBuilder strSql=new StringBuilder();
            String temp1 ="";
            HashMap itemMap=this.getFieldMap(relationItem);

            if(Sql_switcher.searchDbServer()==2){//是oracle
                strSql.append("update "+tableName+" tbn set (");
                String updateField="",tempField="",whereStr="";
                for(String str:oppositeItem){
                    updateField+=str.split("=")[1]+",";
                    FieldItem field=DataDictionary.getFieldItem(str.split("=")[1]);
                    if("D".equalsIgnoreCase(field.getItemtype())){
                        String format=this.getDateFormatFromLength(field.getItemlength(),1);
                        tempField+=Sql_switcher.charToDate(str.split("=")[2]).replaceAll("YYYY.MM.dd", format)+",";
                    }
                    else
                        tempField+=str.split("=")[2]+",";

                }
                updateField=updateField.substring(0, updateField.length()-1);
                tempField=tempField.substring(0,tempField.length()-1);
                strSql.append(updateField);
                strSql.append(") =(select ");
                strSql.append(tempField);
                for(String str:relationItem){
                    if("only".equalsIgnoreCase(str.split("=")[2].split("_")[1]))//不将唯一性指标关联当做关联关系
                        continue;
                    if( !"N".equalsIgnoreCase(((FieldItem)itemMap.get(str.split("=")[1].toUpperCase())).getItemtype()) ){
                        whereStr+=" and "+Sql_switcher.isnull("tbn."+str.split("=")[1], "0")+"= "+Sql_switcher.isnull("etn."+str.split("=")[2], "0");
                    }else{
                        whereStr+=" and "+Sql_switcher.charToFloat(Sql_switcher.isnull("tbn."+str.split("=")[1],"0"))+"= "+Sql_switcher.charToFloat(Sql_switcher.isnull("etn."+str.split("=")[2],"0"));
                    }
                }
                strSql.append(" from "+tempTableName+" etn where  1=1 "+whereStr+")");
                strSql.append(" where exists (select 1 from "+tempTableName+" etn where updateflag='1' "+whereStr+")");

            }else{
                strSql.append("update "+tableName+" set ");
                String updateField="",tempField="",whereStr="";
                for(String str:oppositeItem){

                    if("D".equalsIgnoreCase(DataDictionary.getFieldItem(str.split("=")[1]).getItemtype()))
                        tempField+=str.split("=")[1]+"="+Sql_switcher.charToDate("etn."+str.split("=")[2])+",";
                    else
                        tempField+=str.split("=")[1]+"=etn."+str.split("=")[2]+",";
                }
                tempField=tempField.substring(0,tempField.length()-1);
                strSql.append(tempField);
                for(String str:relationItem){
                    if("only".equalsIgnoreCase(str.split("=")[2].split("_")[1]))//不将唯一性指标关联当做关联关系
                        continue;
                    whereStr+=" and "+Sql_switcher.isnull(tableName+"."+str.split("=")[1], "0")+"= "+Sql_switcher.isnull("etn."+str.split("=")[2], "0");
                }
                strSql.append(" from "+tempTableName+" etn where  updateflag='1' "+whereStr);
            }

            ContentDAO dao = new ContentDAO(this.conn);
            i=dao.update(strSql.toString());


        }catch(Exception e){
            e.printStackTrace();
        }
        return i;
    }

    /**
     * 执行新增操作
     * @param oppositeItem 更新列
     * @param tempTableName 临时表名
     * @param tableName 薪资表名
     * @param onlyName 唯一性指标
     * @param Manager 共享管理员用户名
     * @param appdate 发放日期
     * @param count 发放次数
     * @return
     */
    public int doInsertImportData(ArrayList<String> oppositeItem,String tempTableName,String tableName,String onlyName,String Manager,String appdate,String count){
        int i=0;
        try{
            StringBuilder strSql=new StringBuilder();
            String username="";
            if("".equals(Manager.trim()))
                username=this.userview.getUserName();
            else
                username=Manager;
            if(appdate.length()==7)
                appdate=appdate+"-01";
            ContentDAO dao = new ContentDAO(this.conn);
            RowSet rs=null;
            String sA00Z3=count,sA00Z2=appdate,sA00Z0=appdate;
            String tempOnlyName=onlyName+"_only";
            LazyDynaBean busiDate=new LazyDynaBean(); //业务日期 次数   date:2010-03-01   count:1
            busiDate.set("date",appdate);
            busiDate.set("count",count);
            ArrayList<String> preItem=new ArrayList<String>();

            rs=dao.search("select nbase from "+tempTableName+" where updateflag='2' group by Nbase");
            while(rs.next()){
                if(rs.getString("nbase").length()!=0)
                    preItem.add(rs.getString("nbase"));
            }
            if(preItem.size()==0)//如果没有数据 不再继续更新
                return 0;


//		if(Sql_switcher.searchDbServer()==2)//是oracle
//			strSql.append("select "+Sql_switcher.dateToChar("A00Z2", "yyyy-mm-dd")+" as A00Z2,A00Z3,"+Sql_switcher.dateToChar("A00Z0", "yyyy-mm-dd")+" as A00Z0 from  "+tableName+" where ROWNUM=1");
//		else
//			strSql.append("select top 1 "+Sql_switcher.dateToChar("A00Z2", "yyyy-mm-dd")+" as A00Z2,A00Z3,"+Sql_switcher.dateToChar("A00Z0", "yyyy-mm-dd")+" as A00Z0 from  "+tableName);
//		rs=dao.search(strSql.toString());
//
//		if(rs.next()){
//			sA00Z2=rs.getString("A00Z2");
//			sA00Z0=rs.getString("A00Z0");
//			sA00Z3=rs.getString("A00Z3");
//		}
//		strSql.setLength(0);
            /**—————————————导入人员—————————————————**/
            strSql.append("insert into "+tableName+"(Nbase,A0100,A0000,B0110,E0122,A0101,userflag,Sp_flag,E0122_O,B0110_O,dbid,A00Z0,A00Z1,A00Z2,A00Z3,A01Z0,");
            if(Manager.trim().length()!=0)
                strSql.append("Sp_flag2 ,");

            ArrayList exeptFlds=new ArrayList();
            LazyDynaBean exeptBean=null;

            String updateField="",tempField="",whereStr="";
            for(String str:oppositeItem){
                updateField+=str.split("=")[1]+",";
                FieldItem field=DataDictionary.getFieldItem(str.split("=")[1]);
                if("D".equalsIgnoreCase(field.getItemtype())){
                    String format=this.getDateFormatFromLength(field.getItemlength(),1);
                    tempField+=Sql_switcher.charToDate(str.split("=")[2]).replaceAll("YYYY.MM.dd", format)+",";
                }
                else
                    tempField+=str.split("=")[2]+",";
                exeptBean=new LazyDynaBean();
                exeptBean.set("itemid", str.split("=")[1]);
                exeptFlds.add(exeptBean);
            }

            updateField=updateField.substring(0, updateField.length()-1);
            tempField=tempField.substring(0, tempField.length()-1);
            strSql.append(updateField);
            strSql.append(") select  Nbase,A0100,A0000,B0110,E0122,A0101 ,'"+username+"' as userflag ,'01' as Sp_flag ,(select a0000 from organization where organization.codeitemid=E0122) as E0122_O,"
                    + "(select a0000 from organization where organization.codeitemid=B0110) as B0110,(SELECT dbid from DBNAME where pre=Nbase)as dbid,");
            strSql.append(Sql_switcher.charToDate("'"+sA00Z0+"'")+" as A00Z0,"+count+" as a00z1,");
            strSql.append(Sql_switcher.charToDate("'"+sA00Z2+"'")+" as A00Z2,"+sA00Z3+" as a00z3,A01Z0,");

            if(Manager.trim().length()!=0)
                strSql.append("'01' as Sp_flag2,");

            strSql.append(tempField);
            strSql.append(" from "+tempTableName+" ipt where updateflag='2'");

            i=dao.update(strSql.toString());

            /**————————————导入累计项 计算项———————————————**/
            SalaryAccountBo bo=new SalaryAccountBo(this.conn, userview, this.salaryid);
            ArrayList itemList=bo.getSalaryTemplateBo().getSalaryItemList("",""+salaryid,1);
            String importMenSql_where="";
            StringBuffer strWhere=new StringBuffer();
            for(String strPre:preItem){
                importMenSql_where="select A0100 from "+tempTableName+" where updateflag='2' and lower(Nbase)='"+strPre.toLowerCase()+"'";

                strWhere.append(" exists (select null from "+tempTableName+" where "+tableName+".a0100="+tempTableName+".a0100 and updateflag='2' and lower("+tableName+".nbase)='");
                strWhere.append(strPre.toLowerCase());
                strWhere.append("' and "+tableName+".a00z1=(select Max(a00z1) from  "+tableName+" t1 where t1.a0100="+tempTableName+".a0100 and lower(t1.nbase)='"+strPre.toLowerCase()+"'))");

                bo.firstComputing(strWhere.toString(), strPre, true, exeptFlds, itemList, importMenSql_where, busiDate);
                strWhere.setLength(0);
            }




        }catch(Exception e){
            e.printStackTrace();
        }
        return i;
    }

    /**
     * 删除临时表
     * @param tableName
     */
    public void dropTempTable(String tableName){
        try{
            ContentDAO dao = new ContentDAO(this.conn);
            dao.update("drop table "+tableName);

        }catch(Exception e){
            e.printStackTrace();
        }
    }
    /**
     * 薪资类别定义复杂条件时，生成的SQL语句 与salaryAccountBo中同名方法类似。
     * @param paramMap 参数集合   	paramMap.put("pre",pre);               //人员库 	paramMap.put("ff_date",ff_date);  //发放日期  	paramMap.put("cond",cond);    //高级条件
     * @return
     */
    private String  getComplexCondSql(com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo ctrlparam,HashMap paramMap)throws GeneralException
    {
        String w ="";
        try
        {
            SalaryTemplateBo gzbo=new SalaryTemplateBo(this.conn,this.salaryid,this.userview);
            ContentDAO dao=new ContentDAO(this.conn);
            String pre=(String)paramMap.get("pre");
            String ff_date=(String)paramMap.get("ff_date");
            String cond=(String)paramMap.get("cond");
            String manager=ctrlparam.getValue(SalaryCtrlParamBo.SHARE_SET, "user");    //共享薪资帐套的管理员帐号
            String flag=ctrlparam.getValue(SalaryCtrlParamBo.PRIV_MODE,"flag");  // 人员范围权限过滤标志  1：有


            String tempTableName ="";

            int infoGroup = 0; // forPerson 人员
            int varType = 8; // logic

            cond=cond.replaceAll("归属日期\\(\\)","#"+ff_date+"#");
            cond=cond.replaceAll("归属日期","#"+ff_date+"#");

            String whereIN="";
            if(manager!=null&&manager.length()>0&&!manager.equalsIgnoreCase(this.userview.getUserName()))
            {
                whereIN=InfoUtils.getWhereINSql(this.userview,pre);
                whereIN="select "+pre+"A01.a0100 "+whereIN;
            }
            if("1".equals(flag))
            {
                whereIN=InfoUtils.getWhereINSql(this.userview,pre);
                whereIN="select "+pre+"A01.a0100 "+whereIN;
            }
            ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
            //			alUsedFields.addAll(this.getMidVariableList());   没有意义吧
            YksjParser yp = new YksjParser(this.userview ,alUsedFields,
                    YksjParser.forSearch, varType, infoGroup, "Ht",pre.toString());
            YearMonthCount ymc=null;
            yp.run_Where(cond, ymc,"","hrpwarn_result", dao, whereIN,this.conn,"A", null);
            tempTableName = yp.getTempTableName();
            w = yp.getSQL();

            if(w.trim().length()<3)
                throw GeneralExceptionHandler.Handle(new Exception(gzbo.getTemplatevo().getString("cname")+" 定义的人员范围有误!"));


            if(w!=null&&w.trim().length()>0)
            {
                return  " and exists (select null from "+tempTableName+" where "+tempTableName+".a0100="+pre+"A01.a0100 and ( "+w+" ))";
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        return w;

    }
    /**
     * 获取数据检验结果
     * @param tempTableName
     * @return 结果数据 下标为 0 重复 1更新 2插入 3无法匹配  4源数据唯一性指标重复 5 非起草或驳回数据 6 数据格式有误
     */
    public String[] getInputDataMsg(String tempTableName){
        String [] msgitem={"0","0","0","0","0","0","0"};

        try{
            ContentDAO dao=new ContentDAO(this.conn);
            String strSql="select count(1) as num ,"+Sql_switcher.isnull("nullif(updateflag,'')", "'3'")+" as updateflag from "+tempTableName+" GROUP BY updateflag ORDER BY updateflag";
            RowSet rs=null;
            rs=dao.search(strSql);
            //updateflag 0 重复 1更新 2插入 3无法匹配 4源数据唯一性指标重复 5 非起草或驳回数据 6 数据格式有误
            while(rs.next())
                msgitem[Integer.parseInt(rs.getString("updateflag"))]=rs.getString("num");

            //strMsg="本次导入可更新数据"+msgitem[1]+"条，可新增数据"+msgitem[2]+"条，存在重复对应数据"+msgitem[0]+"条，无法对应数据"+msgitem[3]+"条。";

        }catch(Exception e){
            e.printStackTrace();
        }
        return msgitem;
    }

    /**
     * 获取导出excel sql
     * @param tempTableList
     * @param tempTableName
     * @param flag
     * @return
     */
    public String getCheckExportSql(ArrayList<String> tempTableList,String tempTableName,HashMap<String,LazyDynaBean> headMap,String flag){
        StringBuilder strSql=new StringBuilder();

        LazyDynaBean columsBean =null;
        strSql.append("select ");
        for(String str:tempTableList){
            String [] stritem=str.split("=");
            if(!stritem[1].startsWith("f_useless")){
                columsBean=headMap.get(stritem[1].toLowerCase());
                if("D".equalsIgnoreCase(columsBean.get("colType").toString()))
                {
                    strSql.append(Sql_switcher.charToDate(stritem[2])+" as "+stritem[2]+",");
                }else
                    strSql.append(stritem[2]+",");

            }else
                strSql.append(stritem[2]+",");
        }
        if(!"0".equals(flag)){
            flag=" updateflag='"+flag+"'";
        }else{
            flag=" (updateflag<>'1' and updateflag<>'2') or (updateflag is null or updateflag ='') ";
        }
        strSql.append(" (case "+Sql_switcher.isnull("nullif(updateflag,'')", "'-1'")+" when '0' then '数据重复匹配'  when '1' then '更新行'  when '2' then '新增行'  when '-1' then '无法对应'  when '4' then '系统数据唯一指标重复'  when '5' then '审核状态不为起草或驳回'  when '6' then errmsg end ) as  updateflag from "+tempTableName+" where "+flag);
        return strSql.toString();
    }


    /**
     * 导出excel
     * @param fileName
     * @param headList
     * @param dataList
     */
    public void createExcelSheet(ArrayList<LazyDynaBean> headList,ArrayList dataList,String sheetName){

        try{
            int page=1;
            int nrows =20000;
            LazyDynaBean rowDataList = null;
            LazyDynaBean dataBean=null;
            ArrayList dataArrayList = null;
            LazyDynaBean headBean=null;
            HSSFCell cell=null;
            HSSFRichTextString richTextString =null;
            HSSFDataFormat df = wb.createDataFormat();
            int rowNum=0;
            HashMap dropDownDataTemp = new HashMap();
            HSSFRow row = null;

            if (dataList.size() == 0) {
                if(StringUtils.isBlank(sheetName))
                    sheet = wb.createSheet(page + "");
                else
                    sheet = wb.createSheet(sheetName+(page==1?"":page));//haosl 20161012 解决导出数据超过20000条时，不能导出的问题
                //设置表格列标题
                setHead(headList,0);
            }

            HashMap colStyle_Map=new HashMap();
            HSSFCellStyle style=null;
            for(int i=0;i<dataList.size();i++)
            {
                if (i == 0 || (i != 1 && i % nrows == 1)) {
                    rowNum = 0;
                    if(StringUtils.isBlank(sheetName))
                        sheet = wb.createSheet(page + "");
                    else
                        sheet = wb.createSheet(sheetName+(page==1?"":page));//haosl 20161012 解决导出数据超过20000条时，不能导出的问题
                    page++;

                    //设置表格列标题
                    setHead(headList,0);
                    rowNum = 0 + 1;

//				if(this.protect)//是否锁定为只读
//					sheet.protectSheet("");//保护当前页，只读
                }

                boolean isArrayList = false;
                if(dataList.get(i) instanceof ArrayList)//判断数据是List还是LazyDynaBean
                    isArrayList = true;
                if(isArrayList){
                    dataArrayList = (ArrayList)dataList.get(i);//第i行数据
                }else{
                    rowDataList=(LazyDynaBean)dataList.get(i);//第i行数据
                }


                String content = "";
                int fromRowNum = rowNum;//合并单元格从第几行开始
                int toRowNum = rowNum;//合并单元格到地几行结束
                int fromColNum = 0;//合并单元格从第几列开始
                int toColNum = 0;//合并单元格到第几列结束

                row = sheet.getRow(rowNum);
                if (row == null)
                    row = sheet.createRow(rowNum);

                //给该行赋值
                for(int columnIndex=0;columnIndex<headList.size();columnIndex++)
                {
                    headBean=(LazyDynaBean)headList.get(columnIndex);
                    String headItemid = (String)headBean.get("itemid");//列标题代码
                    String type = (String)headBean.get("colType");//该列的类型，D：日期，N：数字，A：字符
                    int deciwidth = headBean.get("decwidth") == null?0:Integer.parseInt((String)headBean.get("decwidth"));//小数点位数

                    //当前列是否锁定
                    boolean columnLocked = headBean.get("columnLocked") == null ? false : (Boolean)headBean.get("columnLocked");

                    if(isArrayList){
                        content = (String)dataArrayList.get(columnIndex);
                        fromRowNum = rowNum;
                        toRowNum = rowNum;
                        fromColNum = toColNum;
                    }else{
                        if(rowDataList.get(headItemid) == null){
                            dataBean = new LazyDynaBean();
                        }else
                            dataBean = (LazyDynaBean)rowDataList.get(headItemid);
                        content = (String)dataBean.get("content");

                        if(dataBean.get("fromRowNum")!=null && dataBean.get("toRowNum")!=null && dataBean.get("fromColNum")!=null && dataBean.get("toColNum")!=null){
                            fromRowNum = (Integer)dataBean.get("fromRowNum");
                            toRowNum = (Integer)dataBean.get("toRowNum");
                            fromColNum = (Integer)dataBean.get("fromColNum");
                            toColNum = (Integer)dataBean.get("toColNum");
                        }else{
                            fromRowNum = rowNum;
                            toRowNum = rowNum;
                            fromColNum = toColNum;
                        }
                    }
//				 if(this.rowHeight != -1){
//					 row.setHeight(this.rowHeight);
//				 }
                    cell = row.getCell(fromColNum);
                    if (cell == null)
                        cell = row.createCell(fromColNum);
                    HashMap colStyleMap = (HashMap)headBean.get("colStyleMap");

                    if(colStyle_Map.get(headItemid+"_"+columnIndex)!=null)
                        style=(HSSFCellStyle)colStyle_Map.get(headItemid+"_"+columnIndex);
                    else
                    {
                        if("N".equals(type))
                        {
                            style = getStyle(colStyleMap,"");
                            int scale = deciwidth;
                            StringBuffer buf = new StringBuffer();
                            for(int k=0;k<scale;k++)
                            {
                                buf.append("0");
                            }
                            String format="";
                            if(scale>0){
                                format="0."+buf.toString()+"_ ";
                                style.setDataFormat(df.getFormat(format));
                            }
                            style.setAlignment(HorizontalAlignment.RIGHT);
                            style.setLocked(columnLocked);
                        }
                        else
                            style = getStyle(colStyleMap,"");
                        colStyle_Map.put(headItemid+"_"+columnIndex, style);
                    }

                    if("N".equals(type))
                    {
                        if(StringUtils.isBlank(content))
                            content="0";
                        cell.setCellStyle(style);
                        BigDecimal bd = new BigDecimal(content);
                        bd = bd.setScale(deciwidth, BigDecimal.ROUND_HALF_UP);
                        cell.setCellValue(bd.doubleValue());
                    }
                    else
                    {
                        richTextString=new HSSFRichTextString(content);
                        style.setLocked(columnLocked);
                        cell.setCellStyle(style);
                        cell.setCellValue(richTextString);
                    }
                    toColNum++;
                }
                rowNum++;
            }



        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void doSendExcel(String fileName){
        FileOutputStream fileOut = null;
        try{
            String url = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + fileName;
            fileOut = new FileOutputStream(url);
            wb.write(fileOut);
        }catch(Exception e){
            e.printStackTrace();
        }finally {
        	PubFunc.closeIoResource(fileOut);
        }
    }
    private int setHead(ArrayList<LazyDynaBean> headList,int headStartRowNum) throws GeneralException{
        int rowNum = headStartRowNum;
        try {
            int headSize = headList.size();
            HSSFPatriarch patr = sheet.createDrawingPatriarch();
            HSSFComment comm = null;
            int fromRowNum = 0;
            int toRowNum = 0;
            int fromColNum = 0;
            int toColNum = 0;

            for (int columnIndex = 0; columnIndex < headSize; columnIndex++) {
                LazyDynaBean headBean = (LazyDynaBean) headList.get(columnIndex);
                String type=(String)headBean.get("colType");//该列的样式
                String itemid = (String) headBean.get("itemid");// 代码类id
                String codesetid = (String) headBean.get("codesetid");// 代码类id
                String content = (String) headBean.get("content");//当前列名标题
                String comment = (String) headBean.get("comment");//当前列名标题的注释


                if(headBean.get("fromRowNum")!=null && headBean.get("toRowNum")!=null && headBean.get("fromColNum")!=null && headBean.get("toColNum")!=null){
                    fromRowNum = (Integer)headBean.get("fromRowNum");
                    toRowNum = (Integer)headBean.get("toRowNum");
                    fromColNum = (Integer)headBean.get("fromColNum");
                    toColNum = (Integer)headBean.get("toColNum");
                }else{
                    fromRowNum = headStartRowNum;
                    toRowNum = headStartRowNum;
                    fromColNum = toColNum;
                }

                HSSFCellStyle headStyle = null;
                HashMap headStyleMap = (HashMap) headBean.get("headStyleMap");//当前列名标题样式
                short columnWidth = 0;
                if(headStyleMap != null){
                    Integer columnWidthTemp = (Integer)headStyleMap.get("columnWidth");
                    columnWidth = Short.valueOf(columnWidthTemp.toString());
                }
                if(columnWidth == 0){
                    if ("D".equals(type)) {
                        columnWidth = 4000;
                    }else if("N".equals(type)){
                        columnWidth = 3000;
                    }else if ("UN".equalsIgnoreCase(codesetid) || "UM".equalsIgnoreCase(codesetid)|| "@k".equalsIgnoreCase(codesetid))
                    {
                        columnWidth = 5000;
                    }else{
                        columnWidth = 3500;
                    }
                }
                //设置单元格长度
                sheet.setColumnWidth((short) (fromColNum), columnWidth);
                //获得该单元格样式
                headStyle = this.getStyle(headStyleMap,"head");
                this.executeCell(fromRowNum, fromColNum, toRowNum, toColNum, content, headStyle ,comment,patr);
                toColNum++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return rowNum;
    }

    private HSSFCellStyle getStyle(HashMap styleMap,String type)
    {
        HSSFCellStyle a_style=wb.createCellStyle();
        a_style.setWrapText(true);//自动换行
        short border = (short) 1;
        short borderColor = HSSFColor.BLACK.index;
        HorizontalAlignment align = HorizontalAlignment.CENTER;
        FillPatternType fillPattern = FillPatternType.SOLID_FOREGROUND;
        short fillForegroundColor = HSSFColor.WHITE.index;
        String fontName = ResourceFactory.getProperty("gz.gz_acounting.m.font");
        int fontSize = 0;//字体大小
        boolean fontBoldWeight = false;
        boolean isFontBold = false;//是否加粗
        if(styleMap != null){//设置了单元格样式
            if(styleMap.get("border") != null)
                border = (Short)styleMap.get("border");//值为-1时则改样式不设置
            if(styleMap.get("borderColor") != null)
                borderColor = (Short)styleMap.get("borderColor");
            if(styleMap.get("align") != null)
                align = (HorizontalAlignment)styleMap.get("align");
            if(styleMap.get("fillForegroundColor") != null)
                fillForegroundColor = (Short)styleMap.get("fillForegroundColor");
            if(styleMap.get("fillPattern") != null)
                fillPattern = (FillPatternType)styleMap.get("fillPattern");
            if(styleMap.get("fontName") != null)
                fontName = (String)styleMap.get("fontName");
            if(styleMap.get("fontSize") != null)
                fontSize = (Integer)styleMap.get("fontSize");
            if(styleMap.get("isFontBold") != null)
                isFontBold = (Boolean)styleMap.get("isFontBold");
        }else{//没有设置单元格样式
            if("head".equals(type)){//默认头部字体是加粗
                isFontBold = true;
            }
        }

        HSSFFont fonttitle = null;
        if("head".equals(type)){
            a_style.setFillPattern(fillPattern);
            a_style.setFillForegroundColor(fillForegroundColor);
            if(fontSize == 0)
                fontSize = 10;
            if(isFontBold)
                fontBoldWeight = true;
            //设置字体
            StringBuffer fontKey = new StringBuffer(fontName);
            fontKey.append("_");
            fontKey.append(fontSize);
            fontKey.append("_");
            fontKey.append(fontBoldWeight);

            if(fontMap.get(fontKey.toString()) == null){
                fonttitle = fonts(fontName, fontSize,fontBoldWeight);
                fontMap.put(fontKey.toString(), fonttitle);
            }else fonttitle = fontMap.get(fontKey.toString());
            a_style.setFont(fonttitle);
        }else if("mergedCell".equals(type)){
            if(border !=-1){
                a_style.setFillPattern(fillPattern);
                a_style.setFillForegroundColor(fillForegroundColor);
            }
            if(fontSize == 0)
                fontSize = 15;
            if(isFontBold)
                fontBoldWeight = true;
            //设置字体
            StringBuffer fontKey = new StringBuffer(fontName);
            fontKey.append("_");
            fontKey.append(fontSize);
            fontKey.append("_");
            fontKey.append(isFontBold);
            if(fontMap.get(fontKey.toString()) == null){
                fonttitle = fonts(fontName, fontSize,fontBoldWeight);
                fontMap.put(fontKey.toString(), fonttitle);
            }else fonttitle = fontMap.get(fontKey.toString());
            a_style.setFont(fonttitle);
        }else{
            if(align == HorizontalAlignment.CENTER)
            	align = HorizontalAlignment.LEFT;
            
            if(fontSize == 0)
                fontSize = 10;
            if(isFontBold)
                fontBoldWeight = true;
            //设置字体
            StringBuffer fontKey = new StringBuffer(fontName);
            fontKey.append("_");
            fontKey.append(fontSize);
            fontKey.append("_");
            fontKey.append(isFontBold);
            if(fontMap.get(fontKey.toString()) == null){
                fonttitle = fonts(fontName, fontSize,fontBoldWeight);
                fontMap.put(fontKey.toString(), fonttitle);
            }else fonttitle = fontMap.get(fontKey.toString());
            a_style.setFont(fonttitle);
        }

        if(border !=-1){
            a_style.setBorderBottom(BorderStyle.valueOf(border));
            a_style.setBottomBorderColor(borderColor);
            a_style.setBorderLeft(BorderStyle.valueOf(border));
            a_style.setLeftBorderColor(borderColor);
            a_style.setBorderRight(BorderStyle.valueOf(border));
            a_style.setRightBorderColor(borderColor);
            a_style.setBorderTop(BorderStyle.valueOf(border));
            a_style.setTopBorderColor(borderColor);
        }
        a_style.setVerticalAlignment(VerticalAlignment.CENTER);
        a_style.setAlignment(align);
        a_style.setLocked(false);
        return a_style;
    }

    /**
     * 设置excel的字体
     * @param workbook
     * @param fonts
     * @param size
     * @return
     */
    private HSSFFont fonts(String fonts, int size, boolean bolderWeight)
    {
        HSSFFont font = wb.createFont();
        font.setFontHeightInPoints((short) size);
        font.setFontName(fonts);
        font.setBold(bolderWeight);// 加粗
        return font;
    }
    public void executeCell(int fromRowNum, int fromColNum, int toRowNum, int toColNum, String content,HSSFCellStyle cellStyle,String comment,HSSFPatriarch patr) {

        HSSFComment comm = null;
        //取得第fromRowNum行
        HSSFRow row = sheet.getRow(fromRowNum);
        if(row==null)
            row = sheet.createRow(fromRowNum);
        //取得fromColNum列的单元格
        HSSFCell cell = row.getCell(fromColNum);
        if(cell==null)
            cell = row.createCell(fromColNum);
        //设置该单元格样式
        cell.setCellStyle(cellStyle);
        //给该单元格赋值
        cell.setCellValue(new HSSFRichTextString(content));
        if(StringUtils.isNotBlank(comment)){//当注释不为空时
            comm = patr.createComment(new HSSFClientAnchor(0, 0, 0, 1, (short) (fromColNum + 1), 0, (short) (fromColNum + 2), 1));
            comm.setString(new HSSFRichTextString(comment));
            cell.setCellComment(comm);
        }

        int fromColNum1 = fromColNum;
        while (++fromColNum1 <= toColNum) {
            cell = row.getCell(fromColNum1);
            if(cell==null)
                cell = row.createCell(fromColNum1);
            cell.setCellStyle(cellStyle);
        }
        for (int fromRowNum1 = fromRowNum + 1; fromRowNum1 <= toRowNum; fromRowNum1++) {
            row = sheet.getRow(fromRowNum1);
            if(row==null)
                row = sheet.createRow(fromRowNum1);
            fromColNum1 = fromColNum;
            while (fromColNum1 <= toColNum) {
                cell = row.getCell(fromColNum1);
                if(cell==null)
                    cell = row.createCell(fromColNum1);
                cell.setCellStyle(cellStyle);
                fromColNum1++;
            }
        }
    }


    /**==================================================================================================*/



//	/**
//	 * @Title: getBatchUpdateSQL
//	 * @Description: TODO(取得 update语句)
//	 * @param oppositeItem
//	 * @param relationItem
//	 * @param gz_tablename
//	 * @throws GeneralException
//	 */
//	private String getBatchUpdateSQL(ArrayList<String> oppositeItem,
//			ArrayList<String> relationItem, String gz_tablename)
//			throws GeneralException {
//		try {
//			StringBuffer sql = new StringBuffer("update " + gz_tablename
//					+ " set ");
//			StringBuffer set = new StringBuffer("");// 要更新的字段
//			StringBuffer where = new StringBuffer("");// 筛选条件
//			int n = 0;
//			for (int i = 0; i < oppositeItem.size(); i++) {
//				/*
//				 * 薪资发放：新建/导入：设置对应指标页面，点击【保存对应】、【没对应数据】、设置方案后点击【确定】都保存报错
//				 * ArrayIndexOutException xiaoyun 2014-9-19 start
//				 */
//				String[] temps = PubFunc.keyWord_reback(oppositeItem.get(i))
//						.split("=");
//				/*
//				 * 薪资发放：新建/导入：设置对应指标页面，点击【保存对应】、【没对应数据】、设置方案后点击【确定】都保存报错
//				 * ArrayIndexOutException xiaoyun 2014-9-19 end
//				 */
//
//				set.append("," + temps[1].trim() + "=?");
//				n++;
//			}
//			sql.append(set.substring(1));
//			sql.append(" where ");
//			// 设置where条件
//			for (int i = 0; i < relationItem.size(); i++) {
//				/*
//				 * 薪资发放：新建/导入：设置对应指标页面，点击【保存对应】、【没对应数据】、设置方案后点击【确定】都保存报错
//				 * ArrayIndexOutException xiaoyun 2014-9-19 start
//				 */
//				String[] temps = PubFunc.keyWord_reback(relationItem.get(i))
//						.split("=");
//				/*
//				 * 薪资发放：新建/导入：设置对应指标页面，点击【保存对应】、【没对应数据】、设置方案后点击【确定】都保存报错
//				 * ArrayIndexOutException xiaoyun 2014-9-19 end
//				 */
//				where.append(" and " + temps[1].trim() + "=?");
//			}
//			sql.append(where.substring(4));
//
//			// 增加导入修改数据的限制
//			sql.append(getImportWhere(salaryid));
//
//			if (n == 0)
//				sql.setLength(0);
//			return sql.toString();
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw GeneralExceptionHandler.Handle(e);
//		}
//	}

    /**
     * @Title: getImportWhere
     * @Description: TODO(获得导入数据的限制条件)
     * @param salaryid
     *            薪资类别
     * @author lis
     * @throws GeneralException
     * @date 2015-7-17 下午06:09:15
     */
    public String getImportWhere(int salaryid) throws GeneralException {
        try {
            StringBuffer sql = new StringBuffer();
            SalaryTemplateBo templateBo = new SalaryTemplateBo(conn, salaryid,
                    userview);
            // 增加导入修改数据的限制
            boolean isShare = true;// 是否共享
            boolean isGZmanager = false;// 如果是共享，是否是该薪资类别的管理员
            boolean isApprove = false;// 是否走审批
            String manager = templateBo.getManager();

            if (manager == null || (manager != null && manager.length() == 0))// 不共享
                isShare = false;

            if (this.userview.getUserName().equals(manager))
                isGZmanager = true;

            String flow_flag = templateBo.getCtrlparam().getValue(
                    SalaryCtrlParamBo.FLOW_CTRL, "flag");
            if ("1".equalsIgnoreCase(flow_flag))
                isApprove = true;

            /** 导入数据 */
            String dbpres = templateBo.getTemplatevo().getString("cbase");
            /** 应用库前缀 */
            String[] dbarr = StringUtils.split(dbpres, ",");
            StringBuffer sub_str = new StringBuffer("");
            // 判断当前用户对应用库是否有权限
            for (int i = 0; i < dbarr.length; i++) {
                String pre = dbarr[i];
                if (!(this.userview.isSuper_admin() || "1".equals(this.userview
                        .getGroupId()))) {
                    if (this.userview.getDbpriv().toString().toLowerCase()
                            .indexOf("," + pre.toLowerCase() + ",") == -1) {
                        sql.append(" and  upper("
                                + templateBo.getGz_tablename() + ".nbase)<>'"
                                + pre.toUpperCase() + "' ");
                    }
                }
            }

            if (isApprove)// 走审批
            {
                sql.append(" and sp_flag in ('01','07')");// 起草或已批
            }else {//不走审批的需要控制提交后不能导入
                if (!this.isAllowEditSubdata()){
                    sql.append(" and sp_flag in ('01','07')");
                }
            }

            if (isShare && isGZmanager == false)// 薪资发放-共享-非管理员
            {

                String whl_str = templateBo.getWhlByUnits(templateBo
                        .getGz_tablename(),true);
                sql.append(" and (sp_flag2 ='01' or sp_flag2='07') ");
                sql.append(whl_str);
            }
            return sql.toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * @Title: isAllowEditSubdata
     * @Description:  薪资发放 是否允许提交后更改数据；具有 “允许提交后更改数据”
     * @param @return
     * @author wangrd
     * @param @throws GeneralException
     * @return boolean
     * @throws
     */
    public boolean isAllowEditSubdata() throws GeneralException
    {
        boolean bAllowEditSubdata=false;
        try
        {
            String allowEditSubdata=getLprogramAttri("allow_edit_subdata",SalaryLProgramBo.CONFIRM_TYPE);
            if(allowEditSubdata==null||allowEditSubdata.trim().length()==0)
                allowEditSubdata="0";

            if ("1".equals(allowEditSubdata)){//允许提交后更改数据 且 具有提交权限,功能授权暂时不区分薪资保险，同薪资发放前台权限
                bAllowEditSubdata=true;
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();

        }
        return bAllowEditSubdata;
    }

    public String getLprogramAttri(String attriName,int nodeValue)
    {
        SalaryTemplateBo salaryTemplateBo = new SalaryTemplateBo(conn, salaryid, userview);
        String value="";
        SalaryLProgramBo lpbo=new SalaryLProgramBo(salaryTemplateBo.getTemplatevo().getString("lprogram"));
        value=lpbo.getValue(nodeValue,attriName);
        return value;
    }









//	/**
//	 * @Title: getAllDataMap
//	 * @Description: TODO(获取该薪资临时表中所有数据)
//	 * @param relationItem
//	 *            关联关系
//	 * @param salaryItemTypeMap
//	 *            薪资项目map
//	 * @param salaryid
//	 *            薪资列表id
//	 * @throws GeneralException
//	 * @author lis
//	 * @date 2015-7-17 下午06:18:30
//	 */
//private HashMap getAllDataMap(ArrayList<String> relationItem,
//			HashMap salaryItemTypeMap, int salaryid) throws GeneralException {
//		HashMap map = new HashMap();
//		RowSet rowSet = null;
//		try {
//			ContentDAO dao = new ContentDAO(this.conn);
//			String[] aim_item = new String[relationItem.size()];
//			SalaryTemplateBo templateBo = new SalaryTemplateBo(conn, salaryid,
//					userview);
//			StringBuffer select_str = new StringBuffer("");
//
//			for (int i = 0; i < relationItem.size(); i++) {
//				String temp = relationItem.get(i);
//				temp = PubFunc.keyWord_reback(temp);
//				String[] temps = temp.split("=");
//				aim_item[i] = temps[1].trim();
//				select_str.append("," + temps[1].trim().trim());
//			}
//
//			if (select_str.length() > 1) {
//				String gzTableName = templateBo.getGz_tablename();
//				String sql = "select * from " + gzTableName + " where ( 1=1 ) ";
//				String manager = templateBo.getManager();
//				String where = getImportWhere(salaryid);
//				sql += where;
//
//				String dataSql = sql.replace("*", select_str.substring(1));
//				String countSql = sql.replace("*", "count(*)");
//
//				rowSet = dao.search(countSql);
//				if (rowSet.next()) {
//					int num = rowSet.getInt(1);
//					if (num > 6000)
//						return null;
//				}
//				StringBuffer temp = new StringBuffer("");
//				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
//				rowSet = dao.search(dataSql);
//				while (rowSet.next()) {
//					temp.setLength(0);
//					for (int i = 0; i < relationItem.size(); i++) {
//						LazyDynaBean columnBean = (LazyDynaBean) salaryItemTypeMap
//								.get(aim_item[i].trim());
//						String decwidth = (String) columnBean.get("decwidth");
//						String itemtype = (String) columnBean.get("itemtype");
//						if (itemtype.equals("D")) {
//							if (rowSet.getDate(i + 1) != null)
//								temp.append("/"
//										+ sf.format(rowSet.getDate(i + 1)));
//							else
//								temp.append("/");
//						} else
//							temp.append("/" + rowSet.getString(i + 1));
//					}
//
//					map.put(temp.toString(), "1");
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw GeneralExceptionHandler.Handle(e);
//		} finally {
//			PubFunc.closeDbObj(rowSet);
//		}
//		return map;
//	}

    /**
     * @Title: getCalcuItemMap
     * @Description: TODO(公式计算项)
     * @return HashMap
     * @throws GeneralException
     * @author lis
     * @date 2015-7-22 下午03:31:19
     */
    public HashMap getCalcuItemMap() throws GeneralException {
        RowSet rowSet = null;
        try {
            HashMap calcuItemMap = new HashMap();// 公式计算项
            // 公式计算项
            ArrayList list = new ArrayList();
            list.add(salaryid);
            String sql = "select itemname from salaryformula  where salaryid=?";
            ContentDAO dao = new ContentDAO(conn);
            rowSet = dao.search(sql, list);
            while (rowSet.next()) {
                String itemid = rowSet.getString("itemname");
                calcuItemMap.put(itemid.toLowerCase(), itemid);
            }
            return calcuItemMap;

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeResource(rowSet);
        }
    }

    public String exportExcel() {

        return null;
    }

    /**
     * @Title: getHeadList
     * @Description: TODO(得到要导出模板的列名相关数据)
     * @param headItemList
     *            可以导出的薪资项目列表
     * @param onlyname
     *            唯一性标识
     * @param _flag
     *            薪资项目中是否存在唯一性标识
     * @param royalty_valid
     *            是否是提成工资
     * @return ArrayList<LazyDynaBean>
     * @throws GeneralException
     * @author lis
     * @date 2015-7-23 上午10:09:26
     */
    public ArrayList<LazyDynaBean> getHeadList(
            ArrayList<LazyDynaBean> headItemList, String onlyname,
            boolean _flag, String royalty_valid) throws GeneralException {
        try {

            LazyDynaBean bean = null;
            ArrayList<LazyDynaBean> headList = new ArrayList<LazyDynaBean>();
            // 北京移动 下载模板不要下载主键标识串
            if (!(SystemConfig.getPropertyValue("clientName") != null && "bjyd".equalsIgnoreCase(SystemConfig
                    .getPropertyValue("clientName")))
                    && (onlyname == null || onlyname.trim().length() == 0 || _flag)
                    && !"1".equals(royalty_valid)) {
                HashMap headStyleMap = new HashMap();
                headStyleMap.put("columnWidth", 6800);
                headStyleMap.put("fillForegroundColor", HSSFColor.GREEN.index);
                bean = new LazyDynaBean();
                bean.set("headStyleMap", headStyleMap);// 列头样式
                bean.set("content", ResourceFactory.getProperty("gz_new.gz_primarKey"));// 列头名称:主键标识串
                bean.set("itemid", "key_id");// 列头代码
                bean.set("comment",  ResourceFactory.getProperty("gz_new.gz_primarKey"));// 列头注释:主键标识串
                bean.set("colType", "A");// 该列数据类型
                bean.set("columnHidden", true);
                headList.add(bean);
            }

            if ("1".equals(royalty_valid)) // 如果是提成工资
            {
                if (onlyname.length() == 0)
                    throw new GeneralException(ResourceFactory.getProperty("gz_new.gz_accounting.no_onlyname"));//系统没有设置唯一性指标
                boolean flag = false;
                for (int i = 0; i < headItemList.size(); i++) {
                    bean = (LazyDynaBean) headItemList.get(i);
                    if (((String) bean.get("itemid"))
                            .equalsIgnoreCase(onlyname))
                        flag = true;
                }
                if (!flag)
                    throw new GeneralException(ResourceFactory.getProperty("gz_new.gz_accounting.gz_no_onlyname"));//薪资类别没有设置唯一性指标项目

            }
            String fieldExplain = "";
            for (int i = 0; i < headItemList.size(); i++) {
                bean = new LazyDynaBean();
                LazyDynaBean headBean = headItemList.get(i);
                String itemid = (String) headBean.get("itemid");//薪资项目代码
                String itemdesc = (String) headBean.get("itemdesc");//薪资项目名称
                String itemtype = ((String) headBean.get("itemtype"));//薪资项目数据类型
                String codesetid = (String) headBean.get("codesetid");// 薪资项目是代码类时代码类id
                String decwidth = (String) headBean.get("decwidth");//小数点位数
                String itemlength = (String) headBean.get("itemlength");//长度
                //格式化日期格式
                if("D".equalsIgnoreCase(itemtype)) {
                	
                	bean.set("dateFormat", getDateFormatFromLength(Integer.parseInt(itemlength), 0).replace(".", "-"));
                }
                //姓名、名称、单位名称列锁定，不可编辑
                if("a0101".equals(itemid.toLowerCase()) || "e0122".equals(itemid.toLowerCase()) || "b0110".equals(itemid.toLowerCase()))
                    bean.set("columnLocked", true);

                int columnWidth = 0;
                //指标描述
                fieldExplain = DataDictionary.getFieldItem(itemid).getExplain();
                if (SystemConfig.getPropertyValue("excel_template_desc") != null
                        && "true"
                        .equalsIgnoreCase(SystemConfig.getPropertyValue("excel_template_desc"))
                        && fieldExplain != null
                        && fieldExplain.trim().length() > 0) {
                    itemdesc += "\r\n如：" + fieldExplain;
                    columnWidth = 5000;
                }else if ("UN".equalsIgnoreCase(codesetid)
                        || "UM".equalsIgnoreCase(codesetid)
                        || "@k".equalsIgnoreCase(codesetid)) {
                    columnWidth = 5000;
                }else if(headBean.get("displaywidth")!=null&&StringUtils.isNotBlank(headBean.get("displaywidth").toString())){
                    columnWidth=Integer.parseInt(headBean.get("displaywidth").toString());
                }

                HashMap headStyleMap = new HashMap();
                headStyleMap.put("columnWidth", columnWidth);
                headStyleMap.put("fillForegroundColor", HSSFColor.LIGHT_GREEN.index);//设置列头为绿色
                HashMap colStyleMap = new HashMap();
                colStyleMap.put("columnWidth", headBean.get("displaywidth"));
                colStyleMap.put("align", headBean.get("align"));
                bean.set("colStyleMap", colStyleMap);// 该列宽度

                bean.set("headStyleMap", headStyleMap);// 列头样式
                bean.set("content", itemdesc);// 列头名称
                bean.set("itemid", itemid);// 列头代码
                bean.set("codesetid", codesetid);// 列头代码
                bean.set("comment", itemid);// 列头注释
                bean.set("decwidth", decwidth);// 列小数点后面位数
                bean.set("colType", itemtype);// 该列数据类型
                bean.set("specialType", "1");//薪资下载模板传参的特殊处理，配置了该参数，会在单位部门名称前加代码id 例：1002:XX大学sunjian 2017-7-5  1：代表启用
                headList.add(bean);
            }
            return headList;
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * @Title: getCodeCols
     * @Description: TODO(得到要标记下拉数据的指标)
     * @param headItemList
     *            要导出的指标集合
     * @return ArrayList<String>
     * @throws GeneralException
     * @author lis
     * @date 2015-7-23 下午01:38:00
     */
    public ArrayList<String> getCodeCols(ArrayList<LazyDynaBean> headItemList)
            throws GeneralException {
        try {
            ArrayList codeCols = new ArrayList();
            //HashMap codeSize=new HashMap();
            ContentDAO dao = new ContentDAO(this.conn);
            for (int i = 0; i < headItemList.size(); i++) {
                LazyDynaBean headBean = headItemList.get(i);
                String itemid = (String) headBean.get("itemid");
                String codesetid = (String) headBean.get("codesetid");// 代码类id
                if (!"0".equals(codesetid) && StringUtils.isNotBlank(codesetid)) {

						/*int size=0;
						if(codeSize.get(codesetid.toLowerCase())!=null)
							size=Integer.parseInt((String)codeSize.get(codesetid.toLowerCase()));
						else
						{
							size=getCodeSize(itemid,codesetid.toLowerCase(),dao);
							codeSize.put(codesetid.toLowerCase(), size+"");
						}
						if(size<50)*/ //代码项个数大于50 或 没有写权限都不生成代码数据

                    codeCols.add(itemid + ":" + codesetid);
                }
            }
            return codeCols;
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }



    /**
     * 获得代码项个数
     * @param field
     * @param codesetid
     * @param dao
     * @return
     */
    private int getCodeSize(String itemid,String codesetid,ContentDAO dao)
    {


        int size=0;
        FieldItem item=DataDictionary.getFieldItem(itemid);
        if(item!=null)
        {
            String pri = this.userview.analyseFieldPriv(itemid);
            if (!"2".equals(pri))// 没有写权限
                return 10000;
        }


        StringBuffer codeBuf=new StringBuffer("");
        if (!"UM".equalsIgnoreCase(codesetid) && !"UN".equalsIgnoreCase(codesetid) && !"@K".equalsIgnoreCase(codesetid))
        {
            codeBuf.append("select count(*) from codeitem where lower(codesetid)='" + codesetid + "'");
        } else
        {
            if (!"UN".equalsIgnoreCase(codesetid))
            {
                if("UM".equalsIgnoreCase(codesetid))
                    codeBuf.append("select count(*) from organization where ( codesetid='UM' OR codesetid='UN' ) ");
                else
                    codeBuf.append("select count(*) from organization where lower(codesetid)='" + codesetid
                            + "' and  codeitemid not in (select parentid from organization where lower(codesetid)='" + codesetid + "')");
            }
            else if ("UN".equalsIgnoreCase(codesetid))
            {
                codeBuf.append("select count(*) from organization where codesetid='UN'");
            }
        }
        RowSet rowSet=null;
        try
        {
            rowSet=dao.search(codeBuf.toString());
            if(rowSet.next())
                size=rowSet.getInt(1);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            PubFunc.closeDbObj(rowSet);
        }
        return size;
    }







    /**
     * @Title: getHeadItemList
     * @Description: TODO(得到显示的薪资项目)
     * @param itemSetList 所有薪资项目
     * @param column 列
     * @return ArrayList<LazyDynaBean>
     * @throws GeneralException
     * @author lis
     * @date 2015-7-24 下午03:38:21
     */
    public ArrayList<LazyDynaBean> getHeadItemList(ArrayList itemSetList,
                                                   ArrayList<ColumnsInfo> column) throws GeneralException {
        ArrayList<LazyDynaBean> itemList = new ArrayList<LazyDynaBean>();
        try {
            //可以显示的薪资项目代码集合
            ArrayList<String> itemids = new ArrayList<String>();
            for(int i=0;i<column.size();i++){
                if(column.get(i).getLoadtype()!=ColumnsInfo.LOADTYPE_BLOCK)
                    continue;
                else itemids.add(column.get(i).getColumnId());
            }

            // 进行过滤得到薪资项目
            for (int i = 0; i < itemSetList.size(); i++) {
                LazyDynaBean bean = (LazyDynaBean) itemSetList.get(i);
                if (itemids.contains(((String) bean.get("itemid"))
                        .toLowerCase()))
                    itemList.add(bean);
                else
                    continue;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return itemList;
    }


    /**
     * @Title: getExportData
     * @Description: TODO(得到导出数据)
     * @param headList
     *            导出excel列名
     * @param sql
     *            导出数据sql
     * @param onlyname
     *            唯一性标识
     * @param _flag
     *            在导出列名中是否有唯一性标识
     * @param royalty_valid
     *            是否是提成工资
     * @return ArrayList<LazyDynaBean>
     * @throws SQLException
     * @author lis
     * @throws GeneralException
     * @date 2015-7-23 下午01:24:43
     */
    public ArrayList<LazyDynaBean> getExportData(
            ArrayList<LazyDynaBean> headList, String sql) throws SQLException, GeneralException {

        try {
            ContentDAO dao = new ContentDAO(this.conn);
            LazyDynaBean rowDataBean = null;
            LazyDynaBean dataBean = null;
            LazyDynaBean bean = new LazyDynaBean();
            Date d = null;
            ArrayList<LazyDynaBean> dataList = new ArrayList<LazyDynaBean>();
            String itemid = "";
            String itemtype = "";
            String itemdesc = "";
            String codesetid = "";
            int decwidth = 0;
            String dateFormat = "";
            SimpleDateFormat df = null;
            RowSet rowSet = dao.search(sql);
            dao.search(sql);

            while (rowSet.next()) {

                rowDataBean = new LazyDynaBean();
                String nASE = rowSet.getString("NBASE");
                String a0100 = rowSet.getString("A0100");
                String a00Z0 = rowSet.getDate("A00Z0").toString();
                String a00Z1 = rowSet.getString("A00Z1");
                String key_value = nASE + "|" + a0100 + "|" + a00Z0 + "|" + a00Z1;
                for (int i = 0; i < headList.size(); i++) {

                    bean = (LazyDynaBean) headList.get(i);
                    itemid = (String) bean.get("itemid");
                    dataBean = new LazyDynaBean();

                    dataBean.set("key_id", key_value);
                    if ("key_id".equals(itemid)) {
                        rowDataBean.set("key_id", dataBean);
                        continue;
                    }
                    itemtype = (String) bean.get("colType");
                    itemdesc = (String) bean.get("content");
                    codesetid = (String) bean.get("codesetid");// 代码类id
                    decwidth = (Integer) bean.get("decwidth");
                    dateFormat = (String) bean.get("dateFormat");
                    if (StringUtils.isEmpty(codesetid))
                        codesetid = "0";
                    if ("D".equals(itemtype)) {
                        // 日期型
                        if (StringUtils.isEmpty(dateFormat))
                            df = new SimpleDateFormat("yyyy-MM-dd");
                        else
                            df = new SimpleDateFormat(dateFormat);
                        d = null;
                        d = rowSet.getDate(itemid);
                        if (d != null)
                            dataBean.set("content", df.format(d));
                        else
                            dataBean.set("content", "");
                        rowDataBean.set(itemid, dataBean);
                    } else if ("M".equals(itemtype)) {
                        // 是备注型
                        dataBean.set("content", Sql_switcher.readMemo(rowSet, itemid));
                        rowDataBean.set(itemid, dataBean);
                    } else if ("A".equals(itemtype) && !"0".equals(codesetid)) {
                        // 是代码类
                        String value = rowSet.getString(itemid);
                        if (value != null) {
                            if(",b0110,e0122,e01a1,".indexOf(","+itemid.toLowerCase()+",") != -1)
                                dataBean.set("content", StringUtils.isEmpty(AdminCode
                                        .getCodeName(codesetid, value)) ? value
                                        : AdminCode.getCodeName(codesetid, value));
                            else if (!"UM".equals(codesetid) && !"UN".equals(codesetid) && !"@K".equalsIgnoreCase(codesetid))
                                dataBean.set("content", StringUtils.isEmpty(AdminCode
                                        .getCodeName(codesetid, value)) ? value
                                        : AdminCode.getCodeName(codesetid, value));
                            else
                                dataBean.set("content", StringUtils.isEmpty(AdminCode
                                        .getCodeName(codesetid, value)) ? value
                                        : value+":"+AdminCode.getCodeName(codesetid, value));
                        } else {
                            dataBean.set("content", "");
                        }
                        rowDataBean.set(itemid, dataBean);
                    } else if ("N".equals(itemtype)) {
                        // 数字型
                        if (rowSet.getString(itemid) != null) {
                            dataBean.set("content", PubFunc.round(rowSet.getString(itemid), decwidth));
                        } else
                            dataBean.set("content", "");
                        rowDataBean.set(itemid, dataBean);
                    } else {
                        if (rowSet.getString(itemid) != null)
                            dataBean.set("content", rowSet.getString(itemid));
                        else
                            dataBean.set("content", "");
                        rowDataBean.set(itemid, dataBean);
                    }
                }
                dataList.add(rowDataBean);
            }
            try {
                rowSet.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return dataList;
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

    }

    /**
     * @Title: getDropDataList
     * @Description: TODO(得到下拉数据)
     * @param codeCols
     *            拥有下拉数据的指标代码集合
     * @throws GeneralException
     * @author lis
     * @date 2015-7-23 下午01:23:36
     */
    public HashMap getDropDataList(ArrayList codeCols) throws GeneralException {
        RowSet rowSet = null;
        try {
            HashMap dropDownMap = new HashMap();
            ContentDAO dao = new ContentDAO(this.conn);

            for (int n = 0; n < codeCols.size(); n++) {
                String codeCol = (String) codeCols.get(n);
                String[] temp = codeCol.split(":");
                String itemid = temp[0];//指标代号

                if(",b0110,e0122,e01a1,".indexOf(","+itemid.toLowerCase()+",") != -1)
                    continue;
                String codesetid = temp[1];//指标是代码类的id
                StringBuffer codeBuf = new StringBuffer();
                if (!"UM".equals(codesetid) && !"UN".equals(codesetid)
                        && !"@K".equalsIgnoreCase(codesetid)) {
                    codeBuf
                            .append("select codesetid,codeitemid,codeitemdesc from codeitem where codesetid='"
                                    + codesetid + "'");
                } else {

                    String where="";
                    if(!this.userview.isSuper_admin()&&!"1".equals(this.userview.getGroupId()))
                    {
                        String b_units=this.userview.getUnitIdByBusiOutofPriv("1");// 1:工资发放  2:工资总额  3:所得税
                        if(b_units.length()==0)
                            where=" and 1=2";
                        else
                        {
                            if(b_units!=null&&b_units.length()>2&&!"UN".equalsIgnoreCase(b_units)&&!"UN`".equalsIgnoreCase(b_units)) //模块操作单位
                            {
                                String[] unitarr =b_units.split("`");
                                for(int i=0;i<unitarr.length;i++)
                                {
                                    String codeid=unitarr[i];
                                    if(codeid==null|| "".equals(codeid))
                                        continue;
                                    if(codeid!=null&&codeid.trim().length()>2)
                                    {
                                        String privCode = codeid.substring(0,2);
                                        String privCodeValue = codeid.substring(2);
                                        where+=" or  codeitemid like '"+privCodeValue+"%'";
                                    }
                                }
                                if(where.length()>3)
                                    where=" and ( "+where.substring(3)+" )";
                            }
                        }
                    }

                    if (!"UN".equals(codesetid)) {
                        if ("UM".equalsIgnoreCase(codesetid))
                            codeBuf
                                    .append("select codesetid,codeitemid,codeitemdesc from organization where "+Sql_switcher.sqlNow()+" BETWEEN START_DATE and END_DATE and codesetid='UM' "+where
                                            + " order by codeitemid");
                        else
                            codeBuf
                                    .append("select codesetid,codeitemid,codeitemdesc from organization where "+Sql_switcher.sqlNow()+" BETWEEN START_DATE and END_DATE and codesetid='"
                                            + codesetid
                                            + "' and  codeitemid not in (select parentid from organization where "+Sql_switcher.sqlNow()+" BETWEEN START_DATE and END_DATE and codesetid='"
                                            + codesetid + "')"+where);

                    } else if ("UN".equals(codesetid)) {
                        codeBuf.append("select count(*) from organization where codesetid='UN'");
                        rowSet = dao.search(codeBuf.toString());
                        if (rowSet.next())
                            if (rowSet.getInt(1) == 1) {
                                codeBuf.setLength(0);
                                codeBuf
                                        .append("select codesetid,codeitemid,codeitemdesc from organization where "+Sql_switcher.sqlNow()+" BETWEEN START_DATE and END_DATE and codesetid='UN' "+where);
                            } else if (rowSet.getInt(1) > 1) {
                                codeBuf.setLength(0);
                                codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization where "+Sql_switcher.sqlNow()+" BETWEEN START_DATE and END_DATE and codesetid='"
                                        + codesetid
                                        + "' "+where);
                                codeBuf.append(" union all ");
                                codeBuf
                                        .append("select codesetid,codeitemid,codeitemdesc from organization where "+Sql_switcher.sqlNow()+" BETWEEN START_DATE and END_DATE and codesetid='UN' and  codeitemid=parentid and childid in (select codeitemid from organization where codesetid!='UN') "+where);
                            }
                    }
                }

                rowSet = dao.search(codeBuf.toString());

                ArrayList list = new ArrayList();
                int m = 0;
                String theCodename = "";
                String codeitemid = "";
                String codeitem = "";
                //该指标的parentid
                String pcodeitem = "";
                //判断其显示几级的部门单位
                Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
                String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
                if(StringUtils.isBlank(display_e0122)|| "00".equals(display_e0122))
                    display_e0122="0";
                while (rowSet.next()) {

                    if (!"UM".equals(codesetid) && !"UN".equals(codesetid)&& !"@K".equalsIgnoreCase(codesetid))
                        list.add(rowSet.getString("codeitemdesc"));
                    else {
                        codeitemid = rowSet.getString("codeitemid");
                        CodeItem item = AdminCode.getCode(codesetid,codeitemid,Integer.parseInt(display_e0122));
                        if(item != null) {
                            theCodename = item.getCodename();
                            //部门的时候判断是否需要显示单位，如果设置了参数为显示2级部门，且theCodename也显示了二级部门则不添加单位，否则添加单位
                            if(!"UN".equals(codesetid) && theCodename.split(AdminCode.dept_seq).length-1 != Integer.parseInt(display_e0122)) {
                                pcodeitem = item.getPcodeitem();
                                item = AdminCode.getCode("UN",pcodeitem,Integer.parseInt(display_e0122));
                                if(item!=null)
                                    theCodename = item.getCodename() + "-" + theCodename;
                            }

                        }else
                            theCodename = AdminCode.getCodeName(codesetid,codeitemid);
                        list.add(rowSet.getString("codeitemid") + ":"
                                + theCodename);
                    }
                }
                dropDownMap.put(itemid, list);
            }
            return dropDownMap;
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeResource(rowSet);
        }
    }

    /**
     * 导入时删除不在条件范围中的人员
     * @param tableName
     * @author dengcan
     */
    public int delNoConditionData(String tablename,HashMap insertRecordMap)
    {
        int num=0;
        try
        {
            SalaryTemplateBo salaryTemplateBo = new SalaryTemplateBo(conn, salaryid, userview);
            ContentDAO dao=new ContentDAO(this.conn);
            RowSet rowSet=null;
            /**导入数据*/
            String dbpres=salaryTemplateBo.getTemplatevo().getString("cbase");
            /**应用库前缀*/
            String[] dbarr=StringUtils.split(dbpres, ",");
            String flag=salaryTemplateBo.getCtrlparam().getValue(SalaryCtrlParamBo.COND_MODE,"flag");  // "":没条件 0：简单条件 1：复杂条件
            String aflag=salaryTemplateBo.getCtrlparam().getValue(SalaryCtrlParamBo.PRIV_MODE,"flag");  // 人员范围权限过滤标志  1：有

            String cond=salaryTemplateBo.getTemplatevo().getString("cond");
            String cexpr=salaryTemplateBo.getTemplatevo().getString("cexpr");
            String sql="";
            ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
            HashSet keySet=new HashSet();
            SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
            for(int i=0;i<dbarr.length;i++)
            {
                String pre=dbarr[i];

                if(aflag!=null&& "1".equals(aflag))
                {
                    String asql="select a00z0,a00z1,nbase,a0100 from "+tablename+" where upper(nbase)='"+pre.toUpperCase()+"' and a0100 not in (select a0100 "+this.userview.getPrivSQLExpression(pre, false)+" )";
                    rowSet=dao.search(asql);
                    while(rowSet.next())
                    {
                        String key=pre.toLowerCase()+"|"+rowSet.getString("a0100")+"|"+df.format(rowSet.getDate("a00z0"))+"|"+rowSet.getString("a00z1");
                        keySet.add(key);
                    }

                    asql="delete from "+tablename+" where upper(nbase)='"+pre.toUpperCase()+"' and a0100 not in (select a0100 "+this.userview.getPrivSQLExpression(pre, false)+" )";
                    dao.delete(asql,new ArrayList());
                }

                if(flag!=null&& "0".equals(flag)&&cond.length()>0)  //0：简单条件
                {
                    FactorList factor = new FactorList(cexpr, cond,pre, false, false, true, 1, "su");
                    String strSql ="";
                    if(factor.size()>0)
                    {
                        strSql=factor.getSqlExpression();

                        sql="select a00z0,a00z1,nbase,a0100 from "+tablename+" where upper(nbase)='"+pre.toUpperCase()+"' and a0100 ";
                        sql+="not in (select "+pre+"a01.a0100 "+strSql+" )";
                        rowSet=dao.search(sql);
                        while(rowSet.next())
                        {
                            String key=pre.toLowerCase()+"|"+rowSet.getString("a0100")+"|"+df.format(rowSet.getDate("a00z0"))+"|"+rowSet.getString("a00z1");
                            keySet.add(key);
                        }

                        sql="delete from "+tablename+" where upper(nbase)='"+pre.toUpperCase()+"' and a0100 ";
                        sql+="not in (select "+pre+"a01.a0100 "+strSql+" )";
                        dao.delete(sql,new ArrayList());
                    }
                }
                if(flag!=null&& "1".equals(flag)&&cond.length()>0)  // 1：复杂条件
                {

                    int infoGroup = 0; // forPerson 人员
                    int varType = 8; // logic

                    String whereIN="select a0100 from "+pre+"A01";
                    alUsedFields.addAll(salaryTemplateBo.getMidVariableList(this.salaryid+""));
                    YksjParser yp = new YksjParser(this.userview ,alUsedFields,
                            YksjParser.forSearch, varType, infoGroup, "Ht",pre.toString());
                    YearMonthCount ymc=null;
                    yp.run_Where(cond, ymc,"","hrpwarn_result", dao, whereIN,this.conn,"A", null);
                    String tempTableName = yp.getTempTableName();
                    String w = yp.getSQL();
                    if(w!=null&&w.trim().length()>0)
                    {

                        sql="select a00z0,a00z1,nbase,a0100 from "+tablename+" where upper(nbase)='"+pre.toUpperCase()+"' and a0100 ";
                        sql+="not ";
                        sql+=" in (select a0100 from "+tempTableName+" where "+w+" )";
                        rowSet=dao.search(sql);
                        while(rowSet.next())
                        {
                            String key=pre.toLowerCase()+"|"+rowSet.getString("a0100")+"|"+df.format(rowSet.getDate("a00z0"))+"|"+rowSet.getString("a00z1");
                            keySet.add(key);
                        }

                        sql="delete from "+tablename+" where upper(nbase)='"+pre.toUpperCase()+"' and a0100 ";
                        sql+="not ";
                        sql+=" in (select a0100 from "+tempTableName+" where "+w+" )";
                        dao.delete(sql,new ArrayList());
                    }
                }
            }

            for(Iterator t=keySet.iterator();t.hasNext();)
            {
                String str=(String)t.next();
                if(insertRecordMap.get(str)!=null)
                    num++;
            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return num;
    }

    /**
     * @author lis
     * @Description: 同步临时表
     * @date 2016-1-7
     * @param whl_str
     * @param update_str
     * @param salaryid
     * @throws GeneralException
     */
    public void synTempData(String whl_str,String update_str,String salaryid) throws GeneralException
    {
        RowSet rowSet = null;
        try
        {
            ContentDAO dao=new ContentDAO(this.conn);
            StringBuffer sql2=new StringBuffer("select distinct userflag from salaryhistory where 1=1 ");
            sql2.append(whl_str);
            rowSet = dao.search(sql2.toString());
            while(rowSet.next())
            {
                String userflag=rowSet.getString("userflag");
                if(userflag==null||userflag.trim().length()==0)
                    continue;
                String primitiveDataTable=userflag+"_salary_"+salaryid;

                StringBuffer sql0=new StringBuffer("");
                if(Sql_switcher.searchDbServer()==Constant.ORACEL)
                {

                    String[] temps=update_str.split(",");
                    StringBuffer str1=new StringBuffer("");
                    StringBuffer str2=new StringBuffer("");
                    for(int i=0;i<temps.length;i++)
                    {
                        if(temps[i]!=null&&temps[i].trim().length()>0)
                        {
                            str1.append(","+primitiveDataTable+"."+temps[i]);
                            str2.append(",salaryhistory."+temps[i]);
                        }
                    }

                    sql0.append("update "+primitiveDataTable+" set ("+str1.substring(1)+")=(select  "+str2.substring(1)+"  from salaryhistory where ");
                    sql0.append("  salaryhistory.a0100="+primitiveDataTable+".a0100 and  upper(salaryhistory.nbase)=upper("+primitiveDataTable+".nbase) and  salaryhistory.a00z0="+primitiveDataTable+".a00z0 and  salaryhistory.a00z1="+primitiveDataTable+".a00z1 and salaryid='"+salaryid+"' ");
                    sql0.append("  "+whl_str+" ) where exists (select null  from salaryhistory where ");
                    sql0.append(" salaryhistory.a0100="+primitiveDataTable+".a0100 and  upper(salaryhistory.nbase)=upper("+primitiveDataTable+".nbase) and  salaryhistory.a00z0="+primitiveDataTable+".a00z0 and  salaryhistory.a00z1="+primitiveDataTable+".a00z1  and salaryid='"+salaryid+"' "+whl_str+" )");
                }
                else
                {
                    String[] temps=update_str.split(",");
                    StringBuffer str1=new StringBuffer("");
                    for(int i=0;i<temps.length;i++)
                    {
                        if(temps[i]!=null&&temps[i].trim().length()>0)
                        {
                            str1.append(","+primitiveDataTable+"."+temps[i]+"=salaryhistory."+temps[i]);
                        }
                    }

                    sql0.append("update  "+primitiveDataTable+"   set  "+str1.substring(1));
                    sql0.append(" from   salaryhistory");
                    sql0.append(" where  salaryhistory.a0100="+primitiveDataTable+".a0100 and ");
                    sql0.append(" upper(salaryhistory.nbase)=upper("+primitiveDataTable+".nbase) ");
                    sql0.append(" and  salaryhistory.a00z0="+primitiveDataTable+".a00z0 and ");
                    sql0.append(" salaryhistory.a00z1="+primitiveDataTable+".a00z1  and salaryid='"+salaryid+"'  "+whl_str);

                }
                dao.update(sql0.toString());
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally{
            PubFunc.closeResource(rowSet);
        }
    }

    /**
     * 将字符串转换为指定时间格式，若字符串不是时间格式，则返回null
     * @param strDate 时间
     * @param itemlength 字段长度
     * @return 转换后的字符串
     * @author zhanghua
     * @date 2017年6月27日 下午2:09:46
     */
    private String checkDateFormat(String strDate,int itemlength){
        Date date=new Date();

        SimpleDateFormat dateFormat=new SimpleDateFormat(this.getDateFormatFromLength(itemlength,0));
        try{
            if(StringUtils.isBlank(strDate))
                return null;
            strDate=strDate.replace('-', '.');
            date =dateFormat.parse(strDate);

        }catch(Exception e){
            return null;
        }
        return dateFormat.format(date);
    }
    /**
     * 获取时间格式
     * @param itemlength
     * @param type 0：java格式化使用 ； 1：数据库格式化使用
     * @return
     * @author zhanghua
     * @date 2017年6月27日 下午4:58:42
     */
    private String getDateFormatFromLength(int itemlength,int type){
        String format="";
        if(type==0 ||(type==1 &&Sql_switcher.searchDbServer()==Constant.MSSQL)){
            switch(itemlength){
                case 4:format="yyyy";break;
                case 7:format="yyyy.MM";break;
                case 10:format="yyyy.MM.dd";break;
                case 16:format="yyyy.MM.dd HH:mm";break;
                case 18:format="yyyy.MM.dd HH:mm:ss";break;
                default :format="yyyy.MM.dd";
            }
        }else {
            if(Sql_switcher.searchDbServer()==Constant.ORACEL){
                switch(itemlength){
                    case 4:format="YYYY";break;
                    case 7:format="YYYY.MM";break;
                    case 10:format="YYYY.MM.DD";break;
                    case 16:format="YYYY.MM.DD HH24:mi";break;
                    case 18:format="YYYY.MM.DD HH24:mi:ss";break;
                    default :format="YYYY.MM.DD";
                }
            }
        }
        return format;
    }
    /**
     * @param conn
     *            the conn to set
     */
    public void setConn(Connection conn) {
        this.conn = conn;
    }

    /**
     * @return the conn
     */
    public Connection getConn() {
        return conn;
    }

    /**
     * @param salaryid
     *            the salaryid to set
     */
    public void setSalaryid(int salaryid) {
        this.salaryid = salaryid;
    }

    /**
     * @return the salaryid
     */
    public int getSalaryid() {
        return salaryid;
    }

    /**
     * @param userview
     *            the userview to set
     */
    public void setUserview(UserView userview) {
        this.userview = userview;
    }

    /**
     * @return the userview
     */
    public UserView getUserview() {
        return userview;
    }
}
