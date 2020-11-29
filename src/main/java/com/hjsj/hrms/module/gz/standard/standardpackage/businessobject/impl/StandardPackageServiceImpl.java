/**
 * FileName: StandardPackageServiceImpl Author: xuchangshun Date: 2019/11/6
 * 13:36 Description: 薪资标准历史沿革实现类 History: <author> <time> <version> <desc> 作者姓名
 * 修改时间 版本号 描述
 */
package com.hjsj.hrms.module.gz.standard.standardpackage.businessobject.impl;

import com.hjsj.hrms.module.gz.standard.standardpackage.businessobject.IStandardPackageService;
import com.hjsj.hrms.module.gz.standard.standardpackage.dao.IStandardPackageDao;
import com.hjsj.hrms.module.gz.standard.standardpackage.dao.impl.StandardPackageDaoImpl;
import com.hjsj.hrms.module.gz.standard.utils.DownLoadXml;
import com.hjsj.hrms.module.gz.standard.utils.StandardUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.AbstractException;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 〈类功能描述〉<br>
 * 〈薪资标准历史沿革实现类〉
 *
 * @Author xuchangshun
 * @Date 2019/11/6
 * @since 1.0.0
 */
public class StandardPackageServiceImpl implements IStandardPackageService {
    /**数据库链接conn**/
    private Connection conn;

    /**当前登录用户相关信息**/
    private UserView userView;

    /**数据层操作类**/
    private IStandardPackageDao standardPackageDao;


    /**
     * 薪资标准历史沿革实现类构造方案
     * @Author xuchangshun
     * @param conn : 数据库链接
     * @param userView : 当前登录用户
     * @Date 2019/11/6 13:44
     */
    public StandardPackageServiceImpl(Connection conn, UserView userView) {
        this.conn = conn;
        this.userView = userView;
        standardPackageDao = new StandardPackageDaoImpl(conn);
    }

    /**
     * 获取历史沿革表格列表的tableconfig
     * @Author xuchangshun
     * @return 表格组件的tableconfig
     * @throws GeneralException 错误异常信息
     * @Date 2019/11/22 11:51
     */
    @Override
    public String getStandardPackageTableConfig() throws GeneralException {
        ArrayList<ColumnsInfo> columnsInfoList = getColumnsList();
        //查询table所需要的sql
        String dataSql = "select pkg_id,name,start_date,end_date,status,b0110 from gz_stand_pkg";
        //创建表格对象
        TableConfigBuilder builder = new TableConfigBuilder(SUBMODULEID, columnsInfoList, SUBMODULEID, this.userView, this.conn);
        //设置标题
        builder.setTitle(ResourceFactory.getProperty("standard.standardPackage.title"));
        //是否有复选框列
        builder.setSelectable(true);
        builder.setDataSql(dataSql);
        builder.setColumnFilter(true);
        //是否可编辑
        builder.setEditable(true);
        //排序指标
        builder.setOrderBy(" order by status desc,start_date desc");
        builder.setTableTools(getButtonList());
        return builder.createExtTableConfig();
    }

    /**
     * 获取历史沿革表格列表的六列表头
     * @Author qinxx
     * @return ArrayList表头信息
     * @throws GeneralException 错误异常信息
     * @Date 2019/12/04 17:21
     */
    private ArrayList<ColumnsInfo> getColumnsList() throws GeneralException {
        ArrayList<ColumnsInfo> columnsInfoList = new ArrayList<ColumnsInfo>();
        ColumnsInfo columnsInfo;
        Map<String, Object> extraParam = new HashMap<String, Object>();

        extraParam.put("encrypted", true);
        columnsInfo = getColumn("A", "0", "pkg_id", "", 0, ColumnsInfo.LOADTYPE_ONLYLOAD, extraParam);
        columnsInfoList.add(columnsInfo);
        extraParam.clear();

        //标准历史沿革
        if (!this.userView.isSuper_admin() && !this.userView.hasTheFunction(RENAME_FUNCID)) {
            extraParam.put("editableValidFunc", "false");
        }
        extraParam.put("columnLength", 30);
        extraParam.put("rendererFunc", "standardPackagePage.renderNameColumn");
        extraParam.put("validFunc", "standardPackagePage.nameValidate");
        columnsInfo = getColumn("A", "0", "name", ResourceFactory.getProperty("standard.standardPackage.name"), 300, ColumnsInfo.LOADTYPE_ALWAYSLOAD, extraParam);
        columnsInfoList.add(columnsInfo);
        extraParam.clear();

        //启用日期
        extraParam.put("columnLength", 10);
        extraParam.put("editableValidFunc", "false");
        columnsInfo = getColumn("D", "0", "start_date", ResourceFactory.getProperty("standard.standardPackage.start_date"), 110, ColumnsInfo.LOADTYPE_ALWAYSLOAD, extraParam);
        columnsInfoList.add(columnsInfo);

        //失效日期
        columnsInfo = getColumn("D", "0", "end_date", ResourceFactory.getProperty("standard.standardPackage.end_date"), 110, ColumnsInfo.LOADTYPE_ALWAYSLOAD, extraParam);
        columnsInfoList.add(columnsInfo);
        extraParam.remove("columnLength");

        //启用
        extraParam.put("rendererFunc", "standardPackagePage.renderStatusColumn");
        extraParam.put("textAlign", "center");
        columnsInfo = getColumn("A", "0", "status", ResourceFactory.getProperty("standard.standardPackage.status"), 80, ColumnsInfo.LOADTYPE_ALWAYSLOAD, extraParam);
        columnsInfoList.add(columnsInfo);
        extraParam.clear();

        //所属组织
        if (!this.userView.isSuper_admin() && !this.userView.hasTheFunction(EDIT_FUNCID)) {
            extraParam.put("editableValidFunc", "false");
        }
        extraParam.put("ctrltype", "3");
        extraParam.put("nmodule", "1");
        extraParam.put("rendererFunc", "standardPackagePage.renderOrganizationFunc");
        columnsInfo = getColumn("A", "UM", "b0110", ResourceFactory.getProperty("standard.standardPackage.organization"), 300, ColumnsInfo.LOADTYPE_ALWAYSLOAD, extraParam);
        columnsInfoList.add(columnsInfo);

        //操作
        extraParam.put("sortable", false);
        extraParam.put("filterable", false);
        extraParam.put("editableValidFunc", "false");
        extraParam.put("rendererFunc", "standardPackagePage.renderOperateColumn");
        columnsInfo = getColumn("A", "0", "operate", ResourceFactory.getProperty("standard.standardPackage.operate"), 150, ColumnsInfo.LOADTYPE_ALWAYSLOAD, extraParam);
        columnsInfoList.add(columnsInfo);

        return columnsInfoList;
    }

    /**
     * 获取历史沿革表格列表表头
     * @Author qinxx
     * @return columnsInfo 每一列表头信息
     * @param columnType 列类型
     * @param codeSetId =0为字符型
     * @param columnId 列id
     * @param columnDesc 列名
     * @param columnWidth 列宽
     * @param loadType 数据加载与显示
     * @param extraParam 额外参数配置
     * @throws GeneralException 错误异常信息
     * @Date 2019/12/04 17:21
     */
    private ColumnsInfo getColumn(String columnType, String codeSetId, String columnId, String columnDesc, int columnWidth, int loadType,
            Map<String, Object> extraParam) throws GeneralException {
        ColumnsInfo columnsInfo = new ColumnsInfo();
        columnsInfo.setColumnType(columnType);
        columnsInfo.setCodesetId(codeSetId);
        columnsInfo.setColumnId(columnId);
        columnsInfo.setColumnDesc(columnDesc);
        columnsInfo.setColumnWidth(columnWidth);
        columnsInfo.setLoadtype(loadType);
        try {
            //额外参数赋值
            if (extraParam != null) {
                for (String key : extraParam.keySet()) {
                    Field field = columnsInfo.getClass().getDeclaredField(key);
                    if (field != null) {
                        field.setAccessible(true);
                        field.set(columnsInfo, extraParam.get(key));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeneralException("gz.standard.getColumnInfoError");
        }
        return columnsInfo;
    }

    /**
     * 获取历史沿革表格按钮
     * @Author qinxx
     * @return ArrayList 四个按钮
     * @throws GeneralException 错误异常信息
     * @Date 2019/12/05 09:21
     */
    private ArrayList getButtonList() {
        ArrayList buttonList = new ArrayList();

        //新增按钮
        if (this.userView.isSuper_admin() || this.userView.hasTheFunction(ADD_FUNCID)) {
            buttonList.add(newButton(ResourceFactory.getProperty("standard.standardPackage.addButton"), "standardPackagePage.addStandardPackage('')"));
        }

        //保存按钮
        buttonList.add(newButton(ResourceFactory.getProperty("standard.standardPackage.saveButton"),"standardPackagePage.saveStandardPackage"));

        //导出导入按钮
        if (this.userView.isSuper_admin() || this.userView.hasTheFunction(IMPORT_EXPORT_FUNCID)) {
            buttonList.add(newButton(ResourceFactory.getProperty("standard.standardPackage.exportButton"),"standardPackagePage.exportButton"));
            buttonList.add(newButton(ResourceFactory.getProperty("standard.standardPackage.importButton"),"standardPackagePage.importButton"));
        }
        return buttonList;
    }

    /**
     * 获取历史沿革表格按钮
     * @param text 按钮显示文字
     * @param handler 按钮触发方法
     * @Author qinxx
     * @return button 按钮信息
     * @throws GeneralException 错误异常信息
     * @Date 2019/12/05 09:40
     */
    private ButtonInfo newButton(String text, String handler) {
        ButtonInfo button = new ButtonInfo(text, handler);
        button.setGetData(true);
        return button;
    }

    /**
     * 获取历史沿革列表功能权限Map(页面细节控制用)
     * @author wangbs
     * @return Map 权限map
     * @throws GeneralException 错误异常信息
     * @date 2019/12/6 11:08
     */
    @Override
    public Map getFuncPrivMap() throws GeneralException {
        //所有历史沿革权限控制
        Map<String, Map> funcPrivMap = new HashMap<String, Map>();
        //获取权限 (操作范围>操作单位>管理范围)
        String orgPriv = StandardUtil.getPriv(this.userView);

        String sql = "select pkg_id,status,b0110 from gz_stand_pkg";
        List<RecordVo> pkgList = standardPackageDao.getPackageList(sql);
        for (RecordVo pkgInfo : pkgList) {
            String pkgId = PubFunc.encrypt(String.valueOf(pkgInfo.getInt("pkg_id")));
            String b0110 = pkgInfo.getString("b0110");
            String status = pkgInfo.getString("status");

            Map<String, Object> pkgPrivInfo = assemblePkgPrivInfo(orgPriv,b0110);
            pkgPrivInfo.put("status", status);
            funcPrivMap.put(pkgId, pkgPrivInfo);
        }

        //获取通用权限放入funcPrivMap
        Map<String, Object> pkgPrivInfo = assemblePkgPrivInfo(orgPriv, STOP);
        funcPrivMap.put("common", pkgPrivInfo);

        return funcPrivMap;
    }

    /**
     * 拼装pkgPrivInfo数据
     * @author wangbs
     * @param orgPriv 机构权限
     * @param b0110 所属组织
     * @return Map<String, Object>
     * @date 2019/12/12 17:30
     */
    private Map<String, Object> assemblePkgPrivInfo(String orgPriv, String b0110){
        //单个历史沿革信息
        Map<String, Object> pkgPrivInfo = new HashMap<String, Object>();

        //修改、删除、导入导出(下载)、重命名、启用权限
        pkgPrivInfo.put("editFlag", checkHasPriv(EDIT_FUNCID, orgPriv, b0110));
        pkgPrivInfo.put("deleteFlag", checkHasPriv(DELETE_FUNCID, orgPriv, b0110));
        pkgPrivInfo.put("importOrExportFlag", checkHasPriv(IMPORT_EXPORT_FUNCID, orgPriv, b0110));
        pkgPrivInfo.put("renameFlag", checkHasPriv(RENAME_FUNCID, orgPriv, b0110));
        pkgPrivInfo.put("enableFlag", checkHasPriv(ENABLE_FUNCID, orgPriv, b0110));

        return pkgPrivInfo;
    }
    /**
     * 校验有无该权限
     * @author wangbs
     * @param privFuncId 功能权限id
     * @param orgPriv 当前用户权限
     * @param b0110 所属组织
     * @return boolean
     * @date 2019/12/12 14:48
     */
    private boolean checkHasPriv(String privFuncId, String orgPriv, String b0110) {
        boolean privFlag = this.userView.isSuper_admin() || this.userView.hasTheFunction(privFuncId);
        if (StringUtils.equals(b0110, STOP)) {
            return privFlag;
        }
        //有功能按钮授权 且 机构权限非全权 且兼容老数据b0110没有值的情况
        if (privFlag && !StringUtils.equals(orgPriv, ALL) && StringUtils.isNotBlank(b0110)) {
            if (StringUtils.equals(orgPriv, NO) || !b0110.startsWith(orgPriv)) {
                privFlag = false;
            }
        }
        return privFlag;
    }

    /**
     * 获取历史沿革的相关信息(包含引用的历史沿革列表)
     * @Author xuchangshun
     * @param pkg_id :历史沿革id
     * @return Map 历史沿革的相关信息
     * @throws GeneralException 错误异常信息
     * @Date 2019/11/22 11:51
     */
    @Override
    public Map getStandPackageInfor(String pkg_id, String init_type) throws GeneralException {
        StringBuffer noImportBuffer = new StringBuffer();

        Map  standMap = new HashMap();
        List standList = new ArrayList();
        HashMap pkgMap = new HashMap();
        try {
          //未引用标准表的sql
            noImportBuffer.append("select id,name from gz_stand where id not in(");
            noImportBuffer.append("select distinct(id) from gz_stand_history where pkg_id = '");
            noImportBuffer.append(pkg_id).append("')");

            List importStandList = standardPackageDao.getStandTableList(pkg_id);
            for(int i = 0; i < importStandList.size(); i++) {
                List importList = new ArrayList();
                RecordVo recordVo = (RecordVo)importStandList.get(i);
                String standardId = recordVo.getString("id");
                String name = recordVo.getString("name");
                importList.add(standardId);
                importList.add(name);
                importList.add("1");
                standList.add(importList);
            }

            //修改历史沿革回显数据
            if(StringUtils.equals(init_type, "edit")) {

                RecordVo vo = standardPackageDao.getPackageInfor(pkg_id);
                String name = vo.getString("name");
                String status = vo.getString("status");
                String start_date = vo.getString("start_date").substring(0,10);
                //获取机构描述信息
                String organizationCode = vo.getString("b0110");
                String b0110 = AdminCode.getCodeName("UN", organizationCode);
                if(StringUtils.isBlank(b0110)) {
                   b0110 = AdminCode.getCodeName("UM", organizationCode);
                }
                pkgMap.put("name",name);
                pkgMap.put("start_date",start_date);
                pkgMap.put("status",status);
                pkgMap.put("b0110",organizationCode + "`" + b0110);
                List noImportList  =  standardPackageDao.getNoUseInPackageStandList(noImportBuffer.toString());
                standList.addAll(noImportList);

            } else {
                String orgPriv = StandardUtil.getPriv(this.userView);
                String b0110 = "";
                //排除没有任何权限情况
                if(!StringUtils.equals(orgPriv, "no")) {
                    //拥有所有权限
                    if(StringUtils.equals(orgPriv, "all")) {
                      //顶级机构sql
                        String superOrganizationSql = "select codeitemid from organization where codeitemid = parentid order by codeitemid asc ";
                        orgPriv = standardPackageDao.getSuperOrganization(superOrganizationSql);
                    }
                    //获取机构描述信息
                    b0110 = AdminCode.getCodeName("UN", orgPriv);
                    if(StringUtils.isBlank(b0110)) {
                       b0110 = AdminCode.getCodeName("UM", orgPriv);
                       if(StringUtils.isBlank(b0110)) {
                           b0110 = "";
                       }
                    }
                }
                pkgMap.put("b0110",orgPriv + "`" + b0110);
            }
            standMap.put("standList",standList);
            standMap.put("pkgMap",pkgMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return standMap;
    }

    /**
     * 保存单个历史沿革信息
     * @Author xuchangshun
     * @param standPackageInfor :历史沿革信息集合
     * @param ref_standIds:标准表id数组
     * @throws GeneralException 错误异常信息
     * @Date 2019/11/22 11:59
     */
    @Override
    public void saveStandPackageInfor(Map<String,String> standPackageInfor, List ref_standIds) throws GeneralException {
        int pkg_id = 0;
        String status = "";

        List sqlList = new ArrayList();
        List standardId = (ArrayList) ref_standIds.get(0);
        List newEnableId = (ArrayList) ref_standIds.get(1);
        List newCloseId = (ArrayList) ref_standIds.get(2);

        String init_type = "create";

        RecordVo vo = new RecordVo("gz_stand_pkg");
        StringBuffer deleteSql = new StringBuffer();
        StringBuffer insertSql = new StringBuffer();

        String deleteStandSql = "";
        String deleteItemSql = "";
        String updateCloseSql = "";
        String updateNewOpenSql = "";
        StringBuffer standAddSql=new StringBuffer();
        StringBuffer itemAddSql=new StringBuffer();

        String maxSql = "select max(pkg_id) id from gz_stand_pkg";
        //新建、修改历史沿革时gz_stand_pkg的vo
        for (Map.Entry<String, String> entry : standPackageInfor.entrySet()) {
            String key = entry.getKey();
            //新增修改时的pkg_id
            if (StringUtils.equalsIgnoreCase(key, "pkg_id")) {
                String value = entry.getValue();
                if(StringUtils.equalsIgnoreCase(value, "")) {
                    pkg_id = (standardPackageDao.getMaxPkgId(maxSql))+1;
                    vo.setInt("pkg_id", pkg_id);
                    newEnableId=standardId;
                    continue;
                } else {
                    vo.setString("pkg_id", value);
                    init_type = "edit";
                    pkg_id = Integer.valueOf(value);
                    continue;
                }
            }
            String value = entry.getValue();
            if (StringUtils.equalsIgnoreCase(key, "b0110")) {
                value = value.split("`")[0];
            } else if(StringUtils.equalsIgnoreCase(key, "start_date")) {
                vo.setDate(key,value);
                continue;
            } else if(StringUtils.equalsIgnoreCase(key, "status")) {

                if(StringUtils.equalsIgnoreCase(value, "1")) {
                    status = value;
                    vo.setString(key,"0");
                }
            }
            vo.setString(key,value);
        }

        //删除不引用的标准表
        if(newCloseId.size()>0) {
            int zheng = newCloseId.size()/999;
            int yu = newCloseId.size()%999;
            deleteSql.append(" ( ");
            if(zheng>0) {
                for(int i=0;i<zheng;i++){
                    if(i!=0){
                        deleteSql.append("or ");
                    }
                    deleteSql.append("id in (");
                    for(int j=i*999;j<(i+1)*999;j++){
                        if(j!=i*999){
                            deleteSql.append(",");
                        }
                        deleteSql.append(newCloseId.get(j));
                    }
                    deleteSql.append(")");
                }
                if(yu>0) {
                    deleteSql.append("or id in (");
                    for(int i=zheng*999;i<zheng*999+yu;i++){
                        if(i!=zheng*999){
                            deleteSql.append(",");
                        }
                        deleteSql.append(newCloseId.get(i));
                    }
                    deleteSql.append(")");
                }
            } else if(zheng == 0){
                if(yu>0){
                    deleteSql.append(" id in (");
                    for(int i=0;i<yu;i++){
                        if(i!=0){
                            deleteSql.append(",");
                        }
                        deleteSql.append(newCloseId.get(i));
                    }
                    deleteSql.append(")");
                }
            }
            deleteSql.append(" ) ");
        }
        //删除历史沿革停用的标准表
        if(newCloseId.size()>0) {
            deleteStandSql = "delete from gz_stand_history where pkg_id=" + pkg_id +" and  " + deleteSql+"";
            deleteItemSql = "delete from gz_item_history where pkg_id=" + pkg_id + " and  " + deleteSql+"";
        }

        //新选用的标准表
        if(newEnableId.size()>0) {
            int zheng = newEnableId.size()/999;
            int yu = newEnableId.size()%999;
            insertSql.append(" ( ");
            if(zheng>0) {
                for(int i=0;i<zheng;i++){
                    if(i!=0){
                        insertSql.append("or ");
                    }
                    insertSql.append("id in (");
                    for(int j=i*999;j<(i+1)*999;j++){
                        if(j!=i*999){
                            insertSql.append(",");
                        }
                        insertSql.append(newEnableId.get(j));
                    }
                    insertSql.append(")");
                }
                if(yu>0) {
                    insertSql.append("or id in (");
                    for(int i=zheng*999;i<zheng*999+yu;i++){
                        if(i!=zheng*999){
                            insertSql.append(",");
                        }
                        insertSql.append(newEnableId.get(i));
                    }
                    insertSql.append(")");
                }
            } else if(zheng == 0){
                if(yu>0){
                    insertSql.append(" id in (");
                    for(int i=0;i<yu;i++){
                        if(i!=0){
                            insertSql.append(",");
                        }
                        insertSql.append(newEnableId.get(i));
                    }
                    insertSql.append(")");
                }
            }
            insertSql.append(" ) ");
        }
        //更新现在启用历史沿革的标准表flag
        if(StringUtils.equals(status, "1")) {
            if(newCloseId.size()>0) {
                updateCloseSql = "update gz_stand set flag=0 where "+deleteSql+"";
            }
            if(newEnableId.size()>0) {
                updateNewOpenSql = "update gz_stand set flag=1 where "+insertSql+"";
            }
        }

        //新增历史沿革选用的标准表
        if (newEnableId.size()>0) {
            standAddSql.append("insert into gz_stand_history (id,pkg_id,name,s_hfactor,hfactor,hcontent,s_vfactor,vfactor,vcontent,item,createtime");
            if(StringUtils.equalsIgnoreCase(init_type, "create")) {
                standAddSql.append(",b0110,createorg ");
            }
            standAddSql.append(")  select id,"+pkg_id+",name,s_hfactor,hfactor,hcontent,s_vfactor,vfactor,vcontent,item,"+Sql_switcher.sqlNow()+"");
            if(StringUtils.equalsIgnoreCase(init_type, "create")) {
                standAddSql.append(",");
                String orgPriv = StandardUtil.getUnitOrManagePriv(this.userView);
                String orgPrivSub = "',"+orgPriv+"'";
                if(StringUtils.equalsIgnoreCase(orgPriv, "all")) {
                    standAddSql.append("Null,'UN' ");
                } else if(StringUtils.equalsIgnoreCase(orgPriv, "no")) {
                    standAddSql.append("Null,Null");
                } else {
                    standAddSql.append(orgPrivSub+","+orgPriv);
                }
            }
            standAddSql.append(" from gz_stand where "+insertSql+"");
            itemAddSql.append("insert into gz_item_history (id,pkg_id,hvalue,vvalue,s_hvalue,s_vvalue,standard) ");
            itemAddSql.append(" select id,"+pkg_id+", hvalue,vvalue,s_hvalue,s_vvalue,standard from gz_item where "+insertSql+"");
        }

        String standInsertSql = standAddSql.toString();
        String itemInsertSql = itemAddSql.toString();

        sqlList.add(init_type);
        sqlList.add(deleteStandSql);
        sqlList.add(deleteItemSql);
        sqlList.add(standInsertSql);
        sqlList.add(itemInsertSql);
        sqlList.add(updateCloseSql);
        sqlList.add(updateNewOpenSql);
        standardPackageDao.savePackageInfor(vo,sqlList);
        if(StringUtils.equalsIgnoreCase(status, "1")) {
            this.enablePackage(pkg_id+"",init_type);
        }
    }

    /**
     * 批量保存历史沿革信息
     * @Author xuchangshun
     * @param standPackageInforList : 历史沿革数据列表
     * @throws GeneralException 异常信息
     * @Date 2019/11/22 13:21
     */
    @Override
    public void batchSaveStandPackInfor(List standPackageInforList) throws GeneralException {
        try {
            ArrayList packageList = new ArrayList();
            for (int i = 0; i < standPackageInforList.size(); i++) {
                HashMap<String, String> modifiedData = PubFunc.DynaBean2Map((DynaBean) standPackageInforList.get(i));
                String pkg_id = PubFunc.decrypt(modifiedData.get("pkg_id"));
                //根据主键查询相应数据
                RecordVo vo = standardPackageDao.getPackageInfor(pkg_id);
                //拼装新值
                for (Map.Entry<String, String> entry : modifiedData.entrySet()) {
                    String key = entry.getKey();
                    if (StringUtils.equalsIgnoreCase(key, "pkg_id")) {
                        continue;
                    }
                    String value = entry.getValue();
                    if (StringUtils.equalsIgnoreCase(key, "b0110")) {
                        value = value.split("`")[0];
                    }
                    if (StringUtils.equalsIgnoreCase(key, "name")) {
                        value = PubFunc.hireKeyWord_filter(value);
                    }
                    vo.setString(key, value);
                    packageList.add(vo);
                }
            }
            standardPackageDao.batchSavePackageInfor(packageList);
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeneralException("gz.standard.saveFail");
        }
    }

    /**
     * 获取历史沿革所引用的标准表列表
     * @Author xuchangshun
     * @param pkg_id :历史沿革id
     * @return 历史沿革引用的标准表列表
     * @throws GeneralException 异常信息
     * @Date 2019/11/22 13:25
     */
    @Override
    public List<RecordVo> getStandListOfPackage(String pkg_id) throws GeneralException {
        List<RecordVo> standTableList = standardPackageDao.getStandTableList(pkg_id);
        return standTableList;
    }

    /**
     * 获取当前历史沿革中没有使用到的标准表列表
     * @Author xuchangshun
     * @param pkg_id :历史沿革id
     * @return 当前历史沿革中没有使用到的标准表列表
     * @throws GeneralException 异常信息
     * @Date 2019/11/22 13:29
     */
    @Override
    public List getNoUseInPackageStandList(String pkg_id) throws GeneralException {
        return null;
    }

    /**
     * 导出历史沿革中选中标准表的数据结构
     * @Author xuchangshun
     * @param pkg_id :历史沿革id
     * @param standIds :选中的标准表id,多个使用逗号分割
     * @return 导出数据的集合 成功：导出文件的路径，失败：导出失败原因提示信息
     * @throws GeneralException 异常信息
     * @Date 2019/11/22 13:54
     */
    @Override
    public Map exportPackageStandStruct(String pkg_id, String standIds,String outFileName) throws GeneralException {
        HashMap outNameMap = new HashMap();
        String outName = "";
        try {
            if (standIds != null && standIds.length() > 0) {
                outName = DownLoadXml.outPutXmlInfo(conn, standIds, userView,outFileName, pkg_id);
            }
            outNameMap.put("outName", outName);
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeneralException("gz.standard.pkg.exportSectStandardError");
        }
        return outNameMap;
    }

    /**
     * 导入历史沿革以及标准表的数据结构(初始化加载导入的标准表编号 名称)
     * @Author xuchangshun
     * @param fileId 文件加密id
     * @return  map 标准表的编号 名称 (失败 返回txt文件名)
     * @throws GeneralException 异常信息
     * @Date 2019/11/22 14:09
     */
    @Override
    public Map importPackageStandStruct(String fileId) throws GeneralException {
        Map map = new HashMap();
        String logFileName = userView.getUserName()+"_"+ResourceFactory.getProperty("standard.standardPackage.importLogName");
        String logPath = System.getProperty("java.io.tmpdir") + File.separator + logFileName;
        File file = new File(logPath);
        if(file.exists()) {
            file.delete();
        }
        //存放错误信息
        ArrayList<Object> msgList = new ArrayList<Object>();
        try {
            InputStream in = VfsService.getFile(fileId);
            //读取压缩包里的文件
            HashMap fileMap = DownLoadXml.extZipFileList(in);
            ArrayList gzStandardPackageInfo = DownLoadXml.getStandardList((String) fileMap.get("gz_stand.xml"),userView);
            map.put("gzStandardPackageInfo", gzStandardPackageInfo);
            if (gzStandardPackageInfo.size() == 0) { // 导入失败
                msgList.add(ResourceFactory.getProperty("standard.standardPackage.importError"));
                DownLoadXml.exportErrorLog(msgList,userView);
                logFileName = PubFunc.encrypt(logFileName);
                map.put("errorLogName", logFileName);
                return map;
            }
            if (file.exists()) {
                logFileName = PubFunc.encrypt(logFileName);
                map.put("errorLogName", logFileName);
                return map;
            }
        } catch (Exception e) {
            e.printStackTrace();
            msgList.add(e.getMessage());
            DownLoadXml.exportErrorLog(msgList,userView);
            logFileName = PubFunc.encrypt(logFileName);
            map.put("errorLogName", logFileName);
            throw new GeneralException("fail");
        }
        return map;
    }

    /**
     * 启用历史沿革
     * @Author xuchangshun
     * @param pkg_id :历史沿革id
     * @param init_type：区分历史沿革页面直接修改数据和新建修改页面修改数据
     * @throws GeneralException 异常信息
     * @Date 2019/11/22 14:14
     */
    @Override
    public void enablePackage(String pkg_id,String init_type) throws GeneralException {
        //关闭启用中的历史沿革并修改时间sql
        StringBuffer endSql = new StringBuffer();
        //启用点击的历史沿革并修改时间sql
        StringBuffer startSql = new StringBuffer();
        //根据点击的历史沿革更新gz_stand的sql
        StringBuffer updateEnableSql = new StringBuffer();
        StringBuffer updateDisableSql = new StringBuffer();
        //查找点击的历史沿革没有启用的标准表
        StringBuffer noImportBuffer = new StringBuffer();
        noImportBuffer.append("select id,name from gz_stand where id not in(");
        noImportBuffer.append("select distinct(id) from gz_stand_history where pkg_id = '");
        noImportBuffer.append(pkg_id).append("')");
        List<RecordVo> standardList = standardPackageDao.getStandTableList(pkg_id);
        List noImportList = standardPackageDao.getNoUseInPackageStandList(noImportBuffer.toString());

        endSql.append("update gz_stand_pkg set end_date=");
        startSql.append("update gz_stand_pkg set ");
        if (Sql_switcher.searchDbServer() == 2) {
            endSql.append(Sql_switcher.addDays("sysdate", "-1"));
            if(StringUtils.isBlank(init_type)) {
            	startSql.append("start_date = sysdate,");
            }
        } else {
            endSql.append(Sql_switcher.addDays("getdate()", "-1"));
            if(StringUtils.isBlank(init_type)) {
            	startSql.append("start_date = getdate(),");
            }
        }
        endSql.append(",status='0' where status='1'");
        startSql.append("end_date=null,status='1' where pkg_id='").append(pkg_id).append("'");

        if(standardList.size()>0) {
            updateEnableSql.append("update gz_stand set flag = 1 where (");
            int zheng = standardList.size()/999;
            int yu = standardList.size()%999;
            if(zheng>0) {
                for(int i=0;i<zheng;i++){
                    if(i!=0){
                        updateEnableSql.append("or ");
                    }
                    updateEnableSql.append("id in (");
                    for(int j=i*999;j<(i+1)*999;j++){
                        if(j!=i*999){
                            updateEnableSql.append(",");
                        }
                        RecordVo vo = standardList.get(j);
                        int standardId = vo.getInt("id");
                        updateEnableSql.append(standardId);
                    }
                    updateEnableSql.append(")");
                }
                if(yu>0) {
                    updateEnableSql.append("or id in (");
                    for(int i=zheng*999;i<zheng*999+yu;i++){
                        if(i!=zheng*999){
                            updateEnableSql.append(",");
                        }
                        RecordVo vo = standardList.get(i);
                        int standardId = vo.getInt("id");
                        updateEnableSql.append(standardId);
                    }
                    updateEnableSql.append(")");
                }
            } else if(zheng == 0){
                if(yu>0){
                    updateEnableSql.append(" id in (");
                    for(int i=0;i<yu;i++){
                        if(i!=0){
                            updateEnableSql.append(",");
                        }
                        RecordVo vo = standardList.get(i);
                        int standardId = vo.getInt("id");
                        updateEnableSql.append(standardId);
                    }
                    updateEnableSql.append(")");
                }
            }
            updateEnableSql.append(" ) ");
        }
        if(noImportList.size()>0) {
            updateDisableSql.append("update gz_stand set flag = 0 where (");
            int zheng = noImportList.size()/999;
            int yu = noImportList.size()%999;
            if(zheng>0) {
                for(int i=0;i<zheng;i++){
                    if(i!=0){
                        updateDisableSql.append("or ");
                    }
                    updateDisableSql.append("id in (");
                    for(int j=i*999;j<(i+1)*999;j++){
                        if(j!=i*999){
                            updateDisableSql.append(",");
                        }
                        List standardIdList =  (List) noImportList.get(j);
                        String standardId = (String) standardIdList.get(0);
                        updateDisableSql.append(standardId);
                    }
                    updateDisableSql.append(")");
                }
                if(yu>0) {
                    updateDisableSql.append("or id in (");
                    for(int i=zheng*999;i<zheng*999+yu;i++){
                        if(i!=zheng*999){
                            updateDisableSql.append(",");
                        }
                        List standardIdList =  (List) noImportList.get(i);
                        String standardId = (String) standardIdList.get(0);
                        updateDisableSql.append(standardId);
                    }
                    updateDisableSql.append(")");
                }
            } else if(zheng == 0){
                if(yu>0){
                    updateDisableSql.append(" id in (");
                    for(int i=0;i<yu;i++){
                        if(i!=0){
                            updateDisableSql.append(",");
                        }
                        List standardIdList =  (List) noImportList.get(i);
                        String standardId = (String) standardIdList.get(0);
                        updateDisableSql.append(standardId);
                    }
                    updateDisableSql.append(")");
                }
            }
            updateDisableSql.append(" ) ");
        }

        List sqlList = new ArrayList();
        sqlList.add(endSql.toString());
        sqlList.add(startSql.toString());
        sqlList.add(updateEnableSql.toString());
        sqlList.add(updateDisableSql.toString());
        standardPackageDao.enablePackage(sqlList,init_type);
    }

    /**
     * 删除历史沿革数据
     * @Author qinxx
     * @param pkg_id :历史沿革id
     * @return String
     * @throws GeneralException 异常信息
     * @Date 2019/12/10 13:38
     */
    @Override
    public String deletePackageInfor(String pkg_id) throws GeneralException {
        String flag = "yes";
        List<RecordVo> voList = standardPackageDao.getStandTableList(pkg_id);
        if (CollectionUtils.isEmpty(voList)) {
            RecordVo vo = new RecordVo("gz_stand_pkg");
            vo.setString("pkg_id", pkg_id);
            standardPackageDao.deletePackageInfor(vo);
        } else {
            flag = "no";
        }
        return flag;
    }

    /**
    * 导入历史沿革以及标准表的数据结构
    * @Author xuchangshun
    * @param flag : 0:加载导入标注表的名称  1:覆盖 2:追加
    * @param importStandardIds : 导入标注表的id
    * @param fileId :文件加密id
    * @return 导入数据的情况 成功：返回成功的提示信息 失败：形成错误日志文件路径，输出到前端供操作人员分析
    * @throws GeneralException 异常信息
    * @Date 2019/11/22 14:09
    */
    @Override
    public Map importPackageStandStruct(String flag, String importStandardIds, String fileId) throws GeneralException {
        String logFileName =userView.getUserName()+"_"+ResourceFactory.getProperty("standard.standardPackage.importLogName");
        String logPath = System.getProperty("java.io.tmpdir") + File.separator + logFileName;
        File file = new File(logPath);
        if(file.exists()) {
            file.delete();
        }
        //存放错误信息
        ArrayList<Object> msgList = new ArrayList<Object>();
        //返回数据
        Map logNameMap = new HashMap();
        String[] stand_ids = importStandardIds.split(",");
        InputStream in;
        try {
            in = VfsService.getFile(fileId);
            //读取压缩包里的文件
            HashMap fileMap = DownLoadXml.extZipFileList(in);
            ArrayList gzStandardPackageInfo = DownLoadXml.getStandardList((String) fileMap.get("gz_stand.xml"),userView);//5
            String value = "";
            boolean noManage = false;
            if (this.userView.isSuper_admin()) {
                value = "UN";
            } else {
                String unit_id = this.userView.getUnit_id();
                if (unit_id != null && unit_id.trim().length() > 2) {
                    if ("UN`".equalsIgnoreCase(unit_id)) {
                        value = "UN";
                    } else {
                        String[] arr = unit_id.split("`");
                        value = arr[0].substring(2);
                    }
                } else {
                    if (StringUtils.isEmpty(this.userView.getManagePrivCode())) {
                        noManage = true;
                    }
                    if (StringUtils.isNotEmpty(this.userView.getManagePrivCode())) {
                        String codevalue = (StringUtils.isEmpty(this.userView.getManagePrivCode())) ? "UN" : this.userView.getManagePrivCodeValue();
                        value = codevalue;
                    }
                }
            }
            if (noManage) {
                value = null;
            }
            //查找已启用的历史沿革
            String startPkgID = DownLoadXml.getStartPkgID(this.conn);
            byte[] b0 = ((String) fileMap.get("gz_stand.xml")).getBytes();
            if (b0.length == 0) {
                msgList.add(ResourceFactory.getProperty("standard.standardPackage.importError"));
                DownLoadXml.exportErrorLog(msgList,userView);
                logFileName = SafeCode.encode(PubFunc.encrypt(logFileName));
                logNameMap.put("errorLogName", logFileName);
                return logNameMap;
            }
            InputStream ip0 = new ByteArrayInputStream(b0);
            Document standard_doc = PubFunc.generateDom(ip0);
            byte[] b1 = ((String) fileMap.get("gz_item.xml")).getBytes();
            if (b1.length == 0) {
                msgList.add(ResourceFactory.getProperty("standard.standardPackage.importError"));
                DownLoadXml.exportErrorLog(msgList,userView);
                logFileName = SafeCode.encode(PubFunc.encrypt(logFileName));
                logNameMap.put("errorLogName", logFileName);
                return logNameMap;
            }
            InputStream ip1 = new ByteArrayInputStream(b1);
            Document item_doc = PubFunc.generateDom(ip1);
            //取得导入数据
            ArrayList gz_stand_data = DownLoadXml.getTableData(standard_doc, stand_ids, "2",userView);
            ArrayList gz_item_data = DownLoadXml.getTableData(item_doc, stand_ids, "3",userView);
            HashMap standIdMap = new HashMap();
            HashMap amap = new HashMap();
            //覆盖
            if ("1".equals(flag)) {
                //检查有无导入标准表权限
                HashMap map = DownLoadXml.getHasNoCreateOrgStand(startPkgID, stand_ids, this.conn, value);
                //删除覆盖的标准表(无权限不能删除被覆盖的标准表)
                DownLoadXml.deleteFromExistData(this.conn, gzStandardPackageInfo, stand_ids, startPkgID, map);
                //获取标准表数据
                gz_stand_data = DownLoadXml.editStandDataList(this.conn, gz_stand_data, standIdMap, 1, map, amap);
                gz_item_data = DownLoadXml.editStandDataList(this.conn, gz_item_data, standIdMap, 2, map, amap);
            } else if ("2".equals(flag)) {
                //追加 id+1
                gz_stand_data = DownLoadXml.editStandDataList(this.conn, gz_stand_data, standIdMap, 1);
                gz_item_data = DownLoadXml.editStandDataList(this.conn, gz_item_data, standIdMap, 2);
            }
            if (file.exists()) {
                logFileName = SafeCode.encode(PubFunc.encrypt(logFileName));
                logNameMap.put("errorLogName", logFileName);
            } else {
                DownLoadXml.importData(this.conn, gz_stand_data, gz_item_data, startPkgID, value, amap, flag);
            }
        } catch (Exception e) { //捕获到未知错误，写到日志文件中
            e.printStackTrace();
            msgList.add(((AbstractException) e).getErrorDescription()+"\r\n"+ResourceFactory.getProperty("standard.standardPackage.threeImportError"));
            DownLoadXml.exportErrorLog(msgList,userView);
            logFileName = PubFunc.encrypt(logFileName);
            logNameMap.put("errorLogName", logFileName);
            throw new GeneralException("fail");
        }
        return logNameMap;
    }

}
