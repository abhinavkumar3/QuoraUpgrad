package com.QuoraUpGrad.quora.service.business;

import com.QuoraUpGrad.quora.service.dao.AnswerDao;
import com.QuoraUpGrad.quora.service.entity.AnswerEntity;
import com.QuoraUpGrad.quora.service.entity.QuestionEntity;
import com.QuoraUpGrad.quora.service.entity.UserAuthEntity;
import com.QuoraUpGrad.quora.service.exception.AnswerNotFoundException;
import com.QuoraUpGrad.quora.service.exception.AuthorizationFailedException;
import com.QuoraUpGrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AnswerService {

    @Autowired
    QuestionService questionService;

    @Autowired
    AnswerUserService answerUserService;

    @Autowired
    AnswerDao answerDao;

    /**
     * This method is used for creating answer.
     *
     * @param authorizationToken authorization token
     * @param answerEntity answer entity will have all the fields set
     * @return UserAuthEntity which contains the access-token and other details.
     * @throws @AuthorizationFailedException ATH-001 if the username doesn't exist in DB or ATH-002 if the token is expired.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity createAnswer(AnswerEntity answerEntity, final String authorizationToken) throws AuthorizationFailedException {
        UserAuthEntity userAuthEntity;
        userAuthEntity = answerUserService.checkIfTokenIsValid(authorizationToken);
        if(userAuthEntity == null)
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to post an answer");

        answerEntity.setUser(userAuthEntity.getUserEntity());

        return answerDao.createAnswer(answerEntity);
    }

    /**
     * Gets answer from answer id.
     *
     * @param answerId answerID
     * @return AnswerEntity which contains the access-token and other details.
     * @throws @AnswerNotFoundException ANS-001 If the Answer ID is not present in the database.
     */
    public AnswerEntity getAnswerbyId(String answerId) throws AnswerNotFoundException {
        AnswerEntity answerEntity = answerDao.getAnswerbyId(answerId);
        if (answerEntity == null)
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");

        return answerEntity;
    }

    /**
     * Deletes an answer using answer id.
     *
     * @param answerId answerID
     * @param authorizationToken authorization token
     * @return AnswerEntity which contains the access-token and other details.
     * @throws @AuthorizationFailedException ATH-001 if the username doesn't exist in DB or ATH-002 if the token is expired.
     * @throws @AnswerNotFoundException ANS-001 If the Answer ID is not present in the database.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity deleteAnswer(String answerId, final String authorizationToken) throws AuthorizationFailedException, AnswerNotFoundException {
        UserAuthEntity userAuthEntity;
        userAuthEntity = answerUserService.checkIfTokenIsValid(authorizationToken);
        if(userAuthEntity == null)
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to delete an answer");

        AnswerEntity answerEntity = getAnswerbyId(answerId);

        if ((answerEntity.getUser().getId() == userAuthEntity.getUserEntity().getId())
             || userAuthEntity.getUserEntity().getRole().equals("admin")) {
                return answerDao.deleteAnswer(answerEntity);
        } else {
                throw new AuthorizationFailedException("ATHR-003", "Only the answer owner or admin can delete the answer");
        }
    }

    /**
     * Edits an answer using answer id.
     *
     * @param answerId answerID
     * @param authorizationToken authorization token
     * @param editedAnswer New answer which is to be updated
     * @return AnswerEntity which contains the access-token and other details.
     * @throws @AnswerNotFoundException ANS-001 If the Answer ID is not present in the database.
     * @throws @AuthorizationFailedException ATH-001 if the username doesn't exist in DB or ATH-002 if the token is expired.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity editAnswer(String answerId, final String authorizationToken, String editedAnswer) throws AnswerNotFoundException, AuthorizationFailedException {
        UserAuthEntity userAuthEntity;
        userAuthEntity = answerUserService.checkIfTokenIsValid(authorizationToken);
        if(userAuthEntity == null)
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to edit an answer");

        AnswerEntity answerEntity = getAnswerbyId(answerId);
        if ((answerEntity.getUser().getId() == userAuthEntity.getUserEntity().getId())) {
            answerEntity.setAnswer(editedAnswer);
            return answerDao.editAnswer(answerEntity);
        } else {
                throw new AuthorizationFailedException("ATHR-003", "Only the answer owner can edit the answer");
        }
    }

    /**
     * Edits an answer using answer id.
     *
     * @param questionId question id which answers are to be retrieved
     * @param authorizationToken authorization token
     * @return AnswerEntity List of answer entities which contains the access-token and other details.
     * @throws @InvalidQuestionException QUES-001 If the Question ID is not present in the database.
     * @throws @AuthorizationFailedException ATH-001 if the username doesn't exist in DB or ATH-002 if the token is expired.
     */
    public List<AnswerEntity> getAnswersbyQuestionID(String questionId, final String authorizationToken) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthEntity userAuthEntity;
        userAuthEntity = answerUserService.checkIfTokenIsValid(authorizationToken);
        if(userAuthEntity == null)
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to get the answers");

        QuestionEntity questionEntity = questionService.getQuestionById(questionId);
        List<AnswerEntity> allAnswers = answerDao.getAnswersbyQUestionId(questionEntity);

        return allAnswers;
    }

}
