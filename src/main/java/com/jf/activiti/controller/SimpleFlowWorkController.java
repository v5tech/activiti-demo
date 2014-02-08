package com.jf.activiti.controller;

import com.jf.activiti.service.ProcessExtensionService;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.*;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.GroupQuery;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.bpmn.diagram.ProcessDiagramGenerator;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.util.IoUtil;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.jboss.marshalling.ByteInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.InputStream;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: wangbin
 * Date: 13-11-11
 * Time: 下午2:14
 * To change this template use File | Settings | File Templates.
 *  Activiti工作流引擎的数据库表中的表名称都是以 ACT_.第二部分两个字母表示表的类型。使用模糊匹配的方式说明表的类型匹配activiti的服务API.
 ·         ACT_RE_*: RE代表仓储(Repository).这种表前缀以“static”表示流程定义信息或者流程资源信息（如流程的图表和规则等）.
 ·         ACT_RU_*: RU标识为运行(Runtime)时表。包含流程实例，用户任务和变量任务等在运行时的数据信息。这些表只存储Activiti在流程实例运行执行的数据，在流程结束的时候从表中去除数据。从而保持运行时候数据的表的快速和小数据量.
 ·         ACT_ID_*:ID标识为唯一(Identity)的。包含一些唯一的信息如用户，用户做等信息。
 ·         ACT_HI_*:HI表示历史数据(History)表，包括过期的流程实例，过期的变量和过期的任务等。
 ·         ACT_GE_*:GE表示公用(General data)的数据库表类型。
 */
@Controller
@RequestMapping("/simple")
public class SimpleFlowWorkController {


    private static Logger logger = LoggerFactory.getLogger(SimpleFlowWorkController.class);

    @Resource
    private IdentityService identityService;

    @Resource
    protected RepositoryService repositoryService;

    @Resource
    protected TaskService taskService;

    @Resource
    protected FormService formService;

    @Resource
    private RuntimeService runtimeService;

    @Resource
    protected HistoryService historyService;

    @Autowired
    protected ProcessEngineFactoryBean processEngine;

    @Resource
    protected ProcessExtensionService processExtensionService;

    /**
     * 个人任务首页
     * @return
     */
    @RequestMapping("/index.do")
    public String index(HttpServletRequest request,
                        HttpServletResponse response,
                        Model model){

        User user =request.getSession(true).getAttribute("user")==null?null:(User)request.getSession(true).getAttribute("user");

        List<Group> groups = request.getSession(true).getAttribute("groups")==null?null:(List<Group>)request.getSession(true).getAttribute("groups");


        if(null==user){
            return "redirect:/simple/login.do";
        }
        else {
            model.addAttribute("user",user);
            model.addAttribute("groups",groups);
            /**
             * 所有部署的任务
             */
            List<ProcessDefinition> pdList =repositoryService.createProcessDefinitionQuery().list();

            model.addAttribute("pdList",pdList);
            /**
             * 该用户所有可以认领的任务
             */
            List<Task> groupTasks = taskService.createTaskQuery().taskCandidateUser(user.getId()).list();

            List<Task> userTasks = taskService.createTaskQuery().taskAssignee(user.getId()).list();
            model.addAttribute("userTasks",userTasks);
            model.addAttribute("groupTasks",groupTasks);
            /**
             * 查看任务实例
             */
            List<Task> taskList = taskService.createTaskQuery().list();
            model.addAttribute("taskList",taskList);
            /**
             * 历史流程
             */
            List<HistoricProcessInstance> hpiList = historyService.createHistoricProcessInstanceQuery().finished().list();
            model.addAttribute("hpiList",hpiList);
        }

        return "/simple/index";
    }

    /**
     * 部署
     * @param file
     * @return
     */
    @RequestMapping(value = "/deploy.do")
    public String  deploy(@RequestParam(value = "file", required = false) MultipartFile file){

        String fileName = file.getOriginalFilename();

        try {
            InputStream fileInputStream = file.getInputStream();

            repositoryService.createDeployment().addInputStream(fileName,fileInputStream).deploy();
        }
        catch (Exception e){

        }

        return "redirect:/simple/index.do";
    }

    /**
     * 删除执行流程
     * @param request
     * @param processDefId
     * @return
     */
    @RequestMapping(value = "/remove.do")
    public String remove(HttpServletRequest request,
                         @RequestParam("processDefId") String processDefId){

        repositoryService.deleteDeployment(processDefId);
        return "redirect:/simple/index.do";
    }

    
    /**
     * 查看流程定义
     * @param request
     * @param processDefId 流程定义id
     * @return
     */
    @RequestMapping(value = "/viewprocessDef.do")
    public String viewprocessDef(HttpServletRequest request,
    							 HttpServletResponse response,	
                         @RequestParam("processDefId") String processDefId) throws Exception{
    	//根据流程定义id查询流程定义
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefId).singleResult();
        
        InputStream inputStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), processDefinition.getResourceName());
        
//        // 输出资源内容到相应对象
//        byte[] b = new byte[1024];
//        int len;
//        while ((len = inputStream.read(b, 0, 1024)) != -1) {
//        	response.getOutputStream().write(b, 0, len);
//        }
        
        response.getOutputStream().write(IoUtil.readInputStream(inputStream, "processDefInputStream"));
        
        return null;
    }
    
    
    /**
     * 查看流程定义图
     * @param request
     * @param processDefId 流程定义id
     * @return
     */
    @RequestMapping(value = "/viewprocessDefImage.do")
    public String viewprocessDefImage(HttpServletRequest request,
    							 HttpServletResponse response,	
                         @RequestParam("processDefId") String processDefId) throws Exception{
    	//根据流程定义id查询流程定义
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefId).singleResult();
        
        InputStream inputStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), processDefinition.getDiagramResourceName());
        
//        // 输出资源内容到相应对象
//        byte[] b = new byte[1024];
//        int len;
//        while ((len = inputStream.read(b, 0, 1024)) != -1) {
//        	response.getOutputStream().write(b, 0, len);
//        }
        
        response.getOutputStream().write(IoUtil.readInputStream(inputStream, "processDefInputStream"));
        
        return null;
    }
    
    
    

    /**
     * 跳转到图片显示页面
     * @param request
     * @param executionId
     * @return
     */
    @RequestMapping(value = "/view.do")
    public String view(HttpServletRequest request,
                       @RequestParam(value = "executionId") String executionId,
                       Model model){
        model.addAttribute("executionId",executionId);
        return "/simple/view";
    }


    /**
     * 显示图片
     * @return
     */
    @RequestMapping(value = "/viewPic.do")
    public void viewPic(HttpServletRequest request,
                        HttpServletResponse response,
                        @RequestParam("executionId") String executionId) throws Exception{
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(executionId).singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
        List<String> activeActivityIds = runtimeService.getActiveActivityIds(executionId);

        // 使用spring注入引擎请使用下面的这行代码
        Context.setProcessEngineConfiguration(processEngine.getProcessEngineConfiguration());

        InputStream imageStream = ProcessDiagramGenerator.generateDiagram(bpmnModel, "png", activeActivityIds);

        // 输出资源内容到相应对象
        byte[] b = new byte[1024];
        int len;
        while ((len = imageStream.read(b, 0, 1024)) != -1) {
            response.getOutputStream().write(b, 0, len);
        }
    }

    /**
     * 签收任务
     * @return
     */
    @RequestMapping(value = "/claim/{id}")
    public String claimTask(@PathVariable("id") String taskId,@RequestParam("userId")String userId){

        //签收任务
        taskService.claim(taskId,userId);

        return "redirect:/simple/index.do";
    }

    /**
     * 跳转到任务执行页面
     * @param request
     * @return
     */
    @RequestMapping(value = "/form.do")
    public String from(HttpServletRequest request,
                       @RequestParam("taskId")String taskId,
                       Model model){

        List<Task> taskList =  taskService.createTaskQuery().taskId(taskId).list();
        Task task = taskList.get(0);
        //获取表单数据
        TaskFormData tfd =  formService.getTaskFormData(taskId);
        List<FormProperty>   fpList = tfd.getFormProperties();

        Map map = runtimeService.getVariables(task.getExecutionId());

        List<ActivityImpl> activityList = new ArrayList<ActivityImpl>();

        try {
            //查找所有可驳回的节点
            activityList = processExtensionService.findBackActivity(taskId);
            //model.addAttribute("activityList",activityList);
        }catch (Exception e){
            e.printStackTrace();
        }


//        model.addAttribute("task",task);
//        model.addAttribute("fpList",fpList);
//        model.addAttribute("map",map);
//        model.addAttribute("taskId",taskId);

        request.setAttribute("task", task);
        request.setAttribute("fpList", fpList);
        request.setAttribute("map", map);
        request.setAttribute("taskId", taskId);
        request.setAttribute("activityList", activityList);
        
        return "/simple/form";
    }

    /**
     * 提交流程
     * @return
     */
    @RequestMapping(value = "/submit.do")
    public String submit(HttpServletRequest request,
                         HttpServletResponse response,
                         @RequestParam(value = "taskId",required = false)String taskId,
                         @RequestParam(value = "day",required = false) String day,
                         @RequestParam(value = "type",required = false) String type,
                         @RequestParam(value = "reason",required = false) String reason,
                         @RequestParam(value = "result",required = false) String result,
                         @RequestParam(value = "toSign",required = false) String toSign,
                         @RequestParam(value = "backActivityId",required = false) String backActivityId)throws Exception{
        List<Task> taskList = taskService.createTaskQuery().taskId(taskId).list();
        Task task = taskList.get(0);
        String taskName = task.getName();

        //result = new String(result.getBytes("ISO-8859-1"), "UTF-8");

        if(result.equals("同意")){
            Map map = new HashMap();
            if(StringUtils.isNotBlank(day)){
                map.put("day",day);
                map.put("reason",reason);
                map.put("type",0);
                taskService.complete(taskId,map);
            }
            else {
                taskService.complete(taskId);
            }
        }
        else if(result.equals("驳回")){
           ProcessInstance   processInstance = processExtensionService.findProcessInstanceByTaskId(taskId);

           Map<String,Object> map = runtimeService.getVariables(processInstance.getId());

           processExtensionService.backProcess(taskId,backActivityId,map);
        }
        else if(result.equals("转签")){

           if(processExtensionService.isPermissionInActivity(taskId,toSign))
           {
               taskService.setAssignee(taskId,toSign);

           }

        }
        return "redirect:/simple/index.do";
    }


    /**
     * 开始流程
      * @return
     */
    @RequestMapping(value = "/start.do")
    public String startProcessDefinition(HttpServletRequest request,
                                         @RequestParam("processDefId") String processDefId,
                                         @RequestParam("userId") String userId){

        // 用来设置启动流程的人员ID，引擎会自动把用户ID保存到activiti:initiator中
        identityService.setAuthenticatedUserId(userId);

        Map map = new HashMap();
        map.put("owner",userId);
        runtimeService.startProcessInstanceById(processDefId,map);


        return "redirect:/simple/index.do";
    }

    /**
     * 登录页面
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequestMapping("/login.do")
    public String login(HttpServletRequest request,
                        HttpServletResponse response,
                        Model model){

        return "/simple/login";
    }

    /**
     * 验证用户
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequestMapping("/checkLogin.do")
    public String checkLogin(HttpServletRequest request,
                        HttpServletResponse response,
                        @RequestParam("userName")String userName,
                        @RequestParam("passWord")String passWord,
                        Model model){
        logger.debug("logon request: {username={}, password={}}", userName, passWord);
        boolean checkPassword = identityService.checkPassword(userName, passWord);

        HttpSession session = request.getSession(true);

        if(checkPassword)
        {
            User user = identityService.createUserQuery().userId(userName).singleResult();
            session.setAttribute("user",user);
            GroupQuery groupQuery =  identityService.createGroupQuery();
            List<Group> groupList =  groupQuery.groupMember(userName).list();

            session.setAttribute("groups",groupList);

            String[] groupNames = new String[groupList.size()];

            for(int i=0;i<groupNames.length;i++){
                groupNames[i] = groupList.get(i).getName();
            }

            session.setAttribute("groupNames", ArrayUtils.toString(groupNames));
            return "redirect:/simple/index.do";
        }
        else {
            return "redirect:/simple/login.do";
        }
    }


}
