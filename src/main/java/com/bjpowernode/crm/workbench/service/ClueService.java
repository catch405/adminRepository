package com.bjpowernode.crm.workbench.service;

import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.Clue;
import com.bjpowernode.crm.workbench.domain.Tran;

import java.util.List;
import java.util.Map;

public interface ClueService {
    boolean save(Clue c);

    PaginationVO<Clue> pageList(Map<String, Object> map);

    Clue detail(String id);

    List<Activity> getActivityListByClueId(String clueId);

    boolean unbundById(String id);

    List<Activity> getActivityListByNameAndNotByClueId(Map<String, String> map);

    boolean bund(String clueId, String[] activityIds);

    List<Activity> getActivityListByName(String name);

    boolean convert(String clueId, Tran t,String createBy);
}
