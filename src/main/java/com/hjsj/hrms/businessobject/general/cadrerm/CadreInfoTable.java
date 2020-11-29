/**
 * 
 */
package com.hjsj.hrms.businessobject.general.cadrerm;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import java.util.ArrayList;

/**
 * 
 * 干部信息表
 * @author Owner
 *
 */
public class CadreInfoTable {
	
	private String id;		 //编号
	private String name;	 //姓名
	private String sex;		 //性别
	private String birthDate;//出生日期
	private String imagePath; //图片路径
	private String age;      //年龄
	private String nation;   //民族
	private String birthAddress;   //出生地
	private String bodyStatus;     //身体状况
	private String address;        //籍贯
	private String enterWorkDate;  //参加工作时间
	private String enterPartyDate; //入党时间
	private String duty;           //专业技术职务
	private String teChang;        //特长
	
	//全日制
	private String qrzSchoolAge;      //学历
	private String qrzDegree;         //学位
	private String qrzSchool;         //毕业学校
	private String qrzSpecialty;	   //毕业专业

	//在职
	private String zSchoolAge;      //学历
	private String zDegree;         //学位
	private String zSchool;         //毕业学校
	private String zSpecialty;	   //毕业专业
	
	//当前单位职务
	private String currentDuty;    //当前职务
	private String currentOrg;     //当前单位
	
	//简历信息 
	private ArrayList resumeStartDate = new ArrayList(); //简历起始时间
	private ArrayList resumeEndDate = new ArrayList();   //简历终止时间
	private ArrayList resumeOrg = new ArrayList();	   //简历对应单位
	private ArrayList resumeDuty = new ArrayList();      //简历对应职务
	
	//奖励情况encouragement
	public ArrayList encouragementName = new ArrayList(); //奖励名称
	public ArrayList encouragementCausation = new ArrayList(); //奖励原因
	public ArrayList encouragementDate = new ArrayList(); //奖励时间
	public ArrayList encouragementOrg = new ArrayList();  //奖励单位
	
	//惩罚情况punish
	public ArrayList punishName = new ArrayList();      //处罚名称
	public ArrayList punishCausation = new ArrayList(); //处罚原因
	public ArrayList punishDate = new ArrayList();      //处罚日期
	public ArrayList punishOrg = new ArrayList();       //处罚单位
	
	
	//年度考核
	public ArrayList examDate = new ArrayList();
	public ArrayList examResult = new ArrayList();
	
	public String fosterDate = "";
	
	//家庭成员情况
	public ArrayList familyName = new ArrayList();	 //姓名
	public ArrayList familyRelation = new ArrayList(); //关系
	public ArrayList familyBirthDate = new ArrayList();//出生日期
	public ArrayList familyOrg = new ArrayList();      //单位
	public ArrayList familyZzmm = new ArrayList();     //政治面貌
	
	public ArrayList familyBirthDateTxt = new ArrayList();
	
	//社会关系情况
	public ArrayList communityName = new ArrayList();	 //姓名
	public ArrayList communityRelation = new ArrayList(); //关系
	public ArrayList communityBirthDate = new ArrayList();//出生日期
	public ArrayList communityOrg = new ArrayList();      //单位
	public ArrayList communityZzmm = new ArrayList();     //政治面貌
	
	public ArrayList communityBirthDateTxt = new ArrayList();
	
	public String fillInTableName;  //填报人
	public String fillInTableDate;  //填表时间
	public String ageDate;   //计算年龄时间
	
	public CadreInfoTable() {
		
	}
	
	/**
	 * 格式化输出
	 */
	public String toTxt(String userName){
		/*格式
		"艾永平","男","196204","汉族","安徽省怀远县","199501；无党派","健康或良好","安徽省宿州市","198307",
		"高中毕业#@大学毕业#","44#@sss#","教授级高级工程师","","","","","无",
		"1983.08－2001.04  国家电力公司成都勘测设计研究院
		2001.04－2005.03  云南华能澜沧江水电开发有限公司
		2003.03－2004.04  云南华能澜沧江水电开发有限公司小湾建设公司
		2005.03 至今      云南华能澜沧江水电开发有限公司
		","无","优秀","妻@父子@@@@@@@@","刘静@艾博@@@@@@@@","@@@@@@@@@","中共党员@共青团员@@@@@@@@","国家电力公司成都勘测设计研究院@成都市三元外国语学校@@@@@@@@","","","","","","200610","su"
		
		"寇伟","男","196110","白族","云南大理州","196508","健康或良好","云南昆明市","198308",
		"大学毕业#学士@研究生毕业#硕士","云南工学院#@天津大学华种科技大学#","教授级高级工程师","","","","","无","","无","",
		"@@@@@@@@@","@@@@@@@@@","@@@@@@@@@","@@@@@@@@@","@@@@@@@@@","","","","","","200610","su"
		*/
		StringBuffer cadreInfo = new StringBuffer();
		
		cadreInfo.append("\"");
		cadreInfo.append(this.getName()); //姓名
		cadreInfo.append("\",");
		
		cadreInfo.append("\"");
		cadreInfo.append(this.getSex()); //性别
		cadreInfo.append("\",");
		
		cadreInfo.append("\"");
		String birthDate = this.getBirthDate();
		cadreInfo.append(birthDate.replaceAll("\\.","")); //出生年月
		cadreInfo.append("\",");
		
		cadreInfo.append("\"");
		cadreInfo.append(this.getNation()); //民族
		cadreInfo.append("\",");
		
		cadreInfo.append("\"");
		cadreInfo.append(this.getAddress()); //籍贯
		cadreInfo.append("\",");
		
		cadreInfo.append("\"");
		String ep = this.getEnterPartyDate();
		ep = ep.substring(0,4)+ep.substring(5,ep.length());
		cadreInfo.append(ep); //入党时间
		cadreInfo.append("\",");
		
		cadreInfo.append("\"");
		cadreInfo.append(this.getBodyStatus()); //身体状况
		cadreInfo.append("\",");
		
		cadreInfo.append("\"");
		cadreInfo.append(this.getBirthAddress()); //出生地
		cadreInfo.append("\",");
		
		cadreInfo.append("\"");
		String enterWorkDate = this.getEnterWorkDate();
		cadreInfo.append(enterWorkDate.replaceAll("\\.","")); //参加工作时间
		cadreInfo.append("\",");
		
		cadreInfo.append("\"");
		cadreInfo.append(this.getQrzSchoolAge()); //全日制学历
		cadreInfo.append("#");
		cadreInfo.append(this.getQrzDegree());//全日制学位
		cadreInfo.append("@");
		cadreInfo.append(this.getZSchoolAge()); //在职学历
		cadreInfo.append("#");
		cadreInfo.append(this.getZDegree()); //在职学位
		cadreInfo.append("\",");
		
		cadreInfo.append("\"");
		cadreInfo.append(this.getQrzSchool()); //全日制学校
		cadreInfo.append("#");
		cadreInfo.append(this.getQrzSpecialty());//全日制专业
		cadreInfo.append("@");
		cadreInfo.append(this.getZSchool()); //在职学校
		cadreInfo.append("#");
		cadreInfo.append(this.getZSpecialty()); //在职专业
		cadreInfo.append("\",");
		
		cadreInfo.append("\"");
		cadreInfo.append(this.getDuty()); //专业技术职务
		cadreInfo.append("\",");
		
		cadreInfo.append("\"");
		cadreInfo.append("\",");
		cadreInfo.append("\"");
		cadreInfo.append("\",");
		cadreInfo.append("\"");
		cadreInfo.append("\",");
		cadreInfo.append("\"");
		cadreInfo.append("\",");
		
		cadreInfo.append("\"");
		cadreInfo.append(this.getTeChang());//专长
		cadreInfo.append("\",");
		
		//简历
		cadreInfo.append("\"");
		for(int i=0; i< resumeStartDate.size(); i++){
			cadreInfo.append((String)this.getResumeStartDate().get(i));//简历开始时间
			cadreInfo.append(" - ");
			String temp = (String)this.getResumeEndDate().get(i);
			if("".equals(temp)){
				temp="至今";
			}
			cadreInfo.append(temp);//简历结束时间
			cadreInfo.append(" " + (String)this.getResumeOrg().get(i));//简历对应单位
			cadreInfo.append(" " + (String)this.getResumeDuty().get(i));//简历对应职务
			cadreInfo.append("\r\n");
		}	
		cadreInfo.append("\",");
		
		//奖惩情况
		cadreInfo.append("\"");
		for(int i=0; i< encouragementName.size(); i++){
			String ed = (String)this.getEncouragementDate().get(i);
			ed = ed.replaceAll("\\.","年");
			ed = ed + "月";
			cadreInfo.append(" " + ed);//奖励日期			
			cadreInfo.append(" "+((String)this.getEncouragementCausation().get(i)).trim());//奖励原因		
			cadreInfo.append(" " + (String)this.getEncouragementOrg().get(i));//奖励单位
			cadreInfo.append(" "+(String)this.getEncouragementName().get(i));//奖励名称
			cadreInfo.append("\n");
		}
		for(int i=0; i< punishName.size(); i++){
			String pd = (String)this.getPunishDate().get(i);
			pd = pd.replaceAll("\\.","年");
			pd = pd + "月";
			cadreInfo.append(" " + pd);//惩罚日期
			cadreInfo.append(" "+((String)this.getPunishCausation().get(i)).trim());//惩罚原因
			cadreInfo.append(" " + (String)this.getPunishOrg().get(i));//惩罚单位
			cadreInfo.append(" "+(String)this.getPunishName().get(i));//惩罚名称
			cadreInfo.append("\n");
		}
		if(encouragementName.size()==0 && punishName.size()==0){
			cadreInfo.append("无");
		}
		cadreInfo.append("\",");
		
		cadreInfo.append("\"");
		int en = this.getExamDate().size();
		String te="";
		for(int i=0; i< en; i++){
			te+=(String)this.getExamDate().get(i)+"年年度考核"+(String)this.getExamResult().get(i)+", ";
		}
		cadreInfo.append(te);//年度考核结果
		cadreInfo.append("\",");
	
		//关系
		cadreInfo.append("\"");		
		int n = familyRelation.size();
		for(int i=0; i<n;i++ ){
			cadreInfo.append((String)this.getFamilyRelation().get(i));
			cadreInfo.append("@");
		}
		int nn = communityRelation.size();
		for(int i=0; i<nn;i++ ){
			cadreInfo.append((String)this.getCommunityRelation().get(i));
			cadreInfo.append("@");
		}
		n+=nn;
		for(int i= n; i<9; i++){
			cadreInfo.append("@");
		}
		cadreInfo.append("\",");
		
		//姓名
		cadreInfo.append("\"");		
		int n1 = familyName.size();
		for(int i=0; i<n1;i++ ){
			cadreInfo.append((String)this.getFamilyName().get(i));
			cadreInfo.append("@");
		}
		int nn1 = this.communityName.size();
		for(int i=0; i<nn1;i++ ){
			cadreInfo.append((String)this.getCommunityName().get(i));
			cadreInfo.append("@");
		}
		n1+=nn1;
		for(int i= n1; i<9; i++){
			cadreInfo.append("@");
		}
		cadreInfo.append("\",");
		
		
		
		//出生日期
		cadreInfo.append("\"");		
		int n2 = this.familyBirthDateTxt.size();
		for(int i=0; i<n2;i++ ){
			cadreInfo.append((String)this.getFamilyBirthDateTxt().get(i));
			cadreInfo.append("@");
		}
		int nn2 = this.communityBirthDateTxt.size();
		for(int i=0; i<nn2;i++ ){
			cadreInfo.append((String)this.getCommunityBirthDateTxt().get(i));
			cadreInfo.append("@");
		}
		n2+=nn2;
		for(int i= n2; i<9; i++){
			cadreInfo.append("@");
		}
		cadreInfo.append("\",");
		
		//政治面貌
		cadreInfo.append("\"");		
		int n3 = this.familyZzmm.size();
		for(int i=0; i<n3;i++ ){
			cadreInfo.append((String)this.getFamilyZzmm().get(i));
			cadreInfo.append("@");
		}
		int nn3 = this.communityZzmm.size();
		for(int i=0; i<nn3;i++ ){
			cadreInfo.append((String)this.getCommunityZzmm().get(i));
			cadreInfo.append("@");
		}
		n3+=nn3;
		for(int i= n3; i<9; i++){
			cadreInfo.append("@");
		}
		cadreInfo.append("\",");
	
		
		
		//工作单位及职务
		cadreInfo.append("\"");		
		int n4 = this.familyOrg.size();
		for(int i=0; i<n4;i++ ){
			cadreInfo.append((String)this.getFamilyOrg().get(i));
			cadreInfo.append("@");
		}
		int nn4 = this.communityOrg.size();
		for(int i=0; i<nn4;i++ ){
			cadreInfo.append((String)this.getCommunityOrg().get(i));
			cadreInfo.append("@");
		}
		n4+=nn4;
		for(int i= n4; i<9; i++){
			cadreInfo.append("@");
		}
		cadreInfo.append("\",");
		
		cadreInfo.append("\"");
		cadreInfo.append("\",");		
		cadreInfo.append("\"");
		cadreInfo.append("\",");
		cadreInfo.append("\"");
		cadreInfo.append("\",");
		cadreInfo.append("\"");
		cadreInfo.append("\",");
		cadreInfo.append("\"");
		cadreInfo.append("\",");
		
		cadreInfo.append("\"");
		cadreInfo.append(this.getFillInTableDate());//当前年月
		cadreInfo.append("\",");
		
		cadreInfo.append("\"");
		cadreInfo.append(userName);//操纵用户名
		cadreInfo.append("\",");
		
		
		
		return cadreInfo.toString();
	}

	
	/**
	 * WORD_html 填充 数据
	 * @param doc        文档对象
	 * @param fieldFlag  ID标识
	 * @param fieldValue 值
	 */
	public void setValue(Document doc , String fieldFlag , String fieldValue){
		if(fieldValue == null || "".equals(fieldValue)){
			return;
		}
		StringBuffer temp = new StringBuffer();
		temp.append("/body/div/table/tr/td[@id='");
		temp.append(fieldFlag);
		temp.append("']");
		try {
			XPath xPath = XPath.newInstance(temp.toString());
			Element td = (Element) xPath.selectSingleNode(doc);
			if(td == null){	
			}else{
				td.setText(fieldValue);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * XML形式配置word文件
	 * @param doc
	 * @param userName
	 */
	public void toWord(Document doc,String userName){
		
		this.setValue(doc,"A0101",this.getName());
		this.setValue(doc,"A0107","   "+this.getSex());
		this.setValue(doc,"A0111",this.getBirthDate()+"<br></br>("+this.getAge()+"岁)");
		this.setValue(doc,"IMG",this.getImagePath());
		
		String nation = this.getNation();
		if(nation.length()>6){
			nation = nation.substring(0,6);
		}
		this.setValue(doc,"A0121",nation);
		
		String address = this.getAddress();
		if(address.length() > 8){
			address = address.substring(0,8);
		}	
		this.setValue(doc,"A0114",address);
		
		String birthAddress = this.getBirthAddress();
		if(birthAddress.length() > 6){
			birthAddress = birthAddress.substring(0,6);
		}	
		this.setValue(doc,"A0117",birthAddress);
		
		
		this.setValue(doc,"A2210",this.getEnterPartyDate());
		this.setValue(doc,"A0141",this.getEnterWorkDate());
		
		String bodyStatus = this.getBodyStatus();
		if(bodyStatus.length()>6){
			bodyStatus = bodyStatus.substring(0,6);
		}
		this.setValue(doc,"A0124",bodyStatus);
		
		String duty = this.getDuty();
		if(duty == null || "".equals(duty)){
			duty="    无";
		}
		this.setValue(doc,"A1005",duty);
		
		String tc = this.getTeChang();
		if(tc == null || "".equals(tc)){
			tc="    无";
		}else{
			tc = tc.substring(0,16);
		}
		this.setValue(doc,"E1003",tc);
		
		//全日制教育
		String qsa = this.getQrzSchoolAge();
		String qd = this.getQrzDegree();
		if(qsa == null){
			qsa ="";
		}
		if(qd == null){
			qd="";
		}
		if( !"".equals(qsa) && !"".equals(qd)){
			this.setValue(doc,"QA0405",qsa +"<br></br>" + qd);
		}else{
			this.setValue(doc,"QA0405",qsa + qd);
		}
			
		//毕业院校及专业
		String qs = this.getQrzSchool();
		String qsy = this.getQrzSpecialty();
		if(qs == null){
			qs="";
		}
		if(qsy == null){
			qsy="";
		}
		String he = qs + qsy;
		if(he.length()>22){
			he = he.substring(0,22);
		}
		if(he == null){
			
		}else{
			this.setValue(doc,"QA0435",he);
		}
		
		
		

	    //在职教育
		String za = this.getZSchoolAge();
		String zd = this.getZDegree();
		if(za == null){
			za="";
		}
		if(zd == null){
			zd="";
		}
		if(!"".equals(za) && !"".equals(zd)){
			this.setValue(doc,"ZA0405",za + "<br></br>" +zd);
		}else{
			this.setValue(doc,"ZAO405",za +zd);
		}
		
	    //毕业院校及专业
		this.setValue(doc,"ZA0435",this.getZSchool()+this.getZSpecialty());
		
	    //现任职务
		this.setValue(doc,"A07",this.getCurrentOrg()+" " +this.getCurrentDuty());
		//拟任职务(无)
		//拟免职务(无)
		
		
	    //简历
		StringBuffer temp = new StringBuffer();
		for(int i=0; i< this.getResumeStartDate().size(); i++){
			temp.append((String)this.getResumeStartDate().get(i));//简历开始时间
			temp.append(" - ");
			String temp1 = (String)this.getResumeEndDate().get(i);
			if("".equals(temp1)){
				temp1="至今";
			}
			temp.append(temp1);//简历结束时间
			temp.append("  " + (String)this.getResumeOrg().get(i));//简历对应单位
			temp.append("  " + (String)this.getResumeDuty().get(i));//简历对应职务
			temp.append("<br></br>");
		}
		this.setValue(doc,"A19",temp.toString());
	
	    //奖惩情况
		StringBuffer temp2 = new StringBuffer();
		for(int i=0; i< this.getEncouragementName().size(); i++){

			if(this.getEncouragementDate().size()!=0){
				temp2.append(" " + (String)this.getEncouragementDate().get(i));//奖励日期
			}else{
				temp2.append(" ");//奖励日期
			}
			
			if(this.getEncouragementCausation().size()!=0){
				temp2.append(" "+(String)this.getEncouragementCausation().get(i));//奖励原因
			}else{
				temp2.append(" ");//奖励原因
			}

			if(this.getEncouragementOrg().size()!=0){
				temp2.append(" " + (String)this.getEncouragementOrg().get(i));//奖励单位
			}else{
				temp2.append(" ");//奖励单位
			}
			
			temp2.append((String)this.getEncouragementName().get(i));//奖励名称
			
			temp2.append("<br></br>");
		}
		
		for(int i=0; i< this.getPunishName().size(); i++){
			
			if(this.getPunishDate().size() !=0){
				temp2.append(" " + (String)this.getPunishDate().get(i));//惩罚日期
			}else{
				temp2.append(" " );//惩罚日期
			}
			
			if(this.getPunishCausation().size()!=0){
				temp2.append(" "+(String)this.getPunishCausation().get(i));//惩罚原因
			}else{
				temp2.append(" ");//惩罚原因
			}
			
			if(this.getPunishOrg().size()!=0){
				temp2.append(" " + (String)this.getPunishOrg().get(i));//惩罚单位
			}else{
				temp2.append(" ");//惩罚单位
			}
			
			temp2.append((String)this.getPunishName().get(i));//惩罚名称			
			temp2.append("<br></br>");
		}
		if(this.getEncouragementName().size()==0 && this.getPunishName().size()==0){
			temp2.append("无");
		}
		this.setValue(doc,"A28",temp2.toString());
	
	    //年度考核结果
		int en = this.getExamDate().size();
		String te="";
		for(int i=0; i< en; i++){
			te+=(String)this.getExamDate().get(i)+"年年度考核"+(String)this.getExamResult().get(i)+", ";
		}
		this.setValue(doc,"A25",te);
		
		
	    //培训情况
		
		String d = this.getFosterDate();
		if(d == null || "".equals(d)){
			this.setValue(doc,"A37","无");
		}else{
			this.setValue(doc,"A37","累计培训时间达"+d+"学时");
		}
		
	    //任免理由
		//this.setValue(doc,"RMLY","工作需要");

		int familyFlag = this.getFamilyName().size();  //家庭成员个数
		int communityFlag = this.getCommunityName().size();//社会成员个数
		
		//System.out.println("familyFlag=" + familyFlag + "    communityFlag="  + communityFlag);
		
		int ff = familyFlag;
		int cf = 0;
		if(familyFlag >6 ){
			ff = 6;
		}else{
			cf = 6-ff;
		}
		
		if(communityFlag == 0){
			cf=0;
		}else if(communityFlag <cf){
			cf=communityFlag;
		}
		
		int k = 0;
		int g = 0;
		boolean fb = false;
		boolean cb = false;
		boolean bb = false;
		
		if(ff > 0){	
			fb = true;
			cb = false;
			bb = false;
			ff--;
			k++;
		}else{
			if(cf >0){
				fb = false;
				cb = true;
				bb = false;
				cf--;
				g++;
			}else{
				fb = false;
				cb = false;
				bb = true;
			}
		}

		
	//称谓1
		if(fb){		
			this.setValue(doc,"A7900",(String)this.getFamilyRelation().get(k-1));	
		}else if(cb){
			this.setValue(doc,"A7900",(String)this.getCommunityRelation().get(g-1));
		}else if(bb){
			
		}

	
	//姓名1
		if(fb){
			this.setValue(doc,"A7910",(String)this.getFamilyName().get(k-1));
		}else if(cb){
			this.setValue(doc,"A7910",(String)this.getCommunityName().get(g-1));
		}else if(bb){
			
		}

		
	//出生年月
		if(fb){
			this.setValue(doc,"A7920",(String)this.getFamilyBirthDate().get(k-1));
		}else if(cb){
			this.setValue(doc,"A7920",(String)this.getCommunityBirthDate().get(g-1));
		}else if(bb){
			
		}

	//政治面貌
		if(fb){
			if(this.getFamilyZzmm().size()<(k)){
				
			}else{
				this.setValue(doc,"A7930",(String)this.getFamilyZzmm().get(k-1));
			}
		}else if(cb){
			if(this.getCommunityZzmm().size()<(g)){
				
			}else{
				this.setValue(doc,"A7930",(String)this.getCommunityZzmm().get(g-1));
			}
		}else if(bb){
			
		}

		
	//工作单位及职务
		if(fb){
			this.setValue(doc,"A7940",(String)this.getFamilyOrg().get(k-1));
		}else if(cb){
			this.setValue(doc,"A7940",(String)this.getCommunityOrg().get(g-1));
		}else if(bb){
			
		}
		
		if(ff > 0){	
			fb = true;
			cb = false;
			bb = false;
			ff--;
			k++;
		}else{
			if(cf >0){
				fb = false;
				cb = true;
				bb = false;
				cf--;
				g++;
			}else{
				fb = false;
				cb = false;
				bb = true;
			}
		}
    //称谓2
		if(fb){		
			this.setValue(doc,"A7901",(String)this.getFamilyRelation().get(k-1));	
		}else if(cb){
			this.setValue(doc,"A7901",(String)this.getCommunityRelation().get(g-1));
		}else if(bb){
		}
		
		
		
	//姓名2
		if(fb){
			this.setValue(doc,"A7911",(String)this.getFamilyName().get(k-1));
		}else if(cb){
			this.setValue(doc,"A7911",(String)this.getCommunityName().get(g-1));
		}else if(bb){
			
		}

//		出生年月
		if(fb){
			this.setValue(doc,"A7921",(String)this.getFamilyBirthDate().get(k-1));
		}else if(cb){
			this.setValue(doc,"A7921",(String)this.getCommunityBirthDate().get(g-1));
		}else if(bb){
			
		}
		
		
//		政治面貌
		if(fb){
			if(this.getFamilyZzmm().size()<(k)){
				
			}else{
				this.setValue(doc,"A7931",(String)this.getFamilyZzmm().get(k-1));
			}
		}else if(cb){
			if(this.getCommunityZzmm().size()<(g)){
				
			}else{
				this.setValue(doc,"A7931",(String)this.getCommunityZzmm().get(g-1));
			}
		}else if(bb){
			
		}
		
//		工作单位及职务
		if(fb){
			this.setValue(doc,"A7941",(String)this.getFamilyOrg().get(k-1));
		}else if(cb){
			this.setValue(doc,"A7941",(String)this.getCommunityOrg().get(g-1));
		}else if(bb){
			
		}
		
		
		if(ff > 0){	
			fb = true;
			cb = false;
			bb = false;
			ff--;
			k++;
		}else{
			if(cf >0){
				fb = false;
				cb = true;
				bb = false;
				cf--;
				g++;
			}else{
				fb = false;
				cb = false;
				bb = true;
			}
		}

    //称谓3
		if(fb){		
			this.setValue(doc,"A7902",(String)this.getFamilyRelation().get(k-1));	
		}else if(cb){
			this.setValue(doc,"A7902",(String)this.getCommunityRelation().get(g-1));
		}else if(bb){
			
		}
		
		
   //姓名3
		if(fb){
			this.setValue(doc,"A7912",(String)this.getFamilyName().get(k-1));
		}else if(cb){
			this.setValue(doc,"A7912",(String)this.getCommunityName().get(g-1));
		}else if(bb){
			
		}

//		出生年月
		if(fb){
			this.setValue(doc,"A7922",(String)this.getFamilyBirthDate().get(k-1));
		}else if(cb){
			this.setValue(doc,"A7922",(String)this.getCommunityBirthDate().get(g-1));
		}else if(bb){
			
		}
	
//		政治面貌
		if(fb){
			if(this.getFamilyZzmm().size()<(k)){
				
			}else{
				this.setValue(doc,"A7932",(String)this.getFamilyZzmm().get(k-1));
			}
		}else if(cb){
			if(this.getCommunityZzmm().size()<(g)){
				
			}else{
				this.setValue(doc,"A7932",(String)this.getCommunityZzmm().get(g-1));
			}
		}else if(bb){
			
		}
		
//		工作单位及职务
		if(fb){
			this.setValue(doc,"A7942",(String)this.getFamilyOrg().get(k-1));
		}else if(cb){
			this.setValue(doc,"A7942",(String)this.getCommunityOrg().get(g-1));
		}else if(bb){
			
		}
		
		if(ff > 0){	
			fb = true;
			cb = false;
			bb = false;
			ff--;
			k++;
		}else{
			if(cf >0){
				fb = false;
				cb = true;
				bb = false;
				cf--;
				g++;
			}else{
				fb = false;
				cb = false;
				bb = true;
			}
		}
		//System.out.println("ff=" + ff + " cf = " + cf);
		//System.out.println("k =" + (k-1) + "    g=" + (g-1));
		
   //称谓4
		if(fb){		
			this.setValue(doc,"A7903",(String)this.getFamilyRelation().get(k-1));	
		}else if(cb){
			this.setValue(doc,"A7903",(String)this.getCommunityRelation().get(g-1));
		}else if(bb){
			
		}
		
  //姓名4
		if(fb){
			this.setValue(doc,"A7913",(String)this.getFamilyName().get(k-1));
		}else if(cb){
			this.setValue(doc,"A7913",(String)this.getCommunityName().get(g-1));
		}else if(bb){
		
		}

//		出生年月
		if(fb){
			this.setValue(doc,"A7923",(String)this.getFamilyBirthDate().get(k-1));
		}else if(cb){
			this.setValue(doc,"A7923",(String)this.getCommunityBirthDate().get(g-1));
		}else if(bb){
			
		}
		
//		政治面貌
		if(fb){
			if(this.getFamilyZzmm().size()<(k)){
				
			}else{
				this.setValue(doc,"A7933",(String)this.getFamilyZzmm().get(k-1));
			}
		}else if(cb){
			if(this.getCommunityZzmm().size()<(g)){
				
			}else{
				this.setValue(doc,"A7933",(String)this.getCommunityZzmm().get(g-1));
			}
		}else if(bb){
			
		}
		
//		工作单位及职务
		if(fb){
			this.setValue(doc,"A7943",(String)this.getFamilyOrg().get(k-1));
		}else if(cb){
			this.setValue(doc,"A7943",(String)this.getCommunityOrg().get(g-1));
		}else if(bb){
			
		}

		if(ff > 0){	
			fb = true;
			cb = false;
			bb = false;
			ff--;
			k++;
		}else{
			if(cf >0){
				fb = false;
				cb = true;
				bb = false;
				cf--;
				g++;
			}else{
				fb = false;
				cb = false;
				bb = true;
			}
		}
		//System.out.println("ff=" + ff + " cf = " + cf);
		//System.out.println("k =" + (k-1) + "    g=" + (g-1));
		
	//称谓5
		if(fb){		
			this.setValue(doc,"A7904",(String)this.getFamilyRelation().get(k-1));	
		}else if(cb){
			this.setValue(doc,"A7904",(String)this.getCommunityRelation().get(g-1));
		}else if(bb){
			
		}

//		姓名5
		if(fb){
			this.setValue(doc,"A7914",(String)this.getFamilyName().get(k-1));
		}else if(cb){
			this.setValue(doc,"A7914",(String)this.getCommunityName().get(g-1));
		}else if(bb){
			
		}
		
//		出生年月
		if(fb){
			this.setValue(doc,"A7924",(String)this.getFamilyBirthDate().get(k-1));
		}else if(cb){
			this.setValue(doc,"A7924",(String)this.getCommunityBirthDate().get(g-1));
		}else if(bb){
			
		}
		
//		政治面貌
		if(fb){
			if(this.getFamilyZzmm().size()<(k)){
				
			}else{
				this.setValue(doc,"A7934",(String)this.getFamilyZzmm().get(k-1));
			}
		}else if(cb){
			if(this.getCommunityZzmm().size()<(g)){
				
			}else{
				this.setValue(doc,"A7934",(String)this.getCommunityZzmm().get(g-1));
			}
		}else if(bb){
			
		}

//		工作单位及职务
		if(fb){
			this.setValue(doc,"A7944",(String)this.getFamilyOrg().get(k-1));
		}else if(cb){
			this.setValue(doc,"A7944",(String)this.getCommunityOrg().get(g-1));
		}else if(bb){
			
		}
		
		if(ff > 0){	
			fb = true;
			cb = false;
			bb = false;
			ff--;
			k++;
		}else{
			if(cf >0){
				fb = false;
				cb = true;
				bb = false;
				cf--;
				g++;
			}else{
				fb = false;
				cb = false;
				bb = true;
			}
		}
		//System.out.println("ff=" + ff + " cf = " + cf);
		//System.out.println("k =" + (k-1) + "    g=" + (g-1));
		
	    //称谓6
		if(fb){		
			this.setValue(doc,"A7905",(String)this.getFamilyRelation().get(k-1));	
		}else if(cb){
			this.setValue(doc,"A7905",(String)this.getCommunityRelation().get(g-1));
		}else if(bb){
			
		}
		
		//姓名6
		if(fb){
			this.setValue(doc,"A7915",(String)this.getFamilyName().get(k-1));
		}else if(cb){
			this.setValue(doc,"A7915",(String)this.getCommunityName().get(g-1));
		}else if(bb){
			
		}
		//出生年月
		if(fb){
			this.setValue(doc,"A7925",(String)this.getFamilyBirthDate().get(k-1));
		}else if(cb){
			this.setValue(doc,"A7925",(String)this.getCommunityBirthDate().get(g-1));
		}else if(bb){
			
		}
		
		//政治面貌
		if(fb){
			if(this.getFamilyZzmm().size()<(k)){
				
			}else{
				this.setValue(doc,"A7935",(String)this.getFamilyZzmm().get(k-1));
			}
		}else if(cb){
			if(this.getCommunityZzmm().size()<(g)){
				
			}else{
				this.setValue(doc,"A7935",(String)this.getCommunityZzmm().get(g-1));
			}
		}else if(bb){
			
		}
		
		
		
		//工作单位及职务
		if(fb){
			this.setValue(doc,"A7945",(String)this.getFamilyOrg().get(k-1));
		}else if(cb){
			this.setValue(doc,"A7945",(String)this.getCommunityOrg().get(g-1));
		}else if(bb){
			
		}
		
		
		//填表人
		StringBuffer temp1 = new StringBuffer();
		temp1.append("/body/div/p/span[@id='");
		temp1.append("NAME");
		temp1.append("']");
		try {
			XPath xPath = XPath.newInstance(temp1.toString());
			Element td = (Element) xPath.selectSingleNode(doc);
			if(td == null){	
			}else{
				td.setText("填表人<u>"+userName+"</u>");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 格式化输出HTML word 格式
	 * @param userName
	 * @return
	 */
	public String toWord(String userName){
	

		StringBuffer cadreHtml = new StringBuffer();

		//cadreHtml.append("<body lang=ZH-CN style='tab-interval:21.0pt;text-justify-trim:punctuation'>");
		
		cadreHtml.append("");
		cadreHtml.append("		<div class=Section1 style='layout-grid:15.6pt'>");
		cadreHtml.append("");
		cadreHtml
				.append("			<p class=MsoNormal align=center style='margin-right:-.12gd;text-align:center'>");
		cadreHtml
				.append("				<b><span style='font-size:22.0pt;mso-bidi-font-size:12.0pt;font-family:宋体;mso-ascii-font-family:");
		cadreHtml
				.append("\"Times New Roman\";mso-hansi-font-family:\"Times New Roman\";letter-spacing:3.0pt'>干部任免审批表</span></b><b><span lang=EN-US");
		cadreHtml.append("					style='font-size:22.0pt;");
		cadreHtml
				.append("mso-bidi-font-size:12.0pt;letter-spacing:3.0pt'><o:p></o:p></span></b>");
		cadreHtml.append("			</p>");
		cadreHtml.append("");
		cadreHtml
				.append("			<table border=1 cellspacing=0 cellpadding=0 width=571 style='width:428.55pt;");
		cadreHtml
				.append(" margin-left:23.55pt;border-collapse:collapse;border:none;mso-border-alt:solid windowtext .5pt;");
		cadreHtml.append(" mso-padding-alt:0cm 5.4pt 0cm 5.4pt'>");
		cadreHtml.append("				<tr style='height:31.2pt'>");
		cadreHtml
				.append("					<td width=67 colspan=2 style='width:50.4pt;border:solid windowtext .5pt;");
		cadreHtml.append("  padding:0cm 5.4pt 0cm 5.4pt;height:31.2pt'>");
		cadreHtml
				.append("						<p class=MsoNormal align=center style='text-align:center'>");
		cadreHtml
				.append("							<span style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>姓</span><span lang=EN-US style='font-size:12.0pt'><span style=\"mso-spacerun: yes\">&nbsp; </span></span><span");
		cadreHtml
				.append("								style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>名</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml.append("					</td>");
		cadreHtml
				.append("					<td width=77 colspan=3 style='width:57.6pt;border:solid windowtext .5pt;");
		cadreHtml
				.append("  border-left:none;mso-border-left-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;");
		cadreHtml.append("  height:31.2pt'>");
		
	//姓名
		cadreHtml.append(this.getName());
		
		cadreHtml.append("					</td>");
		cadreHtml
				.append("					<td width=70 style='width:52.7pt;border:solid windowtext .5pt;border-left:");
		cadreHtml
				.append("  none;mso-border-left-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;");
		cadreHtml.append("  height:31.2pt'>");
		cadreHtml
				.append("						<p class=MsoNormal align=center style='text-align:center'>");
		cadreHtml
				.append("							<a name=\"A0101_1\"></a><span style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>性</span><span lang=EN-US style='font-size:12.0pt'><span style=\"mso-spacerun: yes\">&nbsp; </span></span><span");
		cadreHtml
				.append("								style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>别</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml.append("					</td>");
		cadreHtml
				.append("					<td width=86 style='width:64.3pt;border:solid windowtext .5pt;border-left:");
		cadreHtml
				.append("  none;mso-border-left-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;");
		cadreHtml.append("  height:31.2pt'>");
		
	//性别
		cadreHtml.append("&nbsp;&nbsp;&nbsp;"+this.getSex());
		
		cadreHtml.append("					</td>");
		cadreHtml
				.append("					<td width=79 style='width:59.55pt;border:solid windowtext .5pt;border-left:");
		cadreHtml
				.append("  none;mso-border-left-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;");
		cadreHtml.append("  height:31.2pt'>");
		cadreHtml
				.append("						<p class=MsoNormal align=center style='text-align:center'>");
		cadreHtml
				.append("							<a name=\"A0104_2\"></a><span style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>出生年月</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml
				.append("						<p class=MsoNormal align=center style='text-align:center'>");
		cadreHtml
				.append("							<span style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>（</span><span lang=EN-US style='font-size:12.0pt'><span style=\"mso-spacerun: yes\">&nbsp; </span></span><span");
		cadreHtml
				.append("								style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>岁）</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml.append("					</td>");
		cadreHtml
				.append("					<td width=77 style='width:57.45pt;border:solid windowtext .5pt;border-left:");
		cadreHtml
				.append("  none;mso-border-left-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;");
		cadreHtml.append("  height:31.2pt'>");
		
	//出生年月
		cadreHtml.append(this.getBirthDate()+"\n("+this.getAge()+"岁)");
		
		cadreHtml.append("					</td>");
		cadreHtml
				.append("					<td width=115 nowrap rowspan=4 style='width:86.55pt;border:solid windowtext .5pt;");
		cadreHtml
				.append("  border-left:none;mso-border-left-alt:solid windowtext .5pt;padding:0cm 0cm 0cm 0cm;");
		cadreHtml.append("  height:31.2pt'>");
		cadreHtml.append("						<a name=\"A0107_3\"></a>");
		
	//图片
		cadreHtml.append(this.getImagePath());
		
		cadreHtml.append("					</td>");
		cadreHtml.append("					<a name=\"P0192A_12\"></a>");
		cadreHtml.append("				</tr>");
		cadreHtml.append("				<tr style='height:31.2pt'>");
		cadreHtml
				.append("					<td width=67 colspan=2 style='width:50.4pt;border:solid windowtext .5pt;");
		cadreHtml
				.append("  border-top:none;mso-border-top-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;");
		cadreHtml.append("  height:31.2pt'>");
		cadreHtml
				.append("						<p class=MsoNormal align=center style='text-align:center'>");
		cadreHtml
				.append("							<span style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>民</span><span lang=EN-US style='font-size:12.0pt'><span style=\"mso-spacerun: yes\">&nbsp; </span></span><span");
		cadreHtml
				.append("								style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>族</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=77 colspan=3");
		cadreHtml
				.append("						style='width:57.6pt;border-top:none;border-left:none;");
		cadreHtml
				.append("  border-bottom:solid windowtext .5pt;border-right:solid windowtext .5pt;");
		cadreHtml
				.append("  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;");
		cadreHtml.append("  padding:0cm 5.4pt 0cm 5.4pt;height:31.2pt'>");
	
	//民族
		String nation = this.getNation();
		if(nation.length()>6){
			nation = nation.substring(0,6);
		}
		cadreHtml.append(nation);
		
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=70");
		cadreHtml
				.append("						style='width:52.7pt;border-top:none;border-left:none;border-bottom:");
		cadreHtml
				.append("  solid windowtext .5pt;border-right:solid windowtext .5pt;mso-border-top-alt:");
		cadreHtml
				.append("  solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;");
		cadreHtml.append("  height:31.2pt'>");
		cadreHtml
				.append("						<p class=MsoNormal align=center style='text-align:center'>");
		cadreHtml
				.append("							<a name=\"A0117_4\"></a><span style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>籍</span><span lang=EN-US style='font-size:12.0pt'><span style=\"mso-spacerun: yes\">&nbsp; </span></span><span");
		cadreHtml
				.append("								style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>贯</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=86");
		cadreHtml
				.append("						style='width:64.3pt;border-top:none;border-left:none;border-bottom:");
		cadreHtml
				.append("  solid windowtext .5pt;border-right:solid windowtext .5pt;mso-border-top-alt:");
		cadreHtml
				.append("  solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;");
		cadreHtml.append("  height:31.2pt'>");
	
	//籍贯
		String address = this.getAddress();
		if(address.length() > 8){
			address = address.substring(0,8);
		}	
		cadreHtml.append(address);
		
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=79");
		cadreHtml
				.append("						style='width:59.55pt;border-top:none;border-left:none;");
		cadreHtml
				.append("  border-bottom:solid windowtext .5pt;border-right:solid windowtext .5pt;");
		cadreHtml
				.append("  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;");
		cadreHtml.append("  padding:0cm 5.4pt 0cm 5.4pt;height:31.2pt'>");
		cadreHtml
				.append("						<p class=MsoNormal align=center style='text-align:center'>");
		cadreHtml
				.append("							<a name=\"A0111_5\"></a><span style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>出生地</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=77");
		cadreHtml
				.append("						style='width:57.45pt;border-top:none;border-left:none;");
		cadreHtml
				.append("  border-bottom:solid windowtext .5pt;border-right:solid windowtext .5pt;");
		cadreHtml
				.append("  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;");
		cadreHtml.append("  padding:0cm 5.4pt 0cm 5.4pt;height:31.2pt'>");
	
	//出生地
		String birthAddress = this.getBirthAddress();
		if(birthAddress.length() > 6){
			birthAddress = birthAddress.substring(0,6);
		}	
		cadreHtml.append(birthAddress);
		
		
		cadreHtml.append("					</td>");
		cadreHtml.append("					<a name=\"A0114_6\"></a>");
		cadreHtml.append("				</tr>");
		cadreHtml.append("				<tr style='height:31.2pt'>");
		cadreHtml
				.append("					<td width=67 colspan=2 style='width:50.4pt;border:solid windowtext .5pt;");
		cadreHtml
				.append("  border-top:none;mso-border-top-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;");
		cadreHtml.append("  height:31.2pt'>");
		cadreHtml
				.append("						<p class=MsoNormal align=center style='text-align:center'>");
		cadreHtml
				.append("							<span style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>入</span><span lang=EN-US style='font-size:12.0pt'><span style=\"mso-spacerun: yes\">&nbsp; </span></span><span");
		cadreHtml
				.append("								style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>党</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml
				.append("						<p class=MsoNormal align=center style='text-align:center'>");
		cadreHtml
				.append("							<span style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>时</span><span lang=EN-US style='font-size:12.0pt'><span style=\"mso-spacerun: yes\">&nbsp; </span></span><span");
		cadreHtml
				.append("								style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>间</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=77 colspan=3");
		cadreHtml
				.append("						style='width:57.6pt;border-top:none;border-left:none;");
		cadreHtml
				.append("  border-bottom:solid windowtext .5pt;border-right:solid windowtext .5pt;");
		cadreHtml
				.append("  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;");
		cadreHtml.append("  padding:0cm 5.4pt 0cm 5.4pt;height:31.2pt'>");
	
	//入党时间
		cadreHtml.append(this.getEnterPartyDate());
		
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=70");
		cadreHtml
				.append("						style='width:52.7pt;border-top:none;border-left:none;border-bottom:");
		cadreHtml
				.append("  solid windowtext .5pt;border-right:solid windowtext .5pt;mso-border-top-alt:");
		cadreHtml
				.append("  solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;");
		cadreHtml.append("  height:31.2pt'>");
		cadreHtml
				.append("						<p class=MsoNormal align=center style='text-align:center'>");
		cadreHtml
				.append("							<a name=\"A0144_7\"></a><span style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>参加工作时间</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=86");
		cadreHtml
				.append("						style='width:64.3pt;border-top:none;border-left:none;border-bottom:");
		cadreHtml
				.append("  solid windowtext .5pt;border-right:solid windowtext .5pt;mso-border-top-alt:");
		cadreHtml
				.append("  solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;");
		cadreHtml.append("  height:31.2pt'>");
	
	//参加工作时间
		cadreHtml.append(this.getEnterWorkDate());
		
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=79");
		cadreHtml
				.append("						style='width:59.55pt;border-top:none;border-left:none;");
		cadreHtml
				.append("  border-bottom:solid windowtext .5pt;border-right:solid windowtext .5pt;");
		cadreHtml
				.append("  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;");
		cadreHtml.append("  padding:0cm 5.4pt 0cm 5.4pt;height:31.2pt'>");
		cadreHtml
				.append("						<p class=MsoNormal align=center style='text-align:center'>");
		cadreHtml
				.append("							<a name=\"A0134_8\"></a><span style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>健康状况</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=77");
		cadreHtml
				.append("						style='width:57.45pt;border-top:none;border-left:none;");
		cadreHtml
				.append("  border-bottom:solid windowtext .5pt;border-right:solid windowtext .5pt;");
		cadreHtml
				.append("  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;");
		cadreHtml.append("  padding:0cm 5.4pt 0cm 5.4pt;height:31.2pt'>");
		
	//健康状况
		String bodyStatus = this.getBodyStatus();
		if(bodyStatus.length()>6){
			bodyStatus = bodyStatus.substring(0,6);
		}
		cadreHtml.append(bodyStatus);
		
		cadreHtml.append("					</td>");
		cadreHtml.append("					<a name=\"A0127_9\"></a>");
		cadreHtml.append("				</tr>");
		cadreHtml.append("				<tr style='height:31.2pt'>");
		cadreHtml
				.append("					<td width=67 colspan=2 style='width:50.4pt;border:solid windowtext .5pt;");
		cadreHtml
				.append("  border-top:none;mso-border-top-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;");
		cadreHtml.append("  height:31.2pt'>");
		cadreHtml
				.append("						<p class=MsoNormal align=center style='text-align:center'>");
		cadreHtml
				.append("							<span style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>专业技</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml
				.append("						<p class=MsoNormal align=center style='text-align:center'>");
		cadreHtml
				.append("							<span style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>术职务</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=147 colspan=4");
		cadreHtml
				.append("						style='width:110.3pt;border-top:none;border-left:");
		cadreHtml
				.append("  none;border-bottom:solid windowtext .5pt;border-right:solid windowtext .5pt;");
		cadreHtml
				.append("  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;");
		cadreHtml.append("  padding:0cm 5.4pt 0cm 5.4pt;height:31.2pt'>");
		
	//专业技术职务
		String duty = this.getDuty();
		if(duty == null || "".equals(duty)){
			duty="&nbsp;&nbsp;&nbsp;&nbsp;无";
		}
		cadreHtml.append(duty);
		
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=86");
		cadreHtml
				.append("						style='width:64.3pt;border-top:none;border-left:none;border-bottom:");
		cadreHtml
				.append("  solid windowtext .5pt;border-right:solid windowtext .5pt;mso-border-top-alt:");
		cadreHtml
				.append("  solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;");
		cadreHtml.append("  height:31.2pt'>");
		cadreHtml
				.append("						<p class=MsoNormal align=center style='text-align:center'>");
		cadreHtml
				.append("							<a name=\"A0125_10\"></a><span style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>熟悉专业</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml
				.append("						<p class=MsoNormal align=center style='text-align:center'>");
		cadreHtml
				.append("							<span style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>有何专长</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=156 colspan=2");
		cadreHtml
				.append("						style='width:117.0pt;border-top:none;border-left:");
		cadreHtml
				.append("  none;border-bottom:solid windowtext .5pt;border-right:solid windowtext .5pt;");
		cadreHtml
				.append("  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;");
		cadreHtml.append("  padding:0cm 5.4pt 0cm 5.4pt;height:31.2pt'>");
	
	//熟悉专业和特长
		String tc = this.getTeChang();
		if(tc == null || "".equals(tc)){
			tc="&nbsp;&nbsp;&nbsp;&nbsp;无";
		}else{
			tc = tc.substring(0,16);
		}
		cadreHtml.append(tc);
		
		cadreHtml.append("					</td>");
		cadreHtml.append("					<a name=\"A0187A_11\"></a>");
		cadreHtml.append("				</tr>");
		cadreHtml.append("				<tr style='height:34.0pt'>");
		cadreHtml
				.append("					<td width=67 colspan=2 rowspan=2 style='width:50.4pt;border:solid windowtext .5pt;");
		cadreHtml
				.append("  border-top:none;mso-border-top-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;");
		cadreHtml.append("  height:34.0pt'>");
		cadreHtml
				.append("						<p class=MsoNormal align=center style='text-align:center'>");
		cadreHtml
				.append("							<span style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>学</span><span lang=EN-US style='font-size:12.0pt'><span style=\"mso-spacerun: yes\">&nbsp; </span></span><span");
		cadreHtml
				.append("								style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>历</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml
				.append("						<p class=MsoNormal align=center style='text-align:center'>");
		cadreHtml
				.append("							<span style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>学</span><span lang=EN-US style='font-size:12.0pt'><span style=\"mso-spacerun: yes\">&nbsp; </span></span><span");
		cadreHtml
				.append("								style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>位</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=72 colspan=2");
		cadreHtml
				.append("						style='width:53.8pt;border-top:none;border-left:none;");
		cadreHtml
				.append("  border-bottom:solid windowtext .5pt;border-right:solid windowtext .5pt;");
		cadreHtml
				.append("  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;");
		cadreHtml.append("  padding:0cm 5.4pt 0cm 5.4pt;height:34.0pt'>");
		cadreHtml
				.append("						<p class=MsoNormal align=center style='text-align:center'>");
		cadreHtml
				.append("							<span style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>全日制</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml
				.append("						<p class=MsoNormal align=center style='text-align:center'>");
		cadreHtml
				.append("							<span style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>教</span><span lang=EN-US style='font-size:12.0pt'><span style=\"mso-spacerun: yes\">&nbsp; </span></span><span");
		cadreHtml
				.append("								style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>育</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=161 colspan=3");
		cadreHtml
				.append("						style='width:120.8pt;border-top:none;border-left:");
		cadreHtml
				.append("  none;border-bottom:solid windowtext .5pt;border-right:solid windowtext .5pt;");
		cadreHtml
				.append("  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;");
		cadreHtml.append("  padding:0cm 5.4pt 0cm 5.4pt;height:34.0pt'>");

	//全日制教育
		String qsa = this.getQrzSchoolAge();
		String qd = this.getQrzDegree();
		
	
		
		if(qsa == null){
			qsa ="";
		}
		if(qd == null){
			qd="";
		}
		
		if( !"".equals(qsa) && !"".equals(qd)){
			cadreHtml.append(qsa +"<br>" + qd);
		}else{
			cadreHtml.append(qsa + qd);
		}
		
		
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=79");
		cadreHtml
				.append("						style='width:59.55pt;border-top:none;border-left:none;");
		cadreHtml
				.append("  border-bottom:solid windowtext .5pt;border-right:solid windowtext .5pt;");
		cadreHtml
				.append("  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;");
		cadreHtml.append("  padding:0cm 5.4pt 0cm 5.4pt;height:34.0pt'>");
		cadreHtml
				.append("						<p class=MsoNormal align=center style='text-align:center'>");
		cadreHtml
				.append("							<a name=\"A0128_13\"></a><span style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>毕业院校</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml
				.append("						<p class=MsoNormal align=center style='text-align:center'>");
		cadreHtml
				.append("							<span style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>系及专业</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=192 colspan=2");
		cadreHtml
				.append("						style='width:144.0pt;border-top:none;border-left:");
		cadreHtml
				.append("  none;border-bottom:solid windowtext .5pt;border-right:solid windowtext .5pt;");
		cadreHtml
				.append("  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;");
		cadreHtml.append("  padding:0cm 5.4pt 0cm 5.4pt;height:34.0pt'>");
		
	//毕业院校及专业
		String qs = this.getQrzSchool();
		String qsy = this.getQrzSpecialty();
		String he = qs + qsy;
		if(qs == null){
			qs="";
		}
		if(qsy == null){
			qsy="";
		}
		if(he.length()>22){
			he = he.substring(0,22);
		}	
		cadreHtml.append(he);
		
		cadreHtml.append(" <a name=\"A0130_15\"></a>");
		cadreHtml.append("				</tr>");
		cadreHtml.append("				<tr style='height:34.0pt'>");
		cadreHtml.append("					<td width=72 colspan=2");
		cadreHtml
				.append("						style='width:53.8pt;border-top:none;border-left:none;");
		cadreHtml
				.append("  border-bottom:solid windowtext .5pt;border-right:solid windowtext .5pt;");
		cadreHtml
				.append("  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;");
		cadreHtml.append("  padding:0cm 5.4pt 0cm 5.4pt;height:34.0pt'>");
		cadreHtml
				.append("						<p class=MsoNormal align=center style='text-align:center'>");
		cadreHtml
				.append("							<span style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>在</span><span lang=EN-US style='font-size:12.0pt'><span style=\"mso-spacerun: yes\">&nbsp; </span></span><span");
		cadreHtml
				.append("								style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>职</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml
				.append("						<p class=MsoNormal align=center style='text-align:center'>");
		cadreHtml
				.append("							<span style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>教</span><span lang=EN-US style='font-size:12.0pt'><span style=\"mso-spacerun: yes\">&nbsp; </span></span><span");
		cadreHtml
				.append("								style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>育</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=161 colspan=3");
		cadreHtml
				.append("						style='width:120.8pt;border-top:none;border-left:");
		cadreHtml
				.append("  none;border-bottom:solid windowtext .5pt;border-right:solid windowtext .5pt;");
		cadreHtml
				.append("  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;");
		cadreHtml.append("  padding:0cm 5.4pt 0cm 5.4pt;height:34.0pt'>");

	//在职教育
		String za = this.getZSchoolAge();
		String zd = this.getZDegree();
		if(za == null){
			za="";
		}
		if(zd == null){
			zd="";
		}
		if(!"".equals(za) && !"".equals(zd)){
			cadreHtml.append(za + "<br>" +zd);
		}else{
			cadreHtml.append(za +zd);
		}
		
		
		
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=79");
		cadreHtml
				.append("						style='width:59.55pt;border-top:none;border-left:none;");
		cadreHtml
				.append("  border-bottom:solid windowtext .5pt;border-right:solid windowtext .5pt;");
		cadreHtml
				.append("  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;");
		cadreHtml.append("  padding:0cm 5.4pt 0cm 5.4pt;height:34.0pt'>");
		cadreHtml
				.append("						<p class=MsoNormal align=center style='text-align:center'>");
		cadreHtml
				.append("							<a name=\"A0128_14\"></a><span style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>毕业院校</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml
				.append("						<p class=MsoNormal align=center style='text-align:center'>");
		cadreHtml
				.append("							<span style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>系及专业</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=192 colspan=2");
		cadreHtml
				.append("						style='width:144.0pt;border-top:none;border-left:");
		cadreHtml
				.append("  none;border-bottom:solid windowtext .5pt;border-right:solid windowtext .5pt;");
		cadreHtml
				.append("  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;");
		cadreHtml.append("  padding:0cm 5.4pt 0cm 5.4pt;height:34.0pt'>");
	
	//毕业院校及专业
		cadreHtml.append(this.getZSchool()+this.getZSpecialty());
		
		cadreHtml.append("					</td>");
		cadreHtml.append("					<a name=\"A0130_16\"></a>");
		cadreHtml.append("				</tr>");
		cadreHtml.append("				<tr style='height:29.75pt'>");
		cadreHtml
				.append("					<td width=110 colspan=3 style='width:82.2pt;border:solid windowtext .5pt;");
		cadreHtml
				.append("  border-top:none;mso-border-top-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;");
		cadreHtml.append("  height:29.75pt'>");
		cadreHtml
				.append("						<p class=MsoNormal align=center style='text-align:center'>");
		cadreHtml
				.append("							<span style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>现</span><span style='font-size:12.0pt'> </span><span");
		cadreHtml
				.append("								style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:");
		cadreHtml
				.append("  \"Times New Roman\";mso-hansi-font-family:\"Times New Roman\"'>任</span><span style='font-size:12.0pt'> </span><span");
		cadreHtml.append("								style='font-size:12.0pt;font-family:");
		cadreHtml
				.append("  宋体;mso-ascii-font-family:\"Times New Roman\";mso-hansi-font-family:\"Times New Roman\"'>职</span><span style='font-size:12.0pt'> </span><span");
		cadreHtml.append("								style='font-size:12.0pt;font-family:");
		cadreHtml
				.append("  宋体;mso-ascii-font-family:\"Times New Roman\";mso-hansi-font-family:\"Times New Roman\"'>务</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=462 colspan=7");
		cadreHtml
				.append("						style='width:346.35pt;border-top:none;border-left:");
		cadreHtml
				.append("  none;border-bottom:solid windowtext .5pt;border-right:solid windowtext .5pt;");
		cadreHtml
				.append("  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;");
		cadreHtml.append("  padding:0cm 5.4pt 0cm 5.4pt;height:29.75pt'>");
		
	//现任职务

		cadreHtml.append(this.getCurrentOrg()+"&nbsp;" +this.getCurrentDuty());
		
		cadreHtml.append("					</td>");
		cadreHtml.append("					<a name=\"A0215_17\"></a>");
		cadreHtml.append("				</tr>");
		cadreHtml.append("				<tr style='height:29.75pt'>");
		cadreHtml
				.append("					<td width=110 colspan=3 style='width:82.2pt;border:solid windowtext .5pt;");
		cadreHtml
				.append("  border-top:none;mso-border-top-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;");
		cadreHtml.append("  height:29.75pt'>");
		cadreHtml
				.append("						<p class=MsoNormal align=center style='text-align:center'>");
		cadreHtml
				.append("							<span style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>拟</span><span style='font-size:12.0pt'> </span><span");
		cadreHtml
				.append("								style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:");
		cadreHtml
				.append("  \"Times New Roman\";mso-hansi-font-family:\"Times New Roman\"'>任</span><span style='font-size:12.0pt'> </span><span");
		cadreHtml.append("								style='font-size:12.0pt;font-family:");
		cadreHtml
				.append("  宋体;mso-ascii-font-family:\"Times New Roman\";mso-hansi-font-family:\"Times New Roman\"'>职</span><span style='font-size:12.0pt'> </span><span");
		cadreHtml.append("								style='font-size:12.0pt;font-family:");
		cadreHtml
				.append("  宋体;mso-ascii-font-family:\"Times New Roman\";mso-hansi-font-family:\"Times New Roman\"'>务</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=462 colspan=7");
		cadreHtml
				.append("						style='width:346.35pt;border-top:none;border-left:");
		cadreHtml
				.append("  none;border-bottom:solid windowtext .5pt;border-right:solid windowtext .5pt;");
		cadreHtml
				.append("  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;");
		cadreHtml.append("  padding:0cm 5.4pt 0cm 5.4pt;height:29.75pt'>");
		
	//拟任职务
		cadreHtml.append("");
		
		cadreHtml.append("					</td>");
		cadreHtml.append("					<a name=\"RMZW_18\"></a>");
		cadreHtml.append("				</tr>");
		cadreHtml.append("				<tr style='height:29.75pt'>");
		cadreHtml
				.append("					<td width=110 colspan=3 style='width:82.2pt;border:solid windowtext .5pt;");
		cadreHtml
				.append("  border-top:none;mso-border-top-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;");
		cadreHtml.append("  height:29.75pt'>");
		cadreHtml
				.append("						<p class=MsoNormal align=center style='text-align:center'>");
		cadreHtml
				.append("							<span style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>拟</span><span style='font-size:12.0pt'> </span><span");
		cadreHtml
				.append("								style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:");
		cadreHtml
				.append("  \"Times New Roman\";mso-hansi-font-family:\"Times New Roman\"'>免</span><span style='font-size:12.0pt'> </span><span");
		cadreHtml.append("								style='font-size:12.0pt;font-family:");
		cadreHtml
				.append("  宋体;mso-ascii-font-family:\"Times New Roman\";mso-hansi-font-family:\"Times New Roman\"'>职</span><span style='font-size:12.0pt'> </span><span");
		cadreHtml.append("								style='font-size:12.0pt;font-family:");
		cadreHtml
				.append("  宋体;mso-ascii-font-family:\"Times New Roman\";mso-hansi-font-family:\"Times New Roman\"'>务</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=462 colspan=7");
		cadreHtml
				.append("						style='width:346.35pt;border-top:none;border-left:");
		cadreHtml
				.append("  none;border-bottom:solid windowtext .5pt;border-right:solid windowtext .5pt;");
		cadreHtml
				.append("  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;");
		cadreHtml.append("  padding:0cm 5.4pt 0cm 5.4pt;height:29.75pt'>");
		
	//拟免职务
		cadreHtml.append("");
		
		cadreHtml.append("					</td>");
		cadreHtml.append("					<a name=\"RMZW_19\"></a>");
		cadreHtml.append("				</tr>");
		cadreHtml.append("				<tr style='height:323.35pt'>");
		cadreHtml
				.append("					<td width=48 style='width:36.0pt;border:solid windowtext .5pt;border-top:");
		cadreHtml
				.append("  none;mso-border-top-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;");
		cadreHtml.append("  height:323.35pt'>");
		cadreHtml
				.append("						<p class=MsoNormal align=center style='margin-top:0cm;margin-right:5.65pt;");
		cadreHtml
				.append("  margin-bottom:0cm;margin-left:5.65pt;margin-bottom:.0001pt;text-align:center'>");
		cadreHtml
				.append("							<span style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>简</span><span lang=EN-US style='font-size:12.0pt'><span style=\"mso-spacerun:");
		cadreHtml
				.append("  yes\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
		cadreHtml
				.append("	</span><span style=\"mso-spacerun: yes\">&nbsp;</span></span><span style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>历</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=523 colspan=9 valign=top");
		cadreHtml.append("						style='width:392.55pt;border-top:none;");
		cadreHtml
				.append("  border-left:none;border-bottom:solid windowtext .5pt;border-right:solid windowtext .5pt;");
		cadreHtml
				.append("  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;");
		cadreHtml.append("  padding:0cm 5.4pt 0cm 5.4pt;height:323.35pt'>");
		
		//System.out.println("简历数据");
	//简历
		StringBuffer temp = new StringBuffer();
		for(int i=0; i< this.getResumeStartDate().size(); i++){
			temp.append((String)this.getResumeStartDate().get(i));//简历开始时间
			temp.append(" - ");
			String temp1 = (String)this.getResumeEndDate().get(i);
			if("".equals(temp1)){
				temp1="至今";
			}
			temp.append(temp1);//简历结束时间
			temp.append("  " + (String)this.getResumeOrg().get(i));//简历对应单位
			temp.append("  " + (String)this.getResumeDuty().get(i));//简历对应职务
			temp.append("<br>");
		}
		cadreHtml.append(temp.toString());
	//	System.out.println("................");
		
		
		cadreHtml.append("");
		cadreHtml.append("					</td>");
		cadreHtml.append("					<a name=\"A1701_20\"></a>");
		cadreHtml.append("				</tr>");
		cadreHtml.append("				<![if !supportMisalignedColumns]>");
		cadreHtml.append("				<tr height=0>");
		cadreHtml.append("					<td width=48 style='border:none'></td>");
		cadreHtml.append("					<td width=19 style='border:none'></td>");
		cadreHtml.append("					<td width=42 style='border:none'></td>");
		cadreHtml.append("					<td width=29 style='border:none'></td>");
		cadreHtml.append("					<td width=5 style='border:none'></td>");
		cadreHtml.append("					<td width=70 style='border:none'></td>");
		cadreHtml.append("					<td width=86 style='border:none'></td>");
		cadreHtml.append("					<td width=79 style='border:none'></td>");
		cadreHtml.append("					<td width=77 style='border:none'></td>");
		cadreHtml.append("					<td width=115 style='border:none'></td>");
		cadreHtml.append("				</tr>");
		cadreHtml.append("				<![endif]>");
		cadreHtml.append("			</table>");
		cadreHtml.append("");
		cadreHtml
				.append("			<span lang=EN-US style='font-size:10.5pt;mso-bidi-font-size:12.0pt;font-family:");
		cadreHtml
				.append("\"Times New Roman\";mso-fareast-font-family:宋体;mso-font-kerning:1.0pt;mso-ansi-language:");
		cadreHtml
				.append("EN-US;mso-fareast-language:ZH-CN;mso-bidi-language:AR-SA'><br clear=all");
		cadreHtml
				.append("					style='mso-special-character:line-break;page-break-before:always'> </span>");
		cadreHtml.append("");
		cadreHtml.append("			<p class=MsoNormal>");
		cadreHtml.append("				<![if !supportEmptyParas]>");
		cadreHtml.append("				&nbsp;");
		cadreHtml.append("				<![endif]>");
		cadreHtml.append("				<span lang=EN-US><o:p></o:p></span>");
		cadreHtml.append("			</p>");
		cadreHtml.append("");
		cadreHtml
				.append("			<table border=1 cellspacing=0 cellpadding=0 width=570 style='width:427.7pt;");
		cadreHtml
				.append(" margin-left:18.7pt;border-collapse:collapse;border:none;mso-border-alt:solid windowtext .5pt;");
		cadreHtml.append(" mso-padding-alt:0cm 5.4pt 0cm 5.4pt'>");
		cadreHtml.append("				<tr style='height:39.2pt'>");
		cadreHtml
				.append("					<td width=66 style='width:49.7pt;border:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;");
		cadreHtml.append("  height:39.2pt'>");
		cadreHtml
				.append("						<p class=MsoNormal style='mso-line-height-alt:0pt'>");
		cadreHtml.append("							<span style='font-size:");
		cadreHtml
				.append("  12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";mso-hansi-font-family:");
		cadreHtml
				.append("  \"Times New Roman\"'>奖</span><span style='font-size:12.0pt'> </span><span");
		cadreHtml
				.append("								style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>情</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml
				.append("						<p class=MsoNormal style='mso-line-height-alt:0pt'>");
		cadreHtml.append("							<span style='font-size:");
		cadreHtml
				.append("  12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";mso-hansi-font-family:");
		cadreHtml
				.append("  \"Times New Roman\"'>惩</span><span style='font-size:12.0pt'> </span><span");
		cadreHtml
				.append("								style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>况</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml.append("					</td>");
		cadreHtml
				.append("					<td width=504 colspan=8 style='width:378.0pt;border:solid windowtext .5pt;");
		cadreHtml
				.append("  border-left:none;mso-border-left-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;");
		cadreHtml.append("  height:39.2pt'>");
		
	//奖惩情况
		StringBuffer temp2 = new StringBuffer();
		for(int i=0; i< this.getEncouragementName().size(); i++){

			if(this.getEncouragementDate().size()!=0){
				temp2.append(" " + (String)this.getEncouragementDate().get(i));//奖励日期
			}else{
				temp2.append(" ");//奖励日期
			}
			
			if(this.getEncouragementCausation().size()!=0){
				temp2.append(" "+(String)this.getEncouragementCausation().get(i));//奖励原因
			}else{
				temp2.append(" ");//奖励原因
			}

			if(this.getEncouragementOrg().size()!=0){
				temp2.append(" " + (String)this.getEncouragementOrg().get(i));//奖励单位
			}else{
				temp2.append(" ");//奖励单位
			}
			
			temp2.append((String)this.getEncouragementName().get(i));//奖励名称
			
			temp2.append("<br>");
		}
		
		for(int i=0; i< this.getPunishName().size(); i++){
			
			if(this.getPunishDate().size() !=0){
				temp2.append(" " + (String)this.getPunishDate().get(i));//惩罚日期
			}else{
				temp2.append(" " );//惩罚日期
			}
			
			if(this.getPunishCausation().size()!=0){
				temp2.append(" "+(String)this.getPunishCausation().get(i));//惩罚原因
			}else{
				temp2.append(" ");//惩罚原因
			}
			
			if(this.getPunishOrg().size()!=0){
				temp2.append(" " + (String)this.getPunishOrg().get(i));//惩罚单位
			}else{
				temp2.append(" ");//惩罚单位
			}
			
			temp2.append((String)this.getPunishName().get(i));//惩罚名称			
			temp2.append("<br>");
		}
		if(this.getEncouragementName().size()==0 && this.getPunishName().size()==0){
			temp2.append("无");
		}
		cadreHtml.append(temp2.toString());
	
		
		
		cadreHtml.append("					</td>");
		cadreHtml.append("					<a name=\"A1401_21\"></a>");
		cadreHtml.append("				</tr>");
		cadreHtml.append("				<tr style='height:51.05pt'>");
		cadreHtml
				.append("					<td width=66 style='width:49.7pt;border:solid windowtext .5pt;border-top:");
		cadreHtml
				.append("  none;mso-border-top-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;");
		cadreHtml.append("  height:51.05pt'>");
		cadreHtml
				.append("						<p class=MsoNormal style='mso-line-height-alt:0pt'>");
		cadreHtml.append("							<span style='font-size:");
		cadreHtml
				.append("  12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";mso-hansi-font-family:");
		cadreHtml
				.append("  \"Times New Roman\"'>年</span><span style='font-size:12.0pt'> </span><span");
		cadreHtml
				.append("								style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>核</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml
				.append("						<p class=MsoNormal style='mso-line-height-alt:0pt'>");
		cadreHtml.append("							<span style='font-size:");
		cadreHtml
				.append("  12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";mso-hansi-font-family:");
		cadreHtml
				.append("  \"Times New Roman\"'>度</span><span style='font-size:12.0pt'> </span><span");
		cadreHtml
				.append("								style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>结</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml
				.append("						<p class=MsoNormal style='mso-line-height-alt:0pt'>");
		cadreHtml.append("							<span style='font-size:");
		cadreHtml
				.append("  12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";mso-hansi-font-family:");
		cadreHtml
				.append("  \"Times New Roman\"'>考</span><span style='font-size:12.0pt'> </span><span");
		cadreHtml
				.append("								style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>果</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=504 colspan=8");
		cadreHtml
				.append("						style='width:378.0pt;border-top:none;border-left:");
		cadreHtml
				.append("  none;border-bottom:solid windowtext .5pt;border-right:solid windowtext .5pt;");
		cadreHtml
				.append("  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;");
		cadreHtml.append("  padding:0cm 5.4pt 0cm 5.4pt;height:51.05pt'>");
	
	//年度考核结果
		int en = this.getExamDate().size();
		String te="";
		for(int i=0; i< en; i++){
			te+=(String)this.getExamDate().get(i)+"年年度考核"+(String)this.getExamResult().get(i)+", ";
		}
		cadreHtml.append(te);
		
		cadreHtml.append("					</td>");
		cadreHtml.append("					<a name=\"A1501_22\"></a>");
		cadreHtml.append("				</tr>");
		cadreHtml.append("				<tr style='height:39.7pt'>");
		cadreHtml
				.append("					<td width=66 style='width:49.7pt;border:solid windowtext .5pt;border-top:");
		cadreHtml
				.append("  none;mso-border-top-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;");
		cadreHtml.append("  height:39.7pt'>");
		cadreHtml
				.append("						<p class=MsoNormal style='mso-line-height-alt:0pt'>");
		cadreHtml.append("							<span style='font-size:");
		cadreHtml
				.append("  12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";mso-hansi-font-family:");
		cadreHtml
				.append("  \"Times New Roman\"'>培</span><span style='font-size:12.0pt'> </span><span");
		cadreHtml
				.append("								style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>情</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml
				.append("						<p class=MsoNormal style='mso-line-height-alt:0pt'>");
		cadreHtml.append("							<span style='font-size:");
		cadreHtml
				.append("  12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";mso-hansi-font-family:");
		cadreHtml
				.append("  \"Times New Roman\"'>训</span><span style='font-size:12.0pt'> </span><span");
		cadreHtml
				.append("								style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>况</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=504 colspan=8");
		cadreHtml
				.append("						style='width:378.0pt;border-top:none;border-left:");
		cadreHtml
				.append("  none;border-bottom:solid windowtext .5pt;border-right:solid windowtext .5pt;");
		cadreHtml
				.append("  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;");
		cadreHtml.append("  padding:0cm 5.4pt 0cm 5.4pt;height:39.7pt'>");
		
	//培训情况
		
		String d = this.getFosterDate();
		if(d == null || "".equals(d)){
			cadreHtml.append("");
		}else{
			cadreHtml.append("累计培训时间达"+d+"学时");
		}
		
		
		cadreHtml.append("					</td>");
		cadreHtml.append("					<a name=\"A1101_73\"></a>");
		cadreHtml.append("				</tr>");
		cadreHtml.append("				<tr style='height:39.75pt'>");
		cadreHtml
				.append("					<td width=66 style='width:49.7pt;border:solid windowtext .5pt;border-top:");
		cadreHtml
				.append("  none;mso-border-top-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;");
		cadreHtml.append("  height:39.75pt'>");
		cadreHtml
				.append("						<p class=MsoNormal style='mso-line-height-alt:0pt'>");
		cadreHtml.append("							<span style='font-size:");
		cadreHtml
				.append("  12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";mso-hansi-font-family:");
		cadreHtml
				.append("  \"Times New Roman\"'>任</span><span style='font-size:12.0pt'> </span><span");
		cadreHtml
				.append("								style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>理</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml
				.append("						<p class=MsoNormal style='mso-line-height-alt:0pt'>");
		cadreHtml.append("							<span style='font-size:");
		cadreHtml
				.append("  12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";mso-hansi-font-family:");
		cadreHtml
				.append("  \"Times New Roman\"'>免</span><span style='font-size:12.0pt'> </span><span");
		cadreHtml
				.append("								style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>由</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=504 colspan=8");
		cadreHtml
				.append("						style='width:378.0pt;border-top:none;border-left:");
		cadreHtml
				.append("  none;border-bottom:solid windowtext .5pt;border-right:solid windowtext .5pt;");
		cadreHtml
				.append("  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;");
		cadreHtml.append("  padding:0cm 5.4pt 0cm 5.4pt;height:39.75pt'>");
		
	//任免理由
		cadreHtml.append("工作需要");
		
		cadreHtml.append("					</td>");
		cadreHtml.append("				</tr>");
		cadreHtml.append("				<tr style='height:31.2pt'>");
		cadreHtml
				.append("					<td width=66 rowspan=7 style='width:49.7pt;border:solid windowtext .5pt;");
		cadreHtml
				.append("  border-top:none;mso-border-top-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;");
		cadreHtml.append("  height:31.2pt'>");
		cadreHtml
				.append("						<p class=MsoNormal style='mso-line-height-alt:0pt'>");
		cadreHtml.append("							<span style='font-size:");
		cadreHtml
				.append("  12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";mso-hansi-font-family:");
		cadreHtml
				.append("  \"Times New Roman\"'>主</span><span style='font-size:12.0pt'> </span><span");
		cadreHtml
				.append("								style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>及</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml
				.append("						<p class=MsoNormal style='mso-line-height-alt:0pt'>");
		cadreHtml.append("							<span style='font-size:");
		cadreHtml
				.append("  12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";mso-hansi-font-family:");
		cadreHtml
				.append("  \"Times New Roman\"'>要</span><span style='font-size:12.0pt'> </span><span");
		cadreHtml
				.append("								style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>社</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml
				.append("						<p class=MsoNormal style='mso-line-height-alt:0pt'>");
		cadreHtml.append("							<span style='font-size:");
		cadreHtml
				.append("  12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";mso-hansi-font-family:");
		cadreHtml
				.append("  \"Times New Roman\"'>家</span><span style='font-size:12.0pt'> </span><span");
		cadreHtml
				.append("								style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>会</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml
				.append("						<p class=MsoNormal style='mso-line-height-alt:0pt'>");
		cadreHtml.append("							<span style='font-size:");
		cadreHtml
				.append("  12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";mso-hansi-font-family:");
		cadreHtml
				.append("  \"Times New Roman\"'>庭</span><span style='font-size:12.0pt'> </span><span");
		cadreHtml
				.append("								style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>关</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml
				.append("						<p class=MsoNormal style='mso-line-height-alt:0pt'>");
		cadreHtml.append("							<span style='font-size:");
		cadreHtml
				.append("  12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";mso-hansi-font-family:");
		cadreHtml
				.append("  \"Times New Roman\"'>成</span><span style='font-size:12.0pt'> </span><span");
		cadreHtml
				.append("								style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>系</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml
				.append("						<p class=MsoNormal style='mso-line-height-alt:0pt'>");
		cadreHtml.append("							<span style='font-size:");
		cadreHtml
				.append("  12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";mso-hansi-font-family:");
		cadreHtml
				.append("  \"Times New Roman\"'>员</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml.append("					</td>");
		
		
		cadreHtml.append("					<td width=60");//43  32.2
		cadreHtml
				.append("						style='width:45.0pt;border-top:none;border-left:none;border-bottom:");
		cadreHtml
				.append("  solid windowtext .5pt;border-right:solid windowtext .5pt;mso-border-top-alt:");
		cadreHtml
				.append("  solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;");
		cadreHtml.append("  height:31.2pt'>");
		cadreHtml
				.append("						<p class=MsoNormal align=center style='text-align:center'>");
		cadreHtml
				.append("							<span style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>称</span><span style='font-size:12.0pt'> </span><span");
		cadreHtml
				.append("								style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:");
		cadreHtml
				.append("  \"Times New Roman\";mso-hansi-font-family:\"Times New Roman\"'>谓</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml.append("					</td>");
		
		
		cadreHtml.append("					<td width=72 colspan=2");//89  66.8
		cadreHtml
				.append("						style='width:54.0pt;border-top:none;border-left:none;");
		cadreHtml
				.append("  border-bottom:solid windowtext .5pt;border-right:solid windowtext .5pt;");
		cadreHtml
				.append("  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;");
		cadreHtml.append("  padding:0cm 5.4pt 0cm 5.4pt;height:31.2pt'>");
		cadreHtml
				.append("						<p class=MsoNormal align=center style='text-align:center'>");
		cadreHtml
				.append("							<span style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>姓</span><span lang=EN-US style='font-size:12.0pt'><span style=\"mso-spacerun: yes\">&nbsp; </span></span><span");
		cadreHtml
				.append("								style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>名</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=36");
		cadreHtml
				.append("						style='width:27.0pt;border-top:none;border-left:none;border-bottom:");
		cadreHtml
				.append("  solid windowtext .5pt;border-right:solid windowtext .5pt;mso-border-top-alt:");
		cadreHtml
				.append("  solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;");
		cadreHtml.append("  height:31.2pt'>");
		cadreHtml
				.append("						<p class=MsoNormal align=center style='text-align:center'>");
		cadreHtml
				.append("							<span style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>年</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml
				.append("						<p class=MsoNormal align=center style='text-align:center'>");
		cadreHtml
				.append("							<span style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>龄</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=91 colspan=2");
		cadreHtml
				.append("						style='width:68.05pt;border-top:none;border-left:none;");
		cadreHtml
				.append("  border-bottom:solid windowtext .5pt;border-right:solid windowtext .5pt;");
		cadreHtml
				.append("  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;");
		cadreHtml.append("  padding:0cm 5.4pt 0cm 5.4pt;height:31.2pt'>");
		cadreHtml
				.append("						<p class=MsoNormal align=center style='text-align:center'>");
		cadreHtml
				.append("							<span style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>政</span><span lang=EN-US style='font-size:12.0pt'><span style=\"mso-spacerun: yes\">&nbsp; </span></span><span");
		cadreHtml
				.append("								style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>治</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml
				.append("						<p class=MsoNormal align=center style='text-align:center'>");
		cadreHtml
				.append("							<span style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>面</span><span lang=EN-US style='font-size:12.0pt'><span style=\"mso-spacerun: yes\">&nbsp; </span></span><span");
		cadreHtml
				.append("								style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>貌</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=245 colspan=2");
		cadreHtml
				.append("						style='width:183.95pt;border-top:none;border-left:");
		cadreHtml
				.append("  none;border-bottom:solid windowtext .5pt;border-right:solid windowtext .5pt;");
		cadreHtml
				.append("  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;");
		cadreHtml.append("  padding:0cm 5.4pt 0cm 5.4pt;height:31.2pt'>");
		cadreHtml
				.append("						<p class=MsoNormal align=center style='text-align:center'>");
		cadreHtml
				.append("							<span style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>工</span><span style='font-size:12.0pt'> </span><span");
		cadreHtml
				.append("								style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:");
		cadreHtml
				.append("  \"Times New Roman\";mso-hansi-font-family:\"Times New Roman\"'>作</span><span style='font-size:12.0pt'> </span><span");
		cadreHtml.append("								style='font-size:12.0pt;font-family:");
		cadreHtml
				.append("  宋体;mso-ascii-font-family:\"Times New Roman\";mso-hansi-font-family:\"Times New Roman\"'>单</span><span style='font-size:12.0pt'> </span><span");
		cadreHtml.append("								style='font-size:12.0pt;font-family:");
		cadreHtml
				.append("  宋体;mso-ascii-font-family:\"Times New Roman\";mso-hansi-font-family:\"Times New Roman\"'>位</span><span style='font-size:12.0pt'> </span><span");
		cadreHtml.append("								style='font-size:12.0pt;font-family:");
		cadreHtml
				.append("  宋体;mso-ascii-font-family:\"Times New Roman\";mso-hansi-font-family:\"Times New Roman\"'>及</span><span style='font-size:12.0pt'> </span><span");
		cadreHtml.append("								style='font-size:12.0pt;font-family:");
		cadreHtml
				.append("  宋体;mso-ascii-font-family:\"Times New Roman\";mso-hansi-font-family:\"Times New Roman\"'>职</span><span style='font-size:12.0pt'> </span><span");
		cadreHtml.append("								style='font-size:12.0pt;font-family:");
		cadreHtml
				.append("  宋体;mso-ascii-font-family:\"Times New Roman\";mso-hansi-font-family:\"Times New Roman\"'>务</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml.append("					</td>");
		cadreHtml.append("				</tr>");
		cadreHtml.append("");
		cadreHtml.append("				<tr style='height:1.0cm'>");
		cadreHtml.append("					<td width=43");
		cadreHtml
				.append("						style='width:32.2pt;border-top:none;border-left:none;border-bottom:");
		cadreHtml
				.append("  solid windowtext .5pt;border-right:solid windowtext .5pt;mso-border-top-alt:");
		cadreHtml
				.append("  solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;");
		cadreHtml.append("  height:1.0cm'>");

/*
		public ArrayList familyName;	 //姓名
		public ArrayList familyRelation; //关系
		public ArrayList familyBirthDate;//出生日期
		public ArrayList familyOrg;      //单位
		public ArrayList familyZzmm;     //政治面貌
		
		//社会关系情况
		public ArrayList communityName;	 //姓名
		public ArrayList communityRelation; //关系
		public ArrayList communityBirthDate;//出生日期
		public ArrayList communityOrg;      //单位
		public ArrayList communityZzmm;     //政治面貌
		*/
		
		
		int familyFlag = this.getFamilyName().size();  //家庭成员个数
		int communityFlag = this.getCommunityName().size();//社会成员个数
		
		//System.out.println("familyFlag=" + familyFlag + "    communityFlag="  + communityFlag);
		
		int ff = familyFlag;
		int cf = 0;
		if(familyFlag >6 ){
			ff = 6;
		}else{
			cf = 6-ff;
		}
		
		if(communityFlag == 0){
			cf=0;
		}else if(communityFlag <cf){
			cf=communityFlag;
		}
		
		//System.out.println("ff=" + ff + "  cf=" + cf);
		
		int k = 0;
		int g = 0;
		boolean fb = false;
		boolean cb = false;
		boolean bb = false;
		
		if(ff > 0){	
			fb = true;
			cb = false;
			bb = false;
			ff--;
			k++;
		}else{
			if(cf >0){
				fb = false;
				cb = true;
				bb = false;
				cf--;
				g++;
			}else{
				fb = false;
				cb = false;
				bb = true;
			}
		}

	//称谓1
		if(fb){		
			cadreHtml.append(this.getFamilyRelation().get(k-1));	
		}else if(cb){
			cadreHtml.append(this.getCommunityRelation().get(g-1));
		}else if(bb){
			cadreHtml.append("");
		}


		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=89 colspan=2");
		cadreHtml
				.append("						style='width:66.8pt;border-top:none;border-left:none;");
		cadreHtml
				.append("  border-bottom:solid windowtext .5pt;border-right:solid windowtext .5pt;");
		cadreHtml
				.append("  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;");
		cadreHtml.append("  padding:0cm 5.4pt 0cm 5.4pt;height:1.0cm'>");
		cadreHtml.append("						<a name=\"A3604B_24\"></a> ");
	
	//姓名1
		if(fb){
			cadreHtml.append(this.getFamilyName().get(k-1));
		}else if(cb){
			cadreHtml.append(this.getCommunityName().get(g-1));
		}else if(bb){
			cadreHtml.append("");
		}

		
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=36");
		cadreHtml
				.append("						style='width:27.0pt;border-top:none;border-left:none;border-bottom:");
		cadreHtml
				.append("  solid windowtext .5pt;border-right:solid windowtext .5pt;mso-border-top-alt:");
		cadreHtml
				.append("  solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;");
		cadreHtml.append("  height:1.0cm'>");
		cadreHtml.append("						<a name=\"A3601_31\"></a>");
		
	//出生年月
		if(fb){
			cadreHtml.append(this.getFamilyBirthDate().get(k-1));
		}else if(cb){
			cadreHtml.append(this.getCommunityBirthDate().get(g-1));
		}else if(bb){
			cadreHtml.append("");
		}
	
		
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=91 colspan=2");
		cadreHtml
				.append("						style='width:68.05pt;border-top:none;border-left:none;");
		cadreHtml
				.append("  border-bottom:solid windowtext .5pt;border-right:solid windowtext .5pt;");
		cadreHtml
				.append("  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;");
		cadreHtml.append("  padding:0cm 5.4pt 0cm 5.4pt;height:1.0cm'>");
		cadreHtml.append("						<a name=\"A3607_38\"></a>");
		
	//政治面貌
		if(fb){
			if(this.getFamilyZzmm().size()<(k)){
				cadreHtml.append("");
			}else{
				cadreHtml.append(this.getFamilyZzmm().get(k-1));
			}
		}else if(cb){
			if(this.getCommunityZzmm().size()<(g)){
				cadreHtml.append("");
			}else{
				cadreHtml.append(this.getCommunityZzmm().get(g-1));
			}
		}else if(bb){
			cadreHtml.append("");
		}
		
		
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=245 colspan=2");
		cadreHtml
				.append("						style='width:183.95pt;border-top:none;border-left:");
		cadreHtml
				.append("  none;border-bottom:solid windowtext .5pt;border-right:solid windowtext .5pt;");
		cadreHtml
				.append("  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;");
		cadreHtml.append("  padding:0cm 5.4pt 0cm 5.4pt;height:1.0cm'>");
		cadreHtml.append("						<a name=\"A3627_45\"></a> ");
		
	//工作单位及职务
		if(fb){
			cadreHtml.append(this.getFamilyOrg().get(k-1));
		}else if(cb){
			cadreHtml.append(this.getCommunityOrg().get(g-1));
		}else if(bb){
			cadreHtml.append("");
		}
		
		cadreHtml.append("					</td>");
		cadreHtml.append("					<a name=\"A3634_67\"></a>");
		cadreHtml.append("				</tr>");
		cadreHtml.append("");
		cadreHtml.append("				<tr style='height:1.0cm'>");
		cadreHtml.append("					<td width=43");
		cadreHtml
				.append("						style='width:32.2pt;border-top:none;border-left:none;border-bottom:");
		cadreHtml
				.append("  solid windowtext .5pt;border-right:solid windowtext .5pt;mso-border-top-alt:");
		cadreHtml
				.append("  solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;");
		cadreHtml.append("  height:1.0cm'>");
	
		
		if(ff > 0){	
			fb = true;
			cb = false;
			bb = false;
			ff--;
			k++;
		}else{
			if(cf >0){
				fb = false;
				cb = true;
				bb = false;
				cf--;
				g++;
			}else{
				fb = false;
				cb = false;
				bb = true;
			}
		}
    //称谓2
		if(fb){		
			cadreHtml.append(this.getFamilyRelation().get(k-1));	
		}else if(cb){
			cadreHtml.append(this.getCommunityRelation().get(g-1));
		}else if(bb){
			cadreHtml.append("");
		}
		
		
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=89 colspan=2");
		cadreHtml
				.append("						style='width:66.8pt;border-top:none;border-left:none;");
		cadreHtml
				.append("  border-bottom:solid windowtext .5pt;border-right:solid windowtext .5pt;");
		cadreHtml
				.append("  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;");
		cadreHtml.append("  padding:0cm 5.4pt 0cm 5.4pt;height:1.0cm'>");
		cadreHtml.append("						<a name=\"A3604B_25\"></a> ");
		
	//姓名2
		if(fb){
			cadreHtml.append(this.getFamilyName().get(k-1));
		}else if(cb){
			cadreHtml.append(this.getCommunityName().get(g-1));
		}else if(bb){
			cadreHtml.append("");
		}
		
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=36");
		cadreHtml
				.append("						style='width:27.0pt;border-top:none;border-left:none;border-bottom:");
		cadreHtml
				.append("  solid windowtext .5pt;border-right:solid windowtext .5pt;mso-border-top-alt:");
		cadreHtml
				.append("  solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;");
		cadreHtml.append("  height:1.0cm'>");
		cadreHtml.append("						<a name=\"A3601_32\"></a>");
	
//		出生年月
		if(fb){
			cadreHtml.append(this.getFamilyBirthDate().get(k-1));
		}else if(cb){
			cadreHtml.append(this.getCommunityBirthDate().get(g-1));
		}else if(bb){
			cadreHtml.append("");
		}
		
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=91 colspan=2");
		cadreHtml
				.append("						style='width:68.05pt;border-top:none;border-left:none;");
		cadreHtml
				.append("  border-bottom:solid windowtext .5pt;border-right:solid windowtext .5pt;");
		cadreHtml
				.append("  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;");
		cadreHtml.append("  padding:0cm 5.4pt 0cm 5.4pt;height:1.0cm'>");
		cadreHtml.append("						<a name=\"A3607_39\"></a>");
		
//		政治面貌
		if(fb){
			if(this.getFamilyZzmm().size()<(k)){
				cadreHtml.append("");
			}else{
				cadreHtml.append(this.getFamilyZzmm().get(k-1));
			}
		}else if(cb){
			if(this.getCommunityZzmm().size()<(g)){
				cadreHtml.append("");
			}else{
				cadreHtml.append(this.getCommunityZzmm().get(g-1));
			}
		}else if(bb){
			cadreHtml.append("");
		}
		
		
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=245 colspan=2");
		cadreHtml
				.append("						style='width:183.95pt;border-top:none;border-left:");
		cadreHtml
				.append("  none;border-bottom:solid windowtext .5pt;border-right:solid windowtext .5pt;");
		cadreHtml
				.append("  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;");
		cadreHtml.append("  padding:0cm 5.4pt 0cm 5.4pt;height:1.0cm'>");
		cadreHtml
				.append("						<a name=\"A3627_46\"></a> ");
		
//		工作单位及职务
		if(fb){
			cadreHtml.append(this.getFamilyOrg().get(k-1));
		}else if(cb){
			cadreHtml.append(this.getCommunityOrg().get(g-1));
		}else if(bb){
			cadreHtml.append("");
		}
		
		
		cadreHtml.append("<a name=\"A3634_68\"></a>");
		
		cadreHtml.append("				</tr>");
		cadreHtml.append("");
		cadreHtml.append("");
		cadreHtml.append("");
		cadreHtml.append("				<tr style='height:1.0cm'>");
		cadreHtml.append("					<td width=43");
		cadreHtml
				.append("						style='width:32.2pt;border-top:none;border-left:none;border-bottom:");
		cadreHtml
				.append("  solid windowtext .5pt;border-right:solid windowtext .5pt;mso-border-top-alt:");
		cadreHtml
				.append("  solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;");
		cadreHtml.append("  height:1.0cm'>");
		
		
		
		
		if(ff > 0){	
			fb = true;
			cb = false;
			bb = false;
			ff--;
			k++;
		}else{
			if(cf >0){
				fb = false;
				cb = true;
				bb = false;
				cf--;
				g++;
			}else{
				fb = false;
				cb = false;
				bb = true;
			}
		}

    //称谓3
		if(fb){		
			cadreHtml.append(this.getFamilyRelation().get(k-1));	
		}else if(cb){
			cadreHtml.append(this.getCommunityRelation().get(g-1));
		}else if(bb){
			cadreHtml.append("");
		}
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=89 colspan=2");
		cadreHtml
				.append("						style='width:66.8pt;border-top:none;border-left:none;");
		cadreHtml
				.append("  border-bottom:solid windowtext .5pt;border-right:solid windowtext .5pt;");
		cadreHtml
				.append("  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;");
		cadreHtml.append("  padding:0cm 5.4pt 0cm 5.4pt;height:1.0cm'>");
		cadreHtml.append("						<a name=\"A3604B_26\"></a>");
		
   //姓名3
		if(fb){
			cadreHtml.append(this.getFamilyName().get(k-1));
		}else if(cb){
			cadreHtml.append(this.getCommunityName().get(g-1));
		}else if(bb){
			cadreHtml.append("");
		}
		
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=36");
		cadreHtml
				.append("						style='width:27.0pt;border-top:none;border-left:none;border-bottom:");
		cadreHtml
				.append("  solid windowtext .5pt;border-right:solid windowtext .5pt;mso-border-top-alt:");
		cadreHtml
				.append("  solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;");
		cadreHtml.append("  height:1.0cm'>");
		cadreHtml.append("						<a name=\"A3601_33\"></a> ");
		
//		出生年月
		if(fb){
			cadreHtml.append(this.getFamilyBirthDate().get(k-1));
		}else if(cb){
			cadreHtml.append(this.getCommunityBirthDate().get(g-1));
		}else if(bb){
			cadreHtml.append("");
		}
		
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=91 colspan=2");
		cadreHtml
				.append("						style='width:68.05pt;border-top:none;border-left:none;");
		cadreHtml
				.append("  border-bottom:solid windowtext .5pt;border-right:solid windowtext .5pt;");
		cadreHtml
				.append("  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;");
		cadreHtml.append("  padding:0cm 5.4pt 0cm 5.4pt;height:1.0cm'>");
		cadreHtml.append("						<a name=\"A3607_40\"></a> ");
		
//		政治面貌
		if(fb){
			if(this.getFamilyZzmm().size()<(k)){
				cadreHtml.append("");
			}else{
				cadreHtml.append(this.getFamilyZzmm().get(k-1));
			}
		}else if(cb){
			if(this.getCommunityZzmm().size()<(g)){
				cadreHtml.append("");
			}else{
				cadreHtml.append(this.getCommunityZzmm().get(g-1));
			}
		}else if(bb){
			cadreHtml.append("");
		}
		
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=245 colspan=2");
		cadreHtml
				.append("						style='width:183.95pt;border-top:none;border-left:");
		cadreHtml
				.append("  none;border-bottom:solid windowtext .5pt;border-right:solid windowtext .5pt;");
		cadreHtml
				.append("  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;");
		cadreHtml.append("  padding:0cm 5.4pt 0cm 5.4pt;height:1.0cm'>");
		cadreHtml.append("						<a name=\"A3627_47\"></a> ");
		
//		工作单位及职务
		if(fb){
			cadreHtml.append(this.getFamilyOrg().get(k-1));
		}else if(cb){
			cadreHtml.append(this.getCommunityOrg().get(g-1));
		}else if(bb){
			cadreHtml.append("");
		}
		
		
		cadreHtml.append("					</td>");
		cadreHtml.append("					<a name=\"A3634_69\"></a>");
		cadreHtml.append("				</tr>");
		cadreHtml.append("");
		cadreHtml.append("");
		cadreHtml.append("");
		cadreHtml.append("				<tr style='height:1.0cm'>");
		cadreHtml.append("					<td width=43");
		cadreHtml
				.append("						style='width:32.2pt;border-top:none;border-left:none;border-bottom:");
		cadreHtml
				.append("  solid windowtext .5pt;border-right:solid windowtext .5pt;mso-border-top-alt:");
		cadreHtml
				.append("  solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;");
		cadreHtml.append("  height:1.0cm'>");
		
		
		
		if(ff > 0){	
			fb = true;
			cb = false;
			bb = false;
			ff--;
			k++;
		}else{
			if(cf >0){
				fb = false;
				cb = true;
				bb = false;
				cf--;
				g++;
			}else{
				fb = false;
				cb = false;
				bb = true;
			}
		}
		//System.out.println("ff=" + ff + " cf = " + cf);
		//System.out.println("k =" + (k-1) + "    g=" + (g-1));
		
   //称谓4
		if(fb){		
			cadreHtml.append(this.getFamilyRelation().get(k-1));	
		}else if(cb){
			cadreHtml.append(this.getCommunityRelation().get(g-1));
		}else if(bb){
			cadreHtml.append("");
		}
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=89 colspan=2");
		cadreHtml
				.append("						style='width:66.8pt;border-top:none;border-left:none;");
		cadreHtml
				.append("  border-bottom:solid windowtext .5pt;border-right:solid windowtext .5pt;");
		cadreHtml
				.append("  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;");
		cadreHtml.append("  padding:0cm 5.4pt 0cm 5.4pt;height:1.0cm'>");
		cadreHtml.append("						<a name=\"A3604B_27\"></a> ");
		
  //姓名4
		if(fb){
			cadreHtml.append(this.getFamilyName().get(k-1));
		}else if(cb){
			cadreHtml.append(this.getCommunityName().get(g-1));
		}else if(bb){
			cadreHtml.append("");
		}
		
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=36");
		cadreHtml
				.append("						style='width:27.0pt;border-top:none;border-left:none;border-bottom:");
		cadreHtml
				.append("  solid windowtext .5pt;border-right:solid windowtext .5pt;mso-border-top-alt:");
		cadreHtml
				.append("  solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;");
		cadreHtml.append("  height:1.0cm'>");
		cadreHtml.append("						<a name=\"A3601_34\"></a> ");
		
//		出生年月
		if(fb){
			cadreHtml.append(this.getFamilyBirthDate().get(k-1));
		}else if(cb){
			cadreHtml.append(this.getCommunityBirthDate().get(g-1));
		}else if(bb){
			cadreHtml.append("");
		}
		
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=91 colspan=2");
		cadreHtml
				.append("						style='width:68.05pt;border-top:none;border-left:none;");
		cadreHtml
				.append("  border-bottom:solid windowtext .5pt;border-right:solid windowtext .5pt;");
		cadreHtml
				.append("  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;");
		cadreHtml.append("  padding:0cm 5.4pt 0cm 5.4pt;height:1.0cm'>");
		cadreHtml.append("						<a name=\"A3607_41\"></a> ");
		
//		政治面貌
		if(fb){
			if(this.getFamilyZzmm().size()<(k)){
				cadreHtml.append("");
			}else{
				cadreHtml.append(this.getFamilyZzmm().get(k-1));
			}
		}else if(cb){
			if(this.getCommunityZzmm().size()<(g)){
				cadreHtml.append("");
			}else{
				cadreHtml.append(this.getCommunityZzmm().get(g-1));
			}
		}else if(bb){
			cadreHtml.append("");
		}
		
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=245 colspan=2");
		cadreHtml
				.append("						style='width:183.95pt;border-top:none;border-left:");
		cadreHtml
				.append("  none;border-bottom:solid windowtext .5pt;border-right:solid windowtext .5pt;");
		cadreHtml
				.append("  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;");
		cadreHtml.append("  padding:0cm 5.4pt 0cm 5.4pt;height:1.0cm'>");
		cadreHtml.append("						<a name=\"A3627_48\"></a> ");
		
//		工作单位及职务
		if(fb){
			cadreHtml.append(this.getFamilyOrg().get(k-1));
		}else if(cb){
			cadreHtml.append(this.getCommunityOrg().get(g-1));
		}else if(bb){
			cadreHtml.append("");
		}
		
		
		cadreHtml.append("					</td>");
		cadreHtml.append("					<a name=\"A3634_70\"></a>");
		cadreHtml.append("				</tr>");
		cadreHtml.append("");
		cadreHtml.append("");
		cadreHtml.append("				<tr style='height:1.0cm'>");
		cadreHtml.append("					<td width=43");
		cadreHtml
				.append("						style='width:32.2pt;border-top:none;border-left:none;border-bottom:");
		cadreHtml
				.append("  solid windowtext .5pt;border-right:solid windowtext .5pt;mso-border-top-alt:");
		cadreHtml
				.append("  solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;");
		cadreHtml.append("  height:1.0cm'>");
		
		
		if(ff > 0){	
			fb = true;
			cb = false;
			bb = false;
			ff--;
			k++;
		}else{
			if(cf >0){
				fb = false;
				cb = true;
				bb = false;
				cf--;
				g++;
			}else{
				fb = false;
				cb = false;
				bb = true;
			}
		}
		//System.out.println("ff=" + ff + " cf = " + cf);
		//System.out.println("k =" + (k-1) + "    g=" + (g-1));
		
	//称谓5
		if(fb){		
			cadreHtml.append(this.getFamilyRelation().get(k-1));	
		}else if(cb){
			cadreHtml.append(this.getCommunityRelation().get(g-1));
		}else if(bb){
			cadreHtml.append("");
		}
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=89 colspan=2");
		cadreHtml
				.append("						style='width:66.8pt;border-top:none;border-left:none;");
		cadreHtml
				.append("  border-bottom:solid windowtext .5pt;border-right:solid windowtext .5pt;");
		cadreHtml
				.append("  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;");
		cadreHtml.append("  padding:0cm 5.4pt 0cm 5.4pt;height:1.0cm'>");
		cadreHtml.append("						<a name=\"A3604B_28\"></a> ");
		
//		姓名5
		if(fb){
			cadreHtml.append(this.getFamilyName().get(k-1));
		}else if(cb){
			cadreHtml.append(this.getCommunityName().get(g-1));
		}else if(bb){
			cadreHtml.append("");
		}
		
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=36");
		cadreHtml
				.append("						style='width:27.0pt;border-top:none;border-left:none;border-bottom:");
		cadreHtml
				.append("  solid windowtext .5pt;border-right:solid windowtext .5pt;mso-border-top-alt:");
		cadreHtml
				.append("  solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;");
		cadreHtml.append("  height:1.0cm'>");
		cadreHtml.append("						<a name=\"A3601_35\"></a> ");
		
//		出生年月
		if(fb){
			cadreHtml.append(this.getFamilyBirthDate().get(k-1));
		}else if(cb){
			cadreHtml.append(this.getCommunityBirthDate().get(g-1));
		}else if(bb){
			cadreHtml.append("");
		}
		
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=91 colspan=2");
		cadreHtml
				.append("						style='width:68.05pt;border-top:none;border-left:none;");
		cadreHtml
				.append("  border-bottom:solid windowtext .5pt;border-right:solid windowtext .5pt;");
		cadreHtml
				.append("  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;");
		cadreHtml.append("  padding:0cm 5.4pt 0cm 5.4pt;height:1.0cm'>");
		cadreHtml.append("						<a name=\"A3607_42\"></a>");
		
//		政治面貌
		if(fb){
			if(this.getFamilyZzmm().size()<(k)){
				cadreHtml.append("");
			}else{
				cadreHtml.append(this.getFamilyZzmm().get(k-1));
			}
		}else if(cb){
			if(this.getCommunityZzmm().size()<(g)){
				cadreHtml.append("");
			}else{
				cadreHtml.append(this.getCommunityZzmm().get(g-1));
			}
		}else if(bb){
			cadreHtml.append("");
		}
		
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=245 colspan=2");
		cadreHtml
				.append("						style='width:183.95pt;border-top:none;border-left:");
		cadreHtml
				.append("  none;border-bottom:solid windowtext .5pt;border-right:solid windowtext .5pt;");
		cadreHtml
				.append("  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;");
		cadreHtml.append("  padding:0cm 5.4pt 0cm 5.4pt;height:1.0cm'>");
		cadreHtml.append("						<a name=\"A3627_49\"></a> ");
		
//		工作单位及职务
		if(fb){
			cadreHtml.append(this.getFamilyOrg().get(k-1));
		}else if(cb){
			cadreHtml.append(this.getCommunityOrg().get(g-1));
		}else if(bb){
			cadreHtml.append("");
		}
		
		
		cadreHtml.append("					</td>");
		cadreHtml.append("					<a name=\"A3634_71\"></a>");
		cadreHtml.append("				</tr>");
		cadreHtml.append("");
		cadreHtml.append("");
		cadreHtml.append("				<tr style='height:1.0cm'>");
		cadreHtml.append("					<td width=43");
		cadreHtml
				.append("						style='width:32.2pt;border-top:none;border-left:none;border-bottom:");
		cadreHtml
				.append("  solid windowtext .5pt;border-right:solid windowtext .5pt;mso-border-top-alt:");
		cadreHtml
				.append("  solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;");
		cadreHtml.append("  height:1.0cm'>");
		
		
		if(ff > 0){	
			fb = true;
			cb = false;
			bb = false;
			ff--;
			k++;
		}else{
			if(cf >0){
				fb = false;
				cb = true;
				bb = false;
				cf--;
				g++;
			}else{
				fb = false;
				cb = false;
				bb = true;
			}
		}
		//System.out.println("ff=" + ff + " cf = " + cf);
		//System.out.println("k =" + (k-1) + "    g=" + (g-1));
		
	//称谓6
		if(fb){		
			cadreHtml.append(this.getFamilyRelation().get(k-1));	
		}else if(cb){
			cadreHtml.append(this.getCommunityRelation().get(g-1));
		}else if(bb){
			cadreHtml.append("");
		}
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=89 colspan=2");
		cadreHtml
				.append("						style='width:66.8pt;border-top:none;border-left:none;");
		cadreHtml
				.append("  border-bottom:solid windowtext .5pt;border-right:solid windowtext .5pt;");
		cadreHtml
				.append("  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;");
		cadreHtml.append("  padding:0cm 5.4pt 0cm 5.4pt;height:1.0cm'>");
		cadreHtml.append("						<a name=\"A3604B_63\"></a>");
		
//		姓名6
		if(fb){
			cadreHtml.append(this.getFamilyName().get(k-1));
		}else if(cb){
			cadreHtml.append(this.getCommunityName().get(g-1));
		}else if(bb){
			cadreHtml.append("");
		}
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=36");
		cadreHtml
				.append("						style='width:27.0pt;border-top:none;border-left:none;border-bottom:");
		cadreHtml
				.append("  solid windowtext .5pt;border-right:solid windowtext .5pt;mso-border-top-alt:");
		cadreHtml
				.append("  solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;");
		cadreHtml.append("  height:1.0cm'>");
		cadreHtml.append("						<a name=\"A3601_64\"></a> ");
		
//		出生年月
		if(fb){
			cadreHtml.append(this.getFamilyBirthDate().get(k-1));
		}else if(cb){
			cadreHtml.append(this.getCommunityBirthDate().get(g-1));
		}else if(bb){
			cadreHtml.append("");
		}
		
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=91 colspan=2");
		cadreHtml
				.append("						style='width:68.05pt;border-top:none;border-left:none;");
		cadreHtml
				.append("  border-bottom:solid windowtext .5pt;border-right:solid windowtext .5pt;");
		cadreHtml
				.append("  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;");
		cadreHtml.append("  padding:0cm 5.4pt 0cm 5.4pt;height:1.0cm'>");
		cadreHtml.append("						<a name=\"A3607_65\"></a> ");
		
//		政治面貌
		if(fb){
			if(this.getFamilyZzmm().size()<(k)){
				cadreHtml.append("");
			}else{
				cadreHtml.append(this.getFamilyZzmm().get(k-1));
			}
		}else if(cb){
			if(this.getCommunityZzmm().size()<(g)){
				cadreHtml.append("");
			}else{
				cadreHtml.append(this.getCommunityZzmm().get(g-1));
			}
		}else if(bb){
			cadreHtml.append("");
		}
		
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=245 colspan=2");
		cadreHtml
				.append("						style='width:183.95pt;border-top:none;border-left:");
		cadreHtml
				.append("  none;border-bottom:solid windowtext .5pt;border-right:solid windowtext .5pt;");
		cadreHtml
				.append("  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;");
		cadreHtml.append("  padding:0cm 5.4pt 0cm 5.4pt;height:1.0cm'>");
		cadreHtml.append("						<a name=\"A3627_66\"></a>");
		
//		工作单位及职务
		if(fb){
			cadreHtml.append(this.getFamilyOrg().get(k-1));
		}else if(cb){
			cadreHtml.append(this.getCommunityOrg().get(g-1));
		}else if(bb){
			cadreHtml.append("");
		}
		
		
		cadreHtml.append("					</td>");
		cadreHtml.append("					<a name=\"A3634_72\"></a>");
		cadreHtml.append("				</tr>");
		cadreHtml.append("");
		cadreHtml.append("");
		cadreHtml.append("");
		cadreHtml.append("");
		cadreHtml.append("");
		cadreHtml.append("				<tr style='height:33.6pt'>");
		cadreHtml
				.append("					<td width=186 colspan=3 style='width:139.7pt;border:solid windowtext .5pt;");
		cadreHtml
				.append("  border-top:none;mso-border-top-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;");
		cadreHtml.append("  height:33.6pt'>");
		cadreHtml
				.append("						<p class=MsoNormal align=center style='text-align:center'>");
		cadreHtml
				.append("							<span style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>呈报或提议单位意见</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=192 colspan=5");
		cadreHtml
				.append("						style='width:144.0pt;border-top:none;border-left:");
		cadreHtml
				.append("  none;border-bottom:solid windowtext .5pt;border-right:solid windowtext .5pt;");
		cadreHtml
				.append("  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;");
		cadreHtml.append("  padding:0cm 5.4pt 0cm 5.4pt;height:33.6pt'>");
		cadreHtml
				.append("						<p class=MsoNormal align=center style='text-align:center'>");
		cadreHtml
				.append("							<span style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\";letter-spacing:1.0pt'>试任职</span><span lang=EN-US style='font-size:12.0pt;letter-spacing:1.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml
				.append("						<p class=MsoNormal align=center style='text-align:center'>");
		cadreHtml
				.append("							<span style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\";letter-spacing:1.0pt'>审批机关意见</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=192");
		cadreHtml
				.append("						style='width:144.0pt;border-top:none;border-left:none;");
		cadreHtml
				.append("  border-bottom:solid windowtext .5pt;border-right:solid windowtext .5pt;");
		cadreHtml
				.append("  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;");
		cadreHtml.append("  padding:0cm 5.4pt 0cm 5.4pt;height:33.6pt'>");
		cadreHtml
				.append("						<p class=MsoNormal align=center style='text-align:center'>");
		cadreHtml
				.append("							<span style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\";letter-spacing:1.0pt'>试任职</span><span lang=EN-US style='font-size:12.0pt;letter-spacing:1.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml
				.append("						<p class=MsoNormal align=center style='text-align:center'>");
		cadreHtml
				.append("							<span style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\";letter-spacing:1.0pt'>行政任免机关意见</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml.append("					</td>");
		cadreHtml.append("				</tr>");
		cadreHtml.append("				<tr style='height:93.55pt'>");
		cadreHtml
				.append("					<td width=186 colspan=3 valign=top style='width:139.7pt;border:solid windowtext .5pt;");
		cadreHtml
				.append("  border-top:none;mso-border-top-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;");
		cadreHtml.append("  height:93.55pt'>");
		cadreHtml.append("						<p class=MsoNormal>");
		cadreHtml
				.append("							<span lang=EN-US style='font-size:12.0pt'><![if !supportEmptyParas]>&nbsp;<![endif]><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml.append("						<p class=MsoNormal>");
		cadreHtml
				.append("							<span lang=EN-US style='font-size:12.0pt'><![if !supportEmptyParas]>&nbsp;<![endif]><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml.append("						<p class=MsoNormal>");
		cadreHtml
				.append("							<span lang=EN-US style='font-size:12.0pt'><![if !supportEmptyParas]>&nbsp;<![endif]><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml
				.append("						<p class=MsoNormal align=right style='margin-right:1.71gd;text-align:right;");
		cadreHtml.append("  tab-stops:117.0pt'>");
		cadreHtml
				.append("							<span style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:");
		cadreHtml
				.append("  \"Times New Roman\";mso-hansi-font-family:\"Times New Roman\"'>盖</span><span lang=EN-US style='font-size:12.0pt'><span style=\"mso-spacerun: yes\">&nbsp; </span></span><span");
		cadreHtml
				.append("								style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:");
		cadreHtml
				.append("  \"Times New Roman\";mso-hansi-font-family:\"Times New Roman\"'>章</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml
				.append("						<p class=MsoNormal align=right style='margin-top:.5gd;margin-right:.85gd;");
		cadreHtml
				.append("  margin-bottom:0cm;margin-left:0cm;margin-bottom:.0001pt;text-align:right'>");
		cadreHtml
				.append("							<span style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>年</span><span lang=EN-US style='font-size:12.0pt'><span style=\"mso-spacerun: yes\">&nbsp; </span></span><span");
		cadreHtml
				.append("								style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>月</span><span lang=EN-US style='font-size:12.0pt'><span style=\"mso-spacerun: yes\">&nbsp; </span></span><span");
		cadreHtml
				.append("								style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>日</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=192 colspan=5 valign=top");
		cadreHtml.append("						style='width:144.0pt;border-top:none;");
		cadreHtml
				.append("  border-left:none;border-bottom:solid windowtext .5pt;border-right:solid windowtext .5pt;");
		cadreHtml
				.append("  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;");
		cadreHtml.append("  padding:0cm 5.4pt 0cm 5.4pt;height:93.55pt'>");
		cadreHtml.append("						<p class=MsoNormal>");
		cadreHtml
				.append("							<span lang=EN-US style='font-size:12.0pt'><![if !supportEmptyParas]>&nbsp;<![endif]><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml.append("						<p class=MsoNormal>");
		cadreHtml
				.append("							<span lang=EN-US style='font-size:12.0pt'><![if !supportEmptyParas]>&nbsp;<![endif]><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml.append("						<p class=MsoNormal>");
		cadreHtml
				.append("							<span lang=EN-US style='font-size:12.0pt'><![if !supportEmptyParas]>&nbsp;<![endif]><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml
				.append("						<p class=MsoNormal align=right style='margin-right:1.88gd;text-align:right;");
		cadreHtml.append("  tab-stops:117.0pt'>");
		cadreHtml
				.append("							<span style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:");
		cadreHtml
				.append("  \"Times New Roman\";mso-hansi-font-family:\"Times New Roman\"'>盖</span><span lang=EN-US style='font-size:12.0pt'><span style=\"mso-spacerun: yes\">&nbsp; </span></span><span");
		cadreHtml
				.append("								style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:");
		cadreHtml
				.append("  \"Times New Roman\";mso-hansi-font-family:\"Times New Roman\"'>章</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml
				.append("						<p class=MsoNormal align=right style='margin-top:.5gd;margin-right:1.02gd;");
		cadreHtml
				.append("  margin-bottom:0cm;margin-left:0cm;margin-bottom:.0001pt;text-align:right'>");
		cadreHtml
				.append("							<span style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>年</span><span lang=EN-US style='font-size:12.0pt'><span style=\"mso-spacerun: yes\">&nbsp; </span></span><span");
		cadreHtml
				.append("								style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>月</span><span lang=EN-US style='font-size:12.0pt'><span style=\"mso-spacerun: yes\">&nbsp; </span></span><span");
		cadreHtml
				.append("								style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>日</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=192 valign=top");
		cadreHtml
				.append("						style='width:144.0pt;border-top:none;border-left:");
		cadreHtml
				.append("  none;border-bottom:solid windowtext .5pt;border-right:solid windowtext .5pt;");
		cadreHtml
				.append("  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;");
		cadreHtml.append("  padding:0cm 5.4pt 0cm 5.4pt;height:93.55pt'>");
		cadreHtml.append("						<p class=MsoNormal>");
		cadreHtml
				.append("							<span lang=EN-US style='font-size:12.0pt'><![if !supportEmptyParas]>&nbsp;<![endif]><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml.append("						<p class=MsoNormal>");
		cadreHtml
				.append("							<span lang=EN-US style='font-size:12.0pt'><![if !supportEmptyParas]>&nbsp;<![endif]><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml.append("						<p class=MsoNormal>");
		cadreHtml
				.append("							<span lang=EN-US style='font-size:12.0pt'><![if !supportEmptyParas]>&nbsp;<![endif]><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml
				.append("						<p class=MsoNormal align=right style='margin-right:2.05gd;text-align:right;");
		cadreHtml.append("  tab-stops:117.0pt'>");
		cadreHtml
				.append("							<span style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:");
		cadreHtml
				.append("  \"Times New Roman\";mso-hansi-font-family:\"Times New Roman\"'>盖</span><span lang=EN-US style='font-size:12.0pt'><span style=\"mso-spacerun: yes\">&nbsp; </span></span><span");
		cadreHtml
				.append("								style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:");
		cadreHtml
				.append("  \"Times New Roman\";mso-hansi-font-family:\"Times New Roman\"'>章</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml
				.append("						<p class=MsoNormal align=right style='margin-top:.5gd;margin-right:1.2gd;");
		cadreHtml
				.append("  margin-bottom:0cm;margin-left:0cm;margin-bottom:.0001pt;text-align:right'>");
		cadreHtml
				.append("							<span style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>年</span><span lang=EN-US style='font-size:12.0pt'><span style=\"mso-spacerun: yes\">&nbsp; </span></span><span");
		cadreHtml
				.append("								style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>月</span><span lang=EN-US style='font-size:12.0pt'><span style=\"mso-spacerun: yes\">&nbsp; </span></span><span");
		cadreHtml
				.append("								style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>日</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml.append("					</td>");
		cadreHtml.append("				</tr>");
		cadreHtml.append("				<tr style='height:25.5pt'>");
		cadreHtml
				.append("					<td width=282 colspan=6 style='width:211.7pt;border:solid windowtext .5pt;");
		cadreHtml
				.append("  border-top:none;mso-border-top-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;");
		cadreHtml.append("  height:25.5pt'>");
		cadreHtml
				.append("						<p class=MsoNormal align=center style='text-align:center'>");
		cadreHtml
				.append("							<span style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>正式任职审批机关意见</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=288 colspan=3");
		cadreHtml
				.append("						style='width:216.0pt;border-top:none;border-left:");
		cadreHtml
				.append("  none;border-bottom:solid windowtext .5pt;border-right:solid windowtext .5pt;");
		cadreHtml
				.append("  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;");
		cadreHtml.append("  padding:0cm 5.4pt 0cm 5.4pt;height:25.5pt'>");
		cadreHtml
				.append("						<p class=MsoNormal align=center style='text-align:center'>");
		cadreHtml
				.append("							<span style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>正式任职行政任免机关意见</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml.append("					</td>");
		cadreHtml.append("				</tr>");
		cadreHtml.append("				<tr style='height:93.55pt'>");
		cadreHtml
				.append("					<td width=282 colspan=6 valign=top style='width:211.7pt;border:solid windowtext .5pt;");
		cadreHtml
				.append("  border-top:none;mso-border-top-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;");
		cadreHtml.append("  height:93.55pt'>");
		cadreHtml.append("						<p class=MsoNormal>");
		cadreHtml
				.append("							<span lang=EN-US style='font-size:12.0pt'><![if !supportEmptyParas]>&nbsp;<![endif]><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml.append("						<p class=MsoNormal>");
		cadreHtml
				.append("							<span lang=EN-US style='font-size:12.0pt'><![if !supportEmptyParas]>&nbsp;<![endif]><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml.append("						<p class=MsoNormal>");
		cadreHtml
				.append("							<span lang=EN-US style='font-size:12.0pt'><![if !supportEmptyParas]>&nbsp;<![endif]><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml
				.append("						<p class=MsoNormal align=right style='margin-right:2.05gd;text-align:right;");
		cadreHtml.append("  tab-stops:117.0pt'>");
		cadreHtml
				.append("							<span style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:");
		cadreHtml
				.append("  \"Times New Roman\";mso-hansi-font-family:\"Times New Roman\"'>盖</span><span lang=EN-US style='font-size:12.0pt'><span style=\"mso-spacerun: yes\">&nbsp; </span></span><span");
		cadreHtml
				.append("								style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:");
		cadreHtml
				.append("  \"Times New Roman\";mso-hansi-font-family:\"Times New Roman\"'>章</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml
				.append("						<p class=MsoNormal align=right style='margin-top:.5gd;margin-right:1.2gd;");
		cadreHtml
				.append("  margin-bottom:0cm;margin-left:0cm;margin-bottom:.0001pt;text-align:right;");
		cadreHtml.append("  word-break:break-all'>");
		cadreHtml
				.append("							<span style='font-size:12.0pt;font-family:宋体;");
		cadreHtml
				.append("  mso-ascii-font-family:\"Times New Roman\";mso-hansi-font-family:\"Times New Roman\"'>年</span><span lang=EN-US style='font-size:12.0pt'><span style=\"mso-spacerun: yes\">&nbsp; </span></span><span");
		cadreHtml
				.append("								style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:");
		cadreHtml
				.append("  \"Times New Roman\";mso-hansi-font-family:\"Times New Roman\"'>月</span><span lang=EN-US style='font-size:12.0pt'><span style=\"mso-spacerun: yes\">&nbsp; </span></span><span");
		cadreHtml
				.append("								style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:");
		cadreHtml
				.append("  \"Times New Roman\";mso-hansi-font-family:\"Times New Roman\"'>日</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml.append("					</td>");
		cadreHtml.append("					<td width=288 colspan=3 valign=top");
		cadreHtml.append("						style='width:216.0pt;border-top:none;");
		cadreHtml
				.append("  border-left:none;border-bottom:solid windowtext .5pt;border-right:solid windowtext .5pt;");
		cadreHtml
				.append("  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;");
		cadreHtml.append("  padding:0cm 5.4pt 0cm 5.4pt;height:93.55pt'>");
		cadreHtml.append("						<p class=MsoNormal>");
		cadreHtml
				.append("							<span lang=EN-US style='font-size:12.0pt'><![if !supportEmptyParas]>&nbsp;<![endif]><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml.append("						<p class=MsoNormal>");
		cadreHtml
				.append("							<span lang=EN-US style='font-size:12.0pt'><![if !supportEmptyParas]>&nbsp;<![endif]><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml.append("						<p class=MsoNormal>");
		cadreHtml
				.append("							<span lang=EN-US style='font-size:12.0pt'><![if !supportEmptyParas]>&nbsp;<![endif]><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml
				.append("						<p class=MsoNormal align=right style='margin-right:2.05gd;text-align:right;");
		cadreHtml.append("  tab-stops:117.0pt'>");
		cadreHtml
				.append("							<span style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:");
		cadreHtml
				.append("  \"Times New Roman\";mso-hansi-font-family:\"Times New Roman\"'>盖</span><span lang=EN-US style='font-size:12.0pt'><span style=\"mso-spacerun: yes\">&nbsp; </span></span><span");
		cadreHtml
				.append("								style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:");
		cadreHtml
				.append("  \"Times New Roman\";mso-hansi-font-family:\"Times New Roman\"'>章</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml
				.append("						<p class=MsoNormal align=right style='margin-top:.5gd;margin-right:1.2gd;");
		cadreHtml
				.append("  margin-bottom:0cm;margin-left:0cm;margin-bottom:.0001pt;text-align:right'>");
		cadreHtml
				.append("							<span style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>年</span><span lang=EN-US style='font-size:12.0pt'><span style=\"mso-spacerun: yes\">&nbsp; </span></span><span");
		cadreHtml
				.append("								style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>月</span><span lang=EN-US style='font-size:12.0pt'><span style=\"mso-spacerun: yes\">&nbsp; </span></span><span");
		cadreHtml
				.append("								style='font-size:12.0pt;font-family:宋体;mso-ascii-font-family:\"Times New Roman\";");
		cadreHtml
				.append("  mso-hansi-font-family:\"Times New Roman\"'>日</span><span lang=EN-US style='font-size:12.0pt'><o:p></o:p></span>");
		cadreHtml.append("						</p>");
		cadreHtml.append("					</td>");
		cadreHtml.append("				</tr>");
		cadreHtml.append("				<![if !supportMisalignedColumns]>");
		cadreHtml.append("				<tr height=0>");
		cadreHtml.append("					<td width=66 style='border:none'></td>");
		cadreHtml.append("					<td width=43 style='border:none'></td>");
		cadreHtml.append("					<td width=77 style='border:none'></td>");
		cadreHtml.append("					<td width=12 style='border:none'></td>");
		cadreHtml.append("					<td width=36 style='border:none'></td>");
		cadreHtml.append("					<td width=48 style='border:none'></td>");
		cadreHtml.append("					<td width=43 style='border:none'></td>");
		cadreHtml.append("					<td width=53 style='border:none'></td>");
		cadreHtml.append("					<td width=192 style='border:none'></td>");
		cadreHtml.append("				</tr>");
		cadreHtml.append("				<![endif]>");
		cadreHtml.append("			</table>");
		cadreHtml.append("");
		cadreHtml
				.append("			<p class=MsoNormal style='margin-top:.5gd;text-indent:21.0pt;mso-char-indent-count:");
		cadreHtml.append("2.0;mso-char-indent-size:10.5pt'>");
		cadreHtml
				.append("				<span style='font-family:宋体;mso-ascii-font-family:");
		cadreHtml
				.append("\"Times New Roman\";mso-hansi-font-family:\"Times New Roman\"'>填表人</span>");
		
	//填表人
		cadreHtml.append("<u>"+userName+"</u>");
		
		cadreHtml.append("				");
		cadreHtml.append("			</p>");
		cadreHtml.append("");
		cadreHtml.append("		</div>");
		cadreHtml.append("");
		//cadreHtml.append("	</body>");

		//System.out.println(cadreHtml.toString());
		return cadreHtml.toString();

	}
	
	
	
	
	
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getAgeDate() {
		return ageDate;
	}

	public void setAgeDate(String ageDate) {
		this.ageDate = ageDate;
	}

	public String getBirthAddress() {
		return birthAddress;
	}

	public void setBirthAddress(String birthAddress) {
		this.birthAddress = birthAddress;
	}

	public String getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}

	public String getBodyStatus() {
		return bodyStatus;
	}

	public void setBodyStatus(String bodyStatus) {
		this.bodyStatus = bodyStatus;
	}

	public ArrayList getCommunityBirthDate() {
		return communityBirthDate;
	}

	public void setCommunityBirthDate(ArrayList communityBirthDate) {
		this.communityBirthDate = communityBirthDate;
	}

	public ArrayList getCommunityName() {
		return communityName;
	}

	public void setCommunityName(ArrayList communityName) {
		this.communityName = communityName;
	}

	public ArrayList getCommunityOrg() {
		return communityOrg;
	}

	public void setCommunityOrg(ArrayList communityOrg) {
		this.communityOrg = communityOrg;
	}

	public ArrayList getCommunityRelation() {
		return communityRelation;
	}

	public void setCommunityRelation(ArrayList communityRelation) {
		this.communityRelation = communityRelation;
	}

	public ArrayList getCommunityZzmm() {
		return communityZzmm;
	}

	public void setCommunityZzmm(ArrayList communityZzmm) {
		this.communityZzmm = communityZzmm;
	}

	public String getCurrentDuty() {
		return currentDuty;
	}

	public void setCurrentDuty(String currentDuty) {
		this.currentDuty = currentDuty;
	}

	public String getCurrentOrg() {
		return currentOrg;
	}

	public void setCurrentOrg(String currentOrg) {
		this.currentOrg = currentOrg;
	}

	public String getDuty() {
		return duty;
	}

	public void setDuty(String duty) {
		this.duty = duty;
	}

	public ArrayList getEncouragementCausation() {
		return encouragementCausation;
	}

	public void setEncouragementCausation(ArrayList encouragementCausation) {
		this.encouragementCausation = encouragementCausation;
	}

	public ArrayList getEncouragementDate() {
		return encouragementDate;
	}

	public void setEncouragementDate(ArrayList encouragementDate) {
		this.encouragementDate = encouragementDate;
	}

	public ArrayList getEncouragementName() {
		return encouragementName;
	}

	public void setEncouragementName(ArrayList encouragementName) {
		this.encouragementName = encouragementName;
	}

	public ArrayList getEncouragementOrg() {
		return encouragementOrg;
	}

	public void setEncouragementOrg(ArrayList encouragementOrg) {
		this.encouragementOrg = encouragementOrg;
	}

	public String getEnterPartyDate() {
		return enterPartyDate;
	}

	public void setEnterPartyDate(String enterPartyDate) {
		this.enterPartyDate = enterPartyDate;
	}

	public String getEnterWorkDate() {
		return enterWorkDate;
	}

	public void setEnterWorkDate(String enterWorkDate) {
		this.enterWorkDate = enterWorkDate;
	}

	public ArrayList getFamilyBirthDate() {
		return familyBirthDate;
	}

	public void setFamilyBirthDate(ArrayList familyBirthDate) {
		this.familyBirthDate = familyBirthDate;
	}

	public ArrayList getFamilyName() {
		return familyName;
	}

	public void setFamilyName(ArrayList familyName) {
		this.familyName = familyName;
	}

	public ArrayList getFamilyOrg() {
		return familyOrg;
	}

	public void setFamilyOrg(ArrayList familyOrg) {
		this.familyOrg = familyOrg;
	}

	public ArrayList getFamilyRelation() {
		return familyRelation;
	}

	public void setFamilyRelation(ArrayList familyRelation) {
		this.familyRelation = familyRelation;
	}

	public ArrayList getFamilyZzmm() {
		return familyZzmm;
	}

	public void setFamilyZzmm(ArrayList familyZzmm) {
		this.familyZzmm = familyZzmm;
	}

	public String getFillInTableDate() {
		return fillInTableDate;
	}

	public void setFillInTableDate(String fillInTableDate) {
		this.fillInTableDate = fillInTableDate;
	}

	public String getFillInTableName() {
		return fillInTableName;
	}

	public void setFillInTableName(String fillInTableName) {
		this.fillInTableName = fillInTableName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNation() {
		return nation;
	}

	public void setNation(String nation) {
		this.nation = nation;
	}

	public ArrayList getPunishCausation() {
		return punishCausation;
	}

	public void setPunishCausation(ArrayList punishCausation) {
		this.punishCausation = punishCausation;
	}

	public ArrayList getPunishDate() {
		return punishDate;
	}

	public void setPunishDate(ArrayList punishDate) {
		this.punishDate = punishDate;
	}

	public ArrayList getPunishName() {
		return punishName;
	}

	public void setPunishName(ArrayList punishName) {
		this.punishName = punishName;
	}

	public ArrayList getPunishOrg() {
		return punishOrg;
	}

	public void setPunishOrg(ArrayList punishOrg) {
		this.punishOrg = punishOrg;
	}

	public String getQrzDegree() {
		return qrzDegree;
	}

	public void setQrzDegree(String qrzDegree) {
		this.qrzDegree = qrzDegree;
	}

	public String getQrzSchool() {
		return qrzSchool;
	}

	public void setQrzSchool(String qrzSchool) {
		this.qrzSchool = qrzSchool;
	}

	public String getQrzSchoolAge() {
		return qrzSchoolAge;
	}

	public void setQrzSchoolAge(String qrzSchoolAge) {
		this.qrzSchoolAge = qrzSchoolAge;
	}

	public String getQrzSpecialty() {
		return qrzSpecialty;
	}

	public void setQrzSpecialty(String qrzSpecialty) {
		this.qrzSpecialty = qrzSpecialty;
	}

	public ArrayList getResumeDuty() {
		return resumeDuty;
	}

	public void setResumeDuty(ArrayList resumeDuty) {
		this.resumeDuty = resumeDuty;
	}

	public ArrayList getResumeEndDate() {
		return resumeEndDate;
	}

	public void setResumeEndDate(ArrayList resumeEndDate) {
		this.resumeEndDate = resumeEndDate;
	}

	public ArrayList getResumeOrg() {
		return resumeOrg;
	}

	public void setResumeOrg(ArrayList resumeOrg) {
		this.resumeOrg = resumeOrg;
	}

	public ArrayList getResumeStartDate() {
		return resumeStartDate;
	}

	public void setResumeStartDate(ArrayList resumeStartDate) {
		this.resumeStartDate = resumeStartDate;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getTeChang() {
		return teChang;
	}

	public void setTeChang(String teChang) {
		this.teChang = teChang;
	}

	public String getZDegree() {
		return zDegree;
	}

	public void setZDegree(String degree) {
		zDegree = degree;
	}

	public String getZSchool() {
		return zSchool;
	}

	public void setZSchool(String school) {
		zSchool = school;
	}

	public String getZSchoolAge() {
		return zSchoolAge;
	}

	public void setZSchoolAge(String schoolAge) {
		zSchoolAge = schoolAge;
	}

	public String getZSpecialty() {
		return zSpecialty;
	}

	public void setZSpecialty(String specialty) {
		zSpecialty = specialty;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public ArrayList getExamDate() {
		return examDate;
	}

	public void setExamDate(ArrayList examDate) {
		this.examDate = examDate;
	}

	public ArrayList getExamResult() {
		return examResult;
	}

	public void setExamResult(ArrayList examResult) {
		this.examResult = examResult;
	}

	public String getFosterDate() {
		return fosterDate;
	}

	public void setFosterDate(String fosterDate) {
		this.fosterDate = fosterDate;
	}

	public ArrayList getCommunityBirthDateTxt() {
		return communityBirthDateTxt;
	}

	public void setCommunityBirthDateTxt(ArrayList communityBirthDateTxt) {
		this.communityBirthDateTxt = communityBirthDateTxt;
	}

	public ArrayList getFamilyBirthDateTxt() {
		return familyBirthDateTxt;
	}

	public void setFamilyBirthDateTxt(ArrayList familyBirthDateTxt) {
		this.familyBirthDateTxt = familyBirthDateTxt;
	}
	
	
	
}
