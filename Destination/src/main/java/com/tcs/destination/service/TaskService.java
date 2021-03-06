package com.tcs.destination.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tcs.destination.bean.NotesT;
import com.tcs.destination.bean.TaskBdmsTaggedLinkT;
import com.tcs.destination.bean.TaskT;
import com.tcs.destination.data.repository.AutoCommentsEntityFieldsTRepository;
import com.tcs.destination.data.repository.AutoCommentsEntityTRepository;
import com.tcs.destination.data.repository.CollaborationCommentsRepository;
import com.tcs.destination.data.repository.ConnectRepository;
import com.tcs.destination.data.repository.NotesTRepository;
import com.tcs.destination.data.repository.NotificationEventGroupMappingTRepository;
import com.tcs.destination.data.repository.NotificationsEventFieldsTRepository;
import com.tcs.destination.data.repository.OpportunityRepository;
import com.tcs.destination.data.repository.TaskBdmsTaggedLinkRepository;
import com.tcs.destination.data.repository.TaskRepository;
import com.tcs.destination.data.repository.UserNotificationSettingsRepository;
import com.tcs.destination.data.repository.UserNotificationsRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.enums.TaskCollaborationPreference;
import com.tcs.destination.enums.TaskEntityReference;
import com.tcs.destination.enums.TaskStatus;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.helper.AutoCommentsHelper;
import com.tcs.destination.helper.NotificationHelper;
import com.tcs.destination.helper.NotificationsLazyLoader;
import com.tcs.destination.utils.DestinationUtils;
import com.tcs.destination.utils.StringUtils;

/**
 * Service class to handle Task module related requests.
 * 
 */
@Service
public class TaskService {

	private static final Logger logger = LoggerFactory
			.getLogger(TaskService.class);

	private static final String STATUS_CLOSED = "CLOSED";

	// Required for auto comments
	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	TaskRepository taskRepository;

	@Autowired
	TaskBdmsTaggedLinkRepository taskBdmsTaggedLinkRepository;

	@Autowired
	ConnectRepository connectRepository;

	@Autowired
	OpportunityRepository opportunityRepository;

	@Autowired
	UserRepository userRepository;

	// Required beans for Auto comments - start
	@Autowired
	ThreadPoolTaskExecutor autoCommentsTaskExecutor;

	@Autowired
	AutoCommentsEntityTRepository autoCommentsEntityTRepository;

	@Autowired
	AutoCommentsEntityFieldsTRepository autoCommentsEntityFieldsTRepository;

	@Autowired
	CollaborationCommentsRepository collaborationCommentsRepository;
	// Required beans for Auto comments - end

	// Required beans for Notifications - start
	@Autowired
	NotificationsEventFieldsTRepository notificationEventFieldsTRepository;

	@Autowired
	UserNotificationsRepository userNotificationsTRepository;

	@Autowired
	UserNotificationSettingsRepository userNotificationSettingsRepo;

	@Autowired
	ThreadPoolTaskExecutor notificationsTaskExecutor;

	@Autowired
	NotificationEventGroupMappingTRepository notificationEventGroupMappingTRepository;

	@Autowired
	CollaborationCommentsService collaborationCommentsService;
	
	@Autowired
	NotesTRepository notesTRepository;

	// Required beans for Notifications - end

	/**
	 * This method is used to find task details for the given task id.
	 * 
	 * @param taskId
	 * @return task details for the given task id.
	 */
	public TaskT findTaskById(String taskId) throws Exception {
		logger.debug("Begin:Inside findTaskById() of TaskService");
		TaskT task = taskRepository.findOne(taskId);

		if (task == null) {
			logger.error("NOT_FOUND: No task found for the TaskId: {}", taskId);
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No task found for the TaskId:" + taskId);
		}
		logger.debug("End::Inside findTaskById() of TaskService");
		return task;
	}

	/**
	 * This method is used to find all the tasks with the given task
	 * description.
	 * 
	 * @param taskDescription
	 * @return tasks with the given task description.
	 */
	public List<TaskT> findTasksByNameContaining(String taskDescription)
			throws Exception {
		logger.debug("Begin:Inside findTasksByNameContaining() of TaskService");
		List<TaskT> taskList = taskRepository
				.findByTaskDescriptionIgnoreCaseContaining(taskDescription);

		if ((taskList == null) || taskList.isEmpty()) {
			logger.error(
					"NOT_FOUND: No tasks found with the given task description: {}",
					taskDescription);
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No tasks found with the given task description: "
							+ taskDescription);
		}
		logger.debug("End::Inside findTasksByNameContaining() of TaskService");
		return taskList;
	}

	/**
	 * This method is used to find all the tasks for the given connect id.
	 * 
	 * @param connectId
	 * @return tasks for the given connect id.
	 */
	public List<TaskT> findTasksByConnectId(String connectId) throws Exception {
		logger.debug("Begin:Inside findTasksByConnectId() of TaskService");
		List<TaskT> taskList = taskRepository.findByConnectId(connectId);

		if ((taskList == null) || taskList.isEmpty()) {
			logger.error("NOT_FOUND: No tasks found for the ConnectId: {}",
					connectId);
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No tasks found for the ConnectId: " + connectId);
		}
		logger.debug("End:Inside findTasksByConnectId() of TaskService");
		return taskList;
	}

	/**
	 * This method is used to find all the tasks for the given opportunity id.
	 * 
	 * @param opportunityId
	 * @return tasks for the given opportunity id.
	 */
	public List<TaskT> findTasksByOpportunityId(String opportunityId)
			throws Exception {
		logger.debug("Begin: Inside findTasksByOpportunityId() of TaskService");
		List<TaskT> taskList = taskRepository
				.findByOpportunityId(opportunityId);

		if ((taskList == null) || taskList.isEmpty()) {
			logger.error("NOT_FOUND: No tasks found for the OpportunityId: {}",
					opportunityId);
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No tasks found for the OpportunityId: " + opportunityId);
		}
		logger.debug("End: Inside findTasksByOpportunityId() of TaskService");
		return taskList;
	}

	/**
	 * This method is used to find all the tasks (open & hold) for the given
	 * task owner.
	 * 
	 * @param taskOwner
	 *            , taskStatus
	 * @return tasks for the given task owner.
	 */
	public List<TaskT> findTasksByTaskOwnerAndStatus(String taskOwner,
			String taskStatus) throws Exception {
		logger.debug("Begin: Inside findTasksByTaskOwnerAndStatus() of TaskService");
		List<TaskT> taskList = null;
		if (!taskStatus.isEmpty()) {
			if (!taskStatus.equalsIgnoreCase("all")) {
				// Validate Task Status
				if (!TaskStatus.contains(taskStatus)) {
					logger.error("BAD_REQUEST: Invalid Task Status: {}",
							taskStatus);
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"Invalid Task Status: " + taskStatus);
				}
				taskList = taskRepository
						.findByTaskOwnerAndTaskStatusOrderByTargetDateForCompletionAsc(
								taskOwner, taskStatus);
			} else {
				// Get all tasks with OPEN & HOLD status
				taskList = taskRepository
						.findByTaskOwnerAndTaskStatusNotOrderByTargetDateForCompletionAsc(
								taskOwner, STATUS_CLOSED);
			}
		}

		if ((taskList == null) || taskList.isEmpty()) {
			logger.error("NOT_FOUND: No tasks found for the Task Owner: {}",
					taskOwner);
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No tasks found for the Task Owner: " + taskOwner);
		}
		logger.debug("End: Inside findTasksByTaskOwnerAndStatus() of TaskService");
		return taskList;
	}

	/**
	 * This method is used to find all the tasks assigned to others by a given
	 * user id.
	 * 
	 * @param userId
	 * @return tasks assigned to others by a given user id.
	 */
	public List<TaskT> findTasksAssignedtoOthersByUser(String userId)
			throws Exception {
		logger.debug("Begin: Inside findTasksAssignedtoOthersByUser() of TaskService");

		List<TaskT> taskList = taskRepository
				.findByCreatedByAndTaskOwnerNotOrderByTargetDateForCompletionAsc(
						userId, userId);

		if ((taskList == null) || taskList.isEmpty()) {
			logger.error(
					"NOT_FOUND: No assigned to others tasks found for the UserId: {}",
					userId);
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No assigned to others tasks found for the UserId: "
							+ userId);
		}
		logger.debug("End: Inside findTasksAssignedtoOthersByUser() of TaskService");
		return taskList;
	}

	/**
	 * This method is used to find all the tasks assigned to a user with a
	 * specific target completion start and end date.
	 * 
	 * @param userId
	 *            , fromDate, toDate
	 * @return tasks assigned to a user with a specific target completion start
	 *         and end date.
	 */
	public List<TaskT> findTasksByUserAndTargetDate(String userId,
			Date fromDate, Date toDate) throws Exception {
		logger.debug("Begin: Inside findTasksByUserAndTargetDate() of TaskService");

		List<TaskT> taskList = null;

		taskList = taskRepository
				.findByTaskOwnerAndTargetDateForCompletionBetween(userId,
						fromDate, toDate);

		if ((taskList == null) || taskList.isEmpty()) {
			logger.error(
					"NOT_FOUND: No tasks found for the UserId:{} and Target completion date:{} {}",
					userId, fromDate, toDate);
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No tasks found for the UserId:" + userId
							+ "and Target completion date: " + fromDate + ", "
							+ toDate);
		}
		logger.debug("End: Inside findTasksByUserAndTargetDate() of TaskService");
		return taskList;
	}

	/**
	 * This method is used to create a new task.
	 * 
	 * @param task
	 * @return task
	 */
	@Transactional
	public TaskT createTask(TaskT task) throws Exception {
		logger.debug("Begin: Inside createTask() of TaskService");
		List<TaskBdmsTaggedLinkT> taskBdmsTaggedLinkTs = null;
		List<NotesT> listOfNotesT = null;
		TaskT managedTask = null;
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		// getting user Id
		task.setCreatedBy(userId);
		task.setModifiedBy(userId);

		// Validate input parameters
		validateTask(task);

		// TaskBdmsTaggedLinkT contains a not null task_id, so save the parent
		// task first
		if (task.getTaskBdmsTaggedLinkTs() != null) {
			taskBdmsTaggedLinkTs = task.getTaskBdmsTaggedLinkTs();
			task.setTaskBdmsTaggedLinkTs(null);
		}

		managedTask = taskRepository.save(task);
		
		if ((null != managedTask) && managedTask.getTaskId() != null) {
			
			if (task.getNotesTs() != null) {
				for (NotesT notes : task.getNotesTs()) {
					notes.setTaskId(managedTask.getTaskId());
				}
				listOfNotesT = (List<NotesT>) notesTRepository.save(task.getNotesTs());
			}
			
			if (taskBdmsTaggedLinkTs != null) {
				for (TaskBdmsTaggedLinkT taskBdmTaggedLink : taskBdmsTaggedLinkTs) {
					taskBdmTaggedLink.setTaskT(managedTask);
					taskBdmTaggedLink.setTaskId(managedTask.getTaskId());
					// Setting user id
					taskBdmTaggedLink.setCreatedBy(userId);
					taskBdmTaggedLink.setModifiedBy(userId);
				}
				// Persist TaskBdmsTaggedLinkT
				taskBdmsTaggedLinkRepository.save(taskBdmsTaggedLinkTs);
			}
		}

		// Invoke Asynchronous Auto Comments Thread
		processAutoComments(managedTask.getTaskId(), null);
		// Invoke Asynchronous Notifications Thread
//		processNotifications(managedTask.getTaskId(), null);
		logger.debug("End: Inside createTask() of TaskService");
		return managedTask;
	}

	/**
	 * This method is used to load database object with auto comments eligible
	 * lazy collections populated
	 * 
	 * @param taskId
	 * @return
	 * @throws Exception
	 */
	public TaskT loadDbTaskWithLazyCollections(String taskId) throws Exception {
		logger.debug("Begin: Inside loadDbTaskWithLazyCollections() of TaskService");
		TaskT task = (TaskT) NotificationsLazyLoader.loadLazyCollections(
				taskId, EntityType.TASK.name(), taskRepository,
				notificationEventFieldsTRepository, null);
		logger.debug("End: Inside loadDbTaskWithLazyCollections() of TaskService");
		return task;
	}

	/**
	 * This method is used to update a task.
	 * 
	 * @param task
	 * @return task
	 */
	@Transactional
	public TaskT editTask(TaskT task) throws Exception {
		logger.debug("Begin: Inside editTask() of TaskService");
		List<TaskBdmsTaggedLinkT> taskBdmsTaggedLinkTs = null;
		List<TaskBdmsTaggedLinkT> removeBdmsTaggedLinkTs = null;
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		if (task.getTaskId() == null) {
			logger.error("TaskId is required for update");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"TaskId is required for update");
		}
		// Check if task exists
		if (!taskRepository.exists(task.getTaskId())) {
			logger.error("NOT_FOUND: Task not found for update: {}",
					task.getTaskId());
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Task not found for update: " + task.getTaskId());
		}

		// Load db object before update with lazy collections populated for
		// NNotifications
		TaskT dbTask = loadDbTaskWithLazyCollections(task.getTaskId());
		if (dbTask != null) {

			// Get a copy of the db object for processing Auto comments
			TaskT oldObject = (TaskT) DestinationUtils.copy(dbTask);
			// setting user id
			task.setModifiedBy(userId);
			task.setCreatedBy(userId);
			// Validate input parameters
			validateTask(task);

			if (task.getTaskBdmsTaggedLinkTs() != null) {
				taskBdmsTaggedLinkTs = task.getTaskBdmsTaggedLinkTs();
				task.setTaskBdmsTaggedLinkTs(null);
			}

			if (task.getNotesTs() != null) {
				for (NotesT notes : task.getNotesTs()) {
					notes.setTaskId(task.getTaskId());
				}
			}

			// Remove all the TaskBdmsTaggedLinkT's marked for remove
			if (task.getTaskBdmsTaggedLinkDeletionList() != null) {
				removeBdmsTaggedLinkTs = task
						.getTaskBdmsTaggedLinkDeletionList();
				task.setTaskBdmsTaggedLinkDeletionList(null);
			}

			// Persist task
			dbTask = taskRepository.save(task);

			// Persist TaskBdmsTaggedLinkT
			if (taskBdmsTaggedLinkTs != null) {
				for (TaskBdmsTaggedLinkT taskBdmTaggedLink : taskBdmsTaggedLinkTs) {
					taskBdmTaggedLink.setTaskT(dbTask);
					taskBdmTaggedLink.setTaskId(dbTask.getTaskId());
					// Setting user id
					taskBdmTaggedLink.setCreatedBy(userId);
					taskBdmTaggedLink.setModifiedBy(userId);
				}
				taskBdmsTaggedLinkRepository.save(taskBdmsTaggedLinkTs);
			}

			// Remove all the TaskBdmsTaggedLinkT's marked for remove
			if (removeBdmsTaggedLinkTs != null) {
				taskBdmsTaggedLinkRepository.delete(removeBdmsTaggedLinkTs);
			}

			// Invoke Asynchronous Auto Comments Thread
			processAutoComments(dbTask.getTaskId(), oldObject);
			// Invoke Asynchronous Notifications Thread
//			processNotifications(dbTask.getTaskId(), oldObject);
		} else {
			logger.error("NOT_FOUND: Task not found for update: {}",
					task.getTaskId());
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Task not found for update: " + task.getTaskId());
		}
		logger.debug("End: Inside editTask() of TaskService");
		return dbTask;
	}

	/**
	 * This method is used to validate task input parameters.
	 * 
	 * @param task
	 * @return
	 */
	private void validateTask(TaskT task) throws DestinationException {
		logger.debug("Begin: Inside validateTask() of TaskService");
		// Validate Task Entity Reference
		if (task.getEntityReference() != null) {
			String entityRef = task.getEntityReference();
			if (TaskEntityReference.contains(entityRef)) {
				// If EntityReference is Connect, ConnectId should be passed
				if (TaskEntityReference.CONNECT.equalsName(entityRef)) {
					if (task.getConnectId() == null) {
						logger.error("BAD_REQUEST: ConnectId is required");
						throw new DestinationException(HttpStatus.BAD_REQUEST,
								"ConnectId is required");
					}
					if (task.getOpportunityId() != null) {
						logger.error("BAD_REQUEST: EntityReference is Connect, OpportunityId should not be passed");
						throw new DestinationException(HttpStatus.BAD_REQUEST,
								"EntityReference is Connect, OpportunityId should not be passed");
					}
					if (!connectRepository.exists(task.getConnectId())) {
						logger.error("NOT_FOUND: ConnectId not found");
						throw new DestinationException(HttpStatus.NOT_FOUND,
								"ConnectId not found");
					}
				}
				// If EntityReference is Opportunity, OpportunityId should be
				// passed
				if (TaskEntityReference.OPPORTUNITY.equalsName(entityRef)) {
					if (task.getOpportunityId() == null) {
						logger.error("BAD_REQUEST: OpportunityId is required");
						throw new DestinationException(HttpStatus.BAD_REQUEST,
								"OpportunityId is required");
					}
					if (task.getConnectId() != null) {
						logger.error("BAD_REQUEST: EntityReference is Opportunity, ConnectId should not be passed");
						throw new DestinationException(HttpStatus.BAD_REQUEST,
								"EntityReference is Opportunity, ConnectId should not be passed");
					}
					if (!opportunityRepository.exists(task.getOpportunityId())) {
						logger.error("NOT_FOUND: OpportunityId not found");
						throw new DestinationException(HttpStatus.NOT_FOUND,
								"OpportunityId not found");
					}
				}
			} else {
				logger.error("BAD_REQUEST: Invalid Task Entity Reference: {}",
						entityRef);
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Invalid Task Entity Reference: " + entityRef);
			}
		}

		// Validate Task Status
		if (task.getTaskStatus() != null) {
			if (!TaskStatus.contains(task.getTaskStatus())) {
				logger.error("BAD_REQUEST: Invalid Task Status: {}",
						task.getTaskStatus());
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Invalid Task Status: " + task.getTaskStatus());
			}
		}

		// Validate Task Collaboration Preference
		if (task.getCollaborationPreference() != null) {
			String collaborationPreference = task.getCollaborationPreference();
			if (TaskCollaborationPreference.contains(collaborationPreference)) {
				// If BDM collaboration preference is Restricted, one or more
				// BDMs should be tagged
				if (TaskCollaborationPreference.RESTRICTED
						.equalsName(collaborationPreference)) {
					if (task.getTaskBdmsTaggedLinkTs() == null) {
						logger.error("BAD_REQUEST: BDM Collaboration preference is Restricted, one or more BDMs should be tagged");
						throw new DestinationException(HttpStatus.BAD_REQUEST,
								"BDM Collaboration preference is Restricted, one or more BDMs should be tagged");
					}
				}

			} else {
				logger.error(
						"BAD_REQUEST: Invalid Task BDM Collaboration Preference: {}",
						collaborationPreference);
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Invalid Task BDM Collaboration Preference: "
								+ collaborationPreference);
			}
		}
		logger.debug("End: Inside validateTask() of TaskService");
	}

	/**
	 * This method is used to find all the team tasks (open & hold) for the
	 * given supervisor id.
	 * 
	 * @param taskOwner
	 *            , taskStatus
	 * @return team tasks for the given supervisor.
	 */
	public List<TaskT> findTeamTasks(String supervisorId, String status)
			throws Exception {
		logger.debug("Begin: Inside findTeamTasks() of TaskService");

		List<TaskT> taskList = null;

		if (!StringUtils.isEmpty(supervisorId)) {

			// Get all sub-ordinates user id's

			List<String> userIds = userRepository
					.getAllSubordinatesIdBySupervisorId(supervisorId);

			// Adding supervisor Id
			userIds.add(supervisorId);

			taskList = new ArrayList<TaskT>();

			if (!status.isEmpty()) {
				if (!status.equalsIgnoreCase("all")) {
					// Validate Task Status
					if (!TaskStatus.contains(status)) {
						logger.error("BAD_REQUEST: Invalid Task Status: {}",
								status);
						throw new DestinationException(HttpStatus.BAD_REQUEST,
								"Invalid Task Status: " + status);
					}
					taskList = taskRepository
							.findTeamTasksBySupervisorIdAndStatus(userIds,
									status);
				} else {
					// Get all tasks with OPEN & HOLD status
					taskList = taskRepository
							.findTeamTasksBySupervisorIdAndStatusNot(userIds,
									STATUS_CLOSED);
				}
			}

			if ((taskList == null) || taskList.isEmpty()) {
				logger.error(
						"NOT_FOUND: No team tasks found for the Supervisor: {}",
						supervisorId);
				throw new DestinationException(HttpStatus.NOT_FOUND,
						"No team tasks found for the Supervisor user: "
								+ supervisorId);
			}

		} else {
			logger.error("NOT_FOUND: Supervisor Id is empty");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Supervisor Id is empty");
		}

		logger.debug("End: Inside findTeamTasks() of TaskService");
		return taskList;
	}

	// This method is used to invoke asynchronous thread for auto comments
	private void processAutoComments(String taskId, Object oldObject)
			throws Exception {
		logger.debug("Begin: Inside processAutoComments() of TaskService");
		AutoCommentsHelper autoCommentsHelper = new AutoCommentsHelper();
		autoCommentsHelper.setEntityId(taskId);
		autoCommentsHelper.setEntityType(EntityType.TASK.name());
		if (oldObject != null) {
			autoCommentsHelper.setOldObject(oldObject);
		}
		autoCommentsHelper
				.setAutoCommentsEntityTRepository(autoCommentsEntityTRepository);
		autoCommentsHelper
				.setAutoCommentsEntityFieldsTRepository(autoCommentsEntityFieldsTRepository);
		autoCommentsHelper
				.setCollaborationCommentsRepository(collaborationCommentsRepository);
		autoCommentsHelper.setCrudRepository(taskRepository);
		autoCommentsHelper.setEntityManagerFactory(entityManager
				.getEntityManagerFactory());
		autoCommentsHelper.setCollCommentsService(collaborationCommentsService);
		// Invoking Auto Comments Task Executor Thread
		autoCommentsTaskExecutor.execute(autoCommentsHelper);
		logger.debug("End: Inside processAutoComments() of TaskService");
	}

	// This method is used to invoke asynchronous thread for notifications
	private void processNotifications(String taskId, Object oldObject) {
		logger.debug("Begin: Inside processNotifications() of TaskService");
		NotificationHelper notificationsHelper = new NotificationHelper();
		notificationsHelper.setEntityId(taskId);
		notificationsHelper.setEntityType(EntityType.TASK.name());
		if (oldObject != null) {
			notificationsHelper.setOldObject(oldObject);
		}
		notificationsHelper
				.setNotificationsEventFieldsTRepository(notificationEventFieldsTRepository);
		notificationsHelper
				.setUserNotificationsTRepository(userNotificationsTRepository);
		notificationsHelper
				.setUserNotificationSettingsRepo(userNotificationSettingsRepo);
		notificationsHelper
				.setNotificationEventGroupMappingTRepository(notificationEventGroupMappingTRepository);
		notificationsHelper.setCrudRepository(taskRepository);
		notificationsHelper.setEntityManagerFactory(entityManager
				.getEntityManagerFactory());
		// Invoking notifications Task Executor Thread
		notificationsTaskExecutor.execute(notificationsHelper);
		logger.debug("End: Inside processNotifications() of TaskService");
	}

	/**
	 * This service find all the tasks assigned to others by users under a
	 * supervisor
	 * 
	 * @param supervisorId
	 * @return
	 * @throws Exception
	 */
	public List<TaskT> findTeamTasksAssignedtoOthers(String supervisorId)
			throws Exception {
		logger.debug("Begin: Inside findTeamTasksAssignedtoOthers() of TaskService");

		List<TaskT> taskList = null;
		List<String> userIds = null;

		if (!StringUtils.isEmpty(supervisorId)) {
			userIds = userRepository
					.getAllSubordinatesIdBySupervisorId(supervisorId);
			if ((userIds != null) && (!userIds.isEmpty())) {
				taskList = new ArrayList<TaskT>();
				for (String userId : userIds) {
					List<TaskT> tasks = taskRepository
							.findByCreatedByAndTaskOwnerNotOrderByTargetDateForCompletionAsc(
									userId, userId);
					taskList.addAll(tasks);
				}
			} else {
				logger.error(
						"NOT_FOUND: No Subordinates found for Supervisor Id : {}",
						supervisorId);
				throw new DestinationException(HttpStatus.NOT_FOUND,
						"No Subordinates found for Supervisor Id : "
								+ supervisorId);
			}
		} else {
			logger.error("NOT_FOUND: Missing Supervisor Id");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Missing Supervisor Id");
		}

		if ((taskList == null) || taskList.isEmpty()) {
			logger.error(
					"NOT_FOUND: No assigned to others tasks found for the supervisorId: {}",
					supervisorId);
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No assigned to others tasks found for the supervisorId: "
							+ supervisorId);
		}
		logger.debug("End: Inside findTeamTasksAssignedtoOthers() of TaskService");
		return taskList;
	}

}