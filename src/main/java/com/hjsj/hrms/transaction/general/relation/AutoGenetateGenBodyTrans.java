package com.hjsj.hrms.transaction.general.relation;

import com.hjsj.hrms.businessobject.general.relation.GenRelationBo;
import com.hjsj.hrms.businessobject.general.relation.GenRenderRelationBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:AutoGenetateBodyTrans.java</p>
 * <p> Description:考核关系/自动生成考核主体</p>
 * <p>Company:hjsj</p>
 * <p> create time:2009-04-21 13:00:00</p> 
 * @author FanZhiGuo
 * @version 1.0 
 */
public class AutoGenetateGenBodyTrans extends IBusiness
{
    public void execute() throws GeneralException
    {    	
    	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
    	String oper = (String) hm.get("oper");
    	hm.remove("oper");
    	HashMap map = new HashMap();
    
    	ArrayList list = (ArrayList)this.getFormHM().get("genObjects");	
    	GenRelationBo bo1 = new GenRelationBo(this.frameconn);
    	String fieldItem = bo1.getPS_SUPERIOR_value();
    	String dbpre = (String)this.getFormHM().get("dbpre");
		String sp_grade = (String)this.getFormHM().get("sp_grade");
		String relationid = (String)this.getFormHM().get("relationid");
		String actor_type = (String)this.getFormHM().get("actor_type");
    	if(oper!=null && "individual".equalsIgnoreCase(oper))
    	{
    		String objectIds = (String) hm.get("objectIDs");//被选中的考核对象,可以是多个
    		objectIds = SafeCode.decode(objectIds);
    		String[] objs = objectIds.split("#");
    		for(int i=0;i<objs.length;i++)
        	{
        	    String object_id = (String)objs[i];
        	    if("".equals(object_id.trim()))
        	    	continue;
        	    map.put(object_id, "");
        	}	
    	}
    
    	for(int i=0;i<list.size();i++)
    	{
    	    LazyDynaBean abean = (LazyDynaBean)list.get(i);
    	    String object_id = (String)abean.get("object_id");
    	    
    	    if(oper!=null && "individual".equalsIgnoreCase(oper) && map.get(object_id)==null)//个别自动生成 只是对选中的考核对象自动生成考核主体
    	    	continue;
    	    	    
    	    
    	    String postid=(String)abean.get("postid");
    	    GenRenderRelationBo bo2 = new GenRenderRelationBo(this.getFrameconn(),object_id,postid,fieldItem,dbpre,relationid,this.userView,actor_type);
    	    bo2.saveMainBody(sp_grade);
    	}

    }

}
