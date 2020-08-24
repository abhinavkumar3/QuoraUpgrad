package com.QuoraUpGrad.quora.service.business;

import com.QuoraUpGrad.quora.service.dao.AnswerDao;
import com.QuoraUpGrad.quora.service.dao.QuestionDao;
import com.QuoraUpGrad.quora.service.dao.UserAuthDao;
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

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AnswerService {

    @Autowired
    QuestionService questionService;

    @Autowired
    UserAuthDao userAuthDao;

    @Autowired
    AnswerDao answerDao;


    @Autowired
    QuestionDao questionDao;

    /**
     * This method is used for creating answer.
     *
     * @param authorizationToken authorization token
     * @param answerEntity answer entity will have all the fields set
     * @return UserAuthEntity which contains the access-token and other details.
     * @throws @AuthorizationFailedException ATH-001 if the username doesn't exist in DB or ATH-002 if the token is expired.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity createAnswer(AnswerEntity answerEntity, String questionId, final String accessToken) throws AuthorizationFailedException,InvalidQuestionException {
        UserAuthEntity userAuthEntity = userAuthDao.getUserAuthByToken(accessToken);
        if(userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post an answer");
        }

        QuestionEntity question = questionDao.getQuestionById(questionId);
        if (question == null) {
            throw new InvalidQuestionException("QUES-001", "The question entered is invalid");
        }

        answerEntity.setUuid(String.valueOf(UUID.randomUUID()));
        answerEntity.setQuestion(question);
        answerEntity.setDate(ZonedDateTime.now());
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
    public AnswerEntity deleteAnswer(String answerId, final String accessToken) throws AuthorizationFailedException, AnswerNotFoundException {
        UserAuthEntity userAuthEntity = userAuthDao.getUserAuthByToken(accessToken);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to "
                    + "delete an answer");
        }
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
    public AnswerEntity editAnswer(String answerId, final String accessToken, String editedAnswer) throws AnswerNotFoundException, AuthorizationFailedException {
        UserAuthEntity userAuthEntity = userAuthDao.getUserAuthByToken(accessToken);
        if(userAuthEntity == null){
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit an answer");
        }

        AnswerEntity answerEntity = getAnswerbyId(answerId);
        if (answerEntity == null) {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        }
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
    public List<AnswerEntity> getAnswersbyQuestionID(String questionId, final String accessToken) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthEntity userAuthEntity = userAuthDao.getUserAuthByToken(accessToken);
        if(userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get the answers");
         }
                QuestionEntity questionEntity = questionService.getQuestionById(questionId);
        List<AnswerEntity> allAnswers = answerDao.getAnswersbyQUestionId(questionEntity);

        return allAnswers;
    }

}
