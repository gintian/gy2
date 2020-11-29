package com.hjsj.hrms.transaction.org.autostatic.mainp;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.gz.TempvarBo;
import com.hjsj.hrms.interfaces.analyse.IParserConstant;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class CheckProjectStrTrans extends IBusiness {

	public void execute() throws GeneralException {
		String c_expr = (String) this.getFormHM().get("c_expr");
		c_expr=c_expr!=null&&c_expr.trim().length()>0?c_expr:"";
		
		String itemid = (String) this.getFormHM().get("itemid");
		itemid=itemid!=null&&itemid.trim().length()>0?itemid:"";
		
		String checkflag = (String) this.getFormHM().get("checkflag");
		checkflag=checkflag!=null&&checkflag.trim().length()>0?checkflag:"1";
		
		FieldItem fielditem = DataDictionary.getFieldItem(itemid);
		String type="";
		if(fielditem!=null){
			type = fielditem.getItemtype();
		}
		type=type!=null&&type.trim().length()>0?type:"L";
		c_expr=SafeCode.decode(c_expr);
		
		//System.out.println(c_expr);
		
		String flag = "";
		if (c_expr != null && c_expr.length() > 0) {
			ArrayList alUsedFields = new ArrayList();
			if("2".equals(checkflag)){
				String tableid = (String)this.getFormHM().get("tabid");
				tableid=tableid!=null&&tableid.trim().length()>0?tableid:"";
				TemplateTableBo changebo = new TemplateTableBo(this.frameconn,Integer.parseInt(tableid),this.userView);
				ArrayList itemlist = changebo.getAllFieldItem();
				HashMap map = changebo.getSub_domain_map();
				for(int i=0;i<itemlist.size();i++){
					FieldItem field = (FieldItem)itemlist.get(i);
					if(field.isChangeAfter()){
						field.setItemdesc(ResourceFactory.getProperty("inform.muster.to.be")+field.getItemdesc());
					}
//					else if(field.isChangeBefore()){
//						//多个变化前加上_id
//						String sub_domain_id="";
//						String itemdesc = field.getItemdesc();
//						if(map!=null&&map.get(""+i)!=null&&map.get(""+i).toString().trim().length()>0){
//						sub_domain_id ="_"+(String)map.get(""+i);
//						itemdesc = (String)map.get(""+i+"hz");
//						}
//						field.setItemdesc(itemdesc);
//						if(!field.isMainSet()){
//							field.setItemdesc(ResourceFactory.getProperty("inform.muster.now")+field.getItemdesc());
//						}
//						
//					}
					alUsedFields.add(field);
				}
				TempvarBo tempvarbo = new TempvarBo();
				alUsedFields.addAll(tempvarbo.getMidVariableList(this.frameconn,tableid));
			}else{
				if(fielditem!=null&& "r45".equalsIgnoreCase(fielditem.getFieldsetid())){
					alUsedFields = DataDictionary.getFieldList("r45",Constant.ALL_FIELD_SET);
				}else{
					alUsedFields = DataDictionary.getAllFieldItemList(
							Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);	
				}
			}
			//System.out.println("ok...");
			
			// YksjParser.LOGIC// 此处需要调用者知道该公式的数据类型
			YksjParser yp = new YksjParser(getUserView(), alUsedFields, YksjParser.forSearch, getColumType(type)
					, YksjParser.forPerson, "Ht", "");
			
			//System.out.println("ok1...");
			
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
	}
	/**
	 * 设置Field的数据类型
	 * @param type  数据类型
	 * @param decimalwidth 小数点后面值的宽度
	 * @return int 
	 **/
	public int getColumType(String type){
		int temp=1;
		if("A".equals(type)){
			temp=IParserConstant.STRVALUE;
		}else if("D".equals(type)){
			temp=IParserConstant.DATEVALUE;
		}else if("N".equals(type)){
			temp=IParserConstant.FLOAT;
		}else if("L".equals(type)){
			temp=IParserConstant.LOGIC;
		}else{
			temp=IParserConstant.STRVALUE;
		}
		return temp;
	}
}
