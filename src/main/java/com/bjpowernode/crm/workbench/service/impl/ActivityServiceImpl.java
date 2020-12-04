package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.settings.dao.UserDao;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.utils.SqlSessionUtil;
import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.dao.ActivityDao;
import com.bjpowernode.crm.workbench.dao.Activity_remarkDao;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.Activity_remark;
import com.bjpowernode.crm.workbench.service.ActivityService;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityServiceImpl implements ActivityService {
    private UserDao userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);
    private ActivityDao activityDao = SqlSessionUtil.getSqlSession().getMapper(ActivityDao.class);
    private Activity_remarkDao activity_remarkDao = (Activity_remarkDao) SqlSessionUtil.getSqlSession().getMapper(Activity_remarkDao.class);

    @Override
    public boolean save(Activity activity) {
        boolean flag = true;
        int count = activityDao.save(activity);
        if (count != 1) {
            flag = false;

        }

        return flag;
    }

    @Override
    public PaginationVO<Activity> pageList(Map<String, Object> map) {
        //取得total
        int total = activityDao.getTotalByCondition(map);

        //取得datalist
        List<Activity> dataList = activityDao.getActivityListByCondition(map);

        //创建一个Vo对象,将total和dataList封装到vo中
        PaginationVO<Activity> vo = new PaginationVO<Activity>();
        vo.setTotal(total);
        vo.setDataList(dataList);

        //将vo返回
        return vo;
    }

    @Override
    public boolean delete(String[] ids) {
        //查询出要删除的备注的数量
        boolean flag = true;
        int count1 = activity_remarkDao.getCountByAids(ids);
//删除备注,返回收到影响的条数(实际删除的数量)
        int count2 = activity_remarkDao.deleteByAids(ids);
        if (count1 != count2) {
            flag = false;
        }

        //删除市场活动
        int count3 = activityDao.delete(ids);
        if (count3 != ids.length) {
            flag = false;
        }

        return flag;
    }

    @Override
    public Map<String, Object> getUserListAndActivity(String id) {
        //取uList
        List<User> uList = userDao.getUserList();

        //取a
        Activity a = activityDao.getById(id);


        //将uList和a打包进map
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("uList", uList);
        map.put("a", a);

        //返回map给前端


        return map;
    }

    @Override
    public boolean update(Activity activity) {
        boolean flag = true;
        int count = activityDao.update(activity);
        if (count != 1) {
            flag = false;

        }

        return flag;

    }

    @Override
    public Activity detail(String id) {
        Activity a = activityDao.detail(id);
        return a;
    }

    @Override
    public List<Activity_remark> getRemarkListByAid(String activityId) {
        List<Activity_remark> arList = activity_remarkDao.getRemarkListByAid(activityId);
        return arList;
    }

    @Override
    public boolean deleteRemark(String id) {
        boolean flag = true;
        int count = activity_remarkDao.deleteRemark(id);
        if (count != 1) {
            flag = false;
        }
        return flag;
    }

    @Override
    public boolean saveRemark(Activity_remark ar) {
        boolean flag = true;
        int count = activity_remarkDao.saveRemark(ar);
        if (count != 1) {
            flag = false;
        }


        return flag;
    }

    @Override
    public boolean updateRemark(Activity_remark ar) {
        boolean flag=true;
        int count=activity_remarkDao.updateRemark(ar);

        if (count!=1){
            flag=false;
        }

        return flag;
    }
}
