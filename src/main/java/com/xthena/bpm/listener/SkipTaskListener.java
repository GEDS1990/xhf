package com.xthena.bpm.listener;

import java.util.Collections;
import java.util.List;

import com.xthena.api.org.OrgConnector;
import com.xthena.api.user.UserConnector;

import com.xthena.bpm.cmd.CompleteTaskWithCommentCmd;
import com.xthena.bpm.persistence.domain.BpmConfRule;
import com.xthena.bpm.persistence.manager.BpmConfRuleManager;
import com.xthena.bpm.support.DefaultTaskListener;
import com.xthena.bpm.support.MapVariableScope;

import com.xthena.core.spring.ApplicationContextHelper;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.el.ExpressionManager;
import org.activiti.engine.impl.persistence.entity.HistoricProcessInstanceEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SkipTaskListener extends DefaultTaskListener {
    private static Logger logger = LoggerFactory
            .getLogger(SkipTaskListener.class);

    @Override
    public void onCreate(DelegateTask delegateTask) throws Exception {
        String taskDefinitionKey = delegateTask.getTaskDefinitionKey();
        String processDefinitionId = delegateTask.getProcessDefinitionId();
        String processInstanceId = delegateTask.getProcessInstanceId();
        HistoricProcessInstanceEntity historicProcessInstanceEntity = Context
                .getCommandContext().getHistoricProcessInstanceEntityManager()
                .findHistoricProcessInstance(processInstanceId);

        List<BpmConfRule> bpmConfRules = ApplicationContextHelper
                .getBean(BpmConfRuleManager.class)
                .find("from BpmConfRule where bpmConfNode.bpmConfBase.processDefinitionId=? and bpmConfNode.code=?",
                        processDefinitionId, taskDefinitionKey);
        logger.debug("delegateTask.getId : {}", delegateTask.getId());
        logger.debug("taskDefinitionKey : {}", taskDefinitionKey);
        logger.debug("processDefinitionId : {}", processDefinitionId);
        logger.debug("processInstanceId : {}", processInstanceId);
        logger.debug("bpmConfRules : {}", bpmConfRules);

        UserConnector userConnector = ApplicationContextHelper
                .getBean(UserConnector.class);
        OrgConnector orgConnector = ApplicationContextHelper
                .getBean(OrgConnector.class);
        ExpressionManager expressionManager = Context
                .getProcessEngineConfiguration().getExpressionManager();
        MapVariableScope mapVariableScope = new MapVariableScope();
        String initiator = historicProcessInstanceEntity.getStartUserId();
        mapVariableScope.setVariable("initiator",
                userConnector.findById(initiator));

        for (BpmConfRule bpmConfRule : bpmConfRules) {
            String value = bpmConfRule.getValue();

            if ("职位".equals(value)) {
                // 获得发起人的职位
                int initiatorLevel = orgConnector
                        .getJobLevelByUserId(initiator);

                // 获得审批人的职位
                int assigneeLevel = orgConnector
                        .getJobLevelByUserId(delegateTask.getAssignee());

                // 比较
                if (initiatorLevel >= assigneeLevel) {
                    logger.info("skip task : {}", delegateTask.getId());
                    logger.info("initiatorLevel : {}, assigneeLevel : {}",
                            initiatorLevel, assigneeLevel);
                    new CompleteTaskWithCommentCmd(delegateTask.getId(),
                            Collections.<String, Object> emptyMap(), "跳过")
                            .execute(Context.getCommandContext());
                }
            } else {
                Boolean result = (Boolean) expressionManager.createExpression(
                        value).getValue(mapVariableScope);

                logger.info("value : {}, result : {}", value, result);

                if (result) {
                    logger.info("skip task : {}", delegateTask.getId());
                    new CompleteTaskWithCommentCmd(delegateTask.getId(),
                            Collections.<String, Object> emptyMap(), "跳过")
                            .execute(Context.getCommandContext());
                }
            }
        }
    }
}
