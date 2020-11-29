package com.hjsj.hrms.utils.components.defineformula.transaction;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.gz.TempvarBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * 项目名称 ：ehr7x
 * 类名称：CheckAllFormulaCond
 * 类描述：关闭弹出框之前校验所有计算公式
 * 创建人： lis
 * 创建时间：Jun 12, 2016
 */
public class CheckAllFormulaCond  extends IBusiness {
	@Override
	public void execute() throws GeneralException {
		String id = (String)this.getFormHM().get("id");//薪资类别id 或者人事异动模版id
		String module = (String)this.getFormHM().get("module");//模块号，1是薪资模块  2：薪资总额  3：人事异动  4...其他
		RowSet rs =null;
		String flag = "";
		try {
			ArrayList fieldlist = null;
			if("3".equals(module)){
				String groupId = (String)this.getFormHM().get("groupId");//薪资类别id 或者人事异动模版id
				groupId = groupId!=null&&groupId.length()>0?groupId:"";
				
				String stritem ="";
				ArrayList alUsedFields = new ArrayList();
				
				TemplateTableBo changebo = new TemplateTableBo(this.frameconn,Integer.parseInt(id),this.userView);
				TempvarBo tempvarbo = new TempvarBo();
				//临时变量
				ArrayList templist = tempvarbo.getMidVariableList(this.frameconn,id);
				for(int i=0;i<templist.size();i++){
					FieldItem fielditems = (FieldItem)templist.get(i);
					if(stritem.indexOf(fielditems.getItemid())!=-1)
						continue;
					stritem+=fielditems.getItemid()+",";
					alUsedFields.add(fielditems);
				}
				ArrayList varList=(ArrayList)alUsedFields.clone();
				
				//模板中的所有指标
				ArrayList itemlist = changebo.getAllFieldItem();
				HashMap map = changebo.getSub_domain_map();
				for(int i=0;i<itemlist.size();i++){
					FieldItem field = (FieldItem)itemlist.get(i);
					if(field==null)
						continue;
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
					fieldlist=alUsedFields;
				}
			
				StringBuffer sqlstr = new StringBuffer();
				
				sqlstr.append("select formula,cfactor,chz "); 
				sqlstr.append("from gzAdj_formula where ");
				sqlstr.append("tabid="+id);
				sqlstr.append(" and id="+groupId);
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				rs = dao.search(sqlstr.toString());
				String formula = null;
				if(rs.next()){
					formula=rs.getString("formula");//当前公式组所有的计算公式
					if(StringUtils.isNotBlank(formula)){
						String arr[] = formula.split("`");
						for(int i=0;i<arr.length;i++){
							String itemidA = arr[i].substring(0,arr[i].indexOf("="));
					        int k =itemidA.lastIndexOf("_");
				            if(k>0){
								String itemid=itemidA.substring(0,k);
								if(itemid.length()>1){
									String c_expr = arr[i].substring(arr[i].indexOf("=")+1,arr[i].length());
									if(c_expr.indexOf("统计表单子集") > -1) {
										break;
									}
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
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(rs);
		}
		if(flag.length()<1){
			flag = "ok";
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
