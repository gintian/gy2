package com.hjsj.hrms.transaction.gz.templateset.standard;

import com.hjsj.hrms.businessobject.gz.templateset.GzStandardItemBo;
import com.hjsj.hrms.businessobject.gz.templateset.GzStandardItemVo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class UpdateColumnsTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			GzStandardItemBo bo=new GzStandardItemBo(this.getFrameconn());
			String type=(String)hm.get("type");
			GzStandardItemVo gzStandardItemVo=(GzStandardItemVo)this.getFormHM().get("gzStandardItemVo");
			ArrayList selectItemList=new ArrayList();
			ArrayList parentItemList=new ArrayList();
			String parentItemId="";
			if("1".equals(type)|| "3".equals(type))
			{
				if("1".equals(type))
				{
					parentItemList=bo.getSelectItemList("0",gzStandardItemVo,"");
				}
				if("3".equals(type))
				{
					parentItemList=bo.getSelectItemList("2",gzStandardItemVo,"");
				}
				if(parentItemList.size()>0)
				{
					CommonData data=(CommonData)parentItemList.get(0);
					if(hm.get("parentItem")==null)
						parentItemId=data.getDataValue();
					else
					{
						parentItemId=(String)hm.get("parentItem");
						hm.remove("parentItem");
					}
				}
			}
			selectItemList=bo.getSelectItemList(type,gzStandardItemVo,parentItemId);	
				
//			 0: 增减横栏目  1：增减子横栏目   2: 增减纵栏目  3：增减子纵栏目
			String atype="";   //   5:代码树 6：指标熟
			String flag="3";   // 0:库 1：指标集 2：指标  3：代码  4：UN  5:UM  6@K  7:数字  8: 日期
			String id="";
			String desc="";
			String factor="";
			if("0".equals(type))
			{
				factor=gzStandardItemVo.getHfactor();
			}
			else if("1".equals(type))
			{
				factor=gzStandardItemVo.getS_hfactor();
			}
			else  if("2".equals(type))
			{
				factor=gzStandardItemVo.getVfactor();				
			}
			else  if("3".equals(type))
			{
				factor=gzStandardItemVo.getS_vfactor();		
			}
			FieldItem fieldItem=DataDictionary.getFieldItem(factor);
			if("A".equals(fieldItem.getItemtype())&&!"0".equals(fieldItem.getCodesetid()))
			{
				atype="5";
				if("UN".equals(fieldItem.getCodesetid()))
				{
					id="UN";
					flag="4";
					desc="单位代码";
				}
				else if("UM".equals(fieldItem.getCodesetid()))
				{
					id="UM";
					flag="5";
					desc="部门代码";
				}
				else if("@K".equals(fieldItem.getCodesetid()))
				{
					id="@K";
					flag="6";
					desc="职位代码";
				}
				else
				{
					id=fieldItem.getCodesetid();
					flag="3";
					ContentDAO dao=new ContentDAO(this.getFrameconn());
					this.frowset=dao.search("select * from codeset where codesetid='"+fieldItem.getCodesetid()+"'");
					if(this.frowset.next())
						desc=this.frowset.getString("codesetdesc");
				}
			}
			else
			{
				atype="6";
				id=factor;
				desc=fieldItem.getItemdesc();
			}
			this.getFormHM().put("type",atype);
			this.getFormHM().put("flag",flag);
			this.getFormHM().put("id",id);
			this.getFormHM().put("desc",desc);
			
			this.getFormHM().put("selectItemList",selectItemList);
			this.getFormHM().put("parentItemList",parentItemList);
			this.getFormHM().put("parentItemId",parentItemId);
			this.getFormHM().put("optType",type);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
