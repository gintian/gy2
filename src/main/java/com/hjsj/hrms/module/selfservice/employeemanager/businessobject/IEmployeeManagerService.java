package com.hjsj.hrms.module.selfservice.employeemanager.businessobject;

import com.hrms.struts.exception.GeneralException;

import java.util.Map;

public interface IEmployeeManagerService {
    /**
     * 获得查看员工指标集信息
     * @param nbase     人员库
     * @param a0100     人员编号
     * @param needPhoto 是否需要获取照片路径
     * @return Map
     * @throws GeneralException
     */
    Map searchEmployeeSetInfo(String nbase, String a0100, Boolean needPhoto) throws GeneralException;

    /**
     * 获取查看员工主集信息数据
     * @param nbase 人员库
     * @param a0100 人员编号
     * @return Map
     * @throwsException
     */
    Map searchEmployeeMainSetInfo(String nbase, String a0100) throws Exception;

    /**
     * 获得查看员工子集信息数据
     * @param nbase 人员库
     * @param a0100 人员编号
     * @param setId 子集id
     * @param groupName 分类名称
     * @param sortname 指标分类名称
     * @return Map
     * @throws GeneralException
     */
    Map searchEmployeeSubSetInfo(String nbase,String a0100,String setId,String groupName,String sortname)throws GeneralException;

    /**
     * 获得查看员工子集某条记录附件数据
     * @param nbase 人员库
     * @param a0100 人员编号
     * @param setId 子集id
     * @param i9999 子集记录编号
     * @return Map
     * @throws GeneralException
     */
    Map searchEmployeeSubSetAttachmentInfo(String nbase,String a0100,String setId,int i9999)throws GeneralException;

    /**
     * 上传人员照片
     * @param nbase 人员库
     * @param a0100 人员编号
     * @param fileStr 照片文件的Base64编码
     * @param fileurl 照片文件路径
     * @return Map
     * @throws Exception
     */
    Map savePhoto(String nbase, String a0100, String fileStr, String fileName) throws Exception;
}
