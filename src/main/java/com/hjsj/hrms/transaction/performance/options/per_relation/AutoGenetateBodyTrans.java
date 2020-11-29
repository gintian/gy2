package com.hjsj.hrms.transaction.performance.options.per_relation;

import com.hjsj.hrms.businessobject.performance.options.PerRelationBo;
import com.hjsj.hrms.businessobject.performance.options.RenderRelationBo;
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
public class AutoGenetateBodyTrans extends IBusiness
{
    public void execute() throws GeneralException
    {    	
    	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
    	String oper = (String) hm.get("oper");
    	hm.remove("oper");
    	HashMap map = new HashMap();
    
    	ArrayList list = (ArrayList)this.getFormHM().get("perObjects");	
    	PerRelationBo bo1 = new PerRelationBo(this.frameconn);
    	String fieldItem = bo1.getPS_SUPERIOR_value();
    	
    	PerRelationBo bo = new PerRelationBo(this.frameconn);	
		HashMap joinedObjs = bo.getJoinedObjs();
		
    	if(oper!=null && "individual".equalsIgnoreCase(oper))
    	{
    		String objectIds = (String) hm.get("objectIDs");//被选中的考核对象,可以是多个
    		String[] objs = objectIds.split("@");
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
    	    else if(oper!=null && "batch".equalsIgnoreCase(oper) && joinedObjs.get(object_id)!=null)//批量自动生成 过滤掉不允许调整的考核对象
    	    	continue;   	    
    	    
    	    String postid=(String)abean.get("postid");
    	    RenderRelationBo bo2 = new RenderRelationBo(this.getFrameconn(),object_id,postid,fieldItem);
    	    bo2.saveMainBody();
    	}

    }

}
