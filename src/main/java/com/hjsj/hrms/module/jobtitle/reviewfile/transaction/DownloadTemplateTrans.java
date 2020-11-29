package com.hjsj.hrms.module.jobtitle.reviewfile.transaction;

import com.hjsj.hrms.module.jobtitle.reviewfile.businessobject.ExportBo;
import com.hjsj.hrms.module.jobtitle.reviewfile.businessobject.GenerateAcPwBo;
import com.hjsj.hrms.module.jobtitle.reviewfile.businessobject.ReviewFileBo;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PinyinUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * <p>Title: DownloadTemplateTrans </p>
 * 上会材料-导入数据-下载模板
 * <p>create time  2016-5-23 上午10:46:31</p>
 * @author linbz
 */
@SuppressWarnings("serial")
public class DownloadTemplateTrans extends IBusiness{

    @SuppressWarnings("unchecked")
    @Override
    public void execute() throws GeneralException {

        try {
            ExportBo exBo = new ExportBo(this.getFrameconn(), this.userView);
            
            ArrayList idlist = (ArrayList) this.getFormHM().get("idlist");
            
            String isSelectAll = (String) this.getFormHM().get("isSelectAll");
            isSelectAll = "".equals(isSelectAll) ? "0" : isSelectAll;
            
            String meettingName = (String) this.getFormHM().get("meettingName");//会议名称
            meettingName = StringUtils.isBlank(meettingName)?"" : meettingName;
            
            GenerateAcPwBo generateAcPwBo = new GenerateAcPwBo(this.frameconn, this.userView);
            ArrayList<HashMap<String, String>> selList = new ArrayList<HashMap<String, String>>();
            selList = generateAcPwBo.getSelectList(isSelectAll, idlist);//实际选中的数据
            
            String reviewPersonIds = "";//选中的评审编号，全选时为空
            for (int i = 0; i < selList.size(); i++) {
                HashMap<String, String> map = (HashMap<String, String>) selList.get(i);
                String reviewPersonId = map.get("w0501");
                reviewPersonIds += "'" + reviewPersonId + "',";
            }
            String w0301 = "";
            if(selList.size()>0){
            	w0301 = selList.get(0).get("w0301");
            }
            if (StringUtils.isNotEmpty(reviewPersonIds))
                reviewPersonIds = reviewPersonIds.substring(0,reviewPersonIds.length()-1);
            
            ExportExcelUtil excelUtil = new ExportExcelUtil(this.getFrameconn());//实例化导出Excel工具类
            TableDataConfigCache catche = (TableDataConfigCache)this.userView.getHm().get("reviewFile");
			HashMap columnMap = catche.getColumnMap();
            
            ReviewFileBo reviewFileBo = new ReviewFileBo(this.frameconn, this.userView);// 工具类
            ArrayList<ColumnsInfo> columnsInfo = new ArrayList<ColumnsInfo>();//reviewFileBo.getColumnList(ReviewFileTrans.exceptFields, ReviewFileTrans.notEditFields, ReviewFileTrans.islock);//根据页面展示得到列
            for(ColumnsInfo c : columnsInfo) {
            	String itmeid = c.getColumnId();
            	if(columnMap.containsKey(itmeid)) {
            		ColumnsInfo column = (ColumnsInfo)columnMap.get(itmeid);
            		c.setTextAlign(column.getTextAlign());
            		c.setColumnWidth(column.getColumnWidth());
            	}
            }
            ArrayList<LazyDynaBean> mergedCellList = exBo.getMergedCellList(columnsInfo);//复合列头
            ArrayList<LazyDynaBean> headList = exBo.getSecondSheetHeadList(columnsInfo ,true,w0301);//列头
            
            ArrayList secondDataList = exBo.getSecondDataList(columnsInfo, headList, reviewPersonIds, true);//数据集合
            //生成excel名称以 ‘会议名称_登录用户’修改【34440】
            String fileName = meettingName + "_" + PinyinUtil.stringToHeadPinYin(this.userView.getUserName()) + ".xls";//根据规则生成Excel名称
            excelUtil.setProtect(true);//是否启用锁定页面,先启用，才能设置只读
           
            HashMap map = new HashMap();
            
            // 问卷调查计划
            ArrayList<HashMap> qnPlan = new ArrayList<HashMap>();
            qnPlan = reviewFileBo.getQnPlan();
            ArrayList qnlist = new ArrayList();
            for(int i=0;i<qnPlan.size();i++){
                HashMap qnmap = qnPlan.get(i);
                String planId = (String) qnmap.get("dataValue");
                String planName = (String) qnmap.get("dataName");
                qnlist.add(planId+":"+planName);
            }
            map.put("w0539", qnlist);
            map.put("w0541", qnlist);
            
            //取代码型下拉列表
            for (int i = 0; i < headList.size(); i++) {
                LazyDynaBean codebean = headList.get(i);
                String codesetid = (String) codebean.get("codesetid");
                if(!("0".equalsIgnoreCase(codesetid))&&codesetid!=null&&!("".equalsIgnoreCase(codesetid))){
                    String itemid = (String) codebean.get("itemid");
                    if(ReviewFileTrans.exportIslock.indexOf(itemid) != -1){
                        continue;
                    }
                    ArrayList<String> desclist = getCodeByDesc(codesetid);
                    map.put(itemid, desclist);
                }
            }
            
            excelUtil.exportExcel(fileName, ResourceFactory.getProperty("上会材料"), 
                    mergedCellList, headList, secondDataList, map, 1);//导出表格
            this.getFormHM().put("fileName", PubFunc.encrypt(fileName));//表格名传进前台
            
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        
    }
    /**
     * 获取代码型 数据  下拉列表数据集合
     * 
     * @param fieldCodeSetId 
     * @return desclist 下拉列表数据集合
     */
    private ArrayList<String> getCodeByDesc(String fieldCodeSetId){
    	String tableName = "";
    	if("UN".equalsIgnoreCase(fieldCodeSetId) 
    			|| "UM".equalsIgnoreCase(fieldCodeSetId)
    			||"@K".equalsIgnoreCase(fieldCodeSetId))
    		tableName = "organization";
    	else
    		tableName = "codeitem";
    	String sql="select codeitemdesc from "+tableName+" where codesetid='"+fieldCodeSetId+"' and "+Sql_switcher.isnull("invalid", "1")+"='1'";
        RowSet rs = null;
        ArrayList<String> desclist = new ArrayList<String>();
        try{
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            rs=dao.search(sql);
            while(rs.next()){
                String codeitemdesc=rs.getString("codeitemdesc");
                desclist.add(codeitemdesc);
            }
            return desclist;
        }catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(rs!=null){
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

}
