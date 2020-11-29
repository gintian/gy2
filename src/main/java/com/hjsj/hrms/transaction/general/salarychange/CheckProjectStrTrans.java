package com.hjsj.hrms.transaction.general.salarychange;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.gz.TempvarBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class CheckProjectStrTrans extends IBusiness {

	public void execute() throws GeneralException {
		String check = (String) this.getFormHM().get("check");
		check=check!=null&&check.trim().length()>0?check:"";
		
		String item = (String) this.getFormHM().get("item");
		item=item!=null&&item.trim().length()>0?item:"";
		item=SafeCode.decode(item);
		item=item.replace("START*DATE", "START_DATE");
		item=item.replace("start*date", "start_date");
		String tableid = (String) this.getFormHM().get("tableid");
		tableid=tableid!=null&&tableid.trim().length()>0?tableid:"";
		TemplateTableBo changebo = new TemplateTableBo(this.frameconn,Integer.parseInt(tableid),this.userView);
		String stritem ="";
		String arr[] = item.split("`");
		String flag = "";
		ArrayList alUsedFields = new ArrayList();
		//想不明白为什么要加上单位名称指标，计算时又没兼容   2013-10-27   邓灿
		/*
		FieldItem items = DataDictionary.getFieldItem("b0110");
		if(items!=null){
			alUsedFields.add(items);
			stritem = "b0110,";
		} 
		*/
		TempvarBo tempvarbo = new TempvarBo();
		ArrayList templist = tempvarbo.getMidVariableList(this.frameconn,tableid);
		for(int i=0;i<templist.size();i++){
			FieldItem fielditem = (FieldItem)templist.get(i);
			if(stritem.indexOf(fielditem.getItemid())!=-1)
				continue;
			stritem+=fielditem.getItemid()+",";
			alUsedFields.add(fielditem);
		}
		ArrayList varList=(ArrayList)alUsedFields.clone();
		
		ArrayList itemlist = changebo.getAllFieldItem();
		HashMap map = changebo.getSub_domain_map();
		for(int i=0;i<itemlist.size();i++){
			FieldItem field = (FieldItem)itemlist.get(i);
			if(field==null)
				continue;
		//	if(stritem.indexOf(field.getItemid())!=-1)
		//		continue;
			if(field.isChangeAfter()&&!field.isMemo()){
				if(stritem.indexOf(field.getItemid()+"_2,")!=-1)
							continue;

				field.setItemid(field.getItemid()+"_2");
				field.setItemdesc(ResourceFactory.getProperty("inform.muster.to.be")+field.getItemdesc());
				stritem+=field.getItemid()+"_2,";
			
			}
			else if(field.isChangeBefore()&&!field.isMemo()){
				//多个变化前加上_id
				String sub_domain_id="";
				String itemdesc = field.getItemdesc();
				if(map!=null&&map.get(""+i)!=null&&map.get(""+i).toString().trim().length()>0){
				sub_domain_id ="_"+(String)map.get(""+i);
				itemdesc = (String)map.get(""+i+"hz");
				}
				if(stritem.indexOf(field.getItemid()+sub_domain_id+"_1,")!=-1)
					continue;
				field.setItemdesc(itemdesc);
				if(!field.isMainSet()){
					field.setItemdesc(ResourceFactory.getProperty("inform.muster.now")+field.getItemdesc());
				}
			//	else
			//		field.setItemdesc(ResourceFactory.getProperty("inform.muster.to.be")+field.getItemdesc());
				stritem+=field.getItemid()+sub_domain_id+"_1,";
				field.setItemid(field.getItemid()+sub_domain_id+"_1");
				
			}
			else
			{
				if(stritem.indexOf(field.getItemid()+",")!=-1)
					continue;
				stritem+=field.getItemid()+",";
			
			}
			
			alUsedFields.add(field);
		}
		for(int i=0;i<arr.length;i++){
			String id = arr[i].substring(0,arr[i].indexOf("="));
	        int k =id.lastIndexOf("_");
            if(k>0){
				String itemid=id.substring(0,k);
				if(itemid.length()>1){
					String c_expr = arr[i].substring(arr[i].indexOf("=")+1,arr[i].length());
					if(c_expr.length()<1){
						flag = ResourceFactory.getProperty("inform.muster.cond.setformula.save")+"!";
						break;
					}
					FieldItem fielditem = DataDictionary.getFieldItem(itemid);
					String type="";
					if(fielditem!=null){
						type = fielditem.getItemtype();
					}else{
						if("codesetid".equalsIgnoreCase(itemid)|| "codeitemdesc".equalsIgnoreCase(itemid)|| "corcode".equalsIgnoreCase(itemid)|| "parentid".equalsIgnoreCase(itemid)|| "start_date".equalsIgnoreCase(itemid))
						{	//组织机构公式检查针对特殊字段的处理 xieguiquan 20110115
							if(!"start_date".equalsIgnoreCase(itemid)){
							type="A";
							}else{
								type="D";
							}
						}
					}
					type=type!=null&&type.trim().length()>0?type:"L";
					if (c_expr != null && c_expr.length() > 0) {
						// YksjParser.LOGIC// 此处需要调用者知道该公式的数据类型,forSearch->改成forNormal cmq for 代码转名称2
						YksjParser yp = new YksjParser(getUserView(), alUsedFields, YksjParser.forNormal, getColumType(type)
								, YksjParser.forPerson, "Ht", "");
						yp.setVarList(varList); //20141125 dengcan 
						yp.setSupportVar(true);
						
						//System.sout.println("ok1...");
						yp.setCon(this.getFrameconn());
						boolean b = yp.Verify_where(c_expr.trim());			
						if (!b){ //验证没有通过
							flag += yp.getStrError()+"\n";
							if(flag==null||flag.trim().length()==0)
							{
								flag="此处有未知字符串!";
							}
							break;
						} 
					}
				}
			}
		}
		if(flag.length()<1){
			flag = "ok";
		}
		String checks = "check";
		if("save".equalsIgnoreCase(check)){
			checks="save";
		}else{
			checks=check;
		}
		this.getFormHM().put("check",checks);
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
			temp=YksjParser.STRVALUE;
		}else if("D".equals(type)){
			temp=YksjParser.DATEVALUE;
		}else if("N".equals(type)){
			temp=YksjParser.FLOAT;
		}else if("L".equals(type)){
			temp=YksjParser.LOGIC;
		}else{
			temp=YksjParser.STRVALUE;
		}
		return temp;
	}
}
