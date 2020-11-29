package com.hjsj.hrms.transaction.hire.employNetPortal;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchPosDescTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String z0301=(String)hm.get("z0301");
		z0301=SafeCode.decode(z0301);
		//zxj 20150126  防范反射型ＸＳＳ注入
		z0301 = PubFunc.hireKeyWord_filter(z0301);
		
		String posID=(String)hm.get("posID");//这个得到的是岗位,那么对于猎头招聘,应该怎么处理呢？  
		posID=SafeCode.decode(posID);
		//zxj 20150126  防范反射型ＸＳＳ注入
		posID = PubFunc.hireKeyWord_filter(posID);
		
		String a0100=(String)this.getFormHM().get("a0100");
		String hireChannel = (String) this.getFormHM().get("hireChannel");
		posID=PubFunc.getReplaceStr(posID);
		z0301=PubFunc.getReplaceStr(z0301);
		a0100=PubFunc.getReplaceStr(a0100);
		String major="";
		String login2=(String)hm.get("login2");
		String username="";
		if(login2!=null&&login2.length()!=0){
			a0100=this.userView.getA0100();
//			a0100=(String)hm.get("a0100");
			username=(String)hm.get("username");
			username=SafeCode.decode(username);
			hm.remove("a0100");
			hm.remove("login2");
		}
		if(hm.get("major")!=null)
		{
			major=(String)hm.get("major");
			hm.remove("major");
		}
		//a0100="00000001";
		//this.getFormHM().put("a0100",a0100);
		
		EmployNetPortalBo employNetPortalBo=new EmployNetPortalBo(this.getFrameconn());
		HashMap fieldMap=new HashMap();
		ArrayList list=DataDictionary.getFieldList("Z03",Constant.USED_FIELD_SET);
		for(int i=0;i<list.size();i++)
		{
			FieldItem item=(FieldItem)list.get(i);
			fieldMap.put(item.getItemid().toLowerCase(),item.getItemdesc()+"^"+item.getItemtype()+"^"+item.getCodesetid());
		}
		ArrayList posDescFiledList=employNetPortalBo.getPosDescFiledList(z0301,fieldMap); //职位详细信息 指标列表
		
		ArrayList applyedPosList=new ArrayList();   //已申请的职位信息列表
		//如果为登陆用户，则得到其已申请职位的信息列表
		if(a0100!=null&&a0100.trim().length()>0&&!"headHire".equals(hireChannel))
		{
			applyedPosList=employNetPortalBo.getApplyedPosList(a0100.trim());
		}
		
		
		String isApplyedPos="0";
		ParameterXMLBo xmlBo=new ParameterXMLBo(this.getFrameconn(),"1");
		HashMap map=xmlBo.getAttributeValues();

		if(a0100!=null&&a0100.length()>0&&applyedPosList.size()>0){
			
			isApplyedPos=employNetPortalBo.getIsApplyed(z0301,applyedPosList);
		}
		
		employNetPortalBo.addStatInfo(1,z0301);
		String isHaveExp="0";
		String isConfigExp="0";
		Object object = hm.get("from");
		String from = object==null?"":(String)object;
		hm.remove("from");
        if(hm.get("hireChannel")!=null)
        {
            hireChannel=(String)hm.get("hireChannel");
            hm.remove("hireChannel");
        }
        hireChannel = PubFunc.hireKeyWord_filter(hireChannel);
        String unitLevel = "";
        if(map != null && map.get("unitLevel") != null)
        	unitLevel = (String) map.get("unitLevel");
        ArrayList unitList = employNetPortalBo.getAllZpUnitList(hireChannel, unitLevel);//存放的是左侧组织机构树
        String zpUnit ="";
        String zpUnitCode="";
    	if("".equals(zpUnit)&&unitList!=null&&unitList.size()>0)
    	{
    		zpUnitCode=(String)((LazyDynaBean)unitList.get(0)).get("codeitemid");
    	}
    	String appliedPosItems = "";//外网已申请职位列表显示指标集
		if(map.get("appliedPosItems")!=null)
			appliedPosItems = (String)map.get("appliedPosItems");
		String acountBeActived = "0";//注册帐号需通过邮箱激活才生效
		 if(map.get("acountBeActived")!=null&&((String)map.get("acountBeActived")).length()>0)
             acountBeActived=(String)map.get("acountBeActived");
         String failedTime="3";//最大登录失败次数
         if(map.get("failedTime")!=null&&((String)map.get("failedTime")).length()>0)
             failedTime=(String)map.get("failedTime");
         String unlockTime="60";//解锁时间间隔
         if(map.get("unlockTime")!=null&&((String)map.get("unlockTime")).length()>0)
             unlockTime=(String)map.get("unlockTime");
		this.getFormHM().put("appliedPosItems", appliedPosItems);
		this.getFormHM().put("acountBeActived", acountBeActived);
        this.getFormHM().put("failedTime", failedTime);
        this.getFormHM().put("unlockTime", unlockTime);
        //北理工外网集成
        if("thirdparty".equals(from)){
        	//快速查询指标
        	ArrayList conditionFieldList = employNetPortalBo.getPosQueryConditionList(hireChannel,"pos_query");
        	this.getFormHM().put("conditionFieldList",conditionFieldList);
        }
        this.getFormHM().put("zpUnitCode", zpUnitCode);
        this.getFormHM().put("unitList", unitList);
        this.getFormHM().put("channelName", employNetPortalBo.getChannelName(hireChannel));
		this.getFormHM().put("isHaveExp", isHaveExp);
		this.getFormHM().put("isConfigExp", isConfigExp);
		this.getFormHM().put("isApplyedPos",isApplyedPos);//是否应聘了当前职位
		this.getFormHM().put("posID",this.getPosid(posID, z0301));//用途是做什么？
		this.getFormHM().put("applyedPosList",applyedPosList);
		this.getFormHM().put("posDescFiledList",posDescFiledList);//职位详细信息 指标列表
		this.getFormHM().put("requireId", z0301);//请求查看的用工需求号
		String max_count="";
		if(map.get("max_count")!=null)
			max_count = (String)map.get("max_count");//岗位最大申请数
		this.getFormHM().put("max_count", max_count);
		String hireMajor="-1";
		if(map.get("hireMajor")!=null&&!"".equals((String)map.get("hireMajor")))
			hireMajor=(String)map.get("hireMajor");
		this.getFormHM().put("hireMajor", hireMajor);
		if(login2!=null&&login2.length()!=0){
			this.getFormHM().put("a0100", a0100);
			this.getFormHM().put("userName", username);
		}
		String posDesc=this.getPosDesc(z0301);
		this.getFormHM().put("posDesc", posDesc);
		//是否显示 忘记帐号 
        String accountFlag = employNetPortalBo.checkAccount();
        this.getFormHM().put("accountFlag", accountFlag);
	}
	public String getPosDesc(String z0301)
	{
		String ss="";
		try
		{
			 Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
			 String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
			 if(display_e0122==null|| "00".equals(display_e0122)|| "".equals(display_e0122))
				 display_e0122="0";
			ContentDAO dao  = new ContentDAO(this.getFrameconn());
			StringBuffer sql = new StringBuffer("");
			sql.append("select z0321,z0325,z0351");
			sql.append(" from z03 where 1=1 ");
			sql.append(" and z03.z0301=?");
			ArrayList<String> value = new ArrayList<String>();
			value.add(z0301);
			this.frowset=dao.search(sql.toString(),value);
			while(this.frowset.next()){
				String z0325=this.frowset.getString("z0325");
				if(z0325==null|| "".equals(z0325)|| "0".equals(display_e0122))
	    				ss=this.frowset.getString("z0351");
	     		else{
	     				CodeItem item=AdminCode.getCode("UM",z0325,Integer.parseInt(display_e0122));
	    				if(item!=null){
	    					ss=item.getCodename()+"/"+this.frowset.getString("z0351");
	     				}else{
	     					ss=this.frowset.getString("z0351");
		    			}
		    		}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return ss;
	}
	public String getPosid(String posid,String z0301)
	{
		String ss="";
		FieldItem z0311 = DataDictionary.getFieldItem("z0311","Z03");//判断z0311 是否构库
		if(z0311!=null&&"1".equals(z0311.getUseflag())){
			try
			{
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				String sql = "select z0311 from z03 where z0301=? and z0311=?";
				ArrayList<String> value = new ArrayList<String>();
				value.add(z0301);
				value.add(posid);
				this.frowset=dao.search(sql,value);
				while(this.frowset.next())
				{
					ss=this.frowset.getString("z0311");
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return ss;
	}

}
