package com.QuoraUpGrad.quora.service.business;

import com.QuoraUpGrad.quora.service.dao.QuestionDao;
import com.QuoraUpGrad.quora.service.dao.UserDao;
import com.QuoraUpGrad.quora.service.entity.QuestionEntity;
import com.QuoraUpGrad.quora.service.entity.UserEntity;
import com.QuoraUpGrad.quora.service.exception.AuthorizationFailedException;
import com.QuoraUpGrad.quora.service.exception.InvalidQuestionException;
import com.QuoraUpGrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class QuestionService {
    @Autowired
    private QuestionDao questionDao;
    @Autowired
    private UserDao userDao;

    /**
     * Create a question in database
     *
     * @param questionEntity This object has the content i.e the question.
     * @return QuestionEntity object of the question created in DB.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(QuestionEntity questionEntity) {
        questionDao.createQuestion(questionEntity);
        return questionEntity;
    }

    /**
     * Fetch all the questions from database
     * @return List of QuestionEntity object of the questions stored in DB.
     */
    @Transactional
    public List<QuestionEntity> getAllQuestions() {
        return questionDao.getAllQuestions();
    }

    /**
     * Fetch a question from database for a particular question id.
     *
     * @param questionId id of the question.
     * @return QuestionEntity object of the question stored in DB.
     */
    @Transactional
    public QuestionEntity getQuestionById(String questionId) throws InvalidQuestionException {

        QuestionEntity questionEntity = questionDao.getQuestionById(questionId);
        if (questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }
        return questionEntity;
    }

    /**
     * Edit a question in database for a particular question.
     *
     * @param signedInUser UserEntity object of the associated user of the question.
     * @param questionEntity object of the question.
     * @param questionContent data of the question.
     */
    @Transactional
    public void editQuestionContent(UserEntity signedInUser, QuestionEntity questionEntity, String questionContent) throws AuthorizationFailedException, InvalidQuestionException {

        if (questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }
        UserEntity questionOwner = questionEntity.getUserEntity();
        if (!questionOwner.getUuid().equals(signedInUser.getUuid())) {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
        }
        questionEntity.setContent(questionContent);
        questionDao.editQuestionContent(questionEntity);
    }

    /**
     * Delete a question from database.
     *
     * @param signedInUser UserEntity object of the associated user of the question.
     * @param questionEntity object of the question.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteQuestion(UserEntity signedInUser, QuestionEntity questionEntity) throws InvalidQuestionException, AuthorizationFailedException {
        if (questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }
        String role = signedInUser.getRole();
        UserEntity questionOwner = questionEntity.getUserEntity();
        if (!(questionOwner.getUuid().equals(signedInUser.getUuid()) || role.equals("admin"))) {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
        }
        questionDao.deleteQuestion(questionEntity);
    }

    /**
     * Fetch all the questions from database for a particular User.
     * @param userId id of the user for whom questions to be fetched.
     * @return List of QuestionEntity object of the questions stored in DB.
     */
    @Transactional
    public List<QuestionEntity> getQuestionsByUser(String userId)  throws UserNotFoundException  {
        UserEntity user = userDao.getUserById(userId);
        if (user == null) {
            throw new UserNotFoundException(
                    "USR-001", "User with entered uuid whose question details are to be seen does not exist");
        }
        return questionDao.getQuestionsByUser(user);
    }
}



