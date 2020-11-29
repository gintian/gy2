package com.hjsj.hrms.module.gz.templateset.taxtable.businessobject;

import com.hrms.struts.exception.GeneralException;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.beanutils.DynaBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description: 税率表业务接口类
 * @Author manjg
 * @Date 2019/12/3 15:31
 * @Version V1.0
 */
public interface ITaxTableService {

    /**
     * 税率表首页新建按钮权限功能号
     */
    String CREATE_FUNC_ID = "3240901";

    /**
     * 税率表首页删除按钮权限功能号
     */
    String DELETE_FUNC_ID = "3240902";

    /**
     * 税率表首页编辑权限功能号
     */
    String EDIT_FUNC_ID = "3240904";

    /**
     * 税率表首页导出、导入按钮权限功能号
     */
    String EXPORT_IMPORT_FUNC_ID = "3240906";

    /**
     * 获取税率表表格组件内容
     * @return
     * @throws GeneralException
     */
    String getTaxTableConfig() throws GeneralException;

    /**
     * 税率表保存
     * @param voList 税率表方案记录集合
     */
    void saveTaxTable(List<DynaBean> voList) throws GeneralException;

    /**
     * 删除税率方案记录
     * @param ids 税率表方案id
     */
    void deleteTaxTable(String ids) throws GeneralException;

    /**
     * 导出税率表方案
     * @param ids 税率表方案id
     * @return
     * @throws GeneralException
     */
    String exportTaxTable(String ids) throws GeneralException;

    /**
     * 导入税率表方案
     * @param fileId 文件加密id
     * @throws GeneralException
     */
    List<Map> importTaxTable(String fileId) throws GeneralException;
    /**
     * 导入税率表方案
     * @param taxList 追加或是覆盖数据
     * @throws GeneralException
     */
    void importTaxTable(List<MorphDynaBean> taxList) throws GeneralException;

    /**
     * 获取税率明细表表格组件内容
     * @return
     * @throws GeneralException
     */
    String getTaxTableDetailConfig(String txid) throws GeneralException;

    /**
     * 税率明细方案保存
     * @param taxData 税率表方案记录集合
     * @param taxDetailData 税率表明细方案记录集合
     * @throws GeneralException
     */
    String saveTaxTableDetail(List taxData,List taxDetailData) throws GeneralException;

    /**
     * 删除税率表明细方案
     * @param id 税率表明细方案id
     * @throws GeneralException
     */
    void deleteTaxTableDetail(String id) throws GeneralException;

    /**
     * 获取计税方式代码类指标集合
     * @return 计税方式代码项集合
     * @throws GeneralException
     */
    List getTaxModeCodeItem() throws GeneralException;

    /**
     * 检验税率表首页操作权限（编辑）
     * @param funcid 权限ID
     * @return 权限标志
     * @throws GeneralException
     */
    boolean isHaveOperationPriv(String funcid) throws GeneralException;

    /**
     * 刷新税率明细表表格数据
     * @param taxId 税率表ID
     * @throws GeneralException
     */
    void refsTableData(String taxId) throws GeneralException;

    /**
     * 删除税率明细表数据
     * @param ids
     */
    void deleteTaxTableDetail(ArrayList<String> ids) throws GeneralException;
}
