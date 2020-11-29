package com.hjsj.hrms.module.recruitment.position.businessobject;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.businessobject.hire.HireTemplateBo;
import com.hjsj.hrms.businessobject.infor.multimedia.PhotoImgBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.recruitment.exammanage.examhall.businessobject.ExamHallBo;
import com.hjsj.hrms.module.recruitment.exammanage.examinee.businessobject.ExamineeBo;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hjsj.hrms.module.recruitment.recruitbatch.businessobject.RecruitBatchBo;
import com.hjsj.hrms.module.recruitment.recruitflow.businessobject.RecruitflowBo;
import com.hjsj.hrms.module.recruitment.resumecenter.businessobject.ResumeBo;
import com.hjsj.hrms.module.recruitment.util.RecruitPrivBo;
import com.hjsj.hrms.module.recruitment.util.RecruitUtilsBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.businessobject.TableFactoryBO;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnConfig;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.frame.utility.DateStyle;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jdom.Document;
import org.jdom.Element;
import org.mortbay.util.ajax.JSON;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 
 * <p>Title: PositionBo </p>
 * <p>Description: </p>
 * <p>Company: hjsj</p>
 * <p>create time  2015-1-19 下午01:37:42</p>
 * @author xiongyy
 * @version 1.0
 */
public class PositionBo {
    Connection conn;
    ContentDAO dao;
    UserView userview;
    
    public PositionBo(Connection conn, ContentDAO contentDAO, UserView userview) {
        this.conn = conn;
        this.dao = contentDAO;
        this.userview = userview;
        
    }
    
    public String getDefaultQuery(){
        ArrayList defaultQuery = new ArrayList();
        HashMap<String,String> hm = new HashMap<String,String>();
        hm = new HashMap<String,String>();
        hm.put("fieldsetid", "Z03");
        hm.put("itemid", "z0325");
        hm.put("itemdesc", "需求部门");
        hm.put("itemtype", "A");
        hm.put("codesetid", "UM");
        defaultQuery.add(hm);
        hm = new HashMap<String,String>();
        hm.put("fieldsetid", "Z03");
        hm.put("itemid", "z0351");
        hm.put("itemdesc", "职位名称");
        hm.put("itemtype", "A");
        hm.put("codesetid", "0");
        defaultQuery.add(hm);
       
        hm = new HashMap<String,String>();
        hm.put("fieldsetid", "Z03");
        hm.put("itemid", "z0336");
        hm.put("itemdesc", "招聘渠道");
        hm.put("itemtype", "A");
        hm.put("codesetid", "35");
        defaultQuery.add(hm);
        hm = new HashMap<String,String>();
        hm.put("itemid", "Z0103");
        hm.put("itemdesc", "招聘批次");
        hm.put("itemtype", "A");
        hm.put("codesetid", "0");
        defaultQuery.add(hm);
       
        return JSON.toString(defaultQuery);
    }
    /**
     * 审批状态
     * @return
     */
    public String getApprovalstatus(){
        ArrayList<HashMap<String,String>> appStatus = new ArrayList<HashMap<String,String>>();
        HashMap<String,String> hm = new HashMap<String,String>();
        hm = new HashMap<String,String>();
        hm.put("codeitemid", "01");
        hm.put("codeitemdesc", "起草");
        appStatus.add(hm);
        hm = new HashMap<String,String>();
        hm.put("codeitemid", "04");
        hm.put("codeitemdesc", "已发布");
        appStatus.add(hm);
        hm = new HashMap<String,String>();
        hm.put("codeitemid", "09");
        hm.put("codeitemdesc", "暂停");
        appStatus.add(hm);
        hm = new HashMap<String,String>();
        hm.put("codeitemid", "06");
        hm.put("codeitemdesc", "结束");
        appStatus.add(hm);
        
        return JSON.toString(appStatus);
    }
    public String getOptionalQuery(){
        ArrayList<HashMap<String,String>> optionalQuery = new ArrayList<HashMap<String,String>>();
        //查询模板预置
        HashMap<String,String> hm = new HashMap<String,String>();
        ArrayList<FieldItem> fieldList = DataDictionary.getFieldList("Z03", 1);
        for (FieldItem fieldItem : fieldList) {
            if(StringUtils.isNotEmpty(fieldItem.getItemdesc())){
                hm = new HashMap<String,String>();
                if(!"z0101".equals(fieldItem.getItemid())){
                    hm.put("fieldsetid", fieldItem.getFieldsetid());
                    hm.put("itemid", fieldItem.getItemid());
                    hm.put("itemdesc", fieldItem.getItemdesc());
                    hm.put("itemtype", fieldItem.getItemtype());
                    hm.put("codesetid", fieldItem.getCodesetid());
                    if(!"Z0301".equalsIgnoreCase(fieldItem.getItemid())&&!"Z0381".equalsIgnoreCase(fieldItem.getItemid())&&fieldItem.isVisible()){
                        optionalQuery.add(hm);
                    }
                }
            }
        }
        hm = new HashMap<String,String>();
        hm.put("itemid", "Z0103");
        hm.put("itemdesc", "招聘批次");
        hm.put("itemtype", "A");
        hm.put("codesetid", "0");
        optionalQuery.add(hm);
        hm = new HashMap<String,String>();
        hm.put("itemid", "publishTime");
        hm.put("itemdesc", "发布日期");
        hm.put("itemtype", "D");
        hm.put("codesetid", "0");
        optionalQuery.add(hm);
        hm = new HashMap<String,String>();
        hm.put("itemid", "responsPosi");
        hm.put("itemdesc", "招聘负责人");
        hm.put("itemtype", "A");
        hm.put("codesetid", "0");
        optionalQuery.add(hm);
        hm = new HashMap<String,String>();
        hm.put("itemid", "depResponsPosi");
        hm.put("itemdesc", "部门招聘负责人 ");
        hm.put("itemtype", "A");
        hm.put("codesetid", "0");
        optionalQuery.add(hm);
        return JSON.toString(optionalQuery);
    }
    
    /**
     * 获取招聘批次
     * @return
     * @throws GeneralException 
     */
    public String getBatchQuery() throws GeneralException{
        RowSet search = null;
        ArrayList<HashMap<String,String>> batchQuery = new ArrayList<HashMap<String,String>>();
        HashMap<String,String> hm = new HashMap<String,String>();
        StringBuffer sql = new StringBuffer();
        sql.append("select z0101,z0103 from z01");
        sql.append(" where z0129<>'01' and ");
        RecruitPrivBo privBo = new RecruitPrivBo();
		
        try {
        	String privB0110 = privBo.getPrivB0110Whr(userview, "z0105", RecruitPrivBo.LEVEL_GLOBAL_PARENT_SELF_CHILD);
    		sql.append(privB0110) ;
			HashMap<String, Object> parame = privBo.getChannelPrivMap(userview, conn);
			boolean setFlag = (Boolean) parame.get("setFlag");
			StringBuffer str = new StringBuffer();
			if(setFlag) {
				ArrayList<String> hirePriv = (ArrayList<String>) parame.get("hirePriv");
				if(hirePriv.size()>0) {
					str.append(" and (");
					for (String hire : hirePriv) {
						str.append(" z0151 like ");
						str.append("'"+hire+"%' or");
					}
					str.setLength(str.length()-2);
					str.append(" )");
					}
				else
					str.append(" and 1=2 ");
			}
    		
			sql.append(str);
			sql.append(" order by Z0107 desc ");
            search = dao.search(sql.toString());
            while(search.next()){
                hm = new HashMap<String,String>();
                hm.put("codeitemid", "custom_"+search.getString("z0101"));
                hm.put("codeitemdesc", search.getString("z0103"));
                batchQuery.add(hm);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            PubFunc.closeResource(search);
        }
        return JSON.toString(batchQuery);
    }

    /**
     * @param isModule 
     * @Title: getColumnList   
     * @Description:    查询列表表头信息
     * @param @return 
     * @return ArrayList    
     * @throws GeneralException 
     */
    public ArrayList getColumnList(boolean isModule ) throws Exception  {
        
        ArrayList list = new ArrayList();
        ArrayList columnList = new ArrayList();
      //防止出现重复指标
        ArrayList<String> temp = new ArrayList();
        TableFactoryBO tableBo = new TableFactoryBO("zp_position_191130_00001", this.userview, conn);
        HashMap scheme = tableBo.getTableLayoutConfig();
        String itemStr = "responsPosi`depResponsPosi`publishTime`position`z0301`z0381`z0319`z0336`z0321`z0325`z0371`z0329`z0331`z0310`z0373";
        if(scheme!=null)
        {
            Integer scheme_str = (Integer)scheme.get("schemeId");
            int schemeId = scheme_str.intValue();;
            ArrayList columnConfigLst = tableBo.getTableColumnConfig(schemeId);
            list = (ArrayList) columnConfigLst.clone();
            list.add("z0301");
            list.add("z0381");   //流程ID
            //已保存过旧栏目设置
            boolean isnew = false;
            for(int i=0;i<columnConfigLst.size();i++) {
            	Object object = columnConfigLst.get(i);
            	if(!(object instanceof ColumnConfig)) 
            		continue;
            	if("newandall".equalsIgnoreCase(((ColumnConfig) object).getItemid())) {
            		isnew = true;
            		break;
            	}
            }
            //删除栏目设置
            if(isnew) {
            	ArrayList<String> arrayList = new ArrayList<String>();
        		arrayList.add("zp_position_191130_00001");
        		arrayList.add(userview.getUserName());
        		StringBuffer sql = new StringBuffer();
        		sql.append("delete from t_sys_table_scheme_item ");
        		sql.append(" where scheme_id in (select scheme_id from t_sys_table_scheme where submoduleid=? and username=?)");
        		int delete = dao.delete(sql.toString(), arrayList);
        		sql.setLength(0);
    			int delete2 = dao.delete("delete from t_sys_table_scheme where submoduleid=? and username=?", arrayList);
    			//防止前面删除不成功造成递归死循环
    			if(delete>0 && delete2>0)
    				return getColumnList(isModule);
            }
            
        }else{
            list.add("z0301");
            list.add("z0381");   //流程ID
            list.add("z0319");  // 状态
            list.add("z0336");  //  招聘类别
            list.add("position");  //职位
            list.add("z0321");   //需求单位
            list.add("z0325");   //需求部门
            list.add("z0371");  //进程中的候选人
            list.add("publishTime");   // 发布日期
            list.add("z0329");   // 有效起始日期
            list.add("z0331");   // 有效结束日期 
            list.add("z0310");  //  创建人
            list.add("responsPosi");  // 招聘负责人
            list.add("depResponsPosi");  // 部门招聘负责人
            //获取业务字典里所有相关列
            ArrayList column = this.getColumn();
            for(int i=0;i<column.size();i++)
            {
                String itemId = (String)column.get(i);
                if(itemStr.indexOf(itemId)<0)
                {                   
                    list.add(itemId);
                }
            }
        }
        
        try {
        
            String mergedesc = "";
            int mergedescIndex = 0;
            int num = 0;
            for (int i = 0; i < list.size(); i++) {
                String displayDesc = "";
                String itemId = "";
                ColumnsInfo info = new ColumnsInfo();
                FieldItem item = null;
                if(scheme!=null)
                {
                    if(!"z0301".equals(list.get(i))&&!"z0381".equals(list.get(i)))
                    {
                        //当前用户有自定义栏目设置时
                        ColumnConfig column = (ColumnConfig)list.get(i);
                        displayDesc = column.getDisplaydesc();
                        itemId = column.getItemid();
                        if(temp.contains(itemId))
                        	continue;
                        if("z0373".equals(itemId) || "z0365".equals(itemId))
                            continue;
                        
                        item = DataDictionary.getFieldItem(itemId,"z03");
                        if(item!=null&&"1".equals(item.getUseflag()))
                        {
                            info = new ColumnsInfo(item);
                            if("1".equals(column.getIs_lock()))
                            {
                                info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
                            }else if("0".equals(column.getIs_lock()))
                            {
                                info.setLoadtype(ColumnsInfo.LOADTYPE_NOTLOAD);
                            }
                            info.setLoadtype(Integer.parseInt(("".equals((String)column.getIs_lock())||(String)column.getIs_lock()==null)?"0":(String)column.getIs_lock()));
                            info.setColumnWidth(column.getDisplaywidth());
                            info.setTextAlign(column.getAlign()+"");
                            if("1".equals(column.getIs_sum()))
                            {
                                info.setSummaryType(ColumnsInfo.SUMMARYTYPE_SUM);
                            }else if("2".equals(column.getIs_sum())){
                                info.setSummaryType(ColumnsInfo.SUMMARYTYPE_AVERAGE);
                            }else if("3".equals(column.getIs_sum())){
                                info.setSummaryType(ColumnsInfo.SUMMARYTYPE_MIN);
                            }else if("4".equals(column.getIs_sum())){
                                info.setSummaryType(ColumnsInfo.SUMMARYTYPE_MAX);
                            }
                            if("0".equalsIgnoreCase(column.getIs_fromdict()))
                            {                           
                                info.setFromDict(Boolean.parseBoolean("false"));
                            }
                            if(column.getMergedesc()!=null&&column.getMergedesc().length()>0){
                                if(mergedesc.equalsIgnoreCase(column.getMergedesc())&&mergedescIndex==i-1)
                                {
                                    ArrayList tableheadlist = new ArrayList( );
                                    if(columnList.size() > mergedescIndex-num ) {
                                    	  tableheadlist.add(columnList.get(mergedescIndex-num));
                                          tableheadlist.add(info);
                                          HashMap topHead = new HashMap();
                                          topHead.put("text",mergedesc);
                                          topHead.put("items", tableheadlist);
                                          columnList.remove(mergedescIndex-num);//当合并时移除最后一列
                                          columnList.add(topHead);
                                          num+=1;
                                          continue;
                                    }
                                }else{                                  
                                    mergedesc = column.getMergedesc();
                                    mergedescIndex = columnList.size();
                                }
                            }
                        }
                    }else{
                        itemId = (String) list.get(i);
                        item = DataDictionary.getFieldItem(itemId); 
                        info = new ColumnsInfo(item);
                    }
                }else{
                    itemId = (String) list.get(i);
                    item = DataDictionary.getFieldItem(itemId,"z03");
                    if(itemStr.indexOf(itemId)<0)
                    {                       
                        info = new ColumnsInfo(item);
                        info.setLoadtype(ColumnsInfo.LOADTYPE_NOTLOAD);
                    }
                }
                if(temp.contains(itemId))
                	continue;

                if(item==null&&"position".equalsIgnoreCase(itemId)){
                    info.setColumnId("position");
                    info.setColumnType("A");
                    info.setColumnDesc("职位 ");
                    info.setColumnWidth(200);
                    info.setRendererFunc("Global.toPositionDetail");
                    info.setLocked(true);
                    info.setFromDict(false);
                    //info.setFilterable(false);
                }else if(item==null&&"responsPosi".equalsIgnoreCase(itemId)){
                    info.setColumnId("responsPosi");
                    info.setColumnType("A");
                    info.setColumnDesc("招聘负责人 ");
                    info.setFromDict(false);
                }else if(item==null&&"depResponsPosi".equalsIgnoreCase(itemId)){
                    info.setColumnId("depResponsPosi");
                    info.setColumnType("A");
                    info.setColumnDesc("部门招聘负责人 ");
                    info.setFromDict(false);
                }else if(item==null&&"publishTime".equalsIgnoreCase(itemId)){
                    info.setColumnId("publishTime");
                    info.setColumnType("D");
                    info.setColumnLength(10);
                    info.setColumnDesc("发布日期 ");
                    info.setFromDict(false);
                }else if(item!=null&&"z0301".equalsIgnoreCase(itemId)){
                    info = new ColumnsInfo(item);
                    info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
                    info.setEncrypted(true);
                }else if(item!=null&&"z0381".equalsIgnoreCase(itemId)){
                    info = new ColumnsInfo(item);
                    info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
                    info.setEncrypted(true);
                    ColumnsInfo info1 = new ColumnsInfo();
                    info1.setColumnId("flow");
                    info1.setColumnType("A");
                    info1.setColumnDesc(item.getItemdesc());
                    info1.setFilterable(false);
                    columnList.add(info1);
                }else if(item!=null&&"1".equals(item.getUseflag())&&itemStr.indexOf(itemId)>0){
                    info = new ColumnsInfo(item);
                    if("z0319".equalsIgnoreCase(itemId)){
//                      info.setOperationData("Global.dataList");//为了列的功能显示出 统计，当为代码类时显示，所以注释掉
                        info.setColumnWidth(70);
                        info.setLocked(true);
                    }else if("z0371".equalsIgnoreCase(itemId)){
                    	info.setColumnWidth(80);
                    	if(this.userview.isSuper_admin()||this.userview.hasTheFunction("3110117")){
                    		if(!isModule)
                    			info.setRendererFunc("Global.toRecruitprocess");
                    	}
                    }else if("z0325".equalsIgnoreCase(itemId)||"z0321".equalsIgnoreCase(itemId)){
                        info.setCtrltype("3");
                        info.setNmodule("7");
                        info.setLocked(true);
                    }else if("z0336".equalsIgnoreCase(itemId)){
                        info.setLocked(true);
                    }
                    
                }
                else if("z0369".equalsIgnoreCase(itemId)) {
                	info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
                	if(this.userview.isSuper_admin()||this.userview.hasTheFunction("31102")){
                    	if(!isModule)
                    		info.setRendererFunc("Global.showNewResume");
                    }
                }else if("z0323".equalsIgnoreCase(itemId)) {
                	info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
                	if(this.userview.isSuper_admin()||this.userview.hasTheFunction("31102")){
                    	if(!isModule)
                    		info.setRendererFunc("Global.showAllResume");
                    }
                }else if("z0333".equalsIgnoreCase(itemId)) {
                	info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
                }
                if("M".equalsIgnoreCase(info.getColumnType()))
                {
                    info.setSortable(false);
                }
                else if("D".equalsIgnoreCase(info.getColumnType()))
                {
                    info.setTextAlign("left");
                }
                if("z0321".equalsIgnoreCase(itemId)||"z0325".equalsIgnoreCase(itemId)||"z0301".equalsIgnoreCase(itemId)){
                    info.setOrdertype("1");
                }
                if(info.getColumnId()==null)
                    continue;
                if(StringUtils.isNotEmpty(displayDesc))
                {
                    info.setColumnDesc(displayDesc);
                }
                columnList.add(info);
            }//for end
            
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        
        return columnList;
    }
    
    /**
     * @throws GeneralException 
     * @param flag 
     * 
     * @Title: getPositionSql   
     * @Description:    用来获取列表数据的sql语句
     * @param positionColumn
     * @return String    
     * @throws
     */
    public String getPositionSql(ArrayList positionColumn) throws GeneralException {
        
        StringBuffer strsql = new StringBuffer("select ");
        
        //招聘负责人对应的表 left join (select z0301,a0101 responsPosi from zp_members  where member_type = '1') m1 on m1.z0301 = z.Z0301
        String responsPosiSql = "";  
        
        // 部门招聘负责人对应的表   left join (select z0301,a0101 depResponsPosi from zp_members  where member_type = '3') m2 on m2.z0301=z.Z0301
        String depResponsPosiSql = ""; 
        
        String publishSql = "";
        
        //batchPosiSql  招聘批次
        String batchPosiSql = "";
        StringBuffer columnsSql = new StringBuffer(); // 字段 sql
        try{
        for (int i = 0; i < positionColumn.size(); i++) {
            String infoId = "";
            //int num = positionColumn.get(i).getClass().getName().lastIndexOf("HashMap"); 
            Object o = positionColumn.get(i);
            if(o instanceof HashMap)
            {
                HashMap hm = (HashMap) positionColumn.get(i);
                ArrayList list = (ArrayList)hm.get("items");
                for(int j=0;j<list.size();j++)
                {
                    ColumnsInfo info = (ColumnsInfo) list.get(j);
                    infoId = info.getColumnId();
                    if(StringUtils.contains(columnsSql.toString(), infoId) && !"z0333".equals(infoId))
                    	continue;
                    if("z0351".equalsIgnoreCase(infoId))
                        continue;
                    if("position".equalsIgnoreCase(infoId)){
                          //职位
                          String temp3 = getSpecColumn(infoId);
                          columnsSql.append(temp3+" ,");
                          
                      }else if("publishTime".equalsIgnoreCase(infoId)){
                          //创建人
                          columnsSql.append("m3.publishTime ,");
                          publishSql = "left join (select z0301,create_time publishTime from zp_members  where member_type = '4') m3 on m3.z0301 = z.z0301 ";
                      
                      }else if("responsPosi".equalsIgnoreCase(infoId)){
                          //招聘负责人
                          columnsSql.append("m1.responsPosi ,");
                          responsPosiSql = "left join (select z0301,a0100,a0101 responsPosi from zp_members  where member_type = '1') m1 on m1.z0301 = z.z0301 ";
                      
                      }else if("depResponsPosi".equalsIgnoreCase(infoId)){
                          //部门招聘负责人
                          columnsSql.append("m2.depResponsPosi ,");
                          depResponsPosiSql = "left join (select z0301,a0100,a0101 depResponsPosi  from zp_members "
                          		+ "where member_id in (select min(member_id) from zp_members where member_type = '3' group by z0301)"
                          		+ "and member_type = '3') m2 on m2.z0301=z.z0301 ";
                          
                      }else if("z0371".equalsIgnoreCase(infoId)){
                          columnsSql.append(Sql_switcher.isnull("z."+infoId, "0")+"as z0371,");
                      }else if("z0101".equalsIgnoreCase(infoId)){
                          //招聘批次
                          columnsSql.append("z01.z0103 z0101,");
                         batchPosiSql = " left join z01 on z01.z0101 = z.z0101 ";
                      }else{
                          columnsSql.append("z."+infoId+" ,");
                      }
                }
            }else{              
                ColumnsInfo info = (ColumnsInfo) positionColumn.get(i);
                infoId = info.getColumnId();
                if(StringUtils.contains(columnsSql.toString(), infoId) && !"z0333".equals(infoId))
                	continue;
                if("z0351".equalsIgnoreCase(infoId))
                    continue;
                if("position".equalsIgnoreCase(infoId)){
                    //职位
                    String temp3 = getSpecColumn(infoId);
                    columnsSql.append(temp3+" ,");
                    
                }else if("publishTime".equalsIgnoreCase(infoId)){
                    //创建人
                    columnsSql.append("m3.publishTime ,");
                    publishSql = "left join (select z0301,create_time publishTime from zp_members  where member_type = '4') m3 on m3.z0301 = z.z0301 ";
                    
                }else if("responsPosi".equalsIgnoreCase(infoId)){
                    //招聘负责人
                    columnsSql.append("m1.responsPosi ,");
                    responsPosiSql = "left join (select z0301,a0100,a0101 responsPosi from zp_members  where member_type = '1') m1 on m1.z0301 = z.z0301 ";
                    
                }else if("depResponsPosi".equalsIgnoreCase(infoId)){
                    //部门招聘负责人
                    columnsSql.append("m2.depResponsPosi ,");
                    depResponsPosiSql = "left join (select z0301,a0100,a0101 depResponsPosi  from zp_members "
                    		+ "where member_id in (select min(member_id) from zp_members where member_type = '3' group by z0301)"
                    		+ "and member_type = '3') m2 on m2.z0301=z.z0301 ";
                    
                }else if("z0371".equalsIgnoreCase(infoId)){
                    columnsSql.append(Sql_switcher.isnull("z."+infoId, "0")+"as z0371,");
                }else if("z0101".equalsIgnoreCase(infoId)){
                    //招聘批次
                    columnsSql.append("z01.z0103 z0101,");
                    batchPosiSql = " left join z01 on z01.z0101 = z.z0101 ";
                }else if("flow".equalsIgnoreCase(infoId)) {
                	//招聘流程
                	columnsSql.append("(select name from zp_flow_definition where flow_id=z.z0381) flow,");
                }else{
                    columnsSql.append("z."+infoId+" ,");
                }
            }
            
            
        }
        if(columnsSql.length()>0)
            columnsSql.setLength(columnsSql.length()-1);
        strsql.append(columnsSql);
        strsql.append(",z0351 from z03 z ");
        strsql.append(batchPosiSql);
        strsql.append(responsPosiSql);
        strsql.append(depResponsPosiSql);
        strsql.append(publishSql);
        strsql.append("left join organization org on org.codeitemid = z.z0325 ");
        this.getDownloadFile();
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return strsql.toString();
    }
    
    /**
     * 
     * @Title: getSpecColumn   
     * @Description:    获取特殊列的字段
     * @param infoId
     * @return String    
     * @throws
     */
    private String getSpecColumn(String infoId) {
        String str = "";
        if("position".equalsIgnoreCase(infoId)){
             str = Sql_switcher.isnull("z.z0351", "'未知职业'")+" as position";
        }
        
        return str;
    }

    /**
     * 
     * @Title: getQueryList   
     * @Description: 获得查询方案  
     * @return ArrayList    
     */
    public ArrayList getQueryList() {
        ArrayList list = new ArrayList();
        ArrayList list0 = new ArrayList();
        ArrayList list1 = new ArrayList();
        ArrayList list2 = new ArrayList();
        ArrayList list3 = new ArrayList();
        list0.add(new PositionPlan("全部", "Global.searchAll()","all","all"));
        list.add(list0);
        list.add(getTimePlan(list1));
        list.add(getTypePlan(list2)); 
        list.add(getPositionPlan(list3)); 
        return list;
    }
    
    
    /**
     * 返回和职位相关的查询方案
     * @param list3
     * @return
     */
    private ArrayList getPositionPlan(ArrayList list3) {
        list3.add(new PositionPlan("所有职位", "Global.tosearch('1','2','C')","C","C1"));
        list3.add(new PositionPlan("我的职位", "Global.tosearch('2','2','C')","C","C2"));
        
        return list3;
    }
    
    
    /**
     * 返回和状态相关的查询方案(如：进行中 等)
     * @param list2
     * @return
     */
    private ArrayList getTypePlan(ArrayList list2) {
        list2.add(new PositionPlan("进行中", "Global.tosearch('2','1','B')","B","B2"));
        list2.add(new PositionPlan("暂停", "Global.tosearch('4','1','B')","B","B4"));
        list2.add(new PositionPlan("结束", "Global.tosearch('5','1','B')","B","B5"));
        list2.add(new PositionPlan("已过期", "Global.tosearch('6','1','B')","B","B6"));
        return list2;
    }

    /**
     * 返回查询时间相关的查询方案
     * @param list1
     * @return
     */
    private ArrayList getTimePlan(ArrayList list1) {
       // list1.add(new PositionPlan("往年", "Global.tosearch('4','0','A')","A","A4"));
        list1.add(new PositionPlan("本年度", "Global.tosearch('1','0','A')","A","A1"));
        list1.add(new PositionPlan("本季度", "Global.tosearch('2','0','A')","A","A2"));
        list1.add(new PositionPlan("本月", "Global.tosearch('3','0','A')","A","A3")); 
        
        return list1;
    }

    /**
     * 获取where条件
     * @param flag
     * @param queryStr 
     * @param queryArray 
     * @param isModule 
     * @param a0100s 用来判断推荐职位的是单人还是多人
     * @return
     */
    public String getWhere(String flag, String queryStr, ArrayList queryArray, boolean isModule, String a0100s) throws GeneralException {
        StringBuffer strwhere=new StringBuffer("");
        if(queryStr!=null&&queryStr.length()>0){    //快速查询条件
            strwhere.append(getFastQuery(queryStr));
        }
        
        if(!"1".equals(flag)){
            if(queryArray!=null&&queryArray.size()>0){ //方案查询条件
                strwhere.append("where ");
                strwhere.append(getWhereTime( (String) queryArray.get(0)));
                strwhere.append(getWhereType( (String) queryArray.get(1)));
                strwhere.append(getPosition( (String) queryArray.get(2)));
                
            }else{
                //推荐职位时走的权限
                strwhere.append("where ");
                strwhere.append(getPosition("1"));
            }
        }else{
            strwhere.append("where ");
            strwhere.append(getPosition("1"));
        } 

        if(isModule){     
            strwhere.append(" and z.z0319='04' ");
            if(StringUtils.isNotEmpty(a0100s)){//推荐单个候选人时排除掉已经应聘的职位
                HashMap map = (HashMap) JSON.parse(a0100s);
                JSONArray object = JSONArray.fromObject(map.get("a0100"));
                JSONObject temp= (JSONObject) object.get(0);
                String a0100  = PubFunc.decrypt((String) temp.get("a0100"));
                String nbase  = PubFunc.decrypt((String) map.get("nbase"));
                if(object.size()==1){
                    strwhere.append(" and z.z0301 not in(");
                    strwhere.append(" select zp_pos_id from zp_pos_tache ");
                    strwhere.append(" where a0100='"+a0100+"' ");
                    strwhere.append(" and nbase='"+nbase+"'");
                    strwhere.append(")");
                }
            }
        }
        
        return strwhere.toString();
    }
    /***
     * 查询邮件服务器是否配置
    * @Title:getFromAddr
    * @Description：
    * @return
    * @throws GeneralException
     */
    public String getFromAddr() throws GeneralException {
        String str = "";
        RecordVo stmp_vo = ConstantParamter.getConstantVo("SS_STMP_SERVER");
        if (stmp_vo == null)
            return "";
        String param = stmp_vo.getString("str_value");
        if (param == null || "".equals(param))
            return "";
        Document doc = null;
        try {
            doc = PubFunc.generateDom(param);
            Element root = doc.getRootElement();
            Element stmp = root.getChild("stmp");
            str = stmp.getAttributeValue("from_addr");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        } finally {
            PubFunc.closeResource(doc);
        }
        return str;
    }
    /**
     * 
     * @param queryStr  快速查询条件
     * @return
     */
    private String getFastQuery(String queryStr) {
        Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(conn);
        queryStr = PubFunc.hireKeyWord_filter(queryStr);
        String pinyin_field = sysbo.getValue(Sys_Oth_Parameter.PINYIN_FIELD_SEARCH);
        if(pinyin_field==null || pinyin_field.length()==0)
            pinyin_field="c0103";
        StringBuffer str = new StringBuffer("");
        str.append(" left join UsrA01 usr2 on usr2.A0100 = m2.a0100");
        str.append(" left join UsrA01 usr1 on usr1.A0100 = m1.a0100");
        str.append(" where (usr2."+pinyin_field+" like '%"+queryStr+"%'");
        str.append(" or usr2.C0102 like '"+queryStr+"%'");
        str.append(" or usr1."+pinyin_field+" like '%"+queryStr+"%'");
        str.append(" or m1.responsPosi like '%"+queryStr+"%'");
        str.append(" or m2.depResponsPosi like '%"+queryStr+"%'");
        str.append(" or org.codeitemdesc like '%"+queryStr+"%'");
        String email_field ="";
        try {
            email_field=this.getFromAddr();
        } catch (GeneralException e) {
            e.printStackTrace();
        }
        FieldItem c0102 = DataDictionary.getFieldItem(email_field);
        if(c0102!=null&&!"".equals(c0102)){
             str.append(" or usr1."+c0102.getItemid()+" like '"+queryStr+"%'");
        }
        String positionSql = getSpecColumn("position");
        str.append(" or "+positionSql.substring(0,positionSql.length()-11)+ " like '%"+queryStr+"%') and ");
        return str.toString();
    }

    /**
     * 根据人员范围控制展示的职位  控制需求部门
     * @param strwhere
     */
    private void getPrivSql(StringBuffer strwhere) {
        String privCode = userview.getUnitIdByBusi("7");
        if("un`".equalsIgnoreCase(privCode)||this.userview.isSuper_admin()){
            return;
        }
        String[] split = privCode.split("`");
        String whl="";
        for (int i = 0; i < split.length; i++) {
            whl+=" or z0325 like '"+split[i].substring(2, split[i].length())+"%' ";
        }
        strwhere.append(" ( ");
        if(whl.length()>0)
            strwhere.append(" ("+whl.substring(3)+") or ");
        if(userview.getA0100().length()>0)
            strwhere.append(" z.z0301 in ( select z0301 from zp_members where a0100 = '"+userview.getA0100()+"' or create_user='"+userview.getUserName()+"'  )) and "); //这里最后也可以不去掉or因为在getwhere()方法里会减去3个长度
        else
            strwhere.append(" z.z0301 in ( select z0301 from zp_members where  create_user='"+userview.getUserName()+"'  ))  and "); //这里最后也可以不去掉or因为在getwhere()方法里会减去3个长度
    }
    
    

    // 解析前台传的arry数组    -----------------------  start
    // 所有职位    我的职位
    private String getPosition(String str) {
        StringBuffer temp = new StringBuffer();
        //权限下的所有职位
        //从权限中获取职位权限
        RecruitPrivBo bo = new RecruitPrivBo();
        if("2".equals(str)){ //代表我的职位
            
          //拼接我的职位（创建人）
            temp.append(" ( z.z0309='"+this.userview.getUserName()+"' "); 
            //负责人、招聘成员、部门负责人
            if(this.userview.getA0100().length()>0)
            {
                temp.append(" or z.z0301 in ( select z0301 from zp_members ");
                temp.append(" where a0100 = '"+this.userview.getA0100()+"' and nbase='"+this.userview.getDbname()+"' ) ");
            }
            temp.append(" ) ");
        }else{
            try {
                temp.append(bo.getPositionWhr(this.userview));
            } catch (GeneralException e) {
                e.printStackTrace();
            }
        }
        //获取招聘职位渠道权限条件
        temp.append(bo.getHirePrivSql(this.userview, this.conn));
        return temp.toString();
    }
    //草稿    已发布    进行中    暂停    结束    已过期 
    private String getWhereType(String str) {
        String temp = "";
        if("1".equals(str)){
            temp = " Z0319 = '01' and ";
        }else if("2".equals(str)){
        	temp = " Z0319 != '06' and Z0319 != '09' and Z0331 > "+Sql_switcher.sqlNow()+" and  ";
        }else if("3".equals(str)){
            temp = " Z0319 = '05' and ";
        }else if("4".equals(str)){
            temp = " Z0319 = '09' and ";
        }else if("5".equals(str)){
            temp = " Z0319 = '06' and ";
        }else if("6".equals(str)){
            temp = " Z0331 < "+Sql_switcher.sqlNow()+" and ";
        }
        return temp;
    }
    
    // 本年度    本季度    本月 |  
    private String getWhereTime(String str) {
        String temp = "";
        String timeNow = new SimpleDateFormat("yyyy.MM.dd").format(new Date());
        String year1 = "";
        String year2 = "";
        boolean flag= false;
        //2015-01-24
        if("1".equals(str)){
            year1 = Sql_switcher.charToDate("'"+timeNow.substring(0,5)+"01.01'");
            year2 = Sql_switcher.charToDate("'"+timeNow.substring(0,5)+"12.31'");
            flag=true;
        }else if("2".equals(str)){  //本季度
            //2015-02-01,2015-04-31
            ArrayList timeFor = getQuarter(timeNow);
            year1 = Sql_switcher.charToDate("'"+timeFor.get(0)+"'");
            year2 = Sql_switcher.charToDate("'"+timeFor.get(1)+"'");
            flag=true;
        }else if("3".equals(str)){//本月
            year1 = Sql_switcher.charToDate("'"+timeNow.substring(0,8)+"01'");
            year2 = Sql_switcher.charToDate("'"+timeNow.substring(0,8)+getDayOfMonth()+"'");
            flag=true;
        }
        if(flag)
            temp = " Z0329 between "+year1+" and "+year2+" and ";
        else if("4".equals(str))
            temp = " Z0329 < "+Sql_switcher.charToDate("'"+timeNow.substring(0,5)+"01.01'")+" and ";
        return temp;
    }
    /**
     * 获得季度的时间段
     * @param timeNow
     * @return
     */
    private ArrayList getQuarter(String timeNow) {
        ArrayList list = new ArrayList();
        int temp = Integer.parseInt(timeNow.substring(5,7));
        if(temp>=1&&temp<=3){
            list.add(timeNow.substring(0,5)+"01.01");
            list.add(timeNow.substring(0,5)+"03-31");
        }else if(temp>=4&&temp<=6){
            list.add(timeNow.substring(0,5)+"04.01");
            list.add(timeNow.substring(0,5)+"06.30");
        }else if(temp>=7&&temp<=9){
            list.add(timeNow.substring(0,5)+"07.01");
            list.add(timeNow.substring(0,5)+"09.30");
        }else{
            list.add(timeNow.substring(0,5)+"10.01");
            list.add(timeNow.substring(0,5)+"12.31");
        }
        
        return list;
    }
    //获得当前月份的天数
    public static int getDayOfMonth() {
        Calendar aCalendar = Calendar.getInstance(Locale.CHINA);
        int day = aCalendar.getActualMaximum(Calendar.DATE);
        return day;
    }
    // 解析前台传的arry数组    -----------------------  end
    
    /**
     * @param deleteIds 删除的id
     * @throws GeneralException 
     * 
     */
    public void deletePosition(String deleteIds) throws GeneralException {
        RowSet rs = null;
        try{
            if (deleteIds.length() > 0) {
                StringBuffer strmark = new StringBuffer("");
                ArrayList list = new ArrayList();
                splistString(deleteIds, list, strmark);
                StringBuffer bufsql = new StringBuffer("");
                bufsql.append("delete z03 where z0301 in");
                bufsql.append("(");
                bufsql.append(strmark);
                bufsql.append(")");
                dao.delete(bufsql.toString(), list);
                
                bufsql.setLength(0);
                bufsql.append("delete zp_pos_tache where Zp_pos_id in");
                bufsql.append("(");
                bufsql.append(strmark);
                bufsql.append(")");
                dao.delete(bufsql.toString(), list);
                
                bufsql.setLength(0);
                bufsql.append("delete zp_members where z0301 in");
                bufsql.append("(");
                bufsql.append(strmark);
                bufsql.append(")");
                dao.delete(bufsql.toString(), list);
                
                bufsql.setLength(0);
                bufsql.append("delete zp_examiner_arrange where ");
                bufsql.append("z0501 in(select z0501 from z05 where z0301 in ");
                bufsql.append("(");
                bufsql.append(strmark);
                bufsql.append("))");
                dao.delete(bufsql.toString(), list);
                
                bufsql.setLength(0);
                bufsql.append("delete z05 where z0301 in");
                bufsql.append("(");
                bufsql.append(strmark);
                bufsql.append(")");
                dao.delete(bufsql.toString(), list);
                //查询删除的考生考场编号
                bufsql.setLength(0);
                bufsql.append("select exam_hall_id from zp_exam_assign");
                bufsql.append(" where exam_hall_id is not null ");
                bufsql.append(" and z0301 in ");
                bufsql.append("(");
                bufsql.append(strmark);
                bufsql.append(")");
                bufsql.append(" group by exam_hall_id ");
                rs = dao.search(bufsql.toString(), list);
                while(rs.next()){
                    String examHallId = rs.getString("exam_hall_id")==null?"":rs.getString("exam_hall_id");
                    if(examHallId.length()>0)
                    {                       
                        this.updateHallPerNum(examHallId);
                    }
                }
                //清除改职位下所有考生
                bufsql.setLength(0);
                bufsql.append("delete from  zp_exam_assign where z0301 in");
                bufsql.append("(");
                bufsql.append(strmark);
                bufsql.append(")");
                dao.update(bufsql.toString(), list);
                //清除准考证号和成绩信息
                bufsql.setLength(0);
                bufsql.append("delete from Z63 where z0301 in");
                bufsql.append("(");
                bufsql.append(strmark);
                bufsql.append(")");
                dao.update(bufsql.toString(), list);
                
                //删除考试安排表信息
                bufsql.setLength(0);
                bufsql.append("delete zp_exam_assign where z0301 in");
                bufsql.append("(");
                bufsql.append(strmark);
                bufsql.append(")");
                dao.delete(bufsql.toString(), list);
                
            }
            
        }catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally{
            PubFunc.closeDbObj(rs);
        }
    }

    /**
     * 更新考场中已分配人数
     * 
     * @param hallId
     * @throws GeneralException
     */
    public void updateHallPerNum(String hallId) throws GeneralException {
        String sql = "update zp_exam_hall set people_num=people_num-? where id=?";
        ContentDAO dao = new ContentDAO(this.conn);
        ExamHallBo ehbo = new ExamHallBo(conn, userview);
        ArrayList values = new ArrayList();
        values.add(ehbo.getHallPerNums(hallId));
        values.add(hallId);
        try {
            dao.update(sql, values);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
    
    /**
     * 职位列表的功能方法  比如 发布,暂停等功能
     * @param act =publish发布任务  =stop暂停  =end结束
     * @param z0301s  被勾选的数据的z0301字段拼接成的 0000002,000003,...,
     * @throws GeneralException 
     */
    public void functionOfPosition(String act, String z0301s) throws GeneralException {
        this.functionOfPosition(act, z0301s, "");        
    }
    
    /**
     * 职位列表的功能方法  比如 发布,暂停等功能
     * @param act =publish发布任务  =stop暂停  =end结束
     * @param z0301s  被勾选的数据的z0301字段拼接成的 0000002,000003,...,
     * @param opinion  点击退回按钮弹出文本框中填写的退回意见,
     * @throws GeneralException 
     */
    public void functionOfPosition(String act, String z0301s, String opinion) throws GeneralException {
    	if(z0301s==null && z0301s.length()==0){
            return; 
         }
         
         ArrayList sqlParams = new ArrayList();
         sqlParams.add(opinion);
         
         StringBuffer strmark = new StringBuffer("");
         splistString(z0301s, sqlParams, strmark);
         
         StringBuffer sql = new StringBuffer("");
        try {
            sql.append("update z03 set ");
            if("publish".equals(act)){
                sql.append(" Z0319 = '04'");
                toUpdatePublishTime(sqlParams, strmark);
            }else if("stop".equals(act)){
                sql.append(" Z0319 = '09'");
            }else if("end".equals(act)){
                sql.append(" Z0319 = '06'");
            }else if("report".equals(act)){
                sql.append(" Z0319 = '02'");
            }else if("revoke".equals(act)){
                sql.append(" Z0319 = '01'");
            }else if("approve".equals(act)){
                sql.append(" Z0319 = '03'");
            }else if("return".equals(act)){
                sql.append(" Z0319 = '07'");
            }
            
            sql.append(",Z0327 = ?");
            sql.append(" where z0301 in ");
            sql.append("(");
            sql.append(strmark);
            sql.append(")");
            dao.update(sql.toString(), sqlParams);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        
    }
    
    /**
     * 修改结束时间 
     * @param list
     * @param strmark
     * @throws SQLException 
     */
    private void toUpdateEndTime(ArrayList list, StringBuffer strmark) throws SQLException {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        StringBuffer sqlTime = new StringBuffer("update z03 set Z0331 =");
        sqlTime.append(Sql_switcher.charToDate("'"+date+"'")+" where z0301 in");
        sqlTime.append("(");
        sqlTime.append(strmark);
        sqlTime.append(")");
        //dao.update(sqlTime.toString(), list);
    }

    /**
     * 修改发布时间
     * @param list
     * @param strmark
     * @throws GeneralException 
     */
    private void toUpdatePublishTime(ArrayList list, StringBuffer strmark) throws GeneralException {
        //先要判断是否有创建人 有直接修改 没有就直接插入
        try {
            for (int i = 0; i < list.size(); i++) {
                ArrayList listz = new ArrayList();
                listz.add(list.get(i));
                String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                StringBuffer sqlTime = new StringBuffer("update zp_members set create_time =");
                if(Sql_switcher.searchDbServer()==Constant.ORACEL)
                {
                    sqlTime.append("to_date('" + date + "','YYYY-MM-DD HH24:MI:SS')"
                            + " where z0301 =?");
                }else{                  
                    sqlTime.append(Sql_switcher.charToDate("'" + date + "'")
                            + " where z0301 =?");
                }
                sqlTime.append(" and member_type = 4");
                int count = dao.update(sqlTime.toString(), listz);
                if (count == 0) {
                    saveMenber(userview.getDbname() + userview.getA0100(), 4,
                            (String) list.get(i));
                }
                
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
    
    
    
    
    /**
     * 以逗号切割并放到list里 同时生成对应的"?"
     * @param z0301s
     * @param list
     * @param strmark
     */
    private void splistString(String z0301s, ArrayList list,
            StringBuffer strmark) {
        String[] split = z0301s.split(",");
        for (int i = 0; i < split.length; i++) {
            list.add(PubFunc.decrypt(split[i]));
            strmark.append("?,");
        }
        if(strmark.length()>0){
            strmark.setLength(strmark.length()-1);
        }
    }
    
    
    /**
     * 
     * @return 返回编辑和创建职位的字段吧 用来映射list 
     * @throws GeneralException 
     */
    public ArrayList getPositionPageMap() throws GeneralException {
        ArrayList list = new ArrayList();
        ArrayList listfield = new ArrayList();
        getPositionFieldList(list);
        for (int i = 0; i < list.size(); i++) {
            
            HashMap map = new HashMap();
            map.put("table"+i,list.get(i));
            listfield.add(map);
        }
        
        return listfield;
    }
    /**
     * 获得新建职位的所有字段    目前是假数据
     * @param list
     * @throws GeneralException 
     */
    private void getPositionFieldList(ArrayList list) throws GeneralException {
        try{
    	    String zp_pos_apply_start_field = SystemConfig.getPropertyValue("zp_pos_apply_start_field");
    	    String zp_pos_apply_end_field = SystemConfig.getPropertyValue("zp_pos_apply_end_field");
            String sysIds = "z0373/z0365/z0381/z0331/z0329/z0375/z0333/z0301/z0321/z0325/z0101/z0309/z0307/z0310/z0319/Z0335/Z0371/Z0377/Z0379/Z0336".toLowerCase();
            if(this.getItemInZ03(zp_pos_apply_start_field) && this.getItemInZ03(zp_pos_apply_end_field))
        		sysIds = zp_pos_apply_start_field.toLowerCase() +"/" + zp_pos_apply_end_field.toLowerCase() +"/" + sysIds;
          
            ArrayList list1 = new ArrayList();
            ArrayList list2 = new ArrayList();
            ArrayList<String> list3 = new ArrayList<String>();
            ArrayList list4 = new ArrayList();
            list1.add("z0301hide");  //主键 需要隐藏;
            list1.add("z0351");   //职位名称y
            list1.add("z0321"); //需求单位y
            list1.add("z0325"); //需求部门
            list1.add("z0101"); //招聘批次
            list1.add("z0333");    //工作地点
            list1.add("z0375");   //到岗日期
            list1.add("z0315"); //需求人数、招聘人数y
            list.add(list1);
          //人民大学职称要求为多选 z03a2职称要求，z0390职称要求隐藏代码
            String title_Requirements = SystemConfig.getPropertyValue("title_Requirements");
            String z03a2 = "";
            String z0390 = "";
            if(StringUtils.isNotEmpty(title_Requirements)&&title_Requirements.split(":").length==2) {
            	z03a2 = title_Requirements.split(":")[0];
            	z0390 = title_Requirements.split(":")[1];
            }
            //得到招聘描述 start
            ArrayList fieldList = DataDictionary.getFieldList("z03", Constant.USED_FIELD_SET);
            for(int i=0;i<fieldList.size();i++){
                FieldItem item = (FieldItem) fieldList.get(i);
                String colunmName=item.getItemid().toLowerCase();
                if("z0365".equalsIgnoreCase(colunmName) || "z0373".equalsIgnoreCase(colunmName)
                        || "z0384".equalsIgnoreCase(colunmName) || "z0385".equalsIgnoreCase(colunmName)
                        || "z0351".equalsIgnoreCase(colunmName)|| "z0315".equalsIgnoreCase(colunmName))
                	continue;
                
                if(z0390.equalsIgnoreCase(colunmName))
                	list2.add("z0390hide");
                
                if(item!=null&&"1".equals(item.getState())&&"1".equals(item.getUseflag())&&sysIds.lastIndexOf(colunmName)<0)
                    list2.add(colunmName);
            }
            
            list.add(list2);
            //得到招聘描述 end
            ArrayList sbjList = new ArrayList();
            this.getSubjects(sbjList);
            LazyDynaBean sbjBean = new LazyDynaBean();
            for(int i=0;i<sbjList.size();i++)
            {
                sbjBean = (LazyDynaBean)sbjList.get(i);
                list4.add("subject_"+sbjBean.get("codeitemid").toString());
            }
            if(list4.size()>0)
            {
                 list.add(list4);
            }
            
            ParameterXMLBo parameterXMLBo = new ParameterXMLBo(this.conn);
            HashMap map = parameterXMLBo.getAttributeValues();
            if(this.getItemInZ03(zp_pos_apply_start_field) && this.getItemInZ03(zp_pos_apply_end_field)){
            		 list3.add(zp_pos_apply_start_field);//报名起始时间 y
                     list3.add(zp_pos_apply_end_field);//报名结束时间 y
            }
           
            list3.add("z0329");//有效起始时间 y
            list3.add("z0331");//有效结束时间 y
            list3.add("z0381");//流程id
            list3.add("z0336");//招聘渠道
            
            if (map != null && map.get("candidate_status") != null) {
                String candidateStatus = (String) map.get("candidate_status");
                if(StringUtils.isNotEmpty(candidateStatus) && !"#".equalsIgnoreCase(candidateStatus))
                    //招聘其他渠道
                    list3.add("z0385");
            }
            
//            list3.add("z0365");//接受内部推荐
//            list3.add("z0373");//是否猎头招聘
            FieldItem fieldItem = null;
            ArrayList listTemp = new ArrayList();
            for (String  itemid : list3) {
                fieldItem = DataDictionary.getFieldItem(itemid, "Z03");
                if(fieldItem!=null)
                    listTemp.add(itemid);
            }
            
            if(listTemp.contains("z0385")) {
                int index = listTemp.indexOf("z0385");
                listTemp.add(index, "z0384hide");
            }
            
            listTemp.add("apply_control");//不符合职位筛选规则是否允许申请 1 不允许，0允许
            listTemp.add("accept_post");//是否自动接收职位申请
            list.add(listTemp);
            
        }catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
    
    
    
    /**
     * 这是对应的职位数据参数
     * @param z0301 
     * @return
     * @throws GeneralException 
     */
    public HashMap getPositionPageData() throws GeneralException {
        
        ArrayList list = new ArrayList();
        getPositionFieldList(list);
        HashMap map = new HashMap();
        
        //人民大学职称要求为多选 z03a2职称要求，z0390职称要求隐藏代码
        String title_Requirements = SystemConfig.getPropertyValue("title_Requirements");
        String z03a2 = "";
        String z0390 = "";
        if(StringUtils.isNotEmpty(title_Requirements)&&title_Requirements.split(":").length==2) {
        	z03a2 = title_Requirements.split(":")[0];
        	z0390 = title_Requirements.split(":")[1];
        }
        
        //获取科目信息集合
        ArrayList sbjList = new ArrayList();
        this.getSubjects(sbjList);
        LazyDynaBean sbjBean = new LazyDynaBean();
        
        String requiredstr="z0321/z0351/z0336/z0329/z0331/z0381";//这些强制性设置必填项
        String zp_pos_apply_start_field = SystemConfig.getPropertyValue("zp_pos_apply_start_field");
 	    String zp_pos_apply_end_field = SystemConfig.getPropertyValue("zp_pos_apply_end_field");
 	    
        if(this.getItemInZ03(zp_pos_apply_start_field) && this.getItemInZ03(zp_pos_apply_end_field))
            requiredstr=  zp_pos_apply_start_field +"/" +zp_pos_apply_end_field+"/" + requiredstr ;
    	
        RowSet rs = null;
        try{
            for (int i = 0; i < list.size(); i++) {
                ArrayList listTemp = (ArrayList) list.get(i);
                for (int j = 0; j < listTemp.size(); j++) {
                    String required = "n"; //=y必填    =n 
                    ArrayList codeList = new ArrayList();
                    String itemid = (String)listTemp.get(j);
                    if(itemid.lastIndexOf("hide")>0)
                        itemid = itemid.substring(0, itemid.lastIndexOf("hide"));
                    FieldItem item = DataDictionary.getFieldItem(itemid,"z03");
                    String level = "0";
                    
                    if("accept_post".equalsIgnoreCase(itemid)){
                        item = new FieldItem();
                        item.setItemid(itemid);
                        item.setItemdesc("自动接收职位申请");
                        item.setItemtype("A");   
                        item.setItemlength(1); 
                        item.setCodesetid("45");
                   }else if("apply_control".equalsIgnoreCase(itemid)){
                       item = new FieldItem();
                       item.setItemid(itemid);
                       item.setItemdesc("不符合职位筛选规则不允许申请");
                       item.setItemtype("A");   
                       item.setItemlength(1); 
                       item.setCodesetid("45");
                   }
          
                    if(item!=null&&("1".equals(item.getState())&&"1".equals(item.getUseflag())||"z0384".equalsIgnoreCase(itemid)
                    		||z0390.equalsIgnoreCase(itemid)||requiredstr.indexOf(item.getItemid())>-1
                    		||"z0301".equals(item.getItemid())||"z0101".equals(item.getItemid()))){
                        String codeSetid = item.getCodesetid();
                        if("z0385".equalsIgnoreCase(itemid))
                            codeSetid = "35";
                        else if(z03a2.equalsIgnoreCase(itemid))
                        	codeSetid = "DL";
                        
                        if(codeSetid!=null&&codeSetid.trim().length()>0&&!"0".equals(codeSetid)&&!"UM".equalsIgnoreCase(codeSetid)&&!"UN".equalsIgnoreCase(codeSetid)){
                            level = getCodeSetLayer(codeSetid);
                            codeList = getCodeList(codeSetid);
                        }
                        
                        if("z0381".equalsIgnoreCase(itemid)){
                            RecruitflowBo rfbo= new RecruitflowBo(conn, userview);
                            codeList = rfbo.getRecruitflowList(0,"valid");
                            level = "1";
                        }
                        
                        if("z0101".equalsIgnoreCase(itemid)){
                            codeList = this.getBatch();
                            level = "1";
                        }
                        
                        if(item.isFillable()||requiredstr.lastIndexOf(itemid)!=-1){
                            required="y";
                        }
                        
                        if("z0385".equalsIgnoreCase(itemid)||z03a2.equalsIgnoreCase(itemid))
                            level = "0";
                        
                        PositionDataMap data = new PositionDataMap(itemid,item.getItemdesc(),codeSetid,level,item.getItemtype(),codeList,required,String.valueOf(item.getItemlength()));
                        map.put((String)listTemp.get(j), data);
                        
                    }else{
                        //  目前没有不是业务字典里的
                        if(itemid.indexOf("subject_")!=-1)
                        {
                            for(int n=0;n<sbjList.size();n++)
                            {
                                sbjBean = (LazyDynaBean) sbjList.get(n);
                                if(itemid.equalsIgnoreCase("subject_"+sbjBean.get("codeitemid").toString()))
                                {    
                                    codeList = this.getSubjList(sbjBean.get("codeitemid").toString(),"0");
                                    if(codeList.size()<1)
                                    {
                                        codeList = this.getSubjList(sbjBean.get("codeitemid").toString(),"1");
                                    }
                                    level = "1";
                                    PositionDataMap data = new PositionDataMap(itemid,sbjBean.get("codeitemdesc").toString(),"79",level,"A",codeList,required,"");
                                    map.put((String)listTemp.get(j), data);
                                }
                            }
                        }
                        
                        if(item == null)
                            continue;
                        
                        if (!"accept_post".equalsIgnoreCase(item.getItemid())&&!"apply_control".equalsIgnoreCase(item.getItemid()))
                            continue;
                        
                        String codesetid = item.getCodesetid() == null ? "" : item.getCodesetid();
                        if (StringUtils.isEmpty(codesetid.trim()) || "0".equals(codesetid) 
                                || "UM".equalsIgnoreCase(codesetid)) 
                            continue;
                        
                        level = getCodeSetLayer(codesetid);
                        codeList = getCodeList(codesetid);
                        
                        PositionDataMap data = new PositionDataMap(itemid, item.getItemdesc(), codesetid, level, item.getItemtype(), codeList, required, String.valueOf(item.getItemlength()));
                        map.put((String) listTemp.get(j), data);
                            
                    }                    
                }
       
            }
            
            //取得每个小模块儿的标题
            //getTableTitle(map);
            map.put("table0", "招聘职位");
            map.put("table1", "职位描述");
            if(sbjList.size()>0)
            {               
                map.put("table2", "考试科目");
                map.put("table3", "职位发布");
            }else{
                map.put("table2", "职位发布");
            }
            
        }catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally{
            PubFunc.closeResource(rs);
        }
        
        return map;
    }
    
    /**
     * 获得1级代码类的所有
     * @param codesetid
     * @return
     * @throws GeneralException 
     * @throws SQLException 
     */
    public ArrayList getCodeList(String codesetid) throws GeneralException  {
        ArrayList codelist = new ArrayList();
        ArrayList templist = new ArrayList();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String backdate = sdf.format(new Date());

        RowSet rs = null;
        templist.add(codesetid);
        try{
            StringBuffer sql = new StringBuffer();
        	sql.append("select codeitemid,codeitemdesc,layer");
        	sql.append(" from codeitem");
        	sql.append(" where codesetid=?");
        	sql.append(" and invalid=1");
        	sql.append(" and " + Sql_switcher.dateValue(backdate) + " between start_date and end_date");
        	sql.append(" order by a0000,codeitemid");
            rs = dao.search(sql.toString(), templist);
            while (rs.next()) {
                String str = rs.getString("codeitemid")+"`"+rs.getString("codeitemdesc")+"`"+rs.getString("layer");
                codelist.add(str);
            }
        }catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally{
            PubFunc.closeResource(rs);
        }
        
        
        return codelist;
    }

    /**
     * 
     * @param z0301 当z0301有值的时候说明走的是编辑
     * @return
     * @throws SQLException 
     * @throws GeneralException 
     */
    public String getPositionJson(String z0301) throws GeneralException, SQLException {
        
        ArrayList fieldList = getPositionPageMap();
        HashMap mapData = getPositionPageData();
        HashMap dataPosition = new HashMap();
        if(z0301!=null&&z0301.length()>0)
            dataPosition = getPositionById(z0301);
        
        ArrayList endBatch = this.getEndBatch();
        
        HashMap map = new HashMap();
        map.put("datafield", fieldList);
        map.put("pageData", mapData);
        map.put("endBatch", endBatch);
        if(dataPosition.size()>0){
            map.put("dataPosition", dataPosition);
        }
        String jsonStr = JSON.toString(map);
        
        
        return jsonStr;
    }
    /**
     * 根据z0301查出职位详情
     * @param z0301
     * @return
     * @throws GeneralException 
     */
    private HashMap getPositionById(String z0301) throws GeneralException {
        HashMap map = new HashMap();
        RowSet rs = null;
        RowSet rs1 = null;
        RowSet rs2 = null;
        RowSet rs3 = null;
        try{
            ArrayList list = new ArrayList();
            getPositionFieldList(list);
            StringBuffer columns = new StringBuffer();
            StringBuffer posSql = new StringBuffer("select ");
            for (int i = 0; i < list.size(); i++) {
                ArrayList listTemp = (ArrayList) list.get(i);
                for (int j = 0; j < listTemp.size(); j++) {
                   String clumn = (String) listTemp.get(j);
                    if(clumn.lastIndexOf("hide")>0){
                        clumn = clumn.substring(0, clumn.lastIndexOf("hide"));
                    }
                    columns.append(clumn+",");
                }
            }
            
            if(columns.length()>0)
                columns.setLength(columns.length()-1);
            
            
            posSql.append(columns);
            posSql.append(" from z03 where z0301 = ?");
            ArrayList valist = new ArrayList();
            valist.add(z0301);
            rs = dao.search(posSql.toString(),valist);
            while (rs.next()) {
                for (int i = 0; i < list.size(); i++) {
                    ArrayList listTemp = (ArrayList) list.get(i);
                    for (int j = 0; j < listTemp.size(); j++) {
                        String clumn = (String) listTemp.get(j);
                        if(clumn.lastIndexOf("hide")>0){
                            clumn = clumn.substring(0, clumn.lastIndexOf("hide"));
                        }
                        
                        FieldItem item = DataDictionary.getFieldItem(clumn);
                        
                        if("accept_post".equalsIgnoreCase(clumn)){
                            item = new FieldItem();
                            item.setItemid(clumn);
                            item.setItemdesc("自动接收职位申请");
                            item.setItemtype("A");   
                            item.setItemlength(1); 
                            item.setCodesetid("45");
                       }else if("apply_control".equalsIgnoreCase(clumn)){
                            item = new FieldItem();
                            item.setItemid(clumn);
                            item.setItemdesc("不符合职位筛选规则不允许申请");
                            item.setItemtype("A");   
                            item.setItemlength(1); 
                            item.setCodesetid("45");
                        }
                        
                        if(item!=null){
                            if("A".equals(item.getItemtype())&&!"0".equals(item.getCodesetid())||"z0357".equals(clumn))
                                map.put(clumn ,getOrgDesc(item.getCodesetid(),rs.getString(clumn)));
                            else{
                                if("D".equalsIgnoreCase(item.getItemtype()))
                                {
                                    if(rs.getDate(clumn)!=null)
                                    {
                                    	RecruitUtilsBo bo = new RecruitUtilsBo(conn);
                                		String dateFormat = bo.getDateFormat(clumn);
                                        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
                                        Date time =rs.getTimestamp(clumn);
                                        map.put(clumn, sdf.format(time));
                                    }else{
                                        map.put(clumn, "");
                                    }
                                }else{                                  
                                    if (rs.getString(clumn) != null){
                                        if("z0301".equalsIgnoreCase(clumn)){
                                            map.put(clumn, PubFunc.encrypt(rs.getString(clumn)));
                                        }else{
                                            map.put(clumn, rs.getString(clumn));
                                        }
                                    }else{
                                        map.put(clumn, "");
                                    }
                                }
                            }
                            
                        }else if(clumn.toLowerCase().indexOf("subject_")!=-1)
                        {
                            map.put(clumn, rs.getString(clumn));
                        }
                    }
                }
    
            }
            
            String rpsql = "select a0100,nbase,a0101,member_id from zp_members where z0301=? and member_type = 1";  //查询招聘负责人
            String rpmsql = "select a0100,nbase,a0101,member_id from zp_members where z0301=? and member_type = 2"; //查询招聘成员
            String derpsql = "select a0100,nbase,a0101,member_id from zp_members where z0301=? and member_type = 3"; //查询部门招聘负责人
            rs1 = dao.search(rpsql,valist);
            rs2 = dao.search(rpmsql,valist);
            rs3 = dao.search(derpsql,valist);
            putPersonInMap(rs1,map,1);
            putPersonInMap(rs2,map,2);
            putPersonInMap(rs3,map,3);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally{
            PubFunc.closeResource(rs);
            PubFunc.closeResource(rs1);
            PubFunc.closeResource(rs2);
            PubFunc.closeResource(rs3);
        }

        
        return map;
    }
    
    /**
     * 
     * @param codeitemid
     * @param codesetid 
     * @return
     * @throws GeneralException 
     */
    private String getCodeDesc(String codeitemid, String codesetid) throws GeneralException {
        if(codeitemid!=null){
            String sql = "select codeitemdesc from codeitem where codeitemid = ? and codesetid = ? order by a0000";
            ArrayList list = new ArrayList();
            list.add(codeitemid);
            list.add(codesetid);
            String desc = "";
            RowSet rs = null;
            try{
                rs = dao.search(sql, list);
                while(rs.next()){
                    desc = rs.getString("codeitemdesc");
                }
            }catch (Exception e) {
                e.printStackTrace();
                throw GeneralExceptionHandler.Handle(e);
            }finally{
                PubFunc.closeResource(rs);
            }
            return codeitemid+"`"+desc;
            
        }else{
            return "";
        }
    }

    /**
     * 根据codeitemid 得到desc
     * @param codesetid 
     * @param clumn
     * @return
     * @throws GeneralException 
     */
    private String getOrgDesc(String codesetid,String codeitemid) throws GeneralException {
        if(codeitemid!=null){
            String desc = AdminCode.getCodeName(codesetid,codeitemid);
            return codeitemid+"`"+desc;
        }else{
            return "";
        }
        
    }

    /**
     * 将职位负责人和部门负责人存入map中
     * @param rs1
     * @param map 
     * @param type =1代表招聘负责人 =2代表招聘成员 =3代表部门招聘负责人
     * @throws SQLException 
     */
    private void putPersonInMap(RowSet rs, HashMap map, int type) throws SQLException {
        ArrayList list = new ArrayList();
        while(rs.next()){
            HashMap valueMap = new HashMap();
            String a0100 =rs.getString("a0100");
            String name = rs.getString("a0101");
            ArrayList value = new ArrayList();
            value.add(a0100);
            String nbase = rs.getString("nbase");
            valueMap.put("a0100", PubFunc.encrypt(nbase+a0100));
            valueMap.put("url", getPhotoPath(nbase,a0100));
            valueMap.put("name", name);
            valueMap.put("id",rs.getString("member_id"));
            
            if(type==1){
                map.put("responsPosi", valueMap);
            }else if(type==3){
                list.add(valueMap);
            }else if(type==2){
                list.add(valueMap);
            }
            
        }
        if(type==2&&list.size()>0){
            map.put("responsMember", list);
        }
        
        if(type==3&&list.size()>0){
            map.put("depResponsPosi", list);
        }
        
    }
    
    
    /**
     * 保存职位的简历人数（没被录用或淘汰的候选人个数）
     * @param z0301
     * @param opt: 1:   新简历数：应聘该职位且在简历中心没执行接受、拒绝操作的简历数 
     *             2:   进程中的候选人数：没被录用或淘汰的候选人个数
     *             3:   所有简历数
     *             4：  已录用的人数
     * @author dengc
     */
    public void saveCandiatesNumber(String z0301,int opt) throws GeneralException 
    {
        try
        {
            FieldItem item=null;
            String sql="";
            switch(opt){
                case 1:
                    item=DataDictionary.getFieldItem("z0369");
                    if(item!=null&& "1".equals(item.getUseflag()))
                    {
                        sql="select count(a0100) from zp_pos_tache where zp_pos_id=?   and "+Sql_switcher.isnull("status", "'0'")+"='0'";
                    }
                    break;
                case 2://所有阶段的“已淘汰”、“已终止”、“候选人放弃”、“已入职”的都不算在人数里
                    item=DataDictionary.getFieldItem("z0371");
                    if(item!=null&& "1".equals(item.getUseflag()))
                    {
                        sql="select count(a0100) from zp_pos_tache where zp_pos_id=? and status='1'  and "+Sql_switcher.isnull("status", "'0'")+"='1'";
                        sql+=" and resume_flag not in ('0105','0106','0205','0206','0306','0307','0308','0406','0407','0408','0506','0507','0508','0603','0604','0703','0704','0805','0806','1003','1004','1005')";
                        sql+="and "+Sql_switcher.substr("resume_flag","1","2")+" in ('01','02','03','04','05','06','07','08','10') ";
                    }
                    break;
                case 3:
                    item=DataDictionary.getFieldItem("z0323");
                    if(item!=null&& "1".equals(item.getUseflag()))
                    {
                        sql="select count(a0100) from zp_pos_tache where zp_pos_id=?";
                    }
                    break;
                case 4:
                    item=DataDictionary.getFieldItem("z0367");
                    if(item!=null&& "1".equals(item.getUseflag()))
                    {
                        sql="select count(a0100) from zp_pos_tache where zp_pos_id=? and status='1' ";
                        sql+=" and "+Sql_switcher.substr("resume_flag","1","2")+" in ('07','08','10')  and resume_flag not in ('0703','0805','1004')  ";
                    }
                    break;
            }
            if(sql.length()>0)
            {
                ArrayList paramList=new ArrayList();
                paramList.add(z0301);
                RowSet rowSet=this.dao.search(sql,paramList);
                if(rowSet.next())
                {
                    int count=rowSet.getInt(1);
                    dao.update("update z03 set "+item.getItemid()+"="+count+" where z0301=? ",paramList);
                }
            }
            
        }
        catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
    
    /**
     * 获得部门所在单位
     * @param e0122_value
     * @return
     * @throws GeneralException
     */
    private String getBelongUn(String e0122_value)throws GeneralException
    {
        
        String unit="";
        try{
            for(int i=1;i<e0122_value.length();i++)
            {
                String temp=e0122_value.substring(0,e0122_value.length()-i);
                if(AdminCode.getCode("UN",temp)!=null)
                {
                    unit=temp;
                    break;
                }
                
            } 
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return unit;
    }
    
    /**
     * 简历是否进去职位申请流程
     * @param e0122_value
     * @return
     * @throws GeneralException
     */
    public String getPositionFlag(String a0100Str, String name, String z0301, 
    		String nbase)throws GeneralException{
    	RowSet rs = null;
        String flag="true";
        try{
        	StringBuffer sql = new StringBuffer("");
        	sql.append("select status from zp_pos_tache where ( ");
        	String sql2 ="select status from zp_pos_tache where ( (a0100=? and zp_pos_id=?) or (a0100=? and zp_pos_id=?) or (a0100=? and zp_pos_id=?)) and lower(nbase)=? ";
            ArrayList list = new ArrayList();
            ArrayList z03list = new ArrayList();
            String[] a0100s = a0100Str.split(",");
            String[] zp_pos_ids = z0301.split(",");
            
            ResumeBo rebo = new ResumeBo(this.conn);
            for (int i = 0; i < a0100s.length; i++) {
                String a0100 = PubFunc.decrypt(a0100s[i]);
                String zp_pos_id = PubFunc.decrypt(zp_pos_ids[i]);
                z03list.add(zp_pos_id);                
               
                // 岗位号存在且对应职位存在时将数据塞入list
                if (StringUtils.isNotEmpty(zp_pos_id)) { 
                    ArrayList values = new ArrayList();
                    list.add(a0100);
                    list.add(zp_pos_id);
                    sql.append(" (a0100=? and zp_pos_id=?) ");
                    if( i < a0100s.length-1){
                    	sql.append(" or ");
                    }
                }
            }
            sql.append(" ) and lower(nbase)=? ");
            list.add(nbase.toLowerCase());
            rs = dao.search(sql.toString(),list);
            
            while(rs.next()){
            	String status =rs.getString("status"); 
            	if(StringUtils.isNotEmpty(status) && !"0".equalsIgnoreCase(status)){
            		flag = "false";
            	}
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally{
            PubFunc.closeResource(rs);
        }
        return flag;
    }
    
    /**
     * 职位申请
     * @param status 职位申请
     * @param a0100Str 人员编号 
     * @param name 姓名
     * @return z0301 职位编号
     * @throws GeneralException 
     */
    public ArrayList applyPosition(String status, String a0100Str, String name, String z0301, 
            String nbase, String flag) throws GeneralException {      
        ArrayList List = new ArrayList();  
        try{
            String noPositionNames = "";
            ArrayList z03list = new ArrayList();
            int number = 0;
            RecruitflowBo recruitflowBo = new RecruitflowBo(this.conn, this.userview);
            int[] count = {};
            ArrayList list = new ArrayList(); // 用于插入操作
            
            //判断是否是外网招聘调用方法
            if("1".equalsIgnoreCase(flag)){
                a0100Str = PubFunc.encrypt(a0100Str);
                z0301 = PubFunc.encrypt(z0301);
                a0100Str = a0100Str + ",";
                z0301 = z0301 + ",";           
            }
            
            StringBuffer updateStr = new StringBuffer("");
            updateStr.append("update zp_pos_tache set status=?");
            //判断职位申请是否是拒绝状态
            if (!"2".equals(status))
                updateStr.append(",link_id=?,resume_flag=?");
            else
                updateStr.append(",resume_flag=''");
            updateStr.append(" where a0100=? and zp_pos_id=? and lower(nbase)=?");

            int filterNum = 0;
            String[] a0100s = a0100Str.split(",");
            String[] zp_pos_ids = z0301.split(",");
            String[] names = null;
            if(StringUtils.isNotEmpty(name))
                names = name.split(",");
            
            ResumeBo rebo = new ResumeBo(this.conn);
            for (int i = 0; i < a0100s.length; i++) {
                String a0100 = PubFunc.decrypt(a0100s[i]);
                String zp_pos_id = PubFunc.decrypt(zp_pos_ids[i]);
                
                if(StringUtils.isEmpty(zp_pos_id)&&names!=null) {
                    if(number < 6)
                        noPositionNames += names[i] + ",";
                    
                    number++;
                    continue;
                }
                
                z03list.add(zp_pos_id);                
                String node_flag = rebo.getNode_flag(a0100.trim(), zp_pos_id.trim(), nbase.trim());             
                if ("1".equals(node_flag)) {
                    filterNum++;
                    continue;
                }
                // 岗位号存在且对应职位存在时将数据塞入list
                if (StringUtils.isNotEmpty(zp_pos_id)) { 
                    LazyDynaBean statusBean = recruitflowBo.getFirstStatusByZ0301(zp_pos_id); // 获得职位流程第一环节状态信息
                    ArrayList values = new ArrayList();
                    values.add(status);
                    if (!"2".equals(status) && statusBean != null) {
                        values.add((String) statusBean.get("link_id"));
                        values.add((String) statusBean.get("status"));
                    }
                    
                    values.add(a0100);
                    values.add(zp_pos_id);
                    values.add(nbase.toLowerCase());
                    list.add(values);
                }
            }

            StringBuffer mailmsg = new StringBuffer();
            // 选中数据都有职位时才进行操作
            if (list.size() == a0100s.length) { 
                count = dao.batchUpdate(updateStr.toString(), list);
                PositionBo positionBo = new PositionBo(this.conn, dao, this.userview);
                for (int i=0;i<z03list.size();i++) {
                    String z0301t = (String) z03list.get(i);
                    positionBo.saveCandiatesNumber(z0301t, 2); // 进程中的候选人数：没被录用或淘汰的候选人个数
                    positionBo.saveCandiatesNumber(z0301t, 1); // 新简历数：应聘该职位且在简历中心没执行接受、拒绝操作的简历数
                    positionBo.saveCandiatesNumber(z0301t, 3); // 所有简历数
                }
            }
            
            mailmsg.append("true");
            List.add(mailmsg);
            List.add(count);
            List.add(filterNum);
           
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        
        return List;
    }
    
    
    /**
     * 保存职位信息
     * @param dataList 职位字段
     * @param objdata  职位信息
     * @param type   =report 保存报批   =save 只保存不发布
     * @throws GeneralException 
     */
    public String savePosition(ArrayList dataList, JSONObject objdata,
            String type) throws GeneralException {
        String az0301 = "";
        try{
            StringBuffer sbq = new StringBuffer("");
            StringBuffer column = new StringBuffer("");
            ArrayList valueList = new ArrayList();
            String z0325_value="";
           // boolean isZ0321=false; //  编辑页面是否有所属单位字段
            for (int i = 0; i < dataList.size(); i++) {
                String clumn = (String) dataList.get(i);
                String value = PubFunc.toReplaceStr(objdata.getString(clumn)) ;
                if("z0301".equals(clumn)){ 
                    az0301 = PubFunc.decrypt(value);
                    continue;
                }
                //  continue;
                if (value != null && value.length() > 0||"edit".equalsIgnoreCase(type)) {
                    if("".equals(value))
                        value = null;
                    if("z0325".equals(clumn))
                        z0325_value=value;
                    sbq.append("?,");
                    column.append(clumn + ",");
                    FieldItem item = DataDictionary.getFieldItem(clumn);

                    if("accept_post".equalsIgnoreCase(clumn)){
                        item = new FieldItem();
                        item.setItemid(clumn);
                        item.setItemdesc("自动接收职位申请");
                        item.setItemtype("A");   
                        item.setItemlength(1); 
                        item.setCodesetid("45");
                   }else if("apply_control".equalsIgnoreCase(clumn)){
                       item = new FieldItem();
                       item.setItemid(clumn);
                       item.setItemdesc("不符合职位筛选规则不允许申请");
                       item.setItemtype("A");   
                       item.setItemlength(1); 
                       item.setCodesetid("45");
                   }

                    
                    if(item!=null&&"D".equalsIgnoreCase(item.getItemtype())&&value!=null)
                    {
                    	RecruitUtilsBo bo = new RecruitUtilsBo(conn);
                		String dateFormat = bo.getDateFormat(clumn);
                		String zp_pos_apply_end_field = SystemConfig.getPropertyValue("zp_pos_apply_end_field");
                		
                		if("yyyy-MM-dd".equalsIgnoreCase(dateFormat) && ("z0331".equalsIgnoreCase(clumn)
                				||	(this.getItemInZ03(zp_pos_apply_end_field) 
                						&& zp_pos_apply_end_field.equalsIgnoreCase(clumn)))){
                			value =  value + " 23:59:59";
                			valueList.add(new Timestamp(DateUtils.getDate(value, "yyyy-MM-dd HH:mm:ss").getTime()));
                		}else if("yyyy-MM-dd HH:mm".equalsIgnoreCase(dateFormat) && ("z0331".equalsIgnoreCase(clumn)
                        				||	(this.getItemInZ03(zp_pos_apply_end_field) 
                        						&& zp_pos_apply_end_field.equalsIgnoreCase(clumn)))){
                			value =  value + ":59";
                			valueList.add(new Timestamp(DateUtils.getDate(value, "yyyy-MM-dd HH:mm:ss").getTime()));
                		}else 
                			valueList.add(new Timestamp(DateUtils.getDate(value, dateFormat).getTime()));
                    }else{
                        valueList.add(value);
                    }
                }
            }
            /*//写入需求部门时自动填充单位
            if(z0325_value.length()>0)
            {
                String unit=getBelongUn(z0325_value);
                if(unit!=null&&unit.length()>0)
                {
                     column.append("z0321,");
                     sbq.append("?,");
                     valueList.add(unit);
                }
            }*/
                
                
            if ("edit".equalsIgnoreCase(type)) {
                StringBuffer upSql = new StringBuffer("update z03 set ");
                String[] columnstr =column.toString().split(",");
                for (int i = 0; i < columnstr.length; i++) {
                    upSql.append(columnstr[i]+"=? ,");
                }
                if(columnstr.length>0)
                    upSql.setLength(upSql.length()-1);
                upSql.append("where z0301 = ?"); 
                valueList.add(az0301);
                dao.update(upSql.toString(), valueList);
    
            } else {//新建
                IDGenerator idg = new IDGenerator(2, this.conn);
                az0301 = idg.getId("Z03.Z0301");
                column.append("z0301,");
                column.append("z0319,z0310,z0307,z0309");//
                valueList.add(az0301);
                if("report".equals(type))
                    valueList.add("02");
                else
                    valueList.add("01");
                valueList.add(userview.getUserFullName()); 
                valueList.add(java.sql.Date.valueOf((new java.sql.Date(new Date().getTime()))+""));
                valueList.add(userview.getUserName());
                sbq.append("?,?,?,?,?");
                StringBuffer inserSql = new StringBuffer("insert into z03(");
                inserSql.append(column);
                inserSql.append(")");
                inserSql.append(" values(");
                inserSql.append(sbq);
                inserSql.append(")"); 
                dao.insert(inserSql.toString(), valueList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return az0301;
    }
    
    /**
     * 让新建页面一加载就能获得招聘负责人信息
     * @param reponsA0100
     * @return
     * @throws SQLException 
     */
    public String getResponsPosiPhoto(String reponsA0100) throws SQLException {
        String pohosrc = "";
        pohosrc = getPhotoPath(userview.getDbname(),reponsA0100);
        return pohosrc;
    }
    
    /**
     * 保存招聘成员等信息
     * @param memberdata
     * @param type
     * @param z0301 
     * @throws GeneralException 
     */
    public void savePosMember(JSONObject memberdata, String type, String z0301) throws GeneralException {
        //保存部门招聘负责人
        JSONArray depResponsPosiId = memberdata.getJSONArray("depResponsPosiId"); 
        String responsPosiId = PubFunc.decrypt(memberdata.getString("responsPosiId"));
        JSONArray jarr = memberdata.getJSONArray("ponsMemberId");

        for (int i = 0; i < depResponsPosiId.size(); i++) {
            String str = PubFunc.decrypt((String) depResponsPosiId.get(i));
            saveMenber(str, 3, z0301);
        }
        
        // 保存招聘负责人
        if (responsPosiId != null && responsPosiId.length() > 0||userview.getStatus()==0){
            saveMenber(responsPosiId, 1, z0301);
        }

        for (int i = 0; i < jarr.size(); i++) {
            String str = PubFunc.decrypt((String) jarr.get(i));
            saveMenber(str, 2, z0301);
        }
        
        if("publish".equals(type)){
            saveMenber(userview.getDbname()+userview.getA0100(),4,z0301);
        }
    }
    
   /*
     * 更新成员
     * @param depResponsPosiId
     * @param i
     * @param z0301
     * @throws GeneralException 
     *
    private void updateMenber(String nbsa0100, int i, String z0301) throws GeneralException {
        try{
            String nbs = nbsa0100.substring(0,3);
            String a0100 = nbsa0100.substring(3, nbsa0100.length());
            String nbssql= "select b0110,e0122,e01a1,a0101 from "+nbs+"A01 where a0100 = ?";
            ArrayList nbslist = new ArrayList();
            ArrayList memlist = new ArrayList();
            nbslist.add(a0100);
            RowSet rs = dao.search(nbssql,nbslist);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        
        
    }*/

    /**
     * 根据 a0100来给zp_ members
     * @param depResponsPosiId
     * @param i =1招聘负责人 =2参与人 =3部门负责人
     * @param z0301 
     * @throws GeneralException 
     */
    private String saveMenber(String nbsa0100, int i, String z0301) throws GeneralException {
        IDGenerator idg = new IDGenerator(2, this.conn);
        String member_id = idg.getId("zp_members.member_id");
        RowSet rs = null;
        try{
            String nbs = "";
            String a0100="";
            if(StringUtils.isNotBlank(nbsa0100)&&nbsa0100.length()>0){
                nbs = nbsa0100.substring(0,3);
                a0100 = nbsa0100.substring(3, nbsa0100.length());
                
            }else{
                nbs = "usr";
            }
            String nbssql= "select b0110,e0122,e01a1,a0101 from "+nbs+"A01 where a0100 = ?";
            ArrayList nbslist = new ArrayList();
            ArrayList memlist = new ArrayList();
            nbslist.add(a0100);
            rs = dao.search(nbssql,nbslist);
            
            memlist.add(member_id);
            memlist.add(String.valueOf(i));
            memlist.add(z0301);
            memlist.add(nbs);
            memlist.add(a0100);
            String b0110="";
            String e0122="";
            String e01a1="";
            String a0101="";
            if(rs.next()){
                b0110 =rs.getString("b0110"); 
                e0122 =rs.getString("e0122"); 
                e01a1 =rs.getString("e01a1");
                a0101 =rs.getString("a0101");
            }
            if(a0100==null||a0100.length()<1)
                a0101 = userview.getUserFullName();
            memlist.add(b0110);
            memlist.add(e0122);
            memlist.add(e01a1);
            memlist.add(a0101);
            memlist.add(userview.getUserName());
            memlist.add(userview.getUserFullName());
            String sql = "insert into zp_members(member_id,member_type,z0301,nbase,a0100,b0110,e0122,e01a1,a0101" +
                    ",create_user,create_fullname ) values(?,?,?,?,?,?,?,?,?,?,?)";
            dao.insert(sql, memlist);
           
            String timesql = "update zp_members set create_time= "+Sql_switcher.sqlNow()+" where member_id=?";
            ArrayList timelist = new ArrayList();
            timelist.add(member_id);
            dao.update(timesql, timelist);
          //发布、暂停、结束 后刷新外网职位列表
            EmployNetPortalBo bo = new EmployNetPortalBo(this.conn);
            bo.refreshStaticAttribute();
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally{
            PubFunc.closeResource(rs);
        }
        return member_id;
    }
    
    
    
    //从选人控件copy过来的
    public String getPhotoPath(String nbase, String a0100) {
        PhotoImgBo imgBo = new PhotoImgBo(conn);
        return imgBo.getPhotoPath(nbase, a0100);
    }
    
    
    /**
     * 根据传来的memberid删除成员
     * @param memberid
     * @throws SQLException 
     */
    public void deleteByMemberId(String memberid) throws SQLException {
        String sql ="delete zp_members where member_id= ?";
        ArrayList list = new ArrayList();
        list.add(memberid);
        dao.delete(sql, list);
    }
    /**
     * 更新成员
     * @param nbsa0100
     * @param parseInt
     * @param z0301
     * @param a0101 
     * @param e01a1 
     * @param e0122 
     * @param b0110 
     * @param memberId 
     * @throws GeneralException 
     */
    public void updateMenber(String nbsa0100, int parseInt, String z0301, String b0110, String e0122, String e01a1, String a0101, String memberId) throws GeneralException {
        try {
            String nbs = nbsa0100.substring(0, 3);
            String a0100 = nbsa0100.substring(3, nbsa0100.length());
            StringBuffer buf = new StringBuffer("update zp_members set ");
            buf.append("nbase='" + nbs + "',");
            buf.append("a0100='" + a0100 + "',");
            buf.append("b0110='" + b0110 + "',");
            buf.append("e0122='" + e0122 + "',");
            buf.append("a0101='" + a0101 + "',");
            buf.append("e01a1='" + e01a1 + "',");
            buf.append("create_user='" + userview.getUserName() + "',");
            buf.append("create_fullname='" + userview.getUserFullName() + "',");
            buf.append("create_time= " +Sql_switcher.sqlNow());
            buf.append(" where member_id = ?");
            ArrayList list = new ArrayList();
            list.add(memberId);
            dao.update(buf.toString(), list);
        }catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        
        
    }

    public String saveTheMenber(String nbsa0100, int parseInt, String z0301) throws GeneralException {
        
        return saveMenber(nbsa0100, parseInt, z0301);
    }
    /**
     * @param  z0301 z03表中主键
     * @return String 返回当前用户在当前职位中担任的角色=1招聘负责人=2招聘成员=3部门招聘负责人=4发布人
     * @throws GeneralException 
     */
    public String getMemTypeByUserView(String z0301) throws GeneralException {
        if(z0301==null||z0301.length()==0){
            return "";
        }
        if(userview.isSuper_admin()){
            return "1";
        }
        String type ="";
        String a0100 = userview.getA0100();
        RowSet rs = null;
        try {
            String sql = "select member_type from zp_members where z0301 = ? and a0100 = ? and member_type <> 4 ";
            ArrayList list = new ArrayList();
            list.add(z0301);
            list.add(a0100);
            rs = dao.search(sql,list);
            if(rs.next()){
                type= rs.getString("member_type");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally{
            PubFunc.closeResource(rs);
        }
        
        return type;
    }
    /**
     * 当用户没有报批权限的时候，在新建职位的时候就不显示保存&报批的按钮
     * @return
     */
    public String getPriveForPublish() {
        String publish = "y";
        if(this.userview.isSuper_admin()||this.userview.hasTheFunction("3110112")){
            
        }else{
            publish = "n";
        }
        return publish;
    }
    
    private ButtonInfo newButton(String text,String id,String handler,String icon,String getdata)
    {  
        ButtonInfo button = new ButtonInfo(text,handler); 
        if(getdata!=null)
            button.setGetData(Boolean.valueOf(getdata).booleanValue());
        if(icon!=null)
            button.setIcon(icon);
        if(id!=null)    
            button.setId(id);
        return button;
    }
    /**
     * 查询功能按钮
     * @param isModule 
     * @return
     */
    public ArrayList getButtonList(boolean isModule) {
        ArrayList buttonList = new ArrayList();
        ArrayList menuList = new ArrayList();
        RecruitUtilsBo ubo = new RecruitUtilsBo(conn);
        
        if(isModule){//从推荐职位过来
            buttonList.add(newButton("推荐",null,"Global.successRecommend",null,"true"));//成功推荐职位
            buttonList.add(newButton("返回",null,"Global.returnBack",null,"true"));//返回到哪个页面
        }else{//从简历中心过来
        	if(userview.isSuper_admin()||userview.hasTheFunction("311010101")){
        		LazyDynaBean exportBean = RecruitUtilsBo.getMenuBean("导出Excel", null, "/images/export.gif", "cusMenu", "export" ,new ArrayList());
        		menuList.add(exportBean);
        	}
        	if(userview.isSuper_admin()||userview.hasTheFunction("311010102")){
        		LazyDynaBean importBean = RecruitUtilsBo.getMenuBean("导入职位", "Global.importData()", "", null, null, new ArrayList());
            	menuList.add(importBean);
        	}
        	
        	if(userview.isSuper_admin()||userview.hasTheFunction("311010103")){
        		LazyDynaBean analyseBean = RecruitUtilsBo.getMenuBean("统计分析", null, null, "cusMenu", "analyse", new ArrayList());
            	menuList.add(analyseBean);
        	}
        	
        	if(userview.isSuper_admin()||userview.hasTheFunction("3110111")) {
        		LazyDynaBean matchBean = RecruitUtilsBo.getMenuBean("人岗匹配", "Global.matchPersonnel()", "", null, null, new ArrayList());
        		menuList.add(matchBean);
        	}
        
        	String menuStr = RecruitUtilsBo.getMenuStr(ResourceFactory.getProperty("gz_new.gz_accounting.FunctionNavigation"), "aaaa", menuList);
        	buttonList.add(menuStr);
        	
            if(userview.isSuper_admin()||userview.hasTheFunction("3110105")||userview.hasTheFunction("3110106")){
                buttonList.add("-"); 
                if(userview.isSuper_admin()||userview.hasTheFunction("3110105"))
                    buttonList.add(newButton("新建职位",null,"Global.insertPosition",null,"true"));
                if(userview.isSuper_admin()||userview.hasTheFunction("3110106"))
                    buttonList.add(newButton("删除职位",null,"Global.deletePosition",null,"true"));
                
            }
            
            if(userview.isSuper_admin()||userview.hasTheFunction("3110112")||userview.hasTheFunction("3110113")){
                if(userview.isSuper_admin()||userview.hasTheFunction("3110112"))
                    buttonList.add(newButton("报批",null,"Global.reportPosition",null,"true"));
                if(userview.isSuper_admin()||userview.hasTheFunction("3110113"))
                    buttonList.add(newButton("撤回",null,"Global.revokePosition",null,"true"));
            }
            
            String publishWithApprove = SystemConfig.getPropertyValue("zp_pos_publish_with_approve");
            if(userview.isSuper_admin()||userview.hasTheFunction("3110114")||userview.hasTheFunction("3110115")){
            	if(!"true".equalsIgnoreCase(publishWithApprove) || !(userview.isSuper_admin()||userview.hasTheFunction("3110102"))){
            		 if(userview.isSuper_admin()||userview.hasTheFunction("3110114"))
                         buttonList.add(newButton("批准",null,"Global.approvePosition",null,"true"));
            	}
               
                if(userview.isSuper_admin()||userview.hasTheFunction("3110115"))
                    buttonList.add(newButton("退回",null,"Global.returnPosition",null,"true"));
            }
            
            if(userview.isSuper_admin()||userview.hasTheFunction("3110102")||userview.hasTheFunction("3110103")||userview.hasTheFunction("3110104")){
                buttonList.add("-"); 
                if(userview.isSuper_admin()||userview.hasTheFunction("3110102"))
                    buttonList.add(newButton("发布职位",null,"Global.publishPosition",null,"true"));
                if(userview.isSuper_admin()||userview.hasTheFunction("3110103"))
                    buttonList.add(newButton("暂停",null,"Global.stopPosition",null,"true"));
                if(userview.isSuper_admin()||userview.hasTheFunction("3110104"))
                    buttonList.add(newButton("结束",null,"Global.toEndPosition",null,"true"));
                
            }
           
        }
        ButtonInfo queryBox = new ButtonInfo();
        queryBox.setFunctionId("ZP0000002081");
        queryBox.setType(ButtonInfo.TYPE_QUERYBOX);
        queryBox.setText("请输入单位、部门、职位、工作地点...");
        buttonList.add(queryBox);
        return buttonList;
    }
    
    /**
     * 
     * @param z0301s
     * @param a0101s
     * @throws GeneralException 
     */
    public String recommendPosition(String z0301s, String a0100s) throws GeneralException {
        ArrayList z0301List = new ArrayList();
        ArrayList a0100sList = new ArrayList();
        com.hjsj.hrms.module.hire.businessobject.PositionBo positionBo = new com.hjsj.hrms.module.hire.businessobject.PositionBo(conn, userview);
        ParameterXMLBo bo = new ParameterXMLBo(conn);
        HashMap map = bo.getAttributeValues();
        String max_count = (String) map.get("max_count"); //配置参数得到的岗位申请最大数
        
        if(max_count==null||"".equals(max_count))
            max_count="3";
        
        String a0100ForUnRecommend="";
        RowSet rs = null;
        try {
            toDecryptSplit(z0301s, z0301List);
            String nbase = PubFunc.decrypt(a0100s.split("`")[1]);
            String a0100str = a0100s.split("`")[0];
            toDecryptSplit(a0100str,a0100sList);
            //推荐职位的时候，将人员插入候选人表中
            for (int j = 0; j < a0100sList.size(); j++) {
                String a0100 =(String )a0100sList.get(j);
                for (int i = 0; i < z0301List.size(); i++) {
                    String z0301 = (String) z0301List.get(i);
                    int num = positionBo.getSizeJobs(a0100, z0301);
                    toInsertPerson(a0100,z0301,nbase,num+1);
                }
            }
            if(a0100ForUnRecommend.length()>0)
                a0100ForUnRecommend+=nbase;
            return a0100ForUnRecommend;
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally {
            PubFunc.closeResource(rs);
        }
        
        
    }
    
    private int toSearchCount(String a0100) throws GeneralException {
        int count = 0;
        RowSet rs = null;
        try{
            String sql = "select count(*) counts from zp_pos_tache where a0100 = ?";
            ArrayList list = new ArrayList();
            list.add(a0100);
            rs = dao.search(sql,list);
            if(rs.next())
                count = rs.getInt("counts");
                
            return count;
        }catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally{
            PubFunc.closeResource(rs);
        }
    }

    /**
     * 基于职位ID获得招聘流程第一环节的状态信息 
     * @param z0301   招聘职位id  
     * @return
     * @throws GeneralException
     */
    public LazyDynaBean getFirstStatusByZ0301(String z0301)throws GeneralException 
    { 
        LazyDynaBean bean=null;
        RowSet rowSet=null;
        try
        {
            ContentDAO dao = new ContentDAO(this.conn);
            ArrayList valueList=new ArrayList();
            String sql="select * from  zp_flow_links where flow_id=(select z0381 from z03 where z0301=? ) and valid=1   order by seq"; 
            valueList.add(z0301);  
         
            rowSet=dao.search(sql,valueList);
            if(rowSet.next())
            {
                String _link_id=rowSet.getString("id"); 
                rowSet=dao.search("select status from zp_flow_status where link_id='"+_link_id+"' and valid=1 order by seq ");
                if(rowSet.next())
                {
                    bean=new LazyDynaBean();
                    bean.set("link_id",_link_id);
                    bean.set("status",rowSet.getString(1)!=null?rowSet.getString(1):"");
                }
                
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        finally
        {
            PubFunc.closeDbObj(rowSet);
        }
        return bean;
    }
    
    /**
     * 将人员插入表中 插入zp_pos_tache
     * @param a0101
     * @param z0301
     * @param nbase
     * @param i 
     * @throws GeneralException 
     */
    private void toInsertPerson(String a0100, String z0301, String nbase, int i) throws GeneralException {
        try {
            LazyDynaBean statusBean = this.getFirstStatusByZ0301(z0301);
            if(statusBean==null){//排除流程第一个环节没有已启用的可用状态
                throw GeneralExceptionHandler.Handle(new Exception("该职位对应的招聘流程第一个环节无已启用的状态"));
            }else{
                ArrayList valueList = new ArrayList();
                String sql = "insert into zp_pos_tache(zp_pos_id,a0100,thenumber," +
                "status,resume_flag,link_id,nbase,recusername,recdate,apply_date) " +
                "values(?,?,?,?,?,?,?,?,?,?)";
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:MM:ss");
                String time = dateFormat.format(new Date(System.currentTimeMillis())).toString();
                valueList.add(z0301);
                valueList.add(a0100);
                valueList.add(i+"");
                valueList.add("1");
                valueList.add(statusBean.get("status").toString());
                valueList.add(statusBean.get("link_id").toString());
                valueList.add(nbase);
                valueList.add(userview.getUserFullName());
                valueList.add(Timestamp.valueOf(time));
                valueList.add(Timestamp.valueOf(time));
                /*if(Sql_switcher.searchDbServer()!=Constant.ORACEL)
                {
                    valueList.add(dateFormat.format(new Date(System.currentTimeMillis())).toString());
                    valueList.add(dateFormat.format(new Date(System.currentTimeMillis())).toString());
                }*/
                dao.insert(sql, valueList);
                saveCandiatesNumber(z0301, 2);
                saveCandiatesNumber(z0301, 3);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    //给加密的以逗号分隔的字符串 解析成 集合
    private void toDecryptSplit(String str, ArrayList list) {
        String[] split = str.split(",");
        for (int i = 0; i < split.length; i++) {
            list.add(PubFunc.decrypt(split[i]));
        }
    }
    /**
     * 当从人员库中进来的要从人员库中移除这些人员
     * @param a0100s
     * @param a0100ForUnRecommend 
     * @throws GeneralException 
     */
    public void deleteOnRecommend(String a0100s, String a0100ForUnRecommend) throws GeneralException {
        ArrayList a0100sList = new ArrayList();
        try{
            String a0100str = a0100s.split("`")[0];
            toDecryptSplit(a0100str,a0100sList);
            ArrayList valueList = new ArrayList();
            StringBuffer buf = new StringBuffer();
            for (int i = 0; i < a0100sList.size(); i++) {
                if(a0100ForUnRecommend.lastIndexOf((String) a0100sList.get(i))<0){
                    valueList.add(a0100sList.get(i));
                    buf.append("?,");
                }
            }
            if(buf.length()>0)
                buf.setLength(buf.length()-1);
            String sql = "delete zp_talents where a0100 in("+buf.toString()+")";
            if(valueList.size()>0)
            {               
                dao.delete(sql, valueList);
            }
        }catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    public String getMsgByUnRecommend(String a0100ForUnRecommend) throws GeneralException {
        RowSet rs = null;
        try{
            String[] split = a0100ForUnRecommend.split(",");
            StringBuffer msg = new StringBuffer("");
            for (int i = 0; i < split.length - 1; i++) {
                ArrayList list = new ArrayList();
                String sql = "select a0101 from " + split[split.length - 1]
                        + "a01 where a0100= ?";
                list.add(split[i]);
                rs = dao.search(sql, list);
                if (rs.next())
                    msg.append(rs.getString("a0101") + ",");

            }
            if(msg.length()>0){
                msg.setLength(msg.length()-1);
                msg.append("已申请的职位达到系统设定的岗位最大申请个数"); 
            }
            
            return msg.toString();
        }catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally{
            PubFunc.closeResource(rs);
        }
        
    }
   /**
    * 根据业务字典获取职位所有参数列
    * @return
    */
   public ArrayList getColumn()
   {
       ArrayList columnList = new ArrayList();
       try {
           ArrayList fieldList = DataDictionary.getFieldList("z03", Constant.USED_FIELD_SET);
           for(int i=0;i<fieldList.size();i++){
               FieldItem item = (FieldItem) fieldList.get(i);
               String colunmName=item.getItemid();
               if("z0351".equalsIgnoreCase(colunmName))
                   continue;
               
               if("z0365".equalsIgnoreCase(colunmName))
                   continue; 
               
               if("z0373".equalsIgnoreCase(colunmName))
                   continue;
              
               
               if(item!=null&&"1".equals(item.getState())&&"1".equals(item.getUseflag()))
                   columnList.add(colunmName);
           }
       } catch (Exception e) {
            e.printStackTrace();
       }
       return columnList;
   }
   
   /***
    * 获取当前能够进行推荐的人员
   * @Title:getA0100s
   * @Description：
   * @author xiexd
   * @param a0100s
   * @return
    */
   public String getA0100s(String a0100s){
       String resume_state_field="";
       RecordVo vo=ConstantParamter.getConstantVo("ZP_DBNAME");
       String dbname="";  //应聘人员库
       if(vo!=null){            
            dbname=vo.getString("str_value");
       }
       RowSet rs = null;
       try {
           ParameterXMLBo bo=new ParameterXMLBo(this.conn,"1");
           HashMap map=bo.getAttributeValues();
           if(map!=null&&map.get("resume_state")!=null){               
               resume_state_field=(String)map.get("resume_state");
           }else{
               return a0100s;
           }
           
           //将字符串转换为json对象
           JSONObject jsonObject = JSONObject.fromObject(a0100s);
           StringBuffer sql = new StringBuffer("select a0100 from ");
           sql.append(" zp_pos_tache");
           sql.append(" where  "+ Sql_switcher.isnull("resume_flag", "0") +"='1003' ");
           sql.append(" and (a0100 in(");
           String userId = "";
           JSONObject json = new JSONObject();
           ContentDAO dao = new ContentDAO(conn);
           //a0100下的值包含的是json数组
           JSONArray array = JSONArray.fromObject(jsonObject.get("a0100").toString());
           for(int i=0;i<array.size();i++)
           {
               json = (JSONObject)array.get(i);
               userId =PubFunc.decrypt(json.getString("a0100").toString());
               if(i/1000>0 && i%1000==0){
                   sql.setLength(sql.length()-1);
                   sql.append(") or a0100 in('"+userId+"',");
               }
               else
                   sql.append("'"+userId+"',");
               
           }
           sql.setLength(sql.length()-1);
           sql.append("))");
           rs = dao.search(sql.toString());
           HashMap<String,String> cantA0100 = new HashMap<String, String>();
           while(rs.next()){
               cantA0100.put(rs.getString("a0100"), "1003");
           }
               
           a0100s = "{\"a0100\":[";
           int ps = 0;
           for(int i=0;i<array.size();i++)
           {
               //{"a0100":[{"a0100":"MEcz377r1CCF5X22ZpBefA@3HJD@@3HJD@","z0301":"CR4rYmJfERQtZP0VPZDYNQ@3HJD@@3HJD@","a0101":"dd"},{"a0100":"dwCYtkBQeRm8b4LOTSL@2HJB@Pw@3HJD@@3HJD@","z0301":"mvTOyf4e9wO24CklR@2HJF@gy8w@3HJD@@3HJD@","a0101":"aa"},{"a0100":"nSpgxEgByaBIXB45IPmdaA@3HJD@@3HJD@","z0301":"CR4rYmJfERQtZP0VPZDYNQ@3HJD@@3HJD@","a0101":"韩懿"}],"nbase":"Y4dVEa8M9zE@3HJD@"}
               json = (JSONObject)array.get(i);
               userId =PubFunc.decrypt(json.getString("a0100").toString());
               if(StringUtils.isEmpty(cantA0100.get(userId))){
                   ps++;
                   a0100s += "{\"a0100\":\""+PubFunc.encrypt(userId)+"\",";
                   a0100s += "\"z0301\":\""+json.getString("z0301").toString()+"\",";
                   a0100s += "\"a0101\":\""+json.getString("a0101").toString()+"\"},";
               }else{
            	   return "";
               }
           }
           if(ps>0)
           {
               a0100s = a0100s.substring(0, a0100s.length()-1);
           }else{
               return "";
           }
           a0100s += "],\"nbase\":\""+jsonObject.get("nbase").toString()+"\"}";
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
             PubFunc.closeResource(rs);
        }
       return a0100s;
   }
   
   /***
    * 判断当前职位下是否含有人员
   * @Title:getPersonNum
   * @Description：
   * @author xiexd
   * @param z0301
   * @return
    */
   public String getPersonNum(String z0301)
   {
       String num = "";
       RowSet rs = null;
       try {
           String sql = "select count(*) num from zp_pos_tache where zp_pos_id='"+z0301+"' and link_id is not null";
           ContentDAO dao = new ContentDAO(conn);
           rs = dao.search(sql);
           if(rs.next())
           {
               num = rs.getString("num");
           }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            PubFunc.closeResource(rs);
        }
       return num;
   }
   /**
    * 判断申请职位日期是否处于申请职位的报名开始和结束日期范围内
    * @param z0301
    * @return isTimeOut 为true表示不出于范围内，info代表处于小日期前还是大日期后
    */
   public HashMap isTimeOut(String z0301){
       HashMap res = new HashMap();
       String sql = "select z01.z0155,z01.z0157 from Z03 z03 left join z01 z01 on z01.z0101 = z03.z0101 where z03.z0301=?";
       String zp_pos_apply_start_field = SystemConfig.getPropertyValue("zp_pos_apply_start_field");
	   String zp_pos_apply_end_field = SystemConfig.getPropertyValue("zp_pos_apply_end_field");
	  
	   if(this.getItemInZ03(zp_pos_apply_start_field) && this.getItemInZ03(zp_pos_apply_end_field))
		   sql = "select "+zp_pos_apply_start_field+","+zp_pos_apply_end_field+" from Z03 z03 left join z01 z01 on z01.z0101 = z03.z0101 where z03.z0301=?";
    	  
       ArrayList value = new ArrayList();
       value.add(z0301);
       Timestamp startTime = null;
       Timestamp endTime = null;
       RowSet rs = null;
       try{
           rs = dao.search(sql,value);
           while(rs.next()){
        	   if(this.getItemInZ03(zp_pos_apply_start_field) && this.getItemInZ03(zp_pos_apply_end_field)){
        		   startTime = rs.getTimestamp(zp_pos_apply_start_field);//报名开始时间
               	   endTime = rs.getTimestamp(zp_pos_apply_end_field);//报名结束时间
	               if(startTime == null && endTime == null){
	               	   startTime = rs.getTimestamp("z0155");//报名开始时间
	             	   endTime = rs.getTimestamp("z0157");//报名结束时间
	               }
        	   }else{
               	   startTime = rs.getTimestamp("z0155");//报名开始时间
               	   endTime = rs.getTimestamp("z0157");//报名结束时间
        	   }
        		   
               if(startTime == null && endTime == null){//两者皆为空则不控制时间
                   res.put("isTimeOut", false);
                   break;
               }
               
               if("before".equalsIgnoreCase(this.dealDate(startTime, endTime,"yyyy-MM-dd HH:mm:ss"))){
                   res.put("isTimeOut",true);
                   res.put("info","before");
               }else if("after".equalsIgnoreCase(this.dealDate(startTime, endTime,"yyyy-MM-dd HH:mm:ss"))){
                   res.put("isTimeOut",true);
                   res.put("info","after");
               }else
                   res.put("isTimeOut", false);
           }
       }catch(Exception e){
           e.printStackTrace();
       }finally{
           PubFunc.closeResource(rs);
       }
       return res;
   }
   /**
    * 判断当前日期处于给定两个日期的范围内还是范围外
    * @param d1 小日期
    * @param d2 大日期
    * @return before  在小日期前      after   在大日期后    scope 在两个日期范围内
    */
   public String dealDate(Date d1,Date d2,String format){
       Date now  =  new Date();
       SimpleDateFormat sdf = new SimpleDateFormat(format);
       now = DateUtils.getDate(sdf.format(now), format);
       
       if(d1!=null && now.before(d1) && !now.equals(d1))//d1为空的情况下不存在提前申请的情况
           return "before";
       else if(d2!=null && now.after(d2) && !now.equals(d2))//d2为空的情况下不存在在结束之后申请的情况
           return "after";
       else
           return "scope";
   }
   /***
    * 获取招聘批次
   * @Title:getBatch
   * @Description：
   * @author xiexd
   * @return
   * @throws GeneralException
    */
   public ArrayList getBatch() throws GeneralException {
       HireTemplateBo bo = new HireTemplateBo(this.conn);
       bo.getB0110(this.userview);
       ArrayList list = new ArrayList();
       RecruitBatchBo rbbo = new RecruitBatchBo(conn, userview); 
       ArrayList rbList = rbbo.getAllBatchInfos("5");
       CommonData commonData = new CommonData();
       for(int i=0;i<rbList.size();i++)
       {
           commonData = (CommonData) rbList.get(i);
           list.add(commonData.getDataValue()+"`"+commonData.getDataName());
       }
       return list;
   }
   
   /***
    * 自动构建Z03表字段
   * @Title:structureRequirement
   * @Description：
   * @author xiexd
    */
   public void structureRequirement(){
       //查询79号代码类一级数
       String sql = "select * from codeitem where codesetid='79' and codeitemid = parentid and invalid=1 order by codeitemid asc";
    ArrayList currentItem= new ArrayList();
    ExamineeBo bo = new ExamineeBo(conn, userview);
    
    RowSet rs = null;
    String codeitemid = "";
    DbWizard dbWizard = new DbWizard(this.conn);
    Table table = new Table("Z03");
    
    Field temp = null;
    try{
        //获取zp_exam_assign所有列名
        HashMap Z03 = bo.getAssignAllColumns("Z03");
        ArrayList columns = (ArrayList) Z03.get("name");
        rs = dao.search(sql);
        //插入字段
        while(rs.next()){
            codeitemid = rs.getString("codeitemid");
            currentItem.add("subject_"+codeitemid);
            //防止插入的字段是大写
            currentItem.add("subject_".toUpperCase()+codeitemid);
            
            if(columns.contains("subject_"+codeitemid) || columns.contains("SUBJECT_"+codeitemid))
                continue;
            
            temp = new Field("subject_"+codeitemid);
            temp.setDatatype(DataType.STRING);
            temp.setLength(30);
            table.addField(temp);
        }
        if(table.getCount()>0)
            dbWizard.addColumns(table);
        
        table.clear();

        //删除不存在的指标字段
        columns.removeAll(currentItem);
        String colName = "";
        for (int i = 0; i < columns.size(); i++) {
            colName = (String) columns.get(i);
            if(colName.contains("subject_".toLowerCase()) || colName.contains("subject_".toUpperCase())){
                temp = new Field(colName, "");
                table.addField(temp);
            }
            }
        if(table.getCount()>0)
            dbWizard.dropColumns(table);
    }catch(Exception e){
        e.printStackTrace();
    }finally{
        PubFunc.closeResource(rs);
    }
   }
   
   /***
    * 查询79号代码类一级代码
   * @Title:getObjects
   * @Description：
   * @author xiexd
   * @param list
    */
   public void getSubjects(ArrayList list)
   {
       //查询79号代码类一级数
       StringBuffer codeSql = new StringBuffer("select codeitemid,codeitemdesc from codeitem where codesetid='79' and parentid = codeitemid and invalid=1 order by a0000");
       ContentDAO dao = new ContentDAO(conn);
       RowSet rs = null;
        try {
            rs = dao.search(codeSql.toString());
            LazyDynaBean bean = new LazyDynaBean();
            while(rs.next())
            { 
                bean = new LazyDynaBean();
                bean.set("codeitemid", rs.getString("codeitemid"));
                bean.set("codeitemdesc", rs.getString("codeitemdesc"));
                list.add(bean);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            PubFunc.closeResource(rs);
        }
   }
   
   /****
    * 获取专业下科目列表
   * @Title:getSubjList
   * @Description：
   * @author xiexd
   * @param codeId 
   * @param flg 0:子节点  1：本级节点
   * @return
    */
   public ArrayList getSubjList(String codeId,String flg){
       ArrayList list = new ArrayList();
       StringBuffer sql = new StringBuffer("select codeitemid,codeitemdesc from codeitem where codesetid='79' ");
       if("0".equals(flg))
       {           
           sql.append(" and childid = codeitemid ");
       }else if("1".equals(flg)){
           sql.append(" and codeitemid = parentid ");
       }
       sql.append(" and invalid=1 and parentid=?");
       sql.append(" order by a0000");
       list.add(codeId);
       RowSet rs = null;
       ContentDAO dao = new ContentDAO(conn);
       try {
           rs = dao.search(sql.toString(), list);
           list.clear();
           while(rs.next())
           {
               list.add(rs.getString("codeitemid")+"`"+rs.getString("codeitemdesc"));
           }
           
        } catch (Exception e) {
            list.clear();
            e.printStackTrace();
        }finally{
            PubFunc.closeResource(rs);
        }
       
       return list;
   }
   
   /****
    * 获取招聘批次对应的信息
   * @Title:getBatchInfo
   * @Description：
   * @author xiexd
   * @param batchId
   * @return
 * @throws GeneralException 
    */
   public LazyDynaBean getBatchInfo(String batchId) throws GeneralException {
       LazyDynaBean bean = new LazyDynaBean();
        RowSet rs = null;
        RowSet rs2 = null;
        RowSet rs3 = null;
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            
            StringBuffer sql = new StringBuffer();
            sql.append("select z.z0107,z.z0109,z.z0151,z.z0153,o.codeitemdesc");
            sql.append(" from z01 z,organization o");
            sql.append(" where z.z0105=o.codeitemid");
            sql.append(" and z.z0101=?");
            
            ArrayList params = new ArrayList();
            params.add(batchId);
            RecruitUtilsBo bo = new RecruitUtilsBo(conn);
    	
            
            rs = dao.search(sql.toString(), params);
            sql.setLength(0);
            params.clear();
            sql.append("select * from zp_flow_definition where flow_id=?");
            while(rs.next()){
                if(rs.getString("z0151")!=null)
                {               
                	ArrayList privChannel = getChannelJson();
                	boolean flag = true;
            		for(int j = 0; j< privChannel.size();j++){
            			if(rs.getString("z0151").equals(privChannel.get(j))) {
            				flag = false;
            				break;
            			}
            		}
                	
            		if(!flag){
            			 bean.set("z0151",rs.getString("z0151"));//招聘渠道
            		}else {
               		 	bean.set("z0151","");
               	    }
                }else{
                	 bean.set("z0151","");
                }
                
                if(rs.getString("z0153")!=null)
                {
                    params.add(rs.getString("z0153"));
                    rs2 = dao.search(sql.toString(), params);
                    if(rs2.next()) {
                    	if("1".equalsIgnoreCase(rs2.getString("valid"))) {
                    		ArrayList list = new ArrayList();
                    		String z0153Id =rs.getString("z0153");
                    		StringBuffer sqlz0153 = new StringBuffer("select flow_id as itemid ,name as itemdesc from zp_flow_definition  WHERE ");
            				RecruitPrivBo privBo = new RecruitPrivBo();
            				String privB0110 = privBo.getPrivB0110Whr(userview, "B0110", RecruitPrivBo.LEVEL_GLOBAL_PARENT_SELF_CHILD);
            				if(StringUtils.isNotEmpty(z0153Id)){
            					sqlz0153.append( privB0110);
            					sqlz0153.append(" and flow_id=?");
            					list.add(z0153Id);
            					rs3 = dao.search(sqlz0153.toString(),list);
            					if(rs3!=null&&rs3.next()){
            						bean.set("z0153",rs.getString("z0153"));//招聘流程 
            					}else {
            						bean.set("z0153","");//招聘流程 
            					}
            				}
                    	}else {
                    		bean.set("z0153","");
                    	}
                    }else
                        bean.set("z0153","");
                }else{
                    bean.set("z0153","");
                }
                
                if (rs.getDate("z0107") != null){//招聘开始时间
                	String dateFormat = bo.getDateFormat("z0329");
                    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
                    Date time =rs.getTimestamp("z0107");
                    bean.set("z0107",sdf.format(time));
                }   
                else {
                    bean.set("z0107","");
               }
                
                if (rs.getDate("z0109") != null){//招聘结束时间
                	String dateFormat = bo.getDateFormat("z0331");
                    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
                    Date time =rs.getTimestamp("z0109");
                    bean.set("z0109",sdf.format(time));
                }else {
                    bean.set("z0109","");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return bean;
    }
   /***
    * 获取职位下是否有安排考试
   * @Title:getPositionExaminee
   * @Description：
   * @author xiexd
   * @param z0301
   * @return
    */
   public String getPositionExaminee(String z0301)
   {
       String positionName = "";
       RowSet rs = null;
       try {
           ContentDAO dao = new ContentDAO(conn);
           StringBuffer sql = new StringBuffer("select z03.z0351 from zp_exam_assign zpe left join z03 on z03.z0301 = zpe.z0301 where z03.z0301=?");
           ArrayList value = new ArrayList();
           value.add(z0301);
           rs = dao.search(sql.toString(), value);
           
           if(rs.next())
           {
               positionName = rs.getString("z0351");
           }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            PubFunc.closeDbObj(rs);
        }
        return positionName;
   }
   
   /***
    * 获取职位下是否有候选人
   * @Title:getPositionCandidate
   * @Description：
   * @author wangjl
   * @param z0301
   * @return
    */
   public String getPositionCandidate(String z0301)
   {
	   String positionName = "";
       RowSet rs = null;
       try {
           ContentDAO dao = new ContentDAO(conn);
           StringBuffer sql = new StringBuffer("select z03.z0351 from zp_pos_tache zp left join Z03 on zp.ZP_POS_ID = z03.Z0301 where z03.z0301=?");
           ArrayList value = new ArrayList();
           value.add(z0301);
           rs = dao.search(sql.toString(), value);
           
           if(rs.next())
        	   positionName = rs.getString("z0351");
           
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            PubFunc.closeDbObj(rs);
        }
       return positionName;
   }
   
   
   /**
    * 获取下载文件列头
 * @return 
    */
   public String getDownloadFile() throws GeneralException {
        String columnids = "";
        ArrayList positionColumn;
        ArrayList columnlist = new ArrayList();
        positionColumn = this.getColumn();
        for (int i = 0; i < positionColumn.size(); i++) {
            columnids = columnids + "," + positionColumn.get(i);           
        }
        if (columnids.indexOf(",z0351") < 0) {
            columnids =",z0351"+columnids;
        }
        columnids = columnids.replaceAll(",z0321", "");
        columnids = columnids.replaceAll(",z0325", "");
     //   columnids =columnids.substring(1);
        String[] strs =(columnids).split(",");
        for(int y=0;y<strs.length;y++){         
            FieldItem item= (FieldItem)DataDictionary.getFieldItem(strs[y]);
            columnlist.add(item);
        }
        //creatSheet(selectitems,seconditems,fieldsetid,hasName,list,wb);
        return columnids;
    }
   
   /**
    * 创建下载文件
    */
   public void creatExcel() throws GeneralException {
       HSSFWorkbook wb = new HSSFWorkbook(); // 创建新的Excel 工作簿
       ArrayList columnlist = new ArrayList();
    //   columnlist = this.getDownloadFile();
       creatSheet(columnlist,wb,"z03");
       String outName = "hire_"+this.userview.getUserName() + ".xls";

    }
   
   /**
    * 创建下载文件
    */
   public void creatSheet( ArrayList list, HSSFWorkbook wb,String fieldsetid) throws GeneralException {
      /* String fieldsetdesc="";
        ContentDAO dao1 = new ContentDAO(conn);
        String sql = "select fieldsetdesc from fieldSet where fieldsetid='"+fieldsetid+"'";
        RowSet rs;
        try {
            rs = dao1.search(sql);
            if(rs.next()){
                fieldsetdesc=rs.getString("fieldsetdesc");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String SheetName = fieldsetdesc+"("+fieldsetid+")"; 
        // 【12937】青牛北京软件有限公司 记录录入---批量下载--无法下载通讯费子集模板
        // 将 / 替换成_ sunm add 2015-9-18
        SheetName = SheetName.replaceAll("/", "_").replaceAll("／", "_");
        HSSFSheet sheet = wb.createSheet(SheetName);
        // sheet.setProtect(true);
        HSSFFont font2 = wb.createFont();
        font2.setFontHeightInPoints((short) 10);
        HSSFCellStyle style2 = wb.createCellStyle();
        style2.setFont(font2);
        style2.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        style2.setWrapText(true);
        style2.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style2.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style2.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style2.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style2.setBottomBorderColor((short) 8);
        style2.setLeftBorderColor((short) 8);
        style2.setRightBorderColor((short) 8);
        style2.setTopBorderColor((short) 8);
        style2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style2.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);

        HSSFCellStyle style1 = wb.createCellStyle();
        style1.setFont(font2);
        style1.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style1.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        style1.setWrapText(true);
        style1.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style1.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style1.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style1.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style1.setBottomBorderColor((short) 8);
        style1.setLeftBorderColor((short) 8);
        style1.setRightBorderColor((short) 8);
        style1.setTopBorderColor((short) 8);
        style1.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));// 文本格式

        HSSFCellStyle styleN = dataStyle(wb);
        styleN.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
        styleN.setWrapText(true);
        HSSFDataFormat df = wb.createDataFormat();
        styleN.setDataFormat(df.getFormat(decimalwidth(0)));

        HSSFCellStyle styleCol0 = dataStyle(wb);
        HSSFFont font0 = wb.createFont();
        font0.setFontHeightInPoints((short) 5);
        styleCol0.setFont(font0);
        styleCol0.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));// 文本格式
        styleCol0.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        styleCol0.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);

        HSSFCellStyle styleCol0_title = dataStyle(wb);
        styleCol0_title.setFont(font2);
        styleCol0_title.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));// 文本格式
        styleCol0_title.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        styleCol0_title.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);

        HSSFCellStyle styleF1 = dataStyle(wb);
        styleF1.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
        styleF1.setWrapText(true);
        HSSFDataFormat df1 = wb.createDataFormat();
        styleF1.setDataFormat(df1.getFormat(decimalwidth(1)));

        HSSFCellStyle styleF2 = dataStyle(wb);
        styleF2.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
        styleF2.setWrapText(true);
        HSSFDataFormat df2 = wb.createDataFormat();
        styleF2.setDataFormat(df2.getFormat(decimalwidth(2)));

        HSSFCellStyle styleF3 = dataStyle(wb);
        styleF3.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
        styleF3.setWrapText(true);
        HSSFDataFormat df3 = wb.createDataFormat();
        styleF3.setDataFormat(df3.getFormat(decimalwidth(3)));

        HSSFCellStyle styleF4 = dataStyle(wb);
        styleF4.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
        styleF4.setWrapText(true);
        HSSFDataFormat df4 = wb.createDataFormat();
        styleF4.setDataFormat(df4.getFormat(decimalwidth(4)));

        HSSFCellStyle styleF5 = dataStyle(wb);
        styleF5.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
        styleF5.setWrapText(true);
        HSSFDataFormat df5 = wb.createDataFormat();
        styleF5.setDataFormat(df5.getFormat(decimalwidth(5)));

        //sheet.setColumnWidth((short) 0, (short) 1000);// 标识列不隐藏了，因为客户复制整行数据时候不能复制第一列的内容
        HSSFPatriarch patr = sheet.createDrawingPatriarch();
        
        HSSFRow row =sheet.getRow(0);
        if(row==null){
            row=sheet.createRow(0);
        }
        HSSFCell cell = null;
        HSSFComment comm = null;

        

        ArrayList codeCols = new ArrayList();
        for (int i = 0; i < list.size(); i++)
        {
            FieldItem field = (FieldItem) list.get(i);
            String fieldName = field.getItemid().toLowerCase();
            String fieldLabel = field.getItemdesc();
            //if (fieldName.equalsIgnoreCase("b0110") || fieldName.equalsIgnoreCase("e0122")||fieldName.equalsIgnoreCase("e01a1"))
            int w=field.getDisplaywidth();
            if(w==0){
                w=8;
            }
            if(w>186)
                w=186;
            sheet.setColumnWidth((i), w*350);
            cell=row.getCell(i);
            if(cell==null)
                cell=row.createCell(i);

            cell.setCellValue(cellStr(fieldLabel));
            cell.setCellStyle(style2);
            comm = patr.createComment(new HSSFClientAnchor(0, 0, 0, 1, (short) (i + 1), 0, (short) (i + 2), 1));
            if(hasName){
                if(i==1){
                    comm.setString(new HSSFRichTextString(fieldName+"`"+fieldsetid));
                }else if(seconditems.indexOf(fieldName)!=-1){
                    comm.setString(new HSSFRichTextString(fieldName+"`foreignkey"));
                }else{
                    comm.setString(new HSSFRichTextString(fieldName));
                }
            }else{              
                if(i==0){
                    comm.setString(new HSSFRichTextString(fieldName+"`"+fieldsetid));
                }else if(seconditems.indexOf(fieldName)!=-1){
                    comm.setString(new HSSFRichTextString(fieldName+"`foreignkey"));
                }else{
                    comm.setString(new HSSFRichTextString(fieldName));
                }
            }
            cell.setCellComment(comm);
            if (field.getItemtype().equalsIgnoreCase("A")&&(field.getCodesetid()!=null&&!field.getCodesetid().equals("")&&!field.getCodesetid().equals("0")))
            codeCols.add(field.getCodesetid() + ":" + new Integer(i).toString());
        }
        
        
        
        try
        {
            ContentDAO dao = new ContentDAO(this.frameconn);
            int rowCount = 1;
            while(rowCount<1001)
            {
                row =sheet.getRow(rowCount);
                if(row==null){
                    row=sheet.createRow(rowCount);
                }
                for (int i = 0; i < list.size(); i++)
                {
                    FieldItem field = (FieldItem) list.get(i);
                    String itemtype = field.getItemtype();
                    int decwidth = field.getDecimalwidth();

                    cell = row.getCell(i);
                    if(cell==null)
                        cell=row.createCell(i);
                    if (itemtype.equals("N"))
                    {
                        if (decwidth == 0)
                            cell.setCellStyle(styleN);
                        else if (decwidth == 1)
                            cell.setCellStyle(styleF1);
                        else if (decwidth == 2)
                            cell.setCellStyle(styleF2);
                        else if (decwidth == 3)
                            cell.setCellStyle(styleF3);
                        else if (decwidth == 4)
                            cell.setCellStyle(styleF4);
                        // else if(decwidth==5)
                        // cell.setCellStyle(styleF5);
                        cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                        cell.setCellValue("");
                    } else
                    {
                        cell.setCellStyle(style1);
                        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    }

                }
                rowCount++;
            }
            rowCount--;
            
         guodd 2015-04-14   office 高版本 下拉数据不能从不同的sheet中获取了
         *  String codesetSheetName = "hjehr_codeset_"+index/255; 
            if(codeCols.size()>0){
                if(index%255==0){//当sheet列数满255时，重新生成一个sheet
                    codesetSheet = wb.createSheet(codesetSheetName);
                }
                wb.setSheetHidden(wb.getSheetIndex(codesetSheet), true);
                codesetSheet.setColumnWidth((index),0);
            }
            String[] lettersUpper =
            { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };

           codesetSheet = sheet;
           // 下拉数据放到最后，excel 最后一列是IV, 为方便计算，这里从HZ 开始到着输入，依次为 HZ、HY、HX......   guodd 2015-04-14
           String[] firstUpper = {"H","G","F","E","D","C","B","A"};
            String[] lettersUpper =
            { "Z", "Y", "X", "W", "V", "U", "T", "S", "R", "Q", "P", "O", "N", "M", "L", "K", "J", "I", "H", "G", "F", "E", "D", "C", "B", "A" };
            
            for (int n = 0; n < codeCols.size(); n++)
            {
                //int m = 0;
               int m = 2001;//初始行为2001行
                String columnIndex = firstUpper[index/26] + lettersUpper[index%26];//计算列的 列标识 guodd 2015-04-14
                int cellIndex = columnToIndex(columnIndex);  // 通过列标识 计算出列 index guodd 2015-04-14
                
                String codeCol = (String) codeCols.get(n);
                String[] temp = codeCol.split(":");
                String codesetid = temp[0];
                int codeCol1 = Integer.valueOf(temp[1]).intValue();
                StringBuffer codeBuf = new StringBuffer();
                if (!codesetid.equals("UM") && !codesetid.equals("UN") && !codesetid.equalsIgnoreCase("@K"))
                {
                    codeBuf.append("select count(*) from codeitem where codesetid='" + codesetid + "'");// and codeitemid=childid and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date");
                    this.frowset = dao.search(codeBuf.toString());
                    if(this.frowset.next()){
                        if(this.frowset.getInt(1)<500){//20160713 linbz 代码型中指标大于500的时候，就不再加载了 
                            codeBuf.setLength(0);
                            codeBuf.append("select codesetid,codeitemid,codeitemdesc from codeitem where codesetid='" + codesetid + "' order by codeitemid");// zhaoguodong 2013.09.23 使获取的字段按codeitemid排序
                            //codeBuf.append("select codesetid,codeitemid,codeitemdesc from codeitem where codesetid='" + codesetid + "' order by a0000,codeitemid");// and codeitemid=childid and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date");
                        }else{
                            continue;
                        }
                    }
                } else
                {
                    if (!codesetid.equals("UN")){
                        m=loadorg(codesetSheet,row,cell,cellIndex,m,dao,codesetid);
                    }else if (codesetid.equals("UN"))
                    {
                        codeBuf.setLength(0);
                        if(this.userView.isSuper_admin()){
                            codeBuf.append("select codesetid,codeitemid,codeitemdesc,grade from organization where codesetid='" + codesetid
                                    + "' and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid");
                        }else{
                            String manpriv=this.userView.getManagePrivCode();
                            String manprivv=this.userView.getManagePrivCodeValue();
                            if(manprivv.length()>0)
                                codeBuf.append("select codesetid,codeitemid,codeitemdesc,grade from organization where codesetid='" + codesetid
                                        + "' and codeitemid like '"+manprivv+"%' and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid");
                            else if(manpriv.length()>=2)
                                codeBuf.append("select codesetid,codeitemid,codeitemdesc,grade from organization where codesetid='" + codesetid
                                        + "' and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid");
                            else
                                codeBuf.append("select codesetid,codeitemid,codeitemdesc,grade from organization where 1=2");
                        }
                    }
                }
                if (!codesetid.equals("UM") && !codesetid.equalsIgnoreCase("@K"))
                {
                    this.frowset = dao.search(codeBuf.toString());
                    while (this.frowset.next())
                    {
                        row = codesetSheet.getRow(m + 0); 
                        if (row == null)
                            row = codesetSheet.createRow(m + 0);
                        // cell = row.createCell((208 + index));
                        //cell = row.createCell((index));
                        cell = row.createCell((cellIndex));
                        if(codesetid.equals("UN")){
                            int grade=this.frowset.getInt("grade");
                            StringBuffer sb=new StringBuffer();
                            sb.setLength(0);
                            for(int i=1;i<grade;i++){
                                sb.append("  ");
                            }
                            cell.setCellValue(new HSSFRichTextString(sb.toString()+this.frowset.getString("codeitemdesc")+"("+this.frowset.getString("codeitemid")+")"));
                        }else{
                            cell.setCellValue(new HSSFRichTextString(this.frowset.getString("codeitemdesc")));
                        }
                        m++;
                    }
                }
                if(m==2001){
                    continue;
                }
                // 放到单独页签
                // sheet.setColumnWidth((208+index),0);
                String strFormula ="";
                if(index<=25){
                    strFormula = codesetSheetName + "!$" + lettersUpper[index] + "$1:$" + lettersUpper[index] + "$" + Integer.toString(m); // 表示BA列1到m行作为下拉列表来源数据
                }else if(index>25){
                    strFormula = codesetSheetName + "!$" + lettersUpper[index/26-1] + lettersUpper[index%26] 
                               + "$1:$" + lettersUpper[index/26-1] + lettersUpper[index%26] + "$" + Integer.toString(m); // 表示BA列1到m行作为下拉列表来源数据
                }
                //因为是倒着输入的，所以重新计算公式  guodd 2015-04-14
                strFormula = "$" + firstUpper[index/26] + lettersUpper[index%26] 
                               + "$2001:$" + firstUpper[index/26] + lettersUpper[index%26] + "$" + Integer.toString(m); // 表示BA列1到m行作为下拉列表来源数据
                CellRangeAddressList addressList = new CellRangeAddressList(1, rowCount, codeCol1, codeCol1);//rowCount
                DVConstraint dvConstraint = DVConstraint.createFormulaListConstraint(strFormula);
                HSSFDataValidation dataValidation = new HSSFDataValidation(addressList, dvConstraint);
                dataValidation.setSuppressDropDownArrow(false);
                sheet.addValidationData(dataValidation);
                index++;
            }

        } catch (SQLException e1)
        {
            e1.printStackTrace();
        }  */

    }
   
   /***
    * 获取已结束招聘批次
   * @Title:getBatch
   * @Description：
   * @author xiexd
   * @return
   * @throws GeneralException
    */
   public ArrayList getEndBatch() throws GeneralException {
       ArrayList list = new ArrayList();
       RecruitBatchBo rbbo = new RecruitBatchBo(conn, userview); 
       ArrayList rbList = rbbo.getAllBatchInfos("4");
       CommonData commonData = new CommonData();
       for(int i=0;i<rbList.size();i++)
       {
           commonData = (CommonData) rbList.get(i);
           list.add(commonData.getDataValue()+"`"+commonData.getDataName());
       }
       return list;
   }
   
   /**
    * 刷新权限范围内职位的新简历数，候选人数，简历数量，已录用人数
 * @param dao
 * @param userview
 * @throws SQLException
 * @throws GeneralException
 */
   public synchronized static void countA0100(ContentDAO dao, UserView userview) throws SQLException, GeneralException{
        StringBuffer sql = new StringBuffer();
        //权限下的所有职位
        //从权限中获取职位权限
        RecruitPrivBo bo = new RecruitPrivBo();
        String privStr = bo.getPositionWhr(userview);
        String privStrTemp = privStr.replaceAll("z\\.", "");
        
        //新简历数
        FieldItem fieldItem = DataDictionary.getFieldItem("Z0369", "Z03");
        if(fieldItem!=null&&"1".equals(fieldItem.getUseflag())){
            sql.append("update Z03 set Z0369=(select num from (select ZP_POS_ID,COUNT(a0100) num from zp_pos_tache,z03 z ");
            sql.append(" where ZP_POS_ID = z.Z0301 and "+privStr+" and ");
            sql.append(Sql_switcher.isnull("status", "'0'")+"='0'  group by ZP_POS_ID)zpt where Z0301=zpt.ZP_POS_ID and "+privStrTemp+") ");
            sql.append(" where Z0301= (select ZP_POS_ID from (select ZP_POS_ID from zp_pos_tache,z03 z");
            sql.append(" where ZP_POS_ID = z.Z0301 and "+privStr+" and ");
            sql.append(Sql_switcher.isnull("status", "'0'")+"='0' group by ZP_POS_ID)zpt where z0301=zpt.ZP_POS_ID and "+privStrTemp+") ");
            sql.append(" and "+privStrTemp);
            dao.update(sql.toString());  
            sql.setLength(0);
            sql.append("update Z03 set Z0369=0 where Z0301 not in(" );
            sql.append(" select ZP_POS_ID from zp_pos_tache,z03 z");
            sql.append(" where ZP_POS_ID = z.Z0301 and "+privStr+" and ");
            sql.append(Sql_switcher.isnull("status", "'0'")+"='0' group by ZP_POS_ID) ");
            sql.append(" and "+privStrTemp);
            dao.update(sql.toString());
        }
        //候选人数
        fieldItem = DataDictionary.getFieldItem("Z0371", "Z03");
        if(fieldItem!=null&&"1".equals(fieldItem.getUseflag())){
            sql.setLength(0);
            sql.append("update Z03 set z0371=(select num from (select ZP_POS_ID,COUNT(a0100) num from zp_pos_tache,z03 z ");
            sql.append(" where ZP_POS_ID = z.Z0301 and "+privStr+" and ");
            sql.append(" status='1' and "+Sql_switcher.isnull("status", "'0'")+"='1' and resume_flag not in ('0105','0106','0205','0206','0306','0307','0308','0406','0407','0408','0506','0507','0508','0603','0604','0703','0704','0805','0806','1003','1004','1005') ");
            sql.append(" and "+Sql_switcher.substr("resume_flag","1","2")+" in ('01','02','03','04','05','06','07','08','10') group by ZP_POS_ID)zpt where Z0301=zpt.ZP_POS_ID and "+privStrTemp+")  ");
            sql.append(" where Z0301= (select ZP_POS_ID from (select ZP_POS_ID from zp_pos_tache,z03 z ");
            sql.append(" where ZP_POS_ID = z.Z0301 and "+privStr+" and ");
            sql.append(" status='1' and "+Sql_switcher.isnull("status", "'0'")+"='1' and resume_flag not in ('0105','0106','0205','0206','0306','0307','0308','0406','0407','0408','0506','0507','0508','0603','0604','0703','0704','0805','0806','1003','1004','1005') ");
            sql.append(" and "+Sql_switcher.substr("resume_flag","1","2")+" in ('01','02','03','04','05','06','07','08','10') group by ZP_POS_ID)zpt where z0301=zpt.ZP_POS_ID and "+privStrTemp+")  ");
            sql.append(" and "+privStrTemp);
            dao.update(sql.toString());
            sql.setLength(0);
            sql.append("update Z03 set Z0371=0  where Z0301 not in(");
            sql.append("select ZP_POS_ID from zp_pos_tache,z03 z  where ZP_POS_ID = z.Z0301 and "+privStr+" and  status='1' and "+Sql_switcher.isnull("status", "'0'")+"='1' ");
            sql.append(" and resume_flag not in ('0105','0106','0205','0206','0306','0307','0308','0406','0407','0408','0506','0507','0508','0603','0604','0703','0704','0805','0806','1003','1004','1005')  ");
            sql.append(" and "+Sql_switcher.substr("resume_flag","1","2")+" in ('01','02','03','04','05','06','07','08','10') group by ZP_POS_ID)");
            sql.append(" and "+privStrTemp);
            dao.update(sql.toString());
        }
        //简历数量
        fieldItem = DataDictionary.getFieldItem("Z0323", "Z03");
        if(fieldItem!=null&&"1".equals(fieldItem.getUseflag())){
            sql.setLength(0);
            sql.append("update Z03 set z0323=(");
            sql.append("select num from (select z0301,count(a0100) num from zp_pos_tache right join z03 z  on ZP_POS_ID = z.Z0301 and 1=1 group by z0301)zpt");
            sql.append(" where z03.Z0301=zpt.Z0301)");
            sql.append(" where "+privStrTemp);
            dao.update(sql.toString()); 
        }
        //已录用人数
        fieldItem = DataDictionary.getFieldItem("Z0367", "Z03");
        if(fieldItem!=null&&"1".equals(fieldItem.getUseflag())){
            sql.setLength(0);
            sql.append("update Z03 set z0367=(select num from (select ZP_POS_ID,count(a0100) num from zp_pos_tache,z03 z ");
            sql.append(" where ZP_POS_ID = z.Z0301 and "+privStr+" and ");
            sql.append(" status='1' and "+Sql_switcher.substr("resume_flag","1","2")+" in ('10') and resume_flag not in ('0703','0805','1004') group by ZP_POS_ID)zpt where Z0301=zpt.ZP_POS_ID and "+privStrTemp+") ");
            sql.append(" where Z0301= (select ZP_POS_ID from (select ZP_POS_ID from zp_pos_tache,z03 z ");
            sql.append(" where ZP_POS_ID = z.Z0301 and "+privStr+" and ");
            sql.append(" status='1' and "+Sql_switcher.substr("resume_flag","1","2")+" in ('07','08','10') and resume_flag not in ('0703','0805','1004') group by ZP_POS_ID)zpt where z0301=zpt.ZP_POS_ID and "+privStrTemp+") ");
            sql.append(" and "+privStrTemp);
            dao.update(sql.toString()); 
        }
    }
   
   /**
    * 获取代码层级
     * @param codesetid
     * @return
     */
    public String getCodeSetLayer(String codeSetid) {
       RowSet rs = null;
       String layer = "0";
       try {
           String sql = "select 1 from codeitem where codesetid = ?";
           String codesql = "select MAX(layer) as layer from codeitem where codesetid = ?";
           ArrayList codeSetId = new ArrayList();
           codeSetId.add(codeSetid);
           rs = dao.search(sql,codeSetId);
           if(rs.next()) {
               sql = "select 1 from codeitem where layer is null and codesetid = ?";
               rs = dao.search(sql, codeSetId);
               if(rs.next()) //如果代码类里有layer为null的，重置代码层级
                   this.updateLayer(codeSetid);
               
               rs = dao.search(codesql, codeSetId);
               if(rs.next()){
                   String temp = rs.getString("layer");
                   if(temp != null)
                       layer = temp;
                   
               }
           }
       } catch (Exception e) {
           e.printStackTrace();
       } finally {
           PubFunc.closeResource(rs);
       }
       return layer;
   }
   
   /**
    * 重置代码层级
     * @param codesetid
     */
    public void updateLayer(String codeSetid) {
           try{
               String sql = " update codeitem set layer=null where codesetid='"+codeSetid+"'";
                dao.update(sql);
                sql = " update codeitem set layer = 1 where codesetid='"+codeSetid+"' and parentid=codeitemid";
                dao.update(sql);
                sql = " update codeitem set layer=(select layer from codeitem c1 where c1.codesetid='"+codeSetid+"' and c1.codeitemid=codeitem.parentid)+1 where codesetid='"+codeSetid+"' and layer is null ";
                int i=1;
                while(i>0){
                    i = dao.update(sql);
                }
           }catch(Exception e){
               e.printStackTrace();
           }
      }
    
    /**获取登录用户权限范围内的招聘渠道
     * @return
     */
    public ArrayList getChannelJson() {
    	RowSet rs = null;
    	ArrayList<String> list = new ArrayList<String>();
    	try {
			RecruitPrivBo privBo = new RecruitPrivBo();
			HashMap<String, Object> parame = privBo.getChannelPrivMap(userview, conn);
			boolean setFlag = (Boolean) parame.get("setFlag");
			ArrayList<String> value = new ArrayList<String>();
			StringBuffer str = new StringBuffer();
			str.append("select codeitemid from codeitem ");
			str.append(" where codesetid='35' ");
			if(setFlag) {
				ArrayList<String> hirePriv = (ArrayList<String>) parame.get("hirePriv");
				if(hirePriv.size()>0) {
					str.append(" and (");
					for (String hire : hirePriv) {
						str.append(" codeitemid like ");
						str.append("'"+hire+"%' or");
					}
					str.setLength(str.length()-2);
					str.append(" )");
					}
				else
					str.append(" and 1=2 ");
			}
			str.append(" and invalid=1 ");
			str.append(" order by a0000");
 
			rs = dao.search(str.toString());
			while(rs.next()) {
				list.add(rs.getString("codeitemid"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}
		return list;
    }
    
    /**
     * 校验招聘渠道权限
     * @param codeIds
     * @return
     * @throws GeneralException
     */
    public boolean checkPrivChannel(String codeIds) throws GeneralException {
    	ArrayList privChannel = getChannelJson();
    	String[] split = codeIds.split(",");
    	for (String codeId : split) {
    		boolean flag = true;
    		for(int j = 0; j< privChannel.size();j++){
    			if(codeId.equals(privChannel.get(j))) {
    				flag = false;
    				break;
    			}
    		}
    		if(flag){
    			CodeItem code = AdminCode.getCode("35", codeId);
    			throw GeneralExceptionHandler.Handle(new Exception("您没有“"+code.getCodename()+"”权限！"));
    		}
		}
		return true;
    }

    //创建一个新轮招聘职位
    public void copyPosition(HashMap map) throws GeneralException {
    	String z0315 = (String) map.get("z0315");
		String z0329 = (String) map.get("z0329");
		String z0331 = (String) map.get("z0331");
		String posid = (String) map.get("posid");
    	RecordVo vo = new RecordVo("z03");
		vo.setString("z0301", posid);
		RowSet rs = null;
		try {
			vo=dao.findByPrimaryKey(vo);
			RecruitUtilsBo bo = new RecruitUtilsBo(conn);
			String z0329Format = bo.getDateFormat("Z0329");
			if("yyyy-MM-dd".equalsIgnoreCase(z0329Format))
				z0329Format = "yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat df = new SimpleDateFormat(z0329Format);
            Date z0329Time = df.parse(z0329);
            String z0331Format = bo.getDateFormat("Z0331");
            if("yyyy-MM-dd".equalsIgnoreCase(z0331Format))
            	z0331Format = "yyyy-MM-dd HH:mm:ss";
            df = new SimpleDateFormat(z0331Format);
            Date z0331Time = df.parse(z0331);
			IDGenerator idg = new IDGenerator(2,conn);
            String az0301 = idg.getId("Z03.Z0301");
			vo.setString("z0301", az0301);
			vo.setString("z0315", z0315);
			vo.setDate("z0329", z0329Time);
			vo.setDate("z0331", z0331Time);
			vo.setString("z0319", "01");
			vo.setString("z0367", "0");
			vo.setString("z0369", "0");
			vo.setString("z0371", "0");
			vo.setString("z0323", "0");
			dao.addValueObject(vo);
			
			StringBuffer sql = new StringBuffer("select member_id from zp_members ");
			sql.append(" where z0301 = ? order by member_id asc");
			ArrayList<String> list = new ArrayList<String>();
			list.add(posid);
			rs = dao.search(sql.toString(), list);
			while(rs.next()) {
				String member_id = rs.getString("member_id");
				vo = new RecordVo("zp_members");
				vo.setString("member_id", member_id);
				vo=dao.findByPrimaryKey(vo);
	            member_id = idg.getId("zp_members.member_id");
	            vo.setString("member_id", member_id);
	            vo.setString("z0301", az0301);
	            dao.addValueObject(vo);
			}
			
			sql = new StringBuffer("select id from zp_resume_filter ");
			sql.append(" where zp_pos_id = ? ");
			rs = dao.search(sql.toString(), list);
			if(rs.next()) {
				String id = rs.getString("id");
				vo = new RecordVo("zp_resume_filter");
				vo.setString("id", id);
				vo=dao.findByPrimaryKey(vo);
				String newId = idg.getId("zp_resume_filter.id");
	            vo.setString("id", newId);
	            vo.setString("zp_pos_id", az0301);
	            dao.addValueObject(vo);
	            
	            sql = new StringBuffer("insert into  zp_resume_filter_rule ");
				sql.append(" select ? Filter_id,fieldsetid,itemid,Itemdesc,itemtype,codesetid, ");
				sql.append(" datetype,queryvalue1,queryvalue2,Displayid from ");
				sql.append(" zp_resume_filter_rule where filter_id= ? ");
				list = new ArrayList<String>();
				list.add(newId);
				list.add(id);
				dao.update(sql.toString(), list);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}
    }
    
    
    
	/**
	 * 获取要重新开始招聘的职位信息
	 * @param posid
	 */
	public ArrayList<LazyDynaBean> getPositionInfo(String posid) {
		RowSet rs = null;
		ArrayList<LazyDynaBean> list = new ArrayList<LazyDynaBean>();
		try {
			String[] fields = {"Z0351","Z0329","Z0331","Z0315","Z0319"};
			ArrayList<String> value = new ArrayList<String>();
			value.add(posid);
			StringBuffer sql = new StringBuffer("select ");
			for (String field : fields) {
				sql.append(field+",");
			}
			sql.setLength(sql.length()-1);
			sql.append(" from z03 where z0301=?");
			rs = dao.search(sql.toString(), value);
			LazyDynaBean bean = new LazyDynaBean();
			if(rs.next()) {
				for (String field : fields) {
					bean = new LazyDynaBean();
					FieldItem item = DataDictionary.getFieldItem(field, "Z03");
					bean.set("itemid", field);
					bean.set("itemdesc", item.getItemdesc());
					bean.set("itemtype", item.getItemtype());
					bean.set("itemlength", item.getItemlength());
					bean.set("itemFillable", item.isFillable());
					if("D".equalsIgnoreCase(item.getItemtype()))
                    {
                        if(rs.getDate(field)!=null)
                        {
                        	RecruitUtilsBo bo = new RecruitUtilsBo(conn);
                    		String dateFormat = bo.getDateFormat(field);
                            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
                            Date time =rs.getTimestamp(field);
                            bean.set("value", sdf.format(time));
                        }else
                        	bean.set("value", "");
                        	
                    }else {
                    	String fieldValue = StringUtils.isNotEmpty(rs.getString(field))?rs.getString(field):"";
                    	bean.set("value", fieldValue);
                    }
                    	
					list.add(bean);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}
		return list;
	}
	
	/**
	 * 判断是否有已发布的职位
	 * 如果有进入招聘页面后直接定位到发布
	 * 没有则还按照现在定位到全部
	 * @return
	 */
	public boolean hasPublicPos() {
		RowSet rs = null;
		try {
			rs = dao.search("select 1 from z03 where z0319='04'");
			if(rs.next())
				return true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}
		return false;
	}
	
	
	/**
	 * 判断该指标是否在z03表中存在且已经构库
	 * 如果存在且构库返回true，没有则返回false
	 * @return
	 */
	public boolean getItemInZ03(String itemid) {
	    Boolean applyInZ03 =false;
	    if(StringUtils.isNotEmpty(itemid)){
	   	    FieldItem item = DataDictionary.getFieldItem(itemid , "Z03");
	    	
	        if(null != item && "1".equalsIgnoreCase(item.getUseflag()))
	        	applyInZ03 = true;
	    }
		return applyInZ03;
	}
	
	  /**
     * 判断招聘渠道对应的35号代码类是否存在
     * @throws Exception 
     */
    public void getCodeItem() throws Exception {
        RowSet rs = null;
        try {
        	String bosdate=DateStyle.dateformat(new Date(),"yyyy-MM-dd");
			StringBuffer sql = new StringBuffer(); //邮件内容
			sql.append("select codeitemid,codeitemdesc from codeitem ");
			sql.append(" where codesetid='35' ");
			sql.append(" and ( ").append(Sql_switcher.isnull("invalid","1")).append(" = 1 OR invalid <> 0) ");
			sql.append(" and ").append(Sql_switcher.dateValue(bosdate)).append(" between start_date and end_date  ");
			sql.append(" order by codeitemid");
			rs= dao.search(sql.toString());
	        if(!rs.next())
	        {
	        	throw GeneralExceptionHandler.Handle(new Exception("招聘渠道关联的35号代码没有可用数据,请前往设置"));						
	        }
        } catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}
    }
}
