package com.QuoraUpGrad.quora.api.controller;

import com.QuoraUpGrad.quora.api.model.UserDeleteResponse;
import com.QuoraUpGrad.quora.service.business.AdminService;
import com.QuoraUpGrad.quora.service.entity.UserEntity;
import com.QuoraUpGrad.quora.service.exception.AuthorizationFailedException;
import com.QuoraUpGrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/")
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * used to delete a user from the Application.
     * Only an admin is authorized to access this endpoint.
     * @PathVariable userId userId which needs to be deleted.
     * @param accessToken Token used for authenticating the user.
     * @return UUID of the user who is signed out.
     * @throws @AuthorizationFailedException If the access token provided by the user does not exist in the database or If the user has signed out, @UserNotFoundException If the user with uuid whose profile is to be retrieved does not exist in the database.
     */

    @RequestMapping(
            method = RequestMethod.DELETE,
            path = "/admin/user/{userId}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    
    public ResponseEntity<UserDeleteResponse> fetchUserDetails(@PathVariable(value = "userId") final String userId,@RequestHeader("authorization") final String accessToken)
            throws UserNotFoundException, AuthorizationFailedException {
        adminService.checkIfTokenIsValid(accessToken);
        UserEntity deletedUserEntity = adminService.deleteUserById(userId);
        UserDeleteResponse deleteResponse = new UserDeleteResponse().id(deletedUserEntity.getUuid()).status("USER SUCCESSFULLY DELETED");
        return new ResponseEntity<>(deleteResponse, HttpStatus.OK);
    }

}
