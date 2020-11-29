/**
 * 
 */
package com.hjsj.hrms.transaction.sys.logonuser;

import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>Title:SearchOrgEmployTrans</p>
 * <p>Description:</p> 
 * <p>Company:hjsj</p> 
 * create time at:Jun 23, 200612:02:06 PM
 * @author chenmengqing
 * @version 4.0
 */
public class SearchCheckvalueTrans extends IBusiness {

	public void execute() throws GeneralException {
		String checkvalue="";
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String param=(String)hm.get("param");
		//系统管理，授权，业务范围，点开组织机构后的业务范围弹出机构树，后台报错 jingq upd 2014.10.20
		/*if(param!=null){
			try {
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}*/
		String params[]=param.split(",");
		if(params.length==3){
			String userflag=params[0];
			String role_id=params[1];
			param=params[2];
			RecordVo vo=null;
			ContentDAO dao = new ContentDAO(this.frameconn);
			if("0".equals(userflag)){
				vo=new RecordVo("operuser");
	    		vo.setString("username", role_id);
			}else{
				vo=new RecordVo("t_sys_function_priv");
	    		vo.setString("id",role_id);
	            vo.setString("status",userflag/*GeneralConstant.ROLE*/);
			}
			try {
				vo = dao.findByPrimaryKey(vo);
			} catch (Exception e) {
			}
			if(vo!=null){
				String busi_org_dept = vo.getString("busi_org_dept");
				if(busi_org_dept!=null&&busi_org_dept.length()>0){
					String str[] = busi_org_dept.split("\\|");
					for(int i=0;i<str.length;i++){//1,UNxxx`UM9191`
						String tmp = str[i];
						String ts[] = tmp.split(",");
						if(ts.length==2){
							/*StringBuffer sb = new StringBuffer();
							if(ts[1].length()>0){//UNxxx`UM9191`
								String tt[] = ts[1].split("`");
								for(int n=0;n<tt.length;n++){
									String ttt = tt[n];//UNxxx
									if(ttt.length()>2){
										String codesetid = ttt.substring(0, 2);
										String codeitemid = ttt.substring(2);
										sb.append(com.hrms.frame.utility.AdminCode.getCodeName(codesetid, codeitemid)+",");
									}else{
										continue;
									}
								}
							}*/
							if(param.equals(ts[0])){
								checkvalue=","+ts[1].replaceAll("`", ",")+",";
							}
						}else{
							continue;
						}
					}
				}
			}
			
		}
		/*}*/
		// bug 36668 获取机构树对应的title wangb 20180420
		CheckPrivSafeBo cpsBo = new CheckPrivSafeBo(this.frameconn, this.userView);
		if(checkvalue.trim().length() >0) //bug 37450 操作单位没有选择时，不过滤历史机构   wangb 20180514
			checkvalue =","+ cpsBo.orgValidCheck(checkvalue);//校验不是有效期内机构不显示  wangb 20180504
		String[] checkvalues = checkvalue.split(",");
		String checkTitle = "";
		for (int i = 0; i < checkvalues.length; i++) {
			if(checkvalues[i].trim().length()==0)
				continue;
			checkTitle +=AdminCode.getCodeName(checkvalues[i].substring(0, 2),checkvalues[i].substring(2)) + ",";
		}
		this.getFormHM().put("checktitle",checkTitle);
		this.getFormHM().put("checkvalue", checkvalue);
	}
	
}
