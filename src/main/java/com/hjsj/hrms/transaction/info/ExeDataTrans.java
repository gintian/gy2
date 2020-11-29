package com.hjsj.hrms.transaction.info;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.ScanFormationBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.ibm.icu.text.NumberFormat;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
/**
 * 将读入内存的数据持久化到数据库
 * @author xujian
 *Apr 24, 2010
 */
public class ExeDataTrans extends IBusiness {
    private String privFields="";
    private String privFieldSets="";
    private String fieldsetid="A01";
    private String privM="b0110";
    private String privMv="";
    private int num = 0;
    public void execute() throws GeneralException {
        HashMap key_a0100 = new HashMap();
        this.getFormHM().remove("message");
        ArrayList<String> personIdList = new ArrayList<String>();
        try{
            String userbase = (String)this.getFormHM().get("userbase");
            HashMap unusekey = (HashMap)this.getFormHM().get("unusekey");
            unusekey=unusekey!=null?unusekey:new HashMap();
            String isupdate =(String)this.getFormHM().get("isupdate");
            isupdate=isupdate==null|| "".equals(isupdate)?"1":isupdate;
            String updatestr = (String)this.getFormHM().get("updatestr");
            updatestr=updatestr!=null?updatestr:"";
            HashMap a0100s = (HashMap)this.getFormHM().get("a0100s");
            a0100s = a0100s!=null?a0100s:new HashMap();
            
            HashMap<String, HashMap<String, String>> repeatMap = (HashMap)this.getFormHM().get("repeatMap");
            StringBuffer highta0100sb=new StringBuffer();
            if(this.userView.getPrivExpression().length()>2){
                this.doHigthPriv(userbase, highta0100sb);
            }
            
            HashMap<String, String> errorPersonMap = (HashMap<String, String>) this.userView.getHm().get("errorPerson");
            
            fieldsetid = ((String)this.getFormHM().get("fieldsetid")).toLowerCase();
            ArrayList mapsList =(ArrayList)this.getFormHM().get("mapsList");
            String noExistsField = "";
            for (int no = 0;no<mapsList.size();no++){

                Object[] maps=(Object[])mapsList.get(no);
                HashMap fieldMap = (HashMap)maps[0];
                if(fieldMap==null){
                    continue;
                    //return; //缺陷3131  zgd 2014-8-5 多个信息集同时导入，其中中间一个信息集为空，后面的信息集同样需要导入。
                }
                
                ArrayList valueList = (ArrayList)maps[1];
                if(valueList==null||valueList.size()==0){
                    continue;
                    //return;  //缺陷3131  zgd 2014-8-5 多个信息集同时导入，其中中间一个信息集为空，后面的信息集同样需要导入。
                }
                
                String primarykeys=((StringBuffer)maps[2]).toString();  // 主键值
                ArrayList keyList =(ArrayList)maps[3];      // 字段列表
                ArrayList foreignkeys = (ArrayList)maps[4];
                StringBuffer a0100sb=(StringBuffer)maps[5];//用于判断库中是否已存在
                String nopriva0100=((StringBuffer)maps[6]).toString();//用于存储没权限修改的记录
                fieldsetid=((StringBuffer)maps[7]).toString();
                StringBuffer isNullField =  (StringBuffer)maps[8];
                //用于保存系统中的存在的人员但是姓名与模板中的姓名不一致的数据map中key为唯一性指标的值，value为姓名
                HashMap<String, String> diffa0101 = (HashMap<String, String>)maps[9];
                if(StringUtils.isNotEmpty(isNullField.toString())){
                    if(StringUtils.isNotEmpty(noExistsField))
                        noExistsField +="\n";
                    
                    if(isNullField.toString().endsWith(","))
                        isNullField.setLength(isNullField.toString().length() - 1);
                    
                    noExistsField += isNullField + ";";
                }
                
                if(keyList==null||keyList.size()==0){
                    continue;
                    //return;  //缺陷3131  zgd 2014-8-5 多个信息集同时导入，其中中间一个信息集为空，后面的信息集同样需要导入。
                    
                }
                initPriv();
                String primarykey=(String)keyList.get(0);
                ContentDAO dao = new ContentDAO(this.frameconn);
                String tablename=userbase+fieldsetid.toLowerCase();
                this.doPrivFields();
                
                // WJH 2013-8-30  增加编制检查
                // 先清空
                this.getFormHM().put("info", "");
                this.getFormHM().put("num", 0+"");
                ScanFormationBo scanFormationBo=new ScanFormationBo(this.frameconn, userView);
                if(scanFormationBo.doScan()){
                    int infoFlag; // 信息类型， 1,2,3： 主集,一般子集，兼职子集
                    if ("A01".equalsIgnoreCase(fieldsetid)){
                        infoFlag = 1;
                    } else if (!fieldsetid.equalsIgnoreCase(scanFormationBo.getPart_setid()) ) {
                        infoFlag = 2;
                    } else {
                        infoFlag = 3;                       
                    }
                    StringBuffer items = new StringBuffer(",");
                    for(int i=0; i<keyList.size(); i++){
                        // 第一个是唯一性标识
                        if(infoFlag!=1 && i==0)
                            continue;
                        
                        items.append(keyList.get(i).toString().trim());
                        items.append(",");
                    }
                    if(scanFormationBo.needDoScan(userbase, items.toString())){
                        // 调用端组装要插入或修改记录放到list<LazyDynaBean>
                        // A01: 是否更新，不更新时，不发送已有记录。如果更新， 已有记录addflag为修改
                        // 一般子集： 每个人只传最近一条记录，一个人有未对应记录时，发送最后一条记录。
                        // 如果一个人的记录在库里都有对应，不更新则不传，更新时，取与库中最近一条对应的记录，没有则不发送。 addflag都是修改
                        // 兼职子集： 按兼职传，具体控制编制类处理
                        // 编制考虑兼职时，根据任免标识，任的记录都传。 addflag 能对应的传为更新，不对应的为新增。
                        // 传的过程中， 兼职单位、部门、职位指标分别放到b0110,e0122,e01a1，加兼职标志.
                        
                        String a0100 = null;
                        String addFlag = null;
                        HashMap scanMap = new HashMap(); // 追加一般子集时，记录哪些人已处理
                        HashMap maxI9999Map = new HashMap(); // 记录最大I9999
                        ArrayList scanList = new ArrayList();
                        String[] itemlist = items.substring(1, items.length()-1).toString().split(",");
                        LazyDynaBean voBean;
                        HashMap<String, String> i9999Map = new HashMap<String, String>();
                        if (infoFlag != 1)
                            i9999Map = getUserI9999(tablename);
                            
                        // 从后到前，最后的作为进入子集的最近记录
                        for (int i=valueList.size()-1; i>=0; i--){
                            HashMap valueMap=(HashMap)valueList.get(i);
                            // 信息是否加到编制审查中
                            String keyvalue= (String)valueMap.get(primarykey);
                            int i9999 = -1;  //  导入编制子集才用
                            if(keyvalue==null|| "".equals(keyvalue)){
                                continue; 
                            }
                            if (infoFlag == 1) {
                                // 主集
                                if(unusekey.containsKey(keyvalue)){
                                    // 修改
                                    if(updatestr.indexOf(keyvalue)!=-1||"t".equals(isupdate)){//在提示时选择更新记录
                                        String dba0100=(String)unusekey.get(keyvalue);
                                        String t[]=dba0100.split("`");
                                        if(!t[0].equalsIgnoreCase(userbase))
                                            continue;
                                        a0100 = t[1];
                                        addFlag = "0";
                                    } else {
                                        continue;
                                    }                               
                                }else {
                                    // 新增
                                    if(a0100sb.toString().indexOf(keyvalue)!=-1)//库中已存在
                                        continue;
                                    a0100 = "";
                                    addFlag = "1";
                                }
                            } else {
                                // 子集
                                if(!unusekey.containsKey(keyvalue))
                                    continue;
                                if(a0100s.get(keyvalue)==null)
                                    continue;
                                String dba0100=(String)unusekey.get(keyvalue);
                                String t[]=dba0100.split("`");
                                if(!t[0].equalsIgnoreCase(userbase))
                                    continue;
                                a0100 = t[1];   
                                if(updatestr.indexOf(keyvalue)!=-1||"t".equals(isupdate)){//更新记录
                                    if (infoFlag == 2) {
                                        if (scanMap.get(keyvalue) != null && "new".equals((String)scanMap.get(keyvalue)) )
                                            continue;
                                    }
                                    // 找到对应的记录 
                                    String a0100temp=(String)a0100s.get(keyvalue);
                                    if(a0100temp==null){
                                        a0100temp=(String)key_a0100.get(keyvalue);//ty add 主集中存在的人员
                                    }
                                    i9999 = getVoI9999(tablename, a0100temp, primarykey, valueMap, foreignkeys, dao);
                                    if (infoFlag == 2) {
                                        if(i9999==0){
                                            // 新增
                                            if(scanMap.get(keyvalue) != null){
                                                int idx=Integer.parseInt((String)scanMap.get(keyvalue));
                                                if(scanList.size()>idx)
                                                    scanList.remove(idx);
                                            }                                   
                                            scanMap.put(keyvalue, "new");
                                        }else{
                                            // 修改
                                            int maxi9999;
                                            
                                            // 取当前最大号
                                            if (maxI9999Map.get(keyvalue) == null){
                                                maxi9999 = Integer.parseInt(i9999Map.get((String)a0100s.get(keyvalue)))-1;
                                                maxI9999Map.put(keyvalue, Integer.valueOf(maxi9999).toString());
                                            }else{
                                                maxi9999 = Integer.parseInt((String)maxI9999Map.get(keyvalue));
                                            }
                                            if(i9999<maxi9999)
                                                continue;
                                            // size记录了scanList中已有记录的位置， 如果找到新增子集记录，scanList中将删除本条
                                            scanMap.put(keyvalue, scanList.size()+"");
                                        }                               
                                        addFlag = "0";
                                    }else{
                                        // 兼职子集 
                                        if(i9999==0){
                                            addFlag = "1";
                                        }else{
                                            addFlag = "0";
                                        }
                                    }
                                }else {
                                    // 非更新，记录全部追加
                                    if (infoFlag == 2) {
                                        // 一般子集 更新最后一条
                                        if (scanMap.get(keyvalue) != null) {                                    
                                            continue;
                                        }
                                        scanMap.put(keyvalue, "0");  // 值无所谓
                                        addFlag = "0";
                                    }else {
                                        // 兼职子集 全加   在任不在人编制检查中有处理
                                        addFlag = "1";
                                    }
                                }
                            }
                            
                            voBean = new LazyDynaBean();
                            // 放值 子集中也有主集指标，需过滤掉，不用valueMap
                            for (int j=0; j < itemlist.length; j++) {
                                String itemid = itemlist[j].trim();
                                if (infoFlag==3) {
                                    // 兼职指标转换
                                    String itemScan = new String(itemid);
                                    if (itemid.equalsIgnoreCase(scanFormationBo.getPart_unit())) {
                                        itemScan = "b0110";
                                    } if (itemid.equalsIgnoreCase(scanFormationBo.getPart_dept())) {
                                        itemScan = "e0122";
                                    } if (itemid.equalsIgnoreCase(scanFormationBo.getPart_pos())) {
                                        itemScan = "e01a1";
                                    }
                                    voBean.set(itemScan, valueMap.get(itemid));
                                } else {
                                    voBean.set(itemid, valueMap.get(itemid));
                                }
                            }
                            // 其他值
                            voBean.set("objecttype", "1");  // 人员
                            voBean.set("nbase", userbase);
                            if (infoFlag ==3 ) {
                                voBean.set("ispart", "1");  // 兼职
                                voBean.set("i9999", i9999+"");
                            }else {
                                voBean.set("ispart", "0");  // 非兼职
                            }
                            voBean.set("a0100", a0100);     // a0100
                            voBean.set("addflag", addFlag); // 新增、修改
                            scanList.add(voBean);                       
                        }
                        scanFormationBo.execDate2TmpTable(scanList);
                        String mess=scanFormationBo.isOverstaffs();
                        if(!"ok".equals(mess)){
                        	
                            if("warn".equals(scanFormationBo.getMode())){
                                //提示，继续, 
                                //根据前台信息抛出再做后续处理
                                this.getFormHM().put("info", mess);
                            }else{
                                //提示，中止。 消息
                            	mess += " " + ResourceFactory.getProperty("batchimport.info.error.notimport");
                                this.getFormHM().put("info", mess);
                                this.getFormHM().put("num", 0+"");
                                return;
                            }
                        }
                    }
                }
                // 编制检查结束
                
                StringBuffer column = new StringBuffer();
                StringBuffer columnValue = new StringBuffer();
                ArrayList<ArrayList> valuelist = new ArrayList<ArrayList>();
                ArrayList<String> itemList = getItemList(fieldMap, userbase, fieldsetid);
                InfoUtils infoUtils=new InfoUtils();
                IDGenerator idg = new IDGenerator(2, this.getFrameconn());
                if("A01".equalsIgnoreCase(fieldsetid)){
                    boolean flag = true;
                    for(int i=0;i<valueList.size();i++){
                        HashMap valueMap=(HashMap)valueList.get(i);
                        String keyvalue= (String)valueMap.get(primarykey);
                        if(keyvalue==null|| "".equals(keyvalue)){
                            continue;
                        }
                        //只要系统中存在的人员的姓名与模板中的姓名不一致就不导入
                        if(diffa0101.containsKey(keyvalue))
                            continue;
                        //只要系统中存在的人员的姓名与模板中的姓名不一致就不导入
                        if(errorPersonMap.containsKey(keyvalue))
                            continue;
                        
                        ArrayList list = new ArrayList();
                        if(unusekey.containsKey(keyvalue)){
                            if(nopriva0100.indexOf(keyvalue)!=-1){//无权限修改
                                continue;
                            }
                            
                            if(updatestr.indexOf(keyvalue)!=-1||"t".equals(isupdate)){//在提示时选择更新记录
                                String dba0100=(String)unusekey.get(keyvalue);
                                boolean IsPart = false;
                                String t[]=dba0100.split("`");
                                if(!t[0].equalsIgnoreCase(userbase))
                                    continue;
                                if(!this.userView.isAdmin()&&this.userView.getDbpriv().toString().toUpperCase().indexOf(t[0].toUpperCase())==-1)
                                    continue;
                                if(this.userView.getPrivExpression().length()>2){
                                    if(highta0100sb.indexOf(t[1])==-1){
                                        IsPart = true;//是不是兼职在该人员范围权限内
                                    }
                                }
                                
                                if(i == 0 || flag) {
                                    column.append("a0100,");
                                    columnValue.append("?,");
                                }
                                list.add(t[1]);
                                
                                for(int a = 0; a < itemList.size(); a++){
                                    String itemid=(String)itemList.get(a);
                                    
                                    //没有此指标写权限
                                    if(!this.userView.isAdmin()&&this.privFields.indexOf(itemid.toUpperCase())==-1)
                                        continue;
                                    
                                    if(i == 0 || flag) {
                                        column.append(itemid + ",");
                                        columnValue.append("?,");                           
                                    }
                                    
                                    //zgd 2014-2-11 当导入人员兼职在该人员范围权限下时，该人员的单位、部门、岗位的值不能修改。
                                    if((IsPart)&&("b0110".equalsIgnoreCase(itemid)||"e0122".equalsIgnoreCase(itemid)||"e01a1".equalsIgnoreCase(itemid))){
                                        list.add("");
                                        continue;
                                    }
                                    
                                    FieldItem item = DataDictionary.getFieldItem(itemid);
                                    if("D".equals(item.getItemtype())){
                                        String value=(String)valueMap.get(itemid);
                                        if(value==null||"".equals(value)) {
                                            list.add(null);
                                            continue;
                                        }
                                        
                                        if(!"```".equals(value))
                                            list.add(this.getFormatTime(value, value.length()));
                                        else
                                            list.add(null);
                                        
                                    }else if("N".equals(item.getItemtype())){//缺陷3031 zgd 2014-7-8 数值型指标单独处理
                                        String value=(String)valueMap.get(itemid);
                                        if(item.isSequenceable())
                                            value = infoUtils.getSequenceableValue(itemid, userbase, fieldsetid, this.frameconn, t[1], "", idg);
                                        
                                        if(value==null||"".equals(value)) {
                                            list.add(null);
                                            continue;
                                        }
                                        
                                        if(!"```".equals(value)){
                                            int dw=item.getDecimalwidth();
                                            if(dw==0)
                                                list.add(Integer.parseInt(value) + "");
                                            else {
                                            	double number = Double.valueOf(value).doubleValue();
                                            	value = number + "";
                                            	if(StringUtils.isNotEmpty(value) && value.indexOf("E") > -1)
                                            		value = formatDouble(number);
                                            		
                                            	list.add(value);
                                            }
                                            
                                        } else 
                                            list.add(null);
                                    }else{
                                        String value=(String)valueMap.get(itemid);
                                        if(item.isSequenceable())
                                            value = infoUtils.getSequenceableValue(itemid, userbase, fieldsetid, this.frameconn, t[1], "", idg);
                                        
                                        if(value==null||"".equals(value)) {
                                            list.add("");
                                            continue;
                                        }
                                        
                                        if("A".equals(item.getItemtype()) && "0".equals(item.getCodesetid()))
                                            value=splitString(value, item.getItemlength());
                                        
                                        if(!"```".equals(value))
                                            list.add(value);
                                        else 
                                            list.add("");
                                    }
                                }
                                
                                if(i == 0 || flag) {
                                    column.append("updateflag,IsPart,modtime,modusername,createtime,createusername,"); 
                                    columnValue.append("?,?,?,?,?,?,");
                                }
                                
                                list.add("1");
                                if(IsPart)
                                    list.add("1");
                                else
                                    list.add("0");
                                
                                list.add(new Date(System.currentTimeMillis()));
                                list.add(this.userView.getUserName());
                                list.add("");
                                list.add("");
                                
                                flag = false;
                            }else{
                                continue;
                            }
                        }else{
                            if(a0100sb.toString().indexOf(keyvalue)!=-1)//库中已存在
                                continue;
                            String codeitemid=(String)valueMap.get(this.privM);
                            if(codeitemid!=null&&!"".equals(codeitemid)){//无权限插入的
                                if(codeitemid.indexOf(this.privMv)==-1)
                                    continue;
                            }
                            
                            String newa0100 =this.getUserId(tablename);
                            if(i == 0 || flag) {
                                column.append("a0100,");
                                columnValue.append("?,");
                            }
                            list.add(newa0100);
                         
                            for(int a = 0; a < itemList.size(); a++){
                                String itemid=itemList.get(a);
                                //没有此指标写权限
                                if(!this.userView.isAdmin()&&this.privFields.indexOf(itemid.toUpperCase())==-1)
                                    continue;
                                
                                if(i == 0 || flag) {
                                    column.append(itemid + ",");
                                    columnValue.append("?,");
                                }
                                
                                FieldItem item = DataDictionary.getFieldItem(itemid);
                                if("D".equals(item.getItemtype())){
                                    String value=(String)valueMap.get(itemid);
                                    if(value==null||"".equals(value)){
                                        list.add(null);
                                        continue;
                                    }
                                    
                                    if(!"```".equals(value))
                                        list.add(this.getFormatTime(value, value.length()));
                                    else
                                        list.add(null);
                                    
                                }else if("N".equals(item.getItemtype())){//缺陷3031 zgd 2014-7-8 数值型指标单独处理
                                    String value=(String)valueMap.get(itemid);
                                    if(item.isSequenceable())
                                        value = "";
                                    
                                    if(value==null||"".equals(value)){
                                        list.add(null);
                                        continue;
                                    }
                                    
                                    if(!"```".equals(value)){
                                        int dw=item.getDecimalwidth();
                                        if(dw==0){
                                            list.add(Integer.parseInt(value) + "");
                                        }else{
                                        	double number = Double.valueOf(value).doubleValue();
                                        	value = number + "";
                                        	if(StringUtils.isNotEmpty(value) && value.indexOf("E") > -1)
                                        		value = formatDouble(number);
                                        	
                                        	list.add(value);
                                        }
                                    } else {
                                        list.add(null);
                                    }
                                }else{
                                    String value=(String)valueMap.get(itemid);
                                    if(item.isSequenceable())
                                        value = "";
                                    
                                    if(value==null||"".equals(value)){
                                        list.add("");
                                        continue;
                                    }
                                    
                                    if("A".equals(item.getItemtype())&& "0".equals(item.getCodesetid()))
                                        value=splitString(value, item.getItemlength());
                                    
                                    if(!"```".equals(value))
                                        list.add(value);
                                    else
                                        list.add("");
                                }
                            }
                            
                            if(i == 0 || flag) {
                                column.append("updateflag,IsPart,modtime,modusername,createtime,createusername,"); 
                                columnValue.append("?,?,?,?,?,?,");
                            }
                            list.add("0");
                            list.add("0");
                            list.add(new Date(System.currentTimeMillis()));
                            list.add(this.userView.getUserName());
                            list.add(new Date(System.currentTimeMillis()));
                            list.add(this.userView.getUserName());
                           
                            key_a0100.put(keyvalue, newa0100);
                            personIdList.add(newa0100);
                            flag = false;
                        }
                        
                        valuelist.add(list);
                        
                    }
                    
                }else{
                        HashMap<String, String> a0100Map = new HashMap<String, String>(); 
                        boolean msg = true;
                        HashMap<String, String> i9999Map = getUserI9999(userbase + fieldsetid);
                        String[] primarykeyValues = ((StringBuffer)maps[2]).toString().split("','");
                        for(int i=0;i<valueList.size();i++){
                            
                            HashMap valueMap=(HashMap)valueList.get(i);
                            String keyvalue= (String)valueMap.get(primarykey);
                            if(keyvalue==null)
                                continue;
                            StringBuffer A01primarykeys  =(StringBuffer)this.getFormHM().get("A01primarykeys");
                            if(!unusekey.containsKey(keyvalue)&&-1==A01primarykeys.indexOf(keyvalue)){
                                continue;
                            }
                            if(a0100s.get(keyvalue)==null&&-1==A01primarykeys.indexOf(keyvalue))
                                continue;
                            if(nopriva0100.indexOf(keyvalue)!=-1){//无权限修改插入
                                continue;
                            }
                            //子集中的姓名与子集中的姓名不一致则不导入
                            if(diffa0101.containsKey(keyvalue))
                                continue;
                            
                            String dba0100;
                            String t[]=new String[2];
                            dba0100 =(String)unusekey.get(keyvalue);
                            if(dba0100!=null)
                             t=dba0100.split("`");
                            else if (-1!=A01primarykeys.indexOf(keyvalue)){
                                t[0]=userbase;
                                t[1]=keyvalue;
                            }else{
                                continue;
                            }
                            tablename=userbase+fieldsetid.toLowerCase();
                            if(!this.userView.isAdmin()&&this.userView.getDbpriv().toString().toUpperCase().indexOf(t[0].toUpperCase())==-1)
                                continue;
                            
                            ArrayList list = new ArrayList();
                            if(updatestr.indexOf(keyvalue)!=-1||"t".equals(isupdate)){//更新记录
                                if(!t[0].equalsIgnoreCase(userbase))
                                    continue;
                                // WJH　取i9999代码提到了单独的方法里
                                String a0100=(String)a0100s.get(keyvalue);
                                if(a0100==null){
                                    a0100=(String)key_a0100.get(keyvalue);//ty add 主集中存在的人员
                                }
                                
                                if(StringUtils.isEmpty(a0100))
                                    continue;
                                
                                int i9999 = getVoI9999(tablename,a0100 , primarykey, valueMap, foreignkeys, dao);
                                String flag="add";
                                if(i9999!=0){
                                    flag="update";
                                }
                                if(this.userView.getPrivExpression().length()>2){
                                    if((String)a0100s.get(keyvalue)!=null){//zhaogd 2014-3-6 bug:非su，模板新增主集人员，且同时添加子集信息，此时a0100s为空
                                        if(highta0100sb.indexOf((String)a0100s.get(keyvalue))==-1){
                                            continue;
                                        }
                                    }
                                }
                                
                                if("update".equals(flag)){
                                    String a0100temp=(String)a0100s.get(keyvalue);
                                    if(a0100temp==null){
                                        a0100temp=(String)key_a0100.get(keyvalue);//ty add 主集中存在的人员
                                    }
                                    if(a0100temp==null||"".equals(a0100temp)){
                                        continue;
                                    }
                                    if(i == 0 || msg) {
                                        column.append("a0100,i9999,");
                                        columnValue.append("?,?,");
                                    }
                                    list.add(a0100temp);
                                    list.add(i9999 + "");                
                                   
                                    StringBuffer foreignkeyValue = new StringBuffer();
                                    for(int a = 0; a < itemList.size(); a++){
                                        String itemid=(String)itemList.get(a);
                                        //没有此指标写权限
                                        if(!this.userView.isAdmin()&&this.privFields.indexOf(itemid.toUpperCase())==-1)
                                            continue;
                                         
                                        if(primarykey.equalsIgnoreCase(itemid))
                                            continue;
                                        
                                        if(foreignkeys.contains(itemid)){
                                            String itemidValue=(String)valueMap.get(itemid);
                                            if(itemidValue == null || "".equals(itemidValue))
                                                continue;
                                            
                                            if("```".equals(itemidValue))
                                                continue;
                                                
                                            FieldItem item = DataDictionary.getFieldItem(itemid);
                                            if("M".equals(item.getItemtype()))
                                                continue;
                                            
                                            foreignkeyValue.append(itemidValue + ";");
                                        }
                                        
                                        if(i == 0 || msg) {
                                            column.append(itemid + ",");
                                            columnValue.append("?,");
                                        }
                                        
                                        FieldItem item = DataDictionary.getFieldItem(itemid);
                                        if("D".equals(item.getItemtype())){
                                            String value=(String)valueMap.get(itemid);
                                            if(value==null||"".equals(value)){
                                                list.add(null);
                                                continue;
                                            }
                                            
                                            if(!"```".equals(value))
                                                list.add(this.getFormatTime(value, value.length()));
                                            else
                                                list.add(null);
                                        }else if("N".equals(item.getItemtype())){//缺陷3031 zgd 2014-7-8 数值型指标单独处理
                                            String value=(String)valueMap.get(itemid);
                                            if(item.isSequenceable())
                                                value = infoUtils.getSequenceableValue(itemid, userbase, fieldsetid, this.frameconn, t[1], i9999+"", idg);
                                            
                                            if(value==null||"".equals(value)){
                                                list.add(null);
                                                continue;
                                            }
                                            
                                            if(!"```".equals(value)){
                                                int dw=item.getDecimalwidth();
                                                if(dw==0)
                                                    list.add(Integer.parseInt(value) + "");
                                                else {
                                                	double number = Double.valueOf(value).doubleValue();
                                                	value = number + "";
                                                	if(StringUtils.isNotEmpty(value) && value.indexOf("E") > -1)
                                                		value = formatDouble(number);
                                                		
                                                	list.add(value);
                                                }
                                            } else 
                                                list.add(null);
                                        }else{
                                            String value=(String)valueMap.get(itemid);
                                            if(item.isSequenceable())
                                                value = infoUtils.getSequenceableValue(itemid, userbase, fieldsetid, this.frameconn, t[1], i9999+"", idg);
                                            
                                            if(value==null||"".equals(value)){
                                                list.add("");
                                                continue;
                                            }
                                            
                                            if("A".equals(item.getItemtype())&& "0".equals(item.getCodesetid()))
                                                value=splitString(value, item.getItemlength());
                                            
                                            if(!"```".equals(value)&&!item.isSequenceable())
                                                list.add(value);
                                            else
                                                list.add("");
                                        }
                                    }
                                    
                                    if(i == 0 || msg) {
                                        column.append("updateflag,createtime,modtime,createusername,modusername,"); 
                                        columnValue.append("?,?,?,?,?,");
                                    }
                                    
                                    list.add("1");
                                    list.add("");
                                    list.add(new Date(System.currentTimeMillis()));
                                    list.add("");
                                    list.add(this.userView.getUserName());
                                    msg = false;
                                    //模板中的重复数据不进行导入
                                    String primarykeyValue = primarykeyValues[i];
                                    HashMap<String, String> map = repeatMap.get(primarykeyValue);
                                    if(map != null) {
                                        String sum = map.get(foreignkeyValue.toString());
                                        if(StringUtils.isNotEmpty(sum) && Integer.valueOf(sum) > 1)
                                            continue;
                                    }
                                }else{
                                    String newa0100 =(String)a0100s.get(keyvalue);
                                    if(newa0100==null){
                                        newa0100=(String)key_a0100.get(keyvalue);//ty add 主集中存在的人员
                                    }
                                    if(newa0100==null||"".equals(newa0100)){
                                        continue;
                                    }
                                    
                                    if(i == 0 || msg) {
                                        column.append("a0100,i9999,");
                                        columnValue.append("?,?,");                                        
                                    }
                                    list.add(newa0100);
                                    String id = i9999Map.get(newa0100);
                                    if(a0100Map != null && a0100Map.get(newa0100) != null) {
                                        String number = a0100Map.get(newa0100);
                                        id = (Integer.parseInt(number) + 1) + "";
                                    }

                                    if(StringUtils.isEmpty(id))
                                        id = "1";
                                    
                                    list.add(Integer.parseInt(id));
                                    a0100Map.put(newa0100, id);
                                    
                                    for(int a = 0; a < itemList.size(); a++){
                                        String itemid=itemList.get(a);
                                        //没有此指标写权限
                                        if(!this.userView.isAdmin()&&this.privFields.indexOf(itemid.toUpperCase())==-1)
                                            continue;
                                        
                                        if(primarykey.equalsIgnoreCase(itemid))
                                            continue;
                                        
                                        if(i == 0 || msg) {
                                            column.append(itemid + ",");
                                            columnValue.append("?,");                                           
                                        }
                                        
                                        FieldItem item = DataDictionary.getFieldItem(itemid);
                                        if("D".equals(item.getItemtype())){
                                            String value=(String)valueMap.get(itemid);
                                            if(value==null||"".equals(value)){
                                                list.add(null);
                                                continue;
                                            }
                                            
                                            if(!"```".equals(value))
                                                list.add(this.getFormatTime(value, value.length()));
                                            else
                                                list.add(null);
                                            
                                        }else if("N".equals(item.getItemtype())){//缺陷3031 zgd 2014-7-8 数值型指标单独处理
                                            String value=(String)valueMap.get(itemid);
                                            if(item.isSequenceable())
                                                value = "";
                                            
                                            if(value==null||"".equals(value)){
                                                list.add(null);
                                                continue;
                                            }
                                            if(!"```".equals(value)){
                                                int dw=item.getDecimalwidth();
                                                if(dw==0)
                                                    list.add(Integer.parseInt(value) + "");
                                                else {
                                                	double number = Double.valueOf(value).doubleValue();
                                                	value = number + "";
                                                	if(StringUtils.isNotEmpty(value) && value.indexOf("E") > -1)
                                                		value = formatDouble(number);
                                                		
                                                	list.add(value);
                                                }
                                            } else
                                                list.add(null);
                                        }else{
                                            String value=(String)valueMap.get(itemid);
                                            if(item.isSequenceable())
                                                value = "";
                                            
                                            if(value==null||"".equals(value)){
                                                list.add("");
                                                continue;
                                            }
                                            
                                            if("A".equals(item.getItemtype())&& "0".equals(item.getCodesetid())){
                                                value=splitString(value, item.getItemlength());
                                            }
                                            
                                            if(!"```".equals(value))
                                                list.add(value);
                                        }
                                    }
                                    if(i == 0 || msg) {
                                        column.append("updateflag,createtime,modtime,createusername,modusername,"); 
                                        columnValue.append("?,?,?,?,?,");
                                    }
                                    list.add("0");
                                    list.add(new Date(System.currentTimeMillis()));
                                    list.add(new Date(System.currentTimeMillis()));
                                    list.add(this.userView.getUserName());
                                    list.add(this.userView.getUserName());
                                    personIdList.add(newa0100 + ":" + id);
                                    msg = false;
                                }
                                
                            }else{
                                if(!this.userView.isAdmin()&&(this.privFields.length()<2||this.privFieldSets.indexOf(this.fieldsetid)==-1))
                                    continue;
                                
                                String newa0100 =(String)a0100s.get(keyvalue);
                                if(newa0100==null){
                                    newa0100 = (String)key_a0100.get(keyvalue);
                                }
                                if(newa0100==null||"".equals(newa0100)){
                                    continue;
                                }
                                
                                if(i == 0 || msg) {
                                    column.append("a0100,i9999,");
                                    columnValue.append("?,?,");
                                }
                                
                                list.add(newa0100);
                                String id = i9999Map.get(newa0100);
                                
                                if(a0100Map != null && a0100Map.get(newa0100) != null) {
                                    String number = a0100Map.get(newa0100);
                                    id = (Integer.parseInt(number) + 1) + "";
                                }

                                if(StringUtils.isEmpty(id))
                                    id = "1";

                                list.add(Integer.parseInt(id));
                                a0100Map.put(newa0100, id);
                                
                                for(int a = 0; a < itemList.size(); a++){
                                    String itemid=itemList.get(a);
                                    //没有此指标写权限
                                    if(!this.userView.isAdmin()&&this.privFields.indexOf(itemid.toUpperCase())==-1)
                                        continue;
                                    
                                    if(primarykey.equalsIgnoreCase(itemid))
                                        continue;
                                    
                                    if(i == 0 || msg) {
                                        column.append(itemid + ",");
                                        columnValue.append("?,");
                                    }
                                    
                                    FieldItem item = DataDictionary.getFieldItem(itemid);
                                    if("D".equals(item.getItemtype())){
                                        String value=(String)valueMap.get(itemid);
                                        if(value==null||"".equals(value)){
                                            list.add(null);
                                            continue;
                                        }
                                        
                                        if(!"```".equals(value))
                                            list.add(this.getFormatTime(value, value.length()));
                                        else
                                            list.add(null);
                                    }else if("N".equals(item.getItemtype())){//缺陷3031 zgd 2014-7-8 数值型指标单独处理
                                        String value=(String)valueMap.get(itemid);
                                        if(item.isSequenceable())
                                            value = "";
                                        
                                        if(value==null||"".equals(value)){
                                            list.add(null);
                                            continue;
                                        }
                                        
                                        if(!"```".equals(value)){
                                            int dw=item.getDecimalwidth();
                                            if(dw==0)
                                                list.add(Integer.parseInt(value) + "");
                                            else {
                                            	double number = Double.valueOf(value).doubleValue();
                                            	value = number + "";
                                            	if(StringUtils.isNotEmpty(value) && value.indexOf("E") > -1)
                                            		value = formatDouble(number);
                                            		
                                            	list.add(value);
                                            }
                                        } else
                                            list.add(null);
                                    }else{
                                        String value=(String)valueMap.get(itemid);
                                        if(item.isSequenceable())
                                            value = "";
                                        
                                        if(value==null||"".equals(value)){
                                            list.add("");
                                            continue;
                                        }
                                        
                                        if("A".equals(item.getItemtype())&& "0".equals(item.getCodesetid())){
                                            value=splitString(value, item.getItemlength());
                                        }
                                        if(!"```".equals(value))
                                            list.add(value);
                                        else
                                            list.add("");
                                    }
                                }
                                
                                if(i == 0 || msg) {
                                    column.append("updateflag,createtime,modtime,createusername,modusername,"); 
                                    columnValue.append("?,?,?,?,?,");
                                }
                                list.add("0");
                                list.add(new Date(System.currentTimeMillis()));
                                list.add(new Date(System.currentTimeMillis()));
                                list.add(this.userView.getUserName());
                                list.add(this.userView.getUserName());
                                personIdList.add(newa0100 + ":" + id);
                                msg = false;
                            }
                            valuelist.add(list);
                        } 
                }
                
                String tempTableName = getTempTableName(userbase+fieldsetid.toLowerCase());
                            
                StringBuffer insertSql = new StringBuffer();
                insertSql.append("insert into " + tempTableName);
                
                if(column.toString().endsWith(","))
                    column.setLength(column.length() - 1);
                
                insertSql.append(" (" + column);
                insertSql.append(") values (");
                
                if(columnValue.toString().endsWith(","))
                    columnValue.setLength(columnValue.length() - 1);
                
                insertSql.append(columnValue);
                insertSql.append(")");
                
                HashMap<String, Object> tableMap = new HashMap<String, Object>();
                tableMap.put("insertSql", insertSql.toString());
                tableMap.put("column", column.toString());
                tableMap.put("values", valuelist);
                tableMap.put("tableName", tablename);
                tableMap.put("itemList", itemList);
                insertDate(tableMap);
                
                DbWizard db = new DbWizard(this.frameconn);
                if(db.isExistTable(tempTableName, false))
                    db.dropTable(tempTableName);
                updateSequenceableValue(personIdList, fieldsetid, userbase, keyList);
            }
            
            if(noExistsField != null && noExistsField.length() > 0)
                noExistsField = noExistsField.substring(0, noExistsField.length() - 1) + "。";
            
            this.getFormHM().put("noExistsField", noExistsField);
        }catch(Exception e){
            this.getFormHM().put("message", "导入数据出错！");
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally{
            this.getFormHM().put("num", String.valueOf(this.num));
        }
    }
    
    private Timestamp getFormatTime(String value, int formatLen) {
        String format = "yyyy-MM-dd";
        int length = formatLen;
        if(length == 4)
            format = "yyyy";
        else if(length == 7)
            format = "yyyy-MM";
        else if(length == 13)
            format = "yyyy-MM-dd HH";
        else if(length == 16)
            format = "yyyy-MM-dd HH:mm";
        else if(length >= 18)
            format = "yyyy-MM-dd HH:mm:ss";
        
        return DateUtils.getTimestamp(value, format);
    }
    
    // 返回导入子集记录对应的I9999
    // getVoI9999(tablename, (String)a0100s.get(keyvalue), primarykey, valueMap, foreignkeys, dao)
    private int getVoI9999(String strTableName, String a0100, String primarykey, HashMap valueMap,
             ArrayList foreignkeys, ContentDAO dao) throws Exception {
        StringBuffer sql = new StringBuffer();
        sql.append("select i9999 from "+strTableName);
        ArrayList values = new ArrayList();
        sql.append(" where a0100=?");
        values.add(a0100);
        
        if(foreignkeys != null && foreignkeys.size()>0){//判断子集信息有相同记录没，按按外键识别
            for(int n=0;n<foreignkeys.size();n++){
                String itemid=(String)foreignkeys.get(n);
                if(primarykey.equalsIgnoreCase(itemid)){
                    continue;
                }
                if(!this.userView.isAdmin()&&this.privFields.indexOf(itemid.toUpperCase())==-1)//没有此指标写权限
                    continue;
                String value=(String)valueMap.get(itemid);
                if(value==null||"".equals(value)){
                    continue;
                }
                if("```".equals(value)){
                    continue;
                }
                FieldItem item = DataDictionary.getFieldItem(itemid);
                if(item == null) {
                }else if("A".equals(item.getItemtype())){
                    sql.append(" and "+itemid+"=?");
					value=splitString(value, item.getItemlength());
                    values.add(value);
                } else if("D".equals(item.getItemtype()))
                    sql.append(" and "+itemid+"="+Sql_switcher.dateValue(value));
                else if("N".equals(item.getItemtype())){
                        if("".equals(value)){
                            //sql.append(" and "+Sql_switcher.sqlNull(itemid, 0)+"="+0);
                        }else{
                            //value=splitString(value, item.getItemlength());
                            sql.append(" and "+itemid+"="+value);
                        }
                }else if("M".equals(item.getItemtype())){
               
                }
            }
        }else{//如果导出模板没有设置外键列，则所有的值匹配上才算相同记录
            for(Iterator it=valueMap.keySet().iterator();it.hasNext();){
                String itemid=(String)it.next();
                if(primarykey.equalsIgnoreCase(itemid)){
                    continue;
                }
                if(!this.userView.isAdmin()&&this.privFields.indexOf(itemid.toUpperCase())==-1)//没有此指标写权限
                    continue;
                String value=(String)valueMap.get(itemid);
                if(value==null||"".equals(value)){
                    continue;
                }
                if("```".equals(value)){
                    continue;
                }
                FieldItem item = DataDictionary.getFieldItem(itemid);
                if(item == null) {
                    
                } else if("A".equals(item.getItemtype())) {
                    sql.append(" and "+itemid+"=?");
					value=splitString(value, item.getItemlength());
                    values.add(value);
                } else if("D".equals(item.getItemtype()))
                    sql.append(" and "+itemid+"="+Sql_switcher.dateValue(value));
                else if("N".equals(item.getItemtype())){
                        if("".equals(value)){
                            //sql.append(" and "+Sql_switcher.sqlNull(itemid, 0)+"="+0);
                        }else{
                            //value=splitString(value, item.getItemlength());
                            sql.append(" and "+itemid+"="+value);
                        }
                }else if("M".equals(item.getItemtype())){
               
                }
            }
        }
        this.frecset = dao.search(sql.toString(), values);
        int i9999=0;  // 返回0表示未找到
        while(this.frecset.next()){
            i9999=this.frecset.getInt("i9999");
        }
        return i9999;
    }

    //获得I9999的最大顺序号
    private HashMap<String, String> getUserI9999(String strTableName){
        StringBuffer strsql=new StringBuffer();
        strsql.append("select max("+Sql_switcher.isnull("I9999", "0")+")+1 as I9999,a0100");
        strsql.append(" from " + strTableName);
        strsql.append(" group by a0100 ");
        HashMap<String, String> map = new HashMap<String, String>();
        try {
            ContentDAO dao = new ContentDAO(this.frameconn);
            this.frowset = dao.search(strsql.toString());
            while(this.frowset.next()) {
                String a0100 = this.frowset.getString("a0100");
                if(StringUtils.isEmpty(a0100))
                    continue;
                
                String i9999 = this.frowset.getString("I9999");
                map.put(a0100, i9999);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return map;
    }
    //获取a0100
    private synchronized String getUserId(String tableName) throws GeneralException{
        return DbNameBo.insertMainSetA0100(tableName,this.getFrameconn());
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
        ArrayList privFieldSetList = this.userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);
        sb.setLength(0);
        for(int i=0;i<privFieldSetList.size();i++){
            FieldSet item=(FieldSet)privFieldSetList.get(i);
            if(item.getPriv_status()==2){
                sb.append(item.getFieldsetid()+",");
            }
        }
        this.privFieldSets=sb.toString().toLowerCase();
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
    
    private void doHigthPriv(String userbase,StringBuffer highta0100sb){
        try {
            String wherestr=this.userView.getPrivSQLExpression(this.userView.getPrivExpression(), userbase, true, new ArrayList());
            String sql="select "+userbase+"a01.a0100 a0100"+wherestr;
            ContentDAO dao = new ContentDAO(this.frameconn);
            this.frowset = dao.search(sql);
            while(this.frowset.next()){
                highta0100sb.append(this.frowset.getString("a0100")+",");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    /**
     * 获取导入数据的指标并创建临时表
     * @param valueMap 导入的数据
     * @param nbase 人员库
     * @param fieldsetId 要导入的子集
     * @return
     */
    private ArrayList<String> getItemList(HashMap valueMap, String nbase, String fieldsetId) {
        ArrayList<String> list = new ArrayList<String>();
        if(valueMap == null || valueMap.size() < 1 || StringUtils.isEmpty(nbase) || StringUtils.isEmpty(fieldsetid))
            return list;
        
        Statement stmt = null;
        ResultSet rs = null;
        try {
            String temTableName = getTempTableName(nbase + fieldsetid.toLowerCase());
            DbWizard db = new DbWizard(this.frameconn);
            if(db.isExistTable(temTableName, false))
                db.dropTable(temTableName);
            
            StringBuffer tempSql = new StringBuffer();
            if(Sql_switcher.searchDbServer() == Constant.MSSQL) {
                tempSql.append("create table ");
                tempSql.append(temTableName);
            } else {
                tempSql.append("Create Table ");
                tempSql.append(temTableName);
            }
            tempSql.append(" (a0100 varchar(8),");
            for(Iterator it=valueMap.keySet().iterator();it.hasNext();){
                String itemid=(String)it.next();
                //没有此指标写权限
                if(!this.userView.isAdmin()&&this.privFields.indexOf(itemid.toUpperCase())==-1)
                    continue;
                
                FieldItem fi = DataDictionary.getFieldItem(itemid, fieldsetid);
                if(fi == null || "0".equalsIgnoreCase(fi.getUseflag()))
                    continue;
                
                list.add(itemid);
                
                tempSql.append(fi.getItemid());
                if("A".equalsIgnoreCase(fi.getItemtype())) {
                    tempSql.append(" varchar(" + fi.getItemlength() + "),");
                } else if("N".equalsIgnoreCase(fi.getItemtype())) {
                    tempSql.append(" numeric(" + (fi.getItemlength() + fi.getDecimalwidth()) + ","+ fi.getDecimalwidth() +"),");
                } else if("D".equalsIgnoreCase(fi.getItemtype())) {
                    if(Sql_switcher.searchDbServer() == Constant.MSSQL)
                        tempSql.append(" Datetime,");
                    else
                        tempSql.append(" Date,");
                } else if("M".equalsIgnoreCase(fi.getItemtype())) {
                    if(Sql_switcher.searchDbServer() == Constant.MSSQL)
                        tempSql.append(" text,");
                    else
                        tempSql.append(" clob,");
                } 
            }
            
            //用于区分是新增还是更新的指标
            tempSql.append("updateflag int,");
            if("A01".equalsIgnoreCase(fieldsetid))
                //是否是兼职
                tempSql.append("IsPart int,");  
            else
                tempSql.append("i9999 int,"); 
            
			if(Sql_switcher.searchDbServer() == Constant.MSSQL)
	            tempSql.append(" createtime dateTime,modtime  dateTime,");
            else
                tempSql.append(" createtime date,modtime  date,");
            
            tempSql.append("createusername  varchar(50),modusername varchar(50)");
            
            tempSql.append(")");
           
            stmt = this.frameconn.createStatement();
            stmt.execute(tempSql.toString());
        } catch (Exception e) {
            this.getFormHM().put("message", "导入数据出错！");
            e.printStackTrace();
        } finally {
            PubFunc.closeIoResource(stmt);
        }
        return list;
    }
    /**
     * 往临时表中插入数据
     * @param map 插入数据的sql语句、数据、指标、表名
     */
    private void insertDate(HashMap<String, Object> map) {
        try {
            String sql = (String) map.get("insertSql");
            ArrayList<ArrayList> valuelist = (ArrayList<ArrayList>) map.get("values");
            if(valuelist == null || valuelist.size() < 1)
                return;
            
            ContentDAO dao = new ContentDAO(this.frameconn);
            dao.batchInsert(sql, valuelist);
            
            ArrayList<String> itemList = (ArrayList<String>) map.get("itemList");
            String column = (String) map.get("column");
            String tableName = (String) map.get("tableName");
           
            insertInfo(tableName, itemList);
            
        } catch (Exception e) {
        	this.getFormHM().put("message", "导入数据出错！");
            e.printStackTrace();
        }
    }
    /**
     * 将临时表的数据更到对应的子集中
     * @param tableName 子集名称
     * @param itemList 指标
     */
    private void insertInfo(String tableName, ArrayList<String> itemList) {
        String fieldsetDesc = "";
        RowSet rs = null;
        try {
        	String fieldsetid = tableName.substring(3);
        	FieldSet fieldset = DataDictionary.getFieldSetVo(fieldsetid);
        	fieldsetDesc = fieldset.getFieldsetdesc();
        	
            String tempTable = getTempTableName(tableName);
            //执行更新并且是非兼职的人员的指标
            StringBuffer updateColumn = new StringBuffer();
            //执行更新并且是兼职的人员的指标
            StringBuffer ispartColumn = new StringBuffer();
            
            StringBuffer selectColumn = new StringBuffer();
            StringBuffer selectispartColumn = new StringBuffer();
            for(int i = 0; i < itemList.size(); i++){
                String itemid = itemList.get(i);
                //获取有读写权限的指标
                if(!this.userView.isSuper_admin()&&this.privFields.indexOf(itemid.toUpperCase())==-1)
                    continue;
                //兼职人员不需要更新 b0110、e0122、e01a1
                if(tableName.toLowerCase().endsWith("a01") && !"b0110".equalsIgnoreCase(itemid)
                        && !"e0122".equalsIgnoreCase(itemid) && !"e01a1".equalsIgnoreCase(itemid)){
                    ispartColumn.append("a." + itemid + ",");
                    selectispartColumn.append(itemid + ",");
                }
                updateColumn.append("a." + itemid + ",");
                selectColumn.append(itemid + ",");
                    
            }
            
            updateColumn.append("a.modtime,a.modusername");
            selectColumn.append("modtime,modusername");
            
            ArrayList<String> sqlList = new ArrayList<String>();
            StringBuffer updateTime = new StringBuffer();
            if(Sql_switcher.searchDbServer() != Constant.MSSQL) {
                StringBuffer updateSql = new StringBuffer();
                updateSql.append("update " + tableName + " a set (state,");
                updateSql.append(updateColumn);
                updateSql.append(")=(select '3' state,");
                updateSql.append(selectColumn);
                updateSql.append(" from " + tempTable);
                updateSql.append(" b where ((b.updateflag=1");
                
                if(!tableName.toLowerCase().endsWith("a01")) 
                    updateSql.append(")) and a.i9999 = b.i9999");
                else
                    updateSql.append(" and b.ispart=0) or b.updateflag=0)");
                //拼接执行更新且是兼职的人员的基本信息
                if(tableName.toLowerCase().endsWith("a01")) {
                    
                    ispartColumn.append("a.modtime,a.modusername");
                    selectispartColumn.append("modtime,modusername");
                    
                    StringBuffer updateIspartSql = new StringBuffer();
                    updateIspartSql.append("update " + tableName + " a set (state,");
                    updateIspartSql.append(ispartColumn);
                    updateIspartSql.append(")=(select '3' state,");
                    updateIspartSql.append(selectispartColumn);
                    updateIspartSql.append(" from " + tempTable);
                    updateIspartSql.append(" b where b.updateflag=1 and b.ispart=1");
                    updateIspartSql.append(" and a.a0100=b.a0100)");
                    updateIspartSql.append(" where exists (select a0100 from ");
                    updateIspartSql.append(tempTable);
                    updateIspartSql.append(" c where  a.a0100=c.a0100 and c.updateflag=1 and c.ispart=1");
                    updateIspartSql.append(")");
                    sqlList.add(updateIspartSql.toString());
                }
                
                updateSql.append(" and a.a0100=b.a0100)");
                updateSql.append(" where exists (select a0100 from ");
                updateSql.append(tempTable);
                updateSql.append(" c where a.a0100=c.a0100 and ((c.updateflag=1");
                if(!"A01".equalsIgnoreCase(fieldsetid.toUpperCase()))
                    updateSql.append(")) and a.i9999=c.i9999");
                else
                    updateSql.append(" and c.ispart=0) or c.updateflag=0)");
                    
                updateSql.append(")");
                sqlList.add(updateSql.toString());
                
                if(!"A01".equalsIgnoreCase(fieldsetid.toUpperCase())) {
                    //拼接执行插入操作的sql语句
                    StringBuffer insertSql = new StringBuffer();
                    insertSql.append("insert into ");
                    insertSql.append(tableName);
                    insertSql.append(" a (state,");
                    insertSql.append(updateColumn);
                    insertSql.append(",a.createtime,a.createusername,a.a0100");
                    if(!"A01".equalsIgnoreCase(fieldsetid.toUpperCase()))
                        insertSql.append(",a.i9999");
                    
                    insertSql.append(") select '3' state,");
                    insertSql.append(selectColumn);
                    insertSql.append(",createtime,createusername,a0100");
                    if(!"A01".equalsIgnoreCase(fieldsetid.toUpperCase()))
                        insertSql.append(",i9999");
                    
                    insertSql.append(" from " + tempTable);
                    insertSql.append(" b where b.updateflag=0");                    
                    sqlList.add(insertSql.toString());
                } else {
                    updateTime.append("update " + tableName + " a set (state,");
                    updateTime.append("createusername,createtime");
                    updateTime.append(")=(select '3' state,");
                    updateTime.append("createusername,createtime");
                    updateTime.append(" from " + tempTable);
                    updateTime.append(" b where  b.updateflag=0");
                    updateTime.append(" and a.a0100=b.a0100)");
                    updateTime.append(" where exists (select a0100 from ");
                    updateTime.append(tempTable);
                    updateTime.append(" c where a.a0100=c.a0100 and  c.updateflag=0)");
                }
                    
            } else {
                StringBuffer mssqlColumn = new StringBuffer();
                StringBuffer mssqlIsPartColumn = new StringBuffer();
                StringBuffer mssqlInserColumn = new StringBuffer();
                for(int i = 0; i < itemList.size(); i++){
                    String itemid = itemList.get(i);
                    //获取有读写权限的指标
                    if(!this.userView.isSuper_admin()&&this.privFields.indexOf(itemid.toUpperCase())==-1)
                        continue;
                    //兼职人员不需要更新 b0110、e0122、e01a1
                    if(tableName.toLowerCase().endsWith("a01") && !"b0110".equalsIgnoreCase(itemid)
                            && !"e0122".equalsIgnoreCase(itemid) && !"e01a1".equalsIgnoreCase(itemid))
                        mssqlIsPartColumn.append(tableName + "." + itemid + "=b." + itemid + ",");
                    
                    mssqlColumn.append(tableName + "." + itemid + "=b." + itemid + ",");
                    mssqlInserColumn.append(itemid + ",");
                        
                }
                
                mssqlColumn.append(tableName + ".modtime=b.modtime,");
                mssqlColumn.append(tableName + ".modusername=b.modusername");
                mssqlIsPartColumn.append(tableName + ".modtime=b.modtime,");
                mssqlIsPartColumn.append(tableName + ".modusername=b.modusername");
                
                mssqlInserColumn.append("modtime,modusername");
                
                StringBuffer updateSql = new StringBuffer();
                updateSql.append("update " + tableName + " set state='3',");
                updateSql.append(mssqlColumn);
                updateSql.append(" from " + tempTable);
                updateSql.append(" b where ((b.updateflag=1");
                
                if(!tableName.toLowerCase().endsWith("a01")) 
                    updateSql.append(")) and " + tableName + ".i9999 = b.i9999");
                else
                    updateSql.append(" and b.ispart=0) or b.updateflag=0)");
                //拼接执行更新且是兼职的人员的基本信息
                if(tableName.toLowerCase().endsWith("a01")) {
                    
                    ispartColumn.append("a.modtime,a.modusername");
                    selectispartColumn.append("modtime,modusername");
                    
                    StringBuffer updateIspartSql = new StringBuffer();
                    updateIspartSql.append("update " + tableName + " set state='3',");
                    updateIspartSql.append(mssqlIsPartColumn);
                    updateIspartSql.append(" from " + tempTable);
                    updateIspartSql.append(" b where b.updateflag=1 and b.ispart=1 and " + tableName + ".a0100=b.a0100");
                    updateIspartSql.append(" and exists (select a0100 from ");
                    updateIspartSql.append(tempTable);
                    updateIspartSql.append(" c where " + tableName + ".a0100=c.a0100 and c.updateflag=1 and c.ispart=1");
                    updateIspartSql.append(")");
                    sqlList.add(updateIspartSql.toString());
                }
                
                updateSql.append(" and " + tableName + ".a0100=b.a0100");
                updateSql.append(" and exists (select a0100 from ");
                updateSql.append(tempTable);
                updateSql.append(" c where " + tableName + ".a0100=c.a0100 and ((c.updateflag=1");
                if(!"A01".equalsIgnoreCase(fieldsetid.toUpperCase()))
                    updateSql.append(")) and " + tableName + ".i9999=c.i9999");
                else 
                    updateSql.append(" and c.ispart=0) or c.updateflag=0)");
                
                updateSql.append(")");
                sqlList.add(updateSql.toString());
                
                if(!"A01".equalsIgnoreCase(fieldsetid.toUpperCase())) {
                    //拼接执行插入操作的sql语句
                    StringBuffer insertSql = new StringBuffer();
                    insertSql.append("insert into ");
                    insertSql.append(tableName);
                    insertSql.append(" (state,");
                    insertSql.append(mssqlInserColumn);
                    insertSql.append(",createtime,createusername,a0100");
                    if(!"A01".equalsIgnoreCase(fieldsetid.toUpperCase()))
                        insertSql.append(",i9999");
                    
                    insertSql.append(") select '3' state,");
                    insertSql.append(mssqlInserColumn);
                    insertSql.append(",createtime,createusername,a0100");
                    if(!"A01".equalsIgnoreCase(fieldsetid.toUpperCase()))
                        insertSql.append(",i9999");
                    
                    insertSql.append(" from " + tempTable);
                    insertSql.append(" b where b.updateflag=0");                    
                    sqlList.add(insertSql.toString());
                } else {
                    updateTime.append("update " + tableName + " set state='3',");
                    updateTime.append(tableName + ".createusername=b.createusername,");
                    updateTime.append(tableName + ".createtime=b.createtime");
                    updateTime.append(" from " + tempTable);
                    updateTime.append(" b where b.updateflag=0");
                    updateTime.append(" and " + tableName + ".a0100=b.a0100");
                    updateTime.append(" and exists (select a0100 from ");
                    updateTime.append(tempTable);
                    updateTime.append(" c where " + tableName + ".a0100=c.a0100 and c.updateflag=0)");
                    
                }
            }
            
            ContentDAO dao = new ContentDAO(this.frameconn);
            int updateCount = 0;
            for(String sql : sqlList) {
                String selectSql = "";
                if(sql.startsWith("insert")) {
                    selectSql = sql.substring(sql.indexOf("from"));
                    selectSql = "select count(1) recordNum " + selectSql;
                } else {
                    selectSql = sql.substring(sql.indexOf("exists"));
                    selectSql = selectSql.replace(tableName + ".", "a.");
                    selectSql = "select count(1) recordNum from " + tableName + " a where " + selectSql;
                }
                
                rs = dao.search(selectSql);
                if(rs.next()) {
                    int count = rs.getInt("recordNum");
                    updateCount = updateCount + count;
                }
            }
            
            int[] nums =  dao.batchUpdate(sqlList);
            //此条sql只是更新新增人员数据的创建时间和创建帐号，因此不计算到更新成功的记录数量中
            if(StringUtils.isNotEmpty(updateTime.toString()))
                dao.update(updateTime.toString());
            
            int countRow = 0;
            for(int i = 0; i < nums.length; i++)
                countRow = countRow + nums[i];
            //低版本的jdbc中update方法返回的值有问题，当更新语句返回的记录条数为0时，即认为查询出的记录全部更新完成
            if(0 == countRow)
                this.num += updateCount;
            else
                this.num += countRow;
            
        } catch (Exception e) {
        	String error = e.getMessage();
        	String message = fieldsetDesc + "子集导入数据出错";
        	if(error.indexOf("ORA-01427") > -1)
        		message += "，请检查模板中是否有关键指标重复的数据";
        	this.getFormHM().put("message", message + "！");
        	error = error.substring(error.indexOf(":") + 2).replace("\n", "");
        	this.getFormHM().put("error", error);
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
    }
    /**
     * 生成临时表名
     * @param tableName 子集名称
     * @return
     */
    private String getTempTableName(String tableName) {
        String tempTableName = "T_" + this.userView.getUserName() + tableName;
        if(Sql_switcher.searchDbServer() != Constant.MSSQL)
            tempTableName = "Temp_" + this.userView.getUserName() + tableName;
        
        return tempTableName;        
    }
    
    private void updateSequenceableValue(ArrayList<String> personIdList, String fieldSetId, 
            String nbase, ArrayList<String> itemList) {
        try {
            InfoUtils infoUtils=new InfoUtils();
            IDGenerator idg = new IDGenerator(2, this.getFrameconn());
            ArrayList<String> updateSqlList = new ArrayList<String>();
            for(String personId : personIdList) {
                String a0100 = "";
                String i9999 = "0";
                if(!"A01".equalsIgnoreCase(fieldSetId)) {
                    if(personId.indexOf(":") > -1) {
                        a0100 = personId.substring(0, personId.indexOf(":"));
                        i9999 = personId.substring(personId.indexOf(":") + 1);
                    }else
                    	a0100 = personId;
                } else
                	a0100 = personId;
                
                for(String itemid : itemList) {
                    FieldItem fi = DataDictionary.getFieldItem(itemid, fieldSetId);
                    if(fi == null || "0".equals(fi.getUseflag()))
                        continue;
                    
                    String value = "";
                    if(fi.isSequenceable()) {
                        value = infoUtils.getSequenceableValue(itemid, nbase, fieldSetId, this.frameconn, 
                                a0100, i9999, idg);
                        
                        StringBuffer sql = new StringBuffer();
                        sql.append("update ");
                        sql.append(nbase + fieldSetId);
                        sql.append(" set " + itemid + "='" + value + "'");
                        sql.append(" where a0100='" + a0100 + "'");
                        if(!"A01".equalsIgnoreCase(fieldSetId))
                            sql.append(" and i9999=" + i9999);
                        
                        updateSqlList.add(sql.toString());
                    }
                }
            }
            
            ContentDAO dao = new ContentDAO(this.frameconn);
            dao.batchUpdate(updateSqlList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

	/**
	 * 将科学计数法显示的数值转换为正常显示的数字
	 * 
	 * @param d
	 *            科学记数法显示的数字
	 * @return
	 */
	private static String formatDouble(double d) {
		NumberFormat nf = NumberFormat.getInstance();
		// 设置保留多少位小数
		nf.setMaximumFractionDigits(20);
		// 取消科学计数法
		nf.setGroupingUsed(false);
		// 返回结果
		return nf.format(d);
	}
}
