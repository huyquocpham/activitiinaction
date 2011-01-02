package org.bpmnwithactiviti.chapter3.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;

public class IdentityServiceTest {
	
	@Rule 
	public ActivitiRule activitiRule = new ActivitiRule("activiti.cfg-mem.xml");
	
	@Test
	public void createNewUser() {
		User newUser = activitiRule.getIdentityService().newUser("John Doe");
		activitiRule.getIdentityService().saveUser(newUser);
		User user = activitiRule.getIdentityService().createUserQuery().singleResult();
		assertEquals("John Doe", user.getId());
	}
	
	@Test
	public void createNewGroup() {
		Group newGroup = activitiRule.getIdentityService().newGroup("sales");
		newGroup.setName("Sales");
		activitiRule.getIdentityService().saveGroup(newGroup);
		Group group = activitiRule.getIdentityService().createGroupQuery().singleResult();
		assertEquals("Sales", group.getName());
	}
	
	@Test
	public void createNewMembership() {
		activitiRule.getIdentityService().createMembership("John Doe", "sales");
	}
	
	@Test
	@Deployment(resources = {"chapter3/bookorder.bpmn20.xml"})
	public void testMembership() {
		RuntimeService runtimeService = activitiRule.getRuntimeService();
		Map<String, Object> variableMap = new HashMap<String, Object>();
		variableMap.put("isbn", "123456");
		runtimeService.startProcessInstanceByKey("bookorder", variableMap);
		
		TaskService taskService = activitiRule.getTaskService();
		Task task = taskService.createTaskQuery().taskCandidateUser("John Doe").singleResult();
		assertNotNull(task);
		assertEquals("Complete order", task.getName());
	}
}
