package com.hjsj.hrms.service.core;

public interface HrServiceIntf {
  public String createOrganization(Organization org);
  public boolean updateOrganization(Organization org,String outerOrgId);
  public boolean removeOrganization(String outerOrgId);
  public String createUser(User user);
  public boolean updateUser(User user,String outerUserId);
  public boolean removeUser(String outerUserId);
  public boolean changeUserOrg(String newDeptId,String oldDeptId,String outerUserId);
  public boolean validateUserId(String UseCode);
  public Organization[] getAllOrganizations();
  public User[] getAllUsers();
  public int batchAppend(String username ,String password,String xml);
  public int batchUpdate(String username ,String password,String xml);
  public int batchDelete(String username ,String password,String xml);
}
