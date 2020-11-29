package com.hjsj.hrms.transaction.general.sprelationmap;

import com.hjsj.hrms.businessobject.general.sprelationmap.RelationMapBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class QueryUpPersonTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			String opt=(String)this.getFormHM().get("opt");
			if("query".equals(opt)){
				String un=(String)this.getFormHM().get("un");
				String um=(String)this.getFormHM().get("um");
				String kk=(String)this.getFormHM().get("kk");
				String aa=(String)this.getFormHM().get("aa");
				String selected=(String)this.getFormHM().get("selected");
				RelationMapBo bo = new RelationMapBo(this.getFrameconn(),this.getUserView());
				//田野修改（如果操作人没有选择单位是，这根据登录人对组织机构控制范围的单位进行查询，
				//如果选择了那就按照选择的部门或单位或者岗位进行查询，因为前台页面已经控制了登录人范围才显示的机构树
				//然后进行遍历每个部门进行查询在拼接str
				String str = "";
				if("".equals(un)&&"".equals(um)&&"".equals(kk)){
					String unsStr = userView.getUnitIdByBusi("4");
					String[] uns = unsStr.split("`");
					String temphead="";
					String tempend = "";
					for(int i = 0 ;i<uns.length;i++){
						//根据登录人控制的范围在单位、部门或者岗位上的不同处理情况不同
						String codeSet = uns[i].substring(0,2);
						String code = uns[i].substring(2);
						if("UN".equals(codeSet)){
							un = code;
							um = "";
							kk = "";
						}else if("UM".equals(codeSet)){
							um = code;
							un = "";
							kk = "";
						}else if("@k".equals(codeSet)){
							kk = code;
							un = "";
							um = "";
						}
					 String info = bo.getPersonByCond(un, um, kk, aa,selected);
					 if(!"".equals(info)){
						 //info形式为USR00000556`USR00000564`USR00000566@宣教教育中心/杨征`宣教教育中心/钱小芳`宣教教育中心/刘树刚
						 String head = info.substring(0,info.indexOf("@"));
						  temphead +="`"+head;//形式为USR00000556`USR00000564`USR00000566
						  tempend += info.substring(info.indexOf("@")+1)+"`";//形式为宣教教育中心/杨征`宣教教育中心/钱小芳`宣教教育中心/刘树刚
					 }
					}
					if(!"".equals(temphead)){
						str=(temphead+"@"+tempend.substring(0,tempend.length()-1)).substring(1);
					}
					
				}else{
					str=bo.getPersonByCond(un, um, kk, aa,selected);
				}
				//田野修改结束
				//String str=bo.getPersonByCond(un, um, kk, aa,selected);原来的代码 （仅此一行代码）
				this.getFormHM().put("str", str);
			}else if("getdown".equalsIgnoreCase(opt)){
				String upId=(String)this.getFormHM().get("upId");
				RelationMapBo bo = new RelationMapBo(this.getFrameconn(),this.getUserView());
				ArrayList spRelationList=bo.spRelationList(upId);
				String relation_id="-1";
				if(spRelationList.size()>0)
					relation_id=((CommonData)spRelationList.get(0)).getDataValue();
				ArrayList downPersonList = bo.getDownPersonList(relation_id, upId);
				this.getFormHM().put("spRelationList", spRelationList);
				this.getFormHM().put("downPersonList", downPersonList);
			}else if("getdownself".equalsIgnoreCase(opt)){
				String upId=(String)this.getFormHM().get("upId");
				RelationMapBo bo = new RelationMapBo(this.getFrameconn(),this.getUserView());
				String relation_id=(String)this.getFormHM().get("relation_id");
				ArrayList downPersonList = bo.getDownPersonList(relation_id, upId);
				this.getFormHM().put("downPersonList", downPersonList);
			}
			else if("getname".equalsIgnoreCase(opt)){
				String upId = (String)this.getFormHM().get("upId");
				String relation_id=(String)this.getFormHM().get("relation_id");
				String id=(String)this.getFormHM().get("id");
				RelationMapBo bo = new RelationMapBo(this.getFrameconn(),this.getUserView());
				String message = "";
				if(bo.isCanAdd(upId, id, relation_id)){
					RecordVo vo = new RecordVo(id.substring(0, 3)+"A01");
					vo.setString("a0100", id.substring(3));
					ContentDAO dao = new ContentDAO(this.getFrameconn());
					if(dao.isExistRecordVo(vo))
						vo=dao.findByPrimaryKey(vo);
					Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
				  	String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
				  	String seprartor=sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122, "sep");
					seprartor=seprartor!=null&&seprartor.length()>0?seprartor:"/";
					if(display_e0122==null|| "00".equals(display_e0122)|| "".equals(display_e0122))
						display_e0122="0";
					String dataName="";
					if("0".equals(display_e0122))
						dataName=AdminCode.getCodeName("UM",vo.getString("e0122"))+"/"+vo.getString("a0101");
	        		  else{
	        			  CodeItem citem=AdminCode.getCode("UM",vo.getString("e0122"),Integer.parseInt(display_e0122));
	        			  if(citem!=null)
	        				  dataName=citem.getCodename()+seprartor+vo.getString("a0101");
	        			  else
	        				  dataName=AdminCode.getCodeName("UM",vo.getString("e0122"))+"/"+vo.getString("a0101");
	        		  }
					this.getFormHM().put("dataValue",id);
					this.getFormHM().put("dataName",dataName);
				}else{
					RecordVo vo = new RecordVo(id.substring(0, 3)+"A01");
					vo.setString("a0100", id.substring(3));
					ContentDAO dao = new ContentDAO(this.getFrameconn());
					if(dao.isExistRecordVo(vo))
						vo=dao.findByPrimaryKey(vo);
					RecordVo avo  = new RecordVo(upId.substring(0, 3)+"A01");
					avo.setString("a0100",upId.substring(3));
					if(dao.isExistRecordVo(avo))
						avo = dao.findByPrimaryKey(avo);
					RecordVo rvo = new RecordVo("t_wf_relation");
					rvo.setInt("relation_id",Integer.parseInt(relation_id));
					if(dao.isExistRecordVo(rvo))
						rvo=dao.findByPrimaryKey(rvo);
					message=(avo.getString("a0101")==null?"":avo.getString("a0101"))+" 和 "+(vo.getString("a0101")==null?"":vo.getString("a0101"))+" 在 "+
							(rvo.getString("cname")==null?"":rvo.getString("cname"))+" 中已存在审批关系，不能在进行设置！";
					
				}
				this.getFormHM().put("message", SafeCode.encode(message));
			}else if("save".equalsIgnoreCase(opt)){
				String object_ids=(String)this.getFormHM().get("object_ids");
				String mainbody_id=(String)this.getFormHM().get("mainbody_id");
				String relation_id=(String)this.getFormHM().get("relation_id");
				RelationMapBo bo = new RelationMapBo(this.getFrameconn(),this.getUserView());
				String str=bo.saveRelation(relation_id, mainbody_id, object_ids);
				this.getFormHM().put("mess",str);
			}
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
