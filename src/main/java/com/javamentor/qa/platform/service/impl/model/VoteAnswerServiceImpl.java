package com.javamentor.qa.platform.service.impl.model;

import com.javamentor.qa.platform.dao.abstracts.model.VoteAnswerDao;
import com.javamentor.qa.platform.dao.abstracts.repository.ReadWriteDao;
import com.javamentor.qa.platform.models.entity.question.VoteType;
import com.javamentor.qa.platform.models.entity.question.answer.Answer;
import com.javamentor.qa.platform.models.entity.question.answer.VoteAnswer;
import com.javamentor.qa.platform.models.entity.user.User;
import com.javamentor.qa.platform.service.abstracts.model.VoteAnswerService;
import com.javamentor.qa.platform.service.impl.repository.ReadWriteServiceImpl;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class VoteAnswerServiceImpl extends ReadWriteServiceImpl<VoteAnswer, Long> implements VoteAnswerService {

    private final VoteAnswerDao voteAnswerDao;

    public VoteAnswerServiceImpl(ReadWriteDao<VoteAnswer, Long> readWriteDao, VoteAnswerDao voteAnswerDao) {
        super(readWriteDao);
        this.voteAnswerDao = voteAnswerDao;
    }

    @Override
    @Transactional
    public void voteUpToAnswer(User userWhoVotes, Answer answer) {
        VoteAnswer voteAnswer;

        Optional<VoteAnswer> optionalVoteAnswer = voteAnswerDao.getVoteAnswerByAnswerIdAndUserId(answer.getId(), userWhoVotes.getId());

        if (optionalVoteAnswer.isEmpty()) {
            voteAnswer=new VoteAnswer(userWhoVotes, answer, VoteType.UP);
            voteAnswerDao.persist(voteAnswer);
        } else {
            voteAnswer=optionalVoteAnswer.get();
            voteAnswerDao.update(voteAnswer);
        }
    }

    @Override
    public Long getAllTheVotesForThisAnswer(Long answerId) {
        return voteAnswerDao.getAllTheVotesForThisAnswer(answerId);
    }


}
