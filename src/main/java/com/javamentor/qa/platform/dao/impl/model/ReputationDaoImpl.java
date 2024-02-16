package com.javamentor.qa.platform.dao.impl.model;

import com.javamentor.qa.platform.dao.abstracts.model.ReputationDao;
import com.javamentor.qa.platform.dao.impl.repository.ReadWriteDaoImpl;
import com.javamentor.qa.platform.dao.util.SingleResultUtil;
import com.javamentor.qa.platform.models.entity.user.reputation.Reputation;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Repository
public class ReputationDaoImpl extends ReadWriteDaoImpl<Reputation, Long> implements ReputationDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Reputation> getReputationByAnswerIdAndUserId(Long senderId, Long answerId) {
        return SingleResultUtil.getSingleResultOrNull(entityManager.createQuery(
                        """
                                from Reputation r where r.answer.id =: answerId
                                and r.sender.id =: senderId""", Reputation.class)
                .setParameter("answerId", answerId)
                .setParameter("senderId", senderId));

    }
}
