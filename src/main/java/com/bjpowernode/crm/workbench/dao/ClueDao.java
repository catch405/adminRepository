package com.bjpowernode.crm.workbench.dao;

import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.Clue;

import java.util.List;
import java.util.Map;

public interface ClueDao {


    int save(Clue c);

    int getTotalByCondition(Map<String, Object> map);

    List<Clue> getCListByCondition(Map<String, Object> map);

    Clue detail(String id);

    List<Activity> getActivityListByClueId(String clueId);

    int unbundById(String id);

    List<Activity> getActivityListByNameAndNotByClueId(Map<String, String> map);

    List<Activity> getActivityListByName(String name);

    Clue getById(String clueId);

    int delete(String clueId);
}
