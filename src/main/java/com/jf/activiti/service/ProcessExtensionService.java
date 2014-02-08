package com.jf.activiti.service;

import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.runtime.ProcessInstance;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: wangbin
 * Date: 13-11-13
 * Time: 下午2:10
 * To change this template use File | Settings | File Templates.
 */
public interface ProcessExtensionService {

    /**
     * 根据当前任务ID，查询可以驳回的任务节点
     * @param taskId
     * 当前任务ID
     */
    public List<ActivityImpl> findBackActivity(String taskId)throws Exception;

    /**
     * 驳回节点
     * @param taskId
     * @param activityId
     * @param variables
     * @throws Exception
     */
    public void backProcess(String taskId,String activityId,
                            Map<String,Object> variables)throws Exception;

    /**
     * 查找流程实例
     * @param taskId
     * @return
     */
    public ProcessInstance findProcessInstanceByTaskId(String taskId);

    /**
     * 查询某个用户ID是否在某个Activity里面拥有权限
     * @param taskId
     * @param userId
     * @return
     */
    public boolean isPermissionInActivity(String taskId,String userId);
}
