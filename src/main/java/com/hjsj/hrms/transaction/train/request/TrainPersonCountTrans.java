package com.hjsj.hrms.transaction.train.request;

import com.hjsj.hrms.businessobject.train.TrainClassBo;
import com.hjsj.hrms.businessobject.train.TrainEffectEvalBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.sql.SQLException;
import java.util.*;

/**
 * <p>
 * Title:培训班
 * </p>
 * <p>
 * Description:培训班内学院人数
 * </p>
 * 
 * @author liweichao
 * @version 4.0
 */
public class TrainPersonCountTrans extends IBusiness {

    public void execute() throws GeneralException {

        try {
            String classid = (String) this.getFormHM().get("classid");
            String msg = (String) this.getFormHM().get("msg");// 0：培训班归档  1：培训学员手动/条件选人 2：培训自助培训班报名申请 3:培训分析中加入新培训班 4:培训班代报名 5:培训学员批准 6：培训班学员导入
            
            if (!"0".equalsIgnoreCase(msg) && !"1".equalsIgnoreCase(msg) && !"5".equalsIgnoreCase(msg) && !"6".equalsIgnoreCase(msg)) {
                classid = PubFunc.decrypt(SafeCode.decode(classid));
            }

            HashMap hm = new HashMap();
            
            if ("0".equalsIgnoreCase(msg) || "1".equalsIgnoreCase(msg) || "3".equalsIgnoreCase(msg) || "5".equalsIgnoreCase(msg) 
            		|| "6".equalsIgnoreCase(msg)) {
                TrainClassBo cbo = new TrainClassBo(frameconn);
                if (!cbo.checkClassPiv(classid, this.userView))
                    throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("train.info.chang.nopiv")));
            }

            TrainEffectEvalBo bo = new TrainEffectEvalBo(this.frameconn, classid);
            String ctrl_apply = (String) bo.getBean("ctrl_apply", "").get("text");
            String flag = "true";
            int person = 0;
            if (classid.length() == 0 || classid == null) {
                this.getFormHM().put("flag", flag);
                return;
            }

            ContentDAO dao = new ContentDAO(this.getFrameconn());
            String sql = "select count(R4001) as person from R40 where R4005='" + classid + "'";
            if (!"0".equalsIgnoreCase(msg))
                sql += " and R4013='03'";
            this.frecset = dao.search(sql);
            if (this.frecset.next()) {
                person = this.frecset.getInt("person");
                this.getFormHM().put("person", String.valueOf(person));
            }

            
            if (!"0".equalsIgnoreCase(msg)) {
                String ctrl_count = (String) bo.getBean("ctrl_count", "").get("text");

                String personstr = (String) this.getFormHM().get("personstr");
                personstr = personstr != null && personstr.trim().length() > 0 ? personstr : "";
                
                if ("3".equalsIgnoreCase(msg))
                    personstr = getids(personstr);
                
                if ("5".equalsIgnoreCase(msg))
                    personstr = personstr.replaceAll("@", "`");
                
                if ("6".equalsIgnoreCase(msg)){
                	personstr = "";
                	hm = getStudents(classid);
                	String persons = (String) this.getFormHM().get("persons");
                	persons = persons != null && persons.trim().length() > 0 ? persons : "";
                	String[] students = null;
                	students = persons.split(",");
                	
                	for (int i = 0; i < students.length; i++) {
						if (!hm.containsKey(students[i])) 
							personstr += students[i] + "`";
        			}
                	
                	if(personstr.endsWith("`"))
                		personstr = personstr.substring(0, personstr.length()-1);
                }

                String[] personarr = null;
                if (personstr.length() > 0)
                    personarr = personstr.split("`");

                if ("1".equalsIgnoreCase(ctrl_count) && (!"6".equalsIgnoreCase(msg) || ("6".equalsIgnoreCase(msg) &&(personarr != null && personarr.length >0)))) {

                    int r3110 = 0;
                    this.frowset = dao.search("select r3110 from r31 where r3101='" + classid + "'");
                    if (this.frowset.next())
                        r3110 = this.frowset.getInt("r3110");

                    if (r3110 == 0) {
                        flag = ResourceFactory.getProperty("train.job.class.student.isnull");
                    } else {
                        if (person >= r3110)
                            flag = ResourceFactory.getProperty("train.job.class.student.isfull");
                        
                        if ((!("2".equalsIgnoreCase(msg) && "".equalsIgnoreCase(personstr)))
                                && !("4".equalsIgnoreCase(msg) && ("1".equalsIgnoreCase(ctrl_apply) || "".equalsIgnoreCase(ctrl_apply) || ctrl_apply == null))) {
                            if(personarr != null && personarr.length > 0)
                                person = person + personarr.length;
                            
                            if (person > r3110 && "true".equalsIgnoreCase(flag))
                                flag = ResourceFactory.getProperty("train.job.class.student.overfulfil");
                        }
                    }
                }
                
                if("true".equalsIgnoreCase(flag) && "1".equalsIgnoreCase(msg))
                    flag = isExistStudent(classid, personstr);
                        
                this.getFormHM().put("flag", SafeCode.encode(flag));
                this.getFormHM().put("personstr", personstr);
                this.getFormHM().put("r3101", SafeCode.encode(PubFunc.encrypt(classid)));
            }
        } catch (Exception e) {
            throw GeneralExceptionHandler.Handle(e);
        }

    }

    public String getids(String id) {
        String persons = "";
        id = SafeCode.decode(id);
        if (id.length() > 0 && id != null) {
            String[] ids = id.split(",");
            HashSet hs = new HashSet();
            for (int i = 0; i < ids.length; i++) {
                hs.add(ids[i]);
            }
            
            if (hs.size() > 0) {
                Iterator iterator = hs.iterator();
                while (iterator.hasNext()) {
                    persons = persons + iterator.next() + "`";
                }
            }
        }
        return persons;
    }
    /**
     * 获取该培训班下的所有已批状态的学员
     * @param classid 培训班编号
     * @return
     */
	private HashMap getStudents(String classid) {
		HashMap hm = new HashMap();
		String sql = "select r4001,r4002 from r40 where r4005='" + classid + "' and r4013='03'";
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			this.frowset= dao.search(sql);
			while(this.frowset.next()){
				hm.put(this.frowset.getString("r4001"), this.frowset.getString("r4002"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return hm;
	}
	
	/**
     * 查看添加的学员在培训班中是否存在
     * @param classid 培训班编号
     * @return
     */
    private String isExistStudent(String classid, String personIDs) {
        String StudentNames = "true";
        if(StringUtils.isEmpty(classid) || StringUtils.isEmpty(personIDs))
            return StudentNames;
        
        HashMap<String, String> personMap = new HashMap<String, String>();
        String[] persons = personIDs.split("`");
        for(int i = 0; i < persons.length; i++){
            String person = persons[i];
            if(StringUtils.isNotEmpty(person)){
                person = SafeCode.decode(person);
                String[] personInfo = person.split("::");
                String a0100 = PubFunc.decrypt(personInfo[0]);
                String nbase = PubFunc.decrypt(personInfo[4]);
                String a0100s = personMap.get(nbase);
                if(StringUtils.isEmpty(a0100s))
                    personMap.put(nbase, a0100);
                else {
                    a0100s += "','" + a0100;
                    personMap.put(nbase, a0100s);
                }
            }
        }
        
        ArrayList<String> valueList = new ArrayList<String>();
        StringBuffer sql = new StringBuffer();
        sql.append("select r4002 from r40");
        sql.append(" where r4005=?");
        sql.append(" and (");
        valueList.add(classid);
        Iterator iter = personMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String nbase = (String) entry.getKey();
            String a0100s = (String) entry.getValue();
            if(sql.indexOf("nbase") > -1)
                sql.append(" or");
                
            sql.append(" (nbase=? and r4001 in (?))");
            valueList.add(nbase);
            valueList.add(a0100s);
        }
        
        sql.append(")");
        
        ContentDAO dao = new ContentDAO(this.frameconn);
        try {
            this.frowset= dao.search(sql.toString(), valueList);
            while(this.frowset.next()){
                String name = StringUtils.isNotEmpty(this.frowset.getString("r4002")) ?
                        this.frowset.getString("r4002") : "    ";
                if("true".equalsIgnoreCase(StudentNames))
                    StudentNames = name;
                else
                    StudentNames += "，" + name;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        if(!"true".equalsIgnoreCase(StudentNames))
            StudentNames = "以下人员在此培训班中已存在：\n" + StudentNames;
        
        return StudentNames;
    }
    
}
