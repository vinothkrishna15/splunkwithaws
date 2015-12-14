package com.tcs.destination.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.CollaborationCommentT;
import com.tcs.destination.bean.Status;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.CollaborationCommentsService;
import com.tcs.destination.utils.ResponseConstructors;

/**
 * 
 * This Controller is used to handle comments module.
 *
 */
@RestController
@RequestMapping("/comments")
public class CollaborationCommentsController {

	private static final Logger logger = LoggerFactory
			.getLogger(CollaborationCommentsController.class);

	@Autowired
	CollaborationCommentsService commentsService;

	/**
	 * This is used to insert new comments
	 * @param comments
	 * @return ResponseEntity<String>
	 * @throws DestinationException
	 */
	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> insertComments(
			@RequestBody CollaborationCommentT comments)
			throws DestinationException {
		logger.info("Inside CollaborationCommentsController : Start of inserting the comments");
		try {
			Status status = new Status();
			status.setStatus(Status.FAILED, "");
			if (commentsService.insertComments(comments) != null) {
				logger.debug("Comments Inserted Successfully");
				status.setStatus(Status.SUCCESS, comments.getCommentId());
			}
			logger.info("Inside CollaborationCommentsController : End of inserting the comments");
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews("all", "",
							status), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in inserting the collaboration comments");
		}
	}

}
