package com.hjsj.hrms.module.template.signature.transaction;

import com.hjsj.hrms.module.template.signature.businessobject.SignatureBo;
import com.hjsj.hrms.module.template.utils.javabean.TemplateModuleParam;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchSignatureListTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		try {
			String flag = (String) this.getFormHM().get("flag");
			SignatureBo bo = new SignatureBo(this.frameconn,this.userView);
			if("0".equals(flag)) {//展示列表
				String queryflag = (String) this.getFormHM().get("queryflag");
				String condsql = (String) this.getFormHM().get("condsql");
				StringBuffer datasql =  new StringBuffer();
				datasql.append("select signatureid,username,password,usertype,"
						+ "fullname,b0110,e0122,e01a1,ext_param,hardwareid,issavetodisk from signature where 1=1 ");
				if("0".equals(queryflag)) {//查询
					if(StringUtils.isNotBlank(condsql)) {
						condsql = PubFunc.keyWord_reback(SafeCode.decode(condsql)).replace("T.", "");
						datasql.append(condsql);
					}
					TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get("mb_signature_00001");
					tableCache.setTableSql(datasql.toString());
					return;
				}
				//获取表头
				ArrayList<ColumnsInfo> columnList =bo.getColumnList();
				/** 获取操作按钮*/
	    		ArrayList buttonList = bo.getButtonList();
	    		
				TableConfigBuilder builder = new TableConfigBuilder("mb_signature_00001", columnList, "signature", userView, this.getFrameconn());
				builder.setDataSql(datasql.toString());
				builder.setOrderBy("order by signatureid");
				builder.setAutoRender(false);
				builder.setTitle("印章及U盾管理");
				builder.setSetScheme(false);
				builder.setSelectable(true);
				builder.setColumnFilter(true);
				builder.setPageSize(20);
				builder.setTableTools(buttonList);
				String config = builder.createExtTableConfig();
				this.getFormHM().put("tableConfig", config.toString());
				ArrayList fieldsArray = new ArrayList();
				ArrayList fieldsMap = new ArrayList();
				for(int i=0;i<columnList.size();i++) {
					ColumnsInfo column = columnList.get(i);
					LazyDynaBean item = new LazyDynaBean();
					HashMap map = new HashMap();
					if("UN".equalsIgnoreCase(column.getCodesetId())|| "UM".equalsIgnoreCase(column.getCodesetId())|| "@K".equalsIgnoreCase(column.getCodesetId())||
							"fullname".equalsIgnoreCase(column.getColumnId())||"usertype".equalsIgnoreCase(column.getColumnId())){
			            item.set("codesetid", column.getCodesetId());
			            item.set("useflag", "1");
			            item.set("itemtype", column.getColumnType());
			            item.set("itemid", column.getColumnId().toUpperCase());
			            item.set("itemdesc", column.getColumnDesc());
			            map.put("type", column.getColumnType());
			            map.put("itemid", column.getColumnId().toUpperCase());
			            map.put("itemdesc", column.getColumnDesc());
			            map.put("codesetid", column.getCodesetId());
			            if("usertype".equalsIgnoreCase(column.getColumnId())) {
			            	map.put("codesource", "GetSignatureSelectTree");
			            	map.put("codesetid", "usertype");
			            }
			            map.put("codesetValid", false);
			            fieldsMap.add(item);
			            fieldsArray.add(map);
					}
				}
				this.getFormHM().put("fieldsMap", fieldsMap);
				this.getFormHM().put("fieldsArray", fieldsArray);
				//获得列表签章文件的图片高宽
				ArrayList imghwlist = bo.getImgHW();
				this.getFormHM().put("imghwlist", imghwlist);
				TemplateModuleParam tmp = new TemplateModuleParam(this.getFrameconn(),this.userView);
				this.getFormHM().put("signature_usb",tmp.getSignature_usb());
				String username = this.userView.getUserName();
				if(this.userView.getStatus()==4) {
					username = this.userView.getDbname()+this.userView.getA0100();
				}
				this.getFormHM().put("currentUser",username);
			}else if("1".equals(flag)) {//查询回显时用户的签章数据
				String signxml = (String) this.getFormHM().get("signxml");
				String tabid = (String) this.getFormHM().get("tabid");
				ArrayList userSignatureList = bo.getUserSignatureList(signxml,tabid);
				this.getFormHM().put("userSignatureList", userSignatureList);
			}else if("2".equals(flag)) {//验证锁是不是用户绑定的
				String BaningID = (String) this.getFormHM().get("BaningID");
				boolean bandingflag = bo.getBandingFlag(BaningID);
				this.getFormHM().put("bandingflag", bandingflag);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
