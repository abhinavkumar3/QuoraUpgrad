package com.QuoraUpGrad.quora.service.dao;

import com.QuoraUpGrad.quora.service.entity.QuestionEntity;
import com.QuoraUpGrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionDao {
    @PersistenceContext
    private EntityManager entityManager;

    /**
     *  Persist the question in the DB.
     * @param questionEntity question to be persisted.
     * @return Persisted question.
     */
    public QuestionEntity createQuestion(QuestionEntity questionEntity) {
        entityManager.persist(questionEntity);
        return questionEntity;
    }

    /**
     *  Fetch all the questions from the DB.
     * @return List of QuestionEntity
     */
    public List<QuestionEntity> getAllQuestions() {
        try {
            return entityManager.createNamedQuery("getAllQuestions", QuestionEntity.class).getResultList();
        } catch (NoResultException nre) {
            return null;
        }

    }

    /**
     * Get the question for the given id.
     * @param uuid id of the required question.
     * @return QuestionEntity if question with given id is found else null.
     */
    public QuestionEntity getQuestionById(String uuid) {
        try {
            return entityManager.createNamedQuery("getQuestionById", QuestionEntity.class).setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }

    }

    /**
     *  Delete the question
     * @param questionEntity question entity to be deleted.
     */
    public QuestionEntity deleteQuestion(QuestionEntity questionEntity) {
        entityManager.remove(questionEntity);
        return questionEntity;
    }

    /**
     *  Fetch all the questions from the DB.
     * @param userEntity user whose list of asked questions has to be retrieved
     * @return List of QuestionEntity
     */
    public List<QuestionEntity> getQuestionsByUser(UserEntity userEntity) {
        try {
            return entityManager.createNamedQuery("getQuestionByUser", QuestionEntity.class).setParameter("user", userEntity).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     *  Update the question
     * @param questionEntity question entity to be updated
     */
    public QuestionEntity editQuestionContent(final QuestionEntity questionEntity) {
        return entityManager.merge(questionEntity);
    }
}