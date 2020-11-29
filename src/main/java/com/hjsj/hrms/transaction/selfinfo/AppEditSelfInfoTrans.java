
package com.hjsj.hrms.transaction.selfinfo;

import com.hjsj.hrms.businessobject.structuresql.MyselfDataApprove;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.Connection;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * Title: AppEditSelfInfoTrans
 * </p>
 * <p>
 * Description: AppEditSelfInfoTrans类处理个人申请修改的业务
 * </P>
 * 
 * @author wangzhongjun
 * @version 1.0 create time:2009-12-23
 */
public class AppEditSelfInfoTrans extends IBusiness {
	
	/**
	 * 查询t_hr_mydata_chg表中的已批的所有数据信息
	 */
	public void execute() throws GeneralException{
	    try {
    		MyselfDataApprove self = new MyselfDataApprove(this.frameconn, this.userView);
    		Map map = (Map) this.getFormHM().get("requestPamaHM");
    		String prove = (String) map.get("b_search");
    		String setname = (String) map.get("setname");
    		String flag = (String)map.get("flag");
    		String userbase = (String) this.getFormHM().get("userbase");
    		String a0100 = (String) this.getFormHM().get("a0100");
    		if(!"notself".equals(flag)){
    			userbase = this.userView.getDbname();
    			a0100 = this.userView.getA0100();
    		}else{
    			CheckPrivSafeBo cps = new CheckPrivSafeBo(this.frameconn, this.userView);
    			userbase = cps.checkDb(userbase);
    			a0100 = cps.checkA0100("",userbase , a0100, "");
    		}
    			
    		
    		//报批
    		int proveRecNum = 0;
    		if (prove != null && prove.length() > 0 && "prove".equals(prove)) {
    			List list = (List) this.getFormHM().get("pageselectedlist");
    			for (int i = 0; i < list.size(); i++) {
    				RecordVo vo = (RecordVo) list.get(i);
    				String state = vo.getString("state");
    				if ("new".equals(state)|| "update".equals(state)|| "insert".equals(state)|| "delete".equals(state)) {
    					
    					String chg_id= vo.getString("id");
    					String keyid = vo.getString("a0100");
    					String sequenceid = String.valueOf(vo.getInt("i9999")) ;
    					FieldSet fieldset = DataDictionary.getFieldSetVo(setname);
    					self.updateApployMyselfDataApp(chg_id,fieldset,"02",keyid,state,sequenceid); 
    					
    					proveRecNum++;
    				}
    			}
    			
    			//zxj 20160505 未选中有效数据时进行提示
    			if (proveRecNum == 0) {
    			    throw new GeneralException("", "请选择新增、更新、删除或插入状态的数据进行报批！", "", "");
    			}
    		}
    		
    		//撤销删除
    		if (prove != null && prove.length() > 0 && "backdel".equals(prove)){
    			self.backdelBySetname(userbase, a0100, setname);
    		}
	    } catch (Exception e) {
	        throw GeneralExceptionHandler.Handle(e);
	    }
	}
	
	/**
	 * 获得“单位/部门/职位/姓名”形式字符窜
	 * @param userbase
	 * @param A0100
	 * @param dao
	 */
	private String setOrgInfo(String userbase,String A0100,Connection connection)
	   {
			ContentDAO dao = new ContentDAO(connection);
			StringBuffer strsql=new StringBuffer();
			StringBuffer name=new StringBuffer();
			String b0110="";
			String e0122="";
			String e01a1="";
			String a0101="";
			try{
			    strsql.append("select b0110,e0122,e01a1,a0101 from ");
			    strsql.append(userbase);
			    strsql.append("A01 where a0100='");
			    strsql.append(A0100);
			    strsql.append("'");
			    this.frowset = dao.search(strsql.toString()); 
			    if(this.frowset.next())
				{
				     b0110=this.getFrowset().getString("B0110");
				     e0122=this.getFrowset().getString("E0122");
				     e01a1=this.getFrowset().getString("E01A1");
				     a0101=this.getFrowset().getString("a0101");			
				 }
			}catch(Exception e){
				
			}
			finally
			{
				if(b0110 !=null && b0110.trim().length()>0)
					 b0110=AdminCode.getCode("UN",b0110)!=null?AdminCode.getCode("UN",b0110).getCodename():"";
				if(e0122 !=null && e0122.trim().length()>0)
					e0122=AdminCode.getCode("UM",e0122)!=null?AdminCode.getCode("UM",e0122).getCodename():"";
				if(e01a1 !=null && e01a1.trim().length()>0)
					e01a1=AdminCode.getCode("@K",e01a1)!=null?AdminCode.getCode("@K",e01a1).getCodename():"";
			}
			
			if (b0110 == null) {
				name.append("");
			} else {
				name.append(b0110);
			}		
			name.append("/");
			if (e0122 == null) {
				name.append("");
			} else {
				name.append(e0122);
			}
			name.append("/");
			if (e01a1 == null) {
				name.append("");
			} else {
				name.append(e01a1);
			}
			name.append("/");
			if (a0101 == null) {
				name.append("");
			} else {
				name.append(a0101);
			}
			
			return name.toString();
	   }
	
	
}
