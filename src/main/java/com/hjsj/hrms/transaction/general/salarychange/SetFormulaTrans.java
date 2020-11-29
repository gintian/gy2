package com.hjsj.hrms.transaction.general.salarychange;

import com.hjsj.hrms.businessobject.general.salarychange.ChangeFormulaBo;
import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.gz.TempvarBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class SetFormulaTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap reqhm = (HashMap)this.getFormHM().get("requestPamaHM");
		String tableid = (String)reqhm.get("tableid");
		reqhm.remove("tableid");
		tableid=tableid!=null&&tableid.trim().length()>0?tableid:"";
		
		String id = (String)reqhm.get("id");
		reqhm.remove("id");
		id=id!=null&&id.trim().length()>0?id:"";
		
		String flag = (String)reqhm.get("flag");
		reqhm.remove("flag");
		flag=flag!=null&&flag.trim().length()>0?flag:"";
		/**判断用户是否拥有该模版资源的权限**/
        boolean isCorrect=false;
        if(this.userView.isHaveResource(IResourceConstant.RSBD,tableid))//人事移动
            isCorrect=true;
        if(!isCorrect)
            if(this.userView.isHaveResource(IResourceConstant.ORG_BD,tableid))//组织变动
                isCorrect=true;
        if(!isCorrect)
            if(this.userView.isHaveResource(IResourceConstant.POS_BD,tableid))//岗位变动
                isCorrect=true;
        if(!isCorrect)
            if(this.userView.isHaveResource(IResourceConstant.GZBD,tableid))//工资变动
                isCorrect=true;
        if(!isCorrect)
            if(this.userView.isHaveResource(IResourceConstant.INS_BD,tableid))//保险变动
                isCorrect=true;
        if(!isCorrect)
            if(this.userView.isHaveResource(IResourceConstant.PSORGANS,tableid))
                isCorrect=true;
        if(!isCorrect)
            if(this.userView.isHaveResource(IResourceConstant.PSORGANS_FG,tableid))
                isCorrect=true;
        if(!isCorrect)
            if(this.userView.isHaveResource(IResourceConstant.PSORGANS_GX,tableid))
                isCorrect=true;
        if(!isCorrect)
            if(this.userView.isHaveResource(IResourceConstant.PSORGANS_JCG,tableid))
                isCorrect=true;
        if(!isCorrect){
            throw new GeneralException("当前用户不具有相应的权限");
        }
		ArrayList itemlist = new ArrayList();
		ArrayList affterlist = new ArrayList();
		String affteritem_arr = "";
		String stritem="";
		if(tableid.length()>0){
			TemplateTableBo changebo = new TemplateTableBo(this.frameconn,Integer.parseInt(tableid),this.userView);
			ArrayList list = changebo.getAllFieldItem();
			int infor_type =changebo.getInfor_type();
			HashMap map = changebo.getSub_domain_map();
			HashMap field_name_map = changebo.getField_name_map();
			for(int i=0;i<list.size();i++){
				
				
				FieldItem fielditem = (FieldItem)(((FieldItem)list.get(i)).cloneItem());
				String itemdesc = "";
				if(infor_type!=1&&("codesetid".equalsIgnoreCase(fielditem.getItemid())|| "codeitemdesc".equalsIgnoreCase(fielditem.getItemid())|| "corcode".equalsIgnoreCase(fielditem.getItemid())|| "parentid".equalsIgnoreCase(fielditem.getItemid())/*||fielditem.getItemid().equalsIgnoreCase("start_date")*/))
				{
					continue;
				}
				if ("start_date".equalsIgnoreCase(fielditem.getItemid())){
					//前台判断全部按下划线_，兼容,防止报错，保存的时候再替换回来。
					fielditem.setItemid("start*date");
				}
				if(fielditem.getVarible()==2){//去掉子集
						continue;
					}
			//	if(fielditem.isChangeAfter()&&!fielditem.isMemo()){
				if(fielditem.isChangeAfter()){  //计算公式支持大字段类型
					if(stritem.indexOf(fielditem.getItemid()+"_2,")!=-1)
						continue;
					if(changebo.getOpinion_field()!=null&&changebo.getOpinion_field().length()>0&&changebo.getOpinion_field().equalsIgnoreCase(fielditem.getItemid()))
						continue;
					stritem+=fielditem.getItemid()+"_2,";
					itemdesc=ResourceFactory.getProperty("inform.muster.to.be")+fielditem.getItemdesc();
					CommonData dataobj = new CommonData(fielditem.getItemid(),ResourceFactory.getProperty("inform.muster.to.be")+fielditem.getItemdesc());
					affterlist.add(dataobj);
					affteritem_arr+=fielditem.getItemid()+":"+ResourceFactory.getProperty("inform.muster.to.be")+fielditem.getItemdesc()+"`";
				}else if(fielditem.isChangeBefore()){
					//多个变化前加上_id
					String sub_domain_id="";
					if(map!=null&&map.get(""+i)!=null&&map.get(""+i).toString().trim().length()>0){
					
					sub_domain_id ="_"+(String)map.get(""+i);
					}
					if(stritem.indexOf(fielditem.getItemid()+sub_domain_id+"_1,")!=-1)
						continue;
					if(field_name_map!=null&&field_name_map.get(fielditem.getItemid()+sub_domain_id+"_1")!=null)
						continue;
					stritem+=fielditem.getItemid()+sub_domain_id+"_1,";
					if(sub_domain_id!=null&&sub_domain_id.length()>0){
					fielditem.setItemid(fielditem.getItemid()+"_"+map.get(""+i)+"_1 ");
					fielditem.setItemdesc(""+map.get(""+i+"hz"));
					}
				/*	if(fielditem.isMainSet()){
						itemdesc=fielditem.getItemdesc();
					}
					else */
					{
						itemdesc=ResourceFactory.getProperty("inform.muster.now")+fielditem.getItemdesc();
					}
				} else {
					if(stritem.indexOf(fielditem.getItemid())!=-1)
						continue;
					itemdesc=fielditem.getItemdesc();
					stritem+=fielditem.getItemid()+",";
				}
			//	if(!fielditem.getItemid().equalsIgnoreCase("photo")&&!fielditem.getItemid().equalsIgnoreCase("ext")&&!fielditem.isMemo())
				if(!"photo".equalsIgnoreCase(fielditem.getItemid())&&!"ext".equalsIgnoreCase(fielditem.getItemid())&&!"attachment".equalsIgnoreCase(fielditem.getItemid()))
				{
					CommonData dataobj = new CommonData(fielditem.getItemid()+":"+itemdesc,itemdesc);
					itemlist.add(dataobj);
				}
			}
		}
		TempvarBo tempvarbo = new TempvarBo();
		ArrayList templist = tempvarbo.getMidVariableList(this.frameconn,tableid);
		for(int i=0;i<templist.size();i++){
			FieldItem fielditem = (FieldItem)templist.get(i);
			if(stritem.indexOf(fielditem.getItemid())!=-1)
				continue;
			stritem+=fielditem.getItemid()+",";
			CommonData dataobj = new CommonData(fielditem.getItemid()+":"+fielditem.getItemdesc(),fielditem.getItemdesc());
			itemlist.add(dataobj);
		}
		
		CommonData dataobj = new CommonData(":","");
		itemlist.add(0,dataobj);
		String item = "";
		String item2 ="";
		ChangeFormulaBo formulabo = new ChangeFormulaBo();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		if("alert".equals(flag)){
			String[] itemFactor = formulabo.getItem(dao,tableid,id,affterlist);
			item = itemFactor[0];
			item2 = itemFactor[3];
			item= item.replace("START_DATE", "START*DATE");
			item2= item2.replace("START_DATE", "START*DATE");
			this.getFormHM().put("cfactor",SafeCode.encode(itemFactor[1]));
			this.getFormHM().put("id",id);
			this.getFormHM().put("chz",itemFactor[2]);
		}else{
			flag="save";
			this.getFormHM().put("cfactor","");
			this.getFormHM().put("id",id);
			this.getFormHM().put("chz","");
		}
		
		ArrayList affitemlist = formulabo.itemList(dao,item2,affterlist,tableid,id);
		this.getFormHM().put("flag",flag);
		this.getFormHM().put("itemid","");
		this.getFormHM().put("itemlist",itemlist);
		this.getFormHM().put("tableid",tableid);
		this.getFormHM().put("item",item);
		this.getFormHM().put("chz_arr",formulabo.itemStr(affitemlist));
		this.getFormHM().put("affteritem_arr",formulabo.selectStr(affteritem_arr));
		this.getFormHM().put("itemtable",formulabo.itemTable(item,affitemlist));
	}

}
