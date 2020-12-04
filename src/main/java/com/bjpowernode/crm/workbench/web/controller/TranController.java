package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.settings.service.impl.UserServiceImpl;
import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.PrintJson;
import com.bjpowernode.crm.utils.ServiceFactory;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.Contacts;
import com.bjpowernode.crm.workbench.domain.Tran;
import com.bjpowernode.crm.workbench.domain.TranHistory;
import com.bjpowernode.crm.workbench.service.ClueService;
import com.bjpowernode.crm.workbench.service.ContactsService;
import com.bjpowernode.crm.workbench.service.CustomerService;
import com.bjpowernode.crm.workbench.service.TranService;
import com.bjpowernode.crm.workbench.service.impl.ClueServiceImpl;
import com.bjpowernode.crm.workbench.service.impl.ContactsServiceImpl;
import com.bjpowernode.crm.workbench.service.impl.CustomerServiceImpl;
import com.bjpowernode.crm.workbench.service.impl.TranServiceImpl;
import javafx.scene.control.Pagination;

import javax.lang.model.element.NestingKind;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入到交易控制器");
        String path = request.getServletPath();
        if ("/workbench/transaction/add.do".equals(path)) {
            add(request, response);
        } else if ("/workbench/transaction/getActivityListByName.do".equals(path)) {
            getActivityListByName(request, response);
        } else if ("/workbench/transaction/getContactsListByfullname.do".equals(path)) {
            getContactsListByfullname(request, response);
        } else if ("/workbench/transaction/getCustomerName.do".equals(path)) {
            getCustomerName(request, response);
        } else if ("/workbench/transaction/save.do".equals(path)) {
            save(request, response);
        } else if ("/workbench/transaction/showTranList.do".equals(path)) {
            showTranList(request, response);
        } else if ("/workbench/transaction/detail.do".equals(path)) {
            detail(request, response);
        } else if ("/workbench/transaction/getHistoryListByTranId.do".equals(path)) {
            getHistoryListByTranId(request, response);
        } else if ("/workbench/transaction/changeStage.do".equals(path)){
            changeStage(request,response);
        } else if ("/workbench/transaction/getCharts.do".equals(path)){
            getCharts(request,response);
        }
    }

    private void getCharts(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("取得交易统计图表的数据");
        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());

        Map<String,Object> map = ts.getCharts();
        PrintJson.printJsonObj(response,map);

    }

    private void changeStage(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("执行改变阶段的操作");
        String id = request.getParameter("id");
        String stage = request.getParameter("stage");
        String money = request.getParameter("money");
        String expectedDate = request.getParameter("expectedDate");
        String editBy = ((User)request.getSession().getAttribute("user")).getName();
        String editTime = DateTimeUtil.getSysTime();

        Tran t = new Tran();
        t.setId(id);
        t.setStage(stage);
        t.setEditBy(editBy);
        t.setEditTime(editTime);
        t.setMoney(money);
        t.setExpectedDate(expectedDate);

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());
        boolean flag = ts.changeStage(t);
        Map<String,Object> map = new HashMap<String, Object>();

        //处理可能性
        Map<String,String> pMap = (Map<String, String>) this.getServletContext().getAttribute("pMap");
        t.setPossibility(pMap.get(stage));
        map.put("success",flag);
        map.put("t",t);
        PrintJson.printJsonObj(response,map);





    }

    private void getHistoryListByTranId(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("根据交易id取得相应的历史列表");
        String tranId = request.getParameter("tranId");
        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());
        List<TranHistory> thList = ts.getHistoryByTranId(tranId);
        //处理可能性
        Map<String, String> pMap = (Map<String, String>) this.getServletContext().getAttribute("pMap");
        for (TranHistory th : thList) {

            String stage = th.getStage();
            String possibility = pMap.get(stage);
            th.setPossibility(possibility);
        }
        PrintJson.printJsonObj(response, thList);


    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("跳转到详细信息页");
        String id = request.getParameter("id");
        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());
        Tran tran = ts.detail(id);
        /*
        处理可能性
        阶段和可能性之间的对应关系在 pMap里面
         */
        String stage = tran.getStage();
        Map<String, String> pMap = (Map<String, String>) this.getServletContext().getAttribute("pMap");
        String possibility = pMap.get(stage);
        tran.setPossibility(possibility);

        request.setAttribute("tran", tran);

        request.getRequestDispatcher("/workbench/transaction/detail.jsp").forward(request, response);


    }

    private void showTranList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入到查询交易表操作,分页查询");
        String owner = request.getParameter("owner");
        String name = request.getParameter("name");
        String customerName = request.getParameter("customerName");
        String stage = request.getParameter("stage");
        String type = request.getParameter("type");
        String source = request.getParameter("source");
        String contactsName = request.getParameter("contactsName");
        String pageNoStr = request.getParameter("pageNo");
        String pageSizeStr = request.getParameter("pageSize");
        int pageNo = Integer.valueOf(pageNoStr);
        int pageSize = Integer.valueOf(pageSizeStr);
        int skipCount = (pageNo - 1) * pageSize;
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("owner", owner);
        map.put("name", name);
        map.put("stage", stage);
        map.put("type", type);
        map.put("source", source);
        map.put("pageSize", pageSize);
        map.put("skipCount", skipCount);
        map.put("customerName", customerName);
        map.put("contactsName", contactsName);
        TranService tranService = (TranService) ServiceFactory.getService(new TranServiceImpl());
        PaginationVO<Tran> dataList = tranService.showTranList(map);
        PrintJson.printJsonObj(response, dataList);


    }

    private void save(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("执行添加交易的操作");

        String id = UUIDUtil.getUUID();
        String owner = request.getParameter("owner");
        String money = request.getParameter("money");
        String name = request.getParameter("name");
        String expectedDate = request.getParameter("expectedDate");
        String customerName = request.getParameter("customerName"); //此处我们暂时只有客户名称，还没有id
        String stage = request.getParameter("stage");
        String type = request.getParameter("type");
        String source = request.getParameter("source");
        String activityId = request.getParameter("activityId");
        String contactsId = request.getParameter("contactsId");
        String createTime = DateTimeUtil.getSysTime();
        String createBy = ((User) request.getSession().getAttribute("user")).getName();
        String description = request.getParameter("description");
        String contactSummary = request.getParameter("contactSummary");
        String nextContactTime = request.getParameter("nextContactTime");

        Tran t = new Tran();
        t.setId(id);
        t.setOwner(owner);
        t.setMoney(money);
        t.setName(name);
        t.setExpectedDate(expectedDate);
        t.setStage(stage);
        t.setType(type);
        t.setSource(source);
        t.setActivityId(activityId);
        t.setContactsId(contactsId);
        t.setCreateTime(createTime);
        t.setCreateBy(createBy);
        t.setDescription(description);
        t.setContactSummary(contactSummary);
        t.setNextContactTime(nextContactTime);

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());

        boolean flag = ts.save(t, customerName);

        if (flag) {

            //如果添加交易成功，跳转到列表页
            response.sendRedirect(request.getContextPath() + "/workbench/transaction/index.jsp");

        }


    }

    private void getCustomerName(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("取得客户名称列表,按照客户名称进行模糊查询");
        String name = request.getParameter("name");
        CustomerService customerService = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());
        List<String> sList = customerService.getCustomerName(name);
        PrintJson.printJsonObj(response, sList);

    }

    private void getContactsListByfullname(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("根据联系人名字模糊查询");
        String fullname = request.getParameter("cName");
        ContactsService service = (ContactsService) ServiceFactory.getService(new ContactsServiceImpl());
        List<Contacts> cList = service.getContactsListByfullname(fullname);
        PrintJson.printJsonObj(response, cList);


    }

    private void getActivityListByName(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("根据市场活动名称模糊查询");
        String name = request.getParameter("aname");
        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        List<Activity> data = cs.getActivityListByName(name);
        PrintJson.printJsonObj(response, data);
    }

    private void add(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入到跳转交易添加页的操作");
        //取得用户信息列表.为所有者下拉框铺值
        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> uList = us.getUserList();
        request.setAttribute("uList", uList);
        request.getRequestDispatcher("/workbench/transaction/save.jsp").forward(request, response);

    }
}
