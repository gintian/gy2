/**
 * FileName: IStandTableService
 * Author:   xuchangshun
 * Date:     2019/11/22 15:50
 * Description: 标准表服务接口
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.hjsj.hrms.module.gz.standard.standard.businessobject;

import com.hrms.struts.exception.GeneralException;

import java.util.List;
import java.util.Map;

/**
 * 〈接口功能描述〉<br> 
 * 〈标准表服务接口〉
 *
 * @Author xuchangshun
 * @Date 2019/11/22 15:50
 * @Since 1.0.0
 */
public interface IStandTableService {
    /**
     * 获取标准表列表表格组件
     * @Author xuchangshun
     * @param pkg_id 历史沿革id
     * @return String 表格组件
     * @throws GeneralException 异常信息
     * @Date 2019/11/22 16:30
     */
    String getStandardTableConfig(String pkg_id)throws GeneralException;
    /**
     * 获取标准表结构
     * @Author xuchangshun
     * @param pkg_id :历史沿革id
     * @param stand_id :标准表id
     * @return Map 标准表结构
     * @throws GeneralException 异常信息
     * @Date 2019/11/22 16:35
     */
    Map getStandStructInfor(String pkg_id,String stand_id)throws GeneralException;
    /**
     * 获得二级指标表达式的内容
     * @Author xuchangshun
     * @param item :要操作的二级指标id
     * @param item_id :要操作二级指标第几个表达式
     * @return List 二级表达式的内容
     * @throws GeneralException 异常信息
     * @Date 2019/11/22 16:38
     */
    List getItemLexpr(String item,String item_id)throws GeneralException;
    /**
     * 保存二级指标表达式的内容
     * @Author xuchangshun
     * @param itemInfor :二级指标表达式的信息
     * @return String 成功：success 失败 给出提示信息
     * @throws GeneralException 异常信息
     * @Date 2019/11/22 16:44
     */
    String saveItemLexpr(Map itemInfor)throws GeneralException;
    /**
     * 获取标准表编辑页面数据(仅创建、调整结构时调用)
     * @Author xuchangshun
     * @param pkg_id :历史沿革id
     * @param stand_id :标准表id
     * @param standInfor :标准表结构
     * @return Map 标准表编辑页面的数据对象
     * @throws GeneralException 异常信息
     * @Date 2019/11/22 16:51
     */
    Map getStandData(String pkg_id,String stand_id,Map standInfor)throws GeneralException;
    /**
     * 获取标准表编辑页面数据(编辑标准表数据时调用)
     * @Author xuchangshun
     * @param pkg_id :历史沿革id
     * @param stand_id :标准表id
     * @return Map 标准表编辑页面的数据对象
     * @throws GeneralException 异常信息
     * @Date 2019/11/22 16:51
     */
    Map getStandData(String pkg_id,String stand_id)throws GeneralException;
    /**
     * 保存标准表编辑页面数据(仅创建、调整结构时调用)
     * @Author xuchangshun
     * @param pkg_id :历史沿革id
     * @param stand_id :标准表id
     * @param standInfor :标准表结构
     * @param stanardDataList :标准表单元格数据
     * @param saveType :保存类型
     * @return 
     * @throws GeneralException 异常信息
     * @Date 2019/11/22 16:56
     */
    String saveStandData(String pkg_id,String stand_id,Map standInfor,List stanardDataList,String saveType)throws GeneralException;
    /**
     * 保存标准表编辑页面数据(仅编辑标准表时调用)
     * @Author xuchangshun
     * @param pkg_id :历史沿革id
     * @param stand_id :标准表id
     * @param stanardDataList :标准表数据
     * @throws GeneralException 异常信息
     * @Date 2019/11/22 16:56
     */
    void saveStandData(String pkg_id, String stand_id, List stanardDataList)throws GeneralException;
    /**
     * 导出标准表数据
     * @Author xuchangshun
     * @param pkg_id :历史沿革id
     * @param standIds :选中的标准表id,多个使用逗号分割
     * @return Map 成功包含导出文件的路径，失败 给出提示信息
     * @throws GeneralException 异常信息
     * @Date 2019/11/22 17:11
     */
    Map exportStandData(String pkg_id,String standIds)throws GeneralException;
    /**
     * 导入标准表数据
     * @Author xuchangshun
     * @param fileId :文件加密id
     * @return Map 成功 给出提示信息 失败 生成失败文件路径 供操作人员分析
     * @throws GeneralException 异常信息
     * @Date 2019/11/22 17:14
     */
    Map importStandData(String fileId, String pkg_id, String standardId) throws GeneralException;

    /**
     * 删除标准表
     * @Author xuchangshun
     * @param pkg_id :历史沿革id
     * @param stand_id :标准表id
     * @return String 返回成功或者失败的提示信息
     * @throws GeneralException 异常信息
     * @Date 2019/11/22 17:21
     */
    String deleteStand(String pkg_id,String stand_id)throws GeneralException;
    /**
     * 取得工资标准列表信息
     * @Author linjiasi
     * @param pkg_id
     * @return 工资标准表信息
     * @throws GeneralException 异常信息
     * @Date 2019/12/04 17:45
     */
    String getSalaryStandardList(String pkg_id)throws GeneralException;
    /**
     * 保存薪资标准表名称及归属单位的数据
     * @Author linjs
     * @param pkg_id :历史沿革id
     * @param stand_id :标准表id
     * @param name :标准表名称
     * @param b0110 :gz_stand_history表b0110列
     * @return String 返回成功或者失败的提示信息
     * @throws GeneralException 异常信息
     * @Date 2019/11/22 17:21
     */
    String saveStandardListOrg(String pkg_id, String stand_id,String name,String b0110) throws GeneralException;

    /**
     * 获取标准表列表编辑权限集合
     * @Author houby
     * @param pkg_id
     * @return map 标准表id以及权限标识
     * @throws GeneralException 异常信息
     * @Date 2019/12/04 17:45
     */
    Map getStandardCreatePriv(String pkg_id) throws GeneralException;
    /**
     * 获取调整结构所需要的标准表结构数据格式
     * @param standStructInfor  结构数据
     * @return
     */
    Map getFormatStandStructInfor(Map standStructInfor) throws GeneralException;

    /**
     * 检验标准表是否可以删除
     * @param stand_ids 标准表数组
     * @param pkg_id 历史沿革id
     * @return
     */
    Map checkStandDel(String[] stand_ids, String pkg_id);
}
