<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%--引入jstl标准标签库--%>
<%
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
%>
<html>
<head>
    <base href="<%=basePath%>">
    <title></title>

</head>
<body>
$.ajax({
        url:"",
        data:{

        },
        type:"",
        dataType:"json",
        success:function (data) {

        }
})
String createTime = DateTimeUtil.getSysTime();
//创建人当前登录用户
String createBy = ((User)request.getSession().getAttribute("user")).getName();
//日历控件
$(".time").datetimepicker({
minView: "month",
language:  'zh-CN',
format: 'yyyy-mm-dd',
autoclose: true,
todayBtn: true,
pickerPosition: "bottom-left"
});
</body>
</html>