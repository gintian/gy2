package com.hjsj.hrms.transaction.param;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 保存电话邮箱配置
 * @author Owner
 *
 */
public class SavePhoneParamTrans extends IBusiness {
	public void execute() throws GeneralException 
	{
		String email=(String)this.getFormHM().get("email");
		String phone=(String)this.getFormHM().get("phone");
		//系统管理，电话邮箱参数还原   jingq add 2014.09.22
		email = PubFunc.keyWord_reback(email);
		phone = PubFunc.keyWord_reback(phone);
		String telephone=(String)this.getFormHM().get("telephone");
		ExecuteSQL exesql=new ExecuteSQL();
		try{
			/**邮箱***/
		String sql="delete from constant where constant='SS_EMAIL'";
		exesql.execUpdate(sql,this.getFrameconn());
		sql="insert into constant(constant,type,str_value,describe) values('SS_EMAIL','','" + email + "','Email指标')";
		exesql.execUpdate(sql,this.getFrameconn());
		RecordVo vo=new RecordVo("constant");
		vo.setString("constant","SS_EMAIL");
	    vo.setString("str_value",email);	    
	    ConstantParamter.putConstantVo(vo,"SS_EMAIL");
	    /***电话**/
		sql="delete from constant where constant='SS_MOBILE_PHONE'";
		exesql.execUpdate(sql,this.getFrameconn());
		sql="insert into constant(constant,type,str_value,describe) values('SS_MOBILE_PHONE','','" + phone + "','SS_MOBILE_PHONE指标')";
		exesql.execUpdate(sql,this.getFrameconn());
		vo=new RecordVo("constant");
		vo.setString("constant","SS_MOBILE_PHONE");
	    vo.setString("str_value",phone);
	    ConstantParamter.putConstantVo(vo,"SS_MOBILE_PHONE");
	    
	    /***固定电话**/
		sql="delete from constant where constant='SS_TELEPHONE'";
		exesql.execUpdate(sql,this.getFrameconn());
		sql="insert into constant(constant,type,str_value,describe) values('SS_TELEPHONE','','" + telephone + "','SS_TELEPHONE指标')";
		exesql.execUpdate(sql,this.getFrameconn());
		vo=new RecordVo("constant");
		vo.setString("constant","SS_TELEPHONE");
	    vo.setString("str_value",telephone);
	    ConstantParamter.putConstantVo(vo,"SS_TELEPHONE");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
