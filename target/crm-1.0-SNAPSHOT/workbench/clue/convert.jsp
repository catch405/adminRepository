<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";

			String fullname = request.getParameter("fullname");
			String id = request.getParameter("id");
			String appellation = request.getParameter("appellation");
			String company = request.getParameter("company");
			String owner = request.getParameter("owner");
%>
<!DOCTYPE html>
<html>
<head>
	<base href="<%=basePath%>">
	<meta charset="UTF-8">

<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>


<link href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css" rel="stylesheet" />
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>

<script type="text/javascript">
	$(function(){
		$("#isCreateTransaction").click(function(){
			if(this.checked){
				$("#create-transaction2").show(200);
			}else{
				$("#create-transaction2").hide(200);
			}
		});
		$(".time").datetimepicker({
			minView: "month",
			language:  'zh-CN',
			format: 'yyyy-mm-dd',
			autoclose: true,
			todayBtn: true,
			pickerPosition: "bottom-left"
		});
		//为放大镜图标绑定事件,打开搜索市场活动的模态窗口
		$("#openSearchModalBtn").click(function () {
			$("#searchActivityModal").modal("show");
		});
		//为搜索市场活动的模态窗口的搜索框绑定事件,执行搜索并展现市场活动列表的操作
		$("#aname").keydown(function (event) {
			if (event.keyCode==13){
				$.ajax({
					url:"workbench/clue/getActivityListByName.do",
					data:{
						"aname":$.trim($("#aname").val())
					},
					type:"get",
					dataType:"json",
					success:function (data) {
						var html="";
						$.each(data,function (i,n) {
							html +='<tr>';
							html +='<td><input type="radio" name="xz" value="'+n.id+'" "/></td>';
							html +='<td id="'+n.id+'">'+n.name+'</td>';
							html +='<td>'+n.startDate+'</td>';
							html +='<td>'+n.endDate+'</td>';
							html +='<td>'+n.owner+'</td>';
							html +='</tr>';

						})
						$("#activitySearchBody").html(html);
					}
				})

				return false;
			}
		});

		//为搜索市场活动的模态窗口中的提交按钮绑定事件,填充市场活动源(填写两项信息,id和name)
		$("#submitActivityBtn").click(function () {

			//取得选中的市场活动id
			//取得选中市场活动的id

				var $xz = $("input[name=xz]:checked");
				var id = $xz.val();
			if (id!=null) {
				//取得选中市场活动的名字
				var name = $("#" + id).html();
				$("#activityName").val(name);
				$("#activityId").val(id);
				$("#searchActivityModal").modal("hide");
				$("#aname").val("");
				$("#activitySearchBody").html("");


			}else {
				alert("请选择一条记录");
			}
		});

		//为转换按钮绑定事件,执行线索的转换操作
		$("#convertBtn").click(function () {
				/*提交请求到后台.执行线索转换的操作,应该发起一个传统请求
				请求结束后,响应回线索列表页
				根据'为客户创建交易的复选框有没有挑勾'来判断是否有创建交易

				 */
			if ($("#isCreateTransaction").prop("checked"))	{
				//alert("需要创建交易")
				//如果需要创建交易,传的参数为 clueId之外还需要交易表中的信息(金额,预计成交日期,交易名称,阶段,市场活动源(id)
				/*//window.location.href = "workbench/clue/convert.do?clueId=xxx&money=xx&expectedDate=xx&name=xx&stage=xx&activityId=xx";*/
				//考虑到表单可能扩充,参数会很多,所以改到使用提交表单的形式发出本次请求
				//提交表单表单的参数不用手动挂载(直接在表单写name属性),而且表单还能提交post请求


				//提交表单
				$("#tranForm").submit();


			}else {
				//alert("不需要")
				//不用创建交易的时候,传一个clueId就行
				window.location.href = "workbench/clue/convert.do?clueId=${param.id}";
			}


		});

	});
</script>

</head>
<body>
	
	<!-- 搜索市场活动的模态窗口 -->
	<div class="modal fade" id="searchActivityModal" role="dialog" >
		<div class="modal-dialog" role="document" style="width: 90%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title">搜索市场活动</h4>
				</div>
				<div class="modal-body">
					<div class="btn-group" style="position: relative; top: 18%; left: 8px;">
						<form class="form-inline" role="form">
						  <div class="form-group has-feedback">
						    <input type="text" class="form-control" id="aname" style="width: 300px;" placeholder="请输入市场活动名称，支持模糊查询">
						    <span class="glyphicon glyphicon-search form-control-feedback"></span>
						  </div>
						</form>
					</div>
					<table id="activityTable" class="table table-hover" style="width: 900px; position: relative;top: 10px;">
						<thead>
							<tr style="color: #B3B3B3;">
								<td></td>
								<td>名称</td>
								<td>开始日期</td>
								<td>结束日期</td>
								<td>所有者</td>
								<td></td>
							</tr>
						</thead>
						<tbody id="activitySearchBody">
					<%--		<tr>
								<td><input type="radio" name="activity"/></td>
								<td>发传单</td>
								<td>2020-10-10</td>
								<td>2020-10-20</td>
								<td>zhangsan</td>
							</tr>
							<tr>
								<td><input type="radio" name="activity"/></td>
								<td>发传单</td>
								<td>2020-10-10</td>
								<td>2020-10-20</td>
								<td>zhangsan</td>
							</tr>--%>
						</tbody>
					</table>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
					<button type="button" class="btn btn-primary"  id="submitActivityBtn">提交</button>
				</div>
			</div>
		</div>
	</div>

	<div id="title" class="page-header" style="position: relative; left: 20px;">
		<h4>转换线索 <small><%=fullname%><%=appellation%>-<%=company%></small></h4>
	</div>
	<div id="create-customer" style="position: relative; left: 40px; height: 35px;">
		新建客户：<%=company%>
	</div>
	<div id="create-contact" style="position: relative; left: 40px; height: 35px;">
		<%--也可以用el表达式取值,从隐含对象param取值--%>
		新建联系人：${param.fullname}<%--<%=fullname%><%=appellation%>--%>
	</div>
	<div id="create-transaction1" style="position: relative; left: 40px; height: 35px; top: 25px;">
		<input type="checkbox" id="isCreateTransaction"/>
		为客户创建交易
	</div>
	<div id="create-transaction2" style="position: relative; left: 40px; top: 20px; width: 80%; background-color: #F7F7F7; display: none;" >
	<%--改造这张form表单,把要提交的参数,以name属性列出--%>

		<form id="tranForm" action="workbench/clue/convert.do" method="post">
			<input type="hidden" name="flag" value="a"/><%--给后端做一个是否需要创建交易的标记--%>
			<input type="hidden" name="clueId" value="${param.id}"/>
		  <div class="form-group" style="width: 400px; position: relative; left: 20px;">
		    <label for="amountOfMoney">金额</label>
		    <input type="text" class="form-control" name="money">
		  </div>
		  <div class="form-group" style="width: 400px;position: relative; left: 20px;">
		    <label for="tradeName">交易名称</label>
		    <input type="text" class="form-control" id="tradeName" name="name">
		  </div>
		  <div class="form-group" style="width: 400px;position: relative; left: 20px;">
		    <label for="expectedClosingDate">预计成交日期</label>
		    <input type="text" class="form-control time" name="expectedDate">
		  </div>
		  <div class="form-group" style="width: 400px;position: relative; left: 20px;">
		    <label for="stage">阶段</label>
		    <select id="stage"  class="form-control" name="stage">
		    	<option></option>
				<c:forEach items="${stageList}" var="s">
					<option value="${s.value}">${s.text}</option>
				</c:forEach>
		    	<%--<option>资质审查</option>
		    	<option>需求分析</option>
		    	<option>价值建议</option>
		    	<option>确定决策者</option>
		    	<option>提案/报价</option>
		    	<option>谈判/复审</option>
		    	<option>成交</option>
		    	<option>丢失的线索</option>
		    	<option>因竞争丢失关闭</option>--%>
		    </select>
		  </div>
		  <div class="form-group" style="width: 400px;position: relative; left: 20px;">
		    <label for="activity">市场活动源&nbsp;&nbsp;<a href="javascript:void(0);" id="openSearchModalBtn" style="text-decoration: none;"><span class="glyphicon glyphicon-search"></span></a></label>
		    <input type="text" class="form-control" id="activityName" placeholder="点击上面搜索" readonly>
			  <%--后台需要操作的是id,可以把他保存到隐藏域中--%>
			  <input type="hidden" id="activityId" name="activityId"/>
		  </div>
		</form>
		
	</div>
	
	<div id="owner" style="position: relative; left: 40px; height: 35px; top: 50px;">
		记录的所有者：<br>
		<b><%=owner%></b>
	</div>
	<div id="operation" style="position: relative; left: 40px; height: 35px; top: 100px;">
		<input class="btn btn-primary" type="button" value="转换" id="convertBtn">
		&nbsp;&nbsp;&nbsp;&nbsp;
		<input class="btn btn-default" type="button" value="取消">
	</div>
</body>
</html>