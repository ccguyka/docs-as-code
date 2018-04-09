package org.ccguyka.docsascode.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(path = "/users", consumes = "application/json", produces = "application/json")
class UserController {

    static final Map<UUID, User> USERS = new HashMap<>();

    @RequestMapping(method = RequestMethod.GET, path = "/{userId}")
    ResponseEntity<User> user(@PathVariable UUID userId) {
        return ResponseEntity.ok(USERS.get(userId));
    }

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<User> addUser(@RequestBody User user) {
        UUID newUserId = UUID.randomUUID();
        USERS.put(newUserId, user);

        return ResponseEntity.created(URI.create("/users/" + newUserId)).build();
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/{userId}")
    ResponseEntity<User> updateUser(@PathVariable UUID userId, @RequestBody User user) {
        if (USERS.containsKey(userId)) {
            USERS.put(userId, user);
            return ResponseEntity.ok(user);
        }

        return ResponseEntity.notFound().build();
    }
}
