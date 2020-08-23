package com.QuoraUpGrad.quora.service.dao;

import com.QuoraUpGrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class UserDao {
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * This methods stores the user details in the DB. This method receives the object of UserEntity
     * type with its attributes being set.
     */
    public UserEntity createUser(UserEntity userEntity) {
        entityManager.persist(userEntity);
        return userEntity;
    }

    /**
     * Fetch a single user by given id from the DB.
     *
     * @param userId Id of the user whose information is to be fetched.
     * @return User details if exist in the DB else null.
     */
    public UserEntity getUserById(final String userId) {
        try {
            return entityManager
                    .createNamedQuery("userByUserId", UserEntity.class)
                    .setParameter("userId", userId)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * This methods gets the user details based on the username passed.
     *
     * @param userName username of the user whose information is to be fetched.
     * @return null if the user with given username doesn't exist in DB.
     */
    public UserEntity getUserByUserName(final String userName) {
        try {
            return entityManager
                    .createNamedQuery("userByUserName", UserEntity.class)
                    .setParameter("userName", userName)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * This methods gets the user details based on the email passed.
     *
     * @param email email of the user whose information is to be fetched.
     * @return null if the user with given email doesn't exist in DB.
     */
    public UserEntity getUserByEmail(final String email) {
        try {
            return entityManager
                    .createNamedQuery("userByEmail", UserEntity.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public void updateUserEntity(final UserEntity updatedUserEntity) {
        entityManager.merge(updatedUserEntity);
    }

    /**
     * Delete a single user by given user object.
     *
     * @param userEntity object of the user whose information is to be deleted.
     */
    public void deleteUserEntity(final UserEntity userEntity) {
        entityManager.remove(userEntity);
    }
}
