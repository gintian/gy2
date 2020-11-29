/*
 * @(#)GetPasswordTrans.java 2018年7月2日下午2:38:29 ehr Copyright 2018 HJSOFT, Inc.
 * All rights reserved. HJSOFT PROPRIETARY/CONFIDENTIAL. Use is subject to
 * license terms.
 */
package com.hjsj.hrms.module.serviceclient.serviceSetting;

import com.hjsj.hrms.businessobject.sys.SmsBo;
import com.hjsj.hrms.businessobject.sys.SysParamBo;
import com.hjsj.hrms.businessobject.sys.SysParamConstant;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.Des;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @Titile: GetPasswordTrans
 * @Description:
 * @Company:hjsj
 * @Create time: 2018年7月2日下午2:38:29
 * @author: wangz
 * @version 1.0
 *
 */
public class GetPasswordTrans extends IBusiness {
    private String msg="-1";
    private boolean flag=false;
    private Pattern phonepattern=Pattern.compile("[0-9]{11}");
    private Random random;//=new Random();
    private StringBuffer strSrc=new StringBuffer();
    private String username;
    private String password;
    String usernamevalue="";
    String passwordvalue="";
    String userfullname="";
    String sql="";
    String email="";
    String phone="";
    @Override
    public void execute() throws GeneralException {

        try {
            /**用户登录平台=1业务=2自助*/
            String logintype = (String) this.getFormHM().get("logintype");
            /**找回密码方式=1根据电话=2根据邮箱 自助终端规定死的使用手机*/
            String type = (String) this.getFormHM().get("type");
            /**电话或邮箱*/
            String inputusername = SafeCode.decode((String) this.getFormHM().get("ZE"));
            String ZE = inputusername;
            ContentDAO dao = new ContentDAO(this.getFrameconn());

            /**取得密码的长度*/
            int pwdlen = 8;//默认的密码长度8位
            String pwdlength = SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, SysParamConstant.PASSWORDLENGTH);
            try {
                pwdlen = Integer.parseInt(pwdlength);
            } catch (Exception e) {
                pwdlen = 8;
            }
            String newpassword = getRandomPwd(pwdlen);
            //验证密码通过此发送方式是否可修改  
            boolean isValidateSuccess = validateCanChangeSelf(newpassword, ZE, type);
            if(!isValidateSuccess) {//校验未通过,返回
                return;
            }
            if (!flag) {
                msg = "1";//"系统未找到与您输入匹配的用户，请您仔细检查输入是否正确!",前端基于标志位提示相应的信息
                this.getFormHM().put("msg", msg);
                return;
            }
            this.usernamevalue = inputusername;
            String why = SystemConfig.getPropertyValue("sys_name");
            if (StringUtils.isEmpty(why)) {
                why = StringUtils.EMPTY;
            }
                
            String str = new String(why.getBytes("ISO-8859-1"), "gb2312");
            if (str.length() == 0) {
                str = ResourceFactory.getProperty("frame.logon.title");
            }
            StringBuffer content = new StringBuffer("");
            String helloMessage = ResourceFactory.getProperty("serviceclient.hello");//您好：
            String yourMessage = ResourceFactory.getProperty("serviceclient.your");//您的
            String userNameMessage = ResourceFactory.getProperty("serviceclient.username");//用户名：
            String passwordMessage = ResourceFactory.getProperty("serviceclient.password");//密码：
            content.append(((userfullname == null || "".equals(userfullname)) ? usernamevalue : userfullname) + ",").append(helloMessage);//您好
            content.append(yourMessage).append(str).append(",");//您的....,
            content.append(userNameMessage).append("\"").append(usernamevalue).append("\",");//用户名：.....,
            content.append(passwordMessage).append("\"").append(StringUtils.trimToEmpty(passwordvalue)).append("\"").append("。");//密码：.....。
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            content.append(format.format(new Date()));
            try {
                    ArrayList destlist = new ArrayList();
                    LazyDynaBean dyvo = new LazyDynaBean();
                    dyvo.set("sender", str);
                    dyvo.set("receiver", phone);
                    dyvo.set("phone_num", phone);
                    dyvo.set("msg", content.toString());
                    destlist.add(dyvo);
                    SmsBo smsbo = new SmsBo(this.getFrameconn());
                    smsbo.batchSendMessage(destlist);
                    msg = "0";
            } catch (Exception e) {
                e.printStackTrace();
                msg = "2";//"短信服务器配置不成功,未能成功发送短信!",前端基于标志位提示相应的信息
                this.getFormHM().put("msg", msg);
                return;
            }
            dao.update(sql);
            this.getFormHM().put("msg", msg);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public GetPasswordTrans() {
        strSrc.append("qazwsxedcrfvtgbyhnujmiklp192384756");
        random=new Random(System.currentTimeMillis());
    }
    /**
     * 验证密码是否可以通过这种方式进行修改
     * @param newpassword 新密码
     * @param ZE 输入的用户名,(自助终端应当是输入进来的卡号或者身份证号)
     * @param type 获取密码的方式
     * @throws Exception
     */
    private boolean validateCanChangeSelf(String newpassword,String ZE,String type) throws Exception{
        boolean isSuccess = true;//校验是否通过,默认是true
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        String phone="";
        String zpFld = "";//自定义用户名指标
        String pwdFld="";//自定义密码指标
        String phoneFld="";//自定义移动电话指标
        if("1".equals(type)){//如果通过手机号码方式找回
            RecordVo avo = ConstantParamter.getConstantVo("SS_MOBILE_PHONE");
            if(avo!=null){
                phoneFld=avo.getString("str_value");
            }
            if(phoneFld==null|| "".equals(phoneFld)|| "#".equals(phoneFld)){
                msg="3";//"系统未设置移动电话指标，无法找回密码!",这里传递标志位,前台展现信息提示
                this.getFormHM().put("msg", msg);
                isSuccess = false;
                return isSuccess;
            }
        }
        RecordVo login_vo = ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
        Sys_Oth_Parameter sybo = new Sys_Oth_Parameter(this.frameconn);
        String cardIdField = sybo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"1","name");
        if (login_vo != null){
            String login_name = login_vo.getString("str_value");//存储自定义的用户名和密码指标,用逗号分隔开,如果都没有设置则只有一个逗号
            int idx = login_name.indexOf(",");
            if (idx != -1){
                zpFld = login_name.substring(0, idx);
                if(login_name.length()>idx)
                   pwdFld=login_name.substring(idx+1);
            }
         }
        if ("".equals(zpFld)|| "#".equals(zpFld)) {//给用户名指标一个默认值
            zpFld = "username";
        }
        if("".equals(pwdFld)|| "#".equals(pwdFld)) {//给密码指标一个默认值
           pwdFld="userpassword";
        }
        String[] pre = this.getPre(dao);
        for(int i=0;i<pre.length;i++){
            StringBuffer buf = new StringBuffer("");
            if("1".equals(type)){
                buf.append("select a0101,"+zpFld+","+pwdFld+","+phoneFld+" from "+pre[i]+"a01 where "+cardIdField+" ='"+ZE+"'");
            }
            this.frowset=dao.search(buf.toString());
            while(this.frowset.next()){
                flag=true;
                usernamevalue=this.frowset.getString(zpFld);
                userfullname=this.frowset.getString("a0101");
                passwordvalue=newpassword;
                if("1".equals(type)){
                    phone=this.frowset.getString(phoneFld);
                    phone=phone==null?"":phone;
                    if(phone.length()<1){
                        msg="5";//msg="无移动电话号码，无法找回密码!";
                        this.getFormHM().put("msg", msg);
                        isSuccess = false;
                        return isSuccess;
                    }
                    Matcher m = phonepattern.matcher(phone);
                    if(!m.matches()){
                        msg="6";//msg="移动电话号码不正确，无法找回密码!";
                        this.getFormHM().put("msg", msg);
                        isSuccess = false;
                        return isSuccess;
                    }
                }
            }
            if(flag){
                sql="update "+pre[i]+"a01 set "+pwdFld+"='"+newpassword+"' where "+cardIdField+"='"+ZE+"'";
                if(ConstantParamter.isEncPwd(this.getFrameconn())){
                    Des des0=new Des(); 
                    String newpwd=des0.EncryPwdStr(newpassword);    
                    sql="update "+pre[i]+"a01 set "+pwdFld+"='"+newpwd+"' where "+cardIdField+"='"+ZE+"'";
                }
                break;

            }
        }
        this.phone = phone;
        return isSuccess;
    }
    /**
     * 取得随机生成的密码的长度
     * @param pwdlen
     * @return
     */
    private String getRandomPwd(int pwdlen) {
        StringBuffer strpwd = new StringBuffer();
        int index = 0;
        for (int i = 0; i < pwdlen; i++) {
            index = random.nextInt(33);
            strpwd.append(this.strSrc.charAt(index));
        }
        return strpwd.toString();
    }
    /**
     * 获取人员库前缀
     * @param dao
     * @return
     */
    public String[] getPre(ContentDAO dao){
        String[] pre = new String[0];
        try{
            /**登录参数表*/
            RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN");
            String login_pre="";
            if(login_vo!=null) { 
                login_pre = login_vo.getString("str_value").toLowerCase();
            }
            
            if(login_pre.length()==0) {
                return pre;
            }
            String sql ="select pre from dbname ";
            this.frowset=dao.search(sql);
            String tempPres="";
            int i=0;
            while(this.frowset.next())
            {
                String dbpre = this.frowset.getString(1);
                if(login_pre.toLowerCase().indexOf(dbpre.toLowerCase())!=-1){
                    if(i!=0) {
                        tempPres+=",";
                    }
                    tempPres+=dbpre;
                    i++;
                }
            }
            pre=tempPres.split(",");
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return pre;
    }
}
