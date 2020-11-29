package com.hjsj.hrms.servlet.sys;

public interface SecurityType {
	/**北京公安**/
    public static final int BJGA=0;
    /**天鸿集团**/
    public static final int TH=1;  
    /**联通*/
    public static final int UNICOM=2;
    /**东城人事局*/
    public static final int DCRSJ=3;
    /**活动目录认证,深国税*/
    public static final int ADGS=4;    
    /**630研究院*/
    public static final int AD630=5;
    /**世纪,自助平台，业务平台*/
    public static final int HJSJ=6;
    /**业务平台*/
    public static final int HJSJ_YW=7;
    /**自助平台*/
    public static final int HJSJ_ZZ=14; 
    /**自助平台-密码加密传过来－*/
    public static final int HJSJ_ZZ_DES=17;     
    /***/
    public static final int HYHZGS=8;
    /**农业部*/
    public static final int NYB_YW=9;    
    public static final int NYB_ZZ=10;
    /*解密转递登陆*/
    public static final int HJDES=11;
    /*光通过账号登陆OA连接但本应用的登陆还是账号和密码的,明源*/
    public static final int HJUSER_YW=12;
    public static final int HJUSER_ZZ=13;
    /**仅验证账号,建银投资*/
    public static final int JT_SSO=15;
    /**
     * 核电三期Epass接口
     */
    public static final int HJSJ_HD=16;
    public static final int BJ_MOBILE=18;
    public static final int HJSJ_ZZ_ZM=19;//通过SafeCode.encode对用户名转码
    public static final int HJSJ_YW_ZM=20;//通过SafeCode.encode对用户名转码
    public static final int HJSJ_YW4_ZM=21;//通过SafeCode.encode对用户名转码
    public static final int HJSJ_YW4_EM=25;//通过SafeCode.encode对用户名转码
    public static final int HJSJ_yljx=22;//引滦入津
    public static final int BJ_MOBILE_EMP=23;
    public static final int BJ_ZFW_BJCA=24;
    public static final int HJSJ_YW5_ZM=26;//通过SafeCode.encode对用户名转码
    public static final int HJSJ_HCM7=30;
    public static final int HJSJ_YW5_EM=27;//通过SafeCode.encode对用户名转码
    public static final int HJSJ_YW4_DES=28;//通过DES对用户名转码
    public static final int HJSJ_EM4_DES=29;//通过DES对用户名转码
    public static final int HJSJ_YW_DES=38;//通过DES对用户名转码
    public static final int HJSJ_EM_DES=39;//通过DES对用户名转码
    public static final int HJSJ_5_DES=40;//通过DES对用户名转码
    public static final int HJSJ_YW4_DES_SafeCode=35;//通过SafeCode先解密一次再用DES在解密一次对用户名转码，业务
    public static final int HJSJ_EM4_DES_SafeCode=36;//通过SafeCode先解密一次再用DES在解密一次对用户名转码，自助
    public static final int HJSJ_YW5_YP=50;//通过SafeCode.encode对用户名转码
    
    public static final int HJSJ_YW5_BASE64=33;//通过SafeCode.encode对用户名转码
    
    public static final int HJSJ_CEO=45;
    
    //北京路政局单点登录
    public static final int LZJ_SSO=46;
    
    //国家电网
    public static final int STATE_GRID=47;
    
    //在线学习门户
    public static final int HJSJ_ILEARNING = 55;
    
    //同帐号某一时间点仅允许登陆一次
    public static final String LOGOUT_OTH="only_logon_one";
}
