package com.bjpowernode.crm.workbench.dao;

import com.bjpowernode.crm.workbench.domain.Activity_remark;

import java.util.List;

public interface Activity_remarkDao {
    int getCountByAids(String[] ids);

    int deleteByAids(String[] ids);

    List<Activity_remark> getRemarkListByAid(String activityId);

    int deleteRemark(String id);

    int saveRemark(Activity_remark ar);

    int updateRemark(Activity_remark ar);
}
