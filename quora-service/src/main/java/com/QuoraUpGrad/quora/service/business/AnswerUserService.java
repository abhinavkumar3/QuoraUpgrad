package com.QuoraUpGrad.quora.service.business;

import com.QuoraUpGrad.quora.service.dao.UserAuthDao;
import com.QuoraUpGrad.quora.service.entity.UserAuthEntity;
import com.QuoraUpGrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnswerUserService {

    @Autowired
    UserAuthDao userAuthDao;

    /**
     * Gets the user auth information based on the access token.
     *
     * @param accessToken access token of the user auth whose details is to be fetched.
     * @return A single user auth object or null
     */
    public UserAuthEntity checkIfTokenIsValid(String accessToken) throws AuthorizationFailedException {
        UserAuthEntity userAuthEntity = userAuthDao.getUserAuthByToken(accessToken);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException(
                    "ATHR-001", "User has not signed in");
        }
        if (userAuthEntity.getLogoutAt() != null) {
            return null;
        }

        return userAuthEntity;
    }

}
