package com.technews.controller;

import com.technews.model.Post;
import com.technews.model.User;
import com.technews.repository.UserRepository;
import com.technews.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {
  // The @Autowired annotation tells Spring to scan the project for objects that will need to be instantiated for a class or method to run.
  // only instantiate each object as needed by the program
  @Autowired
  UserRepository repository;

  @Autowired
  VoteRepository voteRepository;

  // GET requests
  // this combines route and type of HTTP method used (GET)
  @GetMapping("/api/users")
  public List<User> getAllUsers() {
    // get a list of users and assign it to the userList variable
    List<User> userList = repository.findAll();
    for (User u : userList) {
      // get posts for every user
      List<Post> postList = u.getPosts();
      for (Post p : postList) {
        p.setVoteCount(voteRepository.countVotesByPostId(p.getId()));
      }
    }
    // return a list of users
    return userList;
  }

  @GetMapping("/api/users/{id}")
  public User getUserById(@PathVariable Integer id) {
    User returnUser = repository.getById(id);
    List<Post> postList = returnUser.getPosts();
    for (Post p : postList) {
      p.setVoteCount(voteRepository.countVotesByPostId(p.getId()));
    }

    // return a single user by id
    return returnUser;
  }

  // POST requests
  // add a user to the database
  // the @RequestBody annotation  will map the body of this request to a transfer object, then deserialize the body onto a Java object for easier use.
  @PostMapping("/api/users")
  public User addUser(@RequestBody User user) {
    // Encrypt password
    user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
    repository.save(user);
    return user;
  }

  // PUT request
  // update a user based on a specific id
  @PutMapping("/api/users/{id}")
  // The @PathVariable will allow us to enter the int id into the request URI as a parameter, replaces {id}
  public User updateUser(@PathVariable int id, @RequestBody User user) {
    User tempUser = repository.getById(id);

    if (!tempUser.equals(null)) {
      user.setId(tempUser.getId());
      repository.save(user);
    }
    return user;
  }

  // DELETE request
  @DeleteMapping("/api/users/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  // delete user based on specific id passed in the request URI
  public void deleteUser(@PathVariable int id) {
    repository.deleteById(id);
  }
}
