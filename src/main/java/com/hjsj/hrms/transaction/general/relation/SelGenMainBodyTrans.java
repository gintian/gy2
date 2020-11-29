package com.hjsj.hrms.transaction.general.relation;

import com.hjsj.hrms.businessobject.general.relation.GenRelationBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * Title:DelKhMainBodyTrans.java
 * </p>
 * <p>
 * Description:考核实施/指定考核主体/条件（手工）选择考核主体
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2008-06-01 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class SelGenMainBodyTrans extends IBusiness
{
    public void execute() throws GeneralException
    {

	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
	String mainBodyType = (String) hm.get("code");
	String db=(String)hm.get("db");
	hm.remove("db");
	String object = (String) this.getFormHM().get("khObject");
	GenRelationBo bo = new GenRelationBo(this.frameconn,this.userView);
	String relation_id =(String) this.getFormHM().get("relationid");
	String str_sql = (String) this.getFormHM().get("paramStr");//这个得到的结果已经包含了用户范围的控制
	if(db!=null&&!"".equals(db)){//条件选人，传递的是加密的sql
		str_sql = PubFunc.decrypt(SafeCode.decode(str_sql));
	}else{
		String str_sqlArray[]=str_sql.split("`");
		str_sql="";
		if(str_sqlArray!=null){
			for(int i=0;i<str_sqlArray.length;i++){
				//right_fields+= ",'"+objList[i]+"'";	
				str_sql+=",'"+str_sqlArray[i]+"'";
			}
			if(str_sql.trim().length()>0){
				str_sql=str_sql.substring(1);
			}
		}
	}
	 //sql=SafeCode.encode(PubFunc.encrypt(sql));//加密
	String dbpre = (String)this.getFormHM().get("dbpre");
	String actor_type = (String)this.getFormHM().get("actor_type");
	if(actor_type!=null&& "4".equals(actor_type)){
		String content = (String)hm.get("content");
		content = SafeCode.decode(content);
		if(content.endsWith(",")){
			String temp[] = content.split(",");
			String str ="";
			for(int j=0;j<temp.length;j++){
				if(temp[j]!=null&&temp[j].length()>0)
				str+="'"+temp[j]+"',";
			}
			if(str.length()>0)
			str_sql = " select username from operuser where username in("+str.substring(0,str.length()-1)+") ";
		}
	}
	String select_copy="";
	if ("all".equals(object)) // 将考核主体应用于所有考核对象
	{
	    ArrayList objectList = (ArrayList) this.getFormHM().get("khObjectList");
	    for (int i = 0; i < objectList.size(); i++)
	    {
		CommonData vo = (CommonData) objectList.get(i);
		object = vo.getDataValue();
		select_copy = bo.selMainBody(str_sql,  mainBodyType, object,relation_id,dbpre,actor_type,db);
	    }
	} else
		select_copy = bo.selMainBody(str_sql,  mainBodyType, object,relation_id,dbpre,actor_type,db);
	//if(select_copy!=null&&select_copy.trim().length()>0)
	if(select_copy==null||select_copy.trim().length()<=0){
	    select_copy="";
	}
	this.getFormHM().put("select_copy", select_copy);
    }
}
