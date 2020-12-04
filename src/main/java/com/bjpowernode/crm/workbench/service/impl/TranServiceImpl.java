package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.SqlSessionUtil;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.dao.ContactsDao;
import com.bjpowernode.crm.workbench.dao.CustomerDao;
import com.bjpowernode.crm.workbench.dao.TranDao;
import com.bjpowernode.crm.workbench.dao.TranHistoryDao;


import com.bjpowernode.crm.workbench.domain.Customer;
import com.bjpowernode.crm.workbench.domain.Tran;
import com.bjpowernode.crm.workbench.domain.TranHistory;
import com.bjpowernode.crm.workbench.service.TranService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranServiceImpl implements TranService {
    private TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);
    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);
    private ContactsDao contactsDao = SqlSessionUtil.getSqlSession().getMapper(ContactsDao.class);

    @Override
    public boolean save(Tran t, String customerName) {
        /*
        交易添加业务:
            在做添加之前,参数t里面少了一项信息,就是客户的主键 customerId
            先处理客户客户相关的需求
            1.判断customerName,根据客户名称在客户表进行精确查询
                如果有这个客户,则取出id,封装到t对象,如果没有,则在客户表新建一条信息,然后将新建客户的id取出,封装到t对象
            2.t对象信息全了以后,执行交易添加操作

            3.添加完毕后,需要生成一交易历史
         */
        boolean flag = true;
        Customer cus = customerDao.getCustomerByName(customerName);//根据名称精确查询到客户
        if (cus == null) {

            cus = new Customer();
            cus.setId(UUIDUtil.getUUID());
            cus.setName(customerName);
            cus.setCreateBy(t.getCreateBy());
            cus.setCreateTime(DateTimeUtil.getSysTime());
            cus.setContactSummary(t.getContactSummary());
            cus.setNextContactTime(t.getNextContactTime());
            cus.setOwner(t.getOwner());
            //添加客户
            int count1 = customerDao.save(cus);
            if (count1 != 1) {
                flag = false;
            }
        }
        t.setCustomerId(cus.getId());


        //添加交易
        int count2 = tranDao.save(t);
        if (count2 != 1) {
            flag = false;
        }

        //添加交易历史
        TranHistory th = new TranHistory();
        th.setId(UUIDUtil.getUUID());
        th.setTranId(t.getId());
        th.setStage(t.getStage());
        th.setMoney(t.getMoney());
        th.setExpectedDate(t.getExpectedDate());
        th.setCreateTime(DateTimeUtil.getSysTime());
        th.setCreateBy(t.getCreateBy());
        int count3 = tranHistoryDao.save(th);
        if (count3 != 1) {
            flag = false;
        }


        return flag;
    }

    @Override
    public PaginationVO<Tran> showTranList(Map<String, Object> map) {

        //取得total
        int total = tranDao.getTotalCondition(map);
        //取得TranList
        List<Tran> dataList = tranDao.getListCondition(map);
        PaginationVO<Tran> vo = new PaginationVO<Tran>();
        vo.setTotal(total);
        vo.setDataList(dataList);

        return vo;
    }

    @Override
    public Tran detail(String id) {

        Tran t = tranDao.detail(id);

        return t;
    }

    @Override
    public List<TranHistory> getHistoryByTranId(String tranId) {
        List<TranHistory> thList = tranHistoryDao.getHistoryByTranId(tranId);


        return thList;
    }

    @Override
    public boolean changeStage(Tran t) {
        boolean flag = true;
        //改变交易阶段
        int count = tranDao.changeStage(t);
        if (count != 1) {
            flag = false;
        }

        //随着修改,要生成一张交易历史表
        TranHistory th = new TranHistory();
        th.setId(UUIDUtil.getUUID());
        th.setCreateBy(t.getEditBy());
        th.setCreateTime(DateTimeUtil.getSysTime());
        th.setTranId(t.getId());
        th.setMoney(t.getMoney());
        th.setExpectedDate(t.getExpectedDate());
        th.setStage(t.getStage());
        th.setPossibility(t.getPossibility());
        //添加交易历史
        int count1 = tranHistoryDao.save(th);
        if (count1 != 1) {
            flag = false;
        }


        return flag;
    }

    @Override
    public Map<String, Object> getCharts() {
        //取得总条数total
        int total = tranDao.getTotal();

        //取得dataList
        List<Map<String,Object>> dataList = tranDao.getCharts();
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("total",total);
        map.put("dataList",dataList);

        //将total和dataList封装到map返回前端


        return map;
    }
}
