<%@page import="java.util.Iterator"%>
<%@ page
	import="org.activiti.engine.impl.pvm.process.ActivityImpl,org.activiti.engine.impl.persistence.entity.TaskEntity,java.util.List,java.util.Map,java.util.HashMap,org.activiti.engine.form.FormProperty,org.activiti.engine.form.FormType"%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="path" value="${pageContext.request.contextPath}" />
<jsp:include page="head.jsp"></jsp:include>
	<div class="wrap">
	<div class="container">
			<%
				TaskEntity task = (TaskEntity) request.getAttribute("task");
				List<FormProperty> fpList = (List<FormProperty>) request.getAttribute("fpList");
				Map map = (HashMap) request.getAttribute("map");
				String taskId = (String) request.getAttribute("taskId");
				List<ActivityImpl> activityList = (List<ActivityImpl>) request.getAttribute("activityList");
				if ("申请".equals(task.getName())) {
			%>
			<form action="${path}/simple/submit.do" method="post" role="form" class="leaveform">
				<h2 class="form-signin-heading text-center"><%=task.getName()%></h2>
				<input type="hidden" name="taskId" value="<%=taskId%>">
				<%
					for (FormProperty fp : fpList) {
				%>
				<div class="form-group">
				<label for="<%=fp.getId()%>"><%=fp.getName()%></label>
				<%
					if (fp.isRequired()) {
				%>
				<font color=red>*</font>
				<%
					}	
					FormType ft = fp.getType();

					if ("enum".equals(ft.getName())) {
				%>
				<select id="<%=fp.getId()%>" name="<%=fp.getId()%>" class="form-control input-lg selectpicker">
					<%
						Map mp = (HashMap) ft.getInformation("values");
									Iterator iter = mp.entrySet().iterator();
									while (iter.hasNext()) {
										Map.Entry entry = (Map.Entry) iter.next();
										Object key = entry.getKey();
										Object val = entry.getValue();
					%>
					<option value="<%=key%>"><%=val%></option>"
					<%
						}
					%>
				</select>
				</div>
				<%
					} else{
				%>
				<input type='text' name="<%=fp.getId()%>" class="form-control input-lg" id="<%=fp.getId()%>"/>
				</div>
				<%
					}
				  }%>
				  <button type="submit" class="btn btn-primary btn-lg" name="result" value="同意"><span class="glyphicon glyphicon-ok-sign"></span> 申请</button>
				  <%
				} else  if(!"申请".equals(task.getName())){
				%>
				<form action="${path}/simple/submit.do" method="post" role="form" class="form-horizontal leaveform">
				<h2 class="form-signin-heading text-center"><%=task.getName()%></h2>
					<input type="hidden" name="taskId" value="<%=taskId%>">
					
					<div class="form-group">
						<label class="col-sm-3 control-label">请假天数</label>
						<div class="col-sm-9">
							<p class="form-control-static"><%=map.get("day")%></p>
						</div>
					</div>
					<input type="hidden" name="day" value="<%=map.get("day")%>">
					
					<%
						if ("0".equals(String.valueOf(map.get("type")))) {
					%>
					
					<div class="form-group">
						<label class="col-sm-3 control-label">假别</label>
						<div class="col-sm-9">
							<p class="form-control-static">事假</p>
						</div>
					</div>
					
					<input type="hidden" name="type" value="<%=map.get("type")%>" />
					
					<div class="form-group">
						<label class="col-sm-3 control-label">请假原因</label>
						<div class="col-sm-9">
							<p class="form-control-static"><%=map.get("reason")%></p>
						</div>
					</div>
					
					<input type="hidden" name="reason" value="<%=map.get("reason")%>" />

					<%
						}
					%>
					<button type="submit" class="btn btn-primary btn-lg" name="result" value="同意"><span class="glyphicon glyphicon-ok-sign"></span> 同意</button>
					<% 
							if(activityList!=null&&activityList.size()>0){
								%>
								<button type="submit" class="btn btn-default btn-lg" name="result" value="驳回"><span class="glyphicon glyphicon-remove-circle"></span> 驳回</button>					
								<select name="backActivityId" class="form-control input-lg selectpicker">
								<%
								for (ActivityImpl activiti : activityList) {
									%>
									<option value="<%=activiti.getId()%>"><%=activiti.getProperty("name")%></option>
									<%
								}%>
								</select>
								<%
							}
						%>
						<input type="text" name="toSign" value="" class="form-control input-lg"> 
						<button type="submit" class="btn btn-default btn-lg" name="result" value="转签"><span class="glyphicon glyphicon-remove-circle"></span> 转签</button>	
					<%				
					}
					%>
				</form>
		</div>
	</div>
<jsp:include page="foot.jsp"></jsp:include>