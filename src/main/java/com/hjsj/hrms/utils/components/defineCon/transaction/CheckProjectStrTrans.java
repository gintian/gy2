package com.hjsj.hrms.utils.components.defineCon.transaction;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.gz.TempvarBo;
import com.hjsj.hrms.interfaces.analyse.IParserConstant;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hjsj.hrms.utils.components.defineCon.businessobject.DefineConditionBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 * 项目名称：hcm7.x
 * 类名称：CheckProjectStrTrans 
 * 类描述：条件定义校验
 * 创建人：sunming
 * 创建时间：2015-7-21
 * @version
 */
public class CheckProjectStrTrans extends IBusiness {
	public void execute() throws GeneralException {
		DefineConditionBo bo=new DefineConditionBo(this.getFrameconn(),this.userView);
		//定义的条件
		String c_expr = (String) this.getFormHM().get("c_expr");
		c_expr=c_expr!=null&&c_expr.trim().length()>0?c_expr:"";
		c_expr=SafeCode.decode(c_expr);
		
		//主键
		String primarykey = (String) this.getFormHM().get("primarykey");
		primarykey=primarykey!=null&&primarykey.trim().length()>0?primarykey:"";
		
		//模块号
		String imodule = (String) this.getFormHM().get("imodule");
		String itemid = (String) this.getFormHM().get("itemid");
		itemid=itemid!=null&&itemid.trim().length()>0?itemid:"";
		
		if("0".equals(imodule)){//模块号为0时，薪资发放--批量修改校验的方法
			c_expr=PubFunc.keyWord_reback(c_expr);
			primarykey = PubFunc.decrypt(SafeCode.decode(primarykey));
			String clsflag =(String) this.getFormHM().get("clsflag");
			clsflag=clsflag!=null&&clsflag.trim().length()>0?clsflag:"";
			
			String fieldsetid = (String) this.getFormHM().get("fieldsetid");
			fieldsetid=fieldsetid!=null&&fieldsetid.trim().length()>0?fieldsetid:"";
			String itemtype = (String)this.getFormHM().get("itemtype");
			itemtype=itemtype!=null&&itemtype.trim().length()>0?itemtype:"";
			
			String itemsetid = (String)this.getFormHM().get("itemsetid");
			itemsetid=itemsetid!=null&&itemsetid.trim().length()>0?itemsetid:"";
			
			String flag = bo.checkProjectStr(c_expr, primarykey, itemid,
					clsflag, fieldsetid, itemtype, itemsetid);
			this.getFormHM().put("info",SafeCode.encode(flag));
		}
		/**
		 * gaohy
		 * 增加人事异动-批量修改-条件组件
		 */
		else if("2".equals(imodule)){
			if(itemid.indexOf("_")!=-1)
				itemid=itemid.substring(2);
			FieldItem fielditem = DataDictionary.getFieldItem(itemid);
			String type="";
			if(fielditem!=null){
				type = fielditem.getItemtype();
			}
			type=type!=null&&type.trim().length()>0?type:"L";
	
			String flag = "";
			if (c_expr != null && c_expr.length() > 0) {
				ArrayList alUsedFields = new ArrayList();
				TemplateTableBo changebo = new TemplateTableBo(this.frameconn,Integer.parseInt(primarykey),this.userView);
				ArrayList itemlist = changebo.getAllFieldItem();
				HashMap map = changebo.getSub_domain_map();
				boolean existB0110 = false;
				boolean existE0122 = false;
				boolean existE01A1 = false;
				boolean existA0101 = false;
				for(int i=0;i<itemlist.size();i++){
					FieldItem field = (FieldItem)itemlist.get(i);
					if(field.isChangeAfter()){
						field.setItemdesc(ResourceFactory.getProperty("inform.muster.to.be")+field.getItemdesc());
					}
					if("B0110".equalsIgnoreCase(field.getItemid())&&field.isChangeBefore())
						existB0110=true;
					if("E0122".equalsIgnoreCase(field.getItemid())&&field.isChangeBefore())
						existE0122=true;
					if("E01A1".equalsIgnoreCase(field.getItemid())&&field.isChangeBefore())
						existE01A1=true;
					if("A0101".equalsIgnoreCase(field.getItemid())&&field.isChangeBefore())
						existA0101=true;
					alUsedFields.add(field);
				}
				if(changebo.getInfor_type()==1&&changebo.getOperationtype()!=0)
				{
					FieldItem item = new FieldItem();
					if(!existB0110){
						item=DataDictionary.getFieldItem("B0110");
						alUsedFields.add(item);
					}
					if(!existE0122){
						item=DataDictionary.getFieldItem("E0122");
						alUsedFields.add(item);
					}
					if(!existE01A1){
						item=DataDictionary.getFieldItem("E01A1");
						alUsedFields.add(item);
					}
					if(!existA0101){
						item=DataDictionary.getFieldItem("A0101");
						alUsedFields.add(item);
					}
				}
				TempvarBo tempvarbo = new TempvarBo();
				alUsedFields.addAll(tempvarbo.getMidVariableList(this.frameconn,primarykey));
				
				// YksjParser.LOGIC// 此处需要调用者知道该公式的数据类型
				YksjParser yp = new YksjParser(getUserView(), alUsedFields, YksjParser.forSearch, IParserConstant.LOGIC
						, YksjParser.forPerson, "Ht", "");
	
				yp.setCon(this.getFrameconn());
				boolean b = yp.Verify_where(c_expr.trim());
				
				if (b) {// 校验通过
					flag="ok";
				}else{
					flag = yp.getStrError();
				} 
			}else{
				flag="ok";
			}
			this.getFormHM().put("info",SafeCode.encode(flag));
		}else if("3".equals(imodule)){
			String type="";
			type=type!=null&&type.trim().length()>0?type:"L";
			String itemsetid = (String)this.getFormHM().get("itemsetid");
			itemsetid=itemsetid!=null&&itemsetid.trim().length()>0?itemsetid:"";
			this.formHM.remove("itemsetid");
			
			String info = bo.checkCondition(c_expr, itemsetid, type);
			this.getFormHM().put("info",info);
		}else if("4".equals(imodule)){
			
		}
		
	}
}
