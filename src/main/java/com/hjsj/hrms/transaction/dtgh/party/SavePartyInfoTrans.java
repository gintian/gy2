package com.hjsj.hrms.transaction.dtgh.party;

import com.hjsj.hrms.businessobject.dtgh.CodeUtilBo;
import com.hjsj.hrms.valueobject.common.FieldItemView;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
/**
 * 
 * @author xujian
 *Jan 19, 2010
 */
public class SavePartyInfoTrans extends IBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void execute() throws GeneralException {
		this.getFormHM().put("sign","");
		String fieldsetid = (String)this.getFormHM().get("fieldsetid");
		ArrayList infofieldlist = (ArrayList)this.getFormHM().get("infofieldlist");
		String first = (String)this.getFormHM().get("first");
		String a_code = (String)this.getFormHM().get("a_code");
		String type = (String)this.getFormHM().get("type");//区分主集新增还是修改
		String subtype = (String)this.getFormHM().get("subtype");//区分子集新增还是修改
		boolean isMain = false;
		String codesetid = "64";
		if("Y01".equalsIgnoreCase(fieldsetid)){
			isMain = true;
			codesetid = "64";
		}else if("V01".equalsIgnoreCase(fieldsetid)){
			isMain = true;
			codesetid = "65";
		}else if("W01".equalsIgnoreCase(fieldsetid)){
			isMain = true;
			codesetid = "66";
		}else if("H01".equalsIgnoreCase(fieldsetid)){
			isMain = true;
			RecordVo constantuser_vo = ConstantParamter
			.getRealConstantVo("PS_C_CODE");
			codesetid = constantuser_vo.getString("str_value");
		}
		String codeitemid = "";
		String codeitemdesc = "";
		RecordVo vo = new RecordVo(fieldsetid.toLowerCase());
		for(int i=0;i<infofieldlist.size();i++){
			FieldItemView fieldItemView=(FieldItemView)infofieldlist.get(i);
			if("codeitemid".equalsIgnoreCase(fieldItemView.getItemid())){
				if("edit".equals(type)){
					codeitemid = fieldItemView.getValue();
				}else{
					codeitemid = a_code.substring(2)+fieldItemView.getValue();
				}
				vo.setString((fieldsetid.substring(0,1)+"0100").toLowerCase(), codeitemid);
			}else if("codeitemdesc".equalsIgnoreCase(fieldItemView.getItemid())){
				codeitemdesc= fieldItemView.getValue();
			}else{
				if("D".equals(fieldItemView.getItemtype())){
					vo.setDate(fieldItemView.getItemid().toLowerCase(), fieldItemView.getValue().replaceAll("\\.", "-"));
				}else if("N".equals(fieldItemView.getItemtype())){
					vo.setNumber(fieldItemView.getItemid().toLowerCase(), fieldItemView.getValue());
				}else{
					vo.setString(fieldItemView.getItemid().toLowerCase(), fieldItemView.getValue());
				}
			}
		}
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if(("add".equals(type)&&isMain)||(!isMain&&"add".equals(subtype))||(!isMain&&"insert".equals(subtype))){
			vo.setDate("createtime", sdf.format(new Date()));
			vo.setDate("modtime", sdf.format(new Date()));
			vo.setString("createusername", this.userView.getUserName());
			vo.setString("modusername", this.userView.getUserName());
		}else{
			vo.setDate("modtime", sdf.format(new Date()));
			vo.setString("modusername", this.userView.getUserName());
		}
		if(isMain){
			if("add".equals(type)){
				CodeUtilBo.saveCode(this.frameconn, first, codesetid, a_code.substring(2), codeitemid, codeitemdesc);
				CodeUtilBo.saveOrUpdateMainSet2(this.frameconn,vo,"add"); 
			}else{
				CodeUtilBo.updateCode(this.frameconn, codesetid, codeitemid, codeitemdesc);
				CodeUtilBo.saveOrUpdateMainSet2(this.frameconn,vo,"update");
			}
			//CodeUtilBo.updateLayer(frameconn, codesetid);
			//CodeUtilBo.saveOrUpdateMainSet(this.frameconn,vo);
			if("add".equals(type)){
				this.getFormHM().put("isrefresh", "save");
			}else{
				this.getFormHM().put("isrefresh", "update");
				this.getFormHM().put("sign", "update");
			}
			this.getFormHM().put("type","edit");
			this.getFormHM().put("codesetid", codesetid);
			this.getFormHM().put("codeitemid", codeitemid);
			this.getFormHM().put("codeitemdesc", codeitemdesc);
		}else{
			codeitemid = (String)this.getFormHM().get("codeitemid");
			String key= (fieldsetid.substring(0,1)+"0100").toLowerCase();
			vo.setString(key, codeitemid);
			if("add".equals(subtype)){
				vo.setInt("i9999", CodeUtilBo.getNextI9999(this.frameconn, fieldsetid,codeitemid));
			}if("insert".equals(subtype)){
				vo.setInt("i9999", Integer.parseInt((String)this.getFormHM().get("i9999")));
				CodeUtilBo.updateInsertI9999((String)this.getFormHM().get("i9999"), this.frameconn, fieldsetid, codeitemid);
				//vo.setInt("i9999", CodeUtilBo.getNextI9999(this.frameconn, fieldsetid,codeitemid));
			}else if("edit".equals(subtype)){
				vo.setInt("i9999", Integer.parseInt((String)this.getFormHM().get("i9999")));
			}
			CodeUtilBo.saveOrUpdateMainSet(this.frameconn,vo,key);
			this.getFormHM().put("isrefresh", "");
		}
		
	}

}
