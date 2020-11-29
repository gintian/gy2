/**
 * FileName: IStandardPackageService
 * Author:   xuchangshun
 * Date:     2019/11/6 13:29
 * Description: 薪资标准历史沿革接口类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.hjsj.hrms.module.gz.standard.standardpackage.businessobject;

import com.hrms.struts.exception.GeneralException;

import java.util.List;
import java.util.Map;

/**
 * 〈接口功能描述〉<br> 
 * 〈薪资标准历史沿革接口类〉
 *
 * @Author xuchangshun
 * @Date 2019/11/6 13:29
 * @Since 1.0.0
 */
public interface IStandardPackageService {
    /** 历史沿革表格id */
    String SUBMODULEID = "standardPackageId";
    /** 新建按钮权限号 */
    String ADD_FUNCID = "3241001";
    /** 所属组织列编辑、操作列编辑按钮权限号 */
    String EDIT_FUNCID = "3241002";
    /** 操作列删除按钮权限号 */
    String DELETE_FUNCID = "3241003";
    /** 重命名历史沿革 */
    String RENAME_FUNCID = "3241005";
    /** 启用历史沿革 */
    String ENABLE_FUNCID = "3241006";
    /** 导入导出按钮 */
    String IMPORT_EXPORT_FUNCID = "3241007";
    /** UN`表示操作单位全权 */
    String ALL_PRIV = "UN`";
    /** 操作单位权限长度*/
    int UNIT_ID_LENGTH = 2;
    /** 全权 */
    String ALL = "all";
    /** 无权 */
    String NO = "no";
    /** stop 不再继续运行下面的代码 */
    String STOP = "stop";

    /**
     * 获取历史沿革表格列表的tableconfig
     * @Author xuchangshun
     * @return 表格组件的tableconfig
     * @throws GeneralException 错误异常信息
     * @Date 2019/11/22 11:51
     */
     String getStandardPackageTableConfig() throws GeneralException;

    /**
     * 获取历史沿革列表功能权限Map(页面细节控制用)
     * @author wangbs
     * @return Map 权限map
     * @throws GeneralException 错误异常信息
     * @date 2019/12/6 11:08
     */
    Map getFuncPrivMap() throws GeneralException;
     /**
      * 获取历史沿革的相关信息(包含引用的历史沿革列表)
      * @Author xuchangshun
      * @param pkg_id :历史沿革id
      * @return Map 历史沿革的相关信息
      * @throws GeneralException 错误异常信息
      * @Date 2019/11/22 11:51
      */
     Map getStandPackageInfor(String pkg_id, String init_type)throws GeneralException;
     /**
      * 保存单个历史沿革信息
      * @Author xuchangshun
      * @param standPackageInfor :历史沿革信息集合
      * @param ref_standIds:标准表id数组
      * @throws GeneralException 错误异常信息
      * @Date 2019/11/22 11:59
      */
     void saveStandPackageInfor(Map<String,String> standPackageInfor, List ref_standIds)throws GeneralException;
     /**
      * 批量保存历史沿革信息
      * @Author xuchangshun
      * @param standPackageInforList : 历史沿革数据列表
      * @throws GeneralException 异常信息
      * @Date 2019/11/22 13:21
      */
     void batchSaveStandPackInfor(List standPackageInforList)throws GeneralException;
     /**
      * 获取历史沿革所引用的标准表列表
      * @Author xuchangshun
      * @param pkg_id :历史沿革id
      * @return 历史沿革引用的标准表列表
      * @throws GeneralException 异常信息
      * @Date 2019/11/22 13:25
      */
     List getStandListOfPackage(String pkg_id)throws GeneralException;
     /**
      * 获取当前历史沿革中没有使用到的标准表列表
      * @Author xuchangshun
      * @param pkg_id :历史沿革id
      * @return 当前历史沿革中没有使用到的标准表列表
      * @throws GeneralException 异常信息
      * @Date 2019/11/22 13:29
      */
     List getNoUseInPackageStandList(String pkg_id) throws GeneralException;
     /**
      * 导出历史沿革中选中标准表的数据结构
      * @Author xuchangshun
      * @param pkg_id :历史沿革id
      * @param standIds:选中的标准表id,多个使用逗号分割
      * @return 导出数据的集合 成功：导出文件的路径，失败：导出失败原因提示信息
      * @throws GeneralException 异常信息
      * @Date 2019/11/22 13:54
      */
     Map  exportPackageStandStruct(String pkg_id,String standIds,String outFileName)throws GeneralException;
     /**
      * 导入历史沿革以及标准表的数据结构
      * @Author xuchangshun
      * @param fileId 文件加密id
      * @return 导入数据的情况 成功：返回成功的提示信息 失败：形成错误日志文件路径，输出到前端供操作人员分析
      * @throws GeneralException 异常信息
      * @Date 2019/11/22 14:09
      */
     Map  importPackageStandStruct(String fileId) throws GeneralException;
     /**
      * 启用历史沿革
      * @Author xuchangshun
      * @param pkg_id :历史沿革id
      * @param init_type：区分历史沿革页面直接修改数据和新建修改页面修改数据
      * @throws GeneralException 异常信息
      * @Date 2019/11/22 14:14
      */
     void enablePackage(String pkg_id,String init_type)throws GeneralException;
     /**
      * 删除历史沿革数据
      * @Author qinxx
      * @param pkg_id :历史沿革id
      * @return String
      * @throws GeneralException 异常信息
      * @Date 2019/12/10 13:38
      */
     String deletePackageInfor(String pkg_id) throws GeneralException;
     
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
     Map  importPackageStandStruct(String flag,String importStandardIds,String fileId) throws GeneralException;
}
