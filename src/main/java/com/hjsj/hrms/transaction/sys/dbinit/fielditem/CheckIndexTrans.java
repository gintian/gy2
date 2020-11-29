package com.hjsj.hrms.transaction.sys.dbinit.fielditem;

import com.hjsj.hrms.businessobject.sys.fieldsubset.IndexBo;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 
 * <p>Title:验证指标代码是否重名</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Aug 4, 2008:1:03:44 AM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class CheckIndexTrans extends IBusiness{

	public void execute() throws GeneralException {
		String msg = "1";
		try
		{
			String indexcode = (String)this.getFormHM().get("indexcode");
			String indexname = (String)this.getFormHM().get("indexname");
			IndexBo subset = new IndexBo(this.getFrameconn());
			String fieldsetid=(String)this.getFormHM().get("fieldsetid");
			fieldsetid=fieldsetid==null?"X":fieldsetid;
			boolean flag=true;
			if(checkSysFieldStr(fieldsetid,indexcode)){
				msg=ResourceFactory.getProperty("system.item.code")+indexcode+ResourceFactory.getProperty("system.check.msg");
				flag=false;
			}
			if(flag&&subset.checkcode(indexcode)){
				flag=false;
				msg=ResourceFactory.getProperty("kjg.error.clew"); 
			}
			if(flag&&subset.checkname(indexname)){
				flag=false;
				msg=ResourceFactory.getProperty("kjg.error.clew"); 
			}
			if(flag&&subset.checkName(fieldsetid,indexname)){
				flag=false;
				msg=ResourceFactory.getProperty("kjg.error.clew"); 
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			this.getFormHM().put("msg",msg);
		}
		
	}

	private boolean checkSysFieldStr(String fieldsetid,String itemid){
		String infor=fieldsetid.substring(0, 1);
		StringBuffer sysFieldStr=new StringBuffer();
		if("A".equalsIgnoreCase(infor)){
			if("A01".equalsIgnoreCase(fieldsetid)){//?	构建主集A01
				sysFieldStr.append(",A0000");
				sysFieldStr.append(",A0100");
				sysFieldStr.append(",B0110");
				sysFieldStr.append(",E0122");
				sysFieldStr.append(",E01A1");
				sysFieldStr.append(",State");
				sysFieldStr.append(",CreateTime");
				sysFieldStr.append(",ModTime");
				sysFieldStr.append(",ModTime1");
				sysFieldStr.append(",CreateUserName");
				sysFieldStr.append(",ModUserName");
				sysFieldStr.append(",UserName");
				sysFieldStr.append(",UserPassword");
				sysFieldStr.append(",Groups,");
				
			}else{//	?构建子集-（UsrA02—Axx）
				sysFieldStr.append(",B0110");
				sysFieldStr.append(",E0122");
				sysFieldStr.append(",E01A1");
				sysFieldStr.append(",A0100");
				sysFieldStr.append(",I9999");
				sysFieldStr.append(",State");
				sysFieldStr.append(",Id");
				sysFieldStr.append(",CreateTime");
				sysFieldStr.append(",ModTime");
				sysFieldStr.append(",CreateUserName");
				sysFieldStr.append(",ModUserName");
				sysFieldStr.append(",SealFlag");
				sysFieldStr.append(","+fieldsetid+"Z0");
				sysFieldStr.append(","+fieldsetid+"Z1,");
			}
		}else if("B".equalsIgnoreCase(infor)){//?	主集（B01）
			if("B01".equalsIgnoreCase(fieldsetid)){
				sysFieldStr.append(",B0110");
				sysFieldStr.append(",E0122");
				sysFieldStr.append(",E01A1");
				sysFieldStr.append(",State");
				sysFieldStr.append(",CreateTime");
				sysFieldStr.append(",ModTime");
				sysFieldStr.append(",ModTime1");
				sysFieldStr.append(",CreateUserName");
				sysFieldStr.append(",ModUserName,");
			}else{//?	子集(B02—Bxx)
				sysFieldStr.append(",B0110");
				sysFieldStr.append(",E0122");
				sysFieldStr.append(",E01A1");
				sysFieldStr.append(",I9999");
				sysFieldStr.append(",State");
				sysFieldStr.append(",Id");
				sysFieldStr.append(",CreateTime");
				sysFieldStr.append(",ModTime");
				sysFieldStr.append(",CreateUserName");
				sysFieldStr.append(",ModUserName");
				sysFieldStr.append(","+fieldsetid+"Z0");
				sysFieldStr.append(","+fieldsetid+"Z1,");
			}
		}else if("K".equalsIgnoreCase(infor)){
			if("K01".equalsIgnoreCase(fieldsetid)){//?	主集－K01
				sysFieldStr.append(",B0110");
				sysFieldStr.append(",E0122");
				sysFieldStr.append(",E01A1");
				sysFieldStr.append(",State");
				sysFieldStr.append(",CreateTime");
				sysFieldStr.append(",ModTime");
				sysFieldStr.append(",ModTime1");
				sysFieldStr.append(",CreateUserName");
				sysFieldStr.append(",ModUserName");
			}else{//?	子集-(K02—Kxx)
				sysFieldStr.append(",B0110");
				sysFieldStr.append(",E0122");
				sysFieldStr.append(",E01A1");
				sysFieldStr.append(",I9999");
				sysFieldStr.append(",State");
				sysFieldStr.append(",Id");
				sysFieldStr.append(",CreateTime");
				sysFieldStr.append(",ModTime");
				sysFieldStr.append(",CreateUserName");
				sysFieldStr.append(",ModUserName");
				sysFieldStr.append(","+fieldsetid+"Z0");
				sysFieldStr.append(","+fieldsetid+"Z1,");
			}
		}else if("H".equalsIgnoreCase(infor)){
			if("H01".equalsIgnoreCase(fieldsetid)){
				sysFieldStr.append(",B0110");
				sysFieldStr.append(",E0122");
				sysFieldStr.append(",E01A1");
				sysFieldStr.append(",H0100");
				sysFieldStr.append(",B0110");
				sysFieldStr.append(",State");
				sysFieldStr.append(",CreateTime");
				sysFieldStr.append(",ModTime");
				sysFieldStr.append(",CreateUserName");
				sysFieldStr.append(",ModUserName,");
			}else{
				sysFieldStr.append(",B0110");
				sysFieldStr.append(",E0122");
				sysFieldStr.append(",E01A1");
				sysFieldStr.append(",H0100");
				sysFieldStr.append(",I9999");
				sysFieldStr.append(",State");
				sysFieldStr.append(",CreateTime");
				sysFieldStr.append(",ModTime");
				sysFieldStr.append(",CreateUserName");
				sysFieldStr.append(",ModUserName");
				sysFieldStr.append(","+fieldsetid+"Z0");
				sysFieldStr.append(","+fieldsetid+"Z1,");
			}
		}else if("Y".equalsIgnoreCase(infor)){  //党组织
			if("Y01".equalsIgnoreCase(fieldsetid)){
				sysFieldStr.append(",B0110");
				sysFieldStr.append(",E0122");
				sysFieldStr.append(",E01A1");
				sysFieldStr.append(",Y0100");
				sysFieldStr.append(",State");
				sysFieldStr.append(",CreateTime");
				sysFieldStr.append(",ModTime");
				sysFieldStr.append(",CreateUserName");
				sysFieldStr.append(",ModUserName,");
			}else{
				sysFieldStr.append(",B0110");
				sysFieldStr.append(",E0122");
				sysFieldStr.append(",E01A1");
				sysFieldStr.append(",Y0100");
				sysFieldStr.append(",I9999");
				sysFieldStr.append(",State");
				sysFieldStr.append(",CreateTime");
				sysFieldStr.append(",ModTime");
				sysFieldStr.append(",CreateUserName");
				sysFieldStr.append(",ModUserName");
				sysFieldStr.append(","+fieldsetid+"Z0");
				sysFieldStr.append(","+fieldsetid+"Z1,");
			}
		}else if("V".equalsIgnoreCase(infor)){  //团组织
			if("V01".equalsIgnoreCase(fieldsetid)){
				sysFieldStr.append(",B0110");
				sysFieldStr.append(",E0122");
				sysFieldStr.append(",E01A1");
				sysFieldStr.append(",V0100");
				sysFieldStr.append(",State");
				sysFieldStr.append(",CreateTime");
				sysFieldStr.append(",ModTime");
				sysFieldStr.append(",CreateUserName");
				sysFieldStr.append(",ModUserName");
			}else{
				sysFieldStr.append(",B0110");
				sysFieldStr.append(",E0122");
				sysFieldStr.append(",E01A1");
				sysFieldStr.append(",V0100");
				sysFieldStr.append(",I9999");
				sysFieldStr.append(",State");
				sysFieldStr.append(",CreateTime");
				sysFieldStr.append(",ModTime");
				sysFieldStr.append(",CreateUserName");
				sysFieldStr.append(",ModUserName");
				sysFieldStr.append(","+fieldsetid+"Z0");
				sysFieldStr.append(","+fieldsetid+"Z1,");
			}
		}else if("W".equalsIgnoreCase(infor)){  //工会组织
			if("W01".equalsIgnoreCase(fieldsetid)){
				sysFieldStr.append(",B0110");
				sysFieldStr.append(",E0122");
				sysFieldStr.append(",E01A1");
				sysFieldStr.append(",W0100");
				sysFieldStr.append(",State");
				sysFieldStr.append(",CreateTime");
				sysFieldStr.append(",ModTime");
				sysFieldStr.append(",CreateUserName");
				sysFieldStr.append(",ModUserName");
			}else{
				sysFieldStr.append(",B0110");
				sysFieldStr.append(",E0122");
				sysFieldStr.append(",E01A1");
				sysFieldStr.append(",W0100");
				sysFieldStr.append(",I9999");
				sysFieldStr.append(",State");
				sysFieldStr.append(",CreateTime");
				sysFieldStr.append(",ModTime");
				sysFieldStr.append(",CreateUserName");
				sysFieldStr.append(",ModUserName");
				sysFieldStr.append(","+fieldsetid+"Z0");
				sysFieldStr.append(","+fieldsetid+"Z1,");
			}
		}
		
		return sysFieldStr.toString().toUpperCase().indexOf(itemid.toUpperCase())!=-1;
	}
}
