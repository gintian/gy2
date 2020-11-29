/**
 * FileName: IStandardPackageDao
 * Author:   xuchangshun
 * Date:     2019/11/22 14:57
 * Description: 历史沿革数据层操作接口
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.hjsj.hrms.module.gz.standard.standardpackage.dao;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;

import java.util.List;

/**
 * 〈接口功能描述〉<br> 
 * 〈历史沿革数据层操作接口〉
 *
 * @Author xuchangshun
 * @Date 2019/11/22 14:57
 * @Since 1.0.0
 */
public interface IStandardPackageDao {
    /**
     * 获取历史沿革列表
     * @Author xuchangshun
     * @param sql 查询语句
     * @return List<RecordVo> 历史沿革列表数据
     * @throws GeneralException 异常信息
     * @Date 2019/11/22 15:05
     */
    List<RecordVo> getPackageList(String sql) throws GeneralException;

    /**
     * 获取单个历史沿革的信息
     * @Author xuchangshun
     * @param pkg_id :历史沿革id
     * @return RecordVo:历史沿革信息集
     * @throws GeneralException 异常信息
     * @Date 2019/11/22 15:07
     */
    RecordVo getPackageInfor(String pkg_id) throws GeneralException;
    /**
     * 批量保存历史沿革数据
     * @Author xuchangshun
     * @param packageList :历史沿革数据列表
     * @throws GeneralException 异常信息
     * @Date 2019/11/22 15:17
     */
    void batchSavePackageInfor(List<RecordVo> packageList)throws GeneralException;
    
    /**
     * 保存历史沿革数据
     * @Author xuchangshun
     * @param vo :历史沿革数据vo
     * @param sqlList:sql语句集合以及init_type
     * @throws GeneralException 异常信息
     * @Date 2019/11/22 15:23
     */
    void savePackageInfor(RecordVo vo,List sqlList)throws GeneralException;
    /**
     * 删除历史沿革数据
     * @Author xuchangshun
     * @param vo :历史沿革数据vo
     * @throws GeneralException 异常信息
     * @Date 2019/11/22 15:26
     */
    void deletePackageInfor(RecordVo vo)throws GeneralException;
    /**
     * 批量删除历史沿革数据
     * @Author xuchangshun
     * @param packageList :历史沿革数据列表
     * @return String 成功返回success 失败返回错误的提示信息
     * @throws GeneralException 异常信息
     * @Date 2019/11/22 15:29
     */
    String batchDeletePackageInfor(List<RecordVo> packageList)throws GeneralException;
    /**
     * 启用历史沿革
     * @Author qinxx
     * @param sqlList:启用历史沿革sql集合
     * @param init_type：区分历史沿革页面直接修改数据和新建修改页面修改数据
     * @throws GeneralException 异常信息
     * @Date 2019/12/06 10:42
     */
    void enablePackage(List sqlList,String init_type) throws GeneralException;
    
    /**
     * 获取标准表列表
     * @Author xuchangshun
     * @param pck_id :历史沿革id
     * @return List 标准表列表
     * @throws GeneralException 异常信息
     * @Date 2019/11/22 17:55
     */
    List<RecordVo> getStandTableList(String pck_id)throws GeneralException;
    /**
     * 获取当前历史沿革中没有使用到的标准表列表
     * @Author qinxx
     * @param noImportSql :未引用标准表的sql
     * @return 当前历史沿革中没有使用到的标准表列表
     * @throws GeneralException 异常信息
     * @Date 2019/12/12 18:29
     */
    List getNoUseInPackageStandList(String noImportSql) throws GeneralException;
    /**
     * 获取顶级机构代码
     * @Author qinxx
     * @return 顶级机构代码
     * @param superOrganizationSql:顶级机构代码sql
     * @throws GeneralException 异常信息
     * @Date 2019/12/13 14:29
     */
    String getSuperOrganization(String superOrganizationSql) throws GeneralException;
    /**
     * 创建历史沿革时获取id
     * @Author qinxx
     * @return 最大id
     * @param maxSql:最大id的sql
     * @throws GeneralException 异常信息
     * @Date 2019/12/14 11:16
     */
    int getMaxPkgId(String maxSql) throws GeneralException;
}
