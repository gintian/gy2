/**
 * 
 */
package com.hjsj.hrms.transaction.general.query.quick;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * <p>Title:InitQuickQueryTrans</p>
 * <p>Description:快速查询参数初始化</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-4-26:13:24:54</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class InitQuickQueryTrans extends IBusiness {

    /**根据传过的的指标串，分解成对应的指标对象*/
    private ArrayList splitField(String strfields)
    {
        ArrayList list=new ArrayList();
        strfields=strfields+",";
        int pos=0;
        StringTokenizer st = new StringTokenizer(strfields, ",");
        while (st.hasMoreTokens())
        {
            /** for examples A01.A0405*/
            String fieldname=st.nextToken();
            pos=fieldname.indexOf(".");
            fieldname=fieldname.substring(pos+1);
            
            FieldItem item=DataDictionary.getFieldItem(fieldname);
            if(item==null){
                cat.debug("not find fielditem=["+fieldname+"]");
                continue;
            }
            list.add(item);
        }
        return list;
    }
    
	public void execute() throws GeneralException {
		String type=(String)this.getFormHM().get("type");
		String show_dbpre=(String)this.getFormHM().get("show_dbpre");
		cat.debug("type=>"+type);
        if(type==null|| "".equals(type))
        	type="1";
        RecordVo vo=null;
        if("2".equals(type))
          vo= ConstantParamter.getRealConstantVo("SS_BQUERYTEMPLATE");
        else if("3".equals(type))
            vo= ConstantParamter.getRealConstantVo("SS_KQUERYTEMPLATE");        	
        else
            vo= ConstantParamter.getRealConstantVo("SS_QUERYTEMPLATE");        	
        /**查询模板指标*/
        if(vo!=null)
        {
            String strfields=vo.getString("str_value");
            ArrayList fieldlist=splitField(strfields);
            this.getFormHM().put("fieldlist",fieldlist);            
        }      
        else
        	throw new GeneralException(ResourceFactory.getProperty("error.notquery.model"));
		/**权限范围内的人员库列表*/
		ArrayList list = getDbList(show_dbpre);
		this.getFormHM().put("dblist",list);
		
	}

	/**取得权限范围的人员库列表
	 * @return
	 * @throws GeneralException
	 */
	private ArrayList getDbList(String show_dbpre) throws GeneralException {
		ArrayList dblist=this.userView.getPrivDbList();
		DbNameBo dbvo=new DbNameBo(this.getFrameconn());
		dblist=dbvo.getDbNameVoList(dblist);
		ArrayList list=new ArrayList();
		for(int i=0;i<dblist.size();i++)
		{
			CommonData vo=new CommonData();
			RecordVo dbname=(RecordVo)dblist.get(i);
			if(show_dbpre.indexOf(dbname.getString("pre"))==-1)
				continue;
			vo.setDataName(dbname.getString("dbname"));
			vo.setDataValue(dbname.getString("pre"));
			list.add(vo);
		}
		return list;
	}
}
