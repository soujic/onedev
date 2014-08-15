package com.pmease.gitplex.core.manager.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.pmease.commons.hibernate.Sessional;
import com.pmease.commons.hibernate.dao.Dao;
import com.pmease.commons.hibernate.dao.EntityCriteria;
import com.pmease.gitplex.core.manager.CommitCommentManager;
import com.pmease.gitplex.core.model.CommitComment;
import com.pmease.gitplex.core.model.Repository;

@Singleton
public class DefaultCommitCommentManager implements CommitCommentManager {

	private final Dao dao;
	
	@Inject
	public DefaultCommitCommentManager(Dao dao) {
		this.dao = dao;
	}

	@Sessional
	@Override
	public List<CommitComment> findByFile(Repository repository, String file) {
		return dao.query(EntityCriteria.of(CommitComment.class)
				.add(Restrictions.eq("repository", repository))
				.add(Restrictions.eq("file", file))
				.addOrder(Order.asc("id")), 0, 0);
	}

	@Sessional
	@Override
	public List<CommitComment> findByCommit(Repository repository, String commit) {
		return dao.query(EntityCriteria.of(CommitComment.class)
				.add(Restrictions.eq("repository", repository))
				.add(Restrictions.eq("commit", commit))
				.addOrder(Order.asc("id")), 0, 0);
	}

	@Override
	public List<CommitComment> findByCommitAndFile(Repository repository,
			String commit, String file) {
		return dao.query(EntityCriteria.of(CommitComment.class)
				.add(Restrictions.eq("repository", repository))
				.add(Restrictions.eq("commit", commit))
				.add(Restrictions.eq("file", file))
				.addOrder(Order.asc("id")), 0, 0);
	}

}