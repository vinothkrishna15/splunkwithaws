package com.tcs.destination.tasklet;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.utils.DestinationMailUtils;

@Component("emailDefaultPasswordProcessor")
public class EmailDefaultPasswordProcessor implements Tasklet{
	
	private static final Logger logger = LoggerFactory
			.getLogger(EmailDefaultPasswordProcessor.class);
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private DestinationMailUtils destinationMailUtils;

	@Override
	public RepeatStatus execute(StepContribution contribution,
			ChunkContext chunkContext) throws Exception {
		
		//fetch users
		 List<UserT> userList = userRepo.findUsersByStatusAndActive(0,true);
		//for each user 
		 for(UserT user : userList){
			 //form email
			 boolean mailSent = destinationMailUtils.sendDefaultPasswordAutomatedEmail("DESTiNATION : User Account created ",user);
			 
			 //update table
			 if(mailSent) {
			  user.setStatus(1);
			  userRepo.save(user);
			 }
		 }
  		 
		return RepeatStatus.FINISHED;
	}

}
