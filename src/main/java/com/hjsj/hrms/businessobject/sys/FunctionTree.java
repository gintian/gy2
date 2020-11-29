package com.hjsj.hrms.businessobject.sys;

import com.hrms.hjsj.sys.EncryptLockClient;
import com.hrms.struts.constant.SystemConfig;

public class FunctionTree {
	private EncryptLockClient lock;
	public FunctionTree(EncryptLockClient lock)
	{
		this.lock=lock;
	}
	  /**
     * 是否输出功能列表
     * @param func_id
     * @return
     */
    public boolean isMayOut(String func_id,String username)
    {
    	boolean bflag=true;
        if(("0".equals(func_id)&&(!(isHaveBusiDesk(0,username)||isHaveBusiDesk(1,username)||isHaveBusiDesk(2,username)||
        		isHaveBusiDesk(3,username)||isHaveBusiDesk(4,username)||isHaveBusiDesk(5,username)||
        		isHaveBusiDesk(22,username)||isHaveBusiDesk(29,username)||isHaveBusiDesk(32,username)||isHaveBusiDesk(35,username))))) {
            bflag=false;
        }
    	
        if(("2".equals(func_id)&&(!(isHaveBusiDesk(11,username)||isHaveBusiDesk(7,username)||isHaveBusiDesk(15,username))))) {
            bflag=false;
        }

        if(("03".equals(func_id)&&(!(isHaveBusiDesk(1,username))))) {
            bflag=false;
        }
        if(("05".equals(func_id)&&(!(isHaveBusiDesk(1,username))))) {
            bflag=false;
        }
        if(("04".equals(func_id)&&(!(isHaveBusiDesk(1,username))))) {
            bflag=false;
        }
        /**
         *目标管理必须购买360模块（目标可选）
         */
        if(("06".equals(func_id)&&(!(isHaveBusiDesk(3,username)/*||isHaveBusiDesk(29,username)*/)))) {
            bflag=false;
        }
        if(("09".equals(func_id)&&(!isHaveBusiDesk(2,username)))) {
            bflag=false;
        }
        if(("0A".equals(func_id)&&(!isHaveBusiDesk(4,username)))) {
            bflag=false;
        }
        if(("0B".equals(func_id)&&(!isHaveBusiDesk(5,username))))
        {
        	bflag=false;
        }
        if(("0C".equals(func_id)&&(!isHaveBusiDesk(22,username))))
        {
        	bflag=false;
        }        
        if(("01".equals(func_id)&&(!(isHaveBusiDesk(0,username))))) {
            bflag=false;
        }
        /* 放开版本授权控制,系统管理不进行版权控制 chenmengqing changed at 20110620
        if((func_id.equals("07")&&(!isHaveBusiDesk(0,username))))
        	bflag=false;
        if((func_id.equals("08")&&(!(isHaveBusiDesk(0,username)))))
        	bflag=false;
        	*/
        if(("11".equals(func_id)&&(!isHaveBusiDesk(0,username)))) {
            bflag=false;
        }
        if(("230".equals(func_id)&&(!isHaveBusiDesk(11,username)))) {
            bflag=false;
        }
        if(("250".equals(func_id)&&(!isHaveBusiDesk(11,username)))) {
            bflag=false;
        }
        if(("260".equals(func_id)&&(!isHaveBusiDesk(11,username)))) {
            bflag=false;
        }

        if(("300".equals(func_id)&&(!isHaveBusiDesk(11,username)))) {
            bflag=false;
        }
        if(("290".equals(func_id)&&(!isHaveBusiDesk(13,username)))) {
            bflag=false;
        }
        if(("270".equals(func_id)&&(!isHaveBusiDesk(6,username)))) {
            bflag=false;
        }
        if(("240".equals(func_id)&&(!isHaveBusiDesk(7,username)))) {
            bflag=false;
        }
        if(("330".equals(func_id)&&(!isHaveBusiDesk(15,username)))) {
            bflag=false;
        }
        if(("280".equals(func_id)&&(!isHaveBusiDesk(12,username)))) {
            bflag=false;
        }
        if(("340".equals(func_id)&&(!isHaveBusiDesk(12,username)))) {
            bflag=false;
        }
        if(("324".equals(func_id)&&(!isHaveBusiDesk(8,username)))) {
            bflag=false;
        }
        if(("325".equals(func_id)&&(!isHaveBusiDesk(14,username)))) {
            bflag=false;
        }
        if(("320".equals(func_id)&&(!isHaveBusiDesk(20,username)))) {
            bflag=false;
        }
        if(("321".equals(func_id)&&(!isHaveBusiDesk(18,username)))) {
            bflag=false;
        }
        if(("322".equals(func_id)&&(!isHaveBusiDesk(19,username)))) {
            bflag=false;
        }
        if(("323".equals(func_id)&&(!isHaveBusiDesk(10,username)))) {
            bflag=false;
        }
        if(("310".equals(func_id)&&(!isHaveBusiDesk(7,username)))) {
            bflag=false;
        }
        if(("326".equals(func_id)&&(!isHaveBusiDesk(9,username)))) {
            bflag=false;
        }
        if(("327".equals(func_id)&&(!isHaveBusiDesk(8,username)))) {
            bflag=false;
        }
        if(("0607".equals(func_id)&&(!isHaveBusiDesk(29,username)))) {
            bflag=false;
        }
        /**
         * cmq changed at 20131024 c+b,for bugid=0039713 绩效模块加密锁服务没有传过来，仅自助相关的模块
         */
        if(("0608".equals(func_id)&&(!((isHaveBusiDesk(9,username))||(isHaveBusiDesk(3,username)))))) {
            bflag=false;
        }
        //if((func_id.equals("0609")&&(!isHaveBusiDesk(29,username))))
        //	bflag=false;   
        /**移动服务*/
        if(("0K".equals(func_id)&&(!isHaveBusiDesk(35,username)))) {
            bflag=false;
        }
        /**总裁桌面*/
        if(("AK".equals(func_id)&&(!isHaveBusiDesk(34,username)))) {
            bflag=false;
        }
        if(("9902".equals(func_id)&&(!isHaveBusiDesk(9,username))))
        {
        	bflag=false;
        }   
        if(("9903".equals(func_id)&&(!isHaveBusiDesk(8,username))))
        {
        	bflag=false;
        }   
        if(("9904".equals(func_id)&&(!isHaveBusiDesk(10,username))))
        {
        	bflag=false;
        }  
        if(("9905".equals(func_id)&&(!isHaveBusiDesk(14,username))))
        {
        	bflag=false;
        }    
        if(("9906".equals(func_id)&&(!isHaveBusiDesk(15,username))))
        {
        	bflag=false;
        }   
        if(("9907".equals(func_id)&&(!isHaveBusiDesk(16,username))))
        {
        	bflag=false;
        }   
        if(("9908".equals(func_id)&&(!isHaveBusiDesk(17,username))))
        {
        	bflag=false;
        }         
        if(("990".equals(func_id)&&(!isHaveBusiDesk(11,username))))
        {
        	bflag=false;
        }      
        if(("301".equals(func_id)&&(!isHaveBusiDesk(11,username))))
        {
        	bflag=false;
        }     
        if(("360".equals(func_id)&&(!isHaveBusiDesk(36,username))))
        {
        	bflag=false;
        } 
        if(("28".equals(func_id)&&(!isHaveBusiDesk(12,username))))
        {
        	bflag=false;
        } 
        if(("231".equals(func_id)&&(!isHaveBusiDesk(11,username))))
        {
        	bflag=false;
        } 
        if(("331".equals(func_id)&&(!isHaveBusiDesk(20,username))))
        {
        	bflag=false;
        }
        /*项目工时*/
        if(("390".equals(func_id)&&(!isHaveBusiDesk(7,username))))
        {
        	bflag=false;
        }
        /*考勤管理*/
        if(("271".equals(func_id)&&(!isHaveBusiDesk(6,username))))
        {
        	bflag=false;
        }
        /*证照管理*/
        if(("400".equals(func_id)&&(!isHaveBusiDesk(52,username))))
        {
        	bflag=false;
        }
        if("9A0".equals(func_id)){//仅针对c+b，自助模块就不用显示工具箱
        	bflag=lock.isBmodule(11,username);
        }
        bflag=bflag&&haveCustomFunc(func_id);
    	return bflag;
    }

    /**
     * 客户定制的个性化需求,有的用户就不输出啦
     * @param funcid
     * @return
     */
    private boolean haveCustomFunc(String funcid)
    {
    	String cfunc=",080804,30044,";
    	boolean isldap=SystemConfig.isLdap();
    	if(cfunc.indexOf(","+funcid+",")==-1) {
            return true;
        }
    	if(cfunc.indexOf(","+funcid+",")!=-1&&isldap) {
            return true;
        }
    	return false;
    }    
    /**
     * 是否有业务平台
     * @return
     */
    private boolean isHaveBusiDesk(int module_id,String username)
    {
    	return this.lock.isBmodule(module_id,username);    	
    }
}
