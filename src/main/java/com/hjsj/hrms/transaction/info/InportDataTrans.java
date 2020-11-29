package com.hjsj.hrms.transaction.info;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.struts.upload.FormFile;

import javax.sql.RowSet;
import java.io.InputStream;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * 将要导入的数据进行分析，是否进行提示报告。读入内存。
 * @author xujian
 *Apr 24, 2010
 */
public class InportDataTrans extends IBusiness {
    /**
     * 导入数据规则： 
     * a.主集信息： 
     *   1.模板中唯一性指标为空时不读取整行记录；
     *   2.模板中唯一性指标的值在正在导入数据的人员库中存在时，可以更新对应记录但不能新增，其他需要校验唯一性的人员库中存在时，不导入数据；不存在的记录可以导入；
     *   3.模板中同时存在唯一性指标和姓名指标时，导入数据的人员库中存在对应的记录，但是姓名与模板中不同，模板中的记录不导入；
     *   4.模板中唯一性指标的值在导入的数据库中存在时，但是不在登录用户权限范围内时，不导入数据； 
     * b.子集信息：
     *   1.模板中唯一性指标的值在导入的数据库中的主集中和模板的主集中不存在时，不导入对应记录；
     *   2.模板中的主集存在姓名列时，校验主集中的姓名与子集的姓名是否一致，不一致给出提示并且此人的所有数据都不导入数据库
     *   3.模板中子集存在姓名列时，校验对应人员在数据库中存在时，模板中的姓名与人员库中姓名不一致给出提示并且此人的所有数据都不导入数据库
     *   4.模板中子集的关联指标为空或有重复时，不允许导入任何记录
     */
	private String privFields="";
	private String fieldsetid="A01";
	private String fieldsetdesc=DataDictionary.getFieldSetVo(fieldsetid).getCustomdesc();
	private String a01fieldsetdesc=fieldsetdesc;
	private String privM="b0110";
	private String privMv="";
	private HashMap noPrivPersons=new HashMap();
	private RowSet orgRowSet = null;
	//模板中要导入的且在系统中存在的人员的唯一性指标和姓名
	private HashMap<String, ArrayList<String>> personMap = new HashMap<String, ArrayList<String>>();
	//模板人员基本信息集（a01）中新增的、在系统中不存在的人员，且姓名与子集不同的唯一性指标
	private HashMap<String, String> errorPersonMap = new HashMap<String, String>();
	//保存模板中关键指标对应的记录数量
    private HashMap<String, HashMap<String, String>> repeatMap = new HashMap<String, HashMap<String, String>>();
	public void execute() throws GeneralException {
		HashMap msgMap = new HashMap();
		ArrayList msglist = new ArrayList();
		String isupdate="0";
		String issameunique="1";
		try {
			this.doPrivFields();
			this.initPriv();
			ArrayList selectitemidlist = new ArrayList();

			FormFile file = (FormFile) this.getFormHM().get("file");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			ArrayList mapsList = readExcel(file,msgMap,selectitemidlist,dao);
			HashMap s = new HashMap();//用于记录处理的记录的主键所在人员库  key=主键值value=usr，oth 
            HashMap a0100s= new HashMap();
            this.getFormHM().put("mapsList", mapsList);
            StringBuffer A01primarykeys = new StringBuffer();//存放Excle中主集信息集中人员对应的唯一标志
            String primarykeyLabel="";
            String userbase=(String)this.getFormHM().get("userbase");
            String originalUserbase = userbase;
            String code=(String)this.getFormHM().get("code"); 
    		CheckPrivSafeBo checkPrivSafeBo=new CheckPrivSafeBo(this.frameconn,this.userView);
    		userbase=checkPrivSafeBo.checkDb(userbase);
    		code=checkPrivSafeBo.checkOrg(code, "");
    		String kind="2";
    		if(!userView.isSuper_admin())
            { 
               code=userView.getManagePrivCodeValue();
             }
    		String manaprivCode = this.userView.getManagePrivCode();
    		if("UN".equalsIgnoreCase(manaprivCode)){
    			kind="2";
    		}else if("UM".equals(manaprivCode)){
    			kind="1";
    		}else if("@K".equals(manaprivCode)){
    			kind="0";
    		}
    		
    		this.openOrgRowSet(dao);
    		
			for (int num=0 ;num<mapsList.size();num++){
			    Object[] maps = (Object[])mapsList.get(num);
			    
	            HashMap fieldMap = (HashMap)maps[0];
	            ArrayList valueList = (ArrayList)maps[1];
	            String primarykeys=((StringBuffer)maps[2]).toString();
	            ArrayList keyList =(ArrayList)maps[3];
	            ArrayList foreignkeys = (ArrayList)maps[4];
	            StringBuffer a0100sb = (StringBuffer)maps[5];
	            StringBuffer nopriva0100=(StringBuffer)maps[6];
	            fieldsetid=((StringBuffer)maps[7]).toString();
	            HashMap<String, String> diffa0101 = (HashMap<String, String>)maps[9];
	            String isExistB0110 = ((StringBuffer)maps[10]).toString();
	            if("A01".equalsIgnoreCase(fieldsetid)){
	                A01primarykeys  =(StringBuffer)maps[2];
	            }
	            
	            fieldsetdesc=DataDictionary.getFieldSetVo(fieldsetid).getCustomdesc();
	            a01fieldsetdesc=fieldsetdesc;
	            String sql = "";
	            if(keyList.size()<1){
	            	continue;
	                //return; //缺陷3131  zgd 2014-8-5 多个信息集同时导入，其中中间一个信息集为空，后面的信息集同样需要导入。
	            }
	            String primarykey =(String)keyList.get(0);
	            FieldItem fi=(FieldItem)fieldMap.get(primarykey);
	            if(fi!=null){
	                primarykeyLabel = fi.getItemdesc();
	            }
	            String b0110=(String)this.getFormHM().get("bb0110");
	            b0110=b0110!=null?b0110:"";
	            String e0122=(String)this.getFormHM().get("be0122");
	            e0122=e0122!=null?e0122:"";
	            String e01a1=(String)this.getFormHM().get("be01a1");
	            e01a1=e01a1!=null?e01a1:"";
	            String codeOfB0110=(String)this.getFormHM().get("codeOfB0110");
	            codeOfB0110=codeOfB0110!=null?codeOfB0110:"";
	            String codeOfE0122=(String)this.getFormHM().get("codeOfE0122");
	            codeOfE0122=codeOfE0122!=null?codeOfE0122:"";
	            String codeOfE01a1=(String)this.getFormHM().get("codeOfE01a1");
	            codeOfE01a1=codeOfE01a1!=null?codeOfE01a1:"";
	            String replacecode = "",replacevalue = ""; // 根据id(b0110,e0122,e01a1) 自动存储对应的 值。取值为codeOfB0110、codeOfE0122、codeOfE01a1
	            ArrayList<String> prList = new ArrayList<String>();
	            String ttt[]=primarykeys.split("','");
	            if(ttt.length>500){
	                StringBuffer sb=new StringBuffer();
	                int n=0;
	                boolean f=false;
	                for(int i=0;i<ttt.length;i++){
	                    if("".equals(ttt[i])){
	                        continue;
	                    }
	                    f=false;
	                    sb.append(ttt[i]+"','");
	                    if(n>498){
	                        f=true;
	                        prList.add(sb.toString());
	                        sb=new StringBuffer();
	                        n=0;
	                    }
	                    n++;
	                }
	                if(!f)
	                    prList.add(sb.toString());
	            }else{
	                prList.add(primarykeys);
	            }
	            
	            if("true".equalsIgnoreCase(isExistB0110))
	                //过滤单位部门岗位是否正确
	                this.doOrgCheck(valueList, dao, msgMap, primarykey);
	            else {
	            	if("A01".equalsIgnoreCase(fieldsetid)) {
	            		String dbnames = this.userView.getDbpriv().toString();
	            		String[] nbases = dbnames.split(",");
	            		ArrayList<String> dbnameList = new ArrayList<String>();
	            		sql = "select pre,dbname from dbname order by dbid";
	    	            this.frowset = dao.search(sql);
	    	            while(this.frowset.next()) {
	    	            	String nbase = this.frowset.getString("pre");
	    	            	for(int n = 0; n < nbases.length; n++) {
	    	            		String dbname = nbases[n];
	    	            		if(nbase.equalsIgnoreCase(dbname)) {
	    	            			dbnameList.add(nbase);
	    	            			break;
	    	            		}
	    	            	}
	    	            }
	    	            
	            		for(int n = 0; n < dbnameList.size(); n++) {
	            			String nbase = dbnameList.get(n);
	            			if(StringUtils.isEmpty(nbase))
	            				continue;
	            			
	            			for(int a = 0; a < prList.size(); a++) {
	            				StringBuffer searchSql = new StringBuffer();
	            				searchSql.append("select b0110,e0122," + primarykey);
	            				searchSql.append(" from " + nbase + "a01");
	            				searchSql.append(" where upper(" + primarykey + ") in ('" + prList.get(a) + "')");
	            				this.frowset = dao.search(searchSql.toString());
	            				while (this.frowset.next()) {
	            					String primaryvalue = this.frowset.getString(primarykey);
	            					String b0110Value = this.frowset.getString("b0110");
	            					String e0122Value = this.frowset.getString("e0122");
	            					for(int index = 0; index < valueList.size(); index++){
	            						HashMap valuemap=(HashMap)valueList.get(index);
	            						String primarykeyvalue=(String)valuemap.get(primarykey);
	            						if(primarykeyvalue.equals(primaryvalue)) {
	            							if(!keyList.contains("b0110")) {
	            								if(keyList.contains("e0122"))
	            									valuemap.put("b0110", b0110Value);
	            								
	            								if(!keyList.contains("e0122") && keyList.contains("e01a1")) {
	            									valuemap.put("b0110", b0110Value);
	            									valuemap.put("e0122", e0122Value);
	            								}
	            							} else {
	            								if(!keyList.contains("e0122") && keyList.contains("e01a1"))
	            									valuemap.put("e0122", e0122Value);
	            							}
	            							
	            							break;
	            						}
	            					}
	            				}
	            			}
	            		}
	            		
	            		this.doOrgCheck(valueList, dao, msgMap, primarykey);
	            	}
	            }
	                
	            this.doUNUMKCheck(valueList, fieldMap, dao);
	            //检查代码型的字段从excel中读取的codeitemdesc值是否在库中有对应的codeitemid
	            for(int n=0;n<keyList.size();n++){
	                String key=(String)keyList.get(n);
	                FieldItem item=(FieldItem)fieldMap.get(key);
	                if(item==null)
	                    continue;
	                String itemid= item.getItemid();
	                if((item.getCodesetid()!=null&&!"".equals(item.getCodesetid())&&!"0".equals(item.getCodesetid()))&& "A".equalsIgnoreCase(item.getItemtype())){
	                    if("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid())){
	                        sql = "select codeitemdesc,codeitemid from organization where upper(codesetid)='"+item.getCodesetid().toUpperCase()+"' and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date";
	                    }else{
	                        sql = "select codeitemdesc,codeitemid from codeitem where upper(codesetid)='"+item.getCodesetid().toUpperCase()+"'";// and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date";
	                    }
	                    
	                    
	                    this.frowset=dao.search(sql);
	                    HashMap codemap= new HashMap();
	                    HashMap valuemaps= new HashMap();
	                    while(this.frowset.next()){
	                    	if(StringUtils.isEmpty(this.frowset.getString("codeitemdesc").trim()))
	                    		continue;
	                    	
	                        codemap.put(this.frowset.getString("codeitemdesc").trim(),this.frowset.getString("codeitemid").trim());
	                        valuemaps.put(this.frowset.getString("codeitemid").trim(),this.frowset.getString("codeitemdesc").trim());
	                    }
	                    
	                    HashMap leafItemMmaps= new HashMap();
	                    DbWizard db = new DbWizard(this.frameconn);
	                    if(db.isExistField("codeset", "leaf_node", false)) {
	                        this.frowset = dao.search("select leaf_node from codeset where upper(codesetid)='"+item.getCodesetid().toUpperCase()+"'");
	                        if(this.frowset.next()) {
	                            String leafNode = this.frowset.getString("leaf_node");
	                            if("1".equals(leafNode)) {
	                                this.frowset=dao.search(sql + " and  codeitemid=childid");
	                                while(this.frowset.next()){
	                                    leafItemMmaps.put(this.frowset.getString("codeitemid").trim(),this.frowset.getString("codeitemdesc").trim());
	                                }
	                            }
	                        }
	                    }
                        
	                    for(int m=0;m<valueList.size();m++){
	                        HashMap valuemap=(HashMap)valueList.get(m);
	                        String primarykeyvalue=(String)valuemap.get(primarykey);
	                        String value=(String)valuemap.get(itemid);
	                        if("b0110".equalsIgnoreCase(item.getItemid())){
	                            if(value==null|| "".equals(value)){
	                                value=codeOfB0110;
	                            }
	                            replacecode = codeOfB0110;
	                        }
	                        if("e0122".equalsIgnoreCase(item.getItemid())){
	                            if(value==null|| "".equals(value)){
	                                value=codeOfE0122;
	                            }
	                            replacecode = codeOfE0122;
	                        }
	                        if("e01a1".equalsIgnoreCase(item.getItemid())){
	                            if(value==null|| "".equals(value)){
	                                value=codeOfE01a1;
	                            }
	                            replacecode = codeOfE01a1;
	                        }
	                        value = value==null?"":value;
	                        if("e0122".equalsIgnoreCase(item.getItemid()) || "b0110".equalsIgnoreCase(item.getItemid())
	                                || "e01a1".equalsIgnoreCase(item.getItemid()) || "UN".equalsIgnoreCase(item.getCodesetid())
	                                || "UM".equalsIgnoreCase(item.getCodesetid()) || "@K".equalsIgnoreCase(item.getCodesetid())){
	                            
	                        	if(codemap.containsKey(value)){//直接输入机构名称 并且机构名称存在  ,将  机构名称  转为  机构 编码 
                            		String orgcode = codemap.get(value).toString(); //获取 机构  id
                            			valuemap.put(itemid, orgcode);
                            			
                            	}else if(valuemaps.containsKey(value)){//直接输入机构代码 或者  "机构名（代码）" 的方式，读取excel值时时会直接读取出  机构代码
                            			valuemap.put(itemid, value);
                            	}else if(!"".equals(value)){ // 没有此【机构名称】(直接输入机构名称方式)，又没有此【 机构代码】(直接输入代码或 名称+代码方式)，又不为空，说明输入值不对
                            		
                            		valuemap.put(itemid, "```");
	                                ArrayList sb=(ArrayList)msgMap.get(primarykeyvalue);
                            		if(sb!=null){
	                                    int no=sb.size();
	                                    sb.add((no+1)+".&nbsp;["+item.getItemdesc()+"]"+ResourceFactory
	                                            .getProperty("workbench.info.import.error.codenol")+value+ResourceFactory
	                                            .getProperty("workbench.info.import.error.codenor"));
	                                }else{
	                                    sb= new ArrayList();
	                                    sb.add("1.&nbsp;["+item.getItemdesc()+"]"+ResourceFactory
	                                            .getProperty("workbench.info.import.error.codenol")+value+ResourceFactory
	                                            .getProperty("workbench.info.import.error.codenor"));
	                                }
                            	}else//走到这里说明值 是空，并且 机构树上也没有选择机构
                            		valuemap.put(itemid, value);
	                        	
	                        }else{
	                            if(!codemap.containsKey(value)&&!"".equals(value)){
	                                valuemap.put(itemid, "```");
	                                ArrayList sb=(ArrayList)msgMap.get(primarykeyvalue);
	                                if(sb!=null){
	                                    int no=sb.size();
	                                    sb.add((no+1)+".&nbsp;["+item.getItemdesc()+"]"+ResourceFactory
	                                            .getProperty("workbench.info.import.error.codenol")+value+ResourceFactory
	                                            .getProperty("workbench.info.import.error.codenor"));
	                                }else{
	                                    sb= new ArrayList();
	                                    sb.add("1.&nbsp;["+item.getItemdesc()+"]"+ResourceFactory
	                                            .getProperty("workbench.info.import.error.codenol")+value+ResourceFactory
	                                            .getProperty("workbench.info.import.error.codenor"));
	                                }
	                                msgMap.put(primarykeyvalue, sb);
	                            }else{
	                                if(!"".equals(value)){
	                                    String codeitemid = (String) codemap.get(value);
	                                    if(!leafItemMmaps.isEmpty() && !leafItemMmaps.containsKey(codeitemid)) {
	                                        valuemap.put(itemid, "```");
	                                        ArrayList sb=(ArrayList)msgMap.get(primarykeyvalue);
	                                        StringBuffer msg = new StringBuffer();
	                                        msg.append(".&nbsp;["+item.getItemdesc()+"]");
	                                        msg.append("仅可选择末端代码，");
	                                        msg.append("[" + value + "]不可选择！");
	                                        if(sb!=null){
	                                            int no=sb.size();
	                                            sb.add((no+1) + msg.toString());
	                                        }else{
	                                            sb= new ArrayList();
	                                            sb.add("1" + msg.toString());
	                                        }
	                                        msgMap.put(primarykeyvalue, sb);
	                                    } else
	                                        valuemap.put(itemid, codeitemid);
	                                }
	                            }
	                        }
	                    }
	                }
	            }
	            
	            HashMap allmap=new HashMap();
	            sql = "select pre,dbname from dbname order by dbid";
	            this.frowset = dao.search(sql);
	            Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
	            String chk = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"1","name");//身份证
	            String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");//唯一性指标
	            String chkvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"1","valid");
	            String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","valid");
	            if(chkvalid==null)
	                 chkvalid="0";
	             if(uniquenessvalid==null)
	                 chkvalid="0";
	             if(uniquenessvalid==null)
	                 uniquenessvalid="";
	             String chkcheck="",uniquenesscheck="";

	             if("0".equalsIgnoreCase(chkvalid)|| "".equalsIgnoreCase(chkvalid)){
	                 chkcheck="";
	             } else {
	                 chkcheck="checked";
	             }
	             
	             if("0".equalsIgnoreCase(uniquenessvalid)|| "".equalsIgnoreCase(uniquenessvalid)){
	                 uniquenesscheck="";
	             } else {
	                 uniquenesscheck="checked";
	             }
	             
	             String dbprimarykey="a0101";//默认为姓名
	                if(chk==null)
	                     chk="";
	                if(onlyname==null)
	                     onlyname = "";
	                
	                if(!("".equals(onlyname))&& "checked".equals(uniquenesscheck)){
	                    dbprimarykey=onlyname;
	                } else if(!("".equals(chk))&& "checked".equals(chkcheck)){
	                    dbprimarykey=chk;
	                }
	                
	                if(!dbprimarykey.equalsIgnoreCase(primarykey))
	                    issameunique="0";
	             
	            if(chk == null)
	                 chk = "";
	            
	            if(onlyname == null)
	                 onlyname = "";
	            
	            String onlynameValue = "";
	            if((primarykey.equals(onlyname))&& "checked".equals(uniquenesscheck)){
                    onlynameValue = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","db");
                    onlynameValue = StringUtils.isEmpty(onlynameValue) ? "all" : onlynameValue.toLowerCase();
	            }
	            
	            String chkValue = "";
	            if("checked".equals(chkcheck)){
                    chkValue = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"1","db");
                    chkValue = StringUtils.isEmpty(chkValue) ? "all" : chkValue.toLowerCase();
	            }
	            
	            HashMap<String, String> onlyDbMap = new HashMap<String, String>();
	            HashMap<String, String> chkDbMap = new HashMap<String, String>();
	            while(this.frowset.next()){
	                allmap.put(this.frowset.getString("pre"), this.frowset.getString("dbname"));
	                if(StringUtils.isNotEmpty(onlynameValue) && ("all".equals(onlynameValue)
	                            || onlynameValue.indexOf(this.frowset.getString("pre").toLowerCase()) > -1)){
	                    onlyDbMap.put(this.frowset.getString("pre"), this.frowset.getString("dbname"));
	                    
	                }
	                
	                if(StringUtils.isNotEmpty(chkValue) && ("all".equals(chkValue)
                            || chkValue.indexOf(this.frowset.getString("pre").toLowerCase()) > -1)){
	                    chkDbMap.put(this.frowset.getString("pre"), this.frowset.getString("dbname"));
                        
                    }
	            }
	            
	            if(primarykey.equals(chk)) {
	                onlyDbMap.clear();
	                onlyDbMap.putAll(chkDbMap);
	            }
	            
	            //需要校验唯一性的人员库（非当前选择的人员库）中已存在的人员数据
	            StringBuffer othDbnamePerson = new StringBuffer();
	            String selectNbase = userbase;
	            for(int m=0;m<prList.size();m++){
	                String tempp = prList.get(m); 
	                String nameField = "a0101,";
	                if("a0101".equalsIgnoreCase(primarykey))
	                    nameField = "";
	                
	                if("A01".equalsIgnoreCase(fieldsetid)){
	                    if(onlyDbMap.containsKey(userbase)){
	                        for(Iterator it=onlyDbMap.keySet().iterator();it.hasNext();){
	                            userbase = (String)it.next();
	                            String dbname=(String)onlyDbMap.get(userbase);
	                            // WJH 无权修改人员
	                            appendAdvNoprivA0100(userbase, dbname, tempp, primarykey, msgMap, s, nopriva0100,code,kind);
	                            if("admin".equals(this.privM)){
	                                sql="select a0100," + nameField + primarykey+" from "+userbase+fieldsetid+" where upper("+primarykey+") in('"+tempp.toUpperCase()+"##')";
	                            }else{
	                                sql="select "+this.privM+",a0100," + nameField +primarykey+" from "+userbase+fieldsetid+" where upper("+primarykey+") in('"+tempp.toUpperCase()+"##')";
	                            }
	                            this.frowset = dao.search(sql);
	                            while(this.frowset.next()){
	                                if(!userbase.equalsIgnoreCase(selectNbase))
	                                    othDbnamePerson.append(this.frowset.getString(primarykey)+",");
	                                    
	                                a0100sb.append(this.frowset.getString(primarykey)+",");
	                                String primaryvalue=this.frowset.getString(primarykey);
	                                if(!"admin".equalsIgnoreCase(this.privM)){
	                                    if(noPrivPersons.containsKey(primaryvalue)){
	                                        nopriva0100.append(primaryvalue+",");
	                                        ArrayList sb=(ArrayList)msgMap.get(primaryvalue);
	                                        if(sb!=null){
	                                            int no=sb.size();
	                                            sb.add((no+1)+".&nbsp;"+dbname+fieldsetdesc+ResourceFactory.getProperty("workbench.info.import.error.haveset")+"(您无权限操作)");
	                                        }else{
	                                            sb= new ArrayList();
	                                            sb.add("1.&nbsp;"+dbname+fieldsetdesc+ResourceFactory.getProperty("workbench.info.import.error.haveset")+"(您无权限操作)");
	                                        }
	                                        msgMap.put(primaryvalue, sb);
	                                        continue;
	                                    }
	                                }
	                                
	                                s.put(primaryvalue,userbase+"`"+this.frowset.getString("a0100"));//在人员库中存在，如果下一步选择了更新，则按所在人员库个更新，如果不存在则默认在在职人员库中插入新记录
	                                ArrayList sb=(ArrayList)msgMap.get(primaryvalue);
	                                if(sb!=null){
	                                    int no=sb.size();
	                                    sb.add((no+1)+".&nbsp;"+dbname+fieldsetdesc+ResourceFactory.getProperty("workbench.info.import.error.haveset"));
	                                }else{
	                                    sb= new ArrayList();
	                                    sb.add("1.&nbsp;"+dbname+fieldsetdesc+ResourceFactory.getProperty("workbench.info.import.error.haveset"));
	                                }
	                                /*
	                                 * 判断系统中的人员姓名和模板中的人员姓名是否一致：
	                                 * 1.不一致则保存到diffa0101中，不导入到数据库中；
	                                 * 2.一致则正常导入数据库
	                                 */
	                                ArrayList<String> infoList = personMap.get(fieldsetid.toLowerCase());
	                                if(infoList != null && infoList.size() > 0){
	                                    for(int i = 0; i < infoList.size(); i++) {
	                                        String info = infoList.get(i);
	                                        if(StringUtils.isEmpty(info))
	                                            continue;
	                                        
	                                        String newKey = info.split("=")[0];
	                                        if(StringUtils.isEmpty(newKey) || !newKey.equals(primaryvalue))
	                                            continue;
	                                        
	                                        String newa0101 = info.split("=")[1];
	                                        if(newa0101 != null) {
	                                            String a0101 = this.frowset.getString("a0101");
	                                            if(!newa0101.equals(a0101)) {
	                                            	String msg = ResourceFactory.getProperty("workbench.info.import.error.personNameDiff");
	                                            	msg = msg.replace("{0}", newa0101).replace("{1}", a0101);
	                                                if(sb!=null){
	                                                    int no=sb.size();
	                                                    sb.add((no+1)+".&nbsp;" + msg);
	                                                }else{
	                                                    sb= new ArrayList();
	                                                    sb.add("1.&nbsp;" + msg);
	                                                }
	                                                
	                                                diffa0101.put(primaryvalue, "1");
	                                            }
	                                        }
	                                        
	                                        break;
	                                    }
	                                }
	                                
	                                msgMap.put(primaryvalue, sb);
	                                isupdate="1";
	                            }
	                        }
	                    }else{
	                        String dbname=(String)allmap.get(userbase);
	                        // WJH 无权修改人员
	                        appendAdvNoprivA0100(userbase, dbname, tempp, primarykey, msgMap, s, nopriva0100, code, kind);                          
	                        if("admin".equals(this.privM)){
	                            sql="select a0100," + nameField +primarykey+" from "+userbase+fieldsetid+" where upper("+primarykey+") in('"+tempp.toUpperCase()+"##')";
	                        }else{
	                            sql="select "+this.privM+",a0100," + nameField +primarykey+" from "+userbase+fieldsetid+" where upper("+primarykey+") in('"+tempp.toUpperCase()+"##')";
	                        }
	                        this.frowset = dao.search(sql);
	                        while(this.frowset.next()){
	                            if(!userbase.equalsIgnoreCase(selectNbase))
	                                othDbnamePerson.append(this.frowset.getString(primarykey)+",");

	                            a0100sb.append(this.frowset.getString(primarykey)+",");
	                            String primaryvalue=this.frowset.getString(primarykey);
	                            if(!"admin".equalsIgnoreCase(this.privM)){
	                                if(noPrivPersons.containsKey(primaryvalue)){
	                                    nopriva0100.append(this.frowset.getString(primarykey)+",");
	                                    continue;
	                                }
	                            }
	                            //在人员库中存在，如果下一步选择了更新，则按所在人员库个更新，如果不存在则默认在在职人员库中插入新记录
	                            s.put(primaryvalue,userbase+"`"+this.frowset.getString("a0100"));
	                            ArrayList sb=(ArrayList)msgMap.get(primaryvalue);
	                            if(sb!=null){
	                                int no=sb.size();
	                                sb.add((no+1)+".&nbsp;"+dbname+fieldsetdesc+ResourceFactory.getProperty("workbench.info.import.error.haveset"));
	                            }else{
	                                sb= new ArrayList();
	                                sb.add("1.&nbsp;"+dbname+fieldsetdesc+ResourceFactory.getProperty("workbench.info.import.error.haveset"));
	                            }
	                            /*
	                             * 判断系统中的人员姓名和模板中的人员姓名是否一致：
	                             * 1.不一致则保存到diffa0101中，不导入到数据库中；
	                             * 2.一致则正常导入数据库
	                             */
	                            ArrayList<String> infoList = personMap.get(fieldsetid.toLowerCase());
	                            if(infoList != null && infoList.size() > 0){
	                                for(int i = 0; i < infoList.size(); i++) {
	                                    String info = infoList.get(i);
	                                    if(StringUtils.isEmpty(info))
	                                        continue;
	                                    
	                                    String newKey = info.split("=")[0];
	                                    if(StringUtils.isEmpty(newKey) || !newKey.equals(primaryvalue))
	                                        continue;
	                                    
	                                    String newa0101 = info.split("=")[1];
	                                    if(newa0101 != null) {
	                                        String a0101 = this.frowset.getString("a0101");
	                                        if(!newa0101.equals(a0101)) {
	                                            if(sb!=null){
	                                                int no=sb.size();
	                                                sb.add((no+1)+".&nbsp;模板中的姓名（" + newa0101 + "）与系统中的姓名（" + a0101 + "）不一致！");
	                                            }else{
	                                                sb= new ArrayList();
	                                                sb.add("1.&nbsp;模板中的姓名（" + newa0101 + "）与系统中的姓名（" + a0101 + "）不一致！");
	                                            }
	                                            
	                                            diffa0101.put(primaryvalue, "1");
	                                        }
	                                    }
	                                    break;
	                                }  
	                            }
	                            
	                            msgMap.put(primaryvalue, sb);
	                            isupdate="1";
	                        }
	                    }
	                    
	                    if(!primarykey.equals(chk) && keyList.contains(chk)) {
	                        FieldItem chkItem = DataDictionary.getFieldItem(chk, "A01"); 
	                        String chkDesc = chkItem.getItemdesc();
	                        StringBuffer idCardValues = new StringBuffer();
	                        for(int a = 0; a < valueList.size(); a++) {
	                            HashMap valueMap = (HashMap) valueList.get(a);
	                            if(StringUtils.isEmpty((String)valueMap.get(chk))) {
	                            	continue;
	                            }
	                            
	                            idCardValues.append(valueMap.get(chk) + "','");
	                        }
	                        
	                        if(StringUtils.isNotEmpty(idCardValues.toString())) {
	                        	for(Iterator it=chkDbMap.keySet().iterator();it.hasNext();){
	                        		userbase = (String)it.next();
	                        		String dbname=(String)chkDbMap.get(userbase);
	                        		if("admin".equals(this.privM)){
	                        			sql="select a0100," + nameField + primarykey+" from "+userbase+fieldsetid+" where upper("+chk+") in('"+idCardValues.toString().toUpperCase()+"##')";
	                        		}else{
	                        			sql="select "+this.privM+",a0100," + nameField +primarykey+" from "+userbase+fieldsetid+" where upper("+chk+") in('"+idCardValues.toString().toUpperCase()+"##')";
	                        		}
	                        		this.frowset = dao.search(sql);
	                        		if(this.frowset.next()){
	                        			if(!userbase.equalsIgnoreCase(selectNbase))
	                        				othDbnamePerson.append(this.frowset.getString(primarykey)+",");
	                        			
	                        			a0100sb.append(this.frowset.getString(primarykey)+",");
	                        			String primaryvalue=this.frowset.getString(primarykey);
	                        			
	                        			s.put(primaryvalue,userbase+"`"+this.frowset.getString("a0100"));//在人员库中存在，如果下一步选择了更新，则按所在人员库个更新，如果不存在则默认在在职人员库中插入新记录
	                        			ArrayList sb=(ArrayList)msgMap.get(primaryvalue);
	                        			if(sb == null) {
	                        				sb = new ArrayList();
	                        			}
	                        			
	                        			sb.add((sb.size() + 1) +".&nbsp;"+dbname+fieldsetdesc + "[" + chkDesc + "]"
	                        					+ResourceFactory.getProperty("workbench.info.import.error.haveset"));
	                        			msgMap.put(primaryvalue, sb);
	                        		}
	                        	}
	                        }
	                    }
	                }else{
	                    ArrayList<String> a01InfoList = personMap.get("a01");
	                    ArrayList<String> infoList = personMap.get(fieldsetid.toLowerCase());
	                    HashMap<String, String> existInfoMap = new HashMap<String, String>();
	                    userbase = originalUserbase;
	                    // WJH 无权修改人员
	                    appendAdvNoprivA0100(userbase, "", tempp, primarykey, msgMap, s, nopriva0100, code, kind);
	                    
	                    if("admin".equals(this.privM)){
	                        sql="select a0100," + nameField +primarykey+" from "+userbase+"a01"+" where upper("+primarykey+") in('"+tempp.toUpperCase()+"##')";
	                    }else{
	                        sql="select "+this.privM+",a0100," + nameField +primarykey+" from "+userbase+"a01"+" where upper("+primarykey+") in('"+tempp.toUpperCase()+"##')";
	                    }
	                    this.frowset = dao.search(sql);
	                    while(this.frowset.next()){
	                        String primaryvalue=this.frowset.getString(primarykey);
	                        s.put(primaryvalue,userbase+"`"+this.frowset.getString("a0100"));
	                        if("admin".equals(this.privM)){
	                            a0100s.put(primaryvalue, this.frowset.getString("a0100"));
	                        }else{
	                            if(noPrivPersons.containsKey(primaryvalue)){
	                                nopriva0100.append(primaryvalue+",");
	                            }else{
	                                a0100s.put(primaryvalue, this.frowset.getString("a0100"));
	                            }
	                        }
	                        
	                        existInfoMap.put(primaryvalue, "1");
	                        /*
	                         * 判断系统中的人员姓名和模板中的人员姓名是否一致：
	                         * 1.不一致则在a0100s中删除，不导入到数据库中；
	                         * 2.一致则正常导入数据库
	                         */
	                        if(infoList != null && infoList.size() > 0){
	                            for(int i = 0; i < infoList.size(); i++) {
	                                String info = infoList.get(i);
	                                if(StringUtils.isEmpty(info))
	                                    continue;
	                                
	                                String newKey = info.split("=")[0];
	                                if(StringUtils.isEmpty(newKey) || !newKey.equalsIgnoreCase(primaryvalue))
	                                    continue;
	                                
	                                String newa0101 = info.split("=")[1];
	                                if(newa0101 != null) {
	                                    String a0101 = this.frowset.getString("a0101");
	                                    if(!newa0101.equals(a0101)) {
	                                        ArrayList sb=(ArrayList)msgMap.get(primaryvalue);
	                                        if(sb!=null){
	                                            int no=sb.size();
	                                            sb.add((no+1)+".&nbsp;模板中的姓名（" + newa0101 + "）与系统中的姓名（" + a0101 + "）不一致！");
	                                        }else{
	                                            sb= new ArrayList();
	                                            sb.add("1.&nbsp;模板中的姓名（" + newa0101 + "）与系统中的姓名（" + a0101 + "）不一致！");
	                                        }
	                                        
	                                        msgMap.put(primaryvalue, sb);
	                                        a0100s.remove(primaryvalue);
	                                    }
	                                }
	                            }
	                        }
	                    }
	                    //当模板中的主集存在姓名列时，校验主集中的姓名与子集的姓名是否一致，不一致给出提示并且此人的所有数据都不导入数据库    
	                    if(a01InfoList != null && a01InfoList.size() > 0) {
	                        for(int i = 0; i < a01InfoList.size(); i++) {
	                            String a01info = a01InfoList.get(i);
	                            if(StringUtils.isEmpty(a01info))
	                                continue;
	                            
	                            String newA01Key = a01info.split("=")[0];
	                            if(StringUtils.isEmpty(newA01Key))
	                                continue;
	                            //系统中存在的人员不进行校验
	                            if(existInfoMap.containsKey(newA01Key))
	                                continue;
	                            
	                            String newa0101name = a01info.split("=")[1];
	                            if(newa0101name != null) {
	                                for(int n = 0; n < infoList.size(); n++) {
	                                    String info = infoList.get(n);
	                                    if(StringUtils.isEmpty(info))
	                                        continue;
	                                    
	                                    String newKey = info.split("=")[0];
	                                    if(StringUtils.isEmpty(newKey) || !newKey.equals(newA01Key))
	                                        continue;
	                                    
	                                    String newa0101 = info.split("=")[1];
	                                    if(newa0101 != null) {
	                                        if(!newa0101name.equals(newa0101)) {
	                                            ArrayList sb=(ArrayList)msgMap.get(newA01Key);
	                                            if(sb!=null){
	                                                int no=sb.size();
	                                                sb.add((no+1)+".&nbsp;模板中" + fieldsetdesc + "的姓名（" + newa0101 + "）与主集的姓名（" + newa0101name + "）不一致！");
	                                            }else{
	                                                sb= new ArrayList();
	                                                sb.add("1.&nbsp;模板中" + fieldsetdesc + "的姓名（" + newa0101 + "）与主集的姓名（" + newa0101name + "）不一致！");
	                                            }
	                                            
	                                            msgMap.put(newA01Key, sb);
	                                            diffa0101.put(newA01Key, "1");
	                                            errorPersonMap.put(newA01Key, "1");
	                                        }
	                                    }
	                                }
	                            }
	                        }
	                    }
	                }
	            }
	            //如果在其他需要校验唯一性的人员库中已存在的人员数量和需要导入的人员数量相同则在提示信息页面不显示导入按钮和记录更新选择框
	            if(StringUtils.isNotEmpty(othDbnamePerson.toString())) {
	                String[] existA0100s = othDbnamePerson.toString().trim().split(",");
	                if(ttt.length <= existA0100s.length)
	                    //需要导入的主键值与已存在的主键个数一样的时候不显示导入按钮
	                    this.getFormHM().put("RepeatPrimaryKey", "1");
	            }
	            
	            if(!"A01".equalsIgnoreCase(fieldsetid)){
	                StringBuffer tempsb= new StringBuffer();
	                String t[]=primarykeys.split("','");
	                for(int i=0;i<t.length;i++){
	                    if("".equals(t[i])){
	                        continue;
	                    }
	                    if(!s.containsKey(t[i])){//主键在所有人员库中都不存在
	                        if(-1!=A01primarykeys.indexOf(t[i])){
	                            continue;  
	                        }
	                        ArrayList sb=(ArrayList)msgMap.get(t[i]);
	                        if(sb!=null){
	                            int no=sb.size();
	                            sb.add((no+1)+".&nbsp;"+a01fieldsetdesc+ResourceFactory
	                                    .getProperty("workbench.info.import.error.mainhave"));
	                        }else{
	                            sb= new ArrayList();
	                            sb.add("1.&nbsp;"+a01fieldsetdesc+ResourceFactory
	                                    .getProperty("workbench.info.import.error.mainhave"));
	                        }
	                        msgMap.put(t[i], sb);
	                        a0100s.remove(t[i]);
	                    }else{
	                        String dba0100=(String)s.get(t[i]);
	                        String tt[]=dba0100.split("`");
	                        userbase = tt[0];
	                        StringBuffer sbsql = new StringBuffer();
	                        ArrayList values = new ArrayList();
	                        HashMap valueMap = (HashMap)valueList.get(i);
	                        if(foreignkeys.size()>0){//判断子集信息有相同记录没，按按外键识别
	                            //HashMap valueMap = (HashMap)valueList.get(i);
	                            sbsql.setLength(0);
	                            sbsql.append("select a0100 from "+userbase+fieldsetid+" where a0100='"+(String)a0100s.get(t[i])+"'");
	                            for(int n=0;n<foreignkeys.size();n++){
	                                String itemid=(String)foreignkeys.get(n);
	                                String value=(String)valueMap.get(itemid);
	                                if(value==null || "".equals(value) || "```".equals(value)){
                                        sbsql.append(" and "+itemid+" is null ");
                                        continue;
                                    }
                                    
                                    FieldItem item = DataDictionary.getFieldItem(itemid);
                                    if("A".equals(item.getItemtype())&&!"".equals(value)){
                                        sbsql.append(" and "+itemid+"=?");
                                        value=splitString(value, item.getItemlength());
                                        values.add(value);
                                    }else if("D".equals(item.getItemtype())){
                                        if(!"".equals(value))
                                            sbsql.append(" and "+itemid+"="+Sql_switcher.dateValue(value));
                                        else
                                            sbsql.append(" and "+itemid+" is null ");
                                           
                                    }else if("N".equals(item.getItemtype())){
                                            if("".equals(value)){
                                                sbsql.append(" and "+itemid+" is null ");
                                            }else{
                                                if(item.getDecimalwidth()!=0){
                                                    value=splitString(value, item.getItemlength()+item.getDecimalwidth()+1);//zgd 2014-7-8长度以整数位于小数位位数之和
                                                }else{
                                                    value=splitString(value, item.getItemlength());
                                                }
                                                sbsql.append(" and "+itemid+"="+value);
                                            }
                                    }else if("M".equals(item.getItemtype())){
                                    }
	                            }
	                        }else{//如果导出模板没有设置外键列，则所有的值匹配上才算相同记录
	                            //HashMap valueMap = (HashMap)valueList.get(i);
	                            sbsql.setLength(0);
	                            sbsql.append("select a0100 from "+userbase+fieldsetid+" where a0100='"+(String)a0100s.get(t[i])+"'");
	                            for(Iterator it=valueMap.keySet().iterator();it.hasNext();){
	                                String itemid=(String)it.next();
	                                if(itemid.equalsIgnoreCase(primarykey)){
	                                    continue;
	                                }
	                                String value=(String)valueMap.get(itemid);
	                                if(value==null || "".equals(value) || "```".equals(value)){
	                                	sbsql.append(" and "+itemid+" is null ");
	                                    continue;
	                                }
	                                
	                                FieldItem item = DataDictionary.getFieldItem(itemid);
	                                if("A".equals(item.getItemtype())&&!"".equals(value)){
	                                    sbsql.append(" and "+itemid+"=?");
	                                    value=splitString(value, item.getItemlength());
                                        values.add(value);
	                                }else if("D".equals(item.getItemtype())){
	                                    if(!"".equals(value))
	                                        sbsql.append(" and "+itemid+"="+Sql_switcher.dateValue(value));
	                                    else
	                                        sbsql.append(" and "+itemid+" is null ");
	                                       
	                                }else if("N".equals(item.getItemtype())){
	                                        if("".equals(value)){
	                                            sbsql.append(" and "+itemid+" is null ");
	                                        }else{
	                                        	if(item.getDecimalwidth()!=0){
	                                        		value=splitString(value, item.getItemlength()+item.getDecimalwidth()+1);//zgd 2014-7-8长度以整数位于小数位位数之和
	                                        	}else{
	                                        		value=splitString(value, item.getItemlength());
	                                        	}
	                                            sbsql.append(" and "+itemid+"="+value);
	                                        }
	                                }else if("M".equals(item.getItemtype())){
	                                }
	                            }
	                        }
	                        
	                        this.frowset = dao.search(sbsql.toString(), values);
	                        if(this.frowset.next()){
	                            ArrayList sb=(ArrayList)msgMap.get(t[i]);
	                            String dbname=(String)allmap.get(userbase);
	                            if(sb!=null){
	                                int no=sb.size();
	                                if(foreignkeys.size()>0){
	                                    tempsb.setLength(0);
	                                    tempsb.append((no+1)+".&nbsp;"+dbname+fieldsetdesc+ResourceFactory.getProperty("workbench.info.import.error.haveset1"));
	                                    for(int n=0;n<foreignkeys.size();n++){
	                                        String itemid=(String)foreignkeys.get(n);
	                                        String value=(String)valueMap.get(itemid);
	                                        if(value==null||"".equals(value)){
	                                            continue;
	                                        }
	                                        if("```".equals(value)){
	                                            continue;
	                                        }
	                                        FieldItem item = DataDictionary.getFieldItem(itemid);
	                                        if("M".equals(item.getItemtype()))
	                                            continue;
	                                        if(!"0".equals(item.getCodesetid())){
	                                            value=AdminCode.getCodeName(item.getCodesetid(), value);
	                                        }
	                                        tempsb.append("["+item.getItemdesc()+"]="+value);
	                                    }
	                                    tempsb.append(ResourceFactory.getProperty("workbench.info.import.error.haveset2"));
	                                    sb.add(tempsb.toString());
	                                }else{
	                                    sb.add((no+1)+".&nbsp;"+dbname+fieldsetdesc+ResourceFactory.getProperty("workbench.info.import.error.haveset"));
	                                }
	                            }else{
	                                sb= new ArrayList();
	                                if(foreignkeys.size()>0){
	                                    tempsb.setLength(0);
	                                    tempsb.append("1.&nbsp;"+dbname+fieldsetdesc+ResourceFactory.getProperty("workbench.info.import.error.haveset1"));
	                                    for(int n=0;n<foreignkeys.size();n++){
	                                        String itemid=(String)foreignkeys.get(n);
	                                        String value=(String)valueMap.get(itemid);
	                                        if(value==null||"".equals(value)){
	                                            continue;
	                                        }
	                                        if("```".equals(value)){
	                                            continue;
	                                        }
	                                        FieldItem item = DataDictionary.getFieldItem(itemid);
	                                        if("M".equals(item.getItemtype()))
	                                            continue;
	                                        if(!"0".equals(item.getCodesetid())){
	                                            value=AdminCode.getCodeName(item.getCodesetid(), value);
	                                        }
	                                        tempsb.append("["+item.getItemdesc()+"]="+value + "&nbsp;");
	                                    }
	                                    tempsb.append(ResourceFactory.getProperty("workbench.info.import.error.haveset2"));
	                                    sb.add(tempsb.toString());
	                                }else{
	                                    sb.add("1.&nbsp;"+dbname+fieldsetdesc+ResourceFactory.getProperty("workbench.info.import.error.haveset"));
	                                }
	                            }
	                            msgMap.put(t[i], sb);
	                            isupdate="1";
	                        }
	                    }
	                }
	            }
			}
			 for(Iterator i =msgMap.keySet().iterator();i.hasNext();){
                 String key=(String)i.next();
                 LazyDynaBean ldb= new LazyDynaBean();
                 ldb.set("keyid", key);
                 ArrayList sb=(ArrayList)msgMap.get(key);
                 StringBuffer sbb=new StringBuffer();
                 for(int n=0;n<sb.size();n++){
                     sbb.append("&nbsp;"+(String)sb.get(n)+"</br>");
                 }
                 ldb.set("content", sbb.toString());
                 msglist.add(ldb);
             }
			this.getFormHM().put("unusekey", s);
			this.getFormHM().put("A01primarykeys", A01primarykeys);
			this.getFormHM().put("a0100s", a0100s);
			this.getFormHM().put("fieldsetid", fieldsetid);
			this.getFormHM().put("primarykeyLabel", primarykeyLabel);
			this.userView.getHm().put("errorPerson", errorPersonMap);
			this.getFormHM().put("repeatMap", repeatMap);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			this.getFormHM().put("msglist", msglist);
			this.getFormHM().put("isupdate", isupdate);
			this.getFormHM().put("issameunique", issameunique);
			
			PubFunc.closeDbObj(this.orgRowSet);
		}
	}
	
	/** 高级权限范围外的a0100
	 * @param userbase： 人员库
	 * @param tempp： 要检查的人员
	 * @param primarykey
	 * @param kind 
	 * @param code 
	 * @param a0100sb： 结果提示
	 * @param nopriva0100： 追加无权限的a0100
	 * @throws SQLException 
	 * @throws GeneralException 
	 */
	private void appendAdvNoprivA0100(String userbase, String dbname, String tempp, String primarykey, HashMap msgMap,
			HashMap s, StringBuffer nopriva0100, String code, String kind) throws SQLException, GeneralException {
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String sql;
		if("admin".equals(this.privM)){
			return;
		}else{
			InfoUtils infoUtils=new InfoUtils();
			String term_Sql=infoUtils.getWhereSQLExists(this.getFrameconn(),this.userView,userbase,code,true,kind,"org",null,null);
			sql = "select a0100,"+primarykey+" from "+userbase+"a01"+" where upper("+primarykey+") in('"+tempp.toUpperCase()+"##')"
			    + "and not exists(select a0100 from ("+term_Sql.toString()+")A where  "+userbase+"A01.a0100=A.a0100)";
		}//zgd term_Sql中第五个参数isCodeLike默认为true，sql中指标就为like 2014-2-15
		this.frowset = dao.search(sql);
		while(this.frowset.next()){
			nopriva0100.append(this.frowset.getString(primarykey)+",");
			String primaryvalue=this.frowset.getString(primarykey);
			// 记录，无权限时不做其他提示了
			noPrivPersons.put(primaryvalue, "");
			
			s.put(primaryvalue,userbase+"`"+this.frowset.getString("a0100"));//在人员库中存在，如果下一步选择了更新，则按所在人员库个更新，如果不存在则默认在在职人员库中插入新记录
			ArrayList sb=(ArrayList)msgMap.get(primaryvalue);
			if(sb!=null){
				int no=sb.size();
				sb.add((no+1)+".&nbsp;"+dbname+ResourceFactory.getProperty("workbench.info.import.error.nopersonpriv"));
			}else{
				sb= new ArrayList();
				sb.add("1.&nbsp;"+dbname+ResourceFactory.getProperty("workbench.info.import.error.nopersonpriv"));
			}
			msgMap.put(primaryvalue, sb);			
		}
    }

	/**
	 * 读取excel数据放入集合对象
	 * 
	 * @param file
	 * @param dao 
	 * @param hashMap
	 * @return
	 * @throws Exception
	 */
	private ArrayList readExcel(FormFile file, HashMap msgMap,
			ArrayList selectitemidlist, ContentDAO dao) throws Exception {
		
		ArrayList mapsList = new ArrayList();
		Workbook owb = null;
		Sheet osheet = null;
		// =0,没有重复主键；=1，有重复主键或有主键的值为空，不允许导入，提示信息页面不显示导入按钮
		String RepeatPrimaryKey = "0";
		InputStream ism = null;
		try {
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
            String chk = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"1","name");//身份证
            String chkvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"1","valid");
            String idType = sysbo.getValue(Sys_Oth_Parameter.CHK_IdTYPE); // 证件类型指标
			String idTypeValue = sysbo.getIdTypeValue();
			String idTypeTempValue = "";
			
			ism = file.getInputStream();
			owb = WorkbookFactory.create(ism);
			String fieldSetName = "";
			for (int owbIndex =0 ;owbIndex<owb.getNumberOfSheets();owbIndex++){
				ArrayList valueList =new ArrayList();
				HashMap fieldMap = new HashMap();//key=//当前指标值：所在cell列数 value=FieldItem
				HashMap fieldIdex = new HashMap();
				StringBuffer keysb=new StringBuffer();
				ArrayList keyList =new ArrayList();
				ArrayList foreignkey=new ArrayList();
				StringBuffer a0100sb=new StringBuffer();
				StringBuffer nopriva0100=new StringBuffer();
				StringBuffer onefieldsetid =new StringBuffer();
				StringBuffer isNullField =new StringBuffer();
				StringBuffer isExistB0110 = new StringBuffer("false");
				//用于保存系统中的存在的人员但是姓名与模板中的姓名不一致的数据map中key为唯一性指标的值，value为姓名
				HashMap<String, String> diffa0101 =new HashMap<String, String>();
				Object[] maps = { fieldMap, valueList ,keysb,keyList,foreignkey,a0100sb,nopriva0100,onefieldsetid,isNullField, diffa0101, isExistB0110};
				osheet = owb.getSheetAt(owbIndex);
				String sheetName =osheet.getSheetName();
				//zhangh 2019-10-30 无需读取隐藏页,所以遇到隐藏页直接跳过
				if("hidden".equalsIgnoreCase(sheetName)){
					continue;
				}
				fieldSetName = sheetName;
				if (sheetName!=null&&sheetName.startsWith("hjehr_codeset_")){
					continue;
				}
				Row orow = osheet.getRow(0);//第一行标题
				if (orow == null) {
					continue;
				}
				int cols = orow.getPhysicalNumberOfCells();//总hang数
				int rows = osheet.getPhysicalNumberOfRows();//总行数
				String primarykey="";
				String primarykeyLabel ="";
				StringBuffer primaryvalue = new StringBuffer(",");
				boolean isA01 = false;//是否是主集
				int b=0;
				//姓名列（a0101）是模板中的第几列
				int a0101Index = -1;

				ArrayList<String> infoList = new ArrayList<String>();

				for (int c = 0; c < cols; c++) {//遍历列
					String itemid = "";
					String itemDesc = "";
					Cell cell = orow.getCell(c);
					if (cell != null) {
						itemid = cell.getCellComment().getString().getString().toLowerCase();
						itemDesc = cell.getStringCellValue();
					}
					String t[]=itemid.split("`");
					if( !isA01&&t.length>1&&"a01".equalsIgnoreCase(t[1])){
						isA01=true;
					}
					itemid=t[0];
					if("".equals(itemid))
						break;

					if("a0101".equalsIgnoreCase(itemid))
						a0101Index = c;

					if("b0110".equalsIgnoreCase(itemid))
						isExistB0110 = new StringBuffer("true");

					if("a0101".equalsIgnoreCase(itemid)&&t.length==1&&!isA01){
						b=1;
						continue;
					}
					FieldItem item = DataDictionary.getFieldItem(itemid, fieldsetid);
					//下载模板不选主集并且不选择唯一性指标，但系统参数有唯一性指标时，获取唯一性指标的信息
					if(t.length > 1 && !"foreignkey".equalsIgnoreCase(t[1]))
						item = DataDictionary.getFieldItem(itemid, "A01");

					if(t.length > 1 && item == null)
						throw new GeneralException("", ResourceFactory.getProperty("workbench.info.import.error.excel"), "", "");

					if(item==null){
						isNullField.append(itemDesc + ",");
						continue;
					}
					if(c==b){
						if(t.length==2){
							fieldsetid=t[1];
							onefieldsetid.append(fieldsetid);
							FieldSet fs= DataDictionary.getFieldSetVo(fieldsetid);
							fieldsetdesc=fs.getCustomdesc();
						}else{
							throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("workbench.info.import.error.excel")));
						}
						fieldIdex.put(itemid, new Integer(c));//指标映射的列数
						fieldMap.put(itemid, item);//指标编码映射的指标对象
						keyList.add(itemid);//
						primarykey=itemid;
						primarykeyLabel=item.getItemdesc();
					}else /*if(selectitemidlist.contains(itemid))*/{
						//selectitemidlist.remove(itemid);
						if(t.length==2&& "foreignkey".equalsIgnoreCase(t[1])){
							foreignkey.add(t[0]);
						}
						fieldIdex.put(itemid, new Integer(c));
						fieldMap.put(itemid, item);//当前指标值：所在cell列数
						keyList.add(itemid);
					}
				}

				//zhaogd 2013-12-2 验证唯一性指标有无超出长度范围，超出长度范围的提示出来，且不进行插入操作
				String userbase=(String)this.getFormHM().get("userbase");
				String sqlQLenght = "select * from "+userbase+"A01 where 1=2";
				this.frecset = dao.search(sqlQLenght);
				ResultSetMetaData metaData=this.frecset.getMetaData();
				int columnCount=metaData.getColumnCount();

				cols = keyList.size();
				String value="";
				double dvalue=0;
				HashMap<String, String> primaryMap = new HashMap<String, String>();
				DateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
				for (int j = 1; j < rows; j++) {
					orow = osheet.getRow(j);
					if (orow != null) {
						String a0101 = "";
						if(a0101Index != -1) {
							Cell cell = orow.getCell(a0101Index);
							if(cell != null) {
								a0101 = cell.getStringCellValue();
								a0101 = StringUtils.isEmpty(a0101) ? "" : a0101.trim();
							}
						}

						HashMap valueMap = new HashMap();//key=指标itemid value=值
						String primarykeyValue="";
						boolean islenghtover = false;
						String idCardValue = "";
						for(int n=0;n<cols;n++){
							String key=(String)keyList.get(n);
							FieldItem item= (FieldItem)fieldMap.get(key);
							if(item==null)
								continue;
							int idex = ((Integer)fieldIdex.get(key)).intValue();
							Cell cell = orow.getCell(idex);
							if (cell != null) {
								switch (cell.getCellType()) {
									case Cell.CELL_TYPE_FORMULA: {
									    primarykeyValue = checkData(j, n, cell, item, foreignkey, a0101, msgMap, 
										        RepeatPrimaryKey, a0101Index, primaryMap, infoList, isA01, 
										        valueMap, primarykeyValue);
										break;
									}
									case Cell.CELL_TYPE_NUMERIC: {
									    primarykeyValue = checkData(j, n, cell, item, foreignkey, a0101, msgMap, 
                                                RepeatPrimaryKey, a0101Index, primaryMap, infoList, isA01, 
                                                valueMap, primarykeyValue);
									    break;
									}
									case Cell.CELL_TYPE_STRING: {
									    primarykeyValue = checkData(j, n, cell, item, foreignkey, a0101, msgMap, 
                                                RepeatPrimaryKey, a0101Index, primaryMap, infoList, isA01, 
                                                valueMap, primarykeyValue);
										break;
									}
									default:{
									    primarykeyValue = checkData(j, n, cell, item, foreignkey, a0101, msgMap, 
                                                RepeatPrimaryKey, a0101Index, primaryMap, infoList, isA01, 
                                                valueMap, primarykeyValue);
									}
								}
								
								value = (String) valueMap.get(item.getItemid());
							}
							if(cell != null && n==0){
								int itemLength = 0;
								String columnName = "";
								for(int i=1;i<=columnCount;i++)
								{
									columnName=metaData.getColumnName(i).toLowerCase();
									itemLength = metaData.getColumnDisplaySize(i);
									if(primarykey.equalsIgnoreCase(columnName)){
										break;
									}
								}
								int pyLenght=0;
								//获取带汉字的字符串正确字节长度
								byte[]  primarykeybyte = primarykeyValue.getBytes();
								pyLenght = primarykeybyte.length;
								if(pyLenght>itemLength){
									islenghtover = true;
									ArrayList sb=(ArrayList)msgMap.get(primarykeyValue);
									if(sb!=null){
										int no=sb.size();
										sb.add((no+1)+".&nbsp;第" + (j + 1) + "行["+item.getItemdesc()+"]"+ResourceFactory.getProperty("workbench.info.import.error.intlength"));
									}else{
										sb= new ArrayList();
										sb.add("1.&nbsp;第" + (j + 1) + "行["+item.getItemdesc()+"]"+ResourceFactory.getProperty("workbench.info.import.error.intlength"));
									}
									msgMap.put(primarykeyValue, sb);
								}
							}

							if(StringUtils.isEmpty(primarykeyValue))
								break;
						}
						//校验身份证号是否有效
						if(StringUtils.isNotEmpty(idType)) {
							idTypeTempValue = (String) valueMap.get(idType);
						}
						/**
						 * 导入的指标中包含身份证指标，并且身份证指标的值不为空时，以下情况需要校验身份证号是否符合要求：
						 * 1.没有设置身份证类型指标
						 * 2.设置了身份证类型指标，但是身份证类型指标没有值
						 * 3.设置了身份证类型指标，身份证类型指标的值是和代码类中身份证对应的代码项id一致
						 */
						String idValue = (String) valueMap.get(chk);
						if(StringUtils.isNotEmpty(primarykeyValue) && StringUtils.isNotEmpty(chk) && StringUtils.isNotEmpty(idValue)
								&& "1".equalsIgnoreCase(chkvalid) && keyList.contains(chk) 
								&& (StringUtils.isEmpty(idType) || (StringUtils.isNotEmpty(idType) 
										&& (StringUtils.isEmpty(idTypeTempValue) 
												|| (StringUtils.isNotEmpty(idTypeTempValue) && idTypeTempValue.equalsIgnoreCase(idTypeValue)))))) {
							boolean flag = isValid(idValue);
							FieldItem item = DataDictionary.getFieldItem(chk, "A01");
							if(!flag) {
								ArrayList sb=(ArrayList)msgMap.get(primarykeyValue);
								if(sb!=null){
									int no=sb.size();
									sb.add((no+1)+".&nbsp;第" + (j + 1) + "行["+item.getItemdesc()+"]" + ResourceFactory.getProperty("workbench.info.import.error.idcard"));
								}else{
									sb= new ArrayList();
									sb.add("1.&nbsp;第" + (j + 1) + "行["+item.getItemdesc()+"]" + ResourceFactory.getProperty("workbench.info.import.error.idcard"));
								}
								idValue="```";
								msgMap.put(primarykeyValue, sb);
								valueMap.put(chk, idValue);
							}
							
							idCardValue = value;
						}

						if(!"".equals(primarykeyValue) && !islenghtover
								&& (!keyList.contains(chk) || (keyList.contains(chk) && !"```".equals(idCardValue)))){//主键为空就不提取此行记录
							//缺陷3876 zgd 2014-8-20 主键有效时，才加入
							keysb.append(primarykeyValue+"','");
							valueList.add(valueMap);
							//判断模板中是否存在重复数据，若存在给出提示并记录重复数据的数量
							if(!"A01".equalsIgnoreCase(fieldsetid)){
								HashMap<String, String> map = repeatMap.get(primarykeyValue);
								StringBuffer foreignkeyValues = new StringBuffer();
								StringBuffer tempsb = new StringBuffer();
								if(foreignkey != null && foreignkey.size() > 0){
									tempsb.append(".&nbsp;模板中" + fieldsetdesc
											+ ResourceFactory.getProperty("workbench.info.import.error.haveset1"));
									for(int i = 0; i < foreignkey.size(); i++){
										String itemid = (String) foreignkey.get(i);
										String itemidValue = (String) valueMap.get(itemid);
										if(itemidValue==null||"".equals(itemidValue))
											continue;

										if("```".equals(itemidValue))
											continue;

										FieldItem item = DataDictionary.getFieldItem(itemid);
										if("M".equals(item.getItemtype()))
											continue;

										foreignkeyValues.append(itemidValue + ";");
										if(!"0".equals(item.getCodesetid()))
											value=AdminCode.getCodeName(item.getCodesetid(), value);

										tempsb.append("["+item.getItemdesc()+"]="+itemidValue + "&nbsp;");
									}

									tempsb.append(ResourceFactory.getProperty("workbench.info.import.error.haveset2"));
								} else {
									for(int i=0;i<cols;i++){
										String itemid = (String) keyList.get(i);
										String itemidValue = (String) valueMap.get(itemid);
										if(itemidValue==null||"".equals(itemidValue))
											continue;

										if("```".equals(itemidValue))
											continue;

										FieldItem item = DataDictionary.getFieldItem(itemid);
										if("M".equals(item.getItemtype()))
											continue;

										foreignkeyValues.append(itemidValue + ";");
									}
									tempsb.append(".&nbsp;模板中"
											+ fieldsetdesc+ResourceFactory.getProperty("workbench.info.import.error.haveset"));
								}

								if(map != null && StringUtils.isNotEmpty(foreignkeyValues.toString())){
									if(map.containsKey(foreignkeyValues.toString())){
										String sum = map.get(foreignkeyValues.toString());
										sum = (Integer.valueOf(sum) + 1) + "";
										map.put(foreignkeyValues.toString(), sum);
										ArrayList sb=(ArrayList)msgMap.get(primarykeyValue);
										if(sb!=null){
											int no=sb.size();
											sb.add((no+1)+ tempsb.toString());
										}else{
											sb= new ArrayList();
											sb.add("1" + tempsb.toString());
										}
										msgMap.put(primarykeyValue, sb);
										RepeatPrimaryKey = "1";
									} else {
										map.put(foreignkeyValues.toString(), "1");
									}
								} else {
									map = new HashMap<String, String>();
									map.put(foreignkeyValues.toString(), "1");
									repeatMap.put(primarykeyValue, map);
								}

							}
						}
					}
				}

				if(StringUtils.isNotEmpty(isNullField.toString())) {
					String nullFieldNames = isNullField.toString();
					isNullField.setLength(0);
					isNullField.append(fieldSetName + ":");
					isNullField.append(nullFieldNames);

				}
				maps[10] = isExistB0110;
				mapsList.add(maps);

				personMap.put(fieldsetid.toLowerCase(), infoList);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("workbench.info.import.error.excel")));
		}finally{
			PubFunc.closeResource(owb);
			PubFunc.closeIoResource(ism);
		}
		this.getFormHM().put("RepeatPrimaryKey", RepeatPrimaryKey);
		return mapsList;
	}
	
	private String createNumberPattern(FieldItem item){	
		int length = item.getItemlength();
		int delength = item.getDecimalwidth();
		
		String pattern = "#";
		StringBuilder v = new StringBuilder();
		for(;length>0;length--)
			pattern+="#";
		if(delength>0)
		    pattern+=".";	
		for(;delength>0;delength--)
			pattern+="#";
		
		
		return pattern;
	}
	
	
	private String checkdate(String str){
        str = StringUtils.isEmpty(str) ? "" : str.replace("/", "-");
        if(str.indexOf("日") > -1)
            str = str.replace(" ", "");
        
        String dateStr ="false";
        if(str.length()<4)
            dateStr = "false";
        else if(str.length()==4){
            Pattern p= Pattern.compile("^(\\d{4})$");
            Matcher m = p.matcher(str);
            if(m.matches())
                dateStr = str+"-01-01";
            else
                dateStr = "false";
        } else if(str.length()<6){
            Pattern p= Pattern.compile("^(\\d{4})年$");
            Matcher m = p.matcher(str);
            if(m.matches())
                dateStr = str.replace("年", "-")+"01-01";
            else
                dateStr = "false";
        } else if(str.length()==7){
            if(str.indexOf("月")!=-1){
                Pattern p= Pattern.compile("^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])[-.月]$");
                Matcher m = p.matcher(str);
                if(m.matches()){
                    if(str.indexOf("月")!=-1)
                        dateStr = str.replace("年", "-").replace(".", "-").replace("月", "-")+"01";
                    else
                        dateStr = str.replace("年", "-").replace(".", "-")+"-01";
                }else
                    dateStr = "false";
            }else{
                Pattern p= Pattern.compile("^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])$");
                Matcher m = p.matcher(str);
                if(m.matches())
                    dateStr = str.replace("年", "-").replace(".", "-")+"-01";
                else
                    dateStr = "false";
            }
        } else if(str.length()<8){//2010年3  2010年3月
            Pattern p= Pattern.compile("^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])[-.月]*$");
            Matcher m = p.matcher(str);
            if(m.matches()){
                if(str.indexOf("月")!=-1)
                    dateStr = str.replace("年", "-").replace(".", "-").replace("月", "-")+"01";
                else
                    dateStr = str.replace("年", "-").replace(".", "-")+"-01";
            }else
                dateStr = "false";
        } else if(str.length()==8){//2010年3  2010年3月1
            Pattern p= Pattern.compile("^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])[-.月]([0]*\\d{1}|[12]\\d{1}|3[01])*$");
            Matcher m = p.matcher(str);
            if(m.matches()){
                str =str.replace("年", "-").replace(".", "-").replace("月", "-");
                if(str.lastIndexOf("-")==str.length()){
                    if(str.length()<10)
                        dateStr = str+"01";
                } else {
                    String[] temps=str.split("-");
                    if(temps.length>2)
                        dateStr = checkMothAndDay(str);
                    else
                        dateStr = "false";
                }
            }else{
                dateStr = "false";
            }
        } else if(str.length() <= 11) {//2017年1月1日
            Pattern p= Pattern.compile("^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])[-.月]([0]*\\d{1}|[12]\\d{1}|3[01])[日]*$");
            Matcher m = p.matcher(str);
            if(m.matches()){
                String temp=str.replace("年", "-").replace(".", "-").replace("月", "-").replace("日", "");
                dateStr = checkMothAndDay(temp);
            } else
                dateStr = "false";
            
        } else {//2017年1月1日1时1分      2017年1月1日1时1分1秒 
            str = str.replace("时", ":").replace("分", ":");
            if(str.endsWith(":"))
                str = str.substring(0, str.length() - 1);
            
            Pattern p = null;
            if(str.split(":").length < 3)
                p= Pattern.compile("^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])[-.月]([0]*\\d{1}|[12]\\d{1}|3[01])[\\s日]([01]*\\d{1}|2[0-3])[:时]([0-5]*\\d{1})[:分]*$");
            else
                p= Pattern.compile("^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])[-.月]([0]*\\d{1}|[12]\\d{1}|3[01])[\\s日]([01]*\\d{1}|2[0-3])[:时]([0-5]*\\d{1})[:分]([0-5]*\\d{1})[秒]*$");
                
            Matcher m = p.matcher(str);
            if(m.matches()){
                String tempDate=str.replace("年", "-").replace(".", "-").replace("月", "-").replace("日", " ");
                String temp = tempDate.split(" ")[0];
                dateStr = checkMothAndDay(temp);
                if(!"false".equalsIgnoreCase(dateStr)) {
                    String tempTime = tempDate.split(" ")[1];
                    dateStr += " " + tempTime;
                }
            } else
                dateStr = "false";
        }
        
        if(!"false".equals(dateStr))
            dateStr = formatDate(dateStr);
        
        return dateStr;
    }
	/**
     * 校验月与日是否符合规则
     * @param date 日期数据
     * @return
     */
    private String checkMothAndDay(String date){
        String tempDate = "false";
        String[] dates = date.split("-");
        if(dates[0].length()>0&&dates[1].length()>0&&dates[2].length()>0){
            int year = Integer.parseInt(dates[0]);
            int month=Integer.parseInt(dates[1]);
            int day=Integer.parseInt(dates[2]);
            switch(month){
                case 1:
                case 3:
                case 5:
                case 7:
                case 8:
                case 10:
                case 12:{
                    if(1<=day&&day<=31)
                        tempDate=date;
                    
                    break;
                }
                case 4:
                case 6:
                case 9:
                case 11: {
                    if(1<=day&&day<=30)
                        tempDate=date;
                    
                    break;
                }
                case 2: {
                    if(isLeapYear(year)){
                        if(1<=day&&day<=29)
                            tempDate=date;
                        
                    }else{
                        if(1<=day&&day<=28)
                            tempDate=date;
                    }
                    break;
                }
            }
        }
        return tempDate;
    }
	
	private String splitString(String source, int len)
	  {
	    byte[] bytes = source.getBytes();
	    int bytelen = bytes.length;
	    int j = 0;
	    int rlen = 0;
	    if (bytelen <= len)
	      return source;

	    for (int i = 0; i < len; ++i)
	    {
	      if (bytes[i] < 0)
	        ++j;
	    }
	    if (j % 2 == 1)
	      rlen = len - 1;
	    else
	      rlen = len;
	    byte[] target = new byte[rlen];
	    System.arraycopy(bytes, 0, target, 0, rlen);
	    String dd = new String(target);
	    return dd;
	  }
	
	private void initPriv(){
		if(this.userView.isSuper_admin()){
			this.privM="admin";
			return;
		}
		String codesetid=this.userView.getManagePrivCode();
		if("UN".equalsIgnoreCase(codesetid)){
			this.privM="b0110";
		}else if("UM".equalsIgnoreCase(codesetid)){
			this.privM="e0122";
		}else if("@K".equalsIgnoreCase(codesetid)){
			this.privM="e01a1";
		}
		this.privMv=this.userView.getManagePrivCodeValue().toUpperCase();
	}
	
	private void doPrivFields(){
		ArrayList privfieldList = this.userView.getPrivFieldList(this.fieldsetid);
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<privfieldList.size();i++){
			FieldItem item=(FieldItem)privfieldList.get(i);
			if(item.getPriv_status()==2){
				sb.append(item.getItemid()+",");
			}
		}
		this.privFields=sb.toString().toUpperCase();
	}
	
	private void doOrgCheck(ArrayList valueList, ContentDAO dao, HashMap<String, ArrayList<String>> msgMap,
	        String primarykey) throws Exception{
		Pattern p = Pattern.compile("[A-Z0-9]*");
		for(int m = valueList.size() -1; m >= 0; m--){
			HashMap valuemap=(HashMap)valueList.get(m);
			String value="";
			String b0110=(String)valuemap.get("b0110");
			String primarykeyValue = (String)valuemap.get(primarykey);
			if(StringUtils.isNotEmpty(b0110)&&b0110.endsWith(")")){
				value=b0110.substring(b0110.lastIndexOf("(")+1, b0110.lastIndexOf(")"));
				Matcher ma=p.matcher(value);
				if(ma.matches()){
					valuemap.put("b0110", value);
					b0110=value;
				}
			} else if(StringUtils.isNotEmpty(b0110)) {
				value = this.findOrgCode("UN", b0110, "", "");
				if(StringUtils.isEmpty(value)) {
				    ArrayList sb=(ArrayList)msgMap.get(primarykeyValue);
                    if(sb == null)
                        sb= new ArrayList();
                    
                    int no=sb.size();
                    sb.add((no+1)+".&nbsp;" + ResourceFactory.getProperty("batchimport.info.error.unitmsg"));
                    msgMap.put(primarykeyValue, sb);
                    valueList.remove(m);
                    continue;
				}
				
				valuemap.put("b0110", value);
				b0110=value;
			}
			
			String e0122=(String)valuemap.get("e0122");
			if(StringUtils.isNotEmpty(e0122)&&e0122.endsWith(")")){
				value=e0122.substring(e0122.lastIndexOf("(")+1, e0122.lastIndexOf(")"));
				Matcher ma=p.matcher(value);
				if(ma.matches()){
					valuemap.put("e0122", value);
					e0122=value;
				}
			} else if(StringUtils.isNotEmpty(e0122)) {
				value = this.findOrgCode("UM", e0122, b0110, "");
				if(StringUtils.isEmpty(value)) {
                    ArrayList sb=(ArrayList)msgMap.get(primarykeyValue);
                    if(sb == null)
                        sb = new ArrayList();
                    
                    int no=sb.size();
                    sb.add((no+1)+".&nbsp;" + ResourceFactory.getProperty("batchimport.info.error.departmentmsg"));
                    msgMap.put(primarykeyValue, sb);
                    valueList.remove(m);
                    continue;
                }
				
				valuemap.put("e0122", value);
				e0122 = value;
			}
			
			String e01a1=(String)valuemap.get("e01a1");
			if(StringUtils.isNotEmpty(e01a1)&&e01a1.endsWith(")")){
				value=e01a1.substring(e01a1.lastIndexOf("(")+1, e01a1.lastIndexOf(")"));
				Matcher ma=p.matcher(value);
				if(ma.matches()){
					valuemap.put("e01a1", value);
					e01a1=value;
				}
			} else if(StringUtils.isNotEmpty(e01a1)){
				value = this.findOrgCode("@K", e01a1, b0110, e0122);
				if(StringUtils.isEmpty(value)) {
                    ArrayList sb=(ArrayList)msgMap.get(primarykeyValue);
                    if(sb==null)
                        sb= new ArrayList();
                    
                    int no=sb.size();
                    sb.add((no+1)+".&nbsp;" + ResourceFactory.getProperty("batchimport.info.error.positionmsg"));
                    msgMap.put(primarykeyValue, sb);
                    valueList.remove(m);
                    continue;
                }
				
				valuemap.put("e01a1", value);
				e01a1 = value;
			}
			
			if(StringUtils.isNotEmpty(b0110)){
				if(StringUtils.isNotEmpty(e0122)){
					if(checkUNUM(b0110,e0122,dao)){
						valuemap.put("e0122", "");
						valuemap.put("e01a1", "");
						e01a1="";
					}
					
					if(StringUtils.isNotEmpty(e01a1)){
						if(checkUMKK(e0122,e01a1,dao)){
							valuemap.put("e01a1", "");
						}
					}
				}else{
				    if (StringUtils.isNotEmpty(e01a1)) {
    				    CodeItem item = AdminCode.getCode("@K", e01a1);
    				    //当部门为空时，检查是不是挂在单位下的岗位
    				    if (item == null || !item.getPcodeitem().equals(b0110)) {
    				        valuemap.put("e01a1", "");
    				    }
				    }
				}
			} 
		}
	}
	
	private void doUNUMKCheck(ArrayList valueList,HashMap fieldMap,ContentDAO dao)throws Exception{
		Pattern p = Pattern.compile("[A-Z0-9]*");
		for(int m=0;m<valueList.size();m++){
			HashMap valuemap=(HashMap)valueList.get(m);
			String value="";
			for(Iterator i=valuemap.keySet().iterator();i.hasNext();){
				String itemid = (String)i.next();
				if(!"b0110".equalsIgnoreCase(itemid)&&!"e0122".equalsIgnoreCase(itemid)&&!"e01a1".equalsIgnoreCase(itemid)){
					FieldItem item=(FieldItem)fieldMap.get(itemid);
					if(item!=null){
						if("UN".equalsIgnoreCase(item.getCodesetid())||"UM".equalsIgnoreCase(item.getCodesetid())||"@K".equalsIgnoreCase(item.getCodesetid())){
							String b0110=(String)valuemap.get(itemid);
							if(b0110!=null&&b0110.endsWith(")")){
								value=b0110.substring(b0110.lastIndexOf("(")+1, b0110.lastIndexOf(")"));
								Matcher ma=p.matcher(value);
								if(ma.matches()){
									valuemap.put(itemid, value);
								}
							}
						}
					}
				}
			}
		}	
	}
	private boolean checkUNUM(String b0110,String e0122,ContentDAO dao)throws Exception{
		boolean flag=false;
		Pattern p = Pattern.compile("[A-Z0-9]*");
		Matcher ma=p.matcher(b0110);//支持直接输入机构编码，可解决机构名重复的问题
		String unCodeitemid="";
		String umCodeitemid="";
		if(ma.matches()){
			unCodeitemid=b0110;
		}else{
			this.frecset = dao.search("select codeitemid from organization where codeitemdesc='"+b0110+"' and codesetid='UN'");
			if(this.frecset.next()){
				unCodeitemid=this.frecset.getString("codeitemid");
			}else{
				flag=true;
			}
		}
		ma=p.matcher(e0122);
		if(ma.matches()){
			umCodeitemid=e0122;
		}else{
			this.frecset = dao.search("select codeitemid from organization where codeitemdesc='"+e0122+"' and codesetid='UM'");
			if(this.frecset.next()){
				umCodeitemid=this.frecset.getString("codeitemid");
			}else{
				flag=true;
			}
		}
		if(!"".equals(unCodeitemid)&&!"".equals(umCodeitemid)){
			flag=this.dochildchecknm(unCodeitemid, umCodeitemid,dao);
		}
		return flag;
	}
	private boolean checkUMKK(String e0122,String e01a1,ContentDAO dao)throws Exception{
		boolean flag=false;
		Pattern p = Pattern.compile("[A-Z0-9]*");
		Matcher ma=p.matcher(e0122);//支持直接输入机构编码，可解决机构名重复的问题
		String kkCodeitemid="";
		String umCodeitemid="";
		if(ma.matches()){
			umCodeitemid=e0122;
		}else{
			this.frecset = dao.search("select codeitemid from organization where codeitemdesc='"+e0122+"' and codesetid='UM'");
			if(this.frecset.next()){
				umCodeitemid=this.frecset.getString("codeitemid");
			}else{
				flag=true;
			}
		}
		ma=p.matcher(e0122);
		if(ma.matches()){
			kkCodeitemid=e01a1;
		}else{
			this.frecset = dao.search("select codeitemid from organization where codeitemdesc='"+e01a1+"' and codesetid='@K'");
			if(this.frecset.next()){
				kkCodeitemid=this.frecset.getString("codeitemid");
			}else{
				flag=true;
			}
		}
		if(!"".equals(umCodeitemid)&&!"".equals(kkCodeitemid)){
			flag=this.dochildcheckmk(umCodeitemid, kkCodeitemid,dao);
		}
		return flag;
	}
	
	private boolean dochildchecknm(String unCodeitemid,String umCodeitemid,ContentDAO dao)throws Exception{
		RowSet rs=null;
		boolean flag=true;
		String sql = "select codeitemid from organization where codesetid<>'@K' and parentid='"+unCodeitemid+"' and parentid<>codeitemid";
		try{
			rs = dao.search(sql);
			String codeitemid="";
			while(rs.next()){
				codeitemid=rs.getString("codeitemid");
				if(umCodeitemid.equalsIgnoreCase(codeitemid)){
					flag=false;
					break;
				}else if(umCodeitemid.indexOf(codeitemid)!=-1){
					flag=this.dochildchecknm(codeitemid, umCodeitemid, dao);
					if(!flag)//如果返回false，说明下级匹配成功，不再继续，跳出   guodd 14-12-15
						break;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(rs!=null){
				rs.close();
			}
		}
		return flag;
	}
	private boolean dochildcheckmk(String umCodeitemid,String kkCodeitemid,ContentDAO dao)throws Exception {
		RowSet rs=null;
		boolean flag=true;
		String sql = "select codeitemid from organization where codesetid<>'UN' and parentid='"+umCodeitemid+"' and parentid<>codeitemid";
		try{
			rs = dao.search(sql);
			String codeitemid="";
			while(rs.next()){
				codeitemid=rs.getString("codeitemid");
				if(kkCodeitemid.equalsIgnoreCase(codeitemid)){
					flag=false;
					return flag;
				}else if(kkCodeitemid.indexOf(codeitemid)!=-1){
					flag=this.dochildchecknm(codeitemid, kkCodeitemid, dao);
					if(!flag)//如果返回false，说明下级匹配成功，不再继续，跳出   guodd 14-12-15
						break;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(rs!=null){
				rs.close();
			}
		}
		return flag;
	}
	
	/**
	 *  闰年的条件是：
	 *	① 能被4整除，但不能被100整除；
	 *	② 能被100整除，又能被400整除。
	 * @param year
	 * @return
	 */
	private boolean isLeapYear(int year){
		boolean t=false;
		if(year%4==0){
			   if(year%100!=0){
				   t=true;
			   }else if(year%400==0){
				   t=true;
			   }
		  }
		return t;
	}
	
	/**
	 * 打开组织机构数据集，供循环校验excel中组织机构数据使用
	 * @param dao
	 */
	private void openOrgRowSet(ContentDAO dao) {
		if (orgRowSet != null)
			return;
		
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select codeitemdesc,codeitemid,codesetid,parentid from organization");
			sql.append(" where "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date");
			//【55167】批量导入的时候对导入的单位、部门、岗位进行权限过滤，目前不支持高级条件
			String pivCodeValue = this.userView.getManagePrivCodeValue();
			if(!this.userView.isSuper_admin() && !"UN`".equalsIgnoreCase(pivCodeValue)) {
				if(StringUtils.isEmpty(pivCodeValue) || "UN".equalsIgnoreCase(pivCodeValue)) {
					sql.append(" and 1=2");
				} else {
					sql.append(" and codeitemid like '" + pivCodeValue + "%'");
				}
			}
			
			this.orgRowSet = dao.search(sql.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * 根据给定的值从组织机构数据集中寻找对应记录，找到后，返回机构代码
	 * 代码相同或名称相同,并且与b0110、e0122一致
	 * @param codeSetId
	 * @param orgValue
	 * @return 
	 */
	private String findOrgCode(String codeSetId, String orgValue, String b0110, String e0122) {
		String orgCode = "";
		
		if(StringUtils.isEmpty(codeSetId) || StringUtils.isEmpty(orgValue))
			return orgCode;
		
		//单位是空，部门、岗位不导入
    	if(!"UN".equalsIgnoreCase(codeSetId) && StringUtils.isEmpty(b0110))
    		return orgCode;
    	
		try {
			if (this.orgRowSet == null)
				return orgCode;
		
			this.orgRowSet.beforeFirst();
			while(this.orgRowSet.next()) {
				String setId = this.orgRowSet.getString("codesetid");
				String itemid = this.orgRowSet.getString("codeitemid");
				String itemdesc = this.orgRowSet.getString("codeitemdesc");
				String parentid = this.orgRowSet.getString("parentid");
				
				if(!codeSetId.equalsIgnoreCase(setId))
					continue;
				
				if((!orgValue.equalsIgnoreCase(itemid) && !orgValue.equalsIgnoreCase(itemdesc))) 
					continue;
				
				if("UN".equalsIgnoreCase(codeSetId)) {
					orgCode = itemid;
					break;
				}
		    	
			    if("UM".equalsIgnoreCase(codeSetId)) {
			    	//父节点不是当前单位，说明是其它单位下的同名部门，则继续找
			    	if(!parentid.equalsIgnoreCase(b0110)) {
			    	    if(parentid.startsWith(b0110)) {
			    	      boolean flag = getParentid(codeSetId, parentid, b0110);
			    	      if(!flag)
			    	          continue;
			    	      
			    	    } else
			    	        continue;
			    	}
			    	
			    	orgCode = itemid;
					break;
			    }
			    
			    if("@K".equalsIgnoreCase(codeSetId)) {
			    	if(StringUtils.isNotEmpty(e0122)) {
				    	//父节点不是当前部门，说明是其它机构下的同名岗位，则继续找
			    	    if(!parentid.equalsIgnoreCase(e0122)) {
                            if(parentid.startsWith(e0122)) {
                              boolean flag = getParentid(codeSetId, parentid, e0122);
                              if(!flag)
                                  continue;
                              
                            } else
                                continue;
                        }
				    	
				    	orgCode = itemid;
						break;
			    	} else {
			    		//当前部门为空，岗位是直接挂在当前单位下的
			    	    if(!parentid.equalsIgnoreCase(b0110)) {
                            if(!parentid.equalsIgnoreCase(b0110)) {
                                if(parentid.startsWith(b0110)) {
                                    boolean flag = getParentid(codeSetId, parentid, b0110);
                                    if(!flag)
                                        continue;
                                    
                                } else
                                    continue;
                            }
                        }
                        
                        orgCode = itemid;
                        break;
			    	}
			    }
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return orgCode;

	}
	
	private boolean getParentid (String codesetid, String codeitemid, String orgId) {
	    boolean flag = false;
	    CodeItem codeItem = AdminCode.getCode(codesetid, codeitemid);
	    if(codeItem != null && codeItem.getPcodeitem().equalsIgnoreCase(orgId))
	        flag = true;
	    else if(codeItem != null && codeItem.getPcodeitem().startsWith(orgId))
	        flag = getParentid (codesetid, codeItem.getPcodeitem(), orgId);
	        
        return flag;
	}
	/**
     * 将日期数据1900-1-1 1:1:1转换成1900-01-01 01:01:01
     * @param date 校验完成的数据
     * @return
     */
    private String formatDate(String date) {
        String newDate = "";
        String[] dates = date.split(" ");
        String year = dates[0].split("-")[0];
        String month = dates[0].split("-")[1];
        month = Integer.parseInt(month) < 10 && month.length() == 1 ? "0" + month : month;
        String day = dates[0].split("-")[2];
        day = Integer.parseInt(day) < 10 && day.length() == 1 ? "0" + day : day;
        newDate = year + "-" + month + "-" + day;
        
        if(dates.length == 2) {
            String[] oldTime = dates[1].split(":");
            String hour = oldTime[0];
            hour = Integer.parseInt(hour) < 10 && hour.length() == 1 ? "0" + hour : hour;
            newDate += " " + hour;
            if(oldTime.length > 1) {
                String min = oldTime[1];
                min = Integer.parseInt(min) < 10 && min.length() == 1 ? "0" + min : min;
                newDate += ":" + min;
            }
            
            if(oldTime.length > 2) {
                String second = oldTime[2];
                second = Integer.parseInt(second) < 10 && second.length() == 1 ? "0" + second : second;
                newDate += ":" + second;
            }
        }
            
        return newDate;
    }
	/**
	 * 身份证验证
	 *
	 * @param id
	 *            号码内容
	 * @return 是否有效
	 */
    private boolean isValid(String id) {
		if (id == null)
			return false;

		int len = id.length();
		if (len != 15 && len != 18)
			return false;

		// 校验区位码
		if (!validCityCode(id.substring(0, 2)))
			return false;

		// 校验生日
		if (!validDate(id))
			return false;

		if (len == 15)
			return true;

		// 校验位数
		return validParityBit(id);

	}

	/**
	 * 18位身份证号校验
	 * 
	 * @param id
	 *            身份证号
	 * @return
	 */
	private boolean validParityBit(String id) {
		/**
		 * 效验码
		 */
		char[] PARITYBIT = { '1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2' };

		/**
		 * 加权因子 Math.pow(2, i - 1) % 11
		 */
		int[] POWER = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };
		char[] cs = id.toUpperCase().toCharArray();
		int power = 0;
		for (int i = 0; i < cs.length; i++) {
			// 最后一位可以是X
			if (i == cs.length - 1 && cs[i] == 'X')
				break;

			// 非数字
			if (cs[i] < '0' || cs[i] > '9')
				return false;

			// 加权求和
			if (i < cs.length - 1) {
				power += (cs[i] - '0') * POWER[i];
			}
		}
		return PARITYBIT[power % 11] == cs[cs.length - 1];
	}

	/**
	 * 校验生日
	 * 
	 * @param id
	 *            身份证号
	 * @return
	 */
	private boolean validDate(String id) {
		try {
			String birth = id.length() == 15 ? "19" + id.substring(6, 12) : id.substring(6, 14);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			Date birthDate = sdf.parse(birth);
			if (!birth.equals(sdf.format(birthDate)))
				return false;
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * 校验区位码
	 * 
	 * @param cityCode
	 *            区位码
	 * @return
	 */
	private static boolean validCityCode(String cityCode) {
		/**
		 * <pre>
		 * 省、直辖市代码表：
		 *     11 : 北京  12 : 天津  13 : 河北   14 : 山西  15 : 内蒙古
		 *     21 : 辽宁  22 : 吉林  23 : 黑龙江 31 : 上海  32 : 江苏
		 *     33 : 浙江  34 : 安徽  35 : 福建   36 : 江西  37 : 山东
		 *     41 : 河南  42 : 湖北  43 : 湖南   44 : 广东  45 : 广西  46 : 海南
		 *     50 : 重庆  51 : 四川  52 : 贵州   53 : 云南  54 : 西藏
		 *     61 : 陕西  62 : 甘肃  63 : 青海   64 : 宁夏  65 : 新疆
		 *     71 : 台湾
		 *     81 : 香港  82 : 澳门
		 *     91 : 国外
		 * </pre>
		 */
		String CITY_CODE[] = { "11", "12", "13", "14", "15", "21", "22", "23", "31", "32", "33", "34", "35", "36", "37",
				"41", "42", "43", "44", "45", "46", "50", "51", "52", "53", "54", "61", "62", "63", "64", "65", "71",
				"81", "82", "91" };
		for (String code : CITY_CODE) {
			if (code.equals(cityCode))
				return true;
		}
		return false;
	}

    /**
     * 获取模板中单元格的数据并进行校验
     * 
     * @param rowIndex
     *            第几行
     * @param columnIndex
     *            第几列
     * @param cell
     *            单元格对象
     * @param fi
     *            指标对象
     * @param foreignkey
     *            子集的主键
     * @param a0101
     *            姓名
     * @param msgMap
     *            异常信息
     * @param RepeatPrimaryKey
     *            是否存在重复主键标识
     * @param a0101Index
     *            姓名所在的列
     * @param primaryMap
     *            主键对象
     * @param infoList
     *            主键与姓名对应的list
     * @param isA01
     *            是否是主集
     * @param valueMap
     *            模板中导入的数据
     * @param primarykeyValue
     *            主键的值
     */
    private String checkData(int rowIndex, int columnIndex, Cell cell, FieldItem fi, ArrayList foreignkey, String a0101,
            HashMap<String, ArrayList<String>> msgMap, String RepeatPrimaryKey, int a0101Index,
            HashMap<String, String> primaryMap, ArrayList<String> infoList, boolean isA01, HashMap valueMap,
            String primarykeyValue) {
        String value = "";
        switch (cell.getCellTypeEnum()) {
            case STRING: {
                value = cell.getStringCellValue();
                break;
            }
            case FORMULA: {
                value = cell.getStringCellValue();
                break;
            }
            case NUMERIC: {
                if ("D".equals(fi.getItemtype())) {
                    Date date = cell.getDateCellValue();
                    String pattern = "yyyy-MM-dd";
                    if(fi.getItemlength() == 4) {
                        pattern = "yyyy";
                    } else if(fi.getItemlength() == 7) {
                        pattern = "yyyy-MM";
                    } else if(fi.getItemlength() == 13) {
                        pattern = "yyyy-MM-dd HH";
                    } else if(fi.getItemlength() == 16) {
                        pattern = "yyyy-MM-dd HH:mm";
                    } else if(fi.getItemlength() >= 18) {
                        pattern = "yyyy-MM-dd HH:mm:ss";
                    }
                    
                    value = DateUtils.format(date, pattern);
                } else {
                    value = PubFunc.round(String.valueOf(cell.getNumericCellValue()), fi.getDecimalwidth());
                }
                
                break;
            }
            default: {
                value = cell.getStringCellValue();
            }
        }
        
        if(StringUtils.isNotEmpty(value)) {
        	value = value.replace("~", "");
        }
        
        if ("N".equals(fi.getItemtype())) {
            if ((value + " ").split("\\.").length > 2) {// 如果存在两个.则也提示为无效数值 xuj add 2015-1-9
                ArrayList sb = (ArrayList) msgMap.get(primarykeyValue);
                if (sb == null) {
                    sb = new ArrayList<String>();
                }
                
                sb.add((sb.size() + 1) + ".&nbsp;第" + (rowIndex + 1) + "行[" + fi.getItemdesc() + "]"
                        + ResourceFactory.getProperty("workbench.info.import.error.inttype") + value);
                msgMap.put(primarykeyValue, sb);
                value = "```";
            } else {
                Pattern p = Pattern.compile("[+-]?[\\d.]*");
                Matcher m = p.matcher(value);
                if (!m.matches()) {
                    ArrayList sb = (ArrayList) msgMap.get(primarykeyValue);
                    if (sb == null) {
                        sb = new ArrayList<String>();
                    }
                    
                    sb.add((sb.size() + 1) + ".&nbsp;第" + (rowIndex + 1) + "行[" + fi.getItemdesc() + "]"
                            + ResourceFactory.getProperty("workbench.info.import.error.inttype") + value);
                    value = "```";
                    msgMap.put(primarykeyValue, sb);
                } else {
                    if ("N".equals(fi.getItemtype())) {
                        int dw = fi.getDecimalwidth();
                        if (dw == 0) {
                            if (value.indexOf('.') != -1)
                                value = value.substring(0, value.indexOf('.'));
                        } else {
                            int il = fi.getItemlength();// zgd 2014-7-7 数值型指标长度限制去除
                            int intValueLength = 0;
                            if (value.indexOf('.') != -1) {
                                value = PubFunc.round(value, dw);
                            } else {
                                intValueLength = value.length();
                            }
                            if (intValueLength > il) {
                                ArrayList sb = (ArrayList) msgMap.get(primarykeyValue);
                                if (sb == null) {
                                    sb = new ArrayList<String>();
                                }
                                
                                sb.add((sb.size() + 1) + ".&nbsp;第" + (rowIndex + 1) + "行" + fi.getItemdesc()
                                + "指标中值长度超过指标长度！");
                                msgMap.put(primarykeyValue, sb);
                                value = "```";
                            }
                        }
                        value = value.replaceAll("\\+", "");
                    }
                }
            }
        } else if ("D".equals(fi.getItemtype())) {
            if (!"".equals(value)) {
                String tmp = checkdate(value);
                if ("false".equals(tmp)) {
                    ArrayList sb = (ArrayList) msgMap.get(primarykeyValue);
                    if (sb == null) {
                        sb = new ArrayList<String>();
                    }
                    
                    sb.add((sb.size() + 1) + ".&nbsp;第" + (rowIndex + 1) + "行[" + fi.getItemdesc() + "]"
                            + ResourceFactory.getProperty("workbench.info.import.error.datetype") + value);
                    value = "```";
                    msgMap.put(primarykeyValue, sb);
                } else {
                    value = tmp;
                }
            }
        }

        valueMap.put(fi.getItemid(), value);
        if (columnIndex == 0) {
            primarykeyValue = value;
            if(StringUtils.isEmpty(primarykeyValue)) {
                return primarykeyValue;
            }
            
            primarykeyValue = primarykeyValue.replaceAll("['|‘|’]", "");// tianye update 去掉单引号 影响sql
            if (a0101Index > -1)
                infoList.add(primarykeyValue + "=" + (StringUtils.isEmpty(a0101) ? " " : a0101));

            if (isA01 && primaryMap.containsKey(primarykeyValue)) {// zgd 2014-2-13 判断导入主集的Excel中有无重复记录，提示出来。
                RepeatPrimaryKey = "1";
                ArrayList sb = (ArrayList) msgMap.get(primarykeyValue);
                if (sb == null) {
                    sb = new ArrayList<String>();
                }
                
                sb.add((sb.size() + 1) + ".&nbsp;第" + (rowIndex + 1) + "行"
                        + ResourceFactory.getProperty("workbench.info.import.error.sameprimary"));
                msgMap.put(primarykeyValue, sb);
            }
            primaryMap.put(primarykeyValue, "1");
        }
        
        if (foreignkey != null && foreignkey.contains(fi.getItemid())
                && (value == null || "".equals(value) || "```".equals(value))) {
            ArrayList sb = (ArrayList) msgMap.get(primarykeyValue);
            if (sb == null) {
                sb = new ArrayList<String>();
            }

            sb.add((sb.size() + 1) + ".&nbsp;关联指标第" + (rowIndex + 1) + "行[" + fi.getItemdesc() + "]的值为空或不符合格式要求不允许导入");
            RepeatPrimaryKey = "1";
            msgMap.put(primarykeyValue, sb);
        }
        
        return primarykeyValue;
    }

}
