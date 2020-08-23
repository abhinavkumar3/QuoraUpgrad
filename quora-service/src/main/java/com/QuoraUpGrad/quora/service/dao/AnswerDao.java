package com.QuoraUpGrad.quora.service.dao;

import com.QuoraUpGrad.quora.service.entity.AnswerEntity;
import com.QuoraUpGrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class AnswerDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     *
     * @param answerEntity
     * @return AnswerEntity which updated in the database
     */

    public AnswerEntity createAnswer(AnswerEntity answerEntity){
        entityManager.persist(answerEntity);
        return answerEntity;
    }

    /**
     *
     * @param answerId
     * @return AnswerEntity retrieves answer from answer ID
     */
    public AnswerEntity getAnswerbyId(String answerId){
        try {
            return entityManager.createNamedQuery("answerbyId", AnswerEntity.class).setParameter("uuid",answerId).getSingleResult();
        }catch (NoResultException nre){
            return null;
        }
    }

    /**
     *
     * @param answerEntity
     * @return Answer entity which was deleted
     */
    public AnswerEntity deleteAnswer(AnswerEntity answerEntity){
        entityManager.remove(answerEntity);
        return answerEntity;
    }

    /**
     *
     * @param answerEntity
     * @return Answer Entity which was edited
     */
    public AnswerEntity editAnswer(AnswerEntity answerEntity){
        entityManager.merge(answerEntity);
        return answerEntity;
    }

    /**
     *
     * @param question
     * @return Retrieves list of answer entities for that question ID
     */
    public List<AnswerEntity> getAnswersbyQUestionId(QuestionEntity question){
        try{
            return entityManager.createNamedQuery("answersByQuestionId", AnswerEntity.class).setParameter("question",question).getResultList();
        }catch (NoResultException nre){
            return null;
        }

    }

}
