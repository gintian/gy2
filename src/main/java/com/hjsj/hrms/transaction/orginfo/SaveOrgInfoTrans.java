package com.hjsj.hrms.transaction.orginfo;

import com.hjsj.hrms.businessobject.info.OrgInfoUtils;
import com.hjsj.hrms.businessobject.org.AddOrgInfo;
import com.hjsj.hrms.businessobject.structuresql.StructureExecSqlString;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SaveOrgInfoTrans extends IBusiness {

	public void execute() throws GeneralException {
	    try {
	        HashMap map=(HashMap)this.getFormHM().get("requestPamaHM");
	        String xuj = (String)map.get("xuj");
	        map.remove("xuj");
	        String first=(String)this.getFormHM().get("first");
	        String grade=(String)this.getFormHM().get("grade");
	        String action=(String)this.getFormHM().get("edittype");
	        String edittype = action;
	        String code=(String)this.getFormHM().get("code");
	        code=code!=null?code:"";
	        String I9999=(String)this.getFormHM().get("i9999");
	        List fieldlist=(List)this.getFormHM().get("infofieldlist");	         //获得fieldList
	        String setname=(String)map.get("setname");
	        if(StringUtils.isEmpty(setname))
	            setname=(String)this.getFormHM().get("setname");
	        
	        String len = (String) this.getFormHM().get("len");
	        String contentField = (String)this.getFormHM().get("contentField");
	        String value=(String)this.getFormHM().get("contentFieldValue");
	        if(setname!=null && setname.length()>=3 && "01".equals(setname.substring(1,3)))
	            action="new";
	        String kind=(String)this.getFormHM().get("kind");
	        if(code==null||code.length()<=0&&kind==null||kind.length()<=0)
	            throw GeneralExceptionHandler.Handle(new GeneralException("","请从左侧选择机构！","",""));
	        this.getFormHM().put("parentid", code);
	        StringBuffer fields=new StringBuffer();
	        StringBuffer fieldvalues=new StringBuffer();
	        String[] fieldsname=null;
	        String[] fieldcode=null;
	        if(!"xuj".equalsIgnoreCase(xuj)){//保存的是子集
	            fieldsname=new String[fieldlist.size()];
	            fieldcode=new String[fieldlist.size()];
	        }else{//主集
	            int num = 0;
	            for(int i=0;i<fieldlist.size();i++) {
	                FieldItem fi = (FieldItem) fieldlist.get(i);
	                if(",codesetid,codeitemid,codeitemdesc,corcode,".contains(fi.getItemid())) {
	                    num++;
	                }
	            }
	            
	            if(setname.length()==3 && "01".equals(setname.substring(1,3)) || setname.length()==6 && "01".equals(setname.substring(4,6))){
	                String unit_code_field="";
	                RecordVo unit_vo= ConstantParamter.getRealConstantVo("UNIT_CODE_FIELD");//单位代码
	                if(unit_vo!=null)
	                {
	                    unit_code_field=unit_vo.getString("str_value");
	                    if(unit_code_field!=null&&unit_code_field.length()>0&&!"#".equals(unit_code_field))
	                    {
	                        if("add".equalsIgnoreCase(edittype)){
	                            fieldsname=new String[fieldlist.size()-3];
	                            fieldcode=new String[fieldlist.size()-3];
	                        }else{
	                            fieldsname=new String[fieldlist.size()-1];
	                            fieldcode=new String[fieldlist.size()-1];
	                        }
	                        
	                    }
	                }
	            }
	            
	            if(fieldsname == null && fieldcode == null) {
	                fieldsname=new String[fieldlist.size()-num];
	                fieldcode=new String[fieldlist.size()-num];
	            }
	        }
	        String  ps_superior="";
	        RecordVo ps_superior_vo=ConstantParamter.getRealConstantVo("PS_SUPERIOR",this.getFrameconn());
	        if(ps_superior_vo!=null)
	        {
	            ps_superior=ps_superior_vo.getString("str_value");
	            this.getFormHM().put("ps_superior",ps_superior);
	        }
	        RecordVo vo=new RecordVo(setname.toLowerCase(),1);
	        String codesetid="";
	        String codeitemid="";
	        String codeitemdesc="";
	        String corcode="";
	        int n=0;
	        String orgFieldID=(String)this.getFormHM().get("orgFieldID");
	        orgFieldID=orgFieldID==null?"":orgFieldID;
	        for(int i=0;i<fieldlist.size();i++)
	        {
	            FieldItem fieldItem=(FieldItem)fieldlist.get(i);
	            if(fieldItem.getItemid()!=null&& "codesetid".equalsIgnoreCase(fieldItem.getItemid())){
	                codesetid=fieldItem.getValue();
	                continue;
	            }
	            if(fieldItem.getItemid()!=null&& "codeitemid".equalsIgnoreCase(fieldItem.getItemid())){
	                codeitemid=fieldItem.getValue();
	                if("new".equalsIgnoreCase(action) && !"1".equals(first) && codeitemid.length() != Integer.valueOf(len)) {
	                    throw new GeneralException("",ResourceFactory.getProperty("error.org.codelength") + len + "！","","");
	                }
	                continue;
	            }
	            if(fieldItem.getItemid()!=null&& "codeitemdesc".equalsIgnoreCase(fieldItem.getItemid())){
	                codeitemdesc=this.replaceSQLkey_reback(fieldItem.getValue());
	                codeitemdesc = PubFunc.splitString(codeitemdesc,fieldItem.getItemlength());
	                fieldItem.setValue(codeitemdesc);
	                continue;
	            }
	            if(fieldItem.getItemid()!=null&& "corcode".equalsIgnoreCase(fieldItem.getItemid())){
	                corcode=fieldItem.getValue();
	                continue;
	            }
	            
	            if(fieldItem.getItemid()!=null && fieldItem.getItemid().length()>0 && !"e01a1".equalsIgnoreCase(fieldItem.getItemid()) && !"b0110".equalsIgnoreCase(fieldItem.getItemid()))
	            {  
	                if(ps_superior.equalsIgnoreCase(fieldItem.getItemid()) && code.equalsIgnoreCase(fieldItem.getValue()))
	                {
	                    throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("pos.posparameter.directnessnoself"),"",""));
	                }
	                fields.append(fieldItem.getItemid());
	                fieldsname[n]=fieldItem.getItemid();			    
	                if("D".equals(fieldItem.getItemtype()) && (fieldItem.getValue() != null&&fieldItem.getValue().length()>0))
	                {
	                    fieldvalues.append(PubFunc.DateStringChange(fieldItem.getValue()));
	                    vo.setDate(fieldItem.getItemid().toLowerCase(), PubFunc.DateStringChangeValue(fieldItem.getValue()));
	                    fieldcode[n]=PubFunc.DateStringChange(fieldItem.getValue());				 		  
	                }else if("M".equals(fieldItem.getItemtype()))
	                {
	                    if (fieldItem.getValue() == null || "null".equals(fieldItem.getValue()) || "".equals(fieldItem.getValue()))
	                    {	
	                        fieldcode[n]="null";
	                        fieldvalues.append("null");
	                        vo.setString(fieldItem.getItemid().toLowerCase(), null);
	                    }
	                    else
	                    {
	                        String content=fieldItem.getValue();
	                        if((fieldItem.getItemid().equalsIgnoreCase(orgFieldID)&&"1".equals((String)this.getFormHM().get("type")))||((!fieldItem.getItemid().equalsIgnoreCase(orgFieldID)&&1==fieldItem.getInputtype()))){
	                            content=PubFunc.keyWord_reback(content);
	                        }
	                        
	                        content=PubFunc.getStr(content);
	                        content=PubFunc.stripScriptXss(content);
	                        fieldcode[n]="'" + content + "'";
	                        fieldvalues.append("'" + content + "'");
	                        vo.setString(fieldItem.getItemid().toLowerCase(), content);
	                    }
	                }else if("N".equals(fieldItem.getItemtype()))
	                {
	                    if (fieldItem.getValue() == null || "null".equals(fieldItem.getValue()) || "".equals(fieldItem.getValue()))
	                    {	
	                        fieldcode[n]="null";
	                        fieldvalues.append("null");
	                    }
	                    else
	                    {
	                        fieldcode[n]=fieldItem.getValue();
	                        fieldvalues.append(fieldItem.getValue());
	                        vo.setString(fieldItem.getItemid().toLowerCase(), fieldItem.getValue());
	                        
	                    }
	                }
	                else
	                {
	                    if(fieldItem.getValue() == null || "null".equals(fieldItem.getValue()) || "".equals(fieldItem.getValue())) {
	                        fieldcode[n]="null";
	                        fieldvalues.append("null");
	                        vo.setString(fieldItem.getItemid().toLowerCase(), null);
	                    } else {
	                        String content=PubFunc.getStr(fieldItem.getValue());
	                        content = this.replaceSQLkey_reback(content);
	                        fieldcode[n]="'" + PubFunc.splitString(content,fieldItem.getItemlength()) + "'";
	                        fieldvalues.append("'" + PubFunc.splitString(content,fieldItem.getItemlength()) + "'");
	                        vo.setString(fieldItem.getItemid().toLowerCase(), PubFunc.splitString(content,fieldItem.getItemlength()));
	                    }
	                }
	                fields.append(",");
	                fieldvalues.append(",");
	                n++;
	            }
	        }
	        if("xuj".equalsIgnoreCase(xuj)){
	            if((codeitemid==null||codeitemid.length()<=0)&&"add".equalsIgnoreCase(edittype))
	                throw GeneralExceptionHandler.Handle(new GeneralException("","本级系统代码不能为空，操作失败！","",""));
	            if(codeitemdesc==null||codeitemdesc.length()<=0)
	                throw GeneralExceptionHandler.Handle(new GeneralException("","单位名称不能为空，操作失败！","",""));
	            String tmp = isCheckCodeItemid(code+codeitemid);
	            if(tmp.length()>0&&"add".equalsIgnoreCase(edittype))
	            {
	                tmp = tmp.substring(1);
	                throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("label.org.adderrors1")+tmp,"",""));
	            }
	            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	            String backdate = (String)this.getFormHM().get("backdate");
	            backdate = backdate!=null&&backdate.length()>9?backdate:sdf.format(new java.util.Date());
	            if("add".equalsIgnoreCase(edittype)){
	                if("".equalsIgnoreCase(code)&&!("UN".equals(this.userView.getManagePrivCode())&&this.userView.getManagePrivCodeValue().length()==0)){
	                    throw GeneralExceptionHandler.Handle(new GeneralException("","只能在管理范围内新建组织单元，操作失败！","",""));
	                }
	                if("".equalsIgnoreCase(code)&&"UM".equalsIgnoreCase(codesetid)){
	                    throw GeneralExceptionHandler.Handle(new GeneralException("","根节点下不能新增部门，操作失败！","",""));
	                }
	                
	                corcode=corcode==null?"":corcode;
	                ContentDAO dao=new ContentDAO(this.getFrameconn());
	                String corcode_unique=com.hrms.struts.constant.SystemConfig.getPropertyValue("corcode_unique");
	                if("1".equals(corcode_unique)&&corcode.length()>0){
	                    try {
	                        this.frowset=dao.search("select codeitemid,codeitemdesc  from (select codeitemid,codeitemdesc  from organization where corcode='"+corcode+"' and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date union all select codeitemid,codeitemdesc  from vorganization where corcode='"+corcode+"' and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date) tt");
	                        if(this.frowset.next()){
	                            throw GeneralExceptionHandler.Handle(new GeneralException("","单位代码值\""+corcode+"\"在系统中已存在["+this.frowset.getString("codeitemdesc")+"("+this.frowset.getString("codeitemid")+")]，必需唯一!","",""));
	                        }
	                    } catch (SQLException e) {
	                        e.printStackTrace();
	                        throw GeneralExceptionHandler.Handle(e);
	                    }
	                }
	                
	                if(!addOrgData(codesetid,code,codeitemid,codeitemdesc,corcode,grade,first)){
	                    return;
	                }
	                code+=codeitemid;
	                
	                this.getFormHM().put("code", code);
	            }else{
	                
	                corcode=corcode==null?"":corcode;
	                ContentDAO dao=new ContentDAO(this.getFrameconn());
	                String corcode_unique=com.hrms.struts.constant.SystemConfig.getPropertyValue("corcode_unique");
	                if("1".equals(corcode_unique)&&corcode.length()>0){
	                    try {
	                        this.frowset=dao.search("select codeitemid,codeitemdesc from (select codeitemid,codeitemdesc from organization where corcode='"+corcode+"' and codeitemid<>'"+code+"'  and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date union all select codeitemid,codeitemdesc from vorganization where corcode='"+corcode+"' and codeitemid<>'"+code+"' and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date) tt");
	                        if(this.frowset.next()){
	                            throw GeneralExceptionHandler.Handle(new GeneralException("","单位代码值\""+corcode+"\"在系统中已存在["+this.frowset.getString("codeitemdesc")+"("+this.frowset.getString("codeitemid")+")]，必需唯一!","",""));
	                        }
	                    } catch (SQLException e) {
	                        e.printStackTrace();
	                        throw GeneralExceptionHandler.Handle(e);
	                    }
	                }
	                if(!eidtOrgData(code,codeitemdesc,corcode)){
	                    return;
	                }
	            }
	        }
	        if(setname.length()==3 && "01".equals(setname.substring(1,3)) || setname.length()==6 && "01".equals(setname.substring(4,6))){
	            String unit_code_field="";
	            RecordVo unit_vo= ConstantParamter.getRealConstantVo("UNIT_CODE_FIELD");//单位代码
	            if(unit_vo!=null)
	            {
	                unit_code_field=unit_vo.getString("str_value");
	                if(unit_code_field!=null&&unit_code_field.length()>0&&!"#".equals(unit_code_field))
	                {
	                    vo.setString(unit_code_field, corcode);
	                    fieldsname[fieldsname.length-1]=unit_code_field;
	                    if(corcode.length()==0)
	                        fieldcode[fieldcode.length-1]="null";
	                    else
	                        fieldcode[fieldcode.length-1]="'"+corcode+"'";
	                }
	            }
	        }
	        boolean flag=false;
	        String id;
	        String type;
	        if("2".equals(kind))
	            type="2";
	        else if("1".equals(kind))
	            type="3";
	        else
	            type="4";
	        
	        if(!"null".equalsIgnoreCase(code)&&code!=null&& !"".equals(code))
	        {
	            if(("new".equalsIgnoreCase(edittype)||"add".equalsIgnoreCase(edittype)||"insert".equalsIgnoreCase(edittype)))
	            {
	                if("insert".equalsIgnoreCase(edittype)){// xuj 机构管理子集中添加插入功能
	                    new StructureExecSqlString().detailinfoForInsertVo(type, setname, vo, code, userView.getUserName(), this.getFrameconn(), I9999);
	                }else{
	                    id=new StructureExecSqlString().InfoInsertVo(type,setname,vo,code,userView.getUserName(),this.getFrameconn());
	                    if(setname.length()==3 && "01".equals(setname.substring(1,3)) || setname.length()==6 && "01".equals(setname.substring(4,6)))
	                    { 
	                    }
	                    else
	                    {
	                        new StructureExecSqlString().insertZJRecord(setname, code, this.userView, this.getFrameconn());
	                    }
	                }
	                
	                if(!"B01".equalsIgnoreCase(setname) && !"K01".equalsIgnoreCase(setname)) {
	                    I9999 = vo.getString("i9999");
	                }
	            }
	            else
	                flag=new StructureExecSqlString().InfoUpdate(type,setname,fieldsname,fieldcode,code,I9999,userView.getUserName(),this.getFrameconn());
	        }	
	        if("b01".equalsIgnoreCase(setname)&&contentField!=null&&!"".equals(contentField))
	        {
	            this.updateContentField(code, setname, contentField, value);
	        }
	        if("add".equalsIgnoreCase(edittype)){	
	            if("UM".equalsIgnoreCase(codesetid))
	                kind="1";
	            else if("UN".equalsIgnoreCase(codesetid))
	                kind="2";
	            this.getFormHM().put("kind", kind);
	        }
	        
	        if(("new".equalsIgnoreCase(edittype)||"add".equalsIgnoreCase(edittype)||"insert".equalsIgnoreCase(edittype))) {
	            OrgInfoUtils orgInfoUtils=new OrgInfoUtils(this.getFrameconn());	
	            orgInfoUtils.updateSequenceableValue(code, setname, I9999);
	        }
	        this.getFormHM().put("infofieldlist",fieldlist);
	        this.getFormHM().put("edit_flag", "save");//证明主集已添加
	        this.getFormHM().put("setid", codesetid.length()>0?codesetid:getCodesetid(code,false));
	        this.getFormHM().put("codesetid", codesetid.length()>0?codesetid:getCodesetid(code,false));
	        this.getFormHM().put("itemid", code);
	        this.getFormHM().put("codeitemdesc", codeitemdesc.trim().length()>0?codeitemdesc:getCodesetid(code,true));
	        if("add".equalsIgnoreCase(edittype)){
	            this.getFormHM().put("isrefresh", "save");
	            if(this.getUserView().isAdmin()){
	                this.getFormHM().put("issuperuser", "1");
	                this.getFormHM().put("manageprive", "");
	            }else{
	                this.getFormHM().put("issuperuser", "0");
	                this.getFormHM().put("manageprive", this.getUserView().getManagePrivCode()+this.getUserView().getManagePrivCodeValue());
	            }
	        }else{
	            this.getFormHM().put("isrefresh", "update");
	        }
	        this.getFormHM().put("edittype", "edit");
            
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
	}
	public void updateContentField(String code,String setname,String contentField,String value)
	{
		try {
		    //指标未构库时，不更新此指标 chenxg 2016-11-22
		    DbWizard db = new DbWizard(this.frameconn);
		    if(db.isExistField(setname, contentField, false)) {
		        String sql = " update "+setname+" set "+contentField+"='"+value+"' where b0110='"+code+"'";
		        ContentDAO dao = new ContentDAO(this.getFrameconn());
		        dao.update(sql);		        
		    }
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	private String isCheckCodeItemid(String codeitemid)
	{
		boolean isCorrect=true;
		StringBuffer sql=new StringBuffer();
		sql.append("select codeitemdesc,codeitemid from organization where ");
		sql.append("codeitemid like '"+codeitemid+"%'");		
		sql.append(" union select codeitemdesc,codeitemid from vorganization where ");
		sql.append("codeitemid like '"+codeitemid+"%' order by codeitemid");
	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	    StringBuffer sb = new StringBuffer();
	    try {
			this.frowset=dao.search(sql.toString());
			while(this.frowset.next()){
				sb.append("、"+this.frowset.getString("codeitemdesc")+"("+this.frowset.getString("codeitemid")+")");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	private boolean addOrgData(String codesetid,String code,String codeitemid,String codeitemdesc,String corcode,String grade,String first)throws GeneralException
	{
		 ContentDAO dao=new ContentDAO(this.getFrameconn());		 
		 boolean doInitLayer=false;
		 AddOrgInfo ao=new AddOrgInfo(this.frameconn);
		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		 Date start_date=null;
		 Date end_date=null;		
		 try {
				start_date = new Date(sdf.parse(sdf.format(new java.util.Date())).getTime());
				end_date = new Date(sdf.parse("9999-12-31").getTime());
		 } catch (ParseException e1) {
				e1.printStackTrace();
		 }
		 boolean isCorrect=true;
		 StringBuffer sqlstr=new StringBuffer();
		 RecordVo vo = new RecordVo("organization");
		  Map lenmap = vo.getAttrLens();
		  int codeitemdesclen = Integer.parseInt((String)lenmap.get("codeitemdesc"));
		 sqlstr.append("insert into organization(codesetid,codeitemid,codeitemdesc,parentid,childid,state,grade,A0000,corcode,start_date,end_date,layer,levelA0000)");//添加同级排序字段  wangb 20180428 bug 36991
		 sqlstr.append("values(?,?,?,?,?,?,?,?,?,?,?,?,?)");//添加同级排序字段  wangb 20180428 bug 36991
		 
		 ArrayList sqlvalue=new ArrayList();
		 sqlvalue.add(codesetid);
		 sqlvalue.add((code +codeitemid).toUpperCase());
		 sqlvalue.add(PubFunc.splitString(codeitemdesc,codeitemdesclen));
		 String parentid="";
		 if(code!=null && code.trim().length()>0){
			 parentid=code.toUpperCase();
			 sqlvalue.add(code.toUpperCase());
		 }else{
			 parentid=(code + codeitemid).toUpperCase();
			 sqlvalue.add((code + codeitemid).toUpperCase());
		 }
		 sqlvalue.add((code + codeitemid).toUpperCase());
		 sqlvalue.add(null);
		 sqlvalue.add(grade);
		 sqlvalue.add(getMaxA0000(code));
		 sqlvalue.add(corcode);
		 sqlvalue.add(start_date);
		 sqlvalue.add(end_date);
		 int layer=0;
		try {
			layer = ao.getLayer(parentid, (code +codeitemid).toUpperCase(), codesetid);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
			if(0==layer){
				doInitLayer=true;
			}
			sqlvalue.add(new Integer(layer));
		 sqlvalue.add(getMaxLevelA0000(code));//新增机构时，levelA0000 同级排序 指标也要添加数据   wangb 20180428 bug 36991
		 CodeItem item=new CodeItem();
		 item.setCodeid(codesetid);
		 item.setCodeitem((code +codeitemid).toUpperCase());
		 item.setCodename(PubFunc.splitString(codeitemdesc,codeitemdesclen));
		 if(code!=null && code.trim().length()>0)
				item.setPcodeitem(code.toUpperCase());
		 else
		    	item.setPcodeitem((code +codeitemid).toUpperCase());
		 item.setCcodeitem((code +codeitemid).toUpperCase());
		 item.setCodelevel(grade+"");
		 AdminCode.addCodeItem(item);
		 AdminCode.updateCodeItemDesc(codesetid,(code + codeitemid).toUpperCase(),PubFunc.splitString(codeitemdesc,codeitemdesclen));
		 try
		 {
			 dao.insert(sqlstr.toString(),sqlvalue);
			 if("1".equals(first))
			 {
					StringBuffer update=new StringBuffer();
					update.append("update organization set childid='");
					update.append(code + codeitemid);
					update.append("' where codeitemid='");
					update.append(code);
					update.append("'");	
					dao.update(update.toString());
					first="0";
			 }
			 if(doInitLayer){//重置层级
				ao.executeInitLayer();
			}
		 }catch(Exception e)
		 {
			isCorrect=false; 
            e.printStackTrace();
		 }
		 return isCorrect;
	}
	private String getMaxA0000(String descode) throws GeneralException
	{
		String a0000="1";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try{
		   this.frowset=dao.search("select max(a0000) as a0000 from organization where codeitemid like '" + descode + "%'");
		   if(this.frowset.next())
		   {
			   a0000=String.valueOf(this.frowset.getInt("a0000") + 1);
			   dao.update("update organization set a0000=a0000 + 1 where a0000>" + this.frowset.getInt("a0000"));
		   }
		   else
			   dao.update("update organization set a0000=a0000 + 1 where a0000>0"); 
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return a0000;
	}
	
	private boolean eidtOrgData(String codeitemid,String codeitemdesc,String corcode)throws GeneralException
	{
		 ContentDAO dao=new ContentDAO(this.getFrameconn());
		 RecordVo vo = new RecordVo("organization");
		  Map lenmap = vo.getAttrLens();
		  int codeitemdesclen = Integer.parseInt((String)lenmap.get("codeitemdesc"));
		 boolean isCorrect=true;
		 StringBuffer sqlstr=new StringBuffer();
		 sqlstr.append("update organization set ");
		 sqlstr.append("codeitemdesc='"+PubFunc.splitString(codeitemdesc,codeitemdesclen)+"',corcode='"+corcode+"'");
		 sqlstr.append(" where codeitemid='"+codeitemid+"'");
			
		
		 try
		 {
			 dao.update(sqlstr.toString());
			 sqlstr.delete(0, sqlstr.length());
			 
			 sqlstr.append("select codesetid,codeitemid,codeitemdesc,parentid,childid,state,grade,A0000,corcode,start_date,end_date from ");
			 sqlstr.append(" organization where codeitemid='"+codeitemid+"'");
			 this.frowset=dao.search(sqlstr.toString());
			 if(this.frowset.next())
			 {
				 CodeItem item=new CodeItem();
				 item.setCodeid(this.frowset.getString("codesetid").toUpperCase());
				 item.setCodeitem(codeitemid.toUpperCase());
				 item.setCodename(PubFunc.splitString(codeitemdesc,codeitemdesclen));
				 item.setPcodeitem(this.frowset.getString("parentid").toUpperCase());				 
				 item.setCcodeitem(this.frowset.getString("childid").toUpperCase());
				 item.setCodelevel(this.frowset.getString("grade"));
				 AdminCode.addCodeItem(item);
				 AdminCode.updateCodeItemDesc(this.frowset.getString("codesetid").toUpperCase(), codeitemid.toUpperCase(),PubFunc.splitString(codeitemdesc,codeitemdesclen));
			 }
		 }catch(Exception e)
		 {
			isCorrect=false; 
            e.printStackTrace();
		 }
		 return isCorrect;
	}
	private String getCodesetid(String codeitemid,boolean isShowDesc){
		String corcode = "";
		StringBuffer strsql=new StringBuffer();
		String codesetid = "codesetid";
		if(isShowDesc) {
			codesetid = "codeitemdesc as codesetid";
		}
		strsql.append("select " + codesetid + " from organization where codeitemid='");
		strsql.append(codeitemid);
		strsql.append("' ");
		strsql.append("union select " + codesetid + " from vorganization where codeitemid='");
		strsql.append(codeitemid);
		strsql.append("' ");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try {
			this.frecset = dao.search(strsql.toString());
			if(this.frecset.next()){
				corcode=this.frecset.getString("codesetid");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return corcode!=null?corcode:"";
	}
	private String replaceSQLkey_reback(String value){
		try {
			value=value.replaceAll("_insert_"," insert ");
	        value=value.replaceAll("_select_"," select ");
	        value=value.replaceAll("_master_"," master ");
	        value=value.replaceAll("_update_"," update ");
	        value=value.replaceAll("_trancate_"," trancate ");
	        value=value.replaceAll("_into_"," into ");
			value=value.replaceAll("_and_"," and ");
			value=value.replaceAll("_or_"," or ");
			value=value.replaceAll("_asciit_"," asciit ");
			value=value.replaceAll("_exec_"," exec ");
			value=value.replaceAll("_execute_"," execute ");
			value=value.replaceAll("_drop_"," drop ");
			value=value.replaceAll("_delete_"," delete ");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
	
	/**
	 * 同级机构排序获取levelA0000最大值   wangb    20180428   bug 36991
	 * @param descode
	 * @return
	 * @throws GeneralException
	 */
	private String getMaxLevelA0000(String descode)throws GeneralException
	{
		String levelA0000 = "1";
	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	    ArrayList list = new ArrayList();
		try {
			list.add(descode);
			if(descode == null || "".equalsIgnoreCase(descode) || descode.trim().length()==0)//不存在 当前机构为顶级机构  bug 35006 wangb  20180301
				this.frowset=dao.search("select MAX(LEVELA0000) as levelA0000 from organization where codeitemid=parentid");
			else//上级机构存在
				this.frowset=dao.search("SELECT MAX(LEVELA0000) as levelA0000 FROM organization where PARENTID=?",list);
			if(this.frowset.next())
				levelA0000 = String.valueOf(this.frowset.getInt("levelA0000") + 1);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return levelA0000;
	}
}
