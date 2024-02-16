package com.javamentor.qa.platform.service.impl.model;

import com.javamentor.qa.platform.dao.abstracts.model.ReputationDao;
import com.javamentor.qa.platform.dao.abstracts.repository.ReadWriteDao;
import com.javamentor.qa.platform.models.entity.question.answer.Answer;
import com.javamentor.qa.platform.models.entity.user.User;
import com.javamentor.qa.platform.models.entity.user.reputation.Reputation;
import com.javamentor.qa.platform.models.entity.user.reputation.ReputationType;
import com.javamentor.qa.platform.service.abstracts.model.ReputationService;
import com.javamentor.qa.platform.service.impl.repository.ReadWriteServiceImpl;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ReputationServiceImpl extends ReadWriteServiceImpl<Reputation, Long> implements ReputationService {

    private final ReputationDao reputationDao;

    public ReputationServiceImpl(ReadWriteDao<Reputation, Long> readWriteDao, ReputationDao reputationDao) {
        super(readWriteDao);
        this.reputationDao = reputationDao;
    }

    @Override
    @Transactional
    public void addReputation(User sender, Answer answer) {
        Reputation reputation;
        Optional<Reputation> optionalReputation = reputationDao.getReputationByAnswerIdAndUserId(sender.getId(), answer.getId());

        if (!optionalReputation.isPresent()) {

            reputation = new Reputation();
            reputation.setPersistDate(LocalDateTime.now());
            reputation.setAuthor(answer.getUser());
            reputation.setSender(sender);
            reputation.setCount(10);
            reputation.setType(ReputationType.VoteAnswer);
            reputation.setAnswer(answer);

            reputationDao.persist(reputation);
        } else {
            reputation = optionalReputation.get();
            reputationDao.update(reputation);
        }
    }
}
