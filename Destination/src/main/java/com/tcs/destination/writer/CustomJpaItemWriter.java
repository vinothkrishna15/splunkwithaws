package com.tcs.destination.writer;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;

public class CustomJpaItemWriter <T> implements ItemWriter<T>  {

	protected static final Log logger = LogFactory.getLog(CustomJpaItemWriter.class);

	@Autowired
	private JpaItemWriter jpaWriter;

	public JpaItemWriter getJpaWriter() {
		return jpaWriter;
	}


	public void setJpaWriter(JpaItemWriter jpaWriter) {
		this.jpaWriter = jpaWriter;
	}


	/**
	 * Merge all provided items that aren't already in the persistence context
	 * and then flush the entity manager.
	 *
	 * @see org.springframework.batch.item.ItemWriter#write(java.util.List)
	 */
	@Override
	public void write(List<? extends T> items) {
		if (!items.isEmpty() &&  ArrayList.class.isInstance(items.get(0))) {
			List<?> newItems = new ArrayList();
			for (T it: items) {
				newItems.addAll((List)it);
			}
			jpaWriter.write(newItems);
		}
	}
}

