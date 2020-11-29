/*
 *
 *  *   @copyright      Copyright ©  2020 贵州银行 All rights reserved.
 *  *   @project        hrs-backend
 *  *   @author         warne
 *  *   @date           2020/5/20 下午3:06
 *  *
 *
 */

package com.hjsj.hrms.module.system.numberrule;

import java.io.Serializable;
import java.util.Date;

/**
 * 编号信息
 */
public class NumberRuleBean implements Serializable {

    private String id;//
    private String applicant;//申请人
    private String mobile;//手机号
    private Integer count;//编号个数
    private String remark;//申请说明
    private Date createTime;//创建时间
    private Date modTime;//修改时间
    private String createUserName;//创建者
    private String modUserName;//修改者
    private String numberList;//编号详情
    private String lastNo;//最新编号
    private String systemName;//系统名称（中文）
    private String systemCode;//系统简称（英文）

    private String deleteFlag; // 删除标志
    private String patchIndex; //# 生成批次
    private String requestType; //# 请求来源，0后台添加，1接口添加

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApplicant() {
        return applicant;
    }

    public void setApplicant(String applicant) {
        this.applicant = applicant;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModTime() {
        return modTime;
    }

    public void setModTime(Date modTime) {
        this.modTime = modTime;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public String getModUserName() {
        return modUserName;
    }

    public void setModUserName(String modUserName) {
        this.modUserName = modUserName;
    }

    public String getNumberList() {
        return numberList;
    }

    public void setNumberList(String numberList) {
        this.numberList = numberList;
    }

    public String getLastNo() {
        return lastNo;
    }

    public void setLastNo(String lastNo) {
        this.lastNo = lastNo;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getSystemCode() {
        return systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

    public String getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(String deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public String getPatchIndex() {
        return patchIndex;
    }

    public void setPatchIndex(String patchIndex) {
        this.patchIndex = patchIndex;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    @Override
    public String toString() {
        return "{" +
                "id='" + id + '\'' +
                ", applicant='" + applicant + '\'' +
                ", mobile='" + mobile + '\'' +
                ", count=" + count +
                ", remark='" + remark + '\'' +
                ", createTime=" + createTime +
                ", modTime=" + modTime +
                ", createUserName='" + createUserName + '\'' +
                ", modUserName='" + modUserName + '\'' +
                ", numberList='" + numberList + '\'' +
                ", lastNo='" + lastNo + '\'' +
                ", systemName='" + systemName + '\'' +
                ", systemCode='" + systemCode + '\'' +
                ", deleteFlag='" + deleteFlag + '\'' +
                ", patchIndex='" + patchIndex + '\'' +
                ", requestType='" + requestType + '\'' +
                '}';
    }
}
