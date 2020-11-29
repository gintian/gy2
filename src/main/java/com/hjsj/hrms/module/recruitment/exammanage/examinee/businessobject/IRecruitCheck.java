package com.hjsj.hrms.module.recruitment.exammanage.examinee.businessobject;

/**
 * 招聘资格检查接口（用于申请职位、二次推荐等处个性化校验身份）
 * @author zhaoxj
 */
public interface IRecruitCheck {
    public String check(String nbase, String a0100, String posId);
    // 招聘资格检查-二次推荐 nbase：人员库标识；a0100s:多个a0100组装的字符串，中间以,分割 ；
    // posId:岗位ID return:符合招聘资格检查的一个或一个以上的a0100字符串，中间以逗号分割
    public String checkA0100s(String nbase, String a0100s, String posId);
}
