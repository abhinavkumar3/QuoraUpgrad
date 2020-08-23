package com.QuoraUpGrad.quora.service.business;

import com.QuoraUpGrad.quora.service.dao.UserAuthDao;
import com.QuoraUpGrad.quora.service.dao.UserDao;
import com.QuoraUpGrad.quora.service.entity.UserAuthEntity;
import com.QuoraUpGrad.quora.service.entity.UserEntity;
import com.QuoraUpGrad.quora.service.exception.AuthorizationFailedException;
import com.QuoraUpGrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PersistenceUnit;


@Service
public class AdminService {

    @Autowired
    private UserAuthDao userAuthDao;

    @Autowired
    private UserDao userDao;

    private final String NON_ADMIN_ROLE = "nonadmin";

    /**
     * This method checks if the access token exist in the DB and it is not logged out.
     *
     * @param accessToken token to be validated.
     * @throws AuthorizationFailedException ATHR-001 if the token doesn't exit in the DB , ATHR-002 if
     *                                      the user has already logged out using the token or ATHR-003 if Unauthorized Access, Entered user is not an admin.
     */
    public void checkIfTokenIsValid(String accessToken) throws AuthorizationFailedException {
        UserAuthEntity userAuthEntity = userAuthDao.getUserAuthByToken(accessToken);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.");
        }
        if(userAuthEntity.getUserEntity() != null && NON_ADMIN_ROLE.equals(userAuthEntity.getUserEntity().getRole())){
            throw new AuthorizationFailedException("ATHR-003", "Unauthorized Access, Entered user is not an admin");
        }
    }

    /**
     * This methods delete the user details based on the userId passed.
     *
     * @param userId Id of the user whose information is to be fetched.
     * @return User details if exist in the DB else null.
     * @throws UserNotFoundException USR-001 User with entered uuid to be deleted does not exist.
     */
    @Transactional
    public UserEntity deleteUserById(final String userId) throws UserNotFoundException {
        UserEntity userEntity = userDao.getUserById(userId);
        if (userEntity == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid to be deleted does not exist");
        }
        userDao.deleteUserEntity(userEntity);
        return userEntity;
    }
}
