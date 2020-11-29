# 人力资源管理系统-后端

# 贵州银行人力资源系统(HRS：Human Resource System)

## 一 模块说明：
##### 1.1 组织机构管理
##### 1.2 招聘管理
##### 1.3 人员信息管理
##### 1.4 人事异动管理
##### 1.5 劳动合同管理
##### 1.6 干部管理
##### 1.7 考勤管理
##### 1.8 保险管理
##### 1.9 薪资管理
##### 1.10 培训管理
##### 1.11 绩效管理
##### 1.12 能力素质
##### 1.13 报表管理
##### 1.14 员工应用
##### 1.15 领导应用

## 二 集成关系：
##### 2.1 新核心业务系统
##### 2.2 全渠道平台
##### 2.3 数据同步（大数据平台）
##### 2.4 数据同步（Kafka平台）
##### 2.5 统一门户（领导/员工应用）
##### 2.6 文件传输平台/影像平台
##### 2.7 邮件服务
##### 2.8 短信平台
##### 2.9 招聘门户
##### 2.10 统一认证

## 三 如何区别环境打包：
##### 3.1 开发环境(默认)
```shell
mvn clean && mvn package -Dmaven.test.skip=true -Pdev
```
##### 3.2 联调环境
```shell
mvn clean && mvn package -Dmaven.test.skip=true -Pliantiao
```
##### 3.3 SIT环境
```shell
mvn clean && mvn package -Dmaven.test.skip=true -Psit
```
##### 3.4 UAT环境
```shell
mvn clean && mvn package -Dmaven.test.skip=true -Puat
```
##### 3.5 生产环境
```shell
mvn clean && mvn package -Dmaven.test.skip=true -Pprod
```


---

## 开发须知  
#### 1. 代码注释要简明扼要，必须要有！  
#### 2. 数据库表及字段必须要有注释！  
#### 3. 分支管理
   开发人员只能从develop分支派生自己的开发分支进行开发（分支命名规则为feature_模块名，feature_开发者英文名，总之以feature_开头），
提交代码请求合并时，只能请求合并到develop分支，不能直接推送到develop分支（master、release也都不能动），提交合并人员为项目经理，
项目经理来进行代码合并至develop分支。具体开发时，根据项目功能模块分解的开发任务进行各自的代码编写，
提交代码时尽量避免冲突（如公共类文件修改时，约定一个开发人员进行单独提交合并，其他人只管更新即可）。  

#### 4. 提交流程
```pre
                                 |  
                                 ↓  
                          ................  
                          ┆开发人员编写代码┆    
                          ▔▔▔▔▔▔▔▔▔                       
                                 ↓                               
                           ................      .........                 
                           ┆开发人员push代码┆  ←  ┆fix 代码┆   ← ┒  
                           ▔▔▔▔▔▔▔▔▔      ▔▔▔▔▔     ｜  
                                 ↓                             ｜ 
                           .......................             ｜  
                           ┆创建请求并提交给项目经理┆            ｜  
                           ▔▔▔▔▔▔▔▔▔▔▔▔▔            ｜  
                                 ↓                             ｜  
                           ....................                ｜ 
                          ╱ 项目经理review代码 ╱   × 未通过  →   ┙  
                         ╱..................╱  
                                 √  
                                通过  
                                 ↓   
                           .................  
                           ┆合并代码至develop┆  
                           ▔▔▔▔▔▔▔▔▔▔  
                                 ↓  
                           ................  
                           ┆开发人员更新代码┆  
                           ▔▔▔▔▔▔▔▔▔      
```
                    
#### 5. 开发规范见Wiki： http://172.31.1.195/guanli/dm/HR/hrms-backend/wikis/home  

---